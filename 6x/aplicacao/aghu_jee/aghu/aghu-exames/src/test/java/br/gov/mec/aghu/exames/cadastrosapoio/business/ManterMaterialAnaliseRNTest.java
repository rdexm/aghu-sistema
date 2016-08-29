package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ManterMaterialAnaliseRN.ManterMaterialAnaliseRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseJnDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterMaterialAnaliseRNTest extends AGHUBaseUnitTest<ManterMaterialAnaliseRN>{

	@Mock
	private AelMaterialAnaliseDAO mockedAelMaterialAnaliseDAO;
	@Mock
	private AelMaterialAnaliseJnDAO mockedAelMaterialAnaliseJnDAO;
	@Mock
	private AelTipoAmostraExameDAO mockedAelTipoAmostraExameDAO;
	@Mock
	private IParametroFacade  mockedParametroFacade;
	
	private AelMateriaisAnalises aelMaterialAnalise;
	private AelMateriaisAnalises aelMaterialAnaliseOriginal;
	
	@Before
	public void doBeforeEachTestCase() {
		aelMaterialAnalise = new AelMateriaisAnalises();
		aelMaterialAnaliseOriginal = new AelMateriaisAnalises();
	}
	
	/**
	 * Método que pré-popula os objetos de teste com valores iguais.
	 */
	private void popularMateriasTeste() {
		aelMaterialAnalise.setSeq(5);
		aelMaterialAnaliseOriginal.setSeq(5);
		aelMaterialAnalise.setDescricao("Desc1");
		aelMaterialAnaliseOriginal.setDescricao("Desc1");
		Date agora = new Date();
		aelMaterialAnalise.setCriadoEm(agora);
		aelMaterialAnaliseOriginal.setCriadoEm(agora);
		aelMaterialAnalise.setIndSituacao(DominioSituacao.A);
		aelMaterialAnaliseOriginal.setIndSituacao(DominioSituacao.A);
	}
	
	@Test
	public void testPreUpdateDescricaoDiferente() {
		try {
			
			
			
			popularMateriasTeste();
			
			final List<AelMateriaisAnalises> lista = new ArrayList<AelMateriaisAnalises>();
			lista.add(aelMaterialAnalise);
			Mockito.when(mockedAelMaterialAnaliseDAO.pesquisarDescricao(Mockito.any(AelMateriaisAnalises.class))).thenReturn(lista);
			aelMaterialAnaliseOriginal.setDescricao("Desc2");

			systemUnderTest.preUpdate(aelMaterialAnaliseOriginal, aelMaterialAnalise);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para as descrições diferentes
			assertEquals(e.getCode(), ManterMaterialAnaliseRNExceptionCode.AEL_00346);
		}
	}
	
	@Test
	public void testPreUpdateDescricaoIgual() {
		try {
			
			popularMateriasTeste();
			
			final List<AelMateriaisAnalises> lista = new ArrayList<AelMateriaisAnalises>();
			lista.add(aelMaterialAnalise);
			Mockito.when(mockedAelMaterialAnaliseDAO.pesquisarDescricao(Mockito.any(AelMateriaisAnalises.class))).thenReturn(lista);
			
			systemUnderTest.preUpdate(aelMaterialAnaliseOriginal, aelMaterialAnalise);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreUpdateCriacaoDiferente() {
		try {
			
			popularMateriasTeste();
			
			final List<AelMateriaisAnalises> lista = new ArrayList<AelMateriaisAnalises>();
			lista.add(aelMaterialAnalise);
			Mockito.when(mockedAelMaterialAnaliseDAO.pesquisarDescricao(Mockito.any(AelMateriaisAnalises.class))).thenReturn(lista);

			Date agora = new Date();
			agora.setTime(0);
			aelMaterialAnaliseOriginal.setCriadoEm(agora);

			systemUnderTest.preUpdate(aelMaterialAnaliseOriginal, aelMaterialAnalise);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para as datas de criação diferentes
			assertEquals(e.getCode(), ManterMaterialAnaliseRNExceptionCode.AEL_00369);
		}
	}
	
	@Test
	public void testPreUpdateCriacaoIgual() {
		try {
			popularMateriasTeste();

			final List<AelMateriaisAnalises> lista = new ArrayList<AelMateriaisAnalises>();
			lista.add(aelMaterialAnalise);
			Mockito.when(mockedAelMaterialAnaliseDAO.pesquisarDescricao(Mockito.any(AelMateriaisAnalises.class))).thenReturn(lista);
			
			systemUnderTest.preUpdate(aelMaterialAnaliseOriginal, aelMaterialAnalise);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreUpdateMaterialEmUsoExame() {
		try {
			popularMateriasTeste();
			
			aelMaterialAnalise.setIndSituacao(DominioSituacao.I);

			Mockito.when(mockedAelTipoAmostraExameDAO.pesquisarPorMaterialAnalise(Mockito.anyInt())).thenReturn(true);
			
			final List<AelMateriaisAnalises> lista = new ArrayList<AelMateriaisAnalises>();
			lista.add(aelMaterialAnaliseOriginal);
			Mockito.when(mockedAelMaterialAnaliseDAO.pesquisarDescricao(Mockito.any(AelMateriaisAnalises.class))).thenReturn(lista);
			
			systemUnderTest.preUpdate(aelMaterialAnaliseOriginal, aelMaterialAnalise);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção devido ao material estar associado a um exame
			assertEquals(e.getCode(), ManterMaterialAnaliseRNExceptionCode.AEL_01197);
		}
	}
	
	@Test
	public void testPreUpdateMaterialNaoEmUsoExame() {
		try {
			popularMateriasTeste();
			
			aelMaterialAnalise.setIndSituacao(DominioSituacao.I);
			Mockito.when(mockedAelTipoAmostraExameDAO.pesquisarPorMaterialAnalise(Mockito.anyInt())).thenReturn(false);
			
			final List<AelMateriaisAnalises> lista = new ArrayList<AelMateriaisAnalises>();
			lista.add(aelMaterialAnalise);
			Mockito.when(mockedAelMaterialAnaliseDAO.pesquisarDescricao(Mockito.any(AelMateriaisAnalises.class))).thenReturn(lista);
			
			systemUnderTest.preUpdate(aelMaterialAnaliseOriginal, aelMaterialAnalise);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreInsertSuccess() {
		try {
			popularMateriasTeste();
			Mockito.when(mockedAelTipoAmostraExameDAO.pesquisarPorMaterialAnalise(Mockito.anyInt())).thenReturn(false);
			
			final List<AelMateriaisAnalises> lista = new ArrayList<AelMateriaisAnalises>();
			lista.add(aelMaterialAnaliseOriginal);
			Mockito.when(mockedAelMaterialAnaliseDAO.pesquisarDescricao(Mockito.any(AelMateriaisAnalises.class))).thenReturn(lista);

			
			systemUnderTest.preInsert(aelMaterialAnalise);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreInsertError() {
		try {
			popularMateriasTeste();
			Mockito.when(mockedAelTipoAmostraExameDAO.pesquisarPorMaterialAnalise(Mockito.anyInt())).thenReturn(false);			
			
			final List<AelMateriaisAnalises> lista = new ArrayList<AelMateriaisAnalises>();
			aelMaterialAnaliseOriginal.setDescricao("Desc1");
			aelMaterialAnaliseOriginal.setSeq(Integer.valueOf(6));
			lista.add(aelMaterialAnaliseOriginal);
			Mockito.when(mockedAelMaterialAnaliseDAO.pesquisarDescricao(Mockito.any(AelMateriaisAnalises.class))).thenReturn(lista);
			
			systemUnderTest.preInsert(aelMaterialAnalise);
			
			fail("Exceção não gerada");
		}  catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção devido ao material estar associado a um exame
			assertEquals(e.getCode(), ManterMaterialAnaliseRNExceptionCode.ERRO_UK_DESCRICAO_MATERIAL_ANALISE);
		} 
	}
	
	@Test
	public void testPreDeleteParametroNaoExiste() {
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
			.thenThrow(new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE));

			systemUnderTest.preDelete(aelMaterialAnalise);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção devido ao material estar associado a um exame
			assertEquals(e.getCode(), ManterMaterialAnaliseRNExceptionCode.AEL_00344);
		} catch(BaseException e) {
			fail("Exceção não esperada gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testPreDeletePeriodoInvalido() {
		try {
			popularMateriasTeste();
			
			//Seta data de criação fora do período
			Calendar dataForaPeriodoCalendar = Calendar.getInstance();
			dataForaPeriodoCalendar.add(Calendar.DAY_OF_MONTH, -12);
			dataForaPeriodoCalendar.set(Calendar.HOUR, 0);
			dataForaPeriodoCalendar.set(Calendar.MINUTE, 0);
			dataForaPeriodoCalendar.set(Calendar.SECOND, 0);
			dataForaPeriodoCalendar.set(Calendar.MILLISECOND, 0);
			aelMaterialAnalise.setCriadoEm(dataForaPeriodoCalendar.getTime());
			
			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(BigDecimal.TEN);
			
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
			.thenReturn(parametro);
			
			systemUnderTest.preDelete(aelMaterialAnalise);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção devido ao material estar associado a um exame
			assertEquals(e.getCode(), ManterMaterialAnaliseRNExceptionCode.AEL_00343);
		} catch(BaseException e) {
			fail("Exceção não esperada gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testPreDeletePeriodoValido() {
		try {
			popularMateriasTeste();
			
			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(BigDecimal.TEN);
			
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
			.thenReturn(parametro);

			//Deve passar no teste pois a data de criação é o dia atual
			systemUnderTest.preDelete(aelMaterialAnalise);
		} catch(BaseException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	
}
