package br.gov.mec.aghu.registrocolaborador;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;

public class RapServidoresDAOTest extends AbstractDAOTest<RapServidoresDAO> {
	private static final Integer FIRST = 0;
	private static final Integer MAX = 10;
	private static final String ORDER = RapServidores.Fields.ID.toString();
	private static final Boolean ASC = true;

	@Override
	protected RapServidoresDAO doDaoUnderTests() {
		return new RapServidoresDAO() {
			private static final long serialVersionUID = 1958737117605018822L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return RapServidoresDAOTest.this.runCriteria(criteria);
			}
		
			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return RapServidoresDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return RapServidoresDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected Query createQuery(String query) {
				return RapServidoresDAOTest.this.createQuery(query);
			}
			
			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return RapServidoresDAOTest.this.runCriteriaCount(criteria);
			}
		};
	}
	
	/**
	 * Testa pesquisa de compradores ativos por matricula.
	 */
	@Test
	public void testPesquisaCompradoresAtivosPorMatricula() {
		doDaoUnderTests().pesquisarCompradoresAtivos("1", FIRST, MAX, ORDER, ASC);
	}
	
	@Test
	public void testBuscarNroRegistroConselho() {
		
		final Integer matricula = 14869;
		final Short vinCodigo = 1;
		
		String retorno = null;
		
		if (isEntityManagerOk()) {
			// assert
			try {
				
				retorno = doDaoUnderTests().buscarNroRegistroConselho(vinCodigo, matricula);
				
				if (retorno != null) {
					logger.info(retorno);
					System.out.println(retorno);
				} else {
					logger.info("Retornou null.");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	@Test
	public void testObterPrimeiroNroRegistroConselho() {
		
		final Integer matricula = 14869;
		final Short vinculo = 1;
		
		String retorno = null;
		
		if (isEntityManagerOk()) {
			// assert
			try {
				
				retorno = doDaoUnderTests().obterPrimeiroNroRegistroConselho(matricula, vinculo);
				
				if (retorno != null) {
					logger.info(retorno);
					System.out.println(retorno);
				} else {
					logger.info("Retornou null.");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	@Test
	public void testBuscaVRapServSolExme() {
		
		final Integer matricula = 14869;
		final Short vinculo = 1;
		final Integer diasServidorFimVinculoPermitidoSolicitarExame = null;
		final String numeroConselho = null;
		final String siglaConselho = null;
		
		RapServidores retorno = null;
		
		if (isEntityManagerOk()) {
			// assert
			try {
				retorno = doDaoUnderTests().buscaVRapServSolExme(vinculo, matricula, diasServidorFimVinculoPermitidoSolicitarExame, numeroConselho, siglaConselho);
			} catch (ApplicationBusinessException e) {
				logger.info(e.getMessage());
			}
			
			if (retorno != null) {
				logger.info(retorno);
				System.out.println(retorno);
			} else {
				logger.info("Retornou null.");
			}
		}
	}
	
	/**
	 * Testa pesquisa de compradores ativos por nome usual.
	 */
	@Test
	public void testPesquisaCompradoresAtivosPorNomeUsual() {
		doDaoUnderTests().pesquisarCompradoresAtivos("Nome Usual do Servidor", 
				FIRST, MAX, ORDER, ASC);
	}
	

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}
