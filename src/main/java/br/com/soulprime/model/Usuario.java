package br.com.soulprime.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.soulprime.model.enums.Perfil;
import br.com.soulprime.model.enums.StatusUsuario;

/**
 * Pessoa que acessa a plataforma (TB_USUARIO): participante, validador ou representante de empresa.
 * Associacoes como objeto: Nivel nivel e EmpresaParceira empresa.
 */
public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private String telefone;
    private LocalDate dataNascimento;
    private String cep;
    private String logradouro;
    private String cidade;
    private String uf;
    private String chavePix;
    private Perfil perfil;
    private EmpresaParceira empresa;
    private Nivel nivel;
    private long pontosTotal;
    private long pontosSaldo;
    private int missoesConcluidas;
    private int tentativasLogin;
    private LocalDateTime dataCadastro;
    private StatusUsuario status;

    public Usuario() {
    }

    public Usuario(int id) {
        this.id = id;
    }

    /** Maximo de senhas erradas seguidas antes do bloqueio (RN02). */
    public static final int MAX_TENTATIVAS_LOGIN = 5;

    public boolean isAtivo() {
        return status == StatusUsuario.ATIVO;
    }

    public boolean possuiPerfil(Perfil... perfis) {
        for (Perfil p : perfis) {
            if (p == perfil) {
                return true;
            }
        }
        return false;
    }

    /** Credita pontos de um registro aprovado: soma no total (ranking) e no saldo (conversao). */
    public void creditarPontos(long pontos) {
        this.pontosTotal += pontos;
        this.pontosSaldo += pontos;
    }

    /** Retira pontos do saldo na conversao. O total acumulado nao muda (progresso permanente). */
    public void debitarSaldo(long pontos) {
        this.pontosSaldo -= pontos;
    }

    /** Missoes so aumentam: nunca diminuem (progresso permanente). */
    public void atualizarMissoes(int missoesCalculadas) {
        this.missoesConcluidas = Math.max(this.missoesConcluidas, missoesCalculadas);
    }

    /**
     * Registra uma senha errada. Ao atingir 5 tentativas a conta e SUSPENSA.
     * @return true se a conta acabou de ser bloqueada
     */
    public boolean registrarFalhaLogin() {
        this.tentativasLogin = Math.min(tentativasLogin + 1, MAX_TENTATIVAS_LOGIN);
        if (tentativasLogin >= MAX_TENTATIVAS_LOGIN) {
            this.status = StatusUsuario.SUSPENSO;
            return true;
        }
        return false;
    }

    public int tentativasRestantes() {
        return MAX_TENTATIVAS_LOGIN - tentativasLogin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public EmpresaParceira getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaParceira empresa) {
        this.empresa = empresa;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public long getPontosTotal() {
        return pontosTotal;
    }

    public void setPontosTotal(long pontosTotal) {
        this.pontosTotal = pontosTotal;
    }

    public long getPontosSaldo() {
        return pontosSaldo;
    }

    public void setPontosSaldo(long pontosSaldo) {
        this.pontosSaldo = pontosSaldo;
    }

    public int getMissoesConcluidas() {
        return missoesConcluidas;
    }

    public void setMissoesConcluidas(int missoesConcluidas) {
        this.missoesConcluidas = missoesConcluidas;
    }

    public int getTentativasLogin() {
        return tentativasLogin;
    }

    public void setTentativasLogin(int tentativasLogin) {
        this.tentativasLogin = tentativasLogin;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }
}
