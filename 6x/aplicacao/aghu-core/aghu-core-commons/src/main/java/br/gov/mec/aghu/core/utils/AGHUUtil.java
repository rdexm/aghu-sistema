package br.gov.mec.aghu.core.utils;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe para métodos utilitários genéricos no AGHU.
 * 
 * @author geraldo
 * 
 */
public class AGHUUtil {
	
	
	private static final Log LOG = LogFactory.getLog(AGHUUtil.class);

	/**
	 * Formata o cep para correta exibição na camada de visão, ajustando para o
	 * número correto de caracteres e colocando os separadores.
	 * 
	 * @param cep
	 * @return
	 */
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
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void ordenarLista(List<?> lista, String propriedade, boolean asc) {
		class ComparadorGenerico implements Comparator<Object> {

			private String propriedade;

			public ComparadorGenerico(String propriedade) {
				this.propriedade = propriedade;
			}

			
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

			Comparator<Object> comparadorGenerico = new ComparadorGenerico(
					propriedade);

			if (!asc) {
				comparadorGenerico = new ReverseComparator(comparadorGenerico);
			}

			Collections.sort(lista, comparadorGenerico);
		}
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
	
	
	/**
	 * Método para converter um string em um stream.
	 * 
	 * @param newValue
	 * @param oldValue
	 * @return
	 */
	public static InputStream getResourceAsStream(String resource, InputStream stream) 
	   {
	      String stripped = resource.charAt(0) == '/' ? resource.substring(1) : resource;
	      if (stream!=null) {
	         try {
	            if (stream!=null) {
	            	LOG.debug("Loaded resource from servlet context: " + resource);
	            }
	         } catch (Exception e) {       
	             LOG.error(e.getMessage(),e);
	         }
	      }
	      
	      if (stream==null) {
	         stream = getResourceAsStream(resource, stripped);
	      }
	      return stream;
	   }
	
	 public static InputStream getResourceAsStream(String resource, String stripped) {
	      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	      InputStream stream = null;
	      if (classLoader!=null) {
	         stream = classLoader.getResourceAsStream(stripped);
	         if (stream !=null) {
	        	 LOG.debug("Loaded resource from context classloader: " + stripped);
	         }
	      }
	      return stream;
	   }
	

}
