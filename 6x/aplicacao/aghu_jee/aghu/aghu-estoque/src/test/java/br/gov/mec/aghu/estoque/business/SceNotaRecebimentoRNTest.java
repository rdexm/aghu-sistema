package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.SceNotaRecebimentoRN.SceNotaRecebimentoRNExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceNotaRecebimentoRNTest extends AGHUBaseUnitTest<SceNotaRecebimentoRN>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private SceNotaRecebimentoDAO mockedSceNotaRecebimentoDAO;
	@Mock
	private ISiconFacade mockedSiconFacade;
	@Mock
	private IComprasFacade mockedComprasFacade;
	@Mock
	private IAutFornecimentoFacade mockedAutFornecimentoFacade;
	@Mock
	private SceTipoMovimentosDAO mockedSceTipoMovimentosDAO;
	@Mock
	private SceTipoMovimentosRN mockedSceTipoMovimentosRN;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";

	private SceNotaRecebimento getDefaultInstance(){
		
		SceNotaRecebimento notaRecebimento = new SceNotaRecebimento();
		
		ScoAutorizacaoForn autorizacaoFornecimento = new ScoAutorizacaoForn();
		autorizacaoFornecimento.setNumero(0);
		notaRecebimento.setAutorizacaoFornecimento(autorizacaoFornecimento);
		
		ScoFornecedor fornecedor = new ScoFornecedor();
		
		ScoPropostaFornecedor propostaFornecedor = new ScoPropostaFornecedor();
		propostaFornecedor.setFornecedor(fornecedor);
		autorizacaoFornecimento.setPropostaFornecedor(propostaFornecedor);
	
		SceDocumentoFiscalEntrada documentoFiscalEntrada = new SceDocumentoFiscalEntrada();
		documentoFiscalEntrada.setFornecedor(fornecedor);
		notaRecebimento.setDocumentoFiscalEntrada(documentoFiscalEntrada);
		
		return notaRecebimento;
		
	}
	

	@Test
	public void testValidarNotaRecebimentoFornecedorDocumentoFiscalSuccess01(){
		
		SceNotaRecebimento notaRecebimento = this.getDefaultInstance();
		
		ScoFornecedor fornecedor = new ScoFornecedor();
		fornecedor.setNumero(0);
		notaRecebimento.getDocumentoFiscalEntrada().setFornecedor(fornecedor); // Teste aqui!
		
		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.valueOf(100));
		
		try {
			
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

			Mockito.when(mockedAutFornecimentoFacade.existeAutorizacaoFornecimentoNotaImportacao(Mockito.anyInt(), Mockito.anyShort())).thenReturn(false);

			this.systemUnderTest.validarNotaRecebimentoFornecedorDocumentoFiscalEntrada(notaRecebimento.getAutorizacaoFornecimento(), notaRecebimento.getDocumentoFiscalEntrada());
			Assert.assertFalse(false);	
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
		
	}
	
	@Test
	public void testValidarNotaRecebimentoAutorizacaoFornecimentoValidaError01(){
	
		try {
			
			SceNotaRecebimento notaRecebimento = this.getDefaultInstance();

			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(BigDecimal.valueOf(100)); // Valor padrão dos parâmetros
			
			final List<ScoAutorizacaoForn> listaAutorizacaoForn = new ArrayList<ScoAutorizacaoForn>();

			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

			Mockito.when(mockedAutFornecimentoFacade.pesquisarAutorizacaoFornecimentoValidasInsercao(Mockito.anyInt())).thenReturn(listaAutorizacaoForn);

			this.systemUnderTest.validarNotaRecebimentoAutorizacaoFornecimentoValida(notaRecebimento.getAutorizacaoFornecimento().getNumero());
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceNotaRecebimentoRNExceptionCode.SCE_00704);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceNotaRecebimentoRNExceptionCode.SCE_00704, e.getCode());
		}

	}

}
