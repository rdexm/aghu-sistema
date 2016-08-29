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
import br.gov.mec.aghu.exames.cadastrosapoio.business.ManterRegiaoAnatomicaRN.ManterRegiaoAnatomicaRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelRegiaoAnatomicaDAO;
import br.gov.mec.aghu.exames.dao.AelRegiaoAnatomicaJnDAO;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterRegiaoAnatomicaRNTest extends AGHUBaseUnitTest<ManterRegiaoAnatomicaRN>{

	private AelRegiaoAnatomica regiao;
	private AelRegiaoAnatomica regiaoOld;
	@Mock
	private AelRegiaoAnatomicaDAO mockedAelRegiaoAnatomicaDAO;
	@Mock
	private AelRegiaoAnatomicaJnDAO mockedAelRegiaoAnatomicaJnDAO;
	@Mock
	private IParametroFacade mockedParametroFacade;


	@Before
	public void doBeforeEachTestCase() {

		regiao = new AelRegiaoAnatomica();
		regiaoOld = new AelRegiaoAnatomica();
	}
	
	
	/**
	 * Método que pré-popula os objetos de teste com valores iguais.
	 */
	private void popularRegiaoTeste() {
		regiao.setSeq(5);
		regiaoOld.setSeq(5);
		regiao.setDescricao("Desc1");
		regiaoOld.setDescricao("Desc1");
		Date agora = new Date();
		regiao.setCriadoEm(agora);
		regiaoOld.setCriadoEm(agora);
		regiao.setIndSituacao(DominioSituacao.A);
		regiaoOld.setIndSituacao(DominioSituacao.A);
	}
	
	@Test
	public void testPreUpdateDescricaoDiferente() {
		try {
			popularRegiaoTeste();
			
			regiaoOld.setDescricao("Desc2");
			
			final List<AelRegiaoAnatomica> lista = new ArrayList<AelRegiaoAnatomica>();
			lista.add(regiao);
			Mockito.when(mockedAelRegiaoAnatomicaDAO.pesquisarDescricao(Mockito.any(AelRegiaoAnatomica.class))).thenReturn(lista);
			
			systemUnderTest.preUpdate(regiaoOld, regiao);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para as descrições diferentes
			assertEquals(e.getCode(), ManterRegiaoAnatomicaRNExceptionCode.AEL_00346);
		}
	}
	
	@Test
	public void testPreUpdateDescricaoIgual() {
		try {
			popularRegiaoTeste();
			
			final List<AelRegiaoAnatomica> lista = new ArrayList<AelRegiaoAnatomica>();
			lista.add(regiao);
			Mockito.when(mockedAelRegiaoAnatomicaDAO.pesquisarDescricao(Mockito.any(AelRegiaoAnatomica.class))).thenReturn(lista);

			systemUnderTest.preUpdate(regiaoOld, regiao);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreUpdateCriacaoDiferente() {
		try {
			popularRegiaoTeste();
			
			Date agora = new Date();
			agora.setTime(0);
			regiaoOld.setCriadoEm(agora);

			final List<AelRegiaoAnatomica> lista = new ArrayList<AelRegiaoAnatomica>();
			lista.add(regiao);
			Mockito.when(mockedAelRegiaoAnatomicaDAO.pesquisarDescricao(Mockito.any(AelRegiaoAnatomica.class))).thenReturn(lista);

			systemUnderTest.preUpdate(regiaoOld, regiao);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção para as datas de criação diferentes
			assertEquals(e.getCode(), ManterRegiaoAnatomicaRNExceptionCode.AEL_00369);
		}
	}
	
	@Test
	public void testPreUpdateCriacaoIgual() {
		try {
			popularRegiaoTeste();

			final List<AelRegiaoAnatomica> lista = new ArrayList<AelRegiaoAnatomica>();
			lista.add(regiao);
			Mockito.when(mockedAelRegiaoAnatomicaDAO.pesquisarDescricao(Mockito.any(AelRegiaoAnatomica.class))).thenReturn(lista);

			systemUnderTest.preUpdate(regiaoOld, regiao);
		} catch(ApplicationBusinessException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
	@Test
	public void testPreDeleteParametroNaoExiste() {
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).
			thenThrow(new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE));
			
			systemUnderTest.preDelete(regiao);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção devido ao material estar associado a um exame
			assertEquals(e.getCode(), ManterRegiaoAnatomicaRNExceptionCode.AEL_00344);
		} catch(BaseException e) {
			fail("Exceção não esperada gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testPreDeletePeriodoInvalido() {
		try {
			popularRegiaoTeste();
			
			//Seta data de criação fora do período
			Calendar dataForaPeriodoCalendar = Calendar.getInstance();
			dataForaPeriodoCalendar.add(Calendar.DAY_OF_MONTH, -12);
			dataForaPeriodoCalendar.set(Calendar.HOUR, 0);
			dataForaPeriodoCalendar.set(Calendar.MINUTE, 0);
			dataForaPeriodoCalendar.set(Calendar.SECOND, 0);
			dataForaPeriodoCalendar.set(Calendar.MILLISECOND, 0);
			regiao.setCriadoEm(dataForaPeriodoCalendar.getTime());
			
			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(BigDecimal.TEN);
			
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).
			thenReturn(parametro);
			
			systemUnderTest.preDelete(regiao);
			
			fail("Exceção não gerada");
		} catch(ApplicationBusinessException e) {
			//Deve gerar uma exceção devido ao material estar associado a um exame
			assertEquals(e.getCode(), ManterRegiaoAnatomicaRNExceptionCode.AEL_00343);
		} catch(BaseException e) {
			fail("Exceção não esperada gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testPreDeletePeriodoValido() {
		try {
			popularRegiaoTeste();
			
			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(BigDecimal.TEN);
			
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).
			thenReturn(parametro);

			//Deve passar no teste pois a data de criação é o dia atual
			systemUnderTest.preDelete(regiao);
		} catch(BaseException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
		//Passa no teste caso não tenha sido lançada uma exceção
		assertEquals(1, 1);
	}
	
}
