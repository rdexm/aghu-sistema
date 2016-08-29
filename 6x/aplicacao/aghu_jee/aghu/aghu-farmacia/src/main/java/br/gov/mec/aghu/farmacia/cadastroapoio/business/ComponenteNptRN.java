package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.model.AfaComponenteNpt;

/**
 * 
 * Triggers de:<br/>
 * @ORADB: <code>AFA_COMPONENTE_NPTS</code>
 * Changelog:
 * 	2011.02.04 -- gandriotti:
 * 		Classe oca que eh usada pela {@link MedicamentoRN}
 * @author gandriotti
 *
 */
@Stateless
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
public class ComponenteNptRN extends AbstractAGHUCrudRn<AfaComponenteNpt> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1495822150449226504L;

	private static final Log LOG = LogFactory.getLog(ComponenteNptRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}		
	
	/*
	 * Implementar aqui as regras de negocio da entidade acima
	 */
}
