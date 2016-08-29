package br.gov.mec.aghu.compras.business;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste Unitário da RN de Condição de Pagamento de Proposta
 * 
 * @author mlcruz
 */
public class ScoCondicaoPagamentoProposONTest extends AGHUBaseUnitTest<ScoCondicaoPagamentoProposRN>{	
	
	/** DAO Mockeado */
	@Mock
	private ScoCondicaoPagamentoProposDAO dao;
	
	/** DAO de Itens da Proposta Mockeado */
	@Mock
	private ScoItemPropostaFornecedorDAO itemPropostaDao;
	
	/** PAC Façade Mockeada */
	@Mock
	private IPacFacade pacFacade;

	/** DAO de Parcelas Mockeado */
	@Mock
	private ScoParcelasPagamentoDAO parcelasPagamentoDao;

	/** RN de Parcelas Mockeado */
	@Mock
	private ScoParcelasPagamentoRN parcelasPagamentoRn;
	
	
	/**
	 * Testa exclusão de condição de proposta não julgada.
	 */
	@Test
	public void testExclusaoCondicaoPropostaNaoJulgada() {
		final Integer id = 1;
		final ScoCondicaoPagamentoPropos condicao = new ScoCondicaoPagamentoPropos();
		condicao.setNumero(id);
		
		Mockito.when(itemPropostaDao.existeItemAssociadoACondicao(id)).thenReturn(false);
		
		Mockito.when(dao.obterPorChavePrimaria(id)).thenReturn(condicao);				
		
		try {
			systemUnderTest.excluir(id);
		} catch (ApplicationBusinessException e) {
			e.printStackTrace();
			fail("Não excluiu condição de proposta não julgada.");
		}
	}
	
	/**
	 * Testa exclusão de condição de proposta julgada.
	 */
	@Test
	public void testExclusaoCondicaoPropostaJulgada() {
		final Integer id = 1;
		final ScoCondicaoPagamentoPropos condicao = new ScoCondicaoPagamentoPropos();
		condicao.setNumero(id);
		
				Mockito.when(itemPropostaDao.existeItemAssociadoACondicao(id)).thenReturn(true);
				
		try {
			systemUnderTest.excluir(id);
			fail("Excluiu condição de proposta julgada.");
		} catch (ApplicationBusinessException e) {
			
		}
	}
	
	/**
	 * Testa inclusão de condição de proposta com valores percentuais incompletos.
	 */
	@Test
	public void testInclusaoCondicaoPropostaComPercentualIncompleto() {
		ScoCondicaoPagamentoPropos condicao = new ScoCondicaoPagamentoPropos();
		List<ScoParcelasPagamento> parcelas = new ArrayList<ScoParcelasPagamento>();
		
		{
			ScoParcelasPagamento parcela = new ScoParcelasPagamento();
			parcela.setCondicaoPagamentoPropos(condicao);
			parcela.setParcela((short) 1);
			parcela.setPercPagamento(BigDecimal.ONE);
			parcelas.add(parcela);
		}
		
		{
			ScoParcelasPagamento parcela = new ScoParcelasPagamento();
			parcela.setCondicaoPagamentoPropos(condicao);
			parcela.setParcela((short) 2);
			parcela.setPercPagamento(BigDecimal.TEN);
			parcelas.add(parcela);
		}
		
		try {
			systemUnderTest.inserir(condicao, parcelas);
			fail("Incluiu condição com valores percentuais incompletos.");
		} catch (BaseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Testa inclusão de condição de proposta com valores percentuais completos.
	 */
	@Test
	public void testInclusaoCondicaoPropostaComPercentualCompleto() throws ApplicationBusinessException {
		final ScoCondicaoPagamentoPropos condicao = new ScoCondicaoPagamentoPropos();
		List<ScoParcelasPagamento> parcelas = new ArrayList<ScoParcelasPagamento>();
		
		{
			ScoParcelasPagamento parcela = new ScoParcelasPagamento();
			parcela.setCondicaoPagamentoPropos(condicao);
			parcela.setParcela((short) 1);
			parcela.setPercPagamento(BigDecimal.valueOf(45.75));
			parcelas.add(parcela);
		}
		
		{
			ScoParcelasPagamento parcela = new ScoParcelasPagamento();
			parcela.setCondicaoPagamentoPropos(condicao);
			parcela.setParcela((short) 2);
			parcela.setPercPagamento(BigDecimal.valueOf(54.25));
			parcelas.add(parcela);
		}
		
		
		try {
			systemUnderTest.inserir(condicao, parcelas);
		} catch (BaseException e) {
			e.printStackTrace();
			fail("Não incluiu condição com valores percentuais completos.");
		}
	}
	
	/**
	 * Testa inclusão de condição de proposta com valores incompletos.
	 */
	@Test
	public void testInclusaoCondicaoPropostaComValoresIncompletos() {
		final ScoPropostaFornecedor proposta = new ScoPropostaFornecedor();
		final ScoCondicaoPagamentoPropos condicao = new ScoCondicaoPagamentoPropos();
		condicao.setPropostaFornecedor(proposta);
		List<ScoParcelasPagamento> parcelas = new ArrayList<ScoParcelasPagamento>();
		
		{
			ScoParcelasPagamento parcela = new ScoParcelasPagamento();
			parcela.setCondicaoPagamentoPropos(condicao);
			parcela.setParcela((short) 1);
			parcela.setValorPagamento(BigDecimal.ONE);
			parcelas.add(parcela);
		}
		
		{
			ScoParcelasPagamento parcela = new ScoParcelasPagamento();
			parcela.setCondicaoPagamentoPropos(condicao);
			parcela.setParcela((short) 2);
			parcela.setValorPagamento(BigDecimal.TEN);
			parcelas.add(parcela);
		}
		
		Mockito.when(pacFacade.obterValorTotalProposta(proposta)).thenReturn(BigDecimal.valueOf(50.00));
		
		try {
			systemUnderTest.inserir(condicao, parcelas);
			fail("Incluiu condição com valores incompletos.");
		} catch (BaseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Testa inclusão de condição de proposta com valores completos.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testAlteracaoCondicaoPropostaComValoresCompletos() throws ApplicationBusinessException {
		final Integer id = 1;
		final ScoItemPropostaFornecedor item = new ScoItemPropostaFornecedor();
		final ScoCondicaoPagamentoPropos condicao = new ScoCondicaoPagamentoPropos();
		condicao.setNumero(id);
		condicao.setItemPropostaFornecedor(item);
		List<ScoParcelasPagamento> parcelas = new ArrayList<ScoParcelasPagamento>();
		
		{
			ScoParcelasPagamento parcela = new ScoParcelasPagamento();
			parcela.setCondicaoPagamentoPropos(condicao);
			parcela.setParcela((short) 1);
			parcela.setValorPagamento(BigDecimal.ONE);
			parcelas.add(parcela);
		}
		
		{
			ScoParcelasPagamento parcela = new ScoParcelasPagamento();
			parcela.setCondicaoPagamentoPropos(condicao);
			parcela.setParcela((short) 2);
			parcela.setValorPagamento(BigDecimal.TEN);
			parcelas.add(parcela);
		}
		
		Mockito.when(pacFacade.obterValorTotalItemProposta(item)).thenReturn(BigDecimal.ONE.add(BigDecimal.TEN));
		
		Mockito.when(itemPropostaDao.existeItemAssociadoACondicao(id)).thenReturn(false);
		

		try {
			systemUnderTest.atualizar(condicao, parcelas, new ArrayList<ScoParcelasPagamento>());
		} catch (BaseException e) {
			e.printStackTrace();
			fail("Não incluiu condição com valores completos.");
		}
	}
	
	/**
	 * Verifica mocks.
	 */
	@After
	public void tearDown() {
	}
}
