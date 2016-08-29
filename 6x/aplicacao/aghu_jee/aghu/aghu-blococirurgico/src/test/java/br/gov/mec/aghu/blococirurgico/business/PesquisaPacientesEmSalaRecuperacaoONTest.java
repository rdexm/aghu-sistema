package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.model.AghParametros;

@Ignore
public class PesquisaPacientesEmSalaRecuperacaoONTest extends AGHUBaseUnitTest<PacientesEmSalaRecuperacaoON>{

	@Mock
	private IParametroFacade mockedParametroFacade;

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
//		mockedParametroFacade = mockingContext.mock(IParametroFacade.class);
//
//		systemUnderTest = new PacientesEmSalaRecuperacaoON() {
//
//			private static final long serialVersionUID = 8223861734090709564L;
//
//			protected IParametroFacade getParametroFacade() {
//				return mockedParametroFacade;
//			}
//			
//		};
//
//	}

	@Test
	public void recuperarDataAnteriorTeste() throws ApplicationBusinessException {
		final AghParametros parametros = new AghParametros(new BigDecimal(13));
//		mockingContext.checking(new Expectations() {
//			{				
//				oneOf(mockedParametroFacade).buscarAghParametro(AghuParametrosEnum.P_DIAS_RETROATIVOS_DA_CIRURGIA_EM_SR);
//				will(returnValue(parametros));
//			}
//		});
		
		Date dataAtual = new Date(); 
		Date dataTeste = DateUtil.adicionaDias(dataAtual, -13);
		Assert.assertEquals(dataTeste, systemUnderTest.recuperarDataAnterior(dataAtual));
	}
	
}
