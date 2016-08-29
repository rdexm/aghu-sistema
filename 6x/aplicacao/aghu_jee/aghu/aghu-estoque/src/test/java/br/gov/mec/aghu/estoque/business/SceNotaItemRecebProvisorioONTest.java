package br.gov.mec.aghu.estoque.business;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.estoque.dao.SceItemRecbXProgrEntregaDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste unitário da classe {@link SceNotaItemRecebProvisorioON}.
 * 
 * @author mlcruz
 */
public class SceNotaItemRecebProvisorioONTest extends AGHUBaseUnitTest<SceNotaItemRecebProvisorioON>{
	
	@Mock
	private SceItemRecebProvisorioDAO itemRpDao;
	
	@Mock
	private SceItemRecbXProgrEntregaDAO mockedSceItemRecbXProgrEntregaDAO;
	
	/**
	 * Testa soma de quantidade de itens de notas de recebimentos associadas a
	 * um item de AF.
	 */
	@Test
	public void testSomaQtdeItensNotaRecebProvisorio() {
		final ScoItemAutorizacaoForn itemAf = new ScoItemAutorizacaoForn();
		final Long qtdeExpected = 0l;
		final QtdeRpVO.NotaRecebimentoProvisorio notaExpected = new QtdeRpVO.NotaRecebimentoProvisorio();
		
		Mockito.when(mockedSceItemRecbXProgrEntregaDAO.obterSomaItemRecbXProgrEntregaPorItemAF(new ScoProgEntregaItemAutorizacaoFornecimento())).thenReturn(Long.valueOf(qtdeExpected));
		
		Mockito.when(itemRpDao.somarQtdeItensNotaRecebProvisorio(itemAf)).thenReturn(Long.valueOf(qtdeExpected));
		
		Mockito.when(itemRpDao.pesquisarNotasRecebimentoProvisorio(itemAf, 10)).thenReturn(Arrays.asList(notaExpected));

		QtdeRpVO soma = systemUnderTest.somarQtdeItensNotaRecebProvisorio(itemAf, 10);
		
		QtdeRpVO.NotaRecebimentoProvisorio notaReturned = soma.getNotasRecebimento().get(0);
		assertEquals(qtdeExpected, soma.getQuantidade());
		assertEquals(notaExpected, notaReturned);
	}
	
	/**
	 * Testa soma de quantidade de itens de notas de recebimentos associadas a
	 * uma parcela de item de AF.
	 */
	//@Test
	public void testSomaQtdeItensNotaRecebProvisorioParcela() {
		final ScoProgEntregaItemAutorizacaoFornecimento item = new ScoProgEntregaItemAutorizacaoFornecimento();
		final Integer qtdeExpected = 0;
		final QtdeRpVO.NotaRecebimentoProvisorio notaExpected = new QtdeRpVO.NotaRecebimentoProvisorio();
		
		Mockito.when(mockedSceItemRecbXProgrEntregaDAO.obterSomaItemRecbXProgrEntregaPorItemAF(new ScoProgEntregaItemAutorizacaoFornecimento())).thenReturn(Long.valueOf(qtdeExpected));
		
		Mockito.when(itemRpDao.somarQtdeItensNotaRecebProvisorio(item)).thenReturn(qtdeExpected);
		
		Mockito.when(itemRpDao.pesquisarNotasRecebimentoProvisorio(item, 10)).thenReturn(Arrays.asList(notaExpected));

		QtdeRpVO soma = systemUnderTest.somarQtdeItensNotaRecebProvisorio(item, 10);
		
		QtdeRpVO.NotaRecebimentoProvisorio notaReturned = soma.getNotasRecebimento().get(0);
		assertEquals(qtdeExpected, soma.getQuantidade());
		assertEquals(notaExpected, notaReturned);
	}
	
}
