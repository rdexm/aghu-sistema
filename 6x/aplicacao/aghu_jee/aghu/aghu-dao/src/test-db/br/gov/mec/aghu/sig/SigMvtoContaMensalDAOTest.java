package br.gov.mec.aghu.sig;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigMvtoContaMensalDAO;

/**
 * @author rmalvezzi
 */
public class SigMvtoContaMensalDAOTest extends AbstractDAOTest<SigMvtoContaMensalDAO> {

	@Override
	protected SigMvtoContaMensalDAO doDaoUnderTests() {
		return new SigMvtoContaMensalDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return SigMvtoContaMensalDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return SigMvtoContaMensalDAOTest.this.createHibernateQuery(query);
			}
			
			@Override
			protected javax.persistence.Query createQuery(String query) {
				return SigMvtoContaMensalDAOTest.this.createQuery(query);
			}
		};
	}

	@Test
	public void buscarSomatorioConsumoInsumoMaterialCentroCustoTest() {
		FccCentroCustos centroCusto = this.entityManager.find(FccCentroCustos.class, 10007);

		this.daoUnderTests.buscarSomatorioConsumoInsumoMaterialCentroCusto(this.entityManager.find(SigProcessamentoCusto.class, 9),
				this.entityManager.find(ScoMaterial.class, 40193), null, null, centroCusto);

		this.daoUnderTests.buscarSomatorioConsumoInsumoMaterialCentroCusto(this.entityManager.find(SigProcessamentoCusto.class, 9), null,
				this.entityManager.find(SigGrupoOcupacoes.class, 1), null, centroCusto);

		this.daoUnderTests.buscarSomatorioConsumoInsumoMaterialCentroCusto(this.entityManager.find(SigProcessamentoCusto.class, 9), null, null, "60752",
				centroCusto);
	}

	@Test
	public void buscarConsumoInsumoRateioTest() {
		this.daoUnderTests.buscarConsumoInsumoRateio(9, 44300, 44105);
	}

	@Test
	public void buscarDepreciacaoRateioEquipamentos() {
		this.daoUnderTests.buscarDepreciacaoRateioEquipamentos(10);
	}

	@Test
	public void buscarSaldoVidaUtilTest() {
		this.daoUnderTests.buscarSaldoVidaUtil(this.entityManager.find(ScoMaterial.class, 282344), this.entityManager.find(FccCentroCustos.class, 31313),
				this.entityManager.find(SigProcessamentoCusto.class, 40));
	}

	@Test
	public void buscarFolhaPessoalTest(){
		this.daoUnderTests.buscarFolhaPessoal(14, 31313, 2);
	}
	
	@Test
	public void buscarConsumoInsumoAlocadoAtividadesTest(){
		this.daoUnderTests.buscarConsumoInsumoAlocadoAtividades(14, 31313, 2);
	}
	
	@Test
	public void buscarValoresPessoalTest() {
		this.daoUnderTests.buscarValoresPessoal(14);
	}

	@Test
	public void buscarDetalheFolhaPessoalTest() {
		this.daoUnderTests.buscarDetalheFolhaPessoal(40, 31313, 293);
	}
	
	@Test
	public void buscarCustoMedioMaterialComUnidadeMedida() {
		this.daoUnderTests.buscarCustoMedioMaterialComUnidadeMedida(41, 31323, 20966);
	}

	

	

	@Test
	public void buscarMovimentosPessoasCentroCustoTest() {
		this.daoUnderTests.buscarMovimentosPessoasCentroCusto(4, 31313);
	}
	
	
	
	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}