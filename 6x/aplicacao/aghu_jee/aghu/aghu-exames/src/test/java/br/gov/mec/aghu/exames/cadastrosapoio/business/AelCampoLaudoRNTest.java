package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.cadastrosapoio.business.AelCampoLaudoRN.AelCampoLaudoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelCampoLaudoRNTest extends AGHUBaseUnitTest<AelCampoLaudoRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelCampoLaudoDAO mockedAelCampoLaudoDAO;
	@Mock
	private AelParametroCamposLaudoDAO mockedAelParametroCamposLaudoDAO;
	@Mock
	private AnticoagulanteRN mockedAnticoagulanteRN;
	@Mock
	private AelSinonimoCampoLaudoRN mockedAelSinonimoCampoLaudoRN;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";

	
	/**
	 * Obtém instância padrão para os testes
	 * @return
	 */
	private AelCampoLaudo getDefaultInstance(){
		final AelCampoLaudo o = new AelCampoLaudo();
		return o;
	}
	
	
	@Test
	public void testExecutarRestricoesError01(){
		
		try {
			
			AelCampoLaudo campoLaudo = this.getDefaultInstance();

			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica = new AelGrupoResultadoCaracteristica();
			campoLaudo.setGrupoResultadoCaracteristica(grupoResultadoCaracteristica); // Teste
			
			AelGrupoResultadoCodificado grupoResultadoCodificado = new AelGrupoResultadoCodificado();
			campoLaudo.setGrupoResultadoCodificado(grupoResultadoCodificado); // Teste
			
			this.systemUnderTest.executarRestricoes(campoLaudo);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelCampoLaudoRNExceptionCode.AEL_00667);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), AelCampoLaudoRNExceptionCode.AEL_00667, e.getCode());
		}

	}
	
	@Test
	public void testExecutarRestricoesError02(){
		
		try {
			
			AelCampoLaudo campoLaudo = this.getDefaultInstance();

			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica = new AelGrupoResultadoCaracteristica();
			campoLaudo.setGrupoResultadoCaracteristica(grupoResultadoCaracteristica);
			
			campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.A); // Teste
			
			this.systemUnderTest.executarRestricoes(campoLaudo); 
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelCampoLaudoRNExceptionCode.AEL_00770);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), AelCampoLaudoRNExceptionCode.AEL_00770, e.getCode());
		}

	}
	
	@Test
	public void testExecutarRestricoesError03(){
		
		try {
			
			AelCampoLaudo campoLaudo = this.getDefaultInstance();

			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica = new AelGrupoResultadoCaracteristica();
			campoLaudo.setGrupoResultadoCaracteristica(grupoResultadoCaracteristica);
			
			// Teste
			campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.C);
			campoLaudo.setFluxo(null); 
			campoLaudo.setOrdem((short)10);
			
			this.systemUnderTest.executarRestricoes(campoLaudo); 
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelCampoLaudoRNExceptionCode.AEL_00885);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), AelCampoLaudoRNExceptionCode.AEL_00885, e.getCode());
		}

	}
	
	@Test
	public void testExecutarRestricoesError04(){
		
		try {
			
			AelCampoLaudo campoLaudo = this.getDefaultInstance();

			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica = new AelGrupoResultadoCaracteristica();
			campoLaudo.setGrupoResultadoCaracteristica(grupoResultadoCaracteristica);
			
			// Teste
			campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.C);
			campoLaudo.setFluxo(false);
			campoLaudo.setOrdem((short)10);
			
			this.systemUnderTest.executarRestricoes(campoLaudo);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelCampoLaudoRNExceptionCode.AEL_01047);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), AelCampoLaudoRNExceptionCode.AEL_01047, e.getCode());
		}

	}
	
	
	@Test
	public void testExecutarRestricoesError05(){
		
		try {
			
			AelCampoLaudo campoLaudo = this.getDefaultInstance();

			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica = new AelGrupoResultadoCaracteristica();
			campoLaudo.setGrupoResultadoCaracteristica(grupoResultadoCaracteristica);
			
			// Teste
			campoLaudo.setCancelaItemDept(true);
			campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.C);
			campoLaudo.setFluxo(false);
			campoLaudo.setOrdem((short)10);
			
			this.systemUnderTest.executarRestricoes(campoLaudo); // Teste
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelCampoLaudoRNExceptionCode.AEL_01252);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), AelCampoLaudoRNExceptionCode.AEL_01252, e.getCode());
		}

	}
	
	
	@Test
	public void testVerificarGrupoResultadoCaracteristicaAtivoError01(){
		
		try {
			
			AelCampoLaudo campoLaudo = this.getDefaultInstance();

			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica = new AelGrupoResultadoCaracteristica();
			grupoResultadoCaracteristica.setSituacao(DominioSituacao.I); // Teste!
			campoLaudo.setGrupoResultadoCaracteristica(grupoResultadoCaracteristica);
			
			this.systemUnderTest.verificarGrupoResultadoCaracteristicaAtivo(campoLaudo);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelCampoLaudoRNExceptionCode.AEL_00769);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), AelCampoLaudoRNExceptionCode.AEL_00769, e.getCode());
		}

	}
	
	@Test
	public void testVerificarGrupoResultadoCodificadoAtivoError01(){
		
		try {
			
			AelCampoLaudo campoLaudo = this.getDefaultInstance();

			AelGrupoResultadoCodificado grupoResultadoCodificado = new AelGrupoResultadoCodificado();
			grupoResultadoCodificado.setSituacao(DominioSituacao.I); // Teste!
			campoLaudo.setGrupoResultadoCodificado(grupoResultadoCodificado);
			
			
			this.systemUnderTest.verificarGrupoResultadoCodificadoAtivo(campoLaudo);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelCampoLaudoRNExceptionCode.AEL_00692);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), AelCampoLaudoRNExceptionCode.AEL_00692, e.getCode());
		}

	}

	
	@Test
	public void testVerificarCampoLaudoTextoFixoUnicoError01(){
		
		try {
			
			AelCampoLaudo campoLaudo = this.getDefaultInstance();

			campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.T); // Teste
			
			Mockito.when(mockedAelCampoLaudoDAO.existeCampoLaudoAtivoTipoCampoTextoFixo()).thenReturn(Boolean.TRUE);
			
			this.systemUnderTest.verificarCampoLaudoTextoFixoUnico(campoLaudo);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelCampoLaudoRNExceptionCode.AEL_01063);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), AelCampoLaudoRNExceptionCode.AEL_01063, e.getCode());
		}

	}

	
}
