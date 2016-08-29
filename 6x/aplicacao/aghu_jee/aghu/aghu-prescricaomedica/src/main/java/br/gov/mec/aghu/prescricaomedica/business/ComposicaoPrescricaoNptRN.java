package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.prescricaomedica.dao.MpmComposicaoPrescricaoNptDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ComposicaoPrescricaoNptRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3223350423981469999L;

	private static final Log LOG = LogFactory.getLog(ComposicaoPrescricaoNptRN.class);

	@Inject
	private MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO;

	protected enum ComposicaoPrescricaoNptExceptionCode implements
	BusinessExceptionCode {
		MPM_00942;
	}

	public void persistir(MpmComposicaoPrescricaoNpt composicao) throws ApplicationBusinessException {
		MpmComposicaoPrescricaoNpt original = mpmComposicaoPrescricaoNptDAO.obterOriginal(composicao.getId());
		if(original == null) {
			inserir(composicao);
		} else {
			atualizar(composicao);
		}
	}
	
	public void inserir(MpmComposicaoPrescricaoNpt composicao) throws ApplicationBusinessException {
		this.preInserir(composicao);
		mpmComposicaoPrescricaoNptDAO.persistir(composicao);
	}
	
	public void atualizar(MpmComposicaoPrescricaoNpt composicao) throws ApplicationBusinessException {
		this.preAtualizar(composicao);
		mpmComposicaoPrescricaoNptDAO.atualizar(composicao);
	}

	//MPMT_CPT_BRU
	protected void preAtualizar(MpmComposicaoPrescricaoNpt composicao) throws ApplicationBusinessException {
		if(composicao.getQtdeHorasCorrer() != null) {
			composicao.setVelocidadeAdministracao(this.rnCptpVerAdm(composicao));
		}
	}

	//MPMT_CPT_BRI
	protected void preInserir(MpmComposicaoPrescricaoNpt composicao) throws ApplicationBusinessException {
		if(composicao.getQtdeHorasCorrer() != null) {
			composicao.setVelocidadeAdministracao(this.rnCptpVerAdm(composicao));
		}
	}

	/**
	 * ORADB RN_CPTP_ATU_VEL_ADM
	 */
	protected BigDecimal rnCptpVerAdm(MpmComposicaoPrescricaoNpt composicao) throws ApplicationBusinessException {
		if(composicao.getAfaTipoVelocAdministracoes() == null) {
			throw new ApplicationBusinessException(
					ComposicaoPrescricaoNptExceptionCode.MPM_00942);
		}
		
		return BigDecimal.ZERO;
	}
	
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
}
