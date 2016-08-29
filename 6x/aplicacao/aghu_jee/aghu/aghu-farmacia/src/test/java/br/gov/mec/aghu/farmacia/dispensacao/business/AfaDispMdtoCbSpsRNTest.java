package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioCoresSinaleiro;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;

@Ignore
public class AfaDispMdtoCbSpsRNTest extends AGHUBaseUnitTest<AfaDispMdtoCbSpsRN> {
	
	//Daos e Facades a serem mockadas
	@Mock
	private AfaDispMdtoCbSpsDAO mockedAfaDispMdtoCbSpsDAO;
	@Mock
	private AfaDispensacaoMdtosDAO mockedAfaDispensacaoMdtosDAO;

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
//		// mocking cada uma das DAOs utilizadas
//		mockedAfaDispMdtoCbSpsDAO = mockingContext.mock(AfaDispMdtoCbSpsDAO.class);
//		mockedAfaDispensacaoMdtosDAO = mockingContext.mock(AfaDispensacaoMdtosDAO.class);
//
//		// criação do objeto da classe a ser testada, com os devidos métodos
//		// sobrescritos.
//		systemUnderTest = new AfaDispMdtoCbSpsRN() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 467144461250581562L;
//
//			@Override
//			protected AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO(){
//				return mockedAfaDispMdtoCbSpsDAO;
//			}
//			
//			@Override
//			protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
//				return mockedAfaDispensacaoMdtosDAO;
//			}
//
//		};
//	}


	@Test
	public void preencheSinaleiraVerdeDisp(){
		DominioCoresSinaleiro resultadoEsperado = DominioCoresSinaleiro.VERDE;
		esperaDispMdtoCount(0);
		esperaSituacaoMdto(DominioSituacaoDispensacaoMdto.D);
		DominioCoresSinaleiro resultadoObtido = systemUnderTest.preencheSinaleira(Long.valueOf(1), new BigDecimal(6));
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void preencheSinaleiraVerde(){
		DominioCoresSinaleiro resultadoEsperado = DominioCoresSinaleiro.VERDE;
		esperaDispMdtoCount(6);
		esperaSituacaoMdto(DominioSituacaoDispensacaoMdto.T);
		DominioCoresSinaleiro resultadoObtido = systemUnderTest.preencheSinaleira(Long.valueOf(1), new BigDecimal(6));
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}

	@Test
	public void preencheSinaleiraAmarelo(){
		DominioCoresSinaleiro resultadoEsperado = DominioCoresSinaleiro.AMARELO;
		esperaDispMdtoCount(123);
		esperaSituacaoMdto(DominioSituacaoDispensacaoMdto.T);
		DominioCoresSinaleiro resultadoObtido = systemUnderTest.preencheSinaleira(Long.valueOf(1), new BigDecimal(200));
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void preencheSinaleiraVermelho(){
		DominioCoresSinaleiro resultadoEsperado = DominioCoresSinaleiro.VERMELHO;
		esperaDispMdtoCount(400);
		esperaSituacaoMdto(DominioSituacaoDispensacaoMdto.T);
		DominioCoresSinaleiro resultadoObtido = systemUnderTest.preencheSinaleira(Long.valueOf(1), new BigDecimal(200));
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void assinalaMdtoDispensado(){
		Boolean resultadoEsperado = Boolean.TRUE;
		esperaDispMdtoCount(0);
		esperaSituacaoMdto(DominioSituacaoDispensacaoMdto.D);
		Boolean resultadoObtido = systemUnderTest.assinalaMedicamentoDispensado(Long.valueOf(1));
		Assert.assertEquals(resultadoEsperado, resultadoObtido);
	}

	private void esperaSituacaoMdto(final DominioSituacaoDispensacaoMdto situacaoMdto) {
//		mockingContext.checking(new Expectations(){{
//			allowing(mockedAfaDispensacaoMdtosDAO).pesquisarSituacaoDispensacaoMdto(with(any(Long.class)));
//			will(returnValue(situacaoMdto));
//		}});

	}


	private void esperaDispMdtoCount(final Integer qtdeEtiquetas) {
//		mockingContext.checking(new Expectations(){{
//			allowing(mockedAfaDispMdtoCbSpsDAO).getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(with(any(Long.class)),with(any(DominioIndExcluidoDispMdtoCbSps.class)));
//			will(returnValue(qtdeEtiquetas));
//		}});

	}

	private String getMsgResultado(
			DominioCoresSinaleiro resultadoEsperado,
			DominioCoresSinaleiro resultadoObtido) {
		return "O resultado esperado era ==> " + resultadoEsperado + " mas o resultado obtido foi ==> " + resultadoObtido;
	}


	public static enum TiposRetornoSinaleira {
		AMARELO, VERMELHO, VERDE;
	}

}
