package br.gov.mec.aghu.exames.coleta.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ColetaRealizadaRNTest  extends AGHUBaseUnitTest<ColetaRealizadaRN>{

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private AelItemHorarioAgendadoDAO mockedAelItemHorarioAgendadoDAO;
	@Mock
	private AelAmostraItemExamesDAO mockedAelAmostraItemExamesDAO;
	
	@Test
	public void testVerificarMaterialAnaliseColetavelTrue() {
		final List<AelItemHorarioAgendado> listaHorarioAgendado = new ArrayList<AelItemHorarioAgendado>(); 
		listaHorarioAgendado.add(new AelItemHorarioAgendado());
		final AelItemHorarioAgendadoId itemHorarioAgendadoId = new AelItemHorarioAgendadoId();
		itemHorarioAgendadoId.setHedGaeUnfSeq(Short.valueOf("311"));
		itemHorarioAgendadoId.setHedGaeSeqp(9);
		itemHorarioAgendadoId.setHedDthrAgenda(new Date());
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(itemHorarioAgendadoId);
				
		Mockito.when(mockedAelItemHorarioAgendadoDAO.pesquisarItemHorarioAgendadoItemSolicitacaoExameMaterialColetavel(
				Mockito.anyShort(), Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(listaHorarioAgendado);
		
		Assert.assertTrue(systemUnderTest.verificarMaterialAnaliseColetavel(itemHorarioAgendado));
	}
	
	@Test
	public void testVerificarMaterialAnaliseColetavelFalse() {
		final List<AelItemHorarioAgendado> listaHorarioAgendado = new ArrayList<AelItemHorarioAgendado>(); 
		final AelItemHorarioAgendadoId itemHorarioAgendadoId = new AelItemHorarioAgendadoId();
		itemHorarioAgendadoId.setHedGaeUnfSeq(Short.valueOf("311"));
		itemHorarioAgendadoId.setHedGaeSeqp(9);
		itemHorarioAgendadoId.setHedDthrAgenda(new Date());
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(itemHorarioAgendadoId);
				
		Mockito.when(mockedAelItemHorarioAgendadoDAO.pesquisarItemHorarioAgendadoItemSolicitacaoExameMaterialColetavel(
				Mockito.anyShort(), Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(listaHorarioAgendado);
		
		Assert.assertFalse(systemUnderTest.verificarMaterialAnaliseColetavel(itemHorarioAgendado));
	}
	
	@Test
	public void testAtualizarSituacaoColetaAmostra() {
		final List<AelAmostraItemExames> listaAmostraItemExame = new ArrayList<AelAmostraItemExames>();
		final AelItemHorarioAgendadoId itemHorarioAgendadoId = new AelItemHorarioAgendadoId();
		itemHorarioAgendadoId.setHedGaeUnfSeq(Short.valueOf("311"));
		itemHorarioAgendadoId.setHedGaeSeqp(9);
		itemHorarioAgendadoId.setHedDthrAgenda(new Date());
		final AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(itemHorarioAgendadoId);
		
		try {
			Mockito.when(mockedAelAmostraItemExamesDAO.pesquisarAmostraItemExameSituacaoGeradaPorItemHorarioAgendado(
					Mockito.anyShort(), Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(listaAmostraItemExame);

			systemUnderTest.atualizarSituacaoColetaAmostra(itemHorarioAgendado, NOME_MICROCOMPUTADOR);
			
		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
}
