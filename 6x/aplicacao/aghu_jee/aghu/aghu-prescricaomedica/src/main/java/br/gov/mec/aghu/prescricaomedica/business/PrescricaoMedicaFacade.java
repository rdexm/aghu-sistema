package br.gov.mec.aghu.prescricaomedica.business;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
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
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEstadoPacienteDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRegistroDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.vo.AtestadoVO;
import br.gov.mec.aghu.comissoes.vo.SolicitacoesUsoMedicamentoVO;
import br.gov.mec.aghu.comissoes.vo.VMpmItemPrcrMdtosVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroTipoRespostaConsultoriaVO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoPacientesDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaractUnidFuncionaisDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.configuracao.dao.AghImpressoraPadraoUnidsDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.configuracao.dao.VAghUnidFuncionalDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.constantes.TipoItemAprazamento;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioDuracaoCalculo;
import br.gov.mec.aghu.dominio.DominioFinalizacao;
import br.gov.mec.aghu.dominio.DominioIndConcluidaSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioSituacaoPim2;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoCalculoDose;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.dominio.DominioTipoMedicaoPeso;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoEspecial;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.farmacia.dao.AfaCompoGrupoComponenteDAO;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDecimalComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoUsoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaParamComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoComposicoesDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoUsoMdtoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.farmacia.dao.MpmInformacaoPrescribentesDAO;
import br.gov.mec.aghu.farmacia.dao.VMamMedicamentosDAO;
import br.gov.mec.aghu.farmacia.vo.CodAtendimentoInformacaoPacienteVO;
import br.gov.mec.aghu.farmacia.vo.InformacoesPacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.farmacia.vo.QuantidadePrescricoesDispensacaoVO;
import br.gov.mec.aghu.faturamento.dao.FatSituacaoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.vo.ItemAlteracaoNptVO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.internacao.vo.DadosAltaSumarioVO;
import br.gov.mec.aghu.internacao.vo.JustificativaComponenteSanguineoVO;
import br.gov.mec.aghu.internacao.vo.PesquisaFoneticaPrescricaoVO;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.paciente.dao.AipAlergiaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.prontuario.vo.AltaObitoSumarioVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelatorioEvolucoesPacienteVO;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumStatusItem;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.dao.*;
import br.gov.mec.aghu.prescricaomedica.vo.AfaCompoGrupoComponenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaComposicaoNptPadraoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaGrupoComponenteNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaItemNptPadraoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaMedicamentoPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaCadastradaVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaEvolucaoEstadoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaPrincReceitasVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaRecomendacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioInfoComplVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioVO;
import br.gov.mec.aghu.prescricaomedica.vo.AvaliacaoMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.CalculoAdultoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.CentralMensagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.CidAtendimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.CompSanguineoProcedHemoterapicoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteNPTVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConfirmacaoPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaCompoGrupoComponenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaTipoComposicoesVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultarRetornoConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultoriasInternacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.CursorPacVO;
import br.gov.mec.aghu.prescricaomedica.vo.DadosDialiseVO;
import br.gov.mec.aghu.prescricaomedica.vo.DadosPesoAlturaVO;
import br.gov.mec.aghu.prescricaomedica.vo.DetalhesParecerMedicamentosVO;
import br.gov.mec.aghu.prescricaomedica.vo.EstadoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.GerarPDFSinanVO;
import br.gov.mec.aghu.prescricaomedica.vo.HistoricoParecerMedicamentosJnVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemDispensacaoFarmaciaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoDietaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.JustificativaMedicamentoUsoGeralVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.prescricaomedica.vo.ListaPacientePrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.LocalDispensa2VO;
import br.gov.mec.aghu.prescricaomedica.vo.LocalDispensaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ModoUsoProcedimentoEspecialVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmComposicaoPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmSolicitacaoConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO;
import br.gov.mec.aghu.prescricaomedica.vo.ParametrosProcedureVO;
import br.gov.mec.aghu.prescricaomedica.vo.ParecerPendenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ProcedimentoEspecialVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSolHemoterapicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioAltaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioAnamnesePacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioEstatisticaProdutividadeConsultorVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioLaudosProcSusVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioSumarioObitoVO;
import br.gov.mec.aghu.prescricaomedica.vo.RetornoConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.SinamVO;
import br.gov.mec.aghu.prescricaomedica.vo.SolicitacaoConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaPosAltaMotivoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaPrescricaoProcedimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosConsultoriasVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosCrgListasVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosCrgVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.TipoComposicaoComponenteVMpmDosagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificaAtendimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificarDadosItensJustificativaPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.VisualizaDadosSolicitacaoConsultoriaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.sig.custos.vo.NutricaoParenteralVO;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.vo.RapServidoresVO;

//@InjetarLogin
@Modulo(ModuloEnum.PRESCRICAO_MEDICA)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects", "PMD.NcssTypeCount" })
@Stateless
public class PrescricaoMedicaFacade extends BaseFacade implements IPrescricaoMedicaFacade {

	@EJB
	private AprovacaoEmLoteRN aprovacaoEmLoteRN;
	
	@EJB
	private AvaliacaoMedicamentoRN avaliacaoMedicamentoRN;
	
	@EJB
	private ManterObtCausaAntecedenteON manterObtCausaAntecedenteON;
	
	@EJB
	private ManterAltaReinternacaoON manterAltaReinternacaoON;
	
	@EJB
	private ManterSumarioRN manterSumarioRN;
	
	@EJB
	private ConcluirSumarioAltaON concluirSumarioAltaON;
	
	@EJB
	private ManterAltaMotivoON manterAltaMotivoON;
	
	@EJB
	private VisualizacaoFichaApacheON visualizacaoFichaApacheON;
	
	@EJB
	private RelatorioSumarioObitoON relatorioSumarioObitoON;
	
	@EJB
	private RelatorioConclusaoSumarioAltaON relatorioConclusaoSumarioAltaON;
	
	@EJB
	private CancelarPrescricaoMedicaON cancelarPrescricaoMedicaON;
	
	@EJB
	private ManterAltaSumarioON manterAltaSumarioON;
	
	@EJB
	private FichaApacheRN fichaApacheRN;
	
	@EJB
	private ManterObtCausaDiretaON manterObtCausaDiretaON;
	
	@EJB
	private ConsultoriaON consultoriaON;
	
	@EJB
	private LaudoRN laudoRN;
	
	@EJB
	private ManterPrescricaoMedicaRN manterPrescricaoMedicaRN;
	
	@EJB
	private LaudoProcedimentoSusRN laudoProcedimentoSusRN;
	
	@EJB
	private ListaPacientesInternadosON listaPacientesInternadosON;
	
	@EJB
	private ManterObitoNecropsiaON manterObitoNecropsiaON;
	
	@EJB
	private PrescreverProcedimentosEspeciaisON prescreverProcedimentosEspeciaisON;
	
	@EJB
	private ManterSumarioSchedulerRN manterSumarioSchedulerRN;
	
	@EJB
	private ManterTipoFrequenciaAprazamentoON manterTipoFrequenciaAprazamentoON;
	
	@EJB
	private ManterPrescricaoDietaON manterPrescricaoDietaON;
	
	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;
	
	@EJB
	private CadastroSinamRN cadastroSinamRN;

	@EJB
	private ManterModoUsoPrescProcedRN manterModoUsoPrescProcedRN;
	
	@EJB
	private RelatorioConsultoriaON relatorioConsultoriaON;
	
	@EJB
	private ManterAltaOtrProcedimentoON manterAltaOtrProcedimentoON;
	
	@EJB
	private ManterAltaEstadoPacienteON manterAltaEstadoPacienteON;
	
	@EJB
	private LaudoProcedimentoSusON laudoProcedimentoSusON;
	
	@EJB
	private ManterAltaPrincFarmacoON manterAltaPrincFarmacoON;
	
	@EJB
	private ManterAltaRecomendacaoON manterAltaRecomendacaoON;
	
	@EJB
	private DescricaoFormatadaRelatorioItensConfirmadosON descricaoFormatadaRelatorioItensConfirmadosON;
	
	@EJB
	private ManterCidUsualPorUnidadeRN manterCidUsualPorUnidadeRN;
	
	@EJB
	private ManterPrescricaoMedicaON manterPrescricaoMedicaON;
	
	@EJB
	private PrescreverProcedimentoEspecialRN prescreverProcedimentoEspecialRN;
	
	@EJB
	private ConfigurarListaPacientesON configurarListaPacientesON;
	
	@EJB
	private ManterPrescricaoMedicamentoON manterPrescricaoMedicamentoON;
	
	@EJB
	private AprazamentoRN aprazamentoRN;
	
	@EJB
	private ManterAltaCirgRealizadaON manterAltaCirgRealizadaON;
	
	@EJB
	private SolicitacaoHemoterapicaON solicitacaoHemoterapicaON;
	
	@EJB
	private ListaPacientesInternadosDialiseON listaPacientesInternadosDialiseON;
	
	@EJB
	private ManterDiagnosticoAtendimentoRN manterDiagnosticoAtendimentoRN;
	
	@EJB
	private LaudoON laudoON;
	
	@EJB
	private ConfirmarPrescricaoMedicaON confirmarPrescricaoMedicaON;
	
	@EJB
	private Pim2JournalRN pim2JournalRN;
	
	@EJB
	private ManterSumarioAltaPosAltaON manterSumarioAltaPosAltaON;
	
	@EJB
	private ListarSumarioAltaReimpressaoON listarSumarioAltaReimpressaoON;
	
	@EJB
	private ItemPrescricaoMedicamentoRN itemPrescricaoMedicamentoRN;
	
	@EJB
	private Pim2RN pim2RN;
	
	@EJB
	private ManterAltaSumarioRN manterAltaSumarioRN;
	
	@EJB
	private ParametroCalculoPrescricaoON parametroCalculoPrescricaoON;
	
	@EJB
	private ManterAltaOutraEquipeSumrON manterAltaOutraEquipeSumrON;
	
	@EJB
	private ManterAltaPedidoExameON manterAltaPedidoExameON;
	
	@EJB
	private MotivoIngressoCtiRN motivoIngressoCtiRN;
	
	@EJB
	private ManterUnidadeTempoON manterUnidadeTempoON;
	
	@EJB
	private AtualizarPrescricaoPendenteRN atualizarPrescricaoPendenteRN;
	
	@EJB
	private PrescricaoMedicamentoRN prescricaoMedicamentoRN;
	
	@EJB
	private VerificarPrescricaoON verificarPrescricaoON;
	
	@EJB
	private ManterHorarioInicAprazamentoON manterHorarioInicAprazamentoON;
	
	@EJB
	private ManterPrescricaoCuidadoON manterPrescricaoCuidadoON;
	
	@EJB
	private ManterSumarioAltaProcedimentosON manterSumarioAltaProcedimentosON;
	
	@EJB
	private ConcluirSumarioAltaRN concluirSumarioAltaRN;
	
	@EJB
	private ManterPrescricaoDietaRN manterPrescricaoDietaRN;
	
	@EJB
	private MpmTipoRespostaConsultoriaRN mpmTipoRespostaConsultoriaRN;
	
	@EJB
	private MpmSolicitacaoConsultoriaRN mpmSolicitacaoConsultoriaRN;
	
	@EJB
	private MpmCuidadoUsualRN mpmCuidadoUsualRN;
	
	@EJB
	private PrescreverItemMdtoON prescreverItemMdtoON;
	
	@EJB
	private ManterAltaConsultoriaON manterAltaConsultoriaON;
	
	@EJB
	private TipoFrequenciaAprazamentoON tipoFrequenciaAprazamentoON;
	
	@EJB
	private VMpmItemPrcrMdtosON vMpmItemPrcrMdtosON;
	
	@EJB
	private ConsultaHistoricoDiagnosticoAtendimentoON consultaHistoricoDiagnosticoAtendimentoON;
	
	@EJB
	private ManterAltaItemPedidoExameON manterAltaItemPedidoExameON;
	
	@EJB
	private PrescricaoMedicaON prescricaoMedicaON;
	
	@EJB
	private ManterDiagnosticoAtendimentoON manterDiagnosticoAtendimentoON;
	
	@EJB
	private ManterObtGravidezAnteriorON manterObtGravidezAnteriorON;
	
	@EJB
	private ConsultaSitDispMedicON consultaSitDispMedicON;
	
	@EJB
	private SumarioAltaON sumarioAltaON;
	
	@EJB
	private SumarioAltaRN sumarioAltaRN;
	
	@EJB
	private ManterObtOutraCausaON manterObtOutraCausaON;
	
	@EJB
	private MpmJustificativaUsoMdtoON mpmJustificativaUsoMdtoON;
	
	@EJB
	private MpmJustificativaUsoMdtoRN mpmJustificativaUsoMdtoRN;
	
	@EJB
	private MpmPacAtendProfissionalRN mpmPacAtendProfissionalRN;
	
	@EJB
	private MpmRespostaConsultoriaRN mpmRespostaConsultoriaRN;
	
	@EJB
	private MpmMotivoIngressoCtiRN mpmMotivoIngressoCtiRN;
	
	@EJB
	private RelatorioEstatisticaProdutividadeConsultorON relatorioEstatisicaProdutividadeConsultorON;
	
	@EJB
	private RelatorioListaConsultoriaON relatorioListaConsultoriaON;
	
	@EJB
	private VisualizaDadosSolicitacaoConsultoriaON visualizaDadosSolicitacaoConsultoriaON;
	
	@EJB
	private ControlPrevAltasON controlPrevAltasON;
		
	@EJB
	private PesquisaFoneticaPrescricaoON pesquisaFoneticaPrescricaoON;
		
	@EJB
	private AfaComposicaoNptPadraoRN afaComposicaoNptPadraoRN;
	
	@EJB
	private ManterFormulaNptPadraoRN manterFormulaNptPadraoRN;
	
	@Inject
	private MpmMotivoAltaMedicaDAO mpmMotivoAltaMedicaDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO prescricaoMedicaDAO;
	
	@Inject
	private MpmTipoRespostaConsultoriaDAO mpmTipoRespostaConsultoriaDAO;

	@Inject
	private VMpmPrescrMdtosDAO vMpmPrescrMdtosDAO;
	
	@Inject
	private MpmCuidadoUsualDAO mpmCuidadoUsualDAO;
	
	@Inject
	private MpmInformacaoPrescribentesDAO mpmInformacaoPrescribentesDAO;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private AfaComposGrupoComponentesRN afaComposGrupoComponentesRN;

	@EJB
	private AfaTipoComposicoesON afaTipoComposicoesON;	
	
	@Inject
	private MpmPrescricaoMedicaAuxiliarDAO mpmPrescricaoMedicaAuxiliarDAO;
	
	@Inject
	private AipAlergiaPacientesDAO aipAlergiaPacientesDAO;
		
	@Inject 
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private AfaItemNptPadraoRN afaItemNptPadraoRN;
	
	@Inject
	private MpmAlergiaUsualDAO mpmAlergiaUsualDAO;
	
	@Inject
	private VMpmPrescrMdtosDAO mpmPrescrMdtosDAO;
	
	@Inject
	private MpmParecerUsoMdtoJnDAO mpmParecerUsoMdtoJnDAO;
	
	@Inject
	private AfaMedicamentoDAO medicamentoDAO;
	
	@Inject
	private AfaTipoUsoMdtoDAO tipoUsoDAO;
	
	@Inject
	private AfaGrupoUsoMedicamentoDAO grupoUsoMedicamentoDAO;
	
	@Inject
	private VAghUnidFuncionalDAO vAghUnidFuncionalDAO;
	
	@Inject
	private VMpmItemPrcrMdtosDAO vMpmItemPrcrMdtosDAO;
	
	@Inject
	private VMpmpProfInternaDAO vMpmpProfInternaDAO;
	
	@Inject
	private VMedicoSolicitanteDAO vMedicoSolicitanteDAO;
	
	@Inject
	private AinQuartosDAO ainQuartosDAO;
		
	@Inject
	private MpmUnidadeTempoDAO mpmUnidadeTempoDAO;
	
	@Inject
	private MpmListaServResponsavelDAO mpmListaServResponsavelDAO;
	
	@Inject
	private MpmTipoLaudoTextoPadraoDAO mpmTipoLaudoTextoPadraoDAO;
	
	@Inject
	private MpmObtOutraCausaDAO mpmObtOutraCausaDAO;
	
	@Inject
	private MpmPacAtendProfissionalDAO mpmPacAtendProfissionalDAO;
	
	@Inject
	private MpmPostoSaudeDAO mpmPostoSaudeDAO;
	
	@Inject
	private MpmTipoLaudoDAO mpmTipoLaudoDAO;
	
	@Inject
	private MpmAltaEvolucaoDAO mpmAltaEvolucaoDAO;
	
	@Inject
	private MpmListaServEspecialidadeDAO mpmListaServEspecialidadeDAO;
	
	@Inject
	private MpmAltaMotivoDAO mpmAltaMotivoDAO;
	
	@Inject
	private MpmFichaApacheJnDAO mpmFichaApacheJnDAO;
	
	@Inject
	private VMpmDosagemDAO vMpmDosagemDAO;
	
	@Inject
	private MpmAltaReinternacaoDAO mpmAltaReinternacaoDAO;
	
	@Inject
	private MpmAltaTrgAlergiaDAO mpmAltaTrgAlergiaDAO;
	
	@Inject
	private MpmAltaOutraEquipeSumrDAO mpmAltaOutraEquipeSumrDAO;
	
	@Inject
	private MpmAltaRecomendacaoDAO mpmAltaRecomendacaoDAO;
	
	@Inject
	private MpmTrfDiagnosticoDAO mpmTrfDiagnosticoDAO;
	
	@Inject
	private MpmAltaRespostaConsultoriaDAO mpmAltaRespostaConsultoriaDAO;
	
	@Inject
	private MpmDataItemSumarioDAO mpmDataItemSumarioDAO;
	
	@Inject
	private MpmAltaEstadoPacienteDAO mpmAltaEstadoPacienteDAO;
	
	@Inject
	private MpmTrfEscoreGravidadeDAO mpmTrfEscoreGravidadeDAO;
	
	@Inject
	private MamPcSumMascLinhaDAO mamPcSumMascLinhaDAO;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@Inject
	private MpmAltaDiagPrincipalDAO mpmAltaDiagPrincipalDAO;
	
	@Inject
	private MpmCidUnidFuncionalDAO mpmCidUnidFuncionalDAO;
	
	@Inject
	private MpmAltaTriagemDAO mpmAltaTriagemDAO;
	
	@Inject
	private MpmServidorUnidFuncionalDAO mpmServidorUnidFuncionalDAO;
	
	@Inject
	private VMpmMdtoPrescNewDAO vMpmMdtoPrescNewDAO;
	
	@Inject
	private MamPcIntItemParadaDAO mamPcIntItemParadaDAO;
	
	@Inject
	private MpmAltaPedidoExameDAO mpmAltaPedidoExameDAO;
	
	@Inject
	private MamPcSumObservacaoDAO mamPcSumObservacaoDAO;
	
	@Inject
	private MpmFichaApacheDAO mpmFichaApacheDAO;
	
	@Inject
	private MpmListaServEquipeDAO mpmListaServEquipeDAO;
	
	@Inject
	private MpmProcedEspecialRmDAO mpmProcedEspecialRmDAO;
	
	@Inject
	private MamPcSumExameTabDAO mamPcSumExameTabDAO;
	
	@Inject
	private MpmAltaAtendimentoDAO mpmAltaAtendimentoDAO;
	
	@Inject
	private MpmTextoPadraoLaudoDAO mpmTextoPadraoLaudoDAO;
	
	@Inject
	private MpmModeloBasicoProcedimentoDAO mpmModeloBasicoProcedimentoDAO;
	
	@Inject
	private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;
	
	@Inject
	private MpmProcedEspecialDiversoDAO mpmProcedEspecialDiversoDAO;
	
	@Inject
	private MpmTrfMotivoDAO mpmTrfMotivoDAO;
	
	@Inject
	private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;
	
	@Inject
	private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;
	
	@Inject
	private MpmAltaComplFarmacoDAO mpmAltaComplFarmacoDAO;
	
	@Inject
	private MpmPim2DAO mpmPim2DAO;
	
	@Inject
	private MpmAltaPlanoDAO mpmAltaPlanoDAO;
	
	@Inject
	private MpmAltaDiagMtvoInternacaoDAO mpmAltaDiagMtvoInternacaoDAO;
	
	@Inject
	private MpmAltaImpDiagnosticaDAO mpmAltaImpDiagnosticaDAO;
	
	@Inject
	private MpmListaServSumrAltaDAO mpmListaServSumrAltaDAO;
	
	@Inject
	private MpmSolicitacaoConsultHistDAO mpmSolicitacaoConsultHistDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@Inject
	private MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO;
	
	@Inject
	private MpmAltaTrgMedicacaoDAO mpmAltaTrgMedicacaoDAO;
	
	@Inject
	private MpmObtCausaAntecedenteDAO mpmObtCausaAntecedenteDAO;
	
	@Inject
	private VMpmOtrProcedSumDAO vMpmOtrProcedSumDAO;
	
	@Inject
	private MpmPrescricaoMdtoHistDAO mpmPrescricaoMdtoHistDAO;
	
	@Inject
	private VMpmMdtosDescrDAO vMpmMdtosDescrDAO;
	
	@Inject
	private MpmUnidadeMedidaMedicaDAO mpmUnidadeMedidaMedicaDAO;
	
	@Inject
	private MpmTipoLaudoConvenioDAO mpmTipoLaudoConvenioDAO;
	
	@Inject
	private MpmLaudoDAO mpmLaudoDAO;
	
	@Inject
	private MpmSumarioAltaDAO mpmSumarioAltaDAO;
	
	@Inject
	private MpmMotivoIngressoCtiDAO mpmMotivoIngressoCtiDAO;
	
	@Inject
	private MpmAprazamentoFrequenciasDAO mpmAprazamentoFrequenciasDAO;
	
	@Inject
	private MpmAltaPrincExameDAO mpmAltaPrincExameDAO;
	
	@Inject
	private MpmAltaTrgExameDAO mpmAltaTrgExameDAO;
	
	@Inject
	private MpmAltaTrgSinalVitalDAO mpmAltaTrgSinalVitalDAO;
	
	@Inject
	private MpmMotivoReinternacaoDAO mpmMotivoReinternacaoDAO;
	
	@Inject
	private MpmAltaSumarioDAO mpmAltaSumarioDAO;
	
	@Inject
	private MpmParamCalculoPrescricaoDAO mpmParamCalculoPrescricaoDAO;
	
	@Inject
	private MpmAltaPrincFarmacoDAO mpmAltaPrincFarmacoDAO;
	
	@Inject
	private MpmAltaConsultoriaDAO mpmAltaConsultoriaDAO;
	
	@Inject
	private MpmModeloBasicoModoUsoProcedimentoDAO mpmModeloBasicoModoUsoProcedimentoDAO;
	
	@Inject
	private MpmHorarioInicAprazamentoDAO mpmHorarioInicAprazamentoDAO;
	
	@Inject
	private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;
	
	@Inject
	private MamPcIntParadaDAO mamPcIntParadaDAO;
	
	@Inject
	private MpmTrfDestinoDAO mpmTrfDestinoDAO;
	
	@Inject
	private MpmAltaOtrProcedimentoDAO mpmAltaOtrProcedimentoDAO;
	
	@Inject
	private MamPcSumExameMascDAO mamPcSumExameMascDAO;
	
	@Inject
	private MamPcSumMascCampoEditDAO mamPcSumMascCampoEditDAO;
	
	@Inject
	private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;
	
	@Inject
	private MpmItemPrescParecerMdtoDAO mpmItemPrescParecerMdtoDAO;
	
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;
	
	@Inject
	private MpmAltaDiagSecundarioDAO mpmAltaDiagSecundarioDAO;
	
	@Inject
	private AfaGrupoComponenteNptDAO afaGrupoComponenteNptDAO;
	
	@Inject
	private MpmAltaAtendMotivoDAO mpmAltaAtendMotivoDAO;
	
	@Inject
	private MpmAltaCirgRealizadaDAO mpmAltaCirgRealizadaDAO;
	
	@Inject
	private MamPcControlePacDAO mamPcControlePacDAO;
	
	@Inject
	private MpmAltaAtendRegistroDAO mpmAltaAtendRegistroDAO;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;

	@Inject
	private AfaGrupoComponenteNptsDAO afaGrupoComponenteNptsDAO;
	
	@Inject
	private AfaCompoGrupoComponenteDAO afaCompoGrupoComponenteDAO;
	@Inject
	private VMpmPrescrPendenteDAO vMpmPrescrPendenteDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private AghAtendimentoDAO atendimentoDAO;

	@Inject
	private MpmEscoreSaps3DAO mpmEscoreSaps3DAO;
	
	@EJB
	private EscoreSaps3RN escoreSaps3RN;
	
	@Inject
	private AghImpressoraPadraoUnidsDAO aghImpressoraPadraoUnidsDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	@EJB
	private RespostasConsultoriaON respostasConsultoriaON;
	
	@EJB
	private RetornoConsultoriaON retornoConsultoriaON;

	@EJB
	private  AfaTipoComposicoesRN  afaTipoComposicoesRN;
	
	@EJB
	private  MpmNotificacaoTbRN  mpmNotificacaoTbRN;

	@Inject
	private AfaMensCalculoNptDAO afaMensCalculoNptDAO;
	
	@Inject 
	private RapServidoresDAO rapServidoresDAO;

	@EJB
	private PrescricaoNptON prescricaoNptON;
	
	@EJB
	private PrescricaoNptRN prescricaoNptRN;
	
	@Inject
	private MpmJustificativaNptDAO mpmJustificativaNPTDAO;
	
	@Inject
	private MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO;
	
	@Inject
	private AfaComposicaoNptPadraoDAO afaComposicaoNptPadraoDAO;
		
	@Inject
	private AfaFormulaNptPadraoDAO afaFormulaNptPadraoDAO;

	@Inject
	private AghAtendimentoPacientesDAO aghAtendimentoPacientesDAO;
	
	@Inject
	private AfaTipoComposicoesDAO afaTipoComposicoesDAO;
	
	@Inject
	private FatSituacaoSaidaPacienteDAO fatSituacaoSaidaPacienteDAO;
	
	@Inject
	private VMamMedicamentosDAO vMamMedicamentosDAO;
	
	@EJB
	private ManterReceitaON manterReceitaON;

	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;

	@EJB
	private AfaFormulaNtpPadraoRN afaFormulaNtpPadraoRN;
	
	@Inject
	private AfaTipoVelocAdministracoesDAO afaTipoVelocAdministracoesDAO;
	
	@Inject
	private AfaItemNptPadraoDAO afaItemNptPadraoDAO;
	
	@Inject
	private AfaComponenteNptDAO afaComponenteNptDAO;
	
	@Inject
	private AfaFormaDosagemDAO afaFormaDosagemDAO;

	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	

	@Inject
	private AfaParamComponenteNptDAO afaParamComponenteNptDAO;
	
	@Inject
	private AfaDecimalComponenteNptDAO afaDecimalComponenteNptDAO;
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@Inject
	private MpmAltaPrincReceitasDAO mpmAltaPrincReceitasDAO;
	
	@Inject
	private MamReceituariosDAO mamReceituariosDAO;
	
	@EJB
	private PrescricaoMedicaAlergiaRN  prescricaoMedicaAlergiaRN;
	
	@Inject
	private MamEstadoPacienteDAO mamEstadoPacienteDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;

	@Inject
	private AghCaractUnidFuncionaisDAO aghCaractUnidFuncionaisDAO;

	@Inject
	private MpmJustificativaUsoMdtoDAO mpmJustificativaUsoMdtoDAO;

	@EJB
	private ConfirmarPrescricaoMedicaRN confirmarPrescricaoMedicaRN;

	@EJB
	private MpmTextoPadraoParecerON mpmTextoPadraoParecerON;
	
	@Inject
	private MpmTextoPadraoParecerDAO mpmTextoPadraoParecerDAO;
	
	@Inject
	private MamRegistroDAO mamRegistroDAO;

	@Inject
	private MamTipoEstadoPacienteDAO mamTipoEstadoPacienteDAO;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	@EJB
	private CalcularNptON calcularNptON;
	
	@Inject
	private MpmNotaAdicionalEvolucoesDAO mpmNotaAdicionalEvolucoesDAO;

	@Inject
	private MpmNotaAdicionalAnamnesesDAO mpmNotaAdicionalAnamnesesDAO;

	@Inject
	private MpmEvolucoesDAO mpmEvolucoesDAO;

	@Inject
	private MpmAnamnesesDAO mpmAnamnesesDAO;

	@EJB
	private MpmNotaAdicionalAnamnesesRN mpmNotaAdicionalAnamnesesRN;

	@EJB
	private MpmNotaAdicionalEvolucoesRN mpmNotaAdicionalEvolucoesRN;

	@EJB
	private MpmEvolucoesRN mpmEvolucoesRN;

	@EJB
	private RelatorioEvolucoesPacienteON relatorioEvolucoesPacienteON;

	@EJB
	private RelatorioAnamnesePacienteON relatorioAnamnesePacienteON;

	@EJB
	private MpmAnamneseRN mpmAnamneseRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7621673807612164559L;

//	private static final String ENTITY_MANAGER = "entityManager";

	/**
	 * Método que realiza o processo de tornar uma prescrição pendente.
	 * 
	 * @param prescricao
	 * @throws BaseException
	 */
	@Override
	@Secure("#{s:hasPermission('prescricaoMedica','deixarPendente')}")
	public void atualizarPrescricaoPendente(final Integer seqAtendimento,
			final Integer seqPrescricao, String nomeMicrocomputador) throws BaseException {
		this.getAtualizarPrescricaoPendenteRN().atualizarPrescricaoPendente(
				seqAtendimento, seqPrescricao, nomeMicrocomputador);
	}

	/**
	 * Lista os itens confirmados de uma prescrição.
	 * 
	 * @param prescricao
	 * @param dataTrabalho
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('itemPrescricaoMedica','pesquisar')}")
	@BypassInactiveModule
	public List<ItemPrescricaoMedica> listarItensPrescricaoMedicaConfirmados(
			final MpmPrescricaoMedica prescricao) {
		return this.getConfirmarPrescricaoMedicaON().listarItensPrescricaoMedicaConfirmados(prescricao);
	}

	/*
	 * Método responsável por executar a lógica de confirmar uma prescrição
	 * médica.
	 * 
	 * @param prescricao
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('prescricaoMedica','confirmar')}")
	public ConfirmacaoPrescricaoVO confirmarPrescricaoMedica(
			MpmPrescricaoMedica prescricao, String nomeMicrocomputador, 
			Date dataFimVinculoServidor) throws BaseException {
		return this.getConfirmarPrescricaoMedicaON().confirmarPrescricaoMedica(
				prescricao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * Método responsável por executar a lógica de gravar as alteracoes de
	 * Sumario Alta - Evolucao e Estado do Paciente
	 * 
	 * @param altaEvolucaoEstadoVo
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('sumarioAlta','elaborar')}")
	public AltaEvolucaoEstadoPacienteVO gravarAltaSumarioEvolucaoEstado(
			final AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoVo, final String origem)
			throws ApplicationBusinessException {
		return this.getManterAltaSumarioON().gravarAltaSumarioEvolucaoEstado(
				altaEvolucaoEstadoVo, origem);
	}
	
	@Override
	@Secure("#{s:hasPermission('sumarioAlta','elaborar')}")
	public AltaEvolucaoEstadoPacienteVO gravarAltaSumarioEvolucao(
			final AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoVo)
			throws ApplicationBusinessException {
		return this.getManterAltaSumarioON().gravarAltaSumarioEvolucao(
				altaEvolucaoEstadoVo);
	}
	
	@Override
	@Secure("#{s:hasPermission('sumarioAlta','elaborar')}")
	public AltaEvolucaoEstadoPacienteVO gravarAltaSumarioEstado(
			final AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoVo, final String origem)
			throws ApplicationBusinessException {
		return this.getManterAltaSumarioON().gravarAltaSumarioEstado(
				altaEvolucaoEstadoVo, origem);
	}

	/**
	 * Busca as informacoes pra o preenchimento da tela de Sumario de alta<br>
	 * Aba Evolucao<br>
	 * 
	 * @param altaSumario
	 *            , <code>MpmAltaSumario</code>
	 * @return <code>AltaEvolucaoEstadoPacienteVO</code>
	 */
	@Override
	@Secure("#{s:hasPermission('sumarioAlta','pesquisar')}")
	public AltaEvolucaoEstadoPacienteVO buscaAltaSumarioEvolucaoEstado(
			final MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		return this.getManterAltaSumarioON().buscaAltaSumarioEvolucaoEstado(
				altaSumario);
	}

	/**
	 * Método responsável por executar a lógica de cancelar uma prescrição
	 * médica.
	 * 
	 * @param idAtendimento
	 * @param seqPrescricao
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('prescricaoMedica','cancelar')}")
	public void cancelarPrescricaoMedica(final Integer idAtendimento,
			final Integer seqPrescricao, String nomeMicrocomputador) throws BaseException {
		this.getCancelarPrescricaoMedicaON().cancelarPrescricao(idAtendimento,
				seqPrescricao, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade#obterDadosJustificativaUsoMedicamento(java.lang.Integer)
	 */
	@Override
	public MpmJustificativaUsoMdto obterDadosJustificativaUsoMedicamento(Integer jumSeq) {

		return getMpmJustificativaUsoMdtoDAO().obterDadosJustificativaUsoMedicamento(jumSeq);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade#atualizarMpmJustificativaUsoMdto(br.gov.mec.aghu.model.MpmJustificativaUsoMdto)
	 */
	@Override
	public void atualizarMpmJustificativaUsoMdto(MpmJustificativaUsoMdto justificativa) {

		getMpmJustificativaUsoMdtoDAO().atualizar(justificativa);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade#validarPesquisaSolicitacoesUsoMedicamentos(br.gov.mec.aghu.comissoes.vo.SolicitacoesUsoMedicamentoVO)
	 */
	@Override
	public void validarPesquisaSolicitacoesUsoMedicamentos(SolicitacoesUsoMedicamentoVO filtro) throws ApplicationBusinessException {

		getvMpmItemPrcrMdtosON().validarPesquisaSolicitacoesUsoMedicamentos(filtro);
	}

	private CancelarPrescricaoMedicaON getCancelarPrescricaoMedicaON() {
		return cancelarPrescricaoMedicaON;
	}

	private ConfirmarPrescricaoMedicaON getConfirmarPrescricaoMedicaON() {
		return confirmarPrescricaoMedicaON;
	}

	/**
	 * Retorna RN de atualização da prescrição para que fique pendente.
	 * 
	 * @return
	 */
	private AtualizarPrescricaoPendenteRN getAtualizarPrescricaoPendenteRN() {
		return atualizarPrescricaoPendenteRN;

	}

	private ManterPrescricaoMedicaON getManterPrescricaoMedicaON() {
		return manterPrescricaoMedicaON;
	}

	private ManterAltaSumarioON getManterAltaSumarioON() {
		return manterAltaSumarioON;
	}

	private SolicitacaoHemoterapicaON getSolicitacaoHemoterapicaON() {
		return solicitacaoHemoterapicaON;
	}

	private ManterAltaSumarioRN getManterAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	@Override
	@BypassInactiveModule
	public String obtemNomeServidorEditado(final Short vinCodigo, final Integer matricula) {
		return this.getManterAltaSumarioRN().obtemNomeServidorEditado(
				vinCodigo, matricula);
	}
	@Override
	@BypassInactiveModule
	public Date obterDataInternacao(final Integer int_seq, final Integer atu_seq,
			final Integer hod_seq) throws ApplicationBusinessException {
		return this.getManterAltaSumarioRN().obterDataInternacao(int_seq,
				atu_seq, hod_seq);
	}

	private ManterAltaOutraEquipeSumrON getManterAltaOutraEquipeSumrON() {
		return manterAltaOutraEquipeSumrON;
	}

	private ManterAltaRecomendacaoON getManterAltaRecomendacaoON() {
		return manterAltaRecomendacaoON;
	}

	private ManterAltaItemPedidoExameON getManterAltaItemPedidoExameON() {
		return manterAltaItemPedidoExameON;
	}

	private PrescricaoMedicaRN getPrescricaoMedicaRN() {
		return prescricaoMedicaRN;
	}

	@Override
	@Secure("#{s:hasPermission('itemPrescricaoMedica','pesquisar')}")
	@BypassInactiveModule
	public List<ItemPrescricaoMedicaVO> buscarItensPrescricaoMedica(
			final MpmPrescricaoMedicaId prescricaoMedicaId, final Boolean listarTodos)
			throws ApplicationBusinessException {
		return this.getManterPrescricaoMedicaON().buscarItensPrescricaoMedica(
				prescricaoMedicaId, listarTodos);
	}

	@Override
	@Secure("#{s:hasPermission('itemPrescricaoMedica','pesquisar')}")
	@BypassInactiveModule
	public List<ItemPrescricaoMedicaVO> buscarItensPrescricaoMedica(MpmPrescricaoMedicaId prescricaoMedicaId,
			Boolean dieta, Boolean cuidadoMedico, Boolean medicamento, 
			Boolean solucao, Boolean consultoria, Boolean hemoterapia, 
			Boolean nutricaoParental, Boolean procedimento, Boolean listarTodas
			) throws ApplicationBusinessException {
		return this.getManterPrescricaoMedicaON().buscarItensPrescricaoMedica(
				prescricaoMedicaId, dieta, cuidadoMedico, medicamento, solucao,
				consultoria, hemoterapia, nutricaoParental, procedimento,
				listarTodas);
	}
	
	@Override
	@Secure("#{s:hasPermission('prescricaoMedica','pesquisar')}")
	@BypassInactiveModule
	public PrescricaoMedicaVO buscarDadosCabecalhoPrescricaoMedicaVO(
			final MpmPrescricaoMedicaId prescricaoMedicaId)
			throws ApplicationBusinessException {
		return this.getManterPrescricaoMedicaON()
				.buscarDadosCabecalhoPrescricaoMedicaVO(prescricaoMedicaId);
	}

	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('prescricaoMedica','pesquisar')}")
	public AghAtendimentos obterAtendimentoPorProntuario(final Integer prontuario) {
		return this.getVerificarPrescricaoON()
				.obterAtendimentoAtualPorProntuario(prontuario);
	}
	
	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('prescricaoMedica','pesquisar')}")
	public AghAtendimentos obterAtendimentoPorProntuarioLeito(final Integer prontuario) {
		return this.getVerificarPrescricaoON()
				.obterAtendimentoAtualPorProntuarioLeito(prontuario);
	}
	
	@Override
	@Secure("#{s:hasPermission('prescricaoMedica','pesquisar')}")
	@BypassInactiveModule
	public MpmPrescricaoMedica obterPrescricaoMedicaPorId(
			final MpmPrescricaoMedicaId id) {
		return this.getMpmPrescricaoMedicaDAO().obterPorChavePrimaria(id);
	}

	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('prescricaoMedica','pesquisar') or s:hasPermission('leito','pesquisar')}")
	public AghAtendimentos obterAtendimentoPorLeito(final String param)
			throws ApplicationBusinessException {
		return this.getVerificarPrescricaoON().obterAtendimentoAtualPorLeitoId(
				param);
	}

	@Override
	@Secure("#{s:hasPermission('prescricaoMedica','pesquisar')}")
	public List<MpmPrescricaoMedica> pesquisarPrescricoesMedicasNaoEncerradasPorAtendimento(
			final Integer seqAtendimento) {
		return this.getVerificarPrescricaoON()
				.pesquisarPrescricoesMedicasNaoEncerradasPorAtendimentoSeq(
						seqAtendimento);
	}

	@Override
	public MpmSumarioAlta obterSumarioAltaSemMotivoAltaPeloAtendimento(
			final Integer atdSeq) {
		return this.getManterPrescricaoMedicaON()
				.obterSumarioAltaSemMotivoAltaPeloAtendimento(atdSeq);
	}

	@Override
	public MpmTipoRespostaConsultoria obterMpmRespostaConsultoriaPorSeq(Short seq) {
		return this.mpmTipoRespostaConsultoriaDAO.obterMpmRespostaConsultoriaPorSeq(seq);
	}

	@Override
	public AghEspecialidades obterEspecialidadePorUsuarioConsultor() {
		return this.mpmSolicitacaoConsultoriaRN.obterEspecialidadePorUsuarioConsultor();
	}
	
	@Override
	public void verificarAcessoProfissionalEspecialidade(Short espSeq) throws ApplicationBusinessException {
		this.mpmSolicitacaoConsultoriaRN.verificarAcessoProfissionalEspecialidade(espSeq);
	}
	
	@Override
	public List<ConsultoriasInternacaoVO> listarConsultoriasInternacaoPorAtendimento(Short espSeq, Short unfSeq, DominioTipoSolicitacaoConsultoria tipo,
			DominioSimNao urgencia, DominioSituacaoConsultoria situacao) throws ApplicationBusinessException {
		return this.mpmSolicitacaoConsultoriaRN.listarConsultoriasInternacaoPorAtendimento(espSeq, unfSeq, tipo, urgencia, situacao);
	}

	@Override
	@BypassInactiveModule
	public MpmAltaSumario obterAltaSumarioConcluidaPeloAtendimento(
			final Integer apaAtdSeq) {
		return getManterPrescricaoMedicaON()
				.obterAltaSumarioConcluidaPeloAtendimento(apaAtdSeq);
	}

	/**
	 * Retorna a lista de pacientes internados do profissional logado.
	 * 
	 * @param servidor
	 * @return lista de {@link PacienteListaProfissionalVO}
	 * @throws BaseException
	 */
	@Override
	public List<PacienteListaProfissionalVO> listaPaciente(
			final RapServidores servidor) throws BaseException {
		return this.getListaPacientesInternadosON().pesquisarListaPacientes(
				servidor);
	}
	
	@Override
	public List<AelItemSolicitacaoExames> pesquisarExamesNaoVisualizados(final Integer atdSeq) {
		return aelItemSolicitacaoExameDAO.pesquisarExamesNaoVisualizados(atdSeq);
	}
	
	@BypassInactiveModule
	public List<PacienteListaProfissionalVO> pesquisarListaPacientesResumo(
			RapServidores servidor) {
		return this.getListaPacientesInternadosON()
				.pesquisarListaPacientesResumo(servidor);
	}

	/**
	 * Realiza as consistências antes de chamar a tela Sumário de Alta
	 * 
	 * @param atdSeq
	 * @throws BaseException
	 */
	@Override
	public void realizarConsistenciasSumarioAlta(final Integer atdSeq)
			throws ApplicationBusinessException {
		this.getListaPacientesInternadosON().realizarConsistenciasSumarioAlta(
				atdSeq);
	}

	/**
	 * Realiza as consistências antes de chamar a tela Sumário de Óbito
	 * 
	 * @param atdSeq
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void realizarConsistenciasSumarioObito(final Integer atdSeq)
			throws ApplicationBusinessException {
		this.getListaPacientesInternadosON().realizarConsistenciasSumarioObito(
				atdSeq);
	}

	/**
	 * Verificar se existe motivo de óbito para o atendimento
	 * 
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean isMotivoAltaObito(final Integer atdSeq)
			throws ApplicationBusinessException {
		return this.getListaPacientesInternadosON().isMotivoAltaObito(atdSeq);
	}

	/**
	 * Realiza as consistências antes de chamar a tela de Diagnósticos
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void realizarConsistenciasDiagnosticos() throws ApplicationBusinessException {
		this.getListaPacientesInternadosON()
				.realizarConsistenciasDiagnosticos();
	}
	
	/**
	 * Retorna a ON {@link ListaPacientesInternadosON}
	 * 
	 * @return
	 */
	private ListaPacientesInternadosON getListaPacientesInternadosON() {
		return listaPacientesInternadosON;
	}

	@Override
	public AbsSolicitacoesHemoterapicas clonarSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica)
			throws ApplicationBusinessException {
		return this.getSolicitacaoHemoterapicaON()
				.clonarSolicitacaoHemoterapica(solicitacaoHemoterapica);
	}

	/**
	 * Utilizado pela estória Manter Prescricao Medicamento.
	 * 
	 * @return
	 */

	@Override
	public List<MpmItemPrescricaoMdto> clonarItensPrescricaoMedicamento(
			final MpmPrescricaoMdto prescricaoMedicamento)
			throws ApplicationBusinessException {
		return this.getManterPrescricaoMedicamentoON()
				.clonarItensPrescricaoMedicamento(prescricaoMedicamento);
	}

	@Override
	public MpmPrescricaoMdto ajustaIndPendenteN(
			final MpmPrescricaoMdto prescricaoMedicamento)
			throws ApplicationBusinessException {
		return this.getManterPrescricaoMedicamentoON().ajustaIndPendenteN(
				prescricaoMedicamento);
	}

	@Override
	public MpmPrescricaoMdto ajustaIndPendenteN(
			final MpmPrescricaoMdto prescricaoMedicamento,
			final List<MpmItemPrescricaoMdto> listaItens) throws ApplicationBusinessException {
		return this.getManterPrescricaoMedicamentoON().ajustaIndPendenteN(
				prescricaoMedicamento, listaItens);
	}

	@Override
	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritosPelaChavePrescricao(
			final MpmPrescricaoMedicaId prescriao, final Date dtHrFimPrescricaoMedica,
			final Boolean isSolucao) {
		return this.getManterPrescricaoMedicamentoON()
				.obterListaMedicamentosPrescritosPelaChavePrescricao(prescriao,
						dtHrFimPrescricaoMedica, isSolucao);
	}

	@Override
	public List<AbsSolicitacoesHemoterapicas> obterListaSolicitacoesHemoterapicasPelaChavePrescricao(
			final MpmPrescricaoMedicaId prescricaoId) {
		return this.getSolicitacaoHemoterapicaON()
				.obterListaSolicitacoesHemoterapicasPelaChavePrescricao(
						prescricaoId);
	}

	@Override
	@BypassInactiveModule
	public MpmPrescricaoMdto obterPrescricaoMedicamento(final Integer atdSeq, final Long seq) {
		return this.getManterPrescricaoMedicamentoON().obterPrescricaoMedicamentoDetalhado(
				atdSeq, seq);
	}
	
	@Override
	@BypassInactiveModule
	public MpmPrescricaoMdtosHist obterPrescricaoMedicamentoHist(final Integer atdSeq, final Long seq) {
		return this.getMpmPrescricaoMdtoHistDAO().obterPorChavePrimaria(
				new MpmPrescricaoMdtosHistId(atdSeq, seq));
	}

	@Override
	public List<AfaMedicamentoPrescricaoVO> prepararListasMedicamento(
			final MpmAltaSumarioId id) throws ApplicationBusinessException {
		return this.getManterAltaSumarioON().prepararListasMedicamento(id);
	}

	@Override
	public void excluirJustificativaItemSolicitacaoHemoterapica(
			final AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa)
			throws ApplicationBusinessException {
		this.getSolicitacaoHemoterapicaON()
				.excluirJustificativaItemSolicitacaoHemoterapica(
						itemSolicitacaoHemoterapicaJustificativa);
	}

	@Override
	public AbsSolicitacoesHemoterapicas persistirSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador)
			throws BaseException {
		return this.getSolicitacaoHemoterapicaON().persistirSolicitacaoHemoterapica(
				solicitacaoHemoterapica, nomeMicrocomputador);
	}
	
	/**
	 * Este método chama a trigger MPMT_PDT_BRU de prescrição de dietas
	 * 
	 * @param prescricaoDieta
	 * @param dataFimVinculoServidor 
	 * @throws BaseException
	 */
	@Override
	public void atualizarPrescricaoDieta(final MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getManterPrescricaoDietaRN().beforeRowUpdate(prescricaoDieta, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void excluirSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador)
			throws BaseException{
		this.getSolicitacaoHemoterapicaON().removerSolicitacaoHemoterapica(
				solicitacaoHemoterapica, nomeMicrocomputador);
	}

	private ManterPrescricaoMedicamentoON getManterPrescricaoMedicamentoON() {
		return manterPrescricaoMedicamentoON;
	}

	private MpmPrescricaoMdtoDAO getMpmPrescricaoMdtoDAO() {
		return mpmPrescricaoMdtoDAO;
	}

	private MpmPrescricaoMdtoHistDAO getMpmPrescricaoMdtoHistDAO() {
		return mpmPrescricaoMdtoHistDAO;
	}

	@Override
	public List<VMpmDosagem> buscarDosagensMedicamento(final Integer medMatCodigo) {
		return this.getManterPrescricaoMedicamentoON()
				.buscarDosagensMedicamento(medMatCodigo);
	}

	@Override
	public void verificarCriarPrescricao(final AghAtendimentos atendimento)
			throws ApplicationBusinessException {
		this.getVerificarPrescricaoON().verificarCriarPrescricao(atendimento);
	}

	@Override
	@Secure("#{s:hasPermission('prescricaoMedica','criar')}")
	public Object criarPrescricao(final AghAtendimentos atendimento,
			final Date dataReferencia, String nomeMicrocomputador) throws BaseException {
		
		return this.getVerificarPrescricaoON().criarPrescricao(atendimento,
				dataReferencia, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('prescricaoMedica','editar')}")
	public void editarPrescricao(final MpmPrescricaoMedica prescricao,
			final Boolean cienteEmUso) throws BaseException {
		this.getVerificarPrescricaoON().editarPrescricao(prescricao,
				cienteEmUso);
	}

	private VerificarPrescricaoON getVerificarPrescricaoON() {
		return verificarPrescricaoON;
	}

	// TODO: @Secure("#{s:hasPermission('?','?')}")
	@Override
	public List<MpmTipoFrequenciaAprazamento> buscarTipoFrequenciaAprazamento(
			final String strPesquisa) {
		return this.getManterPrescricaoMedicamentoON()
				.buscarTipoFrequenciaAprazamento(strPesquisa);
	}
	
	// TODO: @Secure("#{s:hasPermission('?','?')}")
	@Override
	public Long buscarTipoFrequenciaAprazamentoCount(
			final String strPesquisa) {
		return this.getManterPrescricaoMedicamentoON()
				.buscarTipoFrequenciaAprazamentoCount(strPesquisa);
	}

	// TODO: @Secure("#{s:hasPermission('?','?')}")
	@Override
	public List<MpmTipoFrequenciaAprazamento> buscarTipoFrequenciaAprazamentoHemoterapico(
			final String strPesquisa) {
		return this.getSolicitacaoHemoterapicaON()
				.buscarTipoFrequenciaAprazamentoHemoterapico(strPesquisa);
	}

	// TODO: @Secure("#{s:hasPermission('?','?')}")
	@Override
	public Boolean validaBombaInfusao(final AghUnidadesFuncionais unidade,
			final AfaViaAdministracao viaAdministracao, final AfaMedicamento medicamento) {
		return this.getManterPrescricaoMedicamentoON().validaBombaInfusao(
				unidade, viaAdministracao, medicamento);
	}

	/**
	 * 
	 * bsoliveira
	 * 
	 * 07/10/2010
	 * 
	 * Chama método que verifica se existe algum item na lista de itens da
	 * prescrição que devem ser excluido, cas exista chama o metodo de exclusão
	 * e remove o objeto da lista.
	 * 
	 * @throws ApplicationBusinessException
	 */

	// TODO: @Secure("#{s:hasPermission('?','?')}")
	@Override
	public void verificaDoseFracionada(final Integer codigoMedicamento,
			final BigDecimal dose, final Integer seqFormaDosagem)
			throws ApplicationBusinessException {
		this.getManterPrescricaoMedicamentoON().verificaDoseFracionada(
				codigoMedicamento, dose, seqFormaDosagem);
	}

	@Override
	@Secure("#{s:hasPermission('itemPrescricaoMedica','excluir')}")
	public void excluirSelecionados(final MpmPrescricaoMedica prescricaoMedica,
			final List<ItemPrescricaoMedicaVO> itens, String nomeMicrocomputador) throws BaseException {
		this.getManterPrescricaoMedicaON().excluirSelecionados(
				prescricaoMedica, itens, nomeMicrocomputador);

	}

	@Override
	@Secure("#{s:hasPermission('itemPrescricaoMedica','excluir')}")
	public void excluirPrescricaoMedicamento(
			final MpmPrescricaoMdto prescricaoMedicamento)
			throws ApplicationBusinessException {
		this.getManterPrescricaoMedicamentoON().excluirPrescricaoMedicamento(
				prescricaoMedicamento);
	}

	@Override
	@Secure("#{s:hasPermission('itemPrescricaoMedica','persistir')}")
	public void persistirPrescricaoMedicamento(
			final MpmPrescricaoMdto prescricaoMedicamento, String nomeMicrocomputador, MpmPrescricaoMdto prescricaoMedicamentoOriginal) throws BaseException {
		// Chama o método persistir considerando que o item não foi originado de
		// uma cópia
		// quando da criação de uma nova Prescrição Médica
		this.persistirPrescricaoMedicamento(prescricaoMedicamento, false, nomeMicrocomputador,prescricaoMedicamentoOriginal);
	}
	
	public void persistirPrescricaoMedicamentoCancelar(
			final MpmPrescricaoMdto prescricaoMedicamento, String nomeMicrocomputador, MpmPrescricaoMdto prescricaoMedicamentoOriginal) throws BaseException {
		this.getManterPrescricaoMedicamentoON().persistirPrescricaoMedicamentoCancelar(prescricaoMedicamento, false, nomeMicrocomputador,prescricaoMedicamentoOriginal);
	}

	@Override
	@Secure("#{s:hasPermission('itemPrescricaoMedica','persistir')}")
	public void persistirPrescricaoMedicamento(
			final MpmPrescricaoMdto prescricaoMedicamento, final Boolean isCopiado, String nomeMicrocomputador
			, MpmPrescricaoMdto prescricaoMedicamentoOriginal)
			throws BaseException {
		
		this.getManterPrescricaoMedicamentoON().persistirPrescricaoMedicamento(
				prescricaoMedicamento, isCopiado, nomeMicrocomputador,prescricaoMedicamentoOriginal);
	}

	public void verificaGrupoUsoMedicamentoTuberculostatico(
			List<MpmItemPrescricaoMdto> itensPrescricaoMdtos) throws BaseException{
		this.getManterPrescricaoMedicamentoON().verificaGrupoUsoMedicamentoTuberculostatico(itensPrescricaoMdtos);
	}

	@Override
	@Secure("#{s:hasPermission('itemPrescricaoMedica','persistir')}")
	public void persistirPrescricaoMedicamentos(
			final List<MpmPrescricaoMdto> prescricaoMedicamentos, String nomeMicrocomputador, List<MpmPrescricaoMdto> prescricaoMedicamentosOriginal)
			throws BaseException {
		
		this.getManterPrescricaoMedicamentoON()
				.persistirPrescricaoMedicamentos(prescricaoMedicamentos, nomeMicrocomputador,prescricaoMedicamentosOriginal);
	}

	@Override
	@Secure("#{s:hasPermission('itemPrescricaoMedica','persistir')}")
	public void persistirParecerItemPrescricaoMedicamento(
			final MpmItemPrescParecerMdto itemParecer) throws ApplicationBusinessException {
		this.getManterPrescricaoMedicamentoON()
				.persistirParecerItemPrescricaoMedicamento(itemParecer);
	}
	
	// métodos da estória configurar lista de pacientes
	private ConfigurarListaPacientesON getConfigurarListaPacientesON() {
		return configurarListaPacientesON;
	}

	@Override
	@BypassInactiveModule
	public List<AghEspecialidades> getListaEspecialidades(final RapServidores servidor)
			throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON().getListaEspecialidades(
				servidor);
	}

	@Override
	@BypassInactiveModule
	public List<AghEspecialidades> getListaEspecialidades(final String parametro)
			throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON().getListaEspecialidades(
				parametro);
	}

	@Override
	@BypassInactiveModule
	public void salvarListaEspecialidades(
			final List<AghEspecialidades> listaEspecialidades, final RapServidores servidor)
			throws ApplicationBusinessException {
		this.getConfigurarListaPacientesON().salvarListaEspecialidades(
				listaEspecialidades, servidor);
	}

	@Override
	@BypassInactiveModule
	public List<AghEquipes> getListaEquipes(final RapServidores servidor)
			throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON().getListaEquipes(servidor);
	}

	@Override
	@BypassInactiveModule
	public List<ProfessorCrmInternacaoVO> getListaResponsaveis(final RapServidores servidor) throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON().getListaResponsaveis(servidor);
	}
	
	@Override
	public List<AghEquipes> getListaEquipesPorEspecialidade(final Object objPesquisa,
			final AghEspecialidades especialidade, final DominioSituacao situacao) throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON()
				.getListaEquipesPorEspecialidade(objPesquisa, especialidade, situacao);
	}	
	
	@Override
	public List<VRapServidorConselho> getListaProfissionaisPorEspecialidade(
			final Object objPesquisa, final AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON()
				.getListaProfissionaisPorEspecialidade(objPesquisa,
						especialidade);
	}

	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			final RapServidores servidor) throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON().getListaUnidadesFuncionais(
				servidor);
	}

	@Override
	@BypassInactiveModule
	public List<AghEquipes> getListaEquipes(final String paramString)
			throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON()
				.getListaEquipes(paramString);
	}

	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			final String paramString) throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON().getListaUnidadesFuncionais(
				paramString);
	}

	@Override
	@BypassInactiveModule
	public void salvarListaEquipes(final List<AghEquipes> listaEquipes,
			final RapServidores servidor) throws ApplicationBusinessException {
		this.getConfigurarListaPacientesON().salvarListaEquipes(listaEquipes,
				servidor);

	}
	
	@Override
	@BypassInactiveModule
	public void salvarListaResponsaveis(final List<ProfessorCrmInternacaoVO> listaResponsaveis, final RapServidores servidor) throws ApplicationBusinessException {
		this.getConfigurarListaPacientesON().salvarListaResponsaveis(listaResponsaveis, servidor);

	}

	@Override
	@BypassInactiveModule
	public void salvarListaUnidadesFuncionais(
			final List<AghUnidadesFuncionais> listaUnidadesFuncionais,
			final RapServidores servidor) throws ApplicationBusinessException {
		this.getConfigurarListaPacientesON().salvarListaUnidadesFuncionais(
				listaUnidadesFuncionais, servidor);
	}

	@Override
	@BypassInactiveModule
	public List<AghAtendimentos> getListaAtendimentos(final RapServidores servidor)
			throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON().getListaAtendimentos(
				servidor);
	}

	@Override
	@BypassInactiveModule
	public MpmListaPacCpa getPacienteCuidadosPosAnestesicos(
			final RapServidores servidor) throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON()
				.getPacienteCuidadosPosAnestesicos(servidor);
	}

	@Override
	@BypassInactiveModule
	public void salvarIndicadorPacientesAtendimento(final boolean indicadorPacCPA,
			final RapServidores servidor) throws ApplicationBusinessException {
		this.getConfigurarListaPacientesON()
				.salvarIndicadorPacientesAtendimento(indicadorPacCPA, servidor);
	}

	@Override
	@BypassInactiveModule
	public List<AghAtendimentos> getListaAtendimentos(final Integer prontuario)
			throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON().getListaAtendimentos(
				prontuario);
	}

	@Override
	@BypassInactiveModule
	public void salvarListaAtendimentos(
			final List<AghAtendimentos> listaServAtendimentos, final RapServidores servidor)
			throws ApplicationBusinessException {
		this.getConfigurarListaPacientesON().salvarListaAtendimentos(
				listaServAtendimentos, servidor);
	}

	@Override
	public List<AghAtendimentos> pesquisaFoneticaAtendimentos(
			final String nomePesquisaFonetica, final String leitoPesquisaFonetica,
			final String quartoPesquisaFonetica,
			final AghUnidadesFuncionais unidadeFuncionalPesquisaFoneticaSelecionada)
			throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesON()
				.pesquisaFoneticaAtendimentos(nomePesquisaFonetica,
						leitoPesquisaFonetica, quartoPesquisaFonetica,
						unidadeFuncionalPesquisaFoneticaSelecionada);
	}

	// métodos da estória #882 Prescrever Procedimentos Especiais
	protected PrescreverProcedimentosEspeciaisON getPrescreverProcedimentosEspeciaisON() {
		return prescreverProcedimentosEspeciaisON;
	}

	@Override
	public List<MbcProcedimentoCirurgicos> getListaProcedimentoCirurgicosRealizadosNoLeito(
			final Object objPesquisa) {
		return this.getPrescreverProcedimentosEspeciaisON()
				.getListaProcedimentosRealizadosLeito(objPesquisa);
	}

	@Override
	public List<ScoMaterial> getListaMateriaisOrteseseProteses(
			final BigDecimal paramVlNumerico, final Object objPesquisa)
			throws ApplicationBusinessException {
		return this.getPrescreverProcedimentosEspeciaisON()
				.getListaOrtesesProteses(paramVlNumerico, objPesquisa);
	}

    public List<ScoMaterial> obterMateriaisOrteseseProtesesPrescricao(final BigDecimal paramVlNumerico,
                                                                      final String  objPesquisa) {
        return this.getPrescreverProcedimentosEspeciaisON()
                .obterMateriaisOrteseseProtesesPrescricao(paramVlNumerico, objPesquisa);
    }

	@Override
	public List<MpmTipoModoUsoProcedimento> getListaTipoModoUsoProcedEspeciaisDiversos(
			final Object objPesquisa, final MpmProcedEspecialDiversos procedEspecial)
			throws ApplicationBusinessException {

		return this.getPrescreverProcedimentosEspeciaisON()
				.getListaTipoModoUsoProcedimento(objPesquisa, procedEspecial);
	}

	@Override
	public List<MpmProcedEspecialDiversos> getListaProcedEspecialDiversos(
			final Object objPesquisa) {
		return this.getPrescreverProcedimentosEspeciaisON()
				.getListaProcedEspecialDiversos(objPesquisa);
	}

	@Override
	public List<ItemPrescricaoMedicaVO> buscaListaMedicamentoPorPrescricaoMedica(
			final MpmPrescricaoMedicaId prescricaoMedicaId) {
		return this.getManterPrescricaoMedicaON()
				.buscaListaMedicamentoPorPrescricaoMedica(prescricaoMedicaId);
	}

	@Override
	public List<ItemPrescricaoMedicaVO> buscaListaProcedimentoEspecialPorPrescricaoMedica(
			final MpmPrescricaoMedicaId prescricaoMedicaId) {
		return this.getManterPrescricaoMedicaON()
				.buscaListaProcedimentoEspecialPorPrescricaoMedica(
						prescricaoMedicaId);
	}

	@Override
	public Short buscaCalculoQuantidade24Horas(final Short frequencia,
			final Short seqTipoFrequenciaAprazamento, final BigDecimal dosePrescrita,
			final Integer seqFormaDosagem, final Integer codigoMedicamento)
			throws ApplicationBusinessException {
		return this.getItemPrescricaoMedicamentoRN()
				.buscaCalculoQuantidade24Horas(frequencia,
						seqTipoFrequenciaAprazamento, dosePrescrita,
						seqFormaDosagem, codigoMedicamento);
	}

	/**
	 * Sumário de Alta
	 * 
	 * @param apaAtdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */

	@Override
	@BypassInactiveModule
	public MpmAltaSumario obterAltaSumariosAtivoConcluido(final Integer apaAtdSeq) {
		return this.getMpmAltaSumarioDAO().obterAltaSumariosAtivoConcluido(apaAtdSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Boolean habilitarAltaSumario(final Integer atdSeq) {
		return this.getManterPrescricaoMedicaON().habilitarAltaSumario(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public AltaSumarioVO populaAltaSumarioVO(final MpmAltaSumario altaSumario,
			final String origem) throws BaseException {
		return this.getManterAltaSumarioON().populaAltaSumarioVO(altaSumario,
				origem);
	}

	@Override
	@BypassInactiveModule
	public MpmUnidadeMedidaMedica obterUnidadeMedicaPorId(final Integer fdsUmmSeq) {
		return this.getMpmUnidadeMedidaMedicaDAO()
				.obterUnidadesMedidaMedicaPeloId(fdsUmmSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarAssociacoesPorServidorCount(final  RapServidores servidor) {
		return getMpmListaServEquipeDAO().pesquisarAssociacoesPorServidorCount(servidor);
	}
	
	protected MpmUnidadeMedidaMedicaDAO getMpmUnidadeMedidaMedicaDAO() {
		return mpmUnidadeMedidaMedicaDAO;
	}

	protected MpmListaServEquipeDAO getMpmListaServEquipeDAO() {
		return mpmListaServEquipeDAO;
	}

	protected MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}
	
	protected MpmItemPrescricaoNptDAO getMpmItemPrescricaoNptDAO(){
			
		return mpmItemPrescricaoNptDAO;
		
	}

	@Override
	public void atualizarIdade(final AltaSumarioVO altaSumarioVO)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().calcularIdade(altaSumarioVO);
	}

	@Override
	public void atualizarDiasPermanencia(final AltaSumarioVO altaSumarioVO)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().calcularDiasPermanencia(altaSumarioVO);
	}

	@Override
	public String obterDescricaoServicoOutrasEquipes(final Integer asuApaAtdSeq,
			final Integer asuApaSeq, final Short asuSeqp)
			throws ApplicationBusinessException {
		return this.getManterAltaOutraEquipeSumrON()
				.obterDescricaoServicoOutrasEquipes(asuApaAtdSeq, asuApaSeq,
						asuSeqp);
	}

	/**
	 * Atualiza os campos informados na alta sumário especificada pelo parâmetro
	 * id.
	 * 
	 * @param id
	 *            identificador da alta sumário.
	 * @param diasPermanencia
	 * @param idadeDias
	 * @param idadeMeses
	 * @param idadeAnos
	 * @param dataAlta
	 * @throws ApplicationBusinessException
	 */
	@Override
	
	public void atualizarAltaSumario(final MpmAltaSumarioId id,
			final Short diasPermanencia, final Integer idadeDias, final Integer idadeMeses,
			final Short idadeAnos, final Date dataAlta, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().atualizarAltaSumario(id, diasPermanencia,
				idadeDias, idadeMeses, idadeAnos, dataAlta, nomeMicrocomputador);
	}

	@Override	
	public void atualizarAltaSumario(final MpmAltaSumario altaSumario, String nomeMicrocomputador)
			throws BaseException {
		this.getManterAltaSumarioON().atualizarAltaSumario(altaSumario, nomeMicrocomputador);
	}

	@Override
	public void atualizarAltaSumarioViaRN(final MpmAltaSumario altaSumario, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioRN().atualizarAltaSumario(altaSumario, nomeMicrocomputador);
	}

	@Override
	public void verificarDataTRF(final Integer novoAtdSeq, final Date novoDtHrTransf)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioRN().verificarDataTRF(novoAtdSeq,
				novoDtHrTransf);
	}

	@Override
	@Secure("#{s:hasPermission('prescricaoMedica','pesquisar')}")
	public Date obterDataReferenciaProximaPrescricao(final AghAtendimentos atendimento)
			throws ApplicationBusinessException {
		return this.getVerificarPrescricaoON()
				.obterDataReferenciaProximaPrescricao(atendimento);
	}

	@Override
	public List<MpmAltaSumario> pesquisarAltaSumarios(Integer atdSeq) {
		return this.getMpmAltaSumarioDAO().pesquisarAltaSumarios(atdSeq);
	}
	
	@Override
	@Secure("#{s:hasPermission('prescricaoMedica','pesquisar')}")
	public MpmPrescricaoMedica obterUltimaPrescricaoAtendimento(final Integer atdSeq,
			final Date dtReferencia, final Date dataInicio)
			throws ApplicationBusinessException {
		return this.getVerificarPrescricaoON()
				.obterUltimaPrescricaoAtendimento(atdSeq, dtReferencia,
						dataInicio);
	}

	protected ItemPrescricaoMedicamentoRN getItemPrescricaoMedicamentoRN() {
		return itemPrescricaoMedicamentoRN;
	}

	@Override
	public ProcedimentoEspecialVO buscaPrescricaoProcedimentoEspecialVOPorId(
			final MpmPrescricaoProcedimentoId idPrescProc) {
		return this.getPrescreverProcedimentosEspeciaisON()
				.buscaPrescricaoProcedimentoEspecialVOPorId(idPrescProc);
	}

	@Override
	
	public void gravarPrescricaoProcedimentoEspecial(
			final MpmPrescricaoProcedimento prescProc,
			final List<ModoUsoProcedimentoEspecialVO> listaModoUsoParaExclusao,
			final DominioTipoProcedimentoEspecial tipo, String nomeMicrocomputador, Boolean formChanged) throws BaseException {
		
		this.getPrescreverProcedimentosEspeciaisON()
				.gravarPrescricaoProcedimentoEspecial(prescProc,
						listaModoUsoParaExclusao, tipo, nomeMicrocomputador, formChanged);
	}

	@Override
	public void verificarTipoModoUsoProcedimento(final Short novoTupSeqp,
			final Short novoTupPedSeq, final Short novaQuantidade)
			throws ApplicationBusinessException {
		getManterModoUsoPrescProcedRN().verificarTipoModoUsoProcedimento(
				novoTupSeqp, novoTupPedSeq, novaQuantidade);
	}

	private ManterModoUsoPrescProcedRN getManterModoUsoPrescProcedRN() {
		return manterModoUsoPrescProcedRN;
	}

	private LaudoON getLaudoON() {
		return laudoON;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void geraDadosSumarioPrescricao(final Integer seqAtendimento,
			final DominioTipoEmissaoSumario tipoEmissao)
			throws ApplicationBusinessException {
		this.getManterSumarioRN().geraDadosSumarioPrescricao(seqAtendimento,
				tipoEmissao);
	}

	private ManterSumarioRN getManterSumarioRN() {
		return manterSumarioRN;
	}

	@Override
	@BypassInactiveModule
	public void atualizarLaudo(final MpmLaudo laudoNew, final MpmLaudo laudoOld)
			throws BaseException {
		this.getLaudoON().atualizarLaudo(laudoNew, laudoOld);
	}
	
	@Override
	@BypassInactiveModule
	public void inserirLaudo(final MpmLaudo laudoNew) throws BaseException {
		this.getLaudoON().inserirLaudo(laudoNew);
	}
	

	private MpmLaudoDAO getMpmLaudoDAO() {
		return mpmLaudoDAO;
	}

	@Override
	public List<MpmLaudo> listarLaudosPorAtendimento(final Integer atdSeq) {
		return this.getMpmLaudoDAO().listarLaudosPorAtendimento(atdSeq);
	}
	
	protected MpmTextoPadraoLaudoDAO getMpmTextoPadraoLaudoDAO() {
		return mpmTextoPadraoLaudoDAO;
	}

	@Override
	public List<MpmTextoPadraoLaudo> listarTextoPadraoLaudosPorLaudo(
			final Integer laudoSeq) {
		return this.getMpmTextoPadraoLaudoDAO()
				.listarTextoPadraoLaudosPorLaudo(laudoSeq);
	}

	/**
	 * 
	 * Verifica se o paciente esta com previsão de alta nas próximas horas (48);
	 * Implementação da função  MPMC_PAC_PRV_ALTA_48H.
	 * @author andremachado - 09/03/2012
	 * @param aghAtendimentos
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public boolean verificaPrevisaoAltaProxima (final AghAtendimentos aghAtendimentos ) {
		return this.getPrescricaoMedicaRN().verificaPrevisaoAltaProxima(aghAtendimentos);
	}

	/**
	 * Faz uma pré validação do objeto antes mesmo da prescrição de dieta
	 * existir.
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void preValidar(final MpmItemPrescricaoDieta itemDieta,
			final List<ItemPrescricaoDietaVO> listaItens) throws BaseException {
		this.getManterPrescricaoDietaON().preValidar(itemDieta, listaItens);
	}

	protected ManterPrescricaoDietaON getManterPrescricaoDietaON() {
		return manterPrescricaoDietaON;
	}

	@Override
	public void excluirPrescricaoDieta(final MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputador)
			throws BaseException {
		
		this.getManterPrescricaoDietaON().excluirPrescricaoDieta(
				prescricaoDieta, nomeMicrocomputador);
	}

	@Override
	public void removerPrescricaoProcedimento(
			final MpmPrescricaoProcedimento prescricaoProcedimento) throws BaseException {
		this.getPrescreverProcedimentoEspecialRN()
				.removerPrescricaoProcedimento(prescricaoProcedimento);
	}

    @Override
    public void removerModoDeUso(
            List<ModoUsoProcedimentoEspecialVO> listaModoUsoParaExclusao) throws BaseException {
        this.getPrescreverProcedimentoEspecialRN()
                .removerModoDeUso(listaModoUsoParaExclusao);
    }

	@Override
	public void atualizarPrescricaoProcedimento(
			final MpmPrescricaoProcedimento prescricaoProcedimento, String nomeMicrocomputador)
			throws BaseException {
		this.getPrescreverProcedimentoEspecialRN()
				.atualizarPrescricaoProcedimento(prescricaoProcedimento, nomeMicrocomputador);
	}

	/**
	 * Retorna lista com todos os cuidados usuais ativos e que podem ser
	 * prescritos para a unidade onde o paciente está sendo atendido.
	 * 
	 * @param descricao
	 * @param unidade
	 * @return
	 */
	@Override
	public List<MpmCuidadoUsual> getListaCuidadosUsuaisAtivosUnidade(
			final Object descricao, final AghUnidadesFuncionais unidade) {
		return this.getManterPrescricaoCuidadoON().getListaCuidadosUsuaisAtivosUnidade(descricao, unidade);
	}

	@Override
	public List<MpmTipoRespostaConsultoria> listarTiposRespostasConsultoria(FiltroTipoRespostaConsultoriaVO filtro,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.mpmTipoRespostaConsultoriaDAO.listarTiposRespostasConsultoria(filtro, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long listarTiposRespostasConsultoriaCount(FiltroTipoRespostaConsultoriaVO filtro) {
		return this.mpmTipoRespostaConsultoriaDAO.listarTiposRespostasConsultoriaCount(filtro);
	}
	
	@Override
	public void inserirTipoRespostaConsultoria(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria)
			throws ApplicationBusinessException {
		this.mpmTipoRespostaConsultoriaRN.inserirTipoRespostaConsultoria(mpmTipoRespostaConsultoria);
	}
	
	@Override
	public void atualizarTipoRespostaConsultoria(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria)
			throws ApplicationBusinessException {
		this.mpmTipoRespostaConsultoriaRN.atualizarTipoRespostaConsultoria(mpmTipoRespostaConsultoria);
	}
	
	@Override
	public void excluirTipoRespostaConsultoria(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria)
			throws ApplicationBusinessException {
		this.mpmTipoRespostaConsultoriaRN.excluirTipoRespostaConsultoria(mpmTipoRespostaConsultoria);
	}
	
	@Override
	public List<MpmCuidadoUsual> listarCuidadosMedicos(Integer codigo, String descricao, Boolean indCci,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.mpmCuidadoUsualDAO.listarCuidadosMedicos(codigo, descricao, indCci,
				firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long listarCuidadosMedicosCount(Integer codigo, String descricao, Boolean indCci) {
		return this.mpmCuidadoUsualDAO.listarCuidadosMedicosCount(codigo, descricao, indCci);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCuidadosInteresseCCIH','alterar')}")
	public void atualizarMpmCuidadoUsual(MpmCuidadoUsual mpmCuidadoUsual) {
		this.mpmCuidadoUsualRN.atualizarMpmCuidadoUsual(mpmCuidadoUsual);
	}

	@Override
	@BypassInactiveModule
	public MpmAltaSumario obterAltaSumario(final MpmAltaSumarioId id) {
		return this.getMpmAltaSumarioDAO().obterPorChavePrimaria(id);
	}

	@Override
	@BypassInactiveModule
	public Integer obterNextValPleSeq(){
		return getMpmAltaSumarioDAO().getNextValPleSeq();
	}

	@Override
	public MpmAltaSumario pesquisarAltaSumarios(MpmAltaSumarioId id) {
		return getMpmAltaSumarioDAO().pesquisarAltaSumarios(id);
	}

	@Override
	public Long obterQtAltasSumario(final Integer atdSeq){
		return getMpmAltaSumarioDAO().obterQtAltasSumario(atdSeq);
	}
	
	private MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	/**
	 * Retorna o número de registros de cuidados usuais ativos e que podem ser
	 * prescritos para a unidade onde o paciente está sendo atendido.
	 * 
	 * @param descricao
	 * @param unidade
	 * @return
	 */
	@Override
	public Long getListaCuidadosUsuaisAtivosUnidadeCount(final Object descricao,
			final AghUnidadesFuncionais unidade) {
		return this.getManterPrescricaoCuidadoON()
				.getListaCuidadosUsuaisAtivosUnidadeCount(descricao, unidade);
	}

	@Override
	public List<MpmPrescricaoCuidado> obterListaCuidadosPrescritos(
			final MpmPrescricaoMedicaId prescricaoMedicaId, final Date dthrFim) {
		return this.getManterPrescricaoCuidadoON()
				.obterListaCuidadosPrescritos(prescricaoMedicaId, dthrFim);
	}

	@Override
	public MpmPrescricaoCuidado obterPrescricaoCuidado(final Integer atdSeq, final Long seq) {
		return this.getManterPrescricaoCuidadoON().obterCuidadoPrescritoPorId(
				new MpmPrescricaoCuidadoId(atdSeq, seq));
	}

	protected ManterPrescricaoCuidadoON getManterPrescricaoCuidadoON() {
		return manterPrescricaoCuidadoON;
	}

	@Override
	public MpmAltaSumario recuperarSumarioAlta(final Integer altanAtdSeq,
			final Integer altanApaSeq) throws ApplicationBusinessException {
		return this.getManterAltaSumarioON().recuperarSumarioAlta(altanAtdSeq,
				altanApaSeq);
	}

	@Override
	public MpmAltaSumario gerarAltaSumario(final Integer altanAtdSeq,
			final Integer altanApaSeq, final String altanListaOrigem)
			throws BaseException {
		return this.getManterAltaSumarioON().gerarAltaSumario(altanAtdSeq,
				altanApaSeq, altanListaOrigem);
	}

	@Override
	public MpmAltaSumario versionarAltaSumario(final MpmAltaSumario altaSumario,
			final String altanListaOrigem, String nomeMicrocomputador) throws BaseException {
		return this.getManterAltaSumarioON().versionarAltaSumario(altaSumario,
				altanListaOrigem, nomeMicrocomputador);
	}

	private ManterDiagnosticoAtendimentoON getManterDiagnosticoAtendimentoON() {
		return manterDiagnosticoAtendimentoON;
	}

	private ConsultaHistoricoDiagnosticoAtendimentoON getConsultaHistoricoDiagnosticoAtendimentoON() {
		return consultaHistoricoDiagnosticoAtendimentoON;
	}

	@Override
	public List<MpmCidAtendimento> buscarMpmCidsPorAtendimento(
			final AghAtendimentos atendimento) throws ApplicationBusinessException {
		return this.getManterDiagnosticoAtendimentoON()
				.buscarMpmCidsPorAtendimento(atendimento);
	}

	@Override
	public List<MpmCidAtendimento> buscarHistoricoMpmCidsPorAtendimento(
			final AghAtendimentos atendimento) throws ApplicationBusinessException {
		return this.getConsultaHistoricoDiagnosticoAtendimentoON()
				.buscarHistoricoMpmCidsPorAtendimento(atendimento);
	}

	@Override
	public boolean verificarEmergencia(final Integer altanAtdSeq,
			final Integer altanApaSeq, final Short seqp)
			throws ApplicationBusinessException {
		return this.getManterAltaSumarioON().verificarEmergencia(altanAtdSeq,
				altanApaSeq, seqp);
	}

	@Override
	public boolean inserirAltaDiagPrincipal(
			final SumarioAltaDiagnosticosCidVO altaDiagnosticosCidVO)
			throws BaseException {
		return this.getManterAltaSumarioON().inserirAltaDiagPrincipal(
				altaDiagnosticosCidVO);
	}

	@Override
	public void removerAltaDiagPrincipal(
			final SumarioAltaDiagnosticosCidVO altaDiagnosticosCidVO)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().removerAltaDiagPrincipal(
				altaDiagnosticosCidVO);
	}

	@Override
	public void gerarAltaOtrProcedimento(final MpmAltaSumario altaSumario)
			throws BaseException {
		this.getManterAltaOtrProcedimentoON().gerarAltaOtrProcedimento(
				altaSumario);
	}

	private ManterAltaOtrProcedimentoON getManterAltaOtrProcedimentoON() {
		return manterAltaOtrProcedimentoON;
	}

	@Override
	public void gerarAltaOutraEquipeSumr(final MpmAltaSumario altaSumario)
			throws BaseException {
		this.getManterAltaOutraEquipeSumrON().gerarAltaOutraEquipeSumr(
				altaSumario);
	}

	@Override
	public void inserirMpmCidAtendimento(final MpmCidAtendimento mpmCidAtendimento,
			final RapServidores servidor) throws ApplicationBusinessException {
		this.getManterDiagnosticoAtendimentoON().incluir(mpmCidAtendimento,
				servidor);
	}

	@Override
	public void atualizarMpmCidAtendimento(final MpmCidAtendimento mpmCidAtendimento,
			final RapServidores servidor) throws ApplicationBusinessException {
		this.getManterDiagnosticoAtendimentoON().alterar(mpmCidAtendimento,
				servidor);
	}

	@Override
	public void excluirMpmCidAtendimento(final MpmCidAtendimento mpmCidParaExcluir,
			final RapServidores servidor) throws ApplicationBusinessException {
		this.getManterDiagnosticoAtendimentoON().excluir(mpmCidParaExcluir,
				servidor);
	}

	@Override
	public void inserir(final MpmPrescricaoCuidado prescricaoCuidado, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		
		this.getManterPrescricaoCuidadoON().incluir(prescricaoCuidado, false, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void alterarPrescricaoCuidado(final MpmPrescricaoCuidado prescricaoCuidado, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		
		this.getManterPrescricaoCuidadoON().alterarPrescricaoCuidado(
				prescricaoCuidado, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void verificarRegrasNegocioAtualizacaoConsultoria(
			final MpmSolicitacaoConsultoria solicitacaoConsultoria)
			throws BaseException {

		this.getConsultoriaON()
				.verificarRegrasNegocioAtualizacaoConsultoria(solicitacaoConsultoria);
	}

	@Override
	public void excluirSolicitacaoConsultoria(
			final MpmSolicitacaoConsultoria solicitacaoConsultoria)
			throws BaseException {
		this.getConsultoriaON()
				.excluirSolicitacaoConsultoria(solicitacaoConsultoria);
	}

	@Override
	public void removerPrescricaoCuidado(final MpmPrescricaoCuidado prescricaoCuidado) throws ApplicationBusinessException {
		this.getManterPrescricaoCuidadoON().removerPrescricaoCuidado(
				prescricaoCuidado);
	}

	@Override
	@BypassInactiveModule
	public String buscaProcedimentoHospitalarInternoAgrupa(final Integer phiSeq,
			final Short cnvCodigo, final Byte cspSeq, final Short phoSeq)
			throws ApplicationBusinessException {
		return this.getPrescricaoMedicaON()
				.buscaProcedimentoHospitalarInternoAgrupa(phiSeq, cnvCodigo,
						cspSeq, phoSeq);
	}

	/**
	 * Obtém o número de vias de um relatório conforme a unidade funcional
	 * 
	 * @param prescricaoMedica
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public Byte obterNumeroDeViasRelatorio(final MpmPrescricaoMedica prescricaoMedica) {
		return this.getPrescricaoMedicaON().obterNumeroDeViasRelatorio(
				prescricaoMedica);
	}
	
	@Override
	@BypassInactiveModule
	public Long buscaDescricaoProcedimentoHospitalarInterno(final Integer phiSeq,
			final Short cnvCodigo, final Byte cspSeq, final Short phoSeq)
			throws ApplicationBusinessException {
		return this.getPrescricaoMedicaON()
				.buscaDescricaoProcedimentoHospitalarInterno(phiSeq, cnvCodigo,
						cspSeq, phoSeq);
	}

	@Override
	@BypassInactiveModule
	public String buscaJustificativaItemLaudo(final Integer atdSeq, final Integer phiSeq) {
		return this.getPrescricaoMedicaON().buscaJustificativaItemLaudo(atdSeq,
				phiSeq);
	}

	protected PrescricaoMedicaON getPrescricaoMedicaON() {
		return prescricaoMedicaON;
	}

	// métodos da estória #882 Prescrever Procedimentos Especiais
	protected PrescreverProcedimentoEspecialRN getPrescreverProcedimentoEspecialRN() {
		return prescreverProcedimentoEspecialRN;
	}

	private ManterAltaCirgRealizadaON getManterAltaCirgRealizadaON() {
		return manterAltaCirgRealizadaON;
	}

	@Override
	public void gerarAltaCirgRealizada(final MpmAltaSumario altaSumario,
			final Integer pacCodigo) throws BaseException {
		this.getManterAltaCirgRealizadaON().gerarAltaCirgRealizada(altaSumario,
				pacCodigo);
	}

	private ManterAltaConsultoriaON getManterAltaConsultoriaON() {
		return manterAltaConsultoriaON;
	}

	@Override
	public void gerarAltaConsultoria(final MpmAltaSumario altaSumario)
			throws BaseException {
		this.getManterAltaConsultoriaON().gerarAltaConsultoria(altaSumario);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoFormatadaDietaRelatorioItensConfirmados(
			final MpmPrescricaoDieta dietaConfirmada, final Boolean inclusaoExclusao,
			final Boolean impressaoTotal) {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDescricaoFormatadaDieta(dietaConfirmada,
						inclusaoExclusao, impressaoTotal);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoAlteracaoDietaRelatorioItensConfirmados(
			final MpmPrescricaoDieta dieta) {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDescricaoAlteracaoDieta(dieta);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoFormatadaCuidadoRelatorioItensConfirmados(final Integer atdSeq, final Long seqCuidado) {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON().obterDescricaoFormatadaCuidado(atdSeq, seqCuidado);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoAlteracaoCuidadoRelatorioItensConfirmados(
			final MpmPrescricaoCuidado cuidado) {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDescricaoAlteracaoCuidado(cuidado);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoFormatadaMedicamentoSolucaoRelatorioItensConfirmados(
			final MpmPrescricaoMdto medicamentoConfirmada, final Boolean inclusaoExclusao,
			final Boolean impressaoTotal, final Boolean isUpperCase, final Boolean incluirCodigoMedicamentos) throws ApplicationBusinessException {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDescricaoFormatadaMedicamentoSolucao(
						medicamentoConfirmada, inclusaoExclusao,
						impressaoTotal, isUpperCase, incluirCodigoMedicamentos);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoAlteracaoMedicamentoSolucaoRelatorioItensConfirmados(
			final MpmPrescricaoMdto medicamentoConfirmada, final Boolean isUpperCase) throws ApplicationBusinessException {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDescricaoAlteracaoMedicamentoSolucao(
						medicamentoConfirmada, isUpperCase);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoFormatadaConsultoriaRelatorioItensConfirmados(final Integer atdSeq, Integer seq) {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON().obterDescricaoFormatadaConsultoria(atdSeq, seq);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoAlteracaoConsultoriaRelatorioItensConfirmados(final Integer atdSeq, final Integer seq) {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON().obterDescricaoAlteracaoConsultoria(atdSeq, seq);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoFormatadaSolicitacaoHemoterapicasRelatorioItensConfirmados(Integer atdSeq, Integer seq, Boolean impressaoTotal, Boolean inclusaoExclusao) {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDecricaoformatadaSolicitacoesHemoterapicas(atdSeq, seq, impressaoTotal, inclusaoExclusao);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoAlteracaoSolicitacaoHemoterapicasRelatorioItensConfirmados(Integer atdSeq, Integer seq, Boolean impressaoTotal) {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDecricaoAlteracaoSolicitacoesHemoterapicas(atdSeq, seq, impressaoTotal);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoFormatadaProcedimentoRelatorioItensConfirmados(final Integer atdSeq, final Long seq, Boolean impressaoTotal, Boolean inclusaoExclusao) {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON().obterDecricaoFormatadaProcedimento(atdSeq, seq, impressaoTotal, inclusaoExclusao);
	}

	@Override
	@BypassInactiveModule
	public String obterDescricaoAlteracaoProcedimentoRelatorioItensConfirmados(final Integer atdSeq, final Long seq) {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON().obterDecricaoAlteracaoProcedimento(atdSeq, seq);
	}

	/**
	 * Retorna a prescrição médica pelo id. * @param atdSeq
	 * 
	 * @param seq
	 * @return
	 */
	@Override
	public MpmPrescricaoMedica obterPrescricaoPorId(final Integer atdSeq, final Integer seq) {
		return this.getPrescricaoMedicaON().obterPrescricaoPorId(atdSeq, seq);
	}

	protected MpmPrescricaoProcedimentoDAO getMpmPrescricaoProcedimentoDAO() {
		return mpmPrescricaoProcedimentoDAO;
	}

	@Override
	@BypassInactiveModule
	public MpmPrescricaoProcedimento obterProcedimentoPorId(final Integer atdSeq,
			final Long seq) {
		return this.getMpmPrescricaoProcedimentoDAO().obterProcedimentoPeloId(
				atdSeq, seq);
	}

	@Override
	public Integer recuperarAtendimentoPaciente(final Integer altanAtdSeq)
			throws ApplicationBusinessException {
		return this.getListaPacientesInternadosON()
				.recuperarAtendimentoPaciente(altanAtdSeq);
	}

	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void desbloquearAlta(final Integer atdSeq, String nomeMicrocomputador) throws ApplicationBusinessException,
			Exception {
		getManterPrescricaoMedicaON().desbloquearAlta(atdSeq, true, nomeMicrocomputador);
	}

	@Override
	@BypassInactiveModule
	public List<RelatorioLaudosProcSusVO> pesquisaLaudoProcedimentoSus(
			final Integer seqAtendimento, final Integer apaSeq, final Short seqp)
			throws ApplicationBusinessException {
		return this.getLaudoProcedimentoSusON().pesquisaLaudoProcedimentoSus(seqAtendimento, apaSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public List<RelatorioLaudosProcSusVO> pesquisaLaudoProcedimentoSus(
			final Integer seqAtendimento, final Integer apaSeq, final Short seqp, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		return this.getLaudoProcedimentoSusON().pesquisaLaudoProcedimentoSus(seqAtendimento, apaSeq, seqp, servidorLogado);
	}

	@Override
	public List<RelSolHemoterapicaVO> pesquisarHemoterapiasRelatorio(
			final MpmPrescricaoMedica prescricaoMedica,
			final EnumTipoImpressao tipoImpressao, RapServidores servidorValida, Date dataMovimento) throws BaseException {
		return this.getSolicitacaoHemoterapicaON()
				.pesquisarRelSolHemoterapicaVOs(prescricaoMedica, tipoImpressao, servidorValida, dataMovimento);
	}

	protected LaudoProcedimentoSusON getLaudoProcedimentoSusON() {
		return laudoProcedimentoSusON;
	}

	@Override
	public MpmPrescricaoDieta obterPrescricaoDieta(final MpmPrescricaoDietaId id) {
		return this.getManterPrescricaoDietaON().obterPrescricaoDieta(id);
	}

	@Override
	public MpmPrescricaoDieta obterPosterior(final MpmPrescricaoDieta dieta) {
		return this.getManterPrescricaoDietaON().obterPosterior(dieta);
	}

	/**
	 * Grava alterações na prescrição de dieta criando, alterando ou excluido
	 * itens associados.
	 * 
	 * @see ManterPrescricaoDietaON#gravar(MpmPrescricaoDieta, List, List, List)
	 * @param dieta
	 * @param novos
	 * @param alterados
	 * @param excluidos
	 * @throws CloneNotSupportedException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 */
	@Override
	public void gravar(final MpmPrescricaoDieta dieta,
			final List<MpmItemPrescricaoDieta> novos,
			final List<MpmItemPrescricaoDieta> alterados,
			final List<MpmItemPrescricaoDieta> excluidos, String nomeMicrocomputador) throws BaseException,
			CloneNotSupportedException {
		
		this.getManterPrescricaoDietaON().gravar(dieta, novos, alterados, excluidos, nomeMicrocomputador);
	}

	/**
	 * Grava inclusão de prescrição de dieta criando itens associados.
	 * 
	 * @see ManterPrescricaoDietaON#gravar(MpmPrescricaoDieta, List)
	 * @param dieta
	 * @param novos
	 */
	@Override
	public void gravar(final MpmPrescricaoDieta dieta,final List<MpmItemPrescricaoDieta> novos, String nomeMicrocomputador) throws BaseException {
		this.getManterPrescricaoDietaON().gravar(dieta, novos, nomeMicrocomputador);
	}

	
	@Override
	public void inserir(final MpmPrescricaoDieta dieta,final List<MpmItemPrescricaoDieta> itensDieta) throws BaseException {
		this.getManterPrescricaoDietaON().inserir(dieta, itensDieta);
	}
	

	
	@Override
	public void removerPrescricaoMedicamento(
			final MpmPrescricaoMedica prescricaoMedica,final MpmPrescricaoMdto prescicaoMedicamento,
			String nomeMicrocomputador, MpmPrescricaoMdto prescricaoMedicamentoOriginal) throws BaseException {
		
		this.getManterPrescricaoMedicamentoON().removerPrescricaoMedicamento(prescricaoMedica, prescicaoMedicamento, nomeMicrocomputador,prescricaoMedicamentoOriginal);
	}

	@Override
	public Boolean isPrescricaoVigente(final Date dthrInicio, final Date dthrFim)
			throws ApplicationBusinessException {
		return this.getManterPrescricaoMedicamentoON().isPrescricaoVigente(dthrInicio, dthrFim);
	}

	@Override
	public boolean isPrescricaoVigente(final MpmPrescricaoMedica prescricaoMedica) {
		return this.getPrescricaoMedicaON().isPrescricaoVigente(prescricaoMedica);
	}

	@Override
	public void validarInserirDieta(final MpmPrescricaoDieta prescricaoDieta)
			throws ApplicationBusinessException {
		this.getManterPrescricaoDietaON().valida(prescricaoDieta);
	}

	@Override
	public void verificaPrescricaoMedica(final Integer seqAtendimento,
			final Date dataHoraInicio, final Date dataHoraFim,
			final Date dataHoraMovimentoPendente,
			final DominioIndPendenteItemPrescricao pendente, final String operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		
		this.getPrescricaoMedicaON().verificaPrescricaoMedica(seqAtendimento,
				dataHoraInicio, dataHoraFim, dataHoraMovimentoPendente,
				pendente, operacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public List<SumarioAltaDiagnosticosCidVO> pesquisarMotivosInternacaoCombo(
			final MpmAltaSumarioId id) {
		return this.getManterAltaSumarioON()
				.pesquisarMotivosInternacaoCombo(id);
	}

	private MpmAltaDiagMtvoInternacaoDAO getMpmAltaDiagMtvoInternacaoDAO() {
		return mpmAltaDiagMtvoInternacaoDAO;
	}

	private MpmAltaDiagPrincipalDAO getMpmAltaDiagPrincipalDAO() {
		return mpmAltaDiagPrincipalDAO;
	}

	private MpmAltaDiagSecundarioDAO getMpmAltaDiagSecundarioDAO() {
		return mpmAltaDiagSecundarioDAO;
	}

	private ManterObtCausaDiretaON getManterObtCausaDiretaON() {
		return manterObtCausaDiretaON;
	}

	@Override
	public List<MpmAltaDiagMtvoInternacao> obterMpmAltaDiagMtvoInternacao(
			final Integer altanAtdSeq, final Integer altanApaSeq, final Short altanAsuSeqp)
			throws ApplicationBusinessException {
		return this.getMpmAltaDiagMtvoInternacaoDAO()
				.obterMpmAltaDiagMtvoInternacao(altanAtdSeq, altanApaSeq,altanAsuSeqp);
	}
	
	@Override
	public MpmMotivoAltaMedica obterMpmMotivoAltaMedica(Short seq){
		return mpmMotivoAltaMedicaDAO.obterMotivoAltaMedicaPeloId(seq);
	}
	
	@Override
	public List<MpmPostoSaude> listarMpmPostoSaudePorSeqDescricao(final Object parametro) {
		return mpmPostoSaudeDAO.listarMpmPostoSaudePorSeqDescricao(parametro);
	}
	
	@Override
	public Long listarMpmPostoSaudePorSeqDescricaoCount(final Object parametro) {
		return mpmPostoSaudeDAO.listarMpmPostoSaudePorSeqDescricaoCount(parametro);
	}
	
	@Override
	public MpmPostoSaude obterPostoSaudePorChavePrimaria(Integer seq) {
		return mpmPostoSaudeDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public MpmAltaDiagPrincipal obterAltaDiagPrincipal(final Integer altanAtdSeq,
			final Integer altanApaSeq, final Short altanAsuSeqp)
			throws ApplicationBusinessException {
		return this.getMpmAltaDiagPrincipalDAO().obterAltaDiagPrincipal(altanAtdSeq, altanApaSeq, altanAsuSeqp);
	}

	@Override
	public List<MpmAltaDiagSecundario> obterAltaDiagSecundario(
			final Integer altanAtdSeq, final Integer altanApaSeq, final Short altanAsuSeqp)
			throws ApplicationBusinessException {
		return this.getMpmAltaDiagSecundarioDAO().obterAltaDiagSecundario(altanAtdSeq, altanApaSeq, altanAsuSeqp);
	}

	@Override
	public void inserirAltaDiagMtvoInternacao(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException {

		this.getManterAltaSumarioON().preinserirAltaDiagMtvoInternacao(
				sumarioAltaDiagnosticosCidVO);

	}

	@Override
	public MpmObtCausaDireta obterObtCausaDireta(final Integer altanAtdSeq,
			final Integer altanApaSeq, final Short altanAsuSeqp) throws BaseException {
		return getManterObtCausaDiretaON().obterObtCausaDireta(altanAtdSeq,
				altanApaSeq, altanAsuSeqp);
	}

	@Override
	public String inserirObtCausaDireta(
			final SumarioAltaDiagnosticosCidVO cidsCausaDiretaVO)
			throws ApplicationBusinessException {
		return this.getManterObtCausaDiretaON().preinserirObtCausaDireta(cidsCausaDiretaVO);
	}

	@Override
	public void removerObtCausaDireta(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException {
		this.getManterObtCausaDiretaON().removerObtCausaDireta(sumarioAltaDiagnosticosCidVO);
	}

	@Override
	public void removerObtCausaDireta(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException {
		this.getManterObtCausaDiretaON().removerObtCausaDireta(altaSumario);
	}

	@Override
	public void removerObitoNecropsia(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException {
		getManterObitoNecropsiaON().removerObitoNecropsia(altaSumario);
	}

	@Override
	public MpmObitoNecropsia obterMpmObitoNecropsia(final MpmAltaSumario altaSumario) {
		return getManterObitoNecropsiaON().obterMpmObitoNecropsia(altaSumario);
	}

	@Override
	public String gravarObitoNecropsia(final MpmAltaSumario altaSumario,
			final DominioSimNao dominioSimNao) throws BaseException {
		return getManterObitoNecropsiaON().gravarObitoNecropsia(altaSumario,
				dominioSimNao);
	}

	@Override
	public void atualizarMpmAltaDiagMtvoInternacao(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().preatualizarMpmAltaDiagMtvoInternacao(
				sumarioAltaDiagnosticosCidVO);
	}

	@Override
	public void atualizarObtCausaDireta(
			final SumarioAltaDiagnosticosCidVO cidsCausaDiretaVO)
			throws ApplicationBusinessException {
		this.getManterObtCausaDiretaON().preatualizarObtCausaDireta(
				cidsCausaDiretaVO);
	}

	@Override
	public void verificaPrescricaoMedicaUpdate(final Integer seqAtendimento,
			final Date dataHoraInicio, final Date dataHoraFim,
			final Date dataHoraMovimentoPendente,
			final DominioIndPendenteItemPrescricao pendente, final String operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		
		this.getPrescricaoMedicaON().verificaPrescricaoMedicaUpdate(
				seqAtendimento, dataHoraInicio, dataHoraFim,
				dataHoraMovimentoPendente, pendente, operacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void removerAltaDiagMtvoInternacao(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().removerAltaDiagMtvoInternacao(
				sumarioAltaDiagnosticosCidVO);
	}

	@Override
	public void removerAltaDiagSecundario(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().removerAltaDiagSecundario(
				sumarioAltaDiagnosticosCidVO);
	}

	@Override
	public void inserirAltaDiagSecundario(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().preinserirAltaDiagSecundario(
				sumarioAltaDiagnosticosCidVO);
	}

	@Override
	public void atualizarAltaDiagSecundario(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().preAtualizarAltaDiagSecundario(
				sumarioAltaDiagnosticosCidVO);
	}

	@Override
	public List<CompSanguineoProcedHemoterapicoVO> pesquisarCompSanguineoProcedHemoterapico()
			throws ApplicationBusinessException {
		return this.getSolicitacaoHemoterapicaON()
				.pesquisarCompSanguineoProcedHemoterapico();
	}

	/**
	 * Recebe um MAP contendo uma lista de justificativasVO para cada grupo de
	 * justificativas, onde o Id do grupo é chave no MAP.
	 * 
	 * @param justificativasSelecionadas
	 */
	@Override
	public List<AbsItemSolicitacaoHemoterapicaJustificativa> gravarItemSolicitacaoHemoterapicaJustificativa(
			final Map<Short, List<JustificativaComponenteSanguineoVO>> justificativasSelecionadas)
			throws ApplicationBusinessException {
		return this.getSolicitacaoHemoterapicaON()
				.gravarItemSolicitacaoHemoterapicaJustificativa(
						justificativasSelecionadas);
	}

	/**
	 * Retorna os itens da prescrição dieta fornecida.
	 * 
	 * @param dieta
	 * @return
	 */
	@Override
	public Set<MpmItemPrescricaoDieta> obterItensPrescricaoDieta(
			final MpmPrescricaoDieta dieta) {
		return this.getManterPrescricaoDietaON().obterItensPrescricaoDieta(
				dieta);
	}

	@Override
	public SumarioAltaProcedimentosCrgListasVO pesquisarCirurgiasRealizadas(
			final MpmAltaSumarioId id) throws ApplicationBusinessException{
		SumarioAltaProcedimentosCrgListasVO listas = new SumarioAltaProcedimentosCrgListasVO();
		listas.setListaComboCirurgias(manterAltaSumarioON.pesquisarCirurgiasRelizadasCombo(
				id));
		listas.setListaGridCirurgias(manterAltaSumarioON.pesquisarCirurgiasRelizadasGrid(id));
		listas.getListaComboCirurgias().removeAll(listas.getListaGridCirurgias());
		return listas;
	}
	
	@Override
	public List<SumarioAltaProcedimentosCrgVO> pesquisarCirurgiasRelizadasCombo(
			final MpmAltaSumarioId id) {
		return this.getManterAltaSumarioON().pesquisarCirurgiasRelizadasCombo(
				id);
	}

	@Override
	public List<SumarioAltaProcedimentosCrgVO> pesquisarCirurgiasRelizadasGrid(
			final MpmAltaSumarioId id) throws ApplicationBusinessException {
		
		return manterAltaSumarioON.pesquisarCirurgiasRelizadasGrid(id);
	}

	@Override
	public List<SumarioAltaProcedimentosConsultoriasVO> pesquisarConsultoriasCombo(
			final MpmAltaSumarioId id) {
		return this.getManterAltaSumarioON().pesquisarConsultoriasCombo(id);
	}

	@Override
	public List<SumarioAltaProcedimentosConsultoriasVO> pesquisarConsultoriasGrid(
			final MpmAltaSumarioId id) throws ApplicationBusinessException {
		return this.getManterAltaSumarioON().pesquisarConsultoriasGrid(id);
	}

	@Override
	public List<SumarioAltaPrescricaoProcedimentoVO> pesquisarPrescricaoProcedimentoCombo(
			final MpmAltaSumarioId id) {
		return this.getManterAltaSumarioON().pesquisarPrescricaoProcedimentoCombo(id);
	}

	@Override
	public List<SumarioAltaPrescricaoProcedimentoVO> pesquisarPrescricaoProcedimentoGrid(
			final MpmAltaSumarioId id) throws ApplicationBusinessException {
		return this.getManterAltaSumarioON().pesquisarPrescricaoProcedimentoGrid(id);
	}

	@Override
	public void populaListaComboPrescricaoProcedimento(
			final List<SumarioAltaPrescricaoProcedimentoVO> listaComboPrescricaoProcedimentos,
			final MpmAltaSumarioId id) {
		this.getManterAltaSumarioON().populaListaComboPrescricaoProcedimento(
				listaComboPrescricaoProcedimentos, id);
	}

	@Override
	public List<SumarioAltaProcedimentosVO> pesquisarPrescricaoOutrosProcedimentoGrid(
			final MpmAltaSumarioId id) throws ApplicationBusinessException {
		return this.getManterAltaSumarioON()
				.pesquisarPrescricaoOutrosProcedimentoGrid(id);
	}

	@Override
	public void inserirAltaCirgRealizada(final SumarioAltaProcedimentosCrgVO vo,
			final Date dthrCirurgia) throws BaseException {
		this.getManterAltaCirgRealizadaON().inserirAltaCirgRealizada(vo,dthrCirurgia);
	}

	@Override
	public void atualizarAltaCirgRealizada(final SumarioAltaProcedimentosCrgVO vo,
			final Date dthrCirurgia) throws BaseException {
		this.getManterAltaCirgRealizadaON().atualizarAltaCirgRealizada(vo,dthrCirurgia);
	}

	@Override
	public void removerAltaCirgRealizada(final SumarioAltaProcedimentosCrgVO vo)
			throws ApplicationBusinessException {
		this.getManterAltaCirgRealizadaON().removerAltaCirgRealizada(vo);
	}

	@Override
	public void inserirAltaOtrProcedimento(
			final SumarioAltaPrescricaoProcedimentoVO vo, final Date dataProcedimento)
			throws BaseException {
		this.getManterAltaOtrProcedimentoON().inserirAltaOtrProcedimento(vo,dataProcedimento);
	}

	@Override
	public void atualizarAltaOtrProcedimento(
			final SumarioAltaPrescricaoProcedimentoVO vo, final Date dataProcedimento)
			throws BaseException {
		this.getManterAltaOtrProcedimentoON().atualizarAltaOtrProcedimento(vo,dataProcedimento);
	}

	@Override
	public void removerAltaOtrProcedimento(
			final SumarioAltaPrescricaoProcedimentoVO vo)
			throws ApplicationBusinessException {
		this.getManterAltaOtrProcedimentoON().removerAltaOtrProcedimento(vo);
	}

	@Override
	public void inserirAltaOtrProcedimento(final SumarioAltaProcedimentosVO vo)
			throws BaseException {
		this.getManterAltaOtrProcedimentoON().inserirAltaOtrProcedimento(vo);
	}

	@Override
	public void atualizarAltaOtrProcedimento(final SumarioAltaProcedimentosVO vo,
			final Date dataProcedimento, final String complemento) throws BaseException {
		this.getManterAltaOtrProcedimentoON().atualizarAltaOtrProcedimento(vo,dataProcedimento, complemento);
	}

	@Override
	public void removerAltaOtrProcedimento(final SumarioAltaProcedimentosVO vo)
			throws ApplicationBusinessException {
		this.getManterAltaOtrProcedimentoON().removerAltaOtrProcedimento(vo);
	}

	@Override
	public void inserirAltaConsultoria(
			final SumarioAltaProcedimentosConsultoriasVO itemEdicao,
			final Date dataConsultoria) throws BaseException {
		this.getManterAltaConsultoriaON().inserirAltaConsultoria(itemEdicao,dataConsultoria);
	}

	@Override
	public void atualizarAltaConsultoria(
			final SumarioAltaProcedimentosConsultoriasVO itemEdicao,
			final Date dataConsultoria) throws BaseException {
		this.getManterAltaConsultoriaON().alterarAltaConsultoria(itemEdicao,dataConsultoria);
	}

	@Override
	public void removerAltaConsultoria(
			final SumarioAltaProcedimentosConsultoriasVO itemEdicao)
			throws ApplicationBusinessException {
		this.getManterAltaConsultoriaON().removerAltaConsultoria(itemEdicao);
	}

	@Override
	public List<AghEspecialidades> listarEspecialidades(final Object objPesquisa) {
		return getPrescricaoMedicaRN().getEspecialidades(objPesquisa);
	}
	
	@Override
	public Long listarEspecialidadesCount(final Object objPesquisa) {
		return getPrescricaoMedicaRN().getEspecialidadesCount(objPesquisa);
	}	

	@Override
	public List<AghEspecialidades> listarEspecialidadesAtivas(final Object objPesquisa) {
		return getPrescricaoMedicaRN().getEspecialidadesAtivas(objPesquisa);
	}

	@Override
	public List<AghEspecialidades> listarEspecialidadesPorServidor(final RapServidores servidor) {
		return getPrescricaoMedicaRN().getEspecialidadesPorServidor(servidor);
	}

	@Override
	public List<AghProfEspecialidades> listarConsultoresPorEspecialidade(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final AghEspecialidades especialidade) {
		return getPrescricaoMedicaRN().getConsultoresPorEspecialidade(
				firstResult, maxResult, orderProperty, asc, especialidade);
	}
	
	@Override
	public Long listarConsultoresPorEspecialidadeCount(final AghEspecialidades especialidade) {
		return getPrescricaoMedicaRN().getConsultoresPorEspecialidadeCount(especialidade);
	}

	private VMpmOtrProcedSumDAO getVMpmOtrProcedSumDAO() {
		return vMpmOtrProcedSumDAO;
	}

	@Override
	public List<VMpmOtrProcedSum> listarVMpmOtrProcedSum(final String objPesquisa) {
		return this.getVMpmOtrProcedSumDAO().pesquisarVMpmOtrProcedSum(
				objPesquisa);
	}
	
	@Override
	public Long listarVMpmOtrProcedSumCount(final String objPesquisa) {
		return this.getVMpmOtrProcedSumDAO().pesquisarVMpmOtrProcedSumCount(
				objPesquisa);
	}
	
	@Override
	public VMpmOtrProcedSum obterVMpmOtrProcedSum(final Integer matCodigo,
			final Integer pciSeq, final Short pedSeq) {
		return this.getVMpmOtrProcedSumDAO().obterVMpmOtrProcedSum(matCodigo,
				pciSeq, pedSeq);
	}

	private ManterAltaPrincFarmacoON getManterAltaPrincFarmacoON() {
		return manterAltaPrincFarmacoON;
	}

	@Override
	public void inserirMpmAltaPrincFarmaco(final AfaMedicamentoPrescricaoVO vo)
			throws ApplicationBusinessException {
		this.getManterAltaPrincFarmacoON().inserirMpmAltaPrincFarmaco(vo);
	}

	@Override
	public void removerMpmAltaPrincFarmaco(final AfaMedicamentoPrescricaoVO vo)
			throws ApplicationBusinessException {
		this.getManterAltaPrincFarmacoON().removerMpmAltaPrincFarmaco(vo);
	}
	
	@BypassInactiveModule
	public void removerMpmPim2(final MpmPim2 pim2) throws ApplicationBusinessException {
		getPim2RN().removerPim2(pim2);
	}
	
	@BypassInactiveModule
	public void removerMamPcIntItemParada(final MamPcIntItemParada itemParada){
		getMamPcIntItemParadaDAO().remover(itemParada);
	}
	
	@BypassInactiveModule
	public void removerMamPcIntParada(final MamPcIntParada pcIntParada){
		getMamPcIntParadaDAO().remover(pcIntParada);
	}

	@Override
	public void inserirPrescricaoDieta(final MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputador)
			throws BaseException {
		
		this.getManterPrescricaoDietaRN().inserirPrescricaoDieta(
				prescricaoDieta, false, nomeMicrocomputador);
	}

	protected ManterPrescricaoDietaRN getManterPrescricaoDietaRN() {
		return manterPrescricaoDietaRN;
	}

	@Override
	@BypassInactiveModule
	public List<String> gerarAprazamento(final Integer atdSeq, final Integer prescSeq,
			final Date dthrInicioItem, final Date dthrFimItem,
			final Short seqTipoFrequenciaAprazamento,
			final TipoItemAprazamento tipoItem, final Date dtHrInicioTratamento,
			final Boolean indNecessario, final Short frequencia) {
		
		return this.getAprazamentoRN().gerarAprazamento(atdSeq, prescSeq,
				dthrInicioItem, dthrFimItem, seqTipoFrequenciaAprazamento,
				tipoItem, dtHrInicioTratamento, indNecessario, frequencia);
	}

	@Override
	public List<String> buscaResultadoExames(final AipPacientes paciente,
			final String codigoComponenteSanguineo,
			final String codigoProcedimentoHemoterapico) throws ApplicationBusinessException {
		return this.getPrescricaoMedicaON().buscaResultadoExames(paciente,
				codigoComponenteSanguineo, codigoProcedimentoHemoterapico);
	}

	@Override
	@BypassInactiveModule
	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			final Integer matricula, final Short vinculo) throws ApplicationBusinessException {
		return this.getLaudoProcedimentoSusRN()
				.buscaConselhoProfissionalServidorVO(matricula, vinculo);

	}
	
	@Override
	@BypassInactiveModule
	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			final Integer matricula, final Short vinculo, Boolean testaDataFimVinculo) throws ApplicationBusinessException {
		return this.getLaudoProcedimentoSusRN()
				.buscaConselhoProfissionalServidorVO(matricula, vinculo, testaDataFimVinculo);

	}

	private LaudoProcedimentoSusRN getLaudoProcedimentoSusRN() {
		return laudoProcedimentoSusRN;
	}

	private AprazamentoRN getAprazamentoRN() {
		return aprazamentoRN;
	}

	protected VMpmMdtoPrescNewDAO getVMpmMdtoPrescNewDAO() {
		return vMpmMdtoPrescNewDAO;
	}

	protected MpmAltaPrincFarmacoDAO getMpmAltaPrincFarmacoDAO() {
		return mpmAltaPrincFarmacoDAO;
	}

	private DescricaoFormatadaRelatorioItensConfirmadosON getDescricaoFormatadaRelatorioItensConfirmadosON() {
		return descricaoFormatadaRelatorioItensConfirmadosON;
	}

	private ManterSumarioAltaPosAltaON getManterSumarioAltaPosAltaON() {
		return manterSumarioAltaPosAltaON;
	}

	/**
	 * Busca em MPM_MOTIVO_ALTA_MEDICAS.<br>
	 * Entidade: <code>MpmMotivoAltaMedica</code><br>
	 * Executa a regra pra nao retornar os itens jah associados ao Sumario de
	 * Alta.
	 * 
	 * @param altaSumario
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public List<SumarioAltaPosAltaMotivoVO> listaMotivoAltaMedica(
			final MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		return this.getManterSumarioAltaPosAltaON().listaMotivoAltaMedica(
				altaSumario);
	}

	@Override
	public List<SumarioAltaPosAltaMotivoVO> listaPlanoPosAlta(
			final MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		return this.getManterSumarioAltaPosAltaON().listaPlanoPosAlta(
				altaSumario);
	}

	@Override
	public List<SumarioAltaPosAltaMotivoVO> buscaAltaMotivo(
			final MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		return this.getManterSumarioAltaPosAltaON()
				.buscaAltaMotivo(altaSumario);
	}

	@Override
	public List<SumarioAltaPosAltaMotivoVO> buscaAltaPlano(
			final MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		return this.getManterSumarioAltaPosAltaON().buscaAltaPlano(altaSumario);
	}

	/**
	 * Grava ou altera as informações de <code> MpmAltaMotivo </code>
	 * 
	 * @param umMpmAltaMotivo
	 * @throws BaseException
	 */
	@Override
	public void gravarAltaMotivo(final MpmAltaMotivo umMpmAltaMotivo)
			throws BaseException {
		this.getManterSumarioAltaPosAltaON().gravarAltaMotivo(umMpmAltaMotivo);
	}

	/**
	 * Exclui um <code> MpmAltaMotivo </code> no banco.
	 * 
	 * @param vo
	 */
	@Override
	public void removerAltaMotivo(final MpmAltaMotivo umMpmAltaMotivo)
			throws BaseException {
		this.getManterSumarioAltaPosAltaON().removerAltaMotivo(umMpmAltaMotivo);
	}

	@Override
	public void gravarAltaPlano(final MpmAltaPlano umMpmAltaPlano)
			throws BaseException {
		this.getManterSumarioAltaPosAltaON().gravarAltaPlano(umMpmAltaPlano);
	}

	@Override
	public void removerAltaPlano(final MpmAltaPlano umMpmAltaPlano)
			throws BaseException {
		this.getManterSumarioAltaPosAltaON().removerAltaPlano(umMpmAltaPlano);
	}

	@Override
	@BypassInactiveModule
	public Boolean verificarPrimeiraImpressao(RapServidores servidorValida) {
		return this.getPrescricaoMedicaON().verificarPrimeiraImpressao(servidorValida);
	}

	/**
	 * Método responsável por retornar uma lista de informação complementar.
	 * 
	 * @author gfmenezes
	 * 
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */

	@Override
	public List<AltaSumarioInfoComplVO> findListaInformacaoComplementar(
			final MpmAltaSumarioId id) throws ApplicationBusinessException {
		return this.getManterAltaSumarioON()
				.findListaInformacaoComplementar(id);
	}

	@Override
	@BypassInactiveModule
	public EnumStatusItem buscarStatusItem(final ItemPrescricaoMedica item,  Date dataMovimento)
			throws ApplicationBusinessException {
		return this.getPrescricaoMedicaON().buscarStatusItem(item, dataMovimento);
	}

	/**
	 * Método responsável pela adição de uma informação complementar.
	 * 
	 * @author gfmenezes
	 * 
	 * @param informacaoComplementarVO
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void gravarAltaSumarioInformacaoComplementar(final String complemento,
			final AltaSumarioInfoComplVO informacaoComplementarVO)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().gravarAltaSumarioInformacaoComplementar(
				complemento, informacaoComplementarVO);
	}

	/**
	 * Método responsável por excluir uma informação complementar.
	 * 
	 * @author gfmenezes
	 * 
	 * @param informacaoComplementarVO
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void removerAltaSumarioInformacaoComplementar(
			final AltaSumarioInfoComplVO informacaoComplementarVO)
			throws ApplicationBusinessException {
		this.getManterAltaSumarioON().removerAltaSumarioInformacaoComplementar(
				informacaoComplementarVO);
	}

	@Override
	public List<VAelExamesSolicitacao> obterNomeExames(final Object objPesquisa) {
		return this.getManterAltaItemPedidoExameON().obterNomeExames(
				objPesquisa);
	}

	@Override
	public AelUnfExecutaExames buscarAelUnfExecutaExamesPorID(final Integer manSeq,
			final String sigla, final Integer unfSeq) {
		return this.getManterAltaItemPedidoExameON()
				.buscarAelUnfExecutaExamesPorID(manSeq, sigla, unfSeq);
	}

	@Override
	public AelMateriaisAnalises buscarAelMateriaisAnalisesPorAelUnfExecutaExames(
			final AelUnfExecutaExames aelUnfExecutaExames) {
		return this.getManterAltaItemPedidoExameON()
				.buscarAelMateriaisAnalisesPorAelUnfExecutaExames(
						aelUnfExecutaExames);
	}

	@Override
	public AghUnidadesFuncionais buscarAghUnidadesFuncionaisPorAelUnfExecutaExames(
			final AelUnfExecutaExames aelUnfExecutaExames) {
		return this.getManterAltaItemPedidoExameON()
				.buscarAghUnidadesFuncionaisPorAelUnfExecutaExames(
						aelUnfExecutaExames);
	}

	@Override
	public void inserirAltaItemPedidoExame(final MpmAltaItemPedidoExame item)
			throws BaseException {
		this.getManterAltaItemPedidoExameON().inserirAltaItemPedidoExame(item);

	}

	@Override
	public List<MpmAltaItemPedidoExame> obterMpmAltaItemPedidoExame(
			final Integer altanAtdSeq, final Integer altanApaSeq, final Short altanAsuSeqp)
			throws ApplicationBusinessException {
		return this.getManterAltaItemPedidoExameON()
				.obterMpmAltaItemPedidoExame(altanAtdSeq, altanApaSeq,
						altanAsuSeqp);
	}

	@Override
	public void excluirMpmAltaItemPedidoExame(
			final MpmAltaItemPedidoExame altaItemPedidoExame)
			throws ApplicationBusinessException {
		this.getManterAltaItemPedidoExameON().excluirMpmAltaItemPedidoExame(
				altaItemPedidoExame);
	}

	/**
	 * Busca os itens da Ultima Prescricao Medica associado ao
	 * altaSumario.atendimento.<br>
	 * Retorna todos os itens da Ultima Prescricao Medica os que estiverem
	 * associados ao SumarioAlta<br>
	 * estaram marcados. <code>ItemPrescricaoMedicaVO.marcado</code><br>
	 * 
	 * @param altaSumario
	 * @return
	 * @throws BaseException
	 */
	@Override
	public List<ItemPrescricaoMedicaVO> buscaItensRecomendacaoPlanoPosAltaPrescricaoMedica(
			final MpmAltaSumario altaSumario) throws BaseException {
		return this
				.getManterAltaRecomendacaoON()
				.buscaItensRecomendacaoPlanoPosAltaPrescricaoMedica(altaSumario);
	}

	@Override
	public List<ItemPrescricaoMedicaVO> buscaItensPrescricaoMedicaMarcado(
			final MpmAltaSumario altaSumario) throws BaseException {
		return this.getManterAltaRecomendacaoON()
				.buscaItensPrescricaoMedicaMarcado(altaSumario);
	}

	/**
	 * 
	 * Atualiza os Itens Recomendados no Plano Pos Alta que deve ficar
	 * associados ao AltaSumario.<br>
	 * Remove (inativa) os que estavam associados e nao estao mais.<br>
	 * 
	 * @param altaSumario
	 * @param listaItens
	 * @throws BaseException
	 */
	@Override
	public void gravarRecomendacaoPlanoPosAltaPrescricaoMedica(
			final MpmAltaSumario altaSumario, final List<ItemPrescricaoMedicaVO> listaItensInsert)
			throws BaseException {
		this.getManterAltaRecomendacaoON()
				.gravarRecomendacaoPlanoPosAltaPrescricaoMedica(altaSumario,
						listaItensInsert);
	}

	/**
	 * Retorna uma lista de alta recomendação
	 * 
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public List<AltaRecomendacaoVO> listarAltaRecomendacao(final MpmAltaSumarioId id)
			throws BaseException {
		return this.getManterAltaRecomendacaoON().listarAltaRecomendacao(id);
	}

	/**
	 * Método que exclui alta não cadastrada
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void excluirAltaNaoCadastrado(final AltaRecomendacaoVO vo)
			throws BaseException {
		this.getManterAltaRecomendacaoON().removerAltaRecomendacao(vo);
	}

	/**
	 * Método que grava um alta não cadastrada
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void gravarAltaNaoCadastrado(final AltaRecomendacaoVO vo)
			throws BaseException {
		this.getManterAltaRecomendacaoON().gravarAltaRecomendacao(vo);
	}

	/**
	 * Método que gera recomendações de gestação
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void montarAltaNaoCadastradaGestacao(final MpmAltaSumario altaSumario) throws ApplicationBusinessException{
		this.getManterAltaRecomendacaoON().montarAltaNaoCadastradaRN(altaSumario);
	}
	/**
	 * Método que atualiza uma alta não cadastrada
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void atualizarAltaNaoCadastrado(final AltaRecomendacaoVO vo)
			throws BaseException {
		this.getManterAltaRecomendacaoON().atualizarAltaRecomendacao(vo);
	}

	/**
	 * Método que atualiza uma alta cadastrada
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void atualizarAltaCadastrada(final AltaCadastradaVO vo)
			throws BaseException {
		this.getManterAltaRecomendacaoON().atualizarAltaRecomendacao(vo);
	}
	
	@Override
	public void atualizarAltaItemPrescricao(final MpmAltaSumario altaSumario,
			final ItemPrescricaoMedicaVO itemPrescricaoMedicaVO)
			throws ApplicationBusinessException {
		this.getManterAltaRecomendacaoON().atualizarAltaItemPrescricao(altaSumario, itemPrescricaoMedicaVO);
	}
	
	@Override
	public void inativarAltaItemPrescricao(final MpmAltaSumario altaSumario,
			final ItemPrescricaoMedicaVO itemPrescricaoMedicaVO) throws BaseException {
		this.getManterAltaRecomendacaoON()
				.inativarAltaItemPrescricao(altaSumario,
						itemPrescricaoMedicaVO);
	}

	/**
	 * Valida informações de ionterface da Aba Procedimentos Slider Informações
	 * Complementares.
	 * 
	 * @param {AltaSumarioInfoComplVO} vo
	 * @throws BaseException
	 */
	@Override
	public void validarInformacoesComplementares(final String complemento,
			final AltaSumarioInfoComplVO vo) throws BaseException {

		this.getManterSumarioAltaProcedimentosON()
				.validarInformacoesComplementares(complemento, vo);

	}

	

	

	protected ManterAltaPedidoExameON getManterAltaPedidoExameON() {
		return manterAltaPedidoExameON;
	}

	@Override
	public MpmAltaPedidoExame obterMpmAltaPedidoExame(final Integer integer,
			final Integer integer2, final Short short1)
			throws ApplicationBusinessException {
		return this.getManterAltaPedidoExameON().obterMpmAltaPedidoExame(
				integer, integer2, short1);
	}

	@Override
	public void refazer(final Integer atdSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.getListarSumarioAltaReimpressaoON().refazer(atdSeq, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public Long countPesquisaListaSumAltaReimp(final Integer prontuario,
			final Integer codigo) {
		return this.getListarSumarioAltaReimpressaoON()
				.countPesquisaListaSumAltaReimp(prontuario, codigo);
	}

	@Override
	public List<MpmListaServSumrAlta> listaPesquisaListaSumAltaReimp(
			final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final Integer prontuario, final Integer codigo) {
		return this.getListarSumarioAltaReimpressaoON()
				.listaPesquisaListaSumAltaReimp(firstResult, maxResults,
						orderProperty, asc, prontuario, codigo);
	}

	private ListarSumarioAltaReimpressaoON getListarSumarioAltaReimpressaoON() {
		return listarSumarioAltaReimpressaoON;
	}

	@Override
	public List<RapServidores> listaProfissionaisEquipAtendimento(
			final RapServidores servidor) {

		return getMpmListaServEquipeDAO().listaProfissionaisEquipAtendimento(
				servidor);
	}

	private MpmMotivoReinternacaoDAO getMpmMotivoReinternacaoDAO() {
		return mpmMotivoReinternacaoDAO;
	}

	@Override
	public List<MpmMotivoReinternacao> obterMpmMotivoReinternacao(
			final Object parametro) {
		return this.getMpmMotivoReinternacaoDAO().obterMpmMotivoReinternacao(
				parametro);
	}

	private MpmAltaReinternacaoDAO getMpmAltaReinternacaoDAO() {
		return mpmAltaReinternacaoDAO;
	}

	@Override
	public MpmAltaReinternacao obterMpmAltaReinternacao(final Integer altanAtdSeq,
			final Integer altanApaSeq, final Short altanAsuSeqp)
			throws ApplicationBusinessException {
		return this.getMpmAltaReinternacaoDAO().obterMpmAltaReinternacao(
				altanAtdSeq, altanApaSeq, altanAsuSeqp);
	}

	@Override
	public MpmMotivoReinternacao obterMotivoReinternacaoPeloId(final Integer seq) {
		return this.getMpmMotivoReinternacaoDAO()
				.obterMotivoReinternacaoPeloId(seq);
	}

	private ManterAltaReinternacaoON getManterAltaReinternacaoON() {
		return manterAltaReinternacaoON;
	}

	@Override
	public void gravarAltaReinternacao(final MpmAltaReinternacao altaReinternacao)
			throws BaseException {
		this.getManterAltaReinternacaoON().gravarAltaReinternacao(
				altaReinternacao);
	}

	@Override
	public void removerAltaReinternacao(final MpmAltaReinternacao altaReinternacao)
			throws BaseException {
		this.getManterAltaReinternacaoON().removerAltaReinternacao(
				altaReinternacao);
	}

	@Override
	public String gravarAltaPedidoExame(final MpmAltaPedidoExame altaPedidoExame)
			throws BaseException {
		return this.getManterAltaPedidoExameON().gravarAltaPedidoExame(
				altaPedidoExame);
	}

	@Override
	public void removerAltaPedidoExame(final MpmAltaPedidoExame altaPedidoExame)
			throws BaseException {
		this.getManterAltaPedidoExameON().excluirAltaPedidoExame(
				altaPedidoExame);
	}

	@Override
	public String gravarObtGravidezAnterior(final MpmAltaSumario altaSumario,
			final DominioSimNao indicadorNecropsia)
			throws ApplicationBusinessException {
		return getManterObtGravidezAnteriorON().gravarObtGravidezAnterior(
				altaSumario, indicadorNecropsia);
	}

	@Override
	public MpmObtGravidezAnterior obterMpmObtGravidezAnterior(
			final MpmAltaSumario altaSumario) {
		return getManterObtGravidezAnteriorON().obterMpmObtGravidezAnterior(
				altaSumario);
	}

	private ManterObtGravidezAnteriorON getManterObtGravidezAnteriorON() {
		return manterObtGravidezAnteriorON;
	}

	protected ConsultoriaON getConsultoriaON() {
		return consultoriaON;
	}

	private MpmObtCausaAntecedenteDAO getMpmObtCausaAntecedenteDAO() {
		return mpmObtCausaAntecedenteDAO;
	}

	private MpmObtOutraCausaDAO getMpmObtOutraCausaDAO() {
		return mpmObtOutraCausaDAO;
	}

	private ManterObtCausaAntecedenteON getManterObtCausaAntecedenteON() {
		return manterObtCausaAntecedenteON;
	}
	
	private ManterObtOutraCausaON getManterObtOutraCausaON() {
		return manterObtOutraCausaON;
	}

	private ManterObitoNecropsiaON getManterObitoNecropsiaON() {
		return manterObitoNecropsiaON;
	}
	
	@Override
	public List<MpmObtCausaAntecedente> obterMpmObtCausaAntecedente(
			final Integer apaAtdSeq, final Integer apaSeq, final Short seqp)
			throws BaseException {
		return this.getMpmObtCausaAntecedenteDAO().obterMpmObtCausaAntecedente(
				apaAtdSeq, apaSeq, seqp);
	}

	@Override
	public List<MpmObtOutraCausa> obterMpmObtOutraCausa(final Integer apaAtdSeq,
			final Integer apaSeq, final Short seqp) throws ApplicationBusinessException {
		return this.getMpmObtOutraCausaDAO().obterMpmObtOutraCausa(apaAtdSeq,
				apaSeq, seqp);
	}

	@Override
	public String persistirCausaAntecedente(
			final SumarioAltaDiagnosticosCidVO itemSelecionado)
			throws BaseException {
		return this.getManterObtCausaAntecedenteON().persistirCausaAntecedente(
				itemSelecionado);
	}

	@Override
	public String persistirOutraCausa(
			final SumarioAltaDiagnosticosCidVO itemSelecionado)
			throws BaseException {
		return this.getManterObtOutraCausaON().persistirOutraCausa(
				itemSelecionado);
	}

	@Override
	public void cancelarSumario(final MpmAltaSumario altaSumario, String nomeMicrocomputador)
			throws BaseException {
		this.getManterAltaSumarioON().cancelarSumario(altaSumario, nomeMicrocomputador);
	}

	@Override
	public void removerObtCausaAntecedente(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException {
		this.getManterObtCausaAntecedenteON().removerObtCausaAntecedente(
				altaSumario);
	}

	@Override
	public void removerObtCausaAntecedente(final SumarioAltaDiagnosticosCidVO itemGrid)
			throws ApplicationBusinessException {
		this.getManterObtCausaAntecedenteON().removerObtCausaAntecedente(
				itemGrid);
	}

	@Override
	public void removerObtOutraCausa(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException {
		this.getManterObtOutraCausaON().removerObtOutraCausa(altaSumario);
	}

	@Override
	public void removerObtOutraCausa(final SumarioAltaDiagnosticosCidVO itemGrid)
			throws ApplicationBusinessException {
		this.getManterObtOutraCausaON().removerObtOutraCausa(itemGrid);
	}

	@Override
	public List<AltaCadastradaVO> listarAltaCadastrada(
			final MpmAltaSumario altaSumario) throws BaseException {
		return this.getManterSumarioAltaPosAltaON().listarAltaCadastrada(
				altaSumario);
	}

	@Override
	public List<AltaCadastradaVO> listarAltaCadastradaGravada(
			final MpmAltaSumario altaSumario) throws BaseException {
		return this.getManterSumarioAltaPosAltaON()
				.listarAltaCadastradaGravada(altaSumario);
	}

	@Override
	public void gravarAltasCadastradasSelecionadas(
			final List<AltaCadastradaVO> listaAltasCadastradas,
			final MpmAltaSumario altaSumario) throws BaseException {
		this.getManterSumarioAltaPosAltaON()
				.gravarAltasCadastradasSelecionadas(listaAltasCadastradas,
						altaSumario);
	}



	@Override
	public List<RelatorioConsultoriaVO> gerarDadosRelatorioConsultoria(
			final RapServidores servidor, final AghAtendimentos atd,
			final PrescricaoMedicaVO prescricao, Boolean validarIndImpSolicConsultoria) throws ApplicationBusinessException {
		return this.getRelatorioConsultoriaON().gerarRelatorioConsultoria(servidor, atd, prescricao, validarIndImpSolicConsultoria);
	}

	protected RelatorioConsultoriaON getRelatorioConsultoriaON() {
		return relatorioConsultoriaON;
	}

	@Override
	public void inserirMotivoIngressoCti(final MpmMotivoIngressoCti motivoIngressoCti)
			throws ApplicationBusinessException {
		motivoIngressoCtiRN.inserirMotivoIngressoCti(motivoIngressoCti);
	}

	@Override
	public void validarCamposObrigatoriosSumarioAlta(final MpmAltaSumario altaSumario) throws BaseException {
		this.getConcluirSumarioAltaON().validarCamposObrigatoriosSumarioAlta(altaSumario);
	}
	
	/**
	 * Executa a Finalizacao da Conclusao do Sumario de Alta.<br>
	 * Validações para conclusao do Sumario de Alta.<br>
	 * Gera Laudos.<br>
	 * Verifica se precisa de Justificativas.<br>
	 * Atualiza flag no banco indicando a conclusao da alta do paciente.<br>
	 * 
	 * @param altaSumario
	 * @return <code>ehSolicitarJustificativa</code>
	 * @throws BaseException
	 */
	@Override
	public boolean concluirSumarioAlta(final MpmAltaSumario altaSumario, String nomeMicrocomputador, RapServidores servidorValida)
			throws BaseException {
		MpmAltaSumario altaSumarioSalvar = getMpmAltaSumarioDAO().obterAltaSumarioPeloId(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		return this.getConcluirSumarioAltaON().concluirSumarioAlta(altaSumarioSalvar, nomeMicrocomputador, servidorValida);
	}

	@Override
	public List<AtestadoVO> obterListaDocumentosPacienteAtestados(Integer apaAtdSeq, Integer apaSeq, Short seqp, Short tasSeq) throws ApplicationBusinessException {
		return this.getConcluirSumarioAltaON().obterListaDocumentosPacienteAtestados(apaAtdSeq, apaSeq, seqp, tasSeq);
	}
	
	@Override
	public AtestadoVO obterDocumentoPacienteAtestado(Long atsSeq, Boolean imprimirAtestado) throws ApplicationBusinessException {
		return this.getConcluirSumarioAltaON().obterDocumentoPacienteAtestado(atsSeq, imprimirAtestado);
	}

	/**
	 * Executa Update no MpmAltaSumario.<br>
	 * Seta o indConcluido pra true.<br>
	 * 
	 * @param altaSumario
	 * @throws BaseException
	 */
	@Override
	public void continuarConclusaoSumarioAlta(final MpmAltaSumario altaSumario, String nomeMicrocomputador)
			throws BaseException {
		this.getConcluirSumarioAltaON().continuarConclusaoSumarioAlta(
				altaSumario, nomeMicrocomputador);
	}

	/**
	 * Cancela a Conclusao do Sumaro de Alta.<br>
	 * Remove os Laudos gerados.<br>
	 * 
	 * Chamada no cancelar da tela de Justificativas de Laudos no Sumario de
	 * Alta.<br>
	 * 
	 * @param altaSumario
	 * @throws BaseException
	 */
	@Override
	public void cancelarConclusaoSumarioAlta(final MpmAltaSumario altaSumario)
			throws BaseException {
		this.getConcluirSumarioAltaON().cancelarConclusaoSumarioAlta(
				altaSumario);
	}

	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void atualizarSumarioAlta(final MpmSumarioAlta sumarioAlta, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException, Exception {
		getSumarioAltaON().atualizarSumarioAlta(sumarioAlta, nomeMicrocomputador, dataFimVinculoServidor);
	}

	private ConcluirSumarioAltaON getConcluirSumarioAltaON() {
		return concluirSumarioAltaON;
	}

	protected ManterAltaMotivoON getManterAltaMotivoON() {
		return manterAltaMotivoON;
	}

	protected ManterAltaEstadoPacienteON getManterAltaEstadoPacienteON() {
		return manterAltaEstadoPacienteON;
	}

	@Override
	public void removerAltaMotivo(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException {
		getManterAltaMotivoON().removerAltaMotivo(altaSumario);
	}

	@Override
	public void removerAltaEstadoPaciente(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException {
		getManterAltaEstadoPacienteON().removerAltaEstadoPaciente(altaSumario);
	}

	@Override
	public void verificarEscalaGlasglow(final Integer seqEscalaGlasglow,
			final Integer seqAtendimento) throws ApplicationBusinessException {
		this.getPrescricaoMedicaRN().verificarEscalaGlasglow(seqEscalaGlasglow,
				seqAtendimento);
	}

	@Override
	public void verificarAtendimento(final Date dataHoraInicio,
			final Integer seqAtendimento, final Date dataRealizacao)
			throws ApplicationBusinessException {
		this.getPrescricaoMedicaRN().verificarAtendimento(dataHoraInicio,
				seqAtendimento, dataRealizacao);
	}
	
	@Override
	@BypassInactiveModule
	public VerificaAtendimentoVO verificarAtendimento(final Date dataHoraInicio,
			final Date dataHoraFim, final Integer seqAtendimento, final Integer seqHospitalDia,
			final Integer seqInternacao, final Integer seqAtendimentoUrgencia) throws ApplicationBusinessException{
		return this.getPrescricaoMedicaRN().verificaAtendimento(dataHoraInicio, dataHoraFim, seqAtendimento, seqHospitalDia, seqInternacao, seqAtendimentoUrgencia);
	}

	@Override
	public String obterComplementoInformacaoComplementar(final MpmAltaSumarioId id)
			throws ApplicationBusinessException {
		final MpmAltaComplFarmaco altaComplFarm = getManterAltaSumarioON()
				.getMpmAltaComplFarmaco(id);
		String complemento = null;
		if (altaComplFarm != null) {
			complemento = altaComplFarm.getDescricao();
		}
		return complemento;
	}

	@Override
	public boolean verificaModoUsoProcedimentoEspecialPrescrito(final Short pedSeq, final Short seqp) {
		return getManterPrescricaoMedicaON().existeModoUsoProcedimentoEspecialPrescrito(pedSeq, seqp);
	}
	
	/**
	 * 
	 * @param prescricaoMedicamento
	 * @param isUppercase
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public String obterDescricaoAlteracaoMedicamentoSolucaoDispensacaoFarmacia(
			final MpmPrescricaoMdto prescricaoMedicamento,String aprazamento, final Boolean isUppercase) throws ApplicationBusinessException {
		return this.getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDescricaoAlteracaoMedicamentoSolucaoDispensacaoFarmacia(prescricaoMedicamento, aprazamento,isUppercase);
	}
	
	public List<ItemDispensacaoFarmaciaVO> obterDescricaoDosagemAlteracaoItensMedicamentoSolucaoDispensacaoFarmacia(
			MpmPrescricaoMdto medicamentoSolucao, Boolean isUpperCase) {

		return getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDescricaoDosagemAlteracaoItensMedicamentoSolucaoDispensacaoFarmacia(
						medicamentoSolucao, isUpperCase);
	}

	public String obterDescricaoFormatadaMedicamentoSolucaoDispensacaoFarmacia(
			final MpmPrescricaoMdto medicamentoSolucao, String aprazamento, final Boolean inclusaoExclusao,
			final Boolean impressaoTotal, final Boolean isUpperCase) throws ApplicationBusinessException{
		
		return getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDescricaoFormatadaMedicamentoSolucaoDispensacaoFarmacia(medicamentoSolucao, aprazamento);
	}
	
	public List<ItemDispensacaoFarmaciaVO> obterDescricaoDosagemItensMedicamentoSolucaoDispensacaoFarmacia(
			final MpmPrescricaoMdto medicamentoSolucao, final Boolean isUpperCase){
		
		return getDescricaoFormatadaRelatorioItensConfirmadosON()
				.obterDescricaoDosagemItensMedicamentoSolucaoDispensacaoFarmacia(
						medicamentoSolucao, isUpperCase);
	}

	/**
	 * 
	 * @param fichaApache
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void inserirFichaApache(final MpmFichaApache fichaApache)
			throws ApplicationBusinessException {
		this.getFichaApacheRN().inserirFichaApache(fichaApache);

	}

	/**
	 * 
	 * @return
	 */
	private FichaApacheRN getFichaApacheRN() {
		return fichaApacheRN;
	}

	@Override
	public void verificaExigeObservacao(final Integer codigoMedicamento,
			final String observacao) throws ApplicationBusinessException {
		this.getItemPrescricaoMedicamentoRN().verificaExigeObservacao(
				codigoMedicamento, observacao);

	}

	@Override
	@BypassInactiveModule
	public void geraLaudoInternacao(final AghAtendimentos atendimento,
			final Date dthrUltimoEvento, final AghuParametrosEnum pLaudoAcompanhante,
			final FatConvenioSaudePlano convenioSaudePlano)
			throws BaseException {
		this.getLaudoON().geraLaudoInternacao(atendimento, dthrUltimoEvento,
				pLaudoAcompanhante, convenioSaudePlano);
	}


	@Override
	public void inserirAltaSumario(final MpmAltaSumario altaSum)
			throws ApplicationBusinessException {
		getManterAltaSumarioRN().inserirAltaSumario(altaSum);

	}

	@Override
	public void incluirPrescricaoCuidado(final MpmPrescricaoCuidado prescricaoCuidado, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		
		this.getManterPrescricaoCuidadoON().incluir(prescricaoCuidado, nomeMicrocomputador, dataFimVinculoServidor);

	}

	@Override
	public void inserirPrescricaoMedicamentoModeloBasico(
			final MpmPrescricaoMdto mpmPrescricaoMedicamento, String nomeMicrocomputador)
			throws BaseException {
		
		this.getManterPrescricaoMedicamentoON()
				.inserirPrescricaoMedicamentoModeloBasico(
						mpmPrescricaoMedicamento, nomeMicrocomputador);

	}

	@Override
	public Boolean isUnidadeBombaInfusao(final AghUnidadesFuncionais unidadeFuncional) {
		return this.getManterPrescricaoMedicamentoON().isUnidadeBombaInfusao(
				unidadeFuncional);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void agendarGerarDadosSumarioPrescricaoMedica(final String cron, final Date dataInicio, final Date dataFim, RapServidores servidorLogado, String nomeProcessoQuartz) throws ApplicationBusinessException {
		getManterSumarioSchedulerRN().gerarDadosSumarioPrescricaoMedica(cron, dataInicio, dataFim,servidorLogado, nomeProcessoQuartz);
	}
	
	@Override
	public void inserirCargaPrescricaoProcedimentoEspecial(
			final MpmPrescricaoProcedimento procedimento,
			final MpmPrescricaoMedica prescricaoMedica, String nomeMicrocomputador) throws BaseException {
		
		getPrescreverProcedimentosEspeciaisON()
				.inserirCargaPrescricaoProcedimentoEspecial(procedimento,
						prescricaoMedica, nomeMicrocomputador);

	}

	@Override
	public void removerAtendProf(final Integer atdSeq) {
		this.getPrescricaoMedicaRN().removerAtendProf(atdSeq);

	}

	@Override
	public void removerSumariosPendentes(final Integer atdSeq) {
		this.getPrescricaoMedicaRN().removerSumariosPendentes(atdSeq);

	}

	@Override
	public void validarAprazamento(
			final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			final Short frequencia) throws ApplicationBusinessException {
		this.getPrescricaoMedicaRN().validaAprazamento(
				tipoFrequenciaAprazamento, frequencia);

	}

	@Override
	@BypassInactiveModule
	public RelSumarioAltaVO criaRelatorioSumarioAltaPorId(final MpmAltaSumarioId id) {
		return relatorioConclusaoSumarioAltaON.criaRelatorioSumarioAltaPorId(id);
	}

	public RelatorioSumarioObitoON getRelatorioSumarioObitoON() {
		return relatorioSumarioObitoON;
	}

	@Override
	@BypassInactiveModule
	public RelatorioSumarioObitoVO criaRelatorioSumarioAlta(
			final MpmAltaSumarioId id, final String tipoImpressao) throws BaseException {
		return this.getRelatorioSumarioObitoON().criaRelatorioSumarioAlta(id,
				tipoImpressao);
	}

	@Override
	public void excluirAprazamentoFrequencia(final MpmAprazamentoFrequenciaId id) throws ApplicationBusinessException {
		this.getTipoFrequenciaAprazamentoON().excluirAprazamentoFrequencia(id);

	}

	@Override
	public Long countTipoFrequenciaAprazamento(
			final MpmTipoFrequenciaAprazamento entityFilter) {
		return this.getTipoFrequenciaAprazamentoON().count(entityFilter);
	}

	@Override
	public List<MpmTipoFrequenciaAprazamento> pesquisarTipoFrequenciaAprazamento(
			final MpmTipoFrequenciaAprazamento entityFilter, final Integer firstResult,
			final Integer maxResults, final String orderProperty, final boolean asc) {
		return this.getTipoFrequenciaAprazamentoON().paginator(entityFilter,
				firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public void validarExclusaoTipoFrequenciaAprazamento(
			final MpmTipoFrequenciaAprazamento entity, final boolean validaAprazamentos)
			throws ApplicationBusinessException {
		this.getTipoFrequenciaAprazamentoON()
				.validaExclusaoTipoFrequenciaAprazamento(entity,
						validaAprazamentos);
	}

	@Override
	public void criarAprazamentoFrequencia(final MpmAprazamentoFrequencia detail,
			final MpmTipoFrequenciaAprazamento entity) throws ApplicationBusinessException {
		this.getTipoFrequenciaAprazamentoON().criaAprazamentoFrequencia(detail,
				entity);
	}

	private TipoFrequenciaAprazamentoON getTipoFrequenciaAprazamentoON() {
		return tipoFrequenciaAprazamentoON;
	}

	private ManterSumarioAltaProcedimentosON getManterSumarioAltaProcedimentosON() {
		return manterSumarioAltaProcedimentosON;
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificarServidorMedico(final Integer serMatricula, final Short serCodigo) throws ApplicationBusinessException{
		return getConfirmarPrescricaoMedicaON().verificarServidorMedico(serMatricula, serCodigo);
	}
	

	private SumarioAltaON getSumarioAltaON() {
		return sumarioAltaON;
	}
	
	private ManterPrescricaoMedicaRN getManterPrescricaoMedicaRN() {
		return manterPrescricaoMedicaRN;
	}
	
	
	

	@Override
	public boolean verificaProcedimentosEspeciasPrescritos(
			final Short codigoProcedimento) {
		return getManterPrescricaoMedicaON()
				.existeProcedimentoEspecialPrescrito(codigoProcedimento);
	}

	@Override
	public boolean existeProcedimentosComLaudoJustificativaParaImpressao(
			final AghAtendimentos atendimento) throws BaseException {
		final ConcluirSumarioAltaRN dao = getConcluirSumarioAltaRN();
		return dao
				.existeProcedimentosComLaudoJustificativaParaImpressao(atendimento);
	}
	
	private ConcluirSumarioAltaRN getConcluirSumarioAltaRN() {
		return concluirSumarioAltaRN;
	}

	@Override
	public void excluirTipoFrequenciaAprazamento(final Short seq) throws ApplicationBusinessException {
		getTipoFrequenciaAprazamentoON().excluirTipoAprazamentoFrequencia(seq);
	}

	/**
	 * Este método verifica se o hospital tem ambulatório ou não.
	 * 
	 * @author gfmenezes
	 * 
	 * @return
	 * @throws BaseException
	 */
	@Override
	public Boolean existeAmbulatorio() throws BaseException {
		return this.getConcluirSumarioAltaON().existeAmbulatorio();
	}


	@Override
	public void inativarAltaRecomendacaoCadastrada(final MpmAltaSumario altaSumario,
			final MpmAltaRecomendacaoId altaRecomendacaoId) throws BaseException {
		this.getManterSumarioAltaPosAltaON()
				.inativarAltaRecomendacaoCadastrada(altaSumario,
						altaRecomendacaoId);
	}

	@Override
	public void salvarTipoFrequenciaAprazamento(
			final MpmTipoFrequenciaAprazamento entity,
			final List<MpmAprazamentoFrequencia> aprazamentos,
			final List<MpmAprazamentoFrequencia> excluidosList)
			throws ApplicationBusinessException {
		getTipoFrequenciaAprazamentoON().salvar(entity, aprazamentos,
				excluidosList);
	}

	@Override
	@BypassInactiveModule
	public String buscarResumoLocalPaciente(final AghAtendimentos atendimento) throws ApplicationBusinessException {
		return getManterPrescricaoMedicaRN().buscarResumoLocalPaciente(atendimento);
	}
	
	@Override
	@BypassInactiveModule
	public String buscarResumoLocalPacienteII(final AghAtendimentos atendimento) throws ApplicationBusinessException {
		return getManterPrescricaoMedicaRN().buscarResumoLocalPaciente(atendimento);
	}
	
	@Override
	@BypassInactiveModule
	public String buscarResumoLocalPacienteUniFuncional(AghAtendimentos atendimento) throws ApplicationBusinessException {
		return getManterPrescricaoMedicaRN().buscarResumoLocalPacienteUniFuncional(atendimento);
	}
	
	@Override
	@BypassInactiveModule
	public String buscarResumoLocalPaciente(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException {
		return getManterPrescricaoMedicaRN().buscarResumoLocalPaciente(leito, quarto, unidadeFuncional);
	}
	
	@Override
	@BypassInactiveModule
	public String buscarResumoLocalPaciente2(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException {
		return getManterPrescricaoMedicaRN().buscarResumoLocalPaciente2(leito, quarto, unidadeFuncional);
	}	

	@Override
	public boolean verificaModoUsoEmModeloBasico(final Short pedSeq, final Short seqp) {
		return getMpmModeloBasicoModoUsoProcedimentoDAO().existeModeloBasicoComModoUso(pedSeq, seqp);
	}

	private MpmModeloBasicoModoUsoProcedimentoDAO getMpmModeloBasicoModoUsoProcedimentoDAO() {
		return mpmModeloBasicoModoUsoProcedimentoDAO;
	}
	
	@Override
	public boolean verificaProcedimentoEspecialEmModeloBasico(final MpmProcedEspecialDiversos procedimento) {
		return getMpmModeloBasicoProcedimentoDAO().existeModeloBasicoComProcedimentoEspecial(procedimento);
	}
	
	private MpmModeloBasicoProcedimentoDAO getMpmModeloBasicoProcedimentoDAO() {
		return mpmModeloBasicoProcedimentoDAO;
	}
	
	/**
	 * @param pCthSeq
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public Integer buscarCidAlta(final Integer pCthSeq) {
		return getMpmAltaDiagPrincipalDAO().buscarCidAlta(pCthSeq);
	}

	
	
	
	protected MpmProcedEspecialDiversoDAO getMpmProcedEspecialDiversoDAO(){
		return mpmProcedEspecialDiversoDAO;
	}
	
	@Override
	public List<MpmProcedEspecialDiversos> listarProcedimentosEspeciaisAtivosComProcedimentoIntAtivoPorPhiSeq(final Integer phiSeq){
		return this.getMpmProcedEspecialDiversoDAO().listarProcedimentosEspeciaisAtivosComProcedimentoIntAtivoPorPhiSeq(phiSeq);
	}
	
	@Override
	public List<MpmProcedEspecialDiversos> listarMpmProcedEspecialDiversos(final Object objPesquisa){
		return this.getMpmProcedEspecialDiversoDAO().listarMpmProcedEspecialDiversos(objPesquisa);
	}
	
	@Override
	public Long listarMpmProcedEspecialDiversosCount(final Object objPesquisa){
		return this.getMpmProcedEspecialDiversoDAO().listarMpmProcedEspecialDiversosCount(objPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public Object[] buscaConsProf(Integer matricula,Short vincodigo) throws ApplicationBusinessException {
		return getManterPrescricaoMedicaRN().buscaConsProf(matricula, vincodigo);
	}
	
	@Override	
	public Object[] buscaConsProf(RapServidores rapServidor) throws ApplicationBusinessException {
		return getManterPrescricaoMedicaRN().buscaConsProf(rapServidor);
	}

	@Override
	public List<MpmTipoLaudo> listarTiposLaudo(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final Short seq, final String descricao, final DominioSituacao situacao) {
		return this.getMpmTipoLaudoDAO().listarTiposLaudo(firstResult, maxResult, orderProperty, asc, seq,
				descricao, situacao);
	}

	@Override
	@BypassInactiveModule
	public Long listarTiposLaudoCount(final Short seq, final String descricao, final DominioSituacao situacao) {
		return this.getMpmTipoLaudoDAO().listarTiposLaudoCount(seq, descricao, situacao);
	}

	@Override
	@BypassInactiveModule
	public List<MpmTextoPadraoLaudo> listarTextoPadraoLaudo(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final Integer seq, final String descricao, final DominioSituacao situacao) {
		return this.getMpmTextoPadraoLaudoDAO().listarTextoPadraoLaudo(firstResult, maxResult, orderProperty, asc, seq, descricao,
				situacao);
	}

	@Override
	@BypassInactiveModule
	public Long listarTextoPadraoLaudoCount(final Integer seq, final String descricao, final DominioSituacao situacao) {
		return this.getMpmTextoPadraoLaudoDAO().listarTextoPadraoLaudoCount(seq, descricao, situacao);
	}

	@Override
	public MpmTipoLaudo obterMpmTipoLaudoPorChavePrimaria(final Short seq) {
		return this.getMpmTipoLaudoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public MpmTipoLaudo obterTipoLaudoComTiposSecundarios(Short seq) {
		return this.getMpmTipoLaudoDAO().obterTipoLaudoComTiposSecundarios(seq);
	}
	
	@Override
	public List<MpmTipoLaudoConvenio> pesquisarTipoLaudoConvenio(Short tipoLaudoSeq) {
		return this.getMpmTipoLaudoConvenioDAO().pesquisarTipoLaudoConvenio(tipoLaudoSeq);
	}
	
	@Override
	public List<MpmTipoLaudoTextoPadrao> pesquisarTipoLaudoTextoPadrao(Short tipoLaudoSeq) {
		return this.getMpmTipoLaudoTextoPadraoDAO().pesquisarTipoLaudoTextoPadrao(tipoLaudoSeq);
	}
	
	@Override
	public MpmTextoPadraoLaudo obterMpmTextoPadraoLaudoPorChavePrimaria(final Integer seq) {
		return this.getMpmTextoPadraoLaudoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void inserirMpmTipoLaudo(final MpmTipoLaudo tipoLaudo, final boolean flush) throws BaseException {
		this.getPrescricaoMedicaON().inserirMpmTipoLaudo(tipoLaudo, flush);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterTipoLaudo','editarTipoLaudo')}")
	public void atualizarMpmTipoLaudo(final MpmTipoLaudo tipoLaudo, final boolean flush) throws BaseException {
		this.getPrescricaoMedicaON().atualizarMpmTipoLaudo(tipoLaudo, flush);
	}

	@Override
	public void inserirMpmTextoPadraoLaudo(final MpmTextoPadraoLaudo textoPadraoLaudo, final boolean flush) throws BaseException {
		this.getPrescricaoMedicaON().inserirMpmTextoPadraoLaudo(textoPadraoLaudo, flush);
	}

	@Override
	public void atualizarMpmTextoPadraoLaudo(final MpmTextoPadraoLaudo textoPadraoLaudo, final boolean flush) throws BaseException {
		this.getPrescricaoMedicaON().atualizarMpmTextoPadraoLaudo(textoPadraoLaudo, flush);
	}
	
	@Override
	public Date atualizaInicioTratamento(final Date dataHoraInicio, final Date dataHoraInicioAdm) throws ApplicationBusinessException {
		return getPrescricaoMedicamentoRN().atualizaInicioTratamento(dataHoraInicio, dataHoraInicioAdm);
	}
	
	/**
	 * Calcula o número de vezes que um aprazamento
	 * 
	 */
	@Override
	public Long calculoNumeroVezesAprazamento24Horas(final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento, final Short frequencia) {
		return getManterPrescricaoDietaON().calculoNumeroVezesAprazamento24Horas(tipoFrequenciaAprazamento, frequencia);
	}

	private PrescricaoMedicamentoRN getPrescricaoMedicamentoRN() {
		return prescricaoMedicamentoRN;		
	}
	
	@Override
	public MpmTipoLaudoConvenio obterMpmTipoLaudoConvenioPorChavePrimaria(final MpmTipoLaudoConvenioId id) {
		return this.getMpmTipoLaudoConvenioDAO().obterPorChavePrimaria(id);
	}

	@Override
	public MpmTipoLaudoTextoPadrao obterMpmTipoLaudoTextoPadraoPorChavePrimaria(final MpmTipoLaudoTextoPadraoId id) {
		return this.getMpmTipoLaudoTextoPadraoDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void inserirMpmTipoLaudoConvenio(final MpmTipoLaudoConvenio tipoLaudoConvenio, final boolean flush) throws BaseException {
		this.getPrescricaoMedicaON().inserirMpmTipoLaudoConvenio(tipoLaudoConvenio, flush);
	}

	@Override
	public void removerMpmTipoLaudoConvenio(final MpmTipoLaudoConvenio tipoLaudoConvenio, final boolean flush) {
		this.getPrescricaoMedicaON().removerMpmTipoLaudoConvenio(tipoLaudoConvenio, flush);
	}

	@Override
	public void inserirMpmTipoLaudoTextoPadrao(final MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao, final boolean flush) throws BaseException {
		this.getPrescricaoMedicaON().inserirMpmTipoLaudoTextoPadrao(tipoLaudoTextoPadrao, flush);
	}

	@Override
	public void removerMpmTipoLaudoTextoPadrao(final MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao, final boolean flush) {
		this.getPrescricaoMedicaON().removerMpmTipoLaudoTextoPadrao(tipoLaudoTextoPadrao, flush);
	}

	@Override
	public List<MpmTextoPadraoLaudo> pesquisarTextosPadrao(final String filtro) {
		return this.getMpmTextoPadraoLaudoDAO().pesquisarTextosPadrao(filtro);
	}

	@Override
	public Long pesquisarTextosPadraoCount(final String filtro) {
		return this.getMpmTextoPadraoLaudoDAO().pesquisarTextosPadraoCount(filtro);
	}

	protected MpmTipoLaudoTextoPadraoDAO getMpmTipoLaudoTextoPadraoDAO() {
		return mpmTipoLaudoTextoPadraoDAO;
	}
	
	protected MpmTipoLaudoConvenioDAO getMpmTipoLaudoConvenioDAO() {
		return mpmTipoLaudoConvenioDAO;
	}

	protected MpmTipoLaudoDAO getMpmTipoLaudoDAO() {
		return mpmTipoLaudoDAO;
	}

	@Override
	public void clear() {
		this.flush();
		this.clear();
	}

	

	@Override
	public List<MpmPrescricaoMdto> pesquisarPrescricaoMdtoNovo(
			final Integer pmeAtdSeq, final Date pmeData, final Date pmeDthrInicio, final Date pmeDthrFim) {
		return getMpmPrescricaoMdtoDAO().pesquisarPrescricaoMdtoNovo(pmeAtdSeq, pmeData, pmeDthrInicio, pmeDthrFim);
	}

	@Override
	public List<MpmPrescricaoMdto> pesquisarPrescricaoMdtoSitConfirmado(
			final Integer pmeAtdSeq, final Integer pmeSeq) {
		return getMpmPrescricaoMdtoDAO().pesquisarPrescricaoMdtoSitConfirmado(pmeAtdSeq, pmeSeq);
	}

	@Override
	public Long pesquisarItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendenteCount(
			final MpmPrescricaoMedica prescricaoMedica, final AfaMedicamento medicamento) {
		return getMpmItemPrescricaoMdtoDAO().pesquisarItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendenteCount(prescricaoMedica, medicamento);
	}
	
	private MpmItemPrescricaoMdtoDAO getMpmItemPrescricaoMdtoDAO(){
		return mpmItemPrescricaoMdtoDAO;
	}

	@Override
	public MpmItemPrescricaoMdto obterMpmItemPrescricaoMdtoPorChavePrimaria(
			final MpmItemPrescricaoMdtoId chavePrimaria) {
		return getMpmItemPrescricaoMdtoDAO().obterPorIdComJoin(chavePrimaria);
	}

	@Override
	public List<MpmItemPrescricaoMdto> obterListaItemPrescricaoParaMdto(
			final AfaMedicamento entidade) {
		return getMpmItemPrescricaoMdtoDAO().obterListaItemPrescricaoParaMdto(entidade);
	}

	@Override
	public MpmPrescricaoMedica obterMpmPrescricaoMedicaPorChavePrimaria(
			final MpmPrescricaoMedicaId mpmId) {
		//return getMpmPrescricaoMedicaDAO().obterPorChavePrimaria(mpmId);
		return this.getConfirmarPrescricaoMedicaON().obterPrescricaoPorChavePrimaria(mpmId);
	}

	@Override
	public void verificarPrescricaoCancelada(MpmPrescricaoMedica prescricao) 
			throws ApplicationBusinessException {
		getCancelarPrescricaoMedicaON().verificarPrescricaoCancelada(prescricao);
	}

	@Override
	public VMpmPrescrMdtos obtemPrescMdto(final Integer imePmdAtdSeq, final Long vPmdSeq,
			final Integer imeMedMatCodigo, final Short imeSeqp) {
		return getPrescricaoMedicaRN().obtemPrescMdto(imePmdAtdSeq, vPmdSeq,
				imeMedMatCodigo, imeSeqp);
	}

	@Override
	public VMpmMdtosDescr obterVMpmMdtosDescrPorMedicamento(
			final AfaMedicamento medicamento) {
		return getVMpmMdtosDescrDAO().obterVMpmMdtosDescrPorMedicamento(medicamento);
	}
	
	protected VMpmMdtosDescrDAO getVMpmMdtosDescrDAO(){
		return vMpmMdtosDescrDAO;
	}

	@Override
	public List<MpmItemPrescricaoMdto> pesquisarItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendente(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final MpmPrescricaoMedica prescricaoMedica, final AfaMedicamento medicamento) {
		return getMpmItemPrescricaoMdtoDAO()
				.pesquisarItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendente(
						firstResult, maxResult, orderProperty, asc,
						prescricaoMedica, medicamento);
	}

	@Override
	public VMpmDosagem obterVMpmDosagem(final Integer matCodigo, final Integer seq) {
		return getVMpmDosagemDAO().obterVMpmDosagem(matCodigo, seq);
	}
	
	private VMpmDosagemDAO getVMpmDosagemDAO(){
		return vMpmDosagemDAO;
	}
	
	@Override
	public MpmUnidadeMedidaMedica obterUnidadesMedidaMedicaPeloId(final Integer seq) {
		return getMpmUnidadeMedidaMedicaDAO().obterUnidadesMedidaMedicaPeloId(seq);
	}

	@Override
	public List<MpmPrescricaoMdtosHist> pesquisaMedicamentosHistPOL(final Integer atdSeq) {
		return this.getMpmPrescricaoMdtoHistDAO().pesquisaMedicamentosHistPOL(atdSeq);
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoMdto> pesquisaMedicamentosPOL(final Integer atdSeq) {
		return this.getMpmPrescricaoMdtoDAO().pesquisaMedicamentosPOL(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoMdto> pesquisaMedicamentosPOL(Integer atdSeq, DominioIndPendenteItemPrescricao dominio) {
		return this.getMpmPrescricaoMdtoDAO().pesquisaMedicamentosPOL(atdSeq, dominio);
	}
	
	@Override
	@BypassInactiveModule
	public MpmAltaSumario pesquisarAltaSumarioConcluido(final Integer apaAtdSeq) {
		return this.getMpmAltaSumarioDAO().pesquisarAltaSumarioConcluido(apaAtdSeq);
	}
	@Override
	@BypassInactiveModule
	public MpmCidAtendimento pesquisaCidAtendimentoPrincipal(final AghAtendimentos atendimento) {
		return this.getMpmCidAtendimentoDAO().pesquisaCidAtendimentoPrincipal(atendimento);
	}
	
	protected MpmCidAtendimentoDAO getMpmCidAtendimentoDAO() {
		return mpmCidAtendimentoDAO;
	}
	
	protected MpmAltaImpDiagnosticaDAO getMpmAltaImpDiagnosticaDAO() {
		return mpmAltaImpDiagnosticaDAO;
	}
	
	protected MpmAltaPrincExameDAO getMpmAltaPrincExameDAO() {
		return mpmAltaPrincExameDAO;
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmAltaImpDiagnostica> pesquisarAltaImpDiagnosticaPorAtendimento(
			final Integer atdSeq){
		return this.getMpmAltaImpDiagnosticaDAO().pesquisarAltaImpDiagnosticaPorAtendimento(atdSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisaSolicitacoesConsultoriaCount(final Integer atdSeq) {
		return this.getConsultoriaON().pesquisaSolicitacoesConsultoriaCount(atdSeq);
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmSolicitacaoConsultoria> pesquisaSolicitacoesConsultoria(final Integer atdSeq) {
		return this.getConsultoriaON().pesquisaSolicitacoesConsultoria(atdSeq);
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmSolicitacaoConsultoriaVO> pesquisaSolicitacoesConsultoriaVO(final Integer atdSeq) {
		return this.getConsultoriaON().pesquisaSolicitacoesConsultoriaVO(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public MpmSolicitacaoConsultoria obterSolicitacaoConsultoriaPorId(final Integer idAtendimento, final Integer seqSolicitacaoconsultoria) {
		return this.getConsultoriaON().obterSolicitacaoConsultoriaPorId(idAtendimento, seqSolicitacaoconsultoria);
	}
	
	@Override
	@BypassInactiveModule
	public String obterRespostasConsultoria(final Integer idAtendimento, final Integer seqConsultoria, final Integer ordem) {
		return this.getConsultoriaON().obterRespostasConsultoria(idAtendimento, seqConsultoria, ordem);
	}

	/**
	 * Método utilizado para pesquisar as consultorias de uma prescrição médica
	 * 
	 * @param prescricaoMedica
	 * @return
	 */
	@Override
	public List<MpmSolicitacaoConsultoria> pesquisarConsultoriasPorPrescricao(final MpmPrescricaoMedica prescricaoMedica) {
		return this.getConsultoriaON().pesquisarConsultoriasPorPrescricao(prescricaoMedica);
	}

	/**
	 * Método que verifica se já existe uma solicitação de consultoria para esta
	 * especialidade em uma mesma prescrição de um mesmo atendimento. TODO
	 * alterar nome de metodo para pesquisarSolicitacaoConsultoria.
	 * 
	 * @param especialidade
	 * @param idAtendimento
	 * @return
	 */
	@Override
	public MpmSolicitacaoConsultoria verificarSolicitacaoConsultoriaAtivaPorEspecialidade(final Short idEspecialidade,
			final Integer idAtendimento, final Integer idPrescricao) {
		return this.getConsultoriaON().verificarSolicitacaoConsultoriaAtivaPorEspecialidade(idEspecialidade, idAtendimento,
				idPrescricao);
	}

	/**
	 * método responsável por fazer a persistência
	 * 
	 * @param solicitacaoConsultoria
	 * @param idAtendimento
	 * @param idPrescricao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public MpmSolicitacaoConsultoria persistirSolicitacaoConsultoria(final MpmSolicitacaoConsultoria solicitacaoConsultoria,
			final Integer idAtendimento, final Integer idPrescricao) throws BaseException {
		return this.getConsultoriaON().persistirSolicitacaoConsultoria(solicitacaoConsultoria, idAtendimento, idPrescricao);
	}

	@Override
	public void atualizarVisualizacaoConsultoria(MpmSolicitacaoConsultoria solicitacaoConsultoria) throws BaseException {
		this.getConsultoriaON().atualizarVisualizacaoConsultoria(solicitacaoConsultoria);
		
	}
	
	@Override
	public void atualizarVisualizacaoInformacaoPrescribente(MpmInformacaoPrescribente informacaoPrescribente, RapServidores servidorLogado) throws BaseException {
		this.getPrescricaoMedicaRN().atualizarVisualizacaoInformacaoPrescribente(informacaoPrescribente, servidorLogado);
	}
	
	@Override
	public void atualizarVisualizacaoParecer(Integer atdSeq, ParecerPendenteVO parecerPendenteVO) throws BaseException {
		this.getPrescricaoMedicaRN().atualizarVisualizacaoParecer(atdSeq, parecerPendenteVO);
	}
	
	@Override
	@BypassInactiveModule
	public MpmTipoFrequenciaAprazamento obterTipoFrequenciaAprazamentoId(final Short tFASeq) {
		return this.getMpmTipoFrequenciaAprazamentoDAO().obterTipoFrequenciaAprazamentoPeloId(tFASeq);
	}
	
	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}
	
	@Override
	@BypassInactiveModule
	public List<Date> listarDataIngressoUnidadeOrdenadoPorSeqpDesc(final Integer atdSeq) {
		return this.getMpmFichaApacheDAO().listarDataIngressoUnidadeOrdenadoPorSeqpDesc(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public Boolean verificaPendenciaFichaApache(final Integer atdSeq) {
		return this.getMpmFichaApacheDAO().verificaPendenciaFichaApache(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaSumario> listarAltasSumarioPorAtendimento(final Integer atdSeq) {
		return this.getMpmAltaSumarioDAO().listarAltasSumarioPorAtendimento(atdSeq);
	}
	
	@Override
	@BypassInactiveModule
	public MpmAltaSumario obterAltaSumarioPeloId(final Integer apaAtdSeq, final Integer apaSeq, final Short seqp) {
		return this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(apaAtdSeq, apaSeq, seqp);
	}
	
	@Override
	public List executaCursorAltaSumarioAtendimentoEnforceRN(final Integer atdSeq) {
		return this.getMpmAltaSumarioDAO().executaCursorAltaSumarioAtendimentoEnforceRN(atdSeq);
	}
	
	protected MpmFichaApacheDAO getMpmFichaApacheDAO() {
		return mpmFichaApacheDAO;
	}
	
	protected MpmMotivoIngressoCtiDAO getMpmMotivoIngressoCtiDAO(){
		return mpmMotivoIngressoCtiDAO;
	}
	
	protected MpmSumarioAltaDAO getMpmSumarioAltaDAO() {
		return mpmSumarioAltaDAO;
	}
	
	@Override
	@BypassInactiveModule
	public Date pesquisarDataAltaInternacao(final Integer internacaoSeq) {
		return getMpmSumarioAltaDAO().pesquisarDataAltaInternacao(internacaoSeq);
	}

	@Override
	@BypassInactiveModule
	public DadosAltaSumarioVO buscarDadosAltaSumario(final Integer internacaoSeq) {
		return getMpmAltaSumarioDAO().buscarDadosAltaSumario(internacaoSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Date obterDataAltaPorInternacao(final Integer intSeq) {
		return getMpmAltaSumarioDAO().obterDataAltaPorInternacao(intSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Date obterDataAltaInternacaoComMotivoAlta(final Integer intSeq) {
		return getMpmSumarioAltaDAO().obterDataAltaInternacaoComMotivoAlta(intSeq);
	}
	
	private ManterTipoFrequenciaAprazamentoON getManterTipoFrequenciaAprazamentoON() {
		return manterTipoFrequenciaAprazamentoON;
	}

	private ManterHorarioInicAprazamentoON getManterHorarioInicAprazamentoON() {
		return manterHorarioInicAprazamentoON;
	}
	
	/**
	 * Efetua a busca de horários de início de aprazamento
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param unfSeq
	 * @param situacao
	 * @return Lista de horários de início de aprazamento
	 */
	@Override
	public List<MpmHorarioInicAprazamento> pesquisarHorariosInicioAprazamentos(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final Short unfSeq, final DominioSituacao situacao) {
		
		return getManterHorarioInicAprazamentoON().pesquisarHorariosInicioAprazamentos(firstResult, maxResult, orderProperty,
			asc, unfSeq, situacao);
	}
	
	/**
	 * Efetua o count para a busca de horários de início de aprazamento
	 * @param unfSeq
	 * @param situacao
	 * @return Count
	 */
	@Override
	public Long pesquisarHorariosInicioAprazamentosCount(final Short unfSeq, final DominioSituacao situacao) {
		return getManterHorarioInicAprazamentoON().pesquisarHorariosInicioAprazamentosCount(unfSeq, situacao);
	}

	/**
	 * Obtém unidades funcionais relativas à aprazamento por código ou descricao e situação
	 * @param paramPesquisa Código ou descrição
	 * @return Lista de Unidades Funcionais
	 */
	@Override
	public List<AghUnidadesFuncionaisVO> pesquisarUnidadesFuncionaisAprazamentoPorCodigoDescricao(
			final Object paramPesquisa) {
		return getManterHorarioInicAprazamentoON().pesquisarUnidadesFuncionaisAprazamentoPorCodigoDescricao(paramPesquisa);
	}

	/**
	 * Pesquisa tipos de aprazamento ativos e de frequência por código ou descrição
	 * @param objPesquisa Códuigo/ Descrição
	 * @return Lista de tipos de aprazamento
	 */
	@Override
	public List<MpmTipoFrequenciaAprazamento> pesquisarTipoAprazamentoAtivoFrequenciaPorCodigoDescricao(
			final Object objPesquisa) {
		
		return getManterTipoFrequenciaAprazamentoON().pesquisarTipoAprazamentoAtivoFrequenciaPorCodigoDescricao(objPesquisa);
	}

	/**
	 * Inclui ou atualiza o o horário de início de aprazamento
	 * @param horarioAprazamento
	 * @throws BaseException 
	 */
	@Override
	public void persistirHorarioInicioAprazamento(
			final MpmHorarioInicAprazamento horarioAprazamento) throws BaseException {		
		getManterHorarioInicAprazamentoON().persistirHorarioInicioAprazamento(horarioAprazamento);		
	}

	/**
	 * Remove um horário de início de aprazamento de acordo com o id informado.
	 * @param horarioAprazamentoId
	 */
	@Override
	public void removerHorarioAprazamento(
			final MpmHorarioInicAprazamentoId horarioAprazamentoId) {
		getManterHorarioInicAprazamentoON().removerHorarioAprazamento(horarioAprazamentoId);
	}

	/**
	 * ORADB Forms AINP_VERIFICA_PRESCRICAO (CURSOR C_PRESC) obtém a prescrição
	 * médica
	 * 
	 * @param atdSeq
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public MpmPrescricaoMedica obterPrescricaoMedica(final Integer atdSeq) {
		return getMpmPrescricaoMedicaDAO().obterPrescricaoMedica(atdSeq);
	}
	
	@Override
	public MpmPrescricaoMedica obterPrescricaoComAtendimentoPaciente(Integer atdSeq, Integer seq) {
		return mpmPrescricaoMedicaDAO.obterPrescricaoComAtendimentoPaciente(atdSeq, seq);
	}
	
	@Override
	public void removerMpmFichaApache(final MpmFichaApache mpmFichaApache, final boolean flush){
		getMpmFichaApacheDAO().remover(mpmFichaApache);
		if (flush){
			getMpmFichaApacheDAO().flush();
		}
	}
	
	@Override
	@BypassInactiveModule
	public void removerMpmMotivoIngressoCti(final MpmMotivoIngressoCti motivoIngressoCti, final boolean flush){
		getMpmMotivoIngressoCtiDAO().remover(motivoIngressoCti);
		if (flush){
			getMpmMotivoIngressoCtiDAO().flush();
		}
	}
	
	@Override
	@BypassInactiveModule
	public void removerMpmLaudo(final MpmLaudo mpmLaudo, final boolean flush) throws ApplicationBusinessException {
		this.laudoRN.removerLaudo(mpmLaudo);
		if (flush){
			getMpmLaudoDAO().flush();
		}
	}
	
	/**
	 * Método que obtém as fichas apache de um atendimento
	 * 
	 * @param atdSeq
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public List<MpmFichaApache> pesquisarFichasApachePorAtendimento(final Integer atdSeq) {
		return getMpmFichaApacheDAO().pesquisarFichasApachePorAtendimento(atdSeq);
	}
	
	/**
	 * Método que obtém os laudos não impressos de um atendimento
	 * 
	 * @param atdSeq
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public List<MpmLaudo> pesquisarLaudosNaoImpressosPorAtendimento(final Integer atdSeq) {
		return getMpmLaudoDAO().pesquisarLaudosNaoImpressosPorAtendimento(atdSeq);
	}
	
	/**
	 * Método que obtém os laudos impressos de um atendimento
	 * 
	 * @param atdSeq
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public List<MpmLaudo> pesquisarLaudosImpressosPorAtendimento(final Integer atdSeq) {
		return getMpmLaudoDAO().pesquisarLaudosImpressosPorAtendimento(atdSeq);
	}
	
	@Override
	@BypassInactiveModule
	public void removerMpmAltaSumario(final MpmAltaSumario mpmAltaSumario, final boolean flush) {
		getMpmAltaSumarioDAO().remover(mpmAltaSumario);
		if (flush){
			getMpmAltaSumarioDAO().flush();
		}
	}

	@Override
	@BypassInactiveModule
	public void verificarStatusCid(final AghCid cid) throws ApplicationBusinessException {
		
		getManterDiagnosticoAtendimentoRN().verificarStatusCid(cid);
		
	}
	
	
	private ManterDiagnosticoAtendimentoRN getManterDiagnosticoAtendimentoRN(){
		return manterDiagnosticoAtendimentoRN;
	}

	@Override
	@BypassInactiveModule
	public void validaServidorLogado() throws ApplicationBusinessException {
		this.getManterAltaSumarioRN().validaServidorLogado();
	}
	
	protected ManterSumarioSchedulerRN getManterSumarioSchedulerRN(){
		return manterSumarioSchedulerRN;
	}

	@Override
	public Long pesquisarAlterarDispensacaoDeMedicamentosCount(
			final AinLeitos leito, final Integer numeroPrescricao,
			final Date dthrDataInicioValidade, final Date dthrDataFimValidade,
			final Integer numeroProntuario, final AipPacientes paciente) {
		return getMpmPrescricaoMedicaDAO().pesquisarAlterarDispensacaoDeMedicamentosCount(leito, numeroPrescricao,
				dthrDataInicioValidade, dthrDataFimValidade,
				numeroProntuario, paciente);
	}

	@Override
	public List<MpmPrescricaoMedica> pesquisarAlterarDispensacaoDeMedicamentos(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final Boolean asc, final AinLeitos leito, final Integer numeroPrescricao,
			final Date dthrDataInicioValidade, final Date dthrDataFimValidade,
			final Integer numeroProntuario, final AipPacientes paciente) {
		return getMpmPrescricaoMedicaDAO().pesquisarAlterarDispensacaoDeMedicamentos(firstResult,
				maxResult, orderProperty, asc, leito, numeroPrescricao,
				dthrDataInicioValidade, dthrDataFimValidade,
				numeroProntuario, paciente);
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoMedica> pesquisarPrescricaoMedicaPorAtendimentoEDataFimAteDataAtual(
			final Integer atdSeq, final Date dataFim) {
		return getMpmPrescricaoMedicaDAO().pesquisarPrescricaoMedicaPorAtendimentoEDataFimAteDataAtual(atdSeq, dataFim);
	}
	
//	@Override
//	@BypassInactiveModule
//	public List<VAbsMovimentoComponente> pesquisarItensSolHemoterapicasPOL(final Integer codigoPaciente){
//		return bancoDeSangueFacade.pesquisarItensSolHemoterapicasPOL(codigoPaciente);
//	}
	
	@Override
	@BypassInactiveModule
	public PrescricaoMedicaVO buscarDadosCabecalhoContraCheque(final MpmPrescricaoMedica prescricao, final Boolean listaVaziaItens) throws BaseException {
		return getManterPrescricaoMedicaON().buscarDadosCabecalhoContraCheque(prescricao, listaVaziaItens);
	}
	
	@Override
	public Date obterDataInternacao2(final Integer seqAtendimento) throws ApplicationBusinessException{
		return getManterAltaSumarioRN().obterDataInternacao2(seqAtendimento);
	}
	
	@Override
	public String obterLocalPaciente(final Integer apaAtdSeq) throws ApplicationBusinessException{
		return getManterAltaSumarioRN().obterLocalPaciente(apaAtdSeq);
	}
	
	@Override
	public void inserirPim2(final MpmPim2 pim2) {
		getPim2RN().inserirPim2(pim2);
	}
	
	@Override
	public void atualizarPim2(final MpmPim2 pim2, final MpmPim2 pim2Old) throws ApplicationBusinessException {
		getPim2RN().atualizarPim2(pim2, pim2Old);
	}
	
	@Override
	public MpmPim2 clonarPim2(final MpmPim2 pim2) throws ApplicationBusinessException {
		return getPim2JournalRN().clonarPim2(pim2);
	}
	
	protected Pim2RN getPim2RN() {
		return pim2RN;
	}
	
	protected Pim2JournalRN getPim2JournalRN() {
		return pim2JournalRN;
	}
	
	/**
	 * @author lucasbuzzo
	 * #3476
	 * 
	 * @param  
	 * @return ManterCidUsualPorUnidadeRN 
	 */
	private ManterCidUsualPorUnidadeRN getManterCidUsualPorUnidadeRN(){
		return manterCidUsualPorUnidadeRN;
	}	
	
	/**
	 * @author lucasbuzzo
	 * #3476
	 * 
	 * @param  
	 * @return MpmCidUnidFuncionalDAO 
	 */
	private MpmCidUnidFuncionalDAO getMpmCidUnidFuncionalDAO(){
		return mpmCidUnidFuncionalDAO;
	}	
		
	/**
	 * @author lucasbuzzo
	 * #3476
	 * 
	 * @param Integer firstResult
	 * @param Integer maxResult
	 * @param String orderProperty
	 * @param boolean asc
	 * @param AghUnidadesFuncionais aghUnidadesFuncionais
	 * @return List<MpmCidUnidFuncional>
	 */
	@Override
	public List<MpmCidUnidFuncional> listaCidUnidadeFuncional(Integer firstResult,Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais aghUnidadesFuncionais) {
		
		return getMpmCidUnidFuncionalDAO().listaCidUnidadeFuncional(firstResult, maxResult, orderProperty, asc, aghUnidadesFuncionais);
	}
		
	/**
	 * @author lucasbuzzo
	 * #3476
	 * 
	 * @param Integer firstResult
	 * @param Integer maxResult
	 * @param String orderProperty
	 * @param boolean asc
	 * @param AghUnidadesFuncionais aghUnidadesFuncionais
	 * @return Integer
	 */
	@Override
	public Long listaCidUnidadeFuncionalCount(AghUnidadesFuncionais aghUnidadesFuncionais) {
		
		return getMpmCidUnidFuncionalDAO().listaCidUnidadeFuncionalCount(aghUnidadesFuncionais);
	}

	/**
	 * @author lucasbuzzo
	 * #3476
	 *	
	 * @param 
	 * @return AghCidDAO
	 * @throws ApplicationBusinessException 
	 */
	@Override
	public void persistirCidUnidadeFuncional(MpmCidUnidFuncional mpmCidUnidFuncional) throws ApplicationBusinessException {
		getManterCidUsualPorUnidadeRN().persistirCidUnidadeFuncional(mpmCidUnidFuncional);
		
	}

	
	/**
	 * @author lucasbuzzo
	 * #3476
	 *	
	 * @param MpmCidUnidFuncionalId id
	 * @return MpmCidUnidFuncional
	 */
	@Override
	public MpmCidUnidFuncional obterMpmCidUnidFuncionalPorId(MpmCidUnidFuncionalId id) {
		return getMpmCidUnidFuncionalDAO().obterPorChavePrimaria(id);
	}

	/**
	 * @author lucasbuzzo
	 * #3476
	 *	
	 * @param MpmCidUnidFuncionalId id
	 * @return MpmCidUnidFuncional
	 */
	@Override
	public void excluirMpmCidAtendimento(MpmCidUnidFuncionalId id) {
		getManterCidUsualPorUnidadeRN().remover(id);
	}
			
	/**
	 * @author heliz
	 * #15822
	 * Métodos implementados para a #15822 - Visualizar sumário de transferência do paciente
	 * na internação
	 * @param object 
	 */
	//-------------------------------------------------------------------------------------------------------
	@Override
	@BypassInactiveModule
	public Boolean verificarSumarioTransfPacInternacao(Integer atdSeq, Short seqpAltaSumario) {
		return getMpmAltaSumarioDAO().verificarSumarioTransfPacInternacao(atdSeq, seqpAltaSumario);
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaSumario> pesquisarAltaSumarioTransferencia(
			AghAtendimentos atendimento, Short seqpAltaSumario) {
		return getMpmAltaSumarioDAO().pesquisarAltaSumarioTransferencia(atendimento, seqpAltaSumario);
	}
	
	@Override
	@BypassInactiveModule
	public List<LinhaReportVO> obterOutrasEquipes(Integer apaAtdSeq,
			Integer apaSeq, Short seqp) {
		return getMpmAltaOutraEquipeSumrDAO().obterRelatorioSumario(apaAtdSeq, apaSeq, seqp);
	}
	
	protected MpmAltaOutraEquipeSumrDAO getMpmAltaOutraEquipeSumrDAO(){
		
		return mpmAltaOutraEquipeSumrDAO;
		
	}
	
	@Override
	@BypassInactiveModule
	public MpmTrfDestino obterEquipeDestino(Integer apaAtdSeq, Integer apaSeq,
			Short seqp) {
		return getMpmTrfDestinoDAO().obterEquipeDestino(apaAtdSeq, apaSeq, seqp);
	}
	
	protected MpmTrfDestinoDAO getMpmTrfDestinoDAO(){
		
		return mpmTrfDestinoDAO;
		
	}

	@Override
	@BypassInactiveModule
	public String obterMotivoTransferencia(Integer apaAtdSeq, Integer apaSeq,
			Short seqp) throws ApplicationBusinessException {
		return getMpmTrfMotivoDAO().obterMotivoTransferencia(apaAtdSeq, apaSeq, seqp);
	}
	
	protected MpmTrfMotivoDAO getMpmTrfMotivoDAO(){
		
		return mpmTrfMotivoDAO;
		
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmTrfDiagnostico> obterTransferenciaDiagPrincipais(Integer apaAtdSeq, Integer apaSeq,
			Short seqp) throws ApplicationBusinessException {
		return getMpmTrfDiagnosticoDAO().obterTransferenciaDiagPrincipais(apaAtdSeq, apaSeq, seqp);
	}
	
	protected MpmTrfDiagnosticoDAO getMpmTrfDiagnosticoDAO(){
		
		return mpmTrfDiagnosticoDAO;
		
	}
	
	@Override
	@BypassInactiveModule
	public String obterTransferenciaDiagSecundario(Integer apaAtdSeq, Integer apaSeq,
			Short seqp) throws ApplicationBusinessException {
		return getMpmTrfEscoreGravidadeDAO().obterTransferenciaDiagSecundario(apaAtdSeq, apaSeq, seqp);
	}
	
	protected MpmTrfEscoreGravidadeDAO getMpmTrfEscoreGravidadeDAO(){
		
		return mpmTrfEscoreGravidadeDAO;
		
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmAltaOtrProcedimento> obterMpmAltaOtrProcedimento(Integer apaAtdSeq, Integer apaSeq,
			Short seqp) throws ApplicationBusinessException {
		return getMpmAltaOtrProcedimentoDAO().obterMpmAltaOtrProcedimento(apaAtdSeq, apaSeq, seqp);
	}
	
	protected MpmAltaOtrProcedimentoDAO getMpmAltaOtrProcedimentoDAO(){
		
		return mpmAltaOtrProcedimentoDAO;
		
	}
	
	@Override
	@BypassInactiveModule
	public List<LinhaReportVO> obterAltaCirgRealizadas(Integer apaAtdSeq,
			Integer apaSeq, Short seqp) {
		return getMpmAltaCirgRealizadaDAO().obterRelatorioSumario(apaAtdSeq, apaSeq, seqp);
	}
	
	protected MpmAltaCirgRealizadaDAO getMpmAltaCirgRealizadaDAO(){
		
		return mpmAltaCirgRealizadaDAO;
		
	}

	@Override
	@BypassInactiveModule
	public List<LinhaReportVO> obterAltaConsultorias(Integer apaAtdSeq,
			Integer apaSeq, Short seqp) {
		return getMpmAltaConsultoriaDAO().obterRelatorioSumario(apaAtdSeq, apaSeq, seqp);
	}
	
	protected MpmAltaConsultoriaDAO getMpmAltaConsultoriaDAO(){
		
		return mpmAltaConsultoriaDAO;
		
	}

	@Override
	@BypassInactiveModule
	public List<LinhaReportVO> obterPrincFarmacos(Integer apaAtdSeq,
			Integer apaSeq, Short seqp) {
		return getMpmAltaPrincFarmacoDAO().obterRelatorioSumario(apaAtdSeq, apaSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public List<LinhaReportVO> obterComplFarmacos(Integer apaAtdSeq,
			Integer apaSeq, Short seqp) {
		return getMpmAltaComplFarmacoDAO().obterRelatorioSumario(apaAtdSeq, apaSeq, seqp);
	}
	
	protected MpmAltaComplFarmacoDAO getMpmAltaComplFarmacoDAO(){
		
		return mpmAltaComplFarmacoDAO;
		
	}

	@Override
	@BypassInactiveModule
	public String obterEvolucao(Integer apaAtdSeq, Integer apaSeq, Short seqp) {
		return getMpmAltaEvolucaoDAO().obterRelatorioSumario(apaAtdSeq, apaSeq, seqp);
	}
	
	/*@Override
	@BypassInactiveModule
	public MpmAltaEvolucao obterMpmAltaSumarioPorChavePrimaria(MpmAltaSumarioId mpmAltaSumarioId) {
		return getMpmAltaEvolucaoDAO().obterPorChavePrimaria(mpmAltaSumarioId);
	}*/
	
	protected MpmAltaEvolucaoDAO getMpmAltaEvolucaoDAO(){
		
		return mpmAltaEvolucaoDAO;
		
	}

	@Override
	@BypassInactiveModule
	public MpmAltaMotivo obterMpmAltaMotivo(Integer apaAtdSeq, Integer apaSeq,
			Short seqp) throws ApplicationBusinessException {
		return getMpmAltaMotivoDAO().obterMpmAltaMotivo(apaAtdSeq, apaSeq, seqp, null);
	}
	
	protected MpmAltaMotivoDAO getMpmAltaMotivoDAO(){
		
		return mpmAltaMotivoDAO;
		
	}	
	
	@Override
	@BypassInactiveModule
	public MpmAltaPlano obterMpmAltaPlano(Integer apaAtdSeq, Integer apaSeq,
			Short seqp) throws ApplicationBusinessException {
		return getMpmAltaPlanoDAO().obterMpmAltaPlano(apaAtdSeq, apaSeq, seqp);
	}
	
	protected MpmAltaPlanoDAO getMpmAltaPlanoDAO(){
		
		return mpmAltaPlanoDAO;
		
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaRecomendacao> obterMpmAltaRecomendacoes(Integer apaAtdSeq,
			Integer apaSeq, Short seqp, DominioSituacao situacao) {
		return getMpmAltaRecomendacaoDAO().obterMpmAltaRecomendacao(apaAtdSeq, apaSeq, seqp, situacao);
	}
	
	@Override
	@BypassInactiveModule
	public MpmAltaPlano obterMpmAltaPlanoObterPorChavePrimaria(MpmAltaSumarioId id) {
		return getMpmAltaPlanoDAO().obterPorChavePrimaria(id);
	}
	
	protected MpmAltaRecomendacaoDAO getMpmAltaRecomendacaoDAO(){
		
		return mpmAltaRecomendacaoDAO;
		
	}
	//---------------------------------------------------------------------------------------------------

	
	@Override
	@BypassInactiveModule
	public boolean existeAltaSumarioConcluidaPorAtendimento(Integer atdSeq) {
		return this.getMpmAltaSumarioDAO()
				.existeAltaSumarioConcluidaPorAtendimento(atdSeq);
	}

	@Override
	public List<AfaMedicamentoPrescricaoVO> obterListaAltaPrincFarmacoPorIndCarga(
        	Integer apaAtdSeq, Integer apaSeq, Short seqp, Boolean indCarga){
		return getMpmAltaPrincFarmacoDAO().obterListaAltaPrincFarmacoPorIndCarga(apaAtdSeq, apaSeq, seqp, indCarga);
	}
	
	@Override
	public List<MpmAltaPedidoExame> pesquisarMpmAltaPedidoExamePorZonaESala(Short unfSeq, 
			Byte sala) {
		return getMpmAltaPedidoExameDAO().pesquisarMpmAltaPedidoExamePorZonaESala(unfSeq, sala);
	}
	
	protected MpmAltaPedidoExameDAO getMpmAltaPedidoExameDAO(){
		return mpmAltaPedidoExameDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MpmDataItemSumario> listarDataItemSumario(Integer apaAtdSeq) {
		return this.getMpmDataItemSumarioDAO().listarDataItemSumario(apaAtdSeq);
	}

	@Override
	@BypassInactiveModule
	public List<RelSumarioPrescricaoVO> pesquisaGrupoDescricaoStatus(
			SumarioPrescricaoMedicaVO prescricao, boolean limitValor) {
		return this.getMpmDataItemSumarioDAO().pesquisaGrupoDescricaoStatus(
				prescricao, limitValor);
	}

	@Override
	public List<QuantidadePrescricoesDispensacaoVO> pesquisarRelatorioQuantidadePrescricoesDispensacao(
			Date dataEmissaoInicio, Date dataEmissaoFim) {
		return this.getMpmPrescricaoMedicaDAO()
				.pesquisarRelatorioQuantidadePrescricoesDispensacao(
						dataEmissaoInicio, dataEmissaoFim);
	}

	@Override
	public List<Date> executarCursorPrCr(Integer atdSeq) {
		return this.getMpmPrescricaoMedicaDAO().executarCursorPrCr(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public Boolean executarCursorPrescricao(Integer atdSeq) {
		return this.getMpmPrescricaoMedicaDAO()
				.executarCursorPrescricao(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoMedica> listarPrescricoesMedicasParaGerarSumarioDePrescricao(
			Integer seqAtendimento, Date dthrInicioPrescricao,
			Date dthrFimPrescricao) {
		return this
				.getMpmPrescricaoMedicaDAO()
				.listarPrescricoesMedicasParaGerarSumarioDePrescricao(
						seqAtendimento, dthrInicioPrescricao, dthrFimPrescricao);
	}

	//bypass pq chama na POL
	@BypassInactiveModule
	@Override
	public void atualizarMpmPrescricaoMedicaDepreciado(
			MpmPrescricaoMedica mpmPrescricaoMedica) {
		this.getMpmPrescricaoMedicaDAO().atualizar(
				mpmPrescricaoMedica);
		this.getMpmPrescricaoMedicaDAO().flush();
	} 

	@Override
	@BypassInactiveModule
	public List<MpmSolicitacaoConsultoria> obterConsultoriaAtiva(Integer seq) {
		return this.getMpmSolicitacaoConsultoriaDAO().obterConsultoriaAtiva(seq);
	}

	@Override
	public List<MpmSolicitacaoConsultoria> pesquisarSolicitacaoConsultoriaPorInternacaoOutrasEspecialidades(Integer intSeq) {
		return this.getMpmSolicitacaoConsultoriaDAO().pesquisarSolicitacaoConsultoriaPorInternacaoOutrasEspecialidades(intSeq);
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaSumario> pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(Integer atdSeq) {
		return this.getMpmAltaSumarioDAO().pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(atdSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Map<Integer, List<MpmAltaSumario>> pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(List<Integer> atdSeqs) {
		Map<Integer, List<MpmAltaSumario>> mapAltaSumario = new HashMap<Integer, List<MpmAltaSumario>>();
		Integer seq;
		List<MpmAltaSumario> list;
		List<MpmAltaSumario> listaAltaSumario = this.getMpmAltaSumarioDAO().pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(atdSeqs);
		for (MpmAltaSumario altaSumario : listaAltaSumario) {
			seq = altaSumario.getAtendimento().getSeq();
			if (mapAltaSumario.containsKey(seq)) {
				list = mapAltaSumario.get(seq);
			} else {
				list = new LinkedList<MpmAltaSumario>();
				mapAltaSumario.put(seq, list);
			}
			list.add(altaSumario);
		}//FOR
		return mapAltaSumario;
	}


	@Override
	@BypassInactiveModule
	public DominioSimNao verificarAltaSumarioObito(AghAtendimentos atendimento) {
		return this.getMpmAltaSumarioDAO().verificarAltaSumarioObito(atendimento);
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaSumario> listarAltasSumario(Integer atdSeq, DominioSituacao situacao,DominioIndTipoAltaSumarios[] tiposAltaSumario) {
		return this.getMpmAltaSumarioDAO().listarAltasSumario(atdSeq, situacao, tiposAltaSumario);
	}
 
	@Override
	@BypassInactiveModule
	public Long buscaAltaSumarioTransferenciaCount(Integer apaAtdSeq) {
		return this.getMpmAltaSumarioDAO().buscaAltaSumarioTransferenciaCount(apaAtdSeq);
	}

	@Override
	@BypassInactiveModule
	//precisa do bypass pq rotinas subsequentes usam isso
	public List executarCursorAltaSumario(Integer atdSeq) {
		return this.getMpmAltaSumarioDAO().executarCursorAltaSumario(atdSeq);
	}

	@Override
	@BypassInactiveModule
	//precisa do bypass pq rotinas subsequentes usam isso
	public List executarCursorSumario(Integer atdSeq) {
		return this.getMpmAltaSumarioDAO().executarCursorSumario(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public MpmAltaSumario obterAltaSumarioPorAtendimento(AghAtendimentos atendimento) {
		return this.getMpmAltaSumarioDAO().obterAltaSumarioPorAtendimento(atendimento);
	}

	@Override
	public List<MpmAltaSumario> listarAltasSumarioPorCodigoPaciente(Integer pacCodigo) {
		return this.getMpmAltaSumarioDAO().listarAltasSumarioPorCodigoPaciente(pacCodigo);
	}

	@Override
	public List<MpmAltaSumario> listarAltasSumarios(Integer pacCodigo, AghAtendimentos atendimento) {
		return this.getMpmAltaSumarioDAO().listarAltasSumarios(pacCodigo, atendimento);
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaSumario> obterTrfDestinoComAltaSumarioEPaciente(Integer apaAtdSeq) {
		return this.getMpmAltaSumarioDAO().obterTrfDestinoComAltaSumarioEPaciente(apaAtdSeq);
	}

	@Override
	public void persistirMpmAltaSumario(MpmAltaSumario mpmAltaSumario) {
		this.getMpmAltaSumarioDAO().persistir(mpmAltaSumario);
	}
	
	@Override
	public void atualizarMpmAltaSumario(MpmAltaSumario mpmAltaSumario) {
		this.getMpmAltaSumarioDAO().atualizar(mpmAltaSumario);
	}

	@Override
	@BypassInactiveModule
	public List<MpmTipoFrequenciaAprazamento> obterListaTipoFrequenciaAprazamentoDigitaFrequencia(Boolean listarApenasAprazamentoSemFrequencia, Object parametro) {
		return mpmTipoFrequenciaAprazamentoDAO.obterListaTipoFrequenciaAprazamentoDigitaFrequencia(listarApenasAprazamentoSemFrequencia, parametro);
	}

	protected MpmSolicitacaoConsultoriaDAO getMpmSolicitacaoConsultoriaDAO() {
		return mpmSolicitacaoConsultoriaDAO;
	}

	protected MpmDataItemSumarioDAO getMpmDataItemSumarioDAO() {
		return mpmDataItemSumarioDAO;
	}
	
	@Override
	public List<MpmAprazamentoFrequencia> listarAprazamentosFrequenciaPorTipo(
			MpmTipoFrequenciaAprazamento tipo){
		return getMpmAprazamentoFrequenciaDAO().listarAprazamentosFrequenciaPorTipo(tipo);
	}
	
	@Override
	public Date obterHoraInicialPorTipoFrequenciaAprazamento(
			AghUnidadesFuncionais unidade,
			MpmTipoFrequenciaAprazamento tipoFrequencia, Short frequencia){
		return getMpmHorarioInicAprazamentoDAO().obterHoraInicialPorTipoFrequenciaAprazamento(unidade, tipoFrequencia, frequencia);
	}

	@Override
	public List<Integer> listarSeqsCidAtendimentoPorCodigoAtendimento(
			Integer newAtdSeq) {
		return this.getMpmCidAtendimentoDAO()
				.listarSeqsCidAtendimentoPorCodigoAtendimento(newAtdSeq);
	}

	@Override
	public Short obterProximoSeqPMpmFichaApache(Integer seqAtendimento) {
		return this.getMpmFichaApacheDAO().obterProximoSeqP(seqAtendimento);
	}

	@Override
	public List<MpmListaServEquipe> pesquisarListaServidorEquipePorServidor(
			Integer matricula, Short vinCodigo, Date dataFim) {
		return this.getMpmListaServEquipeDAO()
				.pesquisarListaServidorEquipePorServidor(matricula, vinCodigo,
						dataFim);
	}

	@Override
	public List<MpmListaServEspecialidade> pesquisarListaServidorEspecialidadePorEspecialiade(
			Short seqEspecialidade, Date dataFim) {
		return this.getMpmListaServEspecialidadeDAO()
				.pesquisarListaServidorEspecialidadePorEspecialiade(
						seqEspecialidade, dataFim);
	}

	@Override
	public void removerListaServidorSumarioAltaPorAtendimento(
			Integer seqAtendimento) {
		this.getMpmListaServSumrAltaDAO()
				.removerListaServidorSumarioAltaPorAtendimento(seqAtendimento);
	}

	@Override
	public void inserirListaServidorSumarioAlta(MpmListaServSumrAltaId id) {
		this.getMpmListaServSumrAltaDAO().inserirListaServidorSumarioAlta(id);
	}

	@Override
	public String obterMensagemResourceBundle(String key) {
		return getConcluirSumarioAltaON().obterMensagemResourceBundle(key);
	}
	
	@Override
	public MpmListaServSumrAlta obterMpmListaServSumrAltaPorChavePrimaria(
			MpmListaServSumrAltaId id) {
		return this.getMpmListaServSumrAltaDAO().obterPorChavePrimaria(id);
	}

	@Override
	public List<MpmPacAtendProfissional> pesquisarPacienteAtendimentoProfissional(
			Integer seqAtendimento, Date dataFim) {
		return this.getMpmPacAtendProfissionalDAO()
				.pesquisarPacienteAtendimentoProfissional(seqAtendimento,
						dataFim);
	}

	@Override
	public List<MpmParamCalculoPrescricao> pesquisarMpmParamCalculoPrescricoes(
			Integer pesoPacCodigo, Integer alturaPacCodigo) {
		return this.getMpmParamCalculoPrescricaoDAO()
				.pesquisarMpmParamCalculoPrescricoes(pesoPacCodigo,
						alturaPacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<MpmPim2> pesquisarPim2PorAtendimentoSituacao(
			Integer seqAtendimento, DominioSituacaoPim2 situacao) {
		return this.getMpmPim2DAO().pesquisarPim2PorAtendimentoSituacao(
				seqAtendimento, situacao);
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmPim2> pesquisarPim2PorAtendimentoSituacao(
			Integer seqAtendimento, List<DominioSituacaoPim2> situacao) {
		return this.getMpmPim2DAO().pesquisarPim2PorAtendimentoSituacao(
				seqAtendimento, situacao);
	}

	@Override
	public List<MpmServidorUnidFuncional> pesquisarServidorUnidadeFuncional(
			Short seqUnidadeFuncional, Date dataFim) {
		return this
				.getMpmServidorUnidFuncionalDAO()
				.pesquisarServidorUnidadeFuncional(seqUnidadeFuncional, dataFim);
	}

	@Override
	public List<MpmPostoSaude> listarPostosSaudePorNumeroLogradouroParesEImpares(
			Integer atuSeq, Integer nroLogradouro) {
		return this.getMpmPostoSaudeDAO()
				.listarPostosSaudePorNumeroLogradouroParesEImpares(atuSeq,
						nroLogradouro);
	}

	@Override
	@BypassInactiveModule
	public MpmSumarioAlta obterMpmSumarioAltaPorChavePrimaria(Integer seq) {
		return this.getMpmSumarioAltaDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void inserirMpmFichaApacheJn(MpmFichaApacheJn mpmFichaApacheJn) {
		this.getMpmFichaApacheJnDAO().persistir(mpmFichaApacheJn);
		this.getMpmFichaApacheJnDAO().flush();
	}

	protected MpmAprazamentoFrequenciasDAO getMpmAprazamentoFrequenciaDAO() {
		return mpmAprazamentoFrequenciasDAO;
	}

	protected MpmHorarioInicAprazamentoDAO getMpmHorarioInicAprazamentoDAO() {
		return mpmHorarioInicAprazamentoDAO;
	}

	protected MpmListaServEspecialidadeDAO getMpmListaServEspecialidadeDAO() {
		return mpmListaServEspecialidadeDAO;
	}

	protected MpmListaServSumrAltaDAO getMpmListaServSumrAltaDAO() {
		return mpmListaServSumrAltaDAO;
	}
	
	protected MpmPacAtendProfissionalDAO getMpmPacAtendProfissionalDAO() {
		return mpmPacAtendProfissionalDAO;
	}
	
	protected MpmParamCalculoPrescricaoDAO getMpmParamCalculoPrescricaoDAO() {
		return mpmParamCalculoPrescricaoDAO;
	}
	
	protected MpmPim2DAO getMpmPim2DAO() {
		return mpmPim2DAO;
	}
	
	protected MpmServidorUnidFuncionalDAO getMpmServidorUnidFuncionalDAO() {
		return mpmServidorUnidFuncionalDAO;
	}
	
	protected MpmPostoSaudeDAO getMpmPostoSaudeDAO() {
		return mpmPostoSaudeDAO;
	}

	protected MpmFichaApacheJnDAO getMpmFichaApacheJnDAO() {
		return mpmFichaApacheJnDAO;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#listaUnidadeTempo(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.MpmUnidadeTempo)
	 */
	@Override
	public List<MpmUnidadeTempo> listaUnidadeTempo(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, MpmUnidadeTempo mpmUnidadeTempo) {
		return this.getMpmUnidadeTempoDAO().pesquisarUnidadeTempo(firstResult, maxResult, orderProperty, asc, mpmUnidadeTempo);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarUnidadeTempoCount(br.gov.mec.aghu.model.MpmUnidadeTempo)
	 */
	@Override
	public Long pesquisarUnidadeTempoCount(MpmUnidadeTempo mpmUnidadeTempo) {
		return this.getMpmUnidadeTempoDAO().pesquisarUnidadeTempoCount(mpmUnidadeTempo);
	}
	
	private MpmUnidadeTempoDAO getMpmUnidadeTempoDAO() {
		return mpmUnidadeTempoDAO;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterMpmUnidadeTempoPorId(java.lang.Integer)
	 */
	@Override
	public MpmUnidadeTempo obterMpmUnidadeTempoPorId(Integer seq) {
		return this.getMpmUnidadeTempoDAO().buscarUnidadeTempoPorId(seq);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#persistirMpmUnidadeTempo(br.gov.mec.aghu.model.MpmUnidadeTempo)
	 */
	@Override
	public void persistirMpmUnidadeTempo(MpmUnidadeTempo unidadeTempo) throws BaseException {
		getManterUnidadeTempoON().persistirUnidadeTempo(unidadeTempo);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#excluirUnidadeTempo(br.gov.mec.aghu.model.MpmUnidadeTempo)
	 */
	@Override
	public void excluirUnidadeTempo(Short seqMpmUnidadeTempo) throws ApplicationBusinessException {
		getManterUnidadeTempoON().excluirUnidadeTempo(seqMpmUnidadeTempo);
	}
	
	protected ManterUnidadeTempoON getManterUnidadeTempoON(){
		return manterUnidadeTempoON;
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#getListaUnidadeMedidaMedica()
	 */
	@Override
	public List<MpmUnidadeMedidaMedica> getListaUnidadeMedidaMedica() {
		return getMpmUnidadeMedidaMedicaDAO().getListaUnidadeMedidaMedicaAtivasPrescreve();
	}

	@Override
	@BypassInactiveModule
	public LocalDispensaVO obterLocalDispensaVO(Integer medMatCodigo, Integer atendimentoSeq) {
		return getMpmPrescricaoMdtoDAO().obterLocalDispensaVO(medMatCodigo, atendimentoSeq);
	}

	@Override
	public List<MpmItemPrescricaoMdto> listarItensPrescricaoMedicamentoFarmaciaMe(
			Integer atendimentoSeq, Date dthrInicio, Date dthrFim) {
		return getMpmPrescricaoMdtoDAO().listarItensPrescricaoMedicamentoFarmaciaMe(atendimentoSeq, dthrInicio, dthrFim);
	}

	@Override
	public List<LocalDispensa2VO> listarPrescricaoItemMedicamentoFarmaciaMov(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento) {
		return this.mpmItemPrescricaoMdtoDAO.listarPrescricaoItemMedicamentoFarmaciaMov(atdSeq, dthrInicio, dthrFim, unfSeq, dthrMovimento);
	}
	
	@Override
	public List<LocalDispensa2VO> listarPrescricaoMedicamentoFarmaciaMov(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento) {
		return this.vMpmPrescrMdtosDAO.listarPrescricaoMedicamentoFarmaciaMov(atdSeq, dthrInicio, dthrFim, unfSeq, dthrMovimento);
	}	
	
	@Override
	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritosConfirmadosPelaChavePrescricao(
			MpmPrescricaoMedicaId prescricao, Date dtHrFimPrescricaoMedica, Boolean isSolucao) {
		return this.getConfirmarPrescricaoMedicaON().obterListaMedicamentosPrescritosConfirmadosPelaChavePrescricao(prescricao, dtHrFimPrescricaoMedica, isSolucao);
	}

	@Override
	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritos(
			MpmPrescricaoMedicaId prescricao, Date dtHrFimPrescricaoMedica,
			Boolean isSolucao) {
		return this.getConfirmarPrescricaoMedicaON().obterListaMedicamentosPrescritos(prescricao, dtHrFimPrescricaoMedica, isSolucao);
	}

	@Override
	@BypassInactiveModule
	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			RapServidores servidorValida) throws BaseException {
		return getLaudoProcedimentoSusRN().buscaConselhoProfissionalServidorVO(servidorValida);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * listarLaudosPorAtendimentoData(java.lang.Integer, java.util.Date)
	 */
	@Override
	@BypassInactiveModule
	public List<MpmLaudo> listarLaudosPorAtendimentoData(final Integer atdSeq,
			final Date pDtDesdobr) {
		return getMpmLaudoDAO().listarLaudosPorAtendimentoData(atdSeq,
				pDtDesdobr);
	}

	@Override
	@BypassInactiveModule
	public List<MpmMotivoIngressoCti> pesquisarMotivoIngressoCtisPorAtendimento(
			AghAtendimentos atendimento) {
		return getMpmMotivoIngressoCtiDAO().pesquisarMotivoIngressoCtisPorAtendimento(atendimento);
	}
	
	@Override
	public List<MpmMotivoIngressoCti> pesquisarMotivoIngressoCtiPorAtdSeq(Integer atdSeq) {
		return getMpmMotivoIngressoCtiDAO().pesquisarMotivoIngressoCtiPorAtdSeq(atdSeq);
	}

	@Override
	public void desatachar(MpmMotivoReinternacao motivoReinternacao) {
		getMpmMotivoReinternacaoDAO().desatachar(motivoReinternacao);		
	}

	@Override
	public void desatachar(MpmPrescricaoMdto prescricaoMdto) {
		getMpmPrescricaoMdtoDAO().desatachar(prescricaoMdto);		
	}

	@Override
	public MpmMotivoReinternacao atualizarMpmMotivoReinternacao(MpmMotivoReinternacao motivoReinternacao) {
		MpmMotivoReinternacao retorno = mpmMotivoReinternacaoDAO.merge(motivoReinternacao);
		return retorno;
	}

	@Override
	public MpmMotivoReinternacao inserirMpmMotivoReinternacao(MpmMotivoReinternacao motivoReinternacao) {
		mpmMotivoReinternacaoDAO.persistir(motivoReinternacao);
		return motivoReinternacao;
	}

	@Override
	public void removerMpmMotivoReinternacao(MpmMotivoReinternacao motivoReinternacao) {
		getMpmMotivoReinternacaoDAO().remover(motivoReinternacao);
	}

	@Override
	public List<MpmMotivoReinternacao> pesquisarMpmMotivoReinternacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MpmMotivoReinternacao motivoReinternacao) {		
		return getMpmMotivoReinternacaoDAO().pesquisar(firstResult, maxResult, orderProperty, asc, motivoReinternacao);		
	}

	@Override
	public Long pesquisarMpmMotivoReinternacaoCount(MpmMotivoReinternacao motivoReinternacao) {
		return getMpmMotivoReinternacaoDAO().pesquisarCount(motivoReinternacao);
	}

	@Override
	@BypassInactiveModule
	public String obterNomePosto(Integer codigoAreaAtuacao,
			Integer numeroLogradouro, Integer opcao) {
		return getMpmPostoSaudeDAO().obterNomePosto(codigoAreaAtuacao, numeroLogradouro, opcao);
	}
	
	private MpmPrescricaoNptDAO getMpmPrescricaoNptDAO() {
		return mpmPrescricaoNptDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoNpt> obterItensPrescricaoNpt(Integer atdSeq,
			Integer prescricaoMedicaSeq, Date dataInicio, Date dataFim) {
		return getMpmPrescricaoNptDAO().obterItensPrescricaoNpt(atdSeq, prescricaoMedicaSeq, dataInicio, dataFim);
	}

	@Override
	@BypassInactiveModule
	public List<ItemAlteracaoNptVO> pesquisarAlteracoesNpt(Integer atdSeq,
			Integer pmeSeq, Date pmeDthrIniMvto) {
		return getMpmPrescricaoNptDAO().pesquisarAlteracoesNpt(atdSeq, pmeSeq, pmeDthrIniMvto);
	}

	@Override
	@BypassInactiveModule
	public MpmPrescricaoNpt obterNutricaoParentalTotalPeloId(Integer atdSeq,
			Integer seq) {
		return getMpmPrescricaoNptDAO().obterNutricaoParentalTotalPeloId(atdSeq, seq);
	}

	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoProcedimento> obterItensPrescricaoProcedimento(
			Integer atdSeq, Integer prescricaoMedicaSeq, Date dataInicio,
			Date dataFim) {
		return getMpmPrescricaoProcedimentoDAO().obterItensPrescricaoProcedimento(atdSeq, prescricaoMedicaSeq, dataInicio, dataFim);
	}

	@Override
	@BypassInactiveModule
	public List<ItemAlteracaoNptVO> pesquisarProcedimentosEspeciaisDiversos(
			Integer atdSeq, Integer pmeSeq, Date pmeDthrIniMvto) {
		return getMpmPrescricaoProcedimentoDAO().pesquisarProcedimentosEspeciaisDiversos(atdSeq, pmeSeq, pmeDthrIniMvto);
	}
	
	@Override
	public List<MpmListaServResponsavel> pesquisarServidorResponsavel(final RapServidores servidorResp, final Date dataFim) {
		return getMpmListaServResponsavelDAO().pesquisarServidorResponsavel(servidorResp, dataFim);
	}


	@Override
	@BypassInactiveModule
	public MpmPrescricaoProcedimento obterPrescricaoProcedimentoPorId(Long seq,
			Integer atdSeq) {
		return getMpmPrescricaoProcedimentoDAO().obterPrescricaoProcedimentoPorId(seq, atdSeq);
	}

	@Override
	public MpmPrescricaoNpt obterPrescricaoNptPorChavePrimaria(MpmPrescricaoNptId id) {
		return getMpmPrescricaoNptDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void desatacharMpmProcedEspecialDiversos(
			MpmProcedEspecialDiversos mpmProcedEspecialDiversos) {
		getMpmProcedEspecialDiversoDAO().desatachar(mpmProcedEspecialDiversos);
	}	

	@Override
	@BypassInactiveModule
	public List<MpmSolicitacaoConsultHist> obterHistoricoConsultoria(Integer atdSeq) {
		return getMpmSolicitacaoConsultHistDAO().obterHistoricoConsultoria(atdSeq);
	}
	
	@Override
	public MpmParamCalculoPrescricao obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(Integer atdSeq) {
		return getMpmParamCalculoPrescricaoDAO().obterParamCalculoPrescricoesCriadasHojePeloAtendimentoESituacao(atdSeq, DominioSituacao.A);
	}
	
	@Override
	public void inicializarCadastroPesoAltura(Integer atdSeq, DadosPesoAlturaVO vo) {
		getParametroCalculoPrescricaoON().inicializarCadastroPesoAltura(atdSeq, vo);
	}

	@Override
	public BigDecimal calculaSC(Boolean pacientePediatrico, BigDecimal peso, BigDecimal altura) {
		return getParametroCalculoPrescricaoON().calculaSC(pacientePediatrico, peso, altura);
	}
	
	@Override
	public void atualizarDadosPesoAltura(Integer pacCodigo, Integer atdSeq, BigDecimal peso, DominioTipoMedicaoPeso tipoMedicaoPeso, BigDecimal altura, DominioTipoMedicaoPeso tipoMedicaoAltura,
			BigDecimal sc, BigDecimal scCalculada, DadosPesoAlturaVO vo) 
	throws ApplicationBusinessException{
		getParametroCalculoPrescricaoON().atualizar(pacCodigo, atdSeq, peso, tipoMedicaoPeso, altura, tipoMedicaoAltura, sc, scCalculada, vo);
	}
	
	@Override
	public List<MpmUnidadeMedidaMedica> getListaUnidadeMedidaMedicaAtivasPeloMedFmDosagPeloCodigoOuDescricao(Integer matCodigo) {
		return getMpmUnidadeMedidaMedicaDAO().getListaUnidadeMedidaMedicaAtivasPeloMedFmDosagPeloCodigoOuDescricao(matCodigo);
	}

	@Override
	public Boolean possuiDadosPesoAlturaDia(Integer atdSeq) {
		return getParametroCalculoPrescricaoON().possuiDadosPesoAlturaDia(atdSeq);
	}
	
	@Override
	public Object[] calculoDose(Short frequencia, Short tfqSeq, BigDecimal qtdParamCalculo, DominioUnidadeBaseParametroCalculo baseParamCalculo, 
			DominioTipoCalculoDose tipoCalculoDose, Integer duracao, DominioDuracaoCalculo unidadeTempo, BigDecimal peso,
			BigDecimal altura, BigDecimal sc) {
		return getPrescricaoMedicamentoRN().calculoDose(frequencia, tfqSeq, qtdParamCalculo, baseParamCalculo, tipoCalculoDose, duracao, unidadeTempo, peso, altura, sc);
	}
	
	protected ParametroCalculoPrescricaoON getParametroCalculoPrescricaoON() {
		return parametroCalculoPrescricaoON;
	}
	
	protected MpmSolicitacaoConsultHistDAO getMpmSolicitacaoConsultHistDAO(){		
		return mpmSolicitacaoConsultHistDAO;		
	}
	
	protected MpmListaServResponsavelDAO getMpmListaServResponsavelDAO() {
		return mpmListaServResponsavelDAO;
	}
	
	protected MamPcIntItemParadaDAO getMamPcIntItemParadaDAO(){
		return mamPcIntItemParadaDAO;
	}
	
	protected MamPcIntParadaDAO getMamPcIntParadaDAO(){
		return mamPcIntParadaDAO;
	}
	
	@BypassInactiveModule
	public List<MamPcIntItemParada> pesquisarItemParadaPorAtendimento(AghAtendimentos atendimento) {
		return getMamPcIntItemParadaDAO()
		.pesquisarItemParadaPorAtendimento(atendimento);

	}
	
	@BypassInactiveModule
	public List<MpmPim2> pesquisarPim2PorAtendimento(Integer seqAtendimento, DominioSituacaoPim2 situacao){
		return getMpmPim2DAO().pesquisarPim2PorAtendimento(seqAtendimento, situacao);
	}
	
	@BypassInactiveModule
	public List<MamPcIntParada> pesquisarPcIntParadaPorAtendimento(AghAtendimentos atendimento){
		return getMamPcIntParadaDAO()
		.pesquisarPcIntParadaPorAtendimento(atendimento);
	}
	
	@Override
	@BypassInactiveModule
	public List<MamPcIntParada> pesquisarParadaInternacaoPorAtendimento(Integer atendimento){
		return getMamPcIntParadaDAO().pesquisarParadaInternacaoPorAtendimento(atendimento);
	}

	@Override
	@BypassInactiveModule
	public MpmTipoFrequenciaAprazamento obterTipoFrequeciaAprazPorChavePrimaria(Short icsTfqSeq) {
		return getMpmTipoFrequenciaAprazamentoDAO().obterPorChavePrimaria(icsTfqSeq);
	}
	
	protected MamPcSumExameTabDAO getMamPcSumExameTabDAO() {
		return mamPcSumExameTabDAO;
	}
	
	@Override
	@BypassInactiveModule
	public MamPcSumExameTab obterSumarioExameTabela(Integer atdSeq, Date dthrCriacao) {
		return getMamPcSumExameTabDAO().obterSumarioExameTabela(atdSeq, dthrCriacao);
	}
	
	@Override
	@BypassInactiveModule
	public List<MamPcIntItemParada> pesquisarItemParadaPorAtendimento(Integer atdSeq, Date dthrCriacao, Float ordem, Boolean isOrdemMaiorIgual) {
		return getMamPcIntItemParadaDAO().pesquisarItemParadaPorAtendimento(atdSeq, dthrCriacao, ordem, isOrdemMaiorIgual);
	}
	
	@Override
	@BypassInactiveModule
	public List<MamPcSumExameMasc> pesquisarSumarioExamesMasc(Integer atdSeq, Date dthrCriacao, Integer pacCodigo, Boolean recemNascido) {
		return getMamPcSumExameMascDAO().pesquisarSumarioExameMasc(atdSeq, dthrCriacao, pacCodigo, recemNascido);
	}
	
	@Override
	@BypassInactiveModule
	public List<MamPcControlePac> pesquisarControlePaciente(Integer atdSeq, Date dthrCriacao) {
		return getMamPcControlePacDAO().pesquisarControlePaciente(atdSeq, dthrCriacao);
	}
	
	protected MamPcControlePacDAO getMamPcControlePacDAO() {
		return mamPcControlePacDAO;
	}
	
	protected MamPcSumExameMascDAO getMamPcSumExameMascDAO() {
		return mamPcSumExameMascDAO;
	}
	
	protected MamPcSumMascLinhaDAO getMamPcSumMascLinhaDAO() {
		return mamPcSumMascLinhaDAO;
	}
	
	protected MamPcSumObservacaoDAO getMamPcSumObservacaoDAO() {
		return mamPcSumObservacaoDAO;
	}
	
	protected MamPcSumMascCampoEditDAO getMamPcSumMascCampoEditDAO() {
		return mamPcSumMascCampoEditDAO;
	}
	
	@Override
	@BypassInactiveModule
	public List<MamPcSumExameTab> pesquisarSumarioExameTabela (Integer atdSeq, Date dthrCriacao, Integer pacCodigo, Boolean recemNascido) {
		return getMamPcSumExameTabDAO().pesquisarSumarioExameTabela(atdSeq, dthrCriacao, pacCodigo, recemNascido);
	}
	
	@Override
	@BypassInactiveModule
	public List<MamPcSumMascLinha> pesquisarSumarioMascLinha(Integer atdSeq, Date dthrCriacao, Integer ordemRelatorio) {
		return getMamPcSumMascLinhaDAO().pesquisarSumarioMascLinha(atdSeq, dthrCriacao, ordemRelatorio);
	}	
	
	@Override
	@BypassInactiveModule
	public MamPcSumObservacao obterSumarioObservacao(Integer atdSeq, Date dthrCriacao, Integer pacCodigo, DominioSumarioExame pertenceSumario, Date dthrEvento, Boolean recemNascido) {
		return getMamPcSumObservacaoDAO().obterSumarioObservacao(atdSeq, dthrCriacao, pacCodigo, pertenceSumario, dthrEvento, recemNascido);
	}
	
	@Override
	@BypassInactiveModule
	public MamPcSumMascCampoEdit obterSumarioMascCampoEdit(Integer atdSeq, Date dthrCriacao, Integer nroLinha, Integer ordemRelatorio) {
		return getMamPcSumMascCampoEditDAO().obterSumarioMascCampoEdit(atdSeq, dthrCriacao, nroLinha, ordemRelatorio);
	}

	@Override
	public Long obterNomeExamesCount(final Object objPesquisa) {
		return this.getManterAltaItemPedidoExameON().obterNomeExamesCount(objPesquisa);
	}

	@Override
	@BypassInactiveModule
	public List<AltaObitoSumarioVO> pesquisarAltaObitoSumariosAtdSeqSituacaoIndConcluido(
			Integer atdSeq, DominioSituacao dominioSituacao, DominioIndConcluido dominioIndConcluido) {
		return this.getMpmAltaSumarioDAO().pesquisarAltaObitoSumariosAtdSeqSituacaoIndConcluido(atdSeq,
						dominioSituacao, dominioIndConcluido);
	}
	
	@Override
	public List<CidAtendimentoVO> pesquisarCidAtendimentoEmAndamentoOrigemInternacaoPorPacCodigo(Integer pacCodigo) {
		return getMpmCidAtendimentoDAO().pesquisarCidAtendimentoEmAndamentoOrigemInternacaoPorPacCodigo(pacCodigo);
	}
	
	@Override
	@BypassInactiveModule
	public MpmPim2 obterMpmPim2PorChavePrimaria(Long seqPim2) {
		return getMpmPim2DAO().obterPorChavePrimaria(seqPim2);
	}

	
	@Override
	public List<MpmAltaDiagSecundario> buscaAltasSecundariosPorAtendimento(AghAtendimentos atendimento) {
		return getMpmAltaDiagSecundarioDAO().buscaAltasSecundariosPorAtendimento(atendimento);
	}
	
	@Override
	public MpmAltaDiagPrincipal buscaAltaPrincipalPorAtendimento(AghAtendimentos atendimento) {
		return getMpmAltaDiagPrincipalDAO().buscaAltaPrincipalPorAtendimento(atendimento);
	}

	@Override
	public MpmItemPrescricaoMdto obterItemMedicamentoNaoDiluente(List<MpmItemPrescricaoMdto> listaItens){
		return getItemPrescricaoMedicamentoRN().obterItemMedicamentoNaoDiluente(listaItens);
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmAltaTriagem> pesquisarAltaTriagemPeloMpmAltaSumarioId(
			MpmAltaSumarioId altaSumarioId) {
		return getMpmAltaTriagemDAO().pesquisarAltaTriagemPeloMpmAltaSumarioId(altaSumarioId);
	}
	
	private MpmAltaTriagemDAO getMpmAltaTriagemDAO() {
		return mpmAltaTriagemDAO;
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmAltaTrgSinalVital> pesquisarAltaTrgSinalVitalPorMpmAltaSumarioIdEAltaTriagemSeqp(
			Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp) {
		return getMpmAltaTrgSinalVitalDAO().pesquisarAltaTrgSinalVitalPorMpmAltaSumarioIdEAltaTriagemSeqp(apaAtdSeq, apaSeq, seqp, altaTriagemSeqp);
	}
	
	private MpmAltaTrgSinalVitalDAO getMpmAltaTrgSinalVitalDAO() {
		return mpmAltaTrgSinalVitalDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MamItemReceituario> listarItemReceituarioPorAltaSumario(
			Integer atdSeq, Integer apaSeq, Short seqp) {
		return getMpmAltaSumarioDAO().listarItemReceituarioPorAltaSumario(atdSeq, apaSeq, seqp);
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmAltaTrgMedicacao> pesquisarAltaTrgMedicacaoPorMpmAltaSumarioIdEAltaTriagemSeqp(
			Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp) {
		return getMpmAltaTrgMedicacaoDAO().pesquisarAltaTrgMedicacaoPorMpmAltaSumarioIdEAltaTriagemSeqp(apaAtdSeq, apaSeq, seqp, altaTriagemSeqp);
	}
	
	private MpmAltaTrgMedicacaoDAO getMpmAltaTrgMedicacaoDAO() {
		return mpmAltaTrgMedicacaoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaTrgExame> pesquisarAltaTrgExamePorMpmAltaSumarioIdEAltaTriagemSeqp(
			Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp) {
		return getMpmAltaTrgExameDAO().pesquisarAltaTrgExamePorMpmAltaSumarioIdEAltaTriagemSeqp(apaAtdSeq, apaSeq, seqp, altaTriagemSeqp);
	}
	
	private MpmAltaTrgExameDAO getMpmAltaTrgExameDAO() {
		return mpmAltaTrgExameDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaTrgAlergia> pesquisarAltaTrgAlergiaPorMpmAltaSumarioIdEAltaTriagemSeqp(
			Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp) {
		return getMpmAltaTrgAlergiaDAO().pesquisarAltaTrgAlergiaPorMpmAltaSumarioIdEAltaTriagemSeqp(apaAtdSeq, apaSeq, seqp, altaTriagemSeqp);
	}
	
	private MpmAltaTrgAlergiaDAO getMpmAltaTrgAlergiaDAO() {
		return mpmAltaTrgAlergiaDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaAtendimento> pesquisarAltaAtendimentoPeloMpmAltaSumarioId(
			MpmAltaSumarioId altaSumarioId) {
		return getMpmAltaAtendimentoDAO().pesquisarAltaAtendimentoPeloMpmAltaSumarioId(altaSumarioId);
	}
	
	private MpmAltaAtendimentoDAO getMpmAltaAtendimentoDAO() {
		return mpmAltaAtendimentoDAO;
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmAltaAtendMotivo> pesquisarAltaAtendMotivoPorMpmAltaSumarioIdEAltaAtendSeqp(
			MpmAltaSumarioId altaSumarioId, Integer altaAtendSeqp) {
		return getMpmAltaAtendMotivoDAO().pesquisarAltaAtendMotivoPorMpmAltaSumarioIdEAltaAtendSeqp(altaSumarioId, altaAtendSeqp);
	}
	
	private MpmAltaAtendMotivoDAO getMpmAltaAtendMotivoDAO() {
		return mpmAltaAtendMotivoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaAtendRegistro> pesquisarAltaAtendRegistroPorMpmAltaSumarioIdEAltaAtendSeqp(
			MpmAltaSumarioId altaSumarioId, Integer altaAtendseqp) {
		return getMpmAltaAtendRegistroDAO().pesquisarAltaAtendRegistroPorMpmAltaSumarioIdEAltaAtendSeqp(altaSumarioId, altaAtendseqp);
	}
	
	@Override
	@BypassInactiveModule
	public MpmAltaEstadoPaciente obterMpmAltaEstadoPacientePorChavePrimaria(MpmAltaSumarioId id) {
		return getMpmAltaEstadoPacienteDAO().obterPorChavePrimaria(id);
	}
	
	private MpmAltaAtendRegistroDAO getMpmAltaAtendRegistroDAO() {
		return mpmAltaAtendRegistroDAO;
	}
	
	private MpmAltaEstadoPacienteDAO getMpmAltaEstadoPacienteDAO() {
		return mpmAltaEstadoPacienteDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaImpDiagnostica> listarAltaImpDiagnosticaPorIdSemSeqp(
			Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		return getMpmAltaImpDiagnosticaDAO().listarAltaImpDiagnosticaPorIdSemSeqp(asuApaAtdSeq, asuApaSeq, asuSeqp);
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaPrincExame> listarMpmAltaPrincExamePorIdSemSeqpIndImprime(
			Integer apaAtdSeq, Integer apaSeq, Short seqp) {
		return getMpmAltaPrincExameDAO().listarMpmAltaPrincExamePorIdSemSeqpIndImprime(apaAtdSeq, apaSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaPrincFarmaco> obterMpmAltaPrincFarmaco(
			Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos) throws ApplicationBusinessException {
		return getMpmAltaPrincFarmacoDAO().obterMpmAltaPrincFarmaco(altanAtdSeq, altanApaSeq, altanAsuSeqp, apenasAtivos);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarAltaTriagemPeloMpmAltaSumarioIdCount(
			MpmAltaSumarioId idAltaSumario) {
		return getMpmAltaTriagemDAO().pesquisarAltaTriagemPeloMpmAltaSumarioIdCount(idAltaSumario);
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaConsultoria> obterListaAltaConsultoriaPeloAltaSumarioId(
			MpmAltaSumarioId altaSumarioId) {
		return getMpmAltaConsultoriaDAO().obterListaAltaConsultoriaPeloAltaSumarioId(altaSumarioId);
	}

	@Override
	@BypassInactiveModule
	public List<MpmAltaRespostaConsultoria> pesquisarAltaRespostaConsultoriaPorMpmAltaSumarioIdEAltaConsultoriaSeqp(
			Integer apaAtdSeq, Integer apaSeq, Short seqp, Short altaConsultoriaSeqp) {
		return getMpmAltaRespostaConsultoriaDAO().pesquisarAltaRespostaConsultoriaPorMpmAltaSumarioIdEAltaConsultoriaSeqp(apaAtdSeq, apaSeq, seqp, altaConsultoriaSeqp);
	}
	
	private MpmAltaRespostaConsultoriaDAO getMpmAltaRespostaConsultoriaDAO() {
		return mpmAltaRespostaConsultoriaDAO;
	}

	@Override
	@BypassInactiveModule
	public MpmAltaEvolucao obterMpmAltaEvolucao(
			Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		return getMpmAltaEvolucaoDAO().obterMpmAltaEvolucao(asuApaAtdSeq, asuApaSeq, asuSeqp);
	}
	@Override
	@BypassInactiveModule
	public MpmFichaApache pesquisarFichaApacheComEscalaGrasgows(Integer atdSeq, Short seqp) {
		return getMpmFichaApacheDAO().pesquisarFichaApacheComEscalaGrasgows(atdSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public Integer calcularPontuacaoFichaApache(MpmFichaApache fichaApche) {
		return getVisualizacaoFichaApacheON().calcularPontuacaoFichaApache(fichaApche);
	}
	
	private VisualizacaoFichaApacheON getVisualizacaoFichaApacheON() {
		return visualizacaoFichaApacheON;
	}
	
	@Override
	@BypassInactiveModule
	public List<MpmProcedEspecialRm> listarProcedimentosRmAtivosPeloPedSeq(Short pedSeq){
		return this.getMpmProcedEspecialRmDAO().listarProcedimentosRmAtivosPeloPedSeq(pedSeq);
	}
	
	private MpmProcedEspecialRmDAO getMpmProcedEspecialRmDAO(){
		return mpmProcedEspecialRmDAO;	
	}
	
	@Override
	public List<MpmProcedEspecialRm> listarProcedimentosRmPeloPedSeq(Short pedSeq) {
		return getMpmProcedEspecialRmDAO().listarProcedimentosRmPeloPedSeq(pedSeq);
	}
	
	@Override
	public List<NutricaoParenteralVO> buscarNutricoesParenteraisPrescritas(Integer atdSeq, Date dataInicioProcessamento, Date dataFimProcessamento) {
		return getMpmPrescricaoMedicaDAO().buscarNutricoesParenteraisPrescritas(atdSeq, dataInicioProcessamento, dataFimProcessamento);
	}

	@Override
	public MpmPrescricaoMedica obterPrescricaoComFatConvenioSaude(
			Integer atdSeqPrescricao, Integer seqPrescricao) {
		return this.getPrescricaoMedicaON().obterPrescricaoComFatConvenioSaude(atdSeqPrescricao, seqPrescricao);
	}

	@Override
	public List<MpmPrescricaoMedica> obterMpmPrescricaoMedicaPorAghAtendimento(final Integer seqAtendimento) {
		return mpmPrescricaoMedicaDAO.obterMpmPrescricaoMedicaPorAghAtendimento(seqAtendimento);
	}
	
	
	@Override
	public List<MpmPrescricaoMedica>  obterPrescricaoTecnicaPorAtendimentoOrderCriadoEm(final Integer atdSeq){
		return mpmPrescricaoMedicaDAO.obterPrescricaoTecnicaPorAtendimentoOrderCriadoEm(atdSeq);
		
	}
	
	public MpmTipoFrequenciaAprazamento getMpmTipoFrequenciaAprazamentoPorId(Short seq){
		return this.getMpmTipoFrequenciaAprazamentoDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public List<MpmPrescricaoMedicaVO> listarPrescricaoMedicaSituacaoDispensacao(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final String leitoId, final Integer prontuario, final String nome,
			final Date dtHrInicio, final Date dtHrFim,
			final String seqPrescricao, final Integer seqPrescricaoInt, Boolean indPacAtendimento, Boolean indPmNaoEletronica) throws ApplicationBusinessException {
		return getConsultaSitDispMedicON()
				.listarPrescricaoMedicaSituacaoDispensacao(firstResult,
						maxResult, orderProperty, asc, leitoId, prontuario,
						nome, dtHrInicio, dtHrFim, seqPrescricao, seqPrescricaoInt, indPacAtendimento, indPmNaoEletronica);
	}

	@Override
	public Long listarPrescricaoMedicaSituacaoDispensacaoCount(
			final String leitoId, final Integer prontuario, final String nome,
			final Date dtHrInicio, final Date dtHrFim,
			final String seqPrescricao, final Integer seqPrescricaoInt, Boolean indPacAtendimento, Boolean indPmNaoEletronica) throws ApplicationBusinessException {
		return getConsultaSitDispMedicON()
				.listarPrescricaoMedicaSituacaoDispensacaoCount(leitoId,
						prontuario, nome, dtHrInicio, dtHrFim, seqPrescricao, seqPrescricaoInt, indPacAtendimento, indPmNaoEletronica);
	}

	private ConsultaSitDispMedicON getConsultaSitDispMedicON() {
		return consultaSitDispMedicON;
	}
	
	
	@Override
	@BypassInactiveModule
	public List<MpmSolicitacaoConsultoria> pesquisarConsultoriaEnfermagemResposta(Integer atdSeq) {
		return getMpmSolicitacaoConsultoriaDAO().pesquisarConsultoriaEnfermagemResposta(atdSeq);
	}
	
	@BypassInactiveModule
	public Boolean verificarExisteConsultoriaEnfermagemResposta(Integer atdSeq) {
	    return getMpmSolicitacaoConsultoriaDAO().verificarExisteConsultoriaEnfermagemResposta(atdSeq);
	}	

	@Override
	@BypassInactiveModule
	public List<MpmSolicitacaoConsultoria> pesquisarConsultoriaEnfermagemSolicitada(Integer atdSeq) {
		return getMpmSolicitacaoConsultoriaDAO().pesquisarConsultoriaEnfermagemSolicitada(atdSeq);
	}
	
	@BypassInactiveModule
	public Boolean verificarExisteConsultoriaEnfermagemSolicitada(Integer atdSeq) {
		return getMpmSolicitacaoConsultoriaDAO().verificarExisteConsultoriaEnfermagemSolicitada(atdSeq);
	}	

	@Override
	@BypassInactiveModule
	public List<MpmSolicitacaoConsultoria> pesquisarSolicitacaoConsultoriaAtivaEnfermagem(Integer atdSeq) {
		return getMpmSolicitacaoConsultoriaDAO().pesquisarSolicitacaoConsultoriaAtivaEnfermagem(atdSeq);
	}
	
	@BypassInactiveModule
	public Boolean verificarExisteSolicitacaoConsultoriaAtivaEnfermagem(Integer atdSeq) {
		return getMpmSolicitacaoConsultoriaDAO().verificarExisteConsultoriaEnfermagemResposta(atdSeq);
	}	
	
	/**
	 * #38994 - Serviço que retorna altas por numero da consulta
	 * @param conNumero
	 * @return
	 */
	@Override
	public MpmAltaSumario pesquisarAltaSumariosPorNumeroConsulta(Integer conNumero) {
		return getMpmAltaSumarioDAO().pesquisarAltaSumariosPorNumeroConsulta(conNumero);
			
	}
	
	/**
	 * #39002 - Busca Ultima Prescricao Medica
	 * @param atdSeq
	 * @return
	 */
	@Override
	public MpmPrescricaoMedica obterPrescricaoMedicaPorAtendimento(Integer atdSeq){
		return getMpmPrescricaoMedicaDAO().obterPrescricaoMedicaPorAtendimento(atdSeq);
	}
	
	/**
	 * #39007 - Serviço que retorna alta sumario por atendimento
	 * @param atdSeq
	 * @return
	 */
	@Override
	public MpmAltaSumario obterMpmAltaSumarioPorAtendimento(Integer atdSeq){
		return getMpmAltaSumarioDAO().obterMpmAltaSumarioPorAtendimento(atdSeq);
	}
	
	/**
	 * #39010 - Busca alta de sumário concluído
	 * @param atdSeq
	 * 
	 * @return
	 */
	@Override
	public MpmAltaSumario obterMpmAltaSumarioConcluidoPorAtendimento(Integer atdSeq){
		return getMpmAltaSumarioDAO().obterMpmAltaSumarioConcluidoPorAtendimento(atdSeq);
	}
	
	/**
	 * #39012 - Serviço para atualizar Sumario Alta apagando dados alta
	 * @param atdSeq
	 * @param nomeMicromputador
	 * @throws ApplicationBusinessException 
	 * @throws Exception 
	 */
	@Override
	public void atualizarSumarioAltaApagarDadosAlta(final Integer atdSeq, String nomeMicromputador) throws ApplicationBusinessException{
		getSumarioAltaON().atualizarSumarioAltaApagarDadosAlta(atdSeq, nomeMicromputador);
	}
	
	
	/**
	 * #39013 - Serviço que estorna alta sumario
	 * @param seqp
	 * @param atdSeq
	 * @param apaSeq
	 * @param nomeMicrocomputador
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void atualizarAltaSumarioEstorno(Short seqp, Integer atdSeq, Integer apaSeq, String nomeMicrocomputador) throws ApplicationBusinessException{
		getManterAltaSumarioRN().atualizarAltaSumarioEstorno(seqp, atdSeq, apaSeq, nomeMicrocomputador);
	}
	
	/**
	 * #39018  #39019 #39020 #39021 #39022 #39023 #39014 #39015 #39016
	 * Serviço que desbloqueia sumario alta
	 * @param atdSeq
	 * @param apaSeq
	 * @param seqp
	 * @param nomeMicrocomputador
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void desbloquearSumarioAlta(Integer atdSeq, Integer apaSeq, Short seqp, String nomeMicrocomputador) throws ApplicationBusinessException{
		getSumarioAltaRN().desbloquearSumarioAlta(atdSeq, apaSeq, seqp, nomeMicrocomputador);
	}
	
	@Override
	public List<MpmPrescricaoMedica> pesquisaPrescricoesMedicasPorAtendimento(Integer seqAtendimento) {
		return getPrescricaoMedicaDAO().pesquisaPrescricoesMedicasPorAtendimento(seqAtendimento);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade#listarMensagensPendentes(java.lang.Integer)
	 */
	@Override
	public List<CentralMensagemVO> listarMensagensPendentes(Integer atdSeq) {

		return getPrescricaoMedicaON().listarMensagensPendentes(atdSeq);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade#obterPacienteCentralMensagens(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public MpmPrescricaoMedica obterPacienteCentralMensagens(Integer atdSeq, Integer prescricaoSeq) {

		return getMpmPrescricaoMedicaDAO().obterPacienteCentralMensagens(atdSeq, prescricaoSeq);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade#obterDetalhesJustificativaUsoMedicamento(java.lang.Integer)
	 */
	@Override
	public String obterDetalhesJustificativaUsoMedicamento(Integer jumSeq) throws ApplicationBusinessException {

		return mpmJustificativaUsoMdtoRN.obterDetalhesJustificativaUsoMedicamento(jumSeq);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade#gerarPdfSinan(java.lang.Integer)
	 */
	@Override
	public GerarPDFSinanVO gerarPdfSinan(Integer atdSeq) throws ApplicationBusinessException {

		return mpmNotificacaoTbRN.gerarPdfSinan(atdSeq);
	}

	protected SumarioAltaRN getSumarioAltaRN(){
		return this.sumarioAltaRN;
	}

	protected MpmPrescricaoMedicaDAO getPrescricaoMedicaDAO() {
		return prescricaoMedicaDAO;
	}

	public MpmJustificativaUsoMdtoDAO getMpmJustificativaUsoMdtoDAO() {
		return mpmJustificativaUsoMdtoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoMedica> pesquisarPrescricoesMedicaNaoPendentes(
			Integer atdSeq,
			DominioSituacaoPrescricao situacaoPrescricao) {
		return getMpmPrescricaoMedicaDAO().pesquisarPrescricoesMedicaNaoPendentes(atdSeq, situacaoPrescricao);
	}

	@Override
	public RapServidores obterServidorCriacaoAltaSumario(
			Integer seqAtendimento) {
		return this.getLaudoProcedimentoSusON().obterServidorCriacaoLaudoProcedimentoSus(seqAtendimento);
	}
	
	@Override
	@BypassInactiveModule
	public Long obterListaTipoFrequenciaAprazamentoDigitaFrequenciaCount(Boolean listarApenasAprazamentoSemFrequencia, Object parametro) {
		return getMpmTipoFrequenciaAprazamentoDAO().obterListaTipoFrequenciaAprazamentoDigitaFrequenciaCount(listarApenasAprazamentoSemFrequencia, parametro);
	}
	@Override
	@BypassInactiveModule
	public List<MpmTipoFrequenciaAprazamento> obterListaTipoFrequenciaAprazamento(String parametro) {
		return getMpmTipoFrequenciaAprazamentoDAO().obterListaTipoFrequenciaAprazamento(parametro);
	}

	@BypassInactiveModule
	@Override
	public Long obterListaTipoFrequenciaAprazamentoCount(String parametro) {
		return getMpmTipoFrequenciaAprazamentoDAO().obterListaTipoFrequenciaAprazamentoCount(parametro);
	}
	
	@Override
	public List<JustificativaMedicamentoUsoGeralVO> obterListaMedicamentosUsoRestritoPorAtendimento(Integer atdSeq, String indAntiMicrobiano, Boolean indQuimioterapico, Short gupSeq) {
		return this.vMpmPrescrPendenteDAO.obterListaMedicamentosUsoRestritoPorAtendimento(atdSeq, indAntiMicrobiano, indQuimioterapico, gupSeq);
	}
	
	@Override
	public List<JustificativaMedicamentoUsoGeralVO> obterMedicamentosIndicesRestritosPorAtendimento(Integer atdSeq, Short paramGupTb) {
		return this.vMpmPrescrPendenteDAO.obterMedicamentosIndicesRestritosPorAtendimento(atdSeq, paramGupTb);
	}
	
	@Override
	public List<AipPacientes> pesquisarPacientePorAtendimento(Integer atdSeq){
		return aipPacientesDAO.pesquisarPacientePorAtendimento(atdSeq);
	}

	@Override
	public MpmJustificativaUsoMdto persistirMpmJustificativaUsoMdto(MpmJustificativaUsoMdto justificativa,
			List<JustificativaMedicamentoUsoGeralVO> medicamentos) throws ApplicationBusinessException {
		return this.mpmJustificativaUsoMdtoON.persistirMpmJustificativaUsoMdto(justificativa, medicamentos);
	}
	
	@Override
	public MpmPrescricaoMdto obterMpmPrescricaoMdtoPorChavePrimaria(Object pk, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getMpmPrescricaoMdtoDAO().obterPorChavePrimaria(pk, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
		
	@Override
	public MpmSolicitacaoConsultoria obterMpmSolicitacaoConsultoriaPorIdComPaciente(Integer atdSeq, Integer seq) {
		return mpmSolicitacaoConsultoriaDAO.obterMpmSolicitacaoConsultoriaPorIdComPaciente(atdSeq, seq);
	}
	
	@Override
	public void inserirRespostasConsultoria(List<MpmRespostaConsultoria> respostas, DominioFinalizacao indFinalizacao) throws ApplicationBusinessException, BaseListException {
		this.mpmRespostaConsultoriaRN.inserir(respostas, indFinalizacao);	
	}
	
	@Override
	public List<MpmTipoRespostaConsultoria> pesquisarTiposRespostasConsultoria(DominioIndConcluidaSolicitacaoConsultoria indConcluida) {
		return mpmRespostaConsultoriaRN.pesquisarTiposRespostasConsultoria(indConcluida); 
	}
	
	@Override
	public VisualizaDadosSolicitacaoConsultoriaVO obterDadosSolicitacaoConsultoria(Integer atdSeq, Integer seq) throws ApplicationBusinessException {
		return this.visualizaDadosSolicitacaoConsultoriaON.obterDadosSolicitacaoConsultoria(atdSeq, seq);
	}
	
	@Override
	public String pesquisarRespostasConsultoriaPorAtdSeqConsultoria(Integer atdSeq, Integer scnSeq, Integer ordem) {
		return respostasConsultoriaON.pesquisarRespostasConsultoriaPorAtdSeqConsultoria(atdSeq, scnSeq, ordem);
	}
	
	@Override
	public RelatorioEstatisticaProdutividadeConsultorVO pesquisarRelatorioEstatisticaProdutividadeConsultor(Short espSeq, Date dtInicio, Date dtFim) throws ApplicationBusinessException{
		return relatorioEstatisicaProdutividadeConsultorON.pesquisarRelatorioEstatisticaProdutividadeConsultor(espSeq, dtInicio, dtFim);
	}

	@Override
	public List<RetornoConsultoriaVO> pesquisarRetornoConsultorias(
			Integer atdSeq, Integer scnSeq) throws ApplicationBusinessException {		
		return retornoConsultoriaON.pesquisarRetornoConsultorias(atdSeq, scnSeq);
}
	@Override
	public boolean validarMesmoProfissionalPorVinculoEMatricula(RapServidoresVO profissionalDe, RapServidoresVO profissionalPara) {
		return mpmPacAtendProfissionalRN.validarMesmoProfissionalPorVinculoEMatricula(profissionalDe, profissionalPara);
	}	
	
	@Override
	public Integer transferirPacienteEmAcompanhamento(RapServidoresVO profissionalDe, RapServidoresVO profissionalPara) throws ApplicationBusinessException {
		return mpmPacAtendProfissionalRN.transferirPacienteEmAcompanhamento(profissionalDe, profissionalPara);
	}

	@Override
	public void persistirPacAtendProfissional(MpmPacAtendProfissional mpmPacAtendProfissional, RapServidores servidorPara) throws ApplicationBusinessException {
		mpmPacAtendProfissionalRN.persistirPacAtendProfissional(mpmPacAtendProfissional, null);
	}	
	
	@Override
	public boolean verificaMpmPacAtendProfissionalCadastrado(MpmPacAtendProfissional mpmPacAtendProfissional) throws ApplicationBusinessException {
		return mpmPacAtendProfissionalRN.verificaMpmPacAtendProfissionalCadastrado(mpmPacAtendProfissional);	
	}

	@Override
	public List<ConsultoriasInternacaoVO> formatarColecaoRelatorioConsultorias(List<ConsultoriasInternacaoVO> colecao,
			DominioSituacaoConsultoria situacaoFiltro) {
		return relatorioListaConsultoriaON.formatarColecaoRelatorioConsultorias(colecao, situacaoFiltro);
	}	
	
	@Override
	public MpmPrescricaoMedicaVO pesquisaPrescricaoMedicaPorAtendimentoESeq(Integer atdSeq, Integer seq) {
		return mpmPrescricaoMedicaDAO.pesquisaPrescricaoMedicaPorAtendimentoESeq(atdSeq, seq);
	}
	
	@Override
	public List<SolicitacaoConsultoriaVO> pesquisaSolicitacaoConsultoriaPorAtendimentoEDataInicioEDataFim(Integer atdSeq, Date dthrInicio, Date dthrFim){
		return mpmSolicitacaoConsultoriaDAO.pesquisaSolicitacaoConsultoriaPorAtendimentoEDataInicioEDataFim(atdSeq, dthrInicio, dthrFim);
	}
	
	@Override
	public List<ConsultaTipoComposicoesVO> pesquisarTiposComposicoesNPT(AfaTipoComposicoes afaTipoComposicoes){
		return  afaTipoComposicoesRN.pesquisarTiposComposicaoNPT(afaTipoComposicoes);
	}
	
	@Override
	public List<ConsultaCompoGrupoComponenteVO> pesquisarListaGrupoComposicoesNPT(Short seqSelecionado){
		return  afaTipoComposicoesRN.pesquisarListaGrupoComposicoesNPT(seqSelecionado);
	}
	
	@Override
	public void removerAfaTipoComposicoes(Short seq) throws ApplicationBusinessException{
		afaTipoComposicoesRN.removerAfaTipoComposicoes(seq);
	}
	@Override
	public void gravarAfaTipoComposicoes(AfaTipoComposicoes afaTipoComposicoes) throws ApplicationBusinessException{
		afaTipoComposicoesRN.gravarAfaTipoComposicoes(afaTipoComposicoes);
	}
	
	@Override
	public void alterarAfaTipoComposicoes(AfaTipoComposicoes afaTipoComposicoes) throws ApplicationBusinessException{
		afaTipoComposicoesRN.alterarAfaTipoComposicoes(afaTipoComposicoes);
	}
	@Override
	public List<AfaGrupoComponenteNpt> pesquisarGrupoComponenteAtivo(final Object pesquisa){
		return afaGrupoComponenteNptsDAO.pesquisarGrupoComponenteAtivo(pesquisa);
	}
	@Override
	public AfaGrupoComponenteNpt obterGrupoComponente(final Short seq){
		return afaGrupoComponenteNptsDAO.obterGrupoComponente(seq);
	}
	@Override
	public void adicionarGrupoComponentesAssociados(Short seqGrupo, Short seqTipoComposicao, boolean ativoGrupo) throws ApplicationBusinessException{
		afaComposGrupoComponentesRN.adicionarGrupoComponentesAssociados(seqGrupo, seqTipoComposicao, ativoGrupo);
	}
	@Override
	public void alterarGrupoComponentesAssociados(Short seqGrupo, Short seqTipoComposicao, boolean ativoGrupo) throws ApplicationBusinessException{
		afaComposGrupoComponentesRN.alterarGrupoComponentesAssociados(seqGrupo, seqTipoComposicao, ativoGrupo);
	}
	@Override
	public void removerGrupoComponentesAssociados(ConsultaCompoGrupoComponenteVO listaGrupoComponenteSelecionado){
		afaComposGrupoComponentesRN.removerGrupoComponentesAssociados(listaGrupoComponenteSelecionado);
	}
	
	@Override
	public List<AfaGrupoComponenteNptVO> obterListaGrupoComponentes(Short seq, DominioSituacao situacao, String descricao){
		return afaGrupoComponenteNptDAO.obterListaGrupoComponentes(seq,situacao,descricao);
	}
	
	@Override
	public void validaAlterarAtivo(Boolean ativo,Short seq ) throws ApplicationBusinessException{
		afaTipoComposicoesON.validaAlterarAtivo(ativo, seq);
	}
	@Override
	public void inserirGrupoComponentes(AfaGrupoComponenteNptVO item){
		prescricaoMedicamentoRN.inserirGrupoComponentes(item);
	}
	@Override
	public boolean verificarPrimeiraEntradaUnidadeFuncional(Integer ateSeq) throws ApplicationBusinessException{
		return prescricaoMedicaRN.verificarPrimeiraEntradaUnidadeFuncional(ateSeq);
	}

	@Override
	public void inserirCompoGrupoComponentes(AfaCompoGrupoComponente item, AfaTipoComposicoes tipo, AfaGrupoComponenteNptVO grupo) throws ApplicationBusinessException{
		prescricaoMedicamentoRN.inserirCompoGrupoComponentes(item,tipo,grupo);
	}
	
	@Override
	public void alterarCompoGrupoComponentes(AfaCompoGrupoComponenteVO item, AfaTipoComposicoes tic, AfaGrupoComponenteNptVO grupo) throws ApplicationBusinessException{
		prescricaoMedicamentoRN.alterarCompoGrupoComponentes(item,tic,grupo);
	}
	
	@Override
	public void removerGrupoComponentes(AfaGrupoComponenteNptVO item) throws ApplicationBusinessException{
		prescricaoMedicamentoRN.removerGrupoComponentes(item);
	}	
	
	@Override
	public void removerCompo(AfaCompoGrupoComponenteVO item) throws ApplicationBusinessException{
		AfaCompoGrupoComponenteId id = new AfaCompoGrupoComponenteId();
		id.setGcnSeq(item.getGcnSeq());
		id.setTicSeq(item.getTicSeq());
		afaCompoGrupoComponenteDAO.removerPorId(id);
	}
	
	@Override
	public void gravarFormularioSinam(SinamVO vo) throws ApplicationBusinessException {
		this.cadastroSinamRN.gravarFormularioSinam(vo);
	}
	@Override
	public List<AfaCompoGrupoComponenteVO> obterListaCompoGrupo(Short gcnSeq){
		return afaCompoGrupoComponenteDAO.obterListaCompoGrupo(gcnSeq);
	}
	
	@Override
	public List<AfaTipoComposicoes> obterListaSuggestionTipoComposicoes(String strPesquisa, List<AfaCompoGrupoComponenteVO> lista) {
		return afaTipoComposicoesDAO.obterListaSuggestion(strPesquisa,lista);
	}
	
	@Override
	public List<VMpmMdtosDescr> obterSuggestionMedicamento(String strPesquisa) {
		return vMpmMdtosDescrDAO.obterSuggestionMedicamento(strPesquisa);
	}
	
	@Override
	public List<AfaGrupoComponenteNpt> obterSuggestionGrupoComponentes(String strPesquisa) {
		 return afaGrupoComponenteNptDAO.obterSuggestionGrupoComponentes(strPesquisa);
	}
	
	
	@Override
	public MpmUnidadeMedidaMedica obterUnidadeMedica(Integer seq) {
		 return mpmUnidadeMedidaMedicaDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public List<MpmUnidadeMedidaMedica> obterSuggestionUnidade(String strPesquisa) {
		 return mpmUnidadeMedidaMedicaDAO.obterSuggestionUnidade(strPesquisa);
	}
	
	@Override
	public AfaTipoComposicoes obterTipoPorSeq(Short seq){
		return afaTipoComposicoesDAO.obterTipoPorSeq(seq);
	}	

	@Override
	public AghAtendimentos obterAtendimentoPorSeq(final Integer seq) {
		return atendimentoDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public Long listarJustificativaNPTCount(Short seq, final String descricao, final DominioSituacao situacao) {
		return mpmJustificativaNPTDAO.listarJustificativaNPTCount(seq, descricao, situacao);
	}
	
	@Override
	public void persistir(MpmEscoreSaps3 sap) {
		escoreSaps3RN.persistir(sap);
	}
	
	@Override
	public List<MpmEscoreSaps3> pesquisarEscorePendentePorAtendimento(Integer atdSeq){
		return mpmEscoreSaps3DAO.pesquisarEscorePendentePorAtendimento(atdSeq);
	}
	
	@Override
	public MpmEscoreSaps3 obterEscoreSaps3(Integer seq) {
		return mpmEscoreSaps3DAO.pesquisarEscorePorSeq(seq);
	}

		@Override
	public List<MpmJustificativaNpt> listarJustificativasNPT(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final Short seq, final String descricao, final DominioSituacao situacao) {
		return mpmJustificativaNPTDAO.listarJustificativaNPT(firstResult, maxResult, orderProperty, asc, seq, descricao, situacao);
	}
	
	@Override
	public MpmJustificativaNpt obterMpmJustificativaNPToPorChavePrimaria(final Short seq) {
		return mpmJustificativaNPTDAO.obterMpmJustificativaNPToPorChavePrimaria(seq);
	}
	
	
	@Override
	public void persistirMpmJustificativasNPT(MpmJustificativaNpt justificativaNpt) throws BaseException {
		prescricaoMedicaON.persistirMpmJustificativasNPT(justificativaNpt);
	}

	@Override
	public List<AfaMensCalculoNpt> listarMensagensCalculoNpt(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AfaMensCalculoNpt filtro){
		return afaMensCalculoNptDAO.listarMensagensCalculoNpt(firstResult, maxResult, orderProperty, asc, filtro);

	}

	@Override
	public void persistirNotificacaoTuberculostatico(SinamVO vo) throws ApplicationBusinessException {
		this.cadastroSinamRN.persistirNotificacaoTuberculostatico(vo);
	}
	
	@Override
	public void persistirMpmControlPrevAltas(MpmControlPrevAltas controlPrevAltas){
		this.controlPrevAltasON.persistir(controlPrevAltas);
	}
	
	@Override
	public SinamVO obterNotificacaoTuberculostatica(Integer ntbSeq) {
		return this.cadastroSinamRN.obterNotificacaoTuberculostatica(ntbSeq);
	}

	@Override
	public Long listarMensagensCalculoNptCount(AfaMensCalculoNpt filtro){
		return afaMensCalculoNptDAO.listarMensagensCalculoNptCount(filtro);
	}

	@Override
	public void excluirMensagemCalculoNpt(AfaMensCalculoNpt item) {
		prescricaoMedicaRN.excluirMensagemCalculoNpt(item);
	}

	@Override
	public void salvarMensagemCalculoNpt(AfaMensCalculoNpt item, RapServidores servidorLogado) throws BaseException, ApplicationBusinessException {
		prescricaoMedicaRN.salvarMensagemCalculoNpt(item, servidorLogado);
	}

	@Override
	public RapServidores obterHintAfaMensCalculoNpt(AfaMensCalculoNpt item) {
		return rapServidoresDAO.obterHintAfaMensCalculoNpt(item);
	}
	
	@Override
	public void atualizarEscoreSaps3(MpmEscoreSaps3 item) {
		this.escoreSaps3RN.atualizar(item);
	}

	@Override
	public List<AfaFormulaNptPadrao> obterFormulaNptPadrao(AfaFormulaNptPadrao filtro){
		return afaFormulaNptPadraoDAO.montaPesquisa(filtro);
	}
	@Override
	public void excluiFormulaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao) throws ApplicationBusinessException{
		afaFormulaNtpPadraoRN.excluiFormulaNptPadrao(afaFormulaNptPadrao);
	}
		
	@Override
	public AfaTipoVelocAdministracoes obterTipoVelocidadeAdministracaoPorSeqTipoVelocAdministracao(Short id){
		return afaTipoVelocAdministracoesDAO.carregarPorChavePrimaria(id);
	}
	
	@Override
	public void atualizarComposicao(AfaComposicaoNptPadrao afaComposicaoNptPadrao){
		afaComposicaoNptPadraoRN.atualizar(afaComposicaoNptPadrao);
	}
	
	@Override
	public AfaItemNptPadrao obterItemNptPadrao(AfaItemNptPadraoId id){
		return afaItemNptPadraoDAO.obterPorChavePrimaria(id);
	}	
	
	@Override
	public void excluiItemNptPadrao(AfaItemNptPadraoId id) throws ApplicationBusinessException{
		afaItemNptPadraoRN.excluiItemNptPadrao(id);
	}
	
	@Override
	public AfaComponenteNpt obterComponentePorId(Integer id){
		return afaComponenteNptDAO.obterPorChavePrimaria(id);
	}
	
	
	@Override
	public VMpmDosagem obterVMpmDosagemPorId(Integer id){
		return vMpmDosagemDAO.obterPorChavePrimaria(id);
	}
	
	@Override
	public void salvarComponente(AfaItemNptPadrao afaItemNptPadraoAdd){
		afaItemNptPadraoDAO.persistir(afaItemNptPadraoAdd);
	}
	
	@Override
	public void atualizarItemNptPadrao(AfaItemNptPadrao afaItemNptPadraoAdd){
		afaItemNptPadraoRN.atualizar(afaItemNptPadraoAdd);
	}
	
	@Override
	public AfaItemNptPadrao obterAfaItemNptPadraoPorId(AfaItemNptPadraoId afaItemNptPadraoId){
		return afaItemNptPadraoDAO.obterPorChavePrimaria(afaItemNptPadraoId);
	}
	
	@Override
	public AfaFormaDosagem obterAfaFormaDosagemPorId(Integer id){
		return afaFormaDosagemDAO.obterPorChavePrimaria(id);
	}	
	@Override
	public Double calcularObito(Short pontosSaps,Double somaObito, Double multiObito, Double subObito){
		return this.escoreSaps3RN.calcularObito(pontosSaps,somaObito,multiObito,subObito);
	}
	
	@Override
	public void gravarComponenteNpt(AfaComponenteNpt entity, Integer matCodigo, Short gcnSeq) throws ApplicationBusinessException{
		prescricaoMedicaRN.gravarComponenteNpt(entity,matCodigo,gcnSeq);
	}
	@Override
	public void gravarParamComponenteNpt(AfaParamComponenteNpt entity) throws ApplicationBusinessException{
		prescricaoMedicaRN.gravarParamComponenteNpt(entity);
	}
	
	@Override
	public void gravarCasaComponenteNpt(AfaDecimalComponenteNpt entity) throws ApplicationBusinessException{
		prescricaoMedicaRN.gravarCasaComponenteNpt(entity);
	}
	
	@Override
	public void removerParamComponenteNpt(AfaParamComponenteNpt entity) throws ApplicationBusinessException{
		prescricaoMedicaRN.removerParamComponenteNpt(entity);
	}
	
	@Override
	public void removerCasaComponenteNpt(AfaDecimalComponenteNpt entity) throws ApplicationBusinessException{
		prescricaoMedicaRN.removerCasaComponenteNpt(entity);
	}
	
	@Override
	public void atualizarParamComponenteNpt(AfaParamComponenteNpt entity) throws ApplicationBusinessException{
		prescricaoMedicaRN.atualizarParamComponenteNpt(entity);
	}
	
	@Override
	public void atualizarCasaComponenteNpt(AfaDecimalComponenteNpt entity) throws ApplicationBusinessException{
		prescricaoMedicaRN.atualizarCasaComponenteNpt(entity);
	}
	
	@Override
	public void removerComponenteNpt(ComponenteNPTVO selecionado) throws ApplicationBusinessException{
		prescricaoMedicaRN.removerComponenteNpt(selecionado);
	}
	
	
	@Override
	public void atualizarComponenteNpt(AfaComponenteNpt entity, Integer matCodigo, Short gcnSeq)  throws ApplicationBusinessException{
		prescricaoMedicaRN.atualizarComponenteNpt(entity,matCodigo,gcnSeq);
	}
	
	@Override
	public AfaComponenteNpt obterComponenteNptPorChave(Integer seq){
		return afaComponenteNptDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public AfaMedicamento obterMedicamento(Integer seq){
		return afaMedicamentoDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public List<AfaParamComponenteNpt> listarComponentesPorMatCodigo(Integer cod){
		return afaParamComponenteNptDAO.listarPorMatCodigo(cod);
	}
	
	@Override
	public Short obterParamComponenteCount(Integer componenteSeq){
		return afaParamComponenteNptDAO.obterParamComponenteCount(componenteSeq);
	}
	
	@Override
	public Short obterCasaComponenteCount(Integer componenteSeq){
		return afaDecimalComponenteNptDAO.obterCasaComponenteCount(componenteSeq);
	}
	
	@Override
	public List<AfaDecimalComponenteNpt> listarCasasDecimaisPorMatCodigo(Integer cod){
		return afaDecimalComponenteNptDAO.listarPorMatCodigo(cod);
	}	
	@Override	
	public Long popularResultadosParamSaps3(String parametro, Integer atdSeq){
		return this.escoreSaps3RN.popularResultadosParametrosSaps3(parametro,atdSeq);
	}
	
	@Override
	public String salvarDiagPacCti(PrescricaoMedicaVO prescricaoMedicaVO, String complemento, AghCid aghCid, Integer pmeSeqAtendimento) throws ApplicationBusinessException{
		return this.mpmMotivoIngressoCtiRN.salvarDiagPacCti(prescricaoMedicaVO, complemento, aghCid, pmeSeqAtendimento);
	}
	
	
	//#5795
	@Override
	public InformacoesPacienteAgendamentoPrescribenteVO obterInformacoesPacienteAgendamentoPrescribenteVO(Integer seq) {
		return mpmInformacaoPrescribentesDAO.obterInformacoesPacienteAgendamentoPrescribenteVO(seq);
	}

	@Override
	public MpmInformacaoPrescribente obterMpmInformacaoPrescribentePorChavePrimaria(Integer seq) {
		return mpmInformacaoPrescribentesDAO.obterMpmInformacaoPrescribentePorId(seq);
	}

	@Override
	public RapServidores obterServidorPorUsuario(String login) throws ApplicationBusinessException {
		return registroColaboradorFacade.obterServidorPorUsuario(login);
	}

	@Override
	public void validarAlteracaoMpmInformacaoPrescribentes(MpmInformacaoPrescribente novo, MpmInformacaoPrescribente old) throws ApplicationBusinessException {
		prescricaoMedicaON.validarAlteracaoMpmInformacaoPrescribentes(novo, old);
	}

	@Override
	public void validarInsercaoMpmInformacaoPrescribentes(MpmInformacaoPrescribente novo) throws ApplicationBusinessException {
		prescricaoMedicaON.validarInsercaoMpmInformacaoPrescribentes(novo);
	}
	
	@Override
	public VerificarDadosItensJustificativaPrescricaoVO mpmpVerDadosItens(Integer atdSeq) throws ApplicationBusinessException {
		return prescreverItemMdtoON.mpmpVerDadosItens(atdSeq);
	}
	
	/**
	 * obtém Lista de mat_Codigo do Procedimento Especial.
	 * @param pedSeq
	 * @return
	 */
	@Override
	public List<Integer> buscarListaMatCodigoProcedEspecial(Short pedSeq){
		return mpmProcedEspecialRmDAO.listarProcedimentosRmPeloPedSeqII(pedSeq);
	}
	
	/**
	 * obtém AghAtendimentos
	 * @param Seq - AghAtendimento
	 * @return
	 */
	@Override
	public AghAtendimentos buscarAghAtendimento(Integer seqAtendimento){
		return atendimentoDAO.obterAtendimentoPeloSeq(seqAtendimento);
	}
	
	/**
	 * obtém AghAtendimentos com FccCentroCusto
	 * @param Seq - AghAtendimento
	 * @return
	 */
	@Override
	public AghAtendimentos buscarAghAtendimentoComCentroDeCusto(Integer seqAtendimento){
		return atendimentoDAO.obterAtendimentoPeloSeqComFccCentroCusto(seqAtendimento);
	}
	
	/**
	 * obtém MpmCidAtendimento
	 * @param Seq - AghAtendimento
	 * @return
	 */
	@Override
	public List<MpmCidAtendimento> buscarListaMpmCidAtendimento(Integer seqAtendimento){
		return mpmCidAtendimentoDAO.listarCidAtendimentoPorAtdSeq(seqAtendimento);
	}
	
	/**
	 * obtém Impressora de destino da RM
	 * @ORADB RMSP_GERA_RM_PRCS - C2
	 * @param TipoDocumentoImpressao
	 * @return
	 */
	public List<AghImpressoraPadraoUnids> buscarImpressoraDestinoRM(TipoDocumentoImpressao tipoDocumentoImpressao){
		return aghImpressoraPadraoUnidsDAO.listarImpressorasPorTipoDocumentoImpressao(tipoDocumentoImpressao);
	}
	
	/**
	 * obtém lista de SceEstoqueAlmoxarifadoRM
	 * @ORADB RMSP_GERA_RM_PRCS - C3
	 * @param pedSeqProcEspRm, matCodigoProcEspRm
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public List<SceEstoqueAlmoxarifado> buscarsceEstoqueAlmoxarifado(Short pedSeqProcEspRm, Integer matCodigoProcEspRm) throws ApplicationBusinessException{
		return sceEstoqueAlmoxarifadoDAO.buscarsceEstoqueAlmoxarifadoPorPedSeqeMatCodigo(pedSeqProcEspRm, matCodigoProcEspRm);
	}
	
	@Override
	public List<AfaFormulaNptPadrao> listarFormulaNptPadrao(){
		return afaFormulaNptPadraoDAO.listarFormulaNptPadrao();
	}

	@Override
	public AfaComposicaoNptPadrao obterComposicaoNptPadrao(AfaComposicaoNptPadraoId id){
		return afaComposicaoNptPadraoDAO.obterPorChavePrimaria(id);
	}
	
	@Override
	public void excluiComposicaoNptPadrao(AfaComposicaoNptPadrao afaComposicaoNptPadrao) throws ApplicationBusinessException{
		afaComposicaoNptPadraoRN.excluiComposicaoNptPadrao(afaComposicaoNptPadrao);
	}
	
	@Override
	public VMpmDosagem obterVMpmDosagemPorAfaItem(AfaItemNptPadrao afaItemNptPadrao){
		return vMpmDosagemDAO.obterVMpmDosagemPorAfaItem(afaItemNptPadrao);
	}
	
	@Override
	public void persistirFormulaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao,RapServidores rapServidores){
		manterFormulaNptPadraoRN.criaAfaForumlaNptPadrao(afaFormulaNptPadrao, rapServidores);
	}
	
	@Override
	public void atualizarFormulaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao,RapServidores rapServidores){
		manterFormulaNptPadraoRN.atualizarAfaForumlaNptPadrao(afaFormulaNptPadrao, rapServidores);
	}
	
	@Override
	public	AfaFormulaNptPadrao obterFormulaNptPadraoPorPk(Short seq){
		return afaFormulaNptPadraoDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public List<AfaTipoComposicoes> pesquisaAfaTipoComposicoesPorFiltro(Object filtro){
		return afaTipoComposicoesDAO.pesquisaAfaTipoComposicoesPorFiltro(filtro);
	}
	
	@Override
	public long pesquisaAfaTipoComposicoesPorFiltroCount(Object filtro){
		return afaTipoComposicoesDAO.pesquisaAfaTipoComposicoesPorFiltroCount(filtro);
	}
	
	@Override
	public List<AfaTipoVelocAdministracoes> pesquisaAfaTipoVelocAdministracoesAtivos(Object objeto){
		return afaTipoVelocAdministracoesDAO.pesquisaAfaTipoVelocAdministracoesAtivos(objeto);
	}
	
	@Override
	public long pesquisaAfaTipoVelocAdministracoesAtivosCount(Object objeto){
		return afaTipoVelocAdministracoesDAO.pesquisaAfaTipoVelocAdministracoesAtivosCount(objeto);
	}
	
	@Override
	public List<TipoComposicaoComponenteVMpmDosagemVO> pesquisaComponenteVinculadoComposicaoFormula(short seqAfaTipoComposicoes,Object objeto){
		return afaItemNptPadraoDAO.pesquisaComponenteVinculadoComposicaoFormula(seqAfaTipoComposicoes,objeto);
	}
	
	@Override
	public long pesquisaComponenteVinculadoComposicaoFormulaCount(short seqAfaTipoComposicoes,Object objeto){
		return afaItemNptPadraoDAO.pesquisaComponenteVinculadoComposicaoFormulaCount(seqAfaTipoComposicoes,objeto);
	}
	
	@Override
	public List<VMpmDosagem> pesquisarVMpmDosagemPorfiltro(Integer medMatCodigo,Object objeto) {
		return vMpmDosagemDAO.pesquisarVMpmDosagemPorfiltro(medMatCodigo, objeto);
	}
	
	@Override
	public long pesquisarVMpmDosagemPorfiltroCount(Integer medMatCodigo,Object objeto) {
		return vMpmDosagemDAO.pesquisarVMpmDosagemPorfiltroCount(medMatCodigo, objeto);
	}
	
	@Override
	public void salvarComposicao(AfaComposicaoNptPadrao afaComposicaoNptPadrao) {
		afaComposicaoNptPadraoDAO.persistir(afaComposicaoNptPadrao);
	}
	
	@Override
	public String montarLocalizacaoPaciente(Integer seqAghAtendimento) throws ApplicationBusinessException{
		return manterAltaSumarioRN.obterLocalPaciente(seqAghAtendimento);
	}
	
	@Override
	public CodAtendimentoInformacaoPacienteVO buscarCodigoInformacoesPaciente(Integer seq) {
		return mpmInformacaoPrescribentesDAO.buscarCodigoInformacoesPaciente(seq);
	}
	
	@Override
	public MpmPrescricaoMedica obterValoresPrescricaoMedica(Integer atdSeqPme, Integer seqPme) {
		return mpmPrescricaoMedicaAuxiliarDAO.obterValoresPrescricaoMedica(atdSeqPme, seqPme);
	}
	
	@Override
	public List<MpmJustificativaNpt> pesquisarJustificativaNptPorDescricao(
			String param) {
		return mpmJustificativaNPTDAO.pesquisarJustificativaNptPorDescricao(param);
	}

	@Override
	public Long pesquisarJustificativaNptPorDescricaoCount(String param) {
		return mpmJustificativaNPTDAO.pesquisarJustificativaNptPorDescricaoCount(param);
	}


	@Override
	public Boolean isFormulaPadraoOuLivre(Short seq) {
		return prescricaoNptON.isFormulaPadraoOuLivre(seq);
	}

	@Override
	public String montarMensagemPrescricaoNpt(Integer atdSeq) {
		return prescricaoNptON.montarMensagemPrescricao(atdSeq);
	}

	@Override
	public AfaFormulaNptPadrao obterFormulaPediatricao(Integer atdSeq) {
		return prescricaoNptON.obterFormulaPediatricao(atdSeq);
	}

	@Override
	public void persistirPrescricaoNpt(Short fnpSeq, String descricaoFormula,
			MpmJustificativaNpt justificativa,
			PrescricaoMedicaVO prescricaoMedicaVO,
			MpmPrescricaoNptVO prescricaoNptVO, String nomeMicrocomputador,
			Boolean desconsiderarItensNulos)
			throws ApplicationBusinessException {
		prescricaoNptON.persistirPrescricaoNpt(fnpSeq, descricaoFormula, justificativa, prescricaoMedicaVO, prescricaoNptVO, nomeMicrocomputador, desconsiderarItensNulos);
		
	}

	@Override
	public boolean temComposicoesComponentesNulos(MpmPrescricaoNptVO vo) {
		return prescricaoNptON.temComposicoesComponentesNulos(vo);
	}

	@Override
	public void excluirPrescricaoNpt(MpmPrescricaoNpt prescricaoNpt,
			String nomeComputador) throws ApplicationBusinessException {
		prescricaoNptRN.excluir(prescricaoNpt, nomeComputador);		
	}

	@Override
	public void atualizarPrescricaoNpt(MpmPrescricaoNpt prescricao,
			String nomeMicrocomputador, Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		prescricaoNptRN.atualizar(prescricao, nomeMicrocomputador, dataFimVinculoServidor);		
	}

	@Override
	public MpmPrescricaoNptVO buscarDadosPrescricaoNpt(Integer atdSeq,
			Integer pnpSeq) {
		return prescricaoNptON.buscarDadosPrescricaoNpt(atdSeq, pnpSeq);
	}

	@Override
	public List<MpmComposicaoPrescricaoNptVO> buscarComposicoesNptDescritaPorId(
			Integer seq, Integer atdSeq) {
		return mpmComposicaoPrescricaoNptDAO.buscarComposicoesNptDescritaPorId(seq, atdSeq);
	}

	@Override
	public List<MpmItemPrescricaoNptVO> buscarComponentesComposicaoNptDescritaPorId(
			Integer cptPnpAtdSeq, Integer cntPnpSeq, Short cptSeqp) {
		return mpmItemPrescricaoNptDAO.buscarComponentesComposicaoNptDescritaPorId(cptPnpAtdSeq, cntPnpSeq, cptSeqp);
	}

	@Override
	public MpmJustificativaNpt obterJustificativaPorChavePrimaria(Short seq) {
		return mpmJustificativaNPTDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public void excluirPrescricaoNptPorItem(ItemPrescricaoMedicaVO item, String nomeComputador) throws ApplicationBusinessException {
		prescricaoNptRN.excluir(item, nomeComputador);		
	}

	@Override
	public String descricaoFormatadaNpt(MpmPrescricaoNpt prescricaoNpts) {
		return manterPrescricaoMedicaON.descricaoFormatadaNpt(prescricaoNpts);
	}	
	
	@Override
	public List<ListaPacientePrescricaoVO>  obterListaDePacientes(Integer matricula, Short vinCodigo) {
		return aghAtendimentoPacientesDAO.obterListaDePacientes(matricula, vinCodigo);
	}
	
	@Override
	public DadosDialiseVO obterCaminhoDialise(Integer atdSeq, String nomeMicrocomputador) throws BaseException {
		return this.listaPacientesInternadosDialiseON.obterCaminhoDialise(atdSeq, nomeMicrocomputador);
	}
	
	@Override
	public List<AfaComposicaoNptPadraoVO> obterListaComposicaoNptPadraoVO(Short seq){
		return afaComposicaoNptPadraoDAO.pesquisaAfaFormulaNptPadraoPorFiltros(seq);
	}

	@Override
	public MpmModeloBasicoProcedimento obterModeloBasicoProcedimentoPorChavePrimaria(
			MpmModeloBasicoProcedimentoId id, boolean left, Enum<?>... fields) {
		return getMpmModeloBasicoProcedimentoDAO().obterPorChavePrimaria(id, left, fields);
	}

	@Override
	public List<AfaItemNptPadraoVO> obterListaAfaItemNptPadrao(Short composicaoSeqp, Short composicaoFnpSeq) {
		return afaItemNptPadraoDAO.pesquisaAfaItemNptPadraoPorFiltro(composicaoSeqp, composicaoFnpSeq);
	}
	
	@Override
	public List<AfaItemNptPadraoVO> obterListaAfaItemNptPadraoOrder(Short composicaoSeqp, Short composicaoFnpSeq) {
		return afaItemNptPadraoDAO.pesquisaAfaItemNptPadraoPorFiltroOrder(composicaoSeqp, composicaoFnpSeq);
	}
	
	
	@Override
	public Integer obterIdadePaciente(Integer codPaciente) {
		return prescricaoMedicaON.obterIdadePaciente(codPaciente);
	}
	
	@Override
	public String obterNomeUsualPacitente(Integer matricula, Short vinCodigo) {
		return prescricaoMedicaON.obterNomeUsualPacitente(matricula, vinCodigo);
	}

	@Override
	public List<FatSituacaoSaidaPaciente> obterListaEstadosClinicos(Short idade, DominioSexoDeterminante sexo){
		return this.fatSituacaoSaidaPacienteDAO.listarEstadoClinicoPacienteAtivos(idade, sexo);
	}
	@Override
	public List<FatSituacaoSaidaPaciente> listarEstadoClinicoPacienteObitoAtivos(Short idade, DominioSexoDeterminante sexo) {
		return this.fatSituacaoSaidaPacienteDAO.listarEstadoClinicoPacienteObitoAtivos(idade, sexo);
	}
	@Override
	public void inserirNovaReceita(MamReceituarios receitaGeral, List<MamItemReceituario> itensReceitaGeral,MamReceituarios receitaEspecial, List<MamItemReceituario> itensReceitaEspecial) throws ApplicationBusinessException{
		manterReceitaON.inserirNovaReceita(receitaGeral, itensReceitaGeral, receitaEspecial, itensReceitaEspecial);
	}
	@Override
	public void validarItemReceita(MamItemReceituario item) throws BaseListException{
		this.manterReceitaON.validarItemReceituario(item); 
	}
	@Override
	public List<VMamMedicamentos> obterMedicamentosReceitaPorDescricaoOuCodigo(String descricaoCodigo) {
		return vMamMedicamentosDAO.obterMedicamentosReceitaPorDescricaoOuCodigo(descricaoCodigo);
	}
	
	
	@Override
	public List<MpmSolicitacaoConsultoria> pesquisarSolicitacaoConsultoriaPorServidorEspecialidade(RapServidores servidor) {
		return this.getMpmSolicitacaoConsultoriaDAO().pesquisarSolicitacaoConsultoriaPorServidorEspecialidade(servidor);
	}

	@Override
	public List<MpmItemPrescricaoMdto> listarPrescMedicamentoDetalhePorPacienteAtendimento(Integer pacCodigo, Integer atdSeq) {
		return this.mpmItemPrescricaoMdtoDAO.listarPrescMedicamentoDetalhePorPacienteAtendimento(pacCodigo, atdSeq);
	}
	@Override
	public void gerarPendenciasSolicitacaoConsultoria(){
		mpmSolicitacaoConsultoriaRN.gerarPendenciasSolicitacaoConsultoria();
	}
	
	@Override
	public AghEspecialidades obterSiglaNomeEspecialidadePorEspSeq(Short pEspSeq){
		return aghEspecialidadesDAO.obterSiglaNomeEspecialidadePorEspSeq(pEspSeq);
	}
	@Override
	public ConsultarRetornoConsultoriaVO obterPacienteNroProntuario(Integer pAtdSeq){
		return aghAtendimentoDAO.obterPacienteNroProntuario(pAtdSeq);
	}
	
	@Override
	public Long obterMedicamentosReceitaPorDescricaoOuCodigoCount(String descricaoCodigo) {
		return vMamMedicamentosDAO.obterMedicamentosReceitaPorDescricaoOuCodigoCount(descricaoCodigo);
	}
	
	
	@Override
	public List<AltaPrincReceitasVO> obterListaMedicamentosPrescritosAlta(Integer apaAtdSeq, Integer apaSeq) {
		return mpmAltaPrincReceitasDAO.obterListaMedicamentosPrescritosAlta(apaAtdSeq, apaSeq);
	}
	
	public void persistirMpmAlta(List<AltaPrincReceitasVO> listaAltaPrincReceitasAux1,  List<AltaPrincReceitasVO> listaAltaPrincReceitasAux2, MpmAltaSumario altoSumario) throws BaseException{
		this.getManterSumarioAltaPosAltaON().gravarMpmAlta(listaAltaPrincReceitasAux1, listaAltaPrincReceitasAux2, altoSumario);
	}
	@Override
	public List<MamReceituarios> buscarDadosReceituario(Integer atendimentoSeq,DominioTipoReceituario tipo){
		return mamReceituariosDAO.obterDadosReceituario(atendimentoSeq,  tipo);
	}
	@Override
	public void excluirReceita(MamReceituarios receitaGeral,MamReceituarios receitaEspecial) throws ApplicationBusinessException{
		manterReceitaON.excluirReceita(receitaGeral, receitaEspecial);
	}
	 
	@Override
	public void prescricaoMedicaTemPeloMenosUmaDieta(PrescricaoMedicaVO prescricaoMedicaVO) throws ApplicationBusinessException{
		confirmarPrescricaoMedicaRN.prescricaoMedicaTemPeloMenosUmaDieta(prescricaoMedicaVO);
	}

	@Override
	public List<AipAlergiaPacientes> listarAlergiasPacientesPorCodigoPaciente(Integer codigoPaciente){
		return aipAlergiaPacientesDAO.listarAlergiasPacientesPorCodigoPaciente(codigoPaciente);
	}
	
	@Override
	public MamReceituarios obterReceituario(Long seq){
		return mamReceituariosDAO.obterPorChavePrimaria(seq);
	}
	@Override
	public List<VMamMedicamentos> obterMedicamentosReceitaPorDescricaoExata(String descricao){
		return vMamMedicamentosDAO.obterMedicamentosReceitaPorDescricaoExata(descricao);
	}
	@Override
	public boolean verificarPacienteInternadoCaracteristicaControlePrevisao(Integer atendimentoSeq) throws ApplicationBusinessException{
		return prescricaoMedicaRN.verificarPacienteInternadoCaracteristicaControlePrevisao(atendimentoSeq);
	}	

	@Override
	public void validaListaMedicamentosPrescritosAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer atdSeq) {
		this.getManterSumarioAltaPosAltaON().validaListaMpmAlta(apaAtdSeq, apaSeq, seqp, atdSeq);
	}

	//3468
	@Override
	public List<AipAlergiaPacientes> obterAipAlergiasPacientes(Integer pacCodigo) {
		return aipAlergiaPacientesDAO.obterAipAlergiasPacientes(pacCodigo);
	}

	@Override
	public List<MpmAlergiaUsual> obterMpmAlergiasUsual(String parametro) {
		return this.mpmAlergiaUsualDAO.obterMpmAlergiasUsual(parametro);
	}

	@Override
	public Long obterMpmAlergiasUsualCount(String parametro) {
		return this.mpmAlergiaUsualDAO.obterMpmAlergiasUsualCount(parametro);
	}

	@Override
	public MpmAlergiaUsual obterMpmAlergiaUsualPorSeq(Integer seq) {
		return this.mpmAlergiaUsualDAO.obterMpmAlergiaUsualPorSeq(seq);
	}

	@Override
	public List<AipAlergiaPacientes> atualizarListaAipAlergiaPacientes(List<AipAlergiaPacientes> listaAipAlergiaPacientes, AipAlergiaPacientes aipPacientesSelecionado, MpmAlergiaUsual mpmAlergiaUsualSelecionado,
			boolean motivoCancelamentoHabilitado, String descricaoNaoCadastrado, String motivoCancelamento, RapServidores servidorLogado) throws ApplicationBusinessException, CloneNotSupportedException {
		return this.prescricaoMedicaON.atualizarListaAipAlergiaPacientes(listaAipAlergiaPacientes, aipPacientesSelecionado, mpmAlergiaUsualSelecionado, 
				motivoCancelamentoHabilitado, descricaoNaoCadastrado, motivoCancelamento, servidorLogado);
	}

	@Override
	public void adicionarAipAlergiaPacientes(List<AipAlergiaPacientes> listaAipAlergiaPacientes, AipAlergiaPacientes aipPacientesSelecionado,
			MpmAlergiaUsual mpmAlergiaUsualSelecionado, boolean motivoCancelamentoHabilitado, String descricaoNaoCadastrado, String motivoCancelamento,
			int contador, AipPacientes paciente)
			throws ApplicationBusinessException {
		this.prescricaoMedicaON.adicionarAipAlergiaPacientes(listaAipAlergiaPacientes, aipPacientesSelecionado, mpmAlergiaUsualSelecionado, 
				motivoCancelamentoHabilitado, descricaoNaoCadastrado, motivoCancelamento, contador, paciente);
	}

	@Override
	public void atualizarIndPacientePediatrico(PrescricaoMedicaVO prescricaoMedicaVO) throws ApplicationBusinessException{
		prescricaoMedicaAlergiaRN.atualizarIndPacientePediatrico(prescricaoMedicaVO);
	}
	
	@Override
	public String buscarEstadoPaciente(Integer pmeSeqAtendimento, Integer pmeSeq) {
		return mamEstadoPacienteDAO.buscarEstadoPaciente(pmeSeqAtendimento, pmeSeq);
	}
	
	@Override
	public boolean verificaExistenciaAlergiaCadastradaPaciente(Integer atendimentoSeq) {
		return aipAlergiaPacientesDAO.verificaExistenciaAlergiaCadastradaPaciente(atendimentoSeq);
	}
	
	@Override
	public AacConsultas obterConsultaPorAtendimentoSeq(Integer codAtendimento) {
		return aacConsultasDAO.obterConsultaPorAgendamentoSeq(codAtendimento);
	}
	
	@Override
	public AghAtendimentos obterAghUnidadeFuncionalPorAtendimentoSeq(Integer codAtendimento) {
		return aghUnidadesFuncionaisDAO.obterAghUnidadesFuncionaisPorAtendimentoSeq(codAtendimento);
	}

	@Override
	public boolean obterCaracteristicaPorUnfSeq(Short seq, ConstanteAghCaractUnidFuncionais caracteristica) {
		return aghCaractUnidFuncionaisDAO.possuiCaracteristicaPorUnidadeEConstante(seq, caracteristica);
	}

	@Override
	public ParametrosProcedureVO verificarParametros(String obterLoginUsuarioLogado,
			Integer codAtendimento, Integer seqPrescricao, ParametrosProcedureVO parametrosProcedureVO) throws ApplicationBusinessException{
		return prescricaoMedicaAlergiaRN.validarParametros(obterLoginUsuarioLogado, codAtendimento, seqPrescricao, parametrosProcedureVO);
	}

	@Override
	public Long buscarTriagemEstadoPaciente(Integer pmeSeqAtendimento,	Integer pmeSeq) {
		return mamEstadoPacienteDAO.buscarTriagemEstadoPaciente(pmeSeqAtendimento, pmeSeq);
	}	
	
	@Override
	public void validarCIDAtendimento(ParametrosProcedureVO parametrosProcedureVO,Integer codAtendimento, Integer seqPrescricao) throws ApplicationBusinessException{
		prescricaoMedicaAlergiaRN.validarCIDAtendimento(parametrosProcedureVO, codAtendimento, seqPrescricao);
	}
	
	//3468

	@Override
	public void gravarAipAlergiaPacientes(
			List<AipAlergiaPacientes> listaAipAlergiaPacientes, RapServidores servidorLogado) throws ApplicationBusinessException {
		this.prescricaoMedicaON.gravarAipAlergiaPacientes(listaAipAlergiaPacientes, servidorLogado);
	}

	@Override
	public List<AipAlergiaPacientes> obterAipAlergiasPacientesHistorico(
			Integer pacCodigo) {
		return this.aipAlergiaPacientesDAO.obterAipAlergiasPacientesHistorico(pacCodigo);
	}
	
	@Override
	public List<MpmItemPrescParecerMdto> pesquisarMpmItemPrescParecerMdtoPorProtuarioLeito(Integer prontuario,Date parametroDataFim,Integer atdSeq){
		return mpmItemPrescParecerMdtoDAO.pesquisarMpmItemPrescParecerMdtoPorProtuarioLeito(prontuario, parametroDataFim,atdSeq);
	}
	
	@Override
	public List<AinLeitos> pesquisarLeitosPorUnidadeFuncionalSalaQuarto(Object param){
		return ainLeitosDAO.pesquisarLeitosPorUnidadeFuncionalSalaQuarto(param);
	}
	
	@Override
	public Long countPesquisarLeitosPorUnidadeFuncionalSalaQuarto(Object param){
		return ainLeitosDAO.countPesquisarLeitosPorUnidadeFuncionalSalaQuarto(param);
	}
	
	@Override
	public AghAtendimentos obterAghAtendimentosPorProntuario(Integer prontuario, Date parametroDataFim){
		return aghAtendimentoDAO.obterAghAtendimentosPorProntuario(prontuario,parametroDataFim);
	}
	
	@Override
	public Long obterQuantidadePrescricoesVerificarAnamnese(Integer prontuario) {
		return this.mpmPrescricaoMedicaDAO.obterQuantidadePrescricoesVerificarAnamnese(prontuario);
	}
	
	@Override
	public AghAtendimentos obterAghAtendimentosPorLeitoID(String leitoID,boolean filtraIndPaciente) {
		return aghAtendimentoDAO.obterAghAtendimentosPorLeitoID(leitoID,filtraIndPaciente);
	}
	
	
	@Override
	public AvaliacaoMedicamentoVO imprimirAvaliacaoMedicamento(Integer pSolicitacao, Integer pMedMatCodigo) throws ApplicationBusinessException {
		return avaliacaoMedicamentoRN.obterRelatorioAvaliacaoMedicamento(pSolicitacao, pMedMatCodigo);
	}
	
	@Override
	public String obterIdadeFormatada(Date dtNascimento) {
		return avaliacaoMedicamentoRN.obterIdadeFormatada(dtNascimento);
	}
	
	@Override
	public Boolean justificativaAntiga(String indicacao, String infeccaoTratar,
			String diagnostico) {
		return this.getAvaliacaoMedicamentoRN().justificativaAntiga(indicacao, infeccaoTratar, diagnostico);
	}

	public AvaliacaoMedicamentoRN getAvaliacaoMedicamentoRN() {
		return avaliacaoMedicamentoRN;
	}

	@Override
	public Boolean usoRestriAntimicrobianoIgualNao(Short gupSeq,
			String indicacao) throws ApplicationBusinessException {
		return this.getAvaliacaoMedicamentoRN().usoRestriAntimicrobianoIgualNao(gupSeq, indicacao);
	}

	@Override
	public Boolean usoRestriAntimicrobianoIgualSim(Short gupSeq,
			String infeccaoTratar) throws ApplicationBusinessException {
		return this.getAvaliacaoMedicamentoRN().usoRestriAntimicrobianoIgualSim(gupSeq, infeccaoTratar);
	}

	@Override
	public Boolean naoPadronAntimicrobianoIgualNao(Short gupSeq,
			String indicacao, String infeccaoTratar)
			throws ApplicationBusinessException {
		return this.getAvaliacaoMedicamentoRN().naoPadronAntimicrobianoIgualNao(gupSeq, indicacao, infeccaoTratar);
	}

	@Override
	public Boolean naoPadronAntimicrobianoIgualSim(Short gupSeq,
			String indicacao, String infeccaoTratar)
			throws ApplicationBusinessException {
		return this.getAvaliacaoMedicamentoRN().naoPadronAntimicrobianoIgualSim(gupSeq, indicacao, infeccaoTratar);
	}

	@Override
	public Boolean indQuimioterapicoIgualSim(String diagnostico) {
		return this.getAvaliacaoMedicamentoRN().indQuimioterapicoIgualSim(diagnostico);
	}

	@Override
	public void atualizarSituacaoMedicamento(Integer seqSolicitacao,
			Date dataInicio, Date dataFim,
			DominioIndRespAvaliacao indResponsavelAvaliacao) {
		this.getAvaliacaoMedicamentoRN().atualizarSituacaoMedicamento(seqSolicitacao, dataInicio, 
				dataFim, indResponsavelAvaliacao);
	}

	@Override
	public String funcaoCfSolicitanteFormula(Integer curJumSeq)
			throws ApplicationBusinessException {
		return this.getAvaliacaoMedicamentoRN().funcaoCfSolicitanteFormula(curJumSeq);
	}

	@Override
	public String obterNomeServidor(Integer serMatricula, Short serVinCodigo) {
		return this.getAvaliacaoMedicamentoRN().obterNomeServidor(serMatricula, serVinCodigo);
	}

	@Override
	public String obterRegConselho(Integer serMatricula, Short serVinCodigo) {
		return this.getAvaliacaoMedicamentoRN().obterRegConselho(serMatricula, serVinCodigo);
	}

	@Override
	public String obterSiglaServidor(Integer serMatricula, Short serVinCodigo) {
		return this.getAvaliacaoMedicamentoRN().obterSiglaServidor(serMatricula, serVinCodigo);
	}
	
	@Override
	public DetalhesParecerMedicamentosVO obterDetalhesParecerMedicamentos(BigDecimal parecerSeq, MpmItemPrescParecerMdtoId mpmItemPrescParecerMdtoId) {
		return getMpmPrescrMdtosDAO().obterDetalhesParecerMedicamentos(parecerSeq,mpmItemPrescParecerMdtoId);
	}

	@Override
	public List<HistoricoParecerMedicamentosJnVO> obterHistoricoParecerMedicamentos(BigDecimal parecerSeq) {
		return getMpmParecerUsoMdtoJnDAO().obterHistoricoParecerMedicamentos(parecerSeq);
	}
	
	public VMpmPrescrMdtosDAO getMpmPrescrMdtosDAO() {
		return mpmPrescrMdtosDAO;
	}

	public MpmParecerUsoMdtoJnDAO getMpmParecerUsoMdtoJnDAO() {
		return mpmParecerUsoMdtoJnDAO;
	}

	public VMpmItemPrcrMdtosON getvMpmItemPrcrMdtosON() {
		return vMpmItemPrcrMdtosON;
	}

//	#1291 - SB1
	@Override
	public List<AfaMedicamento> pesquisarMedicamentosSB1(String parametro) {
		return this.medicamentoDAO.pesquisarMedicamentosSB1(parametro);
	}
	
//	#1291 - SB1
	@Override
	public Long pesquisarMedicamentosSB1Count(String parametro) {
		return this.medicamentoDAO.pesquisarMedicamentosSB1Count(parametro);
	}
	
//	#1291 - SB2
	@Override
	public List<AfaTipoUsoMdto> pesquisarTipoUsoSB2(String parametro) {
		return this.tipoUsoDAO.pesquisaTipoUsoMdtoAtivosII(parametro);
	}
	
//	#1291 - SB2
	@Override
	public Integer pesquisarTipoUsoSB2Count(String parametro) {
		return this.tipoUsoDAO.pesquisarTipoUsoMDTOCount(parametro);
	}

//	#1291 - SB3
	@Override
	public List<AfaGrupoUsoMedicamento> pesquisarGrupoUsoSB3(String parametro) {
		return this.grupoUsoMedicamentoDAO.pesquisarGrupoUsoMedicamentoAtivo(parametro);
	}	
	
//	#1291 - SB3
	@Override
	public Integer pesquisarGrupoUsoSB3Count(String parametro) {
		return this.grupoUsoMedicamentoDAO.pesquisarGrupoUsoMedicamentoAtivoCount(parametro);
	}

	@Override
	public List<VAghUnidFuncional> pesquisarVUnidFuncionalSB4(String parametro) {
		return this.vAghUnidFuncionalDAO.pesquisarVUnidFuncionalSB4(parametro);
	}

	@Override
	public Long pesquisarVUnidFuncionalSB4Count(String parametro) {
		return this.vAghUnidFuncionalDAO.pesquisarVUnidFuncionalSB4Count(parametro);
	}
	
	@Override
	public List<VMpmpProfInterna> pesquisarVMpmpProfInternaSB5(String parametro) {
		return this.vMpmpProfInternaDAO.pesquisarVMpmpProfInternaSB5(parametro);
	}

	@Override
	public Long pesquisarVMpmpProfInternaSB5Count(String parametro) {
		return this.vMpmpProfInternaDAO.pesquisarVMpmpProfInternaSB5Count(parametro);
	}
	
	@Override
	public List<VMedicoSolicitante> pesquisarVMedicoSolicitanteSB6(String parametro) {
		return this.vMedicoSolicitanteDAO.pesquisarVMedicoSolicitanteSB6(parametro);
	}

	@Override
	public Long pesquisarVMedicoSolicitanteSB6Count(String parametro) {
		return this.vMedicoSolicitanteDAO.pesquisarVMedicoSolicitanteSB6Count(parametro);
	}

	@Override
	public List<VMpmItemPrcrMdtosVO> pesquisarSolicitacoesUsoMedicamento(Integer arg0, Integer arg1, String arg2, boolean arg3,
			SolicitacoesUsoMedicamentoVO filtro) {
		return this.vMpmItemPrcrMdtosDAO.pesquisarSolicitacoesUsoMedicamento(arg0, arg1, arg2, arg3, filtro);
	}

	@Override
	public Long pesquisarSolicitacoesUsoMedicamentoCount(SolicitacoesUsoMedicamentoVO filtro) {
		return this.vMpmItemPrcrMdtosDAO.pesquisarSolicitacoesUsoMedicamentoCount(filtro);
	}

	@Override
	public AinQuartos pesquisarAinQuartoPorId(Integer numeroLeito) {
		return ainQuartosDAO.obterQuartosLeitosPorId(numeroLeito.shortValue());
	}

	@Override
	public AghUnidadesFuncionais pesquisarAghUnidadesFuncionaisPorId(Integer unfSeq) {
		return aghUnidadesFuncionaisDAO.obterPorUnfSeq(unfSeq.shortValue());
	}

	@Override
	public AinLeitos pesquisarAinLeitoPorId(String LtoLtoId) {
		return ainLeitosDAO.obterLeitoPorId(LtoLtoId);
	}
	
	@Override
	public Boolean verificarAnamneseCriarPrescricao(AghAtendimentos atd) throws ApplicationBusinessException {
		return this.verificarPrescricaoON.verificarAnamneseCriarPrescricao(atd);
	}
	
	@Override
	public List<MpmTextoPadraoParecer> pesquisarMpmTextoPadraoParecer(String sigla, String descricao) {
		return this.mpmTextoPadraoParecerDAO.obterMpmTextoPadraoParecer(sigla, descricao);
	}

	@Override
	@BypassInactiveModule
	public boolean validarSumarioConcluidoAltaEObitoPorAtdSeq(Integer atdSeq) {
		return this.getMpmAltaSumarioDAO().validarSumarioConcluidoAltaEObitoPorAtdSeq(atdSeq);
	}

	@Override
	public AvaliacaoMedicamentoVO imprimirSolicitacaoMedicamentoAvaliar(
			MpmJustificativaUsoMdto jumSeq) throws ApplicationBusinessException {
		return avaliacaoMedicamentoRN.obterRelatorioSolicitacaoMedicamentoAvaliar(jumSeq);
	}

	public AinLeitos obterLeitoQuartoUnidadeFuncionalPorId(String leitoID){
		return ainLeitosDAO.obterLeitoQuartoUnidadeFuncionalPorId(leitoID);
	}
	@Override
	public void editarMpmTextoPadraoParecer(MpmTextoPadraoParecer mpmTextoPadraoParecer,
			String siglaNova, String descricao) throws BaseException {
		this.mpmTextoPadraoParecerON.editarMpmTextoPadraoParecer(mpmTextoPadraoParecer, siglaNova, descricao);
	}

	@Override
	public void removerMpmTextoPadraoParecer(MpmTextoPadraoParecer mpmTextoPadraoParecer)
			throws BaseException {
		this.mpmTextoPadraoParecerON.removerMpmTextoPadraoParecer(mpmTextoPadraoParecer);
	}

	@Override
	public void adicionarMpmTextoPadraoParecer(String sigla, String descricao)
			throws BaseException {
		this.mpmTextoPadraoParecerON.adicionarMpmTextoPadraoParecer(sigla, descricao);
	}
	
	@Override
	public Object obterSiglaMpmTextoPadraoParecer(String sigla){
		return this.mpmTextoPadraoParecerDAO.obterSiglaMpmTextoPadraoParecer(sigla);
	}
	@Override
	public MpmTextoPadraoParecer obterMpmTextoPadraoParecerOriginal(
			String sigla) {
		return mpmTextoPadraoParecerDAO.obterOriginal(sigla);
	}
	/**
	 * 1378
	 */
	@Override
	public List<MamTipoEstadoPaciente> obterListaTipoEstadoPacientePrescricao(Integer atdSeq) {
		return this.mamEstadoPacienteDAO.obterListaTipoEstadoPaciente(atdSeq);
	}


	@Override
	public void manterEstadoPaciente(MamEstadoPaciente estadoPaciente, Long rgtSeq) {
		this.manterPrescricaoMedicaRN.manterEstadoPaciente(estadoPaciente, rgtSeq);
	}

	@Override
	public void alterarEstadoPaciente(EstadoPacienteVO estadoAnterior, MamEstadoPaciente novoEstado, Long atdSeq) throws ApplicationBusinessException {
		this.manterPrescricaoMedicaRN.alterarEstadoPaciente(estadoAnterior, novoEstado, atdSeq);
	} 

	@Override
	public EstadoPacienteVO obterEstadoPacientePeloTrgSeq(Long trgSeq) {
		return this.manterPrescricaoMedicaRN.obterEstadoPacientePeloTrgSeq(trgSeq);
	}

	@Override
	public EstadoPacienteVO obterEstadoPacientePeloAtdSeq(Long atdSeq) {
		return this.manterPrescricaoMedicaRN.obterEstadoPacientePeloAtdSeq(atdSeq);
	} 
	
	@Override
	public Long obterEsaSeqPorRgtSeq(Long rgtSeq) {
		return this.mamEstadoPacienteDAO.obterEsaSeqPorRgtSeq(rgtSeq);
	}
	
	public MamEstadoPaciente obterMamEstadoPacienteAtdSeq(Long atdSeq){
		return this.mamEstadoPacienteDAO.obterMamEstadoPacienteAtual(atdSeq);
	}

	@Override
	public Long[] obterContagemTotais(Integer matricula, Short vinCodigo, DominioIndRespAvaliacao respAvaliacao) {
		return this.aprovacaoEmLoteRN.obterContagemTotais(matricula, vinCodigo, respAvaliacao);
	}

	@Override
	public void aprovarLote(Integer matricula, Short vinCodigo, DominioIndRespAvaliacao respAvaliacao) throws ApplicationBusinessException {
		this.aprovacaoEmLoteRN.aprovarLote(matricula, vinCodigo, respAvaliacao);
	}

	@Override
	public MamTipoEstadoPaciente obterTipoEstadoPacientePorDescricao(String estadoPaciente) {
		return mamTipoEstadoPacienteDAO.buscarTipoEstadoPacientePorDescricao(estadoPaciente);
	}

	@Override
	public Long obterTriagemPorPaciente(Integer atendimentoSeq) {
		return mamTrgEncInternoDAO.obterTriagemPorAtendimento(atendimentoSeq);
	}
	
	@Override
	public List<AghAtendimentosVO> pesquisarAghAtendimentosPorProntuario(
			Integer prontuario,String leitoID,Date parametroDataFim){
		return aghAtendimentoDAO.pesquisarAghAtendimentosPorProntuario(prontuario,leitoID,parametroDataFim);
	}
	
	@Override
	public MamTrgEncInterno recuperarCurTei(Long cTrgSeq) {
		return mamTrgEncInternoDAO.recuperarCurTei(cTrgSeq);
	}

	@Override
	public CursorPacVO buscarCursorPac(Integer cConNumero) {
		return aacConsultasDAO.buscarCursorPac(cConNumero);
	}

	@Override
	public Date buscarDataNascimentoPaciente(Integer pPacCodigo) {
		return aipPacientesDAO.buscarDataNascimentoPaciente(pPacCodigo);
	}

	@Override
	public MamRegistro obterRegistroPorSeq(Long registro) {
		return mamRegistroDAO.obterPorChavePrimaria(registro);
	}

	@Override
	public void validarProporcaoGlicose50Menor100(BigDecimal paramPercGlic50) throws ApplicationBusinessException {
		this.calcularNptON.validarProporcaoGlicose50Menor100(paramPercGlic50);
	}

	@Override
	public void validarProporcaoGlicose10Menor100(BigDecimal paramPercGlic10) throws ApplicationBusinessException {
		this.calcularNptON.validarProporcaoGlicose10Menor100(paramPercGlic10);
	}

	@Override
	public void validarSomaProporcaoGlicose(BigDecimal paramPercGlic50, BigDecimal paramPercGlic10) throws ApplicationBusinessException {
		this.calcularNptON.validarSomaProporcaoGlicose(paramPercGlic50, paramPercGlic10);
	}

	@Override
	public void validarIntervaloTempoInfusaoSolucao(Short paramTempInfusaoSol) throws ApplicationBusinessException {
		this.calcularNptON.validarIntervaloTempoInfusaoSolucao(paramTempInfusaoSol);
	}

	@Override
	public void validarIntervaloTempoInfusaoLipidios(Short paramTempInfusaoLip) throws ApplicationBusinessException {
		this.calcularNptON.validarIntervaloTempoInfusaoLipidios(paramTempInfusaoLip);
	}
	
	@Override
	public CalculoAdultoNptVO iniciarCalculoNpt(MpmPrescricaoNptVO prescricaoNptVo) throws ApplicationBusinessException {
		return this.calcularNptON.iniciarCalculoNpt(prescricaoNptVo);
	}

	@Override
	public void atualizarPesoAlturaCalculoNpt(MpmPrescricaoNptVO prescricaoNptVo, CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException {
		this.calcularNptON.atualizarPesoAlturaCalculoNpt(prescricaoNptVo, calculoAdultoNptVO);
	}

	@Override
	public void gravarCalculoNpt(MpmPrescricaoNptVO prescricaoNptVo, CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		this.calcularNptON.gravarCalculoNpt(prescricaoNptVo, calculoAdultoNptVO);
	}

	@Override
	public void verificarDosagemMedicamentoSolucao(Integer medMatCodigo, BigDecimal pimDose, Integer seq) throws ApplicationBusinessException {
		itemPrescricaoMedicamentoRN.verificaDoseFracionada(medMatCodigo, pimDose, seq);
	}

	@Override
	public List<PesquisaFoneticaPrescricaoVO> pesquisarPorFonemasPrescricao(Integer firstResult,
			Integer maxResults, String nome, String nomeMae,
			boolean respeitarOrdem, Date dataNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException {
		return pesquisaFoneticaPrescricaoON.pesquisarPorFonemas(firstResult,
				maxResults, nome, nomeMae, respeitarOrdem, dataNascimento,
				listaOrigensAtendimentos);
	}
	
	@Override
	public Long pesquisarPorFonemasPrescricaoCount(String nome, String nomeMae,
			Boolean respeitarOrdem, Date dtNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException {
		return pesquisaFoneticaPrescricaoON.pesquisarPorFonemasCount(nome, nomeMae,
				respeitarOrdem, dtNascimento, listaOrigensAtendimentos);
	}

	@Override
	public String obterServicoMedicoDoAtendimento(Integer atdSeq) throws ApplicationBusinessException{
		return manterPrescricaoMedicaRN.obterServicoMedicoDoAtendimento(atdSeq);
	}

	@Override
	public void validarCampoDose(BigDecimal dose) throws ApplicationBusinessException{
		itemPrescricaoMedicamentoRN.validarCampoDose(dose);
	}
	
	public MpmNotaAdicionalAnamnesesRN getMpmNotaAdicionalAnamnesesRN() {
		return mpmNotaAdicionalAnamnesesRN;
	}

	public MpmNotaAdicionalAnamnesesDAO getMpmNotaAdicionalAnamnesesDAO() {
		return mpmNotaAdicionalAnamnesesDAO;
	}

	@Override
	public void persistirNotaAdicionalAnamnese(
			MpmNotaAdicionalAnamneses notaAdicional, String descricao,
			RapServidores servidor, String nomeEspecialidade) {

		getMpmNotaAdicionalAnamnesesRN().persistirMpmNotaAdicionalAnamneses(
				notaAdicional, descricao, servidor, nomeEspecialidade);

	}

	@Override
	public List<MpmNotaAdicionalAnamneses> listarNotasAdicionaisAnamnese(
			Long seqAnamneses) {

		return getMpmNotaAdicionalAnamnesesDAO()
				.listarNotasAdicionaisAnamnesesPorSeqAnamneses(seqAnamneses);

	}

	public MpmNotaAdicionalEvolucoesRN getMpmNotaAdicionalEvolucoesRN() {
		return mpmNotaAdicionalEvolucoesRN;
	}

	@Override
	public void persistirNotaAdicionalEvolucao(
			MpmNotaAdicionalEvolucoes notaAdicional, String descricao,
			String nomeEspecialidade) {

		getMpmNotaAdicionalEvolucoesRN().persistirNotaAdicionalEvolucoes(
				notaAdicional, descricao, nomeEspecialidade);

	}

	public MpmEvolucoesDAO getMpmEvolucoesDAO() {
		return mpmEvolucoesDAO;
	}

	@Override
	public MpmEvolucoes buscarMpmEvolucoes(Long seqEvolucao) {
		return getMpmEvolucoesDAO().buscarMpmEvolucoes(seqEvolucao);
	}

	public MpmAnamnesesDAO getMpmAnamnesesDAO() {
		return mpmAnamnesesDAO;
	}

	private MpmEvolucoesRN getEvolucaoRN() {
		return mpmEvolucoesRN;
	}

	private MpmAnamneseRN getAnamneseRN() {
		return mpmAnamneseRN;
	}

	@Override
	public MpmAnamneses obterAnamneseValidadaPorAnamneses(Long seqAnamneses) {
		return getMpmAnamnesesDAO().obterAnamneseValidadaPorAnamneses(
				seqAnamneses);
	}

	public MpmNotaAdicionalEvolucoesDAO getMpmNotaAdicionalEvolucoesDAO() {
		return mpmNotaAdicionalEvolucoesDAO;
	}

	@Override
	public List<MpmNotaAdicionalEvolucoes> listarNotasAdicionaisEvolucoes(
			Long seqEvolucoes) {

		return getMpmNotaAdicionalEvolucoesDAO()
				.listarNotasAdicionaisEvolucoesPorSeqEvolucao(seqEvolucoes);

	}

	@Override
	public Date obterDataReferenciaEvolucao(AghAtendimentos atendimento)
			throws ApplicationBusinessException {

		return getEvolucaoRN().obterDataReferenciaEvolucao(atendimento);

	}

	@Override
	public MpmAnamneses criarAnamnese(AghAtendimentos atendimento,
			RapServidores servidor) throws ApplicationBusinessException {

		return getAnamneseRN().criarMpmAnamnese(atendimento, servidor);

	}

	@Override
	public MpmEvolucoes criarEvolucao(MpmAnamneses anamnese,
			Date dataReferencia, RapServidores servidor)
			throws ApplicationBusinessException {

		return getEvolucaoRN().criarMpmEvolucoes(anamnese, dataReferencia,
				servidor);

	}

	@Override
	public void atualizarMpmEvolucaoEmUso(MpmEvolucoes evolucao,
			RapServidores servidor) {

		getEvolucaoRN().atualizarMpmEvolucaoEmUso(evolucao, servidor);

	}

	@Override
	public void validarEvolucaoEmUso(MpmEvolucoes evolucao)
			throws ApplicationBusinessException {

		getEvolucaoRN().validarEvolucaoEmUso(evolucao);

	}

	@Override
	public List<MpmEvolucoes> obterEvolucoesAnamnese(MpmAnamneses anamnese,
			Date data, List<DominioIndPendenteAmbulatorio> situacoes) {

		return getMpmEvolucoesDAO().obterEvolucoesAnamnese(anamnese, data,
				situacoes);

	}

	@Override
	public List<MpmEvolucoes> obterEvolucoesAnamnese(MpmAnamneses anamnese,
			Date data, DominioIndPendenteAmbulatorio situacao) {

		return getMpmEvolucoesDAO().obterEvolucoesAnamnese(anamnese, data,
				situacao);

	}

	@Override
	public void atualizarMpmEvolucoes(MpmEvolucoes evolucao) {

		getMpmEvolucoesDAO().atualizar(evolucao);

	}

	@Override
	public boolean existeAnamneseValidaParaAtendimento(Integer atdSeq) {

		return this.getMpmAnamnesesDAO().existeAnamneseValidaParaAtendimento(
				atdSeq);

	}

	@Override
	public void iniciarEdicaoAnamnese(MpmAnamneses anamnese,
			RapServidores servidor) {

		this.getAnamneseRN().iniciarEdicaoAnamnese(anamnese, servidor);

	}

	@Override
	public void persistirMpmAnamnese(MpmAnamneses anamnese) {

		getMpmAnamnesesDAO().persistir(anamnese);

	}

	@Override
	public void persistirMpmEvolucoes(MpmEvolucoes evolucao) {

		getMpmEvolucoesDAO().persistir(evolucao);

	}

	@Override
	public MpmAnamneses obterAnamneseAtendimento(Integer atdSeq) {
		MpmAnamneses anamnese = getMpmAnamnesesDAO().obterAnamneseAtendimento(atdSeq);
		return anamnese;
	}

	@Override
	public MpmAnamneses obterAnamneseValidadaPorAtendimento(
			Integer seqAtendimento) {
		return getMpmAnamnesesDAO().obterAnamneseAtendimento(seqAtendimento);
	}
	
	@Override
	public MpmAnamneses obterAnamneseDetalhamento(MpmAnamneses anamnese) {
		return getMpmAnamnesesDAO().obterAnamneseDetalhamento(anamnese);
	}

	@Override
	public MpmAnamneses obterAnamnese(Integer seqAtendimento, Long seqAnamnese,
			RapServidores servidor) throws ApplicationBusinessException {

		return this.getAnamneseRN().obterAnamnese(seqAtendimento, seqAnamnese,
				servidor);

	}

	@Override
	public boolean verificarEvolucoesNotasAdicionais(Long anaSeq) {

		return this.getAnamneseRN().verificarEvolucoesNotasAdicionais(anaSeq);

	}

	@Override
	public void concluirAnamnese(MpmAnamneses anamnese, RapServidores servidor)
			throws ApplicationBusinessException {

		this.getAnamneseRN().concluirAnamnese(anamnese, servidor);

	}

	@Override
	public void deixarPendenteAnamnese(MpmAnamneses anamnese,
			RapServidores servidor) throws ApplicationBusinessException{

		this.getAnamneseRN().deixarPendenteAnamnese(anamnese, servidor);

	}

	public MpmEvolucoesRN getMpmEvolucoesRN() {
		return mpmEvolucoesRN;
	}

	public RelatorioEvolucoesPacienteON getRelatorioEvolucoesPacienteON() {
		return relatorioEvolucoesPacienteON;
	}

	public RelatorioAnamnesePacienteON getRelatorioAnamnesePacienteON() {
		return relatorioAnamnesePacienteON;
	}

	@Override
	public void concluirEvolucoes(MpmEvolucoes evolucao,
			String descricaoEvolucao, RapServidores servidor)
			throws ApplicationBusinessException {

		getMpmEvolucoesRN().concluirEvolucao(evolucao, descricaoEvolucao,
				servidor);

	}

	@Override
	public void deixarPendenteEvolucao(MpmEvolucoes evolucao,
			String descricaoEvolucao, RapServidores servidor)
			throws ApplicationBusinessException   {

		getMpmEvolucoesRN().deixarPendenteEvolucao(evolucao, descricaoEvolucao,
				servidor);

	}

	@Override
	public void validarEclusaoEvolucao(Long seqEvolucao, RapServidores servidor)
			throws ApplicationBusinessException {

		getMpmEvolucoesRN().validarEclusaoEvolucao(seqEvolucao, servidor);

	}

	@Override
	public void excluirEvolucao(Long evolucao, RapServidores servidor)
			throws ApplicationBusinessException {

		getMpmEvolucoesRN().excluirEvolucao(evolucao, servidor);

	}

	@Override
	public List<MpmEvolucoes> pesquisarEvolucoesAnteriores(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Long anaSeq,
			Date dataInicial, Date dataFinal) {

		return getMpmEvolucoesDAO().pesquisarEvolucoesAnteriores(firstResult,
				maxResult, orderProperty, asc, anaSeq, dataInicial, dataFinal);

	}

	@Override
	public Long pesquisarEvolucoesAnterioresCount(Long anaSeq,
			Date dataInicial, Date dataFinal) {
		return getMpmEvolucoesDAO().pesquisarEvolucoesAnterioresCount(anaSeq,
				dataInicial, dataFinal);
	}

	@Override
	public boolean verificarAnamneseValida(Long anaSeq) {

		return getMpmAnamnesesDAO().verificarAnamneseValida(anaSeq);

	}

	@Override
	public MpmAnamneses obterAnamneseValidaParaAtendimento(Integer atdSeq) {

		return getMpmAnamnesesDAO().obterAnamneseValidaParaAtendimento(atdSeq);

	}

	@Override
	public boolean possuiNotaAdicionalEvolucao(Long seqEvolucoes) {

		return getMpmNotaAdicionalEvolucoesDAO().possuiNotaAdicional(
				seqEvolucoes);

	}

	@Override
	public List<MpmEvolucoes> listarEvolucoesConcluidasAnamnese(Long seqAnamnese) {

		return getMpmEvolucoesDAO().listarEvolucoesConcluidasAnamnese(
				seqAnamnese);

	}

	@Override
	public boolean verificarEvolucoesAnamnesePorSituacao(Long anaSeq,
			DominioIndPendenteAmbulatorio pendente, boolean situacaoIgual) {

		return getMpmEvolucoesDAO().verificarEvolucoesAnamnesePorSituacao(
				anaSeq, pendente, situacaoIgual);

	}

	@Override
	public boolean verificarEvolucoesValidadas(Long seqAnamnese) {

		return getMpmEvolucoesDAO().verificarEvolucoesValidadas(seqAnamnese);

	}

	@Override
	public List<MpmNotaAdicionalEvolucoes> listarNotasAdicionaisEvolucao(
			Long seqEvolucao) {

		return getMpmNotaAdicionalEvolucoesDAO().listarNotasAdicionais(
				seqEvolucao);

	}

	@Override
	public List<RelatorioAnamnesePacienteVO> gerarRelatorioAnamnesePaciente(
			Long seqAnamnese) {
		return getRelatorioAnamnesePacienteON().gerarRelatorioAnamnesePaciente(
				seqAnamnese);
	}

	@Override
	public List<RelatorioEvolucoesPacienteVO> gerarRelatorioEvolucaoPaciente(
			Long seqEvolucao) {
		return getRelatorioEvolucoesPacienteON()
				.gerarRelatorioEvolucaoPaciente(seqEvolucao);

	}

	@Override
	public MpmEvolucoes criarMpmEvolucaoComDescricao(String descricao,
			MpmAnamneses anamnese, Date dataReferencia, RapServidores servidor)
			throws ApplicationBusinessException {
		return getEvolucaoRN().criarMpmEvolucaoComDescricao(descricao,
				anamnese, dataReferencia, servidor);
	}

	@Override
	public Boolean verificarAdiantamentoEvolucao(AghAtendimentos atendimento,
			Date dataAtual) {
		return getEvolucaoRN().validarAdiantamento(atendimento, dataAtual);
	}

	@Override
	public MpmAnamneses obterMpmAnamnese(Long seqAnamnese) {
		return this.getMpmAnamnesesDAO().obterPorChavePrimaria(seqAnamnese);
	}
	
	@Override
	public void removerAnamnese(Long seqAnamnese) {
		this.getAnamneseRN().desfazerAlteracoesAnamnese(seqAnamnese);
	}

	@Override
	public void removerEvolucao(Long seqEvolucao) throws ApplicationBusinessException {
		this.getEvolucaoRN().desfazerAlteracoesEvolucao(seqEvolucao);
	}
	
	@Override
	public List<MpmEvolucoes> obterEvolucoesAnamnese(MpmAnamneses anamnese, List<DominioIndPendenteAmbulatorio> situacoes, Date dataInicio, Date dataFim) {
		return getMpmEvolucoesDAO().obterEvolucoesAnamnese(anamnese, situacoes, dataInicio, dataFim);
	}

}
