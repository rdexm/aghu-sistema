package br.gov.mec.aghu.estoque.business;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.estoque.dao.SceCfopDAO;
import br.gov.mec.aghu.model.SceCfop;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe de teste para classe de regra de negocio(RN) da {@link SceCfopRN}.
 */
public class SceCfopRNTest extends AGHUBaseUnitTest<SceCfopRN> {

	@Mock
	private SceCfopRN cfopRN;
	@Mock
	private SceCfopDAO cfopDAO;

	/**
	 * Método de teste da inserção de Cfop.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testInserirCFOPException() throws Exception {
		SceCfop sceCfop = new SceCfop();
		sceCfop.setAplicacao("AGHU");
		Mockito.when(cfopDAO.pesquisarCodigo(Mockito.anyShort())).thenReturn(sceCfop);

		this.systemUnderTest.inserirCFOP(new SceCfop());
	}
	
	/**
	 * Método de teste da inserção de Cfop.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testInserirCFOP() throws Exception {
		SceCfop sceCfop = null;
		Mockito.when(cfopDAO.pesquisarCodigo(Mockito.anyShort())).thenReturn(sceCfop);

		this.systemUnderTest.inserirCFOP(new SceCfop());
	}


}
