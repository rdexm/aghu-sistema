package br.gov.mec.aghu.blococirurgico.business;

import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaDAO;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class ExtratoCirurgiaONTest extends AGHUBaseUnitTest<ExtratoCirurgiaON> {

	@Mock
	private MbcExtratoCirurgiaDAO mockMbcExtratoCirurgiaDAO;
	@Mock
	private ExtratoCirurgiaRN mockExtratoCirurgiaRN;

	@Before
	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};

//		mockExtratoCirurgiaRN = mockingContext.mock(ExtratoCirurgiaRN.class);
//		mockMbcExtratoCirurgiaDAO = mockingContext.mock(MbcExtratoCirurgiaDAO.class);

		systemUnderTest = new ExtratoCirurgiaON() {
			private static final long serialVersionUID = 1L;
			protected MbcExtratoCirurgiaDAO getMbcExtratoCirurgiaDAO() {
				return mockMbcExtratoCirurgiaDAO;
			}
			protected ExtratoCirurgiaRN getExtratoCirurgiaRN() {
				return mockExtratoCirurgiaRN;
			}
		};
	}

	/*@Test
	public void testPersistirMbcExtratoCirurgiaComGravacao() {
		MbcExtratoCirurgia extratoCirurgia = new MbcExtratoCirurgia();
		extratoCirurgia.setId(new MbcExtratoCirurgiaId(Integer.valueOf(1),Integer.valueOf(1).shortValue()));
		extratoCirurgia.setCriadoEm(new Date(2003,3,3));
		
		try {
			esperaObterOriginal();
			esperaAtualizar();
			esperaPosUpdateMbcExtratoCirurgia();
			
			systemUnderTest.persistirMbcExtratoCirurgia(extratoCirurgia);
		} catch (ApplicationBusinessException e) {
			e.printStackTrace();
		}
		mockingContext.assertIsSatisfied();
	}

	@Test
	public void testPersistirMbcExtratoCirurgiaSemGravacao() {
		MbcExtratoCirurgia extratoCirurgia = new MbcExtratoCirurgia();
		extratoCirurgia.setId(new MbcExtratoCirurgiaId(Integer.valueOf(1),Integer.valueOf(1).shortValue()));
		extratoCirurgia.setCriadoEm(new Date(2003,1,1));
		
		try {
			esperaObterOriginal();
			esperaNuncaAtualizar();
			esperaNuncaPosUpdateMbcExtratoCirurgia();
			
			systemUnderTest.persistirMbcExtratoCirurgia(extratoCirurgia);
		} catch (ApplicationBusinessException e) {
			e.printStackTrace();
		}
		mockingContext.assertIsSatisfied();
	}*/

	public void esperaObterOriginal() {
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockMbcExtratoCirurgiaDAO).obterOriginal(with(any(MbcExtratoCirurgia.class)));
//				will(returnValue(extratoCirurgiaOriginal()));
//			}
//			
//			MbcExtratoCirurgia extratoCirurgiaOriginal() {
//				MbcExtratoCirurgia extratoCirurgia = new MbcExtratoCirurgia();
//				extratoCirurgia.setId(new MbcExtratoCirurgiaId(Integer.valueOf(1),Integer.valueOf(1).shortValue()));
//				extratoCirurgia.setCriadoEm(new Date(2003,1,1));
//				return extratoCirurgia;
//			}
//		});
	}
	
	public void esperaAtualizar() {
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockMbcExtratoCirurgiaDAO).atualizar(with(any(MbcExtratoCirurgia.class)));
//			}
//		});
	}
	
	/*private void esperaPosUpdateMbcExtratoCirurgia() throws ApplicationBusinessException {
		mockingContext.checking(new Expectations() {
			{
				oneOf(mockExtratoCirurgiaRN).posUpdateMbcExtratoCirurgia(with(any(MbcExtratoCirurgia.class)));
			}
		});
	}*/

	private void esperaNuncaAtualizar() {
//		mockingContext.checking(new Expectations() {
//			{
//				never(mockMbcExtratoCirurgiaDAO).atualizar(with(any(MbcExtratoCirurgia.class)));
//			}
//		});
	}
	
	/*private void esperaNuncaPosUpdateMbcExtratoCirurgia() throws ApplicationBusinessException {
		mockingContext.checking(new Expectations() {
			{
				never(mockExtratoCirurgiaRN).posUpdateMbcExtratoCirurgia(with(any(MbcExtratoCirurgia.class)));
			}
		});
	}*/
	
}
