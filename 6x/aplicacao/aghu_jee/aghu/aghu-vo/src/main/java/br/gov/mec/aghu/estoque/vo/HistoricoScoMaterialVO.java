package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

public class HistoricoScoMaterialVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -451387831500810691L;
	private String valorAtual;
	private String valorAnterior;
	private String campo;

	public HistoricoScoMaterialVO() {
	}

	public HistoricoScoMaterialVO(String valorAtual, String valorAnterior,
			String campo) {
		super();
		this.valorAtual = valorAtual;
		this.valorAnterior = valorAnterior;
		this.campo = campo;
	}

	public void setValorAtual(String valorAtual) {
		this.valorAtual = valorAtual;
	}

	public String getValorAtual() {
		return valorAtual;
	}

	public void setValorAnterior(String valorAnterior) {
		this.valorAnterior = valorAnterior;
	}

	public String getValorAnterior() {
		return valorAnterior;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public String getCampo() {
		return campo;
	}

	@Override
	public String toString() {
		return "HistoricoScoMaterialVO [valorAtual=" + valorAtual
				+ ", valorAnterior=" + valorAnterior + ", campo=" + campo + "]";
	}

}
