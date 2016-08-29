package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;

public class AfPendenteCompradorVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -442672208806976842L;
	
	private Integer codigoGrupoMaterial;
	private String compradorU;
	private String comprador;
	private String gestorU;
	private String gestor;
	private Integer afNumero;
	private Short afComplemento;
	private Date geradaEm;
	private Date dtPrevEntrega;
	private Date dtVencContrato;
	private String modlLict;
	private DominioModalidadeEmpenho modlEmp;
	private Boolean itemContrato;
	private Short item;
	private String sit;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private String unid;
	private Integer qtdeSolic;
	private Integer qtdeRecb;
	private Double custoUnit;
	private Double valorEfet;
	private Double valorSaldo;
	private Double valorItem;
	private String fornecedor;
	
	public AfPendenteCompradorVO () {}
	
	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}
	public String getCompradorU() {
		return compradorU;
	}
	public String getComprador() {
		return comprador;
	}
	public String getGestorU() {
		return gestorU;
	}
	public String getGestor() {
		return gestor;
	}
	public Integer getAfNumero() {
		return afNumero;
	}
	public Short getAfComplemento() {
		return afComplemento;
	}
	public Date getGeradaEm() {
		return geradaEm;
	}
	public Date getDtPrevEntrega() {
		return dtPrevEntrega;
	}
	public Date getDtVencContrato() {
		return dtVencContrato;
	}
	public String getModlLict() {
		return modlLict;
	}
	public DominioModalidadeEmpenho getModlEmp() {
		return modlEmp;
	}
	public Boolean getItemContrato() {
		return itemContrato;
	}
	public Short getItem() {
		return item;
	}
	public String getSit() {
		return sit;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public String getUnid() {
		return unid;
	}
	public Integer getQtdeSolic() {
		return qtdeSolic;
	}
	public Integer getQtdeRecb() {
		return qtdeRecb;
	}
	public Double getCustoUnit() {
		return custoUnit;
	}
	public Double getValorEfet() {
		return valorEfet;
	}
	public Double getValorSaldo() {
		return valorSaldo;
	}
	public Double getValorItem() {
		return valorItem;
	}
	public String getFornecedor() {
		return fornecedor;
	}
	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}
	public void setCompradorU(String compradorU) {
		this.compradorU = compradorU;
	}
	public void setComprador(String comprador) {
		this.comprador = comprador;
	}
	public void setGestorU(String gestorU) {
		this.gestorU = gestorU;
	}
	public void setGestor(String gestor) {
		this.gestor = gestor;
	}
	public void setAfNumero(Integer afNumero) {
		this.afNumero = afNumero;
	}
	public void setAfComplemento(Short afComplemento) {
		this.afComplemento = afComplemento;
	}
	public void setGeradaEm(Date geradaEm) {
		this.geradaEm = geradaEm;
	}
	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}
	public void setDtVencContrato(Date dtVencContrato) {
		this.dtVencContrato = dtVencContrato;
	}
	public void setModlLict(String modlLict) {
		this.modlLict = modlLict;
	}
	public void setModlEmp(DominioModalidadeEmpenho modlEmp) {
		this.modlEmp = modlEmp;
	}
	public void setItemContrato(Boolean itemContrato) {
		this.itemContrato = itemContrato;
	}
	public void setItem(Short item) {
		this.item = item;
	}
	public void setSit(String sit) {
		this.sit = sit;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public void setUnid(String unid) {
		this.unid = unid;
	}
	public void setQtdeSolic(Integer qtdeSolic) {
		this.qtdeSolic = qtdeSolic;
	}
	public void setQtdeRecb(Integer qtdeRecb) {
		this.qtdeRecb = qtdeRecb;
	}
	public void setCustoUnit(Double custoUnit) {
		this.custoUnit = custoUnit;
	}
	public void setValorEfet(Double valorEfet) {
		this.valorEfet = valorEfet;
	}
	public void setValorSaldo(Double valorSaldo) {
		this.valorSaldo = valorSaldo;
	}
	public void setValorItem(Double valorItem) {
		this.valorItem = valorItem;
	}
	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	public enum Fields {
		GRUPO("codigoGrupoMaterial"),
		COMPRADOR("compradorU"),
		GESTOR("gestorU");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
