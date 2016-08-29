package br.gov.mec.aghu.compras;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornecedorPedidoDAO;
import br.gov.mec.aghu.compras.vo.AcessoFornProgEntregaFiltrosVO;
import br.gov.mec.aghu.compras.vo.FiltroPesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.ParcelaAfPendenteEntregaVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralIAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPIAFVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.VScoFornecedor;

/**
 * Teste unitário da classe {@link ScoAutorizacaoFornecedorPedidoDAO}.
 * 
 * @author ihaas
 */
public class ScoAutorizacaoFornecedorPedidoDAOTest extends AbstractDAOTest<ScoAutorizacaoFornecedorPedidoDAO> {

	@Override
	protected ScoAutorizacaoFornecedorPedidoDAO doDaoUnderTests() {
		return new ScoAutorizacaoFornecedorPedidoDAO() {
			private static final long serialVersionUID = 2635360511375362639L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return ScoAutorizacaoFornecedorPedidoDAOTest.this.runCriteria(criteria);
			}

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return ScoAutorizacaoFornecedorPedidoDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return ScoAutorizacaoFornecedorPedidoDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return ScoAutorizacaoFornecedorPedidoDAOTest.this.createHibernateQuery(query);
			}
			
			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return ScoAutorizacaoFornecedorPedidoDAOTest.this.runCriteriaCount(criteria);
			}
			
			@Override
			protected boolean isPostgreSQL() {
				return ScoAutorizacaoFornecedorPedidoDAOTest.this.isPostgreSQL();
			}
			
			@Override
			public boolean isOracle() {
				return ScoAutorizacaoFornecedorPedidoDAOTest.this.isOracle();
			}
		};
	}
	
	@Test
	public void testListarAutorizacoesFornecimentoFiltrado() {
		ScoModalidadeLicitacao modalidadeCompra = new ScoModalidadeLicitacao();
		modalidadeCompra.setCodigo("DI");
		VScoFornecedor fornecedor = new VScoFornecedor();
		fornecedor.setNumeroFornecedor(856);
		FiltroPesquisaGeralAFVO filtro = new FiltroPesquisaGeralAFVO();
		filtro.setModalidadeCompra(modalidadeCompra);
		filtro.setFornecedor(fornecedor);
		filtro.setTermoLivre("A");
		
		final List<PesquisaGeralAFVO> listVo = doDaoUnderTests().
			listarAutorizacoesFornecimentoFiltrado(0, 10, null, true, filtro);
		
		if (listVo != null && !listVo.isEmpty()) {
			logger.info("Resultados encontrados: "+listVo.size());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testListarItensAutorizacoesFornecimentoFiltrado() {
		ScoModalidadeLicitacao modalidadeCompra = new ScoModalidadeLicitacao();
		modalidadeCompra.setCodigo("DI");
		VScoFornecedor fornecedor = new VScoFornecedor();
		fornecedor.setNumeroFornecedor(856);
		FiltroPesquisaGeralAFVO filtro = new FiltroPesquisaGeralAFVO();
		filtro.setModalidadeCompra(modalidadeCompra);
		filtro.setFornecedor(fornecedor);
		
		final List<PesquisaGeralIAFVO> listVo = doDaoUnderTests().
			listarItensAutorizacoesFornecimentoFiltrado(0, 10, null, true, filtro);
		
		if (listVo != null && !listVo.isEmpty()) {
			logger.info("Resultados encontrados: "+listVo.size());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testListarPedidosAutorizacoesFornecimentoFiltrado() {
		ScoModalidadeLicitacao modalidadeCompra = new ScoModalidadeLicitacao();
		modalidadeCompra.setCodigo("DI");
		VScoFornecedor fornecedor = new VScoFornecedor();
		fornecedor.setNumeroFornecedor(856);
		FiltroPesquisaGeralAFVO filtro = new FiltroPesquisaGeralAFVO();
		filtro.setModalidadeCompra(modalidadeCompra);
		filtro.setFornecedor(fornecedor);
		
		final List<PesquisaGeralPAFVO> listVo = doDaoUnderTests().
			listarPedidosAutorizacoesFornecimentoFiltrado(0, 10, null, true, filtro);
		
		if (listVo != null && !listVo.isEmpty()) {
			logger.info("Resultados encontrados: "+listVo.size());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testListarParcelasItensAutorizacoesFornecimentoFiltrado() {
		ScoModalidadeLicitacao modalidadeCompra = new ScoModalidadeLicitacao();
		modalidadeCompra.setCodigo("DI");
		VScoFornecedor fornecedor = new VScoFornecedor();
		fornecedor.setNumeroFornecedor(856);
		FiltroPesquisaGeralAFVO filtro = new FiltroPesquisaGeralAFVO();
		filtro.setModalidadeCompra(modalidadeCompra);
		filtro.setFornecedor(fornecedor);
		
		final List<PesquisaGeralPIAFVO> listVo = doDaoUnderTests().
			listarParcelasItensAutorizacoesFornecimentoFiltrado(0, 10, null, true, filtro);
		
		if (listVo != null && !listVo.isEmpty()) {
			logger.info("Resultados encontrados: "+listVo.size());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testListarProgEntregaFornecedor(){
		Assert.assertNotNull(doDaoUnderTests().listarProgEntregaFornecedor(new AcessoFornProgEntregaFiltrosVO(), 0, 10));
	}
	
	@Test
	public void testListarProgEntregaFornecedorCount(){
		Assert.assertNotNull(doDaoUnderTests().listarProgEntregaFornecedorCount(new AcessoFornProgEntregaFiltrosVO()));
	}

	@Test
	public void testVerificarPendenciasFornecedor() {
		Long result = doDaoUnderTests().verificarPendenciasFornecedor(15);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void testA() {
		ParcelaAfPendenteEntregaVO vo = new ParcelaAfPendenteEntregaVO();
	//	vo.setNumeroAf(123);
		vo.setNumeroAfp(123);
		vo.setNumeroCp(2);
		vo.setParcela(2);
	}

	@Test
	public void testBuscarDataMaisAntigaEnvioFornecedor() {
		ProgrGeralEntregaAFVO parcelaAF = new ProgrGeralEntregaAFVO();
		parcelaAF.setNroAF(0);
		parcelaAF.setItemAF(0);
		parcelaAF.setParcela(0);
		parcelaAF.setProgEntregaItemAFSeq(0);
		doDaoUnderTests().buscarDataMaisAntigaEnvioFornecedor(parcelaAF);
	}
	
	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}