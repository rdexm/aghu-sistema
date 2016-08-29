package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioRestricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.nutricao.dao.AnuTipoItemDietaDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class TiposDietaCRUDTest extends AGHUBaseUnitTest<TiposDietaCRUD> {

	@Mock
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@Mock
	private IAghuFacade aghuFacade;
	@Mock
	private IParametroFacade parametroFacade;
	@Mock
	private INutricaoFacade nutricaoFacade;
	@Mock
	private IFaturamentoFacade faturamentoFacade;
	@Mock
	private IServidorLogadoFacade iServidorLogadoFacade;
	@Mock
	private AnuTipoItemDietaDAO anuTipoItemDietaDAO;

	AnuTipoItemDieta tipoDietaOriginal;
	private AnuTipoItemDieta tipoDieta;


	private List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs = new LinkedList<AnuTipoItemDietaUnfs>();

	@Before
	public void doBeforeTest() throws Exception {
		inicializaTipoDieta();
		when(nutricaoFacade.obterAnuTipoItemDietaPorChavePrimaria(anyInt())).thenReturn(new AnuTipoItemDieta());
	}

	private void mockBuscarParametros(String vlrNumerico, Integer seq) throws ApplicationBusinessException {
		AghParametros parametro = new AghParametros();
		if (vlrNumerico != null) {
			parametro.setVlrNumerico(new BigDecimal(vlrNumerico));
		} else {
			parametro.setVlrNumerico(new BigDecimal("5"));
		}
		if (seq != null) {
			parametro.setSeq(seq);
		} else {
			parametro.setSeq(1);
		}
		when(parametroFacade.buscarAghParametro(any(AghuParametrosEnum.class))).thenReturn(parametro);
	}

	private void inicializaTipoDieta() {
		// Instancia um br.gov.mec.aghu.model.AnuTipoItemDieta
		tipoDieta = new AnuTipoItemDieta();
		tipoDieta.setSeq(Integer.MIN_VALUE);
		tipoDieta.setDescricao(null);
		tipoDieta.setIndDigitaQuantidade(DominioRestricao.C);
		tipoDieta.setIndDigitaAprazamento(DominioRestricao.O);
		tipoDieta.setIndAdulto(true);
		tipoDieta.setIndPediatria(false);
		tipoDieta.setIndNeonatologia(false);
		tipoDieta.setIndDietaPadronizada(false);
		tipoDieta.setIndSituacao(DominioSituacao.I);
		tipoDieta.setIndItemUnico(false);
		tipoDieta.setFrequencia(Short.MAX_VALUE);

		MpmTipoFrequenciaAprazamento frequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		frequenciaAprazamento.setSeq(Short.MIN_VALUE);
		tipoDieta.setTipoFrequenciaAprazamento(frequenciaAprazamento);

		RapServidores rapServidores = new RapServidores();
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(Integer.MIN_VALUE);
		id.setVinCodigo(Short.MIN_VALUE);
		rapServidores.setId(id);
		tipoDieta.setServidor(rapServidores);

		MpmUnidadeMedidaMedica unidadeMedidaMedica = new MpmUnidadeMedidaMedica();
		unidadeMedidaMedica.setSeq(Integer.MIN_VALUE);
		tipoDieta.setUnidadeMedidaMedica(unidadeMedidaMedica);

		// Expectation para a regra ORADB ANUK_TID_RN.RN_TIDP_VER_DELECAO
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 7);
		tipoDieta.setCriadoEm(cal.getTime());
	}

	/**
	 * Valida pré-deleção com data válida
	 */
	@Test
	public void preDeleteTiposDieta001() {
		try {
			mockBuscarParametros("5", 1);
			systemUnderTest.preDeleteTiposDieta(tipoDieta);
			verify(parametroFacade).buscarAghParametro((AghuParametrosEnum) argumentCaptor.capture());

			Assert.assertEquals(AghuParametrosEnum.P_DIAS_PERM_DEL_AFA, argumentCaptor.getValue());
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Valida pré-deleção com data inválida
	 */
	@Test
	public void preDeleteTiposDieta002() {

		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -6);

		try {
			mockBuscarParametros("5", 1);
			tipoDieta.setCriadoEm(cal.getTime());
			systemUnderTest.preDeleteTiposDieta(tipoDieta);
			Assert.fail("Deveria ter ocorrido uma AFA_00172");
		} catch (BaseException e) {
			Assert.assertEquals(TiposDietaCRUD.TiposDietaCRUDExceptionCode.AFA_00172.toString(), e.getMessage());
		} catch (IllegalArgumentException e) {
			Assert.fail("Deveria ter ocorrido uma AFA_00172");
		}
	}

	/**
	 * Valida pré-deleção nula
	 */
	@Test(expected = IllegalArgumentException.class)
	public void preDeleteTiposDieta003() {
		try {
			systemUnderTest.preDeleteTiposDieta(null);
			Assert.fail("Deveria ter ocorrido uma IllegalArgumentException");
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Valida data de criação válida
	 */
	/*
	 * @Test public void verificaDataCriacao001() {
	 * 
	 * try { mockingContext.checking(new Expectations() { { AghParametros
	 * parametro = new AghParametros(); parametro.setVlrNumerico(new
	 * BigDecimal("5"));
	 * allowing(mockedParametroFacade).buscarAghParametro(with(
	 * any(AghuParametrosEnum.class))); will(returnValue(parametro)); } });
	 * 
	 * 
	 * systemUnderTest.verificaDataCriacao(new Date()); } catch (Exception e) {
	 * Assert.fail("Ocorreu uma exceção: " + e.getMessage()); } }
	 */

	/**
	 * Valida data de criação inválida
	 */
	@Test
	public void verificaDataCriacao002() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -2);

		try {
			mockBuscarParametros("1", 1);
			systemUnderTest.verificaDataCriacao(cal.getTime());
			Assert.fail("Deveria ter ocorrido uma AFA_00172");
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00172", e.getCode(),
					TiposDietaCRUD.TiposDietaCRUDExceptionCode.AFA_00172);
		}
	}

	/**
	 * Valida deleção
	 */
	// corvalao-agora @Test
	public void excluirTiposDieta001() {

		try {
			mockBuscarParametros("5", 1);
			systemUnderTest.excluirTiposDieta(tipoDieta.getSeq());
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Valida deleção nula
	 */
	@Test(expected = IllegalArgumentException.class)
	public void excluirTiposDieta002() {
		try {
			systemUnderTest.excluirTiposDieta(null);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Valida deleção nula
	 */
	@Test
	public void validaDelecao001() {
		try {
			systemUnderTest.validaDelecao(tipoDieta);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Valida deleção
	 */
	@Test(expected = IllegalArgumentException.class)
	public void validaDelecao002() {
		try {
			systemUnderTest.validaDelecao(null);
			Assert.fail("Deveria ter ocorrido uma IllegalArgumentException");
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Valida deleção em FatProcedHospInternos
	 */
	@Test
	public void deleteFatProcedHospInternos() {
		try {
			systemUnderTest.deleteFatProcedHospInternos(null, null, null, null, null, null, null, null, tipoDieta.getSeq());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Valida persistência (inserção)
	 */
	@Test
	public void persistTiposDieta002() {
		try {

			tipoDieta.setSeq(null);

			when(prescricaoMedicaFacade.calculoNumeroVezesAprazamento24Horas(any(MpmTipoFrequenciaAprazamento.class), anyShort()))
					.thenReturn(Long.valueOf(1));

			systemUnderTest.persistirTiposDieta(tipoDieta, this.listaAnuTipoItemDietaUnfs);

		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Valida à frequência com tipo obrigatório válida
	 */
	@Test
	public void persistTiposDietaFrequenciaComTipoObrigatoriaInvalida() {
		try {

			tipoDieta.setIndDigitaAprazamento(DominioRestricao.C);

			systemUnderTest.persistirTiposDieta(tipoDieta, this.listaAnuTipoItemDietaUnfs);
			Assert.fail("Deveria ter ocorrido uma FREQUENCIA_COM_TIPO_OBRIGATORIA_INVALIDA");

		} catch (BaseException e) {
			Assert.assertEquals(TiposDietaCRUD.TiposDietaCRUDExceptionCode.FREQUENCIA_COM_TIPO_OBRIGATORIA_INVALIDA.toString(),
					e.getMessage());
		}
	}

	/**
	 * Valida à frequência com tipo para paciente obrigatório
	 */
	@Test
	public void persistTiposDietaFrequenciaComTipoPacienteObrigatorio() {
		try {

			tipoDieta.setIndAdulto(false);

			systemUnderTest.persistirTiposDieta(tipoDieta, this.listaAnuTipoItemDietaUnfs);
			Assert.fail("Deveria ter ocorrido uma TIPO_PACIENTE_OBRIGATORIO");

		} catch (BaseException e) {
			Assert.assertEquals(TiposDietaCRUD.TiposDietaCRUDExceptionCode.TIPO_PACIENTE_OBRIGATORIO.toString(), e.getMessage());
		}
	}

}
