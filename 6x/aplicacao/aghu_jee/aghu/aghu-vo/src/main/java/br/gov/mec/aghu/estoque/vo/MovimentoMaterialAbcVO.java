package br.gov.mec.aghu.estoque.vo;


public class MovimentoMaterialAbcVO {
	
	private Integer codigoMaterial;
	private Double valor;
	private Boolean estorno;
	private String operacaoBasica;
	private Integer quantidade;
	
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public Boolean getEstorno() {
		return estorno;
	}
	public void setEstorno(Boolean estorno) {
		this.estorno = estorno;
	}
	public String getOperacaoBasica() {
		return operacaoBasica;
	}
	public void setOperacaoBasica(String operacaoBasica) {
		this.operacaoBasica = operacaoBasica;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
}
