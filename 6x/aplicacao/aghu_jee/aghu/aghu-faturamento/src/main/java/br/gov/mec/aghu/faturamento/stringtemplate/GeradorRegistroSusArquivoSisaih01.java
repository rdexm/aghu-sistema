package br.gov.mec.aghu.faturamento.stringtemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;

import br.gov.mec.aghu.faturamento.business.GeracaoArquivoLog;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoDebugCode;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.AgrupamentoRegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAih.NomeStringTemplateEnum;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihNormalSus;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihOpmSus;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihRegCivilSus;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class GeradorRegistroSusArquivoSisaih01
		extends
			AbstractGeradorRegistroSus {

	private static final String MSG_PARAMETRO_REGISTRO_NAO_INFORMADO = "Parametro registro nao informado!!!";

	/**
	 * 
	 */
	private static final long serialVersionUID = -5772414974511754565L;

	protected static final boolean TRUNC_LIST_EXCESS = true;
	private static final String STRING_TEMPLATE_MARCADOR_STRING_EQ_NUM_FILLER = "num_filler_";
	private static final int SUS_FILLER_FINAL_AIH07_EQ_285 = 285;
	private static final int SUS_FILLER_FINAL_AIH04_EQ_183 = 183;
	private static final int SUS_FILLER_FINAL_NORMAL_EQ_72 = 72;
	protected static final int SUS_MAX_QTD_ENTRADA_DADOS_OPM_EQ_10 = 10;
	protected static final int SUS_MAX_QTD_ENTRADA_REG_CIVIL_EQ_8 = 8;
	public static final int SUS_MAX_QTD_ENTRADA_PROC_SEC_ESP_EQ_10 = 10;
	protected static final String STRING_TEMPLATE_GROUP_NAME = "arquivo_sus_txt";
	protected static final String STRING_TEMPLATE_PARENT_DIR = "stringtemplates";
	protected static final String STRING_TEMPLATE_GROUP_NAME_FILE = STRING_TEMPLATE_GROUP_NAME + ".stg";
	protected static final int OUTPUT_STRING_LENGTH = 1600;

	private StringTemplateGroup stGroup = null;

	protected Reader getReaderFromContext() {

		InputStream in = null;
		InputStreamReader result = null;

		in = Thread.currentThread().getContextClassLoader().getResourceAsStream(STRING_TEMPLATE_PARENT_DIR + "/" + STRING_TEMPLATE_GROUP_NAME_FILE);
		result = new InputStreamReader(in);

		return result;
	}

	protected Reader getReaderFromFile()
			throws FileNotFoundException {

		FileReader reader = null;
		String path = null;

		path = System.getProperty("user.dir") + File.separatorChar
				+ "jar" + File.separatorChar
				+ "src" + File.separatorChar
				+ "main" + File.separatorChar			
				+ "resources" + File.separatorChar
				+ STRING_TEMPLATE_PARENT_DIR + File.separatorChar
				+ STRING_TEMPLATE_GROUP_NAME_FILE;
		reader = new FileReader(path);

		return reader;
	}

	protected Reader getReader()
			throws FileNotFoundException {

		Reader result = null;

		try {
			result = this.getReaderFromContext();
		} catch (Exception e) {
			result = this.getReaderFromFile();
		}

		return result;
	}

	protected StringTemplateGroup getStGroup()
			throws FileNotFoundException {

		Reader reader = null;

		if (this.stGroup == null) {
			reader = this.getReader();
			this.stGroup = new StringTemplateGroup(reader, AngleBracketTemplateLexer.class);
		}

		return this.stGroup;
	}

	protected static int obterQuantidadeRegistros(
			final NomeStringTemplateEnum tipo) {

		int result = -1;

		switch (tipo) {
		case COMUM:
		case ESPECIFICACAO:
		case IDENT_PACIENTE:
		case END_PACIENTE:
		case INTERNACAO:
		case UTI_NEONATAL:
		case ACIDENTE_TRABALHO:
		case PARTO:
		case DOC_PACIENTE:
			result = 1;
			break;
		case PROC_SEC_ESP:
			result = SUS_MAX_QTD_ENTRADA_PROC_SEC_ESP_EQ_10;
			break;
		case REG_CIVIL:
			result = SUS_MAX_QTD_ENTRADA_REG_CIVIL_EQ_8;
			break;
		case DADOS_OPM:
			result = SUS_MAX_QTD_ENTRADA_DADOS_OPM_EQ_10;
			break;
		case NENHUM:
			result = 0;
			break;
		default:
			throw new IllegalArgumentException("Tipo nao reconhecido: "
					+ tipo);
		}

		return result;
	}

	protected static int obterTamanhoEsperado(final NomeStringTemplateEnum tipo) {

		int result = -1;

		switch (tipo) {
		case COMUM:
			result = 105;
			break;
		case ESPECIFICACAO:
			result = 163;
			break;
		case IDENT_PACIENTE:
			result = 251;
			break;
		case END_PACIENTE:
			result = 121;
			break;
		case INTERNACAO:
			result = 23;
			break;
		case PROC_SEC_ESP:
			result = 73;
			break;
		case UTI_NEONATAL:
			result = 6;
			break;
		case ACIDENTE_TRABALHO:
			result = 24;
			break;
		case PARTO:
			result = 73;
			break;
		case DOC_PACIENTE:
			result = 32;
			break;
		case REG_CIVIL:
			result = 164;
			break;
		case DADOS_OPM:
			result = 121;
			break;
		case NENHUM:
			result = 0;
			break;
		default:
			throw new IllegalArgumentException("Tipo nao reconhecido: "
					+ tipo);
		}

		return result;
	}

	protected static String zeroPadding(final int qtd) {

		String result = null;

		if (qtd > 0) {
			result = String.format("%0" + qtd + "d", Integer.valueOf(0));
		} else {
			result = "";
		}

		return result;
	}

	protected static String obterFiller(final String atributo) {

		String result = null;
		String strQtd = null;
		int qtd = 0;

		strQtd = atributo.substring(STRING_TEMPLATE_MARCADOR_STRING_EQ_NUM_FILLER.length(), atributo.length());
		qtd = Integer.valueOf(strQtd);
		result = zeroPadding(qtd);

		return result;
	}

	@SuppressWarnings("rawtypes")
	protected static void ajustarNumFiller(final StringTemplate st) {

		Map map = null;
		String key = null;
		String filler = null;

		map = st.getFormalArguments();
		for (Object e : map.entrySet()) {
			key = (String) ((Map.Entry) e).getKey();
			if (key.contains(STRING_TEMPLATE_MARCADOR_STRING_EQ_NUM_FILLER)) {
				filler = obterFiller(key);
				st.setAttribute(key, filler);
			}
		}
	}

	protected static StringTemplate perpararStringTemplate(
			final StringTemplateGroup stg,
			final RegistroAih reg) {

		StringTemplate result = null;

		result = reg.obterStringTemplateComAtributos(stg);
		ajustarNumFiller(result);

		return result;
	}

	protected static String obterStringParcialComTamanhoVerificado(
			final StringTemplate st,
			final RegistroAih reg,
			final NomeStringTemplateEnum tipo)
			throws ApplicationBusinessException {

		String result = null;
		int tamExperado = 0;
		int tamReal = 0;

		tamExperado = obterTamanhoEsperado(tipo);
		if (reg != null) {
			try {
				result = st.toString();
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException(
						"Obtencao da string para registro do tipo ["
								+ tipo + "] usando template ["
								+ st.getName() + "] = [" + st.getTemplate()
								+ "], erro:\n" + e.getLocalizedMessage(), e);
			}
			tamReal = result.length();
			if (tamReal != tamExperado) {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.ARQ_SUS_QTD_CHAR_REG_INVALIDO,
						reg.getNomeStringTemplate(),
						Integer.valueOf(tamExperado),
						Integer.valueOf(tamReal));
			}
		} else {
			result = zeroPadding(tamExperado);
		}

		return result;
	}

	protected String obterNovoResultado(
			final RegistroAih reg, final NomeStringTemplateEnum tipo)
			throws FileNotFoundException,
			ApplicationBusinessException {

		String result = null;
		StringTemplateGroup stg = null;
		StringTemplate st = null;

		stg = this.getStGroup();
		if (reg != null) {
			this.ajustarFormatoAtributos(reg);
			st = perpararStringTemplate(stg, reg);
		}
		result = obterStringParcialComTamanhoVerificado(st, reg, tipo);

		return result;
	}

	protected String obterNovoResultadoParaLista(
			@SuppressWarnings("rawtypes") final List lista,
			final NomeStringTemplateEnum tipo)
			throws ApplicationBusinessException,
				IOException {

		String parcial = null;
		RegistroAih casted = null;
		@SuppressWarnings("rawtypes")
		List listaReal = null;
		int max = 0;
		int expLength = 0;

		max = obterQuantidadeRegistros(tipo);
		StringBuffer result = new StringBuffer();
		listaReal = lista;
		if (listaReal != null) {
			if (listaReal.size() > max) {
				if (TRUNC_LIST_EXCESS) {
					listaReal = listaReal.subList(0, max);
					this.logFile(FaturamentoDebugCode.ARQ_SUS_LISTA_REG_TRUNCADA,
							tipo.getNome(),
							Integer.valueOf(max),
							Integer.valueOf(lista.size()),
							lista.subList(max, lista.size()).toString());
				} else {
					throw new ApplicationBusinessException(FaturamentoExceptionCode.ARQ_SUS_QTD_MAX_REG_EXCEDIDO,
							tipo.getNome(),
							Integer.valueOf(max),
							Integer.valueOf(listaReal.size()),
							listaReal);
				}
			}
			for (Object obj : listaReal) {
				casted = (RegistroAih) obj;
				parcial = this.obterNovoResultado(casted, tipo);
				result.append(parcial);
			}
		}
		expLength = max * obterTamanhoEsperado(tipo);
		result.append(zeroPadding(expLength - result.length()));

		return result.toString();
	}

	protected static int obterFillerFinal(final AgrupamentoRegistroAih registro) {

		int result = 0;

		if (registro instanceof RegistroAihNormalSus) {
			result = SUS_FILLER_FINAL_NORMAL_EQ_72;
		} else if (registro instanceof RegistroAihRegCivilSus) {
			result = SUS_FILLER_FINAL_AIH04_EQ_183;
		} else if (registro instanceof RegistroAihOpmSus) {
			result = SUS_FILLER_FINAL_AIH07_EQ_285;
		}

		return result;
	}

	protected static void verificarResultado(final String result) {

		if (OUTPUT_STRING_LENGTH != result.length()) {
			throw new IllegalStateException("Tamanho da linha ["
					+ result.length() + "] nao confere com o esperado ["
					+ OUTPUT_STRING_LENGTH + "]");
		}
	}

	public GeradorRegistroSusArquivoSisaih01(final GeracaoArquivoLog logger) {

		super(logger, AjusteFormatoAtributoRegistroAihSisaih01.getInstance());
	}

	@Override
	public String obterRegistroAihNormalFormatado(
			final RegistroAihNormalSus registro)
			throws ApplicationBusinessException,
				IOException {

		RegistroAih reg = null;
		NomeStringTemplateEnum tipo = null;

		//check args
		if (registro == null) {
			throw new IllegalArgumentException(MSG_PARAMETRO_REGISTRO_NAO_INFORMADO);
		}
		if (registro.getComum() == null) {
			throw new IllegalArgumentException();
		}
		if (registro.getEspecificacao() == null) {
			throw new IllegalArgumentException();
		}
		if (registro.getIdentificacaoPaciente() == null) {
			throw new IllegalArgumentException();
		}
		if (registro.getEnderecoPaciente() == null) {
			throw new IllegalArgumentException();
		}
		if (registro.getInternacao() == null) {
			throw new IllegalArgumentException();
		}
		if (registro.getDocumentoPaciente() == null) {
			throw new IllegalArgumentException();
		}
		//algo
		StringBuffer result = new StringBuffer();
		//Comum: 001 - 011
		reg = registro.getComum();
		tipo = NomeStringTemplateEnum.COMUM;
		result.append(this.obterNovoResultado(reg, tipo));
		//Especificacao: 012 - 036
		reg = registro.getEspecificacao();
		tipo = NomeStringTemplateEnum.ESPECIFICACAO;
		result.append(this.obterNovoResultado(reg, tipo));
		//Paciente: 037 - 047
		reg = registro.getIdentificacaoPaciente();
		tipo = NomeStringTemplateEnum.IDENT_PACIENTE;
		result.append(this.obterNovoResultado(reg, tipo));
		//EnderecoPaciente: 048 - 055
		reg = registro.getEnderecoPaciente();
		tipo = NomeStringTemplateEnum.END_PACIENTE;
		result.append(this.obterNovoResultado(reg, tipo));
		//Internacao: 056 - 058
		reg = registro.getInternacao();
		tipo = NomeStringTemplateEnum.INTERNACAO;
		result.append(this.obterNovoResultado(reg, tipo))
		//Proc Sec/Esp 059 - 070
		.append(this.obterNovoResultadoParaLista(
				registro.getProcedimentosSecundariosEspeciais(),
				NomeStringTemplateEnum.PROC_SEC_ESP));
		//UtiNeonatal: 071 - 073
		reg = registro.getUtiNeonatal();
		tipo = NomeStringTemplateEnum.UTI_NEONATAL;
		result.append(this.obterNovoResultado(reg, tipo));
		//AcidenteTrabalho: 074 - 078
		reg = registro.getAcidenteTrabalho();
		tipo = NomeStringTemplateEnum.ACIDENTE_TRABALHO;
		result.append(this.obterNovoResultado(reg, tipo));
		//Parto: 079 - 091
		reg = registro.getParto();
		tipo = NomeStringTemplateEnum.PARTO;
		result.append(this.obterNovoResultado(reg, tipo));
		//DocumentoPaciente: 092 - 092
		reg = registro.getDocumentoPaciente();
		tipo = NomeStringTemplateEnum.DOC_PACIENTE;
		result.append(this.obterNovoResultado(reg, tipo))
		//filler: 093
		.append(zeroPadding(obterFillerFinal(registro)));
		//check
		verificarResultado(result.toString());

		return result.toString();
	}

	@Override
	public String obterRegistroAihRegCivilFormatado(
			final RegistroAihRegCivilSus registro)
			throws ApplicationBusinessException,
				IOException {

		RegistroAih reg = null;
		NomeStringTemplateEnum tipo = null;

		//check args
		if (registro == null) {
			throw new IllegalArgumentException(MSG_PARAMETRO_REGISTRO_NAO_INFORMADO);
		}
		if (registro.getComum() == null) {
			throw new IllegalArgumentException();
		}
		if (registro.getRegistroCivil() == null) {
			throw new IllegalArgumentException();
		}
		//algo
		StringBuffer result = new StringBuffer();
		//Comum: 001 - 011
		reg = registro.getComum();
		tipo = NomeStringTemplateEnum.COMUM;
		result.append(this.obterNovoResultado(
				reg, tipo))
		//RegistroCivil: 012 - 020
		.append(this.obterNovoResultadoParaLista(
				registro.getRegistroCivil(),
				NomeStringTemplateEnum.REG_CIVIL))
		//filler: 021
		.append(zeroPadding(obterFillerFinal(registro)));
		//check
		verificarResultado(result.toString());

		return result.toString();
	}

	@Override
	public String obterRegistroAihOpmFormatado(final RegistroAihOpmSus registro)
			throws ApplicationBusinessException,
				IOException {

		RegistroAih reg = null;
		NomeStringTemplateEnum tipo = null;

		//check args
		if (registro == null) {
			throw new IllegalArgumentException(MSG_PARAMETRO_REGISTRO_NAO_INFORMADO);
		}
		if (registro.getComum() == null) {
			throw new IllegalArgumentException();
		}
		if (registro.getDadosOpm() == null) {
			throw new IllegalArgumentException();
		}
		//algo
		StringBuffer result = new StringBuffer();
		//Comum: 001 - 011
		reg = registro.getComum();
		tipo = NomeStringTemplateEnum.COMUM;
		result.append(this.obterNovoResultado(reg, tipo))
		//DadosOpm: 012 - 020
		.append(this.obterNovoResultadoParaLista(
				registro.getDadosOpm(),
				NomeStringTemplateEnum.DADOS_OPM))
		//filler: 021
		.append(zeroPadding(obterFillerFinal(registro)));
		//check
		verificarResultado(result.toString());

		return result.toString();
	}

	@Override
	public String obterRegistroAihFormatado(final AgrupamentoRegistroAih registro)
			throws ApplicationBusinessException,
				IOException {

		String result = null;

		//check args
		if (registro == null) {
			throw new IllegalArgumentException(MSG_PARAMETRO_REGISTRO_NAO_INFORMADO);
		}
		if (registro instanceof RegistroAihNormalSus) {
			result = this.obterRegistroAihNormalFormatado((RegistroAihNormalSus) registro);
		} else if (registro instanceof RegistroAihRegCivilSus) {
			result = this.obterRegistroAihRegCivilFormatado((RegistroAihRegCivilSus) registro);
		} else if (registro instanceof RegistroAihOpmSus) {
			result = this.obterRegistroAihOpmFormatado((RegistroAihOpmSus) registro);
		} else {
			throw new IllegalArgumentException("Agrupamento de registro nao pode ser reconhecido: " + registro.getClass().getSimpleName());
		}

		return result;
	}
}