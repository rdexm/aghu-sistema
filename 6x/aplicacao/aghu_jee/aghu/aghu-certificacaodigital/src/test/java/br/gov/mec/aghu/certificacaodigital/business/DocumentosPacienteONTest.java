package br.gov.mec.aghu.certificacaodigital.business;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.certificacaodigital.business.DocumentosPacienteON.DocumentosPacienteONExceptionCode;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe DocumentoPacienteON.<br>
 * 
 * @author dcastro
 * 
 */
public class DocumentosPacienteONTest extends AGHUBaseUnitTest<DocumentosPacienteON> {

	@Mock
	private IParametroFacade mockedParametroFacade;

	@Test(expected = IllegalArgumentException.class)
	public void validarFiltrosPesquisaComException() throws BaseException {

		this.systemUnderTest.validarFiltrosPesquisa(null, null, null, null, null, null, null);
		Assert.fail("Deveria ter ocorrido uma IllegalArgumentException");

	}

	@Test
	public void verificarFiltrosInformadosComException() throws BaseException {

		try {
			this.systemUnderTest.verificarFiltrosInformados(null, null, null, null, null);
			Assert.fail("Deveria ter gerado uma exception");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(DocumentosPacienteONExceptionCode.MENSAGEM_NECESSARIO_INFORMAR_FILTRO, e.getCode());
		}

	}

	@Test
	public void verificarFiltrosInformadosSemException() throws BaseException {

		try {
			this.systemUnderTest.verificarFiltrosInformados(new AipPacientes(), new RapServidores(), null, null, null);
			assert true;
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void verificarDataFinalBeforeDataInicialComDataFinalMenorInicial() throws BaseException {

		Calendar dataInicial = Calendar.getInstance();
		dataInicial.set(2011, 06, 13);
		dataInicial.set(Calendar.HOUR_OF_DAY, 0);
		dataInicial.set(Calendar.MINUTE, 0);
		dataInicial.set(Calendar.SECOND, 0);

		Calendar dataFinal = Calendar.getInstance();
		dataFinal.set(2011, 06, 12);
		dataFinal.set(Calendar.HOUR_OF_DAY, 23);
		dataFinal.set(Calendar.MINUTE, 59);
		dataFinal.set(Calendar.SECOND, 59);

		try {
			this.systemUnderTest.verificarDataFinalBeforeDataInicial(dataInicial.getTime(), dataFinal.getTime());
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {
			Assert.assertEquals(DocumentosPacienteONExceptionCode.MENSAGEM_DATA_FINAL_MENOR_DATA_INICIAL, e.getCode());
		}

	}

	@Test
	public void verificarDataFinalBeforeDataInicialComDatasIguais() throws BaseException {

		Calendar dataInicial = Calendar.getInstance();
		dataInicial.set(2011, 06, 13);
		dataInicial.set(Calendar.HOUR_OF_DAY, 0);
		dataInicial.set(Calendar.MINUTE, 0);
		dataInicial.set(Calendar.SECOND, 0);

		Calendar dataFinal = Calendar.getInstance();
		dataFinal.set(2011, 06, 13);
		dataFinal.set(Calendar.HOUR_OF_DAY, 23);
		dataFinal.set(Calendar.MINUTE, 59);
		dataFinal.set(Calendar.SECOND, 59);

		try {
			this.systemUnderTest.verificarDataFinalBeforeDataInicial(dataInicial.getTime(), dataFinal.getTime());
			assert true;
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void verificarDataFinalBeforeDataInicialComDataFinalMaiorInicial() throws BaseException {

		Calendar dataInicial = Calendar.getInstance();
		dataInicial.set(2011, 06, 13);
		dataInicial.set(Calendar.HOUR_OF_DAY, 0);
		dataInicial.set(Calendar.MINUTE, 0);
		dataInicial.set(Calendar.SECOND, 0);

		Calendar dataFinal = Calendar.getInstance();
		dataFinal.set(2011, 06, 14);
		dataFinal.set(Calendar.HOUR_OF_DAY, 23);
		dataFinal.set(Calendar.MINUTE, 59);
		dataFinal.set(Calendar.SECOND, 59);

		try {
			this.systemUnderTest.verificarDataFinalBeforeDataInicial(dataInicial.getTime(), dataFinal.getTime());
			assert true;
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void verificarPeriodoPesquisaComDatasIguais() throws BaseException {

		Calendar dataInicial = Calendar.getInstance();
		dataInicial.set(2011, 06, 13);
		dataInicial.set(Calendar.HOUR_OF_DAY, 0);
		dataInicial.set(Calendar.MINUTE, 0);
		dataInicial.set(Calendar.SECOND, 0);

		Calendar dataFinal = Calendar.getInstance();
		dataFinal.set(2011, 06, 13);
		dataFinal.set(Calendar.HOUR_OF_DAY, 23);
		dataFinal.set(Calendar.MINUTE, 59);
		dataFinal.set(Calendar.SECOND, 59);

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_NRO_MAX_DIAS_PESQ_CERT_DIGITAL", DominioSimNao.N, Calendar
				.getInstance().getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, new BigDecimal(
				30), null, "TESTE PARAMETRO", null);

		try {
			this.systemUnderTest.verificarPeriodoPesquisa(dataInicial.getTime(), dataFinal.getTime());
			assert true;
		} catch (BaseException e) {
		}

	}

	@Test
	public void verificarPeriodoPesquisaComPeriodoSuperior30Dias() throws BaseException {

		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String stringDataInicial = "26/05/2012";
		String stringDataFinal = "26/07/2012";
		Date dataInicial = null;
		Date dataFinal = null;
		try {
			dataInicial = sdf.parse(stringDataInicial);
			dataFinal = sdf.parse(stringDataFinal);
		} catch (Exception e) {
			Assert.fail("Não foi possivel converter String em data.");
		}

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_NRO_MAX_DIAS_PESQ_CERT_DIGITAL", DominioSimNao.N, Calendar
				.getInstance().getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, new BigDecimal(
				30), null, "TESTE PARAMETRO", null);


		try {
			this.systemUnderTest.verificarPeriodoPesquisa(dataInicial, dataFinal);
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {
		}

	}

	@Test
	public void verificarPeriodoPesquisaComPeriodoSuperiorAoParametro() throws BaseException {

		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String stringDataInicial = "26/05/2012";
		String stringDataFinal = "26/07/2012";
		Date dataInicial = null;
		Date dataFinal = null;
		try {
			dataInicial = sdf.parse(stringDataInicial);
			dataFinal = sdf.parse(stringDataFinal);
		} catch (Exception e) {
			Assert.fail("Não foi possivel converter String em data.");
		}

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_NRO_MAX_DIAS_PESQ_CERT_DIGITAL", DominioSimNao.N, Calendar
				.getInstance().getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, null, null,
				"TESTE PARAMETRO", null);

		try {
			this.systemUnderTest.verificarPeriodoPesquisa(dataInicial, dataFinal);
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {

			Assert.assertEquals(DocumentosPacienteONExceptionCode.MENSAGEM_PARAMETRO_NRO_MAXIMO_PESQUISA_NAO_DEFINIDO, e.getCode());

		}

	}

	@Test
	public void verificarPeriodoPesquisaComParametroNull() throws BaseException {

		Calendar dataInicial = Calendar.getInstance();
		dataInicial.set(2011, 06, 13);
		dataInicial.set(Calendar.HOUR_OF_DAY, 0);
		dataInicial.set(Calendar.MINUTE, 0);
		dataInicial.set(Calendar.SECOND, 0);

		Calendar dataFinal = Calendar.getInstance();
		dataFinal.set(2011, 07, 12);
		dataFinal.set(Calendar.HOUR_OF_DAY, 23);
		dataFinal.set(Calendar.MINUTE, 59);
		dataFinal.set(Calendar.SECOND, 59);


		try {
			this.systemUnderTest.verificarPeriodoPesquisa(dataInicial.getTime(), dataFinal.getTime());
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {
			Assert.assertEquals(DocumentosPacienteONExceptionCode.MENSAGEM_PARAMETRO_NRO_MAXIMO_PESQUISA_NAO_DEFINIDO, e.getCode());
		}

	}

}
