package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class RegistroLancesLoteDisputaPregaoVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String loteTipo12;
	private String numeroItem;
	private String cnpjTipo12;
	private String nomeFornecedorTipo12;
	private String entregaLance;
	private String valor;
	private String lanceCancelado;
	
	
	public String getLoteTipo12() {
		return loteTipo12;
	}
	public void setLoteTipo12(String loteTipo12) {
		this.loteTipo12 = loteTipo12;
	}
	public String getNumeroItem() {
		return numeroItem;
	}
	public void setNumeroItem(String numeroItem) {
		this.numeroItem = numeroItem;
	}
	public String getCnpjTipo12() {
		return cnpjTipo12;
	}
	public void setCnpjTipo12(String cnpjTipo12) {
		this.cnpjTipo12 = cnpjTipo12;
	}
	public String getNomeFornecedorTipo12() {
		return nomeFornecedorTipo12;
	}
	public void setNomeFornecedorTipo12(String nomeFornecedorTipo12) {
		this.nomeFornecedorTipo12 = nomeFornecedorTipo12;
	}
	public String getEntregaLance() {
		return entregaLance;
	}
	public void setEntregaLance(String entregaLance) {
		this.entregaLance = entregaLance;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getLanceCancelado() {
		return lanceCancelado;
	}
	public void setLanceCancelado(String lanceCancelado) {
		this.lanceCancelado = lanceCancelado;
	}
	
}
