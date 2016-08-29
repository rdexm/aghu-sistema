package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.List;


/**
 * 
 * @author fsantos
 * 
 */
public class RelatorioCotacaoPrecoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1094758748075863219L;

	private String numeroCotacao;
	private List<CotacaoPrecoVO> listaCotacoes;
	private String usuario;
	private String hospitalEnderecoCidadeData;
	
	public String getNumeroCotacao() {
		return numeroCotacao;
	}
	public void setNumeroCotacao(String numeroCotacao) {
		this.numeroCotacao = numeroCotacao;
	}
	public List<CotacaoPrecoVO> getListaCotacoes() {
		return listaCotacoes;
	}
	public void setListaCotacoes(List<CotacaoPrecoVO> listaCotacoes) {
		this.listaCotacoes = listaCotacoes;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getHospitalEnderecoCidadeData() {
		return hospitalEnderecoCidadeData;
	}
	public void setHospitalEnderecoCidadeData(String hospitalEnderecoCidadeData) {
		this.hospitalEnderecoCidadeData = hospitalEnderecoCidadeData;
	}


}