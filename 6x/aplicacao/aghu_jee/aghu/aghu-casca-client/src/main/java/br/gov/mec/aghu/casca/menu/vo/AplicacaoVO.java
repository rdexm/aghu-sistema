/*
 * AplicacaoVO.java
 * Copyright (c) Ministério da Educação - MEC.
 *
 * Este software é confidencial e propriedade do Ministério da Educação - MEC.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem expressa autorização do MEC.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.mec.aghu.casca.menu.vo;

/**
 * Objeto que usuário nas tranferências JSON para informar quais são as aplicações que o usuário tem acesso. 
 * 
 * @author manoelsantos
 * @version 1.0
 */
public class AplicacaoVO {

	private Integer id;
	private String nome;
	private String descricao;
	private String urlAcesso;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}
	/**
	 * @param _id the id to set
	 */
	public void setId(Integer _id) {
		this.id = _id;
	}
	/**
	 * @return the nome
	 */
	public String getNome() {
		return this.nome;
	}
	/**
	 * @param _nome the nome to set
	 */
	public void setNome(String _nome) {
		this.nome = _nome;
	}
	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return this.descricao;
	}
	/**
	 * @param _descricao the descricao to set
	 */
	public void setDescricao(String _descricao) {
		this.descricao = _descricao;
	}
	/**
	 * @return the urlAcesso
	 */
	public String getUrlAcesso() {
		return this.urlAcesso;
	}
	/**
	 * @param _urlAcesso the urlAcesso to set
	 */
	public void setUrlAcesso(String _urlAcesso) {
		this.urlAcesso = _urlAcesso;
	}

	
}