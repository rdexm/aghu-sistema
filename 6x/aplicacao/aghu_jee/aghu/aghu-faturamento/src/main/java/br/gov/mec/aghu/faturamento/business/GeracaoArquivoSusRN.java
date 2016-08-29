package br.gov.mec.aghu.faturamento.business;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoDebugCode;
import br.gov.mec.aghu.faturamento.business.exception.RegistroAihNormalSusPadrao;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.stringtemplate.GeradorRegistroSusArquivoSisaih01;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioCpfCnsCnpjCnes;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioGrauInstru;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioInEquipe;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioModIntern;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioSaidaUtineo;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioStGestrisco;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioStMudaproc;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioTipoDocPac;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioTpContracep;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.AbstractRegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihAcidenteTrabalho;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihComum;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihDadosOpm;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihDocumentoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihEnderecoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihEspecificacao;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihIdentificacaoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihInternacao;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihParto;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihProcedimentosSecundariosEspeciais;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihRegistroCivil;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.DefaultRegistroAihUtiNeonatal;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.RegistroAihOpmSusPadrao;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.RegistroAihRegCivilSusPadrao;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.AgrupamentoRegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.GeradorRegistroSus;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihAcidenteTrabalho;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihComum;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihDadosOpm;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihDocumentoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihEnderecoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihEspecificacao;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihIdentificacaoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihInternacao;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihNormalSus;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihOpmSus;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihParto;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihProcedimentosSecundariosEspeciais;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihRegCivilSus;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihRegistroCivil;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihUtiNeonatal;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;
import br.gov.mec.aghu.faturamento.vo.EvtGeraAaqParcialAihGeralVO;
import br.gov.mec.aghu.faturamento.vo.EvtGeraAaqParcialAihNormalVO;
import br.gov.mec.aghu.faturamento.vo.EvtGeraAaqParcialAihRegCivilVO;
import br.gov.mec.aghu.faturamento.vo.EvtGeraAaqParcialAihRegCivilVO.AamPacVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta", "PMD.AghuTooManyMethods","PMD.AtributoEmSeamContextManager"})
@Stateless
public class GeracaoArquivoSusRN extends AbstractGeracaoArquivoRN {


@EJB
private BuscaCpfCboResponsavelPorIchRN buscaCpfCboResponsavelPorIchRN;

	private static final String MAGIC_STRING_SEXO_EAI_EQ_1 = "1";
	private static final String MAGIC_STRING_SEXO_SUS_EQ_M = "M";
	private static final String MAGIC_STRING_SEXO_SUS_EQ_F = "F";
	protected static final String EXTENSAO_ARQUIVO_SUS_EQ_TXT = ".TXT";
	private static final int MAGIC_INT_LENGTH_REG_NASCIMENTO_EQ_30 = 30;
	protected static final Date MAGIC_DATE_TROCA_TIPO_REG_CIVIL_EQ_2010_01_04 = new GregorianCalendar(2010, Calendar.JANUARY, 4).getTime();
	private static final Integer MAGIC_INTEGER_EAI_SEQ_BUSCA_UTI_NEONATAL_EQ_1 = Integer.valueOf(1);
	protected static final Byte CODIGO_SUS_OBITO_BYTE_EQ_4 = Byte.valueOf((byte) 4);
	protected static final boolean IS_CHARSET_ISO = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = 674322050308040427L;
	//TODO migracao nao pode ter atributos.
	private final AttributeFormatPairRendererHelper renderer;
	
	
	public GeracaoArquivoSusRN() {
		super(new GeracaoArquivoLog());
		this.renderer = null;
	}
	
	//TODO migracao remover este construtor
	public GeracaoArquivoSusRN(
			final AttributeFormatPairRendererHelper renderer,
			final GeracaoArquivoLog logger) {

		super(logger);

		if (renderer == null) {
			throw new IllegalArgumentException("Parametro renderer nao informado!!!");
		}
		if (logger == null) {
			throw new IllegalArgumentException("Parametro logger nao informado!!!");
		}
		this.renderer = renderer;
		AbstractRegistroAih.DEFAULT_NULL_TO_ZERO_FLAG = true;
	}
	

	protected BuscaCpfCboResponsavelPorIchRN getBuscaCpfResponsavelPorIchRN() {

		return buscaCpfCboResponsavelPorIchRN;
	}

	protected GeradorRegistroSus getGeracaoRegistroSusArquivoSisaih01() {
		return new GeradorRegistroSusArquivoSisaih01(this.getFileLogger());
	}

	protected RegistroAihComum obterRegistroAihComum(
			final FatEspelhoAih eai,
			final Short tahSeq,
			final String orgLocRec,
			final BigInteger cnes,
			final Integer codMunicipio)
			throws IOException {

		RegistroAihComum result = null;
		Long nuLote = null;
		Date apresLote = null;
		Long nuAih = null;
		Short identAih = null;
		Byte especAih = null;

		if (eai.getDciCodigoDcih() != null) {
			nuLote = Long.valueOf(eai.getDciCodigoDcih());
		} else {
			this.logFile(FaturamentoDebugCode.ARQ_SUS_EAI_SEM_DCI_COD, eai.getContaHospitalar().getSeq(), eai.getPacProntuario());
		}
		apresLote = eai.getDciCpeDtHrInicio();
		if (apresLote != null) {
			apresLote = DateUtil.adicionaMeses(apresLote, Integer.valueOf(1));
		}
		nuAih = eai.getNumeroAih();
		identAih = tahSeq;
		especAih = eai.getEspecialidadeAih();
		result = new DefaultRegistroAihComum(this.renderer, nuLote, null, apresLote, null, orgLocRec, cnes, codMunicipio, nuAih, identAih, especAih);

		return result;
	}

	protected RegistroAihEspecificacao obterRegistroAihEspecificacao(
			final FatEspelhoAih eai,
			final Long cpfDirClinico,
			final Short modalidadeIntern) {

		RegistroAihEspecificacao result = null;
		DominioModIntern modIntern = null;
		Short seqAih5 = null;
		Long aihProx = null;
		Long aihAnt = null;
		Date dtEmissao = null;
		Date dtIntern = null;
		Date dtSaida = null;
		Long procSolicitado = null;
		DominioStMudaproc stMudaproc = null;
		Long procRealizado = null;
		Byte carIntern = null;
		Byte motSaida = null;
		DominioCpfCnsCnpjCnes identMedSol = null;
		Long docMedSol = null;
		DominioCpfCnsCnpjCnes identMedResp = null;
		Long docMedResp = null;
		DominioCpfCnsCnpjCnes identDirclinico = null;
		Long docDirclinico = null;
		DominioCpfCnsCnpjCnes identAutoriz = null;
		Long docAutoriz = null;
		String diagPrin = null;
		String diagSec = null;
		String diagObito = null;
		String codSolLib = null;

		modIntern = DominioModIntern.valueOf(modalidadeIntern.intValue());
		if (modIntern == null) {
			throw new IllegalArgumentException("Nao foi possivel converter a modalidade internacao para os valores aceitos pelo SUS: " + modalidadeIntern);
		}
		seqAih5 = eai.getNroSeqaih5();
		aihProx = eai.getNumeroAihPosterior();
		aihAnt = eai.getNumeroAihAnterior();
		dtEmissao = eai.getAihDthrEmissao();
		dtIntern = eai.getDataInternacao();
		dtSaida = eai.getDataSaida();
		if (eai.getIphCodSusSolic() == null) {
			throw new IllegalArgumentException("Parametro eai.iphCodSusSolic nao informado!!!");
		}
		procSolicitado = eai.getIphCodSusSolic();
		if (eai.getIphCodSusRealiz() == null) {
			throw new IllegalArgumentException("Parametro eai.iphCodSusRealiz nao informado!!!");
		}
		procRealizado = eai.getIphCodSusRealiz();
		stMudaproc = procSolicitado.equals(procRealizado)
				? DominioStMudaproc.NAO
				: DominioStMudaproc.SIM;
		carIntern = eai.getTciCodSus();
		if (eai.getMotivoCobranca() == null) {
			throw new IllegalArgumentException("Parametro eai.motivoCobranca nao informado!!!");
		}
		motSaida = Byte.valueOf(eai.getMotivoCobranca());
		identMedSol = DominioCpfCnsCnpjCnes.CPF;
		docMedSol = eai.getCpfMedicoSolicRespons();
		identMedResp = DominioCpfCnsCnpjCnes.CPF;
		docMedResp = eai.getCpfMedicoSolicRespons();
		identDirclinico = DominioCpfCnsCnpjCnes.CPF;
		docDirclinico = cpfDirClinico;
		identAutoriz = DominioCpfCnsCnpjCnes.CPF;
		docAutoriz = eai.getCpfMedicoAuditor();
		diagPrin = eai.getCidPrimario();
		diagSec = eai.getCidSecundario();
		if (CODIGO_SUS_OBITO_BYTE_EQ_4.equals(motSaida)) {
			diagObito = eai.getCidPrimario();
		}
		codSolLib = eai.getExclusaoCritica();
		result = new DefaultRegistroAihEspecificacao(this.renderer, modIntern, seqAih5, aihProx, aihAnt, dtEmissao, dtIntern, dtSaida, procSolicitado,
				stMudaproc, procRealizado, carIntern, motSaida, identMedSol, docMedSol, identMedResp, docMedResp, identDirclinico, docDirclinico, identAutoriz,
				docAutoriz, diagPrin, diagSec, null, diagObito, codSolLib);

		return result;
	}

	protected static String obterSexoPaciente(final FatEspelhoAih eai) {

		String result = null;
		String eaiSexoPac = null;

		eaiSexoPac = eai.getPacSexo();
		if (MAGIC_STRING_SEXO_EAI_EQ_1.equals(eaiSexoPac)) {
			result = MAGIC_STRING_SEXO_SUS_EQ_M;
		} else {
			result = MAGIC_STRING_SEXO_SUS_EQ_F;
		}

		return result;
	}

	protected RegistroAihIdentificacaoPaciente obterRegistroAihIdentificacaoPaciente(
			final FatEspelhoAih eai,
			final BigInteger numeroCNS) {

		RegistroAihIdentificacaoPaciente result = null;
		String nmPaciente = null;
		Date dtNascPac = null;
		String sexoPac = null;
		String racaCor = null;
		String nmMaePac = null;
		String nmRespPac = null;
		DominioTipoDocPac tpDocPac = null;
		BigInteger nuCns = null;
		Short nacPac = null;

		nmPaciente = eai.getPacNome();
		dtNascPac = eai.getPacDtNascimento();
		sexoPac = obterSexoPaciente(eai);
		racaCor = eai.getPacCor();
		nmMaePac = eai.getPacNomeMae();
		nmRespPac = eai.getNomeResponsavelPac();
		if (eai.getIndDocPac() == null) {
			tpDocPac = DominioTipoDocPac.IGNORADO;
		} else {
			tpDocPac = DominioTipoDocPac.valueOf(eai.getIndDocPac().intValue());
		}
		
		final Integer etniaIndigena;
		final AipPacientes pac = getPacienteFacade().obterPacientePorProntuario(eai.getPacProntuario());
		
		// #11584 Se CAMPO RACA/COR = '05' INDIGENA: 
		// Preencher com os codigos da PORTARIA SAS 508 DE 28/09/2010
		if(pac != null && pac.getEtnia() != null && DominioCor.I.equals(pac.getCor())){
			etniaIndigena = pac.getEtnia().getId();
			
		} else {
			etniaIndigena = Integer.valueOf(0);
		}
		
		nuCns = numeroCNS;
		nacPac = eai.getNacionalidadePac();
		result = new DefaultRegistroAihIdentificacaoPaciente(this.renderer, nmPaciente, dtNascPac, sexoPac, racaCor, nmMaePac, nmRespPac, tpDocPac,
				etniaIndigena, nuCns, nacPac);

		return result;
	}

	protected RegistroAihEnderecoPaciente obterRegistroAihEnderecoPaciente(
			final FatEspelhoAih eai) {

		RegistroAihEnderecoPaciente result = null;
		Short tpLogradouro = null;
		String logrPac = null;
		Integer nuEndPac = null;
		String complEndPac = null;
		String bairroPac = null;
		Integer codMunEndPac = null;
		String ufPac = null;
		Integer cepPac = null;

		tpLogradouro = eai.getEndTipCodigo();
		logrPac = eai.getEndLogradouroPac();
		nuEndPac = eai.getEndNroLogradouroPac();
		complEndPac = eai.getEndCmplLogradouroPac();
		bairroPac = eai.getEndBairroPac();
		codMunEndPac = eai.getCodIbgeCidadePac();
		ufPac = eai.getEndUfPac();
		cepPac = eai.getEndCepPac();
		result = new DefaultRegistroAihEnderecoPaciente(this.renderer, tpLogradouro, logrPac, nuEndPac, complEndPac, bairroPac, codMunEndPac, ufPac, cepPac);

		return result;
	}

	protected RegistroAihInternacao obterRegistroAihInternacao(
			final FatEspelhoAih eai) {

		RegistroAihInternacao result = null;
		Long nuProntuario = null;
		String nuEnfermaria = null;
		String nuLeito = null;

		if (eai.getPacProntuario() != null) {
			nuProntuario = Long.valueOf(eai.getPacProntuario().longValue());
		}
		nuEnfermaria = eai.getEnfermaria();
		nuLeito = eai.getLeito();
		result = new DefaultRegistroAihInternacao(this.renderer, nuProntuario, nuEnfermaria, nuLeito);

		return result;
	}

	protected RegistroAihDocumentoPaciente obterRegistroAihDocumentoPaciente(
			final FatEspelhoAih eai) {

		RegistroAihDocumentoPaciente result = null;
		BigInteger nuDocpac = null;

		nuDocpac = eai.getPacNroCartaoSaude();
		result = new DefaultRegistroAihDocumentoPaciente(this.renderer, nuDocpac);

		return result;
	}

	protected boolean verificarIgualdadeGrupoAmaParamGrupoOpm(
			final FatAtoMedicoAih ama)
			throws ApplicationBusinessException {

		boolean result = false;
		AghParametros param = null;
		int grpOpm = 0;
		int grpSeq = 0;

		param = this.buscarAghParametro(AghuParametrosEnum.P_GRUPO_OPM);
		grpOpm = param.getVlrNumerico().intValue();
		grpSeq = ama.getItemProcedimentoHospitalar().getFormaOrganizacao().getId().getSgrGrpSeq().intValue();
		result = grpOpm == grpSeq;

		return result;
	}

	protected RegistroAihProcedimentosSecundariosEspeciais obterRegistroAihProcedimentosSecundariosEspeciais(
			final FatEspelhoAih eai,
			final FatAtoMedicoAih ama,
			final BigInteger cnes)
			throws ApplicationBusinessException,
				IOException {

		RegistroAihProcedimentosSecundariosEspeciais result = null;
		BuscaCpfCboResponsavelPorIchRN cpfRespRn = null;
		DominioCpfCnsCnpjCnes inProf = null;
		Long identProf = null;
		Integer cboProf = null;
		DominioInEquipe inEquipe = null;
		DominioCpfCnsCnpjCnes inServico = null;
		Long identServico = null;
		DominioCpfCnsCnpjCnes inExecutor = null;
		Long identExecutor = null;
		Long codProced = null;
		Short qtdProced = null;
		String cmpt = null;
		String cboResp = null;

		identProf = ama.getCpfCns();
		if ((identProf == null) || (identProf.longValue() <= 0l)) {
			cpfRespRn = this.getBuscaCpfResponsavelPorIchRN();
			identProf = cpfRespRn.buscarCpfResponsavel(
					ama.getId().getEaiCthSeq(),
					ama.getItemProcedimentoHospitalar().getId().getSeq(),
					ama.getItemProcedimentoHospitalar().getId().getPhoSeq());
		}
		if (identProf != null) {
			inProf = DominioCpfCnsCnpjCnes.CPF;
		} else {
			this.logFile(FaturamentoDebugCode.ARQ_SUS_AMA_SEM_IDENT_RESP, eai.getContaHospitalar().getSeq(), eai.getPacProntuario(), ama.getId().getEaiSeq());
			inProf = DominioCpfCnsCnpjCnes.NAO_APLICAVEL;
		}
		try {
			cboProf = Integer.valueOf(ama.getCbo());
		} catch (Exception e) {
			cboProf = null;
		}
		if ((cboProf == null) || (cboProf.intValue() <= 0)) {
			cpfRespRn = this.getBuscaCpfResponsavelPorIchRN();
			cboResp = cpfRespRn.buscarCboResponsavel(
					ama.getId().getEaiCthSeq(),
					ama.getItemProcedimentoHospitalar().getId().getSeq(),
					ama.getItemProcedimentoHospitalar().getId().getPhoSeq());
			if (cboResp != null) {
				cboProf = Integer.valueOf(cboResp);
			}
		}
		if (ama.getIndEquipe() != null) {
			inEquipe = DominioInEquipe.valueOf(ama.getIndEquipe().intValue());
		}
		inExecutor = DominioCpfCnsCnpjCnes.CNES;
		identExecutor = Long.valueOf(cnes.longValue());
		if (this.verificarIgualdadeGrupoAmaParamGrupoOpm(ama)) {
			inServico = DominioCpfCnsCnpjCnes.CNPJ;
			identServico = ama.getCgc();
		} else {
			inServico = inExecutor;
			identServico = identExecutor;
		}
		codProced = ama.getIphCodSus();
		qtdProced = ama.getQuantidade();
		cmpt = ama.getCompetenciaUti();
		result = new DefaultRegistroAihProcedimentosSecundariosEspeciais(this.renderer, inProf, identProf, cboProf, inEquipe, inServico, identServico,
				inExecutor, identExecutor, codProced, qtdProced, cmpt);

		return result;
	}

	protected List<RegistroAihProcedimentosSecundariosEspeciais> obterListaRegistroAihProcedimentosSecundariosEspeciais(
			final FatEspelhoAih eai,
			final List<FatAtoMedicoAih> listaAma,
			final BigInteger cnes)
			throws ApplicationBusinessException,
				IOException {

		List<RegistroAihProcedimentosSecundariosEspeciais> result = null;
		RegistroAihProcedimentosSecundariosEspeciais reg = null;

		if ((listaAma != null) && !listaAma.isEmpty()) {
			result = new LinkedList<RegistroAihProcedimentosSecundariosEspeciais>();
			for (FatAtoMedicoAih ama : listaAma) {
				reg = this.obterRegistroAihProcedimentosSecundariosEspeciais(eai, ama, cnes);
				result.add(reg);
			}
		}

		return result;
	}

	/**
	 * ORADB: <code>FATC_BUSCA_DADOS_RN</code>
	 * @param cthSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected FatAtoMedicoAih obterFatAtoMedicoAihParaUtiNeoAlta(
			final Integer cthSeq)
			throws ApplicationBusinessException {

		FatAtoMedicoAih result = null;
		List<FatAtoMedicoAih> lista = null;
		FatAtoMedicoAihDAO dao = null;
		AghParametros param = null;
		Long iphCodSus = null;

		dao = this.getFatAtoMedicoAihDAO();
		param = this.buscarAghParametro(AghuParametrosEnum.P_COD_UTI_3_NEONATAL_ALTA);
		iphCodSus = Long.valueOf(param.getVlrNumerico().longValue());
		lista = dao.listarPorIphCodSusCthSeqEaiSeq(iphCodSus, cthSeq, MAGIC_INTEGER_EAI_SEQ_BUSCA_UTI_NEONATAL_EQ_1);
		if ((lista != null) && !lista.isEmpty()) {
			result = lista.get(0);
		}

		return result;
	}

	protected RegistroAihUtiNeonatal obterRegistroAihUtiNeonatal(
			final FatEspelhoAih eai)
			throws ApplicationBusinessException {

		RegistroAihUtiNeonatal result = null;
		FatAtoMedicoAih ama = null;
		DominioSaidaUtineo saidaUtineo = null;
		Integer pesoUtineo = null;
		Integer mesgestUtineo = null;
		int notaFiscal = 0;

		if (eai.getId() == null) {
			throw new IllegalArgumentException("Parametro eai.id nao informado!!!");
		}
		ama = this.obterFatAtoMedicoAihParaUtiNeoAlta(eai.getId().getCthSeq());
		if ((ama != null) && (ama.getNotaFiscal() != null)) {
			if (ama.getNotaFiscal() == null) {
				throw new IllegalArgumentException("Parametro ama.notaFiscal nao informado!!!");
			}
			// supondo notaFiscal no formato ABCDEF (6 digitos)
			notaFiscal = ama.getNotaFiscal().intValue();
			// mes = F
			mesgestUtineo = Integer.valueOf(notaFiscal % 10);
			// notaFiscal = ABCDE
			notaFiscal = notaFiscal / 10;
			// peso = BCDE
			pesoUtineo = Integer.valueOf(notaFiscal % 10000);
			// notaFiscal = A
			notaFiscal = notaFiscal / 10000;
			// saida = A
			saidaUtineo = DominioSaidaUtineo.valueOf(notaFiscal % 10);
			result = new DefaultRegistroAihUtiNeonatal(this.renderer, saidaUtineo, pesoUtineo, mesgestUtineo);
		} else {
			result = new DefaultRegistroAihUtiNeonatal(this.renderer);
		}

		return result;
	}

	/**
	 * TODO: Nao estah previsto no AGH
	 * @param eai
	 * @return
	 */
	protected RegistroAihAcidenteTrabalho obterRegistroAihAcidenteTrabalho(
			final FatEspelhoAih eai) {

		RegistroAihAcidenteTrabalho result = null;

		result = new DefaultRegistroAihAcidenteTrabalho(this.renderer);

		return result;
	}

	/**
	 * <p>
	 * TODO AGH nao preve o preenchimento de:<br/>
	 * <ul>
	 * <li> QT_FILHOS </li>
	 * <li> CID_INDICACAO</li>
	 * <li> TP_CONTRACEP1</li>
	 * <li> TP_CONTRACEP2</li>
	 * </ul>
	 * </p>
	 * @param eai
	 * @return
	 */
	protected RegistroAihParto obterRegistroAihParto(
			final FatEspelhoAih eai) {

		RegistroAihParto result = null;
		Byte qtVivos = null;
		Byte qtMortos = null;
		Byte qtAlta = null;
		Byte qtTransf = null;
		Byte qtObito = null;
		Byte qtFilhos = null;
		DominioGrauInstru grauInstru = null;
		String cidIndicacao = null;
		DominioTpContracep tpContracep1 = null;
		DominioTpContracep tpContracep2 = null;
		DominioStGestrisco stGestrisco = null;
		Long nuPrenatal = null;

		qtVivos = eai.getNascidosVivos();
		qtMortos = eai.getNascidosMortos();
		qtAlta = eai.getSaidasAlta();
		qtTransf = eai.getSaidasTransferencia();
		qtObito = eai.getSaidasObito();
		//TODO QT_FILHOS
		grauInstru = DominioGrauInstru.valueOf(eai.getGrauInstrucaoPac());
		//TODO cidIndicacao
		//TODO tpContracep1
		//TODO tpContracep2
		stGestrisco = DominioStGestrisco.NAO;
		nuPrenatal = eai.getNroSisprenatal();
		result = new DefaultRegistroAihParto(this.renderer, qtVivos, qtMortos, qtAlta, qtTransf, qtObito, qtFilhos, grauInstru, cidIndicacao, tpContracep1,
				tpContracep2, stGestrisco, nuPrenatal);

		return result;
	}

	protected RegistroAihNormalSus obterAgrupamentoRegistroSusParaAihNormal(
			final EvtGeraAaqParcialAihNormalVO valor)
			throws ApplicationBusinessException,
				IOException {

		RegistroAihNormalSus result = null;
		RegistroAihComum comum = null;
		RegistroAihEspecificacao especificacao = null;
		RegistroAihIdentificacaoPaciente identificacaoPaciente = null;
		RegistroAihEnderecoPaciente enderecoPaciente = null;
		RegistroAihInternacao internacao = null;
		RegistroAihDocumentoPaciente documentoPaciente = null;
		List<RegistroAihProcedimentosSecundariosEspeciais> procedimentosSecundariosEspeciais = null;
		RegistroAihUtiNeonatal utiNeonatal = null;
		RegistroAihAcidenteTrabalho acidenteTrabalho = null;
		RegistroAihParto parto = null;

		comum = this.obterRegistroAihComum(valor.getEai(), valor.getTahSeq(), valor.getOrgLocRec(), valor.getCnes(), valor.getCodMunicipio());
		especificacao = this.obterRegistroAihEspecificacao(valor.getEai(), valor.getCpfDirClinico(), valor.getModalidadeIntern());
		identificacaoPaciente = this.obterRegistroAihIdentificacaoPaciente(valor.getEai(), valor.getNumeroCNSPaciente());
		enderecoPaciente = this.obterRegistroAihEnderecoPaciente(valor.getEai());
		internacao = this.obterRegistroAihInternacao(valor.getEai());
		documentoPaciente = this.obterRegistroAihDocumentoPaciente(valor.getEai());
		procedimentosSecundariosEspeciais = this.obterListaRegistroAihProcedimentosSecundariosEspeciais(valor.getEai(), valor.getListaAma(), valor.getCnes());
		utiNeonatal = this.obterRegistroAihUtiNeonatal(valor.getEai());
		acidenteTrabalho = this.obterRegistroAihAcidenteTrabalho(valor.getEai());
		parto = this.obterRegistroAihParto(valor.getEai());
		result = new RegistroAihNormalSusPadrao(comum, especificacao, identificacaoPaciente, enderecoPaciente, internacao, documentoPaciente,
				procedimentosSecundariosEspeciais, utiNeonatal, acidenteTrabalho, parto);

		return result;
	}

	protected RegistroAihRegistroCivil obterRegistroAihRegistroCivil(
			final AamPacVO aamPac) {

		RegistroAihRegistroCivil result = null;
		Long numeroDn = null;
		String nomeRn = null;
		String rsCart = null;
		String livroRn = null;
		Short folhaRn = null;
		Integer termoRn = null;
		Date dtEmisRn = null;
		Short linha = null;
		String matricula = null;
		AipPacientesDadosCns pds = null;
		AipPacientes pac = null;
		FatAtoMedicoAih aam = null;

		pac = aamPac.getPac();
		pds = pac.getAipPacientesDadosCns();
		aam = aamPac.getAam();
		dtEmisRn = pds.getDataEmissao();
		linha = aam.getSeqArqSus();
		if (dtEmisRn.before(MAGIC_DATE_TROCA_TIPO_REG_CIVIL_EQ_2010_01_04)) {
			numeroDn = pds.getNumeroDn();
			nomeRn = pac.getNome();
			rsCart = pds.getNomeCartorio();
			livroRn = pds.getLivro();
			folhaRn = pds.getFolhas();
			termoRn = pds.getTermo();
		}
		matricula =  pac.getRegNascimento();
		if (matricula.length() == MAGIC_INT_LENGTH_REG_NASCIMENTO_EQ_30) {
			matricula = null;
		}
		result = new DefaultRegistroAihRegistroCivil(this.renderer, numeroDn, nomeRn, rsCart, livroRn, folhaRn, termoRn, dtEmisRn, linha, matricula);

		return result;
	}

	protected List<RegistroAihRegistroCivil> obterListaRegistroAihRegistroCivil(
			final List<AamPacVO> listaAamPac) {

		List<RegistroAihRegistroCivil> result = null;
		RegistroAihRegistroCivil reg = null;

		if ((listaAamPac != null) && !listaAamPac.isEmpty()) {
			result = new LinkedList<RegistroAihRegistroCivil>();
			for (AamPacVO aamPac : listaAamPac) {
				reg = this.obterRegistroAihRegistroCivil(aamPac);
				result.add(reg);
			}
		}

		return result;
	}

	protected RegistroAihRegCivilSus obterAgrupamentoRegistroSusParaAihRegCivil(
			final EvtGeraAaqParcialAihRegCivilVO valor)
			throws IOException {

		RegistroAihRegCivilSus result = null;
		RegistroAihComum comum = null;
		List<RegistroAihRegistroCivil> registroCivil = null;

		comum = this.obterRegistroAihComum(valor.getEai(), valor.getTahSeq(), valor.getOrgLocRec(), valor.getCnes(), valor.getCodMunicipio());
		registroCivil = this.obterListaRegistroAihRegistroCivil(valor.getListaAamPac());
		result = new RegistroAihRegCivilSusPadrao(comum, registroCivil);

		return result;
	}

	protected RegistroAihDadosOpm obterRegistroAihDadosOpm(
			final FatEspelhoAih eai,
			final FatAtoMedicoAih ama)
			throws IOException {

		RegistroAihDadosOpm result = null;
		Long codOpm = null;
		Short linha = null;
		String regAnvisa = null;
		String serie = null;
		String lote = null;
		BigInteger notaFiscal = null;
		Long cnpjForn = null;
		Long cnpjFabric = null;

		codOpm = ama.getIphCodSus();
		linha = ama.getSeqArqSus();
		regAnvisa = ama.getRegAnvisaOpm();
		serie = ama.getSerieOpm();
		lote = ama.getLoteOpm();
		if (ama.getNotaFiscal() != null) {
			notaFiscal = BigInteger.valueOf(ama.getNotaFiscal().longValue());
		} else {
			this.logFile(FaturamentoDebugCode.ARQ_SUS_OPM_SEM_NF, eai.getContaHospitalar().getSeq(), eai.getPacProntuario(), codOpm);
		}
		cnpjForn = ama.getCgc();
		cnpjFabric = ama.getCnpjRegAnvisa();
		result = new DefaultRegistroAihDadosOpm(this.renderer, codOpm, linha, regAnvisa, serie, lote, notaFiscal, cnpjForn, cnpjFabric);

		return result;
	}

	protected List<RegistroAihDadosOpm> obterListaRegistroAihDadosOpm(
			final FatEspelhoAih eai,
			final List<FatAtoMedicoAih> listaAma)
			throws IOException {

		List<RegistroAihDadosOpm> result = null;
		RegistroAihDadosOpm reg = null;

		if ((listaAma != null) && !listaAma.isEmpty()) {
			result = new LinkedList<RegistroAihDadosOpm>();
			for (FatAtoMedicoAih ama : listaAma) {
				reg = this.obterRegistroAihDadosOpm(eai, ama);
				result.add(reg);
			}
		}

		return result;
	}

	protected RegistroAihOpmSus obterAgrupamentoRegistroSusParaAihOpm(
			final EvtGeraAaqParcialAihNormalVO valor)
			throws IOException {

		RegistroAihOpmSus result = null;
		RegistroAihComum comum = null;
		List<RegistroAihDadosOpm> dadosOpm = null;

		comum = this.obterRegistroAihComum(valor.getEai(), valor.getTahSeq(), valor.getOrgLocRec(), valor.getCnes(), valor.getCodMunicipio());
		dadosOpm = this.obterListaRegistroAihDadosOpm(valor.getEai(), valor.getListaAma());
		result = new RegistroAihOpmSusPadrao(comum, dadosOpm);

		return result;
	}

	protected AgrupamentoRegistroAih obterAgrupamentoRegistroSus(
			final EvtGeraAaqParcialAihGeralVO valor)
			throws ApplicationBusinessException,
				IOException {

		AgrupamentoRegistroAih result = null;
		Short identAih = null;

		identAih = valor.getTahSeq();
		switch (identAih.intValue()) {
		case 1:
		case 3:
		case 5:
			if (!(valor instanceof EvtGeraAaqParcialAihNormalVO)) {
				throw new IllegalArgumentException("VO para AIH " + identAih
						+ " nao eh do tipo esperado ["
						+ EvtGeraAaqParcialAihNormalVO.class.getSimpleName()
						+ "], recebido: " + valor.getClass().getSimpleName());
			}
			result = this.obterAgrupamentoRegistroSusParaAihNormal((EvtGeraAaqParcialAihNormalVO) valor);
			break;
		case 4:
			if (!(valor instanceof EvtGeraAaqParcialAihRegCivilVO)) {
				throw new IllegalArgumentException("VO para AIH " + identAih
						+ " nao eh do tipo esperado ["
						+ EvtGeraAaqParcialAihRegCivilVO.class.getSimpleName()
						+ "], recebido: " + valor.getClass().getSimpleName());
			}
			result = this.obterAgrupamentoRegistroSusParaAihRegCivil((EvtGeraAaqParcialAihRegCivilVO) valor);
			break;
		case 7:
			if (!(valor instanceof EvtGeraAaqParcialAihNormalVO)) {
				throw new IllegalArgumentException("VO para AIH " + identAih
						+ " nao eh do tipo esperado ["
						+ EvtGeraAaqParcialAihNormalVO.class.getSimpleName()
						+ "], recebido: " + valor.getClass().getSimpleName());
			}
			result = this.obterAgrupamentoRegistroSusParaAihOpm((EvtGeraAaqParcialAihNormalVO) valor);
			break;
		default:
			throw new IllegalArgumentException("AIH do tipo "
					+ identAih
					+ " nao eh valido para geracao de registro de envio para o SUS");
		}

		return result;
	}

	protected List<AgrupamentoRegistroAih> obterListaRegistrosSus(
			final List<EvtGeraAaqParcialAihGeralVO> valor,
			final String arquivo)
			throws IOException {

		List<AgrupamentoRegistroAih> result = null;
		AgrupamentoRegistroAih reg = null;
		Integer cthSeq = null;
		Integer pronturario = null;

		result = new LinkedList<AgrupamentoRegistroAih>();
		for (EvtGeraAaqParcialAihGeralVO val : valor) {
			try {
				reg = this.obterAgrupamentoRegistroSus(val);
				result.add(reg);
			} catch (Exception e) {
				cthSeq = val.getEai().getContaHospitalar().getSeq();
				pronturario = val.getEai().getPacProntuario();
				this.logFile(FaturamentoDebugCode.ARQ_SUS_ERRO_TRADUCAO_VO_REG,
						cthSeq,
						pronturario,
						arquivo,
						val.getClass().getSimpleName(),
						e.getLocalizedMessage(),
						Arrays.toString(e.getStackTrace()));
			}
		}

		return result;
	}

	protected List<String> obterListaEntradasSus(
			final List<EvtGeraAaqParcialAihGeralVO> valor,
			final GeradorRegistroSus gerador, final String arquivo)
			throws IOException {

		List<String> result = null;
		List<AgrupamentoRegistroAih> listaAgrpReg = null;
		EvtGeraAaqParcialAihGeralVO vo = null;
		FatEspelhoAih eai = null;
		String linha = null;
		int ndx = 0;

		listaAgrpReg = this.obterListaRegistrosSus(valor, arquivo);
		if ((listaAgrpReg != null) && !listaAgrpReg.isEmpty()) {
			result = new LinkedList<String>();
			ndx = 0;
			for (AgrupamentoRegistroAih reg : listaAgrpReg) {
				try {
					linha = gerador.obterRegistroAihFormatado(reg);
					result.add(linha);
				} catch (Exception e) {
					vo = valor.get(ndx);
					eai = vo.getEai();
					this.logFile(FaturamentoDebugCode.ARQ_ERRO_GERANDO_ENTRADA_ARQUIVO, eai.getContaHospitalar().getSeq(), eai.getPacProntuario(), arquivo,
							e.getLocalizedMessage());
				}
				ndx++;
			}
		}

		return result;
	}

	public URI gerarDadosEmArquivo(
			final List<EvtGeraAaqParcialAihGeralVO> listaVO,
			final String prefixoArqSus)
			throws IOException {

		URI result = obterURIArquivo(prefixoArqSus.trim(), EXTENSAO_ARQUIVO_SUS_EQ_TXT);
		GeradorRegistroSus gerador = this.getGeracaoRegistroSusArquivoSisaih01();
		List<String> listaEntrada = this.obterListaEntradasSus(listaVO, gerador, prefixoArqSus);
		int lstEntSize = listaEntrada != null ? listaEntrada.size()	: 0;
		
		if (lstEntSize != listaVO.size()) {
			this.logFile(FaturamentoDebugCode.ARQ_ERRO_GERACAO_ENTRADA_REG, prefixoArqSus, Integer.valueOf(listaVO.size()), Integer.valueOf(0));
		}
		if (listaEntrada != null) {
			this.gravarListaEntradasEmArquivo(result, listaEntrada, IS_CHARSET_ISO);
		}

		return result;
	}
}
