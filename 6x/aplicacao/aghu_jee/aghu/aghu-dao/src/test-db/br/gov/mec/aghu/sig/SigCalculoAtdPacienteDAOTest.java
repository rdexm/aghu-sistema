package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdPacienteDAO;

/**
 * @author rmalvezzi
 */
public class SigCalculoAtdPacienteDAOTest extends AbstractDAOTest<SigCalculoAtdPacienteDAO> {

	@Override
	protected SigCalculoAtdPacienteDAO doDaoUnderTests() {
		return new SigCalculoAtdPacienteDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigCalculoAtdPacienteDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void buscarTodasInternacoesTest() {
		this.getDaoUnderTests().buscarTodasInternacoes(null);
		this.getDaoUnderTests().buscarTodasInternacoes(this.entityManager.find(SigProcessamentoCusto.class, 4));
	}

	@Test
	public void buscarAltasInternacaoTest() {
		this.getDaoUnderTests().buscarAltasInternacao(null);
		this.getDaoUnderTests().buscarAltasInternacao(this.entityManager.find(SigProcessamentoCusto.class, 4));
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}