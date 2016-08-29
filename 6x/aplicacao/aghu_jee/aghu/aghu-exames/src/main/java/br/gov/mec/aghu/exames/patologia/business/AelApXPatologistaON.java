package br.gov.mec.aghu.exames.patologia.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelApXPatologistaDAO;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelApXPatologistaId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AelApXPatologistaON extends BaseBusiness {
	
	@EJB
	private AelApXPatologistaRN aelApXPatologistaRN;
	
	private static final Log LOG = LogFactory.getLog(AelApXPatologistaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AelApXPatologistaDAO aelApXPatologistaDAO;

	private static final long serialVersionUID = -2025818928671028609L;

	public void persistirAelApXPatologista(final AelApXPatologista aelApXPatologista) throws BaseException {
		final boolean novo = aelApXPatologista.getCriadoEm() == null;
		try {
			if (novo) {
				this.getAelApXPatologistaRN().inserirAelApXPatologistaRN(aelApXPatologista);
			} else {
				this.getAelApXPatologistaRN().atulizarAelApXPatologistaRN(aelApXPatologista, this.getAelApXPatologistaDAO().obterOriginal(aelApXPatologista));
			}
			
		} catch (final ApplicationBusinessException e) {
			if (novo) {
				aelApXPatologista.setCriadoEm(null);
			}
			throw e;
		}
	}

	public void excluirAelApXPatologistaPorPatologista(final Integer seqPatologista, final Long seqAnatomoPatologico) throws ApplicationBusinessException {
		this.getAelApXPatologistaRN().excluirAelApXPatologista(getAelApXPatologistaDAO().obterPorChavePrimaria(new AelApXPatologistaId(seqAnatomoPatologico, seqPatologista) ));
	}

	protected AelApXPatologistaRN getAelApXPatologistaRN() {
		return aelApXPatologistaRN;
	}

	protected AelApXPatologistaDAO getAelApXPatologistaDAO() {
		return aelApXPatologistaDAO;
	}
}