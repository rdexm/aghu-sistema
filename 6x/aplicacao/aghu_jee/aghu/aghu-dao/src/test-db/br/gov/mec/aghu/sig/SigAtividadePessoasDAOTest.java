package br.gov.mec.aghu.sig;

import org.hibernate.Query;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.sig.dao.SigAtividadePessoasDAO;

/**
 * @author rogeriovieira
 */
public class SigAtividadePessoasDAOTest extends AbstractDAOTest<SigAtividadePessoasDAO> {

	@Override
	protected SigAtividadePessoasDAO doDaoUnderTests() {
		return new SigAtividadePessoasDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Query createHibernateQuery(String query) {
				return SigAtividadePessoasDAOTest.this.createHibernateQuery(query);
			}
		};
	}
	
	@Test
	public void pesquisarCalculoObjetoCustoTest() {
		daoUnderTests.buscarGruposOcupacaoAlocadosAtividade(40);
	}
	
	@Test
	public void pesquisarCalculoObjetoCustoApoioTest() {
		daoUnderTests.buscarGruposOcupacaoAlocadosAtividadeApoio(40);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}