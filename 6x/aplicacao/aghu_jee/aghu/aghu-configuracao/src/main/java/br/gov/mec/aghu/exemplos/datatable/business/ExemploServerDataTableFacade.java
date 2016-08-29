package br.gov.mec.aghu.exemplos.datatable.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.model.AipPacientes;



@Stateless
public class ExemploServerDataTableFacade extends BaseFacade implements IExemploServerDataTableFacade {

@EJB
private ExemploServerDataTableON exemploServerDataTableON;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1369053120280048344L;

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 * @see br.gov.mec.aghu.exemplos.datatable.business.ExemploServerDataTableON#recuperarPacientes(int,
	 *      int, java.lang.String, boolean)
	 */
	@Override
	public List<AipPacientes> recuperarPacientes(int firstResult, int maxResults, String orderProperty, boolean asc) {
		return this.getExemploServerDataTableON().recuperarPacientes(firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * @return
	 * @see br.gov.mec.aghu.exemplos.datatable.business.ExemploServerDataTableON#recuperarPacientesCount()
	 */
	@Override
	public Long recuperarPacientesCount() {
		return this.getExemploServerDataTableON().recuperarPacientesCount();
	}

	protected ExemploServerDataTableON getExemploServerDataTableON() {
		return exemploServerDataTableON;
	}

}
