package br.com.soulprime.bo;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.AcaoDao;
import br.com.soulprime.dao.CategoriaAcaoDao;
import br.com.soulprime.dto.AcaoRequestDto;
import br.com.soulprime.exception.ConflitoException;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.exception.RegraNegocioException;
import br.com.soulprime.model.Acao;
import br.com.soulprime.model.enums.Perfil;
import br.com.soulprime.model.enums.StatusAtivacao;

//Catalogo de acoes sustentaveis: leitura publica, escrita pelo perfil VALIDADOR
@Service
public class AcaoBo {

    private static final int LIMITE_DIARIO_PADRAO = 2;

    private final AcaoDao acaoDao;
    private final CategoriaAcaoDao categoriaDao;
    private final AcessoBo acessoBo;

    public AcaoBo(AcaoDao acaoDao, CategoriaAcaoDao categoriaDao, AcessoBo acessoBo) {
        this.acaoDao = acaoDao;
        this.categoriaDao = categoriaDao;
        this.acessoBo = acessoBo;
    }

    //Sem filtro de status retorna so as acoes ATIVAS; "TODAS" retorna todas
    public List<Acao> listar(String status, Integer categoriaId) throws SQLException {
        StatusAtivacao filtro = StatusAtivacao.ATIVA;
        if (status != null) {
            filtro = status.equalsIgnoreCase("TODAS") ? null : ValidadorDados.converterEnum(StatusAtivacao.class, status);
        }
        return acaoDao.listar(filtro, categoriaId);
    }

    public Acao buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        return acaoDao.buscarPorId(id);
    }

    public Acao cadastrar(Integer usuarioLogado, AcaoRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        if (acaoDao.existeNome(dto.getNome(), 0)) {
            throw new ConflitoException("Ja existe uma acao com esse nome.");
        }
        Acao acao = new Acao();
        preencher(acao, dto);
        acao.setDataCriacao(LocalDateTime.now().withNano(0));
        acao.setStatus(StatusAtivacao.ATIVA);
        acaoDao.cadastrar(acao);
        return acao;
    }

    public Acao atualizar(Integer usuarioLogado, int id, AcaoRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        Acao acao = acaoDao.buscarPorId(id);
        if (acaoDao.existeNome(dto.getNome(), id)) {
            throw new ConflitoException("Ja existe uma acao com esse nome.");
        }
        preencher(acao, dto);
        acaoDao.atualizar(acao);
        return acao;
    }

    //Exclusao logica (INATIVA): preserva os registros historicos que usam a acao
    public void inativar(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        Acao acao = acaoDao.buscarPorId(id);
        if (!acao.isAtiva()) {
            throw new RegraNegocioException("Acao ja esta inativa.");
        }
        acaoDao.atualizarStatus(id, StatusAtivacao.INATIVA);
    }

    private void preencher(Acao acao, AcaoRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        acao.setCategoria(categoriaDao.buscarPorId(dto.getCategoriaId()));
        acao.setNome(ValidadorDados.texto(dto.getNome()));
        acao.setDescricao(ValidadorDados.texto(dto.getDescricao()));
        acao.setImpacto(dto.getImpacto());
        acao.setLimiteDiario(dto.getLimiteDiario() == null ? LIMITE_DIARIO_PADRAO : dto.getLimiteDiario());
        acao.setRepetivel(dto.getRepetivel() == null || dto.getRepetivel());
        acao.setCo2PorUnidade(dto.getCo2PorUnidade());
    }
}
