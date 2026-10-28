package br.com.soulprime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//Credenciais de acesso (e-mail + senha).
public class LoginRequestDto {

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "E-mail invalido.")
    @Size(max = 150, message = "O e-mail deve ter no maximo 150 caracteres.")
    private String email;

    @NotBlank(message = "Informe a senha.")
    private String senha;

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
}
