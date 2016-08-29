package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificadoId;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class AelResultadoExameRN extends BaseBusiness {

	
	@EJB
	private AelResultadosExamesON aelResultadosExamesON;
	
	private static final Log LOG = LogFactory.getLog(AelResultadoExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject
	private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelCampoLaudoDAO aelCampoLaudoDAO;
	
	@Inject
	private AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8363571626802394548L;

	public enum AelResultadoExameRNExceptionCode implements
			BusinessExceptionCode {

		AEL_00801, //Para o tipo numerico o valor deve ser informado.
		AEL_00802, //Para os tipos expressão(E) e texto fixo(T) não e permitido informar.
		AEL_00804, //resultado codificado ou caracteristico o tipo de campo laudo deve ser codificado.
		AEL_00867, //resultado codificado para este codigo não esta ativo.
		AEL_00868, //resultado característica para este código não está ativo.
		AEL_00869, //campo laudo informado nao existe.
		AEL_01987, //prazo permitido para alteração dos resultados deste exame já expirou.
		AEL_02551, //Nao e permitido anular todos os resultados do exame tendo a situacao dele como liberado.
		AEL_00807,//O indicador anulacao laudo so pode ser alterado de 'N' para 'S'.
		AEL_03414, // É necessário informar um valor numérico para o parâmetro P_SITUACAO_LIBERADO
		;

	}
	
	/**
	 * Atualiza AelResultadoExame
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void atualizar(AelResultadoExame elemento, String nomeMicrocomputador)throws BaseException {
		this.preAtualizar(elemento);
		this.getAelResultadoExameDAO().atualizar(elemento);
		this.getAelResultadoExameDAO().flush();
		this.posAtualizar(elemento, null, nomeMicrocomputador);
	}
	
	/**
	 * Persiste AelResultadoExame
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void persistir(AelResultadoExame elemento) throws BaseException {
		this.inserir(elemento);
		this.getAelResultadoExameDAO().flush();
	}

	
	/**
	 * Insere AelResultadoExame
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void inserir(AelResultadoExame elemento) throws BaseException {
		this.preInserir(elemento);
		this.getAelResultadoExameDAO().persistir(elemento);
		this.posInserir(elemento);
	}

	
	/**
	 * ORADB TRIGGER AELT_REE_BRI
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void preInserir(AelResultadoExame elemento)
			throws BaseException {
		
		//RN1
		elemento.setCriadoEm(new Date());
		elemento.setAlteradoEm(new Date());
		
		//RN2
		final AelResultadoExame oldElemento = this.getAelResultadoExameDAO().obterOriginal(elemento);
		
		Integer cacSeq = this.obterValorResultadoCaracteristica(elemento);
		Integer oldcacSeq = this.obterValorResultadoCaracteristica(oldElemento);
		Integer gtcSeq = this.obterValorGtcSeqResultadoCodificado(elemento);
		Integer oldGtcSeq = this.obterValorGtcSeqResultadoCodificado(oldElemento);
		Integer rcdSeqp = this.obterValorSeqpResultadoCodificado(elemento);
		Integer oldRcdSeqp = this.obterValorSeqpResultadoCodificado(oldElemento);
		
		
		if(oldElemento!=null && oldElemento.getId() != null && (CoreUtil.modificados(elemento.getId().getPclCalSeq(), oldElemento.getId().getPclCalSeq()) 
				|| CoreUtil.modificados(cacSeq,	oldcacSeq)
				|| CoreUtil.modificados(gtcSeq,	oldGtcSeq)
				|| CoreUtil.modificados(rcdSeqp, oldRcdSeqp)
				|| CoreUtil.modificados(elemento.getId().getPclVelEmaExaSigla(), 
						oldElemento.getId().getPclVelEmaExaSigla())
				|| CoreUtil.modificados(elemento.getId().getPclVelEmaManSeq(), 
						oldElemento.getId().getPclVelEmaManSeq()))) {
			this.verificarTipoCampo(elemento);
		}
		
		//RN3
		this.verificarTempoLiberacao(elemento);
		
		//RN4
		this.verificarNovaSolicitacaoEnvio(elemento);
		
	}
	
	/**
	 * ORADB TRIGGER AELP_ENFORCE_REE_RULES
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void posInserir(AelResultadoExame elemento)
			throws BaseException {
		 
		this.executarPosInserirEnforce(elemento);
	}

	
	/**
	 * ORADB TRIGGER AELT_REE_BRU
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preAtualizar(AelResultadoExame elemento) throws ApplicationBusinessException {
		//RN1
		elemento.setAlteradoEm(new Date());
		
		//RN2
		this.verificarTipoCampo(elemento);
		
		//RN3
		this.verificarAnulacao(elemento,null);
		
		//RN4
		this.verificarAnulacaoSumario(elemento,null);
		
		//RN5
		this.verificarAnulacaoLaudo(elemento,null);
		
	}

	/**
	 * ORADB TRIGGER AELP_ENFORCE_REE_RULES
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void posAtualizar(AelResultadoExame elemento, AelResultadoExame original, String nomeMicrocomputador)	throws BaseException {
		this.executarPosAtualizarEnforce(elemento, original, nomeMicrocomputador);
	}

	/**
	 * ORADB PROCEDURE aelk_ree_rn.rn_reep_ver_tp_campo
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	public void verificarTipoCampo(AelResultadoExame elemento) throws ApplicationBusinessException {
				
		final AelCampoLaudo campoLaudo = this.getAelCampoLaudoDAO().obterCampoLaudoPorSeq(elemento.getId().getPclCalSeq());
			
		if(campoLaudo == null) {
			throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_00869);
		} else {
			//1.1
			if(elemento.getResultadoCaracteristica() != null
					|| elemento.getResultadoCodificado() != null
					&& DominioTipoCampoCampoLaudo.C != campoLaudo.getTipoCampo()) {
				throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_00804);
			//1.2
			} else if(DominioTipoCampoCampoLaudo.C == campoLaudo.getTipoCampo()
					&& campoLaudo.getGrupoResultadoCaracteristica() != null) {
				
				final AelResultadoCaracteristica resultadoCaracteristica = 
					this.getAelResultadoCaracteristicaDAO().obterResultadoCaracteristicaPorTipoCampo(
							elemento.getId().getPclCalSeq(), elemento.getId().getPclVelEmaExaSigla(), 
							elemento.getId().getPclVelEmaManSeq());
				
				if(DominioSituacao.I == resultadoCaracteristica.getIndSituacao()) {
					throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_00868);
				}
				
				if(campoLaudo.getGrupoResultadoCodificado() != null) {
					final AelResultadoCodificadoId id = new AelResultadoCodificadoId(
							elemento.getResultadoCodificado().getId().getGtcSeq(),
							elemento.getResultadoCodificado().getId().getSeqp());
					
					final AelResultadoCodificado resultadoCodificado = 
						this.getAelResultadoCodificadoDAO().obterOriginal(id);
					
					if(DominioSituacao.I == resultadoCodificado.getSituacao()) {
						throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_00867);
					}
				}
				
			}
			//1.3
			if(DominioTipoCampoCampoLaudo.N == campoLaudo.getTipoCampo()
					&& elemento.getValor() == null) {
				
				throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_00801);
			} else if(DominioTipoCampoCampoLaudo.T == campoLaudo.getTipoCampo()) {
				throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_00802);
			}
		}
		
	}
		
	
	
	/**
	 * ORADB PROCEDURE aelk_ree_rn.rn_reep_ver_temp_lib
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 *  
	 */
	public void verificarTempoLiberacao(AelResultadoExame elemento) throws ApplicationBusinessException {
		final Integer tempo;
		final DominioUnidTempo unidTempo;
		final String situacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO).getVlrTexto();
		//c_ise
		AelItemSolicitacaoExames solicExames = this.getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorSituacaoSoeSeqp(
				situacao, elemento.getId().getIseSoeSeq(), elemento.getId().getIseSeqp());
		
		if(solicExames != null){
		
			//c_exame		
			final AelUnfExecutaExames executaExames = this.getAelUnfExecutaExamesDAO()
				.buscaAelUnfExecutaExames(solicExames.getExame().getSigla(), solicExames.getMaterialAnalise().getSeq(), 
						solicExames.getUnidadeFuncional().getSeq());
			
			if(executaExames.getTempoAposLiberacao() != null) {
				tempo = executaExames.getTempoAposLiberacao().intValue();
				unidTempo = executaExames.getUnidTempoAposLib();
			} else {
				AghParametros paramTempo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TEMPO_APOS_LIBERACAO);
				if(paramTempo.getVlrNumerico() == null){
					throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_03414);
				}
				tempo = paramTempo.getVlrNumerico().intValue();
				unidTempo = DominioUnidTempo.valueOf(DominioUnidTempo.class, this.getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_UNID_TEMPO_APOS_LIB).getVlrTexto());
			}
			
			if(DominioUnidTempo.H == unidTempo) {
				Integer horas = DateUtil.obterQtdHorasEntreDuasDatas(solicExames.getDthrLiberada(), new Date());
				
				if(horas > tempo) {
					throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_01987);
				}
			} else {
				if(solicExames.getDthrLiberada()!=null){
					Integer dias = this.solicitacaoExameFacade.calcularDiasUteisEntreDatas(solicExames.getDthrLiberada(), new Date());
					
					if(dias > tempo) {
						throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_01987);
					}
				}
			}
		}
	}
	
	
	/**
	 * ORADB PROCEDURE aelk_ree_rn.rn_reep_atu_nova_soe
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void verificarNovaSolicitacaoEnvio(AelResultadoExame elemento) throws ApplicationBusinessException {
		Integer pclCalSeq = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_CAMPO_LAUDO_GERA_NOVA_SOLIC).getVlrNumerico().intValue();
		
		Integer gtcSeq = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_GRUPO_CODIF_GERA_NOVA_SOLIC).getVlrNumerico().intValue();
		
		Integer rcdSeqp = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_RESULTADO_GERA_NOVA_SOLIC).getVlrNumerico().intValue();
		
		if(elemento.getId().getPclCalSeq() == pclCalSeq 
				&& elemento.getResultadoCodificado().getId().getGtcSeq() == gtcSeq
				&& elemento.getResultadoCodificado().getId().getSeqp() == rcdSeqp 
				&& !elemento.getAnulacaoLaudo()) {
			//insert em ael_solicitacao_exames conforme a procedure.
			AelSolicitacaoExames solicitacaoExame = this.getAelSolicitacaoExameDAO().obterPeloId(
					elemento.getItemSolicitacaoExame().getId().getSoeSeq());
			
			AelSolicitacaoExames solicitacaoExameNew = new AelSolicitacaoExames();
			solicitacaoExameNew.setUnidadeFuncional(solicitacaoExame.getUnidadeFuncional());
			solicitacaoExameNew.setAtendimento(solicitacaoExame.getAtendimento());
			solicitacaoExameNew.setRecemNascido(solicitacaoExame.getRecemNascido());
			solicitacaoExameNew.setServidorResponsabilidade(solicitacaoExame.getServidorResponsabilidade());
			solicitacaoExameNew.setInformacoesClinicas(solicitacaoExame.getInformacoesClinicas());
			solicitacaoExameNew.setConvenioSaudePlano(solicitacaoExame.getConvenioSaudePlano());
			solicitacaoExameNew.setUnidadeFuncionalAreaExecutora(solicitacaoExame.getUnidadeFuncionalAreaExecutora());
			solicitacaoExameNew.setUsaAntimicrobianos(solicitacaoExame.getUsaAntimicrobianos());
			solicitacaoExameNew.setIndTransplante(solicitacaoExame.getIndTransplante());
			solicitacaoExameNew.setProjetoPesquisa(solicitacaoExame.getProjetoPesquisa());
			solicitacaoExameNew.setAtendimentoDiverso(solicitacaoExame.getAtendimentoDiverso());
			
			this.getAelSolicitacaoExameDAO().persistir(solicitacaoExameNew);
		}
		
	}

	
	/**
	 * ORADB PROCEDURE AELP_ENFORCE_REE_RULES
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void executarPosInserirEnforce(AelResultadoExame elemento) throws BaseException {
		this.verificarResultadoExame(elemento);
		
	}
	
	
	/**
	 * ORADB PROCEDURE aelk_ree_rn.rn_reep_ver_resul
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void verificarResultadoExame(AelResultadoExame elemento) throws ApplicationBusinessException {
		
		if(elemento.getAnulacaoLaudo()) {
			final String situacao = this.getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_SITUACAO_LIBERADO).getVlrTexto();
			
			Boolean existeResultadosNaoAnulados = this.getAelResultadoExameDAO().existeResultadosNaoAnulados(
					elemento.getId().getIseSoeSeq(), elemento.getId().getIseSeqp());
			
			if(!existeResultadosNaoAnulados) {
				Boolean existeSolicitacaoExamePorSitucao = this.getAelItemSolicitacaoExameDAO()
					.verificarExisteSolicitacaoExamePorSitucao(elemento.getId().getIseSoeSeq(), 
							elemento.getId().getIseSeqp(), situacao);
				
				if(existeSolicitacaoExamePorSitucao) {
					throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_02551);
				}
			}
		}
		
	}
	
	
	/**
	 * ORADB PROCEDURE aelk_ree_rn.rn_reep_ver_anulacao
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException 
	 */
	public void verificarAnulacao(AelResultadoExame elemento, AelResultadoExame original) throws ApplicationBusinessException {
		
		if(original == null){
			original = this.getAelResultadoExameDAO().obterOriginal(elemento);
		}
		
		if(CoreUtil.modificados(elemento.getAnulacaoLaudo(), original.getAnulacaoLaudo()) 
				&& !elemento.getAnulacaoLaudo()) {
			throw new ApplicationBusinessException(AelResultadoExameRNExceptionCode.AEL_00807);
		}
		
	}
	
	
	/**
	 * ORADB PROCEDURE aelk_ree_rn.rn_reep_atu_sumario
	 * 
	 * @param elemento
	 */
	public void verificarAnulacaoSumario(AelResultadoExame elemento, AelResultadoExame original) {
		
		if(original == null){
			original = this.getAelResultadoExameDAO().obterOriginal(elemento);			
		}
		
		if(CoreUtil.modificados(elemento.getAnulacaoLaudo(), original.getAnulacaoLaudo())
				&& elemento.getAnulacaoLaudo()) {
			AelItemSolicitacaoExames solicitacaoExames = elemento.getItemSolicitacaoExame();
			
			if(solicitacaoExames != null && solicitacaoExames.getDataImpSumario() != null) {
				solicitacaoExames.setDataImpSumario(null);
				this.getAelItemSolicitacaoExameDAO().atualizar(solicitacaoExames);
			}
		}
	}
	
	
	/**
	 * ORADB PROCEDURE AELP_ENFORCE_REE_RULES
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void executarPosAtualizarEnforce(AelResultadoExame elemento, AelResultadoExame original, String nomeMicrocomputador) throws BaseException {
		if(original == null){
			original = this.getAelResultadoExameDAO().obterOriginal(elemento);			
		}
		
		if(CoreUtil.modificados(elemento.getAnulacaoLaudo(), original.getAnulacaoLaudo())
				&& elemento.getAnulacaoLaudo()) {
			
			//RN1 de 6.3 - Procedure TFormDigitacaoExame.Exclusao
			this.getAelResultadosExamesON().anularResultado(elemento.getId().getIseSoeSeq(), 
					elemento.getId().getIseSeqp(), Boolean.FALSE, nomeMicrocomputador, null);
		}
		
	}
	
	
	/**
	 * Se foi modificado o valor de ind_anulacao_laudo
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException  
	 */
	private void verificarAnulacaoLaudo(AelResultadoExame elemento, AelResultadoExame original) throws ApplicationBusinessException {
		if(original == null){
			original = this.getAelResultadoExameDAO().obterOriginal(elemento);			
		}
		
		if(CoreUtil.modificados(elemento.getAnulacaoLaudo(), original.getAnulacaoLaudo())
				&& elemento.getAnulacaoLaudo()) {
			verificarTempoLiberacao(elemento);
		}
	}
	
	
	private Integer obterValorResultadoCaracteristica(AelResultadoExame elemento) {
		if(elemento == null || elemento.getResultadoCaracteristica() == null) {
			return null;
		} else {
			return elemento.getResultadoCaracteristica().getSeq();
		}
	}
	
	
	private Integer obterValorGtcSeqResultadoCodificado(AelResultadoExame elemento) {
		if(elemento == null || elemento.getResultadoCodificado() == null) {
			return null;
		} else {
			return elemento.getResultadoCodificado().getId().getGtcSeq();
		}
	}
	
	
	private Integer obterValorSeqpResultadoCodificado(AelResultadoExame elemento) {
		if(elemento == null || elemento.getResultadoCodificado() == null) {
			return null;
		} else {
			return elemento.getResultadoCodificado().getId().getSeqp();
		}
	}
	

	/** GET **/
	protected AelResultadoExameDAO getAelResultadoExameDAO() {
		return aelResultadoExameDAO;
	}
	
	protected AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}
	
	protected AelResultadoCaracteristicaDAO getAelResultadoCaracteristicaDAO() {
		return aelResultadoCaracteristicaDAO;
	}

	protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return aelResultadoCodificadoDAO;
	}
	
	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
		
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected AelResultadosExamesON getAelResultadosExamesON() {
		return aelResultadosExamesON;
	}
}
