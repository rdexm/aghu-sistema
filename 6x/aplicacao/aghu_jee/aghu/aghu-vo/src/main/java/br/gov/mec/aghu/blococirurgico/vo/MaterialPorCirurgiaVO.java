package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class MaterialPorCirurgiaVO implements Serializable{

	private static final long serialVersionUID = -2089748027876956441L;
	
	private Integer crgSeqId;
	private Integer crgSeq;
	private Integer matCodigo;
	private String matNome;
	private Integer gmtCodigo;
	private String umdCodigo;
	private String unidadeMedidaMat;
	private String unidadeMedidaCons;
	private Double quantidade;
	private String origem;
	private Short status;
	
	
	public enum Fields {

		CRG_SEQ_ID("crgSeqId"),
		CRG_SEQ("crgSeq"),
		MAT_CODIGO("matCodigo"),
		MAT_NOME("matNome"),
		GMT_CODIGO("gmtCodigo"),
		UMD_CODIGO("umdCodigo"),
		UMD_MAT("unidadeMedidaMat"),
		UMD_CONS("unidadeMedidaCons"),
		QUANTIDADE("quantidade"),
		ORIGEM("origem"),
		STATUS("status");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getCrgSeqId() {
		return crgSeqId;
	}

	public void setCrgSeqId(Integer crgSeqId) {
		this.crgSeqId = crgSeqId;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getMatNome() {
		return matNome;
	}

	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}

	public Integer getGmtCodigo() {
		return gmtCodigo;
	}

	public void setGmtCodigo(Integer gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}

	public String getUmdCodigo() {
		return umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	public String getUnidadeMedidaMat() {
		return unidadeMedidaMat;
	}

	public void setUnidadeMedidaMat(String unidadeMedidaMat) {
		this.unidadeMedidaMat = unidadeMedidaMat;
	}

	public String getUnidadeMedidaCons() {
		return unidadeMedidaCons;
	}

	public void setUnidadeMedidaCons(String unidadeMedidaCons) {
		this.unidadeMedidaCons = unidadeMedidaCons;
	}

	public Double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}
}
