package br.gov.mec.aghu.compras;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;

public class ScoFaseSolicitacaoDAOTest extends AbstractDAOTest<ScoFaseSolicitacaoDAO> {

	@Override
	protected ScoFaseSolicitacaoDAO doDaoUnderTests() {
		return new ScoFaseSolicitacaoDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected SQLQuery createSQLQuery(String query) {
				return ScoFaseSolicitacaoDAOTest.this.createSQLQuery(query);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return ScoFaseSolicitacaoDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}

	@Override
	protected void finalizeMocks() {
		
	}



	
	@Test
	public void testObterValorReforco() {
		if (isEntityManagerOk()) {
			//setup
			Integer numeroAf = 127819;
			Short seqAlteracao = (short) 0;
			//assert
			try {
				BigDecimal resultado = getDaoUnderTests().obterValorReforco(numeroAf, seqAlteracao);
				logger.info("Valor Reforco: " + resultado.toString());
			} catch (Exception e) {
				logger.info(Arrays.toString(e.getStackTrace()));
				fail("Not expecting exception: " + e.getLocalizedMessage());
			}
		}
	}

	@Test
	public void testObterValorAF() {
		if (isEntityManagerOk()) {
			// setup
			Integer numeroAf = 55195;
			Short seqAlteracao = (short) 0;
			// assert
			try {
				BigDecimal resultado = getDaoUnderTests().obterValorAF(numeroAf, seqAlteracao);
				logger.info("Valor Af: " + resultado.toString());
			} catch (Exception e) {
				logger.info(Arrays.toString(e.getStackTrace()));
				fail("Not expecting exception: " + e.getLocalizedMessage());
			}
		}
	}
	
	@Test
	public void testBuscaQtdeSaldoScEmAf() {
		doDaoUnderTests().buscarItensScEmAf(999999);
	}
	
	@Test
	public void testBuscaSaldoEmAfSemContrato() {
		doDaoUnderTests().buscarSaldoEmAfSemContrato(1, 999999);
	}
	
	@Test
	public void testBuscaSaldoEmAfSemParcelas() {
		doDaoUnderTests().buscarSaldoEmAfSemParcelas(1);
	}
}
