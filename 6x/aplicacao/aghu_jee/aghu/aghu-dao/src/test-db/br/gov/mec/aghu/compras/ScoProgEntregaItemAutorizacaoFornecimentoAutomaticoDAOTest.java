package br.gov.mec.aghu.compras;

import java.util.Calendar;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;

/**
 * Teste unitário da classe {@link ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO}.
 * 
 * @author luismoura
 * 
 */
public class ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAOTest extends AbstractDAOTest<ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO> {

	@Override
	protected ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO doDaoUnderTests() {
		return new ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO() {
			private static final long serialVersionUID = 1988642551848301228L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void buscarParcelasAssinadasSaldoReceberAntesPrimeiraParcelasTest() {
		Object[] result = doDaoUnderTests().buscarParcelasAssinadasSaldoReceberAntesPrimeiraParcelas(128897, 1, DateUtil.obterData(2013, Calendar.DECEMBER, 13));
		if (result != null) {
			logger.info(result[0]);
			logger.info(result[1]);
		}
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}