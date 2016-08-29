package br.gov.mec.aghu.exames.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.dao.AelResuCodifLwsDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoGrupoPesquisaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoPadraoCampoDAO;
import br.gov.mec.aghu.exames.vo.ResultadoCodificadoExameVO;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificadoId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelResultadoCodificadoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelResultadoCodificadoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelResultadoGrupoPesquisaDAO aelResultadoGrupoPesquisaDAO;
	
	@Inject
	private AelResultadoPadraoCampoDAO aelResultadoPadraoCampoDAO;
	
	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;
	
	@Inject
	private AelResuCodifLwsDAO aelResuCodifLwsDAO;
	
	@Inject
	private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4779657271186003580L;

	public enum AelResultadoCodificadoRNExceptionCode implements BusinessExceptionCode {
		AEL_00369,// 
		AEL_00692,// 
		AEL_00346,// 
		AEL_00343,// 
		AEL_00344,// 
		MSG_VAL_RESULTADO_EXAME,// 
		MSG_VAL_RESULTADO_EXISTENTE,// 
		MSG_VAL_RESULTADO_PADRAO,// 
		MSG_VAL_RESUL_GRUPO_PESQUISA,// 
		MSG_VAL_RESUL_COD_LWS,//
		;
	}

	protected AelResuCodifLwsDAO getAelResuCodifLwsDAO(){
		return aelResuCodifLwsDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void persistirResultadoCodificado(AelResultadoCodificado resultado) throws BaseException {

		if (resultado != null && resultado.getId() == null) {
			preInserir(resultado);
			getAelResultadoCodificadoDAO().persistir(resultado);			
		}else{
			AelResultadoCodificado resultadoOriginal = getAelResultadoCodificadoDAO().obterOriginal(resultado);
			
			if(resultadoOriginal == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			
			preAtualizar(resultado, resultadoOriginal);
			getAelResultadoCodificadoDAO().merge(resultado);
		}
	}



	/**
	 * ORADB AELT_RDC_BRI
	 * @param AelResultadoCodificado
	 * @throws BaseException
	 */
	private void preInserir(AelResultadoCodificado resultado) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.executarRestricoes(resultado);
		resultado.setCriadoEm(new Date());
		resultado.setServidor(servidorLogado);
		validaDescricaoUnicaPorGrupo(resultado);
	}

	/**
	 * ORADB AELT_RDC_BRU
	 */
	private void preAtualizar(AelResultadoCodificado resultado, AelResultadoCodificado resultadoOriginal) throws ApplicationBusinessException {
		if(!resultado.getCriadoEm().equals(resultadoOriginal.getCriadoEm()) 
				|| !resultado.getServidor().getId().getMatricula().equals(resultadoOriginal.getServidor().getId().getMatricula())
				|| !resultado.getServidor().getId().getVinCodigo().equals(resultadoOriginal.getServidor().getId().getVinCodigo())){
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.AEL_00369);
		}

		validaGrupoAtivoInativo(resultado);
		validaDescricaoModificada(resultado, resultadoOriginal); 
	}

	private void validaGrupoAtivoInativo(AelResultadoCodificado resultado)throws ApplicationBusinessException {
		AelGrupoResultadoCodificado grupo = resultado.getGrupoResulCodificado();
		if(grupo != null && !grupo.getSituacao().isAtivo()){
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.AEL_00692);
		}
	}
	
	private void validaDescricaoModificada(AelResultadoCodificado resultado, AelResultadoCodificado resultadoOriginal)throws ApplicationBusinessException {
		if(!resultado.getDescricao().equalsIgnoreCase(resultadoOriginal.getDescricao())){
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.AEL_00346);
		}
	}
	
	/** Valida a exitência de um Resultado Codificado com a mesma descrição por grupo
	 * ORADB ael_rcd_uk1
	 * @param resultado
	 * @throws ApplicationBusinessException
	 */
	public void validaDescricaoUnicaPorGrupo(AelResultadoCodificado resultado)throws ApplicationBusinessException {
		boolean existeDescricao = getAelResultadoCodificadoDAO().pesquisaDescricaoExistenteResultadoCodificadoPorGrupo(resultado.getGrupoResulCodificado(), resultado.getDescricao());
		if(existeDescricao){
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.MSG_VAL_RESULTADO_EXISTENTE, resultado.getDescricao());
		}
	}

	/**
	 * ORADB AELT_RDC_BRD
	 * @param resultado
	 * @throws ApplicationBusinessException
	 */
	public void preRemover(AelResultadoCodificado resultado)throws ApplicationBusinessException {		
		verificaDataCriacao(resultado.getCriadoEm(), AghuParametrosEnum.P_DIAS_PERM_DEL_AEL, AelResultadoCodificadoRNExceptionCode.AEL_00343, AelResultadoCodificadoRNExceptionCode.AEL_00344);
		validaDependencias(resultado);
	}
	
	/**
	 * Remove um resultado codificado
	 */
	public void removerResultadoCodificado(AelResultadoCodificadoId id)throws ApplicationBusinessException {
		AelResultadoCodificado resultado = getAelResultadoCodificadoDAO().obterPorChavePrimaria(id);
		
		if (resultado == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		preRemover(resultado);
		getAelResultadoCodificadoDAO().remover(resultado);
	}

	private void validaDependencias(AelResultadoCodificado resultado) throws ApplicationBusinessException {

		boolean existeResultadoExame = getAelResultadoExameDAO().existeDependenciaResultadoCodificado(resultado.getId().getSeqp(), resultado.getId().getGtcSeq());
		boolean existeResultadoPadrao = getAelResultadoPadraoCampoDAO().existeDependenciaResultadoCodificado(resultado.getId().getSeqp(), resultado.getId().getGtcSeq());
		boolean existeResultGrupoPesquisa = getAelResultadoGrupoPesquisaDAO().existeDependenciaResultadoCodificado(resultado.getId().getSeqp(), resultado.getId().getGtcSeq());
		boolean existeResultCodLws = getAelResuCodifLwsDAO().existeDependenciaResultadoCodificado(resultado.getId().getSeqp(), resultado.getId().getGtcSeq());

		if(existeResultadoExame){
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.MSG_VAL_RESULTADO_EXAME);
		}
		if(existeResultadoPadrao){
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.MSG_VAL_RESULTADO_PADRAO);
		}
		if(existeResultGrupoPesquisa){
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.MSG_VAL_RESUL_GRUPO_PESQUISA);
		}
		if(existeResultCodLws){
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.MSG_VAL_RESUL_COD_LWS);
		}
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
	
	/**
	 * ORADB CONSTRAINTS (INSERT/UPDATE)
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	private void executarRestricoes(AelResultadoCodificado elemento) throws ApplicationBusinessException {
		/*
		AelResultadoCodificado resultCodificado = this.getAelResultadoCodificadoDAO()
			.obterAelResultadoCodificadoPorDescricao(elemento.getDescricao());
		
		if(resultCodificado != null) {
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.AEL_RCD_UK1);
		}
		*/
		boolean existeDescricao = getAelResultadoCodificadoDAO().pesquisaDescricaoExistenteResultadoCodificadoPorGrupo(elemento.getGrupoResulCodificado(), elemento.getDescricao());
		if(existeDescricao){
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.MSG_VAL_RESULTADO_EXISTENTE, elemento.getDescricao());
		}
	}
	
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificadosPorDescricao(String descricao) throws ApplicationBusinessException {
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CCIH_REGISTRO_BACTERIA);
		
		if (aghParametro == null || aghParametro.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.AEL_00344);
		}

		return aelResultadoCodificadoDAO.buscarResultadosCodificadosPorDescricao(descricao, aghParametro.getVlrNumerico().intValue());
	}	
	
	public Long buscarResultadosCodificadosBacteriaMultirCount(String param) throws ApplicationBusinessException {
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CCIH_REGISTRO_BACTERIA);
		
		if (aghParametro == null || aghParametro.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.AEL_00344);
		}

		return aelResultadoCodificadoDAO.buscarResultadosCodificadosBacteriaMultirCount(param, aghParametro.getVlrNumerico().intValue());
	}		
	
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificadosBacteriaMultir(String param) throws ApplicationBusinessException {
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CCIH_REGISTRO_BACTERIA);
		
		if (aghParametro == null || aghParametro.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(AelResultadoCodificadoRNExceptionCode.AEL_00344);
		}

		return aelResultadoCodificadoDAO.buscarResultadosCodificadosBacteriaMultir(param, aghParametro.getVlrNumerico().intValue());
	}		
	
	/** GET/SET **/
	protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return aelResultadoCodificadoDAO;
	}

	protected AelResultadoExameDAO getAelResultadoExameDAO(){
		return aelResultadoExameDAO;
	}
	
	protected AelResultadoPadraoCampoDAO getAelResultadoPadraoCampoDAO(){
		return aelResultadoPadraoCampoDAO;
	}
	
	protected AelResultadoGrupoPesquisaDAO getAelResultadoGrupoPesquisaDAO(){
		return aelResultadoGrupoPesquisaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
