package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

/**
 * VO para representar grupo sanguineo
 * 
 * @author luismoura
 * 
 */
public class DadosSanguineos implements Serializable {
	private static final long serialVersionUID = 6782462473173183819L;

	private String grupoSanguineo;
	private String fatorRh;
	private String coombs;

	public DadosSanguineos() {

	}

	public DadosSanguineos(String grupoSanguineo, String fatorRh, String coombs) {
		super();
		this.grupoSanguineo = grupoSanguineo;
		this.fatorRh = fatorRh;
		this.coombs = coombs;
	}

	public String getGrupoSanguineo() {
		return grupoSanguineo;
	}

	public void setGrupoSanguineo(String grupoSanguineo) {
		this.grupoSanguineo = grupoSanguineo;
	}

	public String getFatorRh() {
		return fatorRh;
	}

	public void setFatorRh(String fatorRh) {
		this.fatorRh = fatorRh;
	}

	public String getCoombs() {
		return coombs;
	}

	public void setCoombs(String coombs) {
		this.coombs = coombs;
	}
}