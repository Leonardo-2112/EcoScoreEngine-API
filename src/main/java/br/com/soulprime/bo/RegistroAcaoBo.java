package br.com.soulprime.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.AcaoDao;
import br.com.soulprime.dao.CampanhaEsgDao;
import br.com.soulprime.dao.RegistroAcaoDao;
import br.com.soulprime.dao.UsuarioDao;
import br.com.soulprime.dto.RegistroAcaoRequestDto;
import br.com.soulprime.dto.ValidacaoRequestDto;
import br.com.soulprime.exception.AcessoNegadoException;
import br.com.soulprime.exception.DadosInvalidosException;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.exception.RegraNegocioException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.Acao;
import br.com.soulprime.model.RegistroAcao;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.enums.Perfil;
import br.com.soulprime.model.enums.StatusValidacao;
import br.com.soulprime.model.enums.TipoEvidencia;

/**
 * Registro e validacao de acoes sustentaveis (caso de uso "Registrar e Validar Acao"):
 * COMPROVADA (1,0) e creditada na hora; PARCIAL (0,7) e SEM_COMPROVACAO (0,4) ficam PENDENTES
 * ate o VALIDADOR aprovar (credita os pontos) ou negar (0 pontos).
 */
@Service
public class RegistroAcaoBo {

    private final RegistroAcaoDao registroAcaoDao;
    private final UsuarioDao usuarioDao;
    private final AcaoDao acaoDao;
    private final CampanhaEsgDao campanhaDao;
    private final GamificacaoBo gamificacaoBo;
    private final AcessoBo acessoBo;
    private final ConnectionFactory connectionFactory;

    public RegistroAcaoBo(RegistroAcaoDao registroAcaoDao, UsuarioDao usuarioDao, AcaoDao acaoDao, CampanhaEsgDao campanhaDao,
                          GamificacaoBo gamificacaoBo, AcessoBo acessoBo, ConnectionFactory connectionFactory) {
        this.registroAcaoDao = registroAcaoDao;
        this.usuarioDao = usuarioDao;
        this.acaoDao = acaoDao;
        this.campanhaDao = campanhaDao;
        this.gamificacaoBo = gamificacaoBo;
        this.acessoBo = acessoBo;
        this.connectionFactory = connectionFactory;
    }

    public RegistroAcao registrar(Integer usuarioLogado, RegistroAcaoRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        Usuario participante = acessoBo.exigirPerfil(usuarioLogado, Perfil.PARTICIPANTE);
        String evidencia = ValidadorDados.texto(dto.getEvidencia());
        if (dto.getTipoEvidencia() == TipoEvidencia.COMPROVADA && evidencia == null) {
            throw new DadosInvalidosException("Descreva a evidencia (foto, nota fiscal, comprovante) para uma acao COMPROVADA.");
        }
        LocalDateTime agora = LocalDateTime.now().withNano(0);

        Connection conexao = connectionFactory.getConnection();
        try {
            conexao.setAutoCommit(false);
            Usuario usuario = usuarioDao.buscarPorIdParaAtualizar(conexao, participante.getId());
            Acao acao = acaoDao.buscarPorId(conexao, dto.getAcaoId());
            if (!acao.isAtiva()) {
                throw new RegraNegocioException("A acao '" + acao.getNome() + "' esta inativa.");
            }
            if (!acao.isRepetivel() && registroAcaoDao.existeRegistro(conexao, usuario.getId(), acao.getId())) {
                throw new RegraNegocioException("A acao '" + acao.getNome() + "' so pode ser registrada uma vez.");
            }
            int registrosHoje = registroAcaoDao.contarNoDia(conexao, usuario.getId(), acao.getId(), agora.toLocalDate());
            if (registrosHoje >= acao.getLimiteDiario()) {
                throw new RegraNegocioException("Limite diario atingido: no maximo " + acao.getLimiteDiario()
                        + " registros por dia da acao '" + acao.getNome() + "' (RN14).");
            }

            //pontos = TRUNC((impacto x 10 x 1/repeticoes x confiabilidade) x bonus do nivel)
            int repeticoes = registrosHoje + 1;
            BigDecimal confiabilidade = dto.getTipoEvidencia().getConfiabilidade();
            BigDecimal bonus = usuario.getNivel().getBonusPontos();
            long pontos = gamificacaoBo.calcularPontos(acao.getImpacto(), repeticoes, confiabilidade, bonus);
            String memoria = gamificacaoBo.memoriaCalculo(acao.getImpacto(), repeticoes, confiabilidade, bonus, pontos);

            RegistroAcao registro = new RegistroAcao();
            registro.setUsuario(usuario);
            registro.setAcao(acao);
            registro.setCampanha(campanhaDao.buscarVigente(conexao, acao.getId(), agora.toLocalDate()));
            registro.setDataRealizacao(agora);
            registro.setRepeticoes(repeticoes);
            registro.setConfiabilidade(confiabilidade);
            registro.setPontosGerados(pontos);
            registro.setEvidencia(evidencia);
            if (dto.getTipoEvidencia() == TipoEvidencia.COMPROVADA) {
                registro.setValidacao(StatusValidacao.VALIDADA);
                registro.setDataValidacao(agora);
                registro.setObservacao(memoria);
            } else {
                registro.setValidacao(StatusValidacao.PENDENTE);
                registro.setObservacao("Aguardando validador. Pontos previstos: " + memoria);
            }
            registroAcaoDao.cadastrar(conexao, registro);
            if (registro.isCreditado()) {
                gamificacaoBo.creditar(conexao, usuario, registro, agora);
            }
            conexao.commit();
            return registro;
        } catch (Exception e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.close();
        }
    }

    //Decisao do validador sobre um registro PENDENTE (aprovar credita; negar zera e exige justificativa)
    public RegistroAcao validar(Integer usuarioLogado, int id, ValidacaoRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        Usuario validador = acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        String observacao = ValidadorDados.texto(dto.getObservacao());
        if (!dto.getAprovado() && observacao == null) {
            throw new DadosInvalidosException("Informe a justificativa (observacao) para negar o registro.");
        }
        BigDecimal novaConfiabilidade = dto.getConfiabilidade();
        if (novaConfiabilidade != null && !confiabilidadeValida(novaConfiabilidade)) {
            throw new DadosInvalidosException("Confiabilidade invalida: use 1.0, 0.7 ou 0.4.");
        }
        LocalDateTime agora = LocalDateTime.now().withNano(0);

        Connection conexao = connectionFactory.getConnection();
        try {
            conexao.setAutoCommit(false);
            RegistroAcao registro = registroAcaoDao.buscarPorIdParaAtualizar(conexao, id);
            if (registro.getValidacao() != StatusValidacao.PENDENTE) {
                throw new RegraNegocioException("Somente registros PENDENTES podem ser validados (atual: " + registro.getValidacao() + ").");
            }
            Usuario participante = usuarioDao.buscarPorIdParaAtualizar(conexao, registro.getUsuario().getId());
            registro.setValidador(validador);
            registro.setDataValidacao(agora);

            if (dto.getAprovado()) {
                BigDecimal confiabilidade = novaConfiabilidade == null ? registro.getConfiabilidade() : novaConfiabilidade;
                //o bonus de nivel do momento do registro e mantido
                BigDecimal bonus = gamificacaoBo.bonusDaMemoria(registro.getObservacao(), participante.getNivel().getBonusPontos());
                long pontos = gamificacaoBo.calcularPontos(registro.getAcao().getImpacto(), registro.getRepeticoes(), confiabilidade, bonus);
                String memoria = gamificacaoBo.memoriaCalculo(registro.getAcao().getImpacto(), registro.getRepeticoes(), confiabilidade, bonus, pontos);
                registro.setConfiabilidade(confiabilidade);
                registro.setPontosGerados(pontos);
                registro.setValidacao(confiabilidade.compareTo(BigDecimal.ONE) == 0 ? StatusValidacao.VALIDADA : StatusValidacao.PARCIAL);
                registro.setObservacao(limitar(observacao == null ? memoria : memoria + " | " + observacao));
                registroAcaoDao.atualizarValidacao(conexao, registro);
                gamificacaoBo.creditar(conexao, participante, registro, agora);
            } else {
                registro.setValidacao(StatusValidacao.NEGADA);
                registro.setPontosGerados(0);
                registro.setObservacao(limitar("Negado pelo validador: " + observacao));
                registroAcaoDao.atualizarValidacao(conexao, registro);
            }
            conexao.commit();
        } catch (Exception e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.close();
        }
        return registroAcaoDao.buscarPorId(id);
    }

    //VALIDADOR ve todos (com filtros); PARTICIPANTE ve apenas os proprios; EMPRESA nao ve registros (RN17)
    public List<RegistroAcao> listar(Integer usuarioLogado, String status, Integer usuarioId) throws SQLException {
        Usuario usuario = acessoBo.usuarioLogado(usuarioLogado);
        if (usuario.getPerfil() == Perfil.EMPRESA) {
            throw new AcessoNegadoException("Empresas acessam apenas dados agregados no Dashboard ESG (RN17).");
        }
        StatusValidacao filtro = status == null ? null : ValidadorDados.converterEnum(StatusValidacao.class, status);
        Integer filtroUsuario = usuario.getPerfil() == Perfil.VALIDADOR ? usuarioId : Integer.valueOf(usuario.getId());
        return registroAcaoDao.listar(filtro, filtroUsuario);
    }

    public List<RegistroAcao> listarDoUsuario(Integer usuarioLogado, int usuarioId, String status) throws SQLException {
        acessoBo.exigirProprioOuValidador(usuarioLogado, usuarioId);
        StatusValidacao filtro = status == null ? null : ValidadorDados.converterEnum(StatusValidacao.class, status);
        return registroAcaoDao.listar(filtro, usuarioId);
    }

    public RegistroAcao buscar(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        RegistroAcao registro = registroAcaoDao.buscarPorId(id);
        acessoBo.exigirProprioOuValidador(usuarioLogado, registro.getUsuario().getId());
        return registro;
    }

    private boolean confiabilidadeValida(BigDecimal valor) {
        for (TipoEvidencia tipo : TipoEvidencia.values()) {
            if (tipo.getConfiabilidade().compareTo(valor) == 0) {
                return true;
            }
        }
        return false;
    }

    private String limitar(String texto) {
        return texto.length() <= 300 ? texto : texto.substring(0, 300);
    }
}
