package br.gov.mec.aghu.controlepaciente.business;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpHorarioControleDAO;
import br.gov.mec.aghu.controlepaciente.vo.PacienteInternadoVO.StatusSinalizadorUP;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe
 * ListaPacientesInternadosON.<br>
 * 
 * @author cvagheti
 * 
 */
public class ListarPacientesRegistroControleONTest extends AGHUBaseUnitTest<ListarPacientesRegistroControleON> {

	@Mock
	private EcpHorarioControleDAO mockedEcpHorarioControleDAO;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private IPrescricaoEnfermagemFacade mockedPrescricaoEnfermagemFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;

	@Test(expected = IllegalArgumentException.class)
	public void pesquisarPacientesInternadosTest001() throws BaseException {
		this.systemUnderTest.pesquisarPacientesInternados(null);
		Assert.fail("Deveria ter ocorrido uma IllegalArgumentException");

	}

	@Test(expected = IllegalArgumentException.class)
	public void disableIconeChecagem001() throws BaseException {
		this.systemUnderTest.disableIconeChecagem(null);
		Assert.fail("Deveria ter ocorrido uma IllegalArgumentException");
	}

	@Test
	public void disableIconeChecagem002() {

		Mockito.when(
				mockedAghuFacade.unidadeFuncionalPossuiCaracteristica(Mockito.anyShort(),
						Mockito.any(ConstanteAghCaractUnidFuncionais.class), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
				.thenReturn(Boolean.TRUE);

		try {
			boolean result = this.systemUnderTest.disableIconeChecagem((short) 1);
			Assert.assertEquals(result, false);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void obterParametroIntegraChecagem001() throws ApplicationBusinessException {

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_INTEGRA_CHECAGEM", DominioSimNao.N, Calendar.getInstance()
				.getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, null, "S", "TESTE PARAMETRO",
				null);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			String result = this.systemUnderTest.obterParametroIntegraChecagem();
			Assert.assertEquals(result, "S");
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void obterParametroIntegraChecagem002() throws ApplicationBusinessException {

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_INTEGRA_CHECAGEM", DominioSimNao.N, Calendar.getInstance()
				.getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, null, null,
				"TESTE PARAMETRO", null);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			String result = this.systemUnderTest.obterParametroIntegraChecagem();
			Assert.assertEquals(result, DominioSimNao.N.toString());
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void obterParametroControleUP001() throws ApplicationBusinessException {

		final Short valorParametro = 1;
		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_CONTROLES_UP", DominioSimNao.N, Calendar.getInstance()
				.getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null,
				new BigDecimal(valorParametro), null, "TESTE PARAMETRO", null);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			Short result = this.systemUnderTest.obterParametroControleUP();
			Assert.assertEquals(result, valorParametro);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void obterParametroLinhaCorteUP001() throws ApplicationBusinessException {

		final BigDecimal valorParametro = new BigDecimal(13);
		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_LINHA_CORTE_UP", DominioSimNao.N, Calendar.getInstance()
				.getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, valorParametro, null,
				"TESTE PARAMETRO", null);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			BigDecimal result = this.systemUnderTest.obterParametroLinhaCorteUP();
			Assert.assertEquals(result, valorParametro);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void buscarSinalizadorUP001() {

		Mockito.when(mockedPrescricaoEnfermagemFacade.existeComunicadoUlceraPressaoPorAtendimento(Mockito.anyInt())).thenReturn(
				Boolean.TRUE);

		StatusSinalizadorUP result = this.systemUnderTest.buscarSinalizadorUP(1, (short) 1, new BigDecimal(13));
		Assert.assertEquals(result, StatusSinalizadorUP.FLAG_VERMELHO);
	}

	@Test
	public void buscarSinalizadorUP002() {

		final BigDecimal paramLinhaCorteUP = new BigDecimal(13);
		final BigDecimal medicaoPaciente = new BigDecimal(5);

		Mockito.when(mockedPrescricaoEnfermagemFacade.existeComunicadoUlceraPressaoPorAtendimento(Mockito.anyInt())).thenReturn(
				Boolean.FALSE);

		Mockito.when(mockedEcpHorarioControleDAO.buscarMedicaoUlceraPressaoPorAtendimento(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(medicaoPaciente);

		StatusSinalizadorUP result = this.systemUnderTest.buscarSinalizadorUP(1, (short) 1, paramLinhaCorteUP);
		Assert.assertEquals(result, StatusSinalizadorUP.FLAG_AMARELO);
	}

	@Test
	public void buscarSinalizadorUP003() {

		final BigDecimal paramLinhaCorteUP = new BigDecimal(13);
		final BigDecimal medicaoPaciente = new BigDecimal(13);

		Mockito.when(mockedPrescricaoEnfermagemFacade.existeComunicadoUlceraPressaoPorAtendimento(Mockito.anyInt())).thenReturn(
				Boolean.FALSE);

		Mockito.when(mockedEcpHorarioControleDAO.buscarMedicaoUlceraPressaoPorAtendimento(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(medicaoPaciente);

		StatusSinalizadorUP result = this.systemUnderTest.buscarSinalizadorUP(1, (short) 1, paramLinhaCorteUP);
		Assert.assertEquals(result, StatusSinalizadorUP.FLAG_AMARELO);
	}

	@Test
	public void buscarSinalizadorUP004() {

		final BigDecimal paramLinhaCorteUP = new BigDecimal(13);
		final BigDecimal medicaoPaciente = new BigDecimal(21);

		Mockito.when(mockedPrescricaoEnfermagemFacade.existeComunicadoUlceraPressaoPorAtendimento(Mockito.anyInt())).thenReturn(
				Boolean.FALSE);

		Mockito.when(mockedEcpHorarioControleDAO.buscarMedicaoUlceraPressaoPorAtendimento(Mockito.anyInt(), Mockito.anyShort()))
				.thenReturn(medicaoPaciente);

		StatusSinalizadorUP result = this.systemUnderTest.buscarSinalizadorUP(1, (short) 1, paramLinhaCorteUP);
		Assert.assertEquals(result, StatusSinalizadorUP.FLAG_VERDE);
	}

}
