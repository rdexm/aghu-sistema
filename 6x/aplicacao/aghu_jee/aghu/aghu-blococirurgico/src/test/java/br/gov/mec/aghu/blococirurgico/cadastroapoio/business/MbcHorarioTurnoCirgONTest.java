package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.MbcHorarioTurnoCirgON.MbcHorarioTurnoCirgONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgId;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcHorarioTurnoCirgONTest extends AGHUBaseUnitTest<MbcHorarioTurnoCirgON> {

	private IAdministracaoFacade mockIAdministracaoFacade;
	private IAghuFacade mockIAghuFacade;
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//
//		mockIAdministracaoFacade = mockingContext.mock(IAdministracaoFacade.class);
//		mockIAghuFacade = mockingContext.mock(IAghuFacade.class);
//		mbcHorarioTurnoCirgDAO = mockingContext.mock(MbcHorarioTurnoCirgDAO.class);
//
//		systemUnderTest = new MbcHorarioTurnoCirgON(){
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 7479360924004891552L;
//
//			@Override
//			protected IAdministracaoFacade getAdministracaoFacade() {
//				return mockIAdministracaoFacade;
//			};
//
//			@Override
//			protected IAghuFacade getAghuFacade() {
//				return mockIAghuFacade;
//			};
//
//			@Override
//			protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO() {
//				return mbcHorarioTurnoCirgDAO;
//			};
//			
//			@Override
//			protected String getMessage(String message) {
//				return "TESTE";
//			};
//		};
//	}


	/**
	 * Se o turno não está cadastrado, não mostra erro	
	 */
	@Test
	public void testVerificarExistenciaTurno() {
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		mbcHorarioTurnoCirg.setId(new MbcHorarioTurnoCirgId());
		
		final MbcHorarioTurnoCirg jaExiste = null;
//		mockingContext.checking(new Expectations() {{
//			oneOf(mbcHorarioTurnoCirgDAO).obterPorChavePrimaria(with(any(Object.class))); 
//			will(returnValue(jaExiste));
//		}});
		
		try {
			systemUnderTest.verificarExistenciaTurno(mbcHorarioTurnoCirg);
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
	
	/**
	 * Se o turno não está cadastrado, não mostra erro	
	 */
	@Test
	public void testVerificarExistenciaTurno02() {
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		mbcHorarioTurnoCirg.setId(new MbcHorarioTurnoCirgId());
		
		final MbcHorarioTurnoCirg jaExiste = new MbcHorarioTurnoCirg();
		jaExiste.setId(null);
		
//		mockingContext.checking(new Expectations() {{
//			oneOf(mbcHorarioTurnoCirgDAO).obterPorChavePrimaria(with(any(Object.class))); 
//			will(returnValue(jaExiste));
//		}});
		try {
			systemUnderTest.verificarExistenciaTurno(mbcHorarioTurnoCirg);
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
	
	
	/**
	 * Se o turno escolhido já estiver cadastrado para a unidade cirúrgica selecionada, mostrar a mensagem de erro:MBC-00075
	 */
	@Test
	public void testVerificarExistenciaTurno03() {
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		mbcHorarioTurnoCirg.setId(new MbcHorarioTurnoCirgId());
		
		final MbcHorarioTurnoCirg jaExiste = new MbcHorarioTurnoCirg();
		MbcHorarioTurnoCirgId id = new MbcHorarioTurnoCirgId();
		jaExiste.setId(id);
		
//		mockingContext.checking(new Expectations() {{
//			oneOf(mbcHorarioTurnoCirgDAO).obterPorChavePrimaria(with(any(Object.class))); 
//			will(returnValue(jaExiste));
//		}});
		try {
			systemUnderTest.verificarExistenciaTurno(mbcHorarioTurnoCirg);
			Assert.fail("Exeção esperada não gerada: MBC_00075");
		} catch (BaseException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgONExceptionCode.MBC_00075, e.getCode());
		}
	}

	/**
	 * Se o horário inicial for diferente ao horário final, não mostra erro
	 */
	@Test
	public void testVerificarMesmoHorario() {
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		Calendar horaInicial = Calendar.getInstance();
		Calendar horaFinal = Calendar.getInstance();
		horaFinal.add(Calendar.HOUR, 1);
		mbcHorarioTurnoCirg.setHorarioInicial(horaInicial.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(horaFinal.getTime());
		
		try {
			systemUnderTest.verificarMesmoHorario(mbcHorarioTurnoCirg);
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}

	/**
	 * Se o horário inicial for igual ao horário fina, mostrar a mensagem de erro:	MBC-00201
	 */
	@Test
	public void testVerificarMesmoHorario02() {
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		Calendar horaIgual = Calendar.getInstance();
		mbcHorarioTurnoCirg.setHorarioInicial(horaIgual.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(horaIgual.getTime());
		
		try {
			systemUnderTest.verificarMesmoHorario(mbcHorarioTurnoCirg);
			Assert.fail("Exeção esperada não gerada: MBC_00201");
		} catch (BaseException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgONExceptionCode.MBC_00201, e.getCode());
		}
	}

	/**
	 * Se o usuário tem a UNF_SEQ na tabela AGH_MICROCOMPUTADORES e a UNF_SEQ tem característica 'Unid Executora Cirurgias', 
	 * e a UNF_SEQ ao qual se está inserindo o turno é diferente desta UNF_SEQ, mostrar a exceção: ERRO_UNIDADE_FUNCIONAL_CIRURGIA
	 */
	@Test
	public void testVerificarUnidadeFuncional() {
		try {
			final AghMicrocomputador micro = new AghMicrocomputador();
			AghUnidadesFuncionais aghUnidadesFuncionais = new AghUnidadesFuncionais();
			aghUnidadesFuncionais.setSeq(Short.parseShort("1"));
			micro.setAghUnidadesFuncionais(aghUnidadesFuncionais);

			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
			AghUnidadesFuncionais aghUnidadesFuncionais2 = new AghUnidadesFuncionais();
			aghUnidadesFuncionais2.setSeq(Short.parseShort("2"));
			mbcHorarioTurnoCirg.setAghUnidadesFuncionais(aghUnidadesFuncionais2);

//			mockingContext.checking(new Expectations() {{
//				oneOf(mockIAdministracaoFacade).obterAghMicroComputadorPorNomeOuIP(with(any(String.class)), with(any(DominioCaracteristicaMicrocomputador.class))); 
//				will(returnValue(micro));
//			}});
//
//			mockingContext.checking(new Expectations() {{
//				oneOf(mockIAghuFacade).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)), with(any(ConstanteAghCaractUnidFuncionais.class))); 
//				will(returnValue(true));
//			}});
			systemUnderTest.verificarUnidadeFuncional(mbcHorarioTurnoCirg, "");
			
			Assert.fail("Exeção esperada não gerada: ERRO_UNIDADE_FUNCIONAL_CIRURGIA");
		} catch (BaseException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgONExceptionCode.ERRO_UNIDADE_FUNCIONAL_CIRURGIA, e.getCode());
		}
	}

	/**
	 * Se o usuário não tem a UNF_SEQ na tabela AGH_MICROCOMPUTADORES , não mostrar exceção
	 */
	@Test
	public void testVerificarUnidadeFuncional02() {
		try {
			final AghMicrocomputador micro = new AghMicrocomputador();
			micro.setAghUnidadesFuncionais(null);

			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();

//			mockingContext.checking(new Expectations() {{
//				oneOf(mockIAdministracaoFacade).obterAghMicroComputadorPorNomeOuIP(with(any(String.class)), with(any(DominioCaracteristicaMicrocomputador.class))); 
//				will(returnValue(micro));
//			}});
//
//			mockingContext.checking(new Expectations() {{
//				oneOf(mockIAghuFacade).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)), with(any(ConstanteAghCaractUnidFuncionais.class))); 
//				will(returnValue(true));
//			}});
			systemUnderTest.verificarUnidadeFuncional(mbcHorarioTurnoCirg, "");
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}

	/**
	 * Se o usuário tem a UNF_SEQ na tabela AGH_MICROCOMPUTADORES e a UNF_SEQ não tem característica 'Unid Executora Cirurgias'
	 * , não mostrar exceção
	 */
	@Test
	public void testVerificarUnidadeFuncional03() {
		try {
			final AghMicrocomputador micro = new AghMicrocomputador();
			AghUnidadesFuncionais aghUnidadesFuncionais = new AghUnidadesFuncionais();
			aghUnidadesFuncionais.setSeq(Short.parseShort("1"));
			micro.setAghUnidadesFuncionais(aghUnidadesFuncionais);

			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
			mbcHorarioTurnoCirg.setAghUnidadesFuncionais(aghUnidadesFuncionais);

//			mockingContext.checking(new Expectations() {{
//				oneOf(mockIAdministracaoFacade).obterAghMicroComputadorPorNomeOuIP(with(any(String.class)), with(any(DominioCaracteristicaMicrocomputador.class))); 
//				will(returnValue(micro));
//			}});
//
//			mockingContext.checking(new Expectations() {{
//				oneOf(mockIAghuFacade).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)), with(any(ConstanteAghCaractUnidFuncionais.class))); 
//				will(returnValue(false));
//			}});
			systemUnderTest.verificarUnidadeFuncional(mbcHorarioTurnoCirg, "");
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}

	/**
	 * Se o usuário tem a UNF_SEQ na tabela AGH_MICROCOMPUTADORES e a UNF_SEQ tem característica 'Unid Executora Cirurgias', 
	 * e a UNF_SEQ ao qual se está inserindo o turno é igual desta UNF_SEQ, não mostrar exceção
	 */
	@Test
	public void testVerificarUnidadeFuncional04() {
		try {
			final AghMicrocomputador micro = new AghMicrocomputador();
			AghUnidadesFuncionais aghUnidadesFuncionais = new AghUnidadesFuncionais();
			aghUnidadesFuncionais.setSeq(Short.parseShort("1"));
			micro.setAghUnidadesFuncionais(aghUnidadesFuncionais);

			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
			mbcHorarioTurnoCirg.setAghUnidadesFuncionais(aghUnidadesFuncionais);

//			mockingContext.checking(new Expectations() {{
//				oneOf(mockIAdministracaoFacade).obterAghMicroComputadorPorNomeOuIP(with(any(String.class)), with(any(DominioCaracteristicaMicrocomputador.class))); 
//				will(returnValue(micro));
//			}});
//
//			mockingContext.checking(new Expectations() {{
//				oneOf(mockIAghuFacade).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)), with(any(ConstanteAghCaractUnidFuncionais.class))); 
//				will(returnValue(true));
//			}});
			systemUnderTest.verificarUnidadeFuncional(mbcHorarioTurnoCirg, "");
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
	
	@Test
	public void testValidarCamposObrigatorios() {
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setAghUnidadesFuncionais(null);
		try {
			systemUnderTest.validarCamposObrigatorios(mbcHorarioTurnoCirg);
			Assert.fail("Exceção esperada não gerada: CAMPO_OBRIGATORIO");
		} catch (BaseException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgONExceptionCode.CAMPO_OBRIGATORIO, e.getCode());
		}
	}
	
	@Test
	public void testValidarCamposObrigatorios02() {
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		mbcHorarioTurnoCirg.setHorarioInicial(null);
		try {
			systemUnderTest.validarCamposObrigatorios(mbcHorarioTurnoCirg);
			Assert.fail("Exceção esperada não gerada: CAMPO_OBRIGATORIO");
		} catch (BaseException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgONExceptionCode.CAMPO_OBRIGATORIO, e.getCode());
		}
	}
	
	@Test
	public void testValidarCamposObrigatorios03() {
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(new Date());
		mbcHorarioTurnoCirg.setHorarioFinal(null);
		mbcHorarioTurnoCirg.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		try {
			systemUnderTest.validarCamposObrigatorios(mbcHorarioTurnoCirg);
			Assert.fail("Exceção esperada não gerada: CAMPO_OBRIGATORIO");
		} catch (BaseException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgONExceptionCode.CAMPO_OBRIGATORIO, e.getCode());
		}
	}
	
	@Test
	public void testValidarCamposObrigatorios04() {
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(new Date());
		mbcHorarioTurnoCirg.setHorarioFinal(new Date());
		mbcHorarioTurnoCirg.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		mbcHorarioTurnoCirg.setMbcTurnos(new MbcTurnos());
		try {
			systemUnderTest.validarCamposObrigatorios(mbcHorarioTurnoCirg);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
}
