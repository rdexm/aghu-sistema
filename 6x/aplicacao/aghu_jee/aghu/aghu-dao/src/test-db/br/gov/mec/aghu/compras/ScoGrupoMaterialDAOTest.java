package br.gov.mec.aghu.compras;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoGrupoMaterialDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;

/**
 * Classe reponsável por testar unitariamente {@link ScoGrupoMaterialDAO}.
 * 
 * @author mlcruz
 */

public class ScoGrupoMaterialDAOTest extends AbstractDAOTest<ScoGrupoMaterialDAO> {

	@Override
	protected ScoGrupoMaterialDAO doDaoUnderTests() {
		return new ScoGrupoMaterialDAO() {

			private static final long serialVersionUID = 723792094616755271L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return ScoGrupoMaterialDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void testNaturezaDespesaPorMaterial() {
		doDaoUnderTests().obtemNaturezaDespesaPorMaterial(0);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}