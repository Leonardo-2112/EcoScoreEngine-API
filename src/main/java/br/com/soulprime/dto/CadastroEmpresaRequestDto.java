package br.com.soulprime.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

//Cadastro B2B: empresa (nasce PENDENTE - RN03) + representante (perfil EMPRESA).
public class CadastroEmpresaRequestDto {

    @NotNull(message = "Informe os dados da empresa.")
    @Valid
    private EmpresaRequestDto empresa;

    @NotNull(message = "Informe os dados do representante.")
    @Valid
    private CadastroUsuarioRequestDto representante;

    public EmpresaRequestDto getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaRequestDto empresa) {
        this.empresa = empresa;
    }

    public CadastroUsuarioRequestDto getRepresentante() {
        return representante;
    }

    public void setRepresentante(CadastroUsuarioRequestDto representante) {
        this.representante = representante;
    }
}
