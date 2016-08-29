package br.gov.mec.aghu.faturamento.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoDebugCode;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.BuscaContaVO;
import br.gov.mec.aghu.faturamento.vo.CmceCthSeqVO;
import br.gov.mec.aghu.faturamento.vo.ParSsmSolicRealizVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * ORADB: <code>FATF_ARQ_TXT_INT</code>
 * 
 * Se chamava GeracaoArquivoTextoInternacaoRN, agora se chama
 * GeracaoArquivoTextoInternacaoBean para ficar dentro do padrão do sistema.
 * 
 * @author gandriotti
 * 
 */
@Stateless
public class GeracaoArquivoTextoInternacaoBean extends AbstractFatDebugExtraFileLogEnable implements GeracaoArquivoTextoInternacaoInterface {


	@EJB
	private FaturamentoInternacaoRN faturamentoInternacaoRN;

	@EJB
	private GeracaoArquivoCsvRN geracaoArquivoCsvRN;

	@EJB
	private ContaHospitalarRN contaHospitalarRN;

	private static final int MAGIC_INT_QTD_MAXIMA_SQL_IN_EQ_999 = 999;
	private static final String MAGIC_STRING_SITUACAO_SMS_NULL_EQ_SPACE = " ";
	protected static final String MAGIC_STRING_CARACTERISTICA_EQ_COBRA_EM_AIH = "Cobra Em AIH";
	protected static final Byte MAGIC_BYTE_CSP_SEQ_EQ_1 = Byte.valueOf((byte) 1);
	protected static final Short MAGIC_SHORT_CNV_CODIGO_EQ_1 = Short.valueOf((short) 1);
	private static final String MAGIC_STRING_FAT_LOG_ERRO_FORA_EST_EQ_PACIENTE_FORA_DO_ESTADO = "PACIENTE FORA DO ESTADO";
	private static final String MAGIC_STRING_FAT_LOG_ERRO_ESP_CIR_EQ_CONTA_COM_SSM = "CONTA COM SSM CIRURGICO E SEM CIRURGIA CORRESPONDENTE";
	protected static final String MAGIC_STRING_LEITO_SEM_INTERNACAO_EQ_S_INT = "S/INT";
	protected static final String MAGIC_STRING_PREFIXO_ARQ_CSV_EQ_CTH = "CTH_";
	
	private static final DominioSituacaoConta[] MAGIC_ARRAY_DOM_SIT_CONTA_EQ_A_E_F = 
			new DominioSituacaoConta[] { DominioSituacaoConta.A,DominioSituacaoConta.E, DominioSituacaoConta.F };
	private static final Byte MAGIC_SHORT_CNV_SEQ_EQ_1 = Byte.valueOf((byte) 1);
	/**
	 * 
	 */
	private static final long serialVersionUID = -6094507221413743816L;

	protected GeracaoArquivoCsvRN getGeracaoArquivoCsvRN() {
		return geracaoArquivoCsvRN;
	}

	protected ContaHospitalarRN getContaHospitalarRN() {
		return contaHospitalarRN;
	}

	protected static BigDecimal obterValorTotal(final BuscaContaVO vo) {

		BigDecimal result = null;
		List<BigDecimal> valorList = null;

		valorList = new LinkedList<BigDecimal>();
		// nvl(VALOR_SH,0) 
		valorList.add(vo.getValorSh());
		// + nvl(VALOR_UTI,0) 		
		valorList.add(vo.getValorUti());
		// + nvl(VALOR_UTIE,0) 
		valorList.add(vo.getValorUtie());
		// + nvl(VALOR_SP,0) 
		valorList.add(vo.getValorSp());
		// + nvl(VALOR_ACOMP,0) 
		valorList.add(vo.getValorAcomp());
		// + nvl(VALOR_RN,0) 
		valorList.add(vo.getValorRn());
		// + nvl(VALOR_SADT,0) 
		valorList.add(vo.getValorSadt());
		// + nvl(VALOR_HEMAT,0) 
		valorList.add(vo.getValorHemat());
		// + nvl(VALOR_TRANSP,0) 
		valorList.add(vo.getValorTransp());
		// + nvl(VALOR_OPM,0) 
		valorList.add(vo.getValorOpm());
		// + nvl(VALOR_ANESTESISTA,0) 
		valorList.add(vo.getValorAnestesista());
		// + nvl(VALOR_PROCEDIMENTO,0) valor_conta,
		valorList.add(vo.getValorProcedimento());
		// soma
		result = BigDecimal.ZERO;
		for (BigDecimal val : valorList) {
			if (val != null) {
				result = result.add(val);
			}
		}

		return result;
	}

	protected String obterStatuSms(final String indEnviadoSms, final String indAutorizadoSms) {
		String result = faturamentoInternacaoRN.situacaoSMS(indEnviadoSms, indAutorizadoSms);
		return (result != null ? result : MAGIC_STRING_SITUACAO_SMS_NULL_EQ_SPACE);
	}

	protected List<BuscaContaVO> obterListaVoInt() {

		List<BuscaContaVO> result = null;
		FatContasInternacaoDAO dao = null;

		dao = this.getFatContasInternacaoDAO();
		result = dao.listarParaCsvContasPeriodoViaInt(MAGIC_SHORT_CNV_CODIGO_EQ_1, MAGIC_SHORT_CNV_SEQ_EQ_1, MAGIC_ARRAY_DOM_SIT_CONTA_EQ_A_E_F);

		return result;
	}

	protected List<BuscaContaVO> obterListaVoDcs() {

		List<BuscaContaVO> result = null;
		FatContasInternacaoDAO dao = null;

		dao = this.getFatContasInternacaoDAO();
		result = dao.listarParaCsvContasPeriodoViaDcs(MAGIC_SHORT_CNV_CODIGO_EQ_1, MAGIC_SHORT_CNV_SEQ_EQ_1, MAGIC_ARRAY_DOM_SIT_CONTA_EQ_A_E_F);

		return result;
	}

	protected static <T> List<List<T>> separarListaEmListasDeTamMax(final List<T> lista, final int maximo) {

		List<List<T>> result = null;
		List<T> lst = null;
		int size = 0;
		int ndxIni = 0;
		int ndxFim = 0;

		result = new LinkedList<List<T>>();
		size = lista.size();
		if (size <= maximo) {
			result.add(lista);
		} else {
			while (ndxIni < size) {
				ndxFim = ndxIni + maximo;
				ndxFim = Math.min(ndxFim, size);
				lst = lista.subList(ndxIni, ndxFim);
				result.add(lst);
				ndxIni += maximo;
			}
		}

		return result;
	}

	protected Map<Integer, ParSsmSolicRealizVO> obterMapaCthSeqSsm(final List<Integer> listaCthSeq) {

		Map<Integer, ParSsmSolicRealizVO> result = null;
		Map<Integer, ParSsmSolicRealizVO> parcial = null;
		List<List<Integer>> fatiado = null;
		FaturamentoRN fatRn = null;

		result = new HashMap<Integer, ParSsmSolicRealizVO>();
		fatRn = this.getFaturamentoRN();
		fatiado = separarListaEmListasDeTamMax(listaCthSeq, MAGIC_INT_QTD_MAXIMA_SQL_IN_EQ_999);
		for (List<Integer> lstParcial : fatiado) {
			parcial = fatRn.listarSsmParaListaCthSeqSsmSolicRealiz(lstParcial, MAGIC_SHORT_CNV_CODIGO_EQ_1, MAGIC_BYTE_CSP_SEQ_EQ_1);
			result.putAll(parcial);
		}

		return result;
	}

	protected Map<Integer, ParSsmSolicRealizVO> obterMapaCthSeqSsmComplex(final List<Integer> listaCthSeq) {

		Map<Integer, ParSsmSolicRealizVO> result = null;
		Map<Integer, ParSsmSolicRealizVO> parcial = null;
		List<List<Integer>> fatiado = null;
		FaturamentoRN fatRn = null;

		result = new HashMap<Integer, ParSsmSolicRealizVO>();
		fatRn = this.getFaturamentoRN();
		fatiado = separarListaEmListasDeTamMax(listaCthSeq, MAGIC_INT_QTD_MAXIMA_SQL_IN_EQ_999);
		for (List<Integer> lstParcial : fatiado) {
			parcial = fatRn.listarSsmComplexidadeParaListaCthSeqSsmSolicRealiz(lstParcial, MAGIC_SHORT_CNV_CODIGO_EQ_1, MAGIC_BYTE_CSP_SEQ_EQ_1);
			result.putAll(parcial);
		}

		return result;
	}

	/**
	 * ORADB: <code>FATC_VER_CTH_EH_MAE</code>
	 * @param cthSeq
	 * @return
	 */
	protected List<Integer> obterListaCthSeqContaDesdobrada(final List<Integer> listaCthSeq) {

		List<Integer> result = null;
		List<Integer> parcial = null;
		List<List<Integer>> fatiado = null;
		FatContasHospitalaresDAO dao = null;
		Integer[] arrCthSeq = null;

		dao = this.getFatContasHospitalaresDAO();
		result = new LinkedList<Integer>();
		fatiado = separarListaEmListasDeTamMax(listaCthSeq, MAGIC_INT_QTD_MAXIMA_SQL_IN_EQ_999);
		for (List<Integer> lstParcial : fatiado) {
			arrCthSeq = new Integer[lstParcial.size()];
			arrCthSeq = lstParcial.toArray(arrCthSeq);
			parcial = dao.listarCthSeqPorFilha(arrCthSeq);
			result.addAll(parcial);
		}

		return result;
	}

	/**
	 * ORADB: <code>FATC_VER_CTH_ESP_CIR</code>
	 * @param cthSeq
	 * @return
	 */
	protected List<Integer> obterListaCthSeqPorErro(final List<Integer> listaCthSeq, final String erro) {

		List<Integer> result = null;
		List<Integer> parcial = null;
		List<List<Integer>> fatiado = null;
		FatLogErrorDAO dao = null;
		Integer[] arrCthSeq = null;

		dao = this.getFatLogErrorDAO();
		result = new LinkedList<Integer>();
		fatiado = separarListaEmListasDeTamMax(listaCthSeq, MAGIC_INT_QTD_MAXIMA_SQL_IN_EQ_999);
		for (List<Integer> lstParcial : fatiado) {
			arrCthSeq = new Integer[lstParcial.size()];
			arrCthSeq = lstParcial.toArray(arrCthSeq);
			parcial = dao.listaCthSeqPorCthConteudoErro(erro, arrCthSeq);
			result.addAll(parcial);
		}

		return result;
	}

	/**
	 * ORADB: <code>FATC_VER_CTH_ESP_CIR</code>
	 * @param cthSeq
	 * @return
	 */
	protected List<Integer> obterListaEspCirurgia(final List<Integer> listaCthSeq) {

		List<Integer> result = null;

		result = this.obterListaCthSeqPorErro(listaCthSeq, MAGIC_STRING_FAT_LOG_ERRO_ESP_CIR_EQ_CONTA_COM_SSM);

		return result;
	}

	/**
	 * ORADB: <code>FATC_VER_CTH_FORA_ESTADO</code>
	 * @param cthSeq
	 * @return
	 */
	protected List<Integer> obterListaForaEstado(final List<Integer> listaCthSeq) {

		List<Integer> result = null;

		result = this.obterListaCthSeqPorErro(listaCthSeq, MAGIC_STRING_FAT_LOG_ERRO_FORA_EST_EQ_PACIENTE_FORA_DO_ESTADO);

		return result;
	}

	protected List<Integer> obterListaCobraAih(final List<Integer> listaPhiSeq) {

		List<Integer> result = null;
		List<Integer> parcial = null;
		List<List<Integer>> fatiado = null;
		ContaHospitalarRN cthRn = null;
		Integer[] arrPhiSeq = null;

		cthRn = this.getContaHospitalarRN();
		result = new LinkedList<Integer>();
		fatiado = separarListaEmListasDeTamMax(listaPhiSeq, MAGIC_INT_QTD_MAXIMA_SQL_IN_EQ_999);
		for (List<Integer> lstParcial : fatiado) {
			arrPhiSeq = new Integer[lstParcial.size()];
			arrPhiSeq = lstParcial.toArray(arrPhiSeq);
			try {
				parcial = cthRn.fatcVerCaractListaPhiQrInt(MAGIC_SHORT_CNV_CODIGO_EQ_1, MAGIC_BYTE_CSP_SEQ_EQ_1, MAGIC_STRING_CARACTERISTICA_EQ_COBRA_EM_AIH,
						arrPhiSeq);
				result.addAll(parcial);
			} catch (BaseException e) {
				result = Collections.emptyList();
				break;
			}
		}

		return result;
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	protected void ajustarListaVoComPropDerivadas(final List<BuscaContaVO> listaVo)
			throws IOException {

		BigDecimal valorTotal = null;
		Boolean isCobraAih = null;
		Boolean isEspCir = null;
		Boolean isForaEstado = null;
		Boolean isDesdobrada = null;
		String fcfDesc = null;
		String statusSms = null;
		String iphSsmSol = null;
		String iphSsmReal = null;
		Integer cthSeq = null;
		Integer cmce = null;
		String msgErro = null;
		List<Integer> listaPhiSeq = null;
		List<Integer> listaCthSeq = null;
		List<Integer> listaCobraAih = null;
		List<Integer> listaEspCirurgia = null;
		List<Integer> listaForaEst = null;
		List<Integer> listaDesd = null;
		Map<Integer, ParSsmSolicRealizVO> mapaSsm = null;
		Map<Integer, ParSsmSolicRealizVO> mapaSsmComplex = null;
		Map<Integer, CmceCthSeqVO> mapaCmce = null;
		Map<Integer, String> mapaMsgErro = null;
		ParSsmSolicRealizVO ssmVo = null;
		ParSsmSolicRealizVO ssmComplexVo = null;
		long tCobra = 0l;
		long tEsp = 0l;
		long tEst = 0l;
		long tDesd = 0l;
		long tSms = 0l;
		long tSmsCmplx = 0l;
		long tSet = 0l;
		long ini = 0l;
		long fim = 0l;
		long[] vals = null;

		listaCthSeq = new LinkedList<Integer>();
		listaPhiSeq = new LinkedList<Integer>();
		for (BuscaContaVO vo : listaVo) {
			listaCthSeq.add(vo.getCthSeq());
			listaPhiSeq.add(vo.getPhiSeq());
		}
		ini = System.currentTimeMillis();
		listaCobraAih = this.obterListaCobraAih(listaPhiSeq);
		fim = System.currentTimeMillis();
		tCobra += fim - ini;
		ini = fim;
		listaEspCirurgia = this.obterListaEspCirurgia(listaCthSeq);
		fim = System.currentTimeMillis();
		tEsp += fim - ini;
		ini = fim;
		listaForaEst = this.obterListaForaEstado(listaCthSeq);
		fim = System.currentTimeMillis();
		tEst += fim - ini;
		ini = fim;
		listaDesd = this.obterListaCthSeqContaDesdobrada(listaCthSeq);
		fim = System.currentTimeMillis();
		tDesd += fim - ini;
		ini = fim;
		mapaSsm = this.obterMapaCthSeqSsm(listaCthSeq);
		fim = System.currentTimeMillis();
		tSms += fim - ini;
		ini = fim;
		mapaCmce = this.obterMapaCthSeqCmce(listaCthSeq);
		fim = System.currentTimeMillis();
		tSms += fim - ini;
		ini = fim;
		mapaMsgErro = this.obterMapaCthSeqMsgErro(listaCthSeq);
		fim = System.currentTimeMillis();
		tSms += fim - ini;
		ini = fim;
		mapaSsmComplex = this.obterMapaCthSeqSsmComplex(listaCthSeq);
		fim = System.currentTimeMillis();
		tSmsCmplx += fim - ini;
		ini = fim;
		cthSeq = null;
		for (BuscaContaVO vo : listaVo) {
			if (!vo.getCthSeq().equals(cthSeq)) {
				cthSeq = vo.getCthSeq();
				valorTotal = obterValorTotal(vo);
				isCobraAih = listaCobraAih.contains(vo.getPhiSeq())
						? Boolean.TRUE
						: Boolean.FALSE;
				isEspCir = listaEspCirurgia.contains(cthSeq)
						? Boolean.TRUE
						: null;
				isForaEstado = listaForaEst.contains(cthSeq)
						? Boolean.TRUE
						: null;
				isDesdobrada = listaDesd.contains(cthSeq)
						? Boolean.TRUE
						: Boolean.FALSE;
				ssmComplexVo = mapaSsmComplex.get(vo.getCthSeq());
				if ("S".equals(vo.getRealizadoSolicitado())){
					fcfDesc = ssmComplexVo.getSsmStrSolicitado();
				} else {
					fcfDesc = ssmComplexVo.getSsmStrRealizado();
				}
				statusSms = this.obterStatuSms(vo.getIndEnviadoSms(), vo.getIndAutorizadoSms());
				ssmVo = mapaSsm.get(vo.getCthSeq());
				iphSsmSol = ssmVo.getSsmStrSolicitado();
				iphSsmReal = ssmVo.getSsmStrRealizado();
				if (mapaCmce != null
						&& mapaCmce.get(cthSeq) != null
						&& mapaCmce.get(cthSeq).getConCodCentral() != null
						&& vo.getIndSituacao().equals(DominioSituacaoConta.E)){
					cmce = mapaCmce.get(cthSeq).getConCodCentral();
				}else {
					cmce = Integer.valueOf(0);
				}
				if (mapaMsgErro != null){
					msgErro = mapaMsgErro.get(cthSeq);
				}else {
					msgErro = null;
				}
			}
			vo.setCmce(cmce);
			vo.setMsgErro(msgErro);
			vo.setCaracterInternacao(buscarCaracterInternacao(vo));
			vo.setValorTotal(valorTotal);
			vo.setIsCobraAih(isCobraAih);
			vo.setIsEspCir(isEspCir);
			vo.setIsForaEstado(isForaEstado);
			vo.setIsDesdobrada(isDesdobrada);
			vo.setFcfDesc(fcfDesc);
			vo.setStatusSms(statusSms);
			vo.setIphSsmSol(iphSsmSol);
			vo.setIphSsmReal(iphSsmReal);
		}
		fim = System.currentTimeMillis();
		tSet += fim - ini;
		vals = new long[] {
				tCobra / 1000,
				tEsp / 1000,
				tEst / 1000,
				tDesd / 1000,
				tSms / 1000,
				tSmsCmplx / 1000,
				tSet / 1000,
		};
		this.logFile(FaturamentoDebugCode.ARQ_CSV_AJUSTE_ENTIDADES_STATS, Arrays.toString(vals));
	}

	private Map<Integer, String> obterMapaCthSeqMsgErro(
			List<Integer> listaCthSeq) {
		Map<Integer, String> result = null;
		Map<Integer, String> parcial = null;
		List<List<Integer>> fatiado = null;
		FaturamentoRN fatRn = null;

		result = new HashMap<Integer, String>();
		fatRn = this.getFaturamentoRN();
		fatiado = separarListaEmListasDeTamMax(listaCthSeq, MAGIC_INT_QTD_MAXIMA_SQL_IN_EQ_999);
		for (List<Integer> lstParcial : fatiado) {
			parcial = fatRn.listarMsgErroParaListaCthSeq(lstParcial);
			result.putAll(parcial);
		}

		return result;
	}

	private String buscarCaracterInternacao(BuscaContaVO vo) {
		return Integer.valueOf("2").equals(vo.getTciSeq()) ? "ELETIVA" : Integer.valueOf("3").equals(vo.getTciSeq()) ? "URGENCIA" : "   ";
	}

	private Map<Integer, CmceCthSeqVO> obterMapaCthSeqCmce(List<Integer> listaCthSeq) {
		Map<Integer, CmceCthSeqVO> result = null;
		Map<Integer, CmceCthSeqVO> parcial = null;
		List<List<Integer>> fatiado = null;
		FaturamentoRN fatRn = null;

		result = new HashMap<Integer, CmceCthSeqVO>();
		fatRn = this.getFaturamentoRN();
		fatiado = separarListaEmListasDeTamMax(listaCthSeq, MAGIC_INT_QTD_MAXIMA_SQL_IN_EQ_999);
		for (List<Integer> lstParcial : fatiado) {
			parcial = fatRn.listarCmceParaListaCthSeq(lstParcial, MAGIC_BYTE_CSP_SEQ_EQ_1);
			result.putAll(parcial);
		}

		return result;
	}
	
	/**
	 * ORADB: <code>FATF_ARQ_TXT_INT.BUSCA_CONTA.C_CTH_PER</code>
	 * @param arquivo TODO
	 * @throws IOException 
	 */
	protected List<BuscaContaVO> obterContasPeriodo(final String arquivo)
			throws IOException {

		List<BuscaContaVO> result = null;
		List<BuscaContaVO> listaVoInt = null;
		List<BuscaContaVO> listaVoDcs = null;
		long stats = 0l;

		stats = System.currentTimeMillis();
		result = new LinkedList<BuscaContaVO>();
		//INT
		listaVoInt = this.obterListaVoInt();
		result.addAll(listaVoInt);
		//DCS
		listaVoDcs = this.obterListaVoDcs();
		// leito
		for (BuscaContaVO vo : listaVoDcs) {
			if (vo.getLeitoID() == null) {
				vo.setLeitoID(MAGIC_STRING_LEITO_SEM_INTERNACAO_EQ_S_INT);
			}
		}
		result.addAll(listaVoDcs);
		//sort
		Collections.sort(result, new Comparator<BuscaContaVO>() {

			@Override
			public int compare(final BuscaContaVO o1, final BuscaContaVO o2) {

				return o1.getCthSeq().intValue() - o2.getCthSeq().intValue();
			}
		});
		stats = System.currentTimeMillis() - stats;
		this.logFile(FaturamentoDebugCode.ARQ_CSV_BUSCA_ENTIDADES, arquivo, Long.valueOf(stats), Long.valueOf(stats / 1000));
		stats = System.currentTimeMillis();
		this.ajustarListaVoComPropDerivadas(result);
		stats = System.currentTimeMillis() - stats;
		this.logFile(FaturamentoDebugCode.ARQ_CSV_AJUSTE_ENTIDADES, arquivo, Long.valueOf(stats), Long.valueOf(stats / 1000));

		return result;
	}

	protected static String obterPrefixoNomeArquivo(final Date data) {

		String result = null;
		String prefix = null;

		//v_arquivo := 'C:/AGH_FAT/CTH_'||TO_CHAR(SYSDATE,'DDMMYYYY')||'.TXT';
		prefix = MAGIC_STRING_PREFIXO_ARQ_CSV_EQ_CTH;
		result = String.format("%1$s%2$td%2$tm%2$tY-", prefix, data);

		return result;
	}

	public GeracaoArquivoTextoInternacaoBean() {

		super();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	//@TransactionTimeout(MAGIC_INT_TIMEOUT_TRANSACTION_EQ_1DIA)
	public ArquivoURINomeQtdVO gerarArquivoTextoContasPeriodo()
			throws BaseException {

		ArquivoURINomeQtdVO result = null;
		URI uriCsv = null;
		List<BuscaContaVO> listaVo = null;
		GeracaoArquivoCsvRN geraRn = null;
		String prefixo = null;
		String nomeCsv = null;

		try {
			prefixo = obterPrefixoNomeArquivo(new Date());
			this.logFile(FaturamentoDebugCode.ARQ_CSV_INI, prefixo);
			listaVo = this.obterContasPeriodo(prefixo);
			geraRn = this.getGeracaoArquivoCsvRN();
			uriCsv = geraRn.gerarDadosEmArquivo(listaVo, prefixo);
			nomeCsv = GeracaoArquivoZip.obterNomeArquivo(uriCsv, true);
			result = new ArquivoURINomeQtdVO(uriCsv, nomeCsv, listaVo.size(), 1);
		} catch (IOException e) {
			this.logError("Erro gerando arquivo", e);
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ARQ_ERRO_FISICO);
		}

		return result;
	}
}
