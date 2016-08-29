package br.gov.mec.aghu.exames.business;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.AelValorNormalidCampoRN.AelValorNormalidCampoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelValorNormalidCampoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelValorNormalidCampo;
import br.gov.mec.aghu.model.AelValorNormalidCampoId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author amalmeida
 *
 */
public class AelValorNormalidCampoRNTest extends AGHUBaseUnitTest<AelValorNormalidCampoRN>{

	@Mock
	private AelValorNormalidCampoDAO mockedAelValorNormalidCampoDAO;
	@Mock
	private AelCampoLaudoDAO mockedAelCampoLaudoDAO;
	@Mock
	private AelParametroCamposLaudoDAO mockedAelParametroCamposLaudoDAO;
	
	
	@Test
	public void validaCampoLaudoTest() {
	
		final AelCampoLaudo campoLaudoOld = new AelCampoLaudo();
		campoLaudoOld.setSituacao(DominioSituacao.I);
		AelValorNormalidCampoId aelValorNormalidCampoId = new AelValorNormalidCampoId();
		aelValorNormalidCampoId.setCalSeq(2);
		AelValorNormalidCampo valorCampo = new AelValorNormalidCampo();
		valorCampo.setId(aelValorNormalidCampoId);
		
		Mockito.when(mockedAelCampoLaudoDAO.obterPorChavePrimaria(Mockito.any(AelValorNormalidCampoId.class))).thenReturn(campoLaudoOld);
		
		try {
			systemUnderTest.validaCampoLaudo(valorCampo);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(AelValorNormalidCampoRNExceptionCode.AEL_00782 == e.getCode());
		}
	}

	
	@Test
	public void validaValorMaxMinTest() {
	
		AelValorNormalidCampoId aelValorNormalidCampoId = new AelValorNormalidCampoId();
		aelValorNormalidCampoId.setCalSeq(2);
		AelValorNormalidCampo valorCampo = new AelValorNormalidCampo();
		valorCampo.setId(aelValorNormalidCampoId);
		valorCampo.setValorMinimo("34");
		valorCampo.setValorMaximo("36");
		
		
		try {
			systemUnderTest.validaValorMaxMin(valorCampo);
			
		} catch (BaseException e) {
			Assert.fail();
		}
	}


	@Test
	public void validaValorMaxMinAceitavelTest() {
	
		AelValorNormalidCampoId aelValorNormalidCampoId = new AelValorNormalidCampoId();
		aelValorNormalidCampoId.setCalSeq(2);
		AelValorNormalidCampo valorCampo = new AelValorNormalidCampo();
		valorCampo.setId(aelValorNormalidCampoId);
		valorCampo.setValorMinimoAceitavel("10");
		valorCampo.setValorMaximoAceitavel("8");
		
		
		try {
			systemUnderTest.validaValorMaxMinAceitavel(valorCampo);
			Assert.fail();
			
		} catch (BaseException e) {
			Assert.assertTrue(AelValorNormalidCampoRNExceptionCode.AEL_00878 == e.getCode());
		}
	}
	

	@Test
	public void validaValorMaxMinAbsurdoTest() {
	
		AelValorNormalidCampoId aelValorNormalidCampoId = new AelValorNormalidCampoId();
		aelValorNormalidCampoId.setCalSeq(2);
		AelValorNormalidCampo valorCampo = new AelValorNormalidCampo();
		valorCampo.setId(aelValorNormalidCampoId);
		valorCampo.setValorMinimoAbsurdo("10");
		valorCampo.setValorMaximoAbsurdo("8 test");
		
		
		try {
			systemUnderTest.validaValorMaxMinAbsurdo(valorCampo);
			
		} catch (BaseException e) {
			Assert.fail();

		}
	}
	
	@Test
	public void validaAlteracaoSituacaoTest() {
	
		AelValorNormalidCampo valorCampo = new AelValorNormalidCampo();
		valorCampo.setSituacao(DominioSituacao.A);
		
		AelValorNormalidCampo valorCampoOld = new AelValorNormalidCampo();
		valorCampoOld.setSituacao(DominioSituacao.I);
		
		
		try {
			systemUnderTest.validaAlteracaoSituacao(valorCampo, valorCampoOld);
			Assert.fail();
			
		} catch (BaseException e) {
			Assert.assertTrue(AelValorNormalidCampoRNExceptionCode.AEL_00883 == e.getCode());
		}
	}


	

}