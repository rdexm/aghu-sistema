package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcDescricaoTecnicaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcDescricaoTecnicaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 8856905213111316281L;
	
	private static final Integer TAMANHO_MAX_DESCRICAO_TECNICA = 4000;
	
	public enum MbcDescricaoTecnicaONExceptionCode implements BusinessExceptionCode {
		ERRO_DESCRICAO_CIRURGICA_TAM_MAXIMO_DESC_TECNICA_EXCEDIDO;
	}
	
	public void validarTamanhoDescricaoTecnica(String descricaoTecnica)
			throws ApplicationBusinessException {
		
		if (descricaoTecnica.length() > TAMANHO_MAX_DESCRICAO_TECNICA) {
			throw new ApplicationBusinessException(
					MbcDescricaoTecnicaONExceptionCode.ERRO_DESCRICAO_CIRURGICA_TAM_MAXIMO_DESC_TECNICA_EXCEDIDO);
		}
	}

}
