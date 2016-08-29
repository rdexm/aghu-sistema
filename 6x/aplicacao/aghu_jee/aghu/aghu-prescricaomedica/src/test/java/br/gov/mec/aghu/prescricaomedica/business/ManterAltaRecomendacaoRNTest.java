package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.gov.mec.aghu.model.MpmAltaRecomendacao;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaRecomendacaoVO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaRecomendacaoRNTest extends AGHUBaseUnitTest<ManterAltaRecomendacaoRN>{
	
	/**
	 * Deve retornar exceção MPM_02690.
	 */
	@Test
	public void verificarAtualizacao001Test() {
		
		MpmAltaRecomendacao altaRecomendacao = new MpmAltaRecomendacao();
		MpmAltaRecomendacaoVO atualAltaRecomendacao = new MpmAltaRecomendacaoVO();
		
		altaRecomendacao.setDescricaoSistema("teste");
		atualAltaRecomendacao.setDescricaoSistema("teste1");
					
		try {
		
			systemUnderTest.verificarAtualizacao(altaRecomendacao, atualAltaRecomendacao);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("MPM_02690", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02691.
	 */
	@Test
	public void verificarAtualizacao002Test() {
		
		MpmAltaRecomendacao altaRecomendacao = new MpmAltaRecomendacao();
		MpmAltaRecomendacaoVO atualAltaRecomendacao = new MpmAltaRecomendacaoVO();
		
		altaRecomendacao.setDescricaoSistema("teste");
		atualAltaRecomendacao.setDescricaoSistema("teste");
		
		altaRecomendacao.setPmdSeq(Integer.valueOf("1"));
		atualAltaRecomendacao.setPmdSeq(Integer.valueOf("2"));
					
		try {
		
			systemUnderTest.verificarAtualizacao(altaRecomendacao, atualAltaRecomendacao);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("MPM_02691", atual.getMessage());
			
		}
		
	}

}
