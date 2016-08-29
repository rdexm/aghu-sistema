package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.cadastrosapoio.business.AnticoagulanteRN.ManterAnticoagulanteRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelAnticoagulanteDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AnticoagulanteRNTest extends AGHUBaseUnitTest<AnticoagulanteRN>{


	private AelAnticoagulante anticoagulante;
	private AelAnticoagulante anticoagulanteOld;
	@Mock
	private AelTipoAmostraExameDAO mockAelTipoAmostraExameDAO;
	@Mock
	private AelAnticoagulanteDAO mockAelAnticoagulanteDAO;
	@Mock
	private IParametroFacade mockParametroFacade;


	@Before
	public void doBeforeEachTestCase() {

		anticoagulante = new AelAnticoagulante();
		anticoagulanteOld = new AelAnticoagulante();

	}

	@Test
	public void testPreUpdateAnticoagulanteSuccess() {
		try {
			Mockito.when(mockAelTipoAmostraExameDAO.pesquisarPorAnticoagulante(Mockito.anyInt())).thenReturn(false);
			
			final List<AelAnticoagulante> lista = new ArrayList<AelAnticoagulante>();
			lista.add(anticoagulante);
			Mockito.when(mockAelAnticoagulanteDAO.pesquisarDescricao(Mockito.any(AelAnticoagulante.class))).thenReturn(lista);

			systemUnderTest.preUpdateAnticoagulante(anticoagulante, anticoagulante);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
	}
	@Test
	public void testPreUpdateAnticoagulanteErro1() {
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_WEEK, -1);

		final List<AelAnticoagulante> lista = new ArrayList<AelAnticoagulante>();
		lista.add(anticoagulante);
		Mockito.when(mockAelAnticoagulanteDAO.pesquisarDescricao(Mockito.any(AelAnticoagulante.class))).thenReturn(lista);
		
		anticoagulante.setCriadoEm(Calendar.getInstance().getTime());
		anticoagulanteOld.setCriadoEm(ontem.getTime());


		try {
			systemUnderTest.preUpdateAnticoagulante(anticoagulante, anticoagulanteOld);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterAnticoagulanteRNExceptionCode.AEL_00369);
		}
	}

	@Test
	public void testPreUpdateAnticoagulanteErro2() {
		RapServidores servidor = new RapServidores();
		RapServidoresId id1 = new RapServidoresId();
		id1.setMatricula(Integer.valueOf("123"));
		id1.setVinCodigo(Short.valueOf("1"));
		servidor.setId(id1);

		RapServidores servidor2 = new RapServidores();
		RapServidoresId id2 = new RapServidoresId();
		id2.setMatricula(Integer.valueOf("321"));
		id2.setVinCodigo(Short.valueOf("1"));
		servidor2.setId(id2);



		anticoagulante.setServidor(servidor);
		anticoagulanteOld.setServidor(servidor2);


		final List<AelAnticoagulante> lista = new ArrayList<AelAnticoagulante>();
		lista.add(anticoagulante);
		Mockito.when(mockAelAnticoagulanteDAO.pesquisarDescricao(Mockito.any(AelAnticoagulante.class))).thenReturn(lista);
		
		try {
			systemUnderTest.preUpdateAnticoagulante(anticoagulante, anticoagulanteOld);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterAnticoagulanteRNExceptionCode.AEL_00369);
		}
	}

	@Test
	public void testPreUpdateAnticoagulanteErro3() {
		RapServidores servidor = new RapServidores();
		RapServidoresId id1 = new RapServidoresId();
		id1.setMatricula(Integer.valueOf("123"));
		id1.setVinCodigo(Short.valueOf("2"));
		servidor.setId(id1);

		RapServidores servidor2 = new RapServidores();
		RapServidoresId id2 = new RapServidoresId();
		id2.setMatricula(Integer.valueOf("123"));
		id2.setVinCodigo(Short.valueOf("1"));
		servidor2.setId(id2);



		anticoagulante.setServidor(servidor);
		anticoagulanteOld.setServidor(servidor2);

		final List<AelAnticoagulante> lista = new ArrayList<AelAnticoagulante>();
		lista.add(anticoagulante);
		Mockito.when(mockAelAnticoagulanteDAO.pesquisarDescricao(Mockito.any(AelAnticoagulante.class))).thenReturn(lista);

		try {
			systemUnderTest.preUpdateAnticoagulante(anticoagulante, anticoagulanteOld);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterAnticoagulanteRNExceptionCode.AEL_00369);
		}
	}

	@Test
	public void testPreUpdateAnticoagulanteErro4() {
		anticoagulante.setDescricao("Desc1");
		anticoagulanteOld.setDescricao("Desc2");

		final List<AelAnticoagulante> lista = new ArrayList<AelAnticoagulante>();
		lista.add(anticoagulante);
		Mockito.when(mockAelAnticoagulanteDAO.pesquisarDescricao(Mockito.any(AelAnticoagulante.class))).thenReturn(lista);
		
		try {
			systemUnderTest.preUpdateAnticoagulante(anticoagulante, anticoagulanteOld);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterAnticoagulanteRNExceptionCode.AEL_00346);
		}
	}

	@Test
	public void testPreUpdateAnticoagulanteErro5() {
		Mockito.when(mockAelTipoAmostraExameDAO.pesquisarPorAnticoagulante(Mockito.anyInt())).thenReturn(true);

		final List<AelAnticoagulante> lista = new ArrayList<AelAnticoagulante>();
		lista.add(anticoagulante);
		Mockito.when(mockAelAnticoagulanteDAO.pesquisarDescricao(Mockito.any(AelAnticoagulante.class))).thenReturn(lista);
		
		try {
			systemUnderTest.preUpdateAnticoagulante(anticoagulante, anticoagulanteOld);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterAnticoagulanteRNExceptionCode.AEL_01196);
		}
	}

	/**
	 * Caso de teste: Configurar a data de criação de um anticoagulante para 6 dias atrás e o parâmetro 
	 * P_DIAS_PERM_DEL_AEL para 7 dias atrás
	 * 
	 * Resultado esperado: Deverá apresentar a exceção ApplicationBusinessException, código AEL_00343
	 */
	@Test
	public void testVerificarParametroDelecao() {
		try {
			//Preparando os dados que serão testados:
			Calendar expected = Calendar.getInstance();
			expected.add(Calendar.DAY_OF_MONTH, -6);

			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(new BigDecimal("7"));
			Mockito.when(mockParametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(parametro);

			assertTrue(systemUnderTest.verificarParametroDelecao(expected.getTime()));
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
	}
	/**
	 * Caso de teste: Configurar a data de criação de um anticoagulante para 6 dias atrás e o parâmetro 
	 * P_DIAS_PERM_DEL_AEL para 6 dias atrás
	 * Resultado esperado: true
	 */
	@Test
	public void testVerificarParametroDelecaoMesmoDia() {
		try {
			//Preparando os dados que serão testados:
			Calendar expected = Calendar.getInstance();
			expected.add(Calendar.DAY_OF_MONTH, -6);

			//Removendo as integrações
			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(new BigDecimal("6"));
			Mockito.when(mockParametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(parametro);

			assertTrue(systemUnderTest.verificarParametroDelecao(expected.getTime()));
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
	}
		
	/**
	 * Caso de teste: Configurar a data de criação para 8 dias atrás e o parâmetro 
	 * P_DIAS_PERM_DEL_AEL para 6 dias atrás
	 * 
	 * Resultado esperado: Deverá apresentar a exceção ApplicationBusinessException, código AEL_00343
	 */
	@Test
	public void testVerificarParametroDelecaoDataNaoPermitida() {
		try {
			//Preparando os dados que serão testados:
			Calendar expected = Calendar.getInstance();
			expected.add(Calendar.DAY_OF_MONTH, -8);

			//Removendo as integrações
			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(new BigDecimal("6"));
			Mockito.when(mockParametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(parametro);


			
			systemUnderTest.verificarParametroDelecao(expected.getTime());
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterAnticoagulanteRNExceptionCode.AEL_00343);
		} catch (BaseException e) {
			fail("Não devemos gerar esta exceção para este teste: " + e.getMessage());
		}
	}
	
	/**
	 * Caso de teste: Exceção na integração da busca de parâmetros 
	 * Resultado esperado: Deverá apresentar a exceção ApplicationBusinessException, código AEL_00344
	 */
	@Test
	public void testPreDeleteAnticoagulanteExcecaoParametro() {
		try {
			//Preparando os dados que serão testados:
			Calendar expected = Calendar.getInstance();
			expected.add(Calendar.DAY_OF_MONTH, -8);
			
			Mockito.when(mockParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).
			thenThrow(new ApplicationBusinessException(ManterAnticoagulanteRNExceptionCode.AEL_00344, Severity.ERROR, (Object[])null));

			systemUnderTest.verificarParametroDelecao(expected.getTime());
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterAnticoagulanteRNExceptionCode.AEL_00344);
		} catch (BaseException e) {
			fail("Não devemos gerar esta exceção para este teste: " + e.getMessage());
		}
	}

	@Test
	public void testPreInsertAnticoagulanteSuccess() {
		RapServidores rapServidor = new RapServidores();
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(Integer.valueOf(1));
		rapServidor.setId(id);

		anticoagulante.setServidor(rapServidor);

		final List<AelAnticoagulante> lista = new ArrayList<AelAnticoagulante>();
		lista.add(anticoagulante);
		Mockito.when(mockAelAnticoagulanteDAO.pesquisarDescricao(Mockito.any(AelAnticoagulante.class))).thenReturn(lista);
		
		try {
			systemUnderTest.preInsertAnticoagulante(anticoagulante);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}
	}
	
	
	@Test
	public void testPreInsertAnticoagulanteErro1() {
		anticoagulante.setServidor(null);

		try {
			systemUnderTest.preInsertAnticoagulante(anticoagulante);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterAnticoagulanteRNExceptionCode.AEL_00353);
		}
	}
	
	
}
