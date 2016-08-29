package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

public class CotacaoPrecoVO implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8745679234377844065L;
	
	private Integer seq;
	private Integer codigoMaterial;
	private Integer solicitacaoCompra;
	private String material;
	private String unidade;
	private Long quantidade;
	private String materialLabel;
	private String materialTruncado;
	private String materialDescricao;
	private String unidadeDescricao;
	
	public CotacaoPrecoVO(){
		
	}
	
	public enum Fields {
		SEQ("seq"),
		CODIGO_MATERIAL("codigoMaterial"),
		SOLICITACAO_COMPRA("solicitacaoCompra"),
		MATERIAL("material"),
		UNIDADE("unidade"),
		QUANTIDADE("quantidade"),
		MATERIAL_LABEL("materialLabel"),
		MATERIAL_TRUNCADO("materialTruncado"),
		MATERIAL_DESCRICAO("materialDescricao"),
		UNIDADE_DESCRICAO("unidadeDescricao");
	
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
		
	}
	
	public Integer getSolicitacaoCompra() {
		return solicitacaoCompra;
	}
	public void setSolicitacaoCompra(Integer solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}

	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setMaterialLabel(String materialLabel) {
		this.materialLabel = materialLabel;
	}
	public String getMaterialLabel() {
		return materialLabel;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setMaterialTruncado(String materialTruncado) {
		this.materialTruncado = materialTruncado;
	}
	public String getMaterialTruncado() {
		return materialTruncado;
	}
	public void setMaterialDescricao(String materialDescricao) {
		this.materialDescricao = materialDescricao;
	}
	public String getMaterialDescricao() {
		return materialDescricao;
	}
	public void setUnidadeDescricao(String unidadeDescricao) {
		this.unidadeDescricao = unidadeDescricao;
	}
	public String getUnidadeDescricao() {
		return unidadeDescricao;
	}
}
