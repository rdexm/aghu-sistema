package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;

/**
 * Os dados armazenados nesse objeto representam as Prescrições por Unidade
 * 
 * @author Lilian
 */

public class PrescricaoUnidadeVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1906437550474439327L;

	private String andarAla;
	
	private String validade;
	
	private String conf;
	
	private String situacaoConfPresc;
	
	private String localizacao;
	
	private String prontuario;
	
	private Integer seqPrescricao;
	
	private String dataHora;
	
	private String obsrvacao;	
	
	private Boolean indPmNaoEletronica;
	
		
	// GETs and SETs

	public String getAndarAla() {
		return andarAla;
	}

	public void setAndarAla(String andarAla) {
		this.andarAla = andarAla;
	}

	public String getValidade() {
		return validade;
	}

	public void setValidade(String validade) {
		this.validade = validade;
	}

	public String getConf() {
		return conf;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}

	public String getSituacaoConfPresc() {
		return situacaoConfPresc;
	}

	public void setSituacaoConfPresc(String situacaoConfPresc) {
		this.situacaoConfPresc = situacaoConfPresc;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getSeqPrescricao() {
		return seqPrescricao;
	}

	public void setSeqPrescricao(Integer seqPrescricao) {
		this.seqPrescricao = seqPrescricao;
	}

	public String getDataHora() {
		return dataHora;
	}

	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}

	public String getObsrvacao() {
		return obsrvacao;
	}

	public void setObsrvacao(String obsrvacao) {
		this.obsrvacao = obsrvacao;
	}

	public Boolean getIndPmNaoEletronica() {
		return indPmNaoEletronica;
	}

	public void setIndPmNaoEletronica(Boolean indPmNaoEletronica) {
		this.indPmNaoEletronica = indPmNaoEletronica;
	}
}