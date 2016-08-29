package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FatValorDiariaInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatValorDiariaInternacaoON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5235691833994771589L;
	
	public enum DataInvalida implements BusinessExceptionCode {
		DATA_INVALIDA;
	}
	
	@EJB
	private FatValorDiariaInternacaoRN fatValorDiariaInternacaoRN;
	
	private static final Log LOG = LogFactory.getLog(FatValorDiariaInternacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}


	/**
	 * Persiste um objeto FatValorDiariaInternacao
	 * 
	 * @param fatValorDiariaInternacao
	 * @throws ApplicationBusinessException 
	 */
	public void persistirValorDiariaInternacao(FatValorDiariaInternacao fatValorDiariaInternacao) throws ApplicationBusinessException {
		
		
		int result = fatValorDiariaInternacao.getId().getDataInicioValidade().compareTo(fatValorDiariaInternacao.getDataFimValidade());
		if(result > 0){
			throw new ApplicationBusinessException(DataInvalida.DATA_INVALIDA);
		}
		fatValorDiariaInternacaoRN.persistirValorDiariaInternacao(fatValorDiariaInternacao);
	}

	public void alterarValorDiariaInternacao(FatValorDiariaInternacao fatValorDiariaInternacao) throws ApplicationBusinessException {
		
		int result = fatValorDiariaInternacao.getId().getDataInicioValidade().compareTo(fatValorDiariaInternacao.getDataFimValidade());
		if(result > 0){
			throw new ApplicationBusinessException(DataInvalida.DATA_INVALIDA);
		}
		fatValorDiariaInternacaoRN.alterarValorDiariaInternacao(fatValorDiariaInternacao);
	}
}