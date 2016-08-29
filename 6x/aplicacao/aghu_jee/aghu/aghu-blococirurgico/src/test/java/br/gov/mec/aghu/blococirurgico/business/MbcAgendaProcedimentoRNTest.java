package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.business.MbcAgendaProcedimentoRN.MbcAgendaProcedimentoRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirgPorUnidDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoPorGrupoDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendaProcedimentoId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgsId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;

@Ignore
public class MbcAgendaProcedimentoRNTest extends AGHUBaseUnitTest<MbcAgendaProcedimentoRN> {

	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private MbcAgendasDAO mockedAgendasDAO;
	@Mock
	private MbcProcedimentoPorGrupoDAO mockMbcProcedimentoPorGrupoDAO;
	@Mock
	private MbcEquipamentoCirgPorUnidDAO mockMbcEquipamentoCirgPorUnidDAO;
	@Mock
	private MbcProcEspPorCirurgiasDAO mockMbcProcEspPorCirurgiasDAO;

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
//		mockedParametroFacade = mockingContext.mock(IParametroFacade.class);
//		mockedAgendasDAO = mockingContext.mock(MbcAgendasDAO.class);
//		mockMbcProcedimentoPorGrupoDAO = mockingContext.mock(MbcProcedimentoPorGrupoDAO.class);
//		mockMbcEquipamentoCirgPorUnidDAO = mockingContext.mock(MbcEquipamentoCirgPorUnidDAO.class);
//		mockMbcProcEspPorCirurgiasDAO = mockingContext.mock(MbcProcEspPorCirurgiasDAO.class);
//		
//		systemUnderTest = new MbcAgendaProcedimentoRN() {
//
//			private static final long serialVersionUID = 7100957165151032378L;
//
//			@Override
//			protected IParametroFacade getParametroFacade() {
//				return mockedParametroFacade;
//			}
//			
//			@Override
//			protected MbcAgendasDAO getMbcAgendasDAO() {
//				return mockedAgendasDAO;
//			}
//			
//			@Override
//			protected MbcProcedimentoPorGrupoDAO getMbcProcedimentoPorGrupoDAO() {
//				return mockMbcProcedimentoPorGrupoDAO;
//			}
//			
//			@Override
//			protected MbcEquipamentoCirgPorUnidDAO getMbcEquipamentoCirgPorUnidDAO() {
//				return mockMbcEquipamentoCirgPorUnidDAO;
//			}
//			
//			@Override
//			protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
//				return mockMbcProcEspPorCirurgiasDAO;
//			}
//
//		};
//	}

	/**
	 * SUCESSO
	 */
	@Test
	public void validarAgendaComControleEscalaCirurgicaDefinitivaTest1() {
		MbcAgendas agenda = new MbcAgendas();
		
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAgendasDAO).pesquisarAgendaComControleEscalaCirurgicaDefinitiva((with(any(Integer.class))));
//				will(returnValue(null));
//			}
//		});

		try {
			systemUnderTest.validarAgendaComControleEscalaCirurgicaDefinitiva(agenda);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00833
	 */
	@Test
	public void validarAgendaComControleEscalaCirurgicaDefinitivaTest2() {
		MbcAgendas agenda = new MbcAgendas();
		agenda.setSeq(1);
		
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					MbcAgendas resultado = new MbcAgendas();
//					resultado.setIndGeradoSistema(false);
//					oneOf(mockedAgendasDAO).pesquisarAgendaComControleEscalaCirurgicaDefinitiva((with(any(Integer.class))));
//					will(returnValue(resultado));
//				}
//			});
			
			systemUnderTest.validarAgendaComControleEscalaCirurgicaDefinitiva(agenda);
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_00833));
		}
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarDisponibilidapporemdeEquipamentosTest1() {
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					AghParametros grupoVideo = new AghParametros();
//					grupoVideo.setVlrNumerico(BigDecimal.ONE);
//					
//					oneOf(mockedParametroFacade).buscarAghParametro(with(AghuParametrosEnum.P_GRUPO_VIDEOLAPAROSCOPIA));
//					will(returnValue(grupoVideo));
//				}
//			});
			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockMbcProcedimentoPorGrupoDAO).listarGrupoSeqPorProcedimento(with(any(Integer.class)));
//					will(returnValue(new ArrayList<Short>()));
//				}
//			});
			
			
			systemUnderTest.verificarDisponibilideDeEquipamentos(1, null, 2, true, new Date(),
					null, null, null, "rn_agtp_ver_utlz_equ");
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00862
	 */
	@Test
	public void verificarDisponibilidapporemdeEquipamentosTest2() {
		Calendar dataIni = Calendar.getInstance();
		dataIni.set(2013, 3, 10, 12, 30);
		Calendar dataFim = Calendar.getInstance();
		dataFim.set(2013, 3, 10, 13, 0);
		
		final List<Short> gruposSeq = new ArrayList<Short>();
		gruposSeq.add(Short.valueOf("2"));
		gruposSeq.add(Short.valueOf("1"));
		
		final List<MbcAgendas> listaHorariosColisao = new ArrayList<MbcAgendas>();
		
		MbcAgendas colisao1 = new MbcAgendas();
		Calendar dataIni1 = Calendar.getInstance();
		dataIni1.set(2013, 3, 10, 8, 0);
		colisao1.setDthrPrevInicio(dataIni1.getTime());
		Calendar dataFim1 = Calendar.getInstance();
		dataFim1.set(2013, 3, 10, 9, 0);
		colisao1.setDthrPrevFim(dataFim1.getTime());
		
		MbcAgendas colisao2 = new MbcAgendas();
		Calendar dataIni2 = Calendar.getInstance();
		dataIni2.set(2013, 3, 10, 12, 10);
		colisao2.setDthrPrevInicio(dataIni2.getTime());
		Calendar dataFim2 = Calendar.getInstance();
		dataFim2.set(2013, 3, 10, 14, 50);
		colisao2.setDthrPrevFim(dataFim2.getTime());
		
		MbcAgendas colisao3 = new MbcAgendas();
		Calendar dataIni3 = Calendar.getInstance();
		dataIni3.set(2013, 3, 10, 12, 0);
		colisao3.setDthrPrevInicio(dataIni3.getTime());
		Calendar dataFim3 = Calendar.getInstance();
		dataFim3.set(2013, 3, 10, 13, 0);
		colisao3.setDthrPrevFim(dataFim3.getTime());
		
		MbcAgendas colisao4 = new MbcAgendas();
		Calendar dataIni4 = Calendar.getInstance();
		dataIni4.set(2013, 3, 10, 12, 0);
		colisao4.setDthrPrevInicio(dataIni4.getTime());
		Calendar dataFim4 = Calendar.getInstance();
		dataFim4.set(2013, 3, 10, 13, 0);
		colisao4.setDthrPrevFim(dataFim4.getTime());
		
		listaHorariosColisao.add(colisao1);
		listaHorariosColisao.add(colisao2);
		listaHorariosColisao.add(colisao3);
		listaHorariosColisao.add(colisao4);
		
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					AghParametros grupoVideo = new AghParametros();
//					grupoVideo.setVlrNumerico(BigDecimal.ONE);
//					
//					oneOf(mockedParametroFacade).buscarAghParametro(with(AghuParametrosEnum.P_GRUPO_VIDEOLAPAROSCOPIA));
//					will(returnValue(grupoVideo));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					AghParametros equipamentoVideo = new AghParametros();
//					equipamentoVideo.setVlrNumerico(BigDecimal.TEN);
//					
//					oneOf(mockedParametroFacade).buscarAghParametro(with(AghuParametrosEnum.P_EQUIPAMENTO_VIDEOLAP));
//					will(returnValue(equipamentoVideo));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockMbcProcedimentoPorGrupoDAO).listarGrupoSeqPorProcedimento(with(any(Integer.class)));
//					will(returnValue(gruposSeq));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockMbcEquipamentoCirgPorUnidDAO).obterQuantidadePorId(with(any(Short.class)), with(any(Short.class)));
//					will(returnValue(Short.valueOf("3")));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockMbcProcEspPorCirurgiasDAO).buscarQuantidadeCirurgiasComDetProc(
//							with(any(Integer.class)),
//							with(any(Short.class)),
//							with(any(Date.class)),
//							with(any(Short.class)));
//					will(returnValue(3));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedAgendasDAO).buscarAgendasComProcEquipColisaoHorario(
//							with(any(Date.class)),
//							with(any(Short.class)),
//							with(any(Integer.class)),
//							with(any(Short.class)));
//					will(returnValue(listaHorariosColisao));
//				}
//			});
			
			
			systemUnderTest.verificarDisponibilideDeEquipamentos(1, DominioSituacaoAgendas.ES, 55, false, dataIni.getTime(),
					dataFim.getTime(), Short.valueOf("131"), new Date(), "rn_agtp_ver_utlz_equ");
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_00862));
		}
	}
	
	/**
	 * MBC_00858
	 */
	@Test
	public void verificarDisponibilidapporemdeEquipamentosTest3() {
		Calendar dataIni = Calendar.getInstance();
		dataIni.set(2013, 3, 10, 12, 30);
		Calendar dataFim = Calendar.getInstance();
		dataFim.set(2013, 3, 10, 13, 0);
		
		final List<Short> gruposSeq = new ArrayList<Short>();
		gruposSeq.add(Short.valueOf("2"));
		gruposSeq.add(Short.valueOf("1"));
		
		final List<MbcAgendas> listaHorariosColisao = new ArrayList<MbcAgendas>();
		
		MbcAgendas colisao1 = new MbcAgendas();
		Calendar dataIni1 = Calendar.getInstance();
		dataIni1.set(2013, 3, 10, 8, 0);
		colisao1.setDthrPrevInicio(dataIni1.getTime());
		Calendar dataFim1 = Calendar.getInstance();
		dataFim1.set(2013, 3, 10, 9, 0);
		colisao1.setDthrPrevFim(dataFim1.getTime());
		
		MbcAgendas colisao2 = new MbcAgendas();
		Calendar dataIni2 = Calendar.getInstance();
		dataIni2.set(2013, 3, 10, 12, 10);
		colisao2.setDthrPrevInicio(dataIni2.getTime());
		Calendar dataFim2 = Calendar.getInstance();
		dataFim2.set(2013, 3, 10, 14, 50);
		colisao2.setDthrPrevFim(dataFim2.getTime());
		
		MbcAgendas colisao3 = new MbcAgendas();
		Calendar dataIni3 = Calendar.getInstance();
		dataIni3.set(2013, 3, 10, 12, 0);
		colisao3.setDthrPrevInicio(dataIni3.getTime());
		Calendar dataFim3 = Calendar.getInstance();
		dataFim3.set(2013, 3, 10, 13, 0);
		colisao3.setDthrPrevFim(dataFim3.getTime());
		
		MbcAgendas colisao4 = new MbcAgendas();
		Calendar dataIni4 = Calendar.getInstance();
		dataIni4.set(2013, 3, 10, 12, 0);
		colisao4.setDthrPrevInicio(dataIni4.getTime());
		Calendar dataFim4 = Calendar.getInstance();
		dataFim4.set(2013, 3, 10, 13, 0);
		colisao4.setDthrPrevFim(dataFim4.getTime());
		
		listaHorariosColisao.add(colisao1);
		listaHorariosColisao.add(colisao2);
		listaHorariosColisao.add(colisao3);
		listaHorariosColisao.add(colisao4);
		
		try {
//			mockingContext.checking(new Expectations() {
//				{
//					AghParametros grupoVideo = new AghParametros();
//					grupoVideo.setVlrNumerico(BigDecimal.ONE);
//					
//					oneOf(mockedParametroFacade).buscarAghParametro(with(AghuParametrosEnum.P_GRUPO_VIDEOLAPAROSCOPIA));
//					will(returnValue(grupoVideo));
//				}
//			});
			
//			mockingContext.checking(new Expectations() {
//				{
//					AghParametros equipamentoVideo = new AghParametros();
//					equipamentoVideo.setVlrNumerico(BigDecimal.TEN);
//					
//					oneOf(mockedParametroFacade).buscarAghParametro(with(AghuParametrosEnum.P_EQUIPAMENTO_VIDEOLAP));
//					will(returnValue(equipamentoVideo));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockMbcProcedimentoPorGrupoDAO).listarGrupoSeqPorProcedimento(with(any(Integer.class)));
//					will(returnValue(gruposSeq));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockMbcEquipamentoCirgPorUnidDAO).obterQuantidadePorId(with(any(Short.class)), with(any(Short.class)));
//					will(returnValue(Short.valueOf("3")));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockMbcProcEspPorCirurgiasDAO).buscarQuantidadeCirurgiasComDetProc(
//							with(any(Integer.class)),
//							with(any(Short.class)),
//							with(any(Date.class)),
//							with(any(Short.class)));
//					will(returnValue(3));
//				}
//			});
//			
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedAgendasDAO).buscarAgendasComProcEquipColisaoHorario(
//							with(any(Date.class)),
//							with(any(Short.class)),
//							with(any(Integer.class)),
//							with(any(Short.class)));
//					will(returnValue(listaHorariosColisao));
//				}
//			});
			
			
			systemUnderTest.verificarDisponibilideDeEquipamentos(1, DominioSituacaoAgendas.ES, 55, false, dataIni.getTime(),
					dataFim.getTime(), Short.valueOf("131"), new Date(), "rn_agdp_ver_utlz_equ");
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_00858));
		}
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarQuantidadeInformadaTest1() {
		MbcAgendaProcedimento mbcAgendaProcedimento = new MbcAgendaProcedimento();
		mbcAgendaProcedimento.setQtde(Short.valueOf("1"));
		
		MbcAgendas agenda = new MbcAgendas();
		agenda.setIndGeradoSistema(true);
		
		mbcAgendaProcedimento.setMbcAgendas(agenda);
		
		try {
			systemUnderTest.verificarQuantidadeInformada(mbcAgendaProcedimento);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00864
	 */
	@Test
	public void verificarQuantidadeInformadaTest2() {
		MbcAgendaProcedimento mbcAgendaProcedimento = new MbcAgendaProcedimento();
		
		MbcProcedimentoCirurgicos procCirg = new MbcProcedimentoCirurgicos();
		procCirg.setIndProcMultiplo(false);
		
		MbcAgendas agenda = new MbcAgendas();
		agenda.setIndGeradoSistema(false);
		
		mbcAgendaProcedimento.setProcedimentoCirurgico(procCirg);
		mbcAgendaProcedimento.setMbcAgendas(agenda);
		mbcAgendaProcedimento.setQtde(Short.valueOf("2"));
		
		try {
			systemUnderTest.verificarQuantidadeInformada(mbcAgendaProcedimento);
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_00864));
		}
	}
	
	/**
	 * MBC_01069
	 */
	@Test
	public void verificarQuantidadeInformadaTest3() {
		MbcAgendaProcedimento mbcAgendaProcedimento = new MbcAgendaProcedimento();
		
		MbcProcedimentoCirurgicos procCirg = new MbcProcedimentoCirurgicos();
		procCirg.setIndProcMultiplo(true);
		procCirg.setTempoMinimo(Short.valueOf("200"));
		procCirg.setDescricao("Enxerto ósseo");
		
		MbcAgendas agenda = new MbcAgendas();
		agenda.setIndGeradoSistema(false);
		Calendar data = Calendar.getInstance();
		data.set(2013, 3, 10, 1, 30);
		agenda.setTempoSala(data.getTime());
		
		mbcAgendaProcedimento.setMbcAgendas(agenda);
		mbcAgendaProcedimento.setProcedimentoCirurgico(procCirg);
		mbcAgendaProcedimento.setQtde(Short.valueOf("2"));
		
		try {
			systemUnderTest.verificarQuantidadeInformada(mbcAgendaProcedimento);
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_01069));
		}
	}
	
	/**
	 * SUCESSO
	 */
	@Test
	public void verificarProcedimentoPrincipalTest1() {
		MbcAgendaProcedimento mbcAgendaProcedimento = new MbcAgendaProcedimento();
		MbcAgendaProcedimentoId idAgenda = new MbcAgendaProcedimentoId();
		idAgenda.setEprEspSeq(Short.valueOf("22"));
		mbcAgendaProcedimento.setId(idAgenda);
		
		MbcAgendas agenda = new MbcAgendas();
		
		MbcEspecialidadeProcCirgs espProcCirgs = new MbcEspecialidadeProcCirgs();
		MbcEspecialidadeProcCirgsId id = new MbcEspecialidadeProcCirgsId();
		id.setEspSeq(Short.valueOf("23"));
		espProcCirgs.setId(id);
		
		agenda.setEspProcCirgs(espProcCirgs);
		
		mbcAgendaProcedimento.setMbcAgendas(agenda);
		
		try {
			systemUnderTest.verificarProcedimentoPrincipal(mbcAgendaProcedimento);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * MBC_00891
	 */
	@Test
	public void verificarProcedimentoPrincipalTest2() {
		MbcAgendaProcedimento mbcAgendaProcedimento = new MbcAgendaProcedimento();
		MbcAgendaProcedimentoId idAgenda = new MbcAgendaProcedimentoId();
		idAgenda.setEprEspSeq(Short.valueOf("23"));
		idAgenda.setEprPciSeq(2);
		mbcAgendaProcedimento.setId(idAgenda);
		
		MbcAgendas agenda = new MbcAgendas();
		agenda.setIndGeradoSistema(false);
		
		MbcEspecialidadeProcCirgs espProcCirgs = new MbcEspecialidadeProcCirgs();
		MbcEspecialidadeProcCirgsId id = new MbcEspecialidadeProcCirgsId();
		id.setEspSeq(Short.valueOf("23"));
		id.setPciSeq(2);
		espProcCirgs.setId(id);
		
		agenda.setEspProcCirgs(espProcCirgs);
		
		mbcAgendaProcedimento.setMbcAgendas(agenda);
		
		try {
			systemUnderTest.verificarProcedimentoPrincipal(mbcAgendaProcedimento);
			Assert.fail("Deveria falhar");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcAgendaProcedimentoRNExceptionCode.MBC_00891));
		}
	}
	
}