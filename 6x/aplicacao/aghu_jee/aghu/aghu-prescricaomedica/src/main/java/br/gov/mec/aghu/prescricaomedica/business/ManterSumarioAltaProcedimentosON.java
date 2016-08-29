package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioInfoComplVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterSumarioAltaProcedimentosON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterSumarioAltaProcedimentosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1441562504135408867L;

	public enum ManterSumarioAltaProcedimentosONExceptionCode implements BusinessExceptionCode {
		ERRO_NENHUMA_INFORMACAO_COMPLEMENTAR_PREENCHIDA;
	}

	/**
	 * Valida se dados foram preenchidos em Informações Complementares.
	 * 
	 * @param complemento
	 * @param {AltaSumarioInfoComplVO} vo
	 * @throws ApplicationBusinessException 
	 */
	public void validarInformacoesComplementares(String complemento, AltaSumarioInfoComplVO vo) throws ApplicationBusinessException {

		if (StringUtils.isBlank(complemento) && StringUtils.isBlank(vo.getDescricaoMedicamento()) && vo.getvAfaDescrMdto() == null) {
			
			throw new ApplicationBusinessException(ManterSumarioAltaProcedimentosONExceptionCode.ERRO_NENHUMA_INFORMACAO_COMPLEMENTAR_PREENCHIDA);
			
		}
		
	}

}
