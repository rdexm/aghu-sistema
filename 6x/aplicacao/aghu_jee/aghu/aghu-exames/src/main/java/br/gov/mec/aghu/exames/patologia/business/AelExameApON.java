package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AelExameApON extends BaseBusiness {

	@EJB
	private AelExameApRN aelExameApRN;

	private static final Log LOG = LogFactory.getLog(AelExameApON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExameApDAO aelExameApDAO;

	private static final long serialVersionUID = -2025818928671028609L;

	public void persistirAelExameAp(final AelExameAp aelExameAp, final AelExameAp aelExameApOld) throws BaseException {
		final boolean novo = aelExameAp.getSeq() == null;
		if (novo) {
			this.getAelExameApRN().inserirAelExameApRN(aelExameAp);
		} 
		else {
			this.getAelExameApRN().atualizarAelExameApRN(aelExameAp, aelExameApOld);
		}
	}
	
	public void persistirAelExameAp(final AelExameAp aelExameAp) throws BaseException {
		final boolean novo = aelExameAp.getSeq() == null;
		if (novo) {
			this.getAelExameApRN().inserirAelExameApRN(aelExameAp);
		} 
		else {
			this.getAelExameApRN().atualizarAelExameApRN(aelExameAp);
		}
	}

	public void atualizarImpressao(final Long luxSeq) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		exameAp.setDthrImpressao(new Date());
		exameAp.setIndImpresso(Boolean.TRUE);
		exameAp.setServidorRespImpresso(servidorLogado);
		
		persistirAelExameAp(exameAp);

	}
	
	protected AelExameApRN getAelExameApRN() {
		return aelExameApRN;
	}

	protected AelExameApDAO getAelExameApDAO() {
		return aelExameApDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
