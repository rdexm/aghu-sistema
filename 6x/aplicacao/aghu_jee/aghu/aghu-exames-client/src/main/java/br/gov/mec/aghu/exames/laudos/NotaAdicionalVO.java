package br.gov.mec.aghu.exames.laudos;

import java.io.Serializable;
import java.util.Date;

public class NotaAdicionalVO implements Serializable{
	
	private static final long serialVersionUID = -1244687529315217002L;
	private String nota;
	private Date criadoEm;
	private String criadoPor;

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}

}
