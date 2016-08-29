package br.gov.mec.aghu.exames.solicitacao.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dominio.DominioFormaRespiracao;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndImpressoLaudo;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoDia;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.dominio.DominioTipoTransporteUnidade;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameDAO;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelExameConselhoProfsDAO;
import br.gov.mec.aghu.exames.dao.AelExamesEspecialidadeDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoSolicitacaoUnidadeExecutoraDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelMotivoCancelaExamesDAO;
import br.gov.mec.aghu.exames.dao.AelPermissaoUnidSolicDAO;
import br.gov.mec.aghu.exames.dao.AelServidoresExameUnidDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameCancelamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExameConselhoProfs;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesEspecialidade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelInformacaoSolicitacaoUnidadeExecutora;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelServidoresExameUnid;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MpaPops;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.protocolo.business.IProtocoloFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Responsavel pelas regras de negocio da entidade AelItemSolicitacaoExames.
 * 
 * Regras de 6 a 45 do documento de pre-analise.
 * 
 * Tabela: AEL_ITEM_SOLICITACAO_EXAMES ORADB Trigger AELT_ISE_BRI. ORADB Trigger
 * AELT_ISE_BRU.
 * 
 * @author rcorvalao
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class ItemSolicitacaoExameRN extends BaseBusiness {
	
	@EJB
	private ItemSolicitacaoExameEnforceRN itemSolicitacaoExameEnforceRN;
	
	private static final Log LOG = LogFactory.getLog(ItemSolicitacaoExameRN.class);
	
	@Inject
	private AelInformacaoSolicitacaoUnidadeExecutoraDAO aelInformacaoSolicitacaoUnidadeExecutoraDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;
	
	@Inject
	private AelServidoresExameUnidDAO aelServidoresExameUnidDAO;
	
	@Inject
	private AelExameConselhoProfsDAO aelExameConselhoProfsDAO;
	
	@Inject
	private AelMotivoCancelaExamesDAO aelMotivoCancelaExamesDAO;
	
	@Inject
	private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;
	
	@Inject
	private AelExamesEspecialidadeDAO aelExamesEspecialidadeDAO;
	
	@EJB
	private IProtocoloFacade protocoloFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private AelPermissaoUnidSolicDAO aelPermissaoUnidSolicDAO;
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelMatrizSituacaoDAO aelMatrizSituacaoDAO;

	@Inject
	private AelAgrpPesquisasDAO aelAgrpPesquisasDAO;
	
	@Inject
	private AelAgrpPesquisaXExameDAO aelAgrpPesquisaXExameDAO;
	
	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7689918001995390212L;

	public enum ItemSolicitacaoExameRNExceptionCode implements
	BusinessExceptionCode {
		AEL_00499 // “Obrigatório a informação do material de análise.”
		, AEL_00500 // “Não é permitido informar descrição do material de análise.”
		, AEL_01682 // “O exame e/ ou profissional solicitante não autorizado para a agenda ligada a um Protocolo Enf Exames.”
		, AEL_02304 // igual ao AEL_01682
		, AEL_00444B // “Servidor pertence a um conselho profissional que não permite solicitar este exame.”
		, AEL_00445 // “Na inclusão de itens de exames a data de impressão do sumário não pode ser informada.”
		, AEL_00490B // “Este exame não está liberado para este usuário solicitar.”
		, AEL_01007 // “Não é permitido solicitar exame com situação A EXECUTAR se material de análise é coletável.”
		, AEL_00477 // “Situação para exame solicitado não permitido.”
		, AEL_00728 // “Voce não tem permissão para alterar exame nesta situação.”
		, AEL_01872 // “Responsável pela solicitação não tem autorização para alterar exame nesta situação.”
		, AEL_00542 // "Este Exame Solicitado não pode mais ser alterado.”
		, AEL_01987 // “O prazo permitido para alteração dos resultados deste exame já expirou. Neste caso, digite a modificação na tela de Notas Adicionais (Menu Exames, Incluir Notas Adicionais a Resultados).”
		, AEL_00426 // “Permissão da Unidade Solicitante não está cadastrada.”
		, AEL_00426B // “Permissão da Unidade Solicitante {0} não está cadastrada.”
		, AEL_00426C // “Permissão de unidade solicitante do exame {0} não está cadastrada. Entre em contato com a área executora do exame para verificar permissões".”
		, AEL_00427 // “Obrigatório a informação do tipo de transporte para o paciente e se necessita O2.”
		, AEL_00430 // “Não é permitido programar este exame nesta Unidade Solicitante.”
		, AEL_00467 // “Programação de exames não pode ser superior a P_PRAZO_MAX_SOLIC_PROGRAMADA dias.”
		, AEL_00439 // “Unidade não executa este exame neste horario."
		, AEL_00440 // “Para esta Unidade Solicitante, este exame não é executado em plantão.”
		, AEL_00443 // “Unidade executora deste exame não encontrada.”
		, AEL_00441 // “Este exame não é executado em plantão.”
		, AEL_00442 // “Para esta Unidade Solicitante não é permitido coleta urgente deste exame.”
		, AEL_00579 // Solicitacao nao Existe
		, AEL_01656B, AEL_01657B, AEL_01658B, AEL_01816B, AEL_01817B, AEL_01818B, AEL_00389, AEL_00420, AEL_00488, AEL_00581, AEL_00493, AEL_00447B, AEL_00451B, AEL_00452B, AEL_00454B, AEL_01153B, AEL_00455B, AEL_00456B, AEL_01155B, AEL_00457B, AEL_00458B, AEL_03404, 
		ERRO_NUMERO_REGISTRO_CONSELHO_NAO_CADASTRADO,
		// Estorno do cancelamento n\u00E3o \u00E9 permitido ap\u00F3s {1} horas.
		MSG_ADVERTENCIA_NENHUMA_ITEM_SELECIONADO_PARA_EXCLUSAO, SERVIDOR_RESPONSAVEL_SEM_USUARIO;

	}

	public void cancelarItensResponsabilidadeSolicitante(List<ItemSolicitacaoExameCancelamentoVO> itens, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor, RapServidores servidorLogado) throws BaseException {

		if (itens != null) {

			boolean hasItemSelecionado = false;

			for (int i = itens.size() - 1; i >= 0; i--) {

				ItemSolicitacaoExameCancelamentoVO itemSolicitacaoExameVO = itens.get(i);

				if (itemSolicitacaoExameVO.getExcluir() != null && itemSolicitacaoExameVO.getExcluir().booleanValue()) {

					itemSolicitacaoExameVO.setAelItemSolicitacaoExames(getAelItemSolicitacaoExameDAO().obterPorId(itemSolicitacaoExameVO.getAelItemSolicitacaoExames().getId().getSoeSeq(), itemSolicitacaoExameVO.getAelItemSolicitacaoExames().getId().getSeqp()));

					atualizarItensRespSolicitante(itemSolicitacaoExameVO.getAelItemSolicitacaoExames(), AghuParametrosEnum.P_SITUACAO_CANCELADO, AghuParametrosEnum.P_MOC_CANCELADO_PELO_SOLIC, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
					itens.remove(itemSolicitacaoExameVO);
					hasItemSelecionado = true;

				}

			}

			if (!hasItemSelecionado) {

				throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.MSG_ADVERTENCIA_NENHUMA_ITEM_SELECIONADO_PARA_EXCLUSAO);

			}

		}

	}

	/*
	 * PROCEDURE AELP_ATU_SITUACAO_ITEM
	 */
	private void atualizarItensRespSolicitante(AelItemSolicitacaoExames aelItemSolicitacaoExames, AghuParametrosEnum enumSituacao, 
			AghuParametrosEnum enumMotivoCancelamento, String nomeMicrocomputador, final Date dataFimVinculoServidor, RapServidores servidorLogado) throws BaseException {
		// Set situação
		AghParametros parametroSit = getParametroFacade().buscarAghParametro(enumSituacao);
		AelSitItemSolicitacoes vIseSituacao = getAelSitItemSolicitacoesDAO().obterPeloId(parametroSit.getVlrTexto());
		aelItemSolicitacaoExames.setSituacaoItemSolicitacao(vIseSituacao);

		// Set Motivo Cancelamento
		AghParametros parametroMotCancel = getParametroFacade().buscarAghParametro(enumMotivoCancelamento);
		// Buscar motivo do cacelamento e setar abaixo
		AelMotivoCancelaExames motivoCancelaExames = this.getAelMotivoCancelaExamesDAO().obterPeloId(parametroMotCancel.getVlrNumerico().shortValue());
		aelItemSolicitacaoExames.setAelMotivoCancelaExames(motivoCancelaExames);
		/*
		 * Atualiza BD AelItemSolicitacaoExames
		 */
		this.atualizar(aelItemSolicitacaoExames, null, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);

	}

	/**
	 * Insere objeto AelItemSolicitacaoExames no banco.
	 * @param servidorLogado 
	 * @param {AelItemSolicitacaoExames} item
	 * @return
	 * @throws BaseException
	 */
	public AelItemSolicitacaoExames inserir(AelItemSolicitacaoExames item, String nomeMicrocomputador, 
			RapServidores servidorLogado, final Date dataFimVinculoServidor, final boolean flush) throws BaseException {
		this.preInserir(null, item);

		this.inserirExame(item, nomeMicrocomputador, dataFimVinculoServidor, flush, servidorLogado);

		return item;
	}
	
	public AelItemSolicitacaoExames inserirExameDependente(AelItemSolicitacaoExames item, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor, final boolean flush, RapServidores servidorLogado) throws BaseException {
		this.preInserirExameDependete(null, item);

		this.inserirExame(item, nomeMicrocomputador, dataFimVinculoServidor, flush, servidorLogado);

		return item;
	}
	
	private void inserirExame(AelItemSolicitacaoExames item, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor, final boolean flush, RapServidores servidorLogado) throws BaseException  {
		
		this.getAelItemSolicitacaoExameDAO().persistir(item);

		getItemSolicitacaoExameEnforceRN().enforceInsert(item, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor, flush);
	}

	/**
	 * Insere objeto AelItemSolicitacaoExames no banco.
	 * @param {AelItemSolicitacaoExames} item
	 * @return
	 * @throws BaseException
	 */
	public AelItemSolicitacaoExames inserirContratualizacao(AelItemSolicitacaoExames item, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor, final boolean flush, RapServidores servidorLogado) throws BaseException {
		this.preInserir(null, item);

		this.getAelItemSolicitacaoExameDAO().persistir(item);

		getItemSolicitacaoExameEnforceRN().enforceInsertContratualizacao(item, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor, flush);

		return item;
	}

	/**
	 * ORADB AELT_ISE_BRI
	 * @param itemSolicitacaoExame
	 * @throws BaseException
	 */
	private void preInserir(AelItemSolicitacaoExames itemOriginal, AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {
		this.verificarPreInsercao(itemOriginal, itemSolicitacaoExame, false);
	}
	
	private void preInserirExameDependete(AelItemSolicitacaoExames itemOriginal, AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {
		this.verificarPreInsercao(itemOriginal, itemSolicitacaoExame, true);
	}
	
	private void verificarPreInsercao(AelItemSolicitacaoExames itemOriginal, AelItemSolicitacaoExames itemSolicitacaoExame, boolean isItemDependente) throws BaseException  {
		this.verificarExigeMaterialAnalise(itemSolicitacaoExame);
		this.verificarInformacaoClinica(itemSolicitacaoExame);
		
		//Validações necessários apenas se não for dependente
		if(!isItemDependente) {
			
//			RapServidores servidor = (itemSolicitacaoExame.getServidorResponsabilidade() != null) 
			//		? itemSolicitacaoExame.getServidorResponsabilidade()
			//				: servidorLogado;
			RapServidores servidor = itemSolicitacaoExame.getSolicitacaoExame().getServidorResponsabilidade(); //Deve pegar o servidor responsavel. Se nao existir (atd externo) nao valida.
			
			this.verificarConselhoProfissionalOuPermissao(
					itemSolicitacaoExame.getId(),
					itemSolicitacaoExame.getSolicitacaoExame(),
					servidor,
					itemSolicitacaoExame.getSolicitacaoExame().getAtendimento(),
					itemSolicitacaoExame.getExame(),
					itemSolicitacaoExame.getMaterialAnalise()
			);
			this.verificarEspecialidadeExame(itemSolicitacaoExame);
		}
		
		this.verificarTransicaoSituacao(itemOriginal, itemSolicitacaoExame);
		this.verificarSituacaoAtiva(itemSolicitacaoExame);
		this.verificarPermissoesQuandoGrupoConvenioSUS(itemSolicitacaoExame);
		this.verificarFormaRespiracao(itemSolicitacaoExame);
		this.verificarDataImpSumario(itemSolicitacaoExame);
		this.verificarExameMaterialAnalise(itemSolicitacaoExame);
		this.verificarServidorExameUnidadeConvenioSUS(itemSolicitacaoExame);
		this.atribuirCargaContador(itemSolicitacaoExame);
		this.verificarIndicativoImpressoLaudo(itemSolicitacaoExame);
		this.atribuirUnfSeqAvisa(itemSolicitacaoExame);
		this.verificarSituacaoExameMaterialAnalise(itemSolicitacaoExame);
		this.verificarCotaExameCadastroProjeto(itemSolicitacaoExame);
		this.atualizarCotaExameCadastroProjeto(itemSolicitacaoExame);
		/*
		 * Regra removida da Versão 1 de Exames
		 * 	this.verificarAPAnterior(itemSolicitacaoExame);
		 */
		this.setarIndicativoUsoO2Unidade(itemSolicitacaoExame);
		this.atribuirTipoTransporte(itemSolicitacaoExame);
	}

	/**
	 * RN6
	 * 
	 * ORADB Package aelk_ise_rn.rn_isep_ver_desc_mat
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarExigeMaterialAnalise(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		if (itemSolicitacaoExame.getMaterialAnalise() != null
				&& itemSolicitacaoExame.getMaterialAnalise()
				.getIndExigeDescMatAnls()
				&& StringUtils.isBlank(itemSolicitacaoExame
						.getDescMaterialAnalise())) {

			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00499);

		} else if (StringUtils.isNotBlank(itemSolicitacaoExame
				.getDescMaterialAnalise())
				&& (itemSolicitacaoExame.getMaterialAnalise() == null
						|| itemSolicitacaoExame.getMaterialAnalise()
						.getIndExigeDescMatAnls() == null || !itemSolicitacaoExame
						.getMaterialAnalise().getIndExigeDescMatAnls())) { // VERIFICA
			// SE
			// É
			// FALSE
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00500);
		}
	}

	/**
	 * RN7
	 * 
	 * ORADB Package aelk_ise_rn.rn_isep_ver_INFO_CLI
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarInformacaoClinica(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		// Busca AelUnfExecutaExames
		AelUnfExecutaExames aelUnfExecutaExames = this
		.buscaAelUnfExecutaExames(itemSolicitacaoExame);

		// aelUnfExecutaExames == null é o mesmo que IndExigeInfoClin == false,
		// então não entra no if
		// itemSolicitacaoExame.getSolicitacaoExame().getAtendimento() == null é
		// o mesmo que o atd_seq ser null
		if (aelUnfExecutaExames != null
				&& aelUnfExecutaExames.getIndExigeInfoClin()
				&& itemSolicitacaoExame.getSolicitacaoExame()
				.getInformacoesClinicas() == null
				&& (itemSolicitacaoExame.getSolicitacaoExame().getAtendimento() != null && itemSolicitacaoExame
						.getSolicitacaoExame().getAtendimento().getSeq() != null)) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00389);
		}
	}

	/**
	 * RN8
	 * 
	 * ORADB Package aelk_ise_rn.RN_ISEP_VER_CONSELHO
	 * 
	 * @param itemSolicitacaoExame
	 * @throws BaseException
	 */
	public void verificarConselhoProfissionalOuPermissao(
			AelItemSolicitacaoExamesId itemSolicitacaoExameId, 
			AelSolicitacaoExames solicitacaoExames, 
			RapServidores servidorResponsabilidade,
			AghAtendimentos atendimentoSolicitacao,
			AelExames exame,
			AelMateriaisAnalises materialAnalise) throws BaseException {
		if (itemSolicitacaoExameId != null
				&& itemSolicitacaoExameId.getSeqp() != null) {
			return;
		}

		AghParametros paramEhConsisteProtocoloAmb = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONSISTE_POP_AMB);
		boolean temConsistirPopAmb = (paramEhConsisteProtocoloAmb.getVlrTexto() != null && "S"
				.equalsIgnoreCase(paramEhConsisteProtocoloAmb.getVlrTexto()));

		if (solicitacaoExames == null) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00579);
		}

		if (servidorResponsabilidade == null) {
			return;
		}

		//Descomentar quando for desenvolvido solução para verificar se um usuário X tem a permissão Y.
		//Tarefa no Redmine: #8935
		if (temConsistirPopAmb && !cascaFacade.temPermissao(this.obterLoginUsuarioLogado(), "inibirFiltroExamesProtocEnfermagem", "pesquisar")) {
			if (atendimentoSolicitacao != null
					&& DominioOrigemAtendimento.A == atendimentoSolicitacao.getOrigem()
					&& temPermissaoConsistirProtocoloExamesAmbulatorial(servidorResponsabilidade)) {

				if (temPermissaoProtocoloEnfermagemExamesAmbulatorial(servidorResponsabilidade)) {
					if (!(existeProtolocoLiberado(atendimentoSolicitacao, exame))) {
						throw new ApplicationBusinessException(
								ItemSolicitacaoExameRNExceptionCode.AEL_01682); 
						/*“O exame e/ou profissional solicitante não autorizado para a agenda ligada a um Protocolo Enf Exames.”*/
					}
				} else {
					throw new ApplicationBusinessException(
							ItemSolicitacaoExameRNExceptionCode.AEL_02304);
				}
			} else {
				verificaConselhoProfissional(exame, servidorResponsabilidade, materialAnalise);
			}
		} else {
		verificaConselhoProfissional(exame, servidorResponsabilidade, materialAnalise);
		}
	}

	protected void verificaConselhoProfissional(AelExames exame,
			RapServidores servidorResponsabilidade,
			AelMateriaisAnalises materialAnalise)
	throws ApplicationBusinessException {
		String descricaoUsual = exame.getDescricaoUsual();
		if (!temConselhoPermiteSolicitarExame(
				servidorResponsabilidade, exame,
				materialAnalise)) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00444B, descricaoUsual);
		}
	}

	protected boolean temConselhoPermiteSolicitarExame(
			RapServidores servidorRespon, AelExames exame,
			AelMateriaisAnalises matAnalise) throws ApplicationBusinessException {
		AelExameConselhoProfsDAO dao = getAelExameConselhoProfsDAO();

		List<AelExameConselhoProfs> conselhos = dao
		.listarConselhosProfissionaisSolicitacaoExame(servidorRespon,
				exame, matAnalise);
		
		Boolean possuiNroRegConselho = Boolean.FALSE;
		
		for(AelExameConselhoProfs exConselho : conselhos) {
			if(existeNroRegConselho(servidorRespon, dao, exConselho)) {
				possuiNroRegConselho = Boolean.TRUE;
			}
		}
		
		return ((conselhos != null && !conselhos.isEmpty()) && possuiNroRegConselho);
	}

	private Boolean existeNroRegConselho(RapServidores servidorRespon,	AelExameConselhoProfsDAO dao, AelExameConselhoProfs conselhos) throws ApplicationBusinessException {
		
		Boolean existeNroRegConselho = dao.buscarNroRegConselhoPorPessoaFisica(servidorRespon.getId().getMatricula(), servidorRespon.getId().getVinCodigo(), conselhos.getConselhosProfissionais().getSigla());
		if (!existeNroRegConselho) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.ERRO_NUMERO_REGISTRO_CONSELHO_NAO_CADASTRADO);
		}
		return existeNroRegConselho;
	}

	protected boolean existeProtolocoLiberado(
			AghAtendimentos atendimentoSolic, AelExames exame) {
		if (atendimentoSolic != null
				&& atendimentoSolic.getEspecialidade() != null
				&& exame != null) {
			List<MpaPops> list = this.getProtocoloFacade().pesquisarProtocoloLiberado(
					atendimentoSolic.getEspecialidade(),
					exame);
			return (list != null && !list.isEmpty());
		}
		return false;
	}

	protected boolean temPermissaoProtocoloEnfermagemExamesAmbulatorial(
			RapServidores servidor) throws ApplicationBusinessException {
		if(servidor == null){
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_02304);
		}
		return getICascaFacade().temPermissao(servidor.getUsuario(), "listarExamesProtocEnfermagem", "pesquisar");
	}

	protected boolean temPermissaoConsistirProtocoloExamesAmbulatorial(
			RapServidores servidor) throws ApplicationBusinessException {
		if(servidor == null){
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_01682);
		} else if(servidor.getUsuario() == null) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.SERVIDOR_RESPONSAVEL_SEM_USUARIO);
		}
		return getICascaFacade().temPermissao(servidor.getUsuario(), "elaborarSolicitacaoExame", "consistirProtocoloParaExamesAmbulatoriais");
	}

	/**
	 * RN9
	 * 
	 * ORADB Package aelk_ise_rn.RN_ISEP_VER_EXME_ESP
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarEspecialidadeExame(AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {

		FatConvenioSaude convenioSaude = this.obterFatConvenioSaude(itemSolicitacaoExame.getSolicitacaoExame());

		RapServidores usuarioResponsavel = itemSolicitacaoExame.getSolicitacaoExame().getServidorResponsabilidade();

		this.verificarEspecialidadeExame(itemSolicitacaoExame, convenioSaude, usuarioResponsavel);

	}

	/**
	 * RN9
	 * 
	 * ORADB Package aelk_ise_rn.RN_ISEP_VER_EXME_ESP
	 * 
	 * @param itemSolicitacaoExame
	 * @param convenioSaude
	 * @param usuarioResponsavel
	 * @throws ApplicationBusinessException
	 */
	public void verificarEspecialidadeExame(AelItemSolicitacaoExames itemSolicitacaoExame, FatConvenioSaude convenioSaude, RapServidores usuarioResponsavel) throws ApplicationBusinessException {

		if (convenioSaude != null && DominioGrupoConvenio.S == convenioSaude.getGrupoConvenio() && usuarioResponsavel != null) {

			Boolean servidorEspecialidadeValido = null;

			/*
			 * Quando o item solicitado estiver cadastrado na tabela exame unidade especialidade, 
			 * o mesmo so pode ser solicitado por um usuario que esteja cadastrado
			 * na mesma especialidade desta tabela (exame unidade especialidade).
			 *
			 */
			List<AelExamesEspecialidade>  listaExamesEspecialidade = getAelExamesEspecialidadeDAO().buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeq(
					itemSolicitacaoExame.getExame().getSigla(),
					itemSolicitacaoExame.getMaterialAnalise().getSeq(),
					itemSolicitacaoExame.getUnidadeFuncional().getSeq());

			// Percorre as especialidades do exame
			for (AelExamesEspecialidade especialidadeExame : listaExamesEspecialidade) {

				servidorEspecialidadeValido = false;

				// Percorre as especialidades do servidor responsavel
				for (AghProfEspecialidades especialidadeResponsavel : getAghuFacade().listarEspecialidadesPorServidor(usuarioResponsavel)) {

					// Compara a especialidade do servidor responsavel com a especialidade do exame
					final short seqEspecialidadeServidorResponsavel = especialidadeResponsavel.getId().getEspSeq().shortValue();
					final short seqEspecialidadeExame = especialidadeExame.getId().getEspSeq();
					if (seqEspecialidadeServidorResponsavel == seqEspecialidadeExame) {
						servidorEspecialidadeValido = true;
						break;
					}

				}

				if (servidorEspecialidadeValido) {
					break;
				}

			}

			// Verifica se o exame exige um servidor responsavel com a mesma especialidade
			if (servidorEspecialidadeValido != null && !servidorEspecialidadeValido) {
				throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.AEL_00493, itemSolicitacaoExame.getExame().getDescricaoUsual());
			}
		}

	}

	/**
	 * RN10<br>
	 * 
	 * ORADB Package aelk_ise_rn.RN_ISEP_VER_SITUACAO.<br>
	 * 
	 * No insert a situacao deve ser qualquer situacao da fk "PARA" onde a fk
	 * "de" for = null da entidade matriz situacao.<br>
	 * 
	 * No update a situacao deve ser qualquer situacao da fk "PARA" da situacao
	 * "DE" que eh igual ao old da item solicitacao exame.<br>
	 * 
	 * Tanto no insert como no update deve ser verificado a autorizacao
	 * alteracao situacao (perfil do usuario).<br>
	 * 
	 * @param itemOriginal
	 * @param item
	 * @throws ApplicationBusinessException 
	 * @throws BaseException 
	 */
	protected void verificarTransicaoSituacao(AelItemSolicitacaoExames itemOriginal, AelItemSolicitacaoExames item) throws BaseException
	 {
		if (item == null || item.getSituacaoItemSolicitacao() == null) {
			throw new IllegalArgumentException(
			"Parametro obrigatorio nao informado!!!");
		}
		AelMatrizSituacao matrizSituacao = verificarPermissaoMatrizTransicao(itemOriginal, item);
		verificarPermissaoResponsavelTransicaoSituacao(matrizSituacao, item);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void verificarPermissaoResponsavelTransicaoSituacao(AelMatrizSituacao matrizSituacao,
			AelItemSolicitacaoExames item) throws BaseException {
		RapServidores servidor = null;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final String sitCodigoAgendado = "AG";
		final String sitCodigoAColetar = "AC";
		final String sitCodigoAExecutar = "AX";
		final String sitCodigoPendente = "PE";
		
		boolean temPermissao = true;
		if (((item.getId() == null) 
				|| (item.getSolicitacaoExame() != null	
						&& item.getSolicitacaoExame().getAtendimento() != null
						&& DominioOrigemAtendimento.A.equals(item.getSolicitacaoExame().getAtendimento().getOrigem())
					) && (
							matrizSituacao.getSituacaoItemSolicitacao() != null 
							&& matrizSituacao.getSituacaoItemSolicitacaoPara() != null
							&& (  (sitCodigoAgendado.equalsIgnoreCase(matrizSituacao.getSituacaoItemSolicitacao().getCodigo())
									&& sitCodigoPendente.equalsIgnoreCase(matrizSituacao.getSituacaoItemSolicitacaoPara().getCodigo())
								  )
								||	
								  (sitCodigoAColetar.equalsIgnoreCase(matrizSituacao.getSituacaoItemSolicitacao().getCodigo())							
									&& (sitCodigoAgendado.equalsIgnoreCase(matrizSituacao.getSituacaoItemSolicitacaoPara().getCodigo()) 
											|| sitCodigoPendente.equalsIgnoreCase(matrizSituacao.getSituacaoItemSolicitacaoPara().getCodigo()))
								  )
								||	
								  (sitCodigoAExecutar.equalsIgnoreCase(matrizSituacao.getSituacaoItemSolicitacao().getCodigo())
									&& (sitCodigoAgendado.equalsIgnoreCase(matrizSituacao.getSituacaoItemSolicitacaoPara().getCodigo()) 
											|| sitCodigoPendente.equalsIgnoreCase(matrizSituacao.getSituacaoItemSolicitacaoPara().getCodigo()))
								  )	
							   )
					) && item.isOrigemTelaSolicitacao() 
			) && (item.getSolicitacaoExame() != null 
					&& item.getSolicitacaoExame().getServidorResponsabilidade() != null)) {
			servidor = item.getSolicitacaoExame().getServidorResponsabilidade();
		} else {
			servidor = servidorLogado;
		}

		if (servidor.equals(servidorLogado)) {
			temPermissao = getExamesFacade().validarPermissaoAlterarExameSituacao(matrizSituacao, servidor);

			if (!temPermissao && matrizSituacao.getSituacaoItemSolicitacao() != null
					&& matrizSituacao.getSituacaoItemSolicitacao().getDescricao() != null) {

				String situacaoDe = "";
				String situacaoPara = "";

				if (matrizSituacao.getSituacaoItemSolicitacao() != null) {

					situacaoDe = matrizSituacao.getSituacaoItemSolicitacao().getDescricao();

				}

				if (matrizSituacao.getSituacaoItemSolicitacaoPara() != null) {

					situacaoPara = matrizSituacao.getSituacaoItemSolicitacaoPara().getDescricao();

				}

				throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.AEL_00728, situacaoDe, situacaoPara);
			}
		} else {
			temPermissao = getExamesFacade().validarPermissaoAlterarExameSituacao(matrizSituacao, servidor);
			if (!temPermissao) {
				throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.AEL_01872);
			}
		}
	}


	/**
	 * Busca um AelMatrizSituacao que permite a transicao da situacao atual para
	 * a nova situacao informada.<br>
	 * 
	 * @param item
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected AelMatrizSituacao verificarPermissaoMatrizTransicao(AelItemSolicitacaoExames itemOriginal,
			AelItemSolicitacaoExames item)
	throws ApplicationBusinessException {
		AelSitItemSolicitacoes sitCodigoOriginal = null;
		if (item.getId() != null) {
			if (itemOriginal != null) {
				sitCodigoOriginal = itemOriginal.getSituacaoItemSolicitacao();
			}
		}

		List<AelMatrizSituacao> lista = getAelMatrizSituacaoDAO()
		.pesquisarMatrizSituacaoPorSituacaoOrigem(sitCodigoOriginal);
		boolean transicoesOk = false;
		AelMatrizSituacao matrizSituacao = null;
		for (AelMatrizSituacao matrizSit : lista) {
			if (item.getSituacaoItemSolicitacao().equals(matrizSit.getSituacaoItemSolicitacaoPara())) {
				matrizSituacao = matrizSit;
				transicoesOk = true;
				break;
			}
		}
		if (!transicoesOk) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00477);
		}

		return matrizSituacao;
	}

	/**
	 * RN11
	 * 
	 * @oradb RN_ISEP_VER_SIT_ATIV
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarSituacaoAtiva(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		if (!itemSolicitacaoExame.getSituacaoItemSolicitacao().getIndSituacao()
				.isAtivo()) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00420);
		}
	}

	/**
	 * RN12
	 * 
	 * ORADB Package aelk_ise_rn.rn_isep_ver_PERMISSA
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarPermissoesQuandoGrupoConvenioSUS(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws BaseException {
		FatConvenioSaude convenioSaude = this
		.obterFatConvenioSaude(itemSolicitacaoExame
				.getSolicitacaoExame());
		if (convenioSaude != null && DominioGrupoConvenio.S != convenioSaude.getGrupoConvenio()) {
			return;
		}
		AelPermissaoUnidSolic permissaoUnidSolicitante = this
		.verificaPermissaoUnidadeSolicitante(itemSolicitacaoExame);

		this.verificaTransporteO2Paciente(itemSolicitacaoExame,
				permissaoUnidSolicitante);

		this.verificaDataProgramacao(itemSolicitacaoExame,
				permissaoUnidSolicitante);

		this.verificaExecucaoExamePlantao(itemSolicitacaoExame,
				permissaoUnidSolicitante);

		this.verificaColetaUrgente(itemSolicitacaoExame,
				permissaoUnidSolicitante);
	}

	/**
	 * 5) Verifica Coleta Urgente.<br>
	 * 
	 * Verificar na tabela permissao unidade solicitante se permite coletar
	 * urgente para esta unidade solicitante e unid func exame material analise
	 * quando o tipo coleta(da item solicitacao exame) = 'U'.<br>
	 * 
	 * ORADB procedure aelp_coleta_urgente [local]
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificaColetaUrgente(
			AelItemSolicitacaoExames itemSolicitacaoExame,
			AelPermissaoUnidSolic permissaoUnidSolicitante)
	throws ApplicationBusinessException {
		if (DominioTipoColeta.U == itemSolicitacaoExame.getTipoColeta() 
				&& !permissaoUnidSolicitante.getIndPermiteColetarUrgente()) {
			if (itemSolicitacaoExame.getMaterialAnalise() != null
					&& itemSolicitacaoExame.getMaterialAnalise().getIndColetavel()) {
				// “Para esta Unidade Solicitante não é permitido coleta urgente deste exame.”
				throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.AEL_00442, itemSolicitacaoExame.getExame().getDescricaoUsual());
			}
		}
	}

	/**
	 * 4) Verifica Execucao Exame Plantao<br>
	 * 
	 * ORADB Procedure aelp_executa_plantao [local]
	 * 
	 * Verificar na tabela permissao unidade solicitante se permite solicitar em
	 * plant¿o para esta unidade solicitante e unid func exame material analise
	 * quando o tipo coleta(da item solicitacao exame) = 'U'.<br>
	 * 
	 * @param itemSolicitacaoExame
	 * @throws BaseException
	 */
	protected void verificaExecucaoExamePlantao(
			AelItemSolicitacaoExames itemSolicitacaoExame,
			AelPermissaoUnidSolic permissaoUnidSolicitante)
	throws BaseException {
		if (DominioTipoColeta.N == itemSolicitacaoExame.getTipoColeta()) {
			return;
		}

		AghHorariosUnidFuncional horarioUnidFuncional = this
		.verificaExecucaoExamePlantao(
				itemSolicitacaoExame.getUnidadeFuncional(),
				itemSolicitacaoExame.getDthrProgramada());

		if (horarioUnidFuncional == null) {
			// “Unidade não executa este exame neste horario.”
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00439);
		}

		if (horarioUnidFuncional.getIndPlantao()) {
			// ind_permite_solicitar_plantao
			if (!permissaoUnidSolicitante.getIndPermiteSolicitarPlantao()) {
				// “Para esta Unidade Solicitante, este exame não é executado em
				// plantão.”
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_00440);

			} else {
				AelUnfExecutaExames unidFuncionalExames = this
				.buscaAelUnfExecutaExames(itemSolicitacaoExame);

				if (unidFuncionalExames == null) {
					// “Unidade executora deste exame não encontrada.”
					throw new ApplicationBusinessException(
							ItemSolicitacaoExameRNExceptionCode.AEL_00443);
				}

				if (!unidFuncionalExames.getIndExecutaEmPlantao()) {
					//“Este exame não é executado em plantão.”
					throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.AEL_00441);
				}
			}
		}// IF indPlantao
	}

	public AghHorariosUnidFuncional verificaExecucaoExamePlantao(
			AghUnidadesFuncionais unfExecutora, Date dataHoraProgramada)
	throws BaseException {
		DominioTipoDia tipoDia = this.getTipoDia(dataHoraProgramada);

		AghHorariosUnidFuncional horarioUnidFuncional = this
		.getAghuFacade().obterHorarioUnidadeFuncionalPor(unfExecutora, tipoDia,
				dataHoraProgramada);

		return horarioUnidFuncional;
	}

	private DominioTipoDia getTipoDia(Date dtHrProgramada)
	throws ApplicationBusinessException {
		DominioTipoDia tipoDia = null;

		AghFeriados feriado = this.getAghuFacade().obterFeriado(
				dtHrProgramada);
		tipoDia = this.calcularTipoDia(feriado, dtHrProgramada);

		if (tipoDia == null) {
			Date proxDia = DateUtil.adicionaDias(dtHrProgramada, 1);

			feriado = this.getAghuFacade().obterFeriado(proxDia);
			tipoDia = this.calcularTipoDia(feriado, dtHrProgramada);

			Date horaVesperaFeriado = this
			.getParametroSistemaHora(AghuParametrosEnum.P_HORA_VESPERA_FERIADO);

			if (tipoDia != null
					&& (feriado.getTurno() == null || (DominioTurno.M == feriado
							.getTurno() && DateValidator.validaDataMenorIgual(
									proxDia, horaVesperaFeriado)))) {
				tipoDia = DominioTipoDia.VFE;
			} else {
				String diaDaSemana = DateFormatUtil.diaDaSemana(dtHrProgramada);
				tipoDia = DominioTipoDia.getInstance(diaDaSemana);
			}
		}// IF tipodia nulo.

		return tipoDia;
	}

	/**
	 * Calcula o tipo de dia apartir do Feriado relacionado a Data.
	 * 
	 * @param feriado
	 * @param dtHrProgramada
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected DominioTipoDia calcularTipoDia(AghFeriados feriado,
			Date dtHrProgramada) throws ApplicationBusinessException {
		DominioTipoDia tipoDia = null;

		if (feriado != null) {
			if (DominioTurno.M == feriado.getTurno()) {
				Date horaManhaFeriado = this
				.getParametroSistemaHora(AghuParametrosEnum.P_HORA_MANHA_FERIADO);

				if (DateUtil.validaHoraMenorIgual(dtHrProgramada,
						horaManhaFeriado)) {
					tipoDia = DominioTipoDia.FER;
				}

			} else if (DominioTurno.T == feriado.getTurno()) {
				Date horaManhaFeriado = this
				.getParametroSistemaHora(AghuParametrosEnum.P_HORA_MANHA_FERIADO);

				if (DateUtil.validaHoraMaior(dtHrProgramada, horaManhaFeriado)) {
					tipoDia = DominioTipoDia.FER;
				}

			} else if (DominioTurno.N == feriado.getTurno()) {
				Date horaManhaFeriado = this
				.getParametroSistemaHora(AghuParametrosEnum.P_HORA_TARDE_FERIADO);

				if (DateUtil.validaHoraMaior(dtHrProgramada, horaManhaFeriado)) {
					tipoDia = DominioTipoDia.FER;
				}

			} else {
				tipoDia = DominioTipoDia.FER;
			}
		}// IF tem feriado

		return tipoDia;
	}

	/**
	 * 3) Verifica Data Programacao.
	 * 
	 * Se o exame material analise eh solicitado via sistema nao verifica a data
	 * de programacao.<br>
	 * Verifica se eh um exame solicitado via sistema.<br>
	 * 
	 * Permitir data programada para aquelas unidades solicitantes que estiverem
	 * com o ind permite programar exames = 'S' na tabela
	 * ael_permissao_unid_solics e o periodo permitido eh a sysdate + o
	 * parametro p_prazo_max_solic_programado(em dias) no sistema de parametros.
	 * Quando n¿o informada pelo usu¿rio assumir¿ o criado_em da tabela
	 * ael_solicitacao_exames. <br>
	 * 
	 * 
	 * @param itemSolicitacaoExame
	 * @throws BaseException
	 */
	protected void verificaDataProgramacao(
			AelItemSolicitacaoExames itemSolicitacaoExame,
			AelPermissaoUnidSolic permissaoUnidSolicitante)
	throws BaseException {
		AelExamesMaterialAnalise exameMaterial = getAelExamesMaterialAnaliseDAO()
		.buscarAelExamesMaterialAnalisePorId(
				itemSolicitacaoExame.getExame().getSigla(),
				itemSolicitacaoExame.getMaterialAnalise().getSeq());
		if (exameMaterial.getIndSolSistema() == null
				|| !exameMaterial.getIndSolSistema()) {

			if (itemSolicitacaoExame.getDthrProgramada() == null) {
				itemSolicitacaoExame.setDthrProgramada(itemSolicitacaoExame
						.getSolicitacaoExame().getCriadoEm());
				return;
			}

			AghParametros paramSysTempoMesmoMomento = getParametroFacade()
			.buscarAghParametro(
					AghuParametrosEnum.P_TEMPO_MINUTOS_SOLIC);
			Integer tempoMesmoMomentoEmMinuto = paramSysTempoMesmoMomento
			.getVlrNumerico().intValue();

			Date dtInicio = DateUtil.adicionaMinutos(
					itemSolicitacaoExame.getDthrProgramada(),
					(tempoMesmoMomentoEmMinuto * -1));
			Date dtFinal = DateUtil.adicionaMinutos(
					itemSolicitacaoExame.getDthrProgramada(),
					tempoMesmoMomentoEmMinuto);

			if (DateUtil.validaDataMaiorIgual(itemSolicitacaoExame
					.getSolicitacaoExame().getCriadoEm(), dtInicio)
					&& DateValidator.validaDataMenorIgual(itemSolicitacaoExame
							.getSolicitacaoExame().getCriadoEm(), dtFinal)) {
				return;
			}

			List<DominioSimNaoRotina> listaDominioSimNaoRotina = Arrays.asList(
					DominioSimNaoRotina.S, DominioSimNaoRotina.R);
			if (!listaDominioSimNaoRotina.contains(permissaoUnidSolicitante
					.getIndPermiteProgramarExames())) {
				// “Não é permitido programar este exame nesta Unidade
				// Solicitante.”
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_00430);
			}

			AghParametros paramSysPrazoMaxSolicProg = getParametroFacade()
			.buscarAghParametro(
					AghuParametrosEnum.P_PRAZO_MAX_SOLIC_PROGRAMADA);
			Integer prazoMaxSolicProgEmDias = paramSysPrazoMaxSolicProg
			.getVlrNumerico().intValue();

			Date dtCalculada = DateUtil.adicionaDias(itemSolicitacaoExame
					.getSolicitacaoExame().getCriadoEm(),
					prazoMaxSolicProgEmDias);

			if (DateUtil.validaDataMaior(
					itemSolicitacaoExame.getDthrProgramada(), dtCalculada)) {
				// “Programação de exames não pode ser superior a
				// P_PRAZO_MAX_SOLIC_PROGRAMADA dias.”
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_00467,
						prazoMaxSolicProgEmDias);
			}
		}// IF nao eh solicitado pelo sistema
	}

	/**
	 * 2) Verifica Transporte O2 Paciente<br>
	 * 
	 * ORADB PROCEDURE [local] aelp_exige_transporte<br>
	 * 
	 * Exigir transporte/O2 quando o ind exige transporte da tabela permissao
	 * unidade solicitante for igual a "S"<br>
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificaTransporteO2Paciente(
			AelItemSolicitacaoExames itemSolicitacaoExame,
			AelPermissaoUnidSolic permissaoUnidSolicitante)
	throws ApplicationBusinessException {
		if (permissaoUnidSolicitante.getIndExigeTransporteO2()
				&& (itemSolicitacaoExame.getIndUsoO2() == null || itemSolicitacaoExame
						.getTipoTransporte() == null)) {
			// “Obrigatório a informação do tipo de transporte para o paciente e
			// se necessita O2.”
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00427);
		}
	}

	/**
	 * 1) Verifica Permissao Unidade Solicitante.
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected AelPermissaoUnidSolic verificaPermissaoUnidadeSolicitante(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		AelPermissaoUnidSolic permissaoUnidadeSolicitante = getAelPermissaoUnidSolicDAO()
		.buscarAelPermissaoUnidSolicPor(
				itemSolicitacaoExame.getExame(), // c_ufe_ema_exa_sigla
				itemSolicitacaoExame.getMaterialAnalise(), // c_ufe_ema_man_seq
				itemSolicitacaoExame.getUnidadeFuncional(), // c_ufe_unf_seq
				itemSolicitacaoExame.getSolicitacaoExame()
				.getUnidadeFuncional() // soe.unf_seq
		);

		if (permissaoUnidadeSolicitante == null) {
			// “Permissão da Unidade Solicitante não está cadastrada.”
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00426C,
					itemSolicitacaoExame.getExame().getDescricaoUsual());
		}

		return permissaoUnidadeSolicitante;
	}

	protected Date getParametroSistemaHora(AghuParametrosEnum paramSys)
	throws ApplicationBusinessException {
		Date horaReturno = null;

		if (paramSys != null) {
			AghParametros paramSysHr = getParametroFacade().buscarAghParametro(
					paramSys);
			String strHoraTardeFeriado = paramSysHr.getVlrTexto();

			//Por padrão do banco, o campo vlr_texto sempre será 1400=14:00
			if (strHoraTardeFeriado.length() > 3) {
				int hora = Integer.valueOf(strHoraTardeFeriado.substring(0,2));
				int minuto = Integer.valueOf(strHoraTardeFeriado.substring(2,4)); 
				horaReturno = DateUtil.obterData(2011, 1, 1, hora, minuto);
			}
		}

		return horaReturno;
	}

	/**
	 * RN13
	 * 
	 * ORADB AELK_ISE_RN.RN_ISEP_VER_RESPIR
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarFormaRespiracao(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		//
		// Recupera o ExameMaterialAnalise
		AelExamesMaterialAnalise exameMaterialAnalise = getAelExamesMaterialAnaliseDAO()
		.buscarAelExamesMaterialAnalisePorId(
				itemSolicitacaoExame.getExame().getSigla(),
				itemSolicitacaoExame.getMaterialAnalise().getSeq());
		String descricaoUsualExame = itemSolicitacaoExame.getExame().getDescricaoUsual();
		if (exameMaterialAnalise.getIndFormaRespiracao()) {
			if (itemSolicitacaoExame.getFormaRespiracao() == null) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_01656B, descricaoUsualExame);
			} else if (DominioFormaRespiracao.DOIS == itemSolicitacaoExame
					.getFormaRespiracao()) { 
				if (itemSolicitacaoExame.getLitrosOxigenio() == null) {
					// Não informou os litros do oxigênio (litros_oxigenio)
					throw new ApplicationBusinessException(
							ItemSolicitacaoExameRNExceptionCode.AEL_01657B, descricaoUsualExame);
				}	
			} else if (DominioFormaRespiracao.TRES == itemSolicitacaoExame
					.getFormaRespiracao()) { 
				if (itemSolicitacaoExame.getPercOxigenio() == null) {
					// Não informou o percentual de oxigênio (perc_oxigenio)
					throw new ApplicationBusinessException(
							ItemSolicitacaoExameRNExceptionCode.AEL_01658B, descricaoUsualExame);
				}	
			}
		} else {
			if (itemSolicitacaoExame.getFormaRespiracao() != null) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_01816B, descricaoUsualExame);
			} else if (itemSolicitacaoExame.getLitrosOxigenio() != null) {
				// Informou os litros do oxigênio (litros_oxigenio)
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_01817B, descricaoUsualExame);
			} else if (itemSolicitacaoExame.getPercOxigenio() != null) {
				// Informou o percentual de oxigênio (perc_oxigenio)
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_01818B, descricaoUsualExame);
			}
		}
	}

	/**
	 * RN14
	 * 
	 * ORADB Package aelk_ise_rn.rn_isep_ver_DATA_IMP
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarDataImpSumario(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		if (itemSolicitacaoExame.getDataImpSumario() != null) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00445);
		}
	}

	/**
	 * RN15
	 * 
	 * ORADB PACKAGE AELK_ISE_RN.RN_ISEP_ATU_SIT_AMOS
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void verificarExameMaterialAnalise(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		AelExamesMaterialAnalise exameMaterialAnalise = getAelExamesMaterialAnaliseDAO()
		.buscarAelExamesMaterialAnalisePorId(
				itemSolicitacaoExame.getExame().getSigla(),
				itemSolicitacaoExame.getMaterialAnalise().getSeq());

		String descricaoUsual = itemSolicitacaoExame.getExame().getDescricaoUsual();
		// Verifica região anatômica
		if (exameMaterialAnalise.getIndExigeRegiaoAnatomica()) {
			if (itemSolicitacaoExame.getDescRegiaoAnatomica() == null
					&& itemSolicitacaoExame.getRegiaoAnatomica() == null) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_00447B, descricaoUsual);
			}
		}

		// Verifica o indSolicInformaColetas
		if (exameMaterialAnalise.getIndSolicInformaColetas()) {
			if (itemSolicitacaoExame.getNroAmostras() == null
					&& !itemSolicitacaoExame.getIndGeradoAutomatico()) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_00451B, descricaoUsual);
			}
		} else {
			if (itemSolicitacaoExame.getNroAmostras() != null) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_00452B, descricaoUsual);
			}
		}

		// Verifica o preenchimento de amostras
		if (itemSolicitacaoExame.getNroAmostras() != null && itemSolicitacaoExame.getNroAmostras() > 1) {
			if (exameMaterialAnalise.getUnidTempoColetaAmostras() == DominioUnidTempo.D) {
				if (itemSolicitacaoExame.getIntervaloDias() == null) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoExameRNExceptionCode.AEL_00454B, descricaoUsual);
				} else if (itemSolicitacaoExame.getIntervaloDias() == 0) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoExameRNExceptionCode.AEL_01153B, descricaoUsual);
				}
			} else if (exameMaterialAnalise.getUnidTempoColetaAmostras() == DominioUnidTempo.H) {
				if (itemSolicitacaoExame.getIntervaloHoras() == null) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoExameRNExceptionCode.AEL_00455B, descricaoUsual);
				} else {
					int tempoIntervaloHorasEmMinutos = DateUtil
					.getHoras(itemSolicitacaoExame.getIntervaloHoras()) * 60;
					tempoIntervaloHorasEmMinutos += DateUtil
					.getMinutos(itemSolicitacaoExame
							.getIntervaloHoras());

					if (tempoIntervaloHorasEmMinutos == 0) {
						throw new ApplicationBusinessException(
								ItemSolicitacaoExameRNExceptionCode.AEL_01153B, descricaoUsual);
					} else {
						try {
							AghParametros parametro = getParametroFacade()
							.buscarAghParametro(
									AghuParametrosEnum.P_TEMPO_MINUTOS_SOLIC);
							if (parametro != null) {
								if (tempoIntervaloHorasEmMinutos <= parametro
										.getVlrNumerico().intValue()) {
									throw new ApplicationBusinessException(
											ItemSolicitacaoExameRNExceptionCode.AEL_01155B, descricaoUsual, parametro.getVlrNumerico());
								}
							}
						} catch (ApplicationBusinessException e) {
							throw new IllegalArgumentException(
							"Parametro P_TEMPO_MINUTOS_SOLIC nao encontrado.");
						}
					}
				}
			} else {
				if (itemSolicitacaoExame.getIntervaloDias() != null
						|| itemSolicitacaoExame.getIntervaloHoras() == null) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoExameRNExceptionCode.AEL_00456B, descricaoUsual);
				}
			}
		}

		// Verifica intervalo cadastrado
		if (exameMaterialAnalise.getIndUsaIntervaloCadastrado()) {
			if (itemSolicitacaoExame.getIntervaloColeta() == null) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_00457B, descricaoUsual);
			}
		} else {
			if (itemSolicitacaoExame.getIntervaloColeta() != null) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_00458B, descricaoUsual);
			}
		}
	}

	/**
	 * RN16
	 * 
	 * ORADB Package aelk_ise_rn.rn_isep_ver_SERV_EXM
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarServidorExameUnidadeConvenioSUS(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		FatConvenioSaude convenioSaude = this
		.obterFatConvenioSaude(itemSolicitacaoExame
				.getSolicitacaoExame());

		if (convenioSaude != null && DominioGrupoConvenio.S == convenioSaude.getGrupoConvenio()
				&& itemSolicitacaoExame.getSolicitacaoExame()
				.getServidorResponsabilidade() != null) {
			RapServidores servResp = itemSolicitacaoExame.getSolicitacaoExame()
			.getServidorResponsabilidade();

			AelServidoresExameUnidDAO dao = getAelServidoresExameUnidDAO();
			List<AelServidoresExameUnid> lista = dao
			.buscaListaAelServidoresExameUnid(
					itemSolicitacaoExame.getExame(),
					itemSolicitacaoExame.getMaterialAnalise(),
					itemSolicitacaoExame.getUnidadeFuncional());
			boolean servidorPodeSolicitarExame = true;

			if (lista != null && !lista.isEmpty()) {
				servidorPodeSolicitarExame = false;
				for (AelServidoresExameUnid servidorExameUnid : lista) {
					if (servidorExameUnid.getServidor().equals(servResp)
							|| servidorExameUnid.getServidor().equals(
									servidorLogado)) {
						servidorPodeSolicitarExame = true;
						break;
					}
				}// FOR
			}

			if (!servidorPodeSolicitarExame) {
				String descricaoUsual = itemSolicitacaoExame.getExame().getDescricaoUsual();
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_00490B, descricaoUsual);
			}
		}
	}

	/**
	 * Busca um FatConvenioSaude de uma Solicitacao de exames.<br>
	 * ORADB function AELC_VER_GRUPO_CNV_G.<br>
	 * Pode retornar nulo.<br>
	 * 
	 * @param solicitacaoExame
	 * @return um FatConvenioSaude.
	 */
	public FatConvenioSaude obterFatConvenioSaude(AelSolicitacaoExames solicitacaoExame) {
		FatConvenioSaudePlano convenioSaudeplano = null;

		//inicio - modificar para usar:
		//convenioSaudeplano = obterFatConvenioSaudePlano(solicitacaoExame);
		if (solicitacaoExame.getAtendimento() != null) {
			convenioSaudeplano = this.obterFatConvenioSaudePlano(solicitacaoExame.getAtendimento(), null);
		} else if (solicitacaoExame.getAtendimentoDiverso() != null) {
			convenioSaudeplano = this.obterFatConvenioSaudePlano(null, solicitacaoExame.getAtendimentoDiverso());
		}
		//fim

		return (convenioSaudeplano != null) ? convenioSaudeplano.getConvenioSaude() : null;
	}

	public FatConvenioSaudePlano obterFatConvenioSaudePlano(AelSolicitacaoExames solicitacaoExame) {
		FatConvenioSaudePlano convenioSaudeplano = null;

		if (solicitacaoExame.getAtendimento() != null) {
			convenioSaudeplano = this.obterFatConvenioSaudePlano(solicitacaoExame.getAtendimento(), null);
		} else if (solicitacaoExame.getAtendimentoDiverso() != null) {
			convenioSaudeplano = this.obterFatConvenioSaudePlano(null, solicitacaoExame.getAtendimentoDiverso());
		}

		return convenioSaudeplano;
	}


	/**
	 * Busca um FatConvenioSaude de uma Solicitacao de exames.<br>
	 * ORADB function AELC_VER_GRUPO_CNV_G.<br>
	 * Pode retornar nulo.<br>
	 * 
	 * @param atendimento
	 * @param atendimentoDiverso
	 * @return um FatConvenioSaude.
	 */
	public FatConvenioSaude obterFatConvenioSaude(AghAtendimentos atendimento, AelAtendimentoDiversos atendimentoDiverso) {
		FatConvenioSaudePlano convenioSaudeplano = this.obterFatConvenioSaudePlano(atendimento, atendimentoDiverso);
		return (convenioSaudeplano != null) ? convenioSaudeplano.getConvenioSaude() : null;		
	}

	private FatConvenioSaudePlano obterFatConvenioSaudePlano(AghAtendimentos atendimento, AelAtendimentoDiversos atendimentoDiverso) {
		if (atendimento == null && atendimentoDiverso == null) {
			throw new IllegalArgumentException("Um dos parametros deve ser informado: AghAtendimentos ou AelAtendimentoDiversos");
		}
		FatConvenioSaudePlano convenioSaudeplano = null;

		if (atendimento != null) {
			ConvenioExamesLaudosVO convenioExameVO = this.getPacienteFacade().buscarConvenioExamesLaudos(atendimento.getSeq());
			convenioSaudeplano = this.getFaturamentoFacade().obterConvenioSaudePlano(convenioExameVO.getCodigoConvenioSaude(), convenioExameVO.getCodigoConvenioSaudePlano());
		} else if (atendimentoDiverso != null) {
			ConvenioExamesLaudosVO vo = this.getExamesFacade().rnAelpBusConvAtv(atendimentoDiverso.getSeq());
			if (vo != null) {
				convenioSaudeplano = this.getFaturamentoFacade()
				.obterConvenioSaudePlano(vo.getCodigoConvenioSaude(),
						vo.getCodigoConvenioSaudePlano());
			}
		}

		return convenioSaudeplano;// (convenioSaudeplano != null) ? convenioSaudeplano.getConvenioSaude() : null;
	}

	/**
	 * Busca um FatConvenioSaudePlano de uma Solicitacao de exames.<br>
	 * ORADB function AELC_VER_GRUPO_CNV_G.<br>
	 * Pode retornar nulo.<br>
	 * 
	 * @param solicitacaoExameVO
	 * @return um FatConvenioSaudePlano.
	 */
	public FatConvenioSaudePlano obterFatConvenioSaudePlano(SolicitacaoExameVO solicitacaoExameVO) {
		FatConvenioSaudePlano convenioSaudeplano = null;

		if (solicitacaoExameVO.getAtendimento() != null) {
			convenioSaudeplano = this.obterFatConvenioSaudePlano(solicitacaoExameVO.getAtendimento(), null);
		} else if (solicitacaoExameVO.getAtendimentoDiverso() != null) {
			convenioSaudeplano = this.obterFatConvenioSaudePlano(null, solicitacaoExameVO.getAtendimentoDiverso());
		}

		return convenioSaudeplano;
	}

	/**
	 * ORADB PACKAGE AELK_ISE_RN.RN_ISEP_ATU_CONTCELL
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected void atribuirCargaContador(AelItemSolicitacaoExames itemSolicitacaoExame) {
		// Recupera o ExameMaterialAnalise
		AelExamesMaterialAnalise exameMaterialAnalise = getAelExamesMaterialAnaliseDAO()
		.buscarAelExamesMaterialAnalisePorId(
				itemSolicitacaoExame.getExame().getSigla(),
				itemSolicitacaoExame.getMaterialAnalise().getSeq());

		if (exameMaterialAnalise.getIndPertenceContador()) {
			itemSolicitacaoExame.setIndCargaContador(false);
		}
	}

	/**
	 * ORADB Package AELK_RN.RN_ISEP_ATU_IMP_LAUD
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected void verificarIndicativoImpressoLaudo(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		if (itemSolicitacaoExame.getSolicitacaoExame().getAtendimentoDiverso() != null) {
			itemSolicitacaoExame.setIndImpressoLaudo(DominioIndImpressoLaudo.N);
		}
	}

	/**
	 * ORADB PACKAGE AELK_ISE_RN.RN_ISEP_ATU_UNF_AVIS
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected void atribuirUnfSeqAvisa(AelItemSolicitacaoExames itemSolicitacaoExame) {
		AelPermissaoUnidSolic permissao = getAelPermissaoUnidSolicDAO()
		.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
				itemSolicitacaoExame.getExame().getSigla(),
				itemSolicitacaoExame.getMaterialAnalise().getSeq(),
				itemSolicitacaoExame.getUnidadeFuncional().getSeq(),
				itemSolicitacaoExame.getSolicitacaoExame()
				.getUnidadeFuncional().getSeq());
		if (permissao != null && permissao.getUnfSeqAvisa() != null) {
			itemSolicitacaoExame
			.setUnfSeqAvisa(permissao.getUnfSeqAvisa().getSeq());
		}
	}

	/**
	 * RN20 Nao permitir alterar a situacao do exame solicitado para A EXECUTAR,
	 * se o material de analise for coletavel.
	 * 
	 * ORADB Package aelk_ise_rn.rn_isep_VER_SIT_AEXE
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarSituacaoExameMaterialAnalise(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		if (DominioSituacaoItemSolicitacaoExame.AX.toString().equals(
				itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())
				&& itemSolicitacaoExame.getMaterialAnalise().getIndColetavel()) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_01007);
		}
	}

	/**
	 * RN21 Verifica a cota de exames no cadastro de projetos.
	 * 
	 * ORADB PACKAGE AELK_ISE_RN.RN_ISEP_VER_COTA_PJQ
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected void verificarCotaExameCadastroProjeto(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		// NAO implementar neste sprint conforme doc de pre-analise.
	}

	/**
	 * RN22 Atualiza a cota de exames no cadastro de projetos.
	 * 
	 * ORADB PACKAGE AELK_ISE_RN.RN_ISEP_ATU_COTA_PJQ
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected void atualizarCotaExameCadastroProjeto(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		// NAO implementar neste sprint conforme doc de pre-analise.
	}

	/**
	 * RN24
	 * 
	 * ORADB TRIGGER AELT_ISE_BRI trecho final.
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected void setarIndicativoUsoO2Unidade(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		itemSolicitacaoExame.setIndUsoO2Un(itemSolicitacaoExame.getIndUsoO2());
	}

	/**
	 * RN25
	 * 
	 * ORADB TRIGGER AELT_ISE_BRI
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected void atribuirTipoTransporte(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		if (itemSolicitacaoExame.getTipoTransporte() != null) {
			itemSolicitacaoExame
			.setTipoTransporteUn(DominioTipoTransporteUnidade
					.valueOf(itemSolicitacaoExame.getTipoTransporte()
							.toString()));
		}
	}

	/**
	 * Atualizar objeto AelItemSolicitacaoExames
	 * com flush.
	 * 
	 * @param {AelItemSolicitacaoExames} item
	 * @return {AelItemSolicitacaoExames}
	 * @throws BaseException
	 */
	public AelItemSolicitacaoExames atualizar(AelItemSolicitacaoExames item, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador, final Date dataFimVinculoServidor, RapServidores servidorLogado) throws BaseException {
		return this.atualizar(item, itemSolicitacaoExameOriginal, false, true, nomeMicrocomputador,dataFimVinculoServidor, servidorLogado);
	}
	/**
	 * Atualizar objeto AelItemSolicitacaoExames
	 * com flush.
	 * 
	 * @param {AelItemSolicitacaoExames} item
	 * @return {AelItemSolicitacaoExames}
	 * @throws BaseException
	 */
	public AelItemSolicitacaoExames atualizar(AelItemSolicitacaoExames item, String nomeMicrocomputador, final Date dataFimVinculoServidor, RapServidores servidorLogado) throws BaseException {
		return this.atualizar(item, null, false, true, nomeMicrocomputador,dataFimVinculoServidor, servidorLogado);
	}
	
	/**atualizar
	 * Atualizar objeto AelItemSolicitacaoExames
	 * com flush.
	 * 
	 * @param {AelItemSolicitacaoExames} item
	 * @return {AelItemSolicitacaoExames}
	 * @throws BaseException
	 */
	public AelItemSolicitacaoExames atualizar(AelItemSolicitacaoExames item, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, boolean atualizarItemAmostra, String nomeMicrocomputador, final Date dataFimVinculoServidor, RapServidores servidorLogado) throws BaseException {
		return this.atualizar(item, itemSolicitacaoExameOriginal, atualizarItemAmostra, true, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
	}
	
	/**
	 * Atualizar objeto AelItemSolicitacaoExames
	 * com flush.
	 * 
	 * @param {AelItemSolicitacaoExames} item
	 * @return {AelItemSolicitacaoExames}
	 * @throws BaseException
	 */
	public AelItemSolicitacaoExames atualizar(AelItemSolicitacaoExames item, boolean atualizarItemAmostra, String nomeMicrocomputador, final Date dataFimVinculoServidor, RapServidores servidorLogado) throws BaseException {
		return this.atualizar(item, null, atualizarItemAmostra, true, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
	}

//	/**
//	 * Atualizar objeto AelItemSolicitacaoExames.<br>
//	 * 
//	 * @param {AelItemSolicitacaoExames} item
//	 * @param {boolean} flush
//	 * 
//	 * @return {AelItemSolicitacaoExames}
//	 * @throws BaseException
//	 */
//	private AelItemSolicitacaoExames atualizar(AelItemSolicitacaoExames item,  Boolean atualizarItemAmostra, boolean flush, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
//		return atualizar(item, null, atualizarItemAmostra, flush, nomeMicrocomputador, dataFimVinculoServidor);
//	}
	
	private AelItemSolicitacaoExames atualizar(AelItemSolicitacaoExames item, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, Boolean atualizarItemAmostra, boolean flush, String nomeMicrocomputador, final Date dataFimVinculoServidor,RapServidores servidorLogado) throws BaseException {
		if (itemSolicitacaoExameOriginal == null) {
			itemSolicitacaoExameOriginal = this.getAelItemSolicitacaoExameDAO().obterOriginal(item.getId());
		}
		
		this.preAtualizar(itemSolicitacaoExameOriginal, item);

		item = this.getAelItemSolicitacaoExameDAO().merge(item);
		if (flush){
			this.getAelItemSolicitacaoExameDAO().flush();
		}

		getItemSolicitacaoExameEnforceRN().enforceUpdate(itemSolicitacaoExameOriginal, atualizarItemAmostra, item, nomeMicrocomputador, servidorLogado,dataFimVinculoServidor, flush);

		return item;
	}

	/**
	 * Atualizar objeto AelItemSolicitacaoExames
	 * sem flush.
	 * 
	 * @param {AelItemSolicitacaoExames} item
	 * @return {AelItemSolicitacaoExames}
	 * @throws BaseException
	 */
	public AelItemSolicitacaoExames atualizarSemFlush(AelItemSolicitacaoExames item, String nomeMicrocomputador, final Date dataFimVinculoServidor, final Boolean flush, RapServidores servidorLogado) throws BaseException {
		return this.atualizar(item, false, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
	}

	/**
	 * ORADB AELT_ISE_BRU.<br>
	 * 
	 * @param itemSolicitacaoExameOriginal
	 * @param itemSolicitacaoExame
	 * @throws BaseException
	 */
	private void preAtualizar(AelItemSolicitacaoExames itemSolicitacaoExameOriginal, AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {
		this.verificarFormaRespiracaoNaAlteracao(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
		this.verificarAlteracaoExames(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
		this.verificarCamposAlteradosValidarAtualizacaoUsuario(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
		this.verificarPossibilidadeAlteracao(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
		this.verificarSituacaoOrigem(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
		this.atualizarInfomacoesSolicitacaoUnidadeExecutora(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
		this.verificarPermissoesQuandoGrupoConvenioSUSAlteracao(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
		this.verificarExamesMaterialAnalise(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
		this.verificarAlteracaoSituacaoItemSolicitacao(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
	}

	/**
	 * RN26
	 * 
	 * ORADB TRIGGER AELT_ISE_BRU trecho inicial.
	 * 
	 * @param itemOriginal
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarFormaRespiracaoNaAlteracao(AelItemSolicitacaoExames itemOriginal,
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		boolean foiModificado = CoreUtil.modificados(
				itemSolicitacaoExame.getFormaRespiracao(),
				itemOriginal.getFormaRespiracao());

		if (foiModificado) {
			this.verificarFormaRespiracao(itemSolicitacaoExame);
		}
	}

	/**
	 * RN27
	 * 
	 * ORADB TRIGGER AELT_ISE_BRU trecho inicial.
	 * 
	 * @param itemSolicitacaoExameBD
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarAlteracaoExames(AelItemSolicitacaoExames itemSolicitacaoExameBD, AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {

		if ((itemSolicitacaoExame.getExame()!=null && CoreUtil.modificados(itemSolicitacaoExame.getExame().getSigla(), itemSolicitacaoExameBD.getExame().getSigla()))
				|| (itemSolicitacaoExame.getMaterialAnalise()!=null && CoreUtil.modificados(itemSolicitacaoExame.getMaterialAnalise().getSeq(),itemSolicitacaoExameBD.getMaterialAnalise().getSeq()))
				|| (itemSolicitacaoExame.getUnidadeFuncional()!=null && CoreUtil.modificados(itemSolicitacaoExame.getUnidadeFuncional().getSeq(),itemSolicitacaoExameBD.getUnidadeFuncional().getSeq()))){
			throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.AEL_00488);
		}
	}

	/**
	 * RN28<br>
	 * 
	 * Verificacao pra chamada de: aelk_ise_rn.rn_isep_VER_UPDATE; Se os campos
	 * tipo_coleta, ind_uso_o2, ind_gerado_automatico , iço_seq, ran_seq,
	 * desc_regiao_anatomica, desc_material_analise , nro_amostras,
	 * intervalo_dias, intervalo_horas , dthr_programada ou prioridade_execucao
	 * forem alterados então verificarAtualizacaoUsuario.<br>
	 * 
	 * @param itemOriginal
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarCamposAlteradosValidarAtualizacaoUsuario(AelItemSolicitacaoExames itemOriginal,
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {

		if (CoreUtil.modificados(itemSolicitacaoExame.getTipoColeta(),
				itemOriginal.getTipoColeta())
				|| CoreUtil.modificados(itemSolicitacaoExame.getIndUsoO2(),
						itemOriginal.getIndUsoO2())
						|| CoreUtil.modificados(
								itemSolicitacaoExame.getIndGeradoAutomatico(),
								itemOriginal.getIndGeradoAutomatico())
								|| CoreUtil.modificados(
										itemSolicitacaoExame.getIntervaloColeta(),
										itemOriginal.getIntervaloColeta())
										|| CoreUtil.modificados(
												itemSolicitacaoExame.getRegiaoAnatomica(),
												itemOriginal.getRegiaoAnatomica())
												|| CoreUtil.modificados(
														itemSolicitacaoExame.getDescRegiaoAnatomica(),
														itemOriginal.getDescRegiaoAnatomica())
														|| CoreUtil.modificados(
																itemSolicitacaoExame.getDescMaterialAnalise(),
																itemOriginal.getDescMaterialAnalise())
																|| CoreUtil.modificados(itemSolicitacaoExame.getNroAmostras(),
																		itemOriginal.getNroAmostras())
																		|| CoreUtil.modificados(
																				itemSolicitacaoExame.getIntervaloDias(),
																				itemOriginal.getIntervaloDias())
																				|| CoreUtil.modificados(
																						itemSolicitacaoExame.getIntervaloHoras(),
																						itemOriginal.getIntervaloHoras())
																						|| CoreUtil.modificados(
																								itemSolicitacaoExame.getDthrProgramada(),
																								itemOriginal.getDthrProgramada())
																								|| CoreUtil.modificados(
																										itemSolicitacaoExame.getPrioridadeExecucao(),
																										itemOriginal.getPrioridadeExecucao())) {
			this.verificarAtualizacaoUsuario(itemSolicitacaoExame);
		}
	}

	/**
	 * RN29
	 * 
	 * ORADB Package aelk_ise_rn.rn_isep_VER_UPDATE
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarAtualizacaoUsuario(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (!servidorLogado.equals(itemSolicitacaoExame.getSolicitacaoExame()
				.getServidorResponsabilidade())
				&& !servidorLogado.equals(itemSolicitacaoExame
						.getSolicitacaoExame().getServidor())) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoExameRNExceptionCode.AEL_00581);
		}
	}

	/**
	 * RN30
	 * 
	 * ORADB Package aelk_ise_rn.rn_isep_VER_UPDATE
	 * 
	 * @param itemOriginal
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarPossibilidadeAlteracao(AelItemSolicitacaoExames itemOriginal,
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {

		if (CoreUtil.modificados(itemSolicitacaoExame.getTipoColeta(),
				itemOriginal.getTipoColeta())
				|| CoreUtil.modificados(itemSolicitacaoExame.getIndUsoO2(),
						itemOriginal.getIndUsoO2())
						|| CoreUtil.modificados(
								itemSolicitacaoExame.getIndGeradoAutomatico(),
								itemOriginal.getIndGeradoAutomatico())
								|| CoreUtil.modificados(
										itemSolicitacaoExame.getIntervaloColeta(),
										itemOriginal.getIntervaloColeta())
										|| CoreUtil.modificados(
												itemSolicitacaoExame.getRegiaoAnatomica(),
												itemOriginal.getRegiaoAnatomica())
												|| CoreUtil.modificados(itemSolicitacaoExame.getNroAmostras(),
														itemOriginal.getNroAmostras())
														|| CoreUtil.modificados(
																itemSolicitacaoExame.getIntervaloDias(),
																itemOriginal.getIntervaloDias())
																|| CoreUtil.modificados(
																		itemSolicitacaoExame.getIntervaloHoras(),
																		itemOriginal.getIntervaloHoras())
																		|| CoreUtil.modificados(
																				itemSolicitacaoExame.getDthrProgramada(),
																				itemOriginal.getDthrProgramada())
																				|| CoreUtil.modificados(
																						itemSolicitacaoExame.getPrioridadeExecucao(),
																						itemOriginal.getPrioridadeExecucao())) {
			List<String> listaSituacaoItem = Arrays.asList(
					DominioSituacaoItemSolicitacaoExame.AC.toString(), // A
					// COLETAR
					DominioSituacaoItemSolicitacaoExame.AX.toString(), // A
					// EXECUTAR
					DominioSituacaoItemSolicitacaoExame.AG.toString(), // AGENDADO
					DominioSituacaoItemSolicitacaoExame.CS.toString() // COLETADO
					// PELO
					// SOLICITANTE
			);

			if (!listaSituacaoItem.contains(itemSolicitacaoExame
					.getSituacaoItemSolicitacao().getCodigo())) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_00542);
			}
		}
	}

	/**
	 * RN31
	 * 
	 * @param itemSolicitacaoExame
	 * @throws BaseException 
	 */
	protected void verificarSituacaoOrigem(AelItemSolicitacaoExames itemSolicitacaoExameOriginal,
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws BaseException {
		//.obterOriginal
		AelItemSolicitacaoExames itemSolicitacaoExameBD = getAelItemSolicitacaoExameDAO().obterOriginal(itemSolicitacaoExame.getId());
		if (CoreUtil.modificados(
				itemSolicitacaoExame.getSituacaoItemSolicitacao(),
				itemSolicitacaoExameBD.getSituacaoItemSolicitacao())) {
			verificarTransicaoSituacao(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
		}
	}

	/**
	 * Regras: RN32, RN33 e RN46
	 * 
	 * ORADB Procedure RN_ISEP_ATU_INF_UE
	 * 
	 * @param itemSolicitacaoExameBD
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException  
	 */
	protected void atualizarInfomacoesSolicitacaoUnidadeExecutora(AelItemSolicitacaoExames itemSolicitacaoExameBD,
			AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {

		if (CoreUtil.modificados(itemSolicitacaoExame.getIndUsoO2Un(),
				itemSolicitacaoExameBD.getIndUsoO2Un())
				|| CoreUtil.modificados(
						itemSolicitacaoExame.getTipoTransporteUn(),
						itemSolicitacaoExameBD.getTipoTransporteUn())) {
			AelInformacaoSolicitacaoUnidadeExecutora aelInfSolicUnExecs = new AelInformacaoSolicitacaoUnidadeExecutora();

			aelInfSolicUnExecs.setIndUsoO2(itemSolicitacaoExame.getIndUsoO2Un());

			if (itemSolicitacaoExame.getTipoTransporteUn() != null) {

				aelInfSolicUnExecs.setTipoTransporte(DominioTipoTransporte.valueOf(itemSolicitacaoExame.getTipoTransporteUn().toString()));

			}
			
			itemSolicitacaoExame.addInformacaoSolicitacaoUnidadeExecutora(aelInfSolicUnExecs);

			// RN46.
			this.getExamesFacade().inserirInformacaoSolicitacaoUnidadeExecutoraRN(
					aelInfSolicUnExecs);
		}
	}

	/**
	 * RN34
	 * 
	 * ORADB PACKAGE PROCEDURE aelk_ise_rn.rn_isep_ver_permissa
	 * 
	 * @param itemOriginal
	 * @param itemSolicitacaoExame
	 * @throws BaseException
	 */
	protected void verificarPermissoesQuandoGrupoConvenioSUSAlteracao(AelItemSolicitacaoExames itemOriginal,
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws BaseException {

		if (CoreUtil.modificados(itemSolicitacaoExame.getTipoColeta(),
				itemOriginal.getTipoColeta())
				|| CoreUtil.modificados(itemSolicitacaoExame.getIndUsoO2(),
						itemOriginal.getIndUsoO2())
						|| CoreUtil.modificados(
								itemSolicitacaoExame.getDthrProgramada(),
								itemOriginal.getDthrProgramada())
								|| CoreUtil.modificados(
										itemSolicitacaoExame.getTipoTransporte(),
										itemOriginal.getTipoTransporte())) {
			this.verificarPermissoesQuandoGrupoConvenioSUS(itemSolicitacaoExame);
		}
	}

	/**
	 * RN35
	 * 
	 * ORADB PACKAGE PROCEDURE aelk_ise_rn.rn_isep_ver_mat_anls
	 * 
	 * @param itemSolicitacaoExameBD
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarExamesMaterialAnalise(AelItemSolicitacaoExames itemSolicitacaoExameBD,
			AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {

		if (CoreUtil.modificados(itemSolicitacaoExame.getDescRegiaoAnatomica(),
				itemSolicitacaoExameBD.getDescRegiaoAnatomica())
				|| CoreUtil.modificados(
						itemSolicitacaoExame.getRegiaoAnatomica(),
						itemSolicitacaoExameBD.getRegiaoAnatomica())
						|| CoreUtil.modificados(itemSolicitacaoExame.getNroAmostras(),
								itemSolicitacaoExameBD.getNroAmostras())
								|| CoreUtil.modificados(
										itemSolicitacaoExame.getIndGeradoAutomatico(),
										itemSolicitacaoExameBD.getIndGeradoAutomatico())
										|| CoreUtil.modificados(
												itemSolicitacaoExame.getIntervaloDias(),
												itemSolicitacaoExameBD.getIntervaloDias())
												|| CoreUtil.modificados(
														itemSolicitacaoExame.getIntervaloHoras(),
														itemSolicitacaoExameBD.getIntervaloHoras())
														|| CoreUtil.modificados(
																itemSolicitacaoExame.getIntervaloColeta(),
																itemSolicitacaoExameBD.getIntervaloColeta())) {
			this.verificarExameMaterialAnalise(itemSolicitacaoExame);
		}
	}

	/**
	 * RN36
	 * 
	 * @param itemSolicitacaoExameOriginal
	 * @param itemSolicitacaoExame
	 * @throws BaseException
	 */
	public void verificarAlteracaoSituacaoItemSolicitacao(AelItemSolicitacaoExames itemSolicitacaoExameOriginal,
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws BaseException {

		// Se situacao foi modificada.
		if (this.getItemSolicitacaoExameEnforceRN()
				.verificarSituacaoItemSolicitacaoModificada(
						itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {

			this.verificarSituacaoCancelado(itemSolicitacaoExame, itemSolicitacaoExameOriginal);
			this.verificarSituacaoExameMaterialAnaliseNaAlteracao(itemSolicitacaoExame);
			this.atualizarHorarioColetadoParaAgendado(itemSolicitacaoExame);
			this.alterarSituacaoHorarioDeAreaExecuturaParaAgendado(
					itemSolicitacaoExame, itemSolicitacaoExameOriginal);
			this.verificarSolicitacaoGerada(itemSolicitacaoExame);
			this.verificarTempoAposLiberacao(itemSolicitacaoExame,
					itemSolicitacaoExameOriginal);
			this.atualizarHoraLiberacao(itemSolicitacaoExame);
			this.atualizarCotaExameCadastroProjeto(itemSolicitacaoExame);
			/*
			 * Regra removida da versão 1 de Exames
			 * this.verificarAPAnterior(itemSolicitacaoExame);
			 */

		}

	}

	/**
	 * RN37
	 * 
	 * PACKAGE PROCEDURE AELK_ISE_RN.RN_ISEP_VER_SIT_CANC
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected void verificarSituacaoCancelado(
			AelItemSolicitacaoExames itemSolicitacaoExame,
			AelItemSolicitacaoExames original) {
		original = getAelItemSolicitacaoExameDAO()
				.obterOriginal(itemSolicitacaoExame.getId());
		// Caso a situação fosse cancelado
		if (DominioSituacaoItemSolicitacaoExame.CA.equals(original.getSituacaoItemSolicitacao())) {
			// Caso a nova situação seja diferente
			if (!DominioSituacaoItemSolicitacaoExame.CA.equals(itemSolicitacaoExame.getSituacaoItemSolicitacao())) {
				// Limpa motivo do cancelamento
				itemSolicitacaoExame.setComplementoMotCanc(null);
				itemSolicitacaoExame.setAelMotivoCancelaExames(null);
			}
		}
	}

	/**
	 * RN38
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarSituacaoExameMaterialAnaliseNaAlteracao(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		this.verificarSituacaoExameMaterialAnalise(itemSolicitacaoExame);
	}

	/**
	 * RN39
	 * 
	 * ORADB PACKAGE PROCEDURE AELK_ISE_RN.RN_ISEP_AT_HOR_CO_AG
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected void atualizarHorarioColetadoParaAgendado(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		AelItemSolicitacaoExames itemSolicitacaoExameBD = getAelItemSolicitacaoExameDAO()
		.obterOriginal(itemSolicitacaoExame.getId());
		//AelItemSolicitacaoExames itemSolicitacaoExameBD = 	getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());
		// Se a antiga situação era 'Coletado'
		if (itemSolicitacaoExameBD.getSituacaoItemSolicitacao().getCodigo()
				.equals(DominioSituacaoItemSolicitacaoExame.CO.toString())) {
			// Se a nova situação é 'Agendado'
			if (itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()
					.equals(DominioSituacaoItemSolicitacaoExame.AG.toString())) {
				// Recupera o ItemHorarioAgendado
				List<AelItemHorarioAgendado> listItemHorarioAgendado = getAelItemHorarioAgendadoDAO()
					.obterPorItemSolicitacaoExame(itemSolicitacaoExame);
				
				for(AelItemHorarioAgendado itemHorarioAgendado : listItemHorarioAgendado) {
					// Recupera o HorarioExameDisp
					AelHorarioExameDisp horarioExameDisp = itemHorarioAgendado.getHorarioExameDisp();
					
					// Atualiza a situação do horário do exame para marcado se o
					// mesmo estava executado
					if (horarioExameDisp != null && horarioExameDisp.getSituacaoHorario() == DominioSituacaoHorario.E) {
						horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.M);
					}
				}

			}
		}
	}

	/**
	 * RN40<br>
	 * 
	 * ORADB Package aelk_ise_rn.rn_isep_at_hor_ae_ag<br>
	 * 
	 * Atualizar o hor¿rio do exame de EXECUTADO para MARCADO, quando este passa
	 * de AREA EXECUTORA para AGENDADO.<br>
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected void alterarSituacaoHorarioDeAreaExecuturaParaAgendado(
			AelItemSolicitacaoExames itemSolicitacaoExame,
			AelItemSolicitacaoExames itemSolicitacaoExameOriginal) {

		if (this.getItemSolicitacaoExameEnforceRN()
				.verificarAlteracaoSituacaoExameAreaExecutoraParaAgendado(
						itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {
			List<AelItemHorarioAgendado> listItemHorarioAgendado = getAelItemHorarioAgendadoDAO().obterPorItemSolicitacaoExame(itemSolicitacaoExame);

			for(AelItemHorarioAgendado itemHorarioAgendado : listItemHorarioAgendado) {
				AelHorarioExameDisp horarioExameDisp = itemHorarioAgendado.getHorarioExameDisp();
				if (horarioExameDisp != null && horarioExameDisp.getSituacaoHorario() == DominioSituacaoHorario.E) {
					horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.M);
				}
			}
		}

	}

	/**
	 * RN41 Se a solicitacao foi gerado pelo sistema nao testa solicitante, <BR>
	 * ou seja, somente testa se o campo gerado_automatico for diferente de 'S'.
	 * 
	 * 
	 * ORADB PACKAGE AELK_ISE_RN.RN_ISEP_VER_CANC_SOL
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarSolicitacaoGerada(
			AelItemSolicitacaoExames itemSolicitacaoExame)
	throws ApplicationBusinessException {
		// verificarSolicitacaoCancelamento
		// TODO Deverá ser implementado quando desenvolvido atendimentos
		// diversos
	}

	/**
	 * RN42: Verifica e possivel alterar a situacao do exame apos a liberacao,
	 * para isso verifica e a data atual eh menor que a
	 * dthr_liberada+tempo_apos_liberacao.<br>
	 * 
	 * ORADB Package aelk_ise_rn.rn_isep_ver_temp_lib<br>
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @throws BaseException
	 */
	public void verificarTempoAposLiberacao(
			AelItemSolicitacaoExames itemSolicitacaoExame,
			AelItemSolicitacaoExames itemSolicitacaoExameOriginal)
	throws BaseException {

		// situacao_codigo_original = LI AND situacao_codigo_original <>
		// situacao_codigo_nova
		if (itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao() != null
			&& DominioSituacaoItemSolicitacaoExame.LI.toString().equals(itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo())
			&& itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo() != null
			&& itemSolicitacaoExame.getSituacaoItemSolicitacao() != null
			&& !itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {
			
			AelUnfExecutaExames aelUnfExecutaExames = this.getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(
																							itemSolicitacaoExame.getExame(),
																							itemSolicitacaoExame.getMaterialAnalise(),
																							itemSolicitacaoExame.getUnidadeFuncional());

			Short tempoAposLiberado;
			DominioUnidTempo unidadeTempoAposLiberado;
			if (aelUnfExecutaExames == null || aelUnfExecutaExames.getTempoAposLiberacao() == null) {
				// P_TEMPO_APOS_LIBERACAO - v_vlr_numerico;
				AghParametros paramSysTempoAposLiberacao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TEMPO_APOS_LIBERACAO);
				// P_UNID_TEMPO_APOS_LIB - v_vlr_texto;
				AghParametros paramSysUnidadeTempoAposLiberacao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNID_TEMPO_APOS_LIB);
				unidadeTempoAposLiberado = DominioUnidTempo.getInstance(paramSysUnidadeTempoAposLiberacao.getVlrTexto());
				tempoAposLiberado = paramSysTempoAposLiberacao.getVlrNumerico().shortValue();

			} else {
				unidadeTempoAposLiberado = aelUnfExecutaExames.getUnidTempoAposLib();
				tempoAposLiberado = aelUnfExecutaExames.getTempoAposLiberacao();
			}

			// hora - DominioUnidTempo.H
			if (DominioUnidTempo.H == unidadeTempoAposLiberado) {
				Integer horas = DateUtil.obterQtdHorasEntreDuasDatas(itemSolicitacaoExameOriginal.getDthrLiberada(), new Date());
				if (horas > tempoAposLiberado) {
					throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.AEL_01987);
				}

			} else { // dia - DominioUnidTempo.D
				if(itemSolicitacaoExameOriginal.getDthrLiberada()!=null){
					Integer dias = this.calcularDiasUteisEntreDatas(itemSolicitacaoExameOriginal.getDthrLiberada(), new Date());
					if (dias > tempoAposLiberado) {
						throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.AEL_01987);
					}
				}
			}// IF dominio unidade tempo
		}// IF SituacaoCodigo
	}

	public AelUnfExecutaExames buscaAelUnfExecutaExames(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		AelUnfExecutaExamesDAO dao = getAelUnfExecutaExamesDAO();

		return dao.buscaAelUnfExecutaExames(itemSolicitacaoExame.getExame(),
				itemSolicitacaoExame.getMaterialAnalise(),
				itemSolicitacaoExame.getUnidadeFuncional());
	}

	/**
	 * ORADB FUNCTION AELC_NDIAS_UTEIS
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public Integer calcularDiasUteisEntreDatas(Date d1, Date d2) {
		if (d1 == null || d2 == null) {
			throw new IllegalArgumentException(
			"Nao eh possivel efetuar o calculo. Algum parametro nao foi informado.");
		}
		if (DateUtil.validaDataMaior(d1, d2)) {
			throw new IllegalArgumentException(
			"Nao eh possivel efetuar o calculo. data1 > data2.");
		}

		Integer diferenca = DateUtil.obterQtdDiasEntreDuasDatas(d1, d2);

		if (diferenca > 0) {
			// Cria uma lista de datas com as datas que sao feriados.
			List<AghFeriados> feriados = this.getAghuFacade().obterListaFeriadosEntreDatas(d1, d2);
			Set<Date> datasFeriados = new TreeSet<Date>();
			for (AghFeriados aghFeriados : feriados) {
				datasFeriados.add(aghFeriados.getData());
			}

			Date dtTemp = d1;
			while (DateValidator.validaDataMenorIgual(dtTemp, d2)) {
				if (datasFeriados.contains(dtTemp)) {
					diferenca = diferenca - 1;
				} else if (DateUtil.isFinalSemana(dtTemp)) {
					diferenca = diferenca - 1;
				}
				dtTemp = DateUtil.adicionaDias(dtTemp, 1);
			}
		}

		if (diferenca < 0) {
			throw new IndexOutOfBoundsException(
			"Diferenca nao poderia ser negativa!!!");
		}

		return diferenca;
	}

	/**
	 * ORADB FUNCTION AELC_NHORAS_UTEIS
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public Integer calcularHorasUteisEntreDatas(Date d1, Date d2) {
		Integer dias = calcularDiasUteisEntreDatas(d1, d2);

		Integer horas = null;
		if (dias != 0) {
			horas = dias * 24;
		}
		horas = (horas == null) ? 0 : horas;

		// #43489 - Cálculo das horas quando é no mesmo dia não pode considerar a hora inicial, mas sim a hora exata da solicitação
		Integer horasDoUltimoDia;

		if(dias==0) {
			horasDoUltimoDia = DateUtil.obterQtdHorasEntreDuasDatas(d1, d2);
		}
		else {
			Date d2Zerada = DateUtil.obterDataComHoraInical(d2);
			horasDoUltimoDia = DateUtil.obterQtdHorasEntreDuasDatas(d2Zerada, d2);
		}

		horas = horas + horasDoUltimoDia;

		return horas;
	}

	/**
	 * RN43
	 * 
	 * ORADB PACKAGE PROCEDURE AELK_ISE_RN.RN_ISEP_ATU_DTHR_LIB
	 * 
	 * @param itemSolicitacaoExame
	 * @throws ApplicationBusinessException  
	 */
	public void atualizarHoraLiberacao(
			AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()
				.equals(DominioSituacaoItemSolicitacaoExame.LI.toString())) {
			itemSolicitacaoExame.setDthrLiberada(new Date());

			if(itemSolicitacaoExame.getServidorResponsabilidade() == null){
				itemSolicitacaoExame.setServidorResponsabilidade(servidorLogado);	
			}

		} else {
			itemSolicitacaoExame.setDthrLiberada(null);
		}
	}

	public void receberItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor, RapServidores servidorLogado) throws BaseException {

		AelSitItemSolicitacoes antigo = aelItemSolicitacaoExames.getSituacaoItemSolicitacao();
		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AelSitItemSolicitacoes vIseSituacao = getAelSitItemSolicitacoesDAO().obterPeloId(parametro.getVlrTexto());
		aelItemSolicitacaoExames.setSituacaoItemSolicitacao(vIseSituacao);

		try {
			atualizar(aelItemSolicitacaoExames, null, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);

		} catch (BaseException e) {

			aelItemSolicitacaoExames.setSituacaoItemSolicitacao(antigo);
			throw e;

		}

	}

	public void voltarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor, RapServidores servidorLogado) throws BaseException {

		AelSitItemSolicitacoes antigo = aelItemSolicitacaoExames.getSituacaoItemSolicitacao();

		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);
		AelSitItemSolicitacoes vIseSituacao = getAelSitItemSolicitacoesDAO().obterPeloId(parametro.getVlrTexto());
		aelItemSolicitacaoExames.setSituacaoItemSolicitacao(vIseSituacao);

		try {

			atualizar(aelItemSolicitacaoExames, null, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);

		} catch (BaseException e) {

			aelItemSolicitacaoExames.setSituacaoItemSolicitacao(antigo);
			throw e;

		}
	}

	/**
	 * ORADB PROCEDURE WHEN_BUTTON_PRESSED
	 * 
	 * @param item
	 * @throws BaseException
	 */
	public void estornarItemSolicitacaoExame(AelItemSolicitacaoExames itemEstorno, String nomeMicrocomputador, final Date dataFimVinculoServidor
			,RapServidores servidorLogado) throws BaseException {


		verificaTempoEstorno(itemEstorno.getId().getSoeSeq(), itemEstorno.getId().getSeqp());
		atualizaItemSolicitacaoExame(itemEstorno);
		atualizar(itemEstorno, null, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
		/*
		 * Busca os itens dependentes para o estorno. Essa regra tem no forms.
		 */
		List<AelItemSolicitacaoExames> itens = this.getAelItemSolicitacaoExameDAO().pesquisarItensSolicitacaoExameDependentes(itemEstorno.getId().getSoeSeq(), itemEstorno.getId().getSeqp());
		if(itens!=null && !itens.isEmpty()){
			for(AelItemSolicitacaoExames item: itens){
				verificaTempoEstorno(item.getId().getSoeSeq(), item.getId().getSeqp());
				atualizaItemSolicitacaoExame(item);
				atualizar(item, null, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
			}
		}

	}

	private void verificaTempoEstorno(Integer iseSoeSeq, Short iseSeqp)
	throws ApplicationBusinessException {

		Short maiorSeqp = getAelExtratoItemSolicitacaoDAO().buscarMaiorSeqp(
				iseSoeSeq, iseSeqp, true);

		if (maiorSeqp != null && maiorSeqp > 0) {
			AelExtratoItemSolicitacao extrato = getAelExtratoItemSolicitacaoDAO()
			.obterPorId(iseSoeSeq, iseSeqp, maiorSeqp);
			AghParametros parametro = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_TEMPO_ESTORNO);

			Long tempoEstorno = parametro.getVlrNumerico().longValue();

			BigDecimal dtHraEvento = new BigDecimal(extrato.getDataHoraEvento()
					.getTime());
			BigDecimal dtHoje = new BigDecimal(new Date().getTime());

			Double resultado = (dtHoje.subtract(dtHraEvento).doubleValue() / 1000 / 60 / 60 );
			//Long resultado = ((dtHoje.subtract(dtHraEvento).divide(new BigDecimal(1000)).divide(new BigDecimal(1000)).divide(new BigDecimal(60)).divide(new BigDecimal(60)).divide(new BigDecimal(24))).multiply(new BigDecimal(24))).longValue();

			if (resultado > tempoEstorno) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameRNExceptionCode.AEL_03404,
						tempoEstorno);
			}
		}
	}

	private void atualizaItemSolicitacaoExame(AelItemSolicitacaoExames item)
	throws ApplicationBusinessException {
		Short maiorSeqp = getAelExtratoItemSolicitacaoDAO()
		.buscarMaiorSeqpCancelado(item.getId().getSoeSeq(),
				item.getId().getSeqp());
		AelExtratoItemSolicitacao extrato = getAelExtratoItemSolicitacaoDAO()
		.obterPorId(item.getId().getSoeSeq(), item.getId().getSeqp(),
				maiorSeqp);

		item.setSituacaoItemSolicitacao(extrato.getAelSitItemSolicitacoes());
		item.setAelMotivoCancelaExames(null);
	}

	// FIM DA TRIGGER AELT_ISE_BRU


	/**
	 * Remove item solicitacao exame.<br>
	 * 
	 * @param item
	 */
	public void remover(AelItemSolicitacaoExames item) throws BaseException {
		this.preRemover(item);

		this.getAelItemSolicitacaoExameDAO().remover(item);
		this.getAelItemSolicitacaoExameDAO().flush();
	}

	/**
	 * BEFORE DELETE ON AEL_ITEM_SOLICITACAO_EXAMES FOR EACH ROW.<br>
	 * 
	 * ORADB TRIGGER AELT_ISE_BRD.<br>
	 *  
	 * @param item
	 */
	private void preRemover(AelItemSolicitacaoExames item) {
		/*
		  CREATE OR REPLACE TRIGGER "AGH"."AELT_ISE_BRD" 
		  BEFORE DELETE
		  ON AEL_ITEM_SOLICITACAO_EXAMES
		  FOR EACH ROW
		 DECLARE
		 / * RN_ISE002 * /
		 / * Só permite DELETE  se usuário for o  BACKUP,  que faz a limpeza do AEL* /
		 BEGIN
		    IF aghc_get_user_banco = 'BACKUP' THEN
		        RETURN;
		    END IF;
		    aelk_ise_rn.rn_isep_ver_delecao;
		 END;
		 */
		throw new UnsupportedOperationException("TRIGGER AELT_ISE_BRD - ainda nao implementada.");
	}

	/**
	 * Chamada para Web Service #38474
	 * Utilizado nas estórias #864 e #27542 
	 * @param atdSeq
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarExameVDRLnaoSolicitado(Integer atdSeq) throws ApplicationBusinessException{
		AghParametros aghParametros = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EXAME_VDRL);
		return this.getAelItemSolicitacaoExameDAO().verificarExameVDRLnaoSolicitado(atdSeq, aghParametros.getVlrTexto());
	}
	
	public Boolean verificarSeExameSendoSolicitadoRedome(final AelItemSolicitacaoExames itemSolicitacaoExame, AghUnidadesFuncionais unidadeExecutora) throws BaseException {
		
		   Boolean isRedome = false;
		  
       if (itemSolicitacaoExame != null && unidadeExecutora != null) {
		       
     	   final BigDecimal paramUnidadeImuno = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNID_IMUNO).getVlrNumerico();
     	   Short unidFuncImuno = null; 
     	  
     	   if (paramUnidadeImuno != null) {
					unidFuncImuno = paramUnidadeImuno.shortValue();
				}
					
             final Short ufeUnidFunc = unidadeExecutora.getSeq();
				
             if (ufeUnidFunc != null && ufeUnidFunc.equals(unidFuncImuno)) {
					
					final AelAgrpPesquisas agrpPesquisa = getAelAgrpPesquisasDAO().obterAelAgrpPesquisasPorDescricao(AelAgrpPesquisas.REDOME);

					final List<AelAgrpPesquisaXExame> lista = getAelAgrpPesquisaXExameDAO().buscarAelAgrpPesquisaXExame(itemSolicitacaoExame.getExame(),
							itemSolicitacaoExame.getMaterialAnalise(), unidadeExecutora, agrpPesquisa, null);
					if (lista != null && !lista.isEmpty()) {
						isRedome = true;
					}
				}
			}
		return isRedome;
	}
	
	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(final AelItemSolicitacaoExames itemSolicitacaoExame)
			throws BaseException {
		return getAelAmostrasDAO().buscarAmostrasPorSolicitacaoExame(itemSolicitacaoExame.getSolicitacaoExame());	
	}
	
	// ------------------------------
	// GETTERS / SETTERS

	protected AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}

	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}

	protected AelPermissaoUnidSolicDAO getAelPermissaoUnidSolicDAO() {
		return aelPermissaoUnidSolicDAO;
	}

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}

	protected AelExamesEspecialidadeDAO getAelExamesEspecialidadeDAO() {
		return aelExamesEspecialidadeDAO;
	}

	//	protected AghProfEspecialidadesDAO getAghProfEspecialidadesDAO() {
	//		return aghProfEspecialidadesDAO;
	//	}



	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}




	protected AelExameConselhoProfsDAO getAelExameConselhoProfsDAO() {
		return aelExameConselhoProfsDAO;
	}

	protected AelServidoresExameUnidDAO getAelServidoresExameUnidDAO() {
		return aelServidoresExameUnidDAO;
	}

	protected AelMatrizSituacaoDAO getAelMatrizSituacaoDAO() {
		return aelMatrizSituacaoDAO;
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected AelMotivoCancelaExamesDAO getAelMotivoCancelaExamesDAO() {
		return aelMotivoCancelaExamesDAO;
	}

	protected ItemSolicitacaoExameEnforceRN getItemSolicitacaoExameEnforceRN() {
		return itemSolicitacaoExameEnforceRN;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IProtocoloFacade getProtocoloFacade() {
		return protocoloFacade;		
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public AelInformacaoSolicitacaoUnidadeExecutoraDAO getAelInformacaoSolicitacaoUnidadeExecutoraDAO() {
		return aelInformacaoSolicitacaoUnidadeExecutoraDAO;
	}
	
	public AelAgrpPesquisasDAO getAelAgrpPesquisasDAO() {
		return aelAgrpPesquisasDAO;
	}

	public void setAelAgrpPesquisasDAO(AelAgrpPesquisasDAO aelAgrpPesquisasDAO) {
		this.aelAgrpPesquisasDAO = aelAgrpPesquisasDAO;
	}

	public AelAgrpPesquisaXExameDAO getAelAgrpPesquisaXExameDAO() {
		return aelAgrpPesquisaXExameDAO;
	}

	public void setAelAgrpPesquisaXExameDAO(
			AelAgrpPesquisaXExameDAO aelAgrpPesquisaXExameDAO) {
		this.aelAgrpPesquisaXExameDAO = aelAgrpPesquisaXExameDAO;
	}

	public AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}

	public void setAelAmostrasDAO(AelAmostrasDAO aelAmostrasDAO) {
		this.aelAmostrasDAO = aelAmostrasDAO;
	}

	
}
