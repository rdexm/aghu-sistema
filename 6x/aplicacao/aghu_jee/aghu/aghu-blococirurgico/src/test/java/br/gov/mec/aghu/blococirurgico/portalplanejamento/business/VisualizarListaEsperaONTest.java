package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEspecialidadeProcCirgsDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.ListaEsperaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class VisualizarListaEsperaONTest extends AGHUBaseUnitTest<VisualizarListaEsperaON> {

	private IPesquisaInternacaoFacade mockPesquisaInternacaoFacade;
	private MbcAgendasDAO mockMbcAgendasDAO;
	private MbcEspecialidadeProcCirgsDAO mockMbcEspecialidadeProcCirgsDAO;

//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//
//		mockPesquisaInternacaoFacade = mockingContext.mock(IPesquisaInternacaoFacade.class);
//		mockMbcAgendasDAO = mockingContext.mock(MbcAgendasDAO.class);
//		mockMbcEspecialidadeProcCirgsDAO = mockingContext.mock(MbcEspecialidadeProcCirgsDAO.class);
//
//		systemUnderTest = new VisualizarListaEsperaON() {
//
//			private static final long serialVersionUID = 1L;
//
//			protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
//				return mockPesquisaInternacaoFacade;
//			}
//			
//			protected MbcAgendasDAO getMbcAgendasDAO() {
//				return mockMbcAgendasDAO;
//			}
//			
//			protected MbcEspecialidadeProcCirgsDAO getMbcEspecialidadeProcCirgsDAO() {
//				return mockMbcEspecialidadeProcCirgsDAO;
//			}
//		};
//	}
	
	@Test
	public void testRecuperarListaPaginadaVazia() {

		Integer firstResult = 0; 
		Integer maxResult  = 10;
		String orderProperty = null;
		Boolean asc  = Boolean.TRUE;
		PortalPesquisaCirurgiasParametrosVO parametros = new PortalPesquisaCirurgiasParametrosVO();
		
		esperarListarAgendasListaEsperaVazia();
		try {
			List<ListaEsperaVO> lista = systemUnderTest.recuperarListaPaginada(firstResult, maxResult, orderProperty, asc, parametros);
			Assert.assertTrue(lista.isEmpty());
		} catch (ApplicationBusinessException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testRecuperarListaPaginadaTam10() {

		Integer firstResult = 0; 
		Integer maxResult  = 10;
		String orderProperty = ListaEsperaVO.Fields.PES_NOME_USUAL.toString();
		Boolean asc  = Boolean.TRUE;
		PortalPesquisaCirurgiasParametrosVO parametros = new PortalPesquisaCirurgiasParametrosVO();

		esperarNomeUsual();
		esperarVpeDescricao();
		esperarListarAgendasListaEsperaSemPaginacao(20);
		try {
			List<ListaEsperaVO> lista = systemUnderTest.recuperarListaPaginada(firstResult, maxResult, orderProperty, asc, parametros);
			Boolean tamanhoValido = lista.size() == maxResult;
			Assert.assertTrue(tamanhoValido);
		} catch (ApplicationBusinessException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testRecuperarListaPaginadaTam5() {

		Integer firstResult = 0; 
		Integer maxResult  = 10;
		String orderProperty = ListaEsperaVO.Fields.PES_NOME_USUAL.toString();
		Boolean asc  = Boolean.TRUE;
		PortalPesquisaCirurgiasParametrosVO parametros = new PortalPesquisaCirurgiasParametrosVO();

		esperarNomeUsual();
		esperarVpeDescricao();
		esperarListarAgendasListaEsperaSemPaginacao(9);
		try {
			List<ListaEsperaVO> lista = systemUnderTest.recuperarListaPaginada(firstResult, maxResult, orderProperty, asc, parametros);
			Boolean tamanhoValido = lista.size() == 9;
			Assert.assertTrue(tamanhoValido);
		} catch (ApplicationBusinessException e) {
			Assert.fail();
		}
	}
	
	private void esperarListarAgendasListaEsperaSemPaginacao(final int tamanho) {
//		mockingContext.checking(new Expectations() {{
//			oneOf(mockMbcAgendasDAO).listarAgendasListaEspera(
//					with(any(PortalPesquisaCirurgiasParametrosVO.class)));
//		will(returnValue(processarListaMbcAgenda(tamanho)));
//		}

//		private List<MbcAgendas> processarListaMbcAgenda(Integer tamanho) {
//			
//			List<MbcAgendas> lista = new ArrayList<MbcAgendas>();
//			for(int i = 0; i< tamanho; i++){
//				MbcAgendas a = new MbcAgendas();
//				MbcProfAtuaUnidCirgs prof = new MbcProfAtuaUnidCirgs();
//				MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId();
//				prof.setId(id);
//				a.setProfAtuaUnidCirgs(prof);
//				lista.add(a);
//			}
//			return lista;
//		}});
		
	}

	private void esperarListarAgendasListaEsperaVazia() {
		
//		mockingContext.checking(new Expectations() {{
//				oneOf(mockMbcAgendasDAO).listarAgendasListaEspera(
//						with(any(Integer.class)), 
//						with(any(Integer.class)), 
//						with(any(String.class)), 
//						with(any(Boolean.class)), 
//						with(any(PortalPesquisaCirurgiasParametrosVO.class)));
//		will(returnValue(new ArrayList<MbcAgendas>()));
//		}});
		
	}
	
	private void esperarNomeUsual() {
//		mockingContext.checking(new Expectations() {{
//			allowing(mockPesquisaInternacaoFacade).buscarNomeUsual(
//					with(any(Short.class)), 
//					with(any(Integer.class)));
//			will(returnValue("                "));
//	}});

	}

//	vo.setVpeDescricao(getMbcEspecialidadeProcCirgsDAO().getVpeDescricao(agenda.getEspProcCirgs()));

	private void esperarVpeDescricao() {
//		mockingContext.checking(new Expectations() {{
//			allowing(mockMbcEspecialidadeProcCirgsDAO).getVpeDescricao(with(any(MbcEspecialidadeProcCirgs.class)));
//			will(returnValue("   "));
//		}});
	}
	
}
