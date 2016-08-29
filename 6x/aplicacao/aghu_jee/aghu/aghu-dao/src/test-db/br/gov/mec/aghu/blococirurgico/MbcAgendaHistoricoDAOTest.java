package br.gov.mec.aghu.blococirurgico;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHistoricoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.MbcAgendaHistorico;

public class MbcAgendaHistoricoDAOTest extends AbstractDAOTest<MbcAgendaHistoricoDAO> {
	
	@Override
	protected MbcAgendaHistoricoDAO doDaoUnderTests() {
		return new MbcAgendaHistoricoDAO() {
			
			private static final long serialVersionUID = -1169457840121134222L;
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return MbcAgendaHistoricoDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcAgendaHistoricoDAOTest.this.runCriteria(criteria);
			}
		};
	}
	
	@Override
	protected void initMocks() {
	}

	@Test
	public void testBuscarHistoricoAgenda() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				Integer agdSeq = Integer.valueOf(465146);
				List<MbcAgendaHistorico> result = this.daoUnderTests.buscarAgendaHistorico(agdSeq);
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (MbcAgendaHistorico result1 : result) {
						logger.info("Resultado = " + result1);
					}
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