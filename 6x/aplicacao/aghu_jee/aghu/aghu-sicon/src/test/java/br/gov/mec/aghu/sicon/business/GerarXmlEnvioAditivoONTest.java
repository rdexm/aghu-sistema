package br.gov.mec.aghu.sicon.business;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoAditivo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAditContratoId;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.util.Cnet;
import br.gov.mec.aghu.sicon.util.ObjectFactory;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GerarXmlEnvioAditivoONTest extends AGHUBaseUnitTest<GerarXmlEnvioAditivoON> {

	private static final Log log = LogFactory.getLog(GerarXmlEnvioAditivoONTest.class);

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private ObjectFactory mockedObjectFactory;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	
	@Test
	public void gerarXmlTest() throws ApplicationBusinessException {
		final ScoAditContrato aditContrato = new ScoAditContrato();
		// final ScoContrato contrato = new ScoContrato();
		final AghParametros url = new AghParametros();
		final RapServidores servidor = new RapServidores();
		
		url.setVlrTexto("");
		RapPessoasFisicas rapPessoasFisicas = new RapPessoasFisicas(0);
		rapPessoasFisicas.setCpf(0L);
		servidor.setPessoaFisica(rapPessoasFisicas);

		aditContrato.setTipoContratoSicon(new ScoTipoContratoSicon());
		aditContrato.getTipoContratoSicon().setCodigoSicon(0);
		aditContrato.setId(new ScoAditContratoId());
		aditContrato.getId().setSeq(0);
		aditContrato.setDataAssinatura(new Date());
		aditContrato.setCont(new ScoContrato());
		aditContrato.getCont().setNrContrato(0L);
		aditContrato.getCont().setSeq(0);
		aditContrato.setDataPublicacao(new Date());
		aditContrato.setObjetoContrato("");
		aditContrato.getCont().setFundamentoLegal("");
		aditContrato.setDtInicioVigencia(new Date());
		aditContrato.setDtFimVigencia(new Date());
		aditContrato.setVlAditivado(BigDecimal.ZERO);
		aditContrato.setIndTipoAditivo(DominioTipoAditivo.A);

		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_AMB_INTEGRACAO_SICON)).thenReturn(url);
			
			whenObterServidorLogado();
		} catch (BaseException e1) {
			log.error(e1.getMessage());
		}

		GerarXmlEnvioAditivoON spy = PowerMockito.spy(systemUnderTest);	    	 
   	 	PowerMockito.when(mockedObjectFactory.createCnet()).thenReturn(new Cnet());
		
		Mockito.when(mockedAghuFacade.getUasg()).thenReturn(0);

		Mockito.when(mockedObjectFactory.createCnet()).thenReturn(new Cnet());

		try {
			ScoAditContrato adit = new ScoAditContrato();
			ScoContrato contrato = new ScoContrato(); 
			contrato.setNrContrato(0l);
			contrato.setDtAssinatura(new Date());
			
			adit.setId(new ScoAditContratoId(1, 1));
			adit.setCont(contrato);
			
			spy.gerarXml(adit, "senha");
		} catch (ApplicationBusinessException e) {
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}

	}

    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		pf.setCpf(99981831034l);
		rap.setPessoaFisica(pf);
		
		
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogadoSemCache()).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);	
    }

}
