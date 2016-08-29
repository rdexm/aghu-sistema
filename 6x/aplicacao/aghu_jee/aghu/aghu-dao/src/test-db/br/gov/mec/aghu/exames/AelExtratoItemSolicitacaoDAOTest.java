package br.gov.mec.aghu.exames;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;

public class AelExtratoItemSolicitacaoDAOTest extends AbstractDAOTest<AelExtratoItemSolicitacaoDAO> {
	
	@Override
	protected AelExtratoItemSolicitacaoDAO doDaoUnderTests() {
		return new AelExtratoItemSolicitacaoDAO() {
			private static final long serialVersionUID = -5371483969094159933L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return AelExtratoItemSolicitacaoDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {

	}
	
	@Override
	protected void finalizeMocks() {
		
	}
	
	@Test
	public void pesquisarExamesPorAtendimento() {
		try {
			List<AelExtratoItemSolicitacao> pesquisarExamesPorAtendimento = getDaoUnderTests()
					.pesquisarExamesPorAtendimento(1);
			Assert.assertTrue((pesquisarExamesPorAtendimento == null || pesquisarExamesPorAtendimento.isEmpty()));
		} catch (Exception e) {
			Assert.fail("Not expecting exception: " + e);
		}
	}
}