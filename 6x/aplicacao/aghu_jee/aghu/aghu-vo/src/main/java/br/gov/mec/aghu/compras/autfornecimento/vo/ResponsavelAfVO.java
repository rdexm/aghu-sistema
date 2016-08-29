package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.util.Date;

import br.gov.mec.aghu.model.RapServidores;

/**
 * Responsáveis de AF
 * 
 * @author mlcruz
 */
public class ResponsavelAfVO {
	/** Operação */
	private String operacao;
	
	/** Data */
	private Date data;
	
	/** Usuário */
	private RapServidores usuario;

	// Getters
	
	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public RapServidores getUsuario() {
		return usuario;
	}

	public void setUsuario(RapServidores usuario) {
		this.usuario = usuario;
	}
}