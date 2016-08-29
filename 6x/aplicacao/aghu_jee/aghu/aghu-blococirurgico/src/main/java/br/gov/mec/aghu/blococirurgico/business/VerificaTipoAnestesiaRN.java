package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ORADB PROCEDURE RN_CRGP_VER_TIPO_ANS
 * 
 * @author aghu
 * 
 */
@Stateless
public class VerificaTipoAnestesiaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(VerificaTipoAnestesiaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -5179115164213572153L;

	public enum VerificaTipoAnestesiaRNExceptionCode implements BusinessExceptionCode {
		MBC_00508;
	}

	/**
	 * ORADB PROCEDURE RN_CRGP_VER_TIPO_ANS
	 * 
	 * <p>
	 * Garante que tenha sido informado pelo menos um tipo de anestesia
	 * <p>
	 * 
	 * @param crgSeq
	 * @throws BaseException
	 */
	public void verificarTipoAnestesia(Integer crgSeq) throws BaseException {

		List<MbcAnestesiaCirurgias> listaAnestesias = this.getMbcAnestesiaCirurgiasDAO().listarTipoAnestesiasPorCrgSeq(crgSeq);

		if (listaAnestesias.isEmpty()) {
			// Pelo menos um tipo de anestesia deve ser informado
			throw new ApplicationBusinessException(VerificaTipoAnestesiaRNExceptionCode.MBC_00508);
		}

	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}

}
