package br.com.soulprime.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.CarteiraDao;
import br.com.soulprime.dao.HistoricoConversaoDao;
import br.com.soulprime.dao.HistoricoSaqueDao;
import br.com.soulprime.dao.UsuarioDao;
import br.com.soulprime.dto.SaqueRequestDto;
import br.com.soulprime.exception.DadosInvalidosException;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.exception.RegraNegocioException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.Carteira;
import br.com.soulprime.model.HistoricoConversao;
import br.com.soulprime.model.HistoricoSaque;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.enums.StatusSaque;

/**
 * Carteira digital: conversao de pontos em creditos (100 pts = 1 credito, irreversivel - RN10/RN11)
 * e saque em reais (10 creditos = R$ 1,00, minimo de 10 creditos - RN12/RN13).
 */
@Service
public class CarteiraBo {

    public static final int PONTOS_POR_CREDITO = 100;
    public static final int SAQUE_MINIMO_CREDITOS = 10;
    public static final BigDecimal REAIS_POR_CREDITO = new BigDecimal("0.10");

    private final CarteiraDao carteiraDao;
    private final UsuarioDao usuarioDao;
    private final HistoricoConversaoDao conversaoDao;
    private final HistoricoSaqueDao saqueDao;
    private final AcessoBo acessoBo;
    private final ConnectionFactory connectionFactory;

    public CarteiraBo(CarteiraDao carteiraDao, UsuarioDao usuarioDao, HistoricoConversaoDao conversaoDao, HistoricoSaqueDao saqueDao,
                      AcessoBo acessoBo, ConnectionFactory connectionFactory) {
        this.carteiraDao = carteiraDao;
        this.usuarioDao = usuarioDao;
        this.conversaoDao = conversaoDao;
        this.saqueDao = saqueDao;
        this.acessoBo = acessoBo;
        this.connectionFactory = connectionFactory;
    }

    public Carteira consultar(Integer usuarioLogado, int usuarioId) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirProprioOuValidador(usuarioLogado, usuarioId);
        return carteiraDao.buscarPorUsuario(usuarioDao.buscarPorId(usuarioId));
    }

    //Converte todos os multiplos de 100 do saldo de pontos; o resto continua no saldo
    public HistoricoConversao converter(Integer usuarioLogado, int usuarioId) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirProprioParticipante(usuarioLogado, usuarioId);
        LocalDateTime agora = LocalDateTime.now().withNano(0);
        Connection conexao = connectionFactory.getConnection();
        try {
            conexao.setAutoCommit(false);
            Usuario usuario = usuarioDao.buscarPorIdParaAtualizar(conexao, usuarioId);
            long pontos = (usuario.getPontosSaldo() / PONTOS_POR_CREDITO) * PONTOS_POR_CREDITO;
            if (pontos < PONTOS_POR_CREDITO) {
                throw new RegraNegocioException("Saldo de " + usuario.getPontosSaldo() + " pontos: sao necessarios ao menos "
                        + PONTOS_POR_CREDITO + " pontos para converter (RN10).");
            }
            BigDecimal creditos = BigDecimal.valueOf(pontos / PONTOS_POR_CREDITO);
            Carteira carteira = carteiraDao.buscarPorUsuarioParaAtualizar(conexao, usuario);

            usuario.debitarSaldo(pontos);
            usuarioDao.atualizarPontuacao(conexao, usuario);
            carteira.creditar(creditos, agora);
            carteiraDao.atualizar(conexao, carteira);

            HistoricoConversao conversao = new HistoricoConversao();
            conversao.setUsuario(usuario);
            conversao.setPontosUtilizados(pontos);
            conversao.setCreditosGerados(creditos);
            conversao.setDataConversao(agora);
            conversao.setObservacao("Conversao de " + pontos + " pontos em " + creditos + " creditos.");
            conversaoDao.cadastrar(conexao, conversao);
            conexao.commit();
            return conversao;
        } catch (Exception e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.close();
        }
    }

    //Saque simulado (o pagamento PIX e processado na hora no MVP)
    public HistoricoSaque sacar(Integer usuarioLogado, int usuarioId, SaqueRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        Usuario usuario = acessoBo.exigirProprioParticipante(usuarioLogado, usuarioId);
        String chavePix = ValidadorDados.texto(dto.getChavePix());
        if (chavePix == null) {
            chavePix = usuario.getChavePix();
        }
        if (chavePix == null) {
            throw new DadosInvalidosException("Informe a chave PIX (nao ha chave padrao no cadastro).");
        }
        if (dto.getCreditos() < SAQUE_MINIMO_CREDITOS) {
            throw new RegraNegocioException("O saque minimo e de " + SAQUE_MINIMO_CREDITOS + " creditos (RN13).");
        }
        BigDecimal creditos = BigDecimal.valueOf(dto.getCreditos());
        LocalDateTime agora = LocalDateTime.now().withNano(0);

        Connection conexao = connectionFactory.getConnection();
        try {
            conexao.setAutoCommit(false);
            Carteira carteira = carteiraDao.buscarPorUsuarioParaAtualizar(conexao, usuario);
            if (carteira.getCreditosSaldo().compareTo(creditos) < 0) {
                throw new RegraNegocioException("Saldo insuficiente: a carteira possui " + carteira.getCreditosSaldo()
                        + " creditos e foram solicitados " + creditos + ".");
            }
            BigDecimal valor = creditos.multiply(REAIS_POR_CREDITO);
            carteira.registrarSaque(creditos, valor, agora);
            carteiraDao.atualizar(conexao, carteira);

            HistoricoSaque saque = new HistoricoSaque();
            saque.setUsuario(usuario);
            saque.setCreditosUsados(creditos);
            saque.setValorReais(valor);
            saque.setChavePix(chavePix);
            saque.setStatus(StatusSaque.PROCESSADO);
            saque.setDataSolicitacao(agora);
            saque.setDataProcessamento(agora);
            saque.setObservacao("Pagamento PIX simulado no MVP.");
            saqueDao.cadastrar(conexao, saque);
            conexao.commit();
            return saque;
        } catch (Exception e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.close();
        }
    }

    public List<HistoricoConversao> conversoes(Integer usuarioLogado, int usuarioId) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirProprioOuValidador(usuarioLogado, usuarioId);
        return conversaoDao.listarPorUsuario(usuarioDao.buscarPorId(usuarioId));
    }

    public List<HistoricoSaque> saques(Integer usuarioLogado, int usuarioId) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirProprioOuValidador(usuarioLogado, usuarioId);
        return saqueDao.listarPorUsuario(usuarioDao.buscarPorId(usuarioId));
    }
}
