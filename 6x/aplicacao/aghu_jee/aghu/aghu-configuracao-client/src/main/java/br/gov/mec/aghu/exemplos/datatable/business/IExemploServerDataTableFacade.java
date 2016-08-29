package br.gov.mec.aghu.exemplos.datatable.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.AipPacientes;

public interface IExemploServerDataTableFacade extends Serializable {

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 * @see br.gov.mec.aghu.exemplos.datatable.business.ExemploServerDataTableON#recuperarPacientes(int,
	 *      int, java.lang.String, boolean)
	 */
	public List<AipPacientes> recuperarPacientes(int firstResult,
			int maxResults, String orderProperty, boolean asc);

	/**
	 * @return
	 * @see br.gov.mec.aghu.exemplos.datatable.business.ExemploServerDataTableON#recuperarPacientesCount()
	 */
	public Long recuperarPacientesCount();

}