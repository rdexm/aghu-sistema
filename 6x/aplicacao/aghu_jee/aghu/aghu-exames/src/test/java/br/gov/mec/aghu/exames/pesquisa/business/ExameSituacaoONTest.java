package br.gov.mec.aghu.exames.pesquisa.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.pesquisa.business.ExameSituacaoON.ExameSituacaoONExceptionCode;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ExameSituacaoONTest extends AGHUBaseUnitTest<ExameSituacaoON>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	
	private Date obterDataReferencia(int dia, int ano, int mes, int hora, int min, int sec, int msec) {
		Calendar dtHrIni = Calendar.getInstance();
		
		dtHrIni.set(Calendar.DATE, dia);
		dtHrIni.set(Calendar.YEAR, ano);
		dtHrIni.set(Calendar.MONTH, mes);
		dtHrIni.set(Calendar.HOUR_OF_DAY, hora);
		dtHrIni.set(Calendar.MINUTE, min);
		dtHrIni.set(Calendar.SECOND, sec);
		dtHrIni.set(Calendar.MILLISECOND, msec);
	
		return dtHrIni.getTime();
	}
		
	/**
	 * Testa se as datas de referências foram preenchidas<br> 
	 * a partir de uma situacao cujo valor e igual a LI(liberado).
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testSituacaoLiberadoDataReferenciaNula() throws ApplicationBusinessException {
		final AghParametros parametro = new AghParametros();
		parametro.setNome("P_DIAS_PESQ_EXAME_SIT_TELA");
		parametro.setVlrNumerico(new BigDecimal(60));
		
		final VAelExamesSolicitacao exame = new VAelExamesSolicitacao();
		exame.setDescricaoExame("SANGUE");
		final AelSitItemSolicitacoes situacao = new AelSitItemSolicitacoes();
		situacao.setCodigo("LI");

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		
		try {
			systemUnderTest.validarFiltroPesquisaExameSolicitacaoPacAtend( 
					new Date(), null, null, situacao, null);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == ExameSituacaoONExceptionCode.AEL_01766);
		}
	}

	/**
	 * Testa se as datas de referências foram preenchidas<br> 
	 * a partir de uma situacao cujo valor e igual a LI(liberado).
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testSituacaoLiberadoDataReferenciaNula001() throws ApplicationBusinessException {
		final AghParametros parametro = new AghParametros();
		parametro.setNome("P_DIAS_PESQ_EXAME_SIT_TELA");
		parametro.setVlrNumerico(new BigDecimal(60));
		
		final VAelExamesSolicitacao exame = new VAelExamesSolicitacao();
		exame.setDescricaoExame("SANGUE");
		final AelSitItemSolicitacoes situacao = new AelSitItemSolicitacoes();
		situacao.setCodigo("LI");
					
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			systemUnderTest.validarFiltroPesquisaExameSolicitacaoPacAtend( 
					null, new Date(), null, situacao, null);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == ExameSituacaoONExceptionCode.AEL_01766);
		}
	}

	
	/**
	 * Testa se o intervalo de dias entre as <br> 
	 * datas de referência preenchidas <br>
	 * e maior que o periodo permitido quando <br>
	 * a situaca possuir o valor LI(liberado).
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testSituacaoLiberadoIntervaloDataReferencia() throws ApplicationBusinessException {
		final AghParametros parametro = new AghParametros();
		parametro.setNome("P_DIAS_PESQ_EXAME_SIT_TELA");
		parametro.setVlrNumerico(new BigDecimal(60));
				
		final AelSitItemSolicitacoes situacao = new AelSitItemSolicitacoes();
		situacao.setCodigo("LI");
		
		final Date dtHrIni = obterDataReferencia(28, 2010, 3, 0, 0, 0, 0);
		final Date dtHrFim = obterDataReferencia(28, 2011, 3, 0, 0, 0, 0);
					
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		
		try {
			systemUnderTest.validarFiltroPesquisaExameSolicitacaoPacAtend(	dtHrIni, dtHrFim, null, situacao, null);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == ExameSituacaoONExceptionCode.AEL_01767);
		}
	}
	
	
	@Test
	public void testDataProgramadaNaoNula002() throws ApplicationBusinessException {
		final AghParametros parametro = new AghParametros();
		parametro.setNome("P_SITUACAO_A_COLETAR");
		parametro.setVlrTexto("AC");
		
		final AghParametros parametro2 = new AghParametros();
		parametro2.setNome("P_SITUACAO_EM_COLETA");
		parametro2.setVlrTexto("EC");
				
		final AghParametros parametro3 = new AghParametros();
		parametro2.setNome("P_SITUACAO_NA_AREA_EXECUTORA");
		parametro2.setVlrTexto("AE");
		
		final AelSitItemSolicitacoes situacao = new AelSitItemSolicitacoes();
		situacao.setCodigo("CO");
		
		final Date dtHrIni = null;
		final Date dtHrProgramado = null;
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

					
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro2);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro3);
		
		try {
			systemUnderTest.validarFiltroPesquisaExameSolicitacaoPacAtend(dtHrIni, null, dtHrProgramado, situacao, null);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == ExameSituacaoONExceptionCode.AEL_01012);
		}
	}
	
	@Test
	public void testDatasReferenciaPreenchidas001() throws ApplicationBusinessException {
				
		final Date dtHrIni = null;
		final Date dtHrFim = obterDataReferencia(28, 2011, 3, 0, 0, 0, 0);

				
		try {
			systemUnderTest.validarFiltroPesquisaExameSolicitacaoPacAtend(dtHrIni, dtHrFim, null, null, null);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == ExameSituacaoONExceptionCode.AEL_01404);
		}
	}
	
	@Test
	public void testDatasReferenciaPreenchidas002() throws ApplicationBusinessException {
				
		final Date dtHrIni = obterDataReferencia(28, 2010, 3, 0, 0, 0, 0);
		final Date dtHrFim = null;
		
		
		try {
			systemUnderTest.validarFiltroPesquisaExameSolicitacaoPacAtend(	dtHrIni, dtHrFim, null, null, null);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == ExameSituacaoONExceptionCode.AEL_01404);
		}
	}
	
	@Test
	public void testDatasReferenciaEProgramadasPreenchidas() throws ApplicationBusinessException {
				
		final Date dtHrIni = obterDataReferencia(28, 2010, 3, 0, 0, 0, 0);
		final Date dtHrFim = obterDataReferencia(28, 2010, 3, 0, 0, 0, 0);
		final Date dtHrProg = obterDataReferencia(28, 2010, 3, 0, 0, 0, 0);
		
		
		try {
			systemUnderTest.validarFiltroPesquisaExameSolicitacaoPacAtend(	dtHrIni, dtHrFim, dtHrProg, null, null);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == ExameSituacaoONExceptionCode.AEL_01015);
		}
	}
	
}
