package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioLimite;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioTipoAltaUTI;
import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;
import br.gov.mec.aghu.dominio.DominioTipoItemEspelhoContaHospitalar;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemProcHospTranspDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatPacienteTransplantesDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.faturamento.vo.AinMovimentosInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.FatContasIntPacCirurgiasVO;
import br.gov.mec.aghu.faturamento.vo.FatVariaveisVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcBuscaContaVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerDiariasVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerItemSusVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerPacCtaVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerUtimesesVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.SceRmrPacienteVO;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AipPacienteDadoClinicos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatDadosContaSemInt;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;
import br.gov.mec.aghu.model.FatEspelhoItemContaHospId;
import br.gov.mec.aghu.model.FatItemProcHospTransp;
import br.gov.mec.aghu.model.FatItemProcHospTranspId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.model.FatPacienteTransplantes;
import br.gov.mec.aghu.model.FatPacienteTransplantesId;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Refere-se a package FATK_CTH4_RN_UN
 */
@SuppressWarnings({"PMD.NcssTypeCount", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FatkCth4RN extends AbstractFatDebugLogEnableRN {


	private static final String PERIODO_INCIA_E_TERMINA_ANTES_DO_INICIO_DA_CONTA = "periodo incia e termina antes do inicio da conta";

	private static final String INT = "INT";

	private static final String MSG_NAO_IDENTIFICOU = "NAO FOI POSSIVEL IDENTIFICAR PACIENTE DA CONTA PARA CALCULAR DIARIAS DE UTI.";

	private static final String NOVA_DTLIMITE_0_ = "nova dtlimite: {0}";

	private static final String RN_CTHC_VER_UTIMESES = "RN_CTHC_VER_UTIMESES";

	private static final String RN_CTHC_ATU_LANCAUTI = "RN_CTHC_ATU_LANCAUTI";

	@EJB
	private FatkCthRN fatkCthRN;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(FatkCth4RN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}


	private static final long serialVersionUID = -5202596189971205969L;
	
	private static final DateFormat DIA_MES_ANO_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * ORADB Procedure FATK_CTH4_RN_UN.RN_CTHC_VER_UTIMESES
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	public RnCthcVerUtimesesVO rnCthcVerUtimeses(final Integer pCthSeq)
			throws BaseException {
		
		final IAghuFacade aghuFacade = this.getAghuFacade();
		final RnCthcVerUtimesesVO rnCthcVerUtimesesVO = new RnCthcVerUtimesesVO();
		
		final FaturamentoON faturamentoON = getFaturamentoON();
		final FatkCthRN fatkCthRN = getFatkCthRN();

		FatContasHospitalares regCta = null;
		Integer vIntSeq = null;
		Date vDtIniAtd = null;
		Integer vSeqDsc = null;
		Boolean vSsmCobraUtiAlta = Boolean.FALSE;
		Short vPho = null;
		Integer vIph = null;
		Long vCodSus = null;
		DominioLimite flagLimite = null;
		Byte vMspCodSus = null;
		String vMotivo = null;
		Date vDtAltaMed = null;
		DominioTipoAltaUTI vTipoUti = null;
		DominioTipoAltaUTI vPrimeiraUti = null;
		Byte vDiariasIni = 0;
		Byte vDiariasAnt = 0;
		Byte vDiariasAlta = 0;
		Date vDtIniCta = null;
		Date vDtFimCta = null;
		Integer vPhi = null;
		Integer vPacCodigo = null;
		Integer vPacProntuario = null;
		Date vDtObito = null;
		Integer vDiariasConta = 0;
		Integer vDiasConta = 0;
		Boolean vUtiNeo = Boolean.FALSE;
		Date vDtIniUti = null;
		Date vDtFimUti = null;
		Integer vMesIniUti = 0;
		Integer vMesFimUti = 0;
		Date vDtFimAnt = null;
		Date vDtFimAux = null;
		Boolean vQuebraApos = Boolean.FALSE;
		Boolean vCalcular = null;
		Integer vMesAlta = 0;
		Integer vMesAnt = 0;
		Integer vMesIni = 0;
		Byte vDiasMesIni = 0;
		Byte vDiasMesAnt = 0;
		Byte vDiasMesAlta = 0;
		Integer vDiariasFora = 0;
		Integer vDiariasAux = 0;
		Integer vEspaco = 0;

		logar("*** CALCULO UTI ********");

		// Busca paciente da conta hospitalar
		RnCthcVerPacCtaVO rnCthcVerPacCtaVO = fatkCthRN.rnCthcVerPacCta(pCthSeq);

		if (rnCthcVerPacCtaVO == null || CoreUtil.igual(Boolean.FALSE,rnCthcVerPacCtaVO.getRetorno())) {
			faturamentoON.criarFatLogErrors(MSG_NAO_IDENTIFICOU, INT,
					pCthSeq, null, null, null, null, new Date(), null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, RN_CTHC_VER_UTIMESES, null, null, null);

			rnCthcVerUtimesesVO.setRetorno(false);
			return rnCthcVerUtimesesVO;
		} else {
			vPacCodigo = rnCthcVerPacCtaVO.getPacCodigo();
			vPacProntuario = rnCthcVerPacCtaVO.getPacProntuario();
			vIntSeq = rnCthcVerPacCtaVO.getIntSeq();
		}

		// Busca data de inicio da conta (internacao)
		regCta = getFatContasHospitalaresDAO().obterPorChavePrimaria(pCthSeq);

		vDtIniCta = trunc(regCta != null ? regCta.getDataInternacaoAdministrativa() : null);

		logar("busca data de inicio da conta (internacao): {0}", vDtIniCta);

		Integer auxRegCtaProcedimentoHospitalarInterno = regCta != null && regCta.getProcedimentoHospitalarInterno() != null ? regCta
				.getProcedimentoHospitalarInterno().getSeq() : null;

		Integer auxRegCtaProcedimentoHospitalarInternoRealizado = regCta != null
				&& regCta.getProcedimentoHospitalarInternoRealizado() != null ? regCta.getProcedimentoHospitalarInternoRealizado()
				.getSeq() : null;

		if (vDtIniCta == null) {
			FatMensagemLog fatMensagemLog = new FatMensagemLog();
			fatMensagemLog.setCodigo(164);
			faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR DATA DE INTERNACAO DA CONTA PARA CALCULAR DIARIAS DE UTI.",
					INT, pCthSeq, null, null, null, null, new Date(), null, null, null, null, null, null, vPacCodigo,
					auxRegCtaProcedimentoHospitalarInterno, auxRegCtaProcedimentoHospitalarInternoRealizado, null, vPacProntuario,
					null, null, null, null, null, null, null, null, RN_CTHC_VER_UTIMESES, null, null,fatMensagemLog);

			rnCthcVerUtimesesVO.setRetorno(false);
			return rnCthcVerUtimesesVO;
		}

		// Busca data de fim da conta (alta)
		vDtFimCta = trunc(regCta != null ? regCta.getDtAltaAdministrativa() : null);

		logar("busca data de fim da conta (alta): {0}", vDtFimCta);

		if (vDtFimCta == null) {
			FatMensagemLog fatMensagemLog = new FatMensagemLog();
			fatMensagemLog.setCodigo(162);
			faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR DATA DE ALTA DA CONTA PARA CALCULAR DIARIAS DE UTI.", INT,
					pCthSeq, null, null, null, null, new Date(), null, null, null, null, null, null, vPacCodigo,
					auxRegCtaProcedimentoHospitalarInterno, auxRegCtaProcedimentoHospitalarInternoRealizado, null, vPacProntuario,
					null, null, null, null, null, null, null, null, RN_CTHC_VER_UTIMESES, null, null,fatMensagemLog);

			rnCthcVerUtimesesVO.setRetorno(false);
			return rnCthcVerUtimesesVO;
		}

		// Verifica diarias da conta
		vDiariasConta = DateUtil.calcularDiasEntreDatas(vDtIniCta, vDtFimCta);
		vDiasConta = vDiariasConta + 1;

		logar("verifica diarias da conta): {0}", vDiasConta);

		FatMotivoSaidaPaciente fatMotivoSaidaPaciente = regCta.getMotivoSaidaPaciente();
		if (fatMotivoSaidaPaciente != null) {
			vMspCodSus = fatMotivoSaidaPaciente.getCodigoSus();
		}

		if (vMspCodSus == null) {
			logar("MOTIVO E SITUACAO DE SAIDA DO PACIENTE NAO ENCONTRADO");
			FatMensagemLog fatMensagemLog = new FatMensagemLog();
			fatMensagemLog.setCodigo(168);
			faturamentoON.criarFatLogErrors(
					"NAO FOI POSSIVEL IDENTIFICAR O MOTIVO DE SAIDA DO PACIENTE PARA CALCULAR DIARIAS DE UTI.", INT, pCthSeq, null,
					null, null, null, new Date(), null, null, null, null, null, null, vPacCodigo,
					auxRegCtaProcedimentoHospitalarInterno, auxRegCtaProcedimentoHospitalarInternoRealizado, null, vPacProntuario,
					null, null, null, null, null, null, null, null, RN_CTHC_VER_UTIMESES, null, null,fatMensagemLog);

			rnCthcVerUtimesesVO.setRetorno(false);
			return rnCthcVerUtimesesVO;
		}

		vMotivo = vMspCodSus != null ? vMspCodSus.toString().substring(0, 1) : null;

		logar("busca motivo cobranca da conta : {0}", vMotivo);

		// Verifica se o paciente teve obito na UTI (p/cobrar dia da alta)
		AipPacientes paciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(vPacCodigo);

		if (paciente != null) {
			vDtObito = trunc(paciente.getDtObito());
		}

		logar("verifica se o paciente teve obito na UTI (p/cobrar dia da alta) : {0}", vDtObito);

		// Verifica se proced solic/realiz permite cobrar UTI dia da alta
		if (auxRegCtaProcedimentoHospitalarInternoRealizado != null) {
			vPhi = auxRegCtaProcedimentoHospitalarInternoRealizado;
		}

		logar("verifica se proced solic/realiz permite cobrar UTI dia da alta : {0}", vPhi);

		if (vPhi != null) {
			RnCthcVerItemSusVO rnCthcVerItemSusVO = fatkCthRN
					.rnCthcVerItemSus(DominioOrigemProcedimento.I, regCta.getConvenioSaudePlano() != null ? regCta
							.getConvenioSaudePlano().getId().getCnvCodigo() : null, regCta.getConvenioSaudePlano() != null ? regCta
							.getConvenioSaudePlano().getId().getSeq() : null, (short) 1, vPhi, null);

			Boolean aux = false;
			if (rnCthcVerItemSusVO != null) {
				aux = rnCthcVerItemSusVO.getRetorno();
				vPho = rnCthcVerItemSusVO.getPhoSeq();
				vIph = rnCthcVerItemSusVO.getIphSeq();
				vCodSus = rnCthcVerItemSusVO.getCodSus();
			}

			if (aux) {
				vSsmCobraUtiAlta = fatkCthRN.rnCthcVerUtidalta(vPho, vIph);
			} else {
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(179);
				faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR PROCEDIMENTO REALIZADO PARA CALCULAR UTI.", INT,
						pCthSeq, null, null, null, null, new Date(), null, null, null, null, null, null, vPacCodigo,
						auxRegCtaProcedimentoHospitalarInterno, auxRegCtaProcedimentoHospitalarInternoRealizado, null, vPacProntuario,
						null, null, null, null, null, null, null, null, RN_CTHC_VER_UTIMESES, null, null,fatMensagemLog);

				rnCthcVerUtimesesVO.setRetorno(false);
				return rnCthcVerUtimesesVO;
			}
		}

		logar("DT ATD {0} DT ALT MED {1} DT OBITO {2}", vDtIniAtd, vDtAltaMed, vDtObito);
		logar("DT INT {0} DT ALTA {1} MOT {2} DIAR {3}", vDtIniCta, vDtFimCta, vMotivo, vDiariasConta);
		logar("DIAS " + vDiasConta);

		if (vSsmCobraUtiAlta) {
			logar(" SSM {0} cobra dia alta", vCodSus);
		} else {
			logar(" SSM {0} NAO cobra dia alta", vCodSus);
		}

		// Verifica meses para os campos de uti do cma:
		SimpleDateFormat sdf = new SimpleDateFormat();
		Calendar cal = Calendar.getInstance();
		cal.setTime(vDtFimCta);

		sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
		vMesAlta = Integer.valueOf(sdf.format(cal.getTime()));

		sdf.applyPattern("dd");
		vDiasMesAlta = Byte.valueOf(sdf.format(cal.getTime()));

		cal.add(Calendar.MONTH, -1);

		sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
		vMesAnt = Integer.valueOf(sdf.format(cal.getTime()));

		vDiasMesAnt = DateUtil.obterQtdeDiasMes(cal.getTime()).byteValue();

		cal.add(Calendar.MONTH, -1);

		sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
		vMesIni = Integer.valueOf(sdf.format(cal.getTime()));

		vDiasMesIni = DateUtil.obterQtdeDiasMes(cal.getTime()).byteValue();

		logar(" verifica meses para os campos de uti do cma ");
		logar(" v_mes_alta {0}", vMesAlta);
		logar(" v_dias_mes_alta {0}", vDiasMesAlta);
		logar(" v_mes_ant {0}", vMesAnt);
		logar(" v_mes_ini {0}", vMesIni);
		logar(" v_dias_mes_ini {0}", vDiasMesIni);

		// Verifica se mes inicial, anterior ou alta é o mes de inicio da conta
		// e considera o dia da internacao p/determinar espaco p/cobranca no mes
		sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
		Integer auxAnoMesDtIniCta = Integer.valueOf(sdf.format(vDtIniCta));

		cal.setTime(vDtIniCta);
		if (CoreUtil.igual(auxAnoMesDtIniCta,vMesIni)) {
			vDiasMesIni = (byte) (vDiasMesIni - cal.get(Calendar.DAY_OF_MONTH) + 1);
		} else if (CoreUtil.igual(auxAnoMesDtIniCta,vMesAnt)) {
			vDiasMesIni = 0;
			vDiasMesAnt = (byte) (vDiasMesAnt - cal.get(Calendar.DAY_OF_MONTH) + 1);
		} else if (CoreUtil.igual(auxAnoMesDtIniCta,vMesAlta)) {
			vDiasMesIni = 0;
			vDiasMesAnt = 0;
			vDiasMesAlta = (byte) (vDiasMesAlta - cal.get(Calendar.DAY_OF_MONTH) + 1);
		}
		
		logar(" verifica se mes inicial, anterior ou alta e o mes de inicio da conta ");
		logar(" v_dias_mes_ini {0}", vDiasMesIni);
		logar(" v_dias_mes_ant {0}", vDiasMesAnt);
		logar(" v_dias_mes_alta {0}", vDiasMesAlta);

		// Verifica cobranca dia da alta p/determinar espaco p/cobranca mes da
		// alta
		final Integer pCodMotivoSaidaObito = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_MOTIVO_SAIDA_OBITO);
		final Integer pCodMotivoSaidaOutros = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_MOTIVO_SAIDA_OUTROS);
		
		if (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
			// Nao obito
			logar("nao foi obito, tira um dia no espaco alta...");

			// Por diarias (nao por dias)
			vDiasMesAlta = (byte) (vDiasMesAlta - 1);
		}

		logar("INI: MES {0} DIAS {1} / ANT: MES {2} DIAS {3} / ALTA: MES {4} DIAS {5}",
				vMesIni, vDiasMesIni, vMesAnt, vDiasMesAnt, vMesAlta, vDiasMesAlta);

		vPrimeiraUti = null;
		vTipoUti = null;
		vUtiNeo = Boolean.FALSE;

		logar(" ********** UTI *************");
 
		if (regCta.getContasInternacao() != null && !regCta.getContasInternacao().isEmpty()) {
			List<FatContasInternacao> contasInternacao = new ArrayList<FatContasInternacao>(regCta.getContasInternacao());
			Collections.sort(contasInternacao, new Comparator<FatContasInternacao>() {
				@Override
				public int compare(FatContasInternacao o1, FatContasInternacao o2) {
					return o1.getInternacao().getSeq().compareTo(o2.getInternacao().getSeq());
				}			
			});
			
			for (FatContasInternacao fatContaInternacao : contasInternacao) {
				vIntSeq = fatContaInternacao.getInternacao() != null ? fatContaInternacao.getInternacao().getSeq() : null;

				// Verifica data de alta medica (p/cobrar dia da alta)
				vDtAltaMed = trunc(fatContaInternacao.getInternacao() != null ? fatContaInternacao.getInternacao().getDthrAltaMedica()
						: null);

				// Busca data do inicio do atendimento da internacao
				vDtIniAtd = trunc(fatContaInternacao.getInternacao() != null
						&& fatContaInternacao.getInternacao().getAtendimento() != null ? fatContaInternacao.getInternacao()
						.getAtendimento().getDthrInicio() : null);

				logar("-->>>>> pegou internacao v_dt_alta_med {0} v_dt_ini_atd: {1} v_int_seq: {2}", vDtAltaMed, vDtIniAtd, vIntSeq);

				if (vDtIniAtd == null) {
					FatDadosContaSemInt dadosContaSemInt = getFatDadosContaSemIntDAO().buscarDadosContaSemInt(pCthSeq);

					if (dadosContaSemInt != null) {
						vSeqDsc = dadosContaSemInt.getSeq();
						logar("v_seq_dsc: {0}", vSeqDsc);
					} else {
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(163);
						faturamentoON.criarFatLogErrors(
								"NAO FOI POSSIVEL IDENTIFICAR DATA DE INICIO DO ATENDIMENTO PARA CALCULAR DIARIAS DE UTI.", INT,
								pCthSeq, null, null, null, null, new Date(), null, null, null, null, null, null, vPacCodigo,
								auxRegCtaProcedimentoHospitalarInterno, auxRegCtaProcedimentoHospitalarInternoRealizado, null,
								vPacProntuario, null, null, null, null, null, null, null, null, RN_CTHC_VER_UTIMESES, null, null,fatMensagemLog);

						rnCthcVerUtimesesVO.setRetorno(false);
						return rnCthcVerUtimesesVO;
					}
				}

				logar("chama procedure calculos");

				logar("v_int_seq: {0} v_dt_ini_atd: {1} v_dt_fim_cta: {2}", vIntSeq, vDtIniAtd, vDtFimCta);

				// Tipo de movimento: "3-Estorno" e "5-Ingresso em S.O."
				final Integer[] codigosTipoMovimentoInternacao = new Integer[] {
						buscarVlrInteiroAghParametro(AghuParametrosEnum.P_TIPO_MOVIMENTO_ESTORNO),
						buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_INGR_SO) };

				// Busca os movimentos da internacao desde o inicio do atendimento
				List<AinMovimentosInternacao> movimentosInternacoes = getInternacaoFacade().listarMovimentosInternacao(vIntSeq,vDtIniAtd, vDtFimCta, codigosTipoMovimentoInternacao);
				final Byte mvtIntAlta = buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_ALTA);
				if (movimentosInternacoes != null && !movimentosInternacoes.isEmpty()) {
					for(AinMovimentosInternacao regMvi : movimentosInternacoes){

						// Verifica se a UNF é de UTI e, se for, verfica o tipo (I,II,III)
						ConstanteAghCaractUnidFuncionais[] caracteristicas = new ConstanteAghCaractUnidFuncionais[] { ConstanteAghCaractUnidFuncionais.DIARIA_UTI_1,
																													  ConstanteAghCaractUnidFuncionais.DIARIA_UTI_2, 
																													  ConstanteAghCaractUnidFuncionais.DIARIA_UTI_3
																													};

						List<AghCaractUnidFuncionais> caracteristicasUnidadesFuncionais = getAghuFacade().listarCaracteristicasUnidadesFuncionais(regMvi.getUnidadeFuncional() != null ? regMvi.getUnidadeFuncional().getSeq() : null, caracteristicas, null, null);
						if (caracteristicasUnidadesFuncionais != null && !caracteristicasUnidadesFuncionais.isEmpty()) {
							ConstanteAghCaractUnidFuncionais caracteristica = caracteristicasUnidadesFuncionais.get(0).getId().getCaracteristica();
							if(CoreUtil.igual(ConstanteAghCaractUnidFuncionais.DIARIA_UTI_1,caracteristica)){
								vTipoUti = DominioTipoAltaUTI.POSITIVO_1;
							} else if(CoreUtil.igual(ConstanteAghCaractUnidFuncionais.DIARIA_UTI_2,caracteristica)){
								vTipoUti = DominioTipoAltaUTI.POSITIVO_2;
							} else if(CoreUtil.igual(ConstanteAghCaractUnidFuncionais.DIARIA_UTI_3,caracteristica)){
								vTipoUti = DominioTipoAltaUTI.POSITIVO_3;
							} else {
								vTipoUti = null;
							}
						} else {
							vTipoUti = null;
						}

						// Verifica se a UNF é de UTI NeoNatal
						caracteristicas = new ConstanteAghCaractUnidFuncionais[] { ConstanteAghCaractUnidFuncionais.UNID_UTIN };
						caracteristicasUnidadesFuncionais = aghuFacade.listarCaracteristicasUnidadesFuncionais(regMvi.getUnidadeFuncional() != null ? regMvi.getUnidadeFuncional().getSeq() : null, caracteristicas, null, null);
						if (caracteristicasUnidadesFuncionais != null && !caracteristicasUnidadesFuncionais.isEmpty()) {
							vUtiNeo = Boolean.TRUE;
						}

						sdf.applyPattern("dd/MM/yyyy"); 
						logar("1 MVI:DT {0} TMI {1} UNF {2} TIPO {3} QRT {4} LTO {5} ESP {6} utin {7}",
								sdf.format(regMvi.getDthrLancamento()),
								(regMvi.getTipoMovimentoInternacao() != null ? regMvi.getTipoMovimentoInternacao().getCodigo() : ""),
								(regMvi.getUnidadeFuncional() != null ? regMvi.getUnidadeFuncional().getSeq() : ""),
								vTipoUti != null ? vTipoUti.getCodigo() : null,
								(regMvi.getQuarto() != null ? regMvi.getQuarto().getDescricao() : ""),
								(regMvi.getLeito() != null ? regMvi.getLeito().getLeitoID() : ""),
								(regMvi.getEspecialidade() != null ? regMvi.getEspecialidade().getSeq() : ""),
								vUtiNeo);

						if (vDtIniUti == null) {
							if (vTipoUti != null) {
								// É uma unid funcional de UTI
								logar("mvi UTI");
								vDtIniUti = trunc(regMvi.getDthrLancamento());
								if (vPrimeiraUti == null) {
									vPrimeiraUti = vTipoUti;
									logar("1a primeira uti: {0}", vPrimeiraUti != null ? vPrimeiraUti.getCodigo() : null);
								}
							}
						} else {
							
							if (vTipoUti == null || CoreUtil.igual(regMvi.getTipoMovimentoInternacao().getCodigo(),mvtIntAlta)) {
								logar("mvi nao UTI ou alta da uti tmi_seq={0}", mvtIntAlta);
								vDtFimUti = trunc(regMvi.getDthrLancamento());
								// Verifica casos de saida e entrada na uti no
								// mesmo dia
								if (vDtFimAnt != null) {
									logar("teve mvi ant");
									if ((vDtIniUti == null && vDtFimAnt == null) || (vDtIniUti != null && CoreUtil.igual(vDtIniUti,vDtFimAnt))) {
										logar("ini UTI = fim UTI ant");
										if (!CoreUtil.igual(vDtFimUti,vDtIniUti)) {
											if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
													|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
												// Calculou por dias o periodo
												// anterior (e nao por diarias)
												logar("ini UTI <> fim UTI - pega dia seguinte pq o primeiro ja foi no calculo do mvi anterior");
												// Calcula a partir do dia
												// seguinte pois o primeiro dia
												// ja foi contado no calculo do
												// periodo anterior
												cal = Calendar.getInstance();
												cal.setTime(vDtIniUti);
												cal.add(Calendar.DAY_OF_MONTH, 1);

												vDtIniUti = cal.getTime();
												vCalcular = true;
											}
										} else {
											logar("ini UTI = fim UTI - nao calcula o mvi e pega o proximo");

											// Nao calcula este periodo e vai
											// pegar o proximo movimento
											vCalcular = false;
											vDtFimAnt = vDtFimUti;
											if ((vDtFimUti == null && vDtFimCta == null)
													|| (vDtFimUti != null && CoreUtil.igual(vDtFimUti,vDtFimCta))) {
												vDtIniUti = null;
												vDtFimUti = null;
												vMesIniUti = null;
												vMesFimUti = null;
												break;
											}

											vDtIniUti = null;
											vDtFimUti = null;
											vMesIniUti = null;
											vMesFimUti = null;
										}
									}
								} else {
									logar("nao teve fim ant");
									vCalcular = true;
								}

								if (vCalcular) {
									sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
									vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));
									vMesFimUti = Integer.valueOf(sdf.format(vDtFimUti));

									logar("2 MVI UTI (*): INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);

									// Calcular as diarias e depois limpar
									// vDtIniUti e vDtFimUti:
									// Verifica se periodo inicia e termina
									// antes do inicio da cta
									if (vDtIniUti != null && vDtIniUti.before(vDtIniCta) && vDtFimUti != null
											&& vDtFimUti.before(vDtIniCta)) {
										logar(PERIODO_INCIA_E_TERMINA_ANTES_DO_INICIO_DA_CONTA);
										vDtIniUti = null;
										vDtFimUti = null;
										vMesIniUti = null;
										vMesFimUti = null;
									}

									// Verifica se periodo inicia antes,termina
									// depois do inicio conta
									if (DateValidator.validaDataMenor(vDtIniUti, vDtIniCta)
											&& DateUtil.validaDataMaior(vDtFimUti, vDtIniCta)) {
										vDtIniUti = vDtIniCta;

										sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
										vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));

										logar("3 QUEBRA MVI UTI antes cta a: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
									}

									if (DateUtil.validaDataMaiorIgual(vDtIniUti, vDtIniCta)) {
										// Verifica se o periodo inicia e
										// termina antes do mes inicial
										if (CoreUtil.menor(vMesIniUti, vMesIni) && CoreUtil.menor(vMesFimUti, vMesIni)) {
											vDiariasFora = vDiariasFora + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimUti);

											logar("3.1 diarias fora 1a: {0}", vDiariasFora);
											// Se pode cobrar dia da saida, soma
											// 1 diaria
											if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
													|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
												vDiariasFora++;
												logar("3.2 diarias fora 1a2: {0}", vDiariasFora);
											}
										}

										// Verifica se periodo inicia antes e
										// termina depois mes inicial
										if (CoreUtil.menor(vMesIniUti, vMesIni) && CoreUtil.maiorOuIgual(vMesFimUti, vMesIni)) {
											// Marca fim periodo como ultimo
											// dia mes anterior ao mes inicial
											cal = Calendar.getInstance();
											cal.set(Calendar.DAY_OF_MONTH, 1);
											cal.set(Calendar.MONTH, (vMesIni%100)-1);
											cal.set(Calendar.YEAR, vMesIni / 100);
											cal.add(Calendar.DAY_OF_MONTH, -1);

											vDtFimAux = trunc(cal.getTime());

											logar("4 QUEBRA MVI UTI antes ini: FIM aux {0}  INI {1} mes {2}", vDtFimAux, vDtIniUti, vMesIniUti);
											vDiariasFora = vDiariasFora + (DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimAux));
											logar("5 diarias fora 1: {0}", vDiariasFora);

											// Se pode cobrar dia da saida, soma
											// 1 diaria
											if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
													|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
												vDiariasFora = vDiariasFora + 1;
												logar("6 diarias fora 1b: {0}", vDiariasFora);
											}

											if (DateUtil.validaDataMaior(vDtFimUti, vDtFimAux)) {
												vQuebraApos = Boolean.TRUE;
												// Marca inicio do periodo como
												// primeiro dia do mes inicial
												// (mes seguinte ao "aux")
												cal.setTime(vDtFimAux);
												cal.add(Calendar.MONTH, 1);
												cal.set(Calendar.DAY_OF_MONTH, 1);

												vDtIniUti = trunc(cal.getTime());

												sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
												vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));
												logar("7 QUEBRA MVI UTI pos-calc: ANTES INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
											}
										}

										// Verifica se o periodo esta dentro do
										// mes inicial
										if(CoreUtil.igual(vMesIniUti, vMesIni) && CoreUtil.igual(vMesFimUti, vMesIni)) {
											if (vQuebraApos && CoreUtil.igual(vDtIniUti,vDtFimUti)) {
												logar("7b volta 1 dia pra calcular");
												cal.setTime(vDtIniUti);
												cal.add(Calendar.DAY_OF_MONTH, -1);
												vDtIniUti = cal.getTime();
												vQuebraApos = Boolean.FALSE;
											}

											// Calcula diarias p/ campo mes
											// inicial:
											vDiariasIni = (byte) (vDiariasIni + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimUti));

											if (CoreUtil.maior(vDiariasIni, vDiasMesIni)) {
												vDiariasFora = vDiariasFora + (vDiariasIni - vDiasMesIni);
												vDiariasIni = vDiasMesIni;
											}

											logar("8 diarias ini 1: {0}", vDiariasIni);

											int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
											if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))) {
												logar("estourou no mes Inicial 1");

												flagLimite = DominioLimite.I;
												vDtIniUti = null;
												vDtFimUti = null;
												vMesIniUti = null;
												vMesFimUti = null;
												break;
											}

											// Se pode cobrar dia da saida, soma
											// 1 diaria no mes
											if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
													|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
												vDiariasIni++;
												if (CoreUtil.maior(vDiariasIni, vDiasMesIni)) {
													vDiariasFora = vDiariasFora + (vDiariasIni - vDiasMesIni);
													vDiariasIni = vDiasMesIni;
												}

												logar("9 diarias ini 1b: {0}", vDiariasIni);

												auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
												if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
														&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
														|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
																&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
														|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
														|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))) {
													logar("estourou no mes Inicial 2");

													flagLimite = DominioLimite.I;
													vDtIniUti = null;
													vDtFimUti = null;
													vMesIniUti = null;
													vMesFimUti = null;

													break;
												}
											}
										}

										// Verifica se o periodo inicia no mes inicial e termina depois
										if (CoreUtil.igual(vMesIniUti,vMesIni) && CoreUtil.maior(vMesFimUti, vMesIni)) {
											// Calcula diarias p/campo mes inicial(ate ultimo dia mes inicial)
											vDtFimAux = trunc(DateUtil.obterDataFimCompetencia(vDtIniUti));

											logar("10 QUEBRA MVI UTI ini: FIM aux {0}", vDtFimAux);

											// Calcula diarias e soma 1 por causa da troca de mes (ultimo dia do mes nao entrou no calculo)
											vDiariasIni = (byte) (vDiariasIni + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimAux) + 1);

											if (CoreUtil.maior(vDiariasIni, vDiasMesIni)) {
												vDiariasFora = vDiariasFora + (vDiariasIni - vDiasMesIni);
												vDiariasIni = vDiasMesIni;
											}

											logar("11 diarias ini 2: {0}", vDiariasIni);

											int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
											if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))) {
												logar("estourou no mes Inicial 3");

												flagLimite = DominioLimite.I;
												vDtIniUti = null;
												vDtFimUti = null;
												vMesIniUti = null;
												vMesFimUti = null;

												break;
											}

											if (vDtFimUti != null && vDtFimUti.after(vDtFimAux)) {
												vQuebraApos = Boolean.TRUE;
												// Marca inicio periodo como
												// 1o.dia do mes seguinte ao
												// inicial
												cal.setTime(vDtFimAux);
												cal.set(Calendar.DAY_OF_MONTH, 1);
												cal.add(Calendar.MONTH, 1);

												vDtIniUti = trunc(cal.getTime());

												sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
												vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));
												logar("12 QUEBRA MVI UTI pos-calc: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
											}
										}

										// Verifica se o periodo esta dentro do  mes anterior
										if(CoreUtil.igual(vMesAnt,vMesIniUti) && CoreUtil.igual(vMesAnt,vMesFimUti)) {
											if (vQuebraApos && DateUtil.isDatasIguais(vDtIniUti, vDtFimUti)) {
												logar("12b volta 1 dia pra calcular");
												cal.setTime(vDtIniUti);
												cal.add(Calendar.DAY_OF_MONTH, -1);

												vDtIniUti = cal.getTime();
												vQuebraApos = Boolean.FALSE;
											}

											// Calcula diarias p/ campo mes anterior
											vDiariasAnt = (byte) (vDiariasAnt + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimUti));

											if (CoreUtil.maior(vDiariasAnt, vDiasMesAnt)) {
												vDiariasFora = vDiariasFora + (vDiariasAnt - vDiasMesAnt);
												vDiariasAnt = vDiasMesAnt;
											}

											logar("13 diarias ant 1: {0}", vDiariasAnt);

											int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
											
											if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
														&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
														|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
																&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
														|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
														|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))) {
												logar("estourou no mes aNterior 1");

												flagLimite = DominioLimite.N;
												vDtIniUti = null;
												vDtFimUti = null;
												vMesIniUti = null;
												vMesFimUti = null;

												break;
											}

											// Se pode cobrar dia da saida,  soma 1 diaria no mes
											if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
														|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
												vDiariasAnt++;
												if (CoreUtil.maior(vDiariasAnt, vDiasMesAnt)) {
													vDiariasFora = vDiariasFora + (vDiariasAnt - vDiasMesAnt);
													vDiariasAnt = vDiasMesAnt;
												}

												logar("14 diarias ant 1b: {0}", vDiariasAnt);

												auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
												
												if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
															&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
															|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
																	&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																			.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
															|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																	&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																			.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
															|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																	&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																			.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))) {
													logar("estourou no mes aNterior 2");

													flagLimite = DominioLimite.N;
													vDtIniUti = null;
													vDtFimUti = null;
													vMesIniUti = null;
													vMesFimUti = null;

													break;
												}
											}
										}

										// Verifica se o periodo inicia no mes anterior e termina depois
										if (CoreUtil.igual(vMesAnt,vMesIniUti) && CoreUtil.maior(vMesFimUti, vMesAnt)) {
												
											// Calcula diarias p/ campo mes anterior (ate ultimo dia do mes anterior):
											vDtFimAux = trunc(DateUtil.obterDataFimCompetencia(vDtIniUti));

											logar("15 QUEBRA MVI UTI ant: FIM aux {0}", vDtFimAux);

											// Calcula diarias e soma 1 por  causa da troca de mes (ultimo
											// dia do mes nao entrou no calculo)
											vDiariasAnt = (byte) (vDiariasAnt + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimAux) + 1);

											if (CoreUtil.maior(vDiariasAnt, vDiasMesAnt)) {
												vDiariasFora = vDiariasFora + (vDiariasAnt - vDiasMesAnt);
												vDiariasAnt = vDiasMesAnt;
											}

											logar("16 diarias ant 2: {0}", vDiariasAnt);

											int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
											
											if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											) {
												logar("estourou no mes aNterior 3");

												flagLimite = DominioLimite.N;
												vDtIniUti = null;
												vDtFimUti = null;
												vMesIniUti = null;
												vMesFimUti = null;

												break;
											}

											if (vDtFimUti != null && vDtFimUti.after(vDtFimAux)) {
												vQuebraApos = Boolean.TRUE;

												// Marca inicio periodo como
												// 1o.dia do mes seguinte ao
												// anterior
												cal.setTime(vDtFimAux);
												cal.add(Calendar.MONTH, 1);
												cal.set(Calendar.DAY_OF_MONTH, 1);

												vDtIniUti = trunc(cal.getTime());

												sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
												vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));
												logar("17 QUEBRA MVI UTI pos-calc: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
											}
										}

										// Verifica se o periodo esta dentro do mes de alta
										if (CoreUtil.igual(vMesAlta,vMesIniUti) && CoreUtil.igual(vMesAlta,vMesFimUti)) {
											if (vQuebraApos && CoreUtil.igual(vDtIniUti,vDtFimUti)) {
												logar("17b volta 1 dia pra calcular");

												cal.setTime(vDtIniUti);
												cal.add(Calendar.DAY_OF_MONTH, -1);

												vDtIniUti = cal.getTime();
												vQuebraApos = Boolean.FALSE;
											}

											// Verifica se o fim da uti foi depois do fim da conta
											if (vDtFimUti != null && vDtFimUti.after(vDtFimCta)) {
												// Calcula diarias p/campo  mes alta(ate dia do fim  da conta):
												vDtFimAux = vDtFimCta;

												logar("18 QUEBRA MVI UTI alta: FIM aux " + vDtFimAux);

												vDiariasAlta = (byte) (vDiariasAlta + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimAux));

												if (CoreUtil.maior(vDiariasAlta, vDiasMesAlta)) {
													vDiariasFora = vDiariasFora + (vDiariasAlta - vDiasMesAlta);
													vDiariasAlta = vDiasMesAlta;
												}

												logar("19 diarias alta 01: {0}", vDiariasAlta);

												int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
												
												if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
														&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
														|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
																&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
														|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
														|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))) {
													logar("estourou no mes aLta 1");

													flagLimite = DominioLimite.L;
													vDtIniUti = null;
													vDtFimUti = null;
													vMesIniUti = null;
													vMesFimUti = null;

													break;
												}

												// Marca inicio do periodo  como dia do fim da conta
												vDtIniUti = vDtFimAux;

												sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
												vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));

												logar("20 QUEBRA MVI UTI pos-calc: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
												
											} else {
												// Calcula diarias p/ campo  mes alta
												vDiariasAlta = (byte) (vDiariasAlta + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimUti));

												if (CoreUtil.maior(vDiariasAlta, vDiasMesAlta)) {
													vDiariasFora = vDiariasFora + (vDiariasAlta - vDiasMesAlta);
													vDiariasAlta = vDiasMesAlta;
												}

												logar("21 diarias alta 1: {0}", vDiariasAlta);

												int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
												if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
														&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
														|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
																&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
														|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
														|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
												) {
													logar("estourou no mes aLta 2");

													flagLimite = DominioLimite.L;
													vDtIniUti = null;
													vDtFimUti = null;
													vMesIniUti = null;
													vMesFimUti = null;

													break;
												}
											}

											// Se paciente teve obito ou se pode cobrar dia da saida, soma 1 diaria no mes
											if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
													|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
												logar("XX diarias alta ETB: {0}", vDiariasAlta);
												if (CoreUtil.maior(vDiariasAlta, vDiasMesAlta)) {
													vDiariasFora = vDiariasFora + (vDiariasAlta - vDiasMesAlta);
													vDiariasAlta = vDiasMesAlta;
												}

												logar("22 diarias alta 1c: {0}", vDiariasAlta);

												int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
												if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
														&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
														|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
																&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
														|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
														|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))) {
													logar("estourou no mes aLta 3");

													flagLimite = DominioLimite.L;
													vDtIniUti = null;
													vDtFimUti = null;
													vMesIniUti = null;
													vMesFimUti = null;

													break;
												}
											}

											if (vDiariasAlta != null && vDiariasAlta.intValue() == 0) {
												if (CoreUtil.igual(vDtFimCta,vDtAltaMed) || !CoreUtil.igual(vDtFimCta,vDtFimUti)) {
													vDiariasAlta++;
													if (CoreUtil.maior(vDiariasAlta, vDiasMesAlta)) {
														vDiariasFora = vDiariasFora + (vDiariasAlta - vDiasMesAlta);
														vDiariasAlta = vDiasMesAlta;
													}

													logar("23 diarias alta 1b: {0}", vDiariasAlta);

													int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
													if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
															&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
															|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
																	&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																			.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
															|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																	&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																			.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
															|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																	&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																			.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
													) {
														logar("estourou no mes aLta 4");

														flagLimite = DominioLimite.L;
														vDtIniUti = null;
														vDtFimUti = null;
														vMesIniUti = null;
														vMesFimUti = null;

														break;
													}
												}
											} else if (CoreUtil.maior(vDiariasAlta, 0)) {
												int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;

												if (((CoreUtil.igual(vDtFimUti ,vDtFimCta)))
														&& ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
																&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta)) || (CoreUtil.igual(Boolean.FALSE
																,vSsmCobraUtiAlta)
																&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																		.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta)))
												){
													vDiariasAlta--;

													if (CoreUtil.maior(vDiariasAlta, vDiasMesAlta)) {
														vDiariasFora = vDiariasFora + (vDiariasAlta - vDiasMesAlta);
														vDiariasAlta = vDiasMesAlta;
													}

													logar("23b diarias alta 1b2: {0}", vDiariasAlta);

													auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
													if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
															&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
															|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
																	&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																			.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
															|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																	&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																			.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
															|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
																	&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																			.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))) {
														logar("estourou no mes aLta 5");

														flagLimite = DominioLimite.L;
														vDtIniUti = null;
														vDtFimUti = null;
														vMesIniUti = null;
														vMesFimUti = null;

														break;
													}
												}
											}
										} // if (CoreUtil.igual(vMesAlta,vMesIniUti) && CoreUtil.igual(vMesAlta,vMesFimUti)) {

										// Verifica se o periodo inicia no mes alta e termina depois
										if(CoreUtil.igual(vMesIniUti,vMesAlta) && CoreUtil.maior(vMesFimUti, vMesAlta)) {
											
											// Calcula diarias p/ campo mes alta (ate dia do fim da  conta):
											vDtFimAux = vDtFimCta;
											logar("24 QUEBRA MVI UTI alta: FIM aux {0}", vDtFimAux);
											vDiariasAlta = (byte) (vDiariasAlta + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimAux));
											if (CoreUtil.maior(vDiariasAlta, vDiasMesAlta)) {
												vDiariasFora = vDiariasFora + (vDiariasAlta - vDiasMesAlta);
												vDiariasAlta = vDiasMesAlta;
											}

											logar("25 diarias alta 02: {0}", vDiariasAlta);

											int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
											if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											){
												logar("estourou no mes aLta 6");

												flagLimite = DominioLimite.L;
												vDtIniUti = null;
												vDtFimUti = null;
												vMesIniUti = null;
												vMesFimUti = null;

												break;
											}

											// Marca inicio periodo como dia seguinte ao dia do fim da conta
											cal.setTime(vDtFimAux);
											cal.add(Calendar.DAY_OF_MONTH, 1);
											vDtIniUti = cal.getTime();

											sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
											vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));

											logar("26 QUEBRA MVI UTI pos-calc: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
											
										}
									} // if (DateUtil.validaDataMaiorIgual(vDtIniUti, vDtIniCta)) {

									vDtFimAnt = vDtFimUti;

									if (CoreUtil.igual(vDtFimUti,vDtFimCta)) {
										logar("dt fim uti = dt fim cta");
										vDtIniUti = null;
										vDtFimUti = null;
										vMesIniUti = null;
										vMesFimUti = null;
										break;
									}

									vDtIniUti = null;
									vDtFimUti = null;
									vMesIniUti = null;
									vMesFimUti = null;
									
								} // if (vCalcular) {
							} // if (vTipoUti == null || CoreUtil.igual(regMvi.getTipoMovimentoInternacao().getCodigo(),mvtIntAlta)) {
							
						} // } else {  --> if (vDtIniUti == null) {
					} // for (int i = 0; i < movimentosInternacoes.size(); i++) {
				} // if (movimentosInternacoes != null && !movimentosInternacoes.isEmpty()) {
			
				// Verifica casos em que paciente permaneceu em UTI ate a
				// alta (pois nesse caso nao calculou diarias do ultimo periodo em UTI)
				if (vDtIniUti != null) {
					logar("teve mvi entrada UTI mas nao teve mvi saida");
					vDtFimUti = vDtFimCta;

					// Verifica casos de saida e entrada na uti no mesmo dia
					if (vDtFimAnt != null) {
						logar("teve mvi anterior");
						if (CoreUtil.igual(vDtIniUti,vDtFimAnt)) {
							logar("dt mvi entrada UTI igual a saida anterior");

							if (!CoreUtil.igual(vDtIniUti,vDtFimUti)) {
								logar("dt entrada <> dt saida UTI");
								if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
										|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
									// Calculou por dias o periodo anterior
									// (e nao por diarias)
									logar("calculou dia");
									// Calcula a partir do dia seguinte pois
									// o primeiro dia ja foi contado no
									// calculo do periodo anterior
									cal.setTime(vDtIniUti);
									cal.add(Calendar.DAY_OF_MONTH, 1);

									vDtIniUti = cal.getTime();
									vCalcular = true;
								} else {
									logar("calculou diaria");
								}
							} else {
								logar("dt entrada = dt saida UTI");
								// Nao calcula este periodo e vai pegar o
								// proximo movimento
								vCalcular = false;
							}
						}
					} else {
						logar("nao teve mvi anterior");
						vCalcular = true;
					}

					if (vCalcular) {
						sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
						vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));
						vMesFimUti = Integer.valueOf(sdf.format(vDtFimUti));

						logar("27 MVI UTI: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);

						// Verifica se periodo inicia e termina antes do
						// inicio da cta
						if (vDtIniUti.before(vDtIniCta) && vDtFimUti.before(vDtIniCta)) {
							logar(PERIODO_INCIA_E_TERMINA_ANTES_DO_INICIO_DA_CONTA);
							vDtIniUti = null;
							vDtFimUti = null;
							vMesIniUti = null;
							vMesFimUti = null;
						}

						// Verifica se o periodo inicia antes,termina depois
						// do inicio da conta
						if (vDtIniUti.before(vDtIniCta) && vDtFimUti.after(vDtIniCta)) {
							vDtIniUti = vDtIniCta;

							sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
							vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));
							logar("28 QUEBRA MVI UTI antes cta b: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
						}

						if (DateUtil.validaDataMaiorIgual(vDtIniUti, vDtIniCta)) {
							// Verifica se o periodo inicia e termina antes do mes inicial
							if (CoreUtil.menor(vMesIniUti, vMesIni) && CoreUtil.menor(vMesFimUti, vMesIni)) {
								vDiariasFora = vDiariasFora + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimUti);
								logar("28.1 diarias fora 2a: {0}", vDiariasFora);

								// Se pode cobrar dia da saida, soma 1 diaria
								if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
										|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
									vDiariasFora = vDiariasFora + 1;
									logar("28.2 diarias fora 2a2: {0}", vDiariasFora);
								}
							}

							// Verifica se periodo inicia antes e termina depois mes inicial
							if (CoreUtil.menor(vMesIniUti, vMesIni) && CoreUtil.maiorOuIgual(vMesFimUti, vMesIni)) {
								// Marca fim periodo como ultimo dia do mes anterior ao mes inicial
								cal = Calendar.getInstance();
								cal.set(Calendar.DAY_OF_MONTH, 1);
								cal.set(Calendar.MONTH, (vMesIni%100)-1);
								cal.set(Calendar.YEAR, vMesIni / 100);
								cal.add(Calendar.DAY_OF_MONTH, -1);

								vDtFimAux = trunc(cal.getTime());

								logar("29 QUEBRA MVI UTI antes ini: FIM aux {0}", vDtFimAux);
								vDiariasFora = vDiariasFora + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimAux);
								logar("30 diarias fora 2: {0}", vDiariasFora);

								// Se pode cobrar dia da saida, soma 1 diaria
								if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
										|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
									vDiariasFora = vDiariasFora + 1;
									logar("31 diarias fora 2b: {0}", vDiariasFora);
								}

								if (vDtFimUti.after(vDtFimAux)) {
									vQuebraApos = Boolean.TRUE;
									// Marca inicio periodo como 1o.dia mes
									// inicial(seguinte ao "aux")
									cal.setTime(vDtFimAux);
									cal.add(Calendar.MONTH, 1);
									cal.set(Calendar.DAY_OF_MONTH, 1);

									vDtIniUti = trunc(cal.getTime());
									sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
									vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));
									logar("32 QUEBRA MVI UTI pos-calc: ANTES INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
								}
							}

							// Verifica se ainda nao estourou
							int auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
							if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
									&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString(),
											vMotivo)) && CoreUtil.menor(auxSomaDiarias, vDiariasConta))
									|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiasConta))
									|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menor(auxSomaDiarias, vDiariasConta))
									|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
											&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiasConta))
							 ){
								
								// Verifica se o periodo esta dentro do mes inicial
								if ((vMesIni == null && vMesIniUti == null && vMesFimUti == null)
										|| (vMesIni != null && CoreUtil.igual(vMesIni,vMesIniUti) && CoreUtil.igual(vMesIni,vMesFimUti))) {
									if (vQuebraApos
											&& ((vDtIniUti == null && vDtFimUti == null) || (vDtIniUti != null && CoreUtil.igual(vDtIniUti
													,vDtFimUti)))
									){
										logar("32b volta 1 dia pra calcular");
										cal.setTime(vDtIniUti);
										cal.add(Calendar.DAY_OF_MONTH, -1);

										vDtIniUti = cal.getTime();
										vQuebraApos = Boolean.FALSE;
									}

									// Calcula diarias p/ campo mes inicial:
									vDiariasIni = (byte) (vDiariasIni + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimUti));
									if (CoreUtil.maior(vDiariasIni, vDiasMesIni)) {
										vDiariasFora = vDiariasFora + (vDiariasIni - vDiasMesIni);
										vDiariasIni = vDiasMesIni;
									}

									logar("33 diarias ini 3: {0}", vDiariasIni);

									auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
									if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
									){
										logar("estourou no mes Inicial b");
										flagLimite = DominioLimite.I;
									}

									// Se pode cobrar dia da saida, soma 1
									// diaria no mes
									if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
											|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
										vDiariasIni++;
										if (CoreUtil.maior(vDiariasIni, vDiasMesIni)) {
											vDiariasFora = vDiariasFora + (vDiariasIni - vDiasMesIni);
											vDiariasIni = vDiasMesIni;
										}
										logar("34 diarias ini 3b: {0}", vDiariasIni);
									}

									auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
									if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
									){
										logar("estourou no mes Inicial b1");
										flagLimite = DominioLimite.I;
									}
								}
							}

							// Verifica se ainda nao estourou
							auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
							if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
									&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString(),
											vMotivo)) && CoreUtil.menor(auxSomaDiarias, vDiariasConta))
									|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiasConta))
									|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menor(auxSomaDiarias, vDiariasConta))
									|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
											&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiasConta))
							){
								// Verifica se o periodo inicia no mes inicial e termina depois
								if (vMesIni != null && CoreUtil.igual(vMesIni,vMesIniUti) && CoreUtil.maior(vMesFimUti, vMesIni)) {

									// Calcula diarias p/campo mes inicial(ate ultimo dia mes inicial)
									vDtFimAux = trunc(DateUtil.obterDataFimCompetencia(vDtIniUti));
									logar("35 QUEBRA MVI UTI ini 2: FIM aux {0}", vDtFimAux);

									// Calcula diarias e soma 1 por causa da
									// troca de mes (ultimo dia do mes nao
									// entrou no calculo)
									vDiariasIni = (byte) (vDiariasIni + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimAux) + 1);
									if (CoreUtil.maior(vDiariasIni, vDiasMesIni)) {
										vDiariasFora = vDiariasFora + (vDiariasIni - vDiasMesIni);
										vDiariasIni = vDiasMesIni;
									}

									auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
									if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
									){
										logar("estourou no mes Inicial b2");
										flagLimite = DominioLimite.I;
									}

									logar("36 diarias ini 4: {0}", vDiariasIni);
									if (vDtFimUti != null && vDtFimUti.after(vDtFimAux)) {
										vQuebraApos = Boolean.TRUE;

										// Marca inicio periodo como 1o. dia
										// do mes seguinte ao inicial
										cal.setTime(vDtFimAux);
										cal.add(Calendar.MONTH, 1);
										cal.set(Calendar.DAY_OF_MONTH, 1);

										vDtIniUti = trunc(cal.getTime());

										sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
										vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));
										logar("37 QUEBRA MVI UTI pos-calc: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
									}
								}
							}

							// Verifica se ainda nao estourou
							auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
							if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
									&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString(),
											vMotivo)) && CoreUtil.menor(auxSomaDiarias, vDiariasConta))
									|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiasConta))
									|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menor(auxSomaDiarias, vDiariasConta))
									|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
											&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiasConta))
							){
								// Verifica se o periodo esta dentro do mes anterior
								if ((vMesAnt == null && vMesIniUti == null && vMesFimUti == null)
										|| (vMesAnt != null && CoreUtil.igual(vMesAnt,vMesIniUti) && CoreUtil.igual(vMesAnt,vMesFimUti))) {
									if (vQuebraApos && DateUtil.isDatasIguais(vDtIniUti, vDtFimUti)) {
										logar("37b volta 1 dia pra calcular");
										cal.setTime(vDtIniUti);
										cal.add(Calendar.DAY_OF_MONTH, -1);

										vDtIniUti = cal.getTime();
										vQuebraApos = Boolean.FALSE;
									}

									// Calcula diarias p/ campo mes anterior
									vDiariasAnt = (byte) (vDiariasAnt + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimUti));

									if (CoreUtil.maior(vDiariasAnt, vDiasMesAnt)) {
										vDiariasFora = vDiariasFora + (vDiariasAnt - vDiasMesAnt);
										vDiariasAnt = vDiasMesAnt;
									}

									logar("38 diarias ant 3: {0}", vDiariasAnt);

									auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
									if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
									){
										logar("estourou no mes aNterior b1");
										flagLimite = DominioLimite.N;
									}

									// Se pode cobrar dia da saida, soma 1
									// diaria no mes
									if (vSsmCobraUtiAlta
											|| (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || 
												CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)
											   )
									) {
										vDiariasAnt++;

										if (CoreUtil.maior(vDiariasAnt, vDiasMesAnt)) {
											vDiariasFora = vDiariasFora + (vDiariasAnt - vDiasMesAnt);
											vDiariasAnt = vDiasMesAnt;
										}

										logar("39 diarias ant 3b: {0}", vDiariasAnt);
									}

									auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
									if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
									){
										logar("estourou no mes aNterior b12");
										flagLimite = DominioLimite.N;
									}
								}
							}

							// Verifica se ainda nao estourou
							auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
							if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
									&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString(),
											vMotivo)) && CoreUtil.menor(auxSomaDiarias, vDiariasConta))
									|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiasConta))
									|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menor(auxSomaDiarias, vDiariasConta))
									|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
											&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiasConta))
							){
								// Verifica se o periodo inicia no mes anterior e termina depois
								if (CoreUtil.maior(vMesFimUti, vMesAnt) && CoreUtil.igual(vMesAnt,vMesIniUti)) {
									// Calcula diarias p/campo mes
									// anterior(ate ultimo dia mes anterior)
									vDtFimAux = trunc(DateUtil.obterDataFimCompetencia(vDtIniUti));

									logar("40 QUEBRA MVI UTI ant 2: FIM aux {0}", vDtFimAux);

									// Calcula diarias e soma 1 por causa da
									// troca de mes (ultimo dia do mes nao
									// entrou no calculo)
									vDiariasAnt = (byte) (vDiariasAnt + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimAux) + 1);
									if (CoreUtil.maior(vDiariasAnt, vDiasMesAnt)) {
										vDiariasFora = vDiariasFora + (vDiariasAnt - vDiasMesAnt);
										vDiariasAnt = vDiasMesAnt;
									}

									logar("41 diarias ant 4: {0}", vDiariasAnt);

									auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
									if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
									){
										logar("estourou no mes aNterior b2");
										flagLimite = DominioLimite.N;
									}

									if (vDtFimUti.after(vDtFimAux)) {
										vQuebraApos = Boolean.TRUE;

										// Marca inicio periodo como 1o.dia
										// do mes seguinte ao anterior
										cal.setTime(vDtFimAux);
										cal.add(Calendar.MONTH, 1);
										cal.set(Calendar.DAY_OF_MONTH, 1);

										vDtIniUti = trunc(cal.getTime());
										sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
										vMesIniUti = Integer.valueOf(sdf.format(vDtIniUti));

										logar("42 QUEBRA MVI UTI pos-calc: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
									}
								}
							}

							// Verifica se ainda nao estourou
							auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
							if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
									&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString(),
											vMotivo)) && CoreUtil.menor(auxSomaDiarias, vDiariasConta))
									|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiasConta))
									|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menor(auxSomaDiarias, vDiariasConta))
									|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
											&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.menorOuIgual(auxSomaDiarias, vDiasConta))
							){
								// Verifica se o periodo esta dentro do mes  de alta
								if ((vMesIniUti == null && vMesAlta == null && vMesFimUti == null)
										|| (vMesAlta != null && CoreUtil.igual(vMesAlta,vMesIniUti) && CoreUtil.igual(vMesAlta,vMesFimUti))) {
									if (vQuebraApos && DateUtil.isDatasIguais(vDtIniUti, vDtFimUti)) {
										logar("42b volta 1 dia pra calcular");
										cal.setTime(vDtIniUti);
										cal.add(Calendar.DAY_OF_MONTH, -1);

										vDtIniUti = cal.getTime();
										vQuebraApos = Boolean.FALSE;
									}

									// Calcula diarias p/ campo mes alta
									vDiariasAlta = (byte) (vDiariasAlta + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimUti));

									if (CoreUtil.maior(vDiariasAlta, vDiasMesAlta)) {
										vDiariasFora = vDiariasFora + (vDiariasAlta - vDiasMesAlta);
										vDiariasAlta = vDiasMesAlta;
									}

									logar("43 diarias alta 3: {0}", vDiariasAlta);

									auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
									if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
											&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString()
													,vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
											|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
													&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
									){
										logar("estourou no mes aLta b1");
										flagLimite = DominioLimite.L;
									}

									// Se paciente teve obito ou se pode
									// cobrar dia da saida, soma 1 diaria no mes
									if (vSsmCobraUtiAlta || CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo)
											|| CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) {
										vDiariasAlta++;

										if (CoreUtil.maior(vDiariasAlta, vDiasMesAlta)) {
											vDiariasFora = vDiariasFora + (vDiariasAlta - vDiasMesAlta);
											vDiariasAlta = vDiasMesAlta;
										}

										logar("44 diarias alta 3c: {0}", vDiariasAlta);

										auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
										if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
												&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
														.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
												|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
														&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
												|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
														&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
												|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
														&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
										){
											logar("estourou no mes aLta b2");
											flagLimite = DominioLimite.L;
										}
									}

									if (vDiariasAlta != null && vDiariasAlta.intValue() == 0) {
										if (DateUtil.isDatasIguais(vDtFimCta, vDtAltaMed)
												|| !DateUtil.isDatasIguais(vDtFimUti, vDtFimCta)) {
											vDiariasAlta++;
											if (CoreUtil.maior(vDiariasAlta, vDiasMesAlta)) {
												vDiariasFora = vDiariasFora + (vDiariasAlta - vDiasMesAlta);
												vDiariasAlta = vDiasMesAlta;
											}

											logar("45 diarias alta 3b: {0}", vDiariasAlta);

											auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
											if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											){
												logar("estourou no mes aLta b3");
												flagLimite = DominioLimite.L;
											}
										}
									} else if (CoreUtil.maior(vDiariasAlta, 0)) {
										if (DateUtil.isDatasIguais(vDtFimUti, vDtFimCta)
												&& ((vSsmCobraUtiAlta && 
													(!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)) && CoreUtil.maiorOuIgual((vDiariasIni + vDiariasAnt + vDiariasAlta), vDiariasConta)) || 
													(!vSsmCobraUtiAlta && (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && 
																			!CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo)
																		  ) && CoreUtil.maiorOuIgual((vDiariasIni + vDiariasAnt + vDiariasAlta), vDiariasConta)
													)
												   )
										){
											vDiariasAlta--;
											if (CoreUtil.maior(vDiariasAlta, vDiasMesAlta)) {
												vDiariasFora = vDiariasFora + (vDiariasAlta - vDiasMesAlta);
												vDiariasAlta = vDiasMesAlta;
											}

											logar("45b diarias alta 3b2: {0}", vDiariasAlta);

											auxSomaDiarias = vDiariasIni + vDiariasAnt + vDiariasAlta;
											if ((CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
													&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
															.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.TRUE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && !CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maiorOuIgual(auxSomaDiarias, vDiariasConta))
													|| (CoreUtil.igual(Boolean.FALSE,vSsmCobraUtiAlta)
															&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || CoreUtil.igual(pCodMotivoSaidaOutros
																	.toString(),vMotivo)) && CoreUtil.maior(auxSomaDiarias, vDiasConta))
											){
												logar("estourou no mes aLta b4");
												flagLimite = DominioLimite.L;
											}
										}
									}
								} // if ((vMesIniUti == null && vMesAlta == null && vMesFimUti == null)
							} // Verifica se ainda nao estourou 
						} //if (DateUtil.validaDataMaiorIgual(vDtIniUti, vDtIniCta)) { ---> Verifica se o periodo inicia e termina antes do mes inicial
					} // if (vCalcular) { sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);
				} // ...alta (pois nesse caso nao calculou diarias do ultimo periodo em UTI) ---> if (vDtIniUti != null) {

				if (CoreUtil.igual(DominioLimite.I,flagLimite)) {
					if (vSsmCobraUtiAlta
							&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && 
									!CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						// Deve
						// ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<vDiariasConta
						vDiariasIni = (byte) (vDiariasIni - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiariasConta + 1));
					} else if (vSsmCobraUtiAlta
							&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) ||
									CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<=vDiasConta
						vDiariasIni = (byte) (vDiariasIni - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiasConta));
					} else if (!vSsmCobraUtiAlta
							&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && 
									!CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<vDiariasConta
						vDiariasIni = (byte) (vDiariasIni - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiariasConta + 1));
					} else if (!vSsmCobraUtiAlta
							&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || 
									CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<=vDiasConta
						vDiariasIni = (byte) (vDiariasIni - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiasConta));
					}
				} else if (CoreUtil.igual(DominioLimite.N,flagLimite)) {
					if (vSsmCobraUtiAlta
							&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && 
									!CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<vDiariasConta
						vDiariasAnt = (byte) (vDiariasAnt - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiariasConta + 1));
					} else if (vSsmCobraUtiAlta
							&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) ||
									CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve  ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<=vDiasConta
						vDiariasAnt = (byte) (vDiariasAnt - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiasConta));
					} else if (!vSsmCobraUtiAlta
							&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && 
									!CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<vDiariasConta
						vDiariasAnt = (byte) (vDiariasAnt - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiariasConta + 1));
					} else if (!vSsmCobraUtiAlta
							&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || 
									CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<=vDiasConta
						vDiariasAnt = (byte) (vDiariasAnt - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiasConta));
					}
				} else if (CoreUtil.igual(DominioLimite.L,flagLimite)) {
					if (vSsmCobraUtiAlta
							&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && 
									!CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<vDiariasConta
						vDiariasAlta = (byte) (vDiariasAlta - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiariasConta + 1));
					} else if (vSsmCobraUtiAlta
							&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || 
									CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<=vDiasConta
						vDiariasAlta = (byte) (vDiariasAlta - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiasConta));
					} else if (!vSsmCobraUtiAlta
							&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) && 
									!CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<vDiariasConta
						vDiariasAlta = (byte) (vDiariasAlta - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiariasConta + 1));
					} else if (!vSsmCobraUtiAlta
							&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) || 
									CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<=vDiasConta
						vDiariasAlta = (byte) (vDiariasAlta - ((vDiariasIni + vDiariasAnt + vDiariasAlta) - vDiasConta));
					}
				} else {
					
					// Verifica se ainda cabem diarias de uti na conta (dias UTI deve ser MENOR que dias conta)
					if (vSsmCobraUtiAlta
							&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) &&
									!CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<vDiariasConta
						vEspaco = vDiariasConta - (vDiariasIni + vDiariasAnt + vDiariasAlta) - 1;
					} else if (vSsmCobraUtiAlta
							&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) ||
									CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<=vDiasConta
						vEspaco = vDiasConta - (vDiariasIni + vDiariasAnt + vDiariasAlta);
					} else if (!vSsmCobraUtiAlta
							&& (!CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) &&
									!CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<vDiariasConta
						vEspaco = vDiariasConta - (vDiariasIni + vDiariasAnt + vDiariasAlta) - 1;
					} else if (!vSsmCobraUtiAlta
							&& (CoreUtil.igual(pCodMotivoSaidaObito.toString(),vMotivo) ||
									CoreUtil.igual(pCodMotivoSaidaOutros.toString(),vMotivo))) {
						
						// Deve ficar:(vDiariasIni+vDiariasAnt+vDiariasAlta)<=vDiasConta
						vEspaco = vDiasConta - (vDiariasIni + vDiariasAnt + vDiariasAlta);
					}

					logar("total espaco: {0} total diarias fora: {1}", vEspaco, vDiariasFora);

					if (CoreUtil.maior(vEspaco, 0) && CoreUtil.maior(vDiariasFora, vEspaco)) {
						vDiariasFora = vEspaco;
					}

					// Verifica se teve diarias fora dos 3 meses calculados e
					// lanca adequadamente neles:
					// Verifica se pode lancar no mes inicial:
					logar("46 diarias fora: {0}", vDiariasFora);

					if (CoreUtil.maior(vEspaco, 0) && CoreUtil.maior(vDiariasFora, 0) && CoreUtil.menor(vDiariasIni, vDiasMesIni)) {
						logar("47 diarias ini 05: {0}", vDiariasIni);

						if (CoreUtil.menorOuIgual(vDiariasFora, vEspaco)) {
							if (CoreUtil.menorOuIgual(vDiariasFora, (vDiasMesIni - vDiariasIni))) {
								vDiariasIni = (byte) (vDiariasIni + vDiariasFora);
								vEspaco = vEspaco - vDiariasFora;
								vDiariasFora = 0;
								logar("48 diarias ini 5: {0} diarias fora: {1} espaco: {2}", vDiariasIni, vDiariasFora, vEspaco);
							} else {
								vDiariasAux = vDiasMesIni - vDiariasIni;
								vDiariasIni = (byte) (vDiariasIni + vDiariasAux);
								vEspaco = vEspaco - vDiariasAux;
								vDiariasFora = vDiariasFora - vDiariasAux;
								logar("49 diarias ini 6: {0} diarias fora: {1} espaco: {2}", vDiariasIni, vDiariasFora, vEspaco);
							}
						}
					}

					// Verifica se pode lancar no mes anterior:
					if (CoreUtil.maior(vEspaco, 0) && CoreUtil.maior(vDiariasFora, 0) && CoreUtil.menor(vDiariasAnt, vDiasMesAnt)) {
						logar("50 diarias ant 05: {0}", vDiariasAnt);
						if (CoreUtil.menorOuIgual(vDiariasFora, vEspaco)) {
							if (CoreUtil.menorOuIgual(vDiariasFora, (vDiasMesAnt - vDiariasAnt))) {
								vDiariasAnt = (byte) (vDiariasAnt + vDiariasFora);
								vEspaco = vEspaco - vDiariasFora;
								vDiariasFora = 0;
								logar("51 diarias ant 5: {0} diarias fora: {1} espaco: {2}", vDiariasAnt, vDiariasFora, vEspaco);
							} else {
								vDiariasAux = vDiasMesAnt - vDiariasAnt;
								vDiariasAnt = (byte) (vDiariasAnt + vDiariasAux);
								vEspaco = vEspaco - vDiariasAux;
								vDiariasFora = vDiariasFora - vDiariasAux;
								logar("52 diarias ant 6: {0} diarias fora: {1} espaco: {2}", vDiariasAnt, vDiariasFora, vEspaco);
							}
						}
					}

					// Verifica se pode lancar no mes alta:
					if (CoreUtil.maior(vEspaco, 0) && CoreUtil.maior(vDiariasFora, 0) && CoreUtil.menor(vDiariasAlta, vDiasMesAlta)) {
						logar("53 diarias alta 05: {0}", vDiariasAlta);

						if (CoreUtil.menorOuIgual(vDiariasFora, vEspaco)) {
							if (CoreUtil.menorOuIgual(vDiariasFora, (vDiasMesAlta - vDiariasAlta))) {
								vDiariasAlta = (byte) (vDiariasAlta + vDiariasFora);
								vEspaco = vEspaco - vDiariasFora;
								vDiariasFora = 0;

								logar("54 diarias alta 5: {0} diarias fora: {1} espaco: {2}", vDiariasAlta, vDiariasFora, vEspaco);	
							} else {
								vDiariasAux = vDiasMesAlta - vDiariasAlta;
								vDiariasAlta = (byte) (vDiariasAlta + vDiariasAux);
								vEspaco = vEspaco - vDiariasAux;
								vDiariasFora = vDiariasFora - vDiariasAux;

								logar("55 diarias alta 6: {0} diarias fora: {1} espaco: {2}", vDiariasAlta, vDiariasFora, vEspaco);
							}
						}
					}

					logar("56 sobra diarias fora: {0} espaco: {1}", vDiariasFora, vEspaco);
				}
			} // for (FatContasInternacao fatContaInternacao : contasInternacao) {
		} // if (regCta.getContasInternacao() != null && !regCta.getContasInternacao().isEmpty()) {

		logar("------>>>>>>>finaliza");

		logar("57 diarias ini: {0} ant: {1} alta: {2} tipo UTI: {3} UTI Neo: {4}", vDiariasIni, vDiariasAnt, vDiariasAlta, (vPrimeiraUti!=null?vPrimeiraUti.getCodigo():null), vUtiNeo);

		rnCthcVerUtimesesVO.setDiariasUtiMesIni(nvl(vDiariasIni, 0));
		rnCthcVerUtimesesVO.setDiariasUtiMesAnt(nvl(vDiariasAnt, 0));
		rnCthcVerUtimesesVO.setDiariasUtiMesAlta(nvl(vDiariasAlta, 0));
		rnCthcVerUtimesesVO.setTipoUti(vPrimeiraUti);
		rnCthcVerUtimesesVO.setUtiNeo(vUtiNeo);

		logar("58 diarias ini: {0} ant: {1} alta: {2} tipo UTI: {3} UTI Neo: {4}",
				rnCthcVerUtimesesVO.getDiariasUtiMesIni(),
				rnCthcVerUtimesesVO.getDiariasUtiMesAnt(),
				rnCthcVerUtimesesVO.getDiariasUtiMesAlta(),
				(rnCthcVerUtimesesVO.getTipoUti()!=null?rnCthcVerUtimesesVO.getTipoUti().getCodigo():null),
				rnCthcVerUtimesesVO.getUtiNeo());

		rnCthcVerUtimesesVO.setRetorno(Boolean.TRUE);
		
		return rnCthcVerUtimesesVO;
	}

	/**
	 * ORADB Procedure FATK_CTH4_RN_UN.RN_CTHC_VER_DIARIAS
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount"})
	public RnCthcVerDiariasVO rnCthcVerDiarias(final Integer pCthSeq, final Integer pDiasUti, final Short pDias) throws BaseException {
		final IAghuFacade aghuFacade = this.getAghuFacade();
		final IInternacaoFacade internacaoFacade = getInternacaoFacade();

		final FatkCthRN fatkCthRN = getFatkCthRN();

		final RnCthcVerDiariasVO vo = new RnCthcVerDiariasVO();
		vo.setDiasUti(pDiasUti);

		Integer vPacCodigo = null;
		Integer vIntSeq = null;
		Boolean flagLimite = null;
		Date vDtIniAtd = null;
		Date vDtIniCta = null;
		Date vDtFimCta = null;
		DominioTipoAltaUTI vTipoUti = null;
		Integer vPhi = null;
		Short vPho = null;
		Integer vIph = null;
		Long vCodSus = null;
		Date vDtAltaMed = null;
		Date vDtObito = null;
		Byte vMspCodSus = null;
		Boolean vCalcular = null;
		Boolean vSsmCobraUtiAlta = Boolean.FALSE;
		String vMotivo = null;
		Date vDtLimite = null;
		Date vDtIniUti = null;
		Date vDtFimUti = null;
		Date vDtFimAnt = null;
		Integer vMesIniUti = 0;
		Integer vMesFimUti = 0;
		Integer vDiariasUti = 0;
		Integer vDiferenca = 0;
		Integer vDiariasConta = 0;
		Integer vDiasConta = 0;

		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(DateConstants.DATE_PATTERN_YYYY_MM);

		// Busca paciente da conta hospitalar
		Boolean auxRetorno = null;
		RnCthcVerPacCtaVO rnCthcVerPacCtaVO = fatkCthRN.rnCthcVerPacCta(pCthSeq);
		if (rnCthcVerPacCtaVO != null) {
			vPacCodigo = rnCthcVerPacCtaVO.getPacCodigo();
			vIntSeq = rnCthcVerPacCtaVO.getIntSeq();
			auxRetorno = rnCthcVerPacCtaVO.getRetorno();
		}

		if (Boolean.FALSE.equals(auxRetorno)) {
			return vo;
		}

		// Busca data do inicio do atendimento da internacao
		List<Date> dtrhInicioAtendimentos = aghuFacade.listarDthrInicioAtendimentosPorCodigoInternacao(vIntSeq, 0, 1);
		if (dtrhInicioAtendimentos != null && !dtrhInicioAtendimentos.isEmpty()) {
			vDtIniAtd = DateUtil.truncaData(dtrhInicioAtendimentos.get(0));
		}

		if (vDtIniAtd == null) {
			return vo;
		}

		// Busca data de inicio da conta (internacao)
		final FatContasHospitalares regCta = getFatContasHospitalaresDAO().obterPorChavePrimaria(pCthSeq);
		vDtFimCta = DateUtil.truncaData(nvl(regCta.getDtAltaAdministrativa(), new Date()));
		if (regCta.getDataInternacaoAdministrativa() == null) {
			return vo;
		} else {
			vDtIniCta = DateUtil.truncaData(regCta.getDataInternacaoAdministrativa());
		}

		// Verifica diarias da conta
		vDiariasConta = DateUtil.calcularDiasEntreDatas(vDtIniCta, vDtFimCta);
		vDiasConta = vDiariasConta + 1;

		// Busca motivo cobranca da conta
		if (regCta.getMotivoSaidaPaciente() != null) {
			vMspCodSus = regCta.getMotivoSaidaPaciente().getCodigoSus();
		}

		final Byte codigoMotivoAlta = buscarVlrByteAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_ALTA);
		vMspCodSus = nvl(vMspCodSus, codigoMotivoAlta); // default motivo = ALTA

		if (vMspCodSus == null) {
			return vo;
		}

		vMotivo = vMspCodSus != null ? vMspCodSus.toString().substring(0, 1) : null;

		if (vPacCodigo != null) {
			// Verifica se o paciente teve obito na UTI (p/cobrar dia da alta)
			AipPacientes paciente = getPacienteFacade().obterPacientePorCodigo(vPacCodigo);
			if (paciente != null && paciente.getDtObito() != null) {
				vDtObito = DateUtil.truncaData(paciente.getDtObito());
			}
		}

		// Verifica data de alta medica (p/cobrar dia da alta)
		AinInternacao internacao = internacaoFacade.obterAinInternacaoPorChavePrimaria(vIntSeq);
		if (internacao != null && internacao.getDthrAltaMedica() != null) {
			vDtAltaMed = DateUtil.truncaData(internacao.getDthrAltaMedica());
		}

		// Verifica se proced solic/realiz permite cobrar UTI dia da alta
		if (regCta.getProcedimentoHospitalarInternoRealizado() != null) {
			vPhi = regCta.getProcedimentoHospitalarInternoRealizado().getSeq();
		} else if (regCta.getProcedimentoHospitalarInterno() != null) {
			// Se nao tem Realiz,usa o Solicitado
			vPhi = regCta.getProcedimentoHospitalarInterno().getSeq();
		}

		if (vPhi != null) {
			RnCthcVerItemSusVO rnCthcVerItemSusVO = fatkCthRN.rnCthcVerItemSus(DominioOrigemProcedimento.I,
					regCta.getConvenioSaudePlano() != null ? regCta.getConvenioSaudePlano().getId().getCnvCodigo() : null,
					regCta.getConvenioSaudePlano() != null ? regCta.getConvenioSaudePlano().getId().getSeq() : null, (short) 1, vPhi, null);

			Boolean aux = false;
			if (rnCthcVerItemSusVO != null) {
				aux = rnCthcVerItemSusVO.getRetorno();
				vPho = rnCthcVerItemSusVO.getPhoSeq();
				vIph = rnCthcVerItemSusVO.getIphSeq();
				vCodSus = rnCthcVerItemSusVO.getCodSus();
			}

			if (Boolean.TRUE.equals(aux)) {
				vSsmCobraUtiAlta = fatkCthRN.rnCthcVerUtidalta(vPho, vIph);
			} else {
				return vo;
			}
		}

		logar("DT ATD {0} DT ALT MED {1} DT OBITO {2}", vDtIniAtd, vDtAltaMed, vDtObito);
		logar(" DT INT {0} DT ALTA {1} MOT {2} DIAR {3} DIAS {4}", vDtIniCta, vDtFimCta, vMotivo, vDiariasConta, vDiasConta);

		if (Boolean.TRUE.equals(vSsmCobraUtiAlta)) {
			logar(" SSM {0} cobra dia alta", vCodSus);
		} else {
			logar(" SSM {0} NAO cobra dia alta", vCodSus);
		}

		final Integer pCodMotivoSaidaObito = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_MOTIVO_SAIDA_OBITO);
		final Integer pCodMotivoSaidaOutros = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_MOTIVO_SAIDA_OUTROS);

		final Integer pCodTipoMovimentoEstorno = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_TIPO_MOVIMENTO_ESTORNO);
		final Integer pCodTipoMovimentoIngressoSO = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_INGR_SO);

		// Busca os movimentos da internacao desde o inicio do atendimento
		vTipoUti = null;

		List<AinMovimentosInternacaoVO> listaMovimentosInternacao = internacaoFacade
				.listarMotivosInternacaoVOOrdenadoPorDthrLancamento(vIntSeq, vDtIniAtd, vDtFimCta, new Integer[] {
						pCodTipoMovimentoEstorno, pCodTipoMovimentoIngressoSO });

		if (listaMovimentosInternacao != null && !listaMovimentosInternacao.isEmpty()) {
			for(AinMovimentosInternacaoVO regMvi : listaMovimentosInternacao){
				// Verifica se a UNF é de UTI e, se for, verfica o tipo
				// (I,II,III)
				vTipoUti = null;

				List<AghCaractUnidFuncionais> listaCaracteristicasUnidadesFuncionais = aghuFacade
						.listarCaracteristicasUnidadesFuncionais(regMvi.getUnfSeq(), new ConstanteAghCaractUnidFuncionais[] {
								ConstanteAghCaractUnidFuncionais.DIARIA_UTI_1, ConstanteAghCaractUnidFuncionais.DIARIA_UTI_2,
								ConstanteAghCaractUnidFuncionais.DIARIA_UTI_3 }, 0, 1);
				if (listaCaracteristicasUnidadesFuncionais != null && !listaCaracteristicasUnidadesFuncionais.isEmpty()) {
					AghCaractUnidFuncionais caracteristicaUnidadeFuncional = listaCaracteristicasUnidadesFuncionais.get(0);
					if (ConstanteAghCaractUnidFuncionais.DIARIA_UTI_1.equals(caracteristicaUnidadeFuncional.getId()
							.getCaracteristica())) {
						vTipoUti = DominioTipoAltaUTI.POSITIVO_1;
					} else if (ConstanteAghCaractUnidFuncionais.DIARIA_UTI_2.equals(caracteristicaUnidadeFuncional.getId()
							.getCaracteristica())) {
						vTipoUti = DominioTipoAltaUTI.POSITIVO_2;
					} else if (ConstanteAghCaractUnidFuncionais.DIARIA_UTI_3.equals(caracteristicaUnidadeFuncional.getId()
							.getCaracteristica())) {
						vTipoUti = DominioTipoAltaUTI.POSITIVO_3;
					}
				}

				logar("1 MVI: DT {0} TMI {1} UNF {2} TIPO {3} QRT {4} LTO {5} ESP {6}",
						DIA_MES_ANO_DATE_FORMAT.format(regMvi.getDthrLancamento()), regMvi.getTmiSeq(), regMvi.getUnfSeq(),
						vTipoUti != null ? vTipoUti.getCodigo() : null, regMvi.getQrtNumero(), regMvi.getLtoLtoId(),
						regMvi.getEspSeq());

				if (vDtIniUti == null) {
					// É uma unid funcional de UTI
					if (vTipoUti != null) {
						vDtIniUti = trunc(regMvi.getDthrLancamento());
					}
				} else {
					if (vTipoUti == null) {
						// Nao é unid funcional de UTI
						vDtFimUti = trunc(regMvi.getDthrLancamento());

						// Verifica casos de saida e entrada na uti no mesmo dia
						if (vDtFimAnt != null) {
							if (CoreUtil.igual(vDtIniUti, vDtFimAnt)) {
								if (!CoreUtil.igual(vDtIniUti, vDtFimUti)) {
									if (Boolean.TRUE.equals(vSsmCobraUtiAlta)
											|| CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
										// Calculou por dias o periodo anterior
										// (e nao por diarias)
										// Calcula a partir do dia seguinte pois
										// o primeiro dia ja foi contado no
										// calculo do periodo anterior
										Calendar cal = Calendar.getInstance();
										cal.setTime(vDtIniUti);
										cal.add(Calendar.DAY_OF_MONTH, 1);

										vDtIniUti = cal.getTime();
										vCalcular = true;
									}
								} else {
									// Nao calcula este periodo e vai pegar o
									// proximo movimento
									vCalcular = false;
									vDtFimAnt = vDtFimUti;

									if (CoreUtil.igual(vDtFimUti, vDtFimCta)) {
										vDtIniUti = null;
										vDtFimUti = null;
										vMesIniUti = null;
										vMesFimUti = null;
										
										break;
									}
									vDtIniUti = null;
									vDtFimUti = null;
									vMesIniUti = null;
									vMesFimUti = null;
								}
							}
						} else {
							vCalcular = true;
						}

						if (Boolean.TRUE.equals(vCalcular)) {
							vMesIniUti = vDtIniUti != null ? Integer.valueOf(sdf.format(vDtIniUti)) : null;
							vMesFimUti = vDtFimUti != null ? Integer.valueOf(sdf.format(vDtFimUti)) : null;

							logar("MVI UTI: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);

							// Verifica se periodo inicia e termina antes do
							// inicio da cta
							if (CoreUtil.isMenorDatas(vDtIniUti, vDtIniCta) && CoreUtil.isMenorDatas(vDtFimUti, vDtIniCta)) {
								logar(PERIODO_INCIA_E_TERMINA_ANTES_DO_INICIO_DA_CONTA);
								vDtIniUti = null;
								vDtFimUti = null;
								vMesIniUti = null;
								vMesFimUti = null;
							}

							// Verifica se periodo inicia antes,termina depois
							// do inicio da cta
							if (CoreUtil.isMenorDatas(vDtIniUti, vDtIniCta) && CoreUtil.isMaiorDatas(vDtFimUti, vDtIniCta)) {
								vDtIniUti = vDtIniCta;
								vMesIniUti = vDtIniUti != null ? Integer.valueOf(sdf.format(vDtIniUti)) : null;
								logar("QUEBRA MVI UTI antes cta: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti,
										vMesFimUti);
							}

							if (CoreUtil.isMaiorOuIgualDatas(vDtIniUti, vDtIniCta)) {
								vDiariasUti = vDiariasUti + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimUti);
								logar("diarias uti: {0}", vDiariasUti);

								if ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
										&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
										.maiorOuIgual(vDiariasUti, vDiariasConta))
										|| (Boolean.TRUE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti, vDiasConta))
										|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
												vDiariasConta))
										|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti, vDiasConta))) {
									logar("estourou 1");
									flagLimite = true;
									vDtIniUti = null;
									vDtFimUti = null;
									vMesIniUti = null;
									vMesFimUti = null;
									break;
								}

								// Se paciente teve obito ou se pode cobrar dia
								// saida,soma 1 diaria
								if (Boolean.TRUE.equals(vSsmCobraUtiAlta)
										|| CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
									vDiariasUti++;
									logar("diarias uti c: {0}", vDiariasUti);

									if ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
											&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
													pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
											vDiariasConta))
											|| (Boolean.TRUE.equals(vSsmCobraUtiAlta)
													&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
															pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti,
													vDiasConta))
											|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
													&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
															pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
													vDiariasConta))
											|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
													&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
															pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti,
													vDiasConta))) {
										logar("estourou 2");
										flagLimite = true;
										vDtIniUti = null;
										vDtFimUti = null;
										vMesIniUti = null;
										vMesFimUti = null;
										break;
									}
								}

								if (CoreUtil.igual(vDiariasUti, 0)) {
									if (CoreUtil.igual(vDtFimCta, vDtAltaMed) || !CoreUtil.igual(vDtFimUti, vDtFimCta)) {
										vDiariasUti++;
										logar("diarias uti b: {0}", vDiariasUti);

										if ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
												vDiariasConta))
												|| (Boolean.TRUE.equals(vSsmCobraUtiAlta)
														&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
																pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti,
														vDiasConta))
												|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
														&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
																pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(
														vDiariasUti, vDiariasConta))
												|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
														&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
																pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti,
														vDiasConta))) {
											logar("estourou 3");
											flagLimite = true;
											vDtIniUti = null;
											vDtFimUti = null;
											vMesIniUti = null;
											vMesFimUti = null;
											break;
										}
									}
								} else if (CoreUtil.maior(vDiariasUti, 0)) {
									if (CoreUtil.igual(vDtFimUti, vDtFimCta)
											&& ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
													&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
															pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
													vDiariasConta)) || (Boolean.FALSE.equals(vSsmCobraUtiAlta)
													&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
															pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
													vDiariasConta)))) {
										vDiariasUti--;
										logar("diarias uti b2: {0}", vDiariasUti);

										if ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
												vDiariasConta))
												|| (Boolean.TRUE.equals(vSsmCobraUtiAlta)
														&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
																pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti,
														vDiasConta))
												|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
														&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
																pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(
														vDiariasUti, vDiariasConta))
												|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
														&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
																pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti,
														vDiasConta))) {
											logar("estourou 4");
											flagLimite = true;
											vDtIniUti = null;
											vDtFimUti = null;
											vMesIniUti = null;
											vMesFimUti = null;
											break;
										}
									}
								}

								if (Boolean.TRUE.equals(flagLimite)) {
									if (Boolean.TRUE.equals(vSsmCobraUtiAlta)
											&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
													pCodMotivoSaidaOutros.toString())) {
										// Deve ficar: vDiariasUti <
										// vDiariasConta
										vDiariasUti = vDiariasUti - (vDiariasUti - vDiariasConta + 1);
										logar("diarias uti L1: {0}", vDiariasUti);
									} else if (Boolean.TRUE.equals(vSsmCobraUtiAlta)
											&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
										// Deve ficar: vDiariasUti <= vDiasConta
										vDiariasUti = vDiariasUti - (vDiariasUti - vDiasConta);
										logar("diarias uti L2: {0}", vDiariasUti);
									} else if (Boolean.FALSE.equals(vSsmCobraUtiAlta)
											&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
													pCodMotivoSaidaOutros.toString())) {
										// Deve ficar: vDiariasUti <
										// vDiariasConta
										vDiariasUti = vDiariasUti - (vDiariasUti - vDiariasConta + 1);
										logar("diarias uti L3: {0}", vDiariasUti);
									} else if (Boolean.FALSE.equals(vSsmCobraUtiAlta)
											&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
										// Deve ficar: vDiariasUti <= vDiasConta
										vDiariasUti = vDiariasUti - (vDiariasUti - vDiasConta);
										logar("diarias uti L4: {0}", vDiariasUti);
									}
								}

								if (pDias != null) {
									// Verifica se ja alcancou limite de dias
									logar("dtfim: {0} diarias uti: {1} pDias: {2}", vDtFimUti, vDiariasUti, pDias);

									if (CoreUtil.maior(vDiariasUti, pDias)) {
										vDiferenca = (vDiariasUti - pDias);

										Calendar cal = Calendar.getInstance();
										cal.setTime(vDtFimUti);
										cal.add(Calendar.DAY_OF_MONTH, -vDiferenca);

										vDtLimite = cal.getTime();

										logar("diferenca: {0} dtlimite: {1}", vDiferenca, vDtLimite);

										if (CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
											// Obito
											// Deve ficar: vDiariasUti <=
											// vDiasConta
											if (CoreUtil.maior(vDiariasUti - vDiferenca,
													DateUtil.calcularDiasEntreDatas(vDtIniCta, vDtLimite) + 1)) {
												cal.setTime(vDtLimite);
												cal.add(Calendar.DAY_OF_MONTH,
														(vDiariasUti - vDiferenca)
																- DateUtil.calcularDiasEntreDatas(vDtIniCta, vDtLimite) + 1);

												vDtLimite = cal.getTime();
												logar(NOVA_DTLIMITE_0_, vDtLimite);
											}
										} else {
											// Nao obito
											// Deve ficar: vDiariasUti <
											// vDiariasConta
											if (CoreUtil.maiorOuIgual(vDiariasUti - vDiferenca,
													DateUtil.calcularDiasEntreDatas(vDtIniCta, vDtLimite))) {
												cal.setTime(vDtLimite);
												cal.add(Calendar.DAY_OF_MONTH,
														(vDiariasUti - vDiferenca)
																- DateUtil.calcularDiasEntreDatas(vDtIniCta, vDtLimite) + 1);

												vDtLimite = cal.getTime();

												logar(NOVA_DTLIMITE_0_, vDtLimite);
											}
										}
										vo.setRetorno(vDtLimite);
										return vo;
									}
								}

								vDtFimAnt = vDtFimUti;

								if (CoreUtil.igual(vDtFimUti, vDtFimCta)) {
									vDtIniUti = null;
									vDtFimUti = null;
									vMesIniUti = null;
									vMesFimUti = null;
									
									break;
								}

								vDtIniUti = null;
								vDtFimUti = null;
								vMesIniUti = null;
								vMesFimUti = null;
							}
						}
					}
				}
			
				//getFaturamentoFacade().evict(regMvi);
			}
		}

		// Verifica casos em que paciente permaneceu em UTI ate a alta (pois
		// nesse caso nao calculou diarias do ultimo periodo em UTI)
		if (vDtIniUti != null) {
			vDtFimUti = vDtFimCta;

			// Verifica casos de saida e entrada na uti no mesmo dia
			if (vDtFimAnt != null) {
				if (CoreUtil.igual(vDtIniUti, vDtFimAnt)) {
					if (!CoreUtil.igual(vDtIniUti, vDtFimUti)) {
						if (Boolean.TRUE.equals(vSsmCobraUtiAlta)
								|| CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
							// Calculou por dias o periodo anterior (e nao por
							// diarias)
							// Calcula a partir do dia seguinte pois o primeiro
							// dia ja
							// Foi contado no calculo do periodo anterior
							Calendar cal = Calendar.getInstance();
							cal.setTime(vDtIniUti);
							cal.add(Calendar.DAY_OF_MONTH, 1);

							vDtIniUti = cal.getTime();
							vCalcular = true;
						}
					} else {
						// Nao calcula este periodo e vai pegar o proximo
						// movimento
						vCalcular = false;
					}
				}
			} else {
				vCalcular = true;
			}

			if (Boolean.TRUE.equals(vCalcular)) {
				vMesIniUti = vDtIniUti != null ? Integer.valueOf(sdf.format(vDtIniUti)) : null;
				vMesFimUti = vDtFimUti != null ? Integer.valueOf(sdf.format(vDtFimUti)) : null;

				logar("MVI UTI: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);

				// Verifica se periodo inicia e termina antes do inicio da cta
				if (CoreUtil.isMenorDatas(vDtIniUti, vDtIniCta) && CoreUtil.isMenorDatas(vDtFimUti, vDtIniCta)) {
					logar(PERIODO_INCIA_E_TERMINA_ANTES_DO_INICIO_DA_CONTA);
					vDtIniUti = null;
					vDtFimUti = null;
					vMesIniUti = null;
					vMesFimUti = null;
				}

				// Verifica se periodo inicia antes e termina depois do inicio
				// da conta
				if (CoreUtil.isMenorDatas(vDtIniUti, vDtIniCta) && CoreUtil.isMaiorDatas(vDtFimUti, vDtIniCta)) {
					vDtIniUti = vDtIniCta;
					vMesIniUti = vDtIniUti != null ? Integer.valueOf(sdf.format(vDtIniUti)) : null;
					logar("QUEBRA MVI UTI: INI {0} mes {1}, FIM {2} mes {3}", vDtIniUti, vMesIniUti, vDtFimUti, vMesFimUti);
				}

				if (CoreUtil.isMaiorOuIgualDatas(vDtIniUti, vDtIniCta)) {
					// Verifica se ainda nao estourou
					if ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
							&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
							.menor(vDiariasUti, vDiariasConta))
							|| (Boolean.TRUE.equals(vSsmCobraUtiAlta)
									&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
									.menorOuIgual(vDiariasUti, vDiasConta))
							|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
									&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
									.menor(vDiariasUti, vDiariasConta))
							|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
									&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
									.menorOuIgual(vDiariasUti, vDiasConta))) {
						vDiariasUti = vDiariasUti + DateUtil.calcularDiasEntreDatas(vDtIniUti, vDtFimUti);
						logar("diarias uti: {0}", vDiariasUti);

						if ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
								&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
								.maiorOuIgual(vDiariasUti, vDiariasConta))
								|| (Boolean.TRUE.equals(vSsmCobraUtiAlta)
										&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
										.maior(vDiariasUti, vDiasConta))
								|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
										&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
										.maiorOuIgual(vDiariasUti, vDiariasConta))
								|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
										&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
										.maior(vDiariasUti, vDiasConta))) {
							logar("estourou 5");
							flagLimite = true;
						}

						// Se paciente teve obito ou se pode cobrar dia saida,
						// soma 1 diaria
						if (Boolean.TRUE.equals(vSsmCobraUtiAlta)
								|| CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
							vDiariasUti++;
							logar("diarias uti c: {0}", vDiariasUti);

							if ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
									&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
									.maiorOuIgual(vDiariasUti, vDiariasConta))
									|| (Boolean.TRUE.equals(vSsmCobraUtiAlta)
											&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
											.maior(vDiariasUti, vDiasConta))
									|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
											&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
													pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
											vDiariasConta))
									|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
											&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
											.maior(vDiariasUti, vDiasConta))) {
								logar("estourou 6");
								flagLimite = true;
							}
						}

						if (CoreUtil.igual(vDiariasUti, 0)) {
							if (CoreUtil.igual(vDtFimCta, vDtAltaMed) || !CoreUtil.igual(vDtFimUti, vDtFimCta)) {
								vDiariasUti++;
								logar("diarias uti b: {0}", vDiariasUti);

								if ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
										&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
										.maiorOuIgual(vDiariasUti, vDiariasConta))
										|| (Boolean.TRUE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti, vDiasConta))
										|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
												vDiariasConta))
										|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti, vDiasConta))) {
									logar("estourou 7");
									flagLimite = true;
								}
							}
						} else if (CoreUtil.maior(vDiariasUti, 0)) {
							if (CoreUtil.igual(vDtFimUti, vDtFimCta)
									&& ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
											&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
													pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
											vDiariasConta)) || (Boolean.FALSE.equals(vSsmCobraUtiAlta)
											&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
													pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
											vDiariasConta)))) {
								vDiariasUti--;
								logar("diarias uti b2: {0}", vDiariasUti);

								if ((Boolean.TRUE.equals(vSsmCobraUtiAlta)
										&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString()) && CoreUtil
										.maiorOuIgual(vDiariasUti, vDiariasConta))
										|| (Boolean.TRUE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti, vDiasConta))
										|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maiorOuIgual(vDiariasUti,
												vDiariasConta))
										|| (Boolean.FALSE.equals(vSsmCobraUtiAlta)
												&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(),
														pCodMotivoSaidaOutros.toString()) && CoreUtil.maior(vDiariasUti, vDiasConta))) {
									logar("estourou 8");
									flagLimite = true;
								}
							}
						}
					}

					if (Boolean.TRUE.equals(flagLimite)) {
						if (Boolean.TRUE.equals(vSsmCobraUtiAlta)
								&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
							// Deve ficar: vDiariasUti < vDiariasConta
							vDiariasUti = vDiariasUti - (vDiariasUti - vDiariasConta + 1);
							logar("diarias uti L1: {0}", vDiariasUti);
						} else if (Boolean.TRUE.equals(vSsmCobraUtiAlta)
								&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
							// Deve ficar: vDiariasUti <= vDiasConta
							vDiariasUti = vDiariasUti - (vDiariasUti - vDiasConta);
							logar("diarias uti L2: {0}", vDiariasUti);
						} else if (Boolean.FALSE.equals(vSsmCobraUtiAlta)
								&& CoreUtil.notIn(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
							// Deve ficar: vDiariasUti < vDiariasConta
							vDiariasUti = vDiariasUti - (vDiariasUti - vDiariasConta + 1);
							logar("diarias uti L3: {0}", vDiariasUti);
						} else if (Boolean.FALSE.equals(vSsmCobraUtiAlta)
								&& CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
							// Deve ficar: vDiariasUti <= vDiasConta
							vDiariasUti = vDiariasUti - (vDiariasUti - vDiasConta);
							logar("diarias uti L4: {0}", vDiariasUti);
						}
					}

					if (pDias != null) {
						// Verifica se ja alcancou limite de dias
						logar("dtfim: {0} diarias uti: {1} pDias: {2}", vDtFimUti, vDiariasUti, pDias);

						if (CoreUtil.maior(vDiariasUti, pDias)) {
							vDiferenca = (vDiariasUti - pDias);

							Calendar cal = Calendar.getInstance();
							cal.setTime(vDtFimUti);
							cal.add(Calendar.DAY_OF_MONTH, -vDiferenca);

							vDtLimite = cal.getTime();

							logar("diferenca: {0} dtlimite: {1}", vDiferenca, vDtLimite);

							if (CoreUtil.in(vMotivo, pCodMotivoSaidaObito.toString(), pCodMotivoSaidaOutros.toString())) {
								// Deve ficar: vDiariasUti <= vDiasConta
								if (CoreUtil
										.maior(vDiariasUti - vDiferenca, DateUtil.calcularDiasEntreDatas(vDtIniCta, vDtLimite) + 1)) {
									cal = Calendar.getInstance();
									cal.setTime(vDtLimite);
									cal.add(Calendar.DAY_OF_MONTH,
											(vDiariasUti - vDiferenca) - (DateUtil.calcularDiasEntreDatas(vDtIniCta, vDtLimite) + 1));

									vDtLimite = cal.getTime();
									logar(NOVA_DTLIMITE_0_, vDtLimite);
								}
							} else {
								// Nao obito
								// Deve ficar: vDiariasUti < vDiariasConta
								if (CoreUtil.maiorOuIgual(vDiariasUti - vDiferenca,
										DateUtil.calcularDiasEntreDatas(vDtIniCta, vDtLimite))) {
									cal = Calendar.getInstance();
									cal.setTime(vDtLimite);
									cal.add(Calendar.DAY_OF_MONTH,
											(vDiariasUti - vDiferenca) - DateUtil.calcularDiasEntreDatas(vDtIniCta, vDtLimite) + 1);

									vDtLimite = cal.getTime();
									logar(NOVA_DTLIMITE_0_, vDtLimite);
								}
							}
							vo.setRetorno(vDtLimite);
							return vo;
						}
					}
				}
			}
		}

		vo.setDiasUti(vDiariasUti);
		return vo;
	}

	/**
	 * ORADB Procedure FATK_CTH4_RN_UN.RN_CTHP_ATU_DIA_UTI2
	 * 
	 * @throws BaseException
	 */
	public void rnCthpAtuDiaUti2(Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		// verifica se o convenio da conta é SUS
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		FatContasHospitalares contaHospitalar = faturamentoFacade.obterContaHospitalar(pCthSeq);
		if(contaHospitalar.getConvenioSaude() == null || !DominioGrupoConvenio.S.equals(contaHospitalar.getConvenioSaude().getGrupoConvenio())){
			contaHospitalar = null;
		}
		// se o convenio é SUS, calcula as diarias de UTI
		if(contaHospitalar != null){
			// calcula diarias UTI
			RnCthcVerUtimesesVO rnCthcVerUtimesesVO = this.rnCthcVerUtimeses(pCthSeq); 
			if(CoreUtil.igual(Boolean.TRUE, rnCthcVerUtimesesVO.getRetorno())){
				FatContasHospitalares oldCtaHosp = null;
				try {
					oldCtaHosp = faturamentoFacade.clonarContaHospitalar(contaHospitalar);
				} catch (Exception e) {
					logError("Exceção capturada: ", e);
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}
				contaHospitalar.setDiasUtiMesInicial(rnCthcVerUtimesesVO.getDiariasUtiMesIni());
				contaHospitalar.setDiasUtiMesAnterior(rnCthcVerUtimesesVO.getDiariasUtiMesAnt());
				contaHospitalar.setDiasUtiMesAlta(rnCthcVerUtimesesVO.getDiariasUtiMesAlta());
				contaHospitalar.setIndTipoUti(rnCthcVerUtimesesVO.getTipoUti());
				faturamentoFacade.persistirContaHospitalar(contaHospitalar, oldCtaHosp, false, nomeMicrocomputador, dataFimVinculoServidor);
			}
		}
	}

	public List<Integer> getContasParaReaberturaEmLote(final FatCompetencia competencia, final Date dtInicial, final Date dtFinal, final Long procedimentoSUS) throws BaseException{
		if( (dtInicial != null && dtFinal != null) && dtInicial.after(dtFinal) ){
			throw new BaseException(FaturamentoExceptionCode.ERRO_DATA_INICIAL_MENOR_QUE_FINAL);
		}
		
		return getFatContasHospitalaresDAO().obterContasHospitalaresPorCompetenciaEProcedimentoSUS(competencia, dtInicial, dtFinal, procedimentoSUS);
	}


	/**
	 * ORADB Procedure FATK_CTH4_RN_UN.RN_CTHP_ATU_TRANSP
	 * 
	 * @param pSeq
	 * @param pDtIntAdm
	 * @param pPhiRealizado
	 * @param pNroAih
	 * @param pCnvCodigo
	 * @param pPlano
	 * @param pIndSituacao
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void rnCthpAtuTransp(Integer pSeq
			 ,Date pDtIntAdm
			 ,Integer pPhiRealizado
			 ,Long pNroAih
			 ,Short pCnvCodigo
			 ,Byte pPlano
			 ,DominioSituacaoConta  pIndSituacao)
			throws BaseException {

		Integer vPacCodigo = 0;
		VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO = getVFatAssociacaoProcedimentoDAO();
		FatContasInternacaoDAO fatContasInternacaoDAO = getFatContasInternacaoDAO();
		
		FatContasInternacao fatContaInter = fatContasInternacaoDAO.buscarPrimeiraContaInternacao(pSeq);
		if (fatContaInter != null) {
			if (CoreUtil.igual(pIndSituacao,DominioSituacaoConta.E)) { // [1] 
				if (fatContaInter != null && fatContaInter.getInternacao() != null){ //se achar
					vPacCodigo = fatContaInter.getInternacao().getPacCodigo();
				} else if (fatContaInter != null && fatContaInter.getInternacao() != null){ //senao olha na outra consulta
					vPacCodigo = fatContaInter.getDadosContaSemInt().getPacCodigo();
				}
			
				VFatAssociacaoProcedimento vFatAssProc = vFatAssociacaoProcedimentoDAO.buscaIph(pPhiRealizado, pCnvCodigo, (short)1, pPlano, true);
				
				if (vFatAssProc != null) { // [2]
					Short vIphPhoSeq = vFatAssProc.getId().getIphPhoSeq();
					Integer vIphSeq  = vFatAssProc.getId().getIphSeq();
				
					FatItemProcHospTranspDAO fatItemProcHospTranspDAO = getFatItemProcHospTranspDAO();
					FatItemProcHospTranspId id = new FatItemProcHospTranspId(vIphPhoSeq, vIphSeq);
					FatItemProcHospTransp fatItemProcHospTransp = fatItemProcHospTranspDAO.obterPorChavePrimaria(id);
					if (fatItemProcHospTransp != null) { //[3]
						String vTtrCodigo = fatItemProcHospTransp.getFatTipoTransplante().getCodigo();
						
						FatPacienteTransplantesDAO fatPacTransDAO = getFatPacienteTransplantesDAO(); 
						FatPacienteTransplantes fatPacTransp = fatPacTransDAO.consultaPacTransp(vPacCodigo, vTtrCodigo);
						
						if (fatPacTransp == null) { //não encontrou paciente / transplante no cadastro
							FatPacienteTransplantesId idPacTransp = new FatPacienteTransplantesId(vPacCodigo, vTtrCodigo, pDtIntAdm);
							FatPacienteTransplantes pacTransp = new FatPacienteTransplantes(idPacTransp, pDtIntAdm, pNroAih, vIphPhoSeq, vIphSeq, "A");
							fatPacTransDAO.persistir(pacTransp);
							getFaturamentoFacade().evict(pacTransp);
						}
						else { //encontrou paciente / transplante no cadastro
							Date vDtInscr = fatPacTransp.getId().getDtInscricaoTransplante();
							Date vDtTransplante = fatPacTransp.getDtTransplante();
							Long vNroAih = fatPacTransp.getNroAihTransplante();
							
							if (vDtTransplante == null) { //se a data do transplante for nulla atualiza
								FatPacienteTransplantesId idPacTransp = new FatPacienteTransplantesId(vPacCodigo, vTtrCodigo, vDtInscr);
								FatPacienteTransplantes pacTransp = fatPacTransDAO.obterPorChavePrimaria(idPacTransp);
								pacTransp.setDtTransplante(vDtTransplante);
								pacTransp.setNroAihTransplante(vNroAih);
								//fatPacTransDAO.atualizar(pacTransp);//Foi comentado pois é atualizado no flush final.
								getFaturamentoFacade().evict(pacTransp);
							}
							else if (vDtTransplante != null && vDtTransplante.before(pDtIntAdm)) {
								FatPacienteTransplantesId idPacTransp = new FatPacienteTransplantesId(vPacCodigo, vTtrCodigo, pDtIntAdm);
								FatPacienteTransplantes pacTransp = new FatPacienteTransplantes(idPacTransp, pDtIntAdm, pNroAih, vIphPhoSeq, vIphSeq, "A");
								fatPacTransDAO.persistir(pacTransp);
								getFaturamentoFacade().evict(pacTransp);
							}
						}
					}
				}
			}  
			else { // Reabrindo a conta  -- [1]
				if (CoreUtil.igual(pIndSituacao,DominioSituacaoConta.O)) {
					if (fatContaInter.getInternacao() != null) {//se achar
						vPacCodigo = fatContaInter.getInternacao().getPacCodigo();
					}
					////////////BLOCO IGUAL AO DE CIMA /////////////////////
					VFatAssociacaoProcedimento vFatAssProc = vFatAssociacaoProcedimentoDAO.buscaIph(pPhiRealizado, pCnvCodigo, (short)1, pPlano, true);
					
					if (vFatAssProc != null) { // [2]
						Short vIphPhoSeq = vFatAssProc.getId().getIphPhoSeq();
						Integer vIphSeq  = vFatAssProc.getId().getIphSeq();
					
						FatItemProcHospTranspDAO fatItemProcHospTranspDAO = getFatItemProcHospTranspDAO();
						FatItemProcHospTranspId id = new FatItemProcHospTranspId(vIphPhoSeq, vIphSeq);
						FatItemProcHospTransp fatItemProcHospTransp = fatItemProcHospTranspDAO.obterPorChavePrimaria(id);
						if (fatItemProcHospTransp != null) { //[3]
							String vTtrCodigo = fatItemProcHospTransp.getFatTipoTransplante().getCodigo();
							
							FatPacienteTransplantesDAO fatPacTransDAO = getFatPacienteTransplantesDAO(); 
							FatPacienteTransplantes fatPacTransp = fatPacTransDAO.consultaPacTransp(vPacCodigo, vTtrCodigo);
							
							if (fatPacTransp == null) { //não encontrou paciente / transplante no cadastro
								FatPacienteTransplantesId idPacTransp = new FatPacienteTransplantesId(vPacCodigo, vTtrCodigo, pDtIntAdm);
								FatPacienteTransplantes pacTransp = new FatPacienteTransplantes(idPacTransp, pDtIntAdm, pNroAih, vIphPhoSeq, vIphSeq, "A");
								fatPacTransDAO.persistir(pacTransp);
								getFaturamentoFacade().evict(pacTransp);
								
							}
							else { //encontrou paciente / transplante no cadastro
								Date vDtInscr = fatPacTransp.getId().getDtInscricaoTransplante();
								Date vDtTransplante = fatPacTransp.getDtTransplante();
								Long vNroAih = fatPacTransp.getNroAihTransplante();
								
								if (CoreUtil.igual(vDtTransplante, pDtIntAdm) && CoreUtil.igual(vNroAih, pNroAih)) {
									if (CoreUtil.igual(vDtInscr,pDtIntAdm)) {
										List<FatPacienteTransplantes> listaExclusao = fatPacTransDAO.
											consultaPacientesTransplantadosSemAacAtendimentoApac(vPacCodigo, vTtrCodigo, pDtIntAdm);
										for (FatPacienteTransplantes aux : listaExclusao) {
											fatPacTransDAO.remover(aux);
											fatPacTransDAO.flush();
										}
										for (FatPacienteTransplantes aux : listaExclusao) {
											getFaturamentoFacade().evict(aux);
										}
									}
									else {
										FatPacienteTransplantesId idPacTransp = new FatPacienteTransplantesId(vPacCodigo, vTtrCodigo, vDtInscr);
										FatPacienteTransplantes pacTransp = fatPacTransDAO.obterPorChavePrimaria(idPacTransp);
										pacTransp.setDtTransplante(null);
										pacTransp.setNroAihTransplante(null);
										fatPacTransDAO.atualizar(pacTransp);
										getFaturamentoFacade().evict(pacTransp);
									}
								}
							}
						}
					} //2
				} 
			} //1
		}
	}

	/**
	 * ORADB Procedure FATK_CTH4_RN_UN.RN_CTHC_BUSCA_CONTA
	 * 
	 * @throws BaseException
	 */
	public RnCthcBuscaContaVO rnCthcBuscaConta(Integer pPacCodigo, Date pDthrMovimento, Integer pCthSeq) throws BaseException {
		
		RnCthcBuscaContaVO rnCthcBuscaContaVO = new RnCthcBuscaContaVO();
		FatContasHospitalares contasHospitalar = null;
		
		if (pCthSeq == null) {
			
			contasHospitalar = getFatContasHospitalaresDAO().obterContaHospitalarPaciente(pPacCodigo, pDthrMovimento);
			
		} else {
			
			contasHospitalar = getFatContasHospitalaresDAO().obterFatContaHospitalar(pCthSeq);
			
		}
		
		if (contasHospitalar == null) {
			
			rnCthcBuscaContaVO.setRetorno(false);
			
		} else {
			
			rnCthcBuscaContaVO.setCthSeq(contasHospitalar.getSeq());
			
			if (contasHospitalar.getIndSituacao() == null) {
				
				rnCthcBuscaContaVO.setIndSituacao(DominioSituacaoConta.A);
				
			} else {
				
				rnCthcBuscaContaVO.setIndSituacao(contasHospitalar.getIndSituacao());
				
				if (contasHospitalar.getIndSituacao().equals(DominioSituacaoConta.O)) {
					
					rnCthcBuscaContaVO.setMensagem("Conta nro " + contasHospitalar.getSeq().toString() + " já foi apresentada. O faturamento não será alterado.");
					rnCthcBuscaContaVO.setTipoMensagem("I");
					
				} else if (contasHospitalar.getIndSituacao().equals(DominioSituacaoConta.E)) {
					
					rnCthcBuscaContaVO.setMensagem("Conta nro " + contasHospitalar.getSeq().toString() + " esta encerrada. Contate faturamento para abertura da conta.");
					rnCthcBuscaContaVO.setTipoMensagem("E");
					
				}
				
			}
			
			rnCthcBuscaContaVO.setRetorno(true);
						
		}
		
		return rnCthcBuscaContaVO;
		
	}

	/**
	 * ORADB Procedure FATK_CTH4_RN_UN.RN_CTHP_ATU_SSM_SOL
	 * 
	 * @throws BaseException
	 */
	public void rnCthpAtuSsmSol(Integer cthSeq ,Integer phiSeq, String nomeMicrocomputador
			, final Date dataFimVinculoServidor, boolean umFATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL) throws BaseException {
		FatContasInternacaoDAO fatContasInternacaoDAO = getFatContasInternacaoDAO();
		VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO = getVFatAssociacaoProcedimentoDAO();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		Integer internacaoSeq = null;
		FatContasInternacao contaInternacao =  fatContasInternacaoDAO.buscarPrimeiraContaInternacao(cthSeq);
		if(contaInternacao != null){
			if(contaInternacao.getInternacao()!= null){
				internacaoSeq = contaInternacao.getInternacao().getSeq(); 
			}
		}
		
		Short phoSeq = null;
		Integer seq = null;
		VFatAssociacaoProcedimento vFatAssociacaoProcedimento = vFatAssociacaoProcedimentoDAO.buscarPrimeiroPorConvSaudePlanoInternacaoEProcedHospInternoAtivoEIphAtivo(phiSeq);		
		if(vFatAssociacaoProcedimento != null){
			phoSeq = vFatAssociacaoProcedimento.getItemProcedimentoHospitalar().getId().getPhoSeq();
			seq = vFatAssociacaoProcedimento.getItemProcedimentoHospitalar().getId().getSeq();
		}
		
		FatItensProcedHospitalar itemProcedimentoHospitalar = new FatItensProcedHospitalar();
		FatItensProcedHospitalarId iphId = new FatItensProcedHospitalarId();
		iphId.setPhoSeq(phoSeq);
		iphId.setSeq(seq);
		itemProcedimentoHospitalar.setId(iphId);
		
		
		AinInternacao internacao = getInternacaoFacade().obterAinInternacaoPorChavePrimaria(internacaoSeq);
		internacao.setItemProcedimentoHospitalar(itemProcedimentoHospitalar);
		
		getInternacaoFacade().atualizarInternacao(internacao, nomeMicrocomputador,servidorLogado ,dataFimVinculoServidor, false, umFATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL);
	}

	private IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	/**
	 * ORADB Procedure FATK_CTH4_RN_UN.RN_CTHC_VER_ESP_CTA
	 * 
	 * Retorna uma especialidade.
	 * 
	 * @throws BaseException
	 */
	public AghEspecialidades rnCthcVerEspCta(Integer cthSeq, Date dtAltaAdmt) throws BaseException {
		AinMovimentosInternacao movimento = getInternacaoFacade().buscarPrimeiroMovimentosInternacaoPorMaiorInternacaoDeContaInternacaoEDthrLancamento(cthSeq, dtAltaAdmt);
		if(movimento != null && movimento.getEspecialidade() != null){
			return movimento.getEspecialidade();
		}
		return null;
	}

	/**
	 * ORADB Procedure FATK_CTH4_RN_UN.RN_CTHC_ATU_LANCAUTI
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	public Boolean rnCthcAtuLancauti(Date pDataPrevia, Boolean pPrevia, Integer pCth, Integer pPacCodigo, Integer pPacProntuario,
			Integer pPhiRealiz, Short pPhoRealiz, Integer pIphRealiz, Long pCodSusRealiz, Date pPacDtNasc, Date pDtAltaAdm,
			Byte pDiasIni, Byte pDiasAnt, Byte pDiasAlta, DominioTipoAltaUTI pTipoUti, Boolean pUtiNeo, String nomeMicrocomputador,
			final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws BaseException {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		FaturamentoON faturamentoON = getFaturamentoON();
		FaturamentoRN faturamentoRN = getFaturamentoRN();
		VerificacaoItemProcedimentoHospitalarRN verificacaoItemProcedimentoHospitalarRN = getVerificacaoItemProcedimentoHospitalarRN();
		FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();
		FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		
		Boolean result = Boolean.TRUE;

		BigDecimal mil = new BigDecimal(1000);
		DateFormat df = new SimpleDateFormat(DateConstants.DATE_PATTERN_YYYY_MM);
		
		BigDecimal vPeso = null;
		Byte vMesGest = null;
		BigDecimal vVlrAnestUti = BigDecimal.ZERO;
		BigDecimal vVlrProcedUti = BigDecimal.ZERO;
		BigDecimal vVlrSadtUti = BigDecimal.ZERO;
		BigDecimal vVlrServHospUti = BigDecimal.ZERO;
		BigDecimal vVlrServProfUti = BigDecimal.ZERO;
		String vCompetenciaUti = null;
		Date vDataUtiAnt = null;
		Long vCodUtiEsp = null;
		Short vPhoUti = null;
		Integer vIphUti = null;
		Long vCodSusUti = null;
		Integer vPtAnestUti = null;
		Integer vPtCirUti = null;
		Integer vPtSadtUti = null;
		Byte vTaoUti = null;
		Integer vTivUti = null;
		Short vEicSeqp = null;
		Integer vIdadePac = null;
		FatContasHospitalares regCta = null;
		Integer vDadosRn = null;
		DominioTipoIdadeUTI vIdadeUti = null;

		final Long buscaAtiva = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_BUSCA_ATIVA);
		final Long utiBuscaAtivaMenor2anos = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_BUSCA_ATIVA_MENOR_2ANOS);
		final Long utiBuscaAtivaMaior2anos = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_BUSCA_ATIVA_MAIOR_2ANOS);

		// Verifica limite de idade entre paciente pediatrico e adulto
		final Integer vIdadeLimite = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_LIMITE_IDADE_PEDIATR_ADULTO);

		// Verifica idade do paciente
		vIdadePac = CoreUtil.calculaIdade(pPacDtNasc);

		logar("DtNasc {0} IdPac {1} IdLim {2} tipoUTI {3} utiNeo {4}",
				pPacDtNasc, vIdadePac, vIdadeLimite, (pTipoUti!=null?pTipoUti.getCodigo():null), pUtiNeo);

		if (CoreUtil.igual(DominioTipoAltaUTI.POSITIVO_3,pTipoUti)) {
			// UTI especial
			// Verifica tipo alta UTI, peso ao nascer, idade gestacional
			// (p/neonatal)

			if (CoreUtil.igual(Boolean.TRUE,pUtiNeo)) {
				// Paciente esteve em UTI NEO
				regCta = fatContasHospitalaresDAO.obterPorChavePrimaria(pCth);

				// Busca informacao de peso no AIP
				AipPesoPacientes pesosPaciente = getPacienteFacade().buscarPrimeiroPesosPacienteOrdenadoCriadoEm(pPacCodigo);
				if (pesosPaciente != null) {
					vPeso = pesosPaciente.getPeso();
				}

				logar("peso RN (AIP): {0}", vPeso);

				// Busca idade gestacional no AIP
				AipPacienteDadoClinicos aipPacienteDadoClinicos = getPacienteFacade().obterAipPacienteDadoClinicosPorChavePrimaria(pPacCodigo);
				if (aipPacienteDadoClinicos != null) {
					vMesGest = aipPacienteDadoClinicos.getMesesGestacao();
				}

				logar("meses gestacao RN (AIP): {0}", vMesGest);

				if (regCta != null && regCta.getTipoAltaUti() != null && vPeso != null && vMesGest != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(regCta.getTipoAltaUti().getCodigo())
					.append(mil.multiply(vPeso).intValue())
					.append(vMesGest);

					vDadosRn = Integer.valueOf(sb.toString());
				}

				logar("dados rn (nf): {0}", vDadosRn);

				if (vDadosRn == null) {
					
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(47);
					faturamentoON.criarFatLogErrors("DADOS DO RECEM NASCIDO NAO INFORMADOS CORRETAMENTE.", INT, pCth, null, null,
							null, null, pDataPrevia, null, null, null, pPhoRealiz, null, pIphRealiz, pPacCodigo, null, pPhiRealiz,
							null, pPacProntuario, pCodSusRealiz, null, null, null, null, null, null, null, RN_CTHC_ATU_LANCAUTI,
							null, null,fatMensagemLog);

					result = Boolean.FALSE;
				}
			}

			// Lanca UTI Mes Inicial:
			if (CoreUtil.maior(pDiasIni, 0)) {
				if (CoreUtil.menorOuIgual(vIdadePac, vIdadeLimite)) {
					// Paciente neonatal/pediatrico
					if (pUtiNeo) {
						// Paciente passou por UTI NEO
						// Busca cód UTI Especial Tipo III Mes Inicial
						// p/pacientes neonatal (menor de 1 ano)
						vCodUtiEsp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_NEONATAL_INI);
					} else {
						// Paciente nao passou por UTI NEO
						// Busca cód UTI Especial Tipo III Mes Inicial
						// p/pacientes pediatricos (menor de 12 anos)
						vCodUtiEsp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_PEDIATRIA_INI);
					}
				} else {
					// Paciente adulto
					// Busca cód UTI Especial Tipo III Mes Inicial p/pacientes
					// adultos (maior de 12 anos)
					vCodUtiEsp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_ADULTO_INI);
				}

				Long codigoUtiEmBuscaAtivaDeDoador = buscaCodigoUtiEmBuscaAtivaDeDoador(pCodSusRealiz, vIdadePac, buscaAtiva,
						utiBuscaAtivaMenor2anos, utiBuscaAtivaMaior2anos);
				vCodUtiEsp = codigoUtiEmBuscaAtivaDeDoador != null ? codigoUtiEmBuscaAtivaDeDoador : vCodUtiEsp;

				// Busca codigo do procedimento a lancar
				logar("v_cod_uti_esp: {0}", vCodUtiEsp);

				FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO.buscarPrimeiroItemProcedimentosHospitalares(vCodUtiEsp, DominioSituacao.A, DominioSituacao.A, DominioSituacao.A);
				if (fatItensProcedHospitalar != null) {
					vCodSusUti = fatItensProcedHospitalar.getCodTabela();
					vPhoUti = fatItensProcedHospitalar.getId().getPhoSeq();
					vIphUti = fatItensProcedHospitalar.getId().getSeq();
					vPtAnestUti = fatItensProcedHospitalar.getPontoAnestesista();
					vPtSadtUti = fatItensProcedHospitalar.getPontosSadt();
					vPtCirUti = fatItensProcedHospitalar.getPontosCirurgiao();
					vTaoUti = fatItensProcedHospitalar.getTipoAto() != null ? fatItensProcedHospitalar.getTipoAto().getCodigoSus()
							: null;
					vTivUti = fatItensProcedHospitalar.getTiposVinculo() != null ? fatItensProcedHospitalar.getTiposVinculo()
							.getCodigoSus() : null;

					vCodSusUti = nvl(vCodSusUti, 0);
					vPtAnestUti = nvl(vPtAnestUti, 0);
					vPtCirUti = nvl(vPtCirUti, 0);
					vPtSadtUti = nvl(vPtSadtUti, 0);

					logar("UTI especial mes ini: PHO: {0} IPH: {1} cod sus: {2} qtd: {3}", 
							vPhoUti, vIphUti, vCodSusUti, pDiasIni);

					// Busca valores pro item
					FatVlrItemProcedHospComps fatVlrItemProcedHospComps = verificacaoItemProcedimentoHospitalarRN
							.obterValoresItemProcHospPorModuloCompetencia(vPhoUti, vIphUti, DominioModuloCompetencia.INT, null);
					if (fatVlrItemProcedHospComps != null) {
						vVlrServHospUti = nvl(fatVlrItemProcedHospComps.getVlrServHospitalar(), BigDecimal.ZERO);
						vVlrSadtUti = nvl(fatVlrItemProcedHospComps.getVlrSadt(), BigDecimal.ZERO);
						vVlrProcedUti = nvl(fatVlrItemProcedHospComps.getVlrProcedimento(), BigDecimal.ZERO);
						vVlrAnestUti = nvl(fatVlrItemProcedHospComps.getVlrAnestesista(), BigDecimal.ZERO);
						vVlrServProfUti = nvl(fatVlrItemProcedHospComps.getVlrServProfissional(), BigDecimal.ZERO);
					} else {
						vVlrServHospUti = BigDecimal.ZERO;
						vVlrSadtUti = BigDecimal.ZERO;
						vVlrProcedUti = BigDecimal.ZERO;
						vVlrAnestUti = BigDecimal.ZERO;
						vVlrServProfUti = BigDecimal.ZERO;
					}

					vVlrProcedUti = BigDecimal.ZERO; // ETB 10/01/2008

					Calendar cal = Calendar.getInstance();
					cal.setTime(pDtAltaAdm);
					cal.add(Calendar.MONTH, -2);

					vDataUtiAnt = cal.getTime();

					vCompetenciaUti = vDataUtiAnt != null ? df.format(vDataUtiAnt) : null;

					// Busca proxima seq da tabela de espelho
					vEicSeqp = fatEspelhoItemContaHospDAO.buscaProximaSeqTabelaEspelho(pCth);

					logar("******** fatk_cth4_rn_un ************");
					logar("v_cod_sus_uti:  {0}", vCodSusUti);
					logar("  ******************** ");
					
					// Lanca procedimento de permanencia maior
					try {
						BigDecimal pDiasIniBD = pDiasIni != null ? new BigDecimal(pDiasIni) : BigDecimal.ZERO;

						FatEspelhoItemContaHosp espelho = new FatEspelhoItemContaHosp();

						espelho.setId(new FatEspelhoItemContaHospId(pCth, vEicSeqp));
						espelho.setItemProcedimentoHospitalar(fatItensProcedHospitalarDAO.obterPorChavePrimaria(
								new FatItensProcedHospitalarId(vPhoUti, vIphUti)));
						espelho.setTaoSeq(vTaoUti);
						espelho.setTivSeq(vTivUti != null ? vTivUti.byteValue() : null);
						espelho.setQuantidade(pDiasIni != null ? pDiasIni.shortValue() : null);
						espelho.setProcedimentoHospitalarSus(vCodSusUti);
						espelho.setPontosAnestesista(vPtAnestUti * pDiasIni);
						espelho.setPontosCirurgiao(vPtCirUti * pDiasIni);
						espelho.setPontosSadt(vPtSadtUti * pDiasIni);
						espelho.setIndConsistente(true);
						espelho.setIndModoCobranca(DominioModoCobranca.V);
						espelho.setValorAnestesista(pDiasIniBD.multiply(vVlrAnestUti));
						espelho.setValorProcedimento(pDiasIniBD.multiply(vVlrProcedUti));
						espelho.setValorSadt(pDiasIniBD.multiply(vVlrSadtUti));
						espelho.setValorServHosp(pDiasIniBD.multiply(vVlrServHospUti));
						espelho.setValorServProf(pDiasIniBD.multiply(vVlrServProfUti));
						espelho.setDataPrevia(pDataPrevia);
						espelho.setIndTipoItem(DominioTipoItemEspelhoContaHospitalar.D);
						espelho.setIndGeradoEncerramento(true);
						espelho.setNotaFiscal(vDadosRn);
						espelho.setCompetenciaUti(vCompetenciaUti);

						//faturamentoRN.inserirFatEspelhoItemContaHosp(espelho, true);
						getFatEspelhoItemContaHospDAO().persistir(espelho);
						this.flush();
						//faturamentoFacade.evict(espelho);
					} catch (Exception e) {
						logar("ERRO INSERT uti espec EM fat_espelhos_itens_conta_hosp");
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(71);
						faturamentoON.criarFatLogErrors("ERRO AO INSERIR ESPELHO ITEM CONTA HOSPITALAR PARA UTI ESPECIAL: "
								+ e.getMessage(), INT, pCth, null, null, vCodSusUti, null, pDataPrevia, null, null, null,
								pPhoRealiz, null, pIphRealiz, pPacCodigo, null, pPhiRealiz, null, pPacProntuario, pCodSusRealiz, null,
								vPhoUti, null, vIphUti, null, null, null, RN_CTHC_ATU_LANCAUTI, null, null,fatMensagemLog);

						result = Boolean.FALSE;
					}
				} else {
					logar("nao achou cod UTI mes ini");
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(127);
					faturamentoON.criarFatLogErrors("NAO ENCONTROU CODIGO DO PROCEDIMENTO DE UTI ESPECIAL MES INICIAL.", INT, pCth,
							null, null, null, null, pDataPrevia, null, null, null, pPhoRealiz, null, pIphRealiz, pPacCodigo, null,
							pPhiRealiz, null, pPacProntuario, pCodSusRealiz, null, null, null, null, null, null, null,
							RN_CTHC_ATU_LANCAUTI, null, null,fatMensagemLog);
					result = Boolean.FALSE;
				}
			}

			vEicSeqp = null;
			vCodUtiEsp = null;
			vCodSusUti = null;
			vPtAnestUti = null;
			vPtCirUti = null;
			vPtSadtUti = null;
			vVlrServHospUti = null;
			vVlrSadtUti = null;
			vVlrProcedUti = null;
			vVlrAnestUti = null;
			vVlrServProfUti = null;

			// Lanca UTI Mes Anterior:
			if (CoreUtil.maior(pDiasAnt, 0)) {
				if (CoreUtil.menorOuIgual(vIdadePac, vIdadeLimite)) {
					// Paciente neonatal/pediatrico
					if (pUtiNeo) {
						// Paciente passou por UTI NEO
						// Busca cód UTI Especial Tipo III Mes Anterior
						// p/pacientes neonatal (menor de 1 ano)
						vCodUtiEsp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_NEONATAL_ANT);
					} else {
						// Paciente nao passou por UTI NEO
						// Busca cód UTI Especial Tipo III Mes Anterior
						// p/paciente pediatrico (menor de 12 anos)
						vCodUtiEsp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_PEDIATRIA_ANT);
					}
				} else {
					// Paciente adulto
					// Busca cód UTI Especial Tipo III Mes Anterior p/pacientes
					// adultos (maior de 12 anos)
					vCodUtiEsp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_ADULTO_ANT);
				}

				logar("v_cod_uti_esp: {0}", vCodUtiEsp);

				vCodUtiEsp = nvl(buscaCodigoUtiEmBuscaAtivaDeDoador(pCodSusRealiz, vIdadePac, buscaAtiva, utiBuscaAtivaMenor2anos,
						utiBuscaAtivaMaior2anos), vCodUtiEsp);

				// Busca codigo do procedimento a lancar
				FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO.buscarPrimeiroItemProcedimentosHospitalares(vCodUtiEsp, DominioSituacao.A, DominioSituacao.A, DominioSituacao.A);
				if (fatItensProcedHospitalar != null) {

					vCodSusUti = fatItensProcedHospitalar.getCodTabela();
					vPhoUti = fatItensProcedHospitalar.getId().getPhoSeq();
					vIphUti = fatItensProcedHospitalar.getId().getSeq();
					vPtAnestUti = fatItensProcedHospitalar.getPontoAnestesista();
					vPtSadtUti = fatItensProcedHospitalar.getPontosSadt();
					vPtCirUti = fatItensProcedHospitalar.getPontosCirurgiao();
					vTaoUti = fatItensProcedHospitalar.getTipoAto() != null ? fatItensProcedHospitalar.getTipoAto().getCodigoSus()
							: null;
					vTivUti = fatItensProcedHospitalar.getTiposVinculo() != null ? fatItensProcedHospitalar.getTiposVinculo()
							.getCodigoSus() : null;

					vCodSusUti = nvl(vCodSusUti, 0);
					vPtAnestUti = nvl(vPtAnestUti, 0);
					vPtCirUti = nvl(vPtCirUti, 0);
					vPtSadtUti = nvl(vPtSadtUti, 0);

					logar("UTI especial mes ant: PHO: {0} IPH: {1} cod sus: {2} qtd: {3}",
						vPhoUti, vIphUti, vCodSusUti, pDiasAnt);

					// Busca valores pro item
					FatVlrItemProcedHospComps fatVlrItemProcedHospComps = verificacaoItemProcedimentoHospitalarRN.obterValoresItemProcHospPorModuloCompetencia(vPhoUti, vIphUti, DominioModuloCompetencia.INT, null);
					if (fatVlrItemProcedHospComps != null) {
						vVlrServHospUti = nvl(fatVlrItemProcedHospComps.getVlrServHospitalar(), BigDecimal.ZERO);
						vVlrSadtUti = nvl(fatVlrItemProcedHospComps.getVlrSadt(), BigDecimal.ZERO);
						vVlrProcedUti = nvl(fatVlrItemProcedHospComps.getVlrProcedimento(), BigDecimal.ZERO);
						vVlrAnestUti = nvl(fatVlrItemProcedHospComps.getVlrAnestesista(), BigDecimal.ZERO);
						vVlrServProfUti = nvl(fatVlrItemProcedHospComps.getVlrServProfissional(), BigDecimal.ZERO);
					} else {
						vVlrServHospUti = BigDecimal.ZERO;
						vVlrSadtUti = BigDecimal.ZERO;
						vVlrProcedUti = BigDecimal.ZERO;
						vVlrAnestUti = BigDecimal.ZERO;
						vVlrServProfUti = BigDecimal.ZERO;
					}

					vVlrProcedUti = BigDecimal.ZERO;

					Calendar cal = Calendar.getInstance();
					cal.setTime(pDtAltaAdm);
					cal.add(Calendar.MONTH, -1);

					vDataUtiAnt = cal.getTime();

					vCompetenciaUti = vDataUtiAnt != null ? df.format(vDataUtiAnt) : null;

					// Busca proxima seq da tabela de espelho
					vEicSeqp = fatEspelhoItemContaHospDAO.buscaProximaSeqTabelaEspelho(pCth);

					// Lanca procedimento de UTI Especial Mes Anterior
					try {
						BigDecimal pDiasAntBD = pDiasAnt != null ? new BigDecimal(pDiasAnt) : BigDecimal.ZERO;

						FatEspelhoItemContaHosp espelho = new FatEspelhoItemContaHosp();

						espelho.setId(new FatEspelhoItemContaHospId(pCth, vEicSeqp));
						espelho.setItemProcedimentoHospitalar(fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(vPhoUti, vIphUti)));
						espelho.setTaoSeq(vTaoUti);
						espelho.setTivSeq(vTivUti != null ? vTivUti.byteValue() : null);
						espelho.setQuantidade(pDiasAnt != null ? pDiasAnt.shortValue() : null);
						espelho.setProcedimentoHospitalarSus(vCodSusUti);
						espelho.setPontosAnestesista(vPtAnestUti * pDiasAnt);
						espelho.setPontosCirurgiao(vPtCirUti * pDiasAnt);
						espelho.setPontosSadt(vPtSadtUti * pDiasAnt);
						espelho.setIndConsistente(true);
						espelho.setIndModoCobranca(DominioModoCobranca.V);
						espelho.setValorAnestesista(pDiasAntBD.multiply(vVlrAnestUti));
						espelho.setValorProcedimento(pDiasAntBD.multiply(vVlrProcedUti));
						espelho.setValorSadt(pDiasAntBD.multiply(vVlrSadtUti));
						espelho.setValorServHosp(pDiasAntBD.multiply(vVlrServHospUti));
						espelho.setValorServProf(pDiasAntBD.multiply(vVlrServProfUti));
						espelho.setDataPrevia(pDataPrevia);
						espelho.setIndTipoItem(DominioTipoItemEspelhoContaHospitalar.D);
						espelho.setIndGeradoEncerramento(true);
						espelho.setNotaFiscal(vDadosRn);
						espelho.setCompetenciaUti(vCompetenciaUti);

						faturamentoRN.inserirFatEspelhoItemContaHosp(espelho, true);
						//faturamentoFacade.evict(espelho);
					} catch (Exception e) {
						logar("ERRO INSERT uti espec EM fat_espelhos_itens_conta_hosp");
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(71);
						faturamentoON.criarFatLogErrors("ERRO AO INSERIR ESPELHO ITEM CONTA HOSPITALAR PARA UTI ESPECIAL: "
								+ e.getMessage(), INT, pCth, null, null, vCodSusUti, null, pDataPrevia, null, null, null,
								pPhoRealiz, null, pIphRealiz, pPacCodigo, null, pPhiRealiz, null, pPacProntuario, pCodSusRealiz, null,
								vPhoUti, null, vIphUti, null, null, null, RN_CTHC_ATU_LANCAUTI, null, null,fatMensagemLog);

						result = Boolean.FALSE;
					}
				} else {
					logar("nao achou cod UTI mes ant");
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(126);
					faturamentoON.criarFatLogErrors("NAO ENCONTROU CODIGO DO PROCEDIMENTO DE UTI ESPECIAL MES ANTERIOR.", INT, pCth,
							null, null, null, null, pDataPrevia, null, null, null, pPhoRealiz, null, pIphRealiz, pPacCodigo, null,
							pPhiRealiz, null, pPacProntuario, pCodSusRealiz, null, null, null, null, null, null, null,
							RN_CTHC_ATU_LANCAUTI, null, null,fatMensagemLog);

					result = Boolean.FALSE;
				}
			}

			vEicSeqp = null;
			vCodUtiEsp = null;
			vCodSusUti = null;
			vPtAnestUti = null;
			vPtCirUti = null;
			vPtSadtUti = null;
			vVlrServHospUti = null;
			vVlrSadtUti = null;
			vVlrProcedUti = null;
			vVlrAnestUti = null;
			vVlrServProfUti = null;

			// Lanca UTI Mes Alta:
			if (CoreUtil.maior(pDiasAlta, 0)) {
				if (CoreUtil.menorOuIgual(vIdadePac, vIdadeLimite)) {
					// Paciente neonatal/pediatrico
					if (pUtiNeo) {
						// Paciente passou por UTI NEO
						// Busca cód UTI Especial Tipo III Mes Alta p/pacientes
						// neonatal (menor de 1 ano)
						vCodUtiEsp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_NEONATAL_ALTA);
					} else {
						// Paciente nao passou por UTI NEO
						// // Busca cód UTI Especial Tipo III Mes Alta
						// p/paciente pediatrico (menor de 12 anos)
						vCodUtiEsp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_PEDIATRIA_ALTA);
					}
				} else {
					// Paciente adulto
					// Busca cód UTI Especial Tipo III Mes Alta p/pacientes
					// adultos (maior de 12 anos)
					vCodUtiEsp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_ADULTO_ALTA);
				}

				logar("v_cod_uti_esp: {0}", vCodUtiEsp);

				vCodUtiEsp = nvl(buscaCodigoUtiEmBuscaAtivaDeDoador(pCodSusRealiz, vIdadePac, buscaAtiva, utiBuscaAtivaMenor2anos,
						utiBuscaAtivaMaior2anos), vCodUtiEsp);

				// Busca codigo do procedimento a lancar
				FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO.buscarPrimeiroItemProcedimentosHospitalares(vCodUtiEsp, DominioSituacao.A, DominioSituacao.A, DominioSituacao.A);
				if (fatItensProcedHospitalar != null) {
					vCodSusUti = fatItensProcedHospitalar.getCodTabela();
					vPhoUti = fatItensProcedHospitalar.getId().getPhoSeq();
					vIphUti = fatItensProcedHospitalar.getId().getSeq();
					vPtAnestUti = fatItensProcedHospitalar.getPontoAnestesista();
					vPtSadtUti = fatItensProcedHospitalar.getPontosSadt();
					vPtCirUti = fatItensProcedHospitalar.getPontosCirurgiao();
					vTaoUti = fatItensProcedHospitalar.getTipoAto() != null ? fatItensProcedHospitalar.getTipoAto().getCodigoSus()
							: null;
					vTivUti = fatItensProcedHospitalar.getTiposVinculo() != null ? fatItensProcedHospitalar.getTiposVinculo()
							.getCodigoSus() : null;

					vCodSusUti = nvl(vCodSusUti, 0);
					vPtAnestUti = nvl(vPtAnestUti, 0);
					vPtCirUti = nvl(vPtCirUti, 0);
					vPtSadtUti = nvl(vPtSadtUti, 0);

					logar("UTI especial mes alta: PHO: {0} IPH: {1} cod sus: {2} qtd: {3}",
						vPhoUti, vIphUti, vCodSusUti, pDiasAlta);

					// Busca valores pro item
					FatVlrItemProcedHospComps fatVlrItemProcedHospComps = verificacaoItemProcedimentoHospitalarRN
							.obterValoresItemProcHospPorModuloCompetencia(vPhoUti, vIphUti, DominioModuloCompetencia.INT, null);
					if (fatVlrItemProcedHospComps != null) {
						vVlrServHospUti = nvl(fatVlrItemProcedHospComps.getVlrServHospitalar(), BigDecimal.ZERO);
						vVlrSadtUti = nvl(fatVlrItemProcedHospComps.getVlrSadt(), BigDecimal.ZERO);
						vVlrProcedUti = nvl(fatVlrItemProcedHospComps.getVlrProcedimento(), BigDecimal.ZERO);
						vVlrAnestUti = nvl(fatVlrItemProcedHospComps.getVlrAnestesista(), BigDecimal.ZERO);
						vVlrServProfUti = nvl(fatVlrItemProcedHospComps.getVlrServProfissional(), BigDecimal.ZERO);
					} else {
						vVlrServHospUti = BigDecimal.ZERO;
						vVlrSadtUti = BigDecimal.ZERO;
						vVlrProcedUti = BigDecimal.ZERO;
						vVlrAnestUti = BigDecimal.ZERO;
						vVlrServProfUti = BigDecimal.ZERO;
					}

					vVlrProcedUti = BigDecimal.ZERO;

					// Obtem competência UTI
					vCompetenciaUti = pDtAltaAdm != null ? df.format(pDtAltaAdm) : null;

					// Busca proxima seq da tabela de espelho
					vEicSeqp = fatEspelhoItemContaHospDAO.buscaProximaSeqTabelaEspelho(pCth);

					// Lanca procedimento de UTI Especial Mes Anterior
					try {
						BigDecimal pDiasAltaBD = pDiasAlta != null ? new BigDecimal(pDiasAlta) : BigDecimal.ZERO;

						FatEspelhoItemContaHosp espelho = new FatEspelhoItemContaHosp();

						espelho.setId(new FatEspelhoItemContaHospId(pCth, vEicSeqp));
						espelho.setItemProcedimentoHospitalar(fatItensProcedHospitalarDAO.obterPorChavePrimaria(
								new FatItensProcedHospitalarId(vPhoUti, vIphUti)));
						espelho.setTaoSeq(vTaoUti);
						espelho.setTivSeq(vTivUti != null ? vTivUti.byteValue() : null);
						espelho.setQuantidade(pDiasAlta != null ? pDiasAlta.shortValue() : null);
						espelho.setProcedimentoHospitalarSus(vCodSusUti);
						espelho.setPontosAnestesista(vPtAnestUti * pDiasAlta);
						espelho.setPontosCirurgiao(vPtCirUti * pDiasAlta);
						espelho.setPontosSadt(vPtSadtUti * pDiasAlta);
						espelho.setIndConsistente(true);
						espelho.setIndModoCobranca(DominioModoCobranca.V);
						espelho.setValorAnestesista(pDiasAltaBD.multiply(vVlrAnestUti));
						espelho.setValorProcedimento(pDiasAltaBD.multiply(vVlrProcedUti));
						espelho.setValorSadt(pDiasAltaBD.multiply(vVlrSadtUti));
						espelho.setValorServHosp(pDiasAltaBD.multiply(vVlrServHospUti));
						espelho.setValorServProf(pDiasAltaBD.multiply(vVlrServProfUti));
						espelho.setDataPrevia(pDataPrevia);
						espelho.setIndTipoItem(DominioTipoItemEspelhoContaHospitalar.D);
						espelho.setIndGeradoEncerramento(true);
						espelho.setNotaFiscal(vDadosRn);
						espelho.setCompetenciaUti(vCompetenciaUti);

						faturamentoRN.inserirFatEspelhoItemContaHosp(espelho, true);
						//faturamentoFacade.evict(espelho);
					} catch (Exception e) {
						logar("ERRO INSERT uti espec EM fat_espelhos_itens_conta_hosp");
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(71);
						faturamentoON.criarFatLogErrors("ERRO AO INSERIR ESPELHO ITEM CONTA HOSPITALAR PARA UTI ESPECIAL: "
								+ e.getMessage(), INT, pCth, null, null, vCodSusUti, null, pDataPrevia, null, null, null,
								pPhoRealiz, null, pIphRealiz, pPacCodigo, null, pPhiRealiz, null, pPacProntuario, pCodSusRealiz, null,
								vPhoUti, null, vIphUti, null, null, null, RN_CTHC_ATU_LANCAUTI, null, null,fatMensagemLog);

						result = Boolean.FALSE;
					}
				} else {
					logar("nao achou cod UTI mes alta");
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(125);
					faturamentoON.criarFatLogErrors("NAO ENCONTROU CODIGO DO PROCEDIMENTO DE UTI ESPECIAL MES ALTA.", INT, pCth,
							null, null, null, null, pDataPrevia, null, null, null, pPhoRealiz, null, pIphRealiz, pPacCodigo, null,
							pPhiRealiz, null, pPacProntuario, pCodSusRealiz, null, null, null, null, null, null, null,
							RN_CTHC_ATU_LANCAUTI, null, null,fatMensagemLog);

					result = Boolean.FALSE;
				}
			}
		}

		// Verifica tipo de uti por idade (neonatal/pediatrica/adulto):
		if (CoreUtil.menorOuIgual(vIdadePac, vIdadeLimite)) {
			// Paciente neonatal/pediatrico
			if (pUtiNeo) {
				// Paciente passou por UTI NEO
				vIdadeUti = DominioTipoIdadeUTI.N;
			} else {
				// Paciente nao passou por UTI NEO
				vIdadeUti = DominioTipoIdadeUTI.P;
			}
		} else {
			// Paciente adulto
			vIdadeUti = DominioTipoIdadeUTI.A;
		}

		logar("IdUTI {0}", vIdadeUti);
		
		// ATUALIZA TIPO DE UTI POR IDADE NA CONTA:
		Short pesoRn = null;
		if(vPeso != null){
			pesoRn = mil.multiply(vPeso).shortValue();
		}

		FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO.obterPorChavePrimaria(pCth);
		FatContasHospitalares contaOld = null;
		try{
			contaOld = faturamentoFacade.clonarContaHospitalar(contaHospitalar);
		}catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
		}
		contaHospitalar.setIndIdadeUti(vIdadeUti);
		contaHospitalar.setPesoRn(pesoRn);
		contaHospitalar.setMesesGestacao(nvl(vMesGest, 0));
		faturamentoFacade.persistirContaHospitalar(contaHospitalar, contaOld, nomeMicrocomputador, dataFimVinculoServidor);

	//	atribuirContextoSessao(VariaveisSessaoEnum.FATK_CTH2_RN_UN_V_IDADE_BCO_UTI, vIdadeUti);
		fatVariaveisVO.setIdadeBlocoUti(vIdadeUti);
		logar("atualizou {0}", vIdadeUti);

		return result;
	}

	/**
	 * ORADB Function FATK_CTH4_RN_UN.P_VER_SE_BUSCA_ATIVA
	 * 
	 * Função para buscar codigos de uti em busca ativa de doador
	 */
	private Long buscaCodigoUtiEmBuscaAtivaDeDoador(Long codSusRealiz, Integer idadePac, Long buscaAtiva,
			Long utiBuscaAtivaMenor2anos, Long utiBuscaAtivaMaior2anos) {
		if ((codSusRealiz == null && buscaAtiva == null) || (codSusRealiz != null && CoreUtil.igual(codSusRealiz,buscaAtiva))) {
			return CoreUtil.menor(idadePac, 2) ? utiBuscaAtivaMenor2anos : utiBuscaAtivaMaior2anos;
		}

		return null;
	}
	
	/** ORADB: RN_CTHP_BUSCA_MATERIAIS */
	public void incluiMateriais(final Integer cthSeq, Date dataFimVinculoServidor) throws BaseException {
		final List<FatContasIntPacCirurgiasVO> cContas = getFatContasInternacaoDAO().obterFatContasIntPacCirurgiasVO(cthSeq);
		
		final FatItemContaHospitalarDAO itemDao = getFatItemContaHospitalarDAO();
		for (FatContasIntPacCirurgiasVO regConta : cContas) {
			logar("reg_conta.cirurgia: {0}", regConta.getSeqCirurgia());
			
			final List<SceRmrPacienteVO> cMaterial = estoqueFacade.listarSceRmrPacienteVOPorCirurgiaESituacao(regConta.getSeqCirurgia(), DominioSituacao.A);
			for (SceRmrPacienteVO regMaterial : cMaterial) {
				logar("reg_material.rmp_seq : {0}", regMaterial.getRmpSeq());
				logar("reg_material.numero: {0}", regMaterial.getNumero());
				logar("reg_material.quantidade: {0}", regMaterial.getQuantidade());
				
				// se ja existir na ich nao insere
				final Long cIch = itemDao.obterCountItensPorContaItemRmpsEQuantidade(	cthSeq, 
																						regMaterial.getRmpSeq(), 
																						regMaterial.getNumero(), 
																						regMaterial.getQuantidade().shortValue());
				
				if(cIch == null || cIch == 0L){
					logar("Não achou materiais");
					
					// Informar a CUM, Item e Qtde.
					comprasFacade.fatpAtuIchSce(regMaterial.getRmpSeq(), regMaterial.getNumero(), regMaterial.getQuantidade(), 
												regMaterial.getPhiSeq(), dataFimVinculoServidor);
				}
			}
		}
	}	
	
	protected FatkCthRN getFatkCthRN() {
		return fatkCthRN;
	}
	
}
