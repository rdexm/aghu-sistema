package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business;

import org.junit.Assert;
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

public class ManterLimiteItemControleONTest extends AGHUBaseUnitTest<ManterLimiteItemControleON>{

    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void inserirLimiteItemControleNulo() {
		try {
			whenObterServidorLogado();
			
			systemUnderTest.inserirLimiteItemControle(null);
			Assert.fail("Deveria ter ocorrido a exceção PARAMETRO_OBRIGATORIO");

		} catch (BaseException e) {
			Assert.assertEquals(ManterLimiteItemControleON.ManterLimiteItemControleONExceptionCode.PARAMETRO_OBRIGATORIO.toString(),
					e.getMessage());
		}
	}

	@Test
	public void alterarLimiteItemControleNulo() {
		try {
			systemUnderTest.alterarLimiteItemControle(null);
			Assert.fail("Deveria ter ocorrido a exceção PARAMETRO_OBRIGATORIO");

		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(ManterLimiteItemControleON.ManterLimiteItemControleONExceptionCode.PARAMETRO_OBRIGATORIO.toString(),
					e.getMessage());
		}
	}

	@Test
	public void excluirLimiteItemControleNulo() {
		try {
			systemUnderTest.excluir(null);
			Assert.fail("Deveria ter ocorrido a exceção PARAMETRO_OBRIGATORIO");

		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(ManterLimiteItemControleON.ManterLimiteItemControleONExceptionCode.PARAMETRO_OBRIGATORIO.toString(),
					e.getMessage());
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
