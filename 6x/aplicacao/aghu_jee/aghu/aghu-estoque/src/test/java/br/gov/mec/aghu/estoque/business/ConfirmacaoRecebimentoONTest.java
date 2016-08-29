package br.gov.mec.aghu.estoque.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.ConfirmacaoRecebimentoON.ConfirmacaoRecebimentoONExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste unitário do objeto de negócio responsável pelas regras de negócio sobre
 * AF's.
 * 
 * @author rcdsouza
 */
public class ConfirmacaoRecebimentoONTest extends AGHUBaseUnitTest<ConfirmacaoRecebimentoON>{

	@Mock
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;
	@Mock
	private SceNotaRecebimentoDAO sceNotaRecebimentoDAO;  //dil
	@Mock
	private IComprasFacade comprasFacade;

	// private RapServidores servidor;

	private final Integer seqNotaFiscal1 = 1;
	private final Integer seqNotaFiscal2 = 2;
	private final Double somatorioValorItem1 = 100.0;
	private final Double valorNota0 = 0.0;
	private final Double valorNota100 = 100.0;
	private final Double valorNota200 = 200.0;
	private final Integer afnNumero = 1;
	private final Long valorAssinado50 = 50L;
	private final Long valorAssinado0 = 0L;
	private final SceNotaRecebimento objSceNotaRecebAchou = new SceNotaRecebimento();  //dil
	private final SceNotaRecebimento objSceNotaRecebNaoAchou = null;  //dil


	/**
	 * Testa a verificação de valor da nota fiscal igual ao item.
	 */
	@Test
	public void testVerificarNotaFiscalIgual() {
		Mockito.when(sceItemRecebProvisorioDAO.obterValorTotalItemNotaFiscal(seqNotaFiscal1)).thenReturn(somatorioValorItem1);

		try {

			final SceNotaRecebProvisorio notaRecebimentoProvisorio = new SceNotaRecebProvisorio();
			notaRecebimentoProvisorio.setDocumentoFiscalEntrada(new SceDocumentoFiscalEntrada());
			notaRecebimentoProvisorio.getDocumentoFiscalEntrada().setSeq(seqNotaFiscal1);
			notaRecebimentoProvisorio.getDocumentoFiscalEntrada().setValorTotalNf(valorNota100);

			systemUnderTest.verificarNotaFiscal(notaRecebimentoProvisorio);
		} catch (Exception e) {
			Assert.fail("Erro ao verificar Nota Fiscal:" + e.getMessage());
		}
	}

	/**
	 * Testa a verificação de valor da nota fiscal maior que o item.
	 */
	@Test
	public void testVerificarNotaFiscalMaior() {
		Mockito.when(sceItemRecebProvisorioDAO.obterValorTotalItemNotaFiscal(seqNotaFiscal1)).thenReturn(somatorioValorItem1);

		try {

			final SceNotaRecebProvisorio notaRecebimentoProvisorio = new SceNotaRecebProvisorio();
			notaRecebimentoProvisorio.setDocumentoFiscalEntrada(new SceDocumentoFiscalEntrada());
			notaRecebimentoProvisorio.getDocumentoFiscalEntrada().setSeq(seqNotaFiscal1);
			notaRecebimentoProvisorio.getDocumentoFiscalEntrada().setValorTotalNf(valorNota200);

			systemUnderTest.verificarNotaFiscal(notaRecebimentoProvisorio);
		} catch (Exception e) {
			Assert.fail("Erro ao verificar Nota Fiscal:" + e.getMessage());
		}
	}

	/**
	 * Testa a verificação de valor do item igual a zero.
	 */
	@Test
	public void testVerificarNotaFiscalZero() {
		Mockito.when(sceItemRecebProvisorioDAO.obterValorTotalItemNotaFiscal(seqNotaFiscal1)).thenReturn(somatorioValorItem1);

		try {

			final SceNotaRecebProvisorio notaRecebimentoProvisorio = new SceNotaRecebProvisorio();
			notaRecebimentoProvisorio.setDocumentoFiscalEntrada(new SceDocumentoFiscalEntrada());
			notaRecebimentoProvisorio.getDocumentoFiscalEntrada().setSeq(seqNotaFiscal1);
			notaRecebimentoProvisorio.getDocumentoFiscalEntrada().setValorTotalNf(valorNota0);

			systemUnderTest.verificarNotaFiscal(notaRecebimentoProvisorio);
			Assert.fail("Deveria ocorrer o erro " + ConfirmacaoRecebimentoONExceptionCode.ERRO_VALOR_NOTA_FISCAL);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(ConfirmacaoRecebimentoONExceptionCode.ERRO_VALOR_NOTA_FISCAL));

		}
	}

	/**
	 * Testa a verificação de valor saldo ok.
	 */
	@Test
	public void testVerificarSaldoNOK() {
		Mockito.when(sceItemRecebProvisorioDAO.obterValorTotalItemNotaFiscal(seqNotaFiscal1)).thenReturn(somatorioValorItem1);

		Mockito.when(comprasFacade.verificarValorAssinadoPorAf(afnNumero)).thenReturn(valorAssinado0);

		try {
			final SceNotaRecebProvisorio notaRecebimentoProvisorio = new SceNotaRecebProvisorio();
			ScoAutorizacaoFornecedorPedido autorizacaoFornecedorPedido = new ScoAutorizacaoFornecedorPedido();
			ScoAutorizacaoForn autorizacaoFornecimento = new ScoAutorizacaoForn();
			autorizacaoFornecimento.setNumero(this.afnNumero);
			autorizacaoFornecedorPedido.setScoAutorizacaoForn(autorizacaoFornecimento);
			notaRecebimentoProvisorio.setScoAfPedido(autorizacaoFornecedorPedido);
	
			systemUnderTest.verificarSaldo(notaRecebimentoProvisorio);
			Assert.fail("Deveria ocorrer o erro " + ConfirmacaoRecebimentoONExceptionCode.ERRO_NAO_EXISTE_VALOR_ASSINADO);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(ConfirmacaoRecebimentoONExceptionCode.ERRO_NAO_EXISTE_VALOR_ASSINADO));
			
		}
	}

	/**
	 * Testa a verificação de valor saldo nok.
	 */
	@Test
	public void testVerificarSaldoOK() {
		Mockito.when(sceItemRecebProvisorioDAO.obterValorTotalItemNotaFiscal(seqNotaFiscal1)).thenReturn(somatorioValorItem1);

		Mockito.when(comprasFacade.verificarValorAssinadoPorAf(afnNumero)).thenReturn(valorAssinado50);

		try {
			final SceNotaRecebProvisorio notaRecebimentoProvisorio = new SceNotaRecebProvisorio();
			ScoAutorizacaoFornecedorPedido autorizacaoFornecedorPedido = new ScoAutorizacaoFornecedorPedido();
			ScoAutorizacaoForn autorizacaoFornecimento = new ScoAutorizacaoForn();
			autorizacaoFornecimento.setNumero(this.afnNumero);
			autorizacaoFornecedorPedido.setScoAutorizacaoForn(autorizacaoFornecimento);
			notaRecebimentoProvisorio.setScoAfPedido(autorizacaoFornecedorPedido);
			
			systemUnderTest.verificarSaldo(notaRecebimentoProvisorio);
			
		} catch (Exception e) {
			Assert.fail("Erro ao verificar saldo:" + e.getMessage());
			
		}
	}
	
	/**
	 * Testa a verificação se achou geração de NR. 
	 */
	@Test
	public void testVerificarGeracaoNotaRecebimentoAchou() {  //dil
		
		
		Mockito.when(sceItemRecebProvisorioDAO.obterValorTotalItemNotaFiscal(seqNotaFiscal1)).thenReturn(somatorioValorItem1);

		Mockito.when(sceNotaRecebimentoDAO.obterNotaRecebimentoPorNotaRecebimentoProvisorio(seqNotaFiscal1)).thenReturn(objSceNotaRecebAchou);

		try {
			objSceNotaRecebAchou.setSeq(seqNotaFiscal1);
			
			final SceNotaRecebProvisorio notaRecebimentoProvisorio = new SceNotaRecebProvisorio();
			notaRecebimentoProvisorio.setSeq(seqNotaFiscal1);
	
			systemUnderTest.verificarGeracaoNotaRecebimento(notaRecebimentoProvisorio);
			Assert.fail("Deveria ocorrer o erro " + ConfirmacaoRecebimentoONExceptionCode.ERRO_CONFIRMACAO_RECEBIMENTO_NR_GERADA);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(ConfirmacaoRecebimentoONExceptionCode.ERRO_CONFIRMACAO_RECEBIMENTO_NR_GERADA));
		}
	}
	
	/**
	 * Testa a verificação se NÃO achou geração de NR
	 */
	@Test
	public void testVerificarGeracaoNotaRecebimentoNaoAchou() {  //dil
		
		Mockito.when(sceNotaRecebimentoDAO.obterNotaRecebimentoPorNotaRecebimentoProvisorio(seqNotaFiscal2)).thenReturn(objSceNotaRecebNaoAchou);

		try {
			final SceNotaRecebProvisorio notaRecebimentoProvisorio = new SceNotaRecebProvisorio();
			notaRecebimentoProvisorio.setSeq(seqNotaFiscal2);
			
			systemUnderTest.verificarGeracaoNotaRecebimento(notaRecebimentoProvisorio);
		} catch (Exception e) {
			Assert.fail("Erro ao verificar saldo:" + e.getMessage());
		}
	}
}