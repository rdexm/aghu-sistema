package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.MbcMvtoSalaEspEquipeRN.MbcMvtoSalaEspEquipeRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoSalaEspEquipeDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcMvtoSalaEspEquipe;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcMvtoSalaEspEquipeRNTest extends AGHUBaseUnitTest<MbcMvtoSalaEspEquipeRN> {
	
	@Mock
	private MbcMvtoSalaEspEquipeDAO mockMbcMvtoSalaEspEquipeDAO;
	
//	@Before
//	public void doBeforeEachTestCase() {
//		
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//		mockMbcMvtoSalaEspEquipeDAO = mockingContext.mock(MbcMvtoSalaEspEquipeDAO.class);
//		systemUnderTest = new MbcMvtoSalaEspEquipeRN(){
//			private static final long serialVersionUID = 1580354999290357324L;
//
//			@Override
//			protected MbcMvtoSalaEspEquipeDAO getMbcMvtoSalaEspEquipeDAO() {
//				return mockMbcMvtoSalaEspEquipeDAO;
//			};
//			
//		};
//	}
	
	/**
	 * Estourar exceção MBC_01281
	 */
	@Test
	public void testVerificarDadosAtualizados() {
		
		
		MbcMvtoSalaEspEquipe novo = new MbcMvtoSalaEspEquipe();
		novo.setIndSituacao(DominioSituacao.A);
		
//		mockingContext.checking(new Expectations() {{
//			MbcMvtoSalaEspEquipe original = new MbcMvtoSalaEspEquipe();
//			original.setIndSituacao(DominioSituacao.I);
//			
//			oneOf(mockMbcMvtoSalaEspEquipeDAO).obterOriginal(with(any(MbcMvtoSalaEspEquipe.class))); 
//			will(returnValue(original));
//		}});
		
		try {
			systemUnderTest.verificarDadosAtualizados(novo);
			Assert.fail("Exceção não gerada: MBC_01281");
		} catch (BaseException e) {
			Assert.assertEquals(MbcMvtoSalaEspEquipeRNExceptionCode.MBC_01281, e.getCode());
		}
		
	}
	
}
