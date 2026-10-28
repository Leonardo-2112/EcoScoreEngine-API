package br.com.soulprime.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.soulprime.dao.CarteiraDao;
import br.com.soulprime.dao.NivelDao;
import br.com.soulprime.dao.UsuarioDao;
import br.com.soulprime.exception.AcessoNegadoException;
import br.com.soulprime.exception.ConflitoException;
import br.com.soulprime.exception.ContaBloqueadaException;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.exception.NaoAutenticadoException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.Carteira;
import br.com.soulprime.model.EmpresaParceira;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.enums.Perfil;
import br.com.soulprime.model.enums.StatusUsuario;

//Login (bloqueio apos 5 tentativas - RN02) e cadastro de usuarios (senha com hash BCrypt - RNF01)
@Service
public class AuthBo {

    private final UsuarioDao usuarioDao;
    private final CarteiraDao carteiraDao;
    private final NivelDao nivelDao;
    private final ConnectionFactory connectionFactory;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthBo(UsuarioDao usuarioDao, CarteiraDao carteiraDao, NivelDao nivelDao, ConnectionFactory connectionFactory) {
        this.usuarioDao = usuarioDao;
        this.carteiraDao = carteiraDao;
        this.nivelDao = nivelDao;
        this.connectionFactory = connectionFactory;
    }

    public Usuario login(String email, String senha) throws SQLException {
        Usuario usuario;
        try {
            usuario = usuarioDao.buscarPorEmail(email);
        } catch (EntidadeNaoEncontradaException e) {
            throw new NaoAutenticadoException("E-mail ou senha invalidos.");
        }

        if (usuario.getStatus() == StatusUsuario.SUSPENSO) {
            throw new ContaBloqueadaException("Conta bloqueada apos " + Usuario.MAX_TENTATIVAS_LOGIN
                    + " tentativas invalidas. Solicite o desbloqueio a equipe GaiaTech.");
        }
        if (usuario.getStatus() == StatusUsuario.INATIVO) {
            throw new AcessoNegadoException("Conta inativa.");
        }

        if (!encoder.matches(senha, usuario.getSenha())) {
            boolean bloqueou = usuario.registrarFalhaLogin();
            usuarioDao.atualizarAcesso(usuario);
            if (bloqueou) {
                throw new ContaBloqueadaException("Senha invalida pela " + Usuario.MAX_TENTATIVAS_LOGIN
                        + "a vez: conta bloqueada. Solicite o desbloqueio a equipe GaiaTech.");
            }
            throw new NaoAutenticadoException("E-mail ou senha invalidos. Tentativas restantes: " + usuario.tentativasRestantes() + ".");
        }

        if (usuario.getPerfil() == Perfil.EMPRESA && !usuario.getEmpresa().isAtiva()) {
            throw new AcessoNegadoException("Empresa aguardando validacao do CNPJ pela equipe GaiaTech (RN03).");
        }
        if (usuario.getTentativasLogin() > 0) {
            usuario.setTentativasLogin(0);
            usuarioDao.atualizarAcesso(usuario);
        }
        return usuario;
    }

    //Cadastro publico: sempre cria um PARTICIPANTE com a carteira zerada (usuario + carteira na mesma transacao)
    public Usuario cadastrarParticipante(Usuario usuario) throws SQLException, EntidadeNaoEncontradaException {
        validarNovoUsuario(usuario);
        Connection conexao = connectionFactory.getConnection();
        try {
            conexao.setAutoCommit(false);
            gravarUsuario(conexao, usuario, Perfil.PARTICIPANTE, null);
            Carteira carteira = new Carteira(usuario);
            carteira.setUltimaAtualizacao(usuario.getDataCadastro());
            carteiraDao.cadastrar(conexao, carteira);
            conexao.commit();
            return usuario;
        } catch (Exception e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.close();
        }
    }

    //Valida e normaliza os dados de um novo usuario (CPF, idade, UF, e-mail e CPF unicos)
    void validarNovoUsuario(Usuario usuario) throws SQLException {
        usuario.setCpf(ValidadorDados.validarCpf(usuario.getCpf()));
        ValidadorDados.validarIdade(usuario.getDataNascimento());
        usuario.setUf(ValidadorDados.validarUf(usuario.getUf()));
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        if (usuarioDao.existeEmail(usuario.getEmail(), 0)) {
            throw new ConflitoException("E-mail ja cadastrado (RN01).");
        }
        if (usuarioDao.existeCpf(usuario.getCpf())) {
            throw new ConflitoException("CPF ja cadastrado.");
        }
    }

    //Completa os dados do usuario e grava usando a conexao da transacao (tambem usado no cadastro de empresa)
    void gravarUsuario(Connection conexao, Usuario usuario, Perfil perfil, EmpresaParceira empresa)
            throws SQLException, EntidadeNaoEncontradaException {
        usuario.setNome(ValidadorDados.texto(usuario.getNome()));
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        usuario.setTelefone(ValidadorDados.somenteDigitos(usuario.getTelefone()));
        usuario.setCep(ValidadorDados.somenteDigitos(usuario.getCep()));
        usuario.setLogradouro(ValidadorDados.texto(usuario.getLogradouro()));
        usuario.setCidade(ValidadorDados.texto(usuario.getCidade()));
        usuario.setChavePix(ValidadorDados.texto(usuario.getChavePix()));
        usuario.setPerfil(perfil);
        usuario.setEmpresa(empresa);
        usuario.setNivel(nivelDao.buscarPorMissoes(conexao, 0));
        usuario.setDataCadastro(LocalDateTime.now().withNano(0));
        usuario.setStatus(StatusUsuario.ATIVO);
        usuarioDao.cadastrar(conexao, usuario);
    }

    String gerarHash(String senha) {
        return encoder.encode(senha);
    }
}
