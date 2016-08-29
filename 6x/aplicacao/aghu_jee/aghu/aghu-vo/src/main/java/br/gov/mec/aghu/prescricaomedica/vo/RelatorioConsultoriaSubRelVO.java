package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;


public class RelatorioConsultoriaSubRelVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -619205434821586804L;
	private String especialidade;
	private String diagnostico;
	private String motivo;
	
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public String getDiagnostico() {
		return diagnostico;
	}
	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	
}
