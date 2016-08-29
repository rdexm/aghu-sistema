package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PropostaLicitacaoVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String cnpjTipo7;
	private String nomeTipo7;
	private String bairroTipo7;
	private String entregaDaProposta;
	private String dddContato;
	private String numeroTelefoneContato;
	private String telefone;
	private String contato;
	private String email;
	
	
	public String getCnpjTipo7() {
		return cnpjTipo7;
	}
	public void setCnpjTipo7(String cnpjTipo7) {
		this.cnpjTipo7 = cnpjTipo7;
	}
	public String getNomeTipo7() {
		return nomeTipo7;
	}
	public void setNomeTipo7(String nomeTipo7) {
		this.nomeTipo7 = nomeTipo7;
	}
	public String getBairroTipo7() {
		return bairroTipo7;
	}
	public void setBairroTipo7(String bairroTipo7) {
		this.bairroTipo7 = bairroTipo7;
	}
	public String getEntregaDaProposta() {
		return entregaDaProposta;
	}
	public void setEntregaDaProposta(String entregaDaProposta) {
		this.entregaDaProposta = entregaDaProposta;
	}
	public String getDddContato() {
		return dddContato;
	}
	public void setDddContato(String dddContato) {
		this.dddContato = dddContato;
	}
	public String getNumeroTelefoneContato() {
		return numeroTelefoneContato;
	}
	public void setNumeroTelefoneContato(String numeroTelefoneContato) {
		this.numeroTelefoneContato = numeroTelefoneContato;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getContato() {
		return contato;
	}
	public void setContato(String contato) {
		this.contato = contato;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
