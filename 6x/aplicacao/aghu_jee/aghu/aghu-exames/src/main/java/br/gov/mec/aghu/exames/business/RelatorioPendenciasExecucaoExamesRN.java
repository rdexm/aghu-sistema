package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.vo.PendenciaExecucaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Responsavel pelas regras de negocio do relatório de exames pendentes de execução.
 * 
 * @author fwinck
 *
 */
@Stateless
public class RelatorioPendenciasExecucaoExamesRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioPendenciasExecucaoExamesRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -41000657409050316L;

	public enum RelatorioPendenciasExecucaoExamesRNExceptionCode implements BusinessExceptionCode {
		MSG_VALIDA_PARAMS_REPORT_PENDENCIAS_1, MSG_VALIDA_PARAMS_REPORT_PENDENCIAS_2;
	}

	public List<PendenciaExecucaoVO> pesquisaExamesPendentesExecucao(Short  p_unf_seq, Integer p_grt_seq, Date dtInicial,	Date dtFinal, Integer numUnicoInicial, Integer numUnicoFinal) throws ApplicationBusinessException {
		validaParametrosReport(dtInicial,dtFinal,numUnicoInicial,numUnicoFinal);

		List<PendenciaExecucaoVO> colecao = getAelSolicitacaoExameDAO().pesquisaExamesPendentesExecucao(p_unf_seq, p_grt_seq, dtInicial, dtFinal, numUnicoInicial, numUnicoFinal);

		return colecao;
	}

	private void validaParametrosReport(Date dtInicial,	Date dtFinal, Integer numUnicoInicial, Integer numUnicoFinal) throws ApplicationBusinessException{

		if((numUnicoInicial == null && numUnicoFinal != null) || (numUnicoInicial != null && numUnicoFinal == null)){
			throw new ApplicationBusinessException(RelatorioPendenciasExecucaoExamesRNExceptionCode.MSG_VALIDA_PARAMS_REPORT_PENDENCIAS_1);
		}

		if((dtInicial == null && dtFinal != null) || (dtInicial != null && dtFinal == null) || (dtInicial != null && dtFinal != null && DateUtil.validaDataMaiorIgual(dtInicial, dtFinal))){
			throw new ApplicationBusinessException(RelatorioPendenciasExecucaoExamesRNExceptionCode.MSG_VALIDA_PARAMS_REPORT_PENDENCIAS_2);
		}
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO(){
		return aelSolicitacaoExameDAO;
	}
}