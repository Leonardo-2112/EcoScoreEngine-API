package br.com.soulprime.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.AcaoDao;
import br.com.soulprime.dao.CampanhaEsgDao;
import br.com.soulprime.dao.MetricaEsgDao;
import br.com.soulprime.dto.CampanhaRequestDto;
import br.com.soulprime.exception.AcessoNegadoException;
import br.com.soulprime.exception.ConflitoException;
import br.com.soulprime.exception.DadosInvalidosException;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.exception.RegraNegocioException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.Acao;
import br.com.soulprime.model.CampanhaAcao;
import br.com.soulprime.model.CampanhaEsg;
import br.com.soulprime.model.MetricaEsg;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.enums.Perfil;
import br.com.soulprime.model.enums.StatusCampanha;

/**
 * Campanhas ESG patrocinadas: a EMPRESA cria (PENDENTE) e o VALIDADOR aprova a publicacao (ATIVA - RN16).
 * A campanha nao altera a formula de pontos: ela vincula os registros e alimenta as metricas.
 */
@Service
public class CampanhaBo {

    private final CampanhaEsgDao campanhaDao;
    private final AcaoDao acaoDao;
    private final MetricaEsgDao metricaDao;
    private final AcessoBo acessoBo;
    private final ConnectionFactory connectionFactory;

    public CampanhaBo(CampanhaEsgDao campanhaDao, AcaoDao acaoDao, MetricaEsgDao metricaDao, AcessoBo acessoBo,
                      ConnectionFactory connectionFactory) {
        this.campanhaDao = campanhaDao;
        this.acaoDao = acaoDao;
        this.metricaDao = metricaDao;
        this.acessoBo = acessoBo;
        this.connectionFactory = connectionFactory;
    }

    //Publico: so ATIVAS. EMPRESA: as proprias (qualquer status). VALIDADOR: todas, com filtros
    public List<CampanhaEsg> listar(Integer usuarioLogado, String status, Integer empresaId) throws SQLException {
        Usuario usuario = acessoBo.usuarioOpcional(usuarioLogado);
        StatusCampanha filtro = status == null ? null : ValidadorDados.converterEnum(StatusCampanha.class, status);
        if (usuario != null && usuario.getPerfil() == Perfil.VALIDADOR) {
            return campanhaDao.listar(filtro, empresaId);
        }
        if (usuario != null && usuario.getPerfil() == Perfil.EMPRESA) {
            return campanhaDao.listar(filtro, usuario.getEmpresa().getId());
        }
        return campanhaDao.listar(StatusCampanha.ATIVA, empresaId);
    }

    public CampanhaEsg buscar(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        CampanhaEsg campanha = campanhaDao.buscarPorId(id);
        if (campanha.getStatus() != StatusCampanha.ATIVA) {
            exigirDonaOuValidador(usuarioLogado, campanha);
        }
        return campanha;
    }

    public CampanhaEsg cadastrar(Integer usuarioLogado, CampanhaRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        Usuario usuario = acessoBo.exigirPerfil(usuarioLogado, Perfil.EMPRESA);
        validarPeriodo(dto);
        if (campanhaDao.existeNome(dto.getNome(), 0)) {
            throw new ConflitoException("Ja existe uma campanha com esse nome.");
        }
        LocalDateTime agora = LocalDateTime.now().withNano(0);
        CampanhaEsg campanha = new CampanhaEsg();
        campanha.setEmpresa(usuario.getEmpresa());
        preencher(campanha, dto, agora);
        campanha.setStatus(StatusCampanha.PENDENTE);
        campanha.setDataCriacao(agora);

        Connection conexao = connectionFactory.getConnection();
        try {
            conexao.setAutoCommit(false);
            campanhaDao.cadastrar(conexao, campanha);
            conexao.commit();
            return campanha;
        } catch (Exception e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.close();
        }
    }

    //Edicao pela propria empresa, permitida enquanto a campanha estiver PENDENTE
    public CampanhaEsg atualizar(Integer usuarioLogado, int id, CampanhaRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        Usuario usuario = acessoBo.exigirPerfil(usuarioLogado, Perfil.EMPRESA);
        validarPeriodo(dto);
        CampanhaEsg campanha = campanhaDao.buscarPorId(id);
        if (campanha.getEmpresa().getId() != usuario.getEmpresa().getId()) {
            throw new AcessoNegadoException("A campanha pertence a outra empresa.");
        }
        if (campanha.getStatus() != StatusCampanha.PENDENTE) {
            throw new RegraNegocioException("Somente campanhas PENDENTES podem ser editadas.");
        }
        if (campanhaDao.existeNome(dto.getNome(), id)) {
            throw new ConflitoException("Ja existe uma campanha com esse nome.");
        }
        campanha.getAcoes().clear();
        preencher(campanha, dto, LocalDateTime.now().withNano(0));

        Connection conexao = connectionFactory.getConnection();
        try {
            conexao.setAutoCommit(false);
            campanhaDao.atualizar(conexao, campanha);
            conexao.commit();
        } catch (Exception e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.close();
        }
        return campanhaDao.buscarPorId(id);
    }

    //Cancelamento pela empresa dona ou pelo validador (exclusao logica: status CANCELADA)
    public void cancelar(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        CampanhaEsg campanha = campanhaDao.buscarPorId(id);
        exigirDonaOuValidador(usuarioLogado, campanha);
        mudarStatus(campanha, StatusCampanha.CANCELADA);
    }

    //Aprovacao (PENDENTE -> ATIVA), encerramento ou cancelamento pelo VALIDADOR
    public CampanhaEsg alterarStatus(Integer usuarioLogado, int id, String status) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        StatusCampanha novoStatus = ValidadorDados.converterEnum(StatusCampanha.class, status);
        CampanhaEsg campanha = campanhaDao.buscarPorId(id);
        mudarStatus(campanha, novoStatus);
        campanha.setStatus(novoStatus);
        return campanha;
    }

    public List<MetricaEsg> metricas(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        CampanhaEsg campanha = campanhaDao.buscarPorId(id);
        exigirDonaOuValidador(usuarioLogado, campanha);
        return metricaDao.listarPorCampanha(campanha);
    }

    //PENDENTE -> ATIVA/CANCELADA; ATIVA -> ENCERRADA/CANCELADA; ENCERRADA e CANCELADA sao finais
    private void mudarStatus(CampanhaEsg campanha, StatusCampanha novo) throws SQLException, EntidadeNaoEncontradaException {
        boolean permitida = (campanha.getStatus() == StatusCampanha.PENDENTE && (novo == StatusCampanha.ATIVA || novo == StatusCampanha.CANCELADA))
                || (campanha.getStatus() == StatusCampanha.ATIVA && (novo == StatusCampanha.ENCERRADA || novo == StatusCampanha.CANCELADA));
        if (!permitida) {
            throw new RegraNegocioException("Transicao de status nao permitida: " + campanha.getStatus() + " -> " + novo + ".");
        }
        if (novo == StatusCampanha.ATIVA && !campanha.getEmpresa().isAtiva()) {
            throw new RegraNegocioException("A empresa patrocinadora precisa estar ATIVA para publicar a campanha.");
        }
        campanhaDao.atualizarStatus(campanha.getId(), novo);
    }

    private void exigirDonaOuValidador(Integer usuarioLogado, CampanhaEsg campanha) throws SQLException {
        Usuario usuario = acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR, Perfil.EMPRESA);
        if (usuario.getPerfil() == Perfil.EMPRESA && usuario.getEmpresa().getId() != campanha.getEmpresa().getId()) {
            throw new AcessoNegadoException("A campanha pertence a outra empresa.");
        }
    }

    private void validarPeriodo(CampanhaRequestDto dto) {
        if (!dto.getDataFim().isAfter(dto.getDataInicio())) {
            throw new DadosInvalidosException("A data de fim deve ser posterior a data de inicio (RN09).");
        }
        if (dto.getDataFim().isBefore(LocalDate.now())) {
            throw new DadosInvalidosException("A campanha nao pode terminar no passado.");
        }
    }

    private void preencher(CampanhaEsg campanha, CampanhaRequestDto dto, LocalDateTime agora) throws SQLException, EntidadeNaoEncontradaException {
        campanha.setNome(ValidadorDados.texto(dto.getNome()));
        campanha.setDescricao(ValidadorDados.texto(dto.getDescricao()));
        campanha.setOrcamento(dto.getOrcamento());
        campanha.setDataInicio(dto.getDataInicio());
        campanha.setDataFim(dto.getDataFim());
        for (Integer acaoId : new LinkedHashSet<>(dto.getAcoesIds())) {
            Acao acao = acaoDao.buscarPorId(acaoId);
            if (!acao.isAtiva()) {
                throw new RegraNegocioException("A acao '" + acao.getNome() + "' esta inativa e nao pode ser patrocinada.");
            }
            CampanhaAcao campanhaAcao = new CampanhaAcao(campanha, acao);
            campanhaAcao.setDataInclusao(agora);
            campanha.getAcoes().add(campanhaAcao);
        }
    }
}
