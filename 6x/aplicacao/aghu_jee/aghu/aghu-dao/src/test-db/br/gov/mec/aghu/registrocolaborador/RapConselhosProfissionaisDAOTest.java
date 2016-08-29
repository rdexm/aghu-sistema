package br.gov.mec.aghu.registrocolaborador;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.registrocolaborador.dao.RapConselhosProfissionaisDAO;

public class RapConselhosProfissionaisDAOTest extends AbstractDAOTest<RapConselhosProfissionaisDAO> {

	@Override
	protected RapConselhosProfissionaisDAO doDaoUnderTests() {
		return new RapConselhosProfissionaisDAO() {

			private static final long serialVersionUID = 1958737447605018822L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return RapConselhosProfissionaisDAOTest.this.runCriteria(criteria);
			}
		};
	}
	
	@Test
	public void testObterConselhoProfissionalComNroRegConselho() {
		
		final Integer matricula = 14869;
		final Short vinculo = 1;
		final DominioSituacao situacao = DominioSituacao.A;

		RapConselhosProfissionais rapConselhoProfissional = null;
		
		if (isEntityManagerOk()) {
			// assert
			rapConselhoProfissional = this.doDaoUnderTests().obterConselhoProfissionalComNroRegConselho(matricula, vinculo, situacao);
			
			if (rapConselhoProfissional != null && rapConselhoProfissional.getSigla() != null) {
				logger.info(rapConselhoProfissional.getSigla());
				System.out.println(rapConselhoProfissional.getSigla());
			} else {
				logger.info("Retornou null.");
			}
		}
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}