package br.gov.mec.aghu.prescricaomedica.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObitoNecropsia;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObitoNecropsiaDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterObitoNecropsiaONTest extends AGHUBaseUnitTest<ManterObitoNecropsiaON>{
	
	private static final Log log = LogFactory.getLog(ManterObitoNecropsiaONTest.class);

	// Mocks
	@Mock
	private ManterObitoNecropsiaRN mockedManterObitoNecropsiaRN;
	@Mock
	private MpmObitoNecropsiaDAO mockedMpmObitoNecropsiaDAO;
	// Instâncias reutilizadas nos métodos de teste
	private MpmObitoNecropsia obitoNecropsia = new MpmObitoNecropsia();
	private MpmAltaSumarioId id = new MpmAltaSumarioId();
	private MpmAltaSumario altaSumario = new MpmAltaSumario();
	private static Short SHORT_TESTE = 1;
	
	
	@Before
	public void doBeforeEachTestCase() {

		Mockito.when(mockedMpmObitoNecropsiaDAO.obterMpmObitoNecropsia(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(new MpmObitoNecropsia());
		
		// Inicializa instâncias reutilizadas nos métodos de teste..
		this.altaSumario.setId(id);
		
	}

	@Test
	public void versionarObitoNecropsia(){
		try {
			systemUnderTest.versionarObitoNecropsia(this.altaSumario, SHORT_TESTE);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void versionarObitoNecropsiaAltaSumarioNulo(){
		try {
			systemUnderTest.versionarObitoNecropsia(null, SHORT_TESTE);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void obterMpmObitoNecropsia(){
		MpmObitoNecropsia retorno = systemUnderTest.obterMpmObitoNecropsia(this.altaSumario);
		Assert.assertNotNull(retorno);
	}
	
	@Test
	public void obterMpmObitoNecropsiaNulo(){
			systemUnderTest.obterMpmObitoNecropsia(null);
	}
	
	@Test
	public void gravarObitoNecropsiaAltaSumarioNulo(){
		try {
			String retorno = systemUnderTest.gravarObitoNecropsia(this.altaSumario, null);
			Assert.assertNotNull(retorno);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void gravarObitoNecropsiaDominioNulo(){
		try {
			String retorno = systemUnderTest.gravarObitoNecropsia(this.altaSumario, null);
			Assert.assertNotNull(retorno);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void gravarObitoNecropsiaIndicadorNecropsiaSim(){
		try {
			String retorno = systemUnderTest.gravarObitoNecropsia(this.altaSumario, DominioSimNao.S);
			Assert.assertNotNull(retorno);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void gravarObitoNecropsiaIndicadorNecropsiaNao(){
		try {
			String retorno = systemUnderTest.gravarObitoNecropsia(this.altaSumario, DominioSimNao.N);
			Assert.assertNotNull(retorno);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void inserirObitoNecropsia(){
		try {
			systemUnderTest.inserirObitoNecropsia(this.obitoNecropsia);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void inserirObitoNecropsiaNulo(){
		try {
			systemUnderTest.inserirObitoNecropsia(null);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void atualizarObitoNecropsia(){
		try {
			systemUnderTest.atualizarObitoNecropsia(this.obitoNecropsia);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void atualizarObitoNecropsiaNulo(){
		try {
			systemUnderTest.atualizarObitoNecropsia(null);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void removerObitoNecropsia(){
		try {
			systemUnderTest.removerObitoNecropsia(this.altaSumario);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void removerObitoNecropsiaNulo(){
		try {
			systemUnderTest.removerObitoNecropsia(null);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
		
	}
}

