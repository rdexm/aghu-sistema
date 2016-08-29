package br.gov.mec.aghu.blococirurgico;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;

public class MbcSalaCirurgicaDAOTest extends AbstractDAOTest<MbcSalaCirurgicaDAO> {
	
	@Override
	protected MbcSalaCirurgicaDAO doDaoUnderTests() {
		return new MbcSalaCirurgicaDAO() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 6536607079488461531L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcSalaCirurgicaDAOTest.this.runCriteria(criteria);
			}
		};
	}
	
	@Override
	protected void initMocks() {

	}
	
	@Test
	public void testValidarDataRemarcacaoAgendaEquipe() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			
			Date dtReagendamento = DateUtil.obterData(2010, 10, 8);
			
			MbcProfAtuaUnidCirgs atuaUnidCirgs = new MbcProfAtuaUnidCirgs();
			MbcProfAtuaUnidCirgsId atuaUnidCirgsId = new MbcProfAtuaUnidCirgsId(Integer.valueOf(24834), Short.valueOf("4"), Short.valueOf("126"), DominioFuncaoProfissional.MPF);
			atuaUnidCirgs.setId(atuaUnidCirgsId);
			
			Short unfSeq = Short.valueOf("126");
			
			try {
				List<MbcSalaCirurgica> result = this.daoUnderTests.validarDataRemarcacaoAgendaEquipe(atuaUnidCirgs, Short.valueOf("17"), unfSeq, dtReagendamento);
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
