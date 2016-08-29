package br.gov.mec.aghu.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * Classe para verificar todos os campos de uma tabela através do mapeamento
 * objeto relacional referente a mesma.
 */
public class PesquisaCamposPojoPorAnotacoes {

	private static final String CAMPO_NAME = "name=";
	private List<String> camposList = new ArrayList<String>();

	public enum StringLiterals {
		JOIN_COLUMN(".JoinColumn"), JOIN_COLUMNS(".JoinColumns"), COLUMN(
				".Column"), JOIN_TABLE(".JoinTable");

		private String stringLiterals;

		private StringLiterals(String stringLiterals) {
			this.stringLiterals = stringLiterals;
		}

		@Override
		public String toString() {
			return stringLiterals;
		}
	}


//	public static void main(String[] args) {
//		PesquisaCamposPojoPorAnotacoes pesquisaPac = new PesquisaCamposPojoPorAnotacoes();
//		PesquisaCamposPojoPorAnotacoes pesquisaPacHist = new PesquisaCamposPojoPorAnotacoes();
//
//		// Busca campos de AipPacientes
//		pesquisaPac.pesquisarAnotacoes(AipPacientes.class);
//		List<String> camposPacList = pesquisaPac.getCamposList();
//
//		// Busca campos de AipPacientesHist
//		pesquisaPacHist.pesquisarAnotacoes(AipPacientesHist.class);
//		List<String> camposPacHistList = pesquisaPacHist.getCamposList();
//
//		Boolean listasIguais = pesquisaPac.compararListas(camposPacList,
//				camposPacHistList);
//
//		if (listasIguais) {
//			System.out.println("Listas são IGUAIS");
//			
//			System.out.println("Diferencas: "
//					+ pesquisaPacHist.pesquisarColunasDiferentes(camposPacList,
//							camposPacHistList));
//		} else {
//			System.out.println("Listas são DIFERENTES");
//			
//			System.out.println("Diferencas: "
//					+ pesquisaPacHist.pesquisarColunasDiferentes(camposPacList,
//							camposPacHistList));
//		}
//	}

	/**
	 * Método para pesquisar anotações configuradas em uma classe.
	 * 
	 * @param Classe
	 *            na qual se deseja pesquisar as anotações (*.class)
	 */
	@SuppressWarnings("unchecked")
	public void pesquisarAnotacoes(Class clazz) {
		Method[] methodList = clazz.getMethods();

		// Percorre todos métodos da classe
		for (Method m : methodList) {
			Annotation[] annotationList = m.getAnnotations();

			// Percorre todas anotacoes de cada método
			for (Annotation a : annotationList) {
				verificarAnotacao(a.toString());
			}
		}
	}

	/**
	 * Método para verificar se método tem alguma das anotações que configura
	 * nome de campos da tabela que está sendo mapeada pela classe.
	 * 
	 * @param String
	 *            com a anotacao completa
	 */
	private void verificarAnotacao(String anotacao) {
		if ((anotacao.indexOf(StringLiterals.COLUMN.toString()) > 0)
				|| (anotacao.indexOf(StringLiterals.JOIN_COLUMN.toString()) > 0 && !(anotacao
						.indexOf(StringLiterals.JOIN_TABLE.toString()) > 0))
				|| (anotacao.indexOf(StringLiterals.JOIN_COLUMNS.toString()) > 0)) {
			obterValorAnotacao(anotacao);
		}
	}

	/**
	 * Método para buscar o valor da tag "name" para a anotação recebida por
	 * parâmetro e inserir o mesmo na lista camposList.
	 * 
	 * @param String
	 *            com a anotacao completa
	 * @return String com o valor da tag "name"
	 */
	private void obterValorAnotacao(String anotacao) {
		int posNameTag = anotacao.indexOf(CAMPO_NAME);
		String valor = anotacao.substring(posNameTag + CAMPO_NAME.length(), anotacao.length());
		valor = valor.substring(0, valor.indexOf(','));
		
		if (!camposList.contains(valor.trim())) {
			camposList.add(valor.trim());
		}
	}

	/**
	 * Método para compara duas listas.
	 * 
	 * @param List
	 *            <String> lista1
	 * @param List
	 *            <String> lista2
	 * @return TRUE se listas são iguais e FALSE se forem diferentes
	 */
	@SuppressWarnings("unchecked")
	public Boolean compararListas(List<String> lista1, List<String> lista2) {
		Boolean retorno = false;

		List<String> diferencasList = (List<String>) CollectionUtils
				.disjunction(lista1, lista2);

		if (diferencasList == null || diferencasList.size() == 0) {
			retorno = true;
		}

		return retorno;
	}

	/**
	 * Método para identificar o nome das columas existentes na lista1 que não
	 * estão na lista2 e vice-versa.
	 * 
	 * @param List
	 *            <String> lista1
	 * @param List
	 *            <String> lista2
	 * @return String com itens da lista1 que não estão na lista2 (e vice-versa)
	 *         separados por vírgulas
	 */
	public String pesquisarColunasDiferentes(List<String> lista1,
			List<String> lista2) {
		String ret1, ret2, retorno = "";
		
		//Busca elementos da lista1 inexistentes na lista2
		ret1 = obterCamposInexistentes(lista1, lista2);
		//Busca elementos da lista2 inexistentes na lista1
		ret2 = retorno + obterCamposInexistentes(lista2, lista1);
		
		if ("".equals(ret1)) {
			retorno = ret2;
		} else if ("".equals(ret2)) {
			retorno = ret1;
		} else if (!"".equals(ret1) && !"".equals(ret2)) {
			retorno = ret1 + ", " + ret2;
		}
		
		return retorno;
	}
	
	/**
	 * Método para verificar quais itens estão na lista1, porém não estão na
	 * lista2.
	 * 
	 * @param lista1
	 * @param lista2
	 * @return String com itens que estão na lista1 e que não estão na lista2
	 *         separados por vírgula
	 */
	private String obterCamposInexistentes(List<String> lista1,
			List<String> lista2) {
		StringBuffer retorno = new StringBuffer();
		Boolean existe = false;

		for (String valor1 : lista1) {
			existe = false;
			for (String valor2 : lista2) {
				if (valor1.equals(valor2)) {
					existe = true;
					break;
				}
			}

			if (!existe) {
				if (retorno.length() == 0) {
					retorno.append(valor1);
				} else {
					retorno.append(", ").append(valor1);
				}
			}
		}

		return retorno.toString();
	}

	/**
	 * Método para retorno o número de elementos únicos em uma lista de campos.
	 * 
	 * @return Número de elementos
	 */
	public Integer obterNumeroElementos() {
		Integer retorno = 0;

		if (camposList != null) {
			retorno = CollectionUtils.getCardinalityMap(camposList).size();
		}

		return retorno;
	}

	public List<String> getCamposList() {
		return camposList;
	}

	public void setCamposList(List<String> camposList) {
		this.camposList = camposList;
	}

}
