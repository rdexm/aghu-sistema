package br.gov.mec.aghu.suprimentos.vo;

import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;

public class ParamCalculoValorTotalAFVO {
	
	private Integer qtdadeSolicitada;
	private Double vlrUnitario;
	private Double percIpi;
	private Double percAcrescItem;
	private Double percDesc;
	private Double percAcresc;
	private Double percDescItem;
	private Double valorEfetivado;
	private DominioTipoFaseSolicitacao tipo;
	private Integer qtdeRecebida;
	
	public Integer getQtdadeSolicitada() {
		return qtdadeSolicitada;
	}
	
	public void setQtdadeSolicitada(Integer qtdadeSolicitada) {
		this.qtdadeSolicitada = qtdadeSolicitada;
	}
	
	public Double getVlrUnitario() {
		return vlrUnitario;
	}
	
	public void setVlrUnitario(Double vlrUnitario) {
		this.vlrUnitario = vlrUnitario;
	}
	
	public Double getPercIpi() {
		return percIpi;
	}
	
	public void setPercIpi(Double percIpi) {
		this.percIpi = percIpi;
	}
	
	public Double getPercAcrescItem() {
		return percAcrescItem;
	}
	
	public void setPercAcrescItem(Double percAcrescItem) {
		this.percAcrescItem = percAcrescItem;
	}
	
	public Double getPercDesc() {
		return percDesc;
	}
	
	public void setPercDesc(Double percDesc) {
		this.percDesc = percDesc;
	}
	
	public Double getPercAcresc() {
		return percAcresc;
	}
	
	public void setPercAcresc(Double percAcresc) {
		this.percAcresc = percAcresc;
	}
	
	public Double getPercDescItem() {
		return percDescItem;
	}
	
	public void setPercDescItem(Double percDescItem) {
		this.percDescItem = percDescItem;
	}
	
	public DominioTipoFaseSolicitacao getTipo() {
		return tipo;
	}
	
	public void setTipo(DominioTipoFaseSolicitacao tipo) {
		this.tipo = tipo;
	}

	public Integer getQtdeRecebida() {
		return qtdeRecebida;
	}

	public void setQtdeRecebida(Integer qtdeRecebida) {
		this.qtdeRecebida = qtdeRecebida;
	}

	public Double getValorEfetivado() {
		return valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}
}
