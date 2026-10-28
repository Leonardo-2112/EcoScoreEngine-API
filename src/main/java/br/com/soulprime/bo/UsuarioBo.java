package br.com.soulprime.bo;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.CarteiraDao;
import br.com.soulprime.dao.NivelDao;
import br.com.soulprime.dao.RegistroAcaoDao;
import br.com.soulprime.dao.RelatorioDao;
import br.com.soulprime.dao.UsuarioDao;
import br.com.soulprime.dao.UsuarioMissaoDao;
import br.com.soulprime.dto.AtualizarUsuarioRequestDto;
import br.com.soulprime.dto.NivelResponseDto;
import br.com.soulprime.dto.PainelResponseDto;
import br.com.soulprime.dto.UsuarioResumoDto;
import br.com.soulprime.exception.ConflitoException;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.exception.RegraNegocioException;
import br.com.soulprime.model.Carteira;
import br.com.soulprime.model.Nivel;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.UsuarioMissao;
import br.com.soulprime.model.enums.Perfil;
import br.com.soulprime.model.enums.StatusUsuario;
import org.modelmapper.ModelMapper;

//Regras do usuario: consulta, edicao, exclusao logica, desbloqueio e painel do participante
@Service
public class UsuarioBo {

    private final UsuarioDao usuarioDao;
    private final CarteiraDao carteiraDao;
    private final NivelDao nivelDao;
    private final RegistroAcaoDao registroAcaoDao;
    private final UsuarioMissaoDao usuarioMissaoDao;
    private final RelatorioDao relatorioDao;
    private final AcessoBo acessoBo;
    private final AuthBo authBo;
    private final ModelMapper mapper;

    public UsuarioBo(UsuarioDao usuarioDao, CarteiraDao carteiraDao, NivelDao nivelDao, RegistroAcaoDao registroAcaoDao,
                     UsuarioMissaoDao usuarioMissaoDao, RelatorioDao relatorioDao, AcessoBo acessoBo, AuthBo authBo, ModelMapper mapper) {
        this.usuarioDao = usuarioDao;
        this.carteiraDao = carteiraDao;
        this.nivelDao = nivelDao;
        this.registroAcaoDao = registroAcaoDao;
        this.usuarioMissaoDao = usuarioMissaoDao;
        this.relatorioDao = relatorioDao;
        this.acessoBo = acessoBo;
        this.authBo = authBo;
        this.mapper = mapper;
    }

    public Usuario buscar(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirProprioOuValidador(usuarioLogado, id);
        return usuarioDao.buscarPorId(id);
    }

    //Lista de usuarios (perfil VALIDADOR) com filtros opcionais
    public List<Usuario> listar(Integer usuarioLogado, String perfil, String status) throws SQLException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        Perfil filtroPerfil = perfil == null ? null : ValidadorDados.converterEnum(Perfil.class, perfil);
        StatusUsuario filtroStatus = status == null ? null : ValidadorDados.converterEnum(StatusUsuario.class, status);
        return usuarioDao.listar(filtroPerfil, filtroStatus);
    }

    public Usuario atualizar(Integer usuarioLogado, int id, AtualizarUsuarioRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirProprioOuValidador(usuarioLogado, id);
        Usuario usuario = usuarioDao.buscarPorId(id);
        String email = dto.getEmail().trim().toLowerCase();
        if (usuarioDao.existeEmail(email, id)) {
            throw new ConflitoException("E-mail ja cadastrado por outro usuario (RN01).");
        }
        ValidadorDados.validarIdade(dto.getDataNascimento());
        usuario.setNome(ValidadorDados.texto(dto.getNome()));
        usuario.setEmail(email);
        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank()) {
            usuario.setSenha(authBo.gerarHash(dto.getNovaSenha()));
        }
        usuario.setTelefone(ValidadorDados.somenteDigitos(dto.getTelefone()));
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setCep(ValidadorDados.somenteDigitos(dto.getCep()));
        usuario.setLogradouro(ValidadorDados.texto(dto.getLogradouro()));
        usuario.setCidade(ValidadorDados.texto(dto.getCidade()));
        usuario.setUf(ValidadorDados.validarUf(dto.getUf()));
        usuario.setChavePix(ValidadorDados.texto(dto.getChavePix()));
        usuarioDao.atualizar(usuario);
        return usuario;
    }

    //Exclusao logica (status INATIVO): o historico de acoes e da carteira e preservado
    public void inativar(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirProprioOuValidador(usuarioLogado, id);
        Usuario usuario = usuarioDao.buscarPorId(id);
        if (usuario.getStatus() == StatusUsuario.INATIVO) {
            throw new RegraNegocioException("Usuario ja esta inativo.");
        }
        usuario.setStatus(StatusUsuario.INATIVO);
        usuarioDao.atualizarAcesso(usuario);
    }

    //Desbloqueio de conta SUSPENSA por excesso de tentativas de login (perfil VALIDADOR)
    public Usuario desbloquear(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        Usuario usuario = usuarioDao.buscarPorId(id);
        if (usuario.getStatus() != StatusUsuario.SUSPENSO) {
            throw new RegraNegocioException("Somente contas SUSPENSAS podem ser desbloqueadas.");
        }
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setTentativasLogin(0);
        usuarioDao.atualizarAcesso(usuario);
        return usuario;
    }

    //Desafios tematicos do participante e o progresso em cada um
    public List<UsuarioMissao> desafios(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirProprioOuValidador(usuarioLogado, id);
        return usuarioMissaoDao.listarPorUsuario(usuarioDao.buscarPorId(id));
    }

    //Painel do participante: nivel, proximo nivel, missao em andamento ("x de 3"), carteira, CO2 e ranking
    public PainelResponseDto painel(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirProprioOuValidador(usuarioLogado, id);
        Usuario usuario = usuarioDao.buscarPorId(id);
        if (usuario.getPerfil() != Perfil.PARTICIPANTE) {
            throw new RegraNegocioException("O painel de gamificacao e exclusivo do perfil PARTICIPANTE.");
        }
        int creditados = registroAcaoDao.contarCreditados(id);
        Nivel proximo = nivelDao.buscarProximo(usuario.getNivel());
        Carteira carteira = carteiraDao.buscarPorUsuario(usuario);

        PainelResponseDto painel = new PainelResponseDto();
        painel.setUsuario(mapper.map(usuario, UsuarioResumoDto.class));
        painel.setNivel(mapper.map(usuario.getNivel(), NivelResponseDto.class));
        if (proximo != null) {
            painel.setProximoNivel(mapper.map(proximo, NivelResponseDto.class));
            painel.setMissoesParaProximoNivel(Math.max(0, proximo.getMissoesMinimas() - usuario.getMissoesConcluidas()));
        }
        painel.setMissoesConcluidas(usuario.getMissoesConcluidas());
        painel.setAcoesNaMissaoAtual(creditados % GamificacaoBo.ACOES_POR_MISSAO);
        painel.setAcoesPorMissao(GamificacaoBo.ACOES_POR_MISSAO);
        painel.setPontosTotal(usuario.getPontosTotal());
        painel.setPontosSaldo(usuario.getPontosSaldo());
        painel.setCreditos(carteira.getCreditosSaldo());
        painel.setSaldoEmReais(carteira.getSaldoEmReais());
        BigDecimal co2 = registroAcaoDao.somarCo2Creditado(id);
        painel.setCo2EvitadoKg(co2);
        painel.setRegistrosCreditados(creditados);
        painel.setRegistrosPendentes(registroAcaoDao.contarPendentes(id));
        painel.setPosicaoRanking(relatorioDao.posicaoNoRanking(id));
        return painel;
    }
}
