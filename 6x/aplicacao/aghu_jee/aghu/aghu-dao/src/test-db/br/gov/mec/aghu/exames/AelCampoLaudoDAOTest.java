package br.gov.mec.aghu.exames;

import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;

public class AelCampoLaudoDAOTest extends AbstractDAOTest<AelCampoLaudoDAO> {
	
	@Override
	protected AelCampoLaudoDAO doDaoUnderTests() {
		return new AelCampoLaudoDAO() {
			private static final long serialVersionUID = 5218850679388516246L;

			@Override
			protected Query createQuery(String query) {
				return AelCampoLaudoDAOTest.this.createQuery(query);
			}
		};
	}

	@Override
	protected void initMocks() {
	}

	@Test
	public void testObterResultadoPadraoTipoCampoESituacaoSemValor() {
		if (this.isEntityManagerOk()) {
			try {
				AelCampoLaudo result = this.daoUnderTests.obterResultadoPadraoTipoCampoESituacao(null, null); 
				Assert.assertTrue(result == null);
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testObterResultadoPadraoTipoCampoESituacaoComValor() {
		if (this.isEntityManagerOk()) {
			try {
				AelCampoLaudo result = this.daoUnderTests.obterResultadoPadraoTipoCampoESituacao(
						Integer.valueOf(188), Integer.valueOf(3));
				logger.info("Tipo : " + result.getTipoCampo());
				logger.info("Situacao : " + result.getSituacao());
				Assert.assertTrue(result != null);
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e.getMessage());
			}
		}
	}

	@Override
	protected void finalizeMocks() {
	}
}