package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class SelecaoFornecedorParticipanteVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String cnpjTipo10;
	private String nomeFornecedorTipo10;
	private String aceitacaoPartForn;
	
	public String getCnpjTipo10() {
		return cnpjTipo10;
	}
	
	public void setCnpjTipo10(String cnpjTipo10) {
		this.cnpjTipo10 = cnpjTipo10;
	}
	
	public String getNomeFornecedorTipo10() {
		return nomeFornecedorTipo10;
	}
	
	public void setNomeFornecedorTipo10(String nomeFornecedorTipo10) {
		this.nomeFornecedorTipo10 = nomeFornecedorTipo10;
	}
	
	public String getAceitacaoPartForn() {
		return aceitacaoPartForn;
	}
	
	public void setAceitacaoPartForn(String aceitacaoPartForn) {
		this.aceitacaoPartForn = aceitacaoPartForn;
	}
}
