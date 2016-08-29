package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class VinculoRNTest extends AGHUBaseUnitTest<VinculoRN> {

    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void gerarCodStarh() throws ApplicationBusinessException {
		final Integer parametroInicial = Integer.valueOf("116000");
		final Integer parametroFinal = parametroInicial + Integer.valueOf("100000");

		try {
			whenObterServidorLogado();
		} catch (BaseException e) {

		}
		
		systemUnderTest.gerarCodStarh(parametroInicial, parametroFinal);

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
