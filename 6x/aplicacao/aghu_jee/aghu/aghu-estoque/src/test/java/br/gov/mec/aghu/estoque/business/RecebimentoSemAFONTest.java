package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste da ON {@link RecebimentoSemAFON}
 * 
 * @author luismoura
 * 
 */
public class RecebimentoSemAFONTest extends AGHUBaseUnitTest<RecebimentoSemAFON> {

	// ----- Bundle
	@Mock
	ResourceBundle mockedBundle;

	// ----- SERVICES
	@Mock
	ICascaFacade mockedCascaFacade = null;

	// ----- FACADES
	@Mock
	IParametroFacade mockedParametroFacade = null;

	// ----- ONS
	@Mock
	ConfirmacaoDevolucaoON mockedConfirmacaoDevolucaoON = null;

	public RecebimentoSemAFONTest() {
		mockedBundle = new ListResourceBundle() {
			private final Object[][] contents = new Object[][] { //
			/*	  */{ "LABEL_QUANTIDADE_ENTREGUE", "Quantidade Entregue" },//
					{ "LABEL_VALOR_UNITARIO", "Valor Unitário" }, //
					{ "LABEL_VALOR_TOTAL_NF", "Valor Total" },//
			};//

			@Override
			protected Object[][] getContents() {
				return contents;
			}
		};
	}
	
//	// ----- Setup dos testes
//	@Before
//	public void setUp() throws Exception {
//		this.mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//
//		mockedBundle = new ListResourceBundle() {
//			private final Object[][] contents = new Object[][] { //
//			/*	  */{ "LABEL_QUANTIDADE_ENTREGUE", "Quantidade Entregue" },//
//					{ "LABEL_VALOR_UNITARIO", "Valor Unitário" }, //
//					{ "LABEL_VALOR_TOTAL_NF", "Valor Total" },//
//			};//
//
//			@Override
//			protected Object[][] getContents() {
//				return contents;
//			}
//		};
//
//		this.mockedCascaFacade = this.mockingContext.mock(ICascaFacade.class);
//
//		this.mockedParametroFacade = this.mockingContext.mock(IParametroFacade.class);
//
//		this.mockedConfirmacaoDevolucaoON = this.mockingContext.mock(ConfirmacaoDevolucaoON.class);
//
//		systemUnderTest = new RecebimentoSemAFON() {
//			private static final long serialVersionUID = 2902466012512651645L;
//
//			@Override
//			protected ICascaFacade getCascaFacade() {
//				return mockedCascaFacade;
//			};
//
//			@Override
//			protected IParametroFacade getParametroFacade() {
//				return mockedParametroFacade;
//			};
//
//			@Override
//			protected ConfirmacaoDevolucaoON getConfirmacaoDevolucaoON() {
//				return mockedConfirmacaoDevolucaoON;
//			};
//		};
//	}

	
//	// ----- Fim dos testes
//	@After
//	public void tearDown() throws Exception {
//		this.mockedBundle = null;
//		this.mockedCascaFacade = null;
//		this.mockedParametroFacade = null;
//		this.mockedConfirmacaoDevolucaoON = null;
//		this.mockingContext = null;
//	}

	// ----- validarValorComprometidoSuperiorValorTotalNota

	@Test
	public void validarValorComprometidoSuperiorValorTotalNota01() throws Exception {
		this.validarValorComprometidoSuperiorValorTotalNota("R", 123.45, 543.21, true);
	}

	@Test
	public void validarValorComprometidoSuperiorValorTotalNota02() throws Exception {
		this.validarValorComprometidoSuperiorValorTotalNota("NF", 123.45, 543.21, true);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validarValorComprometidoSuperiorValorTotalNota03() throws Exception {
		this.validarValorComprometidoSuperiorValorTotalNota("R", 543.21, 123.45, true);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validarValorComprometidoSuperiorValorTotalNota04() throws Exception {
		this.validarValorComprometidoSuperiorValorTotalNota("NF", 543.21, 123.45, true);
	}

	@Test
	public void validarValorComprometidoSuperiorValorTotalNota05() throws Exception {
		this.validarValorComprometidoSuperiorValorTotalNota("X", 543.21, 123.45, true);
	}

	@Test
	public void validarValorComprometidoSuperiorValorTotalNota06() throws Exception {
		this.validarValorComprometidoSuperiorValorTotalNota("R", 123.45, 543.21, false);
	}

	@Test
	public void validarValorComprometidoSuperiorValorTotalNota07() throws Exception {
		this.validarValorComprometidoSuperiorValorTotalNota("NF", 123.45, 543.21, false);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validarValorComprometidoSuperiorValorTotalNota08() throws Exception {
		this.validarValorComprometidoSuperiorValorTotalNota("R", 543.21, 123.45, false);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validarValorComprometidoSuperiorValorTotalNota09() throws Exception {
		this.validarValorComprometidoSuperiorValorTotalNota("NF", 543.21, 123.45, false);
	}

	@Test
	public void validarValorComprometidoSuperiorValorTotalNota10() throws Exception {
		this.validarValorComprometidoSuperiorValorTotalNota("X", 543.21, 123.45, false);
	}

	// ----- validarQuantidadeSuperiorSaldoAF

	@Test
	public void validarQuantidadeSuperiorSaldoAF01() throws Exception {
		this.validarQuantidadeSuperiorSaldoAF(true, 1, 10, 20);
	}

	@Test
	public void validarQuantidadeSuperiorSaldoAF02() throws Exception {
		this.validarQuantidadeSuperiorSaldoAF(true, 1, 20, 20);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validarQuantidadeSuperiorSaldoAF03() throws Exception {
		this.validarQuantidadeSuperiorSaldoAF(true, 1, 20, 10);
	}

	@Test
	public void validarQuantidadeSuperiorSaldoAF04() throws Exception {
		this.validarQuantidadeSuperiorSaldoAF(false, 1, 10, 20);
	}

	@Test
	public void validarQuantidadeSuperiorSaldoAF05() throws Exception {
		this.validarQuantidadeSuperiorSaldoAF(false, 1, 20, 20);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validarQuantidadeSuperiorSaldoAF06() throws Exception {
		this.validarQuantidadeSuperiorSaldoAF(false, 1, 20, 10);
	}

	// ----- validaValorExcedePercentualVariacao

	@Test
	public void validaValorExcedePercentualVariacao01() throws Exception {
		this.validaValorExcedePercentualVariacao(false, 5f, 10, BigDecimal.valueOf(950), BigDecimal.valueOf(100));
	}

	@Test
	public void validaValorExcedePercentualVariacao02() throws Exception {
		this.validaValorExcedePercentualVariacao(false, 5f, 10, BigDecimal.valueOf(1050), BigDecimal.valueOf(100));
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validaValorExcedePercentualVariacao03() throws Exception {
		this.validaValorExcedePercentualVariacao(false, 5f, 10, BigDecimal.valueOf(949), BigDecimal.valueOf(100));
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validaValorExcedePercentualVariacao04() throws Exception {
		this.validaValorExcedePercentualVariacao(false, 5f, 10, BigDecimal.valueOf(1051), BigDecimal.valueOf(100));
	}

	@Test
	public void validaValorExcedePercentualVariacao05() throws Exception {
		this.validaValorExcedePercentualVariacao(true, 5f, 10, BigDecimal.valueOf(950), BigDecimal.valueOf(100));
	}

	@Test
	public void validaValorExcedePercentualVariacao06() throws Exception {
		this.validaValorExcedePercentualVariacao(true, 5f, 10, BigDecimal.valueOf(1050), BigDecimal.valueOf(100));
	}

	@Test
	public void validaValorExcedePercentualVariacao07() throws Exception {
		this.validaValorExcedePercentualVariacao(true, 5f, 10, BigDecimal.valueOf(949), BigDecimal.valueOf(100));
	}

	@Test
	public void validaValorExcedePercentualVariacao08() throws Exception {
		this.validaValorExcedePercentualVariacao(true, 5f, 10, BigDecimal.valueOf(1051), BigDecimal.valueOf(100));
	}

	// ----- validarObrigatoriedadeNFEParaRecebimento

	@Test(expected = ApplicationBusinessException.class)
	public void validarObrigatoriedadeNFEParaRecebimento01() throws Exception {
		this.validarObrigatoriedadeNFEParaRecebimento("S", "S");
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validarObrigatoriedadeNFEParaRecebimento02() throws Exception {
		this.validarObrigatoriedadeNFEParaRecebimento("S", "N");
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validarObrigatoriedadeNFEParaRecebimento03() throws Exception {
		this.validarObrigatoriedadeNFEParaRecebimento("N", "S");
	}

	@Test
	public void validarObrigatoriedadeNFEParaRecebimento04() throws Exception {
		this.validarObrigatoriedadeNFEParaRecebimento("N", "N");
	}

	// ----- validarDoacaoPatrimonioImobilizado

	@Test
	public void validarDoacaoPatrimonioImobilizado01() throws Exception {
		this.validarDoacaoPatrimonioImobilizado("DO", false);
	}

	@Test(expected = ApplicationBusinessException.class)
	public void validarDoacaoPatrimonioImobilizado02() throws Exception {
		this.validarDoacaoPatrimonioImobilizado("DO", true);
	}

	@Test
	public void validarDoacaoPatrimonioImobilizado03() throws Exception {
		this.validarDoacaoPatrimonioImobilizado("DA", false);
	}

	@Test
	public void validarDoacaoPatrimonioImobilizado04() throws Exception {
		this.validarDoacaoPatrimonioImobilizado("DA", true);
	}

	// ----- Métodos auxiliares

	/**
	 * Método que realizará os testes de validarValorComprometidoSuperiorValorTotalNota de acordo com os parâmetros
	 * 
	 * @param vlrParam
	 * @param vlrComp
	 * @param vlrTotNF
	 * @throws Exception
	 */
	private void validarValorComprometidoSuperiorValorTotalNota(final String vlrParam, final double vlrComp, final double vlrTotNF,
			final boolean entradaNaNotaFiscal) throws Exception {
		
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedParametroFacade).buscarAghParametro(with(any(AghuParametrosEnum.class)));
//				will(returnValue(new AghParametros(vlrParam)));
//			}
//		});
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(new AghParametros(vlrParam));
		systemUnderTest.validarValorComprometidoSuperiorValorTotalNota(BigDecimal.valueOf(vlrComp), BigDecimal.valueOf(vlrTotNF));
	}

	/**
	 * Método que realizará os testes de validarCamposValores de acordo com os parâmetros
	 * 
	 * @param adiantamentoAF
	 * @param codMaterial
	 * @param qtdInformada
	 * @param qtdSaldoAF
	 * @throws Exception
	 */
	private void validarQuantidadeSuperiorSaldoAF(final boolean adiantamentoAF, final Integer codMaterial, final Integer qtdInformada,
			final Integer qtdSaldoAF) throws Exception {
		systemUnderTest.validarQuantidadeSuperiorSaldoAF(codMaterial, qtdInformada, qtdSaldoAF);
	}

	/**
	 * Método que realizará os testes de validaValorExcedePercentualVariacao de acordo com os parâmetros
	 * 
	 * @param permissao
	 * @param valorPercVariacao
	 * @param qtdEntrege
	 * @param valorTotal
	 * @param vlrUnitario
	 * @throws Exception
	 */
	public void validaValorExcedePercentualVariacao(final boolean permissao, final Float valorPercVariacao, final Integer qtdEntrege,
			final BigDecimal valorTotal, final BigDecimal vlrUnitario) throws Exception {
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedCascaFacade).usuarioTemPermissao(with(any(String.class)), with(any(String.class)));
//				will(returnValue(permissao));
//			}
//		});
		Mockito.when(mockedCascaFacade.usuarioTemPermissao(Mockito.anyString(), Mockito.anyString())).thenReturn(permissao);
		systemUnderTest.validaValorExcedePercentualVariacao("luismoura", (short) 1, valorPercVariacao, qtdEntrege, valorTotal, vlrUnitario);
	}

	/**
	 * Método que realizará os testes de validarObrigatoriedadeNFEParaRecebimento de acordo com os parâmetros
	 * 
	 * @param vlrParam1
	 * @param vlrParam2
	 * @throws Exception
	 */
	public void validarObrigatoriedadeNFEParaRecebimento(final String vlrParam1, final String vlrParam2)
			throws Exception {
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedParametroFacade).buscarAghParametro(with(AghuParametrosEnum.P_CONF_RECEB_IMEDIATA));
//				will(returnValue(new AghParametros(vlrParam1)));
//
//				allowing(mockedParametroFacade).buscarAghParametro(with(AghuParametrosEnum.P_EXIGE_NF_NO_RECEBIMENTO));
//				will(returnValue(new AghParametros(vlrParam2)));
//			}
//		});
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONF_RECEB_IMEDIATA)).thenReturn(new AghParametros(vlrParam1));
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXIGE_NF_NO_RECEBIMENTO)).thenReturn(new AghParametros(vlrParam2));
		systemUnderTest.validarObrigatoriedadeNFEParaRecebimento();
	}

	/**
	 * Método que realizará os testes de validarDoacaoPatrimonioImobilizado de acordo com os parâmetros
	 * 
	 * @param siglaTipo
	 * @param materialImobilizado
	 * @throws Exception
	 */
	public void validarDoacaoPatrimonioImobilizado(final String siglaTipo, final boolean materialImobilizado) throws Exception {
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedConfirmacaoDevolucaoON).verificarMaterialImobilizado(with(any(Integer.class)), with(any(BigDecimal.class)));
//				will(returnValue(materialImobilizado));
//			}
//		});
		
		Mockito.when(mockedConfirmacaoDevolucaoON.verificarMaterialImobilizado(Mockito.anyInt(), Mockito.any(BigDecimal.class))).thenReturn(materialImobilizado);
		systemUnderTest.validarDoacaoPatrimonioImobilizado(siglaTipo, 1, BigDecimal.valueOf(10));
	}
}
