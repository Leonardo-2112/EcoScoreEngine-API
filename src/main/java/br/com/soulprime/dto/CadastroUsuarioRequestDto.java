package br.com.soulprime.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//Cadastro ampliado do usuario. CPF, idade e e-mail duplicado sao conferidos no BO.
public class CadastroUsuarioRequestDto {

    @NotBlank(message = "Informe o nome completo.")
    @Size(min = 3, max = 100, message = "O nome deve ter de 3 a 100 caracteres.")
    @Pattern(regexp = "^\\S+(\\s+\\S+)+$", message = "Informe nome e sobrenome.")
    private String nome;

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "E-mail invalido.")
    @Size(max = 150, message = "O e-mail deve ter no maximo 150 caracteres.")
    private String email;

    @NotBlank(message = "Informe a senha.")
    @Size(min = 6, max = 72, message = "A senha deve ter de 6 a 72 caracteres.")
    private String senha;

    @NotBlank(message = "Informe o CPF.")
    @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$", message = "CPF deve ter 11 digitos.")
    private String cpf;

    @NotBlank(message = "Informe o telefone.")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "Telefone deve ter DDD + numero (10 ou 11 digitos).")
    private String telefone;

    @NotNull(message = "Informe a data de nascimento.")
    @Past(message = "Data de nascimento invalida.")
    private LocalDate dataNascimento;

    @NotBlank(message = "Informe o CEP.")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve ter 8 digitos.")
    private String cep;

    @Size(max = 150, message = "O logradouro deve ter no maximo 150 caracteres.")
    private String logradouro;

    @NotBlank(message = "Informe a cidade.")
    @Size(max = 80, message = "A cidade deve ter no maximo 80 caracteres.")
    private String cidade;

    @NotBlank(message = "Informe a UF.")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "UF deve ter 2 letras.")
    private String uf;

    @Size(max = 150, message = "A chave PIX deve ter no maximo 150 caracteres.")
    private String chavePix;

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
}
