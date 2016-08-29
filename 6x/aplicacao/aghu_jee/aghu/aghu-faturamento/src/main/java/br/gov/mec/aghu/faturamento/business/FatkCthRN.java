package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioAtualizacaoSaldo;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioLocalSoma;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimento;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioTipoItemEspelhoContaHospitalar;
import br.gov.mec.aghu.dominio.DominioUtilizacaoItem;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCampoMedicoAuditAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCidContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatExclusaoCriticaDAO;
import br.gov.mec.aghu.faturamento.dao.FatGrupoProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemGrupoProcedHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatMensagemLogDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobramentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoRejeicaoContaDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAihDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContaHospitalarPacDAO;
import br.gov.mec.aghu.faturamento.vo.BuscaTotalHemoterapiasQueExigemLancamentoModuloTransfusionalVO;
import br.gov.mec.aghu.faturamento.vo.BuscarProcedHospEquivalentePhiVO;
import br.gov.mec.aghu.faturamento.vo.FatCidContaHospitalarVO;
import br.gov.mec.aghu.faturamento.vo.FatContasInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.FatVariaveisVO;
import br.gov.mec.aghu.faturamento.vo.ItemParaTrocaConvenioVO;
import br.gov.mec.aghu.faturamento.vo.PacienteContaHospitalarVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcAtuPermaiorVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcAtuRegrasVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcAtuReintVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerDatasVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerItemSusVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerPacCtaVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerRegraespVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerUtimesesVO;
import br.gov.mec.aghu.faturamento.vo.RnFatcVerItprocexcVO;
import br.gov.mec.aghu.faturamento.vo.RnSutcAtuSaldoVO;
import br.gov.mec.aghu.internacao.vo.FatItemContaHospitalarVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatCidContaHospitalarId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoAihId;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;
import br.gov.mec.aghu.model.FatEspelhoItemContaHospId;
import br.gov.mec.aghu.model.FatExclusaoCritica;
import br.gov.mec.aghu.model.FatGrupoProcedHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatItemGrupoProcedHosp;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatMotivoDesdobramento;
import br.gov.mec.aghu.model.FatPerdaItemConta;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatTipoAih;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.prescricaomedica.vo.DadosRegistroCivilVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Refere-se a package FATK_CTHN_RN_UN
 */
@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.NcssTypeCount",
		"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength","PMD.HierarquiaONRNIncorreta" })
@Stateless
public class FatkCthRN extends AbstractFatDebugLogEnableRN {
	
	private static final String RN_CTHC_ATU_REGRAPOS = "RN_CTHC_ATU_REGRAPOS";

	private static final String RN_CTHC_VER_DADPARTO = "RN_CTHC_VER_DADPARTO";

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final String RN_CTHC_ATU_REGRAS = "RN_CTHC_ATU_REGRAS";

	private static final String MSG_EXCECAO_ENGOLIDA = "A seguinte exceção deve ser engolida, seguindo a lógica migrada do PL/SQL: ";

	private static final String RN_CTHC_VER_SSMDIFER = "RN_CTHC_VER_SSMDIFER";

	private static final String INT = "INT";

	private static final String MSG_EXCECAO_IGNORADA = "A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: ";

	private static final String RN_CTHC_VER_REGRAESP = "RN_CTHC_VER_REGRAESP";

	@EJB
	private FatkCth4RN fatkCth4RN;
	
	@EJB
	private FatkCth5RN fatkCth5RN;
	
	@EJB
	private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private static final Log LOG = LogFactory.getLog(FatkCthRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private CidContaHospitalarPersist cidContaHospitalarPersist;
	
	@EJB
	private FatContaInternacaoPersist fatContaInternacaoPersist;

	@Inject
	private FatMotivoRejeicaoContaDAO fatMotivoRejeicaoContaDAO;

	@Inject
	private FatMensagemLogDAO fatMensagemLogDAO;

	private static final long serialVersionUID = 4396356134458006557L;

	final static Integer INICIO_PORTARIA_203 = 201108;

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_SSMDIFER
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcVerSsmdifer(Date pDataPrevia, Integer pCthSeq,
			Integer pPacCodigo, Integer pPacProntuario, Integer pIntSeq,
			Integer pPhiSolic, Short pPhoSolic, Integer pIphSolic,
			Long pCodSusSolic, Integer pPhiRealiz, Short pPhoRealiz,
			Integer pIphRealiz, Long pCodSusRealiz) throws BaseException {

		// Verifica se o procedimento Solicitado e realizado sao diferentes
		if (!CoreUtil.igual(pCodSusRealiz, pCodSusSolic)) {
			FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();

			// Verifica se o procedimento solicitado permite mudanca de
			// procedimento
			FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO
					.obterPorChavePrimaria(new FatItensProcedHospitalarId(
							pPhoSolic, pIphSolic));
			if (fatItensProcedHospitalar != null
					&& CoreUtil.igual(Boolean.FALSE,
							fatItensProcedHospitalar.getSolicDifereReal())) {
				// Solicitado nao permite mudanca de procedimento
				try {
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(254);
					getFaturamentoON()
							.criarFatLogErrors(
									"SOLICITADO EXIGE QUE O REALIZADO SEJA IGUAL A ELE.",
									INT, pCthSeq, null, null, null, null,
									pDataPrevia, null, null, pPhoSolic,
									pPhoRealiz, pIphSolic, pIphRealiz,
									pPacCodigo, pPhiSolic, pPhiRealiz, null,
									pPacProntuario, pCodSusRealiz,
									pCodSusSolic, null, null, null, null, null,
									null, RN_CTHC_VER_SSMDIFER, null, null,fatMensagemLog);
				} catch (Exception e) {
					logar(MSG_EXCECAO_ENGOLIDA,
							e);
				}

				return false;
			}

			// Verifica se o procedimento realizado permite mudanca de
			// procedimento
			fatItensProcedHospitalar = fatItensProcedHospitalarDAO
					.obterPorChavePrimaria(new FatItensProcedHospitalarId(
							pPhoRealiz, pIphRealiz));
			if (fatItensProcedHospitalar != null
					&& CoreUtil.igual(Boolean.FALSE,
							fatItensProcedHospitalar.getRealDifereSolic())) {
				// Realizado nao permite mudanca de procedimento
				try {
					
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(238);
					getFaturamentoON()
							.criarFatLogErrors(
									"REALIZADO EXIGE QUE O SOLICITADO SEJA IGUAL A ELE.",
									INT, pCthSeq, null, null, null, null,
									pDataPrevia, null, null, pPhoSolic,
									pPhoRealiz, pIphSolic, pIphRealiz,
									pPacCodigo, pPhiSolic, pPhiRealiz, null,
									pPacProntuario, pCodSusRealiz,
									pCodSusSolic, null, null, null, null, null,
									null, RN_CTHC_VER_SSMDIFER, null, null,fatMensagemLog);
				} catch (Exception e) {
					logar(MSG_EXCECAO_ENGOLIDA,
							e);
				}

				return false;
			}
		}

		return true;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_CID_FIXO
	 * 
	 * Verifica se CID Primario esta entre 'S00' e 'T98' O CID secundario deve
	 * estar entre 'V01' e 'Y98'
	 * 
	 * @throws BaseException
	 */
	public Integer rnCthcVerCidFixo(Integer pCthSeq) throws BaseException {
		IAghuFacade aghuFacade = getAghuFacade();
		FatCidContaHospitalarDAO fatCidContaHospitalarDAO = getFatCidContaHospitalarDAO();

		// Busca CID inicial da faixa de CIDs p/os quais o carater de
		// internacao da conta deve ser buscado na internacao
		final String vCidPrimIni = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CID_INI_CARATER_INTERN);

		// Busca CID final da faixa de CIDs p/os quais o carater de
		// internacao da conta deve ser buscado na internacao
		final String vCidPrimFin = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CID_FIN_CARATER_INTERN);

		final String vCidSecundIni = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CID_SECUNDARIO_INICIAL);
		final String vCidSecundFin = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CID_SECUNDARIO_FINAL);

		Integer vCidSeqPrim = null;
		Integer vCidSeqSecund = null;
		String vCidPrim = null;
		String vCidSecund = null;

		// Busca CID primario cadastrado p/conta hospitalar
		vCidSeqPrim = fatCidContaHospitalarDAO.buscarPrimeiroCidSeq(pCthSeq,DominioPrioridadeCid.P);
		
		if (vCidSeqPrim != null) {

			// Busca codigo do cid primario
			AghCid cidPrimario = aghuFacade
					.obterAghCidsPorChavePrimaria(vCidSeqPrim);
			if (cidPrimario != null
					&& CoreUtil.igual(DominioSituacao.A,
							cidPrimario.getSituacao())) {
				vCidPrim = cidPrimario.getCodigo();
			}

		}

		// Busca CID secundario cadastrado p/conta hospitalar
		vCidSeqSecund = fatCidContaHospitalarDAO.buscarPrimeiroCidSeq(pCthSeq,DominioPrioridadeCid.S);
		if (vCidSeqSecund != null) {

			// Busca codigo do cid secundario
			String codigo = aghuFacade.buscaPrimeiroCidContaSituacao(vCidSeqSecund, DominioSituacao.A);
			if(codigo != null){
				vCidSecund = codigo;
			}
		}

		if (vCidPrim != null && vCidPrimIni != null && vCidPrimFin != null
				&& vCidPrim.compareToIgnoreCase(vCidPrimIni) >= 0
				&& vCidPrim.compareToIgnoreCase(vCidPrimFin) <= 0) {
			if (!(vCidSecund != null && vCidSecundIni != null
					&& vCidSecundFin != null
					&& vCidSecund.compareToIgnoreCase(vCidSecundIni) >= 0 && vCidSecund
					.compareToIgnoreCase(vCidSecundFin) <= 0)) {
				return 0;
			} else {
				return 1;
			}
		}

		return null;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_CIDSECUN
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcVerCidsecun(Integer pCthSeq, Long pCodSusRealiz)
			throws BaseException {
		IAghuFacade aghuFacade = this.getAghuFacade();
		FatCidContaHospitalarDAO fatCidContaHospitalarDAO = getFatCidContaHospitalarDAO();
		final Long codigoSusIntercorrenciaPacienteRenalCronicoPediatria = buscarVlrNumericoAghParametro(
				AghuParametrosEnum.P_COD_INTERCORRENCIA_PACIENTE_RENAL_CRONICO_PEDIATRIA)
				.longValue();
		final Long codigoSusIntercorrenciaPacienteRenalCronicoClinicaMed = buscarVlrNumericoAghParametro(
				AghuParametrosEnum.P_COD_INTERCORRENCIA_PACIENTE_RENAL_CRONICO_CLINICA_MED)
				.longValue();
		final String cidInsuficienciaRenalCronicaNaoEspecificada = buscarVlrTextoAghParametro(AghuParametrosEnum.P_CID_SECUNDARIO_INSUFICIENCIA_RENAL_CRONICA_NAO_ESPECIF);

		// Para os ssm realizados (codigos SUS):
		// "80300235" - INTERCORRENCIA PACIENTE RENAL CRONICO (PEDIATRIA)
		// "80500234" - INTERCORRENCIA PACIENTE RENAL CRONICO (CLINICA MED
		// o CID secundario deve ser:
		// "N18.9" - INSUFICIENCIA RENAL CRONICA NAO ESPECIFICADA
		if (pCodSusRealiz != null
				&& (CoreUtil.igual(pCodSusRealiz,
						codigoSusIntercorrenciaPacienteRenalCronicoPediatria) || CoreUtil
						.igual(pCodSusRealiz,
								codigoSusIntercorrenciaPacienteRenalCronicoClinicaMed))) {
			// Busca CID secundario cadastrado p/conta hospitalar
			Integer vSeqCidSecund = fatCidContaHospitalarDAO.buscarPrimeiroCidSeq(pCthSeq, DominioPrioridadeCid.S);

			if (vSeqCidSecund != null) {

				// Busca codigo do cid secundario
				AghCid aghCid = aghuFacade.obterAghCidsPorChavePrimaria(vSeqCidSecund);

				if (aghCid == null
						|| !aghCid.getCodigo().equalsIgnoreCase(cidInsuficienciaRenalCronicaNaoEspecificada)) {
					return false;
				}
			} else {
				return false;
			}
		}

		return true;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_CIDOBRIG
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcVerCidobrig(Integer pCthSeq, Short pTabProcRealiz,
			Integer pProcRealiz) throws BaseException {
		FatCidContaHospitalarDAO fatCidContaHospitalarDAO = getFatCidContaHospitalarDAO();
		FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();

		// Verifica se CID secundario e' obrigatorio
		Boolean vCidObrig = null;

		FatItensProcedHospitalar itemProcedimentoHospitalar = fatItensProcedHospitalarDAO
				.obterPorChavePrimaria(new FatItensProcedHospitalarId(
						pTabProcRealiz, pProcRealiz));
		if (itemProcedimentoHospitalar != null) {
			vCidObrig = itemProcedimentoHospitalar.getCidadeObrigatoria();
		}

		vCidObrig = vCidObrig != null ? vCidObrig : Boolean.FALSE;

		if (vCidObrig) {
			// Busca cid secundario da conta
			Long count = fatCidContaHospitalarDAO.buscaCountQtdCids(pCthSeq,
					DominioPrioridadeCid.S);

			if (count == null || CoreUtil.igual(count, 0)) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_DADPARTO
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public Boolean rnCthcVerDadparto(Date pDataPrevia, Integer pCthSeq,
			Integer pPacCodigo, Integer pPacProntuario, Integer pIntSeq,
			Integer pPhiSolic, Short pPhoSolic, Integer pIphSolic,
			Long pCodSusSolic, Integer pPhiRealiz, Short pPhoRealiz,
			Integer pIphRealiz, Long pCodSusRealiz) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IAghuFacade aghuFacade = this.getAghuFacade();
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();

		// Verifica se a conta hospitalar exige que seja preenchido dados parto
		// Marina 05/10/2012 - chamado 81312
		
		// antes de consistir os dados, verifica se é um parto com aborto, se sim, não deve consistir os dados.
		final DominioRNClassificacaoNascimento vAbo = aghuFacade.verificarAbo(pIntSeq);
		logar("v_abo: {0}", vAbo);
		
		if(vAbo != null && 
				!DominioRNClassificacaoNascimento.ABO.equals(vAbo) && 
					!DominioRNClassificacaoNascimento.NAM.equals(vAbo)){
			
			FatItensProcedHospitalar itemProcedHospitalar = fatItensProcedHospitalarDAO
					.obterPorChavePrimaria(new FatItensProcedHospitalarId(
							pPhoRealiz, pIphRealiz));
			
			Boolean vExigeDados = (itemProcedHospitalar != null && itemProcedHospitalar
					.getDadosParto() != null) ? itemProcedHospitalar
					.getDadosParto() : Boolean.FALSE;
			if (Boolean.TRUE.equals(vExigeDados)) { // precisa dos dados parto na conta
				
				// verifica se a conta hospitalar possui os dados de parto preenchidos
				Boolean vDadosPartoPreenchido = fatContasHospitalaresDAO
						.isDadosPartoPreenchidos(pCthSeq);
				if (!vDadosPartoPreenchido) { // dados parto nao informados
					try {
						FatLogError erro = new FatLogError();
						erro.setModulo(INT);
						erro.setPrograma(RN_CTHC_VER_DADPARTO);
						erro.setCriadoEm(new Date());
						erro.setCriadoPor(servidorLogado.getUsuario());
						erro.setDataPrevia(pDataPrevia);
						erro.setCthSeq(pCthSeq);
						erro.setPacCodigo(pPacCodigo);
						erro.setProntuario(pPacProntuario);
						erro.setPhiSeq(pPhiSolic);
						erro.setIphPhoSeq(pPhoSolic);
						erro.setIphSeq(pIphSolic);
						erro.setCodItemSusSolicitado(pCodSusSolic);
						erro.setPhiSeqRealizado(pPhiRealiz);
						erro.setIphPhoSeqRealizado(pPhoRealiz);
						erro.setIphSeqRealizado(pIphRealiz);
						erro.setCodItemSusRealizado(pCodSusRealiz);
						erro.setErro("REALIZADO EXIGE QUE OS DADOS DE PARTO SEJAM INFORMADOS.");
						
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(239);
						erro.setFatMensagemLog(fatMensagemLog);
						
						persistirLogErroCarregandoFatMensagemLog(faturamentoFacade, erro);
						//faturamentoFacade.evict(erro);
					} catch (Exception e) {
						logar(MSG_EXCECAO_IGNORADA,
								e);
					}
					return Boolean.FALSE;
				} else { // dados de parto foram informados
					FatContasHospitalares dadosParto = fatContasHospitalaresDAO
							.obterPorChavePrimaria(pCthSeq);
					int nascidos = (dadosParto.getRnNascidoVivo() != null ? dadosParto
							.getRnNascidoVivo() : 0);
					nascidos = nascidos
							+ (dadosParto.getRnNascidoMorto() != null ? dadosParto
									.getRnNascidoMorto() : 0);
					int saidas = (dadosParto.getRnSaidaAlta() != null ? dadosParto
							.getRnSaidaAlta() : 0);
					saidas = saidas
							+ (dadosParto.getRnSaidaTransferencia() != null ? dadosParto
									.getRnSaidaTransferencia() : 0);
					saidas = saidas
							+ (dadosParto.getRnSaidaObito() != null ? dadosParto
									.getRnSaidaObito() : 0);
					if (nascidos != saidas) {
						try {
							FatLogError erro = new FatLogError();
							erro.setModulo(INT);
							erro.setPrograma(RN_CTHC_VER_DADPARTO);
							erro.setCriadoEm(new Date());
							erro.setCriadoPor(servidorLogado.getUsuario());
							erro.setDataPrevia(pDataPrevia);
							erro.setCthSeq(pCthSeq);
							erro.setPacCodigo(pPacCodigo);
							erro.setProntuario(pPacProntuario);
							erro.setPhiSeq(pPhiSolic);
							erro.setIphPhoSeq(pPhoSolic);
							erro.setIphSeq(pIphSolic);
							erro.setCodItemSusSolicitado(pCodSusSolic);
							erro.setPhiSeqRealizado(pPhiRealiz);
							erro.setIphPhoSeqRealizado(pPhoRealiz);
							erro.setIphSeqRealizado(pIphRealiz);
							erro.setCodItemSusRealizado(pCodSusRealiz);
							erro.setErro("QTD NASCIDOS VIVOS/MORTOS DEVE SER IGUAL A QTD SAIDAS ALTA/TRANSF/OBITO.");
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(227);
							erro.setFatMensagemLog(fatMensagemLog);
							persistirLogErroCarregandoFatMensagemLog(
									faturamentoFacade, erro);
						} catch (Exception e) {
							logar(MSG_EXCECAO_IGNORADA,
									e);
						}
						return Boolean.FALSE;
					}
				}
				
				// Marina 27/09/2012
				final DadosRegistroCivilVO dadosRegistroCivilVO = aghuFacade.obterDadosRegistroCivil(pIntSeq);
				
				// Marina 28/06/2011  - Chamado 50025
				logar("v_numero_dn: {0}", dadosRegistroCivilVO != null ? dadosRegistroCivilVO.getNumeroDn() : null);
				
				if(dadosRegistroCivilVO == null || dadosRegistroCivilVO.getNumeroDn() == null){
					
					try {
						FatLogError erro = new FatLogError();
						erro.setModulo(INT);
						erro.setPrograma(RN_CTHC_VER_DADPARTO);
						erro.setCriadoEm(new Date());
						erro.setCriadoPor(servidorLogado.getUsuario());
						erro.setDataPrevia(pDataPrevia);
						erro.setCthSeq(pCthSeq);
						erro.setPacCodigo(pPacCodigo);
						erro.setProntuario(pPacProntuario);
						erro.setPhiSeq(pPhiSolic);
						erro.setIphPhoSeq(pPhoSolic);
						erro.setIphSeq(pIphSolic);
						erro.setCodItemSusSolicitado(pCodSusSolic);
						erro.setPhiSeqRealizado(pPhiRealiz);
						erro.setIphPhoSeqRealizado(pPhoRealiz);
						erro.setIphSeqRealizado(pIphRealiz);
						erro.setCodItemSusRealizado(pCodSusRealiz);
						erro.setErro("NUMERO DA DECLARACAO DE NASCIDO VIVO NÃO INFORMADA");
						
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(192);
						erro.setFatMensagemLog(fatMensagemLog);
						
						persistirLogErroCarregandoFatMensagemLog(
								faturamentoFacade, erro);
					} catch (Exception e) {
						logar(MSG_EXCECAO_IGNORADA,e);
					}
					
					return Boolean.FALSE;
					
				}
				
				// Marina 28/06/2011  - Chamado 50025
				logar("v_reg_nasc: {0}", dadosRegistroCivilVO.getRegNascimento());
				
				final Integer paramRegCivilNasc = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_PHI_INCENTIVO_REGISTRO_CIVIL_NASCIMENTO);
				final List<FatItemContaHospitalar> rExigeRegCivil = fatItemContaHospitalarDAO.listarPorCthPhi( pCthSeq, 
																											   paramRegCivilNasc, 
																											   DominioSituacaoItenConta.C);
				
				if(!rExigeRegCivil.isEmpty() && dadosRegistroCivilVO.getRegNascimento() == null){
					try {
						FatLogError erro = new FatLogError();
						erro.setModulo(INT);
						erro.setPrograma(RN_CTHC_VER_DADPARTO);
						erro.setCriadoEm(new Date());
						erro.setCriadoPor(servidorLogado.getUsuario());
						erro.setDataPrevia(pDataPrevia);
						erro.setCthSeq(pCthSeq);
						erro.setPacCodigo(pPacCodigo);
						erro.setProntuario(pPacProntuario);
						erro.setPhiSeq(pPhiSolic);
						erro.setIphPhoSeq(pPhoSolic);
						erro.setIphSeq(pIphSolic);
						erro.setCodItemSusSolicitado(pCodSusSolic);
						erro.setPhiSeqRealizado(pPhiRealiz);
						erro.setIphPhoSeqRealizado(pPhoRealiz);
						erro.setIphSeqRealizado(pIphRealiz);
						erro.setCodItemSusRealizado(pCodSusRealiz);
						erro.setErro("INCENTIVO CIVIL SEM A INFORMAÇÃO DO REGISTRO DE NASCIMENTO");
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(99);
						erro.setFatMensagemLog(fatMensagemLog);
						persistirLogErroCarregandoFatMensagemLog(
								faturamentoFacade, erro);
					} catch (Exception e) {
						logar(MSG_EXCECAO_IGNORADA,e);
					}
					
					return Boolean.FALSE;
				}
	
				/*
				 eSchweigert --> #20681  --> Comentado em 06/11/2012
				 
				// Marina - 24/10/2008
				// Verifica se todos os dados do registro civil foram informados
				final Integer paramRegCivilNasc = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_PHI_INCENTIVO_REGISTRO_CIVIL_NASCIMENTO);
				List<FatItemContaHospitalar> rExigeRegCivil = fatItemContaHospitalarDAO
						.listarPorCthPhi(pCthSeq, paramRegCivilNasc);
	
				if (rExigeRegCivil != null && !rExigeRegCivil.isEmpty()) {
	
					AinInternacao internacao = getInternacaoFacade()
							.obterAinInternacaoPorChavePrimaria(pIntSeq);
	
					// TODO: Tarefa #14529 - Troca do uso do atributo McoGestacoes
					// por gsoPacCodigo e obtenção dos dados DNS
					// do paciente ao invés da gestação
					if (internacao == null
							|| internacao.getAtendimento() == null
							|| internacao.getAtendimento().getGsoPacCodigo() == null
							|| internacao.getAtendimento().getPaciente()
									.getAipPacientesDadosCns() == null
							|| internacao.getAtendimento().getPaciente()
									.getAipPacientesDadosCns().getNumeroDn() == null) {
						try {
							FatLogError erro = new FatLogError();
							erro.setModulo("INT");
							erro.setPrograma("RN_CTHC_VER_DADPARTO");
							erro.setCriadoEm(new Date());
							erro.setCriadoPor(servidorLogado.getUsuario());
							erro.setDataPrevia(pDataPrevia);
							erro.setCthSeq(pCthSeq);
							erro.setPacCodigo(pPacCodigo);
							erro.setProntuario(pPacProntuario);
							erro.setPhiSeq(pPhiSolic);
							erro.setIphPhoSeq(pPhoSolic);
							erro.setIphSeq(pIphSolic);
							erro.setCodItemSusSolicitado(pCodSusSolic);
							erro.setPhiSeqRealizado(pPhiRealiz);
							erro.setIphPhoSeqRealizado(pPhoRealiz);
							erro.setIphSeqRealizado(pIphRealiz);
							erro.setCodItemSusRealizado(pCodSusRealiz);
							erro.setErro("DADOS DO REGISTRO CIVIL INCLOMPLETOS.");
							faturamentoFacade.persistirLogError(erro);
							faturamentoFacade.evict(erro);
						} catch (Exception e) {
							logar("A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: ",
									e);
						}
						return Boolean.FALSE;
					}
				}
	
				// -- Marina 28/06/2011 - Chamado 50025
				final AipPacientes pac = getAghuFacade()
						.obterAipPacienteRecemNascido(pIntSeq, paramRegCivilNasc);
				logar("v_reg_nasc: {0}", (pac != null ? pac.getRegNascimento()
						: null));
				if (pac == null || pac.getRegNascimento() == null) {
					try {
						FatLogError erro = new FatLogError();
						erro.setModulo("INT");
						erro.setPrograma("RN_CTHC_VER_DADPARTO");
						erro.setCriadoEm(new Date());
						erro.setCriadoPor(servidorLogado.getUsuario());
						erro.setDataPrevia(pDataPrevia);
						erro.setCthSeq(pCthSeq);
						erro.setPacCodigo(pPacCodigo);
						erro.setProntuario(pPacProntuario);
						erro.setPhiSeq(pPhiSolic);
						erro.setIphPhoSeq(pPhoSolic);
						erro.setIphSeq(pIphSolic);
						erro.setCodItemSusSolicitado(pCodSusSolic);
						erro.setPhiSeqRealizado(pPhiRealiz);
						erro.setIphPhoSeqRealizado(pPhoRealiz);
						erro.setIphSeqRealizado(pIphRealiz);
						erro.setCodItemSusRealizado(pCodSusRealiz);
						erro.setErro("INCENTIVO CIVIL SEM A INFORMAÇÃO DO REGISTRO DE NASCIMENTO");
						errro.setfatMensagemLog(new fatMensagemLog(99))
						faturamentoFacade.persistirLogError(erro);
						faturamentoFacade.evict(erro);
					} catch (Exception e) {
						logar("A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: ",
								e);
					}
					return Boolean.FALSE;
				}
				*/
	
			} else { // nao deve ter os dados parto na conta
				// verifica se a conta hospitalar possui os dados de parto em branco
				FatContasHospitalares dadosParto = fatContasHospitalaresDAO
						.obterPorChavePrimaria(pCthSeq);
				if (dadosParto != null
						&& (dadosParto.getRnNascidoVivo() != null
								|| dadosParto.getRnNascidoMorto() != null
								|| dadosParto.getRnSaidaAlta() != null
								|| dadosParto.getRnSaidaTransferencia() != null || dadosParto
								.getRnSaidaObito() != null)) { // dados parto
																// informados
					try {
						FatLogError erro = new FatLogError();
						erro.setModulo(INT);
						erro.setPrograma(RN_CTHC_VER_DADPARTO);
						erro.setCriadoEm(new Date());
						erro.setCriadoPor(servidorLogado.getUsuario());
						erro.setDataPrevia(pDataPrevia);
						erro.setCthSeq(pCthSeq);
						erro.setPacCodigo(pPacCodigo);
						erro.setProntuario(pPacProntuario);
						erro.setPhiSeq(pPhiSolic);
						erro.setIphPhoSeq(pPhoSolic);
						erro.setIphSeq(pIphSolic);
						erro.setCodItemSusSolicitado(pCodSusSolic);
						erro.setPhiSeqRealizado(pPhiRealiz);
						erro.setIphPhoSeqRealizado(pPhoRealiz);
						erro.setIphSeqRealizado(pIphRealiz);
						erro.setCodItemSusRealizado(pCodSusRealiz);
						erro.setErro("REALIZADO NAO PERMITE QUE OS DADOS DE PARTO SEJAM INFORMADOS.");
						
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(241);
						erro.setFatMensagemLog(fatMensagemLog);
						
						persistirLogErroCarregandoFatMensagemLog(
								faturamentoFacade, erro);
					} catch (Exception e) {
						logar(MSG_EXCECAO_IGNORADA,
								e);
					}
					return Boolean.FALSE;
				}
			}
		}
		
		return Boolean.TRUE;
	}

	private void persistirLogErroCarregandoFatMensagemLog(final IFaturamentoFacade faturamentoFacade, FatLogError erro) {
		if(erro.getFatMensagemLog() != null && erro.getFatMensagemLog().getCodigo() != null){
			erro.setFatMensagemLog(fatMensagemLogDAO.obterFatMensagemLogPorCodigo(erro.getFatMensagemLog().getCodigo()));
		}
		faturamentoFacade.persistirLogError(erro);
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_UTIDALTA
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcVerUtidalta(Short pIphPhoSeq, Integer pIphSeq)
			throws BaseException {
		FatGrupoProcedHospitalarDAO fatGrupoProcedHospitalarDAO = getFatGrupoProcedHospitalarDAO();
		FatItemGrupoProcedHospDAO fatItemGrupoProcedHospDAO = getFatItemGrupoProcedHospDAO();
		// Busca grupo de procedimentos do item
		List<FatItemGrupoProcedHosp> itensGrupoProcedimentoHospitalar = fatItemGrupoProcedHospDAO
				.listarItensGrupoProcedimentoHospitalar(pIphPhoSeq, pIphSeq, 0,
						1);

		Integer gphSeq = null;
		if (itensGrupoProcedimentoHospitalar != null
				&& !itensGrupoProcedimentoHospitalar.isEmpty()) {
			gphSeq = itensGrupoProcedimentoHospitalar.get(0).getId()
					.getGphSeq();
		}

		// Verifica se grupo de proced permite cobrar uti do dia da alta
		if (gphSeq != null) {
			FatGrupoProcedHospitalar fatGrupoProcedHospitalar = fatGrupoProcedHospitalarDAO
					.obterPorChavePrimaria(gphSeq);

			if (CoreUtil.igual(Boolean.TRUE,
					fatGrupoProcedHospitalar.getIndCobrancaUtiAlta())) {
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_ATU_CID_CRIOP
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcAtuCidCriop(Integer pCthSeq)
			throws BaseException {
		FatCidContaHospitalarDAO fatCidContaHospitalarDAO = getFatCidContaHospitalarDAO();
		FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();

		final Long paramCodCriPrecFatVII = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_CRIOPRECIPITADO_FATOR_VII);
		Boolean vAux = fatEspelhoItemContaHospDAO
				.existePorCthProcedIndConsistente(pCthSeq, Boolean.TRUE,
						paramCodCriPrecFatVII);
		if (Boolean.TRUE.equals(vAux)) {
			// busca CID secundario cadastrado p/conta hospitalar
			FatCidContaHospitalar cidSecundario = fatCidContaHospitalarDAO
					.buscarPrimeiroCidContasHospitalares(pCthSeq,
							DominioPrioridadeCid.S);
			if (cidSecundario == null) {
				return Boolean.FALSE;
			} else { // if c_seq_cid_secund%notfound
				// busca codigo do cid secundario
				AghCid cidSecund = (cidSecundario.getCid() != null &&
						CoreUtil.igual(DominioSituacao.A, cidSecundario.getCid().getSituacao())) ? 
								cidSecundario.getCid() : null;
								
				final String paramCidSecCoag = buscarVlrTextoAghParametro(AghuParametrosEnum.P_COD_CID_SECUND_DEFEITO_COAGULACAO_NAO_ESPECIFICADO);
				if (cidSecund == null
						|| !CoreUtil.igual(paramCidSecCoag,
								cidSecund.getCodigo())) {
					return Boolean.FALSE;
				} // v_cod_cid <> 'D68.0'
			} // c_seq_cid_secund%notfound
		} // if c_espelho_item%found
		return Boolean.TRUE;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_ATU_CID_HEMODIAL
	 * 
	 * @throws BaseException
	 */
	public Byte rnCthcAtuCidHemodial(Integer pCthSeq, Long pCodSusRealiz)
			throws BaseException {
		FatCidContaHospitalarDAO fatCidContaHospitalarDAO = getFatCidContaHospitalarDAO();
		FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();

		final Long paramInsRenCron2 = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_INSUFICIENCIA_RENAL_CRONICA_ACIDOSE_METABOLICA_2);
		final Long paramInsRenAguda = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_INSUFICIENCIA_RENAL_AGUDA);
		final Long paramInsRenCron1 = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_INSUFICIENCIA_RENAL_CRONICA_ACIDOSE_METABOLICA_1);
		final Long paramIntercPacRenalCronClMed = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_INTERCORRENCIA_PACIENTE_RENAL_CRONICO_CLINICA_MED);
		final Long paramIntercPacRenalCronPed = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_INTERCORRENCIA_PACIENTE_RENAL_CRONICO_PEDIATRIA);

		if (CoreUtil.notIn(pCodSusRealiz, paramInsRenCron2, paramInsRenAguda,
				paramInsRenCron1, paramIntercPacRenalCronClMed,
				paramIntercPacRenalCronPed)) {
			final String[] paramStrDialise = (buscarVlrTextoAghParametro(AghuParametrosEnum.P_COD_HEMODIALISE)
					+ "," + buscarVlrTextoAghParametro(AghuParametrosEnum.P_COD_DIALISE_PERITONEAL))
					.split(",");
			Long[] paramDialise = new Long[paramStrDialise.length];
			for (int i = 0; i < paramStrDialise.length; i++) {
				paramDialise[i] = Long.valueOf(paramStrDialise[i]);
			}
			Boolean vAux = fatEspelhoItemContaHospDAO
					.existePorCthProcedIndConsistente(pCthSeq, Boolean.TRUE,
							paramDialise);
			if (vAux) {
				// busca CID secundario cadastrado p/conta hospitalar
				FatCidContaHospitalar cidSecundario = fatCidContaHospitalarDAO
						.buscarPrimeiroCidContasHospitalares(pCthSeq,
								DominioPrioridadeCid.S);
				if (cidSecundario == null) {
					return 0;
				} else { // if c_seq_cid_secund%notfound
					// busca codigo do cid secundario
					AghCid cidSecund = (cidSecundario.getCid() != null && CoreUtil
							.igual(DominioSituacao.A, cidSecundario.getCid()
									.getSituacao())) ? cidSecundario.getCid()
							: null;
					final String paramCidSecInsRen = buscarVlrTextoAghParametro(AghuParametrosEnum.P_CID_SECUNDARIO_OUTRO_TIPO_INSUFICIENCIA_RENAL_AGUDA);
					if (cidSecund == null
							|| !CoreUtil.igual(paramCidSecInsRen,
									cidSecund.getCodigo())) {
						return 0; // erro
					} else {
						return 1; // consistente
					}
				} // c_seq_cid_secund%notfound
			} // if c_espelho_item%found
		} // pCodSusRealiz not in
		return null;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_REINTERN
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcVerReintern(Integer pCthSeq, Date pDtIntAdm,
			Integer pPacCodigo, Integer pPhiSolic, Short pPhoSolic,
			Integer pIphSolic, Long pCodSusSolic, Integer pPhiRealiz,
			Short pPhoRealiz, Integer pIphRealiz, Long pCodSusRealiz)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO = getVFatContaHospitalarPacDAO();
		Boolean retorno = Boolean.FALSE;

		logar("P_CTH_SEQ:  {0} P_DT_INT_ADM:{1} P_PAC_CODIGO:{2}", pCthSeq,
				pDtIntAdm, pPacCodigo);
		logar("P_PHI_SOLIC:  {0} P_PHO_SOLIC:{1} P_COD_SUS_SOLIC:{2}",
				pPhiSolic, pPhoSolic, pCodSusSolic);
		logar("P_PHI_REALIZ:  {0} P_PHO_REALIZ:{1} P_IPH_REALIZ:{2}",
				pPhiRealiz, pPhoRealiz, pIphRealiz);
		logar("P_COD_SUS_REALIZ:  {0}", pCodSusRealiz);

		// verifica nro de dias para reinternacao do proced realizado
		FatItensProcedHospitalar aux = fatItensProcedHospitalarDAO.obterPorChavePrimaria( new FatItensProcedHospitalarId(pPhoRealiz,pIphRealiz) );
		Byte vDiasReintern = (aux != null) ? aux.getDiasReinternacao() : null;
		
		logar("nro de dias para reinternacao do proced realizado:  {0}", vDiasReintern);
		vDiasReintern = nvl(vDiasReintern, 0);
		if (CoreUtil.maior(vDiasReintern, 0)) {
			// busca contas e internacoes do paciente c/mesmo ssm realizado
			Integer vCthAnt = vFatContaHospitalarPacDAO
					.buscarPrimeiraCthIntPacSsmRealizado(pCthSeq, pPacCodigo,
							pPhiRealiz, pDtIntAdm, vDiasReintern);
			if (vCthAnt != null) {
				logar("busca contas e internacoes do paciente c/mesmo ssm realizado : {0}",
						vCthAnt);
				// existe conta anterior c/mesmo realizado
				// dentro do periodo de reinternacao
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma("RN_CTHC_VER_REINTERN");
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("DADOS DE PARTO NÃO CADASTRADOS.");
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA,
							e);
				}
				logar("Marina - Reinternacao: - volta true ");
				retorno = Boolean.TRUE;
			}
		}
		return retorno;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHP_VER_TAIH_MOT
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void rnCthpVerTaihMot(Byte pCthMdsSeq, Byte pCthTahSeq,
			Short pCthCnvCodigo) throws BaseException {
		if (pCthCnvCodigo != null) {
			FatConvenioSaudeDAO fatConvenioSaudeDAO = getFatConvenioSaudeDAO();
			FatMotivoDesdobramentoDAO fatMotivoDesdobramentoDAO = getFatMotivoDesdobramentoDAO();
			FatTipoAihDAO fatTipoAihDAO = getFatTipoAihDAO();

			// verifica se o convenio e' SUS
			FatConvenioSaude vConvSus = fatConvenioSaudeDAO
					.obterPorChavePrimaria(pCthCnvCodigo);
			// se o convenio e' SUS, a conta deve ter tipo_aih preenchido
			if (vConvSus != null
					&& CoreUtil.igual(DominioGrupoConvenio.S,
							vConvSus.getGrupoConvenio())) {
				if (pCthMdsSeq != null) {
					// busca fk de tipo_aih em motivo_desdobramento
					FatMotivoDesdobramento fatMotivoDesdobramento = fatMotivoDesdobramentoDAO
							.obterPorChavePrimaria(pCthMdsSeq);
					FatTipoAih vMdsTah = fatMotivoDesdobramento != null ? fatMotivoDesdobramento
							.getTipoAih() : null;
					if (vMdsTah != null) {
						// busca o codigo sus do tipo de aih do motivo
						// desdobramento
						Short vCodSusTaihMoti = vMdsTah.getCodigoSus();
						// busca o codigo sus do tipo de aih da conta
						FatTipoAih fatTipoAih = fatTipoAihDAO
								.obterPorChavePrimaria(pCthTahSeq);
						Short vCodSusTaihCta = fatTipoAih != null ? fatTipoAih
								.getCodigoSus() : null;
						if (!CoreUtil.igual(vCodSusTaihMoti, vCodSusTaihCta)) {
							// tipo aih do motivo desdobram deve ser igual ao
							// tipo aih da cta
							throw new ApplicationBusinessException(
									FaturamentoExceptionCode.FAT_00439);
						}
					} // c_aih_moti%found
				} // pCthMdsSeq is not null
			} // c_conv_sus%found
		} // pCnvCodigo is not null
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHP_VER_ALTACONV
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void rnCthpVerAltaconv(Integer pCspCnvCodigo,
			Integer pTacCnvCodigo) throws BaseException {
		// Verifica se o convenio do tipo de alta da conta hospitalar e igual ao
		// convenio do plano da conta hospitalar
		if ((pCspCnvCodigo != null && pTacCnvCodigo == null)
				|| (pCspCnvCodigo == null && pTacCnvCodigo != null)
				|| (pCspCnvCodigo != null && !CoreUtil.igual(pCspCnvCodigo,
						pTacCnvCodigo))
				|| (pTacCnvCodigo != null && !CoreUtil.igual(pTacCnvCodigo,
						pCspCnvCodigo))) {
			// Convênio do tipo da alta da conta hospitalar deve ser igual ao
			// convênio do plano saude da conta hospitalar.
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00442);
		}
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_PAC_CTA
	 * 
	 * @throws BaseException
	 */
	public RnCthcVerPacCtaVO rnCthcVerPacCta(Integer pCthSeq)
			throws BaseException {
		RnCthcVerPacCtaVO resultVO = new RnCthcVerPacCtaVO();

		// Busca paciente da conta hospitalar
		List<PacienteContaHospitalarVO> listaPacientesContaHospitalar = this
				.getFatContasInternacaoDAO()
				.listarPacienteContaHospitalarVOComInternacao(pCthSeq);

		if (listaPacientesContaHospitalar != null
				&& !listaPacientesContaHospitalar.isEmpty()) {
			PacienteContaHospitalarVO pacienteContaHospitalarVO = listaPacientesContaHospitalar
					.get(0);

			resultVO.setPacCodigo(pacienteContaHospitalarVO.getPacCodigo());
			resultVO.setPacProntuario(pacienteContaHospitalarVO
					.getPacProntuario());
			resultVO.setIntSeq(pacienteContaHospitalarVO.getIntSeq());
			resultVO.setRetorno(Boolean.TRUE);

			return resultVO;
		} else {
			listaPacientesContaHospitalar = this.getFatContasInternacaoDAO()
					.listarPacienteContaHospitalarVO(pCthSeq);

			if (listaPacientesContaHospitalar != null
					&& !listaPacientesContaHospitalar.isEmpty()) {
				PacienteContaHospitalarVO pacienteContaHospitalarVO = listaPacientesContaHospitalar
						.get(0);

				resultVO.setPacCodigo(pacienteContaHospitalarVO.getPacCodigo());
				resultVO.setPacProntuario(pacienteContaHospitalarVO
						.getPacProntuario());
				resultVO.setIntSeq(null);
				resultVO.setRetorno(Boolean.TRUE);

				return resultVO;
			} else {
				resultVO.setPacCodigo(null);
				resultVO.setPacProntuario(null);
				resultVO.setIntSeq(null);
				resultVO.setRetorno(Boolean.FALSE);

				return resultVO;
			}
		}
	}
	
	
	/**
	 * @ORADB Procedure FATK_CTH_RN.RN_CTHC_VER_CTA_PAC 
	 * Retirado do AGH: Nas contas desdobradas a dt_int e igua a dt_alta da mae 
	 * O Loop serve p/trazer a conta filha pois os itens c/dt=dt_int da filha
	 * devem estar na conta filha - FGi 21/08
	 * @throws BaseException
	 */
	public Integer rnCthcVerCtaPac(final Integer pPacCodigo, final Date pDthrRealizado) throws BaseException {

		Integer vCthSeq = -1;
		
		final DominioSituacaoConta[] pIndSituacao = { DominioSituacaoConta.A, DominioSituacaoConta.F };

		List<Integer> list = this.getFatContasInternacaoDAO().pesquisarContasInternacaoDesdobradas(pPacCodigo, pDthrRealizado, pIndSituacao);

		if (list != null && !list.isEmpty()) {			
			for (Integer i : list) {
				vCthSeq = i;
			}
			
			return vCthSeq;			
		} else {
			return vCthSeq;
		}
	}
	
	/**
	 * Busca o VO para todos os itens de conta hospitalar.<br>
	 * 
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_ITEM_SUS.<br>
	 * 
	 * @param pModulo
	 * @param pCnvCodigo
	 * @param pCspSeq
	 * @param pQuantidade
	 * @param listaItensContaHospitalar
	 * @return
	 */
	public Map<String, List<BuscarProcedHospEquivalentePhiVO>> rnCthcVerItemSusLoadAll(
			DominioOrigemProcedimento pModulo, Short pCnvCodigo, Byte pCspSeq, Short pQuantidade,
			List<FatItemContaHospitalar> listaItensContaHospitalar) throws BaseException {
		FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = this.getFatItensProcedHospitalarDAO();
		
		// Tipo grupo conta padrão para convenio SUS
		Short vGrcSus = buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		List<BuscarProcedHospEquivalentePhiVO> listProcedHospEquivalentePhi = fatItensProcedHospitalarDAO.buscarProcedHospEquivalentePhiLoadAll(
				listaItensContaHospitalar, pQuantidade,
				pCnvCodigo, pCspSeq, vGrcSus
		);
		
		
		Map<String, List<BuscarProcedHospEquivalentePhiVO>> mapReturn = new HashMap<String, List<BuscarProcedHospEquivalentePhiVO>>();
		
		String key;
		List<BuscarProcedHospEquivalentePhiVO> listaVOs;
		for (BuscarProcedHospEquivalentePhiVO procedHospEquivalentePhiVO : listProcedHospEquivalentePhi) {
			key = BuscarProcedHospEquivalentePhiVO.getKeyMapBuscaProcedHospEquivalentePhi(procedHospEquivalentePhiVO.getPhiSeq(), pQuantidade, pCnvCodigo, pCspSeq, vGrcSus);
			if (mapReturn.containsKey(key)) {
				listaVOs = mapReturn.get(key);
			} else {
				listaVOs = new ArrayList<BuscarProcedHospEquivalentePhiVO>(10);
				mapReturn.put(key, listaVOs);
			}
			listaVOs.add(procedHospEquivalentePhiVO);
		}
		
		return mapReturn;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_ITEM_SUS
	 * 
	 * @throws BaseException
	 */
	public RnCthcVerItemSusVO rnCthcVerItemSus(
			DominioOrigemProcedimento pModulo, Short pCnvCodigo, Byte pCspSeq,
			Short pQuantidade, Integer pPhiSeq, Map<String, List<BuscarProcedHospEquivalentePhiVO>> rnCthcVerItemSusVOMap) throws BaseException {
		RnCthcVerItemSusVO rnCthcVerItemSusVO = new RnCthcVerItemSusVO();

		// Se ter mais de um registro, nao preciso mais processar, pois o
		// retorno será falso.
		int maximoRegistrosParaProcessar = 2;
		List<RnFatcVerItprocexcVO> rnFatcVerItprocexcVOList = getVerificacaoFaturamentoSusRN()
				.verificarItemProcHosp(pPhiSeq, pQuantidade, pCnvCodigo,
						pCspSeq, maximoRegistrosParaProcessar, rnCthcVerItemSusVOMap);

		if (rnFatcVerItprocexcVOList != null
				&& rnFatcVerItprocexcVOList.size() == 1) {
			RnFatcVerItprocexcVO rnFatcVerItprocexcVO = rnFatcVerItprocexcVOList
					.get(0);

			Long codTabela = getFatItensProcedHospitalarDAO()
					.listarItensProcedHospitalar(rnFatcVerItprocexcVO.getSeq(),
							rnFatcVerItprocexcVO.getPhoSeq(), true);

			if (codTabela != null) {
				rnCthcVerItemSusVO.setPhoSeq(rnFatcVerItprocexcVO.getPhoSeq());
				rnCthcVerItemSusVO.setIphSeq(rnFatcVerItprocexcVO.getSeq());
				rnCthcVerItemSusVO.setCodSus(codTabela);

				logar("--- >>> retorno verdadeiro {0}",
						rnCthcVerItemSusVO.getCodSus());

				rnCthcVerItemSusVO.setRetorno(Boolean.TRUE);
			} else {
				rnCthcVerItemSusVO.setRetorno(Boolean.FALSE);
			}
		} else {
			rnCthcVerItemSusVO.setRetorno(Boolean.FALSE);
		}

		return rnCthcVerItemSusVO;
	}
	
	private List<String> splitParametros(AghParametros aghParametros)
			throws ApplicationBusinessException {
		
		List<String> listaParametros = null;
		
		if (aghParametros != null && StringUtils.isNotBlank(aghParametros.getVlrTexto())) {
			listaParametros = Arrays.asList(aghParametros.getVlrTexto().split(","));
		} else {
			new ApplicationBusinessException(AghParemetrosONExceptionCode.PARAMETRO_INVALIDO, aghParametros.getNome());
		}
		return listaParametros;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_ATU_REGRAS
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NcssMethodCount", "PMD.NPathComplexity" })
	public RnCthcAtuRegrasVO rnCthcAtuRegras(Boolean pPrevia,
			Date pDataPrevia, Integer pCthSeq, Integer pPacCodigo,
			Integer pPacProntuario, Integer pIntSeq, Integer pPhiSolic,
			Short pPhoSolic, Integer pIphSolic, Long pCodSusSolic,
			Integer pPhiRealiz, Short pPhoRealiz, Integer pIphRealiz,
			Long pCodSusRealiz, Integer pClcRealiz, Integer pDiasConta,
			Date pPacDtNasc, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();

		final RnCthcAtuRegrasVO retorno = new RnCthcAtuRegrasVO();
		retorno.setRetorno(Boolean.TRUE);
		// verifica se procedimento solicitado permite ser lancado como solicitado
		FatItensProcedHospitalar vIphSolicitado = fatItensProcedHospitalarDAO
				.obterPorChavePrimaria(new FatItensProcedHospitalarId(pPhoSolic, pIphSolic));
		DominioUtilizacaoItem vUtilizItem = vIphSolicitado != null ? vIphSolicitado.getUtilizacaoItem() : null;
		
		if (CoreUtil.notIn(vUtilizItem, DominioUtilizacaoItem.S, DominioUtilizacaoItem.A)) { // Solicitado/Ambos
			try {
				FatLogError erro = new FatLogError();
				erro.setModulo(INT);
				erro.setPrograma(RN_CTHC_ATU_REGRAS);
				erro.setCriadoEm(new Date());
				erro.setCriadoPor(servidorLogado.getUsuario());
				erro.setDataPrevia(pDataPrevia);
				erro.setCthSeq(pCthSeq);
				erro.setPacCodigo(pPacCodigo);
				erro.setProntuario(pPacProntuario);
				erro.setPhiSeq(pPhiSolic);
				erro.setIphPhoSeq(pPhoSolic);
				erro.setIphSeq(pIphSolic);
				erro.setCodItemSusSolicitado(pCodSusSolic);
				erro.setPhiSeqRealizado(pPhiRealiz);
				erro.setIphPhoSeqRealizado(pPhoRealiz);
				erro.setIphSeqRealizado(pIphRealiz);
				erro.setCodItemSusRealizado(pCodSusRealiz);
				erro.setErro("PROCEDIMENTO NAO PERMITE SER LANCADO COMO SOLICITADO.");
				
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(208);
				erro.setFatMensagemLog(fatMensagemLog);
				
				persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
						erro);
			} catch (Exception e) {
				logar(MSG_EXCECAO_IGNORADA,
						e);
			}
			retorno.setRetorno(Boolean.FALSE);
		} // v_utiliz_item not in ('S','A')

		// verifica se o procedimento realizado permite ser lancado como realizado
		FatItensProcedHospitalar vIphRealizado = fatItensProcedHospitalarDAO
				.obterPorChavePrimaria(new FatItensProcedHospitalarId(pPhoRealiz, pIphRealiz));
		
		vUtilizItem = vIphRealizado != null ? vIphRealizado.getUtilizacaoItem() : null;
		if (CoreUtil.notIn(vUtilizItem, DominioUtilizacaoItem.R, DominioUtilizacaoItem.A)) { // Solicitado/Ambos
			try {
				FatLogError erro = new FatLogError();
				erro.setModulo(INT);
				erro.setPrograma(RN_CTHC_ATU_REGRAS);
				erro.setCriadoEm(new Date());
				erro.setCriadoPor(servidorLogado.getUsuario());
				erro.setDataPrevia(pDataPrevia);
				erro.setCthSeq(pCthSeq);
				erro.setPacCodigo(pPacCodigo);
				erro.setProntuario(pPacProntuario);
				erro.setPhiSeq(pPhiSolic);
				erro.setIphPhoSeq(pPhoSolic);
				erro.setIphSeq(pIphSolic);
				erro.setCodItemSusSolicitado(pCodSusSolic);
				erro.setPhiSeqRealizado(pPhiRealiz);
				erro.setIphPhoSeqRealizado(pPhoRealiz);
				erro.setIphSeqRealizado(pIphRealiz);
				erro.setCodItemSusRealizado(pCodSusRealiz);
				erro.setErro("PROCEDIMENTO NAO PERMITE SER LANCADO COMO REALIZADO.");
				
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(207);
				erro.setFatMensagemLog(fatMensagemLog);
				
				persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
						erro);
			} catch (Exception e) {
				logar(MSG_EXCECAO_IGNORADA,
						e);
			}
			retorno.setRetorno(Boolean.FALSE);
		} // v_utiliz_item not in ('R','A')

		// busca limites de idade para o procedimento realizado
		Integer vIdMin = vIphRealizado.getIdadeMin() != null ? vIphRealizado.getIdadeMin() : 0;
		Integer vIdMax = vIphRealizado.getIdadeMax() != null ? vIphRealizado.getIdadeMax() : 999;
		Integer vClcRealiz = vIphRealizado.getClinica() != null ? vIphRealizado.getClinica().getCodigo() : null;

		// verifica idade do paciente
		Integer vIdadePac = CoreUtil.calculaIdade(pPacDtNasc);

		// verifica limite de idade entre paciente pediatrico e adulto
		final Long vIdadeLimite = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_LIMITE_IDADE_PEDIATR_ADULTO).longValue();
		// verifica codigo de clinica pediatrica
		final Integer vClinicaPediatrica = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_CLINICA_PEDIATRICA);
		// verifica codigo de clinica medica
		final Integer vClinicaMedica = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_CLINICA_MEDICA);
		// Verifica se o ssm tem caract: Clinica Adicional do SSM
		logar("Antes teste Clínica " + vClcRealiz + " Pediat "
				+ vClinicaPediatrica + " Idade " + vIdadePac + " Idade Lim " + vIdadeLimite);
		if (CoreUtil.igual(vClcRealiz, vClinicaPediatrica)
				&& CoreUtil.maior(vIdadePac, vIdadeLimite)) {
			if (getFaturamentoRN().verificarCaracteristicaExame(pIphRealiz,
					pPhoRealiz,
					DominioFatTipoCaractItem.CLINICA_ADICIONAL_DO_SSM)) {
				logar("Encontrou caract clinica ");
				vClcRealiz = vClinicaMedica;
			}
		}

		// verifica especialidade AIH(pela clinica do realizado ou idade paciente)
		if (vClcRealiz != null) {
			/*#31004*/
				if(CoreUtil.in(vClcRealiz.toString(), splitParametros(parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_LISTA_CLINICAS_REALIZADO)))){
				// realizado possui clinica valida associada
				retorno.setClcRealiz(vClcRealiz);
			} else {
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma(RN_CTHC_ATU_REGRAS);
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setDataPrevia(pDataPrevia);
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setProntuario(pPacProntuario);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("CLINICA DO PROCEDIMENTO REALIZADO INVALIDA.");
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA,
							e);
				}
				retorno.setRetorno(Boolean.FALSE);
				retorno.setClcRealiz(null);
			}
		} else {
			// verifica clinica conforme idade do paciente
			if (CoreUtil.menorOuIgual(vIdadePac, vIdadeLimite)) { // paciente pediatrico
				retorno.setClcRealiz(vClinicaPediatrica); // clinica pediatrica
			} else { // paciente adulto
				retorno.setClcRealiz(vClinicaMedica); // clinica medica
			}
		} // v_clc_realiz is not null

		logar("VERIFICA IDADE DO PACIENTE: {0} v_id_min: {1}", vIdadePac,
				vIdMin);
		// verifica se idade do paciente e' compativel c/idade permitida
		// p/realizado
		if (CoreUtil.menor(vIdadePac, vIdMin)
				|| CoreUtil.maior(vIdadePac, vIdMax)) {
			// Verifica compatibilidade da clinica do realizado c/idade do paciente:
			if (CoreUtil.menor(vIdadePac, vIdadeLimite)
					&& CoreUtil.igual(pClcRealiz, vClinicaMedica)) { // paciente pediatrico e clinica medica
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma(RN_CTHC_ATU_REGRAS);
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setDataPrevia(pDataPrevia);
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setProntuario(pPacProntuario);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("PROCEDIMENTO REALIZADO DE CLINICA MEDICA, INCOMPATIVEL COM IDADE DO PACIENTE.");
					
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(212);
					erro.setFatMensagemLog(fatMensagemLog);
					
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA,
							e);
				}
				retorno.setRetorno(Boolean.FALSE);
			} else if (CoreUtil.maiorOuIgual(vIdadePac, vIdadeLimite)
					&& CoreUtil.igual(pClcRealiz, vClinicaPediatrica)) { // paciente adulto e clinica pediatrica
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma(RN_CTHC_ATU_REGRAS);
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setDataPrevia(pDataPrevia);
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setProntuario(pPacProntuario);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("PROCEDIMENTO REALIZADO DE CLINICA PEDIATRICA, INCOMPATIVEL COM IDADE DO PACIENTE.");
					
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(213);
					erro.setFatMensagemLog(fatMensagemLog);
					
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA,
							e);
				}
				retorno.setRetorno(Boolean.FALSE);
			} else {
				// Verifica exclusao critica tipo 2 e 3:
				if (CoreUtil.menor(vIdadePac, vIdMin)) { // paciente menor que idade minima
					// Marina 29/12/2009
					logar("Exclusão de Crítica:  - paciente menor que idade minima");
					retorno.setCodExclusaoCritica(this.rnFatpExclusaoCritica(
							DominioSituacao.A, DominioSituacao.I,
							DominioSituacao.I, DominioSituacao.I,
							DominioSituacao.I, pCthSeq, DominioSituacao.I, nomeMicrocomputador, dataFimVinculoServidor));
				}

				if (CoreUtil.maior(vIdadePac, vIdMax)) { // paciente maior que idade maxima
					// Marina 29/12/2009
					logar("Exclusão de Crítica:  - paciente maior que idade maxima");
					retorno.setCodExclusaoCritica(this.rnFatpExclusaoCritica(
							DominioSituacao.I, DominioSituacao.A,
							DominioSituacao.I, DominioSituacao.I,
							DominioSituacao.I, pCthSeq, DominioSituacao.I, nomeMicrocomputador, dataFimVinculoServidor));
				}
			}
		}

		// busca permanencia maxima p/ contas de psiquiatria e hospital dia
		final BigDecimal vPermPsiqHospdia = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_PERM_PSIQ_HOSPDIA);

		// Verifica periodo conta psiquiatrica:
		// busca codigo de clinica psiquiatrica
		final BigDecimal vClinicaPsiquiatrica = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_CLINICA_PSIQUIATRICA);

		if (CoreUtil.igual(pClcRealiz, vClinicaPsiquiatrica)
				&& CoreUtil.maior(pDiasConta, vPermPsiqHospdia)) { // clinica psiquiatrica
			try {
				FatLogError erro = new FatLogError();
				erro.setModulo(INT);
				erro.setPrograma(RN_CTHC_ATU_REGRAS);
				erro.setCriadoEm(new Date());
				erro.setCriadoPor(servidorLogado.getUsuario());
				erro.setDataPrevia(pDataPrevia);
				erro.setCthSeq(pCthSeq);
				erro.setPacCodigo(pPacCodigo);
				erro.setProntuario(pPacProntuario);
				erro.setPhiSeq(pPhiSolic);
				erro.setIphPhoSeq(pPhoSolic);
				erro.setIphSeq(pIphSolic);
				erro.setCodItemSusSolicitado(pCodSusSolic);
				erro.setPhiSeqRealizado(pPhiRealiz);
				erro.setIphPhoSeqRealizado(pPhoRealiz);
				erro.setIphSeqRealizado(pIphRealiz);
				erro.setCodItemSusRealizado(pCodSusRealiz);
				erro.setFatMensagemLog(new FatMensagemLog(22));
				erro.setErro("CONTA COM REALIZADO DE CLINICA PSIQUIATRICA NAO PODE TER MAIS DE "
						+ vPermPsiqHospdia.intValue() + " DIAS.");
				persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
						erro);
			} catch (Exception e) {
				logar(MSG_EXCECAO_IGNORADA, e);
			}
			retorno.setRetorno(Boolean.FALSE);
		}

		// verifica periodo conta hospital dia:
		if (vIphRealizado.getHospDia()
				&& CoreUtil.maior(pDiasConta, vPermPsiqHospdia)) {
			try {
				
				FatMensagemLog  fatmensagemLog = new FatMensagemLog();
				fatmensagemLog.setCodigo(23);
				FatLogError erro = new FatLogError();
				erro.setModulo(INT);
				erro.setPrograma(RN_CTHC_ATU_REGRAS);
				erro.setCriadoEm(new Date());
				erro.setCriadoPor(servidorLogado.getUsuario());
				erro.setDataPrevia(pDataPrevia);
				erro.setCthSeq(pCthSeq);
				erro.setPacCodigo(pPacCodigo);
				erro.setProntuario(pPacProntuario);
				erro.setPhiSeq(pPhiSolic);
				erro.setIphPhoSeq(pPhoSolic);
				erro.setIphSeq(pIphSolic);
				erro.setCodItemSusSolicitado(pCodSusSolic);
				erro.setPhiSeqRealizado(pPhiRealiz);
				erro.setIphPhoSeqRealizado(pPhoRealiz);
				erro.setIphSeqRealizado(pIphRealiz);
				erro.setCodItemSusRealizado(pCodSusRealiz);
				erro.setErro("CONTA COM REALIZADO DE HOSPITAL DIA NAO PODE TER MAIS DE "
						+ vPermPsiqHospdia + " DIAS.");
				erro.setFatMensagemLog(fatmensagemLog);
				persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
						erro);
			} catch (Exception e) {
				logar(MSG_EXCECAO_IGNORADA, e);
			}
			retorno.setRetorno(Boolean.FALSE);
		}

		// etb 22/08/2005
		// verifica se RZDO possui característica de limite dias na conta
		Integer vNumdias = getFaturamentoRN().fatcBuscaVlrNum(
				pPhoRealiz,
				pIphRealiz,
				DominioFatTipoCaractItem.NRO_MAXIMO_DIAS_NA_CONTA.getDescricao());
		logar("Encontrou caract lim dias {0} dias calc {1}", vNumdias, pDiasConta);
		if (CoreUtil.maior(vNumdias, 0) && CoreUtil.maior(pDiasConta, vNumdias)) {
			try {
				FatLogError erro = new FatLogError();
				erro.setModulo(INT);
				erro.setPrograma(RN_CTHC_ATU_REGRAS);
				erro.setCriadoEm(new Date());
				erro.setCriadoPor(servidorLogado.getUsuario());
				erro.setDataPrevia(pDataPrevia);
				erro.setCthSeq(pCthSeq);
				erro.setPacCodigo(pPacCodigo);
				erro.setProntuario(pPacProntuario);
				erro.setPhiSeq(pPhiSolic);
				erro.setIphPhoSeq(pPhoSolic);
				erro.setIphSeq(pIphSolic);
				erro.setCodItemSusSolicitado(pCodSusSolic);
				erro.setPhiSeqRealizado(pPhiRealiz);
				erro.setIphPhoSeqRealizado(pPhoRealiz);
				erro.setIphSeqRealizado(pIphRealiz);
				erro.setCodItemSusRealizado(pCodSusRealiz);
				erro.setErro("SSM POSSUI CARACTERÍSTICA LIMITANDO OS DIAS NA CONTA EM "
						+ vNumdias.intValue() + " DIA(S).");
				
				FatMensagemLog  fatmensagemLog = new FatMensagemLog();
				fatmensagemLog.setCodigo(256);
				erro.setFatMensagemLog(fatmensagemLog);
				
				
				persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
						erro);
			} catch (Exception e) {
				logar(MSG_EXCECAO_IGNORADA, e);
			}
			retorno.setRetorno(Boolean.FALSE);
		}
		// FIM etb 22/08/2005

		// verifica se o solicitado e o realizado permitem
		// mudanca de procedimento e, caso nao permita, verifica se os
		// procedimentos realizado e solicitado sao iguais
		if (!rnCthcVerSsmdifer(pDataPrevia, pCthSeq, pPacCodigo,
				pPacProntuario, pIntSeq, pPhiSolic, pPhoSolic, pIphSolic,
				pCodSusSolic, pPhiRealiz, pPhoRealiz, pIphRealiz, pCodSusRealiz)) {
			// mensagens de erro sao geradas na propria funcao
			this.logar("mensagens de erro sao geradas na propria funcao");
			retorno.setRetorno(Boolean.FALSE);
		} // not fatk_cth_rn_un.rn_cthc_ver_ssmdifer

		// verifica se o procedimento realizado exige/permite
		// informacao dos dados de parto na conta
		if (!rnCthcVerDadparto(pDataPrevia, pCthSeq, pPacCodigo,
				pPacProntuario, pIntSeq, pPhiSolic, pPhoSolic, pIphSolic,
				pCodSusSolic, pPhiRealiz, pPhoRealiz, pIphRealiz, pCodSusRealiz)) {
			// log de erro foi inserido dentro da funcao
			retorno.setRetorno(Boolean.FALSE);
		} // not fatk_cth_rn_un.rn_cthc_ver_dadparto

		// verifica se o paciente tem cirurgia sem nota de sala digitada
		if (!rnCthcVerNotacrg(pCthSeq)) {
			logar("Marina ---  verifica se o paciente tem cirurgia sem nota de sala digitada");
			
			try {
				FatLogError erro = new FatLogError();
				erro.setModulo(INT);
				erro.setPrograma(RN_CTHC_ATU_REGRAS);
				erro.setCriadoEm(new Date());
				erro.setCriadoPor(servidorLogado.getUsuario());
				erro.setDataPrevia(pDataPrevia);
				erro.setCthSeq(pCthSeq);
				erro.setPacCodigo(pPacCodigo);
				erro.setProntuario(pPacProntuario);
				erro.setPhiSeq(pPhiSolic);
				erro.setIphPhoSeq(pPhoSolic);
				erro.setIphSeq(pIphSolic);
				erro.setCodItemSusSolicitado(pCodSusSolic);
				erro.setPhiSeqRealizado(pPhiRealiz);
				erro.setIphPhoSeqRealizado(pPhoRealiz);
				erro.setIphSeqRealizado(pIphRealiz);
				erro.setCodItemSusRealizado(pCodSusRealiz);
				erro.setErro("CONTA POSSUI CIRURGIA SEM NOTA DE SALA DIGITADA.");
				
				FatMensagemLog fatMensagemLog= new FatMensagemLog();
				fatMensagemLog.setCodigo(41);
				erro.setFatMensagemLog(fatMensagemLog);
				persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
						erro);
			} catch (Exception e) {
				logar(MSG_EXCECAO_IGNORADA, e);
			}

			retorno.setRetorno(Boolean.FALSE);
		}

		// verifica se é uma conta que exige dados do parto
		// e se o número do pré-natal está cadastrado
		if (!getFatkCth5RN().rnCthcVerSisprenatal(pDataPrevia, pCthSeq,
				pPacCodigo, pPacProntuario, pIntSeq, pPhiSolic, pPhoSolic,
				pIphSolic, pCodSusSolic, pPhiRealiz, pPhoRealiz, pIphRealiz,
				pCodSusRealiz)) {
			//if (!rnCthcVerNotacrg(pCthSeq)) {
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma(RN_CTHC_ATU_REGRAS);
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setDataPrevia(pDataPrevia);
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setProntuario(pPacProntuario);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("CONTA NÃO POSSUI NRO_SISPRENATAL.");
					erro.setFatMensagemLog(new FatMensagemLog(262));
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA, e);
				}
				//retorno.setRetorno(Boolean.FALSE); comentado if e essa linha de acordo com APRH => 21/03/2013 14:59
			//}
		}

		if (retorno.getRetorno()) {
			logar("RN_CTHC_ATU_REGRAS retornou TRUE erro=");
		} else {
			logar("RN_CTHC_ATU_REGRAS retornou FALSE erro=");
		}

		return retorno;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_ATU_REGRAPOS
	 * @param fatkCth2RnUnVMaiorValor 
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public Boolean rnCthcAtuRegrapos(Boolean pPrevia, Date pDataPrevia,
			Integer pCthSeq, Integer pPacCodigo, Integer pPacProntuario,
			Integer pIntSeq, Integer pPhiSolic, Short pPhoSolic,
			Integer pIphSolic, Long pCodSusSolic, Integer pPhiRealiz,
			Short pPhoRealiz, Integer pIphRealiz, Long pCodSusRealiz,
			Date pDtRealizado, FatVariaveisVO fatVariaveisVO) throws BaseException {
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();

		boolean result = true;

		Short vRnNascidoVivo = null;
		// boolean erroSsmExigeSec = false;
		// boolean erroCidSsm = false;
		// boolean erroFxCid = false;
		// boolean erroHemo = false;
		Byte vCidHemo = null;
		Integer vCidFixo = null;
		Short vPhoRn;
		Integer vIphRn;
		Long vCodSusRn;
		Integer vPtAnestRn;
		Integer vPtCirRn;
		Long vCodAtendRn;
		Boolean vCidObrig = null;
		Integer vPtSadtRn;
		Integer vTivSusRn;
		Byte vTaoSusRn;
		BigDecimal vVlrAnestRn = BigDecimal.ZERO;
		BigDecimal vVlrProcedRn = BigDecimal.ZERO;
		BigDecimal vVlrSadtRn = BigDecimal.ZERO;
		BigDecimal vVlrServHospRn = BigDecimal.ZERO;
		BigDecimal vVlrServProfRn = BigDecimal.ZERO;
		Short vEicSeqp;

		FaturamentoON faturamentoON = getFaturamentoON();
		FaturamentoRN faturamentoRN = getFaturamentoRN();
		FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();

		// Vai fazer lancamento de acompanhamento ao RN em sala de parto:
		FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO
				.obterPorChavePrimaria(pCthSeq);

		if (contaHospitalar != null) {
			vRnNascidoVivo = contaHospitalar.getRnNascidoVivo() != null ? contaHospitalar
					.getRnNascidoVivo().shortValue() : null;
		}

		if (vRnNascidoVivo != null && vRnNascidoVivo > 0) {
			// Busca codigo do procedim de acompanhamento ao RN em sala de parto
			vCodAtendRn = buscarVlrNumericoAghParametro(
					AghuParametrosEnum.P_COD_ATENDIM_RN_SALA_PARTO).longValue();

			DominioSituacao situacao = DominioSituacao.A;
			DominioSituacao situacaoRegistroTipoVinculo = DominioSituacao.A;
			DominioSituacao situacaoRegistroTipoAto = DominioSituacao.A;
			FatItensProcedHospitalar itemProcedimentoHospitalar = fatItensProcedHospitalarDAO
					.buscarPrimeiroItemProcedimentosHospitalares(vCodAtendRn,
							situacao, situacaoRegistroTipoVinculo,
							situacaoRegistroTipoAto);
			if (itemProcedimentoHospitalar != null) {
				vPhoRn = itemProcedimentoHospitalar.getId().getPhoSeq();
				vIphRn = itemProcedimentoHospitalar.getId().getSeq();
				vCodSusRn = itemProcedimentoHospitalar.getCodTabela();
				vPtAnestRn = itemProcedimentoHospitalar.getPontoAnestesista();
				vPtSadtRn = itemProcedimentoHospitalar.getPontosSadt();
				vPtCirRn = itemProcedimentoHospitalar.getPontosCirurgiao();
				vTivSusRn = itemProcedimentoHospitalar.getTiposVinculo() != null ? itemProcedimentoHospitalar
						.getTiposVinculo().getCodigoSus() : null;
				vTaoSusRn = itemProcedimentoHospitalar.getTipoAto() != null ? itemProcedimentoHospitalar
						.getTipoAto().getCodigoSus() : null;

				vCodSusRn = vCodSusRn == null ? 0 : vCodSusRn;
				vPtAnestRn = vPtAnestRn == null ? 0 : vPtAnestRn;
				vPtCirRn = vPtCirRn == null ? 0 : vPtCirRn;
				vPtSadtRn = vPtSadtRn == null ? 0 : vPtSadtRn;
				vTivSusRn = vTivSusRn == null ? 0 : vTivSusRn;
				vTaoSusRn = vTaoSusRn == null ? 0 : vTaoSusRn;

				// Busca valores pro item
				FatVlrItemProcedHospComps fatVlrItemProcedHospComps = getVerificacaoItemProcedimentoHospitalarRN()
						.obterValoresItemProcHospPorModuloCompetencia(vPhoRn,
								vIphRn, DominioModuloCompetencia.INT, null);

				if (fatVlrItemProcedHospComps != null) {
					vVlrServHospRn = nvl(
							fatVlrItemProcedHospComps.getVlrServHospitalar(),
							BigDecimal.ZERO);
					vVlrSadtRn = nvl(fatVlrItemProcedHospComps.getVlrSadt(),
							BigDecimal.ZERO);
					vVlrProcedRn = nvl(
							fatVlrItemProcedHospComps.getVlrProcedimento(),
							BigDecimal.ZERO);
					vVlrAnestRn = nvl(
							fatVlrItemProcedHospComps.getVlrAnestesista(),
							BigDecimal.ZERO);
					vVlrServProfRn = nvl(
							fatVlrItemProcedHospComps.getVlrServProfissional(),
							BigDecimal.ZERO);
				} else {
					vVlrServHospRn = BigDecimal.ZERO;
					vVlrSadtRn = BigDecimal.ZERO;
					vVlrProcedRn = BigDecimal.ZERO;
					vVlrAnestRn = BigDecimal.ZERO;
					vVlrServProfRn = BigDecimal.ZERO;
				}

				vVlrProcedRn = BigDecimal.ZERO;

				// Busca proxima seq da tabela de espelho
				vEicSeqp = fatEspelhoItemContaHospDAO
						.buscaProximaSeqTabelaEspelho(pCthSeq);

				// Lanca procedimento de atendimento ao RN em sala de parto
				try {
					String cbo = null;

					// ney 01/09/2011
					if (CoreUtil.isMenorMesAno(pDtRealizado,
							INICIO_PORTARIA_203)) {
						// v_cbo := '223149';
						cbo = buscarVlrNumericoAghParametro(
								AghuParametrosEnum.P_COD_CBO_MEDICO_PEDIATRA_ATENDIMENTO_RN)
								.toString();
					} else {
						// v_cbo := '225124';
						cbo = buscarVlrNumericoAghParametro(
								AghuParametrosEnum.P_COD_CBO_MEDICO_PEDIATRA_ATENDIMENTO_RN_PORTARIA_203_2011)
								.toString();
					}

					logar("******** fatk_cth_rn_un ************");
					logar("v_cod_sus_rn:  ", vCodSusRn);
					logar("  ******************** ");

					final Long cpfCns = Long
							.valueOf(buscarVlrTextoAghParametro(AghuParametrosEnum.P_CPF_CNS_RESPONSAVEL_ATENDIMENTO_RN));

					BigDecimal vRnNascidoVivoBD = new BigDecimal(vRnNascidoVivo);

					FatEspelhoItemContaHosp espelho = new FatEspelhoItemContaHosp();
					espelho.setId(new FatEspelhoItemContaHospId(pCthSeq,vEicSeqp));
					espelho.setItemProcedimentoHospitalar(fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(vPhoRn, vIphRn)));
					
					espelho.setTivSeq(vTivSusRn != null ? vTivSusRn.byteValue(): null);
					espelho.setTaoSeq(vTaoSusRn);
					espelho.setQuantidade(vRnNascidoVivo);
					espelho.setProcedimentoHospitalarSus(vCodSusRn);
					espelho.setPontosAnestesista(vPtAnestRn * vRnNascidoVivo);
					espelho.setPontosCirurgiao(vPtCirRn * vRnNascidoVivo);
					espelho.setPontosSadt(vPtSadtRn * vRnNascidoVivo);
					espelho.setIndConsistente(true);
					espelho.setIndModoCobranca(DominioModoCobranca.V);
					espelho.setValorAnestesista(vRnNascidoVivoBD.multiply(vVlrAnestRn != null ? vVlrAnestRn: BigDecimal.ZERO));
					espelho.setValorProcedimento(vRnNascidoVivoBD.multiply(vVlrProcedRn != null ? vVlrProcedRn: BigDecimal.ZERO));
					espelho.setValorSadt(vRnNascidoVivoBD.multiply(vVlrSadtRn != null ? vVlrSadtRn: BigDecimal.ZERO));
					espelho.setValorServHosp(vRnNascidoVivoBD.multiply(vVlrServHospRn != null ? vVlrServHospRn: BigDecimal.ZERO));
					espelho.setValorServProf(vRnNascidoVivoBD.multiply(vVlrServProfRn != null ? vVlrServProfRn: BigDecimal.ZERO));
					espelho.setDataPrevia(pDataPrevia);
					espelho.setIndTipoItem(DominioTipoItemEspelhoContaHospitalar.D);
					espelho.setIndGeradoEncerramento(true);
					espelho.setIndEquipe((short) 0);
					espelho.setCbo(cbo);
					espelho.setCpfCns(cpfCns);

					faturamentoRN.inserirFatEspelhoItemContaHosp(espelho, true);
					//faturamentoFacade.evict(espelho);
				} catch (Exception e) {
					logar("ERRO INSERT atend rn EM fat_espelhos_itens_conta_hosp");

					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(69);
					faturamentoON
							.criarFatLogErrors(
									"ERRO AO INSERIR ESPELHO ITEM CONTA HOSPITALAR PARA ATENDIMENTO AO RN EM SALA DE PARTO: "
											+ e.getMessage(), INT, pCthSeq,
									null, null, vCodSusRn, null, pDataPrevia,
									null, null, pPhoSolic, pPhoRealiz,
									pIphSolic, pIphRealiz, pPacCodigo,
									pPhiSolic, pPhiRealiz, null,
									pPacProntuario, pCodSusRealiz,
									pCodSusSolic, vPhoRn, null, vIphRn, null,
									pPhiRealiz, null, RN_CTHC_ATU_REGRAPOS,
									null, null,fatMensagemLog);

					result = false;
				}
			} else {
				
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(123);
				faturamentoON
						.criarFatLogErrors(
								"NAO ENCONTROU CODIGO DE PROCEDIMENTO DE ATENDIMENTO AO RECEM NASCIDO PARA LANCAR AUTOMATICAMENTE.",
								INT, pCthSeq, null, null, null, null,
								pDataPrevia, null, null, pPhoSolic, pPhoRealiz,
								pIphSolic, pIphRealiz, pPacCodigo, pPhiSolic,
								pPhiRealiz, null, pPacProntuario,
								pCodSusRealiz, pCodSusSolic, null, null, null,
								null, null, null, RN_CTHC_ATU_REGRAPOS, null,
								null,fatMensagemLog);

				result = false;
			}
		}

		// Verifica se CID secundario e' obrigatorio p/ o realizado
		FatItensProcedHospitalar itemProcedimentoHospitalar = fatItensProcedHospitalarDAO
				.obterPorChavePrimaria(new FatItensProcedHospitalarId(
						pPhoRealiz, pIphRealiz));

		if (itemProcedimentoHospitalar != null) {
			vCidObrig = itemProcedimentoHospitalar.getCidadeObrigatoria();
		}

		vCidObrig = vCidObrig != null ? vCidObrig : false;

		// Verifica se o procedimento realizado exige CID Secundario e, neste
		// caso, se o cid secundario foi informado
//		if (CoreUtil
//				.igual(Boolean.TRUE, 
//						(Boolean) obterContextoSessao("FATK_CTH2_RN_UN_V_MAIOR_VALOR"))) {
		FatMensagemLog fatMensagemLog = new FatMensagemLog();
			
			if (CoreUtil
					.igual(Boolean.TRUE, fatVariaveisVO.getvMaiorValor() )) {
			logar("Maior valor {0}", pPhiRealiz);

			if (!this.rnCthcVerCidobrig(pCthSeq, pPhoRealiz, pIphRealiz)) {
				
				fatMensagemLog.setCodigo(236);
				
				
				faturamentoON.criarFatLogErrors(
						"REALIZADO EXIGE INFORMACAO DE CID SECUNDARIO.", INT,
						pCthSeq, null, null, null, null, pDataPrevia, null,
						null, pPhoSolic, pPhoRealiz, pIphSolic, pIphRealiz,
						pPacCodigo, pPhiSolic, pPhiRealiz, null,
						pPacProntuario, pCodSusRealiz, pCodSusSolic, null,
						null, null, null, null, null, RN_CTHC_ATU_REGRAPOS,
						null, null,fatMensagemLog);

				// erroSsmExigeSec = false;
				result = false;
			}

			// Para os ssm realizados com codigos SUS "80300235" e "80500234" o
			// CID secundario deve ser "N18.9"
			// "80500234" = INTERCORRENCIA PACIENTE RENAL CRONICO (CLINICA MED
			// "80300235" = INTERCORRENCIA PACIENTE RENAL CRONICO (PEDIATRIA)
			if (!this.rnCthcVerCidsecun(pCthSeq, pCodSusRealiz)) {
				fatMensagemLog.setCodigo(237);
				faturamentoON.criarFatLogErrors(
						"REALIZADO EXIGE QUE O CID SECUNDARIO SEJA \"N18.9\".",
						INT, pCthSeq, null, null, null, null, pDataPrevia,
						null, null, pPhoSolic, pPhoRealiz, pIphSolic,
						pIphRealiz, pPacCodigo, pPhiSolic, pPhiRealiz, null,
						pPacProntuario, pCodSusRealiz, pCodSusSolic, null,
						null, null, null, null, null, RN_CTHC_ATU_REGRAPOS,
						null, null,fatMensagemLog);

				// erroCidSsm = true;
				result = false;
			}

			// Verifica se CID Primario esta entre 'S00' e 'T98' o CID
			// secundario deve estar entre 'V01' e 'Y98'
			vCidFixo = this.rnCthcVerCidFixo(pCthSeq);

			logar("CID FIXO {0}", vCidFixo);
			if (vCidFixo != null && vCidFixo.intValue() == 0) {
				// Exige cid sec fixo e esta com erro
				

				fatMensagemLog.setCodigo(244);
				faturamentoON
						.criarFatLogErrors(
								"SE CID PRIMARIO ESTA ENTRE \"S00\" E \"T98\" O CID SECUNDARIO DEVE ESTAR ENTRE \"V01\" E \"Y98\".",
								INT, pCthSeq, null, null, null, null,
								pDataPrevia, null, null, null, null, pIphSolic,
								pIphRealiz, null, null, null, null,
								pPacProntuario, null, null, null, null, null,
								null, null, null, RN_CTHC_ATU_REGRAPOS, null,
								null,fatMensagemLog);

				// erroFxCid = true;
				result = false;
			}

			// Verifica se o procedimento realizado e' diferente de:
			// "80500170" = INSUFICIENCIA RENAL CRONICA. ACIDOSE METABOLICA
			// "80500056" = INSUFICIENCIA RENAL AGUDA
			// "80300154" = INSUFICIENCIA RENAL CRONICA. ACIDOSE METABOLICA
			// "80500234" = INTERCORRENCIA PACIENTE RENAL CRONICO (CLINICA MED
			// "80300235" = INTERCORRENCIA PACIENTE RENAL CRONICO (PEDIATRIA)
			// Se for, verifica se houve lancamento dos procedimentos:
			// "99041014","99041022","99041030" = HEMODIALISE
			// "99040018","99040026","99040034" = DIALISE PERITONEAL
			// pois nesse caso o cid secundario devera ser:
			// "N17.8" = OUTRO TIPO DE INSUFICIENCIA RENAL AGUDA

			vCidHemo = this.rnCthcAtuCidHemodial(pCthSeq, pCodSusRealiz);

			logar("CID HEMD {0}", vCidHemo);

			if (vCidHemo != null && vCidHemo.intValue() == 0) {
				// Exige cid hemod e esta com erro
				if (!vCidObrig) {
					try {
						
						fatMensagemLog.setCodigo(246);
						faturamentoON
								.criarFatLogErrors(
										"SE HA LANCAMENTO DE HEMODIALISE/DIALISE COM REALIZADO DIFERENTE DE 80500170,80500056,80300154,80500234',80300235 O CID SECUNDARIO DEVE SER \"N17.8\".",
										INT, pCthSeq, null, null, null, null,
										pDataPrevia, null, null, pPhoSolic,
										pPhoRealiz, pIphSolic, pIphRealiz,
										pPacCodigo, pPhiSolic, pPhiRealiz,
										null, pPacProntuario, pCodSusRealiz,
										pCodSusSolic, null, null, null, null,
										null, null, RN_CTHC_ATU_REGRAPOS,
										null, null,fatMensagemLog);
					} catch (Exception e) {
						logar(MSG_EXCECAO_IGNORADA,
								e);
					}

					// erroHemo = true;
					result = false;
				}
			}

			// Verifica se houve lancamento do procedimento
			// "94001014" = CRIOPRECIPITADO DE FATOR VII
			// pois nesse caso o cid secundario devera ser
			// "D68.0" = DEFEITO DE COAGULACAO NAO ESPECIFICADO
			if (!this.rnCthcAtuCidCriop(pCthSeq)) {
				// So exige cid para crio quando os 3 abaixo nao forem exigiveis
				if (!vCidObrig && vCidFixo == null && vCidHemo == null) {
					fatMensagemLog.setCodigo(245);
					faturamentoON
							.criarFatLogErrors(
									"SE HA LANCAMENTO DE CRIOPRECIPITADO O CID SECUNDARIO DEVE SER \"D68.0\".",
									INT, pCthSeq, null, null, null, null,
									pDataPrevia, null, null, pPhoSolic,
									pPhoRealiz, pIphSolic, pIphRealiz,
									pPacCodigo, pPhiSolic, pPhiRealiz, null,
									pPacProntuario, pCodSusRealiz,
									pCodSusSolic, null, null, null, null, null,
									null, "RN_CTHC_ATU_REGRAPOS", null, null,fatMensagemLog);

					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_REGRAESP
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NcssMethodCount",
			"PMD.NPathComplexity" })
	public RnCthcVerRegraespVO rnCthcVerRegraesp(Boolean pPrevia,
			Date pDataPrevia, Integer pCthSeq, Integer pPacCodigo,
			Integer pPacProntuario, Integer pIntSeq, Integer pPhiSolic,
			Short pPhoSolic, Integer pIphSolic, Long pCodSusSolic,
			Integer pPhiRealiz, Short pPhoRealiz, Integer pIphRealiz,
			Long pCodSusRealiz, Short pDiariasConta, Integer pDiariasUti,
			String pMotivoCobranca, Integer pCodExclusaoCritica, String nomeMicrocomputador,
			final Date dataFimVinculoServidor)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();
		
		final RnCthcVerRegraespVO retorno = new RnCthcVerRegraespVO();

		// busca para identificar procedimentos que poder cobrar junto outros
		// procedimentos (como multiplas)
		final Long vCodProcMultipla = buscarVlrNumericoAghParametro(
				AghuParametrosEnum.P_COD_PROC_MULTIPLA).longValue();

		// busca os itens lancados no espelho
		List<FatEspelhoItemContaHosp> listaItensEspelho = fatEspelhoItemContaHospDAO
				.listarItensLancadosEspelho(pCthSeq);
		if (listaItensEspelho != null && !listaItensEspelho.isEmpty()) {
			for (FatEspelhoItemContaHosp regEspelho : listaItensEspelho) {
				// verificar se o procedimento e' o realizado
				if (CoreUtil.igual(pCodSusRealiz,
						regEspelho.getProcedimentoHospitalarSus())) {
					logar("marca proced como local soma R: {0}",
							regEspelho.getProcedimentoHospitalarSus());
					regEspelho.setIndLocalSoma(DominioLocalSoma.R); // somar
																	// valor no
																	// realizado
					faturamentoFacade
							.atualizarFatEspelhoItemContaHosp(regEspelho);
				} else {
					logar("marca proced como local soma D: {0}",
							regEspelho.getProcedimentoHospitalarSus());
					regEspelho.setIndLocalSoma(DominioLocalSoma.D); // somar
																	// valor nos
																	// demais
																	// (nao no
																	// realizado)
					faturamentoFacade
							.atualizarFatEspelhoItemContaHosp(regEspelho);
				}
				//faturamentoFacade.evict(regEspelho);
			}
		}

		// busca indicadores de procedimento realizado (do espelho AIH)
		// de cirurgia multipla ou de AIDS/politraumatizado/busca ativa doador:
		Boolean vCirmultPolitr = Boolean.FALSE;
		Boolean vAids = Boolean.FALSE;
		Boolean vBuscaDoador = Boolean.FALSE;

		FatItensProcedHospitalar vItemProcedHospitalar = fatItensProcedHospitalarDAO
				.obterPorChavePrimaria(new FatItensProcedHospitalarId(
						pPhoRealiz, pIphRealiz));
		if (vItemProcedHospitalar != null) {
			vAids = vItemProcedHospitalar.getAidsPolitraumatizado();
			vBuscaDoador = vItemProcedHospitalar.getBuscaDoador();

			if (CoreUtil.igual(pCodSusRealiz, vCodProcMultipla)) {
				vCirmultPolitr = Boolean.TRUE;

			} else {
				vCirmultPolitr = vItemProcedHospitalar.getCirurgiaMultipla();
			}
		}

		FatEspelhoItemContaHosp regEspelho = null;
		// verifica se o realizado e' de cirurgia multila ou politrumatizado
		if (vCirmultPolitr) {
			logar("eh cirmult politr");
			Integer vQtdSsms = 0;

			// busca os itens lancados no espelho
			if (listaItensEspelho != null && !listaItensEspelho.isEmpty()) {
				for (Iterator<FatEspelhoItemContaHosp> iterator = listaItensEspelho
						.iterator(); iterator.hasNext();) {
					regEspelho = iterator.next();
					// verificar se o procedimento e' uma nosologia (SSM)
					Boolean vIndInternacao = regEspelho
							.getItemProcedimentoHospitalar() != null ? regEspelho
							.getItemProcedimentoHospitalar().getInternacao()
							: Boolean.FALSE;
					if (vIndInternacao
							&& (!CoreUtil.igual(
									regEspelho.getProcedimentoHospitalarSus(),
									pCodSusRealiz) || (CoreUtil.igual(
									regEspelho.getProcedimentoHospitalarSus(),
									pCodSusRealiz) && CoreUtil.igual(
									pCodSusRealiz, vCodProcMultipla)))

					) {

						vQtdSsms++;

						if (!CoreUtil.igual(pCodSusRealiz, vCodProcMultipla)) {
							regEspelho.setIndLocalSoma(DominioLocalSoma.R); // somar
																			// valor
																			// no
																			// realizado
							faturamentoFacade
									.atualizarFatEspelhoItemContaHosp(regEspelho);
						}

						logar("eh cirur/politraum IEC: {0} PHO {1} IPH {2} SUS {3}",
								regEspelho.getId().getSeqp(), regEspelho
										.getItemProcedimentoHospitalar()
										.getId().getPhoSeq(), regEspelho
										.getItemProcedimentoHospitalar()
										.getId().getSeq(),
								regEspelho.getProcedimentoHospitalarSus());

					}
				}
			}

			if (CoreUtil.menor(vQtdSsms, 2)) {
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma(RN_CTHC_VER_REGRAESP);
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setDataPrevia(pDataPrevia);
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setProntuario(pPacProntuario);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("PROCEDIMENTO REALIZADO EXIGE LANCAMENTO DE NO MINIMO 2 SSM.");
					
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(215);
					erro.setFatMensagemLog(fatMensagemLog);
					
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA,
							e);
				}

				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}

			// verifica se o realizado e' de tratamento de AIDS
		} else if (vAids) {
			logar("eh aids");

			// busca limite inicial dos codigos permitidos p/ AIDS
			final long vCodIniAids = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_INICIAL_AIDS).longValue();
			final long vCodFinAids = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_FINAL_AIDS).longValue();

			Integer vQtdSsms = 0;
			Integer vQtdSsmsAids = 0;
			// busca os itens lancados no espelho
			if (listaItensEspelho != null && !listaItensEspelho.isEmpty()) {
				for (Iterator<FatEspelhoItemContaHosp> iterator = listaItensEspelho
						.iterator(); iterator.hasNext();) {
					regEspelho = iterator.next();
					// verificar se o procedimento e' uma nosologia (SSM)
					Boolean vIndInternacao = regEspelho
							.getItemProcedimentoHospitalar() != null ? regEspelho
							.getItemProcedimentoHospitalar().getInternacao()
							: Boolean.FALSE;
					if (vIndInternacao
							&& !CoreUtil.igual(
									regEspelho.getProcedimentoHospitalarSus(),
									pCodSusRealiz)) {
						vQtdSsms++;

						if (regEspelho.getProcedimentoHospitalarSus() != null
								&& (CoreUtil.menor(regEspelho
										.getProcedimentoHospitalarSus(),
										vCodIniAids) || CoreUtil
										.maior(regEspelho
												.getProcedimentoHospitalarSus(),
												vCodFinAids))) {
							try {
								FatLogError erro = new FatLogError();
								erro.setModulo(INT);
								erro.setPrograma(RN_CTHC_VER_REGRAESP);
								erro.setCriadoEm(new Date());
								erro.setCriadoPor(servidorLogado.getUsuario());
								erro.setDataPrevia(pDataPrevia);
								erro.setCthSeq(pCthSeq);
								erro.setPacCodigo(pPacCodigo);
								erro.setProntuario(pPacProntuario);
								erro.setPhiSeq(pPhiSolic);
								erro.setIphPhoSeq(pPhoSolic);
								erro.setIphSeq(pIphSolic);
								erro.setCodItemSusSolicitado(pCodSusSolic);
								erro.setPhiSeqRealizado(pPhiRealiz);
								erro.setIphPhoSeqRealizado(pPhoRealiz);
								erro.setIphSeqRealizado(pIphRealiz);
								erro.setCodItemSusRealizado(pCodSusRealiz);
								erro.setIphPhoSeqItem1(regEspelho
										.getItemProcedimentoHospitalar() != null ? regEspelho
										.getItemProcedimentoHospitalar()
										.getId().getPhoSeq()
										: null);
								erro.setIphSeqItem1(regEspelho
										.getItemProcedimentoHospitalar() != null ? regEspelho
										.getItemProcedimentoHospitalar()
										.getId().getSeq()
										: null);
								erro.setCodItemSus1(regEspelho
										.getProcedimentoHospitalarSus());
								erro.setErro("PROCEDIMENTO REALIZADO NAO PERMITE LANCAMENTO DO SSM.");
								
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(221);
								erro.setFatMensagemLog(fatMensagemLog);
								
								persistirLogErroCarregandoFatMensagemLog(
										faturamentoFacade, erro);
							} catch (Exception e) {
								logar(MSG_EXCECAO_IGNORADA,
										e);
							}
							retorno.setRetorno(Boolean.FALSE);
							return retorno;

						} else { // e' um dos ssms permitidos
							vQtdSsmsAids++;
							regEspelho.setIndLocalSoma(DominioLocalSoma.R); // somar
																			// valor
																			// no
																			// realizado
							faturamentoFacade
									.atualizarFatEspelhoItemContaHosp(regEspelho);
							logar("eh aids IEC: {0} PHO {1} IPH {2} SUS {3}",
									regEspelho.getId().getSeqp(), regEspelho
											.getItemProcedimentoHospitalar()
											.getId().getPhoSeq(), regEspelho
											.getItemProcedimentoHospitalar()
											.getId().getSeq(),
									regEspelho.getProcedimentoHospitalarSus());
						}
					} // v_ind_internacao = 'S'
				}
			}

			if (CoreUtil.menor(vQtdSsmsAids, 1)) {
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma(RN_CTHC_VER_REGRAESP);
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setDataPrevia(pDataPrevia);
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setProntuario(pPacProntuario);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("PROCEDIMENTO REALIZADO EXIGE LANCAMENTO DE NO MINIMO 1 SSM DE TRATAMENTO DE AIDS.");
					
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(214);
					erro.setFatMensagemLog(fatMensagemLog);
					
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA,
							e);
				}
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}

			if (CoreUtil.maior(vQtdSsms, 4)) {
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma(RN_CTHC_VER_REGRAESP);
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setDataPrevia(pDataPrevia);
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setProntuario(pPacProntuario);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("PROCEDIMENTO REALIZADO PERMITE LANCAMENTO DE NO MAXIMO 4 SSM.");
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(225);
					erro.setFatMensagemLog(fatMensagemLog);
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA,
							e);
				}
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}

		} else if (vBuscaDoador) { // verifica se o realizado e' de busca ativa
									// de doador de orgaos
			logar("eh busca doador");

			// busca limite inicial dos codigos permitidos p/busca ativa doador
			// orgaos
			final Long vCodIniBusca = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_INICIAL_BUSCA_DOADOR).longValue();
			// busca limite final dos codigos permitidos p/busca ativa doador
			// orgaos
			final Long vCodFinBusca = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_FINAL_BUSCA_DOADOR).longValue();
			// busca um dos codigos permitidos p/ busca ativa de doador de
			// orgaos
			final Long vCodMeioBusca = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_MEIO_BUSCA_DOADOR).longValue();

			Integer vQtdSsms = 0;

			Boolean achouCodIniBusca = Boolean.FALSE;
			Boolean achouCodMeioBusca = Boolean.FALSE;
			Boolean achouCodFinBusca = Boolean.FALSE;

			// busca os itens lancados no espelho
			if (listaItensEspelho != null && !listaItensEspelho.isEmpty()) {
				for (Iterator<FatEspelhoItemContaHosp> iterator = listaItensEspelho
						.iterator(); iterator.hasNext();) {
					regEspelho = iterator.next();
					// verificar se o procedimento e' uma nosologia (SSM)
					Boolean vIndInternacao = regEspelho
							.getItemProcedimentoHospitalar() != null ? regEspelho
							.getItemProcedimentoHospitalar().getInternacao()
							: Boolean.FALSE;
					if (vIndInternacao
							&& !CoreUtil.igual(
									regEspelho.getProcedimentoHospitalarSus(),
									pCodSusRealiz)) {
						vQtdSsms++;
						regEspelho.setIndLocalSoma(DominioLocalSoma.R); // somar
																		// valor
																		// no
																		// realizado
						faturamentoFacade
								.atualizarFatEspelhoItemContaHosp(regEspelho);
						logar("eh busca doador IEC: {0} PHO {1} IPH {2} SUS {3}",
								regEspelho.getId().getSeqp(), regEspelho
										.getItemProcedimentoHospitalar()
										.getId().getPhoSeq(), regEspelho
										.getItemProcedimentoHospitalar()
										.getId().getSeq(),
								regEspelho.getProcedimentoHospitalarSus());

						// verifica se foi lancado o codigo obrigatorio p/busca
						// ativa doador
						if (CoreUtil.igual(
								regEspelho.getProcedimentoHospitalarSus(),
								vCodIniBusca)) {
							achouCodIniBusca = Boolean.TRUE;
						}

						// verifica se foi lancado um dos outros 2 codigos
						// obrigatorios p/busca ativa doador
						if (CoreUtil.igual(
								regEspelho.getProcedimentoHospitalarSus(),
								vCodMeioBusca)) {
							achouCodMeioBusca = Boolean.TRUE;
						}
						if (CoreUtil.igual(
								regEspelho.getProcedimentoHospitalarSus(),
								vCodFinBusca)) {
							achouCodFinBusca = Boolean.TRUE;
						}

						// verifica se o procedimento e' um dos permitidos p/ o
						// tratamento
						if (CoreUtil.igual(
								regEspelho.getProcedimentoHospitalarSus(),
								pCodSusRealiz)
								&& (regEspelho.getProcedimentoHospitalarSus() != null
										&& CoreUtil
												.menor(regEspelho
														.getProcedimentoHospitalarSus(),
														vCodIniBusca) || CoreUtil
										.maior(regEspelho
												.getProcedimentoHospitalarSus(),
												vCodFinBusca))) {
							try {
								FatLogError erro = new FatLogError();
								erro.setModulo(INT);
								erro.setPrograma(RN_CTHC_VER_REGRAESP);
								erro.setCriadoEm(new Date());
								erro.setCriadoPor(servidorLogado.getUsuario());
								erro.setDataPrevia(pDataPrevia);
								erro.setCthSeq(pCthSeq);
								erro.setPacCodigo(pPacCodigo);
								erro.setProntuario(pPacProntuario);
								erro.setPhiSeq(pPhiSolic);
								erro.setIphPhoSeq(pPhoSolic);
								erro.setIphSeq(pIphSolic);
								erro.setCodItemSusSolicitado(pCodSusSolic);
								erro.setPhiSeqRealizado(pPhiRealiz);
								erro.setIphPhoSeqRealizado(pPhoRealiz);
								erro.setIphSeqRealizado(pIphRealiz);
								erro.setCodItemSusRealizado(pCodSusRealiz);
								erro.setIphPhoSeqItem1(regEspelho
										.getItemProcedimentoHospitalar() != null ? regEspelho
										.getItemProcedimentoHospitalar()
										.getId().getPhoSeq()
										: null);
								erro.setIphSeqItem1(regEspelho
										.getItemProcedimentoHospitalar() != null ? regEspelho
										.getItemProcedimentoHospitalar()
										.getId().getSeq()
										: null);
								erro.setCodItemSus1(regEspelho
										.getProcedimentoHospitalarSus());
								erro.setErro("PROCEDIMENTO REALIZADO NAO PERMITE LANCAMENTO DO SSM.");
								persistirLogErroCarregandoFatMensagemLog(
										faturamentoFacade, erro);
							} catch (Exception e) {
								logar(MSG_EXCECAO_IGNORADA,
										e);
							}
							retorno.setRetorno(Boolean.FALSE);
							return retorno;
						}
					} // v_ind_internacao = 'S'
				} // end loop;
			}

			if (CoreUtil.maior(vQtdSsms, 2)) {
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma(RN_CTHC_VER_REGRAESP);
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setDataPrevia(pDataPrevia);
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setProntuario(pPacProntuario);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("PROCEDIMENTO REALIZADO PERMITE LANCAMENTO DE NO MAXIMO 2 SSM.");
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(224);
					erro.setFatMensagemLog(fatMensagemLog);
					
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA,
							e);
				}
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}

			// verifica se foi lancado o codigo obrigatorio p/busca ativa doador
			if (!achouCodIniBusca) {
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma(RN_CTHC_VER_REGRAESP);
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setDataPrevia(pDataPrevia);
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setProntuario(pPacProntuario);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("PROCEDIMENTO REALIZADO EXIGE LANCAMENTO DO SSM "
							+ vCodIniBusca + ".");
					
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(217);
					erro.setFatMensagemLog(fatMensagemLog);
					
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA,
							e);
				}
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			} else { // if not achou_cod_ini_busca

				if (!achouCodMeioBusca && !achouCodFinBusca) {
					try {
						FatLogError erro = new FatLogError();
						erro.setModulo(INT);
						erro.setPrograma(RN_CTHC_VER_REGRAESP);
						erro.setCriadoEm(new Date());
						erro.setCriadoPor(servidorLogado.getUsuario());
						erro.setDataPrevia(pDataPrevia);
						erro.setCthSeq(pCthSeq);
						erro.setPacCodigo(pPacCodigo);
						erro.setProntuario(pPacProntuario);
						erro.setPhiSeq(pPhiSolic);
						erro.setIphPhoSeq(pPhoSolic);
						erro.setIphSeq(pIphSolic);
						erro.setCodItemSusSolicitado(pCodSusSolic);
						erro.setPhiSeqRealizado(pPhiRealiz);
						erro.setIphPhoSeqRealizado(pPhoRealiz);
						erro.setIphSeqRealizado(pIphRealiz);
						erro.setCodItemSusRealizado(pCodSusRealiz);
						erro.setErro("PROCEDIMENTO REALIZADO EXIGE LANCAMENTO DE UM DOS SSM: "
								+ vCodMeioBusca + " OU " + vCodFinBusca + ".");
						
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(216);
						erro.setFatMensagemLog(fatMensagemLog);
						
						persistirLogErroCarregandoFatMensagemLog(
								faturamentoFacade, erro);
					} catch (Exception e) {
						logar(MSG_EXCECAO_IGNORADA,
								e);
					}
					retorno.setRetorno(Boolean.FALSE);
					return retorno;
				}

				// verifica se foi lancado um dos outros 2 codigos obrigatorios
				// p/busca ativa doador
				if (achouCodMeioBusca && achouCodFinBusca) {
					try {
						FatLogError erro = new FatLogError();
						erro.setModulo(INT);
						erro.setPrograma(RN_CTHC_VER_REGRAESP);
						erro.setCriadoEm(new Date());
						erro.setCriadoPor(servidorLogado.getUsuario());
						erro.setDataPrevia(pDataPrevia);
						erro.setCthSeq(pCthSeq);
						erro.setPacCodigo(pPacCodigo);
						erro.setProntuario(pPacProntuario);
						erro.setPhiSeq(pPhiSolic);
						erro.setIphPhoSeq(pPhoSolic);
						erro.setIphSeq(pIphSolic);
						erro.setCodItemSusSolicitado(pCodSusSolic);
						erro.setPhiSeqRealizado(pPhiRealiz);
						erro.setIphPhoSeqRealizado(pPhoRealiz);
						erro.setIphSeqRealizado(pIphRealiz);
						erro.setCodItemSusRealizado(pCodSusRealiz);
						erro.setErro("PROCEDIMENTO REALIZADO PERMITE LANCAMENTO DE APENAS UM DOS SSM: "
								+ vCodMeioBusca + " OU " + vCodFinBusca + ".");
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(222);
						erro.setFatMensagemLog(fatMensagemLog);
						
						persistirLogErroCarregandoFatMensagemLog(
								faturamentoFacade, erro);
					} catch (Exception e) {
						logar(MSG_EXCECAO_IGNORADA,
								e);
					}
					retorno.setRetorno(Boolean.FALSE);
					return retorno;
				} // not achou_cod_ini_busca
			}
		} else {
			logar("nao era tratam especial");
			// verifica se tem mais de uma nosologia lancada
			// pois, fora os casos acima, so' pode ter 1 SSM lancado

			Integer vQtdSsms = 0;

			// busca os itens lancados no espelho
			if (listaItensEspelho != null && !listaItensEspelho.isEmpty()) {
				for (Iterator<FatEspelhoItemContaHosp> iterator = listaItensEspelho
						.iterator(); iterator.hasNext();) {
					regEspelho = iterator.next();
					// verificar se o procedimento e' o realizado
					Boolean vIndInternacao = regEspelho
							.getItemProcedimentoHospitalar() != null ? regEspelho
							.getItemProcedimentoHospitalar().getInternacao()
							: Boolean.FALSE;
					if (CoreUtil.igual(vIndInternacao, Boolean.TRUE)) {
						vQtdSsms++;
						logar("eh SSM IEC: {0} PHO {1} IPH {2} SUS {3}",
								regEspelho.getId().getSeqp(), regEspelho
										.getItemProcedimentoHospitalar()
										.getId().getPhoSeq(), regEspelho
										.getItemProcedimentoHospitalar()
										.getId().getSeq(),
								regEspelho.getProcedimentoHospitalarSus());
					}
				}
			}

			if (CoreUtil.maior(vQtdSsms, 1)) {
				try {
					FatLogError erro = new FatLogError();
					erro.setModulo(INT);
					erro.setPrograma(RN_CTHC_VER_REGRAESP);
					erro.setCriadoEm(new Date());
					erro.setCriadoPor(servidorLogado.getUsuario());
					erro.setDataPrevia(pDataPrevia);
					erro.setCthSeq(pCthSeq);
					erro.setPacCodigo(pPacCodigo);
					erro.setProntuario(pPacProntuario);
					erro.setPhiSeq(pPhiSolic);
					erro.setIphPhoSeq(pPhoSolic);
					erro.setIphSeq(pIphSolic);
					erro.setCodItemSusSolicitado(pCodSusSolic);
					erro.setPhiSeqRealizado(pPhiRealiz);
					erro.setIphPhoSeqRealizado(pPhoRealiz);
					erro.setIphSeqRealizado(pIphRealiz);
					erro.setCodItemSusRealizado(pCodSusRealiz);
					erro.setErro("PROCEDIMENTO REALIZADO PERMITE LANCAMENTO DE APENAS UM SSM.");
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(223);
					erro.setFatMensagemLog(fatMensagemLog);
					persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA,
							e);
				}
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}
		}

		// Busca Procedimento a ser usado no calculo da permanencia maior e
		// menor:
		// (o normal e' o proprio realiz ser utilizado no calculo)

		Short vPhoPerm = pPhoRealiz;
		Integer vIphPerm = pIphRealiz;

		if (vCirmultPolitr || vAids || vBuscaDoador) {
			// verifica se o realizado e' de cirurgia multila ou politrumatizado
			// ou tratamento de AIDS ou busca ativa de doador de orgaos pois
			// neses casos
			// deve utilizar no calculo da PermMaior e PermMenor o primeiro SSM
			// do CMA
			List<FatEspelhoItemContaHosp> listaItensAgrup = fatEspelhoItemContaHospDAO
					.listarItensAgrup(pCthSeq, pPrevia);
			if (listaItensAgrup != null && !listaItensAgrup.isEmpty()) {
				for (FatEspelhoItemContaHosp regItem : listaItensAgrup) {
					// verificar se o procedimento e' uma nosologia (SSM)
					Boolean vIndInternacao = regItem
							.getItemProcedimentoHospitalar() != null ? regItem
							.getItemProcedimentoHospitalar().getInternacao()
							: Boolean.FALSE;
					if (vIndInternacao) {
						vPhoPerm = regItem.getItemProcedimentoHospitalar()
								.getId().getPhoSeq();
						vIphPerm = regItem.getItemProcedimentoHospitalar()
								.getId().getSeq();
						logar("proced p/ calculo PermMaior e PermMenor: IEC {0} PHO {1} IPH {2} SUS {3}",
								(regEspelho != null ? regEspelho.getId()
										.getSeqp() : null), vPhoPerm, vIphPerm,
								regItem.getProcedimentoHospitalarSus());
						retorno.setRetorno(Boolean.FALSE);
						//faturamentoFacade.evict(regItem);
						break;
					}
					//faturamentoFacade.evict(regItem);
				}
			}
		}

		RnCthcAtuPermaiorVO rnCthcAtuPermaiorVO = this.rnCthcAtuPermaior(
				pPrevia, pDataPrevia, pCthSeq, pPacCodigo, pPacProntuario,
				pIntSeq, pPhiSolic, pPhoSolic, pIphSolic, pCodSusSolic,
				pPhiRealiz, pPhoRealiz, pIphRealiz, pCodSusRealiz,
				pDiariasConta, pDiariasUti, vPhoPerm, vIphPerm,
				pMotivoCobranca, pCodExclusaoCritica, nomeMicrocomputador, dataFimVinculoServidor);
		if (rnCthcAtuPermaiorVO != null && !rnCthcAtuPermaiorVO.getRetorno()) {
			retorno.setRetorno(Boolean.FALSE);
			return retorno;
		}

		retorno.setCodExclusaoCritica(rnCthcAtuPermaiorVO
				.getCodExclusaoCritica() != null ? rnCthcAtuPermaiorVO
				.getCodExclusaoCritica() : null);
		retorno.setRetorno(Boolean.TRUE);
		return retorno;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_ATU_QTDMODUL
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public Boolean rnCthcAtuQtdmodul(Boolean pPrevia, Date pDataPrevia,
			Integer pCthSeq, Integer pPacCodigo, Integer pPacProntuario,
			Integer pIntSeq, Integer pPhiSolic, Short pPhoSolic,
			Integer pIphSolic, Long pCodSusSolic, Integer pPhiRealiz,
			Short pPhoRealiz, Integer pIphRealiz, Long pCodSusRealiz)
			throws BaseException {
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FaturamentoON faturamentoON = getFaturamentoON();
		final FaturamentoRN faturamentoRN = getFaturamentoRN();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();

		boolean result = false;

		BuscaTotalHemoterapiasQueExigemLancamentoModuloTransfusionalVO regHemat = null;
		FatEspelhoItemContaHosp espelhoItemContaHospitalar = null;
		Integer qtdModuloTotal = 0;
		Integer qtdHematTotal = 0;
		Integer qtdHematCobra = 0;
		Integer qtdHematSobra = 0;
		Integer qtdModuloSobra = 0;
		Short vNovaQtd = 0;
		BigDecimal vVlrShUnit = BigDecimal.ZERO;
		BigDecimal vVlrSpUnit = BigDecimal.ZERO;
		BigDecimal vVlrSadtUnit = BigDecimal.ZERO;
		BigDecimal vVlrAnestUnit = BigDecimal.ZERO;
		BigDecimal vVlrProcedUnit = BigDecimal.ZERO;
		Integer vPtAnestUnit = 0;
		Integer vPtCirurUnit = 0;
		Integer vPtSadtUnit = 0;
		Long vCodSusModuloTransf = null;
		Short vPhoModuloTransf = null;
		Integer vIphModuloTransf = null;
		Integer vMaxQtd = null;

		// Busca seq do item correspondente ao modulo transfusional 94020019l; // Modulo Transfusional
		// TODO Parametrizar este valor
		vCodSusModuloTransf = buscarVlrLongAghParametro(AghuParametrosEnum.P_AGHU_COD_TABELA_MODULO_TRANSFUSIONAL);

		FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO
				.buscarPrimeiroItemProcedimentosHospitalares(
						vCodSusModuloTransf, DominioSituacao.A);
		if (fatItensProcedHospitalar != null) {
			vPhoModuloTransf = fatItensProcedHospitalar.getId().getPhoSeq();
			vIphModuloTransf = fatItensProcedHospitalar.getId().getSeq();
		}

		logar("modulo transfusional pho:{0} iph:{1}", vPhoModuloTransf,
				vIphModuloTransf);

		if (vPhoModuloTransf == null || vIphModuloTransf == null) {
			logar("erro na busca do iph do modulo");
			//TODO new fatmensagemlog incluido apenas para não apontar erro
			try {
				faturamentoON
						.criarFatLogErrors(
								"NAO ENCONTROU CODIGO DO PROCEDIMENTO MODULO TRANSFUSIONAL.",
								INT, pCthSeq, null, null, null, null,
								pDataPrevia, null, null, pPhoSolic, pPhoRealiz,
								pIphSolic, pIphRealiz, pPacCodigo, pPhiSolic,
								pPhiRealiz, null, pPacProntuario,
								pCodSusRealiz, pCodSusSolic, null, null, null,
								null, null, null, "RN_CTHC_ATU_QTDMODUL", null,
								null,new FatMensagemLog());
			} catch (Exception e) {
				logar(MSG_EXCECAO_IGNORADA,
						e);
			}

			result = false;
		} else {
			// Busca qtd total de hemoterapias lancados no espelho que exigem
			// lancamento de modulo transfusional
			List<BuscaTotalHemoterapiasQueExigemLancamentoModuloTransfusionalVO> listaVOs = fatEspelhoItemContaHospDAO
					.buscaTotalHemoterapiasQueExigemLancamentoModuloTransfusional(
							pCthSeq, vPhoModuloTransf, vIphModuloTransf);

			if (listaVOs != null && !listaVOs.isEmpty()) {
				for (int i = 0; i < listaVOs.size(); i++) {
					regHemat = listaVOs.get(i);

					qtdHematTotal += regHemat.getQuantidade();

					FatItensProcedHospitalar fatItensProcedHospitalarQtd = fatItensProcedHospitalarDAO
							.obterPorChavePrimaria(new FatItensProcedHospitalarId(
									regHemat.getIphPhoSeq(), regHemat
											.getIphSeq()));
					if (fatItensProcedHospitalarQtd != null) {
						vMaxQtd = fatItensProcedHospitalarQtd.getMaxQtdConta() != null ? fatItensProcedHospitalarQtd
								.getMaxQtdConta().intValue() : null;
					}

					logar("pho: {0} iph: {1} sus: {2} qtd: {3} max: {4}",
							regHemat.getIphPhoSeq(), regHemat.getIphSeq(),
							regHemat.getIphCodSus(), regHemat.getQuantidade(),
							vMaxQtd);

					if (CoreUtil.maior(regHemat.getQuantidade(), vMaxQtd)) {
						qtdHematCobra += vMaxQtd;
						qtdHematSobra += (regHemat.getQuantidade() - vMaxQtd);
						logar("qtd hemat cobra: {0} sobra: {1}", qtdHematCobra,
								qtdHematSobra);
					} else {
						qtdHematCobra += regHemat.getQuantidade();
					}
				}
			}

			logar("qtd hemat total:{0} cobra:{1} sobra:{2}", qtdHematTotal,
					qtdHematCobra, qtdHematSobra);

			// Verifica se foi lancado qtd de modulos maior do que podera cobrar
			// de hemoterapias
			if (!CoreUtil.igual(qtdHematTotal, qtdHematCobra)) {
				// Busca qtd total de modulos transfusionais lancados no espelho
				qtdModuloTotal = fatEspelhoItemContaHospDAO
						.sumatorioEspelhoItensContaHospitalar(pCthSeq,
								vCodSusModuloTransf, true);

				logar("qtd modulo total: {0}", qtdModuloTotal);

				// Verifica se tem mais modulos do que hemoterapias lancadas
				if (CoreUtil.maior(qtdModuloTotal, qtdHematCobra)) {
					qtdModuloSobra = qtdModuloTotal - qtdHematCobra;
					logar("qtd modulo sobra: {0}", qtdModuloSobra);

					// Vai atualizar qtdes de modulo transfusional
					List<FatEspelhoItemContaHosp> listaEspelhosItensContaHospitalar = fatEspelhoItemContaHospDAO
							.listarEspelhosItensContaHospitalarOrdenadoPorQuantidadeDesc(
									pCthSeq, vCodSusModuloTransf, true);

					if (listaEspelhosItensContaHospitalar != null
							&& !listaEspelhosItensContaHospitalar.isEmpty()) {
						for (int i = 0; i < listaEspelhosItensContaHospitalar
								.size(); i++) {
							espelhoItemContaHospitalar = listaEspelhosItensContaHospitalar
									.get(i);

							if (qtdModuloSobra != null
									&& qtdModuloSobra.intValue() == 0) {
								faturamentoFacade.evict(espelhoItemContaHospitalar);
								break;
							}

							Short quantidade = espelhoItemContaHospitalar.getQuantidade() != null ? espelhoItemContaHospitalar.getQuantidade() : 0;
							Integer pontosAnestesista = espelhoItemContaHospitalar.getPontosAnestesista() != null ? espelhoItemContaHospitalar.getPontosAnestesista() : 0;
							Integer pontosCirurgiao = espelhoItemContaHospitalar.getPontosCirurgiao() != null ? espelhoItemContaHospitalar.getPontosCirurgiao() : 0;
							Integer pontosSadt = espelhoItemContaHospitalar.getPontosSadt() != null ? espelhoItemContaHospitalar.getPontosSadt() : 0;
							BigDecimal valorServHosp = espelhoItemContaHospitalar.getValorServProf() != null ? espelhoItemContaHospitalar.getValorServProf() : BigDecimal.ZERO;
							BigDecimal valorSadt = espelhoItemContaHospitalar.getValorSadt() != null ? espelhoItemContaHospitalar.getValorSadt() : BigDecimal.ZERO;
							BigDecimal valorProcedimento = espelhoItemContaHospitalar.getValorProcedimento() != null ? espelhoItemContaHospitalar.getValorProcedimento() : BigDecimal.ZERO;
							BigDecimal valorAnestesista = espelhoItemContaHospitalar.getValorAnestesista() != null ? espelhoItemContaHospitalar.getValorAnestesista() : BigDecimal.ZERO;

							logar("qtd modulo: {0} qtd modulo sobra: {1}",quantidade, qtdModuloSobra);

							if (CoreUtil.maiorOuIgual(quantidade,qtdModuloSobra)) {
								BigDecimal quantidadeBD = new BigDecimal(quantidade);

								vVlrShUnit = valorServHosp.divide(quantidadeBD);
								vVlrSpUnit = valorServHosp.divide(quantidadeBD);
								vVlrSadtUnit = valorSadt.divide(quantidadeBD);
								vVlrAnestUnit = valorAnestesista.divide(quantidadeBD);
								vVlrProcedUnit = valorProcedimento.divide(quantidadeBD);
								vPtAnestUnit = pontosAnestesista / quantidade;
								vPtCirurUnit = pontosCirurgiao / quantidade;
								vPtSadtUnit = pontosSadt / quantidade;
								vNovaQtd = (short) (quantidade - qtdModuloSobra);

								BigDecimal vNovaQtdBD = new BigDecimal(vNovaQtd);

								espelhoItemContaHospitalar.setQuantidade(vNovaQtd);
								espelhoItemContaHospitalar.setValorServHosp(vNovaQtdBD.multiply(vVlrShUnit));
								espelhoItemContaHospitalar.setValorServProf(vNovaQtdBD.multiply(vVlrSpUnit));
								espelhoItemContaHospitalar.setValorSadt(vNovaQtdBD.multiply(vVlrSadtUnit));
								espelhoItemContaHospitalar.setValorAnestesista(vNovaQtdBD.multiply(vVlrAnestUnit));
								espelhoItemContaHospitalar.setValorProcedimento(vNovaQtdBD.multiply(vVlrProcedUnit));
								espelhoItemContaHospitalar.setPontosAnestesista(vPtAnestUnit* vNovaQtd);
								espelhoItemContaHospitalar.setPontosCirurgiao(vPtCirurUnit* vNovaQtd);
								espelhoItemContaHospitalar.setPontosSadt(vPtSadtUnit * vNovaQtd);

								faturamentoRN.atualizarFatEspelhoItemContaHosp(espelhoItemContaHospitalar, true);

								qtdModuloSobra = 0;
							} else {
								qtdModuloSobra = qtdModuloSobra - quantidade;

								espelhoItemContaHospitalar.setQuantidade((short) 0);
								espelhoItemContaHospitalar.setValorServHosp(BigDecimal.ZERO);
								espelhoItemContaHospitalar.setValorServProf(BigDecimal.ZERO);
								espelhoItemContaHospitalar.setValorSadt(BigDecimal.ZERO);
								espelhoItemContaHospitalar.setValorAnestesista(BigDecimal.ZERO);
								espelhoItemContaHospitalar.setValorProcedimento(BigDecimal.ZERO);
								espelhoItemContaHospitalar.setPontosAnestesista(0);
								espelhoItemContaHospitalar.setPontosCirurgiao(0);
								espelhoItemContaHospitalar.setPontosSadt(0);

								faturamentoRN.atualizarFatEspelhoItemContaHosp(espelhoItemContaHospitalar, true);
							}
							//faturamentoFacade.evict(espelhoItemContaHospitalar);
						}
					}
				}
			}

			result = true;
		}

		return result;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHP_ATU_SITUACAO
	 * 
	 * @throws BaseException
	 */
	public void rnCthpAtuSituacao(Boolean pPrevia,
			DominioSituacaoConta pSituacao, Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();

		if (!pPrevia) {
			FatContasHospitalares fatContasHospitalares = fatContasHospitalaresDAO
					.obterPorChavePrimaria(pCthSeq);
			if (fatContasHospitalares != null
					&& (CoreUtil.igual(DominioSituacaoConta.A,
							fatContasHospitalares.getIndSituacao()) || CoreUtil
							.igual(DominioSituacaoConta.F,
									fatContasHospitalares.getIndSituacao()))) {
				FatContasHospitalares contaOld = null;
				try {
					contaOld = faturamentoFacade
							.clonarContaHospitalar(fatContasHospitalares);
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					throw new BaseException(
							FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
				}
				fatContasHospitalares.setIndSituacao(pSituacao);
				fatContasHospitalares.setDtEncerramento(new Date());
				faturamentoFacade.persistirContaHospitalar(
						fatContasHospitalares, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
				//faturamentoFacade.evict(fatContasHospitalares);
			}
		}
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_ATU_PERMAIOR
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public RnCthcAtuPermaiorVO rnCthcAtuPermaior(Boolean pPrevia,
			Date pDataPrevia, Integer pCthSeq, Integer pPacCodigo,
			Integer pPacProntuario, Integer pIntSeq, Integer pPhiSolic,
			Short pPhoSolic, Integer pIphSolic, Long pCodSusSolic,
			Integer pPhiRealiz, Short pPhoRealiz, Integer pIphRealiz,
			Long pCodSusRealiz, short pDiariasConta, int pDiariasUti,
			Short pPhoPerm, Integer pIphPerm, String pMotivoCobranca,
			Integer pCodExclusaoCritica, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FaturamentoON faturamentoON = getFaturamentoON();
		final FaturamentoRN faturamentoRN = getFaturamentoRN();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();

		final RnCthcAtuPermaiorVO rnCthcAtuPermaiorVO = new RnCthcAtuPermaiorVO();
		rnCthcAtuPermaiorVO.setCodExclusaoCritica(pCodExclusaoCritica);
		boolean result = true;

		Short vPermPrevista = null;
		Short vMaxPermaiorProc = null;
		Short vMaxPermanMaiorAih = null;
		Long vCodPermMaior = null;
		Short vPhoPm = null;
		Integer vIphPm = null;
		Long vCodSusPm = null;
		Integer vPtAnestPm = null;
		Integer vPtCirPm = null;
		Integer vPtSadtPm = null;
		BigDecimal vVlrAnestPm = BigDecimal.ZERO;
		BigDecimal vVlrProcedPm = BigDecimal.ZERO;
		BigDecimal vVlrSadtPm = BigDecimal.ZERO;
		BigDecimal vVlrServHospPm = BigDecimal.ZERO;
		BigDecimal vVlrServProfPm = BigDecimal.ZERO;
		Short vQtdPermMaior = null;
		Short vEicSeqp = null;
		Integer vMaxQtdAih = null;
		Integer vMaxDiariasConta = null;

		// Busca permanencia prevista, max permanencia maior
		logar("param p/calc PM: PHO {0} IPH {1}", pPhoPerm, pIphPerm);

		FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO
				.obterPorChavePrimaria(new FatItensProcedHospitalarId(pPhoPerm,
						pIphPerm));
		if (fatItensProcedHospitalar != null) {
			vPermPrevista = fatItensProcedHospitalar.getQuantDiasFaturamento();
			vMaxPermaiorProc = fatItensProcedHospitalar
					.getDiasPermanenciaMaior();
		}

		vPermPrevista = vPermPrevista != null ? vPermPrevista : pDiariasConta;
		vMaxPermaiorProc = vMaxPermaiorProc != null ? vMaxPermaiorProc : 0;

		logar("param p/calc PM: v_perm_prevista {0}", vPermPrevista);
		logar("param p/calc PM: v_max_permaior_proc {0}", vMaxPermaiorProc);

		// Valida quantidade de diarias da conta:
		// Verifica qtde maxima p/campos de qtd e diarias
		vMaxQtdAih = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_MAX_QTD_CAMPO_QTD_INT);

		vMaxDiariasConta = (vPermPrevista * 2) + pDiariasUti + vMaxQtdAih;

		if (CoreUtil.maior(pDiariasConta, vMaxDiariasConta)) {
			logar("DIARIAS DA CONTA MAIOR QUE O MAXIMO PERMITIDO");
			FatMensagemLog fatMensagemLog = new FatMensagemLog();
			fatMensagemLog.setCodigo(54);
			faturamentoON.criarFatLogErrors(
					"DIARIAS DA CONTA NAO PODE ULTRAPASSAR " + vMaxDiariasConta
							+ ".", INT, pCthSeq, null, null, null, null,
					pDataPrevia, null, null, pPhoSolic, pPhoRealiz, pIphSolic,
					pIphRealiz, pPacCodigo, pPhiSolic, pPhiRealiz, null,
					pPacProntuario, pCodSusRealiz, pCodSusSolic, null, null,
					null, null, null, null, "RN_CTHC_ATU_PERMAIOR", null, null,fatMensagemLog);

			result = false;
		}

		// Verifica permanencia menor
		logar("PERM PREVISTA: {0} METADE: {1}", vPermPrevista,
				((int) (vPermPrevista / 2)));
		logar("PERM DIARIAS CONTA: {0}", pDiariasConta);

		if (CoreUtil.menor(pDiariasConta, (vPermPrevista / 2))) {
			logar("PERM menor que PREVISTA, moti: {0}", pMotivoCobranca);

			if (pMotivoCobranca != null
					&& !(pMotivoCobranca.charAt(0) == '4'
							|| pMotivoCobranca.charAt(0) == '5' || pMotivoCobranca
							.charAt(0) == '6')) {
				logar("exclusao critica PERM menor que a PREVISTA");
				// Permanencia menor que a metade prevista
				rnCthcAtuPermaiorVO.setCodExclusaoCritica(this
						.rnFatpExclusaoCritica(DominioSituacao.I,
								DominioSituacao.I, DominioSituacao.I,
								DominioSituacao.I, DominioSituacao.A, pCthSeq,
								DominioSituacao.I, nomeMicrocomputador, dataFimVinculoServidor));
			}
		}

		// Verifica se houve permanencia maior na conta
		if (CoreUtil.maior((pDiariasConta - pDiariasUti), (vPermPrevista * 2))) {
			// Calcula dias de permanencia maior
			vQtdPermMaior = (short) (pDiariasConta - pDiariasUti - (vPermPrevista * 2));

			logar("PERM dias de permanencia maior: {0}", vQtdPermMaior);

			// Verifica se o realizado permite lancar permanencia maior
			if (CoreUtil.maior(vMaxPermaiorProc, 0)) {
				// realizado permite PM
				// Verifica qtde maxima de perman maior permitida pelo realizado
				if (CoreUtil.maior(vQtdPermMaior, vMaxPermaiorProc)) {
					vQtdPermMaior = vMaxPermaiorProc;
					logar("PERM  permanencia maxima: {0}", vQtdPermMaior);
				}

				// Verifica qtde maxima cobravel de perman maior em AIH
				vMaxPermanMaiorAih = buscarVlrShortAghParametro(AghuParametrosEnum.P_MAX_PERMANENCIA_MAIOR);

				if (CoreUtil.maior(vQtdPermMaior, vMaxPermanMaiorAih)) {
					vQtdPermMaior = vMaxPermanMaiorAih;
				}

				if (CoreUtil.maior(vQtdPermMaior, 0)) {
					// Busca codigo do procedim permanencia maior para adulto
					vCodPermMaior = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_PERMAN_MAIOR_ADULTO);

					// Na tabela unificada é um código único
					// Busca codigo do procedimento a lancar
					FatItensProcedHospitalar fatItemProcedimentoHospitalar = fatItensProcedHospitalarDAO
							.buscarPrimeiroItemProcedimentosHospitalares(
									vCodPermMaior, DominioSituacao.A);
					if (fatItemProcedimentoHospitalar != null) {
						vCodSusPm = fatItemProcedimentoHospitalar
								.getCodTabela();
						vPhoPm = fatItemProcedimentoHospitalar.getId()
								.getPhoSeq();
						vIphPm = fatItemProcedimentoHospitalar.getId().getSeq();
						vPtAnestPm = fatItemProcedimentoHospitalar
								.getPontoAnestesista();
						vPtSadtPm = fatItemProcedimentoHospitalar
								.getPontosSadt();
						vPtCirPm = fatItemProcedimentoHospitalar
								.getPontoAnestesista();

						vCodSusPm = vCodSusPm != null ? vCodSusPm : 0;
						vPtAnestPm = (vPtAnestPm != null ? vPtAnestPm : 0)
								* vQtdPermMaior;
						vPtCirPm = (vPtCirPm != null ? vPtCirPm : 0)
								* vQtdPermMaior;
						vPtSadtPm = (vPtSadtPm != null ? vPtSadtPm : 0)
								* vQtdPermMaior;

						logar("perm maior: PHO: {0} IPH: {1} cod sus: {2}",
								vPhoPm, vIphPm, vCodSusPm);

						// Busca valores pro item
						FatVlrItemProcedHospComps fatVlrItemProcedHospComps = getVerificacaoItemProcedimentoHospitalarRN()
								.obterValoresItemProcHospPorModuloCompetencia(
										vPhoPm, vIphPm,
										DominioModuloCompetencia.INT, null);

						if (fatVlrItemProcedHospComps == null) {
							vVlrServHospPm = BigDecimal.ZERO;
							vVlrSadtPm = BigDecimal.ZERO;
							vVlrProcedPm = BigDecimal.ZERO;
							vVlrAnestPm = BigDecimal.ZERO;
							vVlrServProfPm = BigDecimal.ZERO;
							logar("perm zerou valores ");
						} else {
							BigDecimal vQtdPermMaiorBD = new BigDecimal(
									vQtdPermMaior);

							vVlrServHospPm = nvl(
									fatVlrItemProcedHospComps
											.getVlrServHospitalar(),
									BigDecimal.ZERO);
							vVlrSadtPm = nvl(
									fatVlrItemProcedHospComps.getVlrSadt(),
									BigDecimal.ZERO);
							vVlrProcedPm = nvl(
									fatVlrItemProcedHospComps
											.getVlrProcedimento(),
									BigDecimal.ZERO);
							vVlrAnestPm = nvl(
									fatVlrItemProcedHospComps
											.getVlrAnestesista(),
									BigDecimal.ZERO);
							vVlrServProfPm = nvl(
									fatVlrItemProcedHospComps
											.getVlrServProfissional(),
									BigDecimal.ZERO);

							vVlrProcedPm = BigDecimal.ZERO;
							vVlrServHospPm = vQtdPermMaiorBD
									.multiply(vVlrServHospPm);
							vVlrSadtPm = vQtdPermMaiorBD.multiply(vVlrSadtPm);
							vVlrAnestPm = vQtdPermMaiorBD.multiply(vVlrAnestPm);
							vVlrServProfPm = vQtdPermMaiorBD
									.multiply(vVlrServProfPm);
						}

						// Busca proxima seq da tabela de espelho
						vEicSeqp = fatEspelhoItemContaHospDAO
								.buscaProximaSeqTabelaEspelho(pCthSeq);

						// Lanca procedimento de permanencia maior
						logar("next eic seqp (pm): {0} perm {1}", vEicSeqp,vCodSusPm);

						try {
							FatEspelhoItemContaHosp espelho = new FatEspelhoItemContaHosp();
							espelho.setId(new FatEspelhoItemContaHospId(pCthSeq, vEicSeqp));
							espelho.setItemProcedimentoHospitalar(fatItensProcedHospitalarDAO
									.obterPorChavePrimaria(new FatItensProcedHospitalarId(vPhoPm, vIphPm)));
							
							espelho.setQuantidade(vQtdPermMaior);
							espelho.setProcedimentoHospitalarSus(vCodSusPm);
							espelho.setPontosAnestesista(vPtAnestPm);
							espelho.setPontosCirurgiao(vPtCirPm);
							espelho.setPontosSadt(vPtSadtPm);
							espelho.setIndConsistente(true);
							espelho.setIndModoCobranca(DominioModoCobranca.V);
							espelho.setValorAnestesista(vVlrAnestPm);
							espelho.setValorProcedimento(vVlrProcedPm);
							espelho.setValorSadt(vVlrSadtPm);
							espelho.setValorServHosp(vVlrServHospPm);
							espelho.setValorServProf(vVlrServProfPm);
							espelho.setDataPrevia(pDataPrevia);
							espelho.setIndTipoItem(DominioTipoItemEspelhoContaHospitalar.D);
							espelho.setIndGeradoEncerramento(true);
							espelho.setIndLocalSoma(DominioLocalSoma.D);

							faturamentoRN.inserirFatEspelhoItemContaHosp(espelho, true);
							//faturamentoFacade.evict(espelho);
						} catch (Exception e) {
							logar("ERRO INSERT PermMaior EM fat_espelhos_itens_conta_hosp");

							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(70);
							faturamentoON.criarFatLogErrors(
									"ERRO AO INSERIR ESPELHO ITEM CONTA HOSPITALAR PARA PERMANENCIA MAIOR: "
											+ e.getMessage(), INT, pCthSeq,
									null, null, vCodSusPm, null, pDataPrevia,
									null, null, pPhoSolic, pPhoRealiz,
									pIphSolic, pIphRealiz, pPacCodigo,
									pPhiSolic, pPhiRealiz, null,
									pPacProntuario, pCodSusRealiz,
									pCodSusSolic, vPhoPm, null, vIphPm, null,
									null, null, "RN_CTHC_ATU_PERMAIOR", null,
									null,fatMensagemLog);

							vQtdPermMaior = 0;
							result = false;
						}
					} else {
						
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(124);
						faturamentoON
								.criarFatLogErrors(
										"NAO ENCONTROU CODIGO DO PROCEDIMENTO DE PERMANENCIA MAIOR PARA LANCAR AUTOMATICAMENTE.",
										INT, pCthSeq, null, null, null, null,
										pDataPrevia, null, null, pPhoSolic,
										pPhoRealiz, pIphSolic, pIphRealiz,
										pPacCodigo, pPhiSolic, pPhiRealiz,
										null, pPacProntuario, pCodSusRealiz,
										pCodSusSolic, null, null, null, null,
										null, null, "RN_CTHC_ATU_PERMAIOR",
										null, null,fatMensagemLog);

						vQtdPermMaior = 0;
						result = false;
					}
				} else {
					vQtdPermMaior = 0;
				}
			} else {
				vQtdPermMaior = 0;
			}
		} else {
			vQtdPermMaior = 0;
		}

		// Atualiza permanencia maior na conta
		logar("vai atualizar {0} diarias de perman maior na conta.. ",
				vQtdPermMaior);

		FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO
				.obterPorChavePrimaria(pCthSeq);
		if (contaHospitalar != null) {
			FatContasHospitalares contaOld = null;
			try {
				contaOld = faturamentoFacade
						.clonarContaHospitalar(contaHospitalar);
			} catch (Exception e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				throw new BaseException(
						FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
			}
			contaHospitalar.setDiasPermanenciaMaior(vQtdPermMaior);
			faturamentoFacade.persistirContaHospitalar(contaHospitalar,
					contaOld, nomeMicrocomputador, dataFimVinculoServidor);
			//faturamentoFacade.evict(contaHospitalar);
		}

		rnCthcAtuPermaiorVO.setRetorno(result);
		return rnCthcAtuPermaiorVO;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_ATU_REABRE
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public Boolean rnCthcAtuReabre(final Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean pPrevia) throws BaseException {
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();
		final FatEspelhoAihDAO fatEspelhoAihDAO = getFatEspelhoAihDAO();
		final FatCampoMedicoAuditAihDAO fatCampoMedicoAuditAihDAO = getFatCampoMedicoAuditAihDAO();
		final FatAtoMedicoAihDAO fatAtoMedicoAihDAO = getFatAtoMedicoAihDAO();
		final FaturamentoRN faturamentoRN = getFaturamentoRN();
		final ItemContaHospitalarON itemContaHospitalarON = getItemContaHospitalarON();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		Boolean result = Boolean.TRUE;
		FatContasHospitalares vFatContasHospitalares = fatContasHospitalaresDAO
				.obterPorChavePrimaria(pCthSeq);
		FatContasHospitalares vFatContasHospitalaresOld;
		try {
			vFatContasHospitalaresOld = faturamentoFacade
					.clonarContaHospitalar(vFatContasHospitalares);
		} catch (Exception e1) {
			LOG.error(EXCECAO_CAPTURADA, e1);
			throw new BaseException(
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
		}

		if (vFatContasHospitalares != null) {

			if (CoreUtil.igual(DominioSimNao.S.toString(),
					vFatContasHospitalares.getIndAutorizadoSms())) {
				FatEspelhoAih buscaDadosCta = fatEspelhoAihDAO
						.obterPorChavePrimaria(new FatEspelhoAihId(pCthSeq, 1));
				if (buscaDadosCta != null
						&& buscaDadosCta.getIndBcoCapac() == null) {
					getSaldoBancoCapacidadeAtulizacaoRN()
							.atualizarSaldoDiariasBancoCapacidade(
									buscaDadosCta.getDataInternacao(),
									buscaDadosCta.getDataSaida(),
									buscaDadosCta.getEspecialidadeAih() != null ? buscaDadosCta
											.getEspecialidadeAih().intValue()
											: null,
									buscaDadosCta.getId().getCthSeq(),
									DominioAtualizacaoSaldo.D, true, nomeMicrocomputador, dataFimVinculoServidor);
				}
				//faturamentoFacade.evict(buscaDadosCta);
			}
			// Marina FIM - 28/08/2008

			// verificar se a competencia não está em manutenção
			// passar a propria dcih como retorno
			DominioSituacaoCompetencia vSitCpe = null;
			FatDocumentoCobrancaAihs docCobrabcaAih = vFatContasHospitalares
					.getDocumentoCobrancaAih();
			if (docCobrabcaAih != null) {
				vSitCpe = docCobrabcaAih.getFatCompetencia() != null ? docCobrabcaAih
						.getFatCompetencia().getIndSituacao() : null;
			}

			fatCampoMedicoAuditAihDAO.removerPorCth(pCthSeq);
			this.flush();
			fatAtoMedicoAihDAO.removerPorCth(pCthSeq);
			this.flush();
			fatEspelhoAihDAO.removerPorCth(vFatContasHospitalares.getSeq());
			this.flush();
			fatEspelhoItemContaHospDAO.removerPorContaHospitalar(pCthSeq);
			this.flush();

			if (vFatContasHospitalares.getValorContaHospitalar() != null) {
				faturamentoRN.excluirFatValorContaHospitalar(vFatContasHospitalares.getValorContaHospitalar());
				vFatContasHospitalares.setValorContaHospitalar(null);
			}

			if (vFatContasHospitalares.getPerdaItensConta() != null
					&& !vFatContasHospitalares.getPerdaItensConta().isEmpty()) {
				for (FatPerdaItemConta fatPerdaItemConta : vFatContasHospitalares.getPerdaItensConta()) {
					faturamentoRN.excluirFatPerdaItemConta(fatPerdaItemConta);
					//faturamentoFacade.evict(fatPerdaItemConta);
				}
				vFatContasHospitalares.getPerdaItensConta().clear();
			}

			if (vFatContasHospitalares.getItensContaHospitalar() != null
					&& !vFatContasHospitalares.getItensContaHospitalar()
							.isEmpty()) {
				for (FatItemContaHospitalar fatItensContaHospitalar : vFatContasHospitalares
						.getItensContaHospitalar()) {
					if (CoreUtil.in(fatItensContaHospitalar.getIndSituacao(),
							DominioSituacaoItenConta.P,
							DominioSituacaoItenConta.V,
							DominioSituacaoItenConta.N)) {
						FatItemContaHospitalar oldFatItensContaHospitalar = null;
						try {
							oldFatItensContaHospitalar = itemContaHospitalarON
									.clonarItemContaHospitalar(fatItensContaHospitalar);
						} catch (Exception e) {
							LOG.error(EXCECAO_CAPTURADA, e);
							FaturamentoExceptionCode.ERRO_AO_ATUALIZAR
									.throwException();
						}
						fatItensContaHospitalar
								.setIndSituacao(DominioSituacaoItenConta.A);
						itemContaHospitalarON.atualizarItemContaHospitalarSemValidacoesForms(
										fatItensContaHospitalar, oldFatItensContaHospitalar, true, 
										servidorLogado, dataFimVinculoServidor, pPrevia, true);
					//	faturamentoFacade.evict(fatItensContaHospitalar);
					}
				}
			}

			if (docCobrabcaAih != null) {
				FatDocumentoCobrancaAihs oldFatDocumentoCobrancaAihs = null;
				try {
					oldFatDocumentoCobrancaAihs = faturamentoRN
							.clonarFatDocumentoCobrancaAihs(docCobrabcaAih);
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}
				Short qtd = nvl(docCobrabcaAih.getQuantidadeContasHosp(), 0);
				docCobrabcaAih.setQuantidadeContasHosp(--qtd);
				faturamentoFacade.atualizarFatDocumentoCobrancaAihs(
						docCobrabcaAih, oldFatDocumentoCobrancaAihs);
			}

			vFatContasHospitalares.setIndSituacao(DominioSituacaoConta.F);
			vFatContasHospitalares.setDtEncerramento(null);

			if (!CoreUtil.igual(DominioSituacaoCompetencia.M, vSitCpe)) {
				vFatContasHospitalares.setDocumentoCobrancaAih(null);
			}
			if (!CoreUtil.igual(DominioSituacaoCompetencia.M, vSitCpe)) {
				vFatContasHospitalares.setTipoClassifSecSaude(null);
			}

			vFatContasHospitalares.setValorSh(null);
			vFatContasHospitalares.setValorUti(null);
			vFatContasHospitalares.setValorUtie(null);
			vFatContasHospitalares.setValorSp(null);
			vFatContasHospitalares.setValorAcomp(null);
			vFatContasHospitalares.setValorRn(null);
			vFatContasHospitalares.setValorSadt(null);
			vFatContasHospitalares.setValorHemat(null);
			vFatContasHospitalares.setValorTransp(null);
			vFatContasHospitalares.setValorOpm(null);
			vFatContasHospitalares.setValorAnestesista(null);
			vFatContasHospitalares.setValorProcedimento(null);
			vFatContasHospitalares.setPontosAnestesista(null);
			vFatContasHospitalares.setPontosCirurgiao(null);
			vFatContasHospitalares.setPontosSadt(null);
			vFatContasHospitalares.setExclusaoCritica(null); // Marina
																// 10/02/2010

			// ATUALIZA SALDO NO BANCO DE UTI:
			if (CoreUtil.igual(DominioSituacaoConta.E,
					vFatContasHospitalaresOld.getIndSituacao())) { // SE A CONTA
																	// ESTAVA
																	// ENCERRADA
				if (!getFatkCth5RN().fatcValidaProcUti(pCthSeq)) {
					RnSutcAtuSaldoVO rnSutcAtuSaldoVO = getSaldoUtiAtualizacaoRN()
							.atualizarSaldoDiariasUti(
									DominioAtualizacaoSaldo.D,
									vFatContasHospitalares
											.getDtAltaAdministrativa(),
									vFatContasHospitalares
											.getDiasUtiMesInicial(),
									vFatContasHospitalares
											.getDiasUtiMesAnterior(),
									vFatContasHospitalares.getDiasUtiMesAlta(),
									vFatContasHospitalares.getIndIdadeUti(), 
									nomeMicrocomputador);
					if (!rnSutcAtuSaldoVO.isRetorno()) {
						result = Boolean.FALSE;
					}
				}
			}

			faturamentoFacade.persistirContaHospitalar(vFatContasHospitalares,
					vFatContasHospitalaresOld, Boolean.TRUE, nomeMicrocomputador, dataFimVinculoServidor);
			//faturamentoFacade.evict(vFatContasHospitalares);
		} else {
			result = Boolean.FALSE;
		}
		
		return result;
	}
	
	public void rnCthcAtuReabreEmLote(String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		
		DominioSituacaoCompetencia vSitCpe = null;
		List<Integer> contas = getFatContasHospitalaresDAO().obterFatContasHospitalaresNaoAutorizadas();
		
		if(contas != null && !contas.isEmpty()) {
			removerPorContaHospitalar(contas);
			//super.commitTransaction();
			
			for (Integer cthSeq : contas) {
				//super.beginTransaction();
				vSitCpe = atualizaContaHospitalar(nomeMicrocomputador,
						servidorLogado, vSitCpe, cthSeq);
			}
		}		
	}

	public DominioSituacaoCompetencia atualizaContaHospitalar(
			String nomeMicrocomputador, RapServidores servidorLogado,
			DominioSituacaoCompetencia vSitCpe, Integer cthSeq) {
		FatContasHospitalares vFatContasHospitalares = getFatContasHospitalaresDAO()
				.obterPorChavePrimaria(cthSeq);
		FatContasHospitalares vFatContasHospitalaresOld;
		
		try {
			vFatContasHospitalaresOld = getFaturamentoFacade()
					.clonarContaHospitalar(vFatContasHospitalares);						
		
			List<DominioSituacaoItenConta> situacoes = new ArrayList<DominioSituacaoItenConta>();
				situacoes.add(DominioSituacaoItenConta.P);
				situacoes.add(DominioSituacaoItenConta.V);
				situacoes.add(DominioSituacaoItenConta.N);
			
			String nomeUsuario = null;
			Integer matriculaServ = null;
			Short vinculoServ = null;
			
			if (servidorLogado != null) {
				nomeUsuario = servidorLogado.getUsuario();
				matriculaServ = servidorLogado.getId().getMatricula();
				vinculoServ = servidorLogado.getId().getVinCodigo();
			}
			
			getFatItemContaHospitalarDAO()
					.alterarSituacaoEmLoteSemValidacoes(cthSeq,
							DominioSituacaoItenConta.A, situacoes,
							nomeUsuario, matriculaServ, vinculoServ);		
			
			FatDocumentoCobrancaAihs docCobrabcaAih = vFatContasHospitalares.getDocumentoCobrancaAih();
			if (docCobrabcaAih != null) {
				vSitCpe = docCobrabcaAih.getFatCompetencia() != null ? docCobrabcaAih
						.getFatCompetencia().getIndSituacao() : null;
			}
			
			if (docCobrabcaAih != null) {
				FatDocumentoCobrancaAihs oldFatDocumentoCobrancaAihs = null;
				try {
					oldFatDocumentoCobrancaAihs = getFaturamentoRN()
							.clonarFatDocumentoCobrancaAihs(docCobrabcaAih);
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}
				Short qtd = nvl(docCobrabcaAih.getQuantidadeContasHosp(), 0);
				docCobrabcaAih.setQuantidadeContasHosp(--qtd);
				getFaturamentoFacade().atualizarFatDocumentoCobrancaAihs(
						docCobrabcaAih, oldFatDocumentoCobrancaAihs);
			}
			
			vFatContasHospitalares.setIndSituacao(DominioSituacaoConta.F);
			vFatContasHospitalares.setDtEncerramento(null);

			if (!CoreUtil.igual(DominioSituacaoCompetencia.M, vSitCpe)) {
				vFatContasHospitalares.setDocumentoCobrancaAih(null);
				vFatContasHospitalares.setTipoClassifSecSaude(null);
			}

			vFatContasHospitalares.setValorSh(null);
			vFatContasHospitalares.setValorUti(null);
			vFatContasHospitalares.setValorUtie(null);
			vFatContasHospitalares.setValorSp(null);
			vFatContasHospitalares.setValorAcomp(null);
			vFatContasHospitalares.setValorRn(null);
			vFatContasHospitalares.setValorSadt(null);
			vFatContasHospitalares.setValorHemat(null);
			vFatContasHospitalares.setValorTransp(null);
			vFatContasHospitalares.setValorOpm(null);
			vFatContasHospitalares.setValorAnestesista(null);
			vFatContasHospitalares.setValorProcedimento(null);
			vFatContasHospitalares.setPontosAnestesista(null);
			vFatContasHospitalares.setPontosCirurgiao(null);
			vFatContasHospitalares.setPontosSadt(null);
			vFatContasHospitalares.setExclusaoCritica(null);
			vFatContasHospitalares.setAlteradoEm(new Date());					
			vFatContasHospitalares.setAlteradoPor(nomeUsuario);

			// ATUALIZA SALDO NO BANCO DE UTI:
			if (CoreUtil.igual(DominioSituacaoConta.E,
					vFatContasHospitalaresOld.getIndSituacao())) { // SE A CONTA ESTAVA ENCERRADA
				if (!getFatkCth5RN().fatcValidaProcUti(cthSeq)) {
					getSaldoUtiAtualizacaoRN()
							.atualizarSaldoDiariasUti(
									DominioAtualizacaoSaldo.D,
									vFatContasHospitalares
											.getDtAltaAdministrativa(),
									vFatContasHospitalares
											.getDiasUtiMesInicial(),
									vFatContasHospitalares
											.getDiasUtiMesAnterior(),
									vFatContasHospitalares.getDiasUtiMesAlta(),
									vFatContasHospitalares.getIndIdadeUti(), 
									nomeMicrocomputador);
				}
			}
			
			//atualizar conta sem validações	
			getFatContasHospitalaresDAO().atualizar(vFatContasHospitalares);
			
			//super.commitTransaction();
		} catch (Exception e1) {
			LOG.error("Conta não processada: " + cthSeq, e1);
			//super.rollbackTransaction();
		}
		return vSitCpe;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void removerPorContaHospitalar(List<Integer> contas) {
		getFatCampoMedicoAuditAihDAO().removerPorCthSeqs(contas);			
		getFatAtoMedicoAihDAO().removerPorCthSeqs(contas);			
		getFatEspelhoAihDAO().removerPorCthSeqs(contas);			
		getFatEspelhoItemContaHospDAO().removerPorCthSeqs(contas);			
		getFatValorContaHospitalarDAO().removerPorCthSeqs(contas);			
		getFatPerdaItemContaDAO().removerPorCthSeqs(contas);
	}
	
	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHP_ATU_DIAR_UTI
	 * 
	 * @throws BaseException
	 */
	public void rnCthpAtuDiarUti(Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();

		// verifica se o convenio da conta e' SUS
		FatContasHospitalares vContaHospConvSus = fatContasHospitalaresDAO
				.obterPorChavePrimaria(pCthSeq);

		// se o convenio e' SUS, calcula as diarias de UTI
		if (vContaHospConvSus != null
				&& vContaHospConvSus.getConvenioSaude() != null
				&& CoreUtil.igual(DominioGrupoConvenio.S, vContaHospConvSus
						.getConvenioSaude().getGrupoConvenio())) {
			// calcula diarias UTI
			RnCthcVerUtimesesVO rnCthcVerUtimesesVO = this.getFatkCth4RN()
					.rnCthcVerUtimeses(pCthSeq);
			if (rnCthcVerUtimesesVO.getRetorno()) {
				if (vContaHospConvSus != null) {
					FatContasHospitalares contaOld = null;
					try {
						contaOld = faturamentoFacade
								.clonarContaHospitalar(vContaHospConvSus);
					} catch (Exception e) {
						LOG.error(EXCECAO_CAPTURADA, e);
						throw new BaseException(
								FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
					}
					vContaHospConvSus.setDiasUtiMesInicial(rnCthcVerUtimesesVO
							.getDiariasUtiMesIni());
					vContaHospConvSus.setDiasUtiMesAnterior(rnCthcVerUtimesesVO
							.getDiariasUtiMesAnt());
					vContaHospConvSus.setDiasUtiMesAlta(rnCthcVerUtimesesVO
							.getDiariasUtiMesAnt());
					vContaHospConvSus.setIndTipoUti(rnCthcVerUtimesesVO
							.getTipoUti());
					faturamentoFacade.persistirContaHospitalar(
							vContaHospConvSus, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
					//faturamentoFacade.evict(vContaHospConvSus);
				}
			}
		}
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_ATU_REINT
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public RnCthcAtuReintVO rnCthcAtuReint(Integer pCthSeq, Integer pPacCodigo, String nomeMicrocomputador, 
											  final Date dataFimVinculoServidor) throws BaseException {

		final RnCthcAtuReintVO vo = new RnCthcAtuReintVO();
		final IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		final ContaHospitalarON contaHospitalarON = getContaHospitalarON();
		final ItemContaHospitalarON itemContaHospitalarON = getItemContaHospitalarON();
		final FaturamentoRN faturamentoRN = getFaturamentoRN();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO = getVFatContaHospitalarPacDAO();
		final FatContasInternacaoDAO fatContasInternacaoDAO = getFatContasInternacaoDAO();
		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();

		Byte vDias = null;
		Short vMaxItem = null;
		Integer vCthSeq = null;
		AinInternacao internacao = null;
		DominioSituacaoConta pSituacao = null;

		FatContasHospitalares regConta = fatContasHospitalaresDAO
				.obterPorChavePrimaria(pCthSeq);
		if (regConta != null
				&& !DominioSituacaoConta.A.equals(regConta.getIndSituacao())
				&& !DominioSituacaoConta.F.equals(regConta.getIndSituacao())) {
			regConta = null;
		}

		if (regConta != null && regConta.getContaHospitalar() == null) {
			// Verifica dias para reinternacao
			vDias = buscarVlrByteAghParametro(AghuParametrosEnum.P_DIAS_REINTERNACAO);

			logar("RN_CTHC_ATU_REINT - V_DIAS: {0}", vDias);

			if (vDias != null) {
				DominioSituacaoConta[] situacoes = new DominioSituacaoConta[] {
						DominioSituacaoConta.A, DominioSituacaoConta.E,
						DominioSituacaoConta.F };
				VFatContaHospitalarPac vFatContaHospitalarPac = vFatContaHospitalarPacDAO
						.buscarPrimeiraContaHospitalaresParaReinternar(
								pCthSeq,
								pPacCodigo,
								regConta.getDataInternacaoAdministrativa(),
								vDias,
								regConta.getConvenioSaudePlano() != null ? regConta
										.getConvenioSaudePlano().getId()
										.getCnvCodigo()
										: null,
								regConta.getConvenioSaudePlano() != null ? regConta
										.getConvenioSaudePlano().getId()
										.getSeq()
										: null, situacoes);
				if (vFatContaHospitalarPac != null) {
					vCthSeq = vFatContaHospitalarPac.getCthSeq();
					pSituacao = vFatContaHospitalarPac.getIndSituacao();
				}

				vo.setSituacao(pSituacao);

				logar("P_SITUACAO: {0}", pSituacao);

				if (DominioSituacaoConta.E.equals(pSituacao)) {
					vo.setRetorno(false);
					return vo;
				} else if (DominioSituacaoConta.A.equals(pSituacao)
						|| DominioSituacaoConta.F.equals(pSituacao)) {
					// Consiste em uma reinternacao conta anterior permanece
					FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO
							.obterPorChavePrimaria(vCthSeq);

					FatContasHospitalares oldContaHospitalar = null;
					try {
						oldContaHospitalar = faturamentoFacade
								.clonarContaHospitalar(contaHospitalar);
					} catch (Exception e) {
						logar("Ocorreu um exceção ao realizar o desdobramento",
								e);
						throw new ApplicationBusinessException(
								FaturamentoExceptionCode.ERRO_CLONAR_CONTA_HOSPITALAR);
					}

					contaHospitalar.setIndSituacao(regConta.getIndSituacao());
					contaHospitalar.setDtAltaAdministrativa(regConta
							.getDtAltaAdministrativa());
					contaHospitalar
							.setProcedimentoHospitalarInternoRealizado(regConta
									.getProcedimentoHospitalarInternoRealizado() != null ? regConta
									.getProcedimentoHospitalarInternoRealizado()
									: regConta
											.getProcedimentoHospitalarInterno());

					contaHospitalarON.atualizarContaHospitalar(contaHospitalar,
							oldContaHospitalar, true, nomeMicrocomputador, dataFimVinculoServidor);

					// Cancela conta do parametro NOVA
					contaHospitalar = fatContasHospitalaresDAO
							.obterPorChavePrimaria(pCthSeq);

					oldContaHospitalar = null;
					try {
						oldContaHospitalar = faturamentoFacade
								.clonarContaHospitalar(contaHospitalar);
					} catch (Exception e) {
						logar("Ocorreu um exceção ao realizar o desdobramento",
								e);
						throw new ApplicationBusinessException(
								FaturamentoExceptionCode.ERRO_CLONAR_CONTA_HOSPITALAR);
					}

					contaHospitalar.setIndSituacao(DominioSituacaoConta.C);
					contaHospitalar
							.setProcedimentoHospitalarInternoRealizado(null);
					contaHospitalar.setAih(null);

					contaHospitalarON.atualizarContaHospitalar(contaHospitalar,
							oldContaHospitalar, true, nomeMicrocomputador, dataFimVinculoServidor);

					this.flush();

					// Recarrega o objeto regConta
					regConta = fatContasHospitalaresDAO
							.obterPorChavePrimaria(pCthSeq);

					FatAih fatAih = regConta.getAih();
					if (fatAih != null) {
						Long count = fatContasHospitalaresDAO
								.countContasHospitalaresQuePossuemAIH(fatAih
										.getNroAih());
						if (count == null || count == 0) {
							FatAih oldFatAih = null;
							try {
								oldFatAih = faturamentoRN.clonarFatAih(fatAih);
							} catch (Exception e) {
								LOG.error(EXCECAO_CAPTURADA, e);
								FaturamentoExceptionCode.ERRO_AO_ATUALIZAR
										.throwException();
							}

							fatAih.setIndSituacao(DominioSituacaoAih.U);

							faturamentoRN.atualizarFatAih(fatAih, oldFatAih);
						}
					}

					FatContasInternacao contaInternacaoAux = fatContasInternacaoDAO
							.buscarPrimeiraContaInternacao(pCthSeq);
					
					if (contaInternacaoAux != null) {
						internacao = contaInternacaoAux.getInternacao();
					}
					
					FatContasInternacao contaInternacaoCHAux = fatContasInternacaoDAO
							.buscarPrimeiraContaInternacao(vFatContaHospitalarPac.getCthSeq());

					try {
						FatContasInternacao contaInternacao = new FatContasInternacao();
						if(contaInternacaoCHAux != null) {
							contaInternacao.setContaHospitalar(contaInternacaoCHAux.getContaHospitalar());
						}
						contaInternacao.setInternacao(internacao);
						faturamentoRN.inserirContaInternacao(contaInternacao,
								true, nomeMicrocomputador, dataFimVinculoServidor);
					} catch (Exception e) {
						if (e.getCause() != null
								&& ConstraintViolationException.class.equals(e
										.getCause().getClass())) {
							logar("Exceção ignorada.", e);
						} else {
							throw new ApplicationBusinessException(
									FaturamentoExceptionCode.ERRO_INSERIR_CONTA_INTERNACAO,
									e.getMessage());
						}
					}

					vMaxItem = fatItemContaHospitalarDAO
							.buscaMaxSeqMaisUm(vCthSeq);

					inserirItensContaReinternacao(pCthSeq,
							dataFimVinculoServidor, itemContaHospitalarON,
							fatItemContaHospitalarDAO, vMaxItem, vCthSeq);

					//super.commitTransaction();
					//super.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
					
					vo.setRetorno(true);
					return vo;
				}
			}
		}

		vo.setRetorno(false);
		return vo;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void inserirItensContaReinternacao(Integer pCthSeq,
			final Date dataFimVinculoServidor,
			final ItemContaHospitalarON itemContaHospitalarON,
			final FatItemContaHospitalarDAO fatItemContaHospitalarDAO,
			Short vMaxItem, Integer vCthSeq)
			throws ApplicationBusinessException {
		List<FatItemContaHospitalar> listaItensContaHospitalar = fatItemContaHospitalarDAO
				.listarItensContaHospitalar(pCthSeq,
						DominioSituacaoItenConta.A);
		if (listaItensContaHospitalar != null
				&& !listaItensContaHospitalar.isEmpty()) {
			for (FatItemContaHospitalar regItem : listaItensContaHospitalar) {
				try {
					FatItemContaHospitalar itemContaHospitalar = new FatItemContaHospitalar();

					itemContaHospitalar.setId(new FatItemContaHospitalarId(vCthSeq, vMaxItem));
					itemContaHospitalar.setIchType(regItem.getIchType());
					itemContaHospitalar.setProcedimentoHospitalarInterno(regItem.getProcedimentoHospitalarInterno());
					itemContaHospitalar.setIndSituacao(regItem.getIndSituacao());
					itemContaHospitalar.setIseSoeSeq(regItem.getIseSoeSeq());
					itemContaHospitalar.setIseSeqp(regItem.getIseSeqp());
					itemContaHospitalar.setQuantidadeRealizada(regItem.getQuantidadeRealizada());
					itemContaHospitalar.setIndOrigem(regItem.getIndOrigem());
					itemContaHospitalar.setLocalCobranca(regItem.getLocalCobranca());
					itemContaHospitalar.setDthrRealizado(regItem.getDthrRealizado());
					itemContaHospitalar.setUnidadesFuncional(regItem.getUnidadesFuncional());
					itemContaHospitalar.setIndModoCobranca(regItem.getIndModoCobranca());
					itemContaHospitalar.setMopCrgSeq(regItem.getMopCrgSeq());
					itemContaHospitalar.setMopMatCodigo(regItem.getMopMatCodigo());
					itemContaHospitalar.setProcEspPorCirurgias(regItem.getProcEspPorCirurgias());
					itemContaHospitalar.setItemRmps(regItem.getItemRmps());
					itemContaHospitalar.setProcedimentoAmbRealizado(regItem.getProcedimentoAmbRealizado());
					itemContaHospitalar.setCmoMcoSeq(regItem.getCmoMcoSeq());
					itemContaHospitalar.setCmoEcoBolNumero(regItem.getCmoEcoBolNumero());
					itemContaHospitalar.setCmoEcoBolBsaCodigo(regItem.getCmoEcoBolBsaCodigo());
					itemContaHospitalar.setCmoEcoBolData(regItem.getCmoEcoBolData());
					itemContaHospitalar.setCmoEcoCsaCodigo(regItem.getCmoEcoCsaCodigo());
					itemContaHospitalar.setCmoSequencia(regItem.getCmoSequencia());
					itemContaHospitalar.setPrescricaoProcedimento(regItem.getPrescricaoProcedimento());
					itemContaHospitalar.setPrescricaoNpt(regItem.getPrescricaoNpt());
					itemContaHospitalar.setCmoEcoSeqp(regItem.getCmoEcoSeqp());
					itemContaHospitalar.setPrescricaoPaciente(regItem.getPrescricaoPaciente());
					itemContaHospitalar.setPaoSeq(regItem.getPaoSeq());
					itemContaHospitalar.setServidor(regItem.getServidor());
					itemContaHospitalar.setCriadoEm(regItem.getCriadoEm());
					itemContaHospitalar.setCriadoPor(regItem.getCriadoPor());

					itemContaHospitalarON
							.inserirItemContaHospitalarSemValidacoesForms( itemContaHospitalar, true, 
																		   null, dataFimVinculoServidor, null);
					//faturamentoFacade.evict(itemContaHospitalar);
					vMaxItem++;
				} catch (Exception e) {
					throw new ApplicationBusinessException(
							FaturamentoExceptionCode.ERRO_INSERIR_ITEM_CONTA_HOSPITALAR_DE_REINTERNACAO,
							e.getMessage());
				}
			}
		}
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_ATU_REAPRESENTA
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NcssMethodCount",
			"PMD.NPathComplexity" })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Boolean rnCthcAtuReapresenta(Integer pCthSeq, Short pRjcSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		final IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		final ContaHospitalarON contaHospitalarON = getContaHospitalarON();
		final ItemContaHospitalarON itemContaHospitalarON = getItemContaHospitalarON();

		final FaturamentoRN faturamentoRN = getFaturamentoRN();
		final FatkCth5RN FatkCth5RN = getFatkCth5RN();
		final SaldoBancoCapacidadeAtulizacaoRN saldoBancoCapacidadeAtulizacaoRN = getSaldoBancoCapacidadeAtulizacaoRN();
		final SaldoUtiAtualizacaoRN saldoUtiAtualizacaoRN = getSaldoUtiAtualizacaoRN();

		final CidContaHospitalarPersist cidContaHospitalarPersist = getCidContaHospitalarPersist();
		final FatContaInternacaoPersist fatContaInternacaoPersist = getFatContaInternacaoPersist();

		final FatCidContaHospitalarDAO fatCidContaHospitalarDAO = getFatCidContaHospitalarDAO();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatContasInternacaoDAO fatContasInternacaoDAO = getFatContasInternacaoDAO();
		final FatEspelhoAihDAO fatEspelhoAihDAO = getFatEspelhoAihDAO();
		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		final FatMotivoRejeicaoContaDAO fatMotivoRejeicaoContaDAO = getFatMotivoRejeicaoContaDAO();

		FatContasHospitalares regConta = null;
		FatItemContaHospitalar regItem = null;
		Integer vCthSeq = null;
		Integer vCthFilha = null;
		FatEspelhoAih rBuscaConta = null;
		Integer vCthReap = null;
		Short vIchSeq = (short) 1;
		Boolean result = true;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		try {
			regConta = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);

			if (regConta != null
					&& DominioSituacaoConta.O.equals(regConta.getIndSituacao())) {

				logar("Situacao O ");

				FatContasHospitalares conta = new FatContasHospitalares();
				try {
					conta.setDataInternacaoAdministrativa(regConta.getDataInternacaoAdministrativa());
					conta.setContaManuseada(regConta.getContaManuseada());
					conta.setAcomodacao(regConta.getAcomodacao());
					conta.setConvenioSaudePlano(regConta.getConvenioSaudePlano());
					conta.setProcedimentoHospitalarInterno(regConta.getProcedimentoHospitalarInterno());
					conta.setTacCnvCodigo(regConta.getTacCnvCodigo());
					conta.setTacSeq(regConta.getTacSeq());
					conta.setProcedimentoHospitalarInternoRealizado(regConta.getProcedimentoHospitalarInternoRealizado());
					conta.setDtAltaAdministrativa(regConta.getDtAltaAdministrativa());
					conta.setAih(regConta.getAih());
					conta.setIndContaReapresentada(true);
					conta.setIndImpAih(regConta.getIndImpAih());
					conta.setNumDiariasAutorizadas(regConta.getNumDiariasAutorizadas());
					conta.setSituacaoSaidaPaciente(regConta.getSituacaoSaidaPaciente());
					conta.setRnNascidoVivo(regConta.getRnNascidoVivo());
					conta.setRnNascidoMorto(regConta.getRnNascidoMorto());
					conta.setRnSaidaAlta(regConta.getRnSaidaAlta());
					conta.setRnSaidaTransferencia(regConta.getRnSaidaTransferencia());
					conta.setRnSaidaObito(regConta.getRnSaidaObito());
					conta.setIndInfeccao(regConta.getIndInfeccao());
					conta.setIndSituacao(DominioSituacaoConta.F);
					conta.setTipoAih(regConta.getTipoAih());
					conta.setContaHospitalarReapresentada(regConta);
					conta.setDiariasAcompanhante(regConta.getDiariasAcompanhante());
					conta.setDiasUtiMesAlta(regConta.getDiasUtiMesAlta());
					conta.setDiasUtiMesAnterior(regConta.getDiasUtiMesAnterior());
					conta.setDiasUtiMesInicial(regConta.getDiasUtiMesInicial());
					conta.setIndTipoUti(regConta.getIndTipoUti());
					conta.setTipoAltaUti(regConta.getTipoAltaUti());
					conta.setPesoRn(regConta.getPesoRn());
					conta.setMesesGestacao(regConta.getMesesGestacao());
					conta.setEspecialidade(regConta.getEspecialidade());
					conta.setNroSeqaih5(regConta.getNroSeqaih5());
					conta.setNroSisprenatal(regConta.getNroSisprenatal());
					conta.setIndAutorizaFat(regConta.getIndAutorizaFat());
					conta.setIndAutorizadoSms(regConta.getIndAutorizadoSms());
					conta.setDtEnvioSms(regConta.getDtEnvioSms());
					conta.setIndEnviadoSms(regConta.getIndEnviadoSms());
					conta.setCodSusAut(regConta.getCodSusAut());

					/*Marina chamado 83225*/
					conta.setCnsMedicoAuditor(regConta.getCnsMedicoAuditor());					
					
					conta = contaHospitalarON.inserirContaHospitalar(conta, true, dataFimVinculoServidor);
					
					vCthSeq = conta.getSeq(); 

					logar("Inseriu cth");
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					throw new ApplicationBusinessException(
							FaturamentoExceptionCode.MENSAGEM_ERRO_GERAR_CONTA_REAPRESENTACAO,
							e.getMessage());
				}

				try {
					logar("vai inserir coi");

					List<FatContasInternacaoVO> listaVO = fatContasInternacaoDAO
							.listaFatContasInternacaoVOPorCthSeq(pCthSeq);
					if (listaVO != null && !listaVO.isEmpty()) {
						for (FatContasInternacaoVO vo : listaVO) {
							FatContasInternacao contaInternacao = new FatContasInternacao();

							contaInternacao.setInternacao(vo.getInternacao());
							contaInternacao.setDadosContaSemInt(vo
									.getDadosContaSemInt());
							contaInternacao.setContaHospitalar(conta);

							fatContaInternacaoPersist.setComFlush(true);
							fatContaInternacaoPersist.inserir(contaInternacao, nomeMicrocomputador, dataFimVinculoServidor);
							getFatContasInternacaoDAO().flush();
							
						}
					}

					logar("inseriu coi");
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					throw new ApplicationBusinessException(
							FaturamentoExceptionCode.MENSAGEM_ERRO_GERAR_INTERNACAO_REAPRESENTACAO,
							e.getMessage());
				}

				try {
					List<FatCidContaHospitalarVO> listaVO = fatCidContaHospitalarDAO
							.listarFatCidContaHospitalarVOPorCthSeq(pCthSeq);
					if (listaVO != null && !listaVO.isEmpty()) {
						for (FatCidContaHospitalarVO vo : listaVO) {
							FatCidContaHospitalar fatCidContaHospitalar = new FatCidContaHospitalar(
									new FatCidContaHospitalarId(vCthSeq,
											vo.getCidSeq(),
											vo.getPrioridadeCid()));
							
							cidContaHospitalarPersist.setComFlush(true);
							cidContaHospitalarPersist
									.inserir(fatCidContaHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
						}
					}

					logar("Inseriu CID");
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					throw new ApplicationBusinessException(
							FaturamentoExceptionCode.MENSAGEM_ERRO_GERAR_CID_REAPRESENTACAO,
							e.getMessage());
				}

				// Ney (27/03/2012) retirei C
				List<FatItemContaHospitalar> listaItensContaHospitalar = fatItemContaHospitalarDAO
						.listarItensContaHospitalarPorCthSituacoesIgnoradas(
								pCthSeq, DominioSituacaoItenConta.D);

				if (listaItensContaHospitalar != null
						&& !listaItensContaHospitalar.isEmpty()) {
					for (int i = 0; i < listaItensContaHospitalar.size(); i++) {
						regItem = listaItensContaHospitalar.get(i);

						/*
						 * if reg_item.phi_seq = reg_conta.phi_seq_realizado and
						 * reg_item.ind_situacao = 'C' then null; -- Ney,
						 * 20120430 else
						 */

						// Ney, 20120430
						if (CoreUtil.igual(regItem
								.getProcedimentoHospitalarInterno(), regConta
								.getProcedimentoHospitalarInternoRealizado())
								&& DominioSituacaoItenConta.C.equals(regItem
										.getIndSituacao())) {
							vIchSeq++;
							logar("Inseriu itens {0} seq {1}", vIchSeq, regItem.getId().getSeq());
							continue;
						} else {
							try {
								DominioSituacaoItenConta situacao = (DominioSituacaoItenConta.P
										.equals(regItem.getIndSituacao())
										|| DominioSituacaoItenConta.V
												.equals(regItem
														.getIndSituacao()) || DominioSituacaoItenConta.N
										.equals(regItem.getIndSituacao())) ? DominioSituacaoItenConta.A
										: regItem.getIndSituacao();

								FatItemContaHospitalar itemContaHospitalar = new FatItemContaHospitalar();

								itemContaHospitalar.setId(new FatItemContaHospitalarId(vCthSeq, vIchSeq));
								itemContaHospitalar.setIchType(regItem.getIchType());
								itemContaHospitalar.setProcedimentoHospitalarInterno(regItem.getProcedimentoHospitalarInterno());
								itemContaHospitalar.setIseSoeSeq(regItem.getIseSoeSeq());
								itemContaHospitalar.setIseSeqp(regItem.getIseSeqp());
								itemContaHospitalar.setQuantidadeRealizada(regItem.getQuantidadeRealizada());
								itemContaHospitalar.setIndOrigem(regItem.getIndOrigem());
								itemContaHospitalar.setLocalCobranca(regItem.getLocalCobranca());
								itemContaHospitalar.setDthrRealizado(regItem.getDthrRealizado());
								itemContaHospitalar.setUnidadesFuncional(regItem.getUnidadesFuncional());
								itemContaHospitalar.setIndModoCobranca(regItem.getIndModoCobranca());
								itemContaHospitalar.setProcEspPorCirurgias(regItem.getProcEspPorCirurgias());
								itemContaHospitalar.setItemRmps(regItem.getItemRmps());
								itemContaHospitalar.setProcedimentoAmbRealizado(regItem.getProcedimentoAmbRealizado());
								itemContaHospitalar.setCmoMcoSeq(regItem.getCmoMcoSeq());
								itemContaHospitalar.setCmoEcoBolNumero(regItem.getCmoEcoBolNumero());
								itemContaHospitalar.setCmoEcoBolBsaCodigo(regItem.getCmoEcoBolBsaCodigo());
								itemContaHospitalar.setCmoEcoBolData(regItem.getCmoEcoBolData());
								itemContaHospitalar.setCmoEcoCsaCodigo(regItem.getCmoEcoCsaCodigo());
								itemContaHospitalar.setCmoSequencia(regItem.getCmoSequencia());
								itemContaHospitalar.setPrescricaoProcedimento(regItem.getPrescricaoProcedimento());
								itemContaHospitalar.setPrescricaoNpt(regItem.getPrescricaoNpt());
								itemContaHospitalar.setCmoEcoSeqp(regItem.getCmoEcoSeqp());
								itemContaHospitalar.setPrescricaoPaciente(regItem.getPrescricaoPaciente());
								itemContaHospitalar.setPaoSeq(regItem.getPaoSeq());
								itemContaHospitalar.setServidor(regItem.getServidor());
								itemContaHospitalar.setCriadoEm(regItem.getCriadoEm());
								itemContaHospitalar.setCriadoPor(regItem.getCriadoPor());
								itemContaHospitalar.setServidoresAlterado(regItem.getServidoresAlterado());
								itemContaHospitalar.setServidorCriado(regItem.getServidorCriado());
								itemContaHospitalar.setServidorAnest(regItem.getServidorAnest());
								itemContaHospitalar.setIndSituacao(situacao);

								itemContaHospitalarON
										.inserirItemContaHospitalarSemValidacoesForms(
												itemContaHospitalar, true,
												servidorLogado, dataFimVinculoServidor, null);
								//faturamentoFacade.evict(itemContaHospitalar);
							} catch (Exception e) {
								LOG.error(EXCECAO_CAPTURADA, e);
								throw new ApplicationBusinessException(
										FaturamentoExceptionCode.MENSAGEM_ERRO_GERAR_ITEM_CONTA_REAPRESENTACAO,
										e.getMessage());
							}
						}

						vIchSeq++;
						logar("Inseriu itens {0} seq {1}", vIchSeq, regItem
								.getId().getSeq());
					}
				}

				logar("fim loop");

				logar("update: {0}", pCthSeq);

				FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO
						.obterPorChavePrimaria(pCthSeq);

				FatContasHospitalares oldContaHospitalar = null;
				try {
					oldContaHospitalar = faturamentoFacade
							.clonarContaHospitalar(contaHospitalar);
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}

				contaHospitalar.setIndSituacao(DominioSituacaoConta.R);
				contaHospitalar
						.setMotivoRejeicao(pRjcSeq != null ? fatMotivoRejeicaoContaDAO
								.obterPorChavePrimaria(pRjcSeq) : null);

				contaHospitalarON.atualizarContaHospitalar(contaHospitalar,
						oldContaHospitalar, true, nomeMicrocomputador, dataFimVinculoServidor);

				//faturamentoFacade.evict(contaHospitalar);

				logar("update R");

				FatAih fatAih = regConta.getAih();
				if (fatAih != null) {
					FatAih oldFatAih = null;
					try {
						oldFatAih = faturamentoRN.clonarFatAih(fatAih);
					} catch (Exception e) {
						LOG.error(EXCECAO_CAPTURADA, e);
						FaturamentoExceptionCode.ERRO_AO_ATUALIZAR
								.throwException();
					}

					fatAih.setIndSituacao(DominioSituacaoAih.R);

					faturamentoRN.atualizarFatAih(fatAih, oldFatAih);
					
					//faturamentoFacade.evict(fatAih);
				}

				logar("update aih");

				// Mantem relacionamento mae/filha de desdobram p/contas
				// reapresentadas:
				// Verifica se a conta original era gerada por
				// desdobramento(possuia mae)
				if (regConta != null && regConta.getContaHospitalar() != null) {
					// Verifica se a conta mae tambem foi reapresentada
					vCthReap = null;

					Integer seq = fatContasHospitalaresDAO
							.obtemPrimeiroSeqContaHospitalarReapresentada(regConta
									.getContaHospitalar() != null ? regConta
									.getContaHospitalar().getSeq() : null);

					if (seq != null) {
						vCthReap = seq;
					}

					contaHospitalar = fatContasHospitalaresDAO
							.obterPorChavePrimaria(vCthSeq);

					oldContaHospitalar = null;
					try {
						oldContaHospitalar = faturamentoFacade
								.clonarContaHospitalar(contaHospitalar);
					} catch (Exception e) {
						LOG.error(EXCECAO_CAPTURADA, e);
						FaturamentoExceptionCode.ERRO_AO_ATUALIZAR
								.throwException();
					}

					FatContasHospitalares auxContaHospitalar = null;
					if (vCthReap != null
							|| regConta.getContaHospitalar() != null) {
						auxContaHospitalar = (vCthReap == null ? regConta
								.getContaHospitalar()
								: fatContasHospitalaresDAO
										.obterPorChavePrimaria(vCthReap));
					}

					contaHospitalar.setContaHospitalar(auxContaHospitalar);
					contaHospitalarON.atualizarContaHospitalar(contaHospitalar,
							oldContaHospitalar, true, nomeMicrocomputador, dataFimVinculoServidor);

					//faturamentoFacade.evict(contaHospitalar);
				}

				// Verifica se a conta original era desdobrada (possuia filha)
				Integer seq = fatContasHospitalaresDAO
						.obtemPrimeiroSeqContaHospitalarFilhaDesdobramento(regConta
								.getSeq());
				if (seq != null) {
					vCthFilha = seq;

					logar("possui filha{0}", vCthFilha);

					// Verifica se a conta filha tambem foi reapresentada
					vCthReap = null;

					seq = fatContasHospitalaresDAO
							.obtemPrimeiroSeqContaHospitalarReapresentada(vCthFilha);
					if (seq != null) {
						vCthReap = seq;

						// Faz a conta filha que ja havia sido reapresentada
						// referenciar a conta mae que esta sendo reapresentada
						contaHospitalar = fatContasHospitalaresDAO
								.obterPorChavePrimaria(vCthReap);

						oldContaHospitalar = null;
						try {
							oldContaHospitalar = faturamentoFacade
									.clonarContaHospitalar(contaHospitalar);
						} catch (Exception e) {
							LOG.error(EXCECAO_CAPTURADA, e);
							FaturamentoExceptionCode.ERRO_AO_ATUALIZAR
									.throwException();
						}

						FatContasHospitalares auxContaHospitalar = null;
						if (vCthSeq != null) {
							auxContaHospitalar = fatContasHospitalaresDAO
									.obterPorChavePrimaria(vCthSeq);
						}

						contaHospitalar.setContaHospitalar(auxContaHospitalar);
						contaHospitalarON.atualizarContaHospitalar(
								contaHospitalar, oldContaHospitalar, true, nomeMicrocomputador, dataFimVinculoServidor);

						//faturamentoFacade.evict(contaHospitalar);
					} else {
						contaHospitalar = fatContasHospitalaresDAO
								.obterPorChavePrimaria(vCthFilha);

						oldContaHospitalar = null;
						try {
							oldContaHospitalar = faturamentoFacade
									.clonarContaHospitalar(contaHospitalar);
						} catch (Exception e) {
							LOG.error(EXCECAO_CAPTURADA, e);
							FaturamentoExceptionCode.ERRO_AO_ATUALIZAR
									.throwException();
						}

						FatContasHospitalares auxContaHospitalar = null;
						if (vCthSeq != null) {
							auxContaHospitalar = fatContasHospitalaresDAO
									.obterPorChavePrimaria(vCthSeq);
						}

						contaHospitalar.setContaHospitalar(auxContaHospitalar);
						contaHospitalarON.atualizarContaHospitalar(
								contaHospitalar, oldContaHospitalar, true, nomeMicrocomputador, dataFimVinculoServidor);

						//faturamentoFacade.evict(contaHospitalar);

						logar("referencia mae filha {0}", vCthFilha);
					}
				}

				// Verifica se conta possui proced pago pelo sus
				Boolean retorno = FatkCth5RN.fatcValidaProcUti(pCthSeq);
				if (Boolean.TRUE.equals(retorno)) {
					// Atualiza banco de UTI
					RnSutcAtuSaldoVO retornoVO = saldoUtiAtualizacaoRN
							.atualizarSaldoDiariasUti(
									DominioAtualizacaoSaldo.D,
									regConta.getDtAltaAdministrativa(),
									regConta.getDiasUtiMesInicial(),
									regConta.getDiasUtiMesAnterior(),
									regConta.getDiasUtiMesAlta(),
									regConta.getIndIdadeUti(), 
									nomeMicrocomputador);

					if (retornoVO == null
							|| !Boolean.TRUE.equals(retornoVO.isRetorno())) {
						logar("problema UTI");
						result = false;
					}

					// Atualiza Banco de Capacidades- Reapresentação (-)
					FatEspelhoAih espelhoAih = fatEspelhoAihDAO
							.buscarPrimeiroEspelhoAIHIndBcoCapacNulo(pCthSeq,
									1, DominioModuloCompetencia.INT);
					if (espelhoAih != null) {
						rBuscaConta = espelhoAih;
						saldoBancoCapacidadeAtulizacaoRN
								.atualizarSaldoDiariasBancoCapacidade(
										rBuscaConta.getDataInternacao(),
										rBuscaConta.getDataSaida(),
										rBuscaConta.getEspecialidadeAih() != null ? rBuscaConta
												.getEspecialidadeAih()
												.intValue() : null, rBuscaConta
												.getId().getCthSeq(),
										DominioAtualizacaoSaldo.D, true, nomeMicrocomputador, dataFimVinculoServidor);
					}
				}
			} else {
				result = false;
			}
			//faturamentoFacade.evict(rBuscaConta);

			//super.commitTransaction();
			//super.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
			
			// Vincula nova conta a aih
			FatAih fatAih = regConta.getAih();
			if (fatAih != null) {
				FatAih oldFatAih = null;
				try {
					oldFatAih = faturamentoRN.clonarFatAih(fatAih);
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}

				fatAih.setIndSituacao(DominioSituacaoAih.A);
				if (vCthSeq != null) {
					fatAih.setContaHospitalar(fatContasHospitalaresDAO
							.obterPorChavePrimaria(vCthSeq));
				}

				faturamentoRN.atualizarFatAih(fatAih, oldFatAih);
				//faturamentoFacade.evict(fatAih);
			}

			this.logar("update aih com nova cth");

			//super.commitTransaction();
			//super.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);

			return result;
		} catch (BaseException e) {
			//super.rollbackTransaction();
			LOG.error(EXCECAO_CAPTURADA, e);
			throw e;
		} catch(ConstraintViolationException e) {
			//super.rollbackTransaction();
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(
					FaturamentoExceptionCode.MENSAGEM_ERRO_FINALIZACAO_REAPRESENTACAO,
					(e.getLocalizedMessage()));
		} catch (Exception e) {
			//super.rollbackTransaction();
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(
					FaturamentoExceptionCode.MENSAGEM_ERRO_FINALIZACAO_REAPRESENTACAO,
					e.getMessage());
		}
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_DATAS
	 * 
	 * @throws BaseException
	 */
	public RnCthcVerDatasVO rnCthcVerDatas(Integer pIntSeq, Date pDataNova,
			Date pDataAnterior, String pTipoData, final Date dataFimVinculoServidor) throws BaseException {
		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		final FatContasInternacaoDAO fatContasInternacaoDAO = getFatContasInternacaoDAO();
		
		final RnCthcVerDatasVO retorno = new RnCthcVerDatasVO();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		retorno.setRetorno(Boolean.TRUE);
		List<FatContasHospitalares> listaConta = fatContasInternacaoDAO
				.listarContasHospitalares(pIntSeq, pDataAnterior, pTipoData);
		if (listaConta != null && !listaConta.isEmpty()) {
			for (FatContasHospitalares regConta : listaConta) {
				// default flag true, pode alterar contas de convenio 21/08/00
				if (CoreUtil.igual("I", pTipoData)) { // verificar internacao
														// administrativa
					if (CoreUtil.isMenorDatas(
							regConta.getDataInternacaoAdministrativa(),
							pDataNova)) {
						Object[] minItensObject = fatItemContaHospitalarDAO
								.buscarPrimeiroMinDthrRealizado(
										regConta.getSeq(), pDataNova);
						
						if (minItensObject != null) {
							FatItemContaHospitalarVO minItens = popularObjetoFatItemContaHospitalarVO(minItensObject);
							retorno.setPhiSeq(minItens.getPhiSeq());
							retorno.setDataLimite(minItens.getDthrRealizado());
							retorno.setRetorno(Boolean.FALSE);
						}
					}
				} else { // pTipoData = 'A' verificar alta administrativa
					if (CoreUtil.isMaiorDatas((Date) CoreUtil.nvl(
							regConta.getDtAltaAdministrativa(), new Date()),
							pDataNova)) {
						FatItemContaHospitalarVO maxItens = fatItemContaHospitalarDAO
								.buscarPrimeiroMaxDthrRealizado(
										regConta.getSeq(), pDataNova);
						if (maxItens != null && maxItens.getPhiSeq() != null) {
							retorno.setPhiSeq(maxItens.getPhiSeq());
							retorno.setDataLimite(maxItens.getDthrRealizado());
							retorno.setRetorno(Boolean.FALSE);

							List<FatItemContaHospitalar> listaItensExa = fatItemContaHospitalarDAO
									.listarItemExa(regConta.getSeq(), pDataNova);
							if (listaItensExa != null
									&& !listaItensExa.isEmpty()) {
								for (FatItemContaHospitalar regMaxExa : listaItensExa) {
									if (CoreUtil.isMaiorDatas(
											regMaxExa.getDthrRealizado(),
											pDataNova)) {
										// dthr_realizado assume
										// dthr_alta_medica
										try {
											FatItemContaHospitalar oldFatItensContaHospitalar = null;
											try {
												oldFatItensContaHospitalar = getItemContaHospitalarON()
														.clonarItemContaHospitalar(
																regMaxExa);
											} catch (Exception e) {
												LOG.error(EXCECAO_CAPTURADA, e);
												FaturamentoExceptionCode.ERRO_AO_ATUALIZAR
														.throwException();
											}
											regMaxExa
													.setDthrRealizado(pDataNova);
											getItemContaHospitalarON()
													.atualizarItemContaHospitalarSemValidacoesForms(
															regMaxExa,
															oldFatItensContaHospitalar,
															true, servidorLogado, dataFimVinculoServidor, null, null);
											//faturamentoFacade.evict(regMaxExa);
											retorno.setRetorno(Boolean.TRUE);
										} catch (Exception e) {
											FaturamentoExceptionCode.ERRO_RN_CTHC_VER_DATAS
													.throwException(e
															.getMessage());
										}
									}
								}
							}
							if (retorno.getRetorno()) {
								List<FatItemContaHospitalar> listaItemPendente = fatItemContaHospitalarDAO
										.listarItemPendente(regConta.getSeq(),
												pDataNova);
								if (listaItemPendente != null
										&& !listaItemPendente.isEmpty()) {
									retorno.setRetorno(Boolean.FALSE);
								}
							}
						}
					}
				}
				if (!retorno.getRetorno()) {
					break;
				}
			}
		}
		return retorno;
	}

	private FatItemContaHospitalarVO popularObjetoFatItemContaHospitalarVO(
			Object[] minItensObject) {

		FatItemContaHospitalarVO intVO = new FatItemContaHospitalarVO();
		if (minItensObject[0] != null) {
			intVO.setPhiSeq((Integer) minItensObject[0]);
		}
		if (minItensObject[1] != null) {
			intVO.setDthrRealizado((Date) minItensObject[1]);
		}

		return intVO;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHP_TROCA_CNV
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public void rnCthpTrocaCnv(Integer pIntSeq, Date pDtInt, Short pCnvOld,
			Byte pCspOld, Short pCnvNew, Byte pCspNew, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		final IAghuFacade aghuFacade = getAghuFacade();
		final IRegistroColaboradorFacade registroColaboradorFacade = getRegistroColaboradorFacade();
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		final FatConvenioSaudePlanoDAO fatConvenioSaudePlanoDAO = getFatConvenioSaudePlanoDAO();
		final FatContasInternacaoDAO fatContasInternacaoDAO = getFatContasInternacaoDAO();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatAihDAO fatAihDAO = getFatAihDAO();

		ItemParaTrocaConvenioVO rItensConta = null;
		FatContasHospitalares rConta = null;
		Integer vCthSeq = null;
		Long vQtdCthAih = null;
		Long vNroAih = null;
		Short vCnv75 = null;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		vCnv75 = buscarVlrShortAghParametro(AghuParametrosEnum.P_COD_CONV_75);

		logar("old={0} new={1} param={2}", pCnvOld, pCnvNew, vCnv75);

		// Se passou de cnv 75 p/outro nao tem conta para cancelar
		if (!CoreUtil.igual(pCnvOld, vCnv75)) {
			// Se passou para cnv 75, passa cada item para PMR
			if (CoreUtil.igual(pCnvNew, vCnv75)) {
				// Podem existir contas sem itens para troca de convenio
				Integer internacaoSeq = pIntSeq;
				Date dtInternacaoAdministrativa = pDtInt;
				DominioSituacaoItenConta situacaoItemContaHospitalar = DominioSituacaoItenConta.A;
				DominioSituacaoConta[] situacoesContaHospitalar = new DominioSituacaoConta[] {
						DominioSituacaoConta.A, DominioSituacaoConta.F };

				List<ItemParaTrocaConvenioVO> listaItensConta = fatItemContaHospitalarDAO
						.listarItensParaTrocaConvenio(internacaoSeq,
								dtInternacaoAdministrativa,
								situacaoItemContaHospitalar,
								situacoesContaHospitalar);

				if (listaItensConta != null && !listaItensConta.isEmpty()) {
					for (int i = 0; i < listaItensConta.size(); i++) {
						rItensConta = listaItensConta.get(i);

						// Passa os itens para ambulatorio
						try {
							FatProcedAmbRealizado fatProcedAmbRealizado = new FatProcedAmbRealizado();

							fatProcedAmbRealizado.setDthrRealizado(rItensConta
									.getItemContaInternacao()
									.getDthrRealizado());
							fatProcedAmbRealizado
									.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
							fatProcedAmbRealizado
									.setLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B);
							fatProcedAmbRealizado.setQuantidade(rItensConta
									.getItemContaInternacao()
									.getQuantidadeRealizada());
							fatProcedAmbRealizado
									.setItemSolicitacaoExame(getExamesLaudosFacade()
											.obterAelItemSolicitacaoExamesPorChavePrimaria(
													new AelItemSolicitacaoExamesId(
															rItensConta
																	.getItemContaInternacao()
																	.getIseSoeSeq(),
															rItensConta
																	.getItemContaInternacao()
																	.getIseSeqp())));
							fatProcedAmbRealizado
									.setProcedimentoHospitalarInterno(rItensConta
											.getItemContaInternacao()
											.getProcedimentoHospitalarInterno());
							fatProcedAmbRealizado
									.setServidor(registroColaboradorFacade
											.obterRapServidor(new RapServidoresId(
													9999999, (short) 955)));
							fatProcedAmbRealizado
									.setUnidadeFuncional(rItensConta
											.getItemContaInternacao()
											.getUnidadesFuncional() != null ? rItensConta
											.getItemContaInternacao()
											.getUnidadesFuncional()
											: aghuFacade
													.obterAghUnidadesFuncionaisPorChavePrimaria(buscarVlrShortAghParametro(AghuParametrosEnum.P_UNID_AMBULATORIO)));
							fatProcedAmbRealizado.setEspecialidade(rItensConta
									.getAtendimento().getEspecialidade());
							fatProcedAmbRealizado.setPaciente(rItensConta
									.getAtendimento().getPaciente());
							fatProcedAmbRealizado
									.setConvenioSaudePlano(fatConvenioSaudePlanoDAO
											.obterPorChavePrimaria(new FatConvenioSaudePlanoId(
													pCnvNew, pCspNew)));

							DominioIndOrigemItemContaHospitalar auxOrigem = rItensConta
									.getItemContaInternacao().getIndOrigem();
							DominioOrigemProcedimentoAmbulatorialRealizado origem = null;
							if (CoreUtil.igual(
									DominioIndOrigemItemContaHospitalar.AEL,
									auxOrigem)) {
								origem = DominioOrigemProcedimentoAmbulatorialRealizado.EXA;
							} else if (CoreUtil.igual(
									DominioIndOrigemItemContaHospitalar.ABS,
									auxOrigem)) {
								origem = DominioOrigemProcedimentoAmbulatorialRealizado.BSA;
							} else if (CoreUtil.igual(
									DominioIndOrigemItemContaHospitalar.BCC,
									auxOrigem)) {
								origem = DominioOrigemProcedimentoAmbulatorialRealizado.CIA;
							} else if (CoreUtil.igual(
									DominioIndOrigemItemContaHospitalar.MPM,
									auxOrigem)) {
								origem = DominioOrigemProcedimentoAmbulatorialRealizado.AIN;
							} else {
								origem = DominioOrigemProcedimentoAmbulatorialRealizado
										.valueOf(auxOrigem.toString());
							}
							fatProcedAmbRealizado.setIndOrigem(origem);

							MbcProcEspPorCirurgias mbcProcEspPorCirurgias = rItensConta
									.getItemContaInternacao()
									.getProcEspPorCirurgias();
							if (mbcProcEspPorCirurgias != null) {
								fatProcedAmbRealizado
										.setPpcCrgSeq(mbcProcEspPorCirurgias
												.getCirurgia().getSeq());
								fatProcedAmbRealizado
										.setPpcEprPciSeq(mbcProcEspPorCirurgias
												.getId().getEprPciSeq());
								fatProcedAmbRealizado
										.setPpcEprEspSeq(mbcProcEspPorCirurgias
												.getId().getEprEspSeq());
								fatProcedAmbRealizado
										.setPpcIndRespProc(mbcProcEspPorCirurgias
												.getId().getIndRespProc());
							}

							fatProcedAmbRealizado.setAtendimento(rItensConta
									.getAtendimento());
							fatProcedAmbRealizado.setCriadoEm(rItensConta
									.getItemContaInternacao().getCriadoEm());

							getProcedimentosAmbRealizadosON()
									.inserirFatProcedAmbRealizado(
											fatProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
							//faturamentoFacade.evict(fatProcedAmbRealizado);
						} catch (Exception e) {
							LOG.error(EXCECAO_CAPTURADA, e);
							// Não foi possivel inserir dados em ambulatorio.
							throw new ApplicationBusinessException(
									FaturamentoExceptionCode.FAT_00614);
						}

						vCthSeq = rItensConta.getContaHospitalar().getSeq();
					}
				}
			}

			// Busca a conta
			DominioSituacaoConta[] situacoesContaHospitalar = new DominioSituacaoConta[] {
					DominioSituacaoConta.A, DominioSituacaoConta.F };
			FatContasHospitalares contasHospitalar = fatContasInternacaoDAO
					.buscarPrimeiroContasHospitalares(pIntSeq,
							situacoesContaHospitalar);
			if (contasHospitalar != null) {
				rConta = contasHospitalar;

				vCthSeq = rConta.getSeq();
				vNroAih = rConta.getAih() != null ? rConta.getAih().getNroAih()
						: null;
			}

			// Cancela itens e conta se existir conta
			if (vCthSeq != null) {
				// Retira o Realizado da conta para poder cancelar
				FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO
						.obterPorChavePrimaria(vCthSeq);
				FatContasHospitalares contaOld = null;
				try {
					contaOld = faturamentoFacade
							.clonarContaHospitalar(contaHospitalar);
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					throw new BaseException(
							FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
				}
				contaHospitalar.setProcedimentoHospitalarInternoRealizado(null);

				faturamentoFacade.persistirContaHospitalar(contaHospitalar,
						contaOld, nomeMicrocomputador, dataFimVinculoServidor);
				//faturamentoFacade.evict(contaHospitalar);

				// Cancela itens da conta
				List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO
						.listarPorCth(vCthSeq);
				if (itensContaHospitalar != null
						&& !itensContaHospitalar.isEmpty()) {
					for (FatItemContaHospitalar itemContaHospitalar : itensContaHospitalar) {
						FatItemContaHospitalar itemContaHospitalarOld = null;
						try {
							itemContaHospitalarOld = faturamentoFacade
									.clonarItemContaHospitalar(itemContaHospitalar);
						} catch (Exception e) {
							LOG.error(EXCECAO_CAPTURADA, e);
							throw new BaseException(
									FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
						}
						itemContaHospitalar
								.setIndSituacao(DominioSituacaoItenConta.C);
						getItemContaHospitalarON()
								.atualizarItemContaHospitalarSemValidacoesForms(
										itemContaHospitalar, itemContaHospitalarOld, true, 
										servidorLogado, dataFimVinculoServidor, null, null);
						//faturamentoFacade.evict(itemContaHospitalar);
					}
				}

				// Cancela conta hospitalar e passa para o novo convenio
				contaHospitalar = fatContasHospitalaresDAO
						.obterPorChavePrimaria(vCthSeq);
				try {
					contaOld = faturamentoFacade
							.clonarContaHospitalar(contaHospitalar);
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					throw new BaseException(
							FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
				}
				contaHospitalar.setIndSituacao(DominioSituacaoConta.C);
				contaHospitalar.setAih(null);
				contaHospitalar.setIndImpAih(false);

				faturamentoFacade.persistirContaHospitalar(contaHospitalar,
						contaOld, nomeMicrocomputador, dataFimVinculoServidor);
				//faturamentoFacade.evict(contaHospitalar);

				if (vNroAih != null) {
					// Verifica quantas contas possuem a AIH
					vQtdCthAih = fatContasHospitalaresDAO
							.countContasHospitalaresQuePossuemAIH(vNroAih);

					// Se nenuma conta mais possui a AIH, Libera a AIH
					if (vQtdCthAih == null || CoreUtil.igual(vQtdCthAih, 0)) {
						try {
							FatAih aih = fatAihDAO
									.obterPorChavePrimaria(vNroAih);

							FatAih oldfatAih = getFaturamentoRN().clonarFatAih(
									aih);
							aih.setIndSituacao(DominioSituacaoAih.U);

							faturamentoFacade.atualizarFatAih(aih, oldfatAih);
						} catch (Exception e) {
							LOG.error(EXCECAO_CAPTURADA, e);
							throw new ApplicationBusinessException(
									FaturamentoExceptionCode.AIH_NAO_ENCONTRADO);
						}
					}
				}
			}
		}
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_NOTACRG
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcVerNotacrg(Integer pCth) throws BaseException {
		VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO = getVFatContaHospitalarPacDAO();
		VFatContaHospitalarPac vFatContaHospitalarPac = vFatContaHospitalarPacDAO
				.obterPorChavePrimaria(pCth);
		if (vFatContaHospitalarPac != null
				&& vFatContaHospitalarPac.getPacCodigo() != null) {
			return !(getBlocoCirurgicoFacade().pacienteTemCirurgiaSemNota(
					vFatContaHospitalarPac.getPacCodigo(),
					vFatContaHospitalarPac.getCthDtIntAdministrativa(),
					vFatContaHospitalarPac.getCthDtAltaAdministrativa()));
		}
		return Boolean.TRUE;
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_INFECCAO
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcVerInfeccao(Integer atdSeq) throws BaseException {

		return getControleInfeccaoFacade()
				.obterVerificacaoMvtoInfeccaoDeAtendimento(atdSeq);
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHP_VER_AIHSOLIC
	 * 
	 * @throws BaseException
	 */
	public void rnCthpVerAihSolic(Long pCthNroAih, Integer pCthPhiSeq)
			throws BaseException {
		if (pCthNroAih != null && pCthPhiSeq == null) {
			throw new ApplicationBusinessException(
					FaturamentoExceptionCode.FAT_00624);
		}
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_REAPRES
	 * 
	 * @param pSeq
	 * @return
	 * @throws BaseException
	 */
	public Boolean rnCthcVerReapres(Integer pSeq) throws BaseException {
		FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();

		FatContasHospitalares fat = fatContasHospitalaresDAO
				.obterPorChavePrimaria(pSeq);
		if (fat.getContaHospitalarReapresentada() == null
				|| fat.getContaHospitalarReapresentada().getSeq() == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_CTHC_VER_DESDOBR
	 * 
	 * @param pSeq
	 * @return
	 * @throws BaseException
	 */
	public Boolean rnCthcVerDesdobr(Integer pSeq) throws BaseException {
		FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();

		FatContasHospitalares fat = fatContasHospitalaresDAO
				.obterPorChavePrimaria(pSeq);
		if (fat.getContaHospitalarReapresentada() == null
				|| fat.getContaHospitalarReapresentada().getContaHospitalar() == null
				|| fat.getContaHospitalarReapresentada().getContaHospitalar()
						.getSeq() == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ORADB Procedure FATK_CTH_RN_UN.RN_FATP_EXCLUSAO_CRITICA
	 * 
	 * Busca código correspondente as exclusões de críticas.
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public Integer rnFatpExclusaoCritica(DominioSituacao pIdMenor,
			DominioSituacao pIdMaior, DominioSituacao pCbo,
			DominioSituacao pQtd, DominioSituacao pPermMenor, Integer pCthSeq,
			DominioSituacao pCns, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatExclusaoCriticaDAO fatExclusaoCriticaDAO = getFatExclusaoCriticaDAO();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatEspelhoAihDAO fatEspelhoAihDAO = getFatEspelhoAihDAO();

		DominioSituacao vIdMenor = DominioSituacao.I;
		DominioSituacao vIdMaior = DominioSituacao.I;
		DominioSituacao vCbo = DominioSituacao.I;
		DominioSituacao vQtd = DominioSituacao.I;
		DominioSituacao vPermMenor = DominioSituacao.I;
		DominioSituacao vCns = DominioSituacao.I;
		FatExclusaoCritica vCodConta = null;

		FatExclusaoCritica vCodigo = null;

		logar("RN_FATP_EXCLUSAO_CRITICA");
		logar("P_ID_MENOR: {0} P_ID_MAIOR: {1} P_CBO: {2} P_QTD: {3} P_PERM_MENOR: {4} P_CTH_SEQ: {5} P_CNS: {6}",
				pIdMenor, pIdMaior, pCbo, pQtd, pPermMenor, pCthSeq, pCns);

		// Verifica na fat_contas_hospitalares se já existe um código de exclusão de crítica gravado.
		FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);

		/*#31004*/
		if (contaHospitalar != null) {
			vCodConta = contaHospitalar.getExclusaoCritica();
		}

		if (vCodConta != null && DominioSituacao.A.equals(vCodConta.getIndSituacao())) {
			logar("v_cod_conta: {0}", vCodConta.getCodigo());
			vIdMenor = vCodConta.getIdMenor();
			vIdMaior = vCodConta.getIdMaior();
			vCbo = vCodConta.getCbo();
			vQtd = vCodConta.getQtd();
			vPermMenor = vCodConta.getPermMenor();
			vCns = vCodConta.getCns();
		} else {
			logar("v_cod_conta:  ");
		}

		if (CoreUtil.igual(DominioSituacao.A, pIdMenor)) {
			vIdMenor = DominioSituacao.A;
		}

		if (CoreUtil.igual(DominioSituacao.A, pIdMaior)) {
			vIdMaior = DominioSituacao.A;
		}

		if (CoreUtil.igual(DominioSituacao.A, pCbo)) {
			vCbo = DominioSituacao.A;
		}

		if (CoreUtil.igual(DominioSituacao.A, pQtd)) {
			vQtd = DominioSituacao.A;
		}

		if (CoreUtil.igual(DominioSituacao.A, pPermMenor)) {
			vPermMenor = DominioSituacao.A;
		}

		if (CoreUtil.igual(DominioSituacao.A, pCns)) {
			vCns = DominioSituacao.A;
		}

		// Busca código correspondente na tabela FAT_EXCLUSAO_CRITICAS
		logar("v_id_menor: {0} v_id_maior: {1} v_cbo: {2} v_qtd: {3} v_perm_menor: {4} v_cns: {5}",
				vIdMenor, vIdMaior, vCbo, vQtd, vPermMenor, vCns);

		FatExclusaoCritica exclusaoCritica = fatExclusaoCriticaDAO
				.buscarPrimeiraExclusaoCritica(vIdMenor, vIdMaior, vCbo, vQtd, vPermMenor, vCns, DominioSituacao.A);
		if (exclusaoCritica != null) {
			vCodigo = exclusaoCritica;
		}

		logar("EXCLUSAO CRITICA - CODIGO: {0}",
				(vCodigo != null ? vCodigo.getCodigo() : null));

		// Atualiza valor na fat_contas_hospitalares
		try {
			FatContasHospitalares contaOld = null;
			try {
				contaOld = faturamentoFacade
						.clonarContaHospitalar(contaHospitalar);
			} catch (Exception e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				throw new BaseException(
						FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
			}
			contaHospitalar.setExclusaoCritica(vCodigo);
			faturamentoFacade.persistirContaHospitalar(contaHospitalar,
					contaOld, nomeMicrocomputador, dataFimVinculoServidor);
			//faturamentoFacade.evict(contaHospitalar);
		} catch (Exception e) {
			logar("nao atualizou a conta com a exclusão de critica "
					+ e.getMessage());
		}

		// Atualiza valor na fat_espelhos_aih
		try {
			FatEspelhoAih fatEspelhoAih = fatEspelhoAihDAO
					.obterPorChavePrimaria(new FatEspelhoAihId(pCthSeq, 1));
			if (fatEspelhoAih != null) {
				fatEspelhoAih.setExclusaoCritica(vCodigo != null ? vCodigo
						.getCodigo() : null);

				faturamentoFacade.atualizarFatEspelhoAih(fatEspelhoAih);
				//faturamentoFacade.evict(fatEspelhoAih);
			}
		} catch (Exception e) {
			logar("nao atualizou o espelho da conta com a exclusão de critica "
					+ e.getMessage());
		}

		Integer retorno = null;
		if (vCodigo != null) {
			try {
				retorno = Integer.valueOf(vCodigo.getCodigo());
			} catch (Exception e) {
				logar(MSG_EXCECAO_IGNORADA, e);
			}
		}

		return retorno;
	}

	protected FatkCth5RN getFatkCth5RN() {
		return fatkCth5RN;
	}

	protected FatkCth4RN getFatkCth4RN() {
		return fatkCth4RN;
	}

	protected ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON() {
		return procedimentosAmbRealizadosON;
	}

	protected CidContaHospitalarPersist getCidContaHospitalarPersist() {
		return cidContaHospitalarPersist;
	}
	
	protected FatContaInternacaoPersist getFatContaInternacaoPersist() {
		return fatContaInternacaoPersist;
	}

	protected FatMotivoRejeicaoContaDAO getFatMotivoRejeicaoContaDAO() {
		return fatMotivoRejeicaoContaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
