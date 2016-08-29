package br.gov.mec.aghu.core.commons;

import java.lang.annotation.Annotation;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Classe que inclui os producer para parametros específicos da aplicação, de
 * acordo com o valor da propriedade value do qualifier Parametro.
 * 
 * @author geraldo
 * 
 */
public class ParameterProcucer {

	/**
	 * Producer que gera as proriedades específicas do sistema, de acordo com o
	 * valor do parâmetro value do qualifier Parametro
	 * 
	 * @param injectionPoint
	 * @param parametrossistema
	 * @return
	 */
	@Produces
	@Parametro
	@Dependent
	public String getParametro(InjectionPoint injectionPoint,
			ParametrosSistema parametrossistema) {
		String retorno = null;

		String nomeParametro = null;
		for (Annotation qualifier : injectionPoint.getQualifiers()) {
			if (qualifier instanceof Parametro) {
				nomeParametro = ((Parametro) qualifier).value();
				break;
			}
		}
		if (nomeParametro == null) {
			throw new IllegalStateException(
					"Não foi possível obter parâmetro a ser injetado");
		}
		retorno = parametrossistema.getParametro(nomeParametro);

		return retorno;

	}

}
