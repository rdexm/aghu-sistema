package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.math.BigDecimal;

public class ItensRecebimentoVO {

	private Integer codigoMaterial;
	private String nomeMaterial;
	private String descricaoMaterial;
	private String unidade;
	private Double valorUnitario;
	private Integer qtdEntregue;
	private BigDecimal valorTotal;
	private String nomeMarcaComercial;
	private Integer idUnico;
	private Integer codigoMarcaComercial;
	
	
	public ItensRecebimentoVO() {
		super();
	}
	
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public Double getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public Integer getQtdEntregue() {
		return qtdEntregue;
	}
	public void setQtdEntregue(Integer qtdEntregue) {
		this.qtdEntregue = qtdEntregue;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	public String getNomeMarcaComercial() {
		return nomeMarcaComercial;
	}
	public void setNomeMarcaComercial(String nomeMarcaComercial) {
		this.nomeMarcaComercial = nomeMarcaComercial;
	}
	public Integer getIdUnico() {
		return idUnico;
	}
	public void setIdUnico(Integer idUnico) {
		this.idUnico = idUnico;
	}

	public Integer getCodigoMarcaComercial() {
		return codigoMarcaComercial;
	}

	public void setCodigoMarcaComercial(Integer codigoMarcaComercial) {
		this.codigoMarcaComercial = codigoMarcaComercial;
	}
}
