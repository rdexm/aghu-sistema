package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ProcedEspecialDiversoRN.ManterProcedEspecialDiversoRNExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialDiversoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ProcedEspecialDiversoRNTest extends AGHUBaseUnitTest<ProcedEspecialDiversoRN>{

	@Mock
	private MpmProcedEspecialDiversoDAO mpmProcedEspecialDiversoDAO;
	@Mock
	private TipoModoUsoProcedimentoRN tipoModoUsoProcedimentoRN;
	@Mock
	private ProcedEspecialRmRN procedEspecialRmRN;
	@Mock
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@Mock
	private IParametroFacade parametroFacade;
	

	@Test
	public void testPreUpdateProcedEspecialDiversoException() {
		MpmProcedEspecialDiversos mpmProcedEspecialDiverso = new MpmProcedEspecialDiversos();
		MpmProcedEspecialDiversos mpmProcedEspecialDiversoOriginal = new MpmProcedEspecialDiversos();

		try {
			mpmProcedEspecialDiverso.setDescricao("Desc1");
			mpmProcedEspecialDiversoOriginal.setDescricao("Desc2");

			Mockito.when(mpmProcedEspecialDiversoDAO.obterPeloId(Mockito.anyShort())).thenReturn(mpmProcedEspecialDiversoOriginal);
			
			systemUnderTest.preUpdateProcedEspecialDiverso(mpmProcedEspecialDiverso);
			fail("Exeção não gerada.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ManterProcedEspecialDiversoRNExceptionCode.MPM_00774);
		}
	}

	@Test
	public void testPreUpdateProcedEspecialDiverso() {
		MpmProcedEspecialDiversos mpmProcedEspecialDiverso = new MpmProcedEspecialDiversos();
		MpmProcedEspecialDiversos mpmProcedEspecialDiversoOriginal = new MpmProcedEspecialDiversos();

		try {
			mpmProcedEspecialDiverso.setDescricao("Desc1");
			mpmProcedEspecialDiversoOriginal.setDescricao("Desc1");

			Mockito.when(mpmProcedEspecialDiversoDAO.obterPeloId(Mockito.anyShort())).thenReturn(mpmProcedEspecialDiversoOriginal);

			systemUnderTest.preUpdateProcedEspecialDiverso(mpmProcedEspecialDiverso);
		} catch (BaseException e) {
			fail("Exeção gerada. " + e.getCode());
		}

		//Passa no teste caso nao tenha ido lançada uma exceção.
		assertEquals(1, 1);
	}
	
	@Test
	public void testValidarDiasPermitidosDelecaoException() {
		try {
			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(BigDecimal.TEN);
			
			Mockito.when(parametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
						
			Calendar onzeDiasAnteriores = Calendar.getInstance();
			onzeDiasAnteriores.set(Calendar.HOUR_OF_DAY, 0);
			onzeDiasAnteriores.set(Calendar.MINUTE, 0);
			onzeDiasAnteriores.set(Calendar.SECOND, 0);
			onzeDiasAnteriores.set(Calendar.MILLISECOND, 0);
			onzeDiasAnteriores.add(Calendar.DAY_OF_MONTH, -12);

			systemUnderTest.validarDiasPermitidosDelecao(onzeDiasAnteriores.getTime());
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterProcedEspecialDiversoRNExceptionCode.MPM_00681);
		} catch (Exception e) {
			fail("Exeção incorreta." + e.getMessage());
		}
	}

	@Test
	public void testValidarDiasPermitidosDelecao() {
		try {
			Mockito.when(parametroFacade.buscarValorInteiro(Mockito.any(AghuParametrosEnum.class))).thenReturn(Integer.valueOf(10));
						
			Calendar onzeDiasAnteriores = Calendar.getInstance();
			onzeDiasAnteriores.add(Calendar.DAY_OF_MONTH, -10);

			systemUnderTest.validarDiasPermitidosDelecao(onzeDiasAnteriores.getTime());
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		} catch (Exception e) {
			fail("Exeção incorreta." + e.getMessage());
		}

		//Passa no teste caso nao tenha ido lançada uma exceção.
		assertEquals(1, 1);
	}
	
	@Test
	public void testValidarDiasPermitidosDelecao02() {
		try {
			Mockito.when(parametroFacade.buscarValorInteiro(Mockito.any(AghuParametrosEnum.class))).thenReturn(Integer.valueOf(10));

			Calendar onzeDiasAnteriores = Calendar.getInstance();
			onzeDiasAnteriores.add(Calendar.DAY_OF_MONTH, -9);

			systemUnderTest.validarDiasPermitidosDelecao(onzeDiasAnteriores.getTime());
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		} catch (Exception e) {
			fail("Exeção incorreta." + e.getMessage());
		}

		//Passa no teste caso nao tenha sido lançada uma exceção. (método void)
		assertEquals(1, 1);
	}

}
