package br.gov.mec.aghu.blococirurgico.business;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.business.MbcAgendaHemoterapiaRN.MbcAgendaProcedimentoRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHistoricoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaHemoterapiaId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcAgendaHemoterapiaRNTest extends AGHUBaseUnitTest<MbcAgendaHemoterapiaRN> {

	@Mock
	private IRegistroColaboradorFacade	mockedRegistroColaboradorFacade;
	@Mock
	private IBancoDeSangueFacade		mockedBancoDeSangueFacade;
	@Mock
	private MbcAgendaHistoricoRN		mockedMbcAgendaHistoricoRN;
	@Mock
	private MbcAgendasDAO				mockedAgendasDAO;
	@Mock
	private MbcAgendaHemoterapiaDAO		mockMbcAgendaHemoterapiaDAO;
	@Mock
	private MbcAgendaHistoricoDAO		mockMbcAgendaHistoricoDAO;

	private MbcAgendaHemoterapiaId		id;
	private MbcAgendaHemoterapia		agendaHemo;
	private MbcAgendas					mbcAgendas;
	private MbcAgendas					mbcAgendasNaoGeradoSistema;
	private AbsComponenteSanguineo		absComponenteSanguineo;

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
//		mockedRegistroColaboradorFacade = mockingContext.mock(IRegistroColaboradorFacade.class);
//		mockedBancoDeSangueFacade = mockingContext.mock(IBancoDeSangueFacade.class);
//		mockedMbcAgendaHistoricoRN = mockingContext.mock(MbcAgendaHistoricoRN.class);
//		mockedAgendasDAO = mockingContext.mock(MbcAgendasDAO.class);
//		mockMbcAgendaHemoterapiaDAO = mockingContext.mock(MbcAgendaHemoterapiaDAO.class);
//		mockMbcAgendaHistoricoDAO = mockingContext.mock(MbcAgendaHistoricoDAO.class);
//		mbcAgendas = new MbcAgendas();
//		mbcAgendasNaoGeradoSistema = new MbcAgendas();
//		mbcAgendas.setSeq(2);
//		mbcAgendas.setIndGeradoSistema(true);
//		absComponenteSanguineo = new AbsComponenteSanguineo("PL", "Componente sanguineo de teste", false, false, false, false, new RapServidores(),
//				DominioSituacao.A, Short.parseShort("1"), Byte.parseByte("1"), Byte.parseByte("1"), Byte.parseByte("1"), Byte.parseByte("1"), "", new Date(),
//				false, false, "", false, false, "Teste", null, false, false, "", new Date(), "", null, null);
//
//		id = new MbcAgendaHemoterapiaId(1, absComponenteSanguineo.getCodigo());
//		agendaHemo = new MbcAgendaHemoterapia(id, mbcAgendas, new RapServidores(), absComponenteSanguineo, new Date(), false, false, false, Short.parseShort("1"),
//				null);
//
//		systemUnderTest = new MbcAgendaHemoterapiaRN() {
//			private static final long	serialVersionUID	= -3753414232060663919L;
//
//			@Override
//			protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO() {
//				return mockMbcAgendaHemoterapiaDAO;
//			}
//
//			@Override
//			protected MbcAgendasDAO getMbcAgendasDAO() {
//				return mockedAgendasDAO;
//			}
//
//			@Override
//			protected MbcAgendaHistoricoDAO getMbcAgendaHistoricoDAO() {
//				return mockMbcAgendaHistoricoDAO;
//			}
//
//			@Override
//			protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
//				return mockedRegistroColaboradorFacade;
//			}
//			
//			@Override
//			protected IBancoDeSangueFacade getBancoDeSangueFacade() {
//				return mockedBancoDeSangueFacade;
//			}
//
//			@Override
//			protected MbcAgendaHistoricoRN getMbcAgendaHistoricoRN() {
//				return mockedMbcAgendaHistoricoRN;
//			}
//		};
//	}
//
//	class MyExpectations extends Expectations {
//		{
//			// MbcAgendaHemoterapiaDAO
//			allowing(mockMbcAgendaHemoterapiaDAO).obterOriginal(with(id));
//			will(returnValue(agendaHemo));
//
//			oneOf(mockMbcAgendaHemoterapiaDAO).persistir(with(any(MbcAgendaHemoterapia.class)));
//			oneOf(mockMbcAgendaHemoterapiaDAO).atualizar(with(any(MbcAgendaHemoterapia.class)));
//			oneOf(mockMbcAgendaHemoterapiaDAO).remover(with(any(MbcAgendaHemoterapia.class)));
//
//			// MbcAgendasDAO
//			allowing(mockedAgendasDAO).pesquisarAgendaComControleEscalaCirurgicaDefinitiva(with(1));
//			will(returnValue(mbcAgendas));
//
//			allowing(mockedAgendasDAO).pesquisarAgendaComControleEscalaCirurgicaDefinitiva(with(2));
//			will(returnValue(mbcAgendasNaoGeradoSistema));
//
//			// MbcAgendaHistoricoDAO
//			allowing(mockMbcAgendaHistoricoDAO).buscarProximoSeqp((with(mbcAgendas.getSeq())));
//			will(returnValue(0.0));
//
//			// BancoDeSangueFacade
//			allowing(mockedBancoDeSangueFacade).obterAbsComponenteSanguineoOriginal(with(any(String.class)));
//			will(returnValue(absComponenteSanguineo));
//			
//			// BlocoCirurgicoPortalPlanejamentoFacade
//			/* FR try {
//				oneOf(mockedMbcAgendaHistoricoRN).inserir(with(any(Integer.class)), with(any(DominioSituacaoAgendas.class)), with(any(DominioOrigem.class)),
//						with(any(String.class)), with(any(DominioOperacaoAgenda.class)));
//			} catch (BaseException e) {
//				e.printStackTrace();
//			}*/
//		}
//	}

	@Test
	public void testarPersistirAgendaHemoterapiaAtualizar() {
//		mockingContext.checking(new MyExpectations());

		/* FR try {
			systemUnderTest.persistirAgendaHemoterapia(agendaHemo);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}*/
	}

	@Test
	public void testarPersistirAgendaHemoterapiaInserir() {
//		mockingContext.checking(new MyExpectations());
		esperarTestarPersistirAgendaHemoterapiaInserir();
		/* FR try {
			systemUnderTest.persistirAgendaHemoterapia(agendaHemo);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}*/
	}

	@Test
	public void testarExcluirAgendaHemoterapia() {

//		mockingContext.checking(new MyExpectations());

		/* FR try {
			systemUnderTest.excluirAgendaHemoterapia(agendaHemo);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}*/
	}

	@Test
	public void testarVerificarIndicadores() {

//		mockingContext.checking(new MyExpectations());

		try {
			systemUnderTest.verificarIndicadores(agendaHemo);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}

	@Test
	public void testarVerificarIndicadoresMBC_00865() {

		esperarTestarVerificarIndicadoresMBC_00865();
		agendaHemo.setIndIrradiado(true);
		try {
			systemUnderTest.verificarIndicadores(agendaHemo);
			Assert.fail("Deveria ocorrer erro MBC_00865.");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_00865));
		}
	}

	@Test
	public void testarVerificarIndicadoresMBC_00866() {

//		mockingContext.checking(new MyExpectations());

		try {
			agendaHemo.setIndIrradiado(true);
			systemUnderTest.verificarIndicadores(agendaHemo);
			Assert.fail("Deveria ocorrer erro MBC_00866.");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_00866));
		}
	}

	@Test
	public void testarVerificarIndicadoresMBC_00867() {

//		mockingContext.checking(new MyExpectations());

		try {
			agendaHemo.setIndFiltrado(true);
			systemUnderTest.verificarIndicadores(agendaHemo);
			Assert.fail("Deveria ocorrer erro MBC_00867.");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_00867));
		}
	}

	@Test
	public void testarVerificarIndicadoresMBC_00868() {

//		mockingContext.checking(new MyExpectations());

		try {
			agendaHemo.setIndLavado(true);
			systemUnderTest.verificarIndicadores(agendaHemo);
			Assert.fail("Deveria ocorrer erro MBC_00868.");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_00868));
		}
	}

	@Test
	public void testarValidarAgendaComControleEscalaCirurgicaDefinitiva() {

//		mockingContext.checking(new MyExpectations());

		try {
			systemUnderTest.validarAgendaComControleEscalaCirurgicaDefinitiva(2);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}

	@Test
	public void testarValidarAgendaComControleEscalaCirurgicaDefinitivaMBC_00836() {

//		mockingContext.checking(new MyExpectations());
		mbcAgendas.setIndGeradoSistema(false);
		try {
			systemUnderTest.validarAgendaComControleEscalaCirurgicaDefinitiva(1);
			Assert.fail("Deveria ocorrer erro MBC_00836.");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_00836));
		}
	}

	@Test
	public void testarIncluirHistoricoAgendaHemoterapia() {

//		mockingContext.checking(new MyExpectations());

		/* FR try {
			systemUnderTest.incluirHistoricoAgendaHemoterapia(agendaHemo, agendaHemo, DominioOperacaoAgenda.A);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}*/
	}
	
	private void esperarTestarVerificarIndicadoresMBC_00865(){
//		mockingContext.checking(new Expectations(){{
//			allowing(mockedBancoDeSangueFacade).obterAbsComponenteSanguineoOriginal(with(any(String.class)));
//			will(returnValue(null));
//		}});
	}
	
	private void esperarTestarPersistirAgendaHemoterapiaInserir(){
//		mockingContext.checking(new Expectations(){{
//			allowing(mockMbcAgendaHemoterapiaDAO).obterOriginal(with(id));
//			will(returnValue(null));
//		}});
	}
}