package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class ListarCirurgiasCanceladasONTest extends AGHUBaseUnitTest<ListarCirurgiasCanceladasON> {

	@Mock
	private MbcCirurgiasDAO mockMbcCirurgiasDAO;
	@Mock
	private ListaCirurgiasCanceladasRN mockListaCirurgiasCanceladasRN;
	@Mock
	private IAghuFacade mockAghuFacade;
	@Mock
	private IInternacaoFacade mockInternacaoFacade;

//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//
//		mockMbcCirurgiasDAO = mockingContext.mock(MbcCirurgiasDAO.class);
//		mockListaCirurgiasCanceladasRN = mockingContext.mock(ListaCirurgiasCanceladasRN.class);
//		mockAghuFacade = mockingContext.mock(IAghuFacade.class);
//		mockInternacaoFacade = mockingContext.mock(IInternacaoFacade.class);
//
//		systemUnderTest = new ListarCirurgiasCanceladasON(){
//
//			private static final long serialVersionUID = 1L;
//
//			protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
//				return mockMbcCirurgiasDAO;
//			};
//			
//			protected ListaCirurgiasCanceladasRN getListaCirurgiasCanceladasRN() {
//				return mockListaCirurgiasCanceladasRN;
//			};
//			
//			protected IAghuFacade getAghuFacade() {
//				return mockAghuFacade;
//			};
//			
//			protected IInternacaoFacade getInternacaoFacade() {
//				return mockInternacaoFacade;
//			};
//		};
//	}

	@Test
	public void testListarCirurgiasCanceladas() throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {{
//			oneOf(mockMbcCirurgiasDAO).pesquisarCirurgiasCanceladas(with(any(PortalPesquisaCirurgiasParametrosVO.class)));
//			will(returnValue(getListaCirurgiasCanceladasVO()));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockListaCirurgiasCanceladasRN).pesquisarMbcProcEspPorCirurgiasByCirurgia(with(any(Integer.class))); 
//			will(returnValue(getMbcProcedimentoCirurgicos()));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockListaCirurgiasCanceladasRN).pesquisarEquipeporCirurgia(with(any(Integer.class))); 
//			will(returnValue(with(any(String.class))));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockAghuFacade).obterAghCaractUnidFuncionaisPorChavePrimaria
//				(new AghCaractUnidFuncionaisId(with(any(Short.class)),
//					ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS)); 
//					will(returnValue(getCaracteristica()));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockInternacaoFacade).obterDescricaoConvenioPlano
//				(with(any(Byte.class)), with(any(Short.class))); 
//					will(returnValue("SUS"));
//		}});
			
		Assert.assertNotNull(systemUnderTest.listarCirurgiasCanceladas("especialidade.sigla", true,
				new PortalPesquisaCirurgiasParametrosVO()));
	}
	
	private List<CirurgiasCanceladasVO> getListaCirurgiasCanceladasVO() {
		List<CirurgiasCanceladasVO> list = new ArrayList<CirurgiasCanceladasVO>();
		CirurgiasCanceladasVO vo = new CirurgiasCanceladasVO();
		
		vo.setOrigemIntLocal("A");
		
		list.add(vo);
		
		return list;
	};
	
	private MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicos() {
		MbcProcedimentoCirurgicos proced = new MbcProcedimentoCirurgicos();
		
		proced.setSeq(1);
		proced.setDescricao("procedimento cirurgico");
		
		return proced;
	};
	
	private AghCaractUnidFuncionais getCaracteristica() {
		AghCaractUnidFuncionais caract = new AghCaractUnidFuncionais();
		
		AghUnidadesFuncionais unid = new AghUnidadesFuncionais();
		
		unid.setSigla("CPO");
		
		caract.setUnidadeFuncional(unid);
		
		return caract;
	};
}
