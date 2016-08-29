package br.gov.mec.aghu.blococirurgico.business;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.business.MbcAgendaDiagnosticoRN.MbcAgendaDiagnosticoRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcAgendaDiagnosticoRNTest extends AGHUBaseUnitTest<MbcAgendaDiagnosticoRN> {

	@Mock
	private MbcAgendasDAO mockAgendasDAO;

//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//
//		mockAgendasDAO = mockingContext.mock(MbcAgendasDAO.class);
//
//		systemUnderTest = new MbcAgendaDiagnosticoRN(){
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -6893735827602065057L;
//
//			@Override
//			protected MbcAgendasDAO getMbcAgendasDAO() {
//				return mockAgendasDAO;
//			};
//
//		};
//	}

	/**
	 * sucesso
	 */
	@Test
	public void testvalidarAgendaComControleEscalaCirurgicaDefinitiva1() {
//		mockingContext.checking(new Expectations() {{
//			MbcAgendas agendas = new MbcAgendas();
//			agendas.setIndGeradoSistema(true);
//			oneOf(mockAgendasDAO).pesquisarAgendaComControleEscalaCirurgicaDefinitiva(with(any(Integer.class))); 
//			will(returnValue(agendas));
//		}});
		
		try {
			systemUnderTest.validarAgendaComControleEscalaCirurgicaDefinitiva(2);
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
		
	}
	
	/**
	 * excecao MBC_01130
	 */
	@Test
	public void testvalidarAgendaComControleEscalaCirurgicaDefinitiva2() {
//		mockingContext.checking(new Expectations() {{
//			MbcAgendas agendas = new MbcAgendas();
//			agendas.setIndGeradoSistema(false);
//			oneOf(mockAgendasDAO).pesquisarAgendaComControleEscalaCirurgicaDefinitiva(with(any(Integer.class))); 
//			will(returnValue(agendas));
//		}});
		
		try {
			systemUnderTest.validarAgendaComControleEscalaCirurgicaDefinitiva(2);
			Assert.fail("Exceção não gerada: MBC_00895");
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaDiagnosticoRNExceptionCode.MBC_01130, e.getCode());
		}
	}
	
}
