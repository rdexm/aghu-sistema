package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.dao.FatExcecaoPercentualDAO;
import br.gov.mec.aghu.model.FatExcecaoPercentual;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatExcecaoPercentualRN extends BaseBusiness{


	/**
	 * 
	 */
	private static final long serialVersionUID = 7826785734234917610L;

	@Inject
	private FatExcecaoPercentualDAO fatExcecaoPercentualDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final Log LOG = LogFactory.getLog(FatExcecaoPercentualRN.class);

	private enum FatExcecaoPercentualRNExceptionCode implements BusinessExceptionCode {
		M1_NUMERO_MAXIMO_EXCECOES_PERCENTUAIS;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public void inserirExcecaoPercentual(FatExcecaoPercentual excecaoPercentual) throws ApplicationBusinessException {
		Long quantidadeMaximaCampoMedico = parametroFacade.buscarValorLong(AghuParametrosEnum.P_MAX_PROCED_CMA_AIH);
		Long countExcecao = this.fatExcecaoPercentualDAO.listarExcecaoPercentualCount(excecaoPercentual) + 1;
		
		if (countExcecao > quantidadeMaximaCampoMedico) {
			throw new ApplicationBusinessException(FatExcecaoPercentualRNExceptionCode.M1_NUMERO_MAXIMO_EXCECOES_PERCENTUAIS);
		} else {
			this.fatExcecaoPercentualDAO.persistir(excecaoPercentual);
		}
		flush();
	}
	
}
