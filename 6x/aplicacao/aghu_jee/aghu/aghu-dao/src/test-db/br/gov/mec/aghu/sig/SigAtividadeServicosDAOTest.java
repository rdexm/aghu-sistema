package br.gov.mec.aghu.sig;

import org.hibernate.Query;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.sig.dao.SigAtividadeServicosDAO;

/**
 * @author jgugel
 */
public class SigAtividadeServicosDAOTest extends AbstractDAOTest<SigAtividadeServicosDAO> {

	@Override
	protected SigAtividadeServicosDAO doDaoUnderTests() {
		return new SigAtividadeServicosDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Query createHibernateQuery(String query) {
				return SigAtividadeServicosDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Test
	public void buscaKitsMedicamentosTest() {
		this.getDaoUnderTests().buscaItensContratoAlocadosAtividades(120);
	}
	
	@Test 
	public void buscaDetalheValorItemContrato(){
		this.getDaoUnderTests().buscaItensContratoAlocadosAtividadesApoio(120);	
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}