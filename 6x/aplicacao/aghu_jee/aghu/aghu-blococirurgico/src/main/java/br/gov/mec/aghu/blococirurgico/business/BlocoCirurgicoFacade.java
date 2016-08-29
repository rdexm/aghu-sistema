package br.gov.mec.aghu.blococirurgico.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.AghWFExecutorDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestRegionalNeuroeixosDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAvalPreSedacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCidUsualEquipeDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiaAnotacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoItensDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoPadraoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoTecnicasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDestinoPacienteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDiagnosticoDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEscalaProfUnidCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEspecialidadeProcCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaAnestesiaRegionalDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaAnestesiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaBloqNervoPlexosDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaEquipeAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaEspecifIntubacoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaExameDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaFarmacoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaFinalDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaFluidoAdministradosDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaFluidoPerdidoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaGasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaGrandePorteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaInducaoManutencaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaInicialDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaMaterialDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaMedMonitorizacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaNeonatologiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaOrgaoTransplanteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaPacienteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaPosicionamentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaProcedimentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaTecnicaEspecialDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaTipoAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaVentilacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaViaAereaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFiguraDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoAlcadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoProcedCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcImagemDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcItensRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMatOrteseProtCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMateriaisItemOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMonitorizacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMotivoCancelamentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMotivoDemoraSalaRecDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcNeuroeixoNvlPuncionadosDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcNotaAdicionaisDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcOcorrFichaFluidoAdmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcOcorrenciaFichaEventosDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcOcorrenciaFichaFarmacoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcOcorrenciaFichaGasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcQuestaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoCirurgiaPosEscalaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoEspExecCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTipoAnestesiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTmpFichaFarmacoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcValorValidoCancDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtViaAereasDAO;
import br.gov.mec.aghu.blococirurgico.dao.VAinServInternaDAO;
import br.gov.mec.aghu.blococirurgico.dao.VMbcProcEspDAO;
import br.gov.mec.aghu.blococirurgico.opmes.business.OPMEPortalAgendamentoRN;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.HistoricoAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PesquisaAgendarProcedimentosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoTurnosSalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RegimeProcedimentoAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.TempoSalaAgendaVO;
import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.AvalOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaCodigoProcedimentoSusVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaComPacEmTransOperatorioVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaAnestesiaVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProfissionalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaMateriaisConsumidosVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.EspecialidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.ExecutorEtapaAtualVO;
import br.gov.mec.aghu.blococirurgico.vo.FichaPreOperatoriaVO;
import br.gov.mec.aghu.blococirurgico.vo.MamLaudoAihVO;
import br.gov.mec.aghu.blococirurgico.vo.MaterialPorCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcEquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcFichaTipoAnestesiaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcProcEspPorCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcProfAtuaUnidCirgsVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcRelatCirurRealizPorEspecEProfVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaConcluidaHojeVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaPreparoVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaRecuperacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.blococirurgico.vo.PacientesCirurgiaUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.PacientesEmListaDeProcedimentosCanceladosVO;
import br.gov.mec.aghu.blococirurgico.vo.PacientesEmSalaRecuperacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.PendenciaWorkflowVO;
import br.gov.mec.aghu.blococirurgico.vo.PesquisarPacientesCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedRealizadoVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentoCirurgicoPacienteVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentosCirurgicosPdtAtivosVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.ProtocoloEntregaNotasDeConsumoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiaComRetornoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasIndicacaoExamesVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasListaEsperaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasPendenteRetornoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasReservaHemoterapicaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioControleChamadaPacienteVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioEtiquetasIdentificacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioLaudoAIHSolicVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioLaudoAIHVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioNotasDeConsumoDaSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesComCirurgiaPorUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesEntrevistarVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProcedAgendPorUnidCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProdutividadePorAnestesistaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProfissionaisUnidadeCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioRegistroDaNotaDeSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.RequerenteVO;
import br.gov.mec.aghu.blococirurgico.vo.RequisicoesOPMEsProcedimentosVinculadosVO;
import br.gov.mec.aghu.blococirurgico.vo.RetornoCirurgiaEmLotePesquisaVO;
import br.gov.mec.aghu.blococirurgico.vo.RetornoCirurgiaEmLoteVO;
import br.gov.mec.aghu.blococirurgico.vo.SolicitarReceberOrcMatNaoLicitadoVO;
import br.gov.mec.aghu.blococirurgico.vo.SuggestionListaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.TelaListaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.blococirurgico.vo.VAghUnidFuncionalVO;
import br.gov.mec.aghu.blococirurgico.vo.VisualizaCirurgiaCanceladaVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.controleinfeccao.vo.ProcedimentoCirurgicoVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioClassificacaoDiagnostico;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioGrupoMbcPosicionamento;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamento;
import br.gov.mec.aghu.dominio.DominioOcorrFichaFluido;
import br.gov.mec.aghu.dominio.DominioOcorrenciaFichaEvento;
import br.gov.mec.aghu.dominio.DominioOrdenacaoProtocoloEntregaNotasConsumo;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoExame;
import br.gov.mec.aghu.dominio.DominioStatusRelatorio;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.dominio.DominioTipoEventoMbcPrincipal;
import br.gov.mec.aghu.dominio.DominioTipoFluidoAdministrado;
import br.gov.mec.aghu.dominio.DominioTipoFluidoPerdido;
import br.gov.mec.aghu.dominio.DominioTipoInducaoManutencao;
import br.gov.mec.aghu.dominio.DominioTipoObrigatoriedadeOpms;
import br.gov.mec.aghu.dominio.DominioTipoOcorrenciaFichaFarmaco;
import br.gov.mec.aghu.dominio.DominioTipoPendenciaCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.exames.vo.MbcCirurgiaVO;
import br.gov.mec.aghu.faturamento.vo.CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.model.MbcDestinoPaciente.Fields;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.CirurgiasInternacaoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemMonitorizacaoDefinidoFichaAnestVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PlanejamentoCirurgicoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.paciente.vo.EscalaCirurgiasVO;
import br.gov.mec.aghu.paciente.vo.HistoricoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.dao.VRapServidorConselhoDAO;
import br.gov.mec.aghu.registrocolaborador.vo.AgendaProcedimentoPesquisaProfissionalVO;

/**
 * Porta de entrada do módulo Bloco Cirúrgico.
 * 
 * @author Ricardo Costa
 * 
 */

@SuppressWarnings({"PMD.CouplingBetweenObjects","PMD.ExcessiveClassLength", "PMD.NcssTypeCount"})
@Modulo(ModuloEnum.BLOCO_CIRURGICO)
@Stateless
public class BlocoCirurgicoFacade extends BaseFacade implements IBlocoCirurgicoFacade {

	@Inject
	private MbcDescricaoTecnicasDAO mbcDescricaoTecnicasDAO;

	@Inject
	private MbcMatOrteseProtCirgDAO mbcMatOrteseProtCirgDAO;

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcFichaAnestesiasDAO mbcFichaAnestesiasDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcFichaTipoAnestesiaDAO mbcFichaTipoAnestesiaDAO;

	@Inject
	private MbcNotaAdicionaisDAO mbcNotaAdicionaisDAO;

	@Inject
	private MbcProfDescricoesDAO mbcProfDescricoesDAO;

	@Inject
	private MbcNeuroeixoNvlPuncionadosDAO mbcNeuroeixoNvlPuncionadosDAO;

	@Inject
	private MbcQuestaoDAO mbcQuestaoDAO;

	@Inject
	private MbcGrupoAlcadaDAO mbcGrupoAlcadaDAO;

	@Inject
	private MbcFichaNeonatologiaDAO mbcFichaNeonatologiaDAO;

	@Inject
	private MbcFichaBloqNervoPlexosDAO mbcFichaBloqNervoPlexosDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;

	@Inject
	private MbcOcorrenciaFichaGasDAO mbcOcorrenciaFichaGasDAO;

	@Inject
	private MbcImagemDescricoesDAO mbcImagemDescricoesDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcTipoAnestesiasDAO mbcTipoAnestesiasDAO;

	@Inject
	private MbcFichaProcedimentoDAO mbcFichaProcedimentoDAO;

	@Inject
	private MbcFichaEquipeAnestesiaDAO mbcFichaEquipeAnestesiaDAO;

	@Inject
	private MbcAgendaHemoterapiaDAO mbcAgendaHemoterapiaDAO;

	@Inject
	private MbcFichaMedMonitorizacaoDAO mbcFichaMedMonitorizacaoDAO;

	@Inject
	private MbcFiguraDescricoesDAO mbcFiguraDescricoesDAO;

	@Inject
	private MbcValorValidoCancDAO mbcValorValidoCancDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcFichaFarmacoDAO mbcFichaFarmacoDAO;

	@Inject
	private MbcFichaInicialDAO mbcFichaInicialDAO;

	@Inject
	private MbcSolicitacaoEspExecCirgDAO mbcSolicitacaoEspExecCirgDAO;

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;

	@Inject
	private MbcFichaExameDAO mbcFichaExameDAO;
	
	@Inject
	private MbcAvalPreSedacaoDAO mbcAvalPreSedacaoDAO;

	@Inject
	private MbcItensRequisicaoOpmesDAO mbcItensRequisicaoOpmesDAO;

	@Inject
	private MbcMonitorizacaoDAO mbcMonitorizacaoDAO;

	@Inject
	private MbcMotivoCancelamentoDAO mbcMotivoCancelamentoDAO;

	@Inject
	private MbcDescricaoItensDAO mbcDescricaoItensDAO;

	@Inject
	private MbcFichaGrandePorteDAO mbcFichaGrandePorteDAO;

	@Inject
	private VMbcProcEspDAO vMbcProcEspDAO;

	@Inject
	private MbcSolicitacaoCirurgiaPosEscalaDAO mbcSolicitacaoCirurgiaPosEscalaDAO;

	@Inject
	private MbcFichaAnestesiaRegionalDAO mbcFichaAnestesiaRegionalDAO;

	@Inject
	private MbcFichaFinalDAO mbcFichaFinalDAO;

	@Inject
	private MbcEscalaProfUnidCirgDAO mbcEscalaProfUnidCirgDAO;

	@Inject
	private MbcFichaTecnicaEspecialDAO mbcFichaTecnicaEspecialDAO;

	@Inject
	private MbcTmpFichaFarmacoDAO mbcTmpFichaFarmacoDAO;

	@Inject
	private MbcFichaEspecifIntubacoesDAO mbcFichaEspecifIntubacoesDAO;

	@Inject
	private MbcFichaFluidoPerdidoDAO mbcFichaFluidoPerdidoDAO;

	@Inject
	private MbcAnestRegionalNeuroeixosDAO mbcAnestRegionalNeuroeixosDAO;

	@Inject
	private MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@Inject
	private MbcAnestesiaDescricoesDAO mbcAnestesiaDescricoesDAO;

	@Inject
	private MbcOcorrenciaFichaFarmacoDAO mbcOcorrenciaFichaFarmacoDAO;

	@Inject
	private MbcOcorrenciaFichaEventosDAO mbcOcorrenciaFichaEventosDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;

	@Inject
	private AghWFExecutorDAO aghWFExecutorDAO;

	@Inject
	private MbcFichaGasDAO mbcFichaGasDAO;

	@Inject
	private MbcEquipamentoCirurgicoDAO mbcEquipamentoCirurgicoDAO;

	@Inject
	private MbcFichaPosicionamentoDAO mbcFichaPosicionamentoDAO;

	@Inject
	private MbcProcDescricoesDAO mbcProcDescricoesDAO;

	@Inject
	private MbcFichaVentilacaoDAO mbcFichaVentilacaoDAO;

	@Inject
	private VAinServInternaDAO vAinServInternaDAO;

	@Inject
	private MbcMateriaisItemOpmesDAO mbcMateriaisItemOpmesDAO;

	@Inject
	private MbcFichaPacienteDAO mbcFichaPacienteDAO;

	@Inject
	private MbcExtratoCirurgiaDAO mbcExtratoCirurgiaDAO;

	@Inject
	private MbcDiagnosticoDescricaoDAO mbcDiagnosticoDescricaoDAO;

	@Inject
	private MbcCirurgiaAnotacaoDAO mbcCirurgiaAnotacaoDAO;

	@Inject
	private MbcDescricaoPadraoDAO mbcDescricaoPadraoDAO;

	@Inject
	private MbcFichaInducaoManutencaoDAO mbcFichaInducaoManutencaoDAO;

	@Inject
	private MbcMotivoDemoraSalaRecDAO mbcMotivoDemoraSalaRecDAO;

	@Inject
	private MbcFichaOrgaoTransplanteDAO mbcFichaOrgaoTransplanteDAO;

	@Inject
	private MbcOcorrFichaFluidoAdmsDAO mbcOcorrFichaFluidoAdmsDAO;

	@Inject
	private MbcCidUsualEquipeDAO mbcCidUsualEquipeDAO;

	@Inject
	private MbcDestinoPacienteDAO mbcDestinoPacienteDAO;

	@Inject
	private MbcFichaViaAereaDAO mbcFichaViaAereaDAO;

	@Inject
	private MbcFichaFluidoAdministradosDAO mbcFichaFluidoAdministradosDAO;

	@Inject
	private MbcEspecialidadeProcCirgsDAO mbcEspecialidadeProcCirgsDAO;

	@Inject
	private MbcFichaMaterialDAO mbcFichaMaterialDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;

	@Inject
	private VRapServidorConselhoDAO vRapServidorConselhoDAO;
	
	@Inject 
	private MbcGrupoProcedCirurgicoDAO mbcGrupoProcedCirurgicoDAO; 

	@EJB
	private MbcEquipamentoUtilCirgRN mbcEquipamentoUtilCirgRN;

	@EJB
	private MbcControleEscalaCirurgicaRN mbcControleEscalaCirurgicaRN;
	
	@EJB
	private MbcControleEscalaCirurgicaON mbcControleEscalaCirurgicaON;

	@EJB
	private ExtratoCirurgiaON extratoCirurgiaON;

	@EJB
	private MonitorCirurgiaON monitorCirurgiaON;

	@EJB
	private PacientesEmSalaRecuperacaoON pacientesEmSalaRecuperacaoON;

	@EJB
	private DiagnosticoDescricaoON diagnosticoDescricaoON;

	@EJB
	private AcompanhamentoCirurgiaON acompanhamentoCirurgiaON;

	@EJB
	private DescricaoCirurgicaAchadosOperatoriosON descricaoCirurgicaAchadosOperatoriosON;

	@EJB
	private MbcCirurgiasRN mbcCirurgiasRN;

	@EJB
	private RelatorioCirurgiasListaEsperaON relatorioCirurgiasListaEsperaON;

	@EJB
	private MbcAgendasON mbcAgendasON;

	@EJB
	private MbcAgendaProcedimentoON mbcAgendaProcedimentoON;

	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;
	
	@EJB
	private AvalPreSedacaoRN AvalPreSedacaoRN;

	@EJB
	private AcompanharProcessoAutorizacaoOPMsRN acompanharProcessoAutorizacaoOPMsRN;

	@EJB
	private MbcAgendaHemoterapiaON mbcAgendaHemoterapiaON;

	@EJB
	private DescricaoItensON descricaoItensON;

	@EJB
	private VerificaColisaoDigitacaoNSON verificaColisaoDigitacaoNSON;

	@EJB
	private RelatorioEtiquetasIdentificacaoON relatorioEtiquetasIdentificacaoON;

	@EJB
	private MbcNotaAdicionaisRN mbcNotaAdicionaisRN;

	@EJB
	private RelatorioControleChamadaPacienteON relatorioControleChamadaPacienteON;

	@EJB
	private AnestesiaDescricoesRN anestesiaDescricoesRN;

	@EJB
	private RelatorioCirExpoRadIonON relatorioCirExpoRadIonON;

	@EJB
	private RelatorioNotasDeConsumoDaSalaON relatorioNotasDeConsumoDaSalaON;

	@EJB
	private DetalhaRegistroCirurgiaON detalhaRegistroCirurgiaON;

	@EJB
	private ProtocoloEntregaNotasDeConsumoON protocoloEntregaNotasDeConsumoON;

	@EJB
	private DescricaoCirurgicaEquipeON descricaoCirurgicaEquipeON;

	@EJB
	private BlocoCirurgicoON blocoCirurgicoON;

	@EJB
	private RelatorioCirurgiasPendenteRetornoON relatorioCirurgiasPendenteRetornoON;

	@EJB
	private MbcProcEspPorCirurgiasRN mbcProcEspPorCirurgiasRN;

	@EJB
	private RelatorioProcedAgendPorUnidCirurgicaON relatorioProcedAgendPorUnidCirurgicaON;

	@EJB
	private AgendaProcedimentosFuncoesON agendaProcedimentosFuncoesON;
	
	@EJB
	private RelatorioAvaliacaoPreAnestesicaON relatorioAvaliacaoPreAnestesicaON;

	@EJB
	private MbcSolicitacaoEspExecCirgRN mbcSolicitacaoEspExecCirgRN;

	@EJB
	private RelatorioProfissionaisUnidadeCirurgicaON relatorioProfissionaisUnidadeCirurgicaON;

	@EJB
	private MbcCirurgiasON mbcCirurgiasON;

	@EJB
	private RelatCirurRealizPorEspecEProfON relatCirurRealizPorEspecEProfON;

	@EJB
	private MbcEquipamentoCirurgicoON2 mbcEquipamentoCirurgicoON;

	@EJB
	private RelatorioEscalaCirurgicaON relatorioEscalaCirurgicaON;

	@EJB
	private RelatorioProdutividadePorAnestesistaON relatorioProdutividadePorAnestesistaON;

	@EJB
	private ListaCirurgiasDescCirurgicaComplON listaCirurgiasDescCirurgicaComplON;

	@EJB
	private RelatorioPacientesComCirurgiaPorUnidadeON relatorioPacientesComCirurgiaPorUnidadeON;

	@EJB
	private MbcAnestesiaCirurgiasRN mbcAnestesiaCirurgiasRN;

	@EJB
	private ProfDescricoesRN profDescricoesRN;

	@EJB
	private AgendaProcedimentosRN agendaProcedimentosRN;

	@EJB
	private MbcMatOrteseProtCirgRN mbcMatOrteseProtCirgRN;

	@EJB
	private RelatorioCirurgiaProcedProfissionalON relatorioCirurgiaProcedProfissionalON;

	@EJB
	private VisualizaCirurgiaCanceladaON visualizaCirurgiaCanceladaON;

	@EJB
	private MbcAgendaOrtProteseON mbcAgendaOrtProteseON;

	@EJB
	private MbcGrupoAlcadaRN mbcGrupoAlcadaRN;

	@EJB
	private RegistroCirurgiaRealizadaON registroCirurgiaRealizadaON;

	@EJB
	private RelatorioCirurgiasReservaHemoterapicaON relatorioCirurgiasReservaHemoterapicaON;

	@EJB
	private MbcSolicitacaoEspExecCirgON mbcSolicitacaoEspExecCirgON;

	@EJB
	private ListarCirurgiasON listarCirurgiasON;

	@EJB
	private RelatorioTransplantesRealizTMOOutrosON relatorioTransplantesRealizTMOOutrosON;

	@EJB
	private PacientesCirurgiaUnidadeON pacientesCirurgiaUnidadeON;

	@EJB
	private ListaCirurgiasDescCirurgicaON listaCirurgiasDescCirurgicaON;

	@EJB
	private MbcDescricaoTecnicaON mbcDescricaoTecnicaON;

	@EJB
	private RelatorioCirurgiasIndicacaoExamesON relatorioCirurgiasIndicacaoExamesON;

	@EJB
	private MbcMaterialPorCirurgiaON mbcMaterialPorCirurgiaON;

	@EJB
	private RelatorioCirurgiasCanceladasON relatorioCirurgiasCanceladasON;

	@EJB
	private RelatorioDiagnosticosPrePosOperatoriosON relatorioDiagnosticosPrePosOperatoriosON;

	@EJB
	private FichaAnestesicaON fichaAnestesicaON;

	@EJB
	private MbcFichaAnestesiasRN mbcFichaAnestesiasRN;

	@EJB
	private MbcAgendasHorarioPrevisaoON mbcAgendasHorarioPrevisaoON;

	@EJB
	private MbcEquipamentoUtilCirgON mbcEquipamentoUtilCirgON;

	@EJB
	private ListaLaudoAihON listaLaudoAihON;

	@EJB
	private FichaPreOperatoriaON fichaPreOperatoriaON;

	@EJB
	private AgendaProcedimentosON agendaProcedimentosON;
	
	@EJB
	private AgendaProcedimentosProfissionaisON agendaProcedimentosProfissionaisON;

	@EJB
	private MbcCirurgiaAnotacaoRN mbcCirurgiaAnotacaoRN;

	@EJB
	private MbcDescricaoTecnicaRN mbcDescricaoTecnicaRN;

	@EJB
	private RelatorioRegistroDaNotaDeSalaON relatorioRegistroDaNotaDeSalaON;

	@EJB
	private MbcAgendaHistoricoON mbcAgendaHistoricoON;

	@EJB
	private MbcSolicHemoCirgAgendadaON mbcSolicHemoCirgAgendadaON;

	@EJB
	private RelatorioLaudoAIHON relatorioLaudoAIHON;

	@EJB
	private ProcedimentosCirurgicosPdtAtivosON procedimentosCirurgicosPdtAtivosON;

	@EJB
	private MbcAgendasRN mbcAgendasRN;

	@EJB
	private MbcAgendasJustificativaRN mbcAgendasJustificativaRN;

	@EJB
	private RelatorioPacientesEmSalaRecuperacaoPorUnidadeON relatorioPacientesEmSalaRecuperacaoPorUnidadeON;

	@EJB
	private PopulaProcedimentoHospitalarInternoRN populaProcedimentoHospitalarInternoRN;

	@EJB
	private RelatorioPacientesEntrevistarON relatorioPacientesEntrevistarON;

	@EJB
	private DescricaoItensRN descricaoItensRN;

	@EJB
	private MbcAgendaHemoterapiaRN mbcAgendaHemoterapiaRN;

	@EJB
	private MbcProcDescricoesRN mbcProcDescricoesRN;

	@EJB
	private RelatorioEscalaCirurgiasAghuON relatorioEscalaCirurgiasAghuON;

	@EJB
	private RetornoCirurgiaEmLoteON retornoCirurgiaEmLoteON;

	@EJB
	private RelatorioCirurgiasComRetornoON relatorioCirurgiasComRetornoON;

	@EJB
	private AcompanharProcessoAutorizacaoOPMsON acompanharProcessoAutorizacaoOPMsON;

	@EJB
	private MbcMaterialPorCirurgiaRN mbcMaterialPorCirurgiaRN;

	@EJB
	private MbcProfCirurgiasRN mbcProfCirurgiasRN;

	@EJB
	private DescricaoCirurgicaON descricaoCirurgicaON;
	
	@EJB
	private InternacaoCOON internacaoCOON;

	@EJB
	private OPMEPortalAgendamentoRN oPMEPortalAgendamentoRN;

	@EJB
	private FaturarCirurgiasCanceladasRN faturarCirurgiasCanceladasRN;
	
	@Inject
	private PdtViaAereasDAO pdtViaAereasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1207933027845942340L;
	
	/*private static final int TRANSACTION_TIMEOUT_1_HORA = 60 * 60 * 1; // 1 hora
	private static final String ENTITY_MANAGER = "entityManager";
	private static final String TRANSACTION = "org.jboss.seam.transaction.transaction";*/

	/**
	 * GETs & SETs
	 */
	protected BlocoCirurgicoON getBlocoCirurgicoON() {
		return blocoCirurgicoON;
	}

	protected ListarCirurgiasON getListarCirurgiasON() {
		return listarCirurgiasON;
	}
	
	protected RelatorioAvaliacaoPreAnestesicaON getRelatorioAvaliacaoPreAnestesicaON() {
		return relatorioAvaliacaoPreAnestesicaON;
	}

	protected MbcDescricaoTecnicaRN getMbcDescricaoTecnicaRN() {
		return mbcDescricaoTecnicaRN;
	}
	
	protected FichaAnestesicaON getFichaAnestesicaON() {
		return fichaAnestesicaON;
	}
	
	@Override
	@Secure("#{s:hasPermission('cirurgia','alterar')}")
	public void atualizarCirurgia(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		// TODO verificar triggers (que não estão implementadas) quando ocorrer
		// o deschaveamento da tabela
		getBlocoCirurgicoON().atualizarCirurgia(cirurgia);
		
	}

	/**
	 * Método para buscar cirurgias por ID de atendimento.
	 * 
	 * @param seqAtendimento
	 * @param dataFimCirurgia
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('cirurgia','pesquisar')}")
	@BypassInactiveModule
	public List<MbcCirurgias> pesquisarCirurgiasPorAtendimento(Integer seqAtendimento, Date dataFimCirurgia) {
		return getBlocoCirurgicoON().pesquisarCirurgiasPorAtendimento(seqAtendimento, dataFimCirurgia);
	}


	/**
	 * Lista Tipos de anestesia que necessitam anestesista
	 * 
	 * @param crgSeq
	 * @param necessitaAnestesista
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public List<MbcTipoAnestesias> listarTipoAnestesias(Integer crgSeq, Boolean necessitaAnestesista) {
		return getMbcAnestesiaCirurgiasDAO().listarTipoAnestesias(crgSeq, necessitaAnestesista);
	}

	/**
	 * verifica se o paciente tem cirurgia sem nota digitada
	 * 
	 * @param pacCodigo
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public Boolean pacienteTemCirurgiaSemNota(Integer pacCodigo, Date dataInternacao, Date dataAlta) {
		return getMbcCirurgiasDAO().pacienteTemCirurgiaSemNota(pacCodigo, dataInternacao, dataAlta);
	}

	/**
	 * Implementa o cursor <code>c_get_cbo_cirg</code>
	 * 
	 * @param crgSeq
	 * @param resp
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public RapServidores buscaRapServidorDeMbcProfCirurgias(Integer crgSeq, DominioSimNao resp) {
		return getMbcProfCirurgiasDAO().buscaRapServidor(crgSeq, resp);
	}

	@Override
	public List<SuggestionListaCirurgiaVO> pesquisarSuggestionEquipe(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro,
			final boolean indResponsavel) {
		return getMbcProfCirurgiasRN().pesquisarSuggestionEquipe(unidade, dtProcedimento, filtro, indResponsavel);
	}

	@Override
	public Long pesquisarSuggestionEquipeCount(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro,
			final boolean indResponsavel) {
		return getMbcProfCirurgiasRN().pesquisarSuggestionEquipeCount(unidade, dtProcedimento, filtro, indResponsavel);
	}

	@Override
	public List<String> obterNomeEquipeCirurgica(final Integer grcSeq) {
		return getMbcProfCirurgiasDAO().obterNomeEquipeCirurgica(grcSeq);
	}

	@Override
	public List<SuggestionListaCirurgiaVO> pesquisarSuggestionProcedimento(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro,
			final Short eprEspSeq, final DominioSituacao situacao, final boolean indPrincipal) {
		return getListarCirurgiasON().pesquisarSuggestionProcedimento(unidade, dtProcedimento, filtro, eprEspSeq, situacao, indPrincipal);
	}

	@Override
	public Long pesquisarSuggestionProcedimentoCount(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro,
			final Short eprEspSeq, final DominioSituacao situacao, final boolean indPrincipal) {
		return getListarCirurgiasON().pesquisarSuggestionProcedimentoCount(unidade, dtProcedimento, filtro, eprEspSeq, situacao, indPrincipal);
	
	}

	@Override
	public List<CirurgiaVO> pesquisarCirurgias(final TelaListaCirurgiaVO tela, String orderProperty, final Integer crgSeq) throws ApplicationBusinessException,
	ApplicationBusinessException {
		return getListarCirurgiasON().pesquisarCirurgias(tela, orderProperty, crgSeq);
	}

	@Override
	public TelaListaCirurgiaVO inicializarTelaListaCirurgiaVO(final String nomeMicrocomputador, boolean carregaUnidadeFuncionalFuncionario)
			throws ApplicationBusinessException, ApplicationBusinessException {
		return getListarCirurgiasON().inicializarTelaListaCirurgiaVO(nomeMicrocomputador, carregaUnidadeFuncionalFuncionario);
	}

	/**
	 * Implementa o cursor <code>c_get_cbo_anest</code>
	 * 
	 * @param crgSeq
	 * @param funcoes
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public RapServidores buscaRapServidorDeMbcProfCirurgias(Integer crgSeq, DominioFuncaoProfissional[] funcoes) {
		return getMbcProfCirurgiasDAO().buscaRapServidor(crgSeq, funcoes);
	}

	@Override
	@BypassInactiveModule
	public Short buscarRnCthcAtuEncPrv(Integer pacCodigo, Date dthrPci, DominioIndRespProc indRespProc) {
		return getMbcProcEspPorCirurgiasDAO().buscarRnCthcAtuEncPrv(pacCodigo, dthrPci, indRespProc);
	}

	/**
	 * Implementa o cursor <code>c_get_cbo_anest_desc</code>
	 * 
	 * @param crgSeq
	 * @param tipoAtuacao
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public RapServidores buscaServidorProfPorCrgSeqETipoAtuacao(Integer crgSeq, DominioTipoAtuacao tipoAtuacao) {
		return getMbcProfDescricoesDAO().buscaServidorProfPorCrgSeqETipoAtuacao(crgSeq, tipoAtuacao);
	}

	@Override
	@BypassInactiveModule
	public MbcCirurgias obterCirurgiaPorChavePrimaria(Integer crgSeq, Enum[] inner, Enum[] left) {
		return this.getMbcCirurgiasDAO().obterPorChavePrimaria(crgSeq, inner, left);
	}
	
	@Override
	@BypassInactiveModule
	public MbcCirurgias obterCirurgiaPorChavePrimaria(Integer crgSeq) {
		return this.getMbcCirurgiasDAO().obterCirurgiaPorChavePrimaria(crgSeq);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#setTimeout(java
	 * .lang.Integer)
	 */
	/*@Override
	public void setTimeout(final Integer timeout) throws ApplicationBusinessException {
		final UserTransaction userTx = this.getUserTransaction();
		try {
			final EntityManager em = this.getEntityManager();
			if (userTx.isNoTransaction() || !userTx.isActive()) {
				userTx.begin();
			}
			if (timeout != null) {
				userTx.setTransactionTimeout(timeout);
			}
			em.joinTransaction();
		} catch (final Exception e) {
			logError(e.getMessage(), e);
		}
	}*/
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#commit(java.lang.Integer)
	 */
	/*@Override
	public void commit(Integer timeout) throws ApplicationBusinessException {
		UserTransaction userTx = this.getUserTransaction();

		try {
			if (userTx.isNoTransaction() || !userTx.isActive()) {
				userTx.begin();
			}
			EntityManager em = this.getEntityManager();
			em.joinTransaction();
			em.flush();
			userTx.commit();
			if (timeout != null) {
				userTx.setTransactionTimeout(timeout);
			}
			if (userTx.isNoTransaction() || !userTx.isActive()) {
				userTx.begin();
			}
			em.joinTransaction();
		} catch (Exception e) {
			logError(e.getMessage(), e);
			EstoqueFacadeExceptionCode.ERRO_AO_CONFIRMAR_TRANSACAO.throwException();
		}
	}
	
	private EntityManager getEntityManager() {
		return (EntityManager) Component.getInstance(ENTITY_MANAGER, true);
	}
	
	private UserTransaction getUserTransaction() {
		return (UserTransaction) org.jboss.seam.Component
				.getInstance(TRANSACTION);
	}*/

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcProfDescricoesDAO getMbcProfDescricoesDAO() {
		return mbcProfDescricoesDAO;
	}

	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	protected MbcTipoAnestesiasDAO getMbcTipoAnestesiasDAO() {
		return mbcTipoAnestesiasDAO;
	}

	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}
	
	protected MbcEscalaProfUnidCirgDAO getMbcEscalaProfUnidCirgDAO(){
		return mbcEscalaProfUnidCirgDAO;
		
	}

	@Override
	@BypassInactiveModule
	public List<MbcProcedimentoCirurgicos> listarMbcProcedimentoCirurgicos(Object objPesquisa) {
		return this.getMbcProcedimentoCirurgicoDAO().listarMbcProcedimentoCirurgicos(objPesquisa);
	}

	@Override
	@BypassInactiveModule
	public Long listarMbcProcedimentoCirurgicosCount(Object objPesquisa) {
		return this.getMbcProcedimentoCirurgicoDAO().listarMbcProcedimentoCirurgicosCount(objPesquisa);
	}
	
	@Override
	public void validarContaminacaoProcedimentoCirurgico(Integer pciSeq, DominioIndContaminacao novoIndContaminacao) throws ApplicationBusinessException {
		getDescricaoItensON().validarContaminacaoProcedimentoCirurgico(pciSeq, novoIndContaminacao);
	}

	@Override
	@BypassInactiveModule
	public List<CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO> listarCirurgiasAgendadasCadastroSugestaoDesdobramento(Date dthrInicioCirurgiaIni,
			Date dthrInicioCirurgiaFim, ConstanteAghCaractUnidFuncionais[] caracteristicas, DominioGrupoConvenio grupoConvenio, DominioTipoPlano indTipoPlano,
			DominioSituacaoCirurgia situacaoCirurgia) {
		return this.getMbcCirurgiasDAO().listarCirurgiasAgendadasCadastroSugestaoDesdobramento(dthrInicioCirurgiaIni, dthrInicioCirurgiaFim, caracteristicas, grupoConvenio,
				indTipoPlano, situacaoCirurgia);
	}

	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcCirurgias> listarCirurgiasPorHistoricoPacienteVO(HistoricoPacienteVO historicoVO) {
		return this.getMbcCirurgiasDAO().listarCirurgiasPorHistoricoPacienteVO(historicoVO);
	}

	@Override
	@BypassInactiveModule
	public MbcCirurgias obterProcedimentoCirurgicoInternacaoUltimaSemana(Integer pacCodigo, Date dtInternacao, DominioSituacaoCirurgia situacaoCirurgia) {
		return getMbcCirurgiasDAO().obterProcedimentoCirurgicoInternacaoUltimaSemana(pacCodigo, dtInternacao, situacaoCirurgia);
	}

	@Override
	@BypassInactiveModule
	public MbcOcorrenciaFichaEvento obterMbcOcorrenciaFichaEventosComFicha(Long seqFichaAnestesia, DominioOcorrenciaFichaEvento tipoOcorrencia, Short seqMbcEventoPrincipal) {
		return getMbcOcorrenciaFichaEventosDAO().obterMbcOcorrenciaFichaEventosComFicha(seqFichaAnestesia, tipoOcorrencia, seqMbcEventoPrincipal);
	}

	private MbcOcorrenciaFichaEventosDAO getMbcOcorrenciaFichaEventosDAO() {
		return mbcOcorrenciaFichaEventosDAO;
	}

	@Override
	@BypassInactiveModule
	public MbcFichaFarmaco obterMbcFichaFarmaco(Integer seqMbcFichaFarmaco) {
		return getMbcFichaFarmacoDAO().obterPorChavePrimaria(seqMbcFichaFarmaco);
	}

	private MbcFichaFarmacoDAO getMbcFichaFarmacoDAO() {
		return mbcFichaFarmacoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaTecnicaEspecial> pesquisarMbcFichasTecnicasEspeciaisComTecnicaEspecial(Long seqMbcFichaAnestesia) {
		return getMbcFichaTecnicaEspecialDAO().pesquisarMbcFichasTecnicasEspeciaisComTecnicaEspecial(seqMbcFichaAnestesia);
	}

	private MbcFichaTecnicaEspecialDAO getMbcFichaTecnicaEspecialDAO() {
		return mbcFichaTecnicaEspecialDAO;
	}

	@Override
	@BypassInactiveModule
	public List<ItemMonitorizacaoDefinidoFichaAnestVO> pesquisarItensMonitorizacaoDefinidosFicha(Long seqMbcFichaAnest) {
		return getMbcFichaAnestesiasDAO().pesquisarItensMonitorizacaoDefinidosFicha(seqMbcFichaAnest);
	}

	private MbcFichaAnestesiasDAO getMbcFichaAnestesiasDAO() {
		return mbcFichaAnestesiasDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaFluidoPerdido> pesquisarFichasFluidosPerdidosByFichaAnestesia(Long seqMbcFichaAnest, DominioTipoFluidoPerdido tipoFluidoPerdido) {
		return getMbcFichaFluidoPerdidoDAO().pesquisarFichasFluidosPerdidosByFichaAnestesia(seqMbcFichaAnest, tipoFluidoPerdido);
	}

	private MbcFichaFluidoPerdidoDAO getMbcFichaFluidoPerdidoDAO() {
		return mbcFichaFluidoPerdidoDAO;
	}

	@Override
	@BypassInactiveModule
	public Long obterSomaVolumeTotalFluidoPerdido(Long seqMbcFichaAnest, DominioTipoFluidoPerdido tipoFluidoPerdido) {
		return getMbcFichaFluidoPerdidoDAO().obterSomaVolumeTotalFluidoPerdido(seqMbcFichaAnest, tipoFluidoPerdido);
	}

	@Override
	@BypassInactiveModule
	public MbcFichaOrgaoTransplante obterFichasOrgaosTransplantes(Long seqMbcFichaAnestia, Short seqMbcOrgaoTransplantado) {
		return getMbcFichaOrgaoTransplanteDAO().obterFichasOrgaosTransplantes(seqMbcFichaAnestia, seqMbcOrgaoTransplantado);
	}

	private MbcFichaOrgaoTransplanteDAO getMbcFichaOrgaoTransplanteDAO() {
		return mbcFichaOrgaoTransplanteDAO;
	}

	@Override
	@BypassInactiveModule
	public MbcOcorrenciaFichaEvento obterOcorrenciaFichaEvento(Long seqMbcFichaAnest, Short seqMbcOrgaoTransplantado, DominioOcorrenciaFichaEvento tipoOcorrencia) {
		return getMbcOcorrenciaFichaEventosDAO().obterOcorrenciaFichaEvento(seqMbcFichaAnest, seqMbcOrgaoTransplantado, tipoOcorrencia);
	}

	@Override
	@BypassInactiveModule
	public List<MbcMonitorizacao> pesquisarMbcMonitorizacoesComFichaAnestesia(Long seqMbcFichaAnestesia, Boolean fichaDefMonitorado) {
		return getMbcMonitorizacaoDAO().pesquisarMbcMonitorizacoesComFichaAnestesia(seqMbcFichaAnestesia, fichaDefMonitorado);
	}

	private MbcMonitorizacaoDAO getMbcMonitorizacaoDAO() {
		return mbcMonitorizacaoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaFluidoAdministrados> pesquisarMbcFichaFluidoAdministrado(Long seqMbcFichaAnest, DominioTipoFluidoAdministrado tipoFluidoPerdido) {
		return getMbcFichaFluidoAdministradosDAO().pesquisarMbcFichaFluidoAdministrado(seqMbcFichaAnest, tipoFluidoPerdido);
	}

	private MbcFichaFluidoAdministradosDAO getMbcFichaFluidoAdministradosDAO() {
		return mbcFichaFluidoAdministradosDAO;
	}

	@Override
	@BypassInactiveModule
	public MbcOcorrFichaFluidoAdms obterMbcOcorrFichaFluidoAdm(Integer seqMbcFichaFluidoAdm, DominioOcorrFichaFluido ocorrFichaFluido, Boolean permaneceNoPos) {
		return getMbcOcorrFichaFluidoAdmsDAO().obterMbcOcorrFichaFluidoAdm(seqMbcFichaFluidoAdm, ocorrFichaFluido, permaneceNoPos);
	}

	private MbcOcorrFichaFluidoAdmsDAO getMbcOcorrFichaFluidoAdmsDAO() {
		return mbcOcorrFichaFluidoAdmsDAO;
	}

	@Override
	@BypassInactiveModule
	public MbcOcorrenciaFichaEvento obterMbcOcorrenciaFichaEventoComEventoPrincipal(Long seqMbcFichaAnestesia, DominioOcorrenciaFichaEvento tipoOcorrenciaFichaEvento) {
		return getMbcOcorrenciaFichaEventosDAO().obterMbcOcorrenciaFichaEventoComEventoPrincipal(seqMbcFichaAnestesia, tipoOcorrenciaFichaEvento);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaGas> listarMbcFichaGasesComMaterial(Long seqMbcFichaAnestesia) {
		return getMbcFichaGasDAO().listarMbcFichaGasesComMaterial(seqMbcFichaAnestesia);
	}

	private MbcFichaGasDAO getMbcFichaGasDAO() {
		return mbcFichaGasDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcOcorrenciaFichaGas> listarMbcOcorrenciaFichaGas(Integer seqMbcFichaGas) {
		return getMbcOcorrenciaFichaGasDAO().listarMbcOcorrenciaFichaGas(seqMbcFichaGas);
	}

	private MbcOcorrenciaFichaGasDAO getMbcOcorrenciaFichaGasDAO() {
		return mbcOcorrenciaFichaGasDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcOcorrenciaFichaFarmaco> listarMbcFichaFarmacosByMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcOcorrenciaFichaFarmacoDAO().listarMbcFichaFarmacosByMbcFichaAnestesia(seqMbcFichaAnestesia);
	}

	private MbcOcorrenciaFichaFarmacoDAO getMbcOcorrenciaFichaFarmacoDAO() {
		return mbcOcorrenciaFichaFarmacoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcOcorrenciaFichaGas> listarMbcFichaGasByMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcOcorrenciaFichaGasDAO().listarMbcFichaGasByMbcFichaAnestesia(seqMbcFichaAnestesia);
	}

	private MbcTmpFichaFarmacoDAO getMbcTmpFichaFarmacoDAO() {
		return mbcTmpFichaFarmacoDAO;
	}

	@Override
	@BypassInactiveModule
	public void persistirMbcTmpFichaFarmaco(MbcTmpFichaFarmaco mbcTmpFichaFarmaco) {
		getMbcTmpFichaFarmacoDAO().persistir(mbcTmpFichaFarmaco);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaFarmaco> listarFichaFarmacoPorFicSeq(Long seqMbcFichaAnestesia) {
		return getMbcFichaFarmacoDAO().listarFichaFarmacoPorFicSeq(seqMbcFichaAnestesia);
	}

	@Override
	@BypassInactiveModule
	public List<Object[]> pesquisarTipoItemMonitorizacaoComMedicao(Long seqMbcFichaAnestesia) {
		return getMbcFichaMedMonitorizacaoDAO().pesquisarTipoItemMonitorizacaoComMedicao(seqMbcFichaAnestesia);
	}

	private MbcFichaMedMonitorizacaoDAO getMbcFichaMedMonitorizacaoDAO() {
		return mbcFichaMedMonitorizacaoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaMedMonitorizacao> pesquisarFichaMedMonitorizacaoComMedicao(Long seqMbcFichaAnestesia, Short seqMbcItemMonitorizacoes) {
		return getMbcFichaMedMonitorizacaoDAO().persquisarFichaMedMononitorizacaoComMedicao(seqMbcFichaAnestesia, seqMbcItemMonitorizacoes);

	}

	@Override
	@BypassInactiveModule
	public MbcFichaAnestesias obterMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaAnestesiasDAO().obterPorChavePrimaria(seqMbcFichaAnestesia);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaFinal> pesquisarMbcFichasFinais(Long seqMbcFichaAnestesia) {
		return getMbcFichaFinalDAO().pesquisarMbcFichasFinais(seqMbcFichaAnestesia);
	}

	private MbcFichaFinalDAO getMbcFichaFinalDAO() {
		return mbcFichaFinalDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaPaciente> pesquisarMbcFichasPacientes(Long seqMbcFichaAnestesia) {
		return getMbcFichaPacienteDAO().pesquisarMbcFichasPacientes(seqMbcFichaAnestesia);
	}

	private MbcFichaPacienteDAO getMbcFichaPacienteDAO() {
		return mbcFichaPacienteDAO;
	}

	@Override
	@BypassInactiveModule
	public BigDecimal calcularSuperficieCorporalDoPaciente(Integer codPaciente) {
		return getMbcFichaPacienteDAO().calcularSuperficieCorporalDoPaciente(codPaciente);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaInicial> pesquisarMbcFichasIniciais(Long seqMbcFichaAnestesia) {
		return getMbcFichaInicialDAO().pesquisarMbcFichasIniciais(seqMbcFichaAnestesia);
	}

	private MbcFichaInicialDAO getMbcFichaInicialDAO() {
		return mbcFichaInicialDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaTipoAnestesia> pesquisarMbcFichasTipoAnestesias(Long seqMbcFichaAnestesia) {
		return getMbcFichaTipoAnestesiaDAO().pesquisarMbcFichasTipoAnestesias(seqMbcFichaAnestesia);
	}

	private MbcFichaTipoAnestesiaDAO getMbcFichaTipoAnestesiaDAO() {
		return mbcFichaTipoAnestesiaDAO;
	}

	@Override
	@BypassInactiveModule
	public Long getCountMbcFichaNeonatologiaByMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaNeonatologiaDAO().getCountMbcFichaNeonatologiaByMbcFichaAnestesia(seqMbcFichaAnestesia);
	}

	private MbcFichaNeonatologiaDAO getMbcFichaNeonatologiaDAO() {
		return mbcFichaNeonatologiaDAO;
	}

	@Override
	@BypassInactiveModule
	public Long getCountMbcFichaGrandePorteByMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaGrandePorteDAO().getCountMbcFichaGrandePorteByMbcFichaAnestesia(seqMbcFichaAnestesia);
	}

	private MbcFichaGrandePorteDAO getMbcFichaGrandePorteDAO() {
		return mbcFichaGrandePorteDAO;
	}

	@Override
	@BypassInactiveModule
	public MbcOcorrenciaFichaFarmaco obterMbcOcorrenciaFichaFarmacoBySeq(Integer seqMbcOcorrenciaFichaFarmaco) {
		return getMbcOcorrenciaFichaFarmacoDAO().obterMbcOcorrenciaFichaFarmacoBySeq(seqMbcOcorrenciaFichaFarmaco);
	}

	@Override
	@BypassInactiveModule
	public MbcFichaAnestesias obterMbcFichaAnestesiaByConsulta(Integer numeroConsulta) {
		return getMbcFichaAnestesiasDAO().obterMbcFichaAnestesiaByConsulta(numeroConsulta);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaEquipeAnestesia> pesquisarMbcFichaEquipeAnestesiasComServidorAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaEquipeAnestesiaDAO().pesquisarMbcFichaEquipeAnestesiasComServidorAnestesia(seqMbcFichaAnestesia);

	}

	private MbcFichaEquipeAnestesiaDAO getMbcFichaEquipeAnestesiaDAO() {
		return mbcFichaEquipeAnestesiaDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaExame> pesquisarMbcFichasExamesComItemSolicitacaoExame(Long seqMbcFichaAnestesia) {
		return getMbcFichaExameDAO().pesquisarMbcFichasExamesComItemSolicitacaoExame(seqMbcFichaAnestesia);
	}

	private MbcFichaExameDAO getMbcFichaExameDAO() {
		return mbcFichaExameDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaInducaoManutencao> pesquisarMbcInducaoManutencaoByFichaAnestesia(Long seqMbcFichaAnestesia, DominioTipoInducaoManutencao tipoInducaoManutencao,
			Boolean fichaInducaoManutSelecionado) {
		return getMbcFichaInducaoManutencao().pesquisarMbcInducaoManutencaoByFichaAnestesia(seqMbcFichaAnestesia, tipoInducaoManutencao, fichaInducaoManutSelecionado);
	}

	private MbcFichaInducaoManutencaoDAO getMbcFichaInducaoManutencao() {
		return mbcFichaInducaoManutencaoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaMedMonitorizacao> pesquisarMbcFichaMedMonitorizacaoComMbcTipoItemMonit(Long seqMbcFichaAnestesia) {
		return getMbcFichaMedMonitorizacaoDAO().pesquisarMbcFichaMedMonitorizacaoComMbcTipoItemMonit(seqMbcFichaAnestesia);
	}

	@Override
	@BypassInactiveModule
	public List<MbcTmpFichaFarmaco> pesquisarMbcTmpFichaFarmacoByFichaAnestesiaESessao(Integer seqMbcFichaAnestesia, String vSessao) {
		return getMbcTmpFichaFarmacoDAO().pesquisarMbcTmpFichaFarmacoByFichaAnestesiaESessao(seqMbcFichaAnestesia, vSessao);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaNeonatologia> pesquisarMbcNeonatologiasByFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaNeonatologiaDAO().pesquisarMbcNeonatologiasByFichaAnestesia(seqMbcFichaAnestesia);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaAnestesiaRegional> pesquisarMbcFichaAnestesiaRegionalByFichaAnestesia(Long seqMbcFichaAnestesia, Boolean ivRegional, Boolean bloqueioNervoPlexo,
			Boolean intercostais, Boolean neuroeixo) {
		return getMbcFichaAnestesiaRegionalDAO().pesquisarMbcFichaAnestesiaRegionalByFichaAnestesia(seqMbcFichaAnestesia, ivRegional, bloqueioNervoPlexo, intercostais, neuroeixo);
	}

	private MbcFichaAnestesiaRegionalDAO getMbcFichaAnestesiaRegionalDAO() {
		return mbcFichaAnestesiaRegionalDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaBloqNervoPlexos> pesquisarMbcFichaBloqNervoPlexosByFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaBloqNervoPlexosDAO().pesquisarMbcFichaBloqNervoPlexosByFichaAnestesia(seqMbcFichaAnestesia);
	}

	private MbcFichaBloqNervoPlexosDAO getMbcFichaBloqNervoPlexosDAO() {
		return mbcFichaBloqNervoPlexosDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcAnestRegionalNeuroeixos> pesquisarMbcAnestRegNeuroeixoByFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcAnestRegionalNeuroeixosDAO().pesquisarMbcAnestRegNeuroeixoByFichaAnestesia(seqMbcFichaAnestesia);
	}

	private MbcAnestRegionalNeuroeixosDAO getMbcAnestRegionalNeuroeixosDAO() {
		return mbcAnestRegionalNeuroeixosDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcNeuroeixoNvlPuncionados> pesquisarMbcNeuroNvlPuncionadosByMbcAnestRegionalNeuroeixos(Integer seqMbcNeuroEixoNvlPuncionados) {
		return getMbcNeuroeixoNvlPuncionadosDAO().pesquisarMbcNeuroNvlPuncionadosByMbcAnestRegionalNeuroeixos(seqMbcNeuroEixoNvlPuncionados);
	}

	private MbcNeuroeixoNvlPuncionadosDAO getMbcNeuroeixoNvlPuncionadosDAO() {
		return mbcNeuroeixoNvlPuncionadosDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaMaterial> pesquisarMbcFichasMateriaisComScoMaterialByFichaAnestesia(Long seqMbcFichaAnestesia, Boolean materialNeuroEixo, Boolean materialViaAerea) {
		return getMbcFichaMaterialDAO().pesquisarMbcFichasMateriaisComScoMaterialByFichaAnestesia(seqMbcFichaAnestesia, materialNeuroEixo, materialViaAerea);
	}

	private MbcFichaMaterialDAO getMbcFichaMaterialDAO() {
		return mbcFichaMaterialDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaEquipeAnestesia> pesquisarMbcFichaEquipeAnestesiasByFichaAnestesia(Long seqMbcFichaAnestesia, Boolean executorBloqueio) {
		return getMbcFichaEquipeAnestesiaDAO().pesquisarMbcFichaEquipeAnestesiasByFichaAnestesia(seqMbcFichaAnestesia, executorBloqueio);
	}

	@Override
	@BypassInactiveModule
	public Long getCountMbcFichaViaAereaByFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaViaAereaDAO().getCountMbcFichaViaAereaByFichaAnestesia(seqMbcFichaAnestesia);
	}

	private MbcFichaViaAereaDAO getMbcFichaViaAereaDAO() {
		return mbcFichaViaAereaDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaViaAerea> pesquisarMbcFichaViaAereaByFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaViaAereaDAO().pesquisarMbcFichaViaAereaByFichaAnestesia(seqMbcFichaAnestesia);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaEspecifIntubacoes> pesquisarMbcEspecifIntubacaoByFichaAnestesiaAndTipo(Long seqMbcFichaAnestesia, String tipo) {
		return getMbcFichaEspecifIntubacoesDAO().pesquisarMbcEspecifIntubacaoByFichaAnestesiaAndTipo(seqMbcFichaAnestesia, tipo);
	}

	private MbcFichaEspecifIntubacoesDAO getMbcFichaEspecifIntubacoesDAO() {
		return mbcFichaEspecifIntubacoesDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaVentilacao> pesquisarMbcFichaVentilacaoByFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaVentilacaoDAO().pesquisarMbcFichaVentilacaoByFichaAnestesia(seqMbcFichaAnestesia);
	}

	private MbcFichaVentilacaoDAO getMbcFichaVentilacaoDAO() {
		return mbcFichaVentilacaoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaPosicionamento> pesquisarMbcFichaPosicionamentosByFichaAnestesia(Long seqMbcFichaAnestesia, Boolean posicionamentoFicha,
			DominioGrupoMbcPosicionamento grupoMbcPosionamento) {
		return getMbcFichaPosicionamentoDAO().pesquisarMbcFichaPosicionamentosByFichaAnestesia(seqMbcFichaAnestesia, posicionamentoFicha, grupoMbcPosionamento);
	}

	private MbcFichaPosicionamentoDAO getMbcFichaPosicionamentoDAO() {
		return mbcFichaPosicionamentoDAO;
	}

	@Override
	@BypassInactiveModule
	public Long obterSomaVolumeTotalFluidoAdministrado(Long seqMbcFichaAnest, Boolean agrupaTipoFluidoAdm) {
		return getMbcFichaFluidoAdministradosDAO().obterSomaVolumeTotalFluidoAdministrado(seqMbcFichaAnest, agrupaTipoFluidoAdm);
	}

	@Override
	@BypassInactiveModule
	public List<MbcOcorrenciaFichaEvento> pesquisarMbcOcorrenciaFichaEvento(Long seqMbcFichaAnestesia, List<DominioTipoEventoMbcPrincipal> tipoEventosPrincipal,
			List<DominioTipoOcorrenciaFichaFarmaco> tipoOcorrenciasFichaFarmaco) {
		return getMbcOcorrenciaFichaEventosDAO().pesquisarMbcOcorrenciaFichaEvento(seqMbcFichaAnestesia, tipoEventosPrincipal, tipoOcorrenciasFichaFarmaco);
	}

	@Override
	@BypassInactiveModule
	public Date obterDtOcorrenciaMaxMbcFichaOcorrenciaEvento(Integer seqMbcOcorrenciaFichaEvento, DominioTipoOcorrenciaFichaFarmaco tipoOcorrenciasFichaEvento) {
		return getMbcOcorrenciaFichaEventosDAO().obterDtOcorrenciaMaxMbcFichaOcorrenciaEvento(seqMbcOcorrenciaFichaEvento, tipoOcorrenciasFichaEvento);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaFinal> pesquisarMbcFichasFinais(Long seqMbcFichaAnestesia, Boolean nenhumEventoAdverso) {
		return getMbcFichaFinalDAO().pesquisarMbcFichasFinais(seqMbcFichaAnestesia, nenhumEventoAdverso);
	}

	@Override
	@BypassInactiveModule
	public List<MbcOcorrenciaFichaEvento> pesquisarMbcOcorrenciaFichaEventoComMbcEventoAdverso(Long seqMbcFichaAnestesia) {
		return getMbcOcorrenciaFichaEventosDAO().pesquisarMbcOcorrenciaFichaEventoComMbcEventoAdverso(seqMbcFichaAnestesia);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaGrandePorte> pesquisarMbcFichaGrandePorteByMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaGrandePorteDAO().pesquisarMbcFichaGrandePorteByMbcFichaAnestesia(seqMbcFichaAnestesia);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaOrgaoTransplante> pesquisarMbcFichaOrgaoTransplanteByFichaAnestesia(Long seqMbcFichaAnestesia) {
		return getMbcFichaOrgaoTransplanteDAO().pesquisarMbcFichaOrgaoTransplanteByFichaAnestesia(seqMbcFichaAnestesia);
	}

	private MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}
	
	@Override
	@BypassInactiveModule
	public List<ProcedimentosPOLVO> pesquisarProcedimentosComDescricaoPOL(Integer pacCodigo) {
		return getMbcDescricaoCirurgicaDAO().pesquisarProcedimentosComDescricaoPOL(pacCodigo);
	}
	
	@Override
	@BypassInactiveModule
	public List<ProcedimentosPOLVO> pesquisarProcedimentosSemDescricaoPOL(Integer pacCodigo) {
		return getMbcProcedimentoCirurgicoDAO().pesquisarProcedimentosSemDescricaoPOL(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<Object[]> listarAtendimentosPacienteCirurgiaInternacaoPorCodigo(Integer pacCodigo, DominioSituacaoCirurgia situacaoCirurgia, DominioOrigemPacienteCirurgia origemPacienteCirurgia, Boolean situacaoCirurgiaDiferente) {
		return this.getMbcCirurgiasDAO().listarAtendimentosPacienteCirurgiaInternacao(pacCodigo, situacaoCirurgia, origemPacienteCirurgia, situacaoCirurgiaDiferente);
	}

	@Override
	@BypassInactiveModule
	public List<Object[]> listarAtendimentosPacienteCirurgiaAmbulatorioPorCodigo(Integer pacCodigo) {
		return this.getMbcCirurgiasDAO().listarAtendimentosPacienteCirurgiaAmbulatorioPorCodigo(pacCodigo);
	}

	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcAgendas> listarAgendarPorCodigoPaciente(Integer pacCodigo) {
		return getMbcAgendasDAO().listarAgendarPorCodigoPaciente(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<CirurgiasInternacaoPOLVO> pesquisarAgendasProcCirurgicosInternacaoPOL(Integer codigo) {
		return getMbcAgendasDAO().pesquisarAgendasProcCirurgicosInternacaoPOL(codigo);
	}

	@Override
	@BypassInactiveModule
	public List<CirurgiasInternacaoPOLVO> pesquisarCirurgiasInternacaoPOL(Integer codigo) {
		return getMbcCirurgiasDAO().pesquisarCirurgiasInternacaoPOL(codigo);
	}

	@Override
	@BypassInactiveModule
	public List<ProcedimentosPOLVO> pesquisarProcedimentosPortalPOL(Integer codigo) {
		return getMbcAgendasDAO().pesquisarProcedimentosPortalPOL(codigo);
	}

	protected EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}

	protected RelatorioEscalaCirurgicaON getRelatorioEscalaCirurgicaON() {
		return relatorioEscalaCirurgicaON;
	}

	protected RelatorioPacientesComCirurgiaPorUnidadeON getRelatorioPacientesComCirurgiaPorUnidadeON() {
		return relatorioPacientesComCirurgiaPorUnidadeON;
	}
	
	protected RelatorioNotasDeConsumoDaSalaON getRelatorioNotasDeConsumoDaSalaON() {
		return relatorioNotasDeConsumoDaSalaON;
	}
	
	protected RelatorioRegistroDaNotaDeSalaON getRelatorioRegistroDaNotaDeSalaON() {
		return relatorioRegistroDaNotaDeSalaON;
	}
	
	protected ProtocoloEntregaNotasDeConsumoON getProtocoloEntregaNotasDeConsumoON() {
		return protocoloEntregaNotasDeConsumoON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.paciente.business.IPacienteFacade#pesquisaEscalaCirurgia (java.lang.Short, java.util.Date, java.util.Date, java.util.Date)
	 */
	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('relatorioPaciente','escalaCirurgia')}")
	public List<EscalaCirurgiasVO> pesquisaEscalaCirurgia(AghUnidadesFuncionais unidadesFuncionais, Date dataCirurgia, DominioStatusRelatorio status)
	throws ApplicationBusinessException {
		return this.getEscalaCirurgiasON().pesquisa(unidadesFuncionais, dataCirurgia, status);
	}

	@Override
	public List<EscalaCirurgiasVO> pesquisarEscalaSimples(AghUnidadesFuncionais unidadesFuncional, Date dataCirurgia) throws ApplicationBusinessException {
		return this.getRelatorioEscalaCirurgicaON().pesquisarEscalaSimples(unidadesFuncional, dataCirurgia);
	}
	
	@Override
	public List<EscalaCirurgiasVO> pesquisarEscalaCirurgica(AghUnidadesFuncionais unidadesFuncional, Date dataCirurgia, Integer grupoMat) throws ApplicationBusinessException {
		return this.getRelatorioEscalaCirurgicaON().pesquisarEscalaCirurgica(unidadesFuncional, dataCirurgia,grupoMat);
	}

	@Override
	public String pesquisarTituloEscala(DominioTipoEscala tipoEscala, Date dataCirurgia) {
		return this.getRelatorioEscalaCirurgicaON().pesquisarTituloEscala(tipoEscala, dataCirurgia);
	}

	protected MbcAnestesiaDescricoesDAO getMbcAnestesiaDescricoesDAO() {
		return mbcAnestesiaDescricoesDAO;
	}

	protected AnestesiaDescricoesRN getAnestesiaDescricoesRN() {
		return anestesiaDescricoesRN;
	}
	
	@Override
	@BypassInactiveModule
	public MbcAnestesiaDescricoes buscarAnestesiaDescricoes(Integer dcgCrgSeq, Short seqp) {
		return getMbcAnestesiaDescricoesDAO().buscarAnestesiaDescricoes(dcgCrgSeq, seqp);
	}

	@Override
	public void inserirAnestesiaDescricoes(final MbcAnestesiaDescricoes anestesiaDescricao)
												throws ApplicationBusinessException {
		getAnestesiaDescricoesRN().inserirAnestesiaDescricoes(anestesiaDescricao);
	}

	@Override
	public void alterarAnestesiaDescricoes(final MbcAnestesiaDescricoes anestesiaDescricao, MbcTipoAnestesias tipoAnestesia)
												throws ApplicationBusinessException, ApplicationBusinessException {
		getAnestesiaDescricoesRN().alterarAnestesiaDescricoes(anestesiaDescricao, tipoAnestesia);
	}
	
	@Override
	@BypassInactiveModule
	public List<MbcCirurgias> pesquisarCirurgiaPorAtendimento(Integer seqAtendimento) {
		return getMbcCirurgiasDAO().pesquisarCirurgiaPorAtendimento(seqAtendimento);
	}

	@Override
	@BypassInactiveModule
	public List<Date> buscarDataCirurgias(Integer codPaciente, Date dtRealizado, Date dtRealizadoFimMes, DominioSituacaoCirurgia situacaoCirurgia, Boolean indDigtNotaSala,
			Short cnvCodigo, DominioIndRespProc dominioIndRespProc, DominioSituacao situacao, Integer phiSeq) {
		return getMbcCirurgiasDAO().buscarDataCirurgias(codPaciente, dtRealizado, dtRealizadoFimMes, situacaoCirurgia, indDigtNotaSala, cnvCodigo, dominioIndRespProc, situacao,
				phiSeq);
	}

	@Override
	@BypassInactiveModule
	public List<MbcCirurgias> listarCirurgias(AipPacientes paciente, AghAtendimentos atendimento) {
		return getMbcCirurgiasDAO().listarCirurgias(paciente, atendimento);
	}

	@Override
	@BypassInactiveModule
	public List<MbcCirurgias> pesquisarCirurgiasPorPaciente(AipPacientes paciente) {
		return getMbcCirurgiasDAO().pesquisarCirurgiasPorPaciente(paciente);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarCirurgiasPorPacienteCount(AipPacientes paciente) {
		return getMbcCirurgiasDAO().pesquisarCirurgiasPorPacienteCount(paciente);
	}

	@Override
	@BypassInactiveModule
	public List<MbcCirurgias> listarCirurgiasPorCodigoPaciente(Integer pacCodigo) {
		return getMbcCirurgiasDAO().listarCirurgiasPorCodigoPaciente(pacCodigo);
	}
	
	@Override
	@BypassInactiveModule
	public void persistirCirurgia(MbcCirurgias mbcCirurgias, RapServidores servidorLogado) throws BaseException {
		getMbcCirurgiasRN().persistirCirurgia(mbcCirurgias, null, new Date());
	}

	@Override
	@BypassInactiveModule
	public void persistirMbcFichaAnestesias(MbcFichaAnestesias fichaAnestesia, RapServidores servidorLogado) throws BaseException {
		getMbcFichaAnestesiasRN().persistir(fichaAnestesia);
	}
	
	@Override
	@BypassInactiveModule
	public Long verificarSeEscalaPortalAgendamentoTemCirurgia(Integer agdSeq, Date dtAgenda) {
		return getMbcCirurgiasDAO().verificarSeEscalaPortalAgendamentoTemCirurgia(agdSeq, dtAgenda);
	}

	@Override
	@BypassInactiveModule
	public boolean procedimentoCirurgicoExigeInternacao(AghAtendimentos atendimento) {
		return getMbcCirurgiasDAO().procedimentoCirurgicoExigeInternacao(atendimento);
	}

	@Override
	@BypassInactiveModule
	public List<Object[]> pesquisarRealizadasPorProcedimentoEspecial(Integer atdSeq) {
		return getMbcCirurgiasDAO().pesquisarRealizadasPorProcedimentoEspecial(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public List<MbcDescricaoCirurgica> listarDescricaoCirurgicaPorSeqCirurgiaSituacao(Integer crgSeq, DominioSituacaoDescricaoCirurgia situacao) {
		return getMbcDescricaoCirurgicaDAO().listarDescricaoCirurgicaPorSeqCirurgiaSituacao(crgSeq, situacao);
	}

	@Override
	@BypassInactiveModule
	public Long listarDescricaoCirurgicaPorSeqCirurgiaSituacaoCount(Integer crgSeq, DominioSituacaoDescricaoCirurgia situacao) {
		return getMbcDescricaoCirurgicaDAO().listarDescricaoCirurgicaPorSeqCirurgiaSituacaoCount(crgSeq, situacao);
	}

	@Override
	@BypassInactiveModule
	public List<MbcDescricaoCirurgica> listarDescricaoCirurgicaPorSeqCirurgia(Integer crgSeq) {
		return getMbcDescricaoCirurgicaDAO().listarDescricaoCirurgicaPorSeqCirurgia(crgSeq);
	}

	@Override
	@BypassInactiveModule
	public MbcDescricaoCirurgica buscarMbcDescricaoCirurgica(Integer crgSeq, Short seqp) {
		return getMbcDescricaoCirurgicaDAO().buscarMbcDescricaoCirurgica(crgSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public MbcDescricaoCirurgica buscarDescricaoCirurgica(Integer crgSeq, Short seqp) {
		return getMbcDescricaoCirurgicaDAO().buscarDescricaoCirurgica(crgSeq, seqp);
	}

	private MbcDescricaoItensDAO getMbcDescricaoItensDAO() {
		return mbcDescricaoItensDAO;
	}

	@Override
	@BypassInactiveModule
	public MbcDescricaoItens buscarDescricaoItens(Integer dcgCrgSeq, Short dcgSeqp) {
		return getMbcDescricaoItensDAO().buscarDescricaoItens(dcgCrgSeq, dcgSeqp);
	}

	@Override
	public void alterarMbcDescricaoItens(MbcDescricaoItens mbcDescricaoItens) throws ApplicationBusinessException, ApplicationBusinessException {
		getDescricaoItensRN().alterarMbcDescricaoItens(mbcDescricaoItens);
	}

	protected DescricaoItensRN getDescricaoItensRN() {
		return descricaoItensRN;
	}

	protected MbcDescricaoTecnicasDAO getMbcDescricaoTecnicasDAO() {
		return mbcDescricaoTecnicasDAO;
	}

	@Override
	@BypassInactiveModule
	public MbcDescricaoTecnicas buscarDescricaoTecnicas(Integer dcgCrgSeq, Short seqp) {
		return getMbcDescricaoTecnicasDAO().buscarDescricaoTecnicas(dcgCrgSeq, seqp);
	}

	private MbcDiagnosticoDescricaoDAO getMbcDiagnosticoDescricaoDAO() {
		return mbcDiagnosticoDescricaoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcDiagnosticoDescricao> buscarMbcDiagnosticoDescricao(Integer dcgCrgSeq, Short dcgSeqp) {
		return getMbcDiagnosticoDescricaoDAO().buscarMbcDiagnosticoDescricao(dcgCrgSeq, dcgSeqp);
	}

	@Override
	public List<MbcDiagnosticoDescricao> buscarMbcDiagnosticoDescricao(final Integer dcgCrgSeq, final Short dcgSeqp, DominioClassificacaoDiagnostico classificacao) {
		return getMbcDiagnosticoDescricaoDAO().buscarMbcDiagnosticoDescricao(dcgCrgSeq, dcgSeqp, classificacao);
	}

	@Override
	@BypassInactiveModule
	public List<MbcDiagnosticoDescricao> listarDiagnosticoDescricaoPorDcgCrgSeqEClassificacao(Integer dcgCrgSeq, DominioClassificacaoDiagnostico classificacao) {
		return getMbcDiagnosticoDescricaoDAO().listarDiagnosticoDescricaoPorDcgCrgSeqEClassificacao(dcgCrgSeq, classificacao);
	}

	private MbcExtratoCirurgiaDAO getMbcExtratoCirurgiaDAO() {
		return mbcExtratoCirurgiaDAO;
	}
	
	private MbcDestinoPacienteDAO getMbcDestinoPacienteDAO() {
		return mbcDestinoPacienteDAO;
	}

	@Override
	@BypassInactiveModule
	public MbcExtratoCirurgia buscarMotivoCancelCirurgia(Integer seq) {
		return getMbcExtratoCirurgiaDAO().buscarMotivoCancelCirurgia(seq);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaAnestesias> listarFichasAnestesiasPorCodigoPacienteComGsoPacCodigoNulo(Integer pacCodigo) {
		return getMbcFichaAnestesiasDAO().listarFichasAnestesiasPorCodigoPacienteComGsoPacCodigoNulo(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaAnestesias> listarFichasAnestesias(Integer pacCodigo, Integer gsoPacCodigo, Short gsoSequence) {
		return getMbcFichaAnestesiasDAO().listarFichasAnestesias(pacCodigo, gsoPacCodigo, gsoSequence);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaAnestesias> listarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull(Integer crgSeq, DominioIndPendenteAmbulatorio pendente) {
		return getMbcFichaAnestesiasDAO().listarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull(crgSeq, pendente);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaAnestesias> pesquisarMbcFichaAnestesiasAtdGso(Integer atdSeq, Integer gsoPacCodigo, Short gsoSeqp) {
		return getMbcFichaAnestesiasDAO().pesquisarMbcFichaAnestesiasAtdGso(atdSeq, gsoPacCodigo, gsoSeqp);
	}

	private MbcFiguraDescricoesDAO getMbcFiguraDescricoesDAO() {
		return mbcFiguraDescricoesDAO;
	}

	@Override
	@BypassInactiveModule
	public MbcFiguraDescricoes buscarFiguraDescricoes(Integer dcgCrgSeq, Short seqp) {
		return getMbcFiguraDescricoesDAO().buscarFiguraDescricoes(dcgCrgSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public Integer obterMaxFDCSeqp(Integer crgSeq, Short seqp) {
		return getMbcFiguraDescricoesDAO().obterMaxFDCSeqp(crgSeq, seqp);
	}

	private MbcImagemDescricoesDAO getMbcImagemDescricoesDAO() {
		return mbcImagemDescricoesDAO;
	}

	@Override
	@BypassInactiveModule
	public MbcImagemDescricoes buscarImagemDescricoes(Integer fdcDcgCrgSeq, Short fdcDcgSeqp, Integer fdcSeqp) {
		return getMbcImagemDescricoesDAO().buscarImagemDescricoes(fdcDcgCrgSeq, fdcDcgSeqp, fdcSeqp);
	}

	private MbcNotaAdicionaisDAO getMbcNotaAdicionaisDAO() {
		return mbcNotaAdicionaisDAO;
	}

	private MbcNotaAdicionaisRN getMbcNotaAdicionaisRN() {
		return mbcNotaAdicionaisRN;
	}

	@Override
	@BypassInactiveModule
	public MbcNotaAdicionais buscarNotaAdicionais(Integer dcgCrgSeq, Short dcgSeqp) {
		return getMbcNotaAdicionaisDAO().buscarNotaAdicionais(dcgCrgSeq, dcgSeqp);
	}

	@Override
	@BypassInactiveModule
	public MbcNotaAdicionais buscarNotaAdicionais1(Integer dcgCrgSeqC1, Short seqpC1, Integer dcgCrgSeq, Short dcgSeqp, Integer seqp) {
		return getMbcNotaAdicionaisDAO().buscarNotaAdicionais1(dcgCrgSeqC1, seqpC1, dcgCrgSeq, dcgSeqp, seqp);
	}

	@Override
	@BypassInactiveModule
	public MbcNotaAdicionais buscarNotaAdicionais2(Integer dcgCrgSeq, Short dcgSeqp, Integer seqp) {
		return getMbcNotaAdicionaisDAO().buscarNotaAdicionais2(dcgCrgSeq, dcgSeqp, seqp);
	}

	@Override
	@BypassInactiveModule
	public Integer obterMaxNTASeqp(Integer crgSeq, Short seqp) {
		return getMbcNotaAdicionaisDAO().obterMaxNTASeqp(crgSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public Integer obterNTASeqp(Integer crgSeq, Short seqp) {
		return getMbcNotaAdicionaisDAO().obterNTASeqp(crgSeq, seqp);
	}

	private MbcProcDescricoesDAO getMbcProcDescricoesDAO() {
		return mbcProcDescricoesDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MbcProcDescricoes> buscarProcDescricoes(Integer dcgCrgSeq, Integer seqp) {
		return getMbcProcDescricoesDAO().buscarProcDescricoes(dcgCrgSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicos(final Object param, final String order, final Integer maxResults, final DominioSituacao situacao) {
		return getMbcProcedimentoCirurgicoDAO().pesquisarProcedimentosCirurgicosPorCodigoDescricao(param, order, maxResults, situacao);
	}
	
	@Override
	@BypassInactiveModule
	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicosPorCodigoDescricao(Object filtro) {
		return getMbcProcedimentoCirurgicoDAO().pesquisarProcedimentosCirurgicosPorCodigoDescricao(filtro);
	}

	@Override
	@BypassInactiveModule
	public MbcProcedimentoCirurgicos obterMbcProcedimentoCirurgicosPorId(Integer seq) {
		return getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarProcedimentosCirurgicosCount(final Object param, final DominioSituacao situacao) {
		return getMbcProcedimentoCirurgicoDAO().pesquisarProcedimentosCirurgicosPorCodigoDescricaoCount(param, situacao);
	}

	@Override
	@BypassInactiveModule
	public List<MbcProcedimentoCirurgicos> buscaProcedimentoCirurgicosRealizadoLeito(Object objPesquisa) {
		return getMbcProcedimentoCirurgicoDAO().buscaProcedimentoCirurgicosRealizadoLeito(objPesquisa);
	}

	@Override
	@BypassInactiveModule
	public List<MbcProcedimentoCirurgicos> listarProcedimentoCirurgicos(final Object objPesquisa) {
		return this.getMbcProcedimentoCirurgicoDAO().buscaProcedimentoCirurgicos(objPesquisa);
	}

	@Override
	@BypassInactiveModule
	public List<MbcProcedimentoCirurgicos> listarProcedimentoCirurgicos(final Object objPesquisa, Integer maxResults) {
		return this.getMbcProcedimentoCirurgicoDAO().buscaProcedimentoCirurgicos(objPesquisa, maxResults);
	}
	
	@Override
	@BypassInactiveModule
	public Long listarProcedimentoCirurgicosCount(final Object objPesquisa) {
		return this.getMbcProcedimentoCirurgicoDAO().buscaProcedimentoCirurgicosCount(objPesquisa);
	}
	
	@Override
	public List<MbcEquipamentoCirurgico> buscaEquipamentosCirurgicos(Object objPesquisa) {
		return getMbcEquipamentoCirurgicoDAO().buscaEquipamentosCirurgicos(objPesquisa);
	}

	@Override
	public Long buscaEquipamentosCirurgicosCount(Object objPesquisa) {
		return getMbcEquipamentoCirurgicoDAO().buscaEquipamentosCirurgicosCount(objPesquisa);
	}

	@Override
	@BypassInactiveModule
	public MbcProcedimentoCirurgicos obterProcedimentoCirurgico(final Integer seq) {
		return this.getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public List<MbcProcEspPorCirurgias> pesquisarMbcProcEspPorCirurgias(final Object objPesquisa, final String ppcEprPciSeq, final String ppcEprEspSeq,
			final DominioIndRespProc indRespProc) {
		return getMbcProcEspPorCirurgiasDAO().pesquisarMbcProcEspPorCirurgias(objPesquisa, ppcEprPciSeq, ppcEprEspSeq, indRespProc);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarMbcProcEspPorCirurgiasCount(final Object objPesquisa, final String ppcEprPciSeq, final String ppcEprEspSeq, final DominioIndRespProc indRespProc) {
		return getMbcProcEspPorCirurgiasDAO().pesquisarMbcProcEspPorCirurgiasCount(objPesquisa, ppcEprPciSeq, ppcEprEspSeq, indRespProc);
	}

	@Override
	@BypassInactiveModule
	public List<MbcProcEspPorCirurgias> retornaProcEspCirurgico(MbcCirurgias cirurgia) {
		return getMbcProcEspPorCirurgiasDAO().retornaProcEspCirurgico(cirurgia);
	}

	@Override
	@BypassInactiveModule
	public List<ProcedimentosPOLVO> pesquisarProcedimentosPOL(Integer codigo) {
		return getMbcProcEspPorCirurgiasDAO().pesquisarProcedimentosPOL(codigo);
	}

	@Override
	@BypassInactiveModule
	public String obterPacOruAccNummer(Integer seq) {
		return getMbcProcEspPorCirurgiasDAO().obterPacOruAccNummer(seq);
	}

	@Override
	@BypassInactiveModule
	public List<MbcProcEspPorCirurgias> obterProcedimentosCirurgicos(Integer atdSeq, Integer pacCodigo) throws ApplicationBusinessException {
		return getMbcProcEspPorCirurgiasDAO().obterProcedimentosCirurgicos(atdSeq, pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public MbcProcEspPorCirurgias obterMbcProcEspPorCirurgiasPorChavePrimaria(MbcProcEspPorCirurgiasId id) {
		return getMbcProcEspPorCirurgiasDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public List<MbcProcEspPorCirurgias> pesquisarProcEspSemProcDiagTerapPorDdtSeqEDdtCrgSeq(Integer ddtSeq, Integer ddtCrgSeq) {
		return getMbcProcEspPorCirurgiasDAO().pesquisarProcEspSemProcDiagTerapPorDdtSeqEDdtCrgSeq(ddtSeq, ddtCrgSeq);
	}
	
	@Override
	public List<MbcEspecialidadeProcCirgs> pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq(Short espSeq, Integer pciSeq) {
		return getMbcEspecialidadeProcCirgsDAO().pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq(espSeq, pciSeq);
	}
	
	@Override
	public List<MbcEspecialidadeProcCirgs> pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq2(Short espSeq, Integer pciSeq) {
		return getMbcEspecialidadeProcCirgsDAO().pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq2(espSeq, pciSeq);
	}
	
	protected MbcEspecialidadeProcCirgsDAO getMbcEspecialidadeProcCirgsDAO() {
		return mbcEspecialidadeProcCirgsDAO;
	}
	
	@Override
	public void persistirProcEspPorCirurgias(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		getMbcProcEspPorCirurgiasRN().persistirProcEspPorCirurgias(procEspPorCirurgia);
	}
	
	@Override
	public void removerMbcProcEspPorCirurgias(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		getMbcProcEspPorCirurgiasRN().removerMbcProcEspPorCirurgias(procEspPorCirurgia);
	}
	
	@Override
	public void removerMbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendada solicHemoCirgAgendada) throws BaseException {
		getMbcSolicHemoCirgAgendadaON().excluirMbcSolicHemoCirgAgendada(solicHemoCirgAgendada);
	}
	
	protected MbcProcEspPorCirurgiasRN getMbcProcEspPorCirurgiasRN() {
		return mbcProcEspPorCirurgiasRN;
	}

	@Override
	@BypassInactiveModule
	public MbcProfCirurgias buscaRapServidorPorCrgSeqEIndResponsavel(Integer crgSeq, DominioSimNao resp) {
		return getMbcProfCirurgiasDAO().buscaRapServidorPorCrgSeqEIndResponsavel(crgSeq, resp);
	}

	@Override
	@BypassInactiveModule
	public MbcProfCirurgias retornaResponsavelCirurgia(MbcCirurgias cirurgia) {
		return getMbcProfCirurgiasDAO().retornaResponsavelCirurgia(cirurgia);
	}

	@Override
	@BypassInactiveModule
	public List<MbcProfDescricoes> buscarProfDescricoes(Integer dcgCrgSeq, Short dcgSeq) {
		return getMbcProfDescricoesDAO().buscarProfDescricoes(dcgCrgSeq, dcgSeq);
	}

	@Override
	public MbcProfDescricoes obterMbcProfDescricoesPorChavePrimaria(MbcProfDescricoesId mbcProfDescricoesId){
		return getMbcProfDescricoesDAO().obterPorChavePrimaria(mbcProfDescricoesId);
	}

	@Override
	@BypassInactiveModule
	public List<MbcFichaProcedimento> obterFichaProcedimentoComProcedimentoCirurgicoByFichaAnestesia(Long seqMbcFichaAnestesia, DominioSituacaoExame situacaoProcedimento) {
		return getMbcFichaProcedimentoDAO().obterFichaProcedimentoComProcedimentoCirurgicoByFichaAnestesia(seqMbcFichaAnestesia, situacaoProcedimento);
	}

	protected MbcFichaProcedimentoDAO getMbcFichaProcedimentoDAO() {
		return mbcFichaProcedimentoDAO;
	}

	@Override
	@BypassInactiveModule
	public void removerMbcTmpFichaFarmacoAnteriores(Integer qtdeDias, Boolean flush) {
		getMbcTmpFichaFarmacoDAO().removerMbcTmpFichaFarmacoAnteriores(qtdeDias, flush);
	}

	@Override
	@BypassInactiveModule
	public List<MbcAgendas> pesquisarCirurgiasListaDeEspera(Integer pacCodigo, DominioSituacaoAgendas situacao) {
		return getMbcAgendasDAO().pesquisarCirurgiasListaDeEspera(pacCodigo, situacao);
	}

	@Override
	@BypassInactiveModule
	public List<MbcCirurgias> verificarSePacienteTemCirurgia(Integer pacCodigo, Integer numDiasPassado, Integer numDiasFuturo) {
		return getMbcCirurgiasDAO().verificarSePacienteTemCirurgia(pacCodigo, numDiasPassado, numDiasFuturo);
	}

	@Override
	@BypassInactiveModule
	public List<MbcAgendas> pesquisarCirurgiaAgendadaPorPaciente(Integer pacCodigo, Integer nroDiasPraFrente, Integer nroDiasPraTras) {
		return getMbcAgendasDAO().pesquisarCirurgiaAgendadaPorPaciente(pacCodigo, nroDiasPraFrente, nroDiasPraTras);
	}

	@Override
	@BypassInactiveModule
	public MbcProcDescricoes buscarProcDescricoes(Integer dcgCrgSeq, Short dcgSeqp) {
		return getMbcProcDescricoesDAO().buscarProcDescricoes(dcgCrgSeq, dcgSeqp);
	}

	@Override
	@BypassInactiveModule
	public MbcCirurgias obterCirurgiaPorSeq(Integer seq) {
		return getMbcCirurgiasDAO().obterCirurgiaPorSeq(seq);
	}

	@Override
	@BypassInactiveModule
	public MbcTipoAnestesias obterMbcTipoAnestesiaPorChavePrimaria(Short seq) {
		return getMbcTipoAnestesiasDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public MbcCirurgias obterCirurgiaProjetpPesquisa(Integer seq) {
		return getMbcCirurgiasDAO().obterCirurgiaProjetpPesquisa(seq);
	}

	@Override
	@BypassInactiveModule
	public MbcFichaAnestesias obterMbcFichaAnestesiaByMcoGestacao(McoGestacoes gestacao, Integer atdSeq, DominioIndPendenteAmbulatorio pendente) {
		return getMbcFichaAnestesiasDAO().obterMbcFichaAnestesiaByMcoGestacao(gestacao, atdSeq, pendente);
	}

	@Override
	@BypassInactiveModule
	public List<MbcCirurgias> pesquisarCirurgiasPacienteDataAtualEDiaSeguinte(AipPacientes paciente) {
		return this.getMbcCirurgiasDAO().pesquisarCirurgiasPacienteDataAtualEDiaSeguinte(paciente);
	}
	
	@Override
	@BypassInactiveModule
	public List<MbcCirurgias> pesquisarCirurgiasPacienteDataEntreDias(AipPacientes paciente) {
		return this.getMbcCirurgiasDAO().pesquisarCirurgiasPacienteDataEntreDias(paciente);
	}

	@Override
	@BypassInactiveModule
	public List<MbcAgendas> pesquisarCirurgiasAgendadasPorResponsavel(MbcCirurgias cirurgia, MbcProfCirurgias profCirurgia) {
		return getMbcAgendasDAO().pesquisarCirurgiasAgendadasPorResponsavel(cirurgia, profCirurgia);
	}

	@Override
	@BypassInactiveModule
	public List<PlanejamentoCirurgicoVO> pesquisarRelatorioPlanejamentoCirurgico(Date dataCirurgia, Short seqEspecialidade, Integer pacCodigo, Integer pucSerMatricula,
			Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf) {
		return getMbcAgendaHemoterapiaDAO().pesquisarRelatorioPlanejamentoCirurgico(dataCirurgia, seqEspecialidade, pacCodigo, pucSerMatricula, pucSerVinCodigo, pucUnfSeq,
				pucIndFuncaoProf);
	}

	protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO() {
		return mbcAgendaHemoterapiaDAO;
	}

	@Override
	public MbcCirurgias verificarSeAgendamentoTemCirurgiaRealizada(Integer agdSeq) {
		return getMbcCirurgiasDAO().verificarSeAgendamentoTemCirurgiaRealizada(agdSeq);
	}

	@Override
	public MbcDescricaoItens obterMbcDescricaoItensPorId(final MbcDescricaoItensId id) {
		return getMbcDescricaoItensDAO().obterPorChavePrimaria(id);
	}

	@Override
	public MbcDescricaoItens obterMbcDescricaoItensOriginal(final MbcDescricaoItensId id) {
		return getMbcDescricaoItensDAO().obterOriginal(id);
	}
	
	@Override
	public void desatacharMbcDescricaoItensOriginal(final MbcDescricaoItens elemento) {
		getMbcDescricaoItensDAO().desatachar(elemento);
	}
	
	@Override
	public LinhaReportVO obterDataInicioFimCirurgia(Integer crgSeq, Date dthrInicioCirg) {
		return getMbcDescricaoItensDAO().obterDataInicioFimCirurgia(crgSeq, dthrInicioCirg);
	}

	@Override
	public AghUnidadesFuncionais obterUnidadeFuncionalCirurgia(final String nomeMicrocomputador) throws ApplicationBusinessException {
		return this.getBlocoCirurgicoON().obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('cidPorEquipeConsulta','pesquisar')}")
	public List<MbcCidUsualEquipe> pesquisarCidUsualEquipe(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, Short eqpSeq) {
		return this.getMbcCidUsualEquipeDAO().pesquisarCidUsualEquipe(firstResult, maxResult, orderProperty, asc, eqpSeq);
	}

	@Override
	public Long pesquisarCidUsualEquipeCount(Short eqpSeq) {
		return this.getMbcCidUsualEquipeDAO().pesquisarCidUsualEquipeCount(eqpSeq);
	}

	protected MbcCidUsualEquipeDAO getMbcCidUsualEquipeDAO() {
		return mbcCidUsualEquipeDAO;
	}

	@Override
	@Secure("#{s:hasPermission('equipamentoCirurgicoConsulta','pesquisar')}")
	public List<MbcEquipamentoCirurgico> pesquisarEquipamentoCirurgico(final Short codigo, final String descricao, final DominioSituacao situacao, final Integer firstResult,
			final Integer maxResults, final String orderProperty, final Boolean asc) {
		return this.getMbcEquipamentoCirurgicoDAO().pesquisarEquipamentoCirurgico(codigo, descricao, situacao, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	@Secure("#{s:hasPermission('equipamentoCirurgicoConsulta','pesquisar')}")
	public Long pesquisarEquipamentoCirurgicoCount(final Short codigo, final String descricao, final DominioSituacao situacao) {
		return this.getMbcEquipamentoCirurgicoDAO().pesquisarEquipamentoCirurgicoCount(codigo, descricao, situacao);
	}
	
	@Override
	public List<MbcEquipamentoCirurgico> pesquisarEquipamentoCirurgicoModuloCustos(final Short codigo, final String descricao, final DominioSituacao situacao, final Integer firstResult,
			final Integer maxResults, final String orderProperty, final Boolean asc) {
		return this.getMbcEquipamentoCirurgicoDAO().pesquisaEquipamentoCirurgicoOrderByDesc(codigo, descricao, situacao, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long pesquisarEquipamentoCirurgicoCountModuloCusto(final Short codigo, final String descricao, final DominioSituacao situacao) {
		return this.getMbcEquipamentoCirurgicoDAO().pesquisarEquipamentoCirurgicoCount(codigo, descricao, situacao);
	}
	
	@Override
	public MbcEquipamentoCirurgico obterEquipamentoCirurgicoByID(final Short codigo){
		return this.getMbcEquipamentoCirurgicoDAO().obterPorChavePrimaria(codigo);
	}

	protected MbcEquipamentoCirurgicoDAO getMbcEquipamentoCirurgicoDAO() {
		return mbcEquipamentoCirurgicoDAO;
	}

	@Override
	public Long pesquisarMotivosDemoraSalaRecCount(Short codigo, String descricao, DominioSituacao situacao) {
		return this.getMbcMotivoDemoraSalaRecDAO().pesquisarMotivosDemoraSalaRecCount(codigo, descricao, situacao);
	}

	@Override
	public List<MbcMotivoDemoraSalaRec> pesquisarMotivosDemoraSalaRec(Short codigo, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResult) {
		return getMbcMotivoDemoraSalaRecDAO().pesquisarMotivosDemoraSalaRec(codigo, descricao, situacao, firstResult, maxResult);
	}

	protected MbcMotivoDemoraSalaRecDAO getMbcMotivoDemoraSalaRecDAO() {
		return mbcMotivoDemoraSalaRecDAO;
	}

	@Override
	public List<PesquisaAgendarProcedimentosVO> pesquisarMbcDisponibilidadeHorario(AghUnidadesFuncionais unidadeFuncional, MbcSalaCirurgica salaCirurgica,
			AghEspecialidades especialidade, Date dataAgenda) throws ApplicationBusinessException {
		return getAgendaProcedimentosRN().pesquisarMbcDisponibilidadeHorario(unidadeFuncional, salaCirurgica, especialidade, dataAgenda);
	}

	@Override
	public boolean temTarget(String componente, String target) throws ApplicationBusinessException {
		return getAgendaProcedimentosON().temTarget(componente, target);
	}

	private AgendaProcedimentosON getAgendaProcedimentosON() {
		return agendaProcedimentosON;
	}
	
	private AgendaProcedimentosProfissionaisON getAgendaProcedimentosProfissionaisON() {
		return agendaProcedimentosProfissionaisON;
	}
	
	private AgendaProcedimentosRN getAgendaProcedimentosRN() {
		return agendaProcedimentosRN;
	}
	
	private AgendaProcedimentosFuncoesON getAgendaProcedimentosFuncoesON() {
		return agendaProcedimentosFuncoesON;
	}
	
 	@Override
	public void executarEventoBotaoPressionadoListaCirurgias(final CirurgiaVO crgVO) {
		getListarCirurgiasON().executarEventoBotaoPressionadoListaCirurgias(crgVO);
	}

	@Override
	public void popularCancelamentoCirurgia(final TelaListaCirurgiaVO tela, final CirurgiaVO crg) throws ApplicationBusinessException {
		getListarCirurgiasON().popularCancelamentoCirurgia(tela, crg);
	}
	
	@Override
	public void popularListaValoresQuestaoEComplemento(final CirurgiaVO crg, final Short mtcSeq) {
		getListarCirurgiasON().popularListaValoresQuestaoEComplemento(crg, mtcSeq);
	}
	
	@Override
	public Boolean verificarCancelamentoCirurgiaComDescricao(final Integer crgSeq) {
		return getListarCirurgiasON().verificarCancelamentoCirurgiaComDescricao(crgSeq);
	}
	
	@Override
	public boolean habilitaBotaoVisualizarRegistros(Integer atdSeqSelecionado, boolean isCheckSalaRecuperacao, Date crgSelecionada) throws ApplicationBusinessException{
		return getListarCirurgiasON().habilitaBotaoVisualizarRegistros(atdSeqSelecionado, isCheckSalaRecuperacao, crgSelecionada);
	}
	
	@Override
	public void validarQuestaoValorValidoEComplemento(MbcQuestao questao, 
			MbcValorValidoCanc valorValidoCanc, String complementoCanc) throws ApplicationBusinessException {
		getListarCirurgiasON().validarQuestaoValorValidoEComplemento(questao, valorValidoCanc, complementoCanc);
	}
	
	@Override
	public void verificarPermissaoCancelamentoCirurgia(final TelaListaCirurgiaVO telaVO, final CirurgiaVO crgVO)
			throws ApplicationBusinessException {
		getListarCirurgiasON().verificarPermissaoCancelamentoCirurgia(telaVO, crgVO);
	}

	@Override
	public void verificarCirurgiaPossuiDescricaoCirurgica(final TelaListaCirurgiaVO telaVO, final CirurgiaVO crgVO) 
			throws ApplicationBusinessException {
		getListarCirurgiasON().verificarCirurgiaPossuiDescricaoCirurgica(telaVO, crgVO);
	}


	@Override
	public MbcMotivoCancelamento obterMotivoCancelamentoPorChavePrimaria(Short seq) {
		return getMbcMotivoCancelamentoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@Secure("#{s:hasPermission('motivoCancelamentoConsulta','pesquisar')}")
	public List<MbcMotivoCancelamento> pesquisarMotivosCancelamento(Short codigo, String descricao, Boolean erroAgendamento, Boolean destSr,
			DominioMotivoCancelamento classificacao, DominioSituacao situacao, Integer firstResult, Integer maxResults, String orderProperty, Boolean asc) {
		return this.getMbcMotivoCancelamentoDAO().pesquisarMotivosCancelamento(codigo, descricao, erroAgendamento, destSr, classificacao, situacao, firstResult, maxResults,
				orderProperty, asc);
	}

	@Override
	@Secure("#{s:hasPermission('motivoCancelamentoConsulta','pesquisar')}")
	public Long pesquisarMotivosCancelamentoCount(Short codigo, String descricao, Boolean erroAgendamento, Boolean destSr, DominioMotivoCancelamento classificacao,
			DominioSituacao situacao) {
		return this.getMbcMotivoCancelamentoDAO().pesquisarMotivosCancelamentoCount(codigo, descricao, erroAgendamento, destSr, classificacao, situacao);

	}

	protected MbcMotivoCancelamentoDAO getMbcMotivoCancelamentoDAO() {
		return mbcMotivoCancelamentoDAO;
	}

	@Override
	@Secure("#{s:hasPermission('questaoMotivoCancelamentoConsulta','pesquisar')}")
	public List<MbcQuestao> listarMbcQuestoes(Short mtcSeq) {
		return this.getMbcQuestaoDAO().listarQuestoes(mtcSeq);
	}

	protected MbcQuestaoDAO getMbcQuestaoDAO() {
		return mbcQuestaoDAO;
	}

	@Override
	@Secure("#{s:hasPermission('questaoMotivoCancelamentoConsulta','pesquisar')}")
	public List<MbcValorValidoCanc> listarMbcValorValidoCancPorQuestao(Short qesMtcSeq, Integer qesSeq) {
		return this.getMbcValorValidoCancDAO().listarMbcValorValidoCancPorQuestao(qesMtcSeq, qesSeq);
	}
	
	public MbcValorValidoCanc obterMbcValorValidoCancById(MbcValorValidoCancId id){
		return mbcValorValidoCancDAO.obterOriginal(id);
	}

	protected MbcValorValidoCancDAO getMbcValorValidoCancDAO() {
		return mbcValorValidoCancDAO;
	}

	@Override
	@Secure("#{s:hasPermission('escalaCirurgicaExecutar','executar')}")
	public void removerEscalaCirurgica(MbcControleEscalaCirurgica controleEscalaCirurgica) throws BaseException {
		mbcControleEscalaCirurgicaDAO.removerMbcControleEscalaCirurgica(controleEscalaCirurgica);
		
	}

	@Override
	@Secure("#{s:hasPermission('escalaCirurgicaExecutar','executar')}")
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void inserirControleEscalaCirurgica(MbcControleEscalaCirurgica controleEscalaCirurgica, String nomeMicrocomputador) throws BaseException {
		this.mbcControleEscalaCirurgicaRN.inserir(controleEscalaCirurgica, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('escalaCirurgicaExecutar','executar')}")
	public void atualizarControleEscalaCirurgica(MbcControleEscalaCirurgica controleEscalaCirurgica, String nomeMicrocomputador) throws BaseException {
		mbcControleEscalaCirurgicaON.atualizar(controleEscalaCirurgica, nomeMicrocomputador);
	}

	@Override
	@BypassInactiveModule
	public List<MbcProcEspPorCirurgias> obterProcedimentosEspeciaisPorCirurgia(Integer codigoPaciente) {
		return getMbcProcEspPorCirurgiasDAO().obterProcedimentosEspeciaisPorCirurgia(codigoPaciente);
	}

	@Override
	@BypassInactiveModule 
	public List<MbcFichaAnestesias> listarFichasAnestesiasPorItemSolicExame(Integer soeSeq, Short seqp, DominioIndPendenteAmbulatorio indPendAmbulat, Boolean dthrMvtoNull) {
		return getMbcFichaAnestesiasDAO().listarFichasAnestesiasPorItemSolicExame(soeSeq, seqp, indPendAmbulat, dthrMvtoNull);
	}

	@Override
	public List<MbcCirurgiaVO> listarMbcProcEspPorCirurgiasPorFatProcedAmbRealizado(Integer ppcCrgSeq, Integer phiSeq, Boolean isItemCirg) {
		return this.getMbcProcEspPorCirurgiasDAO().listarMbcProcEspPorCirurgiasPorFatProcedAmbRealizado(ppcCrgSeq, phiSeq, isItemCirg);
	}

	@Override
	public MbcCirurgias obterMbcCirurgiaPorSituacaoRealizada(Integer crgSeq) {
		return this.getMbcCirurgiasDAO().obterMbcCirurgiaPorSituacaoRealizada(crgSeq);
	}

	@Override
	public List<MbcCirurgiaVO> listarItensOrteseProtese(Integer ppcCrgSeq) {
		return this.getMbcCirurgiasDAO().listarItensOrteseProtese(ppcCrgSeq);
	}

	@Override
	public String obterIdadePorDataNascimento(Date dtNascimentoPaciente) {
		return getDescricaoCirurgicaON().obterIdadePorDataNascimento(dtNascimentoPaciente);
	}

	private DescricaoCirurgicaON getDescricaoCirurgicaON() {
		return descricaoCirurgicaON;
	}

	@Override
	public Object executarDescreverCirurgiaOuOutra(final TelaListaCirurgiaVO telaVO, final CirurgiaVO cirurgiaVO) throws ApplicationBusinessException {
		return getListaCirurgiasDescCirurgicaON().executarDescreverCirurgiaOuOutra(telaVO, cirurgiaVO);
	}

	@Override
	public Object executarEdicaoDescricaoCirurgica(final TelaListaCirurgiaVO tela, final CirurgiaVO cirurgiaVO) throws ApplicationBusinessException {
		return getListaCirurgiasDescCirurgicaON().executarEdicaoDescricaoCirurgica(tela, cirurgiaVO);
	}

	public boolean habilitarNotasAdicionais(final Integer crgSeq, final Short dcgSeqp) throws ApplicationBusinessException, ApplicationBusinessException{
		return getListaCirurgiasDescCirurgicaON().habilitarNotasAdicionais(crgSeq, dcgSeqp);
	}
	
	private ListaCirurgiasDescCirurgicaON getListaCirurgiasDescCirurgicaON() {
		return listaCirurgiasDescCirurgicaON;
	}

	@Override
	public List<ProfDescricaoCirurgicaVO> pesquisarProfAtuaUnidCirgConselhoPorServidorUnfSeq(
			Integer serMatricula, Short serVinCodigo, Short unfSeq, List<String> listaSigla, DominioSituacao situacao) {
		return getMbcProfAtuaUnidCirgsDAO().pesquisarProfAtuaUnidCirgConselhoPorServidorUnfSeq(serMatricula, serVinCodigo, unfSeq, listaSigla, situacao,null,null);
	}

	@Override
	public List<ProfDescricaoCirurgicaVO> pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(Integer firstResult,Integer maxResult,Object objPesquisa,
			Short unfSeq, DominioFuncaoProfissional indFuncaoProf, List<String> listaSigla, DominioSituacao situacao) {
		return getMbcProfAtuaUnidCirgsDAO().pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(firstResult, maxResult,objPesquisa, unfSeq, indFuncaoProf, listaSigla, situacao);
	}

	@Override
	public Long pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoCount(Object objPesquisa,
			Short unfSeq, DominioFuncaoProfissional indFuncaoProf, List<String> listaSigla, DominioSituacao situacao) {
		return getMbcProfAtuaUnidCirgsDAO().pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoCount(objPesquisa, unfSeq, indFuncaoProf, listaSigla, situacao);
	}
	
	@Override
	public List<ProfDescricaoCirurgicaVO> pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncao(Object objPesquisa,
			Short unfSeq, List<DominioFuncaoProfissional> listaIndFuncaoProf, List<String> listaSigla, DominioSituacao situacao) {
		return getMbcProfAtuaUnidCirgsDAO().pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncao(objPesquisa, unfSeq, listaIndFuncaoProf, listaSigla, situacao);
	}

	@Override
	public Long pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoCount(Object objPesquisa,
			Short unfSeq, List<DominioFuncaoProfissional> listaIndFuncaoProf, List<String> listaSigla, DominioSituacao situacao) {
		return getMbcProfAtuaUnidCirgsDAO().pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoCount(objPesquisa, unfSeq, listaIndFuncaoProf, listaSigla, situacao);
	}
	
	@Override
	public List<MbcProfCirurgias> pesquisarProfCirurgiasAnestesistaPorCrgSeq(Integer crgSeq) {
		return getDescricaoCirurgicaEquipeON().pesquisarProfCirurgiasAnestesistaPorCrgSeq(crgSeq);
	}

	@Override
	public List<MbcProfCirurgias> pesquisarProfCirurgiasEnfermeiroPorCrgSeq(Integer crgSeq) {
		return getDescricaoCirurgicaEquipeON().pesquisarProfCirurgiasEnfermeiroPorCrgSeq(crgSeq);
	}

	@Override
	public void inserirProfDescricoes(Integer dcgCrgSeq, Short dcgSeqp, ProfDescricaoCirurgicaVO profAtuaUnidCirgConselhoVO
			) throws ApplicationBusinessException {
		getDescricaoCirurgicaEquipeON().inserirProfDescricoes(dcgCrgSeq, dcgSeqp, profAtuaUnidCirgConselhoVO);
	}

 	@Override
	public void excluirProfDescricoes(final MbcProfDescricoes profDescricoes) throws ApplicationBusinessException {
		getProfDescricoesRN().excluirProfDescricoes(profDescricoes);
	}
	
	@Override
	@BypassInactiveModule
	public List<MbcProfDescricoes> pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, DominioTipoAtuacao tipoAtuacao) {
		return getMbcProfDescricoesDAO().pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao);
	}
	
	@Override
	public List<MbcProfDescricoes> pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpEListaTipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, 
			List<DominioTipoAtuacao> listaTipoAtuacao) {
		return getMbcProfDescricoesDAO().pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpEListaTipoAtuacao(dcgCrgSeq, dcgSeqp, listaTipoAtuacao);
	}
	
	@Override
	public List<MbcProfDescricoes> pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpServidorProfETipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, 
			Integer servidorMatricula, Short servidorVinCodigo, DominioTipoAtuacao tipoAtuacao) {
		return getMbcProfDescricoesDAO().pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpServidorProfETipoAtuacao(dcgCrgSeq, dcgSeqp,
				servidorMatricula, servidorVinCodigo, tipoAtuacao);
	}
	
	@Override
	public List<MbcProfDescricoes> pesquisarProfDescricoesOutrosPorDcgCrgSeqDcgSeqpETipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, DominioTipoAtuacao tipoAtuacao) {
		return getMbcProfDescricoesDAO().pesquisarProfDescricoesOutrosPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao);
	}
	
	@Override
	public MbcDescricaoItens montarDescricaoItens(MbcDescricaoCirurgica descricaoCirurgica,
			String achadosOperatorios, String observacao,
			DominioSimNao indIntercorrencia, DominioSimNao indPerdaSangue,
			Integer volumePerdaSangue, String intercorrenciaClinica
			) {
		return getDescricaoCirurgicaAchadosOperatoriosON().montarDescricaoItens(descricaoCirurgica, achadosOperatorios, observacao,
				indIntercorrencia, indPerdaSangue, volumePerdaSangue, intercorrenciaClinica);
	}
	
	@Override
	public void persistirDescricaoItens(MbcDescricaoItens descricaoItens) 
			throws BaseException {
		getDescricaoCirurgicaAchadosOperatoriosON().persistirDescricaoItens(descricaoItens);
	}
	
	@Override
	public void validarIntercorrenciaAchadosOperatorios(MbcDescricaoItens descricaoItens) throws ApplicationBusinessException {
		getDescricaoCirurgicaAchadosOperatoriosON().validarIntercorrenciaAchadosOperatorios(descricaoItens);
	}

	@Override
	public boolean atualizarDatasDescricaoCirurgica(final Integer dcgCrgSeq, final Short dcgSeqp,
												 final Date lVdcDataCirurgia, final Date dtiDthrInicioCir, final Date dtiDthrFimCirg,  
												 final Date dtHrEntradaSala,  final Date dtHrSaidaSala,    final Short grcUnfSeq, 
												 final Short sciSeqp, final boolean alterandoDtInicio, 	   final Short lPciTempoMinimo) throws BaseException{
		
		return getDescricaoItensON().atualizarDatasDescricaoCirurgica( dcgCrgSeq, dcgSeqp, lVdcDataCirurgia, 
															    dtiDthrInicioCir, dtiDthrFimCirg, dtHrEntradaSala,
															    dtHrSaidaSala, grcUnfSeq, sciSeqp, alterandoDtInicio, 
															    lPciTempoMinimo);
	}
	
	protected DescricaoItensON getDescricaoItensON() {
		return descricaoItensON;
	}
	
	@Override
	public MbcProfAtuaUnidCirgs obterMbcProfAtuaUnidCirgsPorChavePrimaria(MbcProfAtuaUnidCirgsId id) {
		return getMbcProfAtuaUnidCirgsDAO().obterPorChavePrimaria(id);
	}

	@Override
	public MbcProfAtuaUnidCirgs obterMbcProfAtuaUnidCirgs(MbcProfAtuaUnidCirgsId id) {
		return getMbcProfAtuaUnidCirgsDAO().obterMbcProfAtuaUnidCirgs(id);
	}
	
	private MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}

	private DescricaoCirurgicaEquipeON getDescricaoCirurgicaEquipeON() {
		return descricaoCirurgicaEquipeON;
	}

	private ProfDescricoesRN getProfDescricoesRN() {
		return profDescricoesRN;
	}
	
	private DescricaoCirurgicaAchadosOperatoriosON getDescricaoCirurgicaAchadosOperatoriosON() {
		return descricaoCirurgicaAchadosOperatoriosON;
	}
	
	

	@Override
	public List<LinhaReportVO> listarUnidadesFuncionaisPorUnidInternacao(String strPesquisa, Boolean ativo) {
		return getRelatorioPacientesComCirurgiaPorUnidadeON().listarUnidadesFuncionaisPorUnidInternacao(strPesquisa, ativo);
	}

	@Override
	public Long listarUnidadesFuncionaisPorUnidInternacaoCount(String strPesquisa, Boolean ativo) {
		return getRelatorioPacientesComCirurgiaPorUnidadeON().listarUnidadesFuncionaisPorUnidInternacaoCount(strPesquisa, ativo);
	}

	@Override
	public List<RelatorioPacientesComCirurgiaPorUnidadeVO> listarPacientesComCirurgia(Short seqUnidadeCirurgica, Short seqUnidadeInternacao, Date dataCirurgia) {
		return getRelatorioPacientesComCirurgiaPorUnidadeON().listarPacientesComCirurgia(seqUnidadeCirurgica, seqUnidadeInternacao, dataCirurgia);
	}
	
	@Override
	public List<RelatorioNotasDeConsumoDaSalaVO> listarCirurgiasPorSeqUnidadeFuncionalDataNumeroAgenda(Short seqUnidadeCirurgica, 
			Date dataCirurgia, Short numeroAgenda, Boolean nsDigitada) throws ApplicationBusinessException {
		return getRelatorioNotasDeConsumoDaSalaON().listarCirurgiasPorSeqUnidadeFuncionalDataNumeroAgenda(seqUnidadeCirurgica, dataCirurgia, numeroAgenda, nsDigitada);
	}
	
	@Override
	public List<RelatorioRegistroDaNotaDeSalaVO> listarRegistroNotaDeSalaMateriais(Integer crgSeq) {
		return getRelatorioRegistroDaNotaDeSalaON().listarProcedimentosMateriaisPorCirurgia(crgSeq);
		
	}
	
	@Override
	public List<ProtocoloEntregaNotasDeConsumoVO> listarRelatorioProtocoloEntregaNotasDeConsumo(
			Short unfSeq, Date data, DominioSimNao pacienteSus, DominioOrdenacaoProtocoloEntregaNotasConsumo ordenacao) {
		return getProtocoloEntregaNotasDeConsumoON().listarProtocoloEntregaNotasDeConsumo(unfSeq, data, pacienteSus, ordenacao);
	}
	
	@Override
	public List<NotificacoesGeraisVO> listarNotificacoesCirurgia(final Integer codigoPaciente) {
		return getMbcCirurgiasDAO().listarNotificacoesCirurgia(codigoPaciente);
	}

	@Override
	public void validarAgendaProcedimentoAdicionadoExistente(List<MbcAgendaProcedimento> agendaProcedimentoList, MbcAgendaProcedimento agendaProcedimento)
	throws ApplicationBusinessException {
		this.getMbcAgendaProcedimentoON().validarAgendaProcedimentoAdicionadoExistente(agendaProcedimentoList, agendaProcedimento);

	}

	@Override
	public TempoSalaAgendaVO validaTempoMinimo(Date tempoSala, VMbcProcEsp procEsp) throws ParseException {
		return this.getMbcAgendaProcedimentoON().validaTempoMinimo(tempoSala, procEsp);
	}

	@Override
	public RegimeProcedimentoAgendaVO populaRegimeSus(DominioRegimeProcedimentoCirurgicoSus dominioRegimeProc, VMbcProcEsp procEsp) {
		return this.getMbcAgendaProcedimentoON().populaRegimeSus(dominioRegimeProc, procEsp);
	}

	@Override
	public void validarQtdeAgendaProcedimento(MbcAgendaProcedimento agendaProcedimento) throws ApplicationBusinessException {
		this.getMbcAgendaProcedimentoON().validarQtdeAgendaProcedimento(agendaProcedimento);

	}
	
	@Override
	public void inserirProf(Integer dcgCrgSeq, Short dcgSeqp, ProfDescricaoCirurgicaVO profDescricaoCirurgicaVO
			) throws ApplicationBusinessException {
		descricaoCirurgicaEquipeON.inserirProfDescricoes(dcgCrgSeq, dcgSeqp, profDescricaoCirurgicaVO);
	}
	

	@Override
	public Set<MbcAgendaHemoterapia> buscarAgendaHemoterapia(final Integer pciSeq, final Short espSeq) {
		return getMbcAgendaHemoterapiaON().buscarAgendaHemoterapia(pciSeq, espSeq);
	}

	private MbcAgendaProcedimentoON getMbcAgendaProcedimentoON() {
		return mbcAgendaProcedimentoON;
	}

	@Override
	public AipPacientes obterPacientePorLeito(final AinLeitos leito) throws BaseException {
		return getMbcCirurgiasON().obterPacientePorLeito(leito);
	}

	@Override
	public void persistirMbcAvalPreSedacao(final MbcAvalPreSedacao mbcAvalPreSedacao)throws ApplicationBusinessException {
		getAvalPreSedacaoRN().persistirMbcAvalPreSedacao(mbcAvalPreSedacao);
	}
	
	@Override
	public MbcAvalPreSedacao pesquisarMbcAvalPreSedacaoPorDdtSeq(MbcAvalPreSedacaoId id){
		return mbcAvalPreSedacaoDAO.pesquisarMbcAvalPreSedacaoPorDdtSeq(id);
	}
	
	public AvalPreSedacaoRN getAvalPreSedacaoRN() {
		return AvalPreSedacaoRN;
	}

	public void setAvalPreSedacaoRN(AvalPreSedacaoRN avalPreSedacaoRN) {
		AvalPreSedacaoRN = avalPreSedacaoRN;
	}

	private MbcCirurgiasON getMbcCirurgiasON() {
		return mbcCirurgiasON;
	}

	protected MbcAgendaHemoterapiaON getMbcAgendaHemoterapiaON() {
		return mbcAgendaHemoterapiaON;
	}

	@Override
	public void validarAgendaOrteseproteseAdicionadoExistente(List<MbcAgendaOrtProtese> agendaOrteseProteseList, MbcAgendaOrtProtese agendaOrteseProtese)
	throws ApplicationBusinessException {
		getMbcAgendaOrtProteseON().validarAgendaOrteseproteseAdicionadoExistente(agendaOrteseProteseList, agendaOrteseProtese);
	}

	@Override
	public void validarQtdeOrtProtese(MbcAgendaOrtProtese agendaOrteseProtese) throws ApplicationBusinessException {
		getMbcAgendaOrtProteseON().validarQtdeOrtProtese(agendaOrteseProtese);
	}

	private MbcAgendaOrtProteseON getMbcAgendaOrtProteseON() {
		return mbcAgendaOrtProteseON;
	}

	private DiagnosticoDescricaoON getDiagnosticoDescricaoON() {
		return diagnosticoDescricaoON;
	}
	
	@Override
	public MbcDiagnosticoDescricao obterDiagnosticoDescricaoPorChavePrimaria(final MbcDiagnosticoDescricaoId id) {
		return getMbcDiagnosticoDescricaoDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void inserirDiagnosticoDescricoesPreOperatorio(MbcDiagnosticoDescricao diagnosticoDescricao,
			List<MbcDiagnosticoDescricao> listaPreOperatorio, List<MbcDiagnosticoDescricao> listaPosOperatorio) throws ApplicationBusinessException {

		getDiagnosticoDescricaoON().inserirDiagnosticoDescricoesPreOperatorio(diagnosticoDescricao, listaPreOperatorio, listaPosOperatorio);
	}

	@Override
	public void inserirDiagnosticoDescricoesPosOperatorio(MbcDiagnosticoDescricao diagnosticoDescricao,
			List<MbcDiagnosticoDescricao> listaPreOperatorio, List<MbcDiagnosticoDescricao> listaPosOperatorio) throws ApplicationBusinessException {

		getDiagnosticoDescricaoON().inserirDiagnosticoDescricoesPosOperatorio(diagnosticoDescricao, listaPreOperatorio, listaPosOperatorio);
	}
	
	@Override
	public void alterarDiagnosticoDescricoes(MbcDiagnosticoDescricao diagnosticoDescricao) throws ApplicationBusinessException {
		getDiagnosticoDescricaoON().alterarDiagnosticoDescricao(diagnosticoDescricao);
	}

	@Override
	public void excluirDiagnosticoDescricoes(
			final MbcDiagnosticoDescricaoId id)
			throws ApplicationBusinessException, ApplicationBusinessException {

		getDiagnosticoDescricaoON().excluirDiagnosticoDescricoes(id);
	}

	@Override
	public Collection<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO> listarPacientesEmSalaRecuperacaoPorUnidade(Short seqUnidadeCirurgica, String ordemListagem)
	throws ApplicationBusinessException {
		return this.getRelatorioPacientesEmSalaRecuperacaoPorUnidadeON().listarPacientesEmSalaRecuperacaoPorUnidade(seqUnidadeCirurgica, ordemListagem);
	}

	protected RelatorioPacientesEmSalaRecuperacaoPorUnidadeON getRelatorioPacientesEmSalaRecuperacaoPorUnidadeON() {
		return relatorioPacientesEmSalaRecuperacaoPorUnidadeON;
	}

	@Override
	public MbcAgendas obterAgendaPorChavePrimaria(Integer seqMbcAgenda) {
		return getMbcAgendasDAO().obterPorChavePrimaria(seqMbcAgenda);
	}

	@Override
	public void desfazCarregamentoDescricaoCirurgica(final Integer crgSeq, final Short crgSeqp) throws ApplicationBusinessException, ApplicationBusinessException{
		getListaCirurgiasDescCirurgicaON().desfazCarregamentoDescricaoCirurgica(crgSeq, crgSeqp);
	}
	
	@Override
	public Boolean pFinalizaDescricao(Integer crgSeq, Short dcgSeqp, String nomeMicrocomputador, DominioSimNao indIntercorrencia, DominioSimNao indPerdaSangue, String intercorrenciaClinica, Integer volumePerdaSangue, String achadosOperatorios, String descricaoTecnicaDesc) throws BaseException {
		return getListaCirurgiasDescCirurgicaON().pFinalizaDescricao(crgSeq, dcgSeqp, nomeMicrocomputador, indIntercorrencia, indPerdaSangue, intercorrenciaClinica, volumePerdaSangue, achadosOperatorios, descricaoTecnicaDesc);
	}
	
	@Override
	public void desbloqDocCertificacao(final Integer crgSeq, final DominioTipoDocumento tipoDocumento) throws ApplicationBusinessException, ApplicationBusinessException{
		getListaCirurgiasDescCirurgicaComplON().desbloqDocCertificacao(crgSeq, tipoDocumento);
	}

	protected ListaCirurgiasDescCirurgicaComplON getListaCirurgiasDescCirurgicaComplON() {
		return listaCirurgiasDescCirurgicaComplON;
	}	
	
	@Override
	public List<AghEspecialidades> pGeraDadosEsp() {
		return getMbcDescricaoTecnicaRN().pGeraDadosEsp();
	}

	@Override
	public List<MbcProcedimentoCirurgicos> pGeraDadosProc(Short espSeq) {
		return getMbcDescricaoTecnicaRN().pGeraDadosProc(espSeq);
	}

	@Override
	public List<MbcDescricaoPadrao> buscarDescricaoPadraoPelaEspecialidadeEProcedimento(Short espSeq, Integer procSeq) {
		return getMbcDescricaoTecnicaRN().buscarDescricaoPadraoPelaEspecialidadeEProcedimento(espSeq, procSeq);
	}
	
	@Override
	public void persistirDescricaoTecnica(MbcDescricaoTecnicas descricaoTecnica) throws BaseException {
		getMbcDescricaoTecnicaRN().persistir(descricaoTecnica);
	}

	@Override
	public void excluirDescricaoTecnica(MbcDescricaoTecnicas descricaoTecnica) throws BaseException {
		getMbcDescricaoTecnicaRN().excluir(descricaoTecnica);
	}
	
	@Override
	public void validarTamanhoDescricaoTecnica(String descricaoTecnica) throws ApplicationBusinessException {
		getMbcDescricaoTecnicaON().validarTamanhoDescricaoTecnica(descricaoTecnica);	
	}
	
	protected MbcDescricaoTecnicaON getMbcDescricaoTecnicaON() {
		return mbcDescricaoTecnicaON;
	}

	@Override
	public List<PesquisarPacientesCirurgiaVO> pesquisarPacientesCirurgia(
			AipPacientes paciente) {
		return getMbcCirurgiasON().pesquisarPacientesCirurgia(paciente);
	}
	
	@Override
	public List<MbcProcDescricoes> obterProcDescricoes(Integer dcgCrgSeq, Short dcgSeqp, final String orderBy){
		return getMbcProcDescricoesDAO().obterProcDescricoes(dcgCrgSeq, dcgSeqp, orderBy);
	}
	
	@Override
	public void excluirMbcProcDescricoes(final MbcProcDescricoes procDescricao) throws ApplicationBusinessException, ApplicationBusinessException {
		getMbcProcDescricoesRN().excluirMbcProcDescricoes(procDescricao);
	}
	
	@Override
	public void inserirMbcProcDescricoes(final MbcProcDescricoes procDescricao) throws ApplicationBusinessException, ApplicationBusinessException {
		getMbcProcDescricoesRN().inserirMbcProcDescricoes(procDescricao);
	}
	
	@Override
	public void alterarMbcProcDescricoes(final MbcProcDescricoes procDescricao) throws ApplicationBusinessException, ApplicationBusinessException {
		getMbcProcDescricoesRN().alterarMbcProcDescricoes(procDescricao);
	}
	
	@Override
	public boolean validarTempoMinimoCirurgia(final Date dtiDthrInicioCir, final Date dtiDthrFimCirg, final Short lPciTempoMinimo) {
		return getDescricaoItensON().validarTempoMinimoCirurgia(dtiDthrInicioCir, dtiDthrFimCirg, lPciTempoMinimo);
	}

	@Override
	public MbcProcDescricoes obterMbcProcDescricoesPorChavePrimaria(MbcProcDescricoesId id){
		return getMbcProcDescricoesDAO().obterPorChavePrimaria(id, true, MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO);
	}

	@Override
	public List<MbcProcDescricoes> listarProcDescricoesComProcedimentoAtivo(Integer dcgCrgSeq, Short dcgSeqp){
		return getMbcProcDescricoesDAO().listarProcDescricoesComProcedimentoAtivo(dcgCrgSeq, dcgSeqp);
	}
	
	@Override
	public List<ProcedRealizadoVO> obterProcedimentosPorPaciente(Integer pacCodigo, String strPesquisa, Short videoLaparoscopia,
			Date dtRealizCrgVideo, Date dtRealizCrgOrtese, Date dtRealizCrgSemVideo) {
		return getMbcProcDescricoesDAO().obterProcedimentosPorPaciente(pacCodigo, strPesquisa, videoLaparoscopia,
				dtRealizCrgVideo, dtRealizCrgOrtese, dtRealizCrgSemVideo);
	}
	
	@Override
	public ProcedRealizadoVO obterProcedimentoVOPorId(Integer dcgCrgSeq, Short dcgSeqp, Integer seqp) {
		return getMbcProcDescricoesDAO().obterProcedimentoVOPorId(dcgCrgSeq, dcgSeqp, seqp);
	}
	
	@Override
	public void persistirAgenda(MbcAgendas agenda, RapServidores servidorLogado) throws BaseException {
		getMbcAgendasRN().persistirAgenda(agenda,servidorLogado);
	}
	
	@Override
	public void gravarTrocaLocalEspecilidadeEquipeSala(MbcAgendas agenda, String comeFrom) throws BaseException {
		getMbcAgendasON().gravarTrocaLocalEspecilidadeEquipeSala(agenda, comeFrom);
	}
	
	private MbcAgendasON getMbcAgendasON() {
		return mbcAgendasON;
	}

	@Override
	@BypassInactiveModule
	public String buscaDescricaoMaterialExame(final DominioOrigemPacienteCirurgia origemPaciente, final Integer pacCodigo, final Integer atdSeq){
		return getDescricaoItensON().buscaDescricaoMaterialExame(origemPaciente, pacCodigo, atdSeq);
	}
	
	public boolean habilitaEncaminhamentoExame(final DominioOrigemPacienteCirurgia origemPaciente, final Integer pacCodigo, final Integer vAtdSeq){
		return getDescricaoItensON().habilitaEncaminhamentoExame(origemPaciente, pacCodigo, vAtdSeq);
	}

	public void persistirMbcNotaAdicionais(final MbcNotaAdicionais notaAdicional)
			throws ApplicationBusinessException{
		getMbcNotaAdicionaisRN().persistirMbcNotaAdicionais(notaAdicional);
	}
	
	public void excluirMbcNotaAdicionais(
			final MbcNotaAdicionais mbcNotaAdicionais)
			throws ApplicationBusinessException {
		getMbcNotaAdicionaisRN().excluirMbcNotaAdicionais(mbcNotaAdicionais);
	}
	
	public boolean mbcpImprimeDescrCirurgica(final Boolean assinar) throws BaseException {
		return getListaCirurgiasDescCirurgicaON().mbcpImprimeDescrCirurgica(assinar);
	}
	
	public Integer obterSeqAghAtendimentoPorPaciente(final Integer pacCodigo){
		return getDescricaoItensON().obterSeqAghAtendimentoPorPaciente(pacCodigo);
	}
	
	protected MbcAgendasRN getMbcAgendasRN() {
		return mbcAgendasRN;
	}
	
	protected MbcDescricaoPadraoDAO getMbcDescricaoPadraoDAO() {
		return mbcDescricaoPadraoDAO;
	}

	protected MbcProcDescricoesRN getMbcProcDescricoesRN() {
		return mbcProcDescricoesRN;
	}
	
	protected MbcCirurgiasRN getMbcCirurgiasRN() {
		return mbcCirurgiasRN;
	}
	
	protected MbcFichaAnestesiasRN getMbcFichaAnestesiasRN() {
		return mbcFichaAnestesiasRN;
	}

	@Override
	public List<MbcProfCirurgias> pesquisarMbcProfCirurgiasByCirurgia(Integer crgSeq) {
		return getMbcProfCirurgiasDAO().pesquisarMbcProfCirurgiasByCirurgia(crgSeq);
	}

	@Override
	public MbcCirurgias obterCirurgiaComUnidadePacienteDestinoPorCrgSeq(
			Integer crgSeq) {
		return getMbcCirurgiasDAO().obterCirurgiaComUnidadePacienteDestinoPorCrgSeq(crgSeq);
	}

	protected MbcCirurgiaAnotacaoDAO getMbcCirurgiaAnotacaoDAO() {
		return mbcCirurgiaAnotacaoDAO;
	}
	
	@Override
	public List<MbcCirurgiaAnotacao> obterCirurgiaAnotacaoPorSeqCirurgia(Integer seq) {
		return getMbcCirurgiaAnotacaoDAO().obterCirurgiaAnotacaoPorSeqCirurgia(seq);
	}

	@Override
	public String obterQuarto(Integer pacCodigo) {
		return getEscalaCirurgiasON().pesquisaQuarto(pacCodigo);
	}
	
	@Override
	public String  obterQuarto(Integer pac_codigo, boolean ativarLinhaRelatorio) {
		return getEscalaCirurgiasON().pesquisaQuarto(pac_codigo, ativarLinhaRelatorio);
	}

	protected MbcCirurgiaAnotacaoRN getMbcCirurgiaAnotacaoRN() {
		return mbcCirurgiaAnotacaoRN;
	}
	
	@Secure("#{s:hasPermission('manterAnotacoesAgendamentoEletivo','executar')}")
	@Override
	public void persistirCirurgiaAnotacao(MbcCirurgiaAnotacao anotacao,
			Integer mbcCirurgiaCodigo) throws ApplicationBusinessException {
		getMbcCirurgiaAnotacaoRN().persistirCirurgiaAnotacao(anotacao, mbcCirurgiaCodigo);
	}
	
	@Override
	public List<MbcExtratoCirurgia> listarMbcExtratoCirurgiaPorCirurgia(Integer crgSeq) {
		return getMbcExtratoCirurgiaDAO().listarMbcExtratoCirurgiaPorCirurgia(crgSeq);
	}
	
	protected ExtratoCirurgiaON getExtratoCirurgiaON() {
		return extratoCirurgiaON;
	}
	
	@Override
	public String persistirMbcExtratoCirurgia(MbcExtratoCirurgia extratoCirurgia) throws ApplicationBusinessException {
		return getExtratoCirurgiaON().persistirMbcExtratoCirurgia(extratoCirurgia);
	}
	
	@Override
	public String excluirMbcExtratoCirurgia(MbcExtratoCirurgia extratoCirurgia, String usuarioLogado) throws ApplicationBusinessException {
		return getExtratoCirurgiaON().excluirMbcExtratoCirurgia(extratoCirurgia, usuarioLogado);
	}

	@Override
	public List<MbcDestinoPaciente> pesquisarDestinoPacientePorSeqOuDescricao(
			Object pesquisa, Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Byte param) {
		return getMbcDestinoPacienteDAO().pesquisarDestinoPacientePorSeqOuDescricao(pesquisa, firstResult, maxResult, orderProperty, asc, param);
	}

	@Override
	public Long pesquisarDestinoPacientePorSeqOuDescricaoCount(
			String pesquisa) {
		return getMbcDestinoPacienteDAO().pesquisarDestinoPacientePorSeqOuDescricaoCount(pesquisa);
	}
	
	
	@Override
	public List<MbcDestinoPaciente> pesquisarDestinoPacienteAtivoPorSeqOuDescricao(
			Object objPesquisa, Boolean asc, Byte param, MbcDestinoPaciente.Fields ... fieldsOrder) {
		return getMbcDestinoPacienteDAO().pesquisarDestinoPacienteAtivoPorSeqOuDescricao(objPesquisa, asc, param, fieldsOrder);
	}

	@Override
	public Long pesquisarDestinoPacienteAtivoPorSeqOuDescricaoCount(Object objPesquisa, Byte param) {
		return getMbcDestinoPacienteDAO().pesquisarDestinoPacienteAtivoPorSeqOuDescricaoCount(objPesquisa,param);
	}

	private MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO() {
		return mbcSolicHemoCirgAgendadaDAO;
	}

	@Override
	public MbcProcEspPorCirurgias pesquisarTipagemSanguinea(Integer pCrgSeq) {
		return getMbcSolicHemoCirgAgendadaON().pesquisarTipagemSanguinea(pCrgSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificarCirurgiaPossuiHemocomponentes(Integer crgSeq) {
		return getMbcSolicHemoCirgAgendadaDAO().mbcSolicHemoCirgAgendadaCountPorCrgSeq(crgSeq) > 0;
	}
	
	@Override
	public List<MbcSalaCirurgica> obterSalasCirurgicasAtivasPorUnfSeqNome(Short unfSeq, Short seqp, String nome) {
		return getMbcSalaCirurgicaDAO().obterSalasCirurgicasAtivasPorUnfSeqNome(unfSeq, seqp, nome);
	}
	
	@Override
	public Long obterSalasCirurgicasAtivasPorUnfSeqNomeCount(Short unfSeq, Short seqp, String nome) {
		return getMbcSalaCirurgicaDAO().obterSalasCirurgicasAtivasPorUnfSeqNomeCount(unfSeq, seqp, nome);
	}
	
	@Override
	public List<MbcTipoAnestesias> pequisarTiposAnestesiasAtivas(Short seq, String descricao, Integer maxResults) {
		return getMbcTipoAnestesiasDAO().pequisarTiposAnestesiasAtivas(seq, descricao, maxResults);
	}

	@Override
	public Long pequisarTiposAnestesiasAtivasCount(Short seq, String descricao) {
		return getMbcTipoAnestesiasDAO().pequisarTiposAnestesiasAtivasCount(seq, descricao);
	}

	private MbcSolicHemoCirgAgendadaON getMbcSolicHemoCirgAgendadaON() {
		return mbcSolicHemoCirgAgendadaON;
	}
	
	private DetalhaRegistroCirurgiaON getDetalhaRegistroCirurgiaON() {
		return detalhaRegistroCirurgiaON;
	}
	
	@Override
	public List<PdtViaAereas> obterViasAereasAtivasOrdemDescricao(){
		return getPdtViaAereasDAO().obterPdtViaAereasAtivasOrdemDescricao();
	}

	@Override
	public List<MbcProcEspPorCirurgias> pesquisarProcedimentoCirurgicoEscalaCirurgica(
			MbcCirurgias cirurgia) {
		return getDetalhaRegistroCirurgiaON().pesquisarProcedimentoCirurgicoEscalaCirurgica(cirurgia);
	}

	@Override
	public List<MbcAnestesiaCirurgias> pesquisarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(
			Integer crgSeq, DominioSituacao situacao) {
		return getMbcAnestesiaCirurgiasDAO().pesquisarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(crgSeq, situacao);
	}

	@Override
	public String gravarMbcCirurgias(MbcCirurgias cirurgia) throws ApplicationBusinessException, BaseException{
		return getDetalhaRegistroCirurgiaON().persistirMbcCirurgias(cirurgia);
	}
	
	@Override
	public String gravarAcompanhamentoMbcCirurgias(MbcCirurgias cirurgia, Boolean inserirAtendimento) throws ApplicationBusinessException, BaseException{
		return getDetalhaRegistroCirurgiaON().persistirAcompanhamentoMbcCirurgias(cirurgia, inserirAtendimento);
	}

	@Override
	public Collection<FichaPreOperatoriaVO> listarProcedimentoPorCirurgia(
			Short seqUnidadeCirurgica, String unidadeCirurgica, Short seqUnidadeInternacao, Date dataCirurgia,
			Integer prontuario) throws ApplicationBusinessException {
		return getFichaPreOperatoriaON().listarProcedimentoPorCirurgia(seqUnidadeCirurgica, unidadeCirurgica, seqUnidadeInternacao, dataCirurgia, prontuario);
	}
	
	private FichaPreOperatoriaON getFichaPreOperatoriaON() {
		return fichaPreOperatoriaON;
	}
	
	@Override
	public List<MbcSolicHemoCirgAgendada> pesquisarSolicHemoterapica(Integer pCrgSeq) {
		return getMbcSolicHemoCirgAgendadaDAO().pesquisarComponenteSanguineosEscalaCirurgica(pCrgSeq);
	}

	@Override
	public String excluirMbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendada mbcSolicHemoCirgAgendadaDelecao) throws BaseException {
		return getMbcSolicHemoCirgAgendadaON().excluirMbcSolicHemoCirgAgendada(mbcSolicHemoCirgAgendadaDelecao);
	}

	@Override
	public MbcSolicHemoCirgAgendada obterMbcSolicHemoCirgAgendadaById(Integer crgSeq, String csaCodigo) {
		return getMbcSolicHemoCirgAgendadaDAO().obterMbcSolicHemoCirgAgendadaById(crgSeq,csaCodigo);
	}

	@Override
	public String persistirMbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendada solicHemoCirgAgendada) throws BaseException {
		return getMbcSolicHemoCirgAgendadaON().persistirMbcSolicHemoCirgAgendada(solicHemoCirgAgendada);
	}
	
	@Override
	public void exluirMbcAnestesiaDescricoes(MbcAnestesiaDescricoes anestesiaDescricao)  throws ApplicationBusinessException, ApplicationBusinessException{
		this.getAnestesiaDescricoesRN().excluirMbcAnestesiaDescricoes(anestesiaDescricao);
	}
	
	@Override
	public List<HistoricoAgendaVO> buscarAgendaHistoricos(Integer agdSeq) {
		return getMbcAgendaHistoricoON().buscarHistoricoAgenda(agdSeq);
	}
	
	public MbcAgendaHistoricoON getMbcAgendaHistoricoON() {
		return mbcAgendaHistoricoON;		
	}
	
	@Override
	public AlertaModalVO agendarProcedimentosParte1(boolean emEdicao, CirurgiaTelaVO vo,
			String nomeMicrocomputador) throws BaseException {
		return this.getAgendaProcedimentosON().agendarProcedimentosParte1(emEdicao, vo, nomeMicrocomputador);
	}
	
	@Override
	public List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionaisAgendaENotaProcedimento(String strPesquisa, Short unfSeq, List<String> listConselhoMedico, boolean agenda) {
		return getAgendaProcedimentosProfissionaisON().pesquisarProfissionaisAgendaENotaConsumoProcedimento(strPesquisa, unfSeq, listConselhoMedico, agenda);
	}
	
	@Override
	public Long pesquisarProfissionaisAgendaENotaProcedimentoCount(String strPesquisa, Short unfSeq, List<String> listConselhoMedico, boolean agenda) {
		return getAgendaProcedimentosProfissionaisON()
				.pesquisarProfissionaisAgendaENotaConsumoProcedimentoCount(strPesquisa, 
						unfSeq, listConselhoMedico, agenda);
	}
	
	@Override
	@Secure("#{s:hasPermission('agendarProcedimentoEletUrgEmergencia','executar')}")
	public void agendarProcedimentosParte2(boolean emEdicao, CirurgiaTelaVO vo, AlertaModalVO alertaVO, String nomeMicrocomputador) throws BaseException {
		this.getAgendaProcedimentosON().agendarProcedimentosParte2(emEdicao, vo, alertaVO, nomeMicrocomputador);
	}
	
	@Override
	public Date converterTempoPrevistoHoras(Short tempoPrevistoHoras, Byte tempoPrevistoMinutos) {
		return this.getAgendaProcedimentosFuncoesON().converterTempoPrevistoHoras(tempoPrevistoHoras, tempoPrevistoMinutos);
	}
	
	@Override
	public List<VMbcProcEsp> pesquisarProcedimentosEspecialidadeProjeto(final String filtro, final Short espSeq, final Integer pjqSeq) {
		return this.getAgendaProcedimentosON().pesquisarProcedimentosEspecialidadeProjeto(filtro, espSeq, pjqSeq);

	}
	
	public VMbcProcEsp obterProcedimentosAgendadosPorId(Short espSeq, Integer pciSeq, Short seqp) {
		return this.getVMbcProcEspDAO().obterProcedimentosAgendadosPorId(espSeq, pciSeq, seqp);
	}

	protected VMbcProcEspDAO getVMbcProcEspDAO() {
		return vMbcProcEspDAO;
	}
	
	@Override
	public Long pesquisarProcedimentosEspecialidadeProjetoCount(final String filtro, final Short espSeq, final Integer pjqSeq) {
		return this.getAgendaProcedimentosON().pesquisarProcedimentosEspecialidadeProjetoCount(filtro, espSeq, pjqSeq);
	}
	
	@Override
	public List<CirurgiaTelaProcedimentoVO> pesquisarProcedimentosAgendaProcedimentos(Integer crgSeq, DominioIndRespProc indRespProc) {
		return this.getAgendaProcedimentosON().pesquisarProcedimentosAgendaProcedimentos(crgSeq, indRespProc);
	}
	
	@Override
	public List<CirurgiaTelaProfissionalVO> pesquisarProfissionaisAgendaProcedimentos(Integer crgSeq) {
		return this.getAgendaProcedimentosON().pesquisarProfissionaisAgendaProcedimentos(crgSeq);
	}
	
	@Override
	public List<CirurgiaTelaAnestesiaVO> pesquisarAnestesiasAgendaProcedimentos(Integer crgSeq) {
		return this.getAgendaProcedimentosON().pesquisarAnestesiasAgendaProcedimentos(crgSeq);
	}

	@Override
	public Boolean obterTipagemSanguinea(Integer pCrgSeq) {
		return this.getMbcProcEspPorCirurgiasDAO().obterTipagemSanguineaSolicitada(pCrgSeq);
		
	}	
	
	@Override
	public VisualizaCirurgiaCanceladaVO buscarCirurgiaCancelada(Integer agdSeq) throws ApplicationBusinessException {
		return this.getVisualizaCirurgiaCanceladaON().buscarCirurgiaCancelada(agdSeq);
	}
	
	private VisualizaCirurgiaCanceladaON getVisualizaCirurgiaCanceladaON() {
		return visualizaCirurgiaCanceladaON;
	}
	
	private MbcSolicitacaoEspExecCirgON getMbcSolicitacaoEspExecCirgON() {
		return mbcSolicitacaoEspExecCirgON;
	}
	
	private MbcSolicitacaoEspExecCirgRN getMbcSolicitacaoEspExecCirgRN() {
		return mbcSolicitacaoEspExecCirgRN;
	}
	
	@Override
	public List<MbcSolicitacaoEspExecCirg> pesquisarSolicitacaoEspecialidadePorCrgSeq(
			Integer crgSeq) {
		return this.mbcSolicitacaoEspExecCirgDAO.pesquisarSolicitacaoEspecialidadePorCrgSeq(crgSeq);
	}

	@Override
	public MbcSolicitacaoEspExecCirg obterMbcSolicitacaoEspExecCirgPorChavePrimaria(
			MbcSolicitacaoEspExecCirgId id) {
		return this.mbcSolicitacaoEspExecCirgDAO.obterPorChavePrimaria(id);
	}

	@Override
	public MbcSolicitacaoEspExecCirg obterMbcSolicitacaoEspExecCirgPorChavePrimaria(
			MbcSolicitacaoEspExecCirgId id, Enum[] inner, Enum[] left) {
		return this.mbcSolicitacaoEspExecCirgDAO.obterPorChavePrimaria(id, inner, left);
	}
	
	@Override
	public MbcSolicitacaoEspExecCirg pesquisarMbcSolicitacaoEspNessidadeCirurgicaEUnidadeFuncional(MbcSolicitacaoEspExecCirgId id) {
		return this.mbcSolicitacaoEspExecCirgDAO.pesquisarMbcSolicitacaoEspNessidadeCirurgicaEUnidadeFuncional(id);
	}

	
	@Override
	public void verificarSolicEspExistente(Integer crgSeq, Short nciSeq )throws ApplicationBusinessException, ApplicationBusinessException {
		this.getMbcSolicitacaoEspExecCirgON().verificarSolicEspExistente(crgSeq, nciSeq);
	}

	@Secure("#{s:hasPermission('manterSolicitaoEspecial','executar')}")
	@Override
	public void persistirSolicitacaoEspecial(MbcSolicitacaoEspExecCirg solicitacaoEspecial) throws BaseException {
		this.getMbcSolicitacaoEspExecCirgRN().persistirSolicitacaoEspecial(solicitacaoEspecial);
	}
	
	@Override
	public void removerMbcSolicitacaoEspExecCirg(MbcSolicitacaoEspExecCirg solicitacaoEspecial) throws BaseException {
		this.getMbcSolicitacaoEspExecCirgRN().excluirSolicitacaoEspecial(solicitacaoEspecial.getId().getCrgSeq(), 
				solicitacaoEspecial.getId().getNciSeq(), solicitacaoEspecial.getId().getSeqp());
	}

	@Secure("#{s:hasPermission('manterSolicitaoEspecial','executar')}")
	@Override
	public void atualizarSolicitacaoEspecial(MbcSolicitacaoEspExecCirg solicitacaoEspecial) throws BaseException {
		this.getMbcSolicitacaoEspExecCirgRN().atualizarSolicitacaoEspecial(solicitacaoEspecial);
	}

	@Override
	public void excluirSolicitacaoEspecial(Integer crgSeqExc, Short nciSeqExc, Short seqpExc) throws BaseException {
		this.getMbcSolicitacaoEspExecCirgRN().excluirSolicitacaoEspecial(crgSeqExc, nciSeqExc, seqpExc);
	}


	@Override
	public void refreshListMbcSolicHemoCirgAgendada(
			List<MbcSolicHemoCirgAgendada> listaSolicHemoterapicos) {
		getMbcSolicHemoCirgAgendadaON().refreshListMbcSolicHemoCirgAgendada(listaSolicHemoterapicos);
	}

	@Override
	public boolean containsMbcSolicHemoCirgAgendada(
			MbcSolicHemoCirgAgendada item) {
		return getMbcSolicHemoCirgAgendadaDAO().contains(item);
	}

	@Override
	public void refreshMbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendada item) {
		getMbcSolicHemoCirgAgendadaDAO().refresh(item);
	}

	@Override
	public List<MbcMatOrteseProtCirg> pesquisarOrteseProtesePorCirurgia(
			Integer pCrgSeq) {
		return this.getMbcMatOrteseProtCirgDAO().pesquisarOrteseProteseEscalaCirurgicaPorCirurgia(pCrgSeq);
	}
	
	protected MbcMatOrteseProtCirgDAO getMbcMatOrteseProtCirgDAO() {
		return mbcMatOrteseProtCirgDAO;
	}

	@Override
	public void persistirMatOrteseProtese(
			MbcMatOrteseProtCirg materialOrteseProtese) throws BaseException {
		this.mbcMatOrteseProtCirgRN.persistirMatOrteseProtese(materialOrteseProtese);
	}
	
	@Override
	public void removerMatOrteseProtese(
			MbcMatOrteseProtCirg materialOrteseProtese) throws BaseException {
		this.mbcMatOrteseProtCirgRN.excluirMatOrteseProtese(materialOrteseProtese);
	}

	@Override
	public void excluirMatOrteseProtese(
			MbcMatOrteseProtCirg materialOrteseProtese) throws BaseException {
		this.mbcMatOrteseProtCirgRN.excluirMatOrteseProtese(materialOrteseProtese);
		
	}	

	@Override
	public MbcMatOrteseProtCirg obterMbcMatOrteseProtCirgPorChavePrimaria(
			MbcMatOrteseProtCirgId id) {
		return getMbcMatOrteseProtCirgDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public String mbcImpressao(Integer crgSeq) throws ApplicationBusinessException {
		return this.getListarCirurgiasON().mbcImpressao(crgSeq);
	}
	
	@Override
	public List<MbcDescricaoCirurgica> listarMbcDescricaoCirurgica(Integer crgSeq, Short seqp){
		return this.getMbcDescricaoCirurgicaDAO().listarMbcDescricaoCirurgica(crgSeq, seqp);
	}	
	
	
	
	@Override
	public MbcSolicitacaoCirurgiaPosEscala obterSolicitacaoCirurgiaPosEscalPorChavePrimaria(Integer speSeq) {
		return this.getMbcSolicitacaoCirurgiaPosEscalaDAO().obterPorChavePrimaria(speSeq);
	}

	protected MbcSolicitacaoCirurgiaPosEscalaDAO getMbcSolicitacaoCirurgiaPosEscalaDAO() {
		return mbcSolicitacaoCirurgiaPosEscalaDAO;
	}
	@Override
	public Long obterNumeroCirurgiasAgendadasPorPacienteDia(Integer codigoPaciente, Date dataCirurgia) {
		return getMbcCirurgiasDAO().obterNumeroCirurgiasAgendadasPorPacienteDia(codigoPaciente, dataCirurgia);
	}
	
	@Override
	public void persistirMbcAgendaJustificativa(MbcAgendaJustificativa agendaJustificativa) throws BaseException {
		getMbcAgendasJustificativaRN().persistir(agendaJustificativa);
	}
	
	private MbcAgendasJustificativaRN getMbcAgendasJustificativaRN() {
		return mbcAgendasJustificativaRN;
	}
	
	//============
	//== #24943 ==
	//============ 
	
	private MbcEquipamentoUtilCirgRN getMbcEquipamentoUtilCirgRN(){
		return mbcEquipamentoUtilCirgRN;
	}

	@Override
	public List<MbcEquipamentoUtilCirg> pesquisarMbcEquipamentoUtilCirgPorCirurgia(Integer crgSeq) throws ApplicationBusinessException {
		return this.getMbcEquipamentoUtilCirgRN().pesquisarRegraWhenNewBlockInstance(crgSeq);
	}
	
	@Override
	public MbcEquipamentoUtilCirg getMbcEquipamentosUtilCirgsPorMbcEquipamentoCirurgico(MbcEquipamentoCirurgico mbcEquipamentoCirurgicoSelecionadoNaSuggestion, Short quantidade) throws ApplicationBusinessException{
		return this.getMbcEquipamentoCirurgicoON().getMbcEquipamentosUtilCirgsPorMbcEquipamentoCirurgico(mbcEquipamentoCirurgicoSelecionadoNaSuggestion, quantidade);
	}
	
	public MbcEquipamentoCirurgicoON2 getMbcEquipamentoCirurgicoON(){
		return mbcEquipamentoCirurgicoON;
	}

	@Override
	public void persistirListaMbcEquipamentoUtilCirg(List<MbcEquipamentoUtilCirg> listaMbcEquipamentoUtilCirgs, MbcCirurgias cirurgia) throws ApplicationBusinessException {
		this.getMbcEquipamentoUtilCirgRN().persistirListaMbcEquipamentoUtilCirg(listaMbcEquipamentoUtilCirgs, cirurgia);
	}

	@Override
	public void excluirListaMbcEquipamentoUtilCirgPorMbcCirurgiaEEquipamentoCirurgico(Integer crgSeq, Short euuSeq) throws ApplicationBusinessException {
		this.getMbcEquipamentoUtilCirgRN().excluirListaMbcEquipamentoUtilCirgPorMbcCirurgiaEEquipamentoCirurgico(crgSeq, euuSeq);
	}
	
	@Override
	public String obterLeitoComoString(Integer codigo){
		return this.getEscalaCirurgiasON().pesquisaQuarto(codigo);
	}
	
	public MbcEquipamentoUtilCirgON getMbcEquipamentoUtilCirgON(){
		return mbcEquipamentoUtilCirgON;
	}
	
	@Override
	public Boolean atualizarQuantidadeSeExistirMbcEquipamentoCirurgicoNaListaMbcEquipamentoutilCirg(
			MbcEquipamentoCirurgico mbcEquipamentoCirurgicoSelecionadoNaSuggestion,
			List<MbcEquipamentoUtilCirg> listaMbcEquipamentoUtilCirg, Short quantidade) throws ApplicationBusinessException{
		
		return this.getMbcEquipamentoUtilCirgON().atualizarQuantidadeSeExistirMbcEquipamentoCirurgicoNaListaMbcEquipamentoutilCirg(
				mbcEquipamentoCirurgicoSelecionadoNaSuggestion,
				listaMbcEquipamentoUtilCirg, quantidade);
	}
	
	@Override
	public List<ProcedimentoCirurgicoPacienteVO> buscarCirurgiasDoPacienteDuranteUmaInternacao(Integer atdSeq, Date dtInicioProcessamento, Date dtFimProcessamento){
		return this.getMbcProcEspPorCirurgiasDAO().buscarCirurgiasDoPacienteDuranteUmaInternacao(atdSeq, dtInicioProcessamento, dtFimProcessamento);
	}
	

	@Override
	public List<MbcCirurgias> pesquisarCirurgiasAgendaDataCentroCusto(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short agenda, Date data, Integer centroCusto) {
		return getMbcCirurgiasDAO().pesquisarCirurgiasAgendaDataCentroCusto(firstResult, maxResult, orderProperty, asc, 
				agenda, data, centroCusto);
	}

	@Override
	public Long pesquisarCirurgiasAgendaDataCentroCustoCount(Short agenda,
			Date data, Integer centroCusto) {
		return getMbcCirurgiasDAO().pesquisarCirurgiasAgendaDataCentroCustoCount(agenda, data, centroCusto);
	}
	
	public MbcMaterialPorCirurgiaON getMbcMaterialPorCirurgiaON(){
		return mbcMaterialPorCirurgiaON;
	}

	@Override
	public List<MaterialPorCirurgiaVO> pesquisarMateriaisPorCirurgia(
			MbcCirurgias cirurgia, AghParametros pGrMatOrtProt, AghParametros pDispensario)
			throws ApplicationBusinessException {
		return this.getMbcMaterialPorCirurgiaON().pesquisarRegraWhenNewBlockInstance(cirurgia, pGrMatOrtProt, pDispensario);
	}
	
	@Override
	public String obterMensagemMaterialCadastrado() {
		return this.getMbcMaterialPorCirurgiaON().obterMensagemMaterialCadastrado();
	}
	
	public MbcMaterialPorCirurgiaRN getMbcMaterialPorCirurgiaRN(){
		return mbcMaterialPorCirurgiaRN;
	}

	@Secure("#{s:hasPermission('materialConsumidoCirurgia','executar')}")
	@Override
	public void persistirMaterialPorCirurgia(List<MaterialPorCirurgiaVO> materiais, AghParametros pDispensario) throws ApplicationBusinessException {
		this.getMbcMaterialPorCirurgiaRN().persistirMaterialPorCirurgia(materiais, pDispensario);
		
	}
	
	@Secure("#{s:hasPermission('materialConsumidoCirurgia','executar')}")
	@Override
	public void validarMaterialNovo(MbcMaterialPorCirurgia materialPorCirurgia, List<MbcMaterialPorCirurgia> listaMateriais) throws ApplicationBusinessException {
		this.getMbcMaterialPorCirurgiaRN().validarMaterialNovo(materialPorCirurgia, listaMateriais);
	}

	@Override
	public List<MbcCirurgias> pesquisarCirurgiasRealizadasNotaConsumo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcCirurgias elemento) {
		return this.getMbcCirurgiasDAO().pesquisarCirurgiasRealizadasNotaConsumo(firstResult, maxResult, orderProperty, asc, elemento);
	}
	
	@Override
	public Long pesquisarCirurgiasRealizadasNotaConsumoCount(MbcCirurgias elemento) {
		return this.getMbcCirurgiasDAO().pesquisarCirurgiasRealizadasNotaConsumoCount(elemento);
	}

	@Override
	public Short obterMenorSeqpDescricaoCirurgicaPorCrgSeq(Integer crgSeq) {
		return getMbcDescricaoCirurgicaDAO().obterMenorSeqpDescricaoCirurgicaPorCrgSeq(crgSeq);
	}

	@Override
	public String geraCSVRelatCirurRealizPorEspecEProf(
			final Short unidadeFuncional,
			final Date dataInicial, final Date dataFinal,
			final Short especialidade) throws IOException, ApplicationBusinessException {
		
		return getRelatCirurRealizPorEspecEProfON().geraCSVRelatCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, especialidade);
	}

	@Override
	public List<RelatorioCirurgiasReservaHemoterapicaVO> recuperarRelatorioCirurgiasReservaHemoterapicaVO(Short unfSeq, Date dataCirurgia, Boolean cirgComSolicitacao) throws ApplicationBusinessException {
		return getRelatorioCirurgiasReservaHemoterapicaON().recuperarRelatorioCirurgiasReservaHemoterapicaVO(unfSeq, dataCirurgia, cirgComSolicitacao);
	}
	
	@Override
	public List<RelatorioCirurgiasIndicacaoExamesVO> recuperarRelatorioCirurgiasIndicacaoExames(Short seqUnidCirurgia, Short seqUnidExames, Date dataCirurgia, Boolean cirgComSolicitacao) throws ApplicationBusinessException {
		return getRelatorioCirurgiasIndicacaoExamesON().recuperarRelatorioCirurgiasIndicacaoExames(seqUnidCirurgia, seqUnidExames, dataCirurgia,cirgComSolicitacao);
	}
	
	public RelatorioCirurgiasReservaHemoterapicaON getRelatorioCirurgiasReservaHemoterapicaON(){
		return relatorioCirurgiasReservaHemoterapicaON;
	}
	
	public RelatorioCirurgiasIndicacaoExamesON getRelatorioCirurgiasIndicacaoExamesON(){
		return relatorioCirurgiasIndicacaoExamesON;
	}

	/*@Override
	public void downloadedCSV(final String fileName, final String name, final String contentType)
			throws IOException {
		getMbcCirurgiasON().downloadedCSV(fileName, name, contentType);
		
	}*/
	
	@Override
	public String cfESCALAFormula(Short unfSeq, Date dataCirurgia) {		
		return getRelatorioPacientesComCirurgiaPorUnidadeON().cfESCALAFormula(unfSeq, dataCirurgia);
	}
	
	@Override
	public List<RelatorioCirurgiasListaEsperaVO> obterListaEsperaPorUnidadeFuncionalEquipeEspecialidade(
			MbcProfAtuaUnidCirgs equipe, Short espSeq, Short unfSeq,
			Integer pacCodigo) {
		return getRelatorioCirurgiasListaEsperaON().obterListaEsperaPorUnidadeFuncionalEquipeEspecialidade(equipe, espSeq, unfSeq, pacCodigo);
	}
	
	@Secure("#{s:hasPermission('materialConsumidoCirurgia','executar')}")
	@Override
	public void excluirMaterialPorCirurgia(
			MaterialPorCirurgiaVO materialPorCirurgia) throws ApplicationBusinessException {
		this.getMbcMaterialPorCirurgiaRN().excluirMaterialPorCirurgia(materialPorCirurgia);
		
	}
	
	@Override
	@BypassInactiveModule
	public MbcControleEscalaCirurgica obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(
			Short unfSeq, Date dataAgenda, DominioTipoEscala tipo) {
		return getMbcControleEscalaCirurgicaDAO().obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(unfSeq, dataAgenda, tipo);
	}
	
	@Override
	@BypassInactiveModule
	public boolean verificaExistenciaPeviaDefinitivaPorUNFData(Short unf_seq, Date dataPesquisa,DominioTipoEscala tipo ){
		return getMbcControleEscalaCirurgicaDAO().verificaExistenciaPeviaDefinitivaPorUNFData(unf_seq, dataPesquisa, tipo);
	}
	
	public MbcControleEscalaCirurgica obterMbcControleEscalaCirurgicaPorUnfSeqDataEscala(Short unfSeq, Date dataEscala) {
		return getMbcControleEscalaCirurgicaDAO().obterMbcControleEscalaCirurgicaPorUnfSeqDataEscala(unfSeq, dataEscala);
	}
	
	private MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}
	
	private RelatorioCirurgiasListaEsperaON getRelatorioCirurgiasListaEsperaON() {
		return relatorioCirurgiasListaEsperaON;
	}

	@Override
	public List<MbcDestinoPaciente> pesquisarDestinoPacienteAtivoPorSeqOuDescricao(
			Object objPesquisa, boolean b, Byte param, Fields descricao, Fields seq) {
		return getMbcDestinoPacienteDAO().pesquisarDestinoPacienteAtivoPorSeqOuDescricao(objPesquisa, b, param, descricao, seq);
	}

	private RelatCirurRealizPorEspecEProfON getRelatCirurRealizPorEspecEProfON() {
		return relatCirurRealizPorEspecEProfON;
	}
	@Override
	public Collection<MbcRelatCirurRealizPorEspecEProfVO> obterCirurRealizPorEspecEProf(
			Short unidadeFuncional, Date dataInicial, Date dataFinal,
			Short especialidade) throws ApplicationBusinessException {
		
		return getRelatCirurRealizPorEspecEProfON().procDadosReportCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, especialidade);
		
	}
	
	@Override
	public void popularProcedimentoHospitalarInterno(CirurgiaTelaProcedimentoVO procedimentoVO, MbcCirurgias cirurgia) throws BaseException {
		this.getPopulaProcedimentoHospitalarInternoRN().popularProcedimentoHospitalarInterno(procedimentoVO, cirurgia);
	}
	
	@Override
	public void validarModalTempoUtilizacaoO2Ozot(CirurgiaTelaVO vo, AlertaModalVO alertaVO, boolean isPressionouBotaoSimModal) throws BaseException {
		this.getRegistroCirurgiaRealizadaON().validarModalTempoUtilizacaoO2Ozot(vo, alertaVO, isPressionouBotaoSimModal);
	}

	protected PopulaProcedimentoHospitalarInternoRN getPopulaProcedimentoHospitalarInternoRN() {
		return populaProcedimentoHospitalarInternoRN;
	}
	
	private RegistroCirurgiaRealizadaON getRegistroCirurgiaRealizadaON() {
		return registroCirurgiaRealizadaON;
	}

	@Override
	public void validarSeExisteCirurgiatransOperatorio(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		this.getAcompanhamentoCirurgiaON().validarSeExisteCirurgiatransOperatorio(cirurgia);
	}
	
	private AcompanhamentoCirurgiaON getAcompanhamentoCirurgiaON() {
		return acompanhamentoCirurgiaON;
	}
		
	@Override
	public String validarProcedimentoFaturado(CirurgiaTelaVO vo) {
		return this.getRegistroCirurgiaRealizadaON().validarProcedimentoFaturado(vo);
	}

	@Override
	public String validarFaturamentoPacienteTransplantado(CirurgiaTelaVO vo) {
		return this.getRegistroCirurgiaRealizadaON().validarFaturamentoPacienteTransplantado(vo);
	}

	@Override
	public List<String> validarProntuario(CirurgiaTelaVO vo, Integer pacCodigoFonetica) {
		return this.getRegistroCirurgiaRealizadaON().validarProntuario(vo, pacCodigoFonetica);
	}

	@Override
	@Secure("#{s:hasPermission('registroCirurgiaRealizada','executar')}")
	public AlertaModalVO registrarCirurgiaRealizadaNotaConsumo(boolean emEdicao, final boolean confirmaDigitacaoNS, CirurgiaTelaVO vo, String nomeMicrocomputador) throws BaseException {
		return this.getRegistroCirurgiaRealizadaON().registrarCirurgiaRealizadaNotaConsumo(emEdicao, confirmaDigitacaoNS, vo, nomeMicrocomputador);
	}
	
	@Override
	public void popularCodigoSsm(List<CirurgiaTelaProcedimentoVO> listaProcedimentos, MbcCirurgias cirurgia) throws BaseException {
		this.getRegistroCirurgiaRealizadaON().popularCodigoSsm(listaProcedimentos, cirurgia);
	}

	@Override
	public List<CirurgiaCodigoProcedimentoSusVO> listarCodigoProcedimentos(CirurgiaTelaProcedimentoVO procedimentoVO, 
			DominioOrigemPacienteCirurgia origem) throws ApplicationBusinessException {
		return this.getRegistroCirurgiaRealizadaON().listarCodigoProcedimentos(procedimentoVO, origem);
	}
	
	@Override
	public List<RelatorioCirurgiaComRetornoVO> listarCirurgiasComRetorno(Short unfSeq, Short codigoConvenio, Integer seqProcedimento, Date dataInicio, Date dataFim) throws BaseException {
		return getRelatorioCirurgiasComRetornoON().listarCirurgiasComRetorno(unfSeq, codigoConvenio, seqProcedimento, dataInicio, dataFim);
	}
	
	@Override
	public Long quantidadeCirurgiasSemRetorno(Short unfSeq, Short codigoConvenio, Integer seqProcedimento, Date dataInicio, Date dataFim) {
		return getMbcCirurgiasDAO().quantidadeCirurgiasSemRetorno(unfSeq, codigoConvenio, seqProcedimento, dataInicio, dataFim);
	}

	@Override
	public String gerarRelatorioCirurgiasComRetornoCSV(Short unfSeq, Short codigoConvenio, Integer seqProcedimento, Date dataInicio, Date dataFim) throws BaseException, IOException{
		return getRelatorioCirurgiasComRetornoON().gerarRelatorioCirurgiasComRetornoCSV(unfSeq, codigoConvenio, seqProcedimento, dataInicio, dataFim);
	}

	/*@Override
	public void downloadCSVRelatorioCirurgiasComRetorno(final String fileName, final String name) throws IOException{
		getRelatorioCirurgiasComRetornoON().downloadCSVRelatorioCirurgiasComRetorno(fileName, name);
	}*/
	
	@Override
	public NomeArquivoRelatorioCrgVO gerarRelatorioCirurgiaProcedProfissionalCSV(Integer codigoPessoaFisica, Date dthrInicio, Date dthrFim, String extensaoArquivo) throws IOException, ApplicationBusinessException {
		return getRelatorioCirurgiaProcedProfissionalON().gerarRelatorioCirurgiaProcedProfissionalCSV(codigoPessoaFisica, dthrInicio, dthrFim, extensaoArquivo);
	}

	@Override
	public void validarIntervaloDatasPesquisa(Date dataInicio, Date dataFim) throws BaseException {
		getRelatorioCirurgiasComRetornoON().validarIntervaloDatasPesquisa(dataInicio, dataFim);
	}
	
	@Override
	public NomeArquivoRelatorioCrgVO gerarRelatoriosTMOEOutrosTransplantesZip(Date dtInicio,
			Date dtFim, RapPessoasFisicas pessoasFisica, String extensaoArquivoRelatorio, String extensaoArquivoDownload) throws IOException, ApplicationBusinessException {
		return getRelatorioTransplantesRealizTMOOutrosON().gerarRelatoriosTMOEOutrosTransplantesZip(dtInicio, dtFim, pessoasFisica, extensaoArquivoRelatorio, extensaoArquivoDownload);
	}
	
	protected RelatorioCirurgiasComRetornoON getRelatorioCirurgiasComRetornoON() {
		return relatorioCirurgiasComRetornoON;
	}

	protected RelatorioCirurgiaProcedProfissionalON getRelatorioCirurgiaProcedProfissionalON() {
		return relatorioCirurgiaProcedProfissionalON;
	}
	
	protected RelatorioTransplantesRealizTMOOutrosON getRelatorioTransplantesRealizTMOOutrosON() {
		return relatorioTransplantesRealizTMOOutrosON;
	}

	protected MonitorCirurgiaON getMonitorPacienteCentroCirurgicoON() {
		return monitorCirurgiaON;
	}

	@Override
	public List<MonitorCirurgiaSalaPreparoVO> pesquisarMonitorCirurgiaSalaPreparo(Short unfSeq) {
		return getMonitorPacienteCentroCirurgicoON().pesquisarMonitorCirurgiaSalaPreparo(unfSeq);
	}

	@Override
	public List<MonitorCirurgiaSalaCirurgiaVO> pesquisarMonitorPacientesSalaCirurgia(Short unfSeq) {
		return getMonitorPacienteCentroCirurgicoON().pesquisarMonitorPacientesSalaCirurgia(unfSeq);
	}

	@Override
	public List<MonitorCirurgiaSalaRecuperacaoVO> pesquisarMonitorCirurgiaSalaRecuperacao(Short unfSeq) {
		return getMonitorPacienteCentroCirurgicoON().pesquisarMonitorCirurgiaSalaRecuperacao(unfSeq);
	}

	@Override
	public List<MonitorCirurgiaConcluidaHojeVO> pesquisarMonitorCirurgiaConcluidaHoje(Short unfSeq) throws BaseException {
		return getMonitorPacienteCentroCirurgicoON().pesquisarMonitorCirurgiaConcluidaHoje(unfSeq);
	}

	@Override
	public <T extends MonitorCirurgiaVO> List<List<T>> obterListaResultadoPaginado(List<T> resultadoConsulta) throws BaseException {
		return getMonitorPacienteCentroCirurgicoON().obterListaResultadoPaginado(resultadoConsulta);
	}

	@Override
	public List<RelatorioControleChamadaPacienteVO> recuperarRelatorioControleChamadaPacienteVO(Short unfSeq, Date dataCirurgia) throws ApplicationBusinessException {
		return getRelatorioControleChamadaPacienteON().recuperarRelatorioControleChamadaPacienteVO(unfSeq, dataCirurgia);
	}

	@Override
	public void validarIntervaldoEntreDatas(final Date dataInicial, final Date dataFinal)
			throws ApplicationBusinessException {
		getRelatCirurRealizPorEspecEProfON().validarIntervaldoEntreDatas(dataInicial, dataFinal);
	}	
	
	public RelatorioControleChamadaPacienteON getRelatorioControleChamadaPacienteON(){
		return relatorioControleChamadaPacienteON;
	}

	private RelatorioCirExpoRadIonON getRelatorioCirExpoRadIonON() {
		return relatorioCirExpoRadIonON;
	}
	
	@Override
	public String geraRelCSVCirurgiasExposicaoRadiacaoIonizante(
			final Date dataInicial, final Date dataFinal, final List<Short> unidadesFuncionais,
			final List<Short> equipamentos) throws ApplicationBusinessException, IOException {
		return getRelatorioCirExpoRadIonON().geraRelCSVCirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos);
	}

	@Override
	public void validarProfissional(final RapPessoasFisicas rapPessoasFisicas)throws ApplicationBusinessException {
		getRelatorioCirExpoRadIonON().validarProfissional(rapPessoasFisicas);
	}

	private RelatorioDiagnosticosPrePosOperatoriosON getRelatorioDiagnosticosPrePosOperatoriosON(){
		return relatorioDiagnosticosPrePosOperatoriosON;
	}
	
	@Override
	public String geraRelCSVDiagnosticosPrePosOperatorio(final Date dataInicial,
			final Date dataFinal) throws IOException, ApplicationBusinessException {
		return getRelatorioDiagnosticosPrePosOperatoriosON().geraRelCSVDiagnosticosPrePosOperatorio(dataInicial, dataFinal);

	}
	
	@Override
	@BypassInactiveModule
	public List<PortalPlanejamentoTurnosSalaVO> buscarTurnosHorariosDisponiveis(MbcAgendas agenda) {
		return getMbcAgendasHorarioPrevisaoON().buscarTurnosHorariosDisponiveis(agenda);
	}
	
	private MbcAgendasHorarioPrevisaoON getMbcAgendasHorarioPrevisaoON() {
		return mbcAgendasHorarioPrevisaoON;
	}
	
	@Override
	public List<RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO> listarProcedimentosAnestesicosRealizadosPorUnidade(
			Date dataInicial, Date dataFinal, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais) throws ApplicationBusinessException {
		
		return getMbcAnestesiaCirurgiasDAO().listarTipoAnestesiasCirurgiasNaoCanceladasPorPeriodo(dataInicial, dataFinal, constanteAghCaractUnidFuncionais);
	}
	
	@Override
	public List<RelatorioProcedAgendPorUnidCirurgicaVO> pesquisarCirurgiaAgendadaProcedPrincipalAtivoPorUnfSeqPciSeqDtInicioDtFim(
			Short unfSeq, Integer pciSeq, Date dtInicio, Date dtFim) {
		
		return getRelatorioProcedAgendPorUnidCirurgicaON()
				.pesquisarCirurgiaAgendadaProcedPrincipalAtivoPorUnfSeqPciSeqDtInicioDtFim(
						unfSeq, pciSeq, dtInicio, dtFim);
	}
	
	@Override
	public NomeArquivoRelatorioCrgVO gerarRelatorioProcedAgendPorUnidCirurgicaCSV(Short unfSeq, Integer pciSeq,
			Date dtInicio, Date dtFim, String extensaoArquivo) throws IOException {
		return getRelatorioProcedAgendPorUnidCirurgicaON().gerarRelatorioProcedAgendPorUnidCirurgicaCSV(unfSeq, pciSeq, dtInicio, dtFim, extensaoArquivo);
	}
	
	@Override
	public void validarIntervaloEntreDatasRelatorioProcedAgendPorUnidCirurgica(
			final Date dataInicio, final Date dataFim) throws ApplicationBusinessException {
		getRelatorioProcedAgendPorUnidCirurgicaON().validarIntervaloEntreDatasRelatorioProcedAgendPorUnidCirurgica(dataInicio, dataFim);
	}
	
	protected RelatorioProcedAgendPorUnidCirurgicaON getRelatorioProcedAgendPorUnidCirurgicaON() {
		return relatorioProcedAgendPorUnidCirurgicaON;
	}
	
	@Override
	public List<RelatorioCirurgiasPendenteRetornoVO> pesquisarCirurgiasPendenteRetorno(
			DominioTipoPendenciaCirurgia tipoPendenciaCirurgia, Short unfSeq, 
			Date dtInicio, Date dtFim, Integer pciSeq) throws ApplicationBusinessException {
		
		return getRelatorioCirurgiasPendenteRetornoON().pesquisarCirurgiasPendenteRetorno(tipoPendenciaCirurgia, 
				unfSeq, dtInicio, dtFim, pciSeq);
	}
	
	@Override
	public NomeArquivoRelatorioCrgVO gerarRelatorioCirurgiasPendenteRetornoCSV(
			DominioTipoPendenciaCirurgia tipoPendenciaCirurgia, Short unfSeq, 
			Date dtInicio, Date dtFim, Integer pciSeq, String extensaoArquivo) 
					throws ApplicationBusinessException, IOException {
		
		return getRelatorioCirurgiasPendenteRetornoON().gerarRelatorioCirurgiasPendenteRetornoCSV(tipoPendenciaCirurgia, 
				unfSeq, dtInicio, dtFim, pciSeq, extensaoArquivo);
	}
	
	@Override
	public void validarTipoPendenciaRelatorioCirurgiasPendenteRetorno(
			DominioTipoPendenciaCirurgia tipoPendenciaCirurgia)
			throws ApplicationBusinessException {
		
		getRelatorioCirurgiasPendenteRetornoON()
				.validarTipoPendenciaRelatorioCirurgiasPendenteRetorno(
						tipoPendenciaCirurgia);
	}
	
	protected RelatorioCirurgiasPendenteRetornoON getRelatorioCirurgiasPendenteRetornoON() {
		return relatorioCirurgiasPendenteRetornoON;
	}
	
	@Override
	public List<MbcSalaCirurgica> pesquisarSalaCirurgia(Object objPesquisa, Short seq) {
		return this.getMbcSalaCirurgicaDAO().buscarSalasCirurgicas(objPesquisa.toString(), seq, DominioSituacao.A);
	}
	
	@Override
	public MbcSalaCirurgica obterSalaCirurgicaPorId(MbcSalaCirurgicaId id) {
		return getMbcSalaCirurgicaDAO().obterPorChavePrimaria(id);
	}
	
	protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO(){
		return mbcSalaCirurgicaDAO;
	}
	
	public RetornoCirurgiaEmLoteON getRetornoCirurgiaEmLoteON(){
		return retornoCirurgiaEmLoteON;
	}
	
	private MbcGrupoAlcadaRN getMbcGrupoAlcadaRN() {
		return mbcGrupoAlcadaRN;
	}

	@Override
	public List<MbcCirurgias> pesquisarRetornoCirurgiasEmLote(
			Short aghUnidadesFuncionaisSuggestionBox,
			Date dataCirurgia, 
			Short mbcSalaCirurgicaSuggestionBox,
			Integer prontuario) {
		
		return this.getRetornoCirurgiaEmLoteON().pesquisarRetornoCirurgiasEmLote(aghUnidadesFuncionaisSuggestionBox, dataCirurgia, mbcSalaCirurgicaSuggestionBox, prontuario);
	}	
	
	@Override
	public FatConvenioSaudePlano obterPlanoPorId(Byte planoId, Short convenioId) {
		
		return getRetornoCirurgiaEmLoteON().obterPlanoPorId(planoId ,convenioId);
	}

	@Override
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(
			String objPesquisa) {
		
		return getRetornoCirurgiaEmLoteON().pesquisarConvenioSaudePlanos(objPesquisa);
	}

	@Override
	public Long pesquisarCountConvenioSaudePlanos(String strPesquisa) {
		return getRetornoCirurgiaEmLoteON().pesquisarCountConvenioSaudePlanos(strPesquisa);
	}

	@Override
	public boolean verificarProfissionalEstaAtivoEmOutraUnidade(MbcProfAtuaUnidCirgs profissional) {
		return this.getMbcProfAtuaUnidCirgsDAO().verificarProfissionalEstaAtivoEmOutraUnidade(profissional);
	}
	
	@Override
	public MbcProfCirurgias buscarProfissionalResponsavel(Integer cirurgiaSeqSelecionada) {
		MbcCirurgias cirurgia = this.obterCirurgiaPorChavePrimaria(cirurgiaSeqSelecionada);
		MbcProfCirurgias mbcProfCirurgias = this.retornaResponsavelCirurgia(cirurgia);
		return mbcProfCirurgias;
	
	}	
	
	@Override
	public List<MbcAnestesiaCirurgias> obterTipoAnestesia(Integer cirurgiaSeqSelecionada) {
		return this.pesquisarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(cirurgiaSeqSelecionada, DominioSituacao.A);
		
	}

	@Override
	public List<MbcProcEspPorCirurgiasVO> buscarListaMbcProcEspPorCirurgiasVO(MbcCirurgias cirurgia) throws BaseException {
		return this.getRetornoCirurgiaEmLoteON().buscarListaMbcProcEspPorCirurgiasVO(cirurgia);
	}
	@Override
	public List<PacientesEmSalaRecuperacaoVO> listarPacientesEmSR(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeFuncional, Date dataEntradaSr) throws ApplicationBusinessException {
		return getPacientesEmSalaRecuperacaoON().listarPacientesEmSR(firstResult, maxResult, orderProperty, asc, unidadeFuncional, dataEntradaSr);
	}

	private PacientesEmSalaRecuperacaoON getPacientesEmSalaRecuperacaoON() {
		return pacientesEmSalaRecuperacaoON;
	}

	@Override
	public Long listarPacientesEmSRCount(AghUnidadesFuncionais unidadeFuncional, Date dataEntradaSr) throws ApplicationBusinessException {
		return getPacientesEmSalaRecuperacaoON().listarPacientesEmSRCount(unidadeFuncional, dataEntradaSr);
	}

	@Override
	public String setarLocalizacao(Integer pacCodigo) {
		return getPacientesEmSalaRecuperacaoON().setarLocalizacao(pacCodigo);
	}

	@Override
	public String setarCirurgiao(Integer crgSeq) {
		return getPacientesEmSalaRecuperacaoON().setarCirurgiao(crgSeq);
	}

	@Override
	public List<PacientesCirurgiaUnidadeVO> obterPacientesCirurgiaUnidade(Short unfSeq, Date crgDataInicio, Date crgDataFim, Integer serMatricula, Short serVinCodigo) {
		return getMbcCirurgiasDAO().obterPacientesCirurgiaUnidade(unfSeq,crgDataInicio,crgDataFim,serMatricula,serVinCodigo);
	}

	protected PacientesCirurgiaUnidadeON getPacientesCirurgiaUnidadeON() {
		return pacientesCirurgiaUnidadeON;
	}

	@Override
	public NomeArquivoRelatorioCrgVO gerarRelatorioPacientesCirurgiaUnidadeCSV(Short unfSeq, Date crgDataInicio, Date crgDataFim, Integer serMatricula, Short serVinCodigo, String extensaoArquivo) throws IOException, ApplicationBusinessException {
		return getPacientesCirurgiaUnidadeON().gerarRelatorioPacientesCirurgiaUnidadeCSV(unfSeq, crgDataInicio, crgDataFim, serMatricula, serVinCodigo, extensaoArquivo);
	}

	@Override
	public void mudarSituacao(RetornoCirurgiaEmLotePesquisaVO editada) throws ApplicationBusinessException {
		this.getRetornoCirurgiaEmLoteON().mudarSituacao(editada.getSituacao());
	}

	@Override
	public void gravarListaRetornoCirurgiaEmLote(
			MbcCirurgias cirurgiaParaAlterar, String host) throws ApplicationBusinessException,
			BaseException {
		
		this.getRetornoCirurgiaEmLoteON().gravar(cirurgiaParaAlterar, host);
	}
	
	@Override
	public MbcCirurgias obterCirurgiaPorSeqInnerJoinAtendimento(Integer seq){
		return this.getMbcCirurgiasDAO().obterCirurgiaPorSeqInnerJoinAtendimento(seq);
	}

	@Override
	public List<MbcCirurgias> pesquisarCirurgiasDeReserva(Date dataAgendamento, Integer agdSeq){
		return this.getMbcCirurgiasDAO().pesquisarCirurgiasDeReserva(dataAgendamento, agdSeq);
	}

	@Override
	public void mostrarSliders(RetornoCirurgiaEmLoteVO vo) {
		// TODO Auto-generated method stub
		
	}	
	
	@Override
	public List<ProfDescricaoCirurgicaVO> pesquisarAnestesistas(String strPesquisa, AghUnidadesFuncionais unf)  throws ApplicationBusinessException{
		return getRelatorioPacientesEntrevistarON().listarAnestesistas(strPesquisa,unf);
	}
	
	@Override
	public Long pesquisarAnestesistasCount(String strPesquisa,AghUnidadesFuncionais unf) throws ApplicationBusinessException {
	
		return getRelatorioPacientesEntrevistarON().listarAnestesistasCount(strPesquisa,unf);
	}
	
	private RelatorioPacientesEntrevistarON getRelatorioPacientesEntrevistarON() {
		return relatorioPacientesEntrevistarON;
	}

	
	
	@Override
	public List<EscalaCirurgiasVO> pesquisarRelatorioEscalaCirurgiasAghu(AghUnidadesFuncionais unidadesFuncional, Date dataCirurgia, String login, Integer grupoMat, MbcSalaCirurgica sala) throws ApplicationBusinessException {
		return getRelatorioEscalaCirurgiasAghuON().pesquisarRelatorioEscalaCirurgiasAghu(unidadesFuncional, dataCirurgia, login, grupoMat, sala);
	}
	
	private RelatorioEscalaCirurgiasAghuON getRelatorioEscalaCirurgiasAghuON() {
		return relatorioEscalaCirurgiasAghuON;
	}
	
	@Override
	public Byte buscarIntervaloEscalaCirurgiaProcedimento(MbcAgendas agenda) {
		return getMbcAgendasHorarioPrevisaoON().buscarIntervaloEscalaCirurgiaProcedimento(agenda);
	}
	
	@Override
	public List<RelatorioPacientesEntrevistarVO> pesquisarRelatorioPacientesEntrevistar(Short seqUnidCirurgia,Date dataCirurgia, ProfDescricaoCirurgicaVO anestesista) throws ApplicationBusinessException {
		return getRelatorioPacientesEntrevistarON().pesquisarRelatorioPacientesEntrevistar(seqUnidCirurgia, dataCirurgia, anestesista);
	}

	protected ProcedimentosCirurgicosPdtAtivosON getProcedimentosCirurgicosPdtAtivosON() {
		return procedimentosCirurgicosPdtAtivosON;
	}
	
	@Override
	public List<MbcProcedimentoCirurgicos> listarProcedimentoCirurgicoPorTipoSituacaoEspecialidade(
			String strPesquisa, Short especialidade) {
		
		return getProcedimentosCirurgicosPdtAtivosON().listarProcedimentoCirurgicoPorTipoSituacaoEspecialidade(strPesquisa, especialidade);
	}

	@Override
	public Long listarProcedimentoCirurgicoPorTipoSituacaoEspecialidadeCount(
			String strPesquisa, Short especialidade) {
		
		return getProcedimentosCirurgicosPdtAtivosON().listarProcedimentoCirurgicoPorTipoSituacaoEspecialidadeCount(strPesquisa, especialidade);
	}

	@Override
	public String gerarCSVProcedimentosCirurgicosPdtAtivos(final Short especialidade, final Integer procedimento) throws ApplicationBusinessException, IOException {
		return getProcedimentosCirurgicosPdtAtivosON().gerarCSVProcedimentosCirurgicosPdtAtivos(especialidade, procedimento);
	}
	
	@Override
	public void validarNroDeCopiasRelProcedCirgPdtAtivos(final Integer numeroDeCopias)throws ApplicationBusinessException {
		getProcedimentosCirurgicosPdtAtivosON().validarNroDeCopiasRelProcedCirgPdtAtivos(numeroDeCopias);
	}
	
	@Override
	public List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedimentosCirurgicosPdtAtivosListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short especialidade, Integer procedimento) throws ApplicationBusinessException {
		
		return getProcedimentosCirurgicosPdtAtivosON().obterProcedimentosCirurgicosPdtAtivosListaPaginada(firstResult, maxResult, orderProperty, asc, especialidade, procedimento);
	}
	
	@Override
	public Long obterProcedimentosCirurgicosPdtAtivosCount(
			Short especialidade, Integer procedimento) throws ApplicationBusinessException {
		return getProcedimentosCirurgicosPdtAtivosON().obterProcedimentosCirurgicosPdtAtivosCount(especialidade, procedimento);
	}
	
	@Override
	public List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedimentosCirurgicosPdtAtivosVO(
			Short especialidade, Integer procedimento) throws ApplicationBusinessException {
		return getProcedimentosCirurgicosPdtAtivosON().obterProcedimentosCirurgicosPdtAtivosVO(especialidade, procedimento);
	}
	
	@Override
	public List<PacientesEmListaDeProcedimentosCanceladosVO> obterPacientesEmListaDeProcedimentosCancelados(
			MbcProfAtuaUnidCirgs equipe, Short espSeq, Short unfSeq,
			Integer pacCodigo) throws ApplicationBusinessException {
		return getRelatorioCirurgiasListaEsperaON().obterPacientesEmListaDProcedimentosCancelados(equipe, espSeq, unfSeq, pacCodigo);
	}
	
	@Override
	public void persistirAgendaHemoterapia(MbcAgendaHemoterapia agendaHemoterapia) throws BaseException {
		getMbcAgendaHemoterapiaRN().persistirAgendaHemoterapia(agendaHemoterapia);
	}
	
	@Override
	public void excluirAgendaHemoterapia(
			MbcAgendaHemoterapia agendaHemoterapia)
			throws BaseException {
		getMbcAgendaHemoterapiaRN().excluirAgendaHemoterapia(agendaHemoterapia);
	}
	
	
	private MbcAgendaHemoterapiaRN getMbcAgendaHemoterapiaRN() {
		return mbcAgendaHemoterapiaRN;
	}
	
	@Override
	public void gerarPrevisaoInicioFimCirurgiaOrdemOverbooking(RapServidores pucServidor, MbcProfAtuaUnidCirgs profAtuaUnidCirgs, 
			AghEspecialidades especialidade, AghUnidadesFuncionais unidadeFuncional, Date dtAgenda, MbcAgendas agenda, 
			Date dataInicial, Short sciSeqp, Byte ordemOverbooking, Byte proximaOrdemOverbooking, Boolean deslocaHorario) throws BaseException{
		getMbcAgendasHorarioPrevisaoON().gerarPrevisaoInicioFimCirurgiaOrdemOverbooking(pucServidor, profAtuaUnidCirgs,
				especialidade, unidadeFuncional, dtAgenda, agenda, dataInicial, sciSeqp, ordemOverbooking, proximaOrdemOverbooking,
				deslocaHorario, false);
	}

	@Override
	public void persistirAnestesiaCirurgias(
			MbcAnestesiaCirurgias anestesiaCirurgia) throws BaseException {
		getMbcAnestesiaCirurgiasRN().persistir(anestesiaCirurgia);
	}
	
	@Override
	public void removerMbcAnestesiaCirurgias(
			MbcAnestesiaCirurgias anestesiaCirurgia) throws BaseException {
		getMbcAnestesiaCirurgiasRN().removerMbcAnestesiaCirurgias(anestesiaCirurgia);
	}
	
	private MbcAnestesiaCirurgiasRN getMbcAnestesiaCirurgiasRN() {
		return mbcAnestesiaCirurgiasRN;
	}

	@Override
	public void persistirProfessorResponsavel(MbcProfCirurgias profCirurgias) throws BaseException {
		getMbcProfCirurgiasRN().persistirProfissionalCirurgias(profCirurgias);
		
	}
	
	@Override
	public void removerProfessorResponsavel(MbcProfCirurgias profCirurgias) throws BaseException {
		getMbcProfCirurgiasRN().removerMbcProfCirurgias(profCirurgias, false);
		
	}
	
	private MbcProfCirurgiasRN getMbcProfCirurgiasRN() {
		return mbcProfCirurgiasRN;
	}

	@Override
	public Collection<RelatorioProdutividadePorAnestesistaVO> listarProdutividadePorAnestesista(
			AghUnidadesFuncionais unidadeCirurgica, DominioFuncaoProfissional funcaoProfissional, Date dataInicial,
			Date dataFinal){
		return this.getRelatorioProdutividadePorAnestesistaON().listarProdutividadePorAnestesista(unidadeCirurgica, funcaoProfissional, dataInicial, dataFinal);
	}
	
	protected RelatorioProdutividadePorAnestesistaON getRelatorioProdutividadePorAnestesistaON() {
		return relatorioProdutividadePorAnestesistaON;
	}

	@Override
	public Boolean verificarMaterialEspecial(MbcAgendas agenda) throws BaseException {
		return getMbcAgendasON().verificarMaterialEspecial(agenda);
	}
	
	protected RelatorioProfissionaisUnidadeCirurgicaON getRelatorioProfissionaisUnidadeCirurgicaON() {
		return relatorioProfissionaisUnidadeCirurgicaON;
	}
	
	@Override
	public List<RelatorioProfissionaisUnidadeCirurgicaVO> listarProfissionaisPorUnidadeCirurgica(Short seqUnidadeCirurgica, 
			Boolean ativosInativos, Short espSeq, DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica ordenacao) {
		return getRelatorioProfissionaisUnidadeCirurgicaON().listarProfissionaisPorUnidadeCirurgica(seqUnidadeCirurgica, ativosInativos, espSeq, ordenacao);
	}
	
	@Override
	public NomeArquivoRelatorioCrgVO gerarRelatorioCirurgiasCanceladasCSV(Short unfSeq, Date dtInicio, Date dtFim, String extensaoArquivo) throws IOException, ApplicationBusinessException {
		//this.setTimeout(TRANSACTION_TIMEOUT_1_HORA);
		return getRelatorioCirurgiasCanceladasON().gerarRelatorioCSV(unfSeq,dtInicio, dtFim, extensaoArquivo);
	}
	
	protected RelatorioCirurgiasCanceladasON getRelatorioCirurgiasCanceladasON() {
		return relatorioCirurgiasCanceladasON;
	}
	
	@Override
	public List<CirurgiaComPacEmTransOperatorioVO> pesquisarCirurgiasComPacientesEmTransOperatorio(
			DominioSituacaoCirurgia situacaoCirurgia,
			DominioOrigemPacienteCirurgia origemPacienteCirurgia,
			Boolean atendimentoIsNull, List<CirurgiaComPacEmTransOperatorioVO> cirurgiasComPacientesEmTransOperatorio, 
			AghUnidadesFuncionais unidadeFuncional) {
		return getBlocoCirurgicoON()
				.pesquisarCirurgiasComPacientesEmTransOperatorio(
						situacaoCirurgia, origemPacienteCirurgia,
						atendimentoIsNull,
						cirurgiasComPacientesEmTransOperatorio, unidadeFuncional);
	}

	@Override
	public String verificarColisaoDigitacaoNS(CirurgiaTelaVO vo) throws ApplicationBusinessException {
		return getVerificaColisaoDigitacaoNSON().verificarColisaoDigitacaoNS(vo);
	};
	
	private VerificaColisaoDigitacaoNSON getVerificaColisaoDigitacaoNSON() {
		return verificaColisaoDigitacaoNSON;
	}
	
	@Override
	public Boolean validarAvalicaoPreAnestesica(final Integer crgSelecionada) throws ApplicationBusinessException {
		return getRelatorioAvaliacaoPreAnestesicaON().validarAvalicaoPreAnestesica(crgSelecionada);
	}
	
	@Override
	@BypassInactiveModule
	public Map<AghAtendimentos, Boolean> validarExitenciaAvalicaoPreAnestesica(final Integer paciente) throws ApplicationBusinessException{
		return getRelatorioAvaliacaoPreAnestesicaON().validarExitenciaAvalicaoPreAnestesica(paciente);
	}

	@Override
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(String strPesquisa, Boolean somenteAtivos) {
		return getRelatorioPacientesComCirurgiaPorUnidadeON().listarUnidadesFuncionaisPorUnidadeExecutora(strPesquisa, somenteAtivos);
	}

	@Override //aqui
	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(String strPesquisa, Boolean somenteAtivos) {
		return getRelatorioPacientesComCirurgiaPorUnidadeON().listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa, somenteAtivos);
	}
	
	private RelatorioEtiquetasIdentificacaoON getRelatorioEtiquetasIdentificacaoON() {
		return relatorioEtiquetasIdentificacaoON;
	}

	public List<RelatorioEtiquetasIdentificacaoVO> pesquisarRelatorioEtiquetasIdentificacao(Short unfSeq, Short unfSeqMae, Boolean pacientesQueNaoPossuemPrevisaoAlta, Integer pacCodigoFonetica,
			Date dataCirurgia) {
		return getRelatorioEtiquetasIdentificacaoON().pesquisarRelatorioEtiquetasIdentificacao(unfSeq, unfSeqMae, pacientesQueNaoPossuemPrevisaoAlta, pacCodigoFonetica, dataCirurgia);
	}
	
	public void preImprimirValidarUnidadeFuncionalProntuarioUnidadeFuncionalMae(AghUnidadesFuncionais unidadeFuncional, Integer pacCodigoFonetica,
			VAghUnidFuncionalVO unidadeFuncionalMae, Date dataCirurgia, Boolean carateristicaUnidadeCirurgica) throws ApplicationBusinessException {
		getRelatorioEtiquetasIdentificacaoON().preImprimirValidarUnidadeFuncionalProntuarioUnidadeFuncionalMae(unidadeFuncional, pacCodigoFonetica,
				unidadeFuncionalMae, dataCirurgia, carateristicaUnidadeCirurgica);
	}
	

	public String obterUrlFichaAnestesica() throws BaseException {
		return getFichaAnestesicaON().obterUrlFichaAnestesica();
	}
	
	public String identificarAnestesia(Integer crgSeq,
			String microLogado, String nomePaciente, String procedimento)
			throws BaseException {
		return getFichaAnestesicaON().identificarAnestesia(crgSeq,
				microLogado, nomePaciente, procedimento);
	}

	public Boolean habilitarAnestesia(Integer crgSeq,
			DominioSituacaoCirurgia dominioSituacaoCirurgia)
			throws BaseException {
		return getFichaAnestesicaON().habilitarBotaoAnestesia(crgSeq,
				dominioSituacaoCirurgia);
	}
	
	public Boolean habilitarAnestesia(CirurgiaVO cirurgia)
			throws BaseException {
		return getFichaAnestesicaON().habilitarBotaoAnestesia(cirurgia);
	}
	
	
	
	public Boolean verificarExistenciaFichaAnestesica(Integer crgSeq)  {
		return getFichaAnestesicaON().verificarExistenciaFichaAnestesica(crgSeq);
	}
	
	@Override
	public List<ScoUnidadeMedida> pesquisarUnidadeConsumoEConversaoMateriaisConsumidos(Integer matCodigo, Object param) {
		return getMbcMaterialPorCirurgiaON().pesquisarUnidadeMedida(matCodigo, param);
	}
	
	@Override
	public Long pesquisarUnidadeConsumoEConversaoMateriaisConsumidosCount(Integer matCodigo, Object param) {
		return getMbcMaterialPorCirurgiaON().pesquisarUnidadeMedidaCount(matCodigo, param);
	}

	@Override
	public Integer obterSeqFichaAnestesica(CirurgiaVO crgSelecionada) throws BaseException {
		return getFichaAnestesicaON().obterSeqFichaAnestesica(crgSelecionada);
	}
	
	@Override
	public void validarIntervaloEntreDatasCirurgiasCanceladas(final Date dataInicio, final Date dataFim) throws ApplicationBusinessException {
		getRelatorioCirurgiasCanceladasON().validarIntervaloEntreDatasCirurgiasCanceladas(dataInicio, dataFim);
	}
	
	@Override
	public String mbccIdadeExtFormat(Date dtNascimento, Date data) {
		return getDetalhaRegistroCirurgiaON().mbccIdadeExtFormat(dtNascimento, data);
	}
	
	@Override
	public void validarIntervaloDatasPesquisa(Date dataInicial, Date dataFinal,
			AghuParametrosEnum paramPeriodoEntreDatas) throws ApplicationBusinessException {
		getBlocoCirurgicoON().validarIntervaldoEntreDatas(dataInicial, dataFinal, paramPeriodoEntreDatas);		
	}
	
	@Override
	public RelatorioLaudoAIHVO pesquisarLaudoAIH(Integer seqAtendimento,
			Integer codigoPac, Long seq, Integer matricula, Short vinCodigo)
			throws ApplicationBusinessException, ApplicationBusinessException {
		return this.getRelatorioLaudoAIHON().pesquisarLaudoAIH(
				seqAtendimento, codigoPac, seq, matricula, vinCodigo);
	}
	
	private RelatorioLaudoAIHON getRelatorioLaudoAIHON() {
		return relatorioLaudoAIHON;
	}

	@Override
	public List<MamLaudoAihVO> listarLaudosAIH(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AipPacientes paciente) {
		return getLaudoAihON().listarLaudosAIH(firstResult, maxResult, orderProperty, asc, paciente);
	}

	@Override
	public Long listarLaudosAIHCount(AipPacientes paciente) {
		return getLaudoAihON().listarLaudosAIHCount(paciente);
	}
	
	private ListaLaudoAihON getLaudoAihON() {
		return listaLaudoAihON;
	}

	@Override
	public List<MbcCirurgias> pesquisaCirurgiasPorCriterios(Date data, Boolean digitouNotaSala, Integer pacCodigo, Integer atendimentoSeq, DominioOrigemPacienteCirurgia origemPacienteCirurgia) {
		return this.getMbcCirurgiasDAO().pesquisaCirurgiasPorCriterios(data, digitouNotaSala, pacCodigo, atendimentoSeq, origemPacienteCirurgia);
	}


	@Override
	public List<Object> pesquisarVAinServInternaLaudoAih(
			Object pesquisa, Short espSeq) {
		return this.getVAinServInternaDAO().pesquisarVAinServInternaLaudoAih(pesquisa, espSeq);
	}

	@Override
	public Long pesquisarVAinServInternaLaudoAihCount(Object pesquisa,
			Short espSeq) {
		return this.getVAinServInternaDAO().pesquisarVAinServInternaLaudoAihCount(pesquisa, espSeq);
	}

	@Override
	public List<Object> pesquisarVAinServInternaMatriculaVinculoEsp(
			Integer matricula, Short vinCodigo, Short espSeq) {
		return getVAinServInternaDAO().pesquisarVAinServInternaMatriculaVinculoEsp(matricula, vinCodigo, espSeq);
	}
	
	protected VAinServInternaDAO getVAinServInternaDAO() {
		return vAinServInternaDAO;
	}
	
	@Override
	public MamLaudoAih imprimirLaudoAih(Long seq) throws ApplicationBusinessException{
		return getLaudoAihON().imprimirLaudoAih(seq);
	}

	@Override
	public boolean isLateralidade (final Integer procedimentoCirurgicoSeq){	
		return getAgendaProcedimentosON().isLateralidade(procedimentoCirurgicoSeq) ;
	}

	@Override
	public List<CirurgiaCodigoProcedimentoSusVO> listarCodigoProcedimentosSUS(
			Integer pciSeq, Short espSeq, DominioOrigemPacienteCirurgia origemPacienteCirurgia) throws ApplicationBusinessException {
		return this.getRegistroCirurgiaRealizadaON().codigoProcedimentoSusVOs(pciSeq, espSeq, origemPacienteCirurgia);

	}

	@Override
	public List<MbcGrupoAlcadaAvalOpms> listarGrupoAlcadaFiltro(
			Short grupoAlcadaSeq,
			AghEspecialidades aghEspecialidades,
			DominioTipoConvenioOpms tipoConvenioOpms, 
			DominioTipoObrigatoriedadeOpms tipoObrigatoriedadeOpms, 
			Short versao,
			DominioSituacao situacao) {
		return getMbcGrupoAlcadaRN().listarGrupoAlcadaFiltro(grupoAlcadaSeq,
				aghEspecialidades, 
				tipoConvenioOpms, 
				tipoObrigatoriedadeOpms, 
				versao, 
				situacao);
	}
	
	@Override
	public void removerMbcGrupoAlcada(MbcGrupoAlcadaAvalOpms itemExclusao) throws BaseException {
		getMbcGrupoAlcadaRN().removerMbcGrupoAlcadaAvalOpms(itemExclusao);
	}

	@Override
	public List<MbcCirurgias> pesquisarCirurgiaPorCaractUnidFuncionais(
			Integer crgSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		return getMbcCirurgiasDAO().pesquisarCirurgiaPorCaractUnidFuncionais(crgSeq, caracteristica);
	}
	
	@Override
	public List<RequerenteVO> consultarRequerentes(Object requerente) {
		return getAcompanharProcessoAutorizacaoOPMsON().consultarRequerentes(requerente);
	}
	
	protected AcompanharProcessoAutorizacaoOPMsON getAcompanharProcessoAutorizacaoOPMsON() {
		return acompanharProcessoAutorizacaoOPMsON;
	}
	
	@Override
	public List<ExecutorEtapaAtualVO> consultarExecutoresEtapaAtual(Object executor) {
		return getAcompanharProcessoAutorizacaoOPMsON().consultarExecutoresEtapaAtual(executor);
	}

	protected AghWFExecutorDAO getAghWFExecutorDAO() {
		return aghWFExecutorDAO;
	}
	
	@Override
	public List<PendenciaWorkflowVO> buscarPendenciasWorkflowPorUsuarioLogado(RapServidores servidorLogado){
		return this.getAghWFExecutorDAO().buscarPendenciasWorkflowPorUsuarioLogado(servidorLogado);
	}
	
	@Override
	public boolean habilitaUsuarioConectadoAhUnidade(Integer pIdentificao, Integer atdSeq) {
		return getBlocoCirurgicoON().habilitaUsuarioConectadoAhUnidade(pIdentificao, atdSeq);
	}
	
	@Override
	public boolean habilitaBotaoPrescrever(final CirurgiaVO crg) throws ApplicationBusinessException, ApplicationBusinessException {
		    return this.getListarCirurgiasON().habilitaBotaoPrescrever(crg);
	}

	protected MbcItensRequisicaoOpmesDAO getMbcItensRequisicaoOpmesDAO(){
		return mbcItensRequisicaoOpmesDAO;
	}
	
	@Override
	public List<MbcItensRequisicaoOpmes> pesquisarMatSolicitacaoOrcamento(Short seqRequisicaoOpme){
		return this.getMbcItensRequisicaoOpmesDAO().pesquisarMatSolicitacaoOrcamento(seqRequisicaoOpme);
	}
	
		
	@Override
	public SolicitarReceberOrcMatNaoLicitadoVO pesquisarMatNaoLicitado(Short seqRequisicaoOpme){
		return this.mbcRequisicaoOpmesDAO.pesquisarMatNaoLicitado(seqRequisicaoOpme);
	}
	
	@Override
	public List<MbcMateriaisItemOpmes> buscarItemMaterialPorItemRequisicao(MbcItensRequisicaoOpmes itemReq){
		return mbcMateriaisItemOpmesDAO.buscarItemMaterialPorItemRequisicao(itemReq);
	}
	
	
	@Override
	public void atualizarDetalheMaterial(MbcItensRequisicaoOpmes itemDetalhado){
		oPMEPortalAgendamentoRN.atualizarDetalheMaterial(itemDetalhado);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade#
	 * verificarExistenciaCirurgiaPorConsulta(java.lang.Integer)
	 */
	@Override
	public boolean verificarExistenciaCirurgiaPorConsulta(Integer numeroConsulta) {

		return getMbcCirurgiasDAO().verificarExistenciaCirurgiaPorConsulta(numeroConsulta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade#
	 * pesquisarCirurgiasFuturasAgendadasPorPaciente(java.lang.Integer)
	 */
	@Override
	public List<MbcCirurgias> pesquisarCirurgiasFuturasAgendadasPorPaciente(Integer codigoPaciente) {
		return getMbcCirurgiasDAO().pesquisarCirurgiasFuturasAgendadasPorPaciente(codigoPaciente);
	}
	
	protected MbcMateriaisItemOpmesDAO getMbcMateriaisItemOpmesDAO(){
		return mbcMateriaisItemOpmesDAO;
	}
	
	@Override
	public void gravarMateriaisItemOpmes(MbcMateriaisItemOpmes novoMateriaisItens){
		oPMEPortalAgendamentoRN.gravarMateriaisItemOpmes(novoMateriaisItens);
	}
	
	@Override
	public AghEspecialidades buscaAghEspecialidadesPorSeq(Short seq) {
		return getMbcGrupoAlcadaRN().obterAghEspecialidadesPorChavePrimaria(seq);
	}
	
	@Override
	public List<UnidadeFuncionalVO> pesquisarUnidadeFuncional(Object unidade){
		return getAcompanharProcessoAutorizacaoOPMsON().pesquisarUnidadeFuncional(unidade);
	}
	
	@Override
	public List<AghCid> pesquisarCidsPorPciSeq(Integer pciSeq, String descricao, DominioTipoPlano plano, String filtro) {
		return this.getBlocoCirurgicoON().pesquisarCidsPorPciSeq(pciSeq, descricao, plano, filtro);
	}
	
	@Override
	public List<EspecialidadeVO> pesquisarEspecialidade(Object especialidade){
		return getAcompanharProcessoAutorizacaoOPMsON().pesquisarEspecialidade(especialidade);
	}
	
	@Override
	public List<EquipeVO> pesquisarEquipe(Object equipe){
		return getAcompanharProcessoAutorizacaoOPMsON().pesquisarEquipe(equipe);
	}
	
	@Override
	public List<RequisicoesOPMEsProcedimentosVinculadosVO> pesquisarRequisicaoOpmes(Short seqRequisicao, Date dataRequisicao, 
																					RequerenteVO requerente,
																					DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa etapaAtualRequisicaoSelecionada,
																					ExecutorEtapaAtualVO executorEtapaAtual, 
																					Date dataProcedimento,
																					UnidadeFuncionalVO unidadeFuncional,
																					EspecialidadeVO especialidade, 
																					EquipeVO equipe,
																					Integer prontuario, 
																					Boolean pesquisarRequisicao, 
																					Integer nrDias,Integer executorSeq, Integer etapaSeq){
	
		return this.getBlocoCirurgicoON().pesquisarRequisicaoOpmes(seqRequisicao, dataRequisicao, 
																			requerente,
																			etapaAtualRequisicaoSelecionada,
																			executorEtapaAtual, 
																			dataProcedimento,
																			unidadeFuncional,
																			especialidade, 
																			equipe,
																			prontuario, 
																			pesquisarRequisicao, 
																			nrDias,executorSeq, etapaSeq);
	}
	
	@Override
	public MbcAgendas validarUsuarioSituacaoAjustarProcedimento(RequisicoesOPMEsProcedimentosVinculadosVO consAjusProcItem, String usuario) throws ApplicationBusinessException{
		return this.acompanharProcessoAutorizacaoOPMsRN.validarUsuarioSituacaoAjustarProcedimento(consAjusProcItem,usuario);
	}
	
		
	@Override
	public List<DescricaoCirurgicaMateriaisConsumidosVO> pesquisarMateriaisConsumidos(Integer cirurgiaSeq){
		return this.getBlocoCirurgicoON().pesquisarMateriaisConsumidos(cirurgiaSeq);
	}
	
	@Override
	public void validarConcluirMateriaisConsumidos(List<DescricaoCirurgicaMateriaisConsumidosVO> listaMateriaisConsumidos){
		this.getBlocoCirurgicoON().validarConcluirMateriaisConsumidos(listaMateriaisConsumidos);
	}
	
	@Override
	public void validaQtdeUtilizada(DescricaoCirurgicaMateriaisConsumidosVO itemMaterialConsumido){
		this.getBlocoCirurgicoON().validaQtdeUtilizada(itemMaterialConsumido);
	}
	@Override
	public String montarJustificativaMateriaisConsumidos(Short seqRequisicaoOpme){
		return this.getBlocoCirurgicoON().montarJustificativaMateriaisConsumidos(seqRequisicaoOpme);
	}

    @Override
	public void executaRotinaParaSetarResponsavelAoConfirmarNotaConsumo(List<CirurgiaTelaProfissionalVO> listaProfissionais, Short seqUnidadeCirurgica) 
			throws ApplicationBusinessException{
		getRegistroCirurgiaRealizadaON().executaRotinaParaSetarResponsavelAoConfirmarNotaConsumo(listaProfissionais, seqUnidadeCirurgica);
	}

	@Override
	public void executarRotinaVincularCidUnicoAoProcedimento(List<CirurgiaTelaProcedimentoVO> listaProcedimentos, DominioTipoPlano tipoPlano)
			throws ApplicationBusinessException {
		getRegistroCirurgiaRealizadaON().executarRotinaVincularCidUnicoAoProcedimento(listaProcedimentos, tipoPlano);
	}

	@Override
	public String obterPendenciaFichaAnestesia(Integer atdSeq){
		return this.getMbcFichaAnestesiasDAO().obterPendenciaFichaAnestesia(atdSeq);
	}
	@Override
	public MbcDestinoPaciente obterDestinoPaciente(Byte seq){
		return this.mbcDestinoPacienteDAO.obterPorChavePrimaria(seq);		
	}
	@Override
	public List<Object[]> obterCirurgiaDescCirurgicaPaciente(Integer pacCodigo, List<Short> listaUnfSeq, Date dataCirurgia){
		return this.getMbcDescricaoCirurgicaDAO().obterCirurgiaDescCirurgicaPaciente(pacCodigo, listaUnfSeq, dataCirurgia);
	}
	@Override
	public Object[] obterConselhoESiglaVRapServidorConselho(Integer matricula, Short vinCodigo){
		return this.vRapServidorConselhoDAO.obterConselhoESiglaVRapServidorConselho(matricula, vinCodigo);
	}
	@Override
	public List<MbcEquipeVO> obterProfissionaisAtuamUnidCirurgica(String filtro) {
		return this.mbcProfAtuaUnidCirgsDAO.obterProfissionaisAtuamUnidCirurgica(filtro);
	}
	@Override
	public Long obterProfissionaisAtuamUnidCirurgicaCount(Object filtro) {
		return this.mbcProfAtuaUnidCirgsDAO.obterProfissionaisAtuamUnidCirurgicaCount(filtro);
	}
	@Override
	public List<ProcedimentoCirurgicoVO> listarProcCirurgicosPorGrupo(String filtro) {
		return mbcGrupoProcedCirurgicoDAO.listarProcCirurgicosPorGrupo(filtro);
	}
	@Override
	public Long listarProcCirurgicosPorGrupoCount(Object filtro) {
		return mbcGrupoProcedCirurgicoDAO.listarProcCirurgicosPorGrupoCount(filtro);
	}
	@Override
	public AghAtendimentos obterAtendimentoVigentePacienteInternado(final Integer atdSeq, final Integer pacCodigo, Date dthrInicioCirg) {
		return this.getAgendaProcedimentosON().obterAtendimentoVigentePacienteInternado(atdSeq, pacCodigo, dthrInicioCirg);
	}
	
	@Override
	public MbcProcedimentoCirurgicos obterProcedimentoLancaAmbulatorio(Integer seq) {
		return this.getMbcProcedimentoCirurgicoDAO().obterProcedimentoLancaAmbulatorio(seq);
	}
	
	@Override
	public List<AghCid> pesquisarCidsPorPhiSeq(Integer phiSeq,DominioTipoPlano plano, String filtro) throws ApplicationBusinessException{
		return this.getBlocoCirurgicoON().pesquisarCidsPorPhiSeq(phiSeq, plano, filtro);
	}

	@Override
	public MbcCirurgias obterCirurgiaPorSeqServidor(Integer seq){
		return this.getMbcCirurgiasDAO().obterCirurgiaPorSeqServidor(seq);
	}
	
	@Override
	public MbcCirurgias obterCirurgiaPorDtInternacaoEOrigem(Integer pacCodigo, Date dtInternacao, DominioOrigemAtendimento origem) {
		return this.getMbcCirurgiasDAO().obterCirurgiaPorDtInternacaoEOrigem(pacCodigo, dtInternacao, origem);
	}
	
	@Override
	public MbcProfAtuaUnidCirgs buscarNomeResponsavelCirurgia(MbcProfCirurgias mbcProfCirurgias) {
		return this.getMbcProfAtuaUnidCirgsDAO().pesquisarMbcProfAtuaUnidCirgsPorMbcProfCirurgias(mbcProfCirurgias);
	}
	
	@Override
	@BypassInactiveModule
	public List<MbcDescricaoCirurgica> listarDescCirurgicaPorSeqESituacaoOrdenadasPorSeqp(Integer crgSeq, DominioSituacaoDescricaoCirurgia situacao) {
		return getMbcDescricaoCirurgicaDAO().listarDescCirurgicaPorSeqESituacaoOrdenadasPorSeqp(crgSeq, situacao);
	}
	
	@Override
	public List<NotificacoesGeraisVO> listarNotificacoesCirurgiaParaBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq){
		return getMbcAgendasDAO().listarNotificacoesCirurgiaParaBuscaAtiva(codigoPaciente, atdSeq);
	}
	@Override
	public List<MbcCirurgias> obterCirurgiasPorPacienteEDatas(List<Date> datas, Integer pacCodigo, List<Short> listaSeqp) {
		return mbcCirurgiasDAO.obterCirurgiasPorPacienteEDatas(datas, pacCodigo, listaSeqp);
	}	


	@Override
	public List<RapServidores> listarFichasAnestesias(
			Integer atedimentoSeq, Integer gsoPacCodigo, Short gsoSequence,
			DominioIndPendenteAmbulatorio pendente, Integer pciSeq,
			DominioSituacaoExame situacaoProcedimento) {
		
		return mbcFichaAnestesiasDAO.listarFichasAnestesias(
				atedimentoSeq, gsoPacCodigo, gsoSequence,
				pendente, pciSeq,
				situacaoProcedimento);
	}

	@Override
	public List<MbcProfAtuaUnidCirgsVO> obterMbcProfAtuaUnidCirgs(Integer equipeSeqp, Short paramValue) {
		return getMbcProfAtuaUnidCirgsDAO().obterMbcProfAtuaUnidCirgs(equipeSeqp, paramValue);
	}

	@Override
	public List<MbcFichaTipoAnestesiaVO> buscarProcedimentoAgendado(
			Integer seq, Integer pacCodigo, Short gestacaoSeqp, int intValue,
			int intValue2) {
		
		return this.getMbcFichaTipoAnestesiaDAO().buscarProcedimentoAgendado(seq, pacCodigo, gestacaoSeqp, intValue, intValue2);
	}

//	@Override
//	public FatConvenioSaudePlano validarConvenioPorAtendimento(
//			AghAtendimentos atendimendoOrigemUrgencia) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public FatConvenioSaudePlano validarAtendimentoUrgencia(Integer pacCodigo,
//			Short gestacaoSeqp, AghAtendimentos atendimendoOrigemUrgencia) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public void inserirCirurgiaDoCentroObstetrico(Integer pacCodigo,
			Short gestacaoSeqp, String nivelContaminacao,
			Date dataInicioProcedimento, Short salaCirurgicaSeqp,
			Short tempoDuracaoProcedimento, Short anestesiaSeqp,
			Short equipeSeqp, String tipoNascimento) throws BaseException {
		
		this.internacaoCOON.inserirCirurgiaDoCentroObstetrico(pacCodigo, gestacaoSeqp, nivelContaminacao, dataInicioProcedimento, salaCirurgicaSeqp, tempoDuracaoProcedimento, anestesiaSeqp, equipeSeqp, tipoNascimento);
		
	}
	
	@Override
	public Long obterCountDistinctDescricaoCirurgicaPorCrgSeqEServidor(Integer crgSeq, Integer servidorMatricula, Short servidorVinCodigo){
		return getMbcDescricaoCirurgicaDAO().obterCountDistinctDescricaoCirurgicaPorCrgSeqEServidor(crgSeq, servidorMatricula, servidorVinCodigo);
	}
	
	@Override
	public List<CirurgiaVO> pesquisarCirurgias(Integer crgSeq){
		return this.getMbcCirurgiasDAO().pesquisarCirurgias(null, null, null, crgSeq, null, null, null, null);
	}
	@Override
	public List<AvalOPMEVO> verificaObrigRegistroOpmes(MbcAgendas agenda){
		return mbcGrupoAlcadaDAO.verificaObrigRegistroOpmes(agenda);
	}
	@Override
	public void inserirAtendimentoPreparoCirurgia(AipPacientes paciente, MbcCirurgias cirurgia, String nomeMicrocomputador)  throws BaseException {
		mbcControleEscalaCirurgicaON.inserirAtendimentoPreparoCirurgia(paciente, cirurgia, nomeMicrocomputador);
	}

	@Override
	public RelatorioLaudoAIHSolicVO pesquisarLaudoAIHSolic(String materialSolicitado, Integer codigoPac, Integer matricula, Short vinCodigo) throws ApplicationBusinessException{
		return relatorioLaudoAIHON.pesquisarLaudoAIHSolic(materialSolicitado, codigoPac, matricula, vinCodigo); 
	}

	@Override
	public MbcProfAtuaUnidCirgs buscarNomeResponsavelCirurgia(Integer cirurgiaSeqSelecionada) {
		MbcCirurgias cirurgia = this.obterCirurgiaPorChavePrimaria(cirurgiaSeqSelecionada);
		MbcProfCirurgias mbcProfCirurgias = this.retornaResponsavelCirurgia(cirurgia);
		return this.getMbcProfAtuaUnidCirgsDAO().pesquisarMbcProfAtuaUnidCirgsPorMbcProfCirurgias(mbcProfCirurgias);
	}

	@Override
	public List<MbcSalaCirurgica> buscarSalasPorAgenda(MbcAgendas agenda) {
		MbcAgendas retorno = mbcAgendasDAO.obterPorChavePrimaria(agenda.getSeq());
		if(retorno.getSalaCirurgica() != null){
			mbcAgendasDAO.initialize(retorno.getSalaCirurgica());
		}
		
		List<MbcSalaCirurgica> lista = new LinkedList<>();
		lista.add(retorno.getSalaCirurgica());
		return lista;
		
	}

	@Override
	public Long listarGrupoAlcadaFiltroCount(Short grupoAlcadaSeq,
			AghEspecialidades aghEspecialidades,
			DominioTipoConvenioOpms tipoConvenioOpms, Short versao,
			DominioSituacao situacao) {
		// TODO Auto-generated method stub
		return null;
	}	
	@Override
	public List<MbcExtratoCirurgia> pesquisarMbcExtratoCirurgiaPorCirurgiaSituacao(Integer crgSeq, DominioSituacaoCirurgia situacao) {
		return getMbcExtratoCirurgiaDAO().pesquisarMbcExtratoCirurgiaPorCirurgiaSituacao(crgSeq, situacao);
	}

	@Override
	public MbcCirurgias obterCirurgiaAtendimentoCancelada(Integer crgSeq) {
		return mbcCirurgiasDAO.obterCirurgiaAtendimentoCancelada(crgSeq);
	}
	
	protected PdtViaAereasDAO getPdtViaAereasDAO() {
		return pdtViaAereasDAO;
	}

	@Override
	public List<ProfDescricaoCirurgicaVO> pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(Object objPesquisa, Short unfSeq,
			DominioFuncaoProfissional indFuncaoProf, List<String> listaSigla, DominioSituacao situacao) {
		
		return getMbcProfAtuaUnidCirgsDAO().pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(0, 100, objPesquisa, unfSeq, indFuncaoProf, listaSigla, situacao);
	}
	
	@Override
	public ProfDescricaoCirurgicaVO obterProfissionalDescricaoAnestesistaPorDescricaoCirurgica(Integer dcgCrgSeq, Short dcgSeqp) {
		return mbcProfDescricoesDAO.obterProfissionalDescricaoAnestesistaPorDescricaoCirurgica(dcgCrgSeq, dcgSeqp);
	}
	
	@Override
	public void persistirProfDescricaoExecutorAnestesia(Integer dcgCrgSeq, Short dcgSeqp, ProfDescricaoCirurgicaVO profDescricaoCirurgicaVO) throws ApplicationBusinessException {
		descricaoCirurgicaEquipeON.persistirProfDescricaoExecutorAnestesia(dcgCrgSeq, dcgSeqp, profDescricaoCirurgicaVO);
	}

	@Override
	public void verificarPacienteSalaPreparo(Integer crgSeq,  String nomeMicrocomputador, Date vinculoServidor) throws BaseException  {
		faturarCirurgiasCanceladasRN.verficarPacienteSalaPreparo(crgSeq, nomeMicrocomputador, vinculoServidor);
		
	}
	
	@Override
	public List<br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiaVO> obterCirurgiasPorPacienteEDatasGestacao(List<Date> datas, Integer pacCodigo, List<Short> listaSeqp) {
		
		List<MbcCirurgias> cirurgias = this.obterCirurgiasPorPacienteEDatas(datas, pacCodigo, listaSeqp);
		List<br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiaVO> listaBind = new ArrayList<br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiaVO>();
		for(MbcCirurgias cirurgia : cirurgias){
			listaBind.add(new br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiaVO(cirurgia.getSeq()));
		}
		return listaBind;
	}

	@Override
	public List<MbcCirurgias> listarLocalPacMbc(Integer pacCodigo) {
		return mbcCirurgiasDAO.listarLocalPacMbc(pacCodigo);
	}
	
	public void atualizarOrigemPacienteEAtendimentoDaCirurgia(MbcCirurgias cirurgia){
		if(cirurgia != null){
			this.getMbcCirurgiasDAO().atualizar(cirurgia);
		}
	}

}
