package br.gov.mec.aghu.exames.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.VAelExamesLiberadosDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoCartaColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoMapa;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.cadastrosapoio.business.AelParametroCamposLaudoON;
import br.gov.mec.aghu.exames.dao.*;
import br.gov.mec.aghu.exames.laudos.ExamesListaVO;
import br.gov.mec.aghu.exames.laudos.ResultadoLaudoVO;
import br.gov.mec.aghu.exames.patologia.business.AelAnatomoPatologicoRN;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosVO;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaLoteExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaResultadoCargaInterfaceVO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaClinicaVO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaVO;
import br.gov.mec.aghu.exames.questionario.business.InformacaoComplementarRN;
import br.gov.mec.aghu.exames.solicitacao.vo.AmostraVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAmostraColetadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameOrdemCronologicaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ResultadoExamePim2VO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.vo.AelAmostraExamesVO;
import br.gov.mec.aghu.exames.vo.AelAmostraItemExamesVO;
import br.gov.mec.aghu.exames.vo.AelAmostraRecebidaVO;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.exames.vo.AelExamesXAelParametroCamposLaudoVO;
import br.gov.mec.aghu.exames.vo.AelExtratoAmostrasVO;
import br.gov.mec.aghu.exames.vo.AelExtratoItemSolicitacaoVO;
import br.gov.mec.aghu.exames.vo.AelGrpTecnicaUnfExamesVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicConsultadoVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicitacaoExameRelLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicitacaoExamesVO;
import br.gov.mec.aghu.exames.vo.AelMotivoCancelaExamesVO;
import br.gov.mec.aghu.exames.vo.AtendimentoExternoVO;
import br.gov.mec.aghu.exames.vo.CartaRecoletaVO;
import br.gov.mec.aghu.exames.vo.DetalhesExamesPacienteVO;
import br.gov.mec.aghu.exames.vo.ExameDisponivelFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ExameEspecialidadeSelecionadoFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ExameSelecionadoFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.exames.vo.LiberacaoLimitacaoExameVO;
import br.gov.mec.aghu.exames.vo.MascaraExameVO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemAmostraItemExamesVO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemAmostraVO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemVO;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesFiltrosPesquisaVO;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesVO;
import br.gov.mec.aghu.exames.vo.PendenciaExecucaoVO;
import br.gov.mec.aghu.exames.vo.PesquisaResultadoCargaVO;
import br.gov.mec.aghu.exames.vo.RelatorioAgendamentoProfissionalVO;
import br.gov.mec.aghu.exames.vo.RelatorioEstatisticaTipoTransporteVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPendentesVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaBioquimicaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaEpfVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaHematologiaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaHemoculturaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaUroculturaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMateriaisRecebidosNoDiaVO;
import br.gov.mec.aghu.exames.vo.RelatorioPacientesInternadosExamesRealizarVO;
import br.gov.mec.aghu.exames.vo.RelatorioTicketAreaExecutoraVO;
import br.gov.mec.aghu.exames.vo.ResultadosCodificadosVO;
import br.gov.mec.aghu.exames.vo.SecaoConfExameVO;
import br.gov.mec.aghu.exames.vo.TicketExamesPacienteVO;
import br.gov.mec.aghu.faturamento.vo.AtendPacExternPorColetasRealizadasVO;
import br.gov.mec.aghu.faturamento.vo.AtendimentoCargaColetaVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ExamesHistoricoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ExamesPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.prescricaomedica.vo.ResultadoExamesVO;
import br.gov.mec.aghu.registrocolaborador.vo.VRapServCrmAelVO;


@Modulo(ModuloEnum.EXAMES_LAUDOS)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.JUnit4TestShouldUseTestAnnotation", "PMD.CouplingBetweenObjects", "PMD.NcssTypeCount" })
@Stateless
public class ExamesFacade extends BaseFacade implements IExamesFacade {
	@EJB
	private InformacaoComplementarRN informacaoComplementarRN;
	@EJB
	private ProvaCruzadaON provaCruzadaON;
	@EJB
	private AelItemSolicCartasRN aelItemSolicCartasRN;
	@EJB
	private ArquivoSecretariaNotificacaoRN arquivoSecretariaNotificacaoRN;
	@EJB
	private RelatorioAgendamentoProfissionalON relatorioAgendamentoProfissionalON;
	@EJB
	private DetalhesExamesPacientesON detalhesExamesPacientesON;
	@EJB
	private AelOrdExameMatAnaliseRN aelOrdExameMatAnaliseRN;
	@EJB
	private RelatorioPendenciasExecucaoExamesRN relatorioPendenciasExecucaoExamesRN;
	@EJB
	private GerarProtocoloUnicoRN gerarProtocoloUnicoRN;
	@EJB
	private PesquisaFluxogramaON pesquisaFluxogramaON;
	@EJB
	private CarregarArquivoLaudoResultadoExameRN carregarArquivoLaudoResultadoExameRN;
	@EJB
	private EmitirRelatorioExamesPacienteON emitirRelatorioExamesPacienteON;
	@EJB
	private AelAmostrasRN aelAmostrasRN;
	@EJB
	private AelMetodoRN aelMetodoRN;
	@EJB
	private VoltarTodasAmostrasSolicitacaoRN voltarTodasAmostrasSolicitacaoRN;
	@EJB
	private RelatorioEstatisticaTipoTransporteON relatorioEstatisticaTipoTransporteON;
	@EJB
	private AnexoDocumentoLaudoAutomaticoON anexoDocumentoLaudoAutomaticoON;
	@EJB
	private GestaoCartasRecoletaRN gestaoCartasRecoletaRN;
	@EJB
	private AelExtratoAmostrasON aelExtratoAmostrasON;
	@EJB
	private ExameLimitadoAtendON exameLimitadoAtendON;
	@EJB
	private RelatorioPacientesInternadosExamesRealizarON relatorioPacientesInternadosExamesRealizarON;
	@EJB
	private CadastroResultadoPadraoLaudoON cadastroResultadoPadraoLaudoON;
	@EJB
	private RelatorioFichaTrabalhoPatologiaClinicaON relatorioFichaTrabalhoPatologiaClinicaON;
	@EJB
	private AelCopiaResultadosRN aelCopiaResultadosRN;
	@EJB
	private AelExameConselhoProfsRN aelExameConselhoProfsRN;
	@EJB
	private ExameLimitadoAtendRN exameLimitadoAtendRN;
	@EJB
	private RelatorioSolicitacaoExamesCertificacaoDigitalON relatorioSolicitacaoExamesCertificacaoDigitalON;
	@EJB
	private PesquisarMateriaisColetaEnfermagemON pesquisarMateriaisColetaEnfermagemON;
	@EJB
	private AelConfigMapaRN aelConfigMapaRN;
	@EJB
	private RelatorioFichaTrabalhoAmostraON relatorioFichaTrabalhoAmostraON;
	@EJB
	private AelExamesRN aelExamesRN;
	@EJB
	private ArquivoSecretariaNotificacaoON arquivoSecretariaNotificacaoON;
	@EJB
	private AelOrdExameMatAnaliseON aelOrdExameMatAnaliseON;
	@EJB
	private AelAnatomoPatologicoRN aelAnatomoPatologicoRN;
	@EJB
	private AelNotaAdicionalRN aelNotaAdicionalRN;
	@EJB
	private InformacaoSolicitacaoUnidadeExecutoraRN informacaoSolicitacaoUnidadeExecutoraRN;
	@EJB
	private RelatorioCSVExamesON relatorioCSVExamesON;
	@EJB
	private AelConfigMapaExamesRN aelConfigMapaExamesRN;
	@EJB
	private AelAmostrasON aelAmostrasON;
	@EJB
	private PedidoExameRN pedidoExameRN;
	@EJB
	private GrupoExamesUsuaisRN grupoExamesUsuaisRN;
	@EJB
	private AelValorNormalidCampoRN aelValorNormalidCampoRN;
	@EJB
	private AelHorarioExameDispRN aelHorarioExameDispRN;
	@EJB
	private RelatorioTicketExamesPacienteON relatorioTicketExamesPacienteON;
	@EJB
	private RelatorioTicketAreaExecutoraON relatorioTicketAreaExecutoraON;
	@EJB
	private AelAmostraItemExamesRN aelAmostraItemExamesRN;
	@EJB
	private AelControleNumeroUnicoRN aelControleNumeroUnicoRN;
	@EJB
	private ConfigurarFluxogramaON configurarFluxogramaON;
	@EJB
	private PesquisarExamesPorUnidadeFuncionalON pesquisarExamesPorUnidadeFuncionalON;
	@EJB
	private RelatorioMateriaisRecebidosNoDiaON relatorioMateriaisRecebidosNoDiaON;
	@EJB
	private AghAtendimentosPacExternRN aghAtendimentosPacExternRN;
	@EJB
	private RelatorioMateriaisColetaEnfermagemON relatorioMateriaisColetaEnfermagemON;
	@EJB
	private AelExigenciaExameRN aelExigenciaExameRN;
	@EJB
	private EmitirRelatorioSumarioExamesAltaON emitirRelatorioSumarioExamesAltaON;
	@EJB
	private AelGrupoResultadoCodificadoRN aelGrupoResultadoCodificadoRN;
	@EJB
	private AelItemHorarioAgendadoRN aelItemHorarioAgendadoRN;
	@EJB
	private ConfigurarFluxogramaEspecialidadeON configurarFluxogramaEspecialidadeON;
	@EJB
	private AelResultadosExamesON aelResultadosExamesON;
	@EJB
	private AelAmostraItemExamesON aelAmostraItemExamesON;
	@EJB
	private AelProjetoIntercProcRN aelProjetoIntercProcRN;
	@EJB
	private AelResultadoCodificadoRN aelResultadoCodificadoRN;
	@EJB
	private ModuloExamesRN moduloExamesRN;
	@EJB
	private MascaraExameVersionamentoON mascaraExameVersionamentoON;
	@EJB
	private LwsComunicacaoRN lwsComunicacaoRN;
	@EJB
	private AelResultadoExameRN aelResultadoExameRN;
	@EJB
	private AelExtratoItemSolicitacaoRN aelExtratoItemSolicitacaoRN;
	@EJB
	private AtendimentoExternoON atendimentoExternoON;
	@EJB
	private ListarAmostrasSolicitacaoRecebimentoRN listarAmostrasSolicitacaoRecebimentoRN;
	@EJB
	private RelatorioFichaTrabalhoPatologiaON relatorioFichaTrabalhoPatologiaON;
	@EJB
	private AnexarDocumentoLaudoRN anexarDocumentoLaudoRN;
	@EJB
	private AelProjetoProcedimentoRN aelProjetoProcedimentoRN;
	@EJB
	private AelItemSolicConsultadoRN aelItemSolicConsultadoRN;
	@EJB
	private ReceberTodasAmostrasSolicitacaoRN receberTodasAmostrasSolicitacaoRN;
	@EJB
	private MonitorPendenciasExamesON monitorPendenciasExamesON;
	@EJB
	private AelConfigMapaON aelConfigMapaON;
	@EJB
	private AelAutorizacaoAlteracaoSituacaoRN aelAutorizacaoAlteracaoSituacaoRN;
	@EJB
	private AelUnidExameSignificativoRN aelUnidExameSignificativoRN;
	@EJB
	private AelSecaoConfExamesRN aelSecaoConfExamesRN;
	@EJB
	private AelSecaoConfExameON aelSecaoConfExameON;
	@EJB
	private AgruparExamesON agruparExamesON;
	@EJB
	private LaudoUnicoTecnicaRN laudoUnicoTecnicaRN;
	@EJB
	private AgruparExamesRN agruparExamesRN;
	@EJB
	private RelatorioExamesPendentesON relatorioExamesPendentesON;
	@EJB
	private SituacaoPorExameRN situacaoPorExameRN;
	@EJB
	private PesquisarRelatorioTicketExamesPacienteON pesquisarRelatorioTicketExamesPacienteON;
	
	@Inject
	private AelExamesQuestionarioDAO aelExamesQuestionarioDAO;
	@Inject
	private AelProjetoProcedimentoDAO aelProjetoProcedimentoDAO;
	@Inject
	private AelMotivoCancelaExamesDAO aelMotivoCancelaExamesDAO;
	@Inject
	private VAelUnfExecutaExamesDAO vAelUnfExecutaExamesDAO;
	@Inject
	private AelServidorCampoLaudoDAO aelServidorCampoLaudoDAO;
	@Inject
	private AelItemSolicConsultadoDAO aelItemSolicConsultadoDAO;
	@Inject
	private AelTipoMarcacaoExameDAO aelTipoMarcacaoExameDAO;
	@Inject
	private AelGrupoExameTecnicasDAO aelGrupoExameTecnicasDAO;
	@Inject
	private AelExamesDAO aelExamesDAO;
	@Inject
	private VAelExameMatAnaliseDAO vAelExameMatAnaliseDAO;
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	@Inject
	private AelProjetoPacientesDAO aelProjetoPacientesDAO;
	@Inject
	private AelDocResultadoExamesHistDAO aelDocResultadoExamesHistDAO;
	@Inject
	private AelGrupoTecnicaCampoDAO aelGrupoTecnicaCampoDAO;
	@Inject
	private AelAnticoagulanteDAO aelAnticoagulanteDAO;
	@Inject
	private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	@Inject
	private VAelExamesLiberadosDAO vAelExamesLiberadosDAO;
	@Inject
	private AelLoteExameUsualDAO aelLoteExameUsualDAO;
	@Inject
	private AelExameConselhoProfsDAO aelExameConselhoProfsDAO;
	@Inject
	private AelProjetoIntercProcDAO aelProjetoIntercProcDAO;
	@Inject
	private AelSalasExecutorasExamesDAO aelSalasExecutorasExamesDAO;
	@Inject
	private AelAtendimentoDiversosDAO aelAtendimentoDiversosDAO;
	@Inject
	private AelGrupoResultadoCaracteristicaDAO aelGrupoResultadoCaracteristicaDAO;
	@Inject
	private AelQuestionariosConvUnidDAO aelQuestionariosConvUnidDAO;
	@Inject
	private AelGrupoXMaterialAnaliseDAO aelGrupoXMaterialAnaliseDAO;
	@Inject
	private AelExamesEspecialidadeDAO aelExamesEspecialidadeDAO;
	@Inject
	private AelSinonimoExameDAO aelSinonimoExameDAO;
	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;
	@Inject
	private AghMedicoExternoDAO aghMedicoExternoDAO;
	@Inject
	private AelUnidExecUsuarioDAO aelUnidExecUsuarioDAO;
	@Inject
	private AelModeloCartasDAO aelModeloCartasDAO;
	@Inject
	private AelMetodoDAO aelMetodoDAO;
	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;
	@Inject
	private AelCopiaResultadosDAO aelCopiaResultadosDAO;
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	@Inject
	private AelGrupoResultadoCodificadoDAO aelGrupoResultadoCodificadoDAO;
	@Inject
	private AelItemSolicExameHistDAO aelItemSolicExameHistDAO;
	@Inject
	private AelValorNormalidCampoDAO aelValorNormalidCampoDAO;
	@Inject
	private AelExtratoItemSolicHistDAO aelExtratoItemSolicHistDAO;
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	@Inject
	private AelSolicitacaoExamesHistDAO aelSolicitacaoExamesHistDAO;
	@Inject
	private AelGrupoExameUsualDAO aelGrupoExameUsualDAO;
	@Inject
	private AelExigenciaExameDAO aelExigenciaExameDAO;
	@Inject
	private AelRecipienteColetaDAO aelRecipienteColetaDAO;
	@Inject
	private AelRegiaoAnatomicaDAO aelRegiaoAnatomicaDAO;
	@Inject
	private AelServidoresExameUnidDAO aelServidoresExameUnidDAO;
	@Inject
	private AelProjetoPesquisasDAO aelProjetoPesquisasDAO;
	@Inject
	private AelExameGrupoCaracteristicaDAO aelExameGrupoCaracteristicaDAO;
	@Inject
	private AelRespostaQuestaoDAO aelRespostaQuestaoDAO;
	@Inject
	private AelIntervaloColetaDAO aelIntervaloColetaDAO;
	@Inject
	private AelItemPedidoExameDAO aelItemPedidoExameDAO;
	@Inject
	private VAelGradeDAO vAelGradeDAO;
	@Inject
	private AelCampoLaudoDAO aelCampoLaudoDAO;
	@Inject
	private AelPermissaoUnidSolicDAO aelPermissaoUnidSolicDAO;
	@Inject
	private AelHorarioColetaExameDAO aelHorarioColetaExameDAO;
	@Inject
	private AelOrdExameMatAnaliseDAO aelOrdExameMatAnaliseDAO;
	@Inject
	private AelExamesProvaDAO aelExamesProvaDAO;
	@Inject
	private AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO;
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	@Inject
	private AelGrupoExamesDAO aelGrupoExamesDAO;
	@Inject
	private AelMetodoUnfExameDAO aelMetodoUnfExameDAO;
	@Inject
	private AelVersaoLaudoDAO aelVersaoLaudoDAO;
	@Inject
	private AelExamesLimitadoAtendDAO aelExamesLimitadoAtendDAO;
	@Inject
	private AelResultadoPadraoCampoDAO aelResultadoPadraoCampoDAO;
	@Inject
	private AelNotaAdicionalDAO aelNotaAdicionalDAO;
	@Inject
	private AelResultadosPadraoDAO aelResultadosPadraoDAO;
	@Inject
	private AelMaterialAnaliseDAO aelMaterialAnaliseDAO;
	@Inject
	private AelGrupoRecomendacaoDAO aelGrupoRecomendacaoDAO;
	@Inject
	private AelRefCodeDAO aelRefCodeDAO;
	@Inject
	private AelPedidoExameDAO aelPedidoExameDAO;
	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;
	@Inject
	private AelTmpIntervaloColetaDAO aelTmpIntervaloColetaDAO;
	@Inject
	private LwsComAmostraDAO lwsComAmostraDAO;
	@Inject
	private AelRecomendacaoExameDAO aelRecomendacaoExameDAO;
	@Inject
	private AelQuestionariosDAO aelQuestionariosDAO;
	@Inject
	private AelControleNumeroMapaDAO aelControleNumeroMapaDAO;
	@Inject
	private AelNotasAdicionaisHistDAO aelNotasAdicionaisHistDAO;
	@Inject
	private AelPatologistaDAO aelPatologistaDAO;
	@Inject
	private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;
	@Inject
	private AelExameInternetStatusDAO aelExameInternetStatusDAO;
	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	@Inject
	private AelLoteExameDAO aelLoteExameDAO;
	@Inject
	private AelConfigMapaDAO aelConfigMapaDAO;
	@Inject
	private AelRetornoCartaDAO aelRetornoCartaDAO;
	@Inject
	private AghAtendimentosPacExternDAO aghAtendimentosPacExternDAO;
	@Inject
	private AelUnidMedValorNormalDAO aelUnidMedValorNormalDAO;
	@Inject
	private AelItemSolicCartasDAO aelItemSolicCartasDAO;
	@Inject
	private AelConfigMapaExamesDAO aelConfigMapaExamesDAO;
	@Inject
	private AelLaboratorioExternosDAO aelLaboratorioExternosDAO;
	@Inject
	private AelSinonimoCampoLaudoDAO aelSinonimoCampoLaudoDAO;
	@Inject
	private LwsComunicacaoDAO lwsComunicacaoDAO;
	@Inject
	private AelGrpTecnicaUnfExamesDAO aelGrpTecnicaUnfExamesDAO;
	@Inject
	private AelExtratoItemCartasDAO extratoItemCartasDAO;
	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;
	@Inject
	private AelUnidExameSignificativoDAO aelUnidExameSignificativoDAO;
	@Inject
	private AelSecaoConfExamesDAO aelSecaoConfExamesDAO;
	@Inject
	private AelConfigExLaudoUnicoDAO aelConfigExLaudoUnicoDAO;
	@Inject
	private br.gov.mec.aghu.exames.dao.AelGrupoRecomendacaoExameDAO aelGrupoRecomendacaoExameDAO;
	@Inject
	private AghResponsavelDAO aghResponsaveldao;
	@Inject
	private AghPaisBcbDAO aghPaisBcbDAO;
	@Inject
	private AelExameApItemSolicDAO aelExameApItemSolicDAO;
	
	@EJB
	private LaudosExamesON laudosExamesON;
	@EJB
	private MascaraExamesJasperReportON mascaraExamesJasperReportON;
	@EJB
	private MascaraExamesJasperFPreviewON mascaraExamesJasperFPreviewON;
	
	@EJB
	private MascaraExamesPreviaON mascaraExamesPreviaON;
	
	@EJB
	private AelParametroCamposLaudoON aelParametroCamposLaudoON; 
	
	private static final int TRANSACTION_TIMEOUT_8_HORAS = 60 * 60 * 8; // 8
	private static final long serialVersionUID = 8730542876890552681L;

	@Override
	@BypassInactiveModule
	public AelMateriaisAnalises obterMaterialAnalisePeloId(final Integer codigo) {
		return getAelMaterialAnaliseDAO().obterPeloId(codigo);
	}
	/**
	 * #47146 - Método para chamada da Function 1
	 */
	public String obterResultadoExame(AelResultadoExame ree){
		return arquivoSecretariaNotificacaoRN.obterResultado(ree);
	}

	@Override
	@Secure("#{s:hasPermission('manterMaterialAnalise','pesquisar')}")
	public List<AelMateriaisAnalises> pesquisarMateriasAnalise(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelMateriaisAnalises elemento) {
		return getAelMaterialAnaliseDAO().pesquisar(firstResult, maxResult,
				orderProperty, asc, elemento);
	}

	@Override
	public AelAtendimentoDiversos obterAelAtendimentoDiversosPorChavePrimaria(
			final Integer seq) {
		return getAelAtendimentoDiversosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public AghAtendimentosPacExtern obterAghAtendimentosPacExternPorChavePrimaria(
			Integer seq) {
		return this.getAghAtendimentosPacExternDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@Secure("#{s:hasPermission('substituirProntuarioExames','executar')}")
	public void atualizarAghAtendimentosPacExtern(AghAtendimentosPacExtern aghAtendimentosPacExtern, String nomeMicrocomputador, RapServidores servidorLogado ) throws BaseException {
		this.getAghAtendimentosPacExternRN().alterar(aghAtendimentosPacExtern, nomeMicrocomputador, servidorLogado);
	}

	@Override
	public List<AghAtendimentosPacExtern> listarAtendimentosPacExternPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getAghAtendimentosPacExternDAO()
				.listarAtendimentosPacExternPorCodigoPaciente(pacCodigo);
	}

	protected AghAtendimentosPacExternDAO getAghAtendimentosPacExternDAO() {
		return aghAtendimentosPacExternDAO;
	}

	@Override
	public AghMedicoExterno obterMedicoExternoPorId(final Integer seq) {
		return getAghMedicoExternoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public AghMedicoExterno obterMedicoExternoPorPK(final Integer seq) {
		return getAghMedicoExternoDAO().obterMedicoExternoPorPK(seq);
	}
	
	private AghMedicoExternoDAO getAghMedicoExternoDAO() {
		return aghMedicoExternoDAO;
	}

	private AelAtendimentoDiversosDAO getAelAtendimentoDiversosDAO() {
		return aelAtendimentoDiversosDAO;
	}

	
	private EmitirRelatorioExamesPacienteON getEmitirRelatorioExamesPacienteON() {
		return emitirRelatorioExamesPacienteON;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.administracao.business.IAdministracaoFacade#
	 * obterSalaExecutoraExamesPorUnidadeFuncional
	 * (br.gov.mec.aghu.model.AghUnidadesFuncionais, java.lang.Object)
	 */
	@Override
	public List<AelSalasExecutorasExames> obterSalaExecutoraExamesPorUnidadeFuncional(
			AghUnidadesFuncionais unidadeFuncional, Object param) {
		return getAelSalasExecutorasExamesDAO()
				.obterSalaExecutoraExamesPorUnidadeFuncional(unidadeFuncional,
						param);
	}

	@Override
	@Secure("#{s:hasPermission('manterMaterialAnalise','pesquisar')}")
	public Long pesquisarMateriasAnaliseCount(
			final AelMateriaisAnalises elemento) {
		return getAelMaterialAnaliseDAO().pesquisarCount(elemento);
	}

	@Override
	public void persistirAelAnatomoPatologico(
			final AelAnatomoPatologico aelAnatomoPatologico)
			throws BaseException {
		this.getAelAnatomoPatologicoRN().persistir(aelAnatomoPatologico);
	}

	private AelAnatomoPatologicoRN getAelAnatomoPatologicoRN() {
		return aelAnatomoPatologicoRN;
	}
	
	// --------------------------------------------------
	// Regiões Anatômicas

	@Override
	public AelRegiaoAnatomica obterRegiaoAnatomicaPeloId(final Integer codigo) {
		return getAelRegiaoAnatomicaDAO().obterPeloId(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('manterRegiaoAnatomica','pesquisar')}")
	public List<AelRegiaoAnatomica> pesquisarRegioesAnatomicas(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelRegiaoAnatomica elemento) {
		return getAelRegiaoAnatomicaDAO().pesquisar(firstResult, maxResult,
				orderProperty, asc, elemento);
	}

	@Override
	@Secure("#{s:hasPermission('manterRegiaoAnatomica','pesquisar')}")
	public Long pesquisarRegioesAnatomicasCount(
			final AelRegiaoAnatomica elemento) {
		return getAelRegiaoAnatomicaDAO().pesquisarCount(elemento);
	}

	// --------------------------------------------------

	@Override
	public List<AelExamesMaterialAnalise> buscarAelExamesMaterialAnalisePorAelExames(
			final AelExames aelExames) {
		return getAelExamesMaterialAnaliseDAO()
				.buscarAelExamesMaterialAnalisePorAelExames(aelExames);
	}

	@Override
	public AelExamesMaterialAnalise buscarAelExamesMaterialAnalisePorId(
			final String exaSigla, final Integer manSeq) {
		return getAelExamesMaterialAnaliseDAO()
				.buscarAelExamesMaterialAnalisePorId(exaSigla, manSeq);
	}

	@Override
	public List<AelTipoAmostraExame> buscarListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(
			final String emaExaSigla, final Integer emaManSeq) {
		return getAelTipoAmostraExameDAO()
				.buscarListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(
						emaExaSigla, emaManSeq);
	}

	@Override
	@BypassInactiveModule
	public AelExames obterAelExamesPeloId(final String sigla) {
		return this.getAelExamesDAO().obterPeloId(sigla);
	}

	@Override
	@BypassInactiveModule
	public boolean verificarExistenciaSolicitacoesExameComRetornoPeloNumConsulta(
			final Integer numero) {
		return this.getAelSolicitacaoExameDAO()
				.verificarExistenciaSolicitacoesExameComRetornoPeloNumConsulta(numero);
	}

	@Override
	@BypassInactiveModule
	public AelSolicitacaoExames obterAelSolicitacaoExamesPeloId(
			final Integer seq) {
		return this.getAelSolicitacaoExameDAO().obterPeloId(seq);
	}

	@Override
	public Long pesquisarDadosBasicosExamesCount(final AelExames aelExames, AghUnidadesFuncionais unidade) {
		return getAelExamesDAO().pesquisarCount(aelExames, unidade);
	}

	@Override
	public List<AelExames> pesquisarDadosBasicosExames(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelExames elemento, AghUnidadesFuncionais unidade) {
		return getAelExamesDAO().pesquisar(firstResult, maxResult,
				orderProperty, asc, elemento, unidade);
	}

	@Override
	public AelSinonimoExame obterAelSinonimoExamePorId(
			final AelSinonimoExameId id) {
		return getAelSinonimoExameDAO().obterAelSinonimoExamePorId(id);
	}

	@Override
	public void persistirResultados(
			final Map<AelParametroCamposLaudo, Object> valoresCampos,
			final Integer soeSeq, final Short seqp,
			final RapServidores servidorResponsavelLiberacao,
			final Boolean usuarioLiberaExame, String nomeMicrocomputador, AelItemSolicitacaoExames itemOriginal) throws BaseException {
		getAelResultadosExamesON().persistirResultados(valoresCampos, soeSeq,
				seqp, servidorResponsavelLiberacao, usuarioLiberaExame, nomeMicrocomputador, itemOriginal);
	}

	@Override
	public void atualizarAelResultadoExame(AelResultadoExame elemento, String nomeMicrocomputador) throws BaseException {
		getAelResultadoExameRN().atualizar(elemento, nomeMicrocomputador);
	}
	
	@Override
	public void inserirAelResultadoExame(AelResultadoExame elemento)
			throws BaseException {
		getAelResultadoExameRN().persistir(elemento);
	}


	@Override
	public void inserirAelNotaAdicional(AelNotaAdicional notaAdicional)
			throws BaseException {
		getAelNotaAdicionalRN().inserir(notaAdicional);
	}

	@Override
	@Secure("#{s:hasPermission('anularResultadoExame','executar')}")
	public void anularResultado(final Integer soeSeq, final Short seqp,
			final Boolean usuarioAnulaExame, String nomeMicrocomputador, AelItemSolicitacaoExames itemOriginal) throws BaseException {
		getAelResultadosExamesON().anularResultado(soeSeq, seqp,
				usuarioAnulaExame, nomeMicrocomputador, itemOriginal);
	}

	@Override
	public void inserirNotaAdicional(final AelNotaAdicional notaAdicional)
			throws BaseException {
		getAelNotaAdicionalRN().inserir(notaAdicional);
	}

	@Override
	@Secure("#{s:hasPermission('manterUnidadeMedida','pesquisar')}")
	public List<AelUnidMedValorNormal> pesquisarDadosBasicosUnidMedValorNormal(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelUnidMedValorNormal elemento) {
		return getAelUnidMedValorNormalDAO().pesquisar(firstResult, maxResult,
				orderProperty, asc, elemento);
	}

	private AelUnidMedValorNormalDAO getAelUnidMedValorNormalDAO() {
		return aelUnidMedValorNormalDAO;
	}

	@Override
	@Secure("#{s:hasPermission('manterUnidadeMedida','pesquisar')}")
	public Long pesquisarAelUnidMedValorNormalCount(
			final AelUnidMedValorNormal aelUnidMed) {
		return getAelUnidMedValorNormalDAO().pesquisarCount(aelUnidMed);
	}

	@Override
	public AelUnidMedValorNormal obterAelUnidMedValorNormalPeloId(
			final Integer codigo) {
		return this.getAelUnidMedValorNormalDAO().obterPeloId(codigo);
	}

	/**
	 * Realiza a pesquisa de registros da tabela AEL_RECIPIENTES_COLETA.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param elemento
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('manterRecipienteColeta','pesquisar')}")
	public List<AelRecipienteColeta> pesquisaRecipienteColetaList(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelRecipienteColeta elemento) {

		return getAelRecipienteColetaDAO().pesquisaRecipienteColetaList(
				firstResult, maxResult, orderProperty, asc, elemento);
	}

	@Override
	@Secure("#{s:hasPermission('manterRecipienteColeta','pesquisar')}")
	public Long countRecipienteColeta(final AelRecipienteColeta elemento) {

		return getAelRecipienteColetaDAO().countRecipienteColeta(elemento);
	}

	@Override
	public AelRecipienteColeta obterRecipienteColetaPorId(final Integer codigo) {
		return getAelRecipienteColetaDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public List<AelMateriaisAnalises> listarAelMateriaisAnalises(
			final Object parametro) {
		return getAelMaterialAnaliseDAO().listarAelMateriaisAnalises(parametro);
	}

	@Override
	public List<AelMateriaisAnalises> listarAelMateriaisAnalisesAtivoColetavel(
			final Object parametro) {
		return getAelMaterialAnaliseDAO()
				.listarAelMateriaisAnalisesAtivoColetavel(parametro);
	}

	@Override
	public List<AelRecipienteColeta> listarAelRecipienteAtivoColetavel(
			final Object parametro) {
		return getAelRecipienteColetaDAO().listarAelRecipienteAtivoColetavel(
				parametro);
	}

	@Override
	public List<AelAnticoagulante> listarAelAnticoagulanteAtivo(
			final Object parametro) {
		return getAelAnticoagulanteDAO()
				.listarAelAnticoagulanteAtivo(parametro);
	}

	@Override
	public List<AelModeloCartas> listarAelModeloCartas(final Object parametro) {
		return getAelModeloCartasDAO().listarAelModeloCartas(parametro);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioMateriaisColetarEnfermagem','executar')}")
	public List<MateriaisColetarEnfermagemVO> pesquisarRelatorioMateriaisColetaEnfermagem(
			final AghUnidadesFuncionais unidadeFuncional)
			throws ApplicationBusinessException {
		return getRelatorioMateriaisColetaEnfermagemON()
				.pesquisarRelatorioMateriaisColetaEnfermagem(unidadeFuncional);
	}

	@Override
	@Secure("#{s:hasPermission('materiaisColetarEnfermagem','pesquisar')}")
	public List<MateriaisColetarEnfermagemAmostraVO> pesquisarMateriaisColetaEnfermagemAmostra(
			AghUnidadesFuncionais unidadeFuncional, Integer prontuarioPaciente,
			Integer soeSeq, DominioSituacaoAmostra situacao)
			throws ApplicationBusinessException {
		return getPesquisarMateriaisColetaEnfermagemON()
				.pesquisarMateriaisColetaEnfermagem(unidadeFuncional,
						prontuarioPaciente, soeSeq, situacao);
	}
	
	@Override
	@Secure("#{s:hasPermission('materiaisColetarEnfermagem','pesquisar')}")
	public List<MateriaisColetarEnfermagemAmostraItemExamesVO> pesquisarMateriaisColetarEnfermagemPorAmostra(
			Integer amoSoeSeq, Short amoSeqp) {
		return getPesquisarMateriaisColetaEnfermagemON()
				.pesquisarMateriaisColetarEnfermagemPorAmostra(amoSoeSeq,
						amoSeqp);
	}

	@Override
	public boolean verificarExistenciaSolicitacaoExamePorNumConsulta(Integer numero) {

		return getAelSolicitacaoExameDAO().verificarExistenciaSolicitacaoExamePorNumConsulta(numero);
	}
	
	// --------------------------------------------------
	// Intervalos de Coleta

	@Override
	@Secure("#{s:hasPermission('manterIntervaloColeta','pesquisar')}")
	public List<AelIntervaloColeta> listarIntervalosColetaPorExameMaterial(
			final String siglaExame, final Integer codigoMaterial) {
		return getAelIntervaloColetaDAO().listarPorExameMaterial(siglaExame,
				codigoMaterial);
	}

	@Override
	@Secure("#{s:hasPermission('manterIntervaloColeta','pesquisar')}")
	public List<AelRefCode> obterCodigosPorDominio(final String dominio) {
		return getAelRefCodeDAO().obterCodigosPorDominio(dominio);
	}

	// --------------------------------------------------
	// Getters

	protected AelIntervaloColetaDAO getAelIntervaloColetaDAO() {
		return aelIntervaloColetaDAO;
	}

	protected AelRefCodeDAO getAelRefCodeDAO() {
		return aelRefCodeDAO;
	}

	protected AelTmpIntervaloColetaDAO getAelTmpIntervaloColetaDAO() {
		return aelTmpIntervaloColetaDAO;
	}

	protected AelMaterialAnaliseDAO getAelMaterialAnaliseDAO() {
		return aelMaterialAnaliseDAO;
	}

	protected AelModeloCartasDAO getAelModeloCartasDAO() {
		return aelModeloCartasDAO;
	}

	protected AelRetornoCartaDAO getAelRetornoCartaDAO() {
		return aelRetornoCartaDAO;
	}

	private AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}

	private AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}

	protected AelNotaAdicionalDAO getAelNotaAdicionalDAO() {
		return aelNotaAdicionalDAO;
	}

	protected AelNotaAdicionalRN getAelNotaAdicionalRN() {
		return aelNotaAdicionalRN;
	}

	protected RelatorioPendenciasExecucaoExamesRN getRelatorioPendenciasExecucaoExamesRN() {
		return relatorioPendenciasExecucaoExamesRN;
	}

	private AelSinonimoExameDAO getAelSinonimoExameDAO() {
		return aelSinonimoExameDAO;
	}

	private RelatorioMateriaisColetaEnfermagemON getRelatorioMateriaisColetaEnfermagemON() {
		return relatorioMateriaisColetaEnfermagemON;
	}

	private AelRecipienteColetaDAO getAelRecipienteColetaDAO() {
		return aelRecipienteColetaDAO;
	}

	protected AelGrupoRecomendacaoDAO getAelGrupoRecomendacaoDAO() {
		return aelGrupoRecomendacaoDAO;
	}

	private AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}

	// --------------------------------------------------
	// FIM - Getters

	protected AelRegiaoAnatomicaDAO getAelRegiaoAnatomicaDAO() {
		return aelRegiaoAnatomicaDAO;
	}

	protected AelExameConselhoProfsRN getAelExameConselhoProfsRN() {
		return aelExameConselhoProfsRN;
	}

	@Override
	public void inserirAelExameConselhoProfs(
			final AelExameConselhoProfs aelExameConselhoProfs)
			throws ApplicationBusinessException {
		getAelExameConselhoProfsRN().inserirAelExameConselhoProfs(
				aelExameConselhoProfs);
	}

	protected AelCopiaResultadosRN getAelCopiaResultadosRN() {
		return aelCopiaResultadosRN;
	}

	/* Estoria 5354 */

	@Override
	public void inserirAelCopiaResultados(
			final AelCopiaResultados aelCopiaResultados)
			throws ApplicationBusinessException {
		getAelCopiaResultadosRN().inserirAelCopiaResultados(aelCopiaResultados);
	}

	@Override
	public void atualizarAelCopiaResultados(
			final AelCopiaResultados aelCopiaResultados)
			throws ApplicationBusinessException {
		getAelCopiaResultadosRN().atualizarAelCopiaResultados(
				aelCopiaResultados);
	}

	@Override
	public void removerAelCopiaResultados(
			final AelCopiaResultados aelCopiaResultados)
			throws BaseException {
		getAelCopiaResultadosRN().remover(aelCopiaResultados);
	}

	@Override
	public AelGrupoRecomendacao obterAelGrupoRecomendacaoPeloId(final Integer seq) {
		return getAelGrupoRecomendacaoDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public AelGrupoRecomendacao obterAelGrupoRecomendacaoPeloId(final Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return aelGrupoRecomendacaoDAO.obterPorChavePrimaria(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	@Override
	public AelExamesMaterialAnalise obterAelExamesMaterialAnalisePorId(
			final AelExamesMaterialAnaliseId id) {
		return this.getAelExamesMaterialAnaliseDAO().obterPorChavePrimaria(id);
	}

	@Override
	public AelExamesMaterialAnalise obterAelExamesMaterialAnalisePorId(
			final AelExamesMaterialAnaliseId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return this.getAelExamesMaterialAnaliseDAO().obterPorChavePrimaria(id, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	protected AelOrdExameMatAnaliseRN getAelOrdExameMatAnaliseRN() {
		return aelOrdExameMatAnaliseRN;
	}

	@Override
	public void inserirAelOrdExameMatAnalise(
			final AelOrdExameMatAnalise aelOrdExameMatAnalise) throws ApplicationBusinessException {
		getAelOrdExameMatAnaliseRN().inserirAelOrdExameMatAnalise(
				aelOrdExameMatAnalise);
	}

	// Anticoagulantes
	@Override
	@Secure("#{s:hasPermission('manterAnticoagulantes','pesquisar')}")
	public List<AelAnticoagulante> pesquisarDadosBasicosAnticoagulantes(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelAnticoagulante elemento) {
		return getAelAnticoagulanteDAO().pesquisar(firstResult, maxResult,
				orderProperty, asc, elemento);
	}

	private AelAnticoagulanteDAO getAelAnticoagulanteDAO() {
		return aelAnticoagulanteDAO;
	}

	private AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}

	@Override
	@Secure("#{s:hasPermission('manterAnticoagulantes','pesquisar')}")
	public Long pesquisarAelAnticoagulanteCount(
			final AelAnticoagulante aelUnidMed) {
		return getAelAnticoagulanteDAO().pesquisarCount(aelUnidMed);
	}

	@Override
	public AelAnticoagulante obterAelAnticoagulantePeloId(final Integer codigo) {
		return this.getAelAnticoagulanteDAO().obterPeloId(codigo);
	}

	@Override
	public List<AelUnfExecutaExames> buscaListaAelUnfExecutaExames(
			final String sigla, final Integer manSeq, final Boolean ativo) {
		return getAelUnfExecutaExamesDAO().buscaListaAelUnfExecutaExames(sigla,
				manSeq, null, ativo);
	}

	@Override
	@Secure("#{s:hasPermission('imprimirMapaPatologia','executar')}")
	public RelatorioFichaTrabalhoPatologiaVO obterSolicitacaoAtendimento(
			final Integer soeSeq, final Short unfSeq) {
		return getRelatorioFichaTrabalhoPatologiaON().obterFichaTrabPatologia(
				soeSeq, unfSeq);
	}

	@Override
	public List<RelatorioFichaTrabalhoPatologiaClinicaVO> obterFichaTrabPorExame(Integer amoSoeSeq, Short amoSoeSeqP, Boolean recebeAmostra, Short unfSeq) {
		return getRelatorioFichaTrabalhoPatologiaClinicaON().obterFichaTrabPorExame(amoSoeSeq, amoSoeSeqP, recebeAmostra, unfSeq);
	}
	
	@Override
	public RelatorioFichaTrabalhoPatologiaVO obterFichaTrabAmostra(Integer soeSeq, Short seqP, Short unfSeq) throws ApplicationBusinessException {
		return getRelatorioFichaTrabalhoAmostraON().obterFichaTrabAmostra(soeSeq, seqP, unfSeq);
	}
	
	// Preencher Solicitacao de Exames - inicio
	// ##############################################################

	// ##############################################################
	// Preencher Solicitacao de Exames - final

	@Override
	@BypassInactiveModule
	public AelSitItemSolicitacoes pesquisaSituacaoItemExame(final String valor) {
		return this.getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(valor);
	}
	
	// ##############################################################

	protected AelUnidExecUsuarioDAO getAelUnidExecUsuarioDAO() {
		return aelUnidExecUsuarioDAO;
	}

	@Override
	public AelUnidExecUsuario obterUnidExecUsuarioPeloId(
			final RapServidoresId id) {
		return this.getAelUnidExecUsuarioDAO().obterPeloId(id);
	}

	@Override
	public List<AacConsultas> listarSituacaoExames() {
		return getAelSitItemSolicitacoesDAO().listarSituacaoExames();
	}

	/** #5962 Atendiment paciente externo - inicio **/
	@Override
	@Secure("#{s:hasPermission('cadastrarAtendimentoPacienteExterno','pesquisar')}")
	public List<AghMedicoExterno> obterMedicoExternoList(final String parametro) {
		return getAtendimentoExternoON().obterMedicoExternoList(parametro);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarAtendimentoPacienteExterno','pesquisar')}")
	public Long obterMedicoExternoListCount(final String parametro) {
		return getAtendimentoExternoON().obterMedicoExternoListCount(parametro);
	}

	@Override
	public List<AelLaboratorioExternos> obterLaboratorioExternoList(
			final String parametro) {
		return getAelLaboratorioExternosDAO().obterLaboratorioExternoList(
				parametro);
	}

	protected AelLaboratorioExternosDAO getAelLaboratorioExternosDAO() {
		return aelLaboratorioExternosDAO;
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarAtendimentoPacienteExterno','pesquisar')}")
	public List<AtendimentoExternoVO> obterAtendimentoExternoList(
			final Integer codigoPaciente) {
		return getAtendimentoExternoON().obterAtendimentoExternoList(
				codigoPaciente);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarAtendimentoPacienteExterno','pesquisar')}")
	public List<FatConvenioSaudePlano> listarConvenioSaudePlanos(
			final String parametro) {
		return this.getAtendimentoExternoON().listarConvenioSaudePlanos(
				parametro);
	}

	@Override
	public Integer listarConvenioSaudePlanosCount(final String parametro) {
		return this.getAtendimentoExternoON().listarConvenioSaudePlanosCount(
				parametro);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarAtendimentoPacienteExterno','persistir') or s:hasPermission('gerarExamesIntegracao','inserir')}")
	public AghAtendimentosPacExtern gravarAghAtendimentoPacExtern(
			final AghAtendimentosPacExtern atendimentosPacExtern, String nomeMicrocomputador, RapServidores servidorLogado)
			throws BaseException {
		return this.getAtendimentoExternoON().gravar(atendimentosPacExtern, nomeMicrocomputador,servidorLogado);
	}

	/** #5962 Atendiment paciente externo - final **/

	/** GET/SET **/
	private AelRecomendacaoExameDAO getAelRecomendacaoExameDAO() {
		return aelRecomendacaoExameDAO;
	}

	@Override
	public List<AelRecomendacaoExame> obterRecomendacoesExames(
			final String sigla, final Integer manSeq) {
		return getAelRecomendacaoExameDAO().obterRecomendacoesExame(sigla,
				manSeq);
	}

	@Override
	public AelRecomendacaoExame obterAelRecomendacaoExamePorID(
			final AelRecomendacaoExameId id) {
		return getAelRecomendacaoExameDAO().obterAelRecomendacaoExamePorID(id);
	}

	private AelExamesProvaDAO getAelExamesProvaDAO() {
		return aelExamesProvaDAO;
	}

	private AelPermissaoUnidSolicDAO getAelPermissaoUnidSolicDAO() {
		return aelPermissaoUnidSolicDAO;
	}

	private AelExamesEspecialidadeDAO getAelExamesEspecialidadeDAO() {
		return aelExamesEspecialidadeDAO;
	}

	@Override
	public List<AelExamesProva> obterAelExamesProva(final String sigla,
			final Integer manSeq) {
		return getAelExamesProvaDAO().obterAelExamesProva(sigla, manSeq);
	}

	@Override
	public AelExamesProva obterAelExamesProvaPorId(final AelExamesProvaId id) {
		return getAelExamesProvaDAO().obterAelExamesProvaPorId(id);
	}

	@Override
	public List<AelPermissaoUnidSolic> buscaListaAelPermissoesUnidSolicPorEmaExaSiglaEmaManSeqUnfSeq(
			final String emaExaSigla, final Integer emaManSeq,
			final Short unfSeq) {
		return getAelPermissaoUnidSolicDAO()
				.buscaListaAelPermissoesUnidSolicPorEmaExaSiglaEmaManSeqUnfSeq(
						emaExaSigla, emaManSeq, unfSeq);
	}

	@Override
	public List<AelExamesEspecialidade> buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeq(
			final String emaExaSigla, final Integer emaManSeq,
			final Short unfSeq) {
		return getAelExamesEspecialidadeDAO()
				.buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeq(
						emaExaSigla, emaManSeq, unfSeq);
	}

	@Override
	public List<AelExameHorarioColeta> listaHorariosColetaExames(
			final String sigla, final Integer manSeq) {
		return getHorarioColetaExameDAO().listaHorariosColetaExames(sigla,
				manSeq);
	}

	protected AelHorarioColetaExameDAO getHorarioColetaExameDAO() {
		return aelHorarioColetaExameDAO;
	}

	@Override
	public AelExameHorarioColeta obterExameHorarioColetaPorID(
			final AelExameHorarioColetaId id) {
		return getHorarioColetaExameDAO().obterPorChavePrimaria(id); // obterAelRecomendacaoExamePorID
	}

	@Override
	@BypassInactiveModule
	public AelUnfExecutaExames obterAelUnidadeExecutoraExamesPorID(
			final String emaExaSigla, final Integer emaManSeq,
			final Short unfSeq) {
		return getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(
				emaExaSigla, emaManSeq, unfSeq);
	}

	@BypassInactiveModule
	@Override
	public FluxogramaLaborarorialVO pesquisarFluxograma(
			final Short especialidade, final Integer prontuario, boolean historicos)
			throws ApplicationBusinessException {
		return getPesquisaFluxogramaON().pesquisarFluxograma(especialidade,
				prontuario, historicos);
	}

	@Override
	@BypassInactiveModule
	public String verificaNormalidade(
			final FluxogramaLaborarorialDadosVO dadoVO, final Date dataEvento)
			throws ParseException {
		return getPesquisaFluxogramaON()
				.verificaNormalidade(dadoVO, dataEvento);
	}

	/*
	 * restringir pedido de exames a servidores
	 */
	protected AelServidoresExameUnidDAO getAelServidoresExameUnidDAO() {
		return aelServidoresExameUnidDAO;
	}

	@Override
	@Secure("#{s:hasPermission('manterRestringirPedidoServidor','pesquisar')}")
	public List<AelServidoresExameUnid> buscaListaAelServidoresExameUnidPorEmaExaSiglaEmaManSeqUnfSeq(
			final String emaExaSigla, final Integer emaManSeq,
			final Short unfSeq) {

		return getAelServidoresExameUnidDAO()
				.buscaListaAelServidoresExameUnidPorEmaExaSiglaEmaManSeqUnfSeq(
						emaExaSigla, emaManSeq, unfSeq);
	}

	/**
	 * Conselhos Profissionais que Solicitam Exame
	 */
	@Override
	@Secure("#{s:hasPermission('manterConselhosProfissionaisExame','pesquisar')}")
	public List<RapConselhosProfissionais> listarConselhosProfsExame(
			final Object parametro) throws ApplicationBusinessException {
		// Utilizado no suggestionbox
		return getConselhosProfsDAO().listarConselhosProfissionais(parametro);
	}

	protected AelExameConselhoProfsDAO getConselhosProfsDAO() {
		return aelExameConselhoProfsDAO;
	}

	@Override
	@Secure("#{s:hasPermission('manterConselhosProfissionaisExame','pesquisar')}")
	public List<AelExameConselhoProfs> listaConselhosProfsExame(
			final String sigla, final Integer manSeq) {
		return getConselhosProfsDAO().listaConselhosProfsExame(sigla, manSeq);
	}

	@Override
	public AelExameConselhoProfs obterConselhosProfsExamePorID(
			final AelExameConselhoProfsId id) {
		return getConselhosProfsDAO().obterPorChavePrimaria(id);

	}

	@Override
	@Secure("#{s:hasPermission('manterConselhosProfissionaisExame','executar')}")
	public void removerAelExameConselhoProfs(
			final AelExameConselhoProfs exameConselhoProfs) {
		getAelExameConselhoProfsRN().removerAelExameConselhoProfs(
				exameConselhoProfs);
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}

	protected RelatorioTicketExamesPacienteON getRelatorioTicketExamesPacienteON() {
		return relatorioTicketExamesPacienteON;
	}

	protected PesquisaFluxogramaON getPesquisaFluxogramaON() {
		return pesquisaFluxogramaON;
	}

	@Override
	@BypassInactiveModule
	public List<TicketExamesPacienteVO> pesquisarRelatorioTicketExamesPaciente(
			final Integer codSolicitacao, Short unfSeq) throws ApplicationBusinessException {
		return getRelatorioTicketExamesPacienteON()
				.pesquisarRelatorioTicketExamesPaciente(codSolicitacao, unfSeq, null);
	}
	
	@Override
	@BypassInactiveModule
	public void pesquisarUnidadeFuncionalColeta() throws ApplicationBusinessException {
		getRelatorioTicketExamesPacienteON().pesquisarUnidadeFuncionalColeta();
	}
	
	@Override
	@BypassInactiveModule
	public List<TicketExamesPacienteVO> pesquisarRelatorioTicketExamesPaciente(
			final Integer codSolicitacao, Short unfSeq, Set<Short> listaUnfSeq) throws ApplicationBusinessException {
		return getRelatorioTicketExamesPacienteON()
				.pesquisarRelatorioTicketExamesPaciente(codSolicitacao, unfSeq, listaUnfSeq);
	}
	

	@Override
	@BypassInactiveModule
	public TicketExamesPacienteVO geraTicketExamesPacienteVO(AelItemSolicitacaoExames itemSolicitacao) throws ApplicationBusinessException {
		return pesquisarRelatorioTicketExamesPacienteON.geraTicketExamesPacienteVO(itemSolicitacao);
	}

	@Override
	@Secure("#{s:hasPermission('emitirRelatorioPendenciaExecucao','executar')}")
	public List<PendenciaExecucaoVO> pesquisaExamesPendentesExecucao(
			final Short p_unf_seq, final Integer p_grt_seq,
			final Date dtInicial, final Date dtFinal,
			final Integer numUnicoInicial, final Integer numUnicoFinal)
			throws ApplicationBusinessException {
		return getRelatorioPendenciasExecucaoExamesRN()
				.pesquisaExamesPendentesExecucao(p_unf_seq, p_grt_seq,
						dtInicial, dtFinal, numUnicoInicial, numUnicoFinal);
	}

	@Override
	public List<AelSitItemSolicitacoes> listarSituacoesItensSolicitacaoAtivos() {
		return getAelSitItemSolicitacoesDAO().listarSituacoesItensSolicitacaoAtivos();
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExamesDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected AelItemSolicExameHistDAO getAelItemSolicExameHistDAO() {
		return aelItemSolicExameHistDAO;
	}

	// --------------------------------------------------
	// Solicitação Exames

	@Override
	public List<AelSitItemSolicitacoes> pesquisarSituacaoExame(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelSitItemSolicitacoes elemento) {
		return getAelSitItemSolicitacoesDAO().pesquisar(firstResult, maxResult,
				orderProperty, asc, elemento);
	}

	@Override
	public Long pesquisarSituacaoExameCount(
			final AelSitItemSolicitacoes elemento) {
		return getAelSitItemSolicitacoesDAO().pesquisarCount(elemento);
	}

	@Override
	@BypassInactiveModule
	public AelSitItemSolicitacoes obterSituacaoExamePeloId(final String codigo) {
		return getAelSitItemSolicitacoesDAO().obterPeloId(codigo);
	}

	@Override
	public List<AelSitItemSolicitacoes> listarTodosPorSituacaoEMostrarSolicExames(
			final DominioSituacao indSituacao,
			final Boolean indMostraSolicitarExames) {
		return this.getAelSitItemSolicitacoesDAO()
				.listarTodosPorSituacaoEMostrarSolicExames(indSituacao,
						indMostraSolicitarExames);
	}

	@Override
	public List<AelItemSolicitacaoExames> pesquisarCarregarArquivoLaudoResultadoExame(
			final AghUnidadesFuncionais unidadeExecutora,
			final AelSolicitacaoExames solicitacaoExame, final Short seqp)
			throws BaseException {
		return getCarregarArquivoLaudoResultadoExameRN()
				.pesquisarCarregarArquivoLaudoResultadoExame(unidadeExecutora,
						solicitacaoExame, seqp);
	}

	@Override
	public List<AelItemSolicitacaoExames> pesquisarInformarSolicitacaoExameDigitacaoController(
			final Integer solicitacaoExameSeq, final Integer amostraSeqp,
			final Short seqUnidadeFuncional) throws ApplicationBusinessException {
		return getCarregarArquivoLaudoResultadoExameRN()
				.pesquisarInformarSolicitacaoExameDigitacaoController(
						solicitacaoExameSeq, amostraSeqp, seqUnidadeFuncional);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicConsultadoVO> pesquisarAelItemSolicConsultadosResultadosExames(
			final Integer iseSoeSeq, final Short iseSeqp) {
		return getCarregarArquivoLaudoResultadoExameRN()
				.pesquisarAelItemSolicConsultadosResultadosExames(iseSoeSeq,
						iseSeqp);
	}
	
	@Override
	@BypassInactiveModule
	public List<AelItemSolicConsultadoVO> pesquisarAelItemSolicConsultadosResultadosExamesHist(
			final Integer iseSoeSeq, final Short iseSeqp) {
		return getCarregarArquivoLaudoResultadoExameRN()
				.pesquisarAelItemSolicConsultadosResultadosExamesHist(iseSoeSeq,
						iseSeqp);
	}

	@Override
	@BypassInactiveModule
	public void persistirVisualizacaoDownloadAnexo(final Integer iseSoeSeq,
			final Short iseSeqp) throws ApplicationBusinessException{
		getCarregarArquivoLaudoResultadoExameRN()
				.persistirVisualizacaoDownloadAnexo(iseSoeSeq, iseSeqp);
	}
	
	@Override
	@BypassInactiveModule
	public void persistirVisualizacaoDownloadAnexoHist(final Integer iseSoeSeq,
			final Short iseSeqp) throws ApplicationBusinessException {
		getCarregarArquivoLaudoResultadoExameRN()
				.persistirVisualizacaoDownloadAnexoHist(iseSoeSeq, iseSeqp);
	}

	@Override
	public String obterDescricaoVAghUnidFuncional(final Short seq)
			throws BaseException {
		return getCarregarArquivoLaudoResultadoExameRN()
				.obterDescricaoVAghUnidFuncional(seq);
	}

	protected CarregarArquivoLaudoResultadoExameRN getCarregarArquivoLaudoResultadoExameRN() {
		return carregarArquivoLaudoResultadoExameRN;
	}

	@Override
	public Boolean existeDocumentoAnexado(final Integer iseSoeSeq,
			final Short iseSeqp) {
		return getCarregarArquivoLaudoResultadoExameRN()
				.existeDocumentoAnexado(iseSoeSeq, iseSeqp);
	}

	@Override
	@BypassInactiveModule
	public AelDocResultadoExame obterDocumentoAnexado(final Integer iseSoeSeq,
			final Short iseSeqp) {
		return getCarregarArquivoLaudoResultadoExameRN().obterDocumentoAnexado(
				iseSoeSeq, iseSeqp);
	}

	@Override
	public void inserirAelDocResultadoExame(final AelDocResultadoExame doc,
			final AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador)
			throws BaseException {
		this.getCarregarArquivoLaudoResultadoExameRN()
				.inserirAelDocResultadoExame(doc, unidadeExecutora, nomeMicrocomputador);
	}

	@Override
	public void removerAelDocResultadoExame(final AelDocResultadoExame doc,
			final AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador)
			throws BaseException {
		this.getCarregarArquivoLaudoResultadoExameRN()
				.removerAelDocResultadoExame(doc, unidadeExecutora, nomeMicrocomputador);
	}

	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames buscaItemSolicitacaoExamePorId(
			final Integer soeSeq, final Short seqp) {
		return getAelItemSolicitacaoExamesDAO().obterPorId(soeSeq, seqp);
	}
	
	@Override
	@BypassInactiveModule
	public AelItemSolicExameHist buscaItemSolicitacaoExamePorIdHist(
			final Integer soeSeq, final Short seqp) {
		return getAelItemSolicExameHistDAO().obterPorId(soeSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public AelItemSolicExameHist buscaItemSolicitacaoExamePorIdHistOrigemPol(
			final Integer soeSeq, final Short seqp) {
		return getAelItemSolicExameHistDAO().obterPorIdOrigemPol(soeSeq, seqp);
	}


	@Override
	@BypassInactiveModule
	public List<AelNotaAdicional> pesquisarNotaAdicionalPorSolicitacaoEItem(
			final Integer soeSeq, final short seqp) {
		return getAelNotaAdicionalDAO().pesquisarNotaAdicionalPorSolicitacaoEItem(soeSeq,
				seqp);
	}
	
	/*@Override
	@BypassInactiveModule
	public List<AelNotasAdicionaisHist> pesquisarNotaAdicionalPorSolicitacaoEItemHist(
			final Integer soeSeq, final short seqp) {
		return getAelNotaAdicionalDAO().pesquisarNotaAdicionalPorSolicitacaoEItemHist(soeSeq,
				seqp);
	}*/

	@Override
	@BypassInactiveModule
	public void insereVisualizacaoItemSolicitacao(
			final AelItemSolicConsultado itemSolicConsultado, Boolean flush) throws ApplicationBusinessException {
		getAelItemSolicConsultadoRN().inserir(itemSolicConsultado, flush);
	}
	
	@Override
	public List<VRapServCrmAelVO> obterListaResponsavelLiberacao(String paramPesquisa, boolean filtrarMatricula) throws ApplicationBusinessException {
		return getAelResultadosExamesON().obterListaResponsavelLiberacao(paramPesquisa, filtrarMatricula);
	}
	
	@Override
	@BypassInactiveModule
	public void insereVisualizacaoItemSolicitacaoHist(
			final AelItemSolicConsultadoHist itemSolicConsultado, Boolean flush) throws ApplicationBusinessException {
		getAelItemSolicConsultadoRN().inserirHist(itemSolicConsultado, flush);
	}

	@Override
	public List<AelSolicitacaoExames> buscarAelSolicitacaoExames(
			final String valor) {
		return getAelSolicitacaoExameDAO().buscarAelSolicitacaoExames(valor);
	}

	@Override
	public List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesPorParametro(
			final String valor) {
		return getAelSitItemSolicitacoesDAO()
				.buscarListaAelSitItemSolicitacoesPorParametro(valor);
	}

	@Override
	public List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesPorParametro(
			final String valor, final String... codigosRestritivos) {
		return getAelSitItemSolicitacoesDAO()
				.buscarListaAelSitItemSolicitacoesPorParametro(valor,
						codigosRestritivos);
	}
	
	@Override
	public List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesParaExamesPendentes(String parametro) {
		return getAelSitItemSolicitacoesDAO().buscarListaAelSitItemSolicitacoesParaExamesPendentes(parametro);
	}
	
	@Override
	public Long buscarListaAelSitItemSolicitacoesParaExamesPendentesCount(String parametro) {
		return getAelSitItemSolicitacoesDAO().buscarListaAelSitItemSolicitacoesParaExamesPendentesCount(parametro);
	}

	@Override
	@BypassInactiveModule
	public void excluirPedidosExamesPorAtendimento(
			final AghAtendimentos atendimento) {
		this.getPedidoExameRN().excluirPedidosExamesPorAtendimento(atendimento);
	}

	private PedidoExameRN getPedidoExameRN() {
		return pedidoExameRN;

	}

	private AelItemSolicConsultadoRN getAelItemSolicConsultadoRN() {
		return aelItemSolicConsultadoRN;
	}

	// ##############################################################

	// #5446 Listar amostras da solicitação para recebimento

	@Override
	public AelAmostras buscarAmostrasPorId(final Integer soeSeq,
			final Short seqp) {
		return getAelAmostrasDAO().buscarAmostrasPorId(soeSeq, seqp);
	}

	@Override
	public String cancelarEdicaoFrasco(final AelAmostrasId amostrasId) {
		return getListarAmostrasSolicitacaoRecebimentoRN()
				.cancelarEdicaoFrasco(amostrasId);
	}

	@Override
	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(
			final AelSolicitacaoExames solicitacaoExame, final Short amostraSeqp) {
		return getAelAmostrasDAO().buscarAmostrasPorSolicitacaoExame(
				solicitacaoExame, amostraSeqp);
	}

	@Override
	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(
			final Integer soeSeq, final Short amostraSeqp) {
		return getAelAmostrasDAO().buscarAmostrasPorSolicitacaoExame(soeSeq,
				amostraSeqp);
	}

	@Override
	public List<AelAmostrasVO> buscarAmostrasVOPorSolicitacaoExame(final Integer soeSeq, final Short amostraSeqp)
			throws BaseException {
		return getListarAmostrasSolicitacaoRecebimentoRN().buscarAmostrasVOPorSolicitacaoExame(soeSeq, amostraSeqp);
	}

	@Override
	public List<AelAmostrasVO> buscarAmostrasVOPorSolicitacaoExame(final AelSolicitacaoExames solicitacaoExame, final Short amostraSeqp)
			throws BaseException {
		return getListarAmostrasSolicitacaoRecebimentoRN().buscarAmostrasVOPorSolicitacaoExame(solicitacaoExame, amostraSeqp);
	}

	@Override
	public List<AelAmostraItemExames> buscarAelAmostraItemExamesPorAmostra(
			final Integer amoSoeSeq, final Integer amoSeqp) {
		return getAelAmostraItemExamesDAO()
				.buscarAelAmostraItemExamesPorAmostra(amoSoeSeq, amoSeqp);
	}

	@Override
	public List<AelAmostraItemExames> buscarAelAmostraItemExamesPorAmostraComApNotNull(
			final Integer amoSoeSeq, final Integer amoSeqp) {
		return getAelAmostraItemExamesDAO()
				.buscarAelAmostraItemExamesPorAmostraComApNotNull(amoSoeSeq,
						amoSeqp);
	}

	@Override
	public ImprimeEtiquetaVO receberAmostra(
			final AghUnidadesFuncionais unidadeExecutora,
			final AelAmostras amostra, final String nroFrascoFabricante,
			final List<ExameAndamentoVO> listaExamesAndamento, String nomeMicrocomputador) throws BaseException {
		return getListarAmostrasSolicitacaoRecebimentoRN().receberAmostra(
				unidadeExecutora, amostra, nroFrascoFabricante, 
				listaExamesAndamento, nomeMicrocomputador);
					
	}

	@Override
	public void verificarModoInterfaceamento(final AelAmostras amostra,
			final boolean cancelaInterfaceamento, String nomeMicrocomputador ) throws BaseException {
		getListarAmostrasSolicitacaoRecebimentoRN()
				.verificarModoInterfaceamento(amostra, cancelaInterfaceamento, nomeMicrocomputador);
	}

	@Override
	public ImprimeEtiquetaVO receberAmostraSolicitacao(
			final AghUnidadesFuncionais unidadeExecutora,
			final AelAmostras amostra, List<ExameAndamentoVO> listaExamesAndamento, String nomeMicrocomputador) throws BaseException {		
		return getReceberTodasAmostrasSolicitacaoRN()
				.receberAmostraSolicitacao(unidadeExecutora, amostra, 
						listaExamesAndamento, nomeMicrocomputador);
	}

	@Override
	public boolean voltarAmostra(final AghUnidadesFuncionais unidadeExecutora,
			final AelAmostras amostra, String nomeMicrocomputador ) throws BaseException {
		return getListarAmostrasSolicitacaoRecebimentoRN().voltarAmostra(
				unidadeExecutora, amostra, nomeMicrocomputador);
	}

	@Override
	public boolean voltarSituacaoAmostraSolicitacao(
			final AghUnidadesFuncionais unidadeExecutora,
			final AelAmostras amostra, String nomeMicrocomputador ) throws BaseException {
		return getVoltarTodasAmostrasSolicitacaoRN().voltarSituacaoAmostraSolicitacao(
				unidadeExecutora, amostra, nomeMicrocomputador);
	}

	@Override
	public Integer imprimirEtiquetaAmostra(final AelAmostras amostra,
			final AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador )
			throws BaseException {
		return getListarAmostrasSolicitacaoRecebimentoRN().imprimirEtiquetaAmostra(
				amostra, unidadeExecutora, nomeMicrocomputador);
	}

	@Override
	public String comporMensagemConfirmacaoImpressaoEtiquetas(
			final AelAmostras amostra) throws BaseException {
		return getListarAmostrasSolicitacaoRecebimentoRN()
				.comporMensagemConfirmacaoImpressaoEtiquetas(amostra);
	}

	private AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}

	private AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}

	private ListarAmostrasSolicitacaoRecebimentoRN getListarAmostrasSolicitacaoRecebimentoRN() {
		return listarAmostrasSolicitacaoRecebimentoRN;
	}

	private ReceberTodasAmostrasSolicitacaoRN getReceberTodasAmostrasSolicitacaoRN() {
		return receberTodasAmostrasSolicitacaoRN;
	}

	private VoltarTodasAmostrasSolicitacaoRN getVoltarTodasAmostrasSolicitacaoRN() {
		return voltarTodasAmostrasSolicitacaoRN;
	}

	@Override
	public List<AelExamesMaterialAnalise> listarExamesMaterialAnalise(
			final Object objPesquisa) {
		return this.getAelExamesMaterialAnaliseDAO()
				.listarExamesMaterialAnalise(objPesquisa);
	}

	@Override
	public List<VAelExameMatAnalise> listarVExamesMaterialAnalise(
			final Object objPesquisa) {
		return this.getVAelExameMatAnaliseDAO()
				.pesquisarVAelExameMatAnalisePelaSiglaSeq(objPesquisa);
	}

	@Override
	public Long listarVExamesMaterialAnaliseCount(final Object objPesquisa) {
		return this.getVAelExameMatAnaliseDAO()
				.pesquisarVAelExameMatAnalisePelaSiglaSeqCount(objPesquisa);
	}

	@Override
	public List<AelGrupoExameUsual> obterGrupoPorCodigoDescricao(
			final Object objPesquisa) {
		return this.getAelGrupoExameUsualDAO().obterGrupoPorCodigoDescricao(
				(String) objPesquisa);
	}

	@Override
	public Long listarExamesMaterialAnaliseCount(final Object objPesquisa) {
		return this.getAelExamesMaterialAnaliseDAO()
				.listarExamesMaterialAnaliseCount(objPesquisa);
	}

	// 2244 - Solicitar exames em lote
	@Override
	public List<AelLoteExameUsual> getLoteDefaultEspecialidade(
			final AghEspecialidades especialidade) {
		return getAelLoteExamesUsualDAO().obterLotesPorEspecialidade(
				especialidade);
	}

	@Override
	public AelLoteExameUsual getLoteExameUsualPorSeq(final Short seq) {
		return getAelLoteExamesUsualDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@Secure("#{s:hasPermission('manterLoteExames','pesquisar')}")
	public Long pesquisaLotesPorParametrosCount(final PesquisaLoteExamesFiltroVO filtro) {
		return getAelLoteExamesUsualDAO().pesquisaLotesPorParametrosCount(filtro);
	}

	@Override
	public List<AelLoteExameUsual> pesquisaLotesPorParametros(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final PesquisaLoteExamesFiltroVO filtro) {
		return getAelLoteExamesUsualDAO().pesquisaLotesPorParametros(
				firstResult, maxResult, orderProperty, asc, filtro);
	}

	@Override
	public List<AelLoteExameUsual> getLoteDefaultGrupo() {
		return getAelLoteExamesUsualDAO().obterLotesPorGrupo();
	}

	@Override
	public List<AelLoteExameUsual> getLoteDefaultUnidade(
			final SolicitacaoExameVO solicitacaoExameVO) {
		return getAelLoteExamesUsualDAO().obterLotesPorUnidadeFuncional(
				solicitacaoExameVO.getUnidadeFuncional());
	}

	@Override
	@Secure("#{s:hasPermission('manterLoteExames','pesquisar')}")
	public List<AelLoteExame> pesquisaLotesExamesPorLoteExameUsual(
			final Short leuSeq) {
		return getAelLoteExamesDAO().pesquisaLotesExamesPorLoteExameUsual(
				leuSeq);
	}

	private AelLoteExameUsualDAO getAelLoteExamesUsualDAO() {
		return aelLoteExameUsualDAO;
	}

	private AelLoteExameDAO getAelLoteExamesDAO() {
		return aelLoteExameDAO;
	}

	private AelGrupoExameUsualDAO getAelGrupoExameUsualDAO() {
		return aelGrupoExameUsualDAO;
	}
	
	private AelExamesLimitadoAtendDAO getAelExamesLimitadoAtendDAO() {
		return aelExamesLimitadoAtendDAO;
	}
	
	@Override
	public void persistirAelAmostraItemExames(
			final AelAmostraItemExames itemAmostra, final Boolean flush, String nomeMicrocomputador )
			throws BaseException {
		this.getAelAmostraItemExamesON().persistirAelAmostraItemExames(
				itemAmostra, flush, nomeMicrocomputador);
	}

	private AelAmostraItemExamesON getAelAmostraItemExamesON() {
		return aelAmostraItemExamesON;
	}

	@Override
	@BypassInactiveModule
	public void inserirAmostraItemExame(final AelAmostraItemExames itemAmostra)
			throws BaseException {
		this.getAelAmostraItemExamesRN().inserir(itemAmostra);
	}

	private AelAmostraItemExamesRN getAelAmostraItemExamesRN() {
		return aelAmostraItemExamesRN;
	}

	@Override
	public void removerAmostraItemExame(
			final AelAmostraItemExames amostraItemExames) {
		this.getAelAmostraItemExamesRN().removerSemFlush(amostraItemExames);
	}

	@Override
	public void removerExameLimitadoAtend(
			final AelExamesLimitadoAtend exameLimitadoAtend) throws ApplicationBusinessException {
		this.getExameLimitadoAtendRN().remover(exameLimitadoAtend);
	}
	
	@Override
	public void persistirExameLimitadoAtend(
			final AelExamesLimitadoAtend exameLimitadoAtend) throws ApplicationBusinessException {
		this.getExameLimitadoAtendON().persistir(exameLimitadoAtend);
	}
	
	@Override
	public void atualizarExameLimitadoAtend(
			final AelExamesLimitadoAtend exameLimitadoAtend) throws ApplicationBusinessException {
		this.getExameLimitadoAtendRN().atualizar(exameLimitadoAtend);
	}
	
	@Override
	public void atualizarAelAmostraItemExames(
			final AelAmostraItemExames itemAmostra, final Boolean flush,
			final Boolean atualizaItemSolic, String nomeMicrocomputador ) throws BaseException {
		getAelAmostraItemExamesRN().atualizarAelAmostraItemExames(itemAmostra,
				flush, atualizaItemSolic, nomeMicrocomputador);
	}

	@Override
	public void persistirAelAmostra(final AelAmostras amostra,
			final Boolean flush ) throws BaseException {
		getAelAmostrasON().persistirAelAmostra(amostra, flush);
	}
	
	private AelAmostrasON getAelAmostrasON() {
		return aelAmostrasON;
	}

	private AelResultadosExamesON getAelResultadosExamesON() {
		return aelResultadosExamesON;
	}

	private AelResultadoExameRN getAelResultadoExameRN() {
		return aelResultadoExameRN;
	}

	private ExameLimitadoAtendON getExameLimitadoAtendON() {
		return exameLimitadoAtendON;
	}
	
	private ExameLimitadoAtendRN getExameLimitadoAtendRN() {
		return exameLimitadoAtendRN;
	}

	@Override
	public void atualizarAmostra(final AelAmostras amostra, final Boolean flush )
			throws BaseException {
		this.getAelAmostrasRN().atualizarAelAmostra(amostra, flush);
	}

	private AelAmostrasRN getAelAmostrasRN() {
		return aelAmostrasRN;
	}

	@Override
	@BypassInactiveModule
	public void inserirAmostra(final AelAmostras amostra )
			throws BaseException {
		getAelAmostrasRN().inserir(amostra);
	}

	@Override
	public void removerAmostra(final AelAmostras amostra)
			throws BaseException {
		this.getAelAmostrasRN().removerSemFlush(amostra);
	}

	@Override
	@BypassInactiveModule
	public boolean validarPermissaoAlterarExameSituacao(
			final AelMatrizSituacao matrizSituacao,
			final RapServidores userLogado) {
		return getAelAutorizacaoAlteracaoSituacaoRN()
				.temPermissaoAlterarExameSituacao(matrizSituacao, userLogado);
	}

	private AelAutorizacaoAlteracaoSituacaoRN getAelAutorizacaoAlteracaoSituacaoRN() {
		return aelAutorizacaoAlteracaoSituacaoRN;
	}

	@Override
	public AelControleNumeroUnico atualizarControleNumeroUnicoUp(
			final AelControleNumeroUnico controleNumeroUnicoUp)
			throws BaseException {
		return this.getAelControleNumeroUnicoRN().atualizarSemFlush(
				controleNumeroUnicoUp);
	}

	private AelControleNumeroUnicoRN getAelControleNumeroUnicoRN() {
		return aelControleNumeroUnicoRN;
	}

	//bypass pq usa na POL
	@Override
	@BypassInactiveModule
	public void inserirExtratoItemSolicitacao(
final AelExtratoItemSolicitacao extrato, final boolean flush) throws BaseException {
		getAelExtratoItemSolicitacaoRN().inserir(extrato, flush);

	}

	@Override
	@BypassInactiveModule
	public List<AelExtratoItemSolicitacaoVO> pesquisarAelExtratoItemSolicitacaoVOPorItemSolicitacao(
			final Integer iseSoeSeq, final Short iseSeqp, final Boolean isHist) {
		return getAelExtratoItemSolicitacaoRN()
				.pesquisarAelExtratoItemSolicitacaoVOPorItemSolicitacao(
						iseSoeSeq, iseSeqp, isHist);
	}

	private AelExtratoItemSolicitacaoRN getAelExtratoItemSolicitacaoRN() {
		return aelExtratoItemSolicitacaoRN;
	}

	//bypass pq usa na POL
	@Override
	@BypassInactiveModule
	public void atualizarHorarioExameDisp(final AelHorarioExameDisp horarioExame)
			throws BaseException {
		this.getAelHorarioExameDispRN().atualizarSemFlush(horarioExame);
	}

	private AelHorarioExameDispRN getAelHorarioExameDispRN() {
		return aelHorarioExameDispRN;
	}

	@Override
	public void inserirHorarioExameDisp(
			final AelHorarioExameDisp horarioExameDisp ) throws BaseException {
		getAelHorarioExameDispRN().inserirSemFlush(horarioExameDisp);
	}

	@Override
	public void removerHorarioExameDisp(
			final AelHorarioExameDisp horarioExameDisp) {
		getAelHorarioExameDispRN().removerSemFlush(horarioExameDisp);
	}

	@Override
	public void removerItemHorarioAgendado(
			final AelItemHorarioAgendado horariosAgendado, Boolean flush, String nomeMicrocomputador, AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExameOriginal )
			throws BaseException {
		getAelItemHorarioAgendadoRN().remover(horariosAgendado, flush, nomeMicrocomputador, itemSolicitacaoExame, itemSolicitacaoExameOriginal);
	}

	@Override
	public void cancelarHorariosPorItemSolicitacaoExame(
			final AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador )
			throws BaseException {
		getAelItemHorarioAgendadoRN().cancelarHorariosPorItemSolicitacaoExame(
				itemSolicitacaoExame, itemSolicitacaoExameOriginal, nomeMicrocomputador);
	}

	@Override
	public void cancelarHorariosExamesAgendados(
			AelItemHorarioAgendado itemHorarioAgendado, Short globalUnfSeq, String nomeMicrocomputador )
			throws BaseException {
		getAelItemHorarioAgendadoRN().cancelarHorariosExamesAgendados(
				itemHorarioAgendado, globalUnfSeq, nomeMicrocomputador);
	}

	private AelItemHorarioAgendadoRN getAelItemHorarioAgendadoRN() {
		return aelItemHorarioAgendadoRN;
	}

	@Override
	public void persistirItemSolicCartas(
			final AelItemSolicCartas aelItemSolicCartas, final Boolean flush )
			throws ApplicationBusinessException, ApplicationBusinessException {
		getAelItemSolicCartasRN().persistirAelItemSolicCartas(
				aelItemSolicCartas, flush);
	}

	private AelItemSolicCartasRN getAelItemSolicCartasRN() {
		return aelItemSolicCartasRN;
	}

	protected RelatorioTicketAreaExecutoraON getRelatorioTicketAreaExecutoraON() {
		return relatorioTicketAreaExecutoraON;
	}

	@Override
	public List<RelatorioTicketAreaExecutoraVO> pesquisarRelatorioTicketAreaExecutora(
			final Integer soeSeq, final Short unfSeq, String nomeMicrocomputador ) throws BaseException {
		return getRelatorioTicketAreaExecutoraON()
				.pesquisarRelatorioTicketAreaExecutora(soeSeq, unfSeq, nomeMicrocomputador);
	}

	@Override
	@BypassInactiveModule
	public List<AelProjetoPesquisas> pesquisarTodosProjetosPesquisa(
			final String strPesquisa) {
		return getAelProjetoPesquisasDAO().pesquisarTodosProjetosPesquisa(
				strPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaAgendaCirurgiaPorNumeroNome(Object objPesquisa) {
		return getAelProjetoPesquisasDAO().pesquisarProjetosPesquisaAgendaCirurgiaPorNumeroNome(objPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarProjetosPesquisaAgendaCirurgiaPorNumeroNomeCount(Object objPesquisa) {
		return getAelProjetoPesquisasDAO().pesquisarProjetosPesquisaAgendaCirurgiaPorNumeroNomeCount(objPesquisa);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarTodosProjetosPesquisaCount(final String strPesquisa) {
		return getAelProjetoPesquisasDAO().pesquisarTodosProjetosPesquisaCount(
				strPesquisa);
	}

	protected AelProjetoPesquisasDAO getAelProjetoPesquisasDAO() {
		return aelProjetoPesquisasDAO;
	}

	protected RelatorioPacientesInternadosExamesRealizarON getRelatorioPacientesInternadosExamesRealizarON() {
		return relatorioPacientesInternadosExamesRealizarON;
	}

	@Override
	@Secure("#{s:hasPermission('emitirRelacaoPacientesInternadosExamesRealizar','executar')}")
	public RelatorioPacientesInternadosExamesRealizarVO pesquisarRelatorioPacientesInternadosExamesRealizar(
			final AghUnidadesFuncionais unidadeFuncional,
			final DominioSimNao imprimeRecomendacoesExame,
			final Boolean jejumNpo, final Boolean preparo,
			final Boolean dietaDiferenciada, final Boolean unidadeEmergencia,
			final Boolean todos) throws ApplicationBusinessException {
		return getRelatorioPacientesInternadosExamesRealizarON()
				.pesquisarRelatorioPacientesInternadosExamesRealizar(
						unidadeFuncional, imprimeRecomendacoesExame, jejumNpo,
						preparo, dietaDiferenciada, unidadeEmergencia, todos);
	}

	protected AtendimentoExternoON getAtendimentoExternoON() {
		return atendimentoExternoON;
	}

	protected RelatorioSolicitacaoExamesCertificacaoDigitalON getRelatorioSolicitacaoExamesCertificacaoDigitalON() {
		return relatorioSolicitacaoExamesCertificacaoDigitalON;
	}

	@Override
	public List<TicketExamesPacienteVO> pesquisarRelatorioSolicitacaoExamesCertificacaoDigital(
			final Integer codSolicitacao) throws ApplicationBusinessException {
		return getRelatorioSolicitacaoExamesCertificacaoDigitalON()
				.pesquisarRelatorioSolicitacaoExame(codSolicitacao);
	}

	@Override
	@BypassInactiveModule
	public String buscarLaudoProntuarioPaciente(
			final AelSolicitacaoExames solicitacaoExames) {
		return getRelatorioTicketExamesPacienteON()
				.buscarLaudoProntuarioPaciente(solicitacaoExames);
	}
	
	@Override
	@BypassInactiveModule
	public String buscarLaudoProntuarioPacienteHist(
			final AelSolicitacaoExamesHist solicitacaoExames) {
		return getRelatorioTicketExamesPacienteON()
				.buscarLaudoProntuarioPacienteHist(solicitacaoExames);
	}

	@Override
	@BypassInactiveModule
	public String buscarLaudoNomePaciente(
			final AelSolicitacaoExames solicitacaoExames) {
		return getRelatorioTicketExamesPacienteON().buscarLaudoNomePaciente(
				solicitacaoExames);
	}
	
	@Override
	@BypassInactiveModule
	public Date buscarLaudoDataNascimento(
			final AelSolicitacaoExames solicitacaoExames) {
		return getRelatorioTicketExamesPacienteON().buscarLaudoDataNascimento(
				solicitacaoExames);
	}
	
	@Override
	@BypassInactiveModule
	public String buscarNomeServ(final Integer matricula, final Short vinCodigo){
		return getRelatorioTicketExamesPacienteON().buscarNomeServ(matricula, vinCodigo);
	}
	
	@Override
	@BypassInactiveModule
	public String buscarNroRegistroConselho(final Short vinCodigo, final Integer matricula, boolean verificaDataFimVinculo){
		return getRelatorioTicketExamesPacienteON().buscarNroRegistroConselho(vinCodigo, matricula, Boolean.FALSE);
	}
	
	public String buscarLaudoNomeMaeRecemNascido(final AelSolicitacaoExames solicitacaoExames){
		return getRelatorioTicketExamesPacienteON().buscarLaudoNomeMaeRecemNascido(solicitacaoExames);
	}
	
	@Override
	public String buscarLaudoNomeMaeRecemNascidoHist(final AelSolicitacaoExamesHist solicitacaoExames){
		return getRelatorioTicketExamesPacienteON().buscarLaudoNomeMaeRecemNascidoHist(solicitacaoExames);
	}
	
	@Override
	@BypassInactiveModule
	public String buscarLaudoNomePacienteHist(
			final AelSolicitacaoExamesHist solicitacaoExames) {
		return getRelatorioTicketExamesPacienteON().buscarLaudoNomePacienteHist(
				solicitacaoExames);
	}

	@Override
	@BypassInactiveModule
	public Integer buscarLaudoCodigoPaciente(
			final AelSolicitacaoExames solicitacaoExames) {
		return getRelatorioTicketExamesPacienteON().buscarLaudoCodigoPaciente(
				solicitacaoExames);
	}
	
	@Override
	@BypassInactiveModule
	public Integer buscarLaudoCodigoPacienteHist(
			final AelSolicitacaoExamesHist solicitacaoExames) {
		return getRelatorioTicketExamesPacienteON().buscarLaudoCodigoPacienteHist(
				solicitacaoExames);
	}

	/*
	 * Listar Extrato de amostras
	 */

	@Override
	@BypassInactiveModule
	public List<AelExtratoAmostrasVO> pesquisarAelExtratosAmostrasVOPorAmostra(
			final Integer amoSoeSeq, final Short amoSeqp, Boolean isHist) {
		return getAelExtratoAmostrasON().buscarAelExtratosAmostrasVOPorAmostra(
				amoSoeSeq, amoSeqp, isHist);
	}

	protected AelExtratoAmostrasON getAelExtratoAmostrasON() {
		return aelExtratoAmostrasON;
	}

	@Override
	public List<AelSalasExecutorasExames> pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidade(
			final Object parametro, final AghUnidadesFuncionais unidadeExecutora) {
		return getAelSalasExecutorasExamesDAO()
				.pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidade(
						(String) parametro, unidadeExecutora);
	}
	
	@Override
	public List<AelSalasExecutorasExames> pesquisarSalasExecutorasExamesPorNumeroEUnidade(
			final Object parametro, final AghUnidadesFuncionais unidadeExecutora) {
		return getAelSalasExecutorasExamesDAO().pesquisarSalasExecutorasExamesPorNumeroEUnidade((String) parametro, unidadeExecutora);
	}
	
	@Override
	public List<AelSalasExecutorasExames> pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidadeAtivos(
			final Object parametro, final AghUnidadesFuncionais unidadeExecutora) {
		return getAelSalasExecutorasExamesDAO()
				.pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidadeAtivos(
						(String) parametro, unidadeExecutora);
	}

	@Override
	public List<AelGrupoExames> pesquisarGrupoExamePorCodigoOuDescricaoEUnidade(
			final Object parametro, final AghUnidadesFuncionais unidadeExecutora) {
		return getAelGrupoExamesDAO()
				.pesquisarGrupoExamePorCodigoOuDescricaoEUnidade(
						(String) parametro, unidadeExecutora);
	}
	
	@Override
	public List<AelGrupoExames> pesquisarGrupoExamePorCodigoOuDescricaoEUnidadeAtivos(
			final Object parametro, final AghUnidadesFuncionais unidadeExecutora) {
		return getAelGrupoExamesDAO()
				.pesquisarGrupoExamePorCodigoOuDescricaoEUnidadeAtivos(
						(String) parametro, unidadeExecutora);
	}

	@Override
	public List<VAelUnfExecutaExames> pesquisarPorSiglaOuMaterialOuExameOuUnidade(
			final Object parametro) {
		return getVAelUnfExecutaExamesDAO()
				.pesquisarPorSiglaOuMaterialOuExameOuUnidade((String) parametro);
	}

	@Override
	public Long pesquisarPorSiglaOuMaterialOuExameOuUnidadeCount(
			final Object parametro) {
		return getVAelUnfExecutaExamesDAO()
				.pesquisarPorSiglaOuMaterialOuExameOuUnidadeCount((String) parametro);
	}

	@Override
	public List<VAelUnfExecutaExames> pesquisarExamePorDescricaoOuSigla(
			final Object parametro) {
		return getVAelUnfExecutaExamesDAO()
				.pesquisarExamePorDescricaoOuSigla((String) parametro);
	}

	@Override
	public List<VAelUnfExecutaExames> pesquisarExamePorSiglaOuDescricao(
			final Object parametro) {
		return getVAelUnfExecutaExamesDAO()
				.pesquisarExamePorSiglaOuDescricao((String) parametro);
	}

	@Override
	public Long pesquisaSalasExecutorasExamesCount(
			final AelSalasExecutorasExames filtro) {
		return getAelSalasExecutorasExamesDAO()
				.pesquisaSalasExecutorasExamesCount(filtro);
	}

	@Override
	public List<AelSalasExecutorasExames> pesquisaSalasExecutorasExames(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelSalasExecutorasExames filtro) {
		return getAelSalasExecutorasExamesDAO().pesquisaSalasExecutorasExames(
				firstResult, maxResult, orderProperty, asc, filtro);
	}

	/*
	 * Listar Amostras Exames
	 * 
	 * @throws BaseException
	 */
	@Override
	@BypassInactiveModule
	public List<AelAmostrasVO> listarAmostrasExamesVO(final Integer soqSeq,
			Boolean isHist) throws BaseException {
		return getAelAmostrasRN().listarAmostrasExamesVO(soqSeq, isHist);
	}
	
	@Override
	@BypassInactiveModule
	public List<AelAmostrasVO> listarAmostrasExamesVOPorAtendimento(final Short hedGaeUnfSeq,
			final Integer hedGaeSeqp, final Date hedDthrAgenda, Boolean isHist) throws BaseException {
		return getAelAmostrasRN().listarAmostrasExamesVOPorAtendimento(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, isHist);
	}

	@Override
	@BypassInactiveModule
	public List<AelAmostraExamesVO> listarItensAmostra(final Integer soeSeq,
			final Short iseSeqp, final Integer amoSeqp, Boolean isHist) throws BaseException {
		return getAelAmostrasRN().buscarItensAmostraExame(soeSeq,
				iseSeqp, amoSeqp, isHist);
	}

	protected AelMotivoCancelaExamesDAO getAelMotivoCancelaExamesDAO() {
		return aelMotivoCancelaExamesDAO;
	}

	@Override
	@Secure("#{s:hasPermission('manterMotivosCancelamento','pesquisar')}")
	public Long pesquisarMotivoCancelamentoCount(final AelMotivoCancelaExames aelMotivoCancelaExames) {
		return getAelMotivoCancelaExamesDAO().pesquisarMotivoCancelamentoCount(aelMotivoCancelaExames);
	}

	@Override
	@Secure("#{s:hasPermission('manterMotivosCancelamento','pesquisar')}")
	public List<AelMotivoCancelaExames> pesquisarMotivoCancelamento(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelMotivoCancelaExames aelMotivoCancelaExames) {
		return getAelMotivoCancelaExamesDAO().pesquisarMotivoCancelamento(
				firstResult, maxResult, orderProperty, asc,
				aelMotivoCancelaExames);
	}

	@Override
	@BypassInactiveModule
	public AelMotivoCancelaExames obterMotivoCancelamentoPeloId(
			final Short codigoMotivoCancelamentoExclusao) {
		return getAelMotivoCancelaExamesDAO().obterPeloId(
				codigoMotivoCancelamentoExclusao);
	}

	/*
	 * Manter Grupo de Exames
	 */

	@Override
	@Secure("#{s:hasPermission('grupoExames','pesquisar')}")
	public Long pesquisarAelGrupoExameTecnicasCount(
			final AelGrupoExameTecnicas elemento) {
		return getAelGrupoExameTecnicasDAO().pesquisarCount(elemento);
	}

	@Override
	@BypassInactiveModule
	public boolean possuiExameCancelamentoSolicitante(final Integer soeSeq,
			final Short iseSoeSeq) {
		return getAelItemSolicitacaoExamesDAO()
				.possuiExameCancelamentoSolicitante(soeSeq, iseSoeSeq);
	}

	@Override
	@Secure("#{s:hasPermission('grupoExames','pesquisar')}")
	public List<AelGrupoExameTecnicas> pesquisarAelGrupoExameTecnicas(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelGrupoExameTecnicas elemento) {
		return getAelGrupoExameTecnicasDAO().pesquisarAelGrupoExameTecnicas(
				firstResult, maxResult, orderProperty, asc, elemento);
	}

	@Override
	public AelGrupoExameTecnicas obterAelGrupoExameTecnicasPeloId(
			final Integer seq) {
		return this.getAelGrupoExameTecnicasDAO().obterPeloId(seq);
	}

	@Override
	public AelGrpTecnicaUnfExames buscarAelGrpTecnicaUnfExamesPorId(
			final AelGrpTecnicaUnfExamesId id) {
		return getAelGrpTecnicaUnfExamesDAO()
				.buscarAelGrpTecnicaUnfExamesPorId(id);
	}

	protected AelGrupoExameTecnicasDAO getAelGrupoExameTecnicasDAO() {
		return aelGrupoExameTecnicasDAO;
	}

	protected AelGrpTecnicaUnfExamesDAO getAelGrpTecnicaUnfExamesDAO() {
		return aelGrpTecnicaUnfExamesDAO;
	}

	@Override
	public List<AelGrupoExameTecnicas> obterGrupoExameTecnicasPorDescricao(
			final Object objPesquisa) {
		return this.getAelGrupoExameTecnicasDAO().obterGrupoPorCodigoDescricao(
				(String) objPesquisa);
	}

	@Override
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSituacaoLiberado(
			final Object solicitacaoExameSeq, final String situacaoExameLiberado) {
		return getAelItemSolicitacaoExamesDAO()
				.pesquisarItemSolicitacaoExamePorSituacaoLiberado(
						solicitacaoExameSeq, situacaoExameLiberado);
	}

	@Override
	public Long pesquisarItemSolicitacaoExamePorSituacaoLiberadoCount(
			final Object solicitacaoExameSeq, final String situacaoExameLiberado) {
		return getAelItemSolicitacaoExamesDAO()
				.pesquisarItemSolicitacaoExamePorSituacaoLiberadoCount(
						solicitacaoExameSeq, situacaoExameLiberado);
	}

	/*
	 * #5364 Manter Campo Laudo
	 */

	@Override
	public AelCampoLaudo obterCampoLaudoPorSeq(final Integer seq) {
		return getAelCampoLaudoDAO().obterCampoLaudoPorSeq(seq);
	}

	@Override
	@Secure("#{s:hasPermission('manterCampoLaudo','pesquisar')}")
	public Long pesquisarCampoLaudoCount(final AelCampoLaudo campoLaudo) {
		return getAelCampoLaudoDAO().pesquisarCampoLaudoCount(campoLaudo);
	}

	@Override
	@Secure("#{s:hasPermission('atualizarCampoLaudoFluxograma','pesquisar')}")
	public Long pesquisarCampoLaudoFluxogramaCount(
			final AelCampoLaudo campoLaudo) {
		return getAelCampoLaudoDAO().pesquisarCampoLaudoFluxogramaCount(
				campoLaudo);
	}

	@Override
	@Secure("#{s:hasPermission('manterCampoLaudo','pesquisar')}")
	public List<AelCampoLaudo> pesquisarCampoLaudo(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final AelCampoLaudo campoLaudo) {
		return getAelCampoLaudoDAO().pesquisarCampoLaudo(firstResult,
				maxResult, orderProperty, asc, campoLaudo);
	}

	@Override
	@Secure("#{s:hasPermission('atualizarCampoLaudoFluxograma','pesquisar')}")
	public List<AelCampoLaudo> pesquisarCampoLaudoFluxograma(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelCampoLaudo campoLaudo) {
		return getAelCampoLaudoDAO().pesquisarCampoLaudoFluxograma(firstResult,
				maxResult, orderProperty, asc, campoLaudo);
	}

	@Override
	public List<AelGrupoResultadoCaracteristica> pesquisarGrupoResultadoCaracteristicaPorSeqDescricao(
			final Object objPesquisa) {
		return this.getAelGrupoResultadoCaracteristicaDAO()
				.pesquisarGrupoResultadoCaracteristicaPorSeqDescricao(
						objPesquisa);
	}

	@Override
	public List<AelGrupoResultadoCodificado> pesquisarGrupoResultadoCodificadoPorSeqDescricao(
			final Object objPesquisa) {
		return this.getAelGrupoResultadoCodificadoDAO()
				.pesquisarGrupoResultadoCodificadoPorSeqDescricao(objPesquisa);
	}

	private AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}

	private AelGrupoResultadoCaracteristicaDAO getAelGrupoResultadoCaracteristicaDAO() {
		return aelGrupoResultadoCaracteristicaDAO;
	}

	private AelGrupoResultadoCodificadoDAO getAelGrupoResultadoCodificadoDAO() {
		return aelGrupoResultadoCodificadoDAO;
	}

	@Override
	public AelCampoLaudo obterAelCampoLaudoId(final Integer seq) {
		return this.getAelCampoLaudoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<AelCampoLaudo> pesquisarAelCampoLaudoTipo(final DominioTipoCampoCampoLaudo tipo, final Object pesquisa) {
		return this.getAelCampoLaudoDAO().pesquisarAelCampoLaudoTipo(tipo, pesquisa);
	}

	/*
	 * #5366 Manter Valores de Normalidade do Campo Laudo
	 */
	@Override
	@Secure("#{s:hasPermission('manterValoresNormalidadeCampo','pesquisar')}")
	public List<AelValorNormalidCampo> pesquisarNormalidadesCampoLaudo(
			final Integer seq) {
		return getAelValorNormalidCampoDAO().pesquisarNormalidadesCampoLaudo(
				seq);
	}

	private AelValorNormalidCampoDAO getAelValorNormalidCampoDAO() {
		return aelValorNormalidCampoDAO;
	}

	@Override
	@Secure("#{s:hasPermission('manterValoresNormalidadeCampo','executar')}")
	public void inserirValoresNormalidadeCampo(
			final AelValorNormalidCampo valorNormalidCampo )
			throws BaseException {
		getAelValorNormalidCampoRN().inserir(valorNormalidCampo);
	}

	@Override
	@Secure("#{s:hasPermission('manterValoresNormalidadeCampo','executar')}")
	public void atualizarValoresNormalidadeCampo(
			final AelValorNormalidCampo valorNormalidCampo)
			throws BaseException {
		getAelValorNormalidCampoRN().atualizar(valorNormalidCampo);
	}

	private AelValorNormalidCampoRN getAelValorNormalidCampoRN() {
		return aelValorNormalidCampoRN;
	}

	/*
	 * #5365 Manter Sinônimos de Campo Laudo
	 */

	@Override
	@Secure("#{s:hasPermission('manterSinonimosCampoLaudo','pesquisar')}")
	public List<AelSinonimoCampoLaudo> pesquisarSinonimoCampoLaudoPorCampoLaudo(
			final AelCampoLaudo campoLaudo) {
		return this.getAelSinonimoCampoLaudoDAO()
				.pesquisarSinonimoCampoLaudoPorCampoLaudo(campoLaudo);
	}

	private AelSinonimoCampoLaudoDAO getAelSinonimoCampoLaudoDAO() {
		return aelSinonimoCampoLaudoDAO;
	}

	/*
	 * 2207 - Manter Metodos
	 */

	@Override
	@Secure("#{s:hasPermission('manterMetodos','pesquisar')}")
	public List<AelMetodo> pesquisarMetodo(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final AelMetodo elemento) {
		return getAelMetodoDAO().pesquisar(firstResult, maxResult,
				orderProperty, asc, elemento);
	}

	@Override
	@Secure("#{s:hasPermission('manterMetodos','pesquisar')}")
	public Long pesquisarMetodoCount(final AelMetodo aelMetodo) {
		return getAelMetodoDAO().pesquisarCount(aelMetodo);
	}

	@Override
	public AelMetodo obterAelMetodoId(final Short codigo) {
		return this.getAelMetodoDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('manterMetodos','executar')}")
	public void atualizarMetodo(final AelMetodo metodo ) throws BaseException {
		getAelMetodoRN().atualizar(metodo);
	}

	@Override
	@Secure("#{s:hasPermission('manterMetodos','executar')}")
	public void inserirMetodo(final AelMetodo metodo ) throws BaseException {
		getAelMetodoRN().inserir(metodo);
	}
	
	@Override
	public void ativarInativarAleMetodo(AelMetodo metodo) throws BaseException {
		getAelMetodoRN().ativarInativarAleMetodo(metodo);
	}

	/*
	 * #5367 - Manter Título do Resultado Padrão
	 */

	@Override
	public AelResultadosPadrao obterResultadosPadraoPorSeq(final Integer seq) {
		return getAelResultadosPadraoDAO().obterResultadosPadraoPorSeq(seq);
	}

	@Override
	@Secure("#{s:hasPermission('manterTituloResultadoPadrao','pesquisar')}")
	public Long pesquisarResultadosPadraoCount(
			final AelResultadosPadrao resultadosPadrao) {
		return this.getAelResultadosPadraoDAO().pesquisarResultadosPadraoCount(
				resultadosPadrao);
	}

	@Override
	@Secure("#{s:hasPermission('manterTituloResultadoPadrao','pesquisar')}")
	public List<AelResultadosPadrao> pesquisarResultadosPadrao(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AelResultadosPadrao resultadosPadrao) {
		return this.getAelResultadosPadraoDAO().pesquisarResultadosPadrao(
				firstResult, maxResult, orderProperty, asc, resultadosPadrao);
	}

	private AelResultadosPadraoDAO getAelResultadosPadraoDAO() {
		return aelResultadosPadraoDAO;
	}

	/*
	 * #5354 - Manter Número de Cópias para Resultado de Exame
	 */

	@Override
	@Secure("#{s:hasPermission('manterNumeroCopiasResultadoExame','pesquisar')}")
	public List<AelCopiaResultados> pesquisarCopiaResultadosPorExameMaterialAnalise(
			final AelExamesMaterialAnalise exameMaterialAnalise) {
		return this.getAelCopiaResultadosDAO()
				.pesquisarCopiaResultadosPorExameMaterialAnalise(
						exameMaterialAnalise);
	}

	protected AelCopiaResultadosDAO getAelCopiaResultadosDAO() {
		return aelCopiaResultadosDAO;
	}

	@Override
	public AelCopiaResultados obterAelCopiaResultadosId(
			final AelCopiaResultadosId id) {
		return this.getAelCopiaResultadosDAO().obterPorChavePrimaria(id);
	}

	@Override
	@Secure("#{s:hasPermission('configurarFluxograma','executar')}")
	public void persistirExamesFluxogramaSelecionados(
			final RapServidores servidor,
			final List<ExameSelecionadoFluxogramaVO> listaExamesSelecionados)
			throws BaseException {
		this.getConfigurarFluxogramaON().persistirExamesFluxogramaSelecionados(
				servidor, listaExamesSelecionados);
	}

	@Override
	@Secure("#{s:hasPermission('configurarFluxograma','pesquisar') or s:hasPermission('configurarFluxogramaEspecialidade','pesquisar')}")
	public List<ExameDisponivelFluxogramaVO> pesquisarExamesDisponiveisFluxograma() {
		return this.getConfigurarFluxogramaON()
				.pesquisarExamesDisponiveisFluxograma();
	}
	
	public boolean pertenceAoFluxograma(final Integer campoLaudoSeq){
		return this.getConfigurarFluxogramaON().pertenceAoFluxograma(campoLaudoSeq);
	}

	@Override
	@Secure("#{s:hasPermission('configurarFluxograma','pesquisar')}")
	public List<ExameSelecionadoFluxogramaVO> pesquisarExamesSelecionadosPorServidorLogado() throws ApplicationBusinessException {
		return this.getConfigurarFluxogramaON()
				.pesquisarExamesSelecionadosPorServidorLogado();
	}

	protected ConfigurarFluxogramaON getConfigurarFluxogramaON() {
		return configurarFluxogramaON;
	}

	protected AelServidorCampoLaudoDAO getAelServidorCampoLaudoDAO() {
		return aelServidorCampoLaudoDAO;
	}

	/*
	 * #10584 Configurar Fluxograma Especialidade
	 */

	@Override
	@Secure("#{s:hasPermission('configurarFluxogramaEspecialidade','executar')}")
	public void persistirExamesFluxogramaSelecionadosPorEspecialidade(
			final AghEspecialidades especialidade,
			final List<ExameEspecialidadeSelecionadoFluxogramaVO> listaExamesSelecionados,
			List<ExameEspecialidadeSelecionadoFluxogramaVO> listaExamesRemovidos)
			throws BaseException {
		this.getConfigurarFluxogramaEspecialidadeON()
				.persistirExamesFluxogramaSelecionadosPorEspecialidade(
						especialidade, listaExamesSelecionados, listaExamesRemovidos);
	}

	@Override
	@Secure("#{s:hasPermission('configurarFluxogramaEspecialidade','pesquisar')}")
	public List<ExameEspecialidadeSelecionadoFluxogramaVO> pesquisarExamesEspeciadadeSelecionadosPorEspecialidade(
			final AghEspecialidades especialidade) {
		return this.getConfigurarFluxogramaEspecialidadeON()
				.pesquisarExamesEspeciadadeSelecionadosPorEspecialidade(
						especialidade);
	}

	protected ConfigurarFluxogramaEspecialidadeON getConfigurarFluxogramaEspecialidadeON() {
		return configurarFluxogramaEspecialidadeON;
	}

	/**
	 * 
	 * ORADB TRIGGER AELT_AIX_BRI
	 * 
	 * @param entity
	 * @return
	 * @throws ApplicationBusinessException 
	 * @throws ApplicationBusinessException 
	 * @throws BaseException
	 */
	@Override
	public AelInformacaoSolicitacaoUnidadeExecutora inserirInformacaoSolicitacaoUnidadeExecutoraRN(
			final AelInformacaoSolicitacaoUnidadeExecutora entity ) throws ApplicationBusinessException, ApplicationBusinessException {
		return getInformacaoSolicitacaoUnidadeExecutoraRN().inserir(entity);
	}

	protected InformacaoSolicitacaoUnidadeExecutoraRN getInformacaoSolicitacaoUnidadeExecutoraRN() {
		return informacaoSolicitacaoUnidadeExecutoraRN;
	}

	protected ModuloExamesRN getModuloExamesRN() {
		return moduloExamesRN;
	}

	/**
	 * oradb aelc_busca_conv_plan
	 * 
	 * @param seq
	 * @param cnvCodigo
	 * @return
	 */
	@Override
	public String buscarConvenioPlano(
			final FatConvenioSaudePlano convenioSaudePlano) {
		return getRelatorioTicketExamesPacienteON().buscarConvenioPlano(
				convenioSaudePlano);
	}

	/**
	 * ORADB: AELC_GET_PROJ_ATEND ESCHWEIGERT -> (23/04/2012) Deve ser público e
	 * mapeado para outros programas poderem utilizar.
	 * 
	 * @param atendimento
	 * @param atendimentoDiversos
	 * @return
	 */
	@Override
	public String aelcGetProjAtend(final Integer atdSeq, final Integer atvSeq) {
		return getRelatorioTicketExamesPacienteON().aelcGetProjAtend(atdSeq,
				atvSeq);
	}

	@Override
	public AelVersaoLaudo obterVersaoLaudoPorChavePrimaria(
			final AelVersaoLaudoId versaoLaudoPK) {
		return this.getVersaoLaudoDAO().obterPorChavePrimaria(versaoLaudoPK);
	}

	@Override
	public AelVersaoLaudo obterVersaoLaudoComDependencias(final AelVersaoLaudoId versaoLaudoPK) {
		return this.getVersaoLaudoDAO().obterVersaoLaudoComDependencias(versaoLaudoPK);
	}
	
	private AelVersaoLaudoDAO getVersaoLaudoDAO() {
		return aelVersaoLaudoDAO;
	}

	private AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return aelResultadoCodificadoDAO;
	}

	private AelResultadoCodificadoRN getAelResultadoCodificadoRN() {
		return aelResultadoCodificadoRN;
	}

	private AelGrupoResultadoCodificadoRN getAelGrupoResultadoCodificadoRN() {
		return aelGrupoResultadoCodificadoRN;
	}

	@Override
	public AelExameGrupoCaracteristica obterAelExameGrupoCaracteristicaPorId(
			final AelExameGrupoCaracteristicaId grpCaractId) {
		return getAelExameGrupoCaracteristicaDAO().obterPorChavePrimaria(
				grpCaractId);
	}

	private AelExameGrupoCaracteristicaDAO getAelExameGrupoCaracteristicaDAO() {
		return aelExameGrupoCaracteristicaDAO;
	}

	@Override
	@Secure("#{s:hasPermission('manterResultadosCodificados','pesquisar')}")
	public List<AelResultadoCodificado> pesquisaResultadosCodificadosPorParametros(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final ResultadosCodificadosVO filtroResultado) {
		return this.getAelResultadoCodificadoDAO()
				.pesquisaResultadosCodificadosPorParametros(firstResult,
						maxResult, orderProperty, asc, filtroResultado);
	}

	@Override
	@Secure("#{s:hasPermission('manterResultadosCodificados','pesquisar')}")
	public List<AelGrupoResultadoCodificado> pesquisaGrupoResultadosCodificadosPorParametros(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final ResultadosCodificadosVO filtroResultado) {
		return this.getAelGrupoResultadoCodificadoDAO()
				.pesquisaGrupoResultadosCodificadosPorParametros(firstResult,
						maxResult, orderProperty, asc, filtroResultado);
	}

	@Override
	@Secure("#{s:hasPermission('manterResultadosCodificados','executar')}")
	public void persistirResultadoCodificado(
			final AelResultadoCodificado resultado ) throws BaseException {
		getAelResultadoCodificadoRN().persistirResultadoCodificado(resultado);
	}

	@Override
	@Secure("#{s:hasPermission('manterResultadosCodificados','executar')}")
	public void persistirGrupoResultadoCodificado(
			final AelGrupoResultadoCodificado grupoResultado )
			throws BaseException {
		getAelGrupoResultadoCodificadoRN().persistirGrupoResultadoCodificado(
				grupoResultado);
	}

	@Override
	@Secure("#{s:hasPermission('manterResultadosCodificados','executar')}")
	public void removerResultadoCodificado(
			final AelResultadoCodificadoId resultado) throws BaseException {
		getAelResultadoCodificadoRN().removerResultadoCodificado(resultado);
	}

	@Override
	@Secure("#{s:hasPermission('manterResultadosCodificados','executar')}")
	public void removerGrupoResultadoCodificado(
			final Integer grupoResultado)
			throws BaseException {
		getAelGrupoResultadoCodificadoRN().removerGrupoResultadoCodificado(
				grupoResultado);
	}

	@Override
	public AelResultadoCodificado obterResultadoCodificadoPorId(
			final AelResultadoCodificadoId resulCodId) {
		return getAelResultadoCodificadoDAO().obterPorChavePrimaria(resulCodId);
	}

	@Override
	public AelGrupoResultadoCodificado obterGrupoResultadoCodificadoPorSeq(
			final Integer grupoResulCodId) {
		return getAelGrupoResultadoCodificadoDAO().obterPorChavePrimaria(
				grupoResulCodId);
	}

	@Override
	@Secure("#{s:hasPermission('manterResultadosCodificados','pesquisar')}")
	public Long pesquisaResultadosCodificadosPorParametrosCount(
			final ResultadosCodificadosVO filtroResultado) {
		return this.getAelResultadoCodificadoDAO()
				.pesquisaResultadosCodificadosPorParametrosCount(
						filtroResultado);
	}

	@Override
	@Secure("#{s:hasPermission('manterResultadosCodificados','pesquisar')}")
	public Long pesquisaGrupoResultadosCodificadosPorParametrosCount(
			final ResultadosCodificadosVO filtroResultado) {
		return this.getAelGrupoResultadoCodificadoDAO()
				.pesquisaGrupoResultadosCodificadosPorParametrosCount(
						filtroResultado);
	}


	@Override
	public Long listarMascarasExamesVOCount(final Integer soeSeq,
			final Short seqp, final AelExames exame,
			final AelMateriaisAnalises materialAnalise,
			final AipPacientes paciente) {
		return this.getAelItemSolicitacaoExamesDAO()
				.listarMascarasExamesVOCount(soeSeq, seqp, exame,
						materialAnalise, paciente);
	}

	@Override
	public List<MascaraExameVO> listarMascarasExamesVO(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer soeSeq, final Short seqp, final AelExames exame,
			final AelMateriaisAnalises materialAnalise,
			final AipPacientes paciente) {
		return this.getAelItemSolicitacaoExamesDAO().listarMascarasExamesVO(
				firstResult, maxResult, orderProperty, asc, soeSeq, seqp,
				exame, materialAnalise, paciente);
	}

	@Override
	public AelItemSolicitacaoExameRelLaudoUnicoVO obterAelItemSolicitacaoExameRelLaudoUnicoVOPorLuxSeq(
			final Long seq) {
		return this.getAelItemSolicitacaoExamesDAO()
				.obterAelItemSolicitacaoExameRelLaudoUnicoVOPorLuxSeq(seq);
	}

	@Override
	public AelSalasExecutorasExames obterSalaExecutoraExamesPorId(
			final AelSalasExecutorasExamesId id) {
		return this.getAelSalasExecutorasExamesDAO().obterPorId(id);
	}

	@Override
	public Long listarExamesCount(final String strPesquisa) {
		return this.getAelExamesDAO().listarExamesCount(strPesquisa);
	}

	@Override
	public List<AelExames> listarExames(final String strPesquisa) {
		return this.getAelExamesDAO().listarExames(strPesquisa);
	}

	@Override
	public Long listarMateriaisAnaliseCount(final String strPesquisa) {
		return this.getAelMaterialAnaliseDAO().listarMateriaisAnaliseCount(
				strPesquisa);
	}

	@Override
	public List<AelMateriaisAnalises> listarMateriaisAnalise(
			final String strPesquisa) {
		return this.getAelMaterialAnaliseDAO().listarMateriaisAnalise(
				strPesquisa);
	}

	protected AelSalasExecutorasExamesDAO getAelSalasExecutorasExamesDAO() {
		return aelSalasExecutorasExamesDAO;
	}

	@Override
	public List<AelUnidMedValorNormal> pesquisarUnidadesValorNormal(
			final Object parametroConsulta) {
		return this.getAelUnidMedValorNormalDAO()
				.pesquisarSb(parametroConsulta);
	}

	@Override
	public List<AelUnidMedValorNormal> pesquisarUnidadesValorNormal() {
		return this.getAelUnidMedValorNormalDAO().pesquisarSb();
	}

	protected AelMetodoUnfExameDAO getAelMetodoUnfExameDAO() {
		return aelMetodoUnfExameDAO;
	}

	@Override
	public List<AelMetodoUnfExame> obterAelMetodoUnfExamePorUnfExecutaExames(
			final AelUnfExecutaExames unfExecutaExames) {
		return getAelMetodoUnfExameDAO()
				.obterAelMetodoUnfExamePorUnfExecutaExames(unfExecutaExames);
	}

	protected AelMetodoDAO getAelMetodoDAO() {
		return aelMetodoDAO;
	}

	protected AelMetodoRN getAelMetodoRN() {
		return aelMetodoRN;
	}

	@Override
	public List<AelMetodo> obterMetodosPorSerDescricao(final Object parametro) {
		return getAelMetodoDAO().obterMetodosPorSerDescricao(parametro);
	}

	@Override
	@Secure("#{s:hasPermission('manterCaracteristicaResultado','pesquisar')}")
	public Long pesquisarResultadosCaracteristicasCount(
			final AelResultadoCaracteristica resultadoCaracteristica) {
		return getAelResultadoCaracteristicaDAO().pesquisarCount(
				resultadoCaracteristica);
	}

	protected AelResultadoCaracteristicaDAO getAelResultadoCaracteristicaDAO() {
		return aelResultadoCaracteristicaDAO;
	}

	@Override
	@Secure("#{s:hasPermission('manterCaracteristicaResultado','pesquisar')}")
	public List<AelResultadoCaracteristica> pesquisarCaracteristicasResultados(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final AelResultadoCaracteristica elemento) {
		return getAelResultadoCaracteristicaDAO().pesquisar(firstResult, maxResult, orderProperty, asc, elemento);
	}
/*
	@Override
	@BypassInactiveModule
	public List<DesenhoMascaraExameVO> buscaDesenhosMascarasExamesVO(final Integer solicitacaoExameSeq, final Short itemSolicitacaoExameSeq, final Integer velSeqp, Boolean isHist, Map<Integer, NumeroApTipoVO> solicNumeroAp) throws BaseException {
		return this.getMascaraExamesON().buscaDesenhosMascarasExamesVO(solicitacaoExameSeq, itemSolicitacaoExameSeq, velSeqp, isHist, solicNumeroAp);
	}
	
	@Override
	public List<DoubleRangeValidator> obterListaValidatorsValoresNormalidade(AelParametroCamposLaudo parametroCampoLaudo, AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException{
		return this.getMascaraExamesON().obterListaValidatorsValoresNormalidade(parametroCampoLaudo, itemSolicitacaoExame);
	}
	
	public String retornaParametroCampoLaudoTextoLivreSemTag(String textoLivre){
		return getMascaraExamesON().retornaParametroCampoLaudoTextoLivreSemTag(textoLivre);
	}

	@Override
	public List<DesenhoMascaraExameVO> buscarDesenhosMascarasExamesResultadoPadraoVO(AelVersaoLaudo versaoLaudo) throws BaseException {
		return this.getMascaraExamesON().buscarDesenhosMascarasExamesResultadoPadraoVO(versaoLaudo);
	}

	@Override
	public List<DesenhoMascaraExameVO> montaDesenhosMascarasExamesResultadoPadrao(final AelResultadosPadrao resultadoPadrao, final Integer velSeqp, final Integer solicitacaoExameSeq, final Short itemSolicitacaoExameSeq) throws BaseException{
		return this.getMascaraExamesON().montaDesenhosMascarasExamesResultadoPadrao(resultadoPadrao, velSeqp, solicitacaoExameSeq, itemSolicitacaoExameSeq);
	}

	@Override
	public DesenhoMascaraExameVO buscaPreviaMascarasExamesVO(final List<AelParametroCamposLaudo> parametrosPrevia, final AelVersaoLaudo versaoLaudo, Boolean isPdf) throws BaseException{
		return this.getMascaraExamesON().buscaPreviaMascarasExamesVO(parametrosPrevia, versaoLaudo, isPdf, null);
	}

	@Override
	@BypassInactiveModule
	public List<DesenhoMascaraExameVO> buscaDesenhosRelatorioMascarasExamesVO(final Integer solicitacaoExameSeq,
			final Short itemSolicitacaoExameSeq, final DominioSubTipoImpressaoLaudo subTipoImpressaoLaudo, final Boolean isPdf, Boolean isHist, Boolean ultimoItem, List<Short> seqps, Map<Integer, NumeroApTipoVO> solicNumeroAp) throws BaseException {
		return this.getMascaraExamesON().buscaDesenhosRelatorioMascarasExamesVO(solicitacaoExameSeq,itemSolicitacaoExameSeq, subTipoImpressaoLaudo, isPdf, null, isHist, ultimoItem, seqps, solicNumeroAp);
	}
	*/
	/*@Override
	@BypassInactiveModule
	public List<DesenhoMascaraExameHistVO> buscaDesenhosRelatorioMascarasExamesVOHist(final Integer solicitacaoExameSeq,
			final Short itemSolicitacaoExameSeq, final Boolean isPdf) throws BaseException {
		return this.getMascaraExamesHistON().buscaDesenhosRelatorioMascarasExamesVOHist(solicitacaoExameSeq,itemSolicitacaoExameSeq, isPdf, null);
	}*/

	@Override
	public AelResultadoCaracteristica obterAelResultadoCaracteristicaId(final Integer codigo) {
		return this.getAelResultadoCaracteristicaDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public List<AelParametroCamposLaudo> pesquisarCamposTelaEdicaoMascaraPorVersaoLaudo(AelVersaoLaudo versaoLaudo){
		return this.aelParametroCamposLaudoON.pesquisarCamposTelaEdicaoMascaraPorVersaoLaudo(versaoLaudo);
	}
//
//	@Override
//	public String obterStyleParametroCampoLaudo(AelParametroCamposLaudo campo, TipoMascaraExameEnum tipoMascaraExame) {
//		return this.getMascaraExamesON().obterStyleParametroCampoLaudo(campo, tipoMascaraExame);
//	}

	protected AelGrupoExamesDAO getAelGrupoExamesDAO() {
		return aelGrupoExamesDAO;
	}

	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}
	
	protected VAelUnfExecutaExamesDAO getVAelUnfExecutaExamesDAO() {
		return vAelUnfExecutaExamesDAO;
	}

	@Override
	public List<AelTipoMarcacaoExame> pesquisarTipoMarcacaoExame(
			final Object parametro) {
		return getAelTipoMarcacaoExameDAO()
				.pesquisarTipoMarcacaoExameAtivoPorSeqOuDescricaoOrdenado(
						parametro);
	}

//	private MascaraExamesON getMascaraExamesON() {
//		return mascaraExamesON;
//	}
	
	private MascaraExameVersionamentoON getMascaraExameVersionamentoON() {
		return mascaraExameVersionamentoON;
	}

	protected VAelGradeDAO getVAelGradeDAO() {
		return vAelGradeDAO;
	}

	protected AelTipoMarcacaoExameDAO getAelTipoMarcacaoExameDAO() {
		return aelTipoMarcacaoExameDAO;
	}

	@Override
	public void inserirHorarioExameSemFlush(
			final AelHorarioExameDisp horarioExameDisp ) throws BaseException {
		getAelHorarioExameDispRN().inserirSemFlush(horarioExameDisp);
	}

	@Override
	public List<AelPatologista> buscarPatologistasPorAnatomoPatologicos(
			final Long seq) {
		return getAelPatologistaDAO().pesquisarPatologistasPorAnatomoPatologia(
				seq);
	}
	
	@Override
	public List<AelPatologista> listarPatologistaPorSeqNome(final Object valor) {
		return getAelPatologistaDAO().listarPatologistaPorSeqNome(valor);
	}
	
	@Override
	public Long listarPatologistaPorSeqNomeCount(final Object valor) {
		return getAelPatologistaDAO().listarPatologistaPorSeqNomeCount(valor);
	}
	
	@Override
	public List<RelatorioExamesPendentesVO> obterListaExamesPendentes(Date dataInicial, Date dataFinal,
			String situacao, Integer[] patologistaSeq, DominioSituacaoExamePatologia situacaoExmAnd) throws ApplicationBusinessException {
		return getRelatorioExamesPendentesON().obterListaExamesPendentes(dataInicial, dataFinal, situacao, patologistaSeq, situacaoExmAnd);
	}
	
	protected RelatorioExamesPendentesON getRelatorioExamesPendentesON() {
		return relatorioExamesPendentesON;
	}

	protected AelPatologistaDAO getAelPatologistaDAO() {
		return aelPatologistaDAO;
	}

	@Override
	@BypassInactiveModule
	public ConvenioExamesLaudosVO rnAelpBusConvAtv(final Integer atvSeq) {
		return getAelExamesRN().rnAelpBusConvAtv(atvSeq);
	}

	protected AelExamesRN getAelExamesRN() {
		return aelExamesRN;
	}

	@Override
	public void liberarExames(final AelItemSolicitacaoExames itemSolicitacao, String nomeMicrocomputador, AelItemSolicitacaoExames itemOriginal)
			throws BaseException {
		getAelResultadosExamesON().liberarExame(itemSolicitacao, nomeMicrocomputador, itemOriginal);
	}

	protected RelatorioFichaTrabalhoPatologiaON getRelatorioFichaTrabalhoPatologiaON() {
		return relatorioFichaTrabalhoPatologiaON;
	}

	protected RelatorioFichaTrabalhoPatologiaClinicaON getRelatorioFichaTrabalhoPatologiaClinicaON() {
		return relatorioFichaTrabalhoPatologiaClinicaON;
	}
	
	protected RelatorioFichaTrabalhoAmostraON getRelatorioFichaTrabalhoAmostraON() {
		return relatorioFichaTrabalhoAmostraON;
	}

	/*
	 * #14255 Anexar Documentos ao Laudo
	 */

	@Override
	public void anexarDocumentoLaudo(final AelDocResultadoExame doc,
			final AghUnidadesFuncionais unidadeExecutora )
			throws BaseException {
		getAnexarDocumentoLaudoRN().anexarDocumentoLaudo(doc, unidadeExecutora);
	}

	@Override
	public void removerDocumentoLaudo(final AelDocResultadoExame doc,
			final AghUnidadesFuncionais unidadeExecutora )
			throws BaseException {
		getAnexarDocumentoLaudoRN()
				.removerDocumentoLaudo(doc, unidadeExecutora);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> listarItemSolicitacaoExameMarcado(
			final Integer conNumero, final String situacaoCancelado) {
		return getAelItemSolicitacaoExamesDAO()
				.listarItemSolicitacaoExameMarcado(conNumero, situacaoCancelado);
	}

	protected AnexarDocumentoLaudoRN getAnexarDocumentoLaudoRN() {
		return anexarDocumentoLaudoRN;
	}

	@Secure("#{s:hasPermission('pesquisarLogExames','pesquisar')}")
	public List<PesquisaResultadoCargaVO> pesquisarLwsComSolicitacaoExames(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final PesquisaResultadoCargaInterfaceVO filtro) throws ApplicationBusinessException {
		return getLwsComunicacaoDAO().pesquisarLwsComSolicitacaoExames(
				firstResult, maxResult, orderProperty, asc, filtro);
	}

	@Override
	public Long pesquisarLwsComSolicitacaoExamesCount(
			final PesquisaResultadoCargaInterfaceVO filtro) throws ApplicationBusinessException {
		return getLwsComunicacaoDAO()
				.pesquisarLwsComSolicitacaoExamesCount(filtro);
	}

	protected LwsComunicacaoDAO getLwsComunicacaoDAO() {
		return lwsComunicacaoDAO;
	}

	protected LwsComAmostraDAO getLwsComAmostraDAO() {
		return lwsComAmostraDAO;
	}

	// public List<AelAmostraItemExamesVO> pesquisarAmostraItemExames(
	// AghUnidadesFuncionais unidadeExecutora, Integer solicitacao,
	// Integer amostra, AelEquipamentos equipamento, String sigla,
	// DominioSimNao enviado, Integer firstResult, Integer maxResult,
	// String orderProperty, boolean asc, Boolean selecionarTodos) {
	// return this.getAelAmostraItemExamesON().pesquisarAmostraItemExames(
	// unidadeExecutora, solicitacao, amostra, equipamento, sigla,
	// enviado, firstResult, maxResult, orderProperty, asc,
	// selecionarTodos);
	// }

	@Override
	public Long pesquisarAmostraItemExamesCount(
			AghUnidadesFuncionais unidadeExecutora, Integer solicitacao,
			Integer amostra, AelEquipamentos equipamento, String sigla,
			DominioSimNao enviado) {

		return this.getAelAmostraItemExamesON()
				.pesquisarAmostraItemExamesCount(unidadeExecutora, solicitacao,
						amostra, equipamento, sigla, enviado);
	}

	@Override
	public Boolean realizarCargaInterfaceamento(
			Set<AelAmostraItemExamesVO> listAmostraItemExamesVO, String nomeMicrocomputador )
			throws BaseException {
		return this.getAelAmostraItemExamesON().realizarCargaInterfaceamento(
				listAmostraItemExamesVO, nomeMicrocomputador);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> obterSitCodigoPorNumeroConsultaESituacao(
			Integer conNumero, String situacao) {
		return this.getAelItemSolicitacaoExamesDAO()
				.obterSitCodigoPorNumeroConsultaESituacao(conNumero, situacao);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> obterItemSolicitacaoExamesPorNumeroConsultaESituacao(
			Integer conNumero, String situacaoPendente, String situacaoCancelado) {
		return this.getAelItemSolicitacaoExamesDAO()
				.obterItemSolicitacaoExamesPorNumeroConsultaESituacao(
						conNumero, situacaoPendente, situacaoCancelado);
	}

	@Override
	@BypassInactiveModule
	public List<AelSolicitacaoExames> obterSolicitacoesExamesPorConsultaESituacao(
			Integer numeroConsulta, String situacaoPendente,
			String situacaoCancelado) {
		return this.getAelSolicitacaoExameDAO()
				.obterSolicitacoesExamesPorConsultaESituacao(numeroConsulta,
						situacaoPendente, situacaoCancelado);
	}

	@Override
	@BypassInactiveModule
	public List<AelSolicitacaoExames> obterSolicitacoesExamesPorConsultaESituacaoPendente(
			Integer numeroConsulta, String situacaoPendente) {
		return this.getAelSolicitacaoExameDAO()
				.obterSolicitacoesExamesPorConsultaESituacaoPendente(
						numeroConsulta, situacaoPendente);
	}

	@Override
	@BypassInactiveModule
	public List<Integer> obterSeqSolicitacoesExamesPorConsulta(
			Integer numeroConsulta, String vSituacaoPendente,
			String vSituacaoCancelado) {
		return this.getAelSolicitacaoExameDAO()
				.obterSeqSolicitacoesExamesPorConsulta(numeroConsulta,
						vSituacaoPendente, vSituacaoCancelado);
	}

	@Override
	@BypassInactiveModule
	public AelSolicitacaoExames obterAelSolicitacaoExamePorChavePrimaria(
			Integer seq) {
		return getAelSolicitacaoExameDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public List<Integer> obterSeqSolicitacoesExamesPorConsultaCertificacaoDigital(
			Integer numeroConsulta, String vSituacaoPendente) {
		return this.getAelSolicitacaoExameDAO()
				.obterSeqSolicitacoesExamesPorConsultaCertificacaoDigital(
						numeroConsulta, vSituacaoPendente);
	}

	@Override
	public String obterLaudoSexoPaciente(AelSolicitacaoExames solicitacaoExame) {
		return getGerarProtocoloUnicoRN().obterLaudoSexoPaciente(
				solicitacaoExame);
	}
	
	protected GerarProtocoloUnicoRN getGerarProtocoloUnicoRN() {
		return gerarProtocoloUnicoRN;
	}

	protected VAelExameMatAnaliseDAO getVAelExameMatAnaliseDAO() {
		return vAelExameMatAnaliseDAO;
	}
	
	@Override
	@BypassInactiveModule
	public void persistirProjetoPaciente(AelProjetoPacientes projetoPaciente) {
		this.getAelProjetoPacientesDAO().persistir(projetoPaciente);

	}

	@Override
	@BypassInactiveModule
	public AelProjetoPesquisas obterProjetoPesquisaSituacaoData(Integer pjqSeq) {
		return getAelProjetoPesquisasDAO().obterProjetoPesquisaSituacaoData(
				pjqSeq);
	}

	@Override
	@BypassInactiveModule
	public List<AelSolicitacaoExames> buscarSolicitacaoExamesPorAtendimento(
			AghAtendimentos atendimento) {
		return getAelSolicitacaoExameDAO()
				.buscarSolicitacaoExamesPorAtendimento(atendimento);
	}

	@Override
	@BypassInactiveModule
	public Boolean verificarExistenciaRespostasExamesQuestionario(
			String cExaSigla, Integer cManSeq, Short cCspCnvCodigo,
			DominioOrigemAtendimento cOrigemAtend) {
		return getAelExamesQuestionarioDAO()
				.verificarExistenciaRespostasExamesQuestionario(cExaSigla,
						cManSeq, cCspCnvCodigo, cOrigemAtend);
	}

	@Override
	@BypassInactiveModule
	public boolean verificarRespostaQuestao(Integer iseSoeSeq, Short iseSeqp) {
		return getAelRespostaQuestaoDAO().verificarRespostaQuestao(iseSoeSeq,
				iseSeqp);
	}

	@Override
	@BypassInactiveModule
	public List<AelSolicitacaoExames> pesquisarSolicitacaoExamePorItemSolicitacaoExameParaCancelamento(
			Integer conNumero, Date dthrMovimento, Integer soeSeq) {
		return this
				.getAelSolicitacaoExameDAO()
				.pesquisarSolicitacaoExamePorItemSolicitacaoExameParaCancelamento(
						conNumero, dthrMovimento, soeSeq);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSolicitacaoExame(
			final Integer solicitacaoExameSeq) {
		return getAelItemSolicitacaoExamesDAO()
				.pesquisarItemSolicitacaoExamePorSolicitacaoExame(
						solicitacaoExameSeq);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> obterSolicitacoesExamesPorConsultaESituacaoCancelado(
			final Integer numeroConsulta, final String vSituacaoCancelado) {
		return getAelItemSolicitacaoExamesDAO()
				.obterSolicitacoesExamesPorConsultaESituacaoCancelado(
						numeroConsulta, vSituacaoCancelado);
	}

	@Override
	@BypassInactiveModule
	public List<AelExtratoItemSolicitacao> pesquisarExtratoItemSolicitacaoConclusao(
			final Integer iseSoeSeq, final Short iseSeqp) {
		return this.getAelExtratoItemSolicitacaoDAO()
				.pesquisarExtratoItemSolicitacaoConclusao(iseSoeSeq, iseSeqp);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExameParaConclusao(
			final Integer conNumero, final Integer solicitacaoExameSeq,
			final Boolean usaDistinct) {
		return this.getAelItemSolicitacaoExamesDAO()
				.pesquisarItemSolicitacaoExameParaConclusao(conNumero,
						solicitacaoExameSeq, usaDistinct);
	}

	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames obteritemSolicitacaoExamesPorChavePrimaria(
			AelItemSolicitacaoExamesId id) {
		return getAelItemSolicitacaoExamesDAO().obterPorChavePrimaria(id);
	}

    @Override
    @BypassInactiveModule
    public AelItemSolicitacaoExames obterItemSolicitacaoExamePorChavePrimaria(AelItemSolicitacaoExamesId id){
        return getAelItemSolicitacaoExamesDAO().obterItemSolicitacaoExamesPorChavePrimaria(id);
    }

	@Override
	public List<AelPedidoExame> pesquisarAelPedidoExamePorZonaESala(
			Short unfSeq, Byte sala) {
		return this.getAelPedidoExameDAO().pesquisarAelPedidoExamePorZonaESala(
				unfSeq, sala);
	}
	
	@Override
	@BypassInactiveModule
	public List<AelGrupoXMaterialAnalise> pesquisarGrupoXMateriaisAnalisesPorGrupo(
			Integer gmaSeq) {
		return this.getAelGrupoXMaterialAnaliseDAO()
				.pesquisarGrupoXMateriaisAnalisesPorGrupo(gmaSeq);
	}

	@Override
	@BypassInactiveModule
	@Deprecated
	//ATENCAO => o bypass foi usado pra persistir pq na 4.1 foi colocado a POL no HCPA
	//Essa persistencia nao aciona as triggers da tabela AEL_ITEM_SOLIC_CONSULTADOS
	public void persistirAelItemSolicConsultado(
			AelItemSolicConsultado aelItemSolicConsultado) {
		this.getAelItemSolicConsultadoDAO().persistir(aelItemSolicConsultado);
	}

	@Override
	@BypassInactiveModule
	public List<ExameAmostraColetadaVO> obterDadosPorAmostrasColetadas(
			Integer codPaciente, String paramSitCodLiberado,
			String paramSitCodAreaExec) {
		return this.getAelOrdExameMatAnaliseDAO()
				.obterDadosPorAmostrasColetadas(codPaciente,
						paramSitCodLiberado, paramSitCodAreaExec);
	}

	@Override
	@BypassInactiveModule
	public List<ExameAmostraColetadaVO> obterDadosPorAmostrasColetadasHist(
			Integer codPaciente, String paramSitCodLiberado,
			String paramSitCodAreaExec) {
		return this.getAelResultadosExamesON()
				.listarDadosDataExamePorAmostrasColetadasHist(codPaciente,
						paramSitCodLiberado, paramSitCodAreaExec);
	}
	
	@Override
	@BypassInactiveModule
	public Date obterDataExame(Integer soeSeq, Short seqp,
			String sitCodigoLiberado, String sitCodigoAreaExec) {
		return this.getAelOrdExameMatAnaliseDAO().obterDataExame(soeSeq, seqp,
				sitCodigoLiberado, sitCodigoAreaExec);
	}

	@Override
	@BypassInactiveModule
	public List<ExameOrdemCronologicaVO> obterDadosOrdemCronologicaArvorePol(
			Integer codPaciente, String paramSitCodLiberado,
			String paramSitCodAreaExec) {
		return this.getAelOrdExameMatAnaliseDAO()
				.obterDadosOrdemCronologicaArvorePol(codPaciente,
						paramSitCodLiberado, paramSitCodAreaExec);
	}
	
	@Override
	@BypassInactiveModule
	public List<ExameOrdemCronologicaVO> obterDadosOrdemCronologicaArvorePolHist(
			Integer codPaciente, String paramSitCodLiberado, String paramSitCodAreaExec) {
		return this.getAelResultadosExamesON().obterDadosOrdemCronologicaArvorePolHist(codPaciente, paramSitCodLiberado, paramSitCodAreaExec);
	}

	@Override
	@BypassInactiveModule
	public AelProjetoPesquisas buscarProjetoPesquisas(Integer dcgCrgSeq) {
		return this.getAelProjetoPesquisasDAO().buscarProjetoPesquisas(
				dcgCrgSeq);
	}

	@Override
	@BypassInactiveModule
	public AelUnfExecutaExames obterAelUnfExecutaExames(String emaExaSigla,
			Integer emaManSeq, Short unfSeq) {
		return this.getAelUnfExecutaExamesDAO().obterAelUnfExecutaExames(
				emaExaSigla, emaManSeq, unfSeq);
	}
	
	@Override
	public List<VAelUnfExecutaExames> pesquisarPorSiglaMaterialOuExame(String filtro){
		return getVAelUnfExecutaExamesDAO().pesquisarPorSiglaMaterialOuExame(filtro);
	}	
	
	@Override
	public Long pesquisarPorSiglaMaterialOuExameCount(String filtro){
		return getVAelUnfExecutaExamesDAO().pesquisarPorSiglaMaterialOuExameCount(filtro);
	}
	
	@Override
	@Secure("#{s:hasPermission('pesquisarCadastroExamesPorUnidade','pesquisar')}")
	public List<VAelUnfExecutaExames> pesquisarExamePorSeqUnidadeExecutora(Short unfSeq,
			String sigla, String descricaoMaterial, String descricaoUsualExame,
			String indSituacaoUfe, Integer firstResult, Integer maxResults, String orderProperty, Boolean asc) {
		return getVAelUnfExecutaExamesDAO().pesquisarExamePorSeqUnidadeExecutora(unfSeq, sigla,
				descricaoMaterial, descricaoUsualExame, indSituacaoUfe, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	@Secure("#{s:hasPermission('pesquisarCadastroExamesPorUnidade','pesquisar')}")
	public Long pesquisarExamePorSeqUnidadeExecutoraCount(Short unfSeq,
			String sigla, String descricaoMaterial, String descricaoUsualExame,
			String indSituacaoUfe) {
		return getVAelUnfExecutaExamesDAO().pesquisarExamePorSeqUnidadeExecutoraCount(unfSeq, sigla,
				descricaoMaterial, descricaoUsualExame, indSituacaoUfe);
	}
	
	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> listarItemSolicitacaoExamePorSeqCirurgiaSitCodigo(
			Integer seqCirurgia, String[] codigos) {
		return this.getAelItemSolicitacaoExamesDAO()
				.listarItemSolicitacaoExamePorSeqCirurgiaSitCodigo(seqCirurgia,
						codigos);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> buscarItemSolicitacaoExamesPorCirurgia(
			Integer seqMbcCirurgia, String pSituacaoLiberado,
			String pSituacaoExecutado) {
		return this.getAelItemSolicitacaoExamesDAO()
				.buscarItemSolicitacaoExamesPorCirurgia(seqMbcCirurgia,
						pSituacaoLiberado, pSituacaoExecutado);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> pesquisarSolicitacaoExames(Integer crgSeq,
			String[] situacao) {
		return this.getAelItemSolicitacaoExamesDAO()
				.pesquisarSolicitacaoExames(crgSeq, situacao);
	}

	@Override
	@BypassInactiveModule
	public List<VAelExamesLiberados>  buscaExamesLiberadosPeloPaciente(Integer codigoPaciente , Short unfSeq){
		return vAelExamesLiberadosDAO.obterExamesLiberadosPorCodigo(codigoPaciente, unfSeq);
	}
	
	@Override
	@BypassInactiveModule
	public List<ExamesPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimento(
			Integer codigoPaciente, Short unfSeq,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame,
			List<Integer> gruposMateriaisAnalises) {
		return this.getAelItemSolicitacaoExamesDAO()
				.buscaExamesPeloCodigoPacienteSituacaoAtendimento(
						codigoPaciente, unfSeq, situacaoItemExame,
						gruposMateriaisAnalises);
	}

	@Override
	@BypassInactiveModule
	public List<ExamesHistoricoPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimentoHist(
			Integer codigoPaciente, Short unfSeq,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame,
			List<Integer> gruposMateriaisAnalises) {
		return this.getAelItemSolicExameHistDAO()
				.buscaExamesPeloCodigoPacienteSituacaoAtendimento(
						codigoPaciente, unfSeq, situacaoItemExame,
						gruposMateriaisAnalises);
	}
	
	
	@Override
	@BypassInactiveModule
	public List<ExamesPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimentoDiverso(
			Integer codigoPaciente, Short unfSeq,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame,
			List<Integer> gruposMateriaisAnalises) {
		return this.getAelItemSolicitacaoExamesDAO()
				.buscaExamesPeloCodigoPacienteSituacaoAtendimentoDiverso(
						codigoPaciente, unfSeq, situacaoItemExame,
						gruposMateriaisAnalises);
	}

	@Override
	@BypassInactiveModule
	public List<ExamesHistoricoPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimentoDiversoHist(
			Integer codigoPaciente, Short unfSeq,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame,
			List<Integer> gruposMateriaisAnalises) {
		return this.getAelItemSolicExameHistDAO()
				.buscaExamesPeloCodigoPacienteSituacaoAtendimentoDiverso(
						codigoPaciente, unfSeq, situacaoItemExame,
						gruposMateriaisAnalises);
	}
	
	@Override
	@BypassInactiveModule
	public List<ProcedimentosPOLVO> pesquisarExamesProcPOL(Integer codigo) {
		return this.getAelItemSolicitacaoExamesDAO().pesquisarExamesProcPOL(
				codigo);
	}

	protected AelPedidoExameDAO getAelPedidoExameDAO() {
		return aelPedidoExameDAO;
	}

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}

	protected AelExtratoItemSolicHistDAO getAelExtratoItemSolicHistDAO() {
		return aelExtratoItemSolicHistDAO;
	}

	protected AelRespostaQuestaoDAO getAelRespostaQuestaoDAO() {
		return aelRespostaQuestaoDAO;
	}

	protected AelExamesQuestionarioDAO getAelExamesQuestionarioDAO() {
		return aelExamesQuestionarioDAO;
	}

	protected AelProjetoPacientesDAO getAelProjetoPacientesDAO() {
		return aelProjetoPacientesDAO;
	}

	protected AelGrupoXMaterialAnaliseDAO getAelGrupoXMaterialAnaliseDAO() {
		return aelGrupoXMaterialAnaliseDAO;
	}

	protected AelItemSolicConsultadoDAO getAelItemSolicConsultadoDAO() {
		return aelItemSolicConsultadoDAO;
	}

	protected AelOrdExameMatAnaliseDAO getAelOrdExameMatAnaliseDAO() {
		return aelOrdExameMatAnaliseDAO;
	}
	
	@Override
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisa(
			String strPesquisa) {
		return getAelProjetoPesquisasDAO().pesquisarProjetosPesquisa(
				strPesquisa);
	}

	/**
	 * Obter laudos para popular suggestionBox #6389
	 * 
	 * @author lucasbuzzo
	 * @param Object
	 *            param
	 * @return List<AelCampoLaudo>
	 */
	@Override
	public List<AelCampoLaudo> obterLaudo(Object param) {
		return getAelCampoLaudoDAO().listarCampoLaudoSuggestionBox(param);
	}

	@Override
	public Long pesquisarLaudoCount(Object param) {
		return getAelCampoLaudoDAO().obterLaudoCount(param);
	}

	private AelItemPedidoExameDAO getAelItemPedidoExameDAO() {
		return aelItemPedidoExameDAO;
	}

	@Override
	public List<AelPedidoExame> buscarPedidosExamePeloAtendimento(
			Integer seqAtendimento) {
		return getAelPedidoExameDAO().buscarPedidosExamePeloAtendimento(
				seqAtendimento);
	}

	@Override
	public void removerAelPedidoExame(AelPedidoExame aelPedidoExame,
			boolean flush) {
		getAelPedidoExameDAO().remover(aelPedidoExame);
		if (flush){
			getAelPedidoExameDAO().flush();
		}
	}

	@Override
	public void removerAelItemPedidoExame(
			AelItemPedidoExame aelItemPedidoExame, boolean flush) {
		getAelItemPedidoExameDAO().remover(aelItemPedidoExame);
		if (flush){
			getAelItemPedidoExameDAO().flush();
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AelItemSolicitacaoExames obterItemSolicitacaoExameOriginal(
			AelItemSolicitacaoExamesId aelItemSolicitacaoExamesId) {
//		return getAelItemSolicitacaoExamesDAO().obterOriginal(aelItemSolicitacaoExamesId);
		return getAelItemSolicitacaoExamesDAO().obterPorChavePrimaria(aelItemSolicitacaoExamesId);
	}

	@Override
	public List<AtendPacExternPorColetasRealizadasVO> listarAtendPacExternPorColetasRealizadas(
			Date vDtHrInicio, Date vDtHrFim, Integer codSangue, Integer codHemocentro, String situacaoCancelado) {
		return getAelItemSolicitacaoExamesDAO()
				.listarAtendPacExternPorColetasRealizadas(vDtHrInicio, vDtHrFim, codSangue, codHemocentro, situacaoCancelado);
	}

	@Override
	public AtendimentoCargaColetaVO listarAtendimentoParaCargaColetaRD(
			Integer pSoeSeq, Boolean isAtdDiv) {
		return getAelSolicitacaoExameDAO().listarAtendimentoParaCargaColetaRD(
				pSoeSeq, isAtdDiv);
	}

	@Override
	public AelExtratoItemSolicitacao obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(
			Integer soeSeq, Short seqp, String sitCodigo) {
		return getAelExtratoItemSolicitacaoDAO()
				.obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(
						soeSeq, seqp, sitCodigo);
	}

	@Override
	public List<AelItemSolicitacaoExames> obterPorSolicitacaoSitCodigo(
			Integer soeSeq, String[] sitCodigo) {
		return getAelItemSolicitacaoExamesDAO().obterPorSolicitacaoSitCodigo(
				soeSeq, sitCodigo);
	}

	@Override
	@Secure("#{s:hasPermission('cargaExamesInterfaceamento','executar')}")
	public List<AelAmostraItemExamesVO> listarAmostraItemExamesTodos(
			AghUnidadesFuncionais unidadeExecutora, Integer solicitacao,
			Integer amostra, AelEquipamentos equipamento, String sigla,
			DominioSimNao enviado) {

		return this.getAelAmostraItemExamesON().listarAmostraItemExamesTodos(
				unidadeExecutora, solicitacao, amostra, equipamento, sigla,
				enviado);
	}

	@Secure("#{s:hasPermission('cargaExamesInterfaceamento','pesquisar')}")
	@Override
	public List<AelAmostraItemExamesVO> pesquisarAmostraItemExames(
			AghUnidadesFuncionais unidadeExecutora, Integer solicitacao,
			Integer amostra, AelEquipamentos equipamento, String sigla,
			DominioSimNao enviado, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return this.getAelAmostraItemExamesON().pesquisarAmostraItemExames(
				unidadeExecutora, solicitacao, amostra, equipamento, sigla,
				enviado, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	@BypassInactiveModule
	public AelSolicitacaoExames obterSolicitacaoExamePorAtendimento(
			Integer seqAtendimento) {
		return this.getAelSolicitacaoExameDAO()
				.obterSolicitacaoExamePorAtendimento(seqAtendimento);
	}

	@Override
	public List<AelSolicitacaoExames> pesquisarSolicitacaoExamePorGaeUnfSeqGaeSeqpDthrAgenda(Short hedGaeUnfSeq, Integer hedGaeSeqp, 
			Date hedDthrAgenda) {
		return getAelSolicitacaoExameDAO().pesquisarSolicitacaoExamePorGaeUnfSeqGaeSeqpDthrAgenda(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
	}

	@Override
	@BypassInactiveModule
	public List<String> obterDescricaoMaterialAnalise(final Integer atdSeq){
		return getAelSolicitacaoExameDAO().obterDescricaoMaterialAnalise(atdSeq);
	}
	
	@Override
	public List<AelProjetoPacientes> buscarProjetosPesquisaComPaciente(
			Integer codigoPaciente, Integer servidorMatricula,
			Short servidorVinCodigo, String funcao) {
		return this.getAelProjetoPacientesDAO()
				.buscarProjetosPesquisaComPaciente(codigoPaciente,
						servidorMatricula, servidorVinCodigo, funcao);
	}

	@Override
	public List<AelProjetoPacientes> pesquisarProjetoEquipesComPaciente(
			Integer pacCodigo, Integer matricula, Short vinCodigo, String funcao) {
		return getAelProjetoPacientesDAO().pesquisarProjetoEquipesComPaciente(pacCodigo, matricula, vinCodigo, funcao);
	}

	@Override
	public List<AelProjetoPesquisas> pesquisarProjetosPacientes(
			Integer pacCodigo, Integer matricula, short vinCodigo,
			Date dataInicio, Date dataFim) {
		return getAelProjetoPesquisasDAO().pesquisarProjetosPacientes(pacCodigo, matricula, vinCodigo, dataInicio, dataFim);
	}
	
	@Override
	public AelAmostrasVO obterAelAmostraVO(Integer soeSeq, Short seqp) {
		return getAelAmostrasDAO().obterAelAmostraVO(soeSeq, seqp);
	}
	
	@Override
	public List<AelItemSolicitacaoExamesVO> listarItemSolicitacaoExamesVO(Integer soeSeq, Short seqp) {
		return this.getAelItemSolicitacaoExamesDAO().listarItemSolicitacaoExamesVO(soeSeq, seqp);
	}

	@Override
	@Secure("#{s:hasPermission('consultarAjusteNumeracaoUnicaAmostra','pesquisar')}")
	public void ajustarNumeroUnicoAelAmostra(AelAmostras aelAmostraOrigem, AelAmostras aelAmostraDestino ) throws BaseException {
		this.getAelAmostrasON().ajustarNumeroUnicoAelAmostra(aelAmostraOrigem, aelAmostraDestino);
	}
	
	protected AghAtendimentosPacExternRN getAghAtendimentosPacExternRN() {
		return aghAtendimentosPacExternRN;
	}

	@Override
	public List<AelControleNumeroMapa> obterPorDataNumeroUnicoEOrigem(final AelConfigMapa aelConfigMapa, final Date dtEmissao){
		return getAelControleNumeroMapaDAO().obterPorDataNumeroUnicoEOrigem(aelConfigMapa, dtEmissao);
	}

	private AelControleNumeroMapaDAO getAelControleNumeroMapaDAO(){
		return aelControleNumeroMapaDAO;
	}
	
	@Override
	@Secure("#{s:hasPermission('emissaoMapaTrabalho','pesquisar')}")
	public AelConfigMapa obterAelConfigMapa(final Short seq){
		return getAelConfigMapaDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	@Secure("#{s:hasPermission('emissaoMapaTrabalho','pesquisar')}")
	public AelConfigMapa obterAelConfigMapaPorSeq(final Short seq) {
		return getAelConfigMapaDAO().obterAelConfigMapaPorSeq(seq);
	}

	@Override
	@Secure("#{s:hasPermission('manterConfiguracaoMapa','pesquisar')}")
	public List<AelConfigMapa> pesquisarAelConfigMapa(final AelConfigMapa filtros){
		return getAelConfigMapaDAO().pesquisarAelConfigMapa(filtros);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterConfiguracaoMapa','executar')}")
	public void persistirAelConfigMapa(AelConfigMapa aelConfigMapa ) throws BaseException{
		this.getAelConfigMapaRN().persistir(aelConfigMapa);
	}
	
	@Override
	public void removerAelConfigMapa(Short aelConfigMapa ) throws BaseException{
		getAelConfigMapaRN().remover(aelConfigMapa);
	}

	@Override
	public List<RelatorioMapaBioquimicaVO> obterMapasBioquimicaVo( final AelConfigMapa aelConfigMapa, 
																   final DominioTipoImpressaoMapa tipoImpressao, 
																   Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador ) throws BaseException{
		return getAelConfigMapaON().obterMapasBioquimicaVo(aelConfigMapa, tipoImpressao, pDataMapa, pNroMapa, nomeMicrocomputador);
	}

	@Override
	public List<RelatorioMapaEpfVO> obterMapasEpfVO( final AelConfigMapa aelConfigMapa, 
			   final DominioTipoImpressaoMapa tipoImpressao,
			   Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador ) throws BaseException{
		return getAelConfigMapaON().obterMapasEpfVO(aelConfigMapa, tipoImpressao, pDataMapa, pNroMapa, nomeMicrocomputador);
	}
	
	@Override
	public List<RelatorioMapaHemoculturaVO> obterMapasHemoculturaVO( final AelConfigMapa aelConfigMapa, 
																   final DominioTipoImpressaoMapa tipoImpressao, 
																   Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador ) throws BaseException{
		return getAelConfigMapaON().obterMapasHemoculturaVO(aelConfigMapa, tipoImpressao, pDataMapa, pNroMapa, nomeMicrocomputador);
	}

	@Override
	public List<RelatorioMapaUroculturaVO> obterMapasUroculturaVO( final AelConfigMapa aelConfigMapa, 
			   final DominioTipoImpressaoMapa tipoImpressao,
			   Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador ) throws BaseException{
		return getAelConfigMapaON().obterMapasUroculturaVO(aelConfigMapa, tipoImpressao, pDataMapa, pNroMapa, nomeMicrocomputador);
	}

	@Override
	public List<RelatorioMapaHematologiaVO> obterMapasHematologiaVO( final AelConfigMapa aelConfigMapa, 
																   final DominioTipoImpressaoMapa tipoImpressao, 
																   Date pDataMapa, Integer pNroMapa, String nomeMicrocomputador ) throws BaseException{
		return getAelConfigMapaON().obterMapasHematologiaVO(aelConfigMapa, tipoImpressao, pDataMapa, pNroMapa, nomeMicrocomputador);
	}
	
	@Override
	public Integer laudoIdadePaciente(final Integer soeSeq) {
		return getAelConfigMapaRN().laudoIdadePaciente(soeSeq);
	}
 
	private AelConfigMapaRN getAelConfigMapaRN(){
		return aelConfigMapaRN;
	}
	
	private AelConfigMapaDAO getAelConfigMapaDAO(){
		return aelConfigMapaDAO;
	}
	
	@Override
	@Secure("#{s:hasPermission('manterExamesMapa','pesquisar')}")
	public List<AelConfigMapaExames> pesquisarAelConfigMapaExamesPorAelConfigMapa(final AelConfigMapa configMapa, final String sigla, final String exame, final String material, final DominioSituacao situacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		return getAelConfigMapaExamesDAO().pesquisarAelConfigMapaExamesPorAelConfigMapa(configMapa, sigla, exame, material, situacao, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	@Secure("#{s:hasPermission('manterExamesMapa','pesquisar')}")
	public Long pesquisarAelConfigMapaExamesPorAelConfigMapaCount(final AelConfigMapa configMapa, final String sigla, final String exame, final String material, final DominioSituacao situacao){
		return getAelConfigMapaExamesDAO().pesquisarAelConfigMapaExamesPorAelConfigMapaCount(configMapa, sigla, exame, material, situacao);
	}
	
	@Override
	@Secure("#{s:hasPermission('emissaoMapaTrabalho','pesquisar')}")
	public List<AelConfigMapa> pesquisarAelConfigMapaPorPrioridadeUnidadeFederativa( final AghUnidadesFuncionais unidadeFuncional, final String mapa, 
																					 final DominoOrigemMapaAmostraItemExame origem, final Integer firstResult, 
																					 final Integer maxResult, final String orderProperty, final boolean asc){
		
		return getAelConfigMapaON().pesquisarAelConfigMapaPorPrioridadeUnidadeFederativa( unidadeFuncional, mapa, origem, firstResult, 
																						  maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarAelConfigMapaPorPrioridadeUnidadeFederativaCount( final AghUnidadesFuncionais unidadeFuncional, final String mapa, final DominoOrigemMapaAmostraItemExame origem){
		return getAelConfigMapaON().pesquisarAelConfigMapaPorPrioridadeUnidadeFederativaCount(unidadeFuncional, mapa, origem);
	}

	@Override
	public AelConfigMapaExames obterAelConfigMapaExamesPorId(final AelConfigMapaExamesId id){
		return getAelConfigMapaExamesDAO().obterPorChavePrimaria(id);
	}

	@Override
	@Secure("#{s:hasPermission('manterExamesMapa','executar')}")
	public void persistirAelConfigMapaExames(AelConfigMapaExames aelConfigMapaExames ) throws BaseException {
		this.getAelConfigMapaExamesRN().persistir(aelConfigMapaExames);
	}
 
	@Override
	@Secure("#{s:hasPermission('manterExamesMapa','executar')}")
	public void removerAelConfigMapaExames(AelConfigMapaExamesId aelConfigMapaExames ) throws BaseException {
		getAelConfigMapaExamesRN().remover(aelConfigMapaExames);
	}
	
	private AelConfigMapaON getAelConfigMapaON() {
		return aelConfigMapaON;
	}
	
	private AelConfigMapaExamesRN getAelConfigMapaExamesRN() {
		return aelConfigMapaExamesRN;
	}
	
	private AelConfigMapaExamesDAO getAelConfigMapaExamesDAO(){
		return aelConfigMapaExamesDAO;
	}
	
	private AelDocResultadoExamesHistDAO getAelDocResultadoExamesHistDAO() {
		return aelDocResultadoExamesHistDAO;
	}

	@Override
	@BypassInactiveModule
	public AelDocResultadoExamesHist obterDocumentoAnexadoHist(Integer iseSoeSeq, Short iseSeqp) {
		return getAelDocResultadoExamesHistDAO().obterDocumentoAnexado(iseSoeSeq,iseSeqp);
	}
	
	@Override
	@BypassInactiveModule
	public List<AelDocResultadoExamesHist> obterDocumentoAnexadoHistPorItemSolicitacaoExameHist(Integer iseSoeSeq, Short iseSeqp) {
		return getAelDocResultadoExamesHistDAO().obterDocumentoAnexadoPorItemSolicitacaoExameHist(iseSoeSeq,iseSeqp);
	}
	
	@Override
	public List<AelNotaAdicional> obterListaNotasAdicionaisPeloItemSolicitacaoExame(Integer iseSoeSeq, Short iseSeqp) {
		return this.getAelNotaAdicionalDAO().obterListaNotasAdicionaisPeloItemSolicitacaoExame(iseSoeSeq, iseSeqp);
	}
	
	@BypassInactiveModule
	@Override
	public void persistirLwsComunicacao(LwsComunicacao lwsComunicacao) throws BaseException {
		this.getLwsComunicacaoRN().persistir(lwsComunicacao);
	}
	
	@Override
	public void inserirLwsComunicacao(LwsComunicacao lwsComunicacao) throws BaseException {
		this.getLwsComunicacaoRN().inserir(lwsComunicacao);
	}
	@Override
	public void atualizarLwsComunicacao(LwsComunicacao lwsComunicacao) throws BaseException {
		this.getLwsComunicacaoRN().atualizar(lwsComunicacao);
	}
	
	
	private LwsComunicacaoRN getLwsComunicacaoRN() {
		return lwsComunicacaoRN;
	}
	
	@Override
	public Short obterIdModuloLisHis() throws ApplicationBusinessException{
		return this.getGerarProtocoloUnicoRN().obterIdModuloLisHis();
	}
	
	@Override
	public Short obterIdModuloGestaoAmostra() throws ApplicationBusinessException{
		return this.getGerarProtocoloUnicoRN().obterIdModuloGestaoAmostra();
	}

	@Override
	public String geraCSVRelatorioMapaLaminasVO(final Date dtReferencia, final AelCestoPatologia cesto)throws IOException, ApplicationBusinessException{
		return getRelatorioCSVExamesON().geraCSVRelatorioMapaLaminasVO(dtReferencia, cesto);
	}
	
	protected RelatorioCSVExamesON getRelatorioCSVExamesON(){
		return relatorioCSVExamesON;
	}
	
	/** #5992 
	 * @throws BaseException **/
	@Override
	@Secure("#{s:hasPermission('criarNovaVersaoMascaraPrexistente','executar')}")
	public AelVersaoLaudo criarNovaVersaoLaudo(AelVersaoLaudo versaoLaudo ) throws IllegalArgumentException, BaseException{
		return this.getMascaraExameVersionamentoON().criarNovaVersao(versaoLaudo);
	}

	@Override
	public List<LiberacaoLimitacaoExameVO> pesquisarLiberacaoLimitacaoExames(Integer atdSeq){
		return this.getExameLimitadoAtendON().pesquisarLiberacaoLimitacaoExames(atdSeq);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGruposExamesUsuais','pesquisar')}")
	public Long pesquisaGrupoExamesUsuaisCount(Integer seq, String descricao, DominioSituacao situacao) {
		return this.getAelGrupoExameUsualDAO().pesquisaGrupoExamesUsuaisCount(seq, descricao, situacao);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGruposExamesUsuais','pesquisar')}")
	public List<AelGrupoExameUsual> pesquisaGrupoExamesUsuais(Integer firstResult, Integer maxResult, String orderProperty, 
			boolean asc, Integer seq, String descricao, DominioSituacao situacao) {
		return this.getAelGrupoExameUsualDAO().pesquisaGrupoExamesUsuais(firstResult, maxResult, orderProperty, asc, seq, descricao, situacao);
	}
	
	@Override
	public AelGrupoExameUsual obterAelGrupoExameUsualPorId(Integer seq) {
		return this.getAelGrupoExameUsualDAO().obterPorChavePrimaria(seq);
	}
	
	
	@Override
	@Secure("#{s:hasPermission('manterGruposExamesUsuais','executar')}")
	public void inserirAelGrupoExameUsual(AelGrupoExameUsual grupoExameUsualNew) throws ApplicationBusinessException {
		getGrupoExamesUsuaisRN().executarBeforeInsertGrupoExamesUsuais(grupoExameUsualNew);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGruposExamesUsuais','executar')}")
	public void alterarAelGrupoExameUsual(AelGrupoExameUsual grupoExameUsualNew) throws ApplicationBusinessException {
		getGrupoExamesUsuaisRN().executarBeforeUpdateGrupoExamesUsuais(grupoExameUsualNew);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGruposExamesUsuais','executar')}")
	public void excluirAelGrupoExameUsual(Integer seq) throws ApplicationBusinessException {
		getGrupoExamesUsuaisRN().executarBeforeDeleteGrupoExamesUsuais(seq);
	}
	
	protected GrupoExamesUsuaisRN getGrupoExamesUsuaisRN() {
		return grupoExamesUsuaisRN;
	}
	
	@Override
	public AelExamesLimitadoAtend obterAelExamesLimitadoAtendPorId(AelExamesLimitadoAtendId id) {
		return this.getAelExamesLimitadoAtendDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public VAelExameMatAnalise obterVAelExameMaterialAnalise(AelExamesMaterialAnaliseId id){
		return this.getVAelExameMatAnaliseDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoPorLeito(String param) throws ApplicationBusinessException {
		return getExameLimitadoAtendON().obterAtendimentoAtualPorLeitoId(param);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoAtualPorProntuario(Integer prontuario) throws ApplicationBusinessException {
		return getExameLimitadoAtendON().obterAtendimentoAtualPorProntuario(prontuario);
	}
	
	@Override
	public void gravarResultadoPadraoCampoLaudo(AelVersaoLaudo versaoLaudo, AelResultadosPadrao resultadosPadrao, Map<AelParametroCamposLaudo, Object> mapaParametroCamposLaudoTelaValores )
			throws BaseException {
		this.getCadastroResultadoPadraoLaudoON().gravarResultadoPadrao(versaoLaudo, resultadosPadrao, mapaParametroCamposLaudoTelaValores);
	}
	
	@Override
	public List<AelItemSolicitacaoExamesVO> listarMascarasAtivasPorExame(String emaExaSigla, Integer emaManSeq, Integer soeSeq, Short seqp) {
		return this.getAelItemSolicitacaoExamesDAO().listarMascarasAtivasPorExame(emaExaSigla, emaManSeq, soeSeq, seqp);
	}
	
	@Override
	public AelGrpTecnicaUnfExamesVO obterAelGrpTecnicaUnfExamesVO(Integer grtSeq, String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq){
		return this.getAelGrpTecnicaUnfExamesDAO().obterAelGrpTecnicaUnfExamesVO(grtSeq, ufeEmaExaSigla, ufeEmaManSeq, ufeUnfSeq);
	}

	@Override
	@Secure("#{s:hasPermission('manterCampoLaudoPendencia','pesquisar')}")
	public List<AelGrupoTecnicaCampo> pesquisarCampoLaudoPendencia(Integer grtSeq, String ufeEmaExaSigla, Short ufeUnfSeq){
		return this.getAelGrupoTecnicaCampoDAO().pesquisarAelGrupoTecnicaCampo(grtSeq, ufeEmaExaSigla, ufeUnfSeq);
	}
	
	protected AelGrupoTecnicaCampoDAO getAelGrupoTecnicaCampoDAO() {
		return aelGrupoTecnicaCampoDAO;
	}
	
	private CadastroResultadoPadraoLaudoON getCadastroResultadoPadraoLaudoON() {
		return cadastroResultadoPadraoLaudoON;
	}
	
	@Override
	public List<AelCampoLaudo> pesquisarAelCampoLaudoSuggestion(final Object pesquisa) {
		return this.getAelCampoLaudoDAO().pesquisarAelCampoLaudoSuggestion(pesquisa);
	}
	
	protected AelResultadoPadraoCampoDAO getAelResultadoPadraoCampoDAO() {
        return aelResultadoPadraoCampoDAO;
	}
	
	@Override
	@BypassInactiveModule
	public boolean anexarDocumentoLaudoAutomatico(AelItemSolicitacaoExames itemSolicitacaoExame ) throws BaseException {
		return this.getAnexoDocumentoLaudoAutomaticoON().anexarDocumentoLaudoAutomatico(itemSolicitacaoExame);
	}
	@Override
	public String extrairNomeExtensaoDocumentoLaudoAnexo(AelItemSolicitacaoExames itemSolicitacaoExame) {
		return this.getAnexoDocumentoLaudoAutomaticoON().extrairNomeExtensaoDocumentoLaudoAnexo(itemSolicitacaoExame);
	}
	
	protected AnexoDocumentoLaudoAutomaticoON getAnexoDocumentoLaudoAutomaticoON() {
        return anexoDocumentoLaudoAutomaticoON;
	}
	
	@Override
	public List<AelMotivoCancelaExamesVO> listarMotivoCancelamentoExamesAtivos(final AelMotivoCancelaExames motivoCancelaExamesFiltro) {
		return this.getAelMotivoCancelaExamesDAO().listarMotivoCancelamentoExamesAtivos(motivoCancelaExamesFiltro);
	}
	public List<AelResultadoCodificado> pesquisarResultadoCodificadoPorGtcSeqSeqp(Integer gtcSeq, Integer seqp) {
		return getAelResultadoCodificadoDAO().pesquisarResultadoCodificadoPorGtcSeqSeqp(gtcSeq, seqp);
	}
	
	@Override
	public Integer obterTamanhoMaximoBytesUploadLaudo() {
		return this.getAnexoDocumentoLaudoAutomaticoON().obterTamanhoMaximoBytesUploadLaudo();
	}

	@Override
	public List<AelOrdExameMatAnalise> pesquisarAelOrdExameMatParaPOL(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, AelExames exame, AelMateriaisAnalises materialAnalise, Short ordemNivel1, Short ordemNivel2) {
		return getAelOrdExameMatAnaliseDAO().pesquisarAelOrdExameMatParaPOL(firstResult, maxResults, orderProperty, asc, exame, materialAnalise, ordemNivel1, ordemNivel2);
	}

	@Override
	public Long pesquisarAelOrdExameMatParaPOLCount(AelExames exame, AelMateriaisAnalises materialAnalise, Short ordemNivel1, Short ordemNivel2) {
		return getAelOrdExameMatAnaliseDAO().pesquisarAelOrdExameMatParaPOLCount(exame, materialAnalise,ordemNivel1, ordemNivel2);
	}

	@Override
	@Secure("#{s:hasPermission('manterOrdemExamesPOL','pesquisar')}")
	public AelOrdExameMatAnalise recuperaAelOrdExameMatAnalisePorMaterial(AelExamesMaterialAnaliseId aelExamesMaterialAnaliseId) {
		return getAelOrdExameMatAnaliseDAO().recuperaAelOrdExameMatAnalisePorMaterial(aelExamesMaterialAnaliseId);
	}

	public DetalhesExamesPacienteVO buscaResultadosExames(Integer pSoeSeq,
			Integer pIseSoeSeq, Integer pNroSessao) throws ApplicationBusinessException {
		return getDetalhesExamesON().buscaResultadosExames(pSoeSeq, pIseSoeSeq,
				pNroSessao);
	}
	
	public DetalhesExamesPacientesON getDetalhesExamesON(){
		return  detalhesExamesPacientesON;
	}

	@Override
	@Secure("#{s:hasPermission('manterOrdemExamesPOL','executar')}")
	public void atualizarAelOrdExameMatAnalise(AelOrdExameMatAnalise aelOrdExameMatAnalise ) throws BaseException {
		aelOrdExameMatAnaliseON.atualizar(aelOrdExameMatAnalise);
	}
	
	protected PesquisarExamesPorUnidadeFuncionalON getPesquisarExamesPorUnidadeFuncionalON(){
		return pesquisarExamesPorUnidadeFuncionalON;
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqDescricao(String parametro) {
		return getPesquisarExamesPorUnidadeFuncionalON().pesquisarUnidadeFuncionalPorSeqDescricao(parametro);
	}
	
	@Override
	@BypassInactiveModule
	public boolean possuiNotasAdicionaisItemSolicitacaoExameHist(Integer soeSeq, Short seqp) {
		return getAelNotasAdicionaisHistDAO().possuiNotasAdicionaisItemSolicitacaoExame(soeSeq,seqp);
	}
	
	protected AelNotasAdicionaisHistDAO getAelNotasAdicionaisHistDAO() {
		return aelNotasAdicionaisHistDAO;
	}

	@Override
	public List<AelNotasAdicionaisHist> pesquisarNotasAdicionaisItemSolicitacaoExameHist(Integer soeSeq, Short seqp) {
		return getAelNotasAdicionaisHistDAO().pesquisarNotasAdicionaisItemSolicitacaoExame(soeSeq,seqp);
	}
	
	
	@Override
	public List<MonitorPendenciasExamesVO> pesquisarMonitorPendenciasExames(MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) throws BaseException{
		return this.getMonitorPendenciasExamesON().pesquisarMonitorPendenciasExames(filtrosPesquisa);
	}
	
	@Override
	public String buscaAmoSeqParaUmaSolicitacao(Integer iseSoeSeq, Short iseSeqp, Integer amoSoeSeq){
		return this.getAelAmostraItemExamesDAO().buscaAmoSeqParaUmaSolicitacao(iseSoeSeq, iseSeqp, amoSoeSeq);
	}

	
	@Override
	public List<RelatorioAgendamentoProfissionalVO> pesquisarRelatorioAgendamentoProfissional(Date dataReferenciaIni, Date dataReferenciaFim) throws BaseException  {
		return this.getRelatorioAgendamentoProfissionalON().pesquisarRelatorioAgendamentoProfissional(dataReferenciaIni, dataReferenciaFim);
	}

	
	@Override
	public void verificarFiltrosPesquisa(MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) throws BaseException{
		this.getMonitorPendenciasExamesON().verificarFiltrosPesquisa(filtrosPesquisa);
	}

	protected MonitorPendenciasExamesON getMonitorPendenciasExamesON(){
		return monitorPendenciasExamesON;
	}
	
	@Override
	public VAelUnfExecutaExames obterVAelUnfExecutaExames(String emaExaSigla, Integer emaManSeq, Short unfSeq) {
		return this.getVAelUnfExecutaExamesDAO().obterVAelUnfExecutaExames(emaExaSigla, emaManSeq, unfSeq);
	}
	protected AelItemSolicCartasDAO getAelItemSolicCartasDAO() {
		return aelItemSolicCartasDAO;
	}
	
	@Override
	public List<AelItemSolicCartas> listarAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigo(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, DominioSituacaoCartaColeta situacao, Date dtInicio, 
			Date dtFim, Integer iseSoeSeq, Integer pacCodigo) {
		return getAelItemSolicCartasDAO().listarAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigo(firstResult, maxResult, orderProperty, asc,
				situacao, dtInicio, dtFim, iseSoeSeq, pacCodigo);
	}
	
	@Override
	public Long listarAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigoCount(DominioSituacaoCartaColeta situacao, Date dtInicio, 
			Date dtFim, Integer iseSoeSeq, Integer pacCodigo) {
		return getAelItemSolicCartasDAO().listarAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigoCount(situacao, dtInicio, dtFim, iseSoeSeq, pacCodigo);
	}
	
	@Override
	public void validarPesquisaCartasRecoleta(DominioSituacaoCartaColeta situacao, Date dtInicio, Date dtFim,
			Integer iseSoeSeq, Integer pacCodigo) throws BaseException {
		getGestaoCartasRecoletaRN().validarPesquisaCartasRecoleta(situacao, dtInicio, dtFim, iseSoeSeq, pacCodigo);
	}
		
	@Override
	public AelItemSolicCartas obterAelItemSolicCartas(AelItemSolicCartasId id) {
		return getAelItemSolicCartasDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public List<AelModeloCartas> listarAelModeloCartasAtivas(final Object parametro) {
		return getAelModeloCartasDAO().listarAelModeloCartasAtivas(parametro);
	}
	
	@Override
	public List<AbsMotivoRetornoCartas> listarAelRetornoCartaAtivas(Object parametro) {
		return getAelRetornoCartaDAO().listarAelRetornoCartaAtivas(parametro);
	}
	
	@Override
	public List<CartaRecoletaVO> obterCartaParaImpressao(Short iseSeqp, Integer iseSoeSeq, Short seqp) {
		return getGestaoCartasRecoletaRN().obterCartaParaImpressao(iseSeqp, iseSoeSeq, seqp);
	}
	
	@Override
	public void atualizarAelItemSolicCartas(AelItemSolicCartas item, String nomeMicrocomputador ) throws BaseException {
		getGestaoCartasRecoletaRN().atualizar(item, nomeMicrocomputador);
	}
	
	protected GestaoCartasRecoletaRN getGestaoCartasRecoletaRN() {
		return gestaoCartasRecoletaRN;
	}

	@Override
	@BypassInactiveModule
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public RelatorioExamesPacienteVO montarRelatorio(Integer prontuario, Integer atdSeq, DominioSumarioExame pertenceSumario, Date dthrEvento, Boolean recemNascido, String pertenceSumarioRodape, Integer pacCodigo) throws BaseException {
		return getEmitirRelatorioExamesPacienteON().montarRelatorio(prontuario, atdSeq, pertenceSumario, dthrEvento, recemNascido, pertenceSumarioRodape, pacCodigo);
	}
	
	@Override
	public List<VAelUnfExecutaExames> pesquisarExamePorSiglaOuDescricao(final Object parametro, final Short unfSeq){
		return getVAelUnfExecutaExamesDAO().pesquisarExamePorSiglaOuDescricao((String) parametro, unfSeq);
	}
	
	private AelExigenciaExameDAO getAelExigenciaExameDAO() {
		return aelExigenciaExameDAO;
	}
	
	private AelExigenciaExameRN getAelExigenciaExameRN() {
		return aelExigenciaExameRN;
	}
	
	@Override
	@Secure("#{s:hasPermission('configurarRestricaoExames','executar')}")
	public void persistirAelExigenciaExame(AelExigenciaExame exigencia ) throws BaseException {
		getAelExigenciaExameRN().persistir(exigencia);
	}
	
	@Override
	public AelExigenciaExame obterAelExigenciaExame(Integer seq) {
		return getAelExigenciaExameDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public AelExigenciaExame obterAelExigenciaExame(Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getAelExigenciaExameDAO().obterPorChavePrimaria(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	@Override
	public AelSitItemSolicitacoes obterSituacaoItemSolicitacaoParaSituacaoExame(final String codigo) {
		return situacaoPorExameRN.obterSituacaoItemSolicitacaoParaSituacaoExame(codigo);
	}
	
	@Override
	@Secure("#{s:hasPermission('configurarRestricaoExames','pesquisar')}")
	public List<AelExigenciaExame> pesquisarExigenciaExame(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			AelUnfExecutaExames unfExecutaExames, AghUnidadesFuncionais unidadeFuncional, DominioSituacao situacao) {
		return getAelExigenciaExameDAO().pesquisar(firstResult, maxResult, orderProperty, asc, unfExecutaExames, unidadeFuncional, situacao);
	}
	
	@Override
	@Secure("#{s:hasPermission('configurarRestricaoExames','pesquisar')}")
	public Long pesquisarExigenciaExameCount(AelUnfExecutaExames unfExecutaExames, AghUnidadesFuncionais unidadeFuncional, DominioSituacao situacao) {
		return getAelExigenciaExameDAO().pesquisarCount(unfExecutaExames, unidadeFuncional, situacao);
	}

	private RelatorioAgendamentoProfissionalON getRelatorioAgendamentoProfissionalON() {
		return relatorioAgendamentoProfissionalON;
	}

	@Override
	public AelTipoAmostraExame obterAelTipoAmostraExame(String emaExaSigla,	Integer emaManSeq, Integer manSeq, DominioOrigemAtendimento origemAtendimento) {
		return this.getAelTipoAmostraExameDAO().obterAelTipoAmostraExame(emaExaSigla, emaManSeq, manSeq, origemAtendimento);
	}

	@Override
	public List<AelExtratoItemSolicitacaoVO> listarExtatisticaPorResultadoExame(Date dtHrInicial, Date dtHoraFinal, Short unfSeq)
			throws ApplicationBusinessException {
		return this.getAelExtratoItemSolicitacaoDAO().listarExtatisticaPorResultadoExame(dtHrInicial, dtHoraFinal, unfSeq);
	}
	
	
	
	public ArquivoSecretariaNotificacaoON getArquivoSecretariaNotificacaoON(){
		return arquivoSecretariaNotificacaoON;
	}

	@Override
	@Secure("#{s:hasPermission('gerarArquivoSecretariaNotificoes','executar')}")
	public String gerarArquivoSecretaria(Date dataInicio, Date datafim, Boolean isCN4) throws BaseException {
		return getArquivoSecretariaNotificacaoON().gerarArquivoSecretaria(dataInicio, datafim, isCN4);
	}

	@Override
	@Secure("#{s:hasPermission('gerarArquivoSecretariaNotificoes','executar')}")
	public String gerarArquivoSecretariaCarga(Date dataInicio, Date datafim, Boolean isCN4) throws BaseException {
		return getArquivoSecretariaNotificacaoON().gerarArquivoSecretariaCarga(dataInicio, datafim, isCN4);
	}
	
	@Override
	public void efetuarDownloadArquivoSecretaria(String fileName,
			Date dataInicio,Boolean infantil) throws BaseException {
		getArquivoSecretariaNotificacaoON().efetuarDownloadArquivoSecretaria(fileName, dataInicio,infantil);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterInformacoesComplementaresVinculadasExame','pesquisar')}")
	public Long buscarAelExamesQuestionarioCount(String emaExaSigla, Integer emaManSeq) {
		return getAelExamesQuestionarioDAO().buscarAelExamesQuestionarioCount(emaExaSigla, emaManSeq);
	}
	
	private AelQuestionariosDAO getAelQuestionariosDAO() {
		return aelQuestionariosDAO;
	}
	
	@Override
	public List<AelQuestionarios> pesquisarAelQuestionarios(Object param) {
		return getAelQuestionariosDAO().pesquisarAelQuestionarios(param);
	}
	
	@Override
	public List<RelatorioEstatisticaTipoTransporteVO> pesquisarRelatorioEstatisticaTipoTransporte(AghUnidadesFuncionais unidadeExecutora,
			DominioOrigemAtendimento origem, Date dataInicial, Date dataFinal) throws BaseException{
		this.setTimeout(TRANSACTION_TIMEOUT_8_HORAS);
		this.commit(TRANSACTION_TIMEOUT_8_HORAS);
		return getRelatorioEstatisticaTipoTransporteON().pesquisarRelatorioEstatisticaTipoTransporte(unidadeExecutora, origem, dataInicial, dataFinal);
	}
	
	@Override
	public Map<Integer, Integer> obterTotaisTurno(List<RelatorioEstatisticaTipoTransporteVO> lista){
		return getRelatorioEstatisticaTipoTransporteON().obterTotaisTurno(lista);
	}
	
	@Override
	public Map<Integer, BigDecimal> obterPercentuaisTurno(List<RelatorioEstatisticaTipoTransporteVO> lista){
		return getRelatorioEstatisticaTipoTransporteON().obterPercentuaisTurno(lista);
	}
	
	@Override
	public Integer obterTotalGeralTurnos(List<RelatorioEstatisticaTipoTransporteVO> lista) {
		return getRelatorioEstatisticaTipoTransporteON().obterTotalGeralTurnos(lista);
	}
	
	private RelatorioEstatisticaTipoTransporteON getRelatorioEstatisticaTipoTransporteON() {
		return relatorioEstatisticaTipoTransporteON;
	}
	
	@Override
	public List<VAelUnfExecutaExames> pesquisarExamePorSeqUnidadeExecutora(
			Short unfSeq, String sigla, String descricaoMaterial,
			String descricaoUsualExame, String indSituacaoUfe) {
		return getVAelUnfExecutaExamesDAO().pesquisarExamePorSeqUnidadeExecutora(unfSeq, sigla,
				descricaoMaterial, descricaoUsualExame, indSituacaoUfe);
	}
	
	private AelQuestionariosConvUnidDAO getAelQuestionariosConvUnidDAO() {
		return aelQuestionariosConvUnidDAO;
	}
		
	@Override
	@Secure("#{s:hasPermission('associarRequisitante','excluir')}")
	public void remover(AelQuestionariosConvUnidId elemento) throws ApplicationBusinessException{
		getAelQuestionariosConvUnidDAO().removerPorId(elemento);
	}
	
	@Override
	public String obterNomeAtendDiv(Integer atdDivseq) {
		return getGestaoCartasRecoletaRN().obterNomeAtendDiv(atdDivseq);
	}
	
	@Override
	public String obterOrigemIg(AelSolicitacaoExames solicitacaoExame) throws ApplicationBusinessException {
		return getGerarProtocoloUnicoRN().obterOrigemIg(solicitacaoExame);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#setTimeout(java
	 * .lang.Integer)
	 */
	@Override
	public void setTimeout(final Integer timeout) throws ApplicationBusinessException {
		//
		// final UserTransaction userTx = this.getUserTransaction();
		// try {
		// final EntityManager em = this.getEntityManager();
		// if (userTx.isNoTransaction() || !userTx.isActive()) {
		// userTx.begin();
		// }
		// if (timeout != null) {
		// userTx.setTransactionTimeout(timeout);
		// }
		// em.joinTransaction();
		// } catch (final Exception e) {
		// logError(e.getMessage(), e);
		// }
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#commit(java.lang.Integer)
	 */
	@Override
	public void commit(Integer timeout) throws ApplicationBusinessException {
		// UserTransaction userTx = this.getUserTransaction();
		//
		// try {
		// if (userTx.isNoTransaction() || !userTx.isActive()) {
		// userTx.begin();
		// }
		// EntityManager em = this.getEntityManager();
		// em.joinTransaction();
		// em.flush();
		// userTx.commit();
		// if (timeout != null) {
		// userTx.setTransactionTimeout(timeout);
		// }
		// if (userTx.isNoTransaction() || !userTx.isActive()) {
		// userTx.begin();
		// }
		// em.joinTransaction();
		// } catch (Exception e) {
		// logError(e.getMessage(), e);
		// EstoqueFacadeExceptionCode.ERRO_AO_CONFIRMAR_TRANSACAO.throwException();
		// }
	}

	@Override
	public List<AelAmostras> listarAmostrasPorAgendamento(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) {
		return getAelAmostrasDAO().listarAmostrasPorAgendamento(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
	}

	@Override
	@BypassInactiveModule
	public List<AelRespostaQuestao> pesquisarRespostaPorConsultaPendente(
			Integer conNumero, String vSituacaoPendente) {
		return getAelRespostaQuestaoDAO().pesquisarRespostaPorConsultaPendente(conNumero, vSituacaoPendente);
	}
	
	protected PesquisarMateriaisColetaEnfermagemON getPesquisarMateriaisColetaEnfermagemON() {
		return pesquisarMateriaisColetaEnfermagemON;
	}
	
	@Override
	public void validarNumeroFrascoSolicitacao(final AghUnidadesFuncionais unidadeExecutora, List<AelAmostrasVO> listaAmostras) throws BaseException {
		getReceberTodasAmostrasSolicitacaoRN().validarNumeroFrascoSolicitacao(unidadeExecutora, listaAmostras);
	}

	@Override
	public void atualizarIndImpressaoQuestionario(
			AelRespostaQuestao respostaQuestao) {
		getAelAmostraItemExamesRN().atualizarIndImpressaoQuestionario(respostaQuestao);
	}
	

	@Override
	public AelProjetoProcedimento obterProjetoProcedimentoPorChavePrimaria(Integer pjqSeq, Integer pciSeq) {
		return getAelProjetoProcedimentoDAO().obterPorChavePrimaria(new AelProjetoProcedimentoId(pjqSeq, pciSeq));
	}
	
	protected AelProjetoProcedimentoDAO getAelProjetoProcedimentoDAO() {
		return aelProjetoProcedimentoDAO;
	}
	
	@Override
	public AelProjetoIntercProc obterProjetoIntercProcProjetoPacienteQuantidadeEfetivado(Integer pjqSeq, Integer pacCodigo, Integer pciSeq) {
		return getAelProjetoIntercProcDAO().obterProjetoIntercProcProjetoPacienteQuantidadeEfetivado(pjqSeq, pacCodigo, pciSeq);
	}
	
	@Override
	public void persistirProjetoIntercProc(AelProjetoIntercProc projetoIntercProc ) throws BaseException{
		getAelProjetoIntercProcRN().persistirProjetoIntercProc(projetoIntercProc);
	}

	protected AelProjetoIntercProcDAO getAelProjetoIntercProcDAO() {
		return aelProjetoIntercProcDAO;
	}
	
	protected AelProjetoIntercProcRN getAelProjetoIntercProcRN() {
		return aelProjetoIntercProcRN;
	}
	
	@Override
	@BypassInactiveModule
	public AelProjetoProcedimento obterProjetoProcedimentoAtivoPorId(Integer pjqSeq, Integer pciSeq) {
		return getAelProjetoProcedimentoDAO().obterProjetoProcedimentoAtivoPorId(pjqSeq, pciSeq);
	}
	
	@Override
	public void persistirProjetoProcedimento(AelProjetoProcedimento projetoProcedimento ) throws BaseException {
		getAelProjetoProcedimentoRN().persistirProjetoProcedimento(projetoProcedimento);
	}
	
	protected AelProjetoProcedimentoRN getAelProjetoProcedimentoRN() {
		return aelProjetoProcedimentoRN;
	}
	
	public List<AelAmostraItemExames> buscarAelAmostraItemExamesPorSituacaoAmostras(Object objPesquisa, List<DominioSituacaoAmostra> situacaoAmostras){
		return getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesPorSituacaoAmostras(objPesquisa, situacaoAmostras);
	}
	
	@Override
	@BypassInactiveModule
	public AelProjetoPacientes obterProjetoPacienteCadastradoDataProjetoPesquisa(Integer pacCodigo, Integer pjqSeq) {
		return getAelProjetoPacientesDAO().obterProjetoPacienteCadastradoDataProjetoPesquisa(pacCodigo, pjqSeq);
	}
	
	@Override
	public List<RelatorioMateriaisRecebidosNoDiaVO> pesquisarMateriaisRecebidosNoDia(
			Short unfSeq, Date dtInicial, Date dtFinal) throws ApplicationBusinessException {
		return getRelatorioMateriaisRecebidosNoDiaON().pesquisarMateriaisRecebidosNoDia(unfSeq, dtInicial, dtFinal);
	}
	
	private RelatorioMateriaisRecebidosNoDiaON getRelatorioMateriaisRecebidosNoDiaON() {
		return relatorioMateriaisRecebidosNoDiaON;
	}
	
	private EmitirRelatorioSumarioExamesAltaON getEmitirRelatorioSumarioExamesAltaON() {
		return emitirRelatorioSumarioExamesAltaON;
	}

	@Override
	@BypassInactiveModule
	public List<AelExameInternetStatus> listarExameInternetStatus(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Date dataHoraInicial, Date dataHoraFinal,
			DominioSituacaoExameInternet situacao,
			DominioStatusExameInternet status, Integer iseSoeSeq,
			Short iseSeqp, String localizador, String sigla, String nroRegConselho, Long cnpjContratante) throws ParseException{
		
		return this.getAelExameInternetStatusDAO().listarExameInternetStatus(
				firstResult, maxResult, orderProperty, asc, dataHoraInicial,
				dataHoraFinal, situacao, status, iseSoeSeq, iseSeqp,
				localizador, sigla, nroRegConselho, cnpjContratante);
	}
	
	@Override
	@BypassInactiveModule
	public Long listarExameInternetStatusCount(Date dataHoraInicial,
			Date dataHoraFinal, DominioSituacaoExameInternet situacao,
			DominioStatusExameInternet status, Integer iseSoeSeq,
			Short iseSeqp, String localizador, String sigla, String nroRegConselho, Long cnpjContratante) throws ParseException{

		return this.getAelExameInternetStatusDAO()
				.listarExameInternetStatusCount(dataHoraInicial, dataHoraFinal,
						situacao, status, iseSoeSeq, iseSeqp, localizador, sigla, nroRegConselho, cnpjContratante);
	
	}

	private AelExameInternetStatusDAO getAelExameInternetStatusDAO(){
		return aelExameInternetStatusDAO;
	}
	
	@Override
	@BypassInactiveModule
	public RelatorioExamesPacienteVO montarRelatorioPlanoContingenciaSumarioExames(Integer asuApaAtdSeq, Integer asuApaSeq, Short apeSeqp) throws BaseException {
		return getEmitirRelatorioSumarioExamesAltaON().montarRelatorio(asuApaAtdSeq, asuApaSeq, apeSeqp);
	}
	
	@Override
	@BypassInactiveModule
	public List<LinhaReportVO> pesquisarNotaAdicionalPorSolicitacaoEItemVO(
			Integer solicitacao, Short seqp, Boolean isHist) {
		return getAelNotaAdicionalDAO().pesquisarNotaAdicionalPorSolicitacaoEItemVO(solicitacao,seqp, isHist);
	}
		
	@Override
	public Integer buscarMaxVersaoConfPorLu2Seq(Integer lu2Seq) {
		return this.getAelSecaoConfExamesDAO().buscarMaxVersaoConfPorLu2Seq(lu2Seq);
	}
	
	private AelSecaoConfExamesDAO getAelSecaoConfExamesDAO(){
		return aelSecaoConfExamesDAO;
	}

	@Override
	public List<AelSecaoConfExames> buscarPorLu2SeqEVersaoConf(Integer lu2Seq,
			Integer versaoConf) {
		return this.getAelSecaoConfExamesDAO().buscarPorLu2SeqEVersaoConf(lu2Seq, versaoConf);
	}
		
	@BypassInactiveModule
	public List<AelSolicitacaoExames> obterSolicitacaoExamesPorAtendimento(Integer atdSeq) {
		return aelSolicitacaoExameDAO.obterSolicitacaoExamesPorAtendimento(atdSeq);
	}
	
	private AelConfigExLaudoUnicoDAO getAelConfigExLaudoUnicoDAO(){
		return aelConfigExLaudoUnicoDAO;
	}
	
	@Override
	public AelConfigExLaudoUnico bucarConfigExameLaudoUnicoOriginal(
			Integer lu2Seq) {
		return this.getAelConfigExLaudoUnicoDAO().obterPorChavePrimaria(lu2Seq, new Enum[] {AelConfigExLaudoUnico.Fields.SERVIDOR});
	}
	
	public AelSecaoConfExameON getAelSecaoConfExameON(){
		return aelSecaoConfExameON;
	} 
	
	public AelSecaoConfExamesRN getAelSecaoConfExameRN(){
		return aelSecaoConfExamesRN;
	} 

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.exames.business.IExamesFacade#salvarSecaoConfExames(java.util.List, java.lang.Integer)
	 */
	@Override
	public void salvarSecaoConfExames(List<SecaoConfExameVO> lista, Integer lu2Seq, RapServidores servidor) throws ApplicationBusinessException {
		this.getAelSecaoConfExameRN().salvarSecaoConfExames(lista, lu2Seq, servidor);
		
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.exames.business.IExamesFacade#habilitaSecaoObrigatoria(boolean, boolean)
	 */
	@Override
	public boolean habilitaSecaoObrigatoria(boolean ativo, boolean obrigatorio) {
		return this.getAelSecaoConfExameON().habilitaSecaoObrigatoria(ativo, obrigatorio);
		
	}
	
	@Override
	//#22049 - C1
	//QUALIDADE
	public List<AmostraVO> obterAmostrasSolicitacao(Integer solicitacaoNumero, List<Integer> numerosAmostras, final Map<AghuParametrosEnum, String> situacao) {
		return getAgruparExamesON().obterAmostrasSolicitacao(solicitacaoNumero, numerosAmostras, situacao);
	}
	
	@Override
	//#22049 - C2
	public List<ExameAndamentoVO> obterExamesAndamento(Integer pacCodigo, Short unidadaSeq) throws BaseException {
		return getAgruparExamesRN().obterExamesEmAndamento(pacCodigo, unidadaSeq);
	}
	
	//@22049 - C3
	//QUALIDADE
	public List<AelPatologista> pesquisarPatologistasResponsaveis(Object param) {
		return getAelPatologistaDAO().pesquisarPatologistasResponsaveisPorSeqNomeTodasFuncoes(param);
	}

	//@22049 - C3
	//QUALIDADE
	@Override
	public Long pesquisarPatologistasResponsaveisCount(Object param) {
		return getAelPatologistaDAO().pesquisarPatologistasResponsaveisPorSeqNomeTodasFuncoesCount(param);
	}
	
	@Override
	public AelConfigExLaudoUnico obterTipoExameAmostra(Integer solicitacaoNumero, Short seqp) {
		return null;
	}

	@Override
	public void adicionarPatologistaResponsavel(List<AelPatologista> listaPatologistasResponsaveis, AelPatologista novoPatologistaResponsavel)
			throws ApplicationBusinessException {
		getAgruparExamesON().addicionarPatologistaResponsavel(listaPatologistasResponsaveis, novoPatologistaResponsavel);
	}
	
	public void confirmarPatologistasResponsaveis(List<AmostraVO> listaAmostras, List<AelPatologista> listaPatologistasResponsaveis) {
		getAgruparExamesON().confirmarPatologistasResponsaveis(listaAmostras, listaPatologistasResponsaveis);
	}
	
	@Override
	public void agruparAmostras(List<AmostraVO> listaAmostraSelecionadas, Set<ExameAndamentoVO> listaExamesSelecionados, List<AmostraVO> listaAmostras,
			List<AelPatologista> listaPatologistasResponsaveis) throws ApplicationBusinessException {
		getAgruparExamesON().agruparAmostras(listaAmostraSelecionadas, listaExamesSelecionados, listaAmostras, listaPatologistasResponsaveis);
	}	
	
	@Override
	public void desagruparAmostras(List<AmostraVO> listaAmostraSelecionadas, List<AmostraVO> listaAmostras, AmostraVO amostraVO) {
		getAgruparExamesON().desagruparAmostras(listaAmostraSelecionadas, listaAmostras, amostraVO);
	}	
	
	//QUALIDADE
	@Override
	public List<AelAmostraRecebidaVO> gravarAmostras(final List<AmostraVO> listaAmostras, final Integer solicitacaoNumero, final RapServidores servidorLogado,
			final AghUnidadesFuncionais unidadeExecutora, final List<ExameAndamentoVO> listaExamesAndamento,
			final String nomeMicrocomputador, final Map<AghuParametrosEnum, String> situacao) throws ApplicationBusinessException, BaseException {
		return this.getAgruparExamesON().gravarAmostras(listaAmostras, solicitacaoNumero, servidorLogado, unidadeExecutora, 
				listaExamesAndamento, nomeMicrocomputador, situacao);
	}
	
	public AgruparExamesON getAgruparExamesON() {
		return agruparExamesON;
	}
	
	public AgruparExamesRN getAgruparExamesRN() {
		return agruparExamesRN;
	}

	@Override
	public List<Integer> getSeqpAmostrasRecebidas(List<AelAmostrasVO> listaAmostrasRecebidas) {
		return getAgruparExamesON().getSeqpAmostrasRecebidas(listaAmostrasRecebidas);
	}

	@Override
	public void selecionarExameAndamento(ExameAndamentoVO exameAndamento,
			List<ExameAndamentoVO> listaExamesAndamento,
			Set<ExameAndamentoVO> listaExamesSelecionados) {
		getAgruparExamesON().selecionarExameAndamento(exameAndamento, listaExamesAndamento, listaExamesSelecionados);
		
	}

	@Override
	public boolean selecionarAmostraExibirPatologistaResponsavel(
			AmostraVO amostraVO, List<AmostraVO> listaAmostras,
 List<AmostraVO> listaAmostraSelecionadas) {
		return getAgruparExamesON().selecionarAmostraExibirPatologistaResponsavel(amostraVO, listaAmostras, listaAmostraSelecionadas);
	}

	@Override
	public void desfazerSelecaoTodasAmostras(List<AmostraVO> listaAmostras, List<AmostraVO> listaAmostraSelecionadas) {
		getAgruparExamesON().desfazerSelecaoTodasAmostras(listaAmostras, listaAmostraSelecionadas);
	}

	@Override
	public void selecionarTodasAmostras(boolean allChecked, List<AmostraVO> listaAmostras, List<AmostraVO> listaAmostraSelecionadas) {
		getAgruparExamesON().selecionarTodasAmostras(allChecked, listaAmostras, listaAmostraSelecionadas);
	}

	@Override
	public void excluirPatologista(List<AmostraVO> listaAmostras, AelPatologista patologistaResponsavelSelecionado) {
		this.getAgruparExamesON().excluirPatologista(listaAmostras, patologistaResponsavelSelecionado);
	}

	@Override
	public void verificaStatusExame(AelAmostras amostra)
			throws ApplicationBusinessException {
		this.getListarAmostrasSolicitacaoRecebimentoRN().verificaStatusExamePendente(amostra);
	}

	@Override
	public void confirmarPatologistasResponsaveis(final List<AmostraVO> listaAmostraSelecionadas, final AelPatologista patologistaResponsavel) {
		getAgruparExamesON().confirmarPatologistasResponsaveis(listaAmostraSelecionadas, patologistaResponsavel);
	}
	
	@Override
	public Boolean habilitaBotaoTecnica(TelaLaudoUnicoVO telaLaudoVO, DominioSituacaoExamePatologia domain) {
		return getLaudoUnicoTecnicaRN().habilitaBotaoTecnica(telaLaudoVO, domain);
	}
	
	protected LaudoUnicoTecnicaRN getLaudoUnicoTecnicaRN() {
		return laudoUnicoTecnicaRN;
	}
	
	@Override
	public void buscaSecoesConfiguracaoObrigatorias(TelaLaudoUnicoVO telaLaudoVO, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		getLaudoUnicoTecnicaRN().buscaSecoesConfiguracaoObrigatorias(telaLaudoVO, domain);
	}
	
	@Override
	@Secure("#{s:hasPermission('emissaoMapaTrabalho','pesquisar')}")
	public AelConfigMapa obterAelConfigMapa(final Short seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getAelConfigMapaDAO().obterPorChavePrimaria(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	@Override
	public AelTipoAmostraExame obterAelTipoAmostraExame(AelTipoAmostraExameId id) {
		return this.getAelTipoAmostraExameDAO().obterAelTipoAmostraExame(id);
	}


	@Override
	public String obterNomeArquivoSecretariaNotificacao(Boolean isCD4, Date dataIni) {
		return arquivoSecretariaNotificacaoON.obterNomeArquivo(isCD4, dataIni);
	}

	@Override
	public String nameHeaderEfetuarDownloadArquivoSecretaria(Date dataInicio, Boolean infantil) {
		return getArquivoSecretariaNotificacaoON().nameHeaderEfetuarDownloadArquivoSecretaria(dataInicio, infantil);
	}

	public java.util.List<AelTmpIntervaloColeta> pesquisarTempoPorIntervaloColeta(Short codigoIntervaloColeta) {
		return getAelTmpIntervaloColetaDAO().listarPorIntervaloColeta(codigoIntervaloColeta);
	}
	
	@Override
	public AelSolicitacaoExames obterAelSolicitacaoExamePorChavePrimaria(Integer seq, Enum fetchArgs) {
		return getAelSolicitacaoExameDAO().obterPorChavePrimaria(seq, fetchArgs);
	}

	@Override
	public ResultadoExamePim2VO obterExamePim2(Integer atdSeq, String[] listaCampos) {
		return getAelSolicitacaoExameDAO().obterExamePim2(atdSeq, listaCampos);
	}
	
	@Override
	public AelSolicitacaoExamesHist obterAelSolicitacaoExameHistPorChavePrimaria(Integer seq, Enum fetchArgs) {
		return aelSolicitacaoExamesHistDAO.obterPorChavePrimaria(seq, fetchArgs);
	}

	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames obteritemSolicitacaoExamesPorChavePrimaria(AelItemSolicitacaoExamesId id, Enum[] innerFields, Enum[] leftFields) {
		return getAelItemSolicitacaoExamesDAO().obterPorChavePrimaria(id, innerFields, leftFields);
	}

	@Override
	@BypassInactiveModule
	public AelItemSolicExameHist obteritemSolicitacaoExamesHistPorChavePrimaria(AelItemSolicExameHistId id, Enum[] innerFields, Enum[] leftFields) {
		return getAelItemSolicExameHistDAO().obterPorChavePrimaria(id, innerFields, leftFields);
	}

	@Override
	public AelCampoLaudo obterCampoLaudoPorId(final Integer seq, boolean left, Enum... fechArgs) {
		return getAelCampoLaudoDAO().obterPorChavePrimaria(seq, left, fechArgs);
	}

	@Override
	public AelAmostras buscarAmostrasComRecepientePorId(final Integer soeSeq, final Short seqp) {
		return getAelAmostrasDAO().buscarAmostrasComRecepientePorId(soeSeq, seqp);
	}

	@Override
	public List<AelExtratoItemCartas> buscarAelExtratoItemCartasPorItemCartasComMotivoRetorno(AelItemSolicCartas aelItemSolicCartas) {
		return extratoItemCartasDAO.buscarAelExtratoItemCartasPorItemCartasComMotivoRetorno(aelItemSolicCartas);
	}

	@Override
	public AelItemSolicCartas obterAelItemSolicCartasComPaciente(AelItemSolicCartasId id) {
		return getAelItemSolicCartasDAO().obterAelItemSolicCartasComPaciente(id);
	}

	@Override
	public List<AelCampoLaudo> pesquisarAelCampoLaudoSB(final DominioTipoCampoCampoLaudo tipo, final Object pesquisa) {
		return this.getAelCampoLaudoDAO().pesquisarAelCampoLaudoSB(tipo, pesquisa);
	}

	@Override
	public Long pesquisarAelCampoLaudoSBCount(final DominioTipoCampoCampoLaudo tipo, final Object pesquisa) {
		return this.getAelCampoLaudoDAO().pesquisarAelCampoLaudoSBCount(tipo, pesquisa);
	}

	@Override
	public List<AelItemSolicitacaoExamesId> listarAelItemSolicitacaoExamesPorSiglaMaterialPaciente(String siglaExame, Integer seqMaterial, Integer codPaciente) {
		return getAelItemSolicitacaoExamesDAO().listarPorSiglaMaterialPaciente(siglaExame, seqMaterial, codPaciente);
	}

	@Override
	public List<AelResultadoExame> pesquisarAelResultadoExamePorAelItemSolicitacaoExames(Integer soeSeq, Short seqp) {
		return getAelResultadoExameDAO().pesquisarPorAelItemSolicitacaoExames(soeSeq, seqp);
	}

	@Override
	public String obterAelResultadoCodificadoDescricao(Integer gtcSeq, Integer seqp) {
		return getAelResultadoCodificadoDAO().obterDescricao(gtcSeq, seqp);
	}

	@Override
	public String obterAelResultadoCaracteristicaDescricao(Integer seq) {
		return getAelResultadoCaracteristicaDAO().obterDescricao(seq);
	}


	@Override
	public List<AelUnidExameSignificativo> pesquisarUnidadesFuncionaisExamesSignificativosPerinato(Short unfSeq, String siglaExame, Integer seqMatAnls,
			Boolean indCargaExame, int firstResult, int maxResults) {
		return getAelUnidExameSignificativoDAO().pesquisarUnidadesFuncionaisExamesSignificativosPerinato(unfSeq, siglaExame, seqMatAnls, indCargaExame,
				firstResult, maxResults);
	}

	@Override
	public Long pesquisarUnidadesFuncionaisExamesSignificativosPerinatoCount(Short unfSeq, String siglaExame, Integer seqMatAnls, Boolean indCargaExame) {
		return getAelUnidExameSignificativoDAO().pesquisarUnidadesFuncionaisExamesSignificativosPerinatoCount(unfSeq, siglaExame, seqMatAnls, indCargaExame);
	}


	@Override
	public List<AelExamesMaterialAnalise> pesquisarAtivosPorSiglaOuDescricao(String parametro, Integer maxResults) {
		return getAelExamesMaterialAnaliseDAO().pesquisarAtivosPorSiglaOuDescricao(parametro, maxResults);
	}

	@Override
	public Long pesquisarAtivosPorSiglaOuDescricaoCount(String parametro) {
		return getAelExamesMaterialAnaliseDAO().pesquisarAtivosPorSiglaOuDescricaoCount(parametro);
	}

	@Override
	public void persistirAelUnidExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq, Date data, Integer matricula, Short vinCodigo,
			Boolean indPreNatal, Boolean indCargaExame) {
		getAelUnidExameSignificativoRN().persistir(unfSeq, exaSigla, matAnlsSeq, data, matricula, vinCodigo, indPreNatal, indCargaExame);
	}


	@Override
	public void removerAelUnidExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq) {
		getAelUnidExameSignificativoRN().remover(unfSeq, exaSigla, matAnlsSeq);
	}

	@Override
	public List<AelUnidExameSignificativo> pesquisarAelUnidExameSignificativoPorUnfSeq(Short unfSeq, Boolean indCargaExame) {
		return getAelUnidExameSignificativoDAO().pesquisarAelUnidExameSignificativoPorUnfSeq(unfSeq, indCargaExame);
	}

	@Override
	public List<AelExamesMaterialAnalise> pesquisarExamesPorSiglaMaterialAnalise(String sigla, Integer seqMatAnls) {
		return getAelExamesMaterialAnaliseDAO().pesquisarExamesPorSiglaMaterialAnalise(sigla, seqMatAnls);
	}

	protected AelUnidExameSignificativoDAO getAelUnidExameSignificativoDAO() {
		return aelUnidExameSignificativoDAO;
	}
	
	protected AelResultadoExameDAO getAelResultadoExameDAO() {
		return aelResultadoExameDAO;
	}
	
	protected AelUnidExameSignificativoRN getAelUnidExameSignificativoRN() {
		return aelUnidExameSignificativoRN;
	}
	
	@Override
	public List<AelGrupoRecomendacaoExame> obterAelGrupoRecomendacaoExamesPorAelGrupoRecomendacao(AelGrupoRecomendacao grupo) {
		return aelGrupoRecomendacaoExameDAO.obterAelGrupoRecomendacaoExamesPorAelGrupoRecomendacao(grupo);
	}
	
	/**
	 * Web Service #39251 utilizado na estória #864
	 * @param pacCodigo
	 * @return Boolean
	 */
	@Override
	public Boolean verificaPacienteEmProjetoPesquisa(Integer pacCodigo){
		return this.getAelProjetoPacientesDAO().verificaPacienteEmProjetoPesquisa(pacCodigo);
	}
	
	@Override
	public Date obterDataPrimeiraSolicitacaoExamePeloNumConsulta(Integer conNumero) {
		return getAelSolicitacaoExameDAO().obterDataPrimeiraSolicitacaoExamePeloNumConsulta(conNumero);
	}

	@Override
	@BypassInactiveModule
	public List<AelRegiaoAnatomica> pesquisarRegioesAnatomicasAtivasPorDescricaoSeq(String param) {		
		return aelRegiaoAnatomicaDAO.pesquisarRegioesAnatomicasAtivasPorDescricaoSeq(param);
	}

	@Override
	@BypassInactiveModule
	public Boolean verificarRegioesPorSeqAchadoDescricao(List<Integer> seqs, String descricao) {
		return aelRegiaoAnatomicaDAO.verificarRegioesPorSeqAchadoDescricao(seqs, descricao);
	}

	@Override
	@BypassInactiveModule
	public List<AelRegiaoAnatomica> buscarRegioesAnatomicas(String descricao) {
		return aelRegiaoAnatomicaDAO.buscarRegioesAnatomicas(descricao);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarRegioesAnatomicasAtivasPorDescricaoSeqCount(String param) {
		return aelRegiaoAnatomicaDAO.pesquisarRegioesAnatomicasAtivasPorDescricaoSeqCount(param);
	}

	@Override
	public AelMateriaisAnalises buscarMaterialAnalisePorSeq(Integer obterSeq) {
		return aelMaterialAnaliseDAO.obterPeloId(obterSeq);
	}
	
	@Override
	public List<ResultadoExamesVO> obterResultadoExamesSaps3(Integer atdSeq,String param1, Integer param2, Integer param3) {
		return  aelResultadoExameDAO.obterResultadoExamesSaps3(atdSeq,param1,param3,param3);
	}

	@Override
	public String buscarLocalizadorExamesInternet(Integer atendimentoSeq) {
		return aelSolicitacaoExameDAO.buscarLocalizadorExamesInternet(atendimentoSeq);
	}
	
	@Override
	public Boolean gerarPCT(Integer atdSeq, Boolean urgente, DominioSituacaoColeta situacaoColeta,
			Integer shaSeq,	String nomeMicrocomputador, RapServidores servidorLogado, RapServidores responsavel) throws BaseException {
		return provaCruzadaON.gerarPCT(atdSeq, urgente, situacaoColeta, shaSeq, nomeMicrocomputador, servidorLogado, responsavel);
	}

	@Override
	public String obterConvenioAtendimento(Integer atdSeq) {
		return this.informacaoComplementarRN.obterConvenioAtendimento(atdSeq);
	}
	
	@Override
	public Long obterLaboratorioExternoListCount(String parametro) {
		return getAelLaboratorioExternosDAO().obterLaboratorioExternoListCount(
				parametro);
	}	

	@Override
	@BypassInactiveModule
	public String buscarLaudoProntuarioPaciente(
			final IAelSolicitacaoExames solicitacaoExames) {
		return getRelatorioTicketExamesPacienteON()
				.buscarLaudoProntuarioPaciente(solicitacaoExames);
	}

	@Override
	@BypassInactiveModule
	public String buscarLaudoNomePaciente(
			final IAelSolicitacaoExames solicitacaoExames) {
		return getRelatorioTicketExamesPacienteON().buscarLaudoNomePaciente(
				solicitacaoExames);
	}

	public String buscarLaudoNomeMaeRecemNascido(final IAelSolicitacaoExames solicitacaoExames){
		return getRelatorioTicketExamesPacienteON().buscarLaudoNomeMaeRecemNascido(solicitacaoExames);
	}
	
	@Override
	public ExamesListaVO buscarDadosLaudo(
			List<IAelItemSolicitacaoExamesId> itemIds) throws ApplicationBusinessException, BaseException {
		return this.getMascaraExamesJasperReportON().buscarDadosLaudo(itemIds);
	}

	@Override
	public ExamesListaVO buscarDadosLaudoFPreview(List<AelParametroCamposLaudo> parametrosPrevia, AelVersaoLaudo versaoLaudo) throws BaseException {
		return mascaraExamesJasperFPreviewON.buscarDadosLaudo(parametrosPrevia, versaoLaudo);
	}
	
	@Override
	@BypassInactiveModule
	public ResultadoLaudoVO executaLaudo(
			List<IAelItemSolicitacaoExamesId> itemIds,
			DominioTipoImpressaoLaudo tipoLaudo)
			throws ApplicationBusinessException,
			BaseException {
		return this.getLaudosExamesON().executaLaudo(itemIds, tipoLaudo);
	}

	@Override
	@BypassInactiveModule
	public ResultadoLaudoVO executaLaudo(ExamesListaVO dadosLaudo,
			DominioTipoImpressaoLaudo tipoLaudo)
			throws ApplicationBusinessException,
			BaseException {
		return this.getLaudosExamesON().executaLaudo(dadosLaudo,
				tipoLaudo);
	}

	@Override
	public ExamesListaVO buscarDadosLaudoPrevia(
			List<AelParametroCamposLaudo> parametrosPrevia,
			AelVersaoLaudo versaoLaudo) throws BaseException {
		return this.getMascaraExamesPreviaON().buscarDadosLaudo(
				parametrosPrevia, versaoLaudo);
	}

	@Override
	public Short mbcLocalPacMbc(Integer atdSeq) throws ApplicationBusinessException {
		return this.provaCruzadaON.mbcLocalPacMbc(atdSeq);
	}

	protected MascaraExamesPreviaON getMascaraExamesPreviaON() {
		return this.mascaraExamesPreviaON;
	}	
	
	protected MascaraExamesJasperReportON getMascaraExamesJasperReportON() {
		return this.mascaraExamesJasperReportON;
	}
	
	protected LaudosExamesON getLaudosExamesON() {
		return this.laudosExamesON;
	}

	@Override
	public String getEnderecoPaciente(Integer pacCodigo) {
		return arquivoSecretariaNotificacaoRN.obterEnderecoPaciente(pacCodigo);
	}
	
	public List<AghResponsavel> listarResponsavel(String parametro) {
		return this.aghResponsaveldao.listarResponsavel(parametro);
	}
	
	public List<AghPaisBcb> listarPaisesBcb(String parametro, AghPaisBcb paisBrasil) {
		return this.aghPaisBcbDAO.listarPaisesBcb(parametro, paisBrasil);
	}
	
	public AghPaisBcb obterAghPaisBcb(Integer seq) {
		return this.aghPaisBcbDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public Boolean hasAelExameApItemSolicPorSolicitacao(Integer iseSoeSeq) {
		return this.aelExameApItemSolicDAO.hasAelExameApItemSolicPorSolicitacao(iseSoeSeq);
	}
	
	@Override
	public Long pesquisarAelQuestionariosCount(String param) {
		return this.getAelQuestionariosDAO().pesquisarAelQuestionariosCount(param);
	}
	@Override
	@BypassInactiveModule
	public Boolean possuiExameDeImagem(AelItemSolicitacaoExamesId id) {
		return aelItemSolicitacaoExameDAO.verificarSeItemTemExameDeImagem(id);
	}
	
		@Override
	public String obterFatorRhExamesRealizados(Integer pacCodigo){
		return aelResultadoExameDAO.obterFatorRhExamesRealizados(pacCodigo);
	}

	@Override
	public String obterFatorFatorSanguinioExamesRealizados(Integer pacCodigo){
		return aelResultadoExameDAO.obterFatorFatorSanguinioExamesRealizados(pacCodigo);
	}
	@Override
	public List<ImprimeEtiquetaVO> gerarEtiquetasAmostrasRecebidasUnf(
			List<AelAmostrasVO> listaAmostras,
			AghUnidadesFuncionais unidadeExecutora) {
		return this.getReceberTodasAmostrasSolicitacaoRN().gerarEtiquetasAmostrasRecebidasUnf(listaAmostras, unidadeExecutora);
	}
	public void verificarSetarMaiorTempoJejum(List<TicketExamesPacienteVO> tickets) {
		this.getRelatorioTicketExamesPacienteON().verificarSetarMaiorTempoJejum(tickets);
	}
	@Override
	public List<AelExames> obterAelExamesPorSiglaDescricao(String _filtro){
		return aelExamesDAO.obterAelExamesPorSiglaDescricao(_filtro);
	}
	
	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames obterItemSolicitacaoExamesComExame(Integer soeSeq, Short seqp) {
		return getAelItemSolicitacaoExamesDAO().obterItemSolicitacaoExamesComExame(soeSeq, seqp);
	}
	
	@BypassInactiveModule
	public AelItemSolicitacaoExames obterItemSolicitacaoExamesComAtendimento(Integer soeSeq, Short seqp) {
		return getAelItemSolicitacaoExamesDAO().obterItemSolicitacaoExamesComAtendimento(soeSeq, seqp);
	}
	@Override
	public Long obterAelExamesPorSiglaDescricaoCount(String _filtro){
		return aelExamesDAO.obterAelExamesPorSiglaDescricaoCount(_filtro);
	}
	
	@Override
	public List<AelExamesXAelParametroCamposLaudoVO> obterAelCampoLaudoPorNome(String nome, String siglaExame){
		return aelCampoLaudoDAO.obterAelCampoLaudoPorNome(nome, siglaExame);
	}
	
	@Override
	public Long obterAelCampoLaudoPorNomeCount(String nome, String siglaExame){
		return aelCampoLaudoDAO.obterAelCampoLaudoPorNomeCount(nome, siglaExame);
	}
	
	@Override
	public Boolean pesquisarExamesResultadoNaoVisualizado(final Integer atdSeq) {
		return aelItemSolicitacaoExameDAO.pesquisarExamesResultadoNaoVisualizado(atdSeq);
	}
	
	public void setCaminhoLogo(String caminhoLogo){
		getLaudosExamesON().setCaminhoLogo(caminhoLogo);
	}
}