package br.gov.mec.aghu.faturamento;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.casca.UsuarioApiDAOTest;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.faturamento.dao.FatExmPacPreTransDAO;
import br.gov.mec.aghu.model.FatExmPacPreTrans;

public class FatExmPacPreTransDAOTest extends AbstractDAOTest<FatExmPacPreTransDAO> {
	
	@Override
	protected FatExmPacPreTransDAO doDaoUnderTests() {
		return new FatExmPacPreTransDAO() {
			private static final long serialVersionUID = -2220285789400697606L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatExmPacPreTransDAOTest.this.runCriteria(criteria);
			}			
			@Override
			public boolean isOracle() {
				return FatExmPacPreTransDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {}
	
	@Test
	public void obterFatExmPacPreTransPorPacienteETipoTratamento(){
		if (isEntityManagerOk()) {
		    Integer pacCodigo = 2801937;
			Integer tptSeq = 24;
			final List<FatExmPacPreTrans> vos = daoUnderTests.obterFatExmPacPreTransPorPacienteETipoTratamento(pacCodigo, tptSeq);
		    logger.info(vos.size());
		    Assert.assertNotNull(vos);
		}
	}

	@Override
	protected void finalizeMocks() {}
}