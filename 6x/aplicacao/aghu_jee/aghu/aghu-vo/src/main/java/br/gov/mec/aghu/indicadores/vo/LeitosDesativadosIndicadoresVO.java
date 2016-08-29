package br.gov.mec.aghu.indicadores.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class LeitosDesativadosIndicadoresVO {

	private Short unfSeq;
	private String ltoId;
	private DominioSimNao indBlq;
	private Date dataI;
	private Date dataF;
	private Integer dias;

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getLtoId() {
		return ltoId;
	}

	public void setLtoId(String ltoId) {
		this.ltoId = ltoId;
	}

	public DominioSimNao getIndBlq() {
		return indBlq;
	}

	public void setIndBlq(DominioSimNao indBlq) {
		this.indBlq = indBlq;
	}

	public Date getDataI() {
		return dataI;
	}

	public void setDataI(Date dataI) {
		this.dataI = dataI;
	}

	public Date getDataF() {
		return dataF;
	}

	public void setDataF(Date dataF) {
		this.dataF = dataF;
	}

	public Integer getDias() {
		return dias;
	}

	public void setDias(Integer dias) {
		this.dias = dias;
	}
	
	public String getDescricaoValores() {
		StringBuilder sb = new StringBuilder();
		sb.append(unfSeq).append(';')
		.append(ltoId).append(';')
		.append(indBlq).append(';')
		.append(dataI).append(';')
		.append(dataF).append(';')
		.append(dias).append(';');

		return sb.toString();
	}

}
