package br.gov.mec.aghu.casca.business;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.casca.business.PerfilON.PerfilONExceptionCode;
import br.gov.mec.aghu.casca.dao.PerfilDAO;
import br.gov.mec.aghu.casca.dao.PerfilJnDAO;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfilJn;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class PerfilONTest extends AGHUBaseUnitTest<PerfilON> {

	@Mock
	private PerfilDAO mockedPerfilDao;
	@Mock
	private PerfilJnDAO mockedPerfilJnDao;
	@Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	@Before
	public void iniciar() throws BaseException {
		whenObterServidorLogado();
	}

	@Test
	public void salvarPerfilNulo() {
		try {
			systemUnderTest.salvarPerfil(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
			return;
		}
		Assert.fail();
	}

	@Test
	public void salvarPerfilNomeExistente() {
		Perfil perfil = new Perfil();
		perfil.setNome("teste");
		Mockito.when(mockedPerfilDao.pesquisarPerfil(Mockito.anyString())).thenReturn(new Perfil());
		try {
			systemUnderTest.salvarPerfil(perfil);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_EXISTENTE);
			return;
		}
		Assert.fail();
	}

	@Test
	public void salvarPerfilIdExistente() {
		Perfil perfilNovo = new Perfil();
		perfilNovo.setId(1);

		Perfil result = new Perfil();
		result.setId(2);
		Mockito.when(mockedPerfilDao.pesquisarPerfil(Mockito.anyString())).thenReturn(result);

		try {
			systemUnderTest.salvarPerfil(perfilNovo);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_EXISTENTE);
			return;
		}
		Assert.fail();
	}

	@Test
	public void pesquisarPerfis() throws ApplicationBusinessException {
		try {
			systemUnderTest.pesquisarPerfis("teste");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	public void pesquisarPerfisSuggestionBox() throws ApplicationBusinessException {
		try {
			systemUnderTest.pesquisarPerfisSuggestionBox("teste");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void pesquisarPerfisSemParametro() {
		try {
			systemUnderTest.pesquisarPerfis(new ArrayList<String>());
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
			return;
		}
		Assert.fail();
	}

	@Test
	public void pesquisarPerfisList() throws ApplicationBusinessException {
		try {
			ArrayList<String> perfis = new ArrayList<String>();
			perfis.add("");
			systemUnderTest.pesquisarPerfis(perfis);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void pesquisarPerfisCount() {
		try {
			systemUnderTest.pesquisarPerfisCount("teste");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void obterPerfilParametroNaoInformado() {
		try {
			systemUnderTest.obterPerfil(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void obterPerfilNaoEncontrado() {
		try {
			systemUnderTest.obterPerfil(1);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_NAO_ENCONTRADO, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void obterPerfil() {
		Mockito.when(mockedPerfilDao.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(new Perfil());

		try {
			systemUnderTest.obterPerfil(1);
		} catch (Exception e) {
			Assert.fail();
		}

	}

	@Test
	public void excluirPerfilParametroNaoInformado() {
		try {
			systemUnderTest.excluirPerfil(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void excuirPerfilNaoEncontrado() {
		try {
			systemUnderTest.excluirPerfil(1);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(PerfilONExceptionCode.CASCA_MENSAGEM_PERFIL_NAO_ENCONTRADO, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void obterNomePerfisPorUsuario() {
		try {
			systemUnderTest.obterNomePerfisPorUsuario("teste");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void associarPermissoesPerfilParametrosNaoInformados1() {
		try {
			systemUnderTest.associarPermissoesPerfil(1, null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, e.getCode());
			return;
		}
		Assert.fail();
	}

	@Test
	public void associarPermissoesPerfilParametrosNaoInformados2() {
		try {
			systemUnderTest.associarPermissoesPerfil(null, new ArrayList<Permissao>());
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, e.getCode());
			return;
		}
		Assert.fail();
	}

	public void associarPermissoesPerfilParametrosNaoInformados3() {
		try {
			systemUnderTest.associarPermissoesPerfil(null, null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(PerfilONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, e.getCode());
			return;
		}
		Assert.fail();
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