package br.gov.mec.aghu.exames;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSecaoConfiguravel;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.dao.AelSecaoConfExamesDAO;
import br.gov.mec.aghu.model.AelSecaoConfExames;

public class AelSecaoConfExamesDAOTest extends AbstractDAOTest<AelSecaoConfExamesDAO> {
	
	protected Integer maxVersion;
	
	@Before
	public void setupt(){
		this.maxVersion = doDaoUnderTests().buscarMaxVersaoConfPorLu2Seq(8);
	}
	
	@Override
	protected AelSecaoConfExamesDAO doDaoUnderTests() {
		return new AelSecaoConfExamesDAO() {

			private static final long serialVersionUID = -7990924577975272328L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return AelSecaoConfExamesDAOTest.this.runCriteria(criteria);
			}

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return AelSecaoConfExamesDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return AelSecaoConfExamesDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
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
	public void testBuscarPorLu2SeqEVersaoConf(){
		List<AelSecaoConfExames> lista = this.doDaoUnderTests().buscarPorLu2SeqEVersaoConf(8, this.maxVersion);
		Assert.assertNotNull(lista);
	}
	
	@Test
	public void testBuscarAtivoPorDescVersaoConfLu2Seq() {
		this.doDaoUnderTests().buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LDM, 6, 7);
	}
	
	@Test
	public void testBuscaSecoesConfiguracaoObrigatorias() {
		Assert.assertNotNull(this.doDaoUnderTests().buscaSecoesConfiguracaoObrigatorias(DominioSituacaoExamePatologia.MC, null, null));
	}
}