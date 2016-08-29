package br.gov.mec.aghu.estoque.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.SceValidadesRN.SceValidadesRNExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceValidadeDAO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.model.SceValidadeId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceValidadesRNTest extends AGHUBaseUnitTest<SceValidadesRN>{
	
	@Mock
	private SceValidadeDAO mockedSceValidadeDAO;
	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	@Mock
	private IEstoqueFacade mockedEstoqueFacade;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";
	
	@Before
	public void doBeforeEachTestCase() {
		SceEstoqueAlmoxarifado almoxarifado = new SceEstoqueAlmoxarifado();
		ScoMaterial material = new ScoMaterial();
		material.setCodigo(10);
		almoxarifado.setMaterial(material);
		Mockito.when(mockedEstoqueFacade.obterSceEstoqueAlmoxarifadoPorChavePrimaria(Mockito.anyInt())).thenReturn(almoxarifado);
	}
	
	
	/**
	 * Obtém instância padrão para os testes
	 * @return
	 */
	private SceValidade getDefaultInstance(){
		final SceValidade validade = new SceValidade();
		final SceValidadeId id = new SceValidadeId();
		id.setData(new Date(10000));
		id.setDataLong(10000L);
		id.setEalSeq(1);
		validade.setId(id);
		return validade;
	}

	@Test
	public void testValidarValidadeUnicalError01(){
		
		try {
			
			SceValidade validade = this.getDefaultInstance();
			
			Mockito.when(mockedSceValidadeDAO.obterPorChavePrimaria(Mockito.any(SceValidadeId.class))).thenReturn(new SceValidade());

			this.systemUnderTest.validarValidadeUnica(validade);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceValidadesRNExceptionCode.SCE_VAL_EXISTENTE);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceValidadesRNExceptionCode.SCE_VAL_EXISTENTE, e.getCode());
		}

	}
	
	@Test
	public void testValidarEstoqueAlmoxarifadoAtivoError01(){
		
		try {
			
			SceValidade validade = this.getDefaultInstance();
			
			final SceEstoqueAlmoxarifado estoqueAlmoxarifado = new SceEstoqueAlmoxarifado();
			estoqueAlmoxarifado.setIndSituacao(DominioSituacao.I); // Teste aqui!
			validade.setEstoqueAlmoxarifado(estoqueAlmoxarifado);
			
			Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.obterEstoqueAlmoxarifadoPorId(Mockito.anyInt())).thenReturn(estoqueAlmoxarifado);

			this.systemUnderTest.validarEstoqueAlmoxarifadoAtivo(validade);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceValidadesRNExceptionCode.SCE_00292);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceValidadesRNExceptionCode.SCE_00292, e.getCode());
		}
		
	}
	
	@Test
	public void testValidarDataValidadeError01(){
		
		try {
			
			SceValidade validade = this.getDefaultInstance();

			this.systemUnderTest.validarDataValidade(validade);
			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx 1");
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceValidadesRNExceptionCode.SCE_00327);
			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx 2");
		} catch (BaseException e) {
			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" + SceValidadesRNExceptionCode.SCE_00327);
			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" + e.getCode());
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceValidadesRNExceptionCode.SCE_00327, e.getCode());
			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx 2.1");
		}
		
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx 3");
	}
	

}
