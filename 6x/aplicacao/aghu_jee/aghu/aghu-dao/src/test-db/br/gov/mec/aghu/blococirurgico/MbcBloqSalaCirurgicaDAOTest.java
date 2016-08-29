package br.gov.mec.aghu.blococirurgico;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcBloqSalaCirurgicaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;

public class MbcBloqSalaCirurgicaDAOTest extends AbstractDAOTest<MbcBloqSalaCirurgicaDAO> {
	
	@Override
	protected MbcBloqSalaCirurgicaDAO doDaoUnderTests() {
		return new MbcBloqSalaCirurgicaDAO() {
			private static final long serialVersionUID = 2513349051645182221L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcBloqSalaCirurgicaDAOTest.this.runCriteria(criteria);
			}
		};
	}
	
	@Override
	protected void initMocks() {
	}

	@Test
	public void testBuscarDataCirurgias() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			MbcSalaCirurgicaId idSala = new MbcSalaCirurgicaId(Short.valueOf("126"), Short.valueOf("10"));
			MbcSalaCirurgica salaCirurg = entityManager.find(MbcSalaCirurgica.class, idSala);
			
			MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId(1312, Short.valueOf("4"), Short.valueOf("126"), DominioFuncaoProfissional.MPF);
			MbcProfAtuaUnidCirgs profAtuaUnidCirgs = entityManager.find(MbcProfAtuaUnidCirgs.class, id);

			try {
				List<MbcBloqSalaCirurgica> result = this.daoUnderTests.buscarBloqSalaPorCaractSalaEsp(salaCirurg, profAtuaUnidCirgs);
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (MbcBloqSalaCirurgica result1 : result) {
						logger.info("Resultado = " + result1);
					}
				}
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