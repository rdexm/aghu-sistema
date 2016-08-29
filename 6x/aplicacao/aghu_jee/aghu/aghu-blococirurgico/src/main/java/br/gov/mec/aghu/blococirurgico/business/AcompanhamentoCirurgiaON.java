package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;


@Stateless
public class AcompanhamentoCirurgiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AcompanhamentoCirurgiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private EmailUtil emailUtil;


	@EJB
	private IParametroFacade iParametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1793105923901272761L;
	
	public enum AcompanhamentoCirurgiaONExceptionCode implements	BusinessExceptionCode {
		ERRO_EXISTE_CIRURGIA_TRANSOPERATORIO
	}


	public void validarSeExisteCirurgiatransOperatorio(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		
		List<MbcCirurgias> listMbcCirurgias = null;
		if(cirurgia != null && cirurgia.getSalaCirurgica() != null && cirurgia.getSalaCirurgica().getId() != null){
			listMbcCirurgias = getMbcCirurgiasDAO().pesquisarMbcCirurgiasComSalaCirurgia(null, cirurgia.getSalaCirurgica().getId().getUnfSeq(),
					cirurgia.getSalaCirurgica().getId().getSeqp(), DominioSituacaoCirurgia.TRAN, true, null, null, null);
		}
		
		boolean cirurgiaTrans =  listMbcCirurgias != null && !listMbcCirurgias.isEmpty() ? true : false;
		
		if(cirurgiaTrans){
			throw new ApplicationBusinessException(AcompanhamentoCirurgiaONExceptionCode.ERRO_EXISTE_CIRURGIA_TRANSOPERATORIO);
		}
		
	}	

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected EmailUtil getEmailUtil() {
		return emailUtil;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

}
