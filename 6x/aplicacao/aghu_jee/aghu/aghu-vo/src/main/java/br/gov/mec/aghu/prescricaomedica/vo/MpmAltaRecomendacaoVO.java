package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MpmAltaRecomendacaoId;
import br.gov.mec.aghu.model.MpmServRecomendacaoAlta;


public class MpmAltaRecomendacaoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1992220957534668534L;
	private MpmAltaRecomendacaoId id;
	private MpmServRecomendacaoAlta servidorRecomendacaoAlta;
	private String descricaoSistema;
	private Integer pdtAtdSeq;
	private Integer pdtSeq;
	private Integer pcuAtdSeq;
	private Integer pcuSeq;
	private Integer pmdSeq;
	private Integer pmdAtdSeq;
	
	public MpmAltaRecomendacaoId getId() {
		return id;
	}
	public void setId(MpmAltaRecomendacaoId id) {
		this.id = id;
	}
	public MpmServRecomendacaoAlta getServidorRecomendacaoAlta() {
		return servidorRecomendacaoAlta;
	}
	public void setServidorRecomendacaoAlta(
			MpmServRecomendacaoAlta servidorRecomendacaoAlta) {
		this.servidorRecomendacaoAlta = servidorRecomendacaoAlta;
	}
	public String getDescricaoSistema() {
		return descricaoSistema;
	}
	public void setDescricaoSistema(String descricaoSistema) {
		this.descricaoSistema = descricaoSistema;
	}
	public Integer getPdtAtdSeq() {
		return pdtAtdSeq;
	}
	public void setPdtAtdSeq(Integer pdtAtdSeq) {
		this.pdtAtdSeq = pdtAtdSeq;
	}
	public Integer getPdtSeq() {
		return pdtSeq;
	}
	public void setPdtSeq(Integer pdtSeq) {
		this.pdtSeq = pdtSeq;
	}
	public Integer getPcuAtdSeq() {
		return pcuAtdSeq;
	}
	public void setPcuAtdSeq(Integer pcuAtdSeq) {
		this.pcuAtdSeq = pcuAtdSeq;
	}
	public Integer getPcuSeq() {
		return pcuSeq;
	}
	public void setPcuSeq(Integer pcuSeq) {
		this.pcuSeq = pcuSeq;
	}
	public Integer getPmdSeq() {
		return pmdSeq;
	}
	public void setPmdSeq(Integer pmdSeq) {
		this.pmdSeq = pmdSeq;
	}
	public Integer getPmdAtdSeq() {
		return pmdAtdSeq;
	}
	public void setPmdAtdSeq(Integer pmdAtdSeq) {
		this.pmdAtdSeq = pmdAtdSeq;
	}

}
