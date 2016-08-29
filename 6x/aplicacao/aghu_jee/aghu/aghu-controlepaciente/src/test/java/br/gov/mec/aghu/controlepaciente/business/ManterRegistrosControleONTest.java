package br.gov.mec.aghu.controlepaciente.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe
 * ManterControlesPacienteON.<br>
 * 
 * 
 * @author ebicca
 * 
 */
public class ManterRegistrosControleONTest extends AGHUBaseUnitTest<ManterControlesPacienteON>{
	
	private static final Log log = LogFactory.getLog(ManterRegistrosControleONTest.class);

    @Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade; 	
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void testValidacaoServidorAlteracaoOK() {
		final RapServidores servidor = new RapServidores(new RapServidoresId(1,(short) 1));
		EcpControlePaciente controlePaciente = new EcpControlePaciente();
		controlePaciente.setServidor(servidor);
		
		try {
			whenObterServidorLogado();
			this.systemUnderTest.validaServidorAlteracao(controlePaciente);
			assert true;
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testValidacaoServidorAlteracaoErro() {
		final RapServidores servidor = new RapServidores(new RapServidoresId(2,(short) 2));
		EcpControlePaciente controlePaciente = new EcpControlePaciente();
		controlePaciente.setServidor(servidor);
		
		try {
			whenObterServidorLogadoNull();
			this.systemUnderTest.validaServidorAlteracao(controlePaciente);
			Assert.fail("Não deve permitir chegar aqui");
		} catch (BaseException e) {
			log.info("ManterRegistrosControleONTest.testValidacaoServidorAlteracaoErro(): " + e.getMessage());
			assert true;
		}
	}
	
    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
		
		Mockito.when(mockedRegistroColaboradorFacade.obterRapServidor(Mockito.any(RapServidoresId.class))).thenReturn(rap);
		}

    private void whenObterServidorLogadoNull() throws BaseException {
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(null);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(null);
		
		Mockito.when(mockedRegistroColaboradorFacade.obterRapServidor(Mockito.any(RapServidoresId.class))).thenReturn(null);
	}

}
