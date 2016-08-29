package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamExtratoControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamInterconsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLogEmUsosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoRealizadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRegistroDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSituacaoAtendimentosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemEvolucaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoControle;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAlergias;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamExtratoControles;
import br.gov.mec.aghu.model.MamExtratoControlesId;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamLogEmUsos;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class MarcacaoConsultaRN extends BaseBusiness{
	
	private static final long serialVersionUID = -2008260252241803283L;
	private static final Log LOG = LogFactory.getLog(MarcacaoConsultaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private ProcedimentoAtendimentoConsultaRN procedimentoAtendimentoConsultaRN;
	
	@EJB
	private MarcacaoConsultaAtualizacaoPacienteRN marcacaoConsultaAtualizacaoPacienteRN;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private MamTipoItemAnamnesesDAO mamTipoItemAnamnesesDAO;

	@Inject
	private MamItemEvolucoesDAO mamItemEvolucoesDAO;

	@Inject
	private MamRegistroDAO mamRegistroDAO;

	@Inject
	private MamNotaAdicionalEvolucoesDAO mamNotaAdicionalEvolucoesDAO;

	@Inject
	private MamControlesDAO mamControlesDAO;

	@Inject
	private MamExtratoControlesDAO mamExtratoControlesDAO;

	@Inject
	private MamLogEmUsosDAO mamLogEmUsosDAO;

	@Inject
	private MamItemAnamnesesDAO mamItemAnamnesesDAO;

	@Inject
	private MamTipoItemEvolucaoDAO mamTipoItemEvolucaoDAO;

	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;

	@Inject
	private MamSituacaoAtendimentosDAO mamSituacaoAtendimentosDAO;

	@Inject
	private AacConsultasDAO aacConsultasDAO;

	@Inject
	private MamProcedimentoRealizadoDAO mamProcedimentoRealizadoDAO;

	@Inject
	private MamTriagensDAO mamTriagensDAO;

	@Inject
	private MamNotaAdicionalAnamnesesDAO mamNotaAdicionalAnamnesesDAO;

	@Inject
	private MamInterconsultasDAO mamInterconsultasDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	@Inject
	private GestaoInterconsultasRN gestaoInterConsultasRN;
	
	
	public enum MarcacaoConsultaRNExceptionCode implements BusinessExceptionCode {
		AIP_USUARIO_NAO_CADASTRADO, MAM_01506, AAC_00272, AAC_00273, MAM_04009
		, AAC_00263, AAC_00733, AAC_00187, MAM_00304, RAP_00175, MAM_00625, MAM_00626, MAM_00627, MAM_00529
		, MAM_00595, MAM_00596, MAM_00530, MAM_00580, MAM_00581, ERRO_CLONE_NOTA_ADICIONAL_ANAMNESES
		, ERRO_CLONE_NOTA_ADICIONAL_EVOLUCOES, MAM_00816, MAM_00817, MAM_00818, MAM_00819, MAM_00524, MAM_00597, MAM_00598, MAM_00525
		, MAM_00745, MAM_00746, ERRO_OBTER_NOME_MICRO, ERRO_CLONE_PROCEDIMENTO_REALIZADO, AAC_00245, MAM_00650, MAM_00651, MAM_00652, MAM_03792, MAM_03793, MAM_03794, MAM_04316, MAM_04317, MAM_04314, MAM_04315, MAM_01226, MAM_01651, MAM_01229, MAM_01230, MAM_01232, MAM_01233, MAM_01234, MAM_01235, MAM_01237, MAM_01238, MAM_01242, MAM_01243, MAM_01244, MAM_01245, MAM_02332, MAM_02434, MAM_02435, MAM_03426, MAM_03427, MAM_04122, MAM_03371, MAM_03372, MAM_03659;
	}
	
	/**
	 * ORADB: Procedure MAMP_ATU_PAC_CODIGO
	 * @throws ApplicationBusinessException  
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void atualizarPacienteAmbulatorio(Integer conNumero, Integer pacCodigo, String nomeMicrocomputador) throws ApplicationBusinessException{
		AipPacientes paciente = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
		/*
		 * BEGIN UPDATE mam_alta_sumarios SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01226#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamAltaSumarios(conNumero, paciente);
	
		/*
		 * BEGIN UPDATE mam_pista_notif_infeccoes pnn SET pnn.pac_codigo =
		 * p_pac_codigo WHERE pnn.con_numero = p_con_numero OR EXISTS (SELECT 1
		 * FROM mam_resposta_notif_infeccoes rni WHERE rni.pnn_seq = pnn.seq AND
		 * rni.con_numero = p_con_numero); EXCEPTION WHEN NO_DATA_FOUND THEN
		 * NULL; WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01651#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamPistaNotifInfeccao(conNumero, paciente, pacCodigo);

		List<MamAnamneses> listaAnamnese = this.getMamAnamnesesDAO().pesquisarAnamnesePorNumeroConsulta(conNumero);
		for(MamAnamneses anamnese: listaAnamnese){
			anamnese.setPaciente(paciente);
			this.atualizarAnamnese(anamnese);
		}
		
		List<MamControles> listaControle = this.getMamControlesDAO().pesquisarControlePorNumeroConsulta(conNumero);
		for(MamControles controle: listaControle){
			controle.setPaciente(paciente);
			this.atualizarControles(controle, nomeMicrocomputador);
		}
		
		/*
		 * BEGIN UPDATE mam_atestados SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01229#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamAtestados(conNumero, paciente);		
		
		/*
		 * BEGIN UPDATE mam_diagnosticos SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01230#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamDiagnosticos(conNumero, paciente);
		
		List<MamEvolucoes> listaEvolucao = this.getMamEvolucoesDAO().pesquisarEvolucaoPorNumeroConsulta(conNumero);
		for(MamEvolucoes evolucao: listaEvolucao){
			evolucao.setPaciente(paciente);
			this.atualizarEvolucao(evolucao);
		}
		
		/*
		 * BEGIN UPDATE mam_figura_anamneses SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01232#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamFigurasAnamnese(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_figura_evolucoes SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01233#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamFigurasEvolucoes(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_interconsultas SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01234#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamInterconsultas(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_laudo_aihs SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01235#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamLaudoAihs(conNumero, paciente);

		List<MamLogEmUsos> listaLogEmUso = this.getMamLogsEmUsoDAO().pesquisarLogEmUsoPorNumeroConsulta(conNumero);
		for(MamLogEmUsos logEmUso: listaLogEmUso){
			logEmUso.setPaciente(paciente);
			this.atualizarLogEmUso(logEmUso);
		}
		
		/*
		 * BEGIN UPDATE mam_medicamento_ativos SET pac_codigo = p_pac_codigo
		 * WHERE con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN
		 * NULL; WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01237#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamMedicamentosAtivos(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_motivo_atendimentos SET pac_codigo = p_pac_codigo
		 * WHERE con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN
		 * NULL; WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01238#1'||SQLERRM); END;
		 */
		
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamMotivoAtendimentos(conNumero, paciente);
	
		List<MamNotaAdicionalAnamneses> listaNotaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO()
				.pesquisarNotaAdicionalAnamnesesPorNumeroConsulta(conNumero);
		for(MamNotaAdicionalAnamneses notaAdicionalAnamneses: listaNotaAdicionalAnamneses){
			MamNotaAdicionalAnamneses notaAdicionalAnamnesesOld = null;
			if(notaAdicionalAnamneses!=null){
				try {
					notaAdicionalAnamnesesOld = (MamNotaAdicionalAnamneses) BeanUtils.cloneBean(notaAdicionalAnamneses);
				} catch (Exception e) {
					logError("Exceção capturada: ", e);
					new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_ANAMNESES);
				} 
			}
			notaAdicionalAnamneses.setPaciente(paciente);
			this.atualizarNotaAdicionalAnamneses(notaAdicionalAnamneses, notaAdicionalAnamnesesOld);
		}
		
		List<MamNotaAdicionalEvolucoes> listaNotaAdicionalEvolucoes = this.getMamNotaAdicionalEvolucoesDAO()
				.pesquisarNotaAdicionalEvolucoesPorNumeroConsulta(conNumero);
		for(MamNotaAdicionalEvolucoes notaAdicionalEvolucoes: listaNotaAdicionalEvolucoes){
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoesOld = null;
			if(notaAdicionalEvolucoes!=null){
				try {
					notaAdicionalEvolucoesOld = (MamNotaAdicionalEvolucoes) BeanUtils.cloneBean(notaAdicionalEvolucoes);
				} catch (Exception e) {
					logError("Exceção capturada: ", e);
					new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_EVOLUCOES);
				} 
			}
			notaAdicionalEvolucoes.setPaciente(paciente);
			this.atualizarNotaAdicionalEvolucoes(notaAdicionalEvolucoes, notaAdicionalEvolucoesOld);
		}
		
		MamProcedimentoRealizado procedimentoRealizadoOld = null;
		List<MamProcedimentoRealizado> listaProcedimentoRealizado = this.getMamProcedimentoRealizadoDAO()
				.pesquisarProcedimentoRealizadoPorNumeroConsulta(conNumero);
		for(MamProcedimentoRealizado procedimentoRealizado: listaProcedimentoRealizado){
			try {
				procedimentoRealizadoOld = (MamProcedimentoRealizado) BeanUtils.cloneBean(procedimentoRealizado);
			} catch (Exception e) {
				logError("Exceção capturada: ", e);
				new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.ERRO_CLONE_PROCEDIMENTO_REALIZADO);
			}
			procedimentoRealizado.setPaciente(paciente);
			this.getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(procedimentoRealizadoOld, procedimentoRealizado, true);
		}
		/*
		 * BEGIN UPDATE mam_receituarios SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01242#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamReceituarios(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_receituario_cuidados SET pac_codigo = p_pac_codigo
		 * WHERE con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN
		 * NULL; WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01243#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamReceituarioCuidados(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_relatorios SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01244#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamRelatorios(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_solicitacao_retornos SET pac_codigo = p_pac_codigo
		 * WHERE con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN
		 * NULL; WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-01245'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamSolicitacaoRetorno(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_lembretes SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-02332#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamLembretes(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_solic_hemoterapicas SET pac_codigo = p_pac_codigo
		 * WHERE con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN
		 * NULL; WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-02434#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamSolicHemoterapicas(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_solic_procedimentos SET pac_codigo = p_pac_codigo
		 * WHERE con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN
		 * NULL; WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-02435#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamSolicProcedimentos(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_alergias SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-03426#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamAlergias(conNumero, paciente);
		
		/*
		 * BEGIN UPDATE mam_destinos SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-03427#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamDestinos(conNumero, pacCodigo);
		
		/*
		 * BEGIN UPDATE mam_imp_diagnosticas SET pac_codigo = p_pac_codigo WHERE
		 * con_numero = p_con_numero; EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
		 * WHEN regra_negocio THEN RAISE; WHEN OTHERS THEN
		 * raise_application_error (-20000, 'MAM-04122#1'||SQLERRM); END;
		 */
		marcacaoConsultaAtualizacaoPacienteRN.atualizarMamImpDiagnosticas(conNumero, paciente);
		
		/*
		 * FOR r_tei IN c_tei(p_con_numero) LOOP BEGIN UPDATE mam_triagens SET
		 * pac_codigo = p_pac_codigo WHERE seq = r_tei.trg_seq AND pac_codigo <>
		 * p_pac_codigo; EXCEPTION WHEN NO_DATA_FOUND THEN NULL; WHEN
		 * regra_negocio THEN RAISE; WHEN OTHERS THEN raise_application_error
		 * (-20000, 'MAM-03371#1'||SQLERRM); END; -- -- BEGIN UPDATE
		 * mam_trg_alergias SET pac_codigo = p_pac_codigo WHERE trg_seq =
		 * r_tei.trg_seq AND pac_codigo <> p_pac_codigo; EXCEPTION WHEN
		 * NO_DATA_FOUND THEN NULL; WHEN regra_negocio THEN RAISE; WHEN OTHERS
		 * THEN raise_application_error (-20000, 'MAM-03372#1'||SQLERRM); END;
		 * -- -- BEGIN UPDATE mam_paciente_minha_listas SET pac_codigo =
		 * p_pac_codigo WHERE trg_seq = r_tei.trg_seq AND pac_codigo <>
		 * p_pac_codigo; EXCEPTION WHEN NO_DATA_FOUND THEN NULL; WHEN
		 * regra_negocio THEN RAISE; WHEN OTHERS THEN raise_application_error
		 * (-20000, 'MAM-03659#1'||SQLERRM); END; -- -- verifica se o novo
		 * codigo de paciente possui prontuário, -- senão gera um prontuário
		 * virtual em função da emergência. -- v_prontuario :=
		 * mamk_trg_generica.mamc_ins_prnt_virtua (p_pac_codigo); -- END LOOP;
		 */
		
		marcacaoConsultaAtualizacaoPacienteRN.gerarProntuarioVirtualAtualizarDependencias(conNumero, pacCodigo, paciente, nomeMicrocomputador);
	}
	
	/**
	 * ORADB Procedure MAMP_AMBU_AGUARDANDO
	 * 
	 * @param notaAdicionalAnamneses
	 *  
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarPacienteAguardando(Integer consultaNumero, Integer pacCodigo, Integer grdSeq, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		Short unidadeFuncionalSeq = null;
		Long controleSeq = null;
		Boolean passaAguardando = false;
		Integer seqSitAguardando = Integer.valueOf(0);
		Short extratoControleSeqP = Short.valueOf("0");

		AacGradeAgendamenConsultas gradeAgendamentoConsulta = this.getAacGradeAgendamenConsultasDAO().obterGradeAgendamentoParaMarcacaoConsultaEmergencia(grdSeq);
		if(gradeAgendamentoConsulta == null){
			unidadeFuncionalSeq = null;
		}
		if (this.getPesquisaInternacaoFacade()
				.verificarCaracteristicaDaUnidadeFuncional(unidadeFuncionalSeq, ConstanteAghCaractUnidFuncionais.EMERGENCIA_OBSTETRICA)
				.equals(DominioSimNao.S)
				|| this.getPesquisaInternacaoFacade()
						.verificarCaracteristicaDaUnidadeFuncional(unidadeFuncionalSeq, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)
						.equals(DominioSimNao.S)) {
			passaAguardando = true;
		} else {
			passaAguardando = false;
		}
		if(passaAguardando){
			AghParametros parametroAguardando = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_AGUARDANDO);
			if(parametroAguardando.getVlrNumerico()!=null){
					seqSitAguardando = parametroAguardando.getVlrNumerico().intValue();	
			}
			List<MamControles> listaControle = this.getMamControlesDAO().pesquisarControlePorNumeroConsulta(consultaNumero);
			for(MamControles controle: listaControle){
				if(controle==null){
					controleSeq = null;
				}
				if(controleSeq!=null){
					controle.setDthrMovimento(new Date());
					MamSituacaoAtendimentos situacaoAtendimento = this.getMamSituacaoAtendimentosDAO().obterPorChavePrimaria(seqSitAguardando);
					controle.setSituacaoAtendimento(situacaoAtendimento);
					this.getMamControlesDAO().atualizar(controle);
					this.getMamControlesDAO().flush();
					extratoControleSeqP = this.obterExtratoControleSeqP(controle);
					MamExtratoControles extratoControle = new MamExtratoControles();
					MamExtratoControlesId extratoControleId = new MamExtratoControlesId();
					extratoControleId.setSeqp(extratoControleSeqP);
					extratoControleId.setCtlSeq(controleSeq);
					extratoControle.setId(extratoControleId);
					extratoControle.setSituacaoAtendimento(situacaoAtendimento);
					extratoControle.setDthrMovimento(new Date());
					this.inserirExtratoControles(extratoControle, nomeMicrocomputador);
					controle.getExtratoControles().add(extratoControle);
				} else {
					controle.setSeq(controleSeq);
					controle.setDthrMovimento(new Date());
					controle.setSituacao(DominioSituacaoControle.L);
					MamSituacaoAtendimentos situacaoAtendimento = this.getMamSituacaoAtendimentosDAO().obterPorChavePrimaria(seqSitAguardando);
					controle.setSituacaoAtendimento(situacaoAtendimento);
					AipPacientes paciente = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
					controle.setPaciente(paciente);
					AacConsultas consulta = this.getAacConsultasDAO().obterConsulta(consultaNumero);
					controle.setConsulta(consulta);
					this.inserirControles(controle, nomeMicrocomputador);
					MamExtratoControles extratoControle = new MamExtratoControles();
					extratoControle.setControle(controle);
					MamExtratoControlesId extratoControleId = new MamExtratoControlesId();
					extratoControleId.setSeqp(Short.valueOf("1"));
					extratoControleId.setCtlSeq(controleSeq);
					extratoControle.setId(extratoControleId);
					extratoControle.setDthrMovimento(new Date());
					extratoControle.setSituacaoAtendimento(situacaoAtendimento);
					this.inserirExtratoControles(extratoControle, nomeMicrocomputador);
					controle.getExtratoControles().add(extratoControle);
				}

			}
		}
	}
	
	/**
	 * ORADB FUNCTION c_get_exc_seqp
	 * 
	 */
	public Short obterExtratoControleSeqP(MamControles controle){
		return getMamExtratoControlesDAO().nextSeqp(controle);
	}
	
	/**
	 * ORADB Trigger MAMT_NAA_BRU
	 * 
	 * @param notaAdicionalAnamneses
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarNotaAdicionalAnamneses(MamNotaAdicionalAnamneses notaAdicionalAnamneses, MamNotaAdicionalAnamneses notaAdicionalAnamnesesOld)
			throws ApplicationBusinessException {
		if(notaAdicionalAnamnesesOld.getPendente().equals(DominioIndPendenteAmbulatorio.V)){
			if(!notaAdicionalAnamneses.getDescricao().equals(notaAdicionalAnamnesesOld.getDescricao())){
				this.validarAtualizacaoNotaAdicionalAnamneses();
			}
		}
		this.getMamNotaAdicionalAnamnesesDAO().atualizar(notaAdicionalAnamneses);
		this.getMamNotaAdicionalAnamnesesDAO().flush();
	}
	
	/**
	 * ORADB Trigger MAMT_NEV_BRU
	 * 
	 * @param notaAdicionalEvolucoes
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes notaAdicionalEvolucoes, MamNotaAdicionalEvolucoes notaAdicionalEvolucoesOld)
			throws ApplicationBusinessException {
		
		if(notaAdicionalEvolucoesOld.getPendente().equals(DominioIndPendenteAmbulatorio.V)){
			if(!notaAdicionalEvolucoes.getDescricao().equals(notaAdicionalEvolucoesOld.getDescricao())){
				this.validarAtualizacaoNotaAdicionalEvolucoes();
			}
		}
		this.getMamNotaAdicionalEvolucoesDAO().atualizar(notaAdicionalEvolucoes);
	}
	
	/**
	 * ORADB Trigger MAMT_ALG_BRU
	 * 
	 * @param alergia
	 * @param alergiaOld
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarAlergias(MamAlergias alergia, MamAlergias alergiaOld) throws ApplicationBusinessException{
		if ("V".equalsIgnoreCase(alergiaOld.getIndPendente())){
			if (CoreUtil.modificados(alergiaOld.getDescricao(), alergia.getDescricao())){
				TipoOperacaoEnum tipoOperacao = TipoOperacaoEnum.UPDATE;
				verificarAlergiaValidada(tipoOperacao);
			}
		}
		
		if (CoreUtil.modificados(alergiaOld.getData(), alergia.getData())){
			verificarDataAlergia(alergia.getPaciente(), alergia.getData());
		}
		
		if (alergia.getDataFim() != null && CoreUtil.modificados(alergiaOld.getDataFim(), alergia.getDataFim())){
			verificarDataFimAlergia(alergia.getData(), alergia.getDataFim());
		}
	}
	
	/**
	 * ORADB procedure mamk_alg_rn.rn_algp_ver_data_fim
	 * 
	 * @param data
	 * @param dataFim
	 * @throws ApplicationBusinessException 
	 */
	public void verificarDataFimAlergia(Date data, Date dataFim) throws ApplicationBusinessException{
		if (dataFim.before(data)){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_04314);		
		}
		
		if (dataFim.after(new Date())){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_04315);		
		}

	}
	
	/**
	 * ORADB RN_ALGP_VER_DATA
	 * 
	 * @throws ApplicationBusinessException 
	 */
	public void verificarDataAlergia(AipPacientes paciente, Date data) throws ApplicationBusinessException{
		if (data.after(new Date())){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_04316);			
		}
		Date dataNascimentoMenosNoveMeses = DateUtil.adicionaMeses(paciente.getDtNascimento(), -9);
		if (dataNascimentoMenosNoveMeses != null){
			if (data.before(dataNascimentoMenosNoveMeses)){
				throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_04317);
			}
		}
	}
	
	/**
	 * ORADB mamk_alg_rn.rn_algp_ver_validado
	 * 
	 * @param alergia
	 * @param alergiaOld
	 * @throws ApplicationBusinessException 
	 */
	private void verificarAlergiaValidada(TipoOperacaoEnum tipoOperacao) throws ApplicationBusinessException {
		
		if (TipoOperacaoEnum.INSERT.equals(tipoOperacao)){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_03792);
		} else if (TipoOperacaoEnum.UPDATE.equals(tipoOperacao)) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_03793);
		} else if (TipoOperacaoEnum.DELETE.equals(tipoOperacao)) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_03794);
		}
		
	}

	/**
	 * ORADB Trigger MAMT_ATE_BRU
	 * @param atestado
	 * @param atestadoOld
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarAtestados(MamAtestados atestado, MamAtestados atestadoOld) throws ApplicationBusinessException{
		
		if (DominioIndPendenteAmbulatorio.V.equals(atestadoOld.getIndPendente())){
			if (CoreUtil.modificados(atestadoOld.getDataInicial(), atestado.getDataInicial())
					|| CoreUtil.modificados(atestadoOld.getDataFinal(), atestado.getDataFinal())
					|| CoreUtil.modificados(atestadoOld.getObservacao(), atestado.getObservacao())
					|| CoreUtil.modificados(
							atestadoOld.getMamTipoAtestado() == null ? null : atestadoOld.getMamTipoAtestado().getSeq(), 
							atestado.getMamTipoAtestado() == null ? null : atestado.getMamTipoAtestado().getSeq())
					|| CoreUtil.modificados(atestadoOld.getNomeAcompanhante(), atestado.getNomeAcompanhante())
					|| CoreUtil.modificados(
							atestadoOld.getAghCid() == null ? null : atestadoOld.getAghCid().getSeq(), 
							atestado.getAghCid() == null ? null : atestado.getAghCid().getSeq())
					|| CoreUtil.modificados(atestadoOld.getNroVias(), atestado.getNroVias())){
				
				TipoOperacaoEnum tipoOperacao = TipoOperacaoEnum.UPDATE;
				verificarRegistroValidado(tipoOperacao);
			}
		}
	}
	
	/**
	 * ORADB procedure mamk_ate_rn.rn_atep_ver_validado
	 * @param atestado
	 * @param atestadoOld
	 * @throws ApplicationBusinessException 
	 */
	private void verificarRegistroValidado(TipoOperacaoEnum tipoOperacao) throws ApplicationBusinessException {
		if (TipoOperacaoEnum.INSERT.equals(tipoOperacao)){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00650);
		}
		else if (TipoOperacaoEnum.UPDATE.equals(tipoOperacao)){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00651);
		}
		else if (TipoOperacaoEnum.DELETE.equals(tipoOperacao)){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00652);
		}
		
	}
	
	/**
	 * ORADB Trigger MAMT_NEV_BRD
	 * 
	 * @param notaAdicionalEvolucoes
	 * @throws ApplicationBusinessException
	 */
	public void removerNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes notaAdicionalEvolucoes) throws ApplicationBusinessException {
		if(notaAdicionalEvolucoes.getPendente().equals(DominioIndPendenteAmbulatorio.V)){
			this.validarRemocaoNotaAdicionalEvolucoes();
		}
		this.getMamNotaAdicionalEvolucoesDAO().remover(notaAdicionalEvolucoes);
		this.getMamNotaAdicionalEvolucoesDAO().flush();
	}
	
	/**
	 * ORADB Trigger MAMT_NEV_BRI
	 * 
	 * @param notaAdicionalEvolucoes
	 * @throws ApplicationBusinessException 
	 */
	public void inserirNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes notaAdicionalEvolucoes) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(notaAdicionalEvolucoes.getServidor()==null ||notaAdicionalEvolucoes.getServidor().getId()==null){
			notaAdicionalEvolucoes.setServidor(servidorLogado);
			if(notaAdicionalEvolucoes.getServidor()==null){
				throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.RAP_00175);
			}
		}	
		if(notaAdicionalEvolucoes.getSeq()==null){
			this.getMamNotaAdicionalEvolucoesDAO().persistir(notaAdicionalEvolucoes);
		}
		if(notaAdicionalEvolucoes.getPaciente() == null || notaAdicionalEvolucoes.getPaciente().getCodigo() == null){
			if(notaAdicionalEvolucoes.getConsulta()!=null && notaAdicionalEvolucoes.getConsulta().getNumero()!=null){
				Integer consultaNumero = null;
				if(notaAdicionalEvolucoes.getConsulta()!=null) {
					consultaNumero = notaAdicionalEvolucoes.getConsulta().getNumero(); 
				}
				Integer pacCodigo = this.obterCodigoPacienteOrigem(1,consultaNumero);
				if(pacCodigo!=null && pacCodigo>0){
					AipPacientes paciente = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
					notaAdicionalEvolucoes.setPaciente(paciente);
				}
			}
		}
		if(notaAdicionalEvolucoes.getImpresso()==null){
			notaAdicionalEvolucoes.setImpresso(false);
		}
	}
	
	/**
	 * ORADB Trigger MAMT_CTL_BSU, MAMT_CTL_BRU, MAMT_CTL_ARU, MAMT_CTL_ASU  
	 * 
	 * @param controle
	 * @throws ApplicationBusinessException  
	 */
	//TODO implementar trigger quando for invocada em estórias futuras
	public void atualizarControles(MamControles controle, String nomeMicrocomputador) throws ApplicationBusinessException {
		this.preAtualizarControles(controle, nomeMicrocomputador);
		this.getMamControlesDAO().atualizar(controle);
		this.getMamControlesDAO().flush();
	}
	
	/**
	 * #42360 RN-2 TODO
	 * 
	 * @ORADB: MAMT_CTL_ASU
	 */

	public void obterFfcInterfaceAACPRJ(MamControles controle, String nomeMicrocomputador) throws ApplicationBusinessException {
		if (controle != null) {
			Short seqAntigo = controle.getSituacaoAtendimento().getSeq();
			Short seqNovo = null;

			atualizarControles(controle, nomeMicrocomputador);

			seqNovo = controle.getSituacaoAtendimento().getSeq();

			if (seqAntigo != seqNovo) {

				controle.getDthrMovimento();
				controle.getConsulta().getNumero();
				controle.getSituacaoAtendimento().getSeq();
			}

			objetosOracleDAO.ffcInterfaceAACPRJ(DateUtil.dataToString(controle.getDthrMovimento(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO),
					controle.getConsulta().getNumero(), seqAntigo, controle.getSituacaoAtendimento().getSeq(), null, null, obterLoginUsuarioLogado());
		}
	}

	/**
	 * Procedure ORADB MAMK_NAA_RN.RN_NAAP_ALTERAR
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void validarAtualizacaoNotaAdicionalAnamneses() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00580);
	}
	
	/**
	 * Procedure ORADB MAMK_NEV_RN.RN_NEVP_VER_UPDATE
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void validarAtualizacaoNotaAdicionalEvolucoes() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00745);
	}
	
	/**
	 * Procedure ORADB MAMK_NEV_RN.RN_NEVP_VER_DELETE
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void validarRemocaoNotaAdicionalEvolucoes() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00746);
	}
	
	/**
	 * Procedure ORADB MAMK_NAA_RN.RN_NAAP_EXCLUIR
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void validarExclusaoNotaAdicionalAnamneses() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00581);
	}
	
	/**
	 * ORADB Trigger MAMT_CTL_BRU  
	 * 
	 * @param controle
	 * @throws ApplicationBusinessException  
	 */
	public void preAtualizarControles(MamControles controle, String nomeMicrocomputador) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		}
		controle.setServidorAtualiza(servidorLogado);
		controle.setServidorReponsavel(servidorLogado);
		if(nomeMicrocomputador == null){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.ERRO_OBTER_NOME_MICRO);
		}
		controle.setMicNome(nomeMicrocomputador);		
	}
	
	/**
	 * Trigger ORADB MAMT_ANA_BRU
	 * 
	 * @param anamnese
	 * @throws ApplicationBusinessException 
	 */
	public MamAnamneses atualizarAnamnese(MamAnamneses anamnese) throws ApplicationBusinessException {
		if(anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.V)){
			this.verificarItemAnamnese(anamnese.getSeq());
		}
		return this.getMamAnamnesesDAO().atualizar(anamnese);
	}
	
	/**
	 * Trigger ORADB MAMT_IEV_BRU
	 * 
	 * @param anamnese
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarItemEvolucao(MamItemEvolucoes itemEvolucoes) throws ApplicationBusinessException {
		this.verificarItemEvolucaoValidado(DominioOperacaoBanco.UPD, itemEvolucoes.getEvolucao());
		this.getMamItemEvolucoesDAO().atualizar(itemEvolucoes);
		this.getMamItemEvolucoesDAO().flush();
	}
	
	/**
	 * Trigger ORADB MAMT_IEV_BRD
	 * 
	 * @param itemEvolucao
	 * @throws ApplicationBusinessException 
	 */
	public void removerItemEvolucao(MamItemEvolucoes itemEvolucao) throws ApplicationBusinessException {
		this.getMamItemEvolucoesDAO().remover(itemEvolucao);
		this.getMamItemEvolucoesDAO().flush();
	}

	/**
	 * Trigger ORADB MAMT_IAN_BRU
	 * 
	 * @param anamnese
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarItemAnamnese(MamItemAnamneses itemAnamnese) throws ApplicationBusinessException {
		this.verificarItemAnamneseValidado(DominioOperacaoBanco.UPD, itemAnamnese.getAnamnese());
		this.getMamItemAnamnesesDAO().atualizar(itemAnamnese);
		this.getMamItemAnamnesesDAO().flush();
	}
	
	/**
	 * Trigger ORADB MAMT_ANA_BRI
	 * 
	 * @param anamnese
	 * @throws ApplicationBusinessException
	 *  
	 */
	public MamAnamneses inserirAnamnese(MamAnamneses anamnese) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Integer conNumero = null;
		Integer pacCodigo = null;
		Long rgtSeq = null;
		if(anamnese.getConsulta()!=null){
			conNumero = anamnese.getConsulta().getNumero();
		}
		
		if(anamnese.getRegistro()!=null){
			rgtSeq = anamnese.getRegistro().getSeq();
		}
		
		if(anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.V)){
			this.verificarAnamneseValidado(DominioOperacaoBanco.INS);
		}
		anamnese.setServidor(servidorLogado);
		if(anamnese.getServidor()==null){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.RAP_00175);
		}
		if(anamnese.getPaciente()==null){
			if(anamnese.getConsulta()!=null){
				pacCodigo = this.obterCodigoPacienteOrigem(1, conNumero);
				if(pacCodigo!=null && pacCodigo>0){
					AipPacientes paciente = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
					anamnese.setPaciente(paciente);
				}
			} else {
				pacCodigo = this.obterCodigoPacienteOrigem(7, rgtSeq.intValue());
				if(pacCodigo!=null && pacCodigo>0){
					AipPacientes paciente = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
					anamnese.setPaciente(paciente);
				}
			}
		}
		this.getMamAnamnesesDAO().persistir(anamnese);
		this.getMamAnamnesesDAO().flush();
		return anamnese;
	}
	
	/**
	 * Trigger ORADB MAMT_EVO_BRI
	 * 
	 * @param evolucao
	 * @throws ApplicationBusinessException
	 *  
	 */
	public MamEvolucoes inserirEvolucao(MamEvolucoes evolucao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Integer conNumero = null;
		Integer pacCodigo = null;
		Long rgtSeq = null;
		if(evolucao.getConsulta()!=null){
			conNumero = evolucao.getConsulta().getNumero();
		}
		
		if(evolucao.getRegistro()!=null){
			rgtSeq = evolucao.getRegistro().getSeq();
		}
	
		if(evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.V)){
			this.verificarEvolucaoValidado(DominioOperacaoBanco.INS);
		}
		
		evolucao.setServidor(servidorLogado);
		if(evolucao.getServidor()==null){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.RAP_00175);
		}
		if(evolucao.getPaciente()==null){
			if(evolucao.getConsulta()!=null){
				pacCodigo = this.obterCodigoPacienteOrigem(1, conNumero);
				if(pacCodigo!=null && pacCodigo>0){
					AipPacientes paciente = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
					evolucao.setPaciente(paciente);
				}
			} else {
				pacCodigo = this.obterCodigoPacienteOrigem(7, rgtSeq.intValue());
				if(pacCodigo!=null && pacCodigo>0){
					AipPacientes paciente = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
					evolucao.setPaciente(paciente);
				}
			}
		}
		 this.getMamEvolucoesDAO().persistir(evolucao);
		 this.getMamEvolucoesDAO().flush();
		 return evolucao;
	}
	
	/**
	 * Trigger ORADB MAMT_IAN_BRI
	 * 
	 * @param itemAnamnese
	 * @throws ApplicationBusinessException
	 *  
	 */
	public void inserirItemAnamnese(MamItemAnamneses itemAnamnese) throws ApplicationBusinessException {
		this.verificarTipoAnamneseAtivo(itemAnamnese.getId().getTinSeq());
		if (itemAnamnese.getAnamnese() == null && itemAnamnese.getId().getAnaSeq() != null) {
			itemAnamnese.setAnamnese(mamAnamnesesDAO.obterPorChavePrimaria(itemAnamnese.getId().getAnaSeq()));
		}
		this.verificarItemAnamneseValidado(DominioOperacaoBanco.INS,itemAnamnese.getAnamnese());
		this.getMamItemAnamnesesDAO().persistir(itemAnamnese);
		this.getMamItemAnamnesesDAO().flush();
	}
	
	/**
	 * Trigger ORADB MAMT_IEV_BRI
	 * 
	 * @param itemEvolucao
	 * @throws ApplicationBusinessException
	 *  
	 */
	public void inserirItemEvolucao(MamItemEvolucoes itemEvolucao) throws ApplicationBusinessException {
		this.verificarTipoEvolucaoAtivo(itemEvolucao.getId().getTieSeq());
		this.verificarItemEvolucaoValidado(DominioOperacaoBanco.INS,itemEvolucao.getEvolucao());
		this.getMamItemEvolucoesDAO().persistir(itemEvolucao);
		this.getMamItemEvolucoesDAO().flush();
	}
	
	/**
	 * Trigger ORADB MAMT_IAN_BRD
	 *  
	 * @param itemAnamnese
	 * @throws ApplicationBusinessException
	 *  
	 */
	 public void removerItemAnamnese(MamItemAnamneses itemAnamnese) throws ApplicationBusinessException {
		this.verificarItemAnamneseValidado(DominioOperacaoBanco.DEL,itemAnamnese.getAnamnese());
		this.getMamItemAnamnesesDAO().remover(itemAnamnese);
		this.getMamItemAnamnesesDAO().flush();
	}
	 
	 /**
	  * Trigger MAMT_NAA_BRD
	 * 
	  * @param notaAdicionalAnamneses
	  * @throws ApplicationBusinessException
	  */
	 public void removerNotaAdicionalAnamneses(MamNotaAdicionalAnamneses notaAdicionalAnamneses) throws ApplicationBusinessException {
		 if(notaAdicionalAnamneses.getPendente().equals(DominioIndPendenteAmbulatorio.V)){
			 this.validarExclusaoNotaAdicionalAnamneses();
		 }
		 this.getMamNotaAdicionalAnamnesesDAO().remover(notaAdicionalAnamneses);
	}
	
	/**
	 * Trigger ORADB MAMT_ANA_BRD
	 * 
	 * @param anamnese
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void removerAnamnese(MamAnamneses anamnese) throws ApplicationBusinessException {
		if(anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.V)){
			this.verificarAnamneseValidado(DominioOperacaoBanco.DEL);
		}
		this.getMamAnamnesesDAO().remover(anamnese);
		this.getMamAnamnesesDAO().flush();
	}
	
	/**
	 * Trigger ORADB MAMT_EVO_BRD
	 * 
	 * @param evolucao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void removerEvolucao(MamEvolucoes evolucao) throws ApplicationBusinessException {
		if(evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.V)){
			this.verificarEvolucaoValidado(DominioOperacaoBanco.DEL);
		}
		this.getMamEvolucoesDAO().remover(evolucao);
		this.getMamEvolucoesDAO().flush();
	}

	/**
	 * ORADB Trigger MAMT_EVO_BRU 
	 * 
	 * @param evolucao
	 * @throws ApplicationBusinessException 
	 */
	public MamEvolucoes atualizarEvolucao(MamEvolucoes evolucao) throws ApplicationBusinessException {
		if(evolucao.getPaciente().equals(DominioIndPendenteAmbulatorio.V)){
			this.verificarItemEvolucao(evolucao.getSeq());
		}
		return this.getMamEvolucoesDAO().merge(evolucao);
	}
	
	/**
	 * ORADB Trigger MAMT_LES_BRU
	 * 
	 * @param logEmUso
	 */
	//TODO implementar trigger quando for invocada em estórias futuras
	public void atualizarLogEmUso(MamLogEmUsos logEmUso) {
		this.getMamLogsEmUsoDAO().atualizar(logEmUso);
		this.getMamLogsEmUsoDAO().flush();
	}

	/**
	 * ORADB Trigger MAMT_IEO_BSU,MAMT_IEO_BRU, MAMT_IEO_ARU, MAMT_IEO_ASU 
	 * 
	 * @param interconsulta
	 */
	public void atualizarInterconsultas(MamInterconsultas interconsulta) throws ApplicationBusinessException{
		gestaoInterConsultasRN.persistirInterconsulta(interconsulta);
		this.getMamInterconsultasDAO().flush();
	}

	/**
	 * Trigger ORADB MAMT_NAA_BRI
	 * 
	 * @param notaAdicionalAnamneses
	 * @throws ApplicationBusinessException
	 * 
	 */
	@SuppressWarnings("ucd")
	public void inserirNotaAdicionalAnamnese(MamNotaAdicionalAnamneses notaAdicionalAnamneses) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (notaAdicionalAnamneses.getSeq() == null) {
			this.getMamNotaAdicionalAnamnesesDAO().persistir(notaAdicionalAnamneses);
		}

		if (notaAdicionalAnamneses.getServidor() == null || notaAdicionalAnamneses.getServidor().getId() == null) {
			notaAdicionalAnamneses.setServidor(servidorLogado);
		}
		if (notaAdicionalAnamneses.getPaciente() == null || notaAdicionalAnamneses.getPaciente().getCodigo() == null) {
			Integer consultaNumero = null;
			if (notaAdicionalAnamneses.getConsulta() != null) {
				consultaNumero = notaAdicionalAnamneses.getConsulta().getNumero();
			}
			if (notaAdicionalAnamneses.getConsulta() != null && notaAdicionalAnamneses.getConsulta().getNumero() != null) {
				Integer pacCodigo = this.obterCodigoPacienteOrigem(1, consultaNumero);
				if (pacCodigo != null && pacCodigo > 0) {
					AipPacientes paciente = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
					notaAdicionalAnamneses.setPaciente(paciente);
				}
			}
		}
	}
	
	
	/**
	 * ORADB Trigger MAMT_EXC_BRI
	 * 
	 * @param controle
	 * @throws ApplicationBusinessException  
	 */
	public void inserirExtratoControles(MamExtratoControles extrato, String nomeMicrocomputador) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		extrato.setCriadoEm(new Date());
		
		// Obtém Servidor
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		}
		extrato.setServidor(servidorLogado);
		
		if(nomeMicrocomputador == null){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.ERRO_OBTER_NOME_MICRO);
		}
		extrato.setMicNome(nomeMicrocomputador);
				
		this.getMamExtratoControlesDAO().persistir(extrato);
		this.getMamExtratoControlesDAO().flush();
	}
	
	/**
	 * ORADB Trigger MAMT_CTL_BRI
	 * 
	 * @param controle
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void inserirControles(MamControles controle, String nomeMicrocomputador) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Integer codPaciente = null;
		
		controle.setCriadoEm(new Date());
		
		// Obtém Servidor
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		} else {
			controle.setServidor(servidorLogado);
			controle.setServidorReponsavel(servidorLogado);
		}
		
		if(controle.getSituacao().equals(DominioSituacaoControle.U)){
				controle.setDthrMovimento(new Date());
		}
	
		if(controle.getPaciente().getCodigo()==null) {
			if(controle.getConsulta().getNumero()!=null) {
				codPaciente = obterCodigoPacienteOrigem(1, controle.getConsulta().getNumero());
				if(codPaciente!=null && codPaciente>0) {
					controle.getPaciente().setCodigo(codPaciente);
				}	
			}
		}
		if(nomeMicrocomputador == null){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.ERRO_OBTER_NOME_MICRO);
		}
		controle.setMicNome(nomeMicrocomputador);	
				
		this.getMamControlesDAO().persistir(controle);
		this.getMamControlesDAO().flush();
	}
	
	/**
	 * ORADB FUNCTION MAMC_GET_PACIENTE Retorna o código do paciente a partir de
	 * uma determinada origem.
	 * 
	 * @param pOrigem
	 *            , pIdentificador
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Integer obterCodigoPacienteOrigem(Integer pOrigem, Integer pIdentificador) {
		Integer pacCodigo;
		Integer vOrigem;
		Integer vIdentificador;
		
		if(pOrigem==null){
			vOrigem = 0;
		} else {
			if(pOrigem!=null && pOrigem <0){
				vOrigem = 0;
			} else {
				vOrigem = pOrigem;
			}
		}
		
		if(pIdentificador==null){
			vIdentificador = 0;
		} else {
			if(pIdentificador!=null && pIdentificador<0){
				vIdentificador = 0;
			} else {
				vIdentificador = pIdentificador;
			}
		}
		
		pacCodigo = 0;
		
		// Recupera o Paciente da Consulta
		if(vOrigem == DominioOrigemPacienteAmbulatorio.CONSULTA.getCodigo()) {
			AacConsultas consulta = getAacConsultasDAO().obterConsulta(vIdentificador);
			if(consulta!=null){
				pacCodigo = consulta.getPaciente().getCodigo();
			} else {
				pacCodigo = 0;
			}
		}
		
		// Recupera o Paciente da Anamnese
		if(vOrigem == DominioOrigemPacienteAmbulatorio.ANAMNESE.getCodigo()) {
			MamAnamneses anamnese = getMamAnamnesesDAO().obterPorChavePrimaria(vIdentificador);
			if(anamnese!=null){
				pacCodigo = anamnese.getPaciente().getCodigo();
			} else {
				pacCodigo = 0;
			}
		}
		
		// Recupera o Paciente da Evolucao
		if(vOrigem == DominioOrigemPacienteAmbulatorio.EVOLUCAO.getCodigo()) {
			MamEvolucoes evolucao =  getMamEvolucoesDAO().obterPorChavePrimaria(vIdentificador.longValue());
			if(evolucao!=null){
				pacCodigo = evolucao.getPaciente().getCodigo();
			} else {
				pacCodigo = 0;
			}
		}
		
		// Recupera a Triagem do Número de Registro da Emergência
		if(vOrigem == DominioOrigemPacienteAmbulatorio.REG_EMERGENCIA.getCodigo()) {
			MamRegistro registro = getMamRegistroDAO().obterPorChavePrimaria(vIdentificador);
			if(registro==null){
				pacCodigo = 0;
			} else {
				vOrigem = 4;
				vIdentificador = registro.getTriagem().getSeq().intValue();
			}
		}

		// Recupera o Paciente da Triagem
		if(vOrigem == DominioOrigemPacienteAmbulatorio.TRIAGEM_EMERG.getCodigo()) {
			MamTriagens triagem = getMamTriagensDAO().obterPorChavePrimaria(vIdentificador);
			if(triagem!=null){
				pacCodigo = triagem.getPaciente().getCodigo();
			} else {
				pacCodigo = 0;
			}
		}
		
		// Recupera o Paciente do Atendimento
		if(vOrigem == DominioOrigemPacienteAmbulatorio.ATENDIMENTOS.getCodigo()) {
			AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(vIdentificador);
			if(atendimento!=null){
				pacCodigo = atendimento.getPaciente().getCodigo();
			} else {
				pacCodigo = 0;
			}
		}

		// Recupera o Paciente a partir do número de Registro analisando a
		// Triagem e o Atendimento
		if(vOrigem == DominioOrigemPacienteAmbulatorio.REG_ATEND_TRIAGEM.getCodigo()) {
			MamRegistro registro = getMamRegistroDAO().obterPorChavePrimaria(vIdentificador);
			if(registro==null){
				pacCodigo = 0;
			} else {
				if(registro.getTriagem().getSeq()!=null) {
					MamTriagens triagem = getMamTriagensDAO().obterPorChavePrimaria(registro.getTriagem().getSeq());
					if(triagem==null){
						pacCodigo = 0;
					}
				} else {
					if(registro.getAtendimento().getSeq()!=null) {
						AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(registro.getAtendimento().getSeq());
						if(atendimento==null){
							pacCodigo = 0;		
						}
					} else {
						pacCodigo = 0;
					}
				}
			}
		}
		return pacCodigo;
	}

	/**
	 * ORADB: Procedure MAMK_CTL_RN.RN_CTLP_VER_SAT
	 *  
	 * @throws ApplicationBusinessException 
	 */
	public void verificarSituacaoAtendimento(Short seqAtendimento) throws ApplicationBusinessException {
		AghParametros paramAguardando = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_AGUARDANDO);
		Short seqAguardando = paramAguardando.getVlrNumerico().shortValue();
		
		if(seqAtendimento.equals(seqAguardando)){
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01506);
		}	
	}
	
    /**
     * ORADB Procedure MAMK_ANA_RN.RN_ANAP_VER_ITENS
     * 
     * @param seq
     * @throws ApplicationBusinessException 
     */
    public void verificarItemAnamnese(Long anaSeq) throws ApplicationBusinessException{
    	List<MamItemAnamneses> lista = this.getMamItemAnamnesesDAO().pesquisarItemAnamnesesPorAnamneses(anaSeq);
    	for(MamItemAnamneses item: lista){
    		if(StringUtils.isBlank(item.getDescricao())){
    			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00304);
    		}
    	}
    }
    
    /**
     * ORADB Procedure MAMK_EVO_RN.RN_EVOP_VER_ITENS
     * 
     * @param seq
     * @throws ApplicationBusinessException 
     */
    public void verificarItemEvolucao(Long evoSeq) throws ApplicationBusinessException{
    	List<MamItemEvolucoes> lista = this.getMamItemEvolucoesDAO().pesquisarItemEvolucoesPorEvolucao(evoSeq);
    	for(MamItemEvolucoes item: lista){
    		if(StringUtils.isBlank(item.getDescricao())){
    			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00819);
    		}
    	}
    }
    
    /**
     * ORADB Procedure MAMK_ANA_RN.RN_ANAP_VER_VALIDADO
     * 
     * @param seq
     * @throws ApplicationBusinessException 
     */
    public void verificarAnamneseValidado(DominioOperacaoBanco operacao) throws ApplicationBusinessException{
    	if(operacao.equals(DominioOperacaoBanco.INS)){
    		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00625);
    	} else if(operacao.equals(DominioOperacaoBanco.UPD)){
    		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00626);
    	} else if(operacao.equals(DominioOperacaoBanco.DEL)){
    		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00627);
    	}
    }
    
    /**
     * ORADB Procedure MAMK_EVO_RN.RN_EVOP_VER_VALIDADO
     * 
     * @param seq
     * @throws ApplicationBusinessException 
     */
    public void verificarEvolucaoValidado(DominioOperacaoBanco operacao) throws ApplicationBusinessException{
    	if(operacao.equals(DominioOperacaoBanco.INS)){
    		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00816);
    	} else if(operacao.equals(DominioOperacaoBanco.UPD)){
    		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00817);
    	} else if(operacao.equals(DominioOperacaoBanco.DEL)){
    		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00818);
    	}
    }
    
    /**
     * ORADB Procedure MAMK_IAN_RN.RN_IANP_VER_VALIDADO
	 * 
     * @param operacao
     * @param anaSeq
     * @throws ApplicationBusinessException
     */
    public void verificarItemAnamneseValidado(DominioOperacaoBanco operacao, MamAnamneses anamnese) throws ApplicationBusinessException{
    	if(DominioIndPendenteAmbulatorio.V.equals(anamnese.getPendente())){
    		if(operacao.equals(DominioOperacaoBanco.INS)){
        		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00524);
        	} else if(operacao.equals(DominioOperacaoBanco.UPD)){
        		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00597);
        	} else if(operacao.equals(DominioOperacaoBanco.DEL)){
        		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00598);
        	}	
    	}
    }
    
    /**
     * ORADB Procedure MAMK_IEV_RN.RN_IEVP_VER_VALIDADO
	 * 
     * @param operacao
     * @param evoSeq
     * @throws ApplicationBusinessException
     */
    public void verificarItemEvolucaoValidado(DominioOperacaoBanco operacao, MamEvolucoes evolucao) throws ApplicationBusinessException{
    	if(evolucao != null && DominioIndPendenteAmbulatorio.V.equals(evolucao.getPendente())){
    		if(operacao.equals(DominioOperacaoBanco.INS)){
        		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00529);
        	} else if(operacao.equals(DominioOperacaoBanco.UPD)){
        		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00595);
        	} else if(operacao.equals(DominioOperacaoBanco.DEL)){
        		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00596);
        	}	
    	}
    }
    
    /**
     * ORADB Procedure MAMK_IAN_RN.RN_IANP_VER_TIPO_ATI
	 * 
     * @param tinSeq
     * @throws ApplicationBusinessException 
     */
    public void verificarTipoAnamneseAtivo(Integer tinSeq) throws ApplicationBusinessException{
    	MamTipoItemAnamneses tipoItemAnamneses = this.getMamTipoItemAnamnesesDAO().obterPorChavePrimaria(tinSeq);
    	if(tipoItemAnamneses.getSituacao().equals(DominioSituacao.I)){
    		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00530);
    	}
    }
	
    /**
     * ORADB Procedure MAMK_IEV_RN.RN_IEVP_VER_TIPO_ATI
	 * 
     * @param tieSeq
     * @throws ApplicationBusinessException 
     */
    public void verificarTipoEvolucaoAtivo(Integer tieSeq) throws ApplicationBusinessException{
    	MamTipoItemEvolucao tipoItemEvolucao = this.getMamTipoItemEvolucaoDAO().obterPorChavePrimaria(tieSeq);
    	if(tipoItemEvolucao.getSituacao().equals(DominioSituacao.I)){
    		throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_00525);
    	}
    }
    
	protected MamControlesDAO getMamControlesDAO() {
		return mamControlesDAO;
	}
	
	protected MamNotaAdicionalAnamnesesDAO getMamNotaAdicionalAnamnesesDAO() {
		return mamNotaAdicionalAnamnesesDAO;
	}
	
	protected MamNotaAdicionalEvolucoesDAO getMamNotaAdicionalEvolucoesDAO() {
		return mamNotaAdicionalEvolucoesDAO;
	}
	
	protected MamLogEmUsosDAO getMamLogsEmUsoDAO() {
		return mamLogEmUsosDAO;
	}
	
	protected MamExtratoControlesDAO getMamExtratoControlesDAO() {
		return mamExtratoControlesDAO;
	}
	
	protected MamSituacaoAtendimentosDAO getMamSituacaoAtendimentosDAO() {
		return mamSituacaoAtendimentosDAO;
	}
	
	protected MamInterconsultasDAO getMamInterconsultasDAO() {
		return mamInterconsultasDAO;
	}
	
	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}
	
	protected MamAnamnesesDAO getMamAnamnesesDAO() {
		return mamAnamnesesDAO;
	}
	
	protected MamEvolucoesDAO getMamEvolucoesDAO() {
		return mamEvolucoesDAO;
	}
	
	protected MamTriagensDAO getMamTriagensDAO() {
		return mamTriagensDAO;
	}
	
	protected MamRegistroDAO getMamRegistroDAO() {
		return mamRegistroDAO;
	}
	
	protected MamItemAnamnesesDAO getMamItemAnamnesesDAO() {
		return mamItemAnamnesesDAO;
	}
	
	protected MamTipoItemAnamnesesDAO getMamTipoItemAnamnesesDAO() {
		return mamTipoItemAnamnesesDAO;
	}
	
	protected MamItemEvolucoesDAO getMamItemEvolucoesDAO() {
		return mamItemEvolucoesDAO;
	}
	
	protected MamTipoItemEvolucaoDAO getMamTipoItemEvolucaoDAO() {
		return mamTipoItemEvolucaoDAO;
	}
	
	protected AacGradeAgendamenConsultasDAO getAacGradeAgendamenConsultasDAO() {
		return aacGradeAgendamenConsultasDAO;
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade(){
		return pesquisaInternacaoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return this.pacienteFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return (IParametroFacade) parametroFacade;
	}	

	protected MamProcedimentoRealizadoDAO getMamProcedimentoRealizadoDAO() {
		return mamProcedimentoRealizadoDAO;
	}
	
	protected CoreUtil getCoreUtil(){
		return new CoreUtil();
	}
	
	protected ProcedimentoAtendimentoConsultaRN getProcedimentoAtendimentoConsultaRN(){
		return procedimentoAtendimentoConsultaRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
