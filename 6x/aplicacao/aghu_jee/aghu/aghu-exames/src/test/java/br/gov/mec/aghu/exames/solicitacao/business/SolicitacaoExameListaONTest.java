package br.gov.mec.aghu.exames.solicitacao.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SolicitacaoExameListaONTest extends AGHUBaseUnitTest<SolicitacaoExameListaON>{
	

	/**
	 * Deve retornar uma exceção de código MSG_PREENCHER_ALGUM_FILTRO.
	 */
	@Test
	public void verifcarFiltrosPesquisaOrigemNulaTest() {
		
		SolicitacaoExameFilter filter = new SolicitacaoExameFilter();
		//filter.setOrigem(DominioOrigemAtendimento.A);
		
		try {
		
			systemUnderTest.verificarFiltrosPesquisaSolicitacaoExame(filter);
			fail("Falhou pois deveria ter ocorrido uma exceção! Código: MSG_PREENCHER_MAIS_ALGUM_FILTRO");
			
		} catch (ApplicationBusinessException e) {

			assertEquals("MSG_PREENCHER_ALGUM_FILTRO", e.getMessage());
		
		}	
		
	}

}
