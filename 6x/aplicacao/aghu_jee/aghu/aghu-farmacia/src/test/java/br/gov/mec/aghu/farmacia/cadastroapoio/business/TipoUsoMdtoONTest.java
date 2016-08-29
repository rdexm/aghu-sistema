package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.dao.AfaTipoUsoMdtoDAO;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;

/**
 * Classe de teste unitário para TipoUsoMdtoON
 * 
 * @author lcmoura
 * 
 */
@Ignore
public class TipoUsoMdtoONTest extends AGHUBaseUnitTest<TipoUsoMdtoON> {

	private static final String DESCRICAO = "descrição";

	private AfaTipoUsoMdto tipoUsoMdtoIn;
	private AfaTipoUsoMdto tipoUsoMdtoAt;
	private AfaTipoUsoMdto tipoUsoMdtoAtOld;

	@Mock
	private AfaTipoUsoMdtoDAO mockedAfaTipoUsoMdtoDAO;
	@Mock
	private TipoUsoMdtoRN mockedTipoUsoMdtoRN;

//	@Before
//	public void doBeforeEachTestCase() {
//
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//
//		mockedAfaTipoUsoMdtoDAO = mockingContext.mock(AfaTipoUsoMdtoDAO.class);
//		mockedTipoUsoMdtoRN = mockingContext.mock(TipoUsoMdtoRN.class);
//
//		systemUnderTest = new TipoUsoMdtoON() {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 7454751447118790810L;
//
//			@Override
//			public AfaTipoUsoMdtoDAO getAfaTipoUsoMdtoDAO() {
//				return mockedAfaTipoUsoMdtoDAO;
//			}
//
//			@Override
//			public TipoUsoMdtoRN getTipoUsoMdtoRN() {
//				return mockedTipoUsoMdtoRN;
//			}
//
//		};
//
//		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
//				Integer.valueOf(1), DESCRICAO, DominioSituacao.A);
//
//		tipoUsoMdtoIn = new AfaTipoUsoMdto("T1", grupoUsoMedicamento, null,
//				DESCRICAO, true, true, true, true, Calendar.getInstance()
//						.getTime(), DominioSituacao.A, true, true);
//
//		tipoUsoMdtoAt = new AfaTipoUsoMdto("T2", grupoUsoMedicamento, null,
//				DESCRICAO, true, true, true, true, Calendar.getInstance()
//						.getTime(), DominioSituacao.A, true, true);
//
//		tipoUsoMdtoAtOld = new AfaTipoUsoMdto("T2", grupoUsoMedicamento, null,
//				DESCRICAO, false, false, false, false, Calendar.getInstance()
//						.getTime(), DominioSituacao.A, false, false);
//	}

	@Test
	public void inserir() throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).persistir(
//						with(any(AfaTipoUsoMdto.class)));
//				
//				oneOf(mockedAfaTipoUsoMdtoDAO).flush();
//
//				oneOf(mockedAfaTipoUsoMdtoDAO).atualizar(
//						with(any(AfaTipoUsoMdto.class)));
//				will(returnValue(tipoUsoMdtoAt));
//
//				allowing(mockedAfaTipoUsoMdtoDAO).desatachar(
//						with(any(AfaTipoUsoMdto.class)));
//
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(AfaTipoUsoMdto.class)));
//				will(returnValue(null));
//
//				allowing(mockedTipoUsoMdtoRN).preInsertTipoUsoMdto(
//						with(any(AfaTipoUsoMdto.class)));
//
//				allowing(mockedTipoUsoMdtoRN).preUpdateTipoUsoMdto(
//						with(any(AfaTipoUsoMdto.class)),
//						with(any(AfaTipoUsoMdto.class)));
//
//				allowing(mockedTipoUsoMdtoRN).posUpdateTipoUsoMdto(
//						with(any(AfaTipoUsoMdto.class)),
//						with(any(AfaTipoUsoMdto.class)));
//			}
//		});
		
		Mockito.when(mockedAfaTipoUsoMdtoDAO.obterPorChavePrimaria(Mockito.any(AfaTipoUsoMdto.class))).thenReturn(null);

		AfaTipoUsoMdto parametro = new AfaTipoUsoMdto();
		
		AfaTipoUsoMdto retorno = systemUnderTest.inserirAtualizar(parametro, null);

		Assert.assertEquals(parametro, retorno);
	}

	@Test
	public void atualizar() throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).persistir(
//						with(any(AfaTipoUsoMdto.class)));
//				oneOf(mockedAfaTipoUsoMdtoDAO).flush();
//
//				oneOf(mockedAfaTipoUsoMdtoDAO).atualizar(
//						with(any(AfaTipoUsoMdto.class)));
//				will(returnValue(tipoUsoMdtoAt));
//
//				allowing(mockedAfaTipoUsoMdtoDAO).desatachar(
//						with(any(AfaTipoUsoMdto.class)));
//
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(Integer.class)));
//				will(returnValue(tipoUsoMdtoAtOld));
//
//				allowing(mockedTipoUsoMdtoRN).preInsertTipoUsoMdto(
//						with(any(AfaTipoUsoMdto.class)));
//
//				allowing(mockedTipoUsoMdtoRN).preUpdateTipoUsoMdto(
//						with(any(AfaTipoUsoMdto.class)),
//						with(any(AfaTipoUsoMdto.class)));
//
//				allowing(mockedTipoUsoMdtoRN).posUpdateTipoUsoMdto(
//						with(any(AfaTipoUsoMdto.class)),
//						with(any(AfaTipoUsoMdto.class)));
//
//			}
//		});
		
//		oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//		with(any(Integer.class)));
//		will(returnValue(tipoUsoMdtoAtOld));
		Mockito.when(mockedAfaTipoUsoMdtoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(tipoUsoMdtoAtOld);

		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
				Integer.valueOf(1), DESCRICAO, DominioSituacao.A);
		
		tipoUsoMdtoAt = new AfaTipoUsoMdto("T2", grupoUsoMedicamento, null,
				DESCRICAO, true, true, true, true, Calendar.getInstance()
						.getTime(), DominioSituacao.A, true, true);
		
		AfaTipoUsoMdto retorno = systemUnderTest.inserirAtualizar(
				tipoUsoMdtoAt, Boolean.FALSE);

		Assert.assertEquals(tipoUsoMdtoAt, retorno);
	}

	public void obterPorChavePrimaria() throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).obterPorChavePrimaria(
//						with(any(Integer.class)));
//				will(returnValue(tipoUsoMdtoAtOld));
//			}
//		});
		
		Mockito.when(mockedAfaTipoUsoMdtoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(tipoUsoMdtoAtOld);

		AfaTipoUsoMdto retorno = systemUnderTest
				.obterPorChavePrimaria(tipoUsoMdtoAtOld.getSigla());

		Assert.assertEquals(tipoUsoMdtoAtOld, retorno);
	}

	@Test
	public void remover() throws BaseException {

//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedAfaTipoUsoMdtoDAO).remover(
//						with(any(AfaTipoUsoMdto.class)));
//				
//				oneOf(mockedAfaTipoUsoMdtoDAO).flush();
//
//				allowing(mockedTipoUsoMdtoRN).preDeleteTipoUsoMdto(
//						with(any(AfaTipoUsoMdto.class)));
//
//				allowing(mockedTipoUsoMdtoRN).posDeleteTipoUsoMdto(
//						with(any(AfaTipoUsoMdto.class)));
//			}
//		});
		
		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
				Integer.valueOf(1), DESCRICAO, DominioSituacao.A);
				
		tipoUsoMdtoIn = new AfaTipoUsoMdto("T1", grupoUsoMedicamento, null,
				DESCRICAO, true, true, true, true, Calendar.getInstance()
						.getTime(), DominioSituacao.A, true, true);

		systemUnderTest.remover(tipoUsoMdtoIn.getSigla());
	}

	@Test
	public void pesquisar() throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).pesquisar(
//						with(any(Integer.class)), with(any(Integer.class)),
//						with(any(String.class)), with(any(Boolean.class)),
//						with(any(AfaTipoUsoMdto.class)));
//				will(returnValue(Collections.singletonList(tipoUsoMdtoIn)));
//			}
//		});
		
		Mockito.when(
				mockedAfaTipoUsoMdtoDAO.pesquisar(
						Mockito.anyInt(), 
						Mockito.anyInt(), 
						Mockito.anyString(), 
						Mockito.anyBoolean(), 
						Mockito.any(AfaTipoUsoMdto.class)))
				.thenReturn(Collections.singletonList(tipoUsoMdtoIn));

		List<AfaTipoUsoMdto> result = systemUnderTest.pesquisar(1, 10, "sigla",
				true, tipoUsoMdtoAt);

		Assert.assertEquals(Collections.singletonList(tipoUsoMdtoIn), result);

	}

	@Test
	public void pesquisarCount() throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAfaTipoUsoMdtoDAO).pesquisarCount(
//						with(any(AfaTipoUsoMdto.class)));
//				will(returnValue(Integer.valueOf(1)));
//			}
//		});

		Mockito.when(mockedAfaTipoUsoMdtoDAO.pesquisarCount(Mockito.any(AfaTipoUsoMdto.class))).thenReturn(Long.valueOf(1));
		
		Long result = systemUnderTest.pesquisarCount(tipoUsoMdtoAt);

		Assert.assertEquals(Long.valueOf(1), result);
	}
}
