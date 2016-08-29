package br.gov.mec.aghu.faturamento;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.casca.UsuarioApiDAOTest;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.dao.FatExclusaoCriticaDAO;
import br.gov.mec.aghu.model.FatExclusaoCritica;

public class FatExclusaoCriticaDAOTest extends AbstractDAOTest<FatExclusaoCriticaDAO> {

	@Override
	protected FatExclusaoCriticaDAO doDaoUnderTests() {
		return new FatExclusaoCriticaDAO() {

			private static final long serialVersionUID = -6861387702987718553L;

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return FatExclusaoCriticaDAOTest.this.runCriteriaUniqueResult(criteria);
			}

			@Override
			public boolean isOracle() {
				return FatExclusaoCriticaDAOTest.this.isOracle();
			}

		};
	}

	@Override
	protected void initMocks() {
		
	}
	
	@Test
	public void obterPorCodigoESituacao(){
		if(isEntityManagerOk()){
			
			final String codigo = "40";
			final DominioSituacao indSituacao = DominioSituacao.A;
			
			final FatExclusaoCritica exclusaoCritica = this.daoUnderTests.obterPorCodigoESituacao(codigo, indSituacao);
			
			if(exclusaoCritica != null){
				if(exclusaoCritica.getCodigo().equals(codigo) & exclusaoCritica.getIndSituacao().equals(indSituacao)){
					Assert.assertTrue(true);
				}else {
					Assert.assertTrue(false);
				}
				logger.info("");
			}
		}
	}

	@Override
	protected void finalizeMocks() {
		
	}
}