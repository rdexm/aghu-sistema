package br.gov.mec.aghu.faturamento.business;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GeracaoArquivoTextoInternacaoRNTest extends AGHUBaseUnitTest<GeracaoArquivoTextoInternacaoBean>{

	@Test
	public void testSepararListaEmListasDeTamMax() {

		List<Integer> lista = null;
		List<Integer> rest = null;
		List<List<Integer>> result = null;
		Random rand = null;
		int maximo = 0;
		int size = 0;
		
		//setup
		maximo = 0;
		lista = new LinkedList<Integer>();
		//assert
		result = systemUnderTest.separarListaEmListasDeTamMax(lista, maximo);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertTrue(result.get(0).isEmpty());
		//setup
		rand = new Random(System.currentTimeMillis());
		size = 321;
		maximo = 13;
		lista = new LinkedList<Integer>();
		for (int i = 0; i < size; i++) {
			lista.add(Integer.valueOf(rand.nextInt()));
		}
		//assert
		result = systemUnderTest.separarListaEmListasDeTamMax(lista, maximo);
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		rest = new LinkedList<Integer>();
		for (List<Integer> l : result) {
			rest.addAll(l);
		}
		Assert.assertEquals(lista, rest);
	}

}
