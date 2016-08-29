package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.MbcMvtoCaractSalaCirgRN.MbcMvtoCaractSalaCirgRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoCaractSalaCirgDAO;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcMvtoCaractSalaCirg;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcMvtoCaractSalaCirgRNTest extends AGHUBaseUnitTest<MbcMvtoCaractSalaCirgRN> {
	
	@Mock
	private MbcMvtoCaractSalaCirgDAO mocMbcMvtoCaractSalaCirgDAO;
	
//	@Before
//	public void doBeforeEachTestCase() {
//		
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//		mocMbcMvtoCaractSalaCirgDAO = mockingContext.mock(MbcMvtoCaractSalaCirgDAO.class);
//		systemUnderTest = new MbcMvtoCaractSalaCirgRN(){
//			private static final long serialVersionUID = 4970220688726487836L;
//
//			@Override
//			protected MbcMvtoCaractSalaCirgDAO getMbcMvtoCaractSalaCirgDAO() {
//				return mocMbcMvtoCaractSalaCirgDAO;
//			};
//			
//		};
//	}
	
	/**
	 * Estourar exceção MBC_01280
	 */
	@Test
	public void testVerificarDadosAtualizados() {
		
		MbcCaracteristicaSalaCirg carac = new MbcCaracteristicaSalaCirg();
		carac.setSeq(Short.valueOf("2"));
		
		MbcMvtoCaractSalaCirg novo = new MbcMvtoCaractSalaCirg();
		novo.setMbcCaracteristicaSalaCirg(carac);
		
//		mockingContext.checking(new Expectations() {{
//			MbcMvtoCaractSalaCirg original = new MbcMvtoCaractSalaCirg();
//			original.setMbcCaracteristicaSalaCirg(new MbcCaracteristicaSalaCirg());
//			
//			oneOf(mocMbcMvtoCaractSalaCirgDAO).obterOriginal(with(any(MbcMvtoCaractSalaCirg.class))); 
//			will(returnValue(original));
//		}});
		
		try {
			systemUnderTest.verificarDadosAtualizados(novo);
			Assert.fail("Exceção não gerada: MBC_01280");
		} catch (BaseException e) {
			Assert.assertEquals(MbcMvtoCaractSalaCirgRNExceptionCode.MBC_01280, e.getCode());
		}
		
	}
	
}
