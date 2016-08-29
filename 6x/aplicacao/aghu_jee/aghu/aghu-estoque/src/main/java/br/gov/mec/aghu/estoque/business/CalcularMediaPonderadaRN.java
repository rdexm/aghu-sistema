package br.gov.mec.aghu.estoque.business;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * ORADB FUNCTION SCEC_MEDIA_PONDERADA
 * @author aghu
 *
 */
@Stateless
public class CalcularMediaPonderadaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(CalcularMediaPonderadaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = -4790953903336278625L;

	/**
	 * ORADB FUNCTION SCEC_MEDIA_PONDERADA
	 */
	public Integer calcularMediaPonderada(Integer quantidadeConsignadaMes1, Integer quantidadeConsignadaMes2, Integer quantidadeConsignadaMes3, Integer quantidadeConsignadaMes4, Integer quantidadeConsignadaMes5, Integer quantidadeConsignadaMes6) throws BaseException{

		List<Integer> listaValores = new LinkedList<Integer>();
		
		listaValores.add(quantidadeConsignadaMes1);
		listaValores.add(quantidadeConsignadaMes2);
		listaValores.add(quantidadeConsignadaMes3);
		listaValores.add(quantidadeConsignadaMes4);
		listaValores.add(quantidadeConsignadaMes5);
		listaValores.add(quantidadeConsignadaMes6);
		
		// Determinar os dois maiores valores da lista
		List<Integer> listaValoresMaximos = this.obterValoresMaximosLista(listaValores);
		
		Double mediaPonderada = 0d;
		for (Integer valor : listaValores) {
			
			// Caso o valor atual é equivalente a algum dos dois maiores valores da lista
			if(listaValoresMaximos.contains(valor)){
				mediaPonderada += (valor * 3) / 10;
			} else{
				mediaPonderada += (valor) / 10;
			}

		}

		return mediaPonderada.intValue();
	}
	
	/**
	 * Determinar os dois maiores valores de uma lista de valores
	 * @param listaValores
	 * @return
	 */
	protected List<Integer> obterValoresMaximosLista(List<Integer> listaValores){

		final Integer valorMaximo1 = Collections.max(listaValores);
		
		final int indiceValorMaximo1 = listaValores.indexOf(Collections.max(listaValores));
		
		listaValores.remove(indiceValorMaximo1);
		
		final Integer valorMaximo2 = Collections.max(listaValores);
		
		// Retorna uma lista com os dois valores
		return Arrays.asList(valorMaximo1, valorMaximo2);
		
	}
}
