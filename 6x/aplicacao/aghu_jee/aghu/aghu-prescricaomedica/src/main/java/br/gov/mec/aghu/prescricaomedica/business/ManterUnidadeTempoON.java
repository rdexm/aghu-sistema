package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmUnidadeTempo;
import br.gov.mec.aghu.prescricaomedica.dao.MpmUnidadeTempoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ManterUnidadeTempoON extends BaseBusiness {

	@EJB
	private MpmUnidadeTempoRN mpmUnidadeTempoRN;
	
	@Inject
	private MpmUnidadeTempoDAO mpmUnidadeTempoDAO;

	private static final Log LOG = LogFactory
			.getLog(ManterUnidadeTempoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = -5771255553138730611L;

	public void persistirUnidadeTempo(MpmUnidadeTempo mpmUnidadeTempo)
			throws BaseException {

		if (mpmUnidadeTempo.getSeq() != null) {
			updateUnidadeTempo(mpmUnidadeTempo);
		} else {
			insertUnidadeTempo(mpmUnidadeTempo);
		}
	}

	private void insertUnidadeTempo(MpmUnidadeTempo mpmUnidadeTempo)
			throws BaseException {
		getMpmUnidadeTempoRN().insereMpmUnidadeTempo(mpmUnidadeTempo);
	}

	private void updateUnidadeTempo(MpmUnidadeTempo mpmUnidadeTempo)
			throws BaseException {
		getMpmUnidadeTempoRN().atualizaMpmUnidadeTempo(mpmUnidadeTempo);
	}

	protected MpmUnidadeTempoRN getMpmUnidadeTempoRN() {
		return mpmUnidadeTempoRN;
	}

	public void excluirUnidadeTempo(Short seqMpmUnidadeTempo) throws ApplicationBusinessException {
		MpmUnidadeTempo unidadeTempo = mpmUnidadeTempoDAO.obterPorChavePrimaria(seqMpmUnidadeTempo)
;		getMpmUnidadeTempoRN().excluirUnidadeTempo(unidadeTempo);
	}
}
