package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

public class SceItemEntrSaidSemLicitacaoVO implements Serializable {
	
	private static final long serialVersionUID = -2751781330992521055L;
	private Integer seq;
	private Integer eslSeq;
	private Integer codigoMaterial;
	private String codigoUnidadeMedida;
	private Double valorItem;
	private Integer quantidadeItem;
	private Short seqAlmoxarifado;
	private String descricaoMarcaComercial;
	private Integer numeroSolicitacaoCompra;
	
	public Double calculaValorItem(){
		if(quantidadeItem > 0){
			return valorItem * quantidadeItem;
		}
		return 0D;
	}
	
	public Integer getEslSeq() {
		return eslSeq;
	}
	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}
	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}
	public Double getValorItem() {
		return valorItem;
	}
	public void setValorItem(Double valorItem) {
		this.valorItem = valorItem;
	}
	public Integer getQuantidadeItem() {
		return quantidadeItem;
	}
	public void setQuantidadeItem(Integer quantidadeItem) {
		this.quantidadeItem = quantidadeItem;
	}
	public Short getSeqAlmoxarifado() {
		return seqAlmoxarifado;
	}
	public void setSeqAlmoxarifado(Short seqAlmoxarifado) {
		this.seqAlmoxarifado = seqAlmoxarifado;
	}
	public String getDescricaoMarcaComercial() {
		return descricaoMarcaComercial;
	}
	public void setDescricaoMarcaComercial(String descricaoMarcaComercial) {
		this.descricaoMarcaComercial = descricaoMarcaComercial;
	}
	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}
	public void setNumeroSolicitacaoCompra(Integer numeroSolicitacaoCompra) {
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
	}
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public enum Fields {
		SEQ("seq"),
		ESL_SEQ("eslSeq"), //
		CODIGO_MATERIAL("codigoMaterial"), //
		CODIGO_UNIDADE_MEDIDA("codigoUnidadeMedida"), //
		VALOR_ITEM("valorItem"),
		QUANTIDADE_ITEM("quantidadeItem"),
		SEQ_ALMOXARIFADO("seqAlmoxarifado"),
		DESCRICAO_MCM("descricaoMarcaComercial"),
		NUMERO_SLC("numeroSolicitacaoCompra");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}
