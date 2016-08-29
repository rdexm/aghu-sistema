package br.gov.mec.aghu.estoque.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoFiscalEntrada;
import br.gov.mec.aghu.estoque.business.SceDocumentoFiscalEntradaRN.SceDocumentoFiscalEntradaRNExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceDevolucaoFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceDocumentoFiscalEntradaDAO;
import br.gov.mec.aghu.estoque.dao.SceEntradaSaidaSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceDocumentoFiscalEntradaRNTest extends AGHUBaseUnitTest<SceDocumentoFiscalEntradaRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private ScoFornecedorRN mockedScoFornecedorRN;
	@Mock
	private ScoFornecedorEventualRN mockedScoFornecedorEventualRN;
	@Mock
	private SceDocumentoFiscalEntradaDAO mockedSceDocumentoFiscalEntradaDAO;
	@Mock
	private SceEntradaSaidaSemLicitacaoDAO mockedSceEntradaSaidaSemLicitacaoDAO;
	@Mock
	private SceNotaRecebimentoDAO mockedSceNotaRecebimentoDAO;
	@Mock
	private SceDevolucaoFornecedorDAO mockedSceDevolucaoFornecedorDAO;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";
	
	private SceDocumentoFiscalEntrada getDefaultInstance(){
		SceDocumentoFiscalEntrada documentoFiscalEntrada = new SceDocumentoFiscalEntrada();
		return documentoFiscalEntrada;
	}
	
	@Test
	public void testVerificarDocumentoFiscalEntradaDataEmissaoError01(){
		try {
			this.systemUnderTest.verificarDocumentoFiscalEntradaDataEmissao(new Date(new Date().getTime() * 3));
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceDocumentoFiscalEntradaRNExceptionCode.SCE_00656);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceDocumentoFiscalEntradaRNExceptionCode.SCE_00656, e.getCode());
		}
	}
	
	@Test
	public void testAtualizarDocumentoFiscalEntradaSerieError01(){
		
		SceDocumentoFiscalEntrada documentoFiscalEntrada = this.getDefaultInstance();
		documentoFiscalEntrada.setTipoDocumentoFiscalEntrada(DominioTipoDocumentoFiscalEntrada.DFE); // Teste aqui!
		
		this.systemUnderTest.atualizarDocumentoFiscalEntradaSerie(documentoFiscalEntrada);

		if("U".equals(documentoFiscalEntrada.getSerie())){
			Assert.assertFalse(false);
		} else{
			Assert.assertFalse(true);
		}
	}

	
}
