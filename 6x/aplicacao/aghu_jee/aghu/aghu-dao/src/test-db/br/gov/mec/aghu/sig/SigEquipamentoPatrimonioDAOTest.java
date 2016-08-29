package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.sig.dao.SigEquipamentoPatrimonioDAO;

/**
 * @author rmalvezzi
 */
public class SigEquipamentoPatrimonioDAOTest extends AbstractDAOTest<SigEquipamentoPatrimonioDAO> {

	@Override
	protected SigEquipamentoPatrimonioDAO doDaoUnderTests() {
		return new SigEquipamentoPatrimonioDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigEquipamentoPatrimonioDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void pesquisarEquipamentoPatrimonioPeloCodgioPatrimonioTest() {
		this.getDaoUnderTests().pesquisarEquipamentoPatrimonioPeloCodgioPatrimonio("");
	}
	
	@Test
	public void buscaEquipametosCirurgicosTest() {
		this.getDaoUnderTests().buscaEquipametosCirurgicos(this.entityManager.find(MbcEquipamentoCirurgico.class, new Short("1")));
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}

}