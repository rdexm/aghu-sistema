package br.gov.mec.aghu.blococirurgico;

import java.util.Calendar;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;

public class MbcHorarioTurnoCirgDAOTest extends AbstractDAOTest<MbcHorarioTurnoCirgDAO> {
	
	@Override
	protected MbcHorarioTurnoCirgDAO doDaoUnderTests() {
		return new MbcHorarioTurnoCirgDAO() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 6536607079488461531L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcHorarioTurnoCirgDAOTest.this.runCriteria(criteria);
			}
		};
	}
	
	@Override
	protected void initMocks() {
	}

	@Test
	public void buscarSalasTurnosHorariosDisponiveisUnion1() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				MbcAgendas agendas = new MbcAgendas();
				MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId(Integer.valueOf(1297), Short.valueOf("4"), Short.valueOf("131"), DominioFuncaoProfissional.MPF);
				MbcProfAtuaUnidCirgs profAtuaUnidCirgs = entityManager.find(MbcProfAtuaUnidCirgs.class, id);
				agendas.setProfAtuaUnidCirgs(profAtuaUnidCirgs);
				AghEspecialidades esp = new AghEspecialidades();
				esp.setSeq(Short.valueOf("206"));
				agendas.setEspecialidade(esp);
				agendas.setDtAgenda(DateUtil.obterData(2007, Calendar.MARCH, 1));
				
				
				
				List<Object[]> result = this.daoUnderTests.buscarSalasTurnosHorariosDisponiveisUnion1(agendas);
				logger.info("###############################################");
				if (result == null) {
					logger.info("Retornou null.");
				} else {
					logger.info("Resultado = " + result);
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
