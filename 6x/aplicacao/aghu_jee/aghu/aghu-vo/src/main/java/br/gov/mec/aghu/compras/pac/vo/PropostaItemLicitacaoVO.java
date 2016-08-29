package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PropostaItemLicitacaoVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String itemTipo9;
	private String cnpjTipo9;
	private String nomeFornecedorTipo9;
	private String valorUnitario;
	
	public String getItemTipo9() {
		return itemTipo9;
	}
	
	public void setItemTipo9(String itemTipo9) {
		this.itemTipo9 = itemTipo9;
	}
	
	public String getCnpjTipo9() {
		return cnpjTipo9;
	}
	
	public void setCnpjTipo9(String cnpjTipo9) {
		this.cnpjTipo9 = cnpjTipo9;
	}
	
	public String getNomeFornecedorTipo9() {
		return nomeFornecedorTipo9;
	}
	
	public void setNomeFornecedorTipo9(String nomeFornecedorTipo9) {
		this.nomeFornecedorTipo9 = nomeFornecedorTipo9;
	}
	
	public String getValorUnitario() {
		return valorUnitario;
	}
	
	public void setValorUnitario(String valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
}
