package br.gov.mec.aghu.sig;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.CuidadosEnfermagemVO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoPhisDAO;

/**
 * @author rmalvezzi
 */
public class SigObjetoCustoPhisDAOTest extends AbstractDAOTest<SigObjetoCustoPhisDAO> {

	SigProcessamentoCusto processamentoCusto;
    AghAtendimentos atendimento;

	@Override
	protected SigObjetoCustoPhisDAO doDaoUnderTests() {
		return new SigObjetoCustoPhisDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected javax.persistence.Query createQuery(String query) {
				return SigObjetoCustoPhisDAOTest.this.createQuery(query);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return SigObjetoCustoPhisDAOTest.this.createHibernateQuery(query);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigObjetoCustoPhisDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void buscarExamesInternacaoTest() {
		this.daoUnderTests.buscarExamesInternacao(this.atendimento, this.processamentoCusto);
	}

	@Test
	public void pesquisarPhiPorObjetoCustoVersaoTest() {
		this.daoUnderTests.pesquisarPhiPorObjetoCustoVersao(1);
	}

	/**
	 * Teste da consulta buscarCuidadosEnfermagemTest
	 * @author rogeriovieira
	 */
	@Test
	public void buscarCuidadosEnfermagemTest() throws IllegalArgumentException, IllegalAccessException {
		List<CuidadosEnfermagemVO> cuidados = this.daoUnderTests.buscarCuidadosEnfermagem(1, new Date(), new Date());
		this.logger.warn("Quantidade de registros retornados: " + cuidados.size());
	}

	@Override
	protected void initMocks() {
		 processamentoCusto = new SigProcessamentoCusto(4);
         processamentoCusto.setDataInicio(new Date());
         processamentoCusto.setCompetencia(new Date());
         processamentoCusto.setDataFim(new Date());

         atendimento = new AghAtendimentos(10);
	}

	@Override
	protected void finalizeMocks() {
	}
}