package br.gov.mec.aghu.ambulatorio.business;

import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.business.MarcacaoConsultaRN.MarcacaoConsultaRNExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamExtratoControlesDAO;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamExtratoControles;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MarcacaoConsultaRNTest extends AGHUBaseUnitTest<MarcacaoConsultaRN>{

	@Mock
	private MamExtratoControlesDAO mockedmamExtratoControlesDAO;
	@Mock
	private AacConsultasDAO mockedAacConsultasDAO;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;	
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
		
		
	@Test
	public void preAtualizarControles() throws BaseException, UnknownHostException {
		MamControles controle = new MamControles();
		
		whenObterServidorLogado();
			
		systemUnderTest.preAtualizarControles(controle, NOME_MICROCOMPUTADOR);
		
		Assert.assertNotNull(controle.getServidorAtualiza());
		Assert.assertNotNull(controle.getServidorReponsavel());
	}
	
	@Test
	public void preAtualizarControlesException() throws BaseException {
		MamControles controle = new MamControles();
		try {
			whenObterServidorLogadoNull();
			
			systemUnderTest.preAtualizarControles(controle, NOME_MICROCOMPUTADOR);
			Assert.fail("Deveria ter lançado exceção AIP_USUARIO_NAO_CADASTRADO");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("Exceção lançada deveria ser a AIP_USUARIO_NAO_CADASTRADO", e.getCode(),MarcacaoConsultaRNExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		}
	}
	
	@Test
	public void inserirExtratoControles() throws BaseException {
		MamExtratoControles extrato = new MamExtratoControles();
		
		whenObterServidorLogado();
		
		systemUnderTest.inserirExtratoControles(extrato, NOME_MICROCOMPUTADOR);
		Assert.assertNotNull(extrato.getServidor());
		Assert.assertNotNull(extrato.getCriadoEm());
		
	}
	
	@Test
	public void inserirExtratoControlesException() throws BaseException {
		MamExtratoControles extrato = new MamExtratoControles();
		try {
			whenObterServidorLogadoNull();
			
			systemUnderTest.inserirExtratoControles(extrato, NOME_MICROCOMPUTADOR);
			Assert.fail("Deveria ter lançado exceção AIP_USUARIO_NAO_CADASTRADO");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("Exceção lançada deveria ser a AIP_USUARIO_NAO_CADASTRADO", e.getCode(),MarcacaoConsultaRNExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		}
	}

    private void whenObterServidorLogadoNull() throws BaseException {
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(null);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(null);
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
