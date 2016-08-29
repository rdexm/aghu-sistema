package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmTipoRespostaConsultoria;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmRespostaConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoRespostaConsultoriaDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MpmTipoRespostaConsultoriaRN extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(MpmTipoRespostaConsultoriaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MpmTipoRespostaConsultoriaDAO mpmTipoRespostaConsultoriaDAO;
	
	@Inject
	private MpmRespostaConsultoriaDAO mpmRespostaConsultoriaDAO;
	
	private static final long serialVersionUID = 2785561448693429256L;
	
	public enum MpmTipoRespostaConsultoriaRNExceptionCode implements BusinessExceptionCode {
		MPM_00680, MPM_00681, RAP_00175, MENSAGEM_ERRO_CK6_ACOMPANHAMENTO, MENSAGEM_ERRO_CK7_PRIM_VEZ,
		MPM_00737, MENSAGEM_ERRO_RESPOSTA_ASSOCIADA;
	}
	
	public void inserirTipoRespostaConsultoria(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria)
			throws ApplicationBusinessException {
		
		preInserir(mpmTipoRespostaConsultoria);
		validaConstraints(mpmTipoRespostaConsultoria);
		this.getMpmTipoRespostaConsultoriaDAO().persistir(mpmTipoRespostaConsultoria);
	}
	
	/**
	 * TRIGGER "AGH".MPMT_TRC_BRI
	 * @param mpmTipoRespostaConsultoria
	 * @throws ApplicationBusinessException
	 */
	private void preInserir(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		mpmTipoRespostaConsultoria.setCriadoEm(new Date());
		mpmTipoRespostaConsultoria.setServidor(servidorLogado);
		
		if (mpmTipoRespostaConsultoria.getServidor() == null || mpmTipoRespostaConsultoria.getServidor().getId() == null ||
				mpmTipoRespostaConsultoria.getServidor().getId().getMatricula() == null) {
			throw new ApplicationBusinessException(MpmTipoRespostaConsultoriaRNExceptionCode.RAP_00175);
		}
	}
	
	public void atualizarTipoRespostaConsultoria(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria)
			throws ApplicationBusinessException {
		
		preAlterar(mpmTipoRespostaConsultoria);
		validaConstraints(mpmTipoRespostaConsultoria);
		
		this.getMpmTipoRespostaConsultoriaDAO().merge(mpmTipoRespostaConsultoria);
	}
	
	/**
	 * TRIGGER "AGH".MPMT_TRC_BRU
	 * @param tipoRespCons
	 * @throws ApplicationBusinessException
	 */
	private void preAlterar(MpmTipoRespostaConsultoria tipoRespCons) throws ApplicationBusinessException {
		MpmTipoRespostaConsultoria tipoRespConsOriginal = 
				getMpmTipoRespostaConsultoriaDAO().obterOriginal(tipoRespCons.getSeq());
		
		if (CoreUtil.modificados(tipoRespCons.getSeq(), tipoRespConsOriginal.getSeq())
				|| CoreUtil.modificados(tipoRespCons.getDescricao(), tipoRespConsOriginal.getDescricao())
				|| CoreUtil.modificados(tipoRespCons.getCriadoEm(), tipoRespConsOriginal.getCriadoEm())
				|| CoreUtil.modificados(tipoRespCons.getServidor(), tipoRespConsOriginal.getServidor())) {
			
			throw new ApplicationBusinessException(
					MpmTipoRespostaConsultoriaRNExceptionCode.MPM_00737);
		}
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		tipoRespCons.setServidor(servidorLogado);
		
		if (tipoRespCons.getServidor() == null || tipoRespCons.getServidor().getId() == null ||
				tipoRespCons.getServidor().getId().getMatricula() == null) {
			throw new ApplicationBusinessException(MpmTipoRespostaConsultoriaRNExceptionCode.RAP_00175);
		}
	}
	
	private void validaConstraints(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria) throws ApplicationBusinessException {
		if (mpmTipoRespostaConsultoria.getIndAcompanhamento().equals(Boolean.FALSE)
				&& mpmTipoRespostaConsultoria.getIndDigitObrigAcomp().equals(Boolean.TRUE)) {
			throw new ApplicationBusinessException(MpmTipoRespostaConsultoriaRNExceptionCode.MENSAGEM_ERRO_CK6_ACOMPANHAMENTO);
		}
		
		if (mpmTipoRespostaConsultoria.getIndPrimVez().equals(Boolean.FALSE)
				&& mpmTipoRespostaConsultoria.getIndDigitObrigatoria().equals(Boolean.TRUE)) {
			throw new ApplicationBusinessException(MpmTipoRespostaConsultoriaRNExceptionCode.MENSAGEM_ERRO_CK7_PRIM_VEZ);
		}
	}
	
	public void excluirTipoRespostaConsultoria(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria)
			throws ApplicationBusinessException {
		
		preDeleteTipoRespostaConsultoria(mpmTipoRespostaConsultoria);
		
		if(mpmRespostaConsultoriaDAO.existeRespostaAssociada(mpmTipoRespostaConsultoria)) {
			throw new ApplicationBusinessException(MpmTipoRespostaConsultoriaRNExceptionCode.MENSAGEM_ERRO_RESPOSTA_ASSOCIADA);
		}
		
		mpmTipoRespostaConsultoria = getMpmTipoRespostaConsultoriaDAO().merge(mpmTipoRespostaConsultoria);
		
		this.getMpmTipoRespostaConsultoriaDAO().remover(mpmTipoRespostaConsultoria);
	}
	
	/**
	 * TRIGGER "AGH".MPMT_TRC_BRD
	 * @ORADB MPMK_TRC_RN.RN_TRCP_VER_DELECAO (P_OLD_CRIADO_EM)
	 * @param mpmTipoRespostaConsultoria
	 * @throws ApplicationBusinessException
	 */
	public void preDeleteTipoRespostaConsultoria(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria)
			throws ApplicationBusinessException {

		AghParametros aghParametro = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_DIAS_PERM_DEL_MPM);
		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar
					.getInstance().getTime(), mpmTipoRespostaConsultoria.getCriadoEm());
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(
						MpmTipoRespostaConsultoriaRNExceptionCode.MPM_00681);
			}
		} else {
			throw new ApplicationBusinessException(
					MpmTipoRespostaConsultoriaRNExceptionCode.MPM_00680);
		}
	}
	
	protected MpmTipoRespostaConsultoriaDAO getMpmTipoRespostaConsultoriaDAO(){
		return mpmTipoRespostaConsultoriaDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public IParametroFacade getParametroFacade()  {
		return parametroFacade;
	}
}
