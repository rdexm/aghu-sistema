package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

public class ConsultaComplementoVO implements Serializable {
	private static final long serialVersionUID = 456546465L;

	private Boolean selected;
	private String nomePaciente;
	private Integer pacCodigo;
	private String prontuario;
	private Long codCentral;
	
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public Long getCodCentral() {
		return codCentral;
	}
	public void setCodCentral(Long codCentral) {
		this.codCentral = codCentral;
	}
}
