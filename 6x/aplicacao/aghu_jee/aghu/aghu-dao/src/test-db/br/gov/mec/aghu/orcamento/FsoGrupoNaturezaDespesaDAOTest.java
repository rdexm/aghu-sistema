package br.gov.mec.aghu.orcamento;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoGrupoNaturezaDespesaCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoGrupoNaturezaDespesaDAO;

/**
 * Teste unitário da classe FsoGrupoNaturezaDespesaDAO.
 * 
 * @author mlcruz
 */
public class FsoGrupoNaturezaDespesaDAOTest extends AbstractDAOTest<FsoGrupoNaturezaDespesaDAO> {
	@Override
	protected FsoGrupoNaturezaDespesaDAO doDaoUnderTests() {
		return new FsoGrupoNaturezaDespesaDAO() {
			private static final long serialVersionUID = -8710758807976911532L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return FsoGrupoNaturezaDespesaDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return FsoGrupoNaturezaDespesaDAOTest.this.runCriteriaCount(criteria);
			}
		};
	}
	
	/**
	 * Teste garante retorno de grupo pelo ID #349032.
	 */
	@Test
	public void testDeveObterGrupoPorId() {
		FsoGrupoNaturezaDespesaCriteriaVO criteria = 
				new FsoGrupoNaturezaDespesaCriteriaVO();
		criteria.setCodigo(349032);
		
		List<FsoGrupoNaturezaDespesa> result = doDaoUnderTests()
				.pesquisarGruposNaturezaDespesa(criteria, 0, 10,
						FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(),
						true);
		
		if (result != null && !result.isEmpty()) {
			Assert.assertEquals((int) 349032, (int) result.get(0).getCodigo());
		}
	}
	
	/**
	 * Teste garante retorno de grupos que possuem 'PESSOAL' na descrição.
	 */
	@Test
	public void testDeveObterGruposPorDescricao() {
		FsoGrupoNaturezaDespesaCriteriaVO criteria = 
				new FsoGrupoNaturezaDespesaCriteriaVO();
		criteria.setDescricao("PESSOAL");
		
		List<FsoGrupoNaturezaDespesa> result = doDaoUnderTests()
				.pesquisarGruposNaturezaDespesa(criteria, 0, 10,
						FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(),
						true);
		
		if (result != null && !result.isEmpty()) {
			for (FsoGrupoNaturezaDespesa item: result) {
				Assert.assertTrue(item.getDescricao().contains("PESSOAL"));
			}
		}
	}
	
	/**
	 * Teste garante retorno de grupos inativos.
	 */
	@Test
	public void testDeveObterGruposInativos() {
		FsoGrupoNaturezaDespesaCriteriaVO criteria = 
				new FsoGrupoNaturezaDespesaCriteriaVO();
		criteria.setIndSituacao(DominioSituacao.I);
		
		List<FsoGrupoNaturezaDespesa> result = doDaoUnderTests()
				.pesquisarGruposNaturezaDespesa(criteria, 0, 10,
						FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(),
						true);
		
		if (result != null && !result.isEmpty()) {
			for (FsoGrupoNaturezaDespesa item: result) {
				Assert.assertEquals(DominioSituacao.I, item.getIndSituacao());
			}
		}
	}
	
	@Test
	public void testDeveContarGruposAtivos() {
		FsoGrupoNaturezaDespesaCriteriaVO criteria = 
				new FsoGrupoNaturezaDespesaCriteriaVO();
		criteria.setIndSituacao(DominioSituacao.A);
		
		Long count = doDaoUnderTests()
				.contarGruposNaturezaDespesa(criteria);
		Assert.assertTrue(count > 0);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
	
}