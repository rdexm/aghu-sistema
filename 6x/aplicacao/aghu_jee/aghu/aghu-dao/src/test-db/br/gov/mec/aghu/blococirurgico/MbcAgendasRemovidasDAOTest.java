package br.gov.mec.aghu.blococirurgico;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.AgendamentosExcluidosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;

public class MbcAgendasRemovidasDAOTest extends AbstractDAOTest<MbcAgendasDAO> {
	
	@Override
	protected MbcAgendasDAO doDaoUnderTests() {
		return new MbcAgendasDAO() {
			
			private static final long serialVersionUID = 7501805938170456262L;
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return MbcAgendasRemovidasDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
		};
	}
	
	@Override
	protected void initMocks() {
	}

	@Test
	public void testBuscarAgendamentosExcluidos() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				PortalPesquisaCirurgiasParametrosVO vo = new PortalPesquisaCirurgiasParametrosVO();
				List<AgendamentosExcluidosVO> result = this.daoUnderTests.pesquisarAgendamentosExcluidos(0,10,"dtExclusao",Boolean.TRUE,vo);
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (AgendamentosExcluidosVO result1 : result) {
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