package br.gov.mec.aghu.blococirurgico.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.business.AgendaProcedimentosON.AgendaProcedimentosONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelProjetoProcedimento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AgendaProcedimentosON.class)
public class AgendaProcedimentosONTest extends AGHUBaseUnitTest<AgendaProcedimentosON>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private MbcProcedimentoCirurgicoDAO mockedMbcProcedimentoCirurgicoDAO;
	@Mock
	private MbcCirurgiasDAO mockedMbcCirurgiasDAO;
	@Mock
	private MbcControleEscalaCirurgicaDAO mockedMbcControleEscalaCirurgicaDAO;
	@Mock
	private MbcSolicHemoCirgAgendadaDAO mockedMbcSolicHemoCirgAgendadaDAO;
	@Mock
	private MbcAnestesiaCirurgiasDAO mockedMbcAnestesiaCirurgiasDAO;
	@Mock
	private MbcProfCirurgiasDAO mockedMbcProfCirurgiasDAO;
	@Mock
	private MbcProcEspPorCirurgiasDAO mockedMbcProcEspPorCirurgiasDAO;
	

	private AelProjetoPesquisas criarProjetoPesquisaMock() {
		final Calendar dtInicio = Calendar.getInstance();
		dtInicio.set(2013, 06, 13);
		final Calendar dtAtual = Calendar.getInstance();
		dtAtual.set(2013, 07, 26);
		final Calendar dtFim = Calendar.getInstance();
		dtFim.set(2016, 11, 16);
		
		AelProjetoPesquisas projetoPesquisa = new AelProjetoPesquisas();
		projetoPesquisa.setDtInicio(dtInicio.getTime());
		projetoPesquisa.setDtFim(dtFim.getTime());
		return projetoPesquisa;
	}
	
	@Test
	public void testAgendarProcedimentosParte1ErroTempoPrevisto() {
		MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setTempoPrevistoHoras(Short.parseShort("2"));
		cirurgia.setTempoPrevistoMinutos(Byte.parseByte("30"));
		
		MbcProcedimentoCirurgicos procedimentoCirurgicos = new MbcProcedimentoCirurgicos();
		procedimentoCirurgicos.setTempoMinimo(Short.parseShort("300"));
		
		MbcEspecialidadeProcCirgs especialidade = new MbcEspecialidadeProcCirgs();
		especialidade.setMbcProcedimentoCirurgicos(procedimentoCirurgicos);
		
		CirurgiaTelaProcedimentoVO procedimento = new CirurgiaTelaProcedimentoVO();
		procedimento.setMbcEspecialidadeProcCirgs(especialidade);
		List<CirurgiaTelaProcedimentoVO> procedimentoList = new ArrayList<CirurgiaTelaProcedimentoVO>();
		procedimentoList.add(procedimento);
		
		CirurgiaTelaVO vo = new CirurgiaTelaVO();
		vo.setCirurgia(cirurgia);
		vo.setListaProcedimentosVO(procedimentoList);
		
		/*FR try {
			 systemUnderTest.agendarProcedimentosParte1(false, vo, null);
			fail("testAgendarProcedimentosParte1ErroTempoPrevisto: Exeção esperada não gerada.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), AgendaProcedimentosONExceptionCode.MBCP_TEMPO_PREVISTO_MAIOR_MIN_PROCEDIMENTOS);
		}*/
	}
	
	@Test
	public void testPreCommitError1() {
		AelProjetoPesquisas projetoPesquisa = criarProjetoPesquisaMock();
		projetoPesquisa.setVoucherEletronico(false);
		
		MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setProjetoPesquisa(projetoPesquisa);
		
		CirurgiaTelaVO vo = new CirurgiaTelaVO();
		vo.setCirurgia(cirurgia);
		
		try {
			systemUnderTest.preCommit(vo);
			fail("testPreCommitError1: Exeção esperada não gerada.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), AgendaProcedimentosONExceptionCode.MBC_01380);
		}
	}

	@Test
	public void testPreCommitError2() {
		AelProjetoPesquisas projetoPesquisa = criarProjetoPesquisaMock();
		projetoPesquisa.setVoucherEletronico(true);
		
		MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setProjetoPesquisa(projetoPesquisa);
		
		CirurgiaTelaProcedimentoVO procedimentoVO = new CirurgiaTelaProcedimentoVO();
		procedimentoVO.setId(new MbcProcEspPorCirurgiasId(cirurgia.getSeq(), 10, Short.parseShort("5"), DominioIndRespProc.AGND));
		procedimentoVO.setIndPrincipal(true);
		List<CirurgiaTelaProcedimentoVO> listaProc = new ArrayList<CirurgiaTelaProcedimentoVO>();
		listaProc.add(procedimentoVO);
		
		final MbcProcedimentoCirurgicos procedimento = new MbcProcedimentoCirurgicos();
		procedimento.setSeq(10);
		
		CirurgiaTelaVO vo = new CirurgiaTelaVO();
		vo.setCirurgia(cirurgia);
		vo.setListaProcedimentosVO(listaProc);
		
		Mockito.when(mockedMbcProcedimentoCirurgicoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(procedimento);

		Mockito.when(mockedExamesFacade.obterProjetoProcedimentoAtivoPorId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);

		try {
			systemUnderTest.preCommit(vo);
			fail("testPreCommitError2: Exeção esperada não gerada.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), AgendaProcedimentosONExceptionCode.MBC_01376);
		}
	}
	
	@Test
	public void testPreCommitError3() {
		AelProjetoPesquisas projetoPesquisa = criarProjetoPesquisaMock();
		projetoPesquisa.setVoucherEletronico(true);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(20);
		
		MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setPaciente(paciente);
		cirurgia.setProjetoPesquisa(projetoPesquisa);
		
		CirurgiaTelaProcedimentoVO procedimentoVO = new CirurgiaTelaProcedimentoVO();
		procedimentoVO.setId(new MbcProcEspPorCirurgiasId(cirurgia.getSeq(), 10, Short.parseShort("5"), DominioIndRespProc.AGND));
		procedimentoVO.setIndPrincipal(true);
		List<CirurgiaTelaProcedimentoVO> listaProc = new ArrayList<CirurgiaTelaProcedimentoVO>();
		listaProc.add(procedimentoVO);
		
		final MbcProcedimentoCirurgicos procedimento = new MbcProcedimentoCirurgicos();
		procedimento.setSeq(10);
		
		CirurgiaTelaVO vo = new CirurgiaTelaVO();
		vo.setCirurgia(cirurgia);
		vo.setListaProcedimentosVO(listaProc);
		
		final AelProjetoProcedimento projetoProcedimento = new AelProjetoProcedimento();
		
		Mockito.when(mockedMbcProcedimentoCirurgicoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(procedimento);

		Mockito.when(mockedExamesFacade.obterProjetoProcedimentoAtivoPorId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(projetoProcedimento);

		Mockito.when(mockedExamesFacade.obterProjetoPacienteCadastradoDataProjetoPesquisa(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
		
		try {
			systemUnderTest.preCommit(vo);
			fail("testPreCommitError3: Exeção esperada não gerada.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), AgendaProcedimentosONExceptionCode.MBC_01378);
		}
	}
	
	@Test
	public void testPressionarCommitError1() {
		final AlertaModalVO alertaVO = new AlertaModalVO();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(20);
		
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais(Short.parseShort("5"));
		FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		convenioSaudePlano.setConvenioSaude(new FatConvenioSaude());
		
		MbcCirurgias cirurgia = new MbcCirurgias();
		final Calendar dtCirurgia = Calendar.getInstance();
		dtCirurgia.set(2013, 07, 26);
		cirurgia.setData(dtCirurgia.getTime());
		cirurgia.setNumeroAgenda(Short.parseShort("30"));
		cirurgia.setNaturezaAgenda(DominioNaturezaFichaAnestesia.ELE);
		cirurgia.setPaciente(paciente);
		cirurgia.setUnidadeFuncional(unidadeFuncional);
		cirurgia.setConvenioSaudePlano(convenioSaudePlano);
		
		CirurgiaTelaVO vo = new CirurgiaTelaVO();
		vo.setCirurgia(cirurgia);

		Mockito.when(mockedMbcCirurgiasDAO.obterCirurgiaAgendadaPacienteMesmoDia(Mockito.any(Date.class), Mockito.anyInt(), Mockito.anyShort(), Mockito.anyShort())).thenReturn(null);

		try {
	    	 AgendaProcedimentosON spy = PowerMockito.spy(systemUnderTest);	    	 
	    	 PowerMockito.doReturn(true).when(spy, "isHCPA");
			 spy.pressionarCommit(vo, alertaVO);
			} catch (BaseException e) {
				assertEquals(e.getCode(), AgendaProcedimentosONExceptionCode.MBC_01151);
			} catch (Exception e) {
				fail("testPressionarCommitError1: Exeção esperada não gerada.");
			}			
	}
	
	public void testPressionarCommitError2() {
		final AlertaModalVO alertaVO = new AlertaModalVO();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(20);
		
		FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais(Short.parseShort("5"));
		FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		convenioSaudePlano.setConvenioSaude(convenioSaude);
		
		MbcCirurgias cirurgia = new MbcCirurgias();
		final Calendar dtCirurgia = Calendar.getInstance();
		dtCirurgia.set(2013, 07, 26);
		cirurgia.setData(dtCirurgia.getTime());
		cirurgia.setNumeroAgenda(Short.parseShort("30"));
		cirurgia.setNaturezaAgenda(DominioNaturezaFichaAnestesia.ELE);
		cirurgia.setPaciente(paciente);
		cirurgia.setUnidadeFuncional(unidadeFuncional);
		cirurgia.setConvenioSaudePlano(convenioSaudePlano);
		
		CirurgiaTelaProcedimentoVO procedimentoVO = new CirurgiaTelaProcedimentoVO();
		procedimentoVO.setId(new MbcProcEspPorCirurgiasId(cirurgia.getSeq(), 10, Short.parseShort("5"), DominioIndRespProc.AGND));
		procedimentoVO.setIndPrincipal(true);
		procedimentoVO.setQtd(Byte.parseByte("1"));
		List<CirurgiaTelaProcedimentoVO> listaProc = new ArrayList<CirurgiaTelaProcedimentoVO>();
		listaProc.add(procedimentoVO);
		
		final MbcProcedimentoCirurgicos procedimento = new MbcProcedimentoCirurgicos();
		procedimento.setTipo(DominioTipoProcedimentoCirurgico.CIRURGIA);
		
		CirurgiaTelaVO vo = new CirurgiaTelaVO();
		vo.setCirurgia(cirurgia);
		vo.setListaProcedimentosVO(listaProc);
		
		
		Mockito.when(mockedMbcCirurgiasDAO.obterCirurgiaAgendadaPacienteMesmoDia(Mockito.any(Date.class), Mockito.anyInt(), Mockito.anyShort(), Mockito.anyShort())).thenReturn(null);

		List<MbcControleEscalaCirurgica> lista = new ArrayList<MbcControleEscalaCirurgica>();
		Mockito.when(mockedMbcControleEscalaCirurgicaDAO.pesquisarControleEscalaCirurgicaPorUnfSeqDataPesquisa(Mockito.anyShort(), Mockito.any(Date.class))).thenReturn(lista);

		Mockito.when(mockedMbcProcedimentoCirurgicoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(procedimento);
		
		try {
			systemUnderTest.pressionarCommit(vo, alertaVO);
			fail("testPressionarCommitError2: Exeção esperada não gerada.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), AgendaProcedimentosONExceptionCode.MBC_01334);
		}
	}
	
}
