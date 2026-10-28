package br.com.soulprime.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.soulprime.model.enums.Perfil;
import br.com.soulprime.model.enums.StatusUsuario;

//Dados do usuario (sem a senha).
public class UsuarioResponseDto {

    private int id;

    private String nome;

    private String email;

    private String cpf;

    private String telefone;

    private LocalDate dataNascimento;

    private String cep;

    private String logradouro;

    private String cidade;

    private String uf;

    private String chavePix;

    private Perfil perfil;

    private EmpresaResumoDto empresa;

    private NivelResponseDto nivel;

    private long pontosTotal;

    private long pontosSaldo;

    private int missoesConcluidas;

    private int tentativasLogin;

    private LocalDateTime dataCadastro;

    private StatusUsuario status;

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

    public EmpresaResumoDto getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaResumoDto empresa) {
        this.empresa = empresa;
    }

    public NivelResponseDto getNivel() {
        return nivel;
    }

    public void setNivel(NivelResponseDto nivel) {
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
