package br.gov.mec.aghu.estoque;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceRmrPacienteDAO;
import br.gov.mec.aghu.internacao.vo.SceRmrPacienteVO;

public class SceRmrPacienteDAOTest extends AbstractDAOTest<SceRmrPacienteDAO> {

	@Override
	protected SceRmrPacienteDAO doDaoUnderTests() {
		return new SceRmrPacienteDAO() {
			private static final long serialVersionUID = 6277633750353250441L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SceRmrPacienteDAOTest.this.runCriteria(criteria);
			}

			@Override
			public boolean isOracle() {
				return SceRmrPacienteDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {
	}

	@Test
	public void obterOriginalBuscaPorEntidade() {
		if (isEntityManagerOk()) {
			final Integer crgSeq = 623139;
			final DominioSituacao situacao = DominioSituacao.A;
			
			final List<SceRmrPacienteVO> result = this.daoUnderTests.listarSceRmrPacienteVOPorCirurgiaESituacao(crgSeq, situacao);

			Assert.assertTrue(!result.isEmpty());
		}
	}

	@Override
	protected void finalizeMocks() {
	}

}
