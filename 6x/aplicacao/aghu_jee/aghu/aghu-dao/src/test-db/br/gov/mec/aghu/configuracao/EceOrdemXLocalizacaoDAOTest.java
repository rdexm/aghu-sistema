package br.gov.mec.aghu.configuracao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.checagemeletronica.dao.EceOrdemXLocalizacaoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.EceOrdemXLocalizacao;

public class EceOrdemXLocalizacaoDAOTest extends AbstractDAOTest<EceOrdemXLocalizacaoDAO> {

	@Override
	protected void initMocks() {
		
	}

	@Override
	protected EceOrdemXLocalizacaoDAO doDaoUnderTests() {
		return new EceOrdemXLocalizacaoDAO() {
			private static final long serialVersionUID = -667477804974301544L;

			@Override
			protected Query createQuery(String query) {
				return EceOrdemXLocalizacaoDAOTest.this.createQuery(query);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return EceOrdemXLocalizacaoDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return EceOrdemXLocalizacaoDAOTest.this.runCriteriaCount(criteria);
			}
		};
	}

	@Test
	public void executarBuscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao() throws ApplicationBusinessException {

		if (isEntityManagerOk()) {
			// assert
			try {
				List<EceOrdemXLocalizacao> result = this.daoUnderTests.buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao(3390840, new Date(), (short) 117);
				if (result == null || result.isEmpty()) {
					logger.info("Não retornou registros.");
				} else {
					logger.info("Retornou " + result.size() + " resultados.");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}

	@Test
	public void executarBuscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao2() throws ApplicationBusinessException {

		if (isEntityManagerOk()) {
			// assert
			try {
				List<EceOrdemXLocalizacao> result = this.daoUnderTests.buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao(3390840, (short) 117, (short) 931, "0931B");
				if (result == null || result.isEmpty()) {
					logger.info("Não retornou registros.");
				} else {
					logger.info("Retornou " + result.size() + " resultados.");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}

	@Test
	public void executarExisteLocalizacao1() throws ApplicationBusinessException,
			ApplicationBusinessException {
		if (isEntityManagerOk()) {
			// assert
			try {
				boolean result = this.daoUnderTests.existeLocalizacao1(3390840,
						new Date(), (short) 117);

				logger.info("Retornou [" + result + "].");
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}

	@Override
	protected void finalizeMocks() {
	}
}