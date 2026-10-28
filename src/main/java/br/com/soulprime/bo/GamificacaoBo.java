package br.com.soulprime.bo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.MissaoDao;
import br.com.soulprime.dao.NivelDao;
import br.com.soulprime.dao.RegistroAcaoDao;
import br.com.soulprime.dao.UsuarioDao;
import br.com.soulprime.dao.UsuarioMissaoDao;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.model.Missao;
import br.com.soulprime.model.Nivel;
import br.com.soulprime.model.RegistroAcao;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.UsuarioMissao;
import br.com.soulprime.model.enums.StatusMissaoUsuario;

/**
 * Regras centrais da gamificacao (docs/regras-negocio.md):
 *   pontos = TRUNC((impacto x 10 x 1/repeticoes x confiabilidade) x bonus do nivel)
 *   missoes concluidas = registros creditados / 3 (nunca diminuem)
 *   nivel = faixa de missoes 0-4, 5-9, 10-19, 20+ (nunca diminui)
 */
@Service
public class GamificacaoBo {

    //Slides da mentoria: cada missao = 3 acoes sustentaveis
    public static final int ACOES_POR_MISSAO = 3;

    private final UsuarioDao usuarioDao;
    private final NivelDao nivelDao;
    private final RegistroAcaoDao registroAcaoDao;
    private final MissaoDao missaoDao;
    private final UsuarioMissaoDao usuarioMissaoDao;

    public GamificacaoBo(UsuarioDao usuarioDao, NivelDao nivelDao, RegistroAcaoDao registroAcaoDao, MissaoDao missaoDao,
                         UsuarioMissaoDao usuarioMissaoDao) {
        this.usuarioDao = usuarioDao;
        this.nivelDao = nivelDao;
        this.registroAcaoDao = registroAcaoDao;
        this.missaoDao = missaoDao;
        this.usuarioMissaoDao = usuarioMissaoDao;
    }

    //Calcula os pontos de um registro (TRUNC = arredonda para baixo)
    public long calcularPontos(int impacto, int repeticoes, BigDecimal confiabilidade, BigDecimal bonusNivel) {
        BigDecimal pontos = BigDecimal.valueOf(impacto * 10L)
                .multiply(confiabilidade)
                .multiply(bonusNivel)
                .divide(BigDecimal.valueOf(repeticoes), 6, RoundingMode.HALF_UP);
        return pontos.setScale(0, RoundingMode.DOWN).longValue();
    }

    //Memoria de calculo gravada no registro, ex.: "(5 x 10 x 1/2 x 0.7) x 1.1 = 19"
    public String memoriaCalculo(int impacto, int repeticoes, BigDecimal confiabilidade, BigDecimal bonus, long pontos) {
        return "(" + impacto + " x 10 x 1/" + repeticoes + " x " + confiabilidade.setScale(1, RoundingMode.HALF_UP) + ") x "
                + bonus.setScale(1, RoundingMode.HALF_UP) + " = " + pontos;
    }

    //Recupera o bonus de nivel do momento do registro a partir da memoria de calculo (usado na validacao)
    public BigDecimal bonusDaMemoria(String observacao, BigDecimal bonusPadrao) {
        if (observacao != null) {
            Matcher matcher = Pattern.compile("\\) x ([0-9]+(\\.[0-9]+)?) =").matcher(observacao);
            if (matcher.find()) {
                return new BigDecimal(matcher.group(1));
            }
        }
        return bonusPadrao;
    }

    /**
     * Credita os pontos de um registro aprovado, dentro da transacao recebida:
     * soma total e saldo, recalcula missoes e nivel e avanca os desafios tematicos.
     */
    public void creditar(Connection conexao, Usuario usuario, RegistroAcao registro, LocalDateTime agora)
            throws SQLException, EntidadeNaoEncontradaException {
        usuario.creditarPontos(registro.getPontosGerados());

        int creditados = registroAcaoDao.contarCreditados(conexao, usuario.getId());
        usuario.atualizarMissoes(creditados / ACOES_POR_MISSAO);

        Nivel novoNivel = nivelDao.buscarPorMissoes(conexao, usuario.getMissoesConcluidas());
        if (novoNivel.getMissoesMinimas() > usuario.getNivel().getMissoesMinimas()) {
            usuario.setNivel(novoNivel); //o nivel so sobe
        }
        usuarioDao.atualizarPontuacao(conexao, usuario);

        List<Missao> desafios = missaoDao.listarAtivasPorAcao(conexao, registro.getAcao().getId());
        for (Missao missao : desafios) {
            UsuarioMissao progresso = usuarioMissaoDao.buscar(conexao, usuario, missao);
            if (progresso == null) {
                progresso = new UsuarioMissao(usuario, missao, agora);
                progresso.registrarAcao(agora);
                usuarioMissaoDao.cadastrar(conexao, progresso);
            } else if (progresso.getStatus() == StatusMissaoUsuario.EM_ANDAMENTO) {
                progresso.setMissao(missao);
                progresso.registrarAcao(agora);
                usuarioMissaoDao.atualizar(conexao, progresso);
            }
        }
    }
}
