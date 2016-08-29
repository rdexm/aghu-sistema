package br.gov.mec.aghu.compras.solicitacaocompra.business;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.compras.dao.ScoLogGeracaoScMatEstocavelDAO;
import br.gov.mec.aghu.model.ScoLogGeracaoScMatEstocavel;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste unitário da classe {@link ScoLogGeracaoScMatEstocavelRN}.
 * 
 * @author matheus
 */
public class ScoLogGeracaoScMatEstocavelRNTest extends AGHUBaseUnitTest<ScoLogGeracaoScMatEstocavelRN>{
	
	/** DAO Mockeado */
	@Mock
	private ScoLogGeracaoScMatEstocavelDAO dao;
	
	
	/** Testa persistência do log de geração de materiais estocáveis. */
	@Test
	public void assertPersistenciaLogGeracaoScMatEstocavel() {
		final ScoLogGeracaoScMatEstocavel log = 
				new ScoLogGeracaoScMatEstocavel();
		
		
		systemUnderTest.persistir(log);
	}
}
