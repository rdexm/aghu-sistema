package br.gov.mec.aghu.ambulatorio.business;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.hibernate.exception.GenericJDBCException;
import org.hibernate.service.spi.ServiceException;

import br.gov.mec.aghu.ambulatorio.dao.AacAtendimentoApacsDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacCaracteristicaGradeDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacCondicaoAtendimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultaProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacEspecialidadePmpaDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaEspecialidadeDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeSituacaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacHorarioGradeConsultaDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacMotivosDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacNivelBuscaDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacPagadorDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacProcedHospEspecialidadesDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacRetornosDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacSisPrenatalDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacSituacaoConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacTipoAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacUnidFuncionalSalasDAO;
import br.gov.mec.aghu.ambulatorio.dao.FatLaudosPacApacsDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAlergiasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAltaDiagnosticosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAltaEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAltaPrescricoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAltasSumarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAreaAtuacaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAtestadosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamConcatenacaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamConsultorAmbulatorioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamDiagnosticoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEmgLocaisDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEstadoPacienteDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamFuncaoEdicaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamInterconsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemReceituarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLaudoAihDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLembreteDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoRealizadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRecCuidPreferidoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituarioCuidadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRegistroDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRelatorioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespostaAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespostaEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSituacaoAtendimentosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSolicProcedimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoAtestadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemEvolucaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpAlturasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPaDiastolicasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPaSistolicasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPerimCefalicosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPesosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgAlergiasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncAmbulatoriaisDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamValorValidoQuestaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.VAacConsTipoDAO;
import br.gov.mec.aghu.ambulatorio.dao.VAacConvenioPlanoDAO;
import br.gov.mec.aghu.ambulatorio.dao.VAacSiglaUnfSalaDAO;
import br.gov.mec.aghu.ambulatorio.dao.VMamDiferCuidServidoresDAO;
import br.gov.mec.aghu.ambulatorio.dao.VMamPessoaServidoresDAO;
import br.gov.mec.aghu.ambulatorio.dao.VMamProcXCidDAO;
import br.gov.mec.aghu.ambulatorio.dao.VMamReceitasDAO;
import br.gov.mec.aghu.ambulatorio.pesquisa.vo.ConsultasAgendaVO;
import br.gov.mec.aghu.ambulatorio.vo.AnamneseVO;
import br.gov.mec.aghu.ambulatorio.vo.ArquivosEsusVO;
import br.gov.mec.aghu.ambulatorio.vo.AtestadoVO;
import br.gov.mec.aghu.ambulatorio.vo.CabecalhoRelatorioAgendaConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaEspecialidadeAlteradaVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasDeOutrosConveniosVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasGradeVO;
import br.gov.mec.aghu.ambulatorio.vo.ConverterConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO;
import br.gov.mec.aghu.ambulatorio.vo.DisponibilidadeHorariosVO;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.ambulatorio.vo.EspecialidadeDisponivelVO;
import br.gov.mec.aghu.ambulatorio.vo.EspecialidadeRelacionadaVO;
import br.gov.mec.aghu.ambulatorio.vo.EvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.ExamesLiberadosVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroConsultaBloqueioConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroConsultaRetornoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroEspecialidadeDisponivelVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroGradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroParametrosPadraoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.GeraEvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.GerarDiariasProntuariosSamisVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendaVo;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioServiceVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeVO;
import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.ambulatorio.vo.MamConsultorAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.MamRecCuidPreferidoVO;
import br.gov.mec.aghu.ambulatorio.vo.MamRelatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.ParametrosAghEspecialidadesAtestadoVO;
import br.gov.mec.aghu.ambulatorio.vo.ParametrosAghPerfilProcessoVO;
import br.gov.mec.aghu.ambulatorio.vo.PesquisarConsultasPendentesVO;
import br.gov.mec.aghu.ambulatorio.vo.PreGeraItemQuestVO;
import br.gov.mec.aghu.ambulatorio.vo.ProcedHospEspecialidadeVO;
import br.gov.mec.aghu.ambulatorio.vo.ProcedimentoAtendimentoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.ProfissionalHospitalVO;
import br.gov.mec.aghu.ambulatorio.vo.ReceitasGeralEspecialVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAgendamentoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAnamneseEvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAtestadoVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioConsultasAgendaVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioConsultoriaAmbulatorialVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioControleFrequenciaVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioGuiaAtendimentoUnimedVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioProgramacaoGradeVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioSolicitacaoProcedimentoVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatoriosInterconsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.SolicitaInterconsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.TDataVO;
import br.gov.mec.aghu.ambulatorio.vo.TransferirExamesVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.VFatSsmInternacaoVO;
import br.gov.mec.aghu.casca.dao.CseCategoriaProfissionalDAO;
import br.gov.mec.aghu.casca.dao.CsePerfisUsuariosDAO;
import br.gov.mec.aghu.casca.dao.CseUsuarioDAO;
import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.configuracao.dao.AghEquipesDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioAnamneseEvolucao;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioConsultaGenerica;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioGrupoProfissionalAnamnese;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioIndPendenteLaudoAih;
import br.gov.mec.aghu.dominio.DominioMotivoPendencia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultasPesquisa;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoUnidadeFuncionalSala;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.StatusPacienteAgendado;
import br.gov.mec.aghu.emergencia.dao.MamEmgEspecialidadesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.faturamento.dao.FatCandidatosApacOtorrinoDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvGrupoItensProcedDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.faturamento.vo.CidVO;
import br.gov.mec.aghu.faturamento.vo.FatConsultaPrhVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedHospInternosVO;
import br.gov.mec.aghu.faturamento.vo.TipoProcedHospitalarInternoVO;
import br.gov.mec.aghu.faturamento.vo.TriagemRealizadaEmergenciaVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.paciente.dao.AipCidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipGrupoFamiliarDAO;
import br.gov.mec.aghu.paciente.dao.AipLogradourosDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolDiagnosticoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolEvolucaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolImpressaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolPrescricaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltasAmbulatoriasPolVO;
import br.gov.mec.aghu.paciente.vo.AipEnderecoPacienteVO;
import br.gov.mec.aghu.paciente.vo.PacienteGrupoFamiliarVO;
import br.gov.mec.aghu.paciente.vo.RelatorioAnaEvoInternacaoVO;
import br.gov.mec.aghu.paciente.vo.SolicitanteVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.CursorPacVO;
import br.gov.mec.aghu.prescricaomedica.vo.DiagnosticosPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemReceitaMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ReceitaMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptPrescricaoPacienteDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentoAmbulatorioVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.dao.VRapServidorConselhoDAO;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.view.VMamReceitas;
import br.gov.mec.aghu.vo.RapServidoresVO;

/**
 * Porta de entrada da camada de negócio do módulo de ambulatório.
 * @author gmneto
 */
@Modulo(ModuloEnum.AMBULATORIO)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.NcssTypeCount", "PMD.CouplingBetweenObjects" })
@Stateless
public class AmbulatorioFacade extends BaseFacade implements IAmbulatorioFacade {

	@EJB
	private EvolucaoRN evolucaoRN;
	
	@EJB
	private AnamneseRN anamneseRN;

	@EJB
	private EvolucaoON evolucaoON;
	
	@EJB
	private ItemExameON itemExameON;

	@EJB
	private AmbulatorioConsultaON ambulatorioConsultaON;

	@EJB
	private GerarDiariasProntuariosSamisON gerarDiariasProntuariosSamisON;

	@EJB
	private PesquisarPacientesAgendadosON pesquisarPacientesAgendadosON;

	@EJB
	private ConsultaAgendaON consultaAgendaON;

	@EJB
	private FinalizaAtendimentoON finalizaAtendimentoON;

	@EJB
	private CancelamentoAtendimentoRN cancelamentoAtendimentoRN;

	@EJB
	private BloqueioConsultaON bloqueioConsultaON;

	@EJB
	private AmbulatorioRN ambulatorioRN;

	@EJB
	private ListarDiagnosticosAtivosPacienteRN listarDiagnosticosAtivosPacienteRN;

	@EJB
	private ManterReceituarioON manterReceituarioON;
	
	@EJB
	private EsusConsultasON esusConsultasON;

	@EJB
	private ManterSumarioAltaReceitasON manterSumarioAltaReceitasON;

	@EJB
	private ArquivoSisregON arquivoSisregON;

	@EJB
	private DiagnosticoON diagnosticoON;

	@EJB
	private ListarDiagnosticosAtivosPacienteON listarDiagnosticosAtivosPacienteON;

	@EJB
	private ManterFormasParaAgendamentoON manterFormasParaAgendamentoON;

	@EJB
	private ManterGradeAgendamentoRN manterGradeAgendamentoRN;

	@EJB
	private MarcacaoConsultaON marcacaoConsultaON;

	@EJB
	private ConsultasON consultasON;

	@EJB
	private TrgEncInternoON trgEncInternoON;

	@EJB
	private DisponibilidadeHorariosON disponibilidadeHorariosON;

	@EJB
	private AgendaSamisON agendaSamisON;

	@EJB
	private AtualizarConsultaON atualizarConsultaON;

	@EJB
	private ManterItemReceituarioRN manterItemReceituarioRN;

	@EJB
	private AacConsultaProcedHospitalarON aacConsultaProcedHospitalarON;

	@EJB
	private ItemMedicacaoON itemMedicacaoON;

	@EJB
	private AtendimentoPacientesAgendadosRN atendimentoPacientesAgendadosRN;

	@EJB
	private AmbulatorioConsultaRN ambulatorioConsultaRN;
	
	@EJB
	private CuidadoPacienteRN cuidadoPacienteRN;

	@EJB
	private LiberarConsultasON liberarConsultasON;

	@EJB
	private MarcacaoConsultaSisregON marcacaoConsultaSisregON;

	@EJB
	private ManterCondicaoAtendimentoRN manterCondicaoAtendimentoRN;

	@EJB
	private GradeAgendamentoConsultasRN gradeAgendamentoConsultasRN;

	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;

	@EJB
	private ProcedimentoAtendimentoConsultaON procedimentoAtendimentoConsultaON;

	@EJB
	private ProcedimentoAtendimentoConsultaRN procedimentoAtendimentoConsultaRN;

	@EJB
	private ProcedimentoConsultaON procedimentoConsultaON;

	@EJB
	private HorarioConsultaON horarioConsultaON;

	@EJB
	private MamAnamnesesON mamAnamnesesON;

	@EJB
	private RelatorioAnaEvoInternacaoRN relatorioAnaEvoInternacaoRN;

	@EJB
	private ConsultasRN consultasRN;

	@EJB
	private ManterReceituarioRN manterReceituarioRN;

	@EJB
	private LaudoAihON laudoAihON;

	@EJB
	private ZonaSalaON zonaSalaON;

	@EJB
	private AtendimentoPacientesAgendadosON atendimentoPacientesAgendadosON;

	@EJB
	private ExportacaoLogSisregON exportacaoLogSisregON;

	@EJB
	private RelatorioAnaEvoEmergenciaON relatorioAnaEvoEmergenciaON;

	@EJB
	private PagadorON pagadorON;

	@EJB
	private ManterGradeAgendamentoON manterGradeAgendamentoON;

	@EJB
	private ManterPagadorRN manterPagadorRN;

	@EJB
	private ManterEspecialidadePmpaRN manterEspecialidadePmpaRN;

	@EJB
	private ManterMotivoConsultaRN manterMotivoConsultaRN;
	
	@EJB
	private AltaAmbulatorialON altaAmbulatorialON;
	
	@EJB
	private ManterTipoItensAnamneseRN manterTipoItensAnamneseRN;
	
	@EJB
	private MamTipoSolicitacaoProcedimentoRN mamTipoSolicitacaoProcedimentoRN;
	
	@EJB
	private AacConsultasRN aacConsultasRN;
	
	@EJB
	private PesquisarConsultasDeOutrosConveniosON pesquisarConsultasDeOutrosConveniosON;
	
    @EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private RelatorioAgendaConsultasON relatorioAgendaConsultasON;
	
	@EJB
	private MamConsultorAmbulatorioRN mamConsultorAmbulatorioRN;
	
	@Inject
	private VAacConsTipoDAO vAacConsTipoDAO;
	
	@Inject
	private MamLembreteDAO mamLembreteDAO;
	
//	@Inject
//	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
//	
//	@Inject
//	private AelExamesDAO aelExamesDAO;
	
	@EJB
	private AtendimentoPacientesAgendadosAuxiliarON atendimentoPacientesAgendadosAuxiliarON;
	
	@EJB
	private MamQuestionarioRN mamQuestionarioRN;
	
	@EJB
	private ReceitasRN receitasRN;
	
	@EJB
	private MamAnamneseRN mamAnamneseRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@Inject
	private AghAtendimentoDAO aghAtendimentosDAO;
	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;
	@Inject
	private FatLaudosPacApacsDAO fatLaudosPacApacsDAO;
	@Inject
	private MamAlergiasDAO mamAlergiasDAO;

	@Inject
	private AacMotivosDAO aacMotivosDAO;

	@Inject
	private VMamProcXCidDAO vMamProcXCidDAO;

	@Inject
	private MamAtestadosDAO mamAtestadosDAO;
	
	@Inject
	private MamRecCuidPreferidoDAO mamRecCuidPreferidoDAO;

	@Inject
	private MamTrgAlergiasDAO mamTrgAlergiasDAO;

	@Inject
	private AacSisPrenatalDAO aacSisPrenatalDAO;

	@Inject
	private MamAreaAtuacaoDAO mamAreaAtuacaoDAO;

	@Inject
	private AacCondicaoAtendimentoDAO aacCondicaoAtendimentoDAO;

	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;

	@Inject
	private MamTipoItemEvolucaoDAO mamTipoItemEvolucaoDAO;
	
	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;

	@Inject
	private AacRetornosDAO aacRetornosDAO;

	@Inject
	private AacSituacaoConsultasDAO aacSituacaoConsultasDAO;

	@Inject
	private AacNivelBuscaDAO aacNivelBuscaDAO;

	@Inject
	private AacFormaAgendamentoDAO aacFormaAgendamentoDAO;

	@EJB
	private RelatorioAnaEvoInternacaoBeanLocal relatorioAnaEvoInternacaoBean;

	@Inject
	private MamRegistroDAO mamRegistroDAO;

	@Inject
	private MamTrgEncAmbulatoriaisDAO mamTrgEncAmbulatoriaisDAO;

	@Inject
	private MamNotaAdicionalEvolucoesDAO mamNotaAdicionalEvolucoesDAO;

	@Inject
	private MamAltaDiagnosticosDAO mamAltaDiagnosticosDAO;

	@Inject
	private VAacSiglaUnfSalaDAO vAacSiglaUnfSalaDAO;
	
	@Inject
	private MamReceituarioCuidadoDAO mamReceituarioCuidadoDAO;

	@Inject
	private MamProcedimentoDAO mamProcedimentoDAO;

	@Inject
	private MamEmgLocaisDAO mamEmgLocaisDAO;

	@Inject
	private AacProcedHospEspecialidadesDAO aacProcedHospEspecialidadesDAO;

	@Inject
	private MamDiagnosticoDAO mamDiagnosticoDAO;

	@Inject
	private MamConcatenacaoDAO mamConcatenacaoDAO;

	@Inject
	private AacConsultaProcedHospitalarDAO aacConsultaProcedHospitalarDAO;

	@Inject
	private MamNotaAdicionalAnamnesesDAO mamNotaAdicionalAnamnesesDAO;

	@Inject
	private MamEmgEspecialidadesDAO mamEmgEspecialidadesDAO;

	@Inject
	private MamAltaEvolucoesDAO mamAltaEvolucoesDAO;

	@Inject
	private MamTmpPaDiastolicasDAO mamTmpPaDiastolicasDAO;

	@Inject
	private MamItemEvolucoesDAO mamItemEvolucoesDAO;

	@Inject
	private MamTmpAlturasDAO mamTmpAlturasDAO;

	@Inject
	private MamEstadoPacienteDAO mamEstadoPacienteDAO;

	@Inject
	private MamAltaPrescricoesDAO mamAltaPrescricoesDAO;

	@Inject
	private MamItemReceituarioDAO mamItemReceituarioDAO;

	@Inject
	private AacGradeProcedHospitalarDAO aacGradeProcedHospitalarDAO;

	@Inject
	private MamRespostaEvolucoesDAO mamRespostaEvolucoesDAO;

	@Inject
	private MamItemAnamnesesDAO mamItemAnamnesesDAO;
	
	@Inject
	private MamTipoItemAnamnesesDAO mamTipoItemAnamnesesDAO;

	@Inject
	private AacPagadorDAO aacPagadorDAO;

	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private FatConvGrupoItensProcedDAO fatConvGrupoItensProcedDAO;

	@Inject
	private VAacConvenioPlanoDAO vAacConvenioPlanoDAO;

	@Inject
	private AacFormaEspecialidadeDAO aacFormaEspecialidadeDAO;

	@Inject
	private MamProcedimentoRealizadoDAO mamProcedimentoRealizadoDAO;

	@Inject
	private MamTriagensDAO mamTriagensDAO;

	@Inject
	private AacConsultasJnDAO aacConsultasJnDAO;

	@Inject
	private MamTmpPerimCefalicosDAO mamTmpPerimCefalicosDAO;

	@Inject
	private AacAtendimentoApacsDAO aacAtendimentoApacsDAO;

	@Inject
	private AacCaracteristicaGradeDAO aacCaracteristicaGradeDAO;

	@Inject
	private MamTmpPaSistolicasDAO mamTmpPaSistolicasDAO;

	@Inject
	private MamReceituariosDAO mamReceituariosDAO;

	@Inject
	private MamTmpPesosDAO mamTmpPesosDAO;
	
	@Inject
	private VMamDiferCuidServidoresDAO vMamDiferCuidServidoresDAO;
	
	@Inject
	private VMamPessoaServidoresDAO vMamPessoaServidoresDAO;

	@Inject
	private MamControlesDAO mamControlesDAO;

	@Inject
	private AacTipoAgendamentoDAO aacTipoAgendamentoDAO;

	@Inject
	private MamSituacaoAtendimentosDAO mamSituacaoAtendimentosDAO;

	@Inject
	private AacHorarioGradeConsultaDAO aacHorarioGradeConsultaDAO;

	@Inject
	private MamAltasSumarioDAO mamAltasSumarioDAO;

	@Inject
	private MamRespostaAnamnesesDAO mamRespostaAnamnesesDAO;

	@Inject
	private AacUnidFuncionalSalasDAO aacUnidFuncionalSalasDAO;

	@Inject
	private MamInterconsultasDAO mamInterconsultasDAO;

	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;

	@Inject
	private AacGradeSituacaoDAO aacGradeSituacaoDAO;

	@Inject
	private MamLaudoAihDAO mamLaudoAihDAO;
	
	@Inject
	private AacEspecialidadePmpaDAO aacEspecialidadePmpaDAO;
	
	@EJB
	private PrescricaoAmbulatorialON prescricaoAmbulatorialON;

	@EJB
	private ManterTipoAutorizacaoRN manterTipoAutorizacaoRN;
	
	@EJB
	private RetornoConsultaRN retornoConsultaRN;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@EJB
	private ManterTipoItenEvolucaoRN manterTipoItenEvolucaoRN;
	
	@EJB
	private GestaoInterconsultasRN mamInterconsultasRN;

	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO; 
	
	@Inject
	private AghUnidadesFuncionaisDAO  aghUnidadesFuncionaisDAO;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@Inject
	private MamConsultorAmbulatorioDAO mamConsultorAmbulatorioDAO;

	@EJB
	private RelatorioControleFrequenciaRN relatorioControleFrequenciaRN;
	
	@Inject
	private MamSolicProcedimentoDAO mamSolicProcedimentoDAO;
	
	@Inject
	private MamRelatorioDAO mamRelatorioDAO;

	@Inject
	private AghEquipesDAO aghEquipesDAO;
	
	
	@Inject
	private CsePerfisUsuariosDAO csePerfisUsuariosDAO;
	
	@Inject
	private CseUsuarioDAO cseUsuarioDAO;
	
	@Inject
	private AipGrupoFamiliarDAO aipGrupoFamiliarDAO;
	
	@EJB
	private AgrupamentoFamiliarON agrupamentoFamiliarON;

	@EJB
	private TransferirExamesRN transferirExamesRN;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private MamTipoAtestadoDAO mamTipoAtestadoDAO;
	
	@EJB
	private MamAtestadosRN mamAtestadosRN;
		
	@EJB
	private ImprimirGuiaAtendimentoUnimedRN imprimirGuiaAtendimentoUnimedRN;
	
	@Inject
	private MptPrescricaoPacienteDAO mptPrescricaoPacienteDAO;
		
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@Inject
	private AghCidDAO aghCidDAO;
		
	@Inject
	private VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO;
		
	@Inject
	private VRapServidorConselhoDAO vRapServidorConselhoDAO;
	
	@Inject
	private FccCentroCustosDAO fccCentroCustosDAO;
	
	@Inject
	private MamTipoAtestadoDAO mamTipoAtestadosDAO;
	
	@Inject
	private MamFuncaoEdicaoDAO mamFuncaoEdicaoDAO;
	
	@Inject
	private AipEnderecosPacientesDAO aipEnderecosPacientesDAO;
	
	@Inject
	private AipLogradourosDAO aipLogradourosDAO;
	
	@Inject
	private AipCidadesDAO aipCidadesDAO;
	
	@Inject
	private FatCandidatosApacOtorrinoDAO fatCandidatosApacOtorrinoDAO;

    @Inject
    private VMamReceitasDAO vMamReceitasDAO;
    
    @EJB
    private MamRespostaEvolucoesRN mamRespostaEvolucoesRN;
    
    @EJB
    private EvolucaoNegarRN evolucaoNegarRN;
    
	@Inject
    private MamValorValidoQuestaoDAO mamValorValidoQuestaoDAO;
    
	@Inject
	private RelatorioProgramacaoGradeON relatorioProgramacaoGradeON;
	
	@Inject
	private CseCategoriaProfissionalDAO cseCategoriaProfissionalDAO;
		
	private static final long serialVersionUID = -6863585348339275639L;

	@Override
	public void copiarEscolhidos(Long evoSeq, List<ExamesLiberadosVO> examesSelecionados) {
		evolucaoRN.copiarEscolhidos(evoSeq, examesSelecionados);
	}
	
	@Override
	public List<ExamesLiberadosVO> montarTelaExamesLiberados(Integer pacCodigo, Integer numeroConsulta) throws ApplicationBusinessException {
		return evolucaoRN.montarTelaExamesLiberados(pacCodigo, numeroConsulta);
	}

	@Override
	public void atualizarIndPrcrImpressaoTratamentoFisiatrico(Integer atdSeq, Integer pteSeq) {
		ambulatorioRN.atualizarIndPrcrImpressaoTratamentoFisiatrico(atdSeq, pteSeq);
	}
	
	@Override
	public void atualizarIndImpressaoReceituarioCuidado(Long seq) {
		ambulatorioRN.atualizarIndImpressaoReceituarioCuidado(seq);
	}
	
	@Override
	public void atualizarIndImpressaoAtestado(Long seq) {
		ambulatorioRN.atualizarIndImpressaoAtestado(seq);
	}
	
	@Override
	public void movimentarProntuariosParaDesarquivamento(Date dataDiaria, String usuarioLogado,Boolean exibeMsgProntuarioJaMovimentado) throws ApplicationBusinessException {
		getGerarDiariasProntuariosSamisON().getConsultasDiariaParaMovimentar(dataDiaria, exibeMsgProntuarioJaMovimentado);
		
	}
	
	@Override
	public Boolean validarProcessoConsultaAtestado() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcessoConsultaAtestado();
	}

	@Override
	public void atualizarDataInicioAtendimento(Integer numeroConsulta, Date dataInicio, String nomeMicrocomputador) throws BaseException {
		getAtendimentoPacientesAgendadosRN().atualizarDataInicioAtendimento(numeroConsulta, dataInicio, nomeMicrocomputador, false);
	}

	// Precisa de bypass por causa da POL
	@Override
	@BypassInactiveModule
	public void atualizarNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes notaAdicionalEvolucoes,
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoesOld) throws BaseException {
		getMarcacaoConsultaRN().atualizarNotaAdicionalEvolucoes(notaAdicionalEvolucoes, notaAdicionalEvolucoesOld);
	}

	@Override
	public void persistirMamAtestado(MamAtestados atestado, Boolean imprimeAtestado) throws ApplicationBusinessException {
		this.mamAtestadosRN.persistir(atestado, imprimeAtestado);
	}

	@Override
	public void excluirMamAtestado(Long atsSeq) throws ApplicationBusinessException {
		this.mamAtestadosRN.excluir(atsSeq);
	}
	
	@Override
	public void validarPreAtualizarAtestado(MamAtestados atestado, MamAtestados atestadoOld) throws ApplicationBusinessException {
		this.mamAtestadosRN.validarPreAtualizarAtestado(atestado, atestadoOld);
	}

	@Override
	public void atualizarAlergias(MamAlergias alergia, MamAlergias alergiaOld) throws ApplicationBusinessException {
		this.getMarcacaoConsultaRN().atualizarAlergias(alergia, alergiaOld);
	}

	@Override
	public AacConsultas atualizarConsulta(AacConsultas consulta,
			AacConsultas consultaAnterior, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws NumberFormatException,
			BaseException {
		return getAmbulatorioConsultaRN().atualizarConsulta(consultaAnterior,
				consulta, false, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario,true);
	}
	

	@Override
	public AacConsultas refreshConsulta(AacConsultas consulta) {
		return getAacConsultasDAO().obterPorChavePrimaria(consulta.getNumero());
	}

	@Override
	@Deprecated
	public AacConsultas inserirConsulta(AacConsultas consulta, String nomeMicrocomputador, Boolean substituirProntuario)
			throws NumberFormatException, BaseException {
		return getAmbulatorioConsultaRN().inserirConsulta(consulta, false, nomeMicrocomputador, new Date(), substituirProntuario);
	}


	/**
	 * MÃ©todo responsÃ¡vel por realizar a persistÃªncia de um diagnóstico.
	 * 
	 * @param diagnostico
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	@BypassInactiveModule
	public MamDiagnostico persistirDiagnostico(MamDiagnostico diagnostico) throws ApplicationBusinessException {
		return this.getListarDiagnosticosAtivosPacienteRN().inserir(diagnostico);
	}

	/**
	 * 
	 * @param consulta
	 * @throws BaseException
	 */
	@Override
	public void cancelarAtendimento(AacConsultas consulta, String nomeMicrocomputador) throws BaseException, GenericJDBCException {
		this.getCancelamentoAtendimentoRN().cancelarAtendimento(consulta.getNumero(), nomeMicrocomputador);
	}
	
	@Override
	public void cancelarAtendimentoSituacao(Integer conNumero, String nomeMicrocomputador) throws BaseException,GenericJDBCException {
		this.getCancelamentoAtendimentoRN().cancelarAtendimentoSituacao(conNumero, nomeMicrocomputador);
	}
	@Override
	public boolean verificaExtratoPacAtendidoOuFechado(Integer conNumero) throws ApplicationBusinessException{
		return this.getCancelamentoAtendimentoRN().verificaExtratoPacAtendidoOuFechado(conNumero);
	}
	/**
	 * 
	 * @param consulta
	 * @throws BaseException
	 */
	@Override
	public void finalizarAtendimento(AacConsultas consulta, Boolean possuiProcedimentoRealizado, String nomeMicrocomputador)
			throws BaseException {
		this.getFinalizaAtendimentoON().finalizarAtendimento(consulta, possuiProcedimentoRealizado, nomeMicrocomputador);
	}
	@Override
    public void finalizarAtendimento(Integer consultaNumero,String nomeMicrocomputador) throws BaseException{
    	this.getFinalizaAtendimentoON().finalizarAtendimento(consultaNumero,nomeMicrocomputador);
    }
	@Override
	public Boolean concluirBlocoNaoAssina(AacConsultas consulta, String tipoCorrente) throws BaseException {
		return this.getFinalizaAtendimentoON().concluirBlocoNaoAssina(consulta, tipoCorrente);
	}
	
	/**
	 * 
	 * @param procedimento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void persistirProcedimento(AacGradeProcedHospitalar procedimento) throws ApplicationBusinessException {
		this.getManterGradeAgendamentoON().persistirProcedimento(procedimento);
	}

	@Override
	public void persistirCaracteristica(AacCaracteristicaGrade caracteristica) throws ApplicationBusinessException {
		this.getManterGradeAgendamentoON().persistirCaracteristica(caracteristica);
	}

	@Override
	public void persistirCaracteristicaJn(AacCaracteristicaGrade caracteristica) throws ApplicationBusinessException {
		this.getManterGradeAgendamentoRN().persistirCaracteristicaJn(caracteristica);

	}

	@Override
	public void validaRegrasAtendimento(AacConsultas consulta, Boolean supervisionar, Boolean atender, String nomeMicrocomputador)
			throws ApplicationBusinessException, ApplicationBusinessException {
		this.getAtendimentoPacientesAgendadosRN().validaRegrasAtendimento(consulta, supervisionar, atender, nomeMicrocomputador);
	}

	@Override
	public void atualizaControleAguardandoLivre(Integer numeroConsulta, Date dthrMovimento, String nomeMicrocomputador)
			throws ApplicationBusinessException, ApplicationBusinessException {
		this.getAtendimentoPacientesAgendadosON().atualizaControleAguardandoLivre(numeroConsulta, dthrMovimento, nomeMicrocomputador);
	}

	@Override
	public void atualizaControleAguardandoUso(Integer numeroConsulta, Date dthrMovimento) throws ApplicationBusinessException {
		this.getAtendimentoPacientesAgendadosON().atualizaControleAguardandoUso(numeroConsulta, dthrMovimento);
	}

	@Override
	public void persistirSituacao(AacGradeSituacao gradeSituacao) throws ApplicationBusinessException, ApplicationBusinessException {
		this.getManterGradeAgendamentoRN().inserirSituacao(gradeSituacao);
	}

	@Override
	public void removerProcedimento(AacGradeProcedHospitalar procedimento) throws ApplicationBusinessException {
		this.getGradeProcedHospitalarDAO().removerPorId(procedimento.getId());
	}

	@Override
	public void removerCaracteristica(AacCaracteristicaGrade caracteristica) throws ApplicationBusinessException {
		this.getCaracteristicaGradeDAO().removerPorId(caracteristica.getId());
	}

	@Override
	public void removerSituacao(AacGradeSituacao situacao) throws ApplicationBusinessException {
		this.getManterGradeAgendamentoON().eventoPreRemoverGradeSituacao(situacao);
	}

	/**
	 * Lista os diagnósticos Ativos e validados de acordo com paciente e cid.
	 * 
	 * @param paciente
	 * @param cid
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public List<MamDiagnostico> listarDiagnosticosPorPacienteCid(AipPacientes paciente, AghCid cid) {
		return getDiagnosticoON().listarDiagnosticosPorPacienteCid(paciente, cid);
	}

	@Override
	@BypassInactiveModule
	public List<MamDiagnostico> listarDiagnosticosPorCirurgia(Integer crgSeq) {
		return getDiagnosticoDAO().listarDiagnosticosPorCirurgia(crgSeq);
	}

	/**
	 * MÃ©todo que retorna os diagnósticos ativos relativos a um cidAtendimento.
	 * 
	 * @param cidAtendimento
	 * @return
	 */
	@Override
	public List<MamDiagnostico> listarDiagnosticosAtivosPorCidAtendimento(MpmCidAtendimento cidAtendimento) {
		return getDiagnosticoON().listarDiagnosticosAtivosPorCidAtendimento(cidAtendimento);
	}

	/**
	 * Retorna os diagnósticos de um atendimento.
	 * 
	 * @param atendimento
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public List<MamDiagnostico> listardiagnosticosPorAtendimento(AghAtendimentos atendimento) {
		return getDiagnosticoON().listardiagnosticosPorAtendimento(atendimento);
	}

	/**
	 * @return ORADB: Procedure MAMP_CHECK_OUT_INT Adicionado
	 * @BypassInactiveModule, pois chama funÃ§Ã£o do Oracle
	 */
	@Override
	@BypassInactiveModule
	public void gerarCheckOut(Integer seq, Integer pacCodigo, String tipoAltaMedicaCodigoOld, String tipoAltaMedicaCodigo, Short unfSeqOld,
			Short unfSeq, Boolean pacienteInternadoOld, Boolean pacienteInternacao) throws ApplicationBusinessException {
		getAmbulatorioRN().gerarCheckOut(seq, pacCodigo, tipoAltaMedicaCodigoOld, tipoAltaMedicaCodigo, unfSeqOld, unfSeq,
				pacienteInternadoOld, pacienteInternacao);
	}

	/**
	 * ORADB: Procedure MAMP_ATU_TRG_EMATEND Adicionado @BypassInactiveModule,
	 * pois chama funÃ§Ã£o do Oracle
	 */
	@Override
	@BypassInactiveModule
	public void atualizarSituacaoTriagem(Integer pacCodigo) throws ApplicationBusinessException {
		getAmbulatorioRN().atualizarSituacaoTriagem(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public void validarSelecaoImpressaoConsultaAmbulatorio(Integer numero, Boolean selecionar) throws BaseException {
		getAmbulatorioRN().validarSelecaoImpressaoConsultaAmbulatorio(numero, selecionar);
	}

	@Override
	public List<AacGradeProcedHospitalar> listarProcedimentosGrade(Integer grdSeq) {
		return this.getGradeProcedHospitalarDAO().listarProcedimentosGrade(grdSeq);
	}

	@Override
	public List<AacCaracteristicaGrade> listarCaracteristicasGrade(Integer grdSeq) {
		return this.getCaracteristicaGradeDAO().listarCaracteristicaGrade(grdSeq);
	}

	@Override
	public List<AacGradeSituacao> listarSituacoesGrade(Integer grdSeq) {
		return this.getGradeSituacaoDAO().listarSituacaoGrade(grdSeq);
	}

	/**
	 * 
	 * @param especialidade
	 * @return
	 */
	@Override
	public List<AacProcedHospEspecialidades> listarProcedimentosEspecialidades(Object parametro, AghEspecialidades especialidade) {
		return this.getAacProcedHospEspecialidadesDAO().listarProcedimentosEspecialidades(parametro, especialidade);
	}

	/**
	 * 
	 * @param parametro
	 * @param especialidade
	 * @return
	 */
	@Override
	public Long listarProcedimentosEspecialidadesCount(Object parametro, AghEspecialidades especialidade) {
		return this.getAacProcedHospEspecialidadesDAO().listarProcedimentosEspecialidadesCount(parametro, especialidade);
	}

	/**
	 * Lista os procedimento das especialidades, incluindo procedimentos de
	 * especialidades genÃ©ricas
	 * 
	 * @param parametro
	 * @param especialidade
	 * @return
	 */
	@Override
	public List<ProcedHospEspecialidadeVO> listarProcedimentosEspecialidadesComGenericas(Object parametro, AghEspecialidades especialidade) {
		return this.getAacProcedHospEspecialidadesDAO().listarProcedimentosEspecialidadesComGenericas(parametro, especialidade);
	}

	/**
	 * Count para listar procedimento das especialidades, incluindo
	 * procedimentos de especialidades genÃ©ricas
	 * 
	 * @param parametro
	 * @param especialidade
	 * @return
	 */
	@Override
	public Integer listarProcedimentosEspecialidadesComGenericasCount(Object parametro, AghEspecialidades especialidade) {
		return this.getAacProcedHospEspecialidadesDAO().listarProcedimentosEspecialidadesComGenericasCount(parametro, especialidade);
	}

	/**
	 * 
	 */
	@Override
	public List<String> listarCaracteristicas() {
		return this.getManterGradeAgendamentoON().listarCaracteristicas();
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public AacGradeAgendamenConsultas salvarGradeAgendamentoConsulta(AacGradeAgendamenConsultas entity, AacGradeAgendamenConsultas oldEntity)
			throws ApplicationBusinessException {
		return getManterGradeAgendamentoON().salvar(entity, oldEntity);
	}

	@Override
	public void validaHorarioSobreposto(AacHorarioGradeConsulta entity, AacGradeAgendamenConsultas grade)
			throws ApplicationBusinessException {
		getManterGradeAgendamentoON().verificaDataHoraIgualAC(entity, grade);
	}

	@Override
	public void salvarHorarioGradeConsulta(AacHorarioGradeConsulta entity,
			AacGradeAgendamenConsultas entityPai)
			throws ApplicationBusinessException {
		this.gradeAgendamentoConsultasRN.salvarHorarioGradeConsulta(entity,
				entityPai);
		}

	@Override
	public List<AacHorarioGradeConsulta> pesquisarHorariosPorGrade(AacGradeAgendamenConsultas grade) {
		return getAacHorarioGradeConsultaDAO().pesquisaPorGrade(grade);
	}

	@Override
	public void excluirHorarioGradeConsulta(AacHorarioGradeConsulta entity) throws ApplicationBusinessException,
			ApplicationBusinessException {
		entity = getAacHorarioGradeConsultaDAO().merge(entity);
		getAacHorarioGradeConsultaDAO().remover(entity);
		getAacHorarioGradeConsultaDAO().flush();
		getManterGradeAgendamentoRN().posDeleteAacHorarioGradeConsultaJournal(entity);
	}

	@Override
	public List<GradeAgendamentoVO> listarAgendamentoConsultas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroSeq, Short filtroUslUnfSeq, Boolean filtroProcedimento, Boolean filtroEnviaSamis, DominioSituacao filtroSituacao,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, RapServidores profissional, AelProjetoPesquisas filtroProjeto,
			Date filtroDtEm, Date filtroDtInicio, Date filtroDtFim, Date filtroDtInicioUltGeracao, Date filtroDtFimUltGeracao) {

		return getGradeAgendamenConsultasDAO().listarAgendamentoConsultas(firstResult, maxResult, orderProperty, asc, filtroSeq,
				filtroUslUnfSeq, filtroProcedimento, filtroEnviaSamis, filtroSituacao, filtroEspecialidade, filtroEquipe, profissional,
				filtroProjeto, filtroDtEm, filtroDtInicio, filtroDtFim, filtroDtInicioUltGeracao, filtroDtFimUltGeracao);
	}

	@Override
	public List<DisponibilidadeHorariosVO> listarDisponibilidadeHorarios(Integer filtroSeq,
			Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, RapServidores filtroProfissional,
			AacPagador pagador, AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicaoAtendimento, Date dtConsulta,
			Date horaConsulta, Date mesInicio, Date mesFim, DominioDiaSemana dia, Boolean disponibilidade,
			VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala zonaSala,	DataInicioFimVO turno, List<AghEspecialidades> listEspecialidade, Boolean visualizarPrimeirasConsultasSMS)
			throws ApplicationBusinessException {
		return getDisponibilidadeHorariosON().listarDisponibilidadeHorarios(filtroSeq, filtroUslUnfSeq, filtroEspecialidade, filtroEquipe,
				filtroProfissional, pagador, tipoAgendamento, condicaoAtendimento, dtConsulta, horaConsulta, mesInicio, mesFim, dia,
				disponibilidade, zona, zonaSala, turno, listEspecialidade, visualizarPrimeirasConsultasSMS);
	}


	@Override
	public List<DisponibilidadeHorariosVO> listarDisponibilidadeHorariosEmergencia(String orderProperty, boolean asc)
			throws ApplicationBusinessException {
		return getDisponibilidadeHorariosON().listarDisponibilidadeHorariosEmergencia(orderProperty, asc);
	}

	@Override
	public Long listarAgendamentoConsultasCount(Integer filtroSeq, Short filtroUslUnfSeq, Boolean filtroProcedimento,
			Boolean filtroEnviaSamis, DominioSituacao filtroSituacao, AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe,
			RapServidores profissional, AelProjetoPesquisas filtroProjeto, Date filtroDtEm, Date filtroDtInicio, Date filtroDtFim,
			Date filtroDtInicioUltGeracao, Date filtroDtFimUltGeracao) {

		return getGradeAgendamenConsultasDAO().listarAgendamentoConsultasCount(filtroSeq, filtroUslUnfSeq, filtroProcedimento,
				filtroEnviaSamis, filtroSituacao, filtroEspecialidade, filtroEquipe, profissional, filtroProjeto, filtroDtEm,
				filtroDtInicio, filtroDtFim, filtroDtInicioUltGeracao, filtroDtFimUltGeracao);
	}

	@Override
	public Long listarDisponibilidadeHorariosCount(Integer filtroSeq, Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade,
			AghEquipes filtroEquipe, RapServidores filtroProfissional, AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento, Date dtConsulta, Date horaConsulta, Date mesInicio, Date mesFim,
			DominioDiaSemana dia, Boolean disponibilidade, VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala zonaSala,	DataInicioFimVO turno,
			List<AghEspecialidades> listEspecialidade, Boolean visualizarPrimeirasConsultasSMS) {
		try {
			return getDisponibilidadeHorariosON().listarDisponibilidadeHorariosCount(filtroSeq, filtroUslUnfSeq, filtroEspecialidade,
					filtroEquipe, filtroProfissional, pagador, tipoAgendamento, condicaoAtendimento, dtConsulta, horaConsulta, mesInicio,
					mesFim, dia, disponibilidade, zona, zonaSala, turno, listEspecialidade, visualizarPrimeirasConsultasSMS);
		} catch (ApplicationBusinessException e) {
			return 0L;
		}
	}

	@Override
	public Integer listarDisponibilidadeHorariosCount(Integer filtroSeq, Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade,
			AghEquipes filtroEquipe, RapServidores filtroProfissional, AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento, Date dtConsulta, Date horaConsulta, Date mesInicio, Date mesFim,
			DominioDiaSemana dia, Boolean disponibilidade, Boolean visualizarPrimeirasConsultasSMS) {
		try {
			return getDisponibilidadeHorariosON().listarDisponibilidadeHorarios(filtroSeq, filtroUslUnfSeq, filtroEspecialidade,
					filtroEquipe, filtroProfissional, pagador, tipoAgendamento, condicaoAtendimento, dtConsulta, horaConsulta, mesInicio,
					mesFim, dia, disponibilidade, visualizarPrimeirasConsultasSMS).size();
		} catch (ApplicationBusinessException e) {
			return 0;
		}
	}

	@Override
	public Long listarDisponibilidadeHorariosEmergenciaCount() {
		try {
			return Long.valueOf(getDisponibilidadeHorariosON().listarDisponibilidadeHorariosEmergencia("", false).size());
		} catch (ApplicationBusinessException e) {
			return 0L;
		}
	}

	@Override
	public List<AacConsultas> listarConsultasPaciente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral, Integer filtroCodConsultaAnterior,
			Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio, Date filtroDtFim) {

		return getAacConsultasDAO().listarConsultasPaciente(firstResult, maxResult, orderProperty, asc, filtroPacCodigo,
				filtroNumeroConsulta, filtroCodCentral, filtroCodConsultaAnterior, filtroCondAtendSeq, filtroGradeSeq, filtroEspSeq,
				filtroDtInicio, filtroDtFim);
	}

	@Override
	public List<AacConsultas> listarConsultasPaciente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral, Integer filtroCodConsultaAnterior,
			Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio, Date filtroDtFim,
			Boolean utilizarDataFim) {

		return getAacConsultasDAO().listarConsultasPaciente(firstResult, maxResult, orderProperty, asc, filtroPacCodigo,
				filtroNumeroConsulta, filtroCodCentral, filtroCodConsultaAnterior, filtroCondAtendSeq, filtroGradeSeq, filtroEspSeq,
				filtroDtInicio, filtroDtFim, utilizarDataFim);
	}

	@Override
	public Long listarConsultasPacienteCount(Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral,
			Integer filtroCodConsultaAnterior, Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio,
			Date filtroDtFim) {

		return getAacConsultasDAO().listarConsultasPacienteCount(filtroPacCodigo, filtroNumeroConsulta, filtroCodCentral,
				filtroCodConsultaAnterior, filtroCondAtendSeq, filtroGradeSeq, filtroEspSeq, filtroDtInicio, filtroDtFim);
	}

	@Override
	public AacCaracteristicaGrade obterCaracteristicaGradePorChavePrimaria(AacCaracteristicaGradeId chave) {
		return this.getCaracteristicaGradeDAO().obterPorChavePrimaria(chave);

	}

	@Override
	public List<VAacSiglaUnfSalaVO> pesquisarZonas(String objPesquisa) {
		return getVAacSiglaUnfSalaDAO().pesquisarZonas(objPesquisa);
	}

	@Override
	public List<VAacSiglaUnfSalaVO> pesquisarTodasZonas(String objPesquisa) {
		return getVAacSiglaUnfSalaDAO().pesquisarTodasZonas(objPesquisa);
	}

	@Override
	public AacGradeAgendamenConsultas obterGrade(Integer seq) {
		return this.getGradeAgendamenConsultasDAO().obterGradeAgendamento(seq);
	}

	@Override
	public AacGradeAgendamenConsultas obterAacGradeAgendamenConsultas(Integer seq, Enum[] innerJoins, Enum[] leftJoins) {
		return this.getGradeAgendamenConsultasDAO().obterPorChavePrimaria(seq, innerJoins, leftJoins);
	}

	@Override
	public AacRetornos obterRetorno(Integer seq) {
		return this.getAacRetornosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public AacUnidFuncionalSalas obterUnidadeFuncional(Short unfSeq, Byte sala) {
		return getAacUnidFuncionalSalasDAO().obterUnidFuncionalSalasPeloId(unfSeq, sala);
	}

	@Override
	public boolean verificarGradeConsulta(Integer seq) {
		return getAacConsultasDAO().verificarGradeAgendamento(seq);
	}

	@Override
	public void removerGradeConsulta(Integer seqGrade) throws ApplicationBusinessException {
		getManterGradeAgendamentoRN().removerGradeConsulta(seqGrade);
	}

	/**
	 * MÃ©todo que retorna a lista de condiÃ§Ãµes de atendimento ativas.
	 * 
	 * @return
	 */
	@Override
	public List<AacCondicaoAtendimento> listarCondicaoAtendimento() {
		return getAacCondicaoAtendimentoDAO().listarCondicaoAtendimento();
	}

	/**
	 * MÃ©todo que retorna as situaÃ§Ãµes de consulta ativas pela
	 * descriÃ§Ã£o/situaÃ§Ã£o.
	 * 
	 * @param objPesquisa
	 * @return List de AacSituacaoConsultas
	 */
	@Override
	public List<AacSituacaoConsultas> pesquisarSituacao(String objPesquisa) {
		return getHorarioConsultaON().pesquisarSituacoesConsultaAtivas(objPesquisa);
	}

	/**
	 * MÃ©todo que retorna as situaÃ§Ãµes de consulta ativas.
	 * 
	 * @return
	 */
	@Override
	public List<AacSituacaoConsultas> obterSituacoesAtivas() {
		return getAacSituacaoConsultasDAO().obterSituacoesAtivasSemOrder();
	}

	/**
	 * MÃ©todo que retorna as situaÃ§Ãµes de consulta ativas (exceto as Marcadas).
	 * 
	 * @param objPesquisa
	 * @return List de AacSituacaoConsultas
	 */
	@Override
	public List<AacSituacaoConsultas> pesquisarSituacaoSemMarcada(String objPesquisa) {
		return getHorarioConsultaON().pesquisarSituacoesConsultasSemMarcadasAtivas(objPesquisa);
	}

	/**
	 * MÃ©todo que retorna a situaÃ§Ã£o da consulta pelo Id.
	 * 
	 * @param objPesquisa
	 * @return AacSituacaoConsultas
	 */
	public AacSituacaoConsultas pesquisarSituacaoConsultaPeloId(String objPesquisa) {
		return getAacSituacaoConsultasDAO().obterSituacaoConsultaPeloId(objPesquisa);
	}

	/**
	 * MÃ©todo que retorna a lista de pagadores com agendamento.
	 * 
	 * @return
	 */
	@Override
	public List<AacPagador> obterListaPagadoresComAgendamento() {
		return getAacFormaAgendamentoDAO().pesquisaPagadoresComAgendamento();
	}

	/**
	 * MÃ©todo que retorna a lista de autorizaÃ§Ãµes ativas.
	 * 
	 * @return
	 */
	@Override
	public List<AacTipoAgendamento> obterListaAutorizacoesAtivas() {
		return getAacTipoAgendamentoDAO().obterListaAutorizacoesAtivas();
	}

	@Override
	public int gerarDisponibilidade(AacGradeAgendamenConsultas entity, Date dataInicial, Date dataFinal, String nomeMicrocomputador)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NumberFormatException,
			BaseException {
		return getManterGradeAgendamentoON().gerarDisponibilidade(entity, dataInicial, dataFinal, nomeMicrocomputador);
	}

	@Override
	public void alterarDisponibilidadeConsultas(List<Integer> consultas, AacSituacaoConsultas novaSituacao, String nomeMicrocomputador)
			throws BaseException {

		getHorarioConsultaON().atualizarDisponibilidadeConsultas(consultas, novaSituacao, nomeMicrocomputador);
	}

	@Override
	public void manterConsulta(AacConsultas consultaAnterior, AacConsultas consulta, final AacFormaAgendamentoId idFormaAgendamento,
			final boolean emergencia, final String nomeMicrocomputador, boolean cameFromInterconsultas) throws BaseException {
		getMarcacaoConsultaON().manterConsulta(consultaAnterior, consulta, idFormaAgendamento, emergencia, nomeMicrocomputador, cameFromInterconsultas);
	}

	public List<String> validarItensPreManterConsulta(final AacConsultas consulta) throws ApplicationBusinessException {
		return getMarcacaoConsultaON().validarItensPreManterConsulta(consulta);
	}

	@Override
	public void verificarEnderecoPaciente(Integer codPac) throws ApplicationBusinessException {
		getMarcacaoConsultaON().verificarEnderecoPaciente(codPac);
	}

	@Override
	public FatConvenioSaudePlano popularPagador(Integer numeroConsulta) throws ApplicationBusinessException {
		return getMarcacaoConsultaON().popularPagador(numeroConsulta);
	}
	
	@Override
	public void verificarConsultaExcedenteDiaBloqueado(AacConsultas consulta) throws ApplicationBusinessException{
		getMarcacaoConsultaON().verificarConsultaExcedenteDiaBloqueado(consulta);
	}
	
	@Override
	public Boolean verificarConsultaDiaNaoProgramado(AacConsultas consulta){
		return getMarcacaoConsultaON().verificarConsultaDiaNaoProgramado(consulta);
	}
	
	public String obterCabecalhoListaConsultasPorGrade(AacGradeAgendamenConsultas grade){
		return getMarcacaoConsultaON().obterCabecalhoListaConsultasPorGrade(grade);
	}

	@Override
	public VAacConvenioPlano obterVAacConvenioPlanoAtivoPorId(Short cspCnvCodigo, Byte cspSeq) {
		return getVAacConvenioPlanoDAO().obterVAacConvenioPlanoAtivoPorId(cspCnvCodigo, cspSeq);
	}

	@Override
	public Boolean verificarCaracEspecialidade(AghEspecialidades especialidade, DominioCaracEspecialidade caracteristica) {
		return getMarcacaoConsultaON().verificarCaracEspecialidade(especialidade, caracteristica);
	}

	@Override
	public List<AacConsultas> obterConsultasNaoRealizadasPorGrade(AacGradeAgendamenConsultas grade, Short pgdSeq, Short tagSeq,
			Short caaSeq, Boolean emergencia, Date dtConsulta, Date mesInicio, Date mesFim, Date horaConsulta,
			DominioDiaSemana diaSemana, boolean excluirPrimeiraConsulta, boolean visualizarPrimeirasConsultasSMS)
			throws ApplicationBusinessException {
		return getMarcacaoConsultaON().listarConsultasNaoRealizadasPorGrade(grade, pgdSeq, tagSeq, caaSeq, emergencia, dtConsulta,
				mesInicio, mesFim, horaConsulta, diaSemana, excluirPrimeiraConsulta, visualizarPrimeirasConsultasSMS);
	}

	@Override
	public List<AacConsultas> listarHorariosConsultas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta, DominioDiaSemana filtroDia, Date filtroHoraConsulta,
			AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao, AacCondicaoAtendimento filtroCondicao, Date filtroDtInicio,
			Date filtroDtFim, Integer grdSeq) {

		return getAacConsultasDAO().listarHorariosConsultas(firstResult, maxResult, orderProperty, asc, filtroSituacao, filtroNroConsulta,
				filtroDia, filtroHoraConsulta, filtroPagador, filtroAutorizacao, filtroCondicao, filtroDtInicio, filtroDtFim, grdSeq);
	}

	@Override
	public List<Integer> listarHorariosConsultasSeqs(AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta,
			DominioDiaSemana filtroDia, Date filtroHoraConsulta, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, Date filtroDtInicio, Date filtroDtFim, Integer grdSeq) {
		return getAacConsultasDAO().listarHorariosConsultasSeqs(filtroSituacao, filtroNroConsulta, filtroDia, filtroHoraConsulta,
				filtroPagador, filtroAutorizacao, filtroCondicao, filtroDtInicio, filtroDtFim, grdSeq);
	}

	@Override
	public Long listarHorariosConsultasCount(AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta, DominioDiaSemana filtroDia,
			Date filtroHoraConsulta, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao, AacCondicaoAtendimento filtroCondicao,
			Date filtroDtInicio, Date filtroDtFim, Integer grdSeq) {

		return getAacConsultasDAO().listarHorariosConsultasCount(filtroSituacao, filtroNroConsulta, filtroDia, filtroHoraConsulta,
				filtroPagador, filtroAutorizacao, filtroCondicao, filtroDtInicio, filtroDtFim, grdSeq);
	}

	@Override
	public List<AacConsultas> listarHorariosConsultasNaoMarcadas(AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta,
			DominioDiaSemana filtroDia, Date filtroHoraConsulta, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, Date filtroDtInicio, Date filtroDtFim, Integer grdSeq) {

		return getAacConsultasDAO().listarHorariosConsultasNaoMarcadas(filtroSituacao, filtroNroConsulta, filtroDia, filtroHoraConsulta,
				filtroPagador, filtroAutorizacao, filtroCondicao, filtroDtInicio, filtroDtFim, grdSeq);
	}

	@Override
	public AacConsultas obterConsultasMarcada(Integer nroConsulta, Boolean relacionaFormaAgendamento) {
		return getAacConsultasDAO().obterConsultasMarcada(nroConsulta, relacionaFormaAgendamento);
	}

	@Override
	public List<AacFormaAgendamento> pesquisaFormaAgendamentoPorStringEFormaAgendamento(String parametro, AacPagador pagador,
			AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicaoAtendimento) {
		return getAacFormaAgendamentoDAO().pesquisaFormaAgendamentoPorStringEFormaAgendamento(parametro, pagador, tipoAgendamento,
				condicaoAtendimento);
	}

	@Override
	public Long pesquisaFormaAgendamentoPorStringEFormaAgendamentoCount(String parametro, AacPagador pagador,
			AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicaoAtendimento) {
		return getAacFormaAgendamentoDAO().pesquisaFormaAgendamentoPorStringEFormaAgendamentoCount(parametro, pagador, tipoAgendamento,
				condicaoAtendimento);
	}

	@Override
	public List<AacConsultas> listarConsultasAgenda(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana,
			Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetorno,
			FatProcedHospInternosVO filtroProcedimento) {

		return getAacConsultasDAO().listarConsultasAgenda(filtroServico, filtroGrdSeq, filtroUslUnfSeq, filtroEspecialidade, filtroEquipe,
				filtroPagador, filtroAutorizacao, filtroCondicao, filtroSituacao, filtroDiaSemana, filtroHoraConsulta, filtroDtInicio,
				filtroDtFim, filtroProfissional, filtroRetorno, null, null, filtroProcedimento, false);
	}

	@Override
	public List<ConsultasAgendaVO> listarConsultasAgendaScrooler(FccCentroCustos filtroServico, Integer filtroGrdSeq,
			Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador,
			AacTipoAgendamento filtroAutorizacao, AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao,
			DominioDiaSemana filtroDiaSemana, Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim,
			RapServidores filtroProfissional, AacRetornos filtroRetorno, Integer firstResult, Integer maxResult, FatProcedHospInternosVO procedimento) {

		return getAacConsultasDAO().listarConsultasAgendaScrooler(filtroServico, filtroGrdSeq, filtroUslUnfSeq, filtroEspecialidade,
				filtroEquipe, filtroPagador, filtroAutorizacao, filtroCondicao, filtroSituacao, filtroDiaSemana, filtroHoraConsulta,
				filtroDtInicio, filtroDtFim, filtroProfissional, filtroRetorno, firstResult, maxResult, procedimento);
	}

	@Override
	public void validarPesquisaDisponibilidadeHorarios(Integer seq, AghEspecialidades especialidade, Date horaConsulta, Date dtConsulta,
			Date mesInicio, Date mesFim, DominioDiaSemana dia, VAacSiglaUnfSalaVO zona, DominioTurno turno) throws ApplicationBusinessException {
		this.getDisponibilidadeHorariosON().validarPesquisaDisponibilidadeHorarios(seq, especialidade, horaConsulta, dtConsulta, mesInicio,
				mesFim, dia, zona, turno);
	}

	@Override
	public Long listarConsultasAgendaCount(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana,
			Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetorno, FatProcedHospInternosVO filtroProcedimento) {

		return getAacConsultasDAO().listarConsultasAgendaCount(filtroServico, filtroGrdSeq, filtroUslUnfSeq, filtroEspecialidade,
				filtroEquipe, filtroPagador, filtroAutorizacao, filtroCondicao, filtroSituacao, filtroDiaSemana, filtroHoraConsulta,
				filtroDtInicio, filtroDtFim, filtroProfissional, filtroRetorno, filtroProcedimento);
	}

	@Override
	public List<AacNivelBusca> pesquisarNivelBuscaPorFormaAgendamento(AacFormaAgendamento formaAgendamento) {
		return this.getAacNivelBuscaDAO().pesquisarNivelBuscaPorFormaAgendamento(formaAgendamento);
	}

	@Override
	public List<AacFormaEspecialidade> listaFormasEspecialidadePorFormaAgendaneto(AacFormaAgendamento formaAgendamento) {
		return this.getAacFormaEspecialidadeDAO().listaFormasEspecialidadePorFormaAgendaneto(formaAgendamento);
	}

	@Override
	public List<ConsultaAmbulatorioVO> consultaPacientesAgendados(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,   
			Date dtPesquisa, Short zonaUnfSeq, List<Byte> zonaSalas,
			VAacSiglaUnfSala zonaSala, DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade,
			RapServidores profissional, StatusPacienteAgendado status) throws ApplicationBusinessException {

		return this.getConsultasON().consultaPacientesAgendados(firstResult, maxResult, orderProperty, asc, 
				dtPesquisa, zonaUnfSeq, zonaSalas, zonaSala, turno, equipe, espCrmVO,
				especialidade, profissional, status);
	}
	
	
	@Override
	@BypassInactiveModule			
	public Long consultaPacientesAgendadosCount(Date dtPesquisa, Short zonaUnfSeq, List<Byte> zonaSalas,
			VAacSiglaUnfSala zonaSala, DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade,
			RapServidores profissional, StatusPacienteAgendado status) throws ApplicationBusinessException{
		return this.getConsultasON().consultaPacientesAgendadosCount(dtPesquisa, zonaUnfSeq, zonaSalas, zonaSala, turno, equipe, espCrmVO,
				especialidade, profissional, status);
	}
	

	@Override
	public AipPacientes obterPacienteConsulta(AacConsultas consulta) {
		return getMarcacaoConsultaON().obterPacienteConsulta(consulta);
	}

	@Override
	public AacConsultas obterConsultaPorNumero(Integer numero) {
		return this.getAacConsultasDAO().obterConsulta(numero);
	}

	@Override
	public AacConsultas obterConsultaPorNumero(Integer numero, Enum[] innerJoin, Enum[] leftJoin) {
		return this.getAacConsultasDAO().obterPorChavePrimaria(numero, innerJoin, leftJoin);
	}

	@Override
	public AacConsultas obterAacConsultasAtenderPacientesAgendados(Integer numero) {
		return this.getAacConsultasDAO().obterAacConsultasAtenderPacientesAgendados(numero);
	};

	@Override
	public List<AacConsultasJn> obterHistoricoConsultasPorNumero(Integer numero) {
		return getAacConsultasJnDAO().obterHistoricoConsultasPorNumero(numero);
	}

	@Override
	public AacMotivos obterDescricaoMotivoPorCodigo(Short mtoSeq) {
		return getAacMotivosDAO().obterDescricaoMotivoPorCodigo(mtoSeq);
	}

	@Override
	public AacRetornos obterDescricaoRetornoPorCodigo(Integer retSeq) {
		return getAacRetornosDAO().obterDescricaoRetornoPorCodigo(retSeq);
	}

	@Override
	public boolean excluirListaConsultas(List<Integer> consultas, Integer grdSeq, String nomeMicrocomputador, boolean substituirProntuario)
			throws BaseException {
		return getAmbulatorioConsultaRN().excluirListaConsultas(consultas, grdSeq, nomeMicrocomputador, new Date(), substituirProntuario);
	}

	@Override
	public void excluirConsultasAghAtendimento(Integer seqAtendimento, String nomeMicrocomputador, Boolean substituirProntuario)
			throws BaseException {
		getAmbulatorioConsultaRN().excluirConsultasAghAtendimento(seqAtendimento, nomeMicrocomputador, new Date(), substituirProntuario);
	}

	@Override
	public List<String> validaDadosPaciente(Integer codigo) {
		return getAmbulatorioRN().validaDadosPaciente(codigo);
	}
	
	@Override
	public Boolean verificarTrocaPacienteConsulta(Integer codigo){
		return getAmbulatorioRN().verificarTrocaPacienteConsulta(codigo);
	}
	
	@Override
	public boolean validarDadosPacienteAmbulatorio(Integer codigo) {
		return getAmbulatorioRN().validarDadosPacienteAmbulatorio(codigo);
	}

	@Override
	public Boolean necessitaRecadastramentoPaciente(Integer pacienteCodigo, Date dtRecadastroPaciente, Date dtConsulta) {
		return getAmbulatorioRN().necessitaRecadastramentoPaciente(pacienteCodigo, dtRecadastroPaciente, dtConsulta);
	}

	@Override
	public void desmarcaChegadaPaciente(Integer consultaNumero, String nomeMicrocomputador) throws ApplicationBusinessException {
		getPesquisarPacientesAgendadosON().desmarcaChegadaPaciente(consultaNumero, nomeMicrocomputador);
	}

	@Override
	public void atualizarConsultaVO(ConsultaAmbulatorioVO vo) {
		pesquisarPacientesAgendadosON.atualizarConsultaVO(vo);
	}

	@Override
	public void verificaPacienteOutraConsulta(Integer numeroConsulta) throws BaseException {
		getAmbulatorioRN().verificaPacienteOutraConsulta(numeroConsulta);
	}

	@Override
	public void atualizaControleConsultaSituacao(AacConsultas consulta, MamSituacaoAtendimentos situacao, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		getAmbulatorioRN().atualizaControleConsultaSituacao(consulta, situacao, nomeMicrocomputador);
	}

	@Override
	public List<VMamProcXCid> pesquisarCidsPorPrdSeqCidSeq(Integer prdSeq, Integer cidSeq) {
		return getVMamProcXCidDAO().pesquisarCidsPorPrdSeqCidSeq(prdSeq, cidSeq);
	}

	@Override
	public void validarQuantidadeProcedimentoAtendimentoConsulta(Byte quantidade) throws ApplicationBusinessException {
		getProcedimentoAtendimentoConsultaON().validarQuantidadeProcedimento(quantidade);
	}

	@Override
	public void verificarCidProcedimentoAtendimento(Integer consultaNumero, Integer prdSeq, Integer cidSeq)
			throws ApplicationBusinessException {
		getAtendimentoPacientesAgendadosON().verificarCidProcedimento(consultaNumero, prdSeq, cidSeq);
	}

	@Override
	public MamProcedimentoRealizado alterarQuantidadeProcedimentoAtendimento(Integer consultaNumero, Integer prdSeq, Byte quantidade,
			Boolean carga) throws BaseException, CloneNotSupportedException {
		return getProcedimentoAtendimentoConsultaON().alterarQuantidadeProcedimento(consultaNumero, prdSeq, quantidade, carga);
	}

	@Override
	public MamProcedimentoRealizado alterarCidProcedimentoAtendimento(MamProcedimentoRealizado procedimentoRealizado, String cidCodigo,
			Boolean carga) throws ApplicationBusinessException, CloneNotSupportedException {
		return getProcedimentoAtendimentoConsultaON().alterarCidProcedimento(procedimentoRealizado, cidCodigo, carga);
	}

	@Override
	public Boolean existeConsultasAgendadas(AacGradeAgendamenConsultas grade) {
		return getAacConsultasDAO().existeConsultasAgendadas(grade);
	}

	@Override
	public void adicionarProcedimentoConsulta(AacConsultas consultaSelecionada, AacConsultaProcedHospitalar procedimentoConsulta,
			AghCid cid, Integer phiSeq, Short espSeq, Byte quantidade, List<AacConsultaProcedHospitalar> listaProcedimentosHospConsulta,
			String nomeMicrocomputador, final Date dataFimVinculoServidor, final Boolean aack_prh_rn_v_apac_diaria,
			final Boolean aack_aaa_rn_v_protese_auditiva, final Boolean fatk_cap_rn_v_cap_encerramento) throws BaseException {

		getProcedimentoConsultaON().adicionarProcedimentoConsulta(consultaSelecionada, procedimentoConsulta, cid, phiSeq, espSeq,
				quantidade, listaProcedimentosHospConsulta, nomeMicrocomputador, dataFimVinculoServidor, aack_prh_rn_v_apac_diaria,
				aack_aaa_rn_v_protese_auditiva, fatk_cap_rn_v_cap_encerramento);
	}

	@Override
	public List<AacConsultaProcedHospitalar> buscarConsultaProcedHospPorNumeroConsulta(Integer numeroConsulta) {
		return getAacConsultaProcedHospitalarDAO().buscarConsultaProcedHospPorNumeroConsulta(numeroConsulta);
	}

	@Override
	public MamSituacaoAtendimentos obterSituacaoAtendimentos(Short seq) {
		return getMamSituacaoAtendimentosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void removerProcedimentoConsulta(AacConsultaProcedHospitalar procedimentoConsulta, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		getProcedimentoConsultaON().removerProcedimentoConsulta(procedimentoConsulta, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public List<VAacConvenioPlano> getListaConvenios(String parametro) {
		return getMarcacaoConsultaON().getListaConvenios(parametro);
	}

	@Override
	public Long getListaConveniosCount(String parametro) {
		return getMarcacaoConsultaON().getListaConveniosCount(parametro);
	}

	@Override
	public List<AacFormaAgendamento> listarFormasAgendamentos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AacPagador pagador, AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicaoAtendimento) {
		return this.getAacFormaAgendamentoDAO().listarFormasAgendamentos(firstResult, maxResult, orderProperty, asc, pagador,
				tipoAgendamento, condicaoAtendimento);
	}

	@Override
	public Long listarFormasAgendamentosCount(AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento) {
		return this.getAacFormaAgendamentoDAO().listarFormasAgendamentosCount(pagador, tipoAgendamento, condicaoAtendimento);
	}

	// InÃ­cio manterOrgaoPagador
	@Override
	public List<AacPagador> pesquisarPagadores(String filtro) {
		return this.getAacPagadorDAO().pesquisarPagadores(filtro);
	}

	@Override
	public List<AacPagador> pesquisarPagadorPaginado(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short codigoOrgaoPagador, String descricaoOrgaoPagador, DominioSituacao situacaoOrgaoPagador,
			DominioGrupoConvenio convenioOrgaoPagador) {
		return this.getAacPagadorDAO().pesquisarPagadorPaginado(firstResult, maxResult, orderProperty, asc, codigoOrgaoPagador,
				descricaoOrgaoPagador, situacaoOrgaoPagador, convenioOrgaoPagador);
	}

	@Override
	public Long countPagadorPaginado(Short codigoOrgaoPagador, String descricaoOrgaoPagador, DominioSituacao situacaoOrgaoPagador,
			DominioGrupoConvenio convenioOrgaoPagador) {
		return this.getAacPagadorDAO().countPesquisarPagadorPaginado(codigoOrgaoPagador, descricaoOrgaoPagador, situacaoOrgaoPagador,
				convenioOrgaoPagador);
	}

	@Override
	public Long pesquisarPagadoresCount(String filtro) {
		return this.getAacPagadorDAO().pesquisarPagadoresCount(filtro);
	}

	@Override
	public void persistirPagador(AacPagador aacPagador) throws ApplicationBusinessException {
		this.manterPagadorRN.persistirPagador(aacPagador);
	}

	@Override
	public void atualizarPagador(AacPagador aacPagador) throws ApplicationBusinessException {
		this.manterPagadorRN.atualizarPagador(aacPagador);

	}

	@Override
	public void excluirPagador(Short aacPagadorCodigo) throws ApplicationBusinessException {
		this.manterPagadorRN.remover(aacPagadorCodigo);

	}

	// fim manterOrgaoPagador

	@Override
	public List<AacTipoAgendamento> pesquisarTiposAgendamento(String filtro) {
		return this.getAacTipoAgendamentoDAO().pesquisarTiposAgendamento(filtro);
	}

	@Override
	public Long pesquisarTiposAgendamentoCount(String filtro) {
		return this.getAacTipoAgendamentoDAO().pesquisarTiposAgendamentoCount(filtro);
	}

	@Override
	public List<AacCondicaoAtendimento> pesquisarCondicoesAtendimento(String filtro) {
		return this.getAacCondicaoAtendimentoDAO().pesquisarCondicoesAtendimento(filtro);
	}

	@Override
	public Long pesquisarCondicoesAtendimentoCount(String filtro) {
		return this.getAacCondicaoAtendimentoDAO().pesquisarCondicoesAtendimentoCount(filtro);
	}

	@Override
	public List<RelatorioAgendamentoConsultaVO> obterAgendamentoConsulta(Integer nroConsulta) throws ApplicationBusinessException {
		return getMarcacaoConsultaON().obterAgendamentoConsulta(nroConsulta);
	}

	@Override
	public void marcarFaltaPacientes(Date dtPesquisa, List<VAacSiglaUnfSala> zonaSalas, VAacSiglaUnfSala zonaSala, DominioTurno turno,
			AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade, RapServidores profissional, String nomeMicrocomputador)
			throws NumberFormatException, BaseException {

		getMarcacaoConsultaON().marcarFaltaPacientes(dtPesquisa, zonaSalas, zonaSala,
				this.getPesquisarPacientesAgendadosON().definePeriodoTurno(turno), equipe, espCrmVO, especialidade, profissional,
				nomeMicrocomputador);
	}
	@Override
	public void marcarFaltaPaciente(Integer consultaNumero, String nomeMicrocomputador, boolean chegou,Integer codSituacaoAtend) throws NumberFormatException, BaseException {
		getMarcacaoConsultaON().marcarFaltaPaciente(consultaNumero, nomeMicrocomputador, chegou,codSituacaoAtend);
	}
	@Override
	public void salvarFormaAgendamento(AacFormaAgendamento formaAgendamento) throws BaseException {
		this.getManterFormasParaAgendamentoON().salvarFormaAgendamento(formaAgendamento);
	}

	@Override
	public void alterarFormaAgendamento(AacFormaAgendamento formaAgendamento) throws BaseException {
		this.getManterFormasParaAgendamentoON().alterarFormaAgendamento(formaAgendamento);
	}

	@Override
	public void removerFormaAgendamento(AacFormaAgendamentoId id) throws ApplicationBusinessException {
		this.getManterFormasParaAgendamentoON().removerFormaAgendamento(id);
	}

	@Override
	public Long obterQuantidadeAacNivelBuscaPorFormaAgendamento(AacFormaAgendamento formaAgendamento) {
		return this.getAacNivelBuscaDAO().obterQuantidadeAacNivelBuscaPorFormaAgendamento(formaAgendamento);
	}

	@Override
	public AacNivelBusca obterAacNivelBuscaPorChavePrimaria(AacNivelBuscaId id) {
		return this.getAacNivelBuscaDAO().obterPorChavePrimaria(id);
	}

	@Override
	public AacNivelBusca obterAacNivelBuscaPorChavePrimaria(AacNivelBuscaId id, Enum[] innerJoins, Enum[] leftJoins) {
		return this.getAacNivelBuscaDAO().obterPorChavePrimaria(id, innerJoins, leftJoins);
	}

	@Override
	public void salvarNivelBusca(AacNivelBusca nivelBusca) throws BaseException {
		this.getManterFormasParaAgendamentoON().salvarNivelBusca(nivelBusca);
	}

	@Override
	public void alterarNivelBusca(AacNivelBusca nivelBusca) throws BaseException {
		this.getManterFormasParaAgendamentoON().alterarNivelBusca(nivelBusca);
	}

	@Override
	public void salvarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws BaseException {
		this.getManterFormasParaAgendamentoON().salvarFormaEspecialidade(formaEspecialidade);
	}

	@Override
	public void alterarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws BaseException {
		this.getManterFormasParaAgendamentoON().alterarFormaEspecialidade(formaEspecialidade);
	}

	@Override
	public AacFormaEspecialidade obterAacFormaEspecialidadePorChavePrimaria(AacFormaEspecialidadeId id) {
		return getAacFormaEspecialidadeDAO().obterPorChavePrimaria(id);
	}

	public AacFormaEspecialidade obterAacFormaEspecialidadePorChavePrimaria(AacFormaEspecialidadeId id, Enum[] innerJoins, Enum[] leftJoins) {
		return getAacFormaEspecialidadeDAO().obterPorChavePrimaria(id, innerJoins, leftJoins);
	}

	@Override
	public void removerNivelBusca(AacNivelBuscaId id) {
		this.getAmbulatorioRN().removerNivelBusca(id);
	}

	@Override
	public void removerFormaEspecialidade(AacFormaEspecialidadeId id) throws ApplicationBusinessException {
		this.getAmbulatorioRN().removerFormaEspecialidade(id);
	}

	@Override
	public void clear() {
		this.flush();
		this.clear();
	}

	@Override
	public Short buscaProximoSeqpAacNivelBusca(Short fagCaaSeq, Short fagTagSeq, Short fagPgdSeq) {
		return getAacNivelBuscaDAO().buscaProximoSeqp(fagCaaSeq, fagTagSeq, fagPgdSeq);
	}

	@Override
	public List<AacConsultas> listarConsultasParaLiberar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer prontuarioPaciente, Integer codigoPaciente, Integer grade, AghEspecialidades especialidade, Date dataConsulta,
			String situacaoConsulta, Integer nroConsulta) {
		return this.getAacConsultasDAO().listarConsultasParaLiberar(firstResult, maxResult, orderProperty, asc, prontuarioPaciente,
				codigoPaciente, grade, especialidade, dataConsulta, situacaoConsulta, nroConsulta);
	}

	@Override
	public Long listarConsultasParaLiberarCount(Integer prontuarioPaciente, Integer codigoPaciente, Integer grade,
			AghEspecialidades especialidade, Date dataConsulta, String situacaoConsulta, Integer nroConsulta) {
		return this.getAacConsultasDAO().listarConsultasParaLiberarCount(prontuarioPaciente, codigoPaciente, grade, especialidade,
				dataConsulta, situacaoConsulta, nroConsulta);
	}

	@Override
	public void liberarConsulta(Integer numeroConsulta, String nomeMicrocomputador, Boolean possuiReconsulta) throws BaseException {

		this.getLiberarConsultasON().liberarConsulta(numeroConsulta, nomeMicrocomputador, new Date(), possuiReconsulta);
	}

	@Override
	public List<AacUnidFuncionalSalas> listarSalasPorUnidadeFuncionalSalaTipoSituacao(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Short unidadeFuncional, Byte sala, DominioTipoUnidadeFuncionalSala tipo,
			DominioSituacao situacao) {

		return getAacUnidFuncionalSalasDAO().listarSalasPorUnidadeFuncionalSalaTipoSituacao(firstResult, maxResult, orderProperty, asc,
				unidadeFuncional, sala, tipo, situacao);
	}

	@Override
	public Long listarSalasPorUnidadeFuncionalSalaTipoSituacaoCount(Short unidadeFuncional, Byte sala,
			DominioTipoUnidadeFuncionalSala tipo, DominioSituacao situacao) {
		return getAacUnidFuncionalSalasDAO().listarSalasPorUnidadeFuncionalSalaTipoSituacaoCount(unidadeFuncional, sala, tipo, situacao);
	}

	@Override
	public void persistirZonaSala(AacUnidFuncionalSalas oldZonaSala, AacUnidFuncionalSalas newZonaSala, DominioOperacoesJournal operacao)
			throws BaseException {
		this.getZonaSalaON().persistirZonaSala(oldZonaSala, newZonaSala, operacao);
	}

	@Override
	public void removerZonaSala(Short unfSeq, Byte sala) throws BaseException {
		this.getZonaSalaON().removerZonaSala(unfSeq, sala);

	}

	@Override
	public void verificarSituacaoAtendimento(Short seqAtendimento) throws ApplicationBusinessException {
		getMarcacaoConsultaRN().verificarSituacaoAtendimento(seqAtendimento);
	}

	@Override
	public void gerarMovimentacaoProntuario(AacConsultas consulta, RapServidores servidorLogado, Boolean exibeMsgProntuarioJaMovimentado)
			throws ApplicationBusinessException {
		this.getAgendaSamisON().requererProntuarios(consulta, servidorLogado, exibeMsgProntuarioJaMovimentado);
	}

	@Override
	public AacCondicaoAtendimento obterCondicaoAtendimentoPorCodigo(Short fagCaaSeq) {
		return getAacCondicaoAtendimentoDAO().obterPorChavePrimaria(fagCaaSeq);
	}

	@Override
	public AacTipoAgendamento obterTipoAgendamentoPorCodigo(Short fagTagSeq) {
		return getAacTipoAgendamentoDAO().obterPorChavePrimaria(fagTagSeq);
	}

	@Override
	public AacPagador obterPagadorPorCodigo(Short fagPgdSeq) {
		return getAacPagadorDAO().obterPorChavePrimaria(fagPgdSeq);
	}

	@Override
	public List<DocumentosPacienteVO> obterListaDocumentosPaciente(Integer conNumero, Integer gradeSeq, Boolean verificarProcesso) throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().obterListaDocumentosPaciente(conNumero, gradeSeq, verificarProcesso);
	}

	public AtestadoVO obterDadosAtestado(Long seq) {
		return getAtendimentoPacientesAgendadosON().obterDadosAtestado(seq);
	}

	@Override
	@BypassInactiveModule
	public List<DocumentosPacienteVO> obterListaDocumentosPacienteAnamneseEvolucao(Integer conNumero) {
		return getAtendimentoPacientesAgendadosON().obterListaDocumentosPacienteAnamneseEvolucao(conNumero);
	}

	@Override
	@BypassInactiveModule
	public Boolean verificarSeExiteListaDocumentosPacienteAnamneseEvolucao(Integer conNumero) {
		return getAtendimentoPacientesAgendadosON().verificarSeExiteListaDocumentosPacienteAnamneseEvolucao(conNumero);
	}

	// --[NOTAS ADICIONAIS]
	@Override
	@BypassInactiveModule
	public MamNotaAdicionalEvolucoes inserirNotaAdicionalEvolucoes(String notaAdicional, AacConsultas consulta)
			throws ApplicationBusinessException, ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().inserirNotaAdicionalEvolucoes(notaAdicional, consulta);
	}

	@Override
	public List<MamNotaAdicionalEvolucoes> obterNotaAdicionalEvolucoesConsulta(Integer consultaNumero) {
		return getMamNotaAdicionalEvolucoesDAO().obterNotaAdicionalEvolucoesConsulta(consultaNumero);
	}

	@Override
	public void excluiNotaAdicionalEvolucao(MamNotaAdicionalEvolucoes notaAdicional) throws ApplicationBusinessException,
			ApplicationBusinessException {
		getAtendimentoPacientesAgendadosRN().excluiNotaAdicionalEvolucao(notaAdicional);
	}

	@Override
	public MamNotaAdicionalAnamneses inserirNotaAdicionalAnamneses(String notaAdicional, AacConsultas consulta)
			throws ApplicationBusinessException, ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().inserirNotaAdicionalAnamneses(notaAdicional, consulta);
	}

	@Override
	public List<MamNotaAdicionalAnamneses> obterNotaAdicionalAnamnesesConsulta(Integer consultaNumero) {
		return getMamNotaAdicionalAnamnesesDAO().obterNotaAdicionalAnamnesesConsulta(consultaNumero);
	}

	@Override
	public void excluiNotaAdicionalAnamnese(MamNotaAdicionalAnamneses notaAdicional) throws ApplicationBusinessException,
			ApplicationBusinessException {
		getAtendimentoPacientesAgendadosRN().excluiNotaAdicionalAnamnese(notaAdicional);
	}

	// --

	@Override
	public List<VMamProcXCid> listarCidPorProcedimentoAtendimento(String parametro, Integer prdSeq) {
		return getVMamProcXCidDAO().pesquisarCidsParaProcedimentoAtendimentoMedico(parametro, prdSeq);
	}

	@Override
	public Long listarCidPorProcedimentoAtendimentoCount(String parametro, Integer prdSeq) {
		return getVMamProcXCidDAO().pesquisarCidsParaProcedimentoAtendimentoMedicoCount(parametro, prdSeq);
	}

	@Override
	public List<ProcedimentoAtendimentoConsultaVO> listarProcedimentosAtendimento(Integer consultaNumero, Short espSeq, Short paiEspSeq,
			Boolean inicio) throws BaseException {
		return getProcedimentoAtendimentoConsultaON().listarProcedimentosAtendimento(consultaNumero, espSeq, paiEspSeq, inicio);
	}

	/**
	 * Retorna a descricao (string) do CID na forma capitalizada.</br>
	 * 
	 * <b>Para manter a compatibilidade com o que jÃ¡ havia sido implementado,
	 * foi mantido o mÃ©todo sem o parÃ¢metro de tipo, este assume por default o
	 * CapitalizeEnum.PRIMEIRA</b>
	 * 
	 * @param descCid
	 */
	@Override
	@BypassInactiveModule
	public String obterDescricaoCidCapitalizada(String descCid) {
		return atendimentoPacientesAgendadosON.obterDescricaoCidCapitalizada(descCid);
	}

	/**
	 * Retorna a descricao (string) do CID na forma capitalizada.
	 * 
	 * @param descCid
	 * @param tipo
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public String obterDescricaoCidCapitalizada(String descCid, CapitalizeEnum tipo) {
		return atendimentoPacientesAgendadosON.obterDescricaoCidCapitalizada(descCid, tipo);
	}

	@Override
	public String obtemDescricaoConsultaAnterior(AacConsultas consulta) throws ApplicationBusinessException, ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().obtemDescricaoConsultaAnterior(consulta);
	}

	@Override
	public String obtemDescricaoConsultaAtual(AacConsultas consulta) throws ApplicationBusinessException, ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().obtemDescricaoConsultaAtual(consulta);
	}

	@Override
	public Integer getTinSeqEvolucao() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().getTinSeqEvolucao();
	}

	@Override
	public Integer getTinSeqAnamnese() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().getTinSeqAnamnese();
	}

	@Override
	public String getDescricaoItemAnamnese() throws ApplicationBusinessException {
		return getMamAnamnesesON().getDescricaoItemAnamnese();
	}

	@Override
	public String getDescricaoItemEvolucao() throws ApplicationBusinessException {
		return getMamAnamnesesON().getDescricaoItemEvolucao();
	}

	@Override
	public MamItemAnamneses primeiroItemAnamnesesPorAnamneses(Long anaSeq) {
		return getMamItemAnamnesesDAO().primeiroItemAnamnesesPorAnamneses(anaSeq);
	}

	@Override
	public List<MamItemAnamneses> pesquisarItemAnamnesesPorAnamneses(Long anaSeq) {
		return getMamItemAnamnesesDAO().pesquisarItemAnamnesesPorAnamneses(anaSeq);
	}

	@Override
	public MamItemEvolucoes primeiroItemEvolucoesPorEvolucao(Long evoSeq) {
		return getMamItemEvolucoesDAO().primeiroItemEvolucoesPorEvolucao(evoSeq);
	}

	public java.util.List<MamItemEvolucoes> pesquisarItemEvolucoesPorEvolucao(Long evoSeq) {
		return getMamItemEvolucoesDAO().pesquisarItemEvolucoesPorEvolucao(evoSeq);
	}

	@Override
	public Boolean existeConsultaPacientesAgendados(Date dtPesquisa, List<VAacSiglaUnfSala> zonaSalas, VAacSiglaUnfSala zonaSala,
			DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade, RapServidores profissional) {

		return getAacConsultasDAO().existeConsultaPacientesAgendados(dtPesquisa, zonaSalas, zonaSala, turno, equipe, espCrmVO,
				especialidade, profissional);
	}

	@Override
	@Secure("#{s:hasPermission('realizarAnamneseAmbulatorio','salvar')}")
	public void salvarAnamnese(String texto, AacConsultas consulta) throws ApplicationBusinessException, ApplicationBusinessException {
		getAtendimentoPacientesAgendadosON().salvarAnamnese(texto, consulta, getTinSeqAnamnese());
	}

	@Override
	@Secure("#{s:hasPermission('realizarEvolucaoAmbulatorio','salvar')}")
	public void salvarEvolucao(String texto, AacConsultas consulta) throws ApplicationBusinessException, ApplicationBusinessException {
		getAtendimentoPacientesAgendadosON().salvarEvolucao(texto, consulta, getTinSeqEvolucao());
	}

	@Override
	public MamEvolucoes obterEvolucaoAtivaPorNumeroConsulta(Integer conNumero) {
		return getMamEvolucoesDAO().obterEvolucaoAtivaPorNumeroConsulta(conNumero);
	}

	@Override
	public void editarNotaAdicionalAnamneses(MamNotaAdicionalAnamneses notaAdicional, String descricaoAdicional, AacConsultas consulta)
			throws ApplicationBusinessException {
		getAtendimentoPacientesAgendadosON().editarNotaAdicionalAnamneses(notaAdicional, descricaoAdicional, consulta);
	}

	@Override
	public void editarNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes notaAdicional, String descricaoAdicional, AacConsultas consulta)
			throws ApplicationBusinessException {
		getAtendimentoPacientesAgendadosON().editarNotaAdicionalEvolucoes(notaAdicional, descricaoAdicional, consulta);
	}

	@Override
	@BypassInactiveModule
	public RelatorioAnamneseEvolucaoVO retornarRelatorioAnamneseEvolucao(Integer conNumero, DominioAnamneseEvolucao tipo)
			throws ApplicationBusinessException, ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().obterRelatorioAnamneseEvolucao(conNumero, tipo);
	}

	@Override
	public MamAnamneses obterAnamneseAtivaPorNumeroConsulta(Integer conNumero) {
		return getMamAnamnesesDAO().obterAnamneseAtivaPorNumeroConsulta(conNumero);
	}

	@Override
	public MamReceituarios primeiroReceituarioPorConsultaETipo(Integer consultaNumero, DominioTipoReceituario tipo) {
		return getMamReceituariosDAO().primeiroReceituarioPorConsultaETipo(consultaNumero, tipo);
	}

	@Override
	public void executarOperacoesAposSelecionarProcedimento(ProcedimentoAtendimentoConsultaVO procedimentoVO, Integer consultaNumero)
			throws BaseException {
		getProcedimentoAtendimentoConsultaON().executarOperacoesAposSelecionarProcedimento(procedimentoVO, consultaNumero);
	}

	@Override
	public BigDecimal buscarSeqNenhumProcedimento() throws ApplicationBusinessException {
		return getProcedimentoAtendimentoConsultaON().buscarSeqNenhumProcedimento();
	}

	@Override
	public Boolean verificarNenhumProcedimento(Integer prdSeq, BigDecimal seqProcNenhum) throws ApplicationBusinessException {
		return getProcedimentoAtendimentoConsultaON().verificarNenhumProcedimento(prdSeq, seqProcNenhum);
	}

	@Override
	public void mampPend(Integer conNumero, Date dthrMvto, Short satSeq, String nomeMicrocomputador) throws ApplicationBusinessException {
		getCancelamentoAtendimentoRN().mampPend(conNumero, dthrMvto, satSeq, nomeMicrocomputador);
	}

	@Override
	public Short buscaSituacaoPendencia(DominioMotivoPendencia situacao) throws ApplicationBusinessException {
		return getCancelamentoAtendimentoRN().buscaSituacaoPendencia(situacao);
	}

	@Override
	public Boolean existeDocumentosImprimirPaciente(AacConsultas consulta, Boolean verificarProcesso) throws ApplicationBusinessException,
			ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().existeDocumentosImprimirPaciente(consulta, verificarProcesso);
	}

	@Override
	public void integraProcedimento(Integer seq) throws BaseException {
		getAmbulatorioRN().integraProcedimento(seq);
	}

	@Override
	public Boolean verificaUsuarioElaboraAnamnese() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosRN().verificaUsuarioElaboraAnamnese();
	}

	@Override
	public Boolean verificaUsuarioElaboraEvolucao() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosRN().verificaUsuarioElaboraEvolucao();
	}

	@Override
	public void atualizarIndImpressaoAnamnese(Long seqAnamnese) {
		getAtendimentoPacientesAgendadosON().atualizarIndImpressaoAnamnese(seqAnamnese);
	}

	@Override
	public void atualizarIndImpressaoEvolucao(Long seqEvolucao) {
		getAtendimentoPacientesAgendadosON().atualizarIndImpressaoEvolucao(seqEvolucao);
	}

	@Override
	public void atualizarIndImpressaoReceituario(Long seqReceita) throws ApplicationBusinessException {
		getAtendimentoPacientesAgendadosON().atualizarIndImpressaoReceituario(seqReceita);
	}

	@Override
	public void desatacharVMamProcXCid(VMamProcXCid vMamProcXCid) {
		getVMamProcXCidDAO().desatachar(vMamProcXCid);
	}

	@Override
	public List<GerarDiariasProntuariosSamisVO> pesquisarMapaDesarquivamento(Date dataDiaria) throws ApplicationBusinessException,
			ApplicationBusinessException {
		return getGerarDiariasProntuariosSamisON().pesquisarMapaDesarquivamento(dataDiaria);
	}

	@Override
	public void inicioDiaria(Date dataDiaria) throws ApplicationBusinessException {
		getGerarDiariasProntuariosSamisON().inicioDiaria(dataDiaria);
	}

	@Override
	public void fimDiaria(Date dataDiaria) throws ApplicationBusinessException, ApplicationBusinessException {
		getGerarDiariasProntuariosSamisON().fimDiaria(dataDiaria);
	}

	@Override
	public List<AacRetornos> getListaRetornos(String objPesquisa) {
		return getAacRetornosDAO().obterListaRetornosAtivos(objPesquisa);
	}

	@Override
	public List<AacRetornos> getListaRetornosAtivos(String objPesquisa) {
		return getAacRetornosDAO().pesquisarPorNomeCodigoAtivo(objPesquisa);
	}

	@Override
	public List<MamItemExame> pesquisarItemExamePorDescricaoOuSeq(Object param, String ordem, Integer maxResults) {
		return this.getItemExameON().pesquisarItemExamePorDescricaoOuSeq(param, ordem, maxResults);
	}

	@Override
	public void atualizarRetornoConsulta(Integer numeroConsulta, AacRetornos retorno, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws NumberFormatException, BaseException {

		getMarcacaoConsultaON().atualizarRetornoConsulta(numeroConsulta, retorno, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public VAacConvenioPlano obterConvenioPlanoPorId(Short cnvCodigo, Byte plano) {
		return getVAacConvenioPlanoDAO().obterVAacConvenioPlanoPorId(cnvCodigo, plano);
	}

	@Override
	public Long pesquisarItemExamePorDescricaoOuSeq(Object param) {
		return this.getItemExameON().pesquisarItemExamePorDescricaoOuSeq(param);
	}

	@Override
	@BypassInactiveModule
	public List<MamItemMedicacao> listarTodosItensMedicacao(String ordem) {
		return this.getItemMedicacaoON().listarTodosItensMedicacao(ordem);
	}

	@Override
	public List<MamLaudoAih> obterLaudoAihPorTrgSeq(long trgSeq) {
		return this.getLaudoAihON().obterPorTrgSeq(trgSeq);
	}

	@Override
	public List<MamTrgEncInterno> obterTrgEncInternoPorConsulta(AacConsultas consulta) {
		return this.getTrgEncInternoON().obterPorConsulta(consulta);
	}

	@Override
	public Boolean validarProcessoConsultaAnamnese() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcessoConsultaAnamnese();
	}

	@Override
	public Boolean validarProcessoConsultaEvolucao() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcessoConsultaEvolucao();
	}

	@Override
	public Boolean validarProcessoConsultaProcedimento() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcessoConsultaProcedimento();
	}

	@Override
	public Boolean validarProcessoConsultaReceita() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcessoConsultaReceita();
	}

	@Override
	public Boolean validarProcessoExecutaAnamnese() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcessoExecutaAnamnese();
	}

	@Override
	public Boolean validarProcessoExecutaEvolucao() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcessoExecutaEvolucao();
	}

	@Override
	public Boolean validarProcessoExecutaProcedimento() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcessoExecutaProcedimento();
	}

	@Override
	public Boolean validarProcessoExecutaReceita() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcessoExecutaReceita();
	}

	@Override
	public Boolean validarProcessoExecutaSolicitacaoExame() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcessoExecutaSolicitacaoExame();
	}

	@Override
	public List<MamItemReceituario> atualizarReceituarioGeral(MamReceituarios receitaGeral, AacConsultas consultaSelecionada,
			Integer viasGeral, List<MamItemReceituario> itemReceitaGeralList) throws BaseException, CloneNotSupportedException {
		return getAtendimentoPacientesAgendadosRN().atualizarReceituarioGeral(receitaGeral, consultaSelecionada, viasGeral,
				itemReceitaGeralList);
	}

	@Override
	public List<MamItemReceituario> atualizarReceituarioEspecial(MamReceituarios receitaEspecial, AacConsultas consultaSelecionada,
			Integer viasEspecial, List<MamItemReceituario> itemReceitaEspecialList) throws BaseException, CloneNotSupportedException {
		return getAtendimentoPacientesAgendadosRN().atualizarReceituarioEspecial(receitaEspecial, consultaSelecionada, viasEspecial,
				itemReceitaEspecialList);
	}

	@Override
	public void excluirReceituarioEspecial(MamReceituarios receitaEspecial, AacConsultas consultaSelecionada) throws BaseException {
		getAtendimentoPacientesAgendadosRN().excluirReceituarioEspecial(receitaEspecial, consultaSelecionada);
	}

	@Override
	public void excluirReceituarioGeral(MamReceituarios receitaGeral, AacConsultas consultaSelecionada) throws BaseException {
		getAtendimentoPacientesAgendadosRN().excluirReceituarioGeral(receitaGeral, consultaSelecionada);
	}

	@Override
	public void excluirReceitaGeral(MamReceituarios receitaGeral, MamItemReceituario item, AacConsultas consultaSelecionada,
			Integer viasGeral, List<MamItemReceituario> itemReceitaGeralList) throws BaseException, CloneNotSupportedException {
		getAtendimentoPacientesAgendadosRN().excluirReceitaGeral(receitaGeral, item, consultaSelecionada, viasGeral, itemReceitaGeralList);
	}

	@Override
	public void excluirReceitaEspecial(MamReceituarios receitaEspecial, MamItemReceituario item, AacConsultas consultaSelecionada,
			Integer viasEspecial, List<MamItemReceituario> itemReceitaEspecialList) throws BaseException, CloneNotSupportedException {
		getAtendimentoPacientesAgendadosRN().excluirReceitaEspecial(receitaEspecial, item, consultaSelecionada, viasEspecial,
				itemReceitaEspecialList);
	}

	@Override
	@BypassInactiveModule
	public List<MamDiagnostico> buscarDiagnosticosAtivosPaciente(Integer codigo) throws ApplicationBusinessException {
		return this.getListarDiagnosticosAtivosPacienteON().buscarDiagnosticosAtivosPaciente(codigo);
	}

	@Override
	@BypassInactiveModule
	public List<MamDiagnostico> buscarDiagnosticosResolvidosPaciente(Integer codigo) throws ApplicationBusinessException {
		return this.getListarDiagnosticosAtivosPacienteON().buscarDiagnosticosResolvidosPaciente(codigo);
	}

	// Aqui precisa de bypass na persistencia pq Ã© usado na POL
	// No momento da implantacao da 4.0 no HCPA essa Ã© a unica solucao viavel
	// pq nao Ã© possÃ­vel ligar o modulo ambulatorio no momento
	@Override
	@BypassInactiveModule
	public void salvarListaDiganosticosPaciente(List<DiagnosticosPacienteVO> listaVO) throws ApplicationBusinessException {
		this.getListarDiagnosticosAtivosPacienteON().salvarListaDiganosticosPaciente(listaVO);
	}

	@Override
	public void excluirDiagnosticosPorCidAtendimentos(AghAtendimentos atendimento) throws ApplicationBusinessException {
		this.getListarDiagnosticosAtivosPacienteON().excluirDiagnosticosPorCidAtendimentos(atendimento);
	}

	@Override
	@BypassInactiveModule
	public void validarInsercaoDiagnostico(MamDiagnostico diagnostico) throws ApplicationBusinessException {
		getListarDiagnosticosAtivosPacienteRN().beforeRowInsert(diagnostico, null);
	}

	@Override
	@BypassInactiveModule
	public void validarAtualizacaoDiagnostico(MamDiagnostico diagnostico, RapServidores rapServidores) throws ApplicationBusinessException {
		getListarDiagnosticosAtivosPacienteRN().beforeRowUpdate(diagnostico, rapServidores, true, true, false);
	}

	@Override
	@BypassInactiveModule
	public MamDiagnostico inserirDiagnostico(MamDiagnostico diagnostico) throws ApplicationBusinessException {
		return getListarDiagnosticosAtivosPacienteRN().inserir(diagnostico);
	}

	@Override
	@BypassInactiveModule
	public void atualizarDiagnostico(MamDiagnostico diagnostico, MamDiagnostico diagnosticoOld) throws BaseException {
		this.getListarDiagnosticosAtivosPacienteRN().atualizarDiagnostico(diagnostico, diagnosticoOld);
	}

	@Override
	public void atualizarConsulta(AacConsultas consulta) throws ApplicationBusinessException {
		this.getAacConsultasDAO().atualizar(consulta);
	}

	@Override
	public void desatacharItemReceituario(MamItemReceituario itemReceituario) {
		getMamItemReceituarioDAO().desatachar(itemReceituario);
	}

	@Override
	public void atualizarInterconsultas(final Integer pacCodigo, final Character tipo) {
		this.getMamInterconsultasDAO().atualizarInterconsultas(pacCodigo, tipo,
				getServidorLogadoFacade().obterServidorLogado().getUsuario());
	}

	@Override
	public AacConsultaProcedHospitalar inserirAacConsultaProcedHospitalar(final AacConsultaProcedHospitalar novo, String nomeMicrocomputador)
			throws BaseException {
		return getAacConsultaProcedHospitalarON().inserir(novo, nomeMicrocomputador);
	}

	@Override
	public void importarArquivo(String consulta, String nomeArquivo, Integer numeroLinha, StringBuilder msgErroProcedimento)
			throws ApplicationBusinessException {
		getArquivoSisregON().importarArquivo(consulta, nomeArquivo, numeroLinha, msgErroProcedimento);
	}

	@Override
	public void limparConsultasSisreg() {
		getArquivoSisregON().limparConsultasSisreg();
	}

	@Override
	public void tratarErrosImportacaoConsultas(String nomeArquivo, StringBuilder msgErroProcedimento) throws ApplicationBusinessException {
		getArquivoSisregON().tratarErrosImportacaoConsultas(nomeArquivo, msgErroProcedimento);
	}

	@Override
	public StringBuilder marcarConsultas(List<AacConsultasSisreg> consultasSisreg, Integer totalConsultas, String nomeMicrocomputador) {

		return getMarcacaoConsultaSisregON().marcarConsultas(consultasSisreg, totalConsultas, nomeMicrocomputador);
	}

	@Override
	public List<AacConsultasSisreg> obterConsultasSisreg() {
		return getMarcacaoConsultaSisregON().obterConsultasSisreg();
	}

	@Override
	public File exportarLogSisreg(StringBuilder log) {
		return getExportacaoLogSisregON().exportarLog(log);
	}

	@Override
	public boolean possuiConsultasPorGradeAgendamento(Integer grdSeq) {
		return this.getAacConsultasDAO().possuiConsultasPorGradeAgendamento(grdSeq);
	}

	@Override
	public boolean possuiHorariosGradeConsultaPorGradeAgendamento(Integer grdSeq) {
		return this.getAacHorarioGradeConsultaDAO().possuiHorariosGradeConsultaPorGradeAgendamento(grdSeq);
	}

	@Override
	public DominioSituacao verificarSituacaoGrade(Integer grdSeq, Date data) {
		return this.getManterGradeAgendamentoRN().verificarSituacaoGrade(grdSeq, data);
	}

	@Override
	public String obterTextoAgendamentoConsulta(String hospitalLocal, String unidadeFuncional, String sala, AacConsultas consulta)
			throws ApplicationBusinessException {
		return getMarcacaoConsultaON().obterTextoAgendamentoConsulta(hospitalLocal, unidadeFuncional, sala, consulta);
	}

	@Override
	public String obterTextoAgendamentoConsulta(String hospitalLocal, String unidadeFuncional, String sala, AacConsultas consulta,boolean flag)
			throws ApplicationBusinessException {
		return getMarcacaoConsultaON().obterTextoAgendamentoConsulta(hospitalLocal, unidadeFuncional, sala, consulta,flag);
	}

	@Override
	public List<AacCondicaoAtendimento> pesquisarCondicaoAtendimentoPaginado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short filtroSeq, String filtroDescricao, String filtroSigla, DominioConsultaGenerica filtroGenericaAmb,
			DominioConsultaGenerica filtroGenericaInternacao, Boolean filtroCriticaApac, DominioSituacao filtroSituacaoCondicaoAtendimento) {

		return this.getAacCondicaoAtendimentoDAO().pesquisarCondicaoAtendimentoPaginado(firstResult, maxResult, orderProperty, asc,
				filtroSeq, filtroDescricao, filtroSigla, filtroGenericaAmb, filtroGenericaInternacao, filtroCriticaApac,
				filtroSituacaoCondicaoAtendimento);
	}

	@Override
	public Long countCondicaoAtendimentoPaginado(Short filtroSeq, String filtroDescricao, String filtroSigla,
			DominioConsultaGenerica filtroGenericaAmb, DominioConsultaGenerica filtroGenericaInternacao, Boolean filtroCriticaApac,
			DominioSituacao filtroSituacaoCondicaoAtendimento) {
		return this.getAacCondicaoAtendimentoDAO().countPesquisarCondicaoAtendimentoPaginado(filtroSeq, filtroDescricao, filtroSigla,
				filtroGenericaAmb, filtroGenericaInternacao, filtroCriticaApac, filtroSituacaoCondicaoAtendimento);
	}

	@Override
	public void persistirCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) throws BaseException {
		this.getManterCondicaoAtendimentoRN().persistirCondicaoAtendimento(condicaoAtendimento);
	}

	@Override
	public void atualizarCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) throws BaseException {
		this.getManterCondicaoAtendimentoRN().atualizarCondicaoAtendimento(condicaoAtendimento);
	}

	@Override
	public void removerCondicaoAtendimento(Short codigoCondicaoAtendimento) throws ApplicationBusinessException {
		this.getManterCondicaoAtendimentoRN().remover(codigoCondicaoAtendimento);
	}

	@Override
	public File geraArquivoConsulta(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana,
			Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetorno,
			FatProcedHospInternosVO filtroProcedimento)	throws IOException, ApplicationBusinessException {
		List<AacConsultas> consultas = aacConsultasDAO.listarConsultasAgenda(filtroServico, filtroGrdSeq, filtroUslUnfSeq,
				filtroEspecialidade, filtroEquipe, filtroPagador, filtroAutorizacao, filtroCondicao, filtroSituacao, filtroDiaSemana,
				filtroHoraConsulta, filtroDtInicio, filtroDtFim, filtroProfissional, filtroRetorno, null, null, filtroProcedimento, false);
		return this.getConsultaAgendaON().geraArquivoConsulta(consultas);
	}
	
	@Override
	public List<AacConsultas> carregarArquivoRass(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana,
			Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetorno)
			throws IOException, ApplicationBusinessException{
		List<AacConsultas> consultas =  aacConsultasDAO.listarConsultasAgenda(filtroServico, filtroGrdSeq, filtroUslUnfSeq,
				filtroEspecialidade, filtroEquipe, filtroPagador, filtroAutorizacao, filtroCondicao, filtroSituacao, filtroDiaSemana,
				filtroHoraConsulta, filtroDtInicio, filtroDtFim, filtroProfissional, filtroRetorno, null, null, null, true);
		return consultas;
	}
	
	@Override
	public File geraArquivoRass(List<AacConsultas> consultas, Date filtroDtInicio, Date filtroDtFim,  Short filtroUslUnfSeq, AacRetornos filtroRetorno)
			throws IOException, ApplicationBusinessException{
		return this.getConsultaAgendaON().geraArquivoRass(consultas, filtroDtInicio, filtroDtFim, filtroUslUnfSeq, filtroRetorno);
	}
	
	@Override
	public List<AacConsultas> carregarArquivoEsus(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana,
			Date filtroHoraConsulta, Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetorno)
			throws IOException, ApplicationBusinessException{
		List<AacConsultas> consultas =  aacConsultasDAO.listarConsultasAgenda(filtroServico, filtroGrdSeq, filtroUslUnfSeq,
				filtroEspecialidade, filtroEquipe, filtroPagador, filtroAutorizacao, filtroCondicao, filtroSituacao, filtroDiaSemana,
				filtroHoraConsulta, filtroDtInicio, filtroDtFim, filtroProfissional, filtroRetorno, null, null, null, true);
		return consultas;
	}
	
	@Override
	public ArquivosEsusVO gerarArquivoEsus(List<AacConsultas> consultas, Date filtroDtInicio, Date filtroDtFim,  AghEspecialidades especialidade)
			throws IOException, ApplicationBusinessException, BaseException{
		return this.getEsusConsultasON().gerarArquivoEsus(consultas, filtroDtInicio, filtroDtFim, especialidade);
	}
	
	@Override
	public void verificarUnidadeCapsRetornoAtendido(Short filtroUslUnfSeq, AacRetornos filtroRetorno) throws ApplicationBusinessException{
		this.getConsultaAgendaON().verificarUnidadeCapsRetornoAtendido(filtroUslUnfSeq, filtroRetorno);
	}
	@Override
	public void verificarAgendasEsus(AghEspecialidades filtroEspecialidade, AacRetornos filtroRetorno) throws ApplicationBusinessException{
		this.getEsusConsultasON().verificarAgendasEsus(filtroEspecialidade, filtroRetorno);
	}
	
	//List<AacConsultas> consultas, Date dtInicio, Date dtFim, Date mesMovimentoProducaoRas, String versaoRas

	@Override
	public DominioTurno defineTurno(Date data) throws ApplicationBusinessException {
		return this.getPesquisarPacientesAgendadosON().defineTurno(data);
	}

	@Override
	public DominioTurno defineTurnoAtual() throws ApplicationBusinessException, ApplicationBusinessException {
		return this.getPesquisarPacientesAgendadosON().defineTurno(new Date());
	}

	@Override
	public Boolean verificaGradeTipoSisreg(Integer seq) {
		return getAacConsultasDAO().verificaGradeTipoSisreg(seq);
	}

	@Override
	public DataInicioFimVO definePeriodoTurno(DominioTurno turno) throws ApplicationBusinessException {
		return this.getPesquisarPacientesAgendadosON().definePeriodoTurno(turno);
	}

	@Override
	public void processarProcedimentoConsultaPorRetorno(Integer consultaNumero, String nomeMicrocomputador,
			final Date dataFimVinculoServidor, AacRetornos retorno, 
			Boolean aack_prh_rn_v_apac_diaria, 
			Boolean aack_aaa_rn_v_protese_auditiva, 
			Boolean fatk_cap_rn_v_cap_encerramento
			) throws BaseException {

		getAmbulatorioConsultaON().processarConsultaProcedimentoPorRetorno(consultaNumero, nomeMicrocomputador, dataFimVinculoServidor, retorno, 
				aack_prh_rn_v_apac_diaria, 
				aack_aaa_rn_v_protese_auditiva, 
				fatk_cap_rn_v_cap_encerramento
				);
	}

	
	@Override
	public List<DocumentosPacienteVO> obterListaDocumentosPacienteParaCertificacao(Integer conNumero) throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().obterListaDocumentosPacienteParaCertificacao(conNumero);
	}

	@Override
	@BypassInactiveModule
	public StringBuffer visualizaConsulta(Integer numero, Integer codigo) throws BaseException {
		return getAmbulatorioRN().visualizaConsulta(numero, codigo);
	}

	@Override
	public List<MamDiagnostico> listarDiagnosticosPorCidAtendimentoSeq(Integer ciaSeq) {
		return getDiagnosticoDAO().listarDiagnosticosPorCidAtendimentoSeq(ciaSeq);
	}

	@Override
	public MamDiagnostico obterDiagnosticoPorChavePrimaria(Long seq) {
		return getDiagnosticoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public MamDiagnostico obterDiagnosticoOriginal(Long seq) {
		return getDiagnosticoDAO().obterOriginal(seq);
	}

	@Override
	@BypassInactiveModule
	public List<MamAltaSumario> pesquisarAltasSumariosParaAltasAmbulatoriais(Integer numeroConsulta) {
		return getMamAltasSumarioDAO().pesquisarAltasSumariosParaAltasAmbulatoriais(numeroConsulta);
	}

	@Override
	@BypassInactiveModule
	public MamAltaSumario obterMamAltaSumarioPorId(Long seq) {
		return getMamAltasSumarioDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public List<MamAltaDiagnosticos> procurarAltaDiagnosticosBySumarioAltaEIndSelecionado(MamAltaSumario altaSumario,
			DominioSimNao indSelecionado) {
		return getMamAltaDiagnosticosDAO().procurarAltaDiagnosticosBySumarioAltaEIndSelecionado(altaSumario, indSelecionado);
	}

	@Override
	@BypassInactiveModule
	public List<MamAltaEvolucoes> procurarAltaEvolucoesBySumarioAlta(MamAltaSumario altaSumario) {
		return getMamAltaEvolucoesDAO().procurarAltaEvolucoesBySumarioAlta(altaSumario);
	}

	@Override
	@BypassInactiveModule
	public List<MamAltaPrescricoes> procurarAltaPrescricoesBySumarioAltaEIndSelecionado(MamAltaSumario altaSumario,
			DominioSimNao indSelecionado) {
		return getMamAltaPrescricoesDAO().procurarAltaPrescricoesBySumarioAltaEIndSelecionado(altaSumario, indSelecionado);
	}

	@Override
	@BypassInactiveModule
	public MamControles obterMamControlePorNumeroConsulta(Integer numeroConsulta) {
		return getMamControlesDAO().obterMamControlePorNumeroConsulta(numeroConsulta);
	}

	@Override
	@BypassInactiveModule
	public List<AltaAmbulatorialPolImpressaoVO> recuperarAltaAmbuPolImpressaoVO(Long seqMamAltaSumario) {
		return getMamAltasSumarioDAO().recuperarAltaAmbuPolImpressaoVO(seqMamAltaSumario);
	}

	@Override
	@BypassInactiveModule
	public Long recuperarAltaAmbuPolImpressaoVOCount(Long seqMamAltaSumario) {
		return getMamAltasSumarioDAO().recuperarAltaAmbuPolImpressaoVOCount(seqMamAltaSumario);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarAltaPrescricoesCount(Long seqMamAltaSumario) {
		return getMamAltaPrescricoesDAO().pesquisarAltaPrescricoesCount(seqMamAltaSumario);
	}

	@Override
	@BypassInactiveModule
	public List<AltaAmbulatorialPolEvolucaoVO> recuperarAltaAmbPolEvolucaoList(Long seqMamAltaSumario) {
		return getMamAltaEvolucoesDAO().recuperarAltaAmbPolEvolucaoList(seqMamAltaSumario);
	}

	@Override
	@BypassInactiveModule
	public List<AltaAmbulatorialPolDiagnosticoVO> recuperarAltaAmbPolDiagnosticoList(Long seqMamAltaSumario) {
		return getMamAltaDiagnosticosDAO().recuperarAltaAmbPolDiagnosticoList(seqMamAltaSumario);
	}

	@Override
	@BypassInactiveModule
	public List<AltaAmbulatorialPolPrescricaoVO> recuperarAltaAmbPolPrescricaoList(Long seqMamAltaSumario) {
		return getMamAltaPrescricoesDAO().recuperarAltaAmbPolPrescricaoList(seqMamAltaSumario);
	}

	@Override
	@BypassInactiveModule
	public Boolean validarProcesso(Short pSerVinCodigo, Integer pSerMatricula, Short pSeqProcesso) throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().validarProcesso(pSerVinCodigo, pSerMatricula, pSeqProcesso);
	}

	@Override
	@BypassInactiveModule
	public void inserirNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes registroInclusao) throws ApplicationBusinessException {
		getMarcacaoConsultaRN().inserirNotaAdicionalEvolucoes(registroInclusao);
	}

	@Override
	@BypassInactiveModule
	public Boolean verificarExibicaoNodoAltasAmbulatoriais(Integer codPaciente) {
		return getMamAltasSumarioDAO().verificarExibicaoNodoAltasAmbulatoriais(codPaciente);
	}

	@Override
	@BypassInactiveModule
	public Short retornarCodigoProcessoEvolucao() throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosON().retornarCodigoProcessoEvolucao();
	}

	@Override
	@BypassInactiveModule
	public void atualizarMamNotaAdicionalEvolucao(MamNotaAdicionalEvolucoes notaAdicionalEvolucao) {
		getMamNotaAdicionalEvolucoesDAO().atualizar(notaAdicionalEvolucao);
	}

	@Override
	public String obterAssinaturaTexto(MamAnamneses anamnese, MamEvolucoes evolucao, MamNotaAdicionalAnamneses notaAdicionalAnamnese,
			MamNotaAdicionalEvolucoes notaAdicionalEvolucao) throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosRN().obterAssinaturaTexto(anamnese, evolucao, notaAdicionalAnamnese, notaAdicionalEvolucao);
	}

	@Override
	public String obterIdentificacaoResponsavel(MamAnamneses anamnese, MamEvolucoes evolucao) throws ApplicationBusinessException {
		return getAtendimentoPacientesAgendadosRN().obterIdentificacaoResponsavel(anamnese, evolucao);
	}

	@Override
	public List<AacPagador> pesquisaPagadoresComAgendamento() {
		return getAacFormaAgendamentoDAO().pesquisaPagadoresComAgendamento();
	}

	@Override
	public List<AacTipoAgendamento> pesquisaTipoAgendamentoComAgendamentoEPagador(AacPagador pagador) {
		return getAacFormaAgendamentoDAO().pesquisaTipoAgendamentoComAgendamentoEPagador(pagador);
	}

	@Override
	public List<AacCondicaoAtendimento> pesquisaCondicaoAtendimentoComAgendamentoEPagadorETipo(AacPagador pagador, AacTipoAgendamento tipo) {
		return getAacFormaAgendamentoDAO().pesquisaCondicaoAtendimentoComAgendamentoEPagadorETipo(pagador, tipo);
	}

	@Override
	public AacFormaAgendamento findFormaAgendamento(AacPagador pagador, AacTipoAgendamento tipo, AacCondicaoAtendimento condicao) {
		return getAacFormaAgendamentoDAO().findFormaAgendamento(pagador, tipo, condicao);
	}

	@Override
	@BypassInactiveModule
	public AacConsultas obterConsulta(Integer numero) {
		return getConsultasON().obterPorChavePrimaria(numero);
	}

	/**
	 * ORADB: Function AACC_VER_SIT_GRADE
	 * 
	 * @param grade
	 * @param data
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	@BypassInactiveModule
	public String verificaSituacaoGrade(AacGradeAgendamenConsultas grade, Date data) throws ApplicationBusinessException {
		return getGradeAgendamentoConsultasRN().verificaSituacaoGrade(grade, data);
	}

	@Override
	public Boolean existeSolicitacaoExame(AacConsultas consulta) {
		return getAtualizarConsultaON().existeSolicitacaoExame(consulta);
	}

	@Override
	public Boolean existeAtendimentoEmAndamento(AacConsultas consulta) {
		return getAtualizarConsultaON().existeAtendimentoEmAndamento(consulta);
	}

	@Override
	public void preAtualizar(AacConsultas consulta) throws BaseException {
		getAtualizarConsultaON().preAtualizar(consulta);
	}

	@Override
	public AacConsultas clonarConsulta(AacConsultas consulta) throws ApplicationBusinessException {
		return getConsultasON().clonarConsulta(consulta);
	}

	@Override
	public List<AacProcedHospEspecialidades> listarProcedimentosEspecialidadesPorEspecialidade(Short espSeq) {
		return getAacProcedHospEspecialidadesDAO().listarProcedimentosEspecialidadesPorEspecialidade(espSeq);
	}

	@Override
	public void persistirProcedimentoEspecialidade(AacProcedHospEspecialidades procEsp, DominioOperacoesJournal operacao)
			throws BaseException {
		getConsultasON().persistirProcedimentoEspecialidade(procEsp, operacao);
	}

	@Override
	public Boolean validaGradeAgendamento(AacProcedHospEspecialidades procEsp) throws BaseException {
		return getConsultasRN().validaGradeAgendamento(procEsp);
	}

	@Override
	public void excluirProcedimentoEspecialidade(Short espSeq, Integer phiSeq) {
		getConsultasON().excluirProcedimentoEspecialidade(espSeq, phiSeq);
	}

	@Override
	@BypassInactiveModule
	public List<AacConsultasJn> listaConsultasJn(Integer numeroConsulta, String indSituacaoConsulta) {
		return this.getAacConsultasJnDAO().listaConsultasJn(numeroConsulta, indSituacaoConsulta);
	}

	@Override
	@BypassInactiveModule
	public List<Object[]> listarConsultasPacientesParaPassivarProntuario(Integer pacCodigo, Calendar dthr) {
		return this.getAacConsultasDAO().iberarConsultasPacientesParaPassivarProntuario(pacCodigo, dthr);
	}

	@Override
	@BypassInactiveModule
	public List<AacConsultas> pesquisaConsultasPorPacienteDataConsulta(AipPacientes paciente, Date dataUltimaConsulta) {
		return this.getAacConsultasDAO().pesquisaConsultasPorPacienteDataConsulta(paciente, dataUltimaConsulta);
	}

	@Override
	public List<AacMotivos> pesquisa(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short codigo,
			String descricao, DominioSituacao situacao) {
		return getAacMotivosDAO().pesquisa(firstResult, maxResults, orderProperty, asc, codigo, descricao, situacao);
	}

	@Override
	public AacMotivos obterMotivoConsulta(Short aacMotivosCodigo) {
		return getAacMotivosDAO().obterMotivoConsulta(aacMotivosCodigo);
	}

	@Override
	public void persistirMotivoConsulta(AacMotivos aacMotivos) throws ApplicationBusinessException {
		this.getManterMotivoConsultaRN().persistirMotivoConsulta(aacMotivos);
	}

	@Override
	public void atualizarMotivoConsulta(AacMotivos aacMotivos) throws ApplicationBusinessException {
		this.getManterMotivoConsultaRN().atualizarMotivoConsulta(aacMotivos);
	}

	@Override
	public void removerMotivoConsulta(Short accMotivoConsultaCodigo) throws ApplicationBusinessException {
		this.getManterMotivoConsultaRN().remover(accMotivoConsultaCodigo);
	}

	@Override
	public Long pesquisaCount(Short codigoPesquisaMotivoConsulta, String descricaoPesquisaMotivoConsulta,
			DominioSituacao situacaoPesquisaMotivoConsulta) {
		return getAacMotivosDAO().pesquisaCount(codigoPesquisaMotivoConsulta, descricaoPesquisaMotivoConsulta,
				situacaoPesquisaMotivoConsulta);
	}

	@Override
	public List<AacAtendimentoApacs> listarAtendimentosApacsPorCodigoPaciente(Integer pacCodigo) {
		return this.getAacAtendimentoApacsDAO().listarAtendimentosApacsPorCodigoPaciente(pacCodigo);
	}

	@Override
	public void persistirAtendimentoApacs(AacAtendimentoApacs apac) {
		this.getAacAtendimentoApacsDAO().persistir(apac);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * listarConsultasPorComNumeroDescricaoProcedimentoCount(java.lang.Object)
	 */
	@Override
	public Long listarConsultasPorComNumeroDescricaoProcedimentoCount(final Object objPesquisa) {
		return getAacConsultaProcedHospitalarDAO().listarConsultasPorComNumeroDescricaoProcedimentoCount(objPesquisa);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * listarConsultasPorComNumeroDescricaoProcedimento(java.lang.Object)
	 */
	@Override
	public List<AacConsultaProcedHospitalar> listarConsultasPorComNumeroDescricaoProcedimento(final Object objPesquisa) {
		return getAacConsultaProcedHospitalarDAO().listarConsultasPorComNumeroDescricaoProcedimento(objPesquisa);
	}

	@Override
	public AacConsultaProcedHospitalar obterConsultaProcedHospitalar(AacConsultaProcedHospitalarId pk) {
		return this.getAacConsultaProcedHospitalarDAO().obterPorChavePrimaria(pk);
	}

	@Override
	public Integer obterPhiSeqPorNumeroConsulta(final Integer conNumero, final Boolean indConsulta) {
		return this.getAacConsultaProcedHospitalarDAO().obterPhiSeqPorNumeroConsulta(conNumero, indConsulta);
	}
	
	@Override
	public AacConsultaProcedHospitalar obterConsultaProcedGHospPorNumeroEInd(final Integer conNumero, final Boolean indConsulta) {
		return this.getAacConsultaProcedHospitalarDAO().obterConsultaProcedGHospPorNumeroEInd(conNumero, indConsulta);
	}

	@Override
	@BypassInactiveModule
	public List<Object[]> listarAtendimentosPacienteAmbulatorioPorCodigo(Integer pacCodigo, Date inicio, Date fim) {
		return this.getAacConsultasDAO().listarAtendimentosPacienteAmbulatorioPorCodigo(pacCodigo, inicio, fim);
	}

	@Override
	public List<FatConsultaPrhVO> buscarFatConsultaPrhVOAcompanhamento(final Integer codPaciente, final Long numeroAtm,
			final Date dtInicio, final Date dtFim) {
		return this.getAacConsultasDAO().buscarFatConsultaPrhVOAcompanhamento(codPaciente, numeroAtm, dtInicio, dtFim);
	}

	@Override
	public List<FatConsultaPrhVO> buscarFatConsultaPrhVO(final Integer codPaciente, final Long numeroAtm, final Integer codigoTratamento,
			final Date dtInicio, final Date dtFim) {
		return this.getAacConsultasDAO().buscarFatConsultaPrhVO(codPaciente, numeroAtm, codigoTratamento, dtInicio, dtFim);
	}

	@Override
	public List<Integer> obterNumeroDasConsultas(final Integer pacCodigo, final Date dtTransplante, final Date dtInicio, final Date dtFim,
			final String ttrCodigo, final DominioIndAbsenteismo absenteismo) {
		return this.getAacConsultasDAO().obterNumeroDasConsultas(pacCodigo, dtTransplante, dtInicio, dtFim, ttrCodigo, absenteismo);
	}

	@Override
	public List<AacConsultas> buscarConsultaPorProcedAmbRealizadoEspecialidade(final Integer codPaciente, final Date dtInicioCompetencia,
			final Date dtInicio, final Date dtFim, final Short cnvCodigo, // P_CONVENIO_SUS_PADRAO
			final Integer codigoTratamento, final Byte cpeMes, final Short cpeAno) {
		return this.getAacConsultasDAO().buscarConsultaPorProcedAmbRealizadoEspecialidade(codPaciente, dtInicioCompetencia, dtInicio,
				dtFim, cnvCodigo, codigoTratamento, cpeMes, cpeAno);
	}

	@Override
	public List<AacConsultas> buscarApacAssociacao(final Integer codPaciente, final Date dtInicio, final Date dtFim, final Short cnvCodigo, // P_CONVENIO_SUS_PADRAO
			final Integer codigoTratamento) {
		return this.getAacConsultasDAO().buscarApacAssociacao(codPaciente, dtInicio, dtFim, cnvCodigo, codigoTratamento);
	}

	@Override
	@BypassInactiveModule
	public List<Object[]> obterPacientes(Date dtFrom, Date dtTo, Boolean isReprint, String stringSeparator) {
		return this.getAacConsultasDAO().obterPacientes(dtFrom, dtTo, isReprint, stringSeparator);
	}

	@Override
	public void versionarAltaReceituario(MpmAltaSumario altaSumarioNovo, Short antigoAsuSeqp) throws ApplicationBusinessException {
		this.getManterSumarioAltaReceitasON().versionarAltaReceituario(altaSumarioNovo, antigoAsuSeqp);
	}

	@Override
	public void removerReceituario(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		this.getManterSumarioAltaReceitasON().removerReceituario(altaSumario);
	}

	@Override
	@BypassInactiveModule
	public List<Object[]> obterPacConsultas(Date dtFrom, Date dtTo, Boolean isReprint, String stringSeparator) {
		return this.getAacConsultasDAO().obterPacConsultas(dtFrom, dtTo, isReprint, stringSeparator);
	}

	@Override
	public List<AacConsultas> pesquisarAacConsultasPorCodigoEData(Integer pacCodigo, Date data) {
		return this.getAacConsultasDAO().pesquisarAacConsultasPorCodigoEData(pacCodigo, data);
	}

	@Override
	public List<AacConsultas> executarCursorConsulta(Integer cConNumero) throws ApplicationBusinessException {
		return this.getPesquisarPacientesAgendadosON().executarCursorConsulta(cConNumero);

	}

	@Override
	@BypassInactiveModule
	public List<AacConsultas> pesquisarConsultasPorPaciente(AipPacientes paciente) {
		return this.getAacConsultasDAO().pesquisarConsultasPorPaciente(paciente);
	}

	@Override
	@BypassInactiveModule
	public List<AacConsultas> pesquisarConsultasPorPacientePOL(AipPacientes paciente) {
		return this.getAacConsultasDAO().pesquisarConsultasPorPacientePOL(paciente);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarConsultasPorPacienteCount(AipPacientes paciente) {
		return this.getAacConsultasDAO().pesquisarConsultasPorPacienteCount(paciente);
	}

	@Override
	@BypassInactiveModule
	public List<AltasAmbulatoriasPolVO> pesquisarAltasAmbulatoriaisPol(Integer pacCodigo, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return this.getAacConsultasDAO().pesquisarAltasAmbulatoriaisPol(pacCodigo, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public List<AacGradeAgendamenConsultas> executaCursorGetCboExame(Integer conNumero) {
		return getGradeAgendamenConsultasDAO().executaCursorGetCboExame(conNumero);
	}

	@Override
	@BypassInactiveModule
	public List<AacGradeAgendamenConsultas> listarGradesAgendamentoConsultaPorEspecialidade(AghEspecialidades especialidade) {
		return getGradeAgendamenConsultasDAO().listarGradesAgendamentoConsultaPorEspecialidade(especialidade);
	}

	@Override
	public List<AacMotivos> obterListaMotivosAtivos() {
		return getAacMotivosDAO().obterListaMotivosAtivos();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade#
	 * obterPagador(java.lang.Short)
	 */
	@Override
	@Secure("#{s:hasPermission('pagador','pesquisar')}")
	public AacPagador obterPagador(final Short codigo) {
		return getPagadorON().obterPagador(codigo);
	}

	@Override
	public void persistirSisPrenatal(AacSisPrenatal elemento) {
		this.getAacSisPrenatalDAO().persistir(elemento);
	}

	@Override
	public void removerSisPrenatal(AacSisPrenatal elemento) {
		this.getAacSisPrenatalDAO().remover(elemento);
		this.getAacSisPrenatalDAO().flush();
	}

	@Override
	public List<AacSisPrenatal> pesquisarAacSisPrenatal(Integer pacCodigo) {
		return this.getAacSisPrenatalDAO().pesquisarAacSisPrenatal(pacCodigo);
	}

	@Override
	public AacUnidFuncionalSalas obterUnidFuncionalSalasPeloId(final Short unfSeq, final Byte sala) {
		return getAacUnidFuncionalSalasDAO().obterUnidFuncionalSalasPeloId(unfSeq, sala);
	}

	@Override
	public List<MamAlergias> pesquisarMamAlergiasPorPaciente(Integer pacCodigo) {
		return getMamAlergiasDAO().pesquisarMamAlergiasPorPaciente(pacCodigo);
	}

	@Override
	public List<MamAreaAtuacao> listarAreasAtuacaoPorCodigoLogradouroESituacao(Integer codigoLogradouro, DominioSituacao situacao) {
		return this.getMamAreaAtuacaoDAO().listarAreasAtuacaoPorCodigoLogradouroESituacao(codigoLogradouro, situacao);
	}

	@Override
	public List<MamAreaAtuacao> listarAreasAtuacaoPorDescricaoLogradouroESituacao(String descricaoLogradouro, DominioSituacao situacao) {
		return this.getMamAreaAtuacaoDAO().listarAreasAtuacaoPorDescricaoLogradouroESituacao(descricaoLogradouro, situacao);
	}

	@Override
	public MamAreaAtuacao obterAreaAtuacaoAtivaPorNomeLogradouro(String logradouro) {
		return this.getMamAreaAtuacaoDAO().obterAreaAtuacaoAtivaPorNomeLogradouro(logradouro);
	}

	@BypassInactiveModule
	@Override
	public MamAreaAtuacao obterAreaAtuacaoAtivaPorCodigoLogradouro(Integer codigoLogradouro) {
		return this.getMamAreaAtuacaoDAO().obterAreaAtuacaoAtivaPorCodigoLogradouro(codigoLogradouro);
	}

	@Override
	public List<MamAtestados> listarAtestadosPorCodigoPaciente(Integer pacCodigo) {
		return getMamAtestadosDAO().listarAtestadosPorCodigoPaciente(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<MamEmgEspecialidades> listarTodasMamEmgEspecialidade() {
		return getMamEmgEspecialidadesDAO().listarTodos();
	}

	@Override
	public String getPrimeiraDescricaoLocal(Short seq) {
		return getMamEmgLocaisDAO().getPrimeiraDescricaoLocal(seq);
	}

	@Override
	@BypassInactiveModule
	public MamNotaAdicionalEvolucoes buscarNotaParaRelatorio(Integer seqNota) {
		return getMamNotaAdicionalEvolucoesDAO().buscarNotaParaRelatorio(seqNota);
	}

	@Override
	@BypassInactiveModule
	public MamNotaAdicionalEvolucoes obterNotaAdicionalEvolucoesPorChavePrimaria(Integer seq) {
		return getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public MamNotaAdicionalEvolucoes obterNotaAdicionalEvolucoesOriginal(Integer seq) {
		return getMamNotaAdicionalEvolucoesDAO().obterOriginal(seq);
	}

	@Override
	public List<MamProcedimento> pesquisarProcedimentosComProcedEspecialDiverso(Short seq) {
		return getMamProcedimentoDAO().pesquisarProcedimentosComProcedEspecialDiverso(seq);
	}

	/**
	 * Verifica se existe dados a serem impressos no Relatorio EvoluÃ§Ãµes.
	 * 
	 * @param atdSeq
	 *            - Código do atendimento.
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public Boolean existeDadosImprimirRelatorioEvolucao(Integer atdSeq) {
		return getRelatorioAnaEvoInternacaoRN().existeDadosImprimirRelatorioEvolucoes(atdSeq);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade#verificarCustomizacaoRespondida(java.lang.Character, java.lang.Integer, java.lang.Short,
	 *  java.lang.Long)
	 */
	@Override
	public Boolean verificarCustomizacaoRespondida(Character tipo, Integer qutSeq, Short seqp, Long chave) {

		return getEvolucaoNegarRN().verificarCustomizacaoRespondida(tipo, qutSeq, seqp, chave);
	}

	protected RelatorioAnaEvoInternacaoRN getRelatorioAnaEvoInternacaoRN() {
		return relatorioAnaEvoInternacaoRN;
	}

	@Override
	public List<MamRespostaAnamneses> listarRespostasAnamnesesPip(Integer pipPacCodigo, Short pipSeqp) {
		return getMamRespostaAnamnesesDAO().listarRespostasAnamnesesPip(pipPacCodigo, pipSeqp);
	}

	@Override
	public void atualizarAnamnese(MamRespostaAnamneses anamnese) {
		getMamRespostaAnamnesesDAO().atualizar(anamnese);

	}

	@Override
	public List<MamRespostaAnamneses> listarRespostasAnamnesesPdp(Integer pdpPacCodigo, Short pdpSeqp) {
		return getMamRespostaAnamnesesDAO().listarRespostasAnamnesesPdp(pdpPacCodigo, pdpSeqp);
	}

	@Override
	public List<MamRespostaAnamneses> listarRespostasAnamnesesPlp(Integer plpPacCodigo, Short plpSeqp) {
		return getMamRespostaAnamnesesDAO().listarRespostasAnamnesesPlp(plpPacCodigo, plpSeqp);
	}

	@Override
	public List<MamRespostaAnamneses> listarRespostasAnamnesesPorPepPacCodigo(Integer pepPacCodigo) {
		return getMamRespostaAnamnesesDAO().listarRespostasAnamnesesPorPepPacCodigo(pepPacCodigo);
	}

	@Override
	public List<MamRespostaAnamneses> listarRespostasAnamnesesPorPepPacCodigoEPepCriadoEm(Integer pepPacCodigo, Date pepCriadoEm) {
		return getMamRespostaAnamnesesDAO().listarRespostasAnamnesesPorPepPacCodigoEPepCriadoEm(pepPacCodigo, pepCriadoEm);
	}

	@Override
	public List<MamRespostaAnamneses> listarRespostasAnamnesesPorAtpPacCodigo(Integer atpPacCodigo) {
		return getMamRespostaAnamnesesDAO().listarRespostasAnamnesesPorAtpPacCodigo(atpPacCodigo);
	}

	@Override
	public List<MamRespostaAnamneses> listarRespostasAnamnesesPorAtpPacCodigoEAtpCriadoEm(Integer atpPacCodigo, Date atpCriadoEm) {
		return getMamRespostaAnamnesesDAO().listarRespostasAnamnesesPorAtpPacCodigoEAtpCriadoEm(atpPacCodigo, atpCriadoEm);
	}

	@Override
	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPip(Integer pipPacCodigo, Short pipSeqp) {
		return getMamRespostaEvolucoesDAO().listarRespostasEvolucoesPip(pipPacCodigo, pipSeqp);
	}

	@Override
	public void atualizarRespostaEvolucao(MamRespostaEvolucoes evolucao) throws ApplicationBusinessException {
		this.mamRespostaEvolucoesRN.atualizar(evolucao);
	}

	@Override
	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPdp(Integer pdpPacCodigo, Short pdpSeqp) {
		return getMamRespostaEvolucoesDAO().listarRespostasEvolucoesPdp(pdpPacCodigo, pdpSeqp);
	}

	@Override
	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPlp(Integer plpPacCodigo, Short plpSeqp) {
		return getMamRespostaEvolucoesDAO().listarRespostasEvolucoesPlp(plpPacCodigo, plpSeqp);
	}

	@Override
	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPorAtpPacCodigo(Integer atpPacCodigo) {
		return getMamRespostaEvolucoesDAO().listarRespostasEvolucoesPorAtpPacCodigo(atpPacCodigo);
	}

	@Override
	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPorAtpPacCodigoEAtpCriadoEm(Integer atpPacCodigo, Date atpCriadoEm) {
		return getMamRespostaEvolucoesDAO().listarRespostasEvolucoesPorAtpPacCodigoEAtpCriadoEm(atpPacCodigo, atpCriadoEm);
	}

	@Override
	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPorPepPacCodigo(Integer pepPacCodigo) {
		return getMamRespostaEvolucoesDAO().listarRespostasEvolucoesPorPepPacCodigo(pepPacCodigo);
	}

	@Override
	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPorPepPacCodigoEPepCriadoEm(Integer pepPacCodigo, Date pepCriadoEm) {
		return getMamRespostaEvolucoesDAO().listarRespostasEvolucoesPorPepPacCodigoEPepCriadoEm(pepPacCodigo, pepCriadoEm);
	}

	@Override
	public List<MamTmpAlturas> listaTmpAlturasPorPacCodigo(Integer pacCodigo) {
		return getMamTmpAlturasDAO().listaTmpAlturasPorPacCodigo(pacCodigo);
	}

	@Override
	public void atualizarTmpAltura(MamTmpAlturas altura) {
		getMamTmpAlturasDAO().atualizar(altura);

	}

	@Override
	public List<MamTmpPaDiastolicas> listaTmpPaDiastolicasPorPacCodigo(Integer pacCodigo) {
		return getMamTmpPaDiastolicasDAO().listaTmpPaDiastolicasPorPacCodigo(pacCodigo);
	}

	@Override
	public void atualizarTmpPaDiastolicas(MamTmpPaDiastolicas diastolica) {
		getMamTmpPaDiastolicasDAO().atualizar(diastolica);
	}

	@Override
	public List<MamTmpPaSistolicas> listaTmpPaSistolicasPorPacCodigo(Integer pacCodigo) {
		return getMamTmpPaSistolicasDAO().listaTmpPaSistolicasPorPacCodigo(pacCodigo);
	}

	@Override
	public void atualizarTmpPaSistolicas(MamTmpPaSistolicas sistolica) {
		getMamTmpPaSistolicasDAO().atualizar(sistolica);
		getMamTmpPaSistolicasDAO().flush();
	}

	@Override
	public List<MamTmpPerimCefalicos> listaTmpPerimCefalicosPorPacCodigo(Integer pacCodigo) {
		return getMamTmpPerimCefalicosDAO().listaTmpPerimCefalicosPorPacCodigo(pacCodigo);
	}

	@Override
	public void atualizarTmpPerimCefalicos(MamTmpPerimCefalicos cefalico) {
		getMamTmpPerimCefalicosDAO().atualizar(cefalico);

	}

	@Override
	public List<MamTmpPesos> listaTmpPesosPorPacCodigo(Integer pacCodigo) {
		return getMamTmpPesosDAO().listaTmpPesosPorPacCodigo(pacCodigo);
	}

	@Override
	public void atualizarTmpPeso(MamTmpPesos peso) {
		getMamTmpPesosDAO().atualizar(peso);

	}

	@Override
	public List<TriagemRealizadaEmergenciaVO> listarTriagemRealizadaEmergencia(Date vDtHrInicio, Date vDtHrFim) {
		return getMamTrgEncAmbulatoriaisDAO().listarTriagemRealizadaEmergencia(vDtHrInicio, vDtHrFim);
	}

	// usado no estoque
	@Override
	@BypassInactiveModule
	public List<Object[]> listarAtendimentosPacienteTriagemPorCodigo(Integer pacCodigo) {
		return getMamTriagensDAO().listarAtendimentosPacienteTriagemPorCodigo(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public void atualizarAltaSumerio(MamAltaSumario mamAltaSumario) {
		this.getMamAltasSumarioDAO().atualizar(mamAltaSumario);
	}

	@Override
	public List<MamAnamneses> listarAnamnesesPorCodigoPaciente(Integer pacCodigo) {
		return getMamAnamnesesDAO().listarAnamnesesPorCodigoPaciente(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<MamDiagnostico> pesquisarDiagnosticosPorPaciente(AipPacientes paciente) {
		return getDiagnosticoDAO().pesquisarDiagnosticosPorPaciente(paciente);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarDiagnosticosPorPacienteCount(AipPacientes paciente) {
		return getDiagnosticoDAO().pesquisarDiagnosticosPorPacienteCount(paciente);
	}

	@Override
	@BypassInactiveModule
	public List<MamDiagnostico> listarDiagnosticoValidadoPorAtendimento(Integer atdSeq) {
		return getDiagnosticoDAO().listarDiagnosticoValidadoPorAtendimento(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public List<MamDiagnostico> listarDiagnosticosPorFatRelDiagnosticoEPaciente(EpeFatRelDiagnosticoId id, Integer pacCodigo) {
		return getDiagnosticoDAO().listarDiagnosticosPorFatRelDiagnosticoEPaciente(id, pacCodigo);
	}

	@Override
	public List<SumarioAltaDiagnosticosCidVO> pesquisarSumarioAltaDiagnosticosCidVO(Integer pacCodigo) {
		return getDiagnosticoDAO().pesquisarSumarioAltaDiagnosticosCidVO(pacCodigo);
	}

	@Override
	public List<MamEvolucoes> listarEvolucoesPorCodigoPaciente(Integer pacCodigo) {
		return this.getMamEvolucoesDAO().listarEvolucoesPorCodigoPaciente(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<MamInterconsultas> pesquisarConsultoriasAmbulatoriais(Integer codigoPaciente) {
		return getMamInterconsultasDAO().pesquisarConsultoriasAmbulatoriais(codigoPaciente);
	}

	@Override
	public void removerItemReceituario(final MamItemReceituario itemReceituario) throws ApplicationBusinessException {
		this.getManterItemReceituarioRN().removerItemReceituario(itemReceituario);
	}

	/**
	 * Verifica se existe dados a serem impressos no Relatorio Anamneses.
	 * 
	 * @param atdSeq
	 *            - Código do atendimento.
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public Boolean existeDadosImprimirRelatorioAnamneses(Integer atdSeq) {
		return getRelatorioAnaEvoInternacaoRN().existeDadosImprimirRelatorioAnamneses(atdSeq);
	}

	@Override
	public List<Object[]> buscarConfiguracaoImpressaoItensReceituario(MamReceituarios receituarios) {
		return getMamItemReceituarioDAO().buscarConfiguracaoImpressaoItensReceituario(receituarios);
	}

	@Override
	public List<ItemReceitaMedicaVO> obterItemReceituarioPorImpressaoValidade(MamReceituarios receituario, Byte grupoImpressao,
			Byte validadeMeses) {
		return this.getMamItemReceituarioDAO().obterItemReceituarioPorImpressaoValidade(receituario, grupoImpressao, validadeMeses);
	}

	/**
	 * ExclusÃ£o de receita
	 * 
	 * @param receituarios
	 */
	@Override
	public void excluir(final MamReceituarios receituarios) throws BaseException {
		this.getManterSumarioAltaReceitasON().excluir(receituarios);
	}

	@Override
	public void excluirReceituario(final MamReceituarios receituarios) throws BaseException {
		this.getManterSumarioAltaReceitasON().excluirReceituario(receituarios);
	}

	/**
	 * Faz prÃ© validaÃ§Ã£o do objeto MamItemReceituario
	 * 
	 * @param item
	 * @throws BaseException
	 */
	@Override
	public void preValidar(final MamItemReceituario item, final String operacao) throws BaseException {
		this.getManterSumarioAltaReceitasON().preValidar(item, operacao);
	}

	/**
	 * Verifica validade de item do receituario conforme regras da anvisa.
	 * 
	 * @param item
	 * @throws BaseException
	 */
	@Override
	public void validaValidadeItemReceitaEmMeses(final MamItemReceituario item) throws BaseException {
		this.getManterSumarioAltaReceitasON().validaValidadeEmMeses(item);
	}

	/**
	 * Grava alteraÃ§Ãµes no Receituario criando, alterando ou excluido itens
	 * associados.
	 * 
	 * @see ManterSumarioAltaReceitasON#gravar(MamReceituarios, List, List,
	 *      List)
	 * @throws CloneNotSupportedException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public void gravarMamReceituario(final MamReceituarios receituario, final List<MamItemReceituario> novos,
			final List<MamItemReceituario> alterados, final List<MamItemReceituario> excluidos) throws BaseException,
			CloneNotSupportedException {
		this.getManterSumarioAltaReceitasON().gravar(receituario, novos, alterados, excluidos);
	}

	@Override
	public void inserirItem(final MamReceituarios receituario, final MamItemReceituario item) throws ApplicationBusinessException {
		getManterSumarioAltaReceitasON().inserirItem(receituario, item);
	}

	@Override
	public void atualizarItem(final MamItemReceituario item) throws ApplicationBusinessException {
		getManterSumarioAltaReceitasON().atualizarItem(item);
	}

	@Override
	public void excluirItem(final MamItemReceituario item) throws ApplicationBusinessException {
		getManterSumarioAltaReceitasON().excluir(item);
	}

	/**
	 * Grava inclusÃ£o de Receituarios criando itens associados.
	 * 
	 * @see ManterSumarioAltaReceitasON()#gravar(MamReceituarios, List)
	 * @param dieta
	 * @param novos
	 */
	@Override
	public void gravar(final MamReceituarios receituario, final List<MamItemReceituario> novos) throws BaseException {
		this.getManterSumarioAltaReceitasON().gravar(receituario, novos);
	}

	/**
	 * Obtem Receituario pela chave primÃ¡ria
	 * 
	 * @param atdSeq
	 * @return
	 */
	@Override
	public MamReceituarios obterReceituarioPeloSeq(final Long atdSeq) {
		return this.getManterSumarioAltaReceitasON().obterReceituarioPeloSeq(atdSeq);
	}

	/**
	 * Obtem Receituario
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	@Override
	public MamReceituarios obterMamReceituario(final Integer altanAtdSeq, final Integer altanApaSeq, final Short altanAsuSeqp,
			final DominioTipoReceituario tipo) {
		return this.getManterSumarioAltaReceitasON().obterMamReceituario(altanAtdSeq, altanApaSeq, altanAsuSeqp, tipo);
	}

	@Override
	public List<MamItemReceituario> buscarItensReceita(final MpmAltaSumario altaSumario, final DominioTipoReceituario tipo) {
		return this.getManterSumarioAltaReceitasON().buscarItensReceita(altaSumario, tipo);

	}

	@Override
	public List<MamItemReceituario> buscarItensReceita(final MamReceituarios receituario) {
		return getManterSumarioAltaReceitasON().buscarItensReceita(receituario);
	}

	@Override
	public List<MamItemReceituario> obterItensReceitaOrdenadoPorSeqp(final MamReceituarios receituario) {
		return getManterSumarioAltaReceitasON().obterItensReceitaOrdenadoPorSeqp(receituario);
	}

	@Override
	public void inserir(final MamReceituarios receituario) throws BaseException {
		getManterSumarioAltaReceitasON().inserir(receituario);
	}

	@Override
	public MamNotaAdicionalAnamneses obterNotaAdicionalAnamnesePorChavePrimaria(Integer seqAna) {
		return getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(seqAna);
	}

	/**
	 * ObtÃ©m todas as notas adicionais que faÃ§am parte do registro indicado e
	 * estejam pendentes ou a exclusÃ£o nÃ£o tenha sido validada.
	 * 
	 * @param numRegistro
	 * @return
	 * @author bruno.mourao
	 * @since 17/05/2012
	 */
	@Override
	@BypassInactiveModule
	public List<MamNotaAdicionalAnamneses> pesquisarNotaAdicionalAnamnesesPendenteExcNaoValidParaInternacao(Long numRegistro) {
		return getMamNotaAdicionalAnamnesesDAO().pesquisarNotaAdicionalAnamnesesPendenteExcNaoValidParaInternacao(numRegistro);
	}

	@Override
	@BypassInactiveModule
	public List<AghVersaoDocumento> buscarVersaoSeqDoc(Integer seqNota, DominioTipoDocumento tipoDoc) {
		return getMamNotaAdicionalEvolucoesDAO().buscarVersaoSeqDoc(seqNota, tipoDoc);
	}

	@Override
	@BypassInactiveModule
	public List<AghVersaoDocumento> verificaImprime(Integer seqNota, DominioTipoDocumento tipoDoc) {
		return getMamNotaAdicionalEvolucoesDAO().verificaImprime(seqNota, tipoDoc);
	}

	@Override
	@BypassInactiveModule
	public List<MamNotaAdicionalEvolucoes> pesquisarNotasAdicionaisEvolucoesPorCodigoPaciente(Integer pacCodigo, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return getMamNotaAdicionalEvolucoesDAO().pesquisarNotasAdicionaisEvolucoesPorCodigoPaciente(pacCodigo, firstResult, maxResult,
				orderProperty, asc);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarNotasAdicionaisEvolucoesPorCodigoPacienteCount(Integer codigo) {
		return getMamNotaAdicionalEvolucoesDAO().pesquisarNotasAdicionaisEvolucoesPorCodigoPacienteCount(codigo);
	}

	/**
	 * ObtÃ©m todas as notas adicionais que faÃ§am parte do registro indicado e
	 * estejam pendentes ou a exclusÃ£o nÃ£o tenha sido validada.
	 * 
	 * @param numRegistro
	 * @return
	 * @author bruno.mourao
	 * @since 17/05/2012
	 */
	@Override
	@BypassInactiveModule
	public List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEvolucaoPendenteExcNaoValidParaInternacao(Long numRegistro) {

		return getMamNotaAdicionalEvolucoesDAO().pesquisarNotaAdicionalEvolucaoPendenteExcNaoValidParaInternacao(numRegistro);

	}

	/**
	 * ObtÃ©m todas as notas adicionais que estejam validadas e nÃ£o possuam uma
	 * nota pai que seja pendente, validada, excluida nao validada ou alterada
	 * nao validada
	 * 
	 * @param numRegistro
	 * @return
	 * @author bruno.mourao
	 * @since 17/05/2012
	 */
	@Override
	@BypassInactiveModule
	public List<MamNotaAdicionalEvolucoes> pesquisarNotaAdicionalEvolucaoValidasSemPaiParaInternacao(Long numRegistro) {
		return getMamNotaAdicionalEvolucoesDAO().pesquisarNotaAdicionalEvolucaoValidasSemPaiParaInternacao(numRegistro);
	}

	/**
	 * ObtÃ©m todas as notas adicionais que estejam validadas e nÃ£o possuam uma
	 * nota pai que seja pendente, validada, excluida nao validada ou alterada
	 * nao validada
	 * 
	 * @param numRegistro
	 * @return
	 * @author bruno.mourao
	 * @since 17/05/2012
	 */
	@Override
	@BypassInactiveModule
	public List<MamNotaAdicionalAnamneses> pesquisarNotaAdicionalAnamnesesValidasSemPaiParaInternacao(Long numRegistro) {
		return getMamNotaAdicionalAnamnesesDAO().pesquisarNotaAdicionalAnamnesesValidasSemPaiParaInternacao(numRegistro);
	}

	@Override
	public List<MamNotaAdicionalEvolucoes> listarNotasAdicinaisEvolucoesPorCodigoPaciente(Integer pacCodigo) {
		return getMamNotaAdicionalEvolucoesDAO().listarNotasAdicinaisEvolucoesPorCodigoPaciente(pacCodigo);
	}

	@Override
	public List<MamReceituarios> listarReceituariosPorPaciente(AipPacientes paciente) {
		return this.getMamReceituariosDAO().listarReceituariosPorPaciente(paciente);
	}

	@Override
	@BypassInactiveModule
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<RelatorioAnaEvoInternacaoVO> pesquisarRelatorioAnaEvoInternacao(Integer atdSeq, String tipoRelatorio, Date dataInicial,
			Date dataFinal) throws ApplicationBusinessException {
		return getRelatorioAnaEvoInternacaoBean().pesquisarRelatorioAnaEvoInternacao(atdSeq, tipoRelatorio, dataInicial, dataFinal);
	}

	@Override
	@BypassInactiveModule
	public List<MamConcatenacao> pesquisarConcatenacaoAtivaPorIdQuestao(Integer qusQutSeq, Short qusSeq) {
		return getMamConcatenacaoDAO().pesquisarConcatenacaoAtivaPorIdQuestao(qusQutSeq, qusSeq);
	}

	@Override
	public List<AacAtendimentoApacs> listarAtendimentoApacPorPtrPacCodigo(Integer ptrPacCodigo) {
		return this.getAacAtendimentoApacsDAO().listarAtendimentoApacPorPtrPacCodigo(ptrPacCodigo);
	}

	@Override
	public List<AacAtendimentoApacs> obterDataInicioAtendimentoExistente(Integer pacCodigo, Short espSeq, DominioTipoTratamentoAtendimento indTipoTratamento) {
		return this.getAacAtendimentoApacsDAO().obterDataInicioAtendimentoExistente(pacCodigo, espSeq, indTipoTratamento);
	}

	@Override
	public void persistirAacAtendimentoApacs(AacAtendimentoApacs aacAtendimentoApacs) {
		this.getAacAtendimentoApacsDAO().persistir(aacAtendimentoApacs);
	}

	@Override
	public List<SolicitanteVO> executaCursorSolicitante(Short unfSeq, Byte sala, Date dtConsulta) throws ApplicationBusinessException,
			ApplicationBusinessException {
		return this.getPesquisarPacientesAgendadosON().executaCursorSolicitante(unfSeq, sala, dtConsulta);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.administracao.business.IAdministracaoFacade#
	 * obterListaSalasPorUnidadeFuncional
	 * (br.gov.mec.aghu.model.AghUnidadesFuncionais, java.lang.Object)
	 */
	@Override
	public List<AacUnidFuncionalSalas> obterListaSalasPorUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional, Object param) {
		return getAacUnidFuncionalSalasDAO().obterListaSalasPorUnidadeFuncional(unidadeFuncional, param);
	}

	@Override
	public List<AacUnidFuncionalSalas> obterListaSalasPeloNumeroSala(Object param) {
		return getAacUnidFuncionalSalasDAO().obterListaSalasPeloNumeroSala(param);
	}

	@Override
	public VAacSiglaUnfSala obterVAacSiglaUnfSalaPeloId(final byte sala, final short unfSeq) {
		return getVAacSiglaUnfSalaDAO().obterVAacSiglaUnfSalaPeloId(sala, unfSeq);
	}

	@Override
	public List<MamReceituarios> buscarReceituariosPorAltaSumario(MpmAltaSumario altaSumario) {
		return getMamReceituariosDAO().buscarReceituariosPorAltaSumario(altaSumario);
	}

	@Override
	public List<MamReceituarios> pesquisarReceituariosPorConsulta(AacConsultas consulta) {
		return getMamReceituariosDAO().pesquisarReceituariosPorConsulta(consulta);
	}

	@Override
	public void removerReceituario(final MamReceituarios receituario) throws ApplicationBusinessException {
		this.getManterReceituarioRN().removerReceituario(receituario);
	}

	@Override
	public void atualizarReceituario(final MamReceituarios receituario) throws ApplicationBusinessException {
		this.getManterReceituarioRN().atualizarReceituario(receituario);
	}

	@Override
	public List<ReceitaMedicaVO> imprimirReceita(final MpmAltaSumario altaSumario, final boolean atualizar) throws BaseException {
		return this.getManterReceituarioON().imprimirReceita(altaSumario, atualizar);
	}

	@Override
	public List<ReceitaMedicaVO> imprimirReceitaPorSeq(final Long receitaSeq, final Boolean imprimiu) throws BaseException {
		return this.getManterReceituarioON().imprimirReceita(receitaSeq, imprimiu);
	}

	@Override
	public List<MamReceituarios> buscarReceituariosPorAltaSumarioNaoAssinados(MpmAltaSumario altaSumario, MamReceituarios receita) {
		return getMamReceituariosDAO().buscarReceituariosPorAltaSumarioNaoAssinados(altaSumario, receita);
	}

	@Override
	public AacFormaAgendamento obterAacFormaAgendamentoPorChavePrimaria(AacFormaAgendamentoId id) {
		return getAacFormaAgendamentoDAO().obterPorChavePrimaria(id);
	}

	@Override
	public AacFormaAgendamento obterAacFormaAgendamentoPorChavePrimaria(AacFormaAgendamentoId id, Enum[] innerJoins, Enum[] leftJois) {
		return getAacFormaAgendamentoDAO().obterPorChavePrimaria(id, innerJoins, leftJois);
	}

	@Override
	@BypassInactiveModule
	public List<MamTrgEncInterno> obterPorConsultaOrderDesc(Integer numero) {
		return getMamTrgEncInternoDAO().obterPorConsultaOrderDesc(numero);
	}

	@Override
	public List<MamEvolucoes> obterEvolucaoPorTriagemERegistro(Long trgSeq, Long rgtSeq) {
		return getMamEvolucoesDAO().obterEvolucaoPorTriagemERegistro(trgSeq, rgtSeq);
	}

	@Override
	public List<MamAnamneses> obterAnamnesePorTriagemERegistro(Long trgSeq, Long rgtSeq) {
		return getMamAnamnesesDAO().obterAnamnesePorTriagemERegistro(trgSeq, rgtSeq);
	}

	@Override
	@BypassInactiveModule
	public MamTipoEstadoPaciente obterEstadoAtual(Integer atdSeq, Date dataValidacao) {
		return getMamEstadoPacienteDAO().obterEstadoAtual(atdSeq, dataValidacao);
	}

	// --- Getters --------------------------------------------//

	protected ManterReceituarioON getManterReceituarioON() {
		return manterReceituarioON;
	}

	protected AacSituacaoConsultasDAO getAacSituacaoConsultasDAO() {
		return aacSituacaoConsultasDAO;
	}

	protected AacTipoAgendamentoDAO getAacTipoAgendamentoDAO() {
		return aacTipoAgendamentoDAO;
	}

	protected ManterGradeAgendamentoRN getManterGradeAgendamentoRN() {
		return manterGradeAgendamentoRN;
	}

	protected AacGradeAgendamenConsultasDAO getGradeAgendamenConsultasDAO() {
		return aacGradeAgendamenConsultasDAO;
	}

	protected MarcacaoConsultaON getMarcacaoConsultaON() {
		return marcacaoConsultaON;
	}

	protected ConsultaAgendaON getConsultaAgendaON() {
		return consultaAgendaON;
	}
	
	protected EsusConsultasON getEsusConsultasON() {
		return esusConsultasON;
	}

	protected ProcedimentoConsultaON getProcedimentoConsultaON() {
		return procedimentoConsultaON;
	}

	protected AacMotivosDAO getAacMotivosDAO() {
		return aacMotivosDAO;
	}

	protected AacRetornosDAO getAacRetornosDAO() {
		return aacRetornosDAO;
	}

	protected AacConsultaProcedHospitalarDAO getAacConsultaProcedHospitalarDAO() {
		return aacConsultaProcedHospitalarDAO;
	}

	protected DisponibilidadeHorariosON getDisponibilidadeHorariosON() {
		return disponibilidadeHorariosON;
	}

	protected MamSituacaoAtendimentosDAO getMamSituacaoAtendimentosDAO() {
		return mamSituacaoAtendimentosDAO;
	}

	protected AacPagadorDAO getAacPagadorDAO() {
		return aacPagadorDAO;
	}

	protected ManterFormasParaAgendamentoON getManterFormasParaAgendamentoON() {
		return manterFormasParaAgendamentoON;
	}

	protected VAacConvenioPlanoDAO getVAacConvenioPlanoDAO() {
		return vAacConvenioPlanoDAO;
	}

	protected PesquisarPacientesAgendadosON getPesquisarPacientesAgendadosON() {
		return pesquisarPacientesAgendadosON;
	}

	protected AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
		return ambulatorioConsultaRN;
	}

	protected AacNivelBuscaDAO getAacNivelBuscaDAO() {
		return aacNivelBuscaDAO;
	}

	protected AacFormaEspecialidadeDAO getAacFormaEspecialidadeDAO() {
		return aacFormaEspecialidadeDAO;
	}

	protected LiberarConsultasON getLiberarConsultasON() {
		return liberarConsultasON;
	}

	protected AacUnidFuncionalSalasDAO getAacUnidFuncionalSalasDAO() {
		return aacUnidFuncionalSalasDAO;
	}

	protected MarcacaoConsultaRN getMarcacaoConsultaRN() {
		return marcacaoConsultaRN;
	}

	protected ZonaSalaON getZonaSalaON() {
		return zonaSalaON;
	}

	protected CancelamentoAtendimentoRN getCancelamentoAtendimentoRN() {
		return cancelamentoAtendimentoRN;
	}

	protected BloqueioConsultaON getBloqueioConsultaON() {
		return bloqueioConsultaON;
	}

	protected FinalizaAtendimentoON getFinalizaAtendimentoON() {
		return finalizaAtendimentoON;
	}

	protected AacConsultaProcedHospitalarON getAacConsultaProcedHospitalarON() {
		return aacConsultaProcedHospitalarON;
	}

	protected MamNotaAdicionalAnamnesesDAO getMamNotaAdicionalAnamnesesDAO() {
		return mamNotaAdicionalAnamnesesDAO;
	}

	protected MamNotaAdicionalEvolucoesDAO getMamNotaAdicionalEvolucoesDAO() {
		return mamNotaAdicionalEvolucoesDAO;
	}

	protected ProcedimentoAtendimentoConsultaON getProcedimentoAtendimentoConsultaON() {
		return procedimentoAtendimentoConsultaON;
	}

	protected AtendimentoPacientesAgendadosON getAtendimentoPacientesAgendadosON() {
		return atendimentoPacientesAgendadosON;
	}

	protected AgendaSamisON getAgendaSamisON() {
		return agendaSamisON;
	}

	protected VMamProcXCidDAO getVMamProcXCidDAO() {
		return vMamProcXCidDAO;
	}

	protected MamTipoItemEvolucaoDAO getMamTipoItemEvolucaoDAO() {
		return mamTipoItemEvolucaoDAO;
	}

	protected MamReceituariosDAO getMamReceituariosDAO() {
		return mamReceituariosDAO;
	}
	
	protected MamTipoItemAnamnesesDAO getMamTipoItemAnamnesesDAO() {
		return mamTipoItemAnamnesesDAO;
	}

	protected MamEstadoPacienteDAO getMamEstadoPacienteDAO() {
		return mamEstadoPacienteDAO;
	}

	protected MamControlesDAO getMamControlesDAO() {
		return mamControlesDAO;
	}

	protected AmbulatorioRN getAmbulatorioRN() {
		return ambulatorioRN;
	}

	protected DiagnosticoON getDiagnosticoON() {
		return diagnosticoON;
	}

	protected ListarDiagnosticosAtivosPacienteRN getListarDiagnosticosAtivosPacienteRN() {
		return listarDiagnosticosAtivosPacienteRN;
	}

	protected ManterGradeAgendamentoON getManterGradeAgendamentoON() {
		return manterGradeAgendamentoON;
	}

	protected HorarioConsultaON getHorarioConsultaON() {
		return horarioConsultaON;
	}

	protected ArquivoSisregON getArquivoSisregON() {
		return arquivoSisregON;
	}

	protected AacGradeProcedHospitalarDAO getGradeProcedHospitalarDAO() {
		return aacGradeProcedHospitalarDAO;
	}

	protected AacFormaAgendamentoDAO getAacFormaAgendamentoDAO() {
		return aacFormaAgendamentoDAO;
	}

	protected AacHorarioGradeConsultaDAO getAacHorarioGradeConsultaDAO() {
		return aacHorarioGradeConsultaDAO;
	}

	protected AacCaracteristicaGradeDAO getCaracteristicaGradeDAO() {
		return aacCaracteristicaGradeDAO;
	}

	protected AacGradeSituacaoDAO getGradeSituacaoDAO() {
		return aacGradeSituacaoDAO;
	}

	protected VAacSiglaUnfSalaDAO getVAacSiglaUnfSalaDAO() {
		return vAacSiglaUnfSalaDAO;
	}

	protected AacCondicaoAtendimentoDAO getAacCondicaoAtendimentoDAO() {
		return aacCondicaoAtendimentoDAO;
	}

	protected ManterReceituarioRN getManterReceituarioRN() {
		return manterReceituarioRN;
	}

	protected MamTriagensDAO getMamTriagensDAO() {
		return mamTriagensDAO;
	}

	protected MamTrgEncAmbulatoriaisDAO getMamTrgEncAmbulatoriaisDAO() {
		return mamTrgEncAmbulatoriaisDAO;
	}

	protected MamTrgAlergiasDAO getMamTrgAlergiasDAO() {
		return mamTrgAlergiasDAO;
	}

	protected MamTmpPesosDAO getMamTmpPesosDAO() {
		return mamTmpPesosDAO;
	}

	protected MamTmpPerimCefalicosDAO getMamTmpPerimCefalicosDAO() {
		return mamTmpPerimCefalicosDAO;
	}

	protected MamTmpPaSistolicasDAO getMamTmpPaSistolicasDAO() {
		return mamTmpPaSistolicasDAO;
	}

	protected MamTmpPaDiastolicasDAO getMamTmpPaDiastolicasDAO() {
		return mamTmpPaDiastolicasDAO;
	}

	protected MamTmpAlturasDAO getMamTmpAlturasDAO() {
		return mamTmpAlturasDAO;
	}

	protected MamRespostaEvolucoesDAO getMamRespostaEvolucoesDAO() {
		return mamRespostaEvolucoesDAO;
	}

	protected MamRespostaAnamnesesDAO getMamRespostaAnamnesesDAO() {
		return mamRespostaAnamnesesDAO;
	}

	protected MamRegistroDAO getMamRegistroDAO() {
		return mamRegistroDAO;
	}

	protected MamProcedimentoDAO getMamProcedimentoDAO() {
		return mamProcedimentoDAO;
	}

	protected MamEmgLocaisDAO getMamEmgLocaisDAO() {
		return mamEmgLocaisDAO;
	}

	protected MamEmgEspecialidadesDAO getMamEmgEspecialidadesDAO() {
		return mamEmgEspecialidadesDAO;
	}

	protected MamConcatenacaoDAO getMamConcatenacaoDAO() {
		return mamConcatenacaoDAO;
	}

	protected MamAtestadosDAO getMamAtestadosDAO() {
		return mamAtestadosDAO;
	}

	protected MamAreaAtuacaoDAO getMamAreaAtuacaoDAO() {
		return mamAreaAtuacaoDAO;
	}

	protected PagadorON getPagadorON() {
		return pagadorON;
	}

	protected AacSisPrenatalDAO getAacSisPrenatalDAO() {
		return aacSisPrenatalDAO;
	}

	protected MamAlergiasDAO getMamAlergiasDAO() {
		return mamAlergiasDAO;
	}

	protected ManterItemReceituarioRN getManterItemReceituarioRN() {
		return manterItemReceituarioRN;
	}

	protected ManterSumarioAltaReceitasON getManterSumarioAltaReceitasON() {
		return manterSumarioAltaReceitasON;
	}

	protected RelatorioAnaEvoInternacaoBeanLocal getRelatorioAnaEvoInternacaoBean() {
		return (RelatorioAnaEvoInternacaoBeanLocal) relatorioAnaEvoInternacaoBean;
	}

	protected MamAltaDiagnosticosDAO getMamAltaDiagnosticosDAO() {
		return mamAltaDiagnosticosDAO;
	}

	protected MamAltaEvolucoesDAO getMamAltaEvolucoesDAO() {
		return mamAltaEvolucoesDAO;
	}

	protected MamAltaPrescricoesDAO getMamAltaPrescricoesDAO() {
		return mamAltaPrescricoesDAO;
	}

	protected GradeAgendamentoConsultasRN getGradeAgendamentoConsultasRN() {
		return gradeAgendamentoConsultasRN;
	}

	protected ConsultasON getConsultasON() {
		return consultasON;
	}

	protected ConsultasRN getConsultasRN() {
		return consultasRN;
	}

	protected AtualizarConsultaON getAtualizarConsultaON() {
		return atualizarConsultaON;
	}

	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	protected AacConsultasJnDAO getAacConsultasJnDAO() {
		return aacConsultasJnDAO;
	}

	protected MamAnamnesesON getMamAnamnesesON() {
		return mamAnamnesesON;
	}

	protected ExportacaoLogSisregON getExportacaoLogSisregON() {
		return exportacaoLogSisregON;
	}

	protected MarcacaoConsultaSisregON getMarcacaoConsultaSisregON() {
		return marcacaoConsultaSisregON;
	}

	protected ListarDiagnosticosAtivosPacienteON getListarDiagnosticosAtivosPacienteON() {
		return listarDiagnosticosAtivosPacienteON;
	}

	protected ManterCondicaoAtendimentoRN getManterCondicaoAtendimentoRN() {
		return manterCondicaoAtendimentoRN;
	}

	protected AmbulatorioConsultaON getAmbulatorioConsultaON() {
		return ambulatorioConsultaON;
	}

	protected ManterTipoAutorizacaoRN getManterTipoAutorizacaoRN() {
		return manterTipoAutorizacaoRN;
	}

	protected MamDiagnosticoDAO getDiagnosticoDAO() {
		return mamDiagnosticoDAO;
	}

	protected MamAltasSumarioDAO getMamAltasSumarioDAO() {
		return mamAltasSumarioDAO;
	}

	protected AacProcedHospEspecialidadesDAO getAacProcedHospEspecialidadesDAO() {
		return aacProcedHospEspecialidadesDAO;
	}

	protected AacAtendimentoApacsDAO getAacAtendimentoApacsDAO() {
		return aacAtendimentoApacsDAO;
	}

	protected AtendimentoPacientesAgendadosRN getAtendimentoPacientesAgendadosRN() {
		return atendimentoPacientesAgendadosRN;
	}

	protected ProcedimentoAtendimentoConsultaRN getProcedimentoAtendimentoConsultaRN() {
		return procedimentoAtendimentoConsultaRN;
	}

	protected MamAnamnesesDAO getMamAnamnesesDAO() {
		return mamAnamnesesDAO;
	}

	protected MamEvolucoesDAO getMamEvolucoesDAO() {
		return mamEvolucoesDAO;
	}

	protected MamItemAnamnesesDAO getMamItemAnamnesesDAO() {
		return mamItemAnamnesesDAO;
	}

	protected MamItemEvolucoesDAO getMamItemEvolucoesDAO() {
		return mamItemEvolucoesDAO;
	}

	protected MamProcedimentoRealizadoDAO getMamProcedimentoRealizadoDAO() {
		return mamProcedimentoRealizadoDAO;
	}

	protected MamItemReceituarioDAO getMamItemReceituarioDAO() {
		return mamItemReceituarioDAO;
	}

	protected MamInterconsultasDAO getMamInterconsultasDAO() {
		return mamInterconsultasDAO;
	}

	protected GerarDiariasProntuariosSamisON getGerarDiariasProntuariosSamisON() {
		return gerarDiariasProntuariosSamisON;
	}

	protected ItemExameON getItemExameON() {
		return itemExameON;
	}

	protected ItemMedicacaoON getItemMedicacaoON() {
		return itemMedicacaoON;
	}

	protected LaudoAihON getLaudoAihON() {
		return laudoAihON;
	}

	protected TrgEncInternoON getTrgEncInternoON() {
		return trgEncInternoON;
	}

	protected RelatorioAnaEvoEmergenciaON getRelatorioAnaEvoEmergenciaON() {
		return relatorioAnaEvoEmergenciaON;
	}

	protected MamTrgEncInternoDAO getMamTrgEncInternoDAO() {
		return mamTrgEncInternoDAO;
	}

	protected MamLaudoAihDAO getMamLaudoAihDAO() {
		return mamLaudoAihDAO;
	}

	protected AacConsultasRN getAacConsultasRN() {
		return aacConsultasRN;
	}

	@Override
	public List<AacConsultas> verificarSePacienteTemConsulta(Integer pacCodigo, Integer numDiasPassado, Integer numDiasFuturo, Integer paramReteronoConsAgendada) {
		return getAacConsultasDAO().verificarSePacienteTemConsulta(pacCodigo, numDiasPassado, numDiasFuturo,paramReteronoConsAgendada);
	}

	@Override
	@BypassInactiveModule
	public String mpmcMinusculo(String pString, Integer pTipo) {
		return getRelatorioAnaEvoInternacaoRN().mpmcMinusculo(pString, pTipo);
	}

	@Override
	@BypassInactiveModule
	public List<RelatorioAnaEvoInternacaoVO> pesquisarRelatorioAnaEvoEmergencia(Long trgSeq, Date dtHrValidaInicio, Date dtHrValidaFim,
			DominioGrupoProfissionalAnamnese grupoProssional, Integer conNumero, CseCategoriaProfissional categoriaProfissional)
			throws ApplicationBusinessException {
		return getRelatorioAnaEvoEmergenciaON().pesquisarRelatorioAnaEvoEmergencia(trgSeq, dtHrValidaInicio, dtHrValidaFim,
				grupoProssional, conNumero, categoriaProfissional);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarAltasAmbulatoriaisPolCount(Integer pacCodigo) {
		return this.getAacConsultasDAO().pesquisarAltasAmbulatoriaisPolCount(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<Long> buscaSeqTipoItemEvolucao(final Integer atdSeq, final Date data, final Integer seqMTIE, final Integer catSeq) {
		return getMamTipoItemEvolucaoDAO().buscaSeqTipoItemEvolucao(atdSeq, data, seqMTIE, catSeq);
	}

	@Override
	@BypassInactiveModule
	public String obterIdadeMesDias(Date dtNascimento, Date dtReferencia) {
		return getRelatorioAnaEvoInternacaoRN().obterIdadeMesDias(dtNascimento, dtReferencia);
	}

	@Override
	public void validaCRMAmbulatorio(AghProfEspecialidades prof, AacGradeAgendamenConsultas grade) throws ApplicationBusinessException {
		getManterGradeAgendamentoON().validaCRMAmbulatorio(prof, grade);
	}

	@Override
	@BypassInactiveModule
	public List<MamAltaSumario> pesquisarMamAltaSumarioDtValidaNullByConsulta(Integer conNumero,
			DominioIndPendenteDiagnosticos... indPendentes) {
		return getMamAltasSumarioDAO().pesquisarMamAltaSumarioDtValidaNullByConsulta(conNumero, indPendentes);
	}

	@Override
	@BypassInactiveModule
	public List<MamAltaSumario> pesquisarMamAltaSumarioDtValidaNullAndAlsSeqNull(Integer conNumero,
			DominioIndPendenteDiagnosticos indPendenteDiag) {
		return getMamAltasSumarioDAO().pesquisarMamAltaSumarioDtValidaNullAndAlsSeqNull(conNumero, indPendenteDiag);
	}

	public void atualizarNotaAdicionalAnamneses(MamNotaAdicionalAnamneses notaAdicionalAnamneses,
			MamNotaAdicionalAnamneses notaAdicionalAnamnesesOld) throws ApplicationBusinessException {
		this.getMarcacaoConsultaRN().atualizarNotaAdicionalAnamneses(notaAdicionalAnamneses, notaAdicionalAnamnesesOld);
	}

	public MamAnamneses atualizarAnamnese(MamAnamneses anamnese) throws ApplicationBusinessException {
		return this.getMarcacaoConsultaRN().atualizarAnamnese(anamnese);
	}

	public MamEvolucoes atualizarEvolucao(MamEvolucoes evolucao) throws ApplicationBusinessException {
		return this.getMarcacaoConsultaRN().atualizarEvolucao(evolucao);
	}

	public void atualizarLogEmUso(MamLogEmUsos logEmUso) {
		this.getMarcacaoConsultaRN().atualizarLogEmUso(logEmUso);
	}

	public List<AacConsultas> listarConsultasPorCodigoPaciente(Integer pacCodigo) {
		return this.getAacConsultasDAO().listarConsultasPorCodigoPaciente(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public String formataString(String nome, int i) {
		return MarcacaoConsultaSisregUtil.formataString(nome, i);
	}

	@Override
	public AacConsultas obterAacConsulta(Integer numeroConsulta) {
		return getAacConsultasDAO().obterConsulta(numeroConsulta);
	}

	@Override
	@BypassInactiveModule
	public MamRegistro obterMamRegistroPorChavePrimaria(Long rgtSeq) {
		return getMamRegistroDAO().obterPorChavePrimaria(rgtSeq);
	}

	@Override
	public Boolean pacienteEmAtendimentoEmergenciaTerreo(Integer pacCodigo) {
		return getMamTriagensDAO().pacienteEmAtendimentoEmergenciaTerreo(pacCodigo);
	}

	@Override
	public Boolean pacienteEmAtendimentoEmergenciaUltimosDias(Integer pacCodigo, Integer dias) {
		return getMamTriagensDAO().pacienteEmAtendimentoEmergenciaUltimosDias(pacCodigo, dias);
	}

	public void existeGradeAgendamentoConsultaComEquipe(AghEquipes equipe) throws BaseException {
		getManterGradeAgendamentoON().existeGradeAgendamentoConsultaComEquipe(equipe);
	}

	@Override
	@BypassInactiveModule
	public List<AacConsultas> obterConsultaAnamnesesPorDataConsPacEspIndPendente(final Date dataCorrente, final Integer paciente,
			final Short especiliada, final DominioIndPendenteAmbulatorio indPendente) {
		return getAacConsultasDAO().obterConsultaAnamnesesPorDataConsPacEspIndPendente(dataCorrente, paciente, especiliada, indPendente);
	}

	@Override
	@BypassInactiveModule
	public List<AacConsultas> obterConsultaEvolucoesPorDataConsPacEspIndPendente(final Date dataConsulta, final Integer paciente,
			final Short especiliada, final DominioIndPendenteAmbulatorio indPendente) {
		return getAacConsultasDAO().obterConsultaEvolucoesPorDataConsPacEspIndPendente(dataConsulta, paciente, especiliada, indPendente);
	}

	@Override
	@BypassInactiveModule
	public List<VFatSsmInternacaoVO> obterListaProcedimentosLaudoAih(Object param, AipPacientes paciente, Integer cidSeq)
			throws ApplicationBusinessException {
		return getLaudoAihON().obterListaVFatSssInternacao(param, paciente, cidSeq);
	}

	@Override
	@BypassInactiveModule
	public Long obterListaProcedimentosLaudoAihCount(Object param, AipPacientes paciente, Integer cidSeq)
			throws ApplicationBusinessException {
		return getLaudoAihON().obterListaVFatSssInternacaoCount(param, paciente, cidSeq);
	}

	@Override
	@BypassInactiveModule
	public List<AghCid> obterListaCidLaudoAih(Object param, AipPacientes paciente, Long codTabela) throws ApplicationBusinessException {
		return getLaudoAihON().obterListaCidLaudoAih(param, paciente, codTabela);
	}

	@Override
	@BypassInactiveModule
	public Long obterListaCidLaudoAihCount(Object param, AipPacientes paciente, Long codTabela) throws ApplicationBusinessException {
		return getLaudoAihON().obterListaCidLaudoAihCount(param, paciente, codTabela);
	}

	@Override
	public List<AghCid> obterListaCidSecundarioLaudoAih(Object param, AipPacientes paciente, String cidCodigo)
			throws ApplicationBusinessException {
		return this.getLaudoAihON().obterListaCidSecundarioLaudoAih(param, paciente, cidCodigo);
	}

	@Override
	public Long obterListaCidSecundarioLaudoAihCount(Object param, AipPacientes paciente, String cidCodigo)
			throws ApplicationBusinessException {
		return this.getLaudoAihON().obterListaCidSecundarioLaudoAihCount(param, paciente, cidCodigo);
	}

	@Override
	public void protegerLiberarRegistroLai(Long laiSeq, DominioIndPendenteLaudoAih indPendente, RapServidores servidorValida)
			throws ApplicationBusinessException {
		this.getLaudoAihON().protegerLiberarRegistroLai(laiSeq, indPendente, servidorValida);

	}

	@Override
	@BypassInactiveModule
	// @Restrict("#{s:hasPermission('manterLaudoAih','executar')}")
	public Boolean salvarMamLaudoAih(MamLaudoAih laudoAih, AipPacientes paciente) throws BaseException {
		return this.getLaudoAihON().salvar(laudoAih, paciente);
	}

	@Override
	@BypassInactiveModule
	// @Restrict("#{s:hasPermission('manterLaudoAih','executar')}")
	public Boolean atualizarMamLaudoAih(MamLaudoAih laudoAih, AipPacientes paciente) throws BaseException {
		return this.getLaudoAihON().atualizar(laudoAih, paciente);
	}

	@Override
	public List<MamLaudoAih> obterLaudoPorSeqEPaciente(Long seq, Integer pacCodigo) {
		return this.mamLaudoAihDAO.obterLaudoPorSeqEPaciente(seq, pacCodigo);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	@Override
	@BypassInactiveModule
	public List<MamLaudoAih> listarLaudosAIH(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AipPacientes paciente) {
		return mamLaudoAihDAO.listarLaudosAIH(firstResult, maxResult, orderProperty, asc, paciente);
	}
	
	@Override
	@BypassInactiveModule
	public List<MamLaudoAih> obterLaudosDoPaciente(Integer pacCodigo) {
		return mamLaudoAihDAO.obterLaudosDoPaciente(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public Long listarLaudosAIHCount(AipPacientes paciente) {
		return mamLaudoAihDAO.listarLaudosAIHCount(paciente);
	}

	@Override
	@BypassInactiveModule
	public MamLaudoAih obterMamLaudoAihPorChavePrimaria(Long seq) {
		return mamLaudoAihDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public void gravarMamLaudoAih(MamLaudoAih mamLaudoAih) {
		mamLaudoAihDAO.atualizar(mamLaudoAih);
	}

	@Override
	public Long pesquisarConsultaCountSituacaoConsulta(FiltroConsultaBloqueioConsultaVO filtroConsulta) {
		return aacSituacaoConsultasDAO.pesquisarConsultaCount(filtroConsulta);
	}

	@Override
	public List<AacSituacaoConsultas> pesquisarConsultaPaginadaSituacaoConsulta(FiltroParametrosPadraoConsultaVO filtroPadrao,
			FiltroConsultaBloqueioConsultaVO filtroConsulta) {
		return aacSituacaoConsultasDAO.pesquisarConsultaPaginada(filtroPadrao, filtroConsulta);
	}

	@Override
	public AacSituacaoConsultas obterSituacaoConsultaPeloId(String situacao) {
		return aacSituacaoConsultasDAO.obterSituacaoConsultaPeloId(situacao);
	}

	@Override
	@Secure("#{s:hasPermission('manterBloqueioConsulta', 'alterar')}")
	public void persistirRegistroSituacaoConsulta(AacSituacaoConsultas registro)
			throws ApplicationBusinessException {
		aacSituacaoConsultasDAO.persistir(registro);
		aacSituacaoConsultasDAO.flush();		
	}

	@Override
	@Secure("#{s:hasPermission('manterBloqueioConsulta', 'alterar')}")
	public void atualizarRegistroSituacaoConsulta(AacSituacaoConsultas registro)
			throws ApplicationBusinessException {
		aacSituacaoConsultasDAO.atualizar(registro);
		aacSituacaoConsultasDAO.flush();		
	}

	@Override
	@BypassInactiveModule
	public MamLaudoAih obterLaudoAIHPorChavePrimaria(Long laudoAIHSeq) {
		return this.mamLaudoAihDAO.obterPorChavePrimaria(laudoAIHSeq);
	}

	@Override
	@BypassInactiveModule
	public List<VFatSsmInternacaoVO> pesquisarInternacoesPacienteByLaudo(Long seqMamLaudoAih, Short paramTabelaFaturPadrao) {
		return this.mamLaudoAihDAO.pesquisarInternacoesPacienteByLaudo(seqMamLaudoAih, paramTabelaFaturPadrao);
	}

	@Override
	public AacGradeAgendamenConsultas obterGradesAgendaPorEspecialidadeDataLimiteParametros(Short espSeq, Short pPagadorSUS,
			Short pTagDemanda, Short pCondATEmerg, Date dataLimite) {

		return this.aacGradeAgendamenConsultasDAO.obterGradesAgendaPorEspecialidadeDataLimiteParametros(espSeq, pPagadorSUS, pTagDemanda,
				pCondATEmerg, dataLimite);
	}

	@Override
	public GradeAgendaVo obterGradesAgendaVOPorEspecialidadeDataLimiteParametros(Short espSeq, 
			Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg , Date dataLimite) throws ServiceException {
		return this.aacConsultasRN.obterGradesAgendaVOPorEspecialidadeDataLimiteParametros(espSeq, pPagadorSUS, pTagDemanda, pCondATEmerg, dataLimite);
	}
	
	@Override
	public List<GradeAgendamentoAmbulatorioServiceVO> listarQuantidadeConsultasAmbulatorioVO(List<String> sitConsultas, Short espSeq, String situacaoMarcado, Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg, boolean isDtConsultaMaiorDtAtual) throws ApplicationBusinessException {
		return this.aacConsultasRN.listarQuantidadeConsultasAmbulatorioVO(sitConsultas, espSeq, situacaoMarcado, pPagadorSUS, pTagDemanda, pCondATEmerg, isDtConsultaMaiorDtAtual);
	}
	
	@Override
	public Integer inserirConsultaEmergencia(Date dataConsulta, Integer gradeSeq, Integer pacCodigo, Short pConvenioSUSPadrao, Byte pSusPlanoAmbulatorio, Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg, String nomeMicrocomputador) throws BaseException {
		return this.aacConsultasRN.inserirConsultaEmergencia(dataConsulta, gradeSeq, pacCodigo, pConvenioSUSPadrao, pSusPlanoAmbulatorio, pPagadorSUS, pTagDemanda, pCondATEmerg, nomeMicrocomputador);
	}
	
	@Override
	public Integer atualizarConsultaEmergencia(Integer numeroConsulta, Integer pacCodigo, Date dataHoraInicio, Short pConvenioSUSPadrao, Byte pSusPlanoAmbulatorio, String indSitConsulta, String nomeMicrocomputador) throws BaseException {
		return this.aacConsultasRN.atualizarConsultaEmergencia(numeroConsulta, pacCodigo, dataHoraInicio, pConvenioSUSPadrao, pSusPlanoAmbulatorio, indSitConsulta, nomeMicrocomputador);
	}
	
	@Override
	public Short obterUnidadeAssociadaAgendaPorNumeroConsulta(Integer conNumero) {	
		return this.aacGradeAgendamenConsultasDAO.obterUnidadeAssociadaAgendaPorNumeroConsulta(conNumero);
	}
	
	@Override
	public AacSituacaoConsultas obterSituacaoMarcada() {
		return getMarcacaoConsultaON().obterSituacaoMarcada();
	}

	@Override
	public List<AacConsultas> obterConsultasPorNumero(List<Integer> numeros) {
		return aacConsultasDAO.obterConsultasPorNumero(numeros);
	}

	@Override
	public List<ConsultaEspecialidadeAlteradaVO> obterConsultasEspecialidadeAlterada(Short espSeq, Integer grdSeq, Integer conNumero) {
		return aacGradeAgendamenConsultasDAO.obterConsultasEspecialidadeAlterada(espSeq, grdSeq, conNumero);
	}

	@Override
	public List<GradeAgendamentoAmbulatorioVO> listarQuantidadeConsultasAmbulatorio(List<String> sitConsultas, Short espSeq,
			String situacaoMarcado, Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg, boolean isDtConsultaMaiorDtAtual) {

		return this.aacGradeAgendamenConsultasDAO.listarQuantidadeConsultasAmbulatorio(sitConsultas, espSeq, situacaoMarcado, pPagadorSUS,
				pTagDemanda, pCondATEmerg, isDtConsultaMaiorDtAtual);
	}

	@Override
	public List<AacConsultas> pesquisarPorConsultaPorNumeroConsultaEspecialidade(List<Integer> conNumero, Short espSeq) {
		return aacConsultasDAO.pesquisarPorConsultaPorNumeroConsultaEspecialidade(conNumero, espSeq);
	}

	@Override
	public AacConsultas obterAacConsultasJoinGradeEEspecialidade(final Integer numeroConsulta) {
		return aacConsultasDAO.obterAacConsultasJoinGradeEEspecialidade(numeroConsulta);
	}

	
	@Override
	public List<Integer> pesquisarConsultaPorPacienteUnidadeFuncional(Integer pacCodigo, Short unfSeq1, Short unfSeq2) {
		return getAacConsultasDAO().pesquisarPorPacienteUnidadeFuncional(pacCodigo, unfSeq1, unfSeq2);
	}

	@Override
	public List<Date> pesquisarPorPacienteConsultaGestacao(Integer pacCodigo, Integer conNumero, Short gsoSeqp) {
		return getAacConsultasDAO().pesquisarPorPacienteConsultaGestacao(pacCodigo, conNumero, gsoSeqp);
	}

	@Override
	public List<MamLaudoAih> pesquisarLaudosAihPorConsultaPaciente(Integer conNumero, Integer pacCodigo) {
		return getMamLaudoAihDAO().pesquisarLaudosAihPorConsultaPaciente(conNumero, pacCodigo);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoParaPrescricaoMedica(Integer codigoPac, Integer atdSeq) {
		return prescricaoAmbulatorialON.pesquisarAtendimentoParaPrescricaoMedica(codigoPac, atdSeq);
	}

	@Override
	@Secure("#{s:hasPermission('manterTipoAutorizacao','pesquisar')}")
	public List<AacTipoAgendamento> pesquisarTipoAgendamentoPaginado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short codigoTipoAutorizacao, String descricaoTipoAutorizacao, DominioSituacao situacaoTipoAutorizacao) {
		return this.getAacTipoAgendamentoDAO().pesquisarTipoAgendamentoPaginado(firstResult, maxResult, orderProperty, asc,
				codigoTipoAutorizacao, descricaoTipoAutorizacao, situacaoTipoAutorizacao);
	}

	@Override
	public Long countTipoAgendamentoPaginado(Short codigoTipoAutorizacao, String descricaoTipoAutorizacao,
			DominioSituacao situacaoTipoAutorizacao) {
		return this.getAacTipoAgendamentoDAO().countPesquisarTipoAgendamentoPaginado(codigoTipoAutorizacao, descricaoTipoAutorizacao,
				situacaoTipoAutorizacao);
	}

	@Override
	@Secure("#{s:hasPermission('manterTipoAutorizacao','alterar')}")
	public void persistirTipoAgendamento(AacTipoAgendamento tipoAgendamento) throws ApplicationBusinessException {
		this.getManterTipoAutorizacaoRN().persistirTipoAgendamento(tipoAgendamento);
	}

	@Override
	@Secure("#{s:hasPermission('manterTipoAutorizacao','alterar')}")
	public void atualizarTipoAgendamento(AacTipoAgendamento tipoAgendamento) throws ApplicationBusinessException {
		this.getManterTipoAutorizacaoRN().atualizarCondicaoAtendimento(tipoAgendamento);
	}

	@Override
	@Secure("#{s:hasPermission('manterTipoAutorizacao','alterar')}")
	public void removerTipoAgendamento(Short tipoAgendamento) throws ApplicationBusinessException {
		this.getManterTipoAutorizacaoRN().remover(tipoAgendamento);
	}

	@Override
	@BypassInactiveModule
	public List<Long> buscaSeqTipoItemEvolucaoPrescEnf(Integer atdSeq, Date data, Integer seqMTIE, Integer catSeq) {
		return this.getMamTipoItemEvolucaoDAO().buscaSeqTipoItemEvolucaoPrescEnf(atdSeq, data, seqMTIE, catSeq);
	}

	@Override
	public Boolean verificarAtendimentoHCPA() {
		return getAtendimentoPacientesAgendadosRN().verificarAtendimentoHCPA();
	}

	@Override
	public List<AacRetornos> pesquisarConsultaPaginadaRetornoConsulta(FiltroParametrosPadraoConsultaVO filtroPadrao,
			FiltroConsultaRetornoConsultaVO filtroConsulta) {
		return getAacRetornosDAO().pesquisarConsultaPaginadaRetornoConsulta(filtroPadrao, filtroConsulta);
	}

	@Override
	@Secure("#{s:hasPermission('manterRetornoConsulta', 'alterar')}")
	public void persistirRetornoConsulta(AacRetornos situacaoConsulta, DominioSimNao dominioSimNao, DominioSituacao situacao,
			DominioIndAbsenteismo dominioIndAbsenteismo) throws ApplicationBusinessException {
		retornoConsultaRN.persistirRegistro(situacaoConsulta, dominioSimNao, situacao, dominioIndAbsenteismo);
	}

	@Override
	@Secure("#{s:hasPermission('manterRetornoConsulta', 'alterar')}")
	public void atualizarRetornoConsulta(AacRetornos situacaoConsulta, DominioSituacao situacao, DominioIndAbsenteismo dominioIndAbsenteismo)
			throws ApplicationBusinessException {
		retornoConsultaRN.atualizarRegistro(situacaoConsulta, situacao, dominioIndAbsenteismo);
	}

	@Override
	public AacRetornos obterRetornoConsultaPeloId(Integer id) {
		return getAacRetornosDAO().obterPorChavePrimaria(id);
	}

	@Secure("#{s:hasPermission('manterRetornoConsulta', 'alterar')}")
	public void removerRegistroRetornoConsulta(Integer seq) throws ApplicationBusinessException {
		retornoConsultaRN.removerRetornoConsulta(seq);
	}

	@Override
	@BypassInactiveModule
	public List<Integer> buscaSeqTipoItemEvolucaoPorCategoria(Integer seq) {
		return this.getMamTipoItemEvolucaoDAO().buscaSeqTipoItemEvolucaoPorCategoria(seq);
	}
	
	@Override
	public void inserirAtestadoAmbulatorio(MamAtestados atestados) throws BaseException {
		getAtendimentoPacientesAgendadosRN().inserirAtestadoAmbulatorio(atestados);
		
	}
	
	@Override
	public List<MamTipoAtestado> listarTodos(){
		return getAtendimentoPacientesAgendadosRN().listarTodos();
	}
	
	@Override
	public List<RelatorioAtestadoVO> recuperarInfConsultaAtestados(List<MamAtestados> atestado) throws ApplicationBusinessException {
		  return getAtendimentoPacientesAgendadosRN().recuperarInformacoesConsultaAtestados(atestado);
	}
	
	@Override
	public List<RelatorioConsultasAgendaVO> recuperarInformacoesConsultaParaRelPDF(List<AacConsultas> consultas) {
		return getConsultaAgendaON().recuperarInformacoesConsultaParaRelPDF(consultas);
	}
	
	@Override
	public List<MamAtestados> listarAtestado(MamAtestados atestado) {
		return getMamAtestadosDAO().buscarAtestado(atestado);	
	}

	@Override
	public Long pesquisarConsultaCountRetornoConsulta(FiltroConsultaRetornoConsultaVO filtroConsulta) {
		return getAacRetornosDAO().pesquisarConsultaCountRetornoConsulta(filtroConsulta);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoParaPrescricaoMedica(Integer codigoPac, Integer atdSeq,
			List<DominioOrigemAtendimento> origensInternacao, List<DominioOrigemAtendimento> origensAmbulatorio) {
		return prescricaoAmbulatorialON.pesquisarAtendimentoParaPrescricaoMedica(codigoPac, atdSeq, origensInternacao, origensAmbulatorio);

	}

	// Inicio manterEspecialidadePmpa
	@Override
	public void excluirEspecialidadePmpa(AacEspecialidadePmpa aacEspecialidadePmpa) throws ApplicationBusinessException {
		this.getManterEspecialidadePmPaRN().remover(aacEspecialidadePmpa);

	}

	@Override
	public void persistirEspecialidadePmpa(AacEspecialidadePmpa aacEspecialidadePmpa) throws BaseException {
		this.getManterEspecialidadePmPaRN().persistir(aacEspecialidadePmpa);
	}

	@Override
	public Long countEspecialidadePmpaPaginado(Short codigoEspecialidadePmpa, Long seqEspecialidadePmpa) {
		return this.getAacEspecialidadePmpaDAO().countPesquisarEspecialidadePmpaPaginado(codigoEspecialidadePmpa, seqEspecialidadePmpa);
	}

	@Override
	public List<AacEspecialidadePmpa> listarEspecialidadePmpaPaginado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short seqEspecialidadePmpa, Long codigoEspecialidadePmpa) {
		return this.getAacEspecialidadePmpaDAO().listarEspecialidadePmpaPaginado(firstResult, maxResult, orderProperty, asc,
				seqEspecialidadePmpa, codigoEspecialidadePmpa);
	}

	@Override
	public AacEspecialidadePmpa obterAacEspecialidadePmpaPorChavePrimaria(Short seq, Long codigo) {
		return this.getAacEspecialidadePmpaDAO().obterEspecialidade(seq, codigo);
	}

	protected ManterEspecialidadePmpaRN getManterEspecialidadePmPaRN() {
		return this.manterEspecialidadePmpaRN;
	}

	protected AacEspecialidadePmpaDAO getAacEspecialidadePmpaDAO() {
		return aacEspecialidadePmpaDAO;
	}

	// Fim manterEspecialidadePmpa

	protected ManterMotivoConsultaRN getManterMotivoConsultaRN() {
		return this.manterMotivoConsultaRN;
	}

	@Override
	public List<MamTipoItemAnamneses> pesquisarListaTipoItemAnamnesesPorCategoriaProfissional(
			Integer seqCategoriaProfissional) {
		return this.getManterTipoItensAnamneseRN().pesquisarMamTipoItemAnamneses(seqCategoriaProfissional);
	}

	@Override
	public void verificarEPersistirTipoItemAnamneses(
			MamTipoItemAnamneses tipoItemAnamnese) throws BaseException {
		this.getManterTipoItensAnamneseRN().verificarEPersistirTipoItemAnamnese(tipoItemAnamnese);
	}
	
	public AltaAmbulatorialON getAltaAmbulatorialON() {
		return altaAmbulatorialON;
	}

	public void setAltaAmbulatorialON(AltaAmbulatorialON altaAmbulatorialON) {
		this.altaAmbulatorialON = altaAmbulatorialON;
	}
	
	@Override
	public List<AghAtendimentos> pesquisarAtendimentoParaAltaAmbulatorial(
			Integer codigo, Integer atdSeq) {
		return getAltaAmbulatorialON().pesquisarAtendimentoParaAltaAmbulatorial(codigo, atdSeq);
	}

	@Override
	public MamAltaSumario pesquisarAltasSumariosPorNumeroConsulta(Integer numeroConsulta) {
		return getMamAltasSumarioDAO().pesquisarAltasSumariosPorNumeroConsulta(numeroConsulta);
	}

	@Override
	public List<MamAltaDiagnosticos> pesquisarAltaDiagnosticosPorSumarioAlta(Long altaSumarioSeq) {
		return getMamAltaDiagnosticosDAO().pesquisarAltaDiagnosticosPorSumarioAlta(altaSumarioSeq);
	}

	@Override
	public MamAltaSumario persistirMamAltaSumario(AipPacientes paciente, 
			AghAtendimentos atendimento, String loginUsuarioLogado) throws ApplicationBusinessException{
		return getAltaAmbulatorialON().persistirMamAltaSumario(paciente, atendimento, loginUsuarioLogado);
	}

	@Override
	public void persistirMamAltaEvolucoes(MamAltaEvolucoes evolucao) {
		getAltaAmbulatorialON().persistirMamAltaEvolucoes(evolucao);
	}

	@Override
	public void persistirMamAltaDiagnosticos(MamAltaDiagnosticos diagnostico) throws ApplicationBusinessException {
		getAltaAmbulatorialON().persistirMamAltaDiagnosticos(diagnostico);
	}

	@Override
	public void removerMamAltaDiagnosticos(MamAltaDiagnosticos diagnostico) {
		getAltaAmbulatorialON().removerMamAltaDiagnosticos(diagnostico);
	}

	@Override
	public void persistirMamAltaPrescricoes(MamAltaPrescricoes prescricao) {
		getAltaAmbulatorialON().persistirMamAltaPrescricoes(prescricao);
	}

	@Override
	public void removerMamAltaPrescricoes(MamAltaPrescricoes prescricao) {
		getAltaAmbulatorialON().removerMamAltaPrescricoes(prescricao);
	}

	@Override
	public void desbloquearMamAltaSumario(MamAltaSumario altaSumario) {
		getAltaAmbulatorialON().desbloquearMamAltaSumario(altaSumario);
	}
	
	@Override
		public Long pesquisarListaCategoriaProfissionalCount(Object filtro) {
			return this.manterTipoItensAnamneseRN.pesquisarListaCategoriaProfissionalCount(filtro);
	}
	
	@Override
	public List<CseCategoriaProfissional> pesquisarListaCategoriaProfissional(Object filtro) {
		return this.manterTipoItensAnamneseRN.pesquisarListaCategoriaProfissional(filtro);
	}
	
	// InÃ­cio ManterTipoSolicitacaoProcedimentos #12160
	@Override
	public List<MamTipoSolProcedimento> pesquisarTipoSolicitacaoProcedimentosPaginado(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MamTipoSolProcedimento tipoSolicitacaoProcedimento) {
		return this.mamTipoSolicitacaoProcedimentoRN
				.pesquisarTipoSolicitacaoProcedimentosPaginado(firstResult,
						maxResult, orderProperty, asc,
						tipoSolicitacaoProcedimento);
	}

	@Override
	public Long countTipoSolicitacaoProcedimentosPaginado(
			MamTipoSolProcedimento tipoSolicitacaoProcedimento) {
		return this.mamTipoSolicitacaoProcedimentoRN
				.countTipoSolicitacaoProcedimentosPaginado(
						tipoSolicitacaoProcedimento);
	}

	@Override
	public MamTipoSolProcedimento obterTipoSolicitacaoProcedimentoPorCodigo(Short codigoTipoSolicitacaoProcedimento){
		return mamTipoSolicitacaoProcedimentoRN.obterTipoSolicitacaoProcedimentoPorChavePrimaria(codigoTipoSolicitacaoProcedimento);
	}

	// Fim ManterTipoSolicitacaooProcedimentos
	@Override
	public Long pesquisarListaTipoItemAnamnesesPorCategoriaProfissionalCount(
			Integer seqCategoriaProfissional) {
		return this.manterTipoItensAnamneseRN
				.retornaQtdTipoItemAnamnesePorCategoriaProfissionalCount(
						seqCategoriaProfissional);
	}

	@Override
	public Integer obterAtendimentoPorConNumero(Integer conNumero) {
		return this.aghAtendimentosDAO.obterAtendimentoPorConNumero(conNumero);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoPorSeq(Integer seq) {
		return this.ambulatorioRN.obterAghAtendimentosPorSeq(seq);
	}

	private ManterTipoItensAnamneseRN getManterTipoItensAnamneseRN() {
		return manterTipoItensAnamneseRN;
	}
	
	@Override
	@BypassInactiveModule
	public List<DiagnosticoEtiologiaVO> listarAtualizarMamDignosticos(Integer atdSeq) {
		return getDiagnosticoDAO().listarAtualizarMamDignosticos(atdSeq);
	}
	
	
	// Fim ManterTipoItensAnamnese
	@Override
	public void excluirTipoSolicitacaoProcedimentos(Short seq) throws ApplicationBusinessException {
		mamTipoSolicitacaoProcedimentoRN.excluirTipoSolicitacaoProcedimentos(seq);
	}
	
	@Override
	public void persistirTipoSolicitacaoProcedimentos(MamTipoSolProcedimento tipoSolicitacaoProcedimento) throws BaseException {
		mamTipoSolicitacaoProcedimentoRN.persistirTipoSolicitacaoProcedimentos(tipoSolicitacaoProcedimento);
	}

	@Override
	public AacConsultas atualizarConsulta(AacConsultas consulta,
			AacConsultas consultaAnterior, String nomeMicrocomputador,
			Date dataFimVinculoServidor, Boolean substituirProntuario,
			Boolean aack_prh_rn_v_apac_diaria,
			Boolean aack_aaa_rn_v_protese_auditiva,
			Boolean fatk_cap_rn_v_cap_encerramento)
			throws NumberFormatException, BaseException {
		return getAmbulatorioConsultaRN().atualizarConsulta(consultaAnterior,
				consulta, false, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, true, aack_prh_rn_v_apac_diaria, aack_aaa_rn_v_protese_auditiva, fatk_cap_rn_v_cap_encerramento);
	}

	@Override
	public Long pesquisarConsultasPorEspecialidade(Short espSeq,
			List<Integer> consultasPacientesEmAtendimento) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AacConsultas inserirConsulta(AacConsultas consulta,
			String nomeMicrocomputador, Boolean substituirProntuario,
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento) throws NumberFormatException,
			BaseException {
		return getAmbulatorioConsultaRN().inserirConsulta(consulta, false, nomeMicrocomputador, new Date(),
				substituirProntuario, aack_prh_rn_v_apac_diaria, aack_aaa_rn_v_protese_auditiva,
				fatk_cap_rn_v_cap_encerramento);
	}

	@Override
	public Short obtemCodigoEspecialidadeGradePeloNumeroConsulta(
			Integer conNumero) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> pesquisarConsultasPorEspecialidade(Short espSeq) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade#
	 * liberarConsultaPorObito(java.lang.Integer, java.lang.String)
	 */
	@Override
	public void liberarConsultaPorObito(Integer codigoPaciente, String nomeMicrocomputador) throws BaseException {

		getAacConsultasRN().liberarConsultaPorObito(codigoPaciente, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade#pesquisarConsultasPendentes(br.gov.mec.aghu.model.RapServidores, java.util.Date,
	 * br.gov.mec.aghu.model.AghEspecialidades, br.gov.mec.aghu.model.AghEquipes, br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO,
	 * br.gov.mec.aghu.model.VAacSiglaUnfSala, br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO, java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	public List<PesquisarConsultasPendentesVO> pesquisarConsultasPendentes(RapServidores usuario, Date data, AghEspecialidades especialidade, AghEquipes equipe,
			VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala sala, RapServidores profissional, DataInicioFimVO turno, Short paramSeqSituacaoAtendimento,
			Integer paramDiasReabrirPendente) {

		return getAacConsultasDAO().pesquisarConsultasPendentes(usuario, data, especialidade, equipe, zona, sala, profissional, turno, paramSeqSituacaoAtendimento,
				paramDiasReabrirPendente);
	}
	
	@Override
	public AacConsultas atualizarConsulta(AacConsultas consultaAnterior, AacConsultas consulta,
			Boolean marcacaoConsulta, String nomeMicrocomputador, Date dataFimVinculoServidor,
			Boolean substituirProntuario) throws NumberFormatException, BaseException {
		return ambulatorioConsultaRN.atualizarConsulta(consultaAnterior, consulta, marcacaoConsulta,
				nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, true);
	}
	
	@Override
	public List<AacConsultas> pesquisarConsultasAnterioresPacienteByEspecialidade(Integer consultaAtual, Integer codPaciente, Date dtInicio, Date dtFim, Short espSeq, Integer retSeq){
		return getAacConsultasDAO().pesquisarConsultasAnterioresPacienteByEspecialidade(consultaAtual, codPaciente, dtInicio, dtFim, espSeq, retSeq);
	}
	
	@Override
	public Integer obterAtdSeqPorNumeroConsulta(Integer numeroConsulta){
		return this.aacConsultasDAO.obterAtdSeqPorNumeroConsulta(numeroConsulta);
	}
	
	@Override
	public void registraChegadaPaciente(Integer numeroConsulta, String nomeMicrocomputador, Integer retSeq) throws BaseException {
		AacConsultas consulta = aacConsultasDAO.obterPorChavePrimaria(numeroConsulta);
		consulta.setRetSeq(retSeq);
		getPesquisarPacientesAgendadosON().registraChegadaPaciente(consulta, nomeMicrocomputador);
	}
	
	@Override
	public void atualizarConsultaRetorno(AacConsultas consulta)
			throws ApplicationBusinessException {
		AacConsultas consultaOriginal = this.getAacConsultasDAO().obterPorChavePrimaria(consulta.getNumero());
		if (consulta.getRetSeq() != null) {
			consultaOriginal.setRetSeq(consulta.getRetSeq());
			AacRetornos retorno = this.getAacRetornosDAO().obterPorChavePrimaria(consultaOriginal.getRetSeq());
			consultaOriginal.setRetorno(retorno);
		}		
		this.getAacConsultasDAO().merge(consultaOriginal);
		this.getAacConsultasDAO().flush();
	}
	
	 @Override
		public List<AipPacientes> listarConsultasParaLiberarObito(
				Integer firstResult, Integer maxResult, String orderProperty,
				boolean asc, AipPacientes aipPacientes) {
			return aipPacientesDAO.listarConsultasParaLiberarObito(firstResult,
					maxResult, orderProperty, asc, aipPacientes);

		}

	@Override
	public Long listarConsultasParaLiberarObitoCount(AipPacientes aipPacientes) {
		return aipPacientesDAO
				.listarConsultasParaLiberarObitoCount(aipPacientes);
	}
	
	@Override
	public List<ConsultasDeOutrosConveniosVO> pesquisarConsultasDeOutrosConvenios(
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Date mesAno) throws ApplicationBusinessException  {
		return pesquisarConsultasDeOutrosConveniosON.pesquisarConsultasDeOutrosConvenios(firstResult, maxResult, orderProperty, asc, mesAno);
	}

	@Override
	public Long pesquisarConsultasDeOutrosConveniosCount(Date mesAno) throws ApplicationBusinessException  {
			return pesquisarConsultasDeOutrosConveniosON.pesquisarConsultasDeOutrosConveniosCount(mesAno);
	}

	@Override
	public void excluiAnamese(AacConsultas consulta, MamAnamneses anamneseAtual)
			throws ApplicationBusinessException, ApplicationBusinessException {
		getAtendimentoPacientesAgendadosRN().excluiAnamese(consulta, anamneseAtual);		
	}

	@Override
	public void excluiEvolucao(AacConsultas consulta, MamEvolucoes evolucaoAtual)
			throws ApplicationBusinessException, ApplicationBusinessException {
		getAtendimentoPacientesAgendadosRN().excluiEvolucao(consulta, evolucaoAtual);
	}
	
	@Override
	public MamTipoItemEvolucao tipoItemEvolucaoPorSeq(Integer seq) {
		return getMamTipoItemEvolucaoDAO().tipoItemEvolucaoPorSeq(seq);
	}

	@Override
	@Secure("#{s:hasPermission('manterItensEvolucao', 'alterar')}")
	public void salvarItenEvolucao(MamTipoItemEvolucao tipoItemEvolucao) throws BaseException, ApplicationBusinessException {
		manterTipoItenEvolucaoRN.salvarItenEvolucao(tipoItemEvolucao);
	}

	@Override
	public List<MamTipoItemEvolucao> pesquisarListaTipoItemEvoulucaoPorCategoriaProfissional(Integer seq) {
		return getMamTipoItemEvolucaoDAO().pesquisarListaTipoItemEvoulucaoPorCategoriaProfissional(seq);
	}

	@Override
	public boolean validarAlteracaoCamposInalteraveis(MamTipoItemEvolucao tipoItemEvolucao) throws ApplicationBusinessException  {
		return manterTipoItenEvolucaoRN.validarAlteracaoCamposInalteraveis(tipoItemEvolucao);
	}
	
	@Override
	public void excluirAtestadoAcompanhamento(MamAtestados atestado) throws ApplicationBusinessException {
		this.mamAtestadosRN.excluirAtestadoAcompanhamento(atestado);
	}
	
	@Override
	public void excluirAtestadoFgts(MamAtestados atestado) throws ApplicationBusinessException {
		this.mamAtestadosRN.excluirAtestadoFgts(atestado);
	}	
	
	@Override
	public void validarDatasAtestado(Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		this.mamAtestadosRN.validarDatasAtestado(dataInicial, dataFinal);
	}
	
	@Override
	public MamTipoAtestado obterTipoAtestadoPorSeq(Short seq) {
		return mamTipoAtestadoDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarPorSiglaOuNomeEspecialidade(Object parametro) {
		return getAghEspecialidadesDAO().pesquisarPorSiglaOuNomeEspecialidade(parametro);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarPorSiglaOuNomeGestaoInterconsultas(Object parametro){
		return getAghEspecialidadesDAO().pesquisarPorSiglaOuNomeGestaoInterconsultas(parametro);
	}
	
	public Long pesquisarPorSiglaOuNomeGestaoInterconsultasCount(Object parametro){
		return getAghEspecialidadesDAO().pesquisarPorSiglaOuNomeGestaoInterconsultasCount(parametro);
	}
	
	@Override
	public Long pesquisarPorSiglaOuNomeEspecialidadeCount(Object parametro) {
		return getAghEspecialidadesDAO().pesquisarPorSiglaOuNomeEspecialidadeCount(parametro);
	}

	public AghEspecialidadesDAO getAghEspecialidadesDAO() {
		return aghEspecialidadesDAO;
	}
	
	@Override
	public List<MamInterconsultas> listaInterconsultas(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final MamInterconsultas mamInterconsultas, Date dataInicial, Date dataFinal, boolean consultoria, boolean excluidos) {
		return mamInterconsultasRN.listaInterconsultas(firstResult, maxResult, orderProperty, asc, mamInterconsultas, 
				dataInicial, dataFinal, consultoria, excluidos);
	}
	
	@Override
	@BypassInactiveModule			
	public Long listaInterconsultasCount(MamInterconsultas mamInterconsultas, Date dataInicial, Date dataFinal, boolean consultoria, boolean excluidos) {
		return mamInterconsultasRN.listaInterconsultasCount(mamInterconsultas, dataInicial, dataFinal, consultoria, excluidos);
	}
	
	/**
	 * Consulta as unidades funcionais por sigla ou descriÃ§Ã£o e filtra por caracterÃ­stica zona_ambulatorio.
	 * @param parametro Valor para sigla ou descriÃ§Ã£o.
	 * @return Lista com unidades funcionais filtradas.
	 */
	@Override
	public List<AghUnidadesFuncionais> obterSetorPorSiglaDescricaoECaracteristica(Object pesquisa) {
		return this.getAghUnidadesFuncionaisDAO().obterCriteriaAtivaPorSiglaOuDescricaoECaracteristica(pesquisa);
	}

	@Override
	public void excluirInterconsultas(MamInterconsultas excluirInterconsultas) throws ApplicationBusinessException{
		
		mamInterconsultasRN.excluirInterconsultas(excluirInterconsultas);
	}

	@Override
	public void avisarInterconsultas(MamInterconsultas avisarInterconsultas, String foiAvisado) throws ApplicationBusinessException{
		
		mamInterconsultasRN.atualizarInterconsulta(avisarInterconsultas, foiAvisado);
		
	}
	
	@Override
	public void validarDatas(Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		
		mamInterconsultasRN.validarDatas(dataInicial, dataFinal);
	}

	public GestaoInterconsultasRN getMamInterconsultasRN() {
		return mamInterconsultasRN;
	}

	@Override
	public void gravarInterconsultas(MamInterconsultas mamInterconsultas) throws ApplicationBusinessException {
          
		mamInterconsultasRN.gravarInterconsultas(mamInterconsultas);
    }

	public AghUnidadesFuncionaisDAO getAghUnidadesFuncionaisDAO() {
		return aghUnidadesFuncionaisDAO;
	}
	
	public RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}

	@Override
	public List<CabecalhoRelatorioAgendaConsultasVO> carregarRelatorioAgendaConsultas(Date dataInicio, Date dataFim, Integer seqGrade, Short seqEspecialidade, Short seqUnidadeFuncional, 
			DominioTurno turno) throws ApplicationBusinessException, BaseListException{
		return relatorioAgendaConsultasON.carregarRelatorioAgendaConsultas(dataInicio, dataFim, seqGrade, seqEspecialidade, seqUnidadeFuncional, turno);
	}
	
	@Override
    public List<PacienteGrupoFamiliarVO> obterProntuariosSugeridosVinculados(final Integer firstResult, final Integer maxResult, final String orderProperty,
    		final boolean asc,AipPacientes paciente,AipEnderecosPacientes endereco){ 
    	return aipGrupoFamiliarDAO.obterProntuariosSugeridosVinculados(firstResult, maxResult, orderProperty, asc, paciente,endereco);
	}
	
	@Override
	public List<PacienteGrupoFamiliarVO> obterProntuariosSugeridosNaoVinculados(final Integer firstResult, final Integer maxResult, final String orderProperty,
    		final boolean asc,AipPacientes paciente,AipEnderecosPacientes endereco){ 
    	return aipGrupoFamiliarDAO.obterProntuariosSugeridosNaoVinculados(firstResult, maxResult, orderProperty, asc, paciente,endereco);
    }
	
	@Override
	public List<PacienteGrupoFamiliarVO> obterFamiliaresVinculados(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc,AipPacientes pacienteContexto){
		return aipGrupoFamiliarDAO.obterFamiliaresVinculados(firstResult, maxResult, orderProperty, asc, pacienteContexto);
	}

	@Override
	public String gerarCSVAgendaConsultas(List<CabecalhoRelatorioAgendaConsultasVO> listaCabecalho) throws ApplicationBusinessException, IOException{
		return relatorioAgendaConsultasON.gerarCSVAgendaConsultas(listaCabecalho);
	}
	
	@Override
	public AacGradeAgendamenConsultas clonarAgendamenConsultaCopia(AacGradeAgendamenConsultas gradeAgendamenConsultaOriginal) throws IllegalAccessException, InvocationTargetException {
		return getManterGradeAgendamentoON().clonarAgendamenConsultaCopia(gradeAgendamenConsultaOriginal);
	}

	@Override
	public List<AacRetornos> obterTodosRetornosRelatorioAgenda(Integer retornoMaximo){
		return aacRetornosDAO.obterTodosRetornosRelatorioAgenda(retornoMaximo);
	}
	
	@Override
	public void validarHorariosAgendados(AacGradeAgendamenConsultas gradeAgendamenConsultas)  throws ApplicationBusinessException {
		Set<AacHorarioGradeConsulta> horarioGradeConsulta = gradeAgendamenConsultas.getHorarioGradeConsulta();
		for (AacHorarioGradeConsulta aacHorarioGradeConsulta : horarioGradeConsulta) {
			getManterGradeAgendamentoON().validarHorariosAgendados(aacHorarioGradeConsulta);
		}
	}
	
	@Override
	public void salvarInterconsulta(MamInterconsultas parametroSelecionado){
		mamInterconsultasRN.atualizarInterconsulta(parametroSelecionado); 
	}
	
	@Override
	public void validarHorariosSobrepostos(AacGradeAgendamenConsultas gradeAgendamenConsultas)  throws ApplicationBusinessException {
		Set<AacHorarioGradeConsulta> horarioGradeConsulta = gradeAgendamenConsultas.getHorarioGradeConsulta();
		for (AacHorarioGradeConsulta aacHorarioGradeConsulta : horarioGradeConsulta) {
			validaHorarioSobreposto(aacHorarioGradeConsulta, gradeAgendamenConsultas);
		}
	}
	
	@Override
	public AacGradeAgendamenConsultas copiarGradeAgendamento(AacGradeAgendamenConsultas gradeAgendamenConsultaCopia, AacGradeAgendamenConsultas gradeAgendamenConsultaOriginal) throws ApplicationBusinessException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		aacGradeAgendamenConsultasDAO.desatachar(gradeAgendamenConsultaOriginal);
		gradeAgendamenConsultaCopia = getManterGradeAgendamentoON().clonarAgendamenConsultaCopia(gradeAgendamenConsultaOriginal);
		getManterGradeAgendamentoON().salvar(gradeAgendamenConsultaCopia, null);

		copiarHorarioGradeConsulta(gradeAgendamenConsultaCopia, gradeAgendamenConsultaOriginal);
		copiarProcedimento(gradeAgendamenConsultaCopia, gradeAgendamenConsultaOriginal);
		copiarCaracteristica(gradeAgendamenConsultaCopia, gradeAgendamenConsultaOriginal);
		
		return gradeAgendamenConsultaCopia;
	}

	public void copiarHorarioGradeConsulta(AacGradeAgendamenConsultas gradeAgendamenConsultaCopia, AacGradeAgendamenConsultas gradeAgendamenConsultaOriginal) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
		AacHorarioGradeConsulta horarioGradeConsultaClone; 
		for (AacHorarioGradeConsulta horario: pesquisarHorariosPorGrade(gradeAgendamenConsultaOriginal)) {
			getAacHorarioGradeConsultaDAO().desatachar(horario);
			horarioGradeConsultaClone = (AacHorarioGradeConsulta) getManterGradeAgendamentoON().clonarHorarioGradeConsulta(horario, gradeAgendamenConsultaCopia);
			salvarHorarioGradeConsulta(horarioGradeConsultaClone, gradeAgendamenConsultaCopia);
		}
	}
	
	public void copiarProcedimento(AacGradeAgendamenConsultas gradeAgendamenConsultasCopia, AacGradeAgendamenConsultas gradeAgendamenConsultaOriginal) throws ApplicationBusinessException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		AacGradeProcedHospitalar procedimentoClone;
		AacGradeProcedHospitalarId idClone;
		for (AacGradeProcedHospitalar procedimento : listarProcedimentosGrade(gradeAgendamenConsultaOriginal.getSeq())) {
			aacGradeProcedHospitalarDAO.desatachar(procedimento);
			procedimentoClone = (AacGradeProcedHospitalar) getManterGradeAgendamentoON().clonarListaProcedimentosHospitalarConsulta(procedimento);
			idClone = new AacGradeProcedHospitalarId();
			idClone.setGrdSeq(gradeAgendamenConsultasCopia.getSeq());
			idClone.setPhiSeq(procedimento.getId().getPhiSeq());
			procedimentoClone.setId(idClone);
			procedimentoClone.setGradeAgendamentoConsulta(gradeAgendamenConsultasCopia);
			persistirProcedimento(procedimentoClone);
		}
	}

	public void copiarCaracteristica(AacGradeAgendamenConsultas gradeAgendamenConsultasCopia, AacGradeAgendamenConsultas gradeAgendamenConsultaOriginal) throws ApplicationBusinessException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		AacCaracteristicaGrade caracteristicaClone;
		AacCaracteristicaGradeId idClone;
		for (AacCaracteristicaGrade caracteristica : listarCaracteristicasGrade(gradeAgendamenConsultaOriginal.getSeq())) {
			getCaracteristicaGradeDAO().desatachar(caracteristica);
			idClone = new AacCaracteristicaGradeId();
			idClone.setCaracteristica(caracteristica.getId().getCaracteristica());
			idClone.setGrdSeq(gradeAgendamenConsultasCopia.getSeq());
			caracteristicaClone = (AacCaracteristicaGrade) getManterGradeAgendamentoON().clonarHorarioGradeConsulta(caracteristica);
			caracteristicaClone.setGradeAgendamentoConsulta(gradeAgendamenConsultasCopia);
			caracteristicaClone.setId(idClone);
			persistirCaracteristica(caracteristicaClone);
		}
	}
	
	
	@Override
	public MamTipoItemEvolucao obterMamTipoItemEvolucaoPorChavePrimaria(Integer seq){
		return getMamTipoItemEvolucaoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public MamTipoItemAnamneses obterMamTipoItemAnamnesesPorChavePrimaria(Integer seq) {
		return getMamTipoItemAnamnesesDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public List<AacGradeAgendamenConsultas> listarGradeUnidFuncionalECaracteristicas(Integer numeroConsulta) {
		return aacGradeAgendamenConsultasDAO.listarGradeUnidFuncionalECaracteristicas(numeroConsulta);
	}
	
	@Override
	public List<MamConsultorAmbulatorioVO> pesquisarConsultorAmbulatorioPorServidor(RapServidores servidor) {
		return mamConsultorAmbulatorioDAO.pesquisarConsultorAmbulatorioPorServidor(servidor);
	}
	
	@Override
	public void persistirConsultorAmbulatorio(MamConsultorAmbulatorio mamConsultorAmbulatorio) throws ApplicationBusinessException {
		mamConsultorAmbulatorioRN.persistirConsultorAmbulatorio(mamConsultorAmbulatorio);
	}
	
	@Override
	public MamConsultorAmbulatorio obterMamConsultorAmbulatorioPorId(Integer seq) {
		return mamConsultorAmbulatorioDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public List<ConsultaAmbulatorioVO> consultaAbaPacientesAusentes(Date dtPesquisa, Short zonaUnfSeq, VAacSiglaUnfSala zonaSala, AghEspecialidades especialidade, AghEquipes equipe,
			RapServidores profissional) throws ApplicationBusinessException {
		return this.getConsultasON().consultaAbaPacientesAusentes(dtPesquisa, zonaUnfSeq, zonaSala, especialidade, equipe, profissional);
	}	
	@Override
	public String obterNomeResponsavelMarcacaoConsulta(Integer consulta) {
		return getConsultasON().obterNomeResponsavelMarcacaoConsulta(consulta);
	}

	public void persistirControleImpressaoLaudo(ConsultaAmbulatorioVO consulta) throws ApplicationBusinessException {
		getConsultasON().persistirControleImpressaoLaudo(consulta);
	}
	
	@Override
	public List<Integer> obterGradesComHorariosDisponiveis(Short espSeq, Date dataInicio, Date dataFim) {
		return this.aacGradeAgendamenConsultasDAO.obterGradesComHorariosDisponiveis(espSeq, dataInicio, dataFim);
	}
	
	@Override
	public List<AgendamentoAmbulatorioVO> obterHorariosDisponiveisAmbulatorio(Short espSeq, Integer grdSeq, Date dataInicio, Date dataFim) {
		return this.aacGradeAgendamenConsultasDAO.obterHorariosDisponiveisAmbulatorio(espSeq, grdSeq, dataInicio, dataFim);
	}
	
	@Override
	public void atualizarAacConsultas(Integer conNumero, Integer pacCodigo, Short cspCnvCodigo, Short cspSeq, String stcSituacao,
			Integer retSeq, Short caaSeq, Short tagSeq, Short pgdSeq, String nomeMicrocomputador) throws BaseException {
		this.aacConsultasRN.atualizarAacConsultas(conNumero, pacCodigo, cspCnvCodigo,cspSeq, stcSituacao,
				retSeq, caaSeq, tagSeq, pgdSeq, nomeMicrocomputador);
	}


	public List<MamAnamneses> pesquisarAnamnesesVerificarPrescricao(int dias, Integer prontuario) {
		return this.mamAnamnesesDAO.pesquisarAnamnesesVerificarPrescricao(dias, prontuario);
	}

	@Override
	public void salvarPagadores(ConverterConsultasVO novaConsulta, Integer numero){
		 aacConsultasRN.atualizarPagadores(novaConsulta, numero);
	}
   	
	public Long obterConsultaPagadoresCadastradosCount(String sbPagadores){   
		return aacFormaAgendamentoDAO.consultaPagadoresCadastradosCount(sbPagadores); 
	}
	
	//#42081
	@Override
	public List<RelatorioControleFrequenciaVO> pesquisarControleFrequencia(
			Boolean tipoImpressaoCF, Boolean tipoImpressaoLS) {
		return getRelatorioControleFrequenciaRN().pesquisarControleFrequencia();
	}
	@Override
	public Long obterProntuariosSugeridosVinculadosCount(AipPacientes paciente,AipEnderecosPacientes endereco) {
		return aipGrupoFamiliarDAO.obterProntuariosSugeridosVinculadosCount(paciente,endereco);
	}
	
	@Override
	public Long obterProntuariosSugeridosNaoVinculadosCount(AipPacientes paciente,AipEnderecosPacientes endereco) {
		return aipGrupoFamiliarDAO.obterProntuariosSugeridosNaoVinculadosCount(paciente,endereco);
	}
	
    @Override
	public void desvincularPacienteGrupoFamiliar(Integer pacCodigoContexto) throws ApplicationBusinessException{
		agrupamentoFamiliarON.desvincularPacienteGrupoFamiliar(pacCodigoContexto);
	}

	public RelatorioControleFrequenciaRN getRelatorioControleFrequenciaRN() {
		return relatorioControleFrequenciaRN;
	}

	public AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}

	@Override
	public String obterDataLocalFormula(Integer nroConsulta) {
		return relatorioControleFrequenciaRN.obterDataLocalFormula(nroConsulta);
	}

	@Override
	public void atualizarPacientePesquisadoGrupoFamiliar(Integer pacientePesquisado,Integer agfSeq) throws ApplicationBusinessException{
		 agrupamentoFamiliarON.atualizarPacientePesquisadoGrupoFamiliar(pacientePesquisado, agfSeq); 
	}
	
	@Override
	public String obterMesAnoAtual(Integer nroConsulta) {
		return relatorioControleFrequenciaRN.obterMesAnoAtual(nroConsulta);
	}


	@Override
	public AipPacientes obterPacientePorProntuario(Integer nroProntuario) {
		return getAipPacientesDAO().obterPacientePorProtuario(nroProntuario);
	}

	@Override
	public Integer getAghAtendimentosParametroVOQueryBuilder(Integer codConsulta){
		return aghAtendimentosDAO.obterParametroProcedure(codConsulta);
	}
	
	@Override
	public List<VAacConvenioPlano> pesquisarCovenioPlanoSGB(String pesquisa){
		return vAacConvenioPlanoDAO.pesquisarCovenioPlanoSGB(pesquisa);
	}
	
	@Override
	public Long pesquisarCovenioPlanoSGBCount(String pesquisa){
		return vAacConvenioPlanoDAO.pesquisarCovenioPlanoSGBCount(pesquisa);
	}

	@Override
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterDadosDeclaracao(Integer numeroConsulta) {
		return aacConsultasDAO.obterDadosDeclaracao(numeroConsulta);
	}
	
	@Override
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterDadosDeclaracaoConsulta(Integer numeroConsulta) {
		return aacConsultasDAO.obterDadosDeclaracaoConsulta(numeroConsulta);
	}
	
	@Override
	public Date obterDataConsultaPorNumero(Integer numeroConsulta) {
		return aacConsultasDAO.obterAacConsultasDataConsultaPorNumero(numeroConsulta);
	}
	
	@Override
	public Long obterCMCE(Integer nroConsulta) {
		return aacConsultasDAO.obterCMCE(nroConsulta);
	}
	
	@Override
	public List<VAacConvenioPlano> obterListaConvenios(){
		return vAacConvenioPlanoDAO.obterListaConvenios();
	}

	@Override
	public RelatorioSolicitacaoProcedimentoVO obterInformacoesRelatorioSolicProcedimento(Integer conNumero) {
		return mamSolicProcedimentoDAO.obterInformacoesRelatorioSolicitacaoProcedimento(conNumero);
	}

	@Override
	public RelatorioSolicitacaoProcedimentoVO obterDadosRelatorioSolicProcedimento(Long seq) {
		return mamSolicProcedimentoDAO.obterDadosRelatorioSolicitacaoProcedimento(seq);
	}	

	@Override
	public List<ConselhoProfissionalServidorVO> obterRegistroMedico(Integer vMatricula, Short vVinculo) throws ApplicationBusinessException {
		return atendimentoPacientesAgendadosON.obterRegistroMedico(vMatricula, vVinculo);
	}
	
	@Override
	public void atualizarIndImpressaoInterconsultas(Long seq) {
		ambulatorioRN.atualizarIndImpressaoInterconsultas(seq);
	}

	@Override
	public RelatorioConsultoriaAmbulatorialVO obterDadosConsultariaAmbulatorial(Long seq, Short espSeq) throws ApplicationBusinessException{
		return atendimentoPacientesAgendadosON.obterDadosConsultariaAmbulatorial(seq, espSeq);
	}
	
	@Override
	public void atualizarIndImpressoSolicitacaoProcedimento(Long seq){
		atendimentoPacientesAgendadosAuxiliarON.atualizarIndImpressoSolicitacaoProcedimento(seq);
	}

	@Override
	public MamRelatorioVO obterMamRelatorioVOPorSeq(Long seq) {
		return mamRelatorioDAO.obterMamRelatorioVOPorSeq(seq);
	}

	@Override
	public void atualizarIndImpressoRelatorioMedico(Long seq) {
		this.atendimentoPacientesAgendadosAuxiliarON.atualizarIndImpressoRelatorioMedico(seq);
	}

	@Override
	public String obterEspecialidade(Integer matricula, Short vinCodigo) {
		return this.getManterReceituarioON().obterEspecialidade(matricula, vinCodigo);
	}

	@Override
	public void atualizarIndImpressaoLaudoAIH(Long seq){
		this.laudoAihON.atualizarIndImpressaoLaudoAIH(seq); 
	}
	
	@Override
	public void atualizarIndImpressaoAltaAmb(Long seq){
		this.altaAmbulatorialON.atualizarIndImpressaoAltaAmb(seq);
	}
	
	@Override
	public List<GradeVO> obterAacGradeAgendamentoConsultas(FiltroGradeConsultasVO filtro){
		return aacGradeAgendamenConsultasDAO.obterAacGradeAgendamentoConsultas(filtro);
	}

	@Override
	public List<GradeConsultasVO> pesquisarConsultasPorGrade(Integer grade, FiltroGradeConsultasVO filtro,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		return aacConsultasDAO.pesquisaConsultasPorGrade(grade, filtro,firstResult,maxResult,orderProperty,asc);
	}
	
	@Override
	public Long pesquisarConsultasPorGradeCount(Integer grade, FiltroGradeConsultasVO filtro){
		return aacConsultasDAO.pesquisaConsultasPorGradeCount(grade, filtro);
	}
	
	@Override
	public String obterCodigoFormulaPaciente(Integer nroProntuario) {
		return relatorioControleFrequenciaRN.obterCodigoFormulaPaciente(nroProntuario);
	}
	@Override
	public List<VAacSiglaUnfSalaVO> pesquisarListaSetorSala(String pesquisa) throws ApplicationBusinessException{
		return vAacSiglaUnfSalaDAO.pesquisarListaSetorSala(pesquisa);
	}
	@Override
	public Long pesquisarListaSetorSalaCount(String pesquisa) throws ApplicationBusinessException{
		return vAacSiglaUnfSalaDAO.pesquisarListaSetorSalaCount(pesquisa);
	}
	@Override
	public List<AghEquipes> pesquisarEquipeAtiva(String pesquisa){
		return aghEquipesDAO.pesquisarEquipeAtiva(pesquisa);
	}
	
	@Override
	public void trocarConsultaGrade(Integer oldGrade, Integer newGrade, List<GradeConsultasVO> listaConsultasSelecionadas) throws ApplicationBusinessException{
		aacConsultasRN.trocarConsultaGrade(oldGrade, newGrade, listaConsultasSelecionadas);
	}
	
	@Override
	public List<ParametrosAghEspecialidadesAtestadoVO> getAghEspecialidadesParametroVOQueryBuilder(Integer codConsulta){
		return aghEspecialidadesDAO.obterParametroProcedure(codConsulta);
	}
	
	@Override
	public List<ParametrosAghPerfilProcessoVO> getAghPerfilProcessoParametroVOQueryBuilder(RapServidores usuarioLogado){
		return csePerfisUsuariosDAO.obterParametroProcedure(usuarioLogado);
	}
	
	public List<CseProcessos> getAghCseUsuarioParametroVOQueryBuilder(RapServidores usuarioLogado, Integer codConsulta){
		return cseUsuarioDAO.obterParametroProcedure(usuarioLogado, codConsulta);
	}
	
	@Override
	public AacConsultas obterAgendamentoConsultaPorFiltros(
			Integer consultaNumero, Short unidadeNumero){
		return getAacConsultasDAO().obterAgendamentoConsultaPorFiltros(consultaNumero, unidadeNumero);
	}
	
	@Override
	public boolean obterAlaPorNumeroConsulta(Integer consultaNumero)  {
		return getAacConsultasDAO().obterAlaPorNumeroConsulta(consultaNumero);
	}
	
	
	@Override
	public RelatorioAgendamentoConsultaVO popularTicketAgendamentoConsulta(AacConsultas consulta) throws ApplicationBusinessException {
		return getMarcacaoConsultaON().popularTicketAgendamentoConsulta(consulta);
	}
	
	@Override
	public RapServidores pesquisaServidorCseUsuarioPorServidor(RapServidores servidor){
		return getRapServidoresDAO().pesquisaServidorCseUsuarioPorServidor(servidor);
	}
	
	@Override
	public List<AacConsultasJn> pesquisarUsuariosMarcadorConsulta(Integer numeroConsulta){
		return getAacConsultasJnDAO().pesquisarUsuariosMarcadorConsulta(numeroConsulta);
	}
	
	
	@Override
	public List<RelatoriosInterconsultasVO> carregarRelatorioInterconsultas(Date dataInicial, Date dataFinal, 
			DominioSituacaoInterconsultasPesquisa situacaoFiltro, String ordenar, AghEspecialidades agenda){
		return mamInterconsultasDAO.carregarRelatorioInterconsultas(dataInicial, dataFinal, situacaoFiltro, ordenar, agenda);
	}
	
	@Override
	public List<RelatoriosInterconsultasVO> carregarRelatorioPacientesInterconsultas(Date dataInicial, Date dataFinal, AghEspecialidades agenda){
		return mamInterconsultasDAO.carregarRelatorioPacientesInterconsultas(dataInicial, dataFinal, agenda);
	}
	@Override
	public AipGrupoFamiliarPacientes obterProntuarioFamiliaPaciente(Integer pacCodigo){
		return aipGrupoFamiliarDAO.obterProntuarioFamiliaPaciente(pacCodigo);
	}
	@Override
	public Long obterFamiliaresVinculadosCount(AipPacientes paciente){ 
		return aipGrupoFamiliarDAO.obterFamiliaresVinculadosCount(paciente);
	}
	@Override
	public AipPacientes obterPacienteFull(Integer pacCodigo){
		return aipGrupoFamiliarDAO.obterPacienteFull(pacCodigo);
	}
	
	@Override
	public void vincularAmbosPacienteGrupoFamiliar(Integer pacCodigoContexto,Integer pacCodigoSugerido, Integer prontuarioInformado) throws ApplicationBusinessException{
		agrupamentoFamiliarON.vincularAmbosPacienteGrupoFamiliar(pacCodigoContexto, pacCodigoSugerido, prontuarioInformado);
	}

	@Override
	public void vincularPacienteGrupoFamiliar(Integer pacCodigo, Integer agfSeq) throws ApplicationBusinessException {
		 agrupamentoFamiliarON.vincularPacienteGrupoFamiliar(pacCodigo, agfSeq);
 	}
	
	@Override
	public Integer trocarSolicitacoes(Integer oldAtdSeq, Integer newAtdSeq){
		return transferirExamesRN.trocarSolicitacoes(oldAtdSeq, newAtdSeq);
	}
	
	@Override
	public List<TransferirExamesVO> obterDeConsulta(Integer numero){
		return aghAtendimentosDAO.obterDeConsulta(numero, null, false);
	}
	
	@Override
	public List<TransferirExamesVO> obterParaConsultas(Integer numero, Integer codigoPaciente){
		return aghAtendimentosDAO.obterDeConsulta(numero, codigoPaciente, true);
	}

	@Override
	public void cancelarAtestadosAltaSumario(Integer apaAtdSeq, Integer apaSeq, Short seqp, Long seq) throws ApplicationBusinessException {
		this.mamAtestadosRN.cancelarAtestadosAltaSumario(apaAtdSeq, apaSeq, seqp, seq);
	}
		
	@Override
	public List<MamAtestados> obterAtestadoPorSumarioAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp) {
		return this.mamAtestadosDAO.obterAtestadoPorSumarioAlta(apaAtdSeq, apaSeq, seqp);
	}
	
	@Override
    public Long geraRegistroDeAtendimentoVersao2(Integer atdSeq,
                                                               String pIndPesqPend,
                                                               String pTipoPendPesq,
                                                               String pIndPedeSituacao,
                                                               DominioSituacaoRegistro pSituacaoGerar)  throws ApplicationBusinessException{

        Long rgt =  mamAnamnesesON.geraRegistroDeAtendimentoVersao2(atdSeq,
                                                            pIndPesqPend,
                                                            pTipoPendPesq,
                                                            pIndPedeSituacao,
                                                            pSituacaoGerar);

        return rgt;
	}
	
	@Override
	public List<MamAtestados> listarAtestadosPorPacienteTipo(Integer atdSeq, Short tasSeq,MpmAltaSumario mpmAltaSumario) {
		return this.mamAtestadosDAO.listarAtestadosPorPacienteTipo(atdSeq, tasSeq,mpmAltaSumario);
	}
	
	@Override
	public MamAtestados obterAtestadoEAghCidPorSeq(Long seq) {
		return this.mamAtestadosDAO.obterAtestadoEAghCidPorSeq(seq);
	}
	
	@Override
	public List<MamAtestados> obterAtestadoPorSumarioAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp,
			Short tasSeq, Object[] situacoes) {
		return this.mamAtestadosDAO.obterAtestadoPorSumarioAlta(apaAtdSeq, apaSeq, seqp, tasSeq, situacoes);
	}
	
	@Override
	public List<MamAtestados> obterAtestadosPendentes(Integer apaAtdSeq, Integer apaSeq, Short seqp, Long seq) {
		return this.mamAtestadosDAO.obterAtestadosPendentes(apaAtdSeq, apaSeq, seqp, seq);
	}
	
	@Override
	public MamTipoAtestado obterMamTipoAtestadoPorChavePrimaria(Short seq) {
		return this.mamTipoAtestadoDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public MamAtestados obterMamAtestadoPorChavePrimaria(Long atsSeq) {
		return this.mamAtestadosDAO.obterPorChavePrimaria(atsSeq);
	}
    
	public List<ConverterConsultasVO> obterConsultaPagadoresCadastrados(String sbPagadores){   
		return aacFormaAgendamentoDAO.consultaPagadoresCadastrados(sbPagadores); 
	}
	
	@Override
	public Long contarPesquisarConsultasPorGrade(ConsultasGradeVO filtro) {
		return aacConsultasDAO.contarPesquisarConsultasPorGrade(filtro);
	}
	
	@Override
	public List<AacCondicaoAtendimento> obterListaCondicaoAtendimento(String parametro) throws ApplicationBusinessException{
		return aacCondicaoAtendimentoDAO.obterListaCondicaoAtendimento(parametro);
	}
	@Override
	public Long obterListaCondicaoAtendimentoCount(String parametro) throws ApplicationBusinessException{
		return aacCondicaoAtendimentoDAO.obterListaCondicaoAtendimentoCount(parametro);
	}
	@Override
	public List<AacPagador> obterListaPagadores(String filtro){
		return aacPagadorDAO.obterListaPagadores(filtro);
	}
	@Override
	public	Long obterListaPagadoresCount(String filtro){
		return aacPagadorDAO.pesquisarPagadoresCount(filtro);
	}
	
	@Override
	public List<AacTipoAgendamento> obterListaTiposAgendamento(String filtro){
		return aacTipoAgendamentoDAO.obterListaTiposAgendamento(filtro);
	}
	@Override
	public Long obterListaTiposAgendamentoCount(String filtro){
		return aacTipoAgendamentoDAO.pesquisarTiposAgendamentoCount(filtro);
	}
	
	@Override
	public List<RelatorioGuiaAtendimentoUnimedVO> imprimirGuiaAtendimentoUnimed(Integer conNumero) throws ApplicationBusinessException {
		return imprimirGuiaAtendimentoUnimedRN.imprimirGuiaAtendimentoUnimed(conNumero);
	}
	
	@Override
	public List<EspecialidadeDisponivelVO> obterListaEspecialidadesDisponiveis(FiltroEspecialidadeDisponivelVO filtro,Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc){
		return vAacConsTipoDAO.obterListaEspecialidadesDisponiveis(filtro,firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long obterListaEspecialidadesDisponiveisCount(FiltroEspecialidadeDisponivelVO filtro){
		return vAacConsTipoDAO.obterListaEspecialidadesDisponiveisCount(filtro);
	}
	
	@Override
	public void validarCamposPreenchidos(FiltroEspecialidadeDisponivelVO filtro) throws ApplicationBusinessException{
		ambulatorioConsultaON.validarCamposPreenchidos(filtro);
	}
	
	@Override
	public List<BigDecimal> obterSomatorioQuantidade(FiltroEspecialidadeDisponivelVO filtro){
		return vAacConsTipoDAO.obterSomatorioQuantidade(filtro);
	}

	@Override
	public List<FatProcedHospInternosVO> listarFatProcedHospInternosPorEspOuEspGrad(Integer grdSeq, AghEspecialidades especialidade, String parametro) {
		return fatProcedHospInternosDAO.listarFatProcedHospInternosPorEspOuEspGrad(grdSeq, especialidade, parametro);
	}

	@Override
	public Long listarFatProcedHospInternosPorEspOuEspGradCount(Integer grdSeq, AghEspecialidades especialidade, String parametro) {
		return fatProcedHospInternosDAO.listarFatProcedHospInternosPorEspOuEspGradCount(grdSeq, especialidade, parametro);
	}
	
	@Override
	public String obterEspecialidade(Integer conNumero) {
		return manterReceituarioON.obterEspecialidade(conNumero);
	}
	
	@Override
	public String obterEspecialidadePorConsulta(Integer conNumero){
		return aacConsultasDAO.obterEspecialidadePorConsulta(conNumero);
	}
	
	@Override
	public MptPrescricaoPaciente obterVinculoMatriculaResponsavel(Integer atdSeq, Integer seq){
		return mptPrescricaoPacienteDAO.obterVinculoMatriculaResponsavel(atdSeq, seq);
	}

	
	public List<AghEspecialidades> pesquisarAgendaInterconsulta(String parametro, AacConsultas consulta, Integer idadePaciente) {
		return aghEspecialidadesDAO.pesquisarAgendaInterconsulta(parametro, consulta, idadePaciente);
	}
	
	public Long pesquisarAgendaInterconsultaCount(String parametro, AacConsultas consulta, Integer idadePaciente) {
		  return aghEspecialidadesDAO.pesquisarAgendaInterconsultaCount(parametro, consulta, idadePaciente);
	}
	
	public List<EquipeVO> pesquisarEquipeInterconsulta(String parametro) {
		return aghEquipesDAO.pesquisarEquipeInterconsulta(parametro);
	}
	
	public Long pesquisarEquipeInterconsultaCount(String parametro) {
		  return aghEquipesDAO.pesquisarEquipeInterconsultaCount(parametro);
	}
	
	public List<RapServidoresVO> pesquisarServidorInterconsulta(String parametro) {
		return rapServidoresDAO.pesquisarServidorInterconsulta(parametro);
	}
	
	public Long pesquisarServidorInterconsultaCount(String parametro) {
		  return rapServidoresDAO.pesquisarServidorInterconsultaCount(parametro);
	}
	
	public Integer obterCodigoPacienteOrigem(Integer pOrigem,  Integer conNumero) {
		  return marcacaoConsultaRN.obterCodigoPacienteOrigem(pOrigem, conNumero);
	}
	
	public Date obterDataNascimentoAnterior(Integer codigoPaciente) {
	  	return this.getAipPacientesDAO().obterDataNascimentoAnterior(codigoPaciente);
	}
	
	public Date obterDtPrevisaoInterconsulta(Short espSeq, Short caaSeq) throws ApplicationBusinessException{
		return mamInterconsultasRN.obterDtPrevisaoInterconsulta(espSeq, caaSeq);
	}
	
	public void inserirSolicitacaoInterconsulta(List<SolicitaInterconsultaVO> solicitaInterconsultaVO, Integer numConsulta) throws ApplicationBusinessException, ParseException{
		 mamInterconsultasRN.inserirSolicitacaoInterconsulta(solicitaInterconsultaVO, numConsulta);
	}
	
	
	public Boolean verificarInterconsulta(SolicitaInterconsultaVO interconsultaVO) throws ApplicationBusinessException {
		return mamInterconsultasRN.verificarInterconsulta(interconsultaVO);
	}
		
	@Override
	public FatProcedHospInternos obterCodigoDescricaoProcedimentoProTransplante(Integer codigoPaciente) throws ApplicationBusinessException {
		return ambulatorioRN.obterCodigoDescricaoProcedimentoProTransplante(codigoPaciente);
	}
		
	@Override
	public Long consultarCMCEpaciente(final Integer pacCodigo,final Integer conNumero){
		return aacConsultasDAO.consultarCMCEpaciente(pacCodigo,conNumero); 
	}
	@Override
	public Boolean verificarInterconsultaAux(SolicitaInterconsultaVO interconsultaVO) throws ApplicationBusinessException {
		return mamInterconsultasRN.verificarInterconsultaAux(interconsultaVO);
	}
	
	@Override
	public List<ProfissionalHospitalVO> obterListaProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return registroColaboradorFacade.obterListaProfissionaisHospital(profissional, vinculo, conselho, firstResult, maxResult, orderProperty, asc);

	}

	@Override
	public Long obterCountProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho) {
		return registroColaboradorFacade.obterCountProfissionaisHospital(profissional, vinculo, conselho);
	}
	
	@Override
	public List<MamAtestados> obterAtestadosPorAtdSeqTipo(Integer atdSeq, Short tasSeq) {
		return this.mamAtestadosDAO.obterAtestadosPorAtdSeqTipo(atdSeq,tasSeq);
	}

	@Override
	public List<MamAtestados> obterAtestadosPorSumarioAltaTipo(Integer apaAtdSeq,
			Integer apaSeq, Short seqP, Short tasSeq) {
		return this.mamAtestadosDAO.obterAtestadosPorSumarioAltaTipo(apaAtdSeq, apaSeq, seqP,tasSeq);
	}

	@Override
	public void desatacharConsulta(AacConsultas consulta) {
		aacConsultasDAO.desatachar(consulta);
	}
	
	@Override
	public void verificarExisteConsultaMesmoDiaTurno(AacConsultas consulta) throws ApplicationBusinessException{
		getMarcacaoConsultaON().verificarExisteConsultaMesmoDiaTurno(consulta);
	}
	
	@Override
	public AacGradeAgendamenConsultas obterGradeAgendamentoParaMarcacaoConsultaEmergencia(Integer seq) {
		return this.getGradeAgendamenConsultasDAO().obterGradeAgendamentoParaMarcacaoConsultaEmergencia(seq);
	}
	
	@Override
	public Long obterApacNumero(Integer numeroConsulta) {
		return fatLaudosPacApacsDAO.obterApacNumero(numeroConsulta);
	}

	@Override
	public VFatAssociacaoProcedimento obterCodigoTabelaEDescricao(Integer phiSeq,Short iphPhoSeq, Short convenioSus, Byte planoSus, Short cpgGrcSeq) {
		return vFatAssociacaoProcedimentoDAO.obterCodigoTabelaEDescricao(phiSeq, iphPhoSeq, convenioSus, planoSus, cpgGrcSeq);
	}	
	
	@Override
	public CidVO pesquisarCodigoDescricaoCidPorAghParametro(Integer conNumero,String[] parametros){
		return aghCidDAO.pesquisarCodigoDescricaoCidPorAghParametro(conNumero,parametros);
	}
	
	@Override
	public FatProcedHospInternos obterCodigoDescricaoProcedimentoProTransplante(Long ps7, Long ps8, Integer codigoPaciente){
		return fatProcedHospInternosDAO.obterCodigoDescricaoProcedimentoProTransplante(ps7, ps8, codigoPaciente);
	}

	@Override
	public AacConsultas obternomeEspecialidadeDataConsulta(final Integer numeroConsulta){
		return aacConsultasDAO.obternomeEspecialidadeDataConsulta(numeroConsulta);
	}
	
	@Override
	public FatConvGrupoItemProced obterCodigoTabelaDescricaoPorPhiSeq(Integer phiSeq) {
		return fatConvGrupoItensProcedDAO.obterCodigoTabelaDescricaoPorPhiSeq(phiSeq);
	}
	
	@Override
	public CidVO pesquisarJustificativaFoto(Integer conNumero){
		 return aghCidDAO.pesquisarJustificativaFoto(conNumero);
	}
	
	@Override
	public MamSolicitacaoRetorno obterMamSolicitacaoRetornoPorChavePrimaria(
			Long seq) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void atualizarIndImpressaoSolicitacaoRetorno(Long seq) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public String mamcTicketReceita(Integer pSorSeq, String pOrigem) {	
		return atendimentoPacientesAgendadosON.mamcTicketReceita(pSorSeq, pOrigem);
	}
	
	@Override
	public List<TransferirExamesVO> obterSolicitacoesExames(Integer numero){
		return  aelSolicitacaoExameDAO.obterSolicitacoesExames(numero);
	}
	
	@Override
	public String visualizarConsultaAtual(Integer conNumero) throws ApplicationBusinessException {
		return atendimentoPacientesAgendadosRN.visualizarConsultaAtual(conNumero);
	}
	
	@Override
	public List<ConsultasGradeVO> pesquisarConsultasPorGrade(ConsultasGradeVO filtro, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return aacConsultasDAO.pesquisarConsultasPorGrade(filtro, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public List<MamEvolucoes> pesquisarMamEvolucoesPaciente(Integer numero) {
		return mamEvolucoesDAO.pesquisarMamEvolucoesPaciente(numero);
	}
	
	@Override
	public List<MamTipoItemEvolucao> pesquisarTipoItemEvolucaoBotoes(Integer cagSeq) {
		return mamTipoItemEvolucaoDAO.pesquisarTipoItemEvolucaoBotoes(cagSeq);
	}
	
	@Override
	public List<MamItemEvolucoes> pesquisarItemEvolucaoPorEvolucaoTipoItem(Long evoSeq, Integer tieSeq) {
		return mamItemEvolucoesDAO.pesquisarItemEvolucaoPorEvolucaoTipoItem(evoSeq,tieSeq);
	}
	
	@Override
    public TipoProcedHospitalarInternoVO verificaIdadePaciente(Integer codigoPaciente, Date dataFim) throws ApplicationBusinessException{
        return consultasON.verificaIdadePaciente(codigoPaciente, dataFim);
    }
	public StringBuilder obterEmergenciaVisTriagemCon(Long pTrgSeq,
			String pModoVis) throws ApplicationBusinessException {
		return relatorioAnaEvoInternacaoRN.obterEmergenciaVisTriagem(pTrgSeq, pModoVis);
	}
	
	@Override
	public Boolean verificaColar() throws ApplicationBusinessException {
		return evolucaoON.verificaColar();
	}
	
	@Override
	public void gravarEvolucao(Integer conNumero,Long pEvoSeq,List<EvolucaoVO> listaBotoes) throws ApplicationBusinessException{
		evolucaoON.gravarEvolucao(conNumero,pEvoSeq,listaBotoes);
	}
	
	@Override
	public void gravarAtestado(MamAtestados atestado) throws ApplicationBusinessException{
		mamAtestadosRN.gravarAtestado(atestado);
	}
	
	@Override
	public void gravarOkEvolucao(Integer conNumero,Long pEvoSeq,List<EvolucaoVO> listaBotoes) throws ApplicationBusinessException{
		evolucaoON.gravarOkEvolucao(conNumero,pEvoSeq,listaBotoes);
	}	
	
	@Override
	public void excluirEvolucao(Integer conNumero,Long pEvoSeq, EvolucaoVO botaoSelecionado) throws ApplicationBusinessException{
		evolucaoON.excluirEvolucao(conNumero, pEvoSeq, botaoSelecionado);
	}	
	
	@Override
	public  void excluirAtestadoComparecimento(MamAtestados atestado){
		mamAtestadosRN.excluirAtestadoComparecimento(atestado);
	}		
	
	@Override
	public void gravarMotivoPendente(Integer conNumero,String motivoPendencia, String nomeMicrocomputador, Long evoSeq) throws ApplicationBusinessException{
		evolucaoON.gravarMotivoPendente(conNumero,motivoPendencia, nomeMicrocomputador, evoSeq);
	}

	@Override
	public void acaoFinalizarAtendimento(AacConsultas consulta) {
		mamAtestadosRN.acaoFinalizarAtendimento(consulta);
	}

	@Override
	public void acaoCancelarAtendimento(AacConsultas consulta) {
		mamAtestadosRN.acaoCancelarAtendimento(consulta);
	}

	 @Override
	public boolean reabrirConsulta(PesquisarConsultasPendentesVO consultaPendenteVO ,String nomeMicrocomputador ) throws ApplicationBusinessException{
		return atendimentoPacientesAgendadosRN.reabrirConsulta(consultaPendenteVO,nomeMicrocomputador);
	}

	@Override
	public void chamaPortal(PesquisarConsultasPendentesVO consultaPendenteVO,String pHostname) throws BaseException{
		atendimentoPacientesAgendadosRN.chamaPortal(consultaPendenteVO, pHostname);
	}
    
    @Override
	 public List<AacPagador> listarPagadoresAtivos(){
		return this.getAacPagadorDAO().listarPagadoresAtivos();	
	 }
@Override
public List<AghEspecialidades> obterEspecialidadesPorSiglaOUNomeEspecialidade(String parametro) {
	return ambulatorioRN.obterEspecialidadesPorSiglaOUNomeEspecialidade(parametro);
}

    /**
	 * #52025 - Consultas utilizadas em PACKAGE BODY MAMK_FUNCAO_EDICAO 
	 */
	@Override
	public DominioCor obterCurCorPacPorCodigo(Integer codigo) {
		return aipPacientesDAO.obterCurCorPacPorCodigo(codigo);
	}

	@Override
	public DominioSexo obterCurSexoPacPorCodigo(Integer codigo) {
		return aipPacientesDAO.obterCurSexoPacPorCodigo(codigo);
	}

	@Override
	public DominioGrauInstrucao obterCurGrauPacPorCodigo(Integer codigo) {
		return aipPacientesDAO.obterCurGrauPacPorCodigo(codigo);
	}

	@Override
	public String obterCurNacionalidadePorCodigo(Integer codigo) {
		return aipPacientesDAO.obterCurNacionalidadePorCodigo(codigo);
	}

	@Override
	public String obterCurProfissaoPorCodigo(Integer codigo) {
		return aipPacientesDAO.obterCurProfissaoPorCodigo(codigo);
	}

	@Override
	public String obterCurCidadePorCodigo(Integer codigo) {
		return aipPacientesDAO.obterCurCidadePorCodigo(codigo);
	}

@Override
public Long obterEspecialidadesPorSiglaOUNomeEspecialidadeCount(String parametro) {
	return ambulatorioRN.obterEspecialidadesPorSiglaOUNomeEspecialidadeCount(parametro);
}

	@Override
	public String obterNomePacientePorCodigoPac(Integer codigo) {
		return aipPacientesDAO.obterNomePacientePorCodigoPac(codigo);
	}

@Override
public List<VRapServidorConselho> obterVRapServidorConselhoPorNumConselhoOuNome(String parametro) {
	return vRapServidorConselhoDAO.obterVRapServidorConselhoPorNumConselhoOuNome(parametro);
}

	@Override
	public CursorPacVO obterCurPacPorCodigo(Integer codigo) {
		return aipPacientesDAO.obterCurPacPorCodigo(codigo);
	}

@Override
public Long obterVRapServidorConselhoPorNumConselhoOuNomeCount(String parametro) {
	return vRapServidorConselhoDAO.obterVRapServidorConselhoPorNumConselhoOuNomeCount(parametro);
}

	@Override
	public String obterCurPacNomePaiPorCodigo(Integer codigo) {
		return aipPacientesDAO.obterCurPacNomePaiPorCodigo(codigo);
	}

@Override
public List<FccCentroCustos> obterCentroCustoPorCodigoDescricao(String parametro) {
	return fccCentroCustosDAO.obterCentroCustoPorCodigoDescricao(parametro);
}

	@Override
	public String obterCurPacNomeMaePorCodigo(Integer codigo) {
		return aipPacientesDAO.obterCurPacNomeMaePorCodigo(codigo);
	}

@Override
public Long obterCentroCustoPorCodigoDescricaoCount(String parametro) {
	return fccCentroCustosDAO.obterCentroCustoPorCodigoDescricaoCount(parametro);
}

	@Override
	public List<AipEnderecoPacienteVO> obterAipEnderecoVOPorCodigo(Integer codigo) {
		return aipEnderecosPacientesDAO.obterAipEnderecoVOPorCodigo(codigo);
	}

@Override
public List<AghEquipes> obterEquipesPorSeqOuNome(String parametro) {
	return aghEquipesDAO.obterEquipesPorSeqOuNome(parametro);
}

@Override
public Long obterEquipesPorSeqOuNomeCount(String parametro) {
	return aghEquipesDAO.obterEquipesPorSeqOuNomeCount(parametro);
}

	@Override
	public Integer obterCddCodigoPorCodigo(Integer codigo) {
		return aipLogradourosDAO.obterCddCodigoPorCodigo(codigo);
	}

	 @Override
	public String obterNomeAipCidadesPorCodigo(Integer codigo) {
		return aipCidadesDAO.obterNomeAipCidadesPorCodigo(codigo);
	}
	
	@Override
	public String obterCurFuePorSeq(Short seq) {
		return mamFuncaoEdicaoDAO.obterCurFuePorSeq(seq);
	}
	
	@Override
		public void persistirMamAtestadoAmbulatorio(MamAtestados atestado)
				throws ApplicationBusinessException {
			this.mamAtestadosRN.persistirMamAtestados(atestado);
		}

		@Override
		public List<MamAtestados> listarAtestadosPorPacienteTipoAtendimento(
				Integer consulta, Short tasSeq) {
			return mamAtestadosDAO.listarAtestadosPorPacienteTipoAtendimento(consulta, tasSeq);
		}

		@Override
		public void excluirAtestadoFgtsPis(MamAtestados atestado)
				throws ApplicationBusinessException {
			this.mamAtestadosRN.excluirAtestadoFgtsPis(atestado);
		}
	 

	@Override
	public MamTipoAtestado obterTipoAtestadoOriginal(Short seqTipo) {
		return mamTipoAtestadosDAO.obterOriginal(seqTipo);
	}

	@Override
	public LaudoSolicitacaoAutorizacaoProcedAmbVO preencheCamposdaJustificativaDoProcedSolicitado(Integer numeroConsulta) throws ApplicationBusinessException {
		return consultasON.preencheCamposdaJustificativaDoProcedSolicitado(numeroConsulta);
	}
@Override
public List<RelatorioProgramacaoGradeVO> obterRelatorioProgramacaoGrade(
		Date dataInicio, Date dataFim, Integer grade, String sigla,
		Integer seqEquipe, Integer servico, VRapServidorConselho conselho) {
	return relatorioProgramacaoGradeON.obterRelatorioProgramacaoGrade(dataInicio, dataFim, grade, sigla, seqEquipe, servico, conselho);
}
	
	@Override
	public String obterCSVProgramacaoGrade(Date dataInicio, Date dataFim, Integer grade, String sigla,
		Integer seqEquipe, Integer servico, VRapServidorConselho conselho) 
		throws IOException, ApplicationBusinessException {
		return relatorioProgramacaoGradeON.obterCSVProgramacaoGrade(dataInicio, dataFim, grade, sigla, seqEquipe, servico, conselho);
	}
	
	@Override
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterCidOtorrinoNumeroConsulta(Integer numeroConsulta) {
		return fatCandidatosApacOtorrinoDAO.obterCidOtorrinoNumeroConsulta(numeroConsulta);
	}

	@Override
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterNomeCPFProfissResponsavel(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO, Integer numeroConsulta) {
		return fatCandidatosApacOtorrinoDAO.obterNomeCPFProfissResponsavel(laudoSolicitacaoAutorizacaoProcedAmbVO, numeroConsulta);
	}
	
	@Override
	public void validarHoraInicioFimAtestado(Date horaInicio, Date horaFim) throws ApplicationBusinessException {
		mamAtestadosRN.validarHoraInicioFimAtestado(horaInicio, horaFim);
	}

	@Override
	public void validarCamposPreenchidosAtestadoComparecimento(MamAtestados atestado) throws ApplicationBusinessException {
		mamAtestadosRN.validarCamposPreenchidosAtestadoComparecimento(atestado);
	}
	
	@Override
	public BigDecimal obterParametroAtestadoAcompanhamento() throws ApplicationBusinessException{
		return mamAtestadosRN.obterParametroAtestadoAcompanhamento();
	}

	@Override
	public List<String> validarCamposAtestadoAcompanhamento(MamAtestados atestado) {
		return mamAtestadosRN.validarCamposAtestadoAcompanhamento(atestado);
	}

	@Override
	public BigDecimal obterParametroAtestadoAtestadoMedico() throws ApplicationBusinessException {
		return  mamAtestadosRN.obterParametroAtestadoMedico();
	}

	@Override
	public void validarCamposPreenchidosAtestadoMedico(MamAtestados atestado) throws ApplicationBusinessException {
		mamAtestadosRN.validarCamposPreenchidosAtestadoMedico(atestado);
	}

	@Override
	public BigDecimal obterParametroAtestadoComparecimento() throws ApplicationBusinessException {
		return mamAtestadosRN.obterParametroAtestadoComparecimento();
	}

	@Override
	public List<MamAtestados> obterAtestadosDaConsulta(AacConsultas consulta, short tasSeq) {
		return mamAtestadosDAO.obterAtestadosDaConsulta(consulta, tasSeq);
	}

	@Override
	public List<MamAtestados> obterAtestadosDaConsultaComCid(AacConsultas consulta, short tasSeq){
		return mamAtestadosDAO.obterAtestadosDaConsultaComCid(consulta, tasSeq);
	}
	
	@Override
	public List<ReceitasGeralEspecialVO> gerarDados(Integer pacCodigo) {
		return aghAtendimentosDAO.gerarDados(pacCodigo);
	}
	
	@Override
	public VFatAssociacaoProcedimento obterDescricaoPorCodTabela(Long codTabela) {
		return vFatAssociacaoProcedimentoDAO.obterDescricaoPorCodTabela(codTabela);
	}
	
	@Override
	public BigDecimal obterParametroRenovacaoReceita()	throws ApplicationBusinessException {
		return mamAtestadosRN.obterParametroRenovacaoReceita();
	}

	@Override
	public void validarCamposPreenchidosRenovacaoReceita(MamAtestados atestado)	throws ApplicationBusinessException {
		mamAtestadosRN.validarCamposPreenchidosRenovacaoReceita(atestado);
	}
	
	@Override
	public List<TDataVO> pGeraDadosData(Integer pPacCodigo) {
		return receitasRN.pGeraDadosData(pPacCodigo);
	}
	
	@Override
	public List<TDataVO> pGerarDadosEspecialidade(Integer pPacCodigo){
		return receitasRN.pGerarDadosEspecialidade(pPacCodigo);
	}

	@Override
	public Boolean validarValorMinimo(MamAtestados atestado) throws ApplicationBusinessException {
		return mamAtestadosRN.validarValorMinimo(atestado);
	}
	
	@Override
	public Boolean validarValorMinimoPeriodo(MamAtestados atestado) throws ApplicationBusinessException {
		return mamAtestadosRN.validarValorMinimoPeriodo(atestado);
	}

	@Override
	public List<VMamReceitas> obterListaVMamReceitas(Integer pacCodigo,
			Integer conNumero, Integer atdSeq, Date dtCriacao,
			DominioTipoReceituario dominioTipoReceituario) {
		return vMamReceitasDAO.obterListaVMamReceitas(pacCodigo, conNumero, atdSeq, dtCriacao, dominioTipoReceituario);
	}

	@Override
	public List<MamReceituarios> obterListaSeqTipoMamReceituarios(Integer atdSeq, Integer apaAtdSeq, Integer apaSeq, Integer seqp,
			Long trgSeq, Long rgtSeq, Integer conNumero,DominioTipoReceituario dominioTipoReceituario) {
		return mamReceituariosDAO.obterListaSeqTipoMamReceituarios(atdSeq, apaAtdSeq, apaSeq, seqp, trgSeq, rgtSeq, conNumero, dominioTipoReceituario);
	}

	@Override
	public Short obterValorMaxSeqP(Long rctSeq) {
		return mamItemReceituarioDAO.obterValorMaxSeqP(rctSeq);
	}

	@Override
	public void gravarReceitas(Integer conNumero, VMamReceitas registro,
			MamReceituarios receituario,DominioTipoReceituario dominioTipoReceituario)throws ApplicationBusinessException {
		receitasRN.gravarReceitas(conNumero, registro, receituario, dominioTipoReceituario);
	}

	@Override
	public AacConsultas buscaConsultaAnterior(AacConsultas consulta) throws ApplicationBusinessException {
		return atendimentoPacientesAgendadosRN.buscaConsultaAnterior(consulta);
	}
	
	@Override
	public AipPacientes obterPacienteOriginal(Integer codigo){
		return aipPacientesDAO.obterOriginal(codigo);
	}
	
	@Override
	public List<MamLembrete> obterResumoDeCaso(Integer numero) {
		return mamLembreteDAO.obterResumoDeCaso(numero);
	}
	
	@Override
	public VRapServidorConselho obterRapServidorConselhoOriginal(VRapServidorConselhoId id){
		return vRapServidorConselhoDAO.obterOriginal(id);
	}
	
	@Override
	public Integer obterUltimaConsultaGestantePorPaciente(Integer pacCodigo){
		return aghAtendimentosDAO.obterUltimaConsultaGestantePorPaciente(pacCodigo);
	}
	
	public Boolean verificarConsultaJaMarcada(AacConsultas consulta){
		return marcacaoConsultaON.verificarConsultaJaMarcada(consulta);
	}
	@Override
	public MamValorValidoQuestao obterSeqValorSituacaoMamValValidoQuestao(Integer qusQutSeq, Short qusSeqP) {
		return mamValorValidoQuestaoDAO.obterSeqValorSituacaoMamValValidoQuestao(qusQutSeq, qusSeqP);
	}

	@Override
	public EspecialidadeRelacionadaVO obterDadosEspecialidadesRelacionadoAConsulta(Integer conNumero) {
		return aacConsultasDAO.obterDadosEspecialidadesRelacionadoAConsulta(conNumero);
	}

	@Override
	public List<PreGeraItemQuestVO> pPreGeraItemQuest(Long pEvoSeq, Short espSeq, Integer pTieSeq, Integer pPacCodigo, String indTipoPac) {
		return mamQuestionarioRN.pPreGeraItemQuest(pEvoSeq, espSeq, pTieSeq, pPacCodigo, indTipoPac);
	}

	@Override
	public String mamCGetTipoPac(Integer conNumero) {
		return mamQuestionarioRN.mamCGetTipoPac(conNumero);
	}

	@Override
	public GeraEvolucaoVO geraEvolucao(Integer pConNumero, Long pParameterEvoSeq) throws ApplicationBusinessException {
		return evolucaoON.geraEvolucao(pConNumero, pParameterEvoSeq);
	}

	@Override
	public String mamcExecFncEdicao(Short fueSeq, Integer codigo) {
		return evolucaoON.mamcExecFncEdicao(fueSeq, codigo);
	}

	public EvolucaoNegarRN getEvolucaoNegarRN() {
		return evolucaoNegarRN;
	}
	
	@Override
	public List<AacConsultas> verificarConsultaPossuiReconsultasVinculadas(Integer numeroConsulta) {
		return getAacConsultasDAO().pesquisarConsultacPorConsultaNumeroAnterior(numeroConsulta);
	}	

	
	@Override
	public List<PreGeraItemQuestVO> obterListaPreGeraItemQuestVO(Long evoSeq, Integer tieSeq, String indTipoPac) {
		return mamRespostaEvolucoesDAO.obterListaPreGeraItemQuestVO(evoSeq, tieSeq, indTipoPac);
	}

	@Override
	public void excluirRespostaEItemEvolucao(Long evoSeq) {
		this.evolucaoON.excluirRespostaEItemEvolucao(evoSeq);
	}
	
	@Override
	public List<AghAtendimentos> localizarAtendimentoPorPaciente(
			Integer codigoPaciente) {
		return aghAtendimentosDAO.localizarAtendimentoPorPaciente(codigoPaciente);
	}
	
	@Override
	public List<MamRegistro> obterRegistroAnamnesePorAtendSeqCriadoEm(
			Integer atendimentoSeq, Date criadoEm) {
		
		return mamRegistroDAO.obterRegistroAnamnesePorAtendSeqCriadoEm(atendimentoSeq, criadoEm);
	}

	@Override
	public List<MamRegistro> obterRegistroEvolucoesPorAtendSeqCriadoEm(
			Integer atendimentoSeq, Date criadoEm) {
		
		return mamRegistroDAO.obterRegistroEvolucoesPorAtendSeqCriadoEm(atendimentoSeq, criadoEm);
	}

	@Override
	public String obterAnamnesePorMamRegistroSeq(Long rgtSeq, Integer categoriaProfSeq) throws ApplicationBusinessException{
		return mamAnamneseRN.obterAnamnesePorMamRegistroSeq(rgtSeq, categoriaProfSeq);
	}	
	
	@Override
	public CseCategoriaProfissional obterCategoriaPorSeq(Integer seqCat){
		return cseCategoriaProfissionalDAO.obterPorChavePrimaria(seqCat);
	}
	
	@Override
	public List<MamDiagnostico> listarDiagnosticosPortalPacienteInternado(Integer pacCodigo, Date dataFiltro) {
		return mamDiagnosticoDAO.listarDiagnosticosPortalPacienteInternado(pacCodigo, dataFiltro);
	}

	@Override
	public void excluirDiagnostico(MamDiagnostico mamDiagnostico) throws ApplicationBusinessException {
		listarDiagnosticosAtivosPacienteON.excluir(mamDiagnostico);
	}
	
	@Override
	public void obterListaDocumentosPacienteAtestados(Integer conNumero,
			List<DocumentosPacienteVO> listaDocumentos)
			throws ApplicationBusinessException {
		this.atendimentoPacientesAgendadosON.obterListaDocumentosPacienteAtestados(conNumero, listaDocumentos);
	}
	
	@Override
	public void obterListaReceituarioCuidado(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos, Short espSeq, Boolean verificarProcesso) throws ApplicationBusinessException {
		this.atendimentoPacientesAgendadosON.obterListaReceituarioCuidado(conNumero, listaDocumentos, espSeq, verificarProcesso);
	}

	@Override
	public BigDecimal obterParametroAtestadoMarcacao() throws ApplicationBusinessException{
		return mamAtestadosRN.obterParametroAtestadoMarcacao();
	}

	@Override
	public void validarValorMinimoNumeroVias(MamAtestados atestado)	throws ApplicationBusinessException {
		mamAtestadosRN.validarValorMinimoNumeroVias(atestado);
	}
	

	@Override
	public List<MamAnamneses> pesquisarMamAnamnesesPaciente(Integer numero) {
		return mamAnamnesesDAO.pesquisarMamAnamnesesPaciente(numero);
	}

	@Override
	public List<MamTipoItemAnamneses> pesquisarTipoItemAnamneseBotoes(Integer cagSeq) {
		return mamTipoItemAnamnesesDAO.pesquisarTipoItemAnamneseBotoes(cagSeq);
	}

	@Override
	public List<MamItemAnamneses> pesquisarItemAnamnesePorAnamneseTipoItem(Long anaSeq, Integer tiaSeq) {
		return mamItemAnamnesesDAO.pesquisarItemAnamnesesPorAnamnesesTipoItem(anaSeq, tiaSeq);
	}

	@Override
	public void gravarAnamnese(Integer conNumero, Long anaSeq, List<AnamneseVO> listaBotoes) throws ApplicationBusinessException {
		anamneseRN.gravarAnamnese(conNumero, anaSeq, listaBotoes);
	}

	@Override
	public void excluirAnamnese(Integer conNumero, Long anaSeq) throws ApplicationBusinessException {
		anamneseRN.excluirAnamnese(conNumero, anaSeq);
	}
	
	@Override
	public void gravarAnamneseMotivoPendente(Integer conNumero, Long anaSeq, String motivoPendencia, String nomeMicrocomputador) throws ApplicationBusinessException {
		anamneseRN.gravarAnamneseMotivoPendente(conNumero, anaSeq, motivoPendencia, nomeMicrocomputador);
	}

	@Override
	public MamReceituarioCuidado mamReceituarioCuidadoPorNumeroConsulta(Integer numeroConsulta,DominioIndPendenteAmbulatorio pendente) {
		return mamReceituarioCuidadoDAO.mamReceituarioCuidadoPorNumeroConsulta(numeroConsulta, pendente);
	}
			
	@Override
	public MamReceituarioCuidado obterUltimoMamReceituarioCuidadoPorNumeroConsulta(Integer numeroConsulta) {
		return mamReceituarioCuidadoDAO.obterUltimoMamReceituarioCuidadoPorNumeroConsulta(numeroConsulta);
	}
	
	@Override
	public List<MamItemReceitCuidado> listarMamItensReceituarioCuidadoPorNumeroConsulta(Integer numeroConsulta) {
		return mamReceituarioCuidadoDAO.listarMamItensReceituarioCuidadoPorNumeroConsulta(numeroConsulta);				
	}
	
	@Override
	public MamReceituarioCuidado verificaRequisitosReceituarioCuidado(AacConsultas consulta){
		return cuidadoPacienteRN.verificaRequisitosReceituarioCuidado(consulta);
	}
	
	@Override
	public void adicionarEditarMamItemReceitCuidado(MamReceituarioCuidado receituarioAtual,MamReceituarioCuidado receituarioAnterior,MamItemReceitCuidado itemNovo,AacConsultas consulta){
		 cuidadoPacienteRN.adicionarEditarMamItemReceitCuidado(receituarioAtual, receituarioAnterior, itemNovo,consulta);
	}
	
	@Override
	public void excluirmamItemReceitCuidado(MamItemReceitCuidado item){
		cuidadoPacienteRN.excluirmamItemReceitCuidado(item);
	}
	
	@Override
	public void procedimentosReceituarioCuidadoFinalizarAtendimento(Integer numeroConsulta,MamReceituarioCuidado receituarioAtenrior){
		cuidadoPacienteRN.procedimentosReceituarioCuidadoFinalizarAtendimento(numeroConsulta,receituarioAtenrior);
	}
	
	@Override
	public void procedimentosReceituarioCuidadoCancelarAtendimento(Integer numeroConsulta,MamReceituarioCuidado anterior){
		cuidadoPacienteRN.procedimentosReceituarioCuidadoCancelarAtendimento(numeroConsulta,anterior);
	}
	
	@Override
	public List<MamRecCuidPreferidoVO> listarCuidadosPreferidos(RapServidores servidor,boolean ativo) {
		return mamRecCuidPreferidoDAO.listarCuidadosPreferidos(servidor, ativo);
	}
	
	@Override
	public void selecionarCuidadosEntrePreferidosUsuario(List<MamRecCuidPreferidoVO> listaMamRecCuidPreferido,
			MamReceituarioCuidado receituarioAtual,AacConsultas consulta){
		cuidadoPacienteRN.selecionarCuidadosEntrePreferidosUsuario(listaMamRecCuidPreferido, receituarioAtual, consulta);
	}
	
	@Override
	public void copiaCuidadoPreferidosOutroUsuario(VMamDiferCuidServidores servidorOrigem,RapServidores servidorLogado){
		cuidadoPacienteRN.copiaCuidadoPreferidosOutroUsuario(servidorOrigem, servidorLogado);
	}
	
	@Override
	public List<VMamDiferCuidServidores> pesquisarVMamDiferCuidServidores(RapServidores servidorLogado,String nomeMatriculaVincodigo) {
		return vMamDiferCuidServidoresDAO.pesquisarVMamDiferCuidServidores(servidorLogado,nomeMatriculaVincodigo);
		
	}
	@Override
	public Long countVMamDiferCuidServidores(RapServidores servidorLogado,String nomeMatriculaVincodigo) {
			return vMamDiferCuidServidoresDAO.countVMamDiferCuidServidores(servidorLogado,nomeMatriculaVincodigo);	
	}
	
	@Override
	public VMamPessoaServidores obterVMamPessoaServidores(RapServidores servidores) {
		return vMamPessoaServidoresDAO.obterVMamPessoaServidores(servidores);
	}

	@Override
	public AghAtendimentos buscarAtendimentoPossuiMesmoLeitoUnidFuncional(AghAtendimentos atendimento,AghUnidadesFuncionais unidadeFuncional) {
		return aghAtendimentosDAO.buscarLeitoPorUnidFuncional(atendimento, unidadeFuncional);
	}
}