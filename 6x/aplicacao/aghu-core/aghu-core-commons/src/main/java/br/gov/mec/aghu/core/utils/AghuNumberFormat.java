package br.gov.mec.aghu.core.utils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe utiliária para a formatação de números.
 * 
 * @author rcorvalao
 *
 */
public class AghuNumberFormat {

	private static final Log LOG = LogFactory.getLog(AghuNumberFormat.class);

	/**
	 * Formata o valor de acordo com a mascara informada, utilizando locale "pt,BR".
	 * 
	 * @param valor
	 * @param mascara
	 * @return
	 */
	public static String formatarValor(Number valor, String mascara) {
		Locale locale = new Locale("pt","BR");
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locale);
		DecimalFormat dcFormat = new DecimalFormat(mascara, dfSymbols);
		
		return dcFormat.format(valor);		
	}
	
	private static String leftNumberMask(final String str, int precision, char padChar) {
		StringBuffer strReturned = new StringBuffer();
		if (str != null) {
			strReturned.append(str);
		}
		
		for (int i=strReturned.length()+1; i<=precision; i++) {
			strReturned.insert(0, padChar);
			if ((i % 3) == 0) {
				strReturned.insert(0, ',');
			}
		}
		
		return strReturned.toString();
	}

	/**
	 * Formata o valor informado com base na anotação "column" do campo, gerando
	 * uma mascara e a partir dos valores definidos nos atributos "precision" e
	 * "scale" do método getter do campo.
	 * Este método utiliza o locale "pt,BR" ao formatar o valor.
	 * 
	 * @param valor
	 * @param mascara
	 * @return
	 * @throws NoSuchMethodException
	 *             Exceção lançada caso o método getter do campo não seja
	 *             encontrado.
	 * @throws SecurityException
	 *             Exceção lançada caso não seja possível acessar o método
	 *             getter do campo informado devido a restrições de segurança.
	 */	
	public static String formatarValor(Number valor, Class<?> classe, String campo) {
		if (valor == null || classe == null || StringUtils.isBlank(campo)) {
			throw new IllegalArgumentException("Parametro invalidos!!!");
		}
		
		String valorFormatado = "";
		String precisionMask = "#0";
		String scaleMask = "";
		
		Method method = AghuNumberFormat.getMethod(classe, campo);
		Column column = method.getAnnotation(Column.class);
		
		int precision = column.precision();
		int scale = column.scale();

		if (scale > 0) {
			scaleMask = StringUtils.rightPad(scaleMask, scale, "#");
			precision = precision - scale;
		}
		
		if (precision > 2) {
			precisionMask = leftNumberMask(precisionMask, precision, '#');
		}
		
		String mascara = precisionMask + (scale > 0 ? "." + scaleMask : "");
		valorFormatado = formatarValor(valor, mascara);

		return valorFormatado;
	}

	private static Method getMethod(Class<?> classe, String campo) {		
		String methodGet = "get" + campo.toUpperCase().charAt(0) + campo.substring(1);
		Method method = null;
		try {
			method = classe.getDeclaredMethod(methodGet);
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage(), e);
		}
		return method;
	}
	
	

	//- K para represenatr milhares. Ex: 127.000 = 127k
	//- M para representar milhões. Ex: 127.300.000 = 127,3M
	public static String abreviarValorMonetario(Integer valor) {
		return abreviarValorMonetario(valor, Boolean.TRUE);
	}

	/**
	 * Metodo  que abrevia e formada o valor numerico.
	 * @param valor - valor a ser abreviado.
	 * @param abreviarValorMilhar - booleano onde se verdadeiro, 
	 * 								valores entre 1000 a 999999 será abreviado. 
	 * 								Nos demais casos valores iguais ou maior 1000000
	 * 								será abreviado.
	 * 
	 * K para representar milhares. Ex:     127.000 = 127k
	 * M para representar milhões.  Ex: 127.300.000 = 127,3M
	 * @return
	 */
	public static String abreviarValorMonetario(Integer valor, Boolean abreviarValorMilhar) {
		
		String valorFormatado = "";
		if (abreviarValorMilhar != null && abreviarValorMilhar) {
			if(valor < 1000) {
				valorFormatado = String.valueOf(valor);
			} else {
				int mod;	
				int qtdZeros;
				int valorBaseDivisao;
				int valorBaseQtdZeros;
				String metrica;
				String difModulo;
				String modStr;
				String separador = ",";
				
				if(valor > 999999){
					valorBaseDivisao = 1000000;
					valorBaseQtdZeros = 6;
					metrica = "M";
				}else{
					valorBaseDivisao = 1000;
					valorBaseQtdZeros = 3;
					metrica = "k";
				}			
				mod = (valor % valorBaseDivisao);
				difModulo = String.valueOf(valorBaseDivisao - mod);
				modStr = String.valueOf(mod);
				qtdZeros = difModulo.length()-modStr.length();
				
				if(qtdZeros < valorBaseQtdZeros) {
					valorFormatado = valor / valorBaseDivisao + separador + gerarZeros(qtdZeros) + modStr.charAt(0)  + metrica;
				} else{
					valorFormatado = valor / valorBaseDivisao + metrica;
				}			
			}
		} else {
			if(valor < 1000000) {
				valorFormatado = String.valueOf(valor);
			} else {
				int mod;	
				int qtdZeros;
				int valorBaseDivisao;
				int valorBaseQtdZeros;
				String metrica;
				String difModulo;
				String modStr;
				String separador = ",";
				
				valorBaseDivisao = 1000000;
				valorBaseQtdZeros = 6;
				metrica = "M";
				
				mod = (valor % valorBaseDivisao);
				difModulo = String.valueOf(valorBaseDivisao - mod);
				modStr = String.valueOf(mod);
				qtdZeros = difModulo.length()-modStr.length();
				
				if(qtdZeros < valorBaseQtdZeros) {
					valorFormatado = valor / valorBaseDivisao + separador + gerarZeros(qtdZeros) + modStr.charAt(0)  + metrica;
				} else{
					valorFormatado = valor / valorBaseDivisao + metrica;
				}			
			}
		}
		return valorFormatado;
	}
	
	
	private static String gerarZeros(int qtd){
        StringBuffer x = new StringBuffer();
        for(int i=0; i< qtd; i++){
                x.append('0');
        }
        return x.toString();
	}
	
	/**
	 * Método para formatar um Number em uma String com o separador decimal
	 * correto para aparecer em relatórios, outputs etc.
	 * 
	 * @param valor
	 * @return
	 */
	public static String formatarNumeroMoeda(Number valor) {
		if (valor == null) {
			valor = BigDecimal.ZERO;
		}
		
		Locale loc = new Locale("pt", "BR");
		NumberFormat nb = NumberFormat.getInstance(loc);
		nb.setMinimumFractionDigits(2);
		nb.setMaximumFractionDigits(2);
	
		return nb.format(valor);
	}
	
	/**
	 * Método para formatar um Number em uma String com o separador decimal
	 * correto para aparecer em relatórios, outputs etc.
	 * 
	 * Valores negativos sao apresentados entre parentesis.
	 * 
	 * @param valor
	 * @return
	 * 
	 * @see AghuNumberFormat#formatarNumeroMoeda(Number)
	 */
	public static String formatarNumeroMoedaAlternativo(Number valor) {
		String s = formatarNumeroMoeda(valor);
		
		if (s != null && s.charAt(0) == '-') {
			s = s.substring(1);
			s = MessageFormat.format("({0})", s);
		}
		
		return s;
	}

	
}
