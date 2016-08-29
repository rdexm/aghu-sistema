package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.dao.AelResultadoCaracteristicaDAO;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author amalmeida
 * 
 */
@Stateless
public class AelResultadoCaracteristicaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelResultadoCaracteristicaRN.class);
	
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
	private AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5960016652904441045L;

	public enum AelResultadoCaracteristicaRNExceptionCode implements BusinessExceptionCode {

		AEL_00369,MENSAGEM_ERRO_REMOVER_DEPENDENCIAS,AEL_00343,AEL_00344,AEL_00697;

	}

	/**
	 * ORADB AELT_CAC_BRI (INSERT)
	 * @param resultadoCaracteristica
	 * @throws BaseException
	 */
	private void preInserir(AelResultadoCaracteristica resultadoCaracteristica) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		this.validarDescricao(resultadoCaracteristica, Boolean.FALSE);
		resultadoCaracteristica.setCriadoEm(new Date());//RN1
		resultadoCaracteristica.setServidor(servidorLogado);//RN2

	}


	/**
	 * Inserir AelResultadoCaracteristica
	 * @param resultadoCaracteristica
	 * @throws BaseException
	 */
	public void inserir(AelResultadoCaracteristica resultadoCaracteristica) throws BaseException{
		this.preInserir(resultadoCaracteristica);		
		this.getAelResultadoCaracteristicaDAO().persistir(resultadoCaracteristica);
		this.getAelResultadoCaracteristicaDAO().flush();

	}


	/**
	 * ORADB AELT_CAC_BRU (UPDATE)
	 * @param resultadoCaracteristica
	 * @throws BaseException
	 */
	private void preAtualizar(AelResultadoCaracteristica resultadoCaracteristica,AelResultadoCaracteristica old) throws BaseException{

		this.validarDescricao(resultadoCaracteristica, Boolean.TRUE);
		this.validarAlteracao(resultadoCaracteristica, old);

	}


	/**
	 * Valida dados alterados
	 * @param resultadoCaracteristica
	 * @param old 
	 * @throws ApplicationBusinessException 
	 */
	protected void validarAlteracao(AelResultadoCaracteristica resultadoCaracteristica, AelResultadoCaracteristica old) throws ApplicationBusinessException {

		if (resultadoCaracteristica != null && old != null) {

			if (CoreUtil.modificados(resultadoCaracteristica.getCriadoEm(), old.getCriadoEm()) || CoreUtil.modificados(resultadoCaracteristica.getServidor().getId().getMatricula(), old.getServidor().getId().getMatricula()) || CoreUtil.modificados(resultadoCaracteristica.getServidor().getId().getVinCodigo(), old.getServidor().getId().getVinCodigo())) {

				/*Tentativa de alterar campos que não podem ser alterados.*/
				throw new ApplicationBusinessException(AelResultadoCaracteristicaRNExceptionCode.AEL_00369);

			}

		}

	}


	/**
	 * Atuliza AelResultadoCaracteristica
	 * @param resultadoCaracteristica
	 * @throws BaseException
	 */
	public void atualizar(AelResultadoCaracteristica resultadoCaracteristica) throws BaseException{
		AelResultadoCaracteristica old = getAelResultadoCaracteristicaDAO().obterOriginal(resultadoCaracteristica);
		this.preAtualizar(resultadoCaracteristica,old);		
		this.getAelResultadoCaracteristicaDAO().merge(resultadoCaracteristica);
	}


	/**
	 * ORADB AELT_CAC_BRD (DELETE)
	 * @param resultadoCaracteristica
	 * @throws BaseException
	 */
	private void preRemover(AelResultadoCaracteristica resultadoCaracteristica) throws BaseException{

		
			this.validaDependenciasRemover(resultadoCaracteristica);
			this.verificaDataCriacao(resultadoCaracteristica.getCriadoEm(), AghuParametrosEnum.P_DIAS_PERM_DEL_AEL, AelResultadoCaracteristicaRNExceptionCode.AEL_00343, AelResultadoCaracteristicaRNExceptionCode.AEL_00344);

	}
	
	/**
	 * PROCEDURE aelk_ael_rn.rn_aelp_ver_del
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
	 * ORADB CHK_AEL_RESULTADO_CARACTE
	 * @param resultadoCaracteristica
	 * @throws BaseException
	 * @throws BaseListException
	 */
	private void validaDependenciasRemover(
			AelResultadoCaracteristica resultadoCaracteristica)
			throws BaseException, BaseListException {
		// Declara lista de exceções
		final BaseListException erros = new BaseListException();

		// Valida cada dependência
		erros.add(this.obterNegocioExceptionDependencias(resultadoCaracteristica.getSeq(), AelResultadoPadraoCampo.class, AelResultadoPadraoCampo.Fields.CAL_SEQ, this.getResourceBundleValue("LABEL_AEL_RESUL_PADROES_CAMPOS")));
		erros.add(this.obterNegocioExceptionDependencias(resultadoCaracteristica.getSeq(), AelResultadoExame.class, AelResultadoExame.Fields.CAC_SEQ, this.getResourceBundleValue("LABEL_AEL_RESULTADO_EXAME")));
		erros.add(this.obterNegocioExceptionDependencias(resultadoCaracteristica.getSeq(), AelExameGrupoCaracteristica.class, AelExameGrupoCaracteristica.Fields.CAC_SEQ, this.getResourceBundleValue("LABEL_AEL_EXAME_GRUPO_CARACTERISTICA")));
			
		// Lança exceções quando existem
		if (erros.hasException()) {
			throw erros;
		}
	}
	
	/**
	 * Verifica dependências antes da remoção e obtém a exceção necessária
	 * @param elemento
	 * @param classeDependente
	 * @param fieldChaveEstrangeira
	 * @param nomeDependencia
	 * @return
	 * @throws BaseException
	 */
	public final ApplicationBusinessException obterNegocioExceptionDependencias(Object elemento, Class classeDependente, Enum fieldChaveEstrangeira, String nomeDependencia) throws BaseException{

		CoreUtil.validaParametrosObrigatorios(elemento,classeDependente,fieldChaveEstrangeira, nomeDependencia);

		if (this.getAelResultadoCaracteristicaDAO().existeDependencia(elemento, classeDependente, fieldChaveEstrangeira)){
			return new ApplicationBusinessException(AelResultadoCaracteristicaRNExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, nomeDependencia);
		}
		return null;
	}
	

	/**
	 * Atualiza AelResultadoCaracteristica
	 */
	public void remover(Integer seq) throws BaseException{
		AelResultadoCaracteristica resultadoCaracteristica = getAelResultadoCaracteristicaDAO().obterPorChavePrimaria(seq);
		
		if (resultadoCaracteristica == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.preRemover(resultadoCaracteristica);		
		this.getAelResultadoCaracteristicaDAO().remover(resultadoCaracteristica);
		
	}
	
	/**
	 * Valida se ja existe caracteristica com a mesma descricao
	 * @param resultadoCaracteristica
	 * @throws BaseException
	 */
	private void validarDescricao(AelResultadoCaracteristica resultadoCaracteristica, Boolean isUpdate) throws BaseException {
		
		if (getAelResultadoCaracteristicaDAO().obterResultadoCaracteristicaPorDescricao(resultadoCaracteristica.getDescricao(),
				resultadoCaracteristica.getSeq(), isUpdate)) {
			
			throw new ApplicationBusinessException(AelResultadoCaracteristicaRNExceptionCode.AEL_00697);
			
		}
		
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	/**
	 * Getters para RNs e DAOs
	 */

	protected AelResultadoCaracteristicaDAO getAelResultadoCaracteristicaDAO() {
		return aelResultadoCaracteristicaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}