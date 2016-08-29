package br.gov.mec.aghu.blococirurgico.business;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDestinoPacienteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class RelatorioPacientesEmSalaRecuperacaoPorUnidadeONTest extends AGHUBaseUnitTest<RelatorioPacientesEmSalaRecuperacaoPorUnidadeON>{

	private MbcProfCirurgiasDAO mockedMbcProfCirurgiasDAO;
	private MbcDestinoPacienteDAO mockedMbcDestinoPacienteDAO;
	private MbcCirurgiasDAO mockedMbcCirurgiasDAO;
	private IParametroFacade mockedParametroFacade;
	private IPesquisaInternacaoFacade mockedPesquisaInternacaoFacade;
	private EscalaCirurgiasON mockedEscalaCirurgiasON;
	
	private IAmbulatorioFacade mockedAmbulatorioFacade;

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
//		mockedMbcProfCirurgiasDAO = mockingContext.mock(MbcProfCirurgiasDAO.class);
//		
//		mockedMbcDestinoPacienteDAO = mockingContext.mock(MbcDestinoPacienteDAO.class);
//		
//		mockedMbcCirurgiasDAO = mockingContext.mock(MbcCirurgiasDAO.class);
//		
//		mockedParametroFacade = mockingContext.mock(IParametroFacade.class);
//		
//		mockedEscalaCirurgiasON = mockingContext.mock(EscalaCirurgiasON.class);
//		
//		mockedAmbulatorioFacade = mockingContext.mock(IAmbulatorioFacade.class);
//		
//		mockedPesquisaInternacaoFacade = mockingContext.mock(IPesquisaInternacaoFacade.class);
//
//		systemUnderTest = new RelatorioPacientesEmSalaRecuperacaoPorUnidadeON() {
//			
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -9096358957037254852L;
//			
//			protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
//				return mockedMbcProfCirurgiasDAO;
//			}
//			
//			protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
//				return mockedMbcCirurgiasDAO;
//			}
//			
//			protected MbcDestinoPacienteDAO getMbcDestinoPacienteDAO() {
//				return mockedMbcDestinoPacienteDAO;
//			}
//			
//			protected IParametroFacade getParametroFacade(){
//				return mockedParametroFacade;
//			}
//			
//			protected EscalaCirurgiasON getEscalaCirurgiasON(){
//				return mockedEscalaCirurgiasON;
//			}
//			
//			protected IAmbulatorioFacade getAmbulatorioFacade(){
//				return mockedAmbulatorioFacade;
//			}
//			
//			protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade(){
//				return mockedPesquisaInternacaoFacade;
//			}
//
//		};
//
//	}
	
	@Test
	public void listarPacientesEmSalaRecuperacaoPorUnidadeTeste() throws BaseException{
		Short seqUnidadeCirurgica = 131;
		String ordemListagem = "1";
	
//		mockingContext.checking(new Expectations() {{
//			allowing(mockedParametroFacade).buscarAghParametro(with(any(AghuParametrosEnum.class)));
//    		AghParametros aghParametros = new AghParametros();
//    		aghParametros.setVlrNumerico(new BigDecimal("13"));
//    		will(returnValue(aghParametros));    		
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockedPesquisaInternacaoFacade).verificarCaracteristicaDaUnidadeFuncional(with(any(Short.class)), with(any(ConstanteAghCaractUnidFuncionais.class)));    		
//    		will(returnValue(DominioSimNao.S));  		
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//    		allowing(mockedMbcDestinoPacienteDAO).obterOriginal(with(any(Byte.class)));
//    		MbcDestinoPaciente destPaciente = new MbcDestinoPaciente();
//    		destPaciente.setSeq(Byte.valueOf("13"));
//    		will(returnValue(destPaciente));
//    		
//    		List<MbcCirurgias> listPacSalaRecuperacao = new ArrayList<MbcCirurgias>();
//    		MbcCirurgias cirurgia = new MbcCirurgias();
//    		cirurgia.setSeq(561022);
//    		cirurgia.setUnidadeFuncional(new AghUnidadesFuncionais());
//    		cirurgia.getUnidadeFuncional().setDescricao("C.C.A. - CENTRO CIRÚRGICO"); 
//    		cirurgia.setData(new Date());
//    		
//    		allowing(mockedEscalaCirurgiasON).pesquisaQuarto(with(any(Integer.class)));
//    		will(returnValue("Q: 11"));
//    		
//    		cirurgia.setDataEntradaSr(new Date());
//    		processaSystemComNomeCirurgiao(systemUnderTest);
//    		cirurgia.setPaciente(new AipPacientes());
//    		cirurgia.getPaciente().setNome("ADRIANO HKLSPZK");
//    		cirurgia.getPaciente().setProntuario(123456);    		
//    		
//    		listPacSalaRecuperacao.add(cirurgia);
//    		final List<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO> listVo = new ArrayList<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO>();
//    		RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO vo = new RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO();
//    		vo.setUnidade("C.C.A. - CENTRO CIRÚRGICO"); 
//			vo.setDataHora(new Date());
//			
//			allowing(mockedEscalaCirurgiasON).pesquisaQuarto(with(any(Integer.class)));
//			will(returnValue("U:9 S"));
//			
//			vo.setQuarto("U:9 S");
//			vo.setDthrEntrada(cirurgia.getDataEntradaSr());
//			vo.setPacNome("ADRIANO HKLSPZK");
//			vo.setCirurgiao("ADRIANO RHSGFP");
//			
//			vo.setProntuario("123456");
//			vo.setUnidCti("S"); 			
//			vo.setTituloHeader("INFORMAÇÕES DO PACIENTE EM LEITO DE CTI");
//			vo.setNomeFooter("TOTAL DE PACIENTES EM CTI :");			
//			
//			listVo.add(vo);
//			
//    		allowing(mockedMbcCirurgiasDAO).listarPacientesSalaRecuperacaoPorUnidade(with(any(Short.class)),with(any(Byte.class)), with(any(BigDecimal.class)));
//    		will(returnValue(listPacSalaRecuperacao));   		
//		}});

    	try {
			systemUnderTest.listarPacientesEmSalaRecuperacaoPorUnidade(seqUnidadeCirurgica,ordemListagem);
			
			Assert.assertTrue(true);
		} catch (BaseException e) {
			//log.error(e.getMessage());
			Assert.assertFalse(true);
			
		}
	}
	

	protected void processaSystemComNomeCirurgiao(
			RelatorioPacientesEmSalaRecuperacaoPorUnidadeON systemUnderTest2) {
			systemUnderTest = new RelatorioPacientesEmSalaRecuperacaoPorUnidadeON() {
			
			private static final long serialVersionUID = -9096358957037254852L;
			
			protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
				return mockedMbcProfCirurgiasDAO;
			}
			
			protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
				return mockedMbcCirurgiasDAO;
			}
			
			protected MbcDestinoPacienteDAO getMbcDestinoPacienteDAO() {
				return mockedMbcDestinoPacienteDAO;
			}
			
			protected IParametroFacade getParametroFacade(){
				return mockedParametroFacade;
			}
			
			protected EscalaCirurgiasON getEscalaCirurgiasON(){
				return mockedEscalaCirurgiasON;
			}
			
			protected IAmbulatorioFacade getAmbulatorioFacade(){
				return mockedAmbulatorioFacade;
			}
			
			protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade(){
				return mockedPesquisaInternacaoFacade;
			}
			
			public String preencherNomeCirurgiao(Integer crgSeq) {
				return "";
			};

		};
		
	}

	@Test
	public void preencherNomeCirurgiaoTeste() {
		Integer crgSeq = 561022;
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedMbcProfCirurgiasDAO).listarMbcProfCirurgiasPorCrgSeqFuncaoProfissionalOrder(with(any(Integer.class)), with(any(DominioFuncaoProfissional[].class)),with(any(Boolean.class)));
//				List<MbcProfCirurgias> listProfCirurgias = new ArrayList<MbcProfCirurgias>();
//				MbcProfCirurgias profCirurgias = new MbcProfCirurgias();
//				profCirurgias.setServidorPuc(new RapServidores());
//				profCirurgias.getServidorPuc().setPessoaFisica(new RapPessoasFisicas());
//				profCirurgias.getServidorPuc().getPessoaFisica().setNomeUsual("ARMANDO SWKDQR");
//				listProfCirurgias.add(profCirurgias);
//				will(returnValue(listProfCirurgias));
//			}
//		});
//		
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAmbulatorioFacade).formataString(with(any(String.class)), with(any(Integer.class)));
//				MbcProfCirurgias profCirurgias = new MbcProfCirurgias();
//				profCirurgias.setServidorPuc(new RapServidores());
//				profCirurgias.getServidorPuc().setPessoaFisica(new RapPessoasFisicas());
//				profCirurgias.getServidorPuc().getPessoaFisica().setNome("ARMANDO SWKDQR TESTE TESTE");
//				will(returnValue(profCirurgias));
//			}
//		});
		Assert.assertEquals("ARMANDO SWKDQR", systemUnderTest.preencherNomeCirurgiao(crgSeq));
	}
}
