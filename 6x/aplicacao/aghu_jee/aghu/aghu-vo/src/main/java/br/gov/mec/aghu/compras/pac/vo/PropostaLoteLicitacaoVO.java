package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PropostaLoteLicitacaoVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String cnpjTipo8;
	private String nomeFornecedorTipo8;
	private String dataDesclassificacao;
	private String motivoDesclassificacao;
	
	
	public String getCnpjTipo8() {
		return cnpjTipo8;
	}
	public void setCnpjTipo8(String cnpjTipo8) {
		this.cnpjTipo8 = cnpjTipo8;
	}
	public String getNomeFornecedorTipo8() {
		return nomeFornecedorTipo8;
	}
	public void setNomeFornecedorTipo8(String nomeFornecedorTipo8) {
		this.nomeFornecedorTipo8 = nomeFornecedorTipo8;
	}
	public String getDataDesclassificacao() {
		return dataDesclassificacao;
	}
	public void setDataDesclassificacao(String dataDesclassificacao) {
		this.dataDesclassificacao = dataDesclassificacao;
	}
	public String getMotivoDesclassificacao() {
		return motivoDesclassificacao;
	}
	public void setMotivoDesclassificacao(String motivoDesclassificacao) {
		this.motivoDesclassificacao = motivoDesclassificacao;
	}	
}
