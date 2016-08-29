package br.gov.mec.aghu.exames;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.exames.dao.AelExameGrupoCaracteristicaDAO;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;

public class AelExameGrupoCaracteristicaDAOTest extends AbstractDAOTest<AelExameGrupoCaracteristicaDAO> {

	@Override
	protected AelExameGrupoCaracteristicaDAO doDaoUnderTests() {
		return new AelExameGrupoCaracteristicaDAO() {
			private static final long serialVersionUID = -628365880388262561L;
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return AelExameGrupoCaracteristicaDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
		};
	}

	@Override
	protected void initMocks() {

	}
	
	@Test
	public void testPesquisarExameGrupoCarateristica() {
		final AelExameGrupoCaracteristica exameGrupoCaracteristica = new AelExameGrupoCaracteristica();
		final AelExamesMaterialAnalise exameMaterialAnalise = new AelExamesMaterialAnalise();
		final AelExamesMaterialAnaliseId id = new AelExamesMaterialAnaliseId();
		id.setManSeq(Integer.valueOf(83)); //83 (tem registro)
		id.setExaSigla("CAT"); //"CAT" 
		exameMaterialAnalise.setId(id);
		exameGrupoCaracteristica.setExameMaterialAnalise(exameMaterialAnalise);

		if (isEntityManagerOk()) {
			List<AelExameGrupoCaracteristica> result = this.daoUnderTests
				.pesquisarExameGrupoCarateristica(exameGrupoCaracteristica, 1, 10, null, false);
			
			if (result == null) {
				logger.info("Retornou null.");
			} else {
				logger.info("Retornou lista=" + result.size());
			}

		}
	}

	@Override
	protected void finalizeMocks() {
	}
}   