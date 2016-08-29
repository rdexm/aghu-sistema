package br.gov.mec.aghu.blococirurgico.business;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class MbcCirurgiasBean extends BaseBusiness implements IMbcCirurgiasBean {

	private static final Log LOG = LogFactory.getLog(MbcCirurgiasBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	@EJB
	private RegistroCirurgiaRealizadaON registroCirurgiaRealizadaON;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8640337808965093989L;
	
	
	@Resource
	protected SessionContext ctx;

	public enum RegistroCirurgiaRealizadaBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_ATUALIZAR_CIRURGIA_REALIZADA;
	}

	@Override
	public AlertaModalVO registrarCirurgiaRealizadaNotaConsumo(boolean emEdicao, final boolean confirmaDigitacaoNS, CirurgiaTelaVO vo, String nomeMicrocomputador
			) throws BaseException {
		AlertaModalVO alertaVO = null;
		try {
			alertaVO = this.getRegistroCirurgiaRealizadaON().registrarCirurgiaRealizadaNotaConsumo(emEdicao, confirmaDigitacaoNS, vo, nomeMicrocomputador);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(RegistroCirurgiaRealizadaBeanExceptionCode.ERRO_AO_TENTAR_ATUALIZAR_CIRURGIA_REALIZADA);
		}
		return alertaVO;
	}
	
	@Override
	public void validarModalTempoUtilizacaoO2Ozot(CirurgiaTelaVO vo,
			AlertaModalVO alertaVO, boolean isPressionouBotaoSimModal)
			throws BaseException {
		try {
			this.getRegistroCirurgiaRealizadaON().validarModalTempoUtilizacaoO2Ozot(vo, alertaVO, isPressionouBotaoSimModal);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(RegistroCirurgiaRealizadaBeanExceptionCode.ERRO_AO_TENTAR_ATUALIZAR_CIRURGIA_REALIZADA);
		}
	}
	
	private RegistroCirurgiaRealizadaON getRegistroCirurgiaRealizadaON() {
		return registroCirurgiaRealizadaON;
	}

}
