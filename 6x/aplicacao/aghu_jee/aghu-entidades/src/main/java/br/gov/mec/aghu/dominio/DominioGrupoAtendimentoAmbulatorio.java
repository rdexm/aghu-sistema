package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio para identificar o grupo de atendimento do ambulatório.
 * 
 * @author diego.pacheco
 * 
 */
public enum DominioGrupoAtendimentoAmbulatorio implements DominioString {
	
	ALUNO("Al."),
	CONTRATADO("Ct."),
	DOUTORANDO("Ddo."),
	R1("R1"),
	R2("R2"),
	R3("R3");
	
	private String value;
	
	private DominioGrupoAtendimentoAmbulatorio(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}	

	@Override
	public String getDescricao() {
		switch (this) {
		case ALUNO:
			return "Aluno";
		case CONTRATADO:
			return "Contratado";
		case DOUTORANDO:
			return "Doutorando";
		case R1:
			return "R1";
		case R2:
			return "R2";
		case R3:
			return "R3";
		default:
			return "";
		}
	}

}
