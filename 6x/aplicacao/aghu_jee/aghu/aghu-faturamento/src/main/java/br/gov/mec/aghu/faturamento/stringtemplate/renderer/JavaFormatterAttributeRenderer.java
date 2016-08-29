package br.gov.mec.aghu.faturamento.stringtemplate.renderer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.IllegalFormatConversionException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Gramatica aceita para obrigar tamanhos:<br/>
 * <code>#</code>
 * <code>&lt;tamanho: inteiro nao-negativo sem sinal&gt;</code>
 * <code>&lt;tipo de dado: d, s ou t&gt;</code>
 * </p>
 * <p>
 * Onde o tipo tem a seguinte interpretacao:
 * <ul>
 * <li> <code>d</code> tipos numericos </li>
 * <li> <code>s</code> tipos caracteres </li>
 * <li> <code>t</code> tipos data </li>
 * </ul>
 * </p>
 * <p>
 * Caso se queira usar as formatacoes disponives via {@link Formatter}, basta informa-los de forma inalterada.
 * </p>
 * @see JavaFormatterAttributeRenderer#FORMATACAO_INTERNA_COM_IMPOSICAO_TAMANHO
 * @see JavaFormatterAttributeRenderer#TIPOS_SUPORTADOS
 * @author gandriotti
 *
 */
public class JavaFormatterAttributeRenderer
		extends
			ExtendedAttributeRenderer {

	public static class NumericPair
			extends
				AttributeFormatPair {

		@SuppressWarnings("rawtypes")
		public final static Set<Class> TIPOS_SUPORTADOS = new HashSet<Class>();

		static {
			// tipos numericos
			TIPOS_SUPORTADOS.add(Byte.class);
			TIPOS_SUPORTADOS.add(Short.class);
			TIPOS_SUPORTADOS.add(Integer.class);
			TIPOS_SUPORTADOS.add(Long.class);
			TIPOS_SUPORTADOS.add(BigInteger.class);
			TIPOS_SUPORTADOS.add(Float.class);
			TIPOS_SUPORTADOS.add(Double.class);
			TIPOS_SUPORTADOS.add(BigDecimal.class);
		};

		public NumericPair(final Object attribute, final String format) {

			super(attribute, format);
		}
	}

	public static class DatePair
			extends
				AttributeFormatPair {

		@SuppressWarnings("rawtypes")
		public final static Set<Class> TIPOS_SUPORTADOS = new HashSet<Class>();

		static {
			// tipos data
			TIPOS_SUPORTADOS.add(Date.class);
			TIPOS_SUPORTADOS.add(java.sql.Date.class);
			TIPOS_SUPORTADOS.add(Time.class);
			TIPOS_SUPORTADOS.add(Timestamp.class);
			TIPOS_SUPORTADOS.add(Calendar.class);
			TIPOS_SUPORTADOS.add(GregorianCalendar.class);
		};

		public DatePair(final Object attribute, final String format) {

			super(attribute, format);
		}
	}

	public static class CharacterPair
			extends
				AttributeFormatPair {

		@SuppressWarnings("rawtypes")
		public final static Set<Class> TIPOS_SUPORTADOS = new HashSet<Class>();

		static {
			// tipos string
			TIPOS_SUPORTADOS.add(Character.class);
			TIPOS_SUPORTADOS.add(String.class);
		};

		public CharacterPair(final Object attribute, final String format) {

			super(attribute, format);
		}
	}

	public static class BooleanPair
			extends
				AttributeFormatPair {

		@SuppressWarnings("rawtypes")
		public final static Set<Class> TIPOS_SUPORTADOS = new HashSet<Class>();

		static {
			// outros tipos diversos
			TIPOS_SUPORTADOS.add(Boolean.class);
		};

		public BooleanPair(final Object attribute, final String format) {

			super(attribute, format);
		}
	}

	protected static final String FORMATO_DATA_EQ_YYYYMMDD = "%1$tY%1$tm%1$td";
	protected static final String FORMATO_DATA_EQ_YYYYMM = "%1$tY%1$tm";
	protected static final char FLAG_PADDING_CARACTER_CHAR_EQ_MINUS_SIGN = '-';
	protected static final char FLAG_PADDING_NUMERICO_CHAR_EQ_0 = '0';
	protected static final char TIPO_DADO_NUMERICO_CHAR_EQ_D = 'd';
	protected static final char TIPO_DADO_CARACTER_ESQ_CHAR_EQ_S = 's';
	protected static final char TIPO_DADO_CARACTER_DIR_CHAR_EQ_S = 'S';
	protected static final char TIPO_DADO_CARACTER_COMO_NUMERO_CHAR_EQ_N = 'N';
	protected final static char TIPO_DADO_DATA_CHAR_EQ_T = 't';
	protected final static char FORMATACAO_INTERNA_COM_IMPOSICAO_TAMANHO = '#';
	private final static JavaFormatterAttributeRenderer instance = new JavaFormatterAttributeRenderer();

	private JavaFormatterAttributeRenderer() {

		super();
	}

	protected int obterTamanho(final String formato) {

		int result = 0;
		String tamanho = null;

		tamanho = formato.substring(
				1, formato.length() - 1);
		result = Integer.valueOf(tamanho);

		return result;
	}

	protected char obterTipoDado(final String formato) {
	
		char tipoDado = '\0';
	
		tipoDado = formato.charAt(formato.length() - 1);
		
		return tipoDado;
	}

	protected char obterFlag(final String formato) {

		char result = '\0';
		char tipoDado = obterTipoDado(formato);
		switch (tipoDado) {
		case TIPO_DADO_NUMERICO_CHAR_EQ_D:
			result = FLAG_PADDING_NUMERICO_CHAR_EQ_0;
			break;
		case TIPO_DADO_CARACTER_ESQ_CHAR_EQ_S:
			result = FLAG_PADDING_CARACTER_CHAR_EQ_MINUS_SIGN;
			break;
		case TIPO_DADO_CARACTER_DIR_CHAR_EQ_S:
			result = '\0';
			break;
		default:
			throw new IllegalArgumentException("Tipo de dado desconhecido: "
					+ tipoDado);
		}

		return result;
	}

	protected String obterFormatoParaData(final int tamanho) {

		String result = null;

		if (tamanho == 6) {
			result = FORMATO_DATA_EQ_YYYYMM;
		} else if (tamanho == 8) {
			result = FORMATO_DATA_EQ_YYYYMMDD;
		} else {
			throw new IllegalArgumentException("Tamanho " + tamanho
					+ " nao eh valido.");
		}

		return result;
	}

	protected String obterFormatterString(final String formato) {

		String result = null;
		char flag = '\0';
		char tipoDado = '\0';
		int tamanho = 0;

		tamanho = this.obterTamanho(formato);
		tipoDado = formato.charAt(formato.length() - 1);
		if (TIPO_DADO_DATA_CHAR_EQ_T == tipoDado) {
			result = this.obterFormatoParaData(tamanho);
		} else if (TIPO_DADO_CARACTER_COMO_NUMERO_CHAR_EQ_N == tipoDado) {
			result = "%" + tamanho + TIPO_DADO_CARACTER_ESQ_CHAR_EQ_S;
		} else if (TIPO_DADO_CARACTER_DIR_CHAR_EQ_S == tipoDado) {
			result = "%" + tamanho + TIPO_DADO_CARACTER_ESQ_CHAR_EQ_S;
		} else {
			flag = this.obterFlag(formato);
			result = "%" + flag + tamanho + tipoDado;
		}

		return result;
	}
	
	protected String obterValorModificadoParaCharComoNum(String valor, int tamanho) {
		
		String result = null;
		int length = 0;
		
		length = valor.length();
		if (length < tamanho) {
			result = zeroPadding(tamanho - length) + valor;
		} else {
			result = valor;
		}
		
		return result;
	}

	@Override
	public String toString(final Object valor, final String formatacao) {

		String result = null;
		String formatacaoReal = null;
		Object valorReal = null;
		boolean imporTamanho = false;
		int tamanho = 0;
		char tipo = '\0';

		valorReal = valor;
		formatacaoReal = formatacao;
		imporTamanho = FORMATACAO_INTERNA_COM_IMPOSICAO_TAMANHO == formatacao.charAt(0);
		// converte formato interno para Formatter
		if (imporTamanho) {
			tamanho = this.obterTamanho(formatacao);
			tipo = this.obterTipoDado(formatacao);
			formatacaoReal = this.obterFormatterString(formatacao);
			if (TIPO_DADO_CARACTER_COMO_NUMERO_CHAR_EQ_N == tipo) {
				valorReal = this.obterValorModificadoParaCharComoNum((String)valorReal, tamanho);
			}
		}
		try {
			result = String.format(formatacaoReal, valorReal);
		} catch (IllegalFormatConversionException e) {
			throw new IllegalArgumentException("Valor ["
					+ valorReal + "] do tipo ["
					+ valorReal.getClass().getSimpleName()
					+ "] nao pode ser convertido usando: [" + formatacao
					+ "], erro:\n" + e.getLocalizedMessage(), e);
		}
		// se existe imposicao de tamanho, verificar
		if (imporTamanho && (result.length() > tamanho)) {
			throw new IllegalArgumentException("Argumento: [" + valorReal
					+ "], contendo " + result.length()
					+ " caracteres, excedeu o tamanho maximo permitido: "
					+ tamanho);
		}

		return result;
	}

	@Override
	public AttributeFormatPair toValue(final Object valor, final String formatacao) {

		AttributeFormatPair result = null;

		if (valor != null) {
			if (NumericPair.TIPOS_SUPORTADOS.contains(valor.getClass())) {
				result = new NumericPair(valor, formatacao);
			} else if (DatePair.TIPOS_SUPORTADOS.contains(valor.getClass())) {
				result = new DatePair(valor, formatacao);
			} else if (CharacterPair.TIPOS_SUPORTADOS.contains(valor.getClass())) {
				result = new CharacterPair(valor, formatacao);
			} else if (BooleanPair.TIPOS_SUPORTADOS.contains(valor.getClass())) {
				result = new BooleanPair(valor, formatacao);
			} else {
				throw new IllegalArgumentException("Objeto do tipo: " + valor.getClass().getSimpleName() + " nao eh suportado");
			}
		} else {
			result = new AttributeFormatPair(valor, null);
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Set<Class> getSupportedTypes() {

		Set<Class> result = null;

		result = new HashSet<Class>();
		result.addAll(NumericPair.TIPOS_SUPORTADOS);
		result.addAll(DatePair.TIPOS_SUPORTADOS);
		result.addAll(CharacterPair.TIPOS_SUPORTADOS);
		result.addAll(BooleanPair.TIPOS_SUPORTADOS);

		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Class> obterClassesTratadas() {
	
		List<Class> result = null;
	
		result = new LinkedList<Class>();
		result.add(NumericPair.class);
		result.add(DatePair.class);
		result.add(CharacterPair.class);
		result.add(BooleanPair.class);
	
		return result;
	}

	public static String zeroPadding(final int qtd) {
	
		String result = null;
	
		if (qtd > 0) {
			result = String.format("%0" + qtd + "d", Integer.valueOf(0));
		} else {
			result = "";
		}
	
		return result;
	}

	public static JavaFormatterAttributeRenderer getInstance() {

		return instance;
	}
}
