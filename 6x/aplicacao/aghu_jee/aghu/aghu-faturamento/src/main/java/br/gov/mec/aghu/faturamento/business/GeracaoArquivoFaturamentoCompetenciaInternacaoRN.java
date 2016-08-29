package br.gov.mec.aghu.faturamento.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoDebugCode;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelperSisaih01;
import br.gov.mec.aghu.faturamento.vo.ArquivoSUSVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaContaVO;
import br.gov.mec.aghu.faturamento.vo.CursorCAtosMedVO;
import br.gov.mec.aghu.faturamento.vo.CursorCAtosProtVO;
import br.gov.mec.aghu.faturamento.vo.CursorCBuscaRegCivilVO;
import br.gov.mec.aghu.faturamento.vo.CursorCEAIVO;
import br.gov.mec.aghu.faturamento.vo.EvtGeraAaqParcialAihGeralVO;
import br.gov.mec.aghu.faturamento.vo.EvtGeraAaqParcialAihRegCivilVO.AamPacVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoAihId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * ORADB: <code>FATF_ATU_COMP_INT</code>
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta","PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength", "PMD.AtributoEmSeamContextManager"})
@Stateless
public class GeracaoArquivoFaturamentoCompetenciaInternacaoRN
		extends
		GeracaoArquivoFaturamentoCompetenciaInternacaoRN1 {


@EJB
private GeracaoArquivoSusRN geracaoArquivoSusRN;

@EJB
private FaturamentoFatkCpeRN faturamentoFatkCpeRN;

@Inject
private AipPacientesDAO pacienteDAO;

	protected static final String MAGIC_STRING_FCF_CODIGO_EQ_MDO = "MDO";
	protected static final String MAGIC_STRING_FCF_CODIGO_EQ_ALT = "ALT";
	protected static final String MAGIC_STRING_FCF_CODIGO_EQ_EST = "EST";
	private static final Integer SEQP_PARA_CURSOR_C_EAI_INTEGER_EQ_2 = Integer.valueOf(2);
	/**
	 * 
	 */
	private static final long serialVersionUID = -4594889926138542711L;

	private Map<String, List<EvtGeraAaqParcialAihGeralVO>> arquivoConteudo = null;

	public enum DominioTipoArquivoDcih {

		ESTR(MAGIC_STRING_FCF_CODIGO_EQ_EST),
		ALTA(MAGIC_STRING_FCF_CODIGO_EQ_ALT),
		MEDI(MAGIC_STRING_FCF_CODIGO_EQ_MDO), ;

		private final String descricao;

		private DominioTipoArquivoDcih(final String descricao) {

			this.descricao = descricao;
		}

		public String getDescricao() {

			return this.descricao;
		}
	}

	public enum DominioTipoArquivoSaida {

		PARCIAL("parcial"),
		TOTAL("total"), ; // NO_UCD (unused code)

		private final String descricao;

		private DominioTipoArquivoSaida(final String descricao) {

			this.descricao = descricao;
		}

		public String getDescricao() {

			return this.descricao;
		}
	}

	protected Map<String, List<EvtGeraAaqParcialAihGeralVO>> getArquivoConteudo() {

		if (this.arquivoConteudo == null) {
			this.arquivoConteudo = new HashMap<String, List<EvtGeraAaqParcialAihGeralVO>>();
		}

		return this.arquivoConteudo;
	}

	protected void limparArquivoConteudo() {

		this.getArquivoConteudo().clear();
	}

	protected AttributeFormatPairRendererHelper getAttributeFormatPairRendererHelper() {

		return new AttributeFormatPairRendererHelperSisaih01();
	}

	protected GeracaoArquivoSusRN getGeracaoArquivoSusRN() {

		return geracaoArquivoSusRN;
	}


	protected static int obterOrdemDeFcf(final FatCaractFinanciamento fcf) {

		int result = 0;

		result = 99;
		if (fcf != null) {
			if (MAGIC_STRING_FCF_CODIGO_EQ_EST.equals(fcf.getCodigo())) {
				result = 1;
			} else if (MAGIC_STRING_FCF_CODIGO_EQ_ALT.equals(fcf.getCodigo())) {
				result = 2;
			} else if (MAGIC_STRING_FCF_CODIGO_EQ_MDO.equals(fcf.getCodigo())) {
				result = 3;
			}
		}

		return result;
	}


	protected static BigDecimal obterValorContaDeEai(final FatEspelhoAih eai) {

		BigDecimal result = null;
		List<BigDecimal> valorList = null;
		FatContasHospitalares cth = null;

		cth = eai.getContaHospitalar();
		valorList = new LinkedList<BigDecimal>();
		// nvl(VALOR_SH,0) 
		valorList.add(cth.getValorSh());
		// + nvl(VALOR_UTI,0) 		
		valorList.add(cth.getValorUti());
		// + nvl(VALOR_UTIE,0) 
		valorList.add(cth.getValorUtie());
		// + nvl(VALOR_SP,0) 
		valorList.add(cth.getValorSp());
		// + nvl(VALOR_ACOMP,0) 
		valorList.add(cth.getValorAcomp());
		// + nvl(VALOR_RN,0) 
		valorList.add(cth.getValorRn());
		// + nvl(VALOR_SADT,0) 
		valorList.add(cth.getValorSadt());
		// + nvl(VALOR_HEMAT,0) 
		valorList.add(cth.getValorHemat());
		// + nvl(VALOR_TRANSP,0) 
		valorList.add(cth.getValorTransp());
		// + nvl(VALOR_OPM,0) 
		valorList.add(cth.getValorOpm());
		// + nvl(VALOR_ANESTESISTA,0) 
		valorList.add(cth.getValorAnestesista());
		// + nvl(VALOR_PROCEDIMENTO,0) valor_conta,
		valorList.add(cth.getValorProcedimento());
		// soma
		result = BigDecimal.ZERO;
		for (BigDecimal val : valorList) {
			if (val != null) {
				result = result.add(val);
			}
		}

		return result;
	}

	protected Comparator<FatEspelhoAih> obterComparatorBuscaConta() {

		Comparator<FatEspelhoAih> result = null;

		result = new Comparator<FatEspelhoAih>() {

			@Override
			public int compare(final FatEspelhoAih o1, final FatEspelhoAih o2) {

				int result = 0;
				int fcf1 = 0;
				int fcf2 = 0;
				String indBco1 = null;
				String indBco2 = null;
				FatContasHospitalares cth1 = null;
				FatContasHospitalares cth2 = null;
				BigDecimal val1 = null;
				BigDecimal val2 = null;

				if ((o1 == null) && (o2 == null)) {
					result = 0;
				} else if ((o1 == null) && (o2 != null)) {
					result = -1;
				} else if ((o1 != null) && (o2 == null)) {
					result = 1;
				} else {
					// order by 
					fcf1 = obterOrdemDeFcf(GeracaoArquivoFaturamentoCompetenciaInternacaoRN.this.obterFcfDeEai(o1));
					fcf2 = obterOrdemDeFcf(GeracaoArquivoFaturamentoCompetenciaInternacaoRN.this.obterFcfDeEai(o2));
					// decode (fcf.codigo,'EST',1,'ALT',2,'MDO',3,99),
					result = fcf1 - fcf2;
					if (result == 0) {
						// eai.ind_bco_capac desc,
						indBco1 = o1.getIndBcoCapac();
						indBco2 = o2.getIndBcoCapac();
						if ((indBco1 == null) && (indBco2 == null)) {
							result = 0;
						} else if ((indBco1 == null) && (indBco2 != null)) {
							result = 1; //desc
						} else if ((indBco1 != null) && (indBco2 == null)) {
							result = -1; //desc
						} else {
							result = indBco2.compareTo(indBco1); //desc
						}
						if (result == 0) {
							// cth.dt_alta_administrativa,
							cth1 = o1.getContaHospitalar();
							cth2 = o2.getContaHospitalar();
							result = cth1.getDtAltaAdministrativa().compareTo(cth2.getDtAltaAdministrativa());
							if (result == 0) {
								// nvl(VALOR_SH,0) + nvl(VALOR_UTI,0) + nvl(VALOR_UTIE,0) +  nvl(VALOR_SP,0) + nvl(VALOR_ACOMP,0) + nvl(VALOR_RN,0) +  nvl(VALOR_SADT,0) + nvl(VALOR_HEMAT,0) + nvl(VALOR_TRANSP,0) + nvl(VALOR_OPM,0) + nvl(VALOR_ANESTESISTA,0) + nvl(VALOR_PROCEDIMENTO,0) desc;
								val1 = obterValorContaDeEai(o1);
								val2 = obterValorContaDeEai(o2);
								result = val2.compareTo(val1); //desc
							}
						}
					}
				}

				return result;
			}
		};

		return result;
	}
	
	protected List<FatEspelhoAih> obterListaFatEspelhoAihBuscaConta(final Short ano, final Byte mes, final Date dtHrInicio,
			final Date dataEncIni, final Date dataEncFinal) {

		final List<FatEspelhoAih> result;

		if ((dataEncIni == null) && (dataEncFinal == null)) {
			result = getFatEspelhoAihDAO().listarCompetenciaSeqp1DeInternacaoComContasAutorizadasEncerradas(ano, mes, dtHrInicio);
		} else {
			result = getFatEspelhoAihDAO().listarCompetenciaSeqp1DeInternacaoComContasAutorizadasEncerradasEntreDatas(ano, mes, dtHrInicio, dataEncIni, dataEncFinal);
		}
		
		if ((result != null) && !result.isEmpty()) {
			Comparator<FatEspelhoAih> comparator = this.obterComparatorBuscaConta();
			Collections.sort(result, comparator);
		}

		return result;
	}
	
	protected FatItensProcedHospitalar obterIphDeEai(final FatEspelhoAih eai) {

		FatItensProcedHospitalar result = null;
		FatItensProcedHospitalarId id = null;
		FatItensProcedHospitalarDAO dao = null;

		dao = this.getFatItensProcedHospitalarDAO();
		// and iph.pho_seq = eai.iph_pho_seq_realiz
		// and iph.seq = eai.iph_seq_realiz
		id = new FatItensProcedHospitalarId(eai.getIphPhoSeqRealiz(), eai.getIphSeqRealiz());
		result = dao.obterPorChavePrimaria(id);
		if (result != null) {
			// and iph.cod_tabela =  eai.iph_cod_sus_realiz
			if (!result.getCodTabela().equals(eai.getIphCodSusRealiz())) {
				result = null;
			}
		}

		return result;
	}

	protected FatCaractFinanciamento obterFcfDeEai(final FatEspelhoAih eai) {

		FatCaractFinanciamento result = null;
		FatItensProcedHospitalar iph = null;

		iph = this.obterIphDeEai(eai);
		if (iph != null) {
			result = iph.getFatCaracteristicaFinanciamento();
		}

		return result;
	}

	protected static String obterPrefixoNomeArquivo(
			final DominioTipoArquivoDcih tipoDcih,
			final Integer mes,
			final Integer ano,
			final String infix,
			final BigInteger codCnes) {

		//v_arquivo := v_diretorio || '\'||v_arquivo_dcih||' - '||v_capac_ant||to_char(p_cpe_mes,'00') || substr(to_char(p_cpe_ano),3,2) || '.TXT';
		StringBuffer prefix = new StringBuffer(tipoDcih.getDescricao());
		if (StringUtils.isNotBlank(infix)) {
			prefix.append(" - ").append(infix.trim());
		}
		String result = String.format("%1$s_%2$s %3$02d%4$02d", prefix.toString(), codCnes, mes, Integer.valueOf(ano.intValue() % 100));
		//String result = String.format("%1$s - %2$s %02d%02d", prefix.toString(), bcoCap, mes, Integer.valueOf(ano.intValue() % 100));
		
		//return result.replaceAll("[ ]", "_");
		return result;
	}

	protected static String obterPrefixoNomeArquivoSaida(
			final Byte mes,
			final Short ano,
			final DominioTipoArquivoSaida tipo) {

		String result = null;

		result = String.format("%1$02d%2$02d-$3s", mes, Integer.valueOf(ano.intValue() % 100), tipo.getDescricao());

		return result;
	}

	/**
	 * Cursor <code>C_BUSCA_PARTE11</code>
	 * @param cthSeq
	 * @return
	 */
	@SuppressWarnings("ucd")
	protected FatEspelhoAih obterEaiCont(final Integer cthSeq) {

		FatEspelhoAih result = null;
		FatEspelhoAihId id = null;
		FatEspelhoAihDAO dao = null;

		dao = this.getFatEspelhoAihDAO();
		id = new FatEspelhoAihId(cthSeq, SEQP_PARA_CURSOR_C_EAI_INTEGER_EQ_2);
		result = dao.obterPorChavePrimaria(id);

		return result;
	}

	/**
	 * ORADB: <code>C_ATOS_MED</code>
	 * @param cthSeq
	 * @return
	 */
	@SuppressWarnings("ucd")
	protected List<FatAtoMedicoAih> obterListaFatAtoMedicoAihAtosMed(final Integer cthSeq) {

		List<FatAtoMedicoAih> result = null;
		FatAtoMedicoAihDAO dao = null;

		dao = this.getFatAtoMedicoAihDAO();
		result = dao.listarPorCthOrdenadoPorSeqArqSus(cthSeq);

		return result;
	}

	/**
	 * ORADB: <code>C_ATOS_PROT</code>
	 * @param cthSeq
	 * @return
	 *  
	 */
	@SuppressWarnings("ucd")
	protected List<FatAtoMedicoAih> obterListaFatAtoMedicoAihAtosProt(final Integer cthSeq)
			throws ApplicationBusinessException {

		List<FatAtoMedicoAih> result = null;
		FatAtoMedicoAihDAO dao = null;
		AghParametros param = null;
		BigDecimal vlrNum = null;
		Short fogSgrGrpSeq = null;

		param = this.buscarAghParametro(AghuParametrosEnum.P_GRUPO_OPM);
		vlrNum = param.getVlrNumerico();
		fogSgrGrpSeq = Short.valueOf(vlrNum.shortValue());
		dao = this.getFatAtoMedicoAihDAO();
		result = dao.listarPorCthSeqIphFogSgrGrpSeqOrdenadoPorSeqp(cthSeq, fogSgrGrpSeq);

		return result;
	}

	protected List<FatAtoMedicoAih> obterListaAamPorCthSeqIphCodSus(
			final Long iphCodSus,
			final Integer cthSeq) {

		List<FatAtoMedicoAih> result = null;
		FatAtoMedicoAihDAO dao = null;

		dao = this.getFatAtoMedicoAihDAO();
		result = dao.listarPorIphCodSusCthSeq(iphCodSus, cthSeq);

		return result;
	}

	protected List<McoRecemNascidos> obterListaRecemNascidos(
			Integer gsoPacCodigo, Short gsoSeqp) {
		return this.getPerinatologiaFacade().listarPorGestacao(gsoPacCodigo,
				gsoSeqp);
	}

	protected List<AipPacientes> obterListaPacientesFilhosDeAtendimento(
			final AghAtendimentos atendimento) {

		List<AipPacientes> result = null;
		List<McoRecemNascidos> listaRNasc = null;

		// and coi.int_seq = atd.int_seq  
		if (atendimento != null) {
			// and atd.gso_pac_codigo = rna.gso_pac_codigo
			// and atd.gso_seqp = rna.gso_seqp 
			
			//TODO: Tarefa #14529 - Passagem dos parâmetros gsoPacCodigo e gsoSeqp ao invés do objeto gestacao. Até
			//a implantação do sistema de perinatologia os HUs (exceto o HCPA) não deverão ter registros na tabela MCO_GESTACOES
			//porém existirá a chave para a mesma na tabela AGH_ATENDIMENTOS (a FK foi temporariamente dropada nos HUs).
			if (atendimento.getGsoPacCodigo() != null) {
				listaRNasc = obterListaRecemNascidos(atendimento.getGsoPacCodigo(), atendimento.getGsoSeqp());
				if ((listaRNasc != null) && !listaRNasc.isEmpty()) {
					result = new LinkedList<AipPacientes>();
					for (McoRecemNascidos rn : listaRNasc) {
						result.add(pacienteDAO.obterPorChavePrimaria(rn.getPaciente().getCodigo()));
					}
				}
			}
		}

		return result;
	}

	protected List<FatContasInternacao> obterListaCoiPorCthSeq(
			final Integer cthSeq,
			final Integer phiSeq) {

		List<FatContasInternacao> result = null;
		FatContasInternacaoDAO dao = null;

		dao = this.getFatContasInternacaoDAO();
		result = dao.listarPorCthSeqPhiSeq(cthSeq, phiSeq);

		return result;
	}

	protected Long obterIphCodSusIncentivoCivil()
			throws ApplicationBusinessException {

		Long result = null;
		AghParametros param = null;

		param = this.buscarAghParametro(AghuParametrosEnum.P_IPH_COD_SUS_INCENTIVO_CIVIL);
		result = Long.valueOf(param.getVlrNumerico().longValue());

		return result;
	}

	protected Integer obterPhiIncentivoCivil()
			throws ApplicationBusinessException {

		Integer result = null;
		AghParametros param = null;

		param = this.buscarAghParametro(AghuParametrosEnum.P_PHI_INCENTIVO_CIVIL);
		result = Integer.valueOf(param.getVlrNumerico().intValue());

		return result;
	}

	/**
	 * ORADB: <code>c_busca_reg_civil</code>
	 * @param cthSeq
	 * @return
	 *  
	 */
	@SuppressWarnings("ucd")
	protected List<AamPacVO> obterListaRegistroCivil(final Integer cthSeq)
			throws ApplicationBusinessException {

		List<AamPacVO> result = null;
		Long iphCodSus = null;
		Integer phiSeq = null;
		List<FatContasInternacao> listaCoi = null;
		List<FatAtoMedicoAih> listaAam = null;
		List<AipPacientes> listaFilhosPac = null;
		AamPacVO vo = null;

		iphCodSus = this.obterIphCodSusIncentivoCivil();
		// coi.cth_seq = p_cth_seq 
		// and ich.phi_seq = 28619
		phiSeq = this.obterPhiIncentivoCivil();
		listaCoi = this.obterListaCoiPorCthSeq(cthSeq, phiSeq);
		if ((listaCoi != null) && !listaCoi.isEmpty()) {
			result = new LinkedList<AamPacVO>();
			for (FatContasInternacao coi : listaCoi) {
				// and coi.int_seq = atd.int_seq  
				// and atd.gso_pac_codigo = rna.gso_pac_codigo
				// and atd.gso_seqp = rna.gso_seqp 
				// and pds.pac_codigo = rna.pac_codigo
				// and pds.pac_codigo = pac.codigo
				listaFilhosPac = this.obterListaPacientesFilhosDeAtendimento(coi.getInternacao().getAtendimento());
				if ((listaFilhosPac != null) && !listaFilhosPac.isEmpty()) {
					for (AipPacientes pac : listaFilhosPac) {
						// and aam.eai_cth_seq =  coi.cth_seq
						// and aam.iph_cod_sus = 801010047	;
						listaAam = this.obterListaAamPorCthSeqIphCodSus(iphCodSus, cthSeq);
						if ((listaAam != null) && !listaAam.isEmpty()) {
							for (FatAtoMedicoAih aam : listaAam) {
								vo = new AamPacVO(aam, pac);
								result.add(vo);
							}
						}
					}
				}
			}
			if (result.isEmpty()) {
				result = null;
			}
		}

		return result;
	}


	/**
	 * ORADB: <code>AIPC_GET_CARTAO_SUS</code>
	 * @param cthSeq TODO
	 * @param prontuarioPaciente
	 * @param nomePac TODO
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("ucd")
	protected BigInteger obterCartaoSus(final Integer cthSeq, final Integer prontuarioPaciente, final String nomePac)
			throws IOException {

		BigInteger result = null;
		AipPacientes paciente = null;

		paciente = getPacienteFacade().obterPacientePorProntuario(prontuarioPaciente);
		if (paciente == null) {
			this.logFile(FaturamentoDebugCode.ARQ_SUS_PAC_SEM_PRONTUARIO, cthSeq, nomePac);
		} else {
			result = paciente.getNroCartaoSaude();
			if (result == null) {
				this.logFile(FaturamentoDebugCode.ARQ_SUS_PAC_SEM_CNS, cthSeq, prontuarioPaciente);
			}
		}

		return result;
	}

	public GeracaoArquivoFaturamentoCompetenciaInternacaoRN() {

		super();
	}
	
	protected final static String BANCO = "Banco";
	

	@SuppressWarnings({"PMD.NPathComplexity"})
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<ArquivoSUSVO> gerarArquivoFaturamentoParcialSUSNew( final FatCompetencia competencia, 
																	final Date dataEncIni, final Date dataEncFinal) 
																			throws ApplicationBusinessException, IOException {

		super.inicializar();
		//super.commitTransaction();
		//super.beginTransaction(TRANSACTION_TIMEOUT_1_HORA);
		
		cBuscaConta = getFatEspelhoAihDAO().obterResultadoCursorBuscaConta( competencia.getId().getDtHrInicio(), 
																				 		competencia.getId().getMes(), 
																				 		competencia.getId().getAno(), 
																				 		Boolean.TRUE, 
																				 		DominioSituacaoConta.E, 
																				 		dataEncIni, 
																				 		dataEncFinal,
																				 		competencia.getId().getModulo());
		if(cBuscaConta.isEmpty()){
			getFaturamentoFacade().clearSemFlush();
			throw new ApplicationBusinessException(FaturamentoExceptionCode.PERIODO_SEM_CONTAS_PARA_ARQUIVO);
		}
		
		for (CursorBuscaContaVO rBuscaConta : cBuscaConta) {
			
			switch (rBuscaConta.getOrdem()) {	
				case 1:  vArquivoDCIH = DominioTipoArquivoDcih.ESTR; break;
				case 2:  vArquivoDCIH = DominioTipoArquivoDcih.ALTA; break;
				default: vArquivoDCIH = DominioTipoArquivoDcih.MEDI; break;
			}
			
			if( !CoreUtil.igual(vArquivoDCIH, CoreUtil.nvl(vArquivoDCIHAnt, 0))){
				
				//TODO rever....
				if(vArquivoDCIHAnt != null){
					this.logFile(FaturamentoDebugCode.ARQ_SUS_FECHADO, vArquivoDCIH, vArquivo);
					// --      MESSAGE ('fechou '||V_ARQUIVO);PAUSE;
			        // text_io.fclose(sus_arq)
				}
				
				vArquivoDCIHAnt = vArquivoDCIH;
				vCapacAnt = (String) CoreUtil.nvl(rBuscaConta.getIndBcoCapac(), BANCO);
				
				vArquivo = obterPrefixoNomeArquivo( vArquivoDCIH, 
													competencia.getId().getMes(), 
													competencia.getId().getAno(), null, vCodCNES);
				
				if(arq.getLinhas().size() > 0){
					arqs.add(arq);
				}
				
				arq = new ArquivoSUSVO();
				arq.setNomeArquivo(vArquivo);				
			}
			
			/**
			 * Não gera mais o arquivo de complementação quando o teto é ultrapassado.
			 */
			/*if (DominioTipoArquivoDcih.ALTA.equals(vArquivoDCIH)) {

				// caso extraordinario em que um unico espelho supera o teto
				if (rBuscaConta.getValorConta().compareTo(tetoSus) > 0) {
					throw new ApplicationBusinessException(FaturamentoExceptionCode.ARQ_SUS_TETO_FATURAMENTO_POR_ESPELHO_SUPERADO, tetoSus);
				}
				
				valorTotal = valorTotal.add(rBuscaConta.getValorConta());

				// caso o teto seja ultrapassado, trocar de arquivo
				if (valorTotal.compareTo(tetoSus) > 0) {
					if(vPrimeira == 0  ||
							!CoreUtil.igual(CoreUtil.nvl(vCapacAnt, BANCO), CoreUtil.nvl(rBuscaConta.getIndBcoCapac(), BANCO))){
						
						vPrimeira = 1;
						
						// text_io.fclose(sus_arq);
						vCapacAnt = (String) CoreUtil.nvl(rBuscaConta.getIndBcoCapac(), BANCO);
						
						// otem o nome de arquivo (complementar) para inclusao das proximas entradas
						// so adicionar o numero de sequencia se este passar do primeiro arquivo complementar
				        // v_arquivo   := v_diretorio || '\'||v_arquivo_dcih||'-compl'|| ' - '||v_capac_ant|| TO_CHAR(p_cpe_mes,'00') || SUBSTR(TO_CHAR(p_cpe_ano),3,2) || '.TXT';
						String infix = "compl" + (seqComplArqSus > 0 ? String.valueOf(seqComplArqSus + 1) : "");
						vArquivo = obterPrefixoNomeArquivo( vArquivoDCIH, 
															competencia.getId().getMes(), 
															competencia.getId().getAno(), infix, vCodCNES);
						
						if(arq.getLinhas().size() > 0){
							arqs.add(arq);
						}
						
						arq = new ArquivoSUSVO();
						arq.setNomeArquivo(vArquivo);
						
						// sus_arq     := TEXT_IO.FOPEN(v_arquivo, 'W');
				        // IF NOT text_io.is_open(sus_arq) THEN
				        //  QMS$HANDLE_OFG45_MESSAGES('E', true, '', 'Erro ao abrir arquivo na complementação alta - FATF_ATU_COMP_INT' );
				        // END IF;
					}
				}
			}*/
			
			ceais = getFatEspelhoAihDAO().obterResultadoCursorCEAI( rBuscaConta.getCthSeq(), Long.valueOf(buscarVlrTextoAghParametro(AghuParametrosEnum.P_CPF_DOUTOR)), buscarVlrNumericoAghParametro(AghuParametrosEnum.P_CODIGO_MUNICIPIO_HOSPITAL).toString() , 
					buscarVlrTextoAghParametro(AghuParametrosEnum.P_ORGAO_LOC_REC), vCodCNES.intValue()
													   		 );
			for (CursorCEAIVO cursorCEAIVO : ceais) {
				if(cursorCEAIVO.getPacTelefone() != null){
					if(cursorCEAIVO.getPacTelefone().length() > 11){
						cursorCEAIVO.setPacTelefone(StringUtil.trunc(cursorCEAIVO.getPacTelefone(), false, 11L));
					}else if(cursorCEAIVO.getPacTelefone().length() < 11){
						while(cursorCEAIVO.getPacTelefone().length() < 11){
							cursorCEAIVO.setPacTelefone(cursorCEAIVO.getPacTelefone()+"0");
						}
					}
				}
				modalidade = getFaturamentoRN().buscaModalidade(cursorCEAIVO.getIphPhoSeqRealiz(), 
								   	   			   cursorCEAIVO.getIphSeqRealiz(),
								   	   			   cursorCEAIVO.getDataInternacaoTipoDate(), 
								   	   			   cursorCEAIVO.getDataSaidaTipoDate()
								   	   			 );
				
				cursorCEAIVO.setModalidade( modalidade == null ?  "0" : modalidade.toString() );

				// Efetua a formatação dos valores, aplicando os leftPad e RightPad
				final LayoutArquivoSusVO laSusVO = getLayoutArquivoSusRN().aplicarLayout(cursorCEAIVO);
				
		        vIndice = 0;

		        final List<CursorCAtosMedVO> cAtosMed = getFatEspelhoAihDAO().obterResultadoCursorCAtosMed( cursorCEAIVO.getCthSeq(), 
						        																 	   vCodCNES.toString(), 
						        																       fogSgrGrpSeq);
		        for (CursorCAtosMedVO rAtosMed : cAtosMed) {
		        	super.processaAtosMedicos(cursorCEAIVO, rAtosMed, laSusVO);
				}
		        
		        // v_linha_atm := rpad(NVL(v_linha_atm,'0'),730,'0');
		        if(vLinhaAtm == null){
					vLinhaAtm = new StringBuilder();
					vLinhaAtm.append(StringUtils.rightPad(String.valueOf(0), 730));
					
				} else if(vLinhaAtm.length() < 730) {
					String aux = StringUtils.rightPad(vLinhaAtm.toString(), 730, '0');
					vLinhaAtm = new StringBuilder();
					vLinhaAtm.append(aux);
				}

				vLinhaArq = laSusVO.getvParte10() + 
							laSusVO.getvParte11() + 
							laSusVO.getvParte12() + 
							laSusVO.getvParte1()  +
							vLinhaAtm.toString()  +
							laSusVO.getvParte3();
				
				// text_io.put_line(sus_arq,v_linha_arq);
				arq.addLinha(vLinhaArq);
				
				// Inicio Marina 09/10/2008
				StringBuilder vLinhaCivil = new StringBuilder();
				
				List<CursorCBuscaRegCivilVO> cBuscaRegCivil = getFatEspelhoAihDAO().obterResultadoCursorCBuscaRegCivil(rBuscaConta.getCthSeq());

				for (CursorCBuscaRegCivilVO rBuscaRegCivil : cBuscaRegCivil) {
					final LayoutArquivoSusVO laSusVORBuscaRegCivil = getLayoutArquivoSusRN().aplicarLayout(rBuscaRegCivil);
					vLinhaCivil.append(laSusVORBuscaRegCivil.getRegistroCivil());
				}
				
				if(vLinhaCivil.length() > 0){
			        // v_linha_civil  := rpad(NVL(v_linha_civil,'0'),1033,'0');
					if(vLinhaCivil.length() < 1033){
						String aux = vLinhaCivil.toString();
						vLinhaCivil = new StringBuilder();
						vLinhaCivil.append(StringUtils.rightPad(aux, 1033,'0'));
					}
					//-- Ney 23/01/2015
					vLinhaArq = laSusVO.getvParte10()  + 
								"04" +
								laSusVO.getvParte12()  +
								vLinhaCivil.toString() +
								StringUtils.rightPad(String.valueOf(0), 662/*462*/, '0');
					// -- Fim Ney 23/01/2015  
				   // text_io.put_line(sus_arq,v_linha_arq);
					arq.addLinha(vLinhaArq);
				}
				
				// Fim Marina 09/10/2008
				StringBuilder vLinhaProt = new StringBuilder();
				final List<CursorCAtosProtVO> cAtosProt = getFatEspelhoAihDAO().obterResultadoCursorCAtosProt( rBuscaConta.getCthSeq(), fogSgrGrpSeq);
				
				for (CursorCAtosProtVO rAtosProt : cAtosProt) {
					final LayoutArquivoSusVO laSusVORBuscaRegCivil = getLayoutArquivoSusRN().aplicarLayout(rAtosProt);
					vLinhaProt.append(laSusVORBuscaRegCivil.getAtosProt());
				}

				if(vLinhaProt.length() > 0){
					// v_linha_prot  := rpad(NVL(v_linha_prot,'0'),1211,'0');
					// Ney 23/01/2015
					if(vLinhaProt.length() < 1695/*1211*/){
						String aux = vLinhaProt.toString();
						vLinhaProt = new StringBuilder();
						vLinhaProt.append(StringUtils.rightPad(aux, 1695/*1211*/,'0'));
					}
					
					vLinhaArq = laSusVO.getvParte10()  + 
								"07" +
								laSusVO.getvParte12()  +
								vLinhaProt.toString() /*+
								StringUtils.rightPad(String.valueOf(0), 284, '0')*/;
					// Fim Ney 23/01/2015
					//	text_io.put_line(sus_arq,v_linha_arq);
					arq.addLinha(vLinhaArq);
				}
				vLinhaArq  = null;
				vLinhaAtm  = new StringBuilder();
				vLinhaProt = new StringBuilder();
			}
		}
		
		//super.commitTransaction();
		
		if(arq.getLinhas().size() > 0 && !arqs.contains(arq)){
			arqs.add(arq);
		}

		return arqs;
	}

	protected FaturamentoFatkCpeRN getFaturamentoFatkCpeRN() {
		return faturamentoFatkCpeRN;
	}

	/**
	 * ORADB: <code>FATF_ATU_COMP_INT.EVT_GERA_ARQ_COMPETENCIA</code>
	 * 
	 * Foi ajustado para apenas colocar a competencia em manutenção ==> nao deve mais gerar arquivo.
	 * 
	 * @param dataFimVinculoServidor 
	 * @throws BaseException 
	 * 
	 * @throws IOException
	 */
    public void gerarCompetenciaEmManutencao(final FatCompetencia competencia, String nomeMicrocomputador, final Date dataFimVinculoServidor)
    		throws ApplicationBusinessException {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		try {
			//super.commitTransaction();
			//super.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
			//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
			
			// ETB 04112008 - Incluído encerramento competência
			// vai atualizar competencia com dt fim e situ	acao = M e criar nova
			getFaturamentoFatkCpeRN().rnCpecAtuEncComp(DominioModuloCompetencia.INT.toString(),
														competencia.getId().getDtHrInicio(),
														competencia.getId().getMes(),
														competencia.getId().getAno(),
														competencia.getDtHrFim(),
														null, null);

			
		} catch (BaseException e) {
			faturamentoFacade.clearSemFlush();
			//super.rollbackTransaction();
			throw new ApplicationBusinessException(e.getCode());
		}
	}
}
