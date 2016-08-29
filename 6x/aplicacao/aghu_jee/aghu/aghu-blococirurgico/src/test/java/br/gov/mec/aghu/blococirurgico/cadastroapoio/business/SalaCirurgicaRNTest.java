package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.SalaCirurgicaRN.SalaCirurgicaRNExceptionCode;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class SalaCirurgicaRNTest extends AGHUBaseUnitTest<SalaCirurgicaRN> {

	@Mock
	private IAghuFacade mockAghuFacade;

//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//
//		mockAghuFacade = mockingContext.mock(IAghuFacade.class);
//
//		systemUnderTest = new SalaCirurgicaRN(){
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -1784786240446222804L;
//
//			@Override
//			protected IAghuFacade getAghuFacade() {
//				return mockAghuFacade;
//			}
//		};
//	}

	/**
	 * Teste enviando checkbox marcado e motivo inativação vazio
	 */
	@Test
	public void testRnScipVerSituacao() {
		MbcSalaCirurgica salaCirurgica = new MbcSalaCirurgica();
		salaCirurgica.setSituacao(DominioSituacao.A);
		salaCirurgica.setMotivoInat("");


		try {
			systemUnderTest.verificarMotivoInativacao(salaCirurgica);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada não esperada: " + e.getMessage());
		}
	}

	/**
	 * Teste enviando checkbox desmarcado e motivo inativação preenchido
	 */
	@Test
	public void testRnScipVerSituacao02() {
		MbcSalaCirurgica salaCirurgica = new MbcSalaCirurgica();
		salaCirurgica.setSituacao(DominioSituacao.I);
		salaCirurgica.setMotivoInat("Teste");

		try {
			systemUnderTest.verificarMotivoInativacao(salaCirurgica);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada não esperada: " + e.getMessage());
		}
	}

	/**
	 * Teste enviando checkbox desmarcado e motivo inativação vazio
	 */
	@Test
	public void testRnScipVerSituacaoExcecao() {
		MbcSalaCirurgica salaCirurgica = new MbcSalaCirurgica();
		salaCirurgica.setSituacao(DominioSituacao.I);
		salaCirurgica.setMotivoInat("");

		try {
			systemUnderTest.verificarMotivoInativacao(salaCirurgica);
			Assert.fail("Exceção esperada não gerada: MBC-00200");
		} catch (BaseException e) {
			Assert.assertEquals(SalaCirurgicaRNExceptionCode.MBC_00200, e.getCode());
		}
	}

	/**
	 * Se checkbox ativo marcado, remover texto do motivo inativação, caso esteja preenchido.
	 */

	@Test
	public void testRnScipVerSituacao03() {

		MbcSalaCirurgica salaCirurgica = new MbcSalaCirurgica();
		salaCirurgica.setSituacao(DominioSituacao.A);
		salaCirurgica.setMotivoInat("teste");
		MbcSalaCirurgica salaCirurgicaRetorno = systemUnderTest.verificarRemoverTextoMotivoInatSituacaoAtivo(salaCirurgica);
		Assert.assertEquals(salaCirurgicaRetorno.getMotivoInat(), "");
	}

	/**
	 * Se checkbox ativo desmarcado, não remover texto do motivo inativação, caso esteja preenchido.
	 */
	@Test
	public void testRnScipVerSituacao04() {
		MbcSalaCirurgica salaCirurgica = new MbcSalaCirurgica();
		salaCirurgica.setSituacao(DominioSituacao.I);
		salaCirurgica.setMotivoInat("teste");
		MbcSalaCirurgica salaCirurgicaRetorno = systemUnderTest.verificarRemoverTextoMotivoInatSituacaoAtivo(salaCirurgica);
		Assert.assertEquals(salaCirurgicaRetorno.getMotivoInat(), salaCirurgica.getMotivoInat());

	}

	/**
	 * Se a unidade cirúrgica estiver ativa, não apresentar exceção”
	 */
	@Test
	public void verificarUnidadeCirurgicaAtiva() {
		AghUnidadesFuncionais unidadeCirurgica = new AghUnidadesFuncionais();
		unidadeCirurgica.setAtivo(true);		
		try {
			systemUnderTest.verificaUnidadeCirurgicaAtiva(unidadeCirurgica);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada não esperada: " + e.getMessage());
		}
	}

	/**
	 * Se a unidade cirúrgica estiver inativa, apresentar exceção: MBC-00308 - “Unidade funcional deve estar ativa”
	 */
	@Test
	public void verificarUnidadeCirurgicaAtivaExcecao() {
		AghUnidadesFuncionais unidadeCirurgica = new AghUnidadesFuncionais();
		unidadeCirurgica.setAtivo(false);		
		try{
			systemUnderTest.verificaUnidadeCirurgicaAtiva(unidadeCirurgica);
			Assert.fail("Exceção esperada não gerada: MBC-00308");
		} catch (BaseException e) {
			Assert.assertEquals(SalaCirurgicaRNExceptionCode.MBC_00308, e.getCode());
		}
	}

	/**
	 * b.	Se a Unidade cirúrgica tiver característica ‘Unid Executora Cirurgias’, não apresentar exceção
	 */
	public void verificarCaracteristicaUnidadeCirurgica() {
		AghUnidadesFuncionais unidadeCirurgica = new AghUnidadesFuncionais();

//		mockingContext.checking(new Expectations() {{
//			oneOf(mockAghuFacade).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)), with(any(ConstanteAghCaractUnidFuncionais.class))); 
//			will(returnValue(true));
//		}});

		try {
			systemUnderTest.verificarCaracteristicaUnidadeCirurgica(unidadeCirurgica);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada não esperada: " + e.getMessage());
		}
	}

	/**
	 b.	Se a Unidade cirúrgica não tiver característica ‘Unid Executora Cirurgias’, apresentar exceção: MBC-00309 – “Unidade funcional deve ter característica de unidade cirúrgica”
	 */
	public void verificarCaracteristicaUnidadeCirurgicaExcecao() {
		AghUnidadesFuncionais unidadeCirurgica = new AghUnidadesFuncionais();
//		mockingContext.checking(new Expectations() {{
//			oneOf(mockAghuFacade).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)), with(any(ConstanteAghCaractUnidFuncionais.class))); 
//			will(returnValue(false));
//		}});

		try {
			systemUnderTest.verificarCaracteristicaUnidadeCirurgica(unidadeCirurgica);
			Assert.fail("Exceção esperada não gerada: MBC-00309");
		} catch (BaseException e) {
			Assert.assertEquals(SalaCirurgicaRNExceptionCode.MBC_00309, e.getCode());
		}
	}




}
