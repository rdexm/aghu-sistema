package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.AelResultadosPadraoRN.AelResultadosPadraoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelResultadosPadraoDAO;
import br.gov.mec.aghu.exames.dao.AelVersaoLaudoDAO;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelResultadosPadraoRNTest extends AGHUBaseUnitTest<AelResultadosPadraoRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelResultadosPadraoDAO mockedAelResultadosPadraoDAO;
	@Mock
	private AelExamesMaterialAnaliseDAO mockedAelExamesMaterialAnaliseDAO;
	@Mock
	private AelVersaoLaudoDAO mockedAelVersaoLaudoDAO;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";
	
	/**
	 * Obtém instância padrão para os testes
	 * @return
	 */
	private AelResultadosPadrao getDefaultInstance(){
		final AelResultadosPadrao o = new AelResultadosPadrao();
		
		return o;
	}
	
	
	@Test
	public void testVerificarVersaoExameError01(){
		
		try {
			
			AelResultadosPadrao resultadosPadrao = this.getDefaultInstance();
			
			AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
			AelExamesMaterialAnaliseId id = new AelExamesMaterialAnaliseId();
			examesMaterialAnalise.setId(id);
			resultadosPadrao.setExameMaterialAnalise(examesMaterialAnalise);
			
			examesMaterialAnalise.setIndSituacao(DominioSituacao.I); // Teste	

			this.systemUnderTest.verificarVersaoExame(resultadosPadrao);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelResultadosPadraoRNExceptionCode.AEL_01552);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), AelResultadosPadraoRNExceptionCode.AEL_01552, e.getCode());
		}

	}
	
	@Test
	public void testVerificarVersaoExameError02(){
		
		try {
			
			AelResultadosPadrao resultadosPadrao = this.getDefaultInstance();
			
			AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
			AelExamesMaterialAnaliseId id = new AelExamesMaterialAnaliseId();
			examesMaterialAnalise.setId(id);
			resultadosPadrao.setExameMaterialAnalise(examesMaterialAnalise);
			
			examesMaterialAnalise.setIndSituacao(DominioSituacao.A); // Teste

			Mockito.when(mockedAelVersaoLaudoDAO.pesquisarVersaoLaudoPorExameMaterialAnalise(Mockito.anyString(), Mockito.anyInt()))
			.thenReturn(new LinkedList<AelVersaoLaudo>());

			this.systemUnderTest.verificarVersaoExame(resultadosPadrao);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelResultadosPadraoRNExceptionCode.AEL_01553);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), AelResultadosPadraoRNExceptionCode.AEL_01553, e.getCode());
		}

	}
	
	

}
