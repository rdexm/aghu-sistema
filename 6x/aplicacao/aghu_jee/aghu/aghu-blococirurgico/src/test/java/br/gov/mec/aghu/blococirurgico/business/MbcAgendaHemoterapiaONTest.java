package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCompSangProcCirgDAO;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaHemoterapiaId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.RapServidores;

@Ignore
public class MbcAgendaHemoterapiaONTest extends AGHUBaseUnitTest<MbcAgendaHemoterapiaON> {

	@Mock
	private IBancoDeSangueFacade		mockBancoDeSangueFacade;
	@Mock
	private MbcCompSangProcCirgDAO		mockMbcCompSangProcCirgDAO;
	@Mock
	private MbcAgendaHemoterapiaRN		mockMbcAgendaHemoterapiaRN;
	@Mock
	private MbcAgendaHemoterapiaDAO     mockMbcAgendaHemoterapiaDAO;

	private MbcAgendaHemoterapiaId		id;
	private MbcAgendaHemoterapiaId		id1;
	private MbcAgendaHemoterapiaId		id2;
	private MbcAgendaHemoterapiaId		id3;
	private MbcAgendaHemoterapia		agendaHemo;
	private MbcAgendaHemoterapia		agendaHemo1;
	private MbcAgendaHemoterapia		agendaHemo2;
	private MbcAgendaHemoterapia		agendaHemo3;
	private MbcAgendas					mbcAgendas;
	private RapServidores				rapServidores;
	private AbsComponenteSanguineo		absComponenteSanguineo;
	private List<MbcAgendaHemoterapia>	agendaHemoterapias;
	private List<MbcAgendaHemoterapia>	agendaHemoterapiasRemover;

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
//		mockBancoDeSangueFacade = mockingContext.mock(IBancoDeSangueFacade.class);
//		mockMbcAgendaHemoterapiaRN = mockingContext.mock(MbcAgendaHemoterapiaRN.class);
//		mockMbcCompSangProcCirgDAO = mockingContext.mock(MbcCompSangProcCirgDAO.class);
//		mockMbcAgendaHemoterapiaDAO = mockingContext.mock(MbcAgendaHemoterapiaDAO.class);
//
//		mbcAgendas = new MbcAgendas();
//		mbcAgendas.setSeq(1);
//		mbcAgendas.setIndGeradoSistema(true);
//		mbcAgendas.setSeq(2);
//		mbcAgendas.setIndGeradoSistema(false);
//		rapServidores = new RapServidores(new RapServidoresId(9999999, Short.parseShort("1")));
//		absComponenteSanguineo = new AbsComponenteSanguineo("PL", "Componente sanguineo de teste", false, false, false, false, rapServidores,
//				DominioSituacao.A, Short.parseShort("1"), Byte.parseByte("1"), Byte.parseByte("1"), Byte.parseByte("1"), Byte.parseByte("1"), "", new Date(),
//				false, false, "", false, false, "Teste", null, false, false, "", new Date(), "", null, null);
//
//		id = new MbcAgendaHemoterapiaId(1, absComponenteSanguineo.getCodigo());
//		id1 = new MbcAgendaHemoterapiaId(2, absComponenteSanguineo.getCodigo());
//		id2 = new MbcAgendaHemoterapiaId(3, absComponenteSanguineo.getCodigo());
//		id3 = new MbcAgendaHemoterapiaId(4, absComponenteSanguineo.getCodigo());
//		agendaHemo = new MbcAgendaHemoterapia(id, mbcAgendas, rapServidores, absComponenteSanguineo, new Date(), false, false, false, Short.parseShort("1"),
//				null);
//		agendaHemo1 = new MbcAgendaHemoterapia(id1, mbcAgendas, rapServidores, absComponenteSanguineo, new Date(), false, false, false, Short.parseShort("1"),
//				null);
//		agendaHemo2 = new MbcAgendaHemoterapia(id2, mbcAgendas, rapServidores, absComponenteSanguineo, new Date(), false, false, false, Short.parseShort("1"),
//				null);
//		agendaHemo3 = new MbcAgendaHemoterapia(id3, mbcAgendas, rapServidores, absComponenteSanguineo, new Date(), false, false, false, Short.parseShort("1"),
//				null);
//		agendaHemoterapias = new ArrayList<MbcAgendaHemoterapia>(2);
//		agendaHemoterapias.add(agendaHemo);
//		agendaHemoterapias.add(agendaHemo1);
//		agendaHemoterapiasRemover = new ArrayList<MbcAgendaHemoterapia>(2);
//		agendaHemoterapias.add(agendaHemo2);
//		agendaHemoterapias.add(agendaHemo3);
//
//		systemUnderTest = new MbcAgendaHemoterapiaON() {
//			private static final long	serialVersionUID	= 4242772787090777523L;
//
//			@Override
//			protected MbcCompSangProcCirgDAO getMbcCompSangProcCirgDAO() {
//				return mockMbcCompSangProcCirgDAO;
//			}
//
//			@Override
//			protected IBancoDeSangueFacade getBancoDeSangueFacade() {
//				return mockBancoDeSangueFacade;
//			}
//
//			@Override
//			protected MbcAgendaHemoterapiaRN getMbcAgendaHemoterapiaRN() {
//				return mockMbcAgendaHemoterapiaRN;
//			}
//			
//			@Override
//			protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO() {
//				return mockMbcAgendaHemoterapiaDAO;
//			}
//
//		};
//	}
//
//	class MyExpectations extends Expectations {
//		{
//			// MbcAgendaHemoterapiaDAO
//			allowing(mockMbcCompSangProcCirgDAO).buscarMbcCompSangProcCirg(with(any(Integer.class)), with(any(Short.class)));
//			will(returnValue(new ArrayList<MbcAgendaHemoterapia>(0)));
//
//			// BancoDeSangueFacade
//			allowing(mockBancoDeSangueFacade).obterComponeteSanguineoPorCodigo(with(any(String.class)));
//			will(returnValue(absComponenteSanguineo));
//
//			// MbcAgendaHemoterapiaRN
//			/* FR try {
//				allowing(mockMbcAgendaHemoterapiaRN).excluirAgendaHemoterapia(with(any(MbcAgendaHemoterapia.class)));
//			} catch (BaseException e) {
//				e.printStackTrace();
//			}
//			try {
//				allowing(mockMbcAgendaHemoterapiaRN).persistirAgendaHemoterapia(with(any(MbcAgendaHemoterapia.class)));
//			} catch (BaseException e) {
//				e.printStackTrace();
//			}*/
//		}
//	}

	@Test
	public void testarGravarAgendaHemoterapia() {
//		mockingContext.checking(new MyExpectations());

		/* FR try {
			
			mockingContext.checking(new Expectations() {
				{
					exactly(4).of(mockMbcAgendaHemoterapiaDAO).desatachar(with(any(MbcAgendaHemoterapia.class)));
				}
			});
			
			systemUnderTest.gravarAgendaHemoterapia(null, agendaHemoterapias, agendaHemoterapiasRemover, mbcAgendas);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}*/
	}

	@Test
	public void testarGravarAgendaHemoterapiaMBC_00567() {

//		mockingContext.checking(new MyExpectations());

		/* FR try {
			final MbcAgendaHemoterapia agHemo = agendaHemoterapias.get(0);
			agHemo.setQtdeMl(null);
			agHemo.setQtdeUnidade(null);
			systemUnderTest.gravarAgendaHemoterapia(null, agendaHemoterapias, agendaHemoterapiasRemover, mbcAgendas);
			Assert.fail("Deveria ocorrer erro MBC_00567.");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaHemoterapiaONExceptionCode.MBC_00567));
		}*/
	}

}