package br.gov.mec.aghu.farmacia.business;

import java.math.BigDecimal;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.FarmaciaRN.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FarmaciaRNTest extends AGHUBaseUnitTest<FarmaciaRN>{

	private static final Log log = LogFactory.getLog(FarmaciaRNTest.class);
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private AfaTipoVelocAdministracoesDAO mockedAfaTipoVelocAdministracoesDAO;

	@Test
	public void verificaDelecaoTest1() {
		try {

			whenObterParametroNull();
			
			systemUnderTest.verificarDelecao(null);
			Assert.fail("Deveria ter lançado exceção AFA_00173");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00173", e
					.getCode(), FarmaciaExceptionCode.AFA_00173);
		}
	}

	@Test
	public void verificaDelecaoTest2() {
		try {
			whenObterParametroNull();
			systemUnderTest.verificarDelecao(null);
			Assert.fail("Deveria ter lançado exceção AFA_00173");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00173", e
					.getCode(), FarmaciaExceptionCode.AFA_00173);
		}
	}

	@Test
	public void verificaDelecaoTest3() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);

		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(new BigDecimal("5"));

		try {
			whenObterParametro();
			systemUnderTest.verificarDelecao(cal.getTime());
			Assert.fail("Deveria ter lançado exceção AFA_00172");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00172", e
					.getCode(), FarmaciaExceptionCode.AFA_00172);
		}
	}

	@Test
	public void verificaDelecaoTest4() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -2);

		try {
			whenObterParametro5();
			systemUnderTest.verificarDelecao(cal.getTime());
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail("Ocorreu uma exceção: " + e.getMessage());
		}
	}

	@Test
	public void isTipoVelocidadeAtivaTest1() {
		try {
			
			Mockito.when(mockedAfaTipoVelocAdministracoesDAO.obtemSituacaoTipoVelocidade(Mockito.anyShort())).thenReturn(null);
			systemUnderTest.isTipoVelocidadeAtiva((short) 1);
			Assert.fail("Deveria ter lançado exceção AFA_00220");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00220", e
					.getCode(), FarmaciaExceptionCode.AFA_00220);
		}
	}

	@Test
	public void isTipoVelocidadeAtivaTest2() {
		try {
			final DominioSituacao situacao = DominioSituacao.I;

			Mockito.when(mockedAfaTipoVelocAdministracoesDAO.obtemSituacaoTipoVelocidade(Mockito.anyShort())).thenReturn(situacao);

			boolean retorno = systemUnderTest.isTipoVelocidadeAtiva((short) 1);
			Assert.assertEquals(Boolean.FALSE, retorno);
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.fail("Ocorreu uma exceção: " + e.getMessage());
		}
	}

	@Test
	public void isTipoVelocidadeAtivaTest3() {
		try {
			final DominioSituacao situacao = DominioSituacao.A;

			Mockito.when(mockedAfaTipoVelocAdministracoesDAO.obtemSituacaoTipoVelocidade(Mockito.anyShort())).thenReturn(situacao);

			boolean retorno = systemUnderTest.isTipoVelocidadeAtiva((short) 1);
			Assert.assertEquals(Boolean.TRUE, retorno);
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.fail("Ocorreu uma exceção: " + e.getMessage());
		}
	}

	private void whenObterParametro() throws BaseException{
		AghParametros aghParametro = new AghParametros();
		aghParametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(aghParametro);
	}

	private void whenObterParametro5() throws BaseException{
		AghParametros aghParametro = new AghParametros();
		aghParametro.setVlrNumerico(BigDecimal.valueOf(5));
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(aghParametro);
	}

	private void whenObterParametroNull() throws BaseException{
		AghParametros aghParametro = new AghParametros();
		aghParametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(null);
	}

}
