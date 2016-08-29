package br.gov.mec.aghu.exemplos.datatable.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ExemploServerDataTableON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ExemploServerDataTableON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPacienteFacade pacienteFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1675677500247021589L;

	public List<AipPacientes> recuperarPacientes(int firstResult, int maxResults, String orderProperty, boolean asc) {
		return this.getPacienteFacade().recuperarPacientes(firstResult, maxResults, orderProperty, asc);
	}

	public Long recuperarPacientesCount() {
		return this.getPacienteFacade().recuperarPacientesCount();
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

}
