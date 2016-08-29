package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.business.MbcAgendasRN.MbcAgendasRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaProcedimentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirgPorUnidDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoPorGrupoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcAgendasRNTest extends AGHUBaseUnitTest<MbcAgendasRN> {

	@Mock
	private MbcAgendaProcedimentoDAO mockedMbcAgendaProcedimentoDAO;
	@Mock
	private MbcProcEspPorCirurgiasDAO mockedMbcProcEspPorCirurgiasDAO;
	@Mock
	private MbcEquipamentoCirgPorUnidDAO mockedMbcEquipamentoCirgPorUnidDAO;
	@Mock
	private IAghuFacade mockedIAghuFacade;
	@Mock
	private MbcAgendasDAO mockedMbcAgendasDAO;
	@Mock
	private MbcHorarioTurnoCirgDAO mockedMbcHorarioTurnoCirgDAO;
	@Mock
	private MbcControleEscalaCirurgicaDAO mockedMbcControleEscalaCirurgicaDAO;
	@Mock
	private IParametroFacade mockedIParametroFacade;
	@Mock
	private IFaturamentoFacade mockedIFaturamentoFacade;
	@Mock
	private MbcProcedimentoPorGrupoDAO mockedMbcProcedimentoPorGrupoDAO;
	@Mock
	private MbcAgendasParte1RN mockedMbcAgendasParte1RN;

//	@Before
//	public void doBeforeEachTestCase() {
//
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//		
//		//mockedMbcAgendaProcedimentoDAO = mockingContext.mock(MbcAgendaProcedimentoDAO.class);
//		mockedMbcControleEscalaCirurgicaDAO = mockingContext.mock(MbcControleEscalaCirurgicaDAO.class);
//		mockedIAghuFacade = mockingContext.mock(IAghuFacade.class);
//		mockedMbcAgendasDAO = mockingContext.mock(MbcAgendasDAO.class);
//		
//		systemUnderTest = new MbcAgendasRN() {
//			
//			private static final long serialVersionUID = -7121795075476700685L;
//
//			@Override
//			public MbcAgendaProcedimentoDAO getMbcAgendaProcedimentoDAO(){
//				return mockedMbcAgendaProcedimentoDAO;
//			}
//			@Override
//			protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
//				return mockedMbcProcEspPorCirurgiasDAO;
//			}
//			@Override
//			protected MbcEquipamentoCirgPorUnidDAO getMbcEquipamentoCirgPorUnidDAO() {
//				return mockedMbcEquipamentoCirgPorUnidDAO;
//			}
//			@Override
//			protected IAghuFacade getAghuFacade() {
//				return mockedIAghuFacade;
//			}
//			@Override
//			protected MbcAgendasDAO getMbcAgendasDAO(){
//				return mockedMbcAgendasDAO;
//			}
//			@Override
//			protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO(){
//				return mockedMbcHorarioTurnoCirgDAO;
//			}
//			@Override
//			protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO(){
//				return mockedMbcControleEscalaCirurgicaDAO;
//			}
//			@Override
//			protected IParametroFacade getParametroFacade() {
//				return mockedIParametroFacade;
//			}
//			@Override
//			protected IFaturamentoFacade getFaturamentoFacade() {
//				return mockedIFaturamentoFacade;
//			}
//			@Override
//			protected MbcProcedimentoPorGrupoDAO getMbcProcedimentoPorGrupoDAO() {
//				return mockedMbcProcedimentoPorGrupoDAO;
//			}
//			@Override
//			public MbcAgendasParte1RN getAgendasParte1RN() {
//				return mockedMbcAgendasParte1RN;
//			}
//			
//			@Override
//			public Boolean obterAtributoConsisteHorario() {
//				return true;
//			}
//		};
//	}

	/**
	 * SUCESSO
	 */
	@Test
	public void preAtualizarAgendaParte1Test() {
		MbcAgendas agenda = new MbcAgendas();
		MbcAgendas agendaOriginal = new MbcAgendas();
		
		agenda.setDtAgenda(new Date());
		agendaOriginal.setDtAgenda(agenda.getDtAgenda());
		agenda.setUnidadeFuncional(new AghUnidadesFuncionais(new Short("1")));
		agendaOriginal.setUnidadeFuncional(agenda.getUnidadeFuncional());
		agenda.setIndSituacao(DominioSituacaoAgendas.AG);

		/*try {
			systemUnderTest.preAtualizarAgendaParte1(agenda, agendaOriginal);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}*/
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarUnidadeCirurgica001Test(){
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		Boolean indGeradoSistema = Boolean.FALSE;
		
		unidadeFuncional.setIndSitUnidFunc(DominioSituacao.A);
		
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedIAghuFacade).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)),with(any(ConstanteAghCaractUnidFuncionais.class)));
//					will(returnValue(Boolean.TRUE));
//				}
//			});
				
			systemUnderTest.verificarUnidadeCirurgica(unidadeFuncional, indGeradoSistema);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00840
	 */
	@Test
	public void verificarUnidadeCirurgica002Test(){
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		Boolean indGeradoSistema = Boolean.FALSE;
		
		unidadeFuncional.setIndSitUnidFunc(DominioSituacao.A);
		
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedIAghuFacade).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)),with(any(ConstanteAghCaractUnidFuncionais.class)));
//					will(returnValue(Boolean.FALSE));
//				}
//			});
				
			systemUnderTest.verificarUnidadeCirurgica(unidadeFuncional, indGeradoSistema);
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00840));
		}
	}
	
	/**
	 * MBC_00841
	 */
	@Test
	public void verificarUnidadeCirurgica003Test(){
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		Boolean indGeradoSistema = Boolean.FALSE;
		
		unidadeFuncional.setIndSitUnidFunc(DominioSituacao.I);
		
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedIAghuFacade).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)),with(any(ConstanteAghCaractUnidFuncionais.class)));
//					will(returnValue(Boolean.TRUE));
//				}
//			});
				
			systemUnderTest.verificarUnidadeCirurgica(unidadeFuncional, indGeradoSistema);
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00841));
		}
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarDataLimite001Test(){
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		Date dtAgenda = new Date();
		Boolean indGeradoSistema = Boolean.FALSE;
		
		unidadeFuncional.setIndSitUnidFunc(DominioSituacao.A);
		unidadeFuncional.setQtdDiasLimiteCirg(new Short("1"));
		
		try {				
			systemUnderTest.verificarDataLimite(unidadeFuncional, dtAgenda, indGeradoSistema);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00844
	 */
	@Test
	public void verificarDataLimite002Test(){
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		Date dtAgenda = new Date();
		Boolean indGeradoSistema = Boolean.FALSE;
		
		unidadeFuncional.setIndSitUnidFunc(DominioSituacao.I);
		unidadeFuncional.setQtdDiasLimiteCirg(new Short("1"));
		
		try {
			systemUnderTest.verificarDataLimite(unidadeFuncional, dtAgenda, indGeradoSistema);
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00844));
		}
	}
	
	/**
	 * MBC_00776
	 */
	@Test
	public void verificarDataLimite003Test(){
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		Date dtAgenda = new Date();
		Boolean indGeradoSistema = Boolean.FALSE;
		
		dtAgenda = DateUtils.addDays(dtAgenda, 2);
		unidadeFuncional.setIndSitUnidFunc(DominioSituacao.A);
		unidadeFuncional.setQtdDiasLimiteCirg(new Short("1"));
		
		try {
			systemUnderTest.verificarDataLimite(unidadeFuncional, dtAgenda, indGeradoSistema);
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00776));
		}
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarDatasCirurgiaEletivaTest1() {
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais(Short.valueOf("2"));
		
		try {
			systemUnderTest.verificarDatasCirurgiaEletiva(unf, new Date(), Boolean.TRUE);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00777
	 */
	@Test
	public void verificarDatasCirurgiaEletivaTest2() {
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais(Short.valueOf("2"));
		
		Calendar dataAgenda = Calendar.getInstance();
		dataAgenda.set(2010, 10, 27);
		
		try {
			systemUnderTest.verificarDatasCirurgiaEletiva(unf, dataAgenda.getTime(), Boolean.FALSE);
			Assert.fail("Não deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00777));
		}
	}
	
	/**
	 * MBC_00778
	 */
	@Test
	public void verificarDatasCirurgiaEletivaTest3() {
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais(Short.valueOf("2"));
		
		Calendar dataAgenda = Calendar.getInstance();
		dataAgenda.set(2025, 3, 20);
		
		try {
			systemUnderTest.verificarDatasCirurgiaEletiva(unf, dataAgenda.getTime(), Boolean.FALSE);
			Assert.fail("Não deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00778));
		}
	}
	
	/**
	 * MBC_00845
	 */
	@Test
	public void verificarDatasCirurgiaEletivaTest4() {
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais(Short.valueOf("2"));
		
		Calendar dataAgenda = Calendar.getInstance();
		dataAgenda.set(2025, 4, 20);
		
		try {
			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcControleEscalaCirurgicaDAO).obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(
//							(with(any(Short.class))), (with(any(Date.class))), (with(any(DominioTipoEscala.class))));
//					will(returnValue(new MbcControleEscalaCirurgica()));
//				}
//			});
			
			systemUnderTest.verificarDatasCirurgiaEletiva(unf, dataAgenda.getTime(), Boolean.FALSE);
			Assert.fail("Não deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00845));
		}
	}
	
	/**
	 * MBC_00779
	 */
	@Test
	public void verificarDatasCirurgiaEletivaTest5() {
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais(Short.valueOf("2"));
		
		Calendar dataAgenda = Calendar.getInstance();
		dataAgenda.set(2025, 4, 20);
		
		try {
			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcControleEscalaCirurgicaDAO).obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(
//							(with(any(Short.class))), (with(any(Date.class))), (with(any(DominioTipoEscala.class))));
//					will(returnValue(null));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedIAghuFacade).obterFeriadoSemTurnoDataTruncada(
//							(with(any(Date.class))));
//					will(returnValue(new AghFeriados()));
//				}
//			});
			
			systemUnderTest.verificarDatasCirurgiaEletiva(unf, dataAgenda.getTime(), Boolean.FALSE);
			Assert.fail("Não deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00779));
		}
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarColisaoHorarioCirurgicoTest1() {
		MbcAgendas agenda = new MbcAgendas();
		agenda.setIndGeradoSistema(Boolean.TRUE);
		
		try {
			systemUnderTest.verificarColisaoHorarioCirurgico(agenda);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * Hora inicio da agenda 9:30
	 * Hora Fim agenda 10:30
	 * Uma lista com um registro 
	 * Hora inicio do registro 9:20
	 * Hora fim do registro 11:30
	 * Resultado esperado erro pois da colisao de horario
	 * MBC_00846
	 */
	@Test
	public void verificarColisaoHorarioCirurgicoTest2() {
		MbcSalaCirurgicaId salaId = new MbcSalaCirurgicaId();
		salaId.setSeqp(Short.valueOf("2"));
		salaId.setUnfSeq(Short.valueOf("33"));
		MbcSalaCirurgica salaCirg = new MbcSalaCirurgica();
		salaCirg.setId(salaId);
		
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais(Short.valueOf("2"));
		
		MbcAgendas agenda = new MbcAgendas();
		agenda.setIndGeradoSistema(Boolean.FALSE);
		agenda.setSalaCirurgica(salaCirg);
		
		Calendar inicio = Calendar.getInstance();
		inicio.set(2004, 0, 26, 8, 30);
		agenda.setDthrPrevInicio(inicio.getTime());
		
		Calendar fim = Calendar.getInstance();
		fim.set(2004, 0, 26, 9, 30);
		agenda.setDthrPrevFim(fim.getTime());
		
		agenda.setDtAgenda(new Date());
		agenda.setUnidadeFuncional(unf);
		agenda.setSeq(7);
		
		try {
			
//			mockingContext.checking(new Expectations() {
//				{
//					MbcAgendas agenda1 = new MbcAgendas();
//					
//					Calendar prevInicio = Calendar.getInstance();
//					prevInicio.set(2004, 0, 26, 9, 20);
//					agenda1.setDthrPrevInicio(prevInicio.getTime());
//					
//					Calendar prevFim = Calendar.getInstance();
//					prevFim.set(2004, 0, 26, 10, 30);
//					agenda1.setDthrPrevFim(prevFim.getTime());
//					
//					agenda1.setIntervaloEscala(Byte.valueOf("30"));
//					
//					List<MbcAgendas> listaAgendas = new ArrayList<MbcAgendas>();
//					listaAgendas.add(agenda1);
//					
//					oneOf(mockedMbcAgendasDAO).buscarHorariosOcupacaoSalaCirurgica(
//							(with(any(Date.class))), (with(any(Short.class))), (with(any(Short.class))),
//							(with(any(Short.class))), (with(any(Integer.class))));
//					will(returnValue(listaAgendas));
//				}
//			});
			
			systemUnderTest.verificarColisaoHorarioCirurgico(agenda);
			Assert.fail("Não deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00846));
		}
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarColisaoHorarioCirurgicoTest3() {
		MbcSalaCirurgicaId salaId = new MbcSalaCirurgicaId();
		salaId.setSeqp(Short.valueOf("2"));
		salaId.setUnfSeq(Short.valueOf("33"));
		MbcSalaCirurgica salaCirg = new MbcSalaCirurgica();
		salaCirg.setId(salaId);
		
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais(Short.valueOf("2"));
		
		MbcAgendas agenda = new MbcAgendas();
		agenda.setIndGeradoSistema(Boolean.FALSE);
		agenda.setSalaCirurgica(salaCirg);
		
		GregorianCalendar inicio = new GregorianCalendar();
		inicio.set(2004, 0, 26, 8, 30);
		agenda.setDthrPrevInicio(inicio.getTime());
		
		GregorianCalendar fim = new GregorianCalendar();
		fim.set(2004, 0, 26, 10, 30);
		agenda.setDthrPrevFim(fim.getTime());
		
		agenda.setDtAgenda(new Date());
		agenda.setUnidadeFuncional(unf);
		agenda.setSeq(7);
		
		try {
			
//			mockingContext.checking(new Expectations() {
//				{
//					MbcAgendas agenda1 = new MbcAgendas();
//					
//					GregorianCalendar prevInicio = new GregorianCalendar();
//					prevInicio.set(2004, 0, 26, 5, 30);
//					agenda1.setDthrPrevInicio(prevInicio.getTime());
//					
//					GregorianCalendar prevFim = new GregorianCalendar();
//					prevFim.set(2004, 0, 26, 7, 59);
//					agenda1.setDthrPrevFim(prevFim.getTime());
//					
//					agenda1.setIntervaloEscala(Byte.valueOf("30"));
//					
//					List<MbcAgendas> listaAgendas = new ArrayList<MbcAgendas>();
//					listaAgendas.add(agenda1);
//					
//					oneOf(mockedMbcAgendasDAO).buscarHorariosOcupacaoSalaCirurgica(
//							(with(any(Date.class))), (with(any(Short.class))), (with(any(Short.class))),
//							(with(any(Short.class))), (with(any(Integer.class))));
//					will(returnValue(listaAgendas));
//				}
//			});
			
			systemUnderTest.verificarColisaoHorarioCirurgico(agenda);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarDatasAgendaPacienteTest1() {
		try {
			systemUnderTest.verificarDatasAgendaPaciente(2, new Date(), 1, Boolean.TRUE);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00884
	 */
	@Test
	public void verificarDatasAgendaPacienteTest2() {
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcAgendasDAO).verificarExistenciaPacienteAgendadoNaData(
//							(with(any(Integer.class))), (with(any(Date.class))), (with(any(Integer.class))));
//					will(returnValue(true));
//				}
//			});
			systemUnderTest.verificarDatasAgendaPaciente(2, new Date(), 1, Boolean.FALSE);
			Assert.fail("Não deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendasRNExceptionCode.MBC_00884));
		}
	}
}