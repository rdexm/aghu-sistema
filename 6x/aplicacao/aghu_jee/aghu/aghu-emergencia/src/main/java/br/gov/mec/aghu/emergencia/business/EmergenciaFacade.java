package br.gov.mec.aghu.emergencia.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.Hibernate;

import br.gov.mec.aghu.administracao.vo.MicroComputador;
import br.gov.mec.aghu.ambulatorio.dao.MamItemExameDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemMedicacaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.blococirurgico.vo.SalaCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.TipoAnestesiaVO;
import br.gov.mec.aghu.configuracao.vo.CidVO;
import br.gov.mec.aghu.configuracao.vo.EquipeVO;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.configuracao.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.controlepaciente.vo.EcpItemControleVO;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteServiceVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoFormularioAlta;
import br.gov.mec.aghu.dominio.DominioTipoIndicacaoNascimento;
import br.gov.mec.aghu.emergencia.dao.MamCaractSitEmergDAO;
import br.gov.mec.aghu.emergencia.dao.MamDescritorDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgEspecialidadesDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgServEspCoopDAO;
import br.gov.mec.aghu.emergencia.dao.MamExtratoTriagemDAO;
import br.gov.mec.aghu.emergencia.dao.MamFluxogramaDAO;
import br.gov.mec.aghu.emergencia.dao.MamGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamItemGeralDAO;
import br.gov.mec.aghu.emergencia.dao.MamOrigemPacienteDAO;
import br.gov.mec.aghu.emergencia.dao.MamProtClassifRiscoDAO;
import br.gov.mec.aghu.emergencia.dao.MamRegistrosDAO;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.dao.MamTipoCooperacaoDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidAtendeEspDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidAtendemDAO;
import br.gov.mec.aghu.emergencia.vo.AgrupamentoEspecialidadeEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.BoletimAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.CalculoCapurroVO;
import br.gov.mec.aghu.emergencia.vo.DesbloqueioConsultaCOVO;
import br.gov.mec.aghu.emergencia.vo.DescritorTrgGravidadeVO;
import br.gov.mec.aghu.emergencia.vo.DiagnosticoFiltro;
import br.gov.mec.aghu.emergencia.vo.DiagnosticoVO;
import br.gov.mec.aghu.emergencia.vo.EspecialidadeEmergenciaUnidadeVO;
import br.gov.mec.aghu.emergencia.vo.EspecialidadeEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.FiltroVerificaoInclusaoAnamneseVO;
import br.gov.mec.aghu.emergencia.vo.FormularioEncExternoVO;
import br.gov.mec.aghu.emergencia.vo.IntegracaoEmergenciaAghuAGHWebVO;
import br.gov.mec.aghu.emergencia.vo.ItemObrigatorioVO;
import br.gov.mec.aghu.emergencia.vo.MamCapacidadeAtendVO;
import br.gov.mec.aghu.emergencia.vo.MamPacientesAguardandoAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.MamPacientesAtendidosVO;
import br.gov.mec.aghu.emergencia.vo.MamTriagemVO;
import br.gov.mec.aghu.emergencia.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.emergencia.vo.PacienteEmAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.RegistrarExameFisicoVO;
import br.gov.mec.aghu.emergencia.vo.ServidorEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.ServidorEspecialidadeEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.TrgGravidadeFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ExameMaterialAnaliseVO;
import br.gov.mec.aghu.exames.vo.ExameSignificativoVO;
import br.gov.mec.aghu.exames.vo.RegiaoAnatomicaVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoVO;
import br.gov.mec.aghu.farmacia.vo.ViaAdministracaoVO;
import br.gov.mec.aghu.internacao.vo.LeitoVO;
import br.gov.mec.aghu.internacao.vo.UnidadeFuncional;
import br.gov.mec.aghu.model.MamCaractSitEmerg;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamEmgAgrupaEsp;
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.MamEmgServEspCoop;
import br.gov.mec.aghu.model.MamEmgServEspecialidade;
import br.gov.mec.aghu.model.MamEmgServidor;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamItemGeral;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MamOrigemPaciente;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTipoCooperacao;
import br.gov.mec.aghu.model.MamTrgExames;
import br.gov.mec.aghu.model.MamTrgGerais;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.model.MamTrgMedicacoes;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendeEsp;
import br.gov.mec.aghu.model.MamUnidAtendeEspId;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.McoAchado;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoAnamneseEfsId;
import br.gov.mec.aghu.model.McoApgars;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoAtendTrabPartosId;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.McoDiagnostico;
import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascido;
import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascidoId;
import br.gov.mec.aghu.model.McoExameExterno;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartos;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.model.McoProcedimentoObstetricos;
import br.gov.mec.aghu.model.McoProfNascs;
import br.gov.mec.aghu.model.McoProfNascsId;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoRecemNascidosId;
import br.gov.mec.aghu.model.McoSindrome;
import br.gov.mec.aghu.model.McoSnappes;
import br.gov.mec.aghu.model.McoTabAdequacaoPeso;
import br.gov.mec.aghu.model.McoTabBallard;
import br.gov.mec.aghu.model.McoTrabPartos;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistObstetricaVO;
import br.gov.mec.aghu.paciente.vo.DadosSanguineos;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.paciente.vo.PacienteProntuarioConsulta;
import br.gov.mec.aghu.perinatologia.business.ApgarON;
import br.gov.mec.aghu.perinatologia.business.GestacaoNascimentoSumarioDefinitivoON;
import br.gov.mec.aghu.perinatologia.business.ManterCadastroCondutaRN;
import br.gov.mec.aghu.perinatologia.business.ManterCadastroExamesSignificativosRN;
import br.gov.mec.aghu.perinatologia.business.ManterCadastroIndicacaoNascimentoON;
import br.gov.mec.aghu.perinatologia.business.ManterMcoProcedimentoObstetricoRN;
import br.gov.mec.aghu.perinatologia.business.McoAchadoON;
import br.gov.mec.aghu.perinatologia.business.McoAnamneseEfsRN;
import br.gov.mec.aghu.perinatologia.business.McoAtendTrabPartosRN;
import br.gov.mec.aghu.perinatologia.business.McoDiagnosticoRN;
import br.gov.mec.aghu.perinatologia.business.McoEscalaLeitoRecemNascidoRN;
import br.gov.mec.aghu.perinatologia.business.McoExameExternoRN;
import br.gov.mec.aghu.perinatologia.business.McoIntercorrenciaNascsRN;
import br.gov.mec.aghu.perinatologia.business.McoIntercorrenciaRN;
import br.gov.mec.aghu.perinatologia.business.McoProfNascsRN;
import br.gov.mec.aghu.perinatologia.business.McoTabAdequacaoPesoRN;
import br.gov.mec.aghu.perinatologia.business.MedicamentoRecemNascidoRN;
import br.gov.mec.aghu.perinatologia.business.NotaAdicionalRN;
import br.gov.mec.aghu.perinatologia.business.PesquisaGestacoesRN;
import br.gov.mec.aghu.perinatologia.business.RecemNascidoON;
import br.gov.mec.aghu.perinatologia.business.RegistrarConsultaCOON;
import br.gov.mec.aghu.perinatologia.business.RegistrarConsultaCORN;
import br.gov.mec.aghu.perinatologia.business.RegistrarGestacaoAbaExtFisicoRNON;
import br.gov.mec.aghu.perinatologia.business.RegistrarGestacaoAbaNascimentoON;
import br.gov.mec.aghu.perinatologia.business.RegistrarGestacaoAbaNascimentoRN;
import br.gov.mec.aghu.perinatologia.business.RegistrarGestacaoAbaTrabalhoPartoRN;
import br.gov.mec.aghu.perinatologia.business.RegistrarGestacaoON;
import br.gov.mec.aghu.perinatologia.business.RegistrarGestacaoRN;
import br.gov.mec.aghu.perinatologia.business.SnappeON;
import br.gov.mec.aghu.perinatologia.business.SnappeRN;
import br.gov.mec.aghu.perinatologia.dao.McoAchadoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoAnamneseEfsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoApgarsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoAtendTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoBolsaRotasDAO;
import br.gov.mec.aghu.perinatologia.dao.McoDiagnosticoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoExameExternoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIndicacaoNascimentoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIntercorrenciaDAO;
import br.gov.mec.aghu.perinatologia.dao.McoLogImpressoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoMedicamentoTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascIndicacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoProcedimentoObstetricosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoSindromeDAO;
import br.gov.mec.aghu.perinatologia.dao.McoSnappesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTabAdequacaoPesoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTabBallardDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.vo.AchadoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosGestacaoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoSelecionadoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoVO;
import br.gov.mec.aghu.perinatologia.vo.DataNascimentoVO;
import br.gov.mec.aghu.perinatologia.vo.EscalaLeitoRecemNascidoVO;
import br.gov.mec.aghu.perinatologia.vo.ExameResultados;
import br.gov.mec.aghu.perinatologia.vo.IntercorrenciaNascsVO;
import br.gov.mec.aghu.perinatologia.vo.IntercorrenciaVO;
import br.gov.mec.aghu.perinatologia.vo.MedicamentoRecemNascidoVO;
import br.gov.mec.aghu.perinatologia.vo.RecemNascidoFilterVO;
import br.gov.mec.aghu.perinatologia.vo.RecemNascidoVO;
import br.gov.mec.aghu.perinatologia.vo.ResultadoExameSignificativoPerinatologiaVO;
import br.gov.mec.aghu.perinatologia.vo.SnappeElaboradorVO;
import br.gov.mec.aghu.perinatologia.vo.TabAdequacaoPesoPercentilVO;
import br.gov.mec.aghu.perinatologia.vo.UnidadeExamesSignificativoPerinatologiaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PostoSaude;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

/**
 * Fachada do módulo EMERGENCIA
 * 
 * @author frutkowski
 *
 */
@Stateless
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects"})
public class EmergenciaFacade extends BaseFacade implements IEmergenciaFacade {

	private static final long serialVersionUID = -3544938370662118702L;

	//private static final Log LOG = LogFactory.getLog(EmergenciaFacade.class);
	
	@EJB
	private UnidadeFuncionalON unidadeFuncionalON;
	
	@EJB
	private PacientesEmergenciaON pacientesEmergenciaON;
	
	@EJB
	private PacientesEmergenciaEmAtendimentoON pacientesEmergenciaEmAtendimentoON;
	
	@EJB
	private RealizarAcolhimentoON realizarAcolhimentoON;
	
	@EJB
	private MamUnidAtendemRN mamUnidAtendemRN;
	
	@EJB
	private MamUnidAtendeEspsRN mamUnidAtendeEspsRN;
	
	@EJB 
	private MarcarConsultaEmergenciaON marcarConsultaEmergenciaON;
	
	@EJB 
	private MamProtClassifRiscoRN mamProtClassifRiscoRN;
	
	@EJB 
	private MamFluxogramaRN mamFluxogramaRN;
	
	@EJB 
	private MamTrgEncExternoRN mamTrgEncExternoRN;
		
	@EJB 
	private MamDescritorRN mamDescritorRN;
	
	@EJB 
	private MamGravidadeRN mamGravidadeRN;
	
	@EJB
	private MamTrgGeralRN mamTrgGeralRN;
	
	@EJB
	private MamTrgExameRN mamTrgExameRN;
	
	@EJB
	private MamTrgMedicacaoRN mamTrgMedicacaoRN;
	
	@Inject
	private MamUnidAtendemDAO mamUnidAtendemDAO;
	
	@Inject
	private MamUnidAtendeEspDAO mamUnidAtendeEspDAO;
	
	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;
	
	@Inject
	private MamCaractSitEmergDAO mamCaractSitEmergDAO;
	
	@Inject
	private MamProtClassifRiscoDAO mamProtClassifRiscoDAO;
	
	@Inject
	private MamEmgServEspCoopDAO mamEmgServEspCoopDAO;
	
	@Inject
	private MamTipoCooperacaoDAO mamTipoCooperacaoDAO;
	
	@Inject
	private MamFluxogramaDAO mamFluxogramaDAO;
	
	@Inject
	private MamDescritorDAO mamDescritorDAO;
	
	@Inject
	private MamGravidadeDAO mamGravidadeDAO;
	
	@Inject
	private MamItemExameDAO mamItemExameDAO;
	
	@Inject
	private MamItemMedicacaoDAO mamItemMedicacaoDAO;
	
	@Inject
	private MamItemGeralDAO mamItemGeralDAO;
	
	@Inject
	private MamOrigemPacienteDAO mamOrigemPacienteDAO;
	
	@Inject
	private MamEmgEspecialidadesDAO mamEmgEspecialidadesDAO;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	@Inject
	private MamExtratoTriagemDAO mamExtratoTriagemDAO;
	
	@Inject
	private MamRegistrosDAO mamRegistrosDAO;
	
	@Inject
	private McoLogImpressoesDAO mcoLogImpressoesDAO;
	
	@EJB
	private MamSituacaoEmergenciaRN mamSituacaoEmergenciaRN;
	
	@EJB
	private MamEmgEspecialidadesRN mamEmgEspecialidadesRN;
	
	@EJB
	private MarcarConsultaEmergenciaRN marcarConsultaEmergenciaRN;
	
	@EJB
	private MamEmgServidorRN mamEmgServidorRN;
	
	@EJB 
	private MamOrigemPacienteRN mamOrigemPacienteRN;
	
	@EJB
	private MamObrigatoriedadeRN mamObrigatoriedadeRN;

	@EJB
	private MamItemSinalVitalRN mamItemSinalVitalRN;

	@EJB
	private MamTrgGravidadeRN mamTrgGravidadeRN;

	@EJB
	private McoLogImpressoesRN mcoLogImpressoesRN;
	
	@EJB
	private BoletimAtentimentoON boletimAtentimentoON;
	
	@EJB
	private CapacidadeAtendimentoON capacidadeAtendimentoON;
	
	@EJB
	private CapacidadeAtendimentoRN capacidadeAtendimentoRN;

	@EJB
	private FormularioEncaminhamentoExternoON formularioEncaminhamentoExternoON;
	
	@EJB
	private ControlesPacientesON controlesPacientesON;
	
	@EJB
	private MamTrgEncInternoRN mamTrgEncInternoRN;
	
	@EJB
	private PacientesEmergenciaAtendidosON pacientesAtendidosON;
	
	@EJB
	private AtenderPacienteEmergenciaON atenderPacienteEmergenciaON;

	// ----- DAOS
	@Inject
	private McoGestacoesDAO mcoGestacoesDAO;
	
	@Inject
	private McoIndicacaoNascimentoDAO mcoIndicacaoNascimentoDAO;

	@Inject
	private McoProcedimentoObstetricosDAO procedimentoObstetricosDAO;
	
	@Inject
	private McoAtendTrabPartosDAO atendTrabPartoDAO;
	
	@Inject
	private McoMedicamentoTrabPartosDAO mcoMedicamentoTrabPartosDAO;
	
	@EJB
	private RegistrarGestacaoRN registrarGestacaoRN;
	
	@EJB
	private McoAnamneseEfsRN mcoAnamneseEfsRN;
	
	@EJB
	private RegistrarGestacaoAbaNascimentoON registrarGestacaoAbaNascimentoON;
	
	@EJB
	private GestacaoNascimentoSumarioDefinitivoON gestacaoNascimentoSumarioDefinitivoON;
	
	@Inject
	private McoDiagnosticoDAO diagnosticoDAO;
	
	@Inject
	private McoExameExternoDAO exameExternoDAO;
	
	@Inject
	private McoNascIndicacoesDAO mcoNascIndicacoesDAO;
	
	@Inject
	private McoIntercorrenciaDAO mcoIntercorrenciaDAO;
	
	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;
	
	@Inject
	private McoAnamneseEfsDAO mcoAnamneseEfsDAO;
	
	@Inject
	private McoTrabPartosDAO mcoTrabPartosDAO;
	
	// ----- ONS e RNS
	@EJB
	private PesquisaGestacoesRN pesquisaGestacoesRN;
	
	@EJB
	private ManterMcoProcedimentoObstetricoRN manterMcoProcedimentoObstetricoRN;
	
	@EJB
	private ManterCadastroIndicacaoNascimentoON manterCadastroIndicacaoNascimentoON;
	
	@EJB
	private McoDiagnosticoRN diagnosticoRN;
	
	@EJB
	private McoExameExternoRN mcoExameExternoRN;
	
	@EJB
	private McoAtendTrabPartosRN atendTrabPartosRN;

	@EJB
	private RegistrarGestacaoON registrarGestacaoON;
	
	@EJB 
	private ManterCadastroExamesSignificativosRN cadastroExamesSignificativosRN;
	
	@EJB
	private RegistrarConsultaCOON consultaCOON;
	
	@EJB
	private RegistrarConsultaCORN consultaCORN;
	
	@EJB
	private ManterCadastroCondutaRN cadastroCondutaRN;
	
	@EJB
	private McoIntercorrenciaNascsRN mcoIntercorrenciaNascsRN;
	
	@EJB
	private McoIntercorrenciaRN mcoIntercorrenciaRN;
	
	@EJB
	private McoProfNascsRN mcoProfNascsRN;
	
	@EJB
	private RegistrarGestacaoAbaTrabalhoPartoRN registrarGestacaoAbaTrabalhoPartoRN;
	
	@EJB
	private FinalizarConsultaRN finalizarConsultaRN;
	
	@EJB
	private RealizarInternacaoON realizarInternacaoON;
	
	@EJB
	private NotaAdicionalRN notaAdicionalRN;
		
	@Inject
	private McoBolsaRotasDAO mcoBolsaRotasDAO;
	
	@EJB
	private RegistrarGestacaoAbaExtFisicoRNON registrarGestacaoAbaExtFisicoRNON;
	
	@EJB
	private RegistrarGestacaoAbaNascimentoRN registrarGestacaoAbaNascimentoRN;
	
	@EJB
	private RecemNascidoON recemNascidoON;
	
	@EJB
	private McoAchadoON mcoAchadoON;
	
	@Inject
	private McoAchadoDAO mcoAchadoDAO;
	
	@Inject
	private McoSindromeDAO mcoSindromeDAO;
	
	@EJB
	private McoEscalaLeitoRecemNascidoRN mcoEscalaLeitoRecemNascidoRN;
	
	@Inject
	private McoTabBallardDAO mcoTabBallardDAO;
	
	@Inject
	private McoRecemNascidosDAO mcoRecemNascidosDAO;

	@Inject
	private McoApgarsDAO mcoApgarsDAO;
	
	@Inject
	private McoSnappesDAO mcoSnappesDAO;
	
	@EJB
	private ApgarON apgarON;
	
	@EJB
	private MedicamentoRecemNascidoRN medicamentoRecemNascidoRN;
	
	@EJB
	private SnappeON snappeON;
	
	@EJB
	private SnappeRN snappeRN;
	
	@EJB
	private McoTabAdequacaoPesoRN mcoTabAdequacaoPesoRN;
	
	@Inject
	private McoTabAdequacaoPesoDAO mcoTabAdequacaoPesoDAO;
	
	@EJB
	private CalculoCapurroON calculoCapurroON;
	
	@EJB
	private CalculoCapurroRN calculoCapurroRN;
	
	@Override
	public List<UnidadeFuncional> pesquisarUnidadeFuncional(Object objPesquisa) {
		return unidadeFuncionalON.pesquisarUnidadeFuncional(objPesquisa);
		
	}
	
	@Override
	public Long pesquisarUnidadeFuncionalCount(Object objPesquisa) {
		return unidadeFuncionalON.pesquisarUnidadeFuncionalCount(objPesquisa);
	}
	
	@Override
	public List<MamUnidAtendem> pesquisarUnidadesFuncionaisEmergencia(
			Integer firstResult,
			Integer maxResult,
			String orderProperty,
			boolean asc,
			Short unidadeFuncionalSeq, 
			String descricao,
			DominioSituacao indSituacao)
			
	{
		return this.mamUnidAtendemDAO.pesquisarUnidadesFuncionaisEmergencia(firstResult, maxResult, orderProperty, asc, unidadeFuncionalSeq, descricao, indSituacao);
		
	}
	
	@Override
	public Long pesquisarUnidadesFuncionaisEmergenciaCount(		
			Short unidadeFuncionalSeq, 
			String descricao,
			DominioSituacao indSituacao){
		return this.mamUnidAtendemDAO.pesquisarUnidadesFuncionaisEmergenciaCount(unidadeFuncionalSeq, descricao, indSituacao);
	}
	
	@Override
	public MamUnidAtendem pesquisarUnidadeFuncionalAtivaPorUnfSeq(Short unfSeq) {
		return this.mamUnidAtendemDAO.pesquisarUnidadeFuncionalAtivaPorUnfSeq(unfSeq);
	}
	
	@Override
	public void inserir(MamUnidAtendem mamUnidAtendem, String hostName) throws ApplicationBusinessException{
	      this.mamUnidAtendemRN.inserir(mamUnidAtendem, hostName);	
	}
	
	@Override
	public void alterar(MamUnidAtendem mamUnidAtendem, String hostName) throws ApplicationBusinessException {
		this.mamUnidAtendemRN.alterar(mamUnidAtendem, hostName);
	}
	
	@Override
	public void excluir(Short unfSeq) throws ApplicationBusinessException {	  
		this.mamUnidAtendemRN.excluir(unfSeq);
	}
	
	
	@Override
	public List<Especialidade> pesquisarEspecialidade(Object objPesquisa) {
		return unidadeFuncionalON.pesquisarEspecialidade(objPesquisa);
		
	}
	
	@Override
	public Especialidade obterEspecialidade(Short seq){
		return unidadeFuncionalON.obterEspecialidade(seq);
	}
	
	@Override
	public List<MamUnidAtendeEsp> pesquisarUnidAtendeEspPorUnidadeAtendem(	Integer firstResult,
			Integer maxResult,
			String orderProperty,
			boolean asc,
			Short unfSeq){
		return this.mamUnidAtendeEspDAO.pesquisarUnidAtendeEspPorUnidadeAtendem(firstResult, maxResult, orderProperty, asc, unfSeq);
	}
	
	@Override
	public Long pesquisarUnidAtendeEspPorUnidadeAtendemCount(Short unfSeq) {
		return this.mamUnidAtendeEspDAO.pesquisarUnidAtendeEspPorUnidadeAtendemCount(unfSeq);		
	}
	
	@Override
	public void excluirUnidAtendeEsp(MamUnidAtendeEspId id) {
		this.mamUnidAtendeEspsRN.excluirUnidAtendeEsp(id);
	}
	
	@Override
	public void inserirUnidAtendeEsp(MamUnidAtendeEsp mamUnidAtendeEsp, String hostName) throws ApplicationBusinessException {
		this.mamUnidAtendeEspsRN.inserirUnidAtendeEsp(mamUnidAtendeEsp, hostName);
	}
	
	@Override
	public void alterarUnidAtendeEsp(MamUnidAtendeEsp mamUnidAtendeEsp, String hostName) throws ApplicationBusinessException {
		this.mamUnidAtendeEspsRN.alterarUnidAtendeEsp(mamUnidAtendeEsp, hostName);
	}
	
	@Override
	public List<Especialidade> pesquisarEspecialidadeListaSeq(List<Short> listaEspId, Object objPesquisa) {
		return this.marcarConsultaEmergenciaON.pesquisarEspecialidadeListaSeq(listaEspId, objPesquisa);
	}
	
	@Override
	public Long pesquisarEspecialidadeListaSeqCount(List<Short> listaEspId, Object objPesquisa) {
		return this.marcarConsultaEmergenciaON.pesquisarEspecialidadeListaSeqCount(listaEspId, objPesquisa);
	}
	
	
	@Override
	public Especialidade obterEspecialidadePorSeq(Short espSeq) throws ApplicationBusinessException {
		return this.marcarConsultaEmergenciaON.obterEspecialidadePorSeq(espSeq);
	}
	
	@Override
	public List<Short> pesquisarEspecialidadesTriagem(Long seqTriagem){
		return this.mamUnidAtendeEspDAO.pesquisarEspecialidadesTriagem(seqTriagem);
		
	}

	@Override
	public List<MamSituacaoEmergencia> pesquisarSituacoesEmergencia(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Short codigo,
			String descricao, DominioSituacao indSituacao) {
		return mamSituacaoEmergenciaDAO.pesquisarSituacoesEmergencia(firstResult, maxResult, orderProperty, asc, codigo, descricao, indSituacao);
	}

	@Override
	public Long pesquisarSituacoesEmergenciaCount(Short codigo, String descricao, DominioSituacao indSituacao) {
		return mamSituacaoEmergenciaDAO.pesquisarSituacoesEmergenciaCount(codigo, descricao, indSituacao);
	}

	@Override
	public List<MamCaractSitEmerg> pesquisarCaracteristicaSituacaoEmergencia(Short codigoSit) {
		return mamCaractSitEmergDAO.pesquisarCaracteristicaSituacaoEmergencia(codigoSit);
	}

	@Override
	public void excluirMamSituacaoEmergencia(MamSituacaoEmergencia situacaoEmergencia) throws ApplicationBusinessException {
		mamSituacaoEmergenciaRN.excluir(situacaoEmergencia);
	}

	@Override
	public void persistirMamSituacaoEmergencia(MamSituacaoEmergencia situacaoEmergencia) throws ApplicationBusinessException {
		mamSituacaoEmergenciaRN.persistir(situacaoEmergencia);
	}

	@Override
	public void inserirMamCaractSitEmerg(MamCaractSitEmerg caracSituacaoEmergencia) throws ApplicationBusinessException {
		mamSituacaoEmergenciaRN.inserirMamCaractSitEmerg(caracSituacaoEmergencia);
	}

	@Override
	public void ativarInativarMamCaractSitEmerg(MamCaractSitEmerg caracSituacaoEmergencia) throws ApplicationBusinessException {
		mamSituacaoEmergenciaRN.ativarInativarMamCaractSitEmerg(caracSituacaoEmergencia);
	}

	@Override
	public void excluirMamCaractSitEmerg(MamCaractSitEmerg caracSituacaoEmergencia) throws ApplicationBusinessException {
		mamSituacaoEmergenciaRN.excluirMamCaractSitEmerg(caracSituacaoEmergencia);
	}

	@Override
	public MicroComputador obterMicroComputadorPorNomeOuIPException(String hostName) throws ApplicationBusinessException {
		return this.pacientesEmergenciaON.obterMicroComputadorPorNomeOuIPException(hostName);
	}
	
	@Override
	public List<Short> pesquisarUnidadesFuncionaisTriagemRecepcaoAtivas(boolean isTriagem) {
		return this.mamUnidAtendemDAO.pesquisarUnidadesFuncionaisTriagemRecepcaoAtivas(isTriagem);
	}
	
	@Override
	public Short pesquisarUnidadeFuncionalTriagemRecepcao(
			List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador) throws ApplicationBusinessException {
		
		return this.pacientesEmergenciaON
				.pesquisarUnidadeFuncionalTriagemRecepcao(listaUnfSeqTriagemRecepcao, unfSeqMicroComputador);
	}
	
	@Override
	public List<MamUnidAtendem> listarUnidadesFuncionais(final Object parametro, boolean isRecepcao, String orderBy, boolean somenteAtivas) {
		return this.mamUnidAtendemDAO.listarUnidadesFuncionais(parametro, isRecepcao, orderBy, somenteAtivas);
	}
	
	@Override
	public Long listarUnidadesFuncionaisCount(final Object parametro, boolean isRecepcao, boolean somenteAtivas) {
		return this.mamUnidAtendemDAO.listarUnidadesFuncionaisCount(parametro, isRecepcao, somenteAtivas);
	}

	@Override
	public List<Paciente> obterPacientePorCodigoOuProntuario(PacienteFiltro filtro) throws ApplicationBusinessException {
		return this.pacientesEmergenciaON.obterPacientePorCodigoOuProntuario(filtro);
	}

	@Override
	public List<Paciente> pesquisarPorFonemas(PacienteFiltro filtro) throws ApplicationBusinessException {
		return this.pacientesEmergenciaON.pesquisarPorFonemas(filtro);
	}

	@Override
	public Long pesquisarPorFonemasCount(PacienteFiltro filtro) throws ApplicationBusinessException {
		return this.pacientesEmergenciaON.pesquisarPorFonemasCount(filtro);
	}

	@Override
	public List<MamProtClassifRisco> pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricao(Object objPesquisa, Integer maxResults){
		return mamProtClassifRiscoDAO.pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricao(objPesquisa, maxResults);
	}

	@Override
	public Long pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricaoCount(Object objPesquisa) {
		return mamProtClassifRiscoDAO.pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricaoCount(objPesquisa);
	}
	

	@Override
	public void excluirMamEmgEspecialidades(Short espSeq) throws ApplicationBusinessException {
		mamEmgEspecialidadesRN.excluir(espSeq);
	}

	@Override
	public List<EspecialidadeEmergenciaVO> pesquisarEspecialidadeEmergenciaVO(Short seq, DominioSituacao indSituacao) throws ApplicationBusinessException {
		return mamEmgEspecialidadesRN.pesquisarEspecialidadeEmergenciaVO(seq, indSituacao);
	}
	
	@Override
	public Integer gravarEncaminhamentoInterno(Short espSeq, Long seqTriagem, String hostName) throws BaseException, ServiceException{
		return this.marcarConsultaEmergenciaRN.gravarEncaminhamentoInterno(espSeq, seqTriagem, hostName);
	}
	
	@Override
	public List<Integer> verificarConsultasEmergencia(Short espSeq, Long trgSeq) throws ApplicationBusinessException {
		return this.marcarConsultaEmergenciaRN.verificarConsultasEmergencia(espSeq, trgSeq);
	}

	@Override
	public void persistirMamEmgEspecialidades(MamEmgEspecialidades especialidadeEmergencia, boolean create) throws ApplicationBusinessException {
		mamEmgEspecialidadesRN.persistir(especialidadeEmergencia, create);
	}

	@Override
	public List<AgrupamentoEspecialidadeEmergenciaVO> pesquisarAgrupamentoEspecialidadeEmergenciaVO(Short seq) throws ApplicationBusinessException {
		return mamEmgEspecialidadesRN.pesquisarAgrupamentoEspecialidadeEmergenciaVO(seq);
	}

	@Override
	public void inserirMamEmgAgrupaEsp(MamEmgAgrupaEsp mamEmgAgrupaEsp) throws ApplicationBusinessException {
		mamEmgEspecialidadesRN.inserirMamEmgAgrupaEsp(mamEmgAgrupaEsp);
	}

	@Override
	public void ativarInativarMamEmgAgrupaEsp(MamEmgAgrupaEsp mamEmgAgrupaEsp) throws ApplicationBusinessException {
		mamEmgEspecialidadesRN.ativarInativarMamEmgAgrupaEsp(mamEmgAgrupaEsp);
	}
	
	@Override
	public void excluirMamEmgAgrupaEsp(MamEmgAgrupaEsp mamEmgAgrupaEsp) throws ApplicationBusinessException {
		mamEmgEspecialidadesRN.excluirMamEmgAgrupaEsp(mamEmgAgrupaEsp);
	}
	
	@Override
	public List<Especialidade> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param, Integer maxResults) throws ApplicationBusinessException {
		return mamEmgEspecialidadesRN.pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(param, maxResults);
	}
	
	@Override
	public Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(String param) throws ApplicationBusinessException {
		return mamEmgEspecialidadesRN.pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(param);
	}
	
	@Override
	public List<Especialidade> pesquisarEspecialidadesEmergenciaAtivasPorSeqNomeOuSigla(String param) throws ApplicationBusinessException {
		return mamEmgServidorRN.pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(param);
	}
	
	@Override
	public Long pesquisarEspecialidadesEmergenciaAtivasPorSeqNomeOuSiglaCount(String param) throws ApplicationBusinessException {
		return mamEmgServidorRN.pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(param);
	}
	
	@Override
	public List<ServidorEmergenciaVO> pesquisarServidorEmergenciaVO(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short serVinCodigo, Integer matricula, String indSituacao, String nome) throws ApplicationBusinessException {
		return mamEmgServidorRN.pesquisarServidorEmergenciaVO(firstResult, maxResult, orderProperty, asc, serVinCodigo, matricula, indSituacao, nome);
	}
	
	@Override
	public Long pesquisarServidorEmergenciaVOCount(Short serVinCodigo, Integer matricula, String indSituacao, String nome) throws ApplicationBusinessException {
		return mamEmgServidorRN.pesquisarServidorEmergenciaVOCount(serVinCodigo, matricula, indSituacao, nome);
	}
	
	@Override
	public void excluirMamEmgServidores(Integer eseSeq) throws ApplicationBusinessException {
		mamEmgServidorRN.excluir(eseSeq);
	}
	
	@Override
	public List<Servidor> pesquisarServidoresAtivosPorNomeOuVinculoMatricula(String param) throws ApplicationBusinessException {
		return mamEmgServidorRN.pesquisarServidoresAtivosPorNomeOuVinculoMatricula(param);
	}
	
	@Override
	public Long pesquisarServidoresAtivosPorNomeOuVinculoMatriculaCount(String param) throws ApplicationBusinessException {
		return mamEmgServidorRN.pesquisarServidoresAtivosPorNomeOuVinculoMatriculaCount(param);
	}
	
	@Override
	public void persistirMamEmgServidor(MamEmgServidor mamEmgServidor) throws ApplicationBusinessException {
		mamEmgServidorRN.persistir(mamEmgServidor);
	}
	
	@Override
	public List<ServidorEspecialidadeEmergenciaVO> pesquisarServidorEspecialidadeEmergenciaVO(Integer seq) throws ApplicationBusinessException {
		return mamEmgServidorRN.pesquisarServidorEspecialidadeEmergenciaVO(seq);
	}
	
	@Override
	public void inserirMamEmgServEspecialidade(MamEmgServEspecialidade mamEmgServEspecialidade) throws ApplicationBusinessException {
		mamEmgServidorRN.inserirMamEmgServEspecialidade(mamEmgServEspecialidade);
	}
	
	@Override
	public void ativarInativarMamEmgServEspecialidade(MamEmgServEspecialidade mamEmgServEspecialidade) throws ApplicationBusinessException {
		mamEmgServidorRN.ativarInativarMamEmgServEspecialidade(mamEmgServEspecialidade);
	}
	
	@Override
	public void excluirMamEmgServEspecialidade(MamEmgServEspecialidade mamEmgServEspecialidade) throws ApplicationBusinessException {
		mamEmgServidorRN.excluirMamEmgServEspecialidade(mamEmgServEspecialidade);
	}
	

	@Override
	public String gerarEtiquetaPulseira(
			Integer pacCodigo, Integer atdSeq) throws ApplicationBusinessException {
		return this.pacientesEmergenciaON.gerarEtiquetaPulseira(pacCodigo, atdSeq);
	}
	
	@Override
	public Boolean encaminharPacienteAcolhimento(Paciente paciente,
			MamUnidAtendem unidade, Short unfSeqMicroComputador, String hostName, String nomeResponsavel) throws ApplicationBusinessException {
		
		return this.pacientesEmergenciaON.encaminharPacienteAcolhimento(paciente, unidade, unfSeqMicroComputador, hostName, nomeResponsavel);
	}
	
	@Override
	public List<MamEmgServEspCoop> pesquisarMamEmgServEspCoopPorMamEmgServEspecialidade(Integer eseSeq, Short eepEspSeq) {
		return this.mamEmgServEspCoopDAO.pesquisarPorMamEmgServEspecialidade(eseSeq, eepEspSeq);
	}

	@Override
	public List<MamTipoCooperacao> pesquisarMamTipoCooperacaoAtivos(String descricao) {
		return this.mamTipoCooperacaoDAO.pesquisarAtivos(descricao);
	}
	
	@Override
	public void inserirMamEmgServEspCoop(MamEmgServEspCoop mamEmgServEspCoop) throws ApplicationBusinessException {
		this.mamEmgServidorRN.inserirMamEmgServEspCoop(mamEmgServEspCoop);
	}
	
	@Override
	public void excluirMamEmgServEspCoop(MamEmgServEspCoop mamEmgServEspCoop) throws ApplicationBusinessException {
		this.mamEmgServidorRN.excluirMamEmgServEspCoop(mamEmgServEspCoop);
	}
	
	@Override
	public List<MamProtClassifRisco> pesquisarProtClassifRisco(String descricao, DominioSituacao indSituacao) {
		return mamProtClassifRiscoDAO.pesquisarProtClassifRisco(descricao, indSituacao);
	}
	
	@Override
	public void persistirMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException {
		mamProtClassifRiscoRN.persistir(mamProtClassifRisco);
	}
	
	@Override
	public void ativarInativarMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException {
		mamProtClassifRiscoRN.ativarInativar(mamProtClassifRisco);
	}
	
	@Override
	public void excluirMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException {
		mamProtClassifRiscoRN.excluir(mamProtClassifRisco);
	}

	@Override
	public void permitirBloquearChecagemMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException {
		mamProtClassifRiscoRN.permitirBloquearChecagem(mamProtClassifRisco);
	}
	
	@Override
	public List<PacienteAcolhimentoVO> listarPacientesAcolhimento(Short unfSeq) throws ApplicationBusinessException {
		return this.pacientesEmergenciaON.listarPacientesAcolhimento(unfSeq);
	}
	
	@Override
	public List<PacienteEmAtendimentoVO> listarPacientesEmAtendimento(Short unfSeq) throws ApplicationBusinessException {
		return this.pacientesEmergenciaEmAtendimentoON.listarPacientesEmAtendimento(unfSeq);
	}

	@Override
	public List<MamFluxograma> pesquisarFluxogramaPorProtocolo(MamProtClassifRisco mamProtClassifRisco) {
		return mamFluxogramaDAO.pesquisarFluxogramaPorProtocolo(mamProtClassifRisco);
	}
	
	@Override
	public void persistirMamFluxograma(MamFluxograma mamFluxograma) throws ApplicationBusinessException {
		mamFluxogramaRN.persistir(mamFluxograma);
	}

	@Override
	public void persistirMamDescritor(MamDescritor mamDescritor) throws ApplicationBusinessException {
		mamDescritorRN.persistir(mamDescritor);
	}

	@Override
	public List<MamDescritor> pesquisarDescritorPorFluxograma(MamFluxograma mamFluxograma) {
		return mamDescritorDAO.pesquisarDescritorPorFluxograma(mamFluxograma);
	}
	
	@Override
	public List<MamGravidade> pesquisarGravidadesAtivasPorCodigoDescricao(Object objPesquisa, Integer pcrSeq) {
		return mamGravidadeDAO.pesquisarGravidadesAtivasPorCodigoDescricao(objPesquisa, pcrSeq);
	}

	@Override
	public Long pesquisarGravidadesAtivasPorCodigoDescricaoCount(Object objPesquisa, Integer pcrSeq) {
		return mamGravidadeDAO.pesquisarGravidadesAtivasPorCodigoDescricaoCount(objPesquisa, pcrSeq);
	}
	
	@Override
	public void persistirMamGravidade(MamGravidade mamGravidade) throws ApplicationBusinessException {
		mamGravidadeRN.persistir(mamGravidade);
	}

	@Override
	public boolean subirOrdemMamGravidade(MamGravidade mamGravidade, List<MamGravidade> gravidades) throws ApplicationBusinessException {
		return mamGravidadeRN.subirOrdem(mamGravidade, gravidades);
	}

	@Override
	public boolean descerOrdemMamGravidade(MamGravidade mamGravidade, List<MamGravidade> gravidades) throws ApplicationBusinessException {
		return mamGravidadeRN.descerOrdem(mamGravidade, gravidades);
	}

	@Override
	public List<MamGravidade> pesquisarGravidadePorProtocolo(MamProtClassifRisco mamProtClassifRisco) {
		return mamGravidadeDAO.pesquisarGravidadePorProtocolo(mamProtClassifRisco);
	}
	
	@Override
	public List<MamOrigemPaciente> pesquisarOrigensPaciente(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer seq, DominioSituacao indSituacao, String descricao){
		return this.mamOrigemPacienteDAO.pesquisarOrigensPaciente(firstResult, maxResults, orderProperty, asc, descricao, indSituacao, seq);
	}

	@Override
	public Long pesquisarOrigensPacienteCount(Integer seq, DominioSituacao indSituacao, String descricao){
		return this.mamOrigemPacienteDAO.pesquisarOrigemPacientePorCodigoDescricaoSituacaoCount(descricao, indSituacao, seq);
	}

	@Override
	public void persistirOrigemPaciente(DominioSituacao indSituacao, String descricao) throws ApplicationBusinessException{
		mamOrigemPacienteRN.persistir(descricao, indSituacao);
	}
	
	@Override
	public void alterarOrigemPaciente(DominioSituacao situacao, String descricao, MamOrigemPaciente mamOrigemPaciente) throws ApplicationBusinessException{
		mamOrigemPacienteRN.alterar(descricao, situacao, mamOrigemPaciente);
	}
	
	@Override
	public void ativarInativarMamOrigemPaciente(MamOrigemPaciente mamOrigemPaciente) throws ApplicationBusinessException{
		mamOrigemPacienteRN.ativarInativar(mamOrigemPaciente);
	}

	@Override
	public void excluirMamOrigemPaciente(Integer seq) throws ApplicationBusinessException{
		mamOrigemPacienteRN.excluir(seq);
	}
	
	@Override
	public void validarAcolhimentoPaciente(Long trgSeq, String hostName, MamUnidAtendem mamUnidAtendem) throws ApplicationBusinessException {
		pacientesEmergenciaON.validarAcolhimentoPaciente(trgSeq, hostName, mamUnidAtendem);
	}
	
	@Override
	public List<MamOrigemPaciente> listarOrigemPaciente(Object pesquisa) {
		return mamOrigemPacienteDAO.listarOrigemPaciente(pesquisa);
	}
	
	@Override
	public Long listarOrigemPacienteCount(Object pesquisa) {
		return mamOrigemPacienteDAO.listarOrigemPacienteCount(pesquisa);
	}
	
	@Override
	public MamTriagemVO obterTriagemVOPorSeq(Long trgSeq, Integer pacCodigo) throws ApplicationBusinessException {
		return realizarAcolhimentoON.obterTriagemVOPorSeq(trgSeq, pacCodigo);
	}
	
	@Override
	public MamOrigemPaciente obterOrigemPacientePorChavePrimaria(Integer seq) {
		return this.mamOrigemPacienteDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public List<PostoSaude> listarMpmPostoSaudePorSeqDescricao(final Object parametro) throws ApplicationBusinessException {
		return realizarAcolhimentoON.listarMpmPostoSaudePorSeqDescricao(parametro);
	}
	
	@Override
	public Long listarMpmPostoSaudePorSeqDescricaoCount(final Object parametro) throws ApplicationBusinessException {
		return realizarAcolhimentoON.listarMpmPostoSaudePorSeqDescricaoCount(parametro);
	}
	
	@Override
	public List<MamTrgGerais> listarDadosGerais(Long trgSeq, String nomeComputador) {
		return realizarAcolhimentoON.listarDadosGerais(trgSeq, nomeComputador);
	}
	
	@Override
	public List<MamTrgExames> listarExames(Long trgSeq, String nomeComputador) {
		return realizarAcolhimentoON.listarExames(trgSeq, nomeComputador);
	}
	
	@Override
	public List<MamTrgMedicacoes> listarMedicacoes(Long trgSeq, String nomeComputador) {
		return realizarAcolhimentoON.listarMedicacoes(trgSeq, nomeComputador);
	}
	
	@Override
	public MamTrgGerais inserirTrgGeral(Long trgSeq, Integer itgSeq, String nomeComputador) {
		return this.mamTrgGeralRN.inserirTrgGeral(trgSeq, itgSeq, nomeComputador);
	}
	
	@Override
	public MamTrgExames inserirTrgExame(Long trgSeq, Integer emsSeq, String nomeComputador) {
		return this.mamTrgExameRN.inserirTrgExame(trgSeq, emsSeq, nomeComputador);
	}
	
	@Override
	public MamTrgMedicacoes inserirTrgMedicacao(Long trgSeq, Integer mdmSeq, String nomeComputador) {
		return this.mamTrgMedicacaoRN.inserirTrgMedicacao(trgSeq, mdmSeq, nomeComputador);
	}
	
	@Override
	public void excluirTrgGeral(MamTrgGerais mamTrgGerais) throws ApplicationBusinessException {
		this.mamTrgGeralRN.excluirTrgGeral(mamTrgGerais);
	}
	
	@Override
	public void excluirTrgExame(MamTrgExames mamTrgExames) throws ApplicationBusinessException {
		this.mamTrgExameRN.excluirTrgExame(mamTrgExames);
	}
	
	@Override
	public void excluirTrgMedicacao(MamTrgMedicacoes mamTrgMedicacoes) throws ApplicationBusinessException {
		this.mamTrgMedicacaoRN.excluirTrgMedicacao(mamTrgMedicacoes);
	}
	
	@Override
	public Boolean gravarDadosAcolhimento(MamTriagemVO vo, MamTriagemVO mamTriagemVOOriginal,
			boolean validarFluxograma, String hostName) throws ApplicationBusinessException {
		return this.realizarAcolhimentoON.gravarDadosAcolhimento(vo, mamTriagemVOOriginal, validarFluxograma, hostName);
	}
	
	@Override
	public void transferirPacienteUnidade(MamTriagemVO vo, MamTriagemVO mamTriagemVOOriginal,
			String hostName, Paciente paciente) throws ApplicationBusinessException {
		realizarAcolhimentoON.transferirPacienteUnidade(vo, mamTriagemVOOriginal, hostName, paciente);
	}
	
	@Override
	public void naoTransferirPacienteUnidade(MamTriagemVO vo, MamTriagemVO mamTriagemVOOriginal,
			String hostName) throws ApplicationBusinessException {
		realizarAcolhimentoON.naoTransferirPacienteUnidade(vo, mamTriagemVOOriginal, hostName);
	}

	
	@Override
	public List<MamPacientesAguardandoAtendimentoVO> listarPacientesAguardandoAtendimentoEmergencia(Short unfSeq, Short espSeq) throws ApplicationBusinessException{
		return pacientesEmergenciaON.listarPacientesAguardandoAtendimentoEmergencia(unfSeq, espSeq);
	}
	
	@Override
	public List<EspecialidadeEmergenciaUnidadeVO> obterEspecialidadesEmergenciaAssociadasUnidades (Short unfSeq) throws ApplicationBusinessException{
		return mamEmgEspecialidadesRN.obterEspecialidadesEmergenciaAssociadasUnidades(unfSeq);
	}
	

	
	@Override
	public void popularItensObrigatorios(MamDescritor mamDescritor, List<ItemObrigatorioVO> itensSinaisVitais, List<ItemObrigatorioVO> itensExames,
			List<ItemObrigatorioVO> itensMedicamecoes, List<ItemObrigatorioVO> itensGerais) throws ApplicationBusinessException {
		mamObrigatoriedadeRN.popularItensObrigatorios(mamDescritor, itensSinaisVitais, itensExames, itensMedicamecoes, itensGerais);
	}
	
	@Override
	public List<ItemObrigatorioVO> pesquisarSinaisVitais(String parametro) throws ApplicationBusinessException {
		return mamItemSinalVitalRN.pesquisarSinaisVitais(parametro);
	}

	@Override
	public Long pesquisarSinaisVitaisCount(String parametro) throws ApplicationBusinessException {
		return mamItemSinalVitalRN.pesquisarSinaisVitaisCount(parametro);
	}
	
	@Override
	public void ativarDesativarMamObrigatoriedade(ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		mamObrigatoriedadeRN.ativarDesativar(itemObrigatorioVO);
	}
	
	@Override
	public void removerMamObrigatoriedade(ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		mamObrigatoriedadeRN.remover(itemObrigatorioVO);
	}
	
	@Override
	public List<MamItemExame> pesquisarMamItemExameAtivosPorSeqOuDescricao(String param, Integer maxResults) {
		return mamItemExameDAO.pesquisarAtivosPorSeqOuDescricao(param, maxResults);
	}

	@Override
	public Long pesquisarMamItemExameAtivosPorSeqOuDescricaoCount(String param) {
		return mamItemExameDAO.pesquisarAtivosPorSeqOuDescricaoCount(param);
	}
	
	@Override
	public List<MamItemMedicacao> pesquisarMamItemMedicacaoAtivosPorSeqOuDescricao(String param, Integer maxResults) {
		return mamItemMedicacaoDAO.pesquisarAtivosPorSeqOuDescricao(param, maxResults);
	}

	@Override
	public Long pesquisarMamItemMedicacaoAtivosPorSeqOuDescricaoCount(String param) {
		return mamItemMedicacaoDAO.pesquisarAtivosPorSeqOuDescricaoCount(param);
	}
	
	@Override
	public List<MamItemGeral> pesquisarMamItemGeralAtivosPorSeqOuDescricao(String param, Integer maxResults) {
		return mamItemGeralDAO.pesquisarAtivosPorSeqOuDescricao(param, maxResults);
	}

	@Override
	public Long pesquisarMamItemGeralAtivosPorSeqOuDescricaoCount(String param) {
		return mamItemGeralDAO.pesquisarAtivosPorSeqOuDescricaoCount(param);
	}
	
	@Override
	public void inserirObrigatoriedadeSinalVital(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		mamObrigatoriedadeRN.inserirObrigatoriedadeSinalVital(seqDesc, itemObrigatorioVO);
	}
	
	@Override
	public void inserirObrigatoriedadeExame(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		mamObrigatoriedadeRN.inserirObrigatoriedadeExame(seqDesc, itemObrigatorioVO);
	}

	@Override
	public void inserirObrigatoriedadeGeral(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		mamObrigatoriedadeRN.inserirObrigatoriedadeGeral(seqDesc, itemObrigatorioVO);
	}

	@Override
	public void inserirObrigatoriedadeMedicacao(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		mamObrigatoriedadeRN.inserirObrigatoriedadeMedicacao(seqDesc, itemObrigatorioVO);
	}

	@Override
	public List<MamFluxograma> pesquisarFluxogramaAtivoPorProtocolo(String param, Integer seqProtocolo) {
		return mamFluxogramaRN.pesquisarFluxogramaAtivoPorProtocolo(param, seqProtocolo);
	}

	@Override
	public Long pesquisarFluxogramaAtivoPorProtocoloCount(String param, Integer seqProtocolo) {
		return mamFluxogramaRN.pesquisarFluxogramaAtivoPorProtocoloCount(param, seqProtocolo);
	}
	
	@Override
	public List<DescritorTrgGravidadeVO> pesquisarDescritorAtivoPorFluxogramaGravidadeAtivaTriagem(MamFluxograma mamFluxograma, MamTrgGravidade trgGravidade) {
		return mamDescritorRN.pesquisarDescritorAtivoPorFluxogramaGravidadeAtivaTriagem(mamFluxograma, trgGravidade);
	}
	
	@Override
	public String criarMensagemTempoMaximoEspera(Date tempo) {
		return realizarAcolhimentoON.criarMensagemTempoMaximoEspera(tempo);
	}
	
	@Override
	public TrgGravidadeFluxogramaVO obterClassificacaoRiscoPaciente(Long trgSeqAcolhimento, Short unfSeqAcolhimento) {
		return mamTrgGravidadeRN.obterClassificacaoRiscoPaciente(trgSeqAcolhimento, unfSeqAcolhimento);
	}
	
	@Override
	public void gravarMamTrgGravidade(Long trgSeq, DescritorTrgGravidadeVO item, String micNome) throws ApplicationBusinessException {
		this.mamTrgGravidadeRN.gravarMamTrgGravidade(trgSeq, item, micNome);
	}
	
	@Override
	public List<BoletimAtendimentoVO> carregarDadosBoletim(Integer consulta) throws ApplicationBusinessException {
		return boletimAtentimentoON.carregarDadosBoletim(consulta);
	}
	
	@Override
	public List<FormularioEncExternoVO> carregarDadosFormulario(Long triagem, Short seqpTriagem, String cidadeLocal) throws ApplicationBusinessException {
		return formularioEncaminhamentoExternoON.carregarDadosFormulario(triagem, seqpTriagem, cidadeLocal);
	}
	
	@Override
	public List<Short> pesquisarSeqsEspecialidadesEmergencia(Short codigo, DominioSituacao indSituacao) {
		return this.mamEmgEspecialidadesDAO.pesquisarSeqsEspecialidadesEmergencia(codigo, indSituacao);
	}
	
	@Override
	public List<MamCapacidadeAtendVO> pesquisarCapacidadesAtends(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short espSeq, DominioSituacao indSituacao) throws ApplicationBusinessException {
		return this.capacidadeAtendimentoON.pesquisarCapacidadesAtends(firstResult, maxResults, orderProperty, asc, espSeq, indSituacao);
	}
	
	@Override
	public Long pesquisarCapacidadesAtendsCount(Short espSeq, DominioSituacao indSituacao) {
		return this.capacidadeAtendimentoON.pesquisarCapacidadesAtendsCount(espSeq, indSituacao);
	}
	
	@Override
	public void excluirCapacidadeAtend(MamCapacidadeAtendVO capacidadeSelecionada) {
		this.capacidadeAtendimentoRN.excluirCapacidadeAtend(capacidadeSelecionada);
	}
	
	@Override
	public void persistirCapacidadeAtend(Short espSeq, Integer capacidadeSeq, Short qtdeInicialPac, Short qtdeFinalPac,
			Short capacidadeAtend, Boolean indSituacao) throws ApplicationBusinessException {
		this.capacidadeAtendimentoON.persistirCapacidadeAtend(espSeq, capacidadeSeq, qtdeInicialPac, qtdeFinalPac,
				capacidadeAtend, indSituacao);
	}
	
	@Override
	public void verificaCodSituacaoEmergenciaDoEncExt(String hostName, Long trgSeq) throws ApplicationBusinessException{
		 this.mamTrgEncExternoRN.verificaCodSituacaoEmergenciaDoEncExt(hostName, trgSeq);
	}
	
	@Override
	public void gravarEncaminhamentoExterno(Long trgSeq, String especialidade, PostoSaude unidade, String obs, String hostName, Short seqp) throws ApplicationBusinessException{
		this.mamTrgEncExternoRN.gravarEncaminhamentoExterno(trgSeq, especialidade, unidade, obs, hostName, seqp);
	}
	
	@Override
	public String realizaEncaminhamentoExternoValidandoGravidade(String observacao,
			PostoSaude postoSaude, String especialidade) throws ApplicationBusinessException, ServiceException {
		return this.mamTrgEncExternoRN.realizaEncaminhamentoExternoValidandoGravidade(observacao,postoSaude,especialidade);
		
	} 
	
	@Override
	public Boolean existeTriagem(Long trgSeq){
		return this.mamTrgEncExternoRN.existeTriagem(trgSeq);
		
	} 
	
	@Override
	public List<PostoSaude> pesquisarUnidadeSaudeExterno(Object objPesquisa) throws ApplicationBusinessException {
		return this.mamTrgEncExternoRN.pesquisarUnidadeSaudeExterno(objPesquisa);
	}

	@Override
	public Short obtemUltimoSEQPDoAcolhimento(Long trgSeq) {		
		return this.mamTrgEncExternoRN.obtemUltimoSEQPDoAcolhimento(trgSeq);
	}
	
	@Override
	public Long obterTrgSeq(Integer consulta) {
		return mamTrgEncInternoDAO.obterTrgSeq(consulta);		
	}
	
	@Override
	public List<Short> obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia caracteristica) {
		return mamCaractSitEmergDAO.obterSegSeqParaEncInterno(caracteristica);
	}
	
	@Override
	public List<Date> obterDtHrSituacaoExtratoTriagem(Long trgSeq, Short segSeq) {
		return mamExtratoTriagemDAO.obterDtHrSituacaoExtratoTriagem(trgSeq, segSeq);
	}	
	
	@Override
	public List<EcpItemControleVO> buscarItensControlePorPacientePeriodo(
			Integer pacCodigo, Long trgSeq) {
		return this.controlesPacientesON.buscarItensControlePorPacientePeriodo(pacCodigo, trgSeq);
	}
	
	@Override
	public List<RegistroControlePacienteServiceVO> pesquisarRegistrosPaciente(
			Integer pacCodigo,List<EcpItemControleVO> listaItensControle, Long trgSeq) throws ApplicationBusinessException {
		return this.controlesPacientesON.pesquisarRegistrosPaciente(pacCodigo, listaItensControle, trgSeq);
	}

	
	@Override
	public boolean verificarExisteSinalVitalPorPaciente(Integer pacCodigo) throws ServiceException {
		return this.controlesPacientesON.verificarExisteSinalVitalPorPaciente(pacCodigo);
	}
	
	@Override
	public void excluirRegistroControlePaciente(Long seqHorarioControle) throws ApplicationBusinessException {
		this.controlesPacientesON.excluirRegistroControlePaciente(seqHorarioControle);
	}
	
	@Override
	public Boolean validaUnidadeFuncionalInformatizada(Short unfSeq) throws ApplicationBusinessException {
		return this.controlesPacientesON.validaUnidadeFuncionalInformatizada(unfSeq);
	}

	@Override
	public Boolean imprimirEmitirBoletimAtendimento(Short unfSeq) throws ServiceException {
		return mamTrgEncInternoRN.imprimirEmitirBoletimAtendimento(unfSeq);
	}
	
	@Override
	public boolean exibirModalPacienteMenor(Paciente paciente, MamUnidAtendem unidade) throws ApplicationBusinessException {
		return this.pacientesEmergenciaON.exibirModalPacienteMenor(paciente, unidade);
	}
	
	@Override
	public void atualizarSituacaoTriagem(MamTriagens mamTriagem, MamTriagens mamTriagemOriginal, String hostName) throws ApplicationBusinessException {
		this.marcarConsultaEmergenciaRN.atualizarSituacaoTriagem(mamTriagem, mamTriagemOriginal, hostName);
	}
	
	@Override
	public void verificarNotificacaoGmr(PacienteEmAtendimentoVO pacienteSelecionado) throws ApplicationBusinessException{
		this.pacientesEmergenciaEmAtendimentoON.verificarNotificacaoGmr(pacienteSelecionado);
	}
	
	@Override
	public Short obterUltimaGestacaoRegistrada(Integer pacCodigo){
		return this.pacientesEmergenciaEmAtendimentoON.obterUltimaGestacaoRegistrada(pacCodigo);
	}
	
	@Override
	public String verificaImpressaoAdmissaoObstetrica(PacienteEmAtendimentoVO pacienteSelecionado, Short seqP){
		return this.pacientesEmergenciaEmAtendimentoON.verificaImpressaoAdmissaoObstetrica(pacienteSelecionado, seqP);
	}
	
	@Override
	public Integer obterMatricula(){
		return this.pacientesEmergenciaEmAtendimentoON.obterMatricula();
	}
	
	@Override
	public Short obterVinculo(){
		return this.pacientesEmergenciaEmAtendimentoON.obterVinculo();
	}
	
	@Override
	public DominioTipoFormularioAlta obterTipoFormularioAlta(Long seq) {
		return this.mamRegistrosDAO.obterTipoFormularioAlta(seq);
	}
	
	@Override
	public void desbloqueioSumarioAlta(Integer atdSeq, String nomeMicromputador) throws ApplicationBusinessException{
		this.pacientesAtendidosON.desbloqueioSumarioAlta(atdSeq, nomeMicromputador);
	}
	
	@Override
	public Map<Integer, String> reabrirPacienteAtendidos(Long trgSeq, Integer atdSeq, Short unfSeq, String pacNome, Short espSeq, String hostName) throws ApplicationBusinessException {
		return this.pacientesAtendidosON.reabrirPacienteAtendidos(trgSeq, atdSeq, unfSeq, pacNome, espSeq, hostName);
	}
	
	@Override
	public List<MamPacientesAtendidosVO> listarPacientesAtendidos(Short unfSeq, Short espSeq) throws ApplicationBusinessException {	
		return this.pacientesAtendidosON.listarPacientesAtendidos(unfSeq, espSeq);
	}
	
	
	@Override
	public void verificarUnfPmeInformatizada(Short unfSeq, PacienteEmAtendimentoVO pacienteSelecionado) throws ApplicationBusinessException{
		this.pacientesEmergenciaEmAtendimentoON.verificarUnfPmeInformatizada(unfSeq, pacienteSelecionado);
	}
	
	@Override
	public boolean verificarUnfEmergObstetrica(Short unfSeq) throws ServiceException{
		return this.pacientesEmergenciaEmAtendimentoON.verificarUnfEmergObstetrica(unfSeq);
	}
	
	@Override
	public void verificarSituacaoPacientes(List<PacienteEmAtendimentoVO> listaPacientes, Short unfSeq) throws ApplicationBusinessException, ServiceException{
		this.pacientesEmergenciaEmAtendimentoON.verificarSituacaoPacientes(listaPacientes, unfSeq);
	}
	
	@Override
	public IntegracaoEmergenciaAghuAGHWebVO iniciarAtendimentoPaciente(
		Integer atdSeq, Integer conNumero, Long trgSeq,
		Date dataHoraInicio, Short unfSeq, Short espSeq, Integer pacCodigo,
		String hostName, Boolean atenderMedicoAghWeb) throws ServiceException, BaseException {
		return this.atenderPacienteEmergenciaON.iniciarAtendimentoPaciente(atdSeq, conNumero, trgSeq, dataHoraInicio, unfSeq, espSeq, pacCodigo, hostName, atenderMedicoAghWeb);
	}

	@Override
	public Boolean verificaIntegracaoAghuAghWebEmergencia()	throws ApplicationBusinessException {
		return this.atenderPacienteEmergenciaON.verificaIntegracaoAghuAghWebEmergencia();
	}
	
	@Override
	public Boolean verificaAgendaEmergencia(Short unfSeq) throws ServiceException {
		return this.marcarConsultaEmergenciaRN.verificaAgendaEmergencia(unfSeq);
	}

	@Override
	public Short obterUnidadeAssociadaAgendaPorNumeroConsulta(Integer conNumero) {
		return this.marcarConsultaEmergenciaON.obterUnidadeAssociadaAgendaPorNumeroConsulta(conNumero);
	}
	
	@Override
	public DominioSimNao obterParametroFormularioExterno() throws ApplicationBusinessException{
		return this.formularioEncaminhamentoExternoON.obterParametroFormularioExterno();
	}
	
	public List<McoLogImpressoes> listarLogImpressoes(Integer codigoPaciente, Short sequence) {
		return mcoLogImpressoesDAO.listarLogImpressoes(codigoPaciente, sequence);
	}

	@Override
	public void inserirLogImpressoes(Integer gsoPacCodigo, Short gsoSeqp,
			Integer seqp, Integer conNumero, String evento,
			Integer matricula, Short vinCodigo, Date criadoEm) {
		mcoLogImpressoesRN.inserirLogImpressoes(gsoPacCodigo, gsoSeqp, seqp, conNumero, evento, matricula, vinCodigo, criadoEm);	
	}
	
	@Override
	public Paciente reimpressaoPulseiraPacEmergencia(Integer codigoPaciente, MamUnidAtendem mamUnidAtendem)throws ApplicationBusinessException {
		return this.pacientesEmergenciaON.reimpressaoPulseiraPacEmergencia(codigoPaciente, mamUnidAtendem);
	}
	
	@Override
	public Long obterTrgSeqParaAcolhimento(Integer numeroConsulta) throws ApplicationBusinessException {
		return this.realizarAcolhimentoON.obterTrgSeq(numeroConsulta);				
	}

	@Override
	public Integer obterSeqpLogImpressoes(Integer gsoPacCodigo, Short gsoSeqp) {
		return mcoLogImpressoesDAO.obterSeqpLogImpressao(gsoPacCodigo, gsoSeqp);
	}
	
	@Override
	public void atualizaPrevisaoAtendimento(Short espSeq) throws ApplicationBusinessException {
		marcarConsultaEmergenciaRN.atualizaPrevisaoAtendimento(espSeq);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade#obterMcoGestacoes(br.gov.mec.aghu.model.McoGestacoesId)
	 */
	@Override
	@BypassInactiveModule
	public McoGestacoes obterMcoGestacoes(final McoGestacoesId id) {
		return this.getMcoGestacoesDAO().obterPorChavePrimaria(id);
	}

	@Override
	public List<McoGestacoes> pesquisarMcoGestacoesPorPaciente(Integer pacCodigo) {
		return mcoGestacoesDAO.pesquisarPorPaciente(pacCodigo);
	}

	@Override
	public PacienteProntuarioConsulta obterDadosGestante(Integer nroProntuario, Integer nroConsulta) throws ApplicationBusinessException {
		return pesquisaGestacoesRN.obterDadosGestante(nroProntuario, nroConsulta);
	}
	

	@Override
	public void cadastrarProcedimento(McoProcedimentoObstetricos procedimento)throws ApplicationBusinessException {
		manterMcoProcedimentoObstetricoRN.cadastrarProcedimento(procedimento);
	}

	@Override
	public List<McoProcedimentoObstetricos> pesquisarMcoProcedimentoObstetricos(Integer firstResult, Integer maxResults,String orderProperty,boolean asc,Short seq,String descricao,Integer codigoPHI,DominioSituacao dominioSituacao){
		return manterMcoProcedimentoObstetricoRN.pesquisarMcoProcedimentoObstetricos(firstResult, maxResults,orderProperty,asc,seq,descricao,codigoPHI,dominioSituacao);
	}

	@Override
	public McoProcedimentoObstetricos buscarMcoProcedimentoObstetricosPorSeq(Short seq) {
		return procedimentoObstetricosDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public void alterarProcedimento(McoProcedimentoObstetricos procedimento)throws ApplicationBusinessException{
		manterMcoProcedimentoObstetricoRN.alterarProcedimento(procedimento);
	}

	@Override
	public String getIdadeFormatada(Date dtNasc) {
		return pesquisaGestacoesRN.getIdadeFormatada(dtNasc);
	}
	
	@Override
	public Integer buscarUltimaConsultaCO(Integer pacCodigo) throws ApplicationBusinessException {
		return pesquisaGestacoesRN.buscarUltimaConsultaCO(pacCodigo);
	}
	
	@Override
	public List<McoIndicacaoNascimento> pesquisarIndicacoesNascimento(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, Integer codigo, String descricao,
			DominioTipoIndicacaoNascimento tipoIndicacao, DominioSituacao situacao) {
		return this.mcoIndicacaoNascimentoDAO.pesquisarIndicacoesNascimento(firstResult, maxResults, orderProperty, asc,
				codigo, descricao, tipoIndicacao, situacao);
	}
	
	@Override
	public Long pesquisarIndicacoesNascimentoCount(Integer codigo, String descricao,
			DominioTipoIndicacaoNascimento tipoIndicacao, DominioSituacao situacao) {
		return this.mcoIndicacaoNascimentoDAO.pesquisarIndicacoesNascimentoCount(codigo, descricao, tipoIndicacao, situacao);
	}
	
	@Override
	public void persistirIndicacaoNascimento(McoIndicacaoNascimento indicacaoNascimento,
			McoIndicacaoNascimento indicacaoNascimentoOriginal) throws BaseException {
		this.manterCadastroIndicacaoNascimentoON.persistirIndicacaoNascimento(indicacaoNascimento, indicacaoNascimentoOriginal);
	}
	
	@Override
	public McoDiagnostico obterMcoDiagnostico(Integer seq){
		return this.diagnosticoDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public void ativarInativarDiagnostico(McoDiagnostico diagnostico) throws ApplicationBusinessException{
		this.diagnosticoRN.ativarInativar(diagnostico);
	}
	
	@Override
	public Long obterListaDiagnosticoCount(DiagnosticoFiltro filtro){
		return this.diagnosticoDAO.obterDiagnosticoCount(filtro);
	}
	
	@Override
	public List<DiagnosticoVO>  pesquisarDiagnosticos(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			DiagnosticoFiltro filtro) throws ApplicationBusinessException{
		return this.diagnosticoRN.listarDiagnosticos(firstResult, maxResults, orderProperty, asc, filtro);
	}
	
	@Override
	public CidVO obterCidVO(Integer seq) throws ApplicationBusinessException{
		return this.diagnosticoRN.getCid(seq);
	}
	
	@Override
	public void persistirDiagnostico(McoDiagnostico diagnostico) throws ApplicationBusinessException{
		this.diagnosticoRN.persistir(diagnostico);
	}
	
	@Override
	public List<CidVO> obterCids(String param) throws ApplicationBusinessException{
		return this.diagnosticoRN.obterCids(param);
	}
	@Override
	public Long obterCidsCount(String param) throws ApplicationBusinessException{
		return this.diagnosticoRN.obterCidsCount(param);
	}

	@Override
	public void ativarDesativarProcedimentoObstetrico(
			McoProcedimentoObstetricos procedimentoObstetrico)
			throws ApplicationBusinessException {
		this.manterMcoProcedimentoObstetricoRN.ativarDesativarProcedimentoObstetrico(procedimentoObstetrico);		
	}
	
	@Override
	public DadosGestacaoVO pesquisarMcoGestacaoPorId(Integer pacCodigo, Short seqp, Integer numeroConsulta) throws ApplicationBusinessException{
		return this.registrarGestacaoON.pesquisarMcoGestacaoPorId(pacCodigo, seqp, numeroConsulta);
	}
	
	@Override
	public String preencherInformacoesPaciente(String nomePaciente, String idadeFormatada, Integer prontuario, Integer numeroConsulta) {
		return this.registrarGestacaoON.preencherInformacoesPaciente(nomePaciente, idadeFormatada, prontuario, numeroConsulta);
	}
	
	@Override
	public DadosSanguineos obterRegSanguineosPorCodigoPaciente(Integer pacCodigo, Byte seqp) throws ApplicationBusinessException {
		return this.registrarGestacaoON.obterRegSanguineosPorCodigoPaciente(pacCodigo, seqp);
	}
	
	@Override
	public Long pesquisarMcoProcedimentoObstetricosCount(Short seq,String descricao, Integer codigoPHI, DominioSituacao dominioSituacao) {
		return manterMcoProcedimentoObstetricoRN.pesquisarMcoProcedimentoObstetricosCount(seq,descricao,codigoPHI,dominioSituacao);
	}

	@Override
	public Integer buscarUltimaConsulta(Short gsoSeqp, Integer pacCodigo) throws ApplicationBusinessException {
		return pesquisaGestacoesRN.buscarUltimaConsulta(gsoSeqp, pacCodigo);
	}


	@Override
	public List<McoExameExterno> pesquisarExamesExternos(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, String nome,
			DominioSituacao situacao) {
		return exameExternoDAO.pesquisarListarExamesExternos(firstResult, maxResults, orderProperty, asc, nome, situacao);
	}

	@Override
	public Long pesquisarExamesExternosCount(String nome, DominioSituacao situacao) {
		return exameExternoDAO.pesquisarIndicacoesNascimentoCount(nome, situacao);
	}
	
	@Override
	public McoExameExterno obterMcoExameExterno(Integer seq) {
		return exameExternoDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public void persistirExameExterno(McoExameExterno exameExterno) throws ApplicationBusinessException {
		this.mcoExameExternoRN.persistirExameExterno(exameExterno);
	}

	@Override
	public void ativarInativarExameExterno(McoExameExterno exameExterno) throws ApplicationBusinessException {
		this.mcoExameExternoRN.ativarInativarExameExterno(exameExterno);
	}

	@Override
	public void excluirExameExterno(McoExameExterno exameExterno) throws ApplicationBusinessException {
		this.mcoExameExternoRN.excluirExameExterno(exameExterno);	
	}

	@Override
	public void validarParto(DadosGestacaoVO dadosGestacaoVO, DadosGestacaoVO dadosGestacaoVOOriginal,
			Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		this.registrarGestacaoON.validarParto(dadosGestacaoVO, dadosGestacaoVOOriginal, pacCodigo, seqp);
	}
	
	@Override
	public void validarEcoSemanasDias(DadosGestacaoVO dadosGestacaoVO, boolean isSemanas,
			boolean atualizar, boolean calcularIg) throws ApplicationBusinessException {
		this.registrarGestacaoON.validarEcoSemanasDias(dadosGestacaoVO, isSemanas, atualizar, calcularIg);
	}
	
	@Override
	public void calcularDtProvavelParto(DadosGestacaoVO dadosGestacaoVO) {
		this.registrarGestacaoON.calcularDtProvavelParto(dadosGestacaoVO);
	}
	
	@Override
	public void gravar(DadosGestacaoVO dadosGestacaoVO, DadosGestacaoVO dadosGestacaoVOOriginal, Integer pacCodigo, Short seqp,
			List<ExameResultados> examesResultados, List<ResultadoExameSignificativoPerinatologiaVO> resultadosExamesExcluidos)
			throws ApplicationBusinessException {
		this.registrarGestacaoON.gravar(dadosGestacaoVO, dadosGestacaoVOOriginal, pacCodigo, seqp, examesResultados,
				resultadosExamesExcluidos);
	}
	
	@Override
	public boolean isMcoGestacoesAlterada(DadosGestacaoVO dadosGestacaoVO, DadosGestacaoVO dadosGestacaoVOOriginal) {
		return this.registrarGestacaoRN.isMcoGestacoesAlterada(dadosGestacaoVO, dadosGestacaoVOOriginal);
	}
	
	@Override
	public boolean preGravar(DadosGestacaoVO dadosGestacaoVO, DadosGestacaoVO dadosGestacaoVOOriginal,
			Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		return this.registrarGestacaoON.preGravar(dadosGestacaoVO, dadosGestacaoVOOriginal, pacCodigo, seqp);
	}
	
	@Override
	public List<RapServidorConselhoVO> pesquisarServidoresConselho(String strPesquisa) throws ApplicationBusinessException {
		return registrarGestacaoON.pesquisarServidoresConselho(strPesquisa);
	}

	@Override
	public Long pesquisarServidoresConselhoCount(String strPesquisa)
			throws ApplicationBusinessException {
		return registrarGestacaoON.pesquisarServidoresConselhoCount(strPesquisa);
	}

	@Override
	public Long pesquisarIndicacoesNascimentoPorSeqNomeCount(String objPesquisa) {
		return registrarGestacaoON.pesquisarIndicacoesNascimentoPorSeqNomeCount(objPesquisa);
	}
	
	@Override
	public List<McoIndicacaoNascimento> pesquisarIndicacoesNascimentoPorSeqNome(
			String objPesquisa) {
		return registrarGestacaoON.pesquisarIndicacoesNascimentoPorSeqNome(objPesquisa);
	}
	
	@Override
	public List<DadosNascimentoVO> pesquisarMcoNascimentoPorId(Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.pesquisarMcoNascimentoPorId(pacCodigo, gsoSeqp);
	}
	
	@Override
	public boolean calcularDuracaoTotalParto(Integer gsoPacCodigo, Short gsoSeqp, String periodoExpulsivo, String periodoDilatacao,
			DadosNascimentoSelecionadoVO nascimentoSelecionado, DadosNascimentoVO nascimento) {
		return this.registrarGestacaoAbaNascimentoON.calcularDuracaoTotalParto(gsoPacCodigo, gsoSeqp, periodoExpulsivo,
				periodoDilatacao, nascimentoSelecionado, nascimento);
	}
	
	@Override
	public DadosNascimentoSelecionadoVO obterDadosNascimentoSelecionado(Integer seqp, Integer gsoPacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.obterDadosNascimentoSelecionado(seqp, gsoPacCodigo, gsoSeqp);
	}
	
	@Override
	public TipoAnestesiaVO obterAnestesiaAtiva(Short tanSeq) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.obterAnestesiaAtiva(tanSeq);
	}

	@Override
	public Date obterDtHrNascimento(Integer pacCodigo, Short gsoSeqp) {
		return this.registrarGestacaoAbaNascimentoON.obterDtHrNascimento(pacCodigo, gsoSeqp);
	}
	
	@Override
	public List<McoAtendTrabPartos> listarAtendPartosPorId(Short gsoSeqp, Integer gsoPacCodigo){
		return this.atendTrabPartoDAO.listarAtendPartosPorId(gsoSeqp, gsoPacCodigo);
	}
	
	@Override
	public void gravarAtendTrabParto(McoAtendTrabPartos atendTrabParto) throws ApplicationBusinessException {
		this.atendTrabPartosRN.gravarAtendTrabParto(atendTrabParto);
	}
	
	@Override
	public void excluirAtendTrabParto(McoAtendTrabPartos atendTrabParto){
		this.atendTrabPartosRN.excluirAtendTrabParto(atendTrabParto);
	}
	
	@Override
	public McoGestacoes pesquisarMcoGestacaoPorPacCodigoSeqp(Integer pacCodigo, Short seqp){
		return this.mcoGestacoesDAO.pesquisarMcoGestacaoPorId(pacCodigo, seqp);
	}
	@Override
	public List<MedicamentoVO> obterMcoMedicamentoPorSeqOuDescricao(
			Object objPesquisa) throws ServiceException {
		return registrarGestacaoON.obterMcoMedicamentoPorSeqOuDescricao(objPesquisa);
	}

	@Override
	public Long obterMcoMedicamentoPorSeqOuDescricaoCount(Object objPesquisa) throws ServiceException {
		return registrarGestacaoON.obterMcoMedicamentoPorSeqOuDescricaoCount(objPesquisa);
	}
	@Override
	public List<ExameSignificativoVO> pesquisarExamesSignificativos(Short unfSeq, String siglaExame, Integer seqMatAnls, Boolean cargaExame, int firstResult,
			int maxResults) throws ApplicationBusinessException {
		return this.cadastroExamesSignificativosRN.pesquisarExamesSignificativos(unfSeq, siglaExame, seqMatAnls, cargaExame, firstResult, maxResults);
	}

	@Override
	public Long pesquisarExamesSignificativosCount(Short unfSeq, String siglaExame, Integer seqMatAnls, Boolean cargaExame) throws ApplicationBusinessException {
		return this.cadastroExamesSignificativosRN.pesquisarExamesSignificativosCount(unfSeq, siglaExame, seqMatAnls, cargaExame);
	}

	@Override
	public List<ExameMaterialAnaliseVO> pesquisarExameMaterial(String descricao) throws ApplicationBusinessException {
		return this.cadastroExamesSignificativosRN.pesquisarExameMaterial(descricao);
	}

	@Override
	public Long pesquisarExameMaterialCount(String descricao) throws ApplicationBusinessException {
		return this.cadastroExamesSignificativosRN.pesquisarExameMaterialCount(descricao);
	}

	@Override
	public List<UnidadeFuncionalVO> pesquisarUnidadeFuncionalVO(String descricao) throws ApplicationBusinessException {
		return this.cadastroExamesSignificativosRN.pesquisarUnidadeFuncional(descricao);
	}

	@Override
	public Long pesquisarUnidadeFuncionalCount(String descricao) throws ApplicationBusinessException {
		return cadastroExamesSignificativosRN.pesquisarUnidadeFuncionalCount(descricao);
	}

	@Override
	public void persitirExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq, Boolean cargaExame) throws ApplicationBusinessException {
		this.cadastroExamesSignificativosRN.persitirExameSignificativo(unfSeq, exaSigla, matAnlsSeq, cargaExame);
	}

	@Override
	public void excluirExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq) throws ApplicationBusinessException {
		this.cadastroExamesSignificativosRN.excluirExameSignificativo(unfSeq, exaSigla, matAnlsSeq);
	}
	
	@Override
	public McoAtendTrabPartos obterAtendTrabPartoPorId(McoAtendTrabPartosId id){
		return this.atendTrabPartoDAO.obterMcoAtendTrabPartosPorId(id);
	}
	
	@Override
	public void gravarDadosAbaNascimento(Integer conNumero, String hostName, DadosNascimentoVO nascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException {
		
		this.registrarGestacaoAbaNascimentoON.gravarDadosAbaNascimento(conNumero, hostName, nascimentoSelecionado,
				dadosNascimentoSelecionado, dadosNascimentoSelecionadoOriginal);
	}
	
	@Override
	public void adicionarMedicamento(McoMedicamentoTrabPartos medicamento)throws ApplicationBusinessException {
		this.registrarGestacaoAbaTrabalhoPartoRN.adicionarMedicamento(medicamento);
	}

	@Override
	public McoMedicamentoTrabPartos buscarMcoMedicamentoTrabPartosPorId(
			Integer pacCodigo, Short seqp, Integer matCodigo) {
		return mcoMedicamentoTrabPartosDAO.buscarMcoMedicamentoTrabPartosPorId(
				pacCodigo, seqp, matCodigo);
	}

	@Override
	public void alterarMcoMedicamentoTrabPartos(
			McoMedicamentoTrabPartos medicamento) throws ApplicationBusinessException{
		registrarGestacaoAbaTrabalhoPartoRN.alterarMcoMedicamentoTrabPartos(medicamento);
	}

	@Override
	public void gravarBolsaRotas(McoBolsaRotas mcoBolsaRotas) throws ApplicationBusinessException{
		registrarGestacaoAbaTrabalhoPartoRN.gravarBolsaRotas(mcoBolsaRotas);
	}

	@Override
	public void gravarTrabalhoParto(McoTrabPartos mcoTrabPartos,McoBolsaRotas bolsaRotas )
			throws ApplicationBusinessException {
		registrarGestacaoAbaTrabalhoPartoRN.gravarTrabalhoParto(mcoTrabPartos,bolsaRotas);
	}
	
	@Override
	public Boolean isAnamnese(FiltroVerificaoInclusaoAnamneseVO filtroVO) throws BaseException {
		return consultaCOON.isAnamnese(filtroVO);
	}
	
	@Override
	public List<McoDiagnostico> pesquisarDiagnosticoSuggestion(String strPesquisa) {
		return consultaCOON.pesquisarDiagnosticoSuggestion(strPesquisa);
	}

	@Override
	public Long pesquisarDiagnosticoSuggestionCount(String strPesquisa) {
		return consultaCOON.pesquisarDiagnosticoSuggestionCount(strPesquisa);
	}
	
	@Override
	public List<McoConduta> listarCondutas(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Long codigo,
			String descricao, Integer faturamento, DominioSituacao situacao) {
		return this.cadastroCondutaRN.listarCondutas(firstResult, maxResults, orderProperty, asc, codigo, descricao, faturamento, situacao);
	}

	@Override
	public Long listarCondutasCount(Long codigo, String descricao, Integer faturamento, DominioSituacao situacao) {
		return this.cadastroCondutaRN.listarCondutasCount(codigo, descricao, faturamento, situacao);
	}

	@Override
	public void persistirConduta(McoConduta conduta) throws ApplicationBusinessException {
		this.cadastroCondutaRN.persistirConduta(conduta);
	}

	@Override
	public void ativarInativarConduta(McoConduta conduta) {
		this.cadastroCondutaRN.ativarInativarConduta(conduta);	
	}

	@Override
	public List<McoIndicacaoNascimento> pesquisarMcoIndicacaoNascimentoAtivosPorDescricaoOuSeq(String descricao, Integer maxResults) {
		return mcoIndicacaoNascimentoDAO.pesquisarAtivosPorDescricaoOuSeq(descricao, maxResults);
	}

	@Override
	public Long pesquisarMcoIndicacaoNascimentoAtivosPorDescricaoOuSeqCount(String descricao) {
		return mcoIndicacaoNascimentoDAO.pesquisarAtivosPorDescricaoOuSeqCount(descricao);
	}
	
	@Override
	public List<McoNascIndicacoes> pesquisarIndicacoesCesariana(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp) {
		List<McoNascIndicacoes> listRetorno = mcoNascIndicacoesDAO.pesquisarIndicacoesCesariana(gsoPacCodigo, gsoSeqp, seqp);
		// Inicializado por problema de LazyLoad
		for (McoNascIndicacoes item : listRetorno) {
			Hibernate.initialize(item.getIndicacaoNascimento());
		}
		return mcoNascIndicacoesDAO.pesquisarIndicacoesCesariana(gsoPacCodigo, gsoSeqp, seqp);
	}
	
	@Override
	public List<McoNascIndicacoes> pesquisarIndicacoesPartoInstrumentado(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp) {
		List<McoNascIndicacoes> listRetorno = mcoNascIndicacoesDAO.pesquisarIndicacoesPartoInstrumentado(gsoPacCodigo, gsoSeqp, seqp);
		// Inicializado por problema de LazyLoad
		for (McoNascIndicacoes item : listRetorno) {
			Hibernate.initialize(item.getIndicacaoNascimento());
		}
		return mcoNascIndicacoesDAO.pesquisarIndicacoesPartoInstrumentado(gsoPacCodigo, gsoSeqp, seqp);
	}
	
	@Override
	public boolean preGravarNascimento(Integer pacCodigo, Short gsoSeqp, DadosNascimentoVO nascimento) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.preGravarNascimento(pacCodigo, gsoSeqp, nascimento);
	}
	
	@Override
	public DadosNascimentoVO gravarNascimento(Integer pacCodigo, Short gsoSeqp, DadosNascimentoVO nascimentoVO,
			Short seqAnestesia) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.gravarNascimento(pacCodigo, gsoSeqp, nascimentoVO, seqAnestesia);
	}
	
	@Override
	public void excluirNascimento(DadosNascimentoVO nascimentoExcluir, Integer gsoPacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException {
		this.registrarGestacaoAbaNascimentoON.excluirNascimento(nascimentoExcluir, gsoPacCodigo, gsoSeqp);
	}
	
	@Override
	public List<TipoAnestesiaVO> pesquisarTiposAnestesiasAtivas(Object param) {
		return this.registrarGestacaoAbaNascimentoON.pesquisarTiposAnestesiasAtivas(param);
	}
	
	@Override
	public Long pesquisarTiposAnestesiasAtivasCount(Object param) {
		return this.registrarGestacaoAbaNascimentoON.pesquisarTiposAnestesiasAtivasCount(param);
	}
	
	@Override
	public List<IntercorrenciaNascsVO> pesquisarMcoIntercorrenciaNascsPorNascimento(Integer nasGsoPacCodigo, Short nasGsoSeqp,
			Integer nasSeqp) throws ApplicationBusinessException {
		return mcoIntercorrenciaNascsRN.pesquisarIntercorrenciasPorNascimento(nasGsoPacCodigo, nasGsoSeqp, nasSeqp);
	}	
	@Override
	public List<IntercorrenciaVO> pesquisarMcoIntercorrenciaAtivosPorSeqOuDescricao(String parametro) throws ApplicationBusinessException {
		return mcoIntercorrenciaRN.pesquisarMcoIntercorrenciaAtivosPorSeqOuDescricao(parametro);
	}	
	@Override
	public Long pesquisarMcoIntercorrenciaAtivosPorSeqOuDescricaoCount(String parametro) {
		return mcoIntercorrenciaDAO.pesquisarAtivosPorSeqOuDescricaoCount(parametro);
	}
	@Override
	public List<McoAnamneseEfs> obterListaMcoAnamneseEfs(Integer paciente, Short sequence) {
		return consultaCOON.listarMcoAnamnese(paciente, sequence);
	}
	@Override
	public List<CidVO> pesquisarCIDSuggestion(String strPesquisa) throws ApplicationBusinessException {
		return consultaCOON.pesquisarCIDSuggestion(strPesquisa);
	}
	@Override
	public Long pesquisarCIDSuggestionCount(String strPesquisa) throws ApplicationBusinessException {
		return consultaCOON.pesquisarCIDSuggestionCount(strPesquisa);
	}	
	@Override
	public IntercorrenciaNascsVO alterarMcoIntercorrenciaNascs(Integer consulta, IntercorrenciaNascsVO intercorrenciaNascsVO,
			Date dataHoraIntercorrencia, IntercorrenciaVO intercorrenciaVO, McoProcedimentoObstetricos procedimentoObstetrico)
			throws ApplicationBusinessException {
		return mcoIntercorrenciaNascsRN.alterarMcoIntercorrenciaNascs(consulta, intercorrenciaNascsVO, dataHoraIntercorrencia,
				intercorrenciaVO, procedimentoObstetrico);
	}
	@Override
	public IntercorrenciaNascsVO inserirMcoIntercorrenciaNascs(Integer nasGsoPacCodigo, Short nasGsoSeqp, Integer nasSeqp,
			Integer consulta, Date dataHoraIntercorrencia, IntercorrenciaVO intercorrenciaVO,
			McoProcedimentoObstetricos procedimentoObstetrico) throws ApplicationBusinessException {
		return mcoIntercorrenciaNascsRN.inserirMcoIntercorrenciaNascs(nasGsoPacCodigo, nasGsoSeqp, nasSeqp, consulta,
				dataHoraIntercorrencia, intercorrenciaVO, procedimentoObstetrico);
	}
	@Override
	public void removerMcoIntercorrenciaNascs(IntercorrenciaNascsVO intercorrenciaNascsVO) throws ApplicationBusinessException {
		mcoIntercorrenciaNascsRN.removerMcoIntercorrenciaNascs(intercorrenciaNascsVO);
	}	
	@Override
	public List<EquipeVO> pesquisarEquipeAtivaCO(Object param) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.pesquisarEquipeAtivaCO(param);
	}	
	@Override
	public Long pesquisarEquipeAtivaCOCount(Object param) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.pesquisarEquipeAtivaCOCount(param);
	}	
	@Override
	public EquipeVO obterEquipePorId(Short eqpSeq) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.obterEquipePorId(eqpSeq);
	}	
	@Override
	public List<SalaCirurgicaVO> obterSalasCirurgicasAtivasPorUnfSeqNome(Object param) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.obterSalasCirurgicasAtivasPorUnfSeqNome(param);
	}	
	@Override
	public Long obterSalasCirurgicasAtivasPorUnfSeqNomeCount(Object param) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.obterSalasCirurgicasAtivasPorUnfSeqNomeCount(param);
	}
	@Override
	public SalaCirurgicaVO obterDadosSalaCirurgica(Short unfSeq, Short seqp) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.obterDadosSalaCirurgica(unfSeq, seqp);
	}	
	@Override
	public EquipeVO buscarEquipeAssociadaLaudoAih(Integer conNumero, Integer pacCodigo) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaNascimentoON.buscarEquipeAssociadaLaudoAih(conNumero, pacCodigo);
	}	
	@Override
	public List<RapServidorConselhoVO> listarProfissionais(Short gsoSeqp, Integer gsoPacCodigo, Integer nasSeqp) throws ApplicationBusinessException{
		return mcoProfNascsRN.listarProfissionais(gsoSeqp, gsoPacCodigo, nasSeqp);
	}	
	@Override
	public void gravarMcoProfNasc(McoProfNascs profNascs) throws ApplicationBusinessException{
		mcoProfNascsRN.gravarMcoProfNasc(profNascs);
	}	
	@Override
	public void excluirMcoProfNasc(McoProfNascsId id) throws ApplicationBusinessException{
		mcoProfNascsRN.excluirMcoProfNasc(id);
	}	
	@Override
	public List<RapServidorConselhoVO> pesquisarServidoresConselhoPorSiglaCentroCusto(final String sigla) throws ApplicationBusinessException{
		return mcoProfNascsRN.pesquisarServidoresConselhoPorSiglaCentroCusto(sigla);
	}	
	@Override
	public Long pesquisarServidoresConselhoPorSiglaCentroCustoCount(final String sigla) throws ApplicationBusinessException{
		return mcoProfNascsRN.pesquisarServidoresConselhoPorSiglaCentroCustoCount(sigla);
	}
	@Override
	public List<RapServidorConselhoVO> pesquisarServidoresPorSiglaConselhoNumeroNome(String sigla, String param) throws ApplicationBusinessException{
		return mcoProfNascsRN.pesquisarServidoresPorSiglaConselhoNumeroNome(sigla, param);
	}
	@Override
	public Integer pesquisarServidoresPorSiglaConselhoNumeroNomeCount(String sigla, String param) throws ApplicationBusinessException{
		return mcoProfNascsRN.pesquisarServidoresPorSiglaConselhoNumeroNomeCount(sigla, param);
	}
	@Override
	public void excluirConduta(McoPlanoIniciais planoIniciais, McoAnamneseEfs anamneseEfs) throws BaseException {
		consultaCOON.excluirCondutas(planoIniciais, anamneseEfs);
	}
	@Override
	public McoNascimentos obterMcoNascimentosPorChavePrimaria(Integer seqp, Integer codigoPaciente, Short sequence){
		return this.mcoNascimentosDAO.obterMcoNascimento(seqp, codigoPaciente, sequence);
	}
	@Override
	public List<br.gov.mec.aghu.emergencia.vo.MedicamentoVO> pesquisarTrabPartoMedicamentos(Integer gsoPacCodigo, Short gsoSeqp) {
		return registrarGestacaoAbaTrabalhoPartoRN.pesquisarTrabPartoMedicamentos(gsoPacCodigo, gsoSeqp);
	}
	@Override
	public void excluirMedicamento(Integer matCodigo, Short gsoSeqp,Integer gsoPacCodigo) {
		this.registrarGestacaoAbaTrabalhoPartoRN.excluirMedicamento(matCodigo,gsoSeqp,gsoPacCodigo);		
	}
	@Override
	public McoBolsaRotas buscarBolsaRotas(Integer pacCodigo, Short seqp) {
		return this.mcoBolsaRotasDAO.buscarBolsaRotas(pacCodigo,seqp);
	}
	@Override
	public McoAtendTrabPartos buscarMcoAtendTrabPartos(Integer pacCodigo, Short seqp) {
		return this.registrarGestacaoAbaTrabalhoPartoRN.buscarMcoAtendTrabPartos(pacCodigo,seqp);
	}
	
	@Override
	public List<McoAtendTrabPartos> buscarListaMcoAtendTrabPartos(Integer pacCodigo, Short seqp) {
		return this.registrarGestacaoAbaTrabalhoPartoRN.buscarListaMcoAtendTrabPartos(pacCodigo,seqp);
	}
	
	@Override
	public McoTrabPartos buscarMcoTrabPartos(Integer pacCodigo, Short seqp) {
		return this.registrarGestacaoAbaTrabalhoPartoRN.buscarMcoTrabPartos(pacCodigo,seqp);
	}
	
	@Override
	public Boolean verificarSumarioPrevio(Integer pacCodigo, Short seqp){
		return this.registrarGestacaoAbaTrabalhoPartoRN.verificarSumarioPrevio(seqp, pacCodigo);
	}	
	@Override
	public List<McoConduta> pesquisarMcoCondutaSuggestion(String strPesquisa) {
		return consultaCOON.pesquisarMcoCondutaSuggestion(strPesquisa);
	}
	@Override
	public Long pesquisarMcoCondutaSuggestionCount(String strPesquisa) {
		return consultaCOON.pesquisarMcoCondutaSuggestionCount(strPesquisa);
	}
	@Override
	public void validarDataConsulta(Date dthrConsulta) throws ApplicationBusinessException {
		consultaCOON.validarDataConsulta(dthrConsulta);
	}
	@Override
	public void persistirMcoAnamneseEfs(McoAnamneseEfs anamneseEfs, Integer conNumero, Integer pacCodigo, Short seqp) throws BaseException {
		consultaCOON.persistirMcoAnamneseEfs(anamneseEfs, conNumero, pacCodigo, seqp);
	}
	@Override
	public void verificarDilatacao(String dilatacao) throws BaseException {
		consultaCOON.verificarDilatacao(dilatacao);
	}	
	public Long pesquisarExamesCount(String param) throws ApplicationBusinessException {
		return registrarGestacaoRN.pesquisarExamesCount(param);
	}
	public List<UnidadeExamesSignificativoPerinatologiaVO> pesquisarExames(String param) throws ApplicationBusinessException {
		return registrarGestacaoRN.pesquisarExames(param);
	}	
	@Override
	public McoIndicacaoNascimento obterMcoIndicacaoNascimentoPorChavePrimaria(Integer pk){
		return mcoIndicacaoNascimentoDAO.obterPorChavePrimaria(pk);
	}	
	@Override
	public List<McoPlanoIniciais> listarMcoPlanoIniciaisConduta(Integer numeroConsulta, Short seqp, Integer pacCodigo){
		return this.consultaCOON.listarMcoPlanoIniciaisConduta(numeroConsulta, seqp, pacCodigo);
	}	
	@Override
	public McoAnamneseEfs obterMcoAnamneseEfsPorId(McoAnamneseEfsId pk){
		return this.mcoAnamneseEfsDAO.obterPorChavePrimaria(pk, true, McoAnamneseEfs.Fields.CONSULTA, McoAnamneseEfs.Fields.GESTACAO);
	}	
	@Override
	public void insereConduta(McoConduta conduta, McoAnamneseEfs anamneseEfs, String complemento){
		this.consultaCORN.insereConduta(conduta, anamneseEfs, complemento);
	}	
	
	@Override
	@BypassInactiveModule
	public List<McoNascimentos> listarNascimentos(Integer codigoPaciente,
			Short sequence) {
		return this.mcoNascimentosDAO.listarNascimentos(codigoPaciente,
				sequence);
	}
	
	@Override
	@BypassInactiveModule
	public List<McoRecemNascidos> pesquisarMcoRecemNascidoPorGestacaoOrdenado(Integer pacCodigo, Short seqp) {
		return mcoRecemNascidosDAO.pesquisarMcoRecemNascidoPorGestacaoOrdenado(pacCodigo, seqp);
	}
	
	@Override
	@BypassInactiveModule
	public McoAnamneseEfs obterAnamnesePorPaciente(Integer pacCodigo, Short gsoSeqp) {
		return mcoAnamneseEfsDAO.obterAnamnesePorPaciente(pacCodigo, gsoSeqp);
	}
	
	@Override
	@BypassInactiveModule
	public List<HistObstetricaVO> pesquisarAnamnesesEfsPorGestacoesPaginada(
			Integer gsoPacCodigo, Short gsoSeqp, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.mcoAnamneseEfsDAO
				.pesquisarAnamnesesEfsPorGestacoesPaginada(gsoPacCodigo,
						gsoSeqp, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarAnamnesesEfsPorGestacoesPaginadaCount(
			Integer codigo, Short seqp) {
		return this.mcoAnamneseEfsDAO.pesquisarAnamnesesEfsPorGestacoesPaginadaCount(codigo,seqp);
	}
	
	@Override
	@BypassInactiveModule
	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorAdmissaoObs(
			Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		return this.mcoLogImpressoesDAO
				.pesquisarLogImpressoesEventoMcorAdmissaoObs(gsoPacCodigo,
						gsoSeqp, conNumero);
	}
	
	@Override
	@BypassInactiveModule
	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorConsultaObs(
			Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		return this.mcoLogImpressoesDAO
				.pesquisarLogImpressoesEventoMcorConsultaObs(gsoPacCodigo,
						gsoSeqp, conNumero);
	}
	
	@Override
	public void atualizaConduta(McoConduta conduta, McoAnamneseEfs anamneseEfs, String complemento){
		this.consultaCORN.atualizaConduta(conduta, anamneseEfs, complemento);
	}
	@Override
	public RapServidorConselhoVO obterRapPessoasConselhoPorMatriculaVinculo(Short vinculo, Integer matricula) throws ApplicationBusinessException {
		return this.registrarGestacaoAbaTrabalhoPartoRN.obterRapPessoasConselhoPorMatriculaVinculo(vinculo, matricula);
	}	
	@Override
	public McoTrabPartos obterMcoTrabPartoPorId(Integer pacCodigo, Short seqp) {
		return this.mcoTrabPartosDAO.obterMcoTrabPartosPorId(pacCodigo, seqp);		
	}
	@Override
	public McoBolsaRotas obterBolsaRotaPorId(Integer pacCodigo, Short seqp){
		return this.mcoBolsaRotasDAO.buscarBolsaRotas(pacCodigo, seqp);
	}	
	@Override
	public void ingressarConsultaSO(Integer codigoPaciente, Integer numeroConsulta, String hostName) throws ApplicationBusinessException {
		this.consultaCOON.ingressarPacienteSO(codigoPaciente, numeroConsulta, hostName);
	}
	@Override
	public void gravarConsultaCO(McoAnamneseEfs anamneseEfs, McoAnamneseEfs anamneseEfsOriginal) throws ApplicationBusinessException{
		this.consultaCOON.gravarConsultaCO(anamneseEfs, anamneseEfsOriginal);
	}	
	@Override
	public void validarDadosConsultaCO(McoBolsaRotas bolsaRotas) throws ApplicationBusinessException{
		this.registrarGestacaoAbaTrabalhoPartoRN.validarDadosConsultaCO(bolsaRotas);
	}	
	@Override
	public void validarDadosBolsaRotas(McoBolsaRotas bolsaRotas) throws ApplicationBusinessException{
		this.registrarGestacaoAbaTrabalhoPartoRN.validarDadosBolsaRotas(bolsaRotas);
	}
	@Override
	public Object[] carregarExames(Integer gsoPacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		return registrarGestacaoRN.carregarExames(gsoPacCodigo, gsoSeqp);
	}	
	@Override
	public McoLogImpressoes verificaImpressaoAdmissaoObstetrica(Integer pacCodigo, Short seqP){
		return this.mcoLogImpressoesDAO.verificaImpressaoAdmissaoObstetrica(pacCodigo, seqP);
	}	
	@Override
	public Long obtemQuantidadeDefetosDaGestante(Integer pacCodigo, Short seqp){
		return this.consultaCOON.obtemQuantidadeDefetosDaGestante(pacCodigo, seqp);
	}	
	@Override
	public boolean verificarAlteracaoTela(McoAnamneseEfs anamneseEfs, McoAnamneseEfs anamneseEfsOriginal){
		return this.consultaCOON.verificarAlteracaoTela(anamneseEfs, anamneseEfsOriginal);
	}
	@Override
	public void elaborarPrescricaMedica(Integer numeroConsulta, Short seqUnidadeFuncional) throws ApplicationBusinessException {
		consultaCOON.elaborarPrescricaoMedica(numeroConsulta, seqUnidadeFuncional);
	}	
	@Override
	public List<CidVO> obterCidPorSeq(List<Integer> cids) throws ServiceException{
		return this.consultaCOON.obterCidPorSeq(cids);
	}
	@Override
	public void ajustarDesbloqueioConsultaCO(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException {
		consultaCOON.ajustarDesbloqueioConsultaCO(desbloqueioConsultaCOVO, gsoPacCodigo, gsoSeqp, conNumero);
	}
	@Override
	public void ajustarDesbloqueioConsultaCOSelecionada(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException {
		consultaCOON.ajustarDesbloqueioConsultaCOSelecionada(desbloqueioConsultaCOVO, gsoPacCodigo, gsoSeqp, conNumero);
	}
	@Override
	public void desbloquearConsultaCO(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException {
		consultaCOON.desbloquearConsultaCO(desbloqueioConsultaCOVO, gsoPacCodigo, gsoSeqp, conNumero);		
	}
	@Override
	public void finalizarConsulta(Integer conNumero, Integer gsoPacCodigo, Short gsoSeqp, Integer matricula, Short vinCodigo, Date criadoEm, String hostName, Boolean isEmergenciaCustom) throws ApplicationBusinessException{
		finalizarConsultaRN.finalizarConsulta(conNumero, gsoPacCodigo, gsoSeqp, matricula, vinCodigo, criadoEm, hostName, isEmergenciaCustom);
	}
	@Override
	public void validarDadosGestacional(Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		this.consultaCOON.validarDadosGestacional(pacCodigo, seqp);
	}	
	@Override
	public boolean gravarSumarioDefinitivo(Integer pacCodigo, 
											Short seqp, 
											Integer conNumero, 
											String hostName, 
											DadosNascimentoVO nascimentoSelecionado,
											DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
											DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException{
		return this.registrarGestacaoAbaNascimentoRN.gravarSumarioDefinitivo(pacCodigo, 
																				seqp,
																				conNumero,
																				hostName,
																				nascimentoSelecionado,
																				dadosNascimentoSelecionado,
																				dadosNascimentoSelecionadoOriginal);
	}	
	@Override
	public Integer solicitarExameVdrl(Integer conNumero){
		return this.registrarGestacaoAbaNascimentoRN.solicitarExameVdrl(conNumero);
	}	
	@Override
	public Object[] obterConselhoESiglaVRapServidorConselho(){
		return this.registrarGestacaoAbaNascimentoRN.obterConselhoESiglaVRapServidorConselho();
	}	
	@Override
	public void executaImpressaoGeracaoPendenciaAssinatura(Integer pacCodigo, Short gsoSeqp){
		this.registrarGestacaoAbaNascimentoRN.executaImpressaoGeracaoPendenciaAssinatura(pacCodigo, gsoSeqp);
	}	
	@Override
	public Boolean gerarPendenciaAssinaturaDigital() throws ServiceException, ApplicationBusinessException{
		return this.registrarGestacaoAbaNascimentoRN.gerarPendenciaAssinaturaDigital();
	}	
	@Override
	public void verificarIdadeGestacional(Integer gsoPacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		this.finalizarConsultaRN.verificarIdadeGestacional(gsoPacCodigo, gsoSeqp);	
	}
	@Override
	public void verificaSeExisteCondutaSemComplementoNaoCadastrada(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero)
			throws ApplicationBusinessException {
		this.finalizarConsultaRN.verificaSeExisteCondutaSemComplementoNaoCadastrada(gsoPacCodigo, gsoSeqp, conNumero);		
	}
	@Override
	public boolean verificarTipoDeGravidez(Integer pacCodigo, Short seqp) {
		return this.realizarInternacaoON.verificarTipoDeGravidez(pacCodigo, seqp);		
	}
	@Override
	public void realizarInternacao(Integer conNumero, Integer gsoPacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		this.realizarInternacaoON.realizarInternacao(conNumero, gsoPacCodigo, gsoSeqp);		
	}
	@Override
	public boolean verificarExameVDRLnaoSolicitado(Integer atdSeq) throws ApplicationBusinessException {
		return this.realizarInternacaoON.verificarExameVDRLnaoSolicitado(atdSeq);
	}
	@Override
	public RapServidorConselhoVO verificarPermissaoUsuario(Integer matricula, Short vinCodigo) throws ApplicationBusinessException, ServiceException {
		return this.finalizarConsultaRN.verificarPermissaoUsuario(matricula, vinCodigo);		
	}
	
	@Override
	public boolean verificarPermissaoUsuarioImprimirRelatorioDefinitivo(Integer matricula, Short vinCodigo) throws ApplicationBusinessException {
		return this.finalizarConsultaRN.verificarPermissaoUsuarioImprimirRelatorioDefinitivo(matricula, vinCodigo);		
	}
	
	protected McoGestacoesDAO getMcoGestacoesDAO() {
		return mcoGestacoesDAO;
	}
	
	@Override
	public Integer obterSeqAtendimentoPorConNumero(Integer conNumero) throws ApplicationBusinessException {
		return this.realizarInternacaoON.obterSeqAtendimentoPorConNumero(conNumero);
	}
	@Override
	public void inserirLogImpressao(Integer conNumero, Integer gsoPacCodigo, Short gsoSeqp, String evento, Integer matricula, Short vinCodigo, Date criadoEm) throws ApplicationBusinessException {
		this.finalizarConsultaRN.inserirLogImpressao(conNumero, gsoPacCodigo, gsoSeqp, evento, matricula, vinCodigo, criadoEm);		
	}
	@Override
	public boolean gerarPendenciaDeAssinaturaDigital(Integer matricula, Short vinCodigo) throws ApplicationBusinessException {
		return this.finalizarConsultaRN.gerarPendenciaDeAssinaturaDigital(matricula, vinCodigo);
	}
	@Override
	public void realizarInternacaoPacienteAutomaticamente(Integer matricula, Short vinCodigo, Integer pacCodigo, Short seqp, Integer numeroConsulta, String hostName, Long trgSeq) throws ApplicationBusinessException {
		this.realizarInternacaoON.realizarInternacaoPacienteAutomaticamente(matricula, vinCodigo, pacCodigo, seqp, numeroConsulta, hostName, trgSeq);
	}	
	@Override
	public void validarConsultaFinalizada(Integer pacCodigo, Short seqp, Integer numeroConsulta) throws ApplicationBusinessException {
		consultaCORN.validarConsultaFinalizada(pacCodigo, seqp, numeroConsulta);
	}
	@Override
	public boolean moduloAgendamentoExameAtivo() {
		return consultaCORN.moduloAgendamentoExameAtivo();
	}
	@Override
	public void verificaPermissoesImpressaoRelatorio(Integer matricula, Short codigo, String usuarioLogado) throws ApplicationBusinessException {
		consultaCORN.verificaPermissoesImpressaoRelatorio(matricula, codigo, usuarioLogado); 
	}
	@Override
	public Integer obterAtendimentoPorNumConsulta(Integer numConsulta) {
		return consultaCORN.obterAtendimentoPorNumConsulta(numConsulta);
	}	
	@Override
	public boolean verificarSeModuloEstaAtivo(String nome) throws ApplicationBusinessException {
		return this.registrarGestacaoRN.verificarSeModuloEstaAtivo(nome);
	}
	@Override
	public void verificarPermissaoParaSolicitarExames(Integer matricula, Short vinCodigo) throws ApplicationBusinessException, ServiceException {
		this.registrarGestacaoRN.verificarPermissaoParaSolicitarExames(matricula, vinCodigo);		
	}
	@Override
	public Integer obterAtendimentoSeqPorNumeroDaConsulta(Integer numeroConsulta) {
		return this.registrarGestacaoRN.obterAtendimentoSeqPorNumeroDaConsulta(numeroConsulta);
	}

	@Override
	public void gravarNotaAdicional(Integer pacCodigo, Short gsoSeqp, Integer conNumero, DominioEventoNotaAdicional evento, String notaAdicional) throws ApplicationBusinessException {
		this.notaAdicionalRN.gravarNotaAdicional(pacCodigo, gsoSeqp, conNumero, evento, notaAdicional);
	}

	@Override
	public String buscarUltimoEventoParaCadastrarNotaAdicional(Integer pacCodigo,	Short seqp, Integer numeroConsulta) {
		DominioEventoLogImpressao[] eventos = new DominioEventoLogImpressao[] { DominioEventoLogImpressao.MCOR_CONSULTA_OBS, DominioEventoLogImpressao.MCOR_ADMISSAO_OBS };
		return mcoLogImpressoesDAO.buscarUltimoEvento(pacCodigo, seqp, numeroConsulta, eventos);	
	}

	@Override
	public boolean gerarPendenciaDeAssinaturaDigitalDoUsuarioLogado() throws ApplicationBusinessException {
		return notaAdicionalRN.gerarPendenciaDeAssinaturaDigitalDoUsuarioLogado();
	}

	@Override
	public List<RecemNascidoVO> listarRecemNascidoVOs(RecemNascidoFilterVO filter) throws BaseException {
		return recemNascidoON.listarRecemNascidoVOs(filter);
	}

	@Override
	public List<RegiaoAnatomicaVO> pesquisarRegioesAnatomicas(String param) {	
		return mcoAchadoON.pesquisarRegioesAnatomicasAtivasPorDesc(param);
	}

	@Override
	public List<AchadoVO> pesquisarAchados(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			AchadoVO filtro) {
		return mcoAchadoON.pesquisarAchadosPorDescSituacaoRegiaoAnatomicas(firstResult, maxResults, orderProperty, asc, filtro);
	}

	@Override
	public Long pesquisarAchadosCount(AchadoVO filtro) {		
		return mcoAchadoON.pesquisarAchadosCount(filtro);
	}

	@Override
	public McoAchado obterAchadoPorChavePrimaria(Integer seq) {		
		return mcoAchadoDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public void ativarInativarAchado(McoAchado entity) throws ApplicationBusinessException {		
		mcoAchadoON.ativarInativarAchado(entity);
	}

	@Override
	public void atualizarAchado(McoAchado achado) throws ApplicationBusinessException {	
		mcoAchadoON.atualizarAchado(achado);
	}
	
	@Override
	public void inserirAchado(McoAchado achado, String descricaoRegiao) throws ApplicationBusinessException {
		mcoAchadoON.inserirAchado(achado, descricaoRegiao);
	}

	@Override
	public Long pesquisarRegioesAnatomicasCount(String param) {
		return mcoAchadoON.pesquisarRegioesAnatomicasAtivasPorDescCount(param);
	}

	@Override
	public Long listaMcoSindromeAtivaCount(Object param) {
		return mcoSindromeDAO.listaMcoSindromeAtivaCount(param);
	}
	
	@Override
	public List<McoSindrome> listarMcoSindromeAtiva(Object param) {
		return mcoSindromeDAO.listarMcoSindromeAtiva(param);
	}
	
	@Override
	public List<RegistrarExameFisicoVO> obterExameFisicoRN(Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException{
		return registrarGestacaoAbaExtFisicoRNON.obterExameFisicoRN(pacCodigo, gsoSeqp);
	}
	
	@Override
	public RegistrarExameFisicoVO validarRegistroExameFisico(RegistrarExameFisicoVO vo) throws ApplicationBusinessException {
		return registrarGestacaoAbaExtFisicoRNON.validarRegistroExameFisico(vo);
	}
	
	@Override
	public List<McoSindrome> listarSindrome(Integer seq, String descricao, String situacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return mcoSindromeDAO.listarSindrome(seq, descricao, situacao, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long listarSindromeCount(Integer seq, String descricao, String situacao) {
		return mcoSindromeDAO.listarSindromeCount(seq, descricao, situacao);
	}
	
	@Override
	public void persistirSindrome(McoSindrome entity) {
		mcoSindromeDAO.persistir(entity);
	}
	
	@Override
	public void adicionarRecemNascido(RecemNascidoVO vo, Integer quantidadeRemcemNascido) throws BaseException {
		recemNascidoON.adicionarRecemNascido(vo, quantidadeRemcemNascido);
	}
	
	@Override
	public boolean temGravidezConfirmada(Integer pacCodigo, Short gsoSeqp) {
		return recemNascidoON.temGravidezConfirmada(pacCodigo, gsoSeqp);
	}
	
	@Override
	public void validarRecemNascido(RecemNascidoVO vo) throws BaseException {
		recemNascidoON.validarRecemNascido(vo);
	}

	@Override
	public void excluirRecemNascido(RecemNascidoVO vo, List<RecemNascidoVO> listRecemNascidoVOs) throws BaseException {
		recemNascidoON.excluirRecemNascido(vo, listRecemNascidoVOs);
	}
	
	@Override
	public McoSindrome obterSindromePorDescricao(String descricao) {
		return mcoSindromeDAO.obterSindromePorDescricao(descricao);
	}
	
	@Override
	public McoSindrome obterSindromePorChavePrimaria(Integer seq) {
		return mcoSindromeDAO.obterPorChavePrimaria(seq);
	}
	
		


	@Override
	public List<EscalaLeitoRecemNascidoVO> pesquisarEscalaLeitoRecemNascido(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Servidor servidor, LeitoVO leito) throws ApplicationBusinessException {
		
		return mcoEscalaLeitoRecemNascidoRN.pesquisarEscalaLeitoRecemNascido(firstResult, maxResults, orderProperty, asc, servidor, leito);
	}

	@Override
	public Long pesquisarEscalaLeitoRecemNascidoCount(Servidor servidor,
			LeitoVO leito) throws ApplicationBusinessException {
		return mcoEscalaLeitoRecemNascidoRN.pesquisarEscalaLeitoRecemNascidoCount(servidor, leito);
	}

	@Override
	public void deletarMcoEscalaLeitoRecemNascidoPorId(
			McoEscalaLeitoRecemNascidoId id) throws BaseException {
		mcoEscalaLeitoRecemNascidoRN.deletarMcoEscalaLeitoRecemNascidoPorId(id);
		
	}

	@Override
	public void inserirMcoEscalaLeitoRecemNascido(McoEscalaLeitoRecemNascido entity)
			throws ApplicationBusinessException {
		mcoEscalaLeitoRecemNascidoRN.inserirMcoEscalaLeitoRecemNascido(entity);
		
	}

	@Override
	public List<LeitoVO> pesquisarLeitos(String param) throws ApplicationBusinessException {
		return mcoEscalaLeitoRecemNascidoRN.pesquisarLeitoPorLeitoIDUnfs(param);
	}

	@Override
	public Long pesquisarLeitosCount(String param) throws ApplicationBusinessException {
		return mcoEscalaLeitoRecemNascidoRN.pesquisarLeitoPorLeitoIDUnfsCount(param);
	}

	@Override
	public List<Servidor> pesquisarServidores(String param) throws ApplicationBusinessException {
		return mcoEscalaLeitoRecemNascidoRN.buscarProfissionaisNeoPediatriaPorCodigoMatriculaNome(param);
	}

	@Override
	public Long pesquisarServidoresCount(String param) throws ApplicationBusinessException {
		return mcoEscalaLeitoRecemNascidoRN.buscarProfissionaisNeoPediatriaPorCodigoMatriculaNomeCount(param);
	}	
	
	@Override
	public void  validarExclusao(Integer gsoPacCodigo, Short gsoSeqp, RegistrarExameFisicoVO vo) throws ApplicationBusinessException{
		registrarGestacaoAbaExtFisicoRNON.validarExclusao(gsoPacCodigo, gsoSeqp, vo);
	}
		
	@Override
	public boolean alterarRegistroExameFisico(RegistrarExameFisicoVO voAlterado, RegistrarExameFisicoVO voOriginal ){
		return registrarGestacaoAbaExtFisicoRNON.alterarRegistroExameFisico(voAlterado, voOriginal );
	}
	
	@Override
	public List<McoTabBallard> listarBallard(Short escore, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return mcoTabBallardDAO.listarBallard(escore,  firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long listarBallardCount(Short escore) {
		return mcoTabBallardDAO.listarBallardCount(escore);
	}
	
	@Override
	public McoTabBallard obterBallardPorChavePrimaria(Short seq) {
		return mcoTabBallardDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public void removerBallard(Short seq) {
		mcoTabBallardDAO.remover(obterBallardPorChavePrimaria(seq));
	}
	
	@Override
	public McoRecemNascidos obterRecemNascidosPorCodigo(Integer codigoPaciente) {
		return mcoRecemNascidosDAO.obterRecemNascidosPorCodigo(codigoPaciente);
	}
	
	@Override
	public McoRecemNascidos obterRecemNascidoPorId(McoRecemNascidosId id) {
		return mcoRecemNascidosDAO.obterPorChavePrimaria(id);
	}
	
	@Override
	public Paciente obterPacienteRecemNascido(Integer pacCodigoRecemNascido) throws ApplicationBusinessException {
		return recemNascidoON.obterPacienteRecemNascido(pacCodigoRecemNascido);
	}
	
	@Override
	public List<McoApgars> obterListaApgarPorCodigoPaciente(Integer codigoPaciente, Integer matricula, Short vinculo){
		return mcoApgarsDAO.obterListaApgarPorCodigoPaciente(codigoPaciente, matricula, vinculo);
	}
	
	@Override
	public void persistirApgar(McoApgars apgar) throws BaseException {
		apgarON.persistir(apgar);
	}
	
	@Override
	public boolean validarAlteracaoRecebemNascido(RecemNascidoVO voAlterado, RecemNascidoVO voOriginal){
		return recemNascidoON.validarAlteracaoRecebemNascido(voAlterado, voOriginal);
	}

	@Override
	public boolean verificarExisteRegistroRecemNascido(RecemNascidoVO vo) {		
		return recemNascidoON.verificarExistRegistroRecemNascido(vo);
	}

	@Override
	public void validarConsultaFinalizada(Integer pacCodigo, Short seqp,
			Integer numeroConsulta, DominioEventoLogImpressao... eventos)
			throws ApplicationBusinessException {
		recemNascidoON.validarConsultaFinalizada(pacCodigo, seqp, numeroConsulta, eventos);
	}

	@Override
	public Integer obterUltimoAtdSeqRecemNascidoPorPacCodigo(
			Integer pacCodigoRecemNascido) {
		return recemNascidoON.obterUltimoAtdSeqRecemNascidoPorPacCodigo(pacCodigoRecemNascido);
	}
	
	@Override
	public void validarBCF(McoAnamneseEfs anamneseEfs, boolean permNrBfc, int tipoBCF) throws ApplicationBusinessException{
		mcoAnamneseEfsRN.validarBCF(anamneseEfs, permNrBfc, tipoBCF);
	}

	@Override
	public List<MedicamentoRecemNascidoVO> buscarMedicamentosPorDescricao(
			String descricao) {	
		return medicamentoRecemNascidoRN.buscarMedicamentosPorDescricao(descricao);
	}

	@Override
	public List<ViaAdministracaoVO> pesquisarViaAdminstracaoSiglaouDescricao(
			String param) {
		return medicamentoRecemNascidoRN.pesquisarViaAdminstracaoSiglaouDescricao(param);
	}

	@Override
	public List<MedicamentoRecemNascidoVO> buscarMedicamentosPorRecemNascido(
			Integer rnaGsoPacCodigo, Short rnaGsoSeqp, Byte rnaSeqp) {
		return medicamentoRecemNascidoRN.buscarMedicamentosPorRecemNascido(rnaGsoPacCodigo, rnaGsoSeqp, rnaSeqp);
	}

	@Override
	public Long buscarMedicamentosCountPorDescricao(String descricao) {
		return medicamentoRecemNascidoRN.buscarMedicamentosCountPorDescricao(descricao);
	}

	@Override
	public Long pesquisarViaAdminstracaoSiglaouDescricaoCount(String param) {
		return medicamentoRecemNascidoRN.pesquisarViaAdminstracaoSiglaouDescricaoCount(param);
	}

	@Override
	public void validarMedicamento(
			MedicamentoRecemNascidoVO medicamento,
			List<MedicamentoRecemNascidoVO> lista)
			throws ApplicationBusinessException {
		medicamentoRecemNascidoRN.validarMedicamento(medicamento, lista);
	}


	
	@Override
	public List<TabAdequacaoPesoPercentilVO> listaTabAdequacaoPeso(){
		return  snappeON.listaTabAdequacaoPeso();
	}
	

	@Override
	public List<SnappeElaboradorVO> listarSnappeElaboradorVO(Integer pacCodigo) throws ApplicationBusinessException{
		return  snappeON.listaSnappe(pacCodigo);
	}
	
	@Override
	public McoSnappes obterMcoSnappePorId(Integer pacCodigo, Short seqp){
		return mcoSnappesDAO.obterMcoSnappePorId(pacCodigo, seqp);
		
	}
	
	@Override
	public boolean verificarUsuarioAlteracaoSnappe(McoSnappes snappe){
		return snappeON.verificarUsuarioAlteracao(snappe);
	}
	
	@Override
	public Integer calcularSnappe(McoSnappes snappe){
		return snappeRN.calcular(snappe);
	}
	
	@Override
	public void gravarSnappe(McoSnappes snappe, boolean novo) throws ApplicationBusinessException{
		snappeRN.gravar(snappe, novo);
	}
	
	@Override
	public void excluirSnappe(McoSnappes snappe) throws ApplicationBusinessException{
		snappeRN.excluir(snappe);
	}
	
	@Override
	public McoSnappes obterSnappePrePreenchido(Integer pacCodigo) throws ApplicationBusinessException{
		return snappeRN.obterSnappePrePreenchido(pacCodigo);
	}
	
	
	@Override
	public Short obterMaxSeqpSnappesPorCodigoPaciente(Integer pacCodigo){
		return mcoSnappesDAO.obterMaxSeqpSnappesPorCodigoPaciente(pacCodigo);
		
	}
	@Override
	public boolean existeRegistroUsuarioSnappe(List<SnappeElaboradorVO> lista){
		return snappeRN.existeRegistroUsuario(lista);
	}
	@Override
	public boolean verificarAtualizacaoSnappe(McoSnappes snappe){
		return snappeON.verificarAtualizacao(snappe);
	}
	
	@Override
	public List<CalculoCapurroVO> listarCalculoCapurrosPorCodigoPaciente(Integer pacCodigo) {
		return this.calculoCapurroON.listarCalculoCapurrosPorCodigoPaciente(pacCodigo);
	}
	
	@Override
	public boolean possuiAlteracaoIddGestCapurros(CalculoCapurroVO calculoSelecionado) {
		return this.calculoCapurroON.possuiAlteracaoIddGestCapurros(calculoSelecionado);
	}
	
	@Override
	public void persistirMcoIddGestCapurros(CalculoCapurroVO calculoCapurroVO) throws ApplicationBusinessException {
		this.calculoCapurroRN.persistirMcoIddGestCapurros(calculoCapurroVO);
	}
	
	@Override
	public void excluirMcoIddGestCapurros(CalculoCapurroVO calculoCapurroVO) {
		this.calculoCapurroRN.excluirMcoIddGestCapurros(calculoCapurroVO);
	}
	
	@Override
	public boolean verificarSumariaAlta(McoSnappes snappe) throws ApplicationBusinessException{
		return snappeON.verificarSumariaAlta(snappe);
	}

	@Override
	public boolean isMedicamentoRecemNascidoCadastrado(Integer rnaGsoPacCodigo,
			Short rnaGsoSeqp, Byte rnaSeqp, Integer seqPni) {
		return medicamentoRecemNascidoRN.isMedicamentoRecemNascidoCadastrado(rnaGsoPacCodigo, rnaGsoSeqp, rnaSeqp, seqPni);
	}

	@Override
	public void validarListaUnidadesFuncionais(List<Short> bucarCaracteristicasUnidadesFuncionais)throws ApplicationBusinessException {
		registrarGestacaoAbaNascimentoON.validarListaUnidadesFuncionais(bucarCaracteristicasUnidadesFuncionais);
	}

	@Override
	public List<DataNascimentoVO> buscarDatasDoNascimentos(Integer pacCodigo, Short gsoSeqp) {
		return this.mcoNascimentosDAO.buscarDatasDoNascimentos(pacCodigo, gsoSeqp);
	}

	@Override
	public void verificaSeExisteCondutaSemComplementoNaoCadastrada(McoConduta conduta, McoPlanoIniciais planoIniciais)
			throws ApplicationBusinessException {
		finalizarConsultaRN.verificaSeExisteCondutaSemComplementoNaoCadastrada(conduta, planoIniciais);
	}

	@Override
	public void validarProsseguirComGravarNascimento(DadosNascimentoVO nascimento, McoCesarianas cesarianas)throws ApplicationBusinessException {
		
		registrarGestacaoAbaNascimentoON.validarProsseguirComGravarNascimento(nascimento, cesarianas);
		
	}

	@Override
	public void validarFieldSetNascimento(DadosNascimentoSelecionadoVO dados)throws ApplicationBusinessException {
		registrarGestacaoAbaNascimentoON.validarFieldSetNascimento(dados);
		
	}

	@Override
	public void validarAgendamento(DadosNascimentoSelecionadoVO dadosNascimentoSelecionado)throws ApplicationBusinessException {
		registrarGestacaoAbaNascimentoON.validarAgendamento(dadosNascimentoSelecionado);
		
	}

	@Override
	public void validarAnestesia(Short tipoAnestesia)throws ApplicationBusinessException {
		registrarGestacaoAbaNascimentoON.validarAnestesia(tipoAnestesia);
		
	}

	@Override
	public void validarEquipe(EquipeVO equipe)throws ApplicationBusinessException {
		registrarGestacaoAbaNascimentoON.validarEquipe(equipe);
		
	}

//	@Override
//	public void validarDataProcedimento(
//			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado)
//			throws ApplicationBusinessException {
//		return;
//		
//	}

	@Override
	public void confirmarProcedimento(Integer pacCodigo, Short gsoSeqp,
			String contaminacao, Date dthrInicioProcedimento, Short seqp,
			Short tempoProcedimento, Short tanSeq, Short seq,
			String tipoParto) throws ServiceException, ServiceBusinessException {
		
		registrarGestacaoAbaNascimentoON.confirmarProcedimento(pacCodigo, gsoSeqp,
				contaminacao, dthrInicioProcedimento, seqp,
				tempoProcedimento, tanSeq, seq,
				tipoParto); 
	}	

	@Override
	public void atualizarAtendimentoGestante(Integer numeroConsulta,
			Integer pacCodigo, Short seqp, String nomeMicrocomputador,
			String obterLoginUsuarioLogado) throws ApplicationBusinessException {
		this.consultaCOON.atualizarAtendimentoGestante(numeroConsulta, pacCodigo, seqp, 
				nomeMicrocomputador, obterLoginUsuarioLogado);
	}
	
	public void validarDados(Boolean indAcompanhante
			, Integer pacCodigo
			, Short gsoSeqp
			, DadosGestacaoVO gestacao
			, EquipeVO equipeVO
			, DadosNascimentoVO nascimentoSelecionado)  throws ApplicationBusinessException, ServiceException{
		gestacaoNascimentoSumarioDefinitivoON.validarDados(indAcompanhante
				, pacCodigo
				, gsoSeqp
				, gestacao
				, equipeVO
				, nascimentoSelecionado);
	}
	
	public Boolean verificarChamarModalParaExamesVDRL(Integer numeroConsulta, String sigla) throws ApplicationBusinessException{
		return this.gestacaoNascimentoSumarioDefinitivoON.verificarChamarModalParaExamesVDRL(numeroConsulta, sigla);
	}
	
	public void atualizarSituacaoPaciente(Integer numeroConsulta, String login) throws ServiceException, ApplicationBusinessException{
		this.registrarGestacaoAbaNascimentoON.atualizarSituacaoPaciente(numeroConsulta, login);
	}

	@Override
	public Long pesquisarEspecialidadeListaSeqCount(List<Short> listaEspId,	String objPesquisa) {
		return this.marcarConsultaEmergenciaON.pesquisarEspecialidadeListaSeqCount(listaEspId, objPesquisa);
	}
	
	@Override
	public boolean habilitarBotaoNotasAdicionais(Integer gsoPacCodigo, Short gsoSeqp, Byte rnaSeqp, Integer conNumero) throws ApplicationBusinessException {
		return recemNascidoON.habilitarBotaoNotasAdicionais(gsoPacCodigo, gsoSeqp, rnaSeqp, conNumero);
	}

	@Override
	public void persistirMcoTabAdequacaoPeso(McoTabAdequacaoPeso tabAdequacaoPeso) throws ApplicationBusinessException{
		this.mcoTabAdequacaoPesoRN.pesistirMcoTabAdequacaoPeso(tabAdequacaoPeso);		
	}
	
	@Override
	public List<McoTabAdequacaoPeso> pesquisarAdequacaoPesoIgSemanas(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short igSemanas) {
		return mcoTabAdequacaoPesoDAO.pesquisarAdequacaoPesoIgSemanas(firstResult, maxResults, orderProperty, asc, igSemanas);
	}

	@Override
	public Long pesquisarCapacidadesAdequacaoPesoIgSemanasCount(Short igSemanas) {
		return mcoTabAdequacaoPesoDAO.pesquisarCapacidadesAdequacaoPesoIgSemanasCount(igSemanas);
	}
	@Override
	public McoTabAdequacaoPeso obterMcoTabAdequacaoPesoPorSeq(Short seq){
		return this.mcoTabAdequacaoPesoDAO.obterPorChavePrimaria(seq);
	}
	
	public void excluirAdequacaoPeso(Short seq){
		this.mcoTabAdequacaoPesoRN.excluirAdequacaoPeso(seq);
	}

	@Override
	public boolean existeAlteracaoSnappe(McoSnappes newSnappes) {
		return snappeRN.existeAlteracaoSnappe(newSnappes);
	}
	
	public void validarSeTemPermissaoRealizarEvolucaoAmbulatorio() throws ApplicationBusinessException {
		this.atenderPacienteEmergenciaON.validarSeTemPermissaoRealizarEvolucaoAmbulatorio();
	}
	
	public Long iniciaAtendimento(Long trgSeq, Integer conNumero, Integer atdSeq, Date dataHoraInicio, Short espSeq, Boolean isEmergenciaObstetrica, String hostName) throws ServiceException, NumberFormatException, BaseException {
		return this.atenderPacienteEmergenciaON.iniciaAtendimento(trgSeq, conNumero, atdSeq, dataHoraInicio, espSeq, isEmergenciaObstetrica, hostName);
	}
	
	public void atualizarSituacaoPacienteEmConsulta(Long trgSeq, String micNome) throws ServiceException, ApplicationBusinessException {
		this.atenderPacienteEmergenciaON.atualizarSituacaoPacienteEmConsulta(trgSeq,micNome);
	}
	
	public Long geraRegistroAtendimento(Long trgSeq, Integer atdSeq, String hostName) throws ServiceException, ApplicationBusinessException {
		return this.atenderPacienteEmergenciaON.geraRegistroAtendimento(trgSeq, atdSeq, hostName);
	}
	
	public List<DocumentosPacienteVO> obterListaReceitasAtestadosPaciente(Integer conNumero) throws ApplicationBusinessException {
		return this.pacientesEmergenciaEmAtendimentoON.obterListaReceitasAtestadosPaciente(conNumero);
	}

}