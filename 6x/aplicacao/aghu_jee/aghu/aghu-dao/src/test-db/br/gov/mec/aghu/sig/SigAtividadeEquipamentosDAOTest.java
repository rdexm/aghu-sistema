package br.gov.mec.aghu.sig;

import org.hibernate.Query;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.sig.dao.SigAtividadeEquipamentosDAO;

/**
 * 
 * @author rmalvezzi
 *
 */
public class SigAtividadeEquipamentosDAOTest extends AbstractDAOTest<SigAtividadeEquipamentosDAO> {

	@Override
	protected SigAtividadeEquipamentosDAO doDaoUnderTests() {
		return new SigAtividadeEquipamentosDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Query createHibernateQuery(String query) {
				return SigAtividadeEquipamentosDAOTest.this.createHibernateQuery(query);
			}

		};
	}

	@Test
	public void buscarItemConsumoTest() {
		this.daoUnderTests.buscaEquipamentosAlocadosAtividadesAssistencial(13);
	}

	@Test
	public void buscaEquipamentosAlocadosAtividadesApoioTest() {
		this.daoUnderTests.buscaEquipamentosAlocadosAtividadesApoio(13);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}