package br.gov.mec.aghu.prescricaomedica.modelobasico.business;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.VerificarPrescricaoONTest;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoPrescricaoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe ManterModeloBasicoON.<br>
 * Criado com base no teste {@link VerificarPrescricaoONTest}.
 * 
 *@author cvagheti
 * 
 */
public class ManterModeloBasicoONTest extends AGHUBaseUnitTest<ManterModeloBasicoON>{
	
	private static final Log log = LogFactory.getLog(ManterModeloBasicoONTest.class);
	
	@Mock
	private MpmModeloBasicoPrescricaoDAO mockedMpmModeloBasicoPrescricaoDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	/**
	 * Testa exclusão com sucesso.
	 */
	@Test
	public void excluirTest() {

		MpmModeloBasicoPrescricao modelo = new MpmModeloBasicoPrescricao(1);
		RapServidores servidor = new RapServidores();
		// RapServidores servidorDigitado = new RapServidores();

		servidor.setId(new RapServidoresId(1, (short) 1));
		// servidorDigitado.setId(new RapServidoresId(1, (short) 1));

		modelo.setServidor(servidor);
		// modelo.setServidorDigitado(servidorDigitado);

		Mockito.when(mockedMpmModeloBasicoPrescricaoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(modelo);

		try {
			whenObterServidorLogado();
			systemUnderTest.excluirModeloBasico(1);
		} catch (BaseException e) {

			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Testa a exclusão de um modelo com itens associados.
	 */
	@Test
	public void excluirExceptionTest() {

		MpmModeloBasicoPrescricao modelo = new MpmModeloBasicoPrescricao(1);
		RapServidores servidor = new RapServidores();
		RapServidores servidorDigitado = new RapServidores();

		servidor.setId(new RapServidoresId(1, (short) 1));
		servidorDigitado.setId(new RapServidoresId(1, (short) 1));

		modelo.setServidor(servidor);
		modelo.setServidorDigitado(servidorDigitado);

		Mockito.when(mockedMpmModeloBasicoPrescricaoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(modelo);

		try {
			whenObterServidorLogado();
			systemUnderTest.excluirModeloBasico(1);

		} catch (BaseException e) {

			Assert
					.assertEquals(
							ManterModeloBasicoON.ManterModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_POSSUI_ITENS_ASSOCIADOS,
							e.getCode());
		}

	}

	/**
	 * Testa exclusão de modelo não instanciado.
	 */
	@Test
	public void excluirModeloNuloTest() {

		try {
			whenObterServidorLogado();
			systemUnderTest.excluirModeloBasico(null);

			Assert.fail("Falha ao excluir - modelo nulo");
		} catch (IllegalArgumentException e) {

			log.error(e.getMessage());
		} catch (BaseException exp) {

			Assert.fail(exp.getMessage());
			exp.getStackTrace();
		}

	}

	/**
	 * Testa exclusão de modelo onde o servidor não é o servidor logado.
	 */
	@Test
	public void excluirDadosTest() {

		MpmModeloBasicoPrescricao modelo = new MpmModeloBasicoPrescricao(1);
		RapServidores servidor = new RapServidores();

		servidor.setId(new RapServidoresId(1, (short) 2));

		modelo.setServidor(servidor);

		try {
			whenObterServidorLogado();
			Mockito.when(mockedMpmModeloBasicoPrescricaoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(modelo);
			systemUnderTest.excluirModeloBasico(1);
			Assert
					.fail("Falha ao executar teste - premitiu excluir modelo de servidores diferentes");
		} catch (BaseException e) {

			log.error(e.getMessage());
			Assert
					.assertEquals(
							ManterModeloBasicoON.ManterModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_SOMENTE_SERVIDOR_EXCLUSIVO_EXCLUI,
							e.getCode());
		}

	}

	/**
	 * Teste para inclusão de modelo nulo.
	 */
	@Test
	public void incluirTest() {

		try {
			whenObterServidorLogado();
			systemUnderTest.incluir(null);
		} catch (BaseException e) {
			Assert
					.assertEquals(
							ManterModeloBasicoON.ManterModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_ERRO_PARAMETRO_OBRIGATORIO,
							e.getCode());
		}
	}

	/**
	 * Testa para inclusão de modelo em branco (sem descrição).
	 */
	@Test
	public void incluirBlankTest() {

		MpmModeloBasicoPrescricao modeloBasico = new MpmModeloBasicoPrescricao();

		modeloBasico.setDescricao("");

		try {
			whenObterServidorLogado();
			systemUnderTest.incluir(modeloBasico);
		} catch (BaseException e) {
			Assert
					.assertEquals(
							ManterModeloBasicoON.ManterModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_ERRO_PARAMETRO_OBRIGATORIO,
							e.getCode());
		}
	}

	/**
	 * Teste para alteração de modelo nulo.
	 */
	@Test
	public void alterarTest() {

		try {
			whenObterServidorLogado();
			systemUnderTest.alterar(null);
		} catch (IllegalArgumentException e) {

			log.error(e.getMessage());
		} catch (BaseException exp) {

			Assert
					.assertEquals(
							ManterModeloBasicoON.ManterModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_SOMENTE_SERVIDOR_EXCLUSIVO_ALTERA,
							exp.getCode());
		}
	}

	/**
	 * Teste para alterar modelo com servidores diferentes.
	 */
	@Test
	public void alterarServidorDiferenteTest() {

		MpmModeloBasicoPrescricao modeloBasico = new MpmModeloBasicoPrescricao();
		RapServidores servidor = new RapServidores();
		// RapServidores servidorDigitado = new RapServidores();

		servidor.setId(new RapServidoresId(1, (short) 2));
		// servidorDigitado.setId(new RapServidoresId(2, (short) 1));

		modeloBasico.setServidor(servidor);
		// modeloBasico.setServidorDigitado(servidorDigitado);

		try {
			whenObterServidorLogado();
			systemUnderTest.alterar(modeloBasico);
		} catch (BaseException e) {

			Assert
					.assertEquals(
							ManterModeloBasicoON.ManterModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_SOMENTE_SERVIDOR_EXCLUSIVO_ALTERA,
							e.getCode());
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
