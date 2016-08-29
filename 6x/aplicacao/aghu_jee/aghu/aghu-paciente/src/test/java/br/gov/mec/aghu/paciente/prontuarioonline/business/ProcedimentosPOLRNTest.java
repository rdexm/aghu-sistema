package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ProcedimentosPOLRNTest extends AGHUBaseUnitTest<ProcedimentosPOLRN>{
	
	@Mock
	private IBlocoCirurgicoProcDiagTerapFacade mockedBlocoCirurgicoProcDiagTerapFacade;
	@Mock
	private IBlocoCirurgicoFacade mockedBlocoCirurgicoFacade;
	
	@Test
	public void verDescCirurgicaTest(){
		esperarPdtDescricaoDAOlistarDescricaoPorSeqCirurgiaSituacao();
		esperarMbcDescricaoCirurgicaDAOlistarDescricaoCirurgicaPorSeqCirurgiaSituacao();
		Boolean retorno = systemUnderTest.verDescCirurgica(1);
	}
	@Test
	public void verificarSeEscalaPortalAgendamentoTemCirurgiaTest() {
		esperarMbcCirurgiasDAOverificarSeEscalaPortalAgendamentoTemCirurgia();
		Boolean retorno = systemUnderTest.verificarSeEscalaPortalAgendamentoTemCirurgia(1,new Date());
		Assert.assertTrue(retorno);
	}
	
	private void esperarPdtDescricaoDAOlistarDescricaoPorSeqCirurgiaSituacao() {
		List<PdtDescricao> pdtDescricaoList = new ArrayList<PdtDescricao>();
		PdtDescricao pdtDesc = new PdtDescricao();
		pdtDesc.setSeq(10);
		pdtDescricaoList.add(pdtDesc);

		DominioSituacaoDescricao[] situacao = new DominioSituacaoDescricao[2];
		situacao[0] = DominioSituacaoDescricao.PRE;
		situacao[1] = DominioSituacaoDescricao.DEF;
		
		Mockito.when(mockedBlocoCirurgicoProcDiagTerapFacade.listarDescricaoPorSeqCirurgiaSituacao(1, situacao, PdtDescricao.Fields.SEQ)).thenReturn(pdtDescricaoList);
	}
	
	private void esperarMbcDescricaoCirurgicaDAOlistarDescricaoCirurgicaPorSeqCirurgiaSituacao() {
		List<MbcDescricaoCirurgica> mbcDescricaoCirurgicaList = new ArrayList<MbcDescricaoCirurgica>();
		MbcDescricaoCirurgica mbcDescCir = new MbcDescricaoCirurgica();
		mbcDescricaoCirurgicaList.add(mbcDescCir);

		Mockito.when(mockedBlocoCirurgicoFacade.listarDescricaoCirurgicaPorSeqCirurgiaSituacao(Mockito.anyInt(), Mockito.any(DominioSituacaoDescricaoCirurgia.class)))
		.thenReturn(mbcDescricaoCirurgicaList);
	}
	
	private void esperarMbcCirurgiasDAOverificarSeEscalaPortalAgendamentoTemCirurgia(){
		Mockito.when(mockedBlocoCirurgicoFacade.verificarSeEscalaPortalAgendamentoTemCirurgia(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(1l);
	}
}
