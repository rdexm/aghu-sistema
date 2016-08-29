package br.gov.mec.aghu.blococirurgico.business;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.business.MbcAgendaAnestesiaRN.MbcAgendaAnestesiaRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcAgendaAnestesiaRNTest extends AGHUBaseUnitTest<MbcAgendaAnestesiaRN> {

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
//		systemUnderTest = new MbcAgendaAnestesiaRN(){
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -768367015983837648L;
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
	public void testValidarAgendaComControleEscalaCirurgicaDefinitiva1() {
//		mockingContext.checking(new Expectations() {{
//			MbcAgendas agendas = new MbcAgendas();
//			agendas.setIndGeradoSistema(true);
//			oneOf(mockAgendasDAO).pesquisarAgendaComControleEscalaCirurgicaDefinitiva(with(any(Integer.class))); 
//			will(returnValue(agendas));
//		}});
		
		try {
			systemUnderTest.validarAgendaComControleEscalaCirurgicaDefinitiva(new MbcAgendas());
		} catch (BaseException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
		
	}
	
	/**
	 * excecao MBC_00895
	 */
	@Test
	public void testValidarAgendaComControleEscalaCirurgicaDefinitiva2() {
//		mockingContext.checking(new Expectations() {{
//			MbcAgendas agendas = new MbcAgendas();
//			agendas.setIndGeradoSistema(false);
//			oneOf(mockAgendasDAO).pesquisarAgendaComControleEscalaCirurgicaDefinitiva(with(any(Integer.class))); 
//			will(returnValue(agendas));
//		}});
		
		try {
			systemUnderTest.validarAgendaComControleEscalaCirurgicaDefinitiva(new MbcAgendas());
			Assert.fail("Exceção não gerada: MBC_00895");
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaAnestesiaRNExceptionCode.MBC_00895, e.getCode());
		}
	}
	
}
