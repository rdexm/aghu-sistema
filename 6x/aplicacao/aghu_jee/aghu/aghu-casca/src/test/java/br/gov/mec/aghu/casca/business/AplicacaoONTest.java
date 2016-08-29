package br.gov.mec.aghu.casca.business;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.casca.business.AplicacaoON.AplicacaoONExceptionCode;
import br.gov.mec.aghu.casca.dao.AplicacaoDAO;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AplicacaoONTest extends AGHUBaseUnitTest<AplicacaoON> {

	@Mock
	private AplicacaoDAO mockedAplicacaoDAO;

	String nomeAplicacaoExistente = "Nome Existente";
	String nomeAplicacaoNaoExistente = "Nome não Existente";

	@Test
	public void pesquisarAplicacaoPorNome() {
		try {
			systemUnderTest.pesquisarAplicacaoPorNome("Aplicacao Teste");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void salvarAplicacaoInternaComNomeExistente() {
		try {
			Mockito.when(mockedAplicacaoDAO.pesquisarAplicacoes(null, null, null, nomeAplicacaoExistente, false)).thenReturn(
					getAplicacoesTeste());
			Aplicacao aplicacao = criaAplicacaoInternaOK();
			aplicacao.setNome(nomeAplicacaoExistente);
			systemUnderTest.salvar(aplicacao);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_JA_CADASTRADA, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void salvarAplicacaoExternaComNomeExistente() {
		try {
			Mockito.when(
					mockedAplicacaoDAO.pesquisarAplicacoes(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(),
							Mockito.anyBoolean())).thenReturn(getAplicacoesTeste());
			Aplicacao aplicacao = criaAplicacaoExternaOK();
			systemUnderTest.salvar(aplicacao);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_JA_CADASTRADA, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void salvarAplicacaoExternaSemPorta() {
		try {
			Aplicacao aplicacao = criaAplicacaoExternaOK();
			aplicacao.setPorta(null);
			systemUnderTest.salvar(aplicacao);
		} catch (ApplicationBusinessException e) {
			return;
		}
	}

	@Test
	public void salvarAplicacaoExternaSemServidor() {
		try {
			Aplicacao aplicacao = criaAplicacaoExternaOK();
			aplicacao.setServidor(null);
			systemUnderTest.salvar(aplicacao);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_SEM_SERVIDOR, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void salvarAplicacaoExternaSemContexto() {
		try {
			Aplicacao aplicacao = criaAplicacaoExternaOK();
			aplicacao.setContexto(null);
			systemUnderTest.salvar(aplicacao);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_SEM_CONTEXTO, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void salvarAplicacaoInternaSemNome() {
		try {
			Aplicacao aplicacao = criaAplicacaoInternaOK();
			aplicacao.setNome(null);
			systemUnderTest.salvar(aplicacao);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_SEM_NOME, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void salvarAplicacaoNaoInformada() {
		try {
			systemUnderTest.salvar(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_NAO_INFORMADA, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void obterAplicacaoIdNaoInformada() {
		try {
			systemUnderTest.obterAplicacaoParaExclusao(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(AplicacaoONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void excluirAplicacaoIdNaoInformado() {
		try {
			systemUnderTest.excluirAplicacao(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(AplicacaoONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO, e.getCode());
			return;
		}
		Assert.fail();
	}

	private Aplicacao criaAplicacaoExternaOK() {
		Aplicacao aplicacao = new Aplicacao();
		aplicacao.setExterno(true);
		aplicacao.setNome("teste");
		aplicacao.setDescricao("teste");
		aplicacao.setPorta(8080);
		aplicacao.setContexto("teste");
		aplicacao.setServidor("teste");
		return aplicacao;
	}

	private Aplicacao criaAplicacaoInternaOK() {
		Aplicacao aplicacao = new Aplicacao();
		aplicacao.setExterno(false);
		aplicacao.setNome("teste");
		aplicacao.setDescricao("teste");
		return aplicacao;
	}
	
	private List<Aplicacao> getAplicacoesTeste() {
		Aplicacao app1 = new Aplicacao();
		Aplicacao app2 = new Aplicacao();
		Aplicacao app3 = new Aplicacao();
		return Arrays.asList(new Aplicacao[] { app1, app2, app3 });
	}

}
