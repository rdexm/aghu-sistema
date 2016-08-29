package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.exames.cadastrosapoio.business.AelVersaoLaudoRN.AelVersaoLaudoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelCampoUsoFaturamentoDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelVersaoLaudoDAO;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelVersaoLaudoRNTest extends AGHUBaseUnitTest<AelVersaoLaudoRN>{

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelVersaoLaudoDAO mockedAelVersaoLaudoDAO;
	@Mock
	private AelCampoUsoFaturamentoDAO mockedAelCampoUsoFaturamentoDAO;
	@Mock
	private AelParametroCamposLaudoDAO mockedAelParametroCamposLaudoDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;


	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";

	@Before
	public void doBeforeEachTestCase() throws BaseException{
		whenObterServidorLogado();
	}

	@Test
	public void preInserirError01() {

		AelVersaoLaudo versaoLaudo = new AelVersaoLaudo();
		versaoLaudo.setSituacao(DominioSituacaoVersaoLaudo.I);

		try {
			systemUnderTest.preInserir(versaoLaudo);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00913);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00913, AelVersaoLaudoRNExceptionCode.AEL_00913, e.getCode());
		}

	}

	@Test
	public void preInserirSuccess01() {

		Mockito.when(mockedAelVersaoLaudoDAO.existeVersaoLaudoVerificarSituacaoDuplicada(Mockito.any(AelVersaoLaudo.class))).thenReturn(false);

		AelVersaoLaudo versaoLaudo = new AelVersaoLaudo();
		versaoLaudo.setSituacao(DominioSituacaoVersaoLaudo.E);

		try {
			systemUnderTest.preInserir(versaoLaudo);
			Assert.assertFalse(false);
		} catch (BaseException e) {
			Assert.fail(e.getCode().toString());
		} catch (Exception e) {
			Assert.fail(e.toString());
		}

	}
	
	
	@Test
	public void preAtualizarError01() {

		final AelVersaoLaudo versaoLaudo = new AelVersaoLaudo();
		final AelVersaoLaudo old = new AelVersaoLaudo();
		
		// Teste aqui
		versaoLaudo.setCriadoEm(new Date(100)); 
		old.setCriadoEm(new Date(200));

		try {
			systemUnderTest.preAtualizar(versaoLaudo, old);
			Assert.assertFalse(false);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00369);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00369, AelVersaoLaudoRNExceptionCode.AEL_00369, e.getCode());
		}

	}

	@Test
	public void preAtualizarError02() {

		final AelVersaoLaudo versaoLaudo = new AelVersaoLaudo();
		final AelVersaoLaudo old = new AelVersaoLaudo();
		
		// Passa aqui
		Date dataIgual = new Date();
		versaoLaudo.setCriadoEm(dataIgual); 
		old.setCriadoEm(dataIgual);
		
		// Passa aqui
		RapServidores servidor = new RapServidores();
		RapServidoresId idServidor = new RapServidoresId();
		idServidor.setMatricula(0);
		idServidor.setVinCodigo((short)1);
		servidor.setId(idServidor);
		versaoLaudo.setServidor(servidor);
		
		// Teste aqui
		RapServidores servidorOld = new RapServidores();
		RapServidoresId idServidorOld = new RapServidoresId();
		idServidorOld.setMatricula(1);
		idServidorOld.setVinCodigo((short)1);
		servidorOld.setId(idServidorOld);
		old.setServidor(servidorOld);

		try {
			systemUnderTest.preAtualizar(versaoLaudo, old);
			Assert.assertFalse(false);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00369);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00369, AelVersaoLaudoRNExceptionCode.AEL_00369, e.getCode());
		}

	}
	
	@Test
	public void preAtualizarError03() {

		final AelVersaoLaudo versaoLaudo = new AelVersaoLaudo();
		final AelVersaoLaudo old = new AelVersaoLaudo();
		
		// Passa aqui
		Date dataIgual = new Date();
		versaoLaudo.setCriadoEm(dataIgual); 
		old.setCriadoEm(dataIgual);
		
		// Passa aqui
		RapServidores servidor = new RapServidores(new RapServidoresId());
		
		versaoLaudo.setServidor(servidor);
		old.setServidor(servidor);
		
		// Teste aqui
		versaoLaudo.setNomeDesenho("A");
		old.setNomeDesenho("B");

		try {
			systemUnderTest.preAtualizar(versaoLaudo, old);
			Assert.assertFalse(false);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00773);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00773, AelVersaoLaudoRNExceptionCode.AEL_00773, e.getCode());
		}

	}
	
	@Test
	public void preAtualizarError04() {

		final AelVersaoLaudo versaoLaudo = new AelVersaoLaudo();
		final AelVersaoLaudo old = new AelVersaoLaudo();
		
		// Passa aqui
		Date dataIgual = new Date();
		versaoLaudo.setCriadoEm(dataIgual); 
		old.setCriadoEm(dataIgual);
		
		// Passa aqui
		RapServidores servidor = new RapServidores(new RapServidoresId());
		
		versaoLaudo.setServidor(servidor);
		old.setServidor(servidor);
		
		// Passa aqui
		versaoLaudo.setNomeDesenho("A");
		old.setNomeDesenho("A");
		
		// Teste aqui
		old.setSituacao(DominioSituacaoVersaoLaudo.I);
		versaoLaudo.setSituacao(DominioSituacaoVersaoLaudo.E);
		

		try {
			systemUnderTest.preAtualizar(versaoLaudo, old);
			Assert.assertFalse(false);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00914);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00914, AelVersaoLaudoRNExceptionCode.AEL_00914, e.getCode());
		}

	}
	
	@Test
	public void preAtualizarError05() {

		final AelVersaoLaudo versaoLaudo = new AelVersaoLaudo();
		final AelVersaoLaudo old = new AelVersaoLaudo();
		
		// Passa aqui
		Date dataIgual = new Date();
		versaoLaudo.setCriadoEm(dataIgual); 
		old.setCriadoEm(dataIgual);
		
		// Passa aqui
		RapServidores servidor = new RapServidores(new RapServidoresId());
		
		versaoLaudo.setServidor(servidor);
		old.setServidor(servidor);
		
		// Passa aqui
		versaoLaudo.setNomeDesenho("A");
		old.setNomeDesenho("A");
		
		// Teste aqui
		old.setSituacao(DominioSituacaoVersaoLaudo.E);
		versaoLaudo.setSituacao(DominioSituacaoVersaoLaudo.I);
		
		Mockito.when(mockedAelVersaoLaudoDAO.existeVersaoLaudoVerificarSituacaoDuplicada(Mockito.any(AelVersaoLaudo.class))).thenReturn(true);

		try {
			systemUnderTest.preAtualizar(versaoLaudo, old);
			Assert.assertFalse(false);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00914);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + AelVersaoLaudoRNExceptionCode.AEL_00914, AelVersaoLaudoRNExceptionCode.AEL_00914, e.getCode());
		}

	}

	private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }
}
