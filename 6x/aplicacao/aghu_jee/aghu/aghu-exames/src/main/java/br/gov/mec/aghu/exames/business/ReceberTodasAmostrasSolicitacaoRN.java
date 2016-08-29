package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.business.ListarAmostrasSolicitacaoRecebimentoRN.ListarAmostrasSolicitacaoRecebimentoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoDAO;
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
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * ORADB PROCEDURE AELP_RECEBE_SOLICITACAO
 * 
 * @author aghu
 * 
 */
@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
@Stateless
public class ReceberTodasAmostrasSolicitacaoRN extends BaseBusiness {

	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@Inject
	private AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO;
	
	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	
	@Inject
	private AelUnidExecUsuarioDAO aelUnidExecUsuarioDAO;
		
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ListarAmostrasSolicitacaoRecebimentoRN listarAmostrasSolicitacaoRecebimentoRN;
	
	private static final Log LOG = LogFactory.getLog(ReceberTodasAmostrasSolicitacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7126763169233510881L;

	/**
	 * ORADB PROCEDURE AELP_RECEBE_SOLICITACAO Recebe Amostra por Solicitação
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @param numeroApOrigem
	 * @param configExameOrigem 
	 * @throws BaseException
	 */
	public ImprimeEtiquetaVO receberAmostraSolicitacao(
			final AghUnidadesFuncionais unidadeExecutora,
			AelAmostras amostra, List<ExameAndamentoVO> listaExamesAndamento, String nomeMicrocomputador) throws BaseException {
		ImprimeEtiquetaVO imprimeEtiquetaVO = null;
		// Valida parâmetros obrigatórios
		CoreUtil.validaParametrosObrigatorios(unidadeExecutora, amostra);
		
		amostra = getAelAmostrasDAO().obterPorChavePrimaria(amostra.getId());

		/*
		 * A seguinte regra foi exonerada: Armazena o valor da data atual (variável de package no PLSQL) de forma que o extrato dos itens da solicitação, quando gerado na entrada
		 * na área executora fiquem com a mesma data de geração
		 */

		/*
		 * A PROCEDURE AELP_TESTA_NRO_FRASCO_SOL é chamada antecipadamente via facade na controller devido ao preenchimento do número do frasco via ajax.
		 */

		// Unidade de Coleta
		if (this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA)) {
			this.receberAmostraUnidadeColeta(unidadeExecutora, amostra, nomeMicrocomputador);
		}else
		// Unidade Central Recebimento Materiais
		if (this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS)) {
			imprimeEtiquetaVO = this.receberAmostraUnidadeCentralRecebimentoMateriais(unidadeExecutora, amostra, nomeMicrocomputador);
		}else
		// Unidade Executora Exames e/ou Unidade Patologica
		if (unidadeExecutora.possuiCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES)) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			imprimeEtiquetaVO = this.receberAmostraUnidadeExecutoraExames(
					unidadeExecutora, amostra, listaExamesAndamento, nomeMicrocomputador, servidorLogado);
		}


		return imprimeEtiquetaVO;
	}

	/**
	 * ORADB PROCEDURE AELP_RECEBE_SOLICITACAO - Unidade Executora Coleta
	 * 
	 * @param amostra
	 * @throws BaseException
	 */
	public void receberAmostraUnidadeColeta(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, String nomeMicrocomputador)
	throws BaseException {

		// Pesquisa amostra itens exame com situação G (Gerada) ou C (Coletada) através do número da solicitação
		List<AelAmostraItemExames> listaAmostrasItemExamesGeradasColetadas = this.getAelAmostraItemExamesDAO().pesquisarAmostraItemExameGeradaColetadaPorSolicitacao(
				amostra.getId().getSoeSeq());

		for (AelAmostraItemExames amostraItemExameAtualizar : listaAmostrasItemExamesGeradasColetadas) {

			// Seta a situacao do "item de amostra de exame" para U
			// (Recebida Unidade de Coleta)
			//amostraItemExameAtualizar.setSituacao(DominioSituacaoAmostra.U);

			this.getListarAmostrasSolicitacaoRecebimentoRN().atualizarAelAmostraItemExames(amostraItemExameAtualizar, DominioSituacaoAmostra.U, true, nomeMicrocomputador);

			this.getListarAmostrasSolicitacaoRecebimentoRN().imprimirEtiquetaAmostra(amostra, unidadeExecutora, nomeMicrocomputador);

		}

		// TODO revisar a necessidade da contagem de amostras recebidas

	}

	/**
	 * ORADB PROCEDURE AELP_RECEBE_SOLICITACAO - Unidade Executora Exames e/ou Unidade Patologica
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @param numeroApOrigem
	 * @param configExameOrigem 
	 * @throws BaseException
	 */
	protected ImprimeEtiquetaVO receberAmostraUnidadeExecutoraExames(
			final AghUnidadesFuncionais unidadeExecutora,
			final AelAmostras amostra, List<ExameAndamentoVO> listaExamesAndamento, String nomeMicrocomputador,
			final RapServidores servidorLogado) throws BaseException {
		final boolean possuiCaracteristicaPatologica = this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_PATOLOGIA);
		Boolean impFichaPatologia = Boolean.FALSE;
		ImprimeEtiquetaVO vo = new ImprimeEtiquetaVO();
		String nomeImpressora = null;

		//twickert - 28/04/2014
		//Esse código foi comentado pq não deve mais ser verificado se deve informar ap de origem
		//final ListarAmostrasSolicitacaoRecebimentoRN listarAmostrasSolicitacaoRecebimentoRN = this.getListarAmostrasSolicitacaoRecebimentoRN();

		/*
		 * Atenção! Gera etiquetas apenas 1 vez! Se voltar todas e receber novamente não irá gerar.
		 */
		List<AelAmostras> listAmostrasEtiquetas = this.getAelAmostrasDAO().buscarAmostrasPorSolicitacaoExame(amostra.getSolicitacaoExame());
		// Este mapa armazena o ID da AMOSTRA e os dados do NÚMERO ÚNICO (número e data) da TELA
		Map<AelAmostrasId, Map<Integer, Date>> mapaAmostrasNumeroUnicoEtiquetas = new HashMap<AelAmostrasId, Map<Integer, Date>>();
		for (AelAmostras amostrasNumeroUnicoEtiquetas : listAmostrasEtiquetas) {
			Map<Integer, Date> valoresAnterioresNumeroUnicioDataAmostra = new HashMap<Integer, Date>();
			valoresAnterioresNumeroUnicioDataAmostra.put(amostrasNumeroUnicoEtiquetas.getNroUnico(), amostrasNumeroUnicoEtiquetas.getDtNumeroUnico());
			mapaAmostrasNumeroUnicoEtiquetas.put(amostrasNumeroUnicoEtiquetas.getId(), valoresAnterioresNumeroUnicioDataAmostra);
		}
		
		// 2.2.1 "Unidade Executora Patologica"
		//if (possuiCaracteristicaPatologica) {

			// Valida número AP de Origem
			//listarAmostrasSolicitacaoRecebimentoRN.validarNumeroApOrigem(amostra, configExameOrigem, numeroApOrigem);

		//}

		// 2.2.2 Verifica "itens de amostra de exame" com "itens de solicitacao de exame" com a situacao PE (Pendente)
		// Obtém "itens de amostra de exame"
		final List<AelAmostraItemExames> listaAelAmostraItemExames = getAelAmostraItemExamesDAO().buscarAmostrasSolicitacaoRecebimentoPorSolicitacao(amostra);

		// 2.2.2 Atualiza "itens de amostra de exame" para situacao R (Recebido)
		this.getListarAmostrasSolicitacaoRecebimentoRN().atualizarAmostraItensExameRecebidoUnidadeExecutoraExames(unidadeExecutora, amostra, listaAelAmostraItemExames,
				nomeMicrocomputador);

		// INTERFACEAMENTO GESTAM
		this.tratarProtocoloUnico(unidadeExecutora, listaAelAmostraItemExames.get(0).getAelItemSolicitacaoExames().getSolicitacaoExame(), nomeMicrocomputador);

		// Obtém a SOLICITAÇÃO DA AMOSTRA
		Integer amoSoeSeq = amostra.getId().getSoeSeq();	
		
		// Conjunto que controla "amostra solicitação e amostra seqp" ANTERIORES de um AMOSTRA ITEM EXAME. Vide AMO_SOE_SEQ e AMO_SOE_SEQ em AEL_AMOSTRA_ITEM_EXAMES_ID.
		Set<String> vAmoSoeSeqpAntRecebidos = new HashSet<String>();
		
		nomeImpressora = this.getSolicitacaoExameFacade().obterNomeImpressoraEtiquetas(nomeMicrocomputador);				
		
		for (final AelAmostraItemExames amostraItemExame : listaAelAmostraItemExames) {

			// Cria "amostra solicitação seqp " ANTERIOR. Vide literal de AEL_AMOSTRA_ITEM_EXAMES_ID: AMO_SOE_SEQ + "." + AMO_SEQP
			String vAmoSoeSeqpAnterior = amostraItemExame.getId().getAmoSoeSeq() + "." 
			+ amostraItemExame.getId().getAmoSeqp();

			final AelUnfExecutaExames unfExecutaExames = getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(
					amostraItemExame.getAelItemSolicitacaoExames().getExame().getSigla(),
					amostraItemExame.getAelItemSolicitacaoExames().getMaterialAnalise().getSeq(), 
					unidadeExecutora.getSeq());

			/*
			 * Verifica se "amostra solicitação e amostra seqp" ATUAL ESTÁ CONTIDA NAS ANTERIORES. 
			 * A validação é feita para EVITAR a geração de etiquetas em exames dependentes
			 */
			final String vAmoSoeSeqpAtual = amoSoeSeq + "." + amostraItemExame.getId().getAmoSeqp();
			if (!vAmoSoeSeqpAntRecebidos.contains(vAmoSoeSeqpAtual)) {
			
				// Acrescenta vAmoSoeSeqpAnt que será considerado o anterior no laço
				vAmoSoeSeqpAntRecebidos.add(vAmoSoeSeqpAnterior);
					
				// Verifica a necessidade de INTERFACEAMENTO
				this.getListarAmostrasSolicitacaoRecebimentoRN().verificarModoInterfaceamentoSemCancelamento(amostraItemExame.getAelAmostras(),
						nomeMicrocomputador);

				// Atualização do numero do GUICHÊ
				this.getListarAmostrasSolicitacaoRecebimentoRN().atualizarGuiche(amostraItemExame.getAelAmostras(), unidadeExecutora);
				
				// Verificação do número único da solicitação: AELP_VERIFICA_NRO_UNICO
				this.verificarNroUnicoSolicitacao(
						vo, 
						unidadeExecutora, 
						mapaAmostrasNumeroUnicoEtiquetas, 
						amostraItemExame.getAelAmostras(),
						nomeImpressora);
			}

			if (unfExecutaExames != null) {
				final boolean possuiCarac = this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(amostraItemExame.getAelAmostras().getUnidadesFuncionais().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_PATOLOGIA);
				
				if (Boolean.TRUE.equals(unfExecutaExames.getIndImprimeFicha())) {
					if (possuiCarac) {
						impFichaPatologia = Boolean.TRUE;
					} else {
						aelpImprimeFichaTrabalho(vo, amostraItemExame.getAelAmostras().getId().getSoeSeq(), amostraItemExame.getAelAmostras().getId()
								.getSeqp());
					}
				}

				if (Boolean.TRUE.equals(unfExecutaExames.getIndLaudoUnico().isSim()) && possuiCarac) {
					impFichaPatologia = Boolean.TRUE;
				}

			}
		}

		/*
		 * TODO 2.2.3 Verificar necessidade de migração das procedures: AELP_MONTA_CARGA_LABWIDE AELP_MONTA_CARGA_SYSME AELP_MONTA_CARGA_UF100
		 * 
		 * AELP_TRATA_PROT_UNICO AELP_VERIF_NRO_UNICO_SOL AELP_ASSOCIA_NUMERO_AP
		 */

		if (possuiCaracteristicaPatologica) {
			// AELP_ASSOCIA_NUMERO_AP;
//			listarAmostrasSolicitacaoRecebimentoRN.associarNumeroApPorGrupo(amostra, nomeMicrocomputador);

			if (Boolean.TRUE.equals(impFichaPatologia)) {
				this.aelpImprimeFichaTrabalho(vo, amostra.getId().getSoeSeq(), amostra.getId().getSeqp());

				this.aelpImprimeEtiqAp(vo, amostra.getSolicitacaoExame().getSeq(), amostra.getId().getSeqp(), unidadeExecutora.getSeq());
			}
		}

		return vo;
	}

	/**
	 * Inicia chamada para a PROCEDURE AELP_TRATA_PROT_UNICO (INTERFACEAMENTO GESTAM)
	 * 
	 * @param unidadeExecutora
	 * @param solicitacaoExames
	 * @param nomeMicrocomputador
	 * @throws BaseException
	 */
	private void tratarProtocoloUnico(AghUnidadesFuncionais unidadeExecutora, AelSolicitacaoExames solicitacaoExames, String nomeMicrocomputador)
	throws BaseException {
		// Resgata todas as amostras
		List<AelAmostras> listaAmostras = this.getAelAmostrasDAO().buscarAmostrasPorUnidadeFuncionalSolicitacaoExame(unidadeExecutora, solicitacaoExames);
		for (AelAmostras amostraRecebida : listaAmostras) { // Percorre amostras da unidade executora
			this.getListarAmostrasSolicitacaoRecebimentoRN().verificarModoInterfaceamentoSemCancelamento(amostraRecebida, nomeMicrocomputador);
		}
	}

	/**
	 * ORADB Procedure AELP_IMPRIME_FICHA_TRABALHO
	 * 
	 * 
	 * @param soeSeq
	 * @param unfSeq
	 * @throws BaseException
	 */

	public ImprimeEtiquetaVO aelpImprimeFichaTrabalho(final ImprimeEtiquetaVO imprimeEtiquetaVO, final Integer soeSeq, final Short seqp)
	throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AghUnidadesFuncionais unidadeFuncional = null;
		AghUnidadesFuncionais unidadeFuncionalExecutora = null;
		AelUnidExecUsuario usuarioUnidadeExecutora = getAelUnidExecUsuarioDAO().obterPeloId(servidorLogado.getId());
		AelAmostras amostra = getAelAmostrasDAO().obterPorChavePrimaria(new AelAmostrasId(soeSeq, seqp));

		if (usuarioUnidadeExecutora != null) {
			unidadeFuncionalExecutora = usuarioUnidadeExecutora.getUnfSeq();
		}
		
		
		if (unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS)) {
			unidadeFuncional = amostra.getUnidadesFuncionais();
		} else {
			unidadeFuncional = unidadeFuncionalExecutora;
		}

		if (unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA)) {
			return null;
		} else {

			if ((unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_RADIOIMUNOENSAIO))
					|| (unidadeFuncional != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_RADIOIMUNOENSAIO))) { // NOPMD
				/*
				 * "Conforme as consultoras Carmelinda Adriana e Rosane da CGTI, a Ficha de Trabalho Radioimunoensaio não é mais utilizada no HCPA."
				 */
				// IMPRIMIR AELR_FICHA_TRAB_RAD
			} else if ((unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_IMUNOLOGIA))
					|| (unidadeFuncional != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_IMUNOLOGIA))) {// NOPMD
				// TODO IMPRIMIR AELR_FICHA_TRAB_IMU
			} else if ((unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_PATOLOGIA))
					|| (unidadeFuncional != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_PATOLOGIA))) {
				imprimeEtiquetaVO.setSoeSeq(soeSeq);
				imprimeEtiquetaVO.setUnfSeq(unidadeFuncional.getSeq());
				imprimeEtiquetaVO.setImprimirFichaTrabLab(Boolean.FALSE);
				imprimeEtiquetaVO.setImprimirFichaTrabPat(Boolean.TRUE);
				imprimeEtiquetaVO.setImprimir(Boolean.TRUE);
			} else if ((unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.IMPRIME_FICHA_POR_AMOSTRA))
					|| (unidadeFuncional != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.IMPRIME_FICHA_POR_AMOSTRA))) {
				imprimeEtiquetaVO.setSoeSeq(soeSeq);
				imprimeEtiquetaVO.setUnfSeq(unidadeFuncional.getSeq());
				imprimeEtiquetaVO.setAmoSeqP(seqp);
				imprimeEtiquetaVO.setImprimirFichaTrabLab(Boolean.FALSE);
				imprimeEtiquetaVO.setImprimirFichaTrabPat(Boolean.FALSE);
				imprimeEtiquetaVO.setImprimirFichaTrabAmo(Boolean.TRUE);
				imprimeEtiquetaVO.setImprimir(Boolean.TRUE);
			} else if ((unidadeFuncionalExecutora != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncionalExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.IMPRIME_FICHA_POR_EXAME))
					|| (unidadeFuncional != null && this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.IMPRIME_FICHA_POR_EXAME))) {
				imprimeEtiquetaVO.setSoeSeq(soeSeq);
				imprimeEtiquetaVO.setUnfSeq(unidadeFuncional.getSeq());
				imprimeEtiquetaVO.setAmoSeqP(seqp);
				imprimeEtiquetaVO.setReceberAmostra(true);
				imprimeEtiquetaVO.setImprimirFichaTrabLab(Boolean.TRUE);
				imprimeEtiquetaVO.setImprimirFichaTrabPat(Boolean.FALSE);
				imprimeEtiquetaVO.setImprimir(Boolean.TRUE);
			} else {
				imprimeEtiquetaVO.setSoeSeq(soeSeq);
				imprimeEtiquetaVO.setUnfSeq(unidadeFuncional.getSeq());
				imprimeEtiquetaVO.setAmoSeqP(seqp);
				imprimeEtiquetaVO.setImprimirFichaTrabLab(Boolean.FALSE);
				imprimeEtiquetaVO.setImprimirFichaTrabPat(Boolean.FALSE);
				imprimeEtiquetaVO.setImprimirFichaTrabAmo(Boolean.TRUE);
				imprimeEtiquetaVO.setImprimir(Boolean.TRUE);
			}
		}

		return imprimeEtiquetaVO;
	}

	/**
	 * ORADB Procedure AELP_IMPRIME_ETIQ_AP
	 * 
	 * @param soeSeq
	 * @param unfSeq
	 * @throws BaseException
	 */
	public ImprimeEtiquetaVO aelpImprimeEtiqAp(final ImprimeEtiquetaVO imprimeEtiquetaVO, final Integer soeSeq, final Short seqp, final Short unfSeq) throws BaseException {

		final AelAnatomoPatologico ap = this.getAelAnatomoPatologicoDAO().obterAelAnatomoPatologicoPorItemSolic(soeSeq, seqp);
		if (ap != null) {
			final Long nroAp = ap.getNumeroAp();
			final String sigla = ap.getConfigExame().getSigla();

			imprimeEtiquetaVO.setNroAp(nroAp);
			imprimeEtiquetaVO.setNumeroAp(nroAp);
			imprimeEtiquetaVO.setSigla(sigla);
			imprimeEtiquetaVO.setUnfSeq(unfSeq);
			imprimeEtiquetaVO.setImprimir(Boolean.TRUE);
		}
		return imprimeEtiquetaVO;		
	}

	/**
	 * ORADB PROCEDURE AELP_RECEBE_SOLICITACAO - Central Recebimento Materiais
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @throws BaseException
	 */
	public ImprimeEtiquetaVO receberAmostraUnidadeCentralRecebimentoMateriais(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra,
			String nomeMicrocomputador) throws BaseException {
		
		String nomeImpressora = null;

		// Obtém "itens de amostra de exame"
		final List<AelAmostraItemExames> listaUnidadeCentralRecebimentoMateriais = this.getAelAmostraItemExamesDAO().buscarReceberAmostrasSolicitacaoCentralRecebimentoMateriais(
				amostra, unidadeExecutora);

		/*
		 * Atenção! Gera etiquetas apenas 1 vez! Se voltar todas e receber novamente não irá gerar.
		 */
		List<AelAmostras> listAmostrasEtiquetas = this.getAelAmostrasDAO().buscarAmostrasPorSolicitacaoExame(amostra.getSolicitacaoExame());
		// Este mapa armazena o ID da AMOSTRA e os dados do NÚMERO ÚNICO (número e data) da TELA
		Map<AelAmostrasId, Map<Integer, Date>> mapaAmostrasNumeroUnicoEtiquetas = new HashMap<AelAmostrasId, Map<Integer, Date>>();
		for (AelAmostras amostrasNumeroUnicoEtiquetas : listAmostrasEtiquetas) {
			Map<Integer, Date> valoresAnterioresNumeroUnicioDataAmostra = new HashMap<Integer, Date>();
			valoresAnterioresNumeroUnicioDataAmostra.put(amostrasNumeroUnicoEtiquetas.getNroUnico(), amostrasNumeroUnicoEtiquetas.getDtNumeroUnico());
			mapaAmostrasNumeroUnicoEtiquetas.put(amostrasNumeroUnicoEtiquetas.getId(), valoresAnterioresNumeroUnicioDataAmostra);
		}

		// Controla a impressão da ficha
		Boolean impFicha = Boolean.FALSE;

		// Controla as operações de impressão. Obs. Também controlada a QUANTIDADE DE ETIQUETAS IMPRESSAS
		ImprimeEtiquetaVO imprimeEtiquetaVO = new ImprimeEtiquetaVO();

		// Obtém a SOLICITAÇÃO DA AMOSTRA
		Integer amoSoeSeq = amostra.getId().getSoeSeq();

		boolean isRecebeuAmostraUnidade = false;

		// Conjunto que controla "amostra solicitação e amostra seqp" ANTERIORES de um AMOSTRA ITEM EXAME. Vide AMO_SOE_SEQ e AMO_SOE_SEQ em AEL_AMOSTRA_ITEM_EXAMES_ID.
		Set<String> vAmoSoeSeqpAntRecebidos = new HashSet<String>();
		
		nomeImpressora = this.getSolicitacaoExameFacade().obterNomeImpressoraEtiquetas(nomeMicrocomputador);				

		for (final AelAmostraItemExames amostraItemExameCentralRecebimentoMateriais : listaUnidadeCentralRecebimentoMateriais) {

			// Cria "amostra solicitação seqp " ANTERIOR. Vide literal de AEL_AMOSTRA_ITEM_EXAMES_ID: AMO_SOE_SEQ + "." + AMO_SEQP
			String vAmoSoeSeqpAnterior = amostraItemExameCentralRecebimentoMateriais.getId().getAmoSoeSeq() + "."
			+ amostraItemExameCentralRecebimentoMateriais.getId().getAmoSeqp();

			// Pesquisa UPDATE para atualiza "itens de amostra de exame"
			final List<AelAmostraItemExames> listaAmostraItemExameAtualizars = this.getAelAmostraItemExamesDAO().pesquisarReceberTodasAmostrasCentralRecebimentoMateriais(
					amoSoeSeq, amostraItemExameCentralRecebimentoMateriais.getId().getIseSoeSeq(), amostraItemExameCentralRecebimentoMateriais.getId().getIseSeqp());

			// Atualiza "itens de amostra de exame" para situacao R (Recebido)
			for (AelAmostraItemExames amostraItemExameAtualizar : listaAmostraItemExameAtualizars) {

				//amostraItemExameAtualizar.setSituacao(DominioSituacaoAmostra.R);
				this.getListarAmostrasSolicitacaoRecebimentoRN().atualizarAelAmostraItemExames(amostraItemExameAtualizar, DominioSituacaoAmostra.R, true, nomeMicrocomputador);
				isRecebeuAmostraUnidade = true;
			}

			// INTERFACEAMENTO GESTAM
			if (isRecebeuAmostraUnidade) {
				this.tratarProtocoloUnico(unidadeExecutora, listaAmostraItemExameAtualizars.get(0).getAelItemSolicitacaoExames().getSolicitacaoExame(), nomeMicrocomputador);
			}

			// TODO Atualizar "amostra" relacionados ao processo de recebimento de amostras Particularidade do HCPA. Estória em análise.
			// this.atualizarGuicheAmostraEnvioAvisosRelacionadosProcessoRecebimentoAmostra(amostra);

			/*
			 * Verifica se "amostra solicitação e amostra seqp" ATUAL ESTÁ CONTIDA NAS ANTERIORES. 
			 * A validação é feita para EVITAR a geração de etiquetas em exames dependentes
			 */
			final String vAmoSoeSeqpAtual = amoSoeSeq + "." + amostraItemExameCentralRecebimentoMateriais.getId().getAmoSeqp();
			if (!vAmoSoeSeqpAntRecebidos.contains(vAmoSoeSeqpAtual)) {

				// Acrescenta vAmoSoeSeqpAnt que será considerado o anterior no laço
				vAmoSoeSeqpAntRecebidos.add(vAmoSoeSeqpAnterior);

				// Verifica a necessidade de INTERFACEAMENTO
				this.getListarAmostrasSolicitacaoRecebimentoRN().verificarModoInterfaceamentoSemCancelamento(amostraItemExameCentralRecebimentoMateriais.getAelAmostras(),
						nomeMicrocomputador);

				// Atualização do numero do GUICHÊ
				this.getListarAmostrasSolicitacaoRecebimentoRN().atualizarGuiche(amostraItemExameCentralRecebimentoMateriais.getAelAmostras(), unidadeExecutora);

				// Verificação do número único da solicitação: AELP_VERIFICA_NRO_UNICO
				this.verificarNroUnicoSolicitacao(
						imprimeEtiquetaVO, 
						unidadeExecutora, 
						mapaAmostrasNumeroUnicoEtiquetas, 
						amostraItemExameCentralRecebimentoMateriais.getAelAmostras(),
						nomeImpressora);
				
				if (!impFicha) {
					final AelUnfExecutaExames unfExecutaExames = getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(
							amostraItemExameCentralRecebimentoMateriais.getAelItemSolicitacaoExames().getExame().getSigla(),
							amostraItemExameCentralRecebimentoMateriais.getAelItemSolicitacaoExames().getMaterialAnalise().getSeq(),
							amostraItemExameCentralRecebimentoMateriais.getAelItemSolicitacaoExames().getUnidadeFuncional().getSeq());

					if (unfExecutaExames != null) {
						if (Boolean.TRUE.equals(unfExecutaExames.getIndImprimeFicha())) {
							impFicha = Boolean.TRUE;
						}
					}
				}
			}
		}

		// Caso NENHUMA amostra recebida
		if (!isRecebeuAmostraUnidade) {
			// NENHUMA AMOSTRA A RECEBER
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01086, amostra.getId().getSeqp(), unidadeExecutora.getSeq() + " - "
					+ unidadeExecutora.getDescricao());
		} else {
			if (impFicha) {
				aelpImprimeFichaTrabalho(imprimeEtiquetaVO, amostra.getId().getSoeSeq(), amostra.getId().getSeqp());
			}
		}

		return imprimeEtiquetaVO;
	}

	/**
	 * ORADB PROCEDURE AELP_VERIF_NRO_UNICO_SOL
	 * @param imprimeEtiquetaVO
	 * @param unidadeExecutora
	 * @param mapaAmostrasNumeroUnicoEtiquetas
	 * @param amostra
	 * @throws BaseException
	 */
	private void verificarNroUnicoSolicitacao(ImprimeEtiquetaVO imprimeEtiquetaVO, final AghUnidadesFuncionais unidadeExecutora,
			Map<AelAmostrasId, Map<Integer, Date>> mapaAmostrasNumeroUnicoEtiquetas, final AelAmostras amostra, String nomeImpressora) throws BaseException {

		// Resgata os valores (número e data) do NÚMERO ÚNICO  da TELA
		Map<Integer, Date> valoresAnterioresNumeroUnicioDataAmostra = mapaAmostrasNumeroUnicoEtiquetas.get(amostra.getId());
		
		Integer nroUnicoAmostraTela = null;
		Date dtNumeroUnicoAmostraTela = null;
		
		for (Integer key : valoresAnterioresNumeroUnicioDataAmostra.keySet()) {
			nroUnicoAmostraTela = key;
			dtNumeroUnicoAmostraTela = valoresAnterioresNumeroUnicioDataAmostra.get(key);
		}
		
		// Verifica se há diferença entre o NÚMERO ÚNICO DA TELA (número e data) e o NÚMERO ÚNICO DO BANCO
		if(!CoreUtil.igual(nroUnicoAmostraTela, amostra.getNroUnico()) || !CoreUtil.igual(dtNumeroUnicoAmostraTela, amostra.getDtNumeroUnico())){
			Integer nroEtiquetas = this.getSolicitacaoExameFacade().gerarEtiquetas(amostra.getSolicitacaoExame(), amostra.getId().getSeqp(), unidadeExecutora, nomeImpressora, null);
			Integer qtdEtiquetas = imprimeEtiquetaVO.getQtdEtiquetas() != null ? imprimeEtiquetaVO.getQtdEtiquetas() : 0;
			imprimeEtiquetaVO.setQtdEtiquetas(qtdEtiquetas + nroEtiquetas);
		}

	}

	/**
	 * ORADB AELP_TESTA_NRO_FRASCO_SOL Verifica se a amostra requer a informacao do numero do frasco do fornecedor
	 */
	public void validarNumeroFrascoSolicitacao(final AghUnidadesFuncionais unidadeExecutora, List<AelAmostrasVO> listaAmostras) throws BaseException {

		final Boolean isUnidColeta = this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA);
		final Boolean isUnidExecutoraExames =  this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES);
		final Boolean isCentralRecebimentoMateriais = this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS);

		for (AelAmostrasVO vo : listaAmostras) {

			AelAmostras amostra = this.getAelAmostrasDAO().obterAmostraPorId(vo.getSoeSeq(), vo.getSeqp());

			// Unidade de Coleta
			if (isUnidColeta) {
				this.getListarAmostrasSolicitacaoRecebimentoRN().validarNumeroFrasco(amostra, vo.getNroFrascoFabricante());
			}

			// Unidade Executora Exames e/ou Unidade Patologica
			if (isUnidExecutoraExames && unidadeExecutora.equals(amostra.getUnidadesFuncionais())) {
				this.getListarAmostrasSolicitacaoRecebimentoRN().validarNumeroFrasco(amostra, vo.getNroFrascoFabricante());
			}

			// Unidade Central Recebimento Materiais
			if (isCentralRecebimentoMateriais) {
				this.getListarAmostrasSolicitacaoRecebimentoRN().validarNumeroFrasco(amostra, vo.getNroFrascoFabricante());
			}

		}
	}
	
	public List<ImprimeEtiquetaVO> gerarEtiquetasAmostrasRecebidasUnf(final List<AelAmostrasVO> listaAmostras, final AghUnidadesFuncionais unidadeExecutora) {
		if (listaAmostras == null || listaAmostras.isEmpty()) {
			return new ArrayList<ImprimeEtiquetaVO>(0);
		}
		final List<ImprimeEtiquetaVO> retorno = new ArrayList<ImprimeEtiquetaVO>(listaAmostras.size());
		for (final AelAmostrasVO amostra : listaAmostras) {
			if (amostra.getUnidadePatologica()) {
				final ImprimeEtiquetaVO etiqueta = new ImprimeEtiquetaVO();
				final AelAnatomoPatologico ap = this.getAelAnatomoPatologicoDAO().obterAelAnatomoPatologicoPorItemAmostra(amostra.getSoeSeq(),
						amostra.getSeqp());
				etiqueta.setNroAp(ap.getNumeroAp());
				etiqueta.setNumeroAp(ap.getNumeroAp());
				etiqueta.setSigla(ap.getConfigExame().getSigla());
				etiqueta.setUnfSeq(unidadeExecutora.getUnfSeq().getSeq());
				etiqueta.setImprimir(Boolean.TRUE);
				retorno.add(etiqueta);
			}
		}
		return retorno;
	}

	/*
	 * Getters para RNs e DAOs
	 */

	protected ListarAmostrasSolicitacaoRecebimentoRN getListarAmostrasSolicitacaoRecebimentoRN() {
		return listarAmostrasSolicitacaoRecebimentoRN;
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}

	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}

	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}

	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}

	protected AelUnidExecUsuarioDAO getAelUnidExecUsuarioDAO() {
		return aelUnidExecUsuarioDAO;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected AelAnatomoPatologicoDAO getAelAnatomoPatologicoDAO() {
		return aelAnatomoPatologicoDAO;
	}
}
