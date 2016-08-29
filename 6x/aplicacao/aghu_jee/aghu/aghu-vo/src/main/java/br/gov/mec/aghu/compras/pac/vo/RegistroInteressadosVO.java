package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class RegistroInteressadosVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String cnpjTipo15;
	private String nomeFornecedorTipo15;
	private String registroDeInteresse;
	private String tipodeInteresse;
	
	
	public String getCnpjTipo15() {
		return cnpjTipo15;
	}
	public void setCnpjTipo15(String cnpjTipo15) {
		this.cnpjTipo15 = cnpjTipo15;
	}
	public String getNomeFornecedorTipo15() {
		return nomeFornecedorTipo15;
	}
	public void setNomeFornecedorTipo15(String nomeFornecedorTipo15) {
		this.nomeFornecedorTipo15 = nomeFornecedorTipo15;
	}
	public String getRegistroDeInteresse() {
		return registroDeInteresse;
	}
	public void setRegistroDeInteresse(String registroDeInteresse) {
		this.registroDeInteresse = registroDeInteresse;
	}
	public String getTipodeInteresse() {
		return tipodeInteresse;
	}
	public void setTipodeInteresse(String tipodeInteresse) {
		this.tipodeInteresse = tipodeInteresse;
	}
}
