package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;

public class SolicitacaoHemoterapiaNaInternacaoVO implements java.io.Serializable {

	private static final long serialVersionUID = -3705279198012700893L;
	
	private Integer sheSeq;
	private Integer atdSeq;
	private Date criadoEm;
	private String csaCodigo;
	private String pheCodigo;
	private Short qtdeAplicacoes;
	private Byte qtdeUnidades;
	private Short qtdeMl;
	private Integer phiSeq;
	private Integer ocvSeq;
	
	public SolicitacaoHemoterapiaNaInternacaoVO(){
	}
	
	public Integer getSheSeq() {
		return sheSeq;
	}

	public void setSheSeq(Integer sheSeq) {
		this.sheSeq = sheSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getCsaCodigo() {
		return csaCodigo;
	}

	public void setCsaCodigo(String csaCodigo) {
		this.csaCodigo = csaCodigo;
	}

	public String getPheCodigo() {
		return pheCodigo;
	}

	public void setPheCodigo(String pheCodigo) {
		this.pheCodigo = pheCodigo;
	}

	public Short getQtdeAplicacoes() {
		return qtdeAplicacoes;
	}

	public void setQtdeAplicacoes(Short qtdeAplicacoes) {
		this.qtdeAplicacoes = qtdeAplicacoes;
	}

	public Byte getQtdeUnidades() {
		return qtdeUnidades;
	}

	public void setQtdeUnidades(Byte qtdeUnidades) {
		this.qtdeUnidades = qtdeUnidades;
	}

	public Short getQtdeMl() {
		return qtdeMl;
	}

	public void setQtdeMl(Short qtdeMl) {
		this.qtdeMl = qtdeMl;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public enum Fields{
		
		SHE_SEQ("sheSeq"),
		SHE_ATD_SEQ("atdSeq"),
		SHE_CRIADO_EM("criadoEm"),
		ITEM_SHE_CSA_CODIGO("csaCodigo"),
		ITEM_SHE_PHE_CODIGO("pheCodigo"),
		ITEM_SHE_QTDE_APLICACOES("qtdeAplicacoes"),
		ITEM_SHE_QTDE_UNIDADES("qtdeUnidades"),
		ITEM_SHE_QTDE_ML("qtdeMl"),
		PHI_SEQ("phiSeq"),
		OCV_SEQ("ocvSeq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
