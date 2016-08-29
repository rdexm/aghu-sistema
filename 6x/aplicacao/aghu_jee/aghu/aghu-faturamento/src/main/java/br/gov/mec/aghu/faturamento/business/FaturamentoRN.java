package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
//import javax.transaction.UserTransaction;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.configuracao.dao.AghClinicasDAO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dominio.DominioBoletimAmbulatorio;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoDcih;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.dominio.DominioTipoFormularioDataSus;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAihJnDAO;
import br.gov.mec.aghu.faturamento.dao.FatArqEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.dao.FatAtendimentoApacProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatCandidatosApacOtorrinoDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractFinanciamentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatContaSugestaoDesdobrDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatDiariaUtiDigitadaJnDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.faturamento.dao.FatMensagemLogDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobrClinicaDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobrSsmDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobramentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatPendenciaContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoModalidadeDAO;
import br.gov.mec.aghu.faturamento.dao.FatResumoApacsDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoTratamentosDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.faturamento.vo.BuscarJustificativasLaudoProcedimentoSusVO;
import br.gov.mec.aghu.faturamento.vo.CaracteristicaPhiVO;
import br.gov.mec.aghu.faturamento.vo.CmceCthSeqVO;
import br.gov.mec.aghu.faturamento.vo.CnsResponsavelVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaApacVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaHistoricoVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaOutraVO;
import br.gov.mec.aghu.faturamento.vo.CursorCTrpVO;
import br.gov.mec.aghu.faturamento.vo.CursorEspelhoBpiVO;
import br.gov.mec.aghu.faturamento.vo.CursorEspelhoVO;
import br.gov.mec.aghu.faturamento.vo.CursorVaprVO;
import br.gov.mec.aghu.faturamento.vo.DesdobrarContaHospitalarVO;
import br.gov.mec.aghu.faturamento.vo.FatpCandidatoApacDescVO;
import br.gov.mec.aghu.faturamento.vo.FtLogErrorPhiCodVO;
import br.gov.mec.aghu.faturamento.vo.FtLogErrorVO;
import br.gov.mec.aghu.faturamento.vo.MsgErroCthSeqVO;
import br.gov.mec.aghu.faturamento.vo.ParCthSeqSituacaoContaVO;
import br.gov.mec.aghu.faturamento.vo.ParCthSeqSsmVO;
import br.gov.mec.aghu.faturamento.vo.ParSsmSolicRealizVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerItemSusVO;
import br.gov.mec.aghu.faturamento.vo.TipoProcedHospitalarInternoVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.model.AacCaracteristicaGrade;
import br.gov.mec.aghu.model.AacCaracteristicaGradeId;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAgrupItemConta;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatAihJn;
import br.gov.mec.aghu.model.FatArqEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatAutorizadoCma;
import br.gov.mec.aghu.model.FatCandidatosApacOtorrino;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobr;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobrId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvPlanoAcomodacoes;
import br.gov.mec.aghu.model.FatConvTipoDocumentos;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatDiariaUtiDigitada;
import br.gov.mec.aghu.model.FatDiariaUtiDigitadaJn;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMotivoDesdobrClinica;
import br.gov.mec.aghu.model.FatMotivoDesdobrClinicaId;
import br.gov.mec.aghu.model.FatMotivoDesdobrSsm;
import br.gov.mec.aghu.model.FatMotivoDesdobrSsmId;
import br.gov.mec.aghu.model.FatMotivoDesdobramento;
import br.gov.mec.aghu.model.FatPendenciaContaHosp;
import br.gov.mec.aghu.model.FatPerdaItemConta;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedimentoModalidade;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.model.FatResumoApacs;
import br.gov.mec.aghu.model.FatTipoAih;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.FatValorContaHospitalar;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VRapPessoaServidor;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioLaudosProcSusVO;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioMudancaProcedimentosVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Essa classe deve implementar todas procedures e functions referentes ao
 * módulo de Faturamento.
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta", "PMD.NcssTypeCount", "PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity",
		"PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class FaturamentoRN extends AbstractFatDebugLogEnableRN {
	
	private static final String MSG_EXCECAO_DESDOBRAMENTO = "Ocorreu um exceção ao realizar o desdobramento";

	@EJB
	private FaturamentoFatkPmrRN faturamentoFatkPmrRN;
	
	@EJB
	private FatcBuscaServClassRN fatcBuscaServClassRN;
	
	@EJB
	private ContaHospitalarRN contaHospitalarRN;
	
	@EJB
	private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;
	
	@EJB
	private VerificaCaracteristicaItemProcedimentoHospitalarRN verificaCaracteristicaItemProcedimentoHospitalarRN;
	
	@EJB
	private GeracaoSequenciaAtoMedicoAihRN geracaoSequenciaAtoMedicoAihRN;
	
	@EJB
	private FatContaInternacaoPersist fatContaInternacaoPersist;
	
	@EJB
	private CidContaHospitalarPersist cidContaHospitalarPersist;
	
	private static final Log LOG = LogFactory.getLog(FaturamentoRN.class);
	
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
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private FatPendenciaContaHospDAO fatPendenciaContaHospDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;
	
	@Inject
	private FatProcedimentoHospitalarDAO fatProcedimentoHospitalarDAO;
	
	@Inject
	private FatCaractFinanciamentoDAO fatCaractFinanciamentoDAO;
	
	@Inject
	private FatContasInternacaoDAO fatContasInternacaoDAO;
	
	@Inject
	private FatEspelhoProcedAmbDAO fatEspelhoProcedAmbDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;
	
	@Inject
	private VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO;
	
	@Inject
	private FatProcedimentoModalidadeDAO fatProcedimentoModalidadeDAO;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@Inject
	private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;
	
	@Inject
	private FatDiariaUtiDigitadaJnDAO fatDiariaUtiDigitadaJnDAO;
	
	@Inject
	private FatTipoCaractItensDAO fatTipoCaractItensDAO;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private FatAihJnDAO fatAihJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private FatArqEspelhoProcedAmbDAO fatArqEspelhoProcedAmbDAO;
	
	@Inject
	private FatContaSugestaoDesdobrDAO fatContaSugestaoDesdobrDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private FatMotivoDesdobramentoDAO fatMotivoDesdobramentoDAO;
	
	@Inject
	private FatTipoAihDAO fatTipoAihDAO;
	
	@Inject
	private AghClinicasDAO aghClinicasDAO;
	
	@Inject
	private FatCandidatosApacOtorrinoDAO fatCandidatosApacOtorrinoDAO;
	
	@Inject
	private FatMotivoDesdobrClinicaDAO fatMotivoDesdobrClinicaDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@Inject
	private FatMotivoDesdobrSsmDAO fatMotivoDesdobrSsmDAO;

	@Inject
	private FatMensagemLogDAO fatMensagemLogDAO;
	
	@Inject 
	private FatLogErrorDAO fatLogErrorDAO;
	
	@Inject 
	private FatResumoApacsDAO fatResumoApacsDAO;
	
	@Inject
	private FatAtendimentoApacProcHospDAO fatAtendimentoApacProcHospDAO;
	
	@Inject
	private FatTipoTratamentosDAO fatTipoTratamentosDAO;
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;
	
	@EJB
	private FatkCthRN fatkCthRN;
	
	private static final long serialVersionUID = -4841488645435448938L;

	enum FaturamentoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_MOTIVO_DESDOBRAMENTO_CLINICA_DUPLICADA, MENSAGEM_MOTIVO_DESDOBRAMENTO_PROCEDIMENTO_DUPLICADO, FAT_00794, ERRO_INSERIR_CONTA_HOSPITALAR, ERRO_INSERIR_CONTA_HOSPITALAR_NAO_REAP, ERRO_INSERIR_CONTA_HOSPITALAR_REAP, ERRO_INSERIR_ASSOCIACAO_CONTA_HOSPITALAR_INTERNACAO, ERRO_INSERIR_ITEM_CONTA_HOSPITALAR, ERRO_CLONAR_CONTA_HOSPITALAR, ERRO_CLONAR_AIH, ERRO_ALTERAR_SITUACAO_AIH_BLOQUEADA, ERRO_ALTERAR_SITUACAO_AIH_VENCIDA, ERRO_ALTERAR_SITUACAO_AIH_UTIL,
		MBC_00537,FATP_INSERE_ITENS;
	}

	public void persistirMotivoDesdobramento(FatMotivoDesdobramento fatMotivoDesdobramento) {
		FatTipoAih tipoAih = fatTipoAihDAO.obterPorChavePrimaria(fatMotivoDesdobramento.getTipoAih().getSeq());
		fatMotivoDesdobramento.setTipoAih(tipoAih);
		
		if (fatMotivoDesdobramento.getSeq() == null) {
			fatMotivoDesdobramentoDAO.persistir(fatMotivoDesdobramento);
		} else {
			fatMotivoDesdobramentoDAO.atualizar(fatMotivoDesdobramento);
		}
	}
	
	public void persistirMotivoDesdobramentoClinica(FatMotivoDesdobrClinica fatMotivoDesdobrClinica) throws ApplicationBusinessException{ 
		FatMotivoDesdobramento fatMotivoDesdobramento = fatMotivoDesdobramentoDAO.obterPorChavePrimaria(fatMotivoDesdobrClinica.getMotivoDesdobramento().getSeq());
		AghClinicas aghClinicas = aghClinicasDAO.obterPorChavePrimaria(fatMotivoDesdobrClinica.getAghClinicas().getCodigo());
		
		fatMotivoDesdobrClinica.setAghClinicas(aghClinicas);
		fatMotivoDesdobrClinica.setMotivoDesdobramento(fatMotivoDesdobramento);
				
		FatMotivoDesdobrClinicaId fatMotivoDesdobrClinicaId = new FatMotivoDesdobrClinicaId(fatMotivoDesdobramento.getSeq(), aghClinicas.getCodigo());
		FatMotivoDesdobrClinica fatMotivoDesdobrClinicaDuplicado = fatMotivoDesdobrClinicaDAO.obterPorChavePrimaria(fatMotivoDesdobrClinicaId);
		
		if (fatMotivoDesdobrClinicaDuplicado == null) {
			fatMotivoDesdobrClinicaDAO.persistir(fatMotivoDesdobrClinica);			
		} else {
			throw new ApplicationBusinessException(FaturamentoRNExceptionCode.MENSAGEM_MOTIVO_DESDOBRAMENTO_CLINICA_DUPLICADA);
		}		
	}
	
	public void persistirMotivoDesdobramentoProcedimento(
			FatMotivoDesdobrSsm fatMotivoDesdobrSsm)
			throws ApplicationBusinessException {
		
		FatMotivoDesdobramento fatMotivoDesdobramento = fatMotivoDesdobramentoDAO
				.obterPorChavePrimaria(fatMotivoDesdobrSsm.getId().getMdsSeq());

		FatItensProcedHospitalarId fatItensProcedHospitalarId = new FatItensProcedHospitalarId();
		fatItensProcedHospitalarId.setSeq(fatMotivoDesdobrSsm.getId()
				.getIphSeq());
		fatItensProcedHospitalarId.setPhoSeq(fatMotivoDesdobrSsm.getId()
				.getIphPhoSeq());
		FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO
				.obterPorChavePrimaria(fatItensProcedHospitalarId);

		FatMotivoDesdobrSsmId fatMotivoDesdobrSsmId = new FatMotivoDesdobrSsmId(
				fatMotivoDesdobramento.getSeq(), fatItensProcedHospitalar
						.getId().getSeq(), fatItensProcedHospitalar.getId()
						.getPhoSeq());
		
		fatMotivoDesdobrSsm.setId(fatMotivoDesdobrSsmId);
		
		FatMotivoDesdobrSsm fatMotivoDesdobrSsmDuplicado = fatMotivoDesdobrSsmDAO.obterPorChavePrimaria(fatMotivoDesdobrSsmId);
		
		if (fatMotivoDesdobrSsmDuplicado == null) {
			fatMotivoDesdobrSsmDAO.persistir(fatMotivoDesdobrSsm);
		} else {
			throw new ApplicationBusinessException(FaturamentoRNExceptionCode.MENSAGEM_MOTIVO_DESDOBRAMENTO_PROCEDIMENTO_DUPLICADO);
		}
	}	
	
	public void alterarMotivoDesdobramentoSSM(FatMotivoDesdobrSsm fatMotivoDesdobrSsm) {
		
		FatMotivoDesdobramento fatMotivoDesdobramento = fatMotivoDesdobramentoDAO
				.obterPorChavePrimaria(fatMotivoDesdobrSsm.getId().getMdsSeq());

		FatItensProcedHospitalarId fatItensProcedHospitalarId = new FatItensProcedHospitalarId();
		fatItensProcedHospitalarId.setSeq(fatMotivoDesdobrSsm.getId()
				.getIphSeq());
		fatItensProcedHospitalarId.setPhoSeq(fatMotivoDesdobrSsm.getId()
				.getIphPhoSeq());
		FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO
				.obterPorChavePrimaria(fatItensProcedHospitalarId);

		FatMotivoDesdobrSsmId fatMotivoDesdobrSsmId = new FatMotivoDesdobrSsmId(
				fatMotivoDesdobramento.getSeq(), fatItensProcedHospitalar
						.getId().getSeq(), fatItensProcedHospitalar.getId()
						.getPhoSeq());
		
		fatMotivoDesdobrSsm.setId(fatMotivoDesdobrSsmId);

		fatMotivoDesdobrSsmDAO.atualizar(fatMotivoDesdobrSsm);
		
	}
	
	/**
	 * Método para implementar regras da trigger executada antes da inserção de
	 * <code>FatConvPlanoAcomodacoes</code>.
	 * 
	 * ORADB Trigger FATT_CPA_BRI
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void validarConvenioPlanoAcomodacaoAntesInserir(
			final FatConvPlanoAcomodacoes convPlanoAcomodacao)
			throws ApplicationBusinessException {

		// fatK_cpa_rn.rn_fatp_ver_cps_ativ
		this.validarConvenioPlanoSaudeAtivo(convPlanoAcomodacao
				.getConvenioSaudePlano());
	}

	/**
	 * Método para implementar regras da trigger executada antes da inserção de
	 * <code>FatConvTipoDocumentos</code>.
	 * ORADB Trigger FATT_CTD_BRI
	 * @throws ApplicationBusinessException
	 */
	public void validarConvenioTipoDocumentoAntesInserir(
			final FatConvTipoDocumentos convTipoDocumento)
			throws ApplicationBusinessException {

		// fatK_ctd_rn.rn_fatp_ver_cps_ativ
		this.validarConvenioPlanoSaudeAtivo(convTipoDocumento
				.getConvenioSaudePlano());
	}

	/**
	 * Método para verificar se <code>FatConvenioSaudePlano</code> está ativo.
	 * ORADB Package FATK_CTD_RN, FATK_CPA_RN.
	 * ORADB Procedure RN_FATP_VER_CPS_ATIV
	 * @throws ApplicationBusinessException
	 * 
	 */
	private void validarConvenioPlanoSaudeAtivo(
			final FatConvenioSaudePlano convPlano) throws ApplicationBusinessException {

		if (CoreUtil.igual(convPlano.getIndSituacao(), DominioSituacao.I)) {
			throw new ApplicationBusinessException(FaturamentoRNExceptionCode.FAT_00794);
		}
	}

	public List<FatProcedHospInternos> pesquisarFatProcedHospInternos(
			final Integer codigoMaterial,
			final Integer seqProcedimentoCirurgico,
			final Short seqProcedimentoEspecialDiverso) {
		return getFatProcedHospInternosDAO().pesquisarFatProcedHospInternos(
				codigoMaterial, seqProcedimentoCirurgico,
				seqProcedimentoEspecialDiverso);
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<SubRelatorioLaudosProcSusVO> buscarJustificativasLaudoProcedimentoSUS(
			final Integer seqAtendimento) throws ApplicationBusinessException {
		final List<SubRelatorioLaudosProcSusVO> listaVO = new ArrayList<SubRelatorioLaudosProcSusVO>();
		List<BuscarJustificativasLaudoProcedimentoSusVO> justificativasLaudoProcedimentoSUS;

		String situacaoLiberadoItensSolicitacaoExames = this
				.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		String situacaoNaAreaExecutoraItensSolicitacaoExames = this
				.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		String situacaoExecutandoItensSolicitacaoExames = this
				.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);
		Short tipoGrupoContaSUS = this
				.buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);

		justificativasLaudoProcedimentoSUS = getFatProcedHospInternosDAO()
				.buscarJustificativasLaudoProcedimentoSUSSolicitacaoExame(
						seqAtendimento, situacaoLiberadoItensSolicitacaoExames,
						situacaoNaAreaExecutoraItensSolicitacaoExames,
						situacaoExecutandoItensSolicitacaoExames,
						tipoGrupoContaSUS);

		if (justificativasLaudoProcedimentoSUS != null
				&& !justificativasLaudoProcedimentoSUS.isEmpty()) {
			for (final BuscarJustificativasLaudoProcedimentoSusVO justificativaLaudoProcedimentoSusVO : justificativasLaudoProcedimentoSUS) {
				if (CoreUtil.igual(
						DominioOrigemAtendimento.I,
						this.getExamesLaudosFacade()
								.buscaLaudoOrigemPaciente(
										justificativaLaudoProcedimentoSusVO
												.getSoeSeq()))) {
					final SubRelatorioLaudosProcSusVO vo = new SubRelatorioLaudosProcSusVO();
					final Integer auxSeq = justificativaLaudoProcedimentoSusVO
							.getPhiSeq() != null ? justificativaLaudoProcedimentoSusVO
							.getPhiSeq() : justificativaLaudoProcedimentoSusVO
							.getSeq();

					vo.setOrdem(5);
					vo.setProcedimentoCodigo(this.getPrescricaoMedicaFacade()
							.buscaDescricaoProcedimentoHospitalarInterno(
									auxSeq, (short) 1, (byte) 1, (short) 1));
					vo.setProcedimentoDescricao(this
							.getPrescricaoMedicaFacade()
							.buscaProcedimentoHospitalarInternoAgrupa(auxSeq,
									(short) 1, (byte) 1, (short) 1));
					vo.setJustificativa(this.getExamesLaudosFacade()
							.buscaJustificativaLaudo(seqAtendimento, auxSeq));

					if (!listaVO.contains(vo)) {
						listaVO.add(vo);
					}
				}
			}
		}

		justificativasLaudoProcedimentoSUS = getFatProcedHospInternosDAO()
				.buscarJustificativasLaudoProcedimentoSUSComponentesSanguineos(
						seqAtendimento);

		if (justificativasLaudoProcedimentoSUS != null
				&& !justificativasLaudoProcedimentoSUS.isEmpty()) {
			for (final BuscarJustificativasLaudoProcedimentoSusVO justificativaLaudoProcedimentoSusVO : justificativasLaudoProcedimentoSUS) {
				final SubRelatorioLaudosProcSusVO vo = new SubRelatorioLaudosProcSusVO();
				final Integer auxSeq = justificativaLaudoProcedimentoSusVO
						.getPhiSeq() != null ? justificativaLaudoProcedimentoSusVO
						.getPhiSeq() : justificativaLaudoProcedimentoSusVO
						.getSeq();

				vo.setOrdem(6);
				vo.setProcedimentoCodigo(this.getPrescricaoMedicaFacade()
						.buscaDescricaoProcedimentoHospitalarInterno(auxSeq,
								(short) 1, (byte) 1, (short) 1));
				vo.setProcedimentoDescricao(this.getPrescricaoMedicaFacade()
						.buscaProcedimentoHospitalarInternoAgrupa(auxSeq,
								(short) 1, (byte) 1, (short) 1));
				vo.setJustificativa(this.getBancoDeSangueFacade()
						.buscaJustificativaLaudoCsa(seqAtendimento, auxSeq));

				if (!listaVO.contains(vo)) {
					listaVO.add(vo);
				}
			}
		}

		justificativasLaudoProcedimentoSUS = getFatProcedHospInternosDAO()
				.buscarJustificativasLaudoProcedimentoSUSProcedHemoterapicos(
						seqAtendimento);
		if (justificativasLaudoProcedimentoSUS != null
				&& !justificativasLaudoProcedimentoSUS.isEmpty()) {
			for (final BuscarJustificativasLaudoProcedimentoSusVO justificativaLaudoProcedimentoSusVO : justificativasLaudoProcedimentoSUS) {
				final SubRelatorioLaudosProcSusVO vo = new SubRelatorioLaudosProcSusVO();
				final Integer auxSeq = justificativaLaudoProcedimentoSusVO
						.getPhiSeq() != null ? justificativaLaudoProcedimentoSusVO
						.getPhiSeq() : justificativaLaudoProcedimentoSusVO
						.getSeq();

				vo.setOrdem(7);
				vo.setProcedimentoCodigo(this.getPrescricaoMedicaFacade()
						.buscaDescricaoProcedimentoHospitalarInterno(auxSeq,
								(short) 1, (byte) 1, (short) 1));
				vo.setProcedimentoDescricao(this.getPrescricaoMedicaFacade()
						.buscaProcedimentoHospitalarInternoAgrupa(auxSeq,
								(short) 1, (byte) 1, (short) 1));
				vo.setJustificativa(this.getBancoDeSangueFacade()
						.buscaJustificativaLaudoPhe(seqAtendimento, auxSeq));

				if (!listaVO.contains(vo)) {
					listaVO.add(vo);
				}
			}
		}
		// #40967
		Integer tctSeq = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_AGHU_COD_CARACTERISTICA_JUSTIFICATIVA);
		Integer tptSeq = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_TIPO_TRAT_FISIOTERAPIA);
		Short cpgCphCspCnvCodigo = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS);
		Byte cpgCphCspSeq = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
		Short cpgGrcSeq = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		Short iphPhoSeq = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		//#51731
		Integer codNutricaoEnteral = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_NUTRICAO_ENTERAL);
		
		listaVO.addAll(getFatItemContaHospitalarDAO().buscarJustificativasLaudoProcedimentoSUSProcedFisioterapia(seqAtendimento,
				tctSeq, tptSeq, cpgCphCspCnvCodigo, cpgCphCspSeq, cpgGrcSeq, iphPhoSeq, codNutricaoEnteral));
		
		return listaVO;
	}
	
	public List<SubRelatorioMudancaProcedimentosVO> buscarMudancaProcedimentos(Integer seqAtendimento, Integer apaSeq, Short seqp) {
		return getFatItemContaHospitalarDAO().buscarMudancaProcedimentos(seqAtendimento, apaSeq, seqp);
	}

	public void persistirCidContaHospitalar(
			final FatCidContaHospitalar cidContaHospitalar, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		getCidContaHospitalarPersist().setComFlush(true);
		getCidContaHospitalarPersist().inserir(cidContaHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
	}

	private FatContaInternacaoPersist getFatContaInternacaoPersist() {
		return fatContaInternacaoPersist;
	}

	public void inserirContaInternacao(final FatContasInternacao contaInternacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.inserirContaInternacao(contaInternacao, true, nomeMicrocomputador, dataFimVinculoServidor);
	}

	public void inserirContaInternacao(final FatContasInternacao contaInternacao,
			boolean flush, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		getFatContaInternacaoPersist().inserir(contaInternacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	public void removerContaInternacao(final FatContasInternacao contaInternacao,
			boolean flush, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		getFatContaInternacaoPersist().remover(contaInternacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	public void persistirFatAgrupItemConta(final FatAgrupItemConta fatAgrupItemConta) {
		getFatAgrupItemContaDAO().persistir(fatAgrupItemConta);
	}

	private FatDiariaUtiDigitadaJnDAO getFatDiariaUtiDigitadaJnDAO() {
		return fatDiariaUtiDigitadaJnDAO;
	}

	public void excluirFatContaSugestaoDesdobr(
			final FatContaSugestaoDesdobr fatContaSugestaoDesdobr) {
		getFatContaSugestaoDesdobrDAO().remover(fatContaSugestaoDesdobr);
	}

	public void excluirFatEspelhoItemContaHosp(
			final FatEspelhoItemContaHosp fatEspelhoItemContaHosp, boolean flush) {
		getFatEspelhoItemContaHospDAO().remover(fatEspelhoItemContaHosp);
	}

	public FatEspelhoItemContaHosp atualizarFatEspelhoItemContaHosp(
			final FatEspelhoItemContaHosp fatEspelhoItemContaHosp, boolean flush) {
		FatEspelhoItemContaHosp retorno = getFatEspelhoItemContaHospDAO().merge(
				fatEspelhoItemContaHosp);
		if (flush){
			getFatEspelhoItemContaHospDAO().flush();
		}
		return retorno;
	}

	public FatEspelhoItemContaHosp atualizarFatEspelhoItemContaHosp(
			final FatEspelhoItemContaHosp fatEspelhoItemContaHosp) {
		return atualizarFatEspelhoItemContaHosp(fatEspelhoItemContaHosp, true);
	}

	public FatEspelhoItemContaHosp inserirFatEspelhoItemContaHosp(
			final FatEspelhoItemContaHosp fatEspelhoItemContaHosp, boolean flush) {
		getFatEspelhoItemContaHospDAO().persistir(fatEspelhoItemContaHosp);
		if (flush){
			getFatEspelhoItemContaHospDAO().flush();
		}
		return fatEspelhoItemContaHosp;
	}

	public void excluirFatPerdaItemConta(final FatPerdaItemConta fatPerdaItemConta) {
		getFatPerdaItemContaDAO().remover(fatPerdaItemConta);
		getFatPerdaItemContaDAO().flush();
	}

	public void excluirFatValorContaHospitalar(
			final FatValorContaHospitalar fatValorContaHospitalar) {
		getFatValorContaHospitalarDAO().remover(fatValorContaHospitalar);
		getFatValorContaHospitalarDAO().flush();
	}

	/**
	 * Método para verificar se exame possui a caracteristica desejada
	 * 
	 * ORADB FATC_VER_CARACT_IPH
	 * 
	 * @param seqIph
	 * @param seqIphPho
	 * @return
	 */
	public Boolean verificarCaracteristicaExame(final Integer seqIph,
			final Short seqIphPho, final DominioFatTipoCaractItem caracteristica) {
		return getVerificaCaracteristicaItemProcedimentoHospitalarRN().verificarCaracteristicaItemProcHosp(seqIphPho, seqIph, caracteristica);
	}

	public String selecionarSsmEntreSolicitadoRealizado(
			final DominioSituacaoSSM situacaoSSM, final String ssmSol,
			final String ssmReal, final String msgSsmSolNull,
			final String msgSsmRealNull) {

		String result = null;

		if (DominioSituacaoSSM.S.equals(situacaoSSM)) {
			// Busca ssm na fat_espelhos_aih quando for uma conta
			// encerrada ou cobrada/emitida ou rejeitada(situacao =
			// 'E' ou 'O' ou 'R')
			result = ssmSol;
			if (result == null) {
				this.logDebug(msgSsmSolNull);
			}
		} else if (DominioSituacaoSSM.R.equals(situacaoSSM)) {
			result = ssmReal;
			if (result == null) {
				this.logDebug(msgSsmRealNull);
			}
		}
		return result;
	}

	/**
	 * ORADB: Function FATC_BUSCA_SSM ORADB: Function FATC_BUSCA_SSM_COMPL
	 * 
	 */
	public String selecionarSsmConformeSituacaoContaSsm(
			final DominioSituacaoSSM situacaoSSM,
			final DominioSituacaoConta situacaoConta, final Integer cthSeq,
			final String ssmSolA, final String ssmRealA, final String ssmSolB,
			final String ssmRealB) {

		String result = null;
		String ssmSolANulo = null;
		String ssmRealANulo = null;
		String ssmSolBNulo = null;
		String ssmRealBNulo = null;

		try {
			if (situacaoConta != null) {
				switch (situacaoConta) {
				case E:
				case O:
				case R:
					ssmSolANulo = "not found - proced solic CTH" + cthSeq;
					ssmRealANulo = "not found - proced realiz CTH" + cthSeq;
					result = this.selecionarSsmEntreSolicitadoRealizado(
							situacaoSSM, ssmSolA, ssmRealA, ssmSolANulo,
							ssmRealANulo);
					break;
				case A:
				case F:
					ssmSolBNulo = "not found2 - proced solic EAI" + cthSeq;
					ssmRealBNulo = "not found2 -proced realiz EAI" + cthSeq;
					result = this.selecionarSsmEntreSolicitadoRealizado(
							situacaoSSM, ssmSolB, ssmRealB, ssmSolBNulo,
							ssmRealBNulo);
					break;
				default:
					this.logDebug(
							"situacao diferente de F, E, R, A ou O." + cthSeq);
					result = null;
					break;
				}
			} else {
				this.logDebug("not found - conta: " + cthSeq);
			}
		} catch (final Exception e) {
			this.logDebug("A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: " + e.getMessage());
		}
		return result;
	}

	/**
	 * Para permitir testes
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Short buscarTipoGrupoContaSus() throws ApplicationBusinessException {
		Short result = this
				.buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);

		return result;
	}

	public Map<Integer, ParSsmSolicRealizVO> popularMapaVoComSsm(
			List<ParCthSeqSituacaoContaVO> listaCthSit) {
		Map<Integer, ParSsmSolicRealizVO> result = null;
		ParSsmSolicRealizVO resItem = null;
		List<String> listaStrSsm = null;
		String ssmSol = null;
		String ssmReal = null;

		result = new HashMap<Integer, ParSsmSolicRealizVO>();
		for (ParCthSeqSituacaoContaVO vo : listaCthSit) {
			listaStrSsm = vo.getListaSsmStr();
			ssmSol = this.selecionarSsmConformeSituacaoContaSsm(
					DominioSituacaoSSM.S, vo.getSituacaoConta(),
					vo.getCthSeq(), listaStrSsm.get(0), listaStrSsm.get(1),
					listaStrSsm.get(2), listaStrSsm.get(3));
			ssmReal = this.selecionarSsmConformeSituacaoContaSsm(
					DominioSituacaoSSM.R, vo.getSituacaoConta(),
					vo.getCthSeq(), listaStrSsm.get(0), listaStrSsm.get(1),
					listaStrSsm.get(2), listaStrSsm.get(3));
			resItem = new ParSsmSolicRealizVO();
			resItem.setCthSeq(vo.getCthSeq());
			resItem.setSsmStrSolicitado(ssmSol);
			resItem.setSsmStrRealizado(ssmReal);
			result.put(resItem.getCthSeq(), resItem);
		}

		return result;
	}

	public Map<Integer, ParCthSeqSituacaoContaVO> obterMapaVoParcialParaListSsm(
			List<ParCthSeqSituacaoContaVO> listaCthSit) {
		Map<Integer, ParCthSeqSituacaoContaVO> result = null;
		List<String> listaStrSsm = null;

		result = new HashMap<Integer, ParCthSeqSituacaoContaVO>();
		for (ParCthSeqSituacaoContaVO vo : listaCthSit) {
			listaStrSsm = new LinkedList<String>();
			for (int i = 0; i < 4; i++) {
				listaStrSsm.add(null);
			}
			vo.setListaSsmStr(listaStrSsm);
			result.put(vo.getCthSeq(), vo);
		}

		return result;
	}

	public void popularListaStrSsm(Map<Integer, ParCthSeqSituacaoContaVO> parcial,
			List<ParSsmSolicRealizVO> listaA, List<ParSsmSolicRealizVO> listaB) {
		Integer cthSeq = null;
		List<String> listaSsmStr = null;
		ParCthSeqSituacaoContaVO voParcial = null;

		for (ParSsmSolicRealizVO vo : listaA) {
			cthSeq = vo.getCthSeq();
			voParcial = parcial.get(cthSeq);
			if (voParcial != null) {
				listaSsmStr = voParcial.getListaSsmStr();
				listaSsmStr.set(0, vo.getSsmStrSolicitado());
				listaSsmStr.set(1, vo.getSsmStrRealizado());
			} else {
				this.logInfo(
						"CTH SEQ: " + cthSeq
								+ " nao encontrado em mapa parcial de SSMs");
			}
		}
		for (ParSsmSolicRealizVO vo : listaB) {
			cthSeq = vo.getCthSeq();
			voParcial = parcial.get(cthSeq);
			if (voParcial != null) {
				listaSsmStr = voParcial.getListaSsmStr();
				listaSsmStr.set(2, vo.getSsmStrSolicitado());
				listaSsmStr.set(3, vo.getSsmStrRealizado());
			} else {
				this.logInfo(
						"CTH SEQ: " + cthSeq
								+ " nao encontrado em mapa parcial de SSMs");
			}
		}
	}

	static List<ParSsmSolicRealizVO> agregarListas(
			final List<ParCthSeqSsmVO> listaParSol,
			final List<ParCthSeqSsmVO> listaParReal) {
		List<ParSsmSolicRealizVO> result = null;
		Map<Integer, ParSsmSolicRealizVO> parcial = null;
		Integer cthSeq = null;
		ParSsmSolicRealizVO vo = null;

		result = new LinkedList<ParSsmSolicRealizVO>();
		parcial = new HashMap<Integer, ParSsmSolicRealizVO>();
		for (ParCthSeqSsmVO s : listaParSol) {
			cthSeq = s.getCthSeq();
			vo = parcial.get(cthSeq);
			if (vo == null) {
				vo = new ParSsmSolicRealizVO();
				vo.setCthSeq(cthSeq);
				parcial.put(cthSeq, vo);
			}
			if (vo.getSsmStrSolicitado() == null) {
				vo.setSsmStrSolicitado(s.getSsmStr());
			}
		}
		for (ParCthSeqSsmVO r : listaParReal) {
			cthSeq = r.getCthSeq();
			vo = parcial.get(cthSeq);
			if (vo == null) {
				vo = new ParSsmSolicRealizVO();
				vo.setCthSeq(cthSeq);
				parcial.put(cthSeq, vo);
			}
			if (vo.getSsmStrRealizado() == null) {
				vo.setSsmStrRealizado(r.getSsmStr());
			}
		}
		result = new LinkedList<ParSsmSolicRealizVO>(parcial.values());

		return result;
	}

	/**
	 * ORADB: Function FATC_BUSCA_SSM
	 * 
	 */
	public Map<Integer, ParSsmSolicRealizVO> listarSsmParaListaCthSeqSsmSolicRealiz(
			final List<Integer> listaCthSeq, final Short cspCnvCodigo,
			final Byte cspSeq) {
		Map<Integer, ParSsmSolicRealizVO> result = null;
		List<ParCthSeqSituacaoContaVO> listaCthSit = null;
		Map<Integer, ParCthSeqSituacaoContaVO> parcial = null;
		List<ParSsmSolicRealizVO> listaDeCth = null;
		List<ParSsmSolicRealizVO> listaDeEai = null;
		List<ParCthSeqSsmVO> listaParSol = null;
		List<ParCthSeqSsmVO> listaParReal = null;
		FatEspelhoAihDAO eaiDao = null;
		FatContasHospitalaresDAO cthDao = null;
		Short tipoGrupoContaSUS = null;

		eaiDao = this.getFatEspelhoAihDAO();
		cthDao = this.getFatContasHospitalaresDAO();
		// obtem lista de itens a serem incluidos no retorno
		listaCthSit = cthDao.listarSeqIndSituacao(listaCthSeq);
		parcial = this.obterMapaVoParcialParaListSsm(listaCthSit);
		// listas de strings SSM
		listaParSol = eaiDao.listarSsmParaListaCthSeq(listaCthSeq,
				cspCnvCodigo, cspSeq, DominioSituacaoSSM.S);
		listaParReal = eaiDao.listarSsmParaListaCthSeq(listaCthSeq,
				cspCnvCodigo, cspSeq, DominioSituacaoSSM.R);
		listaDeEai = agregarListas(listaParSol, listaParReal);
		try {
			tipoGrupoContaSUS = this.buscarTipoGrupoContaSus();
		} catch (ApplicationBusinessException e) {
			listaDeCth = Collections.emptyList();
		}
		if (tipoGrupoContaSUS != null) {
			listaParSol = cthDao.listarSsmParaListaCthSeq(listaCthSeq,
					cspCnvCodigo, cspSeq, tipoGrupoContaSUS,
					DominioSituacaoSSM.S);
			listaParReal = cthDao.listarSsmParaListaCthSeq(listaCthSeq,
					cspCnvCodigo, cspSeq, tipoGrupoContaSUS,
					DominioSituacaoSSM.R);
			listaDeCth = agregarListas(listaParSol, listaParReal);
		}
		// adiciona resultados das listas no vo parcial
		this.popularListaStrSsm(parcial, listaDeEai, listaDeCth);
		// processa e transfere resultado para mapa final
		result = this.popularMapaVoComSsm(listaCthSit);

		return result;
	}

	/**
	 * ORADB: Function FATC_BUSCA_SSM_COMPL
	 * 
	 */
	public Map<Integer, ParSsmSolicRealizVO> listarSsmComplexidadeParaListaCthSeqSsmSolicRealiz(
			final List<Integer> listaCthSeq, final Short cspCnvCodigo,
			final Byte cspSeq) {
		Map<Integer, ParSsmSolicRealizVO> result = null;
		List<ParCthSeqSituacaoContaVO> listaCthSit = null;
		Map<Integer, ParCthSeqSituacaoContaVO> parcial = null;
		List<ParSsmSolicRealizVO> listaDeCfcA = null;
		List<ParSsmSolicRealizVO> listaDeCfcB = null;
		List<ParCthSeqSsmVO> listaParSol = null;
		List<ParCthSeqSsmVO> listaParReal = null;
		FatCaractFinanciamentoDAO cfcDao = null;
		FatContasHospitalaresDAO cthDao = null;
		Short tipoGrupoContaSUS = null;

		cfcDao = this.getFatCaractFinanciamentoDAO();
		cthDao = this.getFatContasHospitalaresDAO();
		// obtem lista de itens a serem incluidos no retorno
		listaCthSit = cthDao.listarSeqIndSituacao(listaCthSeq);
		parcial = this.obterMapaVoParcialParaListSsm(listaCthSit);
		// listas de strings SSM
		listaParSol = cfcDao.listarSsmComplexParaListaCthSeq(listaCthSeq,
				cspCnvCodigo, cspSeq, DominioSituacaoSSM.S);
		listaParReal = cfcDao.listarSsmComplexParaListaCthSeq(listaCthSeq,
				cspCnvCodigo, cspSeq, DominioSituacaoSSM.R);
		listaDeCfcA = agregarListas(listaParSol, listaParReal);
		try {
			tipoGrupoContaSUS = this.buscarTipoGrupoContaSus();
		} catch (ApplicationBusinessException e) {
			listaDeCfcB = Collections.emptyList();
		}
		if (tipoGrupoContaSUS != null) {
			listaParSol = cfcDao
					.listarSsmAbertaFechadaComplexidadeParaListaCthSeq(
							listaCthSeq, cspCnvCodigo, cspSeq,
							tipoGrupoContaSUS, DominioSituacaoSSM.S);
			listaParReal = cfcDao
					.listarSsmAbertaFechadaComplexidadeParaListaCthSeq(
							listaCthSeq, cspCnvCodigo, cspSeq,
							tipoGrupoContaSUS, DominioSituacaoSSM.R);
			listaDeCfcB = agregarListas(listaParSol, listaParReal);
		}
		// adiciona resultados das listas no vo parcial
		this.popularListaStrSsm(parcial, listaDeCfcA, listaDeCfcB);
		// processa e transfere resultado para mapa final
		result = this.popularMapaVoComSsm(listaCthSit);

		return result;
	}

	/**
	 * ORADB FATC_VER_PHI
	 * 
	 * @param chtSeq
	 * @param phiSeq
	 * @return
	 */
	public boolean verificaProcedimentoHospitalarInterno(final Integer chtSeq,
			final Integer phiSeq) {
		final Long count = getFatItemContaHospitalarDAO()
				.verificaProcedimentoHospitalarInternoCount(chtSeq, phiSeq);
		return count != null && count > 0;
	}

	/**
	 * ORADB FATC_BUSCA_MODALIDADE
	 * 
	 * @param phoSeq
	 * @param iphSeq
	 * @param dataInternacao
	 * @param dataAlta
	 * @return
	 */
	public Short buscaModalidade(final Short phoSeq, final Integer iphSeq,
			final Date dataInternacao, final Date dataAlta) {
		final List<FatProcedimentoModalidade> listaProcedimentosModalidade = getFatProcedimentoModalidadeDAO()
				.listarProcedimentosModalidade(phoSeq, iphSeq);

		if (listaProcedimentosModalidade == null
				|| listaProcedimentosModalidade.isEmpty()) {
			return 0;
		} else if (listaProcedimentosModalidade.size() == 1) {
			return listaProcedimentosModalidade.get(0).getId().getMotCodigo();
		} else if (dataAlta != null
				&& DateValidator.validarMesmoDia(dataInternacao, dataAlta)) {
			return 3; // Hospital Dia
		} else {
			return 2;
		}
	}

	/**
	 * FUNCTION ORADB: FATC_VER_CARACT_PHI_QR, FATC_VER_CARACT_PHI
	 * 
	 * @param cnvCodigo
	 * @param cspSeq
	 * @param phiSeq
	 * @param caracteristica
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public Boolean verificarCaracteristicaPhi(final Short cnvCodigo,
			final Byte cspSeq, final Integer phiSeq,
			final DominioFatTipoCaractItem caracteristica)
			throws ApplicationBusinessException {
		final Short grcSeq = this
				.buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		final List<VFatAssociacaoProcedimento> listaVFatAssociacaoProcedimentos = this
				.getVFatAssociacaoProcedimentoDAO()
				.listarAssociacaoProcedimentoParaCaracteristica(cnvCodigo,
						cspSeq, phiSeq, grcSeq);
		if (listaVFatAssociacaoProcedimentos.size() == 0) {
			return false;
		}
		final VFatAssociacaoProcedimento associacaoProcedimento = listaVFatAssociacaoProcedimentos
				.get(0);
		final List<FatTipoCaractItens> listaFatTipoCaractItens = this
				.getFatTipoCaractItensDAO()
				.listarTipoCaractItensPorCaracteristica(
						caracteristica.getDescricao());
		final FatTipoCaractItens tipoCaracteristicaItem = listaFatTipoCaractItens
				.get(0);
		final FatItensProcedHospitalarId itemProcHospId = new FatItensProcedHospitalarId();
		itemProcHospId.setPhoSeq(associacaoProcedimento.getId().getIphPhoSeq());
		itemProcHospId.setSeq(associacaoProcedimento.getId().getIphSeq());
		final FatCaractItemProcHosp caractItemProcHosp = this
				.getFatCaractItemProcHospDAO().obterPorItemProcHospTipoCaract(
						itemProcHospId, tipoCaracteristicaItem.getSeq());
		if (caractItemProcHosp == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ORADB Function FATC_VER_CARACT_PHI
	 * 
	 * A partir de um phi da tabela pmr verifica  nas
     * tabelas do faturamento se o procedimento possui a característica desejada associada ao iph,
     * retorna v_vlr_number, v_vlr_char, v_vlr_date, e se o phi é de aparelho  
	 * 
	 * @param cnvCodigo
	 * @param cspSeq
	 * @param phiSeq
	 * @param caracteristica
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public CaracteristicaPhiVO fatcVerCaractPhi(Short cnvCodigo, Byte cspSeq, Integer phiSeq, String caracteristica) throws ApplicationBusinessException {
		 
		 CaracteristicaPhiVO retorno = new CaracteristicaPhiVO();
		 
		 Short tipoGrupoContaSus = buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		 Integer tctSeq = null;
		 
		 List<CursorVaprVO> listaVapr = getVFatAssociacaoProcedimentoDAO().listarAssociacaoProcedimento(cnvCodigo, cspSeq, phiSeq, tipoGrupoContaSus);
		 
		 if (listaVapr.isEmpty()) {
			 retorno.setResultado(Boolean.FALSE);
		 }
		 else {
			 List<FatTipoCaractItens> listaTipoCaracter = getFatTipoCaractItensDAO().listarTipoCaractItensPorCaracteristica(caracteristica);
			 if (!listaTipoCaracter.isEmpty()) {
				 tctSeq = listaTipoCaracter.get(0).getSeq();
			 }
			 
			 FatCaractItemProcHosp caractItemProcHosp = getFatCaractItemProcHospDAO().obterPorChavePrimaria(new FatCaractItemProcHospId(listaVapr.get(0).getIphPhoSeq(), listaVapr.get(0).getIphSeq(), tctSeq));
			 
			 if (caractItemProcHosp == null) {
				 retorno.setResultado(Boolean.FALSE);
			 }
			 else {
				 retorno.setResultado(Boolean.TRUE);
				 retorno.setValorChar(caractItemProcHosp.getValorChar());
				 retorno.setValorData(caractItemProcHosp.getValorData());
				 retorno.setValorNumerico(caractItemProcHosp.getValorNumerico());
			 }
		 }
		 
		 return retorno;
	}
	
	
	/**
	 * ORADB FATC_BUSCA_SENHA_CERIH
	 * 
	 * @param cthSeq
	 * @return
	 */
	public String buscaSenhaCerih(final Integer cthSeq) {
		final List<FatContasInternacao> contasInternacao = getFatContasInternacaoDAO()
				.listarContasInternacao(cthSeq);

		if (contasInternacao != null && !contasInternacao.isEmpty()) {
			for (final FatContasInternacao contaInternacao : contasInternacao) {
				final AinInternacao internacao = contaInternacao
						.getInternacao();

				if (internacao != null) {
					final List<AinDiariasAutorizadas> diariasAutorizadas = getInternacaoFacade()
							.pesquisarDiariasAutorizadasOrdenadoCriadoEmDesc(
									internacao.getSeq());

					if (diariasAutorizadas != null
							&& !diariasAutorizadas.isEmpty()) {
						for (final AinDiariasAutorizadas diariaAutorizada : diariasAutorizadas) {
							final String senha = diariaAutorizada.getSenha();

							if (senha != null) {
								return senha.replaceAll("[^0123456789]", "");
							}
						}
					}
				}
			}
		}

		return null;
	}

	public void geraSequenciaAtos( final Integer cthSeq, final Short seqIPHO, final String nomeMicrocomputador, 
			final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		/*final String vRealizado =*/ 
				geracaoSequenciaAtoMedicoAihRN
				.gerarSequenciaAtoMedicaAihPorCthSeq(cthSeq, nomeMicrocomputador, dataFimVinculoServidor);		
		//Marina 16/04/2014: de acordo com portaria número 2.947 de 21/12/2012
		/*logar("*** GERA SEQUENCIA PELA COMPATIBILIDADE ***");
		logar("V_REALIZADO_CONTA: ", vRealizado);
	
		if(vRealizado != null){
			final Integer seqIPH = getFatItensProcedHospitalarDAO().obterIphSeqPorPhoSeqCodTabela(seqIPHO, Long.valueOf(vRealizado));
		
			if(seqIPH != null &&
				verificarCaracteristicaExame(seqIPH, seqIPHO, 
						DominioFatTipoCaractItem.GERA_SEQUENCIA_POR_COMPATIBILIDADE_DO_PRINCIPAL)
						){
				
				logar("*** ACHOU CARACTERISTICA, TEM QUE ORDENAR PELA COMPATIBILIDADE ***");
				fatAtualizacoesCompatibilidadeRN.geraSequenciaCompatibilidade( cthSeq, seqIPH, seqIPHO, 
																					getFatAtoMedicoAihDAO());
			}
		}*/
	}

	/**
	 * ORADB FUNCTION FATC_ATUAL_LAUD_DESD
	 * 
	 * @param pInt
	 * @param pDtDesdobr
	 * @return
	 * @throws BaseException
	 */
	public Boolean fatcAtualLaudDesd(Integer pInt, Date pDtDesdobr)
			throws BaseException {
		AghAtendimentos atendimento = getAghuFacade()
				.buscarAtendimentosPorCodigoInternacao(pInt);
		if (atendimento == null) {
			return false;
		}
		Integer vAtdSeq = atendimento.getSeq();

		List<MpmLaudo> listaLaudos = getPrescricaoMedicaFacade()
				.listarLaudosPorAtendimentoData(vAtdSeq, pDtDesdobr);
		if (listaLaudos != null && !listaLaudos.isEmpty()) {
			for (MpmLaudo regLaudo : listaLaudos) {
				logDebug("Encontrou laudo");
				MpmLaudo laudoOld = null;
				try {
					laudoOld = (MpmLaudo) BeanUtils.cloneBean(regLaudo);
				} catch (Exception e) {
					logError("Exceção capturada: ", e);
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}
				regLaudo.setDthrFimValidade(pDtDesdobr);
				regLaudo.setContaDesdobrada(Boolean.TRUE);
				getPrescricaoMedicaFacade().atualizarLaudo(regLaudo, laudoOld);
				logDebug("Fez update");

				if (pDtDesdobr != null
						&&  DateUtil.validaDataMaior(pDtDesdobr, regLaudo.getDthrFimPrevisao())) {
					regLaudo.setDthrFimPrevisao(pDtDesdobr);
				}

				try {
					MpmLaudo newLaudo = new MpmLaudo();
					newLaudo.setDthrInicioValidade(pDtDesdobr);
					newLaudo.setDthrFimValidade(regLaudo.getDthrFimValidade());
					newLaudo.setDthrFimPrevisao(regLaudo.getDthrFimPrevisao());
					newLaudo.setJustificativa(regLaudo.getJustificativa());
					newLaudo.setContaDesdobrada(Boolean.FALSE);
					newLaudo.setImpresso(Boolean.FALSE);
					newLaudo.setDuracaoTratSolicitado(regLaudo
							.getDuracaoTratSolicitado());
					newLaudo.setLaudoManual(regLaudo.getLaudoManual());
					newLaudo.setProcedimentoHospitalarInterno(regLaudo
							.getProcedimentoHospitalarInterno());
					newLaudo.setAtendimento(regLaudo.getAtendimento());
					newLaudo.setLaudo(regLaudo);
					newLaudo.setTipoLaudo(regLaudo.getTipoLaudo());
					newLaudo.setServidorFeitoManual(regLaudo
							.getServidorFeitoManual());
					newLaudo.setPrescricaoProcedimento(regLaudo
							.getPrescricaoProcedimento());
					newLaudo.setPrescricaoNpts(regLaudo.getPrescricaoNpts());
					newLaudo.setItemPrescricaoMdtos(regLaudo
							.getItemPrescricaoMdtos());
					getPrescricaoMedicaFacade().inserirLaudo(newLaudo);
					logDebug("Inseriu");
				} catch (Exception e) {
					logDebug("A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: " + e.getMessage());
					return false;
				}
			}
		}
		return true;
	}
	
	protected FatEspelhoAihDAO getFatEspelhoAihDAO() {
		return fatEspelhoAihDAO;
	}

	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}

	protected FatCaractFinanciamentoDAO getFatCaractFinanciamentoDAO() {
		return fatCaractFinanciamentoDAO;
	}

	protected FatProcedimentoModalidadeDAO getFatProcedimentoModalidadeDAO() {
		return fatProcedimentoModalidadeDAO;
	}

	protected VFatAssociacaoProcedimentoDAO getVFatAssociacaoProcedimentoDAO() {
		return vFatAssociacaoProcedimentoDAO;
	}

	protected FatTipoCaractItensDAO getFatTipoCaractItensDAO() {
		return fatTipoCaractItensDAO;
	}

	protected FatCaractItemProcHospDAO getFatCaractItemProcHospDAO() {
		return fatCaractItemProcHospDAO;
	}

	protected FatContasInternacaoDAO getFatContasInternacaoDAO() {
		return fatContasInternacaoDAO;
	}

	protected FatContaSugestaoDesdobrDAO getFatContaSugestaoDesdobrDAO() {
		return fatContaSugestaoDesdobrDAO;
	}

	protected FatProcedimentoHospitalarDAO getFatProcedimentoHospitalarDAO() {
		return fatProcedimentoHospitalarDAO;
	}

	protected VerificaCaracteristicaItemProcedimentoHospitalarRN getVerificaCaracteristicaItemProcedimentoHospitalarRN() {

		return verificaCaracteristicaItemProcedimentoHospitalarRN;
	}

	public FatProcedimentosHospitalares obterFatProcedimentosHospitalaresPadrao() {
		FatProcedimentosHospitalares retorno = null;
		try {
			final Short param = this
					.buscarVlrShortAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			final List<FatProcedimentosHospitalares> list = getFatProcedimentoHospitalarDAO()
					.listarProcedimentosHospitalaresPorSeqEDescricao(param);
			if (list != null && list.size() == 1) {
				retorno = list.get(0);
			}
		} catch (final BaseException e) {
			logWarn(e);
		}
		return retorno;
	}

	protected FatPendenciaContaHospDAO getFatPendenciaContaHospDAO() {
		return fatPendenciaContaHospDAO;
	}

	/**
	 * Método para implementar regras da trigger executada antes da inserção de
	 * <code>FatPendenciaContaHosp</code>.
	 * 
	 * ORADB Trigger FATT_FPC_BRI
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 * @throws ApplicationBusinessException
	 */
	protected void fattFpcBri(final FatPendenciaContaHosp fatPendenciaContaHosp)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		fatPendenciaContaHosp.setCriadoEm(new Date());
		if (servidorLogado == null) {
			FaturamentoExceptionCode.FAT_00418.throwException();
		}
		fatPendenciaContaHosp.setSerMatricula(servidorLogado.getId().getMatricula());
		fatPendenciaContaHosp.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
	}

	/**
	 * Método para implementar regras da trigger executada antes da atualização
	 * de <code>FatPendenciaContaHosp</code>.
	 * 
	 * ORADB Trigger FATT_FPC_BRU
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void fattFpcBru(final FatPendenciaContaHosp fatPendenciaContaHosp)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		fatPendenciaContaHosp.setAlteradoEm(new Date());
		if (servidorLogado == null) {
			FaturamentoExceptionCode.FAT_00418.throwException();
		}
		fatPendenciaContaHosp.setSerMatriculaAlterado(servidorLogado.getId().getMatricula());
		fatPendenciaContaHosp.setSerVinCodigoAlterado(servidorLogado.getId().getVinCodigo());
	}

	public void inserir(final FatPendenciaContaHosp fatPendenciaContaHosp)
			throws ApplicationBusinessException {
		this.fattFpcBri(fatPendenciaContaHosp);
		getFatPendenciaContaHospDAO().persistir(fatPendenciaContaHosp);
		getFatPendenciaContaHospDAO().flush();
	}

	public void atualizar(final FatPendenciaContaHosp fatPendenciaContaHosp)
			throws ApplicationBusinessException {
		this.fattFpcBru(fatPendenciaContaHosp);
		getFatPendenciaContaHospDAO()
				.merge(fatPendenciaContaHosp);
		getFatPendenciaContaHospDAO().flush();
	}

	public void remover(final FatPendenciaContaHosp fatPendenciaContaHosp) {
		getFatPendenciaContaHospDAO().remover(fatPendenciaContaHosp);
		getFatPendenciaContaHospDAO().flush();
	}

	private FatAihJnDAO getFatAihJnDAO() {
		return fatAihJnDAO;
	}

	/**
	 * Método para implementar regras da trigger executada antes da inserção de
	 * <code>FatAih</code>.
	 * 
	 * ORADB Trigger FATT_AIH_BRI
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void fattAihBri(final FatAih fatAih) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		fatAih.setCriadoEm(new Date());
		fatAih.setAlteradoEm(new Date());
		fatAih.setCriadoPor(servidorLogado.getUsuario());
		fatAih.setAlteradoPor(servidorLogado.getUsuario());
	}

	/**
	 * Método para implementar regras da trigger executada antes da atualização
	 * de <code>FatAih</code>.
	 * 
	 * ORADB Trigger FATT_AIH_BRU
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void fattAihBru(final FatAih fatAih) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		fatAih.setAlteradoEm(new Date());
		fatAih.setAlteradoPor(servidorLogado.getUsuario());
		if (CoreUtil.igual(DominioSituacaoAih.L, fatAih.getIndSituacao())) {
			fatAih.setLiberadoEm(new Date());
			fatAih.setLiberadoPor(servidorLogado.getUsuario());
		}
	}

	/**
	 * Método para implementar regras da trigger executada depois da atualização
	 * de <code>FatAih</code>.
	 * 
	 * ORADB Trigger FATT_AIH_ARU
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void fattAihAru(final FatAih fatAih, final FatAih oldFatAih) {
		if (!CoreUtil.igual(oldFatAih.getNroAih(), fatAih.getNroAih())
				|| !CoreUtil.igual(oldFatAih.getDthrEmissao(),
						fatAih.getDthrEmissao())
				|| !CoreUtil.igual(oldFatAih.getIndSituacao(),
						fatAih.getIndSituacao())
				|| !CoreUtil.igual(oldFatAih.getSerMatricula(),
						fatAih.getSerMatricula())
				|| !CoreUtil.igual(oldFatAih.getSerVinCodigo(),
						fatAih.getSerVinCodigo())
				|| !CoreUtil.igual(oldFatAih.getAlteradoPor(),
						fatAih.getAlteradoPor())
				|| !CoreUtil.igual(oldFatAih.getAlteradoEm(),
						fatAih.getAlteradoEm())
				|| !CoreUtil.igual(oldFatAih.getCriadoPor(),
						fatAih.getCriadoPor())
				|| !CoreUtil.igual(oldFatAih.getCriadoEm(),
						fatAih.getCriadoEm())
				|| CoreUtil.modificados(oldFatAih.getContaHospitalar(),
						fatAih.getContaHospitalar())
				|| !CoreUtil.igual(oldFatAih.getNroAih(), fatAih.getNroAih())
				|| !CoreUtil.igual(oldFatAih.getNroAih(), fatAih.getNroAih())
				|| !CoreUtil.igual(oldFatAih.getNroAih(), fatAih.getNroAih())
				|| !CoreUtil.igual(oldFatAih.getNroAih(), fatAih.getNroAih())
				|| !CoreUtil.igual(oldFatAih.getNroAih(), fatAih.getNroAih())
				|| !CoreUtil.igual(oldFatAih.getLiberadoPor(),
						fatAih.getLiberadoPor())
				|| !CoreUtil.igual(oldFatAih.getLiberadoEm(),
						fatAih.getLiberadoEm())) {

			final FatAihJn fatAihJn = BaseJournalFactory.getBaseJournal(
					DominioOperacoesJournal.UPD, FatAihJn.class, getServidorLogadoFacade().obterServidorLogado().getUsuario());
			fatAihJn.setNroAih(oldFatAih.getNroAih());
			fatAihJn.setDthrEmissao(oldFatAih.getDthrEmissao());
			fatAihJn.setIndSituacao(oldFatAih.getIndSituacao());
			fatAihJn.setSerMatricula(oldFatAih.getSerMatricula());
			fatAihJn.setSerVinCodigo(oldFatAih.getSerVinCodigo());
			fatAihJn.setAlteradoPor(oldFatAih.getAlteradoPor());
			fatAihJn.setAlteradoEm(oldFatAih.getAlteradoEm());
			fatAihJn.setCriadoPor(oldFatAih.getCriadoPor());
			fatAihJn.setCriadoEm(oldFatAih.getCriadoEm());
			fatAihJn.setCthSeq(oldFatAih.getContaHospitalar() != null ? oldFatAih
					.getContaHospitalar().getSeq() : null);
			fatAihJn.setLiberadoPor(oldFatAih.getLiberadoPor());
			fatAihJn.setLiberadoEm(oldFatAih.getLiberadoEm());

			getFatAihJnDAO().merge(fatAihJn);
			getFatAihJnDAO().flush();
		}
	}

	/**
	 * Atualiza um AIH executando suas respectivas triggers
	 * 
	 * @param fatAih
	 * @param oldFatAih
	 * @throws ApplicationBusinessException
	 */
	public void atualizarFatAih(final FatAih fatAih, final FatAih oldFatAih)
			throws ApplicationBusinessException {
		fattAihBru(fatAih);
		getFatAihDAO().merge(fatAih);
		getFatAihDAO().flush();
		fattAihAru(fatAih, oldFatAih);
	}

	public void atualizarSituacaoFatAih(final FatAih fatAih,
			DominioSituacaoAih indSituacao) throws BaseException {
		switch (fatAih.getIndSituacao()) {
		case B:
			// if valorAntigoIndSituacao = 'B' AND valorNovoIndSituacao NOT IN
			// ('U','V','B') then
			// Exibir mensagem: Aihs bloqueadas só podem ser trocadas para
			// "Útil" ou "Vencida".
			// end if;
			switch (indSituacao) {
			case A:
				break;
			case L:
				break;
			case R:
				throw new BaseException(
						FaturamentoRNExceptionCode.ERRO_ALTERAR_SITUACAO_AIH_BLOQUEADA);
			}
			break;
		case V:
			// if valorAntigoIndSituacao = 'V' AND valorNovoIndSituacao NOT IN
			// ('U','V') then
			// Exibir mensagem: Aihs vencidas só podem ser trocadas para "Útil".
			// end if;
			switch (indSituacao) {
			case A:
				break;
			case L:
				break;
			case R:
				break;
			case B:
				throw new BaseException(
						FaturamentoRNExceptionCode.ERRO_ALTERAR_SITUACAO_AIH_VENCIDA);
			}
			break;
		case U:
			// if valorAntigoIndSituacao = 'U' AND valorNovoIndSituacao NOT IN
			// ('U','V') then
			// Exibir mensagem: Aihs úteis só podem ser trocadas para "Vencida".
			// end if;
			switch (indSituacao) {
			case A:
				break;
			case L:
				break;
			case R:
				break;
			case B:
				throw new BaseException(
						FaturamentoRNExceptionCode.ERRO_ALTERAR_SITUACAO_AIH_UTIL);
			}
			break;
		}

		FatAih velhaFatAih = clonarFatAih(fatAih);
		fatAih.setIndSituacao(indSituacao);
		this.atualizarFatAih(fatAih, velhaFatAih);
	}

	/**
	 * Insere um AIH executando suas respectivas triggers
	 * 
	 * @param fatAih
	 * @throws ApplicationBusinessException
	 */
	public void inserirFatAih(final FatAih fatAih, boolean flush)
			throws ApplicationBusinessException {
		
		FatAih original = getFatAihDAO().obterOriginal(fatAih.getNroAih());
		if(original != null){
			fattAihBri(original);
			original.setContaHospitalar(fatAih.getContaHospitalar());
			original.setDthrEmissao(fatAih.getDthrEmissao());
			original.setIndSituacao(fatAih.getIndSituacao());
			original.setSerMatricula(fatAih.getSerMatricula());
			original.setSerVinCodigo(fatAih.getSerVinCodigo());
			original.setServidor(fatAih.getServidor());
			getFatAihDAO().merge(original);
		}else{
			fattAihBri(fatAih);
			getFatAihDAO().persistir(fatAih);
		}

		if (flush){
			getFatAihDAO().flush();
		}
	}

	/**
	 * Método para clonar um objeto FatAih.
	 * 
	 * @param fatAih
	 * @return Objeto FatAih clonado.
	 * @throws Exception
	 */
	public FatAih clonarFatAih(final FatAih fatAih) throws BaseException {
		FatAih fatAihClone;
		try {
			fatAihClone = (FatAih) BeanUtils.cloneBean(fatAih);
//			if (fatAih.getContaHospitalar() != null) {
//				final FatContasHospitalares cta = new FatContasHospitalares();
//				cta.setSeq(fatAih.getContaHospitalar().getSeq());
//				fatAihClone.setContaHospitalar(cta);
//			}
		} catch (Exception e) {
			logError(e);
			throw new BaseException(
					FaturamentoRNExceptionCode.ERRO_CLONAR_AIH, e.getMessage());
		}
		return fatAihClone;
	}

	/**
	 * ORADB Function FATC_BUSCA_VLR_NUM
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param tctSeq
	 * @return
	 */
	public Integer fatcBuscaVlrNum(final Short iphPhoSeq, final Integer iphSeq,
			final String caracteristica) {
		final List<FatTipoCaractItens> listaFatTipoCaractItens = this
				.getFatTipoCaractItensDAO()
				.listarTipoCaractItensPorCaracteristica(caracteristica);
		if (listaFatTipoCaractItens != null
				&& !listaFatTipoCaractItens.isEmpty()) {
			final Integer seqTct = listaFatTipoCaractItens.get(0).getSeq();
			final FatCaractItemProcHospId id = new FatCaractItemProcHospId(
					iphPhoSeq, iphSeq, seqTct);
			final FatCaractItemProcHosp fatCaractItemProcHosp = this
					.getFatCaractItemProcHospDAO().obterPorChavePrimaria(id);
			if (fatCaractItemProcHosp != null) {
				return fatCaractItemProcHosp.getValorNumerico();
			}
		}
		return Integer.valueOf(0);
	}

	/**
	 * Método para implementar regras da trigger executada antes da atualização
	 * de <code>FatDocumentoCobrancaAihs</code>.
	 * 
	 * ORADB Trigger FATT_DCI_BRU
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void fattDciBru(
			final FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs,
			final FatDocumentoCobrancaAihs oldFatDocumentoCobrancaAihs)
			throws ApplicationBusinessException {

		fatDocumentoCobrancaAihs.setAlteradoEm(new Date());
		fatDocumentoCobrancaAihs.setAlteradoPor(getServidorLogadoFacade().obterServidorLogado().getUsuario());

		if (CoreUtil.modificados(oldFatDocumentoCobrancaAihs.getIndSituacao(),
				fatDocumentoCobrancaAihs.getIndSituacao())
				|| CoreUtil.modificados(
						oldFatDocumentoCobrancaAihs.getMesApresentacao(),
						fatDocumentoCobrancaAihs.getMesApresentacao())
				|| CoreUtil.modificados(
						oldFatDocumentoCobrancaAihs.getAnoApresentacao(),
						fatDocumentoCobrancaAihs.getAnoApresentacao())) {

			if (CoreUtil.igual(DominioSituacaoDcih.R,
					fatDocumentoCobrancaAihs.getIndSituacao())) {
				if (CoreUtil.modificados(
						oldFatDocumentoCobrancaAihs.getMesApresentacao(),
						fatDocumentoCobrancaAihs.getMesApresentacao())
						|| CoreUtil.modificados(oldFatDocumentoCobrancaAihs
								.getAnoApresentacao(), fatDocumentoCobrancaAihs
								.getAnoApresentacao())) {
					FaturamentoExceptionCode.FAT_00874.throwException();
				}
			}
			if (CoreUtil.igual(DominioSituacaoDcih.A,
					fatDocumentoCobrancaAihs.getIndSituacao())) {
				if (fatDocumentoCobrancaAihs.getMesApresentacao() == null
						|| fatDocumentoCobrancaAihs.getAnoApresentacao() == null) {
					FaturamentoExceptionCode.FAT_00873.throwException();
				}
			}
			if (fatDocumentoCobrancaAihs.getIndSituacao() == null
					|| CoreUtil.igual(DominioSituacaoDcih.N,
							fatDocumentoCobrancaAihs.getIndSituacao())) {
				if (fatDocumentoCobrancaAihs.getMesApresentacao() != null
						|| fatDocumentoCobrancaAihs.getAnoApresentacao() != null) {
					FaturamentoExceptionCode.FAT_00875.throwException();
				}
			}
		}
	}

	/**
	 * Método para implementar regras da trigger executada antes da inserção de
	 * <code>FatDocumentoCobrancaAihs</code>.
	 * 
	 * ORADB Trigger FATT_DCI_BRI
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void fattDciBri(
			final FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (fatDocumentoCobrancaAihs.getFatCompetencia() == null) {
			List<FatCompetencia> competencias = getVerificacaoFaturamentoSusRN()
					.obterCompetenciasAbertasNaoFaturadasPorModulo(
							DominioModuloCompetencia.INT);
			if (competencias != null && !competencias.isEmpty()) {
				fatDocumentoCobrancaAihs.setFatCompetencia(competencias.get(0));
			}
		}
		Date agora = new Date();
		fatDocumentoCobrancaAihs.setCriadoEm(agora);
		fatDocumentoCobrancaAihs.setCriadoPor(servidorLogado.getUsuario());
		fatDocumentoCobrancaAihs.setAlteradoEm(agora);
		fatDocumentoCobrancaAihs.setAlteradoPor(servidorLogado.getUsuario());
	}

	/**
	 * Atualiza um FatDocumentoCobrancaAihs executando suas respectivas triggers
	 * 
	 * @param fatDocumentoCobrancaAihs
	 * @param oldFatDocumentoCobrancaAihs
	 * @throws ApplicationBusinessException
	 */
	public FatDocumentoCobrancaAihs atualizarFatDocumentoCobrancaAihs(
			final FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs,
			final FatDocumentoCobrancaAihs oldFatDocumentoCobrancaAihs)
			throws ApplicationBusinessException {
		fattDciBru(fatDocumentoCobrancaAihs, oldFatDocumentoCobrancaAihs);
		FatDocumentoCobrancaAihs retorno = getFatDocumentoCobrancaAihsDAO().merge(
				fatDocumentoCobrancaAihs);
		getFatDocumentoCobrancaAihsDAO().flush();
		return retorno;
	}

	/**
	 * Insere um FatDocumentoCobrancaAihs executando suas respectivas triggers
	 * 
	 * @param fatDocumentoCobrancaAihs
	 * @param flush
	 * @throws ApplicationBusinessException
	 */
	public FatDocumentoCobrancaAihs inserirFatDocumentoCobrancaAihs(
			FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs, boolean flush)
			throws ApplicationBusinessException {
		fattDciBri(fatDocumentoCobrancaAihs);
		getFatDocumentoCobrancaAihsDAO().persistir(
				fatDocumentoCobrancaAihs);
		if (flush){
			getFatDocumentoCobrancaAihsDAO().flush();
		}
		return fatDocumentoCobrancaAihs;
	}

	/**
	 * Método para clonar um objeto FatDocumentoCobrancaAihs.
	 * 
	 * @param fatDocumentoCobrancaAihs
	 * @return Objeto FatDocumentoCobrancaAihs clonado.
	 * @throws Exception
	 */
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public FatDocumentoCobrancaAihs clonarFatDocumentoCobrancaAihs(
			final FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs)
			throws Exception {
		final FatDocumentoCobrancaAihs fatDocumentoCobrancaAihsClone = (FatDocumentoCobrancaAihs) BeanUtils
				.cloneBean(fatDocumentoCobrancaAihs);

		/*if (fatDocumentoCobrancaAihs.getFatTipoClassifSecSaude() != null) {
			final FatTipoClassifSecSaude tcss = new FatTipoClassifSecSaude();
			tcss.setSeq(fatDocumentoCobrancaAihs.getFatTipoClassifSecSaude()
					.getSeq());
			fatDocumentoCobrancaAihsClone.setFatTipoClassifSecSaude(tcss);
		}
		if (fatDocumentoCobrancaAihs.getFatCompetencia() != null) {
			final FatCompetencia cpt = new FatCompetencia();
			cpt.setId(fatDocumentoCobrancaAihs.getFatCompetencia().getId());
			fatDocumentoCobrancaAihsClone.setFatCompetencia(cpt);
		}*/
		return fatDocumentoCobrancaAihsClone;
	}

	public VRapPessoaServidor obterRapPessoasFisicasCPF(final Long cpf) {
		return getFatAtoMedicoAihDAO().obterPessoasFisicasCPF(cpf);
	}

	public List<FatAtoMedicoAih> buscarAtosMedicosEspelho(final Integer cthSeq,
			final int firstResult, final int maxResults,
			final String orderProperty, final boolean asc) {
		final List<FatAtoMedicoAih> lista = getFatAtoMedicoAihDAO()
				.listarFatAtoMedicoEspelho(cthSeq, firstResult, maxResults,
						orderProperty, asc);
		// ORADB FATP_BUSCA_NOME_ATOS
		for (final FatAtoMedicoAih fatAtoMedicoAih : lista) {
			if (fatAtoMedicoAih.getCpfCns() != null) {
				final VRapPessoaServidor pessoaFisica = obterRapPessoasFisicasCPF(fatAtoMedicoAih
						.getCpfCns());
				if (pessoaFisica != null) {
					fatAtoMedicoAih.setNomePessoa(pessoaFisica.getId()
							.getNome());
					fatAtoMedicoAih.setVinculo(pessoaFisica.getId()
							.getSerVinCodigo());
					fatAtoMedicoAih.setMatricula(pessoaFisica.getId()
							.getSerMatricula());
				}
			}

			// Busca descrição do CBO
			if (StringUtils.isNotBlank(fatAtoMedicoAih.getCbo())) {
				try {
					final FatCbos cbos = getFaturamentoFacade()
							.obterFatCboPorCodigoVigente(
									fatAtoMedicoAih.getCbo());
					if (cbos != null) {
						fatAtoMedicoAih.setDescricaoCBO(cbos.getDescricao());
					}
				} catch (final ApplicationBusinessException e) {
					logWarn(fatAtoMedicoAih.getCbo(), e);
				}
			}
		}
		return lista;
	}

	/**
	 * Método para implementar regras da trigger executada antes da inserção de
	 * <code>FatAutorizadoCma</code>.
	 * 
	 * ORADB Trigger FATT_FCMA_BRI
	 * 
	 * @param fatAutorizadoCma
	 * @throws ApplicationBusinessException
	 */
	private void fattFcmaBri(final FatAutorizadoCma fatAutorizadoCma)
			throws ApplicationBusinessException {
		fatAutorizadoCma.setCriadoEm(new Date());
		fatAutorizadoCma.setCriadoPor(getServidorLogadoFacade().obterServidorLogado().getUsuario());
	}

	/**
	 * Insere um FatAutorizadoCma executando suas respectivas triggers
	 * 
	 * @param fatAutorizadoCma
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void inserir(final FatAutorizadoCma fatAutorizadoCma)
			throws ApplicationBusinessException {
		fattFcmaBri(fatAutorizadoCma);
		getFatAutorizadoCmaDAO().persistir(fatAutorizadoCma);
		getFatAutorizadoCmaDAO().flush();
	}

	public FatValorContaHospitalar inserirFatValorContaHospitalar(
			final FatValorContaHospitalar fatValorContaHospitalar, boolean flush) {
		 getFatValorContaHospitalarDAO().persistir(fatValorContaHospitalar);
		if (flush) {
			getFatValorContaHospitalarDAO().flush();
		}
		 return fatValorContaHospitalar;
	}

	public FatValorContaHospitalar atualizarFatValorContaHospitalar(
			final FatValorContaHospitalar fatValorContaHospitalar, boolean flush) {
		FatValorContaHospitalar retorno = getFatValorContaHospitalarDAO().merge(
				fatValorContaHospitalar);
		if (flush) {
			getFatValorContaHospitalarDAO().flush();
		}
		return retorno;
	}

	private CidContaHospitalarPersist getCidContaHospitalarPersist() {
		return cidContaHospitalarPersist;
	}

	/**
	 * Método para implementar regras da trigger executada antes da atualização
	 * de <code>FatPerdaItemConta</code>.
	 * 
	 * ORADB Trigger FATT_PIT_BRU
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void fattPerdaItemContaBru(final FatPerdaItemConta fatPerdaItemConta)
			throws ApplicationBusinessException {
		fatPerdaItemConta.setAlteradoEm(new Date());
		fatPerdaItemConta.setAlteradoPor(getServidorLogadoFacade().obterServidorLogado().getUsuario());
	}

	/**
	 * Atualiza um PerdaItemConta executando suas respectivas triggers
	 * 
	 * @param fatPerdaItemConta
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void atualizarFatPerdaItemConta(final FatPerdaItemConta fatPerdaItemConta)
			throws ApplicationBusinessException {
		fattPerdaItemContaBru(fatPerdaItemConta);
		getFatPerdaItemContaDAO().merge(fatPerdaItemConta);
		getFatPerdaItemContaDAO().flush();
	}	

	public FatDiariaUtiDigitada clonarFatDiariaUtiDigitada(
			FatDiariaUtiDigitada fatDiariaUtiDigitada) {
		if (fatDiariaUtiDigitada != null) {
			FatDiariaUtiDigitada newDiaria = new FatDiariaUtiDigitada();
			newDiaria.setCthSeq(fatDiariaUtiDigitada.getCthSeq());
			newDiaria.setContaHospitalar(fatDiariaUtiDigitada
					.getContaHospitalar());
			newDiaria.setDiasMesInicialNew(fatDiariaUtiDigitada
					.getDiasMesInicialNew());
			newDiaria.setDiasMesAnteriorNew(fatDiariaUtiDigitada
					.getDiasMesAnteriorNew());
			newDiaria.setDiasMesAltaNew(fatDiariaUtiDigitada
					.getDiasMesAltaNew());
			newDiaria.setDiasMesInicialOld(fatDiariaUtiDigitada
					.getDiasMesInicialOld());
			newDiaria.setDiasMesAnteriorOld(fatDiariaUtiDigitada
					.getDiasMesAnteriorOld());
			newDiaria.setDiasMesAltaOld(fatDiariaUtiDigitada
					.getDiasMesAltaOld());
			newDiaria.setCriadoPor(fatDiariaUtiDigitada.getCriadoPor());
			newDiaria.setCriadoEm(fatDiariaUtiDigitada.getCriadoEm());
			newDiaria.setAlteradoPor(fatDiariaUtiDigitada.getAlteradoPor());
			newDiaria.setAlteradoEm(fatDiariaUtiDigitada.getAlteradoEm());
			newDiaria.setTipoUtiNew(fatDiariaUtiDigitada.getTipoUtiNew());
			newDiaria.setTipoUtiOld(fatDiariaUtiDigitada.getTipoUtiOld());
			return newDiaria;
		}
		return null;
	}

	/**
	 * ORADB Trigger FATT_DUD_BRI
	 * 
	 * @param fatDiariaUtiDigitada
	 * @throws ApplicationBusinessException
	 */
	private void fattDudBri(final FatDiariaUtiDigitada fatDiariaUtiDigitada)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Date agora = new Date();
		fatDiariaUtiDigitada.setCriadoEm(agora);
		fatDiariaUtiDigitada.setCriadoPor(servidorLogado.getUsuario());
		fatDiariaUtiDigitada.setAlteradoEm(agora);
		fatDiariaUtiDigitada.setAlteradoPor(servidorLogado.getUsuario());
	}

	/**
	 * ORADB Trigger FATT_DUD_BRU
	 * 
	 * @param fatDiariaUtiDigitada
	 * @throws ApplicationBusinessException
	 */
	private void fattDudBru(final FatDiariaUtiDigitada fatDiariaUtiDigitada)
			throws ApplicationBusinessException {
		Date agora = new Date();
		fatDiariaUtiDigitada.setAlteradoEm(agora);
		fatDiariaUtiDigitada.setAlteradoPor(getServidorLogadoFacade().obterServidorLogado().getUsuario());
	}

	/**
	 * ORADB Trigger FATT_DUD_ARD
	 * 
	 * @param fatDiariaUtiDigitada
	 * @throws ApplicationBusinessException
	 */
	private void fattDudArd(final FatDiariaUtiDigitada fatDiariaUtiDigitada)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final FatDiariaUtiDigitadaJn fatDiariaUtiDigitadaJn = BaseJournalFactory
				.getBaseJournal(DominioOperacoesJournal.DEL,
						FatDiariaUtiDigitadaJn.class, servidorLogado.getUsuario());
		fatDiariaUtiDigitadaJn.setCthSeq(fatDiariaUtiDigitada.getCthSeq());
		fatDiariaUtiDigitadaJn.setDiasMesInicialNew(fatDiariaUtiDigitada
				.getDiasMesInicialNew());
		fatDiariaUtiDigitadaJn.setDiasMesAnteriorNew(fatDiariaUtiDigitada
				.getDiasMesAnteriorNew());
		fatDiariaUtiDigitadaJn.setDiasMesAltaNew(fatDiariaUtiDigitada
				.getDiasMesAltaNew());
		fatDiariaUtiDigitadaJn.setDiasMesInicialOld(fatDiariaUtiDigitada
				.getDiasMesInicialOld());
		fatDiariaUtiDigitadaJn.setDiasMesAnteriorOld(fatDiariaUtiDigitada
				.getDiasMesAnteriorOld());
		fatDiariaUtiDigitadaJn.setDiasMesAltaOld(fatDiariaUtiDigitada
				.getDiasMesAltaOld());
		fatDiariaUtiDigitadaJn
				.setCriadoPor(fatDiariaUtiDigitada.getCriadoPor());
		fatDiariaUtiDigitadaJn.setCriadoEm(fatDiariaUtiDigitada.getCriadoEm());
		fatDiariaUtiDigitadaJn.setAlteradoPor(fatDiariaUtiDigitada
				.getAlteradoPor());
		fatDiariaUtiDigitadaJn.setAlteradoEm(fatDiariaUtiDigitada
				.getAlteradoEm());
		fatDiariaUtiDigitadaJn.setTipoUtiNew(fatDiariaUtiDigitada
				.getTipoUtiNew());
		fatDiariaUtiDigitadaJn.setTipoUtiOld(fatDiariaUtiDigitada
				.getTipoUtiOld());
		getFatDiariaUtiDigitadaJnDAO().persistir(fatDiariaUtiDigitadaJn);
		getFatDiariaUtiDigitadaJnDAO().flush();
	}

	/**
	 * ORADB Trigger FATT_DUD_ARU
	 * 
	 * @param fatDiariaUtiDigitada
	 * @throws ApplicationBusinessException
	 */
	private void fattDudAru(final FatDiariaUtiDigitada fatDiariaUtiDigitada,
			FatDiariaUtiDigitada oldFatDiariaUtiDigitada)
			throws ApplicationBusinessException {

		if (!CoreUtil.igual(oldFatDiariaUtiDigitada.getCthSeq(),
				fatDiariaUtiDigitada.getCthSeq())
				|| !CoreUtil.igual(
						oldFatDiariaUtiDigitada.getDiasMesInicialNew(),
						fatDiariaUtiDigitada.getDiasMesInicialNew())
				|| !CoreUtil.igual(
						oldFatDiariaUtiDigitada.getDiasMesAnteriorNew(),
						fatDiariaUtiDigitada.getDiasMesAnteriorNew())
				|| !CoreUtil.igual(oldFatDiariaUtiDigitada.getDiasMesAltaNew(),
						fatDiariaUtiDigitada.getDiasMesAltaNew())
				|| !CoreUtil.igual(oldFatDiariaUtiDigitada.getTipoUtiNew(),
						fatDiariaUtiDigitada.getTipoUtiNew())
				|| !CoreUtil.igual(
						oldFatDiariaUtiDigitada.getDiasMesInicialOld(),
						fatDiariaUtiDigitada.getDiasMesInicialOld())
				|| !CoreUtil.igual(
						oldFatDiariaUtiDigitada.getDiasMesAnteriorOld(),
						fatDiariaUtiDigitada.getDiasMesAnteriorOld())
				|| !CoreUtil.igual(oldFatDiariaUtiDigitada.getTipoUtiOld(),
						fatDiariaUtiDigitada.getTipoUtiOld())
				|| !CoreUtil.igual(oldFatDiariaUtiDigitada.getDiasMesAltaOld(),
						fatDiariaUtiDigitada.getDiasMesAltaOld())
				|| !CoreUtil.igual(oldFatDiariaUtiDigitada.getCriadoEm(),
						fatDiariaUtiDigitada.getCriadoEm())
				|| !CoreUtil.igual(oldFatDiariaUtiDigitada.getCriadoPor(),
						fatDiariaUtiDigitada.getCriadoPor())
				|| !CoreUtil.igual(oldFatDiariaUtiDigitada.getAlteradoEm(),
						fatDiariaUtiDigitada.getAlteradoEm())
				|| !CoreUtil.igual(oldFatDiariaUtiDigitada.getAlteradoPor(),
						fatDiariaUtiDigitada.getAlteradoPor())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			final FatDiariaUtiDigitadaJn fatDiariaUtiDigitadaJn = BaseJournalFactory
					.getBaseJournal(DominioOperacoesJournal.UPD,
							FatDiariaUtiDigitadaJn.class, servidorLogado.getUsuario());
			fatDiariaUtiDigitadaJn.setCthSeq(fatDiariaUtiDigitada.getCthSeq());
			fatDiariaUtiDigitadaJn.setDiasMesInicialNew(fatDiariaUtiDigitada
					.getDiasMesInicialNew());
			fatDiariaUtiDigitadaJn.setDiasMesAnteriorNew(fatDiariaUtiDigitada
					.getDiasMesAnteriorNew());
			fatDiariaUtiDigitadaJn.setDiasMesAltaNew(fatDiariaUtiDigitada
					.getDiasMesAltaNew());
			fatDiariaUtiDigitadaJn.setDiasMesInicialOld(fatDiariaUtiDigitada
					.getDiasMesInicialOld());
			fatDiariaUtiDigitadaJn.setDiasMesAnteriorOld(fatDiariaUtiDigitada
					.getDiasMesAnteriorOld());
			fatDiariaUtiDigitadaJn.setDiasMesAltaOld(fatDiariaUtiDigitada
					.getDiasMesAltaOld());
			fatDiariaUtiDigitadaJn.setCriadoPor(fatDiariaUtiDigitada
					.getCriadoPor());
			fatDiariaUtiDigitadaJn.setCriadoEm(fatDiariaUtiDigitada
					.getCriadoEm());
			fatDiariaUtiDigitadaJn.setAlteradoPor(fatDiariaUtiDigitada
					.getAlteradoPor());
			fatDiariaUtiDigitadaJn.setAlteradoEm(fatDiariaUtiDigitada
					.getAlteradoEm());
			fatDiariaUtiDigitadaJn.setTipoUtiNew(fatDiariaUtiDigitada
					.getTipoUtiNew());
			fatDiariaUtiDigitadaJn.setTipoUtiOld(fatDiariaUtiDigitada
					.getTipoUtiOld());
			getFatDiariaUtiDigitadaJnDAO()
					.persistir(fatDiariaUtiDigitadaJn);
			getFatDiariaUtiDigitadaJnDAO().flush();
		}
	}

	/**
	 * Atualiza um FatDiariaUtiDigitada executando suas respectivas triggers
	 * 
	 * @param fatDiariaUtiDigitada
	 * @param oldFatDiariaUtiDigitada
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public FatDiariaUtiDigitada atualizarFatDiariaUtiDigitada(
			final FatDiariaUtiDigitada fatDiariaUtiDigitada,
			final FatDiariaUtiDigitada oldFatDiariaUtiDigitada)
			throws ApplicationBusinessException {
		this.fattDudBru(fatDiariaUtiDigitada);
		FatDiariaUtiDigitada retorno = getFatDiariaUtiDigitadaDAO()
				.merge(fatDiariaUtiDigitada);
		getFatDiariaUtiDigitadaDAO().flush();
		this.fattDudAru(fatDiariaUtiDigitada, oldFatDiariaUtiDigitada);
		return retorno;
	}

	/**
	 * Insere um FatDiariaUtiDigitada executando suas respectivas triggers
	 * 
	 * @param fatDiariaUtiDigitada
	 * @param flush
	 * @throws ApplicationBusinessException
	 */
	public FatDiariaUtiDigitada inserirFatDiariaUtiDigitada(
			FatDiariaUtiDigitada fatDiariaUtiDigitada, boolean flush)
			throws ApplicationBusinessException {
		this.fattDudBri(fatDiariaUtiDigitada);
		getFatDiariaUtiDigitadaDAO()
				.persistir(fatDiariaUtiDigitada);
		if (flush){
			getFatDiariaUtiDigitadaDAO().flush();
		}
		return fatDiariaUtiDigitada;
	}

	/**
	 * Remove um FatDiariaUtiDigitada
	 * 
	 * @param fatDiariaUtiDigitada
	 * @param flush
	 * @throws ApplicationBusinessException
	 */
	public void removerFatDiariaUtiDigitada(FatDiariaUtiDigitada fatDiariaUtiDigitada,
			boolean flush) throws ApplicationBusinessException {
		this.fattDudArd(fatDiariaUtiDigitada);
		getFatDiariaUtiDigitadaDAO().remover(fatDiariaUtiDigitada);
		if (flush){
			getFatDiariaUtiDigitadaDAO().flush();
		}
	}

	/**
	 * ORADB FATC_RN_CTHC_ATU_DESD_CTA
	 * 
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	public DesdobrarContaHospitalarVO desdobrarContaHospitalar(Integer cthSeq,
			FatMotivoDesdobramento motivoDesdobramento,
			Date dataHoraDesdobramento, Boolean contaConsideradaReapresentada, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		DesdobrarContaHospitalarVO vo = new DesdobrarContaHospitalarVO();

		ContaHospitalarON contaHospitalarON = getContaHospitalarON();
		ItemContaHospitalarON itemContaHospitalarON = getItemContaHospitalarON();
		ContaHospitalarRN contaHospitalarRN = getContaHospitalarRN();
		FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		FatContasInternacaoDAO fatContasInternacaoDAO = getFatContasInternacaoDAO();
		FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		FatTipoAihDAO fatTipoAihDAO = getFatTipoAihDAO();
		FatContaSugestaoDesdobrDAO fatContaSugestaoDesdobrDAO = getFatContaSugestaoDesdobrDAO();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		Byte vTahSeq = null;
		FatContasHospitalares regCth = null;
		FatItemContaHospitalar regIch = null;
		Short vItem = 1;
		Date vDthrReal = null;
		Integer vIntSeq = null;
		Short vIphPhoSeq = null;
		Integer vIphSeq = null;
		Boolean vIndAih5 = null;
		FatContasHospitalares regCthReap = null;
		Integer vCthSeq = null;
		Boolean vCthReap = null;
		

		vIntSeq = fatContasInternacaoDAO.obterMaxSeqInternacao(cthSeq);

		if (vIntSeq == null) {
			vo.setMensagem("Não foi encontrada internação para a conta!");
			vo.setRetorno(false);
			return vo;
		}

		regCth = fatContasHospitalaresDAO.obterPorChavePrimaria(cthSeq);
		if (regCth == null
				|| regCth.getProcedimentoHospitalarInternoRealizado() == null
				|| (regCth.getContasHospitalares() != null && !regCth
						.getContasHospitalares().isEmpty())) {
			vo.setMensagem("Conta não possui SSM Realizado ou já foi desdobrada!");
			vo.setRetorno(false);
			return vo;
		}

		if (regCth.getProcedimentoHospitalarInterno() == null) {
			vo.setMensagem("Conta não possui SSM Solicitado!");
			vo.setRetorno(false);
			return vo;
		}

		if (regCth.getContaHospitalarReapresentada() != null) {
			vCthReap = contaConsideradaReapresentada;
		}

		DominioSituacaoItenConta[] situacoesIgnoradas = new DominioSituacaoItenConta[] {
				DominioSituacaoItenConta.C, DominioSituacaoItenConta.D,
				DominioSituacaoItenConta.N };

		vDthrReal = fatItemContaHospitalarDAO.buscaMinDataHoraRealizado(cthSeq,
				regCth.getProcedimentoHospitalarInternoRealizado().getSeq(),
				situacoesIgnoradas);

		if (vDthrReal == null
				|| CoreUtil.isMaiorDatas(vDthrReal, dataHoraDesdobramento)) {
			vo.setMensagem("Data e Hora do SSM Realizado não pode ser maior que a data e hora do Desdobramento!");
			vo.setRetorno(false);
			return vo;
		}

		if (CoreUtil.isMaiorDatas(dataHoraDesdobramento,
				nvl(regCth.getDtAltaAdministrativa(), new Date()))
				|| CoreUtil.isMenorDatas(dataHoraDesdobramento,
						regCth.getDataInternacaoAdministrativa())) {
			vo.setMensagem("Data e hora do Desdobramento não pode ser menor que a data e hora da Internação Administrativa ou maior que a data e hora da Alta Administrativa!");
			vo.setRetorno(false);
			return vo;
		}

		RnCthcVerItemSusVO rnCthcVerItemSusVO = contaHospitalarRN
				.rnCthcVerItemSus(
						DominioOrigemProcedimento.I,
						regCth.getConvenioSaudePlano() != null ? regCth
								.getConvenioSaudePlano().getId().getCnvCodigo()
								: null,
						regCth.getConvenioSaudePlano() != null ? regCth
								.getConvenioSaudePlano().getId().getSeq()
								: null,
						(short) 1,
						regCth.getProcedimentoHospitalarInternoRealizado() != null ? regCth
								.getProcedimentoHospitalarInternoRealizado()
								.getSeq()
								: (regCth.getProcedimentoHospitalarInterno() != null ? regCth
										.getProcedimentoHospitalarInterno()
										.getSeq() : null));

		Boolean aux = false;
		if (rnCthcVerItemSusVO != null) {
			aux = rnCthcVerItemSusVO.getRetorno();
			vIphPhoSeq = rnCthcVerItemSusVO.getPhoSeq();
			vIphSeq = rnCthcVerItemSusVO.getIphSeq();
		}

		if (aux) {
			FatItensProcedHospitalar itemProcedimentoHospitalar = fatItensProcedHospitalarDAO
					.obterPorChavePrimaria(new FatItensProcedHospitalarId(
							vIphPhoSeq, vIphSeq));

			if (itemProcedimentoHospitalar != null) {
				vIndAih5 = itemProcedimentoHospitalar.getTipoAih5();
			}
		} else {
			vo.setMensagem("Não foi encontrado procedimento SUS para o SSM Realizado!");
			vo.setRetorno(false);
			return vo;
		}

		if (Boolean.TRUE.equals(vIndAih5)) {
			Short seqMotivoSaidaPaciente = buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_PERMANENCIA);
			if (regCth.getMotivoSaidaPaciente() != null
					&& CoreUtil.igual(regCth.getMotivoSaidaPaciente().getSeq(),
							seqMotivoSaidaPaciente)) {
				Short codigoSus5 = buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_AIH_LONGA_PERMANENCIA);
				FatTipoAih fatTipoAih = fatTipoAihDAO
						.obterItemAihPorCodigoSus(codigoSus5);
				if (fatTipoAih != null) {
					vTahSeq = fatTipoAih.getSeq();
				}

				Calendar cal = Calendar.getInstance();
				cal.setTime(dataHoraDesdobramento);

				if (!CoreUtil.igual(cal.get(Calendar.DAY_OF_MONTH),
						cal.getActualMaximum(Calendar.DAY_OF_MONTH))) {
					vo.setMensagem("Data/Hora do Desdobramento, em AIH5, deve ser o último dia do mês.");
					vo.setRetorno(false);
					return vo;
				}
			} else {
				vIndAih5 = false;
				vTahSeq = 1;
			}
		} else {
			vTahSeq = regCth.getTipoAih() != null ? regCth.getTipoAih()
					.getSeq() : null;
		}

		if (Boolean.FALSE.equals(vCthReap)) {
			try {
				FatContasHospitalares contaHospitalar = new FatContasHospitalares();

				contaHospitalar
						.setDataInternacaoAdministrativa(dataHoraDesdobramento);
				contaHospitalar.setContaManuseada(regCth.getContaManuseada());
				contaHospitalar.setAcomodacao(regCth.getAcomodacao());
				contaHospitalar.setConvenioSaudePlano(regCth
						.getConvenioSaudePlano());
				contaHospitalar.setProcedimentoHospitalarInterno(Boolean.TRUE
						.equals(vIndAih5) ? regCth
						.getProcedimentoHospitalarInterno() : null);
				contaHospitalar
						.setProcedimentoHospitalarInternoRealizado(Boolean.TRUE
								.equals(vIndAih5) ? regCth
								.getProcedimentoHospitalarInternoRealizado()
								: null);
				contaHospitalar.setContaHospitalar(regCth);
				contaHospitalar.setAih(Boolean.TRUE.equals(vIndAih5) ? regCth
						.getAih() : null);
				contaHospitalar.setIndContaReapresentada(false);
				contaHospitalar.setNumDiariasAutorizadas(regCth
						.getNumDiariasAutorizadas());
				contaHospitalar.setIndInfeccao(regCth.getIndInfeccao());
				contaHospitalar
						.setIndSituacao(regCth.getDtAltaAdministrativa() == null ? DominioSituacaoConta.A
								: DominioSituacaoConta.F);
				contaHospitalar.setTipoAih(vTahSeq != null ? fatTipoAihDAO
						.obterPorChavePrimaria(vTahSeq) : null);
				contaHospitalar.setEspecialidade(regCth.getEspecialidade());
				contaHospitalar
						.setNroSeqaih5(Boolean.TRUE.equals(vIndAih5) ? ((short) ((regCth
								.getNroSeqaih5() != null ? regCth
								.getNroSeqaih5() : 0) + 1)) : null);

				contaHospitalar = contaHospitalarON
						.inserirContaHospitalar(contaHospitalar, false, dataFimVinculoServidor);
				vCthSeq = contaHospitalar.getSeq();
			} catch (Exception e) {
				logError(MSG_EXCECAO_DESDOBRAMENTO, e);
				throw new ApplicationBusinessException(
						FaturamentoRNExceptionCode.ERRO_INSERIR_CONTA_HOSPITALAR_NAO_REAP,
						e.getMessage());
			}
		} else if (Boolean.TRUE.equals(vCthReap)) {
			try {
				FatContasHospitalares contaHospitalar = new FatContasHospitalares();

				contaHospitalar
						.setDataInternacaoAdministrativa(dataHoraDesdobramento);
				contaHospitalar.setContaManuseada(regCth.getContaManuseada());
				contaHospitalar.setAcomodacao(regCth.getAcomodacao());
				contaHospitalar.setConvenioSaudePlano(regCth
						.getConvenioSaudePlano());
				contaHospitalar.setProcedimentoHospitalarInterno(Boolean.TRUE
						.equals(vIndAih5) ? regCth
						.getProcedimentoHospitalarInterno() : null);
				contaHospitalar
						.setProcedimentoHospitalarInternoRealizado(Boolean.TRUE
								.equals(vIndAih5) ? regCth
								.getProcedimentoHospitalarInternoRealizado()
								: null);
				contaHospitalar.setContaHospitalar(regCth);
				contaHospitalar.setAih(Boolean.TRUE.equals(vIndAih5) ? regCth
						.getAih() : null);
				contaHospitalar.setIndContaReapresentada(true);
				contaHospitalar
						.setContaHospitalarReapresentada(regCthReap != null ? regCthReap
								.getContaHospitalarReapresentada() : null);
				contaHospitalar.setNumDiariasAutorizadas(regCth
						.getNumDiariasAutorizadas());
				contaHospitalar.setIndInfeccao(regCth.getIndInfeccao());
				contaHospitalar
						.setIndSituacao(regCth.getDtAltaAdministrativa() == null ? DominioSituacaoConta.A
								: DominioSituacaoConta.F);
				contaHospitalar.setTipoAih(vTahSeq != null ? fatTipoAihDAO
						.obterPorChavePrimaria(vTahSeq) : null);
				contaHospitalar.setEspecialidade(regCth.getEspecialidade());
				contaHospitalar
						.setNroSeqaih5(Boolean.TRUE.equals(vIndAih5) ? ((short) ((regCth
								.getNroSeqaih5() != null ? regCth
								.getNroSeqaih5() : 0) + 1)) : null);

				contaHospitalar = contaHospitalarON
				.inserirContaHospitalar(contaHospitalar, false, dataFimVinculoServidor);
				vCthSeq = contaHospitalar.getSeq();
			} catch (Exception e) {
				logError(
						MSG_EXCECAO_DESDOBRAMENTO, e);
				throw new ApplicationBusinessException(
						FaturamentoRNExceptionCode.ERRO_INSERIR_CONTA_HOSPITALAR_REAP,
						e.getMessage());
			}
		} else if (vCthReap == null) {
			try {
				FatContasHospitalares contaHospitalar = new FatContasHospitalares();

				contaHospitalar
						.setDataInternacaoAdministrativa(dataHoraDesdobramento);
				contaHospitalar.setContaManuseada(regCth.getContaManuseada());
				contaHospitalar.setAcomodacao(regCth.getAcomodacao());
				contaHospitalar.setConvenioSaudePlano(regCth
						.getConvenioSaudePlano());
				contaHospitalar.setProcedimentoHospitalarInterno(Boolean.TRUE
						.equals(vIndAih5) ? regCth
						.getProcedimentoHospitalarInterno() : null);
				contaHospitalar
						.setProcedimentoHospitalarInternoRealizado(Boolean.TRUE
								.equals(vIndAih5) ? regCth
								.getProcedimentoHospitalarInternoRealizado()
								: null);
				contaHospitalar.setContaHospitalar(regCth);
				contaHospitalar.setAih(Boolean.TRUE.equals(vIndAih5) ? regCth
						.getAih() : null);
				contaHospitalar.setIndContaReapresentada(regCth
						.getIndContaReapresentada());
				contaHospitalar.setContaHospitalarReapresentada(regCth
						.getContaHospitalarReapresentada());
				contaHospitalar.setNumDiariasAutorizadas(regCth
						.getNumDiariasAutorizadas());
				contaHospitalar.setIndInfeccao(regCth.getIndInfeccao());
				contaHospitalar
						.setIndSituacao(regCth.getDtAltaAdministrativa() == null ? DominioSituacaoConta.A
								: DominioSituacaoConta.F);
				contaHospitalar.setTipoAih(vTahSeq != null ? fatTipoAihDAO
						.obterPorChavePrimaria(vTahSeq) : null);
				contaHospitalar.setEspecialidade(regCth.getEspecialidade());
				contaHospitalar
						.setNroSeqaih5(Boolean.TRUE.equals(vIndAih5) ? ((short) ((regCth
								.getNroSeqaih5() != null ? regCth
								.getNroSeqaih5() : 0) + 1)) : null);

				contaHospitalar = contaHospitalarON
				.inserirContaHospitalar(contaHospitalar, false, dataFimVinculoServidor);
				vCthSeq = contaHospitalar.getSeq();
			} catch (Exception e) {
				logError(
						MSG_EXCECAO_DESDOBRAMENTO, e);
				throw new ApplicationBusinessException(
						FaturamentoRNExceptionCode.ERRO_INSERIR_CONTA_HOSPITALAR,
						String.valueOf(vCthSeq), e.getMessage());
			}
		}

		FatContasHospitalares contaHospitalarCriada = fatContasHospitalaresDAO
				.obterPorChavePrimaria(vCthSeq);
		try {
			FatContasInternacao fatContasInternacao = new FatContasInternacao();

			fatContasInternacao.setContaHospitalar(contaHospitalarCriada);
			fatContasInternacao.setInternacao(getInternacaoFacade()
					.obterAinInternacaoPorChavePrimaria(vIntSeq));

			inserirContaInternacao(fatContasInternacao, nomeMicrocomputador, dataFimVinculoServidor);
		} catch (Exception e) {
			logError(MSG_EXCECAO_DESDOBRAMENTO,
					e);
			throw new ApplicationBusinessException(
					FaturamentoRNExceptionCode.ERRO_INSERIR_ASSOCIACAO_CONTA_HOSPITALAR_INTERNACAO,
					e.getMessage());
		}

		situacoesIgnoradas = new DominioSituacaoItenConta[] {
				DominioSituacaoItenConta.D, DominioSituacaoItenConta.N };
		List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO
				.listarItensContaHospitalarParaDesdobrar(cthSeq,
						dataHoraDesdobramento, situacoesIgnoradas);

		if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
			for (int i = 0; i < itensContaHospitalar.size(); i++) {
				regIch = itensContaHospitalar.get(i);

				try {
					FatItemContaHospitalar itemContaHospitalar = new FatItemContaHospitalar();

					itemContaHospitalar.setId(new FatItemContaHospitalarId(
							vCthSeq, vItem));
					itemContaHospitalar
							.setContaHospitalar(contaHospitalarCriada);
					itemContaHospitalar.setIchType(regIch.getIchType());
					itemContaHospitalar.setProcedimentoHospitalarInterno(regIch
							.getProcedimentoHospitalarInterno());
					itemContaHospitalar.setAgenciaBanco(regIch
							.getAgenciaBanco());
					itemContaHospitalar.setNumRecibo(regIch.getNumRecibo());
					itemContaHospitalar.setNumConta(regIch.getNumConta());
					itemContaHospitalar.setNumCheque(regIch.getNumCheque());
					itemContaHospitalar.setSerieCheque(regIch.getSerieCheque());
					itemContaHospitalar.setNome(regIch.getNome());
					itemContaHospitalar.setCpf(regIch.getCpf());
					itemContaHospitalar
							.setIndSituacao(DominioSituacaoItenConta.C
									.equals(regIch.getIndSituacao()) ? DominioSituacaoItenConta.C
									: DominioSituacaoItenConta.A);
					itemContaHospitalar.setIseSoeSeq(regIch.getIseSoeSeq());
					itemContaHospitalar.setIseSeqp(regIch.getIseSeqp());
					itemContaHospitalar.setQuantidade(regIch.getQuantidade());
					itemContaHospitalar.setIchValor(regIch.getIchValor());
					itemContaHospitalar.setQuantidadeRealizada(regIch
							.getQuantidadeRealizada());
					itemContaHospitalar.setIndOrigem(regIch.getIndOrigem());
					itemContaHospitalar.setLocalCobranca(regIch
							.getLocalCobranca());
					itemContaHospitalar.setDthrRealizado(regIch
							.getDthrRealizado());
					itemContaHospitalar.setUnidadesFuncional(regIch
							.getUnidadesFuncional());
					itemContaHospitalar.setIndModoCobranca(regIch
							.getIndModoCobranca());
					itemContaHospitalar.setProcEspPorCirurgias(regIch
							.getProcEspPorCirurgias());
					itemContaHospitalar.setItemRmps(regIch.getItemRmps());
					itemContaHospitalar.setCmoMcoSeq(regIch.getCmoMcoSeq());
					itemContaHospitalar.setCmoEcoBolNumero(regIch
							.getCmoEcoBolNumero());
					itemContaHospitalar.setCmoEcoBolBsaCodigo(regIch
							.getCmoEcoBolBsaCodigo());
					itemContaHospitalar.setCmoEcoBolData(regIch
							.getCmoEcoBolData());
					itemContaHospitalar.setCmoEcoCsaCodigo(regIch
							.getCmoEcoCsaCodigo());
					itemContaHospitalar.setCmoSequencia(regIch
							.getCmoSequencia());
					itemContaHospitalar.setCmoEcoSeqp(regIch.getCmoEcoSeqp());
					itemContaHospitalar.setPrescricaoProcedimento(regIch
							.getPrescricaoProcedimento());
					itemContaHospitalar.setPrescricaoNpt(regIch
							.getPrescricaoNpt());
					itemContaHospitalar.setCriadoPor(regIch.getCriadoPor());
					itemContaHospitalar.setPrescricaoPaciente(regIch
							.getPrescricaoPaciente());
					itemContaHospitalar.setPaoSeq(regIch.getPaoSeq());
					itemContaHospitalar.setServidor(regIch.getServidor());

					itemContaHospitalarON
							.inserirItemContaHospitalarSemValidacoesForms(itemContaHospitalar, true, servidorLogado, dataFimVinculoServidor, null);
				} catch (Exception e) {
					logError(MSG_EXCECAO_DESDOBRAMENTO,
									e);
					throw new ApplicationBusinessException(
							FaturamentoRNExceptionCode.ERRO_INSERIR_ITEM_CONTA_HOSPITALAR,
							e.getMessage());
				}

				vItem++;

				FatItemContaHospitalar oldFatItensContaHospitalar = null;
				try {
					oldFatItensContaHospitalar = getItemContaHospitalarON()
							.clonarItemContaHospitalar(regIch);
				} catch (Exception e) {
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}

				regIch.setIndSituacao(DominioSituacaoItenConta.D);

				itemContaHospitalarON
						.atualizarItemContaHospitalarSemValidacoesForms(regIch,
								servidorLogado, oldFatItensContaHospitalar, dataFimVinculoServidor);
			}
		}

		FatContasHospitalares oldContaHospitalar = null;
		try {
			oldContaHospitalar = getFaturamentoFacade().clonarContaHospitalar(
					contaHospitalarCriada);
		} catch (Exception e) {
			logError(MSG_EXCECAO_DESDOBRAMENTO,
					e);
			throw new ApplicationBusinessException(
					FaturamentoRNExceptionCode.ERRO_CLONAR_CONTA_HOSPITALAR);
		}

		contaHospitalarCriada.setDtAltaAdministrativa(regCth
				.getDtAltaAdministrativa());
		contaHospitalarON.atualizarContaHospitalar(contaHospitalarCriada,
				oldContaHospitalar, true, nomeMicrocomputador, dataFimVinculoServidor);

		if (Boolean.FALSE.equals(this.fatcAtualLaudDesd(vIntSeq,
				dataHoraDesdobramento))) {
			vo.setMensagem("Não foi possível desdobrar os laudos médicos!");
			vo.setRetorno(false);
			return vo;
		}

		FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO
				.obterPorChavePrimaria(cthSeq);

		oldContaHospitalar = null;
		try {
			oldContaHospitalar = getFaturamentoFacade().clonarContaHospitalar(
					contaHospitalar);
		} catch (Exception e) {
			logError(MSG_EXCECAO_DESDOBRAMENTO,
					e);
			throw new ApplicationBusinessException(
					FaturamentoRNExceptionCode.ERRO_CLONAR_CONTA_HOSPITALAR);
		}

		contaHospitalar.setMotivoDesdobramento(motivoDesdobramento);
		contaHospitalar.setIndSituacao(DominioSituacaoConta.F);
		contaHospitalar.setDtAltaAdministrativa(dataHoraDesdobramento);

		// #7040 RN2
		String parametroAutorizacaoManual = this
				.buscarVlrTextoAghParametro(AghuParametrosEnum.P_INTEGRACAO_SISTEMA_SMS_PARA_AUTORIZACAO_ITENS_DA_CONTA);

		if (DominioSimNao.N.toString().equalsIgnoreCase(
				parametroAutorizacaoManual)) {
			contaHospitalar.setIndEnviadoSms(DominioSimNao.S.toString());
			contaHospitalar.setIndAutorizadoSms(DominioSimNao.S.toString());
		}

		contaHospitalarON.atualizarContaHospitalar(contaHospitalar,
				oldContaHospitalar, true, nomeMicrocomputador, dataFimVinculoServidor);

		contaHospitalarRN.rnCthpAtuDiaUti2(cthSeq, nomeMicrocomputador, dataFimVinculoServidor);

		fatContaSugestaoDesdobrDAO.removerPorCth(cthSeq);

		vo.setRetorno(true);
		return vo;
	}

	private ContaHospitalarRN getContaHospitalarRN() {
		return contaHospitalarRN;
	}

	public List<Long> gerarFatAih(Long nroAihInicial, final Long nroAihFinal) {
		final List<Long> retorno = new ArrayList<Long>();
		while (nroAihInicial <= nroAihFinal) {
			Long nroAih = Long.parseLong(nroAihInicial.toString()
					+ gerarDigitoVerificadosAih(nroAihInicial));
			FatAih fatAih = null;
			try {
				fatAih = getFatAihDAO().obterPorChavePrimaria(nroAih);
			} catch (Exception e) {
				logar("Erro ao executar getFatAihDAO().obterPorChavePrimaria(nroAih)");
			}
			if (fatAih == null) {
				retorno.add(nroAih);
			}
			nroAihInicial++;
		}
		return retorno;
	}

	/*
	 * ORADB: RN_AIHC_VER_DIG_AIH
	 */
	public int gerarDigitoVerificadosAih(final Long nrAih) {
		final int digito = (int) (nrAih % 11);
		if (digito == 10) {
			return 0;
		}
		return digito;
	}

	public Integer gravarFatAihLote(List<Long> novasAihs) {
		int count = 0;
		for (Long nroAih : novasAihs) {
			FatAih novaAih = new FatAih();
			novaAih.setNroAih(nroAih);
			novaAih.setIndSituacao(DominioSituacaoAih.U);
			try {
				inserirFatAih(novaAih, false);
				count++;
			} catch (Exception e) {
				logError(e.getMessage());
				logError(e);
			}
		}
		getFatAihDAO().flush();
		return count;
	}

//	private static UserTransaction obterTransacao() {
//
//		UserTransaction result = null;
//
//		result = (UserTransaction) org.jboss.seam.Component
//				.getInstance(MAGIC_STRING_ORG_JBOSS_SEAM_TRANSACTION_TRANSACTION);
//
//		return result;
//	}
//
//	public void atualizarTxTimeout() throws IOException {
//
//		UserTransaction userTx = null;
//
//		userTx = obterTransacao();
//		if (userTx == null) {
//			this.logInfo("Sem transacao");
//		} else {
//			try {
//				userTx.setTransactionTimeout(MAGIC_INT_TIMEOUT_TRANSACTION_EQ_1DIA); // um
//																						// dia
//			} catch (Exception e) {
//				this.logInfo("Erro ajustando timeout transacao");
//			}
//		}
//	}
//
//	private static final int MAGIC_INT_TIMEOUT_TRANSACTION_EQ_1DIA = 60 * 60 * 24;
//	private static final String MAGIC_STRING_ORG_JBOSS_SEAM_TRANSACTION_TRANSACTION = "org.jboss.seam.transaction.transaction";

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	public IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this
				.internacaoFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	public void removerFatContaSugestaoDesdobr(
			FatContaSugestaoDesdobr contaSugestaoDesdobr, boolean flush) {
		
		getFatContaSugestaoDesdobrDAO().remover(contaSugestaoDesdobr);
		if (flush){
			this.getFatContaSugestaoDesdobrDAO().flush();
		}
	}
	
	public void removerFatContaSugestaoDesdobrPorId(
			FatContaSugestaoDesdobrId id, boolean flush) {
		getFatContaSugestaoDesdobrDAO().removerFatContaSugestaoDesdobrPorId(id);
		if (flush){
			this.getFatContaSugestaoDesdobrDAO().flush();
		}
	}

	public void inserirFatContaSugestaoDesdobr(
			FatContaSugestaoDesdobr contaSugestaoDesdobr, boolean flush)
			throws BaseException {
		this.preInserirFatContaSugestaoDesdobr(contaSugestaoDesdobr);
		this.getFatContaSugestaoDesdobrDAO().persistir(contaSugestaoDesdobr);
		if (flush){
			this.getFatContaSugestaoDesdobrDAO().flush();
		}
	}

	public void atualizarFatContaSugestaoDesdobr(
			FatContaSugestaoDesdobr contaSugestaoDesdobr, boolean flush)
			throws BaseException {
		this.preAtualizarFatContaSugestaoDesdobr(contaSugestaoDesdobr);
		this.getFatContaSugestaoDesdobrDAO().merge(contaSugestaoDesdobr);
		if (flush){
			this.getFatContaSugestaoDesdobrDAO().flush();
		}
	}

	/**
	 * ORADB FATT_CSD_BRI
	 * 
	 * @param contaSugestaoDesdobr
	 * @throws BaseException
	 */
	public void preInserirFatContaSugestaoDesdobr(
			FatContaSugestaoDesdobr contaSugestaoDesdobr)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		String nomeUsuarioLogado = servidorLogado.getUsuario();

		contaSugestaoDesdobr.setCriadoEm(new Date());
		contaSugestaoDesdobr.setCriadoPor(nomeUsuarioLogado);
		contaSugestaoDesdobr.setAlteradoEm(new Date());
		contaSugestaoDesdobr.setAlteradoPor(nomeUsuarioLogado);
	}

	/**
	 * ORADB FATT_CSD_BRU
	 * 
	 * @param contaSugestaoDesdobr
	 * @throws BaseException
	 */
	public void preAtualizarFatContaSugestaoDesdobr(
			FatContaSugestaoDesdobr contaSugestaoDesdobr)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		contaSugestaoDesdobr.setAlteradoEm(new Date());
		contaSugestaoDesdobr.setAlteradoPor(servidorLogado.getUsuario());
	}

	public boolean validaNumeroAIHInformadoManualmente(Long numeroAIH)
			throws ApplicationBusinessException {

		final AghParametros param = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_VALIDA_NRO_AIH);

		if (DominioSimNao.S.toString().equalsIgnoreCase(param.getVlrTexto())) {
			String aih = numeroAIH.toString();

			// deve ser informado pelo menos dois digitos.
			if (aih.length() <= 1) {
				return false;
			}

			// pega o ultimo digito do numero da aih
			long vUltimoDigito = Long
					.parseLong(aih.substring(aih.length() - 1));

			// retira o ultimo digito do numero da aih
			String vPedacoNroAih = aih.substring(0, aih.length() - 1);

			// calcula o resto
			long vResto = Long.parseLong(vPedacoNroAih) % 11;

			// se o resto for 10, o digito verificador e 0
			if (vResto == 10) {
				vResto = 0;
			}

			return (vResto == vUltimoDigito);

		} else {
			return true;
		}
	}

	/**
	 * ORADB Function FATC_BUSCA_CNS_RESP
	 * 
	 * @param pSerMatricula
	 * @param pSerVinCodigo
	 * @param pSoeSeq
	 * @param pIseSeqp
	 * @param pConNumero
	 * @param pCrgSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public CnsResponsavelVO fatcBuscaCnsResp(Integer pSerMatricula, Short pSerVinCodigo,
			Integer pSoeSeq, Short pIseSeqp, Integer pConNumero, Integer pCrgSeq)
			throws ApplicationBusinessException {
		Integer vPesCodigo = null;
		Short vTipoCns = null;
		Long vItemCns = null;
		Short vVinCodigo = null;
		Integer vMatricula = null;
			
		AghParametros param = buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CNS);
		vTipoCns = param.getVlrNumerico().shortValue();

		if (pSerMatricula != null && pSerVinCodigo != null) {
			RapServidores servidor = getRegistroColaboradorFacade().buscaServidor( new RapServidoresId(pSerMatricula, pSerVinCodigo));
			vPesCodigo = servidor.getPessoaFisica().getCodigo();

		} else if (pSoeSeq != null && pIseSeqp != null) {
			String valorParametroSitLiberado = buscarVlrTextoAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO); // LI
			RapServidores servidor = getRegistroColaboradorFacade().buscarRapServidorDeAelExtratoItemSolicitacao(pSoeSeq, pIseSeqp, valorParametroSitLiberado);

			if (servidor != null) {
				vPesCodigo = servidor.getPessoaFisica().getCodigo();
				vVinCodigo = servidor.getId().getVinCodigo();
				vMatricula = servidor.getId().getMatricula();
			}

			param = buscarAghParametro(AghuParametrosEnum.P_PES_CODIGO_USUARIO);
			String[] vetorTipoCns = param.getVlrTexto().split("\\,"); // 19593,
																		// 67341
			if (vPesCodigo.equals(Integer.valueOf(vetorTipoCns[0]))
					|| vPesCodigo.equals(Integer.valueOf(vetorTipoCns[1]))) { // Mariana
																				// Jobim
																				// Wilson
																				// e
																				// Usuário
																				// Imuno

				param = buscarAghParametro(AghuParametrosEnum.P_PES_CODIGO_RESPONSAVEL); // 4834
				vPesCodigo = param.getVlrNumerico().intValue(); // -- Monica
																// Kruger
				
				param = buscarAghParametro(AghuParametrosEnum.P_PES_VINCULO_RESPONSAVEL); // 1
				vVinCodigo = param.getVlrNumerico().shortValue();
				
				param = buscarAghParametro(AghuParametrosEnum.P_PES_MATRICULA_RESPONSAVEL); //16953
				vMatricula = param.getVlrNumerico().intValue();
			}
		} else if (pConNumero != null) {
			List<AacGradeAgendamenConsultas> listaGradeAgendConsultas = getAmbulatorioFacade()
					.executaCursorGetCboExame(pConNumero);
			if (!listaGradeAgendConsultas.isEmpty()) {
				AacGradeAgendamenConsultas grade = listaGradeAgendConsultas.get(0);
				AacCaracteristicaGrade aacCaract = getAmbulatorioFacade()
						.obterCaracteristicaGradePorChavePrimaria(new AacCaracteristicaGradeId(grade.getSeq(),"Obter CBO profissional"));
				if (aacCaract != null) { // -- cbo do profissional
					vPesCodigo = grade.getProfServidor().getPessoaFisica().getCodigo();
					
					if (grade.getProfServidor() != null) {
						vVinCodigo = grade.getProfServidor().getId().getVinCodigo();
						vMatricula = grade.getProfServidor().getId().getMatricula();
					}
				} else { // -- cbo do chefe da equipe
					vPesCodigo = grade.getEquipe().getProfissionalResponsavel().getPessoaFisica().getCodigo();
					
					if (grade.getEquipe().getProfissionalResponsavel() != null) {
						vVinCodigo = grade.getEquipe().getProfissionalResponsavel().getId().getVinCodigo();
						vMatricula = grade.getEquipe().getProfissionalResponsavel().getId().getMatricula();
					}
				}			
			}
		} else if (pCrgSeq != null) {
			MbcProfCirurgias profCirurgia = getBlocoCirurgicoFacade().buscaRapServidorPorCrgSeqEIndResponsavel(pCrgSeq, DominioSimNao.S);
			vPesCodigo = profCirurgia.getServidorPuc().getPessoaFisica().getCodigo();
			if (profCirurgia.getServidorPuc().getPessoaFisica().getRapServidores() != null) {
				vVinCodigo = profCirurgia.getServidorPuc().getPessoaFisica().getRapServidores().getId().getVinCodigo();
				vMatricula = profCirurgia.getServidorPuc().getPessoaFisica().getRapServidores().getId().getMatricula();
			}
		}

		CnsResponsavelVO retorno = new CnsResponsavelVO();
		
		if (vPesCodigo != null) {
			
			// TODO eschweigert detectamos um BUG no agh, devemos esperar o
			// retorno do NEY em 28/11 para obter solução disso
			// antigamente busca o registro por chave primária, contudo, a mesma
			// recebeu um campo extra SEQ que atualmente não
			// era considerada na busca.
			final List<RapPessoaTipoInformacoes> pessoaTipoInformacao = getRegistroColaboradorFacade()
					.listarRapPessoaTipoInformacoesPorPesCodigoTiiSeq(vPesCodigo, vTipoCns);
			if (pessoaTipoInformacao != null && !pessoaTipoInformacao.isEmpty()) {
				vItemCns = Long.valueOf(pessoaTipoInformacao.get(0).getValor());
			}
			
			if (vVinCodigo != null) {
				retorno.setVinCodigo(vVinCodigo);
			}
			else {
				retorno.setVinCodigo(pSerVinCodigo);
			}
			
			if (vMatricula != null) {
				retorno.setMatricula(vMatricula);
			}
			else {
				retorno.setMatricula(pSerMatricula);
			}
		}
		
		retorno.setItemCns(vItemCns);
		
		return retorno;
	}

	public void fatpAgruBpaBpi(final boolean previa, final Date cpeDtHrInicio,
			final Integer ano, final Integer mes, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		agrupaBpaBpi(
				previa,
				getFatCompetenciaDAO().obterPorChavePrimaria(
						new FatCompetenciaId(DominioModuloCompetencia.AMB, mes
								.intValue(), ano.intValue(), cpeDtHrInicio)), nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * ORADB Procedure FATP_AGRU_BPA_BPI
	 * 
	 * @param previa
	 * @param fatCompetencia
	 * @param dataFimVinculoServidor 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void agrupaBpaBpi(final boolean previa,
			final FatCompetencia fatCompetencia, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		DominioModuloCompetencia modulo = DominioModuloCompetencia.AMB;
		String vRegAnt = "";
		Byte vLinha = 0;
		Short vFolha = 1;
		Short vFolhaBpa = 1;
		Integer vCont = 0;
		String vCodUps = null;
		DominioBoletimAmbulatorio vOrigemInf;

		final Integer ano = fatCompetencia.getId().getAno();
		final Integer mes = fatCompetencia.getId().getMes();
		final Date cpeDtHrInicio = fatCompetencia.getId().getDtHrInicio();

		final FatArqEspelhoProcedAmbDAO arqEspelhoProcedAmbDAO = getFatArqEspelhoProcedAmbDAO();
		final FatItensProcedHospitalarDAO itensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final IPacienteFacade pacienteFacade = getPacienteFacade();
		final ICadastroPacienteFacade cadastroPacienteFacade = getCadastroPacienteFacade();
		final FatcBuscaServClassRN fatcBuscaServClassRN = getFatcBuscaServClassRN();

		arqEspelhoProcedAmbDAO.removeArqEspelhoProcedAmb(cpeDtHrInicio, ano, mes, modulo, DominioTipoFormularioDataSus.C);

		vCodUps = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CODIGO_DA_UPS);
		
		List<CursorEspelhoVO> listaEspelhos = getFatEspelhoProcedAmbDAO()
				.listarEspelho(previa, true, cpeDtHrInicio, ano, mes, modulo, DominioTipoFormularioDataSus.C);
		
		listaEspelhos = aplicarFatcBuscaServClassCursorEspelhoVO(fatcBuscaServClassRN, listaEspelhos);

		//UserTransaction userTransaction = obterUserTransaction(null);
		//this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
		
		for (CursorEspelhoVO espelho : listaEspelhos) {
			if (CoreUtil.igual(vLinha, 20)) {
				vLinha = 0;
				vFolha++;
			}
			vLinha++;
			vCont++;
			if (CoreUtil.igual(vCont, 100)) {
				this.flush();
				//this.commitTransaction();
				vCont = 0;
			}

			FatItensProcedHospitalar itensProcedHospitalar = itensProcedHospitalarDAO
					.obterPorChavePrimaria(new FatItensProcedHospitalarId(
							espelho.getIphPhoSeq(), espelho.getIphSeq()));

			FatArqEspelhoProcedAmb elemento = new FatArqEspelhoProcedAmb();

			elemento.setFatCompetencia(fatCompetencia);
			elemento.setCodigoUps(vCodUps);
			elemento.setCompetencia(espelho.getCompetencia());
			elemento.setFolha(vFolha);
			elemento.setLinha(vLinha);
			elemento.setProcedimentoHosp(espelho.getProcedimentoHosp());
			elemento.setCodAtvProf(espelho.getCodAtvProf());
			elemento.setIdade(espelho.getIdade());
			elemento.setQuantidade(espelho.getQuantidade() == null ? null : espelho.getQuantidade().intValue());
			elemento.setVlrAnestes(espelho.getVlrAnestes());
			elemento.setVlrProc(espelho.getVlrProc());
			elemento.setVlrSadt(espelho.getVlrSadt());
			elemento.setVlrServHosp(espelho.getVlrServHosp());
			elemento.setVlrServProf(espelho.getVlrServProf());
			elemento.setItensProcedHospitalar(itensProcedHospitalar);
			elemento.setFccSeq(espelho.getFccSeq());
			elemento.setFcfSeq(espelho.getFcfSeq());
			elemento.setOrigemInf(espelho.getOrigemInf());
			elemento.setTipoFormulario(DominioTipoFormularioDataSus.C);
			
			// Ney 01/09/2012 (Substituir v_serv e v_class pelas variaveis do cursor quando já vier populada no espelho)
			if(!StringUtils.isBlank(espelho.getServClass())){
				elemento.setServico(StringUtils.substring(espelho.getServClass(),0,2));
				elemento.setClassificacao(StringUtils.substring(espelho.getServClass(),3));
			}

			getFaturamentoON().persistirFatArqEspelhoProcedAmb(elemento, false);
		}
		//userTransaction = commitUserTransaction(userTransaction);		
		
		//SEGUNDO A MILENA ESSA ATUALIZAÇÃO NÃO É NECESSÁRIA, POIS JÁ FOI FEITA NA ROTINA ANTERIOR
//		if (previa.equals(DominioSimNao.N)) {
//			atualizaFatProcedAmbRealizado(cpeDtHrInicio, ano, mes, DominioModuloCompetencia.AMB,  nomeMicrocomputador, dataFimVinculoServidor);
//		}

		// -- parte 2

		arqEspelhoProcedAmbDAO.removeArqEspelhoProcedAmb(cpeDtHrInicio, ano, mes, DominioModuloCompetencia.AMB, DominioTipoFormularioDataSus.I);
		vFolha++;
		vFolhaBpa = vFolha.shortValue();
		vLinha = 0;
		vOrigemInf = DominioBoletimAmbulatorio.BPI;

		List<CursorEspelhoBpiVO> listaEspelhosBpi = getFatEspelhoProcedAmbDAO()
				.listarEspelhoBpi(previa,
						DateUtil.getCalendarBy(cpeDtHrInicio), ano, mes,
						modulo, DominioTipoFormularioDataSus.I);

		listaEspelhosBpi = aplicarFatcBuscaServClassCursorEspelhoBpi(fatcBuscaServClassRN, listaEspelhosBpi);
		
		for (CursorEspelhoBpiVO espelhoBpi : listaEspelhosBpi) {
			if (CoreUtil.igual(vLinha, 20)
					|| (!vRegAnt.equals("") && !espelhoBpi.getCnscbo().equals(
							vRegAnt))) {
				vLinha = 0; vFolha++;
				if (CoreUtil.igual(vFolha, 1000)) {
					vFolha = vFolhaBpa.shortValue();
				}
				vRegAnt = espelhoBpi.getCnscbo();
			}
			vLinha++;
			vCont++;
			if (CoreUtil.igual(vCont, 100)) {
				//userTransaction = commitUserTransaction(userTransaction);
				this.flush();
				//this.commitTransaction();
				vCont = 0;
			}

			FatItensProcedHospitalar itensProcedHospitalar = itensProcedHospitalarDAO
					.obterPorChavePrimaria(new FatItensProcedHospitalarId(
							espelhoBpi.getIphphoseq(), espelhoBpi.getIphseq()));

			FatArqEspelhoProcedAmb elemento = new FatArqEspelhoProcedAmb();

			elemento.setFatCompetencia(fatCompetencia);
			elemento.setCodigoUps(vCodUps);
			elemento.setCompetencia(espelhoBpi.getCompetencia());
			elemento.setFolha(vFolha);
			elemento.setLinha(vLinha);
			elemento.setProcedimentoHosp(espelhoBpi.getProcedimentohosp());
			elemento.setCodAtvProf(espelhoBpi.getCodatvprof());
			elemento.setQuantidade(espelhoBpi.getQuantidadesumarizada());
			
			elemento.setVlrAnestes( espelhoBpi.getVlranestes() == null ? null : new BigDecimal(espelhoBpi.getVlranestes()));
			elemento.setVlrProc(espelhoBpi.getVlrproc() == null ? BigDecimal.ZERO : new BigDecimal(espelhoBpi.getVlrproc()));
			elemento.setVlrSadt(espelhoBpi.getVlrsadt() == null ? null : new BigDecimal(espelhoBpi.getVlrsadt()));
			elemento.setVlrServHosp(espelhoBpi.getVlrservhosp() == null ? BigDecimal.ZERO : new BigDecimal(espelhoBpi.getVlrservhosp()));
			elemento.setVlrServProf(espelhoBpi.getVlrservprof() == null ? BigDecimal.ZERO : new BigDecimal(espelhoBpi.getVlrservprof()));
			
			elemento.setItensProcedHospitalar(itensProcedHospitalar);
			elemento.setFccSeq(espelhoBpi.getFccseq());
			elemento.setFcfSeq(espelhoBpi.getFcfseq());
			elemento.setCnsMedico(espelhoBpi.getCnsmedico());
			elemento.setDataAtendimento(espelhoBpi.getDataatendimento());
			elemento.setCnsPaciente(espelhoBpi.getCnspaciente());
			elemento.setNomePaciente(espelhoBpi.getNomepaciente());
			elemento.setDtNascimento(espelhoBpi.getDtnascimento());
			elemento.setRaca(espelhoBpi.getRaca());
			elemento.setCaraterAtendimento(espelhoBpi.getCarateratendimento());
			elemento.setNroAutorizacao(espelhoBpi.getNroautorizacao());
			elemento.setSexo(espelhoBpi.getSexo() == null ? null : DominioSexo.valueOf(espelhoBpi.getSexo()));
			elemento.setCodIbge(espelhoBpi.getCodibge());
			elemento.setCid10(espelhoBpi.getCid10());
			elemento.setIdade(espelhoBpi.getIdade());
			elemento.setOrigemInf(vOrigemInf);
			elemento.setTipoFormulario(DominioTipoFormularioDataSus.I);

			if (espelhoBpi.getNacionalidade() != null) {
				AipNacionalidades nacionalidade = pacienteFacade.obterNacionalidade(espelhoBpi.getNacionalidade());
				elemento.setNacionalidade(nacionalidade);
			}

			// MARINA - 14/05/2013
			// bU8SCA DADOS CADASTRAIS DO PACIENTE
			VAipEnderecoPaciente enderecoPaciente = cadastroPacienteFacade.obterEndecoPaciente(espelhoBpi.getPacCodigo());
			populaEnderecoPacienteEmEspelho(enderecoPaciente,elemento);
			
			// Ney 01/09/2012 (Substituir v_serv e v_class pelas variaveis do cursor quando já vier populada no espelho)
			if(!StringUtils.isBlank(espelhoBpi.getServClass())){
				elemento.setServico(StringUtils.substring(espelhoBpi.getServClass(),0,2));
				elemento.setClassificacao(StringUtils.substring(espelhoBpi.getServClass(),3));
			}
			
			getFaturamentoON().persistirFatArqEspelhoProcedAmb(elemento, false);
		}
		//userTransaction = commitUserTransaction(userTransaction);
		//this.flush();
		//this.commitTransaction();

		//SEGUNDO A MILENA ESSA ATUALIZAÇÃO NÃO É NECESSÁRIA, POIS JÁ FOI FEITA NA ROTINA ANTERIOR
//		if (previa.equals(DominioSimNao.N)) {
//			atualizaFatProcedAmbRealizado(cpeDtHrInicio, ano, mes, DominioModuloCompetencia.AMB, nomeMicrocomputador, dataFimVinculoServidor);
//		}
	}

//	private void atualizaFatProcedAmbRealizado(Date cpeDtHrInicio, Integer ano,
//			Integer mes, DominioModuloCompetencia amb, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
//		
//		FatProcedAmbRealizadoDAO procedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
//		ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON = getProcedimentosAmbRealizadosON();
//		
//		List<Long> listaPmrSeq = getFatEspelhoProcedAmbDAO().obterPmrSeq(
//				cpeDtHrInicio, ano, mes, DominioModuloCompetencia.AMB);
//		
//		int indice = 0;
//		
//		List<FatProcedAmbRealizado> listarFatProcedAmbRealizado = null;
//		UserTransaction userTransaction = obterUserTransaction(null);
//		
//		while (indice < listaPmrSeq.size()) {
//			
//			if (indice + 1000  > listaPmrSeq.size()) {
//				listarFatProcedAmbRealizado = procedAmbRealizadoDAO.
//					listarFatProcedAmbRealizadoPorSeqESituacao(listaPmrSeq.subList(indice, listaPmrSeq.size()).toArray(), 
//															   DominioSituacaoProcedimentoAmbulatorio.ENCERRADO);
//			}
//			else {
//				listarFatProcedAmbRealizado = procedAmbRealizadoDAO.
//					listarFatProcedAmbRealizadoPorSeqESituacao(listaPmrSeq.subList(indice, indice + 1000).toArray(), 
//															   DominioSituacaoProcedimentoAmbulatorio.ENCERRADO);
//			}
//		
//			for (FatProcedAmbRealizado procedAmbRealizado : listarFatProcedAmbRealizado) {
//				FatProcedAmbRealizado clone = procedimentosAmbRealizadosON.clonarFatProcedAmbRealizado(procedAmbRealizado);
//				procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.APRESENTADO);
//				procedimentosAmbRealizadosON.atualizarProcedimentoAmbulatorialRealizado(
//								procedAmbRealizado, clone, nomeMicrocomputador, dataFimVinculoServidor);
//			}
//			indice += 1000;
//			userTransaction = commitUserTransaction(userTransaction);
//		}
//		
//	}

	private void populaEnderecoPacienteEmEspelho(final VAipEnderecoPaciente enderecoPaciente, final FatArqEspelhoProcedAmb elemento) {
		// Marina 27/05/2013
		if (enderecoPaciente != null) {
			
			if (enderecoPaciente.getCep() != null) {
				elemento.setEndCepPaciente(enderecoPaciente.getCep().intValue()); // substr(v_end.CEP,1,8)
			}
			
			elemento.setEndCodLogradouroPaciente(enderecoPaciente.getTlgCodigo() != null ? enderecoPaciente.getTlgCodigo() : 58); // , NVL(v_end.TLG_CODIGO,58)

			// , SUBSTR(v_end.LOGRADOURO,1,30)
			if(enderecoPaciente.getLogradouro() != null){
				elemento.setEndLogradouroPaciente(StringUtils.substring(enderecoPaciente.getLogradouro(),0,29));
			}
			
			// , SUBSTR(v_end.COMPL_LOGRADOURO,1,10)
			if(enderecoPaciente.getComplLogradouro() != null){
				elemento.setEndComplementoPaciente(StringUtils.substring(enderecoPaciente.getComplLogradouro(),0,9));
			}
			
			if(enderecoPaciente.getNroLogradouro() != null){
				elemento.setEndNumeroPaciente(enderecoPaciente.getNroLogradouro());
			} else {
				elemento.setEndNumeroPaciente(0);
			}
			
			// , SUBSTR(decode(v_end.BAIRRO, null, 'CENTRO',v_end.BAIRRO),1,30)
			if(enderecoPaciente.getBairro() != null){
				elemento.setEndBairroPaciente(StringUtils.substring(enderecoPaciente.getBairro(),0,29));
			} else {
				elemento.setEndBairroPaciente("CENTRO");
			}
		}
		
	}

	/**
	 * Atualiza a lista de CursorEspelhoBpiVO aplicando e agrupando pela coluna:
	 *  FATC_BUSCA_serv_class (NULL ,epa.IPH_SEQ ,epa.IPH_PHO_SEQ ,epa.unidade_funcional) serv_class
	 */
	private final List<CursorEspelhoVO> aplicarFatcBuscaServClassCursorEspelhoVO(final FatcBuscaServClassRN fatcBuscaServClassRN, final List<CursorEspelhoVO> listaEspelho) throws ApplicationBusinessException {
		
		List<CursorEspelhoVO> listaRetorno = new ArrayList<CursorEspelhoVO>();

		for (CursorEspelhoVO vo : listaEspelho) {
			vo.setServClass(fatcBuscaServClassRN.fatcBuscaServClass(null, vo.getIphSeq(), vo.getIphPhoSeq(), vo.getUnidadeFuncional()));
		}
		
		boolean achou;
		for (CursorEspelhoVO vo : listaEspelho) {
			achou = false;
			
			for (CursorEspelhoVO voResult : listaRetorno) {
				if(vo.equals(voResult)){
					achou = true;
					voResult.setQuantidade( sumValores(voResult.getQuantidade(), vo.getQuantidade()) );
					voResult.setVlrAnestes( sumValores(voResult.getVlrAnestes(), vo.getVlrAnestes()));
					voResult.setVlrProc(	sumValores(voResult.getVlrProc(),    vo.getVlrProc()));
					voResult.setVlrSadt(	sumValores(voResult.getVlrSadt(),    vo.getVlrSadt()));
					voResult.setVlrServHosp(sumValores(voResult.getVlrServHosp(),vo.getVlrServHosp()));
					voResult.setVlrServProf(sumValores(voResult.getVlrServProf(),vo.getVlrServProf()));
				}
			}			
			if(!achou){
				listaRetorno.add(vo);
			}
		}		
		return listaRetorno;
	}

	private double sumValores(Double v1, Double v2){
		double val1 = v1 != null ? v1 : 0;
		double val2 = v2 != null ? v2 : 0;
		return val1+val2;
	}
	
	private int sumValores(Integer v1, Integer v2){
		int val1 = v1 != null ? v1 : 0;
		int val2 = v2 != null ? v2 : 0;
		return val1+val2;
	}
	
	private long sumValores(Long v1, Long v2){
		long val1 = v1 != null ? v1 : 0;
		long val2 = v2 != null ? v2 : 0;
		return val1+val2;
	}
	
	private BigDecimal sumValores(BigDecimal v1, BigDecimal v2){
		BigDecimal val1 = v1 != null ? v1 : BigDecimal.ZERO;
		BigDecimal val2 = v2 != null ? v2 : BigDecimal.ZERO;
		return val1.add(val2);
	}
	
	/**
	 * Atualiza a lista de CursorEspelhoBpiVO aplicando e agrupando pela coluna:
	 *  FATC_BUSCA_serv_class (NULL ,epa.IPH_SEQ ,epa.IPH_PHO_SEQ ,epa.unidade_funcional) serv_class
	 */
	private final List<CursorEspelhoBpiVO> aplicarFatcBuscaServClassCursorEspelhoBpi(final FatcBuscaServClassRN fatcBuscaServClassRN, final List<CursorEspelhoBpiVO> listaEspelhosBpi) throws ApplicationBusinessException {
		
		List<CursorEspelhoBpiVO> listaRetorno = new ArrayList<CursorEspelhoBpiVO>();

		for (CursorEspelhoBpiVO vo : listaEspelhosBpi) {
			vo.setServClass(fatcBuscaServClassRN.fatcBuscaServClass(null, vo.getIphseq(), vo.getIphphoseq(), vo.getUnidadefuncional()));
		}
		
		boolean achou;
		for (CursorEspelhoBpiVO vo : listaEspelhosBpi) {
			achou = false;
			
			for (CursorEspelhoBpiVO voResult : listaRetorno) {
				if(vo.equals(voResult)){
					achou = true;
					voResult.setQuantidadesumarizada(sumValores(voResult.getQuantidadesumarizada(),vo.getQuantidadesumarizada()));
					voResult.setVlranestes(sumValores(voResult.getVlranestes(),vo.getVlranestes()));
					voResult.setVlrproc(sumValores(voResult.getVlrproc(),vo.getVlrproc()));
					voResult.setVlrsadt(sumValores(voResult.getVlrsadt(),vo.getVlrsadt()));
					voResult.setVlrservhosp(sumValores(voResult.getVlrservhosp(),vo.getVlrservhosp()));
					voResult.setVlrservprof(sumValores(voResult.getVlrservprof(),vo.getVlrservprof()));
				}
			}
			
			if(!achou){
				listaRetorno.add(vo);
			}
		}		
		return listaRetorno;
	}

	/**
	 * ORADB FUNCTION RN_MATC_VER_PHI
	 * 
	 * @param chtSeq
	 * @param phiSeq
	 * @return
	 */
	public boolean verificaProcedimentoHospitalarInterno(Integer matCodigo) {
		final Long count = getFatProcedHospInternosDAO()
				.verificaProcedimentoHospitalarInternoCount(matCodigo);
		return count != null && count > 0;
	}

	public Map<Integer, CmceCthSeqVO> listarCmceParaListaCthSeq(
			List<Integer> lstParcial, Byte magicByteCspSeqEq1) {
		Map<Integer, CmceCthSeqVO> result = new HashMap<Integer, CmceCthSeqVO>();
		FatEspelhoAihDAO fatEspelhoDao = this.getFatEspelhoAihDAO();
		List<CmceCthSeqVO> listCmce = fatEspelhoDao.listarCmcePorChtSeq(lstParcial, magicByteCspSeqEq1);
		for(CmceCthSeqVO cmceVO : listCmce){
			result.put(cmceVO.getCthSeq(), cmceVO);
		}
		return result;
	}

	public Map<Integer, String> listarMsgErroParaListaCthSeq(
			List<Integer> lstParcial) {
		Map<Integer, String> result = new HashMap<Integer, String>();
		List<MsgErroCthSeqVO> listMsgErros = fatMensagemLogDAO.listarMensagensLogErrosChtSeq(lstParcial);
		Integer cthSeq = null;
		StringBuffer msgError = new StringBuffer();
		for(MsgErroCthSeqVO vo : listMsgErros){
			if(!vo.getCthSeq().equals(cthSeq)){
				if(StringUtils.isNotEmpty(msgError.toString())){
					result.put(cthSeq, msgError.substring(0,msgError.length()-2));
					msgError = new StringBuffer();
				}
				cthSeq = vo.getCthSeq();
			}
			msgError.append(vo.getMsgErro());
			msgError.append(", ");
		}
		if(StringUtils.isNotEmpty(msgError.toString())){
			result.put(cthSeq, msgError.substring(0,msgError.length()-2));
		}
		return result;
	}
	
	public List<FtLogErrorVO> pesquisaFatLogErrorFatMensagensLog(Integer contaHospitalar,DominioSituacaoMensagemLog situacao,boolean administrarUnidadeFuncionalInternacao, boolean leituraCadastrosBasicosFaturamento
			,boolean  manterCadastrosBasicosFaturamento, final Short ichSeqp, final String erro,final Integer phiSeqItem1, final Long codItemSus1){
		
		List<FatLogError> lista = new ArrayList<FatLogError>();
		
		if(administrarUnidadeFuncionalInternacao && !leituraCadastrosBasicosFaturamento && !manterCadastrosBasicosFaturamento){
			lista = this.fatLogErrorDAO.pesquisaFatLogErrorFatMensagensLog(contaHospitalar, situacao, true, ichSeqp, erro, phiSeqItem1, codItemSus1);
		}else{
			lista = this.fatLogErrorDAO.pesquisaFatLogErrorFatMensagensLog(contaHospitalar, situacao, false, ichSeqp, erro, phiSeqItem1, codItemSus1);
		}
		return ajustaLogErrorParaVO(lista); 
	}
	
	
	private List<FtLogErrorVO> ajustaLogErrorParaVO(List<FatLogError> listaFatLogErrors){
		
		Integer codigoPHI;
     	Integer codigoErro;
		
		List<FtLogErrorVO> listaAjustada = new ArrayList<FtLogErrorVO>();
		List<FtLogErrorPhiCodVO> listaFtLogErrorPhiCodVO = new ArrayList<FtLogErrorPhiCodVO>();
		int indice=0;
		int inicio=indice+1;
		
		while(indice<listaFatLogErrors.size()){
			codigoPHI = listaFatLogErrors.get(indice).getPhiSeqItem1();
			codigoErro = listaFatLogErrors.get(indice).getSeq();
			
			FtLogErrorPhiCodVO ftLogErrorPhiCodVO = new FtLogErrorPhiCodVO(codigoPHI, codigoErro);
			listaFtLogErrorPhiCodVO.add(ftLogErrorPhiCodVO);
			while(inicio<listaFatLogErrors.size()){
				if(listaFatLogErrors.get(indice).getErro().equals(listaFatLogErrors.get(inicio).getErro())){
					codigoPHI = listaFatLogErrors.get(inicio).getPhiSeqItem1();
					codigoErro = listaFatLogErrors.get(inicio).getSeq();
					ftLogErrorPhiCodVO = new FtLogErrorPhiCodVO(codigoPHI, codigoErro);
					listaFtLogErrorPhiCodVO.add(ftLogErrorPhiCodVO);
					//remover da lista
					listaFatLogErrors.remove(listaFatLogErrors.get(inicio));
					inicio--;
				}
				inicio++;
			}
			
			//Cria VO a adiciona na listaAjustada
			String criticidade="";
			if(listaFatLogErrors.get(indice).getFatMensagemLog().getSituacao()!=null){
				criticidade = listaFatLogErrors.get(indice).getFatMensagemLog().getSituacao().getDescricao();
			}
			
			FtLogErrorVO ftLogErrorVO = new FtLogErrorVO(listaFatLogErrors.get(indice).getErro(),listaFtLogErrorPhiCodVO,
					ajustaCriticidade(criticidade));
			listaAjustada.add(ftLogErrorVO);
			indice++;
			inicio=indice+1;
			listaFtLogErrorPhiCodVO = new ArrayList<FtLogErrorPhiCodVO>();
		}
		
		return listaAjustada;		
	}	
	
	private String ajustaCriticidade(String criticidade) {
		
		if(criticidade.equals("Não Cobrado")){
			return "Não Cobra";
		}else if(criticidade.equals("Inconsistente")){
			return "Inconsistência";
		}else if(criticidade.equals("Não Encerrado")){
			return "Não Encerra";
		}else{
			return "";
		}
	}
	
	public Long pesquisaFatLogErrorFatMensagensLogCount(Integer contaHospitalar,DominioSituacaoMensagemLog situacao,boolean administrarUnidadeFuncionalInternacao, boolean leituraCadastrosBasicosFaturamento
			,boolean  manterCadastrosBasicosFaturamento, final Short ichSeqp, final String erro,final Integer phiSeqItem1, final Long codItemSus1){
		
		if(administrarUnidadeFuncionalInternacao && (leituraCadastrosBasicosFaturamento || manterCadastrosBasicosFaturamento)){
			return this.fatLogErrorDAO.pesquisaFatLogErrorFatMensagensLogCount(contaHospitalar, situacao, true, ichSeqp, erro, phiSeqItem1, codItemSus1);
		}else{
			return this.fatLogErrorDAO.pesquisaFatLogErrorFatMensagensLogCount(contaHospitalar, situacao, false, ichSeqp, erro, phiSeqItem1, codItemSus1);
		}
	}
	
	/**
	 * #42229
	 * @ORADB: RN_ATMP_VER_APAC_AUT 
	 */
	public void verificaApacAutorizacao(Integer pacCodigo, Date data, Byte tipoTratamento) throws ApplicationBusinessException {
		//Retorno do metodo com valor true ignora o restante da validação
		if (!validaPeriodoTratamento(pacCodigo, tipoTratamento, data)) {
			List<CursorBuscaApacVO> buscaApac = fatAtendimentoApacProcHospDAO.obterListaCursorBuscaApacVOs(pacCodigo, tipoTratamento); // TODO
			if (!buscaApac.isEmpty()) {
				if (buscaApac.get(0).getIndAutorizadoSms().equals("S")) {
					verificaConsultaPeriodoApac(data, buscaApac.get(0));
				} else {
					Date dt_fim = DateUtil.obterDataFimCompetencia(DateUtil.adicionaMeses(buscaApac.get(0).getDtInicio(), 2));
					List<CursorBuscaOutraVO> buscaOutra = fatAtendimentoApacProcHospDAO.obterListaCursorCBuscaOutra(buscaApac.get(0).getNumero(), buscaApac.get(0).getDtInicioValidade(), buscaApac.get(0).getCapType(), pacCodigo,buscaApac.get(0).getPhiSeq());
					if (buscaOutra.isEmpty()) {
						//consulta na tabela de historico e verifica se está autorizada
						verificaHistoricoApacAutorizada(buscaApac.get(0), pacCodigo);
						// apac encerrada está com certeza autorizada
						verificaConsultaPeriodoApac(data, buscaApac.get(0), dt_fim);
					} else {
						verificaConsultaPeriodoApac(data, buscaApac.get(0), dt_fim);
					}
				}
			} else {
				FaturamentoExceptionCode.FAT_00856.throwException();
				// Paciente não possui apac em andamento
			}
		}
	}
	
	/**
	 * #42229 Retorno true ignora o restante da verificação
	 * @param pacCodigo
	 * @param tipoTratamento
	 * @param data
	 * @return
	 */
	private boolean validaPeriodoTratamento(Integer pacCodigo,Byte tipoTratamento,Date data) {
		List<CursorCTrpVO> tipoTratamentoVO = fatTipoTratamentosDAO.obterListaCursorCTrpVO(pacCodigo, tipoTratamento);
		if(!tipoTratamentoVO.isEmpty()){
			if(DateUtil.validaDataMaiorIgual(DateUtil.truncaData(data),DateUtil.truncaData(tipoTratamentoVO.get(0).getDtInicioAgendaSessao()))
					&& DateUtil.validaDataMenorIgual(DateUtil.truncaData(data),DateUtil.truncaData(tipoTratamentoVO.get(0).obterDataFimAut()))){
				return true;
			}
		}
		return false;
	}
	/**
	 * #42229 verifica consulta periodo
	 * @param data
	 * @param apac
	 * @param dt_fim
	 * @throws ApplicationBusinessException
	 */
	private void verificaConsultaPeriodoApac(Date data, CursorBuscaApacVO apac, Date dt_fim) throws ApplicationBusinessException {
		if(!(DateUtil.validaDataMaiorIgual(data, apac.getDtInicio()) && DateUtil.validaDataMenorIgual(data, dt_fim))){
			 //consulta para periodo não autorizado
			 FaturamentoExceptionCode.FAT_00857.throwException(); //TODO FAT-00857
		 }
	}
	/** 
	 * #42229 verifica consulta periodo
	 * @param data
	 * @param apac
	 * @param dt_fim
	 * @throws ApplicationBusinessException
	 */
	private void verificaConsultaPeriodoApac(Date data, CursorBuscaApacVO apac) throws ApplicationBusinessException {
		if (!(DateUtil.validaDataMaiorIgual(data, apac.getDtInicio()))) {
			FaturamentoExceptionCode.FAT_00857.throwException(); // TODO FAT-00857
			// consulta para período não autorizado
		}
	}

	/**
	 * #42229 verifica se apac na tabela historico está autorizada
	 * @param data
	 * @param apac
	 * @param dt_fim
	 * @throws ApplicationBusinessException
	 */
	private void verificaHistoricoApacAutorizada(CursorBuscaApacVO apac,Integer pacCodigo) throws ApplicationBusinessException {
		List<CursorBuscaHistoricoVO> buscaHistorico = fatAtendimentoApacProcHospDAO.obterListaCursorBuscaHistorico(apac.getNumero(), apac.getDtInicioValidade(),apac.getCapType(), pacCodigo, apac.getPhiSeq());
		 if(buscaHistorico.isEmpty()){
			 FaturamentoExceptionCode.FAT_00858.throwException(); //TODO FAT-00858
			 //Paciente nao possui apac autorizada
		 }
	}
	
	/** #42803
	 * ORADB: FATP_CANDIDATO_APAC_DESC
	 * @param consulta, dataRealizado
	 * @return Candidato
	 * @throws ApplicationBusinessException 
	 * metodo principal
	 */ 
	public String fatpCandidatoApacDesc(Integer consulta,Date dataRealizado) throws ApplicationBusinessException{
		
		FatpCandidatoApacDescVO fatpCandidatoApacDescVO = new FatpCandidatoApacDescVO();
		fatpCandidatoApacDescVO.setContador(0);
		fatpCandidatoApacDescVO.setPacAnterior(0);
		//		Date vOutubro = to_date('01102003','ddmmyyyy');
		fatpCandidatoApacDescVO.setDtReferencia(dataRealizado);
		fatpCandidatoApacDescVO.setDtIniRealizado(dataRealizado);
		
		
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(fatpCandidatoApacDescVO.getDtReferencia());  
        calendar.add(Calendar.DAY_OF_MONTH, -365); 
        calendar.add(Calendar.MONTH, 1);  
        calendar.set(Calendar.DAY_OF_MONTH, 1);  
        calendar.add(Calendar.DATE, -1);
        
        fatpCandidatoApacDescVO.setDtAnterior(calendar.getTime());
		
		AghParametrosVO aghParametrosVO = getParametrosEnum();
		
		fatpCandidatoApacDescVO.setMaxData(fatCandidatosApacOtorrinoDAO.obterDataMaxCriacao());
		if(fatpCandidatoApacDescVO.getMaxData() == null){
			try {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
				fatpCandidatoApacDescVO.setMaxData(format.parse("010120000000"));
			} catch (ParseException e) {
				ApplicationBusinessException excp = new ApplicationBusinessException(e.getMessage(), Severity.ERROR, e);
				throw excp;
			}
		}
		 
		 //c_busca_pmr - return FatProcedAmbRealizado
		 List<FatProcedAmbRealizado> rPmr = fatProcedAmbRealizadoDAO.listarProcedimentosHospitalaresPorConsulta(consulta);
		 
		 for(FatProcedAmbRealizado lista : rPmr){
			 fatpCandidatoApacDescVO.setTeveApacAparelho('N');

			 if(lista != null){
				 fatpCandidatoApacDescVO.setMsgLog(null);
				 //ver_idade_pac
				 TipoProcedHospitalarInternoVO tipoProcedHospitalarInternoVO = ambulatorioFacade.verificaIdadePaciente(lista.getPaciente().getCodigo(), aghParametrosVO.getVlrData());	
				 
				 
				 paremetros(fatpCandidatoApacDescVO, tipoProcedHospitalarInternoVO);		 
				 
				 // verifica se procedimento é de aparelho
				 CaracteristicaPhiVO caracteristicaPhiVO = faturamentoFacade.fatcVerCaractPhi(lista.getConvenioSaudePlano().getId().getCnvCodigo(), lista.getConvenioSaudePlano().getId().getSeq(), lista.getProcedimentoHospitalarInterno().getSeq(), "Aparelho Auditivo");
				 if(caracteristicaPhiVO.getResultado()){
					 fatpCandidatoApacDescVO.setAparelho('S');
				 }else{
					 fatpCandidatoApacDescVO.setAparelho('N');
				 }
				 
				 // verifica se paciente fez implante antes do atendimento no ambulatório
				 CirurgiaVO cirurgiaVO = new CirurgiaVO();
				 fatpCandidatoApacDescVO.setCirurgia(pacienteFacade.verificarPacienteImplante(lista.getPaciente().getCodigo(), lista.getDthrRealizado(), cirurgiaVO));
				 fatpCandidatoApacDescVO.setTeveApacAparelho('N');
				 
				 //c_busca_apac_aparelho
				 List<FatResumoApacs> datasFimApacAparelho = faturamentoFacade.buscarDatasResumosApacsAtivos(lista.getPaciente().getCodigo(), lista.getDthrRealizado());
				 
				 for (FatResumoApacs fatResumoApacsDate : datasFimApacAparelho) {
					 fatpCandidatoApacDescVO.setTeveApacAparelho('S');
					 fatpCandidatoApacDescVO.setDtFimApacAparelho(fatResumoApacsDate.getDtFinal());
				 }
				 
				 if(fatpCandidatoApacDescVO.getAparelho().equals('S')){
					 verApacAparelho(lista, fatpCandidatoApacDescVO,tipoProcedHospitalarInternoVO);
				 }else{
//						 FatResumoApacs resumo = faturamentoFacade.buscaResumo(lista.getPaciente().getCodigo(), null);
					 List<Integer> listaPhi = new ArrayList<Integer>();
					 listaPhi.add(tipoProcedHospitalarInternoVO.getPhiTerapiaAdu());
					 listaPhi.add(tipoProcedHospitalarInternoVO.getPhiTerapiaInf());
					 
					 
					 if(lista.getProcedimentoHospitalarInterno().getSeq() == tipoProcedHospitalarInternoVO.getPhiSelecaoAmbos()){
						 verApacSelecao(lista, fatpCandidatoApacDescVO, tipoProcedHospitalarInternoVO);
					 }else if(listaPhi.contains(lista.getProcedimentoHospitalarInterno().getSeq())){
						 verApacTerapia(tipoProcedHospitalarInternoVO, fatpCandidatoApacDescVO, lista);
					 }else{
						 verApac(lista, tipoProcedHospitalarInternoVO, fatpCandidatoApacDescVO);
					 }
				 }
				 
			 }else{
				 break;
			 }
		 }
			 
		 Calendar novoCalendar = Calendar.getInstance();
		 novoCalendar.setTime(fatpCandidatoApacDescVO.getDtReferencia());
		 novoCalendar.add(Calendar.DAY_OF_MONTH, 1);
		 
		 fatpCandidatoApacDescVO.setDtReferencia(novoCalendar.getTime());	
		 
		 return fatpCandidatoApacDescVO.getRetorno();

	}

	private AghParametrosVO getParametrosEnum()
			throws ApplicationBusinessException {
		// mudança de apresentação para vo
		AghParametrosVO aghParametrosVO =  new AghParametrosVO();
		aghParametrosVO.setNome(AghuParametrosEnum.P_DT_FIM_COMP_APT.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		
//			AghParametros aghParametro = getParametroFacade().getAghpParametro(AghuParametrosEnum.P_DT_FIM_COMP_APT);
		
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00935.throwException();
		}

		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_DIAG_DEF_AUDITIVA.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
//			vMsg = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PHI_DIAG_DEF_AUDITIVA);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00902.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_DIAG_DEF_MENOR_3ANOS.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00902.throwException();
		}
		
		getParametrosEnum1(aghParametrosVO);
		getParametrosEnum2(aghParametrosVO);
		return aghParametrosVO;
	}

	
	private void getParametrosEnum1(AghParametrosVO aghParametrosVO)
			throws ApplicationBusinessException {
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_DIAG_DEF_MAIOR_3ANOS.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00902.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_REAV_DIAG_MAIOR_3ANOS.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00933.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_REAV_DIAG_MENOR_3ANOS.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00933.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_SELECAO.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00904.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_IMPLANTE_COCLEAR.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00907.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_ACO_ADP_INFANTIL.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg()  != null){
			FaturamentoExceptionCode.FAT_00908.throwException();
		}
		
		getParametrosEnum3(aghParametrosVO);
	}

	private void getParametrosEnum3(AghParametrosVO aghParametrosVO)
			throws ApplicationBusinessException {
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_ACO_ADP_ADULTO.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00908.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_ACO_ADP_MENOR_3ANOS.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00908.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_ACO_ADP_ADU_ANT.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00908.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_ACO_ADP_INF_ANT.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00908.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_ACO_IMPL_INFANTIL.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00910.throwException();
		}
	}

	private void getParametrosEnum2(AghParametrosVO aghParametrosVO)
			throws ApplicationBusinessException {
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_ACO_IMPL_ADULTO.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00910.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_ACO_IMPL_ADU_ANT.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00910.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_ACO_IMPL_INF_ANT.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00910.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_BERA.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00910.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_OTOEMISSOES.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00910.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_TERAPIA_ADULTO.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00934.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_PHI_TERAPIA_INFANTIL.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_00934.throwException();
		}
		
		aghParametrosVO.setNome(AghuParametrosEnum.P_OTO_QTDE_REP.toString());
		getParametroFacade().getAghpParametro(aghParametrosVO);
		if(aghParametrosVO.getMsg() != null){
			FaturamentoExceptionCode.FAT_01137.throwException();
		}
	}

	private void paremetros(FatpCandidatoApacDescVO fatpCandidatoApacDescVO,
			TipoProcedHospitalarInternoVO tipoProcedHospitalarInternoVO) {
		tipoProcedHospitalarInternoVO.setPhiSelecaoAmbos(getParametroFacade().obterAghParametroPorNome("P_PHI_SELECAO").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiSelecao(tipoProcedHospitalarInternoVO.getPhiSelecaoAmbos());
		 
		 tipoProcedHospitalarInternoVO.setPhiReaMaior3anos(getParametroFacade().obterAghParametroPorNome("P_PHI_REAV_DIAG_MAIOR_3ANOS").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiReaDiag(tipoProcedHospitalarInternoVO.getPhiReaMaior3anos());
		 
		 tipoProcedHospitalarInternoVO.setPhiAdaptadoAdu(getParametroFacade().obterAghParametroPorNome("P_PHI_ACO_ADP_ADULTO").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiAdaptado(tipoProcedHospitalarInternoVO.getPhiAdaptadoAdu());
		 
		 tipoProcedHospitalarInternoVO.setPhiAcomimplAduAnt(getParametroFacade().obterAghParametroPorNome("P_PHI_ACO_IMPL_ADU_ANT").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiAcomimpl(tipoProcedHospitalarInternoVO.getPhiAcomimplAduAnt());
		 
		 tipoProcedHospitalarInternoVO.setPhiTerapiaAdu(getParametroFacade().obterAghParametroPorNome("P_PHI_TERAPIA_ADULTO").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiTerapia(tipoProcedHospitalarInternoVO.getPhiTerapiaAdu());

		 tipoProcedHospitalarInternoVO.setPhiTerapiaInf(getParametroFacade().obterAghParametroPorNome("P_PHI_TERAPIA_INFANTIL").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiDiagAnt(getParametroFacade().obterAghParametroPorNome("P_PHI_DIAG_DEF_AUDITIVA").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiDiagMenor3anos(getParametroFacade().obterAghParametroPorNome("P_PHI_DIAG_DEF_MENOR_3ANOS").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiDiagMaior3anos(getParametroFacade().obterAghParametroPorNome("P_PHI_DIAG_DEF_MAIOR_3ANOS").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiReaMenor3anos(getParametroFacade().obterAghParametroPorNome("P_PHI_REAV_DIAG_MENOR_3ANOS").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiImplante(getParametroFacade().obterAghParametroPorNome("P_PHI_IMPLANTE_COCLEAR").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiAdaptadoInf(getParametroFacade().obterAghParametroPorNome("P_PHI_ACO_ADP_INFANTIL").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiAdpMenor3anos(getParametroFacade().obterAghParametroPorNome("P_PHI_ACO_ADP_MENOR_3ANOS").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiAdpAduAnt(getParametroFacade().obterAghParametroPorNome("P_PHI_ACO_ADP_ADU_ANT").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiAdpInfAnt(getParametroFacade().obterAghParametroPorNome("P_PHI_ACO_ADP_INF_ANT").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiAcomimplInf(getParametroFacade().obterAghParametroPorNome("P_PHI_ACO_IMPL_INFANTIL").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiAcomimplAdu(getParametroFacade().obterAghParametroPorNome("P_PHI_ACO_IMPL_ADULTO").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiAcomimplInfAnt(getParametroFacade().obterAghParametroPorNome("P_PHI_ACO_IMPL_INF_ANT").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiBera(getParametroFacade().obterAghParametroPorNome("P_PHI_BERA").getSeq());
		 tipoProcedHospitalarInternoVO.setPhiOto(getParametroFacade().obterAghParametroPorNome("P_PHI_OTOEMISSOES").getSeq());
		 
		 fatpCandidatoApacDescVO.setQtdeRep(getParametroFacade().obterAghParametroPorNome("P_OTO_QTDE_REP").getSeq());
		 fatpCandidatoApacDescVO.setCpeDtFim(getParametroFacade().obterAghParametroPorNome("P_DT_FIM_COMP_APT").getVlrData());
	}
	
	/** #42803
	 * ORADB ver_apac
	 * procedure recriada, agora para modificacao dos VOs.
	 * @throws ApplicationBusinessException 
	 */
	public void verApac(FatProcedAmbRealizado lista,TipoProcedHospitalarInternoVO tipoProcedHospitalarInternoVO, FatpCandidatoApacDescVO fatpCandidatoApacDescVO) throws ApplicationBusinessException{
		FatResumoApacs resumo = faturamentoFacade.buscaResumo(lista.getPaciente().getCodigo(), null);
		Boolean especialidadeOts = ambulatorioFacade.verificarCaracEspecialidade(lista.getEspecialidade(), DominioCaracEspecialidade.CAND_APAC_OTORRINO);
		List<Integer> listaPhi = new ArrayList<Integer>();
		listaPhi.add(tipoProcedHospitalarInternoVO.getPhiDiagMaior3anos());
		listaPhi.add(tipoProcedHospitalarInternoVO.getPhiDiagMenor3anos());
		listaPhi.add(tipoProcedHospitalarInternoVO.getPhiDiagAnt());
		
		List<Integer> listaPhi2 = new ArrayList<Integer>();
		listaPhi2.add(tipoProcedHospitalarInternoVO.getPhiAcomimplAdu());
		listaPhi2.add(tipoProcedHospitalarInternoVO.getPhiAcomimplInf());
		listaPhi2.add(tipoProcedHospitalarInternoVO.getPhiAcomimplAduAnt());
		listaPhi2.add(tipoProcedHospitalarInternoVO.getPhiAcomimplInfAnt());
		
		List<Integer> listaPhi3 = new ArrayList<Integer>();
		listaPhi3.add(tipoProcedHospitalarInternoVO.getPhiSelecaoAmbos());
		listaPhi3.add(tipoProcedHospitalarInternoVO.getPhiReaDiag());
		listaPhi3.add(tipoProcedHospitalarInternoVO.getPhiReaMenor3anos());
		listaPhi3.add(tipoProcedHospitalarInternoVO.getPhiReaMaior3anos());
		
		List<Integer> listaPhi4 = new ArrayList<Integer>();
		listaPhi4.add(tipoProcedHospitalarInternoVO.getPhiAdaptadoAdu());
		listaPhi4.add(tipoProcedHospitalarInternoVO.getPhiAdaptadoInf());
		listaPhi4.add(tipoProcedHospitalarInternoVO.getPhiAdpMenor3anos());
		listaPhi4.add(tipoProcedHospitalarInternoVO.getPhiAdpAduAnt());
		listaPhi4.add(tipoProcedHospitalarInternoVO.getPhiAdpInfAnt());
		
		final String implante = "IMPLANTE";
		final String adaptado = "ADAPTADO";

		if(resumo == null && especialidadeOts){
			fatpCandidatoApacDescVO.setCandidato("DIAGNOSTICO");
			tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiDiag());
			fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
		}else if(resumo != null){
			if(listaPhi.contains(resumo.getProcedimentoHospitalarInterno().getSeq()) 
					&& (resumo.getDtFinal().before(fatpCandidatoApacDescVO.getDtAnterior()))){
				fatpCandidatoApacDescVO.setCandidato("REAVALIACAO");
				tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiReaDiag());
				fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
			} else {
				if(resumo.getIndAparelho().equals(DominioSimNao.S)){
					if(fatpCandidatoApacDescVO.getCirurgia().equals('S') 
							&& (resumo.getDtFinal().before(fatpCandidatoApacDescVO.getDtCirg()) || 
									resumo.getDtFinal().equals(fatpCandidatoApacDescVO.getDtCirg()))){
						fatpCandidatoApacDescVO.setCandidato(implante);
						tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAcomimpl());
						
						if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
							fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
						}
					}else{
						fatpCandidatoApacDescVO.setCandidato(adaptado);
						tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAdaptado());
						if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
							fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
						}
					}
				}else if(listaPhi2.contains(resumo.getProcedimentoHospitalarInterno().getSeq())){
					fatpCandidatoApacDescVO.setCandidato(implante);
					tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAcomimpl());
					if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
						fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
					}
				}else if(listaPhi3.contains(resumo.getProcedimentoHospitalarInterno().getSeq())){
				    //não encontrou aparelho lançado -- fez cirurgia
					 if (fatpCandidatoApacDescVO.getTeveApacAparelho().equals('N') && fatpCandidatoApacDescVO.getCirurgia().equals('S')){
					        fatpCandidatoApacDescVO.setCandidato(implante);
					        tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAcomimpl());				        
					 
						 if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
							 fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
						 }
					 //encontrou aparelho lançado -- não fez cirurgia
					 }else if(fatpCandidatoApacDescVO.getTeveApacAparelho().equals('S') && fatpCandidatoApacDescVO.getCirurgia().equals('N')){
						fatpCandidatoApacDescVO.setCandidato(adaptado);
						tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAdaptado());
						if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
							fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
						}
					//encontrou aparelho lançado -- fez cirurgia
					}else if(fatpCandidatoApacDescVO.getTeveApacAparelho().equals('S') && fatpCandidatoApacDescVO.getCirurgia().equals('S')){
						if(fatpCandidatoApacDescVO.getDtFimApacAparelho().after(fatpCandidatoApacDescVO.getDtCirg()) || fatpCandidatoApacDescVO.getDtFimApacAparelho().equals(fatpCandidatoApacDescVO.getDtCirg()) ){
							fatpCandidatoApacDescVO.setCandidato(adaptado);
							tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAdaptado());						
							if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
								fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
							}
						}else{
							fatpCandidatoApacDescVO.setCandidato(implante);
							tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAcomimpl());
							if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
								fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
							}
						}
					}
				}else if(listaPhi4.contains(resumo.getProcedimentoHospitalarInterno().getSeq())){
					//agosto/2008 : Fez cirurgia posterior a última apac de acompanhamento adaptado. Muda para acomp. implante
					if( fatpCandidatoApacDescVO.getCirurgia().equals('S') && (resumo.getDtFinal().before(fatpCandidatoApacDescVO.getDtCirg()) || resumo.getDtFinal().equals(fatpCandidatoApacDescVO.getDtCirg()) )){
						fatpCandidatoApacDescVO.setCandidato(implante);
						tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAcomimpl());
						if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
							fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
						}
					}else{
						fatpCandidatoApacDescVO.setCandidato(adaptado);
						tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAdaptado());
						if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
							fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
						}
					}
				}else{
					//não encontrou aparelho lançado -- fez cirugia
					if(fatpCandidatoApacDescVO.getTeveApacAparelho().equals('N') && fatpCandidatoApacDescVO.getCirurgia().equals('S')){
				        fatpCandidatoApacDescVO.setCandidato(implante);
				        tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAcomimpl());
						if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
							fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
						}
					//encontrou aparelho lançado -- não fez cirurgia
					}else if(fatpCandidatoApacDescVO.getTeveApacAparelho().equals('S') && fatpCandidatoApacDescVO.getCirurgia().equals('N')){
				        fatpCandidatoApacDescVO.setCandidato(adaptado);
				        tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAdaptado());
						if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
							fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
						}
					//encontrou aparelho lançado -- fez cirurgia
					}else if(fatpCandidatoApacDescVO.getTeveApacAparelho().equals('S') && fatpCandidatoApacDescVO.getCirurgia().equals('S')){
				        if(fatpCandidatoApacDescVO.getDtFimApacAparelho().after(fatpCandidatoApacDescVO.getDtCirg()) || fatpCandidatoApacDescVO.getDtFimApacAparelho().equals(fatpCandidatoApacDescVO.getDtCirg())){
				        	fatpCandidatoApacDescVO.setCandidato(adaptado);
				        	tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAdaptado());
							if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
								fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
							}
				        }
					}else{
						fatpCandidatoApacDescVO.setCandidato(implante);
						tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAcomimpl());
						if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
							fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
						}
					}
				}
			}
		}
	}
	
	/** #42803
	 *  ORADB ver_apac_terapia
	 * @throws ApplicationBusinessException 
	 */
	public void verApacTerapia(TipoProcedHospitalarInternoVO tipoProcedHospitalarInternoVO, FatpCandidatoApacDescVO fatpCandidatoApacDescVO,FatProcedAmbRealizado lista) throws ApplicationBusinessException{
		//c_busca_resumo
		FatResumoApacs resumo = faturamentoFacade.buscaResumo(lista.getPaciente().getCodigo(), null);
		List<Integer> listaPhi = new ArrayList<Integer>();
		listaPhi.add(tipoProcedHospitalarInternoVO.getPhiTerapiaAdu());
		listaPhi.add(tipoProcedHospitalarInternoVO.getPhiTerapiaInf());
		if(resumo != null){
			if(verResumoApac(lista, resumo.getProcedimentoHospitalarInterno().getSeq(), tipoProcedHospitalarInternoVO, fatpCandidatoApacDescVO).equals('N')){
				if(listaPhi.contains(resumo.getProcedimentoHospitalarInterno().getSeq())){
					if(fatpCandidatoApacDescVO.getCirurgia().equals('S')){
						fatpCandidatoApacDescVO.setCandidato("IMPLANTE");
						tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAcomimpl());
						if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
							//INSERE_CANDIDATO
							inserirCandidato(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO, "N");
						}
					}
				}
			}
		}
	}
	
	/** #42803
	 * ORADB insere_candidato
	 * @throws ApplicationBusinessException 
	 */
	
	public void inserirCandidato(FatpCandidatoApacDescVO fatpCandidatoApacDescVO, FatProcedAmbRealizado lista, TipoProcedHospitalarInternoVO tipoProcedHospitalarInternoVO, String reposicao) throws ApplicationBusinessException{
		
		FatCandidatosApacOtorrino listaCandidato = faturamentoFacade.buscarCandidato(lista.getPaciente().getCodigo(), fatpCandidatoApacDescVO.getCandidato());
		FatCandidatosApacOtorrino listaCandidatoCao = faturamentoFacade.buscarCandidatoSemReavaliacao(lista.getPaciente().getCodigo());
		
		if( (listaCandidato == null && !fatpCandidatoApacDescVO.getCandidato().equals("REAVALIACAO") )
				|| fatpCandidatoApacDescVO.getCandidato().equals("APARELHO") || (fatpCandidatoApacDescVO.getCandidato().equals("REAVALIACAO") && listaCandidatoCao.getSeq() == null && listaCandidato == null)){
			if(fatpCandidatoApacDescVO.getCandidato().equals("ADAPTADO") && listaCandidatoCao.getCandidato().equals("APARELHO")){
				fatpCandidatoApacDescVO.setIncluiu(Boolean.FALSE);
			}else{
				RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
				FatCandidatosApacOtorrino fatCandidatosApacOtorrino = new FatCandidatosApacOtorrino();
				fatCandidatosApacOtorrino.setOrigem("PMR");
				fatCandidatosApacOtorrino.setDtEvento(lista.getDthrRealizado());
				fatCandidatosApacOtorrino.setIndSituacao(DominioSituacao.A);
				fatCandidatosApacOtorrino.setProcedimentoHospitalarInterno(lista.getProcedimentoHospitalarInterno());
				fatCandidatosApacOtorrino.setProcedimentoAmbRealizado(lista);
				fatCandidatosApacOtorrino.setEqpSeq(lista.getEspecialidade().getSeq());
				fatCandidatosApacOtorrino.setPacCodigo(lista.getPaciente().getCodigo());
				fatCandidatosApacOtorrino.setCandidato(fatpCandidatoApacDescVO.getCandidato());
				fatCandidatosApacOtorrino.setCriadoEm(new Date());				
				fatCandidatosApacOtorrino.setIndReposicao(reposicao);
				fatCandidatosApacOtorrino.setProcedimentoHospitalarSugerido(faturamentoFacade.obterProcedimentoHospitalarInterno(tipoProcedHospitalarInternoVO.getPhiSugerido()));
				fatCandidatosApacOtorrino.setSerMatricula(servidorLogado.getId().getMatricula());
				fatCandidatosApacOtorrino.setSerMatriculaIncluido(servidorLogado.getId().getMatricula());
				fatCandidatosApacOtorrino.setSerVinCodigoIncluido(servidorLogado.getId().getVinCodigo());
				
				if(lista.getCid()!=null){
					fatCandidatosApacOtorrino.setCidSeq(lista.getCid().getSeq());
				}
				
				try{
					faturamentoFacade.persistirFatCandidatosApacOtorrino(fatCandidatosApacOtorrino);
					fatpCandidatoApacDescVO.setIncluiu(Boolean.TRUE);
				}catch(Exception e){
					FaturamentoExceptionCode.FAT_00895.throwException();
				}
			}
		}
	}
	
	/** #42803
	 *  ORADB ver_resumo
	 * @throws ApplicationBusinessException 
	 */
	public Character verResumoApac(FatProcedAmbRealizado lista, Integer phiSeq,TipoProcedHospitalarInternoVO tipoProcedHospitalarInternoVO, FatpCandidatoApacDescVO fatpCandidatoApacDescVO) throws ApplicationBusinessException{
		List<Integer> listaPhi = new ArrayList<Integer>();
		listaPhi.add(tipoProcedHospitalarInternoVO.getPhiAcomimplAdu());
		listaPhi.add(tipoProcedHospitalarInternoVO.getPhiAcomimplInf());
		listaPhi.add(tipoProcedHospitalarInternoVO.getPhiAcomimplInfAnt());
		listaPhi.add(tipoProcedHospitalarInternoVO.getPhiAcomimplAduAnt());
		
		List<Integer> listaPhi2 = new ArrayList<Integer>();
		listaPhi2.add(tipoProcedHospitalarInternoVO.getPhiAdaptadoAdu());
		listaPhi2.add(tipoProcedHospitalarInternoVO.getPhiAdaptadoInf());
		listaPhi2.add(tipoProcedHospitalarInternoVO.getPhiAdpMenor3anos());
		listaPhi2.add(tipoProcedHospitalarInternoVO.getPhiAdpInfAnt());
		listaPhi2.add(tipoProcedHospitalarInternoVO.getPhiAdpAduAnt());
		
		if(listaPhi.contains(phiSeq)){
			fatpCandidatoApacDescVO.setCandidato("IMPLANTE");
			tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAcomimpl());
		}else if(listaPhi2.contains(phiSeq)){
			fatpCandidatoApacDescVO.setCandidato("ADAPTADO");
			tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiAdaptado());

		}else{
			return 'N';
		}
		
		if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
			fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
			return 'S';
		}else{
			return 'A';
		}
	}
	
	/**
	 * ver_apac_selecao
	 * @throws ApplicationBusinessException 
	 */
	
	public void verApacSelecao(FatProcedAmbRealizado lista,FatpCandidatoApacDescVO fatpCandidatoApacDescVO, TipoProcedHospitalarInternoVO tipoProcedHospitalarInternoVO) throws ApplicationBusinessException{
		 if(faturamentoFacade.buscaResumo(lista.getPaciente().getCodigo(), null) == null){
			 fatpCandidatoApacDescVO.setCandidato("DIAGNOSTICO");
			 tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiDiag());
			 fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
			 fatpCandidatoApacDescVO.setCandidato("SELECAO");
			 tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiSelecao());
			 fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
		 }else{			
			//busca_qtde_apac
			 if(buscaQuantidadeApac(fatpCandidatoApacDescVO, lista, tipoProcedHospitalarInternoVO).equals('S')){
				 fatpCandidatoApacDescVO.setCandidato("SELECAO");
				 tipoProcedHospitalarInternoVO.setPhiSugerido(tipoProcedHospitalarInternoVO.getPhiSelecao());
				 fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());
			 }			 	
		 }
	}
	
	/** #42803
	 *	ORADB busca_qtde_apac
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public Character buscaQuantidadeApac(FatpCandidatoApacDescVO fatpCandidatoApacDescVO,FatProcedAmbRealizado lista, TipoProcedHospitalarInternoVO tipoProcedHospitalarInternoVO) throws ApplicationBusinessException{
		List<Integer> listaDecode = new ArrayList<Integer>();
		Integer phiSelecao = tipoProcedHospitalarInternoVO.getPhiSugerido();
		listaDecode.add(phiSelecao);
		
		if(phiSelecao.equals(tipoProcedHospitalarInternoVO.getPhiDiag())){
			listaDecode.add(tipoProcedHospitalarInternoVO.getPhiDiagAnt());
		}else if(phiSelecao.equals(tipoProcedHospitalarInternoVO.getPhiAdaptadoAdu())){
			listaDecode.add(tipoProcedHospitalarInternoVO.getPhiAdaptadoAduAnt());
		}else if(phiSelecao.equals(tipoProcedHospitalarInternoVO.getPhiAdaptadoInf())){
		 listaDecode.add(tipoProcedHospitalarInternoVO.getPhiAdaptadoInfAnt());
		}else if(phiSelecao.equals(tipoProcedHospitalarInternoVO.getPhiAcomimplAdu())){
			 listaDecode.add(tipoProcedHospitalarInternoVO.getPhiAcomimplAduAnt());
		}else if(phiSelecao.equals(tipoProcedHospitalarInternoVO.getPhiAcomimplInf())){
			 listaDecode.add(tipoProcedHospitalarInternoVO.getPhiAcomimplInfAnt());
		}			
		
		if(phiSelecao.equals(tipoProcedHospitalarInternoVO.getPhiAcomimplAdu())){
			 listaDecode.add(tipoProcedHospitalarInternoVO.getPhiAcomimplInf());
		}else if(phiSelecao.equals(tipoProcedHospitalarInternoVO.getPhiAcomimplInf())){
			 listaDecode.add(tipoProcedHospitalarInternoVO.getPhiAcomimplAdu());
		}
		
		List<FatResumoApacs> fatResumoApacs = fatResumoApacsDAO.obterQuantidadeApac(lista.getPaciente().getCodigo(),listaDecode);
		
		for(FatResumoApacs apacs : fatResumoApacs){
			if( DateUtil.difMeses(lista.getDthrRealizado(), apacs.getDtFinal()) >= 12){
				fatResumoApacs.remove(apacs);
			}
		}
		Integer qtdApacs = fatResumoApacs.size();
		
		//fatc_ver_caract_phi
		CaracteristicaPhiVO caracteristicaPhiVO = faturamentoFacade.fatcVerCaractPhi(lista.getConvenioSaudePlano().getId().getCnvCodigo(), lista.getConvenioSaudePlano().getId().getSeq(), phiSelecao, "Quantidade Apacs Ano");
		fatpCandidatoApacDescVO.setQtdeApac(qtdApacs);
		fatpCandidatoApacDescVO.setQtdeApacAno(caracteristicaPhiVO.getValorNumerico());
		
		if(caracteristicaPhiVO.getResultado()){
			fatpCandidatoApacDescVO.setExisteCaract('S');
		}else{
			fatpCandidatoApacDescVO.setExisteCaract('N');
		}
		
		if(fatpCandidatoApacDescVO.getExisteCaract().equals('S') && 
				fatpCandidatoApacDescVO.getQtdeApac() < fatpCandidatoApacDescVO.getQtdeApacAno()){
			return 'S';
		}else{
			return 'N';
		}
	}
	
	/** #42803
	 * ORADB: ver_apac_aparelho
	 */
	public void verApacAparelho(FatProcedAmbRealizado itemLista, FatpCandidatoApacDescVO fatpCandidatoApacDescVO, TipoProcedHospitalarInternoVO tipoProcedHospitalarInternoVO){
		Long quantidadeAparelho = fatResumoApacsDAO.buscarQuantidadeAparelho(itemLista.getPaciente().getCodigo(), itemLista.getDthrRealizado());
		if(quantidadeAparelho == null){
			quantidadeAparelho = (long) 0 ;
		}
		fatpCandidatoApacDescVO.setQuantidade(quantidadeAparelho);
		fatpCandidatoApacDescVO.setIndRep('N');
		
		if(fatpCandidatoApacDescVO.getQuantidade() >= fatpCandidatoApacDescVO.getQtdeRep()){
			fatpCandidatoApacDescVO.setIndRep('S');
		}
		
		fatpCandidatoApacDescVO.setCandidato("APARELHO");
		tipoProcedHospitalarInternoVO.setPhiSugerido(itemLista.getPaciente().getCodigo());
		fatpCandidatoApacDescVO.setRetorno(fatpCandidatoApacDescVO.getCandidato());

	}
	
	//50569
	public boolean isPacienteTransplantado(AipPacientes paciente){
		if(paciente != null && paciente.getCodigo() != null){
			Date dataUltimoTransplante = faturamentoFacade.obterDataUltimoTransplante(paciente.getCodigo());
			if (dataUltimoTransplante != null) {
				// ATENCÃO. Paciente é transplantado!
				return true;
			}
		}
		return false;
	}
	
	/**
	 * #50635
	 * @throws BaseException, ApplicationBusinessException 
	 * @ORADB Procedure AGH.FAT_DESFAZ_REINTERNACAO
	 */
	public void desfazerReintegracao(Integer prontuario, String nomeMicrocomputador) throws BaseException, ApplicationBusinessException{
			//RN01 - C1 -  Busca a última internação do paciente
			Integer seqInternacao; //c_busca_int  
			seqInternacao = getAghuFacade().obterAghAtendimentoPorProntuario(prontuario);
			
			//RN01 - C2 - Busca contas do paciente
			List<FatContasInternacao> listaContasInternacao = new ArrayList<FatContasInternacao>(); //c_conta_int 
			listaContasInternacao = fatContasInternacaoDAO.obterContaHospitalarePorInternacaoSemFiltro(seqInternacao);
			
			//RN01 - Código Procedure
			Integer vContaAntiga = 0;
			Integer vContaNova = 0;      //Seq da Conta Hospitalar
//			boolean vOk;
			Date vDataAlta = new Date();
			FatContasHospitalares fatContasHospitalaresTemp;
			
			
			for (FatContasInternacao fatContasInternacao : listaContasInternacao) {
				fatContasHospitalaresTemp = fatContasHospitalaresDAO.obterFatContaHospitalares(fatContasInternacao); //C3
				if(fatContasHospitalaresTemp.getIndSituacao().equals(DominioSituacaoConta.E)){
//					Se a conta estiver encerrada, reabrir conta
					this.logar("'Reabri conta encerrada: '" + fatContasHospitalaresTemp.getSeq() +  "'r_conta.ind_situacao: '" + fatContasHospitalaresTemp.getIndSituacao());
//					vOk = fatkCthRN.rnCthcAtuReabre(fatContasHospitalaresTemp.getSeq(), nomeMicrocomputador, new Date(), null);
					fatkCthRN.rnCthcAtuReabre(fatContasHospitalaresTemp.getSeq(), nomeMicrocomputador, new Date(), null);
					fatContasHospitalaresTemp = fatContasHospitalaresDAO.obterFatContaHospitalares(fatContasInternacao); //C3
				}else if(fatContasHospitalaresTemp.getIndSituacao().equals(DominioSituacaoConta.C)){
					vContaNova = fatContasHospitalaresTemp.getSeq();
					// coloca a data fim, caso o paciente já tenha recebido alta
					vDataAlta = ainInternacaoDAO.obterDtAltaMedicaInternacaoPorSeqInternacao(seqInternacao);
					
//					Abre a conta nova - r_conta.seq
					fatContasHospitalaresTemp.setIndSituacao(DominioSituacaoConta.A);
					fatContasHospitalaresDAO.atualizar(fatContasHospitalaresTemp);
					
					fatContasInternacaoDAO.removerPorSeqInternacao(seqInternacao, fatContasHospitalaresTemp.getSeq().intValue());
					
				}else if(fatContasHospitalaresTemp.getIndSituacao().equals(DominioSituacaoConta.A)){
					vContaAntiga = fatContasHospitalaresTemp.getSeq();
					vDataAlta = ainInternacaoDAO.obterDtAltaMedicaInternacao(seqInternacao, fatContasHospitalaresTemp.getSeq());
					
					fatContasHospitalaresTemp.setIndSituacao(DominioSituacaoConta.F);
					
					fatContasHospitalaresTemp.setDtAltaAdministrativa(vDataAlta); 
					fatContasHospitalaresDAO.atualizar(fatContasHospitalaresTemp);
				}
			}
			
			fatpInsereItens(vContaAntiga, vContaNova, vDataAlta);
	}

	/**
	 * ORADB Procedure AGH.FATP_INSERE_ITENS
	 */
	private void fatpInsereItens(Integer vContaAntiga, Integer vContaNova,Date vDataAlta)  throws ApplicationBusinessException{
		
		Short vIchSeqp = 0;        //SeqP da FatItemContaHospitalar
		List<FatItemContaHospitalar> listaFatItemContaHospitalar;
		
		
		try {
			//50635 - C4 - C_BUSCA_ICH 
			listaFatItemContaHospitalar = fatItemContaHospitalarDAO.obterContaHospitalarPorCthSeqEData(vContaAntiga, vDataAlta);
			for (FatItemContaHospitalar fatItemContaHospitalar : listaFatItemContaHospitalar) { //C_BUSCA_ICH
				Boolean contaNovaItensExistente = Boolean.FALSE;
				
				//C5 - Verificação para não duplicar itens
				contaNovaItensExistente = fatItemContaHospitalarDAO.isContaNovaItensExistente(vContaNova, fatItemContaHospitalar);
				
				if(!contaNovaItensExistente){
					FatItemContaHospitalarId fatItemContaHospitalarIdNovo = new FatItemContaHospitalarId();
					FatItemContaHospitalar fatItemContaHospitalarNovo = new FatItemContaHospitalar();
					vIchSeqp = fatItemContaHospitalarDAO.buscaMaxSeqMaisUm(vContaNova);  //C_PROX_ITEM_CONTA - C5
					
					fatItemContaHospitalarIdNovo.setCthSeq(vContaNova);
					fatItemContaHospitalarIdNovo.setSeq(vIchSeqp);
					fatItemContaHospitalarNovo.setId(fatItemContaHospitalarIdNovo);
					fatItemContaHospitalarNovo.setIchType(DominioItemConsultoriaSumarios.ICH);
					fatItemContaHospitalarNovo.setProcedimentoHospitalarInterno(fatItemContaHospitalar.getProcedimentoHospitalarInterno());
					fatItemContaHospitalarNovo.setValor(BigDecimal.valueOf(0));
					fatItemContaHospitalarNovo.setIndSituacao(DominioSituacaoItenConta.A);
					fatItemContaHospitalarNovo.setQuantidadeRealizada(fatItemContaHospitalar.getQuantidadeRealizada());
					fatItemContaHospitalarNovo.setIndOrigem(fatItemContaHospitalar.getIndOrigem());
					fatItemContaHospitalarNovo.setLocalCobranca(DominioLocalCobranca.I);
					fatItemContaHospitalarNovo.setDthrRealizado(fatItemContaHospitalar.getDthrRealizado());
					fatItemContaHospitalarNovo.setUnidadesFuncional(fatItemContaHospitalar.getUnidadesFuncional());
					fatItemContaHospitalarNovo.setIseSoeSeq(fatItemContaHospitalar.getIseSoeSeq());
					fatItemContaHospitalarNovo.setIseSeqp(fatItemContaHospitalar.getIseSeqp());
					fatItemContaHospitalarNovo.setCriadoEm(fatItemContaHospitalar.getCriadoEm());
					fatItemContaHospitalarNovo.setCriadoPor(fatItemContaHospitalar.getCriadoPor());
					
					fatItemContaHospitalarDAO.persistir(fatItemContaHospitalarNovo);
				}
			}
			
			
			List<FatItemContaHospitalar> listaFatItemContaHospitalarRemover = fatItemContaHospitalarDAO.obterContaHospitalarPorCthSeqEData(vContaAntiga, vDataAlta);
			if(listaFatItemContaHospitalarRemover != null && !listaFatItemContaHospitalarRemover.isEmpty()){
				for (FatItemContaHospitalar fatItemContaHospitalar : listaFatItemContaHospitalarRemover) {
					fatItemContaHospitalarDAO.remover(fatItemContaHospitalar);
				}
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(FaturamentoRNExceptionCode.FATP_INSERE_ITENS);
		}
		
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	protected FatEspelhoProcedAmbDAO getFatEspelhoProcedAmbDAO() {
		return fatEspelhoProcedAmbDAO;
	}
	protected FatArqEspelhoProcedAmbDAO getFatArqEspelhoProcedAmbDAO() {
		return fatArqEspelhoProcedAmbDAO;
	}
	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}
	protected ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON() {
		return procedimentosAmbRealizadosON;
	}	
	protected FaturamentoFatkPmrRN getFaturamentoFatkPmrRN() {
		return faturamentoFatkPmrRN;
	}	
	protected FatcBuscaServClassRN getFatcBuscaServClassRN() {
		return fatcBuscaServClassRN;
	}
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}		
	public FatAtendimentoApacProcHospDAO getFatAtendimentoApacProcHospDAO() {
		return fatAtendimentoApacProcHospDAO;
	}

	public FatTipoTratamentosDAO getFatTipoTratamentosDAO() {
		return fatTipoTratamentosDAO;
	}
	
}