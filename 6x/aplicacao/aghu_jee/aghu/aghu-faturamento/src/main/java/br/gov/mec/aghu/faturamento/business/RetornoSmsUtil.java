package br.gov.mec.aghu.faturamento.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RetornoSmsUtil extends BaseBusiness{

	private static final long serialVersionUID = -5003850022172860464L;

	private static final String DATE_PATTERN = "ddMMyyyy";
	
	private static final Log LOG = LogFactory.getLog(RetornoSmsUtil.class);
	
	private enum RetornoSmsUtilExceptionCode implements BusinessExceptionCode {
		ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public Date obterData(String strDate) throws ApplicationBusinessException {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);	 
		try {
			if(StringUtils.isNotBlank(strDate)){
				return  formatter.parse(strDate);
			}else{
				return null;
			}
		} catch (ParseException e) {
			LOG.error(e.getMessage());
			throw new ApplicationBusinessException(RetornoSmsUtilExceptionCode.ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS);
		}
	}
	
	public Integer converterStringEmInteger(String valor) {
		if(CoreUtil.isNumeroInteger(valor)){
			return Integer.parseInt(valor);
		}
		return null;
	}
	
	public Long converterStringEmLong(String valor) {
		if(CoreUtil.isNumeroLong(valor)){
			return Long.parseLong(valor);
		}
		return null;
	}
}
