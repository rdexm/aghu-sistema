package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatContaSugestaoDesdobrDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobramentoDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContaHospitalarPacDAO;
import br.gov.mec.aghu.faturamento.vo.ContaHospitalarCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorCirurgiasCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorClinicaCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorIntCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorMotivoCadastroSugestaoDesdobramentoCirurgicoVO;
import br.gov.mec.aghu.faturamento.vo.CursorMotivosSsmCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.MotivoDesdobramentoCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerDiariasVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerItemSusVO;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobr;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobrId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.AtributoEmSeamContextManager"})
@Stateless
public class GerarSugestoesDesdobramentoON extends AbstractFatDebugLogEnableRN {

	private static final String SUGESTAO_POR_MDS_0_DT_1 = "sugestao por MDS {0} DT {1}";

	@EJB
	private FatkCth4RN fatkCth4RN;

	private static final Log LOG = LogFactory.getLog(GerarSugestoesDesdobramentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private FatkCthRNCache fatkCthRNCache;

	@Inject
	private ConstanteAghCaractUnidFuncionaisCache constanteAghCaractUnidFuncionaisCache;

	@Inject
	private MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCache motivoDesdobramentoCadastroSugestaoDesdobramentoVOCache;

	@Inject
	private CursorMotivosSsmCadastroSugestaoDesdobramentoVOCache cursorMotivosSsmCadastroSugestaoDesdobramentoVOCache;

	@Inject
	private FatContaSugestaoDesdobrDAO fatContaSugestaoDesdobrDAO;

	private static final long serialVersionUID = -2178825850839941744L;

	private static final ConstanteAghCaractUnidFuncionais[] caracteristicasUnidadesFuncionaisBlocoCCA = new ConstanteAghCaractUnidFuncionais[] {
			ConstanteAghCaractUnidFuncionais.CCA, ConstanteAghCaractUnidFuncionais.BLOCO };

	public List<Integer> gerarSugestoesDesdobramento() throws BaseException {

		//IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		//super.commitTransaction();
		//super.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
		//faturamentoFacade.setTimeout(TRANSACTION_TIMEOUT_24_HORAS);
		//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		LOG.debug("inicio: " + df.format(new Date()));

		// Limpa sugestoes que nao foram desconsideradas para refeze-las:
		this.getFatContaSugestaoDesdobrDAO().removeContasSugestaoDesdobramento(null, null, null, true);

		this.flush();
		//super.commitTransaction();
		
		// O delete de todas sugestoes sera feito no encerramento da conta e no desdobramento da conta
		// Executa sugestao com base nos itens de conta
		List<Integer> codigosCths = this.getFatContasHospitalaresDAO().listarContasHospitalaresParaGerarSugestoesDesdobramento(
				new DominioSituacaoConta[] { DominioSituacaoConta.A, DominioSituacaoConta.F }, DominioGrupoConvenio.S);
		return codigosCths;
	}

	public Boolean geraSugestaoDesdobramentoContaHospitalar(final Integer cthSeq) throws BaseException {
        // delete FAT_CONTA_SUGESTAO_DESDOBRS where cth_seq = p_cth_seq and ind_considera = 'S';
		getFatContaSugestaoDesdobrDAO().removeContasSugestaoDesdobramento(null, cthSeq, null, true);
		
		return fatcRnCsdcCadSugDesd(cthSeq, null);
	}
	
	/**
	 * ORADB FATC_RN_CSDC_CAD_SUG_DESD
	 * 
	 * @param pCthSeq
	 * @param pMdsSeq
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	public Boolean fatcRnCsdcCadSugDesd(Integer pCthSeq, Byte pMdsSeq) throws BaseException {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();

		FaturamentoRN faturamentoRN = this.getFaturamentoRN();
		FatkCth4RN fatkCth4RN = this.getFatkCth4RN();

		FatContaSugestaoDesdobrDAO fatContaSugestaoDesdobrDAO = this.getFatContaSugestaoDesdobrDAO();
		FatContasHospitalaresDAO fatContasHospitalaresDAO = this.getFatContasHospitalaresDAO();
		FatContasInternacaoDAO fatContasInternacaoDAO = this.getFatContasInternacaoDAO();
		FatItemContaHospitalarDAO fatItemContaHospitalarDAO = this.getFatItemContaHospitalarDAO();
		FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = this.getFatItensProcedHospitalarDAO();

		ConstanteAghCaractUnidFuncionaisCache constanteAghCaractUnidFuncionaisCache = this.getConstanteAghCaractUnidFuncionaisCache();
		CursorMotivosSsmCadastroSugestaoDesdobramentoVOCache cursorMotivosSsmCadastroSugestaoDesdobramentoVOCache = this
				.getCursorMotivosSsmCadastroSugestaoDesdobramentoVOCache();
		FatkCthRNCache fatkCthRNCache = this.getFatkCthRNCache();
		MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCache motivoDesdobramentoCadastroSugestaoDesdobramentoVOCache = this
				.getMotivoDesdobramentoCadastroSugestaoDesdobramentoVOCache();

		ContaHospitalarCadastroSugestaoDesdobramentoVO regConta = null;
		MotivoDesdobramentoCadastroSugestaoDesdobramentoVO regMotivo = null;
		CursorCirurgiasCadastroSugestaoDesdobramentoVO regCirurgia = null;
		CursorMotivosSsmCadastroSugestaoDesdobramentoVO regMotivoSsm = null;
		CursorIntCadastroSugestaoDesdobramentoVO regInt = null;
		CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO regIchAtivo = null;
		Integer vClcCodigo = null;
		Boolean vIndHospDia = null;
		Boolean vIndTipoAih5 = null;
		Short vIphPhoSeq = null;
		Short vPho = null;
		Integer vIph = null;
		Integer vIphSeq = null;
		Short vPermPrevista = null;
		Integer vIntSeq = null;
		Date vDthrReal = null;
		Short vIchUnf = null;
		String vOrigem = null;
		Boolean vOk = null;
		Boolean vOk2 = false;
		Boolean vUmMotivo = false;
		Date vDthrRealizado = null;
		Date vDthrSugestao = null;
		Date vCca = null;
		BigDecimal vDias = null;
		Integer vDiariasConta = null;
		Integer vDiasUti = null;

		String siglaModuloFaturamento = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SIGLA_MODULO_FATURAMENTO);
		String siglaCentroCirurgicoAmbulatorial = this
				.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SIGLA_CENTRO_CIRURGICO_AMBULATORIAL);
		String siglaUnidadeBlocoCirurgico = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SIGLA_UNIDADE_BLOCO_CIRURGICO);

		Byte codigoSUSMotInternacaoClinicaCirurgia = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_INTERNACAO_CLINICA_CIRURGIA);
		Byte codigoSUSMotCirurgiaReintervencao = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_CIRURGIA_REINTERVENCAO);
		Byte codigoSUSMotInternacaoCirurgicaNaoSubmtdCirurgia = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_INTERNACAO_CIRURGICA_NAO_SUBMTD_CIRURGIA);
		Byte codigoSUSMotInternacaoPsiquiatrica = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_INTERNACAO_PSIQUIATRICA);
		Byte codigoSUSMotInternacaoObstetrica = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_INTERNACAO_OBSTETRICA);
		Byte codigoSUSMotInternacaoClinica = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_INTERNACAO_CLINICA);
		Byte codigoSUSMotPermanenciaCTI = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_PERMANENCIA_CTI);
		Byte codigoSUSMotInternacaoProcedimentoCca = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_INTERNACAO_PROCEDIMENTO_CCA);
		Byte codigoSUSMotPermanenciaInternacaoAposCirurgia = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_PERMANENCIA_INTERNACAO_APOS_CIRURGIA);
		Byte codigoSUSMotInternacaoHospitalDia = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_INTERNACAO_HOSPITAL_DIA);
		Byte codigoSUSMotInternacaoTratamentoAih5 = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_INTERNACAO_TRATAMENTO_AIH_5);
		Byte codigoSUSCirurgiaRealizadaApos7DiasEmIntCirurgica = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_CIRURGIA_REALIZ_APOS_7_DIAS_EM_INT_CIRUR);
		Byte codigoSUSInternacaoCirurgicaComCirurgiaApos7Dias = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_INTERNACAO_CIRUR_COM_CIRURGIA_APOS_7_DIAS);

		List<ContaHospitalarCadastroSugestaoDesdobramentoVO> listaContasHospitalares = fatContasHospitalaresDAO
				.listarContasHospitalaresParaCadastrarSugestoesDesdobramento(pCthSeq, pMdsSeq, new DominioSituacaoConta[] {
						DominioSituacaoConta.A, DominioSituacaoConta.F }, DominioGrupoConvenio.S);

		logar("inicio...");
		logar("fetch");

		if (listaContasHospitalares != null && !listaContasHospitalares.isEmpty()) {
			for (int i = 0; i < listaContasHospitalares.size(); i++) {
				
				regConta = listaContasHospitalares.get(i);

				logar("CTH {0} DTINT {1} DTALTA {2} CNV {3} CSP {4} TAH {5} REAL {6} SOL {7}", regConta.getSeq(),
						regConta.getDtIntAdministrativa(), regConta.getDtAltaAdministrativa(), regConta.getCspCnvCodigo(),
						regConta.getCspSeq(), regConta.getTahSeq(), regConta.getPhiSeqRealizado(), regConta.getPhiSeq());

				vClcCodigo = null;
				vIphPhoSeq = null;
				vIphSeq = null;
				vCca = null;
				vDthrSugestao = null;

				List<CursorClinicaCadastroSugestaoDesdobramentoVO> listaCursorClinicaVO = fatContasInternacaoDAO
						.cursorClinicaCadastroSugestaoDesdobramento(regConta.getSeq(), 0, 1);
				if (listaCursorClinicaVO != null && !listaCursorClinicaVO.isEmpty()) {
					CursorClinicaCadastroSugestaoDesdobramentoVO vo = listaCursorClinicaVO.get(0);
					vIntSeq = vo.getMaxSeqInternacao();
					vClcCodigo = vo.getCodigoClinica();
				}

				if (regConta.getPhiSeqRealizado() != null) {
					vDthrReal = fatItemContaHospitalarDAO.buscaMinDtrhRealizadoCadastroSugestaoDesdobramento(regConta.getSeq(),
							regConta.getPhiSeqRealizado(), regConta.getCspCnvCodigo(), regConta.getCspSeq(),
							DominioSituacaoItenConta.A, DominioSituacao.A, true);
				}

				logar("INT {0} CLC {1} DTREAL {2}", vIntSeq, vClcCodigo, vDthrReal);

				if (vClcCodigo != null) {
					List<MotivoDesdobramentoCadastroSugestaoDesdobramentoVO> listaMotivosDesdobramentos = motivoDesdobramentoCadastroSugestaoDesdobramentoVOCache
							.listaMotivosDesdobramentoCadastroSugestaoDesdobramento(regConta.getTahSeq(),
									vClcCodigo != null ? vClcCodigo.byteValue() : null, pMdsSeq, DominioSituacao.A);

					if (listaMotivosDesdobramentos != null && !listaMotivosDesdobramentos.isEmpty()) {
						for (int j = 0; j < listaMotivosDesdobramentos.size(); j++) {
							
							regMotivo = listaMotivosDesdobramentos.get(j);

							logar("MDS {0} DIAS {1} QTDMIN {2} DIAPOS {3} TIPO {4} SUS {5}", regMotivo.getSeq(),
									regMotivo.getDiasAposInternacao(), regMotivo.getQtdMinima(), regMotivo.getDiasAposProcedimento(),
									regMotivo.getTipoDesdobramento(), regMotivo.getCodigoSus());

							vOk = false;

							if (CoreUtil.igual(regMotivo.getCodigoSus(), codigoSUSMotInternacaoClinicaCirurgia)) {
								// Internacao clinica submetido a cirurgia
								List<CursorCirurgiasCadastroSugestaoDesdobramentoVO> listaCursorCirurgiasVO = fatItemContaHospitalarDAO
										.listarCursorCirurgiasCadastroSugestaoDesdobramentoVO(regConta.getSeq(),
												regConta.getCspCnvCodigo(), regConta.getCspSeq(), DominioSituacaoItenConta.A,
												caracteristicasUnidadesFuncionaisBlocoCCA, DominioSituacao.A, true, 0, 1);

								if (listaCursorCirurgiasVO != null && !listaCursorCirurgiasVO.isEmpty()) {
									regCirurgia = listaCursorCirurgiasVO.get(0);

									ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
											.buscaPrimeiraCaractUnidFuncionais(regCirurgia.getUnfSeq(),
													caracteristicasUnidadesFuncionaisBlocoCCA);

									if (caracteristica != null) {
										if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
											vOrigem = siglaUnidadeBlocoCirurgico;
										} else if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
											vOrigem = siglaCentroCirurgicoAmbulatorial;
										}
									}

									vOrigem = nvl(vOrigem, siglaModuloFaturamento);

									if (CoreUtil.igual(vOrigem, siglaUnidadeBlocoCirurgico)) {
										Calendar cal = Calendar.getInstance();
										cal.setTime(regCirurgia.getDthrRealizado());
										cal.add(Calendar.MINUTE, -1);

										vDthrSugestao = cal.getTime();
										vOk = true;
									}

									logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
								}
							} else if (CoreUtil.igual(regMotivo.getCodigoSus(), codigoSUSMotCirurgiaReintervencao)) {
								// Cirurgia realizada +24 horas apos anterior
								// (reintervenção)
								List<CursorCirurgiasCadastroSugestaoDesdobramentoVO> listaCursorCirurgiasVO = fatItemContaHospitalarDAO
										.listarCursorCirurgiasCadastroSugestaoDesdobramentoVO(regConta.getSeq(),
												regConta.getCspCnvCodigo(), regConta.getCspSeq(), DominioSituacaoItenConta.A,
												caracteristicasUnidadesFuncionaisBlocoCCA, DominioSituacao.A, true, null, null);

								if (listaCursorCirurgiasVO != null && !listaCursorCirurgiasVO.isEmpty()) {
									for (int k = 0; k < listaCursorCirurgiasVO.size(); k++) {
										regCirurgia = listaCursorCirurgiasVO.get(k);
										if (vOk) {
											break;
										}

										if (vDthrRealizado == null) {
											vDias = null;
											vDthrRealizado = regCirurgia.getDthrRealizado();
										} else if (!CoreUtil.igual(vDthrRealizado, regCirurgia.getDthrRealizado())) {
											vDias = DateUtil.calcularDiasEntreDatasComPrecisao(vDthrRealizado,
													regCirurgia.getDthrRealizado());
											vDthrRealizado = regCirurgia.getDthrRealizado();
										}

										if (vDias != null && CoreUtil.maior(vDias, regMotivo.getDiasAposProcedimento())) {
											Calendar cal = Calendar.getInstance();
											cal.setTime(vDthrRealizado);
											cal.add(Calendar.MINUTE, -1);

											vDthrSugestao = cal.getTime();

											vOk = true;

											ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
													.buscaPrimeiraCaractUnidFuncionais(regCirurgia.getUnfSeq(),
															caracteristicasUnidadesFuncionaisBlocoCCA);

											if (caracteristica != null) {
												if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
													vOrigem = siglaUnidadeBlocoCirurgico;
												} else if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
													vOrigem = siglaCentroCirurgicoAmbulatorial;
												}
											}

											vOrigem = nvl(vOrigem, siglaModuloFaturamento);

											logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
										} else if (vDias != null && CoreUtil.igual(vDias, regMotivo.getDiasAposProcedimento())) {
											Calendar cal1 = Calendar.getInstance();
											cal1.setTime(regCirurgia.getDthrRealizado());

											Calendar cal2 = Calendar.getInstance();
											cal2.setTime(vDthrRealizado);

											if ((CoreUtil.maior(cal1.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.HOUR_OF_DAY)))
													|| (CoreUtil.igual(cal1.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.HOUR_OF_DAY)) && CoreUtil
															.maior(cal1.get(Calendar.MINUTE), cal2.get(Calendar.MINUTE)))) {
												Calendar cal = Calendar.getInstance();
												cal.setTime(vDthrRealizado);
												cal.add(Calendar.MINUTE, -1);

												vDthrSugestao = cal.getTime();

												vOk = true;

												ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
														.buscaPrimeiraCaractUnidFuncionais(regCirurgia.getUnfSeq(),
																caracteristicasUnidadesFuncionaisBlocoCCA);

												if (caracteristica != null) {
													if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
														vOrigem = siglaUnidadeBlocoCirurgico;
													} else if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
														vOrigem = siglaCentroCirurgicoAmbulatorial;
													}
												}

												vOrigem = nvl(vOrigem, siglaModuloFaturamento);

												logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
											}
										}
										//Não pode fazer evict de VO, trocando comando por flush para manter o comportamento anterior 	
										//faturamentoFacade.evict(regCirurgia);
										flush();
									}
								}
							} else if (CoreUtil.in(regMotivo.getCodigoSus(), codigoSUSMotInternacaoPsiquiatrica,
									codigoSUSMotInternacaoObstetrica)) {
								// Internacao psiquiatrica a partir de 46 dias,
								// internacao obstetrica a partir de 8 dias
								BigDecimal aux = BigDecimal.ONE.add(DateUtil.calcularDiasEntreDatasComPrecisao(
										regConta.getDtIntAdministrativa(), nvl(regConta.getDtAltaAdministrativa(), new Date())));

								if (CoreUtil.maiorOuIgual(aux, regMotivo.getDiasAposInternacao())) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(regConta.getDtIntAdministrativa());
									cal.add(Calendar.DAY_OF_MONTH, regMotivo.getDiasAposInternacao() - 1);

									vDthrSugestao = cal.getTime();
									vOk = true;

									vOrigem = nvl(vOrigem, siglaModuloFaturamento);

									logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
								}
							} else if (CoreUtil.igual(regMotivo.getCodigoSus(), codigoSUSMotInternacaoClinica)) {
								// Internacao clinica a partir de 100 dias
								RnCthcVerItemSusVO rnCthcVerItemSusVO = fatkCthRNCache.rnCthcVerItemSus(DominioOrigemProcedimento.I,
										regConta.getCspCnvCodigo(), regConta.getCspSeq(), (short) 1,
										nvl(regConta.getPhiSeqRealizado(), regConta.getPhiSeq()));

								if (rnCthcVerItemSusVO != null) {
									vIphPhoSeq = rnCthcVerItemSusVO.getPhoSeq();
									vIphSeq = rnCthcVerItemSusVO.getIphSeq();

									vOk2 = rnCthcVerItemSusVO.getRetorno();

									if (Boolean.TRUE.equals(vOk2)) {
										// Busca permanencia prevista
										vDiariasConta = DateUtil.calcularDiasEntreDatas(
												DateUtil.truncaData(regConta.getDtIntAdministrativa()),
												DateUtil.truncaData(nvl(regConta.getDtAltaAdministrativa(), new Date())));

										FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO
												.obterPorChavePrimaria(new FatItensProcedHospitalarId(vIphPhoSeq, vIphSeq));
										if (fatItensProcedHospitalar != null
												&& fatItensProcedHospitalar.getQuantDiasFaturamento() != null) {
											vPermPrevista = fatItensProcedHospitalar.getQuantDiasFaturamento();
										}

										vPermPrevista = nvl(vPermPrevista, vDiariasConta);

										// Busca diarias de UTI
										RnCthcVerDiariasVO rnCthcVerDiarias = fatkCth4RN.rnCthcVerDiarias(regConta.getSeq(), vDiasUti,
												null);
										if (rnCthcVerDiarias != null) {
											vDiasUti = rnCthcVerDiarias.getDiasUti();
											vDthrSugestao = rnCthcVerDiarias.getRetorno();
										}

										vDiasUti = nvl(vDiasUti, 0);

										logar("100 DIAS: diarias conta: {0} perm: {1} diarias uti: {2}", vDiariasConta, vPermPrevista,
												vDiasUti);

										int numDias = DateUtil
												.calcularDiasEntreDatas(
														DateUtil.truncaData(regConta.getDtIntAdministrativa()),
														DateUtil.truncaData(nvl(regConta.getDtAltaAdministrativa(), new Date())))
												- (vPermPrevista.intValue() * 2) - vDiasUti;

										if (CoreUtil.maiorOuIgual(numDias, regMotivo.getDiasAposInternacao())) {
											Calendar cal = Calendar.getInstance();
											cal.setTime(regConta.getDtIntAdministrativa());
											cal.add(Calendar.DAY_OF_MONTH,
													(vPermPrevista * 2) + vDiasUti + regMotivo.getDiasAposInternacao());

											vDthrSugestao = DateUtil.truncaData(cal.getTime());
											vOk = true;

											vOrigem = siglaModuloFaturamento;

											logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
										}
									}
								}
							} else if (CoreUtil.igual(regMotivo.getCodigoSus(), codigoSUSMotPermanenciaCTI)) {
								// Permanencia em cti a partir de 60 dias
								RnCthcVerDiariasVO rnCthcVerDiarias = fatkCth4RN.rnCthcVerDiarias(regConta.getSeq(), vDiasUti,
										regMotivo.getQtdMinima());
								if (rnCthcVerDiarias != null) {
									vDiasUti = rnCthcVerDiarias.getDiasUti();
									vDthrSugestao = rnCthcVerDiarias.getRetorno();
								}

								if (vDthrSugestao != null) {
									vOk = true;

									vOrigem = siglaModuloFaturamento;

									logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
								}
							} else if (CoreUtil.in(regMotivo.getCodigoSus(), codigoSUSCirurgiaRealizadaApos7DiasEmIntCirurgica,
									codigoSUSInternacaoCirurgicaComCirurgiaApos7Dias)) {
								// Internacao cirur. com cirurgia apos 7 dias
								List<CursorCirurgiasCadastroSugestaoDesdobramentoVO> listaCursorCirurgiasVO = fatItemContaHospitalarDAO
										.listarCursorCirurgiasCadastroSugestaoDesdobramentoVO(regConta.getSeq(),
												regConta.getCspCnvCodigo(), regConta.getCspSeq(), DominioSituacaoItenConta.A,
												caracteristicasUnidadesFuncionaisBlocoCCA, DominioSituacao.A, true, 0, 1);

								if (listaCursorCirurgiasVO != null && !listaCursorCirurgiasVO.isEmpty()) {
									regCirurgia = listaCursorCirurgiasVO.get(0);

									BigDecimal aux = BigDecimal.ONE.add(DateUtil.calcularDiasEntreDatasComPrecisao(
											regConta.getDtIntAdministrativa(), regCirurgia.getDthrRealizado()));

									if (CoreUtil.maiorOuIgual(aux, regMotivo.getDiasAposInternacao())) {
										vDthrSugestao = regCirurgia.getDthrRealizado();
										vOk = true;

										ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
												.buscaPrimeiraCaractUnidFuncionais(regCirurgia.getUnfSeq(),
														caracteristicasUnidadesFuncionaisBlocoCCA);

										if (caracteristica != null) {
											if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
												vOrigem = siglaUnidadeBlocoCirurgico;
											} else if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
												vOrigem = siglaCentroCirurgicoAmbulatorial;
											}
										}

										vOrigem = nvl(vOrigem, siglaModuloFaturamento);

										logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
									}
								}
							} else if (CoreUtil.igual(regMotivo.getCodigoSus(), codigoSUSMotInternacaoCirurgicaNaoSubmtdCirurgia)) {
								// Internacao cirur. não submetido a cirurgia
								List<CursorCirurgiasCadastroSugestaoDesdobramentoVO> auxListaCursorCirurgiasVO = fatItemContaHospitalarDAO
										.listarCursorCirurgiasCadastroSugestaoDesdobramentoVO(regConta.getSeq(),
												regConta.getCspCnvCodigo(), regConta.getCspSeq(), DominioSituacaoItenConta.A,
												caracteristicasUnidadesFuncionaisBlocoCCA, DominioSituacao.A, true, 0, 1);

								if (auxListaCursorCirurgiasVO == null || auxListaCursorCirurgiasVO.isEmpty()) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(regConta.getDtIntAdministrativa());
									cal.add(Calendar.DAY_OF_MONTH, regMotivo.getDiasAposInternacao() - 1);

									vDthrSugestao = cal.getTime();

									if (DateValidator.validaDataMenorIgual(vDthrSugestao, new Date())) {
										vOk = true;

										vOrigem = siglaModuloFaturamento;

										logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
									}
								} else {
									regCirurgia = auxListaCursorCirurgiasVO.get(0);
								}
							} else if (CoreUtil.igual(regMotivo.getCodigoSus(), codigoSUSMotInternacaoProcedimentoCca)) {
								// Internacao com procedimento no cca
								vCca = fatItemContaHospitalarDAO.buscaMenorDthrRealizadoCursorItensUF(regConta.getSeq(),
										regConta.getCspCnvCodigo(), regConta.getCspSeq(), DominioSituacaoItenConta.A,
										ConstanteAghCaractUnidFuncionais.CCA, DominioSituacao.A, true);

								if (vCca != null) {
									vDthrSugestao = vCca;
									vOk = true;
									vOrigem = siglaCentroCirurgicoAmbulatorial;

									logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
								}
							} else if (CoreUtil.igual(regMotivo.getCodigoSus(), codigoSUSMotPermanenciaInternacaoAposCirurgia)) {
								// Permanece internado apos cirurgia
								logar("permanece internado {0}", regMotivo.getCodigoSus());
								vDthrRealizado = null;

								List<CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO> listaVO = fatItemContaHospitalarDAO
										.listarMaxCirurgiaCadastroSugestaoDesdobramento(regConta.getSeq(), regConta.getCspCnvCodigo(),
												regConta.getCspSeq(), DominioSituacaoItenConta.A, DominioSituacao.A, true, 0, 1);

								if (listaVO != null && !listaVO.isEmpty()) {
									CursorMaxCirurgiaCadastroSugestaoDesdobramentoVO vo = listaVO.get(0);

									vDthrRealizado = vo.getDthrRealizado();
									vIchUnf = vo.getIchUnf();
								}

								if (vDthrRealizado != null) {
									logar("data real não nulo {0} DT {1}", regMotivo.getCodigoSus(), vDthrRealizado);

									if (CoreUtil.maiorOuIgual(
											BigDecimal.ONE.add(DateUtil.calcularDiasEntreDatasComPrecisao(vDthrRealizado,
													nvl(regConta.getDtAltaAdministrativa(), new Date()))),
											regMotivo.getDiasAposProcedimento())) {
										Calendar cal = Calendar.getInstance();
										cal.setTime(vDthrRealizado);
										cal.add(Calendar.DAY_OF_MONTH, regMotivo.getDiasAposProcedimento() - 1);

										vDthrSugestao = cal.getTime();

										logar("dias {0}", regMotivo.getDiasAposProcedimento());
										logar("data sugestão {0} DT {1}", regMotivo.getCodigoSus(), vDthrSugestao);

										vOk = true;

										ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
												.buscaPrimeiraCaractUnidFuncionais(vIchUnf, caracteristicasUnidadesFuncionaisBlocoCCA);

										if (caracteristica != null) {
											if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
												vOrigem = siglaUnidadeBlocoCirurgico;
											} else if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
												vOrigem = siglaCentroCirurgicoAmbulatorial;
											}
										}

										vOrigem = nvl(vOrigem, siglaModuloFaturamento);

										logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
									}
								}
							} else if (CoreUtil.in(regMotivo.getCodigoSus(), codigoSUSMotInternacaoHospitalDia,
									codigoSUSMotInternacaoTratamentoAih5)) {
								// Internacao em hospital dia a partir de 46
								// dias, internacao em tratamento de aih5 a
								// partir de 46 dias
								RnCthcVerItemSusVO rnCthcVerItemSusVO = fatkCthRNCache.rnCthcVerItemSus(DominioOrigemProcedimento.I,
										regConta.getCspCnvCodigo(), regConta.getCspSeq(), (short) 1,
										nvl(regConta.getPhiSeqRealizado(), regConta.getPhiSeq()));

								if (rnCthcVerItemSusVO != null) {
									vIphPhoSeq = rnCthcVerItemSusVO.getPhoSeq();
									vIphSeq = rnCthcVerItemSusVO.getIphSeq();

									vOk2 = rnCthcVerItemSusVO.getRetorno();

									if (Boolean.TRUE.equals(vOk2)) {
										vIndHospDia = false;
										vIndTipoAih5 = false;

										FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO
												.obterPorChavePrimaria(new FatItensProcedHospitalarId(vIphPhoSeq, vIphSeq));
										if (fatItensProcedHospitalar != null
												&& Boolean.TRUE.equals(fatItensProcedHospitalar.getInternacao())) {
											vIndHospDia = fatItensProcedHospitalar.getHospDia();
											vIndTipoAih5 = fatItensProcedHospitalar.getTipoAih5();
										}

										if (((Boolean.TRUE.equals(vIndHospDia) && CoreUtil.igual(regMotivo.getCodigoSus(),
												codigoSUSMotInternacaoHospitalDia)) || (Boolean.TRUE.equals(vIndTipoAih5) && CoreUtil
												.igual(regMotivo.getCodigoSus(), codigoSUSMotInternacaoTratamentoAih5)))
												&& (CoreUtil.maiorOuIgual(
														BigDecimal.ONE.add(DateUtil.calcularDiasEntreDatasComPrecisao(
																regConta.getDtIntAdministrativa(),
																nvl(regConta.getDtAltaAdministrativa(), new Date()))),
														regMotivo.getDiasAposInternacao()))) {
											Calendar cal = Calendar.getInstance();
											cal.setTime(regConta.getDtIntAdministrativa());
											cal.add(Calendar.DAY_OF_MONTH, regMotivo.getDiasAposInternacao() - 1);

											vDthrSugestao = cal.getTime();

											vOk = true;
											vOrigem = siglaModuloFaturamento;

											logar(SUGESTAO_POR_MDS_0_DT_1, regMotivo.getCodigoSus(), vDthrSugestao);
										}
									}
								}
							}

							if (Boolean.TRUE.equals(vOk)) {
								if (regConta.getDtAltaAdministrativa() != null
										&& CoreUtil.isMaiorOuIgualDatas(vDthrSugestao, regConta.getDtAltaAdministrativa())) {
									vOk = false;
									logar("dt sugest >= dt alta adm");
								} else {
									vUmMotivo = true;
									logar("um motivo TRUE");
								}
							}

							if (Boolean.TRUE.equals(vOk) && pMdsSeq == null && vDthrSugestao != null) {
								logar("vok true, p_mds null, dthrsug not null");

								if (regInt != null) {
									regInt.setQrtNumero(null);
									regInt.setLtoLtoId(null);
								}

								List<CursorIntCadastroSugestaoDesdobramentoVO> listaIntVO = fatContasInternacaoDAO
										.listarCursorIntCadastroSugestaoDesdobramento(regConta.getSeq(), 0, 1);

								if (listaIntVO != null && !listaIntVO.isEmpty()) {
									regInt = listaIntVO.get(0);

									logar("QRT {0} LTO {1}", regInt.getQrtNumero(), regInt.getLtoLtoId());

									try {
										FatContaSugestaoDesdobr auxContaSugestaoDesdobr = fatContaSugestaoDesdobrDAO
												.obterPorChavePrimaria(new FatContaSugestaoDesdobrId(regMotivo.getSeq(), regConta
														.getSeq(), vDthrSugestao));
										if (auxContaSugestaoDesdobr == null) {
											FatContaSugestaoDesdobr fatContaSugestaoDesdobr = new FatContaSugestaoDesdobr();
											fatContaSugestaoDesdobr.setId(new FatContaSugestaoDesdobrId(regMotivo.getSeq(), regConta
													.getSeq(), vDthrSugestao));
											fatContaSugestaoDesdobr.setOrigem(vOrigem);
											fatContaSugestaoDesdobr.setQuartoNumero(regInt.getQrtNumero());
											fatContaSugestaoDesdobr.setConsidera(true);

											if (StringUtils.isNotBlank(regInt.getLtoLtoId()) || regInt.getQrtNumero() != null) {
												fatContaSugestaoDesdobr.setLtoId(StringUtils.isNotBlank(regInt.getLtoLtoId()) ? regInt
														.getLtoLtoId() : String.valueOf(regInt.getQrtNumero()));
											}

											faturamentoRN.inserirFatContaSugestaoDesdobr(fatContaSugestaoDesdobr, true);
										} else {
											logar("dup_val_on_index");

											List<FatContaSugestaoDesdobr> contasSugestoesDesdobramento = fatContaSugestaoDesdobrDAO
													.listarContasSugestaoDesdobramento(regMotivo.getSeq(), regConta.getSeq());
											if (contasSugestoesDesdobramento != null && !contasSugestoesDesdobramento.isEmpty()) {
												for (FatContaSugestaoDesdobr contaSugestaoDesdobr : contasSugestoesDesdobramento) {
													contaSugestaoDesdobr.setOrigem(vOrigem);
													contaSugestaoDesdobr.setQuartoNumero(regInt.getQrtNumero());

													if (StringUtils.isNotBlank(regInt.getLtoLtoId()) || regInt.getQrtNumero() != null) {
														contaSugestaoDesdobr
																.setLtoId(StringUtils.isNotBlank(regInt.getLtoLtoId()) ? regInt
																		.getLtoLtoId() : String.valueOf(regInt.getQrtNumero()));
													}

													faturamentoRN.atualizarFatContaSugestaoDesdobr(contaSugestaoDesdobr, true);
													faturamentoFacade.evict(contaSugestaoDesdobr);
												}
											}
										}
									} catch (Exception e) {
										logar("others");

										LOG.error("Exceção capturada", e);
										throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_INSERIR_SUGESTAO_DESDOBRAMENTO,
												e.getMessage());
									}
								}
							}

							//faturamentoFacade.evict(regMotivo);
							flush();
						}
					}
				}

				logar("Desdobramento SSM");

				// Verifica desdobramento pelo phi_seq do item da conta
				List<CursorItemContaHospitalarAtivoCadastroSugestaoDesdobramentoVO> listaItensAtivos = fatItemContaHospitalarDAO
						.listarItensAtivosCadastroSugestaoDesdobramento(regConta.getSeq(), DominioSituacaoItenConta.A);

				if (listaItensAtivos != null && !listaItensAtivos.isEmpty()) {
					for (int j = 0; j < listaItensAtivos.size(); j++) {

						regIchAtivo = listaItensAtivos.get(j);

						logar("Desdobramento SSM {0}", regIchAtivo.getPhiSeq());

						vPho = null;
						vIph = null;

						RnCthcVerItemSusVO rnCthcVerItemSusVO = fatkCthRNCache.rnCthcVerItemSus(DominioOrigemProcedimento.I,
								regConta.getCspCnvCodigo(), regConta.getCspSeq(), (short) 1, regIchAtivo.getPhiSeq());

						if (rnCthcVerItemSusVO != null) {
							vPho = rnCthcVerItemSusVO.getPhoSeq();
							vIph = rnCthcVerItemSusVO.getIphSeq();

							vOk2 = rnCthcVerItemSusVO.getRetorno();

							if (Boolean.TRUE.equals(vOk2)) {
								List<CursorMotivosSsmCadastroSugestaoDesdobramentoVO> listaVO = cursorMotivosSsmCadastroSugestaoDesdobramentoVOCache
										.listarMotivosSsmCadastroSugestaoDesdobramento(vPho, vIph, DominioSituacao.A);

								if (listaVO != null && !listaVO.isEmpty()) {
									for (int k = 0; k < listaVO.size(); k++) {
										
										regMotivoSsm = listaVO.get(k);

										logar("Desd item {0} rzdo {1}", regIchAtivo.getPhiSeq(), regIchAtivo.getDthrRealizado());

										if (CoreUtil.maiorOuIgual(
												BigDecimal.ONE.add(DateUtil.calcularDiasEntreDatasComPrecisao(
														regIchAtivo.getDthrRealizado(),
														nvl(regConta.getDtAltaAdministrativa(), new Date()))),
												regMotivoSsm.getDiasInternacao())) {
											logar("Dias {0}", regMotivoSsm.getDiasInternacao());

											Calendar cal = Calendar.getInstance();
											cal.setTime(regConta.getDtIntAdministrativa());
											cal.add(Calendar.DAY_OF_MONTH, regMotivoSsm.getDiasInternacao() - 1);

											vDthrSugestao = cal.getTime();

											logar("Data {0}", vDthrSugestao);

											vOk = true;
											vOrigem = siglaModuloFaturamento;
											vUmMotivo = true;

											if (regInt != null) {
												regInt.setQrtNumero(null);
												regInt.setLtoLtoId(null);
											}

											List<CursorIntCadastroSugestaoDesdobramentoVO> listaIntVO = fatContasInternacaoDAO
													.listarCursorIntCadastroSugestaoDesdobramento(regConta.getSeq(), 0, 1);

											if (listaIntVO != null && !listaIntVO.isEmpty()) {
												regInt = listaIntVO.get(0);

												logar("QRT {0} LTO {1}", regInt.getQrtNumero(), regInt.getLtoLtoId());

												try {
													FatContaSugestaoDesdobr auxContaSugestaoDesdobr = fatContaSugestaoDesdobrDAO
															.obterPorChavePrimaria(new FatContaSugestaoDesdobrId(regMotivoSsm
																	.getMdsSeq(), regConta.getSeq(), vDthrSugestao));
													if (auxContaSugestaoDesdobr == null) {
														FatContaSugestaoDesdobr fatContaSugestaoDesdobr = new FatContaSugestaoDesdobr();
														fatContaSugestaoDesdobr.setId(new FatContaSugestaoDesdobrId(regMotivoSsm
																.getMdsSeq(), regConta.getSeq(), vDthrSugestao));
														fatContaSugestaoDesdobr.setOrigem(vOrigem);
														fatContaSugestaoDesdobr.setQuartoNumero(regInt.getQrtNumero());
														fatContaSugestaoDesdobr.setConsidera(true);

														if (StringUtils.isNotBlank(regInt.getLtoLtoId())
																|| regInt.getQrtNumero() != null) {
															fatContaSugestaoDesdobr.setLtoId(StringUtils.isNotBlank(regInt
																	.getLtoLtoId()) ? regInt.getLtoLtoId() : String.valueOf(regInt
																	.getQrtNumero()));
														}

														faturamentoRN.inserirFatContaSugestaoDesdobr(fatContaSugestaoDesdobr, true);
													} else {
														logar("dup_val_on_index");

														List<FatContaSugestaoDesdobr> contasSugestoesDesdobramento = fatContaSugestaoDesdobrDAO
																.listarContasSugestaoDesdobramento(regMotivoSsm.getMdsSeq(),
																		regConta.getSeq());
														if (contasSugestoesDesdobramento != null
																&& !contasSugestoesDesdobramento.isEmpty()) {
															for (FatContaSugestaoDesdobr contaSugestaoDesdobr : contasSugestoesDesdobramento) {
																contaSugestaoDesdobr.setOrigem(vOrigem);
																contaSugestaoDesdobr.setQuartoNumero(regInt.getQrtNumero());

																if (StringUtils.isNotBlank(regInt.getLtoLtoId())
																		|| regInt.getQrtNumero() != null) {
																	contaSugestaoDesdobr.setLtoId(StringUtils.isNotBlank(regInt
																			.getLtoLtoId()) ? regInt.getLtoLtoId() : String
																			.valueOf(regInt.getQrtNumero()));
																}

																faturamentoRN.atualizarFatContaSugestaoDesdobr(contaSugestaoDesdobr,
																		true);
																faturamentoFacade.evict(contaSugestaoDesdobr);
															}
														}

													}
												} catch (Exception e) {
													logar("others");

													LOG.error("Exceção capturada", e);
													throw new ApplicationBusinessException(
															FaturamentoExceptionCode.ERRO_INSERIR_SUGESTAO_DESDOBRAMENTO,
															e.getMessage());
												}
											}
										}

										//faturamentoFacade.evict(regMotivoSsm);
										flush();
									}
								}
							}
						}

						//faturamentoFacade.evict(regIchAtivo);
						flush();
					}
				}

				logar("fetch");
				faturamentoFacade.clear();
			}
		}

		if (pMdsSeq == null && vUmMotivo) {
			logar("p_mds null, v_um_motivo true");
			//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
			//super.commitTransaction();
			//super.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
			faturamentoFacade.clear();
		}

		return vUmMotivo;
	}
	
	public List<CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO> obterCirurgiasAgendadas(){
		final Date auxDate = new Date();
		
		List<CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO> listaCirurgiasAgendadas = getBlocoCirurgicoFacade()
				.listarCirurgiasAgendadasCadastroSugestaoDesdobramento(DateUtil.obterDataComHoraInical(auxDate),
						DateUtil.obterDataComHoraFinal(auxDate), caracteristicasUnidadesFuncionaisBlocoCCA, DominioGrupoConvenio.S,
						DominioTipoPlano.I, DominioSituacaoCirurgia.AGND);
		
		return listaCirurgiasAgendadas;
	}

	/**
	 * ORADB FATP_RN_CSD_SUG_DESD_CRG
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	public void fatpRnCsdSugDesdCrg() throws BaseException {
		IBlocoCirurgicoFacade blocoCirurgicoFacade = this.getBlocoCirurgicoFacade();
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();

		FaturamentoRN faturamentoRN = this.getFaturamentoRN();

		FatContasInternacaoDAO fatContasInternacaoDAO = this.getFatContasInternacaoDAO();
		FatContaSugestaoDesdobrDAO fatContaSugestaoDesdobrDAO = this.getFatContaSugestaoDesdobrDAO();
		FatItemContaHospitalarDAO fatItemContaHospitalarDAO = this.getFatItemContaHospitalarDAO();
		FatMotivoDesdobramentoDAO fatMotivoDesdobramentoDAO = this.getFatMotivoDesdobramentoDAO();
		VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO = this.getVFatContaHospitalarPacDAO();

		ConstanteAghCaractUnidFuncionaisCache constanteAghCaractUnidFuncionaisCache = this.getConstanteAghCaractUnidFuncionaisCache();

		CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO regAgenda = null;
		VFatContaHospitalarPac regConta = null;
		CursorIntCadastroSugestaoDesdobramentoVO regInt = null;
		CursorMotivoCadastroSugestaoDesdobramentoCirurgicoVO regMotivo = null;
		CursorCirurgiasCadastroSugestaoDesdobramentoVO regCirurgia = null;

		Integer vClcCodigo = null;
		String vOrigem = null;
		Boolean vOk = null;
		Date vDthrRealizado = null;
		Date vDthrSugestao = null;
		BigDecimal vDias = null;
		Date vCca = null;

		String siglaModuloFaturamento = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SIGLA_MODULO_FATURAMENTO);
		String siglaCentroCirurgicoAmbulatorial = this
				.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SIGLA_CENTRO_CIRURGICO_AMBULATORIAL);
		String siglaUnidadeBlocoCirurgico = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SIGLA_UNIDADE_BLOCO_CIRURGICO);

		Byte codigoSUSMotInternacaoClinicaCirurgia = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_INTERNACAO_CLINICA_CIRURGIA);
		Byte codigoSUSMotCirurgiaReintervencao = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_CIRURGIA_REINTERVENCAO);
		Byte codigoSUSMotInternacaoProcedimentoCca = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_MOT_INTERNACAO_PROCEDIMENTO_CCA);
		Byte codigoSUSInternacaoCirurgicaComCirurgiaApos7Dias = this
				.buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_SUS_INTERNACAO_CIRUR_COM_CIRURGIA_APOS_7_DIAS);

		Date auxDate = new Date();
		List<CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO> listaCirurgiasAgendadas = blocoCirurgicoFacade
				.listarCirurgiasAgendadasCadastroSugestaoDesdobramento(DateUtil.obterDataComHoraInical(auxDate),
						DateUtil.obterDataComHoraFinal(auxDate), caracteristicasUnidadesFuncionaisBlocoCCA, DominioGrupoConvenio.S,
						DominioTipoPlano.I, DominioSituacaoCirurgia.AGND);


		if (listaCirurgiasAgendadas != null && !listaCirurgiasAgendadas.isEmpty()) {
			for (int i = 0; i < listaCirurgiasAgendadas.size(); i++) {
				regAgenda = listaCirurgiasAgendadas.get(i);

				if (regConta != null) {
					regConta.setCthSeq(null);
				}

				List<VFatContaHospitalarPac> listaViewContaHospitalarPac = vFatContaHospitalarPacDAO
						.listarContasHospitalaresCadastroSugestaoDesdobramento(regAgenda.getPacCodigo(), regAgenda.getData(),
								regAgenda.getCspCnvCodigo(), regAgenda.getCspSeq(), new DominioSituacaoConta[] {
										DominioSituacaoConta.A, DominioSituacaoConta.F }, 0, 1);
				if (listaViewContaHospitalarPac != null && !listaViewContaHospitalarPac.isEmpty()) {
					regConta = listaViewContaHospitalarPac.get(0);

					if (regConta.getCthSeq() != null) {
						vClcCodigo = null;
						vCca = null;
						vDthrSugestao = null;

						List<CursorClinicaCadastroSugestaoDesdobramentoVO> listaCursorClinicaVO = fatContasInternacaoDAO
								.cursorClinicaCadastroSugestaoDesdobramento(regConta.getCthSeq(), 0, 1);
						if (listaCursorClinicaVO != null && !listaCursorClinicaVO.isEmpty()) {
							CursorClinicaCadastroSugestaoDesdobramentoVO vo = listaCursorClinicaVO.get(0);
							vClcCodigo = vo.getCodigoClinica();
						}

						if (regConta.getCthPhiSeqRealizado() != null) {
							fatItemContaHospitalarDAO.buscaMinDtrhRealizadoCadastroSugestaoDesdobramento(regConta.getCthSeq(),
									regConta.getCthPhiSeqRealizado(), regAgenda.getCspCnvCodigo(), regAgenda.getCspSeq(),
									DominioSituacaoItenConta.A, DominioSituacao.A, true);
						}

						if (vClcCodigo != null) {
							List<CursorMotivoCadastroSugestaoDesdobramentoCirurgicoVO> listaMotivos = fatMotivoDesdobramentoDAO
									.listarMotivosCadastroSugestaoDesdobramentoCirurgico(regConta.getTipoAih() != null ? regConta
											.getTipoAih().getSeq() : null, vClcCodigo, DominioSituacao.A, new Byte[] {
											codigoSUSMotInternacaoClinicaCirurgia, codigoSUSMotCirurgiaReintervencao,
											codigoSUSInternacaoCirurgicaComCirurgiaApos7Dias, codigoSUSMotInternacaoProcedimentoCca });

							if (listaMotivos != null && !listaMotivos.isEmpty()) {
								for (int j = 0; j < listaMotivos.size(); j++) {
									regMotivo = listaMotivos.get(j);

									vOk = false;

									if (CoreUtil.igual(regMotivo.getCodigoSus(), codigoSUSMotInternacaoClinicaCirurgia)) {
										// Internacao clinica submetido a
										// cirurgia
										List<CursorCirurgiasCadastroSugestaoDesdobramentoVO> listaCirurgias = fatItemContaHospitalarDAO
												.listarCursorCirurgiasCadastroSugestaoDesdobramentoCirurgica(regConta.getCthSeq(),
														regAgenda.getCspCnvCodigo(), regAgenda.getCspSeq(),
														DominioSituacaoItenConta.A, caracteristicasUnidadesFuncionaisBlocoCCA,
														DominioSituacao.A, true, 0, 1);

										if (listaCirurgias != null && !listaCirurgias.isEmpty()) {
											regCirurgia = listaCirurgias.get(0);

											ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
													.buscaPrimeiraCaractUnidFuncionais(regCirurgia.getUnfSeq(),
															caracteristicasUnidadesFuncionaisBlocoCCA);

											if (caracteristica != null) {
												if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
													vOrigem = siglaUnidadeBlocoCirurgico;
												} else if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
													vOrigem = siglaCentroCirurgicoAmbulatorial;
												}
											}

											vOrigem = nvl(vOrigem, siglaModuloFaturamento);

											if (CoreUtil.igual(vOrigem, siglaUnidadeBlocoCirurgico)) {
												Calendar cal = Calendar.getInstance();
												cal.setTime(regCirurgia.getDthrRealizado());
												cal.add(Calendar.MINUTE, -1);

												vDthrSugestao = cal.getTime();
												vOk = true;
											}
										} else {
											ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
													.buscaPrimeiraCaractUnidFuncionais(regAgenda.getUnfSeq(),
															caracteristicasUnidadesFuncionaisBlocoCCA);

											if (caracteristica != null) {
												if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
													vOrigem = siglaUnidadeBlocoCirurgico;
												} else if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
													vOrigem = siglaCentroCirurgicoAmbulatorial;
												}
											}

											vOrigem = nvl(vOrigem, siglaModuloFaturamento);

											if (CoreUtil.igual(vOrigem, siglaUnidadeBlocoCirurgico)) {
												if (regAgenda.getData() != null) {
													Calendar cal = Calendar.getInstance();
													cal.setTime(regAgenda.getData());
													cal.add(Calendar.MINUTE, -1);

													vDthrSugestao = cal.getTime();
												} else {
													vDthrSugestao = new Date();
												}

												vOk = true;
											}
										}
									} else if (CoreUtil.igual(regMotivo.getCodigoSus(), codigoSUSMotCirurgiaReintervencao)) {
										// Cirurgia realizada +24 horas apos
										// anterior
										List<CursorCirurgiasCadastroSugestaoDesdobramentoVO> listaCirurgias = fatItemContaHospitalarDAO
												.listarCursorCirurgiasCadastroSugestaoDesdobramentoCirurgica(regConta.getCthSeq(),
														regAgenda.getCspCnvCodigo(), regAgenda.getCspSeq(),
														DominioSituacaoItenConta.A, caracteristicasUnidadesFuncionaisBlocoCCA,
														DominioSituacao.A, true, null, null);

										if (listaCirurgias != null && !listaCirurgias.isEmpty()) {
											for (int k = 0; k < listaCirurgias.size(); k++) {
												if (Boolean.TRUE.equals(vOk)) {
													break;
												}

												regCirurgia = listaCirurgias.get(k);

												if (vDthrRealizado != null) {
													vDthrRealizado = regCirurgia.getDthrRealizado();
													vDias = DateUtil.calcularDiasEntreDatasComPrecisao(regCirurgia.getDthrRealizado(),
															regAgenda.getData());

													ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
															.buscaPrimeiraCaractUnidFuncionais(regAgenda.getUnfSeq(),
																	caracteristicasUnidadesFuncionaisBlocoCCA);

													if (caracteristica != null) {
														if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
															vOrigem = siglaUnidadeBlocoCirurgico;
														} else if (CoreUtil
																.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
															vOrigem = siglaCentroCirurgicoAmbulatorial;
														}
													}

													vOrigem = nvl(vOrigem, siglaModuloFaturamento);
												} else {
													vDias = DateUtil.calcularDiasEntreDatasComPrecisao(vDthrRealizado,
															regCirurgia.getDthrRealizado());

													Calendar cal = Calendar.getInstance();
													cal.setTime(regCirurgia.getDthrRealizado());
													cal.add(Calendar.MINUTE, -1);

													vDthrRealizado = cal.getTime();

													ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
															.buscaPrimeiraCaractUnidFuncionais(regCirurgia.getUnfSeq(),
																	caracteristicasUnidadesFuncionaisBlocoCCA);

													if (caracteristica != null) {
														if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
															vOrigem = siglaUnidadeBlocoCirurgico;
														} else if (CoreUtil
																.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
															vOrigem = siglaCentroCirurgicoAmbulatorial;
														}
													}

													vOrigem = nvl(vOrigem, siglaModuloFaturamento);
												}

												if (vDias != null && CoreUtil.maior(vDias, regMotivo.getDiasAposProcedimento())) {
													Calendar cal = Calendar.getInstance();
													cal.setTime(vDthrRealizado);
													cal.add(Calendar.MINUTE, -1);

													vDthrSugestao = cal.getTime();

													vOk = true;
												} else if (vDias != null && CoreUtil.igual(vDias, regMotivo.getDiasAposProcedimento())) {
													Calendar cal1 = Calendar.getInstance();
													cal1.setTime(regCirurgia.getDthrRealizado());

													Calendar cal2 = Calendar.getInstance();
													cal2.setTime(vDthrRealizado);

													if ((CoreUtil
															.maior(cal1.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.HOUR_OF_DAY)))
															|| (CoreUtil.igual(cal1.get(Calendar.HOUR_OF_DAY),
																	cal2.get(Calendar.HOUR_OF_DAY)) && CoreUtil.maior(
																	cal1.get(Calendar.MINUTE), cal2.get(Calendar.MINUTE)))) {
														Calendar cal = Calendar.getInstance();
														cal.setTime(vDthrRealizado);
														cal.add(Calendar.MINUTE, -1);

														vDthrSugestao = cal.getTime();
														vOk = true;
													}
												}

												//faturamentoFacade.evict(regCirurgia);
												flush();
											}
										}
									} else if (CoreUtil.igual(regMotivo.getCodigoSus(),
											codigoSUSInternacaoCirurgicaComCirurgiaApos7Dias)) {
										// Internacao cirur. com cirurgia apos 7
										// dias
										List<CursorCirurgiasCadastroSugestaoDesdobramentoVO> listaCirurgias = fatItemContaHospitalarDAO
												.listarCursorCirurgiasCadastroSugestaoDesdobramentoCirurgica(regConta.getCthSeq(),
														regAgenda.getCspCnvCodigo(), regAgenda.getCspSeq(),
														DominioSituacaoItenConta.A, caracteristicasUnidadesFuncionaisBlocoCCA,
														DominioSituacao.A, true, 0, 1);

										if (listaCirurgias != null && !listaCirurgias.isEmpty()) {
											regCirurgia = listaCirurgias.get(0);

											if (CoreUtil.maiorOuIgual(
													BigDecimal.ONE.add(DateUtil.calcularDiasEntreDatasComPrecisao(
															regConta.getCthDtIntAdministrativa(), regCirurgia.getDthrRealizado())),
													regMotivo.getDiasAposInternacao())) {
												Calendar cal = Calendar.getInstance();
												cal.setTime(regConta.getCthDtIntAdministrativa());
												cal.add(Calendar.DAY_OF_MONTH, regMotivo.getDiasAposInternacao() - 1);

												vDthrSugestao = cal.getTime();
												vOk = true;

												ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
														.buscaPrimeiraCaractUnidFuncionais(regCirurgia.getUnfSeq(),
																caracteristicasUnidadesFuncionaisBlocoCCA);

												if (caracteristica != null) {
													if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
														vOrigem = siglaUnidadeBlocoCirurgico;
													} else if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
														vOrigem = siglaCentroCirurgicoAmbulatorial;
													}
												}

												vOrigem = nvl(vOrigem, siglaModuloFaturamento);
											}
										} else if (Boolean.FALSE.equals(vOk)) {
											if (CoreUtil.maiorOuIgual(
													BigDecimal.ONE.add(DateUtil.calcularDiasEntreDatasComPrecisao(
															regConta.getCthDtIntAdministrativa(), regAgenda.getData())),
													regMotivo.getDiasAposInternacao())) {
												
												Calendar cal = Calendar.getInstance();
												cal.setTime(regConta.getCthDtIntAdministrativa());
												cal.add(Calendar.DAY_OF_MONTH, regMotivo.getDiasAposInternacao() - 1);

												vDthrSugestao = cal.getTime();

												vOk = true;

												ConstanteAghCaractUnidFuncionais caracteristica = constanteAghCaractUnidFuncionaisCache
														.buscaPrimeiraCaractUnidFuncionais(regAgenda.getUnfSeq(),
																caracteristicasUnidadesFuncionaisBlocoCCA);

												if (caracteristica != null) {
													if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.BLOCO)) {
														vOrigem = siglaUnidadeBlocoCirurgico;
													} else if (CoreUtil.igual(caracteristica, ConstanteAghCaractUnidFuncionais.CCA)) {
														vOrigem = siglaCentroCirurgicoAmbulatorial;
													}
												}

												vOrigem = nvl(vOrigem, siglaModuloFaturamento);
											}
										}
									} else if (CoreUtil.igual(regMotivo.getCodigoSus(), codigoSUSMotInternacaoProcedimentoCca)) {
										// Internacao com procedimento no CCA
										vCca = fatItemContaHospitalarDAO.buscaMenorDthrRealizadoCursorItensUF(regConta.getCthSeq(),
												regAgenda.getCspCnvCodigo(), regAgenda.getCspSeq(), DominioSituacaoItenConta.A,
												ConstanteAghCaractUnidFuncionais.CCA, DominioSituacao.A, true);

										if (vCca != null) {
											vDthrSugestao = vCca;
											vOk = true;
											vOrigem = siglaCentroCirurgicoAmbulatorial;
										}
									}

									if (Boolean.TRUE.equals(vOk)) {
										if (regConta.getCthDtAltaAdministrativa() != null
												&& CoreUtil.maiorOuIgual(vDthrSugestao != null ? vDthrSugestao.getTime() : null,
														regConta.getCthDtAltaAdministrativa().getTime())) {
											vOk = false;
										}

										if (regInt != null) {
											regInt.setQrtNumero(null);
											regInt.setLtoLtoId(null);
										}

										List<CursorIntCadastroSugestaoDesdobramentoVO> listaIntVO = fatContasInternacaoDAO
												.listarCursorIntCadastroSugestaoDesdobramento(regConta.getCthSeq(), 0, 1);

										if (listaIntVO != null && !listaIntVO.isEmpty()) {
											regInt = listaIntVO.get(0);

											try {
												FatContaSugestaoDesdobr auxContaSugestaoDesdobr = fatContaSugestaoDesdobrDAO
														.obterPorChavePrimaria(new FatContaSugestaoDesdobrId(regMotivo.getSeq(),
																regConta.getCthSeq(), vDthrSugestao));

												if (auxContaSugestaoDesdobr == null) {
													FatContaSugestaoDesdobr fatContaSugestaoDesdobr = new FatContaSugestaoDesdobr();
													fatContaSugestaoDesdobr.setId(new FatContaSugestaoDesdobrId(regMotivo.getSeq(),
															regConta.getCthSeq(), vDthrSugestao));
													fatContaSugestaoDesdobr.setOrigem(vOrigem);
													fatContaSugestaoDesdobr.setQuartoNumero(regInt.getQrtNumero());
													fatContaSugestaoDesdobr.setConsidera(true);

													if (StringUtils.isNotBlank(regInt.getLtoLtoId()) || regInt.getQrtNumero() != null) {
														fatContaSugestaoDesdobr
																.setLtoId(StringUtils.isNotBlank(regInt.getLtoLtoId()) ? regInt
																		.getLtoLtoId() : String.valueOf(regInt.getQrtNumero()));
													}

													faturamentoRN.inserirFatContaSugestaoDesdobr(fatContaSugestaoDesdobr, true);
												} else {
													List<FatContaSugestaoDesdobr> contasSugestoesDesdobramento = fatContaSugestaoDesdobrDAO
															.listarContasSugestaoDesdobramento(regMotivo.getSeq(),
																	regConta.getCthSeq());

													if (contasSugestoesDesdobramento != null
															&& !contasSugestoesDesdobramento.isEmpty()) {
														for (FatContaSugestaoDesdobr contaSugestaoDesdobr : contasSugestoesDesdobramento) {
															contaSugestaoDesdobr.setOrigem(vOrigem);
															contaSugestaoDesdobr.setQuartoNumero(regInt.getQrtNumero());

															if (StringUtils.isNotBlank(regInt.getLtoLtoId())
																	|| regInt.getQrtNumero() != null) {
																contaSugestaoDesdobr.setLtoId(StringUtils.isNotBlank(regInt
																		.getLtoLtoId()) ? regInt.getLtoLtoId() : String.valueOf(regInt
																		.getQrtNumero()));
															}

															faturamentoRN.atualizarFatContaSugestaoDesdobr(contaSugestaoDesdobr, true);
															faturamentoFacade.evict(contaSugestaoDesdobr);
														}
													}
												}
											} catch (Exception e) {
												logar("erro para {0} {1} {2}", regMotivo.getSeq(), regConta.getCthSeq(), vOrigem);

												LOG.error("Exceção capturada", e);
												throw new ApplicationBusinessException(
														FaturamentoExceptionCode.ERRO_INSERIR_SUGESTAO_DESDOBRAMENTO, e.getMessage());
											}
										}
									}

									//faturamentoFacade.evict(regMotivo);
									flush();
								}
							}
						}
					}
				}

				//faturamentoFacade.evict(regAgenda);
				flush();
			}
		}

		//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
		//super.commitTransaction();
		//super.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
		faturamentoFacade.clear();
	}

	public Long geracaoSugestoesDesdobramentoCount() {
		Long aux = 0L;

		aux += this.getFatContasHospitalaresDAO().listarContasHospitalaresParaGerarSugestoesDesdobramentoCount(
				new DominioSituacaoConta[] { DominioSituacaoConta.A, DominioSituacaoConta.F }, DominioGrupoConvenio.S);

		Date auxDate = new Date();

		List<CursorCirurgiaAgendadaCadastroSugestaoDesdobramentoVO> listaCirurgiasAgendadas = this.getBlocoCirurgicoFacade()
				.listarCirurgiasAgendadasCadastroSugestaoDesdobramento(DateUtil.obterDataComHoraInical(auxDate),
						DateUtil.obterDataComHoraFinal(auxDate), caracteristicasUnidadesFuncionaisBlocoCCA, DominioGrupoConvenio.S,
						DominioTipoPlano.I, DominioSituacaoCirurgia.AGND);
		if (listaCirurgiasAgendadas != null && !listaCirurgiasAgendadas.isEmpty()) {
			aux += listaCirurgiasAgendadas.size();
		}

		return aux;
	}

	protected FatkCth4RN getFatkCth4RN() {
		return fatkCth4RN;
	}

	protected FatContaSugestaoDesdobrDAO getFatContaSugestaoDesdobrDAO() {
		return fatContaSugestaoDesdobrDAO;
	}

	protected CursorMotivosSsmCadastroSugestaoDesdobramentoVOCache getCursorMotivosSsmCadastroSugestaoDesdobramentoVOCache() {
		return cursorMotivosSsmCadastroSugestaoDesdobramentoVOCache;
	}

	protected FatkCthRNCache getFatkCthRNCache() {
		return fatkCthRNCache;
	}

	protected MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCache getMotivoDesdobramentoCadastroSugestaoDesdobramentoVOCache() {
		return motivoDesdobramentoCadastroSugestaoDesdobramentoVOCache;
	}

	protected ConstanteAghCaractUnidFuncionaisCache getConstanteAghCaractUnidFuncionaisCache() {
		return constanteAghCaractUnidFuncionaisCache;
	}
}
