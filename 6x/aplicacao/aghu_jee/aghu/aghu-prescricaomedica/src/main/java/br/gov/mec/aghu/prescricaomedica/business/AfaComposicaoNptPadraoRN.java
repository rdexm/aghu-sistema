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

import br.gov.mec.aghu.model.AfaComposicaoNptPadrao;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.prescricaomedica.dao.AfaComposicaoNptPadraoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AfaComposicaoNptPadraoRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3410940648936358898L;

	private static final Log LOG = LogFactory.getLog(AfaComposicaoNptPadraoRN.class);
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AfaComposicaoNptPadraoDAO afaComposicaoNptPadraoDAO;
	
	@Inject
	private AghParametrosDAO aghParametrosDAO;
	
	public enum AfaComposicaoNptPadraoRNExceptionCode implements BusinessExceptionCode {
		AFA_FORMULA_NTP_PADRAO_MS04, AFA_COMPOSICAO_NTP_PADRAO_MS05;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	
	public void excluiComposicaoNptPadrao(AfaComposicaoNptPadrao afaComposicaoNptPadrao) throws ApplicationBusinessException{
		validaDataComposicaoNptPadrao(afaComposicaoNptPadrao);
		try {
			afaComposicaoNptPadrao = afaComposicaoNptPadraoDAO.obterPorChavePrimaria(afaComposicaoNptPadrao.getId());
			afaComposicaoNptPadraoDAO.remover(afaComposicaoNptPadrao);
			afaComposicaoNptPadraoDAO.flush();
		}catch (PersistenceException e) {
			if(e.getCause().getClass().equals(ConstraintViolationException.class)){
				throw new ApplicationBusinessException(AfaComposicaoNptPadraoRNExceptionCode.AFA_COMPOSICAO_NTP_PADRAO_MS05, Severity.ERROR);
			}else{
				throw e;
			}
		}
	}
	
	private void validaDataComposicaoNptPadrao(AfaComposicaoNptPadrao afaComposicaoNptPadrao) throws ApplicationBusinessException {
		Date  dataInicial = afaComposicaoNptPadrao.getCriadoEm();
		Date dataFinal = new Date();
		int dias = DateUtil.obterQtdDiasEntreDuasDatas(dataInicial, dataFinal).intValue();
		BigDecimal diasRetornoConsultaC04 = aghParametrosDAO.obterValorNumericoAghParametros("P_DIAS_PERM_DEL_AFA");
		if(dias < diasRetornoConsultaC04.intValue()){
			throw new ApplicationBusinessException(AfaComposicaoNptPadraoRNExceptionCode.AFA_FORMULA_NTP_PADRAO_MS04, Severity.ERROR);
		}
	}
	
	public void atualizar(AfaComposicaoNptPadrao afaComposicaoNptPadrao){
		
		AfaComposicaoNptPadrao afaComposicaoNptPadraoPersistente = prescricaoMedicaFacade.obterComposicaoNptPadrao(afaComposicaoNptPadrao.getId());
		
		afaComposicaoNptPadraoPersistente.setAfaTipoComposicoes(afaComposicaoNptPadrao.getAfaTipoComposicoes());
		afaComposicaoNptPadraoPersistente.setVelocidadeAdministracao(afaComposicaoNptPadrao.getVelocidadeAdministracao());
		afaComposicaoNptPadraoPersistente.setAfaTipoVelocAdministracoes(afaComposicaoNptPadrao.getAfaTipoVelocAdministracoes());
		afaComposicaoNptPadraoPersistente.setRapServidores(afaComposicaoNptPadrao.getRapServidores());
		afaComposicaoNptPadraoPersistente.setAlteradoEm(new Date());
		afaComposicaoNptPadraoPersistente.setVersion(1);
		
		afaComposicaoNptPadraoDAO.merge(afaComposicaoNptPadraoPersistente);
	}

}
