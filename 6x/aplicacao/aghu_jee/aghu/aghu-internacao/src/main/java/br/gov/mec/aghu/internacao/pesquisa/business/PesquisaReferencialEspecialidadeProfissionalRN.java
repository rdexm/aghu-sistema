package br.gov.mec.aghu.internacao.pesquisa.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class PesquisaReferencialEspecialidadeProfissionalRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisaReferencialEspecialidadeProfissionalRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -971505734611506231L;

	public Long pesquisarReferencialEspecialidadeProfissonalGridVOCount(AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		return getAghuFacade().pesquisarReferencialEspecialidadeProfissonalGridVOCount(especialidade);
	}

	public List<Object[]> pesquisarReferencialEspecialidadeProfissonalGridVO(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AghEspecialidades especialidade) throws ApplicationBusinessException {
		return getAghuFacade().pesquisarReferencialEspecialidadeProfissonalGridVO(firstResult, maxResult, orderProperty,
				asc, especialidade);
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

}