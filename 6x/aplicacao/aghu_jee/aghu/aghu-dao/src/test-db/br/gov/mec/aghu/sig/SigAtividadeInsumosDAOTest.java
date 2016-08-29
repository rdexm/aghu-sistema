package br.gov.mec.aghu.sig;

import java.util.Date;

import org.hibernate.Query;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigAtividadeInsumosDAO;

/**
 * Classe de teste da SigAtividadeInsumosDAO
 * @author rhrosa
 *
 */
public class SigAtividadeInsumosDAOTest extends AbstractDAOTest<SigAtividadeInsumosDAO> {

	SigProcessamentoCusto processamentoCusto;
	
	@Override
	protected SigAtividadeInsumosDAO doDaoUnderTests() {
		return new SigAtividadeInsumosDAO() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected javax.persistence.Query createQuery(String query) {
				return SigAtividadeInsumosDAOTest.this.createQuery(query);
			};
			
			@Override
			protected Query createHibernateQuery(String query) {
				return SigAtividadeInsumosDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Test
	public void buscarInsumosAlocadosNaAtividadeAssistencialTest() {
		this.daoUnderTests.buscarInsumosAlocadosNaAtividadeAssistencial(processamentoCusto);
	}

	@Test
	public void buscaInsumosAlocadosNaAtividadeApoioTest() {
		this.daoUnderTests.buscaInsumosAlocadosNaAtividadeApoio(processamentoCusto);
	}

	@Test
	public void buscaDetalheInsumosConsumidosTest() {
		this.daoUnderTests.buscaDetalheInsumosConsumidos(40, 31313, 282344);
	}

	@Test
	public void buscaAtividadeValoresRealizadosCalculosAssistencialApoioTest() {
		this.daoUnderTests.buscaAtividadeValoresRealizadosCalculosAssistencialApoio(40, DominioTipoObjetoCusto.AS);
	}

	@Override
	protected void initMocks() {
		processamentoCusto = new SigProcessamentoCusto(4);
		processamentoCusto.setDataInicio(new Date());
		processamentoCusto.setCompetencia(new Date());
		processamentoCusto.setDataFim(new Date());
	}

	@Override
	protected void finalizeMocks() {
	}
}