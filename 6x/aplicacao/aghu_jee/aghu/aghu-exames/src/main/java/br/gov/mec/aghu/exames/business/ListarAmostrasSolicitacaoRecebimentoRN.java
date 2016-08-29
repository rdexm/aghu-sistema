package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.dao.AelAmostraCtrlEtiquetasDAO;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoDAO;
import br.gov.mec.aghu.exames.dao.AelCadGuicheDAO;
import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.dao.AelExameApItemSolicDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelItemConfigExameDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.dao.AelUnidExecUsuarioDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAmostrasId;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AelExtratoAmostras;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ORADB PROCEDURES AELP_RECEBE_AMOSTRA e AELP_VOLTAR_SIT_AMOSTRA
 * @author aghu
 *
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength", "PMD.JUnit4TestShouldUseTestAnnotation"})
@Stateless
public class ListarAmostrasSolicitacaoRecebimentoRN extends BaseBusiness {

	private static final String _HIFEN_ = " - ";

	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;
	
	@Inject
	private AelCadGuicheDAO aelCadGuicheDAO;
	
	@Inject
	private AelAmostraCtrlEtiquetasDAO aelAmostraCtrlEtiquetasDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelExtratoAmostrasDAO aelExtratoAmostrasDAO;
	
	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@Inject
	private AelItemConfigExameDAO aelItemConfigExameDAO;
	
	@Inject
	private AelUnidExecUsuarioDAO aelUnidExecUsuarioDAO;
	
	@Inject
	private AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO;
	
	@EJB
	private AelAmostrasON aelAmostrasON;
	
	@EJB
	private ReceberTodasAmostrasSolicitacaoRN receberTodasAmostrasSolicitacaoRN;
	
	@EJB
	private TratarProtocoloUnicoRN tratarProtocoloUnicoRN;
	
	@EJB
	private AelAmostraItemExamesRN aelAmostraItemExamesRN;
	
	private static final Log LOG = LogFactory.getLog(ListarAmostrasSolicitacaoRecebimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelExameApDAO aelExameApDAO;

	@Inject
	private AelExameApItemSolicDAO aelExameApItemSolicDAO;

	@EJB
	private VoltarTodasAmostrasSolicitacaoRN voltarTodasAmostrasSolicitacaoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5220148615023412795L;

	/*
	 * Determina o tamanho obrigatório do número do frasco. Verificar a possibilidade de retornar este valor do banco;
	 */
	public final static int QUANTIDADE_CARACTERES_OBRIGATORIOS_NRO_FRASCO = 8;

	public enum ListarAmostrasSolicitacaoRecebimentoRNExceptionCode implements BusinessExceptionCode {
		AEL_01969, AEL_01975, AEL_02720, AEL_02291, AEL_01087, AEL_01086, AEL_01089, AEL_01974, AEL_02722,;
	}

	/**
	 * Compara a unidade executora com a unidade funcional das amostras
	 */
	public boolean validarUnidadeFuncionalAmostra(Short seqpAmostra, AghUnidadesFuncionais unidadeFuncionalItem, AghUnidadesFuncionais unidadeExecutora) throws BaseException {

		// Testa se a "unidade funcional do item" equivale a "unidade executora atual"
		if (!unidadeFuncionalItem.getSeq().equals(unidadeExecutora.getSeq())) {
			return false;
		}

		return true;
	}

	/**
	 * ORADB PROCEDURE AELP_RECEBE_AMOSTRA
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @param numeroApOrigem
	 * @param configExameOrigem 
	 * @throws BaseException
	 */
	public ImprimeEtiquetaVO receberAmostra(
			final AghUnidadesFuncionais unidadeExecutora,
			AelAmostras amostra, final String nroFrascoFabricante,
			final List<ExameAndamentoVO> listaExamesAndamento, String nomeMicrocomputador) throws BaseException {
		ImprimeEtiquetaVO vo = null;
		int qtdAmostras;

		// Valida parametros obrigatorios
		CoreUtil.validaParametrosObrigatorios(unidadeExecutora, amostra);
		
		amostra = getAelAmostrasDAO().obterPorChavePrimaria(amostra.getId());

		// Unidade de Coleta
		if (this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA)) {
			qtdAmostras = this.receberAmostraUnidadeColeta(unidadeExecutora, amostra, nomeMicrocomputador);
			vo = new ImprimeEtiquetaVO();
			vo.setQtdAmostras(qtdAmostras);
		} else
		// Unidade Central Recebimento Materiais
		if (this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS)) {
			vo = this.receberAmostraUnidadeCentralRecebimentoMateriais(unidadeExecutora, amostra, nroFrascoFabricante, nomeMicrocomputador);
		} else
		// Unidade Executora Exames e/ou Unidade Patologica
		if (this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES)) {
			vo = this.receberAmostraUnidadeExecutoraExames(unidadeExecutora, amostra, nroFrascoFabricante, listaExamesAndamento, nomeMicrocomputador);
			
			/**
			 * código retirado para estória #35139
			 
			// atualiza o número do AP de origem (quando houver)
			if (numeroApOrigem != null) {
				this.atualizarNumeroApOrigemAelExameAp(vo.getNroAp(), numeroApOrigem);	
			} */
		}


		return vo;
	}

	/**
	 * ORADB PROCEDURE AELP_RECEBE_AMOSTRA - Unidade Coleta Se "unidade executora" for uma "unidade de coleta" os "itens de amostra de exame" G (Gerados) ou C (Coletados) devem
	 * receber a situacao U (Recebida Unidade de Coleta
	 * 
	 * @param amostra
	 * @throws BaseException
	 */
	public Integer receberAmostraUnidadeColeta(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, String nomeMicrocomputador) throws BaseException {

		int quantidadeAmostrasRecebidasUnidade = 0;
		int qtdAmostras = 0;
		//String nomeImpressora = null;

		// Percorre "itens de amostra de exame"
		for (final AelAmostraItemExames amostraItemExame : amostra.getAelAmostraItemExames()) {

			// Verifica "itens de amostra de exame" G (Gerado) ou C (Coletado)
			if (DominioSituacaoAmostra.G.equals(amostraItemExame.getSituacao()) || DominioSituacaoAmostra.C.equals(amostraItemExame.getSituacao())) {
				
				// Seta a situacao do "item de amostra de exame" para U
				// (Recebida Unidade de Coleta)
				//amostraItemExame.setSituacao(DominioSituacaoAmostra.U);

				this.atualizarAelAmostraItemExames(amostraItemExame, DominioSituacaoAmostra.U, true, nomeMicrocomputador);

				quantidadeAmostrasRecebidasUnidade++;
			}

		}

		// Caso NENHUMA amostra recebida
		if (quantidadeAmostrasRecebidasUnidade == 0) {
			// Nenhuma amostra a receber
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01086, amostra.getId().getSeqp(), unidadeExecutora.getSeq() + _HIFEN_
					+ unidadeExecutora.getDescricao());
		}
		
		/*AghMicrocomputador microcomputador = administracaoFacade.obterMicrocomputadorPorNome(nomeMicrocomputador);
		if (microcomputador != null) {
			if (microcomputador.getImpressoraEtiquetas() == null) {
				nomeImpressora = microcomputador.getImpressoraEtiquetas();				
			}
		}*/			

		// Imprime automaticamente as etiquetas de uma amostra recebida
		qtdAmostras = this.imprimirEtiquetaAmostra(amostra, unidadeExecutora, nomeMicrocomputador);
		
		return qtdAmostras;

	}

	/**
	 * ORADB PROCEDURE AELP_RECEBE_AMOSTRA - Unidade Executora Exames e/ou Unidade Patologica
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @param numeroApOrigem
	 * @param configExameOrigem 
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	protected ImprimeEtiquetaVO receberAmostraUnidadeExecutoraExames(
			final AghUnidadesFuncionais unidadeExecutora,
			final AelAmostras amostra, final String nroFrascoFabricante,
			final List<ExameAndamentoVO> listaExamesAndamento, String nomeMicrocomputador) throws BaseException {

		final boolean isUnidadePatologia = this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_PATOLOGIA);

		Boolean isImprimirFicha = Boolean.FALSE;
		//Boolean isLaudoUnico = Boolean.FALSE;
		final ImprimeEtiquetaVO vo = new ImprimeEtiquetaVO();
		String nomeImpressora = null;
		
		// Cria "amostra solicitação seqp " ANTERIOR. Vide literal de AEL_AMOSTRA_ITEM_EXAMES_ID: AMO_SOE_SEQ + "." + AMO_SEQP
		final Integer nroUnicoAmostraTela = amostra.getNroUnico();
		final Date dtNumeroUnicoAmostraTela = amostra.getDtNumeroUnico();
		
		// 1. Verifica se a amostra requer a informacao do numero do frasco do
		// fornecedor
		this.validarNumeroFrasco(amostra, nroFrascoFabricante);

		//twickert - 28/04/2014
		//Esse código foi comentado pq não deve mais ser verificado se deve informar ap de origem
		// 2. "Unidade Executora Patologica"
		//if (isUnidadePatologia) {
			// Valida número AP de Origem
			//this.validarNumeroApOrigem(amostra, configExameOrigem, numeroApOrigem);
		//}
		
		verificaStatusExamePendente(amostra);

		// 3. Verifica "itens de amostra de exame" com "itens de solicitacao de exame" com a situacao PE (Pendente)
		final List<AelAmostraItemExames> listaAmostraItemExamesPendentes = getAelAmostraItemExamesDAO().buscarAmostrasSolicitacaoRecebimento(amostra);

		// TODO 3.1. Envia avisos relacionados ao processo de recebimento de amostras
		// this.enviaAvisosRelacionadosProcessoRecebimentoAmostra(listaAelAmostraItemExames);
		
		// 3.2 Atualiza "itens de amostra de exame" para situacao R (Recebido)
		if (listaAmostraItemExamesPendentes != null && !listaAmostraItemExamesPendentes.isEmpty()) {
			this.atualizarAmostraItensExameRecebidoUnidadeExecutoraExames(unidadeExecutora, amostra, listaAmostraItemExamesPendentes, nomeMicrocomputador);
		}

		// LOOP para verificar se é pra imprimir
		for (final AelAmostraItemExames amostraItemExame : listaAmostraItemExamesPendentes) {

			if (Boolean.FALSE.equals(isImprimirFicha)) {
				final AelUnfExecutaExames unfExecutaExames = getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(
						amostraItemExame.getAelItemSolicitacaoExames().getExame().getSigla(), amostraItemExame.getAelItemSolicitacaoExames().getMaterialAnalise().getSeq(),
						unidadeExecutora.getSeq());

				if (unfExecutaExames != null) {
					//					if (Boolean.TRUE.equals(unfExecutaExames.getIndImprimeFicha())) {
					isImprimirFicha = Boolean.TRUE.equals(unfExecutaExames.getIndImprimeFicha());
					//					}

					/*if (Boolean.TRUE.equals(unfExecutaExames.getIndLaudoUnico().isSim())) {
						isLaudoUnico = Boolean.TRUE;
					}*/

				}
			}
		}

		if (isUnidadePatologia) {
			// AELP_TESTA_NRO_AP_ANT -- faz o mesmo que
			// AELP_VALIDA_NRO_AP_ORIGEM e adionalmente atualiza o
			// numeroApOrigem na ael_item_solicitacao_exames			
//			this.testarNumeroApAnterior(amostra, numeroApOrigem, nomeMicrocomputador, servidorLogado);
			
			
//			this.atualizarNumeroApOrigemAelExameAp(vo.getNroAp(), (long) numeroApOrigem);
			// AELP_ASSOCIA_NUMERO_AP;			
			this.associarNumeroApPorGrupo(amostra, nomeMicrocomputador);
		}

		// Verifica a necessidade de interfaceamento
		this.verificarModoInterfaceamentoSemCancelamento(amostra, nomeMicrocomputador);

		// TODO AELP_VERIFICA_NRO_UNICO

		if (Boolean.TRUE.equals(isImprimirFicha)) {
			getReceberTodasAmostrasSolicitacaoRN().aelpImprimeFichaTrabalho(vo, amostra.getId().getSoeSeq(), amostra.getId().getSeqp());

			if (isUnidadePatologia) {
				getReceberTodasAmostrasSolicitacaoRN().aelpImprimeEtiqAp(vo, amostra.getSolicitacaoExame().getSeq(), amostra.getId().getSeqp(), unidadeExecutora.getSeq());
			}
		}

		nomeImpressora = this.getSolicitacaoExameFacade().obterNomeImpressoraEtiquetas(nomeMicrocomputador);				
		
		// Verificação do número único: AELP_VERIFICA_NRO_UNICO
		this.verificarNroUnicoSolicitacao(vo, unidadeExecutora, nroUnicoAmostraTela, dtNumeroUnicoAmostraTela, amostra, nomeImpressora);

		return vo;
	}
	
	/**
	 * Baseado na amostra pesquisa os itens caso algum esteja como PENDENTE,
	 * ejeta uma exception
	 * @author ndeitch
	 * @param amostra
	 * @throws AGHUNegocioExceptionSemRollback
	 */
	public void verificaStatusExamePendente(
			final AelAmostras amostra)
			throws ApplicationBusinessException {
		
		final List<AelAmostraItemExames> listaAmostraItemExamesPendentes = getAelAmostraItemExamesDAO().buscarAmostrasSolicitacaoRecebimento(amostra);
		
		if (listaAmostraItemExamesPendentes != null && !listaAmostraItemExamesPendentes.isEmpty()) {

			// Obtém o parametro do sistema para situacao PE (Pendente)
			String codigoSituacaoPendente = null;
			try {
				codigoSituacaoPendente = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_PENDENTE).getVlrTexto();
			} catch (final ApplicationBusinessException e) {
				LOG.error("Exceção capturada:", e);
			}

			// Percorre "itens de amostra de exame"
			for (final AelAmostraItemExames amostraItemExamesPendente : listaAmostraItemExamesPendentes) {

				// Obtém o "item de solicitacao de exame" do
				// "item de amostra de exame"
				final AelItemSolicitacaoExames itemSolicitacaoExames = amostraItemExamesPendente.getAelItemSolicitacaoExames();

				// Verifica se o "item de solicitacao de exame" possui a
				// situacao PE (Pendente)
				if (codigoSituacaoPendente.equalsIgnoreCase(itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo())) {
						throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_02291);
				}

			}
		}
	}

	// @ORADB AELP_ASSOCIA_NUMERO_AP@AELF_RECEBER_AMOSTRA.pll
	// alterado por pedido dos médicos da patologia para gerar 1 numero AP para
	// cada grupo de exames
	// cada grupo de exames
	public void associarNumeroApPorGrupo(final AelAmostras amostra,
			String nomeMicrocomputador)
			throws BaseException {
		// atualizar nr dos item da amostra
//		for (AelAmostraItemExames aelAmostraItemExames : amostra.getAelAmostraItemExames()) {
//			AelItemSolicitacaoExames itemAmostra = aelAmostraItemExames.getAelItemSolicitacaoExames();
//			
//			// O número AP só é calculado se o item de amostra ou cirurgia
//			// associada a este não possuirem um.
//			if (itemAmostra.getAelExameApItemSolic().isEmpty()) {
//				
//				//if (getSolicitacaoExameFacade().verificarCodigoSituacaoNumeroAP(itemAmostra, itemSolicitacaoExameOriginal)) { nao precisa mais desse teste
//
//				//getSolicitacaoExame2Facade().atualizarLaudoUnico(itemAmostra, servidorLogado);
//				//}				
//			}
//
//		}

		getAelItemSolicitacaoExameDAO().flush();
	}	

	protected AelItemConfigExameDAO getAelItemConfigExameDAO() {
		return aelItemConfigExameDAO;
	}
	
	/**
	 * ORADB PROCEDURE AELP_RECEBE_AMOSTRA - Central Recebimento Materiais
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @throws BaseException
	 */
	public ImprimeEtiquetaVO receberAmostraUnidadeCentralRecebimentoMateriais(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, final String nroFrascoFabricante, String nomeMicrocomputador)
	throws BaseException {

		Boolean impFicha = Boolean.FALSE;
		ImprimeEtiquetaVO imprimeEtiquetaVO = new ImprimeEtiquetaVO();
		String nomeImpressora = null;
		
		// Cria "amostra solicitação seqp " ANTERIOR. Vide literal de AEL_AMOSTRA_ITEM_EXAMES_ID: AMO_SOE_SEQ + "." + AMO_SEQP
		final Integer nroUnicoAmostraTela = amostra.getNroUnico();
		final Date dtNumeroUnicoAmostraTela = amostra.getDtNumeroUnico();
		
		// 1. Verifica se a amostra requer a informacao do numero do frasco do fornecedor
		this.validarNumeroFrasco(amostra, nroFrascoFabricante);

		// 2. Verifica "itens de amostra de exame" com "itens de solicitacao de exame" com a situacao PE (Pendente)
		final List<AelAmostraItemExames> listaAmostraItemExamesPendentes = getAelAmostraItemExamesDAO().buscarAmostrasSolicitacaoRecebimentoCentralRecebimentoMateriais(amostra,
				unidadeExecutora);

		// TODO 2.1 Envia avisos relacionados ao processo de recebimento de amostras
		// this.enviaAvisosRelacionadosProcessoRecebimentoAmostra(listaAelAmostraItemExames);

		// 2.2 Atualiza "itens de amostra de exame" para situacao R (Recebido)
		boolean isRecebeuAmostraUnidade = false;
		
		// Percorre "itens de amostra de exame"
		for (final AelAmostraItemExames amostraItemExamePendente : listaAmostraItemExamesPendentes) {

			// Seta a situacao do "item de amostra de exame" para "Recebida"
			//amostraItemExamePendente.setSituacao(DominioSituacaoAmostra.R);
			this.atualizarAelAmostraItemExames(amostraItemExamePendente, DominioSituacaoAmostra.R, false, nomeMicrocomputador);
			isRecebeuAmostraUnidade = true;

			final AelUnfExecutaExames unfExecutaExames = getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(
					amostraItemExamePendente.getAelItemSolicitacaoExames().getExame().getSigla(),
					amostraItemExamePendente.getAelItemSolicitacaoExames().getMaterialAnalise().getSeq(), 
					amostraItemExamePendente.getAelItemSolicitacaoExames().getUnidadeFuncional().getSeq());
		
			if (unfExecutaExames != null) {
				if (Boolean.TRUE.equals(unfExecutaExames.getIndImprimeFicha())) {
						impFicha  = Boolean.TRUE;
				}
			}
		}
		
		// Caso NENHUMA amostra recebida
		if (!isRecebeuAmostraUnidade) {
			// Nenhuma amostra a receber
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01086, amostra.getId().getSeqp(), unidadeExecutora.getSeq() + _HIFEN_
					+ unidadeExecutora.getDescricao());
		}
		else {
			if(impFicha) {
				aelpImprimeFichaTrabalho(imprimeEtiquetaVO, amostra.getId().getSoeSeq(), amostra.getId().getSeqp());				
			}
		}

		// Atualização do NÚMERO DO GUICHÊ
		this.atualizarGuiche(amostra, unidadeExecutora);

		// Verifica a necessidade de interfaceamento
		this.verificarModoInterfaceamentoSemCancelamento(amostra, nomeMicrocomputador);

		// Verificação do número único: AELP_VERIFICA_NRO_UNICO
		nomeImpressora = this.getSolicitacaoExameFacade().obterNomeImpressoraEtiquetas(nomeMicrocomputador);				

		this.verificarNroUnicoSolicitacao(imprimeEtiquetaVO, unidadeExecutora, nroUnicoAmostraTela, dtNumeroUnicoAmostraTela, amostra, nomeImpressora);
		
		return imprimeEtiquetaVO;

	}

	/**
	 * Atualização do número do GUICHÊ
	 * @param amostra
	 * @throws BaseException
	 */
	public void atualizarGuiche(AelAmostras amostra, final AghUnidadesFuncionais unidadeExecutora) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelAmostras amostraAtualizada = this.getAelAmostrasDAO().obterPorChavePrimaria(amostra.getId());
		
		final AelCadGuiche guiche = getAelCadGuicheDAO().obterAelCadGuichePorUsuarioUnidadeExecutora( unidadeExecutora, 
				servidorLogado.getUsuario(), DominioSituacao.A, null);
		amostra.setGuiche(guiche);
		getAelAmostrasON().persistirAelAmostra(amostraAtualizada, false);
	}
	
	/**
	 * ORADB PROCEDURE AELP_VERIF_NRO_UNICO
	 * @param unidadeExecutora
	 * @param nroUnicoAmostraTela
	 * @param dtNumeroUnicoAmostraTela
	 * @param amostra
	 * @throws BaseException
	 */
	private void verificarNroUnicoSolicitacao(ImprimeEtiquetaVO imprimeEtiquetaVO, final AghUnidadesFuncionais unidadeExecutora, final Integer nroUnicoAmostraTela, 
			final Date dtNumeroUnicoAmostraTela, final AelAmostras amostra, String nomeImpressora) throws BaseException {
		
		AelAmostras amostraAtualizada = this.getAelAmostrasDAO().obterPorChavePrimaria(amostra.getId()); // Cursor C_NRO_UNICO
		
		// Verifica se há diferença entre o número único (número e data) da tela e o número único do banco
		if(!CoreUtil.igual(nroUnicoAmostraTela, amostraAtualizada.getNroUnico()) || !CoreUtil.igual(dtNumeroUnicoAmostraTela, amostraAtualizada.getDtNumeroUnico())){
			this.getSolicitacaoExameFacade().gerarEtiquetas(amostraAtualizada.getSolicitacaoExame(), amostraAtualizada.getId().getSeqp(), unidadeExecutora, nomeImpressora, null);
			imprimeEtiquetaVO.setQtdEtiquetas(1);
		}
		
	}

	
	/**
	 * ORADB Procedure AELP_IMPRIME_FICHA_TRABALHO
	 * 
	 * @param soeSeq
	 * @param unfSeq
	 * @throws BaseException
	 */
	
	public ImprimeEtiquetaVO aelpImprimeFichaTrabalho(final ImprimeEtiquetaVO vo, final Integer soeSeq, final Short seqp) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AghUnidadesFuncionais unidadeFuncional = null;
		AghUnidadesFuncionais unidadeFuncionalExecutora = null;
		AelUnidExecUsuario usuarioUnidadeExecutora = getAelUnidExecUsuarioDAO().obterPeloId(servidorLogado.getId());
		AelAmostras amostra = getAelAmostrasDAO().obterPorChavePrimaria(new AelAmostrasId(soeSeq, seqp));

		if(usuarioUnidadeExecutora != null) {
			unidadeFuncionalExecutora = usuarioUnidadeExecutora.getUnfSeq();
		}
		 
		if(unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS)) {
			unidadeFuncional = amostra.getUnidadesFuncionais();
		}
		else {
			unidadeFuncional = unidadeFuncionalExecutora;
		}
		
		if(unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_COLETA)) {
			return null;
		}
		else {
			
			if((unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_RADIOIMUNOENSAIO)) 
					|| (unidadeFuncional != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_RADIOIMUNOENSAIO))) { //NOPMD
				/*
				 * "Conforme as consultoras Carmelinda Adriana e Rosane da CGTI, a Ficha de Trabalho Radioimunoensaio não é mais utilizada no HCPA."
				 */
				//IMPRIMIR AELR_FICHA_TRAB_RAD
			}
			else if((unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_IMUNOLOGIA)) 
					|| (unidadeFuncional != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_IMUNOLOGIA))) {//NOPMD
				//TODO IMPRIMIR AELR_FICHA_TRAB_IMU
			}
			else if((unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_PATOLOGIA)) 
					|| (unidadeFuncional != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_PATOLOGIA))) {
				vo.setSoeSeq(soeSeq);
				vo.setUnfSeq(unidadeFuncional.getSeq());
				vo.setImprimirFichaTrabLab(Boolean.FALSE);
				vo.setImprimirFichaTrabPat(Boolean.TRUE);
				vo.setImprimir(Boolean.TRUE);
			}
			else if((unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(),ConstanteAghCaractUnidFuncionais.IMPRIME_FICHA_POR_AMOSTRA)) 
					|| (unidadeFuncional != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(),ConstanteAghCaractUnidFuncionais.IMPRIME_FICHA_POR_AMOSTRA))) { 
				vo.setSoeSeq(soeSeq);
				vo.setUnfSeq(unidadeFuncional.getSeq());
				vo.setAmoSeqP(seqp);
				vo.setImprimirFichaTrabLab(Boolean.FALSE);
				vo.setImprimirFichaTrabPat(Boolean.FALSE);
				vo.setImprimirFichaTrabAmo(Boolean.TRUE);
				vo.setImprimir(Boolean.TRUE);
			}
			else if((unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(),ConstanteAghCaractUnidFuncionais.IMPRIME_FICHA_POR_EXAME)) 
					|| (unidadeFuncional != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(),ConstanteAghCaractUnidFuncionais.IMPRIME_FICHA_POR_EXAME))) {
				vo.setSoeSeq(soeSeq);
				vo.setUnfSeq(unidadeFuncional.getSeq());
				vo.setAmoSeqP(seqp);
				vo.setReceberAmostra(true);
				vo.setImprimirFichaTrabLab(Boolean.TRUE);
				vo.setImprimirFichaTrabPat(Boolean.FALSE);
				vo.setImprimir(Boolean.TRUE);
			}
			else { 
				vo.setSoeSeq(soeSeq);
				vo.setUnfSeq(unidadeFuncional.getSeq());
				vo.setAmoSeqP(seqp);
				vo.setImprimirFichaTrabLab(Boolean.FALSE);
				vo.setImprimirFichaTrabPat(Boolean.FALSE);
				vo.setImprimirFichaTrabAmo(Boolean.TRUE);
				vo.setImprimir(Boolean.TRUE);
			}
		}
		
		return vo;
	}

	/**
	 * Verifica se a "unidade funcional executora de exames" requer o indicador do numero do frasco fornecedor"
	 * 
	 * @param amostra
	 * @return
	 * @throws BaseException
	 */
	public boolean exigeIndicarNumeroFrascoFornecedor(final AelAmostras amostra) throws BaseException {

		// Percorre "itens de amostra de exame"
		for (final AelAmostraItemExames amostraItemExame : amostra.getAelAmostraItemExames()) {

			// Obtém o "item de solicitacao de exame" do
			// "item de amostra do exame"
			final AelItemSolicitacaoExames itemSolicitacaoExame = amostraItemExame.getAelItemSolicitacaoExames();

			// Obtém a "unidade funcional executora de exames" no
			// "item de solicitacao de exame" do "item de amostra do exame"
			final AelUnfExecutaExames unfExecutaExames = getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(itemSolicitacaoExame.getExame(),
					itemSolicitacaoExame.getMaterialAnalise(), itemSolicitacaoExame.getUnidadeFuncional());

			// Verifica se a "unidade funcional executora de exames" requer o
			// "indicador do numero do frasco fornecedor"
			final boolean isIndicarNumeroFrascoFornecedor = unfExecutaExames.getIndNroFrascoFornec();

			// Unica ocorrencia "indicador do numero do frasco fornecedor" sera
			// o suficiente para indicar a exigencia do mesmo
			if (isIndicarNumeroFrascoFornecedor) {
				return true;
			}
		}

		return false;

	}

	/**
	 * ORADB AELP_TESTA_NRO_FRASCO Verifica se a amostra requer a informacao do numero do frasco do fornecedor
	 */
	public void validarNumeroFrasco(final AelAmostras amostra, final String nroFrascoFabricante) throws BaseException {

		// Verifica se a "unidade funcional executora de exames" requer o
		// "indicador do numero do frasco fornecedor"
		final boolean isIndicarNumeroFrascoFornecedor = exigeIndicarNumeroFrascoFornecedor(amostra);
		
		// Seqp para identificar a amostra na mensagem de erro
		final Short exceptionSeqp = amostra.getId().getSeqp();

		// Se exigido o "indicador do numero do frasco fornecedor" e o
		// "numero do frasco da amostra" esta vazio
		if (isIndicarNumeroFrascoFornecedor && StringUtils.isEmpty(nroFrascoFabricante)) {
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01969, exceptionSeqp);
		}

		// Se exigido o "indicador do numero do frasco fornecedor" e tamanho do
		// "numero do frasco da amostra" NAO contem exatos 8 caracteres
		if (isIndicarNumeroFrascoFornecedor && StringUtils.isNotEmpty(nroFrascoFabricante) && nroFrascoFabricante.length() != QUANTIDADE_CARACTERES_OBRIGATORIOS_NRO_FRASCO) {
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01975, exceptionSeqp);
		}

		// Seta numero do frasco do fabricante na amostra
		amostra.setNroFrascoFabricante(nroFrascoFabricante);

	}

	/**
	 * ORADB AELP_VALIDA_NRO_AP_ORIGEM@AELF_RECEBER_AMOSTRA.pll Verifica o numero AP de origem informado pelo usuário.
	 * 
	 * @param amostra
	 * @param configExameOrigem 
	 * @throws ApplicationBusinessException
	 */
	/*public void validarNumeroApOrigem(final AelAmostras amostra,
			AelConfigExLaudoUnico configExameOrigem,
			final Long numeroApOrigem)
			throws ApplicationBusinessException {
		// Indica a exigencia do "indicador do numero do frasco fornecedor"
		DominioApAnterior isIndicadorNumeroApAnteriorObrigatorio = null;

		// Percorre "itens de amostra de exame"
		for (final AelAmostraItemExames amostraItemExame : amostra.getAelAmostraItemExames()) {

			// Obtém o "item de solicitacao de exame" do
			// "item de amostra do exame"
			final AelItemSolicitacaoExames itemSolicitacaoExame = amostraItemExame.getAelItemSolicitacaoExames();

			// Obtém a "unidade funcional executora de exames" no
			// "item de solicitacao de exame" do "item de amostra do exame"
			final AelUnfExecutaExames unfExecutaExames = getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(itemSolicitacaoExame.getExame(),
					itemSolicitacaoExame.getMaterialAnalise(), itemSolicitacaoExame.getUnidadeFuncional());

			// Verifica se a "unidade funcional executora de exames" requer o
			// "indicador do numero do frasco fornecedor"
			isIndicadorNumeroApAnteriorObrigatorio = unfExecutaExames.getIndNumApAnterior();

			// Unica ocorrencia da obrigatoriedade do
			// "indicador do numero ap anterior" sera o suficiente
			if (DominioApAnterior.O.equals(isIndicadorNumeroApAnteriorObrigatorio) || DominioApAnterior.P.equals(isIndicadorNumeroApAnteriorObrigatorio)) {
				if (DominioApAnterior.O.equals(isIndicadorNumeroApAnteriorObrigatorio) && numeroApOrigem == null) {
					throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_02720);
				}
				if (numeroApOrigem != null) {
					final boolean existeAp = getAelAnatomoPatologicoDAO().verificarAelAnatomoPatologicoByNumeroAp(configExameOrigem, numeroApOrigem);
					if (!existeAp) {
						throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_02722);
					}
				}
			}
		}
	}*/

	/**
	 * Atualiza "itens de amostra de exame" para situacao R (Recebido)
	 * 
	 * @param amostra
	 */
	public void atualizarAmostraItensExameRecebidoUnidadeExecutoraExames(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra,
			final List<AelAmostraItemExames> listaAelAmostraItemExames, String nomeMicrocomputador) throws BaseException {

		boolean isRecebeuAmostraUnidade = false;

		// Percorre "itens de amostra de exame"
		for (final AelAmostraItemExames amostraItemExame : listaAelAmostraItemExames) {
			
			// Compara a unidade executora com a unidade funcional das amostras
			if (this.validarUnidadeFuncionalAmostra(amostra.getId().getSeqp(), amostraItemExame.getAelItemSolicitacaoExames().getUnidadeFuncional(), unidadeExecutora)) {

				// Seta a situacao do "item de amostra de exame" para "Recebida"
				//amostraItemExame.setSituacao(DominioSituacaoAmostra.R);
				this.atualizarAelAmostraItemExames(amostraItemExame, DominioSituacaoAmostra.R, true, nomeMicrocomputador);

				isRecebeuAmostraUnidade = true;
			}

		}

		// Caso NENHUMA amostra recebida
		if (!isRecebeuAmostraUnidade) {
			// Nenhuma amostra a receber
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01086, amostra.getId().getSeqp(), unidadeExecutora.getSeq() + _HIFEN_
					+ unidadeExecutora.getDescricao());
		}

	}

	public void atualizarAelAmostraItemExames(
			AelAmostraItemExames amostraItemExame, DominioSituacaoAmostra novaSituacaoAmostra,
			boolean flush, String nomeMicrocomputador) throws BaseException {

		// Persiste as informacoes
		this.getAelAmostraItemExamesRN().atualizarAelAmostraItemExames(amostraItemExame, novaSituacaoAmostra, flush, true, nomeMicrocomputador);
		
	}

	/**
	 * 
	 * @param amostraItemExame
	 * @param flush
	 * @throws BaseException
	 */
	public void atualizarAelAmostraItemExames(final AelAmostraItemExames amostraItemExame, final Boolean flush, String nomeMicrocomputador) throws BaseException {

		// Persiste as informacoes
		this.atualizarAelAmostraItemExames(amostraItemExame, null, flush, nomeMicrocomputador);

	}

	/**
	 * ORADB PROCEDURE AELP_VOLTAR_SIT_AMOSTRA
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @throws BaseException
	 */
	public boolean voltarAmostra(final AghUnidadesFuncionais unidadeExecutora, AelAmostras amostra, String nomeMicrocomputador) throws BaseException {

		boolean retorno = false;

		// Valida parametros obrigatorios
		CoreUtil.validaParametrosObrigatorios(unidadeExecutora, amostra);
		
		amostra = getAelAmostrasDAO().obterPorChavePrimaria(amostra.getId());

		// Unidade de Coleta
		if (this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA)) {
			retorno = this.voltarAmostraUnidadeColeta(unidadeExecutora, amostra, nomeMicrocomputador);
		}else
		// Unidade Central Recebimento Materiais
		if (this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS)) {
			retorno = this.voltarAmostraUnidadeCentralRecebimentoMateriais(unidadeExecutora, amostra, nomeMicrocomputador);
		}else
		// Unidade Executora Exames
		if (this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES)) {
			retorno = this.voltarAmostraUnidadeExecutoraExames(unidadeExecutora, amostra, nomeMicrocomputador);
		}


		return retorno;

	}

	/**
	 * Atualiza informacoes de um "item de amostra de exames" atraves de um "extrato de amostra" de uma "amostra" cujo recebimento voltou
	 * 
	 * @param amostraItemExame
	 * @param extratoAmostra
	 * @return
	 */
	public boolean atualizarAmostraItemExameVoltouSituacaoExtratoAmostra(final AelAmostraItemExames amostraItemExame, final DominioSituacaoAmostra situacao, String nomeMicrocomputador)
	throws BaseException {

		amostraItemExame.setNroMapa(null);
		amostraItemExame.setDtImpMapa(null);

		// Seta a situacao do "extrado de amostra" no "item de amostra de exames"
		//amostraItemExame.setSituacao(situacao);

		// Persiste informacoes
		this.atualizarAelAmostraItemExames(amostraItemExame, situacao, true, nomeMicrocomputador);
		return true;

	}

	/**
	 * ORADB PROCEDURE AELP_VOLTAR_SIT_AMOSTRA - Unidade Executora Coleta
	 * 
	 * @param amostra
	 * @throws BaseException
	 */
	public boolean voltarAmostraUnidadeColeta(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, String nomeMicrocomputador) throws BaseException {

		// Indica se ocorreu o recebimento de no minimo uma amostra
		int quantidadeAmostrasVoltaramUnidade = 0;

		// Obtém extratos de amostras com a situacao DIFERENTE de U (Recebida
		// Unidade de Coleta) da amostra informada
		final List<AelExtratoAmostras> extratoAmostrasExamesSituacao = this.getAelExtratoAmostrasDAO().buscarExtratosAmostrasPorSituacaoDiferenteInformada(
				amostra.getId().getSoeSeq(), amostra.getId().getSeqp(), DominioSituacaoAmostra.U);

		// Verifica a existencia de itens
		if (extratoAmostrasExamesSituacao != null && !extratoAmostrasExamesSituacao.isEmpty()) {

			// // Pesquisa UPDATE: Pesquisa amostras item exame com a situação igual a U (Recebida Unidade de Coleta)
			List<AelAmostraItemExames> listaAtualizarAmostraItemExame = this.getAelAmostraItemExamesDAO().buscarAmostrasSolicitacaoVoltarAmostraUnidadeColeta(amostra);

			// Percorre os "itens de amostra de exames" da "amostra"
			for (final AelAmostraItemExames amostraItemExameRecebidaUnidadeColeta : listaAtualizarAmostraItemExame) {

				// Atualiza informacoes de um "item de amostra de exames"
				// atraves de um "extrato de amostra"
				this.atualizarAmostraItemExameVoltouSituacaoExtratoAmostra(amostraItemExameRecebidaUnidadeColeta, extratoAmostrasExamesSituacao.get(0).getSituacao(), nomeMicrocomputador);
				quantidadeAmostrasVoltaramUnidade++;
			}

		} else {
			// Informa que a solicitacao de exames gerada pela area executora,
			// nao possui situacao anterior a ser restaurada
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01087);
		}

		// Caso NENHUMA amostra tenha voltado
		if (quantidadeAmostrasVoltaramUnidade == 0) {
			// Nenhuma amostra a receber
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01089, amostra.getId().getSeqp(), unidadeExecutora.getSeq() + _HIFEN_
					+ unidadeExecutora.getDescricao());
		}

		return quantidadeAmostrasVoltaramUnidade > 0;
	}

	/**
	 * ORADB PROCEDURE AELP_VOLTAR_SIT_AMOSTRA - Unidade Executora Exames
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @throws BaseException
	 */
	public boolean voltarAmostraUnidadeExecutoraExames(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, String nomeMicrocomputador) throws BaseException {

		// Indica se ocorreu o recebimento de no minimo uma amostra
		boolean isRetornouAmostraUnidade = false;

		// Obtém extratos de amostras com a situacao DIFERENTE de R (Recebida)
		// da amostra informada
		final List<AelExtratoAmostras> extratoAmostrasExamesSituacao = this.getAelExtratoAmostrasDAO().buscarExtratosAmostrasPorSituacaoDiferenteInformada(
				amostra.getId().getSoeSeq(), amostra.getId().getSeqp(), DominioSituacaoAmostra.R);

		// Verifica a existencia de itens
		if (extratoAmostrasExamesSituacao != null && !extratoAmostrasExamesSituacao.isEmpty()) {

			// Define o tipo de pesquisa atraves da
			// "unidade executora de exames"
			List<AelAmostraItemExames> listaAmostraItemExamesUnidadeExecutora = this.getAelAmostraItemExamesDAO().buscarAmostrasSolicitacaoVoltarAmostra(amostra);
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			// Percorre os "itens de amostra de exames" da "amostra"
			for (final AelAmostraItemExames amostraItemExameUnidadeExecutora : listaAmostraItemExamesUnidadeExecutora) {
				
				getVoltarTodasAmostrasSolicitacaoRN().verificaSeApagaAnatomopatologico(amostraItemExameUnidadeExecutora.getAelItemSolicitacaoExames(), servidorLogado);				

				if (this.validarUnidadeFuncionalAmostra(amostra.getId().getSeqp(), amostraItemExameUnidadeExecutora.getAelItemSolicitacaoExames().getUnidadeFuncional(),
						unidadeExecutora)) {
					// Pesquisa UPDATE
					AelAmostraItemExames amostraItemExameAtualizar = this.getAelAmostraItemExamesDAO()
					.obterAmostraSolicitacaoVoltarAmostraUnidadeExecutoraExamesCentralERecebimentoMateriais(amostraItemExameUnidadeExecutora, amostra);

					if (amostraItemExameAtualizar != null) {
						// Atualiza informacoes de um "item de amostra de exames" atraves de um "extrato de amostra"
						this.atualizarAmostraItemExameVoltouSituacaoExtratoAmostra(amostraItemExameAtualizar, extratoAmostrasExamesSituacao.get(0).getSituacao(), nomeMicrocomputador);
						isRetornouAmostraUnidade = true;
					}

				}
			}

		} else {
			// Informa que a solicitacao de exames gerada pela area executora,
			// nao possui situacao anterior a ser restaurada
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01087);

		}

		// Caso NENHUMA amostra tenha voltado
		if (!isRetornouAmostraUnidade) {
			// Nenhuma amostra a receber
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01089, amostra.getId().getSeqp(), unidadeExecutora.getSeq() + _HIFEN_
					+ unidadeExecutora.getDescricao());
		}

		return isRetornouAmostraUnidade;
	}

	/**
	 * ORADB PROCEDURE AELP_VOLTAR_SIT_AMOSTRA - Central Recebimento Materiais
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @throws BaseException
	 */
	public boolean voltarAmostraUnidadeCentralRecebimentoMateriais(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, String nomeMicrocomputador) throws BaseException {

		// Indica se ocorreu o recebimento de no minimo uma amostra
		boolean isRetornouAmostraUnidade = false;

		// Obtém extratos de amostras com a situacao DIFERENTE de R (Recebida)
		// da amostra informada
		final List<AelExtratoAmostras> extratoAmostrasExamesSituacao = this.getAelExtratoAmostrasDAO().buscarExtratosAmostrasPorSituacaoDiferenteInformada(
				amostra.getId().getSoeSeq(), amostra.getId().getSeqp(), DominioSituacaoAmostra.R);

		// Verifica a existencia de itens
		if (extratoAmostrasExamesSituacao != null && !extratoAmostrasExamesSituacao.isEmpty()) {

			// Define o tipo de pesquisa atraves da
			// "unidade executora de exames"
			List<AelAmostraItemExames> listaAmostraItemExamesUnidadeExecutora = this.getAelAmostraItemExamesDAO()
			.buscarAmostrasSolicitacaoVoltarAmostraCentralRecebimentoMateriais(amostra, unidadeExecutora);

			// Percorre os "itens de amostra de exames" da "amostra"
			for (final AelAmostraItemExames amostraItemExameUnidadeExecutora : listaAmostraItemExamesUnidadeExecutora) {

				// Pesquisa UPDATE
				AelAmostraItemExames amostraItemExameAtualizar = this.getAelAmostraItemExamesDAO()
				.obterAmostraSolicitacaoVoltarAmostraUnidadeExecutoraExamesCentralERecebimentoMateriais(amostraItemExameUnidadeExecutora, amostra);

				if (amostraItemExameAtualizar != null) {
					// Atualiza informacoes de um "item de amostra de exames" atraves de um "extrato de amostra"
					this.atualizarAmostraItemExameVoltouSituacaoExtratoAmostra(amostraItemExameAtualizar, extratoAmostrasExamesSituacao.get(0).getSituacao(), nomeMicrocomputador);
					isRetornouAmostraUnidade = true;
				}

			}

		} else {
			// Informa que a solicitacao de exames gerada pela area executora,
			// nao possui situacao anterior a ser restaurada
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01087);

		}

		// Caso NENHUMA amostra tenha voltado
		if (!isRetornouAmostraUnidade) {
			// Nenhuma amostra a receber
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01089, amostra.getId().getSeqp(), unidadeExecutora.getSeq() + _HIFEN_
					+ unidadeExecutora.getDescricao());
		}

		return isRetornouAmostraUnidade;
	}

	/**
	 * Cancela a edicao do numero do frasco do fornecedor
	 * 
	 * @param amostrasId
	 * @return
	 */
	public String cancelarEdicaoFrasco(final AelAmostrasId amostrasId) {
		final AelAmostras amostra = this.getAelAmostrasDAO().obterPorChavePrimaria(amostrasId);
		this.getAelAmostrasDAO().refresh(amostra);
		return amostra.getNroFrascoFabricante();
	}

	/**
	 * 
	 * @param solicitacaoExame
	 * @param amostraSeqp
	 * @return
	 * @throws BaseException
	 */
	public List<AelAmostrasVO> buscarAmostrasVOPorSolicitacaoExame(final AelSolicitacaoExames solicitacaoExame, final Short amostraSeqp) throws BaseException {
		return buscarAmostrasVOPorSolicitacaoExame(solicitacaoExame.getSeq(), amostraSeqp);
	}

	public List<AelAmostrasVO> buscarAmostrasVOPorSolicitacaoExame(final Integer soeSeq, final Short amostraSeqp) throws BaseException {

		final List<AelAmostras> listaAmostras = this.getAelAmostrasDAO().buscarAmostrasPorSolicitacaoExame(soeSeq, amostraSeqp);

		List<AelAmostrasVO> listaVO = null;

		if (listaAmostras != null && !listaAmostras.isEmpty()) {
			listaVO = new LinkedList<AelAmostrasVO>();

			// Popula lista de VOs
			for (final AelAmostras amostra : listaAmostras) {
				final AelAmostrasVO vo = new AelAmostrasVO();
				vo.setSoeSeq(amostra.getId().getSoeSeq());
				vo.setSeqp(amostra.getId().getSeqp());
				vo.setNroUnico(amostra.getNroUnico());
				vo.setDtNumeroUnico(amostra.getDtNumeroUnico());
				vo.setNroFrascoFabricante(amostra.getNroFrascoFabricante());
				vo.setTempoIntervaloColeta(amostra.getTempoIntervaloColeta());
				vo.setUnidTempoIntervaloColeta(amostra.getUnidTempoIntervaloColeta());
				vo.setSituacao(amostra.getSituacao());
				vo.setEmEdicao(false);
				
				// identifica se é Patologia Cirurgica
				for (final AelAmostraItemExames itemAmostra : amostra.getAelAmostraItemExames()) {
					if (this.getAelExameApItemSolicDAO().hasAelExameApItemSolicPorItemSolicitacaoExame(itemAmostra.getAelItemSolicitacaoExames().getId())) {
						vo.setUnidadePatologica(true);
						break;
					} else {
						vo.setUnidadePatologica(false);
					}
				}
				
				if (amostra.getRecipienteColeta() != null) {
					vo.setRecipienteColeta(amostra.getRecipienteColeta().getDescricao());
				}
				if (amostra.getAnticoagulante() != null) {
					vo.setAnticoagulante(amostra.getAnticoagulante().getDescricao());
				}
				if (amostra.getMateriaisAnalises() != null) {
					vo.setMaterialAnalise(amostra.getMateriaisAnalises().getDescricao());
				}
				// Verifica se o item de vo necessita a indicacao do frasco do
				// fornecedor
				boolean indNroFrascoFornec = false;

				// Obtém a obrigatoriedade da indicacao do frasco do fornecedor
				indNroFrascoFornec = this.exigeIndicarNumeroFrascoFornecedor(amostra);
				/**
				 * A necessidade de indicar o numero do frasco do fornecedor ocorre quando a situacao do mesmo for G (Gerada)
				 */
				if (DominioSituacaoAmostra.G.equals(amostra.getSituacao())) {
					vo.setIndNroFrascoFornec(indNroFrascoFornec);
				} else {
					vo.setIndNroFrascoFornec(false);
				}

				// Acrescenta item populado de vo na listagem
				listaVO.add(vo);
			}
		}

		return listaVO;
	}

	/**
	 * ORADB FUNCTION AELC_CONFIRMA_IMPRESSAO
	 * 
	 * @param amostras
	 * @return
	 */
	public String comporMensagemConfirmacaoImpressaoEtiquetas(final AelAmostras amostra) throws BaseException {

		final Long quantidadeVezesEtiquetaImpressa = this.getAelAmostraCtrlEtiquetasDAO().contarQuantidadeVezesEtiquetaImpressa(amostra.getId().getSoeSeq(),
				amostra.getId().getSeqp(), amostra.getNroUnico(), amostra.getDtNumeroUnico());

		final String numeroSolicitacaoAmostra = String.format("%07d", amostra.getId().getSoeSeq()) + String.format("%03d", amostra.getId().getSeqp());

		String mensagemConfirmacaoImpressaoEtiquetas = "A etiqueta da amostra " + numeroSolicitacaoAmostra + ", ser\u00E1 impressa. Confirmar a impress\u00E3o da etiqueta?";
		if (quantidadeVezesEtiquetaImpressa > 0) {
			mensagemConfirmacaoImpressaoEtiquetas = "A etiqueta da amostra " + numeroSolicitacaoAmostra + ", j\u00E1 foi impressa " + quantidadeVezesEtiquetaImpressa
			+ " vez(es). Confirmar a impress\u00E3o da etiqueta?";
		}

		return mensagemConfirmacaoImpressaoEtiquetas;

	}

	/**
	 * ORADB PROCEDURE AELP_IMPRIME_ETIQUETA
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @throws BaseException
	 */
	public Integer imprimirEtiquetaAmostra(AelAmostras amostra, AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador) throws BaseException {

		Integer qtdAmostras = 0;
		String nomeImpressora = null;
		
		AelSolicitacaoExames solicitacaoExame = aelSolicitacaoExameDAO.obterAelSolicitacaoExameAtendimentos(amostra.getId().getSoeSeq());
		amostra.setSolicitacaoExame(solicitacaoExame);
		
		//amostra = getAelAmostrasDAO().merge(amostra);
		//unidadeExecutora = getAghUnidadesFuncionaisDAO().merge(unidadeExecutora);
		
		nomeImpressora = this.getSolicitacaoExameFacade().obterNomeImpressoraEtiquetas(nomeMicrocomputador);				
		
		// Verifica se unidade que executa o exame deve gerar numero unico
		if (!this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(amostra.getUnidadesFuncionais().getSeq(), ConstanteAghCaractUnidFuncionais.GERA_NRO_UNICO)) {	

			qtdAmostras = this.getSolicitacaoExameFacade().gerarEtiquetas(amostra.getSolicitacaoExame(), amostra.getId().getSeqp(), unidadeExecutora, nomeImpressora, null);

		} else {

			if (amostra.getNroUnico() == null) {

				throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01974);

			} else {

				qtdAmostras = this.getSolicitacaoExameFacade().gerarEtiquetas(amostra.getSolicitacaoExame(), amostra.getId().getSeqp(), unidadeExecutora, nomeImpressora, null);
			}

		}
		
		return qtdAmostras;

	}


	/*private AghUnidadesFuncionaisDAO getAghUnidadesFuncionaisDAO() {
		return aghUnidadesFuncionaisDAO;
	}*/

	/**
	 *  Verifica a necessidade de interfaceamento sem cancelamento de interfaceamento
	 * @param amostra
	 * @throws BaseException
	 */
	public void verificarModoInterfaceamentoSemCancelamento(final AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		verificarModoInterfaceamento(amostra, false, nomeMicrocomputador);
	}

	/**
	 * Verifica a necessidade de interfaceamento
	 * 
	 * @param amostra
	 * @throws BaseException
	 */
	public void verificarModoInterfaceamento(final AelAmostras amostra, final boolean cancelaInterfaceamento, String nomeMicrocomputador) throws BaseException {

		// Busca o modo de interfaceamento
		final AghParametros parametroModoInterfaceamento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MODO_INTERFACEAMENTO);

		String modoInterfaceamento = parametroModoInterfaceamento.getVlrTexto();

		// Testa se o inferfaceamento está ativo no sistema
		if (modoInterfaceamento != null) {

			modoInterfaceamento = modoInterfaceamento.trim();

			if ("H".equalsIgnoreCase(modoInterfaceamento)) {

				/*
				 * Pesquisa amostras RECEBIDAS com equipamentos ATIVOS e com CARGA AUTOMATICA
				 * 
				 * ATENÇÃO: A Verficação se existem equipamentos ativos na entidade AelTmpProtUnico (AEL_TMP_PROT_UNICOS) não existe mais. 
				 * A entidade AEL_TMP_PROT_UNICOS era alimentada manualmente pela CGTI quando eram utilizados diferente interfaceamentos
				 */
				final List<AelAmostraItemExames> listaAmostraItemExames = this.getAelAmostraItemExamesDAO().pesquisarAmostraItemExamesTratarProtocoloUnicoTemporarios(
						amostra.getSolicitacaoExame().getSeq(), amostra.getId().getSeqp().intValue());

				if (!listaAmostraItemExames.isEmpty()) {
					
					// Chamada da PROCEDURE AELP_TRATA_PROT_UNICO
					this.getTratarProtocoloUnicoRN().tratarProtocoloUnico(amostra, cancelaInterfaceamento, nomeMicrocomputador);
				}

			} else if ("N".equalsIgnoreCase(modoInterfaceamento)) {
				// Chamada da PROCEDURE AELP_TRATA_PROT_UNICO
				this.getTratarProtocoloUnicoRN().tratarProtocoloUnico(amostra, cancelaInterfaceamento, nomeMicrocomputador);
			}

		}

	}

	/*
	 * Getters para RNs e DAOs
	 */

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}

	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}

	protected AelAmostrasON getAelAmostrasON() {
		return aelAmostrasON;
	}

	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}

	protected AelExtratoAmostrasDAO getAelExtratoAmostrasDAO() {
		return aelExtratoAmostrasDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected AelAmostraItemExamesRN getAelAmostraItemExamesRN() {
		return aelAmostraItemExamesRN;
	}

	protected ReceberTodasAmostrasSolicitacaoRN getReceberTodasAmostrasSolicitacaoRN() {
		return receberTodasAmostrasSolicitacaoRN;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	protected AelAmostraCtrlEtiquetasDAO getAelAmostraCtrlEtiquetasDAO() {
		return aelAmostraCtrlEtiquetasDAO;
	}

	protected TratarProtocoloUnicoRN getTratarProtocoloUnicoRN() {
		return tratarProtocoloUnicoRN;
	}
	
	protected VoltarTodasAmostrasSolicitacaoRN getVoltarTodasAmostrasSolicitacaoRN() {
		return voltarTodasAmostrasSolicitacaoRN;
	}	

	protected AelAnatomoPatologicoDAO getAelAnatomoPatologicoDAO() {
		return aelAnatomoPatologicoDAO;
	}
	
	protected AelUnidExecUsuarioDAO getAelUnidExecUsuarioDAO() {
		return aelUnidExecUsuarioDAO;
	}

	protected AelCadGuicheDAO getAelCadGuicheDAO() {
		return aelCadGuicheDAO;
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected AelExameApDAO getAelExameApDAO() {
		return aelExameApDAO;
	}

	protected AelExameApItemSolicDAO getAelExameApItemSolicDAO() {
		return aelExameApItemSolicDAO;
	}
	
}
