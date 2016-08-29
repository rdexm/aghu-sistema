package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExtratoExameApDAO;
import br.gov.mec.aghu.model.AelExtratoExameAp;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class AelExtratoExameApRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelExtratoExameApRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExtratoExameApDAO aelExtratoExameApDAO;

	private static final long serialVersionUID = 4879378548867391256L;

	public void inserirAelExtratoExameAp(final AelExtratoExameAp aelExtratoExameAp) throws ApplicationBusinessException {
		final AelExtratoExameApDAO dao = getAelExtratoExameApDAO();
		this.executarAntesInserirAelExtatoExameAp(aelExtratoExameAp);
		dao.persistir(aelExtratoExameAp);
	}
	
	/**
	 * ORADB Trigger AELT_LU5_BRI
	 * 
	 * @param aelExtratoExameAp
	 * @throws ApplicationBusinessException  
	 */
	protected void executarAntesInserirAelExtatoExameAp(
			AelExtratoExameAp aelExtratoExameAp) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelExtratoExameAp.setCriadoEm(new Date());
		aelExtratoExameAp.setRapServidores(servidorLogado);
		
	}

	protected AelExtratoExameApDAO getAelExtratoExameApDAO() {
		return aelExtratoExameApDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
