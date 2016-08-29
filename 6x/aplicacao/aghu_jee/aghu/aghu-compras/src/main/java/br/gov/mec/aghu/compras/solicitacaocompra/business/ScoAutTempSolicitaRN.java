package br.gov.mec.aghu.compras.solicitacaocompra.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAutTempSolicitaDAO;
import br.gov.mec.aghu.model.ScoAutTempSolicita;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ScoAutTempSolicitaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoAutTempSolicitaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private ScoAutTempSolicitaDAO scoAutTempSolicitaDAO;

	private static final long serialVersionUID = -6943869303983504687L;

	public void persistir(ScoAutTempSolicita autTempSolicita) throws ApplicationBusinessException {
		this.getScoAutTempSolicitaDAO().persistir(autTempSolicita);
	}

	protected ScoAutTempSolicitaDAO getScoAutTempSolicitaDAO() {
		return scoAutTempSolicitaDAO;
	}

	public void atualizar(ScoAutTempSolicita autTempSolicita) throws ApplicationBusinessException {
		this.getScoAutTempSolicitaDAO().merge(autTempSolicita);
	}

}
