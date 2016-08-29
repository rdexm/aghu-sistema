package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceEstqAlmoxMvtoDAO;
import br.gov.mec.aghu.model.SceEstqAlmoxMvto;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class SceEstqAlmoxMvtoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SceEstqAlmoxMvtoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceEstqAlmoxMvtoDAO sceEstqAlmoxMvtoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 333919905593950059L;
	private final static BigDecimal valor = BigDecimal.ZERO;
	
	public enum SceEstqAlmoxMvtoRNExceptionCode implements BusinessExceptionCode {
		SCE_00455, VALOR_INVALIDO;
	}
	
	/**
	 * @ORADB TRIGGER SCET_EAM_BRI
	 * @param estqAlmoxMvto
	 * @throws BaseException
	 */
	public void inserir(SceEstqAlmoxMvto estqAlmoxMvto) throws BaseException {
		
		validaEstqAlmoxMvto(estqAlmoxMvto);
		getSceEstqAlmoxMvtoDAO().persistir(estqAlmoxMvto);
		getSceEstqAlmoxMvtoDAO().flush();
		
	}
	
	/**
	 * @ORADB TRIGGER SCET_EAM_BRU
	 * @param estqAlmoxMvto
	 * @throws BaseException
	 */
	public void atualizar(SceEstqAlmoxMvto estqAlmoxMvto) throws BaseException {
		
		validaEstqAlmoxMvto(estqAlmoxMvto);
		getSceEstqAlmoxMvtoDAO().atualizar(estqAlmoxMvto);
		
	}
	
	/**
	 * @ORADB SCEK_EAM_RN.RN_EAMP_VER_QTDE_VAL
	 * @param estqAlmoxMvto
	 * @throws BaseException
	 */
	private void validaEstqAlmoxMvto(SceEstqAlmoxMvto estqAlmoxMvto) throws BaseException {
		
		if(estqAlmoxMvto!=null && estqAlmoxMvto.getSceTipoMovimentos()!=null && estqAlmoxMvto.getSceTipoMovimentos()!=null){
			
			if (estqAlmoxMvto.getSceTipoMovimentos().getIndAltQtdeEstqAlmoxMvto() && estqAlmoxMvto.getValor() == valor) {
				
				throw new ApplicationBusinessException(SceEstqAlmoxMvtoRNExceptionCode.SCE_00455);
				
			}
			
			if (estqAlmoxMvto.getSceTipoMovimentos().getIndAltValrEstqAlmoxMvto() && estqAlmoxMvto.getValor() == valor) {
				
				throw new ApplicationBusinessException(SceEstqAlmoxMvtoRNExceptionCode.VALOR_INVALIDO);
				
			}
			
		}
		
	}
	
	protected SceEstqAlmoxMvtoDAO getSceEstqAlmoxMvtoDAO() {
		return sceEstqAlmoxMvtoDAO;
	}

}
