package br.gov.mec.aghu.sig;

import org.hibernate.Query;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.sig.dao.SigCalculoAtividadeInsumoDAO;

public class SigCalculoAtividadeInsumoDAOTest extends AbstractDAOTest<SigCalculoAtividadeInsumoDAO> {

	@Override
	protected SigCalculoAtividadeInsumoDAO doDaoUnderTests() {
		return new SigCalculoAtividadeInsumoDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Query createHibernateQuery(String query) {
				return SigCalculoAtividadeInsumoDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Test
	public void buscaMateriaisComConsumoExcedenteAssistencialApoioTest() {
		this.daoUnderTests.buscaMateriaisComConsumoExcedenteAssistencialApoio(40, DominioTipoObjetoCusto.AS);
	}

	@Test
	public void buscaAtividadesComConsumoExcedenteAssistencialApoioTest() {
		this.daoUnderTests.buscaAtividadesComConsumoExcedenteAssistencialApoio(276195, 40, 31313, DominioTipoObjetoCusto.AS);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}