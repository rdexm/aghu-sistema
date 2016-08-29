package br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business;

import java.util.Date;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ConsultaArquivoLogPOLON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ConsultaArquivoLogPOLON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6610606176961390820L;

	public enum ConsultaArquivoLogPOLONExceptionCode implements BusinessExceptionCode {
		ERRO_DIFERENCA_DATAS, ERRO_INTERVALO_DATA_INVALIDO;
	}
	
	private static final String PATTERN_DD_MM_YYYY = "dd/MM/yyyy";
	
	/**
	 * Valida se a data inicio e fim são validas
	 * 
	 * @param inicio
	 * @param fim
	 * @throws ApplicationBusinessException 
	 */
	public void validarIntervaloData(Date inicio, Date fim) throws ApplicationBusinessException {
		Integer qtdDiasLimite = 15;
		if (DateUtil.diffInDaysInteger(fim, inicio) > qtdDiasLimite) {
			throw new ApplicationBusinessException(ConsultaArquivoLogPOLONExceptionCode.ERRO_DIFERENCA_DATAS, qtdDiasLimite,obterDataFormatada(inicio), obterDataFormatada(fim), DateUtil.diffInDaysInteger(fim, inicio));
		}
		if (DateUtil.validaDataMaior(inicio, fim)) {
			throw new ApplicationBusinessException(ConsultaArquivoLogPOLONExceptionCode.ERRO_INTERVALO_DATA_INVALIDO);
		}
	}	
	
	private String obterDataFormatada(Date d) {
		return DateUtil.dataToString(d, PATTERN_DD_MM_YYYY);
	}

}
