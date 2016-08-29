package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.estoque.dao.SceDevolucaoAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemDas;
import br.gov.mec.aghu.model.SceItemDasId;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author diego.pacheco
 *
 */
public class ItemDevolucaoAlmoxarifadoRNTest extends AGHUBaseUnitTest<ItemDevolucaoAlmoxarifadoRN>{

	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	@Mock
	private SceDevolucaoAlmoxarifadoDAO mockedSceDevolucaoAlmoxarifadoDAO;
	
	@Test
	public void testVerificarEstoqueAlmoxarifadoSuccess() {
		final SceItemDasId itemDaId = new SceItemDasId(2, 3);
		final SceItemDas itemDa = new SceItemDas();
		itemDa.setId(itemDaId);
		final SceEstoqueAlmoxarifado estoqueAlmoxarifado = new SceEstoqueAlmoxarifado();

		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.obterPorChavePrimaria(itemDa.getId().getEalSeq())).thenReturn(estoqueAlmoxarifado);
		
		try {
			systemUnderTest.verificarEstoqueAlmoxarifado(itemDa);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarEstoqueAlmoxarifadoError() {
		final SceItemDasId itemDaId = new SceItemDasId(2, 3);
		final SceItemDas itemDa = new SceItemDas();
		itemDa.setId(itemDaId);

		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.obterPorChavePrimaria(itemDa.getId().getEalSeq())).thenReturn(null);

		try {
			systemUnderTest.verificarEstoqueAlmoxarifado(itemDa);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ItemDevolucaoAlmoxarifadoRN.ItemDevolucaoAlmoxarifadoRNExceptionCode.SCE_00292);
		}
	}	
	
	@Test
	public void testVerificarDevolucaoUnidadeMedidaSuccess() {
		final SceItemDas itemDa = new SceItemDas();
		final String matCodigo = "ABC";
		final SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
		almoxarifado.setSeq(Short.valueOf("2"));
		final ScoMaterial material = new ScoMaterial();
		material.setCodigo(10);
		final ScoFornecedor fornecedor = new ScoFornecedor();
		fornecedor.setNumero(1);
		final ScoUnidadeMedida unidMedida = new ScoUnidadeMedida();
		unidMedida.setCodigo("UN");
		final SceEstoqueAlmoxarifado estoqueAlmoxarifadoUnidMedida = new SceEstoqueAlmoxarifado();
		final SceEstoqueAlmoxarifado estoqueAlmoxarifadoItemDa;
		estoqueAlmoxarifadoItemDa = new SceEstoqueAlmoxarifado();
		estoqueAlmoxarifadoItemDa.setSeq(6);
		estoqueAlmoxarifadoItemDa.setAlmoxarifado(almoxarifado);
		estoqueAlmoxarifadoItemDa.setMaterial(material);
		estoqueAlmoxarifadoItemDa.setFornecedor(fornecedor);
		estoqueAlmoxarifadoItemDa.setUnidadeMedida(unidMedida);
		itemDa.setEstoqueAlmoxarifado(estoqueAlmoxarifadoItemDa);
		itemDa.setUnidadeMedida(unidMedida);			
		final List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = new ArrayList<SceEstoqueAlmoxarifado>();
		listaEstoqueAlmoxarifado.add(estoqueAlmoxarifadoItemDa);
		
		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.obterEstoqueAlmoxarifadoPorSeqEUmdCodigo(Mockito.anyInt(), Mockito.anyString())).thenReturn(estoqueAlmoxarifadoUnidMedida);

		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.buscarEstoqueAlmoxarifadoPorAlmSeqMatCodigoEFrnNumero(Mockito.anyShort(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(listaEstoqueAlmoxarifado);
		
		try {
			systemUnderTest.verificarDevolucaoUnidadeMedida(itemDa, matCodigo);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarDevolucaoUnidadeMedidaError01() {
		final SceItemDas itemDa = new SceItemDas();
		final String matCodigo = "ABC";
		final SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
		almoxarifado.setSeq(Short.valueOf("2"));
		final ScoMaterial material = new ScoMaterial();
		material.setCodigo(10);
		final ScoFornecedor fornecedor = new ScoFornecedor();
		fornecedor.setNumero(1);
		final ScoUnidadeMedida unidMedida = new ScoUnidadeMedida();
		unidMedida.setCodigo("UN");
		final SceEstoqueAlmoxarifado estoqueAlmoxarifadoUnidMedida = new SceEstoqueAlmoxarifado();
		final SceEstoqueAlmoxarifado estoqueAlmoxarifadoItemDa;
		estoqueAlmoxarifadoItemDa = new SceEstoqueAlmoxarifado();
		estoqueAlmoxarifadoItemDa.setSeq(6);
		estoqueAlmoxarifadoItemDa.setAlmoxarifado(almoxarifado);
		estoqueAlmoxarifadoItemDa.setMaterial(material);
		estoqueAlmoxarifadoItemDa.setFornecedor(fornecedor);
		estoqueAlmoxarifadoItemDa.setUnidadeMedida(unidMedida);
		itemDa.setEstoqueAlmoxarifado(estoqueAlmoxarifadoItemDa);
		itemDa.setUnidadeMedida(unidMedida);			
		final List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = new ArrayList<SceEstoqueAlmoxarifado>();
		
		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.obterEstoqueAlmoxarifadoPorSeqEUmdCodigo(Mockito.anyInt(), Mockito.anyString())).thenReturn(estoqueAlmoxarifadoUnidMedida);

		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.buscarEstoqueAlmoxarifadoPorAlmSeqMatCodigoEFrnNumero(Mockito.anyShort(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(listaEstoqueAlmoxarifado);

		try {
			systemUnderTest.verificarDevolucaoUnidadeMedida(itemDa, matCodigo);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ItemDevolucaoAlmoxarifadoRN.ItemDevolucaoAlmoxarifadoRNExceptionCode.ERRO_ESTOQUE_ALMOXARIFADO_NAO_CADASTRADO_OU_INATIVO);
		}
	}
	
	@Test
	public void testVerificarDevolucaoUnidadeMedidaError02() {
		final SceItemDas itemDa = new SceItemDas();
		final String matCodigo = "ABC";
		final SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
		almoxarifado.setSeq(Short.valueOf("2"));
		final ScoMaterial material = new ScoMaterial();
		material.setCodigo(10);
		final ScoFornecedor fornecedor = new ScoFornecedor();
		fornecedor.setNumero(1);
		final ScoUnidadeMedida unidMedida = new ScoUnidadeMedida();
		unidMedida.setCodigo("UN");
		final SceEstoqueAlmoxarifado estoqueAlmoxarifadoUnidMedida = null;
		final SceEstoqueAlmoxarifado estoqueAlmoxarifadoItemDa;
		estoqueAlmoxarifadoItemDa = new SceEstoqueAlmoxarifado();
		estoqueAlmoxarifadoItemDa.setSeq(6);
		estoqueAlmoxarifadoItemDa.setAlmoxarifado(almoxarifado);
		estoqueAlmoxarifadoItemDa.setMaterial(material);
		estoqueAlmoxarifadoItemDa.setFornecedor(fornecedor);
		estoqueAlmoxarifadoItemDa.setUnidadeMedida(unidMedida);
		itemDa.setEstoqueAlmoxarifado(estoqueAlmoxarifadoItemDa);
		final ScoUnidadeMedida itemUnidMedida = new ScoUnidadeMedida();
		itemUnidMedida.setCodigo("PC");
		itemDa.setUnidadeMedida(itemUnidMedida);			
		final List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = new ArrayList<SceEstoqueAlmoxarifado>();
		listaEstoqueAlmoxarifado.add(estoqueAlmoxarifadoItemDa);

		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.obterEstoqueAlmoxarifadoPorSeqEUmdCodigo(Mockito.anyInt(), Mockito.anyString())).thenReturn(estoqueAlmoxarifadoUnidMedida);

		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.buscarEstoqueAlmoxarifadoPorAlmSeqMatCodigoEFrnNumero(Mockito.anyShort(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(listaEstoqueAlmoxarifado);

		try {
			systemUnderTest.verificarDevolucaoUnidadeMedida(itemDa, matCodigo);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ItemDevolucaoAlmoxarifadoRN.ItemDevolucaoAlmoxarifadoRNExceptionCode.SCE_00280);
		}
	}
	
	@Test
	public void testVerificarDevolucaoUnidadeMedidaError03() {
		final SceItemDas itemDa = new SceItemDas();
		final String matCodigo = null;
		final SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
		almoxarifado.setSeq(Short.valueOf("2"));
		final ScoMaterial material = new ScoMaterial();
		material.setCodigo(10);
		final ScoFornecedor fornecedor = new ScoFornecedor();
		fornecedor.setNumero(1);
		final ScoUnidadeMedida unidMedida = new ScoUnidadeMedida();
		unidMedida.setCodigo("UN");
		final SceEstoqueAlmoxarifado estoqueAlmoxarifadoUnidMedida = null;
		final SceEstoqueAlmoxarifado estoqueAlmoxarifadoItemDa;
		estoqueAlmoxarifadoItemDa = new SceEstoqueAlmoxarifado();
		estoqueAlmoxarifadoItemDa.setSeq(6);
		estoqueAlmoxarifadoItemDa.setAlmoxarifado(almoxarifado);
		estoqueAlmoxarifadoItemDa.setMaterial(material);
		estoqueAlmoxarifadoItemDa.setFornecedor(fornecedor);
		estoqueAlmoxarifadoItemDa.setUnidadeMedida(unidMedida);
		itemDa.setEstoqueAlmoxarifado(estoqueAlmoxarifadoItemDa);
		itemDa.setUnidadeMedida(unidMedida);			
		final List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = new ArrayList<SceEstoqueAlmoxarifado>();

		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.obterEstoqueAlmoxarifadoPorSeqEUmdCodigo(Mockito.anyInt(), Mockito.anyString())).thenReturn(estoqueAlmoxarifadoUnidMedida);

		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.buscarEstoqueAlmoxarifadoPorAlmSeqMatCodigoEFrnNumero(Mockito.anyShort(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(listaEstoqueAlmoxarifado);

		try {
			systemUnderTest.verificarDevolucaoUnidadeMedida(itemDa, matCodigo);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ItemDevolucaoAlmoxarifadoRN.ItemDevolucaoAlmoxarifadoRNExceptionCode.SCE_00280);
		}
	}	
	
	@Test
	public void testVerificarEstornoDevolucaoError() {
		final SceItemDas itemDa = new SceItemDas();
		final SceDevolucaoAlmoxarifado devolucaoAlmoxarifado = new SceDevolucaoAlmoxarifado();
		devolucaoAlmoxarifado.setSeq(8);
		itemDa.setDevolucaoAlmoxarifado(devolucaoAlmoxarifado);
		
		Mockito.when(mockedSceDevolucaoAlmoxarifadoDAO.obterDevolucaoAlmoxarifadoEstorno(Mockito.anyInt())).thenReturn(devolucaoAlmoxarifado);
		
		try {
			systemUnderTest.verificarEstornoDevolucao(itemDa);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ItemDevolucaoAlmoxarifadoRN.ItemDevolucaoAlmoxarifadoRNExceptionCode.SCE_00573);
		}
	}
	
	@Test
	public void testVerificarEstornoDevolucaoSuccess() {
		final SceItemDas itemDa = new SceItemDas();
		final SceDevolucaoAlmoxarifado devolucaoAlmoxarifado = new SceDevolucaoAlmoxarifado();
		devolucaoAlmoxarifado.setSeq(8);
		itemDa.setDevolucaoAlmoxarifado(devolucaoAlmoxarifado);		

		Mockito.when(mockedSceDevolucaoAlmoxarifadoDAO.obterDevolucaoAlmoxarifadoEstorno(Mockito.anyInt())).thenReturn(null);

		try {
			systemUnderTest.verificarEstornoDevolucao(itemDa);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}	

}
