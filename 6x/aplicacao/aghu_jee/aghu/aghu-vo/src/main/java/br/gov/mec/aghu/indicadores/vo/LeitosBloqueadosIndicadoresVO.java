package br.gov.mec.aghu.indicadores.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class LeitosBloqueadosIndicadoresVO {

	private Short unfSeq;
	private String ltoId;
	private DominioSimNao indBlq;
	private Date dataI;
	private Date dataF;
	private BigDecimal dias;
	private Short codBloqueio;
	private String descricao;
	private String privativo;

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

	public BigDecimal getDias() {
		return dias;
	}

	public void setDias(BigDecimal dias) {
		this.dias = dias;
	}

	public void setCodBloqueio(Short codBloqueio) {
		this.codBloqueio = codBloqueio;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Short getCodBloqueio() {
		return codBloqueio;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getPrivativo() {
		return privativo;
	}

	public void setPrivativo(String privativo) {
		this.privativo = privativo;
	}
	
	public String getDescricaoValores() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(unfSeq).append(';')
		.append(ltoId).append(';')
		.append(codBloqueio).append(';')
		.append(descricao).append(';')
		.append(indBlq).append(';')
		.append(dataI).append(';')
		.append(dataF).append(';')
		.append(dias).append(';')
		.append(privativo).append(';');
		
		return sb.toString();
		
	}

}
