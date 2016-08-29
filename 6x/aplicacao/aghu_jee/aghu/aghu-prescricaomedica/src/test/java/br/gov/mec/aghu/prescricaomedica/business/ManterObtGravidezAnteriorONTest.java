package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObtGravidezAnterior;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtGravidezAnteriorDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterObtGravidezAnteriorONTest extends AGHUBaseUnitTest<ManterObtGravidezAnteriorON>{

	// Mocks
	@Mock
	private MpmObtGravidezAnteriorDAO mockedMpmObtGravidezAnteriorDAO;
	
	// Instâncias reutilizadas nos métodos de teste
	private MpmObtGravidezAnterior obtGravidezAnterior = new MpmObtGravidezAnterior();
	private AipPacientes aipPaciente = new AipPacientes();
	private MpmAltaSumarioId id = new MpmAltaSumarioId();
	private MpmAltaSumario altaSumario = new MpmAltaSumario();
	private static Short SHORT_TESTE = 1;
	
	
	@Before
	public void doBeforeEachTestCase() {

		Mockito.when(mockedMpmObtGravidezAnteriorDAO.obterMpmObtGravidezAnterior(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(new MpmObtGravidezAnterior());
		
		// Inicializa instâncias reutilizadas nos métodos de teste..
		this.altaSumario.setId(this.id);
		this.altaSumario.setPaciente(this.aipPaciente);
		
	}
	@Test
	public void versionarObtGravidezAnterior(){
		try {
			systemUnderTest.versionarObtGravidezAnterior(this.altaSumario, SHORT_TESTE);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void removerObtGravidezAnterior(){
		try {
			systemUnderTest.removerObtGravidezAnterior(this.altaSumario);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void removerObtGravidezAnteriorAltaSumarioNulo(){
		try {
			systemUnderTest.removerObtGravidezAnterior(null);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void obterMpmObtGravidezAnterior(){
		MpmObtGravidezAnterior retorno = systemUnderTest.obterMpmObtGravidezAnterior(this.altaSumario);
		Assert.assertNotNull(retorno);
	}
	
	@Test
	public void obterMpmObtGravidezAnteriorAltaSumarioNulo(){
		MpmObtGravidezAnterior retorno = systemUnderTest.obterMpmObtGravidezAnterior(null);
		Assert.assertNotNull(retorno);
	}
	
	@Test
	public void obterIndicadorNecropsia(){
		DominioSimNao indicadorNecropsia = systemUnderTest.obterIndicadorNecropsia(DominioSexo.M, DominioSimNao.S);
		Assert.assertFalse(indicadorNecropsia.equals(DominioSimNao.S));
	}
	
	@Test
	public void obterIndicadorNecropsiaPacienteSexoMaculino(){
		DominioSimNao indicadorNecropsia = systemUnderTest.obterIndicadorNecropsia(DominioSexo.M, DominioSimNao.N);
		Assert.assertFalse(indicadorNecropsia.equals(DominioSimNao.S));
	}
	
	@Test
	public void obterIndicadorNecropsiaPacienteSexoFeminino(){
		DominioSimNao retorno = systemUnderTest.obterIndicadorNecropsia(DominioSexo.F, DominioSimNao.S);
		Assert.assertNotNull(retorno);
	}
	
	@Test
	public void gravarObtGravidezAnteriorSexoMaculino(){
		try {
			aipPaciente.setSexo(DominioSexo.M);
			String retorno = systemUnderTest.gravarObtGravidezAnterior(this.altaSumario, null);
			Assert.assertNotNull(retorno);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void gravarObtGravidezAnteriorSexoFeminino(){
		try {
			aipPaciente.setSexo(DominioSexo.M);
			String retorno = systemUnderTest.gravarObtGravidezAnterior(this.altaSumario, null);
			Assert.assertNotNull(retorno);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void inserirObtGravidezAnterior(){
		try {
			systemUnderTest.inserirObtGravidezAnterior(this.obtGravidezAnterior);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void inserirObtGravidezAnteriorNulo(){
		try {
			systemUnderTest.inserirObtGravidezAnterior(null);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void atualizarObtGravidezAnterior(){
		try {
			systemUnderTest.inserirObtGravidezAnterior(this.obtGravidezAnterior);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void atualizarObtGravidezAnteriorNulo(){
		try {
			systemUnderTest.inserirObtGravidezAnterior(null);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	
}

