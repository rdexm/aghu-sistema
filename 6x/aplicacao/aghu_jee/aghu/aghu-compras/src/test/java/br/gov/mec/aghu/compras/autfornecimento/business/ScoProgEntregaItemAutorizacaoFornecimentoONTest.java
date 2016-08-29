package br.gov.mec.aghu.compras.autfornecimento.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste unitário da classe {@link ScoProgEntregaItemAutorizacaoFornecimentoON}.
 * 
 * @author mlcruz
 */
public class ScoProgEntregaItemAutorizacaoFornecimentoONTest extends AGHUBaseUnitTest<ScoProgEntregaItemAutorizacaoFornecimentoON>{
	
	/** DAO Mockeada */
	@Mock
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO dao;
	
	/** ON de SC's vinculadas à parcelas mockeada. */
	@Mock
	private ScoSolicitacaoProgramacaoEntregaON parcelaScOn;
	
	/** RN Mockeada */
	@Mock
	private ScoProgEntregaItemAutorizacaoFornecimentoRN rn;
	
	/** 
	 * Testa exclusão de parcelas pendentes. 
	 * 
	 * @throws BaseException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	@Test
	public void testExclusaoParcelasPendentes() throws BaseException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		final Integer scId = 1;
		final ScoProgEntregaItemAutorizacaoFornecimento parcela = new ScoProgEntregaItemAutorizacaoFornecimento();
		final List<ScoProgEntregaItemAutorizacaoFornecimento> parcelas = Arrays.asList(parcela);
		
		Mockito.when(dao.pesquisarParcelasPendentes(scId)).thenReturn(parcelas);
		
		checkExclusao(parcela);		
		systemUnderTest.excluirParcelasPendentes(scId);
	}

	/**
	 * Checa exclusão.
	 * 
	 * @param parcela
	 * @throws BaseException
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	private void checkExclusao(
			final ScoProgEntregaItemAutorizacaoFornecimento parcela) throws BaseException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		final ScoProgEntregaItemAutorizacaoFornecimento parcelaOld = (ScoProgEntregaItemAutorizacaoFornecimento) BeanUtils
				.cloneBean(parcela);
		
		Mockito.when(dao.obterPorChavePrimaria(parcela.getId())).thenReturn(parcelaOld);
		
	}

}