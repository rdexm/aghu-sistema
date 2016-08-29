package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;


public class NotaRecebimentoItensVO implements Serializable, Cloneable, Comparable<NotaRecebimentoItensVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4397411647670962725L;
	private Integer ordemTela;
	private Integer numeroNota;
	private Integer itemNumero;
	private Integer afnNumero;
	private Integer itemLicitacaoNumero;
	private Integer qtdeRecebida;
	private Integer qtdeSolicitada;
	private Double valorItem;
	private String codUniMedida;
	private String marca;
	private String nomeComercial;
	private Integer codMaterial;
	private String nomeMaterial;
	private String descricaoMaterial;
	private Integer ccAplicaCodigo;
	private String indEstocavel;
	private Integer solicitacaoCompra;
	private Short almoSeq;
	private String enderecoAlm;
	
	public Integer getOrdemTela() {
		return ordemTela;
	}
	public void setOrdemTela(Integer ordemTela) {
		this.ordemTela = ordemTela;
	}
	public Integer getNumeroNota() {
		return numeroNota;
	}
	public void setNumeroNota(Integer numeroNota) {
		this.numeroNota = numeroNota;
	}
	public Integer getItemNumero() {
		return itemNumero;
	}
	public void setItemNumero(Integer itemNumero) {
		this.itemNumero = itemNumero;
	}
	public Integer getAfnNumero() {
		return afnNumero;
	}
	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}
	public Integer getItemLicitacaoNumero() {
		return itemLicitacaoNumero;
	}
	public void setItemLicitacaoNumero(Integer itemLicitacaoNumero) {
		this.itemLicitacaoNumero = itemLicitacaoNumero;
	}
	public Integer getQtdeRecebida() {
		return qtdeRecebida;
	}
	public void setQtdeRecebida(Integer qtdeRecebida) {
		this.qtdeRecebida = qtdeRecebida;
	}
	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}
	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}
	public Double getValorItem() {
		return valorItem;
	}
	public void setValorItem(Double valorItem) {
		this.valorItem = valorItem;
	}
	public String getCodUniMedida() {
		return codUniMedida;
	}
	public void setCodUniMedida(String codUniMedida) {
		this.codUniMedida = codUniMedida;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getNomeComercial() {
		return nomeComercial;
	}
	public void setNomeComercial(String nomeComercial) {
		this.nomeComercial = nomeComercial;
	}
	public Integer getCodMaterial() {
		return codMaterial;
	}
	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
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
	public Integer getCcAplicaCodigo() {
		return ccAplicaCodigo;
	}
	public void setCcAplicaCodigo(Integer ccAplicaCodigo) {
		this.ccAplicaCodigo = ccAplicaCodigo;
	}

	public NotaRecebimentoItensVO copiar(){
		try {
			return (NotaRecebimentoItensVO) this.clone();
		} catch (CloneNotSupportedException e) {
			// engolir exceção nunca vai acontecer pois o bojeto é clonnable.
			return null;
		}
	}
	
	public int compareTo(NotaRecebimentoItensVO other) {
		int result = this.getItemLicitacaoNumero().compareTo(other.getItemLicitacaoNumero());
        return result;
	}
	
	public String getIndEstocavel() {
		return indEstocavel;
	}
	public void setIndEstocavel(String indEstocavel) {
		this.indEstocavel = indEstocavel;
	}
	public Integer getSolicitacaoCompra() {
		return solicitacaoCompra;
	}
	public void setSolicitacaoCompra(Integer solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}
	public Short getAlmoSeq() {
		return almoSeq;
	}
	public void setAlmoSeq(Short almoSeq) {
		this.almoSeq = almoSeq;
	}
	public String getEnderecoAlm() {
		return enderecoAlm;
	}
	public void setEnderecoAlm(String enderecoAlm) {
		this.enderecoAlm = enderecoAlm;
	}
}