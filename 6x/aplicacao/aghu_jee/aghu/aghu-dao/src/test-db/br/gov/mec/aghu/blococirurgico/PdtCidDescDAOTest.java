package br.gov.mec.aghu.blococirurgico;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.PdtCidDescDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioTipoPlano;

public class PdtCidDescDAOTest extends AbstractDAOTest<PdtCidDescDAO> {
	
	@Override
	protected PdtCidDescDAO doDaoUnderTests() {
		return new PdtCidDescDAO() {
			private static final long serialVersionUID = 1058151041682953177L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return PdtCidDescDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}


	@Test
	public void testBuscarCidSeqPdtCidDesc() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			final Integer crgSeq = 386148;
			final Integer phiSeq = 31764;
			final DominioTipoPlano validade = DominioTipoPlano.A;

			try {
				Integer result = this.daoUnderTests.buscarCidSeqPdtCidDesc(crgSeq, phiSeq, validade);
				logger.info("###############################################");
				if (result == null) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}

	@Override
	protected void finalizeMocks() {

		
	}
}
