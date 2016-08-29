package br.gov.mec.aghu.exames.contratualizacao.business;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.contratualizacao.business.InformacoesProcessamentoArquivosON.InformacoesProcessamentoArquivosONExceptionCode;
import br.gov.mec.aghu.exames.contratualizacao.util.Detalhes;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.exames.contratualizacao.util.SolicitacaoExame;
import br.gov.mec.aghu.exames.contratualizacao.util.Solicitacoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class InformacoesProcessamentoArquivosONTest extends AGHUBaseUnitTest<InformacoesProcessamentoArquivosON>{

    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inciar() {
    	try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
    }
    
	@Test
	public void testVerificarDetalhesNulos() {
		Detalhes detalhes = null;

		try {
			systemUnderTest.validarDetalhes(detalhes);
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_DETALHES_NULO);
		}
		detalhes = new Detalhes();
		try {
			systemUnderTest.validarDetalhes(detalhes);
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_DETALHES_NULO);
		}
		detalhes.setHeader(new Header());
		try {
			systemUnderTest.validarDetalhes(detalhes);
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_DETALHES_NULO);
		}
	}
	
	@Test
	public void testVerificarDetalhesOk() {
		Detalhes detalhes = new Detalhes();
		Header header = new Header();
		detalhes.setHeader(header);
		Solicitacoes solicitacoes = new Solicitacoes();
		solicitacoes.getSolicitacaoExame().add(new SolicitacaoExame());
		detalhes.setSolicitacoes(solicitacoes);
		try {
			systemUnderTest.validarDetalhes(detalhes);
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
	

	@Test
	public void testVerificarServidorOk() {
		try {
			systemUnderTest.validarServidorLogado();
		} catch (BaseException e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testVerificarArquivosEntradaNulos() {
		try {
			systemUnderTest.validarNomesArquivos(null, null, "aa");
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_NOME_ARQUIVO_ENTRADA);
		}
		try {
			systemUnderTest.validarNomesArquivos("aa", null, "aa");
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_NOME_ARQUIVO_ENTRADA);
		}
		try {
			systemUnderTest.validarNomesArquivos(null, "aa", "aa");
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_NOME_ARQUIVO_ENTRADA);
		}
		try {
			systemUnderTest.validarNomesArquivos("", "", "aa");
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_NOME_ARQUIVO_ENTRADA);
		}
		try {
			systemUnderTest.validarNomesArquivos("aa", "", "aa");
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_NOME_ARQUIVO_ENTRADA);
		}
		try {
			systemUnderTest.validarNomesArquivos("", "aa", "aa");
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_NOME_ARQUIVO_ENTRADA);
		}
	}
	
	@Test
	public void testVerificarArquivosOk() {
		try {
			systemUnderTest.validarNomesArquivos("aa", "aa", "aa");
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testVerificarArquivosSaidaNulo() {
		try {
			systemUnderTest.validarNomesArquivos("aa", "aa", null);
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_NOME_ARQUIVO_SAIDA);
		}
		try {
			systemUnderTest.validarNomesArquivos("aa", "aa", "");
			fail("Nao deveria ter passado");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_NOME_ARQUIVO_SAIDA);
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
