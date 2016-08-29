package br.gov.mec.aghu.compras;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoDescricaoTecnicaPadraoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.vo.ScoDescricaoTecnicaPadraoVO;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.ScoDescricaoTecnicaPadrao;
import br.gov.mec.aghu.model.ScoMaterial;

/**
 * Teste unitário da classe {@link ScoSolicitacoesDeComprasDAO}.
 * 
 * @author mlcruz
 */
public class ScoDescricaoTecnicaPadraoDAOTest extends AbstractDAOTest<ScoDescricaoTecnicaPadraoDAO> {

	@Override
	protected ScoDescricaoTecnicaPadraoDAO doDaoUnderTests() {
		
		return new ScoDescricaoTecnicaPadraoDAO() {
			private static final long serialVersionUID = -3875786989221354225L;
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return ScoDescricaoTecnicaPadraoDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return ScoDescricaoTecnicaPadraoDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void testeList() {
		ScoDescricaoTecnicaPadraoVO vo = new ScoDescricaoTecnicaPadraoVO();
		
		List<ScoDescricaoTecnicaPadrao> result = this.doDaoUnderTests().listarDescricaoTecnica(5, 5, "codigo", false, vo, new ScoMaterial());
		
		Assert.assertNotNull(result);
	}
	
	@Test
	public void testeListByCodigoMaterial() {
		ScoDescricaoTecnicaPadraoVO vo = new ScoDescricaoTecnicaPadraoVO();
		vo.setCodigoMaterial(38);
		
		List<ScoDescricaoTecnicaPadrao> result = this.doDaoUnderTests().listarDescricaoTecnica(5, 5, "titulo", false, vo, new ScoMaterial());
		
		Assert.assertNotNull(result);
	}
	
	@Test
	public void testBuscarListaDescricaoTecnicaMaterial() {
		ScoMaterial material = new ScoMaterial();
		material.setCodigo(19);
		
		List<ScoDescricaoTecnicaPadrao> result = this.daoUnderTests.buscarListaDescricaoTecnicaMaterial(material);
		
		Assert.assertNotNull(result);
	}
	
	
	@Test
	public void testExibirRelatorio() {
		Integer codigo = 19 ;
		Assert.assertFalse(doDaoUnderTests().obterRelatorioDescricaoTecnica(codigo.shortValue()).size()==0);
	}

	@Test
	public void testNaoExibirRelatorio() {
		Assert.assertTrue(doDaoUnderTests().obterRelatorioDescricaoTecnica(null).size()==0);
	}

	@Override
	protected void finalizeMocks() {
		
	}

	@Override
	protected void initMocks() {
		
	}
}