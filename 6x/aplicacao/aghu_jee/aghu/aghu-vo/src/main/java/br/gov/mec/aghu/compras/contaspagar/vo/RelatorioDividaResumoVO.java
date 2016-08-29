package br.gov.mec.aghu.compras.contaspagar.vo;

import java.io.Serializable;
import java.util.List;

public class RelatorioDividaResumoVO  implements Serializable {

	private static final long serialVersionUID = -4807218055514531805L;
	
	private String data;
	private Double valor;
	private Boolean atrasado;
	
	List<RelatorioDividaResumoVO> listaRelatorioAtrasados;
	List<RelatorioDividaResumoVO> listaRelatorioAVencer;
	
	public RelatorioDividaResumoVO() {
		this.valor = 0d;
	}
	
	/**
	 * @return the valor
	 */
	public Double getValor() {
		return valor;
	}
	/**
	 * @param valor the valor to set
	 */
	public void setValor(Double valor) {
		this.valor = valor;
	}
	/**
	 * @return the dataVencimento
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param dataVencimento the dataVencimento to set
	 */
	public void setData(String dataVencimento) {
		this.data = dataVencimento;
	}
	/**
	 * @return the atrasado
	 */
	public Boolean getAtrasado() {
		return atrasado;
	}
	/**
	 * @param atrasado the atrasado to set
	 */
	public void setAtrasado(Boolean atrasado) {
		this.atrasado = atrasado;
	}

	public List<RelatorioDividaResumoVO> getListaRelatorioAtrasados() {
		return listaRelatorioAtrasados;
	}

	public void setListaRelatorioAtrasados(
			List<RelatorioDividaResumoVO> listaRelatorioAtrasados) {
		this.listaRelatorioAtrasados = listaRelatorioAtrasados;
	}

	public List<RelatorioDividaResumoVO> getListaRelatorioAVencer() {
		return listaRelatorioAVencer;
	}

	public void setListaRelatorioAVencer(
			List<RelatorioDividaResumoVO> listaRelatorioAVencer) {
		this.listaRelatorioAVencer = listaRelatorioAVencer;
	}
	
}
