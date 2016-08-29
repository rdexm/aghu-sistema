package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class FatProcCboVO implements Serializable {

	private static final long serialVersionUID = 2553867114755571981L;
	
	private Long codTabela;
	
	private Short iphPhoSeq;
	
	private Integer iphSeq;
	
	private String cbosValidos;
	
	private String tabCbosValidos;
	
	private Integer qtdCbos;
	
	private Boolean processado;
	
	private Date competencia;


	public FatProcCboVO() {
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}


	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}


	public Integer getIphSeq() {
		return iphSeq;
	}


	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}


	public String getCbosValidos() {
		return cbosValidos;
	}


	public void setCbosValidos(String cbosValidos) {
		this.cbosValidos = cbosValidos;
	}


	public String getTabCbosValidos() {
		return tabCbosValidos;
	}


	public void setTabCbosValidos(String tabCbosValidos) {
		this.tabCbosValidos = tabCbosValidos;
	}

	public Integer getQtdCbos() {
		return qtdCbos;
	}

	public void setQtdCbos(Integer qtdCbos) {
		this.qtdCbos = qtdCbos;
	}

	public Boolean getProcessado() {
		return processado;
	}

	public void setProcessado(Boolean processado) {
		this.processado = processado;
	}

	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}


}
