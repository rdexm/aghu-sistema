package br.gov.mec.aghu.exames.pesquisa.business;
import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.dao.AelHorarioRotinaColetasDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.model.AbsCandidatosDoadores;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MonitorarColetasEmergenciaRNTest extends AGHUBaseUnitTest<MonitorarColetasEmergenciaRN>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private VAelSolicAtendsDAO mockedVAelSolicAtendsDAO;
	@Mock
	private AelHorarioRotinaColetasDAO mockedAelHorarioRotinaColetasDAO;
	@Mock
	private AelItemSolicitacaoExameDAO mockedAelItemSolicitacaoExameDAO;
	@Mock
	private AelTipoAmostraExameDAO mockedAelTipoAmostraExameDAO;
	
	@Test
	public void teste001() throws ApplicationBusinessException {
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(Integer.MIN_VALUE);
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExames.setAtendimento(atendimento);

		DominioOrigemAtendimento resultado = systemUnderTest.validaLaudoOrigemPaciente(solicitacaoExames);
		Assert.assertEquals(resultado, DominioOrigemAtendimento.A);
	}
	
	@Test
	public void teste002() throws ApplicationBusinessException {
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(Integer.MIN_VALUE);
		solicitacaoExames.setAtendimento(atendimento);
		
		DominioOrigemAtendimento resultado = systemUnderTest.validaLaudoOrigemPaciente(solicitacaoExames);
		Assert.assertNull(resultado);
	}
	
	@Test
	public void teste003() throws ApplicationBusinessException {
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		AelAtendimentoDiversos atendimentoDiverso = new AelAtendimentoDiversos();
		solicitacaoExames.setAtendimentoDiverso(atendimentoDiverso);

		DominioOrigemAtendimento resultado = systemUnderTest.validaLaudoOrigemPaciente(solicitacaoExames);
		Assert.assertNull(resultado);
	}
	
	
	@Test
	public void teste004() throws ApplicationBusinessException {
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		AelAtendimentoDiversos atendimentoDiverso = new AelAtendimentoDiversos();
		atendimentoDiverso.setAbsCandidatosDoadores(null);
		solicitacaoExames.setAtendimentoDiverso(atendimentoDiverso);

		DominioOrigemAtendimento resultado = systemUnderTest.validaLaudoOrigemPaciente(solicitacaoExames);
		Assert.assertNull(resultado);
	}
	
	@Test
	public void teste005() throws ApplicationBusinessException {
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		AelAtendimentoDiversos atendimentoDiverso = new AelAtendimentoDiversos();
		AbsCandidatosDoadores cadSeq = new AbsCandidatosDoadores();
		cadSeq.setSeq(Integer.MIN_VALUE);
		atendimentoDiverso.setAbsCandidatosDoadores(cadSeq); 
		solicitacaoExames.setAtendimentoDiverso(atendimentoDiverso);

		DominioOrigemAtendimento resultado = systemUnderTest.validaLaudoOrigemPaciente(solicitacaoExames);
		Assert.assertEquals(resultado, DominioOrigemAtendimento.D);
	}
	
	
	@Test
	public void teste006() throws ApplicationBusinessException {
		
		AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais();
		
		Mockito.when(mockedAelHorarioRotinaColetasDAO.obterAelHorarioRotinaColetasDAO(Mockito.any(AghUnidadesFuncionais.class), Mockito.any(AelItemSolicitacaoExames.class)))
		.thenReturn(Boolean.TRUE);
		
		boolean resultado = systemUnderTest.validaHorarioColeta(unidadeExecutora, itemSolicitacaoExame);
		Assert.assertEquals(resultado, Boolean.TRUE);
	}
	
	@Test
	public void teste007() throws ApplicationBusinessException {
		
		AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais();
		
		Mockito.when(mockedAelHorarioRotinaColetasDAO.obterAelHorarioRotinaColetasDAO(Mockito.any(AghUnidadesFuncionais.class), Mockito.any(AelItemSolicitacaoExames.class)))
		.thenReturn(Boolean.FALSE);

		boolean resultado = systemUnderTest.validaHorarioColeta(unidadeExecutora, itemSolicitacaoExame);
		Assert.assertEquals(resultado, Boolean.FALSE);
	}
	
	
	@Test
	public void teste008() throws BaseException {
		
		
		final AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ZERO);
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
		.thenReturn(parametro);

		Date resultado = systemUnderTest.obterDataCalculadaAparecimentoSolicitacao();
		Assert.assertNotNull(resultado);
		
	}
	
	/*@Test
	public void teste009() throws BaseException {
		
		mockingContext.checking(new Expectations() {{
			oneOf(mockedParametroFacade).buscarAghParametro(with(any(AghuParametrosEnum.class))); 
			will(returnValue(null));
		}});

		try{
			systemUnderTest.obterDataCalculadaAparecimentoSolicitacao();
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ERRO_P_AVISO_COLETA_PROGRAMADA, e.getCode(), ERRO_P_AVISO_COLETA_PROGRAMADA);
		}
		
	}*/
	
	@Test
	public void teste010() throws BaseException {
		
		final AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("AC");
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
		.thenReturn(parametro);

		String resultado = systemUnderTest.obterSituacaoExameColetado();
		Assert.assertEquals(resultado, "AC");
	
	}	
	
	/*@Test
	public void teste011() throws BaseException {
		
		mockingContext.checking(new Expectations() {{
			oneOf(mockedParametroFacade).buscarAghParametro(with(any(AghuParametrosEnum.class))); 
			will(returnValue(null));
		}});

		try{
			systemUnderTest.obterSituacaoExameColetado();
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+ERRO_P_SITUACAO_A_COLETAR, e.getCode(), ERRO_P_SITUACAO_A_COLETAR);
		}
		
	}	
	*/
}
