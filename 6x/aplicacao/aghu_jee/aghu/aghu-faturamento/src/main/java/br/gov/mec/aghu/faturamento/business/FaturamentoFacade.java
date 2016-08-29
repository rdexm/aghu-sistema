package br.gov.mec.aghu.faturamento.business;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.FatLaudosPacApacsDAO;
import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaCodigoProcedimentoSusVO;
import br.gov.mec.aghu.blococirurgico.vo.CursoPopulaProcedimentoHospitalarInternoVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentosCirurgicosPdtAtivosVO;
import br.gov.mec.aghu.blococirurgico.vo.VFatSsmInternacaoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.configuracao.dao.AghClinicasDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioGrupoProcedimento;
import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemPendencia;
import br.gov.mec.aghu.dominio.DominioMensagemEstornoAIH;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioModuloMensagem;
import br.gov.mec.aghu.dominio.DominioOpcaoEncerramentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioOrigemSugestoesDesdobramento;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;
import br.gov.mec.aghu.dominio.DominioTipoNutricaoParenteral;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.dominio.TipoArquivoRelatorio;
import br.gov.mec.aghu.exames.questionario.vo.VFatProcedSusPhiVO;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.FatCaractComplexidadeRN;
import br.gov.mec.aghu.faturamento.dao.BackupCntaConvDAO;
import br.gov.mec.aghu.faturamento.dao.CntaConvDAO;
import br.gov.mec.aghu.faturamento.dao.ConvDAO;
import br.gov.mec.aghu.faturamento.dao.FatAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatArqEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.dao.FatAtendimentoApacProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatAutorizaApacsDAO;
import br.gov.mec.aghu.faturamento.dao.FatBancoCapacidadeDAO;
import br.gov.mec.aghu.faturamento.dao.FatCandidatosApacOtorrinoDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractComplexidadeDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractFinanciamentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatCboDAO;
import br.gov.mec.aghu.faturamento.dao.FatCidContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatCnesUfDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompatExclusItemDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaCompatibilidDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaProdDAO;
import br.gov.mec.aghu.faturamento.dao.FatContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatContaSugestaoDesdobrDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvGrupoItensProcedDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvPlanoAcomodacoesDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvTipoDocumentosDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.faturamento.dao.FatDadosContaSemIntDAO;
import br.gov.mec.aghu.faturamento.dao.FatDiariaInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatDocumentoCobrancaAihsDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoProcedSiscoloDAO;
import br.gov.mec.aghu.faturamento.dao.FatExcCnvGrpItemProcDAO;
import br.gov.mec.aghu.faturamento.dao.FatExcecaoPercentualDAO;
import br.gov.mec.aghu.faturamento.dao.FatFormaOrganizacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatGrupoDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemProcHospTranspDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.faturamento.dao.FatLogInterfaceDAO;
import br.gov.mec.aghu.faturamento.dao.FatMensagemLogDAO;
import br.gov.mec.aghu.faturamento.dao.FatModalidadeAtendimentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoCobrancaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobrClinicaDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobrSsmDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobramentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoPendenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoRejeicaoContaDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatPacienteTransplantesDAO;
import br.gov.mec.aghu.faturamento.dao.FatPacienteTratamentosDAO;
import br.gov.mec.aghu.faturamento.dao.FatPendenciaContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatPeriodosEmissaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatPossibilidadeRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospIntCidDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoCboDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoRegistroDAO;
import br.gov.mec.aghu.faturamento.dao.FatResumoApacsDAO;
import br.gov.mec.aghu.faturamento.dao.FatSaldoUtiDAO;
import br.gov.mec.aghu.faturamento.dao.FatServClassificacoesDAO;
import br.gov.mec.aghu.faturamento.dao.FatSinonimoItemProcedHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatSituacaoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatSubGrupoDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAtoDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoTransplanteDAO;
import br.gov.mec.aghu.faturamento.dao.FatTiposDocumentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatTiposVinculoDAO;
import br.gov.mec.aghu.faturamento.dao.FatValorContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatValorDiariaInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssocProcCidsDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimento2DAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContaHospitalarPacDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContasHospPacientesDAO;
import br.gov.mec.aghu.faturamento.dao.VFatConvPlanoGrupoProcedDAO;
import br.gov.mec.aghu.faturamento.vo.AIHFaturadaPorPacienteVO;
import br.gov.mec.aghu.faturamento.vo.AihFaturadaVO;
import br.gov.mec.aghu.faturamento.vo.AihPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.AihsFaturadasPorClinicaVO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.CaracteristicaPhiVO;
import br.gov.mec.aghu.faturamento.vo.ClinicaPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.ConsultaRateioProfissionalVO;
import br.gov.mec.aghu.faturamento.vo.ContaApresentadaPacienteProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.ContaHospitalarInformarSolicitadoVO;
import br.gov.mec.aghu.faturamento.vo.ContasPreenchimentoLaudosVO;
import br.gov.mec.aghu.faturamento.vo.CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.DadosCaracteristicaTratamentoApacVO;
import br.gov.mec.aghu.faturamento.vo.DemonstrativoFaturamentoInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.FatArqEspelhoProcedAmbVO;
import br.gov.mec.aghu.faturamento.vo.FatBancoCapacidadeVO;
import br.gov.mec.aghu.faturamento.vo.FatCnesVO;
import br.gov.mec.aghu.faturamento.vo.FatDadosContaSemIntVO;
import br.gov.mec.aghu.faturamento.vo.FatEspelhoEncerramentoPreviaVO;
import br.gov.mec.aghu.faturamento.vo.FatLogErrorVO;
import br.gov.mec.aghu.faturamento.vo.FatMotivoRejeicaoContasVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedAmbRealizadosVO;
import br.gov.mec.aghu.faturamento.vo.FatSaldoUTIVO;
import br.gov.mec.aghu.faturamento.vo.FatUnidadeFuncionalCnesVO;
import br.gov.mec.aghu.faturamento.vo.FatcVerCarPhiCnvVO;
import br.gov.mec.aghu.faturamento.vo.FaturaAmbulatorioVO;
import br.gov.mec.aghu.faturamento.vo.FaturamentoPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.FtLogErrorVO;
import br.gov.mec.aghu.faturamento.vo.ImprimirVencimentoTributosVO;
import br.gov.mec.aghu.faturamento.vo.ItemProcedimentoHospitalarVO;
import br.gov.mec.aghu.faturamento.vo.ItensRealizadosIndividuaisVO;
import br.gov.mec.aghu.faturamento.vo.ListarContasHospPacientesPorPacCodigoVO;
import br.gov.mec.aghu.faturamento.vo.LogInconsistenciaBPAVO;
import br.gov.mec.aghu.faturamento.vo.LogInconsistenciasInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.ParametrosGeracaoLaudoOtorrinoVO;
import br.gov.mec.aghu.faturamento.vo.PendenciaEncerramentoVO;
import br.gov.mec.aghu.faturamento.vo.PendenciasEncerramentoVO;
import br.gov.mec.aghu.faturamento.vo.PreviaDiariaFaturamentoVO;
import br.gov.mec.aghu.faturamento.vo.ProcedimentoNaoFaturadoVO;
import br.gov.mec.aghu.faturamento.vo.ProcedimentoRealizadoDadosOPMVO;
import br.gov.mec.aghu.faturamento.vo.ProtocoloAihVO;
import br.gov.mec.aghu.faturamento.vo.ProtocolosAihsVO;
import br.gov.mec.aghu.faturamento.vo.RateioValoresSadtPorPontosVO;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOPMNaoFaturadaVO;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOrtesesProtesesVO;
import br.gov.mec.aghu.faturamento.vo.ResumoAIHEmLoteServicosVO;
import br.gov.mec.aghu.faturamento.vo.ResumoAIHEmLoteVO;
import br.gov.mec.aghu.faturamento.vo.ResumoCobrancaAihServicosVO;
import br.gov.mec.aghu.faturamento.vo.ResumoCobrancaAihVO;
import br.gov.mec.aghu.faturamento.vo.RnCapcCboProcResVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerDatasVO;
import br.gov.mec.aghu.faturamento.vo.SugestoesDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.TotaisPorDCIHVO;
import br.gov.mec.aghu.faturamento.vo.TotalGeralClinicaPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.faturamento.vo.ValoresAIHPorDCIHVO;
import br.gov.mec.aghu.faturamento.vo.ValoresPreviaVO;
import br.gov.mec.aghu.internacao.business.vo.AtualizarContaInternacaoVO;
import br.gov.mec.aghu.internacao.dao.AinTiposCaraterInternacaoDAO;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.RelatorioIntermediarioLancamentosContaVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.model.FatProcedHospInternosPai.Fields;
import br.gov.mec.aghu.paciente.dao.AipEtniaDAO;
import br.gov.mec.aghu.paciente.dao.AipNacionalidadesDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioLaudosProcSusVO;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioMudancaProcedimentosVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.custos.vo.AssociacaoProcedimentoVO;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoCirurgiaVO;
import br.gov.mec.aghu.sig.custos.vo.SigValorReceitaVO;

/**
 * Porta de entrada do módulo de faturamento.
 * 
 */

@Modulo(ModuloEnum.FATURAMENTO)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.NcssTypeCount",
		"PMD.CouplingBetweenObjects" })
@Stateless
public class FaturamentoFacade extends BaseFacade implements IFaturamentoFacade {
	
	@Inject
	private FatAutorizaApacsDAO fatAutorizaApacsDAO;

	@Inject
	private AghClinicasDAO aghClinicasDAO;
	
	@Inject
	private FatBancoCapacidadeDAO fatBancoCapacidadeDAO;
	
	@Inject
	private FatAtendimentoApacProcHospDAO fatAtendimentoApacProcHospDAO;

	@Inject
	private FatSaldoUtiDAO fatSaldoUtiDAO;
	
	@EJB
	private FatBancoCapacidadeRN fatBancoCapacidadeRN;
	
	@EJB
	private FatSaldoUTIRN saldoUtiRN;
	
	@EJB
	private FaturamentoFatkCapUniRN faturamentoFatkCapUniRN;


	@EJB
	private FatkCth4RN fatkCth4RN;
	
	@Inject
	private FatMotivoDesdobrClinicaDAO fatMotivoDesdobrClinicaDAO;

	@EJB
	private FaturamentoFatkInterfaceAacRN faturamentoFatkInterfaceAacRN;

	@EJB
	private FatDadosContaSemIntRN fatDadosContaSemIntRN;

	@EJB
	private ProcedimentosAmbRealizadoRN procedimentosAmbRealizadoRN;
	
	@EJB
	private FatProcedAmbRealizadoRN fatProcedAmbRealizadoRN;

	@EJB
	private FatItemContaApacRN fatItemContaApacRN;
	
//	@EJB
//	private GerarArquivoSmsON gerarArquivoSmsON;

	@EJB
	private ValorItemProcedimentoON valorItemProcedimentoON;

	@EJB
	private FaturamentoON faturamentoON;

	@EJB
	private FatProcedHospIntCidON fatProcedHospIntCidON;

	@EJB
	private PacienteTransplantadoRN pacienteTransplantadoRN;

	@EJB
	private FatProcedHospInternoON fatProcedHospInternoON;

	@EJB
	private ManterFatProcedHospInternosRN manterFatProcedHospInternosRN;

	@EJB
	private GerarSugestoesDesdobramentoON gerarSugestoesDesdobramentoON;

	@Inject
	private AinTiposCaraterInternacaoDAO ainTiposCaraterInternacaoDAO;

	@EJB
	private RelatorioCSVFaturamentoON relatorioCSVFaturamentoON;

	@EJB
	private RelatorioRateioValoresSadtPorPontosON relatorioRateioValoresSadtPorPontosON;

	@EJB
	private RelatorioResumoCobrancaAihON relatorioResumoCobrancaAihON;

	@EJB
	private RelatorioResumoCobrancaAihRN relatorioResumoCobrancaAihRN;

	@EJB
	private FatkInterfacePrescricaoRN fatkInterfacePrescricaoRN;

	@EJB
	private ReapresentarContaHospitalarRN reapresentarContaHospitalarRN;

	@EJB
	private CaracteristicaItemProcedimentoHospitalarON caracteristicaItemProcedimentoHospitalarON;

	@EJB
	private ItemProcedimentoHospitalarON itemProcedimentoHospitalarON;

	@EJB
	private RelatorioItensRealizIndvON relatorioItensRealizIndvON;

	@EJB
	private RelatorioAihFaturadaON relatorioAihFaturadaON;

	@EJB
	private AtualizaFaturamentoBlocoCirurgicoRN atualizaFaturamentoBlocoCirurgicoRN;

	@EJB
	private FatLogErrorON fatLogErrorON;

	@EJB
	private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;

	@EJB
	private FatCandidatosApacOtorrinoON fatCandidatosApacOtorrinoON;

	@EJB
	private ItemContaHospitalarON itemContaHospitalarON;

	@EJB
	private FatCompetenciaRN fatCompetenciaRN;

	@EJB
	private FaturamentoRN faturamentoRN;

	@EJB
	private FaturamentoInternacaoRN faturamentoInternacaoRN;
	
	@EJB
	private FatkCth2RN fatkCth2RN;
	
	@EJB
	private FatkCthnRN fatkCthnRN;

	@EJB
	private FatArqEspelhoProcedAmbON fatArqEspelhoProcedAmbON;

	@EJB
	private FaturamentoAmbulatorioON faturamentoAmbulatorioON;

	@EJB
	private FatContaInternacaoRN fatContaInternacaoRN;

	@EJB
	private ImportarArquivoSusON importarArquivoSusON;

	@EJB
	private ItensProcedHospitalarRN itensProcedHospitalarRN;

	@EJB
	private ContaHospitalarRN contaHospitalarRN;

	@EJB
	private VerificarCirurgiaFaturadaRN verificarCirurgiaFaturadaRN;

	@EJB
	private FaturamentoFatkAmbRN faturamentoFatkAmbRN;

	@EJB
	private CaracteristicaTratamentoApacRN caracteristicaTratamentoApacRN;

	@EJB
	private ContaHospitalarON contaHospitalarON;

	@EJB
	private EncerramentoContaHospitalarON encerramentoContaHospitalarON;
	
	@EJB
	private FatCompetenciaON fatCompetenciaON;

	@EJB
	private EncerramentoAutomaticoContaHospitalarON encerramentoAutomaticoContaHospitalarON;
	
	@EJB
	private FatCompatExclusItemON fatCompatExclusItemON;

	@EJB
	private CidContaHospitalarON cidContaHospitalarON;

	@EJB
	private ProgramarEncerramentoON programarEncerramentoON;

	@EJB
	private ProcedimentoCboON procedimentoCboON;

	@EJB
	private FatExcCnvGrpItemProcON fatExcCnvGrpItemProcON;

	@EJB
	private ConvenioGrupoItemProcedimentoON convenioGrupoItemProcedimentoON;

	@EJB
	private VerificacaoFaturamentoSusRN verificacaoFaturamentoSusRN;

	@EJB
	private GeracaoArquivoFaturamentoCompetenciaInternacaoON geracaoArquivoFaturamentoCompetenciaInternacaoON;
    @EJB
	private GeracaoArquivoParcialCompetenciaInternacaoON geracaoArquivoParcialCompetenciaInternacaoON;

	@EJB
	private EspelhoAihPersist espelhoAihPersist;

	@EJB
	private FatCampoMedicoAuditAihPersist fatCampoMedicoAuditAihPersist;

	@EJB
	private ImprimirVencimentoTributosRN imprimirVencimentoTributosRN;

	@EJB
	private FatCaractFinanciamentoRN fatCaractFinanciamentoRN;

	@EJB
	private ContaNutricaoEnteralRN contaNutricaoEnteralRN;

	@EJB
	private FatMotivoPendenciaRN fatMotivoPendenciaRN;
	
	@EJB
	private ContaProcedProfissionalVinculoIncorretoON contaProcedProfissionalVinculoIncorretoON;
	
	@EJB
	private ContaNptRN contaNptRN;
	
	@EJB
	private ContaRepresentadaRN contaRepresentadaRN;
	
	@EJB
	private FatItemProcHospTranspRN fatItemProcHospTranspRN;
	
	@EJB
	private RelatorioSugestoesDesdobramentoON relatorioSugestoesDesdobramentoON;
	
	@EJB
	private RecebimentoAihsON recebimentoAihsON;
	
	@EJB
	private FatCaractComplexidadeRN fatCaractComplexidadeRN;
	
	@EJB
	private EspelhoAihRN espelhoAihRN;
	
	@Inject
	private FatValorDiariaInternacaoDAO fatValorDiariaInternacaoDAO;

	@Inject
	private FatMotivoRejeicaoContaDAO fatMotivoRejeicaoContaDAO;

	@Inject
	private FatCompatExclusItemDAO fatCompatExclusItemDAO;

	@Inject
	private FatCboDAO fatCboDAO;

	@Inject
	private FatItemContaApacDAO fatItemContaApacDAO;

	@Inject
	private FatResumoApacsDAO fatResumoApacsDAO;

	@Inject
	private FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO;

	@Inject
	private FatSinonimoItemProcedHospDAO fatSinonimoItemProcedHospDAO;

	@Inject
	private FatGrupoDAO fatGrupoDAO;

	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;

	@Inject
	private FatTipoCaractItensDAO fatTipoCaractItensDAO;

	@Inject
	private FatSituacaoSaidaPacienteDAO fatSituacaoSaidaPacienteDAO;

	@Inject
	private FatProcedHospIntCidDAO fatProcedHospIntCidDAO;

	@Inject
	private FatTiposDocumentoDAO fatTiposDocumentoDAO;

	@Inject
	private FatTipoAihDAO fatTipoAihDAO;
	
	@Inject
	private FatPendenciaContaHospDAO fatPendenciaContaHospDAO;

	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;

	@Inject
	private FatPossibilidadeRealizadoDAO fatPossibilidadeRealizadoDAO;

	@Inject
	private FatCaractFinanciamentoDAO fatCaractFinanciamentoDAO;

	@Inject
	private FatCaractComplexidadeDAO fatCaractComplexidadeDAO;

	@Inject
	private FatEspelhoProcedSiscoloDAO fatEspelhoProcedSiscoloDAO;

	@Inject
	private FatMotivoPendenciaDAO fatMotivoPendenciaDAO;

	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;

	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;

	@Inject
	private VFatAssociacaoProcedimento2DAO vFatAssociacaoProcedimento2DAO;

	@Inject
	private CntaConvDAO cntaConvDAO;

	@Inject
	private FatCandidatosApacOtorrinoDAO fatCandidatosApacOtorrinoDAO;

	@Inject
	private FatCidContaHospitalarDAO fatCidContaHospitalarDAO;

	@Inject
	private FatContaApacDAO fatContaApacDAO;

	@Inject
	private AipNacionalidadesDAO aipNacionalidadesDAO; 
	
	@Inject
	private AipEtniaDAO aipEtniaDAO;
	
	@Inject
	private AghCidDAO aghCidDAO;

	@Inject
	private FatConvTipoDocumentosDAO fatConvTipoDocumentosDAO;

	@Inject
	private VFatContasHospPacientesDAO vFatContasHospPacientesDAO;

	@Inject
	private FatContaSugestaoDesdobrDAO fatContaSugestaoDesdobrDAO;

	@Inject
	private FatAtoMedicoAihDAO fatAtoMedicoAihDAO;

	@Inject
	private FatCompetenciaProdDAO fatCompetenciaProdDAO;

	@Inject
	private FatMotivoSaidaPacienteDAO fatMotivoSaidaPacienteDAO;

	@Inject
	private VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO;

	@Inject
	private FatLogErrorDAO fatLogErrorDAO;
	
	@Inject
	private FatDiariaInternacaoDAO fatDiariaInternacaoDAO;

	@Inject
	private FatPacienteTransplantesDAO fatPacienteTransplantesDAO;

	@Inject
	private VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO;

	@Inject
	private FatSubGrupoDAO fatSubGrupoDAO;

	@Inject
	private FatFormaOrganizacaoDAO fatFormaOrganizacaoDAO;

	@Inject
	private FatConvGrupoItensProcedDAO fatConvGrupoItensProcedDAO;

	@Inject
	private FatProcedimentoCboDAO fatProcedimentoCboDAO;

	@Inject
	private FatTipoAtoDAO fatTipoAtoDAO;

	@Inject
	private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;

	@Inject
	private FatMotivoCobrancaApacDAO fatMotivoCobrancaApacDAO;

	@Inject
	private FatDadosContaSemIntDAO fatDadosContaSemIntDAO;

	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;

	@Inject
	private FatCompetenciaCompatibilidDAO fatCompetenciaCompatibilidDAO;

	@Inject
	private FatMensagemLogDAO fatMensagemLogDAO;

	@Inject
	private FatArqEspelhoProcedAmbDAO fatArqEspelhoProcedAmbDAO;

	@Inject
	private VFatAssocProcCidsDAO vFatAssocProcCidsDAO;

	@Inject
	private FatDocumentoCobrancaAihsDAO fatDocumentoCobrancaAihsDAO;

	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;

	@Inject
	private FatProcedimentoHospitalarDAO fatProcedimentoHospitalarDAO;

	@Inject
	private FatTipoTransplanteDAO fatTipoTransplanteDAO;

	@Inject
	private FatTiposVinculoDAO fatTiposVinculoDAO;

	@Inject
	private FatAihDAO fatAihDAO;

	@Inject
	private FatContasInternacaoDAO fatContasInternacaoDAO;

	@Inject
	private VFatConvPlanoGrupoProcedDAO vFatConvPlanoGrupoProcedDAO;

	@Inject
	private FatLogInterfaceDAO fatLogInterfaceDAO;

	@Inject
	private FatConvenioSaudePlanoDAO fatConvenioSaudePlanoDAO;

	@Inject
	private FatPacienteTratamentosDAO fatPacienteTratamentosDAO;

	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;

	@Inject
	private FatMotivoDesdobramentoDAO fatMotivoDesdobramentoDAO;

	@Inject
	private BackupCntaConvDAO backupCntaConvDAO;

	@Inject
	private FatValorContaHospitalarDAO fatValorContaHospitalarDAO;

	@EJB
	private GeracaoArquivoTextoInternacaoInterface GeracaoArquivoTextoInternacaoBean;

	@Inject
	private FatExcCnvGrpItemProcDAO fatExcCnvGrpItemProcDAO;

	@Inject
	private FatConvenioSaudeDAO fatConvenioSaudeDAO;

	@Inject
	private FatPeriodosEmissaoDAO fatPeriodosEmissaoDAO;

	@Inject
	private FatConvPlanoAcomodacoesDAO fatConvPlanoAcomodacoesDAO;
	
	@EJB
	private FatCnesUfRN fatCnesUfRN;
	
	@Inject
	private FatCnesUfDAO fatCnesUfDAO;
	
	@Inject
	private FatMotivoDesdobrSsmDAO fatMotivoDesdobrSsmDAO;

	@EJB
	private FatExcecaoPercentualRN fatExcecaoPercentualRN;
	
	@EJB
	private FatDiariaInternacaoRN fatDiariaInternacaoRN;
	
	@EJB
	private FatValorDiariaInternacaoRN fatValorDiariaInternacaoRN;
	
	@EJB
	private ClinicaPorProcedimentoON clinicaPorProcedimentoON;
	
	@Inject
	private FatLaudosPacApacsDAO fatLaudosPacApacsDAO;

	@Inject
	private FatItemProcHospTranspDAO fatItemProcHospTranspDAO;
	
	@EJB
	private RelatorioLogInconsistenciaCargaRN relatorioLogInconsistenciaCargaRN;

	private static final long serialVersionUID = -3620482238957972208L;


	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private CidContaHospitalarPersist cidContaHospitalarPersist;

	@EJB
	private FatDadosContaSemIntPersist fatDadosContaSemIntPersist;

	@EJB
	private AtoMedicoAihPersist atoMedicoAihPersist;

	@EJB
	private ContaNaoReapresentadaON contaNaoReapresentadaON;
	
	@EJB
	private FatTipoCaractItensRN fatTipoCaractItensRN;
	
	@Inject
	private FatExcecaoPercentualDAO fatExcecaoPercentualDAO;
	
	@EJB
	private FatPossibilidadeRealizadoRN fatPossibilidadeRealizadoRN;
	
	@EJB
	private RelatorioContasApresentadasPorEspecialidadeRN relatorioContasApresentadasPorEspecialidadeRN;
	
	
	@Inject
	private FatServClassificacoesDAO fatServClassificacoesDAO;
	
	@EJB
	private RelatorioContaApresentadaEspMesON relatorioContaApresentadaEspMesON;
	
	@EJB
	private GerarArquivoSmsON gerarArquivoSmsON;
	
	@EJB
	private FatContasHospitalaresON fatContasHospitalaresON;
	
	@Inject
	private FatModalidadeAtendimentoDAO fatModalidadeAtendimentoDAO;

	@Inject
	private ConvDAO convDAO;
	
	@Inject
    private FatProcedimentoRegistroDAO fatProcedimentoRegistroDAO;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	// @Inject
	// private EncerramentoAutomaticoContaHospitalarScheduler
	// encerramentoAutomaticoContaHospitalarScheduler;
	//
	
	protected ContaHospitalarRN getContaHospitalarRN() {
		return contaHospitalarRN;
	}
	
	protected FatProcedimentoRegistroDAO getFatProcedimentoRegistroDAO() {
		return fatProcedimentoRegistroDAO;
	}

	protected void setFatProcedimentoRegistroDAO(FatProcedimentoRegistroDAO fatProcedimentoRegistroDAO) {
		this.fatProcedimentoRegistroDAO = fatProcedimentoRegistroDAO;
	}

	protected FaturamentoFatkCapUniRN getFaturamentoFatkCapUniRN(){
		return faturamentoFatkCapUniRN;
	}


	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}

	protected VFatContaHospitalarPacDAO getVFatContaHospitalarPacDAO() {
		return vFatContaHospitalarPacDAO;
	}

	protected FatDadosContaSemIntDAO getFatDadosContaSemIntDAO() {
		return fatDadosContaSemIntDAO;
	}

	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}

	protected FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}

	protected FatCidContaHospitalarDAO getFatCidContaHospitalarDAO() {
		return fatCidContaHospitalarDAO;
	}

	protected FatCaractFinanciamentoDAO getFatCaractFinanciamentoDAO() {
		return fatCaractFinanciamentoDAO;
	}

	protected FatCaractComplexidadeDAO getFatCaractComplexidadeDAO() {
		return fatCaractComplexidadeDAO;
	}

	protected FatMotivoDesdobramentoDAO getFatMotivoDesdobramentoDAO() {
		return fatMotivoDesdobramentoDAO;
	}

	protected FatMotivoDesdobrClinicaDAO getFatMotivoDesdobrClinicaDAO() {
		return fatMotivoDesdobrClinicaDAO;
	}

	protected FatExcecaoPercentualDAO getFatExcecaoPercentualDAO() {
		return fatExcecaoPercentualDAO;
	}
    
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void persistirMotivoDesdobramento(FatMotivoDesdobramento fatMotivoDesdobramento) {		
		faturamentoRN.persistirMotivoDesdobramento(fatMotivoDesdobramento);		
	}

	@Override
	public List<AghClinicas> pesquisarClinicasPorCodigoOuDescricao(
			Object parametro) {
		return aghClinicasDAO.pesquisarClinicasPorCodigoOuDescricaoSuggestionBox(parametro,
				true, false);
	}

	@Override
	public Long pesquisarClinicasPorCodigoOuDescricaoCount(Object parametro) {
		return aghClinicasDAO
				.pesquisarClinicasPorCodigoOuDescricaoCount(parametro);
	}
	
	@Override
	public List<FatItensProcedHospitalar> pesquisarProcedimentosPorTabelaOuItemOuProcedimentoOuDescricao(Object parametro) {		
		return fatItensProcedHospitalarDAO.pesquisarPorTabelaOuItemOuProcedimentoOuDescricao(parametro);
	}

	@Override
	public Long pesquisarProcedimentosPorTabelaOuItemOuProcedimentoOuDescricaoCount(Object parametro) {
		return fatItensProcedHospitalarDAO.pesquisarPorTabelaOuItemOuProcedimentoOuDescricaoCount(parametro);
	}

	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void persistirMotivoDesdobramentoClinica(FatMotivoDesdobrClinica fatMotivoDesdobrClinica) throws ApplicationBusinessException {		
		faturamentoRN.persistirMotivoDesdobramentoClinica(fatMotivoDesdobrClinica);	
	}

	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void persistirMotivoDesdobramentoProcedimento(FatMotivoDesdobrSsm fatMotivoDesdobrSsm) throws ApplicationBusinessException {
		faturamentoRN.persistirMotivoDesdobramentoProcedimento(fatMotivoDesdobrSsm);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void alterarMotivoDesdobramentoSSM(FatMotivoDesdobrSsm fatMotivoDesdobrSsm) {
		faturamentoRN.alterarMotivoDesdobramentoSSM(fatMotivoDesdobrSsm);
	}
	
	@Override
	public List<AghClinicas> pesquisarClinicasPorMotivoDesdobramento(
			Short codigoMotivoDesdobramento) {
		return aghClinicasDAO
				.pesquisarClinicasPorMotivoDesdobramento(codigoMotivoDesdobramento);
	}
	
	@Override
	public List<FatMotivoDesdobrSsm> pesquisarMotivosDesdobramentosSSM(Short seqMotivoDesdobramento) {
		return fatMotivoDesdobrSsmDAO.pesquisarMotivosDesdobramentoSSM(seqMotivoDesdobramento);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void excluirClinicaMotivoDesdobramento(FatMotivoDesdobrClinicaId fatMotivoDesdobrClinicaId) {
		fatMotivoDesdobrClinicaDAO.removerPorId(fatMotivoDesdobrClinicaId);		
	}

	@Override
	public void removerCidContaHospitalar(final Integer cthSeq,
			final Integer cidSeq, final DominioPrioridadeCid prioridadeCid,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		getCidContaHospitalarON().removerCidContaHospitalar(cthSeq, cidSeq,
				prioridadeCid, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public VFatContaHospitalarPac obterContaHospitalarPaciente(
			final Integer seq, final Enum[] fetchArgsLeftJoin) {
		return getVFatContaHospitalarPacDAO().obterPorChavePrimaria(seq, null,
				fetchArgsLeftJoin);
	}

	@Override
	public VFatContaHospitalarPac obterContaHospitalarPaciente(Integer seq) {
		return getVFatContaHospitalarPacDAO().obterContaHospitalarPaciente(seq);
	}

	@Override
	@BypassInactiveModule
	public FatContasHospitalares obterContaHospitalar(final Integer seq,
			Enum... fetchArgs) {
		return getFatContasHospitalaresDAO().obterPorChavePrimaria(seq, true,
				fetchArgs);
	}

	@Override
	public FatContasHospitalares buscarCthGerada(final Integer cthSeq) {
		return getFatContasHospitalaresDAO().buscarCthGerada(cthSeq);
	}

	@Override
	public List<FatItemContaHospitalar> listarIchGerada(final Integer cthSeq) {
		return getFatItemContaHospitalarDAO().listarIchGerada(cthSeq);
	}

	@Override
	public List<FatItemContaHospitalar> listarIchDesdobrada(final Integer cthSeq) {
		return getFatItemContaHospitalarDAO().listarIchDesdobrada(cthSeq);
	}
	
	@Override
	public FatItemContaHospitalar obterItemContaHospitalarLazyPorId(final FatItemContaHospitalarId id) {
		return getFatItemContaHospitalarDAO().obterItemContaHospitalarLazyPorId(id);
	}
	
	@Override
	public FatItemContaHospitalar obterItemContaHospitalar(
			final FatItemContaHospitalarId id) {
		final FatItemContaHospitalar result = getFatItemContaHospitalarDAO()
				.obterPorChavePrimaria(
						id,
						true,
						FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO,
						FatItemContaHospitalar.Fields.CONTA_HOSPITALAR,
						FatItemContaHospitalar.Fields.UNIDADE_FUNCIONAL,
						FatItemContaHospitalar.Fields.SERVIDOR,
						FatItemContaHospitalar.Fields.SERVIDOR_ANEST);
		getFatItemContaHospitalarDAO().refresh(result);
		return result;
	}
	
	@Override
	public List<FatItemContaHospitalar> listarItensContaHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer cthSeq, final Integer procedimentoSeq,
			final Short unfSeq, final Date dtRealizado,
			final DominioSituacaoItenConta situacao,
			final DominioIndOrigemItemContaHospitalar origem,
			final Boolean removerFiltro, AghParametros grupoSUS)
			throws BaseException {

		return getFatItemContaHospitalarDAO().listarItensContaHospitalar(
				firstResult, maxResult, orderProperty, asc, cthSeq,
				procedimentoSeq, unfSeq, dtRealizado, situacao, origem,
				removerFiltro, grupoSUS);
	}

	@Override
	public List<FatItemContaHospitalar> listarItensContaHospitalar(final Integer cthSeq, final Integer procedimentoSeq,	final Short unfSeq, final Date dtRealizado,
			final DominioSituacaoItenConta situacao,
			final DominioIndOrigemItemContaHospitalar origem,
			final Boolean removerFiltro, AghParametros grupoSUS)
			throws BaseException {

		return getFatItemContaHospitalarDAO().listarItensContaHospitalar(cthSeq, procedimentoSeq, unfSeq, dtRealizado, situacao, origem, removerFiltro, grupoSUS);
	}

	@Override
	public Long listarItensContaHospitalarCount(final Integer cthSeq,
			final Integer procedimentoSeq, final Short unfSeq,
			final Date dtRealizado, final DominioSituacaoItenConta situacao,
			final DominioIndOrigemItemContaHospitalar origem,
			final Boolean removerFiltro, AghParametros grupoSUS)
			throws BaseException {
		return getFatItemContaHospitalarDAO().listarItensContaHospitalarCount(
				cthSeq, procedimentoSeq, unfSeq, dtRealizado, situacao, origem,
				removerFiltro, grupoSUS);
	}

	@Override
	@BypassInactiveModule
	public List<FatProcedHospInternos> pesquisarFatProcedHospInternos(
			final Integer codigoMaterial,
			final Integer seqProcedimentoCirurgico,
			final Short seqProcedimentoEspecialDiverso) {
		return getFaturamentoON().pesquisarFatProcedHospInternos(
				codigoMaterial, seqProcedimentoCirurgico,
				seqProcedimentoEspecialDiverso);
	}

	@Override
	@BypassInactiveModule
	public List<SubRelatorioLaudosProcSusVO> buscarJustificativasLaudoProcedimentoSUS(
			final Integer seqAtendimento) throws ApplicationBusinessException {
		return getFaturamentoON().buscarJustificativasLaudoProcedimentoSUS(
				seqAtendimento);
	}
	
	@Override
	@BypassInactiveModule
	public List<SubRelatorioMudancaProcedimentosVO> buscarMudancaProcedimentos(Integer seqAtendimento, Integer apaSeq, Short seqp) {
		return getFaturamentoON().buscarMudancaProcedimentos(seqAtendimento, apaSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public void removerContaHospitalar(final FatContasHospitalares newCtaHosp,
			final FatContasHospitalares oldCtaHosp,
			final Date dataFimVinculoServidor) throws BaseException {
		this.getContaHospitalarON().removerContaHospitalar(newCtaHosp,
				oldCtaHosp, dataFimVinculoServidor);
	}

	@Override
	public FatContasHospitalares persistirContaHospitalar(
			final FatContasHospitalares newCtaHosp,
			final FatContasHospitalares oldCtaHosp, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return getContaHospitalarON().persistirContaHospitalar(newCtaHosp,
				oldCtaHosp, Boolean.TRUE, nomeMicrocomputador,
				servidorLogado, dataFimVinculoServidor);
	}

	@Override
	public void persistirContaHospitalar(
			final FatContasHospitalares newCtaHosp,
			final FatContasHospitalares oldCtaHosp, final Boolean flush,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.getContaHospitalarON().persistirContaHospitalar(newCtaHosp,
				oldCtaHosp, flush, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
	}

	@Override
	public List<FatPeriodosEmissao> pesquisarPeriodosEmissaoConvenioSaudePlano(
			FatConvenioSaudePlano convenioSaudePlano) {
		return getFatPeriodosEmissaoDAO()
				.pesquisarPeriodosEmissaoConvenioSaudePlano(convenioSaudePlano);
	}

	@Override
	public void persistirContaHospitalar(
			final FatContasHospitalares newCtaHosp,
			final FatContasHospitalares oldCtaHosp, final Boolean flush,
			final Boolean alterarEspecialidade, String nomeMicrocomputador, RapServidores servidorLogado,
			final Date dataFimVinculoServidor) throws BaseException {
		this.getContaHospitalarON().persistirContaHospitalar(newCtaHosp,
				oldCtaHosp, flush, alterarEspecialidade, nomeMicrocomputador, servidorLogado,
				dataFimVinculoServidor);
	}

	@Override
	public FatProcedHospInternos obterProcedimentoHospitalarInterno(
			final Integer seq) {
		return getFatProcedHospInternosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void buscaCountQtdCids(final Integer cthSeq, final Integer cidSeq)
			throws ApplicationBusinessException {

		getCidContaHospitalarON().buscaCountQtdCids(cthSeq, cidSeq);
	}

	@Override
	public String obterMensagemResourceBundle(String key) {
		return getProcedimentoCboON().obterMensagemResourceBundle(key);
	}

	@Override
	public void inserirFatProcedHospInternos(
			final FatProcedHospInternos fatProcedHospInternos)
			throws ApplicationBusinessException {
		getManterFatProcedHospInternosRN().inserirFatProcedHospInternos(
				fatProcedHospInternos);
	}

	@Override
	public List<FatProcedHospInternos> listarFatProcedHospInternosPorMaterial(
			final ScoMaterial material) {
		return getFatProcedHospInternosDAO()
				.listarFatProcedHospInternosPorMaterial(material);
	}

	@Override
	public List<FatProcedHospInternos> listarFatProcedHospInternosPorProcedimentoCirurgicos(
			final MbcProcedimentoCirurgicos procedimentoCirurgico) {
		return getFatProcedHospInternosDAO()
				.listarFatProcedHospInternosPorProcedimentoCirurgicos(
						procedimentoCirurgico.getSeq());
	}
	
	public List<FatProcedHospInternos> listarFatProcedHospInternosPorProcedimentoCirurgicos(Integer pciSeq, String desc){
		return getFatProcedHospInternosDAO().listarFatProcedHospInternosPorProcedDesc(pciSeq, desc);
	}

	@Override
	public FatConvenioSaudePlano obterConvenioSaudePlanoPeloAtendimentoDaConsulta(
			Integer atdSeq) {
		return getFatConvenioSaudePlanoDAO()
				.obterConvenioSaudePlanoPeloAtendimentoDaConsulta(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public List<FatProcedHospInternos> listarFatProcedHospInternosPorProcedEspecialDiversos(
			final MpmProcedEspecialDiversos procedimentoEspecialDiverso) {
		return getFatProcedHospInternosDAO()
				.listarFatProcedHospInternosPorProcedEspecialDiversos(
						procedimentoEspecialDiverso);
	}

	@Override
	@BypassInactiveModule
	public List<String> buscaProcedimentoHospitalarInternoAgrupa(
			final Integer phiSeq, final Short cnvCodigo, final Byte cspSeq,
			final Short phoSeq, final Short tipoGrupoContaSUS) {
		return getFatConvGrupoItensProcedDAO()
				.buscaProcedimentoHospitalarInternoAgrupa(phiSeq, cnvCodigo,
						cspSeq, phoSeq, tipoGrupoContaSUS);
	}

	@Override
	@BypassInactiveModule
	public List<Long> buscaDescricaoProcedimentoHospitalarInterno(
			final Integer phiSeq, final Short cnvCodigo, final Byte cspSeq,
			final Short phoSeq, final Short tipoGrupoContaSUS) {
		return getFatConvGrupoItensProcedDAO()
				.buscaDescricaoProcedimentoHospitalarInterno(phiSeq, cnvCodigo,
						cspSeq, phoSeq, tipoGrupoContaSUS);
	}

	@Override
	@BypassInactiveModule
	public Boolean verificarFatConvGrupoItensProcedExigeJustificativa(
			final FatProcedHospInternos fatProcedHospInternos,
			final FatConvenioSaudePlano convenioSaudePlano) {

		return this.getFatConvGrupoItensProcedDAO()
				.verificarFatConvGrupoItensProcedExigeJustificativa(
						fatProcedHospInternos, convenioSaudePlano);

	}

	@Override
	public List<FatConvGrupoItemProced> listarFatConvGrupoItensProced(
			final Short pIphPhoSeq, final Integer pIphSeq,
			final Short pCnvCodigo, final Byte pCnvCspSeq,
			final Short pCpgCphPhoSeq, final Short pCpgGrcSeq,
			final Integer phiSeq) {
		return getFatConvGrupoItensProcedDAO().listarFatConvGrupoItensProced(
				pIphPhoSeq, pIphSeq, pCnvCodigo, pCnvCspSeq, pCpgCphPhoSeq,
				pCpgGrcSeq, phiSeq);
	}

	@Override
	public List<FatConvGrupoItemProced> listarFatConvGrupoItensProcedPorPhi(
			final Short pIphPhoSeq, final Integer pIphSeq,
			final Short pCnvCodigo, final Byte pCnvCspSeq,
			final Short pCpgCphPhoSeq, final Short pCpgGrcSeq,
			final Integer phiSeq) {
		return getFatConvGrupoItensProcedDAO().listarFatConvGrupoItensProced(
				pIphPhoSeq, pIphSeq, pCnvCodigo, pCnvCspSeq, pCpgCphPhoSeq,
				pCpgGrcSeq, phiSeq);
	}

	@Override
	public FatTipoAih obterTipoAih(final Byte seq) {
		return getFatTipoAihDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSSolicitado(
			final Integer phiSeq) throws BaseException {
		return getContaHospitalarON()
				.listarAssociacaoProcedimentoSUSSolicitado(null, phiSeq);
	}

	@Override
	public List<FatConvTipoDocumentos> pesquisarConvTipoDocumentoConvenioSaudePlano(
			FatConvenioSaudePlano convenioSaudePlano) {
		return this.getFatConvTipoDocumentosDAO()
				.pesquisarConvTipoDocumentoConvenioSaudePlano(
						convenioSaudePlano);
	}

	@Override
	public List<FatConvPlanoAcomodacoes> pesquisarConvPlanoAcomodocaoConvenioSaudePlano(
			FatConvenioSaudePlano convenioSaudePlano) {
		return this.getFatConvPlanoAcomodacoesDAO()
				.pesquisarConvPlanoAcomodocaoConvenioSaudePlano(
						convenioSaudePlano);
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSRealizado(
			final Integer phiSeq, final Integer cthSeq) throws BaseException {
		return getContaHospitalarON().listarAssociacaoProcedimentoSUSRealizado(
				null, phiSeq, cthSeq);
	}

	@Override
	public String buscaSitSms(final Integer pCthSeq) {
		return getContaHospitalarON().buscaSitSms(pCthSeq);
	}

	@Override
	public List<FatCidContaHospitalar> pesquisarCidContaHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer cthSeq) {
		return getFatCidContaHospitalarDAO().pesquisarCidContaHospitalar(
				firstResult, maxResult, orderProperty, asc, cthSeq);
	}

	@Override
	@BypassInactiveModule
	public List<FatCidContaHospitalar> pesquisarCidContaHospitalar(
			final Integer cthSeq) {
		return getFatCidContaHospitalarDAO()
				.pesquisarCidContaHospitalar(cthSeq);
	}

	@Override
	public Long pesquisarCidContaHospitalarCount(final Integer cthSeq) {
		return getFatCidContaHospitalarDAO().pesquisarCidContaHospitalarCount(
				cthSeq);
	}

	@Override
	public void validarConvenioPlanoAcomodacaoAntesInserir(
			final FatConvPlanoAcomodacoes convPlanoAcomodacao)
			throws ApplicationBusinessException {
		getFaturamentoRN().validarConvenioPlanoAcomodacaoAntesInserir(
				convPlanoAcomodacao);
	}

	@Override
	public void validarConvenioTipoDocumentoAntesInserir(
			final FatConvTipoDocumentos convTipoDocumento)
			throws ApplicationBusinessException {
		getFaturamentoRN().validarConvenioTipoDocumentoAntesInserir(
				convTipoDocumento);
	}

	@Override
	@Secure("#{s:hasPermission('contaHospitalar','pesquisar')}")
	@BypassInactiveModule
	public List<FatConvGrupoItemProced> pesquisarConvenioGrupoItemProcedimento(
			final Integer iphSeq, final Short iphPhoSeq)
			throws ApplicationBusinessException {
		return getConvenioGrupoItemProcedimentoON()
				.pesquisarConvenioGrupoItemProcedimento(iphSeq, iphPhoSeq);
	}

	@Override
	public Boolean existeFaturamentoComPagador(AacPagador aacPagador) {
		return this.getFatConvenioSaudeDAO().existeFaturamentoComPagador(
				aacPagador);
	}

	@Override
	@Secure("#{s:hasPermission('contaHospitalar','alterar')}")
	public void persistirCidContaHospitalar(
			final FatCidContaHospitalar cidContaHospitalar,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		getFaturamentoON().persistirCidContaHospitalar(cidContaHospitalar,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	@Secure("#{s:hasPermission('contaHospitalar','alterar')}")
	public void inserirCidContaHospitalar(
			final FatCidContaHospitalar cidContaHospitalar,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		getCidContaHospitalarPersist().inserir(cidContaHospitalar,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	@Secure("#{s:hasPermission('contaHospitalar','alterar')}")
	public void inserirContaInternacao(
			final FatContasInternacao contaInternacao,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		getFaturamentoON().inserirContaInternacao(contaInternacao,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	@Secure("#{s:hasPermission('contaHospitalar','alterar')}")
	public void inserirContaInternacaoConvenio(
			final FatContasInternacao contaInternacao,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		if(aghuFacade.isHCPA()){
			getFatContaInternacaoRN().asiPosInsercaoStatement(contaInternacao,
					nomeMicrocomputador, dataFimVinculoServidor);
		}
		inserirContaInternacao(contaInternacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	
	@Override
	public List<FatProcedHospIntCid> listarFatProcedHospIntCidPorPhiSeqValidade(
			Integer phiSeq, DominioTipoPlano validade, String filtro) {
		return getFatProcedHospIntCidDAO()
				.listarFatProcedHospIntCidPorPhiSeqValidade(phiSeq, validade,
						filtro);
	}

	@Override
	public void agendarEncerramentoAutomaticoContaHospitalar(
			Date date, final String cron, final String nomeMicrocomputador, final RapServidores servidorLogado, final String nomeProcessoQuartz) {
		encerramentoAutomaticoContaHospitalarON.agendarEncerramentoAutomaticoContaHospitalar(date, cron, nomeMicrocomputador, servidorLogado, nomeProcessoQuartz);
	}

	@Override
	public void buscarSangue(final Integer seqContaInternacao)
			throws ApplicationBusinessException {
		getFatContaInternacaoRN().buscarSangue(seqContaInternacao);
	}

	@Override
	public void incluiMateriais(final Integer cthSeq,
			Date dataFimVinculoServidor) throws BaseException {
		getFatkCth4RN().incluiMateriais(cthSeq, dataFimVinculoServidor);
	}

	protected FatkCth4RN getFatkCth4RN() {
		return fatkCth4RN;
	}

	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('contaHospitalar','pesquisar')}")
	public FatTipoAih obterItemAihPorCodigoSus(final Short codigoSus) {
		return getFatTipoAihDAO().obterItemAihPorCodigoSus(codigoSus);
	}

	@Override
	@Secure("#{s:hasPermission('contaHospitalar','pesquisar')}")
	@BypassInactiveModule
	public List<FatContasInternacao> obterContaInternacaoNaoManuseada(
			final Integer seqInternacao) {
		return getFatContasInternacaoDAO().obterContaInternacaoNaoManuseada(
				seqInternacao);
	}

	@Override
	@Secure("#{s:hasPermission('contaHospitalar','pesquisar')}")
	@BypassInactiveModule
	public List<FatContasInternacao> pesquisarContaInternacaoPorInternacao(
			final Integer seqInternacao) {
		return getFatContasInternacaoDAO()
				.pesquisarContaInternacaoPorInternacao(seqInternacao);
	}

	@Override
	@Secure("#{s:hasPermission('contaHospitalar','pesquisar')}")
	@BypassInactiveModule
	public FatItensProcedHospitalar obterItemProcedHospitalar(
			final Short phoSeq, final Integer seq) {
		return getFatItensProcedHospitalarDAO().obterItemProcedHospitalar(
				phoSeq, seq);
	}

	private FatConvGrupoItensProcedDAO getFatConvGrupoItensProcedDAO() {
		return fatConvGrupoItensProcedDAO;
	}

	private FatTipoAihDAO getFatTipoAihDAO() {
		return fatTipoAihDAO;
	}

	protected ContaHospitalarON getContaHospitalarON() {
		return contaHospitalarON;
	}

	protected EncerramentoContaHospitalarON getEncerramentoContaHospitalarON() {
		return encerramentoContaHospitalarON;
	}

	protected ManterFatProcedHospInternosRN getManterFatProcedHospInternosRN() {
		return manterFatProcedHospInternosRN;
	}

	public FatExcecaoPercentualRN getFatExcecaoPercentualRN() {
		return fatExcecaoPercentualRN;
	}

	@Override
	@BypassInactiveModule
	public void atualizarFatProcedHospInternos(
			final FatProcedHospInternos fatProcedHospInternos)
			throws ApplicationBusinessException {
		getManterFatProcedHospInternosRN().atualizarFatProcedHospInternos(
				fatProcedHospInternos);
	}

	@Override
	public void deleteFatProcedHospInternos(final ScoMaterial matCodigo,
			final MbcProcedimentoCirurgicos pciSeq,
			final MpmProcedEspecialDiversos pedSeq, final String csaCodigo,
			final String pheCodigo, final Short euuSeq, final Integer cduSeq,
			final Short cuiSeq, final Integer tidSeq) {
		getManterFatProcedHospInternosRN().deleteFatProcedHospInternos(
				matCodigo, pciSeq, pedSeq, csaCodigo, pheCodigo, euuSeq,
				cduSeq, cuiSeq, tidSeq);
	}

	@Override
	public void deleteFatProcedHospInternos(final ScoMaterial matCodigo,
			final MbcProcedimentoCirurgicos pciSeq,
			final MpmProcedEspecialDiversos pedSeq, final String csaCodigo,
			final String pheCodigo, final Short euuSeq, final Integer cduSeq,
			final Short cuiSeq, final Integer tidSeq, Boolean flush) {
		getManterFatProcedHospInternosRN().deleteFatProcedHospInternos(
				matCodigo, pciSeq, pedSeq, csaCodigo, pheCodigo, euuSeq,
				cduSeq, cuiSeq, tidSeq, flush);
	}

	@Override
	public List<VFatContaHospitalarPac> pesquisarAbertaFechadaEncerrada(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq) {

		return getVFatContaHospitalarPacDAO().pesquisarAbertaFechadaEncerrada(
				firstResult, maxResult, orderProperty, asc, pacProntuario,
				cthNroAih, pacCodigo, cthSeq);
	}

	@Override
	public List<VFatContaHospitalarPac> pesquisarAbertaOuFechadaOrdenadaPorDtIntAdm(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer pacProntuario, final Integer cthSeq,
			final Integer codigo, final DominioSituacaoConta situacao) {

		return getVFatContaHospitalarPacDAO()
				.pesquisarAbertaOuFechadaOrdenadaPorDtIntAdm(firstResult,
						maxResult, orderProperty, asc, pacProntuario, cthSeq,
						codigo, situacao);
	}

	@Override
	public  List<FatContasHospitalares> pesquisaContaHospitalarParaNotaConsumoDaCirurgia(
			Integer seqAtendimento) {
		return getVFatContaHospitalarPacDAO()
				.pesquisaContaHospitalarParaNotaConsumoDaCirurgia(
						seqAtendimento);
	}

	@Override
	public Long pesquisarAbertaOuFechadaCount(final Integer pacProntuario,
			final Integer cthSeq, final Integer codigo,
			final DominioSituacaoConta situacao) {

		return getVFatContaHospitalarPacDAO().pesquisarAbertaOuFechadaCount(
				pacProntuario, cthSeq, codigo, situacao);
	}

	@Override
	public List<VFatContaHospitalarPac> pesquisarPorPacCodigo(
			final Integer pacCodigo) {
		return getVFatContaHospitalarPacDAO().pesquisarPorPacCodigo(pacCodigo);
	}

	@Override
	public Long pesquisarPorPacCodigoCount(final Integer pacCodigo) {
		return getVFatContaHospitalarPacDAO().pesquisarPorPacCodigoCount(
				pacCodigo);
	}

	@Override
	public Long pesquisarAbertaFechadaEncerradaCount(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq) {
		return getVFatContaHospitalarPacDAO()
				.pesquisarAbertaFechadaEncerradaCount(pacProntuario, cthNroAih,
						pacCodigo, cthSeq);
	}

	@Override
	public AipPacientes pesquisarAbertaFechadaEncerradaPaciente(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq) {
		return getVFatContaHospitalarPacDAO()
				.pesquisarAbertaFechadaEncerradaPaciente(pacProntuario,
						cthNroAih, pacCodigo, cthSeq);
	}

	@Override
	public String situacaoSMS(final FatContasHospitalares contaHospitalar) {
		return faturamentoInternacaoRN.situacaoSMS(contaHospitalar);
	}

	@Override
	public String buscaLeito(final FatContasHospitalares contaHospitalar) {
		return getFaturamentoON().buscaLeito(contaHospitalar);
	}

	@Override
	public String buscaSSM(final Integer pCthSeq, final Short pCspCnvCodigo,
			final Byte pCspSeq, final DominioSituacaoSSM situacaoSSM) {
		return this.faturamentoInternacaoRN.buscaSSM(pCthSeq, pCspCnvCodigo, pCspSeq,
				situacaoSSM);
	}

	@Override
	public String buscaSsmComplexidade(final Integer pCthSeq,
			final Short pCspCnvCodigo, final Byte pCspSeq,
			final Integer pCthPhiSeq, final Integer pCthPhiSeqRealizado,
			final DominioSituacaoSSM situacaoSSM) {
		return faturamentoInternacaoRN.buscaSsmComplexidade(pCthSeq, pCspCnvCodigo,
				pCspSeq, pCthPhiSeq, pCthPhiSeqRealizado, situacaoSSM);
	}

	@Override
	public List<VFatContaHospitalarPac> pesquisarContaHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer prontuario, final Integer contaHospitalar,
			final String codigoDcih, final Long numeroAih,
			final Date competencia, final Integer codigo,
			final DominioSituacaoConta[] situacoes) {

		return getVFatContaHospitalarPacDAO().pesquisarContaHospitalar(
				firstResult, maxResult, orderProperty, asc, prontuario,
				contaHospitalar, codigoDcih, numeroAih, competencia, codigo,
				situacoes);
	}

	@Override
	public Long pesquisarContaHospitalarCount(final Integer prontuario,
			final Integer contaHospitalar, final String codigoDcih,
			final Long numeroAih, final Date competencia, final Integer codigo) {

		return getVFatContaHospitalarPacDAO().pesquisarContaHospitalarCount(
				prontuario, contaHospitalar, codigoDcih, numeroAih,
				competencia, codigo, null);
	}

	@Override
	public Long pesquisarContaHospitalarCount(final Integer prontuario,
			final Integer contaHospitalar, final String codigoDcih,
			final Long numeroAih, final Date competencia, final Integer codigo,
			final DominioSituacaoConta[] situacoes) {

		return getVFatContaHospitalarPacDAO().pesquisarContaHospitalarCount(
				prontuario, contaHospitalar, codigoDcih, numeroAih,
				competencia, codigo, situacoes);
	}

	@Override
	public void removerItemContaHospitalar(
			final FatItemContaHospitalar itemCtaHosp) throws BaseException {
		getItemContaHospitalarON().removerContaHospitalar(itemCtaHosp, null);
	}

	@Override
	public void persistirItemContaHospitalar(
			final FatItemContaHospitalar newItemCtaHosp,
			final FatItemContaHospitalar oldItemCtaHosp, RapServidores servidorLogado,
			final Date dataFimVinculoServidor) throws BaseException {
		getItemContaHospitalarON().persistirItemContaHospitalar(newItemCtaHosp,
				oldItemCtaHosp, servidorLogado, dataFimVinculoServidor);
	}

	@Override
	public void persistirItemContaHospitalar(
			final FatItemContaHospitalar newItemCtaHosp,
			final FatItemContaHospitalar oldItemCtaHosp, final boolean flush,
			RapServidores servidorLogado,
			final Date dataFimVinculoServidor) throws BaseException {
		getItemContaHospitalarON().persistirItemContaHospitalar(newItemCtaHosp,
				oldItemCtaHosp, flush,servidorLogado, dataFimVinculoServidor);
	}

	@Override
	public List<AghCid> pesquisarCidsPorDescricaoOuCodigo(
			final String descricao, final Integer limiteRegistros) {
		return getCidContaHospitalarON().pesquisarCidsPorDescricaoOuCodigo(
				descricao, limiteRegistros);
	}

	@Override
	public List<AghCid> pesquisarCidsPorDescricaoOuCodigo(
			final String descricao, final Integer limiteRegistros,
			final String order) {
		return getFatCidContaHospitalarDAO().pesquisarCidsPorDescricaoOuCodigo(
				descricao, limiteRegistros, order);
	}

	@Override
	public Long pesquisarCidsPorDescricaoOuCodigoCount(final String descricao) {
		return getCidContaHospitalarON()
				.pesquisarCidsPorDescricaoOuCodigoCount(descricao);
	}

	protected FatMotivoSaidaPacienteDAO getFatMotivoSaidaPacienteDAO() {
		return fatMotivoSaidaPacienteDAO;
	}

	protected FatSituacaoSaidaPacienteDAO getFatSituacaoSaidaPacienteDAO() {
		return fatSituacaoSaidaPacienteDAO;
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSSolicitado(
			final Object objPesquisa, final Integer phiSeq)
			throws BaseException {
		return getContaHospitalarON()
				.listarAssociacaoProcedimentoSUSSolicitado(objPesquisa, phiSeq);
	}

	@Override
	public Long listarAssociacaoProcedimentoSUSSolicitadoCount(
			final Object objPesquisa) throws BaseException {
		return getContaHospitalarON()
				.listarAssociacaoProcedimentoSUSSolicitadoCount(objPesquisa);
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSSolicitadoPorPHI(
			final Object objPesquisa, final Integer phiSeq)
			throws BaseException {
		return getContaHospitalarON()
				.listarAssociacaoProcedimentoSUSSolicitadoPorPHI(objPesquisa,
						phiSeq);
	}

	@Override
	public Long listarAssociacaoProcedimentoSUSSolicitadoPorPHICount(
			final Object objPesquisa) throws BaseException {
		return getContaHospitalarON()
				.listarAssociacaoProcedimentoSUSSolicitadoPorPHICount(
						objPesquisa);
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSRealizado(
			final Object objPesquisa, final Integer phiSeq, final Integer cthSeq)
			throws BaseException {
		return getContaHospitalarON().listarAssociacaoProcedimentoSUSRealizado(
				objPesquisa, phiSeq, cthSeq);
	}

	@Override
	public Long listarAssociacaoProcedimentoSUSRealizadoCount(
			final Object objPesquisa, final Integer phiSeq, final Integer cthSeq)
			throws BaseException {
		return getContaHospitalarON()
				.listarAssociacaoProcedimentoSUSRealizadoCount(objPesquisa,
						phiSeq, cthSeq);
	}

	@Override
	public List<FatSituacaoSaidaPaciente> listarSituacaoSaidaPaciente(
			final Object objPesquisa, final Short mspSeq) {
		return getFatSituacaoSaidaPacienteDAO().listarSituacaoSaidaPaciente(
				objPesquisa, mspSeq);
	}

	@Override
	public Long listarSituacaoSaidaPacienteCount(final Object objPesquisa,
			final Short mspSeq) {
		return getFatSituacaoSaidaPacienteDAO()
				.listarSituacaoSaidaPacienteCount(objPesquisa, mspSeq);
	}

	@Override
	public List<FatMotivoSaidaPaciente> listarMotivoSaidaPaciente(
			final Object objPesquisa) {
		return getFatMotivoSaidaPacienteDAO().listarMotivoSaidaPaciente(
				objPesquisa);
	}

	@Override
	public Long listarMotivoSaidaPacienteCount(final Object objPesquisa) {
		return getFatMotivoSaidaPacienteDAO().listarMotivoSaidaPacienteCount(
				objPesquisa);
	}

	@Override
	public FatMotivoSaidaPaciente obterMotivoSaidaPacientePorChavePrimaria(
			Short seq) {
		return getFatMotivoSaidaPacienteDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public FatContasHospitalares clonarContaHospitalar(
			final FatContasHospitalares contaHospitalar) throws Exception {
		return this.getContaHospitalarON().clonarContaHospitalar(
				contaHospitalar);
	}

	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public FatItemContaHospitalar clonarItemContaHospitalar(
			final FatItemContaHospitalar itemContaHospitalar) throws Exception {
		return this.getItemContaHospitalarON().clonarItemContaHospitalar(
				itemContaHospitalar);
	}

	@Override
	@BypassInactiveModule
	public boolean verificaDiariaAcompanhante(
			final FatItensProcedHospitalar itemProcedHospitalar) {
		return getItensProcedHospitalarRN().verificaDiariaAcompanhante(
				itemProcedHospitalar);
	}

	@Override
	public void persistirItemProcedimentoHospitalarComFlush(
			final FatItensProcedHospitalar newIph,
			final FatItensProcedHospitalar oldIph) throws BaseException {
		this.getItemProcedimentoHospitalarON()
				.persistirItemProcedimentoHospitalarComFlush(newIph, oldIph);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * atualizarItemProcedimentoHospitalar
	 * (br.gov.mec.aghu.model.FatItensProcedHospitalar,
	 * br.gov.mec.aghu.model.FatItensProcedHospitalar)
	 */
	@Override
	public void atualizarItemProcedimentoHospitalar(
			FatItensProcedHospitalar newIph, FatItensProcedHospitalar oldIph,
			RapServidores servidorLogado) throws BaseException {
		this.getItemProcedimentoHospitalarON()
				.atualizarItemProcedimentoHospitalar(newIph, oldIph);
	}

	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public FatItensProcedHospitalar clonarItemProcedimentoHospitalar(
			final FatItensProcedHospitalar iph) throws Exception {
		return this.getItemProcedimentoHospitalarON()
				.clonarItemProcedimentoHospitalar(iph);
	}

	@Override
	public List<FatCaractItemProcHosp> listarCaractItemProcHospPorSeqEPhoSeq(
			final Short iphPhoSeq, final Integer iphSeq) {
		return this.getFatCaractItemProcHospDAO()
				.listarCaractItemProcHospPorSeqEPhoSeq(iphPhoSeq, iphSeq);
	}

	@Override
	public List<FatCaractItemProcHosp> listarCaractItemProcHospPorPhoSeqECodTabela(
			final Short iphPhoSeq, final Long codTabela) {
		return this.getFatCaractItemProcHospDAO()
				.listarCaractItemProcHospPorPhoSeqECodTabela(iphPhoSeq,
						codTabela);
	}

	@Override
	public List<FatTipoCaractItens> listarTiposCaracteristicasParaItens(
			final Object objPesquisa) {
		return this.getFatTipoCaractItensDAO()
				.listarTiposCaracteristicasParaItens(objPesquisa);
	}

	@Override
	public Long listarTiposCaracteristicasParaItensCount(
			final Object objPesquisa) {
		return this.getFatTipoCaractItensDAO()
				.listarTiposCaracteristicasParaItensCount(objPesquisa);

	}

	@Override
	public FatTipoCaractItens obterTipoCaracteristicaItemPorChavePrimaria(
			final Integer tctSeq) {
		return this.getFatTipoCaractItensDAO().obterPorChavePrimaria(tctSeq);
	}

	@Override
	@BypassInactiveModule
	public List<FatTipoCaractItens> listarTipoCaractItensPorCaracteristica(
			final String caracteristica) {
		return this.getFatTipoCaractItensDAO()
				.listarTipoCaractItensPorCaracteristica(caracteristica);
	}

	@Override
	@BypassInactiveModule
    // correção issue #44881
	public FatItensProcedHospitalar obterItemProcedHospitalarPorChavePrimaria(
			final FatItensProcedHospitalarId id) {
		return this.getFatItensProcedHospitalarDAO().obterPorChavePrimaria(id,
				true, FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR,
				FatItensProcedHospitalar.Fields.CLINICA,
				FatItensProcedHospitalar.Fields.TIPO_ATO,
				FatItensProcedHospitalar.Fields.TIPOS_VINCULO,
				FatItensProcedHospitalar.Fields.MOTIVO_COBRACA_APAC,
				FatItensProcedHospitalar.Fields.FAT_FORMA_ORGANIZACAO,
				FatItensProcedHospitalar.Fields.SUB_GRUPO,
				FatItensProcedHospitalar.Fields.GRUPO);
	}

	@Override
	public void persistirCaractItemProcedimentoHospitalar(
			final FatCaractItemProcHosp caractItemProcHosp,
			final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		this.getCaracteristicaItemProcedimentoHospitalarON()
				.persistirCaractItemProcedimentoHospitalar(caractItemProcHosp,
						dataFimVinculoServidor);
	}

	@Override
	public void removerCaractItemProcedimentoHospitalar(
			final FatCaractItemProcHosp caractItemProcHosp) {
		this.getFatCaractItemProcHospDAO().removerPorId(
				caractItemProcHosp.getId());
	}

	@Override
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalarTabFatPadrao(final Object obj) throws BaseException {
		short prmPhoSeq = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		return this.getFatItensProcedHospitalarDAO().listarFatItensProcedHospitalarTabFatPadrao(obj, prmPhoSeq);
	}

	@Override
	public Long listarFatItensProcedHospitalarTabFatPadraoCount(final Object obj)throws BaseException {
		short prmPhoSeq = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		return this.getFatItensProcedHospitalarDAO().listarFatItensProcedHospitalarTabFatPadraoCount(obj, prmPhoSeq);
	}

	@Override
	public List<FatItensProcedHospitalar> listarIPHPorConvenioSaudePlanoConvProcedHosp(
			final Object objPesquisa) throws BaseException {
		return this.getFatItensProcedHospitalarDAO()
				.listarIPHPorConvenioSaudePlanoConvProcedHosp(objPesquisa);
	}

	@Override
	public Long listarIPHPorConvenioSaudePlanoConvProcedHospCount(
			final Object objPesquisa) throws BaseException {
		return this.getFatItensProcedHospitalarDAO()
				.listarIPHPorConvenioSaudePlanoConvProcedHospCount(objPesquisa);
	}

	@Override
	public List<ItemProcedimentoHospitalarVO> obterProcedimentosMedicoAudtAIH(
			final Integer cthSeq) {
		return this.getFatItensProcedHospitalarDAO()
				.obterProcedimentosMedicoAudtAIH(cthSeq);
	}
    
	@Override
	public List<ItemProcedimentoHospitalarVO> obterProcedimentosAtosMedicosAIH(final Integer cthSeq) throws ApplicationBusinessException {
		Byte taoSeq = parametroFacade.buscarValorByte(AghuParametrosEnum.P_COD_ATO_OPM);
		return this.getFatItensProcedHospitalarDAO().obterProcedimentosAtosMedicosAIH(cthSeq, taoSeq);
	}

	@Override
	public List<FatItensProcedHospitalar> listarItensProcedHospTabPadraoPlanoInt(
			final Object objPesquisa) throws BaseException {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospTabPadraoPlanoInt(objPesquisa);
	}

	@Override
	public Long listarItensProcedHospTabPadraoPlanoIntCount(
			final Object objPesquisa) throws BaseException {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospTabPadraoPlanoIntCount(objPesquisa);
	}

	protected ItemContaHospitalarON getItemContaHospitalarON() {
		return itemContaHospitalarON;
	}

	protected CidContaHospitalarON getCidContaHospitalarON() {
		return cidContaHospitalarON;
	}

	protected CidContaHospitalarPersist getCidContaHospitalarPersist() {
		return cidContaHospitalarPersist;
	}

	protected FaturamentoRN getFaturamentoRN() {

		return faturamentoRN;
	}

	protected ItensProcedHospitalarRN getItensProcedHospitalarRN() {
		return itensProcedHospitalarRN;
	}

	protected ItemProcedimentoHospitalarON getItemProcedimentoHospitalarON() {
		return itemProcedimentoHospitalarON;
	}

	protected FatCaractItemProcHospDAO getFatCaractItemProcHospDAO() {
		return fatCaractItemProcHospDAO;
	}

	protected CaracteristicaItemProcedimentoHospitalarON getCaracteristicaItemProcedimentoHospitalarON() {
		return caracteristicaItemProcedimentoHospitalarON;
	}

	protected FatTipoCaractItensDAO getFatTipoCaractItensDAO() {
		return fatTipoCaractItensDAO;
	}

	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}

	protected FatProcedimentoHospitalarDAO getFatProcedimentoHospitalarDAO() {
		return fatProcedimentoHospitalarDAO;
	}

	protected FatPossibilidadeRealizadoDAO getFatPossibilidadeRealizadoDAO() {
		return fatPossibilidadeRealizadoDAO;
	}

	@Override
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final String descricao, final Short seq, final Long codTabela)
			throws BaseException {
		return getFatItensProcedHospitalarDAO()
				.obterListaFatItensProcedHospitalar(firstResult, maxResult,
						orderProperty, asc, descricao, seq, codTabela);
	}

	@Override
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final String descricao, final Integer seq, final Long codTabela,
			final Short phoSeq) throws BaseException {
		return getFatItensProcedHospitalarDAO()
				.obterListaFatItensProcedHospitalar(firstResult, maxResult,
						orderProperty, asc, descricao, seq, codTabela, phoSeq);
	}

	@Override
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalarPorPhoSeqECodTabela(
			final Short phoSeq, final Object codTabela,
			final Integer limiteRegistros) {
		return getFatItensProcedHospitalarDAO()
				.listarFatItensProcedHospitalarPorPhoSeqECodTabela(phoSeq,
						codTabela, limiteRegistros);
	}

	@Override
	public Long listarFatItensProcedHospitalarPorPhoSeqESeqCount(
			final Short phoSeq, final Object seq) {
		return getFatItensProcedHospitalarDAO()
				.listarFatItensProcedHospitalarPorPhoSeqECodTabelaCount(phoSeq,
						seq);
	}

	@Override
	@BypassInactiveModule
	public FatProcedimentosHospitalares obterProcedimentoHospitalar(
			final Short seq) {
		return getFatProcedimentoHospitalarDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public Long listarFatItensProcedHospitalarCount(final String descricao,
			final Short seq, final Long codTabela) {
		return getFatItensProcedHospitalarDAO()
				.obterListaFatItensProcedHospitalarCount(descricao, seq,
						codTabela);
	}

	@Override
	public Long listarFatItensProcedHospitalarCount(final String descricao,
			final Integer seq, final Long codTabela, final Short phoSeq) {
		return getFatItensProcedHospitalarDAO()
				.obterListaFatItensProcedHospitalarCount(descricao, seq,
						codTabela, phoSeq);
	}

	@Override
	public List<FatProcedimentosHospitalares> listarProcedimentosHospitalar(
			final Object param) throws BaseException {
		return getFatProcedimentoHospitalarDAO()
				.listarProcedimentosHospitalaresPorSeqEDescricao(param);
	}

	@Override
	public Long listarProcedimentosHospitalarCount(final Object param)
			throws BaseException {
		return getFatProcedimentoHospitalarDAO()
				.listarProcedimentosHospitalaresPorSeqEDescricaoCount(param);
	}

	@Override
	public List<FatProcedimentosHospitalares> listarProcedimentosHospitalaresPorSeqEDescricao(
			final Object objPesquisa) {
		return getFatProcedimentoHospitalarDAO()
				.listarProcedimentosHospitalaresPorSeqEDescricao(objPesquisa);
	}

	@Override
	public Long listarProcedimentosHospitalaresPorSeqEDescricaoCount(
			final Object objPesquisa) {
		return getFatProcedimentoHospitalarDAO()
				.listarProcedimentosHospitalaresPorSeqEDescricaoCount(
						objPesquisa);
	}

	@Override
	public List<FatMotivoCobrancaApac> pesquisarMotivoCobrancaApac(
			final Object objPesquisa) {
		return this.getFatMotivoCobrancaApacDAO().pesquisarMotivoCobrancaApac(
				objPesquisa);
	}

	@Override
	public Long pesquisarMotivoCobrancaApacCount(final Object objPesquisa) {
		return this.getFatMotivoCobrancaApacDAO()
				.pesquisarMotivoCobrancaApacCount(objPesquisa);

	}

	@Override
	public List<DemonstrativoFaturamentoInternacaoVO> pesquisarEspelhoAih(final Integer prontuario, final Date dtInternacaoAdm) throws ApplicationBusinessException {
		Byte taoSeq = parametroFacade.buscarValorByte(AghuParametrosEnum.P_COD_ATO_OPM);
		return getFatEspelhoAihDAO().pesquisarEspelhoAih(prontuario,dtInternacaoAdm, taoSeq);
	}

	@Override
	public List<AihFaturadaVO> listarEspelhoAihFaturada(final Integer cthSeq,
			final Integer prontuario, final Integer mes, final Integer ano,
			final Date dthrInicio, final Long codTabelaIni,
			final Long codTabelaFim, final String iniciais,
			final Boolean reapresentada) {
		return getFatEspelhoAihDAO().listarEspelhoAihFaturada(cthSeq,
				prontuario, mes, ano, dthrInicio, codTabelaIni, codTabelaFim,
				iniciais, reapresentada);
	}

	@Override
	public List<ProcedimentoNaoFaturadoVO> pesquisarEspelhoAihProcedimentosNaoFaturados(
			final Long iphCodTabela, final Long iphCodSusRealiz,
			final Short espSeq, final Integer mes, final Integer ano,
			final Date dthrInicio,
			final DominioGrupoProcedimento grupoProcedimento,
			final String iniciais, final Boolean reapresentada)
			throws ApplicationBusinessException {
		return getFatEspelhoAihDAO()
				.pesquisarEspelhoAihProcedimentosNaoFaturados(iphCodTabela,
						iphCodSusRealiz, espSeq, mes, ano, dthrInicio,
						grupoProcedimento, iniciais, reapresentada);
	}

	@Override
	public List<PendenciaEncerramentoVO> pesquisarMensagensErro(
			final Date dtInicial, final Date dtFinal,
			final DominioSituacaoMensagemLog grupo, final String erro,
			final String nome, final Boolean reapresentada) {
		return getFatContasHospitalaresDAO().pesquisarMensagensErro(dtInicial,
				dtFinal, grupo, erro, nome, reapresentada);
	}

	@Override
	public FatProcedimentosHospitalares obterFatProcedimentosHospitalaresPadrao() {
		return getFaturamentoRN().obterFatProcedimentosHospitalaresPadrao();
	}

	@Override
	public List<FatTipoAto> pesquisarTipoAto(final Object objPesquisa) {
		return this.getFatTipoAtoDAO().pesquisarTipoAto(objPesquisa);
	}

	@Override
	public Long pesquisarTipoAtoCount(final Object objPesquisa) {
		return this.getFatTipoAtoDAO().pesquisarTipoAtoCount(objPesquisa);

	}

	@Override
	public List<FatTiposVinculo> pesquisarTipoVinculo(final Object objPesquisa) {
		return this.getFatTiposVinculoDAO().pesquisarTipoVinculo(objPesquisa);
	}

	@Override
	public Long pesquisarTipoVinculoCount(final Object objPesquisa) {
		return this.getFatTiposVinculoDAO().pesquisarTipoVinculoCount(
				objPesquisa);

	}

	protected FatDocumentoCobrancaAihsDAO getFatDocumentoCobrancaAihsDAO() {
		return fatDocumentoCobrancaAihsDAO;
	}

	protected FatMensagemLogDAO getFatMensagemLogDAO() {
		return fatMensagemLogDAO;
	}

	@Override
	public FatDocumentoCobrancaAihs pesquisarFatDocumentoCobrancaAihsPorCodigoDcih(
			final String codigoDcih) {
		return getFatDocumentoCobrancaAihsDAO().obterPorChavePrimaria(
				codigoDcih);
	}

	@Override
	public List<FatFormaOrganizacao> listarFormasOrganizacaoPorCodigoOuDescricao(
			final Object objPesquisa, final Short grpSeq, final Byte subGrupo) {
		return this.getFatFormaOrganizacaoDAO()
				.listarFormasOrganizacaoPorCodigoOuDescricao(objPesquisa,
						grpSeq, subGrupo);
	}

	@Override
	public Long listarFormasOrganizacaoPorCodigoOuDescricaoCount(
			final Object objPesquisa, final Short grpSeq, final Byte subGrupo) {
		return this.getFatFormaOrganizacaoDAO()
				.listarFormasOrganizacaoPorCodigoOuDescricaoCount(objPesquisa,
						grpSeq, subGrupo);
	}

	@Override
	public List<FatSubGrupo> listarSubGruposPorCodigoOuDescricao(
			final Object objPesquisa, final Short grpSeq) {
		return this.getFatSubGrupoDAO().listarSubGruposPorCodigoOuDescricao(
				objPesquisa, grpSeq);
	}

	@Override
	public Long listarSubGruposPorCodigoOuDescricaoCount(
			final Object objPesquisa, final Short grpSeq) {
		return this.getFatSubGrupoDAO()
				.listarSubGruposPorCodigoOuDescricaoCount(objPesquisa, grpSeq);
	}

	@Override
	public List<FatGrupo> listarGruposPorCodigoOuDescricao(
			final Object objPesquisa) {
		return getFatGrupoDAO().listarGruposPorCodigoOuDescricao(objPesquisa);
	}

	@Override
	public Long listarGruposPorCodigoOuDescricaoCount(final Object objPesquisa) {
		return getFatGrupoDAO().listarGruposPorCodigoOuDescricaoCount(
				objPesquisa);
	}

	protected FatGrupoDAO getFatGrupoDAO() {
		return fatGrupoDAO;
	}

	protected FatSubGrupoDAO getFatSubGrupoDAO() {
		return fatSubGrupoDAO;
	}

	protected FatFormaOrganizacaoDAO getFatFormaOrganizacaoDAO() {
		return fatFormaOrganizacaoDAO;
	}

	protected FatTipoAtoDAO getFatTipoAtoDAO() {
		return fatTipoAtoDAO;
	}

	protected FatTiposVinculoDAO getFatTiposVinculoDAO() {
		return fatTiposVinculoDAO;
	}

	protected FatMotivoCobrancaApacDAO getFatMotivoCobrancaApacDAO() {
		return fatMotivoCobrancaApacDAO;
	}

	protected FatAtoMedicoAihDAO getFatAtoMedicoAihDAO() {
		return fatAtoMedicoAihDAO;
	}

	@Override
	public Long listarFatAtoMedicoEspelhoCount(final Integer cthSeq) {
		return getFatAtoMedicoAihDAO().listarFatAtoMedicoEspelhoCount(cthSeq);
	}

	@Override
	public List<FatLogError> pesquisarFatLogError(final Integer cthSeq,
			final Integer pacCodigo, final Short ichSeqp, final String erro,
			final Integer phiSeqItem1, final Long codItemSus1,
			final DominioModuloCompetencia modulo, final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc) {

		return getLogErrorON().pesquisarFatLogError(cthSeq, pacCodigo, ichSeqp,
				erro, phiSeqItem1, codItemSus1, modulo, firstResult, maxResult,
				orderProperty, asc);
	}

	@Override
	public FatLogError obterFatLogError(final Integer seq) {
		return getLogErrorON().obterFatLogError(seq);
	}

	@Override
	public FatLogErrorVO obterFatLogErrorVo(final Short iphPhoSeqItem1,
			final Integer iphSeqItem1, final Short iphPhoSeqItem2,
			final Integer iphSeqItem2, final Short iphPhoSeqRealizado,
			final Integer iphSeqRealizado, final Short iphPhoSeq,
			final Integer iphSeq) {

		return getLogErrorON().obterFatLogErrorVo(iphPhoSeqItem1, iphSeqItem1,
				iphPhoSeqItem2, iphSeqItem2, iphPhoSeqRealizado,
				iphSeqRealizado, iphPhoSeq, iphSeq);
	}

	@Override
	public Long pesquisarFatLogErrorCount(final Integer cthSeq,
			final Integer pacCodigo, final Short ichSeqp, final String erro,
			final Integer phiSeq1, final Long itemSMM1,
			final DominioModuloCompetencia modulo) {
		return getLogErrorON().pesquisarFatLogErrorCount(cthSeq, pacCodigo,
				ichSeqp, erro, phiSeq1, itemSMM1, modulo);
	}

	protected FatLogErrorON getLogErrorON() {
		return fatLogErrorON;
	}

	protected FatCboDAO getFatCboDAO() {
		return fatCboDAO;
	}

	protected FatEspelhoAihDAO getFatEspelhoAihDAO() {
		return fatEspelhoAihDAO;
	}

	@Override
	public FatCbos obterFatCboPorCodigoVigente(final String codigo)
			throws ApplicationBusinessException {
		return getFatCboDAO().obterFatCboPorCodigoVigente(codigo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Map<String, Object> atualizarItensProcedHosp(
			final List<String> lista) throws BaseException {
		return getImportarArquivoSusON().atualizarItensProcedimentoHospitalar(
				lista);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Map<String, Object> atualizarCboProcedimento(final List<String> lista)
			throws BaseException {
		return getImportarArquivoSusON().atualizarCboProcedimento(lista);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Map<String, Object> atualizarGeral(final List<String> lista)
			throws BaseException {
		return getImportarArquivoSusON().atualizarGeral(lista);
	}

	@Override
	public void verificaNomeArquivoZip(final String arquivo)
			throws ApplicationBusinessException {
		getImportarArquivoSusON().verificaNomeArquivoZip(arquivo);
	}

	protected ImportarArquivoSusON getImportarArquivoSusON() {
		return importarArquivoSusON;
	}

	@Override
	public void converterContaEmSemCobertura(final Integer cthSeqSelected,
			final DominioSituacaoConta situacao, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		getContaHospitalarON().converterContaEmSemCobertura(cthSeqSelected,
				situacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void faturarContaUmDia(final Integer cthSeqSelected,
			final DominioSituacaoConta situacao, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		getContaHospitalarON().faturarContaUmDia(cthSeqSelected, situacao,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	private FaturamentoON getFaturamentoON() {
		return faturamentoON;
	}

	private ConvenioGrupoItemProcedimentoON getConvenioGrupoItemProcedimentoON() {
		return convenioGrupoItemProcedimentoON;
	}

	@Override
	public Boolean fatcVerCaractPhiQrInt(final Short cnvCodigo,
			final Byte cspSeq, final Integer phiSeq, final String caracteristica)
			throws BaseException {
		return getContaHospitalarRN().fatcVerCaractPhiQrInt(cnvCodigo, cspSeq,
				phiSeq, caracteristica);
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoSUSItem(
			final Object objPesquisa, final Integer phiSeq,
			final Integer cthSeq, final Boolean isProcHospSolictado)
			throws BaseException {
		return getVFatAssociacaoProcedimentoDAO()
				.listarAssociacaoProcedimentoSUSItem(objPesquisa, phiSeq,
						cthSeq, isProcHospSolictado);
	}

	@Override
	public Long listarAssociacaoProcedimentoSUSItemCount(
			final Object objPesquisa, final Integer phiSeq,
			final Integer cthSeq, final Boolean isProcHospSolictado)
			throws BaseException {
		return getVFatAssociacaoProcedimentoDAO()
				.listarAssociacaoProcedimentoSUSItemCount(objPesquisa, phiSeq,
						cthSeq, isProcHospSolictado);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Boolean rnCthcAtuGeraEsp(final Integer pCthSeq,
			final Boolean pPrevia, String nomeMicrocomputador,
			final Date dataFimVinculoServidor, boolean refresh) throws BaseException {
		return getFatkCth2RN().rnCthcAtuGeraEsp(pCthSeq, pPrevia,
				nomeMicrocomputador, dataFimVinculoServidor, refresh);
	}

	@Override
	@BypassInactiveModule
	public RnCthcVerDatasVO rnCthcVerDatas(final Integer pIntSeq,
			final Date pDataNova, final Date pDataAnterior,
			final String pTipoData, final Date dataFimVinculoServidor)
			throws BaseException {
		return getContaHospitalarRN().rnCthcVerDatas(pIntSeq, pDataNova,
				pDataAnterior, pTipoData, dataFimVinculoServidor);
	}

	@Override
	public void rnCthpAtuDiarUti(final Integer pCthSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		getContaHospitalarRN().rnCthpAtuDiarUti(pCthSeq, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	public void rnCthpTrocaCnv(final Integer pIntSeq, final Date pDtInt,
			final Short pCnvOld, final Byte pCspOld, final Short pCnvNew,
			final Byte pCspNew, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		getContaHospitalarRN().rnCthpTrocaCnv(pIntSeq, pDtInt, pCnvOld,
				pCspOld, pCnvNew, pCspNew, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	public Boolean validarReaberturaContaHospitalar(
			final Integer cthSeqSelected, final DominioSituacaoConta situacao)
			throws BaseException {
		return getContaHospitalarON().validarReaberturaContaHospitalar(
				cthSeqSelected, situacao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Boolean reabrirContaHospitalar(final Integer cthSeqSelected,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return getEncerramentoContaHospitalarON().reabrirContaHospitalar(cthSeqSelected,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public List<Integer> getContasParaReaberturaEmLote(
			final FatCompetencia competencia, final Date dtInicial,
			final Date dtFinal, final Long procedimentoSUS)
			throws BaseException {
		return getContaHospitalarRN().getContasParaReaberturaEmLote(
				competencia, dtInicial, dtFinal, procedimentoSUS);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public boolean reabrirContasHospitalares(final Integer cthSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return getEncerramentoContaHospitalarON().reabrirContaHospitalar(cthSeq,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void gerarCompetenciaEmManutencao(final FatCompetencia competencia,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		this.getGeracaoArquivoFaturamentoCompetenciaInternacaoON()
				.gerarCompetenciaEmManutencao(competencia, nomeMicrocomputador,
						dataFimVinculoServidor);

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Boolean encerrarCompetenciaAtualEAbreNovaCompetencia(
			final FatCompetencia fatCompetencia, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		return getGeracaoArquivoFaturamentoCompetenciaInternacaoON()
				.encerrarCompetenciaAtualEAbreNovaCompetencia(fatCompetencia,
						nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public Long listarFatEspelhoAihCount(final Integer cthSeq) {
		return getFatEspelhoAihDAO().listarFatEspelhoAihCount(cthSeq);
	}

	protected FatVlrItemProcedHospCompsDAO getFatVlrItemProcedHospCompsDAO() {
		return fatVlrItemProcedHospCompsDAO;
	}

	protected FatSinonimoItemProcedHospDAO getFatSinonimoItemProcedHospDAO() {
		return fatSinonimoItemProcedHospDAO;
	}

	@Override
	public FatVlrItemProcedHospComps persistirFatVlrItemProcedHospComps(
			final FatVlrItemProcedHospComps fatVlrItemProcedHospComps,
			final Integer seq, final Short phoSeq) throws BaseException {
		return getValorItemProcedimentoON().persistirFatVlrItemProcedHospComps(
				fatVlrItemProcedHospComps, seq, phoSeq);
	}

	protected ValorItemProcedimentoON getValorItemProcedimentoON() {
		return valorItemProcedimentoON;
	}

	@Override
	public List<FatVlrItemProcedHospComps> obterListaValorItemProcHospComp(
			final Short phoSeq, final Integer iphSeq) {
		return getFatVlrItemProcedHospCompsDAO()
				.obterListaValorItemProcHospComp(phoSeq, iphSeq);
	}

	@Override
	public void setTimeout(final Integer timeout)
			throws ApplicationBusinessException {
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

	@Override
	public void commit(final Integer timeout)
			throws ApplicationBusinessException {
		// final UserTransaction userTx = this.getUserTransaction();
		//
		// try {
		// if (userTx.isNoTransaction() || !userTx.isActive()) {
		// userTx.begin();
		// }
		// final EntityManager em = this.getEntityManager();
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
		// } catch (final Exception e) {
		// logError(e.getMessage(), e);
		// FaturamentoExceptionCode.ERRO_AO_CONFIRMAR_TRANSACAO
		// .throwException();
		// }
	}

	@Override
	public void commit() throws ApplicationBusinessException {
		this.commit(null);
	}

	@Override
	public void clear() {
		super.flush();
		super.clear();
	}

	@Override
	public void clearSemFlush() {
		super.clear();
	}

	@Override
	public void evict(BaseEntity entity) {
		this.flush();
		super.evict(entity);
	}

	// @Override
	// @BypassInactiveModule
	// public void refresh(final Object entity) {
	// this.getEntityManager().refresh(entity);
	// }

	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}

	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public FatCompetencia clonarFatCompetencia(final FatCompetencia competencia)
			throws Exception {
		return getFatCompetenciaON().clonarFatCompetencia(competencia);
	}

	protected FatCompetenciaON getFatCompetenciaON() {
		return fatCompetenciaON;
	}

	@Override
	public List<FatCompetencia> obterCompetenciasPorModuloESituacoes(
			final DominioModuloCompetencia modulo,
			final DominioSituacaoCompetencia... situacoes) {
		return getFatCompetenciaDAO().obterCompetenciasPorModuloESituacoes(
				modulo, situacoes);
	}

	@Override
	public List<FatProcedimentoCbo> listarProcedimentoCboPorIphSeqEPhoSeq(
			final Integer iphSeq, final Short iphPhoSeq) {
		return getFatProcedimentoCboDAO()
				.listarProcedimentoCboPorIphSeqEPhoSeq(iphSeq, iphPhoSeq);
	}

	@Override
	public List<FatProcedimentoCbo> listarProcedimentoCboPorCbo(
			final String codigoCbo) {
		return getFatProcedimentoCboDAO()
				.listarProcedimentoCboPorCbo(codigoCbo);
	}

	@Override
	public List<FatConvenioSaudePlano> pesquisarPlanoPorConvenioSaude(
			final Short cnvCodigo) {
		return getFatConvenioSaudePlanoDAO().pesquisarPlanoPorConvenioSaude(
				cnvCodigo);
	}

	@Override
	public FatProcedimentoCbo obterProcedimentoCboPorChavePrimaria(
			final Integer seq) {
		return this.getFatProcedimentoCboDAO().obterPorChavePrimaria(seq);
	}

	protected FatProcedimentoCboDAO getFatProcedimentoCboDAO() {
		return fatProcedimentoCboDAO;
	}

	@Override
	public void inserirProcedimentoCbo(final FatProcedimentoCbo procCbo) {

		this.getProcedimentoCboON().inserirProcedimentoCbo(procCbo);
	}

	@Override
	public FatConvenioSaude obterConvenioSaudeComPagador(Short codigoConvenio) {
		return this.getFatConvenioSaudeDAO().obterConvenioSaudeComPagador(
				codigoConvenio);
	}

	@Override
	public void removerProcedimentoCbo(final FatProcedimentoCbo procCbo) {

		this.getProcedimentoCboON().removerProcedimentoCbo(procCbo);
	}

	protected ProcedimentoCboON getProcedimentoCboON() {
		return procedimentoCboON;
	}

	@Override
	public List<FatCbos> listarCbos(final Object objPesquisa)
			throws ApplicationBusinessException {
		return getFatCboDAO().listarCbos(objPesquisa);
	}

	@Override
	public Long listarCbosCount(final Object objPesquisa) {
		return getFatCboDAO().listarCbosCount(objPesquisa);
	}
	
	@Override
	public List<FatCbos> listarCbosAtivos(final Object objPesquisa)
			throws ApplicationBusinessException {
		return getFatCboDAO().listarCbosAtivos(objPesquisa);
	}

	@Override
	public Long listarCbosAtivosCount(final Object objPesquisa) {
		return getFatCboDAO().listarCbosAtivosCount(objPesquisa);
	}	

	@Override
	public List<FatProcedimentosHospitalares> listarProcedimentosHospitalaresPorSeqEDescricaoOrdenado(
			final Object param, final String ordem) {
		return getFatProcedimentoHospitalarDAO()
				.listarProcedimentosHospitalaresPorSeqEDescricaoOrdenado(param,
						ordem);
	}

	protected AtoMedicoAihPersist getAtoMedicoAihPersist() {
		return atoMedicoAihPersist;
	}

	@Override
	public void inserirAtoMedicoAih(final FatAtoMedicoAih atoMedAih,
			final boolean comFlush, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {

		getAtoMedicoAihPersist().setComFlush(comFlush);
		getAtoMedicoAihPersist().inserir(atoMedAih, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	public void atualizarAtoMedicoAih(final FatAtoMedicoAih atoMedAih,
			final boolean comFlush, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {

		getAtoMedicoAihPersist().setComFlush(comFlush);
		getAtoMedicoAihPersist().atualizar(atoMedAih, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	public FatValorContaHospitalar inserirFatValorContaHospitalar(
			final FatValorContaHospitalar fatValorContaHospitalar,
			final boolean flush) {
		return getFaturamentoRN().inserirFatValorContaHospitalar(
				fatValorContaHospitalar, flush);
	}

	@Override
	public FatValorContaHospitalar atualizarFatValorContaHospitalar(
			final FatValorContaHospitalar fatValorContaHospitalar,
			final boolean flush) {
		return getFaturamentoRN().atualizarFatValorContaHospitalar(
				fatValorContaHospitalar, flush);
	}

	@Override
	public FatEspelhoItemContaHosp atualizarFatEspelhoItemContaHosp(
			final FatEspelhoItemContaHosp fatEspelhoItemContaHosp) {
		return getFaturamentoRN().atualizarFatEspelhoItemContaHosp(
				fatEspelhoItemContaHosp);
	}

	@Override
	public FatEspelhoItemContaHosp inserirFatEspelhoItemContaHospSemFlush(
			final FatEspelhoItemContaHosp fatEspelhoItemContaHosp) {
		return getFaturamentoRN().inserirFatEspelhoItemContaHosp(
				fatEspelhoItemContaHosp, false);
	}

	@Override
	public FatDocumentoCobrancaAihs atualizarFatDocumentoCobrancaAihs(
			final FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs,
			final FatDocumentoCobrancaAihs oldFatDocumentoCobrancaAihs)
			throws ApplicationBusinessException {
		return getFaturamentoRN().atualizarFatDocumentoCobrancaAihs(
				fatDocumentoCobrancaAihs, oldFatDocumentoCobrancaAihs);
	}

	@Override
	public void atualizarFatAih(final FatAih fatAih, final FatAih oldFatAih)
			throws ApplicationBusinessException {
		getFaturamentoRN().atualizarFatAih(fatAih, oldFatAih);
	}

	@Override
	public void atualizarSituacaoFatAih(final FatAih fatAih,
			final DominioSituacaoAih indSituacao) throws BaseException {
		getFaturamentoRN().atualizarSituacaoFatAih(fatAih, indSituacao);
	}

	protected EspelhoAihPersist getEspelhoAihPersist() {
		return espelhoAihPersist;
	}

	@Override
	public void inserirFatEspelhoAih(final FatEspelhoAih fatEspelhoAih,
			final boolean flush, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		getEspelhoAihPersist().setComFlush(flush);
		getEspelhoAihPersist().inserir(fatEspelhoAih, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	public void atualizarFatEspelhoAih(final FatEspelhoAih fatEspelhoAih,
			final boolean flush, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		getEspelhoAihPersist().setComFlush(flush);
		getEspelhoAihPersist().atualizar(fatEspelhoAih, nomeMicrocomputador,
				dataFimVinculoServidor);
	}
		
	@Override
	public void atualizarFatEspelhoAih(final FatEspelhoAih fatEspelhoAih) throws BaseException {
		espelhoAihRN.atualizarFatEspelhoAih(fatEspelhoAih);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void persistirLogError(final FatLogError fle) {
		this.getLogErrorON().persistirLogError(fle);
	}

	@Override
	public void persistirLogError(final FatLogError fle, final boolean flush) {
		this.getLogErrorON().persistirLogError(fle, flush);
	}

	@Override
	public void atualizarFaturamentoSolicitacaoExames(
			final AelSolicitacaoExames modificada,
			final AelSolicitacaoExames original, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws IllegalStateException,
			BaseException {
		getVerificacaoFaturamentoSusRN().atualizarFaturamentoSolicitacaoExames(
				modificada, original, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	protected VerificacaoFaturamentoSusRN getVerificacaoFaturamentoSusRN() {
		return verificacaoFaturamentoSusRN;
	}

	@Override
	public void inserirFatPerdaItemConta(
			final FatPerdaItemConta fatPerdaItemConta, final boolean flush)
			throws BaseException {
		faturamentoInternacaoRN.inserirFatPerdaItemConta(fatPerdaItemConta, flush);
	}

	@Override
	public void validarCamposFatVlrItemProcedHospComps(
			final FatVlrItemProcedHospComps vlrItemProcedHospComps,
			final FatVlrItemProcedHospComps vigente)
			throws ApplicationBusinessException {
		getValorItemProcedimentoON().validarCamposFatVlrItemProcedHospComps(
				vlrItemProcedHospComps, vigente);
	}

	protected FatCampoMedicoAuditAihPersist getFatCampoMedicoAuditAihPersist() {
		return fatCampoMedicoAuditAihPersist;
	}

	@Override
	public void inserirFatCampoMedicoAuditAih(
			final FatCampoMedicoAuditAih campoMedicoAuditAih,
			final boolean comFlush, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		getFatCampoMedicoAuditAihPersist().setComFlush(comFlush);
		getFatCampoMedicoAuditAihPersist().inserir(campoMedicoAuditAih,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public List<FatCompatExclusItem> listarFatCompatExclusItem(
			final Short phoSeq, final Integer seq,
			final DominioSituacao indSituacao) {
		return getFatCompatExclusItemDAO().listarFatCompatExclusItem(phoSeq,
				seq, indSituacao);
	}

	@Override
	public void persistirFatCompatExclusItem(
			final List<FatCompatExclusItem> newListaFatCompatExclusItem,
			final List<FatCompatExclusItem> oldListaFatCompatExclusItem,
			final List<FatCompatExclusItem> excluidosListaFatCompatExclusItem)
			throws BaseException {
		getFatCompatExclusItemON().persistirFatCompatExclusItem(
				newListaFatCompatExclusItem, oldListaFatCompatExclusItem,
				excluidosListaFatCompatExclusItem);
	}

	@Override
	public void persistirFatProcedHospIntCid(
			final FatProcedHospIntCid fatProcedHospIntCid, final Boolean flush)
			throws BaseException {
		getFatProcedHospIntCidON().persistirFatProcedHospIntCid(
				fatProcedHospIntCid, flush);
	}

	@Override
	public void removerFatProcedHospIntCid(final Integer phiSeq,
			final Integer cidSeq, final Boolean flush) throws BaseException {
		getFatProcedHospIntCidON().removerFatProcedHospIntCid(phiSeq, cidSeq,
				flush);
	}

	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public List<FatCompatExclusItem> clonarFatCompatExclusItem(
			final List<FatCompatExclusItem> listaOriginal) throws Exception {
		return getFatCompatExclusItemON().clonarListaFatCompatExclusItem(
				listaOriginal);
	}

	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public FatConvGrupoItemProced clonarGrupoItemConvenio(
			final FatConvGrupoItemProced elemento) throws Exception {
		return getConvenioGrupoItemProcedimentoON().clonarGrupoItemConvenio(
				elemento);
	}

	protected FatCompatExclusItemON getFatCompatExclusItemON() {
		return fatCompatExclusItemON;
	}

	protected FatProcedHospIntCidON getFatProcedHospIntCidON() {
		return fatProcedHospIntCidON;
	}

	protected FatCompatExclusItemDAO getFatCompatExclusItemDAO() {
		return fatCompatExclusItemDAO;
	}

	protected VFatConvPlanoGrupoProcedDAO getVFatConvPlanoGrupoProcedDAO() {
		return vFatConvPlanoGrupoProcedDAO;
	}

	@Override
	public List<FatProcedHospInternos> listarPhisAtivosPorSeqEDescricao(
			final Object objPesquisa) {
		return getFatProcedHospInternosDAO().listarPhisAtivosPorSeqEDescricao(
				objPesquisa);
	}

	@Override
	public Long listarPhisAtivosPorSeqEDescricaoCount(final Object objPesquisa) {
		return getFatProcedHospInternosDAO()
				.listarPhisAtivosPorSeqEDescricaoCount(objPesquisa);
	}

	@Override
	public List<VFatConvPlanoGrupoProcedVO> listarTabelas(
			final Object objPesquisa) {
		return getVFatConvPlanoGrupoProcedDAO().listarTabelas(objPesquisa);
	}

	@Override
	public Long listarTabelasCount(final Object objPesquisa) {
		return getVFatConvPlanoGrupoProcedDAO().listarTabelasCount(objPesquisa);
	}

	@Override
	public List<VFatConvPlanoGrupoProcedVO> listarConvenios(
			final Object objPesquisa, final Short grcSeq, final Short cphPhoSeq) {
		return getVFatConvPlanoGrupoProcedDAO().listarConvenios(objPesquisa,
				grcSeq, cphPhoSeq);
	}

	@Override
	public Long listarConveniosCount(final Object objPesquisa,
			final Short grcSeq, final Short cphPhoSeq) {
		return getVFatConvPlanoGrupoProcedDAO().listarConveniosCount(
				objPesquisa, grcSeq, cphPhoSeq);
	}

	@Override
	public List<VFatConvPlanoGrupoProcedVO> listarPlanos(
			final Object objPesquisa, final Short grcSeq,
			final Short cphPhoSeq, final Short cphCspCnvCodigo) {
		return getVFatConvPlanoGrupoProcedDAO().listarPlanos(objPesquisa,
				grcSeq, cphPhoSeq, cphCspCnvCodigo);
	}

	@Override
	public Long listarPlanosCount(final Object objPesquisa, final Short grcSeq,
			final Short cphPhoSeq, final Short cphCspCnvCodigo) {
		return getVFatConvPlanoGrupoProcedDAO().listarPlanosCount(objPesquisa,
				grcSeq, cphPhoSeq, cphCspCnvCodigo);
	}

	@Override
	public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(
			final Object objPesquisa, final Short phoSeq, final String order) {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(
						objPesquisa, phoSeq, order);
	}

	@Override
	public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqOrdenadoPorSeq(
			final Object objPesquisa, final Short phoSeq) {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqOrdenadoPorSeq(
						objPesquisa, phoSeq);
	}

	@Override
	public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(
			final Object objPesquisa, final Short phoSeq) {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(
						objPesquisa, phoSeq);
	}

	@Override
	public List<FatItensProcedHospitalar> listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeq(
			final Object objPesquisa, final Short phoSeq) {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeq(
						objPesquisa, phoSeq);
	}

	@Override
	public Long listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqCount(
			final Object objPesquisa, final Short phoSeq) {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqCount(
						objPesquisa, phoSeq);
	}

	@Override
	public List<FatItensProcedHospitalar> listarItensProcedHospPorCodTabelaOuDescricaoOrteseEProtese(
			final Object objPesquisa, final Short phoSeq) {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospPorCodTabelaOuDescricaoOrteseEProtese(
						objPesquisa, phoSeq);
	}

	@Override
	public Long listarItensProcedHospPorCodTabelaOuDescricaoOrteseEProteseCount(
			final Object objPesquisa, final Short phoSeq) {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospPorCodTabelaOuDescricaoOrteseEProteseCount(
						objPesquisa, phoSeq);
	}

	@Override
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalar(
			final Object objPesquisa) {
		return getFatItensProcedHospitalarDAO().listarFatItensProcedHospitalar(
				objPesquisa);
	}

	@Override
	public Long listarFatItensProcedHospitalarCount(final Object objPesquisa) {
		return getFatItensProcedHospitalarDAO()
				.listarFatItensProcedHospitalarCount(objPesquisa);
	}

	@Override
	public List<FatTipoTransplante> listarTodosOsTiposTransplante() {
		return getFatTipoTransplanteDAO().listarTodosOsTiposTransplante();
	}

	protected FatTipoTransplanteDAO getFatTipoTransplanteDAO() {
		return fatTipoTransplanteDAO;
	}

	protected FatExcCnvGrpItemProcON getFatExcCnvGrpItemProcON() {
		return fatExcCnvGrpItemProcON;
	}

	protected RelatorioResumoCobrancaAihON getRelatorioResumoCobrancaAihON() {
		return relatorioResumoCobrancaAihON;
	}

	@Override
	public void persistirExcCnvGrpItemProc(
			final FatExcCnvGrpItemProc excCnvGrpItemProcNew) {
		this.getFatExcCnvGrpItemProcON().persistirExcCnvGrpItemProc(
				excCnvGrpItemProcNew);
	}

	@Override
	public void removerExcCnvGrpItemProc(
			final FatExcCnvGrpItemProc excCnvGrpItemProc) {
		this.getFatExcCnvGrpItemProcON().removerExcCnvGrpItemProc(
				excCnvGrpItemProc);
	}

	protected FatExcCnvGrpItemProcDAO getFatExcCnvGrpItemProcDAO() {
		return fatExcCnvGrpItemProcDAO;
	}

	@Override
	public List<FatExcCnvGrpItemProc> listarFatExcCnvGrpItemProcPorPlanoConvenioProcedInternoETabela(
			final Integer phiSeq, final Short phoSeq,
			final Short cpgCphCspCnvCodigo, final Byte cpgCphCspSeq) {
		return this
				.getFatExcCnvGrpItemProcDAO()
				.listarFatExcCnvGrpItemProcPorPlanoConvenioProcedInternoETabela(
						phiSeq, phoSeq, cpgCphCspCnvCodigo, cpgCphCspSeq);
	}

	@Override
	public Boolean buscaInstrRegistro(final Integer iphSeq,
			final Short iphPhoSeq, final String codRegistro) {
		return getRelatorioResumoCobrancaAihON().buscaInstrRegistro(iphSeq,
				iphPhoSeq, codRegistro);
	}

	@Override
	public String buscaProcedimentoPrConta(final Integer seq,
			final Short phoSeq, final Integer eaiCthSeq, final Long codTabela) {
		return getRelatorioResumoCobrancaAihON().buscaProcedimentoPrConta(seq,
				phoSeq, eaiCthSeq, codTabela);
	}

	@Override
	public List<ResumoCobrancaAihServicosVO> listarAtosMedicos(
			final Integer seq, final Integer cthSeq) {

		return getRelatorioResumoCobrancaAihON().listarAtosMedicos(seq, cthSeq);
	}

	@Override
	public List<ResumoCobrancaAihServicosVO> listarAtosMedicosPrevia(
			final Integer seq, final Integer cthSeq) {
		return getFatItensProcedHospitalarDAO().listarAtosMedicosPrevia(seq,
				cthSeq);
	}

	/*
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#pesquisarEspelhoAih
	 * (java.lang.Integer, java.lang.Boolean)
	 */
	@Override
	public List<ResumoCobrancaAihVO> gerarRelatorioResumoCobrancaAih(
			final Integer cthSeq, final Boolean previa) {
		return getRelatorioResumoCobrancaAihRN()
				.gerarRelatorioResumoCobrancaAih(cthSeq, previa);
	}

	protected RelatorioResumoCobrancaAihRN getRelatorioResumoCobrancaAihRN() {
		return this.relatorioResumoCobrancaAihRN;
	}

	protected FatPossibilidadeRealizadoRN getFatPossibilidadeRealizadoRN() {
		return fatPossibilidadeRealizadoRN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * listarFinanciamentosAtivosPorCodigoOuDescricao(java.lang.Object)
	 */
	@Override
	public List<FatCaractFinanciamento> listarFinanciamentosAtivosPorCodigoOuDescricao(
			Object objPesquisa) {
		return getFatCaractFinanciamentoDAO()
				.listarFinanciamentosAtivosPorCodigoOuDescricao(objPesquisa);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * listarFinanciamentosAtivosPorCodigoOuDescricao(java.lang.Object)
	 */
	@Override
	public List<FatCaractComplexidade> listarComplexidadesAtivasPorCodigoOuDescricao(
			Object objPesquisa) {
		return getFatCaractComplexidadeDAO()
				.listarComplexidadesAtivasPorCodigoOuDescricao(objPesquisa);
	}

	/*
	 */

	@Override
	public List<ResumoCobrancaAihVO> pesquisarEspelhoAih(final Integer cthSeq,
			final Boolean previa) {
		return this.getContaHospitalarON().pesquisarEspelhoAih(cthSeq, previa);
	}

	@Override
	public FatCaractFinanciamento obterCaractFinanciamentoPorSeqEPhoSeqECodTabela(
			final Short iphPhoSeq, final Integer iphSeq, final Long codTabela) {
		return getFatCaractFinanciamentoDAO()
				.obterCaractFinanciamentoPorSeqEPhoSeqECodTabela(iphPhoSeq,
						iphSeq, codTabela);
	}

	@Override
	public FatCaractComplexidade obterCaractComplexidadePorSeqEPhoSeqECodTabela(
			final Short iphPhoSeq, final Integer iphSeq, final Long codTabela) {
		return getFatCaractComplexidadeDAO()
				.obterCaractComplexidadePorSeqEPhoSeqECodTabela(iphPhoSeq,
						iphSeq, codTabela);
	}

	@Override
	public void excluirProcedimentosAmbulatoriaisRealizadosPorAtendimento(
			final AghAtendimentos atendimento) {
		this.getProcedimentosAmbRealizadosON()
				.excluirProcedimentosAmbulatoriaisRealizadosPorAtendimento(
						atendimento);

	}

	@Override
	public void inserirFatProcedAmbRealizado( FatProcedAmbRealizado fatProcedAmbRealizado, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		this.getProcedimentosAmbRealizadosON()
				.inserirFatProcedAmbRealizado(fatProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
	}
	@Override
	public void inserirFatProcedAmbRealizadoAposVerificarEvolucaoEAnamnese( FatProcedAmbRealizado fatProcedAmbRealizado, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		this.getProcedimentosAmbRealizadosON()
				.inserirFatProcedAmbRealizadoAposVerificarEvolucaoEAnamnese(fatProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void atualizarProcedimentoAmbulatorialRealizado(
			final FatProcedAmbRealizado procedAmbRealizado,
			final FatProcedAmbRealizado oldProcedAmbRealizado,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		getProcedimentosAmbRealizadosON()
				.atualizarProcedimentoAmbulatorialRealizado(procedAmbRealizado,
						oldProcedAmbRealizado, nomeMicrocomputador,
						dataFimVinculoServidor, null);
	}

	@Override
	public void excluirProcedimentoAmbulatorialRealizado(
			final FatProcedAmbRealizado procedAmbRealizado,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		getProcedimentosAmbRealizadosON()
				.excluirProcedimentoAmbulatorialRealizado(procedAmbRealizado,
						nomeMicrocomputador, dataFimVinculoServidor, null);
	}

    @Override
	public FatProcedAmbRealizado clonarFatProcedAmbRealizado(
			final FatProcedAmbRealizado procedAmbRealizado)
			throws BaseException {
		return getProcedimentosAmbRealizadosON().clonarFatProcedAmbRealizado(
				procedAmbRealizado);
	}

	@Override
	public void persistirProcedimentoAmbulatorialRealizado(
			final FatProcedAmbRealizado procedAmbRealizado,
			final FatProcedAmbRealizado oldProcedAmbRealizado,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		getProcedimentosAmbRealizadosON()
				.persistirProcedimentoAmbulatorialRealizado(procedAmbRealizado,
						oldProcedAmbRealizado, nomeMicrocomputador,
						dataFimVinculoServidor);
	}

	protected ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON() {
		return procedimentosAmbRealizadosON;

	}

	@Override
	public FatProcedHospInternos obterFatProcedHospInternosPorExamesMaterialAnalise(
			final AelExamesMaterialAnaliseId exaManId) {
		return getFatProcedHospInternosDAO()
				.obterFatProcedHospInternosPorMaterial(exaManId);
	}

	@Override
	public List<FatContasInternacao> obterContasHospitalaresPorInternacaoPacienteDataSolicitacao(
			final Integer intSeq, final Integer pacCodigo,
			final Date dthrSolicitacao, final Short cspCnvCodigo) {
		return getFatContasInternacaoDAO()
				.obterContasHospitalaresPorInternacaoPacienteDataSolicitacao(
						intSeq, pacCodigo, dthrSolicitacao, cspCnvCodigo);
	}

	@Override
	public List<FatContasInternacao> obterContasHospitalaresPorInternacaoPacienteDataSolicitacaoComExamesEspeciais(
			final Integer intSeq, final Integer pacCodigo,
			final Date dthrSolicitacao, final Short cspCnvCodigo) {
		return getFatContasInternacaoDAO()
				.obterContasHospitalaresPorInternacaoPacienteDataSolicitacaoComExamesEspeciais(
						intSeq, pacCodigo, dthrSolicitacao, cspCnvCodigo);
	}

	protected FatContasInternacaoDAO getFatContasInternacaoDAO() {
		return fatContasInternacaoDAO;
	}

	protected FatAihDAO getFatAihDAO() {
		return fatAihDAO;
	}

	@Override
	public List<FatItemContaHospitalar> obterItensContaHospitalarPorContaHospitalarItemSolicitacaoExame(
			final Short iseSeqp, final Integer iseSoeSeqp, final Integer chtSeq) {
		return getFatItemContaHospitalarDAO()
				.listarPorContaHospitalarItemSolicitacaoExame(iseSeqp,
						iseSoeSeqp, chtSeq);
	}

	@Override
	public List<FatItemContaHospitalar> obterItensContaHospitalarPorSolicitacaoExame(
			final Short iseSeqp, final Integer iseSoeSeqp) {
		return getFatItemContaHospitalarDAO().listarPorItemSolicitacaoExame(
				iseSeqp, iseSoeSeqp);
	}

	@Override
	public Short obterProximoSeqFatItensContaHospitalar(final Integer cthSeq) {
		return getFatItemContaHospitalarDAO().obterProximoSeq(cthSeq);
	}
	@Override
    public AghParametros buscarParametroEditarDescPHI() throws BaseException {
    	return this.parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_EDICAO_DESCRICAO_PHI);
    }
	@Override
	public FatCompetencia buscarCompetenciasDataHoraFimNula(
			final DominioModuloCompetencia modulo,
			final DominioSituacaoCompetencia situacao) {
		return getFatCompetenciaDAO().buscarCompetenciasDataHoraFimNula(modulo,
				situacao);
	}

	@Override
	public List<FatProcedAmbRealizado> buscarFatProcedAmbRealizadoPorProcedHospInternosEItemSolicitacaoExame(
			final Short iseSeqp, final Integer iseSoeSeqp, final Integer phiSeq) {
		return getFatProcedAmbRealizadoDAO()
				.buscarPorProcedHospInternosEItemSolicitacaoExame(iseSeqp,
						iseSoeSeqp, phiSeq);
	}

	@Override
	public boolean existeFatProcedAmbRealizadoTransferida(final Short iseSeqp,
			final Integer iseSoeSeqp, final Integer phiSeq) {
		return getFatProcedAmbRealizadoDAO()
				.existeFatProcedAmbRealizadoTransferida(iseSeqp, iseSoeSeqp,
						phiSeq);
	}

	@Override
	public List<FatProcedAmbRealizado> buscarFatProcedAmbRealizadoPorItemSolicitacaoExameNaoFaturados(
			final Short iseSeqp, final Integer iseSoeSeqp) {
		return getFatProcedAmbRealizadoDAO()
				.buscarPorItemSolicitacaoExameNaoFaturados(iseSeqp, iseSoeSeqp);
	}

	@Override
	public List<FatProcedAmbRealizado> buscarFatProcedAmbRealizadoPorItemSolicitacaoExame(
			final Short iseSeqp, final Integer iseSoeSeqp) {
		return getFatProcedAmbRealizadoDAO().buscarPorItemSolicitacaoExame(
				iseSeqp, iseSoeSeqp);
	}

	@Override
	@BypassInactiveModule
	public FatConvenioSaudePlano obterFatConvenioSaudePlano(
			final Short cnvCodigo, final Byte seq) {
		return getFatConvenioSaudePlanoDAO().obterConvenioSaudePlanoPeloId(
				cnvCodigo, seq);
	}

	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}

	protected FatConvenioSaudePlanoDAO getFatConvenioSaudePlanoDAO() {
		return fatConvenioSaudePlanoDAO;
	}

	protected FatConvenioSaudeDAO getFatConvenioSaudeDAO() {
		return fatConvenioSaudeDAO;
	}

	@Override
	public List<VFatContaHospitalarPac> pesquisarVFatContaHospitalarPac(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq,
			final DominioSituacaoConta[] situacoes, final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc) {
		return getVFatContaHospitalarPacDAO().pesquisarVFatContaHospitalarPac(
				pacProntuario, cthNroAih, pacCodigo, cthSeq, situacoes,
				firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarVFatContaHospitalarPacCount(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq,
			final DominioSituacaoConta[] situacoes) {
		return getVFatContaHospitalarPacDAO()
				.pesquisarVFatContaHospitalarPacCount(pacProntuario, cthNroAih,
						pacCodigo, cthSeq, situacoes);
	}

	@Override
	public List<FatDadosContaSemIntVO> pesquisarContaHospitalarParaCobrancaSemInternacao(
			final Integer pacProntuario, final Integer pacCodigo,
			final Integer cthSeq, final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc) {
		return getFatDadosContaSemIntDAO().pesquisarFatDadosContaSemInt(
				pacProntuario, pacCodigo, cthSeq, firstResult, maxResult,
				orderProperty, asc);
	}

	@Override
	public Long pesquisarContaHospitalarParaCobrancaSemInternacaoCount(
			final Integer pacProntuario, final Integer pacCodigo,
			final Integer cthSeq) {
		return getFatDadosContaSemIntDAO().pesquisarFatDadosContaSemIntCount(
				pacProntuario, pacCodigo, cthSeq);
	}

	@Override
	public List<ContaHospitalarInformarSolicitadoVO> pesquisarContaHospitalarInformarSolicitadoVO(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq,
			final DominioSituacaoConta[] situacoes, final Byte seqTipoAih,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
		return getVFatContasHospPacientesDAO()
				.pesquisarContaHospitalarInformarSolicitadoVO(pacProntuario,
						cthNroAih, pacCodigo, cthSeq, situacoes, seqTipoAih,
						firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarVFatContaHospitalarPacCount(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq,
			final DominioSituacaoConta[] situacoes, final String codigoDcih,
			final Date dataInternacaoAdm, final Date dataAltaAdm,
			final VFatAssociacaoProcedimento procedimentoSolicitado,
			final VFatAssociacaoProcedimento procedimentoRealizado) {
		return getVFatContaHospitalarPacDAO()
				.pesquisarVFatContaHospitalarPacCount(pacProntuario, cthNroAih,
						pacCodigo, cthSeq, situacoes, codigoDcih,
						dataInternacaoAdm, dataAltaAdm, procedimentoSolicitado,
						procedimentoRealizado);
	}

	@Override
	public List<VFatContaHospitalarPac> pesquisarVFatContaHospitalarPac(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq,
			final DominioSituacaoConta[] situacoes, final String codigoDcih,
			final Date dataInternacaoAdm, final Date dataAltaAdm,
			final VFatAssociacaoProcedimento procedimentoSolicitado,
			final VFatAssociacaoProcedimento procedimentoRealizado,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
		return getVFatContaHospitalarPacDAO().pesquisarVFatContaHospitalarPac(
				pacProntuario, cthNroAih, pacCodigo, cthSeq, situacoes,
				codigoDcih, dataInternacaoAdm, dataAltaAdm,
				procedimentoSolicitado, procedimentoRealizado, firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarContaHospitalarInformarSolicitadoVOCount(
			final Integer pacProntuario, final Long cthNroAih,
			final Integer pacCodigo, final Integer cthSeq,
			final DominioSituacaoConta[] situacoes, final Byte seqTipoAih) {
		return getVFatContasHospPacientesDAO()
				.pesquisarContaHospitalarInformarSolicitadoVOCount(
						pacProntuario, cthNroAih, pacCodigo, cthSeq, situacoes,
						seqTipoAih);
	}

	@Override
	public void atualizarProcedimentosAmbulatoriaisRealizados(
			final FatProcedAmbRealizado entidade,
			final FatProcedAmbRealizado oldProcedAmbRealizado,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException {
		getProcedimentosAmbRealizadosON()
				.atualizarProcedimentoAmbulatorialRealizado(entidade,
						oldProcedAmbRealizado, nomeMicrocomputador,
						dataFimVinculoServidor, null);
	}

	@Override
	public void inserirProcedimentosAmbulatoriaisRealizados(
			final FatProcedAmbRealizado entidade, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws IllegalStateException,
			BaseException {
		getProcedimentosAmbRealizadosON().inserirFatProcedAmbRealizado(
				entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void removerProcedimentosAmbulatoriaisRealizados(
			final FatProcedAmbRealizado entidade, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws IllegalStateException,
			BaseException {
		getProcedimentosAmbRealizadosON()
				.excluirProcedimentoAmbulatorialRealizado(entidade,
						nomeMicrocomputador, dataFimVinculoServidor, null);
	}

	protected GeracaoArquivoFaturamentoCompetenciaInternacaoON getGeracaoArquivoFaturamentoCompetenciaInternacaoON() {

		return geracaoArquivoFaturamentoCompetenciaInternacaoON;
	}

    protected GeracaoArquivoParcialCompetenciaInternacaoON getGeracaoArquivoParcialCompetenciaInternacaoON() {

		return geracaoArquivoParcialCompetenciaInternacaoON;
	}

	protected GeracaoArquivoTextoInternacaoInterface getGeracaoArquivoTextoInternacaoBean() {

		return (GeracaoArquivoTextoInternacaoInterface) GeracaoArquivoTextoInternacaoBean;
	}

	@Override
	public ArquivoURINomeQtdVO gerarArquivoContasPeriodo() throws BaseException {

		ArquivoURINomeQtdVO result = null;

		result = this.getGeracaoArquivoTextoInternacaoBean()
				.gerarArquivoTextoContasPeriodo();

		return result;
	}

	@Override
	public List<FatMotivoDesdobramento> listarMotivosDesdobramentos(
			final String filtro, final Byte seqTipoAih) {
		return getFatMotivoDesdobramentoDAO().listarMotivosDesdobramentos(
				filtro, seqTipoAih);
	}

	@Override
	@BypassInactiveModule
	public List<FatMotivoDesdobrClinica> pesquisarMotivoDesdobrClinicaPorClinica(
			AghClinicas clinica) {
		return getFatMotivoDesdobrClinicaDAO()
				.pesquisarMotivoDesdobrClinicaPorClinica(clinica);
	}

	@Override
	public Long listarMotivosDesdobramentosCount(final String filtro,
			final Byte seqTipoAih) {
		return getFatMotivoDesdobramentoDAO().listarMotivosDesdobramentosCount(
				filtro, seqTipoAih);
	}

	@Override
	public void desdobrarContaHospitalar(final Integer cthSeq,
			final FatMotivoDesdobramento motivoDesdobramento,
			final Date dataHoraDesdobramento,
			final Boolean contaConsideradaReapresentada,
			final String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		getFaturamentoON().desdobrarContaHospitalar(cthSeq,
				motivoDesdobramento, dataHoraDesdobramento,
				contaConsideradaReapresentada, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	public void estornarDesdobramento(final Integer cthSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		getFaturamentoON().estornarDesdobramento(cthSeq, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	protected VFatContasHospPacientesDAO getVFatContasHospPacientesDAO() {
		return vFatContasHospPacientesDAO;
	}

	@Override
	public Long consultaQuantidadeProcedimentosConsultaPorPhiSeq(
			final Integer phiSeq) throws BaseException {
		return getVFatAssociacaoProcedimentoDAO()
				.consultaQuantidadeProcedimentosConsultaPorPhiSeq(phiSeq);
	}

	@Override
	public List<FatProcedHospInternos> listarProcedHospInternoPorSeqOuDescricao(
			final Object param, final Integer maxResults, final String order) {
		return getFatProcedHospInternosDAO()
				.listarProcedHospInternoPorSeqOuDescricao(param, maxResults,
						order);
	}

	@Override
	public Long listarProcedHospInternoPorSeqOuDescricaoCount(final Object param) {
		return getFatProcedHospInternosDAO()
				.listarProcedHospInternoPorSeqOuDescricaoCount(param);
	}

	@Override
	public List<FatMensagemLog> listarMensagensErro(final Object objPesquisa) {
		return getFatMensagemLogDAO().listarMensagensErro(objPesquisa);
	}

	@Override
	public Long listarMensagensErroCount(final Object objPesquisa) {
		return getFatMensagemLogDAO().listarMensagensErroCount(objPesquisa);
	}

	@Override
	public List<FatMensagemLogId> listarMensagensErro(final Object objPesquisa,
			final DominioModuloMensagem modulo) {
		return getFatMensagemLogDAO().listarMensagensErro(objPesquisa, modulo);
	}

	@Override
	public Long listarMensagensErroCount(final Object objPesquisa,
			final DominioModuloMensagem modulo) {
		return getFatMensagemLogDAO().listarMensagensErroCount(objPesquisa,
				modulo);
	}

	@Override
	public List<FatProcedHospIntCid> pesquisarFatProcedHospIntCidPorPhiSeq(
			final Integer phiSeq) {
		return getFatProcedHospIntCidDAO()
				.pesquisarFatProcedHospIntCidPorPhiSeq(phiSeq);
	}

	@Override
	public FatProcedHospIntCid obterFatProcedHospIntCidPorChavePrimaria(
			final FatProcedHospIntCidId id) {
		return getFatProcedHospIntCidDAO().obterPorChavePrimaria(id);
	}

	protected FatProcedHospIntCidDAO getFatProcedHospIntCidDAO() {
		return fatProcedHospIntCidDAO;
	}

	@Override
	public VFatContasHospPacientes obterVFatContasHospPacientes(
			final Integer cthSeq, final BigDecimal intSeq) {
		return getVFatContasHospPacientesDAO().obterVFatContasHospPacientes(
				cthSeq, intSeq);
	}

	@Override
	public VFatContaHospitalarPac obterVFatContaHospitalarPac(
			final Integer cthSeq) {
		return getVFatContaHospitalarPacDAO().obterPorChavePrimaria(cthSeq,
				true, VFatContaHospitalarPac.Fields.CONTA_HOSPITALAR,
				VFatContaHospitalarPac.Fields.PACIENTE,
				VFatContaHospitalarPac.Fields.MOTIVO_DESDOBRAMENTO,
				VFatContaHospitalarPac.Fields.MOTIVO_REJEICAO_CONTA);
	}

	@Override
	public List<ListarContasHospPacientesPorPacCodigoVO> listarContasHospPacientesPorPacCodigo(
			final Integer pacCodigo, final Short tipoGrupoContaSUS,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) throws BaseException {
		return getFaturamentoON().listarContasHospPacientesPorPacCodigo(
				pacCodigo, tipoGrupoContaSUS, firstResult, maxResult,
				orderProperty, asc);
	}

	@Override
	public Long listarContasHospPacientesPorPacCodigoCount(
			final Integer pacCodigo) throws BaseException {
		return getVFatContasHospPacientesDAO()
				.listarVFatContasHospPacientesPorPacCodigoCount(pacCodigo);
	}

	@Override
	public List<RapServidores> pesquisarAuditores(final String filtro)
			throws ApplicationBusinessException {
		return getFaturamentoON().pesquisarAuditores(filtro);
	}

	@Override
	public Long pesquisarAuditoresCount(final String filtro)
			throws ApplicationBusinessException {
		return getFaturamentoON().pesquisarAuditoresCount(filtro);
	}

	@Override
	public List<VFatAssociacaoProcedimento> pesquisarAssociacoesProcedimentos(
			final String filtro, final Integer phiSeq,
			final Boolean indInternacaoIph, final Short cpgCphCspCnvCodigo,
			final Byte cpgCphCspSeq, final DominioSituacao indSituacaoPhi,
			final DominioSituacao indSituacaoIph,
			final Short tipoGrupoContaSUS, final Short iphPhoSeq) {
		return getVFatAssociacaoProcedimentoDAO()
				.pesquisarAssociacoesProcedimentos(filtro, phiSeq,
						indInternacaoIph, cpgCphCspCnvCodigo, cpgCphCspSeq,
						indSituacaoPhi, indSituacaoIph, tipoGrupoContaSUS,
						iphPhoSeq);
	}

	@Override
	public Long pesquisarAssociacoesProcedimentosCount(final String filtro,
			final Integer phiSeq, final Boolean indInternacaoIph,
			final Short cpgCphCspCnvCodigo, final Byte cpgCphCspSeq,
			final DominioSituacao indSituacaoPhi,
			final DominioSituacao indSituacaoIph,
			final Short tipoGrupoContaSUS, final Short iphPhoSeq) {
		return getVFatAssociacaoProcedimentoDAO()
				.pesquisarAssociacoesProcedimentosCount(filtro, phiSeq,
						indInternacaoIph, cpgCphCspCnvCodigo, cpgCphCspSeq,
						indSituacaoPhi, indSituacaoIph, tipoGrupoContaSUS,
						iphPhoSeq);
	}

	@Override
	public List<Date> listarDatasTransplantes(final Integer pacCodigo) {
		return getFatPacienteTransplantesDAO().listarDatasTransplantes(
				pacCodigo);
	}

	protected VFatAssociacaoProcedimentoDAO getVFatAssociacaoProcedimentoDAO() {
		return vFatAssociacaoProcedimentoDAO;
	}

	protected VFatAssociacaoProcedimento2DAO getVFatAssociacaoProcedimento2DAO() {
		return vFatAssociacaoProcedimento2DAO;
	}

	protected FatPacienteTransplantesDAO getFatPacienteTransplantesDAO() {
		return fatPacienteTransplantesDAO;
	}

	@Override
	public void informarSolicitado(
			final VFatContasHospPacientes contaHospitalar,
			final VFatAssociacaoProcedimento procedimentoSolicitado,
			final Long numeroAIH, final Date dataHoraEmissao,
			final RapServidores medicoAuditor,
			final String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getFaturamentoON().informarSolicitado(contaHospitalar,
				procedimentoSolicitado, numeroAIH, dataHoraEmissao,
				medicoAuditor, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public DominioMensagemEstornoAIH validaEstornoAIH(final Integer seq,
			final Long nrAIH) throws ApplicationBusinessException {
		return this.getFaturamentoON().validaEstornoAIH(seq, nrAIH);
	}

	@Override
	public void estornarAIH(final Integer seq, final Long nrAIH,
			final Boolean reaproveitarAIH, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		this.getFaturamentoON().estornarAIH(seq, nrAIH, reaproveitarAIH,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public RapServidores obterMedicoAuditor(final RapServidores servidor)
			throws ApplicationBusinessException {
		return getFaturamentoON().obterMedicoAuditor(servidor);
	}

	@Override
	@BypassInactiveModule
	public FatContasInternacao obterFatContasInternacaoPorChavePrimaria(
			final Integer seq, Enum... fields) {
		return getFatContasInternacaoDAO().obterPorChavePrimaria(seq, fields);
	}

	@Override
	public List<FatContasInternacao> listarContasInternacao(final Integer cthSeq) {
		return getFatContasInternacaoDAO().listarContasInternacao(cthSeq);
	}

	@Override
	public Long validarInformarSolicitado(
			final DominioGrupoConvenio grupoConvenio, final Integer cthSeq,
			final Integer prontuario, final Integer intSeq,
			final DominioSituacaoConta[] situacoes, final Date dtInicio,
			final Date dtFim) {
		return getFatContasHospitalaresDAO().validarInformarSolicitado(
				grupoConvenio, cthSeq, prontuario, intSeq, situacoes, dtInicio,
				dtFim);
	}

	@Override
	public FatAih obterFatAihPorChavePrimaria(final Long numeroAIH) {
		return getFatAihDAO().obterPorChavePrimaria(numeroAIH);
	}

	@Override
	public List<FatMotivoPendencia> listarMotivosPendenciaPorSeqOuDescricao(
			final Object filtro) {
		return getFatMotivoPendenciaDAO()
				.listarMotivosPendenciaPorSeqOuDescricao(filtro);
	}

	@Override
	public Long listarMotivosPendenciaPorSeqOuDescricaoCount(final Object filtro) {
		return getFatMotivoPendenciaDAO()
				.listarMotivosPendenciaPorSeqOuDescricaoCount(filtro);
	}

	protected FatMotivoPendenciaDAO getFatMotivoPendenciaDAO() {
		return fatMotivoPendenciaDAO;
	}

	@Override
	public List<FatMotivoPendencia> pesquisarMotivosPendencia(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatMotivoPendencia motivoPendencia) {
		return fatMotivoPendenciaDAO.pesquisarMotivosPendencia(firstResult,
				maxResult, orderProperty, asc, motivoPendencia);
	}

	@Override
	public Long pesquisarMotivosPendenciaCount(
			FatMotivoPendencia motivoPendencia) {
		return fatMotivoPendenciaDAO
				.pesquisarMotivosPendenciaCount(motivoPendencia);
	}

	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void persistirFatMotivoPendencia(FatMotivoPendencia motivoPendencia)
			throws ApplicationBusinessException {
		this.fatMotivoPendenciaRN.persistirFatMotivoPendencia(motivoPendencia);
	}

	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void removerFatMotivoPendencia(final Short seq) throws BaseException {
		this.fatMotivoPendenciaRN.removerFatMotivoPendencia(seq);
	}

	@Override
	public void inserirFatPendenciaContaHospitalar(final Integer cthSeq,
			final FatMotivoPendencia motivoPendencia, final Short unfSeq,
			final DominioSituacao indSituacao)
			throws ApplicationBusinessException {
		getFaturamentoON().inserirFatPendenciaContaHospitalar(cthSeq,
				motivoPendencia, unfSeq, indSituacao);
	}

	@Override
	public void alterarFatPendenciaContaHospitalar(
			final FatPendenciaContaHosp pojo)
			throws ApplicationBusinessException {
		getFaturamentoON().alterarFatPendenciaContaHospitalar(pojo);
	}

	@Override
	public FatPendenciaContaHosp obterFatPendenciaContaHosp(
			final FatPendenciaContaHosp fatPCH) {
		return getFatPendenciaContaHospDAO().obterPorChavePrimaria(
				fatPCH.getId());
	}

	@Override
	public List<FatPendenciaContaHosp> listarFatPendenciaContaHospPorCthSeq(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer cthSeq) {
		return getFatPendenciaContaHospDAO()
				.listarFatPendenciaContaHospPorCthSeq(firstResult, maxResult,
						orderProperty, asc, cthSeq);
	}

	@Override
	public Long listarFatPendenciaContaHospPorCthSeqCount(final Integer cthSeq) {
		return getFatPendenciaContaHospDAO()
				.listarFatPendenciaContaHospPorCthSeqCount(cthSeq);
	}

	@Override
	@BypassInactiveModule
	public List<FatProcedHospInternos> listarFatProcedHospInternos(
			final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc,
			final DominioSituacao situacao, final Boolean indOrigemPresc,
			final DominioTipoNutricaoParenteral tipoNutricaoEnteral,
			final Integer pciSeq, final Integer phiSeq,
			final Integer phiSeqAgrupado) {
		return getFatProcedHospInternosDAO().listarFatProcedHospInternos(
				firstResult, maxResults, orderProperty, asc, situacao,
				indOrigemPresc, tipoNutricaoEnteral, pciSeq, phiSeq,
				phiSeqAgrupado);
	}

	@Override
	@BypassInactiveModule
	public Long listarFatProcedHospInternosCount(
			final DominioSituacao situacao, final Boolean indOrigemPresc,
			final DominioTipoNutricaoParenteral tipoNutricaoEnteral,
			final Integer pciSeq, final Integer phiSeq,
			final Integer phiSeqAgrupado) {
		return getFatProcedHospInternosDAO().listarFatProcedHospInternosCount(
				situacao, indOrigemPresc, tipoNutricaoEnteral, pciSeq, phiSeq,
				phiSeqAgrupado);
	}

	protected FatPendenciaContaHospDAO getFatPendenciaContaHospDAO() {
		return fatPendenciaContaHospDAO;
	}

	@Override
	public List<FatProcedHospInternos> listarPhis(final Object objPesquisa) {
		return getFatProcedHospInternosDAO().listarPhis(objPesquisa);
	}

	@Override
	public Long listarPhisCount(final Object objPesquisa) {

		return getFatProcedHospInternosDAO().listarPhisCount(objPesquisa);
	}

	@Override
	public List<FatProcedHospInternos> listarPhis(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final Integer seq,
			final DominioSituacao situacao, final Integer matCodigo,
			final Integer pciSeq, final Short pedSeq, final String csaCodigo,
			final String pheCodigo, final String emaExaSigla,
			final Integer emaManSeq) {

		return getFatProcedHospInternosDAO().listarPhis(firstResult, maxResult,
				orderProperty, asc, seq, situacao, matCodigo, pciSeq, pedSeq,
				csaCodigo, pheCodigo, emaExaSigla, emaManSeq);
	}

	@Override
	public Long listarPhisCount(final Integer seq,
			final DominioSituacao situacao, final Integer matCodigo,
			final Integer pciSeq, final Short pedSeq, final String csaCodigo,
			final String pheCodigo, final String emaExaSigla,
			final Integer emaManSeq) {

		return getFatProcedHospInternosDAO().listarPhisCount(seq, situacao,
				matCodigo, pciSeq, pedSeq, csaCodigo, pheCodigo, emaExaSigla,
				emaManSeq);

	}

	@Override
	public Long pesquisarAihsLiberadasCount(final Long nroAih,
			final Integer cthSeq,
			final List<DominioSituacaoAih> listaDominioSituacaoAih,
			final Date dtEmissao, final Short serVinCodigo,
			final Integer serMatricula) {
		return getFatAihDAO().pesquisarAihsLiberadasCount(nroAih, cthSeq,
				listaDominioSituacaoAih, dtEmissao, serVinCodigo, serMatricula);
	}

	@Override
	public List<FatAih> pesquisarAihsLiberadas(final Integer firstResult,
			final Integer maxResult, final String order, final boolean asc,
			final Long nroAih, final Integer cthSeq,
			final List<DominioSituacaoAih> listaDominioSituacaoAih,
			final Date dtEmissao, final Short serVinCodigo,
			final Integer serMatricula) {
		return getFatAihDAO().pesquisarAihsLiberadas(firstResult, maxResult,
				order, asc, nroAih, cthSeq, listaDominioSituacaoAih, dtEmissao,
				serVinCodigo, serMatricula);
	}

	@Override
	public List<FatAih> pesquisarAihs(final Integer firstResult,
			final Integer maxResult, final String order, final boolean asc,
			final Long nroAih, final Integer cthSeq,
			final List<DominioSituacaoAih> listaDominioSituacaoAih,
			final Date dtEmissao, final Short serVinCodigo,
			final Integer serMatricula, final Date dtCriadoEm) {
		return getFatAihDAO().pesquisarAihs(firstResult, maxResult, order, asc,
				nroAih, cthSeq, listaDominioSituacaoAih, dtEmissao,
				serVinCodigo, serMatricula, dtCriadoEm);
	}

	@Override
	public Long pesquisarAihsCount(final Long nroAih, final Integer cthSeq,
			final List<DominioSituacaoAih> listaDominioSituacaoAih,
			final Date dtEmissao, final Short serVinCodigo,
			final Integer serMatricula, final Date dtCriadoEm) {
		return getFatAihDAO().pesquisarAihsCount(nroAih, cthSeq,
				listaDominioSituacaoAih, dtEmissao, serVinCodigo, serMatricula,
				dtCriadoEm);
	}

	@Override
	public List<FatCompetencia> listarFatCompetencia(
			final FatCompetencia competencia, final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc) {
		return getFatCompetenciaDAO().listarFatCompetencia(competencia,
				firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long listarFatCompetenciaCount(final FatCompetencia competencia) {
		return getFatCompetenciaDAO().listarFatCompetenciaCount(competencia);
	}

	@Override
	public FatCompetencia obterCompetenciaModuloMesAnoDtHoraInicioSemHora(
			final DominioModuloCompetencia modulo, final Integer mes,
			final Integer ano, final Date dtHoraInicio) {
		return getFatCompetenciaDAO()
				.obterCompetenciaModuloMesAnoDtHoraInicioSemHora(modulo, mes,
						ano, dtHoraInicio);
	}

	@Override
	public void atualizarFatCompetencia(final FatCompetencia competencia)
			throws ApplicationBusinessException {
		getFatCompetenciaRN().atualizarFatCompetencia(competencia);
	}

	@Override
	public List<FatProcedHospInternos> listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricao(
			final Object param, final String ordem,
			final DominioSituacao situacao) {
		return getFatProcedHospInternosDAO()
				.listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricao(
						param, ordem, situacao);
	}

	@Override
	public Long listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricaoCount(
			final Object param, final DominioSituacao situacao) {
		return getFatProcedHospInternosDAO()
				.listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricaoCount(
						param, situacao);
	}

	@Override
	public void persistirProcedimentoHospitalarInterno(
			final FatProcedHospInternos procedHospInterno) throws BaseException {
		getFatProcedHospInternosON().persistirFatCompatExclusItem(
				procedHospInterno);
	}

	private FatCompetenciaRN getFatCompetenciaRN() {
		return fatCompetenciaRN;
	}

	protected FatProcedHospInternoON getFatProcedHospInternosON() {
		return fatProcedHospInternoON;
	}

	@Override
	public void reinternarContaHospitalar(final Integer cthSeq,
			final Integer pacCodigo, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {

		this.getContaHospitalarON().reinternarContaHospitalar(cthSeq,
				pacCodigo, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public List<Long> gerarFatAih(final Long nroAihInicial,
			final Long nroAihFinal) {
		return getFaturamentoRN().gerarFatAih(nroAihInicial, nroAihFinal);
	}

	@Override
	public List<FatAih> pesquisarAihsIntervalo(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final Long nroAihInicial, final Long nroAihFinal) {
		return getFatAihDAO().pesquisarAihsIntervalo(firstResult, maxResult,
				orderProperty, asc, nroAihInicial, nroAihFinal);
	}

	@Override
	public Long pesquisarAihsIntervaloCount(final Long nroAihInicial,
			final Long nroAihFinal) {
		return getFatAihDAO().pesquisarAihsIntervaloCount(nroAihInicial,
				nroAihFinal);
	}

	@Override
	public Integer gravarFatAihLote(final List<Long> novasAihs)
			throws BaseException {
		return getFaturamentoRN().gravarFatAihLote(novasAihs);
	}

	@Override
	public void persistirGrupoItemConvenio(final FatConvGrupoItemProced newFat,
			final FatConvGrupoItemProced oldFat,
			final DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {
		getConvenioGrupoItemProcedimentoON().persistirGrupoItemConvenio(newFat,
				oldFat, operacao);
	}

	@Override
	public void excluirGrupoItemConvenio(final FatConvGrupoItemProced newFat)
			throws ApplicationBusinessException {
		getConvenioGrupoItemProcedimentoON().excluirGrupoItemConvenio(newFat);
	}

	@Override
	public List<LogInconsistenciasInternacaoVO> getLogsInconsistenciasInternacaoVO(
			final Date dtCriacaoIni, final Date dtCriacaoFim,
			final Date dtPrevia, final Integer prontuario,
			final Integer cthSeq, final String inconsistencia,
			final String iniciaisPaciente, final Boolean reapresentada,
			final DominioGrupoProcedimento grupoProcedimento)
			throws ApplicationBusinessException {

		return getFatLogErrorDAO().getLogsInconsistenciasInternacaoVO(
				dtCriacaoIni, dtCriacaoFim, dtPrevia, prontuario, cthSeq,
				inconsistencia, iniciaisPaciente, reapresentada,
				grupoProcedimento);
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarVFatAssociacaoProcedimentoPorProcedHospInt(
			final Integer phiSeq) throws ApplicationBusinessException {
		return getVFatAssociacaoProcedimentoDAO()
				.listarVFatAssociacaoProcedimentoPorProcedHospInt(phiSeq);
	}

	@Override
	public List<FatProcedAmbRealizado> listarProcedimentosAmbulatoriaisRealizados(
			final Integer numeroConsulta) {
		return getFatProcedAmbRealizadoDAO()
				.listarProcedimentosAmbulatoriaisRealizados(numeroConsulta,
						null);
	}

	@Override
	public List<FatProcedAmbRealizado> listarProcedimentosAmbulatoriaisRealizados(
			final Integer numeroConsulta,
			final DominioSituacaoProcedimentoAmbulatorio[] situacoesIgnoradas) {
		return getFatProcedAmbRealizadoDAO()
				.listarProcedimentosAmbulatoriaisRealizados(numeroConsulta,
						situacoesIgnoradas);
	}

	private FatLogErrorDAO getFatLogErrorDAO() {
		return fatLogErrorDAO;
	}

	@Override
	public List<FatCompetencia> listarCompetenciaModuloMesAnoDtHoraInicioSemHora(
			final FatCompetenciaId id) {
		return getFatCompetenciaDAO()
				.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(id);
	}

	@Override
	public Long listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(
			final FatCompetenciaId id) {
		return getFatCompetenciaDAO()
				.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(id);
	}

	@Override
	public List<AIHFaturadaPorPacienteVO> obterAIHsFaturadasPorPaciente(
			final Date dtHrInicio, final Integer ano, final Integer mes,
			final String iniciaisPaciente, final Boolean reapresentada,
			final Integer clinica) {
		return getFatDocumentoCobrancaAihsDAO().obterAIHsFaturadasPorPaciente(
				dtHrInicio, ano, mes, iniciaisPaciente, reapresentada, clinica);
	}

	@Override
	public List<FatProcedHospInternos> obterProcedimentoLimitadoPeloMaterial(
			final Object objPesquisa) throws BaseException {
		return getFatProcedHospInternosDAO()
				.obterProcedimentoLimitadoPeloMaterial(objPesquisa);
	}

	@Override
	public Long obterProcedimentoLimitadoPeloMaterialCount(
			final Object objPesquisa) throws BaseException {
		return getFatProcedHospInternosDAO()
				.obterProcedimentoLimitadoPeloMaterialCount(objPesquisa);
	}

	@Override
	public List<TotaisPorDCIHVO> obterTotaisPorDCIH(final Date dtHrInicio,
			final Integer ano, final Integer mes) {
		return getFatContasHospitalaresDAO().obterTotaisPorDCIH(dtHrInicio,
				ano, mes);
	}

	@Override
	public List<FaturamentoPorProcedimentoVO> obterFaturamentoPorProcedimento(
			final Date dtHrInicio, final Integer ano, final Integer mes) {
		return getFatContasHospitalaresDAO().obterFaturamentoPorProcedimento(
				dtHrInicio, ano, mes);
	}

	@Override
	public FaturamentoPorProcedimentoVO obterFaturamentoPorProcedimentoUTIEspelho(
			final Date dtHrInicio, final Integer ano, final Integer mes) {
		return getFatContasHospitalaresDAO()
				.obterFaturamentoPorProcedimentoUTIEspelho(dtHrInicio, ano, mes);
	}

	@Override
	public String geraCSVRelatorioTotaisDCIH(final FatCompetencia competencia)
			throws IOException {
		return getRelatorioCSVFaturamentoON().geraCSVRelatorioTotaisDCIH(
				competencia);
	}

	@Override
	public String geraCSVRelatorioValoresAIHPorDCIH(final Integer ano,
			final Integer mes) throws IOException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioValoresAIHPorDCIH(ano, mes);
	}

	@Override
	public File geraCSVRelatorioNutricaoEnteralDigitada(String data1, String data2,FatCompetenciaId id) throws IOException, ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioNutricaoEnteralDigitada(data1,data2, id);
	}

	@Override
	public File geraCSVContas(String data1, String data2,FatCompetenciaId id) throws IOException, ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVContas(data1,data2, id);
	}
	

	@Override
	public String gerarCSVRelatorioAihsFaturadas(final Integer cthSeq,
			final Integer prontuario, final Integer mes, final Integer ano,
			final Date dthrInicio, final Long codTabelaIni,
			final Long codTabelaFim, final String iniciais,
			final Boolean reapresentada) throws IOException, BaseException {
		return getRelatorioAihFaturadaON().gerarCSV(cthSeq, prontuario, mes,
				ano, dthrInicio, codTabelaIni, codTabelaFim, iniciais,
				reapresentada);
	}

	protected RelatorioCSVFaturamentoON getRelatorioCSVFaturamentoON() {
		return relatorioCSVFaturamentoON;
	}

	@Override
	public List<AghCid> listarPorItemProcedimentoHospitalarEConvenio(
			final Short iphPhoSeq, final Integer iphSeq,
			final Short cpgCphCspCnvCodigo, final String order) {
		return this.getVFatAssocProcCidsDAO()
				.listarPorItemProcedimentoHospitalarEConvenio(iphPhoSeq,
						iphSeq, cpgCphCspCnvCodigo, order);
	}

	@Override
	public List<FatMotivoRejeicaoConta> pesquisarMotivosRejeicaoConta(
			final String filtro, final DominioSituacao situacao,
			final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc) {
		return this.getFatMotivoRejeicaoContaDAO()
				.pesquisarMotivosRejeicaoConta(filtro, situacao, firstResult,
						maxResults, orderProperty, asc);
	}

	@Override
	public List<FatItensProcedHospitalar> listarPorCidEConvenio(
			final String cidCodigo, final Short cpgCphCspCnvCodigo,
			final String campoOrder, final Boolean order) {
		return this.getVFatAssocProcCidsDAO().listarPorCidEConvenio(cidCodigo,
				cpgCphCspCnvCodigo, campoOrder, order);
	}

	@Override
	public Long pesquisarMotivosRejeicaoContaCount(final String filtro,
			final DominioSituacao situacao) {
		return this.getFatMotivoRejeicaoContaDAO()
				.pesquisarMotivosRejeicaoContaCount(filtro, situacao);
	}

	protected VFatAssocProcCidsDAO getVFatAssocProcCidsDAO() {
		return vFatAssocProcCidsDAO;
	}

	protected FatMotivoRejeicaoContaDAO getFatMotivoRejeicaoContaDAO() {
		return fatMotivoRejeicaoContaDAO;
	}

	@Override
	public List<FatCompatExclusItem> listarFatCompatExclusItemPorIphCompatibilizaEIndSituacaoEIndComparacao(
			final Short iphPhoSeqCompatibiliza,
			final Integer iphSeqCompatibiliza,
			final DominioIndComparacao indComparacao,
			final DominioSituacao iphIndSituacao, final String colunaOrder,
			final Boolean order) {
		return this
				.getFatCompatExclusItemDAO()
				.listarFatCompatExclusItemPorIphCompatibilizaEIndSituacaoEIndComparacao(
						iphPhoSeqCompatibiliza, iphSeqCompatibiliza,
						indComparacao, iphIndSituacao, colunaOrder, order);
	}

	@Override
	public List<FatVlrItemProcedHospComps> obterListaValorItemProcHospCompPorPhoIphAbertos(
			final Short phoSeq, final Integer iphSeq) {
		return this
				.getFatVlrItemProcedHospCompsDAO()
				.obterListaValorItemProcHospCompPorPhoIphAbertos(phoSeq, iphSeq);
	}

	@Override
	public List<ValoresAIHPorDCIHVO> obterValoresAIHPorDCIH(final Integer ano,
			final Integer mes) {
		return getFatContasHospitalaresDAO().obterValoresAIHPorDCIH(ano, mes);
	}

	@Override
	public void reapresentarContaHospitalar(final Integer cthSeq,
			final FatMotivoRejeicaoConta motivoRejeicaoConta,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		this.getReapresentarContaHospitalarRN().reapresentarContaHospitalar(
				cthSeq, motivoRejeicaoConta, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	public void desfazerReapresentacaoContaHospitalar(final Integer cthSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		this.getReapresentarContaHospitalarRN()
				.desfazerReapresentacaoContaHospitalar(cthSeq,
						nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void rejeitarContaHospitalar(final Integer cthSeq,
			final FatMotivoRejeicaoConta motivoRejeicaoConta,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getReapresentarContaHospitalarRN().rejeitarContaHospitalar(cthSeq,
				motivoRejeicaoConta, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	protected ReapresentarContaHospitalarRN getReapresentarContaHospitalarRN() {
		return reapresentarContaHospitalarRN;
	}

	protected FatContaInternacaoRN getFatContaInternacaoRN() {
		return fatContaInternacaoRN;
	}

	@Override
	public List<FatAtoMedicoAih> buscarAtosMedicosEspelho(final Integer cthSeq,
			final int firstResult, final int maxResults,
			final String orderProperty, final boolean asc) {
		return getFaturamentoRN().buscarAtosMedicosEspelho(cthSeq, firstResult,
				maxResults, orderProperty, asc);
	}

	@Override
	@BypassInactiveModule
	public Boolean verificarCaracteristicaExame(final Integer seqIph,
			final Short seqIphPho, final DominioFatTipoCaractItem caracteristica) {
		return getFaturamentoRN().verificarCaracteristicaExame(seqIph,
				seqIphPho, caracteristica);
	}

	@Override
	public Boolean encerrarContasHospitalares(final boolean isScheduled,
			final Integer cth, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final AghJobDetail job)
			throws BaseException {
		return getContaHospitalarON().encerrarContasHospitalares(isScheduled,
				cth, nomeMicrocomputador, dataFimVinculoServidor, job);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Boolean encerrarContasHospitalares(final Integer cth, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final AghJobDetail job)
			throws BaseException {
		return getEncerramentoContaHospitalarON().encerrarContasHospitalares(cth,
				nomeMicrocomputador, dataFimVinculoServidor, job);
	}

	@Override
	public List<Integer> getContasEncerramentoEmLote() {
		return getContaHospitalarON().getContasEncerramentoEmLote();
	}

	@Override
	public List<RelacaoDeOPMNaoFaturadaVO> obterRelacaoDeOPMNaoFaturada(
			final Long procedimento, final Integer ano, final Integer mes,
			final Date dtHrInicio, final Long SSM,
			final String iniciaisPaciente, final Boolean reapresentada)
			throws ApplicationBusinessException {
		return getFatContasHospitalaresDAO().obterRelacaoDeOPMNaoFaturada(
				procedimento, ano, mes, dtHrInicio, SSM, iniciaisPaciente,
				reapresentada);
	}
	
	@Override
	public List<RelacaoDeOrtesesProtesesVO> obterRelacaoDeOrtesesProteses(
			final Long procedimento, 
			final Integer ano, 
			final Integer mes,
			final Date dtHrInicio, 
			final String iniciaisPaciente,
			final Date dtIni,
			final Date dtFim)
			throws ApplicationBusinessException {
		return fatContasHospitalaresON.obterRelacaoDeOrtesesProteses(
				procedimento, ano, mes, dtHrInicio, iniciaisPaciente, dtIni, dtFim);
	}
	
	@Override
	public List<AihsFaturadasPorClinicaVO> obterAihsFaturadasPorClinica(
			final Integer ano, 
			final Integer mes,
			final Date dtHrInicio, 
			final String iniciaisPaciente)
			throws ApplicationBusinessException {
		return getFatContasHospitalaresDAO().obterAihsFaturadasPorClinica(ano, mes, dtHrInicio, iniciaisPaciente);
	}
	

	@Override
	public String geraCSVRelatorioRelacaoDeOPMNaoFaturada(
			final Long procedimento, final Integer ano, final Integer mes,
			final Date dtHrInicio, final Long SSM,
			final String iniciaisPaciente, final Boolean reapresentada)
			throws IOException, ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON().geraCSVRelatorioRelacaoDeOPMNaoFaturada(procedimento, ano,
						mes, dtHrInicio, SSM, iniciaisPaciente, reapresentada);
	}
	
	@Override
	public String geraCSVRelatorioRelacaoOrtesesProteses(
			final Long procedimento, 
			final Integer ano, 
			final Integer mes,
			final Date dtHrInicio, 
			final String iniciaisPaciente,
			final Date dtIni,
			final Date dtFim)
			throws IOException, ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON().geraCSVRelatorioRelacaoOrtesesProteses(procedimento, ano, mes, dtHrInicio, iniciaisPaciente, dtIni, dtFim);
	}
	
	@Override
	public String geraCSVRelatorioAihsFaturadasPorClinica(
			final Integer ano, 
			final Integer mes,
			final Date dtHrInicio, 
			final String iniciaisPaciente)
			throws IOException, ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON().geraCSVRelatorioAihsFaturadasPorClinica(ano, mes, dtHrInicio, iniciaisPaciente);
	}
	

	protected RelatorioAihFaturadaON getRelatorioAihFaturadaON() {
		return relatorioAihFaturadaON;
	}

	@Override
	public List<FatContaSugestaoDesdobr> pesquisarSugestoesDesdobramento(
			final Date dataHoraSugestao, final String origem,
			final String leito, final Integer prontuario,
			final Boolean considera,
			final DominioSituacaoConta[] situacoesContaHospitalar,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
		return this.getFatContaSugestaoDesdobrDAO()
				.pesquisarSugestoesDesdobramento(dataHoraSugestao, origem,
						leito, prontuario, considera, situacoesContaHospitalar,
						firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarSugestoesDesdobramentoCount(
			final Date dataHoraSugestao, final String origem,
			final String leito, final Integer prontuario,
			final Boolean considera,
			final DominioSituacaoConta[] situacoesContaHospitalar) {
		return this.getFatContaSugestaoDesdobrDAO()
				.pesquisarSugestoesDesdobramentoCount(dataHoraSugestao, origem,
						leito, prontuario, considera, situacoesContaHospitalar);
	}

	protected FatContaSugestaoDesdobrDAO getFatContaSugestaoDesdobrDAO() {
		return fatContaSugestaoDesdobrDAO;
	}

	@Override
	public FatDadosContaSemInt obterFatDadosContaSemInt(final Integer seq) {
		return getFatDadosContaSemIntDAO().obterFatDadosContaSemIntPorSeq(seq);
	}

	@Override
	@BypassInactiveModule
	public FatConvenioSaude obterFatConvenioSaudePorId(final Short convCodigo) {
		return getFatConvenioSaudeDAO().obterPorChavePrimaria(convCodigo);
	}

	@Override
	public List<VFatContasHospPacientes> listarContasPorPacCodigoDthrRealizadoESituacaoCth(
			final Integer pacCodigo, final Date dthrRealizado,
			final DominioSituacaoConta[] cthIndSituacao,
			final String colunaOrder, final Boolean order) {
		return getVFatContasHospPacientesDAO()
				.listarContasPorPacCodigoDthrRealizadoESituacaoCth(pacCodigo,
						dthrRealizado, cthIndSituacao, colunaOrder, order);
	}

	public List<Integer> gerarSugestoesDesdobramento() throws BaseException {
		return getGerarSugestoesDesdobramentoON().gerarSugestoesDesdobramento();
	}

	@Override
	public Boolean geraSugestaoDesdobramentoContaHospitalar(final Integer cthSeq)
			throws BaseException {
		return getGerarSugestoesDesdobramentoON()
				.geraSugestaoDesdobramentoContaHospitalar(cthSeq);
	}

	@Override
	public Boolean geraSugestaoDesdobramentoContaHospitalar(Integer pCthSeq,
			Byte pMdsSeq) throws BaseException {
		return getGerarSugestoesDesdobramentoON().fatcRnCsdcCadSugDesd(pCthSeq,
				pMdsSeq);
	}

	@Override
	public void geraSugestaoDesdobramentoParaCirAgendada() throws BaseException {
		getGerarSugestoesDesdobramentoON().fatpRnCsdSugDesdCrg();
	}

	@Override
	public List<CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO> obterCirurgiasAgendadas() {
		return getGerarSugestoesDesdobramentoON().obterCirurgiasAgendadas();
	}

	@Override
	@BypassInactiveModule
	public void removerFatContaSugestaoDesdobr(
			final FatContaSugestaoDesdobr contaSugestaoDesdobr,
			final boolean flush) {
		this.getFaturamentoRN().removerFatContaSugestaoDesdobr(
				contaSugestaoDesdobr, true);
	}

	@Override
	public void removerFatContaSugestaoDesdobrPorId(
			final FatContaSugestaoDesdobrId id, final boolean flush) {
		this.getFaturamentoRN().removerFatContaSugestaoDesdobrPorId(id, true);
	}

	@Override
	public FatContaSugestaoDesdobr obterFatContaSugestaoDesdobrPorChavePrimaria(
			final FatContaSugestaoDesdobrId id) {
		return this.getFatContaSugestaoDesdobrDAO().obterPorChavePrimaria(id, FatContaSugestaoDesdobr.Fields.FAT_MOTIVO_DESDOBRAMENTO);
	}

	@Override
	public void atualizarFatContaSugestaoDesdobr(
			final FatContaSugestaoDesdobr contaSugestaoDesdobr,
			final boolean flush) throws BaseException {
		this.getFaturamentoON().atualizarFatContaSugestaoDesdobr(
				contaSugestaoDesdobr, flush);
	}

	@Override
	public List<FatCompatExclusItem> listaCompatExclusItem(
			final Short iphPhoSeq, final Integer iphSeq,
			final DominioIndComparacao indComparacao,
			final DominioIndCompatExclus indCompatExclus,
			final DominioSituacao indSituacao, final String order) {
		return getFatCompatExclusItemDAO().listaCompatExclusItem(iphPhoSeq,
				iphSeq, indComparacao, indCompatExclus, indSituacao, order);
	}

	@Override
	public String verFatCompatItem(final Short phoSeqCont,
			final Integer iphSeqCont, final Short phoSeqComp,
			final Integer iphSeqComp) {
		return getItensProcedHospitalarRN().verFatCompatItem(phoSeqCont,
				iphSeqCont, phoSeqComp, iphSeqComp);
	}

	@Override
	public List<FatCompatExclusItem> pesquisarExcludencia(final Short phoComp,
			final Integer iphComp, final DominioIndComparacao indComparacao,
			final DominioSituacao indSituacao) {
		return getFatCompatExclusItemDAO().pesquisarExcludencia(phoComp,
				iphComp, indComparacao, indSituacao);
	}

	@Override
	public void inserirFatDadosContaSemInt(final FatDadosContaSemInt entidade,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		getFatDadosContaSemIntPersist().inserir(entidade, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	public void atualizarFatDadosContaSemInt(final FatDadosContaSemInt fdsi,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		getFatDadosContaSemIntPersist().atualizar(fdsi, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	public FatDadosContaSemInt fatcGeraContaSemInt(
			final AghEspecialidades especialidade, final Date dtRealizado,
			final AipPacientes paciente, final AghUnidadesFuncionais un,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return getFatDadosContaSemIntRN().fatcGeraContaSemInt(especialidade,
				dtRealizado, paciente, un, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	private FatDadosContaSemIntRN getFatDadosContaSemIntRN() {
		return fatDadosContaSemIntRN;
	}

	private FatDadosContaSemIntPersist getFatDadosContaSemIntPersist() {
		return fatDadosContaSemIntPersist;
	}

	@Override
	public FatContasInternacao obterContaInternacaoEmFatDadosContaSemInt(
			final Integer dcsSeq, final Integer cthSeq) {
		return getFatContasInternacaoDAO()
				.obterContaInternacaoEmFatDadosContaSemInt(dcsSeq, cthSeq);
	}

	@Override
	public List<FatItemContaHospitalar> obterItensContaHospitalarPorItemRmps(
			final Integer rmpSeq, final Short numero) {
		return this.getFatItemContaHospitalarDAO()
				.obterItensContaHospitalarPorItemRmps(rmpSeq, numero);
	}

	@Override
	public FatItemContaHospitalar obterItensContaHospitalarPorContaHospitalarePHI(
			final Integer cthSeq, final Integer phiSeq) {
		return this
				.getFatItemContaHospitalarDAO()
				.obterItensContaHospitalarPorContaHospitalarePHI(cthSeq, phiSeq);
	}

	@Override
	public boolean validaNumeroAIHInformadoManualmente(final Long numeroAIH)
			throws ApplicationBusinessException {
		return getFaturamentoRN()
				.validaNumeroAIHInformadoManualmente(numeroAIH);
	}

	protected FatLogInterfaceDAO getFatLogInterfaceDAO() {
		return fatLogInterfaceDAO;
	}

	@Override
	@BypassInactiveModule
	public void inserirFatLogInterface(final FatLogInterface fli) {
		this.getFatLogInterfaceDAO().persistir(fli);
	}

	@Override
	public String geraCSVRelatorioLogInconsistenciasInternacao(
			final Date dtCriacaoIni, final Date dtCriacaoFim,
			final Date dtPrevia, final Integer pacProntuario,
			final Integer cthSeq, final String inconsistencia,
			final String iniciaisPaciente, final Boolean reapresentada,
			final DominioGrupoProcedimento grupoProcedimento)
			throws IOException, ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioLogInconsistenciasInternacao(dtCriacaoIni,
						dtCriacaoFim, dtPrevia, pacProntuario, cthSeq,
						inconsistencia, iniciaisPaciente, reapresentada,
						grupoProcedimento);
	}

	@Override
	public String geraCSVRelatorioAIHFaturadaPorPaciente(final Date dtHrInicio,
			final Integer ano, final Integer mes,
			final String iniciaisPaciente, final Boolean reapresentada,
			final Integer clinica) throws IOException,
			ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioAIHFaturadaPorPaciente(dtHrInicio, ano, mes,
						iniciaisPaciente, reapresentada, clinica);
	}

	@Override
	public String geraCSVRelatorioContasPreenchimentoLaudos(
			final Date dtPrevia, final Short unfSeq,
			final String iniciaisPaciente) throws IOException,
			ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioContasPreenchimentoLaudos(dtPrevia, unfSeq,
						iniciaisPaciente);
	}

	protected FatkInterfacePrescricaoRN getFatkInterfacePrescricaoRN() {
		return fatkInterfacePrescricaoRN;
	}

	@Override
	@BypassInactiveModule
	public void atualizaContaHospitalar(final Integer atdSeq,
			final Integer prescricaoMedicaSeq, final Date dataInicio,
			final Date dataFim, final Date dataInicioMovimentoPendente,
			final DominioOperacaoBanco operacao,
			final Date dataFimVinculoServidor) throws BaseException {
		getFatkInterfacePrescricaoRN().atualizaContaHospitalar(atdSeq,
				prescricaoMedicaSeq, dataInicio, dataFim,
				dataInicioMovimentoPendente, operacao, dataFimVinculoServidor);
	}

	@Override
	public Map<String, Integer> buscarDadosInicias(final Integer cthSeq) {
		return this.getFatItemContaHospitalarDAO().buscarDadosInicias(cthSeq);
	}

	@Override
	public Long geracaoSugestoesDesdobramentoCount() {
		return this.getGerarSugestoesDesdobramentoON()
				.geracaoSugestoesDesdobramentoCount();
	}

	protected GerarSugestoesDesdobramentoON getGerarSugestoesDesdobramentoON() {
		return gerarSugestoesDesdobramentoON;
	}

	@Override
	public List<RelatorioIntermediarioLancamentosContaVO> obterItensContaParaRelatorioIntermediarioLancamentos(
			final Integer cthSeq,
			Map<AghuParametrosEnum, AghParametros> parametros)
			throws ApplicationBusinessException {
		return getFatItemContaHospitalarDAO()
				.obterItensContaParaRelatorioIntermediarioLancamentos(cthSeq,
						parametros);
	}

	@Override
	public String geraCSVRelatorioIntermediarioLancamentosConta(
			final Integer cthSeq) throws IOException,
			ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioIntermediarioLancamentosConta(cthSeq);
	}

	@Override
	public String geraCSVRelatorioFaturamentoPorProcedimento(
			final FatCompetencia competencia) throws IOException,
			ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioFaturamentoPorProcedimento(competencia);
	}

	@Override
	public String gerarCSVRelatorioArquivoProcedimento() throws IOException,
			ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.gerarCSVRelatorioArquivoProcedimento();
	}

	@Override
	public List<FatEspelhoAih> obterFatEspelhoAihPorCth(final Integer cthSeq) {
		return getFatEspelhoAihDAO().obterFatEspelhoAihPorCth(cthSeq);
	}

	@Override
	public List<ContasPreenchimentoLaudosVO> obterContasPreenchimentoLaudos(
			final Date dtUltimaPrevia, final Short unfSeq,
			final String iniciaisPaciente) {
		return getContaHospitalarON().obterContasPreenchimentoLaudos(
				dtUltimaPrevia, unfSeq, iniciaisPaciente);
	}

	@Override
	public List<AihPorProcedimentoVO> obterAihsPorProcedimentoVO(
			final Long procedimentoInicial, final Long procedimentoFinal,
			final Date dtHrInicio, final Integer mes, final Integer ano,
			final String iniciaisPaciente, final boolean reapresentada) {
		return getFatEspelhoAihDAO().obterAihsPorProcedimentoVO(
				procedimentoInicial, procedimentoFinal, dtHrInicio, mes, ano,
				iniciaisPaciente, reapresentada);
	}

	@Override
	public String geraCSVRelatorioAIHPorProcedimento(
			final Long procedimentoInicial, final Long procedimentoFinal,
			final Date dtHrInicio, final Integer mes, final Integer ano,
			final String iniciaisPaciente, final boolean reapresentada)
			throws IOException, ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioAIHPorProcedimento(procedimentoInicial,
						procedimentoFinal, dtHrInicio, mes, ano,
						iniciaisPaciente, reapresentada);
	}

	@Override
	public String geraCSVRelatorioPreviaDiariaFaturamento(
			final FatCompetencia competencia) throws IOException,
			ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioPreviaDiariaFaturamento(competencia);
	}

	@Override
	public List<PreviaDiariaFaturamentoVO> obterPreviaDiariaFaturamento(
			final FatCompetencia competencia, final boolean isPDF)
			throws ApplicationBusinessException {
		return getProcedimentosAmbRealizadosON().obterPreviaDiariaFaturamento(
				competencia, isPDF);
	}

	@Override
	public String geraCSVRelatorioLogInconsistenciaBPA(
			final DominioModuloMensagem modulo, final DominioSituacao situacao)
			throws IOException, ApplicationBusinessException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioLogInconsistenciaBPA(modulo, situacao);
	}

	@Override
	public List<FatContaSugestaoDesdobr> pesquisarSugestoesDesdobramento(
			final Date dataHoraSugestao, final Integer mdsSeq,
			final String descricao, final Integer cthSeq,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
		return getFatContaSugestaoDesdobrDAO().pesquisarSugestoesDesdobramento(
				dataHoraSugestao, mdsSeq, descricao, cthSeq, firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarSugestoesDesdobramentoCount(
			final Date dataHoraSugestao, final Integer mdsSeq,
			final String descricao, final Integer cthSeq) {
		return getFatContaSugestaoDesdobrDAO()
				.pesquisarSugestoesDesdobramentoCount(dataHoraSugestao, mdsSeq,
						descricao, cthSeq);
	}

	@Override
	public List<LogInconsistenciaBPAVO> obterLogInconsistenciaBPAVO(
			final DominioModuloMensagem modulo, final String erro,
			final DominioSituacaoMensagemLog situacao, final Short pIphPhoSeq,
			final Short pTipoGrupoContaSUS, final Short pCpgCphCspSeq,
			final Short pCpgCphCspCnvCodigo)
			throws ApplicationBusinessException {
		return getFatMensagemLogDAO().obterLogInconsistenciaBPAVO(modulo, erro,
				situacao, pIphPhoSeq, pTipoGrupoContaSUS, pCpgCphCspSeq,
				pCpgCphCspCnvCodigo);
	}

	@Override
	public List<String> obterLogInconsistenciaBPACSV(
			final DominioModuloCompetencia[] modulo, final String[] erros,
			final String erro, final DominioSituacao situacao,
			final Short pCpgCphCspSeq, final Short pCpgCphCspCnvCodigo,
			final Short pIphPhoSeq) throws ApplicationBusinessException {
		return getFatMensagemLogDAO().obterLogInconsistenciaBPACSV(modulo,
				erros, erro, situacao, pCpgCphCspSeq, pCpgCphCspCnvCodigo,
				pIphPhoSeq);
	}

	@Override
	@BypassInactiveModule
	public FatConvenioSaudePlano obterConvenioPlanoInternacao(
			final Short cnvCodigo) {
		return getFatConvenioSaudePlanoDAO().obterConvenioPlanoInternacao(
				cnvCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<FatConvenioSaudePlano> pesquisarConvenioPlano(
			final Integer firstResult, final Integer maxResult,
			final String strPesquisa) {
		return getFatConvenioSaudePlanoDAO().pesquisarConvenioPlano(
				firstResult, maxResult, strPesquisa);
	}

	@Override
	@BypassInactiveModule
	public List<Long> pesquisarFatAssociacaoProcedimentos(final Integer cidSeq) {
		return getFatItensProcedHospitalarDAO()
				.pesquisarFatAssociacaoProcedimentos(cidSeq);
	}

	@Override
	@BypassInactiveModule
	public List<FatVlrItemProcedHospComps> pesquisarProcedimentos(
			final Object strPesquisa, final Short parametroProcedimento,
			final AipPacientes paciente, final Integer cidSeq)
			throws ApplicationBusinessException {

		final AghParametros parametroPedAdu = this.parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_SEQ_FAT_CARACT_ACEITA_ADULTO_PED);
		final Integer carctAceitaPedAdu = parametroPedAdu.getVlrNumerico()
				.intValue();
		final AghParametros parametroPed = this.parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_CLIN_EXCLUSAO_PED);
		final Integer clinExclusaoPed = parametroPed.getVlrNumerico()
				.intValue();
		final AghParametros parametroAdu = this.parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_CLIN_EXCLUSAO_ADULTO);
		final Integer clinExclusaoAdu = parametroAdu.getVlrNumerico()
				.intValue();
		final AghParametros parametroIdadePed = this.parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_IDADE_PEDIATRICA);
		final Integer idadePediatrica = parametroIdadePed.getVlrNumerico()
				.intValue();

		List<Long> listaProcedimentosAssociacao = null;
		if (cidSeq != null) {
			listaProcedimentosAssociacao = this
					.pesquisarFatAssociacaoProcedimentos(cidSeq);
		}

		return getFatVlrItemProcedHospCompsDAO().pesquisarProcedimentos(
				strPesquisa, parametroProcedimento, paciente,
				listaProcedimentosAssociacao, clinExclusaoPed, clinExclusaoAdu,
				carctAceitaPedAdu, idadePediatrica);
	}

	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	@BypassInactiveModule
	public List<ConvenioPlanoVO> pesquisarConvenioPlanoVOPorCodigoDescricao(
			final Object strPesquisa) {
		return getFatConvenioSaudePlanoDAO()
				.pesquisarConvenioPlanoVOPorCodigoDescricao(strPesquisa);
	}

	@Override
	public Map<Integer, List<ResumoAIHEmLoteServicosVO>> listarAtosMedicosResumoAihEmLote(
			final List<Integer> seqs) {
		return getFatItensProcedHospitalarDAO()
				.listarAtosMedicosResumoAihEmLote(seqs);
	}

	@Override
	public List<ResumoAIHEmLoteVO> pesquisarResumoAIHEmLote(
			final Integer cthSeq, final Date dtInicial, final Date dtFinal,
			final Boolean indAutorizadoSSM, final String iniciaisPaciente,
			final Boolean reapresentada) {
		return getFatEspelhoAihDAO().pesquisarResumoAIHEmLote(cthSeq,
				dtInicial, dtFinal, indAutorizadoSSM, iniciaisPaciente,
				reapresentada);
	}

	@Override
	public Long listarFatProcedAmbRealizadoCount(
			final FatCompetencia competencia, final AipPacientes paciente,
			final FatProcedHospInternos procedimento,
			final AacConsultaProcedHospitalar consulta,
			final AelItemSolicitacaoExames itemSolicitacaoExame,
			final MbcProcEspPorCirurgias procCirurgia,
			final DominioSituacaoProcedimentoAmbulatorio situacao,
			final Short cpgCphCspCnvCodigo, final Short cpgGrcSeq,
			final Byte cpgCphCspSeq, final Short unfSeq,
			final Long procedimentoAmbSeq,
			final DominioOrigemProcedimentoAmbulatorialRealizado origem,
			final Short convenioId, final Byte planoId) {
		return getFatProcedAmbRealizadoDAO().listarFatProcedAmbRealizadoCount(
				competencia, paciente, procedimento, consulta,
				itemSolicitacaoExame, procCirurgia, situacao,
				cpgCphCspCnvCodigo, cpgGrcSeq, cpgCphCspSeq, unfSeq,
				procedimentoAmbSeq, origem, convenioId, planoId);
	}

	@Override
	public List<FatProcedAmbRealizado> buscarProcedimentosCirurgicosParaSeremFaturados(
			final AipPacientes paciente, final MbcCirurgias cirurgia,
			final Short unfSeq, final Short convenioId, final Byte planoId) {
		return getFatProcedAmbRealizadoDAO()
				.buscarProcedimentosCirurgicosParaSeremFaturados(paciente,
						cirurgia, unfSeq, convenioId, planoId);
	}

	@Override
	public List<FatProcedAmbRealizadosVO> listarFatProcedAmbRealizado(
			final FatCompetencia competencia, final AipPacientes paciente,
			final FatProcedHospInternos procedimento,
			final AacConsultaProcedHospitalar consulta,
			final AelItemSolicitacaoExames itemSolicitacaoExame,
			final MbcProcEspPorCirurgias procCirurgia,
			final DominioSituacaoProcedimentoAmbulatorio situacao,
			final Short cpgCphCspCnvCodigo, final Short cpgGrcSeq,
			final Byte cpgCphCspSeq, final Short unfSeq,
			final Long procedimentoAmbSeq,
			final DominioOrigemProcedimentoAmbulatorialRealizado origem,
			final Short convenioId, final Byte planoId,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
		return getFatProcedAmbRealizadoDAO().listarFatProcedAmbRealizado(
				competencia, paciente, procedimento, consulta,
				itemSolicitacaoExame, procCirurgia, situacao,
				cpgCphCspCnvCodigo, cpgGrcSeq, cpgCphCspSeq, unfSeq,
				procedimentoAmbSeq, origem, convenioId, planoId, firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public FatProcedAmbRealizado obterFatProcedAmbRealizadoPorSeq(final Long seq) {
		return getFatProcedAmbRealizadoDAO().obterFatProcedAmbRealizadoPorSeq(
				seq);
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(
			final Object objPesquisa, final Short cpgCphCspCnvCodigo,
			final Short cpgGrcSeq, final Byte cpgCphCspSeq)
			throws ApplicationBusinessException {
		return getVFatAssociacaoProcedimentoDAO()
				.listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(
						objPesquisa, cpgCphCspCnvCodigo, cpgGrcSeq,
						cpgCphCspSeq);
	}

	@Override
	public Long listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(
			final Object objPesquisa, final Short cpgCphCspCnvCodigo,
			final Short cpgGrcSeq, final Byte cpgCphCspSeq)
			throws ApplicationBusinessException {
		return getVFatAssociacaoProcedimentoDAO()
				.listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(
						objPesquisa, cpgCphCspCnvCodigo, cpgGrcSeq,
						cpgCphCspSeq);
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarVFatAssociacaoProcedimento(
			final Integer phiSeq, final Short cpgCphCspCnvCodigo,
			final Byte cpgCphCspSeq, final Short cpgGrcSeq) {
		return getVFatAssociacaoProcedimentoDAO()
				.listarVFatAssociacaoProcedimento(phiSeq, cpgCphCspCnvCodigo,
						cpgCphCspSeq, cpgGrcSeq);
	}

	@Override
	@BypassInactiveModule
	public FatContasHospitalares obterContaHospitalarPorInternacao(
			final Integer intSeq) {
		return getFatContasHospitalaresDAO().obterContaHospitalarPorInternacao(
				intSeq);
	}

	@Override
	@BypassInactiveModule
	public List<FatContasInternacao> pesquisarContasInternacaoParaUpdate(
			final Integer intSeq) {
		return getFatContasInternacaoDAO().pesquisarContasInternacaoParaUpdate(
				intSeq);
	}

	@Override
	@BypassInactiveModule
	public void removerFatContasInternacao(
			final FatContasInternacao fatContasInternacao, final boolean flush) {
		getFatContasInternacaoDAO().remover(fatContasInternacao);
		if (flush) {
			getFatContasInternacaoDAO().flush();
		}
	}

	@Override
	@BypassInactiveModule
	public void removerFatItemContaHospitalar(
			final FatItemContaHospitalar fatItemContaHospitalar,
			final boolean flush) {
		getFatItemContaHospitalarDAO().remover(fatItemContaHospitalar);
		if (flush) {
			getFatItemContaHospitalarDAO().flush();
		}
	}

	@Override
	@BypassInactiveModule
	public void removerFatCidContaHospitalar(
			final FatCidContaHospitalar fatCidContaHospitalar,
			final boolean flush) {
		getFatCidContaHospitalarDAO().remover(fatCidContaHospitalar);
		if (flush) {
			getFatCidContaHospitalarDAO().flush();
		}
	}

	@Override
	@BypassInactiveModule
	public List<FatItemContaHospitalar> pesquisarItensContaHospitalarPorCthSituacao(
			final Integer cthSeq, final DominioSituacaoItenConta situacaoConta) {
		return getFatItemContaHospitalarDAO()
				.pesquisarItensContaHospitalarPorCthSituacao(cthSeq,
						situacaoConta);
	}

	@Override
	@BypassInactiveModule
	public List<FatContaSugestaoDesdobr> pesquisarFatContaSugestaoDesdobrPorCthNaoConsidera(
			final Integer cthSeq) {
		return getFatContaSugestaoDesdobrDAO()
				.pesquisarFatContaSugestaoDesdobrPorCthNaoConsidera(cthSeq);
	}

	@Override
	@BypassInactiveModule
	public List<FatContasInternacao> pesquisarContasInternacaoOrderDtInternacaoDesc(
			final Integer intSeq) {
		return getFatContasInternacaoDAO()
				.pesquisarContasInternacaoOrderDtInternacaoDesc(intSeq);
	}

	@Override
	@BypassInactiveModule
	public Long acomodacaoInternacaoConvenioCount(final AinInternacao internacao) {
		return getFatContasInternacaoDAO().acomodacaoInternacaoConvenioCount(
				internacao);
	}

	@Override
	@BypassInactiveModule
	public FatConvenioSaudePlano obterFatConvenioSaudePlanoPorChavePrimaria(
			final FatConvenioSaudePlanoId id) {
		return getFatConvenioSaudePlanoDAO().obterPorChavePrimaria(id);
	}

	@Override
	@Deprecated
	// nao pode ter bypass pq executa pelo banco
	public FatConvenioSaudePlano atualizarFatConvenioSaudePlano(
			final FatConvenioSaudePlano fatConvenioSaudePlano) {
		// TODO Essa chamada não irá acionar as triggers
		// deve-se chamar ConvenioSaudeON.persistirConvenioPlano, contudo,
		// foi necessário deixar dessa forma para o AGHU funcionar no HCPA
		// as triggers são executadas no banco Oracle
		getFatConvenioSaudePlanoDAO().atualizar(fatConvenioSaudePlano);
		getFatConvenioSaudePlanoDAO().flush();
		getFatConvenioSaudePlanoDAO().desatachar(fatConvenioSaudePlano);
		return getFatConvenioSaudePlanoDAO().obterPorChavePrimaria(
				fatConvenioSaudePlano.getId());
	}

	@Override
	@BypassInactiveModule
	@Deprecated
	public FatContasHospitalares atualizarFatContasHospitalares(
			final FatContasHospitalares fatContasHospitalares) {
		// TODO Realizar chamada a partir ContaHospitalarON alterando as
		// chamadas a esse método para chamada do método
		// persistirContaHospitalar nessa própria classe, retirando esse método
		// Enquanto não for devidamente migradas as funcionalidades das triggers
		// sobre a tabela correspondente a FatContasHospitalares isso deve ser
		// mantido para o AGHU funcionar corretamente no HCPA
		getFatContasHospitalaresDAO().atualizar(fatContasHospitalares);
		getFatContasHospitalaresDAO().flush();
		getFatContasHospitalaresDAO().desatachar(fatContasHospitalares);
		return getFatContasHospitalaresDAO().obterPorChavePrimaria(
				fatContasHospitalares.getSeq());
	}

	@Override
	@BypassInactiveModule
	public List<FatTipoAih> pesquisarTipoAihPorSituacaoCodSus(
			final DominioSituacao situacao, final Short codSus) {
		return getFatTipoAihDAO().pesquisarTipoAihPorSituacaoCodSus(situacao,
				codSus);
	}

	@Override
	@BypassInactiveModule
	public FatContasHospitalares obterContaHospitalarAbertaOuFechada(
			final Integer seqContaHospitalarOld) {
		return getFatContasHospitalaresDAO()
				.obterContaHospitalarAbertaOuFechada(seqContaHospitalarOld);
	}

	@Override
	public boolean verificaProcedimentoHospitalarInterno(final Integer matCodigo) {
		return getFaturamentoRN().verificaProcedimentoHospitalarInterno(
				matCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<FatConvTipoDocumentos> pesquisarObrigatoriosPorFatConvenioSaudePlano(
			final Short novoCspCnvCodigo, final Byte novoCspSeq) {
		return getFatConvTipoDocumentosDAO()
				.pesquisarObrigatoriosPorFatConvenioSaudePlano(
						novoCspCnvCodigo, novoCspSeq);
	}

	protected FatConvTipoDocumentosDAO getFatConvTipoDocumentosDAO() {
		return fatConvTipoDocumentosDAO;
	}

	@Override
	@BypassInactiveModule
	public List<FatConvenioSaude> pesquisarConveniosSaudeGrupoSUS(
			final Short codigoConvenio) {
		return this.getFatConvenioSaudeDAO().pesquisarConveniosSaudeGrupoSUS(
				codigoConvenio);
	}

	@Override
	public List<Byte> obterListaConvenioSaudeAtivoComPlanoAmbulatorialAtivo(
			Short codigoConvenio) {
		return this.getFatConvenioSaudeDAO()
				.obterListaConvenioSaudeAtivoComPlanoAmbulatorialAtivo(
						codigoConvenio);
	}

	@Override
	@BypassInactiveModule
	public FatItensProcedHospitalar obterItemProcedimentoHospitalar(
			final Integer iphSeq, final Short iphPhoSeq) {
		return this.getFatItensProcedHospitalarDAO()
				.obterItemProcedimentoHospitalar(iphSeq, iphPhoSeq);
	}

	@Override
	public FatContasInternacao obtePrimeiraContaHospitalar(
			final Integer seqInternacao, final Short cspCnvCodigo) {
		return this.getFatContasInternacaoDAO().obtePrimeiraContaHospitalar(
				seqInternacao, cspCnvCodigo);
	}

	@Override
	public void validarCamposProgramarEncerramento(final String nomeJob,
			final DominioOpcaoEncerramentoAmbulatorio opcao,
			final Date dtExecucao, final Date dtFimCompetencia,
			final Date dtFimProximaCompetencia, final Boolean previa)
			throws ApplicationBusinessException {
		getProgramarEncerramentoON().validarCamposProgramarEncerramento(
				nomeJob, opcao, dtExecucao, dtFimCompetencia,
				dtFimProximaCompetencia, previa);
	}

	protected ProgramarEncerramentoON getProgramarEncerramentoON() {
		return programarEncerramentoON;
	}

	@Override
	public void atualizarFaturamentoProcedimentoConsulta(
			final Integer conNumero, final Integer phiSeq,
			final Short quantidade, final AacRetornos retorno,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		getFaturamentoFatkInterfaceAacRN()
				.atualizarFaturamentoProcedimentoConsulta(conNumero, phiSeq,
						quantidade, retorno, nomeMicrocomputador,
						dataFimVinculoServidor);
	}

	@Override
	public void inserirFaturamentoProcedimentoConsulta(
			final AacConsultaProcedHospitalar consultaProcedHospitalar,
			final Date dthrRealizado, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		getFaturamentoFatkInterfaceAacRN()
				.inserirFaturamentoProcedimentoConsulta(
						consultaProcedHospitalar, dthrRealizado,
						nomeMicrocomputador, dataFimVinculoServidor);
	}

	protected FaturamentoFatkInterfaceAacRN getFaturamentoFatkInterfaceAacRN() {
		return faturamentoFatkInterfaceAacRN;
	}

	@Override
	public void rnFatpExecFatNew(
			final DominioOpcaoEncerramentoAmbulatorio modulo,
			final Boolean previa, final Date cpeDtFim, final AghJobDetail job,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		getFaturamentoFatkAmbRN().rnFatpExecFatNew(modulo, previa, cpeDtFim,
				job, nomeMicrocomputador, dataFimVinculoServidor);
	}

	protected FaturamentoFatkAmbRN getFaturamentoFatkAmbRN() {
		return faturamentoFatkAmbRN;
	}

	@Override
	public String gerarArquivoBPADataSus(final FatCompetencia competencia,
			final Long procedimento, final Integer tctSeq) throws IOException {
		return getFatArqEspelhoProcedAmbON().gerarArquivoBPADataSus(
				competencia, procedimento, tctSeq);
	}

	@Override
	public List<FatArqEspelhoProcedAmbVO> listarFatArqEspelhoProcedAmbVO(
			final FatCompetencia competencia, final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc) {
		return getFatArqEspelhoProcedAmbDAO().listarFatArqEspelhoProcedAmbVO(
				competencia, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Integer listarFatArqEspelhoProcedAmbVOCount(
			final FatCompetencia competencia) {
		return getFatArqEspelhoProcedAmbDAO()
				.listarFatArqEspelhoProcedAmbVOCount(competencia);
	}

	@Override
	public void fatpAgruBpaBpi(final boolean previa,
			final FatCompetencia competencia, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		getFaturamentoRN().agrupaBpaBpi(previa, competencia,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	private FatArqEspelhoProcedAmbON getFatArqEspelhoProcedAmbON() {
		return fatArqEspelhoProcedAmbON;
	}

	protected FatArqEspelhoProcedAmbDAO getFatArqEspelhoProcedAmbDAO() {
		return fatArqEspelhoProcedAmbDAO;
	}

	@Override
	public void enviaEmailResultadoEncerramentoAmbulatorio(final String msg,
			String remetente, List<String> destinatarios)
			throws ApplicationBusinessException {
		getFaturamentoON().enviaEmailResultadoEncerramentoAmbulatorio(msg,
				remetente, destinatarios);
	}

	@Override
	public void enviaEmailResultadoEncerramentoAmbulatorio(final String msg)
			throws ApplicationBusinessException {
		getFaturamentoON().enviaEmailResultadoEncerramentoAmbulatorio(msg);
	}

	@Override
	public void enviaEmailResultadoEncerramentoCTHs(
			final List<String> resultOK, final List<String> resultNOK,
			final Date dataInicioEncerramento)
			throws ApplicationBusinessException {
		getFaturamentoON().enviaEmailResultadoEncerramentoCTHs(resultOK,
				resultNOK, dataInicioEncerramento);
	}

	@Override
	public void enviaEmailInicioEncerramentoCTHs(
			final Integer quantidadeContas, final Date dataInicioEncerramento)
			throws ApplicationBusinessException {
		getFaturamentoON().enviaEmailInicioEncerramentoCTHs(quantidadeContas,
				dataInicioEncerramento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Map<String, Object> atualizarCidProcedimentoNovo(
			final List<String> lista) throws ApplicationBusinessException {
		return getImportarArquivoSusON().atualizarCidProcedimentoNovo(lista);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Map<String, Object> atualizarCompatibilidade(final List<String> lista)
			throws BaseException {
		return getImportarArquivoSusON().atualizarCompatibilidade(lista);
	}

	@Override
	public String gerarCSVRelatorioConsultaRateioServicosProfissionais(
			final FatCompetencia competencia)
			throws ApplicationBusinessException, IOException {
		return getRelatorioCSVFaturamentoON()
				.gerarCSVRelatorioConsultaRateioServicosProfissionais(
						competencia);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Map<String, Object> atualizarServicoClassificacao(final List<String> lista) throws BaseException {
		return getImportarArquivoSusON().atualizarServicoClassificacao(lista);
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Map<String, Object> atualizarInstrumentoRegistro(final List<String> lista) throws BaseException {
		return getImportarArquivoSusON().atualizarInstrumentoRegistro(lista);
	}
	
	@Override
	public String geraCSVRelatorioFaturaAmbulatorio(
			final FatCompetencia competencia)
			throws ApplicationBusinessException, IOException {
		return getRelatorioCSVFaturamentoON()
				.geraCSVRelatorioFaturaAmbulatorio(competencia);
	}

	@Override
	public List<ConsultaRateioProfissionalVO> listarConsultaRateioServicosProfissionais(
			final FatCompetencia competencia)
			throws ApplicationBusinessException, IOException {
		return getRelatorioCSVFaturamentoON()
				.listarConsultaRateioServicosProfissionais(competencia);
	}

	@Override
	public void gerarArquivoRelatorioRealizadosIndividuaisFolhaRosto(
			final FatCompetenciaId id, final Integer phiSeqInicial,
			final Integer phiSeqFinal, final TipoArquivoRelatorio tipoArquivo) {
		getRelatorioCSVFaturamentoON()
				.gerarArquivoRelatorioRealizadosIndividuaisFolhaRosto(id,
						phiSeqInicial, phiSeqFinal, tipoArquivo);
	}

	@Override
	public String gerarArquivoProducaoPHI(final List<Integer> phiSeqs,
			final Date dtInicio, final Date dtFinal) throws BaseException {
		return this.getRelatorioCSVFaturamentoON().gerarArquivoProducaoPHI(
				phiSeqs, dtInicio, dtFinal);
	}

	@Override
	public FatContasInternacao obterContaHospitalarePorInternacao(
			final Integer seqInternacao) {
		return this.getFatContasInternacaoDAO()
				.obterContaHospitalarePorInternacao(seqInternacao);
	}

	@Override
	public List<FatProcedAmbRealizado> buscarPorNumeroConsultaEProcedHospInternos(
			final Integer conNumero, final Integer phiSeq) {
		return this.getFatProcedAmbRealizadoDAO()
				.buscarPorNumeroConsultaEProcedHospInternos(conNumero, phiSeq);
	}

	@Override
	public void rnPmrpTrcEspAtd(final Integer atdSeq, final Short espSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getFaturamentoFatkAmbRN().rnPmrpTrcEspAtd(atdSeq, espSeq,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public List<AtualizarContaInternacaoVO> listarAtualizarContaInternacaoVO(
			Integer seqInternacao,
			DominioSituacaoConta indSituacaoContaHospitalar) {
		return getFatContasInternacaoDAO().listarAtualizarContaInternacaoVO(
				seqInternacao, indSituacaoContaHospitalar);
	}

	@Override
	public FatConvenioSaudePlano obterConvenioSaudePlanoPorChavePrimaria(
			FatConvenioSaudePlanoId fatConvenioSaudePlanoId) {
		return getFatConvenioSaudePlanoDAO().obterPorChavePrimaria(
				fatConvenioSaudePlanoId);
	}

	private FatCompetenciaProdDAO getFatCompetenciaProdDAO() {
		return fatCompetenciaProdDAO;
	}

	@Override
	public Long pesquisarFatCompetenciaProdCount(
			final FatCompetenciaProd competenciaProducao) {
		return getFatCompetenciaProdDAO().pesquisarFatCompetenciaProdCount(
				competenciaProducao);
	}

	@Override
	public List<FatCompetenciaProd> pesquisarFatCompetenciaProd(
			final FatCompetenciaProd competenciaProducao,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
		return getFatCompetenciaProdDAO()
				.pesquisarFatCompetenciaProd(competenciaProducao, firstResult,
						maxResult, orderProperty, asc);
	}

	/**
	 * Sobrecarga para Método que retorna um FatCompetenciaID com os valores a
	 * serem utilizados pela sugestion de Competência
	 * 
	 * @param objPesquisa
	 *            - Valor da pesquisa
	 * @return - FatCompetenciaId a ser utilizado para a pesquisa da lookup de
	 *         competência
	 * 
	 * @throws ApplicationBusinessException
	 * @author Eduardo Giovany Schweigert - eschweigert
	 */
	@Override
	public FatCompetenciaId getCompetenciaId(Object objPesquisa)
			throws ApplicationBusinessException {
		// Valor DEFAULT de criação de método
		return this.getCompetenciaId(objPesquisa, DominioModuloCompetencia.INT);
	}

	/**
	 * Método que retorna um FatCompetenciaID com os valores a serem utilizados
	 * pela sugestion de Competência
	 * 
	 * @param objPesquisa
	 *            - Valor da pesquisa
	 * @return - FatCompetenciaId a ser utilizado para a pesquisa da lookup de
	 *         competência
	 * 
	 * @throws ApplicationBusinessException
	 * @author Eduardo Giovany Schweigert - eschweigert
	 */
	@Override
	public FatCompetenciaId getCompetenciaId(final Object objPesquisa,
			final DominioModuloCompetencia modulo)
			throws ApplicationBusinessException {
		FatCompetenciaId id = new FatCompetenciaId();
		id.setModulo(modulo);

		final String vlPesquisa = (String) objPesquisa;

		if (vlPesquisa != null && !StringUtils.isBlank(vlPesquisa)) {

			// 04 ou 4
			if ((vlPesquisa.length() == 1 || vlPesquisa.length() == 2)
					&& !Pattern.compile("[0-9]{1,2}").matcher(vlPesquisa)
							.matches()) {
				FaturamentoExceptionCode.COMPETENCIA_INVALIDA.throwException();

				// 2011
			} else if ((vlPesquisa.length() == 4)
					&& !Pattern.compile("[0-9]{4}").matcher(vlPesquisa)
							.matches()) {
				FaturamentoExceptionCode.COMPETENCIA_INVALIDA.throwException();

				// 3/82 ou 03/1982
			} else if ((vlPesquisa.length() > 4 && vlPesquisa.length() < 7)
					&& !Pattern.compile("[0-9]{1,2}/[0-9]{2,4}")
							.matcher(vlPesquisa).matches()) {
				FaturamentoExceptionCode.COMPETENCIA_INVALIDA.throwException();

				// 11/03/1982
			} else if ((vlPesquisa.length() > 7)
					&& !Pattern.compile("[0-9]{1,2}/[0-9]{2}/[0-9]{4}")
							.matcher(vlPesquisa).matches()) {
				FaturamentoExceptionCode.COMPETENCIA_INVALIDA.throwException();
			}

			Integer ano = null, mes = null;
			Date dtHrInicio = null;

			final String[] comp = vlPesquisa.split("/");

			// 11/03/1982
			if (comp.length == 3) {
				dtHrInicio = DateUtil.obterData(Integer.parseInt(comp[2]),
						Integer.parseInt(comp[1]), Integer.parseInt(comp[0]));
				mes = Integer.parseInt(comp[1]);
				ano = Integer.parseInt(comp[2]);

				// 03/1982
			} else if (comp.length == 2) {
				mes = Integer.parseInt(comp[0]);

				if (comp[1].length() == 2) {
					ano = Integer.parseInt("20" + comp[1]);
				} else {
					ano = Integer.parseInt(comp[1]);
				}

			} else {

				// 1982
				if (vlPesquisa.length() == 4) {
					ano = Integer.parseInt(vlPesquisa);

					// 01
				} else if (vlPesquisa.length() == 1 || vlPesquisa.length() == 2) {
					mes = Integer.parseInt(vlPesquisa);

				} else {
					if (vlPesquisa.indexOf('/') > 0) {
						mes = Integer.parseInt(vlPesquisa.substring(0,
								vlPesquisa.indexOf('/')));
					} else {
						FaturamentoExceptionCode.COMPETENCIA_INVALIDA
								.throwException();
					}
				}
			}

			id = new FatCompetenciaId(modulo, mes, ano, dtHrInicio);
		}

		return id;
	}

	@Override
	@BypassInactiveModule
	public FatProcedHospInternos pesquisarPorChaveGenericaFatProcedHospInternos(
			Object seq, Fields field) {
		return this.getFatProcedHospInternosDAO()
				.pesquisarPorChaveGenericaFatProcedHospInternos(seq, field);
	}

	@Override
	public FatProcedHospInternos obterFatProcedHospInternosPorMaterial(
			AelExamesMaterialAnaliseId exaManId) {
		return this.getFatProcedHospInternosDAO()
				.obterFatProcedHospInternosPorMaterial(exaManId);
	}

	@Override
	public FatProcedHospInternos obterFatProcedHospInternosPorMaterial(
			Integer matCodigo) {
		return this.getFatProcedHospInternosDAO()
				.obterFatProcedHospInternosPorMaterial(matCodigo);
	}

	@Override
	public FatConvenioSaudePlano obterConvenioSaudePlano(Short cspCnvCodigo,
			Byte cspSeq) {
		return getFatConvenioSaudePlanoDAO().obterConvenioSaudePlano(
				cspCnvCodigo, cspSeq);
	}

	@Override
	public List<FatProcedHospIntCid> pesquisarFatProcedHospIntCidAtivosPorPhiSeqCidSeq(
			Integer phiSeq, Integer cidSeq) {
		return getFatProcedHospIntCidDAO()
				.pesquisarFatProcedHospIntCidAtivosPorPhiSeqCidSeq(phiSeq,
						cidSeq);
	}

	@Override
	public List<FatProcedHospIntCid> pesquisarFatProcedHospIntCidAtivosPorPhiSeq(
			Integer phiSeq) {
		return getFatProcedHospIntCidDAO()
				.pesquisarFatProcedHospIntCidAtivosPorPhiSeq(phiSeq);
	}

	@Override
	public FatProcedHospIntCid pesquisarFatProcedHospIntCidPorPhiSeqValidade(
			Integer phiSeq, DominioTipoPlano validade) {
		return getFatProcedHospIntCidDAO()
				.pesquisarFatProcedHospIntCidPorPhiSeqValidade(phiSeq, validade);
	}

	@Override
	public List<FatProcedAmbRealizado> buscarPorNumeroConsultaEProcedHospInternosApresentadosExcluidos(
			final Integer conNumero, final Integer phiSeq) {
		return getFatProcedAmbRealizadoDAO()
				.buscarPorNumeroConsultaEProcedHospInternosApresentadosExcluidos(
						conNumero, phiSeq);
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarVFatAssociacaoProcedimentoOutrosProcedimentos(
			Integer conNumero, Integer phiSeq, Short cpgCphCspCnvCodigo,
			Byte cpgCphCspSeq, Short cpgGrcSeq) {
		return getVFatAssociacaoProcedimentoDAO()
				.listarVFatAssociacaoProcedimentoOutrosProcedimentos(conNumero,
						phiSeq, cpgCphCspCnvCodigo, cpgCphCspSeq, cpgGrcSeq);
	}

	@Override
	public List<FatCompetencia> pesquisarCompetenciasPorModulo(
			DominioModuloCompetencia amb) {
		return getFatCompetenciaDAO().pesquisarCompetenciasPorModulo(amb);
	}

	@Override
	public FatCompetencia obterCompetenciaModuloMesAno(
			DominioModuloCompetencia amb, int i, int j) {
		return getFatCompetenciaDAO().obterCompetenciaModuloMesAno(amb, i, j);
	}

	@Override
	public List<FatItemContaApac> listarItemContaApacPorPrhConsultaSituacao(
			Integer consultaNumero) {
		return getFatItemContaApacDAO()
				.listarItemContaApacPorPrhConsultaSituacao(consultaNumero);
	}

	protected FatItemContaApacDAO getFatItemContaApacDAO() {
		return fatItemContaApacDAO;
	}

	@Override
	public List<FatProcedAmbRealizado> listarProcedAmbRealizadoPorPrhConsultaSituacao(
			Integer consultaNumero) {
		return getFatProcedAmbRealizadoDAO()
				.listarProcedAmbRealizadoPorPrhConsultaSituacao(consultaNumero);
	}

	@Override
	public List<FatProcedAmbRealizado> listarProcedAmbRealizadoPorConsulta(
			Integer consultaNumero) {
		return getFatProcedAmbRealizadoDAO()
				.listarProcedAmbRealizadoPorConsulta(consultaNumero);
	}

	@Override
	public void atualizarProcedAmbRealizado(
			FatProcedAmbRealizado procedAmbRealizado) {
		getFatProcedAmbRealizadoDAO().atualizar(procedAmbRealizado);
		getFatProcedAmbRealizadoDAO().flush();

	}

	@Override
	public FatProcedAmbRealizado obterFatProcedAmbRealizadoPorPrhConNumero(
			Integer numero) {
		return getFatProcedAmbRealizadoDAO()
				.obterFatProcedAmbRealizadoPorPrhConNumero(numero);
	}

	protected FatCandidatosApacOtorrinoDAO getFatCandidatosApacOtorrinoDAO() {
		return fatCandidatosApacOtorrinoDAO;
	}

	protected FatContaApacDAO getFatContaApacDAO() {
		return fatContaApacDAO;
	}

	protected FatPacienteTratamentosDAO getFatPacienteTratamentosDAO() {
		return fatPacienteTratamentosDAO;
	}

	protected FatResumoApacsDAO getFatResumoApacsDAO() {
		return fatResumoApacsDAO;
	}

	@Override
	public List<FatResumoApacs> listarResumosApacsPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getFatResumoApacsDAO().listarResumosApacsPorCodigoPaciente(
				pacCodigo);
	}

	@Override
	public void persistirFatResumoApacs(FatResumoApacs fatResumoApacs) {
		this.getFatResumoApacsDAO().persistir(fatResumoApacs);
	}

	public void persistirFatProcedAmbRealizado(
			FatProcedAmbRealizado fatProcedAmbRealizado) {
		this.getFatProcedAmbRealizadoDAO().persistir(fatProcedAmbRealizado);
	}

	@Override
	public void persistirFatPacienteTratamentos(
			FatPacienteTratamentos fatPacienteTratamentos) {
		this.getFatPacienteTratamentosDAO().persistir(fatPacienteTratamentos);
	}

	@Override
	public List<FatPacienteTransplantes> listarPacientesTransplantesPorPacCodigo(
			Integer pacCodigo) {
		return this.getFatPacienteTransplantesDAO()
				.listarPacientesTransplantesPorPacCodigo(pacCodigo);
	}

	@Override
	public void persistirFatPacienteTransplantes(
			FatPacienteTransplantes fatPacienteTransplantes) {
		this.getFatPacienteTransplantesDAO().persistir(fatPacienteTransplantes);
	}

	@Override
	public void removerFatPacienteTransplantes(
			FatPacienteTransplantes fatPacienteTransplantes) {
		this.getFatPacienteTransplantesDAO().remover(fatPacienteTransplantes);
		this.getFatPacienteTransplantesDAO().flush();
	}

	@Override
	public List<FatDadosContaSemInt> listarDadosContaSemIntPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getFatDadosContaSemIntDAO()
				.listarDadosContaSemIntPorCodigoPaciente(pacCodigo);
	}

	@Override
	public void persistirFatContaApac(FatContaApac fatContaApac) {
		this.getFatContaApacDAO().persistir(fatContaApac);
	}

	@Override
	public List<FatContaApac> listarContasApacsPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getFatContaApacDAO().listarContasApacsPorCodigoPaciente(
				pacCodigo);
	}

	@Override
	public void persistirFatCandidatosApacOtorrino(
			FatCandidatosApacOtorrino fatCandidatosApacOtorrino) {
		this.getFatCandidatosApacOtorrinoDAO().persistir(
				fatCandidatosApacOtorrino);
	}

	@Override
	public List<FatCandidatosApacOtorrino> listarCandidatosApacOtorrinoPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getFatCandidatosApacOtorrinoDAO()
				.listarCandidatosApacOtorrinoPorCodigoPaciente(pacCodigo);
	}

	@Override
	public CntaConv obterCntaConv(Integer intdCodPrnt, Date intdDataInt,
			Short convCod) {
		return this.getCntaConvDAO().obterCntaConv(intdCodPrnt, intdDataInt,
				convCod);
	}

	@Override
	@BypassInactiveModule
	public List<CntaConv> listarCntaConvPorIntdCodPrnt(Integer prontuario) {
		return this.getCntaConvDAO().listarCntaConvPorIntdCodPrnt(prontuario);
	}

	@Override
	public void persistirCntaConv(CntaConv cntaConv) {
		this.getCntaConvDAO().persistir(cntaConv);
	}

	@Override
	@BypassInactiveModule
	// Deve ter bypass pq essa funcionalidade precisa fazer pelo Java
	// Nao tem como fazer chamada nativa pq está no forms
	public void atualizarCntaConv(CntaConv cntaConv, boolean flush) {
		this.getCntaConvDAO().atualizar(cntaConv);
		if (flush) {
			this.getCntaConvDAO().flush();
		}
	}

	@Override
	public boolean temContaConv(Integer nroConta) {
		return this.getCntaConvDAO().temContaConv(nroConta);
	}

	// Esse dao vai p/ modulo de faturamento
	@Override
	public boolean temBackupContaConv(Integer nroConta) {
		return this.getBackupCntaConvDAO().temBackupContaConv(nroConta);
	}

	protected FaturamentoAmbulatorioON getFaturamentoAmbulatorioON() {
		return faturamentoAmbulatorioON;
	}

	protected CntaConvDAO getCntaConvDAO() {
		return cntaConvDAO;
	}

	protected BackupCntaConvDAO getBackupCntaConvDAO() {
		return backupCntaConvDAO;
	}

	@Override
	public List<FatConvenioSaude> obterConveniosSaudeAtivos() {
		return this.getFatConvenioSaudeDAO().obterConveniosSaudeAtivos();
	}

	@Override
	public Long obterCountConvenioSaudeAtivoPorPgdSeq(Short pgdSeq) {
		return this.getFatConvenioSaudeDAO()
				.obterCountConvenioSaudeAtivoPorPgdSeq(pgdSeq);
	}

	@Override
	public List<FatConvenioSaude> listarConveniosSaudeAtivosPorPgdSeq(Short pgdSeq) {
		return this.getFatConvenioSaudeDAO().listarConveniosSaudeAtivosPorPgdSeq(pgdSeq);
	}
	
	@Override
	public List<FatConvenioSaudePlano> listarConvenioSaudePlanos(
			String cspDescricao) {
		return this.getFatConvenioSaudePlanoDAO().listarConvenioSaudePlanos(
				cspDescricao);
	}

	@Override
	public FatConvenioSaudePlano obterConvenioSaudePlanoAtivo(
			Short codigoConvenio, Byte seqPlano) {
		return getFatConvenioSaudePlanoDAO().obterConvenioSaudePlanoAtivo(
				codigoConvenio, seqPlano);
	}

	@Override
	public List<FatConvGrupoItemProced> obterListaFatConvGrupoItensProcedPorExame(
			Byte planoConvenio, Short convenio, short parseShort,
			String siglaExame, int emaManSeq, short unfSeq) {
		return this.getFatConvGrupoItensProcedDAO()
				.obterListaFatConvGrupoItensProcedPorExame(planoConvenio,
						convenio, parseShort, siglaExame, emaManSeq, unfSeq);
	}

	@Override
	@BypassInactiveModule
	public FatConvGrupoItemProced obterFatConvGrupoItensProcedPeloId(
			Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq, Integer phiSeq) {
		return getFatConvGrupoItensProcedDAO()
				.obterFatConvGrupoItensProcedPeloId(cpgCphCspCnvCodigo,
						cpgCphCspSeq, phiSeq);
	}
	
	@Override
	public FatConvGrupoItemProced obterFatConvGrupoItensProcedId(FatConvGrupoItemProcedId id){
		return getFatConvGrupoItensProcedDAO().obterFatConvGrupoItensProcedId(id);
	}

	@Override
	public FatTiposDocumento obterTipoDoc(Short seqTipoDoc) {
		return this.getFatTiposDocumentoDAO().obterTipoDoc(seqTipoDoc);
	}

	@Override
	public List<FatTiposDocumento> obterTiposDocs(String seqDesc) {
		return this.getFatTiposDocumentoDAO().obterTiposDocs(seqDesc);
	}

	protected FatTiposDocumentoDAO getFatTiposDocumentoDAO() {
		return fatTiposDocumentoDAO;
	}

	@Override
	public List<FatContasInternacao> pesquisarNumeroAihContaHospitalarAtendimento(
			Integer seqInternacao) {
		return getFatContasHospitalaresDAO()
				.pesquisarNumeroAihContaHospitalarAtendimento(seqInternacao);
	}

	@Override
	public VFatAssociacaoProcedimento obterAssociacaoProcedimento(Short seqPho,
			Integer seqIph) {
		return getFatContasHospitalaresDAO().obterAssociacaoProcedimento(
				seqPho, seqIph);
	}

	@Override
	@BypassInactiveModule
	public void refreshItensProcedimentoHospitalar(
			FatItensProcedHospitalar itemProcedimentoHospitalar) {
		getFatItensProcedHospitalarDAO().refresh(itemProcedimentoHospitalar);

	}

	@Override
	@BypassInactiveModule
	public List<FatProcedAmbRealizado> listarProcedAmbRealizadosPrhConNumeroNulo(
			Integer pacCodigo, AghAtendimentos atendimento) {
		return getFatProcedAmbRealizadoDAO()
				.listarProcedAmbRealizadosPrhConNumeroNulo(pacCodigo,
						atendimento);
	}

	@Override
	public List<FatProcedAmbRealizado> listarProcedAmbRealizados(
			Integer seqSolicitacaoExame,
			DominioSituacaoProcedimentoAmbulatorio[] dominioSituacaoProcedimentoAmbulatorios) {
		return getFatProcedAmbRealizadoDAO().listarProcedAmbRealizados(
				seqSolicitacaoExame, dominioSituacaoProcedimentoAmbulatorios);
	}

	@Override
	public FatProcedHospInternos obterFatProcedHospInternosPorCodigoProcedimentoHemoterapico(
			String codigo) {
		return getFatProcedHospInternosDAO()
				.obterFatProcedHospInternosPorCodigoProcedimentoHemoterapico(
						codigo);
	}

	@Override
	public boolean existeProcedimentohospitalarInterno(
			FatProcedHospInternos fatProcedHospInternos,
			Class<FatConvGrupoItemProced> class1, Enum procedHospInterno) {
		return this.getFatProcedHospInternosDAO().existeItem(
				fatProcedHospInternos, class1, procedHospInterno);
	}

	@Override
	public void removerFatProcedHospInternosPorMaterial(
			AelExamesMaterialAnaliseId exaManId) {
		this.getFatProcedHospInternosDAO()
				.removerFatProcedHospInternosPorMaterial(exaManId);

	}

	@Override
	public List<FatProcedHospInternos> buscaProcedimentosComLaudoJustificativaParaImpressao(
			AghAtendimentos atendimento) {
		return this.getFatProcedHospInternosDAO()
				.buscaProcedimentosComLaudoJustificativaParaImpressao(
						atendimento);
	}

	@Override
	@BypassInactiveModule
	public List<FatProcedHospInternos> buscaProcedimentoHospitalarInterno(
			Integer matCodigo, Integer pciSeq, Short pedSeq) {
		return this.getFatProcedHospInternosDAO()
				.buscaProcedimentoHospitalarInterno(matCodigo, pciSeq, pedSeq);
	}

	@Override
	public List<FatProcedHospInternos> pesquisarPorProcedimentoEspecialDiverso(
			MpmProcedEspecialDiversos procedimentoEspecial) {
		return this.getFatProcedHospInternosDAO()
				.pesquisarPorProcedimentoEspecialDiverso(procedimentoEspecial);
	}

	@Override
	public void removerProcedimetoHospitalarInterno(FatProcedHospInternos proced) {
		this.getFatProcedHospInternosDAO().remover(proced);

	}

	@Override
	public List<FatProcedHospInternos> pesquisarPhis(Object paramPesquisa,
			String ordem, DominioSituacao situacao) {
		return this.getFatProcedHospInternosDAO()
				.listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricao(
						paramPesquisa, ordem, situacao);
	}

	@Override
	public void insereFaturamentoHospitalInternoParaMpmCuidadoUsual(
			MpmCuidadoUsual cuidadoUsual) throws ApplicationBusinessException {
		this.getFatProcedHospInternosON()
				.insereFaturamentoHospitalInternoParaMpmCuidadoUsual(
						cuidadoUsual);

	}

	@Override
	public void atualizaFaturamentoInternoParaMpmCuidadoUsual(
			MpmCuidadoUsual cuidadoUsual) throws ApplicationBusinessException {
		this.getFatProcedHospInternosON()
				.atualizaFaturamentoInternoParaMpmCuidadoUsual(cuidadoUsual);

	}

	@Override
	@BypassInactiveModule
	public FatProcedHospInternos obterProcedimentoHospitalarInternoPorCuidadoUsual(
			Integer seqCuidado) {
		return this.getFatProcedHospInternosDAO().obterPorCuidadoUsual(
				seqCuidado);
	}

	/**
	 * ORADB Forms AINP_VERIFICA_ENCERRA (CURSOR CONTAS - PARTE 1) Obtém a
	 * CntaConv
	 * 
	 * @param intSeq
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public CntaConv obterCntaConv(Integer intSeq) {
		return getCntaConvDAO().obterCntaConv(intSeq);
	}

	@Override
	@BypassInactiveModule
	public List<CntaConv> pesquisarContaNotEcrtPorInternacao(
			Integer seqInternacao) {
		return getCntaConvDAO().pesquisarContaNotEcrtPorInternacao(
				seqInternacao);
	}

	@Override
	@BypassInactiveModule
	public CntaConv obterCntaConvPorChavePrimaria(Integer nroCntaCont) {
		return this.getCntaConvDAO().obterPorChavePrimaria(nroCntaCont);
	}

	@Override
	@BypassInactiveModule
	// Deve ter bypass pq essa funcionalidade precisa fazer pelo Java
	// Nao tem como fazer chamada nativa pq está no forms
	public void removerCntaConv(CntaConv cntaConv, boolean flush) {
		this.getCntaConvDAO().remover(cntaConv);
		if (flush) {
			this.getCntaConvDAO().flush();
		}
	}

	@Override
	public List<CntaConv> listarCntaConvPorSeqAtendimento(Integer seqAtendimento) {
		return this.getCntaConvDAO().listarCntaConvPorSeqAtendimento(
				seqAtendimento);
	}

	@Override
	public VFatContaHospitalarPac buscarPrimeiraContaHospitalarPaciente(
			Integer contaHospitalar) {
		return getVFatContaHospitalarPacDAO()
				.buscarPrimeiraContaHospitalarPaciente(contaHospitalar);
	}

	@Override
	public VFatContaHospitalarPac buscarPrimeiraAihPaciente(Long numeroAih) {
		return getVFatContaHospitalarPacDAO().buscarPrimeiraAihPaciente(numeroAih);
	}
	
	@Override
	public CaracteristicaPhiVO fatcVerCaractPhi(Short cnvCodigo, Byte cspSeq,
			Integer phiSeq, String caracteristica)
			throws ApplicationBusinessException {
		return getFaturamentoRN().fatcVerCaractPhi(cnvCodigo, cspSeq, phiSeq,
				caracteristica);
	}

	@Override
	public List<Date> obterDataPreviaModuloSIS(FatCompetencia competencia) {
		return getFatEspelhoProcedSiscoloDAO().obterDataPreviaModuloSIS(
				competencia);
	}

	protected FatEspelhoProcedSiscoloDAO getFatEspelhoProcedSiscoloDAO() {
		return fatEspelhoProcedSiscoloDAO;
	}

	protected RelatorioItensRealizIndvON getRelatorioItensRealizIndvON() {
		return relatorioItensRealizIndvON;
	}

	@Override
	public List<FaturaAmbulatorioVO> listarFaturamentoAmbulatorioPorCompetencia(
			final Integer mes, final Integer ano, final Date dtHoraInicio) {
		return getFaturamentoAmbulatorioON()
				.listarFaturamentoAmbulatorioPorCompetencia(mes, ano,
						dtHoraInicio);
	}

	@Override
	public List<ItensRealizadosIndividuaisVO> listarItensRealizadosIndividuaisPorCompetencia(
			final Date dtHoraInicio, final Integer ano, final Integer mes,
			Long procedInicial, Long procedFinal) throws BaseException {
		return getRelatorioItensRealizIndvON().listarItensRealzIndiv(
				dtHoraInicio, ano, mes, procedInicial, procedFinal);
	}

	@Override
	public List<ItensRealizadosIndividuaisVO> listarItensRealizadosIndividuaisHistPorCompetencia(
			final Date dtHoraInicio, final Integer ano, final Integer mes,
			Long procedInicial, Long procedFinal) throws BaseException {
		return getRelatorioItensRealizIndvON().listarItensRealzIndivHist(
				dtHoraInicio, ano, mes, procedInicial, procedFinal);
	}

	@Override
	public String gerarPDFRelatorioItensRealzIndv(final byte[] bytes)
			throws ApplicationBusinessException, IOException {
		return getRelatorioItensRealizIndvON().gerarPDFRelatorioItensRealzIndv(
				bytes);
	}

	@Override
	public String nameHeaderDownloadPDFRelatorioItensRealzIndv(
			final String fileName, boolean hist) throws IOException {
		return getRelatorioItensRealizIndvON()
				.nameHeaderDownloadPDFRelatorioItensRealzIndv(hist);
	}

	@Override
	public String geraCSVRelatorioItensRealzIndv(final Date dtHoraInicio,
			final Integer ano, final Integer mes, Long procedInicial,
			Long procedFinal) throws IOException, BaseException {
		return getRelatorioItensRealizIndvON().geraCSVRelatorioItensRealzIndv(
				dtHoraInicio, ano, mes, procedInicial, procedFinal);
	}

	@Override
	public String geraCSVRelatorioItensRealzIndvHist(final Date dtHoraInicio,
			final Integer ano, final Integer mes, Long procedInicial,
			Long procedFinal) throws IOException, BaseException {
		return getRelatorioItensRealizIndvON()
				.geraCSVRelatorioItensRealzIndvHist(dtHoraInicio, ano, mes,
						procedInicial, procedFinal);
	}

	@Override
	public List<FatProcedHospInternos> listaPhiAtivosExame(
			DominioSituacao situacao, String exameSigla) {
		return this.getFatProcedHospInternosDAO().listaPhiAtivosExame(situacao,
				exameSigla);
	}

	@Override
	public FatConvenioSaude obterConvenioSaude(Short codigo) {
		return getFatConvenioSaudeDAO().obterConvenioSaude(codigo);
	}

	@Override
	public DadosCaracteristicaTratamentoApacVO verificarCaracteristicaTratamento(
			final Short phoSeqTrat, final Integer iphSeqTrat,
			final Short phoSeqItem, final Integer iphSeqItem,
			final Integer tciSeq) {
		return getCaracteristicaTratamentoApacRN()
				.verificarCaracteristicaTratamento(phoSeqTrat, iphSeqTrat,
						phoSeqItem, iphSeqItem, tciSeq);
	}

	protected CaracteristicaTratamentoApacRN getCaracteristicaTratamentoApacRN() {
		return caracteristicaTratamentoApacRN;
	}

	@Override
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoParaExameMaterial(
			final Short cnvCodigo, final Byte cspSeq, final String sigla,
			final Integer manSeq, final Short tipoContaSus) {
		return getVFatAssociacaoProcedimentoDAO()
				.listarAssociacaoProcedimentoParaExameMaterial(cnvCodigo,
						cspSeq, sigla, manSeq, tipoContaSus);
	}

	@Override
	public List<VFatProcedSusPhiVO> pesquisarViewFatProcedSusPhiVO(
			VFatProcedSusPhiVO filtro, Short pSusPadrao, Byte pSusAmbulatorio,
			Short pTipoGrupoContaSus) {
		return getFatConvGrupoItensProcedDAO().pesquisarViewFatProcedSusPhiVO(
				filtro, pSusPadrao, pSusAmbulatorio, pTipoGrupoContaSus);
	}

	@Override
	public StringBuilder buscarPhiSus(AelItemSolicitacaoExames item) {
		return getManterFatProcedHospInternosRN().buscarPhiSus(item);
	}

	@Override
	public List<FatVlrItemProcedHospComps> pesquisarVlrItemProcedHospCompsPorDthrLiberadaExaSiglaManSeq(
			Date dthrLiberada, Date dataFinalMaisUm, String exaSigla,
			Integer manSeq, AghParametros pTipoGrupoContaSus) {
		return getFatVlrItemProcedHospCompsDAO()
				.pesquisarVlrItemProcedHospCompsPorDthrLiberadaExaSiglaManSeq(
						dthrLiberada, dataFinalMaisUm, exaSigla, manSeq,
						pTipoGrupoContaSus);
	}

	@Override
	public FatCaractItemProcHosp obterQtdeFatCaractItemProcHosp(
			final FatItensProcedHospitalar proced, final Integer... tctSeq) {
		return getFatCaractItemProcHospDAO().obterQtdeFatCaractItemProcHosp(
				proced, tctSeq);
	}

	@Override
	public Long obterQtdeFatProcedHospIntCid(final Integer phiSeq) {
		return getFatProcedHospIntCidDAO().obterQtdeFatProcedHospIntCid(phiSeq);
	}

	@Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
	public ArquivoURINomeQtdVO gerarArquivoFaturamentoParcialSUSNew(
			final FatCompetencia competencia, final Date dataEncIni,
			final Date dataEncFinal) throws IOException, BaseException {
//		return getGeracaoArquivoFaturamentoCompetenciaInternacaoON()
//				.gerarArquivoParcialNew(competencia, dataEncIni, dataEncFinal);
        return getGeracaoArquivoParcialCompetenciaInternacaoON()
				.gerarArquivoParcialNew(competencia, dataEncIni, dataEncFinal);
	}

	@Override
	@BypassInactiveModule
	public List<FatProcedHospInternos> pesquisarProcedimentosInternosPeloSeqProcCirg(
			Integer pciSeq) {
		return getFatProcedHospInternosDAO()
				.pesquisarProcedimentosInternosPeloSeqProcCirg(pciSeq);
	}

	@Override
	public List<FatProcedHospInternos> pesquisarProcedimentosInternosPeloMatCodigo(
			Integer matCod) {
		return getFatProcedHospInternosDAO()
				.pesquisarProcedimentosInternosPeloMatCodigo(matCod);
	}

	@Override
	@BypassInactiveModule
	public List<VFatAssociacaoProcedimento> pesquisarVFatAssociacaoProcedimentoAtivosPorPhi(
			Integer phiSeq) throws ApplicationBusinessException {
		return getVFatAssociacaoProcedimentoDAO()
				.pesquisarVFatAssociacaoProcedimentoAtivosPorPhi(phiSeq);
	}

	@Override
	public void inserirProcedimentoHospitalarInterno(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico,
			MpmProcedEspecialDiversos procedEspecialDiverso, String csaCodigo,
			String pheCodigo, String descricao, DominioSituacao indSituacao,
			Short euuSeq, Integer cduSeq, Short cuiSeq, Integer tidSeq)
			throws ApplicationBusinessException {
		getManterFatProcedHospInternosRN()
				.inserirProcedimentoHospitalarInterno(matCodigo,
						procedimentoCirurgico, procedEspecialDiverso,
						csaCodigo, pheCodigo, descricao, indSituacao, euuSeq,
						cduSeq, cuiSeq, tidSeq);
	}

	@Override
	public void atualizarProcedimentoHospitalarInternoSituacao(
			ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico,
			MpmProcedEspecialDiversos procedEspecialDiverso, String csaCodigo,
			String pheCodigo, DominioSituacao indSituacao, Short euuSeq,
			Integer cduSeq, Short cuiSeq, Integer tidSeq)
			throws ApplicationBusinessException {
		getManterFatProcedHospInternosRN()
				.atualizarProcedimentoHospitalarInternoSituacao(matCodigo,
						procedimentoCirurgico, procedEspecialDiverso,
						csaCodigo, pheCodigo, indSituacao, euuSeq, cduSeq,
						cuiSeq, tidSeq);
	}

	@Override
	public void atualizarProcedimentoHospitalarInternoDescricao(
			ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico,
			MpmProcedEspecialDiversos procedEspecialDiverso, String csaCodigo,
			String pheCodigo, String descricao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		getManterFatProcedHospInternosRN()
				.atualizarProcedimentoHospitalarInternoDescricao(matCodigo,
						procedimentoCirurgico, procedEspecialDiverso,
						csaCodigo, pheCodigo, descricao, euuSeq, cduSeq,
						cuiSeq, tidSeq);
	}

	@Override
	public List<FatConvenioSaudePlano> sbObterConvenio(final Object parametro) {
		return getFatConvenioSaudePlanoDAO().sbObterConvenio(parametro);
	}

	@Override
	public Long sbObterConvenioCount(final Object parametro) {
		return getFatConvenioSaudePlanoDAO().sbObterConvenioCount(parametro);
	}

	@Override
	public VFatAssociacaoProcedimento obterFatProcedHospIntPorExameMaterialConvCspIphPhoSeq(
			final String sigla, final Integer manSeq, final Short iphPhoSeq,
			final Short cnvCodigo, final Byte cspSeq) {
		return getVFatAssociacaoProcedimentoDAO()
				.obterFatProcedHospIntPorExameMaterialConvCspIphPhoSeq(sigla,
						manSeq, iphPhoSeq, cnvCodigo, cspSeq);
	}

	@Override
	@BypassInactiveModule
	public FatcVerCarPhiCnvVO fatcVerCarPhiCnv(final Short cspCnvCodigo,
			final Byte cspSeq, final Integer phi, final String pCaracteristica,
			final Short cpgGrcSeq) {
		return getFaturamentoON().fatcVerCarPhiCnv(cspCnvCodigo, cspSeq, phi,
				pCaracteristica, cpgGrcSeq);
	}

	@Override
	public void removerFatItemContaApac(final FatItemContaApac elemento)
			throws ApplicationBusinessException {
		this.getFatItemContaApacRN().remover(elemento);
	}

	protected FatItemContaApacRN getFatItemContaApacRN() {
		return fatItemContaApacRN;
	}

	@Override
	public FatProcedAmbRealizado obterFatProcedAmbRealizadoPorCrgSeq(
			Integer ppcCrgSeq, Short cnvCodigo, Byte cspSeq) {
		return this.getFatProcedAmbRealizadoDAO()
				.obterFatProcedAmbRealizadoPorCrgSeq(ppcCrgSeq, cnvCodigo,
						cspSeq);
	}

	@Override
	public List<FatItemContaApac> listarFatItemContaApacPorPpcCrgSeq(
			Integer ppcCrgSeq) {
		return this.getFatItemContaApacDAO()
				.listarFatItemContaApacPorPpcCrgSeq(ppcCrgSeq);
	}

	@Override
	public List<FatItemContaApac> listarFatItemContaApac(Integer ppcCrgSeq) {
		return this.getFatItemContaApacDAO().listarItemContaApac(ppcCrgSeq);
	}

	@Override
	public void atualizarFatItemContaApac(final FatItemContaApac elemento,
			final FatItemContaApac oldElemento, final Boolean flush) throws BaseException {
		this.getFatItemContaApacRN().atualizar(elemento, oldElemento, flush);
	}

	@Override
	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorCrgSeq(
			Integer ppcCrgSeq) {
		return this.getFatProcedAmbRealizadoDAO()
				.listarFatProcedAmbRealizadoPorCrgSeq(ppcCrgSeq);
	}

	@Override
	public void removerFatProcedAmbRealizado(FatProcedAmbRealizado elemento)
			throws BaseException {
		this.getProcedimentosAmbRealizadoRN().remover(elemento);
	}

	@Override
	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorCrgSeqSituacao(
			Integer ppcCrgSeq) {
		return this.getFatProcedAmbRealizadoDAO()
				.listarFatProcedAmbRealizadoPorCrgSeqSituacao(ppcCrgSeq);
	}

	@Override
	public void atualizarFatProcedAmbRealizado(FatProcedAmbRealizado elemento)
			throws BaseException {
		this.getProcedimentosAmbRealizadoRN().atualizar(elemento);
	}

	@Override
	public List<FatItemContaHospitalar> listarContaHospitalarPorCirurgia(
			Integer ppcCrgSeq) {
		return this.getFatItemContaHospitalarDAO()
				.listarContaHospitalarPorCirurgia(ppcCrgSeq);
	}

	protected ProcedimentosAmbRealizadoRN getProcedimentosAmbRealizadoRN() {
		return procedimentosAmbRealizadoRN;
	}

	protected FatProcedAmbRealizadoRN getFatProcedAmbRealizadoRN() {
		return fatProcedAmbRealizadoRN;
	}
	
	@Override
	public List<FatItemContaHospitalar> listarContaHospitalarPorSceRmrPacientes(
			Integer ppcCrgSeq) {
		return this.getFatItemContaHospitalarDAO()
				.listarContaHospitalarPorSceRmrPacientes(ppcCrgSeq);
	}

	@Override
	public Integer obterMaxSeqConta(Date pcDthr, Integer atdSeq,
			Short cnvCodigo, Byte cnvSeq) {
		return this.getFatContasInternacaoDAO().obterMaxSeqConta(pcDthr,
				atdSeq, cnvCodigo, cnvSeq);
	}

	@Override
	public FatProcedHospInternos obterFatProcedHospInternosPorChavePrimaria(
			Integer seq) {
		return this.getFatProcedHospInternosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void atualizarFaturamentoBlocoCirurgico(
			MbcProcEspPorCirurgias procEspPorCirurgia,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		getAtualizaFaturamentoBlocoCirurgicoRN()
				.atualizarFaturamentoBlocoCirurgico(procEspPorCirurgia,
						nomeMicrocomputador, dataFimVinculoServidor);
	}

	protected AtualizaFaturamentoBlocoCirurgicoRN getAtualizaFaturamentoBlocoCirurgicoRN() {
		return atualizaFaturamentoBlocoCirurgicoRN;
	}

	@Override
	public List<EquipamentoCirurgiaVO> buscarEquipamentos(Integer crgSeq) {
		return this.getFatProcedHospInternosDAO().buscarEquipamentos(crgSeq);
	}

	protected PacienteTransplantadoRN getPacienteTransplantadoRN() {
		return pacienteTransplantadoRN;
	}

	@Override
	public Date obterDataUltimoTransplante(Integer pacCodigo) {
		return getPacienteTransplantadoRN().obterDataUltimoTransplante(
				pacCodigo);
	}

	protected VerificarCirurgiaFaturadaRN getVerificarCirurgiaFaturadaRN() {
		return verificarCirurgiaFaturadaRN;
	}

	@Override
	public Boolean verificarCirurgiaFaturada(Integer crgSeq,
			DominioOrigemPacienteCirurgia origem) {
		return getVerificarCirurgiaFaturadaRN().verificarCirurgiaFaturada(
				crgSeq, origem);
	}

	@Override
	public List<FatProcedHospIntCid> pesquisarProcedimentoHospitalarInternoCidCompativel(
			Integer phiSeq, Integer cidSeq, DominioOrigemPacienteCirurgia origem) {
		return getFatProcedHospIntCidDAO()
				.pesquisarProcedimentoHospitalarInternoCidCompativel(phiSeq,
						cidSeq, origem);
	}

	@Override
	public List<FatVlrItemProcedHospComps> pesquisarGruposPopularProcedimentoHospitalarInterno(
			Integer pciSeq, Date cpeComp, Short cnvCodigo, Byte cspSeq,
			Short tipoGrupoContaSus) {
		return getFatVlrItemProcedHospCompsDAO()
				.pesquisarGruposPopularProcedimentoHospitalarInterno(pciSeq,
						cpeComp, cnvCodigo, cspSeq, tipoGrupoContaSus);
	}

	@Override
	public Integer obterProcedimentoCirurgicoPopularProcedimentoHospitalarInterno(
			Short iphPhoSeq, Integer iphSeq, Integer pciSeq, Date cpeComp,
			Short cnvCodigo, Byte cspSeq, Short tipoGrupoContaSus) {
		return getFatConvGrupoItensProcedDAO()
				.obterProcedimentoCirurgicoPopularProcedimentoHospitalarInterno(
						iphPhoSeq, iphSeq, pciSeq, cpeComp, cnvCodigo, cspSeq,
						tipoGrupoContaSus);
	}

	@Override
	public List<FatCompetencia> pesquisarCompetenciaProcedimentoHospitalarInternoPorModulo(
			DominioModuloCompetencia modulo) {
		return getFatCompetenciaDAO()
				.pesquisarCompetenciaProcedimentoHospitalarInternoPorModulo(
						modulo);
	}

	@Override
	public List<FatVlrItemProcedHospComps> pesquisarProcedimentosPopularProcedimentoHospitalarInterno(
			Integer pciSeq, Date cpeComp, Integer phiSeq,
			Short tipoGrupoContaSus) {
		return getFatVlrItemProcedHospCompsDAO()
				.pesquisarProcedimentosPopularProcedimentoHospitalarInterno(
						pciSeq, cpeComp, phiSeq, tipoGrupoContaSus);
	}

	@Override
	public List<FatVlrItemProcedHospComps> pesquisarProcedimentosConvenioPopularProcedimentoHospitalarInterno(
			Integer pciSeq, Date cpeComp, Short cnvCodigo, Byte cspSeq,
			Integer phiSeq, Short tipoGrupoContaSus) {
		return getFatVlrItemProcedHospCompsDAO()
				.pesquisarProcedimentosConvenioPopularProcedimentoHospitalarInterno(
						pciSeq, cpeComp, cnvCodigo, cspSeq, phiSeq,
						tipoGrupoContaSus);

	}

	@Override
	public List<Integer> buscarPhiSeqPorPciSeqOrigemPacienteCodigo(
			Integer pciSeq, Byte origemCodigo, Short grupoSUS) {
		return getFatProcedHospInternosDAO()
				.buscarPhiSeqPorPciSeqOrigemPacienteCodigo(pciSeq,
						origemCodigo, grupoSUS);
	}

	@Override
	public List<CirurgiaCodigoProcedimentoSusVO> obterListaCirurgiaCodigoProcedimentoPorPhiSeqOrigemGrupo(
			Integer phiSeq, Short grupoSUS, Byte origemCodigo) {
		return getVFatAssociacaoProcedimento2DAO()
				.obterListaCirurgiaCodigoProcedimentoPorPhiSeqOrigemGrupo(
						phiSeq, grupoSUS, origemCodigo);
	}

	@Override
	public List<CirurgiaCodigoProcedimentoSusVO> getCursorFatAssocProcdporPhiSeqOrigemGrupo(
			final Integer phiSeq, final Short grupoSUS, final Byte origemCodigo) {
		return getVFatAssociacaoProcedimento2DAO()
				.getCursorFatAssocProcdporPhiSeqOrigemGrupo(phiSeq, grupoSUS,
						origemCodigo);
	}

	@Override
	public List<FatVlrItemProcedHospComps> pesquisarVlrItemProcedHospCompsPorIphSeqDtComp(
			Integer iphSeq, Short iphPhoSeq, Date valorDtComp) {
		return getFatVlrItemProcedHospCompsDAO()
				.pesquisarVlrItemProcedHospCompsPorIphSeqDtComp(iphSeq,
						iphPhoSeq, valorDtComp);
	}

	@Override
	public List<FatProcedAmbRealizado> listarProcedAmbRealizadoPorCodigoPacientePrhConNumeroNulo(
			Integer pacCodigo) {
		return getFatProcedAmbRealizadoDAO()
				.listarProcedAmbRealizadoPorCodigoPacientePrhConNumeroNulo(
						pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<Long> obterListaCodTabelaFatSsm(Long codTabela, Integer idade,
			DominioSexoDeterminante sexo, Short paramTabelaFaturPadrao) {
		return getFatSinonimoItemProcedHospDAO().obterListaCodTabelaFatSsm(
				codTabela, idade, sexo, paramTabelaFaturPadrao);
	}

	@Override
	public List<FatPacienteTratamentos> listarPacientesTratamentosPorCodigoPaciente(
			Integer pacCodigo) {
		return getFatPacienteTratamentosDAO()
				.listarPacientesTratamentosPorCodigoPaciente(pacCodigo);
	}

	public FatPacienteTratamentos atualizarPacienteTratamento(
			FatPacienteTratamentos tratamento) {
		return getFatPacienteTratamentosDAO().atualizar(tratamento);
	}

	protected FatCandidatosApacOtorrinoON getFatCandidatosApacOtorrinoON() {
		return fatCandidatosApacOtorrinoON;
	}
	
	@Override
	public String recuperarProntuarioFormatado(Integer prontuario){
		return getRelatorioResumoCobrancaAihRN().formataProntuarioBarcode(prontuario);
	}
	
	@Override
	public RnCapcCboProcResVO rnCapcCboProcRes(Integer pSerMatricula,
			Short pSerVinCodigo, Integer pSoeSeq, Short pIseSeqp,
			Integer pConNumero, Integer pCrgSeq, Short pIphPhoSeq,
			Integer pIphSeq, Date pDtRealizacao,
			List<Short> resultSeqTipoInformacaoShort, Date ultimoDiaMes)
			throws ApplicationBusinessException {

		return getFaturamentoFatkCapUniRN().rnCapcCboProcRes(pSerMatricula, 
				pSerVinCodigo, pSoeSeq, pIseSeqp, pConNumero, pCrgSeq, pIphPhoSeq, 
				pIphSeq, pDtRealizacao, resultSeqTipoInformacaoShort, ultimoDiaMes);

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void extornaCompetenciaAmbulatorio(String usuarioLogado)
			throws BaseException {
		getFaturamentoFatkAmbRN().extornaCompetenciaAmbulatorio(usuarioLogado);
	}

	@Override
	public FatConvenioSaudePlano obterPorCspCnvCodigoECnvCodigo(Short codigo,
			Byte seq) {
		return this.getFatConvenioSaudePlanoDAO()
				.obterPorCspCnvCodigoECnvCodigo(codigo, seq);
	}

	/**
	 * Esse método executa o cursor c_sus das funções.
	 * 
	 * @ORADB MBCC_TAB_FAT_SUS_AMB / MBCC_TAB_FAT_SUS_INT
	 * @param phiSeq
	 *            Integer
	 * @return String
	 */
	@Override
	public List<Long> obterListaDeCodigoTabela(final Integer phiSeq,
			final DominioSituacao phiIndSituacao,
			final DominioSituacao iphIndSituacao,
			final Short cpgCphCspCnvCodigo, final Byte cpgCphCspSeq,
			final Short iphPhoSeq) {
		return getFatItensProcedHospitalarDAO().obterListaDeCodigoTabela(
				phiSeq, phiIndSituacao, iphIndSituacao, cpgCphCspCnvCodigo,
				cpgCphCspSeq, iphPhoSeq);
	}

	@Override
	public List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedCirPorTipoSituacaoEspecialidadeIphPhoSeqProcedimento(
			final List<DominioTipoProcedimentoCirurgico> tipos,
			final DominioSituacao situacao, final Short especialidade,
			final Short iphPhoSeq, final Integer procedimento) {

		return getFatItensProcedHospitalarDAO()
				.obterProcedCirPorTipoSituacaoEspecialidadeIphPhoSeqProcedimento(
						tipos, situacao, especialidade, iphPhoSeq, procedimento);

	}

	@Override
	public List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedimentosCirurgicosPdtAtivosListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final List<DominioTipoProcedimentoCirurgico> tipos,
			final DominioSituacao situacao, final Short especialidade,
			final Short iphPhoSeq, final Integer procedimento) {

		return getFatItensProcedHospitalarDAO()
				.obterProcedimentosCirurgicosPdtAtivosListaPaginada(
						firstResult, maxResult, orderProperty, asc, tipos,
						situacao, especialidade, iphPhoSeq, procedimento);
	}

	@Override
	public void estornarCompetenciaInternacao(FatCompetencia competencia)
			throws BaseException {
		getFatCompetenciaRN().estornarCompetenciaInternacao(competencia);
	}

	public FatPeriodosEmissaoDAO getFatPeriodosEmissaoDAO() {
		return fatPeriodosEmissaoDAO;
	}

	public void setFatPeriodosEmissaoDAO(
			FatPeriodosEmissaoDAO fatPeriodosEmissaoDAO) {
		this.fatPeriodosEmissaoDAO = fatPeriodosEmissaoDAO;
	}

	public FatConvPlanoAcomodacoesDAO getFatConvPlanoAcomodacoesDAO() {
		return fatConvPlanoAcomodacoesDAO;
	}

	public void setFatConvPlanoAcomodacoesDAO(
			FatConvPlanoAcomodacoesDAO fatConvPlanoAcomodacoesDAO) {
		this.fatConvPlanoAcomodacoesDAO = fatConvPlanoAcomodacoesDAO;
	}

	@Override
	@BypassInactiveModule
	public List<VFatSsmInternacaoVO> obterListaVFatSssInternacao(
			Object strPesquisa, Integer idade, DominioSexoDeterminante sexo,
			Short paramTabelaFaturPadrao, Integer cidSeq, Integer caracteristica) {
		return getFatSinonimoItemProcedHospDAO().obterListaVFatSssInternacao(
				strPesquisa, idade, sexo, paramTabelaFaturPadrao, cidSeq,
				caracteristica);
	}

	@Override
	@BypassInactiveModule
	public Long obterListaVFatSssInternacaoCount(Object strPesquisa,
			Integer idade, DominioSexoDeterminante sexo,
			Short paramTabelaFaturPadrao, Integer cidSeq, Integer caracteristica) {
		return getFatSinonimoItemProcedHospDAO()
				.obterListaVFatSssInternacaoCount(strPesquisa, idade, sexo,
						paramTabelaFaturPadrao, cidSeq, caracteristica);
	}

	@Override
	public FatCompetencia obterUltimaCompetenciaModInternacao() {
		return getFatCompetenciaRN().obterUltimaCompetenciaModInternacao();
	}

	@Override
	public List<VFatSsmInternacaoVO> buscarVFatSsmInternacaoPorIphPho(
			Short phoSeq, Integer iphSeq) {
		return getFatSinonimoItemProcedHospDAO()
				.buscarVFatSsmInternacaoPorIphPho(phoSeq, iphSeq);
	}

	@Override
	public List<FatProcedHospInternos> pesquisarProcedimentosInternosPorTipoEtiqueta(
			String tipoEtiqueta) {
		return this.getFatProcedHospInternosDAO()
				.pesquisarProcedimentosInternosPorTipoEtiqueta(tipoEtiqueta);
	}

	@Override
	public List<CursoPopulaProcedimentoHospitalarInternoVO> obterCursorSSM(
			Integer pciSeq, Date cpeComp, Short cnvCodigo, Byte cspSeq,
			Short tipoGrupoContaSus) {
		return getFatItensProcedHospitalarDAO().obterCursorSSM(pciSeq, cpeComp,
				cnvCodigo, cspSeq, tipoGrupoContaSus);
	}

	@Override
	public List<CursoPopulaProcedimentoHospitalarInternoVO> obterCursorPHI(
			Integer pciSeq, Date cpeComp, Integer phiSeq,
			Short tipoGrupoContaSus) {
		return getFatItensProcedHospitalarDAO().obterCursorPHI(pciSeq, cpeComp,
				phiSeq, tipoGrupoContaSus);

	}

	@Override
	public List<VFatConvPlanoGrupoProcedVO> listarConveniosPlanos(Short grcSeq,
			Short cphPhoSeq, Short cphCspCnvCodigo) {
		return this.getVFatConvPlanoGrupoProcedDAO().listarConveniosPlanos(
				grcSeq, cphPhoSeq, cphCspCnvCodigo);
	}

	@Override
	public List<FatProcedHospInternos> listarPhi(Integer seq,
			DominioSituacao situacao, Integer matCodigo, Integer pciSeq,
			Short pedSeq, String csaCodigo, String pheCodigo,
			String emaExaSigla, Integer emaManSeq, Integer maxResult) {
		return this.getFatProcedHospInternosDAO().listarPhi(seq, situacao,
				matCodigo, pciSeq, pedSeq, csaCodigo, pheCodigo, emaExaSigla,
				emaManSeq, maxResult);
	}

	@Override
	public FatCompetenciaCompatibilid obterFatCompetenciaCompatibilidPorSeq(
			Long vCpxSeq) {
		return getFatCompetenciaCompatibilidDAO()
				.obterPorChavePrimaria(vCpxSeq);
	}

	public List<RapServidores> buscaUsuariosPorCCusto(Integer cCusto) {
		return getFatProcedHospInternosDAO().buscaUsuariosPorCCusto(cCusto);
	}

	@Override
	public List<FatProcedHospInternos> pesquisarProcedimentosInternosPeloCodProcHem(
			String codigo) {
		return getFatProcedHospInternosDAO()
				.pesquisarProcedimentosInternosPeloCodProcHem(codigo);
	}

	private FatkCth2RN getFatkCth2RN() {
		return fatkCth2RN;
	}

	public FatCompetenciaCompatibilidDAO getFatCompetenciaCompatibilidDAO() {
		return fatCompetenciaCompatibilidDAO;
	}

	public void verificaCompatibilidadeConvenioComProcedimento(
			final Integer phiSeq, final Short cpgCphCspCnvCodigo,
			final Byte cpgCphCspSeq) throws ApplicationBusinessException {
		getFaturamentoON().verificaCompatibilidadeConvenioComProcedimento(
				phiSeq, cpgCphCspCnvCodigo, cpgCphCspSeq);
	}

	@Override
	public List<FatMotivoDesdobramento> listarMotivoDesdobramento(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final FatMotivoDesdobramento motivoDesdobramento) {
		return this.getFatMotivoDesdobramentoDAO()
				.listarMovimentoDesdobramento(firstResult, maxResult,
						orderProperty, asc, motivoDesdobramento);
	}

	@Override
	@BypassInactiveModule
	public Long listarMotivoDesdobramentoCount(
			FatMotivoDesdobramento motivoDesdobramento) {
		return this.getFatMotivoDesdobramentoDAO()
				.listarMotivoDesdobramentoCount(motivoDesdobramento);
	}

	@Override
	public List<FatTipoAih> pesquisarTipoAih(Object parametro) {
		return this.getFatTipoAihDAO().pesquisarTipoAih(parametro);
	}

	@Override
	public Long pesquisarTipoAihCount(Object parametro) {
		return this.getFatTipoAihDAO().pesquisarTipoAihCount(parametro);
	}

	@Override
	public ImprimirVencimentoTributosVO recuperaTributosVencidos(
			DominioTipoTributo tipoTributo, Date dataApuracao) throws BaseException {
		return this.imprimirVencimentoTributosRN.recuperaTributosVencidos(tipoTributo, dataApuracao);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * atualizarProcedimentosConsulta(java.lang.Integer, java.lang.Integer,
	 * java.lang.Integer, java.lang.String)
	 */
	@Override
	public void atualizarProcedimentosConsulta(Integer seqAtendimentoAntigo, Integer seqAtendimentoNovo, Integer numeroConsulta,
			String nomeMicrocomputador) throws BaseException {

		getFatProcedAmbRealizadoRN().atualizarProcedimentosConsulta(seqAtendimentoAntigo, seqAtendimentoNovo, numeroConsulta, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * pesquisarItensProcedimentoTransferidoPorConsulta(java.lang.Integer)
	 */
	@Override
	public List<FatItemContaApac> pesquisarItensProcedimentoTransferidoPorConsulta(Integer numeroConsulta) {

		return getFatItemContaApacDAO().pesquisarItensProcedimentoTransferidoPorConsulta(numeroConsulta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * obterFatProcedAmbRealizadoOriginal(br.gov.mec.aghu.model.FatProcedAmbRealizado)
	 */
	@Override
	public FatProcedAmbRealizado obterFatProcedAmbRealizadoOriginal (FatProcedAmbRealizado fatProcedAmbRealizado) {

		return getFatProcedAmbRealizadoDAO().obterOriginal(fatProcedAmbRealizado);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#verificarExistenciaProcedimentoAtendimento(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public DominioSimNao verificarExistenciaProcedimentoAtendimento(Integer phiSeq, Integer codigoPaciente) {

		return getFatAtendimentoApacProcHospDAO().verificarExistenciaProcedimentoAtendimento(phiSeq, codigoPaciente);
	}

	protected FatCaractFinanciamentoRN getFatCaractFinanciamentoRN() {
		return fatCaractFinanciamentoRN;
	}

	protected FatAtendimentoApacProcHospDAO getFatAtendimentoApacProcHospDAO() {
		return fatAtendimentoApacProcHospDAO;
	}

	protected void setFatCaractFinanciamentoRN(FatCaractFinanciamentoRN fatCaractFinanciamentoRN) {
		this.fatCaractFinanciamentoRN = fatCaractFinanciamentoRN;
	}
	
	public List<FatCaractFinanciamento> pesquisarCaracteristicasFinanciamento(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, final FatCaractFinanciamento filtro) {
		return getFatCaractFinanciamentoDAO().pesquisarCaracteristicasFinanciamento(firstResult, maxResult, orderProperty, asc, filtro);
	}
	
	public Long pesquisarCaractFinanciamentosCount(final FatCaractFinanciamento filtro) {
		return getFatCaractFinanciamentoDAO().pesquisarCaractFinanciamentosCount(filtro);
	}
	
	public void persistirCaracteristicasFinanciamento(final FatCaractFinanciamento entidade) throws ApplicationBusinessException, BaseException {
		getFatCaractFinanciamentoRN().persistir(entidade);
	}

	@Override
	public void alterarSituacaoCaracteristicaFinanciamento(final FatCaractFinanciamento entidade) throws ApplicationBusinessException{
		getFatCaractFinanciamentoRN().ativarDesativarCaracteristica(entidade);		
	}
	
	public ArquivoURINomeQtdVO gerarCSVContasNaoReapresentadasCPF(
			FatCompetencia competencia) throws ApplicationBusinessException {
		return contaNaoReapresentadaON
				.gerarCSVContasNaoReapresentadasCPF(competencia);

	}

	public ArquivoURINomeQtdVO gerarCSVDadosContaNutricaoEnteral()
			throws ApplicationBusinessException {
		return contaNutricaoEnteralRN.gerarCSVDadosContaNutricaoEnteral();
	}

	@Override
	public ArquivoURINomeQtdVO gerarCSVContasProcedProfissionalVinculoIncorreto() throws ApplicationBusinessException {
		return contaProcedProfissionalVinculoIncorretoON.gerarCSVContasProcedProfissionalVinculoIncorreto();
	}
	
	public void inserirFatTipoCaractItens(FatTipoCaractItens entity) {
		fatTipoCaractItensRN.inserirFatTipoCaractItens(entity);
	}
	
	public void atualizarFatTipoCaractItens(FatTipoCaractItens entity) throws ApplicationBusinessException {
		fatTipoCaractItensRN.atualizarFatTipoCaractItens(entity);
	}

	@Override
	public List<FatTipoCaractItens> pesquisarTiposCaractItensPorSeqCaracteristica(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer seq, String caracteristica) {
		return fatTipoCaractItensDAO.pesquisarTiposCaractItensPorSeqCaracteristica(firstResult, maxResult, orderProperty, asc, seq, caracteristica);
	}
	
	@Override
	public List<AghClinicas> pesquisarClinicas(Object param) {
		return aghClinicasDAO.buscarClinicasSb(param);
	}
	
	@Override
	public Long pesquisarClinicasCount(Object param) {
		return aghClinicasDAO.buscarClinicasSbCount(param);
	}
	
	@Override
	public List<FatBancoCapacidadeVO> pesquisarBancosCapacidade(Integer mes, Integer ano, Integer numeroLeitos, Integer capacidade, Integer utilizacao, AghClinicas clinica, Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc) {
		return fatBancoCapacidadeDAO.pesquisarBancosCapacidade(mes, ano, numeroLeitos, capacidade, utilizacao, clinica, firstResult, maxResult, orderProperty, asc);
	}
	@Override
	public Long pesquisarBancosCapacidadeCount(Integer mes, Integer ano, Integer numeroLeitos, Integer capacidade, Integer utilizacao, AghClinicas clinica) {
		return fatBancoCapacidadeDAO.pesquisarBancosCapacidadeCount(mes, ano, numeroLeitos, capacidade, utilizacao, clinica);
	}
	@Override
	public FatBancoCapacidadeVO atualizarBancoCapacidade(final FatBancoCapacidadeVO vo) throws ApplicationBusinessException {
		return fatBancoCapacidadeRN.atualizarBancoCapacidade(vo);
	}
	@Override
	public Long pesquisarTiposCaractItensPorSeqCaracteristicaCount(Integer seq, String caracteristica) {
			return fatTipoCaractItensDAO.pesquisarTiposCaractItensPorSeqCaracteristicaCount(seq, caracteristica);
	}

	@Override
	public void excluirFatTipoCaractItensPorSeq(Integer seq) throws ApplicationBusinessException {
		fatTipoCaractItensRN.excluirFatTipoCaractItensPorSeq(seq);
	}
	
	public ArquivoURINomeQtdVO gerarCSVDadosContasComNPT(FatCompetencia competencia) throws ApplicationBusinessException {
		return contaNptRN.gerarCSVDadosContasComNPT(competencia);
		
	}
	
	
	@Override
	public List<FatSaldoUTIVO> pesquisarBancosUTI(Integer mes, Integer ano, DominioTipoIdadeUTI tipoUTI, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return fatSaldoUtiDAO.pesquisarBancosUTI(mes, ano, tipoUTI, firstResult, maxResult, orderProperty, asc);
	}
	@Override
	public Long pesquisarBancosUTICount(Integer mes, Integer ano, DominioTipoIdadeUTI tipoUTI) {
		return fatSaldoUtiDAO.pesquisarBancosUTICount(mes, ano, tipoUTI);
	}
	@Override
	public FatSaldoUTIVO persistirBancoUTI(final FatSaldoUTIVO vo, final Boolean emEdicao) throws ApplicationBusinessException {
		return saldoUtiRN.persistirSaldoUti(vo, emEdicao);
	}
	
	public ArquivoURINomeQtdVO obterDadosContaRepresentada(FatCompetencia competencia) throws ApplicationBusinessException {
		return contaRepresentadaRN.obterDadosContaRepresentada(competencia);
	}

	@Override
	public List<FatCnesVO> pesquisarFatCnesPorSeqDescricao(Object param) {	
		return fatServClassificacoesDAO.pesquisarFatCnesPorSeqDescricao(param);
	}

	@Override
	public List<FatUnidadeFuncionalCnesVO> pesquisarFatUnidadeFuncionalCnes(Short unfSeq) {
		return fatCnesUfDAO.pesquisarFatUnidadeFuncionalCnes(unfSeq);
	}

	@Override
	public void inserirFatCnesUf(FatCnesUf entity) {
		fatCnesUfRN.inserirFatCnesUf(entity);
		
	}

	@Override
	public void deletarFatCnesUf(Short unfSeq, Integer fcsSeq, Short cnesSeq) {
		fatCnesUfRN.deletarFatCnesUf(unfSeq, fcsSeq, cnesSeq);
		
	}

	@Override
	public FatServClassificacoes obterServClassificacoesPorChavePrimaria(Integer codigo) {
		return fatServClassificacoesDAO.obterPorChavePrimaria(codigo);
	}

	@Override
	public List<FatCompetencia> listarCompetenciaModuloMesAnoDtHoraInicioComHora(FatCompetenciaId id) {
		return  fatCompetenciaDAO.listarCompetenciaModuloMesAnoDtHoraInicioComHora(id);
	}
	
	@Override
	public Long listarCompetenciaModuloMesAnoDtHoraInicioComHoraCount(FatCompetenciaId id) {
		return  fatCompetenciaDAO.listarCompetenciaModuloMesAnoDtHoraInicioComHoraCount(id);
	}
	
	
	@Override
	public List<ContaApresentadaPacienteProcedimentoVO> obterContaApresentadaEspecialidade(Short codigoEspecialidade, FatCompetencia competencia) throws ApplicationBusinessException {
		return relatorioContasApresentadasPorEspecialidadeRN.obterContaApresentadaEspecialidade(codigoEspecialidade, competencia);
	}

	@Override
	public List<ProtocoloAihVO> listaProtocolosAih(Date data) {
		return fatContasHospitalaresDAO.listaProtocolosAih(data);
	}
	
	@Override
	public String gerarCSVContaApresentadaEspMes(Short seqEspecialidade, FatCompetencia competencia) throws ApplicationBusinessException {
		return relatorioContaApresentadaEspMesON.gerarCSVContaApresentadaEspMes(seqEspecialidade, competencia);
	}

	@Override
	public String gerarCSVClinicaPorProcedimento(FatCompetencia competencia) throws BaseException {
		return clinicaPorProcedimentoON.geraRelCSVClinicaPorProcedimento(competencia);
	}

	@Override
	public Collection<ClinicaPorProcedimentoVO> recupearColecaoRelatorioClinicaPorProcedimento(FatCompetencia competencia) throws BaseException {
		return clinicaPorProcedimentoON.recupearColecaoRelatorioClinicaPorProcedimento(competencia);
	}

	@Override
	public TotalGeralClinicaPorProcedimentoVO obterTotalGeralClinicaPorProcedimento(FatCompetencia competencia) throws BaseException {
		return clinicaPorProcedimentoON.obterTotalGeralRelatorio(competencia);
	}
	
	@Override
	public Long pesquisarFatCnesPorSeqDescricaoCount(String param) {
		return fatServClassificacoesDAO.pesquisarFatCnesPorSeqDescricaoCount(param);
	}
	@Override
	public List<AghCid> pesquisarCidsPorSSMDescricaoOuCodigo(Integer phiSeq,
			final String descricao, final Integer limiteRegistros) {
		return getCidContaHospitalarON().pesquisarCidsPorSSMDescricaoOuCodigo(
					phiSeq, descricao, limiteRegistros);
	}

	@Override
	public Long pesquisarCidsPorSSMDescricaoOuCodigoCount(Integer phiSeq, final String descricao) {
		return getCidContaHospitalarON()
				.pesquisarCidsPorSSMDescricaoOuCodigoCount(phiSeq,descricao);
	}
	
	@Override
	public List<FatCaractComplexidade> pesquisarCaracteristicasDeComplexidade(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatCaractComplexidade caractComplexidade) {
		return fatCaractComplexidadeDAO.pesquisarCaracteristicasDeComplexidade(firstResult, maxResult, orderProperty, asc, caractComplexidade);
	}

	// #2111
	@Override
	public List<SigValorReceitaVO> obterValorEspelhoPelaInternacao(Integer intSeq, DominioSituacaoConta situacao, Integer eaiSeqp) {
		return getFatEspelhoAihDAO().obterValorEspelhoPelaInternacao(intSeq, situacao, eaiSeqp);
	}
	
	@Override
	public List<SigValorReceitaVO> obterValoresReceitaAtosMedicos(List<Integer> listaCthSeq, DominioModoCobranca modoCobranca) {
		return getFatAtoMedicoAihDAO().obterValoresReceitaAtosMedicos(listaCthSeq, modoCobranca);
	}
	
	@Override
	public List<AssociacaoProcedimentoVO> obterValoresProcedimentosAtravesRespectivosCodigosSus(Set<Integer> listaPhi, Set<Long> listaCodTabela) {
		return getVFatAssociacaoProcedimentoDAO().obterValoresProcedimentosAtravesRespectivosCodigosSus(listaPhi, listaCodTabela);
	}
	
	@Override
	public Long pesquisarCaracteristicasDeComplexidadeCount(FatCaractComplexidade caractComplexidade) {
		return fatCaractComplexidadeDAO.pesquisarCaracteristicasDeComplexidadeCount(caractComplexidade);
	}
	
	@Override
	public List<FatCaractComplexidade> listaCaracteristicasDeComplexidade(Object caractComplexidade) {
		return this.fatCaractComplexidadeDAO.listaCaracteristicasDeComplexidade(caractComplexidade);
	}
	
	@Override
	public Long listaCaracteristicasDeComplexidadeCount(Object caractComplexidade) {
		return this.fatCaractComplexidadeDAO.listaCaracteristicasDeComplexidadeCount(caractComplexidade);
	}

	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void persistirCaracteristicasDeComplexidade(FatCaractComplexidade caractComplexidade, Boolean alteracao)
			throws BaseException {
		this.fatCaractComplexidadeRN.persistirCaracteristicasDeComplexidade(caractComplexidade, alteracao);
	}
	

	@Override
	public List<FatItensProcedHospitalar> listarItensProcedimentosHospitalares(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final FatItensProcedHospitalar itensProcedimentosHospitalares) {
		return this.getFatItensProcedHospitalarDAO().listarItensProcedimentosHospitalares(firstResult, maxResult, orderProperty, asc, itensProcedimentosHospitalares);
	}

	@Override
	public Long listarItensProcedimentosHospitalaresCount(
			FatItensProcedHospitalar itensProcedimentosHospitalares) {
		return this.getFatItensProcedHospitalarDAO().listarItensProcedimentosHospitalaresCount(itensProcedimentosHospitalares);
	}

	/**
	 * Método que lista os Faturamentos de Procedimentos e Possibilidades
	 * Associados.
	 * 
	 * @param itemProcedimentoHospitalar
	 * @return
	 */
	@Override
	public List<FatPossibilidadeRealizado> listarProcedimentosPossibilidadesAssociados(
			FatItensProcedHospitalar itemProcedimentoHospitalar) {
		return this.getFatPossibilidadeRealizadoDAO()
				.listarProcedimentosPossibilidadesAssociados(
						itemProcedimentoHospitalar);
	}

	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void persistirPossibilidadeRealizado(
			FatPossibilidadeRealizado possibilidadeRealizado)
			throws ApplicationBusinessException {

		this.fatPossibilidadeRealizadoRN
				.persistirPossibilidadeRealizado(possibilidadeRealizado);
	}

	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void removerPossibilidadeRealizado(
			FatPossibilidadeRealizado possibilidadeRealizado)
			throws BaseException {
		this.getFatPossibilidadeRealizadoDAO().removerPorId(possibilidadeRealizado
				.getId());

	}

	@Override
	public List<FatItensProcedHospitalar> listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqOrder(
			Object objPesquisa, Short phoSeq) {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqOrder(
						objPesquisa, phoSeq);
	}

	@Override
	public Long listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqOrderCount(
			Object objPesquisa, Short phoSeq) {
		return getFatItensProcedHospitalarDAO()
				.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqOrderCount(
						objPesquisa, phoSeq);
	}

	@Override
	public List<FatItemContaHospitalar> listarItensContaHospitalarComOrigemAfaEPorContaHospitalarSeq(
			Integer cthSeq, DominioIndOrigemItemContaHospitalar origem) {
		
		return this.getFatItemContaHospitalarDAO().listarItensContaHospitalarComOrigemAfaEPorContaHospitalarSeq(cthSeq, origem);
	}

	@Override
	public List<FatExcecaoPercentual> listarExcecaoPercentual(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FatExcecaoPercentual excecaoPercentual) {
		return getFatExcecaoPercentualDAO().listarExcecaoPercentual(firstResult, maxResult, orderProperty, asc, excecaoPercentual);
	}
	
	@Override
	public List<FtLogErrorVO> pesquisaFatLogErrorFatMensagensLog(Integer contaHospitalar,DominioSituacaoMensagemLog situacao, 
			boolean administrarUnidadeFuncionalInternacao, boolean leituraCadastrosBasicosFaturamento,
			boolean  manterCadastrosBasicosFaturamento, final Short ichSeqp, final String erro,
			final Integer phiSeqItem1, final Long codItemSus1){
		return this.getFaturamentoRN().pesquisaFatLogErrorFatMensagensLog(contaHospitalar, situacao, administrarUnidadeFuncionalInternacao, 
				leituraCadastrosBasicosFaturamento, manterCadastrosBasicosFaturamento, ichSeqp, erro, phiSeqItem1, codItemSus1);
		}
	
	@Override
	public Long pesquisaFatLogErrorFatMensagensLogCount(Integer contaHospitalar,DominioSituacaoMensagemLog situacao,boolean administrarUnidadeFuncionalInternacao, boolean leituraCadastrosBasicosFaturamento
			,boolean  manterCadastrosBasicosFaturamento, final Short ichSeqp, final String erro,final Integer phiSeqItem1, final Long codItemSus1){
		return this.getFaturamentoRN().pesquisaFatLogErrorFatMensagensLogCount(contaHospitalar, situacao, administrarUnidadeFuncionalInternacao,
				leituraCadastrosBasicosFaturamento, manterCadastrosBasicosFaturamento, ichSeqp, erro, phiSeqItem1, codItemSus1);
		}
	
	@Override
	public List<FatMensagemLog> listarMensagemErro(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final FatMensagemLog fatMensagemLog){
		return getFatMensagemLogDAO().listarMensagemLog(firstResult, maxResult, orderProperty, asc, fatMensagemLog);
	}
	
	@Override
	public Long listarMensagensLogCount(FatMensagemLog fatMensagemLog) {
		return getFatMensagemLogDAO().pesquisarMensagemLogCount(fatMensagemLog);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void alterarMensagemErro(FatMensagemLog fatMensagemErro)
			throws ApplicationBusinessException, BaseException {
		fatMensagemLogDAO.merge(fatMensagemErro);
	}
	
	@Override
	public ArquivoURINomeQtdVO obterDadosGeracaoArquivoProdutividadeFisiatria(FatCompetencia competencia) throws ApplicationBusinessException {
		ArquivoURINomeQtdVO vo = faturamentoON.obterDadosGeracaoArquivoProdutividadeFisiatria(competencia);
		return vo;
	}

	/**
	 * 41082
	 */
	@Override
	public Long pesquisarProcedimentosHospitalaresTransplanteCount(
			FatItensProcedHospitalar filtro) {
		return getFatItensProcedHospitalarDAO().pesquisarProcedimentosHospitalaresTransplanteCount(filtro);
	}

	@Override
	public List<FatTipoTransplante> pesquisarProcedimentosTransplante(
			Object filtro) {
		return getFatTipoTransplanteDAO().pesquisarProcedimentosTransplante(filtro);
	}

	@Override
	public Long pesquisarProcedimentosTransplanteCount(Object filtro) {
		return getFatTipoTransplanteDAO().pesquisarProcedimentosTransplanteCount(filtro);
	}

	@Override
	public void persistirProcedimentoHospitalarComTransplante(
			FatItemProcHospTransp cadastro) throws BaseException {
		getFatItemProcHospTranspDAO().persistir(cadastro);
	}
	
	@Override
	public List<FatItensProcedHospitalar> pesquisarProcedimentosHospitalaresComInternacao(
			Short seqTabela, String filtro) {
		return getFatItensProcedHospitalarDAO().pesquisarProcedimentosHospitalaresComInternacao(seqTabela, filtro);
	}

	@Override
	public Long pesquisarProcedimentosHospitalaresComInternacaoCount(
			Short seqTabela, String filtro) {
		return getFatItensProcedHospitalarDAO().pesquisarProcedimentosHospitalaresComInternacaoCount(seqTabela, filtro);
	}

	@Override
	public FatItemProcHospTransp obterDescricaoTransplantePorProcedHospitalar(
			FatItemProcHospTranspId id){
		return getFatItemProcHospTranspDAO().obterDescricaoTransplantePorProcedHospitalar(id);
	}
	
	@Override
	public void atualizarItemProcedimentoHospitalarTransplante(
			FatItensProcedHospitalar newIph, FatItensProcedHospitalar oldIph) throws BaseException {
		this.getItemProcedimentoHospitalarON().atualizarItemProcedimentoHospitalarTransplante(newIph, oldIph);
	}
	
	@Override
	public FatItemProcHospTransp pesquisarItemProcHospTranspPorId(
			FatItemProcHospTranspId id){
		return getFatItemProcHospTranspDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void excluirFatItemHospTransp(
			FatItemProcHospTransp itemProcHospTransp) {
		getFatItemProcHospTranspDAO().removerPorId(itemProcHospTransp.getId());
	}	
	
	@Override
	public List<FatItensProcedHospitalar> pesquisarProcedimentosHospitalaresTransplante(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatItensProcedHospitalar filtro) {
		return getFatItensProcedHospitalarDAO().pesquisarProcedimentosHospitalaresTransplante(firstResult, maxResult, orderProperty, asc, filtro);
	}
	
	public void setFatItemProcHospTranspDAO(
			FatItemProcHospTranspDAO fatItemProcHospTranspDAO) {
		this.fatItemProcHospTranspDAO = fatItemProcHospTranspDAO;
	}
	
	public FatItemProcHospTranspDAO getFatItemProcHospTranspDAO() {
		return fatItemProcHospTranspDAO;
	}
	
	public void persistirItemProcedHospTransp(FatItemProcHospTransp cadastro,
			FatItensProcedHospitalar procedRecuperado,
			FatItensProcedHospitalar entidade) throws BaseException{
		getFatItemProcHospTranspRN().persistir(cadastro, procedRecuperado, entidade);
	}

	public void atualizarItemProcedHospTransp(FatItemProcHospTransp cadastro) throws BaseException{
		getFatItemProcHospTranspRN().atualizar(cadastro);
	}
	
	public FatItemProcHospTranspRN getFatItemProcHospTranspRN() {
		return fatItemProcHospTranspRN;
	}
	
	// #2155
	@Override
	public List<SugestoesDesdobramentoVO> pesquisarSugestoesDesdobramento(DominioOrigemSugestoesDesdobramento origem, String incialPac) {
		return this.fatContaSugestaoDesdobrDAO.pesquisarSugestoesDesdobramento(origem, incialPac);
	}
	
	@Override
	public String gerarCSV(DominioOrigemSugestoesDesdobramento origem, String incialPac) throws IOException, BaseException {
		return this.relatorioSugestoesDesdobramentoON.gerarCSV(origem, incialPac);
	}
	
	@Override
	public List<RateioValoresSadtPorPontosVO> obterRateioValoresSadtPorPontos(Date dataHoraInicio, Integer ano, Integer mes) throws ApplicationBusinessException {
		return this.fatItemContaHospitalarDAO.obterRateioValoresSadtPorPontos(dataHoraInicio, ano, mes);
	}
	
	@Override
	public BigDecimal obterFatorMultiplicacaoParaValorRateado(Date dtHrInicio, Integer ano, Integer mes) {
		return this.fatValorContaHospitalarDAO.obterFatorMultiplicacaoParaValorRateado(dtHrInicio, ano, mes);
	}
	
	@Override
	public String gerarCSVRelatorioRateioValoresSadtPorPontos(final FatCompetencia competencia) throws IOException, ApplicationBusinessException {
		return this.relatorioRateioValoresSadtPorPontosON.exportarArquivoCSV(competencia);
	}
	
	@Override
	public List<FatCompetencia> listarCompetenciaModuloParaSuggestionBox(final FatCompetenciaId id) {
		return this.fatCompetenciaDAO.listarCompetenciaModuloParaSuggestionBox(id);
	}

	@Override
	public Long listarCompetenciaModuloParaSuggestionBoxCount(final FatCompetenciaId id) {
		return this.fatCompetenciaDAO.listarCompetenciaModuloParaSuggestionBoxCount(id);
	}
	
	@Override
	public List<ProtocolosAihsVO> getProtocolosAihs(final Integer prontuario, final String nomePaciente, final Integer codpaciente,
			final String leito, final Integer conta,final Date dtInternacao,final Date dtAlta,final Date dtEnvio,final String envio) throws ApplicationBusinessException{
		return this.fatContasHospitalaresDAO.getProtocolosAihs(prontuario, nomePaciente, codpaciente,
				leito, conta, dtInternacao, dtAlta, dtEnvio, envio);
	}
	
	@Override
	public FatContasHospitalares obterProtocoloAihPorId(Integer seq){
		return this.fatContasHospitalaresDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public void atualizarContaHospitalar(FatContasHospitalares newCtaHosp,
			FatContasHospitalares oldCtaHosp, boolean flush,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		contaHospitalarON.atualizarContaHospitalar(newCtaHosp,oldCtaHosp, flush,nomeMicrocomputador,dataFimVinculoServidor);
	}
	
	@Override
	public void atualizarContaHospitalarProtocolosAih(ProtocolosAihsVO item, boolean flush, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor) throws BaseException {
		contaHospitalarON.atualizarContaHospitalarProtocolosAih(item, flush, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
			
			@Override
			public void persistirDiariaInternacaoAutorizada(FatDiariaInternacao fatDiariaInternacao) throws ApplicationBusinessException{
				fatDiariaInternacaoRN.persistirDiariaInternacao(fatDiariaInternacao);
			}
			@Override
			public void alterarDiariaInternacaoAutorizada(FatDiariaInternacao fatDiariaInternacao) throws ApplicationBusinessException{
				fatDiariaInternacaoRN.alterarDiariaInternacao(fatDiariaInternacao);
			}
			
			@Override
			public void removerDiariaInternacaoAutorizada(FatDiariaInternacao fatDiariaInternacao) throws ApplicationBusinessException{
				fatDiariaInternacaoRN.removerDiariaInternacao(fatDiariaInternacao);
			}
			
			@Override
			public List<FatValorDiariaInternacao> listarValorDiariaInternacao(
					 String orderProperty, FatValorDiariaInternacao fatValorDiariaInternacao, FatDiariaInternacao fatDiariaInternacao) {
				return fatValorDiariaInternacaoDAO.listarValorDiariaInternacao(orderProperty, fatValorDiariaInternacao, fatDiariaInternacao);
			}
			
			@Override
	public List<FatValorDiariaInternacao> validarDiariaInternacao(final Integer DinSeq) {
		return fatValorDiariaInternacaoDAO.validarDiariaInternacao(DinSeq);
	}
			
			@Override
			@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
			public Map<String, Object> atualizarFinanciamento(final List<String> lista)
					throws BaseException {
				return getImportarArquivoSusON().atualizarFinanciamento(lista);
			}
	
	@Override
			public void adicionarValorDiariaInternacao(FatValorDiariaInternacao fatValorDiariaInternacao) throws ApplicationBusinessException{
		this.fatValorDiariaInternacaoRN.persistirValorDiariaInternacao(fatValorDiariaInternacao);
			}
			
			@Override
			public void alterarValorDiariaInternacao(FatValorDiariaInternacao fatValorDiariaInternacao) throws ApplicationBusinessException{
		this.fatValorDiariaInternacaoRN.alterarValorDiariaInternacao(fatValorDiariaInternacao);
			}
			
			@Override
			public void removerValorDiariaInternacao(FatValorDiariaInternacao fatValorDiariaInternacao) throws ApplicationBusinessException{
				this.fatValorDiariaInternacaoDAO.removerValorDiariaInternacao(fatValorDiariaInternacao);
			}
	
	//#2178
	@Override
	public List<PendenciasEncerramentoVO> getPendenciasEncerramento(Integer conta,Short itemConta,String erro,Integer prontuario,Date dtOperacao,String programa,DominioItemPendencia item,Short tabItem,Integer seqItem,Long sus,Integer hcpa) throws ApplicationBusinessException{
		return this.fatLogErrorDAO.getPendenciasEncerramento(conta,itemConta,erro,prontuario,dtOperacao,programa,item,tabItem,seqItem,sus,hcpa);
	}

	@Override
	public List<PendenciaEncerramentoVO> obterDadosRelatorioLogInconsistenciaCarga(Date dataImpl){
		return relatorioLogInconsistenciaCargaRN.obterDadosRelatorio(dataImpl);
		}

	@Override
	public ByteArrayOutputStream gerarArquivoSms(Date data) throws ApplicationBusinessException {
		return gerarArquivoSmsON.gerarArquivoSms(data);
	}
	
	@Override
	public Date obterUltimaDataCriacaoFatLogError(){
		return 	getFatLogErrorDAO().obterUltimaDataCriacaoFatLogError();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void processarRetornoSms(InputStream inputStream, String nomeMicrocomputador) throws BaseException {
		recebimentoAihsON.recebimentoAihs(inputStream, nomeMicrocomputador);
	}

	/**
	 * #36463 C1 
	 * Consulta para obter dados FatEspelhoEncerramentoPreviaVO utilizado na
	 * criação do relatório ESPELHO DA AIH
	 */
	@Override
	public FatEspelhoEncerramentoPreviaVO obterFatEspelhoEncerramentoPreviaVO(Integer cthSeq) {
		return fatEspelhoAihDAO.obterFatEspelhoEncerramentoPreviaVO(cthSeq);
	}
	
	
	/**
	 * #36436 C8
	 * Método para obter lista de Dados OPM
	 */
	public List<ProcedimentoRealizadoDadosOPMVO> obterListaDadosOPM(Integer cthSeq){
		return fatVlrItemProcedHospCompsDAO.obterListaDadosOPM(cthSeq);
	}

	/**
	 * #36436 C9
	 * Método para obter ValoresPreviaVO
	 */
	@Override
	public List<ValoresPreviaVO> obterValoresPreviaVO(Integer cthSeq, AghParametros faturamentoPadrao) {
		return fatVlrItemProcedHospCompsDAO.obterValoresPreviaVO(cthSeq, faturamentoPadrao);
	}	
	
	public FatModalidadeAtendimento obterModalidadeAtivaPorCodigo(Short codigo) {
		return fatModalidadeAtendimentoDAO.obterModalidadeAtivaPorCodigo(codigo);
	}


	/**
	 * #36463 C6 
	 * Consulta para obter Motivo Cobrança utilizado na
	 * criação do relatório ESPELHO DA AIH
	 */
	@Override
	public FatMotivoCobrancaApac obterMotivoCobrancaPorCodSus(Byte codigoSus) {
		return fatMotivoCobrancaApacDAO.obterMotivoCobrancaPorCodSus(codigoSus);
	}
	
	/**
	 * #36436 C7
	 * Método para obter lista de procedimentos realizados
	 */
	@Override
	public List<ProcedimentoRealizadoDadosOPMVO> obterListaProcedimentoRealizados(Integer cthSeq) {
		return fatVlrItemProcedHospCompsDAO.obterListaProcedimentoRealizados(cthSeq);
	}
	
	/**
	 * #36436 C10
	 * Método para obter descrição de Motivos Rejeição Conta por fatContasHospitalares.Seq
	 */
	@Override
	public List<FatMotivoRejeicaoContasVO> obterDescricaoMotivosRejeicaoContaPorSeq(Integer seq) {
		return fatContasHospitalaresDAO.obterDescricaoMotivosRejeicaoContaPorSeq(seq);
	}
	
	/**
	 * @ORADB fatc_busca_modalidade
	 * #36463
	 */
	@Override
	public Short aghcModuloOnzeCompl(FatEspelhoEncerramentoPreviaVO objetoVO) {
		return relatorioResumoCobrancaAihON.aghcModuloOnzeCompl(objetoVO);
	}
	
	/**
	 * #36463 C2
	 * Consulta para obter descrição de nacionalidade por código.
	 */
	@Override
	public String obterDescricaoNacionalidadePorCodigo(Integer codigo) {
		return aipNacionalidadesDAO.obterDescricaoNacionalidadePorCodigo(codigo);
	}
	
	/**
	 * 
	 * #36463 C3
	 * Método para obter descrição de etnia por id.
	 */
	@Override
	public String obterDescricaoEtniaPorId(Integer id) {
		return aipEtniaDAO.obterDescricaoEtniaPorId(id);
	}
	
	/**
	 * #36463 C4 
	 * Consulta para obter dados descricao de AghCid utilizado na
	 * criação do relatório ESPELHO DA AIH
	 */
	@Override
	public String obterDescricaoCidPorCodigo(String codigo) {
		return aghCidDAO.obterDescricaoCidPorCodigo(codigo);
	}
	
	/**
	 * Utilizado para obter AinTiposCaraterInternacao em #36463 C5.
	 * 
	 * @param ainTiposCaraterInternacaoCodigo
	 * @return
	 */
	@Override
	public AinTiposCaraterInternacao obterTiposCaraterInternacaoPorSeq(Byte seq) {
		return ainTiposCaraterInternacaoDAO.obterTiposCaraterInternacao(Integer.parseInt(seq.toString()));
	}
	
	/**
	 * @ORADB fatc_busca_modalidade
	 * #36463
	 */
	@Override
	public Short fatcBuscaModalidade(Integer p_iph_seq, Short p_pho_seq,
			Date p_data_int, Date p_data_alta) {
		return relatorioResumoCobrancaAihON.fatcBuscaModalidade(p_iph_seq, p_pho_seq, p_data_int, p_data_alta);
	}

	@Override
	public boolean asuPosAtualizacaoStatement(
			final FatDadosContaSemInt original,
			final FatDadosContaSemInt modificada, String nomeComputador,
			Date dataFimVinculoServidor, final Boolean substituirProntuario, RapServidores usuarioLogado) throws BaseException {
		return getFatDadosContaSemIntRN().asuPosAtualizacaoStatement(original,
				modificada, nomeComputador, dataFimVinculoServidor, substituirProntuario, usuarioLogado);
	}
	@Override
	public Long listarExcecaoPercentualCount(FatExcecaoPercentual excecaoPercentual) {
		return getFatExcecaoPercentualDAO().listarExcecaoPercentualCount(excecaoPercentual);
	}
	
	@Override
	public void excluirExcecaoPercentual(FatExcecaoPercentual excecaoPercentual) {
		this.getFatExcecaoPercentualDAO().excluirExcecaoPercentual(excecaoPercentual);
	}

	@Override
	public void alterarExcecaoPercentual(FatExcecaoPercentual excecaoPercentual) {
		this.getFatExcecaoPercentualDAO().alterarExcecaoPercentual(excecaoPercentual);
	}

	@Override
	public void adicionarExcecaoPercentual(FatExcecaoPercentual excecaoPercentual) throws ApplicationBusinessException {
		this.getFatExcecaoPercentualRN().inserirExcecaoPercentual(excecaoPercentual);
	}

	@Override
	public Short buscarFatItemContaHospitalarMaxSeq(Integer seq) {
		return this.getFatItemContaHospitalarDAO().buscarMax(seq);
	}

	public Conv obterConvenioPorCodigo(Short codigo){
		return this.convDAO.obterPorChavePrimaria(codigo);
	}
	
	public void atualizarConvenio(Conv convenio){
		this.convDAO.atualizar(convenio);
	}


	public List<FatPacienteTransplantes> buscarPacientes(Integer pacienteCodigo) {
		return this.fatPacienteTransplantesDAO.consultaPacientes(pacienteCodigo);
	}
		public FatLaudosPacApacsDAO getFatLaudosPacApacsDAO() {
		return fatLaudosPacApacsDAO;
	}

	

	public FatAutorizaApacsDAO getFatAutorizaApacsDAO() {
		return fatAutorizaApacsDAO;
	}

	@Override
	public FatProcedAmbRealizado obterProcedAmbPorCirurgiaCancelada(Integer crgSeq) {
		return fatProcedAmbRealizadoDAO.obterProcedAmbPorCirurgiaCancelada(crgSeq);
	}
	
	@Override
	public List<FatProcedAmbRealizado> pesquisarProcedAmbPorCirurgiaCancelada(Integer crgSeq) {
		return fatProcedAmbRealizadoDAO.pesquisarProcedAmbPorCirurgiaCancelada(crgSeq);
	}

		

	@Override
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterProcedimentoSolicitado(Integer seq) {
		return getFatLaudosPacApacsDAO().obterProcedimentoSolicitado(seq);
	}

	public void setFatAutorizaApacsDAO(FatAutorizaApacsDAO fatAutorizaApacsDAO) {
		this.fatAutorizaApacsDAO = fatAutorizaApacsDAO;
	}
		@Override
	public ParametrosGeracaoLaudoOtorrinoVO obterParametrosGeracaoLaudoOtorrino(Integer conNumero) {
		return getFatProcedAmbRealizadoDAO().obterParametrosGeracaoLaudoOtorrino(conNumero);
	}

	@Override
	public FatAutorizaApac obterFatAutorizaApacPorCpf(Long cpf) {
		return fatAutorizaApacsDAO.obterFatAutorizaApacPorCpf(cpf);
	}

	@Override
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterCandidatosApacOtorrino(ParametrosGeracaoLaudoOtorrinoVO parametrosGeracaoLaudoOtorrinoVO) {
		return fatCandidatosApacOtorrinoDAO.obterCandidatosApacOtorrino(parametrosGeracaoLaudoOtorrinoVO);
	}

	@Override
	public String getCnsResp(Long cpf) throws ApplicationBusinessException {
		return fatkCthnRN.aipcGetCnsResp(cpf);
	}

	@Override
	public String pesquisarCodigoFormulaPaciente(Integer nroProntuario) {
		return getFatPacienteTransplantesDAO().pesquisarCodigoFormulaPaciente(nroProntuario);
	}
	
	@Override
	public List<FatResumoApacs> buscarDatasResumosApacsAtivos(Integer pacCodigo, Date dtFinal) {
		return fatResumoApacsDAO.buscarDatasResumosApacsAtivos(pacCodigo, dtFinal);
	}

	@Override
	public FatResumoApacs buscaResumo(Integer pacCodigo, Integer diagnostico){
		return fatResumoApacsDAO.buscaResumo(pacCodigo,diagnostico);		
	}

	@Override
	public FatCandidatosApacOtorrino buscarCandidato(Integer codigo,
			String candidato) {
		return fatCandidatosApacOtorrinoDAO.buscarCandidato(codigo, candidato);
	}

	@Override
	public FatCandidatosApacOtorrino buscarCandidatoSemReavaliacao(Integer codigo) {
		return fatCandidatosApacOtorrinoDAO.buscarCandidatoNaoReavaliacao(codigo);
	}

	@Override
	public String fatpCandidatoApacDesc(Integer consulta, Date dataRealizado) throws ApplicationBusinessException {
		return faturamentoRN.fatpCandidatoApacDesc(consulta, dataRealizado);
	}
	
	@Override
	public void verificaApacAutorizacao(Integer pacCodigo,Date data,Byte tipoTratamento) throws ApplicationBusinessException{
		faturamentoRN.verificaApacAutorizacao(pacCodigo, data, tipoTratamento);
	}
	
	@Override
	public String gerarCodigoBarras(String tipoCodigoBarras, String prontuario) {
		return faturamentoON.gerarCodigoBarras(tipoCodigoBarras, prontuario);
	}
	@Override
	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorPrhConNumero(Integer numero){
		
		return getFatProcedAmbRealizadoDAO().listarFatProcedAmbRealizadoPorPrhConNumero(numero);
	}
	
	@Override
	public FatProcedHospInternos obterFatProcedHospInternosPorProcedEspecialDiversos(Short pedSeq) {
		return this.fatProcedHospInternosDAO.obterFatProcedHospInternosPorProcedEspecialDiversos(pedSeq);
	}
	
	@Override
	public List<FatConvGrupoItemProced> pesquisarConvenioVerificarDuracaoTratamento(Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq, Integer phiSeq) {
		return this.fatConvGrupoItensProcedDAO.pesquisarConvenioVerificarDuracaoTratamento(cpgCphCspCnvCodigo, cpgCphCspSeq, phiSeq);
	}
	
	@Override
	public Long obterCodTabelaPorPhi(Integer phiSeq, Short convenioSus,
			Byte planoSusAmbulatorio,
			Short tabelaFaturamentoUnificada){
		return getVFatAssociacaoProcedimento2DAO().obterCodTabelaPorPhi(phiSeq, convenioSus, planoSusAmbulatorio, tabelaFaturamentoUnificada);
	}
	
	@Override
	public FatSituacaoSaidaPaciente obterFatSituacaoSaidaPacientePorChavePrimaria(Object pk){
		return this.fatSituacaoSaidaPacienteDAO.obterPorChavePrimaria(pk);
	}
	//#2146
	
	public List<FatDiariaInternacao> listarDiariasInternacao(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FatDiariaInternacao fatDiariaInternacao) {
		return fatDiariaInternacaoDAO.listarDiariasInternacao(firstResult, maxResults, orderProperty, asc, fatDiariaInternacao);
	}

	@Override
	public Long listarDiariasInternacaoCount(
			FatDiariaInternacao fatDiariaInternacao) {
		return fatDiariaInternacaoDAO.listarDiariasInternacaoCount(fatDiariaInternacao);
	}

	public void setFatLaudosPacApacsDAO(FatLaudosPacApacsDAO fatLaudosPacApacsDAO) {
		this.fatLaudosPacApacsDAO = fatLaudosPacApacsDAO;
	}

	@Override
	public FatLaudoPacApac obterLaudoRelacionadoConsulta(Integer conNumero) {
		return getFatLaudosPacApacsDAO().obterLaudoRelacionadoConsulta(conNumero);
	}
	
	@Override
	public List<VFatAssociacaoProcedimento> pesquisarAssociacaoProcedimentos(Integer phiSeq, Short convenio,
			Byte plano,
			Long codTabela){
		return getVFatAssociacaoProcedimento2DAO().pesquisarAssociacaoProcedimentos(phiSeq,convenio,plano,codTabela);
	}
	
	@Override
	public FatPacienteTransplantes obterFatPacienteTransplante(FatPacienteTransplantesId id){
		return getFatPacienteTransplantesDAO().obterPorChavePrimaria(id);
	}
	

	@Override
	public Integer persistirContasCobradasEmLote(List<String> listCodigoDCHI, String servidorLogado, RapServidores servidorManuseado){
	 	return contaHospitalarON.persistirContasCobradasEmLote(listCodigoDCHI, servidorLogado, servidorManuseado);
	}
	
	@Override
	public boolean isPacienteTransplantado(AipPacientes paciente){
	 	return faturamentoRN.isPacienteTransplantado(paciente);
	}
	
	//50635 - @ORADB Procedure AGH.FAT_DESFAZ_REINTERNACAO
	@Override
	public void desfazerReintegracao(Integer prontuario, String nomeMicrocomputador) throws BaseException, ApplicationBusinessException{
	 	faturamentoRN.desfazerReintegracao(prontuario, nomeMicrocomputador);
	}
	
	@Override
	public Integer removerPorCthModulo(final Integer cthSeq, final DominioModuloCompetencia modulo){
		return fatLogErrorON.removerPorCthModulo(cthSeq, modulo);
	}
	
	@Override
	public FatContasInternacao buscaContaInternacao(final Integer seqContaInternacao){
		return fatContasInternacaoDAO.buscaContaInternacao(seqContaInternacao);
	}
	
	@Override
	public List<FatRegistro> listarFatRegistroPorItensProcedimentoHospitalar(final Short iphPhoSeq, final Integer iphSeq){
		return this.getFatProcedimentoRegistroDAO().listarFatRegistroPorItensProcedimentoHospitalar(iphSeq, iphPhoSeq);
	}
}