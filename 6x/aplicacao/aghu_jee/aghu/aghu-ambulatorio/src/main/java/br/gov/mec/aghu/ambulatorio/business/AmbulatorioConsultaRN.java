package br.gov.mec.aghu.ambulatorio.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.AmbulatorioRN.AmbulatorioRNExceptionCode;
import br.gov.mec.aghu.ambulatorio.business.LiberarConsultasON.LiberarConsultasONExceptionCode;
import br.gov.mec.aghu.ambulatorio.business.MarcacaoConsultaRN.MarcacaoConsultaRNExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.AacCaracteristicaGradeDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacCidConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacCondicaoAtendimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultaProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaEspecialidadeDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacHorarioGradeConsultaDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacMotivosDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacNivelBuscaDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacProcedHospEspecialidadesDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacRadioAcompTomosDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacRetornosDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacSituacaoConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamInterconsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.VAacConvenioPlanoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.configuracao.dao.AghCaractUnidFuncionaisDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.AGHUUtil;
import br.gov.mec.aghu.core.utils.DateMaker;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaGrade;
import br.gov.mec.aghu.dominio.DominioConsultaGenerica;
import br.gov.mec.aghu.dominio.DominioFuncionario;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioIndAtendimentoAnterior;
import br.gov.mec.aghu.dominio.DominioIndExameRH;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioRetornoAgenda;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultasPesquisa;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoProjetoPesquisa;
import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelProjetoPesquisasDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.dao.FatContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AacCaracteristicaGrade;
import br.gov.mec.aghu.model.AacCaracteristicaGradeId;
import br.gov.mec.aghu.model.AacCidConsultas;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalarId;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacConsultasJn;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaAgendamentoId;
import br.gov.mec.aghu.model.AacFormaEspecialidade;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeProcedHospitalar;
import br.gov.mec.aghu.model.AacGradeProcedHospitalarId;
import br.gov.mec.aghu.model.AacHorarioGradeConsulta;
import br.gov.mec.aghu.model.AacMotivos;
import br.gov.mec.aghu.model.AacNivelBusca;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacProcedHospEspecialidades;
import br.gov.mec.aghu.model.AacRadioAcompTomos;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.AelProjetoPacientesId;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghCaractEspecialidadesId;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipLocalizaPaciente;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAacConvenioPlano;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapUniServDependenteDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapUniServPlanoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.Rhfp0283DAO;
import br.gov.mec.aghu.registrocolaborador.dao.Rhfp0285DAO;
/**
 * Implementação das regras de negócio das packages Oracle: AACK_CON_RN
 * AACK_CON_2_RN AACK_CON_3_RN e de triggers que utilizam estas regras de
 * negócio.
 * 
 * @author tfelini
 */
@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.NcssTypeCount","PMD.AtributoEmSeamContextManager",
		"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class AmbulatorioConsultaRN extends BaseBusiness {

	private static final String EXCECAO_IGNORADA = "Exceção ignorada";

	@EJB
	private ProcedimentoConsultaRN procedimentoConsultaRN;
	
	@EJB
	private ManterGradeAgendamentoRN manterGradeAgendamentoRN;
	
	@EJB
	private AmbulatorioRN ambulatorioRN;
	
	@EJB
	private AmbulatorioAltaRN ambulatorioAltaRN;
	
	@EJB
	private AacRadioAcompTomosRN aacRadioAcompTomosRN;
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	@EJB
	private AmbulatorioTriagemRN ambulatorioTriagemRN;
	
	private static final Log LOG = LogFactory.getLog(AmbulatorioConsultaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private AacMotivosDAO aacMotivosDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private AacGradeProcedHospitalarDAO aacGradeProcedHospitalarDAO;
	
	@Inject
	private AacCondicaoAtendimentoDAO aacCondicaoAtendimentoDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private VAacConvenioPlanoDAO vAacConvenioPlanoDAO;
	
	@Inject
	private AacCidConsultasDAO aacCidConsultasDAO;
	
	@Inject
	private AacRetornosDAO aacRetornosDAO;
	
	@Inject
	private AacFormaEspecialidadeDAO aacFormaEspecialidadeDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AacConsultasJnDAO aacConsultasJnDAO;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AacNivelBuscaDAO aacNivelBuscaDAO;
	
	@Inject
	private AacSituacaoConsultasDAO aacSituacaoConsultasDAO;
	
	@Inject
	private AacCaracteristicaGradeDAO aacCaracteristicaGradeDAO;
	
	@Inject
	private AacFormaAgendamentoDAO aacFormaAgendamentoDAO;
	
	@EJB
	private IExamesBeanFacade examesBeanFacade;
	
	@Inject
	private AacProcedHospEspecialidadesDAO aacProcedHospEspecialidadesDAO;
	
	@Inject
	private AacConsultaProcedHospitalarDAO aacConsultaProcedHospitalarDAO;
	
	@Inject
	private AacHorarioGradeConsultaDAO aacHorarioGradeConsultaDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@Inject
	private AacRadioAcompTomosDAO aacRadioAcompTomosDAO;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;
	
	@Inject
	private AelProjetoPesquisasDAO aelProjetoPesquisasDAO;
	
	@Inject
	private MamInterconsultasDAO mamInterconsultasDAO;
	
	@EJB
	private LiberarConsultasON liberarConsultasON;
	
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;

	@Inject
	private FatConvenioSaudeDAO fatConvenioSaudeDAO;
	
	@Inject
	private FatContaApacDAO fatContaApacDAO;
	@Inject
	private RapUniServPlanoDAO rapUniServPlanoDAO;
	@Inject
	private RapUniServDependenteDAO rapUniServDependenteDAO;
	@Inject
	private Rhfp0283DAO rhfp0283DAO;
	@Inject
	private Rhfp0285DAO rhfp0285DAO;
	@Inject
	private AghCaractUnidFuncionaisDAO aghCaractUnidFuncionaisDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3248196158917020486L;

	private static final Date PRMEIRO_MAIO_2000 = DateMaker.obterData(2000,
			Calendar.MAY, 1);

	public enum AmbulatorioConsultaRNExceptionCode implements
			BusinessExceptionCode {
		AIP_00013
	}

	private Boolean vMamUpdEmergencia = false;
	private Boolean kAutConsRetAmb = false;

	private static final Byte VALOR_PADRAO_QUANTIDADE_CONSULTA = Byte
			.valueOf("1");

	public enum AmbulatorioConsultasRNExceptionCode implements
			BusinessExceptionCode {

		AAC_00650, AAC_00258, AAC_00168, AAC_00629, AAC_00197, AAC_00671, AAC_00124, AAC_00672, AAC_00702, AAC_00706, AAC_00647, AAC_00648, AAC_00037, AAC_00666, AAC_00667, AAC_00535, AAC_00681, AAC_00680, AAC_00101, AAC_00269, AAC_00265, AAC_00621, AAC_00006, AAC_00068, AAC_00268, AAC_00104, AAC_00066, AAC_00695, AAC_00696, AAC_00108, AAC_00289, AAC_00284, AAC_00285, AAC_00097, AAC_00645, AAC_00646, AAC_00096, AAC_00691, AAC_00102, AAC_00620, AAC_00622, AAC_00697, AAC_00698, AAC_00722, AAC_00636, AAC_00635, AAC_00639, AAC_00641, AAC_00653, AAC_00654, AAC_00658, AAC_00659, AAC_00271, AAC_00384, AAC_00611, AAC_ERRO_GRADE_FAMLIAR, AAC_00642, AAC_00643, AAC_00644, AAC_00670, MSG_EXCLUSAO_CONSULTA_SITUACAO_NAO_GERADA, AEL_02284, AAC_00728, MENSAGEM_ERRO_RETORNO, ERRO_OBTER_NOME_MICRO, ERRO_CONSULTA_NAO_ENCONTRADA, ERRO_DATA_CONSULTA_MENOR_COMPETENCIA, AAC_00125, AAC_00290, // Paciente
		AAC_00291, AAC_00301,AAC_00730,CONSULTA_JA_MARCADA_PACIENTE_MESMO_DIA;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwExceptionRollback(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	/**
	 * 
	 * @param consulta
	 * @throws NumberFormatException
	 * @throws BaseException
	 */
	@Deprecated
	public AacConsultas inserirConsulta(AacConsultas consulta,
			Boolean marcacaoConsulta, String nomeMicrocomputador,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws NumberFormatException,
			BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Short condicaoAtendimento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COND_AT_EMERG)
				.getVlrNumerico().shortValue();
		
		//se a consulta for de emergencia nao deve validar se a consulta eh do mesmo dia
		if(consulta.getGradeAgendamenConsulta().getCondicaoAtendimento()!=null) {
		if(!consulta.getGradeAgendamenConsulta().getCondicaoAtendimento().getSeq().equals(condicaoAtendimento)) {
		validarConsultaPacienteMesmoDia(consulta);
		}
		}
		else {
			validarConsultaPacienteMesmoDia(consulta);
		}
		
		consulta.setServidor(servidorLogado);
		consulta = this.preInserirConsulta(consulta);
		this.getAacConsultasDAO().persistir(consulta);
		this.getAacConsultasDAO().flush();
		this.validarConsultasEnforce(null, consulta, DominioOperacaoBanco.INS,
				marcacaoConsulta, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, true);
		return consulta;
	}
	
	/**
	 * 
	 * @param consulta
	 * @throws NumberFormatException
	 * @throws BaseException
	 */
	public AacConsultas inserirConsulta(AacConsultas consulta,
			Boolean marcacaoConsulta, String nomeMicrocomputador,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario,
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento) throws NumberFormatException, BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Short condicaoAtendimento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COND_AT_EMERG)
				.getVlrNumerico().shortValue();
		
		//se a consulta for de emergencia nao deve validar se a consulta eh do mesmo dia
		if(consulta.getGradeAgendamenConsulta().getCondicaoAtendimento()!=null) {
		if(!consulta.getGradeAgendamenConsulta().getCondicaoAtendimento().getSeq().equals(condicaoAtendimento)) {
		validarConsultaPacienteMesmoDia(consulta);
		}
		}
		else {
			validarConsultaPacienteMesmoDia(consulta);
		}
		
		consulta.setServidor(servidorLogado);
		consulta = this.preInserirConsulta(consulta);
		this.getAacConsultasDAO().persistir(consulta);
		this.getAacConsultasDAO().flush();
		this.validarConsultasEnforce(null, consulta, DominioOperacaoBanco.INS, marcacaoConsulta,
				nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, true,
				aack_prh_rn_v_apac_diaria, aack_aaa_rn_v_protese_auditiva,
				fatk_cap_rn_v_cap_encerramento);
		return consulta;
	}

	/**
	 * Verifica se o paciente já possui uma consulta marcada para o mesmo dia
	 */
	public void validarConsultaPacienteMesmoDia(AacConsultas consulta) throws ApplicationBusinessException{
		List<AacConsultas> listaConsultas = aacConsultasDAO.pesquisarConsultasDoPaciente(consulta.getPaciente());
		
		Calendar calendarDtInicial = Calendar.getInstance();
		calendarDtInicial.setTime(consulta.getDtConsulta());
		calendarDtInicial.set(Calendar.HOUR_OF_DAY, 0);
		calendarDtInicial.set(Calendar.MINUTE, 0);
		calendarDtInicial.set(Calendar.SECOND, 0);
		Date dataInicial = calendarDtInicial.getTime();
		
		Calendar calendarDtFinal = Calendar.getInstance();
		calendarDtFinal.setTime(consulta.getDtConsulta());
		calendarDtFinal.set(Calendar.HOUR_OF_DAY, 23);
		calendarDtFinal.set(Calendar.MINUTE, 59);
		calendarDtFinal.set(Calendar.SECOND, 59);
		Date dataFinal = calendarDtFinal.getTime();
		
		for (AacConsultas cons: listaConsultas){
			if (cons.getGradeAgendamenConsulta().getSeq().equals(consulta.getGradeAgendamenConsulta().getSeq())
					&& cons.getDtConsulta().after(dataInicial) && cons.getDtConsulta().before(dataFinal)
					&& !cons.getNumero().equals(consulta.getNumero()) ) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.CONSULTA_JA_MARCADA_PACIENTE_MESMO_DIA);
			}
		}
	}
	
	@Deprecated
	/**
	 * Deverá ser usada o metodo não deprecated passando true ou false nos ultimos parametros.
	 * @param consultaAnterior
	 * @param consulta
	 * @param marcacaoConsulta
	 * @param nomeMicrocomputador
	 * @param dataFimVinculoServidor
	 * @param substituirProntuario
	 * @return
	 * @throws NumberFormatException
	 * @throws BaseException
	 */
	public AacConsultas atualizarConsulta(AacConsultas consultaAnterior, AacConsultas consulta, Boolean marcacaoConsulta,
			String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean substituirProntuario, Boolean flush) throws NumberFormatException, BaseException {
		
		if (consultaAnterior == null) {
			consultaAnterior = ambulatorioFacade.obterConsultasMarcada(consulta.getNumero(), true);
		}
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if(marcacaoConsulta) {
			validarConsultaPacienteMesmoDia(consulta);
		}
		
		consulta.setServidorAlterado(servidorLogado);
		consulta = this.atualizarConsultaBeforeRowUpdate(consultaAnterior,consulta, nomeMicrocomputador, substituirProntuario, false);

		// marcacao consulta
		if(marcacaoConsulta){
			Integer grdSeq = null;
			if (consulta.getGradeAgendamenConsulta() != null) {
				grdSeq = consulta.getGradeAgendamenConsulta().getSeq();
			}
			this.verificarDataFeriado(consulta.getDtConsulta(), grdSeq);
		}
				
		// Deslocado para forçar o update na AAC_CONSULTAS e evitar erro da consulta sem atendimento (Dirty Check)
		consulta.setAlteradoEm(new Date());
		consulta = aacConsultasDAO.merge(consulta);
		aacConsultasDAO.flush();
		this.atualizarConsultaAfterRowUpdate(consultaAnterior, consulta, nomeMicrocomputador, flush);
		this.atualizarConsultaAfterUpdate(consultaAnterior, consulta, marcacaoConsulta, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, flush);
		return consulta;
	}

	public AacConsultas atualizarConsulta(AacConsultas consultaAnterior, AacConsultas consulta, Boolean marcacaoConsulta,
			String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean substituirProntuario, Boolean flush, 
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento) throws NumberFormatException, BaseException {
		
		if (consultaAnterior == null) {
			consultaAnterior = aacConsultasDAO.obterOriginal(consulta);
		}
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if(marcacaoConsulta) {
			validarConsultaPacienteMesmoDia(consulta);
		}
		
		consulta.setServidorAlterado(servidorLogado);
		consulta = this.atualizarConsultaBeforeRowUpdate(consultaAnterior,consulta, nomeMicrocomputador, substituirProntuario, aack_prh_rn_v_apac_diaria);
		
		// marcaço consulta
		if(marcacaoConsulta){
			Integer grdSeq = null;
			if (consulta.getGradeAgendamenConsulta() != null) {
				grdSeq = consulta.getGradeAgendamenConsulta().getSeq();
			}
			this.verificarDataFeriado(consulta.getDtConsulta(), grdSeq);
		}
		
		// Deslocado para forçar o update na AAC_CONSULTAS e evitar erro da consulta sem atendimento (Dirty Check)
		consulta.setAlteradoEm(new Date());
		consulta = aacConsultasDAO.merge(consulta);
		aacConsultasDAO.flush();
		this.atualizarConsultaAfterRowUpdate(consultaAnterior, consulta, nomeMicrocomputador,flush);
		this.atualizarConsultaAfterUpdate(consultaAnterior, consulta, marcacaoConsulta, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, flush,
				aack_prh_rn_v_apac_diaria,
				aack_aaa_rn_v_protese_auditiva, fatk_cap_rn_v_cap_encerramento);
		return consulta;
	}
	
	@Deprecated
	/**
	 * 
	 * Trigger ORADB:AACT_CON_ASU
	 * 
	 * @throws NumberFormatException
	 * @throws BaseException
	 */
	public void atualizarConsultaAfterUpdate(AacConsultas consultaAnterior,
			AacConsultas consulta, Boolean marcacaoConsulta,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final Boolean substituirProntuario, final Boolean flush)
			throws NumberFormatException, BaseException {
		this.validarConsultasEnforce(consultaAnterior, consulta,
				DominioOperacaoBanco.UPD, marcacaoConsulta, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, flush);
	}

	/**
	 * Trigger ORADB:AACT_CON_ASU
	 * 
	 * @throws NumberFormatException
	 * @throws BaseException
	 */
	public void atualizarConsultaAfterUpdate(AacConsultas consultaAnterior,
			AacConsultas consulta, Boolean marcacaoConsulta,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final Boolean substituirProntuario,  Boolean flush, 
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento)
			throws NumberFormatException, BaseException {
		this.validarConsultasEnforce(consultaAnterior, consulta,
				DominioOperacaoBanco.UPD, marcacaoConsulta, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, flush, aack_prh_rn_v_apac_diaria,
				aack_aaa_rn_v_protese_auditiva, fatk_cap_rn_v_cap_encerramento);
	}
	
	/**
	 * Trigger ORADB:AACT_CON_ARU
	 * 
	 * @param consultaAnterior
	 * @param consulta
	 * @throws NumberFormatException
	 * @throws BaseException 
	 */
	// TODO Alguns trechos de código estão comentados e não foram implementados
	// por não serem do escopo da estória, os mesmos devem ser implementados
	// quando necessários
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void atualizarConsultaAfterRowUpdate(AacConsultas consultaAnterior,
			AacConsultas consulta, String nomeMicrocomputador, Boolean flush) throws NumberFormatException,
			BaseException {

		// Regra ALTERAR_AACT_CON_ARU da estória #37942
		if(AGHUUtil.modificados(consulta.getAtendimento(), consultaAnterior.getAtendimento())) {
			getFaturamentoFacade().atualizarProcedimentosConsulta(consultaAnterior.getAtendimento().getSeq(), consulta.getAtendimento().getSeq(),
					consulta.getNumero(), nomeMicrocomputador);
		}
		

		this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_USER_URA);
		
		//#50223 - Obtem primeiramente a situação das consultas para posterior comparação
		String situacaoConsultaAtual = null;
		String situacaoConsultaAnterior = null;
		if (consulta.getSituacaoConsulta() != null){
			situacaoConsultaAtual = consulta.getSituacaoConsulta().getSituacao();
		}
		if (consultaAnterior.getSituacaoConsulta() != null){
			situacaoConsultaAnterior = consultaAnterior.getSituacaoConsulta().getSituacao();
		}
		
		if (CoreUtil.modificados(consultaAnterior.getDtConsulta(), consulta.getDtConsulta() ) || 
				CoreUtil.modificados(consulta.getGradeAgendamenConsulta(), consultaAnterior.getGradeAgendamenConsulta()) ||
				CoreUtil.modificados(consultaAnterior.getTpsTipo(),consulta.getTpsTipo()) || 
				CoreUtil.modificados(situacaoConsultaAtual,situacaoConsultaAnterior) ||
				CoreUtil.modificados(consulta.getPaciente(), consultaAnterior.getPaciente()) || 
				(consulta.getConvenioSaudePlano() != null && consultaAnterior.getConvenioSaudePlano() == null) || 
				(consulta.getConvenioSaudePlano() == null && consultaAnterior.getConvenioSaudePlano() != null) || 
				(consulta.getConvenioSaudePlano() != null && consultaAnterior.getConvenioSaudePlano() != null && 
					CoreUtil.modificados(consulta.getConvenioSaudePlano(), consultaAnterior.getConvenioSaudePlano())) ||
					
				(consulta.getServidorConsultado() != null && consultaAnterior.getServidorConsultado() == null) || 
				(consulta.getServidorConsultado() == null && consultaAnterior.getServidorConsultado() != null) || 
				(consulta.getServidorConsultado() != null && consultaAnterior.getServidorConsultado() != null && 
					CoreUtil.modificados(consulta.getServidorConsultado(), consultaAnterior.getServidorConsultado())) || 
				
				CoreUtil.modificados(consulta.getAtendimento(), consultaAnterior.getAtendimento()) || 
				CoreUtil.modificados(consulta.getJustificativa(), consultaAnterior.getJustificativa()) || 
				CoreUtil.modificados(consulta.getRetorno(),consultaAnterior.getRetorno()) || 
				CoreUtil.modificados(consulta.getMotivo(), consultaAnterior.getMotivo()) || 
				CoreUtil.modificados(consulta.getDthrInicio(), consultaAnterior.getDthrInicio()) || 
				CoreUtil.modificados(consulta.getDthrFim(), consultaAnterior.getDthrFim()) || 
				CoreUtil.modificados(consulta.getCondicaoAtendimento(), consultaAnterior.getCondicaoAtendimento()) || 
				CoreUtil.modificados(consulta.getTipoAgendamento(), consultaAnterior.getTipoAgendamento()) || 
				CoreUtil.modificados(consulta.getPagador(), consultaAnterior.getPagador()) || 
				CoreUtil.modificados(consulta.getConsulta(), consultaAnterior.getConsulta()) || 
				CoreUtil.modificados(consulta.getPostoSaude(), consultaAnterior.getPostoSaude()) || 
				CoreUtil.modificados(situacaoConsultaAtual,situacaoConsultaAnterior) ||
				((consulta.getServidorAtendido() != null && consultaAnterior.getServidorAtendido() == null) || 
						(consulta.getServidorAtendido() == null && consultaAnterior.getServidorAtendido() != null) || 
							(consulta.getServidorAtendido() != null && consulta.getServidorAtendido().getId() != null && 
								consultaAnterior.getServidorAtendido() != null && consultaAnterior.getServidorAtendido().getId() != null && 
									CoreUtil.modificados(consulta.getServidorAtendido().getId().getMatricula(), 
														 consultaAnterior.getServidorAtendido().getId().getMatricula()))
														 
				) || CoreUtil.modificados(consulta.getProjetoPesquisa(), consultaAnterior.getProjetoPesquisa())) {
			
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AacConsultasJn jn = BaseJournalFactory.getBaseJournal(
					DominioOperacoesJournal.UPD, AacConsultasJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
			jn.setNumero(consultaAnterior.getNumero());
			jn.setDtConsulta(consultaAnterior.getDtConsulta());
			jn.setCriadoEm(consultaAnterior.getCriadoEm());
			if (consultaAnterior.getServidor() != null
					&& consultaAnterior.getServidor().getId() != null) {
				jn.setSerMatricula(consultaAnterior.getServidor().getId()
						.getMatricula());
				jn.setSerVinCodigo(consultaAnterior.getServidor().getId()
						.getVinCodigo());
			}
			if (consultaAnterior.getGradeAgendamenConsulta() != null) {
				jn.setGrdSeq(consultaAnterior.getGradeAgendamenConsulta()
						.getSeq());
			}
			jn.setTpsTipo(consultaAnterior.getTpsTipo());
			if (consultaAnterior.getSituacaoConsulta() != null) {
				AacSituacaoConsultas situacaoConsulta = this.aacSituacaoConsultasDAO
						.obterPorChavePrimaria(consultaAnterior
								.getSituacaoConsulta().getSituacao());
				jn.setSituacaoConsulta(situacaoConsulta);
			}
			if (consultaAnterior.getPaciente() != null) {
				jn.setPacCodigo(consultaAnterior.getPaciente().getCodigo());
			}
			if (consultaAnterior.getConvenioSaudePlano() != null
					&& consultaAnterior.getConvenioSaudePlano().getId() != null) {
				jn.setCspCnvCodigo(consultaAnterior.getConvenioSaudePlano()
						.getId().getCnvCodigo());
				jn.setCspSeq(consultaAnterior.getConvenioSaudePlano().getId()
						.getSeq());
			}
			if (consultaAnterior.getServidorConsultado() != null
					&& consultaAnterior.getServidorConsultado().getId() != null) {
				jn.setSerMatriculaConsultado(consultaAnterior
						.getServidorConsultado().getId().getMatricula());
				jn.setSerVinCodigoConsultado(consultaAnterior
						.getServidorConsultado().getId().getVinCodigo());
			}
			if (consultaAnterior.getServidorAlterado() != null
					&& consultaAnterior.getServidorAlterado().getId() != null) {
				jn.setSerMatriculaAlterado(consultaAnterior
						.getServidorAlterado().getId().getMatricula());
				jn.setSerVinCodigoAlterado(consultaAnterior
						.getServidorAlterado().getId().getVinCodigo());
			}

			if (consultaAnterior.getServidorMarcacao() != null
					&& consultaAnterior.getServidorMarcacao().getId() != null) {
				jn.setSerMatriculaMarcacao(consultaAnterior
						.getServidorMarcacao().getId().getMatricula());
				jn.setSerVinCodigoMarcacao(consultaAnterior
						.getServidorMarcacao().getId().getVinCodigo());
			}

			jn.setAlteradoEm(consultaAnterior.getAlteradoEm());
			if (consultaAnterior.getAtendimento() != null) {
				jn.setAtdSeq(consultaAnterior.getAtendimento().getSeq());
			}
			jn.setJustificativa(consultaAnterior.getJustificativa());
			if (consultaAnterior.getRetorno() != null) {
				jn.setRetSeq(consultaAnterior.getRetorno().getSeq());
			}
			if (consultaAnterior.getMotivo() != null) {
				jn.setMtoSeq(consultaAnterior.getMotivo().getCodigo());
			}
			jn.setDthrInicio(consultaAnterior.getDthrInicio());
			jn.setDthrFim(consultaAnterior.getDthrFim());
			jn.setCodCentral(consultaAnterior.getCodCentral());
			if (consultaAnterior.getCondicaoAtendimento() != null) {
				jn.setFagCaaSeq(consultaAnterior.getCondicaoAtendimento()
						.getSeq());
			}
			if (consultaAnterior.getPagador() != null) {
				jn.setFagPgdSeq(consultaAnterior.getPagador().getSeq());
			}
			if (consultaAnterior.getTipoAgendamento() != null) {
				jn.setFagTagSeq(consultaAnterior.getTipoAgendamento().getSeq());
			}
			if (consultaAnterior.getConsulta() != null) {
				jn.setConNumero(consultaAnterior.getConsulta().getNumero());
			}
			if (consultaAnterior.getSituacaoConsulta() != null) {
				jn.setStcSituacao(consultaAnterior.getSituacaoConsulta()
						.getSituacao());
			}
			jn.setPostoSaude(consultaAnterior.getPostoSaude());
			if (consultaAnterior.getServidorAtendido() != null
					&& consultaAnterior.getServidorAtendido().getId() != null) {
				jn.setSerMatriculaAtendido(consultaAnterior
						.getServidorAtendido().getId().getMatricula());
				jn.setSerVinCodigoAtendido(consultaAnterior
						.getServidorAtendido().getId().getVinCodigo());
			}
			jn.setGrupoAtendAmb(consultaAnterior.getGrupoAtendAmb());
			if (consultaAnterior.getProjetoPesquisa() != null) {
				jn.setPjqSeq(consultaAnterior.getProjetoPesquisa().getSeq());
			}

			this.getAacConsultasJnDAO().persistir(jn);
			if (flush){
				this.getAacConsultasJnDAO().flush();
			}
		}

		if ((consulta.getConvenioSaudePlano() != null && consultaAnterior
				.getConvenioSaudePlano() == null)
				|| (consulta.getConvenioSaudePlano() != null
						&& consultaAnterior.getConvenioSaudePlano() != null && CoreUtil
							.modificados(consulta.getConvenioSaudePlano()
									.getId(), consultaAnterior
									.getConvenioSaudePlano().getId()))) {
			// fatk_pmr_rn.fatp_pmr_troca_conv(:new.numero,:new.csp_cnv_codigo,:new.csp_seq);
			this.verificarTrocaConvenio(consulta.getNumero(), consulta
					.getConvenioSaudePlano().getId().getCnvCodigo(), consulta
					.getConvenioSaudePlano().getId().getSeq());
		}
		if (CoreUtil.modificados(consulta.getGradeAgendamenConsulta(),
				consultaAnterior.getGradeAgendamenConsulta())) {
			this.realizarTrocaGrade( consulta.getNumero(),consulta.getGradeAgendamenConsulta().getSeq(),nomeMicrocomputador,new Date());
			this.atualizarAtendimentoGrade(consulta.getNumero(), consulta
					.getGradeAgendamenConsulta().getSeq(), nomeMicrocomputador);
		}
		//novo
		
		/**
		 * ORADB: Trigger AACT_CON_ARU
		 * @autor lucas.carvalho
		 */
		if(consulta.getIndSituacaoConsulta().getSituacao().equals("M") && consultaAnterior.getIndSituacaoConsulta().getSituacao().equals("L")){
			if(getLiberarConsultasON().verificaCaracteristicaGrade(consulta.getGradeAgendamenConsulta().getSeq(),"Acompanhamento tomografias")
					&& consulta.getDtConsulta().getHours()==9&&consulta.getDtConsulta().getMinutes()==15){
				atualizarInclusaoRadioterapiaTomografia(consulta.getPaciente(),consulta,consulta.getDtConsulta(),consulta.getConvenioSaudePlano()
						.getId().getCnvCodigo(),consulta.getConvenioSaudePlano().getId().getSeq());
	}
				if(getLiberarConsultasON().verificaCaracteristicaGrade(consulta.getGradeAgendamenConsulta().getSeq(), "Atualizar acomp tomografias")){
					getAacRadioAcompTomosRN().atualizaAacRadioAcompTomos(consulta.getPaciente().getCodigo(),consulta.getDtConsulta(),consulta.getServidorAtendido()
							.getId().getVinCodigo(),consulta.getServidorAtendido().getId().getMatricula());
				}
			}
	}

	/**
	 * Trigger ORADB:AACT_CON_BRU
	 * 
	 * @param consultaAnterior
	 * @param consulta
	 * @throws BaseException 
	 * @throws NumberFormatException
	 */
	// TODO Alguns trechos de código estão comentados e não foram implementados
	// por não serem do escopo da estória, os mesmos devem ser implementados
	// quando necessários
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public AacConsultas atualizarConsultaBeforeRowUpdate(
			AacConsultas consultaAnterior, AacConsultas consulta, String nomeMicrocomputador, Boolean substituirProntuario, Boolean aack_prh_rn_v_apac_diaria)
			throws BaseException {

		Integer pagadorSus = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS)
				.getVlrNumerico().intValue();

		Short caaSeq = null;
		Short taaSeq = null;
		Short pgdSeq = null;

		if (consulta.getCondicaoAtendimento() != null) {
			caaSeq = consulta.getCondicaoAtendimento().getSeq();
		}
		if (consulta.getTipoAgendamento() != null) {
			taaSeq = consulta.getTipoAgendamento().getSeq();
		}
		if (consulta.getPagador() != null) {
			pgdSeq = consulta.getPagador().getSeq();
		}

		if (((consultaAnterior.getSituacaoConsulta() != null
				&& consultaAnterior.getSituacaoConsulta().getSituacao() != null && consultaAnterior
				.getSituacaoConsulta().getSituacao().equals("L")) || (consultaAnterior
				.getSituacaoConsulta() != null
				&& consultaAnterior.getSituacaoConsulta().getSituacao() != null && consultaAnterior
				.getSituacaoConsulta().getSituacao().equals("R")))
				&& (consulta.getSituacaoConsulta() != null
						&& consulta.getSituacaoConsulta().getSituacao() != null && consulta
						.getSituacaoConsulta().getSituacao().equals("M"))) {
			consulta.setSituacaoConsulta(getAacSituacaoConsultasDAO()
					.obterSituacaoConsultaPeloId("M"));
		}
		AacRetornos retorno = this.atualizarRetornoConsulta(
				consulta.getSituacaoConsulta(),
				consultaAnterior.getSituacaoConsulta());
		if (retorno != null) {
			consulta.setRetorno(retorno);
		}
		if (!substituirProntuario) {
			if ((consulta.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta() != null && CoreUtil
						.modificados(consulta.getSituacaoConsulta().getSituacao(),
							consultaAnterior.getSituacaoConsulta()
									.getSituacao()))
					|| (consulta.getSituacaoConsulta() != null && consultaAnterior
							.getSituacaoConsulta() == null)) {
				this.verificarSituacaoConsultaAtiva(consulta
						.getSituacaoConsulta());
				consulta.setIndSituacaoConsulta(consulta.getSituacaoConsulta());
			}
			this.verificarSituacaoNovo(consulta, consultaAnterior
					.getSituacaoConsulta().getSituacao());

			this.verificarFormaAgendamento(consulta.getSituacaoConsulta(),
					caaSeq, taaSeq, pgdSeq);

			if ((consulta.getPagador() != null
					&& consultaAnterior.getPagador() != null && CoreUtil
					.modificados(consulta.getPagador().getSeq(),
							consultaAnterior.getPagador().getSeq()))
					|| (consulta.getCondicaoAtendimento() != null
							&& consultaAnterior.getCondicaoAtendimento() != null && CoreUtil
							.modificados(consulta.getCondicaoAtendimento()
									.getSeq(), consultaAnterior
									.getCondicaoAtendimento().getSeq()))
					|| (consulta.getTipoAgendamento() != null
							&& consultaAnterior.getTipoAgendamento() != null && CoreUtil
							.modificados(
									consulta.getTipoAgendamento().getSeq(),
									consultaAnterior.getTipoAgendamento()
											.getSeq()))) {
				this.verificarAlteracaoFormaAgendamento(consulta
						.getGradeAgendamenConsulta().getSeq(), caaSeq, taaSeq,
						pgdSeq);
			}

			if (consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				verificarConvenioSaudeProjeto2(consulta);
			}

			if (consulta.getGradeAgendamenConsulta().getSeq() > 1) {
				if (consultaAnterior.getSituacaoConsulta() != null
						&& consultaAnterior.getSituacaoConsulta()
								.getSituacao() != null
						&& consultaAnterior.getSituacaoConsulta()
								.getSituacao().equals("M")
						&& consulta.getSituacaoConsulta() != null
						&& consulta.getSituacaoConsulta().getSituacao() != null
						&& consulta.getSituacaoConsulta().getSituacao()
								.equals("L")) {
					verificarConsultaFat(consulta.getNumero());
					verificarEstatisticas(consulta.getDtConsulta());

					// se não consistir na alteração de paciente - fazer
					// alterações
				} else if (consultaAnterior.getSituacaoConsulta() != null
						&& consultaAnterior.getSituacaoConsulta()
								.getSituacao() != null
						&& consultaAnterior.getSituacaoConsulta()
								.getSituacao().equals("M")
						&& consulta.getSituacaoConsulta() != null
						&& consulta.getSituacaoConsulta().getSituacao() != null
						&& consulta.getSituacaoConsulta().getSituacao()
								.equals("M")
						&& consulta.getConvenioSaudePlano() != null
						&& consultaAnterior.getConvenioSaudePlano() != null
						&& (CoreUtil.modificados(
								consulta.getConvenioSaudePlano(),
								consultaAnterior.getConvenioSaudePlano())
								|| CoreUtil.modificados(
										consulta.getAtendimento(),
										consultaAnterior.getAtendimento())
								|| CoreUtil.modificados(consulta
										.getGradeAgendamenConsulta().getSeq(),
										consultaAnterior
												.getGradeAgendamenConsulta()
												.getSeq()) || (consulta
								.getRetorno() != null
								&& consultaAnterior.getRetorno() != null && CoreUtil
								.modificados(
										consulta.getRetorno().getSeq(),
										consultaAnterior.getRetorno()
												.getSeq())))) {

					verificarConsultaFat(consulta.getNumero());
					verificarContextoSessaoConsulta(consulta, aack_prh_rn_v_apac_diaria);
				}
			}
		}

		if (((consultaAnterior.getSituacaoConsulta() != null
				&& consultaAnterior.getSituacaoConsulta().getSituacao() != null && consultaAnterior
				.getSituacaoConsulta().getSituacao().equalsIgnoreCase("L")) || (consultaAnterior
				.getSituacaoConsulta() != null
				&& consultaAnterior.getSituacaoConsulta().getSituacao() != null && (consultaAnterior
				.getSituacaoConsulta().getSituacao().equalsIgnoreCase("R") || consultaAnterior
				.getSituacaoConsulta().getSituacao().equalsIgnoreCase("U"))))
				&& (consulta.getSituacaoConsulta() != null
						&& consulta.getSituacaoConsulta().getSituacao() != null && consulta
						.getSituacaoConsulta().getSituacao().equalsIgnoreCase(
								"M"))) {

			this.verificarFormaAgendamentoEspecialidade(consulta
					.getFormaAgendamento(), consulta
					.getGradeAgendamenConsulta(), consulta.getDtConsulta());

			if (consulta.getPagador() != null
					&& consulta.getPagador().getSeq()
							.equals(pagadorSus.shortValue())) { // somente
																// converte
																// pagador SUS
				consulta = atualizarUnimed(consulta);
				/*
				 * v_fag_pgd_seq := :new.fag_pgd_seq; v_csp_cnv_codigo :=
				 * :new.csp_cnv_codigo; v_csp_seq := :new.csp_seq;
				 * AACK_CON_3_RN.RN_CONP_ATU_UNIMED(:new.GRD_SEQ
				 * ,:new.PAC_CODIGO ,:new.SER_MATRICULA_CONSULTADO
				 * ,:new.SER_VIN_CODIGO_CONSULTADO ,v_fag_pgd_seq
				 * ,v_csp_cnv_codigo ,v_csp_seq ,:new.dt_consulta
				 * ,:new.ind_funcionario); :new.fag_pgd_seq := v_fag_pgd_seq;
				 * :new.csp_cnv_codigo := v_csp_cnv_codigo; :new.csp_seq :=
				 * v_csp_seq;
				 */
			}

			verificarEstatisticas(consulta.getDtConsulta());
			verificarObitoPaciente(consulta);

			if (!substituirProntuario) {
				verificarIdadePaciente(consulta);
			}

			verificarSenha(consulta);// ver se deve ter código da central

			if (consulta.getServidorConsultado() != null) {
				verificarVinculoFuncionario(consulta.getServidorConsultado(),
						consulta.getGradeAgendamenConsulta());
			}

			if (consulta.getServidorAtendido() != null) { // popular
															// profissional da
															// grade - se houver
															// - na consulta
				RapServidores servidorAtendido = atualizarProfissionalGrade(consulta
						.getGradeAgendamenConsulta());
				consulta.setServidorAtendido(servidorAtendido);
			}

			AghParametros pInterconsulta = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_CAA_INTERCONSULTA);
			if (consulta.getCondicaoAtendimento() != null
					&& consulta
							.getCondicaoAtendimento()
							.getSeq()
							.equals(pInterconsulta.getVlrNumerico()
									.shortValue())
					&& consulta.getConvenioSaudePlano() != null
					&& consulta.getConvenioSaudePlano().getId().getCnvCodigo()
							.equals(pagadorSus.shortValue())) {

				Long seqMam = verificarPacienteListaEspera(
						consulta.getGradeAgendamenConsulta(), consulta
								.getPaciente().getCodigo());

				if (seqMam.equals(Long.valueOf("0"))) {
					seqMam = verificarPacienteOutraListaEspera(
							consulta.getGradeAgendamenConsulta(), consulta
									.getPaciente().getCodigo());
					if (seqMam > (Long.valueOf("0"))) {
						// Pega o parametro AACG_SEM_REGRA para comparar ao perfil do usuário
						String semRegra = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AACG_SEM_REGRA).getVlrTexto();
						if (!this.verificarPerfil(semRegra)) {
							// Paciente não está em lista e existem pacientes em lista e usuário não tem perfil
							AmbulatorioConsultasRNExceptionCode.AAC_00702.throwException();
						}
					}
				}
			}
		}
		if ((consulta.getConvenioSaudePlano() == null && consultaAnterior
				.getConvenioSaudePlano() != null)
				|| (consulta.getConvenioSaudePlano() != null && consultaAnterior
						.getConvenioSaudePlano() == null)
				|| (consulta.getConvenioSaudePlano() != null && consultaAnterior
						.getConvenioSaudePlano() != null)
				&& CoreUtil.modificados(consulta.getConvenioSaudePlano()
						.getId(), consultaAnterior.getConvenioSaudePlano()
						.getId().getSeq())) {
			this.verificarConsultaConvenioSituacao(consulta
					.getConvenioSaudePlano());
		}
		if (CoreUtil.modificados(consulta.getRetSeq(),
				consultaAnterior.getRetSeq())
				&& consultaAnterior.getAtendimento() != null) {
			this.atualizarAtendimento(consulta.getRetorno(), consultaAnterior
					.getAtendimento().getSeq());
		}
		if (CoreUtil.modificados(consulta.getAtendimento(),
				consultaAnterior.getAtendimento())) {
			this.verificarConsultaAtendimento(consulta.getAtendimento(),
					consulta.getPaciente().getCodigo());
		}
		if (!consulta.getGradeAgendamenConsulta().getSeq().equals(consultaAnterior.getGradeAgendamenConsulta().getSeq())) {
			
			this.getManterGradeAgendamentoRN().verificaSituacaoGradeData(consulta.getGradeAgendamenConsulta(), consulta.getDtConsulta());
			
			if (!this.vMamUpdEmergencia) {
				this.verificarProjetoGrade( consulta.getGradeAgendamenConsulta(), 
											consulta.getPagador().getSeq(), 
											consulta.getTipoAgendamento().getSeq());
			}
		}

		if (!substituirProntuario) {
			if (CoreUtil.modificados(consulta.getPaciente(), consultaAnterior.getPaciente()) && 
					consulta.getPaciente() != null && 
					consultaAnterior.getPaciente() != null) {
				this.verificarObitoPaciente(consulta);
				this.verificarIdadePaciente(consulta);
			}
		}
		
		this.getAmbulatorioRN().atualizaServidor(consulta.getServidor());
		
		if (consulta.getSituacaoConsulta() != null) {
			this.verificarConsultaRetorno(consulta.getSituacaoConsulta().getSituacao(), consulta.getRetorno());
		} else {
			this.verificarConsultaRetorno(null, consulta.getRetorno());
		}

		if (consultaAnterior.getDthrInicio() != null) {
			if (CoreUtil.modificados(consulta.getDthrInicio(),consultaAnterior.getDthrInicio())) {
				this.verificarDataAtendimento(consulta,consultaAnterior.getDthrInicio());
			}
		}
		if (consultaAnterior.getDthrFim() != null) {
			if (CoreUtil.modificados(consulta.getDthrFim(),consultaAnterior.getDthrFim())) {
				this.verificarDataAtendimento(consulta, consultaAnterior.getDthrInicio());
			}
		}
		if (CoreUtil.modificados(consulta.getDthrInicio(), consultaAnterior.getDthrInicio()) || 
				CoreUtil.modificados(consulta.getDthrInicio(), consultaAnterior.getDthrFim())) {
			this.verificarDiferencaDatas(consulta);
		}
		if (CoreUtil.modificados(consulta.getDthrInicio(), consultaAnterior.getDthrInicio())) {
			consulta.setNomeMicro(this.obterMicro(nomeMicrocomputador));
		}

		if ((consulta.getServidorAtendido() != null && consultaAnterior
				.getServidorAtendido() == null)
				|| (consulta.getServidorAtendido() == null && consultaAnterior
						.getServidorAtendido() != null)
				|| (consulta.getServidorAtendido() != null
						&& consultaAnterior.getServidorAtendido() != null && CoreUtil
							.modificados(consulta.getServidorAtendido().getId()
									.getMatricula(), consultaAnterior
									.getServidorAtendido().getId()
									.getMatricula()))
				|| (consulta.getServidorAtendido() != null
						&& consultaAnterior.getServidorAtendido() != null && CoreUtil
							.modificados(consulta.getServidorAtendido().getId()
									.getVinCodigo(), consultaAnterior
									.getServidorAtendido().getId()
									.getVinCodigo()))) {
			this.verificarProfissionalAtendidoPor(consulta
					.getServidorAtendido(), consulta
					.getGradeAgendamenConsulta().getSeq());

		}

		if (((consultaAnterior.getSituacaoConsulta() != null
				&& consultaAnterior.getSituacaoConsulta().getSituacao() != null && consultaAnterior
				.getSituacaoConsulta().getSituacao().equalsIgnoreCase("L")) || (consultaAnterior
				.getSituacaoConsulta() != null
				&& consultaAnterior.getSituacaoConsulta().getSituacao() != null && consultaAnterior
				.getSituacaoConsulta().getSituacao().equalsIgnoreCase("R")))
				&& (consulta.getSituacaoConsulta() != null
						&& consulta.getSituacaoConsulta().getSituacao() != null && consulta
						.getSituacaoConsulta().getSituacao()
						.equalsIgnoreCase("M"))) {
			if (consulta.getGradeAgendamenConsulta() != null
					&& consulta.getMotivo() != null) {
				// retorno não é tratado pois já esta populado no objeto e
				// apenas é verificado se ocorre erro na busca do parametro
				this.obterMotivo(consulta.getGradeAgendamenConsulta().getSeq(),
						consulta.getMotivo().getCodigo());
			}
			consulta.setDthrMarcacao(new Date());
		}

		this.atualizarProjetoPesquisa(consulta);

		// Se pagador Não for Pesquisa, o pjq_seq deve ser null
		if (CoreUtil.modificados(consulta.getPagador(),
				consultaAnterior.getPagador())
				|| CoreUtil.modificados(consulta.getProjetoPesquisa(),
						consultaAnterior.getProjetoPesquisa())) {
			verificarProjeto(consulta.getProjetoPesquisa(),
					consulta.getPagador());
		}

		// Se existir convênio no projeto(ael_projeto_pesquisas), o convênio da
		// consulta deve ser o mesmmo do projeto.
		if (CoreUtil.modificados(consulta.getFormaAgendamento(),
				consultaAnterior.getFormaAgendamento())
				&& consulta.getProjetoPesquisa() != null) {
			verificarConvenioSaudeProjeto(consulta.getProjetoPesquisa(),
					consulta.getConvenioSaudePlano());
		}
		return consulta;
	}

	private void verificarContextoSessaoConsulta(AacConsultas consulta, Boolean AACK_PRH_RN_V_APAC_DIARIA)
			throws ApplicationBusinessException {
		if (Boolean.FALSE
				.equals(AACK_PRH_RN_V_APAC_DIARIA)) {
			this.verificarEstatisticas(consulta.getDtConsulta());
		}
	}

	// Nunca verificar consulta anterior se consulta for marcada como primeira
	// consulta.
	private Boolean isPrimeiraConsulta(AacConsultas consulta)
			throws ApplicationBusinessException {
		Short codigoPrimeiraConsulta = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_CAA_CONSULTA)
				.getVlrNumerico().shortValue();
		return codigoPrimeiraConsulta.equals(consulta.getCondicaoAtendimento()
				.getSeq());
	}

	/**
	 * ORADB: AACP_ENFORCE_CON_RULES
	 * 
	 * @param consultaAnterior
	 * @param consulta
	 * @param operacao
	 * @throws NumberFormatException
	 * @throws BaseException
	 */
	// TODO Rodrigo Alguns trechos de código estão comentados e não foram implementados
	// por não serem do escopo da estória, os mesmos devem ser implementados
	// quando necessários
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	@Deprecated
	public void validarConsultasEnforce(AacConsultas consultaAnterior,
			AacConsultas consulta, DominioOperacaoBanco operacao,
			Boolean marcacaoConsulta, String nomeMicrocomputador,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario, final Boolean flush) throws BaseException {
		if (operacao.equals(DominioOperacaoBanco.INS)) {
			Integer pacCodigo = null;
			if (consulta.getPaciente() != null) {
				pacCodigo = consulta.getPaciente().getCodigo();
			}

			Integer grdSeq = null;
			if (consulta.getGradeAgendamenConsulta() != null) {
				grdSeq = consulta.getGradeAgendamenConsulta().getSeq();
			}

			Boolean bloqueioAlta = null;

			verificarDataMinCompetencia(consulta.getNumero());

			if (consulta.getGradeAgendamenConsulta() != null
					&& consulta.getGradeAgendamenConsulta().getSeq() > 1
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M") && !substituirProntuario) {

				atualizarProntuarioVirtual(consulta.getNumero(), consulta
						.getPaciente().getCodigo(), nomeMicrocomputador);
			}

			this.verificarDataFuturo(consulta.getDtConsulta());
			if (consulta.getPaciente() != null) {
				this.atualizarConsultaAtendimento(consulta, nomeMicrocomputador);
			}

			if (consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				// segundo objeto esta sendo passado nulo por nao existir
				// consulta anterior
				if (!marcacaoConsulta) {
					atualizarConsultaProcedimento(consulta, null, nomeMicrocomputador, dataFimVinculoServidor);
					atualizarConsultaProcGrade(consulta, nomeMicrocomputador, dataFimVinculoServidor);
				}
				Integer pagadorSus = getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS)
						.getVlrNumerico().intValue();

				if (consulta.getConvenioSaudePlano() != null
						&& Integer.valueOf(consulta.getConvenioSaudePlano().getId()
								.getCnvCodigo()).equals(pagadorSus)) {
					// atualiza a lista como consulta marcada, se houver;
					atualizarAmbulatorioInterconsultas(consulta.getNumero());
				}

				// se inserindo consulta marcada verificar se condição marcada
				// existe como programada na grade
				verificarFormaConsultaExcedente(consulta);

				// se inserindo consulta marcada do tipo RETORNO
				// verifica se possui ala e se a marcação para a agenda esta
				// liberada
				AghParametros bloqueioAltaParametro = this.getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_BLOQUEIO_ALTA);
				if (bloqueioAltaParametro.getVlrTexto() == null
						|| bloqueioAltaParametro.getVlrTexto().equals("N")) {
					bloqueioAlta = false;
				} else {
					bloqueioAlta = true;
				}

				if (bloqueioAlta) {

					AghParametros reconsultaParametro = this
							.getParametroFacade().buscarAghParametro(
									AghuParametrosEnum.P_CAA_RECONSULTA);

					Integer reconsulta = reconsultaParametro.getVlrNumerico()
							.intValue();

					// se é uma reconsulta
					if (consulta.getFormaAgendamento() != null
							&& consulta.getFormaAgendamento().getId()
									.getCaaSeq().equals(reconsulta)) {
						// busca última consulta da agenda e verifica se possui
						// uma alta informada
						// Se possui verifica o tipo de retorno agenda -> Se
						// B(Bloqueado) - não deixa marcar
						consulta.getGradeAgendamenConsulta().getEspecialidade();
					}
				}
			}
			atualizarCadastroPaciente(consultaAnterior, consulta, nomeMicrocomputador, dataFimVinculoServidor);

			// Controle de saldo no projeto
			if (consulta.getProjetoPesquisa() != null
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				try {
					verificarSaldoProjeto(consulta.getProjetoPesquisa()
							.getSeq());
				} catch (NotImplementedException e) {
					logError(EXCECAO_IGNORADA, e);
				}
			}
			if (consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				if (DateUtil.truncaData(consulta.getDtConsulta()).equals(
						DateUtil.truncaData(new Date()))) {
					this.getMarcacaoConsultaRN().atualizarPacienteAguardando(
							consulta.getNumero(), pacCodigo, grdSeq, nomeMicrocomputador);
				}
			}

		} else if (operacao.equals(DominioOperacaoBanco.UPD)) {
			Integer pagadorSus = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS)
					.getVlrNumerico().intValue();
			Short convenioSusPadrao = getParametroFacade()
					.buscarAghParametro(
							AghuParametrosEnum.P_CONVENIO_SUS_PADRAO)
					.getVlrNumerico().shortValue();
			Integer pacCodigo = null;
			if (consulta.getPaciente() != null) {
				pacCodigo = consulta.getPaciente().getCodigo();
			}

			Integer grdSeq = null;
			if (consulta.getGradeAgendamenConsulta() != null) {
				grdSeq = consulta.getGradeAgendamenConsulta().getSeq();
			}

			if (CoreUtil.modificados(consulta, consultaAnterior)) {
				this.getFaturamentoFacade()
						.atualizarFaturamentoProcedimentoConsulta(
								consulta.getNumero(), null, null,
								consulta.getRetorno(), nomeMicrocomputador, dataFimVinculoServidor);
			}

			if (consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				atualizarProntuarioVirtual(consulta.getNumero(), consulta
						.getPaciente().getCodigo(), nomeMicrocomputador);
				// Para não barrar a marcaçãoo quando rotina da SMS - primeiras
				// consultas
				if (consulta.getCodCentral() == null && !substituirProntuario) {
					Short condicaoAtendimentoSeq = null;
					if (consulta.getCondicaoAtendimento() != null) {
						condicaoAtendimentoSeq = consulta
								.getCondicaoAtendimento().getSeq();
					}
					verificarConsultaPacienteMesmaGrade(consulta
							.getGradeAgendamenConsulta().getSeq(), pacCodigo,
							consulta.getDtConsulta(), condicaoAtendimentoSeq);
				}
			}

			// verifica grade/paciente
			if (consultaAnterior.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao() != null
					&& !consultaAnterior.getSituacaoConsulta().getSituacao()
							.equals("M")
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {

				// Não permitir marcação de consultas funcionários antes das
				// 07:00 da manhã
				if (verificarConsultasFuncSmo(consulta.getNumero())
						&& Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 7) {
					AmbulatorioConsultasRNExceptionCode.AAC_00706
							.throwExceptionRollback();
				}
				// Ver regras para marcação da consulta
				Integer nmrConsultaAnterior = null;
				if(!isPrimeiraConsulta(consulta)){
					nmrConsultaAnterior = verificarConsultaAnterior(consulta);
				}

				// atualiza o número da consulta pesquisada na regra anterior
				if (nmrConsultaAnterior != null) {
					consulta = atualizarConsultaNumero(consulta.getNumero(),
							nmrConsultaAnterior);
				}

				if (consulta.getCodCentral() == null) {
					Long codCentral = obterCodigoCentral(pacCodigo,
							consulta.getNumero(), consulta.getDtConsulta());
					// Busca código da Central na primeira consulta da mesma
					// especialidade ou genérica
					if (codCentral != null) {
						consulta = atualizarCodigoCentral(codCentral, consulta,
								nmrConsultaAnterior);
					}
				}
				if (consulta.getPaciente() != null) {
					this.atualizarConsultaAtendimento(consulta, nomeMicrocomputador);
				}
				if (!marcacaoConsulta) {
					atualizarConsultaProcedimento(consulta,	consulta.getSituacaoConsulta(), nomeMicrocomputador, dataFimVinculoServidor);
					atualizarConsultaProcGrade(consulta, nomeMicrocomputador, dataFimVinculoServidor);
				}

				if (consulta.getConvenioSaudePlano() != null
						&& Integer.valueOf(consulta.getConvenioSaudePlano().getId()
								.getCnvCodigo()).equals(pagadorSus)) {
					atualizarAmbulatorioInterconsultas(consulta.getNumero());
				}
			}

			atualizarCadastroPaciente(consultaAnterior, consulta, nomeMicrocomputador, dataFimVinculoServidor);
			// Atualizações a partir de alteração em consulta marcada

			if (consultaAnterior.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao()
							.equals("M")
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				if (consulta.getPaciente() != null
						&& consultaAnterior.getPaciente() != null
						&& CoreUtil.modificados(consulta.getPaciente()
								.getCodigo(), consultaAnterior.getPaciente()
								.getCodigo())) {
					atualizarProcedAmbRealizados(consulta.getPaciente()
							.getCodigo(), consulta.getNumero(), nomeMicrocomputador, dataFimVinculoServidor);
					atualizarAtendimentoPaciente(consulta.getNumero(), consulta
							.getPaciente().getCodigo(), nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);

					// Uma consulta não pode validar outra se os pacientes forem
					// diferentes
					// esta atualiza a consulta anterior desta e das que ela
					// validou
					if (consulta.getPaciente() != null) {
						atualizarAutoRelacionamento(consulta.getNumero(),
								consulta.getPaciente().getCodigo(),
								nomeMicrocomputador,
								dataFimVinculoServidor, substituirProntuario);
					}

					// Atualiza as tabelas do sistema ambulatório onde migra a
					// fk de pacientes
					this.getMarcacaoConsultaRN().atualizarPacienteAmbulatorio(
							consulta.getNumero(), pacCodigo, nomeMicrocomputador);

				}

				if (consulta.getConvenioSaudePlano() != null
						&& CoreUtil.modificados(consulta
								.getConvenioSaudePlano().getId(),
								consultaAnterior.getConvenioSaudePlano()
										.getId())) {
					// atualiza solicitação exames na troca do convênio
					atualizarSolicitacaoExame(consulta, nomeMicrocomputador);
				}
				if (consulta.getConvenioSaudePlano() != null
						&& CoreUtil.modificados(
								consulta.getConvenioSaudePlano().getId()
										.getCnvCodigo(), consultaAnterior
										.getConvenioSaudePlano().getId()
										.getCnvCodigo())
						&& consulta.getConvenioSaudePlano().getId()
								.getCnvCodigo().equals(convenioSusPadrao)
						&& !consultaAnterior.getConvenioSaudePlano().getId()
								.getCnvCodigo().equals(convenioSusPadrao)) {

					atualizarInclusaoProcedHospitalar(consulta,
							consultaAnterior.getSituacaoConsulta(), nomeMicrocomputador, dataFimVinculoServidor);
				}

				if (CoreUtil.modificados(consulta.getGradeAgendamenConsulta()
						.getSeq(), consultaAnterior.getGradeAgendamenConsulta()
						.getSeq())) {
					atualizarProcedimentoHospitalar(consulta, consulta
							.getGradeAgendamenConsulta().getSeq(),
							consultaAnterior.getGradeAgendamenConsulta()
									.getSeq(), nomeMicrocomputador, dataFimVinculoServidor);
				}
			}

			// Quando passa consulta de M para L excluir aac_cid_consultas
			if (consultaAnterior.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& CoreUtil.modificados(consulta.getSituacaoConsulta()
							.getSituacao(), consultaAnterior
							.getSituacaoConsulta().getSituacao())
					&& consultaAnterior.getSituacaoConsulta().getSituacao()
							.equals("M")
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("L")) {
				// deletando os movimentos de prontuario situação Q
				this.atualizarExclusaoCidConsulta(consulta.getNumero());
				if (consultaAnterior.getPaciente() != null) {
					excluirMovimentacaoProntuario(consultaAnterior);
				}

			}

			if (CoreUtil.modificados(consulta, consultaAnterior)
					&& consultaAnterior.getDthrFim() == null
					&& consulta.getDthrFim() != null) {
				List<AghAtendimentos> listaAtendimentos = this
						.getPacienteFacade()
						.listarAtendimentosPorConsultaEOrigem(
								DominioOrigemAtendimento.A,
								consulta.getNumero());
				for (AghAtendimentos atendimento : listaAtendimentos) {
					AghAtendimentos atendimentoAnterior = this
							.getPacienteFacade().clonarAtendimento(atendimento);
					atendimento.setLeito(null);
					this.getPacienteFacade().persistirAtendimento(atendimento,
							atendimentoAnterior, nomeMicrocomputador, dataFimVinculoServidor);
				}

			}

			// Controle de saldo no projeto
			if (consulta.getProjetoPesquisa() != null
					&& consultaAnterior.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")
					&& !consultaAnterior.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				try {
					verificarSaldoProjeto(consulta.getProjetoPesquisa()
							.getSeq());
				} catch (NotImplementedException e) {
					logError(EXCECAO_IGNORADA, e);
				}
			}

			// Se a consulta está sendo MARCADA no dia corrente grava um
			// registro no ambulatório para indicar que o paciente já está
			// aguardando
			Calendar cal = Calendar.getInstance();
			cal.setTime(consulta.getDtConsulta());

			if (consultaAnterior.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& CoreUtil.modificados(consultaAnterior
							.getSituacaoConsulta().getSituacao(), consulta
							.getSituacaoConsulta().getSituacao())
					&& consultaAnterior.getSituacaoConsulta().getSituacao()
							.equals("L")
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")
					&& DateUtils
							.truncate(Calendar.getInstance(), Calendar.DATE)
							.equals(DateUtils.truncate(cal, Calendar.DATE))) {
				this.getMarcacaoConsultaRN().atualizarPacienteAguardando(
						consulta.getNumero(), pacCodigo, grdSeq, nomeMicrocomputador);
			}

			 if (consultaAnterior.getIndSituacaoConsulta() != null
			 && consultaAnterior.getIndSituacaoConsulta().getSituacao() !=
			 null
			 && consulta.getIndSituacaoConsulta() != null
			 && consulta.getIndSituacaoConsulta().getSituacao() != null
			 && consultaAnterior.getIndSituacaoConsulta().getSituacao()
			 .equals("M")
   		     && consulta.getIndSituacaoConsulta().getSituacao()
			 .equals("M")
			 && CoreUtil.modificados(consulta.getConvenioSaudePlano()
			 .getId().getCnvCodigo(), consultaAnterior
			 .getConvenioSaudePlano().getId().getCnvCodigo())) {
			  //#42229 Incluido
		      atualizaLicitacaoSMO(consulta,consultaAnterior);
			 }

		} else if (operacao.equals(DominioOperacaoBanco.DEL)) {
			// Caso em que a consulta tem situacao Marcada e excede programacao
			atualizarCadastroPaciente(null, consultaAnterior, nomeMicrocomputador, dataFimVinculoServidor);
		}
		if(flush){
			this.flush();
		}
	}
	
	/**
	 * ORADB: AACP_ENFORCE_CON_RULES
	 * 
	 * @param consultaAnterior
	 * @param consulta
	 * @param operacao
	 * @throws NumberFormatException
	 * @throws BaseException
	 */
	// TODO Alguns trechos de código estão comentados e não foram implementados
	// por não serem do escopo da estória, os mesmos devem ser implementados
	// quando necessários
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void validarConsultasEnforce(AacConsultas consultaAnterior,
			AacConsultas consulta, DominioOperacaoBanco operacao,
			Boolean marcacaoConsulta, String nomeMicrocomputador,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario, final Boolean flush,
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento) throws BaseException {
		if (operacao.equals(DominioOperacaoBanco.INS)) {
			Integer pacCodigo = null;
			if (consulta.getPaciente() != null) {
				pacCodigo = consulta.getPaciente().getCodigo();
			}

			Integer grdSeq = null;
			if (consulta.getGradeAgendamenConsulta() != null) {
				grdSeq = consulta.getGradeAgendamenConsulta().getSeq();
			}

			Boolean bloqueioAlta = null;

			verificarDataMinCompetencia(consulta.getNumero());

			if (consulta.getGradeAgendamenConsulta() != null
					&& consulta.getGradeAgendamenConsulta().getSeq() > 1
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M") && !substituirProntuario) {

				atualizarProntuarioVirtual(consulta.getNumero(), consulta
						.getPaciente().getCodigo(), nomeMicrocomputador);
			}

			this.verificarDataFuturo(consulta.getDtConsulta());
			if (consulta.getPaciente() != null) {
				this.atualizarConsultaAtendimento(consulta, nomeMicrocomputador);
			}

			if (consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				// segundo objeto esta sendo passado nulo por nao existir
				// consulta anterior
				if (!marcacaoConsulta) {
					atualizarConsultaProcedimento(consulta, null, nomeMicrocomputador, dataFimVinculoServidor,
							aack_prh_rn_v_apac_diaria, aack_aaa_rn_v_protese_auditiva, fatk_cap_rn_v_cap_encerramento);
					atualizarConsultaProcGrade(consulta, nomeMicrocomputador, dataFimVinculoServidor,
							aack_prh_rn_v_apac_diaria, aack_aaa_rn_v_protese_auditiva,
							fatk_cap_rn_v_cap_encerramento);
				}

				if (consulta.getConvenioSaudePlano().getId().getCnvCodigo()
						.equals(1)) {
					// atualiza a lista como consulta marcada, se houver;
					atualizarAmbulatorioInterconsultas(consulta.getNumero());
				}

				// se inserindo consulta marcada verificar se condição marcada
				// existe como programada na grade
				verificarFormaConsultaExcedente(consulta);

				// se inserindo consulta marcada do tipo RETORNO
				// verifica se possui ala e se a marcação para a agenda esta
				// liberada
				AghParametros bloqueioAltaParametro = this.getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_BLOQUEIO_ALTA);
				if (bloqueioAltaParametro.getVlrTexto() == null
						|| bloqueioAltaParametro.getVlrTexto().equals("N")) {
					bloqueioAlta = false;
				} else {
					bloqueioAlta = true;
				}

				if (bloqueioAlta) {

					AghParametros reconsultaParametro = this
							.getParametroFacade().buscarAghParametro(
									AghuParametrosEnum.P_CAA_RECONSULTA);

					Integer reconsulta = reconsultaParametro.getVlrNumerico()
							.intValue();

					// se é uma reconsulta
					if (consulta.getFormaAgendamento() != null
							&& consulta.getFormaAgendamento().getId()
									.getCaaSeq().equals(reconsulta)) {
						// busca última consulta da agenda e verifica se possui
						// uma alta informada
						// Se possui verifica o tipo de retorno agenda -> Se
						// B(Bloqueado) - não deixa marcar
						consulta.getGradeAgendamenConsulta().getEspecialidade();
					}
				}
			}
			atualizarCadastroPaciente(consultaAnterior, consulta, nomeMicrocomputador, dataFimVinculoServidor);

			// Controle de saldo no projeto
			if (consulta.getProjetoPesquisa() != null
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				try {
					verificarSaldoProjeto(consulta.getProjetoPesquisa()
							.getSeq());
				} catch (NotImplementedException e) {
					logError(EXCECAO_IGNORADA, e);
				}
			}
			if (consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				if (DateUtil.truncaData(consulta.getDtConsulta()).equals(
						DateUtil.truncaData(new Date()))) {
					this.getMarcacaoConsultaRN().atualizarPacienteAguardando(
							consulta.getNumero(), pacCodigo, grdSeq, nomeMicrocomputador);
				}
			}

		} else if (operacao.equals(DominioOperacaoBanco.UPD)) {
			Integer pagadorSus = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS)
					.getVlrNumerico().intValue();
			Short convenioSusPadrao = getParametroFacade()
					.buscarAghParametro(
							AghuParametrosEnum.P_CONVENIO_SUS_PADRAO)
					.getVlrNumerico().shortValue();
			Integer pacCodigo = null;
			if (consulta.getPaciente() != null) {
				pacCodigo = consulta.getPaciente().getCodigo();
			}

			Integer grdSeq = null;
			if (consulta.getGradeAgendamenConsulta() != null) {
				grdSeq = consulta.getGradeAgendamenConsulta().getSeq();
			}

			if (CoreUtil.modificados(consulta, consultaAnterior)) {
				this.getFaturamentoFacade()
						.atualizarFaturamentoProcedimentoConsulta(
								consulta.getNumero(), null, null,
								consulta.getRetorno(), nomeMicrocomputador, dataFimVinculoServidor);
			}

			if (consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				atualizarProntuarioVirtual(consulta.getNumero(), consulta
						.getPaciente().getCodigo(), nomeMicrocomputador);
				// Para não barrar a marcaçãoo quando rotina da SMS - primeiras
				// consultas
				if (consulta.getCodCentral() == null && !substituirProntuario) {
					Short condicaoAtendimentoSeq = null;
					if (consulta.getCondicaoAtendimento() != null) {
						condicaoAtendimentoSeq = consulta
								.getCondicaoAtendimento().getSeq();
					}
					verificarConsultaPacienteMesmaGrade(consulta
							.getGradeAgendamenConsulta().getSeq(), pacCodigo,
							consulta.getDtConsulta(), condicaoAtendimentoSeq);
				}
			}

			// verifica grade/paciente
			if (consultaAnterior.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao() != null
					&& !consultaAnterior.getSituacaoConsulta().getSituacao()
							.equals("M")
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {

				// Não permitir marcação de consultas funcionários antes das
				// 07:00 da manhã
				if (verificarConsultasFuncSmo(consulta.getNumero())
						&& Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 7) {
					AmbulatorioConsultasRNExceptionCode.AAC_00706
							.throwExceptionRollback();
				}
				// Ver regras para marcação da consulta
				Integer nmrConsultaAnterior = null;
				if(!isPrimeiraConsulta(consulta)){
					nmrConsultaAnterior = verificarConsultaAnterior(consulta);
				}

				// atualiza o número da consulta pesquisada na regra anterior
				if (nmrConsultaAnterior != null) {
					consulta = atualizarConsultaNumero(consulta.getNumero(),
							nmrConsultaAnterior);
				}

				if (consulta.getCodCentral() == null) {
					Long codCentral = obterCodigoCentral(pacCodigo,
							consulta.getNumero(), consulta.getDtConsulta());
					// Busca código da Central na primeira consulta da mesma
					// especialidade ou genérica
					if (codCentral != null) {
						consulta = atualizarCodigoCentral(codCentral, consulta,
								nmrConsultaAnterior);
					}
				}
				if (consulta.getPaciente() != null) {
					this.atualizarConsultaAtendimento(consulta, nomeMicrocomputador);
				}
				if (!marcacaoConsulta) {
					atualizarConsultaProcedimento(consulta, consulta.getSituacaoConsulta(), nomeMicrocomputador, dataFimVinculoServidor, aack_prh_rn_v_apac_diaria, aack_aaa_rn_v_protese_auditiva, fatk_cap_rn_v_cap_encerramento);
					atualizarConsultaProcGrade(consulta, nomeMicrocomputador, dataFimVinculoServidor);
				}

				if (consulta.getConvenioSaudePlano() != null
						&& Integer.valueOf(consulta.getConvenioSaudePlano().getId()
								.getCnvCodigo()).equals(pagadorSus)) {
					atualizarAmbulatorioInterconsultas(consulta.getNumero());
				}
			}

			atualizarCadastroPaciente(consultaAnterior, consulta, nomeMicrocomputador, dataFimVinculoServidor);
			// Atualizações a partir de alteração em consulta marcada

			if (consultaAnterior.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao()
							.equals("M")
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				if (consulta.getPaciente() != null
						&& consultaAnterior.getPaciente() != null
						&& CoreUtil.modificados(consulta.getPaciente()
								.getCodigo(), consultaAnterior.getPaciente()
								.getCodigo())) {
					atualizarProcedAmbRealizados(consulta.getPaciente()
							.getCodigo(), consulta.getNumero(), nomeMicrocomputador, dataFimVinculoServidor);
					atualizarAtendimentoPaciente(consulta.getNumero(), consulta
							.getPaciente().getCodigo(), nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);

					/*
					 * TODO: CRIAR POJO E DAO DE AAC_RADIO_ACOMP_TOMOS PARA
					 * IMPLEMENTAR O METÓDO ABAIXO:
					 * 
					 * UPDATE AAC_RADIO_ACOMP_TOMOS SET PAC_CODIGO =
					 * l_con_row_new.pac_codigo WHERE PAC_CODIGO =
					 * l_con_saved_row.pac_codigo;
					 */

					// Uma consulta não pode validar outra se os pacientes forem
					// diferentes
					// esta atualiza a consulta anterior desta e das que ela
					// validou
					if (consulta.getPaciente() != null) {
						atualizarAutoRelacionamento(consulta.getNumero(),
								consulta.getPaciente().getCodigo(),
								nomeMicrocomputador,
								dataFimVinculoServidor, substituirProntuario);
					}

					// Atualiza as tabelas do sistema ambulatório onde migra a
					// fk de pacientes
					this.getMarcacaoConsultaRN().atualizarPacienteAmbulatorio(
							consulta.getNumero(), pacCodigo, nomeMicrocomputador);

				}

				if (consulta.getConvenioSaudePlano() != null
						&& CoreUtil.modificados(consulta
								.getConvenioSaudePlano().getId(),
								consultaAnterior.getConvenioSaudePlano()
										.getId())) {
					// atualiza solicitação exames na troca do convênio
					atualizarSolicitacaoExame(consulta, nomeMicrocomputador);
				}
				if (consulta.getConvenioSaudePlano() != null
						&& CoreUtil.modificados(
								consulta.getConvenioSaudePlano().getId()
										.getCnvCodigo(), consultaAnterior
										.getConvenioSaudePlano().getId()
										.getCnvCodigo())
						&& consulta.getConvenioSaudePlano().getId()
								.getCnvCodigo().equals(convenioSusPadrao)
						&& !consultaAnterior.getConvenioSaudePlano().getId()
								.getCnvCodigo().equals(convenioSusPadrao)) {

					atualizarInclusaoProcedHospitalar(consulta,
							consultaAnterior.getSituacaoConsulta(), nomeMicrocomputador, dataFimVinculoServidor);
				}

				if (CoreUtil.modificados(consulta.getGradeAgendamenConsulta()
						.getSeq(), consultaAnterior.getGradeAgendamenConsulta()
						.getSeq())) {
					atualizarProcedimentoHospitalar(consulta, consulta
							.getGradeAgendamenConsulta().getSeq(),
							consultaAnterior.getGradeAgendamenConsulta()
									.getSeq(), nomeMicrocomputador, dataFimVinculoServidor);
				}
			}

			// Quando passa consulta de M para L excluir aac_cid_consultas
			if (consultaAnterior.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& CoreUtil.modificados(consulta.getSituacaoConsulta()
							.getSituacao(), consultaAnterior
							.getSituacaoConsulta().getSituacao())
					&& consultaAnterior.getSituacaoConsulta().getSituacao()
							.equals("M")
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("L")) {
				// deletando os movimentos de prontuario situação Q
				this.atualizarExclusaoCidConsulta(consulta.getNumero());
				if (consultaAnterior.getPaciente() != null) {
					excluirMovimentacaoProntuario(consultaAnterior);
				}

			}

			if (CoreUtil.modificados(consulta, consultaAnterior)
					&& consultaAnterior.getDthrFim() == null
					&& consulta.getDthrFim() != null) {
				List<AghAtendimentos> listaAtendimentos = this
						.getPacienteFacade()
						.listarAtendimentosPorConsultaEOrigem(
								DominioOrigemAtendimento.A,
								consulta.getNumero());
				for (AghAtendimentos atendimento : listaAtendimentos) {
					AghAtendimentos atendimentoAnterior = this
							.getPacienteFacade().clonarAtendimento(atendimento);
					atendimento.setLeito(null);
					this.getPacienteFacade().persistirAtendimento(atendimento,
							atendimentoAnterior, nomeMicrocomputador, dataFimVinculoServidor);
				}

			}

			// Controle de saldo no projeto
			if (consulta.getProjetoPesquisa() != null
					&& consultaAnterior.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")
					&& !consultaAnterior.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				try {
					verificarSaldoProjeto(consulta.getProjetoPesquisa()
							.getSeq());
				} catch (NotImplementedException e) {
					logError(EXCECAO_IGNORADA, e);
				}
			}

			// Se a consulta está sendo MARCADA no dia corrente grava um
			// registro no ambulatório para indicar que o paciente já está
			// aguardando
			Calendar cal = Calendar.getInstance();
			cal.setTime(consulta.getDtConsulta());

			if (consultaAnterior.getSituacaoConsulta() != null
					&& consultaAnterior.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& CoreUtil.modificados(consultaAnterior
							.getSituacaoConsulta().getSituacao(), consulta
							.getSituacaoConsulta().getSituacao())
					&& consultaAnterior.getSituacaoConsulta().getSituacao()
							.equals("L")
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")
					&& DateUtils
							.truncate(Calendar.getInstance(), Calendar.DATE)
							.equals(DateUtils.truncate(cal, Calendar.DATE))) {
				this.getMarcacaoConsultaRN().atualizarPacienteAguardando(
						consulta.getNumero(), pacCodigo, grdSeq, nomeMicrocomputador);
			}

			 if (consultaAnterior.getIndSituacaoConsulta() != null
			 && consultaAnterior.getIndSituacaoConsulta().getSituacao() !=
			 null
			 && consulta.getIndSituacaoConsulta() != null
			 && consulta.getIndSituacaoConsulta().getSituacao() != null
			 && consultaAnterior.getIndSituacaoConsulta().getSituacao()
			 .equals("M")
			 && consulta.getIndSituacaoConsulta().getSituacao()
			 .equals("M")
			 && CoreUtil.modificados(consulta.getConvenioSaudePlano()
			 .getId().getCnvCodigo(), consultaAnterior
			 .getConvenioSaudePlano().getId().getCnvCodigo())) {
			 //#42229 Incluido	 
		     atualizaLicitacaoSMO(consulta,consultaAnterior);
			 }
		} else if (operacao.equals(DominioOperacaoBanco.DEL)) {
			// Caso em que a consulta tem situacao Marcada e excede programacao
			atualizarCadastroPaciente(null, consultaAnterior, nomeMicrocomputador, dataFimVinculoServidor);
		}
		
		if(flush){
			this.flush();
		}
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_VER_IDAD_PAC
	 */
	public void verificarIdadePaciente(AacConsultas consulta)
			throws ApplicationBusinessException {

		// Idade pac incompatível com esp
		if (!(consulta.getPaciente().getIdade(consulta.getDtConsulta()) >= consulta
				.getGradeAgendamenConsulta().getEspecialidade()
				.getIdadeMinPacAmbulatorio() && consulta.getPaciente()
				.getIdade(consulta.getDtConsulta()) <= consulta
				.getGradeAgendamenConsulta().getEspecialidade()
				.getIdadeMaxPacAmbulatorio())) {
			if (consulta.getCodCentral() == null) {
				AmbulatorioConsultasRNExceptionCode.AAC_00265.throwException();
			}
		}
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_ATU_EXC_CCO
	 */
	public void atualizarExclusaoCidConsulta(Integer consultaNumero)
			throws ApplicationBusinessException {
		List<AacCidConsultas> cidConsultas = this.getAacCidConsultasDAO()
				.obterCidConsultaPorNumeroConsulta(consultaNumero);
		if (cidConsultas != null && !cidConsultas.isEmpty()) {
			for (AacCidConsultas cidConsulta : cidConsultas) {
				this.removerCidConsulta(cidConsulta);
			}
		}
	}

	/**
	 * ORADB Trigger AACT_CCO_BSI,AACT_CCO_ARI, AACT_CCO_ASI
	 * 
	 * @param cidConsulta
	 */
	// TODO implementar triggers quando forem invocadas em estórias futuras
	public void removerCidConsulta(AacCidConsultas cidConsulta) {
		this.getAacCidConsultasDAO().remover(cidConsulta);
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_ATU_CO_PR_GR
	 * 
	 * @throws NumberFormatException
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Deprecated
	public void atualizarConsultaProcGrade(AacConsultas consulta, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws NumberFormatException, BaseException {
		List<AacGradeProcedHospitalar> listaGradeProcedHospitalares = this
				.getAacGradeProcedimentoHospitalarDAO()
				.listarProcedimentosGrade(
						consulta.getGradeAgendamenConsulta().getSeq());
		for (AacGradeProcedHospitalar gradeProcedHospitalar : listaGradeProcedHospitalares) {
			Byte quantidade = VALOR_PADRAO_QUANTIDADE_CONSULTA;
			FatProcedHospInternos procedHospInterno = gradeProcedHospitalar
					.getProcedHospInterno();
			AacConsultaProcedHospitalar consultaProcedHospitalar = new AacConsultaProcedHospitalar();
			AacConsultaProcedHospitalarId consultaProcedHospitalarId = new AacConsultaProcedHospitalarId();
			consultaProcedHospitalarId.setConNumero(consulta.getNumero());
			consultaProcedHospitalarId.setPhiSeq(procedHospInterno.getSeq());
			consultaProcedHospitalar.setId(consultaProcedHospitalarId);
			consultaProcedHospitalar.setQuantidade(quantidade);
			consultaProcedHospitalar.setConsulta(false);
			consultaProcedHospitalar.setConsultas(consulta);
			consultaProcedHospitalar.setProcedHospInterno(procedHospInterno);
			this.getProcedimentoConsultaRN().inserirProcedimentoConsulta(
					consultaProcedHospitalar, false, nomeMicrocomputador, dataFimVinculoServidor);
		}

	}
	
	/**
	 * ORADB: AACK_CON_RN.RN_CONP_ATU_CO_PR_GR
	 * 
	 * @throws NumberFormatException
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void atualizarConsultaProcGrade(AacConsultas consulta, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento)
			throws NumberFormatException, BaseException {
		List<AacGradeProcedHospitalar> listaGradeProcedHospitalares = this
				.getAacGradeProcedimentoHospitalarDAO()
				.listarProcedimentosGrade(
						consulta.getGradeAgendamenConsulta().getSeq());
		for (AacGradeProcedHospitalar gradeProcedHospitalar : listaGradeProcedHospitalares) {
			Byte quantidade = VALOR_PADRAO_QUANTIDADE_CONSULTA;
			FatProcedHospInternos procedHospInterno = gradeProcedHospitalar
					.getProcedHospInterno();
			AacConsultaProcedHospitalar consultaProcedHospitalar = new AacConsultaProcedHospitalar();
			AacConsultaProcedHospitalarId consultaProcedHospitalarId = new AacConsultaProcedHospitalarId();
			consultaProcedHospitalarId.setConNumero(consulta.getNumero());
			consultaProcedHospitalarId.setPhiSeq(procedHospInterno.getSeq());
			consultaProcedHospitalar.setId(consultaProcedHospitalarId);
			consultaProcedHospitalar.setQuantidade(quantidade);
			consultaProcedHospitalar.setConsulta(false);
			consultaProcedHospitalar.setConsultas(consulta);
			consultaProcedHospitalar.setProcedHospInterno(procedHospInterno);
			this.getProcedimentoConsultaRN().inserirProcedimentoConsulta(consultaProcedHospitalar,
					false, nomeMicrocomputador, dataFimVinculoServidor, aack_prh_rn_v_apac_diaria,
					aack_aaa_rn_v_protese_auditiva, fatk_cap_rn_v_cap_encerramento);
		}

	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_ATU_PAC_ATD
	 * @throws BaseException 
	 */
	public void atualizarAtendimentoPaciente(Integer numeroConsulta,
			Integer pacCodigo, String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean substituirProntuario) throws BaseException {
		AacConsultas consulta = this.getAacConsultasDAO()
				.obterPorChavePrimaria(numeroConsulta);
		AipPacientes paciente = this.getPacienteFacade()
				.obterAipPacientesPorChavePrimaria(pacCodigo);
		this.getPacienteFacade().atualizarPacienteAtendimento(null, null, null,
				consulta, paciente, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_ATU_ATEND
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void atualizarConsultaAtendimento(AacConsultas consulta, String nomeMicrocomputador)
			throws ApplicationBusinessException {

		Integer pacCodigo = null;
		if (consulta.getPaciente() != null) {
			pacCodigo = consulta.getPaciente().getCodigo();
		}

		String leitoID = null;
		if (consulta.getAtendimento() != null
				&& consulta.getAtendimento().getLeito() != null) {
			leitoID = consulta.getAtendimento().getLeito().getLeitoID();
		}

		Short numeroQuarto = null;
		if (consulta.getAtendimento() != null
				&& consulta.getAtendimento().getQuarto() != null) {
			numeroQuarto = consulta.getAtendimento().getQuarto().getNumero();
		}

		Short unfSeq = null;
		Short espSeq = null;
		Integer clcCodigo = null;
		if (consulta.getGradeAgendamenConsulta() != null) {
			AacGradeAgendamenConsultas grade = getAacGradeAgendamentoConsultasDAO().obterPorChavePrimaria(consulta.getGradeAgendamenConsulta().getSeq());
			if (grade.getAacUnidFuncionalSala() != null
					&& grade.getAacUnidFuncionalSala().getUnidadeFuncional() != null) {
				unfSeq = grade.getAacUnidFuncionalSala().getUnidadeFuncional()
						.getSeq();
			}
			if (grade.getEspecialidade() != null) {
				espSeq = grade.getEspecialidade().getSeq();
				if (grade.getEspecialidade().getClinica() != null) {
					clcCodigo = grade.getEspecialidade().getClinica()
							.getCodigo();
				}
			}
		}

		Integer matricula = null;
		Short vinCodigo = null;
		if (consulta.getServidor() != null
				&& consulta.getServidor().getId() != null) {
			matricula = consulta.getServidor().getId().getMatricula();
			vinCodigo = consulta.getServidor().getId().getVinCodigo();
		}

		if (consulta.getServidor() != null
				&& consulta.getServidor().getId() != null) {
			matricula = consulta.getServidor().getId().getMatricula();
			vinCodigo = consulta.getServidor().getId().getVinCodigo();
		}
		RapServidoresId servidorId = new RapServidoresId();
		if (matricula != null && vinCodigo != null) {
			servidorId.setMatricula(matricula);
			servidorId.setVinCodigo(vinCodigo);
		}
		RapServidores servidor = new RapServidores();
		if (servidorId != null) {
			servidor = this.getRegistroColaboradorFacade()
					.obterRapServidoresPorChavePrimaria(servidorId);
		}

		Integer grdSeq = null;
		if (consulta.getGradeAgendamenConsulta() != null) {
			grdSeq = consulta.getGradeAgendamenConsulta().getSeq();
		}

		if(consulta.getSituacaoConsulta()!=null && "M".equals(consulta.getSituacaoConsulta().getSituacao())){
			if(consulta.getGradeAgendamenConsulta().getEspecialidade()==null){
				throw new ApplicationBusinessException(AmbulatorioRNExceptionCode.AAC_00070);
			}
		}
		this.getPacienteFacade().incluirAtendimento(pacCodigo,
				consulta.getDtConsulta(), null, null, null, null, leitoID,
				numeroQuarto, unfSeq, espSeq, null, clcCodigo, servidor, null,
				null, null, null, consulta.getNumero(), grdSeq, nomeMicrocomputador);

	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_ATU_CAD_PAC
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void atualizarCadastroPaciente(AacConsultas consultaAnterior, AacConsultas consulta
			, String nomeMicrocomputador, final Date dataFimVinculoServidor) 
					throws ApplicationBusinessException, ApplicationBusinessException {
		
		Integer codigoPaciente = null;
		String indSitConsulta = null;
		if (consulta.getSituacaoConsulta() != null) {
			indSitConsulta = consulta.getSituacaoConsulta().getSituacao();
		}
		
		AipPacientes paciente = consulta.getPaciente();
		if (paciente != null) {
			codigoPaciente = paciente.getCodigo();
			paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(
					paciente.getCodigo()
			);
		}
		AipPacientes pacienteOld = null;
		if (consultaAnterior != null && consultaAnterior.getPaciente() != null) {
			pacienteOld = pacienteFacade.obterAipPacientesPorChavePrimaria(
					consultaAnterior.getPaciente().getCodigo()
			);
		}

		Integer codigoPacienteOld = null;
		if (pacienteOld != null) {
			codigoPacienteOld = pacienteOld.getCodigo();
		}

		if (consulta.getSituacaoConsulta() != null
				&& "M".equals(consulta.getSituacaoConsulta().getSituacao())
				&& (consultaAnterior == null
						|| consultaAnterior.getSituacaoConsulta() == null || !consultaAnterior
						.getSituacaoConsulta().getSituacao().equals("M"))) {
			Date dataUltConsultaPac = null;
			if (paciente != null) {
				if (paciente.getDtUltConsulta() != null) {
					dataUltConsultaPac = paciente.getDtUltConsulta();
				} else {
					dataUltConsultaPac = consulta.getDtConsulta();
				}
			} else {
				throw new ApplicationBusinessException(
						AmbulatorioConsultaRNExceptionCode.AIP_00013);
			}

			if (dataUltConsultaPac.before(consulta.getDtConsulta())) {
				paciente.setDtUltConsulta(consulta.getDtConsulta());
				/*
				 * this.getCadastroPacienteFacade().atualizarPaciente(paciente,
				 * getPessoa());
				 */
			}
		}
		if (consulta.getSituacaoConsulta() != null
				&& consulta.getSituacaoConsulta().getSituacao().equals("L")
				&& consultaAnterior != null
				&& consultaAnterior.getSituacaoConsulta() != null
				&& consultaAnterior.getSituacaoConsulta().getSituacao()
						.equals("M")) {
			Date dataUltConsultaPac = null;
			Date consultaUltimaData = null;
			if (pacienteOld != null) {
				if (pacienteOld.getDtUltConsulta() != null) {
					dataUltConsultaPac = pacienteOld.getDtUltConsulta();
				} else {
					dataUltConsultaPac = consulta.getDtConsulta();
				}
			} else {
				throw new ApplicationBusinessException(
						AmbulatorioConsultaRNExceptionCode.AIP_00013);
			}
			if (dataUltConsultaPac.before(consulta.getDtConsulta())) {
				consultaUltimaData = getAacConsultasDAO()
						.obterConsultaUltimaDataPaciente(codigoPacienteOld,
								consulta.getDtConsulta());
				if (consultaUltimaData == null) {
					throw new ApplicationBusinessException(
							AmbulatorioConsultasRNExceptionCode.AAC_00037);
				}
				pacienteOld.setDtUltConsulta(consultaUltimaData);
				this.getCadastroPacienteFacade().atualizarPacienteParcial(pacienteOld, nomeMicrocomputador, dataFimVinculoServidor);
			}
		}

		if (codigoPacienteOld != null && codigoPaciente != null
				&& codigoPaciente != codigoPacienteOld) {
			Date dataUltConsultaPac = null;
			Date consultaUltimaData = null;
			if (pacienteOld != null) {
				if (pacienteOld.getDtUltConsulta() != null) {
					dataUltConsultaPac = pacienteOld.getDtUltConsulta();
				} else {
					dataUltConsultaPac = consulta.getDtConsulta();
				}
			} else {
				throw new ApplicationBusinessException(
						AmbulatorioConsultaRNExceptionCode.AIP_00013);
			}
			if (dataUltConsultaPac.before(consulta.getDtConsulta())) {
				consultaUltimaData = getAacConsultasDAO()
						.obterConsultaUltimaDataPaciente(
								pacienteOld.getCodigo(),
								consulta.getDtConsulta());
				if (consultaUltimaData == null) {
					throw new ApplicationBusinessException(
							AmbulatorioConsultasRNExceptionCode.AAC_00037);
				}
				pacienteOld.setDtUltConsulta(consultaUltimaData);
				this.getCadastroPacienteFacade().atualizarPacienteParcial(pacienteOld, nomeMicrocomputador, dataFimVinculoServidor);
			}
			if (paciente != null) {
				if (paciente.getDtUltConsulta() != null) {
					dataUltConsultaPac = paciente.getDtUltConsulta();
				} else {
					dataUltConsultaPac = consulta.getDtConsulta();
				}
			} else {
				throw new ApplicationBusinessException(
						AmbulatorioConsultaRNExceptionCode.AIP_00013);
			}

			if (dataUltConsultaPac.before(consulta.getDtConsulta())) {
				paciente.setDtUltConsulta(consulta.getDtConsulta());
				this.getCadastroPacienteFacade().atualizarPacienteParcial(paciente, nomeMicrocomputador, dataFimVinculoServidor);
			}
		}

		if (codigoPacienteOld != null && codigoPaciente == null
				&& indSitConsulta == null) {
			Date dataUltConsultaPac = null;
			Date consultaUltimaData = null;
			if (pacienteOld != null) {
				if (pacienteOld.getDtUltConsulta() != null) {
					dataUltConsultaPac = pacienteOld.getDtUltConsulta();
				} else {
					dataUltConsultaPac = consulta.getDtConsulta();
				}
			} else {
				throw new ApplicationBusinessException(
						AmbulatorioConsultaRNExceptionCode.AIP_00013);
			}
			if (dataUltConsultaPac.before(consulta.getDtConsulta())) {
				consultaUltimaData = getAacConsultasDAO()
						.obterConsultaUltimaDataPaciente(
								pacienteOld.getCodigo(),
								consulta.getDtConsulta());
				pacienteOld.setDtUltConsulta(consultaUltimaData);
				this.getCadastroPacienteFacade().atualizarPacienteParcial(pacienteOld, nomeMicrocomputador, dataFimVinculoServidor);
			}
		}
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_ATU_CO_IT_PR
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Deprecated
	public void atualizarConsultaProcedimento(AacConsultas consulta,
			AacSituacaoConsultas aacSituacaoConsultas, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		AghParametros parametroHU = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_HU);
		Short codigoHU = null;
		if (parametroHU != null) {
			codigoHU = parametroHU.getVlrNumerico().shortValue();
		}

		AghParametros parametroConvenioUniversidade = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_UNIVERSIDADE);
		Short codigoUniversidade = null;
		if (parametroConvenioUniversidade != null) {
			codigoUniversidade = parametroConvenioUniversidade.getVlrNumerico().shortValue();
		}

		AghParametros parametroConvenioSusPadrao = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		Short codigoSus = null;
		if (parametroConvenioSusPadrao != null) {
			codigoSus = parametroConvenioSusPadrao.getVlrNumerico()
					.shortValue();
		}

		/*
		 * Este trecho foi comentado conforme acordado com Milenaif
		 * aacc_ver_caract_grd(p_new_grd_seq,'Nao Lanca Consulta BPA') = 'N'
		 */
		if (consulta.getConvenioSaudePlano() != null
				&& consulta.getConvenioSaudePlano().getId() != null
				&& (consulta.getConvenioSaudePlano().getId().getCnvCodigo()
						.equals(codigoHU)
						|| consulta.getConvenioSaudePlano().getId()
								.getCnvCodigo().equals(codigoSus) || consulta
						.getConvenioSaudePlano().getId().getCnvCodigo()
						.equals(codigoUniversidade))) {
			List<AacProcedHospEspecialidades> lista = null;
			if (consulta.getGradeAgendamenConsulta() != null
					&& consulta.getGradeAgendamenConsulta().getEspecialidade() != null) {
				lista = this.getAacProcedHospEspecialidadesDAO()
						.listarProcedimentosEspecialidadesConsulta(
								consulta.getGradeAgendamenConsulta()
										.getEspecialidade().getSeq());
			}
			if (lista.size() > 0) {
				AacProcedHospEspecialidades procedHospEspecialidades = lista
						.get(0);
				if (!consulta.getGradeAgendamenConsulta().getProcedimento()) {
					AacConsultaProcedHospitalar consultaProcedHospitalar = new AacConsultaProcedHospitalar();
					AacConsultaProcedHospitalarId id = new AacConsultaProcedHospitalarId();
					id.setConNumero(consulta.getNumero());
					if (procedHospEspecialidades.getProcedHospInterno() != null) {
						FatProcedHospInternos procedHospInterno = procedHospEspecialidades
								.getProcedHospInterno();
						id.setPhiSeq(procedHospInterno.getSeq());
						consultaProcedHospitalar.setId(id);
						consultaProcedHospitalar.setConsultas(consulta);
						consultaProcedHospitalar
								.setProcedHospInterno(procedHospInterno);
						consultaProcedHospitalar
								.setQuantidade(VALOR_PADRAO_QUANTIDADE_CONSULTA);
						consultaProcedHospitalar.setConsulta(true);
						this.getProcedimentoConsultaRN()
								.inserirProcedimentoConsulta( consultaProcedHospitalar, false, 
															  nomeMicrocomputador, 
															  dataFimVinculoServidor);
					}
				}
			}
		}
	}
	
	/**
	 * ORADB: AACK_CON_RN.RN_CONP_ATU_CO_IT_PR
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizarConsultaProcedimento(AacConsultas consulta,
			AacSituacaoConsultas aacSituacaoConsultas, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento) throws BaseException {
		AghParametros parametroHU = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_HU);
		Short codigoHU = null;
		if (parametroHU != null) {
			codigoHU = parametroHU.getVlrNumerico().shortValue();
		}

		AghParametros parametroConvenioUniversidade = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_UNIVERSIDADE);
		Short codigoUniversidade = null;
		if (parametroConvenioUniversidade != null) {
			codigoUniversidade = parametroConvenioUniversidade.getVlrNumerico().shortValue();
		}

		AghParametros parametroConvenioSusPadrao = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		Short codigoSus = null;
		if (parametroConvenioSusPadrao != null) {
			codigoSus = parametroConvenioSusPadrao.getVlrNumerico()
					.shortValue();
		}

		/*
		 * Este trecho foi comentado conforme acordado com Milenaif
		 * aacc_ver_caract_grd(p_new_grd_seq,'Nao Lanca Consulta BPA') = 'N'
		 */
		if (consulta.getConvenioSaudePlano() != null
				&& consulta.getConvenioSaudePlano().getId() != null
				&& (consulta.getConvenioSaudePlano().getId().getCnvCodigo()
						.equals(codigoHU)
						|| consulta.getConvenioSaudePlano().getId()
								.getCnvCodigo().equals(codigoSus) || consulta
						.getConvenioSaudePlano().getId().getCnvCodigo()
						.equals(codigoUniversidade))) {
			List<AacProcedHospEspecialidades> lista = null;
			if (consulta.getGradeAgendamenConsulta() != null
					&& consulta.getGradeAgendamenConsulta().getEspecialidade() != null) {
				lista = this.getAacProcedHospEspecialidadesDAO()
						.listarProcedimentosEspecialidadesConsulta(
								consulta.getGradeAgendamenConsulta()
										.getEspecialidade().getSeq());
			}
			if (lista.size() > 0) {
				AacProcedHospEspecialidades procedHospEspecialidades = lista
						.get(0);
				if (!consulta.getGradeAgendamenConsulta().getProcedimento()) {
					AacConsultaProcedHospitalar consultaProcedHospitalar = new AacConsultaProcedHospitalar();
					AacConsultaProcedHospitalarId id = new AacConsultaProcedHospitalarId();
					id.setConNumero(consulta.getNumero());
					if (procedHospEspecialidades.getProcedHospInterno() != null) {
						FatProcedHospInternos procedHospInterno = procedHospEspecialidades
								.getProcedHospInterno();
						id.setPhiSeq(procedHospInterno.getSeq());
						consultaProcedHospitalar.setId(id);
						consultaProcedHospitalar.setConsultas(consulta);
						consultaProcedHospitalar
								.setProcedHospInterno(procedHospInterno);
						consultaProcedHospitalar
								.setQuantidade(VALOR_PADRAO_QUANTIDADE_CONSULTA);
						consultaProcedHospitalar.setConsulta(true);
						this.getProcedimentoConsultaRN()
							.inserirProcedimentoConsulta(consultaProcedHospitalar, false, nomeMicrocomputador,
									dataFimVinculoServidor, aack_prh_rn_v_apac_diaria, aack_aaa_rn_v_protese_auditiva,
									fatk_cap_rn_v_cap_encerramento);
					}
				}
			}
		}
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_ATU_RETORNO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public AacRetornos atualizarRetornoConsulta(
			AacSituacaoConsultas newSituacaoConsulta,
			AacSituacaoConsultas oldSituacaoConsulta)
			throws ApplicationBusinessException {

		AacRetornos retorno = null;
		if (newSituacaoConsulta.getSituacao().equals("M")
				&& (oldSituacaoConsulta == null || !oldSituacaoConsulta
						.getSituacao().equals("M"))) {

			retorno = getAacRetornosDAO().obterPorChavePrimaria(DominioSituacaoAtendimento.PACIENTE_AGENDADO.getCodigo());
		}
		return retorno;
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_VER_EXME_SOL
	 */
	@SuppressWarnings("ucd")
	public void verificarExameSolicitacao(AacConsultas consulta)
			throws ApplicationBusinessException {
		if (getAghuFacade().existeAtendimentoComSolicitacaoExame(consulta)) {
			AmbulatorioConsultasRNExceptionCode.AAC_00124.throwException();
		}
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_VER_COMP_FAT
	 */
	public void verificarConsultaFat(Integer consultaNumero)
			throws ApplicationBusinessException {

		List<FatProcedAmbRealizado> procedAmbRealizado = getFaturamentoFacade()
				.listarProcedAmbRealizadoPorPrhConsultaSituacao(consultaNumero);

		List<FatItemContaApac> itensContaApac = getFaturamentoFacade()
				.listarItemContaApacPorPrhConsultaSituacao(consultaNumero);

		if (!procedAmbRealizado.isEmpty() || !itensContaApac.isEmpty()) {
			AmbulatorioConsultasRNExceptionCode.AAC_00101.throwException();
		}
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_VER_CONV_ATI
	 */
	public void verificarConsultaConvenioSituacao(
			FatConvenioSaudePlano convenioSaudePlano)
			throws ApplicationBusinessException {

		if (convenioSaudePlano != null) {

			if (convenioSaudePlano.getConvenioSaude() != null
					&& convenioSaudePlano.getConvenioSaude().getSituacao() != DominioSituacao.A) {
				AmbulatorioConsultasRNExceptionCode.AAC_00068.throwException();
			}

			VAacConvenioPlano vConvenioPlano = null;
			if (convenioSaudePlano.getId() != null) {
				vConvenioPlano = getVAacConvenioPlanoDAO()
						.obterVAacConvenioPlanoAtivoPorId(
								convenioSaudePlano.getId().getCnvCodigo(),
								convenioSaudePlano.getId().getSeq());
			}

			if (vConvenioPlano == null) {
				AmbulatorioConsultasRNExceptionCode.AAC_00268.throwException();
			}
		}
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_VER_CO_TRAT
	 */
	public void verificarConsultaAtendimento(AghAtendimentos atendimento,
			Integer pacCodigo) throws ApplicationBusinessException {
		if (atendimento != null
				&& !atendimento.getPaciente().getCodigo().equals(pacCodigo)) {
			AmbulatorioConsultasRNExceptionCode.AAC_00104.throwException();
		}
	}

	/**
	 * ORADB: AACK_CON_RN.RN_CONP_VER_RETORNO
	 */
	public void verificarConsultaRetorno(String situacaoConsulta,
			AacRetornos retorno) throws ApplicationBusinessException {

		// Consulta marcada deve ter retorno informado
		if (situacaoConsulta.equals("M") && retorno == null) {
			AmbulatorioConsultasRNExceptionCode.AAC_00108.throwException();
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_ATU_MTO_SEQ
	 * 
	 * @throws ApplicationBusinessException
	 */

	public Short obterMotivo(Integer grdSeq, Short mtoCodigo)
			throws ApplicationBusinessException {
		this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_EXAME_PERIODICO);
		if (verificarCaracteristicaGrade(grdSeq,
				DominioCaracteristicaGrade.EXAMES_PERIODICOS)) {
			return mtoCodigo;
		}
		return null;
	}

	/**
	 * Procedure
	 * 
	 * Exclui da tabela AIP_MOVIMENTACAO_PRONTUARIOS pelo paciente da consulta.
	 * 
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_MTO_PRNT
	 * 
	 * @param consulta
	 */
	public void excluirMovimentacaoProntuario(AacConsultas consulta) {
		AipPacientes paciente = consulta.getPaciente();
		if (paciente != null) {
			IPacienteFacade pacienteFacade = getPacienteFacade();
			List<AipMovimentacaoProntuarios> lista = pacienteFacade
					.pesquisarMovimentacaoPacienteProntRequerido(
							paciente.getCodigo(), consulta.getNumero());
			if (lista != null && !lista.isEmpty()) {
				for (AipMovimentacaoProntuarios aipMovimentacaoProntuario : lista) {
					
					
					
					pacienteFacade.
					observarPersistenciaMovimentacaoProntuario(aipMovimentacaoProntuario, DominioOperacoesJournal.DEL);
					
					pacienteFacade
							.removerAipMovimentacaoProntuariosSemFlush(aipMovimentacaoProntuario);
				}
			}
		}
	}

	/**
	 * ORADB: PROCEDURE AACK_CON_2_RN.RN_CONP_ATU_PAC_PMR
	 * 
	 * @param pacCodigo
	 * @param consultaNumero
	 * @throws BaseException 
	 */
	// TODO Quando invocado devem ser implementadas as triggers da tabela
	// FatProcedAmbRealizado
	public void atualizarProcedAmbRealizados(Integer pacCodigo,
			Integer consultaNumero, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		IPacienteFacade pacienteFacade = getPacienteFacade();

		List<FatProcedAmbRealizado> listaFatProcedAmbRealizadao = faturamentoFacade
				.listarProcedAmbRealizadoPorConsulta(consultaNumero);
		for (FatProcedAmbRealizado procedAmbRealizado : listaFatProcedAmbRealizadao) {
			FatProcedAmbRealizado procOld = faturamentoFacade.clonarFatProcedAmbRealizado(procedAmbRealizado);
			AipPacientes paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo);
			procedAmbRealizado.setPaciente(paciente);
			
			faturamentoFacade.atualizarProcedimentoAmbulatorialRealizado(procedAmbRealizado, procOld, nomeMicrocomputador, dataFimVinculoServidor);
		}
	}
	

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_DT_FER
	 * 
	* @throws ApplicationBusinessException
	 */
	public void verificarDataFeriado(Date dtConsulta, Integer grdSeq) throws ApplicationBusinessException {

		AghFeriados feriado = getAghuFacade().obterFeriado(dtConsulta);
		AacGradeAgendamenConsultas grade = this.getAacGradeAgendamentoConsultasDAO().obterPorChavePrimaria(grdSeq);
		
		if (grade.getEspecialidade() != null && grade.getEspecialidade().getIndHoraDispFeriado().equals(DominioSimNao.N)) {
			if (CoreUtil.retornaDiaSemana(dtConsulta).equals(DominioDiaSemana.SABADO) 
					|| CoreUtil.retornaDiaSemana(dtConsulta).equals(DominioDiaSemana.DOMINGO)
					|| (feriado!=null && (feriado.getTurno() != null && feriado.getTurno().equals(this.obterTurno(dtConsulta)) || feriado.getTurno() == null))) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.AAC_00271);
			}	
		}
	}

	/**
	 * ORADB: FUNCTION aacc_busca_turno
	 * 
	 * @param data
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public DominioTurno obterTurno(Date data) throws ApplicationBusinessException,
			ApplicationBusinessException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setLenient(false);
		Date dtAtual;
		try {
			dtAtual = sdf.parse(sdf.format(data));

			AghParametros dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_INI_TURNO1);
			Date dtInicial1 = sdf.parse(dtParam.getVlrTexto());
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_FIM_TURNO1);
			Date dtFinal1 = sdf.parse(dtParam.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial1, dtFinal1)) {
				return DominioTurno.M;
			}

			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_INI_TURNO2);
			Date dtInicial2 = sdf.parse(dtParam.getVlrTexto());
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_FIM_TURNO2);
			Date dtFinal2 = sdf.parse(dtParam.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial2, dtFinal2)) {
				return DominioTurno.T;
			}

			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_INI_TURNO3);
			Date dtInicial3 = sdf.parse(dtParam.getVlrTexto());
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_FIM_TURNO3);
			Date dtFinal3 = sdf.parse(dtParam.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial3, dtFinal3)) {
				return DominioTurno.N;
			}
		} catch (ParseException e) {
			AmbulatorioConsultasRNExceptionCode.AAC_00197.throwException();
		}
		return null;
	}

	/**
	 * ORADB: FUNCTION aacc_busca_turno
	 * 
	 * @param data
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public DominioTurno obterTurno(Date data, AghParametros pIniTurno1,
			AghParametros pFimTurno1, AghParametros pIniTurno2,
			AghParametros pFimTurno2, AghParametros pIniTurno3,
			AghParametros pFimTurno3) throws ApplicationBusinessException,
			ApplicationBusinessException {

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setLenient(false);
		Date dtAtual;
		try {
			dtAtual = sdf.parse(sdf.format(data));

			Date dtInicial1 = sdf.parse(pIniTurno1.getVlrTexto());
			Date dtFinal1 = sdf.parse(pFimTurno1.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial1, dtFinal1)) {
				return DominioTurno.M;
			}

			Date dtInicial2 = sdf.parse(pIniTurno2.getVlrTexto());
			Date dtFinal2 = sdf.parse(pFimTurno2.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial2, dtFinal2)) {
				return DominioTurno.T;
			}

			Date dtInicial3 = sdf.parse(pIniTurno3.getVlrTexto());
			Date dtFinal3 = sdf.parse(pFimTurno3.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial3, dtFinal3)) {
				return DominioTurno.N;
			}
		} catch (ParseException e) {
			AmbulatorioConsultasRNExceptionCode.AAC_00197.throwException();
		}
		return null;
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_DT_ATEND
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarDataAtendimento(AacConsultas consulta,
			Date oldDthrInicio) throws ApplicationBusinessException,
			ApplicationBusinessException {
		IPacienteFacade pacienteFacade = this.getPacienteFacade();
		
		// -- verifica se possui ingresso SO ou Internação para esta consulta
		AghAtendimentos atendimento = this.getAghuFacade()
				.obterAtendimentoPorNumeroConsulta(consulta.getNumero());

		if (atendimento.getAtendimentoUrgencia() != null
				&& atendimento.getAtendimentoUrgencia().getSeq() != null) {
			throw new ApplicationBusinessException(
					AmbulatorioConsultasRNExceptionCode.AAC_00290);
		}

		if (atendimento.getInternacao() != null
				&& atendimento.getInternacao().getSeq() != null) {
			throw new ApplicationBusinessException(
					AmbulatorioConsultasRNExceptionCode.AAC_00291);
		}

		if (consulta.getDthrInicio() == null) {
			List<AipLocalizaPaciente> listaPacientes = pacienteFacade.listarPacientesPorAtendimento(
							consulta.getNumero(),
							consulta.getPaciente().getCodigo());
			for (AipLocalizaPaciente localizaPaciente : listaPacientes) {
				pacienteFacade.removerAipLocalizaPaciente(localizaPaciente);
			}
			return;
		}

		if (consulta.getDthrFim() == null) {
			List<AipLocalizaPaciente> listaPacientes = pacienteFacade.listarPacientesPorAtendimento(
							consulta.getNumero(),
							consulta.getPaciente().getCodigo());
			for (AipLocalizaPaciente localizaPaciente : listaPacientes) {
				pacienteFacade.removerAipLocalizaPaciente(localizaPaciente);
			}
		}

	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_ATU_SOL_EXME
	 * 
	 * @throws BaseException
	 */
	public void atualizarSolicitacaoExame(AacConsultas consulta, String nomeMicrocomputador)
			throws BaseException {
		List<AghAtendimentos> lista = this.getAghuFacade()
				.pesquisarAtendimentosPorNumeroConsulta(consulta.getNumero());
		AghAtendimentos atendimento = lista.get(0);
		if (lista != null && lista.size() > 0) {
			List<AelSolicitacaoExames> listaSolExames = this.getExamesFacade()
					.buscarSolicitacaoExamesPorAtendimento(atendimento);
			if (listaSolExames != null && listaSolExames.size() > 0) {
				for (AelSolicitacaoExames solicitacaoExame : listaSolExames) {
					solicitacaoExame.setConvenioSaudePlano(consulta
							.getConvenioSaudePlano());
					this.getExamesBeanFacade().atualizarSolicitacaoExame(
							solicitacaoExame, null, nomeMicrocomputador);
				}
			}
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_PAC_OBT
	 */
	public void verificarObitoPaciente(AacConsultas consulta)
			throws ApplicationBusinessException {

		if (consulta.getPaciente() != null) {

			AipPacientes paciente = consulta.getPaciente();

			Calendar dtObitoExterno = Calendar.getInstance();
			if (paciente.getDtObitoExterno() != null) {
				dtObitoExterno.setTime(paciente.getDtObitoExterno());
			}
			Calendar dtConsulta = Calendar.getInstance();
			dtConsulta.setTime(consulta.getDtConsulta());

			if ((paciente.getDtObito() != null || paciente.getTipoDataObito() != null)
					&& (paciente.getDtObito() != null
							&& DateUtil.truncaData(paciente.getDtObito()).before(DateUtil.truncaData(consulta.getDtConsulta()))
							|| (paciente.getDtObito() == null && 
							DominioTipoDataObito.IGN.equals(paciente.getTipoDataObito())) || 
							(paciente.getDtObito() == null && (DominioTipoDataObito.DMA.equals(paciente.getTipoDataObito())
							&& paciente.getDtObitoExterno() != null
							&& paciente.getDtObitoExterno().before(consulta.getDtConsulta())
							|| DominioTipoDataObito.MES.equals(paciente.getTipoDataObito())
							&& dtObitoExterno.get(Calendar.MONTH) < dtConsulta.get(Calendar.MONTH) || 
							DominioTipoDataObito.ANO.equals(paciente.getTipoDataObito())
							&& dtObitoExterno.get(Calendar.YEAR) < dtConsulta.get(Calendar.YEAR))))) {

				AmbulatorioConsultasRNExceptionCode.AAC_00269.throwException();
			}
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_DIF_DATA
	 */
	public void verificarDiferencaDatas(AacConsultas consulta)
			throws ApplicationBusinessException {

		Integer horas = null;

		AacGradeAgendamenConsultas grade = consulta.getGradeAgendamenConsulta();

		Boolean emergencia = getInternacaoFacade()
				.verificarCaracteristicaUnidadeFuncional(
						grade.getAacUnidFuncionalSala().getId().getUnfSeq(),
						ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);

		if (emergencia) {
			AghParametros pHorasConsultaEmerg = this.getParametroFacade()
					.buscarAghParametro(
							AghuParametrosEnum.P_HORAS_ANT_CON_EMERG);
			if (pHorasConsultaEmerg != null) {
				horas = pHorasConsultaEmerg.getVlrNumerico().intValue();
			} else {
				horas = 5;
			}
		} else {
			AghParametros pHorasConsultaOutros = this.getParametroFacade()
					.buscarAghParametro(
							AghuParametrosEnum.P_HORAS_ANT_CON_OUTROS);
			if (pHorasConsultaOutros != null) {
				horas = pHorasConsultaOutros.getVlrNumerico().intValue();
			} else {
				horas = 24;
			}
		}

		if (consulta.getDthrInicio() == null && consulta.getDthrFim() != null) {
			// Data Início Atendimento não pode ser nula quando existe Data Fim
			AmbulatorioConsultasRNExceptionCode.AAC_00289.throwException();
		}

		Calendar dtConsultaCal = Calendar.getInstance();
		dtConsultaCal.setTime(consulta.getDtConsulta());
		dtConsultaCal.add(Calendar.HOUR_OF_DAY, -horas);

		Calendar dataAux = (Calendar) dtConsultaCal.clone();

		if (!emergencia) {
			dtConsultaCal.add(Calendar.HOUR_OF_DAY, -horas);
			dtConsultaCal = DateUtils.truncate(dtConsultaCal, Calendar.DATE);
			dataAux = (Calendar) dtConsultaCal.clone();
			Boolean diaUtil = false;
			while (!diaUtil) {
				if (dataAux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
						|| dataAux.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
						|| getAghuFacade().obterFeriado(dataAux.getTime()) != null) {
					dataAux.add(Calendar.DAY_OF_YEAR, -1);
				} else {
					diaUtil = true;
				}
			}
		}
		if (DateValidator.validaDataMenor(consulta.getDthrInicio(),
				dtConsultaCal.getTime())
				&& DateValidator.validaDataMenor(consulta.getDthrInicio(),
						dataAux.getTime())) {
			// Data de Início do Atendimento deve ser superior a Data/Hora da
			// Consulta;
			AmbulatorioConsultasRNExceptionCode.AAC_00284.throwException();
		}

		if (consulta.getDthrFim() != null
				&& DateValidator.validaDataMenor(consulta.getDthrFim(),
						consulta.getDthrInicio())) {
			// Data de Início do Atendimento deve ser inferior a Data/Hora de
			// Fim do Atendimento;
			AmbulatorioConsultasRNExceptionCode.AAC_00285.throwException();
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_VIN_FUN
	 * 
	 * @throws ApplicationBusinessException
	 *             , ApplicationBusinessException
	 */
	public void verificarVinculoFuncionario(RapServidores servidor,
			AacGradeAgendamenConsultas grade)
			throws ApplicationBusinessException {

		AghParametros pEspExDemissional = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_ESP_EX_DEMISSIONAL);

		// Só se não for exame demissional
		if (!grade.getEspecialidade().getSigla()
				.equals(pEspExDemissional.getVlrTexto())) {

			if (!(servidor.getDtFimVinculo() == null || (servidor
					.getDtFimVinculo() != null && servidor.getDtFimVinculo()
					.after(DateUtil.truncaData(new Date()))))) {

				AmbulatorioConsultasRNExceptionCode.AAC_00006.throwException();
			}
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_EXC_ATD
	 */
	public void verificarExclusaoAtendimento(Integer consultaNumero)
			throws BaseException {
		AghAtendimentos atendimento = null;

		List<AghAtendimentos> listaAtendimentos = getAghuFacade()
				.listarAtendimentosPorNumeroConsulta(consultaNumero);
		if (listaAtendimentos != null && !listaAtendimentos.isEmpty()) {
			atendimento = listaAtendimentos.get(0);
		}

		if (atendimento == null) {
			throw new ApplicationBusinessException(
					AmbulatorioConsultasRNExceptionCode.AAC_00104);
		} else if (atendimento.getHospitalDia() != null
				|| atendimento.getInternacao() != null
				|| atendimento.getAtendimentoUrgencia() != null
				|| atendimento.getDcaBolNumero() != null
				|| atendimento.getAtendimentoPacienteExterno() != null) {
			// O atendimento referente a esta consulta já está em andamento
			throw new ApplicationBusinessException(
					AmbulatorioConsultasRNExceptionCode.AAC_00125);
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_MIN_DT
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarMinimaData(Date dthrConsulta, String tpsTipo)
			throws ApplicationBusinessException {

		AghParametros parametro = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_QTD_DIAS_VOLTA_INT);
		Integer vQtdDias = parametro.getVlrNumerico().intValue();
		Calendar dataAtual = Calendar.getInstance();
		Date dataFormatada = dataAtual.getTime();
		DateUtil.adicionaDias(dataFormatada, vQtdDias);

		AghParametros parametro2 = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TP_CONS_UR);

		if (tpsTipo != null && tpsTipo.equals(parametro2.getVlrTexto())) {
			if (DateValidator.validaDataMenorIgual(
					DateUtil.truncaData(dthrConsulta),
					DateUtil.truncaData(dataFormatada))) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.AAC_00384);
			}
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_ESTAT
	 */
	public void verificarEstatisticas(Date dtConsulta)
			throws ApplicationBusinessException {

		Calendar dtConsultaCal = Calendar.getInstance();
		dtConsultaCal.setTime(dtConsulta);

		if (DateUtil.truncaData(dtConsulta).before(
				DateUtil.truncaData(new Date()))) {
			FatCompetencia competencia = getFaturamentoFacade()
					.obterCompetenciaModuloMesAno(DominioModuloCompetencia.AMB,
							dtConsultaCal.get(Calendar.MONTH) + 1,
							dtConsultaCal.get(Calendar.YEAR));

			// Não efetuar alterações em consultas após encerramento da
			// estatística
			if (competencia == null) {
				AmbulatorioConsultasRNExceptionCode.AAC_00535.throwException();
			} else if (competencia.getDthrEncerraEstatistica() != null) {
				AmbulatorioConsultasRNExceptionCode.AAC_00535.throwException();
			}
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONC_ATU_MICRO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public String obterMicro(String nomeMicrocomputador) throws ApplicationBusinessException {
		if(nomeMicrocomputador == null){
			throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.ERRO_OBTER_NOME_MICRO);
		}
		return nomeMicrocomputador;
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONC_ATU_ATD
	 */
	public Integer atualizarAtendimento(AacRetornos retorno,
			Integer oldAtendimentoSeq) throws ApplicationBusinessException {
		// alterar atd_seq conforme retorno
		Integer oldAtdSeq = null;

		if (oldAtendimentoSeq != null
				&& retorno != null
				&& (retorno.getAbsenteismo() == null || DominioIndAbsenteismo.R.equals(retorno.getAbsenteismo()))) {
			// estornando uma consulta programada ou retorno é nulo
			oldAtdSeq = oldAtendimentoSeq;
		}
		return oldAtdSeq;
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_DT_FUTUR
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarDataFuturo(Date dtConsulta)
			throws ApplicationBusinessException {

		Integer meses = null;
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_MESES_CONS_FUTURA);
		if (parametro != null) {
			meses = parametro.getVlrNumerico().intValue();
		}
		Date dtReferencia = new Date();
		dtReferencia = DateUtil.adicionaMeses(dtReferencia, meses);
		if (DateUtil.validaDataMaior(dtConsulta, dtReferencia)) {
			throw new ApplicationBusinessException(
					AmbulatorioConsultasRNExceptionCode.AAC_00611);
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_MIN_COMP
	 */
	public void verificarDataMinCompetencia(Integer consultaNumero)
			throws ApplicationBusinessException {

		List<FatCompetencia> listaCompetencias = this.getFaturamentoFacade()
				.pesquisarCompetenciasPorModulo(DominioModuloCompetencia.AMB);
		Date menorData = null;
		Date dataConsulta = null;
		if (listaCompetencias != null) {
			if (listaCompetencias.size() > 0) {
				menorData = ((FatCompetencia) listaCompetencias.get(0)).getId()
						.getDtHrInicio();
			}
		}
		for (FatCompetencia competencia : listaCompetencias) {
			if (competencia != null
					&& DateValidator.validaDataMenor(competencia.getId()
							.getDtHrInicio(), menorData)) {
				menorData = competencia.getId().getDtHrInicio();
			}
		}
		AacConsultas consulta = this.getAacConsultasDAO().obterConsulta(
				consultaNumero);
		if (consulta != null) {
			dataConsulta = consulta.getDtConsulta();
		}
		if (dataConsulta == null) {
			throw new ApplicationBusinessException(
					AmbulatorioConsultasRNExceptionCode.ERRO_CONSULTA_NAO_ENCONTRADA);
		} else if (DateValidator.validaDataMenor(dataConsulta, menorData)) {
			throw new ApplicationBusinessException(
					AmbulatorioConsultasRNExceptionCode.ERRO_DATA_CONSULTA_MENOR_COMPETENCIA);
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_PROJETO
	 * 
	 * Se pagado não for Pesquisa então pjq_seq deve ser nulo.
	 * 
	 * @throws ApplicationBusinessException
	 *             , ApplicationBusinessException
	 */
	public void verificarProjeto(AelProjetoPesquisas projeto, AacPagador pagador)
			throws ApplicationBusinessException {

		if (projeto != null) {
			Integer pgdSeqPesquisa = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_PGD_SEQ_PESQUISA)
					.getVlrNumerico().intValue();

			if (!projeto.getSeq().equals(pgdSeqPesquisa)) {
				AmbulatorioConsultasRNExceptionCode.AAC_00728.throwException();
				// Qdo Projeto está informado, o Pagador deve ser Pesquisa!
			}
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_SAL_PROJ
	 */
	public void verificarSaldoProjeto(Integer pjqSeq)throws ApplicationBusinessException {
		Long saldo;
		Long qtdeCon = 0L;
		Integer nroConsultasLib = 0;
		Boolean indVoucherEletronico = Boolean.TRUE;
		if (pjqSeq != null) {
			AelProjetoPesquisas projetoPesquisa = getAelProjetoPesquisasDAO().pesquisarNrConsultaLibVoucher(pjqSeq);
			if (projetoPesquisa != null) {
				nroConsultasLib = projetoPesquisa.getNroConsultasLib();
				indVoucherEletronico = projetoPesquisa.getVoucherEletronico();
				if (indVoucherEletronico == Boolean.TRUE) {
					qtdeCon = aacConsultasDAO.pesquisaConsultasMarcadasAtivasSemBloqueio(pjqSeq);
					saldo = nroConsultasLib - qtdeCon;
					if (saldo < 0L) {
						// NAO é permitido marcar consulta p Projeto de pesquisa quando saldo do projeto é menor ou igual a zero
						throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.AAC_00730);
	}
				}
			}
		}
	}

	/**
	 * ORADB: AACK_CON_2_RN.RN_CONP_VER_CNV_PROJ
	 * 
	 * Se existir convênio no projeto(ael_projeto_pesquisas), o convênio da
	 * consulta deve ser o mesmmo do projeto.
	 */
	public void verificarConvenioSaudeProjeto(AelProjetoPesquisas pesquisa,
			FatConvenioSaudePlano convenioSaudePlano)
			throws ApplicationBusinessException {

		if ((pesquisa != null && pesquisa.getDtInicio() == null)
				|| (pesquisa != null && !DateUtil.truncaData(new Date())
						.before(DateUtil.truncaData(pesquisa.getDtInicio())))
				&& ((pesquisa != null && pesquisa.getDtFim() == null) || (pesquisa != null && !DateUtil
						.truncaData(new Date()).after(
								DateUtil.truncaData(pesquisa.getDtFim()))))
				&& (pesquisa != null
						&& pesquisa.getSituacao() != null
						&& pesquisa.getSituacao().equals(
								DominioSituacaoProjetoPesquisa.APROVADO)
						|| (pesquisa != null && pesquisa.getSituacao() != null && pesquisa
								.getSituacao()
								.equals(DominioSituacaoProjetoPesquisa.REAPROVADO)) || (pesquisa != null
						&& pesquisa.getSituacao() != null && pesquisa
						.getSituacao()
						.equals(DominioSituacaoProjetoPesquisa.APROVADO_RES_340_2004)))) {

			if (pesquisa != null
					&& pesquisa.getConvenioSaudePlano() != null
					&& convenioSaudePlano != null
					&& !pesquisa.getConvenioSaudePlano().getId()
							.equals(convenioSaudePlano.getId())) {
				AmbulatorioConsultasRNExceptionCode.AEL_02284
						.throwException(pesquisa.getConvenioSaudePlano()
								.getDescricaoPlanoConvenio());
				// Convênio e Plano da consulta devem ser o mesmo do cadastro de
				// projetos de pesquisa!
				// Limpe o Projeto OU troque o Convenio para #1
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_PRNT_VIR
	 */
	public Integer atualizarProntuarioVirtual(Integer numero, Integer codigo, String nomeMicroComputador)
			throws ApplicationBusinessException {
		AacConsultas consulta = this.getAacConsultasDAO()
				.obterPorChavePrimaria(numero);
		Short seqEspecialidade = consulta.getGradeAgendamenConsulta()
				.getEspecialidade().getSeq();
		AghCaractEspecialidadesId caractEspecialidadesId = new AghCaractEspecialidadesId();
		caractEspecialidadesId.setEspSeq(seqEspecialidade);
		caractEspecialidadesId
				.setCaracteristica(DominioCaracEspecialidade.GERA_PRONTUARIO_VIRTUAL);
		AghCaractEspecialidades caracteristicaEspecialidade = this
				.getAghuFacade()
				.obterCaracteristicaEspecialidadePorChavePrimaria(
						caractEspecialidadesId);
		if (caracteristicaEspecialidade != null) {
			return this.getAmbulatorioTriagemRN().inserirPontuarioVirtual(
					codigo, nomeMicroComputador);
		}
		return null;
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_CNV_PGD2
	 * 
	 * Obter parâmetro pagador sem cobertura. Se parâmetro pagador = parâmetro
	 * sem cobertura, convênio não deve ser informado deve estar nulo (trocar
	 * p_pgd_ufrgs para p_pgd_scobertura. Alterar tela aacf_marca_func_ac ler
	 * cursor passando convenio parâmetro se pagador selecionado = pagador da
	 * consulta, Ok.
	 * 
	 * @throws ApplicationBusinessException
	 *             , ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void verificarConvenioSaudeProjeto2(AacConsultas consulta)
			throws ApplicationBusinessException {

		Integer pgdSemCobertura = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_PGD_SCOBERTURA)
				.getVlrNumerico().intValue();
		Integer tagHcpa = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TAG_HCPA)
				.getVlrNumerico().intValue();
		Integer tagUfrgs = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TAG_UFRGS)
				.getVlrNumerico().intValue();
		Integer planoHcpa = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_PLANO_HCPA)
				.getVlrNumerico().intValue();
		Integer planoUfrgs = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_PLANO_UFRGS)
				.getVlrNumerico().intValue();
		Integer planoAmbu = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO)
				.getVlrNumerico().intValue();
		Integer pgdSus = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS)
				.getVlrNumerico().intValue();
		Integer unfSmo = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_UNIDADE_SMO)
				.getVlrNumerico().intValue();
		Integer codigoPlanoAmbulatorial = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_PLANO_AMBULATORIAL)
				.getVlrNumerico().intValue();

		Short unfGrade = null;
		if (consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala() != null) {
			unfGrade = consulta.getGradeAgendamenConsulta()
					.getAacUnidFuncionalSala().getId().getUnfSeq();
		}
		Short pagador = null;
		if (consulta.getConvenioSaudePlano() != null
				&& consulta.getConvenioSaudePlano().getConvenioSaude() != null
				&& consulta.getConvenioSaudePlano().getConvenioSaude()
						.getPagador() != null) {
			pagador = consulta.getConvenioSaudePlano().getConvenioSaude()
					.getPagador().getSeq();
		}

		if (consulta.getPagador() != null
				&& consulta.getPagador().getSeq().equals(pgdSemCobertura)) {
			if (consulta.getConvenioSaudePlano() == null
					|| (consulta.getConvenioSaudePlano() != null && !consulta
							.getConvenioSaudePlano().getId().getCnvCodigo()
							.equals(codigoPlanoAmbulatorial))) {
				AmbulatorioConsultasRNExceptionCode.AAC_00647.throwException();
				// para pagador 'sem cobertura' convênio não deve ser informado
			}
		} else { // pagador
			if (consulta.getPagador() != null && pagador != null
					&& !consulta.getPagador().getSeq().equals(pagador)) {
				AmbulatorioConsultasRNExceptionCode.AAC_00648.throwException();
				// pagador não confere com o pagador do convênio selecionado
			}
			if (consulta.getPagador() != null
					&& consulta.getPagador().getSeq().equals(pgdSus)) {
				if (consulta.getTipoAgendamento() != null
						&& consulta.getTipoAgendamento().getSeq()
								.equals(tagHcpa)
						&& consulta.getConvenioSaudePlano() != null
						&& consulta.getConvenioSaudePlano().getId().getSeq()
								.equals(planoHcpa) && unfGrade != null
						&& unfGrade.equals(unfSmo)) {

					AmbulatorioConsultasRNExceptionCode.AAC_00650
							.throwException();
					// Para autorização hcpa, plano hcpa
				}
				if (consulta.getTipoAgendamento() != null
						&& consulta.getTipoAgendamento().getSeq()
								.equals(tagUfrgs)
						&& consulta.getConvenioSaudePlano() != null
						&& !consulta.getConvenioSaudePlano().getId().getSeq()
								.equals(planoHcpa)
						&& !consulta.getConvenioSaudePlano().getId().getSeq()
								.equals(planoUfrgs)) {

					AmbulatorioConsultasRNExceptionCode.AAC_00666
							.throwException();
					// Para autorização ufrgs,plano ufrgs ou hcpa
				}
				if (consulta.getTipoAgendamento() != null
						&& !consulta.getTipoAgendamento().getSeq()
								.equals(tagHcpa)
						&& !consulta.getTipoAgendamento().getSeq()
								.equals(tagUfrgs)
						&& consulta.getConvenioSaudePlano() != null
						&& consulta.getConvenioSaudePlano().equals(planoAmbu)) {

					AmbulatorioConsultasRNExceptionCode.AAC_00667
							.throwException();
					// Para autorização sms,plano não deve ser ufrgs ou hcpa
				}
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_EXCLU_AC
	 */
	public void verificarExclusaoConsulta(AacConsultas consulta)
			throws ApplicationBusinessException {
		if (consulta.getSituacaoConsulta() != null
				&& consulta.getSituacaoConsulta().getSituacao() != null
				&& consulta.getSituacaoConsulta().getSituacao().equals("M")) {

			if (consulta.getExcedeProgramacao()==null || !consulta.getExcedeProgramacao()) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.AAC_00102);
			}
		} else if (consulta.getSituacaoConsulta() != null
				&& consulta.getSituacaoConsulta().getSituacao() != null
				&& !consulta.getSituacaoConsulta().getSituacao().equals("G")) {
			// Mensagem com melhoria (não foi utilizada a AAC-00103 do AGH)
			throw new ApplicationBusinessException(
					AmbulatorioConsultasRNExceptionCode.MSG_EXCLUSAO_CONSULTA_SITUACAO_NAO_GERADA);
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_CON_NRO
	 * 
	 * @throws NumberFormatException
	 */
	public AacConsultas atualizarConsultaNumero(Integer consultaNumero,
			Integer consultaNumeroRelacao) throws NumberFormatException {
		AacConsultas consulta = this.getAacConsultasDAO().obterConsulta(
				consultaNumero);
		AacConsultas consultaAnterior = null;
		if (consultaNumeroRelacao != null) {
			consultaAnterior = this.getAacConsultasDAO().obterConsulta(
					consultaNumeroRelacao);
		}
		consulta.setConsulta(consultaAnterior);
		return consulta;
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_COD_CENTRAL
	 */
	public AacConsultas atualizarCodigoCentral(Long codCentral,
			AacConsultas consulta, Integer nroConsultaAnterior) {
		consulta.setCodCentral(codCentral);
		return consulta;
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_FORMA
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarFormaAgendamento(
			AacSituacaoConsultas situacaoConsulta, Short fagCaaSeq,
			Short fagTagSeq, Short fagPgdSeq)
			throws ApplicationBusinessException {

		AacFormaAgendamentoId formaAgendId = new AacFormaAgendamentoId(
				fagCaaSeq, fagTagSeq, fagPgdSeq);
		AacFormaAgendamento aacFormaAgendamento = getFormaAgendamentoDAO()
				.obterPorChavePrimaria(formaAgendId);

		if (situacaoConsulta.getSituacao().equals("M")) {
			// v_qtd < 1 conforme procedure AACK_CON_3_RN.RN_CONP_VER_FORMA
			if (aacFormaAgendamento == null) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.AAC_00620);
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_SENHA
	 * @throws BaseException 
	 * 
	 * @throws ApplicationBusinessException
	 *             , ApplicationBusinessException
	 */
	public void verificarSenha(AacConsultas consulta)
			throws BaseException {
		if (consulta.getSituacaoConsulta() != null
				&& consulta.getSituacaoConsulta().getSituacao() != null
				&& consulta.getSituacaoConsulta().getSituacao().equals("M")) {
			// Se não tiver perfil especial permite marcar a consulta sem
			// validar regras.
			if (!(consulta.getFormaAgendamento().getPerfilEspecial() != null && verificarPerfil(consulta
					.getFormaAgendamento().getPerfilEspecial()))) {
				if (consulta.getFormaAgendamento().getSenhaAutoriza()
						&& consulta.getCodCentral() == null) {
					AmbulatorioConsultasRNExceptionCode.AAC_00621
							.throwExceptionRollback();
				}
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_SIT_NEW
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarSituacaoNovo(AacConsultas consulta, String oldSituacao)
			throws ApplicationBusinessException {

		if (consulta.getSituacaoConsulta() != null && 
				!consulta.getSituacaoConsulta().getSituacao().equals(oldSituacao) && oldSituacao != null) {
			if (consulta.getSituacaoConsulta().getSituacao().equals("G")
					&& (oldSituacao == null || !oldSituacao.equals("L"))) {
				AmbulatorioConsultasRNExceptionCode.AAC_00097.throwException();
			}
		}
		if (consulta.getSituacaoConsulta() != null
				&& consulta.getSituacaoConsulta().getSituacao().equals("M")) {
			if (consulta.getFormaAgendamento() == null) {
				AmbulatorioConsultasRNExceptionCode.AAC_00645.throwException();
			}
			if (consulta.getConvenioSaudePlano() == null || consulta.getPaciente() == null) {
				AmbulatorioConsultasRNExceptionCode.AAC_00646.throwException();
			}
			if (consulta.getRetorno() == null) {
				AmbulatorioConsultasRNExceptionCode.MENSAGEM_ERRO_RETORNO.throwException();
			}
		}
		
		char[] situacoesInvalidas = { 'M', 'R' };
		if ((consulta.getSituacaoConsulta() != null && !StringUtils.containsAny(consulta.getSituacaoConsulta().getSituacao(), situacoesInvalidas))
				&& (consulta.getConvenioSaudePlano() != null 
				|| consulta.getPaciente() != null 
				|| consulta.getRetorno() != null 
				|| consulta.getMotivo() != null)) {
			AmbulatorioConsultasRNExceptionCode.AAC_00096.throwException();
		}
		
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_ANTERIOR
	 * @throws BaseException 
	 */
	// TODO RODRIGO Trechos de codigo comentados devem ser implementados quando chamados
	// por estórias futuras e forem necessários
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	public Integer verificarConsultaAnterior(AacConsultas consulta)
			throws BaseException {
		Integer tempoAnterior = 0;
		Date dataLimite = null;
		Integer vNumeroConsulta = null;
		Short cnvCodigo = null;
		Byte maxFaltas = null;
		Short espSeq = null;
		Short espSeqGen = null;
		Integer serMatriculaCon = null;
		Short serVinCodigoCon = null;
		DominioFuncionario indFuncionario = consulta.getIndFuncionario();
		if (consulta.getServidorConsultado() != null
				&& consulta.getServidorConsultado().getId() != null) {
			serMatriculaCon = consulta.getServidorConsultado().getId()
					.getMatricula();
			serVinCodigoCon = consulta.getServidorConsultado().getId()
					.getVinCodigo();

		}
		if (consulta.getConvenioSaudePlano() != null
				&& consulta.getConvenioSaudePlano().getId() != null) {
			cnvCodigo = consulta.getConvenioSaudePlano().getId().getCnvCodigo();
		}

		String siglaEspExameRhA = null;
		String siglaEspExameRhP = null;
		String siglaEspExameRhD = null;
		Integer vClinica = null;
		Long quantidade = null;
		if (consulta != null
				&& consulta.getGradeAgendamenConsulta() != null
				&& consulta.getGradeAgendamenConsulta().getEspecialidade() != null
				&& consulta.getGradeAgendamenConsulta().getEspecialidade()
						.getClinica() != null) {
			vClinica = consulta.getGradeAgendamenConsulta().getEspecialidade()
					.getClinica().getCodigo();
			maxFaltas = consulta.getGradeAgendamenConsulta().getEspecialidade()
					.getMaxQuantFaltas();
			espSeq = consulta.getGradeAgendamenConsulta().getEspecialidade()
					.getSeq();
		}
		if (consulta != null
				&& consulta.getGradeAgendamenConsulta() != null
				&& consulta.getGradeAgendamenConsulta().getEspecialidade() != null
				&& consulta.getGradeAgendamenConsulta().getEspecialidade()
						.getEspecialidade() != null) {
			espSeqGen = consulta.getGradeAgendamenConsulta().getEspecialidade()
					.getEspecialidade().getSeq();
		} else if (consulta != null
				&& consulta.getGradeAgendamenConsulta() != null
				&& consulta.getGradeAgendamenConsulta().getEspecialidade() != null
				&& consulta.getGradeAgendamenConsulta().getEspecialidade()
						.getEspecialidade() == null) {
			espSeqGen = consulta.getGradeAgendamenConsulta().getEspecialidade()
					.getSeq();
		}

		if (consulta.getSituacaoConsulta().getSituacao().equals("M")) {
			AghEspecialidades especialidade = consulta
					.getGradeAgendamenConsulta().getEspecialidade();

			AacGradeAgendamenConsultas gradeAgendamento = consulta
					.getGradeAgendamenConsulta();
			AacCondicaoAtendimento condicaoAtendimento = this
					.getAacCondicaoAtendimentoDAO().obterPorChavePrimaria(
							consulta.getFormaAgendamento().getId().getCaaSeq());
			AacFormaAgendamento formaAgendamento = consulta
					.getFormaAgendamento();
			AipPacientes paciente = consulta.getPaciente();
			Integer pacCodigo = null;
			if (paciente != null) {
				pacCodigo = paciente.getCodigo();
			}

			Short caaSeq = null;
			Short tagSeq = null;
			Short pgdSeq = null;
			if (formaAgendamento != null && formaAgendamento.getId() != null) {
				caaSeq = formaAgendamento.getId().getCaaSeq();
				tagSeq = formaAgendamento.getId().getTagSeq();
				pgdSeq = formaAgendamento.getId().getPgdSeq();
			}
			Boolean bloqueioAlta = false;
			DominioRetornoAgenda tipoRetornoAgenda;
			String bloqueio;
			Integer vAlta;
			String vDoutor;
			Short unfSeq = null;
			Integer contaFalta = 0;
			if (gradeAgendamento.getAacUnidFuncionalSala() != null
					&& gradeAgendamento.getAacUnidFuncionalSala().getId() != null) {
				unfSeq = gradeAgendamento.getAacUnidFuncionalSala().getId()
						.getUnfSeq();
			}
			Integer grdSeq = null;
			if (gradeAgendamento != null) {
				grdSeq = gradeAgendamento.getSeq();
			}

			if (gradeAgendamento.getTempoAtendAnterior() != null) {
				tempoAnterior = gradeAgendamento.getTempoAtendAnterior();
			} else if (formaAgendamento.getTempoAnterior() != null) {
				tempoAnterior = formaAgendamento.getTempoAnterior();
			}
			if (tempoAnterior != null && tempoAnterior.intValue() == 9999) {
				dataLimite = AmbulatorioConsultaRN.PRMEIRO_MAIO_2000;
			} else if (consulta.getDthrMarcacao() != null) {
				dataLimite = DateUtils.addDays(
						DateUtil.truncaData(consulta.getDthrMarcacao()),
						-tempoAnterior);
			}
			if (consulta.getFormaAgendamento().getExigeProntuario()
					&& consulta.getCodCentral() == null
					&& (paciente.getProntuario() == null || paciente
							.getProntuario() > VALOR_MAXIMO_PRONTUARIO)) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.AAC_00629);
			}
			AghParametros parametroConvenioSus = this.getParametroFacade()
					.buscarAghParametro(
							AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
			if (parametroConvenioSus.getVlrTexto() != null) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.AAC_00197);
			}
			Short vConvenioSusPadrao = Short.valueOf(parametroConvenioSus
					.getVlrNumerico().toString());
			AghParametros parametroCaaReconsulta = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_CAA_RECONSULTA);
			if (parametroCaaReconsulta.getVlrTexto() != null) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.AAC_00197);
			}
			Short vCaaReconsulta = Short.valueOf(parametroCaaReconsulta
					.getVlrNumerico().toString());
			AghParametros parametroCaaPosAlta = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_CAA_POSALTA);
			if (parametroCaaPosAlta.getVlrTexto() != null) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.AAC_00197);
			}
			Integer vCaaPosAlta = Integer.valueOf(parametroCaaPosAlta
					.getVlrNumerico().toString());

			AghParametros parametroRetRefDevolvida = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_RET_REF_DEVOLVIDA);
			if (parametroRetRefDevolvida.getVlrTexto() != null) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.AAC_00671);
			}
			Integer vRetRefDevolvida = Integer.valueOf(parametroRetRefDevolvida
					.getVlrNumerico().toString());
			if (formaAgendamento.getPerfilEspecial() != null
					&& this.verificarPerfil(formaAgendamento
							.getPerfilEspecial())) {
				return null;
			}
			AghParametros parametroBloqueioAlta = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_BLOQUEIO_ALTA);
			if (parametroBloqueioAlta.getVlrTexto() != null) {
				bloqueioAlta = false;
			}
			if (bloqueioAlta) {
				if (consulta.getFormaAgendamento().getId().getCaaSeq() == vCaaReconsulta) {
					tipoRetornoAgenda = this.getAmbulatorioAltaRN()
							.verificarTipoRetornoAgenda(paciente.getCodigo(),
									especialidade.getSeq());
					if (tipoRetornoAgenda == null) {
						bloqueio = "X";
					} else {
						bloqueio = String
								.valueOf(tipoRetornoAgenda.getCodigo());
					}
					if (bloqueio.equals(DominioRetornoAgenda.B)) {
						vAlta = this.getAmbulatorioAltaRN()
								.verificarAltaPaciente(paciente.getCodigo(),
										especialidade.getSeq());
						vDoutor = this.getAmbulatorioAltaRN()
								.obterNomeProfessor(vAlta);
						throw new ApplicationBusinessException(
								MarcacaoConsultaRNExceptionCode.MAM_04009,
								especialidade.getSigla(), vDoutor);
					}
				}
			}
			 if (condicaoAtendimento.getCriticaApac().equals(true)
			 && cnvCodigo == vConvenioSusPadrao
			 && DominioSimNao.S.equals(aghCaractUnidFuncionaisDAO.
					 verificarCaracteristicaUnidadeFuncional(unfSeq,ConstanteAghCaractUnidFuncionais.CRITICA_APAC_SISTEMA))){
				 
				 Byte vlrNumerico=null;
				 AghParametros parametros;
				 if(liberarConsultasON.verificaCaracteristicaGrade(grdSeq, DominioCaracteristicaGrade.AGENDA_PRESCRICAO_QUIMIO.toString())){
					 parametros = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_TIPO_TRATAMENTO_QUIMIOTERAPIA);
					 if(parametros!= null){
						 vlrNumerico = Byte.valueOf(parametros.getVlrNumerico().toString());
						 faturamentoFacade.verificaApacAutorizacao(pacCodigo, consulta.getDtConsulta(),vlrNumerico);
					 }
				 }
				 if(aghCaractUnidFuncionaisDAO.verificarCaracteristicaUnidadeFuncional(unfSeq,ConstanteAghCaractUnidFuncionais.HEMODIALISE)){
					 parametros = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_TIPO_TRATAMENTO_HEMODIALISE);
					 if(parametros!= null){
						 vlrNumerico = Byte.valueOf(parametros.getVlrNumerico().toString());
						 faturamentoFacade.verificaApacAutorizacao(pacCodigo, consulta.getDtConsulta(), vlrNumerico);
					 }
				 }
			 }

			if (formaAgendamento.getExigeGrdFamiliar()) {
				if (this.verificarCaracteristicaGrade(grdSeq,
						DominioCaracteristicaGrade.ATENDIMENTO_FAMILIAR)) {
					return null;
				} else {
					throw new ApplicationBusinessException(
							AmbulatorioConsultasRNExceptionCode.AAC_ERRO_GRADE_FAMLIAR);
				}
			}
			if (formaAgendamento.getExigeSmo()
					&& DominioSimNao.S.equals(getAghuFacade()
							.validarCaracteristicaDaUnidadeFuncional(unfSeq,
									ConstanteAghCaractUnidFuncionais.SMO))) {
				return null;
			}
			if (formaAgendamento.getMesmaGrade()) {
				// TODO chamar cur_con4
				List<AacConsultas> listaConsultas = this.getAacConsultasDAO()
						.pesquisarConsultaAnteriorMesmaGrade(pacCodigo,
								consulta.getDtConsulta(), grdSeq, caaSeq,
								tagSeq, pgdSeq);
				if (listaConsultas == null || listaConsultas.size() == 0) {
					List<AacConsultas> listaConsultasAux = this
							.getAacConsultasDAO()
							.pesquisarConsultaAnteriorMesmaGradeSemNivelBusca(
									pacCodigo, consulta.getDtConsulta(), grdSeq);
					if (listaConsultasAux == null
							|| listaConsultasAux.size() == 0) {
						throw new ApplicationBusinessException(
								AmbulatorioConsultasRNExceptionCode.AAC_00636);
					}
				}
				return null;
			}

			if (formaAgendamento != null
					&& formaAgendamento.getExameRh() != null
					&& !formaAgendamento.getExameRh().equals(
							DominioIndExameRH.N)) {
				if (formaAgendamento.getExameRh().equals(DominioIndExameRH.A)) {
					AghParametros parametroEspExameAdmissional = this
							.getParametroFacade().buscarAghParametro(
									AghuParametrosEnum.P_ESP_EX_ADMISSIONAL);
					if (parametroEspExameAdmissional != null) {
						siglaEspExameRhA = parametroEspExameAdmissional
								.getVlrTexto();
					}
				}
				if (formaAgendamento.getExameRh().equals(DominioIndExameRH.P)) {
					AghParametros parametroEspExamePeriodico = this
							.getParametroFacade().buscarAghParametro(
									AghuParametrosEnum.P_ESP_EX_PERIODICO);
					if (parametroEspExamePeriodico != null) {
						siglaEspExameRhP = parametroEspExamePeriodico
								.getVlrTexto();
					}
				}
				if (formaAgendamento.getExameRh().equals(DominioIndExameRH.D)) {
					AghParametros parametroEspExameDemissional = this
							.getParametroFacade().buscarAghParametro(
									AghuParametrosEnum.P_ESP_EX_DEMISSIONAL);
					if (parametroEspExameDemissional != null) {
						siglaEspExameRhD = parametroEspExameDemissional
								.getVlrTexto();
					}
				}
				if (formaAgendamento.getExameRh().equals(DominioIndExameRH.A)
						&& especialidade.getSigla() != siglaEspExameRhA) {
					throw new ApplicationBusinessException(
							AmbulatorioConsultasRNExceptionCode.AAC_00641);
				}
				if (formaAgendamento.getExameRh().equals(DominioIndExameRH.P)
						&& especialidade.getSigla() != siglaEspExameRhP) {
					throw new ApplicationBusinessException(
							AmbulatorioConsultasRNExceptionCode.AAC_00642);
				}
				if (formaAgendamento.getExameRh().equals(DominioIndExameRH.D)
						&& especialidade.getSigla() != siglaEspExameRhD) {
					throw new ApplicationBusinessException(
							AmbulatorioConsultasRNExceptionCode.AAC_00643);
				}
				if (formaAgendamento.getExameRh().equals(DominioIndExameRH.M)
						&& especialidade.getSigla() != siglaEspExameRhA
						&& especialidade.getSigla() != siglaEspExameRhP) {
					throw new ApplicationBusinessException(
							AmbulatorioConsultasRNExceptionCode.AAC_00644);
				}
			}

			if (formaAgendamento != null
					&& formaAgendamento.getAtendimentoAnterior() != null
					&& (formaAgendamento.getAtendimentoAnterior().equals(
							DominioIndAtendimentoAnterior.C) || formaAgendamento
							.getAtendimentoAnterior().equals(
									DominioIndAtendimentoAnterior.A))) {
				if (consulta.getCodCentral() != null) {
					return null;
				}

				List<AacNivelBusca> listaNivelBusca = this
						.getAacNivelBuscaDAO()
						.pesquisarNivelBuscaPorFormaAgendamento(
								formaAgendamento);
				if (listaNivelBusca == null || listaNivelBusca.size() == 0) {
					throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.AAC_00635);
				}
				if (formaAgendamento == null
						|| formaAgendamento.getTempoAnterior() == null) {
					throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.AAC_00639);
				}
				if (condicaoAtendimento != null
						&& DominioConsultaGenerica.M.equals(condicaoAtendimento.getIndGenericaCon())) {

					if (cnvCodigo.equals(vConvenioSusPadrao)
							&& caaSeq.equals(vCaaReconsulta)
							&& maxFaltas != null) {
						contaFalta = 0;
						List<AacConsultas> listaConsultas1 = this
								.getAacConsultasDAO()
								.pesquisarConsultaAnteriorMesmaEspecialidade(
										pacCodigo, consulta.getDtConsulta(),
										dataLimite, caaSeq, tagSeq, pgdSeq);

						// filtro para especialidade generica ou especialidade
						for (Iterator<AacConsultas> iterator = listaConsultas1
								.iterator(); iterator.hasNext();) {
							AacConsultas consultaAux1 = (AacConsultas) iterator
									.next();
							if (consultaAux1.getGradeAgendamenConsulta() != null
									&& consultaAux1.getGradeAgendamenConsulta() != null
									&& consultaAux1.getGradeAgendamenConsulta()
											.getEspecialidade() != null) {
								Short especialidadeSeq = null;
								AghEspecialidades especialidadeAux1 = consultaAux1
										.getGradeAgendamenConsulta()
										.getEspecialidade();
								if (especialidadeAux1.getEspecialidade() != null) {
									especialidadeSeq = especialidadeAux1
											.getEspecialidade().getSeq();
								} else {
									especialidadeSeq = especialidadeAux1
											.getSeq();
								}
								if (especialidadeSeq.shortValue() != espSeqGen.shortValue()) {
									iterator.remove();
								}
							}
						}

						for (AacConsultas consulta1 : listaConsultas1) {
							vNumeroConsulta = consulta1.getNumero();

							Integer retornoSeq1 = null;
							if (consulta1.getRetorno() != null) {
								retornoSeq1 = consulta1.getRetorno().getSeq();
							}
							Short espSeq1 = null;
							if (consulta1.getGradeAgendamenConsulta() != null
									&& consulta1.getGradeAgendamenConsulta()
											.getEspecialidade() != null) {
								espSeq1 = consulta1.getGradeAgendamenConsulta()
										.getEspecialidade().getSeq();
							}

							if (retornoSeq1.equals(vRetRefDevolvida)
									&& vNumeroConsulta != null
									&& espSeq.equals(espSeq1)) {
								throw new ApplicationBusinessException(
										AmbulatorioConsultasRNExceptionCode.AAC_00670);
							}

							DominioIndAbsenteismo absenteismo = null;
							if (consulta1.getRetorno() != null
									&& consulta1.getRetorno().getAbsenteismo() != null) {
								absenteismo = consulta1.getRetorno()
										.getAbsenteismo();
							}

							if (absenteismo.equals(DominioIndAbsenteismo.R)) {
								break;
							}

							if (espSeq == espSeq1
									&& absenteismo
											.equals(DominioIndAbsenteismo.P)) {
								contaFalta++;
							}
							if (contaFalta >= maxFaltas
									&& formaAgendamento != null
									&& (formaAgendamento
											.getAtendimentoAnterior() != null && formaAgendamento
											.getAtendimentoAnterior()
											.equals(DominioIndAtendimentoAnterior.C))) {
								vNumeroConsulta = null;
								throw new ApplicationBusinessException(
										AmbulatorioConsultasRNExceptionCode.AAC_00653,
										maxFaltas);
							}
						}

					} else {
						List<AacConsultas> listaConsultas1 = this
								.getAacConsultasDAO()
								.pesquisarConsultaAnteriorMesmaEspecialidade(
										pacCodigo, consulta.getDtConsulta(),
										dataLimite, caaSeq, tagSeq, pgdSeq);

						// filtro para especialidade generica ou especialidade
						for (Iterator<AacConsultas> iterator = listaConsultas1
								.iterator(); iterator.hasNext();) {
							AacConsultas consultaAux1 = (AacConsultas) iterator
									.next();
							if (consultaAux1.getGradeAgendamenConsulta() != null
									&& consultaAux1.getGradeAgendamenConsulta() != null
									&& consultaAux1.getGradeAgendamenConsulta()
											.getEspecialidade() != null) {
								Short especialidadeSeq = null;
								AghEspecialidades especialidadeAux1 = consultaAux1
										.getGradeAgendamenConsulta()
										.getEspecialidade();
								if (especialidadeAux1.getEspecialidade() != null) {
									especialidadeSeq = especialidadeAux1
											.getEspecialidade().getSeq();
								} else {
									especialidadeSeq = especialidadeAux1
											.getSeq();
								}
								if (CoreUtil.modificados(especialidadeSeq, espSeqGen)) {
									iterator.remove();
								}
							}
						}

						if (listaConsultas1 == null
								|| listaConsultas1.size() == 0
								&& formaAgendamento.getAtendimentoAnterior() != null
								&& formaAgendamento
										.getAtendimentoAnterior()
										.equals(DominioIndAtendimentoAnterior.C)) {

							if (serMatriculaCon != null
									&& (DominioFuncionario.F
											.equals(indFuncionario) || (indFuncionario == null))) {
								List<AacConsultas> lista = this
										.getAacConsultasDAO()
										.pesquisarConsultaAnteriorMesmaEspecialidadeServidor(
												pacCodigo,
												consulta.getDtConsulta(),
												dataLimite, caaSeq, tagSeq,
												pgdSeq, serMatriculaCon,
												serVinCodigoCon);

								for (Iterator<AacConsultas> iterator = listaConsultas1
										.iterator(); iterator.hasNext();) {
									AacConsultas consultaAux1 = (AacConsultas) iterator
											.next();
									if (consultaAux1
											.getGradeAgendamenConsulta() != null
											&& consultaAux1
													.getGradeAgendamenConsulta() != null
											&& consultaAux1
													.getGradeAgendamenConsulta()
													.getEspecialidade() != null) {
										Short especialidadeSeq = null;
										AghEspecialidades especialidadeAux1 = consultaAux1
												.getGradeAgendamenConsulta()
												.getEspecialidade();
										if (especialidadeAux1
												.getEspecialidade() != null) {
											especialidadeSeq = especialidadeAux1
													.getEspecialidade()
													.getSeq();
										} else {
											especialidadeSeq = especialidadeAux1
													.getSeq();
										}
										if (especialidadeSeq.shortValue() != espSeqGen.shortValue()) {
											iterator.remove();
										}
									}
								}

								if (lista == null || lista.size() == 0) {
									throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.AAC_00622);
								} else {
									vNumeroConsulta = lista.get(0).getNumero();
								}
							} else {
								throw new ApplicationBusinessException(
										AmbulatorioConsultasRNExceptionCode.AAC_00622);
							}
						}

						if (listaConsultas1.size() > 0) {
							AacConsultas consulta1 = listaConsultas1.get(0);
							if (consulta1 == null
									&& formaAgendamento != null
									&& (formaAgendamento
											.getAtendimentoAnterior() != null && formaAgendamento
											.getAtendimentoAnterior()
											.equals(DominioIndAtendimentoAnterior.C))) {
								vNumeroConsulta = null;
							}
							Integer retornoSeq1 = null;
							if (consulta1.getRetorno() != null) {
								retornoSeq1 = consulta1.getRetorno().getSeq();
							}
							Short espSeq1 = null;
							if (consulta1.getGradeAgendamenConsulta() != null
									&& consulta1.getGradeAgendamenConsulta()
											.getEspecialidade() != null) {
								espSeq1 = consulta1.getGradeAgendamenConsulta()
										.getEspecialidade().getSeq();
							}
							if (retornoSeq1.equals(vRetRefDevolvida)
									&& espSeq.equals(espSeq1)
									&& cnvCodigo.equals(vConvenioSusPadrao)
									&& caaSeq.equals(vCaaReconsulta)) {
								throw new ApplicationBusinessException(
										AmbulatorioConsultasRNExceptionCode.AAC_00670);
							}
							vNumeroConsulta = consulta1.getNumero();
						}
					}

					if (vNumeroConsulta == null
							&& (formaAgendamento != null && formaAgendamento
									.getAtendimentoAnterior().equals(
											DominioIndAtendimentoAnterior.C))) {
						throw new ApplicationBusinessException(
								AmbulatorioConsultasRNExceptionCode.AAC_00622);
					}
				}
				if (condicaoAtendimento != null
						&& DominioConsultaGenerica.D.equals(condicaoAtendimento
								.getIndGenericaCon())) {
					List<AacConsultas> listaConsultas = this
							.getAacConsultasDAO()
							.pesquisarConsultaAnteriorEspecialidadeDiferente(
									pacCodigo, consulta.getDtConsulta(),
									espSeqGen, dataLimite, caaSeq, tagSeq,
									pgdSeq);
					if (listaConsultas == null
							|| listaConsultas.size() == 0
							&& (formaAgendamento != null && formaAgendamento
									.getAtendimentoAnterior().equals(
											DominioIndAtendimentoAnterior.C))) {
						throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.AAC_00622);
					}

					vNumeroConsulta = listaConsultas.get(0).getNumero();

				}
				if (condicaoAtendimento != null
						&& DominioConsultaGenerica.I.equals(condicaoAtendimento
								.getIndGenericaCon())) {
					List<AacConsultas> listaConsultas = this
							.getAacConsultasDAO()
							.pesquisarConsultaIndependenteEspecialidadeAnterior(
									pacCodigo, consulta.getDtConsulta(),
									dataLimite, caaSeq, tagSeq, pgdSeq);
					if ((listaConsultas == null || listaConsultas.size() == 0)
							&& (formaAgendamento != null && formaAgendamento
									.getAtendimentoAnterior().equals(
											DominioIndAtendimentoAnterior.C))) {
						throw new ApplicationBusinessException(
								AmbulatorioConsultasRNExceptionCode.AAC_00622);
					}
					vNumeroConsulta = consulta.getNumero();
				}
			}

			if (formaAgendamento != null
					&& formaAgendamento.getAtendimentoAnterior() != null
					&& (formaAgendamento.getAtendimentoAnterior().equals(
							DominioIndAtendimentoAnterior.I) || formaAgendamento
							.getAtendimentoAnterior().equals(
									DominioIndAtendimentoAnterior.A))) {
				if (formaAgendamento.getAtendimentoAnterior().equals(
						DominioIndAtendimentoAnterior.A)
						&& vNumeroConsulta != null) {
					return null;
				}
				if (vClinica == 4) {
					List<AinInternacao> listaInternacao = this
							.getInternacaoFacade()
							.pesquisarInternacaoIndependenteEspecialidade(
									pacCodigo, consulta.getDtConsulta(),
									dataLimite, pgdSeq);
					if (listaInternacao == null || listaInternacao.size() == 0) {
						throw new ApplicationBusinessException(
								AmbulatorioConsultasRNExceptionCode.AAC_00658);
					}
					return null;
				}
				if (condicaoAtendimento != null
						&& DominioConsultaGenerica.M.equals(condicaoAtendimento
								.getIndGenericaInt())) {
					List<AinInternacao> listaInternacao1 = this
							.getInternacaoFacade()
							.pesquisarInternacaoMesmaEspecialidade(
									consulta.getPaciente().getCodigo(),
									consulta.getDtConsulta(), dataLimite,
									pgdSeq);
					for (AinInternacao internacao : listaInternacao1) {
						Boolean unidadeEmergencia = false;
						Boolean verificaEspecialidade = false;
						String leitoID = null;
						Short numeroQuarto = null;
						Short unidadeFuncionalSeq = null;
						if (internacao.getLeito() == null) {
							leitoID = null;
						} else {
							leitoID = internacao.getLeito().getLeitoID();
						}
						if (internacao.getQuarto() == null) {
							numeroQuarto = null;
						} else {
							numeroQuarto = internacao.getQuarto().getNumero();
						}

						if (internacao.getUnidadesFuncionais() == null) {
							unidadeFuncionalSeq = null;
						} else {
							unidadeFuncionalSeq = internacao
									.getUnidadesFuncionais().getSeq();
						}
						Short especialidadeSeq = null;
						Short especialidadeGenericaSeq = null;
						if (internacao.getEspecialidade() == null
								|| internacao.getEspecialidade()
										.getEspecialidade() == null) {
							especialidadeGenericaSeq = null;
						} else {
							especialidadeGenericaSeq = internacao
									.getEspecialidade().getEspecialidade()
									.getSeq();
						}
						if (internacao.getEspecialidade() == null) {
							especialidadeSeq = null;
						} else {
							especialidadeSeq = internacao.getEspecialidade()
									.getSeq();
						}

						unidadeEmergencia = this.verificarUnidadeEmergencia(
								leitoID, numeroQuarto, unidadeFuncionalSeq);
						verificaEspecialidade = this.verificarEspecialidade(
								especialidadeGenericaSeq, especialidadeSeq,
								espSeq);
						if (unidadeEmergencia == false
								&& verificaEspecialidade == false) {
							listaInternacao1.remove(internacao);
						}
					}
					if (listaInternacao1 == null
							|| listaInternacao1.size() == 0) {
						List<AinInternacao> listaInternacao2 = this
								.getInternacaoFacade()
								.pesquisarInternacaoOutrasEspecialidades(
										pacCodigo, consulta.getDtConsulta(),
										espSeqGen, dataLimite, pgdSeq);

						for (AinInternacao internacaoScn : listaInternacao2) {
							List<MpmSolicitacaoConsultoria> listaSolicitacaoConsultoria = this
									.getPrescricaoMedicaFacade()
									.pesquisarSolicitacaoConsultoriaPorInternacaoOutrasEspecialidades(
											internacaoScn.getSeq());
							if (listaSolicitacaoConsultoria != null
									&& listaSolicitacaoConsultoria.size() > 0) {
								for (MpmSolicitacaoConsultoria solicitacaoConsultoria : listaSolicitacaoConsultoria) {
									if (solicitacaoConsultoria
											.getEspecialidade() != null) {
										Short especialidadeSeq = null;
										AghEspecialidades especialidadeAux1 = solicitacaoConsultoria
												.getEspecialidade();
										if (especialidadeAux1
												.getEspecialidade() != null) {
											especialidadeSeq = especialidadeAux1
													.getEspecialidade()
													.getSeq();
										} else {
											especialidadeSeq = especialidadeAux1
													.getSeq();
										}
										if (especialidadeSeq != espSeqGen) {
											listaSolicitacaoConsultoria
													.remove(solicitacaoConsultoria);
										}
									}
								}
							}
							if (listaSolicitacaoConsultoria == null
									|| listaSolicitacaoConsultoria.size() == 0) {
								listaInternacao2.remove(internacaoScn);
							}
						}

						if (listaInternacao2 == null
								|| listaInternacao2.size() == 0) {
							throw new ApplicationBusinessException(
									AmbulatorioConsultasRNExceptionCode.AAC_00659);
						}
					}
					if (caaSeq.equals(vCaaPosAlta)) {
						AinInternacao internacao1 = null;
						if (listaInternacao1 != null
								&& listaInternacao1.size() > 0) {
							internacao1 = listaInternacao1.get(0);
						}
						quantidade = 0l;
						quantidade = this.getAacConsultasDAO()
								.pesquisarConsultaAnteriorPosAltaCount(
										consulta.getNumero(),
										consulta.getPaciente().getCodigo(),
										consulta.getDtConsulta(), espSeq,
										dataLimite, caaSeq, tagSeq, pgdSeq,
										internacao1.getDthrAltaMedica());
						if (quantidade > 1) {
							throw new ApplicationBusinessException(
									AmbulatorioConsultasRNExceptionCode.AAC_00654);
						}
					}
				}
				if (condicaoAtendimento != null
						&& DominioConsultaGenerica.D.equals(condicaoAtendimento
								.getIndGenericaInt())) {
					List<AinInternacao> listaInternacao3 = this
							.getInternacaoFacade()
							.pesquisarInternacaoEspecialidadeDiferente(
									pacCodigo, consulta.getDtConsulta(),
									espSeqGen, dataLimite, pgdSeq);
					if (listaInternacao3 == null
							|| listaInternacao3.size() == 0) {
						throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.AAC_00658);
					}
				}

				if (condicaoAtendimento != null
						&& DominioConsultaGenerica.I.equals(condicaoAtendimento
								.getIndGenericaInt())) {
					List<AinInternacao> listaInternacao4 = this
							.getInternacaoFacade()
							.pesquisarInternacaoIndependenteEspecialidade(
									pacCodigo, consulta.getDtConsulta(),
									dataLimite, pgdSeq);
					if (listaInternacao4 == null || listaInternacao4.size() == 0) {
						throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.AAC_00658);
					}
				}
			}
		}
		if (vNumeroConsulta != null) {
			return vNumeroConsulta;
		}
		return null;
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_GRAD_PA2
	 * @throws BaseException 
	 */
	public void verificarConsultaPacienteMesmaGrade(Integer grdSeq,
			Integer codigoPaciente, Date dtConsulta, Short caaSeq)
			throws BaseException {
		AghParametros parametroAtendimentoEmergencia = this
				.getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_COND_AT_EMERG);
		if (parametroAtendimentoEmergencia.getVlrTexto() != null) {
			throw new ApplicationBusinessException(
					AmbulatorioConsultasRNExceptionCode.AAC_00650);
		}
		Integer vCaaSeq = Integer.valueOf(parametroAtendimentoEmergencia
				.getVlrNumerico().toString());

		AghParametros parametroUnidadeSmo = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_UNIDADE_SMO);
		if (parametroUnidadeSmo.getVlrTexto() != null) {
			throw new ApplicationBusinessException(
					AmbulatorioConsultasRNExceptionCode.AAC_00258);
		}
		Short vUnfSmo = Short.valueOf(parametroUnidadeSmo.getVlrNumerico()
				.toString());

		if (vCaaSeq.equals(caaSeq)) {
			AacGradeAgendamenConsultas grade = this
					.getAacGradeAgendamentoConsultasDAO()
					.obterGradeAgendamento(grdSeq);
			if (grade.getAacUnidFuncionalSala().getUnidadeFuncional().getSeq() != vUnfSmo) {
				List<AacConsultas> listaConsultas = this.getAacConsultasDAO()
						.listarConsultas(grdSeq, codigoPaciente,
								DateUtil.truncaData(dtConsulta));
				if (listaConsultas.size() > 1) {
					if (this.verificarPerfil("AIPG_ADMIN_PACIENTES") == false) {
						throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.AAC_00168);
					}
				}
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_ALT_FAG
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarAlteracaoFormaAgendamento(Integer grdSeq,
			Short fagCaaSeq, Short fagTagSeq, Short fagPgdSeq)
			throws ApplicationBusinessException {
		List<AacGradeAgendamenConsultas> lista = this
				.getAacGradeAgendamentoConsultasDAO()
				.pesquisarGradeAgendamenConsultasPorSeqEFormaAgendamento(
						grdSeq, fagCaaSeq, fagTagSeq, fagPgdSeq);

		if (lista == null || lista.size() == 0) {
			if (kAutConsRetAmb == false) {
				throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.AAC_00620);
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_INC_PRH
	 * 
	 * @throws BaseException
	 * @throws NumberFormatException
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void atualizarInclusaoProcedHospitalar(AacConsultas consulta,
			AacSituacaoConsultas oldSituacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws NumberFormatException,
			BaseException {

		List<AacProcedHospEspecialidades> listaProcedHospEspecialidades = null;
		AghParametros parametroConvenioSus = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		Short codigoSus = parametroConvenioSus.getVlrNumerico().shortValue();
		if (consulta.getConvenioSaudePlano().getId().getCnvCodigo()
				.equals(codigoSus)) {
			if (consulta.getGradeAgendamenConsulta() != null
					&& !consulta.getGradeAgendamenConsulta().getProcedimento()) {

				/*
				 * Conforme conversado com Milena, este bloco IF não precisa ser
				 * migrado
				 * 
				 * IF aacc_ver_caract_grd(p_new_grd_seq,'Nao Lanca Consulta
				 * BPA') = 'N' THEN -- 3
				 */

				if (consulta.getGradeAgendamenConsulta() != null
						&& consulta.getGradeAgendamenConsulta()
								.getEspecialidade() != null) {
					listaProcedHospEspecialidades = this
							.getAacProcedHospEspecialidadesDAO()
							.listarProcedimentosEspecialidadesConsulta(
									consulta.getGradeAgendamenConsulta()
											.getEspecialidade().getSeq());
				}
				if (listaProcedHospEspecialidades != null
						&& !listaProcedHospEspecialidades.isEmpty()) {
					Integer conNumero = consulta.getNumero();
					AacProcedHospEspecialidades procedHospEspecialidades = listaProcedHospEspecialidades
							.get(0);
					FatProcedHospInternos procedHospInterno = procedHospEspecialidades
							.getProcedHospInterno();

					deletarFaturamento(conNumero, procedHospInterno.getSeq(), nomeMicrocomputador, dataFimVinculoServidor);

					AacConsultaProcedHospitalarId id = new AacConsultaProcedHospitalarId();
					id.setConNumero(conNumero);
					id.setPhiSeq(procedHospInterno.getSeq());
					AacConsultaProcedHospitalar consultaProcedHospitalar = this
							.getAacConsultaProcedHospitalarDAO()
							.obterPorChavePrimaria(id);
					this.getProcedimentoConsultaRN()
							.removerProcedimentoConsulta(
									consultaProcedHospitalar, false, nomeMicrocomputador);
					consultaProcedHospitalar
							.setQuantidade(VALOR_PADRAO_QUANTIDADE_CONSULTA);
					consultaProcedHospitalar.setConsulta(true);
					consultaProcedHospitalar.setConsultas(consulta);
					consultaProcedHospitalar
							.setProcedHospInterno(procedHospInterno);
					this.getProcedimentoConsultaRN()
							.inserirProcedimentoConsulta(
									consultaProcedHospitalar, false, nomeMicrocomputador, dataFimVinculoServidor);
				}
			} else if (consulta.getGradeAgendamenConsulta() != null
					&& consulta.getGradeAgendamenConsulta().getProcedimento()) {
				List<AacGradeProcedHospitalar> listaGradeProcedHospitalar = this
						.getAacGradeProcedimentoHospitalarDAO()
						.listarProcedimentosGrade(
								consulta.getGradeAgendamenConsulta().getSeq());
				for (AacGradeProcedHospitalar gradeProcedHospitalar : listaGradeProcedHospitalar) {
					AacConsultaProcedHospitalar consultaAux = new AacConsultaProcedHospitalar();
					AacConsultaProcedHospitalarId consultaAuxId = new AacConsultaProcedHospitalarId();
					FatProcedHospInternos procedHospInterno = gradeProcedHospitalar
							.getProcedHospInterno();

					deletarFaturamento(consulta.getNumero(),
							procedHospInterno.getSeq(), nomeMicrocomputador, dataFimVinculoServidor);

					consultaAuxId.setConNumero(consulta.getNumero());
					if (gradeProcedHospitalar != null
							&& gradeProcedHospitalar.getProcedHospInterno() != null) {
						consultaAuxId.setPhiSeq(procedHospInterno.getSeq());
						consultaAux.setId(consultaAuxId);

						AacConsultaProcedHospitalar consultaProcedHospitalar = this
								.getAacConsultaProcedHospitalarDAO()
								.obterPorChavePrimaria(consultaAuxId);
						this.getProcedimentoConsultaRN()
								.removerProcedimentoConsulta(
										consultaProcedHospitalar, false, nomeMicrocomputador);

						Byte quantidade = VALOR_PADRAO_QUANTIDADE_CONSULTA;
						consultaAux.setQuantidade(quantidade);
						consultaAux.setConsulta(false);
						consultaAux.setConsultas(consulta);
						consultaAux.setProcedHospInterno(procedHospInterno);
						this.getProcedimentoConsultaRN()
								.inserirProcedimentoConsulta(consultaAux, false, nomeMicrocomputador, dataFimVinculoServidor);
					}
				}
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_EXTRA
	 */
	public void verificarExtras(AacFormaAgendamento formaAgendamento)
			throws ApplicationBusinessException {
		if (!formaAgendamento.getPermiteExtra()) {
			AmbulatorioConsultasRNExceptionCode.AAC_00672.throwException();
		}
	}

	public Boolean verificaPeriodoConsultaEspecialidade(Date dataConsulta,
			AacFormaEspecialidade formaEspecialidade) {
		Boolean retorno = false;
		if (((formaEspecialidade.getDtInicio() == null || !DateUtil.truncaData(
				dataConsulta).before(
				formaEspecialidade.getDtInicio())) && formaEspecialidade
				.getDtFinal() == null)
				|| (!DateUtil.truncaData(dataConsulta).before(
						formaEspecialidade.getDtInicio())
						&& formaEspecialidade.getDtFinal() != null && !DateUtil
						.truncaData(dataConsulta).after(
								formaEspecialidade.getDtFinal()))) {
			retorno = true;
		}
		return retorno;
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_FAG_ESP
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarFormaAgendamentoEspecialidade(
			AacFormaAgendamento formaAgendamento,
			AacGradeAgendamenConsultas gradeAgendamentoConsulta,
			Date dataConsulta)
			throws ApplicationBusinessException {
		List<AacFormaEspecialidade> formasEspecialidade = null;
		Boolean retorno = false;
		if (formaAgendamento != null
				&& formaAgendamento.getFormaEspecialidades() != null) {
			formasEspecialidade = getAacFormaEspecialidadeDAO()
					.listaFormasEspecialidadePorFormaAgendaneto(
							formaAgendamento);
		}

		if (formasEspecialidade != null && !formasEspecialidade.isEmpty()) {
			for (AacFormaEspecialidade formaEspecialidade : formasEspecialidade) {
				if (gradeAgendamentoConsulta.getEspecialidade()
						.getSeq()
						.equals(formaEspecialidade.getEspecialidade().getSeq())) {
					// Especialidade permitida. Verificar datas.
					if (formaEspecialidade.getDtInicio() == null) { // Não há restrição por data.
						retorno = true;
					}
					if (verificaPeriodoConsultaEspecialidade(dataConsulta,
							formaEspecialidade)) {
						retorno = true;
					} else {
						AmbulatorioConsultasRNExceptionCode.AAC_00681
								.throwExceptionRollback();
					}
				}
			}
			if (!retorno) {
				AmbulatorioConsultasRNExceptionCode.AAC_00680
						.throwExceptionRollback();
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_AUTO_REL
	 * 
	 * @throws NumberFormatException
	 * @throws BaseException
	 */
	public void atualizarAutoRelacionamento(Integer consultaNumero, Integer pacCodigo, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws NumberFormatException, BaseException {
		
		List<AacConsultas> lista = aacConsultasDAO.pesquisarConsultacPorConsultaNumeroAnterior(consultaNumero);
		for (AacConsultas consulta : lista) {
			Integer numeroConsultaAnterior = obterConsultaAnteriorPaciente(consulta);
			if (numeroConsultaAnterior != null) {
				AacConsultas consultaAnterior = aacConsultasDAO.obterConsulta(numeroConsultaAnterior);
				AacConsultas consultaOld = this.getAmbulatorioFacade().clonarConsulta(consulta);
				consulta.setConsulta(consultaAnterior);
				atualizarConsulta(consultaOld, consulta, false, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, true);
			}
		}
		AacConsultas consulta = this.getAacConsultasDAO().obterConsulta(consultaNumero);
		if (consulta != null) {
			Integer numeroConsultaAnterior = obterConsultaAnteriorPaciente(consulta);
			AacConsultas consultaOld = this.getAmbulatorioFacade().clonarConsulta(consulta);
			if (numeroConsultaAnterior != null) {
				AacConsultas consultaAnterior = aacConsultasDAO.obterConsulta(numeroConsultaAnterior);
				consulta.setConsulta(consultaAnterior);
				this.atualizarConsulta(consultaOld, consulta, false, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, true);
			} else {
				consulta.setConsulta(null);
				this.atualizarConsulta(consultaOld, consulta, false, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, true);
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_GET_ANT_PAC
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	public Integer obterConsultaAnteriorPaciente(AacConsultas consulta)
			throws BaseException {
		Short espSeq = null;
		Byte maxFaltas = null;
		Short newEsp = null;
		Integer clinica = null;
		Integer tempoAnteriorGrd = null;
		DominioConsultaGenerica genericaCon = null;
		DominioConsultaGenerica genericaInt = null;
		Integer tempoAnteriorFag = null;
		String perfilEspecial = null;
		DominioIndAtendimentoAnterior atendimentoAnterior = null;
		Boolean exigeProntuario = false;
		Integer tempoAnterior = 0;
		Date dataLimite = new Date();
		Short convenioSusPadrao = null;
		Short caaReconsulta = null;
		Integer caaPosAlta = null;
		Integer retRefDevolvida = null;
		Integer contaFalta = null;
		Long quantidade = null;
		Short caaSeq = null;
		Short pgdSeq = null;
		Short tagSeq = null;
		Short espSeqGen = null;
		Integer vNumeroConsulta = null;
		if (consulta.getFormaAgendamento() != null
				&& consulta.getFormaAgendamento().getId() != null) {
			caaSeq = consulta.getFormaAgendamento().getId().getCaaSeq();
			pgdSeq = consulta.getFormaAgendamento().getId().getPgdSeq();
			tagSeq = consulta.getFormaAgendamento().getId().getTagSeq();
		}
		if (consulta != null
				&& consulta.getGradeAgendamenConsulta() != null
				&& consulta.getGradeAgendamenConsulta().getEspecialidade() != null
				&& consulta.getGradeAgendamenConsulta().getEspecialidade()
						.getEspecialidade() != null) {
			espSeqGen = consulta.getGradeAgendamenConsulta().getEspecialidade()
					.getEspecialidade().getSeq();
		} else if (consulta != null
				&& consulta.getGradeAgendamenConsulta() != null
				&& consulta.getGradeAgendamenConsulta().getEspecialidade() != null
				&& consulta.getGradeAgendamenConsulta().getEspecialidade()
						.getEspecialidade() == null) {
			espSeqGen = consulta.getGradeAgendamenConsulta().getEspecialidade()
					.getSeq();
		}
		if (consulta.getSituacaoConsulta() != null
				&& consulta.getSituacaoConsulta().getSituacao() != null
				&& consulta.getSituacaoConsulta().getSituacao().equals("M")) {
			if (consulta.getGradeAgendamenConsulta().getEspecialidade() != null
					&& consulta.getGradeAgendamenConsulta().getEspecialidade()
							.getEspecialidade() != null) {
				espSeq = consulta.getGradeAgendamenConsulta()
						.getEspecialidade().getEspecialidade().getSeq();
			} else if (consulta.getGradeAgendamenConsulta().getEspecialidade() != null) {
				espSeq = consulta.getGradeAgendamenConsulta()
						.getEspecialidade().getSeq();
			}
			if (consulta.getGradeAgendamenConsulta().getEspecialidade() != null) {
				maxFaltas = consulta.getGradeAgendamenConsulta()
						.getEspecialidade().getMaxQuantFaltas();
				newEsp = consulta.getGradeAgendamenConsulta()
						.getEspecialidade().getSeq();
				if (consulta.getGradeAgendamenConsulta().getEspecialidade()
						.getClinica() != null) {
					clinica = consulta.getGradeAgendamenConsulta()
							.getEspecialidade().getClinica().getCodigo();
				}
			}
//			if (consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala() != null
//					&& consulta.getGradeAgendamenConsulta()
//							.getAacUnidFuncionalSala().getId() != null) {
//			}
			tempoAnteriorGrd = consulta.getGradeAgendamenConsulta()
					.getTempoAtendAnterior();
			if (consulta.getFormaAgendamento() != null
					&& consulta.getFormaAgendamento().getCondicaoAtendimento() != null) {
				genericaCon = consulta.getFormaAgendamento()
						.getCondicaoAtendimento().getIndGenericaCon();
				genericaInt = consulta.getFormaAgendamento()
						.getCondicaoAtendimento().getIndGenericaInt();
				tempoAnteriorFag = consulta.getFormaAgendamento()
						.getTempoAnterior();
				perfilEspecial = consulta.getFormaAgendamento()
						.getPerfilEspecial();
				atendimentoAnterior = consulta.getFormaAgendamento()
						.getAtendimentoAnterior();
				exigeProntuario = consulta.getFormaAgendamento()
						.getExigeProntuario();
			}
			if (tempoAnteriorGrd != null) {
				tempoAnterior = tempoAnteriorGrd;
			} else if (tempoAnteriorFag != null){
				tempoAnterior = tempoAnteriorFag;
			}
			if (tempoAnterior != null && tempoAnterior.intValue() == 99999) {
				dataLimite = AmbulatorioConsultaRN.PRMEIRO_MAIO_2000;
			} else {
				dataLimite = DateUtil.truncaData(consulta.getDthrMarcacao());
				if (dataLimite != null){
					DateUtils.addMilliseconds(dataLimite, -tempoAnterior);					
				}
			}
			if (exigeProntuario && consulta.getCodCentral() == null) {
				Integer prontuario = null;
				AipPacientes paciente = consulta.getPaciente();
				if (paciente != null) {
					prontuario = paciente.getProntuario();
				}
				if (paciente == null
						|| (prontuario != null && prontuario > VALOR_MAXIMO_PRONTUARIO)) {
					throw new ApplicationBusinessException(
							AmbulatorioConsultasRNExceptionCode.AAC_00629);
				}
			}
			AghParametros parametroConvenioSus = this.getParametroFacade()
					.buscarAghParametro(
							AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
			if (parametroConvenioSus != null) {
				convenioSusPadrao = parametroConvenioSus.getVlrNumerico()
						.shortValue();
			}

			AghParametros parametroCaaReconsulta = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_CAA_RECONSULTA);
			if (parametroCaaReconsulta != null) {
				caaReconsulta = parametroCaaReconsulta.getVlrNumerico()
						.shortValue();
			}

			AghParametros parametroCaaPosAlta = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_CAA_POSALTA);
			if (parametroCaaPosAlta != null) {
				caaPosAlta = parametroCaaPosAlta.getVlrNumerico().intValue();
			}

			AghParametros parametroRetRefDevolvida = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_RET_REF_DEVOLVIDA);
			if (parametroRetRefDevolvida != null) {
				retRefDevolvida = parametroRetRefDevolvida.getVlrNumerico().intValue();
			}

			if (consulta.getSituacaoConsulta() != null
					&& consulta.getSituacaoConsulta().getSituacao() != null
					&& consulta.getSituacaoConsulta().getSituacao()
							.equals("M")) {
				if (atendimentoAnterior.equals(DominioIndAtendimentoAnterior.C)
						|| atendimentoAnterior
								.equals(DominioIndAtendimentoAnterior.A)) {
					if (consulta.getCodCentral() != null) {
						return vNumeroConsulta;
					}
				}
				List<AacNivelBusca> lista = this.getAacNivelBuscaDAO()
						.pesquisarNivelBuscaPorFormaAgendamento(
								consulta.getFormaAgendamento());
				if (lista.isEmpty()) {
					return vNumeroConsulta;
				}
//				if (tempoAnterior == null) {
//					return vNumeroConsulta;
//				}
				if (genericaCon != null && genericaCon.equals(DominioConsultaGenerica.M)) {

					if (consulta.getConvenioSaudePlano().getId().getCnvCodigo()
							.equals(convenioSusPadrao)
							&& consulta.getFormaAgendamento().getId()
									.getCaaSeq().equals(caaReconsulta)
							&& maxFaltas != null) {
						contaFalta = 0;
						List<AacConsultas> listaConsultas1 = this
								.getAacConsultasDAO()
								.pesquisarConsultaMesmaEspecialidade(
										consulta.getNumero(),
										consulta.getPaciente().getCodigo(),
										consulta.getDtConsulta(), dataLimite,
										caaSeq, tagSeq, pgdSeq);

						// filtro para especialidade generica ou especialidade
						for (Iterator<AacConsultas> iterator = listaConsultas1
								.iterator(); iterator.hasNext();) {
							AacConsultas consultaAux1 = (AacConsultas) iterator
									.next();
							if (consultaAux1.getGradeAgendamenConsulta() != null
									&& consultaAux1.getGradeAgendamenConsulta() != null
									&& consultaAux1.getGradeAgendamenConsulta()
											.getEspecialidade() != null) {
								Short especialidadeSeq = null;
								AghEspecialidades especialidadeAux1 = consultaAux1
										.getGradeAgendamenConsulta()
										.getEspecialidade();
								if (especialidadeAux1.getEspecialidade() != null) {
									especialidadeSeq = especialidadeAux1
											.getEspecialidade().getSeq();
								} else {
									especialidadeSeq = especialidadeAux1
											.getSeq();
								}
								if (especialidadeSeq != espSeqGen) {
									iterator.remove();
								}
							}
						}

						for (AacConsultas consulta1 : listaConsultas1) {
							vNumeroConsulta = consulta1.getNumero();
							if (consulta1.getRetorno().getAbsenteismo()
									.equals(DominioIndAbsenteismo.R)) {
								break;
							}
							DominioIndAbsenteismo absenteismo = null;
							if (consulta1.getRetorno() != null
									&& consulta1.getRetorno().getAbsenteismo() != null) {
								absenteismo = consulta1.getRetorno()
										.getAbsenteismo();
							}
							if (newEsp == consulta1.getGradeAgendamenConsulta()
									.getEspecialidade().getSeq()
									&& absenteismo
											.equals(DominioIndAbsenteismo.P)) {
								contaFalta++;
							}
							if (contaFalta > maxFaltas
									&& atendimentoAnterior
											.equals(DominioIndAtendimentoAnterior.C)) {
								vNumeroConsulta = null;
							}
						}

					} else {
						List<AacConsultas> listaConsultas1 = this
								.getAacConsultasDAO()
								.pesquisarConsultaMesmaEspecialidade(
										consulta.getNumero(),
										consulta.getPaciente().getCodigo(),
										consulta.getDtConsulta(), dataLimite,
										caaSeq, tagSeq, pgdSeq);
						// filtro para especialidade generica ou especialidade
						for (Iterator<AacConsultas> iterator = listaConsultas1
								.iterator(); iterator.hasNext();) {
							AacConsultas consultaAux1 = (AacConsultas) iterator
									.next();
							if (consultaAux1.getGradeAgendamenConsulta() != null
									&& consultaAux1.getGradeAgendamenConsulta() != null
									&& consultaAux1.getGradeAgendamenConsulta()
											.getEspecialidade() != null) {
								Short especialidadeSeq = null;
								AghEspecialidades especialidadeAux1 = consultaAux1
										.getGradeAgendamenConsulta()
										.getEspecialidade();
								if (especialidadeAux1.getEspecialidade() != null) {
									especialidadeSeq = especialidadeAux1
											.getEspecialidade().getSeq();
								} else {
									especialidadeSeq = especialidadeAux1
											.getSeq();
								}
								if (especialidadeSeq != espSeqGen) {
									iterator.remove();
								}
							}
						}

						if (listaConsultas1 != null
								&& listaConsultas1.size() > 0) {
							AacConsultas consulta1 = listaConsultas1.get(0);
							if (consulta1 == null
									&& atendimentoAnterior
											.equals(DominioIndAtendimentoAnterior.C)) {
								vNumeroConsulta = null;
							}
							if (consulta1.getRetorno() != null
									&& consulta1.getRetorno().getSeq() == retRefDevolvida
									&& newEsp.equals(consulta1
											.getGradeAgendamenConsulta()
											.getEspecialidade().getSeq())
									&& consulta.getConvenioSaudePlano().getId()
											.getCnvCodigo()
											.equals(convenioSusPadrao)
									&& caaSeq.equals(caaReconsulta)) {
								vNumeroConsulta = null;
							}
							vNumeroConsulta = consulta1.getNumero();
						}
					}
					if (vNumeroConsulta == null
							&& atendimentoAnterior
									.equals(DominioIndAtendimentoAnterior.C)) {
						if (!this.verificarPerfil(perfilEspecial)) {
							return vNumeroConsulta;
						}
					}
				}
				if (genericaCon != null && genericaCon.equals(DominioConsultaGenerica.D)) {
					List<AacConsultas> listaConsultas2 = this
							.getAacConsultasDAO()
							.pesquisarConsultaEspecialidadeDiferente(
									consulta.getPaciente().getCodigo(),
									consulta.getDtConsulta(), espSeqGen,
									dataLimite, caaSeq, tagSeq, pgdSeq);
					if ((listaConsultas2 == null || listaConsultas2.size() == 0)
							&& atendimentoAnterior
									.equals(DominioIndAtendimentoAnterior.C)) {
						if (!this.verificarPerfil(perfilEspecial)) {
							vNumeroConsulta = null;
						} else {
							return vNumeroConsulta;
						}
					} else if (listaConsultas2.size() > 0) {
						AacConsultas consulta2 = listaConsultas2.get(0);
						vNumeroConsulta = consulta2.getNumero();
					}
				}
				if (genericaCon != null && genericaCon.equals(DominioConsultaGenerica.I)) {
					List<AacConsultas> listaConsultas3 = this
							.getAacConsultasDAO()
							.pesquisarConsultaIndependenteEspecialidade(
									consulta.getPaciente().getCodigo(),
									consulta.getDtConsulta(), dataLimite,
									caaSeq, tagSeq, pgdSeq);
					if ((listaConsultas3 == null || listaConsultas3.size() == 0)
							&& atendimentoAnterior
									.equals(DominioIndAtendimentoAnterior.C)) {
						if (!this.verificarPerfil(perfilEspecial)) {
							vNumeroConsulta = null;
						} else {
							return vNumeroConsulta;
						}
					} else if (listaConsultas3.size() > 0) {
						AacConsultas consulta3 = listaConsultas3.get(0);
						vNumeroConsulta = consulta3.getNumero();
					}
				}
			}
			if (atendimentoAnterior.equals(DominioIndAtendimentoAnterior.I)
					|| atendimentoAnterior
							.equals(DominioIndAtendimentoAnterior.A)) {
				if (atendimentoAnterior.equals(DominioIndAtendimentoAnterior.A)
						&& vNumeroConsulta != null) {
					return vNumeroConsulta;
				}
				if (clinica.equals(4)) {
					List<AinInternacao> listaInternacao3 = this
							.getInternacaoFacade()
							.pesquisarInternacaoIndependenteEspecialidade(
									consulta.getPaciente().getCodigo(),
									consulta.getDtConsulta(), dataLimite,
									pgdSeq);

					if ((listaInternacao3 == null || listaInternacao3.size() == 0)
							&& atendimentoAnterior
									.equals(DominioIndAtendimentoAnterior.C)) {
						if (!this.verificarPerfil(perfilEspecial)) {
							vNumeroConsulta = null;
						} else {
							return vNumeroConsulta;
						}
					}
				}
				if (genericaInt.equals(DominioConsultaGenerica.M)) {

					List<AinInternacao> listaInternacao1 = this
							.getInternacaoFacade()
							.pesquisarInternacaoMesmaEspecialidade(
									consulta.getPaciente().getCodigo(),
									consulta.getDtConsulta(), dataLimite,
									pgdSeq);
					for (AinInternacao internacao : listaInternacao1) {
						Boolean unidadeEmergencia = false;
						Boolean verificaEspecialidade = false;
						String leitoID = null;
						Short numeroQuarto = null;
						Short unidadeFuncionalSeq = null;
						if (internacao.getLeito() == null) {
							leitoID = null;
						} else {
							leitoID = internacao.getLeito().getLeitoID();
						}
						if (internacao.getQuarto() == null) {
							numeroQuarto = null;
						} else {
							numeroQuarto = internacao.getQuarto().getNumero();
						}

						if (internacao.getUnidadesFuncionais() == null) {
							unidadeFuncionalSeq = null;
						} else {
							unidadeFuncionalSeq = internacao
									.getUnidadesFuncionais().getSeq();
						}
						Short especialidadeSeq = null;
						Short especialidadeGenericaSeq = null;
						if (internacao.getEspecialidade() == null
								|| internacao.getEspecialidade()
										.getEspecialidade() == null) {
							especialidadeGenericaSeq = null;
						} else {
							especialidadeGenericaSeq = internacao
									.getEspecialidade().getEspecialidade()
									.getSeq();
						}
						if (internacao.getEspecialidade() == null) {
							especialidadeSeq = null;
						} else {
							especialidadeSeq = internacao.getEspecialidade()
									.getSeq();
						}

						unidadeEmergencia = this.verificarUnidadeEmergencia(
								leitoID, numeroQuarto, unidadeFuncionalSeq);
						verificaEspecialidade = this.verificarEspecialidade(
								especialidadeGenericaSeq, especialidadeSeq,
								espSeq);
						if (unidadeEmergencia == false
								&& verificaEspecialidade == false) {
							listaInternacao1.remove(internacao);
						}
					}
					if (listaInternacao1.isEmpty()) {
						List<AinInternacao> listaInternacaoScn = this
								.getInternacaoFacade()
								.pesquisarInternacaoOutrasEspecialidades(
										consulta.getPaciente().getCodigo(),
										consulta.getDtConsulta(),
										espSeqGen,
										dataLimite,
										consulta.getFormaAgendamento().getId()
												.getPgdSeq());
						for (AinInternacao internacaoScn : listaInternacaoScn) {
							List<MpmSolicitacaoConsultoria> listaSolicitacaoConsultoria = this
									.getPrescricaoMedicaFacade()
									.pesquisarSolicitacaoConsultoriaPorInternacaoOutrasEspecialidades(
											internacaoScn.getSeq());
							if (listaSolicitacaoConsultoria != null
									&& listaSolicitacaoConsultoria.size() > 0) {
								for (MpmSolicitacaoConsultoria solicitacaoConsultoria : listaSolicitacaoConsultoria) {
									if (solicitacaoConsultoria
											.getEspecialidade() != null) {
										Short especialidadeSeq = null;
										AghEspecialidades especialidadeAux1 = solicitacaoConsultoria
												.getEspecialidade();
										if (especialidadeAux1
												.getEspecialidade() != null) {
											especialidadeSeq = especialidadeAux1
													.getEspecialidade()
													.getSeq();
										} else {
											especialidadeSeq = especialidadeAux1
													.getSeq();
										}
										if (especialidadeSeq != espSeqGen) {
											listaSolicitacaoConsultoria
													.remove(solicitacaoConsultoria);
										}
									}
								}
							}
							if (listaSolicitacaoConsultoria == null
									|| listaSolicitacaoConsultoria.size() == 0) {
								listaInternacaoScn.remove(internacaoScn);
							}
						}

						if (listaInternacaoScn == null
								|| listaInternacaoScn.size() == 0) {
							if (!this.verificarPerfil(perfilEspecial)) {
								vNumeroConsulta = null;
							} else {
								return vNumeroConsulta;
							}
						}
					}
					AinInternacao internacao1 = new AinInternacao();
					if (listaInternacao1 != null && listaInternacao1.size() > 0) {
						internacao1 = listaInternacao1.get(0);

					}
					if (caaSeq.equals(caaPosAlta)) {
						quantidade = 0l;
						quantidade = this.getAacConsultasDAO()
								.pesquisarConsultaPosAltaCount(
										consulta.getNumero(),
										consulta.getPaciente().getCodigo(),
										consulta.getDtConsulta(), espSeq,
										dataLimite, caaSeq, tagSeq, pgdSeq,
										internacao1.getDthrAltaMedica());
						if (quantidade > 0) {
							if (!this.verificarPerfil(perfilEspecial)) {
								vNumeroConsulta = null;
							} else {
								return vNumeroConsulta;
							}
						}
					}
				}
				if (genericaInt.equals(DominioConsultaGenerica.D)) {
					List<AinInternacao> listaInternacao2 = this
							.getInternacaoFacade()
							.pesquisarInternacaoEspecialidadeDiferente(
									consulta.getPaciente().getCodigo(),
									consulta.getDtConsulta(), espSeq,
									dataLimite, pgdSeq);
					if (listaInternacao2 == null
							|| listaInternacao2.size() == 0) {
						if (!this.verificarPerfil(perfilEspecial)) {
							vNumeroConsulta = null;
						} else {
							return vNumeroConsulta;
						}
					}
				}
				if (genericaInt.equals(DominioConsultaGenerica.I)) {
					List<AinInternacao> listaInternacao3 = this
							.getInternacaoFacade()
							.pesquisarInternacaoIndependenteEspecialidade(
									consulta.getPaciente().getCodigo(),
									consulta.getDtConsulta(), dataLimite,
									pgdSeq);

					if ((listaInternacao3 == null || listaInternacao3.size() == 0)
							&& atendimentoAnterior
									.equals(DominioIndAtendimentoAnterior.C)) {
						if (!this.verificarPerfil(perfilEspecial)) {
							vNumeroConsulta = null;
						} else {
							return vNumeroConsulta;
						}
					}
				}
			}
		}
		return vNumeroConsulta;
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_AT_UFESP_ATD
	 * @throws BaseException 
	 */
	public void atualizarAtendimentoGrade(Integer numConsulta, Integer grdSeqNew, String nomeMicrocomputador)
			throws BaseException {
		AacGradeAgendamenConsultas grade = getAacGradeAgendamentoConsultasDAO()
				.obterPorChavePrimaria(grdSeqNew);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		IAghuFacade aghuFacade = getAghuFacade();

		AghEspecialidades grdEspecialidade = grade.getEspecialidade();
		AghUnidadesFuncionais grdUnidFuncional = grade
				.getAacUnidFuncionalSala().getUnidadeFuncional();

		List<AghAtendimentos> atendimentos = aghuFacade
				.listarAtendimentosPorConsulta(numConsulta);
		
		final Date dataFimVinculoServidor = new Date();

		for (AghAtendimentos aghAtendimento : atendimentos) {
			AghAtendimentos atendimentoOld = aghAtendimento;
			aghAtendimento.setUnidadeFuncional(grdUnidFuncional);
			aghAtendimento.setEspecialidade(grdEspecialidade);
			this.getPacienteFacade().atualizarAtendimento(aghAtendimento,
					atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_SIT_ATIV
	 */
	public void verificarSituacaoConsultaAtiva(AacSituacaoConsultas situacao)
			throws ApplicationBusinessException {

		if (situacao.getIndSituacao() != null
				&& !situacao.getIndSituacao().equals(DominioSituacao.A)) {
			AmbulatorioConsultasRNExceptionCode.AAC_00691.throwException();
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_PGD_GRD
	 */
	public void verificarProjetoGrade(AacGradeAgendamenConsultas grade, Short pgdCon, Short tagCon) throws ApplicationBusinessException {

		if (grade == null) {
			AmbulatorioConsultasRNExceptionCode.AAC_00066.throwException();
		} else {
			if (grade.getPagador() != null
					&& grade.getPagador().getSeq().equals(pgdCon)) {
				AmbulatorioConsultasRNExceptionCode.AAC_00695.throwException();
			}
			if (grade.getTipoAgendamento() != null
					&& grade.getTipoAgendamento().getSeq().equals(tagCon)) {
				AmbulatorioConsultasRNExceptionCode.AAC_00696.throwException();
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_PROF
	 */
	public RapServidores atualizarProfissionalGrade(
			AacGradeAgendamenConsultas grade)
			throws ApplicationBusinessException {

		RapServidores servidor = null;

		if (grade.getProfEspecialidade() != null) {
			servidor = grade.getProfEspecialidade().getRapServidor();
		}
		return servidor;
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_PROF_ATE
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarProfissionalAtendidoPor(RapServidores servidor,
			Integer grdSeq) throws ApplicationBusinessException,
			ApplicationBusinessException {

		AacGradeAgendamenConsultas grade = this
				.getAacGradeAgendamentoConsultasDAO().obterPorChavePrimaria(
						grdSeq);

		if (grade.getProfEspecialidade() != null
				&& grade.getProfEspecialidade().getId() != null
				&& grade.getProfEspecialidade().getId().getSerMatricula() != null) {
			if (servidor != null && servidor.getId() != null
					&& servidor.getId().getMatricula() != null) {
				if (grade.getProfEspecialidade().getId().getSerMatricula() != servidor
						.getId().getMatricula()) {
					throw new ApplicationBusinessException(
							AmbulatorioConsultasRNExceptionCode.AAC_00697);
				}
			}
		}

		if (servidor != null && servidor.getId() != null
				&& servidor.getId().getMatricula() != null) {
			List<AghProfEspecialidades> lista = this
					.getAghuFacade().listaProfEspecialidades(
							servidor, grade.getEspecialidade().getSeq());
			if (lista.isEmpty()) {
				throw new ApplicationBusinessException(
						AmbulatorioConsultasRNExceptionCode.AAC_00698);
			}
		}

	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONC_VER_EN_PAC
	 */
	public Long verificarPacienteListaEspera(AacGradeAgendamenConsultas grade,
			Integer pacCodigo) throws ApplicationBusinessException {

		Long seqInterconsulta = Long.valueOf("0");

		MamInterconsultas interconsulta = getMamIterconsultasDAO()
				.obterInterconsultaPorEspecialidadePaciente(
						grade.getEspecialidade().getSeq(), pacCodigo, true);

		// Verificar se paciente pendente na lista de espera de internconsultas
		if (interconsulta != null && interconsulta.getSeq() != null) {
			seqInterconsulta = interconsulta.getSeq();
		}

		return seqInterconsulta;
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONC_VER_EN_OUTRO
	 * 
	 */
	public Long verificarPacienteOutraListaEspera(
			AacGradeAgendamenConsultas grade, Integer pacCodigo)
			throws ApplicationBusinessException {

		Long seqInterconsulta = Long.valueOf("0");

		MamInterconsultas interconsulta = getMamIterconsultasDAO()
				.obterInterconsultaPorEspecialidadePaciente(
						grade.getEspecialidade().getSeq(), pacCodigo, false);

		// Verificar se paciente pendente na lista de espera de internconsultas
		if (interconsulta != null) {
			seqInterconsulta = interconsulta.getSeq();
		}

		return seqInterconsulta;
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_PROJ_PES
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	public void atualizarProjetoPesquisa(AacConsultas consulta)
			throws ApplicationBusinessException {

		AghParametros p1 = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_PGD_SEQ_PESQUISA);
		Short fagPgdSeq = null;
		Short pFagPgdSeq = null;
		if (p1 != null) {
			fagPgdSeq = p1.getVlrNumerico().shortValue();
		}
		if (consulta.getFormaAgendamento() != null
				&& consulta.getFormaAgendamento().getId() != null) {
			pFagPgdSeq = consulta.getFormaAgendamento().getId().getPgdSeq();
		}

		if (fagPgdSeq != pFagPgdSeq) {
			return;
		}

		AghParametros p2 = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SIT_CONS_MARCADA);
		String stcSituacao = null;
		String pSituacao = null;
		if (p2 != null) {
			stcSituacao = p1.getVlrTexto();
		}

		if (consulta.getSituacaoConsulta() != null) {
			pSituacao = consulta.getSituacaoConsulta().getSituacao();
		}
		if (stcSituacao != pSituacao) {
			return;
		}
		Integer pjqSeq = null;
		if (consulta.getProjetoPesquisa() != null) {
			pjqSeq = consulta.getProjetoPesquisa().getSeq();
		}

		AelProjetoPesquisas projetoPesquisa = this.getExamesFacade()
				.obterProjetoPesquisaSituacaoData(pjqSeq);

		AelProjetoPacientes projetoPaciente = new AelProjetoPacientes();
		AelProjetoPacientesId id = new AelProjetoPacientesId();
		id.setPjqSeq(projetoPesquisa.getSeq());
		id.setPacCodigo(consulta.getPaciente().getCodigo());
		projetoPaciente.setId(id);
		projetoPaciente.setDtInicio(new Date());
		projetoPaciente.setIndSituacao(DominioSituacao.A);
		projetoPaciente.setNumero(projetoPesquisa.getNumero());

		this.getExamesFacade().persistirProjetoPaciente(projetoPaciente);
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONC_VER_CON_FUNC
	 * 
	 */
	public Boolean verificarConsultasFuncSmo(Integer numero) {
		AacConsultas consulta = this.getAacConsultasDAO()
				.obterPorChavePrimaria(numero);
		Short seqEspecialidade = consulta.getGradeAgendamenConsulta()
				.getEspecialidade().getSeq();
		AghCaractEspecialidadesId caractEspecialidadesId = new AghCaractEspecialidadesId();
		caractEspecialidadesId.setEspSeq(seqEspecialidade);
		caractEspecialidadesId
				.setCaracteristica(DominioCaracEspecialidade.FUNC_SMO);
		AghCaractEspecialidades caracteristicaEspecialidade = this
				.getAghuFacade()
				.obterCaracteristicaEspecialidadePorChavePrimaria(
						caractEspecialidadesId);
		if (caracteristicaEspecialidade == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_PRH
	 * 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void atualizarProcedimentoHospitalar(AacConsultas consulta,
			Integer newGrdSeq, Integer oldGrdSeq, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		AacGradeAgendamenConsultas gradeOld = this
				.getAacGradeAgendamentoConsultasDAO().obterGradeAgendamento(
						oldGrdSeq);
		AacGradeAgendamenConsultas grade = this
				.getAacGradeAgendamentoConsultasDAO().obterGradeAgendamento(
						newGrdSeq);
		if (gradeOld.getProcedimento()) {
			List<AacGradeProcedHospitalar> lista = this
					.getAacGradeProcedimentoHospitalarDAO()
					.listarProcedimentosGrade(oldGrdSeq);
			for (AacGradeProcedHospitalar gradeProcedHospitalar : lista) {
				AacGradeProcedHospitalarId id = new AacGradeProcedHospitalarId();
				id.setGrdSeq(newGrdSeq);
				if (gradeProcedHospitalar.getProcedHospInterno() != null) {
					Integer conNumero = consulta.getNumero();
					Integer phiSeq = gradeProcedHospitalar
							.getProcedHospInterno().getSeq();
					id.setPhiSeq(phiSeq);
					AacGradeProcedHospitalar gradeAux = this
							.getAacGradeProcedimentoHospitalarDAO()
							.obterPorChavePrimaria(id);
					if (gradeAux == null) {

						deletarFaturamentoSemCapturaExcecao(conNumero, phiSeq, nomeMicrocomputador, dataFimVinculoServidor);

						AacConsultaProcedHospitalarId consultaProcedHospitalarId = new AacConsultaProcedHospitalarId();
						consultaProcedHospitalarId.setConNumero(conNumero);
						if (gradeProcedHospitalar.getProcedHospInterno() != null) {
							consultaProcedHospitalarId.setPhiSeq(phiSeq);
							AacConsultaProcedHospitalar consultaProcedHospitalar = this
									.getAacConsultaProcedHospitalarDAO()
									.obterPorChavePrimaria(
											consultaProcedHospitalarId);
							this.getProcedimentoConsultaRN()
									.removerProcedimentoConsulta(
											consultaProcedHospitalar, false, nomeMicrocomputador);
						}
					}
				}

			}
		} else {
			if (!gradeOld.getEspecialidade().equals(grade.getEspecialidade())) {
				List<AacProcedHospEspecialidades> listaOld = this
						.getAacProcedHospEspecialidadesDAO()
						.listarProcedimentosEspecialidadesConsulta(
								gradeOld.getEspecialidade().getSeq());
				if (listaOld != null) {
					AacProcedHospEspecialidades procedHospEspecialidadeOld = listaOld
							.get(0);
					AacConsultaProcedHospitalarId consultaProcedHospitalarId = new AacConsultaProcedHospitalarId();
					Integer conNumero = consulta.getNumero();
					consultaProcedHospitalarId.setConNumero(conNumero);
					if (procedHospEspecialidadeOld.getProcedHospInterno() != null) {
						Integer phiSeq = procedHospEspecialidadeOld
								.getProcedHospInterno().getSeq();

						deletarFaturamentoSemCapturaExcecao(conNumero, phiSeq, nomeMicrocomputador, dataFimVinculoServidor);

						consultaProcedHospitalarId.setPhiSeq(phiSeq);
						AacConsultaProcedHospitalar consultaProcedHospitalarOld = this
								.getAacConsultaProcedHospitalarDAO()
								.obterPorChavePrimaria(
										consultaProcedHospitalarId);
						this.getProcedimentoConsultaRN()
								.removerProcedimentoConsulta(
										consultaProcedHospitalarOld, false, nomeMicrocomputador);
					}
					List<AacProcedHospEspecialidades> lista = this
							.getAacProcedHospEspecialidadesDAO()
							.listarProcedimentosEspecialidadesConsulta(
									grade.getEspecialidade().getSeq());
					if (lista != null) {
						AacProcedHospEspecialidades procedHospEspecialidade = lista
								.get(0);
						AacConsultaProcedHospitalar consultaProcedHospitalar = new AacConsultaProcedHospitalar();
						AacConsultaProcedHospitalarId id = new AacConsultaProcedHospitalarId();
						id.setConNumero(consulta.getNumero());
						if (procedHospEspecialidade.getProcedHospInterno() != null) {
							FatProcedHospInternos procedHospInterno = procedHospEspecialidade
									.getProcedHospInterno();
							id.setPhiSeq(procedHospInterno.getSeq());
							consultaProcedHospitalar.setId(id);
							consultaProcedHospitalar
									.setQuantidade(VALOR_PADRAO_QUANTIDADE_CONSULTA);
							consultaProcedHospitalar.setConsulta(true);
							consultaProcedHospitalar.setConsultas(consulta);
							consultaProcedHospitalar
									.setProcedHospInterno(procedHospInterno);
							getProcedimentoConsultaRN()
									.inserirProcedimentoConsulta(
											consultaProcedHospitalar, false, nomeMicrocomputador, dataFimVinculoServidor);
						}
					}
				}

			}
		}
		if (grade.getProcedimento()) {
			List<AacGradeProcedHospitalar> lista = this
					.getAacGradeProcedimentoHospitalarDAO()
					.listarProcedimentosGrade(newGrdSeq);
			for (AacGradeProcedHospitalar gradeProcedHospitalar : lista) {
				AacConsultaProcedHospitalar consultaProcedHospitalar = new AacConsultaProcedHospitalar();
				AacConsultaProcedHospitalarId consultaProcedHospitalarId = new AacConsultaProcedHospitalarId();
				consultaProcedHospitalarId.setConNumero(consulta.getNumero());
				if (gradeProcedHospitalar.getProcedHospInterno() != null) {
					FatProcedHospInternos procedHospInterno = gradeProcedHospitalar
							.getProcedHospInterno();
					consultaProcedHospitalarId.setPhiSeq(procedHospInterno
							.getSeq());
					consultaProcedHospitalar.setId(consultaProcedHospitalarId);
					consultaProcedHospitalar
							.setQuantidade(VALOR_PADRAO_QUANTIDADE_CONSULTA);
					consultaProcedHospitalar.setConsulta(false);
					consultaProcedHospitalar.setConsultas(consulta);
					consultaProcedHospitalar
							.setProcedHospInterno(procedHospInterno);
					getProcedimentoConsultaRN().inserirProcedimentoConsulta(
							consultaProcedHospitalar, false, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_INCL_RAT
	 * 
	 */
	@SuppressWarnings("ucd")
	public void atualizarInclusaoRadioterapiaTomografia(AipPacientes paciente,
			AacConsultas consulta, Date dtConsulta, Short cspCnvCodigo,
			Byte cspSeq) throws ApplicationBusinessException {

		FatConvenioSaudePlanoId convenioId = new FatConvenioSaudePlanoId(
				cspCnvCodigo, cspSeq);
		FatConvenioSaudePlano convenio = getFaturamentoFacade()
				.obterFatConvenioSaudePlanoPorChavePrimaria(convenioId);
		AacRadioAcompTomosDAO radioAcompDao = getAacRadioAcompTomosDAO();

		AacRadioAcompTomos aacRadioAcompTomo = new AacRadioAcompTomos();
		aacRadioAcompTomo.setConvenioSaudePlano(convenio);
		aacRadioAcompTomo.setConsulta(consulta);
		aacRadioAcompTomo.setSituacao(DominioSituacao.A);
		aacRadioAcompTomo.setPaciente(paciente);

		radioAcompDao.persistir(aacRadioAcompTomo);
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_ATU_MAM_EN
	 * 
	 */
	public void atualizarAmbulatorioInterconsultas(Integer consultaNumero)
			throws ApplicationBusinessException {
		List<MamInterconsultas> listaInterconsultas = this
				.getMamIterconsultasDAO().listarInterconsultasPorNumero(
						consultaNumero);
		for (MamInterconsultas interconsulta : listaInterconsultas) {
			interconsulta.setConsultaMarcada(getAmbulatorioFacade()
					.obterConsulta(consultaNumero));
			interconsulta.setSituacao(DominioSituacaoInterconsultasPesquisa.M);
			this.getMarcacaoConsultaRN().atualizarInterconsultas(interconsulta);
		}
	}

	/**
	 * ORADB: AACK_CON_3_RN.RN_CONP_VER_FORMA_PR
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	public void verificarFormaConsultaExcedente(AacConsultas consulta)
			throws ApplicationBusinessException {

		Integer grdSeq = null;
		if (consulta.getGradeAgendamenConsulta() != null) {
			grdSeq = consulta.getGradeAgendamenConsulta().getSeq();
		}
		AghParametros parametroCaa = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_CAA_SESSAO);
		Short caaSeq = null;
		if (consulta.getCondicaoAtendimento() != null) {
			caaSeq = consulta.getCondicaoAtendimento().getSeq();
		}
		Short vCaa = null;
		if (parametroCaa != null) {
			vCaa = parametroCaa.getVlrNumerico().shortValue();
		}

		Boolean existeConsultas = this.getAacConsultasDAO()
				.verificarExisteConsultasNaoExcedentes(
						consulta.getDtConsulta(), caaSeq);

		if (caaSeq == vCaa && (grdSeq == null || !existeConsultas)) {
			throw new ApplicationBusinessException(AmbulatorioConsultasRNExceptionCode.AAC_00722);
		}
	}

	private void atualizaDadosUnimed(AacConsultas consulta) throws ApplicationBusinessException {
		AghParametrosVO parametrosPL =  new AghParametrosVO();
		parametrosPL.setNome(AghuParametrosEnum.P_UNIMED_FUNC_PL.toString());
		AghParametrosVO parametrosCNV =  new AghParametrosVO();
		parametrosCNV.setNome(AghuParametrosEnum.P_UNIMED_FUNC_CNV.toString());
		getParametroFacade().getAghpParametro(parametrosCNV);
		getParametroFacade().getAghpParametro(parametrosPL);
		// #52615
		AacPagador outrosConvenios = ambulatorioFacade.obterPagadorPorCodigo((short) 2);
		consulta.setPagador(outrosConvenios);
		
		consulta.getConvenioSaudePlano().getId().setCnvCodigo(parametrosCNV.getVlrNumerico().shortValue());
		consulta.getConvenioSaudePlano().getId().setSeq(parametrosPL.getVlrNumerico().byteValue());
	}
	
	/**#42229
	 * @ORADB: AACK_CON_3_RN.RN_CONP_ATU_UNIMED
	 * 
	 */
	public AacConsultas atualizarUnimed(AacConsultas consulta) throws ApplicationBusinessException {
		Integer gradeAgendamenConsultaSeq = null, pacienteCodigo = null, matriculaConsultado = null, vProntuario = null;
		Short vinculoCodigoConsultado = null;
		if (consulta == null || consulta.getPagador() == null) {
			return consulta;
		}
		if (consulta.getGradeAgendamenConsulta() != null) {
			gradeAgendamenConsultaSeq = consulta.getGradeAgendamenConsulta().getSeq();
		}
		if (consulta.getPaciente() != null) {
			pacienteCodigo = consulta.getPaciente().getCodigo();
		}

		if (consulta.getServidorConsultado() != null && consulta.getServidorConsultado().getId() != null) {
			matriculaConsultado = consulta.getServidorConsultado().getId().getMatricula(); 
			vinculoCodigoConsultado = consulta.getServidorConsultado().getId().getVinCodigo();
		}
		validarCarteiraUnimed(consulta, gradeAgendamenConsultaSeq, pacienteCodigo, matriculaConsultado, vProntuario, vinculoCodigoConsultado);
		return consulta;
		/*
		 * PROCEDURE AACK_CON_3_RN.RN_CONP_ATU_UPD_RAT (P_PAC_CODIGO IN NUMBER ,P_DT_CONSULTA IN DATE ,P_SER_VIN_CODIGO IN NUMBER ,P_SER_MATRICULA IN
		 * NUMBER ) IS BEGIN -- Incluir ocorrências para acompanhamento de tomografias na radioterapia UPDATE AAC_RADIO_ACOMP_TOMOS SET ser_vin_codigo
		 * = p_ser_vin_codigo, ser_matricula = p_ser_matricula WHERE PAC_CODIGO = p_pac_codigo AND TRUNC(dthr_exame) = TRUNC(p_dt_consulta) AND
		 * ser_matricula IS NULL; END;
		 */
	}

	private void validarCarteiraUnimed(AacConsultas consulta, Integer gradeAgendamenConsultaSeq, Integer pacienteCodigo, Integer matriculaConsultado, Integer vProntuario, Short vinculoCodigoConsultado)
			throws ApplicationBusinessException {
		Integer vSerMatriculaConsultado;
		Short vVincodigoConsultado;
		char vCaractUnimed = 'N';
		Date dataConsulta = consulta.getDtConsulta();
		DominioFuncionario indFuncionario = consulta.getIndFuncionario();
		AacCaracteristicaGrade caractGrade = getAacCaracteristicaGradeDAO().obterCaracteristicaGradeUnimed(gradeAgendamenConsultaSeq);
		vCaractUnimed = checarCarteiraUnimed(caractGrade);
		if (vCaractUnimed == 'S') {
			vSerMatriculaConsultado = matriculaConsultado;
			vVincodigoConsultado = vinculoCodigoConsultado;
			// Dependente e tambem tem matricula
			if (matriculaConsultado == null || indFuncionario == DominioFuncionario.D) {
				vProntuario = this.getPacienteFacade().obterProntuarioPorPacCodigo(pacienteCodigo);
				vSerMatriculaConsultado = null;
				vVincodigoConsultado = null;
			}
			if (vProntuario != null || vSerMatriculaConsultado != null) {
				/* RAPC_VESETEM_UNIMED so é chamada se for Oracle e isHCPA */
				if (getObjetosOracleDAO().isOracle() && isHCPA()) {
					/* RAPC_VESETEM_UNIMED */
					if (verificaServidorTemUnimed(vSerMatriculaConsultado, vVincodigoConsultado, null, vProntuario, dataConsulta).equals("N")) {
						atualizaDadosUnimed(consulta);
					}
				}
			}
		}
	}

	private char checarCarteiraUnimed(AacCaracteristicaGrade caractGrade) {
		char vCaractUnimed;
		if (caractGrade == null) {
			vCaractUnimed = 'N';
		} else {
			vCaractUnimed = 'S';
		}
		return vCaractUnimed;
	}
	/**
	 * ORADB: AACK_CON_3_RN.aacc_busca_cod_central
	 * 
	 */
	public Long obterCodigoCentral(Integer codigoPaciente,
			Integer numeroConsulta, Date dtConsulta) {
		List<AacConsultas> listaConsultas = this.getAacConsultasDAO()
				.listarConsultaCodigoCentral(codigoPaciente, numeroConsulta,
						dtConsulta);
		AacConsultas consulta = null;
		if (listaConsultas != null && listaConsultas.size() > 0) {
			consulta = listaConsultas.get(0);
		}
		if (consulta != null) {
			return consulta.getCodCentral();
		}
		return null;
	}

	/**
	 * FUNCTION ORADB: AACC_VER_ROLES
	 * 
	 * @param nome
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarPerfil(String nome) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		String usuario = servidorLogado != null ? servidorLogado.getUsuario() : null;
		
		Set<String> set = getICascaFacade().obterNomePerfisPorUsuario(usuario);
		for (String perfilUsuario : set) {
			if (perfilUsuario.equalsIgnoreCase(nome)) {
				return true;
			}
		}

		return false;
	}

	protected AmbulatorioTriagemRN getAmbulatorioTriagemRN() {
		return ambulatorioTriagemRN;
	}

	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	protected AacCidConsultasDAO getAacCidConsultasDAO() {
		return aacCidConsultasDAO;
	}

	protected AacCondicaoAtendimentoDAO getAacCondicaoAtendimentoDAO() {
		return aacCondicaoAtendimentoDAO;
	}

	protected AacHorarioGradeConsultaDAO getAacHorarioGradeConsultaDAO() {
		return aacHorarioGradeConsultaDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected AacGradeAgendamenConsultasDAO getAacGradeAgendamentoConsultasDAO() {
		return aacGradeAgendamenConsultasDAO;
	}

	protected AacGradeProcedHospitalarDAO getAacGradeProcedimentoHospitalarDAO() {
		return aacGradeProcedHospitalarDAO;
	}

	protected AacConsultaProcedHospitalarDAO getAacConsultaProcedHospitalarDAO() {
		return aacConsultaProcedHospitalarDAO;
	}

	protected AacSituacaoConsultasDAO getAacSituacaoConsultasDAO() {
		return aacSituacaoConsultasDAO;
	}

	protected ManterGradeAgendamentoRN getManterGradeAgendamentoRN() {
		return manterGradeAgendamentoRN;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected MamInterconsultasDAO getMamIterconsultasDAO() {
		return mamInterconsultasDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IExamesBeanFacade getExamesBeanFacade() {
		return this.examesBeanFacade;
	}

	protected AacRetornosDAO getAacRetornosDAO() {
		return aacRetornosDAO;
	}

	protected AmbulatorioAltaRN getAmbulatorioAltaRN() {
		return ambulatorioAltaRN;
	}

	protected AmbulatorioRN getAmbulatorioRN() {
		return ambulatorioRN;
	}

	protected AacConsultasJnDAO getAacConsultasJnDAO() {
		return aacConsultasJnDAO;
	}

	protected AacCaracteristicaGradeDAO getAacCaracteristicaGradeDAO() {
		return aacCaracteristicaGradeDAO;
	}

	protected AacRadioAcompTomosDAO getAacRadioAcompTomosDAO() {
		return aacRadioAcompTomosDAO;
	}

	protected AacFormaAgendamentoDAO getFormaAgendamentoDAO() {
		return aacFormaAgendamentoDAO;
	}

	protected VAacConvenioPlanoDAO getVAacConvenioPlanoDAO() {
		return vAacConvenioPlanoDAO;
	}

	/**
	 * Exclui consultas selecionadas
	 */
	public boolean excluirListaConsultas(List<Integer> consultas, Integer grdSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor, final boolean substituirProntuario) throws BaseException {
		Date menorData = new Date();
		
		for (Integer numero : consultas) {
			AacConsultas cons = aacConsultasDAO.obterPorChavePrimaria(numero);
			
			// Exclui somente consultas com situação 'Gerada'
			validarConsultaBeforeRowDelete(cons);
			
			if(cons.getDtConsulta().before(menorData)){
				menorData = cons.getDtConsulta();
			}
			
			excluirConsulta(cons, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
		}

		atualizarDtUltGeracaoHorarioGrade(grdSeq);
		atualizarDtUltGeracaoGradeConsulta(grdSeq, menorData);
		
		return true;
	}

	public boolean excluirConsultasAghAtendimento(Integer seqAtendimento, String nomeMicrocomputador, final Date dataFimVinculoServidor, final Boolean substituirProntuario) throws BaseException{
		AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(seqAtendimento);
		
		Set<AacConsultas> consultas = atendimento.getConsultas();
		
		for (AacConsultas consulta : consultas) {
			AacConsultas cons = aacConsultasDAO.obterPorChavePrimaria(consulta.getNumero());
			
			// Exclui somente consultas com situação 'Gerada'
			validarConsultaBeforeRowDelete(consulta);
			excluirConsulta(cons, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
		}

		return true;
	}

	/**
	 * Implementação da procedure de exclusão de consultas geradas
	 * 
	 * ORADB: AACP_EXCLUI_GERACAO
	 * 
	 * @param consulta
	 */
	public void excluirConsulta(AacConsultas consulta,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws BaseException {
		getAacConsultasDAO().remover(consulta);
		validarConsultaAfterDeleteRow(consulta, nomeMicrocomputador);
		// Condição necessária devido a operação de excluir geração.
		// A trigger AACT_CON_ASD só faz sentido quando a consulta é marcada e
		// excede programação.
		if (consulta.getSituacaoConsulta() != null
				&& consulta.getSituacaoConsulta().getSituacao() != null
				&& consulta.getSituacaoConsulta().getSituacao().equals("M")) {
			validarConsultaAfterDelete(consulta, null, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
		}
	}

	/**
	 * Comparator para ordenar consultas por data consulta (ordem crescente).
	 * 
	 * @author diego.pacheco
	 * 
	 */
	static class AacConsultaPorDataConsultaComparator implements
			Comparator<AacConsultas> {
		@Override
		public int compare(AacConsultas a1, AacConsultas a2) {
			int compDtConsulta = a1.getDtConsulta().compareTo(
					a2.getDtConsulta());
			return compDtConsulta;
		}
	}

	/**
	 * Procedure
	 * 
	 * Atualiza dt ultima geração nas horario grade consultas
	 * 
	 * ORADB: AACP_ATUA_DT_GER_AC
	 * 
	 */
	public void atualizarDtUltGeracaoHorarioGrade(Integer grdSeq) {
		AacHorarioGradeConsultaDAO horarioGradeConsultaDAO = getAacHorarioGradeConsultaDAO();
		Date horaFim = null;
		Date dtUltimaConsultaGrade = null;

		List<AacHorarioGradeConsulta> listaHoraGrdCons = horarioGradeConsultaDAO
				.buscarHoraGradeConsultaComDataUltimaGeracao(grdSeq);

		for (AacHorarioGradeConsulta horaGrdConsulta : listaHoraGrdCons) {
			if (horaGrdConsulta.getHoraFim() != null) {
				horaFim = horaGrdConsulta.getHoraFim();
			} else {
				horaFim = obterUltimoHorario(horaGrdConsulta.getHoraInicio(),
						horaGrdConsulta.getNumHorario(),
						horaGrdConsulta.getDuracao());
			}

			dtUltimaConsultaGrade = getAacConsultasDAO()
					.obterDataUltimaConsultaNaoExcedente(
							grdSeq,
							horaGrdConsulta.getHoraInicio(),
							horaFim,
							horaGrdConsulta.getDiaSemana(),
							horaGrdConsulta.getFormaAgendamento()
									.getCondicaoAtendimento().getSeq(),
							horaGrdConsulta.getFormaAgendamento()
									.getTipoAgendamento().getSeq(),
							horaGrdConsulta.getFormaAgendamento().getPagador()
									.getSeq(),
							horaGrdConsulta.getDtUltimaGeracao());

			if (dtUltimaConsultaGrade == null) {
				horaGrdConsulta.setDtUltimaGeracao(null);
			} else {
				horaGrdConsulta.setDtUltimaGeracao(dtUltimaConsultaGrade);
			}

			horarioGradeConsultaDAO.atualizar(horaGrdConsulta);
			horarioGradeConsultaDAO.flush();
		}
	}

	/**
	 * Procedure
	 * 
	 * Atualiza dt ultima geração nas grade consultas
	 * 
	 * ORADB: AACP_ATU_DTGER_GR_AC
	 */
	public void atualizarDtUltGeracaoGradeConsulta(Integer grdSeq,
			Date dtHrInicio) {
		Date dtUltimaConsultaGrade = getAacConsultasDAO()
				.obterDataUltConsultaNaoExcedentePorGradeDataInicio(grdSeq,
						dtHrInicio);
		AacGradeAgendamenConsultasDAO dao = getAacGradeAgendamentoConsultasDAO();
		AacGradeAgendamenConsultas grade = dao.obterGradeAgendamento(grdSeq);
		grade.setDtUltimaGeracao(dtUltimaConsultaGrade);
		dao.atualizar(grade);
		dao.flush();
	}

	/**
	 * Function
	 * 
	 * Retorna hora final considerando a data de inicio, o numero de horarios e
	 * a duracao
	 * 
	 * ORADB: AACC_VER_ULT_HRIO
	 */
	public Timestamp obterUltimoHorario(Date horaInicio, Short numHorario,
			Date duracao) {
		Date horaCorr = (Date) horaInicio.clone();
		Calendar calDuracao = Calendar.getInstance();
		calDuracao.setTime(duracao);
		float fracaoDias = (calDuracao.get(Calendar.HOUR_OF_DAY) * 60)
				+ (calDuracao.get(Calendar.MINUTE) / (24.0f * 60.0f));

		for (int i = 0; i < numHorario; i++) {
			horaCorr = DateUtil.adicionaDiasFracao(horaCorr, fracaoDias);
		}

		Calendar calHoraCorr = Calendar.getInstance();
		calHoraCorr.setTime(horaCorr);

		return new Timestamp(calHoraCorr.getTimeInMillis());
	}

	/**
	 * Trigger
	 * 
	 * ORADB: AACT_CON_BRD
	 * 
	 * @param consulta
	 */
	public void validarConsultaBeforeRowDelete(AacConsultas consulta)
			throws ApplicationBusinessException {
		verificarExclusaoConsulta(consulta);

		// verifica competencia fatura
		if (consulta.getSituacaoConsulta() != null
				&& consulta.getSituacaoConsulta().getSituacao() != null
				&& consulta.getSituacaoConsulta().getSituacao().equals("M")) {

			this.verificarConsultaFat(consulta.getNumero());
		}
	}

	/*
	 * Implementação da Trigger de Inserição da tabela AAC_CONSULTA ORADB:
	 * AACT_CON_BRI
	 */
	/**
	 * Trigger ORADB: AACT_CON_BRI
	 * @throws BaseException 
	 * 
	 * 
	 */
	// Trecho referente a UNIMED foi comentado por nao ser usado no momento
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public AacConsultas preInserirConsulta(AacConsultas consulta)
			throws BaseException {
		Integer codPaciente = null;
		if (consulta.getPaciente() != null) {
			codPaciente = consulta.getPaciente().getCodigo();
		}
		String situacaoConsulta = null;
		if (consulta.getSituacaoConsulta() != null) {
			situacaoConsulta = consulta.getSituacaoConsulta().getSituacao();
		}

		Integer serMatriculaCon = null;
		if (consulta.getServidorConsultado() != null
				&& consulta.getServidorConsultado().getId() != null) {
			serMatriculaCon = consulta.getServidorConsultado().getId()
					.getMatricula();
		}
		consulta.setCriadoEm(new Date());
		consulta.setAlteradoEm(new Date());
		consulta.setDiaSemana(CoreUtil.retornaDiaSemana(consulta
				.getDtConsulta()));

		if (consulta.getSituacaoConsulta()!=null	&& 
			consulta.getSituacaoConsulta().getSituacao()!=null &&
			"M".equals(consulta.getSituacaoConsulta().getSituacao())){
			consulta.setSituacaoConsulta(getAacSituacaoConsultasDAO().obterPorChavePrimaria("M"));
		}
		this.verificarSituacaoConsultaAtiva(consulta.getSituacaoConsulta());
		consulta.setRetorno(atualizarRetornoConsulta(
				consulta.getSituacaoConsulta(), null));
		if ("M".equals(consulta.getSituacaoConsulta().getSituacao())) {
			consulta.setDthrMarcacao(new Date());
			consulta.setExcedeProgramacao(Boolean.TRUE);
		}
		if (consulta.getExcedeProgramacao()) {
			verificarExtras(consulta.getFormaAgendamento());
		}
		this.verificarSituacaoNovo(consulta, null);
		Short caaSeq = null;
		Short tagSeq = null;
		Short pgdSeq = null;

		if (consulta.getFormaAgendamento() != null
				&& consulta.getFormaAgendamento().getId() != null) {
			caaSeq = consulta.getFormaAgendamento().getId().getCaaSeq();
			tagSeq = consulta.getFormaAgendamento().getId().getTagSeq();
			pgdSeq = consulta.getFormaAgendamento().getId().getPgdSeq();
		}
		this.verificarFormaAgendamento(consulta.getSituacaoConsulta(), caaSeq,
				tagSeq, pgdSeq);
		this.verificarSenha(consulta);
		Integer grdSeq = null;
		if (consulta.getGradeAgendamenConsulta() != null) {
			grdSeq = consulta.getGradeAgendamenConsulta().getSeq();
		}
		Short mtoSeq = null;
		if (consulta.getMotivo() != null) {
			mtoSeq = consulta.getMotivo().getCodigo();
		}

		if ("M".equals(consulta.getSituacaoConsulta().getSituacao())) {
			this.verificarAlteracaoFormaAgendamento(grdSeq, caaSeq, tagSeq,
					pgdSeq);
			this.verificarFormaAgendamentoEspecialidade(
					consulta.getFormaAgendamento(),
					consulta.getGradeAgendamenConsulta(),
					consulta.getDtConsulta());

			this.verificarConvenioSaudeProjeto2(consulta);
			if (this.verificarConsultaAnterior(consulta) != null) {
				AacConsultas consultaAnterior = this
						.getAacConsultasDAO()
						.obterConsulta(this.verificarConsultaAnterior(consulta));
				consulta.setConsulta(consultaAnterior);
			}
		}
		this.getManterGradeAgendamentoRN().verificaSituacaoGradeData(
				consulta.getGradeAgendamenConsulta(), consulta.getDtConsulta());
		Short cnvCodigo = null;
		Byte cspSeq = null;
		if (consulta.getConvenioSaudePlano() != null
				&& consulta.getConvenioSaudePlano().getId() != null) {
			cnvCodigo = consulta.getConvenioSaudePlano().getId().getCnvCodigo();
			cspSeq = consulta.getConvenioSaudePlano().getId().getSeq();
		}
		if (cnvCodigo != null || cspSeq != null) {
			this.verificarConsultaConvenioSituacao(consulta
					.getConvenioSaudePlano());
		}
		if (consulta.getAtendimento() != null) {
			this.verificarConsultaAtendimento(consulta.getAtendimento(),
					codPaciente);
		}
		this.verificarConsultaRetorno(situacaoConsulta, consulta.getRetorno());
		if (!StringUtils.isBlank(situacaoConsulta)
				&& "M".equals(situacaoConsulta)) {
			this.verificarEstatisticas(consulta.getDtConsulta());
			this.verificarDataFeriado(consulta.getDtConsulta(), grdSeq);
			this.verificarObitoPaciente(consulta);
			this.verificarIdadePaciente(consulta);
			if (serMatriculaCon != null) {
				this.verificarVinculoFuncionario(
						consulta.getServidorConsultado(),
						consulta.getGradeAgendamenConsulta());
			}
			this.verificarMinimaData(consulta.getDtConsulta(),
					consulta.getTpsTipo());
			if (consulta.getServidorAtendido() == null
					|| consulta.getServidorAtendido().getId() == null) {
				consulta.setServidorAtendido(this
						.atualizarProfissionalGrade(consulta
								.getGradeAgendamenConsulta()));
			}
		}
		if (consulta.getServidor() == null
				|| consulta.getServidor().getId() == null) {
			consulta.setServidor(this.getAmbulatorioRN().atualizaServidor(consulta
					.getServidor()));
		}
		if (consulta.getServidorAlterado() == null
				|| consulta.getServidorAlterado().getId() == null) {
			consulta.setServidorAlterado(this.getAmbulatorioRN().atualizaServidor(consulta.getServidorAlterado()));
		}
		this.atualizarProjetoPesquisa(consulta);
		if (!StringUtils.isBlank(situacaoConsulta)
				&& situacaoConsulta.equals("M")) {
			AacMotivos motivo = null;
			if (grdSeq != null && mtoSeq != null) {
				Short motivoId = obterMotivo(grdSeq, mtoSeq);
				if (motivoId != null) {
					motivo = this.getAacMotivosDAO().obterPorChavePrimaria(
							motivoId);
				}
			}
			consulta.setMotivo(motivo);
		}
		/*
		 * Fora do escopo atual IF :new.ind_sit_consulta = 'M' THEN IF
		 * :new.fag_pgd_seq = 1 THEN -- somente para pagador sus - Milena
		 * 03/2006 v_fag_pgd_seq := :new.fag_pgd_seq; v_csp_cnv_codigo :=
		 * :new.csp_cnv_codigo; v_csp_seq := :new.csp_seq; --
		 * AACK_CON_3_RN.RN_CONP_ATU_UNIMED(:new.GRD_SEQ ,:new.PAC_CODIGO
		 * ,:new.SER_MATRICULA_CONSULTADO ,:new.SER_VIN_CODIGO_CONSULTADO
		 * ,v_fag_pgd_seq ,v_csp_cnv_codigo ,v_csp_seq ,:new.dt_consulta
		 * ,:new.ind_funcionario); :new.fag_pgd_seq := v_fag_pgd_seq;
		 * :new.csp_cnv_codigo := v_csp_cnv_codigo; :new.csp_seq := v_csp_seq;
		 * END IF; end if;
		 */

		this.verificarProjeto(consulta.getProjetoPesquisa(),
				consulta.getPagador());
		this.verificarConvenioSaudeProjeto(consulta.getProjetoPesquisa(),
				consulta.getConvenioSaudePlano());
		if (consulta.getCodCentral() == null) {
			consulta.setCodCentral(this.obterCodigoCentral(codPaciente,
					consulta.getNumero(), consulta.getDtConsulta()));
		}
		return consulta;
	}

	/**
	 * Trigger
	 * 
	 * ORADB: AACT_CON_ASD
	 * 
	 * @param consultaAnterior
	 * @param consulta
	 */
	public void validarConsultaAfterDelete(AacConsultas consultaAnterior,
			AacConsultas consulta, String nomeMicrocomputador,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws BaseException {
		validarConsultasEnforce(consultaAnterior, consulta,
				DominioOperacaoBanco.DEL, false, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, true);
	}

	/**
	 * Trigger
	 * 
	 * ORADB: AACT_CON_ARD
	 * 
	 * @param consulta
	 * @param nomeMicrocomputador
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void validarConsultaAfterDeleteRow(AacConsultas consulta, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		/*
		 * interface faturamento - Milena 05/03/02 copiado da pll libera
		 * consulta
		 */
		// Regra ALTERAR_AACT_CON_ARD da estória #37942
		if (consulta.getAtendimento() != null) {
			getFaturamentoFacade().atualizarProcedimentosConsulta(consulta.getAtendimento().getSeq(), null, consulta.getNumero(), nomeMicrocomputador);
		}

		excluirMovimentacaoProntuario(consulta);

		AacConsultasJn jn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.DEL, AacConsultasJn.class, servidorLogado.getUsuario());
		jn.setNumero(consulta.getNumero());
		jn.setDtConsulta(consulta.getDtConsulta());
		jn.setCriadoEm(consulta.getCriadoEm());
		if (consulta.getServidor() != null
				&& consulta.getServidor().getId() != null) {
			jn.setSerMatricula(consulta.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(consulta.getServidor().getId().getVinCodigo());
		}
		if (consulta.getGradeAgendamenConsulta() != null) {
			jn.setGrdSeq(consulta.getGradeAgendamenConsulta().getSeq());
		}
		jn.setTpsTipo(consulta.getTpsTipo());
		if (consulta.getSituacaoConsulta() != null) {
			jn.setSituacaoConsulta(consulta.getSituacaoConsulta());
		}
		if (consulta.getPaciente() != null) {
			jn.setPacCodigo(consulta.getPaciente().getCodigo());
		}
		if (consulta.getConvenioSaudePlano() != null
				&& consulta.getConvenioSaudePlano().getId() != null) {
			jn.setCspCnvCodigo(consulta.getConvenioSaudePlano().getId()
					.getCnvCodigo());
			jn.setCspSeq(consulta.getConvenioSaudePlano().getId().getSeq());
		}
		if (consulta.getServidorConsultado() != null
				&& consulta.getServidorConsultado().getId() != null) {
			jn.setSerMatriculaConsultado(consulta.getServidorConsultado()
					.getId().getMatricula());
			jn.setSerVinCodigoConsultado(consulta.getServidorConsultado()
					.getId().getVinCodigo());
		}
		if (consulta.getServidorAlterado() != null
				&& consulta.getServidorAlterado().getId() != null) {
			jn.setSerMatriculaAlterado(consulta.getServidorAlterado().getId()
					.getMatricula());
			jn.setSerVinCodigoAlterado(consulta.getServidorAlterado().getId()
					.getVinCodigo());
		}

		if (consulta.getServidorMarcacao() != null
				&& consulta.getServidorMarcacao().getId() != null) {
			jn.setSerMatriculaMarcacao(consulta.getServidorMarcacao().getId()
					.getMatricula());
			jn.setSerVinCodigoMarcacao(consulta.getServidorMarcacao().getId()
					.getVinCodigo());
		}

		jn.setAlteradoEm(consulta.getAlteradoEm());
		if (consulta.getAtendimento() != null) {
			jn.setAtdSeq(consulta.getAtendimento().getSeq());
		}
		jn.setJustificativa(consulta.getJustificativa());
		if (consulta.getRetorno() != null) {
			jn.setRetSeq(consulta.getRetorno().getSeq());
		}
		if (consulta.getMotivo() != null) {
			jn.setMtoSeq(consulta.getMotivo().getCodigo());
		}
		jn.setDthrInicio(consulta.getDthrInicio());
		jn.setDthrFim(consulta.getDthrFim());
		jn.setCodCentral(consulta.getCodCentral());
		if (consulta.getCondicaoAtendimento() != null) {
			jn.setFagCaaSeq(consulta.getCondicaoAtendimento().getSeq());
		}
		if (consulta.getPagador() != null) {
			jn.setFagPgdSeq(consulta.getPagador().getSeq());
		}
		if (consulta.getTipoAgendamento() != null) {
			jn.setFagTagSeq(consulta.getTipoAgendamento().getSeq());
		}
		if (consulta.getConsulta() != null) {
			jn.setConNumero(consulta.getConsulta().getNumero());
		}
		if (consulta.getSituacaoConsulta() != null) {
			jn.setStcSituacao(consulta.getSituacaoConsulta().getSituacao());
		}
		jn.setPostoSaude(consulta.getPostoSaude());
		if (consulta.getServidorAtendido() != null
				&& consulta.getServidorAtendido().getId() != null) {
			jn.setSerMatriculaAtendido(consulta.getServidorAtendido().getId()
					.getMatricula());
			jn.setSerVinCodigoAtendido(consulta.getServidorAtendido().getId()
					.getVinCodigo());
		}
		jn.setGrupoAtendAmb(consulta.getGrupoAtendAmb());
		if (consulta.getProjetoPesquisa() != null) {
			jn.setPjqSeq(consulta.getProjetoPesquisa().getSeq());
		}

		getAacConsultasJnDAO().persistir(jn);
	}

	/**
	 * Function
	 * 
	 * ORADB: AACC_VER_CARACT_GRD
	 * 
	 * @param grdSeq
	 * @param caracteristica
	 */
	public Boolean verificarCaracteristicaGrade(Integer grdSeq,
			DominioCaracteristicaGrade caracteristica) {
		AacCaracteristicaGradeDAO aacCaracteristicaGradeDAO = getAacCaracteristicaGradeDAO();
		AacCaracteristicaGradeId id = new AacCaracteristicaGradeId(grdSeq,
				caracteristica.getDescricao());
		AacCaracteristicaGrade caractGrd = aacCaracteristicaGradeDAO
				.obterPorChavePrimaria(id);

		if (caractGrd != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Function AINC_LOC_UNID_EMG_CH
	 * 
	 * @param leitoID
	 * @param numeroQuarto
	 * @param unfSeq
	 * @return
	 */
	public Boolean verificarUnidadeEmergencia(String leitoID,
			Short numeroQuarto, Short unfSeq) {
		Short unidade;
		if (leitoID != null) {
			AinLeitos leito = this.getInternacaoFacade().obterLeitoPorId(
					leitoID);
			unidade = leito.getUnidadeFuncional().getSeq();
		} else if (numeroQuarto != null) {
			AinQuartos quarto = this.getInternacaoFacade().obterQuartoPorId(
					numeroQuarto);
			unidade = quarto.getUnidadeFuncional().getSeq();
		} else {
			unidade = unfSeq;
		}
		DominioSimNao retorno = this.getPesquisaInternacaoFacade()
				.verificarCaracteristicaDaUnidadeFuncional(unidade,
						ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);
		if (retorno.equals(DominioSimNao.S)) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean verificarEspecialidade(Short especialidadeGenericaSeq,
			Short especialidadeSeq, Short espSeq) {
		if (especialidadeGenericaSeq != null
				&& especialidadeGenericaSeq == espSeq) {
			return true;
		} else if (especialidadeGenericaSeq == null
				&& especialidadeSeq == espSeq) {
			return true;
		}
		return false;
	}

	/**
	 * ORADB PROCEDURE FATK_PMR_RN.FATP_PMR_TROCA_CONV
	 * 
	 * @param numero
	 * @param cspCnvCodigo
	 * @param cspSeq
	 */
	protected void verificarTrocaConvenio(Integer numero, Short cspCnvCodigo,
			Byte cspSeq) {

		FatConvenioSaudePlano convenioSaudePlano = this
				.getFaturamentoFacade().obterFatConvenioSaudePlano(
						cspCnvCodigo, cspSeq);

		// se o convenio for sus
		if (DominioGrupoConvenio.S.equals(convenioSaudePlano.getConvenioSaude().getGrupoConvenio())) {

			// se tipo for != ambulatorio
			if (DominioTipoPlano.A != convenioSaudePlano.getIndTipoPlano()) {
				FatProcedAmbRealizado procedAmbRealizado = this
						.getFaturamentoFacade()
						.obterFatProcedAmbRealizadoPorPrhConNumero(numero);
				if (procedAmbRealizado != null) {
					procedAmbRealizado.setConvenioSaudePlano(convenioSaudePlano);
					procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
					this.getFaturamentoFacade().atualizarProcedAmbRealizado(procedAmbRealizado);
				}
			} else {
				// tipo ambulatorio
				FatProcedAmbRealizado procedAmbRealizado = this
						.getFaturamentoFacade()
						.obterFatProcedAmbRealizadoPorPrhConNumero(numero);
				if (procedAmbRealizado != null) {
					procedAmbRealizado
							.setConvenioSaudePlano(convenioSaudePlano);
					procedAmbRealizado
							.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
					this.getFaturamentoFacade().atualizarProcedAmbRealizado(
							procedAmbRealizado);
				}
			}
		} else {
			FatProcedAmbRealizado procedAmbRealizado = this
					.getFaturamentoFacade()
					.obterFatProcedAmbRealizadoPorPrhConNumero(numero);
			if (procedAmbRealizado != null) {
				procedAmbRealizado
						.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
				this.getFaturamentoFacade().atualizarProcedAmbRealizado(
						procedAmbRealizado);
			}
		}
	}

	/**
	 * Deleta o faturamento do procedimento de consulta informado. Caso ocorra
	 * uma exceção ao deletar, será executado um update passando os
	 * procedimentos da consulta para a situação Cancelado.
	 * 
	 * ORADB: DELETA_PMR
	 * 
	 * @param conNumero
	 * @param phiSeq
	 */
	protected void deletarFaturamento(Integer conNumero, Integer phiSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();

		try {
			List<FatProcedAmbRealizado> listaProcedimentosAmbulatoriaisRealizados = faturamentoFacade
					.buscarPorNumeroConsultaEProcedHospInternos(conNumero,
							phiSeq);

			if (listaProcedimentosAmbulatoriaisRealizados != null
					&& !listaProcedimentosAmbulatoriaisRealizados.isEmpty()) {
				for (FatProcedAmbRealizado procedimentoAmbulatorialRealizado : listaProcedimentosAmbulatoriaisRealizados) {
					faturamentoFacade
							.removerProcedimentosAmbulatoriaisRealizados(procedimentoAmbulatorialRealizado, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		} catch (Exception e) {
			logError("Exceção lançada: ", e);
			DominioSituacaoProcedimentoAmbulatorio[] situacoesIgnoradas = new DominioSituacaoProcedimentoAmbulatorio[] {
					DominioSituacaoProcedimentoAmbulatorio.APRESENTADO,
					DominioSituacaoProcedimentoAmbulatorio.ENCERRADO };

			List<FatProcedAmbRealizado> listaProcedimentosAmbulatoriaisRealizados = faturamentoFacade
					.listarProcedimentosAmbulatoriaisRealizados(conNumero,
							situacoesIgnoradas);

			if (listaProcedimentosAmbulatoriaisRealizados != null
					&& !listaProcedimentosAmbulatoriaisRealizados.isEmpty()) {
				for (FatProcedAmbRealizado procedimentoAmbulatorialRealizado : listaProcedimentosAmbulatoriaisRealizados) {
					FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade
							.clonarFatProcedAmbRealizado(procedimentoAmbulatorialRealizado);

					procedimentoAmbulatorialRealizado
							.setConsultaProcedHospitalar(null);
					procedimentoAmbulatorialRealizado
							.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
					procedimentoAmbulatorialRealizado.setAtendimento(null);

					faturamentoFacade
							.atualizarProcedimentosAmbulatoriaisRealizados(
									procedimentoAmbulatorialRealizado,
									oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
				}
			} else {
				throw new ApplicationBusinessException(
						LiberarConsultasONExceptionCode.FAT_00512);
			}
		}

	}

	/**
	 * Deleta o faturamento do procedimento de consulta informado.
	 * 
	 * @param conNumero
	 * @param phiSeq
	 */
	protected void deletarFaturamentoSemCapturaExcecao(Integer conNumero,
			Integer phiSeq, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();

		List<FatProcedAmbRealizado> listaProcedimentosAmbulatoriaisRealizados = faturamentoFacade
				.buscarPorNumeroConsultaEProcedHospInternos(conNumero, phiSeq);

		if (listaProcedimentosAmbulatoriaisRealizados != null
				&& !listaProcedimentosAmbulatoriaisRealizados.isEmpty()) {
			for (FatProcedAmbRealizado procedimentoAmbulatorialRealizado : listaProcedimentosAmbulatoriaisRealizados) {
				faturamentoFacade.removerProcedimentosAmbulatoriaisRealizados(
						procedimentoAmbulatorialRealizado, nomeMicrocomputador,
						dataFimVinculoServidor);
			}
		}
	}

	/**
	 * ORADB: trigger FATP_PMR_TROCA_GRD
	  * @param numero
	 * @param grdSeq
	 * @param nomeMicrocomputador
	 * @param dataFimVinculoServidor
	 * @throws BaseException
	 */
	
	public void realizarTrocaGrade(Integer numero,Integer grdSeq,String nomeMicrocomputador,final Date dataFimVinculoServidor) throws BaseException{
				
		AacGradeAgendamenConsultas grade = getAacGradeAgendamentoConsultasDAO().obterPorChavePrimariaEspecialidadeUnidFuncionalSala(grdSeq);

		List<FatProcedAmbRealizado> fatProcedAmbRealizados= getFaturamentoFacade().listarFatProcedAmbRealizadoPorPrhConNumero(numero);
			
		for(FatProcedAmbRealizado fatProcedAmbRealizado: fatProcedAmbRealizados ){
			FatProcedAmbRealizado oldProcedAmbRealizado = getFaturamentoFacade().clonarFatProcedAmbRealizado(fatProcedAmbRealizado);
			fatProcedAmbRealizado.setUnidadeFuncional(grade.getUnidadeFuncional());
			fatProcedAmbRealizado.setEspecialidade(grade.getEspecialidade());
				getFaturamentoFacade().atualizarProcedimentoAmbulatorialRealizado(
						fatProcedAmbRealizado, oldProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
				
		}
		
	}
	
	/**Verifica se servidor tem Unimed
	 * @ORADB: RAPC_VESETEM_UNIMED
	 * #42229
	 */
	public String verificaServidorTemUnimed(Integer matricula, Short vinCodigo, Integer depCodigo, Integer prontuario, Date cData) {
		Long carteira = null;
		Date data = null;
		if (cData != null) {
			data = DateUtil.truncaData(cData);
		}
		String sData = DateUtil.obterDataFormatada(data, "dd/MM/yyyy");
		if (data.compareTo(DateUtil.truncaData(DateUtil.obterData(2010, 10, 01))) < 0) {
			carteira = verificaPossuiUnimed(matricula, vinCodigo, depCodigo, prontuario, carteira, sData);		
		}else{
			carteira = verificaPossuiUnimedStarh(matricula, vinCodigo, depCodigo, prontuario, carteira, sData);
		}
		if (carteira == null) {
			return "N";
		} else if (carteira == 0) {
			return "S";
		} else {
			return carteira.toString();
		}
	}

	/**
	 * 
	 * #42229 Verifica se o servidor ou dependente possui unimed
	 * @param matricula
	 * @param vinCodigo
	 * @param depCodigo
	 * @param prontuario
	 * @param carteira
	 * @param sData
	 * @return
	 */
	private Long verificaPossuiUnimed(Integer matricula, Short vinCodigo, Integer depCodigo, Integer prontuario, Long carteira, String sData) {
		if ((matricula != null && vinCodigo != null) && depCodigo == null) {
			// c_ser
			carteira = getRapUniServPlanoDAO().obterNroCarteiraPorMatricula(matricula, vinCodigo, sData);
		} else if (prontuario != null) {
			// c_ser2
			carteira = getRapUniServPlanoDAO().obterNroCarteiraPorProntuario(prontuario, sData);
			if (carteira == null) {
				// c_dep2
				carteira = getRapUniServDependenteDAO().obterNroCarteiraDependenteProntuario(prontuario,sData);
			}
		}else if (matricula != null && vinCodigo != null && depCodigo != null) {
			// c_dep
			carteira = getRapUniServDependenteDAO().obterNroCarteiraDependenteMatricula(matricula,vinCodigo,depCodigo,sData);
		}
		return carteira;
	}
	/**
	 * #42229 Verifica se dependente ou servidor possui unimed na starh
	 * @param matricula
	 * @param vinCodigo
	 * @param depCodigo
	 * @param prontuario
	 * @param carteira
	 * @param sData
	 * @return
	 */
	private Long verificaPossuiUnimedStarh(Integer matricula, Short vinCodigo, Integer depCodigo, Integer prontuario, Long carteira, String sData) {
		String retorno=null;
		if((matricula != null && vinCodigo != null) && depCodigo == null) {
			// c_ser_starth
			retorno = getRhfp0283DAO().obterNroCarteiraPorMatricula(matricula, vinCodigo, sData);
		} else if (prontuario != null) {
			// c_ser2_starth
			retorno = getRhfp0283DAO().obterNroCarteiraPorProntuario(prontuario, sData);
			if (retorno == null) {
				// c_dep2_starth
				retorno = getRhfp0285DAO().obterNroCarteiraDependenteProntuario(prontuario,sData);
			}
		} else if (matricula != null && vinCodigo != null && depCodigo != null) {
			// c_dep_starth
			retorno = getRhfp0285DAO().obterNroCarteiraDependenteMatricula(matricula,vinCodigo,depCodigo,sData);
		}
		Long nroCarteira=null;
		if(retorno != null){
			nroCarteira = Long.valueOf(retorno);
		}
		return nroCarteira;
	}
	/** #42229
	 * @ORADB: AACP_ATU_LCTO_SMO
	 * @param consulta
	 * @param consultaAnterior
	 * @throws ApplicationBusinessException
	 */
	public void atualizaLicitacaoSMO(AacConsultas consulta,AacConsultas consultaAnterior) throws ApplicationBusinessException{
		Short cnvCodigoNew = consulta.getConvenioSaudePlano().getId().getCnvCodigo();
		Short cnvCodigoOld = consultaAnterior.getConvenioSaudePlano().getId().getCnvCodigo();
		Integer number = consulta.getNumero();
		Date data = consulta.getDtConsulta();
		if(getPacienteFacade().modificados(cnvCodigoOld, cnvCodigoNew)){
			FatConvenioSaude convenioOld = getFatConvenioSaudeDAO().obterPorChavePrimaria(cnvCodigoOld);
			FatConvenioSaude convenioNew = getFatConvenioSaudeDAO().obterPorChavePrimaria(cnvCodigoNew);
			if(convenioOld!= null && convenioNew!= null){
				if(convenioOld.getGrupoConvenio() == DominioGrupoConvenio.C && convenioNew.getGrupoConvenio() == DominioGrupoConvenio.S){
					verificaSMO("exclui",data,number);
				}else if(convenioOld.getGrupoConvenio() == DominioGrupoConvenio.S && convenioNew.getGrupoConvenio() == DominioGrupoConvenio.C){
					verificaSMO("inclui",data,number);
				}
			}
		}
	}
	/**
	 * #42229 utilizado por AACP_ATU_LCTO_SMO
	 * @param opcao
	 * @param data
	 * @param pNumero
	 * @throws ApplicationBusinessException
	 */
	private void verificaSMO(String opcao,Date data,Integer pNumero) throws ApplicationBusinessException{
		Short oldSitCodigo= null;
		Short newSitCodigo=null;
		String pData =  DateUtil.obterDataFormatada(data, "dd/MM/yyyy");
		if(opcao.equals("inclui")){
			oldSitCodigo = 2;
			newSitCodigo = 1;
		}else if(opcao.equals("exclui")){
			oldSitCodigo = 1;
			newSitCodigo = 2;
		}
		if(oldSitCodigo != null){
			if(getObjetosOracleDAO().isOracle()){
				getObjetosOracleDAO().ffcInterfaceAACPRJ(pData, pNumero, oldSitCodigo, newSitCodigo, null, null,this.servidorLogadoFacade.obterServidorLogado().getUsuario());
			}
		}
	}
	
	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}

	protected FatContaApacDAO getFatContaApacDAO() {
		return fatContaApacDAO;
	}

	protected AacProcedHospEspecialidadesDAO getAacProcedHospEspecialidadesDAO() {
		return aacProcedHospEspecialidadesDAO;
	}

	protected AacNivelBuscaDAO getAacNivelBuscaDAO() {
		return aacNivelBuscaDAO;
	}

	
	protected AacMotivosDAO getAacMotivosDAO() {
		return aacMotivosDAO;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return this.prescricaoMedicaFacade;
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	protected MarcacaoConsultaRN getMarcacaoConsultaRN() {
		return marcacaoConsultaRN;
	}

	public ProcedimentoConsultaRN getProcedimentoConsultaRN() {
		return procedimentoConsultaRN;
	}

	protected AacFormaEspecialidadeDAO getAacFormaEspecialidadeDAO() {
		return aacFormaEspecialidadeDAO;
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	private LiberarConsultasON getLiberarConsultasON() {
		return liberarConsultasON;
	}

	protected AacRadioAcompTomosRN getAacRadioAcompTomosRN() {
		return aacRadioAcompTomosRN;
	}
	
	public AelProjetoPesquisasDAO getAelProjetoPesquisasDAO() {
		return aelProjetoPesquisasDAO;
	}
	
	public FatConvenioSaudeDAO getFatConvenioSaudeDAO() {
		return fatConvenioSaudeDAO;
	}
	
	public RapUniServPlanoDAO getRapUniServPlanoDAO() {
		return rapUniServPlanoDAO;
}

	public RapUniServDependenteDAO getRapUniServDependenteDAO() {
		return this.rapUniServDependenteDAO;
	}

	public Rhfp0283DAO getRhfp0283DAO() {
		return rhfp0283DAO;
	}

	public Rhfp0285DAO getRhfp0285DAO() {
		return rhfp0285DAO;
	}

	public AghCaractUnidFuncionaisDAO getAghCaractUnidFuncionaisDAO() {
		return aghCaractUnidFuncionaisDAO;
	}

	public void setAghCaractUnidFuncionaisDAO(AghCaractUnidFuncionaisDAO aghCaractUnidFuncionaisDAO) {
		this.aghCaractUnidFuncionaisDAO = aghCaractUnidFuncionaisDAO;
	}
}
