package br.gov.mec.aghu.compras;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;

/**
 * Teste unitário da classe {@link ScoProgEntregaItemAutorizacaoFornecimentoDAO}.
 * 
 * @author mlcruz
 */
public class ScoProgEntregaItemAutorizacaoFornecimentoDAOTest extends AbstractDAOTest<ScoProgEntregaItemAutorizacaoFornecimentoDAO> {
	@Override
	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO doDaoUnderTests() {
		return new ScoProgEntregaItemAutorizacaoFornecimentoDAO() {
			private static final long serialVersionUID = 2635360511375362639L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return ScoProgEntregaItemAutorizacaoFornecimentoDAOTest.this.runCriteria(criteria);
			}
		};
	}
	
	/** Testa pesquisa à parcelas pendentes. */
	@Test
	public void testPesquisaParcelasPendentes() {
		doDaoUnderTests().pesquisarParcelasPendentes(1);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}