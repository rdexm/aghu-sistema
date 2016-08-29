package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class InstrumentosRNTest extends AGHUBaseUnitTest<PdtInstrumentalRN> {

	private IRegistroColaboradorFacade mockRegistroColaboradorFacade;

//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//
//		mockRegistroColaboradorFacade = mockingContext.mock(IRegistroColaboradorFacade.class);
//
//		systemUnderTest = new PdtInstrumentalRN(){
//
//			private static final long serialVersionUID = -3217383646066856608L;
//		};
//	}

	@Test
	public void testAtualizarServidor() {
		try {
			esperarRegistroColaboradorFacadeObterServidorAtivoPorUsuario();
			Assert.fail();
		} catch (ApplicationBusinessException e) {
//			mockingContext.assertIsSatisfied();
		}
	}
	
	private void esperarRegistroColaboradorFacadeObterServidorAtivoPorUsuario() throws ApplicationBusinessException {
//		mockingContext.checking(new Expectations(){{
//			allowing(mockRegistroColaboradorFacade).obterServidorAtivoPorUsuario((with(any(String.class))));
//			will(returnValue(getServidorAtivo()));
//		}
//
//		private RapServidores getServidorAtivo() {
//			RapServidores servidor = new RapServidores();
//			servidor.setId(new RapServidoresId());
//			return servidor;
//		}});
	}
}
