package br.gov.mec.aghu.core.commons;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Classe utilizada para auxiliar na conversão de tipos numéricos.
 * 
 * @author bsoliveira - 29/10/2010
 * 
 */
public class NumberUtil {

	/**
	 * Converte um numero de tipo primitivo int para um objeto Short.
	 * 
	 * bsoliveira 29/10/2010
	 * 
	 * @param value
	 * @return uma numero Short convertido.
	 */
	public static Short obterShort(int value) {

		return Short.valueOf(String.valueOf(value));

	}

	/**
	 * Compara tipo Integer para ver se é igual. Este método é null-safe
	 * 
	 * @param value
	 * @return um booleano que indica se os valores são iguais.
	 */
	public static boolean equals(Integer value1, Integer value2) {

		String value1Str = value1 == null ? "null" : value1.toString();
		String value2Str = value2 == null ? "null" : value2.toString();

		return StringUtils.equals(value1Str, value2Str);

	}

	/**
	 * Obtem Short a partir de um objeto numérico: Short, BigDecimal, Integer,
	 * Long, BigInteger, Long, Byte e Double.
	 * 
	 * @param numObj
	 * @return
	 */
	public static Short getShortFromNumericObject(Object numObj) {

		if (numObj instanceof Short) {
			return (Short) numObj;
		} else if (numObj instanceof BigDecimal) {
			return ((BigDecimal) numObj).shortValue();
		} else if (numObj instanceof Integer) {
			return ((Integer) numObj).shortValue();
		} else if (numObj instanceof BigInteger) {
			return ((BigInteger) numObj).shortValue();
		} else if (numObj instanceof Long) {
			return ((Long) numObj).shortValue();
		} else if (numObj instanceof Byte) {
			return ((Byte) numObj).shortValue();
		} else if (numObj instanceof Double) {
			return ((Double) numObj).shortValue();
		}

		return (Short) numObj;
	}

	/**
	 * Obtem Double a partir de um objeto numérico: Short, BigDecimal, Integer,
	 * Long, BigInteger, Long, Byte e Double.
	 * 
	 * @param numObj
	 * @return
	 */
	public static Double getDoubleFromNumericObject(Object numObj) {

		if (numObj instanceof Short) {
			return ((Short) numObj).doubleValue();
		} else if (numObj instanceof BigDecimal) {
			return ((BigDecimal) numObj).doubleValue();
		} else if (numObj instanceof Integer) {
			return ((Integer) numObj).doubleValue();
		} else if (numObj instanceof BigInteger) {
			return ((BigInteger) numObj).doubleValue();
		} else if (numObj instanceof Long) {
			return ((Long) numObj).doubleValue();
		} else if (numObj instanceof Byte) {
			return ((Byte) numObj).doubleValue();
		} else if (numObj instanceof Double) {
			return (Double) numObj;
		}

		return (Double) numObj;
	}

	/**
	 * Obtem Integer a partir de um objeto numérico: Short, BigDecimal, Integer,
	 * Long, BigInteger, Long, Byte e Double.
	 * 
	 * @param numObj
	 * @return
	 */
	public static Integer getIntegerFromNumericObject(Object numObj) {

		if (numObj instanceof Short) {
			return ((Short) numObj).intValue();
		} else if (numObj instanceof BigDecimal) {
			return ((BigDecimal) numObj).intValue();
		} else if (numObj instanceof Integer) {
			return (Integer) numObj;
		} else if (numObj instanceof BigInteger) {
			return ((BigInteger) numObj).intValue();
		} else if (numObj instanceof Long) {
			return ((Long) numObj).intValue();
		} else if (numObj instanceof Byte) {
			return ((Byte) numObj).intValue();
		} else if (numObj instanceof Double) {
			return ((Double) numObj).intValue();
		}

		return (Integer) numObj;
	}

	/**
	 * Converte uma string com um separador para um array de numbers (de um
	 * determinado tipo)
	 * 
	 * @param val
	 *            - valor a ser convertido
	 * @param regex
	 *            - separador
	 * @param type
	 *            - tipo a ser posto em number
	 * @author Eduardo Giovany Schweigert
	 */
	public static Number[] convertStringToArray(final String val,
			final String regex, final Object type) {
		final String[] vals = val.split(regex);

		final Number[] result = new Number[vals.length];

		for (int i = 0; i < vals.length; i++) {

			if (type instanceof Byte) {
				result[i] = Byte.valueOf(vals[i]);

			} else if (type instanceof Double) {
				result[i] = Double.valueOf(vals[i]);

			} else if (type instanceof Float) {
				result[i] = Float.valueOf(vals[i]);

			} else if (type instanceof Integer) {
				result[i] = Integer.valueOf(vals[i]);

			} else if (type instanceof Long) {
				result[i] = Long.valueOf(vals[i]);

			} else if (type instanceof Short) {
				result[i] = Short.valueOf(vals[i]);
			}
		}

		return result;
	}

	/**
	 * Método Utilizado para truncar valores. Exemplo de utilização: Classe
	 * SceEstoqueGeral, para cálculos do módulo de estoque.
	 * 
	 * @param valor
	 *            - valor para ser truncado
	 * @param casas
	 *            - quantidade de casas decimais
	 * @return
	 */
	public static BigDecimal truncate(BigDecimal valor, int casas) {

		if (valor != null) {

			int index = valor.toString().lastIndexOf(".");

			if (index != -1) {
				String subStringValor = valor.toString().substring(index)
						.replace(".", "");

				if (subStringValor.length() > casas) {

					valor = valor.setScale(casas, RoundingMode.DOWN);
				}

			}

		}
		return valor;

	}

	/**
	 * Método Utilizado para truncar valores. Exemplo de utilização: Classe
	 * SceEstoqueGeral, para cálculos do módulo de estoque.
	 * 
	 * @param valor
	 *            - valor para ser truncado
	 * @param casas
	 *            - quantidade de casas decimais
	 * @return
	 */
	public static Double truncate(Double valor, int casas) {

		if (valor != null) {

			int index = valor.toString().lastIndexOf(".");

			if (index != -1) {
				String subStringValor = valor.toString().substring(index)
						.replace(".", "");

				if (subStringValor.length() > casas) {

					valor = new BigDecimal(valor).setScale(casas,
							RoundingMode.DOWN).doubleValue();
				}

			}

		}

		return valor;

	}

	/**
	 * Método Utilizado para truncar valores. Exemplo de utilização: Classe
	 * SceEstoqueGeral, para cálculos do módulo de estoque.
	 * 
	 * @param valor
	 *            - valor para ser truncado
	 * @param casas
	 *            - quantidade de casas decimais
	 * @return
	 */
	public static Double truncateHALFEVEN(Double valor, int casas) {

		if (valor != null) {

			int index = valor.toString().lastIndexOf(".");

			if (index != -1) {
				String subStringValor = valor.toString().substring(index)
						.replace(".", "");

				if (subStringValor.length() > casas) {

					valor = new BigDecimal(valor).setScale(casas,
							RoundingMode.HALF_EVEN).doubleValue();

				}

			}

		}
		return valor;

	}

	/**
	 * Método Utilizado para truncar valores. 
	 * Exemplo de utilização: Classe SceEstoqueGeral, para cálculos do módulo de estoque.
	 * @param valor - valor para ser truncado
	 * @param casas - quantidade de casas decimais 
	 * @return
	 */
	public static BigDecimal truncateHALFEVEN(BigDecimal valor, int casas){
		
		if (valor != null){
			
			Double valorToDouble = truncateHALFEVEN(valor.doubleValue(), casas);
			
			return new BigDecimal(valorToDouble.toString());

		}

		return valor;

	}
	

	
	/**
	 * Método Utilizado para truncar valores. 
	 * Exemplo de utilização: Classe SceEstoqueGeral, para cálculos do módulo de estoque.
	 * @param valor - valor para ser truncado
	 * @param casas - quantidade de casas decimais 
	 * @return
	 */
	public static Double truncateFLOOR(Double valor, int casas){

		if(valor!=null){

			int index = valor.toString().lastIndexOf(".");

			if(index != -1){
				String subStringValor = valor.toString().substring(index).replace(".", "");

				if(subStringValor.length() > casas){

					valor = new BigDecimal(valor).setScale(casas, RoundingMode.FLOOR).doubleValue();
					
				}

			}

		}
		return valor ;

	}

	/**
	 * Método Utilizado para truncar valores. 
	 * Exemplo de utilização: Classe SceEstoqueGeral, para cálculos do módulo de estoque.
	 * @param valor - valor para ser truncado
	 * @param casas - quantidade de casas decimais 
	 * @return
	 */
	public static BigDecimal truncateFLOOR(BigDecimal valor, int casas){
		
		if(valor != null){
			
			Double valorToDouble = truncateFLOOR(valor.doubleValue(), casas);
			
			return new BigDecimal(valorToDouble.toString());
		}

		return valor;

	}
	
	public static String formatarNumeroAP(String valor){
		final StringBuilder sb = new StringBuilder(valor);

		while (sb.length() < 8) {
			// Adiciona 0 a esquerda ate atingir o numero de caracteres correto
			sb.insert(0, '0');
		}

		// Adiciona a máscara
		sb.insert(6, '/');
		return sb.toString();
	}
	
	
	

	
}
