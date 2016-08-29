package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoNotaSalaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMaterialImpNotaSalaUnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcUnidadeNotaSalaDAO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoLateralidadeProcCirurgicoVO;
import br.gov.mec.aghu.dominio.DominioLadoCirurgiaAgendas;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class RelatorioNotasDeConsumoDaSalaONTest extends AGHUBaseUnitTest<RelatorioNotasDeConsumoDaSalaON> {
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private MbcSolicHemoCirgAgendadaDAO mockedMbcSolicHemoCirgAgendadaDAO;
	@Mock
	private MbcAnestesiaCirurgiasDAO mockedMbcAnestesiaCirurgiasDAO;
	@Mock
	private MbcProfCirurgiasDAO mockedMbcProfCirurgiasDAO;
	@Mock
	private MbcProcEspPorCirurgiasDAO mockedMbcProcEspPorCirurgiasDAO;
	@Mock
	private MbcUnidadeNotaSalaDAO mockedMbcUnidadeNotaSalaDAO;
	@Mock
	private MbcEquipamentoNotaSalaDAO mockedMbcEquipamentoNotaSalaDAO;
	@Mock
	private MbcMaterialImpNotaSalaUnDAO mockedMbcMaterialImpNotaSalaUnDAO;
	
//	@Before
//	 public void doBeforeEachTestCase() {
//		
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//	
//		mockedParametroFacade = mockingContext.mock(IParametroFacade.class);
//		mockedMbcSolicHemoCirgAgendadaDAO = mockingContext.mock(MbcSolicHemoCirgAgendadaDAO.class);
//		mockedMbcAnestesiaCirurgiasDAO = mockingContext.mock(MbcAnestesiaCirurgiasDAO.class);
//		mockedMbcProfCirurgiasDAO = mockingContext.mock(MbcProfCirurgiasDAO.class);
//		mockedMbcProcEspPorCirurgiasDAO = mockingContext.mock(MbcProcEspPorCirurgiasDAO.class);
//		mockedMbcUnidadeNotaSalaDAO = mockingContext.mock(MbcUnidadeNotaSalaDAO.class);
//		mockedMbcEquipamentoNotaSalaDAO = mockingContext.mock(MbcEquipamentoNotaSalaDAO.class);
//		mockedMbcMaterialImpNotaSalaUnDAO = mockingContext.mock(MbcMaterialImpNotaSalaUnDAO.class);
//		
//		systemUnderTest = new RelatorioNotasDeConsumoDaSalaON() {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -1459984343857703647L;
//
//			@Override
//			protected IParametroFacade getParametroFacade() {
//				return mockedParametroFacade;
//			}
//			
//			@Override
//			protected MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO() {
//				return mockedMbcSolicHemoCirgAgendadaDAO;
//			}
//			
//			@Override
//			protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
//				return mockedMbcAnestesiaCirurgiasDAO;
//			}
//			
//			@Override
//			protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
//				return mockedMbcProfCirurgiasDAO;
//			}
//			
//			@Override
//			protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
//				return mockedMbcProcEspPorCirurgiasDAO;
//			}
//			
//			@Override
//			protected MbcUnidadeNotaSalaDAO getMbcUnidadeNotaSalaDAO() {
//				return mockedMbcUnidadeNotaSalaDAO;
//			}
//			
//			@Override
//			protected MbcEquipamentoNotaSalaDAO getMbcEquipamentoNotaSalaDAO() {
//				return mockedMbcEquipamentoNotaSalaDAO;
//			}
//			
//			@Override
//			protected MbcMaterialImpNotaSalaUnDAO getMbcMaterialImpNotaSalaUnDAO() {
//				return mockedMbcMaterialImpNotaSalaUnDAO;
//			}
//			
//			@Override
//			protected Object obterContextoSessao(String var) {
//				return null;
//			}
//			
//		};
//	
//    }
	
	@Test
	public void testPreencherCnvCodDescricao() {
		final FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setCodigo(Short.parseShort("5"));
		convenioSaude.setDescricao("TESTE");
		final MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setConvenioSaude(convenioSaude);
		
		String retorno = systemUnderTest.preencherCnvCodDescricao(cirurgia);
		Assert.assertEquals("005 - TESTE", retorno);
	}
	
	@Test
	public void testPreencherListPciDescricao() {
		final DescricaoLateralidadeProcCirurgicoVO vo1 = new DescricaoLateralidadeProcCirurgicoVO();
		vo1.setProcSeq(23);
		vo1.setDescricaoProcedimento("PROC COM LATERAL");
		vo1.setLadoCirurgia(DominioLadoCirurgiaAgendas.B);
		
		final DescricaoLateralidadeProcCirurgicoVO vo2 = new DescricaoLateralidadeProcCirurgicoVO();
		vo2.setProcSeq(25);
		vo2.setDescricaoProcedimento("PROC SEM LATERAL");
		
		final List<DescricaoLateralidadeProcCirurgicoVO> lista = new ArrayList<DescricaoLateralidadeProcCirurgicoVO>();
		lista.add(vo1);
		lista.add(vo2);
		
//		mockingContext.checking(new Expectations() {{
//			oneOf(mockedMbcProcEspPorCirurgiasDAO).listarProcedimentosComLateralidadePorCrgSeq(with(any(Integer.class)));
//			will(returnValue(lista));
//		}});
		
		String retorno = systemUnderTest.preencherListPciDescricao(1);
		Assert.assertEquals("00023 - PROC COM LATERAL - Bilateral\n" +
					 "00025 - PROC SEM LATERAL", retorno);
	}
	
	@Test
	public void testPreencherAnestesiaDescricao() {
		MbcTipoAnestesias tipoAnestesia = new MbcTipoAnestesias();
		tipoAnestesia.setDescricao("ANESTESIA");
		MbcAnestesiaCirurgias anestesia = new MbcAnestesiaCirurgias();
		anestesia.setMbcTipoAnestesias(tipoAnestesia);
		
		final List<MbcAnestesiaCirurgias> lista = new ArrayList<MbcAnestesiaCirurgias>();
		lista.add(anestesia);
		
//		mockingContext.checking(new Expectations() {{
//			oneOf(mockedMbcAnestesiaCirurgiasDAO).listarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(with(any(Integer.class)));
//			will(returnValue(lista));
//		}});
		
		String retorno = systemUnderTest.preencherAnestesiaDescricao(1);
		Assert.assertEquals("ANESTESIA", retorno);
	}
	
}
