package br.gov.mec.aghu.core.action;

import java.io.Serializable;
import java.util.List;

/**
 * Interface para inserir consultas dinâmicas no componentes serverDataTable
 * substitui: ActionPaginator
 * substitui: MECPaginatorController
 * 
 * @author Cristiano Quadros
 *
 */
public interface ActionPaginator extends Serializable {
	
	/**
	 * Método que retorna os dados da pesquisa já paginados. Deve ser protected
	 * para não ser chamado fora do ciclo de vida da classe.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public <T> List<T> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);
	
	Long recuperarCount();
}
