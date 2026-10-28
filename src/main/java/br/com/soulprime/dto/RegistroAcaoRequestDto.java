package br.com.soulprime.dto;

import br.com.soulprime.model.enums.TipoEvidencia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//Registro de uma acao sustentavel pelo participante do header X-Usuario-Id.
public class RegistroAcaoRequestDto {

    @NotNull(message = "Informe a acao.")
    private Integer acaoId;

    @NotNull(message = "Informe o tipo de evidencia: COMPROVADA, PARCIAL ou SEM_COMPROVACAO.")
    private TipoEvidencia tipoEvidencia;

    @Size(max = 500, message = "A evidencia deve ter no maximo 500 caracteres.")
    private String evidencia;

    public Integer getAcaoId() {
        return acaoId;
    }

    public void setAcaoId(Integer acaoId) {
        this.acaoId = acaoId;
    }

    public TipoEvidencia getTipoEvidencia() {
        return tipoEvidencia;
    }

    public void setTipoEvidencia(TipoEvidencia tipoEvidencia) {
        this.tipoEvidencia = tipoEvidencia;
    }

    public String getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(String evidencia) {
        this.evidencia = evidencia;
    }
}
