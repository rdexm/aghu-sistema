package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class AfaTipoComposicoesON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AfaTipoComposicoesON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	public enum AfaTipoComposicoesONExceptionCode implements BusinessExceptionCode {
		TIPO_COMPOSICAO_MS01;
	}
	
	public void validaAlterarAtivo(Boolean ativo,Short seq ) throws ApplicationBusinessException{
		if(!ativo && seq != null){
			throw new ApplicationBusinessException(AfaTipoComposicoesONExceptionCode.TIPO_COMPOSICAO_MS01, Severity.INFO);
		}
	}
}