package br.gov.mec.aghu.blococirurgico.vo;

import java.math.BigDecimal;

public class MbcItensRequisicaoOpmesVO {
	private Short seq;
	private Short phoSeq;
	private Integer iphSeq;
	private String itemMatDescricao;
	private String indCompativel;
	private String requerido;
	private Integer quantidadeSolicitada;
	private Integer quantidadeAutorizadaHospital;
	private BigDecimal valorUnitarioIph;
	private Integer reqOrder;
	private String descricaoIncompativel;
	
	public MbcItensRequisicaoOpmesVO(){}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public String getItemMatDescricao() {
		return itemMatDescricao;
	}

	public void setItemMatDescricao(String itemMatDescricao) {
		this.itemMatDescricao = itemMatDescricao;
	}

	public String getIndCompativel() {
		return indCompativel;
	}

	public void setIndCompativel(String indCompativel) {
		this.indCompativel = indCompativel;
	}

	public String getRequerido() {
		return requerido;
	}
	
	public Boolean isRequerido(){
		return requerido != null && requerido.equals("S") ? Boolean.TRUE : Boolean.FALSE;
	}
	
	public Boolean isCompativel(){
		return indCompativel != null && indCompativel.equals("S") ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setRequerido(String requerido) {
		this.requerido = requerido;
	}

	public Integer getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}

	public void setQuantidadeSolicitada(Integer quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}

	public Integer getQuantidadeAutorizadaHospital() {
		return quantidadeAutorizadaHospital;
	}

	public void setQuantidadeAutorizadaHospital(Integer quantidadeAutorizadaHospital) {
		this.quantidadeAutorizadaHospital = quantidadeAutorizadaHospital;
	}

	public BigDecimal getValorUnitarioIph() {
		return valorUnitarioIph;
	}

	public void setValorUnitarioIph(BigDecimal valorUnitarioIph) {
		this.valorUnitarioIph = valorUnitarioIph;
	}

	public Integer getReqOrder() {
		return reqOrder;
	}

	public void setReqOrder(Integer reqOrder) {
		this.reqOrder = reqOrder;
	}

	public void setDescricaoIncompativel(String descricaoIncompativel) {
		this.descricaoIncompativel = descricaoIncompativel;
	}

	public String getDescricaoIncompativel() {
		return descricaoIncompativel;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	
}
