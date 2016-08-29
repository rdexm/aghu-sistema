package br.gov.mec.aghu.exames.cadastrosapoio.business;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.AelServidorUnidAssinaElet;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelServidorUnidAssinaEletRNTest extends AGHUBaseUnitTest<AelServidorUnidAssinaEletRN>{
	
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inciar() throws BaseException{
    	whenObterServidorLogado();
    }
	
	@Test
	public void verificarAeltSuaBri() throws ApplicationBusinessException {
		AelServidorUnidAssinaElet entidade = new AelServidorUnidAssinaElet();		
		systemUnderTest.aeltSuaBri(entidade);
		Assert.assertNotNull(entidade.getAlteradoEm());
		Assert.assertNotNull(entidade.getServidorCriado());
		Assert.assertNotNull(entidade.getServidorAlterado());
	}
	
	@Test
	public void verificarAeltSuaBru() throws ApplicationBusinessException {
		AelServidorUnidAssinaElet entidade = new AelServidorUnidAssinaElet();		
		systemUnderTest.aeltSuaBru(entidade);
		Assert.assertNotNull(entidade.getAlteradoEm());
		Assert.assertNotNull(entidade.getServidorAlterado());
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
