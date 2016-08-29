package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class FichaPreOperatoriaONTest extends AGHUBaseUnitTest<FichaPreOperatoriaON> {

	@Mock
	private MbcProcEspPorCirurgiasDAO mockedMbcProcEspPorCirurgiasDAO;
	@Mock
	private FichaPreOperatoriaRN mockedFichaPreOperatoriaRN;
	@Mock
	private EscalaCirurgiasON mockedEscalaCirurgiasON;
	@Mock
	private IBlocoCirurgicoCadastroApoioFacade mockedBlocoCirurgicoCadastroApoioFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;

//	@Before
//	public void doBeforeEachTestCase() {
//
//		// contexto dos mocks, usado para criar os mocks e definir a expectativa de chamada dos métodos.
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//		
//		mockedMbcProcEspPorCirurgiasDAO = mockingContext.mock(MbcProcEspPorCirurgiasDAO.class);
//		mockedFichaPreOperatoriaRN = mockingContext.mock(FichaPreOperatoriaRN.class);
//		mockedEscalaCirurgiasON = mockingContext.mock(EscalaCirurgiasON.class);
//		mockedBlocoCirurgicoCadastroApoioFacade = mockingContext.mock(IBlocoCirurgicoCadastroApoioFacade.class);
//		mockedAghuFacade = mockingContext.mock(IAghuFacade.class);
//		
//		systemUnderTest = new FichaPreOperatoriaON() {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
//				return mockedMbcProcEspPorCirurgiasDAO;
//			}
//			
//			protected FichaPreOperatoriaRN getFichaPreOperatoriaRN(){
//				return mockedFichaPreOperatoriaRN;
//			}
//			
//			protected EscalaCirurgiasON getEscalaCirurgiasON(){
//				return mockedEscalaCirurgiasON;
//			}
//			
//			protected IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade(){
//				return mockedBlocoCirurgicoCadastroApoioFacade;
//			}
//						
//			protected IAghuFacade getAghuFacade(){
//				return mockedAghuFacade;
//			}
//		};
//
//	}
	
	@Test
	public void listarProcedimentoPorCirurgiaTeste() throws BaseException{
		Short seqUnidadeCirurgica = 131;
		String unidadeCirurgica = "C.C.A. - CENTRO CIRÚRGICO AMBULATORIAL";
		Short seqUnidadeInternacao = 369;
		Date dataCirurgia = new Date();
		Integer prontuario = 13430087;
	
//		mockingContext.checking(new Expectations() {{
//    		
//    		List<MbcProcEspPorCirurgias> listProcedimentoPorCirurgia = new ArrayList<MbcProcEspPorCirurgias>();
//    		
//    		MbcProcEspPorCirurgias procedimentoPorCirurgia = new MbcProcEspPorCirurgias();
//
//			allowing(mockedFichaPreOperatoriaRN).mbccIdadeExt(with(any(Date.class)), with(any(Date.class)));
//			
//			allowing(mockedFichaPreOperatoriaRN).mbccGetunfDesc(with(any(Short.class)));
//			
//			allowing(mockedFichaPreOperatoriaRN).mbccGetTurno(with(any(Date.class)));
//			will(returnValue("TARDE"));
//    		
//    		allowing(mockedEscalaCirurgiasON).pesquisaQuarto(with(any(Integer.class)));
//    		will(returnValue("U:1 N"));
//
//    		procedimentoPorCirurgia.setCirurgia(new MbcCirurgias());
//    		procedimentoPorCirurgia.getCirurgia().setDataPrevisaoInicio(new Date());
//    		procedimentoPorCirurgia.getCirurgia().setNumeroAgenda((short) 202);
//    		procedimentoPorCirurgia.getCirurgia().setPaciente(new AipPacientes());
//    		procedimentoPorCirurgia.getCirurgia().getPaciente().setNome("SELVINO NEQBKOQ");
//    		procedimentoPorCirurgia.getCirurgia().getPaciente().setProntuario(13430087);
//    		procedimentoPorCirurgia.getCirurgia().setAtendimento(new AghAtendimentos());
//    		procedimentoPorCirurgia.getCirurgia().getAtendimento().setUnidadeFuncional(new AghUnidadesFuncionais());
//    		procedimentoPorCirurgia.getCirurgia().getAtendimento().getUnidadeFuncional().setSeq((short) 369);
//    		procedimentoPorCirurgia.setProcedimentoCirurgico(new MbcProcedimentoCirurgicos());
//    		procedimentoPorCirurgia.getProcedimentoCirurgico().setDescricao("CISTOSTOMIA");
//    		procedimentoPorCirurgia.getProcedimentoCirurgico().setSeq(29);
//    		
//    		listProcedimentoPorCirurgia.add(procedimentoPorCirurgia);
//
//    		List<FichaPreOperatoriaVO> listVo = new ArrayList<FichaPreOperatoriaVO>();
//    		
//    		FichaPreOperatoriaVO vo = new FichaPreOperatoriaVO();
//    		vo.setUnidadeCirurgica("C.C.A. - CENTRO CIRÚRGICO"); 
//    		vo.setDtCirurgia(new Date());
//    		vo.setNome("SELVINO NEQBKOQ");
//    		vo.setIdade("62 A 2 M");
//    		vo.setUnidade("01 Norte");
//    		vo.setLocal("U:1 N");
//    		vo.setNroAgenda((short) 202);
//    		vo.setDthrPrevInicio(new Date());
//    		//vo.setHrTricotomizar("14:00");
//    		
//    		allowing(mockedBlocoCirurgicoCadastroApoioFacade).buscarAreaTricPeloSeqProcedimento(with(any(Integer.class))); //
//    		
//    		MbcAreaTricProcCirg areaTricProcCirg = new MbcAreaTricProcCirg();
//    		MbcAreaTricotomia areaTrico = new MbcAreaTricotomia();
//    		areaTrico.setDescricao("Nao Tricotomizar");
//    		areaTricProcCirg.setMbcAreaTricotomia(areaTrico);
//    		will(returnValue(Arrays.asList(areaTricProcCirg)));
//    		
//			listVo.add(vo);
//			
//    		allowing(mockedMbcProcEspPorCirurgiasDAO).listarMbcProcEspPorCirurgiasPorDataCrgEUnidadeCrg(with(any(Short.class)), with(any(Short.class)),  with(any(Date.class)),  with(any(Integer.class)));
//    		will(returnValue(listProcedimentoPorCirurgia)); 
//    		
//    		allowing(mockedAghuFacade).pesquisarUnidadesFuncionaisPaciente(with(any(DominioPacAtendimento.class)), with(any(Integer.class)),  with(any(Integer.class)),  with(any(Boolean.class)));
//    		will(returnValue(Arrays.asList(new AghUnidadesFuncionais(Short.valueOf("123")))));   	
//    		
//		}});

    	try {
			systemUnderTest.listarProcedimentoPorCirurgia(seqUnidadeCirurgica, unidadeCirurgica, seqUnidadeInternacao, dataCirurgia, prontuario);
			
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}	
}
