package br.gov.mec.aghu.exames;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.exames.dao.AelConfigExLaudoUnicoDAO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;

public class AelConfigExLaudoUnicoDAOTest extends AbstractDAOTest<AelConfigExLaudoUnicoDAO> {

	@Override
	protected AelConfigExLaudoUnicoDAO doDaoUnderTests() {
		return new AelConfigExLaudoUnicoDAO() {
			private static final long serialVersionUID = -3457578253527631886L;
			
			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return AelConfigExLaudoUnicoDAOTest.this.runCriteriaUniqueResult(criteria);
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
	public void testObtencaoConfigExLaudoUnicoPorItemSolicitacaoExame() {
		AelItemSolicitacaoExamesId itemId = new AelItemSolicitacaoExamesId(6746191, (short) 1);
		AelItemSolicitacaoExames item = entityManager.find(AelItemSolicitacaoExames.class, itemId);
		doDaoUnderTests().obterConfigExLaudoUnico(item);
	}
}