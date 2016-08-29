package br.gov.mec.aghu.estoque;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebProvisorioDAO;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;

/**
 * 
 * @author cvagheti
 */
public class SceNotaRecebProvisorioDAOTest extends AbstractDAOTest<SceNotaRecebProvisorioDAO> {
	@Override
	protected SceNotaRecebProvisorioDAO doDaoUnderTests() {
		return new SceNotaRecebProvisorioDAO() {
			private static final long serialVersionUID = 3834560316013855146L;
			
			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return SceNotaRecebProvisorioDAOTest.this.runCriteriaCount(criteria);
			}
		};
	}
	
	@Test
	public void testContaItensByTipo() {
		SceNotaRecebProvisorio nrp = new SceNotaRecebProvisorio();
		nrp.setSeq(1);
		DominioTipoFaseSolicitacao t = DominioTipoFaseSolicitacao.S;
		doDaoUnderTests().contarItensByTipo(nrp, t);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}