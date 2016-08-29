package br.gov.mec.aghu.estoque.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.SceItemNotaRecebimentoRN.SceItemNotaRecebimentoRNExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemDevolucaoFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceItemNotaRecebimentoRNTest extends AGHUBaseUnitTest<SceItemNotaRecebimentoRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private SceNotaRecebimentoDAO mockedSceNotaRecebimentoDAO;
	@Mock
	private IComprasFacade mockedComprasFacade;
	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	@Mock
	private SceItemRecebProvisorioDAO mockedSceItemRecebProvisorioDAO;
	@Mock
	private SceNotaRecebimentoProvisorioRN mockedSceNotaRecebimentoProvisorioRN;
	@Mock
	private SceItemNotaRecebimentoDAO mockedSceItemNotaRecebimentoDAO;
	@Mock
	private SceItemDevolucaoFornecedorDAO mockedSceItemDevolucaoFornecedorDAO;

	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";
	
	private SceNotaRecebimento getSceNotaRecebimento(){
		
		SceNotaRecebimento notaRecebimento = new SceNotaRecebimento();

		return notaRecebimento;
		
	}
	
	private SceItemNotaRecebimento getDefaultInstance(){
		
		SceItemNotaRecebimento itemNotaRecebimento = new SceItemNotaRecebimento();
		itemNotaRecebimento.setNotaRecebimento(this.getSceNotaRecebimento());

		return itemNotaRecebimento;
		
	}
	
	
	@Test
	public void testValidaQtdeItemNrError01() {
		
		SceItemNotaRecebimento itemNotaRecebimento = this.getDefaultInstance();
		itemNotaRecebimento.setQuantidade(0);
		
		try {			
			systemUnderTest.validaQtdeItemNr(itemNotaRecebimento);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceItemNotaRecebimentoRNExceptionCode.SCE_00729);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceItemNotaRecebimentoRNExceptionCode.SCE_00729, e.getCode());
		}
	}
	
	@Test
	public void testValidaQtdeItemNrError02() {
		
		SceItemNotaRecebimento itemNotaRecebimento = this.getDefaultInstance();
		itemNotaRecebimento.setQuantidade(10);
		itemNotaRecebimento.setValor(Double.valueOf(0));
		
		try {			
			systemUnderTest.validaQtdeItemNr(itemNotaRecebimento);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceItemNotaRecebimentoRNExceptionCode.SCE_00730);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceItemNotaRecebimentoRNExceptionCode.SCE_00730, e.getCode());
		}
	}
	
	@Test
	public void testValidaDebitoNrError02() {
		
		SceItemNotaRecebimento itemNotaRecebimento = this.getDefaultInstance();
		itemNotaRecebimento.setIndDebitoNrIaf(true);
		
		try {			
			systemUnderTest.validaDebitoNr(itemNotaRecebimento);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceItemNotaRecebimentoRNExceptionCode.SCE_00804);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceItemNotaRecebimentoRNExceptionCode.SCE_00804, e.getCode());
		}
	}
	
}
