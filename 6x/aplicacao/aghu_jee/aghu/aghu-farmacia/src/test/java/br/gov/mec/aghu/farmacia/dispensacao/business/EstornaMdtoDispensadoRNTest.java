package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioIndExcluidoDispMdtoCbSps;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoOcorDispensacaoDAO;
import br.gov.mec.aghu.farmacia.dispensacao.business.EstornaMedicamentoDispensadoRN.EstornarMedicamentoDispensadoRNExceptionCode;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;

@Ignore
public class EstornaMdtoDispensadoRNTest extends AGHUBaseUnitTest<EstornaMedicamentoDispensadoRN>{
	
	private static final Log log = LogFactory.getLog(EstornaMdtoDispensadoRNTest.class);
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	// Daos e Facades a serem mockadas
	@Mock
	private AfaTipoOcorDispensacaoDAO mockedAfaTipoOcorDispensacaoDAO;
	@Mock
	private AfaDispMdtoCbSpsDAO mockedAfaDispMdtoCbSpsDAO;
	@Mock
	private AfaDispensacaoMdtosDAO mockedAfaDispensacaoMdtosDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
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
//		// mocking cada uma das DAOs utilizadas
//		mockedAfaTipoOcorDispensacaoDAO = mockingContext
//				.mock(AfaTipoOcorDispensacaoDAO.class);
//		mockedAfaDispensacaoMdtosDAO = mockingContext
//				.mock(AfaDispensacaoMdtosDAO.class);
//		mockedAfaDispMdtoCbSpsDAO = mockingContext
//				.mock(AfaDispMdtoCbSpsDAO.class);
//		mockedAghuFacade = mockingContext.mock(IAghuFacade.class);
//		mockedParametroFacade = mockingContext.mock(IParametroFacade.class);
//
//		// criação do objeto da classe a ser testada, com os devidos métodos
//		// sobrescritos.
//		systemUnderTest = new EstornaMedicamentoDispensadoRN() {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -7624757008316315512L;
//
//			/**
//			 * 
//			 */
//			
//			protected AfaTipoOcorDispensacaoDAO getAfaTipoOcorDispensacaoDAO() {
//				return mockedAfaTipoOcorDispensacaoDAO;
//			};
//
//			protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO() {
//				return mockedAfaDispensacaoMdtosDAO;
//			}
//
//			protected AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO() {
//				return mockedAfaDispMdtoCbSpsDAO;
//			}
//
//			protected IAghuFacade getAghuFacade() {
//				return mockedAghuFacade;
//			}
//			
//			@Override
//			protected IParametroFacade getParametroFacade() {
//				return mockedParametroFacade;
//			}
//		};
//	}

	// *****************************************************************************************
	// ****testes

	//TODO TESTE ORADB TRIGGER "AGH".AFAT_SDC_BRU
	//bruPreAtualizacaoAfaDispMdtoCbSps
//	@Test
//	public void testarBruPreAtualizacaoAfaDispMdtoCbSps() {

//	}

	//@Test
	public void testarEstornaDispensacaoMdtoComEtiquetaOK() {

		// Expectations
		esperarObterListaDispMdtoCbSpsPorEtiquetaValida();
		esperarGetDispMdtoCbSpsByEtiqueta();
		esperarBuscarAghParametroTipoOcorrencia();
		esperarObterAfaTipoOcorDispensacaoPorChavePrimaria();
		esperarPersistirDispMdtoCB();
		esperarPersistirDispMdto();

		String etiqueta = criarEtiquetaValida();
		try {
			systemUnderTest.estornarDispensacaoMdtoComEtiqueta(etiqueta, NOME_MICROCOMPUTADOR);
//			mockingContext.assertIsSatisfied();
		} catch (BaseException e) {
			Assert.fail("Não deveria gerar excecao");
		}
	}
	
	@Test
	public void testarEstornaDispensacaoMdtoComEtiquetaNOK() {

		// Expectations
		esperarObterListaDispMdtoCbSpsPorEtiquetaInvalida();
		esperarGetDispMdtoCbSpsByEtiqueta();
		esperarBuscarAghParametroTipoOcorrencia();
		esperarObterAfaTipoOcorDispensacaoPorChavePrimaria();
		esperarPersistirDispMdtoCB();
		esperarPersistirDispMdto();

		String etiqueta = criarEtiquetaInvalida();
		try {
			systemUnderTest.estornarDispensacaoMdtoComEtiqueta(etiqueta, NOME_MICROCOMPUTADOR);
			Assert.fail("Deveria gerar excecao");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(EstornarMedicamentoDispensadoRNExceptionCode.AFA_01491, e.getCode());
		} catch (Exception e) {
			Assert.fail("Deveria gerar excecao diferente de ApplicationBusinessException código AFA_01491");
		}
	}
	
	@Test
	public void testarBuscarDiasEstornoNOK() {
		try {
			esperarBuscarAghParametroDiasEstornoErro();
			systemUnderTest.buscarDiasEstorno();
			Assert.fail("Deveria ter lançado a excecao AFA_01232");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(EstornarMedicamentoDispensadoRNExceptionCode.AFA_01232, e.getCode());
		} catch (Exception e) {
			Assert.fail("Deveria gerar excecao diferente de ApplicationBusinessException código AFA_01232");
		}
	}


	public void esperarObterListaDispMdtoCbSpsPorEtiquetaValida() {
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedAfaDispMdtoCbSpsDAO)
//						.obterListaDispMdtoCbSpsPorEtiqueta(
//								with(any(String.class)),
//								with(any(DominioIndExcluidoDispMdtoCbSps.class)));
//				will(returnValue(criarListaEtiquetas(true)));
//			}
//		});
	}
	
	public void esperarObterListaDispMdtoCbSpsPorEtiquetaInvalida() {
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedAfaDispMdtoCbSpsDAO)
//						.obterListaDispMdtoCbSpsPorEtiqueta(
//								with(any(String.class)),
//								with(any(DominioIndExcluidoDispMdtoCbSps.class)));
//				will(returnValue(criarListaEtiquetas(false)));
//			}
//		});
	}

	public void esperarGetDispMdtoCbSpsByEtiqueta() {

//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedAfaDispMdtoCbSpsDAO).getDispMdtoCbSpsByEtiqueta(
//						with(any(String.class)),
//						with(any(DominioIndExcluidoDispMdtoCbSps.class)));
//				will(returnValue(criarDispMdtoCb()));
//			}
//		});
	}

	public void esperarObterAfaTipoOcorDispensacaoPorChavePrimaria() {
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedAfaTipoOcorDispensacaoDAO)
//						.obterPorChavePrimaria(with(any(Object.class)));
//				will(returnValue(criarAfaTipoOcorDispensacaoPadraoEstorno()));
//			}
//		});
	}

	public void esperarBuscarAghParametroTipoOcorrencia() {

//		try {
//			mockingContext.checking(new Expectations() {
//				{
//					allowing(mockedParametroFacade).buscarAghParametro(
//							with(any(AghuParametrosEnum.class)));
//					will(returnValue(criarAghParametrosTipoOcorrenciaEstornoPadrao()));
//				}
//			});
//		} catch (ApplicationBusinessException e) {
//			log.error(e.getMessage());
//		}
	}
	
	public void esperarBuscarAghParametroDiasEstornoErro() {

//		try {
//			mockingContext.checking(new Expectations() {
//				{
//					allowing(mockedParametroFacade).buscarAghParametro(
//							with(any(AghuParametrosEnum.class)));
//					will(throwException(new ApplicationBusinessException(
//							AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE)));
//				}
//			});
//		} catch (ApplicationBusinessException e) {
//			log.error(e.getMessage());
//		}
	}

	public void esperarPersistirDispMdto() {

//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedAfaDispensacaoMdtosDAO).persistir(
//						with(any(AfaDispensacaoMdtos.class)));
//				will(returnValue(new AfaDispensacaoMdtos()));
//			}
//		});
	}

	public void esperarPersistirDispMdtoCB() {

//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedAfaDispMdtoCbSpsDAO).persistir(
//						with(any(AfaDispMdtoCbSps.class)));
//				
//			}
//		});
	}

//UTIL
	
	private RapServidores criarServidor() {

		RapServidores servidor = new RapServidores();
		RapServidoresId idServidor = new RapServidoresId();
		idServidor.setMatricula(1);
		idServidor.setVinCodigo(Short.valueOf("1"));
		servidor.setId(idServidor);

		return servidor;
	}

	private String criarEtiquetaValida() {
		return "012345678901234";
	}

	private List<AfaDispMdtoCbSps> criarListaEtiquetas(Boolean etiquetasValidas) {

		List<AfaDispMdtoCbSps> etiquetas = new ArrayList<AfaDispMdtoCbSps>();
		if (etiquetasValidas) {
			AfaDispMdtoCbSps etiqueta1 = criarDispMdtoCb();
			etiquetas.add(etiqueta1);
		}	

		return etiquetas;
	}

	private AfaDispMdtoCbSps criarDispMdtoCb() {

		AfaDispMdtoCbSps afaDispMdtoCbSps = new AfaDispMdtoCbSps();
		afaDispMdtoCbSps.setIndExcluido(DominioIndExcluidoDispMdtoCbSps.I);
			afaDispMdtoCbSps.setNroEtiqueta(criarEtiquetaValida());
		AfaDispensacaoMdtos dispMdtoCb = new AfaDispensacaoMdtos();
		afaDispMdtoCbSps.setDispensacaoMdto(dispMdtoCb);

		return afaDispMdtoCbSps;
	}

	private AghParametros criarAghParametrosTipoOcorrenciaEstornoPadrao() {

		AghParametros paramTipoOcorrenciaPadrao = new AghParametros();
		paramTipoOcorrenciaPadrao.setVlrNumerico(new BigDecimal(63));

		return paramTipoOcorrenciaPadrao;
	}

	private AfaTipoOcorDispensacao criarAfaTipoOcorDispensacaoPadraoEstorno() {

		AfaTipoOcorDispensacao tipoOcorEstorno = new AfaTipoOcorDispensacao();
		tipoOcorEstorno.setSeq(Short.valueOf("63"));
		tipoOcorEstorno.setDescricao("Sobra medicação");

		return tipoOcorEstorno;
	}

	private String criarEtiquetaInvalida() {
		return "987654321098765";
	}

	
}
