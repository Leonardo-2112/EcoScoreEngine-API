package br.com.soulprime.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.EmpresaParceiraDao;
import br.com.soulprime.dto.EmpresaRequestDto;
import br.com.soulprime.exception.AcessoNegadoException;
import br.com.soulprime.exception.ConflitoException;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.exception.RegraNegocioException;
import br.com.soulprime.factory.ConnectionFactory;
import br.com.soulprime.model.EmpresaParceira;
import br.com.soulprime.model.Usuario;
import br.com.soulprime.model.enums.Perfil;
import br.com.soulprime.model.enums.StatusEmpresa;

//Empresas parceiras (modulo B2B). A empresa nasce PENDENTE e so e ativada pelo VALIDADOR (RN03)
@Service
public class EmpresaBo {

    private final EmpresaParceiraDao empresaDao;
    private final AuthBo authBo;
    private final AcessoBo acessoBo;
    private final ConnectionFactory connectionFactory;

    public EmpresaBo(EmpresaParceiraDao empresaDao, AuthBo authBo, AcessoBo acessoBo, ConnectionFactory connectionFactory) {
        this.empresaDao = empresaDao;
        this.authBo = authBo;
        this.acessoBo = acessoBo;
        this.connectionFactory = connectionFactory;
    }

    //Cadastro publico B2B: empresa PENDENTE + representante com perfil EMPRESA, na mesma transacao
    public EmpresaParceira cadastrar(EmpresaRequestDto dadosEmpresa, Usuario representante) throws SQLException, EntidadeNaoEncontradaException {
        String cnpj = ValidadorDados.validarCnpj(dadosEmpresa.getCnpj());
        if (empresaDao.existeCnpj(cnpj)) {
            throw new ConflitoException("CNPJ ja cadastrado.");
        }
        if (empresaDao.existeEmail(dadosEmpresa.getEmail(), 0)) {
            throw new ConflitoException("E-mail corporativo ja cadastrado.");
        }
        authBo.validarNovoUsuario(representante);

        EmpresaParceira empresa = new EmpresaParceira();
        preencher(empresa, dadosEmpresa);
        empresa.setCnpj(cnpj);
        empresa.setDataCadastro(LocalDateTime.now().withNano(0));
        empresa.setStatus(StatusEmpresa.PENDENTE);

        Connection conexao = connectionFactory.getConnection();
        try {
            conexao.setAutoCommit(false);
            empresaDao.cadastrar(conexao, empresa);
            authBo.gravarUsuario(conexao, representante, Perfil.EMPRESA, empresa);
            conexao.commit();
            return empresa;
        } catch (Exception e) {
            conexao.rollback();
            throw e;
        } finally {
            conexao.close();
        }
    }

    public List<EmpresaParceira> listar(Integer usuarioLogado, String status) throws SQLException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        StatusEmpresa filtro = status == null ? null : ValidadorDados.converterEnum(StatusEmpresa.class, status);
        return empresaDao.listar(filtro);
    }

    public EmpresaParceira buscar(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        exigirDonaOuValidador(usuarioLogado, id);
        return empresaDao.buscarPorId(id);
    }

    public EmpresaParceira atualizar(Integer usuarioLogado, int id, EmpresaRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        exigirDonaOuValidador(usuarioLogado, id);
        EmpresaParceira empresa = empresaDao.buscarPorId(id);
        if (!ValidadorDados.somenteDigitos(empresa.getCnpj()).equals(ValidadorDados.somenteDigitos(dto.getCnpj()))) {
            throw new RegraNegocioException("O CNPJ nao pode ser alterado.");
        }
        if (empresaDao.existeEmail(dto.getEmail(), id)) {
            throw new ConflitoException("E-mail corporativo ja cadastrado.");
        }
        preencher(empresa, dto);
        empresaDao.atualizar(empresa);
        return empresa;
    }

    //Validacao do CNPJ/ativacao, suspensao ou inativacao (perfil VALIDADOR)
    public EmpresaParceira alterarStatus(Integer usuarioLogado, int id, String status) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        StatusEmpresa novoStatus = ValidadorDados.converterEnum(StatusEmpresa.class, status);
        EmpresaParceira empresa = empresaDao.buscarPorId(id);
        if (!transicaoPermitida(empresa.getStatus(), novoStatus)) {
            throw new RegraNegocioException("Transicao de status nao permitida: " + empresa.getStatus() + " -> " + novoStatus + ".");
        }
        empresaDao.atualizarStatus(id, novoStatus);
        empresa.setStatus(novoStatus);
        return empresa;
    }

    //PENDENTE -> ATIVA/INATIVA; ATIVA -> SUSPENSA/INATIVA; SUSPENSA -> ATIVA/INATIVA; INATIVA -> ATIVA
    private boolean transicaoPermitida(StatusEmpresa atual, StatusEmpresa novo) {
        switch (atual) {
            case PENDENTE:
                return novo == StatusEmpresa.ATIVA || novo == StatusEmpresa.INATIVA;
            case ATIVA:
                return novo == StatusEmpresa.SUSPENSA || novo == StatusEmpresa.INATIVA;
            case SUSPENSA:
                return novo == StatusEmpresa.ATIVA || novo == StatusEmpresa.INATIVA;
            case INATIVA:
                return novo == StatusEmpresa.ATIVA;
            default:
                return false;
        }
    }

    private void exigirDonaOuValidador(Integer usuarioLogado, int empresaId) throws SQLException {
        Usuario usuario = acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR, Perfil.EMPRESA);
        if (usuario.getPerfil() == Perfil.EMPRESA && usuario.getEmpresa().getId() != empresaId) {
            throw new AcessoNegadoException("Voce so pode acessar os dados da sua propria empresa.");
        }
    }

    private void preencher(EmpresaParceira empresa, EmpresaRequestDto dto) {
        empresa.setNome(ValidadorDados.texto(dto.getNome()));
        empresa.setEmail(dto.getEmail().trim().toLowerCase());
        empresa.setSegmento(ValidadorDados.texto(dto.getSegmento()));
        empresa.setOrcamentoEsg(dto.getOrcamentoEsg() == null ? BigDecimal.ZERO : dto.getOrcamentoEsg());
    }
}
