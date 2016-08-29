package br.gov.mec.aghu.exames.coleta.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.coleta.business.AgendaExameHorarioRN.AgendaExamesHorariosRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AgendaExameHorarioRNTest  extends AGHUBaseUnitTest<AgendaExameHorarioRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	@Mock
	private AelAmostraItemExamesDAO mockedAelAmostraItemExamesDAO;
	@Mock
	private IColetaExamesFacade mockedColetaExamesFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private AelSitItemSolicitacoesDAO mockedAelSitItemSolicitacoesDAO;
	@Mock
	private AelItemHorarioAgendadoDAO mockedAelItemHorarioAgendadoDAO;
	@Mock
	private ISolicitacaoExameFacade mockedSolicitacaoExameFacade;
	@Mock
	private AelExtratoItemSolicitacaoDAO mockedAelExtratoItemSolicitacaoDAO;
	
	@Before
	public void doBeforeEachTestCase() {
	}
	
	@Test
	public void testVerificarAmostrasExamesAgendadosSuccess() {
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(Short.valueOf("33"));
		id.setHedGaeSeqp(257);
		id.setHedDthrAgenda(new Date());
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(id);
		
		final List<AelAmostraItemExames> list = new ArrayList<AelAmostraItemExames>();
		list.add(new AelAmostraItemExames());
		
		try {
			Mockito.when(mockedColetaExamesFacade.verificarMaterialAnaliseColetavel(itemHorarioAgendado)).thenReturn(true);

			Mockito.when(mockedAelAmostraItemExamesDAO.listarAmostrasComHorarioAgendado(itemHorarioAgendado.getId().getHedGaeUnfSeq(),
					itemHorarioAgendado.getId().getHedGaeSeqp(), itemHorarioAgendado.getId().getHedDthrAgenda())).thenReturn(list);

			systemUnderTest.verificarAmostrasExamesAgendados(itemHorarioAgendado);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarAmostrasExamesAgendadosError1() {
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(Short.valueOf("33"));
		id.setHedGaeSeqp(257);
		id.setHedDthrAgenda(new Date());
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(id);
		
		final List<AelAmostraItemExames> list = new ArrayList<AelAmostraItemExames>();
		list.add(new AelAmostraItemExames());
		
		try {
			Mockito.when(mockedColetaExamesFacade.verificarMaterialAnaliseColetavel(itemHorarioAgendado)).thenReturn(false);
			
			systemUnderTest.verificarAmostrasExamesAgendados(itemHorarioAgendado);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AgendaExamesHorariosRNExceptionCode.AEL_00915);
		}
	}
	
	@Test
	public void testVerificarAmostrasExamesAgendadosError2() {
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(Short.valueOf("33"));
		id.setHedGaeSeqp(257);
		id.setHedDthrAgenda(new Date());
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(id);
		
		final List<AelAmostraItemExames> list = new ArrayList<AelAmostraItemExames>();
		
		try {
			Mockito.when(mockedColetaExamesFacade.verificarMaterialAnaliseColetavel(itemHorarioAgendado)).thenReturn(true);

			Mockito.when(mockedAelAmostraItemExamesDAO.listarAmostrasComHorarioAgendado(itemHorarioAgendado.getId().getHedGaeUnfSeq(),
					itemHorarioAgendado.getId().getHedGaeSeqp(), itemHorarioAgendado.getId().getHedDthrAgenda())).thenReturn(list);

			systemUnderTest.verificarAmostrasExamesAgendados(itemHorarioAgendado);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AgendaExamesHorariosRNExceptionCode.AEL_00871);
		}
	}
	
	@Test
	public void testReceberItemSolicitacaoExameAgendadoSuccess() {
		final Short hedGaeUnfSeq = 2;
		final Integer hedGaeSeqp = 3;
		final Date hedDthrAgenda = new Date();
		DominioSituacaoHorario dominioSituacaoHorario = DominioSituacaoHorario.M;
		
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(hedGaeUnfSeq);
		id.setHedGaeSeqp(hedGaeSeqp);
		id.setHedDthrAgenda(hedDthrAgenda);
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(id);
		
		final AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("AE");
		
		final AelSitItemSolicitacoes areaExec = new AelSitItemSolicitacoes();
		
		final AghParametros parametroAgendado = new AghParametros();
		parametroAgendado.setVlrTexto("AG");
		
		final AelSitItemSolicitacoes agendado = new AelSitItemSolicitacoes();
		agendado.setCodigo("");
		
		AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		final AelItemHorarioAgendado agen = new AelItemHorarioAgendado();
		agen.setItemSolicitacaoExame(item);
		
		final List<AelItemHorarioAgendado> listAgendados = new ArrayList<AelItemHorarioAgendado>();
		listAgendados.add(agen);
		
		try {
			Mockito.when(mockedColetaExamesFacade.verificarMaterialAnaliseColetavel(itemHorarioAgendado)).thenReturn(false);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA)).thenReturn(parametro);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO)).thenReturn(parametroAgendado);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametro.getVlrTexto())).thenReturn(areaExec);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametroAgendado.getVlrTexto())).thenReturn(agendado);

			Mockito.when(mockedAelItemHorarioAgendadoDAO.obterExamesAgendados(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, agendado)).thenReturn(listAgendados);
			
			systemUnderTest.receberItemSolicitacaoExameAgendado(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testReceberItemSolicitacaoExameAgendadoError1() {
		final Short hedGaeUnfSeq = 2;
		final Integer hedGaeSeqp = 3;
		final Date hedDthrAgenda = new Date();
		DominioSituacaoHorario dominioSituacaoHorario = DominioSituacaoHorario.M;
		
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(hedGaeUnfSeq);
		id.setHedGaeSeqp(hedGaeSeqp);
		id.setHedDthrAgenda(hedDthrAgenda);
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(id);
		
		final AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("AE");
		
		final AelSitItemSolicitacoes areaExec = new AelSitItemSolicitacoes();
		
		final AghParametros parametroAgendado = new AghParametros();
		parametroAgendado.setVlrTexto("AG");
		
		final AelSitItemSolicitacoes agendado = new AelSitItemSolicitacoes();
		
		AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		final AelItemHorarioAgendado agen = new AelItemHorarioAgendado();
		agen.setItemSolicitacaoExame(item);
		
		final List<AelItemHorarioAgendado> listAgendados = new ArrayList<AelItemHorarioAgendado>();
		listAgendados.add(agen);
		
		try {
			Mockito.when(mockedColetaExamesFacade.verificarMaterialAnaliseColetavel(itemHorarioAgendado)).thenReturn(true);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA)).thenReturn(parametro);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO)).thenReturn(parametroAgendado);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametro.getVlrTexto())).thenReturn(areaExec);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametroAgendado.getVlrTexto())).thenReturn(agendado);

			Mockito.when(mockedAelItemHorarioAgendadoDAO.obterExamesAgendados(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, agendado)).thenReturn(listAgendados);

			systemUnderTest.receberItemSolicitacaoExameAgendado(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AgendaExamesHorariosRNExceptionCode.AEL_00918);
		}
	}
	
	@Test
	public void testReceberItemSolicitacaoExameAgendadoError2() {
		final Short hedGaeUnfSeq = 2;
		final Integer hedGaeSeqp = 3;
		final Date hedDthrAgenda = new Date();
		DominioSituacaoHorario dominioSituacaoHorario = DominioSituacaoHorario.E;
		
		try {
			systemUnderTest.receberItemSolicitacaoExameAgendado(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AgendaExamesHorariosRNExceptionCode.AEL_00908);
		}
	}
	
	@Test
	public void testVoltarItemSolicitacaoExameAgendadoSuccess() {
		final Short hedGaeUnfSeq = 2;
		final Integer hedGaeSeqp = 3;
		final Date hedDthrAgenda = new Date();
		DominioSituacaoHorario dominioSituacaoHorario = DominioSituacaoHorario.E;
		
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(hedGaeUnfSeq);
		id.setHedGaeSeqp(hedGaeSeqp);
		id.setHedDthrAgenda(hedDthrAgenda);
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(id);
		
		final AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("AE");
		
		final AelSitItemSolicitacoes areaExec = new AelSitItemSolicitacoes();
		areaExec.setCodigo("");
		
		final AghParametros parametroAgendado = new AghParametros();
		parametroAgendado.setVlrTexto("AG");
		
		final AelSitItemSolicitacoes agendado = new AelSitItemSolicitacoes();
		agendado.setCodigo("");
		
		AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		final AelItemHorarioAgendado agen = new AelItemHorarioAgendado();
		agen.setItemSolicitacaoExame(item);
		
		final List<AelItemHorarioAgendado> listAgendados = new ArrayList<AelItemHorarioAgendado>();
		listAgendados.add(agen);
		
		try {
			Mockito.when(mockedColetaExamesFacade.verificarMaterialAnaliseColetavel(itemHorarioAgendado)).thenReturn(false);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA)).thenReturn(parametro);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO)).thenReturn(parametroAgendado);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametro.getVlrTexto())).thenReturn(areaExec);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametroAgendado.getVlrTexto())).thenReturn(agendado);

			Mockito.when(mockedAelItemHorarioAgendadoDAO.obterExamesAgendados(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, agendado)).thenReturn(listAgendados);

			systemUnderTest.voltarItemSolicitacaoExameAgendado(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testVoltarItemSolicitacaoExameAgendadoError1() {
		final Short hedGaeUnfSeq = 2;
		final Integer hedGaeSeqp = 3;
		final Date hedDthrAgenda = new Date();
		DominioSituacaoHorario dominioSituacaoHorario = DominioSituacaoHorario.E;
		
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(hedGaeUnfSeq);
		id.setHedGaeSeqp(hedGaeSeqp);
		id.setHedDthrAgenda(hedDthrAgenda);
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(id);
		
		final AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("AE");
		
		final AelSitItemSolicitacoes areaExec = new AelSitItemSolicitacoes();
		
		final AghParametros parametroAgendado = new AghParametros();
		parametroAgendado.setVlrTexto("AG");
		
		final AelSitItemSolicitacoes agendado = new AelSitItemSolicitacoes();
		
		AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		final AelItemHorarioAgendado agen = new AelItemHorarioAgendado();
		agen.setItemSolicitacaoExame(item);
		
		final List<AelItemHorarioAgendado> listAgendados = new ArrayList<AelItemHorarioAgendado>();
		listAgendados.add(agen);
		
		try {
			Mockito.when(mockedColetaExamesFacade.verificarMaterialAnaliseColetavel(itemHorarioAgendado)).thenReturn(true);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA)).thenReturn(parametro);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO)).thenReturn(parametroAgendado);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametro.getVlrTexto())).thenReturn(areaExec);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametroAgendado.getVlrTexto())).thenReturn(agendado);

			Mockito.when(mockedAelItemHorarioAgendadoDAO.obterExamesAgendados(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, agendado)).thenReturn(listAgendados);

			systemUnderTest.voltarItemSolicitacaoExameAgendado(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AgendaExamesHorariosRNExceptionCode.AEL_00918);
		}
	}
	
	@Test
	public void testVoltarItemSolicitacaoExameAgendadoError2() {
		final Short hedGaeUnfSeq = 2;
		final Integer hedGaeSeqp = 3;
		final Date hedDthrAgenda = new Date();
		DominioSituacaoHorario dominioSituacaoHorario = DominioSituacaoHorario.M;
		
		try {
			systemUnderTest.voltarItemSolicitacaoExameAgendado(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AgendaExamesHorariosRNExceptionCode.AEL_00910);
		}
	}
	
	@Test
	public void testVoltarColetaSuccess() {
		final Short hedGaeUnfSeq = 2;
		final Integer hedGaeSeqp = 3;
		final Integer iseSoeSeq = 12;
		final Short iseSeqp = 1;
		final Date hedDthrAgenda = new Date();
		DominioSituacaoHorario dominioSituacaoHorario = DominioSituacaoHorario.E;
		
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(hedGaeUnfSeq);
		id.setHedGaeSeqp(hedGaeSeqp);
		id.setHedDthrAgenda(hedDthrAgenda);
		id.setIseSoeSeq(iseSoeSeq);
		id.setIseSeqp(iseSeqp);
		
		final AghParametros parametroColetado = new AghParametros();
		parametroColetado.setVlrTexto("CO");
		
		final AelSitItemSolicitacoes coletado = new AelSitItemSolicitacoes();
		coletado.setCodigo("");
		
		AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		item.setSituacaoItemSolicitacao(coletado);
		final AelItemHorarioAgendado agen = new AelItemHorarioAgendado();
		agen.setItemSolicitacaoExame(item);
		agen.setId(id);
		
		final List<AelItemHorarioAgendado> listAgendados = new ArrayList<AelItemHorarioAgendado>();
		listAgendados.add(agen);
		
		final AelExtratoItemSolicitacao aelExtratoItemSolicitacao = new AelExtratoItemSolicitacao();
		aelExtratoItemSolicitacao.setAelMotivoCancelaExames(new AelMotivoCancelaExames());
		aelExtratoItemSolicitacao.setComplementoMotCanc("complemento canc");
		aelExtratoItemSolicitacao.setAelSitItemSolicitacoes(new AelSitItemSolicitacoes());
		
		try {
			Mockito.when(mockedColetaExamesFacade.verificarMaterialAnaliseColetavel(Mockito.any(AelItemHorarioAgendado.class))).thenReturn(true);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO)).thenReturn(parametroColetado);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametroColetado.getVlrTexto())).thenReturn(coletado);

			Mockito.when(mockedAelItemHorarioAgendadoDAO.obterItensHorarioAgendadoComAmostras(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, DominioSituacaoAmostra.C)).
			thenReturn(listAgendados);

			Mockito.when(mockedAelExtratoItemSolicitacaoDAO.obterPenultimoExtratoPorItemSolicitacao(iseSoeSeq, iseSeqp)).thenReturn(aelExtratoItemSolicitacao);
			
			systemUnderTest.voltarColeta(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testVoltarColetaError1() {
		final Short hedGaeUnfSeq = 2;
		final Integer hedGaeSeqp = 3;
		final Date hedDthrAgenda = new Date();
		DominioSituacaoHorario dominioSituacaoHorario = DominioSituacaoHorario.M;
		try {
			systemUnderTest.voltarColeta(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AgendaExamesHorariosRNExceptionCode.AEL_00874);
		}
	}
	
	@Test
	public void testVoltarColetaError2() {
		final Short hedGaeUnfSeq = 2;
		final Integer hedGaeSeqp = 3;
		final Date hedDthrAgenda = new Date();
		DominioSituacaoHorario dominioSituacaoHorario = DominioSituacaoHorario.E;
		
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(hedGaeUnfSeq);
		id.setHedGaeSeqp(hedGaeSeqp);
		id.setHedDthrAgenda(hedDthrAgenda);
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(id);
		
		try {
			Mockito.when(mockedColetaExamesFacade.verificarMaterialAnaliseColetavel(Mockito.any(AelItemHorarioAgendado.class))).thenReturn(false);

			systemUnderTest.voltarColeta(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AgendaExamesHorariosRNExceptionCode.AEL_00917);
		}
	}
	
	@Test
	public void testVoltarColetaError3() {
		final Short hedGaeUnfSeq = 2;
		final Integer hedGaeSeqp = 3;
		final Date hedDthrAgenda = new Date();
		DominioSituacaoHorario dominioSituacaoHorario = DominioSituacaoHorario.E;
		
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(hedGaeUnfSeq);
		id.setHedGaeSeqp(hedGaeSeqp);
		id.setHedDthrAgenda(hedDthrAgenda);
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(id);
		
		final AghParametros parametroColetado = new AghParametros();
		parametroColetado.setVlrTexto("CO");
		
		final AelSitItemSolicitacoes areaExec = new AelSitItemSolicitacoes();
		
		final AghParametros parametroAgendado = new AghParametros();
		parametroAgendado.setVlrTexto("AG");
		
		final AelSitItemSolicitacoes agendado = new AelSitItemSolicitacoes();
		
		final List<AelItemHorarioAgendado> listAgendados = null;
		
		try {
			Mockito.when(mockedColetaExamesFacade.verificarMaterialAnaliseColetavel(Mockito.any(AelItemHorarioAgendado.class))).thenReturn(true);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO)).thenReturn(parametroAgendado);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametroAgendado.getVlrTexto())).thenReturn(areaExec);

			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO)).thenReturn(parametroColetado);

			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(parametroColetado.getVlrTexto())).thenReturn(agendado);

			Mockito.when(mockedAelItemHorarioAgendadoDAO.obterItensHorarioAgendadoComAmostras(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, DominioSituacaoAmostra.C)).
			thenReturn(listAgendados);
			
			systemUnderTest.voltarColeta(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AgendaExamesHorariosRNExceptionCode.ERRO_FALTA_ITENS_COLETADOS);
		}
	}
	
}
