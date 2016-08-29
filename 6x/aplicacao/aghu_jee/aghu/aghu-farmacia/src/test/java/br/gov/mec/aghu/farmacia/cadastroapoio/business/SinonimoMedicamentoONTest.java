package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaSinonimoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoId;

/**
 * 
 * @author lcmoura
 * 
 */
public class SinonimoMedicamentoONTest extends AGHUBaseUnitTest<SinonimoMedicamentoON>{

	@Mock
	private AfaSinonimoMedicamentoDAO mockedAfaSinonimoMedicamentoDAO;
	@Mock
	private AfaMedicamentoDAO mockedAfaMedicamentoDAO;
	@Mock
	private SinonimoMedicamentoRN mockedSinonimoMedicamentoRN;

	
//	@Before
//	public void doBeforeEachTestCase() throws ApplicationBusinessException {
//
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//
//		mockedAfaSinonimoMedicamentoDAO = mockingContext
//				.mock(AfaSinonimoMedicamentoDAO.class);
//
//		mockedSinonimoMedicamentoRN = mockingContext
//				.mock(SinonimoMedicamentoRN.class);
//
//		mockedAfaMedicamentoDAO = mockingContext.mock(AfaMedicamentoDAO.class);
//
//		systemUnderTest = new SinonimoMedicamentoON() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -2492651799378542309L;
//
//			@Override
//			public AbstractMedicamentoDAO<AfaSinonimoMedicamento> getEntidadeDAO() {
//				return mockedAfaSinonimoMedicamentoDAO;
//			}
//
//			@Override
//			protected SinonimoMedicamentoRN getSinonimoMedicamentoRN() {
//				return mockedSinonimoMedicamentoRN;
//			}
//
//			@Override
//			public AfaMedicamentoDAO getAfaMedicamentoDAO() {
//				return mockedAfaMedicamentoDAO;
//			}
//
//		};
//
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaSinonimoMedicamentoDAO).persistir(
//						with(any(AfaSinonimoMedicamento.class)));
//				
//				oneOf(mockedAfaSinonimoMedicamentoDAO).flush();
//
//				oneOf(mockedAfaSinonimoMedicamentoDAO).atualizar(
//						with(any(AfaSinonimoMedicamento.class)));
//				will(returnValue(getSinonimoMedicamento()));
//
//				allowing(mockedAfaSinonimoMedicamentoDAO).desatachar(
//						with(any(AfaSinonimoMedicamento.class)));
//
//				allowing(mockedAfaSinonimoMedicamentoDAO).remover(
//						with(any(AfaSinonimoMedicamento.class)));
//
//				oneOf(mockedAfaSinonimoMedicamentoDAO).obterPorChavePrimaria(
//						with(any(AfaSinonimoMedicamentoId.class)));
//				will(returnValue(getSinonimoMedicamento()));
//
//				oneOf(mockedAfaSinonimoMedicamentoDAO).pesquisar(
//						with(any(Integer.class)), with(any(Integer.class)),
//						with(any(String.class)), with(any(Boolean.class)),
//						with(any(AfaMedicamento.class)));
//				will(returnValue(Collections
//						.singletonList(getSinonimoMedicamento())));
//
//				oneOf(mockedAfaSinonimoMedicamentoDAO).pesquisarCount(
//						with(any(AfaMedicamento.class)));
//				will(returnValue(Integer.valueOf(1)));
//			}
//		});
//
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaMedicamentoDAO).pesquisarMedicamentos(
//						with(any(String.class)));
//				will(returnValue(Collections.singletonList(getMedicamento())));
//
//				oneOf(mockedAfaMedicamentoDAO).pesquisarMedicamentosCount(
//						with(any(String.class)));
//				will(returnValue(Integer.valueOf(1)));
//			}
//		});
//
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedSinonimoMedicamentoRN)
//						.preInsertSinonimoMedicamento(
//								with(any(AfaSinonimoMedicamento.class)));
//
//				allowing(mockedSinonimoMedicamentoRN)
//						.preUpdateSinonimoMedicamento(
//								with(any(AfaSinonimoMedicamento.class)));
//
//				allowing(mockedSinonimoMedicamentoRN)
//						.posUpdateSinonimoMedicamento(
//								with(any(AfaSinonimoMedicamento.class)),
//								with(any(AfaSinonimoMedicamento.class)));
//
//				allowing(mockedSinonimoMedicamentoRN)
//						.posDeleteSinonimoMedicamento(
//								with(any(AfaSinonimoMedicamento.class)));
//			}
//		});
//	}

	@Test
	public void inserirSinonimoMedicamento() throws ApplicationBusinessException {
		AfaSinonimoMedicamento sinonimo = getSinonimoMedicamento();
		sinonimo.getId().setSeqp(null);

		AfaSinonimoMedicamento retorno = systemUnderTest
				.inserirAtualizarSinonimoMedicamento(sinonimo);
		Assert.assertNotNull(retorno);
	}

	@Test
	public void removerSinonimoMedicamento() throws ApplicationBusinessException {
		systemUnderTest.removerSinonimoMedicamento(getSinonimoMedicamento());
	}

	@Test
	public void pesquisarMedicamentos() {
		systemUnderTest.pesquisarMedicamentos("PARACETAMOL");
	}

	@Test
	public void pesquisarMedicamentosCount() {
		systemUnderTest.pesquisarMedicamentosCount("PARACETAMOL");
	}

	@Test
	public void pesquisar() {
		systemUnderTest.pesquisar(0, 0, "seqp", true, getMedicamento());
	}

	@Test
	public void pesquisarCount() {
		systemUnderTest.pesquisarCount(getMedicamento());
	}

	/**
	 * Cria uma instância de AfaSinonimoMedicamento
	 * 
	 * @return
	 */
	private AfaSinonimoMedicamento getSinonimoMedicamento() {
		AfaSinonimoMedicamento sinonimoMedicamento = new AfaSinonimoMedicamento();
		AfaSinonimoMedicamentoId id = new AfaSinonimoMedicamentoId(208000, 0);
		sinonimoMedicamento.setId(id);
		return sinonimoMedicamento;
	}

	/**
	 * Cria uma instância de AfaMedicamento
	 * 
	 * @return
	 */
	private AfaMedicamento getMedicamento() {
		AfaMedicamento medicamento = new AfaMedicamento();
		medicamento.setMatCodigo(208000);
		return medicamento;
	}
}
