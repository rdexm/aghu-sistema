package br.gov.mec.aghu.exames.business;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.AssertFalse;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioExameDispId;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class AelItemHorarioAgendadoRNTest extends AGHUBaseUnitTest<AelItemHorarioAgendadoRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	@Mock
	private AelItemHorarioAgendadoDAO mockedAelItemHorarioAgendadoDAO;
	@Mock
	private AelSitItemSolicitacoesDAO mockedAelSitItemSolicitacoesDAO;
	@Mock
	private AelMatrizSituacaoDAO mockedAelMatrizSituacaoDAO;
	@Mock
	private ISolicitacaoExameFacade mockedSolicitacaoExameFacade;
	@Mock
	private AelHorarioExameDispRN mockedAelHorarioExameDispRN;
	@Mock
	private AelAmostrasRN mockedAelAmostrasRN;
	@Mock
	private IParametroFacade mockedParametroFacade;
	
	
	@Test
	/**
	 * Se material for coletável deve atualizar item para AC.
	 */
	public void atualizarItemAgendamentoColetavelTest() throws BaseException {
		
		AelSitItemSolicitacoes sitItemSolicitacoesAG = new AelSitItemSolicitacoes();
		sitItemSolicitacoesAG.setCodigo("AG");
		
		final AelSitItemSolicitacoes sitItemSolicitacoesAC = new AelSitItemSolicitacoes();
		sitItemSolicitacoesAC.setCodigo("AC");
		
		final AelSitItemSolicitacoes sitItemSolicitacoesAE = new AelSitItemSolicitacoes();
		sitItemSolicitacoesAE.setCodigo("AE");
		
		final AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setIndColetavel(true);

		AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoesAG);
		itemSolicitacaoExames.setMaterialAnalise(materialAnalise);
		
		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId(6104644, Short.valueOf("1"));
		itemSolicitacaoExames.setId(itemSolicitacaoExameId);
		
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setItemSolicitacaoExame(itemSolicitacaoExames);
		
		Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString())).thenReturn(materialAnalise.getIndColetavel() ? sitItemSolicitacoesAC : sitItemSolicitacoesAE);
		try {
			systemUnderTest.atualizarItemAgendamento(itemSolicitacaoExames, itemSolicitacaoExames, NOME_MICROCOMPUTADOR);
			Assert.fail("Espera Exeção" );
		} catch (ApplicationBusinessException e) {
			
			assertEquals(sitItemSolicitacoesAC.getCodigo(), itemHorarioAgendado.getItemSolicitacaoExame().getSituacaoItemSolicitacao().getCodigo());
		}
		
	}
	
	@Test
	/**
	 * Se material for NÃO for coletável deve atualizar item para AE.
	 */
	public void atualizarItemAgendamentoNaoColetavelTest() throws BaseException {
		
		AelSitItemSolicitacoes sitItemSolicitacoesAG = new AelSitItemSolicitacoes();
		sitItemSolicitacoesAG.setCodigo("AG");
		
		final AelSitItemSolicitacoes sitItemSolicitacoesAC = new AelSitItemSolicitacoes();
		sitItemSolicitacoesAC.setCodigo("AC");
		
		final AelSitItemSolicitacoes sitItemSolicitacoesAE = new AelSitItemSolicitacoes();
		sitItemSolicitacoesAE.setCodigo("AE");
		
		final AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setIndColetavel(false);

		AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoesAG);
		itemSolicitacaoExames.setMaterialAnalise(materialAnalise);
		
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setItemSolicitacaoExame(itemSolicitacaoExames);
		
		Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString()))
		.thenReturn(materialAnalise.getIndColetavel() ? sitItemSolicitacoesAC : sitItemSolicitacoesAE);
		
		try {
			systemUnderTest.atualizarItemAgendamento(itemSolicitacaoExames, itemSolicitacaoExames, NOME_MICROCOMPUTADOR);
			Assert.fail("Espera Exeção" );
		} catch(ApplicationBusinessException e) {
			assertEquals(sitItemSolicitacoesAE.getCodigo(), itemHorarioAgendado.getItemSolicitacaoExame().getSituacaoItemSolicitacao().getCodigo());
		}
		
	}
	
	/**
	 * Atualiza situação horário para B,
	 * quando indHorarioAgendado for Verdadeiro.
	 * @throws BaseException
	 */
	@Test
	public void atualizarHoraMarcadaIndHorarioExtraVerdadeiroTest() throws BaseException {
		
		final List<AelItemHorarioAgendado> listItemHorarioAgendado = new ArrayList<AelItemHorarioAgendado>();
		
		AelHorarioExameDispId horarioExameDispId = new AelHorarioExameDispId();
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setId(horarioExameDispId);
		horarioExameDisp.setIndHorarioExtra(true);
		
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setHorarioExameDisp(horarioExameDisp);
		
		Mockito.when(mockedAelItemHorarioAgendadoDAO.buscarExamesAgendadosNoMesmoHorario(Mockito.anyShort(), Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(AelItemSolicitacaoExames.class)))
		.thenReturn(listItemHorarioAgendado);

		systemUnderTest.atualizarHoraMarcada(itemHorarioAgendado);
		
		assertEquals(DominioSituacaoHorario.B, itemHorarioAgendado.getHorarioExameDisp().getSituacaoHorario());
		
	}
	
	/**
	 * Atualiza situação horário para L,
	 * quando indHorarioAgendado for Verdadeiro.
	 * @throws BaseException
	 */
	@Test
	public void atualizarHoraMarcadaIndHorarioExtraFalsoTest() throws BaseException {
		
		final List<AelItemHorarioAgendado> listItemHorarioAgendado = new ArrayList<AelItemHorarioAgendado>();
		
		AelHorarioExameDispId horarioExameDispId = new AelHorarioExameDispId();
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setId(horarioExameDispId);
		horarioExameDisp.setIndHorarioExtra(false);
		
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setHorarioExameDisp(horarioExameDisp);
		
		Mockito.when(mockedAelItemHorarioAgendadoDAO.buscarExamesAgendadosNoMesmoHorario(Mockito.anyShort(), Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(AelItemSolicitacaoExames.class)))
		.thenReturn(listItemHorarioAgendado);

		systemUnderTest.atualizarHoraMarcada(itemHorarioAgendado);
		
		assertEquals(DominioSituacaoHorario.L, itemHorarioAgendado.getHorarioExameDisp().getSituacaoHorario());
		
	}
	
	@Test
	public void testCancelarHorariosPorItemSolicitacaoExame() {
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final List<AelItemHorarioAgendado> listaItemHorarioAgendado = new ArrayList<AelItemHorarioAgendado>();
		
		Mockito.when(mockedAelItemHorarioAgendadoDAO.buscarPorItemSolicitacaoExame(itemSolicitacaoExame))
		.thenReturn(listaItemHorarioAgendado);

		try {
			systemUnderTest.cancelarHorariosPorItemSolicitacaoExame(itemSolicitacaoExame, itemSolicitacaoExame, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarHorariosExamesAgendados() {
		final AghParametros paramSituacaoAgendado = new AghParametros();
		final Short unfSeqGlobal = Short.valueOf("10");
		final DominioSimNaoRestritoAreaExecutora restritoUnidExecutora = DominioSimNaoRestritoAreaExecutora.R;
		final AelHorarioExameDispId horarioExameDispId = new AelHorarioExameDispId();
		horarioExameDispId.setGaeSeqp(9);
		horarioExameDispId.setGaeUnfSeq(Short.valueOf("311"));
		final AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setId(horarioExameDispId);
		final AelExames exame = new AelExames();
		exame.setSigla("RMX02");
		final AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setSeq(1);
		final AghUnidadesFuncionais unidFuncional = new AghUnidadesFuncionais(Short.valueOf("9"));
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId(6104644, Short.valueOf("1"));
		itemSolicitacaoExame.setId(itemSolicitacaoExameId);
		itemSolicitacaoExame.setExame(exame);
		itemSolicitacaoExame.setMaterialAnalise(materialAnalise);
		itemSolicitacaoExame.setUnidadeFuncional(unidFuncional);		
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setItemSolicitacaoExame(itemSolicitacaoExame);
		itemHorarioAgendado.setHorarioExameDisp(horarioExameDisp);
		final List<AelItemHorarioAgendado> listaItemHorarioAgendado = new ArrayList<AelItemHorarioAgendado>();
		
		try {
			Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO))
			.thenReturn(paramSituacaoAgendado);


			Mockito.when(mockedAelItemHorarioAgendadoDAO.pesquisarItemHorarioAgendadoPorGradeEItemSolicitacaoExame(
					horarioExameDispId.getGaeUnfSeq(),
					horarioExameDispId.getGaeSeqp(), 
					itemSolicitacaoExameId.getSoeSeq(), 
					itemSolicitacaoExameId.getSeqp(), 
					itemSolicitacaoExame.getExame().getSigla(),
					itemSolicitacaoExame.getMaterialAnalise().getSeq(),
					itemSolicitacaoExame.getUnidadeFuncional().getSeq(),
					paramSituacaoAgendado.getVlrTexto(), restritoUnidExecutora
					))
			.thenReturn(listaItemHorarioAgendado);

			systemUnderTest.cancelarHorariosExamesAgendados(itemHorarioAgendado, unfSeqGlobal, NOME_MICROCOMPUTADOR);
			
		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}

}
