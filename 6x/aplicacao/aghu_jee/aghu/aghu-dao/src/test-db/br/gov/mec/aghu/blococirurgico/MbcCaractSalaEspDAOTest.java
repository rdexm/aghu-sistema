package br.gov.mec.aghu.blococirurgico;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaractSalaEspId;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;

public class MbcCaractSalaEspDAOTest extends AbstractDAOTest<MbcCaractSalaEspDAO> {
	
	@Override
	protected MbcCaractSalaEspDAO doDaoUnderTests() {
		return new MbcCaractSalaEspDAO() {
			private static final long serialVersionUID = 3339428899204117479L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcCaractSalaEspDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {
	}


	@Test
	public void testBuscarOutrosHorariosCaractSala() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			
			MbcCaracteristicaSalaCirg carac = entityManager.find(MbcCaracteristicaSalaCirg.class, Short.valueOf("151"));
			Short seqp = 33;
			Date horaInicio = new Date();
			Date horaFim = new Date();

			try {
				List<MbcCaractSalaEsp> result = this.daoUnderTests.buscarOutrosHorariosCaractSala(carac, seqp, horaInicio, horaFim);
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (MbcCaractSalaEsp result1 : result) {
						logger.info("Resultado = " + result1);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	@Test
	public void pesquisarCaractSalaEspIds() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			Short unfSeq = Short.valueOf("126");
			Short espSeq = Short.valueOf("33");
			Date data = DateUtil.obterData(2013, Calendar.MAY, 30);
			MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId(951, Short.valueOf("4"), Short.valueOf("126"), DominioFuncaoProfissional.MPF);
			MbcProfAtuaUnidCirgs profAtuaUnidCirgs = entityManager.find(MbcProfAtuaUnidCirgs.class, id);
			AghEspecialidades especialidade = new AghEspecialidades();
			especialidade.setSeq(espSeq);
			try {
				List<MbcCaractSalaEspId> result = this.daoUnderTests.pesquisarCaractSalaEspIds(data, profAtuaUnidCirgs, unfSeq, especialidade, null);
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (MbcCaractSalaEspId result1 : result) {
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