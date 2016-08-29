package br.gov.mec.aghu.exames.solicitacao.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.dominio.DominioAbrangenciaGrupoRecomendacao;
import br.gov.mec.aghu.dominio.DominioColetaAtendUrgencia;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioNecessidadeInternacaoAihAssinada;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacao;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioResponsavelGrupoRecomendacao;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSismamaSimNaoNaoSabe;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTelaOriginouSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoMensagem;
import br.gov.mec.aghu.dominio.DominioTipoPesquisaExame;
import br.gov.mec.aghu.dominio.DominioTipoTransporteQuestionario;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameDAO;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelExameDeptConvenioDAO;
import br.gov.mec.aghu.exames.dao.AelExamesLimitadoAtendDAO;
import br.gov.mec.aghu.exames.dao.AelExamesProvaDAO;
import br.gov.mec.aghu.exames.dao.AelExigenciaExameDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioRotinaColetasDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelPermissaoUnidSolicDAO;
import br.gov.mec.aghu.exames.dao.AelRecomendacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmoExameConvDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.AbasIndicadorApresentacaoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DataProgramadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameSuggestionVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameCancelamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.exames.vo.ExamesCriteriosSelecionadosVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExameDeptConvenio;
import br.gov.mec.aghu.model.AelExamesLimitadoAtend;
import br.gov.mec.aghu.model.AelExamesLimitadoAtendId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesProva;
import br.gov.mec.aghu.model.AelExigenciaExame;
import br.gov.mec.aghu.model.AelHorarioRotinaColetas;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoAmoExameConv;
import br.gov.mec.aghu.model.AelTipoAmoExameConvId;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength",
		"PMD.CouplingBetweenObjects", "PMD.CyclomaticComplexity" })
@Stateless
public class ItemSolicitacaoExameON extends BaseBusiness {
	
	private static final String ELABORAR_SOLICITACAO_EXAME_CONSULTA_ANTIGA = "elaborarSolicitacaoExameConsultaAntiga";
	
	@EJB
	private ItemSolicitacaoExameEnforceRN itemSolicitacaoExameEnforceRN;
	
	@EJB
	private ItemSolicitacaoExameRN itemSolicitacaoExameRN;
	
	private static final Log LOG = LogFactory.getLog(ItemSolicitacaoExameON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	//@EJB
	//private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
    @EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@Inject
	private AelPermissaoUnidSolicDAO aelPermissaoUnidSolicDAO;
	
	@Inject
	private AelExamesProvaDAO aelExamesProvaDAO;
	
	@Inject
	private AelExamesLimitadoAtendDAO aelExamesLimitadoAtendDAO;
	
	@Inject
	private AelMaterialAnaliseDAO aelMaterialAnaliseDAO;
	
	@Inject
	private AelHorarioRotinaColetasDAO aelHorarioRotinaColetasDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelExameDeptConvenioDAO aelExameDeptConvenioDAO;
	
	@Inject
	private AelRecomendacaoExameDAO aelRecomendacaoExameDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelTipoAmoExameConvDAO aelTipoAmoExameConvDAO;
	
	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	
	@Inject
	private AelAgrpPesquisasDAO aelAgrpPesquisasDAO;
	
	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelAgrpPesquisaXExameDAO aelAgrpPesquisaXExameDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private VAelSolicAtendsDAO vAelSolicAtendsDAO;
	
	@Inject
	private AelExigenciaExameDAO aelExigenciaExameDAO;
	
	@EJB
	private HorariosRotinaColetaFactory horariosRotinaColetaFactory;
	
	@EJB
	private VerificarPermissaoHorariosRotina verificarPermissaoHorariosRotina;
	
	@EJB
	private VerificarPermissoesExame verificarPermissoesExame;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7063707000067971827L;

	public enum ItemSolicitacaoExameONExceptionCode implements BusinessExceptionCode {
		AEL_00426,
		AEL_00426C,
		MENSAGEM_CONFIRMA_ADD_EXAME_SOLICITACAO,
		AEL_01549B,
		AEL_01550B,
		AEL_01551B,
		AEL_01779,
		AEL_01779a,
		AEL_01780,
		AEL_01634B,
		MSG_EXAME_EXIGE_INFOCLI_ANTIMICRO_PRIMEXAME,
		MSG_EXAME_EXIGE_INFOCLI,
		MSG_EXAME_EXIGE_ANTIMICRO,
		MSG_EXAME_EXIGE_PRIMEXAME,
		AEL_01704B,
		AEL_01337B,
		AEL_01333B,
		AEL_01332B,
		AEL_01335B,
		AEL_01335C,
		AEL_01331B,
		AEL_03407,
		AEL_03408,
		AEL_03409, AEL_03392, AEL_03341, AEL_03332, AEL_034102, AEL_03339, AEL_03335, AEL_03334, AEL_03333,
		AEL_03394, AEL_03395, ERRO_QUESTIONARIO_SISMAMA_TEMPO_MENOPAUSA,
		ERRO_SITUACAO_ITEM_COLETAVEL, ERRO_SITUACAO_ITEM_NAO_COLETAVEL, AEL_SISMAMA_PREENCHER_NOD,
		AEL_SISMAMA_PREENCHER_ANO_MAMO, AEL_SISMAMA_PREENCHER_MAMA_RADIO, AEL_SISMAMA_PREENCHER_ANO_RADIO, AEL_SISMAMA_PREENCHER_ANO_CIRUR;}

	/**
	 * Função que chama somente validações que podem gerar
	 * exceções de negócio (ERROR) ou atualizações de objeto 
	 * necessárias para inclusão de um exame.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @throws BaseException
	 */
	public void validarItemSolicitacaoExameErros(ItemSolicitacaoExameVO itemSolicitacaoExameVO, final Map<String,Object> questionarioSismama) throws BaseException {

		this.validarSituacaoItemSolicitacao(itemSolicitacaoExameVO);
		
		//RN3 e RN4
		if (this.verificarNecessidadeLimitaExames(itemSolicitacaoExameVO)) {
			this.verificarLimitaExames(itemSolicitacaoExameVO);
		}

		//RN5 e RN6
		if (this.verificarNecessidadeExamesPosAlta(itemSolicitacaoExameVO)) {
			this.verificarExamesPosAlta(itemSolicitacaoExameVO);
		}

		//RN7 e RN8
		if (this.verificarNecessidadeExameDesativado(itemSolicitacaoExameVO)) {
			this.verificarExameDesativado(itemSolicitacaoExameVO);
		}		

		//RN9
		if (this.verificarNecessidadeInformacoesClinicas(itemSolicitacaoExameVO)) {
			this.verificarInformacoesClinicas(itemSolicitacaoExameVO);
		}

		//RN13
		this.verificarProvasDependentes(itemSolicitacaoExameVO);

		//RN14
		this.gerarDependentes(itemSolicitacaoExameVO, itemSolicitacaoExameVO.getSolicitacaoExameVO().getUnidadeTrabalho());

		//RN15
		this.verificarProgRotina(itemSolicitacaoExameVO);

		//RN18 Removida pois AELP_CHAMA_QUT_SISMAMA é executada antes de adicionar o questionário (melhoria do aghu).

		//RN8, regra executada na RN de item.
		this.getItemSolicitacaoExameRN()
		.verificarConselhoProfissionalOuPermissao(
				null,
				new AelSolicitacaoExames(),
				itemSolicitacaoExameVO.getSolicitacaoExameVO()
				.getResponsavel(),
				itemSolicitacaoExameVO.getSolicitacaoExameVO()
				.getAtendimento(),
				itemSolicitacaoExameVO.getUnfExecutaExame()
				.getUnfExecutaExame()
				.getAelExamesMaterialAnalise().getAelExames(), 
				itemSolicitacaoExameVO.getUnfExecutaExame()
				.getUnfExecutaExame()
				.getAelExamesMaterialAnalise().getAelMateriaisAnalises());
		
		// RN9
		this.verificarEspecialidadeExame(itemSolicitacaoExameVO);
		
		if(itemSolicitacaoExameVO.getMostrarAbaQuestionarioSismama()) {
			//18450 ON5
			this.verificarExameSismamaAnamnese(itemSolicitacaoExameVO, questionarioSismama);
			//18451 ON5
			this.verificarQuestionarioSismamaInfComplementares(itemSolicitacaoExameVO, questionarioSismama);
		}

	}
	
	/**
	 * @ORADB P_CONSISTE_DADOS (ON5 #18450). 
	 * 
	 * Valida exames da SISMAMA Anamnese.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void verificarExameSismamaAnamnese(ItemSolicitacaoExameVO itemSolicitacaoExameVO, final Map<String,Object> questionarioSismama) throws BaseException {
		AipPacientes paciente = itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento().getPaciente();
		
		if (!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANM_NOD.name()))
				&& !obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANM_NOD_MD.name()))
				&& !obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANM_NOD_ME.name()))) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_SISMAMA_PREENCHER_NOD);
		}
		
		if (questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANM_MAMOGRAF.name()) != null
				&& DominioSismamaSimNaoNaoSabe.SIM.equals(questionarioSismama
						.get(DominioSismamaMamoCadCodigo.C_ANM_MAMOGRAF.name()))) {
			if (questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANM_MAMO_ANO.name()) == null) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameONExceptionCode.AEL_SISMAMA_PREENCHER_ANO_MAMO);
			}
		}
		
		if (questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_RADIO.name()) != null
				&& DominioSismamaSimNaoNaoSabe.SIM.equals(questionarioSismama
						.get(DominioSismamaMamoCadCodigo.C_ANA_RADIO.name()))) {
			
			if (!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_RADIO_MDIR.name()))
					&& !obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_RADIO_MESQ.name()))
					) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameONExceptionCode.AEL_SISMAMA_PREENCHER_MAMA_RADIO);
			}
			
			if (obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_RADIO_MDIR.name()))
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_DIREITA.name()) == null) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameONExceptionCode.AEL_SISMAMA_PREENCHER_ANO_RADIO);
			}

			if (obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_RADIO_MESQ.name()))
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESQUERDA.name()) == null) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameONExceptionCode.AEL_SISMAMA_PREENCHER_ANO_RADIO);
			}
		}	
		if (!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_NAOFEZCIRUR.name()))) {
			if (questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_INC_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_INC_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_EXC_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_EXC_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_CENTRALECTOMIA_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_CENTRALECTOMIA_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_SEGMEN_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_SEGMEN_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_DUTECT_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_DUTECT_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_MASTEC_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_MASTEC_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_M_PELE_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_M_PELE_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_MASTEC_POUP_PE_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_MASTEC_POUP_PE_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESVAZIA_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESVAZIA_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_BIOPSIA_LI_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_BIOPSIA_LI_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_RECON_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_RECON_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_RED_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_RED_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_IMP_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_IMP_E.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_TUMOR_D.name()) == null
					&& questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_TUMOR_E.name()) == null) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoExameONExceptionCode.AEL_SISMAMA_PREENCHER_ANO_CIRUR);
			}
		}
		if(questionarioSismama.get(DominioSismamaMamoCadCodigo.D_ANA_MESTRUAC.name()) != null) {
			if(obtemValorData(questionarioSismama.get(DominioSismamaMamoCadCodigo.D_ANA_MESTRUAC.name())).before((paciente.getDtNascimento()))) {
				throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03394);
			}
			if(obtemValorData(questionarioSismama.get(DominioSismamaMamoCadCodigo.D_ANA_MESTRUAC.name())).after((new Date()))) {
				throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03395);
			}
		}
		if(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_MENOP_IDADE.name()) != null) {
			Integer anoMeno = Integer.parseInt(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_MENOP_IDADE.name()).toString());
			if(anoMeno > paciente.getIdade()) {
				throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.ERRO_QUESTIONARIO_SISMAMA_TEMPO_MENOPAUSA);
			}
		}
	}
	
	private Date obtemValorData(Object obj) {
		if(obj==null){
			return null;
		}else{
			return (Date)obj;
		}
	}


	@SuppressWarnings("PMD.NPathComplexity")
	private void verificarQuestionarioSismamaInfComplementares(
			ItemSolicitacaoExameVO itemSolicitacaoExameVO, final Map<String,Object> questionarioSismama) throws ApplicationBusinessException {
		AipPacientes paciente = itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento().getPaciente();
		
		if(DominioSexo.M.equals(paciente.getSexo()) && obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_MAMO_RASTR.name()))){
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03392);
		}
		
		/*if(!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_MAMO_RASTR.name())) && !obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name()))){
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03341);
		}*/
		
		if(obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_MAMO_DIAG.name()))){
			if(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name()) == null) {
				throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03332);
			}
		}
		
		if(verificarExameUnilateral(itemSolicitacaoExameVO) && 
				obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_MAMO_RASTR.name())) &&
				questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_MASTEC_D.name()) == null &&
				questionarioSismama.get(DominioSismamaMamoCadCodigo.C_ANA_ANO_MASTEC_E.name()) == null){
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_034102);
		}
		
		if( obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_MAMO_DIAG.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_LES_PALPA.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CONTROLE_RADIOL.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_LESAO_DIAG.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_AVALIACAO_ADJ.name())) ){
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03339);
		}
		
		if( 
				obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_LES_PALPA.name())) && 
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_PAPIL_DIR.name())) &&
				questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_DESC_DIR.name()) == null && 
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSE_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_QIE_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSI_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_QII_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQEXT_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQSUP_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINT_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINF_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_RRA_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_PA_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSE_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_QIE_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSI_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_QII_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQEXT_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQSUP_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINT_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINF_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_RRA_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_PA_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_LINF_AX_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_LINF_SUPRA_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_PAPIL_ESQ.name())) &&
				questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_DESC_ESQ.name()) == null && 
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSE_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_QIE_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSI_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_QII_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQEXT_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQSUP_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINT_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINF_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_RRA_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_NOD_PA_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSE_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_QIE_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSI_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_QII_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQEXT_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQSUP_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINT_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINF_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_ESP_PA_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_LINF_AX_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CLI_LINF_SUPRA_ESQ.name())) ){
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03333);
		}
		
		if(
				obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_CONTROLE_RADIOL.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_NODULO_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_MICROCAL_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_FOC_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_DIF_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_AREA_DENS_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_DIST_ARQ_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_NODULO_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_MICROCAL_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_FOC_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_DIF_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_AREA_DENS_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_RAD_DIST_ARQ_ESQ.name())) ){
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03334);
		}
		
		if(
				obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_LESAO_DIAG.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_NODULO_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_MICROCAL_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_FOC_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_DIF_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_AREA_DENS_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_DIST_ARQ_DIR.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_NODULO_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_MICROCAL_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_FOC_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_DIF_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_AREA_DENS_ESQ.name())) &&
				!obtemValorDefaultBoolean(questionarioSismama.get(DominioSismamaMamoCadCodigo.C_DIAG_DIST_ARQ_ESQ.name())) ){
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03335);
		}
	}


	//ON12
	public Boolean verificarExameUnilateral(
			ItemSolicitacaoExameVO itemSolicitacaoExameVO)
			throws ApplicationBusinessException {
		AghParametros param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		FatConvenioSaudePlano convenio = this.obterFatConvenioSaudePlano(itemSolicitacaoExameVO.getSolicitacaoExameVO());
		VFatAssociacaoProcedimento fatAssociacaoProcedimento = getFaturamentoFacade().obterFatProcedHospIntPorExameMaterialConvCspIphPhoSeq(
				itemSolicitacaoExameVO.getUnfExecutaExame().getExameSigla(),
				itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq(), 
				param.getVlrNumerico().shortValue(), convenio.getId().getCnvCodigo(),
				convenio.getId().getSeq());
		if(	getParametroFacade().verificarExisteAghParametro(AghuParametrosEnum.P_EXAME_UNILATERAL)){			
			param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EXAME_UNILATERAL);
			if(fatAssociacaoProcedimento != null){
				if(fatAssociacaoProcedimento.getId().getIphSeq().equals(param.getVlrNumerico().intValue())){
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	private Boolean obtemValorDefaultBoolean(Object obj){
		if(obj==null){
			return Boolean.FALSE;
		}else{
			return (Boolean)obj;
		}
	}


	/**
	 * RN9
	 * 
	 * Alterada em 03/06/2015 - #50796
	 * "ser solicitado por um usuario" por 
	 * "ser solicitado com um usuario responsavel"
	 * 
	 * Quando o item solicitado estiver cadastrado na tabela exame unidade especialidade, 
	 * o mesmo so pode ser solicitado com um usuario responsavel que esteja cadastrado
	 * na mesma especialidade desta tabela (exame unidade especialidade).
	 * 
	 * @param itemSolicitacaoExameVO
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarEspecialidadeExame(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws ApplicationBusinessException {
		RapServidores servidorResponsavel = itemSolicitacaoExameVO.getSolicitacaoExameVO().getResponsavel(); 
				//getServidorLogadoFacade().obterServidorLogado();
		
		AelItemSolicitacaoExames itemSolicitacaoExame = itemSolicitacaoExameVO.getModel();
		
		FatConvenioSaude convenioSaude = this.obterFatConvenioSaude(itemSolicitacaoExameVO.getSolicitacaoExameVO());
		
		this.getItemSolicitacaoExameRN().verificarEspecialidadeExame(itemSolicitacaoExame, convenioSaude, servidorResponsavel);
	}
	
	/**
	 * Função que chama somente validações que podem gerar
	 * notificações do tipo INFO ou WARN para o usuário. 
	 * Sem impedir a inclusão do exame.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @return {BaseListException}
	 * @throws BaseException
	 */
	public BaseListException validarItemSolicitacaoExameMensagens(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws BaseException {

		BaseListException info = new BaseListException();

		//RN1 e RN2
		if (this.verificarNecessidadeExamesJaSolic(itemSolicitacaoExameVO)) {
			ApplicationBusinessException message = this.verificarExameJaSolicitado(itemSolicitacaoExameVO);
			if (message != null) {
				info.add(message);
			}
		}

		//RN16
		if(itemSolicitacaoExameVO != null && itemSolicitacaoExameVO.getSolicitacaoExameVO() !=null && itemSolicitacaoExameVO.getSolicitacaoExameVO().getIsSus() != null ){
			ApplicationBusinessException retornoExigenciaExame = this.verificarExigenciaExame(itemSolicitacaoExameVO);
			if(retornoExigenciaExame != null) {
				info.add(retornoExigenciaExame);
			}
		}
		
		/*ApplicationBusinessException retornoEspecialidadeExame = this.verificarEspecialidadeExame(itemSolicitacaoExameVO);
		if(retornoEspecialidadeExame != null) {
			info.add(retornoEspecialidadeExame);
		}*/
		return info;

	}

	/**
	 * 
	 * @param unfSolicitante
	 * @return
	 */
	public Boolean verificarUrgenciaItemSolicitacaoExame(AghUnidadesFuncionais unfSolicitante) {
		if (unfSolicitante == null || unfSolicitante.getSeq() == null) {
			throw new IllegalArgumentException("Unidade Solicitante eh obrigatorio!!!");
		}

		DominioSimNao caracteristica = getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
				unfSolicitante.getSeq()
				, ConstanteAghCaractUnidFuncionais.DEFAULT_DE_EXAME_URGENTE
		);

		return caracteristica.isSim();
	}

	/**
	 *  Executa regras de validacao para sugerir Situacao para o item de solicitacao de exame.<br>
	 *  
	 * @param unfSolicitante
	 * @param atendimento
	 * @param atendimentoDiversos
	 * @param unfExecutaExame
	 * @param unfTrabalho
	 * @return
	 * @throws BaseException 
	 */
	public AelSitItemSolicitacoes obterSituacaoExameSugestao(AghUnidadesFuncionais unfSolicitante, AghAtendimentos atendimento, AelAtendimentoDiversos atendimentoDiverso, AelUnfExecutaExames unfExecutaExame, AghUnidadesFuncionais unfTrabalho, ItemSolicitacaoExameVO itemSolicEx, SolicitacaoExameVO solicitacaoExameVo) throws BaseException {
		if (unfSolicitante == null || unfSolicitante.getSeq() == null) {
			throw new IllegalArgumentException("Unidade Solicitante obrigatoria.");
		}
		String strSituacao = null;

		// Inicializar como coletado pelo socilitante para o Bloco Cirúrgico.
		DominioSimNao caracteristica = getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
				unfSolicitante.getSeq()
				, ConstanteAghCaractUnidFuncionais.BLOCO
		);
		if (caracteristica.isSim()) {
			strSituacao = buscaParametroSistemaValorTexto(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC);
		}


		DominioResponsavelColetaExames responsavelColeta = obterResponsavelColeta(atendimento, atendimentoDiverso, itemSolicEx); 
		if (responsavelColeta != null && responsavelColeta == DominioResponsavelColetaExames.S) {
			strSituacao = buscaParametroSistemaValorTexto(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC);
		}

		if (strSituacao == null) {
			// Carregar a situacao do exame conforme criterio da unidade fechada.
			DominioSimNao caractUnidFechada = getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
					unfExecutaExame.getUnidadeFuncional().getSeq() // unidade executora selecionada.
					, ConstanteAghCaractUnidFuncionais.AREA_FECHADA
			);
			if (caractUnidFechada.isSim()) {
				strSituacao = verificarMaterialColetavel(strSituacao, responsavelColeta, itemSolicEx);
			} else {
				strSituacao = buscaParametroSistemaValorTexto(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
				strSituacao = verificarResponsavelColetador(responsavelColeta, unfTrabalho, unfExecutaExame, unfSolicitante, itemSolicEx, atendimento, atendimentoDiverso, strSituacao);
			}
		} else {
			strSituacao = verificarResponsavelColetador(responsavelColeta, unfTrabalho, unfExecutaExame, unfSolicitante, itemSolicEx, atendimento, atendimentoDiverso, strSituacao);
		}

		// Verificar se a unidade executora de trabalho é a mesma unidade executora do exame e se é diferente de Patologia.
		if (unfTrabalho != null && unfTrabalho.equals(unfExecutaExame.getUnidadeFuncional())) {
			DominioSimNao caractPatologiaClinica = getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
					unfExecutaExame.getUnidadeFuncional().getSeq() // unidade executora selecionada.
					, ConstanteAghCaractUnidFuncionais.UNID_PATOLOGIA
			);
			/* e nao eh unid patologia */
			if (!caractPatologiaClinica.isSim()) {
				List<DominioOrigemAtendimento> origens = Arrays.asList(DominioOrigemAtendimento.I
						, DominioOrigemAtendimento.N, DominioOrigemAtendimento.D
						, DominioOrigemAtendimento.H, DominioOrigemAtendimento.C
				);
				
				if (atendimento != null && origens.contains(atendimento.getOrigem()) 
						&& solicitacaoExameVo.getTelaOriginouSolicitacao() != null 
						&& !solicitacaoExameVo.getTelaOriginouSolicitacao().equals(DominioTelaOriginouSolicitacaoExame.TELA_PACIENTES_INTERNADOS) 
						&& !solicitacaoExameVo.getTelaOriginouSolicitacao().equals(DominioTelaOriginouSolicitacaoExame.TELA_AMBULATORIO)) {
						
						strSituacao = buscaParametroSistemaValorTexto(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
				}

			}
		}

		return getAelSitItemSolicitacoesPorId(strSituacao);
	}

	private String verificarResponsavelColetador(DominioResponsavelColetaExames responsavelColeta, AghUnidadesFuncionais unfTrabalho, AelUnfExecutaExames unfExecutaExame, AghUnidadesFuncionais unfSolicitante, ItemSolicitacaoExameVO itemSolicEx, AghAtendimentos atendimento, AelAtendimentoDiversos atendimentoDiverso, String strSituacao) throws BaseException {
		if (responsavelColeta != null && responsavelColeta == DominioResponsavelColetaExames.C) { //Se o coletador for o responsável pela coleta
			if (unfTrabalho == null) {
				strSituacao = getSituacaoExameAtendColeta(unfExecutaExame, unfSolicitante, itemSolicEx, atendimento, atendimentoDiverso, strSituacao);
			} 
			if (!verificarMaterialColetavel(itemSolicEx)) {
				strSituacao = buscaParametroSistemaValorTexto(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);
			}
		} else {
			strSituacao = verificarMaterialColetavel(strSituacao, responsavelColeta, itemSolicEx);
		}
		
		return strSituacao;
	}

	private String verificarMaterialColetavel(String strSituacao, DominioResponsavelColetaExames responsavelColeta,  ItemSolicitacaoExameVO itemSolicEx) throws ApplicationBusinessException {
		if (verificarMaterialColetavel(itemSolicEx)) {
			if (responsavelColeta != null && responsavelColeta == DominioResponsavelColetaExames.S) {
				strSituacao = buscaParametroSistemaValorTexto(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC);
			} else {
				strSituacao = buscaParametroSistemaValorTexto(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
			}
		} else {
			strSituacao = buscaParametroSistemaValorTexto(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);
		}

		return strSituacao;
	}

	private boolean verificarMaterialColetavel(ItemSolicitacaoExameVO itemSolicEx) {
		boolean ehColetavel = false;
		//Não é necesário fazer nenhuma verificação de not null, pois todos os campos do POJO são NOT NULL.
		ehColetavel = itemSolicEx.getUnfExecutaExame()
		.getUnfExecutaExame()
		.getAelExamesMaterialAnalise()
		.getAelMateriaisAnalises()
		.getIndColetavel();
		return ehColetavel;
	}

	/**
	 * Método para obter o reponsável da coleta.
	 * 
	 * ORADB: AELP_VERIFICA_RESP_COLETA (pll)
	 * 
	 * @param atendimento
	 * @param atendimentoDiverso
	 * @param itemSolicEx
	 * @return
	 */
	public DominioResponsavelColetaExames obterResponsavelColeta(AghAtendimentos atendimento, AelAtendimentoDiversos atendimentoDiverso, ItemSolicitacaoExameVO itemSolicEx) {
		// Verificar se o responsavel pela coleta deve ser o solicitante.
		boolean encontrouConvenio = false;
		ConvenioExamesLaudosVO conv = null;
		if (atendimento != null && atendimento.getSeq() != null) {
			conv = this.getPacienteFacade().buscarConvenioExamesLaudos(atendimento.getSeq());
			if (conv != null) {
				encontrouConvenio = true;
			}
		}  else if (atendimentoDiverso != null && atendimentoDiverso.getSeq() != null) {
			conv = getExamesFacade().rnAelpBusConvAtv(atendimentoDiverso.getSeq());
			if (conv != null) {
				encontrouConvenio = true;
			}			
		}

		boolean temInfoAmoExameConv = false;
		DominioResponsavelColetaExames responsavelColeta = null; 
		if (encontrouConvenio) {
			AelTipoAmoExameConvId id = new AelTipoAmoExameConvId();
			id.setTaeOrigemAtendimento(itemSolicEx.getSolicitacaoExameVO().getOrigem());
			id.setTaeEmaExaSigla(itemSolicEx.getUnfExecutaExame().getExameSigla());
			id.setTaeEmaManSeq(itemSolicEx.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq());
			id.setTaeManSeq(itemSolicEx.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq());
			id.setCspSeq(Short.valueOf(conv.getCodigoConvenioSaudePlano()));
			id.setCspCnvCodigo(conv.getCodigoConvenioSaude());

			List<AelTipoAmoExameConv> aelTipoAmostraExame = getAelTipoAmoExameConvDAO().obterAelTipoAmoExameConvPorID(id);

			if (aelTipoAmostraExame != null && !aelTipoAmostraExame.isEmpty()) {
				temInfoAmoExameConv = true;
				responsavelColeta = aelTipoAmostraExame.get(0).getResponsavelColeta();
			}

		}

		if (!temInfoAmoExameConv) {
			List<AelTipoAmostraExame> aelTipoAmostraExame = getAelTipoAmostraExameDAO().buscarAelTipoAmostraExameResponsavelColeta(itemSolicEx.getUnfExecutaExame().getExameSigla(), itemSolicEx.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq(), itemSolicEx.getSolicitacaoExameVO().getOrigem());
			if (aelTipoAmostraExame != null && !aelTipoAmostraExame.isEmpty()) {
				responsavelColeta = aelTipoAmostraExame.get(0).getResponsavelColeta();
			}
		}
		return responsavelColeta;
	}

	private String getSituacaoExameAtendColeta(AelUnfExecutaExames unfExecutaExame, AghUnidadesFuncionais unfSolicitante, ItemSolicitacaoExameVO itemSolicEx, AghAtendimentos atendimento, AelAtendimentoDiversos atendimentoDiverso, String situacaoAtualFluxo) throws BaseException {
		String strSituacao = situacaoAtualFluxo;

		boolean atendeColeta = this.verificaAtendimentoColeta(unfExecutaExame, unfSolicitante, itemSolicEx, atendimento, atendimentoDiverso, situacaoAtualFluxo);
		if (!atendeColeta) {
			strSituacao = buscaParametroSistemaValorTexto(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC);
		}

		return strSituacao;
	}

	private AelSitItemSolicitacoes getAelSitItemSolicitacoesPorId(String strSituacao) {
		AelSitItemSolicitacoes situacao = null;
		if (strSituacao != null) {
			situacao = getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(strSituacao);
		}
		return situacao;
	}

	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}

	protected AelTipoAmoExameConvDAO getAelTipoAmoExameConvDAO() {
		return aelTipoAmoExameConvDAO;
	}

	/**
	 * 
	 * ORADB forms Function AELP_VERIFICA_ATEND_COLETA. <br>
	 * 
	 * @param unfExecutaExame
	 * @param unfSolicitante
	 * @param itemSolicEx
	 * @param atendimento
	 * @param atendimentoDiverso
	 * @param situacaoAtualFluxo Situacao que foi setada ate o momento no fluxo
	 * @return
	 * @throws BaseException
	 */
	private boolean verificaAtendimentoColeta(AelUnfExecutaExames unfExecutaExame, AghUnidadesFuncionais unfSolicitante, ItemSolicitacaoExameVO itemSolicEx, AghAtendimentos atendimento, AelAtendimentoDiversos atendimentoDiverso, String situacaoAtualFluxo) throws BaseException {
		String aColetar = buscaParametroSistemaValorTexto(AghuParametrosEnum.P_SITUACAO_A_COLETAR);

		// Verifica atendimento da coleta caso o pedido do exame seja Urgente e a situação A Coletar
		if (!itemSolicEx.getUrgente() || 
				situacaoAtualFluxo == null 
				|| !situacaoAtualFluxo.equalsIgnoreCase(aColetar)
		) {
			return true; // atende coleta
		}

		// Verifica se o atendimento é diferente dos SUS, pois nesses casos a coleta deve ser feita pelo coletador.
		FatConvenioSaude convenio = this.getItemSolicitacaoExameRN().obterFatConvenioSaude(atendimento, atendimentoDiverso);
		if (convenio == null) {
			return true; // atende coleta
		}
		if (DominioGrupoConvenio.S != convenio.getGrupoConvenio()) {
			return true; // atende coleta
		}

		// Verifica se o exame e a unidade solicitante é atendida pela coleta totalmente
		// ou somente em plantões quando solicitado na urgência

//		AelPermissaoUnidSolic aelPermissaoUnidSolic = getAelPermissaoUnidSolicDAO().buscarAelPermissaoUnidSolicPor(
//				itemSolicEx.getUnfExecutaExame().getSinonimoExame().getExame(), 
//				itemSolicEx.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises(), 
//				itemSolicEx.getUnfExecutaExame().getUnfExecutaExame().getUnidadeFuncional(), 
//				unfSolicitante
//		);
		
		AelPermissaoUnidSolic aelPermissaoUnidSolic;
		if (itemSolicEx.getUnfExecutaExame().getSinonimoExame() != null && itemSolicEx.getUnfExecutaExame().getSinonimoExame().getExame() != null){
			aelPermissaoUnidSolic = getAelPermissaoUnidSolicDAO().buscarAelPermissaoUnidSolicPor( itemSolicEx.getUnfExecutaExame().getSinonimoExame().getExame(), 
					itemSolicEx.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises(), 
					itemSolicEx.getUnfExecutaExame().getUnfExecutaExame().getUnidadeFuncional(), unfSolicitante);			
		} else {		
			aelPermissaoUnidSolic = getAelPermissaoUnidSolicDAO().buscarAelPermissaoUnidSolicPorSigla(itemSolicEx.getUnfExecutaExame().getExameSigla(), 
				itemSolicEx.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises(), 
				itemSolicEx.getUnfExecutaExame().getUnfExecutaExame().getUnidadeFuncional(), unfSolicitante);
		}
		
		if (aelPermissaoUnidSolic == null 
				|| aelPermissaoUnidSolic.getColetaAtendeUrgencia() == null 
				|| DominioColetaAtendUrgencia.T == aelPermissaoUnidSolic.getColetaAtendeUrgencia()) {
			return true; // atende coleta
		}

		// verificação de feriado
		// verificação de feriado(para véspera)
		if (itemSolicEx.getDataProgramada() != null) {
			AghHorariosUnidFuncional horarioUnidFunc = this.getItemSolicitacaoExameRN().verificaExecucaoExamePlantao(
					unfExecutaExame.getUnidadeFuncional(), 
					itemSolicEx.getDataProgramada()
			);
			//			if (horarioUnidFunc == null) {
			//				return true; // atende coleta
			//			}

			if (horarioUnidFunc != null && horarioUnidFunc.getIndPlantao()) {
				return true; // atende coleta
			} else {
				return false;
			}
		}

		return true; // atende coleta
	}

	/**
	 * Executa regras para indicar quais abas devem ser apresentada na tela de Solicitacao de Exames.<br>
	 * 
	 * @param unfExecutaExame
	 * @param unfSolicitante
	 * @param atdDivSeq 
	 * @param atdSeq 
	 * @param indGeradoAutomatico 
	 * @return
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public AbasIndicadorApresentacaoVO obterIndicadorApresentacaoAbas(AelUnfExecutaExames unfExecutaExame, 
			AghUnidadesFuncionais unfSolicitante,
			AghUnidadesFuncionais unfTrabalho, DominioOrigemAtendimento origem,
			AelSolicitacaoExames solicitacao, final Integer atdSeq,
			final Integer atdDivSeq, final Boolean indGeradoAutomatico,
			final Boolean includeUnidadeTrabalho,
			final DominioTipoTransporteQuestionario tipoTransporte)
			throws BaseException {
		if (unfExecutaExame == null || unfExecutaExame.getId() == null) {
			throw new IllegalArgumentException("Uma UnidadeExecutaExame valida deve ser informada!!!");
		}
		
		AelUnfExecutaExamesId unidExecExId = unfExecutaExame.getId();
		AelUnfExecutaExames unidExecEx = getAelUnfExecutaExamesDAO().obterPorChavePrimaria(unidExecExId);

		AelPermissaoUnidSolic pus = getAelPermissaoUnidSolicDAO()
				.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
						unidExecExId.getEmaExaSigla(), unidExecExId.getEmaManSeq(), unidExecExId.getUnfSeq().getSeq(), unfSolicitante.getSeq());
		
		AelExamesMaterialAnalise ema = unidExecEx.getAelExamesMaterialAnalise();

		BigDecimal vlrParam = this.buscaParametroSistemaValorNumerico(AghuParametrosEnum.P_COD_MATERIAIS_DIVERSOS);
		boolean compareCodigoMaterialExame = (
				unidExecEx.getAelExamesMaterialAnalise() != null &&
				unidExecEx.getAelExamesMaterialAnalise().getAelMateriaisAnalises() != null &&
				unidExecEx.getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq() != null &&
				unidExecEx.getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq().intValue()
				== vlrParam.intValue()
		);

		AbasIndicadorApresentacaoVO vo = new AbasIndicadorApresentacaoVO();

		if (pus != null) {
			vo.setMostrarAbaTipoTransporte(pus.getIndExigeTransporteO2());
		}
		if (ema != null) {
			vo.setMostrarAbaConcentO2(ema.getIndFormaRespiracao());
			vo.setMostrarAbaNoAmostras(ema.getIndSolicInformaColetas());
			vo.setMostrarAbaIntervColeta(ema.getIndUsaIntervaloCadastrado());
		}

		Boolean exigeDescMatAnls = ema != null ? ema.getAelMateriaisAnalises() != null ? ema.getAelMateriaisAnalises().getIndExigeDescMatAnls() : false : false;
		vo.setMostrarAbaRegMatAnalise((ema != null && ema.getIndExigeRegiaoAnatomica()) || compareCodigoMaterialExame || exigeDescMatAnls);

		//#2249
		if(DominioOrigemAtendimento.I == origem || DominioOrigemAtendimento.N == origem	
				|| DominioOrigemAtendimento.H == origem	|| DominioOrigemAtendimento.C == origem) {

			vo.setRecomendacaoExameList(this.getAelRecomendacaoExameDAO().
					listarRecomendacaoExameResponsavel(
							unfExecutaExame.getAelExamesMaterialAnalise().getAelExames().getSigla(),
							unfExecutaExame.getAelExamesMaterialAnalise().getId().getManSeq(), 
							DominioOrigemAtendimento.I));
		} else {
			vo.setRecomendacaoExameList(this.getAelRecomendacaoExameDAO().
					listarRecomendacaoExameResponsavel(
							unfExecutaExame.getAelExamesMaterialAnalise().getAelExames().getSigla(),
							unfExecutaExame.getAelExamesMaterialAnalise().getId().getManSeq(), 
							DominioOrigemAtendimento.A));
		}

		if(!vo.getRecomendacaoExameList().isEmpty()) {
			/**
			 * Incidente - AGHU #31531
			 */
			for(AelRecomendacaoExame aux: vo.getRecomendacaoExameList()){
				if(aux.getIndAvisaResp()){
					vo.setMostrarAbaRecomendacoes(Boolean.TRUE);
				}
			}
		}
		
		//FIM #2249
		
		//#2257
		ConvenioExamesLaudosVO convenioExamesLaudosVO = null;
		if (atdSeq != null) {
			convenioExamesLaudosVO = this.getPacienteFacade().buscarConvenioExamesLaudos(atdSeq);
		}
		if (atdDivSeq != null) {
			convenioExamesLaudosVO = this.getExamesFacade().rnAelpBusConvAtv(atdDivSeq);
		}
//		if (convenioExamesLaudosVO != null && convenioExamesLaudosVO.getCodigoConvenioSaude() != null) {
//
//		}
		if (!indGeradoAutomatico && convenioExamesLaudosVO != null && convenioExamesLaudosVO.getCodigoConvenioSaude() != null) {
			Boolean executorDigLaudoApac = false;
			Boolean executorDigLaudoSiscolo = false;
//			if (unfSolicitante != null) { #24826 Não é UNF SOLICITANTE, é UNF_TRABALHO!!
			// Deve-se verificar se o usuário logado possui unidade executora identificada
			if (this.usuarioPossuiUnidExecutoraIdentificada()) {
				/*
  --
  -- se for solicitado por area executora, só vou soltar tela questionario
  -- se o exame for cobrado por APAC ou for do programa SISCOLO do SUS
  --
				 */
				final Short tipoContaSus = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico()
						.shortValue();

				executorDigLaudoApac = this.verificarLaudoApac(convenioExamesLaudosVO.getCodigoConvenioSaude(),
						convenioExamesLaudosVO.getCodigoConvenioSaudePlano(), unfExecutaExame.getAelExamesMaterialAnalise().getAelExames().getSigla(),
						unfExecutaExame.getAelExamesMaterialAnalise().getId().getManSeq(), tipoContaSus);
				executorDigLaudoSiscolo = this.verificarLaudoSiscolo(convenioExamesLaudosVO.getCodigoConvenioSaude(),
						convenioExamesLaudosVO.getCodigoConvenioSaudePlano(), unfExecutaExame.getAelExamesMaterialAnalise().getAelExames().getSigla(),
						unfExecutaExame.getAelExamesMaterialAnalise().getId().getManSeq(), null);
			}
			// Se tem que cobrar APAC ou SISCOLO, segue verificação de questionário
			//Se o parametro que ativa a verificação estiver ativo
			if (removerValidacaoApacSiscolo().equals(true)) {
				verificarQuestionarioExistente(unfExecutaExame, origem,
						includeUnidadeTrabalho, tipoTransporte, vo,
						convenioExamesLaudosVO, executorDigLaudoApac,
						executorDigLaudoSiscolo);
			} else {
				if (executorDigLaudoApac || executorDigLaudoSiscolo) {
					verificarQuestionarioExistente(unfExecutaExame, origem,
							includeUnidadeTrabalho, tipoTransporte, vo,
							convenioExamesLaudosVO, executorDigLaudoApac,
							executorDigLaudoSiscolo);
				}
			}
		}
		//FIM #2257
		vo.setMostrarAbaQuestionarioSismama(mostrarAbaQuestionarioSismama(solicitacao, convenioExamesLaudosVO, unfExecutaExame));
		vo.setMostrarAbaQuestionarioSismamaBiopsia(mostrarAbaQuestionarioSismamaBiopsia(solicitacao, convenioExamesLaudosVO, unfExecutaExame));
		return vo;
	}
	
	public void verificarQuestionarioExistente(AelUnfExecutaExames unfExecutaExame, DominioOrigemAtendimento origem, final Boolean includeUnidadeTrabalho, final DominioTipoTransporteQuestionario tipoTransporte,
			AbasIndicadorApresentacaoVO vo,
			ConvenioExamesLaudosVO convenioExamesLaudosVO,
			Boolean executorDigLaudoApac, Boolean executorDigLaudoSiscolo) {
			final List<AelQuestionarios> aelQuestionarios = this.getQuestionarioExamesFacade().pesquisarQuestionarioPorItemSolicitacaoExame(
					unfExecutaExame.getAelExamesMaterialAnalise().getAelExames().getSigla(),
					unfExecutaExame.getAelExamesMaterialAnalise().getId().getManSeq(), convenioExamesLaudosVO.getCodigoConvenioSaude(), origem, tipoTransporte);
			if (aelQuestionarios != null && !aelQuestionarios.isEmpty()) {
				vo.setMostrarAbaQuestionario(true);
				vo.setQuestionarios(aelQuestionarios);
			}
	}
	
	public Boolean removerValidacaoApacSiscolo() {
		boolean retorno = false;
		try{
			AghParametros validacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_DESATIVA_VALIDACAO_APAC_SISCOLO);
			String validacaoApacSiscolo = validacao.getVlrTexto();
			retorno = "S".equalsIgnoreCase(validacaoApacSiscolo);
		}catch (ApplicationBusinessException e){
				LOG.error(e.getMessage(),e);
		}
			return retorno;
	}
	
	public boolean usuarioPossuiUnidExecutoraIdentificada(){
		boolean possuiUnidExecIdentificada = false;
		if (cascaFacade.temPermissao(obterLoginUsuarioLogado(), ELABORAR_SOLICITACAO_EXAME_CONSULTA_ANTIGA, "executor")){
			try {
				AelUnidExecUsuario aelUnidExecUsuario = examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
				if (aelUnidExecUsuario != null){
					possuiUnidExecIdentificada = true;
				}
			} catch (ApplicationBusinessException e) {
				possuiUnidExecIdentificada = false;
				LOG.error(e.getMessage(),e);
			}
		}
		
		return possuiUnidExecIdentificada;
	}
	
	/**
	 * @ORADB AELP_CHAMA_QUT_SISMAMA
	 * Estória #18450
	 * @return boolean
	 * @throws ApplicationBusinessException 
	 */
	protected Boolean mostrarAbaQuestionarioSismama(AelSolicitacaoExames solicitacao, ConvenioExamesLaudosVO convenioExamesLaudosVO,
			AelUnfExecutaExames unfExecutaExame) throws BaseException {
		if (convenioExamesLaudosVO != null && convenioExamesLaudosVO.getCodigoConvenioSaude() != null) {
			final Short pagadorSus = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS).getVlrNumerico()
					.shortValue();
			FatConvenioSaude convenio = getFaturamentoFacade().obterConvenioSaude(convenioExamesLaudosVO.getCodigoConvenioSaude());
			if(convenio.getPagador() != null && pagadorSus.equals(convenio.getPagador().getSeq())) {
				if(getExamesFacade().obterOrigemIg(solicitacao).equals("AMB")) {
					final String exameTransoper = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EXAME_TRANSOPER).getVlrTexto();
					final String exameImunoHisto = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EXAME_IMUNO_HISTO).getVlrTexto();
					if(!exameTransoper.equals(unfExecutaExame.getAelExamesMaterialAnalise().getAelExames().getSigla())
							&& !exameImunoHisto.equals(unfExecutaExame.getAelExamesMaterialAnalise().getAelExames().getSigla())) {
						if(getAghuFacade().validarCaracteristicaDaUnidadeFuncional(
								unfExecutaExame.getId().getUnfSeq().getSeq(), ConstanteAghCaractUnidFuncionais.EXAME_SISMAMA_MAMO)) {
							
							AelAgrpPesquisas agrpPesquisa = getAelAgrpPesquisasDAO().obterAelAgrpPesquisasPorDescricao("SISMAMA");
							List<AelAgrpPesquisaXExame> listAgrpPesquisaXExame = getAelAgrpPesquisaXExameDAO().buscarAelAgrpPesquisaXExame(
									unfExecutaExame.getAelExamesMaterialAnalise().getAelExames(), 
									unfExecutaExame.getAelExamesMaterialAnalise().getAelMateriaisAnalises(), unfExecutaExame.getUnidadeFuncional(),
									agrpPesquisa, DominioSituacao.A);
							if(listAgrpPesquisaXExame != null && listAgrpPesquisaXExame.size() > 0) {
								return true;
							}
						}
					}
					
				}
				
			}
			
		}
		return false;
	}
	
	
	/**
	 * @ORADB AELP_CHAMA_QUT_SISMAMA
	 * Estória #18450
	 * @return boolean
	 * @throws ApplicationBusinessException 
	 */
	protected Boolean mostrarAbaQuestionarioSismamaBiopsia(AelSolicitacaoExames solicitacao, ConvenioExamesLaudosVO convenioExamesLaudosVO,
			AelUnfExecutaExames unfExecutaExame) throws BaseException {
		if (convenioExamesLaudosVO != null && convenioExamesLaudosVO.getCodigoConvenioSaude() != null) {
			final Short pagadorSus = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS).getVlrNumerico()
					.shortValue();
			FatConvenioSaude convenio = getFaturamentoFacade().obterConvenioSaude(convenioExamesLaudosVO.getCodigoConvenioSaude());
			if(convenio.getPagador() != null && pagadorSus.equals(convenio.getPagador().getSeq())) {
				if(getExamesFacade().obterOrigemIg(solicitacao).equals("AMB")) {
					final String exameTransoper = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EXAME_TRANSOPER).getVlrTexto();
					final String exameImunoHisto = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EXAME_IMUNO_HISTO).getVlrTexto();
					if(!exameTransoper.equals(unfExecutaExame.getAelExamesMaterialAnalise().getAelExames().getSigla())
							&& !exameImunoHisto.equals(unfExecutaExame.getAelExamesMaterialAnalise().getAelExames().getSigla())) {
						if(getAghuFacade().validarCaracteristicaDaUnidadeFuncional(
								unfExecutaExame.getId().getUnfSeq().getSeq(), ConstanteAghCaractUnidFuncionais.EXAME_SISMAMA_HISTO)) {
							
							AelAgrpPesquisas agrpPesquisa = getAelAgrpPesquisasDAO().obterAelAgrpPesquisasPorDescricao("SISMAMA");
							List<AelAgrpPesquisaXExame> listAgrpPesquisaXExame = getAelAgrpPesquisaXExameDAO().buscarAelAgrpPesquisaXExame(
									unfExecutaExame.getAelExamesMaterialAnalise().getAelExames(), 
									unfExecutaExame.getAelExamesMaterialAnalise().getAelMateriaisAnalises(), unfExecutaExame.getUnidadeFuncional(),
									agrpPesquisa, DominioSituacao.A);
							if(listAgrpPesquisaXExame != null && listAgrpPesquisaXExame.size() > 0) {
								return true;
							}
						}
					}
					
				}
				
			}
			
		}
		return false;
	}
	

	/**
	 * @ORADB FATC_VER_LD_SISCOLO
	 * 
	 * @param codigoConvenioSaude
	 * @param codigoConvenioSaudePlano
	 * @param sigla
	 * @param manSeq
	 * @param tipoContaSus
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public Boolean verificarLaudoSiscolo(final Short codigoConvenioSaude, final Byte codigoConvenioSaudePlano, final String sigla, final Integer manSeq,final Short tipoContaSus) throws ApplicationBusinessException {

		final List<VFatAssociacaoProcedimento> associacaoProcedimentos = this.getFaturamentoFacade().listarAssociacaoProcedimentoParaExameMaterial(
				codigoConvenioSaude, codigoConvenioSaudePlano, sigla, manSeq,tipoContaSus);
		if (associacaoProcedimentos == null || associacaoProcedimentos.isEmpty()) {
			return false;
		}
		final VFatAssociacaoProcedimento associacaoProcedimento = associacaoProcedimentos.get(0);

		final List<FatTipoCaractItens> tcis = this.getFaturamentoFacade().listarTipoCaractItensPorCaracteristica(
				this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COBRA_SISCOLO).getVlrTexto());
		Integer tciSeq = null;
		if (tcis != null && !tcis.isEmpty()) {
			tciSeq = tcis.get(0).getSeq();
		}

		return this.getFaturamentoFacade().verificarCaracteristicaTratamento(null, null, associacaoProcedimento.getId().getIphPhoSeq(),
				associacaoProcedimento.getId().getIphSeq(), tciSeq) != null;
	}


	/**
	 * 
	 * @ORABD FATC_VER_LAUDO_APAC
	 * 
	 * @param codigoConvenioSaude
	 * @param codigoConvenioSaudePlano
	 * @param sigla
	 * @param manSeq
	 * @param tipoContaSus 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarLaudoApac(final Short codigoConvenioSaude, final Byte codigoConvenioSaudePlano, final String sigla, final Integer manSeq,
			final Short tipoContaSus) throws ApplicationBusinessException {

		final List<VFatAssociacaoProcedimento> associacaoProcedimentos = this.getFaturamentoFacade().listarAssociacaoProcedimentoParaExameMaterial(
				codigoConvenioSaude, codigoConvenioSaudePlano, sigla, manSeq, tipoContaSus);
		if (associacaoProcedimentos == null || associacaoProcedimentos.isEmpty()) {
			return false;
		}
		final VFatAssociacaoProcedimento associacaoProcedimento = associacaoProcedimentos.get(0);
		if (("S".equals(associacaoProcedimento.getIphIndProcPrincipalApac()) && "S".equals(associacaoProcedimento.getId().getIphIndCobrancaApac()))
				|| "S".equals(associacaoProcedimento.getIphCobraBpi())) {
			return true;
		}
		return false;
	}

	/**
	 * Busca parametro sistema. Utiliza o campo texto.
	 * 
	 * @param enumParam
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private String buscaParametroSistemaValorTexto(AghuParametrosEnum enumParam) throws ApplicationBusinessException {
		AghParametros param = getParametroFacade().buscarAghParametro(enumParam);
		if (param != null) {
			return param.getVlrTexto();			
		}
		return null;
	}

	/**
	 * Busca parametro sistema. Utiliza o campo valor numerico.
	 * 
	 * @param enumParam
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private BigDecimal buscaParametroSistemaValorNumerico(AghuParametrosEnum enumParam) throws ApplicationBusinessException {
		AghParametros param = getParametroFacade().buscarAghParametro(enumParam);
		if (param != null) {
			return param.getVlrNumerico();			
		}
		return null;
	}

	/**
	 * Pesquisa por AelUnfExecutaExames que tenha AelSinonimoExame.nome igual ao parametro nomeExame.<br>
	 * Atraves da associacao obrigatorio entre as entidades:<br>
	 * <p>
	 * AelExamesMaterialAnalise -> AelMateriaisAnalise
	 *                          -> AelExames -> AelSinonimoExame
	 *                          -> AelUnfExecutaExames -> AghUnidadesFuncionais
	 * </p>
	 * 
	 * @param nomeExame
	 * @param seqUnidade
	 * @param isSus
	 * @param buscaCompleta 
	 * @return list of UnfExecutaSinonimoExameVO
	 */
	public List<ExameSuggestionVO> pesquisaUnidadeExecutaSinonimoExame(String nomeExame, Short seqUnidade, Boolean isSus, Boolean isOrigemInternacao, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean buscaCompleta, DominioTipoPesquisaExame tipoPesquisa) {
		RapServidoresId idServidor = null;
		List<String> siglasFiltro = null;

		if (filtrarExamesProcEnfermagem){
			RapServidores servidorLogado;
			try {
				servidorLogado = servidorLogadoFacade.obterServidorLogado();
				idServidor = servidorLogado.getId();
			} catch (Exception e) {
				idServidor = null;
			}
			
			if (isOrigemInternacao){
				siglasFiltro = getAelUnfExecutaExamesDAO().buscaSiglasFiltroProtocEnf(idServidor);
			} else {			
				siglasFiltro = getAelUnfExecutaExamesDAO().buscaSiglasFiltroProtocEnfAgenda(seqAtendimento,idServidor);
			}
			if(siglasFiltro == null || siglasFiltro.isEmpty()){
				return new ArrayList<ExameSuggestionVO>(); 
			}
		}
		List<ExameSuggestionVO> lista = null;
		if (isHCPA()){
		    lista = getAelUnfExecutaExamesDAO().pesquisaExamesSuggestion(nomeExame, siglasFiltro, buscaCompleta, tipoPesquisa);
		    if (lista != null && !lista.isEmpty()) {
		    	lista = filtrarPorNome(lista, nomeExame, tipoPesquisa);
			}
		}else {
		    lista = getAelUnfExecutaExamesDAO().pesquisaUnidadeExecutaSinonimoExame(nomeExame,seqUnidade,isSus, idServidor, seqAtendimento);
		}
		return lista;
		
	}
	
	private List<ExameSuggestionVO> filtrarPorNome(List<ExameSuggestionVO> listaExames, String nomeExame, DominioTipoPesquisaExame tipoPesquisa) {
	    List<ExameSuggestionVO> retornoSuggestion = new ArrayList<ExameSuggestionVO>();
		String filtroNomeExame = removeCaracteresDiferentesAlfabetoEacentos(nomeExame.toUpperCase());
		for (ExameSuggestionVO vo : listaExames) {
			if (vo.getExameSigla().equalsIgnoreCase(filtroNomeExame)){
				retornoSuggestion.add(vo);
			}
		}
		
		for (ExameSuggestionVO vo : listaExames) {
			Boolean match = false;
			if (DominioTipoPesquisaExame.INICIO.equals(tipoPesquisa)) {
				
				if (vo.getExaDescricao().trim().startsWith(filtroNomeExame)){
					match = true;
				}
				if (!match){
					String[] exames = vo.getSinonimosNaoFormatado().substring(vo.getSinonimosNaoFormatado().indexOf("@@@")+4).split("(\\*\\*\\*)");
					
					for (String exame : exames) {
						if (exame.trim().startsWith(filtroNomeExame)) {
							match = true;
							break;
						}
					}
				}
			}
				
			if ((vo != null && vo.getSinonimosNaoFormatado() != null && vo.getSinonimosNaoFormatado().contains(filtroNomeExame) && DominioTipoPesquisaExame.QUALQUER.equals(tipoPesquisa)) 
					|| (match && DominioTipoPesquisaExame.INICIO.equals(tipoPesquisa))) {
				if (vo.getExameSigla().equalsIgnoreCase(filtroNomeExame)){
					continue;
				} else {
					retornoSuggestion.add(vo);
				}
				if (retornoSuggestion.size() == 100){
					break;
				}
			}
		}
		return retornoSuggestion;
	}
	
	private String removeCaracteresDiferentesAlfabetoEacentos(String texto) {
		texto = StringUtil.normaliza(texto);
		final String alfabeto = "abcdefghijklmnopqrstuvxywz0123456789-";
		StringBuffer novoTexto = new StringBuffer();
		for (int x = 0; x < texto.length(); x++) {
			if (texto.substring(x, x + 1).equals(" ") || alfabeto.contains(texto.substring(x, x + 1).toLowerCase())) {
				novoTexto.append(texto.substring(x, x + 1));
			}
			if (texto.substring(x, x + 1).equals("/") && (x + 1 < texto.length()) && !texto.substring(x + 1, x + 2).equals(" ")) {
				novoTexto.append(' ');
			}
		}

		return novoTexto.toString();
	}
	
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameAntigo(String nomeExame, Short seqUnidade, Boolean isSus, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem) {
	    RapServidoresId idServidor = null;

		if (filtrarExamesProcEnfermagem){
			RapServidores servidorLogado;
			try {
				servidorLogado = servidorLogadoFacade.obterServidorLogado();
				idServidor = servidorLogado.getId();
			} catch (Exception e) {
				idServidor = null;
			}
		}
		List<Object[]> lista = getAelUnfExecutaExamesDAO().pesquisaUnidadeExecutaSinonimoExameAntigo(nomeExame,seqUnidade,isSus, idServidor, seqAtendimento);
	    return getListUnfExecutaSinonimoExame(lista);
	}
	
	/**
	 * Pesquisa por AelUnfExecutaExames que tenha AelSinonimoExame.nome igual ao parametro nomeExame.<br>
	 * Atraves da associacao obrigatorio entre as entidades:<br>
	 * <p>
	 * AelExamesMaterialAnalise -> AelMateriaisAnalise
	 *                          -> AelExames -> AelSinonimoExame
	 *                          -> AelUnfExecutaExames -> AghUnidadesFuncionais
	 * </p>
	 * 
	 * @param nomeExame
	 * @return list of UnfExecutaSinonimoExameVO
	 */
	public List<ExameSuggestionVO> pesquisaUnidadeExecutaSinonimoExame(String nomeExame, DominioTipoPesquisaExame tipoPesquisa) {
		return pesquisaUnidadeExecutaSinonimoExame(nomeExame,null,false, false, null, false, false, tipoPesquisa);
	}
	
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameAntigo(String nomeExame) {
		return pesquisaUnidadeExecutaSinonimoExameAntigo(nomeExame,null,false, null, false);
	}
	
//	/**
//	 * Pesquisa Count por AelUnfExecutaExames que tenha AelSinonimoExame.nome igual ao parametro nomeExame.<br>
//	 * Atraves da associacao obrigatorio entre as entidades:<br>
//	 * <p>
//	 * AelExamesMaterialAnalise -> AelMateriaisAnalise
//	 *                          -> AelExames -> AelSinonimoExame
//	 *                          -> AelUnfExecutaExames -> AghUnidadesFuncionais
//	 * </p>
//	 * 
//	 * @param nomeExame
//	 * @param seqUnidade
//	 * @param isSus
//	 * @return list of UnfExecutaSinonimoExameVO
//	 */
//	public Long pesquisaUnidadeExecutaSinonimoExameCount(String nomeExame,Short seqUnidade, Boolean isSus, Boolean isOrigemInternacao, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem) {
//		RapServidoresId idServidor = null;
//		List<String> siglasFiltro = null;
//		if (filtrarExamesProcEnfermagem){
//			RapServidores servidorLogado;
//			try {
//			    	servidorLogado = servidorLogadoFacade.obterServidorLogado();
//				idServidor = servidorLogado.getId();
//			} catch (Exception e) {
//				idServidor = null;
//			}
//			
//			if (isOrigemInternacao){
//				siglasFiltro = getAelUnfExecutaExamesDAO().buscaSiglasFiltroProtocEnf(idServidor);
//			} else {			
//				siglasFiltro = getAelUnfExecutaExamesDAO().buscaSiglasFiltroProtocEnfAgenda(seqAtendimento,idServidor);
//			}
//			if(siglasFiltro == null || siglasFiltro.isEmpty()){
//				return 0L;
//			}
//		}
//		if (isHCPA()){
//		    return getAelUnfExecutaExamesDAO().pesquisaExamesSuggestionCount(nomeExame, siglasFiltro, tipo);
//		} else {
//		    return getAelUnfExecutaExamesDAO().pesquisaUnidadeExecutaSinonimoExameCountAntigo(nomeExame,seqUnidade,isSus, idServidor,seqAtendimento);
//		}
//	}	

	/**
	 * Pesquisa por AelUnfExecutaExames que pertença ao lote informado por parâmetro.
	 * Atraves da associacao obrigatorio entre as entidades:<br>
	 * <p>
	 * AelExamesMaterialAnalise -> AelMateriaisAnalise
	 *                          -> AelExames -> AelSinonimoExame
	 *                          -> AelUnfExecutaExames -> AghUnidadesFuncionais
	 *                          -> AelPermissaoUnidSolic
	 * </p>
	 * 
	 * @param nomeExame
	 * @param seqUnidadeFuncional
	 * @param isSus
	 * @param isOrigemInternacao 
	 * @return list of UnfExecutaSinonimoExameVO
	 */
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameLote(Short leuSeq, Short seqUnidade, Boolean isSus, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean isOrigemInternacao) {
		RapServidoresId idServidor = null;
		List<String> siglasFiltro = null;
		
		if (filtrarExamesProcEnfermagem){
			RapServidores servidorLogado;
			try {
				servidorLogado = servidorLogadoFacade.obterServidorLogado();
				idServidor = servidorLogado.getId();
			} catch (Exception e) {
				idServidor = null;
			}
			
			if (isOrigemInternacao){
				siglasFiltro = getAelUnfExecutaExamesDAO().buscaSiglasFiltroProtocEnf(idServidor);
			} else {			
				siglasFiltro = getAelUnfExecutaExamesDAO().buscaSiglasFiltroProtocEnfAgenda(seqAtendimento,idServidor);
			}
			if(siglasFiltro == null || siglasFiltro.isEmpty()){
				return new ArrayList<UnfExecutaSinonimoExameVO>(); 
			}
		}
		List<Object[]> lista = getAelUnfExecutaExamesDAO().pesquisaUnidadeExecutaSinonimoExameLote(leuSeq, siglasFiltro, seqUnidade, isSus, seqAtendimento);
		
		List<UnfExecutaSinonimoExameVO> listaUnfExecutaSinonimoExameVO = getListUnfExecutaSinonimoExame(lista);
		
		List<UnfExecutaSinonimoExameVO> listaSemDuplicado = retonarListaUnfExecutaExameVOSemDuplicados(listaUnfExecutaSinonimoExameVO);
				
		return listaSemDuplicado;
	}
	
	public List<UnfExecutaSinonimoExameVO> retonarListaUnfExecutaExameVOSemDuplicados(List<UnfExecutaSinonimoExameVO> listaUnfExecutaSinonimoExameVO){
		List<UnfExecutaSinonimoExameVO> listaSemDuplicado = new ArrayList<>();
		
		for (UnfExecutaSinonimoExameVO unfExecExame : listaUnfExecutaSinonimoExameVO){
			if (!contemExame(listaSemDuplicado, unfExecExame)){
				listaSemDuplicado.add(unfExecExame);
			}
		}
		
		return listaSemDuplicado;
	}
	
	private boolean contemExame(List<UnfExecutaSinonimoExameVO> listaExames, UnfExecutaSinonimoExameVO exame){
		boolean contem = false;
		
		for (UnfExecutaSinonimoExameVO item : listaExames){
			if (item.getDescricaoExameFormatada().equals(exame.getDescricaoExameFormatada())){
				contem = true;
				break;
			}
		}
		
		return contem;
	}
	
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameLoteSemPermissoes(Short leuSeq, List<UnfExecutaSinonimoExameVO> listaExames, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean isOrigemInternacao) {
//		List<UnfExecutaSinonimoExameVO> returnValue = new LinkedList<UnfExecutaSinonimoExameVO>();
		List<UnfExecutaSinonimoExameVO> listaVO = new LinkedList<UnfExecutaSinonimoExameVO>();
		
		RapServidoresId idServidor = null;
		List<String> siglasFiltro = null ;
		
		if (filtrarExamesProcEnfermagem){
			RapServidores servidorLogado;
			try {
				servidorLogado = servidorLogadoFacade.obterServidorLogado();
				idServidor = servidorLogado.getId();
			} catch (Exception e) {
				idServidor = null;
			}
			
			if (isOrigemInternacao){
				siglasFiltro  = getAelUnfExecutaExamesDAO().buscaSiglasFiltroProtocEnf(idServidor);
			} else {			
				siglasFiltro = getAelUnfExecutaExamesDAO().buscaSiglasFiltroProtocEnfAgenda(seqAtendimento,idServidor);
			}
			if(siglasFiltro == null || siglasFiltro.isEmpty()){
				return new ArrayList<UnfExecutaSinonimoExameVO>(); 
			}
		}
		
		// Setamos os parâmetros de Unidade Funcional e convenio Sus, justamente para retornar lista de exames sem considerar as permissões
		List<Object[]> lista = getAelUnfExecutaExamesDAO().pesquisaUnidadeExecutaSinonimoExameLote(leuSeq, siglasFiltro, null, false, seqAtendimento);
		
		listaVO = getListUnfExecutaSinonimoExame(lista);
		
		List<UnfExecutaSinonimoExameVO> listaSemDuplicado = retonarListaUnfExecutaExameVOSemDuplicados(listaVO);
		
//		for (UnfExecutaSinonimoExameVO vo : listaVO) {
//			if (!listaExames.contains(vo)){
//				returnValue.add(vo);
//			}
//		}
		
		return listaSemDuplicado;
	}
	
//	private List<UnfExecutaSinonimoExameVO> getListUnfExecutaSinonimoExameEntidade(List<AelUnfExecutaExames> lista) {
//		List<UnfExecutaSinonimoExameVO> returnValue = new LinkedList<UnfExecutaSinonimoExameVO>(); 
//		
//		for (AelUnfExecutaExames aelUnfExecutaExames : lista) {
//			UnfExecutaSinonimoExameVO vo = new UnfExecutaSinonimoExameVO(aelUnfExecutaExames);
//
//			returnValue.add(vo);
//		}
//		return returnValue;
//	}
	
	private List<UnfExecutaSinonimoExameVO> getListUnfExecutaSinonimoExame(List<Object[]> lista) {
		List<UnfExecutaSinonimoExameVO> returnValue = new LinkedList<UnfExecutaSinonimoExameVO>(); 
		
		for (Object[] listFileds : lista) {
			AelUnfExecutaExames unfExecutaExame = (AelUnfExecutaExames) listFileds[0];
			AelSinonimoExame sinonimoExame = (AelSinonimoExame) listFileds[1];

			UnfExecutaSinonimoExameVO vo = new UnfExecutaSinonimoExameVO(unfExecutaExame, sinonimoExame);

			returnValue.add(vo);
		}
		return returnValue;
	}

	/**
	 * Pesquisa por AelUnfExecutaExames que não contem permissão para lote informado por parâmetro.
	 * Atraves da associacao obrigatorio entre as entidades:<br>
	 * <p>
	 * AelExamesMaterialAnalise -> AelMateriaisAnalise
	 *                          -> AelExames -> AelSinonimoExame
	 *                          -> AelUnfExecutaExames -> AghUnidadesFuncionais
	 *                          -> AelPermissaoUnidSolic
	 * </p>
	 * 
	 * @param nomeExame
	 * @param listaExames
	 * @return list of UnfExecutaSinonimoExameVO
	 */
	
	/**
	 * Verifica qual tipo de campo deve ser desenhado na tela.
	 * @param itemSolicitacaoExameVO
	 * @param unidadeSolicitante
	 * @return tipo de campo que deve desenhar: TipoCampoDataHoraISE.COMBO ou TipoCampoDataHoraISE.CALENDAR
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public TipoCampoDataHoraISE verificarCampoDataHora(ItemSolicitacaoExameVO itemSolicitacaoExameVO, AghUnidadesFuncionais unidadeSolicitante) throws ApplicationBusinessException {
		TipoCampoDataHoraISE campo = TipoCampoDataHoraISE.CALENDAR;

		AelUnfExecutaExamesId executaExamesId = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getId();
		
		AelPermissaoUnidSolic aelPermissaoUnidSolic = getAelPermissaoUnidSolicDAO()
				.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
						executaExamesId.getEmaExaSigla(), executaExamesId.getEmaManSeq(), executaExamesId.getUnfSeq().getSeq(), unidadeSolicitante.getSeq());

		DesenhaDataHoraISE desenho = getVerificarPermissoesExame();
		DesenhaDataHoraISE desenho2 = getVerificarPermissaoHorariosRotina();
		desenho2.setAelPermissaoUnidSolic(aelPermissaoUnidSolic);
		
		desenho.setSuccessor(desenho2);

		//Verifica se o atendimento é do SUS.
		FatConvenioSaude convenio =null;
		if(itemSolicitacaoExameVO.getSolicitacaoExameVO()!=null){
			convenio = this.getItemSolicitacaoExameRN().obterFatConvenioSaude(itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento(), itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimentoDiverso());
		}
		if (aelPermissaoUnidSolic == null && (convenio !=null && DominioGrupoConvenio.S == convenio.getGrupoConvenio())) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_00426);
		} else {
			campo = desenho.processRequest(itemSolicitacaoExameVO);
		}

		return campo;
	}



	public List<ExamesCriteriosSelecionadosVO> pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(
			Integer firstResult, Integer maxResult, String ordem, boolean b,
			AghUnidadesFuncionais aelUnfExecutaExames,
			AelSolicitacaoExames aelSolicitacaoExames,
			AelSitItemSolicitacoes aelSitItemSolicitacoes,
			FatConvenioSaude fatConvenioSaude, String codigoPaciente, Integer prontuario2, String nomePaciente2) {

		List<AelItemSolicitacaoExames> itensSol = getAelItemSolicitacaoExameDAO().pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(firstResult, maxResult, ordem, b, aelUnfExecutaExames,aelSolicitacaoExames,aelSitItemSolicitacoes,fatConvenioSaude,codigoPaciente,prontuario2,nomePaciente2);
		List<ExamesCriteriosSelecionadosVO> listVo = new ArrayList<ExamesCriteriosSelecionadosVO>();
		for(AelItemSolicitacaoExames item : itensSol){
			ExamesCriteriosSelecionadosVO vo = new ExamesCriteriosSelecionadosVO();
			AelSolicitacaoExames solicitacao = item.getSolicitacaoExame();
			vo.setSolicitacao(item.getId().getSoeSeq().toString());

			String vAelSolicAtends = getVAelSolicAtendsDAO().buscarVAelSolicAtendsPorSoeSeq(item.getId().getSoeSeq());
			if(vAelSolicAtends != null) {
				vo.setConvenio(vAelSolicAtends);
			}
			
			String prontuario = getExamesFacade().buscarLaudoProntuarioPaciente(solicitacao);
			vo.setProntuario(prontuario);

			String codPaciente = null;
			if(solicitacao.getAtendimento() != null){
				AipPacientes paciente = solicitacao.getAtendimento().getPaciente();
				codPaciente = paciente != null ? paciente.getCodigo().toString() : null;
			}else if(solicitacao.getAtendimentoDiverso() != null){
				AipPacientes paciente = solicitacao.getAtendimentoDiverso().getAipPaciente();
				codPaciente = paciente != null ? paciente.getCodigo().toString() : null;
			}
			vo.setPaciente(codPaciente);

			String nomePaciente = getExamesFacade().buscarLaudoNomePaciente(solicitacao);
			vo.setNome(nomePaciente);

			vo.setUnid(item.getUnidadeFuncional().getSeq().toString());

			String descricao = item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelExames().getDescricaoUsual();			
			vo.setExame(descricao);

			if(item.getSituacaoItemSolicitacao() != null) {
				vo.setSit(item.getSituacaoItemSolicitacao().getCodigo());
			}
			
			if(item.getTipoColeta() != null) {
				vo.setUrg(item.getTipoColeta().getDescricao());
			}
			
			vo.setO2(item.getIndUsoO2Un() != null &&  item.getIndUsoO2Un()? "Sim" : "Não");

			if(item.getTipoTransporteUn() != null) {
				vo.setTransporte(item.getTipoTransporteUn().getDescricao());
			}
			
			vo.setAelItemSolicitacaoExames(item);

			listVo.add(vo);
		}

		return listVo;
	}


	public Long pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionadosCount(
			AghUnidadesFuncionais aelUnfExecutaExames,
			AelSolicitacaoExames aelSolicitacaoExames,
			AelSitItemSolicitacoes aelSitItemSolicitacoes,
			FatConvenioSaude fatConvenioSaude, String codigoPaciente,
			Integer prontuario, String nomePaciente) {
		return getAelItemSolicitacaoExameDAO().pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionadosCount(aelUnfExecutaExames,aelSolicitacaoExames,aelSitItemSolicitacoes,fatConvenioSaude,codigoPaciente,prontuario,nomePaciente);
	}



	/**
	 * Retorna a lista que irá popular a combo de data/hora de rotinas de coleta ao solicitar um ítem de exame.
	 * @return List<DataProgramadaVO> listaHorariosRotina
	 * @throws ApplicationBusinessException
	 */
	public List<DataProgramadaVO> getHorariosRotina(ItemSolicitacaoExameVO itemSolicitacaoExameVO, AghUnidadesFuncionais unidadeSolicitante) throws BaseException {		
		List<DataProgramadaVO> datasRotina = new ArrayList<DataProgramadaVO>();

		Date dataLimite = getDataLimite(getParametroRotinaProgrInt());
		Date dataCorrente = new Date();
		while (DateValidator.validarMesmoDia(dataCorrente, dataLimite) ||  (dataCorrente.before(dataLimite) || dataCorrente.equals(dataLimite))) {
			AghFeriados feriado = getAghuFacade().obterFeriado(dataCorrente);

			HorariosRotinaColeta coleta = getHorariosRotinaColetaFactory().getFactory(feriado);

			AelPermissaoUnidSolic permissao = null;			
			
			if(itemSolicitacaoExameVO.getUnfExecutaExame() != null && itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame() != null) {
				AelUnfExecutaExamesId execExamId = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getId();
				permissao = getAelPermissaoUnidSolicDAO()
						.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
								execExamId.getEmaExaSigla(), execExamId.getEmaManSeq(), execExamId.getUnfSeq().getSeq(), unidadeSolicitante.getSeq());
			}
			
			AghUnidadesFuncionais unfSeqAvisa = permissao == null ? null : permissao.getUnfSeqAvisa();

			List<DataProgramadaVO> datas = coleta.getHorariosRotinaColeta(unidadeSolicitante, unfSeqAvisa, dataCorrente, dataLimite);
			if (datas != null && !datas.isEmpty()) {
				datasRotina.addAll(datas);
			}

			dataCorrente = incrementaData(dataCorrente);
		}
		return datasRotina;
	}
	
	protected HorariosRotinaColetaFactory getHorariosRotinaColetaFactory() {
		return horariosRotinaColetaFactory;
	}
	
	private Date incrementaData(Date dataCorrente) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataCorrente);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	/**
	 * Retorna a data limite para a busca de data/hora de rotina.
	 * @param parametroRotinaProgrInt AghParametro, em que o getVlrNumerico representa os minutos que devem ser somados.
	 * @return
	 */
	protected Date getDataLimite(AghParametros parametroRotinaProgrInt) {
		Calendar cal = Calendar.getInstance();
		if (parametroRotinaProgrInt != null && parametroRotinaProgrInt.getVlrNumerico() != null) {
			cal.add(Calendar.HOUR_OF_DAY, parametroRotinaProgrInt.getVlrNumerico().intValue());
		}

		return cal.getTime();	
	}


	/**
	 * Verifica se a unidade solicitante é de Urgência e se o item é gerado automaticamente
	 * Se o unidade NÃO for de urgência e o item NÃO foi gerado automaticamente então deve 
	 * retornar verdadeiro, senão falso. (RN1)
	 * 
	 * SOMENTE EXECUTADO NO ADICIONAR.
	 *   
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @return {Boolean}
	 */
	protected Boolean verificarNecessidadeExamesJaSolic(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {

		Boolean response = false;

		AghUnidadesFuncionais unfSolicitante = itemSolicitacaoExameVO.getSolicitacaoExameVO().getUnidadeFuncional();
		if (unfSolicitante != null) {

			DominioSimNao caracteristica = DominioSimNao.N;
			Boolean indGeradoAutomatico = itemSolicitacaoExameVO.getIndGeradoAutomatico();

			caracteristica = getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
					unfSolicitante.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_URGENCIA
			);

			if (!DominioSimNao.S.equals(caracteristica) && !indGeradoAutomatico) {

				response = true;
			}
		}
		return response;
	}

	/**
	 * ORADB AELP_VER_EXAME_JA_SOLIC (RN2). 
	 * 
	 * Verifica e pede confirmação se há exames solicitados dentro do intervalo 
	 * sugerido entre solicitações (excluindo as unidades de urgência).
	 * 
	 * SOMENTE EXECUTADO NO ADICIONAR.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @return {ApplicationBusinessException}
	 */
	protected ApplicationBusinessException verificarExameJaSolicitado(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {

		ApplicationBusinessException mensagem = null;

		AelExamesMaterialAnalise examesMaterialAnalise = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise();
		if (examesMaterialAnalise  != null) {

			Boolean indLimitaSolic = examesMaterialAnalise.getIndLimitaSolic();
			if (!indLimitaSolic) {

				Short intervaloMinTempoSolic = examesMaterialAnalise.getIntervaloMinTempoSolic();
				if (intervaloMinTempoSolic != null) {

					Date dthrProgramada = DateUtil.adicionaDias(itemSolicitacaoExameVO.getDataProgramada(), -(intervaloMinTempoSolic / 24));
					AghAtendimentos atendimento = itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento();
					if (atendimento != null) {
						Integer pacCodigo = atendimento.getPaciente().getCodigo();
						String ufeEmaExaSigla = examesMaterialAnalise.getAelExames().getSigla();
						Integer ufeEmaManSeq = examesMaterialAnalise.getAelMateriaisAnalises().getSeq();

						Integer soeSeqMax = this.getSolicitacaoExameDAO().buscaSolicitacaoExameSeqMax(pacCodigo, null, ufeEmaExaSigla, ufeEmaManSeq, dthrProgramada);
						if (soeSeqMax != null) {
							AelSolicitacaoExames solicitacaoExames = this.getSolicitacaoExameDAO().obterPeloId(soeSeqMax);
							String descricaoUsualExame = examesMaterialAnalise.getAelExames().getDescricaoUsual();
							String nomeServidor = solicitacaoExames.getServidorResponsabilidade() != null ? solicitacaoExames.getServidorResponsabilidade().getPessoaFisica().getNome() : "";
							mensagem = new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.MENSAGEM_CONFIRMA_ADD_EXAME_SOLICITACAO, br.gov.mec.aghu.core.exception.Severity.WARN, descricaoUsualExame, nomeServidor, soeSeqMax);
						}
					}
				}
			}
		}
		return mensagem;
	}

	/**
	 * Verifica se a origem do atendimento é de Internação (I) OU Nascimento (N)
	 * e se o item NÃO foi gerado automaticamente, então deve 
	 * retornar verdadeiro, senão falso (RN3).
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @return {Boolean}
	 */
	protected Boolean verificarNecessidadeLimitaExames(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {

		Boolean response = false;

		AghAtendimentos atendimento = itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento();
		if (atendimento != null) {

			DominioOrigemAtendimento origem = atendimento.getOrigem();
			if ((DominioOrigemAtendimento.I.equals(origem) || DominioOrigemAtendimento.N.equals(origem)) && !itemSolicitacaoExameVO.getIndGeradoAutomatico()) {

				response = true;
			}
		}
		return response;
	}


	/**
	 * ORADB AELP_VER_LIMITA_EXAMES (RN4). 
	 * 
	 * Verifica para pacientes internados os exames limitados na solicitação.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @return {ApplicationBusinessException}
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarLimitaExames(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws ApplicationBusinessException {

		FatConvenioSaude fatConvenioSaude = this.obterFatConvenioSaude(itemSolicitacaoExameVO.getSolicitacaoExameVO());
		Boolean indGeradoAutomatico = itemSolicitacaoExameVO.getIndGeradoAutomatico();
		if(DominioGrupoConvenio.S.equals(fatConvenioSaude.getGrupoConvenio()) && !indGeradoAutomatico) {

			AelExamesMaterialAnalise examesMaterialAnalise = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise();
			if (examesMaterialAnalise != null && examesMaterialAnalise.getIndLimitaSolic()) {

				SolicitacaoExameVO solicitacaoExameVO = itemSolicitacaoExameVO.getSolicitacaoExameVO();
				AghAtendimentos atendimento = solicitacaoExameVO.getAtendimento();
				if (atendimento != null && 
						this.validarDthrProgramadaMaiorDthrLimite(itemSolicitacaoExameVO)) {

					Date dthrProgramada = itemSolicitacaoExameVO.getDataProgramada();

					String descricaoUnidade = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getUnidadeFuncional().getDescricao();
					if (examesMaterialAnalise.getTempoLimiteSolic() != null) {

						dthrProgramada = this.subtraiTempoColeta(dthrProgramada, examesMaterialAnalise.getUnidTempoLimiteSol(), Double.valueOf(examesMaterialAnalise.getTempoLimiteSolic()));

						Integer pacCodigo = atendimento.getPaciente().getCodigo();
						String ufeEmaExaSigla = examesMaterialAnalise.getAelExames().getSigla();
						Integer ufeEmaManSeq = examesMaterialAnalise.getAelMateriaisAnalises().getSeq();
						Integer soeSeqMax = this.getSolicitacaoExameDAO().buscaSolicitacaoExameSeqMaxSituacaoDiferenteCA(pacCodigo, null, ufeEmaExaSigla, ufeEmaManSeq, dthrProgramada);
						if (soeSeqMax != null) {

							String descUnid = this.verificarDescUnid(itemSolicitacaoExameVO, examesMaterialAnalise.getTempoLimiteSolic(), examesMaterialAnalise.getUnidTempoLimiteSol());
							throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01549B, soeSeqMax, examesMaterialAnalise.getTempoLimiteSolic(), descUnid, descricaoUnidade);
						}
					}

					if (examesMaterialAnalise.getNroAmostrasSolic() != null &&
							itemSolicitacaoExameVO.getNumeroAmostra() > examesMaterialAnalise.getNroAmostrasSolic()) {

						throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01550B, examesMaterialAnalise.getNroAmostrasSolic(), descricaoUnidade);
					}

					if (examesMaterialAnalise.getNroAmostraTempo() != null &&
							examesMaterialAnalise.getTempoLimitePeriodo() != null) {

						dthrProgramada = this.subtraiTempoColeta(dthrProgramada, examesMaterialAnalise.getUnidTempoLimitePeriodo(), Double.valueOf(examesMaterialAnalise.getTempoLimitePeriodo()));
						Integer pacCodigo = atendimento.getPaciente().getCodigo();
						String ufeEmaExaSigla = examesMaterialAnalise.getAelExames().getSigla();
						Integer ufeEmaManSeq = examesMaterialAnalise.getAelMateriaisAnalises().getSeq();
						Integer countAmostras = this.getAmostrasDAO().countAmostras(pacCodigo, null, ufeEmaExaSigla, ufeEmaManSeq, dthrProgramada);
						countAmostras = countAmostras + itemSolicitacaoExameVO.getNumeroAmostra();
						if (countAmostras > examesMaterialAnalise.getNroAmostraTempo()) {

							String descUnid = this.verificarDescUnid(itemSolicitacaoExameVO, examesMaterialAnalise.getTempoLimitePeriodo(), examesMaterialAnalise.getUnidTempoLimitePeriodo());
							throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01551B, examesMaterialAnalise.getNroAmostraTempo(), examesMaterialAnalise.getUnidTempoLimitePeriodo(), descUnid, descricaoUnidade);

						}
					}
				}
			}
		}
	}

	/**
	 * Subtrai na data programada o tempocoleta conforme a unidade de medida de
	 * tempo.<br>
	 * 
	 * @param dthrProgramada
	 * @param unidMedidaTempo
	 * @param tempoColeta
	 * @return
	 */
	protected Date subtraiTempoColeta(Date dthrProgramada,
			DominioUnidTempo unidMedidaTempo, Double tempoColeta) {
		Date returnValue = null;

		if (dthrProgramada != null && unidMedidaTempo != null
				&& tempoColeta != null) {
			if (DominioUnidTempo.H == unidMedidaTempo) {
				returnValue = DateUtil.adicionaHoras(dthrProgramada,
						-tempoColeta.intValue());
			} else { 
				returnValue = DateUtil.adicionaDias(dthrProgramada,
						-tempoColeta.intValue());
			}
		}

		return returnValue;
	}

	/**
	 * Busca um FatConvenioSaude de uma Solicitacao de exames.<br>
	 * Executa regras conforme function <b>AELC VER GRUPO CNV G</b>.
	 * Pode retornar nulo.<br>
	 * 
	 * @param solicitacaoExameVO
	 * @return um FatConvenioSaude.
	 */
	public FatConvenioSaude obterFatConvenioSaude(SolicitacaoExameVO solicitacaoExameVO) {
		FatConvenioSaudePlano convenioSaudeplano = this.obterFatConvenioSaudePlano(solicitacaoExameVO);

		return (convenioSaudeplano != null) ? convenioSaudeplano.getConvenioSaude() : null;
	}

	protected FatConvenioSaudePlano obterFatConvenioSaudePlano(SolicitacaoExameVO solicitacaoExameVO) {
		return getItemSolicitacaoExameRN().obterFatConvenioSaudePlano(solicitacaoExameVO);
	}


	/**
	 * Retorna a descrição da unidade de tempo utilizada.
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @return {String}
	 */
	protected String verificarDescUnid(
			ItemSolicitacaoExameVO itemSolicitacaoExameVO, Short tempoLimite, DominioUnidTempo unidTempo) {

		String descUnid = "";

		AelExamesMaterialAnalise examesMaterialAnalise = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise();
		if (examesMaterialAnalise != null) {

			if (tempoLimite != null) {

				if (DominioUnidTempo.H.equals(unidTempo)) {

					descUnid = DominioUnidTempo.H.getDescricao().toLowerCase();

				} else {

					descUnid = DominioUnidTempo.D.getDescricao().toLowerCase();
				}
			}
		}
		return descUnid;
	}

	/**
	 * Verifica se a origem do atendimento é de Internação (I), Nascimento (N)
	 * OU Urgência (U) e se o item NÃO foi gerado automaticamente, então deve 
	 * retornar verdadeiro, senão falso (RN5).
	 * 
	 * EXECUTA NO ADICIONAR E NO GRAVAR.
	 *   
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @return {Boolean}
	 */
	public Boolean verificarNecessidadeExamesPosAlta(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {

		Boolean response = false;

		AghAtendimentos atendimento = itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento();
		if (atendimento != null) {

			DominioOrigemAtendimento origem = atendimento.getOrigem();
			if ((DominioOrigemAtendimento.I.equals(origem) || 
					DominioOrigemAtendimento.N.equals(origem) || 
					DominioOrigemAtendimento.U.equals(origem)) && 
					!itemSolicitacaoExameVO.getIndGeradoAutomatico()) {

				response = true;
			}
		}
		return response;
	}

	/**
	 * ORADB AELP_VER_EXAMES_POS_ALTA (RN6). 
	 * 
	 * Verifica para pacientes pós-internados/urgência 
	 * permissão de exames.
	 * 
	 * EXECUTA NO ADICIONAR E NO GRAVAR.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @throws ApplicationBusinessException 
	 */
	public void verificarExamesPosAlta(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws BaseException {

		AghAtendimentos atendimento = itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento();
		FatConvenioSaude fatConvenioSaude = this.obterFatConvenioSaude(itemSolicitacaoExameVO.getSolicitacaoExameVO());

		if (atendimento != null && DominioPacAtendimento.N.equals(atendimento.getIndPacAtendimento()) &&
				DominioGrupoConvenio.S.equals(fatConvenioSaude.getGrupoConvenio())) {

			Calendar dthrLimitada = Calendar.getInstance();
			if (atendimento.getDthrFim() != null) {
				dthrLimitada.setTime(atendimento.getDthrFim());
			}

			if (atendimento.getDthrFim() == null || !this.verificarHorasUteis(dthrLimitada.getTime())) {

				AelExamesMaterialAnalise examesMaterialAnalise = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise();
				if (examesMaterialAnalise != null) {

					DominioSimNao permiteSolicAlta = examesMaterialAnalise.getIndPermiteSolicAlta();
					if (DominioSimNao.N.equals(permiteSolicAlta)) {
						if (verificarExistenciaDescricaoUsual(itemSolicitacaoExameVO)){
							String descricaoUsualExame = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelExames().getDescricaoUsual();
							throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01779a, descricaoUsualExame);
						} else {
							throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01779);
						}
					}

					Date atdDthrFim = new Date();
					if (atendimento.getDthrFim() != null) {
						atdDthrFim = atendimento.getDthrFim();
					}

					AghUnidadesFuncionais unidadeTrabalho = itemSolicitacaoExameVO.getSolicitacaoExameVO().getUnidadeTrabalho();
					if (unidadeTrabalho != null) {
						Short tempoSolicAltaCompl = examesMaterialAnalise.getTempoSolicAltaCompl();
						if (tempoSolicAltaCompl == null) {
							tempoSolicAltaCompl = 0;
						}
						atdDthrFim = DateUtil.adicionaDias(atdDthrFim, tempoSolicAltaCompl/24);
					} else {
						Short tempoSolicAlta = examesMaterialAnalise.getTempoSolicAlta();
						if (tempoSolicAlta == null) {
							tempoSolicAlta = 0;
						}
						atdDthrFim = DateUtil.adicionaDias(atdDthrFim, tempoSolicAlta/24);
					}

					Date dthrProgramada = itemSolicitacaoExameVO.getDataProgramada();
					if (DateUtil.validaDataMaior(dthrProgramada, atdDthrFim)) {
						SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						String strDthrFim = sdfHora.format(atdDthrFim);
						throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01780, strDthrFim);
					}
				}
			}
		}
	}
	
	private boolean verificarExistenciaDescricaoUsual(ItemSolicitacaoExameVO itemSolicitacaoExameVO){
		
		return itemSolicitacaoExameVO != null
				&& itemSolicitacaoExameVO.getUnfExecutaExame() != null
				&& itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame() != null
				&& itemSolicitacaoExameVO.getUnfExecutaExame()
						.getUnfExecutaExame().getAelExamesMaterialAnalise() != null
				&& itemSolicitacaoExameVO.getUnfExecutaExame()
						.getUnfExecutaExame().getAelExamesMaterialAnalise().getAelExames() != null
				&& itemSolicitacaoExameVO.getUnfExecutaExame()
						.getUnfExecutaExame().getAelExamesMaterialAnalise()
						.getAelExames().getDescricaoUsual() != null;

	}

	/**
	 * ORADB AELP_VER_HORAS_UTEIS (RN6.3.1). 
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @throws ApplicationBusinessException 
	 */
	protected Boolean verificarHorasUteis(Date dthrAlta) throws BaseException {

		Boolean permite = false;

		if (dthrAlta != null) {
			BigDecimal horasPosAlta = this.buscaParametroSistemaValorNumerico(AghuParametrosEnum.P_HORAS_POS_ALTA);
			if (horasPosAlta != null) {
				Float pHorasPosAlta = horasPosAlta.floatValue();

				Date dataCorrente = new Date();

				if (!DateUtil.validaDataMaiorIgual(dthrAlta, dataCorrente)) {

					if (!pHorasPosAlta.equals(0)) {

						while (pHorasPosAlta > 0) {

							if (DateUtil.isFinalSemana(dthrAlta)) {
								dthrAlta = DateUtil.adicionaDias(DateUtil.truncaData(dthrAlta), 1);
							} else {
								AghFeriados feriados = this.getAghuFacade().obterFeriado(dthrAlta);
								if (feriados != null) {
									dthrAlta = DateUtil.adicionaDias(DateUtil.truncaData(dthrAlta), 1);
								} else {
									Date dthrAltaMaisUm = DateUtil.truncaData(DateUtil.adicionaDias(dthrAlta, 1));
									Float difDaysInHour = DateUtil.diffInDays(dthrAltaMaisUm, dthrAlta)*24;
									pHorasPosAlta = pHorasPosAlta - difDaysInHour;
									dthrAlta = DateUtil.adicionaDias(DateUtil.truncaData(dthrAlta), 1);
								}
							}
						}

						Float pHorasPosAltaDias = pHorasPosAlta / 24;
						dthrAlta = DateUtil.adicionaDias(dthrAlta, pHorasPosAltaDias.intValue());
						if (DateUtil.validaDataMaiorIgual(dthrAlta, dataCorrente)) {
							permite = true;
						}
					}

				} else {

					permite = true;
				}
			}
		}
		return permite;
	}

	/**
	 * Verifica se a origem do atendimento é de Internação (I), Nascimento (N)
	 * OU Ambulatório (A) e a unidade solicitante tiver caracteristica de unidade
	 * de emergência e se o item NÃO foi gerado automaticamente, então deve 
	 * retornar verdadeiro, senão falso (RN7).
	 * 
	 * EXECUTA NO ADICIONAR E NO GRAVAR.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @return {Boolean}
	 */
	protected Boolean verificarNecessidadeExameDesativado(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {

		Boolean response = false;

		AghAtendimentos atendimento = itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento();
		if (atendimento != null) {

			DominioSimNao caracteristica = DominioSimNao.N;
			AghUnidadesFuncionais unfSolicitante = itemSolicitacaoExameVO.getSolicitacaoExameVO().getUnidadeFuncional();
			if (unfSolicitante != null) {
				caracteristica = getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
						unfSolicitante.getSeq()
						, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA
				);
			}	

			DominioOrigemAtendimento origem = atendimento.getOrigem();
			if ((DominioOrigemAtendimento.I.equals(origem) || 
					DominioOrigemAtendimento.N.equals(origem)) || 
					(DominioOrigemAtendimento.A.equals(origem) &&
							DominioSimNao.S.equals(caracteristica)) &&
							!itemSolicitacaoExameVO.getIndGeradoAutomatico()) {

				response = true;
			}
		}

		return response;
	}

	/**
	 * ORADB AELP_VER_EXAMES_POS_ALTA (RN8). 
	 * 
	 * Verifica para pacientes internados e da 
	 * emergência se há exames desativados temporariamente. 
	 * 
	 * EXECUTA NO ADICIONAR E NO GRAVAR.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarExameDesativado(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws BaseException {

		AghAtendimentos atendimento = itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento();
		if (atendimento != null && !itemSolicitacaoExameVO.getIndGeradoAutomatico()) {

			AelUnfExecutaExames unfExecutaExames = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame();
			if (unfExecutaExames != null && unfExecutaExames.getIndDesativaTemp() &&
					this.validarDthrProgramadaMaiorDthrLimite(itemSolicitacaoExameVO)) {

				Date dthrReativaTemp = unfExecutaExames.getDthrReativaTemp();
				Date dthrProgramada = itemSolicitacaoExameVO.getDataProgramada();
				if (dthrReativaTemp != null &&
						!DateUtil.validaDataMaior(dthrProgramada, dthrReativaTemp)) {

					String pMotivo = unfExecutaExames.getMotivoDesativacao();
					SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM/yyyy");
					String pDthrReativaTemp = sdfHora.format(dthrReativaTemp);
					String pDescricaoUnidade = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getUnidadeFuncional().getDescricao();
					throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01634B, pMotivo, pDthrReativaTemp, pDescricaoUnidade);

				}
			}
		}
	}

	/**
	 * Valida se data/hora programada é maior que
	 * a data/hora limite, se sim retorna verdadeiro,
	 * senão falso.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @return {boolean}
	 */
	protected boolean validarDthrProgramadaMaiorDthrLimite(
			ItemSolicitacaoExameVO itemSolicitacaoExameVO) {

		boolean ehMaior = false;

		SolicitacaoExameVO solicitacaoExameVO = itemSolicitacaoExameVO.getSolicitacaoExameVO();
		AghAtendimentos atendimento = solicitacaoExameVO.getAtendimento();
		AelExamesMaterialAnalise examesMaterialAnalise = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise();
		AelExamesLimitadoAtendId examesLimitadoAtendId = new AelExamesLimitadoAtendId();
		examesLimitadoAtendId.setEmaExaSigla(examesMaterialAnalise.getAelExames().getSigla());
		examesLimitadoAtendId.setEmaManSeq(examesMaterialAnalise.getAelMateriaisAnalises().getSeq());
		examesLimitadoAtendId.setAtdSeq(atendimento.getSeq());
		AelExamesLimitadoAtend examesLimitadoAtend = this.getExamesLimitadoAtendDAO().obterPeloId(examesLimitadoAtendId);
		Date dthrProgramada = itemSolicitacaoExameVO.getDataProgramada();
		if (examesLimitadoAtend == null || 
				(examesLimitadoAtend != null && dthrProgramada != null 
						&& DateUtil.validaDataMaior(dthrProgramada, examesLimitadoAtend.getDthrLimite()))) {

			ehMaior = true;
		}

		return ehMaior;
	}

	/**
	 * Verifica se informacoesClinicas, usaAntimicrobianos OU objetivoSolic
	 * são nulos e se é atendimento assistencial, então deve 
	 * retornar verdadeiro, senão falso. Complementa RN9.
	 * 
	 * EXECUTA NO ADICIONAR E NO GRAVAR.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @return {Boolean}
	 */
	public Boolean verificarNecessidadeInformacoesClinicas(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {

		Boolean response = false;

		SolicitacaoExameVO solicitacaoExameVO = itemSolicitacaoExameVO.getSolicitacaoExameVO();
		String informacoesClinicas = solicitacaoExameVO.getInformacoesClinicas();
		DominioSimNao usaAntimicrobianos = solicitacaoExameVO.getUsaAntimicrobianos();
		DominioOrigemSolicitacao objetivoSolic = solicitacaoExameVO.getIndObjetivoSolic();
		if (informacoesClinicas == null ||
				usaAntimicrobianos == null ||
				objetivoSolic == null) {

			AghAtendimentos atendimento = solicitacaoExameVO.getAtendimento();
			if (atendimento != null) {
				response = true;
			} else {
				AelAtendimentoDiversos atendimentoDiverso = solicitacaoExameVO.getAtendimentoDiverso();
				if (atendimentoDiverso != null && atendimentoDiverso.getAelProjetoPesquisas() != null) {
					response = true;
				}
			}
		}

		return response;
	}

	/**
	 * ORADB AELP_VERIFICA_INF_CLINICAS (RN9). 
	 * 
	 * Verifica se o campo de informacoes clínicas
	 * está nulo e deve ser preenchido, se o campo usa_antimicrobianos
	 * está nulo e deve ser preenchido e se o campo ind_objetivo_solic
	 * está nulo e deve ser preenchido.
	 * 
	 * EXECUTA NO ADICIONAR E NO GRAVAR.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void verificarInformacoesClinicas(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws BaseException {

		SolicitacaoExameVO solicitacaoExameVO = itemSolicitacaoExameVO.getSolicitacaoExameVO();
		AghUnidadesFuncionais unidadeFuncional = solicitacaoExameVO.getUnidadeFuncional();

		Boolean exigeInformacoesClinicas = false;
		String informacoesClinicas = solicitacaoExameVO.getInformacoesClinicas();
		if (informacoesClinicas == null) {

			AelUnfExecutaExames unfExecutaExames = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame();
			if (unfExecutaExames != null && unfExecutaExames.getIndExigeInfoClin()) {
				exigeInformacoesClinicas = true;
			}

		}

		Boolean exigeUsaAntimicrobianos = false;
		DominioSimNao usaAntimicrobianos = solicitacaoExameVO.getUsaAntimicrobianos();
		if (usaAntimicrobianos == null) {

			AelUnfExecutaExames unfExecutaExames = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame();
			if (unfExecutaExames != null && unidadeFuncional != null) {

				AelPermissaoUnidSolic permissaoUnidSolic = //unfExecutaExames.getPermissaoUnidadeSolicitante(unidadeFuncional);
					this.getAelPermissaoUnidSolicDAO().buscarAelPermissaoUnidSolicPor(
							unfExecutaExames.getAelExamesMaterialAnalise().getAelExames(),
							unfExecutaExames.getAelExamesMaterialAnalise().getAelMateriaisAnalises(),
							unfExecutaExames.getUnidadeFuncional(),
							unidadeFuncional	);

				if (permissaoUnidSolic != null && permissaoUnidSolic.getIndExigeAntimicrobianos()) {
					exigeUsaAntimicrobianos = true;
				}
			}	
		}

		Boolean exigeObjetivoSolic = false;
		DominioOrigemSolicitacao objetivoSolic = solicitacaoExameVO.getIndObjetivoSolic();
		if (objetivoSolic == null) {

			if (unidadeFuncional != null) {

				DominioSimNao caracteristica = getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
						itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getId().getUnfSeq().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_RADIOLOGIA);
				if (DominioSimNao.S.equals(caracteristica)) {
					exigeObjetivoSolic = true;
				}
			}	
		}

		AelExamesMaterialAnalise examesMaterialAnalise = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise();
		String descricaoExame = examesMaterialAnalise.getAelExames().getDescricaoUsual();
		if (exigeInformacoesClinicas && exigeUsaAntimicrobianos && exigeObjetivoSolic) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.MSG_EXAME_EXIGE_INFOCLI_ANTIMICRO_PRIMEXAME, descricaoExame);
		}

		if (exigeInformacoesClinicas && !exigeUsaAntimicrobianos) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.MSG_EXAME_EXIGE_INFOCLI, descricaoExame);
		}

		if (!exigeInformacoesClinicas && exigeUsaAntimicrobianos) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.MSG_EXAME_EXIGE_ANTIMICRO, descricaoExame);
		}

		if (exigeObjetivoSolic) {
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.MSG_EXAME_EXIGE_PRIMEXAME, descricaoExame);
		}
	}

	/**
	 * ORADB AELP_VER_PROVAS_DEPENDENTES
	 * 
	 * Verifica provas de dependentes (RN13).
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @throws BaseException 
	 */
	protected void verificarProvasDependentes(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws BaseException {

		SolicitacaoExameVO solicitacaoExameVO = itemSolicitacaoExameVO.getSolicitacaoExameVO();
		List<ItemSolicitacaoExameVO> itemSolicitacaoExameVOs = new ArrayList<ItemSolicitacaoExameVO>();
		itemSolicitacaoExameVOs.addAll(solicitacaoExameVO.getItemSolicitacaoExameVos());
		itemSolicitacaoExameVOs.add(itemSolicitacaoExameVO);

		AelAtendimentoDiversos atendimentoDiversos = solicitacaoExameVO.getAtendimentoDiverso();
		if (solicitacaoExameVO.getAtendimento() != null || (atendimentoDiversos != null && atendimentoDiversos.getAelProjetoPesquisas() != null)) {

			FatConvenioSaudePlano convenio = this.obterFatConvenioSaudePlano(solicitacaoExameVO);
			if (convenio != null) {

				for (ItemSolicitacaoExameVO itemSolicitacaoExameVOExterno : itemSolicitacaoExameVOs) {

					AelExamesMaterialAnalise examesMaterialAnalise = itemSolicitacaoExameVOExterno.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise();
					String siglaItem = examesMaterialAnalise.getAelExames().getSigla();
					Integer manSeqItem = examesMaterialAnalise.getAelMateriaisAnalises().getSeq();
					List<AelExameDeptConvenio> listExameDeptConvenio = this.getAelExameDeptConvenioDAO().obterDependentesAtivosPorExame(siglaItem, manSeqItem, convenio.getId().getCnvCodigo(), convenio.getId().getSeq(), false);
					for (AelExameDeptConvenio exameDeptConvenio : listExameDeptConvenio) {

						String siglaDepend = exameDeptConvenio.getId().getExdEmaExaSiglaEhDependent();
						Integer manSeqDepend = exameDeptConvenio.getId().getExdEmaManSeqEhDependente();
						List<AelExamesProva> listExamesProva = this.getExamesProvaDAO().buscarProvasExameSolicitado(siglaDepend, manSeqDepend); 
						for (AelExamesProva examesProva : listExamesProva) {

							String siglaProva = examesProva.getId().getEmaExaSiglaEhProva();
							Integer manSeqProva = examesProva.getId().getEmaManSeqEhProva();
							for (ItemSolicitacaoExameVO itemSolicitacaoExameVOInterno : itemSolicitacaoExameVOs) {

								AelExamesMaterialAnalise examesMaterialAnaliseInterno = itemSolicitacaoExameVOInterno.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise();
								String siglaItemInterno = examesMaterialAnaliseInterno.getAelExames().getSigla();
								Integer manSeqItemInterno = examesMaterialAnaliseInterno.getAelMateriaisAnalises().getSeq();
								if (siglaItemInterno.equals(siglaProva) && manSeqItemInterno.equals(manSeqProva)) {
									String descricao = examesMaterialAnalise.getAelExames().getDescricaoUsual();
									String descricaoProva = examesProva.getExamesMaterialAnaliseEhProva().getAelExames() != null ? examesProva.getExamesMaterialAnaliseEhProva().getAelExames().getDescricaoUsual() : "";
									throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01704B, descricaoProva, descricao);
								}

							}	

						}

					}
				}	
			}
		}

	}

	public List<ItemSolicitacaoExameCancelamentoVO> listarItensExameCancelamentoSolicitante(Integer solicitacaoExameSeq) throws BaseException{

		return this.getAelItemSolicitacaoExameDAO().listarItensExameCancelamentoSolicitante(solicitacaoExameSeq);

	}

	/**
	 * ORADB AELP_GERA_DEPENDENTES (RN14). 
	 * 
	 * Rotina para gerar itens de exames dependentes.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @param {AghUnidadesFuncionais} unfTrabalho
	 * @throws BaseException 
	 */
	public void gerarDependentes(ItemSolicitacaoExameVO itemSolicitacaoExameVO, AghUnidadesFuncionais unfTrabalho) throws BaseException {
		ConvenioExamesLaudosVO convenioExamesLaudos;
		//RN14.2
		if(itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento() != null) {
			//RN14.3
			convenioExamesLaudos = buscarConvenioAtendimentoAssistencial(itemSolicitacaoExameVO);
		} else {
			//RN14.4
			//Busca o convênio do atendimento diverso
			convenioExamesLaudos = getExamesFacade().rnAelpBusConvAtv(itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimentoDiverso().getSeq());
		}

		if (itemSolicitacaoExameVO.getDependentesObrigratorios().isEmpty() && convenioExamesLaudos != null) {
			itemSolicitacaoExameVO.getDependentesObrigratorios().addAll(buscarExamesDependentes(itemSolicitacaoExameVO, convenioExamesLaudos, false));
		} else {
			for(ItemSolicitacaoExameVO itemSolicitacaoExameVODependente : itemSolicitacaoExameVO.getDependentesObrigratorios()) {
				itemSolicitacaoExameVODependente = gerarItemSolicitacaoExameDependente(itemSolicitacaoExameVO, itemSolicitacaoExameVODependente, unfTrabalho, itemSolicitacaoExameVODependente.getUnfExecutaExame().getUnfExecutaExame(), false);
			}
		}
	}

	/**
	 * Obtém a lista de dependentes opcionais para um exame Pai. #8487
	 * @param itemSolicitacaoExamePai
	 * @return List<ItemSolicitacaoExameVO>
	 */
	public List<ItemSolicitacaoExameVO> obterDependentesOpcionais(ItemSolicitacaoExameVO itemSolicitacaoExamePai) {
		List<ItemSolicitacaoExameVO> lista = new ArrayList<ItemSolicitacaoExameVO>();

		ConvenioExamesLaudosVO conv = null;
		if (itemSolicitacaoExamePai.getSolicitacaoExameVO().getAtendimento() != null && itemSolicitacaoExamePai.getSolicitacaoExameVO().getAtendimento().getSeq() != null) {
			conv = this.getPacienteFacade().buscarConvenioExamesLaudos(itemSolicitacaoExamePai.getSolicitacaoExameVO().getAtendimento().getSeq());
		} else if (itemSolicitacaoExamePai.getSolicitacaoExameVO().getAtendimentoDiverso() != null && itemSolicitacaoExamePai.getSolicitacaoExameVO().getAtendimentoDiverso().getSeq() != null) {
			conv = getExamesFacade().rnAelpBusConvAtv(itemSolicitacaoExamePai.getSolicitacaoExameVO().getAtendimentoDiverso().getSeq());
		}

		//Se tem informações de convênio então busca os dependentes opcionais.
		if (conv != null && conv.getCodigoConvenioSaude() != null && conv.getCodigoConvenioSaudePlano() != null) {
			List<ItemSolicitacaoExameVO> listaAux = buscarExamesDependentes(itemSolicitacaoExamePai, conv, true);
			if (listaAux != null) {
				lista.addAll(listaAux);
			}
		}
		return lista; 
	}

	private List<ItemSolicitacaoExameVO> buscarExamesDependentes(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ConvenioExamesLaudosVO conv, boolean opcional) {
		List<ItemSolicitacaoExameVO> listaISE = new ArrayList<ItemSolicitacaoExameVO>();

		List<AelExameDeptConvenio> examesDependentes = getAelExameDeptConvenioDAO().obterDependentesAtivosPorExame(itemSolicitacaoExameVO.getUnfExecutaExame().getExameSigla(), itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq(), conv.getCodigoConvenioSaude(), conv.getCodigoConvenioSaudePlano(), opcional);
		for(AelExameDeptConvenio exameDependente : examesDependentes) {
			//RN14.6
			//Busca informações da unidade funcional executora do exame dependente
			List<AelUnfExecutaExames> unidadesFuncionaisExecutoras = getAelUnfExecutaExamesDAO().buscaListaAelUnfExecutaExames(exameDependente.getExameDependente().getId().getEmaExaSiglaEhDependente(), exameDependente.getExameDependente().getId().getEmaManSeqEhDependente(), null, false);
			
			if (unidadesFuncionaisExecutoras.size() > 1) {
				unidadesFuncionaisExecutoras = filtrarDependenteUnidadesDiferentes(unidadesFuncionaisExecutoras, itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getId().getUnfSeq().getSeq());
			}
			
			unidadesFuncionaisExecutoras = filtrarUnidadesFuncionaisExecutorasAtivas(unidadesFuncionaisExecutoras);
			
			if(!unidadesFuncionaisExecutoras.isEmpty()) {
				for(AelUnfExecutaExames unidadeFuncionalExecutora: unidadesFuncionaisExecutoras) {
					//RN14.7
					//Verifica se é atendimento de projeto para garantir se o exame está contemplado no orçamento do projeto
					//TODO AELP_VER_PROJ_EXAMES_DEPT - NÃO DEVE SER DESENVOLVIDO AGORA 
					boolean atendimentoProjeto = true;

					//RN14.8
					if(atendimentoProjeto) {
						ItemSolicitacaoExameVO itemSolicitacaoExameVODependente = gerarItemSolicitacaoExameDependente(itemSolicitacaoExameVO, null,  itemSolicitacaoExameVO.getSolicitacaoExameVO().getUnidadeTrabalho(), unidadeFuncionalExecutora, opcional);
						listaISE.add(itemSolicitacaoExameVODependente);
					}
				}
			}
		}
		return listaISE;
	}

	/**
	 * Obtem uma lista de ItemSolicitacaoExameVO de dependentes opcionais setados com os valores devidos do exame pai.
	 * @param itemSolicitacaoExameVo Ítem pai
	 * @return List<ItemSolicitacaoExameVO> Lista dos opcionais selecionados com as informações pertinentes do exame pai.
	 */
	public List<ItemSolicitacaoExameVO> obterDependentesOpcionaisSelecionados(ItemSolicitacaoExameVO itemSolicitacaoExameVo) {
		List<ItemSolicitacaoExameVO> lista = new ArrayList<ItemSolicitacaoExameVO>();
		for (ItemSolicitacaoExameVO iseVO : itemSolicitacaoExameVo.getDependentesOpcionais()) {
			if (iseVO.getExameOpcionalSelecionado()) {
				lista.add(gerarItemSolicitacaoExameDependente(itemSolicitacaoExameVo, iseVO, itemSolicitacaoExameVo.getSolicitacaoExameVO().getUnidadeTrabalho(), iseVO.getUnfExecutaExame().getUnfExecutaExame(), true));
			}
		}
		return lista;
	}	

	/**
	 * Busca o convênio do atendimento assistencial (RN14.3).
	 * 
	 * 
	 */
	protected ConvenioExamesLaudosVO buscarConvenioAtendimentoAssistencial(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {
		return getPacienteFacade().buscarConvenioExamesLaudos(itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento().getSeq());
	}

	/**
	 * Gera um novo ItemSolicitacaoExameVO representando um exame dependente.
	 *  *
	 *   @param itemSolicitacaoExameVO Exame Pai
	 * @param itemSolicitacaoExameVODependenteParam Exame dependente
	 * @param unfTrabalho Unidade funcional de trabalho
	 * @param unidadeFuncionalExecutora Unidade Funcional executora do exame
	 * @param opcional Indica se o exame é dependente opcional
	 * @return ItemSolicitacaoExameVO
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected ItemSolicitacaoExameVO gerarItemSolicitacaoExameDependente(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ItemSolicitacaoExameVO itemSolicitacaoExameVODependenteParam, AghUnidadesFuncionais unfTrabalho, AelUnfExecutaExames unidadeFuncionalExecutora, boolean opcional) {
		//RN14.8
		ItemSolicitacaoExameVO itemSolicitacaoExameVODependente = null;
		if (itemSolicitacaoExameVODependenteParam != null) {
			itemSolicitacaoExameVODependente = itemSolicitacaoExameVODependenteParam;
		} else {
			itemSolicitacaoExameVODependente = new ItemSolicitacaoExameVO();
		}

		itemSolicitacaoExameVODependente.setSolicitacaoExameVO(itemSolicitacaoExameVO.getSolicitacaoExameVO());
		itemSolicitacaoExameVODependente.setDataProgramada(itemSolicitacaoExameVO.getDataProgramada());
		itemSolicitacaoExameVODependente.setTipoColeta(itemSolicitacaoExameVO.getTipoColeta());

		if (itemSolicitacaoExameVO.getTipoTransporte() != null) {
			itemSolicitacaoExameVODependente.setTipoTransporte(itemSolicitacaoExameVO.getTipoTransporte());
		}

		if (itemSolicitacaoExameVO.getOxigenioTransporte() != null) {
			itemSolicitacaoExameVODependente.setOxigenioTransporte(itemSolicitacaoExameVO.getOxigenioTransporte());
		}

		if (itemSolicitacaoExameVO.getRegiaoAnatomica() != null) {
			itemSolicitacaoExameVODependente.setRegiaoAnatomica(itemSolicitacaoExameVO.getRegiaoAnatomica());
		}

		if (itemSolicitacaoExameVO.getDescRegiaoAnatomica() != null) {
			itemSolicitacaoExameVODependente.setDescRegiaoAnatomica(itemSolicitacaoExameVO.getDescRegiaoAnatomica());
		}

		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unidadeFuncionalExecutora);
		itemSolicitacaoExameVODependente.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVODependente.setIndGeradoAutomatico(true);
		itemSolicitacaoExameVODependente.setStyleClass(opcional ? "silk-icon silk-exames-dependentes-opcionais" : "silk-icon silk-exames-dependentes-obrigatorios");
		itemSolicitacaoExameVODependente.setEhDependenteObrigatorio(!opcional);
		itemSolicitacaoExameVODependente.setEhDependenteOpcional(opcional);

		itemSolicitacaoExameVODependente.setMostrarAbaRecomendacoes(false);
		itemSolicitacaoExameVODependente.setMostrarAbaConcentO2(false);
		itemSolicitacaoExameVODependente.setMostrarAbaIntervColeta(false);
		itemSolicitacaoExameVODependente.setMostrarAbaNoAmostras(false);
		itemSolicitacaoExameVODependente.setMostrarAbaRegMatAnalise(false);
		
		itemSolicitacaoExameVODependente.setMostrarAbaTipoTransporte(
				(itemSolicitacaoExameVO.getMostrarAbaTipoTransporte() != null ) ? itemSolicitacaoExameVO.getMostrarAbaTipoTransporte() : false);

		//RN14.9
		if (unfTrabalho != null) {
			//RN14.10
			if (unfTrabalho.getSeq().equals(unidadeFuncionalExecutora.getId().getUnfSeq().getSeq())) {
				itemSolicitacaoExameVODependente.setSituacaoCodigo(itemSolicitacaoExameVO.getSituacaoCodigo());
			} else {
				//RN14.11
				buscarSituacaoExamePendente(unidadeFuncionalExecutora.getId().getEmaManSeq(), itemSolicitacaoExameVO, itemSolicitacaoExameVODependente);
			}
		} else {
			//RN14.11
			buscarSituacaoExamePendente(unidadeFuncionalExecutora.getId().getEmaManSeq(), itemSolicitacaoExameVO, itemSolicitacaoExameVODependente);
		}

		//RN14.12
		//RN14.13
		//RN14.14
		//RN14.15
		//Dados copiados ao instanciar o VO com o item original

		//RN14.16
		if (itemSolicitacaoExameVO.getDescMaterialAnalise() != null
				&& unidadeFuncionalExecutora.getAelExamesMaterialAnalise().getAelMateriaisAnalises().getIndExigeDescMatAnls()) {
			itemSolicitacaoExameVODependente.setDescMaterialAnalise(itemSolicitacaoExameVO.getDescMaterialAnalise());
		} else {
			itemSolicitacaoExameVODependente.setDescMaterialAnalise(null);
		}

		return itemSolicitacaoExameVODependente;
	}

	/**
	 * ORADB aelp_busca_sit_codigo (RN14.11).
	 * 
	 * Rotina para buscar a situação do exame dependente que está sendo gerado.
	 */
	protected void buscarSituacaoExamePendente(Integer emaManSeqEhDependente, ItemSolicitacaoExameVO itemSolicitacaoExameVOPai, ItemSolicitacaoExameVO itemSolicitacaoExameVODependente) {
		AelMateriaisAnalises materialAnalise = getAelMaterialAnaliseDAO().obterPeloId(emaManSeqEhDependente);
		if(materialAnalise.getIndColetavel()) {
			//RN14.11.1
			if(itemSolicitacaoExameVOPai.getSituacaoCodigo().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.CS.toString())) {
				//RN14.11.3
				itemSolicitacaoExameVODependente.setSituacaoCodigo(itemSolicitacaoExameVOPai.getSituacaoCodigo());
			} else {
				//RN14.11.4
				AelSitItemSolicitacoes situacao = getAelSitItemSolicitacoesDAO().obterPeloId(DominioSituacaoItemSolicitacaoExame.AC.toString());
				itemSolicitacaoExameVODependente.setSituacaoCodigo(situacao);
			}
		} else {
			//RN14.11.5
			AelSitItemSolicitacoes situacao = getAelSitItemSolicitacoesDAO().obterPeloId(DominioSituacaoItemSolicitacaoExame.AX.toString());
			itemSolicitacaoExameVODependente.setSituacaoCodigo(situacao);
		}
	}

	protected List<AelUnfExecutaExames> filtrarUnidadesFuncionaisExecutorasAtivas(List<AelUnfExecutaExames> unidadesFuncionaisExecutoras) {
		List<AelUnfExecutaExames> unidadesFuncionaisExecutorasRetorno = new ArrayList<AelUnfExecutaExames>();
		for(AelUnfExecutaExames unidadeFuncionalExecutora : unidadesFuncionaisExecutoras) {
			if(unidadeFuncionalExecutora.getIndSituacao().equals(DominioSituacao.A)
					&& unidadeFuncionalExecutora.getAelExamesMaterialAnalise().getIndSituacao().equals(DominioSituacao.A)
					&& unidadeFuncionalExecutora.getAelExamesMaterialAnalise().getAelExames().getIndSituacao().equals(DominioSituacao.A)
					&& unidadeFuncionalExecutora.getAelExamesMaterialAnalise().getAelMateriaisAnalises().getIndSituacao().equals(DominioSituacao.A)) {
				unidadesFuncionaisExecutorasRetorno.add(unidadeFuncionalExecutora);
			}
		}

		return unidadesFuncionaisExecutorasRetorno;
	}
	
	protected List<AelUnfExecutaExames> filtrarDependenteUnidadesDiferentes(List<AelUnfExecutaExames> unidadesFuncionaisExecutoras, Short unfSeq) {
		List<AelUnfExecutaExames> unidadesFuncionaisExecutorasRetorno = new ArrayList<AelUnfExecutaExames>();

		for (AelUnfExecutaExames aelUnfExecutaExames : unidadesFuncionaisExecutoras) {
			if (aelUnfExecutaExames.getId().getUnfSeq().getSeq().equals(unfSeq)) {
				unidadesFuncionaisExecutorasRetorno.add(aelUnfExecutaExames);
			}
		}
		
		if (!unidadesFuncionaisExecutorasRetorno.isEmpty()) {
			return unidadesFuncionaisExecutorasRetorno;
		} 
		
		return unidadesFuncionaisExecutoras;
	}

	/**
	 * ORADB AELP_VER_PROG_ROTINA (RN15). 
	 * 
	 * Verifica data/hora programada de rotina.
	 * 
	 * EXECUTA NO ADICIONAR E NO GRAVAR.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void verificarProgRotina(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws BaseException {

		SolicitacaoExameVO solicitacaoExameVO = itemSolicitacaoExameVO.getSolicitacaoExameVO();

		FatConvenioSaude fatConvenioSaude = this.obterFatConvenioSaude(solicitacaoExameVO);

		AelExamesMaterialAnalise examesMaterialAnalise = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise();
		String descricao = examesMaterialAnalise.getAelExames().getDescricaoUsual();

		if (fatConvenioSaude != null && DominioGrupoConvenio.S.equals(fatConvenioSaude.getGrupoConvenio())) {

			Date dthrProgramadaInicio = null;
			Date dthrProgramadaFim = null;
			List<Date> listDate = this.calcularRangeSolicitacao(itemSolicitacaoExameVO);
			if (!listDate.isEmpty()) {
				dthrProgramadaInicio = listDate.get(0);
				dthrProgramadaFim = listDate.get(1);
			}

			if (dthrProgramadaInicio != null && dthrProgramadaFim != null && 
					!DateUtil.entre(new Date(), dthrProgramadaInicio, dthrProgramadaFim)) {

				if (DominioTipoColeta.U.equals(itemSolicitacaoExameVO.getTipoColeta())) {
					throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01337B, descricao);
				}

				String sigla = examesMaterialAnalise.getAelExames().getSigla();
				Integer manSeq = examesMaterialAnalise.getAelMateriaisAnalises().getSeq();
				Short unfSeq = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getUnidadeFuncional().getSeq();
				Short unfSeqSolicitante = solicitacaoExameVO.getUnidadeFuncional().getSeq();
				
				AelPermissaoUnidSolic permissaoUnidSolic = this.getAelPermissaoUnidSolicDAO().buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(sigla, manSeq, unfSeq, unfSeqSolicitante);

				if (permissaoUnidSolic == null) {
				// “Permissão da Unidade Solicitante não está cadastrada.”
					throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_00426C,itemSolicitacaoExameVO.getItemSolicitacaoExame().getExame().getDescricaoUsual());
				}
				
				if (permissaoUnidSolic == null || DominioSimNaoRotina.N.equals(permissaoUnidSolic.getIndPermiteProgramarExames())) {
					throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01333B, descricao);
				}

				BigDecimal unidDefault = this.buscaParametroSistemaValorNumerico(AghuParametrosEnum.P_COD_UNIDADE_COLETA_DEFAULT);
				if (permissaoUnidSolic.getUnfSeqAvisa() != null && unidDefault != null &&
						permissaoUnidSolic.getUnfSeqAvisa().getSeq().toString().equals(unidDefault.toString())) {

					Date dthrProgramada = itemSolicitacaoExameVO.getDataProgramada();
					Date dataLimite = new Date();
					AghParametros param = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HORAS_ROTINA_PROGR_INT);

                    if (param != null && param.getVlrNumerico() != null) {
						dataLimite = DateUtil.adicionaDiasFracao(dataLimite, (param.getVlrNumerico().floatValue()/24));
					}

					if (dthrProgramada != null && dataLimite != null &&
							DateUtil.validaDataMaior(dthrProgramada, dataLimite)) {

//						SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
//						String strDthrProgramada = sdfHora.format(dataLimite);

                        String hrProgramada = param.getVlrNumerico().toString();

						if (DominioSimNaoRotina.R.equals(permissaoUnidSolic.getIndPermiteProgramarExames())) {
							throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01332B, descricao, hrProgramada);
						} else {
                            throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01335B, descricao, hrProgramada);
						}

					}

					if (DominioSimNaoRotina.R.equals(permissaoUnidSolic.getIndPermiteProgramarExames())) {

						List<AelHorarioRotinaColetas> listHorarioRotinaColetas = this.verificarHorarioRotinaColeta(itemSolicitacaoExameVO, unidDefault);
						if (listHorarioRotinaColetas.isEmpty()) {
							throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_01331B, descricao);
						}
					}
				}
			}
		}
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
	protected List<Date> calcularRangeSolicitacao(
			ItemSolicitacaoExameVO itemSolicitacaoExameVO)
			throws ApplicationBusinessException {
		if (itemSolicitacaoExameVO != null
				&& itemSolicitacaoExameVO.getDataProgramada() != null) {
			int paramTempoMinutosSolicitacao = 60;
			BigDecimal pTempoMinutoSolic = this
			.buscaParametroSistemaValorNumerico(AghuParametrosEnum.P_TEMPO_MINUTOS_SOLIC);
			if (pTempoMinutoSolic != null) {
				paramTempoMinutosSolicitacao = pTempoMinutoSolic.intValue();
			}
			Date v_dthr_programada_inicio = DateUtil.adicionaMinutos(
					itemSolicitacaoExameVO.getDataProgramada(),
					(paramTempoMinutosSolicitacao * -1));
			Date v_dthr_programada_fim = DateUtil.adicionaMinutos(
					itemSolicitacaoExameVO.getDataProgramada(),
					paramTempoMinutosSolicitacao);

			return Arrays.asList(v_dthr_programada_inicio,
					v_dthr_programada_fim);
		} else {
			return new LinkedList<Date>();
		}
	}

	/**
	 * Busca horarios de rotina de coleta.
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @param {BigDecimal} unidDefault
	 * @return {List<AelHorarioRotinaColetas>}
	 */
	protected List<AelHorarioRotinaColetas> verificarHorarioRotinaColeta(
			ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			BigDecimal unidDefault) {

		List<AelHorarioRotinaColetas> listHorarioRotinaColetasIdeal = new ArrayList<AelHorarioRotinaColetas>();

		Date dthrProgramada = itemSolicitacaoExameVO.getDataProgramada();
		if (dthrProgramada != null) {

			AghFeriados feriado = this.getAghuFacade().obterFeriado(dthrProgramada); 
			DominioTurno turno = null;
			String hrProgramada = DateFormatUtil.formataHoraMinuto(dthrProgramada);
			String diaSemana = DateFormatUtil.diaDaSemana(dthrProgramada);
			if (feriado != null) {
				turno = feriado.getTurno();
				if (turno == null) {
					diaSemana = "FER";
				} else if (DominioTurno.M.equals(turno)) {
					diaSemana = "FERM";
				} else if (DominioTurno.T.equals(turno)) {
					diaSemana = "FERT";
				}
			}

			AghUnidadesFuncionais unidadeExecutora = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(Short.valueOf(unidDefault.toString()));
			List<AelHorarioRotinaColetas> listHorarioRotinaColetasAtual = this.getHorarioRotinaColetasDAO().obterAelHorarioRotinaColetasAtivas(unidadeExecutora, itemSolicitacaoExameVO.getSolicitacaoExameVO().getUnidadeFuncional().getSeq());

			for (AelHorarioRotinaColetas horarioRotinaColetas : listHorarioRotinaColetasAtual) {

				Calendar data12 = Calendar.getInstance();
				data12.set(0, 0, 0, 12, 0, 0);
				Calendar dthrProgramadaRotina = Calendar.getInstance();
				dthrProgramadaRotina.setTime(horarioRotinaColetas.getId().getHorario());
				Date hrProgramadaRotina = DateUtil.obterData(0, 0, 0, dthrProgramadaRotina.get(Calendar.HOUR_OF_DAY), dthrProgramadaRotina.get(Calendar.MINUTE));
				String strHrProgramadaRotina = DateFormatUtil.formataHoraMinuto(dthrProgramadaRotina.getTime());

				if ((diaSemana.equalsIgnoreCase(horarioRotinaColetas.getId().getDia().toString()) &&
						hrProgramada.equals(strHrProgramadaRotina) && 
						turno == null) || 
						((diaSemana.equalsIgnoreCase(horarioRotinaColetas.getId().getDia().toString()) &&
								hrProgramada.equals(strHrProgramadaRotina) || 
								("FERM".equals(horarioRotinaColetas.getId().getDia().toString()) && 
										DateValidator.validaDataMenorIgual(hrProgramadaRotina, data12.getTime()))) && 
										DominioTurno.M.equals(turno)) || 
										((diaSemana.equalsIgnoreCase(horarioRotinaColetas.getId().getDia().toString()) &&
												hrProgramada.equals(strHrProgramadaRotina) || 
												("FERT".equals(horarioRotinaColetas.getId().getDia().toString()) && 
														DateUtil.validaDataMaior(hrProgramadaRotina, data12.getTime()))) && 
														DominioTurno.T.equals(turno))) {

					listHorarioRotinaColetasIdeal.add(horarioRotinaColetas);

				}

			}

		}

		return listHorarioRotinaColetasIdeal;

	}

	/**
	 * ORADB AELP_EXIGENCIA_EXAME (RN16). 
	 * 
	 * Rotina para verificar exigências do exame.
	 * 
	 * @param {ItemSolicitacaoExameVO} itemSolicitacaoExameVO
	 * @throws BaseException 
	 */
	protected ApplicationBusinessException verificarExigenciaExame(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws BaseException {
		ApplicationBusinessException mensagemWarn = null;
		
		//RN16.2
		//Verifica se o atendimento é de convênio SUS
		if(itemSolicitacaoExameVO.getSolicitacaoExameVO().getIsSus()) {
			//RN16.3
			if(itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame() != null && itemSolicitacaoExameVO.getSolicitacaoExameVO().getUnidadeFuncional() != null) {
				//RN16.4
				//Testa tipo de mensagem
				List<AelExigenciaExame> exigenciasExame = getAelExigenciaExameDAO().obterPorUnfExecutaExames(itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame());
				if(exigenciasExame != null && !exigenciasExame.isEmpty()) {
					AelExigenciaExame exigenciaExame = exigenciasExame.get(0); //Deve existir apenas 1
					DominioTipoMensagem tipoMensagem = exigenciaExame.getTipoMensagem();

					//RN16.5 - RN16.8
					DominioNecessidadeInternacaoAihAssinada indInternacao = verificarNecessidadeInternacao(itemSolicitacaoExameVO);

					//RN16.9 - RN16.13
					DominioNecessidadeInternacaoAihAssinada indAihAssinada = verificarNecessidadeAihAssinada(itemSolicitacaoExameVO);

					//RN16.14
					if(indInternacao == DominioNecessidadeInternacaoAihAssinada.S && indAihAssinada == DominioNecessidadeInternacaoAihAssinada.S) {
						//RN16.15
						if(tipoMensagem == DominioTipoMensagem.R) {
							throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03409);
						} else if(tipoMensagem == DominioTipoMensagem.I) {
							mensagemWarn = new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03409, br.gov.mec.aghu.core.exception.Severity.WARN);
						}
					}

					//RN16.16
					if(indInternacao == DominioNecessidadeInternacaoAihAssinada.S
							&& (indAihAssinada == DominioNecessidadeInternacaoAihAssinada.N || indInternacao == DominioNecessidadeInternacaoAihAssinada.SV)) {
						//RN16.17
						if(tipoMensagem == DominioTipoMensagem.R) {
							throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03408);
						} else if(tipoMensagem == DominioTipoMensagem.I) {
							mensagemWarn = new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03408, br.gov.mec.aghu.core.exception.Severity.WARN);							
						}
					}

					//RN16.18
					if((indInternacao == DominioNecessidadeInternacaoAihAssinada.N || indInternacao == DominioNecessidadeInternacaoAihAssinada.SV)
							&& indAihAssinada == DominioNecessidadeInternacaoAihAssinada.S) {
						//RN16.19
						if(tipoMensagem == DominioTipoMensagem.R) {
							throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03407);
						} else if(tipoMensagem == DominioTipoMensagem.I) {
							mensagemWarn = new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.AEL_03407, br.gov.mec.aghu.core.exception.Severity.WARN);
						}
					}
				}
			}
		}

		return mensagemWarn;
	}

	/**
	 * ORADB AELK_EXI_EXA.AELC_INTERNAC 
	 * 
	 */
	protected DominioNecessidadeInternacaoAihAssinada verificarNecessidadeInternacao(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {
		//RN16.5
		//Pesquisa o indicador
		List<AelExigenciaExame> exigenciasExameAtivas = getAelExigenciaExameDAO().obterAtivasPorUnfExecutaExamesUnidadeFuncional(itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame(), itemSolicitacaoExameVO.getSolicitacaoExameVO().getUnidadeFuncional());

		if(exigenciasExameAtivas == null || exigenciasExameAtivas.isEmpty()) {
			return DominioNecessidadeInternacaoAihAssinada.V;
		} else {
			AelExigenciaExame exigenciaExame = exigenciasExameAtivas.get(0); //Deve haver apenas 1
			//RN16.6
			if(exigenciaExame.getIndPedeInternacao()) {
				//RN16.7
				if(itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento() == null) {
					return DominioNecessidadeInternacaoAihAssinada.C;
				} else {
					//RN16.8
					if(itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento().getOrigem() == DominioOrigemAtendimento.I
							|| itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento().getOrigem() == DominioOrigemAtendimento.N) {
						return DominioNecessidadeInternacaoAihAssinada.N; //Paciente está internado
					} else {
						return DominioNecessidadeInternacaoAihAssinada.S; //Paciente não está internado
					}
				}

			} else {
				return DominioNecessidadeInternacaoAihAssinada.SV;
			}
		}
	}

	/**
	 * ORADB AELK_EXI_EXA.AELC_AIH_ASSINADA
	 * 
	 */
	protected DominioNecessidadeInternacaoAihAssinada verificarNecessidadeAihAssinada(ItemSolicitacaoExameVO itemSolicitacaoExameVO) {
		//RN16.9
		//Pesquisa o indicador
		List<AelExigenciaExame> exigenciasExameAtivas = getAelExigenciaExameDAO().obterAtivasPorUnfExecutaExamesUnidadeFuncional(itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame(), itemSolicitacaoExameVO.getSolicitacaoExameVO().getUnidadeFuncional());
		AelExigenciaExame exigenciaExame = exigenciasExameAtivas.isEmpty() ? null : exigenciasExameAtivas.get(0); //Deve existir apenas 1

		//RN16.10
		if(itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento() == null) {
			return DominioNecessidadeInternacaoAihAssinada.C;
		} else {
			//RN16.11
			//Verifica triagem
			// List<MamTrgEncInterno> triagens = getMamTrgEncInternoDAO().obterPorConsulta(itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento().getConsulta());
			List<MamTrgEncInterno> triagens = this.getAmbulatorioFacade().obterTrgEncInternoPorConsulta(itemSolicitacaoExameVO.getSolicitacaoExameVO().getAtendimento().getConsulta());
			if(triagens == null || triagens.isEmpty()) {
				return DominioNecessidadeInternacaoAihAssinada.T;
			} else {
				MamTrgEncInterno triagem =  triagens.isEmpty() ? null : triagens.get(0); //Deve existir apenas 1

				//RN16.12
				if(exigenciaExame != null && exigenciaExame.getIndPedeAihAssinada()) {
					//RN16.13
					//Busca se é necessário possuir a AIH assinada para solicitar exame
					// List<MamLaudoAih> laudosAih = getMamLaudoAihDAO().obterPorTrgSeq(triagem.getId().getTrgSeq());
					List<MamLaudoAih> laudosAih = this.getAmbulatorioFacade().obterLaudoAihPorTrgSeq(triagem.getId().getTrgSeq());

					if(laudosAih == null || laudosAih.isEmpty()) {
						return DominioNecessidadeInternacaoAihAssinada.S;
					} else {
						return DominioNecessidadeInternacaoAihAssinada.N;
					}
				} else {
					return DominioNecessidadeInternacaoAihAssinada.SV;
				}
			}
		}
	}
	
	/**
	 * Pesquisa por AelUnfExecutaExames que pertença ao lote informado por parâmetro.
	 * Atraves da associacao obrigatorio entre as entidades, nao levando em conta os sinonimos.<br>
	 * @param isOrigemInternacao 
	 * @param nomeExame
	 * @return List<UnfExecutaSinonimoExameVO>
	 */
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameLote(Short leuSeq, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean isOrigemInternacao) {
		/*List<UnfExecutaSinonimoExameVO> returnValue = new LinkedList<UnfExecutaSinonimoExameVO>(); 
		List<Object[]> lista = getAelUnfExecutaExamesDAO().pesquisaUnidadeExecutaSinonimoExameLote(leuSeq, Boolean.FALSE);
		for(Object exame : lista){
			if(!(exame instanceof AelUnfExecutaExames)){
				break;
			}
			AelUnfExecutaExames unfExecutaExame = (AelUnfExecutaExames) exame;
			UnfExecutaSinonimoExameVO vo = new UnfExecutaSinonimoExameVO(unfExecutaExame);
			returnValue.add(vo);
		}
		return returnValue;
		*/	
	
		
		return pesquisaUnidadeExecutaSinonimoExameLote(leuSeq,null, false, seqAtendimento, filtrarExamesProcEnfermagem, isOrigemInternacao);
	}
	
	/**
	 * Validação para impedir que o usuário solicite exame coletável com situação "A EXECUTAR"
	 * e não coletável com situação "A COLETAR" ou "COLETADO"
	 */
	private void validarSituacaoItemSolicitacao(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws ApplicationBusinessException {

		if (!itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getIndColetavel()
				&& (itemSolicitacaoExameVO.getSituacaoCodigo().getDescricao().equalsIgnoreCase(DominioSituacaoItemSolicitacaoExame.AC.getDescricao())
						|| itemSolicitacaoExameVO.getSituacaoCodigo().getDescricao().equalsIgnoreCase(DominioSituacaoItemSolicitacaoExame.CO.getDescricao()))){
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.ERRO_SITUACAO_ITEM_NAO_COLETAVEL);
		}
		
		if (itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getIndColetavel()
				&& (itemSolicitacaoExameVO.getSituacaoCodigo().getDescricao().equalsIgnoreCase(DominioSituacaoItemSolicitacaoExame.AX.getDescricao()))){
			throw new ApplicationBusinessException(ItemSolicitacaoExameONExceptionCode.ERRO_SITUACAO_ITEM_COLETAVEL);
		}
		
	}
	
	public List<AelRecomendacaoExame> verificarRecomendacaoExameQueSeraoExibidas(
			List<AelRecomendacaoExame> aelRecomendacaoExame,
			ItemSolicitacaoExameVO itemSolicitacaoExameVO) {

		List<AelRecomendacaoExame> recomendacaoExameListAlterada = new ArrayList<AelRecomendacaoExame>();

		String origemPaciente = verificarOrigemDoPaciente(itemSolicitacaoExameVO.getSolicitacaoExameVO().getOrigem().getDescricao());

		if (origemPaciente.equals(DominioAbrangenciaGrupoRecomendacao.I.getDescricao())
				|| origemPaciente.equals(DominioAbrangenciaGrupoRecomendacao.A.getDescricao())) {

			for (AelRecomendacaoExame recomendacaoExame : itemSolicitacaoExameVO.getRecomendacaoExameList()) {
				
				if (origemPaciente.equals(recomendacaoExame.getAbrangencia().getDescricao())
						|| DominioAbrangenciaGrupoRecomendacao.S.getDescricao().equals(recomendacaoExame.getAbrangencia().getDescricao())) {
					
					if (recomendacaoExame.getIndAvisaResp()	|| recomendacaoExame.getResponsavel().getDescricao()
									.equals(DominioResponsavelGrupoRecomendacao.S.getDescricao())) {
						
						recomendacaoExameListAlterada.add(recomendacaoExame);
					}
				}

			}
			return recomendacaoExameListAlterada;

		}

		return null;
	}
	
	public String verificarOrigemDoPaciente(String origem) {

		String abrangencia = null;

		if ((DominioOrigemAtendimento.I.getDescricao().equals(origem)
				|| DominioOrigemAtendimento.N.getDescricao().equals(origem) || DominioOrigemAtendimento.C.getDescricao()
					.equals(origem))
				|| DominioOrigemAtendimento.H.getDescricao().equals(origem)) {

			abrangencia = DominioAbrangenciaGrupoRecomendacao.I.getDescricao();
		
		} else {
			abrangencia = DominioAbrangenciaGrupoRecomendacao.A.getDescricao();
		}

		return abrangencia;
	}
	
	public Boolean verificarSeExameSendoSolicitadoRedome(final AelItemSolicitacaoExames itemSolicitacaoExame,AghUnidadesFuncionais unidadeExecutora)
			throws BaseException {

		return getItemSolicitacaoExameRN().verificarSeExameSendoSolicitadoRedome(itemSolicitacaoExame,unidadeExecutora);
	}
	
	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(final AelItemSolicitacaoExames itemSolicitacaoExame)
			throws BaseException {

		return getItemSolicitacaoExameRN().buscarAmostrasPorSolicitacaoExame(itemSolicitacaoExame);	
	}
	
	protected AelSolicitacaoExameDAO getSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}

	protected AghParametros getParametroRotinaProgrInt() throws BaseException {
		return getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HORAS_ROTINA_PROGR_INT);
	}

	protected ItemSolicitacaoExameRN getItemSolicitacaoExameRN() {
		return itemSolicitacaoExameRN;
	}

	protected AelPermissaoUnidSolicDAO getAelPermissaoUnidSolicDAO() {
		return aelPermissaoUnidSolicDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected VAelSolicAtendsDAO getVAelSolicAtendsDAO() {
		return vAelSolicAtendsDAO;
	}
	
	protected AelAgrpPesquisasDAO getAelAgrpPesquisasDAO() {
		return aelAgrpPesquisasDAO;
	}
	
	protected AelAgrpPesquisaXExameDAO getAelAgrpPesquisaXExameDAO() {
		return aelAgrpPesquisaXExameDAO;
	}

//	protected AghFeriadosDAO getAghFeriadosDAO() {
//		return aghFeriadosDAO;
//	}

	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}

	protected AelExamesLimitadoAtendDAO getExamesLimitadoAtendDAO() {
		return aelExamesLimitadoAtendDAO;
	}
	
	protected ItemSolicitacaoExameEnforceRN getItemSolicitacaoExameEnforceRN() {
		return itemSolicitacaoExameEnforceRN;
	}

	protected AelAmostrasDAO getAmostrasDAO() {
		return aelAmostrasDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected AelExameDeptConvenioDAO getAelExameDeptConvenioDAO() {
		return aelExameDeptConvenioDAO;
	}

	protected AelMaterialAnaliseDAO getAelMaterialAnaliseDAO() {
		return aelMaterialAnaliseDAO;
	}

	protected AelExamesProvaDAO getExamesProvaDAO() {
		return aelExamesProvaDAO;
	}

	protected AelHorarioRotinaColetasDAO getHorarioRotinaColetasDAO() {
		return aelHorarioRotinaColetasDAO;
	}

	protected AelRecomendacaoExameDAO getAelRecomendacaoExameDAO() {
		return aelRecomendacaoExameDAO;
	}

	protected AelExigenciaExameDAO getAelExigenciaExameDAO() {
		return aelExigenciaExameDAO;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}

	protected IQuestionarioExamesFacade getQuestionarioExamesFacade() {
		return this.questionarioExamesFacade;
	}

	protected VerificarPermissaoHorariosRotina getVerificarPermissaoHorariosRotina() {
		return this.verificarPermissaoHorariosRotina;
	}

	protected VerificarPermissoesExame getVerificarPermissoesExame() {
		return this.verificarPermissoesExame;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public Boolean verificaConvenioSus(AghAtendimentos atendimento,
			AelAtendimentoDiversos atendimentoDiverso) {
		FatConvenioSaude convenio = this.getItemSolicitacaoExameRN().obterFatConvenioSaude(atendimento, atendimentoDiverso);
		
		return convenio !=null && DominioGrupoConvenio.S == convenio.getGrupoConvenio();
	}
		
}
