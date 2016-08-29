package br.gov.mec.aghu.exames.business;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.AghAtendimentosPacExternRN.AghAtendimentosPacExternRNExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class AghAtendimentosPacExternRNTest extends AGHUBaseUnitTest<AghAtendimentosPacExternRN>{
	
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	
	
	@Test
	public void testVerificarConvenioAtivoConvenioNaoExistente() {
		Mockito.when(mockedFaturamentoFacade.obterFatConvenioSaudePlanoPorChavePrimaria(Mockito.any(FatConvenioSaudePlanoId.class))).thenReturn(null);
		
		FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		try {
			systemUnderTest.verificarConvenioAtivo(convenioSaudePlano);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(AghAtendimentosPacExternRNExceptionCode.AGH_00331 == e.getCode());
		}
	}
	
	@Test
	public void testVerificarConvenioAtivoConvenioExistenteSituacaoNaoAtiva() {
		final FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		convenioSaudePlano.setIndSituacao(DominioSituacao.I);
		
		Mockito.when(mockedFaturamentoFacade.obterFatConvenioSaudePlanoPorChavePrimaria(Mockito.any(FatConvenioSaudePlanoId.class))).thenReturn(convenioSaudePlano);

		try {
			systemUnderTest.verificarConvenioAtivo(convenioSaudePlano);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(AghAtendimentosPacExternRNExceptionCode.AEL_00332 == e.getCode());
		}
	}
	
	@Test
	public void testVerificarConvenioAtivoConvenioExistenteSituacaoAtiva() {
		final FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		convenioSaudePlano.setIndSituacao(DominioSituacao.A);
		
		Mockito.when(mockedFaturamentoFacade.obterFatConvenioSaudePlanoPorChavePrimaria(Mockito.any(FatConvenioSaudePlanoId.class))).thenReturn(convenioSaudePlano);
		
		try {
			systemUnderTest.verificarConvenioAtivo(convenioSaudePlano);
		} catch (BaseException e) {
			Assert.fail();
		}
	}
	
}
