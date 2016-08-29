package br.gov.mec.aghu.core.dominio;

import java.io.Serializable;

/**
 * Interface a ser implemetada pelo Domínios do sistema.
 * 
 * @author rcorvalao
 *
 */
public interface Dominio extends Serializable {
	
	/**
	 * Retorna o codigo do respectivo dominio, em geral o mesmo que vai para o
	 * BD
	 * 
	 * @return
	 */
	int getCodigo();

	/**
	 * Retorna a descrição em string do dominio. Em geral o toString de uma
	 * enumeration
	 * 
	 * @return
	 */
	String getDescricao();


}
