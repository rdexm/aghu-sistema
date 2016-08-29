package br.gov.mec.aghu.blococirurgico.business;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class RelatorioPacientesComCirurgiaPorUnidadeONTest extends AGHUBaseUnitTest<RelatorioPacientesComCirurgiaPorUnidadeON>{

	private MbcSolicHemoCirgAgendadaDAO mockedMbcSolicHemoCirgAgendadaDAO;
	private IAghuFacade mockedAghuFacade;

//	@Before
//	public void doBeforeEachTestCase() {
//
//		// contexto dos mocks, usado para criar os mocks e definir a expectativa
//		// de chamada dos métodos.
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//
//		mockedMbcSolicHemoCirgAgendadaDAO = mockingContext.mock(MbcSolicHemoCirgAgendadaDAO.class);
//		mockedAghuFacade = mockingContext.mock(IAghuFacade.class);
//		
//		systemUnderTest = new RelatorioPacientesComCirurgiaPorUnidadeON() {
//
//			private static final long serialVersionUID = 8223861734090709564L;
//
//			protected MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO() {
//				return mockedMbcSolicHemoCirgAgendadaDAO;
//			}
//			
//			protected IAghuFacade getAghuFacade() {
//				return mockedAghuFacade;
//			}
//		};
//	}

	@Test
	public void cfSangueFormulaTeste() {
		Integer crgSeq = 0;
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedMbcSolicHemoCirgAgendadaDAO).mbcSolicHemoCirgAgendadaCountPorCrgSeq(with(any(Integer.class)));
//				will(returnValue(1));
//			}
//		});
		Assert.assertEquals("BANCO DE SANGUE", systemUnderTest.cfSangueFormula(crgSeq));
	}
	
	@Test
	public void cfInternacaoFormulaTeste() {
		Short unfSeq = 0;

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAghuFacade).obterAghUnidadesFuncionaisPorChavePrimaria(with(any(Short.class)));
//				will(returnValue(null));
//			}
//		});
		
		Assert.assertEquals("PACIENTES SEM INDICAÇÃO DE QUARTO/LEITO", systemUnderTest.cfInternacaoFormula(unfSeq));
	}
}
