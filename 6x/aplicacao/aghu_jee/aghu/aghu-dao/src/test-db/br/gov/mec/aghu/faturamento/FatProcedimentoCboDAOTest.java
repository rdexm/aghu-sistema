package br.gov.mec.aghu.faturamento;

import java.util.Calendar;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoCboDAO;
import br.gov.mec.aghu.model.FatProcedimentoCbo;

public class FatProcedimentoCboDAOTest extends AbstractDAOTest<FatProcedimentoCboDAO> {

	@Override
	protected FatProcedimentoCboDAO doDaoUnderTests() {
		return new FatProcedimentoCboDAO() {
			private static final long serialVersionUID = -6458773421762286885L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatProcedimentoCboDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			public boolean isOracle() {
				return false;
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}

	@Test
	public void listarDoadorSangueTriagemClinica() {
		if (isEntityManagerOk()) {
			// assert
			this.daoUnderTests.listarProcedimentoCboPorIphSeqEPhoSeq(1, (short) 1);
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testObterFatProcedimentoCbo() {
		if (isEntityManagerOk()) {
			// assert
			final Short pPhoSeq = 12;
			final Integer pIphSeq = 3455;
			final String valor = "223122";
			Calendar data = Calendar.getInstance();
			data.add(Calendar.YEAR, -1);
			FatProcedimentoCbo result = this.daoUnderTests.obterFatProcedimentoCbo(pPhoSeq, pIphSeq, valor, DateUtil.truncaData(data.getTime()));
			logger.info("###############################################");
			if (result == null) {
				logger.info("Retornou null.");
			} else {
				logger.info("Retornou 1 registros.");
				logger.info("seq=" + result.getSeq());
				logger.info("DtInicio=" + result.getDtInicio());
				logger.info("DtFim=" + result.getDtFim());
				logger.info("cbo Codigo=" + result.getCbo().getCodigo());
				logger.info("cbo seq=" + result.getCbo().getSeq());
				logger.info("cbo descrição=" + result.getCbo().getDescricao());
			}
		}
	}

	@Override
	protected void finalizeMocks() {
	}
}