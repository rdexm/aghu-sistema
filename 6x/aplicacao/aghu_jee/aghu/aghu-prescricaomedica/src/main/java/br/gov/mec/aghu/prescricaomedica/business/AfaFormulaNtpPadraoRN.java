package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.prescricaomedica.dao.AfaFormulaNptPadraoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AfaFormulaNtpPadraoRN extends BaseBusiness{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3339759097455127456L;

	private static final Log LOG = LogFactory.getLog(AfaFormulaNtpPadraoRN.class);
	
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private AfaFormulaNptPadraoDAO afaFormulaNptPadraoDAO;
	
	@Inject
	private AghParametrosDAO aghParametrosDAO;
	
	public enum AfaFormulaNptPadraoRNExceptionCode implements BusinessExceptionCode {
		AFA_FORMULA_NTP_PADRAO_MS03, AFA_FORMULA_NTP_PADRAO_MS04;
	}
	
	/**
	 * RN03
	 * @param AfaFormulaNptPadrao
	 * @throws ApplicationBusinessException
	 */
	public void excluiFormulaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao) throws ApplicationBusinessException{
		validaDataFormulaNptPadrao(afaFormulaNptPadrao);
		try {
			afaFormulaNptPadrao = afaFormulaNptPadraoDAO.obterPorChavePrimaria(afaFormulaNptPadrao.getSeq());
			afaFormulaNptPadraoDAO.remover(afaFormulaNptPadrao);
			afaFormulaNptPadraoDAO.flush();
		}catch (PersistenceException e) {
			if(e.getCause().getClass().equals(ConstraintViolationException.class)){
				throw new ApplicationBusinessException(AfaFormulaNptPadraoRNExceptionCode.AFA_FORMULA_NTP_PADRAO_MS03, Severity.ERROR);
			}
		}
	}


	private void validaDataFormulaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao) throws ApplicationBusinessException {
		Date  dataInicial = afaFormulaNptPadrao.getCriadoEm();
		Date dataFinal = new Date();
		int dias = DateUtil.obterQtdDiasEntreDuasDatas(dataInicial, dataFinal).intValue();
		BigDecimal diasRetornoConsultaC04 = aghParametrosDAO.obterValorNumericoAghParametros("P_DIAS_PERM_DEL_AFA");
		if(dias < diasRetornoConsultaC04.intValue()){
			throw new ApplicationBusinessException(AfaFormulaNptPadraoRNExceptionCode.AFA_FORMULA_NTP_PADRAO_MS04, Severity.ERROR);
		}
	}
	
	
	public AfaFormulaNptPadraoDAO getAfaFormulaNptPadraoDAO() {
		return afaFormulaNptPadraoDAO;
	}

	public void setAfaFormulaNptPadraoDAO(AfaFormulaNptPadraoDAO afaFormulaNptPadraoDAO) {
		this.afaFormulaNptPadraoDAO = afaFormulaNptPadraoDAO;
	}

}
