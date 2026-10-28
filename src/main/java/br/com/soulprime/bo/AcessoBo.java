package br.com.soulprime.bo;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.UsuarioDao;
import br.com.soulprime.exception.AcessoNegadoException;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.exception.NaoAutenticadoException;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.enums.Perfil;

/**
 * Controle de acesso por perfil. O Front envia o id do usuario logado no header X-Usuario-Id;
 * aqui conferimos se ele existe, se esta ATIVO e se tem o perfil exigido pela operacao.
 */
@Service
public class AcessoBo {

    public static final String HEADER = "X-Usuario-Id";

    private final UsuarioDao usuarioDao;

    public AcessoBo(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    //Usuario logado: 401 se nao informado/inexistente, 403 se a conta nao estiver ativa
    public Usuario usuarioLogado(Integer idUsuario) throws SQLException {
        if (idUsuario == null) {
            throw new NaoAutenticadoException("Informe o usuario logado no header " + HEADER + ".");
        }
        Usuario usuario;
        try {
            usuario = usuarioDao.buscarPorId(idUsuario);
        } catch (EntidadeNaoEncontradaException e) {
            throw new NaoAutenticadoException("Usuario do header " + HEADER + " nao encontrado.");
        }
        if (!usuario.isAtivo()) {
            throw new AcessoNegadoException("Conta " + usuario.getStatus().name().toLowerCase() + ": acesso nao permitido.");
        }
        if (usuario.getPerfil() == Perfil.EMPRESA && !usuario.getEmpresa().isAtiva()) {
            throw new AcessoNegadoException("Empresa ainda nao ativada pela equipe GaiaTech.");
        }
        return usuario;
    }

    //Usuario logado que obrigatoriamente possui um dos perfis informados
    public Usuario exigirPerfil(Integer idUsuario, Perfil... perfis) throws SQLException {
        Usuario usuario = usuarioLogado(idUsuario);
        if (!usuario.possuiPerfil(perfis)) {
            throw new AcessoNegadoException("Operacao permitida apenas para o perfil " + java.util.Arrays.toString(perfis) + ".");
        }
        return usuario;
    }

    //O proprio usuario (id da URL) ou um VALIDADOR
    public Usuario exigirProprioOuValidador(Integer idUsuario, int idAlvo) throws SQLException {
        Usuario usuario = usuarioLogado(idUsuario);
        if (usuario.getId() != idAlvo && usuario.getPerfil() != Perfil.VALIDADOR) {
            throw new AcessoNegadoException("Voce so pode acessar os seus proprios dados.");
        }
        return usuario;
    }

    //Somente o proprio participante (operacoes da carteira: converter e sacar)
    public Usuario exigirProprioParticipante(Integer idUsuario, int idAlvo) throws SQLException {
        Usuario usuario = exigirPerfil(idUsuario, Perfil.PARTICIPANTE);
        if (usuario.getId() != idAlvo) {
            throw new AcessoNegadoException("Somente o proprio participante pode movimentar a sua carteira.");
        }
        return usuario;
    }

    //Endpoints publicos que mudam conforme o perfil: sem header retorna null
    public Usuario usuarioOpcional(Integer idUsuario) throws SQLException {
        if (idUsuario == null) {
            return null;
        }
        return usuarioLogado(idUsuario);
    }
}
