package br.gov.mec.aghu.blococirurgico;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;

public class MbcCaracteristicaSalaCirgDAOTest extends AbstractDAOTest<MbcCaracteristicaSalaCirgDAO> {
	
	@Override
	protected MbcCaracteristicaSalaCirgDAO doDaoUnderTests() {
		return new MbcCaracteristicaSalaCirgDAO() {
			
			private static final long serialVersionUID = 7501805938170456262L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcCaracteristicaSalaCirgDAOTest.this.runCriteria(criteria);
			}
		};
	}
	
	@Override
	protected void initMocks() {
	}

	@Test
	public void testBuscarHorariosCaractPorSalaCirurgica() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				List<MbcCaracteristicaSalaCirg> result = this.daoUnderTests.buscarHorariosCaractPorSalaCirurgica(Short.valueOf("131"), Short.valueOf("6"));
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (MbcCaracteristicaSalaCirg result1 : result) {
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
	public void testPesquisarDiasSemana() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId(949, Short.valueOf("4"), Short.valueOf("131"), DominioFuncaoProfissional.MPF);
			MbcProfAtuaUnidCirgs profAtuaUnidCirgs = entityManager.find(MbcProfAtuaUnidCirgs.class, id);
			AghEspecialidades especialidade = new AghEspecialidades();
			especialidade.setSeq(Short.valueOf("33"));			
			try {
				List<DominioDiaSemana> result = this.daoUnderTests.pesquisarDiasSemana(profAtuaUnidCirgs,Short.valueOf("131"), especialidade, null, false);
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (DominioDiaSemana result1 : result) {
						logger.info("Resultado = " + result1.getDescricao());
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	
	@Test
	public void testPesquisarDiasSemanaPorTurno() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId(949, Short.valueOf("4"), Short.valueOf("126"), DominioFuncaoProfissional.MPF);
			MbcProfAtuaUnidCirgs profAtuaUnidCirgs = entityManager.find(MbcProfAtuaUnidCirgs.class, id);
			Date dtBase = DateUtil.obterData(2013, Calendar.MAY, 30);
			Short unfSeq = Short.valueOf("126");
			Short espSeq = Short.valueOf("33");
			AghEspecialidades especialidade = new AghEspecialidades();
			especialidade.setSeq(espSeq);		
			
			try {
				List<MbcCaracteristicaSalaCirg> result = this.daoUnderTests.pesquisarDiasSemanaPorTurno(dtBase, profAtuaUnidCirgs, unfSeq, especialidade,null);
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (MbcCaracteristicaSalaCirg result1 : result) {
						logger.info("Resultado = " + result1.getNomeSalaCirurgica());
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