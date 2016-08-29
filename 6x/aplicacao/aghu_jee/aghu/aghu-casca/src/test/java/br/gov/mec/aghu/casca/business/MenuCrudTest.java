package br.gov.mec.aghu.casca.business;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.casca.business.MenucrudON.MenucrudONExceptionCode;
import br.gov.mec.aghu.casca.dao.MenuDAO;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * @author rafael
 * 
 */
public class MenuCrudTest extends AGHUBaseUnitTest<MenucrudON>{
	@Mock
	private MenuDAO mockedMenuDAO;

	@Test
	public void salvarMenu() {
		try {
			systemUnderTest.salvarMenu(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					MenucrudON.MenucrudONExceptionCode.CASCA_MENSAGEM_MENU_NAO_INFORMADO);
			return;
		}
		Assert.fail();
	}

	private List<Menu> criaListaMenu() {
		Menu menu1 = new Menu();
		Menu menu2 = new Menu();
		return Arrays.asList(new Menu[] { menu1, menu2 });

	}

	@Test
	public void salvarMenuNulo() {
		try {
			systemUnderTest.salvarMenu(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					MenucrudON.MenucrudONExceptionCode.CASCA_MENSAGEM_MENU_NAO_INFORMADO);
			return;
		}
		Assert.fail();
	}

	@Test
	public void salvarMenuExistente() {

		Mockito.when(mockedMenuDAO.validaCadastroMenu(Mockito.anyString(), Mockito.any(Aplicacao.class), Mockito.any(Menu.class), Mockito.anyString(),
				Mockito.anyInt())).thenReturn(criaListaMenu());

		try {
			Menu menu = new Menu();
			menu.setUrl("/cups/ativaVisualizaCups.xhtml");
			systemUnderTest.salvarMenu(menu);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					MenucrudON.MenucrudONExceptionCode.CASCA_MENSAGEM_MENU_EXISTENTE);
			return;
		}
		Assert.fail();
	}

	@Test
	public void salvarComUrlIncorreta1() {
		try {
			Menu menu = new Menu();
			menu.setUrl("");
			systemUnderTest.salvarMenu(menu);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					MenucrudON.MenucrudONExceptionCode.CASCA_MENSAGEM_URL_INVALIDA);
			return;
		}
		Assert.fail();
	}

	@Test
	public void salvarComUrlIncorreta2() {
		try {
			Menu menu = new Menu();
			menu.setUrl("aaaaaaaaaaaaaa");
			systemUnderTest.salvarMenu(menu);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					MenucrudON.MenucrudONExceptionCode.CASCA_MENSAGEM_URL_INVALIDA);
			return;
		}
		Assert.fail();
	}

	@Test
	public void pesquisarMenuPorNomeEId() throws ApplicationBusinessException {
		try {
			systemUnderTest.pesquisarMenuPorNomeEId(null);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	public void deletarMenuComIdNull() throws ApplicationBusinessException {
		try {
			systemUnderTest.deletarMenu(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					MenucrudONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
			return;
		}
		Assert.fail();
	}

	public void obterMenuComIdNull() throws ApplicationBusinessException {
		try {
			systemUnderTest.obterMenu(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					e.getCode(),
					MenucrudONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
			return;
		}
		Assert.fail();
	}

	@Test
	public void obterMenu() throws ApplicationBusinessException {
		Mockito.when(mockedMenuDAO.obterPorChavePrimaria(Mockito.anyInt(), Mockito.any(Enum.class))).thenReturn(new Menu());

		try {
			systemUnderTest.obterMenu(1);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}
