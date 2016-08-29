package br.gov.mec.aghu.core.persistence.dialect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/*
 * Classe Utilizada na Máscara de Exames
 * 
 * @Author: GMNETO
 */

public class FuncoesOracle {
	public static Double power(Object a, Object b){
		Double aTemp = null;
		Double bTemp = null;

		if(a instanceof Double){
			aTemp = (Double) a;
		}else{
			aTemp = Double.parseDouble(a.toString());
		}

		if(b instanceof Double){
			bTemp = (Double) b;
		}else{
			bTemp = Double.parseDouble(b.toString());
		}

		return Math.pow(aTemp, bTemp);
	}
	
	public static Double sqrt(Double a){
		return Math.sqrt(a);
	}
	
	public static Object decode(Object... parametros){
		Object expressao = parametros[0];
		Object retorno = null;
		
		int i;
		for (i = 1; i+1<parametros.length; i = i + 2){
			if (expressao!=null && parametros[i]!=null 
				&& expressao.toString().equals(parametros[i].toString())) {
				retorno = parametros[i+1];
				break;
			}
		}
		
		if (retorno == null && parametros.length > i){
			retorno = parametros[parametros.length - 1];
		}
		
		
		return retorno;
	}
	
	public static Integer sign(Double a){
		Integer retorno = null;
		if (a < 0){
			retorno = -1;
		}else if (a > 0){
			retorno = 1;
		}else{
			retorno = 0;
		}		
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	public static Comparable greatest(Comparable... parametros){
		Collection<Comparable> colecao = Arrays.asList(parametros);		
		return (Comparable) Collections.max(colecao);
	}
}