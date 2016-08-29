/**
 * 
 */
package br.gov.mec.aghu.model.interfaces;

/**
 * Classe para indicar ou marcar a existencia de metodos de Prescricao Medica.
 * 
 * @author rcorvalao
 */
public interface IItemPrescricaoMedica {
	
	/**
	 * Regras para montar a descrição dos itens de Prescricao Medica
	 * 
	 * rcorvalao
	 * 28/09/2010
	 * @return
	 */
	String getDescricaoFormatada();
	
}
