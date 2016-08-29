package br.gov.mec.aghu.casca.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.casca.business.ComponenteON.ComponenteONExceptionCode;
import br.gov.mec.aghu.casca.dao.ComponenteDAO;
import br.gov.mec.aghu.casca.dao.MetodoDAO;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ComponenteONTest extends AGHUBaseUnitTest<ComponenteON> {

	@Mock
	private ComponenteDAO mockedComponenteDAO;
	@Mock
	private MetodoDAO mockedMetodoDAO;

	@Test
	public void pesquisarComponentePorNome() {
		try {
			systemUnderTest.pesquisarComponentePorNome("Aplicacao Teste");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void pesquisarActionPorNome() {
		try {
			systemUnderTest.pesquisarActionPorNome("Aplicacao Teste", new Componente());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void pesquisarTodosComponentes() {
		try {
			systemUnderTest.pesquisarTodosComponentes();
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void pesquisarComponentes() {
		try {
			systemUnderTest.pesquisarComponentes(null, null, null, null, false);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void pesquisarMetodosComponenteCount() {
		try {
			systemUnderTest.pesquisarMetodosComponenteCount(null);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void pesquisarMetodosComponente() {
		try {
			systemUnderTest.pesquisarMetodosComponente(null, null, null, null, false);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void pesquisarComponentesCount() {
		try {
			systemUnderTest.pesquisarComponentesCount(null);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void salvarSemComponente() {
		try {
			systemUnderTest.salvar(null, new Aplicacao());
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), ComponenteONExceptionCode.CASCA_MENSAGEM_COMPONENTE_NAO_INFORMADA);
		}
	}

	@Test
	public void salvarSemAplicacao() {
		try {
			systemUnderTest.salvar(new Componente(), null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
	}

	@Test
	public void obterComponente() {
		try {
			systemUnderTest.obterComponente(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		try {
			systemUnderTest.obterComponente(1);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void salvarMetodo() {
		try {
			systemUnderTest.salvarMetodo(null, null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		try {
			systemUnderTest.salvarMetodo(null, 1);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		try {
			systemUnderTest.salvarMetodo(new Metodo(), null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		try {
			systemUnderTest.salvarMetodo(new Metodo(), 1);
		} catch (ApplicationBusinessException e) {
			Assert.fail();
		}
	}

	@Test
	public void obterMetodo1() throws ApplicationBusinessException {
		try {
			systemUnderTest.obterMetodo(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
			return;
		}
		Assert.fail();

	}

	@Test
	public void obterMetodo2() throws ApplicationBusinessException {
		try {
			systemUnderTest.obterMetodo(1);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void excluirMetodo1() throws ApplicationBusinessException {
		try {
			systemUnderTest.excluirMetodo(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
			return;
		}
		Assert.fail();

	}

	@Test
	public void excluirMetodo2() throws ApplicationBusinessException {
		try {
			systemUnderTest.excluirMetodo(1);
		} catch (Exception e) {
			Assert.fail();
		}
	}
}
