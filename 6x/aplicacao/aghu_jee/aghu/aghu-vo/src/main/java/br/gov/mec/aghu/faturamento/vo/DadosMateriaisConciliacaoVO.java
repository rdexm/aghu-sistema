package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class DadosMateriaisConciliacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2330860926020398938L;

	private Date dataProcedimento;

	private String procedimentoPrincipal;

	private String procedimentoSus;

	private String justificativaReq;

	private String observacoesGerais;
	

	public DadosMateriaisConciliacaoVO() {
	}

	public DadosMateriaisConciliacaoVO(Date dataProcedimento, String procedimentoPrincipal, String procedimentoSus,
			String justificativaReq, String observacoesGerais) {
		this.dataProcedimento = dataProcedimento;
		this.procedimentoPrincipal = procedimentoPrincipal;
		this.procedimentoSus = procedimentoSus;
		this.justificativaReq = justificativaReq;
		this.observacoesGerais = observacoesGerais;

	}

	public Date getDataProcedimento() {
		return dataProcedimento;
	}

	public void setDataProcedimento(Date dataProcedimento) {
		this.dataProcedimento = dataProcedimento;
	}

	public String getProcedimentoPricipal() {
		return procedimentoPrincipal;
	}

	public void setProcedimentoPricipal(String procedimentoPricipal) {
		this.procedimentoPrincipal = procedimentoPricipal;
	}

	public String getProcedimentoSus() {
		return procedimentoSus;
	}

	public void setProcedimentoSus(String procedimentoSus) {
		this.procedimentoSus = procedimentoSus;
	}

	public String getJustificativaReq() {
		return justificativaReq;
	}

	public void setJustificativaReq(String justificativaReq) {
		this.justificativaReq = justificativaReq;
	}

	public String getObservacoesGerias() {
		return observacoesGerais;
	}

	public void setObservacoesGerias(String observacoesGerais) {
		this.observacoesGerais = observacoesGerais;
	}

}
