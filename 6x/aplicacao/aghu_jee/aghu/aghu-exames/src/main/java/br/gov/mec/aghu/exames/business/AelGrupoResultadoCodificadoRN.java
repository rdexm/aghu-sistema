package br.gov.mec.aghu.exames.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.dao.AelGrupoResultadoCodificadoDAO;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelGrupoResultadoCodificadoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelGrupoResultadoCodificadoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGrupoResultadoCodificadoDAO aelGrupoResultadoCodificadoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4779657271186003580L;

	public enum AelGrupoResultadoCodificadoRNExceptionCode implements BusinessExceptionCode {
		AEL_00369, AEL_00343, AEL_00344, MSG_VAL_CAMPOS_LAUDOS, MSG_VAL_RESULT_CODIFICADOS;
	}

	protected AelGrupoResultadoCodificadoDAO getAelGrupoResultadoCodificadoDAO() {
		return aelGrupoResultadoCodificadoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void persistirGrupoResultadoCodificado(AelGrupoResultadoCodificado grupoResultado) throws BaseException {

		if (grupoResultado!= null && grupoResultado.getSeq() == null) {
			preInserir(grupoResultado);
			getAelGrupoResultadoCodificadoDAO().persistir(grupoResultado);
		}else{
			AelGrupoResultadoCodificado grupoOriginal = getAelGrupoResultadoCodificadoDAO().obterOriginal(grupoResultado);
			preAtualizar(grupoResultado, grupoOriginal);
			getAelGrupoResultadoCodificadoDAO().merge(grupoResultado);
		}
	}

	/**
	 * ORADB AELT_GTC_BRI
	 * @param AelResultadoCodificado
	 * @throws BaseException
	 */
	private void preInserir(AelGrupoResultadoCodificado resultado) throws BaseException {	
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		resultado.setCriadoEm(new Date());
		resultado.setServidor(servidorLogado);
	}
	
	/**
	 * ORADB AELT_GTC_BRU
	 * @param grupo
	 * @throws ApplicationBusinessException
	 */
	private void preAtualizar(AelGrupoResultadoCodificado grupo, AelGrupoResultadoCodificado grupoOriginal) throws ApplicationBusinessException {
		if(!grupo.getCriadoEm().equals(grupoOriginal.getCriadoEm()) 
				|| !grupo.getServidor().getId().getMatricula().equals(grupoOriginal.getServidor().getId().getMatricula())
				|| !grupo.getServidor().getId().getVinCodigo().equals(grupoOriginal.getServidor().getId().getVinCodigo())){
			throw new ApplicationBusinessException(AelGrupoResultadoCodificadoRNExceptionCode.AEL_00369);
		}
	}
	
	private void validaDependencias(AelGrupoResultadoCodificado resultado) throws ApplicationBusinessException {
		
		AelGrupoResultadoCodificado resultadoOriginal = getAelGrupoResultadoCodificadoDAO().obterOriginal(resultado);
		
		
		if(resultadoOriginal != null && resultadoOriginal.getCamposLaudo() != null && resultadoOriginal.getCamposLaudo().size() == 0){
			throw new ApplicationBusinessException(AelGrupoResultadoCodificadoRNExceptionCode.MSG_VAL_CAMPOS_LAUDOS);
		}
		
		if(resultadoOriginal != null && resultadoOriginal.getCamposLaudo() != null && resultadoOriginal.getResultadosCodificados().size() == 0){
			throw new ApplicationBusinessException(AelGrupoResultadoCodificadoRNExceptionCode.MSG_VAL_RESULT_CODIFICADOS);
		}
	}

	/**
	 * ORADB AELT_GTC_BRD, CHK_AEL_GRUPO_RESUL_CODIF
	 * @param resultado
	 * @throws ApplicationBusinessException
	 */
	private void preRemover(AelGrupoResultadoCodificado resultado)throws ApplicationBusinessException {		
		verificaDataCriacao(resultado.getCriadoEm(), AghuParametrosEnum.P_DIAS_PERM_DEL_AEL, AelGrupoResultadoCodificadoRNExceptionCode.AEL_00343, AelGrupoResultadoCodificadoRNExceptionCode.AEL_00344);
		validaDependencias(resultado);
	}
	
	/**
	 * Remove um resultado codificado
	 */
	public void removerGrupoResultadoCodificado(Integer seq)throws ApplicationBusinessException {
		AelGrupoResultadoCodificado resultado = getAelGrupoResultadoCodificadoDAO().obterPorChavePrimaria(seq);
		if (resultado == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		preRemover(resultado);
		getAelGrupoResultadoCodificadoDAO().remover(resultado);
	}
	
	/**
	 * oradb aelk_rcd_rn.rn_rcdp_ver_delecao
	 * @param data
	 * @param aghuParametrosEnum
	 * @param exceptionForaPeriodoPermitido
	 * @param erroRecuperacaoAghuParametro
	 * @throws ApplicationBusinessException
	 */
	public void verificaDataCriacao(final Date data, final AghuParametrosEnum aghuParametrosEnum, BusinessExceptionCode exceptionForaPeriodoPermitido, BusinessExceptionCode erroRecuperacaoAghuParametro) throws ApplicationBusinessException {
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(aghuParametrosEnum);
		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar.getInstance().getTime(), data);
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(exceptionForaPeriodoPermitido);
			}
		} else {
			throw new ApplicationBusinessException(erroRecuperacaoAghuParametro);
		}
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}