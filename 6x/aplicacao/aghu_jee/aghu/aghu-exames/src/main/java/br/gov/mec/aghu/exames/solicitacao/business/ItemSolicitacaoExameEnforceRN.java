package br.gov.mec.aghu.exames.solicitacao.business;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioProgramacaoExecExames;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaTempo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameDAO;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoDAO;
import br.gov.mec.aghu.exames.dao.AelConfigExLaudoUnicoDAO;
import br.gov.mec.aghu.exames.dao.AelControleNumeroUnicoDAO;
import br.gov.mec.aghu.exames.dao.AelDocResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.dao.AelExameApItemSolicDAO;
import br.gov.mec.aghu.exames.dao.AelExameReflexoDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDependentesDAO;
import br.gov.mec.aghu.exames.dao.AelExamesProvaDAO;
import br.gov.mec.aghu.exames.dao.AelExecExamesMatAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelItemConfigExameDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelPacAgrpPesqExameDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSecaoConfExamesDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.dao.AelTmpIntervaloColetaDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.CancelarExamesAreaExecutoraVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.RnCapcCboProcResVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelControleNumeroUnico;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelExameReflexo;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesProva;
import br.gov.mec.aghu.model.AelExecExamesMatAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelPacAgrpPesqExames;
import br.gov.mec.aghu.model.AelPacAgrpPesqExamesId;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AelSeqCodbarraRedome;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelTmpIntervaloColeta;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Responsavel pelas regras das enforces da entidade AelItemSolicitacaoExames.
 * 
 * Regras de 2 a 43 do documento de pre-analise.
 * 
 * Tabela: AEL_ITEM_SOLICITACAO_EXAMES
 * 
 * @ORADB Enforce AELP_ENFORCE_SOE_RULES.
 * 
 */
@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity",
		"PMD.ExcessiveClassLength",
		"PMD.CouplingBetweenObjects",
		"PMD.NcssTypeCount"})
@Stateless
public class ItemSolicitacaoExameEnforceRN extends BaseBusiness {

	private static final String CALL_STM = "{call ";

	@EJB
	private AelExameApItemSolicRN aelExameApItemSolicRN;
	
	@EJB
	private AelSeqCodbarraRedomeRN aelSeqCodbarraRedomeRN;
	
	@EJB
	private ItemSolicitacaoExameRN itemSolicitacaoExameRN;
	
	@EJB
	private ProcessarFilaExamesLiberados processarFilaExamesLiberados;
	
	private static final Log LOG = LogFactory.getLog(ItemSolicitacaoExameEnforceRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AelExameApItemSolicDAO aelExameApItemSolicDAO;
	
	@Inject
	private AelControleNumeroUnicoDAO aelControleNumeroUnicoDAO;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private AelExameReflexoDAO aelExameReflexoDAO;
	
	@Inject
	private AelExamesProvaDAO aelExamesProvaDAO;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@Inject
	private AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO;
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private AelMaterialAnaliseDAO aelMaterialAnaliseDAO;
	
	@Inject
	private AelExamesDAO aelExamesDAO;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelHorarioExameDispDAO aelHorarioExameDispDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelTmpIntervaloColetaDAO aelTmpIntervaloColetaDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;
		
	@Inject
	private AelPacAgrpPesqExameDAO aelPacAgrpPesqExameDAO;
	
	@Inject
	private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;
	
	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	
	@Inject
	private AelAgrpPesquisasDAO aelAgrpPesquisasDAO;
	
	@Inject
	private AelItemConfigExameDAO aelItemConfigExameDAO;
	
	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;
	
	@Inject
	private AelDocResultadoExameDAO aelDocResultadoExameDAO;
	
	@Inject
	private AelConfigExLaudoUnicoDAO aelConfigExLaudoUnicoDAO;
	
	@Inject
	private AelSecaoConfExamesDAO aelSecaoConfExamesDAO;
	
	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;
	
	@Inject
	private AelExecExamesMatAnaliseDAO aelExecExamesMatAnaliseDAO;
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	
	@Inject
	private AelExameApDAO aelExameApDAO;
	
	@Inject
	private AelExamesDependentesDAO aelExamesDependentesDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelAgrpPesquisaXExameDAO aelAgrpPesquisaXExameDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3315526817160626205L;

//	private static final String ENTITY_MANAGER = "entityManager";

	public enum ItemSolicitacaoExameEnforceRNExceptionCode implements BusinessExceptionCode {
		AEL_00420, AEL_00473, AEL_00474, AEL_01161, AEL_01162, AEL_02550, AEL_01094, AEL_00501, 
		AEL_00502, FAT_00012, ERRO_NA_INTEGRACAO_COM_FATURAMENTO, AEL_02683, AEL_02684, AEL_02685, 
		AEL_02715, AEL_02686, AEL_02687, AEL_01004, AEL_03413,MENSAGEM_ERRO_GENERICA,
		ERRO_CONFG_EXAME_PATOL_NAO_ENCONTRADO, FAT_01250, FAT_01251, FAT_01252;

		public void throwException(final Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwException(final Throwable cause, final Object... params) throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a exceção de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}
	}

	/**
	 * ORADB AELP_ENFORCE_ISE_RULES - EVENTO DE UPDATE
	 * @param servidorLogado 
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws BaseException
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void enforceUpdate(final AelItemSolicitacaoExames itemSolicitacaoExameOriginal, final Boolean atualizarItemAmostra,
			final AelItemSolicitacaoExames itemSolicitacaoExame, String nomeMicrocomputador, 
			RapServidores servidorLogado, final Date dataFimVinculoServidor, final boolean flush) throws BaseException {

		if (this.verificarSituacaoItemSolicitacaoAlterada(itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {

			this.verificarSituacaoAtiva(itemSolicitacaoExame);

			this.atualizarExtrato(itemSolicitacaoExame, flush);

			this.atualizarCancelamento(itemSolicitacaoExame, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);

			if (this.verificarAlteracaoSituacaoExameAgendadoParaColetado(itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {

				this.atualizarHorarioExameAgendadoParaColetado(itemSolicitacaoExameOriginal);

			}

			if (this.verificarAlteracaoSituacaoExameAgendadoParaAreaExecutora(itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {

				this.atualizarHorarioExameAgendadoParaAreaExecutora(itemSolicitacaoExame);

			}

			if (this.verificarAlteracaoSituacaoExameParaLiberado(itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {

				this.verificarResultado(itemSolicitacaoExame);

				// Chamado por CallableStatement quando executado no Oracle do
				// HCPA
				this.atualizarReflexo(itemSolicitacaoExameOriginal, itemSolicitacaoExame, nomeMicrocomputador,servidorLogado, dataFimVinculoServidor, flush);

				this.atualizarAgrupamento(itemSolicitacaoExame);
				
				this.enviarSolicitacaoExameFila(itemSolicitacaoExame);

			}

			if (!atualizarItemAmostra) {

				this.atualizarSituacaoAmostra(itemSolicitacaoExame, itemSolicitacaoExameOriginal, nomeMicrocomputador);

			}

			// Chamado por CallableStatement quando executado no Oracle do HCPA
			this.atualizarExameAbs(itemSolicitacaoExame, servidorLogado != null ? servidorLogado.getUsuario() : null);

			// Chamado por CallableStatement quando executado no Oracle do HCPA
			this.atualizarDoacaoAbs(itemSolicitacaoExame, servidorLogado != null ? servidorLogado.getUsuario() : null);

			try {
			//	if (!CoreUtil.isHU(HUsEnum.HCPA) || !getAghuDataBase().isOracle()) {
					this.realizarInterfaceFaturamento(itemSolicitacaoExame, itemSolicitacaoExameOriginal, nomeMicrocomputador,servidorLogado, dataFimVinculoServidor);
			//	} else {
					// Se estiver rodando no Oracle do HCPA, chama a procedure
					// por CallableStatement
			//		this.realizarInterfaceFaturamentoCallableStatement(itemSolicitacaoExame, null);
			//	}
			} catch (final InactiveModuleException e) {
				logError(e.getMessage());
			}

		}else if(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo() != null && 
				 itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.LI.toString())){
	        
			  this.atualizarExtrato(itemSolicitacaoExame, flush);
        }

		if (this.verificarResponsavelModificado(itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {

			this.atualizarExtratoServidor(itemSolicitacaoExame);

		}

		if (this.verificarDescricaoMaterialAnalise(itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {

			this.verificarProvas(itemSolicitacaoExame);

		}

		// Não permite ter repetição de prioridade para itens de uma mesma
		// solicitação de exames
		if (this.verificaAlteracaoPrioridade(itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {

			// RN24 - Não deve ser implementado.
			this.verificarPrioridade(itemSolicitacaoExame);

		}

		// RN25 - Se o número de amostras (nro_amostras) ou intervalo de dias
		// (intervalo_dias)
		// ou intervalo de horas (intervalo_horas) forem alterados então executa
		// RN26, RN27 e RN28
		if (this.verificarAlteracaoAmostrasIntervalos(itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {

			atualizarItensAmostra(itemSolicitacaoExame);

			atualizarAmostras(itemSolicitacaoExame);

			atualizarNumeroUnico(itemSolicitacaoExame);

		}

		// Se o código da situação (sit_codigo) for modificado então
		// executa RN28, RN30 e RN31
		if (this.verificarSituacaoItemSolicitacaoModificada(itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {

			atualizarNumeroUnico(itemSolicitacaoExame);

			atualizarCcelLib(itemSolicitacaoExame);

			atualizarCaixaPostal(itemSolicitacaoExame);

		}

		// TODO: A procedure abaixo não se encontra migrada em função de não ser
		// necessária
		// para a internação, alta ou contratualização (segundo o Ney e a
		// Milena). Deverá
		// ser migrada quando o Faturamento de Exames também for.
		this.ffcInterfaceAel(itemSolicitacaoExame, itemSolicitacaoExameOriginal);

		// RN33 - Gera dados nas tabelas do laudo único - Patologia.
		// Não faz mais atraves da enforce pq o numero AP não é mais gerado na itemSolicitacaoExame e sim na AelAnatomoPatologico
//		if (this.verificarCodigoSituacaoNumeroAP(itemSolicitacaoExame, itemSolicitacaoExameOriginal)) {
//
//			// RN34
//			this.atualizarLaudoUnico(itemSolicitacaoExame);
//
//		}

	}

	/**
	 * Evento de insert da tabela AelItemSolicitacaoExames.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws BaseException
	 */
	public void enforceInsert(final AelItemSolicitacaoExames itemSolicitacaoExame, String nomeMicrocomputador, RapServidores servidorLogado,
			final Date dataFimVinculoServidor, final boolean flush) throws BaseException {
		this.verificarProvas(itemSolicitacaoExame);
		this.atualizarExtrato(itemSolicitacaoExame, flush);
		this.verificarPrioridade(itemSolicitacaoExame);
		this.atualizarAmostras(itemSolicitacaoExame);
		this.atualizarNumeroUnico(itemSolicitacaoExame);

		// Gera dados nas tabelas do laudo único - Patologia
		// Não faz mais atraves da enforce pq o numero AP não é mais gerado na itemSolicitacaoExame e sim na AelAnatomoPatologico
//		if (DominioSituacaoItemSolicitacaoExame.AE.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {
//			this.atualizarLaudoUnico(itemSolicitacaoExame);
//		}

		this.atualizarAgrupamento(itemSolicitacaoExame);
		try {
			this.realizarInterfaceFaturamento(itemSolicitacaoExame, null, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		} catch (final InactiveModuleException e) {
			logError(e.getMessage());
		}

		// TODO: A procedure abaixo não se encontra migrada em função de não ser
		// necessária
		// para a internação, alta ou contratualização (segundo o Ney e a
		// Milena). Deverá
		// ser migrada quando o Faturamento de Exames também for.
		this.ffcInterfaceAel(itemSolicitacaoExame, null);
	}

	/**
	 * Evento de insert da tabela AelItemSolicitacaoExames. Toda alguns eventos
	 * a menos com relação ao método enforceInsert
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws BaseException
	 */
	public void enforceInsertContratualizacao(final AelItemSolicitacaoExames itemSolicitacaoExame, String nomeMicrocomputador, RapServidores servidorLogado,
			final Date dataFimVinculoServidor, final boolean flush) throws BaseException {
		// this.verificarProvas(itemSolicitacaoExame);
		this.atualizarExtrato(itemSolicitacaoExame, flush);
		this.verificarPrioridade(itemSolicitacaoExame);
		this.atualizarAmostras(itemSolicitacaoExame);
		this.atualizarNumeroUnico(itemSolicitacaoExame);

		// Gera dados nas tabelas do laudo único - Patologia
		// Não faz mais atraves da enforce pq o numero AP não é mais gerado na itemSolicitacaoExame e sim na AelAnatomoPatologico
//		if (DominioSituacaoItemSolicitacaoExame.AE.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {
//			this.atualizarLaudoUnico(itemSolicitacaoExame);
//		}

		this.atualizarAgrupamento(itemSolicitacaoExame);
		try {
			this.realizarInterfaceFaturamento(itemSolicitacaoExame, null, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		} catch (final InactiveModuleException e) {
			logError(e.getMessage());
		}
		// TODO: A procedure abaixo não se encontra migrada em função de não ser
		// necessária
		// para a internação, alta ou contratualização (segundo o Ney e a
		// Milena). Deverá
		// ser migrada quando o Faturamento de Exames também for.
		this.ffcInterfaceAel(itemSolicitacaoExame, null);

	}

	/**
	 * Verifica se a situação do exame foi modificada.
	 * 
	 * Se sim, retorna verdadeiro, senão falso.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @return {boolean}
	 */
	protected boolean verificarSituacaoItemSolicitacaoAlterada(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) {

		boolean response = false;

		if (CoreUtil.modificados(itemSolicitacaoExame.getSituacaoItemSolicitacao(), itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao())) {

			response = true;

		}

		return response;

	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_ver_SIT_ATIV.
	 * 
	 * Verifica se a situação do exame está ativa.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarSituacaoAtiva(final AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {
		
		final AelSitItemSolicitacoes situacao = this.getAelSitItemSolicitacoesDAO().obterPeloId(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo());
		
		if (!DominioSituacao.A.equals(situacao.getIndSituacao())) {

			throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_00420);

		}

	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_ATU_EXTRATO. (RN04)
	 * 
	 * Atualiza o extrato item solicitacao.
	 * @param servidorLogado2 
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	public void atualizarExtrato(final AelItemSolicitacaoExames itemSolicitacaoExame, final boolean flush) throws BaseException {
		
		// RN04.1
		// Atualiza o seqp baseado na tabela ael_extrato_item_solics

		// Obtém dataHoraEvento
		final Date dataHoraEvento = obterDataHoraEvento(itemSolicitacaoExame);
		
		// RN04.6
		// Faz insert na tabela ael_extrato_item_solics
		final AelExtratoItemSolicitacao extrato = new AelExtratoItemSolicitacao();
		extrato.setItemSolicitacaoExame(itemSolicitacaoExame);
		extrato.setAelSitItemSolicitacoes(itemSolicitacaoExame.getSituacaoItemSolicitacao());
		extrato.setDataHoraEvento(dataHoraEvento);
		extrato.setServidor(servidorLogadoFacade.obterServidorLogado());
		extrato.setAelMotivoCancelaExames(itemSolicitacaoExame.getAelMotivoCancelaExames());
		extrato.setComplementoMotCanc(itemSolicitacaoExame.getComplementoMotCanc());
		extrato.setServidorEhResponsabilide(itemSolicitacaoExame.getServidorResponsabilidade());

		try {
			this.getExamesFacade().inserirExtratoItemSolicitacao(extrato, flush);
		} catch (final PersistenceException e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_00474);
		}
	}

	/**
	 * Busca maior seqp do extrato item solicitação.
	 * 
	 * @param {Integer} iseSoeSeq
	 * @param {Short} iseSeqp
	 * @return {Short}
	 */
	private Short buscarMaiorSeqpExtratoItemSolicitacao(final Integer iseSoeSeq, final Short iseSeqp) {
		Short seqp = getAelExtratoItemSolicitacaoDAO().buscarMaiorSeqp(iseSoeSeq, iseSeqp);
		if (seqp == null) {
			seqp = 1;
		} else {
			seqp++;
		}

		return seqp;
	}

	/**
	 * Obter data/hora do evento.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @return {Date}
	 * @throws ApplicationBusinessException
	 */
	protected Date obterDataHoraEvento(final AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {
		// RN04.2
		// Se a situação do exame for igual a agendado
		Date dataHoraEvento;
		if (itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AG.toString())) {
			// RN04.3
			// Se o campo hed_dthr_agenda for diferente de nulo atualiza a
			// dataHoraEvento com o valor do campo
			final Date hedDthrAgenda = getAelItemHorarioAgendadoDAO().buscarMenorHedDthrAgenda(itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp());
			if (hedDthrAgenda == null) {
				throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_00473);
			} else {
				dataHoraEvento = hedDthrAgenda;
			}

		} else {
			// RN04.4
			// Recebe a data corrente do sistema
			dataHoraEvento = new Date();
		}

		return dataHoraEvento;
	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_ATU_CANCELAM.
	 * @param servidorLogado 
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws BaseException
	 */
	protected void atualizarCancelamento(final AelItemSolicitacaoExames itemSolicitacaoExame, String nomeMicrocomputador, 
			RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException {
		// RN05.1
		// Se o código de situação do exame (sit_codigo) estiver cancelado
		if (itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.CA.toString())) {
			// RN05.2
			// Busca os exames dependentes
			final List<CancelarExamesAreaExecutoraVO> examesDependentes = getAelExamesDependentesDAO().buscarPorExameMaterial(
					itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());
			for (final CancelarExamesAreaExecutoraVO exameDependente : examesDependentes) {
				// RN05.3
				// Se o campo ind_cancela_automatico for igual a ‘S’
				if (exameDependente.getIndCancelaAutomatico().isSim()) {
					// RN05.4
					// Se moc_seq for igual ao parâmetro P_MOC_CANCELA_ALTA ou
					// P_MOC_CANCELA_OBITO então executa atualizações
					final AghParametros parametroMocCancelaAlta = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOC_CANCELA_ALTA);
					if (parametroMocCancelaAlta == null || parametroMocCancelaAlta.getVlrNumerico() == null) {
						throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_01161);
					}
					final Short valorMocCancelaAlta = parametroMocCancelaAlta.getVlrNumerico().shortValue();
					final AghParametros parametroMocCancelaObito = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOC_CANCELA_OBITO);
					if (parametroMocCancelaObito == null || parametroMocCancelaObito.getVlrNumerico() == null) {
						throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_01162);
					}
					final Short valorMocCancelaObito = parametroMocCancelaObito.getVlrNumerico().shortValue();

					boolean realizarUpdate = false;

					final AelItemSolicitacaoExames itemSolicitacaoExameDependente = getAelItemSolicitacaoExameDAO().obterPorId(
							exameDependente.getSoeSeq(), exameDependente.getSeqp());

					if (itemSolicitacaoExame.getAelMotivoCancelaExames() != null
							&& (itemSolicitacaoExame.getAelMotivoCancelaExames().getSeq().equals(valorMocCancelaAlta) || itemSolicitacaoExame
									.getAelMotivoCancelaExames().getSeq().equals(valorMocCancelaObito))) {

						if (itemSolicitacaoExameDependente.getSituacaoItemSolicitacao().getCodigo()
								.equals(DominioSituacaoItemSolicitacaoExame.AC.toString())
								|| itemSolicitacaoExameDependente.getSituacaoItemSolicitacao().getCodigo()
										.equals(DominioSituacaoItemSolicitacaoExame.AX.toString())
								|| itemSolicitacaoExameDependente.getSituacaoItemSolicitacao().getCodigo()
										.equals(DominioSituacaoItemSolicitacaoExame.AG.toString())
								|| itemSolicitacaoExameDependente.getSituacaoItemSolicitacao().getCodigo()
										.equals(DominioSituacaoItemSolicitacaoExame.CS.toString())) {
							realizarUpdate = true;
						}

					} else {

						if (itemSolicitacaoExameDependente.getSituacaoItemSolicitacao().getCodigo()
								.equals(DominioSituacaoItemSolicitacaoExame.AC.toString())
								|| itemSolicitacaoExameDependente.getSituacaoItemSolicitacao().getCodigo()
										.equals(DominioSituacaoItemSolicitacaoExame.AX.toString())
								|| itemSolicitacaoExameDependente.getSituacaoItemSolicitacao().getCodigo()
										.equals(DominioSituacaoItemSolicitacaoExame.AG.toString())
								|| itemSolicitacaoExameDependente.getSituacaoItemSolicitacao().getCodigo()
										.equals(DominioSituacaoItemSolicitacaoExame.CS.toString())
								|| itemSolicitacaoExameDependente.getSituacaoItemSolicitacao().getCodigo()
										.equals(DominioSituacaoItemSolicitacaoExame.AE.toString())) {
							realizarUpdate = true;
						}
					}

					if (realizarUpdate) {
						AelItemSolicitacaoExames original = new AelItemSolicitacaoExames();
						try {
							BeanUtils.copyProperties(original, itemSolicitacaoExameDependente);
						} catch (IllegalAccessException | InvocationTargetException e) {
							LOG.error("Erro ao copiar itemSolicitacaoExame.", e);
						}
						// Atualiza
						itemSolicitacaoExameDependente.setSituacaoItemSolicitacao(itemSolicitacaoExame.getSituacaoItemSolicitacao());
						itemSolicitacaoExameDependente.setAelMotivoCancelaExames(itemSolicitacaoExame.getAelMotivoCancelaExames());
						itemSolicitacaoExameDependente.setComplementoMotCanc(itemSolicitacaoExame.getComplementoMotCanc());
						getItemSolicitacaoExameRN().atualizar(itemSolicitacaoExameDependente, original,nomeMicrocomputador, dataFimVinculoServidor,servidorLogado);
					}
				}
			}
		}
	}

	/**
	 * Verifica se a situação do exame foi alterada de AG (Agendado) para CO
	 * (Coletado). Se sim, retorna verdadeiro, senão falso.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @return {boolean}
	 * @throws ApplicationBusinessException
	 */
	protected boolean verificarAlteracaoSituacaoExameAgendadoParaColetado(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws ApplicationBusinessException {

		boolean response = false;

		if (DominioSituacaoItemSolicitacaoExame.AG.toString().equals(itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo())
				&& DominioSituacaoItemSolicitacaoExame.CO.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {

			response = true;

		}

		return response;

	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_AT_HOR_AG_CO.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws BaseException
	 */
	private void atualizarHorarioExameAgendadoParaColetado(final AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {
		final List<AelItemHorarioAgendado> itensHorarioAgendados = getAelItemHorarioAgendadoDAO().buscarPorItemSolicitacaoExame(itemSolicitacaoExame);

		for (final AelItemHorarioAgendado itemHorarioAgendado : itensHorarioAgendados) {
			// Verifica se existe exame não coletado para o horário agendado
			final Integer quantidadeExamesNaoColetados = getAelItemHorarioAgendadoDAO().buscarQuantidadeHorariosNaoColetados(
					itemHorarioAgendado.getId().getHedGaeUnfSeq(), itemHorarioAgendado.getId().getHedGaeSeqp(),
					itemHorarioAgendado.getId().getHedDthrAgenda());

			if (quantidadeExamesNaoColetados == 0) {
				// Atualiza Horário Exame para EXECUTADO
				final AelHorarioExameDisp horarioExame = getAelHorarioExameDispDAO().obterPorId(itemHorarioAgendado.getId().getHedGaeUnfSeq(),
						itemHorarioAgendado.getId().getHedGaeSeqp(), itemHorarioAgendado.getId().getHedDthrAgenda());
				horarioExame.setSituacaoHorario(DominioSituacaoHorario.E);

				this.getExamesFacade().atualizarHorarioExameDisp(horarioExame);
			}
		}

	}

	/**
	 * Verifica se a situação do exame foi alterada de AG (Agendado) para AE
	 * (AREA EXECUTORA). Se sim, retorna verdadeiro, senão falso. (RN08)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @return {boolean}
	 */
	protected boolean verificarAlteracaoSituacaoExameAgendadoParaAreaExecutora(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) {

		boolean response = false;

		if (DominioSituacaoItemSolicitacaoExame.AG.toString().equals(itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo())
				&& DominioSituacaoItemSolicitacaoExame.AE.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {

			response = true;

		}

		return response;

	}
	
	/**
	 * Verifica se a situação do exame foi alterada de AE (AREA EXECUTORA)
	 *	para AG (Agendado). Se sim, retorna verdadeiro, senão falso. (RN08)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @return {boolean}
	 */
	public boolean verificarAlteracaoSituacaoExameAreaExecutoraParaAgendado(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) {

		boolean response = false;

		if (DominioSituacaoItemSolicitacaoExame.AE.toString().equals(itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo())
				&& DominioSituacaoItemSolicitacaoExame.AG.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {

			response = true;

		}

		return response;

	}

	/**
	 * RN9
	 * 
	 * @ORADB aelk_ise_rn.rn_isep_AT_HOR_AG_AE
	 * 
	 */
	protected void atualizarHorarioExameAgendadoParaAreaExecutora(final AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {

		this.atualizarHorarioExameAgendadoParaColetado(itemSolicitacaoExame);

	}

	/**
	 * Verifica se a situação do exame foi alterada para LI (Liberado). (RN10)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @return {boolean}
	 * @throws ApplicationBusinessException
	 */
	protected boolean verificarAlteracaoSituacaoExameParaLiberado(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws ApplicationBusinessException {

		boolean response = false;

		if (!DominioSituacaoItemSolicitacaoExame.LI.toString().equals(itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo())
				&& DominioSituacaoItemSolicitacaoExame.LI.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {

			response = true;

		}

		return response;

	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_VER_RESUL.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	protected void verificarResultado(final AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {
		final boolean existeResultados = getAelResultadoExameDAO().existeResultadosNaoAnulados(itemSolicitacaoExame.getId().getSoeSeq(),
				itemSolicitacaoExame.getId().getSeqp());

		final boolean existeDocumentosAnexados = getAelDocResultadoExameDAO().existeDocumentosAnexadosNaoAnulados(
				itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());

		// Para liberar um exame deve existir pelo menos um resultado digitado
		// ou um documento anexado
		if (!existeResultados && !existeDocumentosAnexados) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_02550);
		}
	}

	/**
	 * @param itemSolicitacaoExameOriginal 
	 * @param itemSolicitacaoExame
	 * @param servidorLogado 
	 * @ORADB aelk_ise_rn.rn_isep_ATU_REFLEXO
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void atualizarReflexo(final AelItemSolicitacaoExames itemSolicitacaoExameOriginal, AelItemSolicitacaoExames itemSolicitacaoExame, 
			final String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor, final boolean flush) throws BaseException {
		/*RN_ISEP_ATU_REFLEXO*/
		/* verifica se o ex liberado possui algum resultado que esteja cadastrado como exame reflexo */
		/* se tiver, para cada resultado com reflexo, gera item ou nova solicitacao contendo este exame reflexo */		
		
		//Verifica se tem algum exame reflexo cadastrado
		List<AelExameReflexo> reflexos =  getAelExameReflexoDAO().buscarAelExamesReflexoAtivos(itemSolicitacaoExame.getExame().getSigla(), itemSolicitacaoExame.getMaterialAnalise().getSeq());
		Boolean achou = false;
		for(AelExameReflexo reflexo : reflexos) {
			achou = false;
			//Verifica se tem campo do laudo com o mesmo resultado que dispara reflexo para campos codificados			
			if(DominioTipoCampoCampoLaudo.C.equals(reflexo.getAelCampoLaudo().getTipoCampo())) {
				if(getAelResultadoExameDAO().isResultadoCodigo(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp(), 
						reflexo.getAelCampoLaudo().getSeq(), reflexo.getAelResultadoCodificado().getId().getGtcSeq(), 
						reflexo.getAelResultadoCodificado().getId().getSeqp())) {
					achou = true;
				}
			}
			
		//Verifica se tem campo do laudo com o mesmo resultado que dispara reflexo para campos numericos
			if(DominioTipoCampoCampoLaudo.N.equals(reflexo.getAelCampoLaudo().getTipoCampo())) {
				if(getAelResultadoExameDAO().isResultadoNumerico(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp(), 
						reflexo.getAelCampoLaudo().getSeq(), reflexo.getResulNumIni(), 
						reflexo.getResulNumFim())) {
					achou = true;
				}
				
			}
		//Verifica se tem campo do laudo com o mesmo resultado que dispara reflexo para campos alfabeticos
			if(DominioTipoCampoCampoLaudo.A.equals(reflexo.getAelCampoLaudo().getTipoCampo())) {
				List<String> resultadoAlfa = getAelResultadoExameDAO().obterDescricaoResultadoAlfa(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp(), 
						reflexo.getAelCampoLaudo().getSeq());
				if(resultadoAlfa != null && !resultadoAlfa.isEmpty() && StringUtils.equals(resultadoAlfa.get(0), reflexo.getResulAlfa())) {
					achou = true;
				}
			}

		//Se achou vai solicitar exame reflexo
		//Envia como parametro o exame/material do exame Reflexo e a
		//unidade executora do exame pai
			if(achou) {
				aelpSolicitaReflex(itemSolicitacaoExameOriginal, itemSolicitacaoExame, itemSolicitacaoExame.getId().getSoeSeq(), 
									itemSolicitacaoExame.getId().getSeqp(), 
									reflexo.getAelExamesMaterialAnaliseReflexo().getId().getExaSigla(), 
									reflexo.getAelExamesMaterialAnaliseReflexo().getId().getManSeq(), 
									itemSolicitacaoExame.getUnidadeFuncional().getSeq(), 
									reflexo.getIdadeLimite(), 
									nomeMicrocomputador, 
									servidorLogado,
									dataFimVinculoServidor, flush);
			}
		}
	}
	//
	
	/**
	 * @param itemSolicitacaoExameOriginal 
	 * @param itemSolicitacaoExame
	 * @param servidorLogado 
	 * @ORADB AELP_SOLICITA_REFLEX
	 */
	private void aelpSolicitaReflex(AelItemSolicitacaoExames itemSolicitacaoExameOriginal, AelItemSolicitacaoExames itemSolicitacaoExame, final Integer iseSoeSeq,final Short iseSeqp, final String exaSigla, final Integer manSeq, 
			final Short unfSeq, final Short idadeLimite, final String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor, final Boolean flush) 
	throws BaseException {
	/* Solicita exame reflexo como um novo ítem da solicitação se o paciente ainda estiver em atendimento,ou faz uma nova solicitação 
	 * se não for possível incluir novo ítem */

	Integer pacCodigo = null;
	DominioOrigemAtendimento origem = null;
	DominioPacAtendimento indPacAtendimento = null;
	FatConvenioSaudePlano convenioSaudePlano = null;
	FatConvenioSaude convenioSaude = null;
	RapServidores servidorResponsabilidade = null;
	Boolean recemNascido = null;
	String criadoEm = null;
	Boolean usaAntimicrobianos = null;
	Boolean indTransplante = null;
	Date dtObito = null;
		
	//Verifica o atendimento da solicitação que gerou o reflexo
		AelSolicitacaoExames solExameAtd = getAelSolicitacaoExameDAO().obterAelSolicitacaoExameComAtendimentoPeloSeq(iseSoeSeq);
		//verifica se é um atendimento diverso
		if(solExameAtd == null) {
			AelSolicitacaoExames solExameAtv = getAelSolicitacaoExameDAO().obterAelSolicitacaoExameComAtendimentoDiversoPeloSeq(iseSoeSeq);
			if(solExameAtv == null) {
				return;
			}
			else {
				pacCodigo = (solExameAtv.getAtendimentoDiverso().getAipPaciente() != null)?solExameAtv.getAtendimentoDiverso().getAipPaciente().getCodigo():null;
				origem = DominioOrigemAtendimento.D;
			}
		}
		else {
			origem = solExameAtd.getAtendimento().getOrigem();
			indPacAtendimento = solExameAtd.getAtendimento().getIndPacAtendimento();
			pacCodigo = solExameAtd.getAtendimento().getPaciente().getCodigo();
			convenioSaude = solExameAtd.getConvenioSaude();
			convenioSaudePlano = solExameAtd.getConvenioSaudePlano();
			servidorResponsabilidade = solExameAtd.getServidorResponsabilidade();
			recemNascido = solExameAtd.getRecemNascido();
			criadoEm = DateUtil.obterDataFormatada(solExameAtd.getCriadoEm(), "dd/MM/yyyy");
			usaAntimicrobianos = solExameAtd.getUsaAntimicrobianos();
			indTransplante = solExameAtd.getIndTransplante();
		}
		
	//verifica se o paciente teve óbito ou tem idade inferior a idade limite pra gerar reflexo
		if(pacCodigo != null) {
			AipPacientes paciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
			dtObito = paciente.getDtObito();
			//se teve óbito não vai mais gerar reflexo
			if(dtObito != null) {
				return;
			}
			else {
				if(idadeLimite != null && idadeLimite > 0) {
					Calendar dtIni = Calendar.getInstance();
					dtIni.setTime(paciente.getDtNascimento());
	                Calendar dtFim = Calendar.getInstance();
	                dtFim.setTime(new Date());
	                long idadeEmMeses = Math.abs(dtFim.get(Calendar.MONTH) -
	                		dtIni.get(Calendar.MONTH) + (dtFim.get(Calendar.YEAR) -
	                				dtIni.get(Calendar.YEAR)) * 12);
					if(idadeEmMeses <= idadeLimite) {
						return;
					}
				}
			}
		}
		//Se a origem for Internação ou Nascimento ou Urgencia :
		//e o paciente ainda estiver internado, gera novo ítem na solicitação.
		//e o paciente já deu alta, grava nova solicitação como atend externo
		if(DominioOrigemAtendimento.I.equals(origem) || DominioOrigemAtendimento.N.equals(origem) || DominioOrigemAtendimento.U.equals(origem)) {
			if(DominioPacAtendimento.S.equals(indPacAtendimento)) {
				aelpInsereItem(itemSolicitacaoExameOriginal, itemSolicitacaoExame, iseSoeSeq, iseSeqp, exaSigla, manSeq, unfSeq, 0, nomeMicrocomputador, 
						servidorLogado, dataFimVinculoServidor, flush);
			}
		//Se o paciente não está mais em atendimento gerar um atendimento externo,
		//gerar nova solicitação para este atend externo para poder solicitar o reflexo
		//e gerar carta
			else {
				Integer soeSeqGerada = null;
				Byte convenioSaudePlanoSeq = aelpInsereAtendimentoExterno(convenioSaudePlano, convenioSaude, pacCodigo, unfSeq, nomeMicrocomputador, servidorResponsabilidade);
				soeSeqGerada = aelpInsereSolicitacao(iseSoeSeq, getFaturamentoFacade().obterConvenioSaudePlano(convenioSaude.getCodigo(), convenioSaudePlanoSeq), 
						convenioSaude, pacCodigo, unfSeq, recemNascido, criadoEm, usaAntimicrobianos, 
						indTransplante, soeSeqGerada, nomeMicrocomputador, servidorLogado,dataFimVinculoServidor, indPacAtendimento);
				aelpInsereItem(itemSolicitacaoExameOriginal, itemSolicitacaoExame, iseSoeSeq, iseSeqp, exaSigla, manSeq, unfSeq, soeSeqGerada, 
						nomeMicrocomputador, servidorLogado, dataFimVinculoServidor, flush);
			}
		}
		else {
			//se a origem for atendimento diverso, verifica se gera
			if(DominioOrigemAtendimento.D.equals(origem)) {
				List<AelItemSolicitacaoExames> listaItensSolcEx = getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExamePorSolicitacaoExame(iseSoeSeq);
				if(listaItensSolcEx != null && !listaItensSolcEx.isEmpty()) {
					Boolean gerarCarta = getPesquisaExamesFacade().verGeraCarta(listaItensSolcEx.get(0));
					if(gerarCarta) {
						aelpInsereItem(itemSolicitacaoExameOriginal, itemSolicitacaoExame, iseSoeSeq, iseSeqp, exaSigla, manSeq, unfSeq, 0, nomeMicrocomputador, 
								servidorLogado, dataFimVinculoServidor, flush);
					}
				}
			}
			else {
			    //se a origem for ambulatorio, gera novo ítem e gera carta para recoleta
				aelpInsereItem(itemSolicitacaoExameOriginal, itemSolicitacaoExame, iseSoeSeq, iseSeqp, exaSigla, manSeq, unfSeq, 0, nomeMicrocomputador, 
						servidorLogado, dataFimVinculoServidor, flush);
			}
		}
	
	}

	
	/**
	 * ORADB AELP_INSERE_SOLIC
	 * @param servidorLogado 
	 * 
	 */ 	
	private Integer aelpInsereSolicitacao(final Integer iseSoeSeqOrigem, final FatConvenioSaudePlano convenioSaudePlano, final FatConvenioSaude convenioSaude,
			final Integer pacCodigo, final Short unfSeq, final Boolean recemNascido, final String criadoEm, 
			final Boolean usaAntimicrobianos, final Boolean indTransplante, final Integer soeSeqGerada,
			final String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor, DominioPacAtendimento pacAtendimento) throws BaseException {
		
		AghAtendimentos atd = null;
		StringBuffer informacoesClinicas = new StringBuffer(101);
			
		//Busca o último atendimento externo gerado para o paciente
		List<AghAtendimentos> atendimentos = getAghuFacade().obterAtendimentosPorPacienteEOrigemOrdenadosPeloAtdExt(pacAtendimento, pacCodigo, Arrays.asList(new DominioOrigemAtendimento[] {DominioOrigemAtendimento.X}));
		if(atendimentos != null && !atendimentos.isEmpty()) {
			atd = atendimentos.get(0);
		}
		else {
			return null;
		}
		
		informacoesClinicas.append("Solicitação de exame confirmatório gerada pelo sistema. ")
		.append("Solicitação de origem número:  " + iseSoeSeqOrigem + " do dia " + criadoEm);
		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		solicitacaoExame.setUnidadeFuncional(getAghuFacade().obterUnidadeFuncional(unfSeq));
		solicitacaoExame.setAtendimento(atd);
		solicitacaoExame.setRecemNascido(recemNascido);
		solicitacaoExame.setServidorResponsabilidade(servidorLogado);
		solicitacaoExame.setInformacoesClinicas(informacoesClinicas.toString());
		solicitacaoExame.setConvenioSaudePlano(convenioSaudePlano);
		solicitacaoExame.setUnidadeFuncionalAreaExecutora(getAghuFacade().obterUnidadeFuncional(unfSeq));
		solicitacaoExame.setUsaAntimicrobianos(usaAntimicrobianos);
		solicitacaoExame.setIndTransplante(indTransplante);
		
		getSolicitacaoExameFacade().gravar(solicitacaoExame, nomeMicrocomputador, dataFimVinculoServidor);
		
		return solicitacaoExame.getSeq();
	}
	
	/**
	 * ORADB AELP_INSERE_ATD_EXT
	 * 
	 */ 	
	private Byte aelpInsereAtendimentoExterno(final FatConvenioSaudePlano convenioSaudePlano, final FatConvenioSaude convenioSaude, 
			final Integer pacCodigo, final Short unfSeq, 
			final String nomeMicrocomputador,  final RapServidores servidor) throws BaseException {
		
		Byte convenioSaudePlanoSeq = null;
		//Busca plano ambulatorial ativo do convenio da solicitação de origem
		if(convenioSaude != null) {
			List<Byte> convenios = getFaturamentoFacade().obterListaConvenioSaudeAtivoComPlanoAmbulatorialAtivo(convenioSaude.getCodigo());
			if(convenios != null && !convenios.isEmpty()) {
				convenioSaudePlanoSeq = convenios.get(0);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
		
		AghAtendimentosPacExtern atendimentoExterno = new AghAtendimentosPacExtern();
		atendimentoExterno.setConvenioSaudePlano(getFaturamentoFacade().obterConvenioSaudePlano(convenioSaude.getCodigo(), convenioSaudePlanoSeq));
		atendimentoExterno.setPaciente(getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo));
		atendimentoExterno.setUnidadeFuncional(getAghuFacade().obterUnidadeFuncional(unfSeq));
		atendimentoExterno.setServidor(servidor);
		getExamesFacade().gravarAghAtendimentoPacExtern(atendimentoExterno, nomeMicrocomputador, servidor);
		
		return convenioSaudePlanoSeq;
	}
	
	
	
	/**
	 * ORADB AELP_INSERE_ITEM
	 * 
	 */ 	
	private void aelpInsereItem(AelItemSolicitacaoExames itemSolicitacaoExameOriginal, AelItemSolicitacaoExames itemSolicitacaoExame, 
			final Integer soeSeq, final Short seqp, final String exaSigla, final Integer manSeq, 
			final Short unfSeqPai, final Integer soeSeqExt, 
			final String nomeMicrocomputador, RapServidores servidorLogado,final Date dataFimVinculoServidor, final boolean flush) throws BaseException {
	//Procedure para inserção do exame reflexo como  ítem da solicitação
	Boolean indColetavel = null;
	AelSitItemSolicitacoes situacaoItemSol = null;
	Integer soeSeqReflexo = null;
	Short seqpReflexo = null;
	Short unfReflexo = null;
//Migrado do ORACLE - Aparentemente sem utilidade
//	Boolean indDesativaTemp = null;
//	Date dthrReativaTemp = null;
		
	//Busca a situação do exame reflexo conforme o material
		AelMateriaisAnalises materialAnalise = getAelMaterialAnaliseDAO().obterPorChavePrimaria(manSeq);
		if(materialAnalise == null) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_01004);
		}
		else {
			indColetavel = materialAnalise.getIndColetavel();
		}
		
		if(indColetavel) {
			final AghParametros paramSitAColetar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
			situacaoItemSol = getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(paramSitAColetar.getVlrTexto());
		}
		else {
			final AghParametros paramSitAExecutar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);
			situacaoItemSol = getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(paramSitAExecutar.getVlrTexto());
		}

		//Busca a unidade executora do exame reflexo
		//tenta colocar na mesma unidade executora do exame pai
		AelUnfExecutaExames unidadeExecutorExa = getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(exaSigla, manSeq, unfSeqPai);
		if(unidadeExecutorExa == null || !DominioSituacao.A.equals(unidadeExecutorExa.getIndSituacao())) {
			List<AelUnfExecutaExames> unidadesExecutorasExa = getAelUnfExecutaExamesDAO().buscaListaAelUnfExecutaExamesAtivas(exaSigla, manSeq);
			if(unidadesExecutorasExa != null && !unidadesExecutorasExa.isEmpty()) {
				unfReflexo = unidadesExecutorasExa.get(0).getUnidadeFuncional().getSeq();
//				indDesativaTemp = unidadesExecutorasExa.get(0).getIndDesativaTemp();
//				dthrReativaTemp = unidadesExecutorasExa.get(0).getDthrReativaTemp();
			} else {
				//Se não encontrar nenhuma unidade executora ativa, deve notificar o usuário, gravar o resultado e não liberar o exame
				itemSolicitacaoExame.setSituacaoItemSolicitacao(itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao());
				itemSolicitacaoExame.setDthrLiberada(itemSolicitacaoExameOriginal.getDthrLiberada());
				itemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().atualizar(itemSolicitacaoExame);
				this.getAelItemSolicitacaoExameDAO().flush();

				throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_03413, getAelExamesDAO().obterPorChavePrimaria(exaSigla).getDescricaoUsual());
			}
		} else {
			unfReflexo = unfSeqPai;
//			indDesativaTemp = unidadeExecutorExa.getIndDesativaTemp();
//			dthrReativaTemp = unidadeExecutorExa.getDthrReativaTemp();
		}
		//Busca informações do exame origem que serão replicadas
		//AelItemSolicitacaoExames itemSolicitacaoExameOriginalReflexo = getAelItemSolicitacaoExameDAO().obterOriginal(new AelItemSolicitacaoExamesId(soeSeq, seqp));
		AelItemSolicitacaoExames itemSolicitacaoExameOriginalReflexo = new AelItemSolicitacaoExames();
		itemSolicitacaoExameOriginalReflexo = getAelItemSolicitacaoExameDAO().obterOriginal(new AelItemSolicitacaoExamesId(soeSeq, seqp));
		//itemSolicitacaoExameOriginalReflexo = getAelItemSolicitacaoExameDAO().obterPorId(soeSeq, seqp);
		if(soeSeqExt == 0) {
			soeSeqReflexo = soeSeq;
		}
		else {
			soeSeqReflexo = soeSeqExt;
		}
		
		Short maxSeqp = getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExameSeqpMax(soeSeqReflexo);
		maxSeqp = (maxSeqp == null) ? 0 : maxSeqp; 
		seqpReflexo = maxSeqp++;

		itemSolicitacaoExameOriginalReflexo.getId().setSoeSeq(soeSeqReflexo);
		itemSolicitacaoExameOriginalReflexo.getId().setSeqp(seqpReflexo);
		itemSolicitacaoExameOriginalReflexo.setSolicitacaoExame(getAelSolicitacaoExameDAO().obterPorChavePrimaria(soeSeqReflexo));
		itemSolicitacaoExameOriginalReflexo.setSituacaoItemSolicitacao(situacaoItemSol);
		itemSolicitacaoExameOriginalReflexo.setExame(getAelExamesDAO().obterPorChavePrimaria(exaSigla));
		itemSolicitacaoExameOriginalReflexo.setMaterialAnalise(getAelMaterialAnaliseDAO().obterPorChavePrimaria(manSeq));
		itemSolicitacaoExameOriginalReflexo.setUnidadeFuncional(getAghuFacade().obterUnidadeFuncional(unfReflexo));
		itemSolicitacaoExameOriginalReflexo.setAelUnfExecutaExames(getAelUnfExecutaExamesDAO().obterPorChavePrimaria(new AelUnfExecutaExamesId(exaSigla, manSeq, getAghuFacade().obterUnidadeFuncional(unfReflexo))));
		itemSolicitacaoExameOriginalReflexo.setIndGeradoAutomatico(true);
		itemSolicitacaoExameOriginalReflexo.setAelMotivoCancelaExames(null);
		itemSolicitacaoExameOriginalReflexo.setNroAmostras(null);
		itemSolicitacaoExameOriginalReflexo.setIntervaloDias(null);
		itemSolicitacaoExameOriginalReflexo.setIntervaloHoras(null);
		itemSolicitacaoExameOriginalReflexo.setDthrProgramada(new Date());
		itemSolicitacaoExameOriginalReflexo.setPrioridadeExecucao(null);
		itemSolicitacaoExameOriginalReflexo.setDataImpSumario(null);
		itemSolicitacaoExameOriginalReflexo.setItemSolicitacaoExame(getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(new AelItemSolicitacaoExamesId(soeSeq, seqp)));
		itemSolicitacaoExameOriginalReflexo.setIndImprimiuTicket(false);
		itemSolicitacaoExameOriginalReflexo.setDthrLiberada(null);
		itemSolicitacaoExameOriginalReflexo.setTipoEmissaoSumario(null);
		itemSolicitacaoExameOriginalReflexo.setUieUoeSeq(null);
		itemSolicitacaoExameOriginalReflexo.setUieSeqp(null);
		itemSolicitacaoExameOriginalReflexo.setIndCargaContador(null);
		itemSolicitacaoExameOriginalReflexo.setIndImpressoLaudo(null);
		itemSolicitacaoExameOriginalReflexo.setUnfSeqAvisa(null);
		itemSolicitacaoExameOriginalReflexo.setIndPossuiImagem(null);
		itemSolicitacaoExameOriginalReflexo.setIndTicketPacImp(null);
		itemSolicitacaoExameOriginalReflexo.setIndInfComplImp(null);
		itemSolicitacaoExameOriginalReflexo.setIndTipoMsgCxPostal(null);
		itemSolicitacaoExameOriginalReflexo.setDthrMsgCxPostal(null);
		itemSolicitacaoExameOriginalReflexo.setComplementoMotCanc(null);
		
		//itemSolicitacaoExameOriginalReflexo.setNumeroApOrigem(itemSolicitacaoExameOriginalReflexo.getNumeroAp());
		
		getItemSolicitacaoExameRN().inserir(itemSolicitacaoExameOriginalReflexo, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor, flush);

		List<AelItemSolicitacaoExames> listaItensSolcEx = getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExamePorSolicitacaoExame(soeSeqReflexo);
		if(listaItensSolcEx != null && !listaItensSolcEx.isEmpty()) {
			Boolean gerarCarta = getPesquisaExamesFacade().verGeraCarta(listaItensSolcEx.get(0));
			if(gerarCarta) {
				 getPesquisaExamesFacade().geraCartaCanc(itemSolicitacaoExameOriginalReflexo);
			}
		}
	}
	
	/**
	 * ORADB RN_ISEP_ATU_AGRP. (RN14)
	 * 
	 * Atualiza agrupamento de exames.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	private void atualizarAgrupamento(final AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {

		final AelPacAgrpPesqExames pacAgrpPesqExame = this.verificarAgrupamento(itemSolicitacaoExame);
		if (pacAgrpPesqExame != null) {

			getAelPacAgrpPesqExameDAO().persistir(pacAgrpPesqExame);

		}

	}

	/**
	 * Verifica se deve ser atualizado o agrupamento de exames. Se o objeto
	 * retornar DIFERENTE de NULO é porque deve ser atualizado.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	private AelPacAgrpPesqExames verificarAgrupamento(final AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {

		AelPacAgrpPesqExames pacAgrpPesqExame = null;

		// RN14.1
		// Verifica se o exame liberado está na tabela
		// ael_agrp_pesquisa_x_exames
		final AelAgrpPesquisaXExame agrpPesquisaXExame = getAelAgrpPesquisaXExameDAO().buscarAtivoPorUnfExecutaExame(
				itemSolicitacaoExame.getExame().getSigla(), itemSolicitacaoExame.getMaterialAnalise().getSeq(),
				itemSolicitacaoExame.getUnidadeFuncional().getSeq(),DominioSituacao.A);
		if (agrpPesquisaXExame != null) {
			final Integer axeSeq = agrpPesquisaXExame.getSeq();

			// RN14.2
			// Verifica qual o paciente
			final AghAtendimentos atendimento = itemSolicitacaoExame.getSolicitacaoExame().getAtendimento();
			if (atendimento != null) {
				final AipPacientes paciente = atendimento.getPaciente();

				// RN14.3
				// Verifica se tem campo do laudo com o mesmo resultado que
				// dispara reflexo para campos alfabéticos
				final boolean existeResultadoDisparaReflexo = getAelPacAgrpPesqExameDAO().obterPorId(axeSeq, paciente.getCodigo()) != null;

				// RN14.4
				// Se não encontrar o campo então insere registro na tabela
				// ael_pac_agrp_pesq_exames
				if (!existeResultadoDisparaReflexo) {

					final AelPacAgrpPesqExamesId pacAgrpPesqExameId = new AelPacAgrpPesqExamesId(axeSeq, paciente.getCodigo());
					pacAgrpPesqExame = new AelPacAgrpPesqExames(pacAgrpPesqExameId);

				}

			}
		}

		return pacAgrpPesqExame;

	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_atu_sit_amos.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 */
	private void atualizarSituacaoAmostra(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador) throws BaseException {
		DominioSituacaoAmostra novaSituacaoAmostra = null;

		// RN15.1
		// Se for coletável então busca nova situação do item amostra de acordo
		// com a situação do item solicitação exame
		if (itemSolicitacaoExame.getMaterialAnalise().getIndColetavel()) {
			novaSituacaoAmostra = obterNovaSituacaoAmostra(itemSolicitacaoExameOriginal, itemSolicitacaoExame);
		}

		// RN15.2
		// Se a nova situação não for nula, atualiza tabela
		// ael_amostra_item_exames
		if (novaSituacaoAmostra != null) {
			for (final AelAmostraItemExames itemAmostra : getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesAelAmostrasPorItemSolicitacaoExame(
					itemSolicitacaoExame)) {
				itemAmostra.setSituacao(novaSituacaoAmostra);
				this.getExamesFacade().atualizarAelAmostraItemExames(itemAmostra, true, false, nomeMicrocomputador);
			}
		}

		// RN15.3
		// Se a antiga situação do exame for AGENDADO ou AREA EXECUTORA E a nova
		// situação for igual a CANCELADO então deleta registro da tabela
		// ael_item_horario_agendados
		if ((itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AG.toString()) || itemSolicitacaoExameOriginal
				.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AE.toString()))
				&& itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.CA.toString())) {

			final List<AelItemHorarioAgendado> horariosAgendados = getAelItemHorarioAgendadoDAO().buscarPorItemSolicitacaoExame(
					itemSolicitacaoExameOriginal);
			for (final AelItemHorarioAgendado horariosAgendado : horariosAgendados) {
				getExamesFacade().removerItemHorarioAgendado(horariosAgendado, Boolean.TRUE, nomeMicrocomputador, itemSolicitacaoExame, itemSolicitacaoExameOriginal);
			}
		}

	}

	/**
	 * 
	 * @ORADB aelc_ver_sit_it_amos
	 * 
	 */
	protected DominioSituacaoAmostra obterNovaSituacaoAmostra(final AelItemSolicitacaoExames itemSolicitacaoExameOld,
			final AelItemSolicitacaoExames itemSolicitacaoExameNew) {
		final String codigoSituacaoOld = itemSolicitacaoExameOld.getSituacaoItemSolicitacao().getCodigo();
		final String codigoSituacaoNew = itemSolicitacaoExameNew.getSituacaoItemSolicitacao().getCodigo();

		if ((codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.CO.toString()) || codigoSituacaoNew
				.equals(DominioSituacaoItemSolicitacaoExame.CS.toString()))
				&& (!codigoSituacaoOld.equals(DominioSituacaoItemSolicitacaoExame.CO.toString()) && !codigoSituacaoOld
						.equals(DominioSituacaoItemSolicitacaoExame.CS.toString()))) {
			return DominioSituacaoAmostra.C;
		} else if (codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.LI.toString())
				&& !codigoSituacaoOld.equals(DominioSituacaoItemSolicitacaoExame.LI.toString())) {
			return DominioSituacaoAmostra.E;
		} else if ((codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.AX.toString())
				|| codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.AC.toString()) || codigoSituacaoNew
				.equals(DominioSituacaoItemSolicitacaoExame.AG.toString()))
				&& (!codigoSituacaoOld.equals(DominioSituacaoItemSolicitacaoExame.AX.toString())
						&& !codigoSituacaoOld.equals(DominioSituacaoItemSolicitacaoExame.AC.toString()) && !codigoSituacaoOld
						.equals(DominioSituacaoItemSolicitacaoExame.AG.toString()))) {
			return DominioSituacaoAmostra.G;
		} else if (codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.RE.toString())
				&& !codigoSituacaoOld.equals(DominioSituacaoItemSolicitacaoExame.RE.toString())) {
			return DominioSituacaoAmostra.U;
		} else if ((codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.AE.toString())
				|| codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.EX.toString()) || codigoSituacaoNew
				.equals(DominioSituacaoItemSolicitacaoExame.EO.toString()))
				&& (!codigoSituacaoOld.equals(DominioSituacaoItemSolicitacaoExame.AE.toString())
						&& !codigoSituacaoOld.equals(DominioSituacaoItemSolicitacaoExame.EX.toString()) && !codigoSituacaoOld
						.equals(DominioSituacaoItemSolicitacaoExame.EO.toString()))) {
			return DominioSituacaoAmostra.R;
		} else if (codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.EC.toString())
				&& !codigoSituacaoOld.equals(DominioSituacaoItemSolicitacaoExame.EC.toString())) {
			return DominioSituacaoAmostra.M;
		} else if (codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.CA.toString())
				&& !codigoSituacaoOld.equals(DominioSituacaoItemSolicitacaoExame.CA.toString())) {
			return DominioSituacaoAmostra.A;
		}

		return null;
	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_atu_exme_abs. (RN16)
	 * 
	 * Gera dados na tabela abs_conclusao_exames - ABS.
	 * @param string 
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void atualizarExameAbs(final AelItemSolicitacaoExames itemSolicitacaoExame, String usuarioLogado) throws ApplicationBusinessException,
			ApplicationBusinessException {

		if (!isHCPA() || !aelExameApItemSolicDAO.isOracle()) {
			return;
		}

		final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AELK_ISE_RN_RN_ISEP_ATU_EXME_ABS.toString();
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection) throws SQLException {

				CallableStatement cs = null;
				try {
					final StringBuilder sbCall = new StringBuilder(CALL_STM);
					sbCall.append(nomeObjeto)
					.append("(?,?,?,?,?,?)}");

					cs = connection.prepareCall(sbCall.toString());

					CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, itemSolicitacaoExame.getId().getSoeSeq());
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, itemSolicitacaoExame.getId().getSeqp());
					CoreUtil.configurarParametroCallableStatement(cs, 3, Types.VARCHAR, itemSolicitacaoExame.getExame().getSigla());
					CoreUtil.configurarParametroCallableStatement(cs, 4, Types.INTEGER, itemSolicitacaoExame.getMaterialAnalise().getSeq());
					CoreUtil.configurarParametroCallableStatement(cs, 5, Types.INTEGER, itemSolicitacaoExame.getUnidadeFuncional().getSeq());
					CoreUtil.configurarParametroCallableStatement(cs, 6, Types.VARCHAR, itemSolicitacaoExame.getSituacaoItemSolicitacao()
							.getCodigo());

					cs.execute();
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};
		
		try {
			this.logDebug("Executando por callableStatement AELK_ISE_RN.RN_ISEP_ATU_EXME_ABS");
			getAelAgrpPesquisasDAO().doWork(work);
		} catch (final Exception e) {
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			final String valores = CoreUtil.configurarValoresParametrosCallableStatement(itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getExame().getSigla(), itemSolicitacaoExame.getMaterialAnalise()
							.getSeq(), itemSolicitacaoExame.getUnidadeFuncional().getSeq(), itemSolicitacaoExame.getSituacaoItemSolicitacao()
							.getCodigo());
			this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores));
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		
		if (work.getException() != null){
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			final String valores = CoreUtil.configurarValoresParametrosCallableStatement(itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getExame().getSigla(), itemSolicitacaoExame.getMaterialAnalise()
							.getSeq(), itemSolicitacaoExame.getUnidadeFuncional().getSeq(), itemSolicitacaoExame.getSituacaoItemSolicitacao()
							.getCodigo());
			this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true, valores));
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), false, valores));
		}

		this.logDebug("Executou por callableStatement AELK_ISE_RN.RN_ISEP_ATU_EXME_ABS com sucesso!");
	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_atu_doa_abs. (RN17).
	 * 
	 * Gera dados na table abs_conclusoes_doacoes - ABS.
	 * @param usuarioLogado 
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void atualizarDoacaoAbs(final AelItemSolicitacaoExames itemSolicitacaoExame, String usuarioLogado) throws ApplicationBusinessException,
			ApplicationBusinessException {

		if (!isHCPA() || !aelExameApItemSolicDAO.isOracle()) {
			return;
		}

		final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AELK_ISE_RN_RN_ISEP_ATU_DOA_ABS.toString();
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection) throws SQLException {

				CallableStatement cs = null;
				try {
					final StringBuilder sbCall = new StringBuilder(CALL_STM);
					sbCall.append(nomeObjeto)
					.append("(?,?,?)}");

					cs = connection.prepareCall(sbCall.toString());

					CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, itemSolicitacaoExame.getId().getSoeSeq());
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, itemSolicitacaoExame.getId().getSeqp());
					CoreUtil.configurarParametroCallableStatement(cs, 3, Types.VARCHAR, itemSolicitacaoExame.getSituacaoItemSolicitacao()
							.getCodigo());

					cs.execute();
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};
		
		try {
			this.logDebug("Executando por callableStatement AELK_ISE_RN.RN_ISEP_ATU_DOA_ABS");
			getAelAgrpPesquisasDAO().doWork(work);
	
		} catch (final Exception e) {
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			final String valores = CoreUtil.configurarValoresParametrosCallableStatement(itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo());
			this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores));
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		if (work.getException() != null){
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(
							itemSolicitacaoExame.getId().getSoeSeq(),
							itemSolicitacaoExame.getId().getSeqp(),
							itemSolicitacaoExame.getSituacaoItemSolicitacao()
									.getCodigo());
			this.logError(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							work.getException(), true, valores));
			throw new ApplicationBusinessException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, work.getException(), false,
									valores));
		}
		this.logDebug("Executou por callableStatement AELK_ISE_RN.RN_ISEP_ATU_DOA_ABS com sucesso!");
		
	}

	/**
	 * RN18
	 * 
	 * ORADB FATP_INTERFACE_AEL
	 * @param servidorLogado 
	 * 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void realizarInterfaceFaturamento(final AelItemSolicitacaoExames itemSolicitacaoExame, final AelItemSolicitacaoExames itemOriginal, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor)
			throws BaseException {

		final VariaveisInterfaceFaturamento variaveis = new VariaveisInterfaceFaturamento();

		final AghParametros paramConv75 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_CONV_75);
		variaveis.setConv75(paramConv75.getVlrNumerico().shortValue());

		// RN18.1
		// Verifica código da situação
		final String codigoSituacaoNew = itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo();
		String codigoSituacaoOld = null;
		if (itemOriginal != null && itemOriginal.getSituacaoItemSolicitacao() != null) {
			codigoSituacaoOld = itemOriginal.getSituacaoItemSolicitacao().getCodigo();
		}

		if ((codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.AE.toString())
				|| codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.CA.toString())
				|| codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.AC.toString())
				|| codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.LI.toString()) || codigoSituacaoNew
				.equals(DominioSituacaoItemSolicitacaoExame.AX.toString()))
				&& CoreUtil.modificados(codigoSituacaoNew, codigoSituacaoOld)
				&& codigoSituacaoOld != null
				|| (codigoSituacaoOld == null && codigoSituacaoNew.equals(DominioSituacaoItemSolicitacaoExame.AE.toString()))) {

			// RN18.2 e RN18.3
			buscarDadosAtendimentoGerouExame(itemSolicitacaoExame, variaveis);

			// RN18.4
			// Se encontrou o código de atendimento
			if (variaveis.getAtendimento() != null) {
				// RN18.5, RN18.6 e RN18.7
				buscarDadosRealizacaoExame(itemSolicitacaoExame, variaveis);

				// RN18.8
				// Se novo código de situação do exame (sit_codigo) for
				// igual a ‘AE’
				if (itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AE.toString())
						&& variaveis.getTipoAtd() == DominioTipoPlano.I) {
					// RN18.9
					buscarDadosContaHospitalar(itemSolicitacaoExame, variaveis);

					// RN18.10
					// Se v_convenio_int for DIFERENTE de v_conv_75 E
					// v_ind_situacao for diferente de ‘C’
					if (!variaveis.getConvenioInt().equals(variaveis.getConv75()) && variaveis.getIndSituacao() != DominioSituacaoConta.C) {
						List<FatItemContaHospitalar> itensContaHospitalar = getFaturamentoFacade()
								.obterItensContaHospitalarPorContaHospitalarItemSolicitacaoExame(itemSolicitacaoExame.getId().getSeqp(),
										itemSolicitacaoExame.getId().getSoeSeq(), variaveis.getCthSeq());

						DominioSituacaoItenConta situacaoAtualizada;
						if (variaveis.getIndSituacao() == null) {
							situacaoAtualizada = DominioSituacaoItenConta.A;
						} else if (variaveis.getIndSituacao() == DominioSituacaoConta.A) {
							situacaoAtualizada = DominioSituacaoItenConta.A;
						} else if (variaveis.getIndSituacao() == DominioSituacaoConta.F) {
							situacaoAtualizada = DominioSituacaoItenConta.A;
						} else {
							situacaoAtualizada = DominioSituacaoItenConta.C;
						}

						for (final FatItemContaHospitalar itemContaHospitalar : itensContaHospitalar) {

							if (itemContaHospitalar.getIndSituacao() != situacaoAtualizada) {

								FatItemContaHospitalar itemContaHospitalarOld;

								try {

									itemContaHospitalarOld = getFaturamentoFacade().clonarItemContaHospitalar(itemContaHospitalar);
								} catch (final Exception e) {
									final BaseException mecEX = new ApplicationBusinessException(
											ItemSolicitacaoExameEnforceRNExceptionCode.ERRO_NA_INTEGRACAO_COM_FATURAMENTO);
									e.initCause(e);
									throw mecEX;
								}

								itemContaHospitalar.setIndSituacao(situacaoAtualizada);
								this.getFaturamentoFacade().persistirItemContaHospitalar(itemContaHospitalar, itemContaHospitalarOld, false, servidorLogado,dataFimVinculoServidor);
							}

						}

						// Se NÃO alterou nenhum registro então busca max da
						// seq na fat_itens_conta_hospitalar
						if (itensContaHospitalar.size() == 0) {

							// Executa o INSERT
							final FatItemContaHospitalarId id = new FatItemContaHospitalarId(variaveis.getCthSeq(), null);
							final FatItemContaHospitalar itemContaHospitalar = new FatItemContaHospitalar();
							itemContaHospitalar.setId(id);
							itemContaHospitalar.setIchType(DominioItemConsultoriaSumarios.ICH);
							itemContaHospitalar.setProcedimentoHospitalarInterno(variaveis.getProcedHospInterno());
							itemContaHospitalar.setValor(BigDecimal.ZERO);
							itemContaHospitalar.setIndSituacao(situacaoAtualizada);
							itemContaHospitalar.setIseSoeSeq(itemSolicitacaoExame.getId().getSoeSeq());
							itemContaHospitalar.setIseSeqp(itemSolicitacaoExame.getId().getSeqp());
							itemContaHospitalar.setQuantidadeRealizada(variaveis.getQtdRealizada() == null ? 1 : variaveis.getQtdRealizada());
							itemContaHospitalar.setIndOrigem(DominioIndOrigemItemContaHospitalar.AEL);
							itemContaHospitalar.setLocalCobranca(DominioLocalCobranca.I);
							itemContaHospitalar.setDthrRealizado(variaveis.getDthrSolic());
							itemContaHospitalar.setUnidadesFuncional(itemSolicitacaoExame.getUnidadeFuncional());
							itemContaHospitalar.setServidor(variaveis.getServidorResp());

							try {
								getFaturamentoFacade().persistirItemContaHospitalar(itemContaHospitalar, null, true,servidorLogado, dataFimVinculoServidor);
							} catch (final PersistenceException e) {
								logError("Exceção capturada: ", e);
								// Caso ao executar o INSERT acima ocorra
								// uma exceção de duplicação de índice único
								// (DUP_VAL_ON_INDEX)
								itensContaHospitalar = getFaturamentoFacade().obterItensContaHospitalarPorContaHospitalarItemSolicitacaoExame(
										itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getId().getSoeSeq(), variaveis.getCthSeq());

								for (final FatItemContaHospitalar itemContaHospitalarVar : itensContaHospitalar) {
									FatItemContaHospitalar itemContaHospitalarOld;

									try {
										itemContaHospitalarOld = getFaturamentoFacade().clonarItemContaHospitalar(itemContaHospitalarVar);
									} catch (final Exception e2) {
										final BaseException mecEX = new ApplicationBusinessException(
												ItemSolicitacaoExameEnforceRNExceptionCode.ERRO_NA_INTEGRACAO_COM_FATURAMENTO);
										e.initCause(e2);
										throw mecEX;
									}
									itemContaHospitalarVar.setIndSituacao(DominioSituacaoItenConta.A);
									this.getFaturamentoFacade().persistirItemContaHospitalar(itemContaHospitalarVar, itemContaHospitalarOld, false, servidorLogado,dataFimVinculoServidor);
								}
							}
						}

					}

					// RN18.11
					// TODO P_GERA_EMAIL (NÃO DEVE SER IMPLEMENTADO);
				} else {
					// RN18.12
					// Se novo código de situação do exame (sit_codigo) for
					// igual a ‘LI’ (LIBERADO) E v_tipo_atd for igual a ‘A’
					if (itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.LI.toString())
							&& variaveis.getTipoAtd() == DominioTipoPlano.A) {
						// RN18.13
						// try {
						this.atualizarAmbulatorio(itemSolicitacaoExame, variaveis, nomeMicrocomputador);
						// } catch (final InactiveModuleException e) {
						// logError(e.getMessage());
						// }

					} else {
						// RN18.14
						if (itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.LI.toString())
								&& variaveis.getTipoAtd() == DominioTipoPlano.I) {

							// RN18.9
							buscarDadosContaHospitalar(itemSolicitacaoExame, variaveis);

							// RN18.11
							// TODO P_GERA_EMAIL (NÃO DEVE SER
							// IMPLEMENTADO);

							// RN18.15
							// Se v_convenio_int for igual a v_conv_75 então
							// seta os parâmetros
							if (variaveis.getConvenioInt().equals(variaveis.getConv75())) {
								final FatConvenioSaudePlano convenioSaudePlanoNovo = getFaturamentoFacade().obterFatConvenioSaudePlano(
										variaveis.getConvenioInt(), variaveis.getPlanoInt());
								variaveis.setConvenioSaudePlano(convenioSaudePlanoNovo);
								// RN18.13
								// try {
								this.atualizarAmbulatorio(itemSolicitacaoExame, variaveis, nomeMicrocomputador);
								// } catch (final InactiveModuleException e) {
								// logError(e.getMessage());
								// }
							}
						} else {
							// RN18.16
							if ((itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()
									.equals(DominioSituacaoItemSolicitacaoExame.CA.toString())
									|| itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()
											.equals(DominioSituacaoItemSolicitacaoExame.AC.toString()) || itemSolicitacaoExame
									.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AX.toString()))
									&& variaveis.getTipoAtd() == DominioTipoPlano.A) {
								// RN18.17
								// try {
								this.cancelarAmbulatorio(itemSolicitacaoExame, nomeMicrocomputador, servidorLogado);
								// } catch (final InactiveModuleException e) {
								// logError(e.getMessage());
								// }
							} else {
								// RN18.18
								// Se novo código de situação do exame
								// (sit_codigo) for igual a ‘CA’, ‘AC’ OU
								// ‘AX’ E v_tipo_atd for igual a ‘I’
								if ((itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()
										.equals(DominioSituacaoItemSolicitacaoExame.CA.toString())
										|| itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()
												.equals(DominioSituacaoItemSolicitacaoExame.AC.toString()) || itemSolicitacaoExame
										.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AX.toString()))
										&& variaveis.getTipoAtd() == DominioTipoPlano.I) {
									// RN18.19
									cancelarInternacao(itemSolicitacaoExame, nomeMicrocomputador, servidorLogado);

									// RN18.11
									// TODO P_GERA_EMAIL (NÃO DEVE SER
									// IMPLEMENTADO);
								}
							}
						}
					}
				}

			} else {
				// RN18.20
				// Busca da atendimento diverso

				// Parâmetro do sistema Imuno
				final AghParametros paramUnfSeqImuno = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNF_SEQ_IMUNO);
				final Short unfAtv = paramUnfSeqImuno.getVlrNumerico().shortValue();

				// Aplica os filtros da consulta original
				if (itemSolicitacaoExame.getSolicitacaoExame().getUnidadeFuncional().getSeq().equals(unfAtv)
						&& itemSolicitacaoExame.getSolicitacaoExame().getConvenioSaude().getGrupoConvenio() == DominioGrupoConvenio.S) {
					// Atualiza variáveis
					variaveis.setPaciente(itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente());
					variaveis.setConvenioSaudePlano(itemSolicitacaoExame.getSolicitacaoExame().getConvenioSaudePlano());

					final AghParametros paramEspSeqImuno = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ESP_SEQ_IMUNO);
					final Short espSeqImuno = paramEspSeqImuno.getVlrNumerico().shortValue();

					final AghEspecialidades especialidadeImuno = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(espSeqImuno);
					variaveis.setEspecialidade(especialidadeImuno);

					// RN18.21
					buscarDadosRealizacaoExame(itemSolicitacaoExame, variaveis);

					// RN18.22
					if (itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.LI.toString())) {
						try {
							this.atualizarAmbulatorio(itemSolicitacaoExame, variaveis, nomeMicrocomputador);
						} catch (final InactiveModuleException e) {
							logError(e.getMessage());
						}
					} else if (itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()
							.equals(DominioSituacaoItemSolicitacaoExame.CA.toString())
							|| itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()
									.equals(DominioSituacaoItemSolicitacaoExame.AC.toString())
							|| itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()
									.equals(DominioSituacaoItemSolicitacaoExame.AX.toString())) {
						// RN18.23
						// try {
						this.cancelarAmbulatorio(itemSolicitacaoExame, nomeMicrocomputador, servidorLogado);
						// } catch (final InactiveModuleException e) {
						// logError(e.getMessage());
						// }
					}
				} else {
					variaveis.setPaciente(null);
					variaveis.setConvenioSaudePlano(null);
				}
			}
		}

	}

	
	/**
	 * RN18.2
	 * 
	 * Busca dados do atendimento que gerou o exame e atualiza variáveis de
	 * acordo
	 * 
	 */
	protected void buscarDadosAtendimentoGerouExame(final AelItemSolicitacaoExames itemSolicitacaoExame, final VariaveisInterfaceFaturamento variaveis) {
		// RN18.2
		// Busca dados do atendimento que gerou o exame
		final List<Object[]> dados = getAelItemSolicitacaoExameDAO().pesquisarDadosAtendimentoGerouExame(itemSolicitacaoExame.getId().getSoeSeq());
		if (dados != null && dados.size() != 0) {
			final FatConvenioSaudePlano convenioSaudePlano = (FatConvenioSaudePlano) dados.get(0)[0];
			final AelSolicitacaoExames solicitacao = (AelSolicitacaoExames) dados.get(0)[1];
			final AghAtendimentos atendimento = solicitacao.getAtendimento();

			// Alimenta variáveis após consulta
			variaveis.setAtendimento(atendimento);
			variaveis.setPaciente(atendimento.getPaciente());
			variaveis.setEspecialidade(atendimento.getEspecialidade());
			variaveis.setConvenioSaudePlano(convenioSaudePlano);
			variaveis.setInternacao(atendimento.getInternacao());
			variaveis.setDthrSolicitacao(solicitacao.getCriadoEm());
			DominioTipoPlano tipoAtd = convenioSaudePlano.getIndTipoPlano();
			if (tipoAtd == null) {
				tipoAtd = DominioTipoPlano.A;
			}
			variaveis.setTipoAtd(tipoAtd);
			variaveis.setOrigem(atendimento.getOrigem());
		} else {
			variaveis.setAtendimento(null);
			variaveis.setPaciente(null);
			variaveis.setConvenioSaudePlano(null);
			variaveis.setOrigem(null);
		}

		buscarDadosAtendimentoMae(variaveis);
	}

	/**
	 * RN18.3
	 * 
	 */
	protected void buscarDadosAtendimentoMae(final VariaveisInterfaceFaturamento variaveis) {
		// RN18.3
		// Verifica se atendimento de RN deve ser utilizado atendimento da mãe
		if (DominioOrigemAtendimento.N.equals(variaveis.getOrigem())) {
			final AghAtendimentos atendimentoMae = variaveis.getAtendimento().getAtendimentoMae();
			variaveis.setAtendimento(atendimentoMae);
			// Atualiza variáveis
			if (atendimentoMae != null) {
				variaveis.setPaciente(atendimentoMae.getPaciente());
				variaveis.setEspecialidade(atendimentoMae.getEspecialidade());
				variaveis.setInternacao(atendimentoMae.getInternacao());
				if(atendimentoMae.getOrigem().equals(DominioOrigemAtendimento.A)) {
					variaveis.setTipoAtd(DominioTipoPlano.A);
				}
				if(atendimentoMae.getOrigem().equals(DominioOrigemAtendimento.I)) {
					variaveis.setTipoAtd(DominioTipoPlano.I);
				}
			} else {
				variaveis.setPaciente(null);
				variaveis.setEspecialidade(null);
				variaveis.setInternacao(null);
				variaveis.setOrigem(null);
			}
		}
	}

	/**
	 * RN18.5, RN18.6 e RN18.7
	 * 
	 * ORADB P_DADOS_PARA_PMR
	 * 
	 */
	protected void buscarDadosRealizacaoExame(final AelItemSolicitacaoExames itemSolicitacaoExame, final VariaveisInterfaceFaturamento variaveis)
			throws ApplicationBusinessException {
		// RN18.5
		// Busca data da realização do exame
		final AelExtratoItemSolicitacao extratoItemSolicitacao = getAelExtratoItemSolicitacaoDAO().buscarPorItemSolicitacaoSituacao(
				itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp(),
				itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo());

		if (extratoItemSolicitacao == null) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_01094);
		}

		variaveis.setDthrEvento(extratoItemSolicitacao.getDataHoraEvento());
		variaveis.setServidor(extratoItemSolicitacao.getServidor());

		// RN18.6
		// Busca dados do exame
		variaveis.setExaSigla(itemSolicitacaoExame.getExame().getSigla());
		variaveis.setManSeq(itemSolicitacaoExame.getMaterialAnalise().getSeq());
		variaveis.setUnidadeFuncional(itemSolicitacaoExame.getUnidadeFuncional());

		if (itemSolicitacaoExame.getIntervaloColeta() != null) {
			variaveis.setQtdRealizada(itemSolicitacaoExame.getIntervaloColeta().getNroColetas());
		}

		variaveis.setServidor(itemSolicitacaoExame.getServidorResponsabilidade());
		variaveis.setDthrSolic(itemSolicitacaoExame.getSolicitacaoExame().getCriadoEm());

		// RN18.7
		// Busca o PHI do exame
		final FatProcedHospInternos phi = getFaturamentoFacade().obterFatProcedHospInternosPorExamesMaterialAnalise(
				itemSolicitacaoExame.getExameMaterialAnalise().getId());
		variaveis.setProcedHospInterno(phi);
	}

	/**
	 * RN18.9 Busca dados da conta hospitalar
	 * 
	 * ORADB P_BUSCA_CONTA
	 * 
	 */
	protected void buscarDadosContaHospitalar(final AelItemSolicitacaoExames itemSolicitacaoExame, final VariaveisInterfaceFaturamento variaveis)
			throws ApplicationBusinessException {
		final List<FatContasInternacao> contasInternacao = getFaturamentoFacade().obterContasHospitalaresPorInternacaoPacienteDataSolicitacao(
				variaveis.getIntSeq(), variaveis.getPacCodigo(), variaveis.getDthrSolicitacao(), variaveis.getConv75());

		if (contasInternacao == null || contasInternacao.size() == 0) {
			// RN18.9.1
			// Busca dados da conta hospitalar com exames especiais
			final List<FatContasInternacao> contasInternacaoExamesEspeciais = getFaturamentoFacade()
					.obterContasHospitalaresPorInternacaoPacienteDataSolicitacaoComExamesEspeciais(variaveis.getIntSeq(), variaveis.getPacCodigo(),
							variaveis.getDthrSolicitacao(), variaveis.getConv75());

			if (contasInternacaoExamesEspeciais == null || contasInternacaoExamesEspeciais.size() == 0) {
				// RN18.9.2
				buscarAtendimentoMae(variaveis);
			} else {
				final FatContasInternacao contaInternacaoExamesEspeciais = contasInternacaoExamesEspeciais.get(0);

				variaveis.setDtAltaAdmin(contaInternacaoExamesEspeciais.getContaHospitalar().getDtAltaAdministrativa());
				variaveis.setCthSeq(contaInternacaoExamesEspeciais.getContaHospitalar().getSeq());
				variaveis.setIndSituacao(contaInternacaoExamesEspeciais.getContaHospitalar().getIndSituacao());
				variaveis.setConvenioInt(contaInternacaoExamesEspeciais.getInternacao().getConvenioSaude().getCodigo());
				variaveis.setPlanoInt(contaInternacaoExamesEspeciais.getInternacao().getConvenioSaudePlano().getId().getSeq());

				variaveis.setDthrSolic(variaveis.getDtAltaAdmin());
			}
		} else {
			final FatContasInternacao contaInternacao = contasInternacao.get(0);
			variaveis.setCthSeq(contaInternacao.getContaHospitalar().getSeq());
			variaveis.setIndSituacao(contaInternacao.getContaHospitalar().getIndSituacao());
			variaveis.setConvenioInt(contaInternacao.getInternacao().getConvenioSaude().getCodigo());
			variaveis.setPlanoInt(contaInternacao.getInternacao().getConvenioSaudePlano().getId().getSeq());

			// RN18.9.4
			verificarConvenioESituacao(variaveis);
		}
	}

	/**
	 * RN18.9.2 Busca os dados do atendimento mãe
	 * 
	 */
	protected void buscarAtendimentoMae(final VariaveisInterfaceFaturamento variaveis) throws ApplicationBusinessException {
		// Se v_atd_seq_mae NÃO for NULO então v_atd_seq = v_atd_seq_mae e busca
		// atendimento da mãe
		if (variaveis.getAtendimento().getAtendimentoMae() != null) {
			final AghAtendimentos atendimentoMae = variaveis.getAtendimento().getAtendimentoMae();
			variaveis.setAtendimento(atendimentoMae);
			variaveis.setPaciente(atendimentoMae.getPaciente());
			variaveis.setEspecialidade(atendimentoMae.getEspecialidade());
			variaveis.setInternacao(atendimentoMae.getInternacao());
			if(atendimentoMae.getOrigem().equals(DominioOrigemAtendimento.A)) {
				variaveis.setTipoAtd(DominioTipoPlano.A);
			}
			if(atendimentoMae.getOrigem().equals(DominioOrigemAtendimento.I)) {
				variaveis.setTipoAtd(DominioTipoPlano.I);
			}

			// Busca dados da conta hospitalar
			final List<FatContasInternacao> contasInternacaoMae = getFaturamentoFacade().obterContasHospitalaresPorInternacaoPacienteDataSolicitacao(
					variaveis.getIntSeq(), variaveis.getPacCodigo(), variaveis.getDthrSolicitacao(), variaveis.getConv75());
			if (contasInternacaoMae == null || contasInternacaoMae.size() == 0) {
				throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.FAT_00012);

			} else {
				final FatContasInternacao contaInternacaoMae = contasInternacaoMae.get(0);
				variaveis.setDthrSolic(contaInternacaoMae.getContaHospitalar().getDtAltaAdministrativa());
				variaveis.setCthSeq(contaInternacaoMae.getContaHospitalar().getSeq());
				variaveis.setIndSituacao(contaInternacaoMae.getContaHospitalar().getIndSituacao());
				variaveis.setConvenioInt(contaInternacaoMae.getInternacao().getConvenioSaude().getCodigo());
				variaveis.setPlanoInt(contaInternacaoMae.getInternacao().getConvenioSaudePlano().getId().getSeq());
			}
		} else {
			// RN18.9.3
			throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.FAT_00012);
		}
	}

	/**
	 * RN18.9.4 Varifica o convênio e a situação das variáveis
	 * 
	 */
	protected void verificarConvenioESituacao(final VariaveisInterfaceFaturamento variaveis) throws ApplicationBusinessException {
		// Se v_convenio_int for DIFERENTE de v_conv_75 E v_ind_situacao for
		// igual a ‘C’
		if (!variaveis.getConvenioInt().equals(variaveis.getConv75()) && variaveis.getIndSituacao() == DominioSituacaoConta.C) {
			// RN18.9.3
			throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.FAT_00012);
		}
	}

	/**
	 * RN18.13
	 * 
	 * ORADB P_ATUALIZA_AMBULATORIO
	 * 
	 */
	private void atualizarAmbulatorio(final AelItemSolicitacaoExames itemSolicitacaoExame, final VariaveisInterfaceFaturamento variaveis, String nomeMicrocomputador)
			throws IllegalStateException, BaseException {
		AghParametros paramVGrupo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		Short vGrupo = paramVGrupo.getVlrNumerico().shortValue();
		
		this.validaCboProfissional(variaveis.getUnfSeq(), variaveis.getPhiSeq(), variaveis.getCspCnvCodigo(), variaveis.getCspSeq(), vGrupo, itemSolicitacaoExame, variaveis);
		
		final IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		final List<FatCompetencia> competencias = faturamentoFacade.obterCompetenciasPorModuloESituacoes(variaveis.getModuloNovo(),
				DominioSituacaoCompetencia.A, DominioSituacaoCompetencia.M);
		final Date dataFimVinculoServidor = new Date(); 

		if (competencias != null && competencias.size() != 0) {
			final FatCompetencia competencia = competencias.get(0);

			final Integer mesNovo = competencia.getId().getMes();
			final Integer anoNovo = competencia.getId().getAno();
			final Date dtHrInicioNovo = competencia.getId().getDtHrInicio();

			// Volta à situação de Ativo se o registro já existia dentro da
			// competência ativa
			List<FatProcedAmbRealizado> procedimentosAmbRealizados = faturamentoFacade
					.buscarFatProcedAmbRealizadoPorProcedHospInternosEItemSolicitacaoExame(itemSolicitacaoExame.getId().getSeqp(),
							itemSolicitacaoExame.getId().getSoeSeq(), variaveis.getPhiSeq());

			for (final FatProcedAmbRealizado procedimentosAmbRealizado : procedimentosAmbRealizados) {
				final FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(procedimentosAmbRealizado);

				procedimentosAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
				procedimentosAmbRealizado.setDthrRealizado(variaveis.getDthrEvento());
				procedimentosAmbRealizado.getFatCompetencia().getId().setDtHrInicio(dtHrInicioNovo);

				procedimentosAmbRealizado.getFatCompetencia().getId().setModulo(variaveis.getModuloNovo());
				procedimentosAmbRealizado.getFatCompetencia().getId().setMes(mesNovo);
				procedimentosAmbRealizado.getFatCompetencia().getId().setAno(anoNovo);

				faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(procedimentosAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
			}

			// Se NÃO alterou nenhum registro, insere na PMR
			if (procedimentosAmbRealizados.size() == 0) {
				// Verifica se já existe na pmr com a situação Transferida
				final boolean existePmrTransferida = faturamentoFacade.existeFatProcedAmbRealizadoTransferida(itemSolicitacaoExame.getId().getSeqp(),
						itemSolicitacaoExame.getId().getSoeSeq(), variaveis.getPhiSeq());

				if (!existePmrTransferida) {
					final FatProcedAmbRealizado procedAmbRealizado = new FatProcedAmbRealizado();
					procedAmbRealizado.setDthrRealizado(variaveis.getDthrEvento());
					procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
					procedAmbRealizado.setLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B);
					procedAmbRealizado.setQuantidade(variaveis.getQtdRealizada() == null ? 1 : variaveis.getQtdRealizada());
					procedAmbRealizado.setItemSolicitacaoExame(itemSolicitacaoExame);
					procedAmbRealizado.setProcedimentoHospitalarInterno(variaveis.getProcedHospInterno());
					procedAmbRealizado.setServidor(variaveis.getServidor());
					procedAmbRealizado.setUnidadeFuncional(variaveis.getUnidadeFuncional());
					procedAmbRealizado.setEspecialidade(variaveis.getEspecialidade());
					procedAmbRealizado.setPaciente(variaveis.getPaciente());
					procedAmbRealizado.setConvenioSaudePlano(variaveis.getConvenioSaudePlano());
					procedAmbRealizado.setIndOrigem(DominioOrigemProcedimentoAmbulatorialRealizado.EXA);
					procedAmbRealizado.setAtendimento(variaveis.getAtendimento());
					procedAmbRealizado.setServidorResponsavel(variaveis.getServidorResp());

					try {
						faturamentoFacade.inserirProcedimentosAmbulatoriaisRealizados(procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
					} catch (final PersistenceException e) {
						logError("Exceção capturada: ", e);
						// Caso ao executar o INSERT acima ocorra uma exceção de
						// duplicação de índice único (DUP_VAL_ON_INDEX)
						procedimentosAmbRealizados = faturamentoFacade.buscarFatProcedAmbRealizadoPorItemSolicitacaoExameNaoFaturados(
								itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getId().getSoeSeq());

						for (final FatProcedAmbRealizado procedimentosAmbRealizado : procedimentosAmbRealizados) {
							final FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(procedimentosAmbRealizado);

							procedimentosAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
							faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(procedimentosAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
						}
					}
				}

			}

		}
	}
	
	/**
	 * Método criado devido a alterações feitas no faturamento das contas de RN <br>
	 * migrado conforme AGH, para as desepesas de RN irem para a conta da Mãe
	 * e não do Ambulatório conforme funcionava antes da alteração.
	 * #40659
	 * @param unfSeq
	 * @param phiSeq
	 * @param cpgCphCspCnvCodigo
	 * @param cpgCphCspSeq
	 * @param cpgGrcSeq
	 * @param itemSolicitacao
	 * @param variaveis
	 * @throws AGHUNegocioException
	 */
	protected void validaCboProfissional(Short unfSeq, Integer phiSeq, Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq, Short cpgGrcSeq, 
			AelItemSolicitacaoExames itemSolicitacao, VariaveisInterfaceFaturamento variaveis) throws ApplicationBusinessException {
//	--AGHP_ENVIA_EMAIL('T','inicio','1 fatp_interface_ael','MPONS@HCPA.UFRGS.BR','correio;') ;
//	 -- verifica se profissional possui CBO --> Teti  07/11/2012
		
		Boolean achouCaracteristica = this.getAghuFacade().
				verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.SOLICITA_RESPONSAVEL);
		
		List<VFatAssociacaoProcedimento> listaProcedAssoc = this.getFaturamentoFacade().
				listarVFatAssociacaoProcedimento(phiSeq, cpgCphCspCnvCodigo, cpgCphCspSeq, cpgGrcSeq);
		
		if(listaProcedAssoc.size() > 0 && !achouCaracteristica){
			
			VFatAssociacaoProcedimento proced = listaProcedAssoc.get(0);
			
			RnCapcCboProcResVO retornoVO = this.getFaturamentoFacade().rnCapcCboProcRes(null, null, 
					itemSolicitacao.getId().getSoeSeq(), itemSolicitacao.getId().getSeqp(), null, null,	
					proced.getId().getIphPhoSeq(), proced.getId().getIphSeq(), 
					variaveis.getDthrEvento(), null, CoreUtil.obterUltimoDiaDoMes(new Date()));
			
			if(retornoVO == null){
//				'Erro na rotina de faturamento ao validar CBO do profissional - FATP_INTERFACE_AEL'
				throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.FAT_01252);
			} else if("NTC".equals(retornoVO.getpErro())){
//				'Não encontrou CBO do responsável pelo exame - FATP_INTERFACE_AEL'
				throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.FAT_01250);
			} else if("INC".equals(retornoVO.getpErro())){
//				'CBO profissional incompativel com CBO do procedimento - FATP_INTERFACE_AEL'
				throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.FAT_01251);
			}
		}
	}
	
	

	/**
	 * RN18.17
	 * 
	 * ORADB P_ATUALIZA_AMBULATORIO
	 * 
	 */
	private void cancelarAmbulatorio(final AelItemSolicitacaoExames itemSolicitacaoExame, String nomeMicrocomputador, RapServidores servidorLogado) throws BaseException {
		final IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		final Date dataFimVinculoServidor = new Date();
		try {
			final List<FatProcedAmbRealizado> procedimentosRealizados = faturamentoFacade.buscarFatProcedAmbRealizadoPorItemSolicitacaoExame(
					itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getId().getSoeSeq());

			for (final FatProcedAmbRealizado procedimentoRealizado : procedimentosRealizados) {
				final FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(procedimentoRealizado);

				procedimentoRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
				faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(procedimentoRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
			}

			if (procedimentosRealizados.size() == 0) {
				final List<FatItemContaHospitalar> itensContaHospitalar = faturamentoFacade.obterItensContaHospitalarPorSolicitacaoExame(
						itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getId().getSoeSeq());

				for (final FatItemContaHospitalar itemContaHospitalar : itensContaHospitalar) {
					final FatItemContaHospitalar itemContaHospitalarOld = faturamentoFacade.clonarItemContaHospitalar(itemContaHospitalar);
					itemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.C);
					faturamentoFacade.persistirItemContaHospitalar(itemContaHospitalar, itemContaHospitalarOld, false,servidorLogado, dataFimVinculoServidor);
				}
			}
		} catch (final Exception e) {
			final BaseException mecEX = new ApplicationBusinessException(
					ItemSolicitacaoExameEnforceRNExceptionCode.ERRO_NA_INTEGRACAO_COM_FATURAMENTO);
			e.initCause(e);
			throw mecEX;
		}
	}

	/**
	 * RN18.19
	 * 
	 * ORADB P_CANCELA_INTERNACAO
	 * 
	 * @throws BaseException
	 * 
	 */
	private void cancelarInternacao(final AelItemSolicitacaoExames itemSolicitacaoExame, String nomeMicrocomputador,RapServidores servidorLogado) throws BaseException {
		final IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		final Date dataFimVinculoServidor = new Date();
		try {
			final List<FatItemContaHospitalar> itensContaHospitalar = faturamentoFacade.obterItensContaHospitalarPorSolicitacaoExame(
					itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getId().getSoeSeq());

			for (final FatItemContaHospitalar itemContaHospitalar : itensContaHospitalar) {
				final FatItemContaHospitalar itemContaHospitalarOld = faturamentoFacade.clonarItemContaHospitalar(itemContaHospitalar);
				itemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.C);
				faturamentoFacade.persistirItemContaHospitalar(itemContaHospitalar, itemContaHospitalarOld, false, servidorLogado,dataFimVinculoServidor);
			}

			if (itensContaHospitalar.size() == 0) {
				final List<FatProcedAmbRealizado> procedimentosRealizados = faturamentoFacade.buscarFatProcedAmbRealizadoPorItemSolicitacaoExame(
						itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getId().getSoeSeq());

				for (final FatProcedAmbRealizado procedimentoRealizado : procedimentosRealizados) {
					final FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(procedimentoRealizado);

					procedimentoRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
					faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(procedimentoRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		} catch (final Exception e) {
			final BaseException mecEX = new ApplicationBusinessException(
					ItemSolicitacaoExameEnforceRNExceptionCode.ERRO_NA_INTEGRACAO_COM_FATURAMENTO);
			e.initCause(e);
			throw mecEX;
		}
	}

	/**
	 * Verifica se o usuário responsável foi modificado. Se sim, retorna
	 * verdadeiro, senão falso. (RN19)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @return {boolean}
	 * @throws ApplicationBusinessException
	 */
	protected boolean verificarResponsavelModificado(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws ApplicationBusinessException {

		boolean response = false;

		if (CoreUtil.modificados(itemSolicitacaoExame.getServidorResponsabilidade(), itemSolicitacaoExameOriginal.getServidorResponsabilidade())) {

			response = true;

		}

		return response;

	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_atu_extr_ser (RN20).
	 * 
	 * Atualizar o extrato item solicitacao.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 */
	private void atualizarExtratoServidor(final AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {
		// Se unidade executora apresentar caracteristica de 'Area Fechada'
		if (getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(itemSolicitacaoExame.getUnidadeFuncional().getSeq(),
				ConstanteAghCaractUnidFuncionais.AREA_FECHADA)) {
			final Short seqp = buscarMaiorSeqpExtratoItemSolicitacao(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());
			if (seqp != null) {
				// Atualiza tabela ael_extrato_item_solics
				final AelExtratoItemSolicitacao extrato = getAelExtratoItemSolicitacaoDAO().obterPorId(itemSolicitacaoExame.getId().getSoeSeq(),
						itemSolicitacaoExame.getId().getSeqp(), seqp);
				if (extrato != null) {
					extrato.setServidorEhResponsabilide(itemSolicitacaoExame.getServidorResponsabilidade());
				}
			}
		}
	}

	/**
	 * Verifica se a descrição do material de analise foi modificada. Se sim,
	 * retorna verdadeiro, senão falso. (RN21)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @return {boolean}
	 * @throws BaseException
	 */
	protected boolean verificarDescricaoMaterialAnalise(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws BaseException {

		boolean response = false;

		if (CoreUtil.modificados(itemSolicitacaoExame.getDescMaterialAnalise(), itemSolicitacaoExameOriginal.getDescMaterialAnalise())) {

			response = true;

		}

		return response;

	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_ver_provas (RN22)
	 * 
	 * Verifica se o exame solicitado já não foi solicitado na mesma
	 * solicitação, exceto os que possuem amostras
	 * (ind_gera_item_por_amostra='s' e desc_material_analise diferentes).
	 * Acusar também erro caso tenha alguma prova deste exame solicitado.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws BaseException
	 */
	private void verificarProvas(final AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {

		if (this.verificarAtendimentoEProjetoPesquisaNulo(itemSolicitacaoExame)) {

			final List<AelItemSolicitacaoExames> itensSolic = getAelItemSolicitacaoExameDAO().pesquisarTodosItensDeSolicitacaoDiferentesDeItem(
					itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());

			for (final AelItemSolicitacaoExames itemSolicitacaoExameVar : itensSolic) {
				if (this.verificarCalculoProvas(itemSolicitacaoExame, itemSolicitacaoExameVar)) {
					// RN22.6
					// Se a dthr_programada do exame (item) estiver entre as
					// datas calculadas
					verificarDataHoraEmIntervalos(itemSolicitacaoExame, itemSolicitacaoExameVar);
				}
				// RN22.7
				// Verifica se existe alguma prova do exame já solicitado
				verificarProvasExameJaSolicitado(itemSolicitacaoExame, itemSolicitacaoExameVar);
			}
		}
	}

	/**
	 * Verifica se deve calcular provas ou não.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameVar
	 * @return {Boolean}
	 */
	protected Boolean verificarCalculoProvas(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameVar) {
		
		Boolean response = false;

		// #50102
		// 2- Erro ao pedir mesmo exame 2 vezes;
//		if (itemSolicitacaoExame.getExame().equals(itemSolicitacaoExameVar.getExame())){
//			return response;
//		}
		
		// RN22.3
		// Se ufe_ema_exa_sigla e ufe_ema_man_seq do item forem iguais a
		// ufe_ema_exa_sigla e ufe_ema_man_seq da solicitação
		if (!CoreUtil.modificados(itemSolicitacaoExame.getExame(), itemSolicitacaoExameVar.getExame())
				&& !CoreUtil.modificados(itemSolicitacaoExame.getMaterialAnalise(), itemSolicitacaoExameVar.getMaterialAnalise())) {
			// RN22.4
			// Se (ind_gera_item_por_coletas for igual a 'N' E ind_dependente
			// for igual a 'N') OU (ind_gera_item_por_coletas = 'S' E
			// ind_dependente = 'N' E o novo valor de ind_gerado_automatico for
			// igual a 'N'
			if ((!itemSolicitacaoExameVar.getExameMaterialAnalise().getIndGeraItemPorColetas() && !itemSolicitacaoExameVar.getExameMaterialAnalise()
					.getIndDependente())
					|| (itemSolicitacaoExameVar.getExameMaterialAnalise().getIndGeraItemPorColetas()
							&& !itemSolicitacaoExameVar.getExameMaterialAnalise().getIndDependente()
							&& !itemSolicitacaoExameVar.getIndGeradoAutomatico() && !itemSolicitacaoExame.getIndGeradoAutomatico())) {
				// RN22.5
				// Se desc_material_analise do item e o novo
				// desc_material_analise for nulo OU forem iguais
				if ((itemSolicitacaoExameVar.getDescMaterialAnalise() == null && itemSolicitacaoExame.getDescMaterialAnalise() == null)
						|| itemSolicitacaoExameVar.getDescMaterialAnalise().equals(itemSolicitacaoExame.getDescMaterialAnalise())) {

					response = true;

				}

			}

		}

		return response;

	}

	/**
	 * Se o atendimento e o projeto de pesquisa da solicitação forem NULOS então
	 * deve retornar falso. Senão verdadeiro.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @return {Boolean}
	 */
	protected Boolean verificarAtendimentoEProjetoPesquisaNulo(final AelItemSolicitacaoExames itemSolicitacaoExame) {

		Boolean response = true;

		if (itemSolicitacaoExame.getSolicitacaoExame().getAtendimento() == null
				&& itemSolicitacaoExame.getSolicitacaoExame().getProjetoPesquisa() == null) {

			response = false;

		}

		return response;

	}

	protected void verificarDataHoraEmIntervalos(final AelItemSolicitacaoExames itemSolicitacaoExame, final AelItemSolicitacaoExames itemEmAnalise)
			throws ApplicationBusinessException {
		final AghParametros paramTemposMinutosSolic = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TEMPO_MINUTOS_SOLIC);
		final int valorTemposMinutosSolic = paramTemposMinutosSolic.getVlrNumerico().intValue();
		final Date dthrProgramadaInicio = DateUtil.adicionaMinutos(itemSolicitacaoExame.getDthrProgramada(), -valorTemposMinutosSolic);
		final Date dthrProgramadaFim = DateUtil.adicionaMinutos(itemSolicitacaoExame.getDthrProgramada(), valorTemposMinutosSolic);

		if (!itemEmAnalise.getDthrProgramada().before(dthrProgramadaInicio) && !itemEmAnalise.getDthrProgramada().after(dthrProgramadaFim)) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_00501, this.getDescricaoFormatadaExameJaSolicitado(itemSolicitacaoExame));
		}
	}

	protected void verificarProvasExameJaSolicitado(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameVar) throws BaseException {
		final List<AelExamesProva> provas = getAelExamesProvaDAO().buscarProvasExameSolicitado(itemSolicitacaoExame.getExame().getSigla(),
				itemSolicitacaoExame.getMaterialAnalise().getSeq());

		boolean exameJaSolicitadoViaProva = false;

		for (int i = 0; i < provas.size(); i++) {
			final AelExamesProva prova = provas.get(i);

			if (prova.getId().getEmaExaSiglaEhProva().equals(itemSolicitacaoExameVar.getExame().getSigla())
					&& prova.getId().getEmaManSeqEhProva().equals(itemSolicitacaoExameVar.getMaterialAnalise().getSeq())) {
				exameJaSolicitadoViaProva = true;
			}
		}

		if (exameJaSolicitadoViaProva) {
			throw new BaseException(ItemSolicitacaoExameEnforceRNExceptionCode.AEL_00502, this.getDescricaoFormatadaExameJaSolicitado(itemSolicitacaoExame));
		}
	}
	
	/**
	 * Obtem a descrição formatada para exames já solicitados
	 * 
	 * @param itemSolicitacaoExame
	 * @return
	 */
	private String getDescricaoFormatadaExameJaSolicitado(final AelItemSolicitacaoExames itemSolicitacaoExame) {

		String nomeExame = itemSolicitacaoExame.getExame() != null ? itemSolicitacaoExame.getExame().getDescricaoUsual() : null;
		String materialAnalise = itemSolicitacaoExame.getMaterialAnalise() != null ? itemSolicitacaoExame.getMaterialAnalise().getDescricao() : null;
		String unidadeFuncional = itemSolicitacaoExame.getUnidadeFuncional() != null ? itemSolicitacaoExame.getUnidadeFuncional().getDescricao() : null;

		return new UnfExecutaSinonimoExameVO().getDescricaoExameFormatada(nomeExame, materialAnalise, unidadeFuncional);
	}

	/**
	 * Verifica se a prioridade foi alterada (RN23)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @return {boolean}
	 */
	protected boolean verificaAlteracaoPrioridade(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) {

		boolean response = false;

		if (CoreUtil.modificados(itemSolicitacaoExame.getPrioridadeExecucao(), itemSolicitacaoExameOriginal.getPrioridadeExecucao())) {

			response = true;

		}

		return response;

	}

	/**
	 * ORADB AELK_ISE_RN.RN_ISEP_VER_PRIORIDA (RN24)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void verificarPrioridade(final AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException,
			ApplicationBusinessException {

		if (!isHCPA() || !aelExameApItemSolicDAO.isOracle()) {
			return;
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AELK_ISE_RN_RN_ISEP_VER_PRIORIDA.toString();
		
		AghWork work = new AghWork(servidorLogado.getUsuario()) {
			@Override
			public void executeAghProcedure(final Connection connection) throws SQLException {

				CallableStatement cs = null;
				try {
					final StringBuilder sbCall = new StringBuilder(CALL_STM);
					sbCall.append(nomeObjeto)
					.append("(?,?,?)}");

					cs = connection.prepareCall(sbCall.toString());

					CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, itemSolicitacaoExame.getId().getSoeSeq());
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, itemSolicitacaoExame.getId().getSeqp());
					CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, itemSolicitacaoExame.getPrioridadeExecucao());

					cs.execute();
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};
		
		try {
			this.logDebug("Executando por callableStatement AELK_ISE_RN_RN_ISEP_VER_PRIORIDA");
			getAelAgrpPesquisasDAO().doWork(work);
			
		} catch (final Exception e) {
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			final String valores = CoreUtil.configurarValoresParametrosCallableStatement(itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getPrioridadeExecucao());
			this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores));
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		
		if (work.getException() != null){
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			final String valores = CoreUtil.configurarValoresParametrosCallableStatement(itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp(), itemSolicitacaoExame.getPrioridadeExecucao());
			this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true, valores));
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), false, valores));
		}

		this.logDebug("Executou por callableStatement AELK_ISE_RN.RN_ISEP_ATU_REFLEXO com sucesso!");
	}

	/**
	 * Se o número de amostras (nro_amostras) ou intervalo de dias
	 * (intervalo_dias) ou intervalo de horas (intervalo_horas) forem alterados
	 * (RN25).
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @throws ApplicationBusinessException
	 */
	protected boolean verificarAlteracaoAmostrasIntervalos(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws ApplicationBusinessException {

		boolean response = false;

		if (CoreUtil.modificados(itemSolicitacaoExame.getNroAmostras(), itemSolicitacaoExameOriginal.getNroAmostras())
				|| CoreUtil.modificados(itemSolicitacaoExame.getIntervaloDias(), itemSolicitacaoExameOriginal.getIntervaloDias())
				|| CoreUtil.modificados(itemSolicitacaoExame.getIntervaloHoras(), itemSolicitacaoExameOriginal.getIntervaloHoras())) {

			response = true;

		}

		return response;

	}

	/**
	 * ORADB AELK_ISE_RN.RN_ISEP_ATU_UPD_AMOS (RN26)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws BaseException
	 */
	protected void atualizarItensAmostra(final AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {

		final List<AelAmostraItemExames> listAmostraItemExames = itemSolicitacaoExame.getAelAmostraItemExames();
		for (final AelAmostraItemExames amostraItemExames : listAmostraItemExames) {

			final List<AelAmostraItemExames> listAmostrasItemExamesRestante = this.getAelAmostraItemExamesDAO()
					.buscarAelAmostraItemExamesPorAmostraPorItemSolicitacaoExame(amostraItemExames.getId().getIseSoeSeq(),
							amostraItemExames.getId().getIseSeqp(), amostraItemExames.getId().getAmoSoeSeq(), amostraItemExames.getId().getAmoSeqp());
			if (listAmostrasItemExamesRestante.size() > 0) {
				this.getExamesFacade().removerAmostraItemExame(amostraItemExames);
			} else {
				this.getExamesFacade().removerAmostraItemExame(amostraItemExames);

				final AelAmostras amostra = this.getAelAmostrasDAO().buscarAmostrasPorId(amostraItemExames.getId().getAmoSoeSeq(),
						amostraItemExames.getId().getAmoSeqp().shortValue());
				if (amostra != null) {

					this.getExamesFacade().removerAmostra(amostra);
				}

			}
		}

	}

	/**
	 * ORADB AELK_ISE_RN.RN_ISEP_ATU_AMOSTRAS (RN27)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected void atualizarAmostras(final AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {

		// Calcula range de solicitacao
		final List<Date> range = this.calcularRangeSolicitacao(itemSolicitacaoExame);
		Date v_dthr_programada_inicio = range.get(0);
		Date v_dthr_programada_fim = range.get(1);

		ColetaUnidadeMedidaTempoVO unidMedidaTempoColeta = new ColetaUnidadeMedidaTempoVO(0, null, null);

		// Logica para quando NAO for gerado automatico
		if (!itemSolicitacaoExame.getIndGeradoAutomatico() && itemSolicitacaoExame.getNroAmostras() != null) {
			unidMedidaTempoColeta = this.getColetaUnidadeMedidaTempoVOPorNroAmostra(itemSolicitacaoExame);
		}

		// Logica quando possuir intervalo coleta.
		if (itemSolicitacaoExame.getIntervaloColeta() != null) {
			unidMedidaTempoColeta = this.getColetaUnidadeMedidaTempoVOPorIntervaloColeta(itemSolicitacaoExame);

			// Adiciona os valores nas datas de inicio e fim do range.
			if (DominioUnidadeMedidaTempo.D == unidMedidaTempoColeta.getUnidadeMedidaTempo()) {
				v_dthr_programada_inicio = DateUtil.adicionaDias(v_dthr_programada_inicio, unidMedidaTempoColeta.getTempoColeta().intValue());
				v_dthr_programada_fim = DateUtil.adicionaDias(v_dthr_programada_fim, unidMedidaTempoColeta.getTempoColeta().intValue());
			} else if (DominioUnidadeMedidaTempo.H == unidMedidaTempoColeta.getUnidadeMedidaTempo()) {
				v_dthr_programada_inicio = DateUtil.adicionaHoras(v_dthr_programada_inicio, unidMedidaTempoColeta.getTempoColeta().intValue());
				v_dthr_programada_fim = DateUtil.adicionaHoras(v_dthr_programada_fim, unidMedidaTempoColeta.getTempoColeta().intValue());
			} else { // DominioUnidadeMedidaTempo.M == v_unid_medida_tempo
				v_dthr_programada_inicio = DateUtil.adicionaMinutos(v_dthr_programada_inicio, unidMedidaTempoColeta.getTempoColeta().intValue());
				v_dthr_programada_fim = DateUtil.adicionaMinutos(v_dthr_programada_fim, unidMedidaTempoColeta.getTempoColeta().intValue());
			}
		}

		// Busca Origem atendimento
		DominioOrigemAtendimento origemAtendimento = null;
		if (itemSolicitacaoExame.getSolicitacaoExame().getAtendimento() != null) {
			origemAtendimento = itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getOrigem();
		} else {
			if (itemSolicitacaoExame.getSolicitacaoExame().getAtendimentoDiverso() == null) {
				origemAtendimento = DominioOrigemAtendimento.X;
			} else {
				origemAtendimento = DominioOrigemAtendimento.D;
			}
		}

		final List<AelAmostras> amostras = getAelAmostrasDAO().buscarAmostrasPorSolicitacaoExame(itemSolicitacaoExame.getSolicitacaoExame());
		final List<AelTipoAmostraExame> tipoAmostras = getAelTipoAmostraExameDAO().buscarListaAelTipoAmostraExame(itemSolicitacaoExame.getExame(),
				itemSolicitacaoExame.getMaterialAnalise(), origemAtendimento);
		if (amostras != null && !amostras.isEmpty()) {
			for (final AelTipoAmostraExame aelTipoAmostraExame : tipoAmostras) {
				this.alterarAmostra(aelTipoAmostraExame, itemSolicitacaoExame, unidMedidaTempoColeta, amostras, v_dthr_programada_inicio,
						v_dthr_programada_fim);
			}
		} else {
			for (final AelTipoAmostraExame aelTipoAmostraExame : tipoAmostras) {
				this.inserirAmostra(aelTipoAmostraExame, itemSolicitacaoExame, unidMedidaTempoColeta.getNroColetasEncontrada(), amostras);
			}
		}

	}// atualizarAmostras

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void alterarAmostra(final AelTipoAmostraExame tipoAmostraExame, final AelItemSolicitacaoExames itemSolicitacaoExame,
			final ColetaUnidadeMedidaTempoVO unidMedidaTempoColeta, final List<AelAmostras> amostras, Date rangeInicio, Date rangeFim)
			throws BaseException {
		Integer numColetasSobraram = unidMedidaTempoColeta.getNroColetasEncontrada();
		boolean encontrouAmostra = false;

		final List<AelTmpIntervaloColeta> tmpIntervaloColetas = this.getTmpIntervaloColeta(itemSolicitacaoExame);
		final DominioSituacaoAmostra situacaoAmostraItem = this.getSituacaoAmostra(itemSolicitacaoExame);

		AelTmpIntervaloColeta tmpIntervaloColeta;
		Short tempo = unidMedidaTempoColeta.getTempoColeta() != null ? unidMedidaTempoColeta.getTempoColeta().shortValue() : 0;
		DominioUnidadeMedidaTempo unidadeMedidaTempo = null;

		for (final AelAmostras amostra : amostras) {
			if (amostra.getUnidadesFuncionais() != null
					&& itemSolicitacaoExame.getAelUnfExecutaExames() != null
					&& amostra.getUnidadesFuncionais().equals(itemSolicitacaoExame.getAelUnfExecutaExames().getUnidadeFuncional())
					&& CoreUtil.igual(tipoAmostraExame.getMaterialAnalise(), amostra.getMateriaisAnalises())
					&& CoreUtil.igual(tipoAmostraExame.getRecipienteColeta(), amostra.getRecipienteColeta())
					&& ((tipoAmostraExame.getAnticoagulante() == null && amostra.getAnticoagulante() == null) || CoreUtil.igual(
							tipoAmostraExame.getAnticoagulante(), amostra.getAnticoagulante()))) {
				int intervaloColetaIndex = 0;

				if ((DateValidator.validaDataMenor(amostra.getDthrPrevistaColeta(), itemSolicitacaoExame.getDthrProgramada())
						|| DateValidator.validaDataMenor(amostra.getDthrPrevistaColeta(), rangeInicio) || DateUtil.validaDataMaior(
						amostra.getDthrPrevistaColeta(), rangeFim))
						&& !(DateUtil.validaDataMaiorIgual(amostra.getDthrPrevistaColeta(), rangeInicio) || DateValidator.validaDataMenorIgual(
								amostra.getDthrPrevistaColeta(), rangeFim)) && (!tmpIntervaloColetas.isEmpty())) {
					for (; intervaloColetaIndex < tmpIntervaloColetas.size(); intervaloColetaIndex++) {
						tmpIntervaloColeta = tmpIntervaloColetas.get(intervaloColetaIndex);
						if (DateValidator.validaDataMenor(amostra.getDthrPrevistaColeta(), rangeInicio)
								|| DateUtil.validaDataMaior(amostra.getDthrPrevistaColeta(), rangeFim)) {
							final List<Date> range = this.calcularRangeSolicitacao(itemSolicitacaoExame);
							rangeInicio = range.get(0);
							rangeFim = range.get(1);

							tempo = tmpIntervaloColeta.getTempo();
							unidadeMedidaTempo = tmpIntervaloColeta.getIntervaloColeta().getUnidMedidaTempo();
							rangeInicio = this.adicionaTempoColeta(rangeInicio, unidadeMedidaTempo, tempo.doubleValue());
							rangeFim = this.adicionaTempoColeta(rangeFim, tmpIntervaloColeta.getIntervaloColeta().getUnidMedidaTempo(),
									tempo.doubleValue());
						} else {
							final List<Date> range = this.calcularRangeSolicitacao(itemSolicitacaoExame);
							rangeInicio = range.get(0);
							rangeFim = range.get(1);

							tempo = tmpIntervaloColeta.getTempo();
							unidadeMedidaTempo = tmpIntervaloColeta.getIntervaloColeta().getUnidMedidaTempo();
							rangeInicio = this.adicionaTempoColeta(rangeInicio, unidadeMedidaTempo, tempo.doubleValue());
							rangeFim = this.adicionaTempoColeta(rangeFim, tmpIntervaloColeta.getIntervaloColeta().getUnidMedidaTempo(),
									tempo.doubleValue());

							break;
						}
					}// FOR tmpIntervaloColeta
				}

				final Short amostraTempo = amostra.getTempoIntervaloColeta().shortValue();
				while (amostraTempo.shortValue() > tempo.shortValue() && intervaloColetaIndex < (tmpIntervaloColetas.size() - 1)) {
					intervaloColetaIndex = intervaloColetaIndex + 1;
					tmpIntervaloColeta = tmpIntervaloColetas.get(intervaloColetaIndex);
					tempo = tmpIntervaloColeta.getTempo();
					unidadeMedidaTempo = tmpIntervaloColeta.getIntervaloColeta().getUnidMedidaTempo();
				}

				if ((DateUtil.validaDataMaiorIgual(amostra.getDthrPrevistaColeta(), rangeInicio) && DateValidator.validaDataMenorIgual(
						amostra.getDthrPrevistaColeta(), rangeFim))
						&& situacaoAmostraItem == amostra.getSituacao() && !tipoAmostraExame.getMaterialAnalise().getIndExigeDescMatAnls()) {
					if(itemSolicitacaoExame.getIndGeradoAutomatico() 
							&& itemSolicitacaoExame.getNroAmostras() != null 
							&& itemSolicitacaoExame.getNroAmostras() > 1) {
						encontrouAmostra = false;
					} else {
						if (itemSolicitacaoExame.getIntervaloColeta() != null) {
							if (amostraTempo.shortValue() == tempo.shortValue()) {
								this.inserirItemAmostra(itemSolicitacaoExame, amostra);
								numColetasSobraram = numColetasSobraram - 1;
								encontrouAmostra = true;
							}
						} else {
							this.atualizarAmostras(amostra, itemSolicitacaoExame, tempo, unidadeMedidaTempo, situacaoAmostraItem);
							this.inserirItemAmostra(itemSolicitacaoExame, amostra);
							numColetasSobraram = numColetasSobraram - 1;
							encontrouAmostra = true;
						}
					}
					if (itemSolicitacaoExame.getIntervaloColeta() != null && numColetasSobraram.intValue() > 0) {
						if (intervaloColetaIndex < (tmpIntervaloColetas.size() - 1)) {
							intervaloColetaIndex = intervaloColetaIndex + 1;
							tmpIntervaloColeta = tmpIntervaloColetas.get(intervaloColetaIndex);

							final List<Date> range = this.calcularRangeSolicitacao(itemSolicitacaoExame);
							rangeInicio = range.get(0);
							rangeFim = range.get(1);

							tempo = tmpIntervaloColeta.getTempo();
							unidadeMedidaTempo = tmpIntervaloColeta.getIntervaloColeta().getUnidMedidaTempo();
							rangeInicio = this.adicionaTempoColeta(rangeInicio, unidadeMedidaTempo, tempo.doubleValue());
							rangeFim = this.adicionaTempoColeta(rangeFim, tmpIntervaloColeta.getIntervaloColeta().getUnidMedidaTempo(),
									tempo.doubleValue());
						}
					} else {
						rangeInicio = this.adicionaTempoColeta(rangeInicio, unidMedidaTempoColeta.getUnidadeMedidaTempo(),
								unidMedidaTempoColeta.getTempoColeta());
						rangeFim = this.adicionaTempoColeta(rangeFim, unidMedidaTempoColeta.getUnidadeMedidaTempo(),
								unidMedidaTempoColeta.getTempoColeta());
					}
				}
			}// IF
		}// FOR Amostras

		if (!encontrouAmostra) {
			this.inserirAmostra(tipoAmostraExame, itemSolicitacaoExame, numColetasSobraram, null);
		} else if (numColetasSobraram.intValue() > 0) {
			this.inserirAmostra(tipoAmostraExame, itemSolicitacaoExame, numColetasSobraram, amostras);
		}

	}

	private void atualizarAmostras(final AelAmostras amostra, final AelItemSolicitacaoExames itemSolicitacaoExame, final Short tempo,
			final DominioUnidadeMedidaTempo unidadeMedidaTempo, final DominioSituacaoAmostra situacaoAmostraItem) throws BaseException {
		if (!itemSolicitacaoExame.getIndGeradoAutomatico() && itemSolicitacaoExame.getIntervaloColeta() != null) {
			final AelAmostras amostraEncontrada = this.getAelAmostrasDAO().buscarAmostrasPor(itemSolicitacaoExame.getSolicitacaoExame(),
					itemSolicitacaoExame.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelMateriaisAnalises(),
					itemSolicitacaoExame.getAelUnfExecutaExames().getUnidadeFuncional(), amostra.getRecipienteColeta(), amostra.getAnticoagulante(),
					amostra.getDthrPrevistaColeta(), tempo, situacaoAmostraItem);
			if (amostraEncontrada != null && amostraEncontrada.getUnidTempoIntervaloColeta() == null) {
				amostraEncontrada.setUnidTempoIntervaloColeta(unidadeMedidaTempo);
				this.getExamesFacade().atualizarAmostra(amostraEncontrada, false);
			}
		}// IF indgeradoautomatico
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void inserirAmostra(final AelTipoAmostraExame aelTipoAmostraExame, final AelItemSolicitacaoExames itemSolicitacaoExame,
			final Integer v_nro_coletas_encontrada, final List<AelAmostras> amostrasDaSolicitacao) throws BaseException {
		ColetaUnidadeMedidaTempoVO unidMedidaTempoColeta = null;

		if (!itemSolicitacaoExame.getIndGeradoAutomatico()) { // NAO eh gerado
																// automatico.
			if (itemSolicitacaoExame.getNroAmostras() != null) {
				unidMedidaTempoColeta = this.getColetaUnidadeMedidaTempoVOPorNroAmostra(itemSolicitacaoExame);
			} else if (itemSolicitacaoExame.getIntervaloColeta() != null) {
				unidMedidaTempoColeta = this.getColetaUnidadeMedidaTempoVOPorIntervaloColeta(itemSolicitacaoExame);

			} else {
				unidMedidaTempoColeta = new ColetaUnidadeMedidaTempoVO(((aelTipoAmostraExame.getNroAmostras() != null) ? aelTipoAmostraExame
						.getNroAmostras().intValue() : 0), null, 0d);
			}
		} else { // eh gerado automatico;
			if (itemSolicitacaoExame.getIntervaloDias() != null) {
				unidMedidaTempoColeta = new ColetaUnidadeMedidaTempoVO(1, DominioUnidadeMedidaTempo.D, itemSolicitacaoExame.getIntervaloDias()
						.doubleValue());

			} else if (itemSolicitacaoExame.getIntervaloHoras() != null) {
				unidMedidaTempoColeta = new ColetaUnidadeMedidaTempoVO(1, DominioUnidadeMedidaTempo.H,
						this.calcularIntervaloHoras(itemSolicitacaoExame.getIntervaloHoras()));
			} else {
				unidMedidaTempoColeta = new ColetaUnidadeMedidaTempoVO(((aelTipoAmostraExame.getNroAmostras() != null) ? aelTipoAmostraExame
						.getNroAmostras().intValue() : 0), null, 0d);
			}
		}

		Integer diferencaColetas = 0;
		if (v_nro_coletas_encontrada != null && v_nro_coletas_encontrada.intValue() > 0) {
			diferencaColetas = unidMedidaTempoColeta.getNroColetasEncontrada() - v_nro_coletas_encontrada;
			// NroColetasEncontrada = NroColetasEncontrada - diferencaColetas
			unidMedidaTempoColeta.setNroColetasEncontrada(unidMedidaTempoColeta.getNroColetasEncontrada() - diferencaColetas);
		}

		Date v_dthr_programada = itemSolicitacaoExame.getDthrProgramada();
		Double v_tempo_coletas_amostra = unidMedidaTempoColeta.getTempoColeta();
		int incremento = 0;
		while (incremento < unidMedidaTempoColeta.getNroColetasEncontrada()) {
			incremento = incremento + 1;

			DominioSituacaoAmostra situacao = this.getSituacaoAmostra(itemSolicitacaoExame);
			AelMateriaisAnalises man = aelTipoAmostraExame.getMaterialAnalise();
			AelRecipienteColeta rco = aelTipoAmostraExame.getRecipienteColeta();
			final AelAnticoagulante antiCoagulante = aelTipoAmostraExame.getAnticoagulante();

			if (itemSolicitacaoExame.getIntervaloColeta() != null) {
				List<AelTmpIntervaloColeta> tmpIntervaloColetas = null;
				tmpIntervaloColetas = getAelTmpIntervaloColetaDAO().listarPorIntervaloColeta(itemSolicitacaoExame.getIntervaloColeta().getSeq());

				if (amostrasDaSolicitacao != null && !amostrasDaSolicitacao.isEmpty()) {
					for (final AelTmpIntervaloColeta tmpIntervaloColeta : tmpIntervaloColetas) {
						for (final AelAmostras amostra : amostrasDaSolicitacao) {
							v_tempo_coletas_amostra = tmpIntervaloColeta.getTempo().doubleValue();
							v_dthr_programada = this.adicionaTempoColeta(itemSolicitacaoExame.getDthrProgramada(), tmpIntervaloColeta
									.getIntervaloColeta().getUnidMedidaTempo(), v_tempo_coletas_amostra);

							final List<Date> rangePrevisto = this.calcularRangeDatas(amostra.getDthrPrevistaColeta(), null);
							final Date dthrPrevistaInicio = rangePrevisto.get(0);
							final Date dthrPrevistaFinal = rangePrevisto.get(1);

							if (DateUtil.validaDataMaiorIgual(v_dthr_programada, dthrPrevistaInicio)
									&& DateValidator.validaDataMenorIgual(v_dthr_programada, dthrPrevistaFinal)) {
								if (v_tempo_coletas_amostra != null
										&& new BigDecimal(v_tempo_coletas_amostra).compareTo(amostra.getTempoIntervaloColeta()) == 0) {
									if (amostra.getUnidadesFuncionais() != null
											&& amostra.getUnidadesFuncionais().equals(
													itemSolicitacaoExame.getAelUnfExecutaExames().getUnidadeFuncional())) {
										man = amostra.getMateriaisAnalises();
										rco = amostra.getRecipienteColeta();
										situacao = amostra.getSituacao();
									}// IF unidades funcionais
									else {
										amostra.getAnticoagulante();
									}
								}// IF tempoAmostra = tempoIntervaloColeta
								else {
									amostra.getAnticoagulante();
								}
							}// IF Range de Datas prevista

						}// FOR AelAmostras
					}// FOR AelTmpIntervaloColeta

				} else {
					final AelTmpIntervaloColeta til = tmpIntervaloColetas.get((incremento - 1));
					v_tempo_coletas_amostra = til.getTempo().doubleValue();

					v_dthr_programada = this.adicionaTempoColeta(itemSolicitacaoExame.getDthrProgramada(), til.getIntervaloColeta()
							.getUnidMedidaTempo(), v_tempo_coletas_amostra);
				}
			} else {
				// Variavel representa o tempo sempre em dias
				// E v_tempo_aux representa o tempo conforme a
				// unidademedidatempo.
				final Double tempoColetaCalc = unidMedidaTempoColeta.getTempoColeta() * diferencaColetas;
				v_dthr_programada = this.adicionaTempoColeta(v_dthr_programada, unidMedidaTempoColeta.getUnidadeMedidaTempo(), tempoColetaCalc);
				v_tempo_coletas_amostra = v_tempo_coletas_amostra + tempoColetaCalc;
			}

			final AelAmostras amostra = new AelAmostras();
			amostra.setSolicitacaoExame(itemSolicitacaoExame.getSolicitacaoExame());
			amostra.setUnidadesFuncionais(itemSolicitacaoExame.getUnidadeFuncional());
			amostra.setMateriaisAnalises(man);
			amostra.setRecipienteColeta(rco);
			amostra.setSituacao(situacao);
			amostra.setAnticoagulante(antiCoagulante);
			amostra.setDthrPrevistaColeta(v_dthr_programada);
			amostra.setTempoIntervaloColeta(new BigDecimal(v_tempo_coletas_amostra));
			amostra.setUnidTempoIntervaloColeta(unidMedidaTempoColeta.getUnidadeMedidaTempo());
			amostra.setServidor(null);
			amostra.setDthrEntrada(null);

			this.getExamesFacade().inserirAmostra(amostra);

			// Caso necessario,
			// Insere automaticamente o numero da solicitacao e o numero da
			// amostra na tabela - AEL_SEQ_CODBARRAS_REDOME.
			this.criarSeqCodBarraRedome(itemSolicitacaoExame, amostra);

			// Insersao do item desta amostra.
			this.inserirItemAmostra(itemSolicitacaoExame, amostra);

		}

	}

	private void inserirItemAmostra(final AelItemSolicitacaoExames itemSolicitacaoExame, final AelAmostras amostra) throws BaseException {
		final DominioProgramacaoExecExames vProgramacao = this.calcularProgramacao(itemSolicitacaoExame.getTipoColeta(),
				itemSolicitacaoExame.getSolicitacaoExame());

		AelEquipamentos equipamento = null;
		final AelExecExamesMatAnalise execExamesMatAnalise = getAelExecExamesMatAnaliseDAO()
				.buscarAelExecExamesMatAnaliseComEquipamentoPorAelItemSolicitacaoExame(itemSolicitacaoExame, vProgramacao);
		if (execExamesMatAnalise != null) {
			equipamento = execExamesMatAnalise.getAelEquipamentos();
		}

		final AelAmostraItemExames itemAmostra = new AelAmostraItemExames();
		itemAmostra.setAelAmostras(amostra);
		itemAmostra.setAelItemSolicitacaoExames(itemSolicitacaoExame);
		itemAmostra.setSituacao(amostra.getSituacao());
		itemAmostra.setAelEquipamentos(equipamento);

		itemSolicitacaoExame.getAelAmostraItemExames().add(itemAmostra);

		this.getExamesFacade().inserirAmostraItemExame(itemAmostra);

	}

	private ColetaUnidadeMedidaTempoVO getColetaUnidadeMedidaTempoVOPorIntervaloColeta(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		ColetaUnidadeMedidaTempoVO returnValue = null;

		Integer v_nro_coletas_encontrada = null;
		DominioUnidadeMedidaTempo v_unid_medida_tempo = null;
		Double v_tempo_col = null;

		// Logica quando possuir intervalo coleta.
		final List<AelTmpIntervaloColeta> tmpIntervaloColetas = getTmpIntervaloColeta(itemSolicitacaoExame);
		if (!tmpIntervaloColetas.isEmpty()) {
			// Valores default.
			v_nro_coletas_encontrada = 1;
			v_tempo_col = 0d;
			v_unid_medida_tempo = DominioUnidadeMedidaTempo.D;

			// Se encontrou intervalo coleta seta os
			// valores do primeiro item da lista.
			if (!tmpIntervaloColetas.isEmpty()) {
				final AelTmpIntervaloColeta tmpICO = tmpIntervaloColetas.get(0);

				v_nro_coletas_encontrada = tmpIntervaloColetas.size();
				v_tempo_col = tmpICO.getTempo().doubleValue();
				v_unid_medida_tempo = tmpICO.getIntervaloColeta().getUnidMedidaTempo();
			}
			returnValue = new ColetaUnidadeMedidaTempoVO(v_nro_coletas_encontrada, v_unid_medida_tempo, v_tempo_col);
		}

		return returnValue;
	}

	private List<AelTmpIntervaloColeta> getTmpIntervaloColeta(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		List<AelTmpIntervaloColeta> tmpIntervaloColetas = null;
		if (itemSolicitacaoExame.getIntervaloColeta() != null && itemSolicitacaoExame.getIntervaloColeta().getSeq() != null) {
			tmpIntervaloColetas = getAelTmpIntervaloColetaDAO().listarPorIntervaloColeta(itemSolicitacaoExame.getIntervaloColeta().getSeq());
		} else {
			tmpIntervaloColetas = new LinkedList<AelTmpIntervaloColeta>();
		}
		return tmpIntervaloColetas;
	}

	private ColetaUnidadeMedidaTempoVO getColetaUnidadeMedidaTempoVOPorNroAmostra(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		ColetaUnidadeMedidaTempoVO returnValue = null;

		Integer v_nro_coletas_encontrada = null;
		DominioUnidadeMedidaTempo v_unid_medida_tempo = null;
		Double v_tempo_col = null;

		if (itemSolicitacaoExame.getNroAmostras() != null) {
			v_nro_coletas_encontrada = itemSolicitacaoExame.getNroAmostras().intValue();
			if (itemSolicitacaoExame.getAelUnfExecutaExames() != null
					&& itemSolicitacaoExame.getAelUnfExecutaExames().getAelExamesMaterialAnalise() != null) {
				final AelExamesMaterialAnalise ema = itemSolicitacaoExame.getAelUnfExecutaExames().getAelExamesMaterialAnalise();
				if (ema.getIndGeraItemPorColetas() && ema.getIndSolicInformaColetas()) {
					v_nro_coletas_encontrada = 1;
				}
			}

			if (itemSolicitacaoExame.getNroAmostras() == 1){
				v_tempo_col = new Double(0);
				v_unid_medida_tempo = DominioUnidadeMedidaTempo.H;
			} else {
				if (itemSolicitacaoExame.getIntervaloDias() != null) {
					v_tempo_col = itemSolicitacaoExame.getIntervaloDias().doubleValue();
					v_unid_medida_tempo = DominioUnidadeMedidaTempo.D;
				} else if (itemSolicitacaoExame.getIntervaloHoras() != null) {
					v_tempo_col = this.calcularIntervaloHoras(itemSolicitacaoExame.getIntervaloHoras());
					v_unid_medida_tempo = DominioUnidadeMedidaTempo.H;
				}
			}

			returnValue = new ColetaUnidadeMedidaTempoVO(v_nro_coletas_encontrada, v_unid_medida_tempo, v_tempo_col);
		}

		return returnValue;
	}

	protected class ColetaUnidadeMedidaTempoVO {
		private Integer nroColetasEncontrada = null;
		private DominioUnidadeMedidaTempo unidadeMedidaTempo = null;
		private Double tempoColeta = null;

		public ColetaUnidadeMedidaTempoVO(final Integer v_nro_coletas_encontrada, final DominioUnidadeMedidaTempo v_unid_medida_tempo,
				final Double v_tempo_col) {
			super();
			this.nroColetasEncontrada = v_nro_coletas_encontrada;
			this.unidadeMedidaTempo = v_unid_medida_tempo;
			this.tempoColeta = v_tempo_col;
		}

		/**
		 * @param nroColetasEncontrada
		 *            the nroColetasEncontrada to set
		 */
		public void setNroColetasEncontrada(final Integer n) {
			this.nroColetasEncontrada = n;
		}

		/**
		 * @return the nroColetasEncontrada
		 */
		public Integer getNroColetasEncontrada() {
			return nroColetasEncontrada;
		}

		/**
		 * @param unidadeMedidaTempo
		 *            the unidadeMedidaTempo to set
		 */
		public void setUnidadeMedidaTempo(final DominioUnidadeMedidaTempo un) {
			this.unidadeMedidaTempo = un;
		}

		/**
		 * @return the unidadeMedidaTempo
		 */
		public DominioUnidadeMedidaTempo getUnidadeMedidaTempo() {
			return unidadeMedidaTempo;
		}

		/**
		 * @param tempoColeta
		 *            the tempoColeta to set
		 */
		public void setTempoColeta(final Double t) {
			this.tempoColeta = t;
		}

		/**
		 * @return the tempoColeta
		 */
		public Double getTempoColeta() {
			return tempoColeta;
		}

	}

	protected DominioProgramacaoExecExames calcularProgramacao(final DominioTipoColeta tipoColeta, final AelSolicitacaoExames solEx) {
		DominioProgramacaoExecExames vProgramacao = null;

		if (DominioTipoColeta.U.equals(tipoColeta)) {

			vProgramacao = DominioProgramacaoExecExames.U;

		} else {

			vProgramacao = DominioProgramacaoExecExames.R;

		}

		if (vProgramacao.equals(DominioProgramacaoExecExames.R) && solEx != null && solEx.getUnidadeFuncional() != null) {

			final Boolean automacao = getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(solEx.getUnidadeFuncional().getSeq(),
					ConstanteAghCaractUnidFuncionais.AUTOMACAO_ROTINA);
			if (!automacao) {
				vProgramacao = DominioProgramacaoExecExames.U;
			}
		}

		return vProgramacao;
	}

	/**
	 * Cria um novo <b>AelSeqCodbarraRedome</b> quando um item solicitacao exame
	 * tiver a sua unidade funcional de execucao do exame igual IMUNO e possuir
	 * um agrupador pesquisa exame.<br>
	 * 
	 * @param itemSolicitacaoExame
	 * @param amostra
	 * @throws BaseException
	 */
	protected void criarSeqCodBarraRedome(final AelItemSolicitacaoExames itemSolicitacaoExame, final AelAmostras amostra) throws BaseException {
		if (itemSolicitacaoExame != null && itemSolicitacaoExame.getUnidadeFuncional() != null && amostra != null) {
			final BigDecimal paramUnidadeImuno = this.buscaParametroSistemaValorNumerico(AghuParametrosEnum.P_UNID_IMUNO);
			Short unidFuncImuno = null;
			if (paramUnidadeImuno != null) {
				unidFuncImuno = paramUnidadeImuno.shortValue();
			}

			final Short ufeUnidFunc = itemSolicitacaoExame.getUnidadeFuncional().getSeq();
			if (ufeUnidFunc != null && ufeUnidFunc.equals(unidFuncImuno)) {
				final AelAgrpPesquisas agrpPesquisa = getAelAgrpPesquisasDAO().obterAelAgrpPesquisasPorDescricao(AelAgrpPesquisas.REDOME);

				final List<AelAgrpPesquisaXExame> lista = getAelAgrpPesquisaXExameDAO().buscarAelAgrpPesquisaXExame(itemSolicitacaoExame.getExame(),
						itemSolicitacaoExame.getMaterialAnalise(), itemSolicitacaoExame.getUnidadeFuncional(), agrpPesquisa, null);
				if (lista != null && !lista.isEmpty()) {
					this.inserirAelSeqCodbarraRedome(amostra);
				}
			}
		}
	}

	protected void inserirAelSeqCodbarraRedome(final AelAmostras amostra) throws BaseException {
		final AelSeqCodbarraRedome codBarra = new AelSeqCodbarraRedome();

		codBarra.setAmostra(amostra);
		this.getAelSeqCodbarraRedomeRN().inserir(codBarra);
	}

	/**
	 * Adiciona na data programada o tempocoleta conforme a unidade de medida de
	 * tempo.<br>
	 * 
	 * @param dthrProgramada
	 * @param unidMedidaTempo
	 * @param tempoColeta
	 * @return
	 */
	protected Date adicionaTempoColeta(final Date dthrProgramada, final DominioUnidadeMedidaTempo unidMedidaTempo, final Double tempoColeta) {
		Date returnValue = dthrProgramada;

		if (dthrProgramada != null && unidMedidaTempo != null && tempoColeta != null) {
			// infelizmente vai perder precisao.
			if (DominioUnidadeMedidaTempo.D == unidMedidaTempo) {
				returnValue = DateUtil.adicionaDias(dthrProgramada, tempoColeta.intValue());
			} else if (DominioUnidadeMedidaTempo.H == unidMedidaTempo) {
				returnValue = DateUtil.adicionaHoras(dthrProgramada, tempoColeta.intValue());
			} else { // DominioUnidadeMedidaTempo.M == v_unid_medida_tempo
				returnValue = DateUtil.adicionaMinutos(dthrProgramada, tempoColeta.intValue());
			}
		}

		return returnValue;
	}

	/**
	 * if p_new_sit_codigo = v_na_area_executora then v_situacao_amostra := 'R';
	 * elsif p_new_sit_codigo = v_coletado_solic then v_situacao_amostra := 'C';
	 * elsif p_new_sit_codigo = v_agendado then v_situacao_amostra := 'G'; elsif
	 * p_new_sit_codigo = v_a_coletar then v_situacao_amostra := 'G'; else
	 * v_situacao_amostra := 'G'; end if;
	 * 
	 * @param itemSolicitacaoExame
	 * @return
	 */
	protected DominioSituacaoAmostra getSituacaoAmostra(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		DominioSituacaoAmostra returnValue = null;

		if (itemSolicitacaoExame.getSituacaoItemSolicitacao() != null) {
			if (DominioSituacaoItemSolicitacaoExame.AE.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {
				// AE //AREA EXECUTORA
				returnValue = DominioSituacaoAmostra.R;
			} else if (DominioSituacaoItemSolicitacaoExame.CS.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {
				// CS //COLETADO PELO SOLICITANTE
				returnValue = DominioSituacaoAmostra.C;
			} else if (DominioSituacaoItemSolicitacaoExame.AG.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {
				// AG //AGENDADO
				returnValue = DominioSituacaoAmostra.G;
			} else if (DominioSituacaoItemSolicitacaoExame.AC.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {
				// AC //A COLETAR
				returnValue = DominioSituacaoAmostra.G;
			} else {
				returnValue = DominioSituacaoAmostra.G;
			}
		}

		return returnValue;
	}

	/**
	 * Transforma a data em intervalor de horas.
	 * 
	 * @param d
	 * @return
	 */
	protected Double calcularIntervaloHoras(final Date d) {
		Double v_tempo_col = null;

		if (d != null) {
			final Integer horas = DateUtil.getHoras(d);
			final BigDecimal minutos = new BigDecimal(DateUtil.getMinutos(d));
			final BigDecimal minEmHrs = minutos.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);

			v_tempo_col = horas + minEmHrs.doubleValue();
		}

		return v_tempo_col;
	}

	/**
	 * Calcular range de Solicitacao.<br>
	 * Se nao existir P_TEMPO_MINUTOS_SOLIC usar o valor 60.<br>
	 * inicio do range recebe a soma de P_TEMPO_MINUTOS_SOLIC na data
	 * programada.<br>
	 * fim do range recebe a subtracao de P_TEMPO_MINUTOS_SOLIC na data
	 * programada.<br>
	 * Retorna lista vazia caso nao tenha data programada.<br>
	 * 
	 * @param itemSolicitacaoExame
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected List<Date> calcularRangeSolicitacao(final AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {
		if (itemSolicitacaoExame != null && itemSolicitacaoExame.getDthrProgramada() != null) {
			int paramTempoMinutosSolicitacao = 60;
			final BigDecimal pTempoMinutoSolic = this.buscaParametroSistemaValorNumerico(AghuParametrosEnum.P_TEMPO_MINUTOS_SOLIC);
			if (pTempoMinutoSolic != null) {
				paramTempoMinutosSolicitacao = pTempoMinutoSolic.intValue();
			}
			final Date v_dthr_programada_inicio = DateUtil.adicionaMinutos(itemSolicitacaoExame.getDthrProgramada(),
					(paramTempoMinutosSolicitacao * -1));
			final Date v_dthr_programada_fim = DateUtil.adicionaMinutos(itemSolicitacaoExame.getDthrProgramada(), paramTempoMinutosSolicitacao);

			return Arrays.asList(v_dthr_programada_inicio, v_dthr_programada_fim);
		} else {
			return new LinkedList<Date>();
		}
	}

	private List<Date> calcularRangeDatas(Date dataInicio, Date dataFim) throws ApplicationBusinessException {
		if (dataInicio == null) {
			dataInicio = new Date();
		}
		if (dataFim == null) {
			dataFim = dataInicio;
		}
		int paramTempoMinutosSolicitacao = 60;
		final BigDecimal pTempoMinutoSolic = this.buscaParametroSistemaValorNumerico(AghuParametrosEnum.P_TEMPO_MINUTOS_SOLIC);
		if (pTempoMinutoSolic != null) {
			paramTempoMinutosSolicitacao = pTempoMinutoSolic.intValue();
		}
		final Date v_dthr_programada_inicio = DateUtil.adicionaMinutos(dataInicio, (paramTempoMinutosSolicitacao * -1));
		final Date v_dthr_programada_fim = DateUtil.adicionaMinutos(dataFim, paramTempoMinutosSolicitacao);

		return Arrays.asList(v_dthr_programada_inicio, v_dthr_programada_fim);
	}

	/**
	 * Busca parametro sistema. Utiliza o campo valor numerico.
	 * 
	 * @param enumParam
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected BigDecimal buscaParametroSistemaValorNumerico(final AghuParametrosEnum enumParam) throws ApplicationBusinessException {
		final AghParametros param = getParametroFacade().buscarAghParametro(enumParam);
		if (param != null) {
			return param.getVlrNumerico();
		}
		return null;
	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_atu_nro_unic (RN28)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	protected void atualizarNumeroUnico(final AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {

		final String codigoSituacao = itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo();

		// Verifica se unidade que executa o exame deve gerar número ou não
		// (característica)
		if ((!DominioSimNao.N.equals(this.getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
				itemSolicitacaoExame.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.GERA_NRO_UNICO)))
				&& (DominioSituacaoItemSolicitacaoExame.AC.toString().equals(codigoSituacao)
						|| DominioSituacaoItemSolicitacaoExame.CS.toString().equals(codigoSituacao)
						|| DominioSituacaoItemSolicitacaoExame.EC.toString().equals(codigoSituacao)
						|| DominioSituacaoItemSolicitacaoExame.CO.toString().equals(codigoSituacao)
						|| DominioSituacaoItemSolicitacaoExame.AE.toString().equals(codigoSituacao)
						|| DominioSituacaoItemSolicitacaoExame.AG.toString().equals(codigoSituacao) || DominioSituacaoItemSolicitacaoExame.RE
						.toString().equals(codigoSituacao))) {

			boolean atdDiverso = false;
			DominioOrigemAtendimento origem = null;
			DominioOrigemAtendimento origemNumeroUnico = null;

			final AghAtendimentos atendimento = itemSolicitacaoExame.getSolicitacaoExame().getAtendimento();
			if (atendimento == null) {
				atdDiverso = true;
				origemNumeroUnico = DominioOrigemAtendimento.A;
			} else {
				origem = atendimento.getOrigem();
			}

			// Determina se é de origem Internação ou Ambulatório para geração
			// do número único
			if (origemNumeroUnico == null) {

				origemNumeroUnico = DominioOrigemAtendimento.A;
				if ((DominioSimNao.N.equals(this.getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
						atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.AUTOMACAO_ROTINA)))
						|| (DominioSimNao.S.equals(this.getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
								atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.AUTOMACAO_ROTINA)) && DominioTipoColeta.U
								.equals(itemSolicitacaoExame.getTipoColeta()))) {

					origemNumeroUnico = DominioOrigemAtendimento.I;

				}

			}

			// Situação A COLETAR e COLETADO PELO SOLIC para essa situação só
			// gera número único para unidade solicitante
			// de convênios ou solicitação de origens Externa ou atendimento
			// diverso.
			if (this.verificarSituacaoExame(codigoSituacao, atendimento, origem, atdDiverso)) {

				// Verifica as amostras do item para atualização do nro único
				final List<AelAmostraItemExames> listAmostrasItemExames = itemSolicitacaoExame.getAelAmostraItemExames();
				for (final AelAmostraItemExames amostraItemExame : listAmostrasItemExames) {

					Date dataNumeroUnico = null;
					boolean atualizaNumeroUnico = false;
					Integer numeroUnico = amostraItemExame.getAelAmostras().getNroUnico();
					final Date dthrUnico = amostraItemExame.getAelAmostras().getDtNumeroUnico();

					if (DominioSituacaoItemSolicitacaoExame.AC.toString().equals(codigoSituacao)
							|| DominioSituacaoItemSolicitacaoExame.CS.toString().equals(codigoSituacao)) {

						if (numeroUnico != null) {

							if (!DateUtil
									.isDatasIguais(DateUtil.truncaData(itemSolicitacaoExame.getDthrProgramada()), DateUtil.truncaData(dthrUnico))) {

								dataNumeroUnico = DateUtil.truncaData(itemSolicitacaoExame.getDthrProgramada());
								atualizaNumeroUnico = true;

							}

						} else {

							dataNumeroUnico = DateUtil.truncaData(itemSolicitacaoExame.getDthrProgramada());
							atualizaNumeroUnico = true;

						}

					}

					if (DominioSituacaoItemSolicitacaoExame.CO.toString().equals(codigoSituacao)
							|| DominioSituacaoItemSolicitacaoExame.EC.toString().equals(codigoSituacao)
							|| DominioSituacaoItemSolicitacaoExame.RE.toString().equals(codigoSituacao)
							|| DominioSituacaoItemSolicitacaoExame.AE.toString().equals(codigoSituacao)) {

						if (numeroUnico != null) {

							if (!DateUtil.isDatasIguais(DateUtil.truncaData(new Date()), DateUtil.truncaData(dthrUnico))) {

								dataNumeroUnico = DateUtil.truncaData(new Date());
								atualizaNumeroUnico = true;

							}

						} else {

							dataNumeroUnico = DateUtil.truncaData(new Date());
							atualizaNumeroUnico = true;

						}

					}

					if (DominioSituacaoItemSolicitacaoExame.AG.toString().equals(codigoSituacao)) {

						List<AelItemHorarioAgendado> listItemHorarioAgendado = this.getAelItemHorarioAgendadoDAO().obterPorItemSolicitacaoExame(
								itemSolicitacaoExame);
						if(listItemHorarioAgendado != null) {
							final AelItemHorarioAgendado itemHorarioAgendado = listItemHorarioAgendado.get(0);
							final Date dthrAgenda = itemHorarioAgendado.getHorarioExameDisp().getId().getDthrAgenda();
							
							if (numeroUnico != null) {
								
								if (!DateUtil.isDatasIguais(DateUtil.truncaData(dthrAgenda), DateUtil.truncaData(dthrUnico))) {
									
									dataNumeroUnico = DateUtil.truncaData(dthrAgenda);
									atualizaNumeroUnico = true;
									
								}
								
							} else {
								
								dataNumeroUnico = DateUtil.truncaData(dthrAgenda);
								atualizaNumeroUnico = true;
								
							}
						}
					}

					// Verifica se há ou não necessidade de atualização de nro
					// único
					if (atualizaNumeroUnico) {

						final AghParametros parametroNroUnicoInicialInt = getParametroFacade().buscarAghParametro(
								AghuParametrosEnum.P_NRO_UNICO_INICIAL_INT);
						final Short valorNroUnicoInicialInt = parametroNroUnicoInicialInt.getVlrNumerico().shortValue();

						final AghParametros parametroNroUnicoInicialAmb = getParametroFacade().buscarAghParametro(
								AghuParametrosEnum.P_NRO_UNICO_INICIAL_AMB);
						final Short valorNroUnicoInicialAmb = parametroNroUnicoInicialAmb.getVlrNumerico().shortValue();

						Integer numeroUnicoSolic = null;
						Date dataNumeroUnicoSolic = null;
						final List<AelAmostras> listAmostras = this.getAelAmostrasDAO().buscarAmostrasPorDthrNumeroUnico(
								DateUtil.truncaData(dataNumeroUnico), itemSolicitacaoExame.getSolicitacaoExame().getSeq());

						for (final AelAmostras amostra : listAmostras) {

							numeroUnicoSolic = amostra.getNroUnico();
							dataNumeroUnicoSolic = amostra.getDtNumeroUnico();
							break;

						}

						boolean usaMesmoNumeroUnico = false;
						if (numeroUnicoSolic != null) {

							if (DateUtil.isDatasIguais(DateUtil.truncaData(dataNumeroUnico), DateUtil.truncaData(dataNumeroUnicoSolic))) {

								numeroUnico = numeroUnicoSolic;
								usaMesmoNumeroUnico = true;

							}

						}

						// Busca número único para atualização na amostras
						// para casos em que a solicitação ainda não tenha
						// recebida
						AelControleNumeroUnico controleNumeroUnicoUp = null;
						if (!usaMesmoNumeroUnico) {

							final List<AelControleNumeroUnico> listControleNumeroUnico = this.getAelControleNumeroUnicoDAO()
									.obterPorDataNumeroUnicoEOrigem(dataNumeroUnico, origemNumeroUnico);

							for (final AelControleNumeroUnico controleNumeroUnico : listControleNumeroUnico) {

								numeroUnico = controleNumeroUnico.getUltNumeroUnico();
								controleNumeroUnicoUp = controleNumeroUnico;
								break;

							}

							// Acrescenta numeração única conforme a origem
							if (numeroUnico == null) {

								numeroUnico = 0;
								if (DominioOrigemAtendimento.I.equals(origemNumeroUnico)) {

									numeroUnico = valorNroUnicoInicialInt + 1;

								} else {

									numeroUnico = valorNroUnicoInicialAmb + 1;

								}

							} else {

								if (DominioOrigemAtendimento.I.equals(origemNumeroUnico)) {

									if (numeroUnico < valorNroUnicoInicialInt) {

										numeroUnico = valorNroUnicoInicialInt + 1;

									} else {

										numeroUnico = numeroUnico + 1;

									}

								} else {

									if (numeroUnico < valorNroUnicoInicialAmb) {

										numeroUnico = valorNroUnicoInicialAmb + 1;

									} else {

										numeroUnico = numeroUnico + 1;

									}

								}

							}

						}

						// Atualiza a amostra com o número único gerado
						if (numeroUnico != null) {

							AelAmostras amostra = aelAmostrasDAO.obterAelAmostras(amostraItemExame.getAelAmostras().getSolicitacaoExame().getSeq(), amostraItemExame.getAelAmostras().getId().getSeqp());
							amostra.setNroUnico(numeroUnico);
							amostra.setDtNumeroUnico(dataNumeroUnico);

							this.getExamesFacade().atualizarAmostra(amostra, true);

						}

						// Atualiza o último número único gerado na tabela de
						// controle (header)
						// Só no caso de novo número único
						if (!usaMesmoNumeroUnico && numeroUnico != null) {

							if (controleNumeroUnicoUp == null) {
								controleNumeroUnicoUp = new AelControleNumeroUnico();
							}

							controleNumeroUnicoUp.setUltNumeroUnico(numeroUnico);
							controleNumeroUnicoUp.setDtNumeroUnico(dataNumeroUnico);
							controleNumeroUnicoUp.setOrigem(origemNumeroUnico);
							controleNumeroUnicoUp = this.getExamesFacade().atualizarControleNumeroUnicoUp(controleNumeroUnicoUp);
							
						}

					}

				}

			}

		}

	}

	/**
	 * Situação A COLETAR e COLETADO PELO SOLIC para essa situação só gera
	 * número único para unidade solicitante de convênios ou solicitação de
	 * origens Externa ou atendimento diverso.
	 * 
	 * @param {String} codigoSituacao
	 * @param {AghAtendimentos} atendimento
	 * @param {DominioOrigemAtendimento} origem
	 * @param {boolean} atdDiverso
	 * @return {boolean} Verdadeiro se pode seguir com a execução da função;
	 */
	protected boolean verificarSituacaoExame(final String codigoSituacao, final AghAtendimentos atendimento, final DominioOrigemAtendimento origem,
			final boolean atdDiverso) {

		boolean response = true;
		if ((DominioSituacaoItemSolicitacaoExame.AC.toString().equals(codigoSituacao) || DominioSituacaoItemSolicitacaoExame.CS.toString().equals(
				codigoSituacao))
				&& (!((atendimento != null && DominioSimNao.S.equals(this.getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
						atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_CONVENIO)))
						|| ((atendimento != null && DominioSimNao.S.equals(this.getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
								atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA))) && (DominioOrigemAtendimento.X
								.equals(origem) || atdDiverso || DominioOrigemAtendimento.A.equals(origem) || DominioOrigemAtendimento.C
								.equals(origem))) || atdDiverso))) {
			response = false;

		}

		return response;

	}

	/**
	 * Verifica se o código da situação do item foi modificada. (RN29) Se sim
	 * retorna verdadeiro; Senão Falso;
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @return {boolean}
	 * @throws ApplicationBusinessException
	 */
	public boolean verificarSituacaoItemSolicitacaoModificada(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws ApplicationBusinessException {

		boolean response = false;

		if (CoreUtil.modificados(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo(), itemSolicitacaoExameOriginal
				.getSituacaoItemSolicitacao().getCodigo())) {

			response = true;

		}

		return response;

	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_atu_CCEL_LIB (RN30)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void atualizarCcelLib(final AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {

		if (!isHCPA() || !aelExameApItemSolicDAO.isOracle()) {
			return;
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AELK_ISE_RN_RN_ISEP_ATU_CCEL_LIB.toString();
		
		AghWork work = new AghWork(servidorLogado.getUsuario()) {
			@Override
			public void executeAghProcedure(final Connection connection) throws SQLException {

				CallableStatement cs = null;
				try {
					final StringBuilder sbCall = new StringBuilder(CALL_STM);
					sbCall.append(nomeObjeto)
					.append("(?,?,?,?,?)}");

					cs = connection.prepareCall(sbCall.toString());

					CoreUtil.configurarParametroCallableStatement(cs, 1, Types.VARCHAR, itemSolicitacaoExame.getSituacaoItemSolicitacao()
							.getCodigo());
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, itemSolicitacaoExame.getId().getSoeSeq());
					CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, itemSolicitacaoExame.getId().getSeqp());
					CoreUtil.configurarParametroCallableStatement(cs, 4, Types.TIMESTAMP, new Date());
					CoreUtil.configurarParametroCallableStatement(cs, 5, Types.VARCHAR, itemSolicitacaoExame.getExame().getSigla());

					cs.execute();
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};
		
		try {
			this.logDebug("Executando por callableStatement AELK_ISE_RN_RN_ISEP_ATU_CCEL_LIB");
			getAelAgrpPesquisasDAO().doWork(work);

		} catch (final Exception e) {
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			final String valores = CoreUtil.configurarValoresParametrosCallableStatement(itemSolicitacaoExame.getSituacaoItemSolicitacao()
					.getCodigo(), itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp(), new Date(), itemSolicitacaoExame
					.getExame().getSigla());
			this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores));
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		
		if (work.getException() != null){
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			final String valores = CoreUtil.configurarValoresParametrosCallableStatement(itemSolicitacaoExame.getSituacaoItemSolicitacao()
					.getCodigo(), itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp(), new Date(), itemSolicitacaoExame
					.getExame().getSigla());
			this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true, valores));
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), false, valores));
		}		

		this.logDebug("Executou por callableStatement AELK_ISE_RN_RN_ISEP_ATU_CCEL_LIB com sucesso!");
	}

	/**
	 * ORADB aelk_ise_rn.rn_isep_atu_CX_POST (RN31)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void atualizarCaixaPostal(final AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException,
			ApplicationBusinessException {

		if (!isHCPA() || !aelExameApItemSolicDAO.isOracle()) {
			return;
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AELK_ISE_RN_RN_ISEP_ATU_CX_POST.toString();
		
		AghWork work = new AghWork(servidorLogado.getUsuario()) {
			@Override
			public void executeAghProcedure(final Connection connection) throws SQLException {

				CallableStatement cs = null;
				try {
					final StringBuilder sbCall = new StringBuilder(CALL_STM);
					sbCall.append(nomeObjeto)
					.append("(?,?,?)}");

					cs = connection.prepareCall(sbCall.toString());

					CoreUtil.configurarParametroCallableStatement(cs, 1, Types.VARCHAR, itemSolicitacaoExame.getSituacaoItemSolicitacao()
							.getCodigo());
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, itemSolicitacaoExame.getId().getSoeSeq());
					CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, itemSolicitacaoExame.getId().getSeqp());

					cs.execute();
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};
		
		try {
			this.logDebug("Executando por callableStatement AELK_ISE_RN_RN_ISEP_ATU_CX_POST");				
			getAelAgrpPesquisasDAO().doWork(work);

		} catch (final Exception e) {
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			final String valores = CoreUtil.configurarValoresParametrosCallableStatement(itemSolicitacaoExame.getSituacaoItemSolicitacao()
					.getCodigo(), itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());
			this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores));
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		
		if (work.getException() != null) {
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(
							itemSolicitacaoExame.getSituacaoItemSolicitacao()
									.getCodigo(), itemSolicitacaoExame.getId()
									.getSoeSeq(), itemSolicitacaoExame.getId()
									.getSeqp());
			this.logError(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							work.getException(), true, valores));
			throw new ApplicationBusinessException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, work.getException(), false,
									valores));
		}
		
		this.logDebug("Executou por callableStatement AELK_ISE_RN_RN_ISEP_ATU_CX_POST com sucesso!");

	}

	/**
	 * ORADB FFC_INTERFACE_AEL (RN32)
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void ffcInterfaceAel(final AelItemSolicitacaoExames itemSolicitacaoExame, final AelItemSolicitacaoExames itemSolicitacaoExameOriginal)
			throws ApplicationBusinessException {

		if (!isHCPA() || !aelExameApItemSolicDAO.isOracle()) {
			return;
		}

		// TODO: O código abaixo tem o objetivo de chamar a procedure
		// FFC_INTERFACE_AEL via callableStatement, porém
		// a mesma não está executando corretamente ao ser chamada desta forma.
		// Após conversa com o Ney e a Milena, foi decidido
		// que por hora esta chamada pode ser comentada, pois segundo eles não
		// haverá um cenário em que a procedure será executada,
		// seja na internação, alta ou na contratualização.
		// ATENÇÃO: Esta procedure DEVERÁ ser migrada quando o Faturamento de
		// Exames for migrado.
		//
		// final String nomeObjeto = EsquemasOracleEnum.CONV + "." +
		// ObjetosBancoOracleEnum.FFC_INTERFACE_AEL.toString();
		// try {
		// this.logDebug("Executando por callableStatement FFC_INTERFACE_AEL");
		// getAelAgrpPesquisasDAO().doWork(new AghWork(servidorLogado.getUsuario()) {
		// public void executeAghProcedure(Connection connection) throws
		// SQLException {
		//
		// CallableStatement cs = null;
		// try {
		// StringBuilder sbCall = new StringBuilder("{call ");
		// sbCall.append(nomeObjeto);
		// sbCall.append("(?,?,?,?,?,?,?,?,?,?)}");
		//
		// String codigoSituacaoOld = null;
		// if (itemSolicitacaoExameOriginal != null){
		// codigoSituacaoOld =
		// itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo();
		// }
		// String dataFormatada = null;
		// SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		// dataFormatada = df.format(new Date());
		//
		// Integer matricula = null;
		// Short vinCodigo = null;
		// if (itemSolicitacaoExame.getServidorResponsabilidade() != null){
		// matricula =
		// itemSolicitacaoExame.getServidorResponsabilidade().getId().getMatricula();
		// vinCodigo =
		// itemSolicitacaoExame.getServidorResponsabilidade().getId().getVinCodigo();
		// }
		// cs = connection.prepareCall(sbCall.toString());
		//
		// CoreUtil.configurarParametroCallableStatement(cs, 1, Types.VARCHAR,
		// itemSolicitacaoExame.getExame().getSigla());
		// CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER,
		// itemSolicitacaoExame.getMaterialAnalise().getSeq());
		// CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER,
		// itemSolicitacaoExame.getUnidadeFuncional().getSeq());
		// CoreUtil.configurarParametroCallableStatement(cs, 4, Types.VARCHAR,
		// dataFormatada);
		// CoreUtil.configurarParametroCallableStatement(cs, 5, Types.INTEGER,
		// itemSolicitacaoExame.getId().getSoeSeq());
		// CoreUtil.configurarParametroCallableStatement(cs, 6, Types.INTEGER,
		// itemSolicitacaoExame.getId().getSeqp());
		// CoreUtil.configurarParametroCallableStatement(cs, 7, Types.VARCHAR,
		// codigoSituacaoOld);
		// CoreUtil.configurarParametroCallableStatement(cs, 8, Types.VARCHAR,
		// itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo());
		// CoreUtil.configurarParametroCallableStatement(cs, 9, Types.INTEGER,
		// matricula);
		// CoreUtil.configurarParametroCallableStatement(cs, 10, Types.INTEGER,
		// vinCodigo);
		//
		// cs.execute();
		// } finally {
		// if (cs != null) {
		// cs.close();
		// }
		// }
		// }
		// });
		//
		// this.logDebug("Executou por callableStatement FFC_INTERFACE_AEL com sucesso!");
		// } catch (Exception e) {
		//
		// String codigoSituacaoOld = null;
		// if (itemSolicitacaoExameOriginal != null){
		// codigoSituacaoOld =
		// itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo();
		// }
		// String dataFormatada = null;
		// SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		// dataFormatada = df.format(new Date());
		// Integer matricula = null;
		// Short vinCodigo = null;
		// if (itemSolicitacaoExame.getServidorResponsabilidade() != null){
		// matricula =
		// itemSolicitacaoExame.getServidorResponsabilidade().getId().getMatricula();
		// vinCodigo =
		// itemSolicitacaoExame.getServidorResponsabilidade().getId().getVinCodigo();
		// }
		//
		// String valores = CoreUtil
		// .configurarValoresParametrosCallableStatement(
		// itemSolicitacaoExame.getExame().getSigla(),
		// itemSolicitacaoExame.getMaterialAnalise().getSeq(),
		// itemSolicitacaoExame.getUnidadeFuncional().getSeq(),
		// dataFormatada,
		// itemSolicitacaoExame.getId().getSoeSeq(),
		// itemSolicitacaoExame.getId().getSeqp(),
		// codigoSituacaoOld,
		// itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo(),
		// matricula,
		// vinCodigo
		// );
		//
		// this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
		// e, true, valores));
		// throw new
		// ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
		// nomeObjeto, valores,
		// CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e,
		// false, valores));
		// }

	}

	/**
	 * Verifica se deve ser gerado dados na tabela de laudo único (RN33).
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExameOriginal
	 * @return {boolean}
	 * @throws ApplicationBusinessException
	 */
	public boolean verificarCodigoSituacaoNumeroAP(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws ApplicationBusinessException {

		boolean response = false;

		if ((CoreUtil.modificados(itemSolicitacaoExame.getNumeroApOrigem(), itemSolicitacaoExameOriginal.getNumeroApOrigem()) || CoreUtil.modificados(
				itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo(), itemSolicitacaoExameOriginal.getSituacaoItemSolicitacao().getCodigo()))
				&& (DominioSituacaoItemSolicitacaoExame.AE.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()) || DominioSituacaoItemSolicitacaoExame.CA
						.toString().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo()))) {

			response = true;

		}

		return response;

	}

    private ProcessarFilaExamesLiberados getProcessarFilaExamesLiberados(){
    	return processarFilaExamesLiberados;
    }
	
    /**
     * Incluir o exame na fila ao ser realizada a sua liberação
     * @param itemSolicitacaoExame
     * @throws ApplicationBusinessException 
     */
	private void enviarSolicitacaoExameFila(
			AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {

		// verifica se integração está ligada
		boolean isAtivarRotina = false;
		final AghParametros parametroAtivaExameInternet = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.SCHEDULERINIT_PROCESSAR_EXAME_INTERNET_SCHEDULER);
		if (parametroAtivaExameInternet != null) {
			Object paramSimOuNao = parametroAtivaExameInternet.getValor();
			if (paramSimOuNao != null) {
				isAtivarRotina = "S".equalsIgnoreCase(paramSimOuNao.toString());
			}
		}	

		if(!isAtivarRotina) {
			return;
		}
		
		/*
		MensagemSolicitacaoExameGrupoVO mensagem = new MensagemSolicitacaoExameLiberadoGrupoVO();
		mensagem.setSeqSolicitacaoExame(itemSolicitacaoExame
				.getSolicitacaoExame().getSeq());
		mensagem.setSeqExameInternetGrupo(this.getAelItemSolicitacaoExameDAO()
				.buscarSeqExameInternetGrupo(
						itemSolicitacaoExame.getId().getSoeSeq(),
						itemSolicitacaoExame.getId().getSeqp()));
		mensagem.setItemSolicitacaoExames(itemSolicitacaoExame);
		
		this.getProcessarFilaExamesLiberados().enviar(mensagem);
		*/
		/**
		 * twickert 23/04/2015
		 * O codigo acima foi comentado pq o servidor beta-pc nao deve enviar diretamente para o portal de exames
		 * deixando essa tarefa para para o servidor ara.
		 * Outro problema eh q a transacao atual ainda nao foi commitada e a fila jms abre uma transacao nova
		 * para fazer o envio, causando a geracao de laudo sem a data da liberacao.
		 */
		
		this.getSolicitacaoExameFacade().inserirStatusInternet(
				itemSolicitacaoExame.getSolicitacaoExame(), itemSolicitacaoExame, null, DominioSituacaoExameInternet.N,
				DominioStatusExameInternet.LI, null,
				itemSolicitacaoExame.getServidorResponsabilidade());

	}
		
	private AelExameApItemSolicDAO getAelExameApItemSolicDAO() {
		return aelExameApItemSolicDAO;
	}
	
	private String obterMateriais(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		final String regiaoAnatomica = this.obterRegiaoAnatomica(itemSolicitacaoExame);

		String materialAnalise = this.obterDescricaoMaterialAnalise(itemSolicitacaoExame);

		if (!StringUtils.isEmpty(regiaoAnatomica)) {
			// O código original teve q ser mudado por causa do PMD, regra UseStringBufferForStringAppends!
			// materialAnalise = materialAnalise == null ? regiaoAnatomica : new StringBuffer(regiaoAnatomica).append(':').append(materialAnalise).toString();
			materialAnalise = new StringBuffer((materialAnalise == null )? regiaoAnatomica : new StringBuffer(regiaoAnatomica).append(':').append(materialAnalise).toString()).toString();
		}
		return materialAnalise;
	}

	private String obterDescricaoMaterialAnalise(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		if (itemSolicitacaoExame.getDescMaterialAnalise() == null) {
			return itemSolicitacaoExame.getMaterialAnalise() == null ? null : itemSolicitacaoExame.getMaterialAnalise().getDescricao();
		} else {
			return itemSolicitacaoExame.getDescMaterialAnalise();
		}
	}

	private String obterRegiaoAnatomica(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		if (itemSolicitacaoExame.getDescRegiaoAnatomica() == null) {
			return itemSolicitacaoExame.getRegiaoAnatomica() == null ? null : itemSolicitacaoExame.getRegiaoAnatomica().getDescricao();
		} else {
			return itemSolicitacaoExame.getDescRegiaoAnatomica();
		}
	}

	
	private String obterMensagem(BusinessExceptionCode code,
			Object... params) {
			String msg = getResourceBundleValue(code.toString());

			// Se for null ou vazio, mostra a propria chave
			if (msg == null || msg.length() == 0) {
				msg = code.toString();
			}

			// Faz a interpolacao de parametros na mensagem
			msg = java.text.MessageFormat.format(msg, params);

			return msg;	
	}	
	// --------------------------------------------------
	// Getters/Setters
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected AelItemConfigExameDAO getAelItemConfigExameDAO() {
		return aelItemConfigExameDAO;
	}

	protected AelAnatomoPatologicoDAO getAelAnatomoPatologicoDAO() {
		return aelAnatomoPatologicoDAO;
	}

	protected AelExameApDAO getAelExameApDAO() {
		return aelExameApDAO;
	}

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}

	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}

	protected AelExamesDependentesDAO getAelExamesDependentesDAO() {
		return aelExamesDependentesDAO;
	}

	protected AelHorarioExameDispDAO getAelHorarioExameDispDAO() {
		return aelHorarioExameDispDAO;
	}

	protected AelResultadoExameDAO getAelResultadoExameDAO() {
		return aelResultadoExameDAO;
	}

	protected AelDocResultadoExameDAO getAelDocResultadoExameDAO() {
		return aelDocResultadoExameDAO;
	}

	protected AelAgrpPesquisaXExameDAO getAelAgrpPesquisaXExameDAO() {
		return aelAgrpPesquisaXExameDAO;
	}

	protected AelPacAgrpPesqExameDAO getAelPacAgrpPesqExameDAO() {
		return aelPacAgrpPesqExameDAO;
	}

	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected AelExamesProvaDAO getAelExamesProvaDAO() {
		return aelExamesProvaDAO;
	}

	

	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}

	protected AelControleNumeroUnicoDAO getAelControleNumeroUnicoDAO() {
		return aelControleNumeroUnicoDAO;
	}

	protected IPesquisaExamesFacade getPesquisaExamesFacade() {
		return this.pesquisaExamesFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return this.examesPatologiaFacade;
	}
	
	private AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}

	protected AelTmpIntervaloColetaDAO getAelTmpIntervaloColetaDAO() {
		return aelTmpIntervaloColetaDAO;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected ItemSolicitacaoExameRN getItemSolicitacaoExameRN() {
		return itemSolicitacaoExameRN;
	}
	
	protected AelExameApItemSolicRN getAelExameApItemSolicRN() {
		return aelExameApItemSolicRN;
	}

	protected AelAgrpPesquisasDAO getAelAgrpPesquisasDAO() {
		return aelAgrpPesquisasDAO;
	}

	protected AelSeqCodbarraRedomeRN getAelSeqCodbarraRedomeRN() {
		return aelSeqCodbarraRedomeRN;
	}

	protected AelExecExamesMatAnaliseDAO getAelExecExamesMatAnaliseDAO() {
		return aelExecExamesMatAnaliseDAO;
	}

	/**
	 * Entidade que armazena os atributos da interface com o faturamento (
	 * realizarInterfaceFaturamento ).
	 * 
	 */
	
	protected class VariaveisInterfaceFaturamento {
		// Variaveis da procedure
		private Short conv75;
		private AghAtendimentos atendimento;
		private AipPacientes paciente;
		private AghEspecialidades especialidade;
		private FatConvenioSaudePlano convenioSaudePlano;
		private AinInternacao internacao;
		private Date dthrSolicitacao;
		private DominioTipoPlano tipoAtd;
		private DominioOrigemAtendimento origem;
		private Date dthrEvento;
		private RapServidores servidor;
		private String exaSigla;
		private Integer manSeq;
		private AghUnidadesFuncionais unidadeFuncional;
		private Short qtdRealizada;
		private Date dthrSolic;
		private FatProcedHospInternos procedHospInterno;
		private Integer cthSeq;
		private DominioSituacaoConta indSituacao;
		private Short convenioInt;
		private Byte planoInt;
		private Date dtAltaAdmin;
		private Short ichSeqp;
		private RapServidores servidorResp = null;
		private DominioModuloCompetencia moduloNovo = DominioModuloCompetencia.AMB;

		public Short getConv75() {
			return conv75;
		}

		public void setConv75(final Short conv75) {
			this.conv75 = conv75;
		}

		public Integer getAtdSeq() {
			if (atendimento != null) {
				return atendimento.getSeq();
			}
			return null;
		}

		public AghAtendimentos getAtendimento() {
			return atendimento;
		}

		public void setAtendimento(final AghAtendimentos atendimento) {
			this.atendimento = atendimento;
		}

		public Integer getPacCodigo() {
			if (paciente != null) {
				return paciente.getCodigo();
			}
			return null;
		}

		public AipPacientes getPaciente() {
			return paciente;
		}

		public void setPaciente(final AipPacientes paciente) {
			this.paciente = paciente;
		}

		public Short getEspSeq() {
			if (especialidade != null) {
				return especialidade.getSeq();
			}
			return null;
		}

		public AghEspecialidades getEspecialidade() {
			return especialidade;
		}

		public void setEspecialidade(final AghEspecialidades especialidade) {
			this.especialidade = especialidade;
		}

		public Short getCspCnvCodigo() {
			if (convenioSaudePlano != null && convenioSaudePlano.getConvenioSaude() != null) {
				return convenioSaudePlano.getConvenioSaude().getCodigo();
			}
			return null;
		}

		public Byte getCspSeq() {
			if (convenioSaudePlano != null) {
				return convenioSaudePlano.getId().getSeq();
			}
			return null;
		}

		public FatConvenioSaudePlano getConvenioSaudePlano() {
			return convenioSaudePlano;
		}

		public void setConvenioSaudePlano(final FatConvenioSaudePlano convenioSaudePlano) {
			this.convenioSaudePlano = convenioSaudePlano;
		}

		public Integer getIntSeq() {
			if (internacao != null) {
				return internacao.getSeq();
			}
			return null;
		}

		public AinInternacao getInternacao() {
			return internacao;
		}

		public void setInternacao(final AinInternacao internacao) {
			this.internacao = internacao;
		}

		public Date getDthrSolicitacao() {
			return dthrSolicitacao;
		}

		public void setDthrSolicitacao(final Date dthrSolicitacao) {
			this.dthrSolicitacao = dthrSolicitacao;
		}

		public DominioTipoPlano getTipoAtd() {
			return tipoAtd;
		}

		public void setTipoAtd(final DominioTipoPlano tipoAtd) {
			this.tipoAtd = tipoAtd;
		}

		public DominioOrigemAtendimento getOrigem() {
			return origem;
		}

		public void setOrigem(final DominioOrigemAtendimento origem) {
			this.origem = origem;
		}

		public Integer getAtdSeqMae() {
			if (atendimento != null && atendimento.getAtendimentoMae() != null) {
				return atendimento.getAtendimentoMae().getSeq();
			}
			return null;
		}

		public Date getDthrEvento() {
			return dthrEvento;
		}

		public void setDthrEvento(final Date dthrEvento) {
			this.dthrEvento = dthrEvento;
		}

		public RapServidores getServidor() {
			return servidor;
		}

		public void setServidor(final RapServidores servidor) {
			this.servidor = servidor;
		}

		public String getExaSigla() {
			return exaSigla;
		}

		public void setExaSigla(final String exaSigla) {
			this.exaSigla = exaSigla;
		}

		public Integer getManSeq() {
			return manSeq;
		}

		public void setManSeq(final Integer manSeq) {
			this.manSeq = manSeq;
		}

		public Short getUnfSeq() {
			return unidadeFuncional.getSeq();
		}

		public AghUnidadesFuncionais getUnidadeFuncional() {
			return unidadeFuncional;
		}

		public void setUnidadeFuncional(final AghUnidadesFuncionais unidadeFuncional) {
			this.unidadeFuncional = unidadeFuncional;
		}

		public Short getQtdRealizada() {
			return qtdRealizada;
		}

		public void setQtdRealizada(final Short qtdRealizada) {
			this.qtdRealizada = qtdRealizada;
		}

		public Date getDthrSolic() {
			return dthrSolic;
		}

		public void setDthrSolic(final Date dthrSolic) {
			this.dthrSolic = dthrSolic;
		}

		public Integer getPhiSeq() {
			if (procedHospInterno != null) {
				return procedHospInterno.getSeq();
			}
			return null;
		}

		public FatProcedHospInternos getProcedHospInterno() {
			return procedHospInterno;
		}

		public void setProcedHospInterno(final FatProcedHospInternos procedHospInterno) {
			this.procedHospInterno = procedHospInterno;
		}

		public Integer getCthSeq() {
			return cthSeq;
		}

		public void setCthSeq(final Integer cthSeq) {
			this.cthSeq = cthSeq;
		}

		public DominioSituacaoConta getIndSituacao() {
			return indSituacao;
		}

		public void setIndSituacao(final DominioSituacaoConta indSituacao) {
			this.indSituacao = indSituacao;
		}

		public Short getConvenioInt() {
			return convenioInt;
		}

		public void setConvenioInt(final Short convenioInt) {
			this.convenioInt = convenioInt;
		}

		public Byte getPlanoInt() {
			return planoInt;
		}

		public void setPlanoInt(final Byte planoInt) {
			this.planoInt = planoInt;
		}

		public Date getDtAltaAdmin() {
			return dtAltaAdmin;
		}

		public void setDtAltaAdmin(final Date dtAltaAdmin) {
			this.dtAltaAdmin = dtAltaAdmin;
		}

		public Short getIchSeqp() {
			return ichSeqp;
		}

		public void setIchSeqp(final Short ichSeqp) {
			this.ichSeqp = ichSeqp;
		}

		public RapServidores getServidorResp() {
			return servidorResp;
		}

		public void setServidorResp(final RapServidores servidorResp) {
			this.servidorResp = servidorResp;
		}

		public DominioModuloCompetencia getModuloNovo() {
			return moduloNovo;
		}

		public void setModuloNovo(final DominioModuloCompetencia moduloNovo) {
			this.moduloNovo = moduloNovo;
		}
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO(){
		return aelSitItemSolicitacoesDAO;
	}
	
	protected AelExameReflexoDAO getAelExameReflexoDAO() {
		return aelExameReflexoDAO;
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected AelMaterialAnaliseDAO getAelMaterialAnaliseDAO() {
		return aelMaterialAnaliseDAO;
	}

	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}
	
	protected AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}
	
	protected AelConfigExLaudoUnicoDAO getAelConfigExLaudoUnicoDAO() {
		return aelConfigExLaudoUnicoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected AelSecaoConfExamesDAO getAelSecaoConfExamesDAO() {
		return this.aelSecaoConfExamesDAO;
	}
}