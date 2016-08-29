package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioParecer;


/**
 * Os dados armazenados nesse objeto representam os Itens de uma Licitação
 * 
 * @author Lilian
 */
public class PareceresAvaliacaoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1094758748075863219L;

	private Integer codigoParecerAvaliacao;
	private Integer codigoParecerMaterial;
	private Date dtCriacaoAvaliacao;
	private String loteAvaliacao;
	private DominioParecer parecerAvaliacao;
	private DominioParecer parecerAvaliacaoTecnica;
	private DominioParecer parecerAvaliacaoConsul;
	private DominioParecer parecerAvaliacaoDesemp;
	
	// GETs and SETs
	
	public Integer getCodigoParecerAvaliacao() {
		return codigoParecerAvaliacao;
	}
	public void setCodigoParecerAvaliacao(Integer codigoParecerAvaliacao) {
		this.codigoParecerAvaliacao = codigoParecerAvaliacao;
	}
	
	public Integer getCodigoParecerMaterial() {
		return codigoParecerMaterial;
	}
	public void setCodigoParecerMaterial(Integer codigoParecerMaterial) {
		this.codigoParecerMaterial = codigoParecerMaterial;
	}
	
		
	
	public Date getDtCriacaoAvaliacao() {
		return dtCriacaoAvaliacao;
	}
	public void setDtCriacaoAvaliacao(Date dtCriacaoAvaliacao) {
		this.dtCriacaoAvaliacao = dtCriacaoAvaliacao;
	}
	public String getLoteAvaliacao() {
		return loteAvaliacao;
	}
	public void setLoteAvaliacao(String loteAvaliacao) {
		this.loteAvaliacao = loteAvaliacao;
	}
	public DominioParecer getParecerAvaliacao() {
		return parecerAvaliacao;
	}
	public void setParecerAvaliacao(DominioParecer parecerAvaliacao) {
		this.parecerAvaliacao = parecerAvaliacao;
	}
	public DominioParecer getParecerAvaliacaoTecnica() {
		return parecerAvaliacaoTecnica;
	}
	public void setParecerAvaliacaoTecnica(DominioParecer parecerAvaliacaoTecnica) {
		this.parecerAvaliacaoTecnica = parecerAvaliacaoTecnica;
	}
	public DominioParecer getParecerAvaliacaoConsul() {
		return parecerAvaliacaoConsul;
	}
	public void setParecerAvaliacaoConsul(DominioParecer parecerAvaliacaoConsul) {
		this.parecerAvaliacaoConsul = parecerAvaliacaoConsul;
	}
	public DominioParecer getParecerAvaliacaoDesemp() {
		return parecerAvaliacaoDesemp;
	}
	public void setParecerAvaliacaoDesemp(DominioParecer parecerAvaliacaoDesemp) {
		this.parecerAvaliacaoDesemp = parecerAvaliacaoDesemp;
	}



	public enum Fields {
		CODIGO_PARECER_AVALIACAO("codigoParecerAvaliacao"),
		CODIGO_PARECER_MATERIAL("codigoParecerMaterial"),
		DT_CRIACAO_AVAL("dtCriacaoAvaliacao"),
		LOTE_AVAL("loteAvaliacao"),
		PARECER_AVAL("parecerAvaliacao"),
		PARECER_AVAL_TEC("parecerAvaliacaoTecnica"),
		PARECER_AVAL_CONSUL("parecerAvaliacaoConsul"),
		PARECER_AVAL_DESEMP("parecerAvaliacaoDesemp");		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}	

	
			
}
