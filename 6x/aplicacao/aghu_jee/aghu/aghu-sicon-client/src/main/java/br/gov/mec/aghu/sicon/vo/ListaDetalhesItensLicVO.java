package br.gov.mec.aghu.sicon.vo;

public class ListaDetalhesItensLicVO {

	private Integer licitacao;
	private Short item;
	private String descTipo;
	private String unidMedida;
	private Integer qtde;
	private Double valorUnitario;
	private Boolean bItemTemSiasg; 
	private String toolTipItemSemSiasg;
	private String toolTipItemComSiasg;
	
	public Short getItem() {
		return item;
	}
	public void setItem(Short item) {
		this.item = item;
	}
	public String getDescTipo() {
		return descTipo;
	}
	public void setDescTipo(String descTipo) {
		this.descTipo = descTipo;
	}
	public String getUnidMedida() {
		return unidMedida;
	}
	public void setUnidMedida(String unidMedida) {
		this.unidMedida = unidMedida;
	}
	public Integer getQtde() {
		return qtde;
	}
	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}
	public Double getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public Boolean getbItemTemSiasg() {
		return bItemTemSiasg;
	}
	public void setbItemTemSiasg(Boolean bItemTemSiasg) {
		this.bItemTemSiasg = bItemTemSiasg;
	}
	public Integer getLicitacao() {
		return licitacao;
	}
	public void setLicitacao(Integer licitacao) {
		this.licitacao = licitacao;
	}
	public String getToolTipItemSemSiasg() {
		return toolTipItemSemSiasg;
	}
	public void setToolTipItemSemSiasg(String toolTipItemSemSiasg) {
		this.toolTipItemSemSiasg = toolTipItemSemSiasg;
	}
	public String getToolTipItemComSiasg() {
		return toolTipItemComSiasg;
	}
	public void setToolTipItemComSiasg(String toolTipItemComSiasg) {
		this.toolTipItemComSiasg = toolTipItemComSiasg;
	}
}
