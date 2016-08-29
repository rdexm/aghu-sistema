package br.gov.mec.aghu.prescricaomedica.business;


import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.model.AfaItemNptPadrao;
import br.gov.mec.aghu.model.AfaItemNptPadraoId;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.prescricaomedica.business.AfaFormulaNtpPadraoRN.AfaFormulaNptPadraoRNExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.AfaItemNptPadraoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AfaItemNptPadraoRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8257523365014052045L;

	private static final Log LOG = LogFactory.getLog(AfaItemNptPadraoRN.class);
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AfaItemNptPadraoDAO afaItemNptPadraoDAO;
	
	@Inject
	private AghParametrosDAO aghParametrosDAO;
	
	
	public enum AfaItemNptPadraoRNExceptionCode implements BusinessExceptionCode {
		AFA_FORMULA_NTP_PADRAO_MS04, AFA_COMPOSICAO_NTP_PADRAO_MS05;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}

	
	public void atualizar(AfaItemNptPadrao afaItemNptPadrao){
		
		AfaItemNptPadrao afaItemNptPadraoPersistente = prescricaoMedicaFacade.obterAfaItemNptPadraoPorId(afaItemNptPadrao.getId());
		afaItemNptPadraoPersistente.setAfaComponenteNpt(afaItemNptPadrao.getAfaComponenteNpt());
		afaItemNptPadraoPersistente.setAfaComposicaoNptPadrao(afaItemNptPadrao.getAfaComposicaoNptPadrao());
		afaItemNptPadraoPersistente.setAfaFormaDosagem(afaItemNptPadrao.getAfaFormaDosagem());
		afaItemNptPadraoPersistente.setAlteradoEm(afaItemNptPadrao.getAlteradoEm());
		afaItemNptPadraoPersistente.setId(afaItemNptPadrao.getId());
		afaItemNptPadraoPersistente.setQtde(afaItemNptPadrao.getQtde());
		afaItemNptPadraoPersistente.setRapServidores(afaItemNptPadrao.getRapServidores());
		afaItemNptPadraoPersistente.setVersion(afaItemNptPadrao.getVersion());
		afaItemNptPadraoPersistente.setvMpmDosagem(afaItemNptPadrao.getvMpmDosagem());
		
		afaItemNptPadraoDAO.merge(afaItemNptPadraoPersistente);
	}
	
	public void excluiItemNptPadrao(AfaItemNptPadraoId id) throws ApplicationBusinessException{
		AfaItemNptPadrao afaItemNptPadrao = afaItemNptPadraoDAO.obterPorChavePrimaria(id);
		validaDataItemNptPadrao(afaItemNptPadrao);
		try {
			afaItemNptPadraoDAO.removerPorId(id);
			afaItemNptPadraoDAO.flush();
		}catch(PersistenceException e) {
			if(e.getCause().getClass().equals(ConstraintViolationException.class)){
				throw new ApplicationBusinessException(AfaFormulaNptPadraoRNExceptionCode.AFA_FORMULA_NTP_PADRAO_MS03, Severity.ERROR);
			}
		}
	}
	
	private void validaDataItemNptPadrao(AfaItemNptPadrao afaItemNptPadrao) throws ApplicationBusinessException {
		Date  dataInicial = afaItemNptPadrao.getCriadoEm();
		Date dataFinal = new Date();
		int dias = DateUtil.obterQtdDiasEntreDuasDatas(dataInicial, dataFinal).intValue();
		BigDecimal diasRetornoConsultaC04 = aghParametrosDAO.obterValorNumericoAghParametros("P_DIAS_PERM_DEL_AFA");
		if(dias < diasRetornoConsultaC04.intValue()){
			throw new ApplicationBusinessException(AfaItemNptPadraoRNExceptionCode.AFA_FORMULA_NTP_PADRAO_MS04, Severity.ERROR);
		}
	}
	
	public AfaItemNptPadraoDAO getAfaItemNptPadraoDAO() {
		return afaItemNptPadraoDAO;
	}

	public void setAfaItemNptPadraoDAO(AfaItemNptPadraoDAO afaItemNptPadraoDAO) {
		this.afaItemNptPadraoDAO = afaItemNptPadraoDAO;
	}

}
