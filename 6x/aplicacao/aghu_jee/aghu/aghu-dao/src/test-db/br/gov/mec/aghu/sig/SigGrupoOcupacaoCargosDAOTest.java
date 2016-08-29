package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapOcupacoesCargoId;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.sig.dao.SigGrupoOcupacaoCargosDAO;

/**
 * @author rmalvezzi
 */
public class SigGrupoOcupacaoCargosDAOTest extends AbstractDAOTest<SigGrupoOcupacaoCargosDAO> {

	@Override
	protected SigGrupoOcupacaoCargosDAO doDaoUnderTests() {
		return new SigGrupoOcupacaoCargosDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigGrupoOcupacaoCargosDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void obterPorOcupacaoCargoTest() {
		this.getDaoUnderTests().obterPorOcupacaoCargo(this.entityManager.find(RapOcupacaoCargo.class, new RapOcupacoesCargoId("", 1)));
	}

	@Test
	public void obterPorGrupoOcupacaoTest() {
		this.getDaoUnderTests().obterPorGrupoOcupacao(this.entityManager.find(SigGrupoOcupacoes.class, 1));
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}