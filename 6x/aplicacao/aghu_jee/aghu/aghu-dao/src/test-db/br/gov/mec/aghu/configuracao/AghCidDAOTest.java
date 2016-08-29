package br.gov.mec.aghu.configuracao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.model.AghCid;

public class AghCidDAOTest extends AbstractDAOTest<AghCidDAO> {

	@Override
	protected AghCidDAO doDaoUnderTests() {
		return new AghCidDAO() {
			private static final long serialVersionUID = -1447104699448914128L;

			@Override
			protected Query createQuery(String query) {
				return AghCidDAOTest.this.createQuery(query);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return AghCidDAOTest.this.runCriteria(criteria);
			}
		};
	}


	@Override
	protected void initMocks() {

	}
	
	@Test
	public void testObterCidExamePorItemSolicitacaoExames() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			final Integer iseSoeSeq = 4648962;
			final Short iseSeqp = 1;
			final Integer qquQaoSeq = 34;
			try {
				String result = this.daoUnderTests.obterCidExamePorItemSolicitacaoExames(iseSoeSeq, iseSeqp, qquQaoSeq);
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Não retornou registros.");
				} else {
					logger.info("Retornou [" + result + "].");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void testObterRestricaoSexo() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				DominioSexoDeterminante result = this.daoUnderTests.obterRestricaoSexo("C02.3");
				logger.info("###############################################");
				if (result == null ) {
					logger.info("Não retornou registros.");
				} else {
					logger.info("Retornou [" + result + "].");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testObterCodigoAghCidsPorPhiSeq() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				String result = this.daoUnderTests.obterCodigoAghCidsPorPhiSeq(30731);
				logger.info("###############################################");
				if (result == null ) {
					logger.info("Não retornou registros.");
				} else {
					logger.info("Retornou [" + result + "].");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testListarAghCidPorIdadeSexo() {
		if (isEntityManagerOk()) {
			try {
				List<Long> listaCodSsm = new ArrayList<Long>();
				listaCodSsm.add(201010119L);
				Integer idade = 25;
				String cidCodigo = "H15.8";
				DominioSexoDeterminante sexo = DominioSexoDeterminante.Q;
				List<AghCid> result = this.daoUnderTests.listarAghCidPorIdadeSexo(listaCodSsm, idade, cidCodigo, sexo);
				logger.info("###############################################");
				if (result == null ) {
					logger.info("Não retornou registros.");
				} else {
					logger.info("Retornou [" + result + "].");
				}
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