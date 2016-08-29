package br.gov.mec.aghu.exames;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoDAO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;

public class AelAnatomoPatologicoDAOTest extends AbstractDAOTest<AelAnatomoPatologicoDAO> {
	
	@Override
	protected AelAnatomoPatologicoDAO doDaoUnderTests() {
		return new AelAnatomoPatologicoDAO() {
			private static final long serialVersionUID = 1138549208577087622L;

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return AelAnatomoPatologicoDAOTest.this.runCriteriaUniqueResult(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {
	}
	
	@Test
	public void obterAelAnatomoPatologicoByNumeroApTest() {
		doDaoUnderTests().obterAelAnatomoPatologicoByNumeroAp(113L, 1);
	}

	@Test
	public void obterAelAnatomoPatologicoPorItemSolicTest() {
		AelAnatomoPatologico aelAnatomoPatologico = doDaoUnderTests().obterAelAnatomoPatologicoPorItemSolic(3282323, (short) 1);
		if (aelAnatomoPatologico != null) {
			logger.info(aelAnatomoPatologico.getNumeroAp());
		}
		else {
			logger.info("Nao encontrado");
		}
	}

	@Override
	protected void finalizeMocks() {
	}
}