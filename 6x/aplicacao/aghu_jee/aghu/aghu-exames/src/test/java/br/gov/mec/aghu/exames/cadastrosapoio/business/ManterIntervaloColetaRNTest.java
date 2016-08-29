package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ManterIntervaloColetaRN.ManterIntervaloColetaRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelIntervaloColetaDAO;
import br.gov.mec.aghu.exames.dao.AelIntervaloColetaJnDAO;
import br.gov.mec.aghu.exames.dao.AelRefCodeDAO;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterIntervaloColetaRNTest extends AGHUBaseUnitTest<ManterIntervaloColetaRN>{

	@Mock
	private AelIntervaloColetaDAO mockedAelIntervaloColetaDAO;
	@Mock
	private AelIntervaloColetaJnDAO mockedAelIntervaloColetaJnDAO;
	@Mock
	private AelRefCodeDAO mockedAelRefCodeDAO;
	@Mock
	private AelExamesMaterialAnaliseDAO mockedAelExamesMaterialAnaliseDAO;
	
	private AelIntervaloColeta aelIntervaloColeta;
	private AelIntervaloColeta aelIntervaloColetaOriginal;
	
	@Before
	public void doBeforeEachTestCase() {
		aelIntervaloColeta = new AelIntervaloColeta();
		aelIntervaloColetaOriginal = new AelIntervaloColeta();
	}
	
	@Test
	public void testPreInsertIndicadorUsoInativo() {
		try {
			final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
			examesMaterialAnalise.setIndUsaIntervaloCadastrado(false);

			Mockito.when(mockedAelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt())).thenReturn(examesMaterialAnalise);
			systemUnderTest.preInsert(aelIntervaloColeta);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para o indicador inativo
			assertEquals(e.getCode(), ManterIntervaloColetaRNExceptionCode.AEL_00431);
		}
	}
	
	@Test
	public void testPreInsertIndicadorUsoAtivo() {
		try {
			final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
			examesMaterialAnalise.setIndUsaIntervaloCadastrado(true);
			Mockito.when(mockedAelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt())).thenReturn(examesMaterialAnalise);
			
			systemUnderTest.preInsert(aelIntervaloColeta);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreInsertUnidadeMedidaVolumeInvalida() {
		try {
			final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
			examesMaterialAnalise.setIndUsaIntervaloCadastrado(true);
			Mockito.when(mockedAelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt())).thenReturn(examesMaterialAnalise);
			
			aelIntervaloColeta.setUnidMedidaVolume("teste");
			Mockito.when(mockedAelRefCodeDAO.verificarValorValido(Mockito.anyString(), Mockito.anyString())).thenReturn(0);			

			systemUnderTest.preInsert(aelIntervaloColeta);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para o volume inválido
			assertEquals(e.getCode(), ManterIntervaloColetaRNExceptionCode.AEL_00226);
		}
	}
	
	@Test
	public void testPreInsertUnidadeMedidaVolumeValida() {
		try {
			final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
			examesMaterialAnalise.setIndUsaIntervaloCadastrado(true);
			
			Mockito.when(mockedAelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt())).thenReturn(examesMaterialAnalise);
			
			aelIntervaloColeta.setUnidMedidaVolume("teste");
			Mockito.when(mockedAelRefCodeDAO.verificarValorValido(Mockito.anyString(), Mockito.anyString())).thenReturn(1);			

			systemUnderTest.preInsert(aelIntervaloColeta);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreInsertTipoSubstanciaInvalida() {
		try {
			final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
			examesMaterialAnalise.setIndUsaIntervaloCadastrado(true);
			Mockito.when(mockedAelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt())).thenReturn(examesMaterialAnalise);
			
			aelIntervaloColeta.setTipoSubstancia("teste");
			Mockito.when(mockedAelRefCodeDAO.verificarValorValido(Mockito.anyString(), Mockito.anyString())).thenReturn(0);			
			
			systemUnderTest.preInsert(aelIntervaloColeta);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para o tipo de substância inválido
			assertEquals(e.getCode(), ManterIntervaloColetaRNExceptionCode.AEL_00228);
		}
	}
	
	@Test
	public void testPreInsertTipoSubstanciaValida() {
		try {
			final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
			examesMaterialAnalise.setIndUsaIntervaloCadastrado(true);
			Mockito.when(mockedAelExamesMaterialAnaliseDAO.buscarAelExamesMaterialAnalisePorId(Mockito.anyString(), Mockito.anyInt())).thenReturn(examesMaterialAnalise);
			
			aelIntervaloColeta.setTipoSubstancia("teste");
			Mockito.when(mockedAelRefCodeDAO.verificarValorValido(Mockito.anyString(), Mockito.anyString())).thenReturn(1);			
			

			systemUnderTest.preInsert(aelIntervaloColeta);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreUpdateUnidadeMedidaVolumeInvalida() {
		try {
			aelIntervaloColetaOriginal.setUnidMedidaVolume("testeOld");
			aelIntervaloColeta.setUnidMedidaVolume("teste");
			
			Mockito.when(mockedAelRefCodeDAO.verificarValorValido(Mockito.anyString(), Mockito.anyString())).thenReturn(0);			

			systemUnderTest.preUpdate(aelIntervaloColetaOriginal, aelIntervaloColeta);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para o volume inválido
			assertEquals(e.getCode(), ManterIntervaloColetaRNExceptionCode.AEL_00226);
		}
	}
	
	@Test
	public void testPreUpdateUnidadeMedidaVolumeValida() {
		try {
			aelIntervaloColetaOriginal.setUnidMedidaVolume("testeOld");
			aelIntervaloColeta.setUnidMedidaVolume("teste");
			
			Mockito.when(mockedAelRefCodeDAO.verificarValorValido(Mockito.anyString(), Mockito.anyString())).thenReturn(1);			

			systemUnderTest.preUpdate(aelIntervaloColetaOriginal, aelIntervaloColeta);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreUpdateTipoSubstanciaInvalida() {
		try {
			aelIntervaloColetaOriginal.setTipoSubstancia("testeOld");
			aelIntervaloColeta.setTipoSubstancia("teste");
			
			Mockito.when(mockedAelRefCodeDAO.verificarValorValido(Mockito.anyString(), Mockito.anyString())).thenReturn(0);			

			systemUnderTest.preUpdate(aelIntervaloColetaOriginal, aelIntervaloColeta);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para o tipo de substância inválido
			assertEquals(e.getCode(), ManterIntervaloColetaRNExceptionCode.AEL_00228);
		}
	}
	
	@Test
	public void testPreUpdateTipoSubstanciaValida() {
		try {
			aelIntervaloColetaOriginal.setTipoSubstancia("testeOld");
			aelIntervaloColeta.setTipoSubstancia("teste");
			
			Mockito.when(mockedAelRefCodeDAO.verificarValorValido(Mockito.anyString(), Mockito.anyString())).thenReturn(1);			

			systemUnderTest.preUpdate(aelIntervaloColetaOriginal, aelIntervaloColeta);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
			
}
