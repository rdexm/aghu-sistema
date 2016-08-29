package br.gov.mec.aghu.sig.custos.vo;

public class EntradaProducaoObjetoCustoVO implements java.io.Serializable {

	private static final long serialVersionUID = 5867281234640453342L;

	private Integer centroCusto;
	private Integer centroProducao;
	private Double valor;
	private boolean isLeituraCorreta = true;
	private String erroLeitura;
	private Integer nroLinha;
	
	
	public EntradaProducaoObjetoCustoVO(){
		this.setErroLeitura("");
	}
	
	public Integer getCentroCusto() {
		return centroCusto;
	}
	public void setCentroCusto(Integer centroCusto) {
		this.centroCusto = centroCusto;
	}
	public Integer getCentroProducao() {
		return centroProducao;
	}
	public void setCentroProducao(Integer centroProducao) {
		this.centroProducao = centroProducao;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public boolean isLeituraCorreta() {
		return isLeituraCorreta;
	}

	public void setLeituraCorreta(boolean isLeituraCorreta) {
		this.isLeituraCorreta = isLeituraCorreta;
	}

	public String getErroLeitura() {
		return erroLeitura;
	}

	public void setErroLeitura(String erroLeitura) {
		this.erroLeitura = erroLeitura;
	}
	public Integer getNroLinha() {
		return nroLinha;
	}
	public void setNroLinha(Integer nroLinha) {
		this.nroLinha = ++nroLinha;
	}
}
