package br.gov.mec.aghu.core.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.PersistenceException;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.MaskFormatter;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import net.htmlparser.jericho.Source;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * substitui: MecUtil
 * substitui: AghuUtil
 * 
 * @author rcorvalao
 */
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.AghuTooManyMethods", "PMD.ShortMethodName"})
public class CoreUtil {

	private static final Log LOG = LogFactory.getLog(CoreUtil.class);
	
	private static final String MIME_TYPE_TEXT_HTML = "text/html";
	
	private enum CoreUtilExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS, MENSAGEM_LIMITE_DIAS_RELATORIO;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	

	/**
	 * Este método já havia sido implementado em outra classe, apenas migrei
	 * para cá, pois precisei reutilizá-lo.
	 * 
	 * Calcula a diferenca em dias entre duas datas.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static Float diferencaEntreDatasEmDias(Date dataFinal, Date dataInicial) {
		Calendar cInicial = Calendar.getInstance();
		cInicial.setTime(dataInicial);
		Calendar cFinal = Calendar.getInstance();
		cFinal.setTime(dataFinal);
	
		float diferenca = cFinal.getTimeInMillis() - cInicial.getTimeInMillis();
	
		// Quantidade de milissegundos em um dia
		int tempoDia = 1000 * 60 * 60 * 24;
	
		 Float diasDiferenca = diferenca / tempoDia;
	
		return diasDiferenca;
	}

	/**
	 * Método que verifica se um dado objeto é um Byte
	 * 
	 * @param param
	 * @return Boolean
	 */
	public static Boolean isNumeroByte(Object param) {
		Boolean retorno = false;
		String numero = null;
		if(param!=null){
			numero = param.toString();
		}
		if (StringUtils.isNotBlank(numero) && StringUtils.isNumeric(numero)) {
			try {
				Byte.parseByte(numero);
				retorno = true;
			} catch (NumberFormatException e) {
				retorno = false;
			}
		}
		return retorno;
	}

	public static String formataCEP(Object cep) {
		if (StringUtils.isNumeric(cep.toString())) {
			StringBuilder sb = new StringBuilder(cep.toString());
	
			while (sb.length() < 8) {
				// Adiciona 0 a esquerda até da o número de caracteres correto
				sb.insert(0, '0');
			}
	
			// Adiciona a máscara
			sb.insert(5, '-');
			sb.insert(2, '.');
	
			return sb.toString();
		} else {
			return null;
		}
	}

	/**
	 * Método utilizado para retirar caracteres especiais de textos. Algumas
	 * rotinas do sistema utilizam HQL e não podem enviar ao banco de dados
	 * alguns caracteres, senão isso pode gerar erros
	 * 
	 * @param texto
	 * @return texto sem caracteres especiais que causam problemas ao BD
	 */
	public static String retirarCaracteresInvalidos(String texto) {
		StringBuilder retorno = new StringBuilder("");
		if (texto != null) {
			String caracteresInvalidos = "'%\"";
	
			for (int i = 0; i < texto.length(); i++) {
				Character c = texto.charAt(i);
				if (caracteresInvalidos.indexOf(c.toString()) > -1) {
					continue;
				} else {
					retorno.append(c.toString());
				}
			}
		}
		return retorno.toString();
	}

	/**
	 * Método para verificar se objetos do tipo Date estão em um intervalo.
	 * Comportamento similar ao comando BETWEEN do SQL.
	 * 
	 * Se (dataBase for maior ou igual a dataComparacao1 e dataBase for menor ou
	 * igual que dataComparacao2) então retorna TRUE.
	 * 
	 * @param dataBase
	 * @param dataComparacao1
	 * @param dataComparacao2
	 * @return
	 */
	public static boolean isBetweenDatas(Date dataBase, Date dataComparacao1, Date dataComparacao2) {
		boolean retorno = false;
	
		if (dataBase != null && dataComparacao1 != null
				&& dataComparacao2 != null) {
	
			if (dataBase.before(dataComparacao1)
					|| dataBase.after(dataComparacao2)) {
				retorno = false;
			} else {
				retorno = true;
			}
		}
	
		return retorno;
	}

	/**
	 * ORADB Function AGHK_UTIL.MODIFICADOS.<br/>
	 * 
	 * Implementa a função modificados da package aghk_util.<br/>
	 * Obs.: A classe dos objetos informados por parâmetro deve implementar o
	 * método equals.
	 * 
	 * @param <T>
	 *            Tipo genérico para indicar que os valores informados por
	 *            parâmetro devem ser do mesmo tipo.
	 * @param newValue
	 *            Valor novo.
	 * @param oldValue
	 *            Valor antigo.
	 * @return Booleano indicando se o valor foi modificado, ou seja, se os
	 *         valores dos parâmetros são diferentes.
	 * @author Marcelo Tocchetto
	 */
	public static <T> Boolean modificados(T newValue, T oldValue) {
		Boolean result = Boolean.FALSE;
	
		if (newValue != null && oldValue != null) {
			result = !newValue.equals(oldValue);
		} else if (newValue != null || oldValue != null) {
			result = Boolean.TRUE;
		}
	
		return result;
	}
	
	/**
	 * Método para verificar de datas foram modificados, considerando comparações entre timestamps e dates.
	 * 
	 * @param newValue
	 * @param oldValue
	 * @return
	 */
	public static Boolean modificados(Date newValue, Date oldValue) {
		Boolean result = Boolean.FALSE;	
		if (newValue != null && oldValue != null) {
			if (newValue instanceof Timestamp) {
				result = !oldValue.equals(newValue);
			} else {
				result = !newValue.equals(oldValue);
			}
		} else if (newValue != null || oldValue != null) {
			result = Boolean.TRUE;
		}
	
		return result;
	}

	public static String formataCPF(Long cpfParam) {
		if (cpfParam != null && StringUtils.isNumeric(cpfParam.toString())) {
			String cpf = StringUtils.leftPad(cpfParam.toString(), 11, "0");
	
			StringBuffer sb = new StringBuffer();
			sb.append(cpf.substring(0, 3));
			sb.append('.');
			sb.append(cpf.substring(3, 6));
			sb.append('.');
			sb.append(cpf.substring(6, 9));
			sb.append('-');
			sb.append(cpf.substring(9));
	
			return sb.toString();
		} else {
			return null;
		}
	}
	
	public static String acrescentarEspacos(Integer numEspacos) {
        StringBuilder retorno = new StringBuilder("");
        for (int i=0;i<numEspacos;i++){
             retorno.append("&nbsp;");
        }
        return retorno.toString();
	}

	/**
	 * O SO é Windows?
	 * 
	 * @return
	 */
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);
	}

	/**
	 * Método que retorna o dia da semana por extenso.
	 * 
	 * @param data
	 * @return DominioDiaSemana
	 */
	public static DominioDiaSemana retornaDiaSemana(Date data) {
		 Calendar calendar = Calendar.getInstance();  
		 calendar.setTime(data);
		 
		 DominioDiaSemana diaSemana;
		 Integer diaSemanaInt = calendar.get(Calendar.DAY_OF_WEEK);
	
		 switch (diaSemanaInt) {
		 	case 1 : diaSemana = DominioDiaSemana.DOMINGO; break;
		 	case 2 : diaSemana = DominioDiaSemana.SEGUNDA; break;
		 	case 3 : diaSemana = DominioDiaSemana.TERCA; break;
		 	case 4 : diaSemana = DominioDiaSemana.QUARTA; break;
		 	case 5 : diaSemana = DominioDiaSemana.QUINTA; break;
		 	case 6 : diaSemana = DominioDiaSemana.SEXTA; break;
		 	case 7 : diaSemana = DominioDiaSemana.SABADO; break;
		 	default: diaSemana = null; break;
		 }
		 return diaSemana;
	}

	/**
	 * Testa se o objeto <code>obj</code> é dierente de todos os objetos do array <code>objs</code>
	 * 
	 * @param obj
	 * @param objs
	 * @return
	 */
	public static Boolean notIn(Object obj, Object...objs) {
		return !in(obj, objs);
	}

	/**
	 * Testa se o objeto <code>obj</code> é dierente de todos os objetos da lista <code>objs</code>
	 * 
	 * @param obj
	 * @param objs
	 * @return
	 */
	public static Boolean notIn(Object obj, List<? extends Object> objs) {
		return !in(obj, objs);
	}

	/**
	 * Veriica se o número <code>a</code> é maior ou igual ao número <code>b</code>
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean maiorOuIgual(Number a, Number b) {
		return (igual(a, b) || maior(a, b));
	}

	/**
	 * Veriica se o número <code>a</code> é menor ou igual ao o número <code>b</code>
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean menorOuIgual(Number a, Number b) {
		return (igual(a, b) || menor(a, b));
	}

	/**
	 * Capitaliza texto no formato do AGHU: Primeira palavra de um texto
	 * capitalizada e as demais minúsculas.
	 * 
	 * @param string
	 *            texto de origem
	 * @return string texto formatado
	 */
	public static String capitalizaTextoFormatoAghu(String string) {
		if (StringUtils.isNotBlank(string)) {
			string = WordUtils.capitalize(string.toLowerCase(), new char[1]);
		}
		return string;
	}
	
	/**
	 * Método para verificar se data1 é maior ou igual data2.
	 * 
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static boolean isMaiorOuIgualDatas(Date data1, Date data2) {
		return data1 != null && data2 != null
				&& data1.getTime() >= data2.getTime();
	}
	
	/**
	 * Método para verificar se data1 é menor ou igual data2.
	 * 
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static boolean isMenorOuIgualDatas(Date data1, Date data2) {
		return data1 != null && data2 != null
				&& data1.getTime() <= data2.getTime();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void ordenarLista(List<?> lista, String propriedade, boolean asc) {
		class ComparadorGenerico implements Comparator<Object> {

			private String propriedade;

			public ComparadorGenerico(String propriedade) {
				this.propriedade = propriedade;
			}

			//@Override
			public int compare(Object o1, Object o2) {
				Comparable valorPropiedadePrimeiroQuarto = null;
				Comparable valorPropiedadeSegundoQuarto = null;
				try {
					try {
						valorPropiedadePrimeiroQuarto = (Comparable) PropertyUtils
								.getProperty(o1, this.propriedade);
					} catch (NestedNullException e) {
						// engole esta exceção. variável permanece null
						LOG.error(e.getMessage(), e);
					}

					try {
						valorPropiedadeSegundoQuarto = (Comparable) PropertyUtils
								.getProperty(o2, this.propriedade);
					} catch (NestedNullException e) {
						// engole esta exceção. variável permanece null
						LOG.error(e.getMessage(), e);
					}
				} catch (IllegalAccessException e) {
					LOG.error(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					LOG.error(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					LOG.error(e.getMessage(), e);
				}
				
				if (valorPropiedadePrimeiroQuarto != null && valorPropiedadeSegundoQuarto != null) {
					return valorPropiedadePrimeiroQuarto.compareTo(valorPropiedadeSegundoQuarto);
				} else if (valorPropiedadePrimeiroQuarto == null && valorPropiedadeSegundoQuarto == null) {
					return 0;
				} else {
					return valorPropiedadePrimeiroQuarto == null ? -1 : 1;
				}
			}
		}

		if (!StringUtils.isBlank(propriedade)) {
			Comparator<Object> comparadorGenerico = new ComparadorGenerico(propriedade);
			if (!asc) {
				comparadorGenerico = new ReverseComparator(comparadorGenerico);
			}
			Collections.sort(lista, comparadorGenerico);
		}
	}
	
	/**
	 * Método que verifica se um dado objeto é um Short
	 * 
	 * @param param
	 * @return Boolean
	 */
	public static Boolean isNumeroShort(Object param) {
		Boolean retorno = false;
		String numero = null;
		if (param != null) {
			numero = param.toString();
		}
		if (StringUtils.isNotBlank(numero) && StringUtils.isNumeric(numero)) {
			try {
				Short.parseShort(numero);
				retorno = true;
			} catch (NumberFormatException e) {
				retorno = false;
			}
		}
		return retorno;
	}
	
	/**
	 * Método que verifica se um dado objeto é um Integer
	 * 
	 * @param param
	 * @return Boolean
	 */
	public static Boolean isNumeroInteger(Object param) {
		Boolean retorno = false;
		String numero = null;
		if (param != null) {
			numero = param.toString();
		}

		if (StringUtils.isNotBlank(numero) && StringUtils.isNumeric(numero)) {
			try {
				Integer.parseInt(numero);
				retorno = true;
			} catch (NumberFormatException e) {
				retorno = false;
			}
		}
		return retorno;
	}

	/**
	 * Método que verifica se um dado objeto é um Long
	 * 
	 * @param param
	 * @return Boolean
	 */
	public static Boolean isNumeroLong(Object param) {
		Boolean retorno = false;
		String numero = null;
		if (param != null) {
			numero = param.toString();
		}

		if (StringUtils.isNotBlank(numero) && StringUtils.isNumeric(numero)) {
			try {
				Long.parseLong(numero);
				retorno = true;
			} catch (NumberFormatException e) {
				retorno = false;
			}
		}
		return retorno;
	}

	/**
	 * Valida parametros obrigatorios
	 * 
	 * @param object
	 */
	public static <T> void validaParametrosObrigatorios(T... param) {
		if (param == null || (param != null && param.length == 0)) {
			throw new IllegalArgumentException("Nenhum dos parâmetros obrigatórios foi informado");
		}
		for (T t : param) {
			if (t == null) {
				throw new IllegalArgumentException("Parâmetro obrigatório");
			}
		}

	}
	
	public static String formatarCNPJ(Long input) {
		try {
			DecimalFormat dc = new DecimalFormat("00000000000000");
			String numeroString = dc.format(input);
			MaskFormatter mf = new MaskFormatter("##.###.###/####-##");
			mf.setValueContainsLiteralCharacters(false);
			return mf.valueToString(numeroString);
		} catch (ParseException e) {
			LOG.error(e.getMessage());
			return input.toString();
		}
	}
	
	public static String formataProntuario(Object valor) {
		if (valor == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder(valor.toString());
		while (sb.length() < 8) {
			// Adiciona 0 a esquerda até da o número de caracteres correto
			sb.insert(0, '0');
		}
		// Adiciona a barra
		sb.insert(7, '/');

		return sb.toString();
	}

	public static String formataProntuarioRelatorio(Object valor) {
		if (valor == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder(valor.toString());
		while (sb.length() < 7) {
			// Adiciona 0 a esquerda até da o número de caracteres correto
			sb.insert(0, '0');
		}
		// Adiciona a barra
		sb.insert(sb.length() - 1, '/');

		return sb.toString();
	}

	/**
	 * Método que devolve um mês por extenso
	 * 
	 * @param mes
	 * @return String
	 */
	public static String obterMesPorExtenso(Integer mes) {
		String retorno = null;
		switch (mes) {
		case 1:
			retorno = "Janeiro";
			break;
		case 2:
			retorno = "Fevereiro";
			break;
		case 3:
			retorno = "Março";
			break;
		case 4:
			retorno = "Abril";
			break;
		case 5:
			retorno = "Maio";
			break;
		case 6:
			retorno = "Junho";
			break;
		case 7:
			retorno = "Julho";
			break;
		case 8:
			retorno = "Agosto";
			break;
		case 9:
			retorno = "Setembro";
			break;
		case 10:
			retorno = "Outubro";
			break;
		case 11:
			retorno = "Novembro";
			break;
		case 12:
			retorno = "Dezembro";
			break;
		}

		return retorno;
	}

	public static boolean igual(Object a, Object b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		if (a instanceof Number && b instanceof Number) {
			return ((Number) a).doubleValue() == ((Number) b).doubleValue();
		} else if (a instanceof Date && b instanceof Date) {
			return ((Date) a).getTime() == ((Date) b).getTime();
		}
		return a.equals(b);
	}
	
	/**
	 * Método para verificar se data1 é maior que data2.
	 * 
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static boolean isMaiorDatas(Date data1, Date data2) {
		return data1 != null && data2 != null
				&& data1.getTime() > data2.getTime();
	}

	/**
	 * Método para verificar se data1 é menor que data2.
	 * 
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static boolean isMenorDatas(Date data1, Date data2) {
		return data1 != null && data2 != null
				&& data1.getTime() < data2.getTime();
	}
	
	/**
	 * Método para valida se a competência (data no formato yyyyMM) é menor que uma competência informada
	 * @param data representará uma competência no formato yyyyMM
	 * @param competencia competência a ser testada
	 * @return
	 */
	public static boolean isMenorMesAno(final Date data, final Integer competencia){
		final SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyyMM");
		
		return menor(Integer.valueOf(sdf.format(data)), competencia);
	}
	
	/**
	 * Método para valida se a competência (data no formato yyyyMM) é menor ou igual que uma competência informada
	 * @param data representará uma competência no formato yyyyMM
	 * @param competencia competência a ser testada
	 * @return
	 */
	public static boolean isMenorIgualMesAno(final Date data, final Integer competencia){
		final SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyyMM");
		
		return menorOuIgual(Integer.valueOf(sdf.format(data)), competencia);
	}
	
	/**
	 * Método para calcular o número de anos entre duas datas através da
	 * subtração da dataFinal pela dataInicial.
	 * 
	 * @param dataFinal
	 * @param dataInicial
	 * @return
	 */
	public static Long calcularAnosEntreDatas(Date dataFinal, Date dataInicial) {
		Long anos = null;
		if (dataFinal != null && dataInicial != null
				&& dataFinal.after(dataInicial)) {
			Long timeMilliseconds = dataFinal.getTime() - dataInicial.getTime();
			anos = timeMilliseconds / 1000 / 60 / 60 / 24 / 365;
		}

		return anos;
	}

	/**
	 * Método para setar parametro dentro de uma callableStatement, verificando
	 * se é necessário setar o parametro por setNull ou pelo tipo de parametro.
	 * 
	 * @param cs
	 * @param indiceParametro
	 * @param tipoParametro
	 * @param valorParametro
	 */
	@SuppressWarnings("PMD.AvoidThreadGroup")
	public static void configurarParametroCallableStatement(
			CallableStatement cs, int indiceParametro, int tipoParametro,
			Object valorParametro) {
		try {
			if (valorParametro == null) {
				cs.setNull(indiceParametro, tipoParametro);
			} else {
				if (tipoParametro == Types.INTEGER) {
					if (valorParametro instanceof Integer) {
						cs.setInt(indiceParametro, (Integer) valorParametro);
					} else if (valorParametro instanceof Short) {
						cs.setShort(indiceParametro, (Short) valorParametro);
					} else if (valorParametro instanceof Byte) {
						cs.setByte(indiceParametro, (Byte) valorParametro);
					} else if (valorParametro instanceof Long) {
						cs.setLong(indiceParametro, (Long) valorParametro);
					} else if (valorParametro instanceof BigDecimal) {
						cs.setBigDecimal(indiceParametro,
								(BigDecimal) valorParametro);
					} else if (valorParametro instanceof BigInteger) {
						cs.setBigDecimal(indiceParametro, new BigDecimal(
								(BigInteger) valorParametro));
					}
				} else if (tipoParametro == Types.VARCHAR) {
					cs.setString(indiceParametro, (String) valorParametro);
				} else if (tipoParametro == Types.DATE) {
					Date data = (Date) valorParametro;
					cs.setDate(indiceParametro, new java.sql.Date(data.getTime()));
				} else if (tipoParametro == Types.TIMESTAMP) {
					Date data = (Date) valorParametro;
					cs.setTimestamp(indiceParametro, new java.sql.Timestamp(data.getTime()));
				}
			}
		} catch (Exception e) {
			LOG.error("Erro ao registrar parametro '" + indiceParametro
					+ "' para chamada de procedure/function de BD", e);
		}
	}

	/**
	 * Método para configurar mensagem a ser apresentada ao usuário e utilizada
	 * no LOG quando ocorrer alguma exception em casos de erro na execução de
	 * alguma procedure/function de banco de dados. É importante passar por
	 * parâmetro o nome completo do objeto do banco de dados se necessário
	 * (package + objeto).
	 * 
	 * @param nomeObjetoBD
	 * @param exception
	 * @param isMensagemLogger
	 *            (true para mensagens de LOG, false para mensagens para
	 *            usuário)
	 * @return mensagem
	 */
	public static String configurarMensagemUsuarioLogCallableStatement(
			String nomeObjetoBD, Exception e, boolean isMensagemLogger,
			String valores) {

		StringBuilder sb = new StringBuilder();
		String erro = null;
		if (e.getCause() != null) {
			erro = e.getCause().toString();
		} else {
			erro = e.toString();
		}
		int tamanhoErro = erro.length();

		if (isMensagemLogger) {
			sb.append("Erro ao executar procedimento do banco de dados ")
					.append(nomeObjetoBD)
					.append(". Contate o administrador do sistema. \n")
					.append("VALOR PARÂMETROS: ").append(valores)
					.append("\nERRO: ").append(erro).append('\n');
		} else {
			if (tamanhoErro <= 700) {
				sb.append(erro.substring(0, tamanhoErro));
			} else {
				sb.append(erro.substring(0, 700)).append("\n...");
			}
		}

		return sb.toString();
	}

	/**
	 * Método para configurar uma string com valor dos parâmetros que estão
	 * sendo enviados para uma chamada de CallableStatement.
	 * 
	 * @param valores
	 * @return
	 */
	public static String configurarValoresParametrosCallableStatement(
			Object... valores) {

		StringBuilder valorParametros = new StringBuilder();
		int count = 1;
		String valor = "";

		for (Object object : valores) {
			valor = object == null ? "null" : object.toString();

			if (count == 1) {
				valorParametros.append(count).append(": ").append(valor);
			} else {
				valorParametros.append(" , ").append(count).append(": ")
						.append(valor);
			}
			count++;
		}

		return valorParametros.toString();
	}

	/**
	 * Testa se o objeto <code>obj</code> é igual a algum objeto do array
	 * <code>objs</code>
	 * 
	 * @param obj
	 * @param objs
	 * @return
	 */
	public static Boolean in(Object obj, Object... objs) {
		return ArrayUtils.contains(objs, obj);
	}

	/**
	 * Testa se o objeto <code>obj</code> é igual a algum objeto da lista
	 * <code>objs</code>
	 * 
	 * @param obj
	 * @param objs
	 * @return
	 */
	public static Boolean in(Object obj, List<? extends Object> objs) {
		return in(obj,objs != null ? objs.toArray() : null);
	}
	
	/**
	 * Veriica se o número <code>a</code> é maior que o número <code>b</code>
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean maior(Number a, Number b) {
		if (a == null) {
			return false;
		}
		if (b == null) {
			return true;
		}
		return a.doubleValue() > b.doubleValue();
	}

	/**
	 * Veriica se o número <code>a</code> é menor que o número <code>b</code>
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean menor(Number a, Number b) {
		if (a == null && b == null) {
			return false;
		}
		if (b == null) {
			return false;
		}
		if (a == null) {
			return true;
		}
		return a.doubleValue() < b.doubleValue();
	}


	/**
	 * Retorna <code>a</code> se diferente de null, senão, retorna
	 * <code>b</code>
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Object nvl(Object a, Object b) {
		return a != null ? a : b;
	}

	/**
	 * Método para verificar se objetos do tipo Integer estao em um
	 * intervalo(Range).
	 * 
	 * @param numberComp
	 * @param rangeIni
	 * @param rangeFim
	 * @return
	 */
	public static boolean isBetweenRange(Integer numberComp, Integer rangeIni, Integer rangeFim) {
		boolean retorno = false;

		if (numberComp != null && rangeIni != null && rangeFim != null) {
			if (numberComp.intValue() >= rangeIni.intValue()
					&& numberComp.intValue() <= rangeFim.intValue()) {
				retorno = true;
			} else {
				retorno = false;
			}
		}

		return retorno;
	}

	// @ORADB : AGHC_MODULO_ONZE_COMPL
	public static Integer calculaModuloOnze(Long numero) {
		if (numero == null) {
			return null;
		}
		String num = numero.toString();

		int base = 9;
		int soma = 0;
		int fator = 2;
		
		String[] numeros, parcial;
		numeros = new String[num.length() + 1];
		parcial = new String[num.length() + 1];

		for (int i = num.length(); i > 0; i--) {
			numeros[i] = num.substring(i - 1, i);
			parcial[i] = String.valueOf(Integer.parseInt(numeros[i]) * fator);
			soma += Integer.parseInt(parcial[i]);
			if (fator == base) {
				fator = 2;
			} else {
				fator++;
			}
		}

		Integer mod = soma % 11;
		if (mod > 1) {
			mod = 11 - mod;
		} else {
			mod = 0;
		}

		return mod;
	}

	/**
	 * Método para obter a informação de um campo de determinado atributo
	 * 
	 * @param classeEntidade
	 * @param nomeAtributo
	 * @return
	 */
	public static String obterNomeCampoAtributo(
			Class<? extends Serializable> classeEntidade, String nomeAtributo) {
		Method metodoGet = null;
		Column anotacaoAtributo = null;
		StringBuffer nomeCampo = new StringBuffer();
		String primeiraLetra = nomeAtributo.substring(0, 1).toUpperCase(); 
		String restante = nomeAtributo.substring(1);

		try {
			metodoGet = classeEntidade.getMethod("get" + primeiraLetra
					+ restante, new Class<?>[] {});
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
			metodoGet = null;
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage(), e);
			metodoGet = null;
		}
		if (metodoGet != null && metodoGet.isAnnotationPresent(Column.class)) {
			anotacaoAtributo = metodoGet.getAnnotation(Column.class);
			nomeCampo.append(anotacaoAtributo.name());
		}

		return nomeCampo.toString();
	}

	/**
	 * Método para retornar "paginar" resultados que são ordenados em memória
	 * para grids que tem ordenação ao clicar nas suas colunas.
	 * 
	 * @param <T>
	 * @param firstResult
	 * @param maxResult
	 * @param lista
	 * @return Lista com registros que aparecerão na tela (normalmente 10
	 *         registros)
	 */
	public static <T> List<T> paginarListaResultadosMemoria(
			Integer firstResult, Integer maxResult, List<T> lista) {
		List<T> retornoList = new ArrayList<T>();

		if (lista != null && lista.size() > 0) {
			if (lista.size() <= maxResult) {
				retornoList = lista;
			} else {
				int j = firstResult + maxResult;
				// Caso a paginação tenha firstResult=20 e maxResult=10 (do
				// registro 20 ao 30), mas o tamanho da lista for de 26, então
				// ajusta a variável 'j' para iterar somente até o 26º elemento
				if (j > lista.size()) {
					j = lista.size();
				}
				for (int i = firstResult; i < j; i++) {
					retornoList.add(lista.get(i));
				}
			}
		}

		return retornoList;
	}
	
	/**
	 * Capitaliza texto no formato do AGHU: Primeira palavra de um texto
	 * capitalizada e as demais minúsculas concatenado a um código.
	 * 
	 * @param descricao
	 *            texto de origem
	 * @param codigo
	 *            codigo de origem
	 * @return texto formatado
	 */
	public static String capitalizaTextoFormatoAghusAcrescentaCodigo(
			String descricao, String codigo) {
		descricao = capitalizaTextoFormatoAghu(descricao);
		if (StringUtils.isNotBlank(descricao)) {
			if (StringUtils.isNotBlank(codigo)) { //NOPMD CollapsibleIfStatements
				descricao += " (" + codigo.toUpperCase() + ")";
			}
		}
		return descricao;
	}
	
	/**
	 * O SO é Linux? 
	 * @return
	 */
	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
	}

	/**
	 * Deserializa o arquivo
	 * 
	 * @param dados
	 * @return
	 */
	public static Object deserializarArquivo(byte[] dados) {
		ByteArrayInputStream is = null;
		ObjectInputStream oip = null;
		try {
			is = new ByteArrayInputStream(dados);
			oip = new ObjectInputStream(is);
			return oip.readObject();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(oip);
		}
		return null;
	}
	
	public static byte[] serializarArquivo(Object obj) {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			byte[] data = bos.toByteArray();
			return data;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(oos);
			IOUtils.closeQuietly(bos);
		}
		return null;
	}
	
	/**
	 * Converte uma instancia de File para um Array de Bytes
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] converterFileToArrayBytes(File file) {
		byte[] arrayBytes = new byte[(int) file.length()];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.read(arrayBytes);

		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
		return arrayBytes;
	}

	/** 
	 * Os parametros campoComparacao e listaId sao obrigatorios.
	 * 
	 * @param campoComparacao
	 * @param whereOrAnd
	 * @param listaId
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String criarClausulaIN(String campoComparacao, String whereOrAnd, List<Integer> listaId) {
		if (campoComparacao == null || "".equals(campoComparacao.trim())) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!");
		}
		if (whereOrAnd == null || "".equals(whereOrAnd.trim())) {
			whereOrAnd = " where ";
		}
		
		StringBuilder clausula = new StringBuilder();
		clausula.append(" ").append(whereOrAnd).append(" (");
		
		List<List<Integer>> listaIdQuebrada = quebrarEmListasDeMilUnidades(listaId);
		for (int i = 0; i < listaIdQuebrada.size(); i++) {
			List<Integer> list = listaIdQuebrada.get(i);
						
			if (i == 0) {
				clausula.append(campoComparacao).append(" in ");
			} else {
				clausula.append(" or ").append(campoComparacao).append(" in ");
			}
			
			String s = list.toString().replace('[', '(');
			s = s.replace(']', ')');
			
			clausula.append(s.trim());//.append('\n');
		}
		clausula.append(") ");
		
		return clausula.toString();
	}
	
	/**
	 * 
	 * @param listaId
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static List<List<Integer>> quebrarEmListasDeMilUnidades(List<Integer> listaId) {
		if (listaId == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!");
		}
		List<List<Integer>> listReturn = new LinkedList<List<Integer>>();
		
		if (listaId.size() > 1000) {
			List<List<Integer>> list = new LinkedList<List<Integer>>();
			
			List<Integer> listaMil = new LinkedList<Integer>();
			for (int i = 0; i < listaId.size(); i++) {
				listaMil.add(listaId.get(i));
				if ((i % 1000) == 0) {
					list.add(listaMil);
					listaMil = new LinkedList<Integer>();
				}			
			}
			if (listaMil != null && !listaMil.isEmpty()) {
				list.add(listaMil);
			}
			// Ajuste de listas para eleminar a primeira lista com um elemento.
			if (list.get(0).size() == 1 && list.get(list.size()-1).size() < 1000) {
				Integer elementoUnico = list.get(0).get(0);
				for (int index = 1; index < list.size(); index++) {
					List<Integer> definitivos = list.get(index);
					if (index == (list.size()-1)) {
						definitivos.add(elementoUnico);
					}
					listReturn.add(definitivos);
				}
			} else {
				listReturn = list;
			}
		} else if (!listaId.isEmpty()) {
			listReturn.add(listaId);
		}
		
		return listReturn;
	}
	
	public static void main(String[] args) {
		LOG.info("Teste ....");	
		String vMotivoCobranca = "62";
		Object[] val = {62,68};
		LOG.info(" -- "+in(Integer.valueOf(vMotivoCobranca), val));
	}

    
	/**
	 * Retorna a idade atual baseado na data de bascimento passado por parâmetro.
	 * 
	 * @param dataNascInput
	 * @return
	 */
	public static int calculaIdade(final Date dataNascInput) {
		final Calendar dateOfBirth = new GregorianCalendar();
		dateOfBirth.setTime(dataNascInput);
		// Cria um objeto calendar com a data atual
		final Calendar today = Calendar.getInstance();
		// Obtém a idade baseado no ano
		int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

		dateOfBirth.add(Calendar.YEAR, age);
		if (today.before(dateOfBirth)) {
			age--;
		}
		return age;
	}
	
	/**
	 * Representa a function LAST_DAY do oracle.
	 * Irá retornar o último dia do mês.
	 * 
	 * @param date data representando o mês a ser processado. Caso null instancia uma nova data.
	 * 
	 * @return Último dia do mês
	 */
	public static Date obterUltimoDiaDoMes(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		
		if(date != null){
			calendar.setTime(date);
			
		} else {
			calendar.setTime(new Date());
		}
		
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastDate);
		
		return calendar.getTime();
	}

	/**
	 * Calcula índice de massa corporal
	 * @param peso
	 * @param altura
	 * @return
	 */
	public static BigDecimal calculaImc(BigDecimal peso, BigDecimal altura) {
		if(peso != null && altura != null && !BigDecimal.ZERO.equals(peso) && !BigDecimal.ZERO.equals(altura)){
			return peso.divide(altura.pow(2), 2, RoundingMode.DOWN);
		}else{
			return null;
		}
	}

	/*
	 * Metódos para validação de Constraints
	 */
	public static void validarConstraint(PersistenceException e,
			Map<String, BusinessExceptionCode> mapConstraintNameCode)
			throws ApplicationBusinessException {
		for (Entry<String, BusinessExceptionCode> entry : mapConstraintNameCode
				.entrySet()) {
			String constraint = entry.getKey();
			BusinessExceptionCode code = entry.getValue();
			validarConstraint(e, constraint, code);
		}
	}

	/*
	 * Metódos para validação de Constraints
	 */
	public static void validarConstraint(PersistenceException e,
			String constraint, BusinessExceptionCode code)
			throws ApplicationBusinessException {
		Throwable cause = e.getCause();
		if (cause != null) {
			if (cause instanceof ConstraintViolationException) {
				if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), constraint)) { //NOPMD CollapsibleIfStatements
					throw new ApplicationBusinessException(code);
				}
			}

			cause = ExceptionUtils.getRootCause(e);
			if (cause != null) {
				String message = cause.getMessage();
				if (message != null
						&& StringUtils.containsIgnoreCase(message, constraint)) {
					throw new ApplicationBusinessException(code);
				}
			}
		}
	}

	/**
	 * Desatcha objeto mantendo os que é enviado como parametro atachado.
	 * Lembrar de acessar os atributos que serão acessados antes de invocar o método.
	 * Ex: obj.getLista();
	 * 
	 * @param <T>
	 * @param bean
	 * @return
	 * @throws ApplicationBusinessExceptionSemRollback
	 */
	@SuppressWarnings({"unchecked", "PMD.AvoidCatchingNPE"})
	public static <T> T cloneBean(T bean) throws ApplicationBusinessException {
		//FIXME
		return null;
		/*
		try {
			Object objCopy = BeanUtils.cloneBean(bean);
			new AGHUGenericDAO<Object>() {

				private static final long serialVersionUID = -4091136125102370875L;}.desatachar(objCopy);
			return (T) objCopy;
		} catch (IllegalAccessException e) {
			throw new ApplicationNegocioException(ApplicationBusinessExceptionCode.NAO_FOI_POSSIVEL_CLONAR_BEAN);
		} catch (InstantiationException e) {
			throw new ApplicationNegocioException(ApplicationBusinessExceptionCode.NAO_FOI_POSSIVEL_CLONAR_BEAN);
		} catch (InvocationTargetException e) {
			throw new ApplicationNegocioException(ApplicationBusinessExceptionCode.NAO_FOI_POSSIVEL_CLONAR_BEAN);
		} catch (NoSuchMethodException e) {
			throw new ApplicationNegocioException(ApplicationBusinessExceptionCode.NAO_FOI_POSSIVEL_CLONAR_BEAN);
		}catch (NullPointerException e) { //Para quando o objeto não estpa atachado ou instanciado vazio
			return bean;
		}*/
	}
	
	/**
	 * Desatcha lista de objetos mantendo os que são enviados como parametros atachados.
	 * @param <T>
	 * @param listaBean
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("rawtypes")
	public static <T> List cloneBean (List<T> listaBean) throws ApplicationBusinessException {
		List<T> listaDesatachada = new ArrayList<T>();
		for(T obj : listaBean){
			listaDesatachada.add(cloneBean(obj));
		}
		return listaDesatachada;
	}

	/**
	 * Método que efetua a validação das iniciais do Paciente, devendo obedecer
	 * a seguinte regra:<br />
	 * 
	 * Informar apenas caracteres SEPARADOS por vírgula
	 */
	public static boolean validaIniciaisPaciente(final String iniciaisPaciente)
			throws ApplicationBusinessException {
		if (iniciaisPaciente != null) {

			if (!StringUtils.isBlank(iniciaisPaciente)
					&& !Pattern.compile("([a-zA-Z]{1},?)+")
							.matcher(iniciaisPaciente).matches()) {
				CoreUtilExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS
						.throwException();
			}

			boolean nextVirgula = false;
			int length = iniciaisPaciente.toCharArray().length;
			int pos = -1;
			for (char letra : iniciaisPaciente.toCharArray()) {
				pos++;
				if (!nextVirgula) {
					nextVirgula = (letra >= 65 && letra <= 122);

				} else {
					if (letra >= 65 && letra <= 122 && pos < length) {
						CoreUtilExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.throwException();
					} else {
						if (pos == (length - 1) && letra == ',') {
							CoreUtilExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.throwException();
						}
						nextVirgula = false;
					}
				}
			}
		}

		return true;
	}

	public static void validaDifencaLimiteDiasParaRelatorio(Date dataInicio,
			Date dataFim, Integer limiteDias) throws ApplicationBusinessException {
		Integer difDiasInformado = DateUtil.calcularDiasEntreDatas(dataInicio,
				dataFim);
		if (difDiasInformado > limiteDias) {
			throw new ApplicationBusinessException(
					CoreUtilExceptionCode.MENSAGEM_LIMITE_DIAS_RELATORIO,
					limiteDias,
					DateFormatUtil.dataToString(dataInicio, "dd/MM/yyyy"),
					DateFormatUtil.dataToString(dataFim, "dd/MM/yyyy"),
					difDiasInformado);
		}
	}

	/**
	 * NÃO UTILIZAR ESTE MÉTODO E SIM O MÉTODO validarEnderecoIPv4(String enderecoIPv4) EM MECController
	 */
	@Deprecated
	public static boolean validarEnderecoIPv4(String enderecoIPv4) {
		LOG.warn("NÃO UTILIZAR ESTE MÉTODO E SIM O MÉTODO validarEnderecoIPv4(String enderecoIPv4) EM MECController");
        Pattern pattern = Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");   
         
        Matcher matcher = pattern.matcher(enderecoIPv4);
        return matcher.find() && matcher.group().equals(enderecoIPv4);  		
	}
	
	/**
	 * Realiza a troca de caracteres especiais por caracteres de escape
	 * da URL passada como parâmetro
	 * 
	 * @param url uma string qualquer que será usada para fazer chamada via URL
	 * @return string com caracteres especiais trocados por escape caso a URL de entrada tenha algum
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeURL(String url) throws UnsupportedEncodingException {
		return URLEncoder.encode(url, "UTF-8");
    }

	/**
	 * Realiza a troca de caracteres de escape por caracteres especiais
	 * da URL passada como parâmetro
	 * 
	 * @param url uma string qualquer vinda de uma chamada por URL ao AGHU
	 * @return string com caracteres de escape trocados por especiais caso a URL de entrada tenha algum
	 * @throws UnsupportedEncodingException
	 */	
	public static String decodeURL(String url) throws UnsupportedEncodingException {	
        return URLDecoder.decode(url, "UTF-8");
    }
	
	public static String rtfToHtml(String resultado)  {
		
		StringReader reader = new StringReader(resultado);
		JEditorPane p = new JEditorPane();
		p.setContentType("text/rtf");
		EditorKit kitRtf = p.getEditorKitForContentType("text/rtf");
		try {
			kitRtf.read(reader, p.getDocument(), 0);
			kitRtf = null;
			HTMLEditorKit kitHtml = (HTMLEditorKit) p
					.getEditorKitForContentType(MIME_TYPE_TEXT_HTML);
			Writer writer = new StringWriter();
			kitHtml.write(writer, p.getDocument(), 0, p.getDocument()
					.getLength());
			return writer.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * @ORADB FUNCTION AGHC_CONV_RTF_TXT.
	 * 
	 * @param strRTF
	 * @return
	 */
	public static String converterRTF2Text(String strRTF) {
		if (strRTF == null || "".equals(strRTF.trim())) {
			return null;
		}
		
		if (strRTF.charAt(0) != '{') {
			return strRTF;
		}
		
		try {
			StringReader reader = new StringReader(strRTF);
			DefaultStyledDocument styledDoc = new DefaultStyledDocument();
			RTFEditorKit rtfKit = new RTFEditorKit();
			rtfKit.read(reader, styledDoc, 0);
			
			Document doc = styledDoc.getDefaultRootElement().getDocument();
			
			String text = doc.getText(0, doc.getLength());
			return (text != null) ? text.trim() : null;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;
		} catch (BadLocationException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}		
	}
	
	
	/**
	 * Método para extrair texto de tags HTML (utiliza API Jericho). Será
	 * realizado um parsing e caso a string passada conter código HTML o texto será
	 * separado das tags. Caso a string informada não seja HTML, apenas retorna a
	 * string informada.
	 * 
	 * @param strHtml
	 * @return String contendo somente texto (sem tags HTML) ou a string
	 *         informada se esta não conter tags HTML.
	 */
	public static String extrairTextoDeHtml(String strHtml) {
		StringReader reader = new StringReader(strHtml);
		Source source = null;
		
		try {
			source = new Source(reader);
			if (!source.getAllTags().isEmpty()) {
				return source.getTextExtractor().setIncludeAttributes(false).toString();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		
		return strHtml;
	}
//	
	/**
	 * Método que concatena uma string no inicio ou no final de outra string
	 * @param texto
	 * @param inicio
	 * @param fim
	 * @return
	 */
	public static String concatenarIsNotEmpty(String texto, String inicio, String fim) {
		StringBuilder sb = new StringBuilder();
		String retorno = null;
		if (texto != null) {
			if (StringUtils.isNotEmpty(inicio)) {
				sb.append(inicio);
			}
			sb.append(texto);
			if (StringUtils.isNotEmpty(fim)) {
				sb.append(fim);
			}
		}
		if (sb.length() > 0) {
			retorno = sb.toString();
		}
		return retorno;
	}

	private static final Pattern CPF_PATTERN = Pattern.compile("[0-9]{11}");
	public static boolean validarCPF(String value) {
        boolean valid;
        int soma1, soma2, d1, d2;
        
        value = value.trim().replace(".", "").replace("-", "");
        
        value = StringUtils.leftPad(value, 11, "0");
        if (value.equals("00000000000") || value.equals("11111111111") 
        		|| value.equals("22222222222") || value.equals("33333333333")
        		|| value.equals("44444444444") || value.equals("55555555555") 
        		|| value.equals("66666666666") || value.equals("77777777777") 
        		|| value.equals("88888888888") || value.equals("99999999999")
        		|| value.equals("00000000191")){
            return false;
        }

        valid = CPF_PATTERN.matcher(value).matches();

        if (valid) {
            soma1 = soma2 = 0;
            for (int i = 0; i < 9; i++) {
                d1 = value.charAt(i) - '0';
                soma1 += d1 * (10 - i);
                soma2 += d1 * (11 - i);
            }

            d1 = soma1 % 11;
            if (d1 < 2) {
                d1 = 0;
            } else {
                d1 = 11 - d1;
            }

            soma2 += d1 * 2;
            d2 = soma2 % 11;
            if (d2 < 2) {
                d2 = 0;
            } else {
                d2 = 11 - d2;
            }

            valid = (value.charAt(9) - '0') == d1 && (value.charAt(10) - '0') == d2;
        }
        return valid;
    }

	private static final Pattern CNPJ_PATTERN = Pattern.compile("[0-9]{14}");
	public static boolean validarCNPJ(String value) {
		boolean valid;
		int soma1, soma2, d1, d2, i, j, k, c;

		value = value.trim().replace(".", "").replace("-", "").replace("/", "");
		
		value = StringUtils.leftPad(value, 14, "0");
		
		valid = CNPJ_PATTERN.matcher(value).matches();

		if (valid) {
			soma2 = soma1 = 0;
			for (i = 11, j = 2, k = 3; i >= 0; i--) {
				c = value.charAt(i) - '0';
				soma1 += c * j;
				soma2 += c * k;
				j = (j + 1) % 10;
				if (j == 0) {
					j = 2;
				}
				k = (k + 1) % 10;
				if (k == 0) {
					k = 2;
				}
			}

			d1 = soma1 % 11;
			if (d1 < 2) {
				d1 = 0;
			} else {
				d1 = 11 - d1;
			}

			soma2 += d1 * 2;
			d2 = soma2 % 11;
			if (d2 < 2) {
				d2 = 0;
			} else {
				d2 = 11 - d2;
			}

			valid = value.charAt(12) - '0' == d1
					&& value.charAt(13) - '0' == d2;
		}
		return valid;
	}
	
}
