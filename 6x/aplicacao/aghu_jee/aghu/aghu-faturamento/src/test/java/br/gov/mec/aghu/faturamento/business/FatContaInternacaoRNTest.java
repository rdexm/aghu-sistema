package br.gov.mec.aghu.faturamento.business;

import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FatContaInternacaoRNTest extends AGHUBaseUnitTest<FatContaInternacaoRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private ObjetosOracleDAO mockedObjetosOracleDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	final Log log = LogFactory.getLog(this.getClass());
	Date data = new Date();

	@Before
	public void inciar() throws BaseException {
		whenObterServidorLogado();
	}
	
	@Test
	public void testPosAtualizacao() throws BaseException {
		FatContasInternacao entidade = new FatContasInternacao();
		boolean result = false;
		try {
			result = systemUnderTest.posAtualizacao(entidade, entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}		
	}

	@Test
	public void testPosInsercao() throws BaseException {
		FatContasInternacao entidade = new FatContasInternacao();
		boolean result = false;
		try {
			result = systemUnderTest.posInsercao(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}

	@Test
	public void testPosRemocao() throws BaseException {
		FatContasInternacao entidade = new FatContasInternacao();
		boolean result = false;
		try {
			result = systemUnderTest.posRemocao(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}

	@Test
	public void testPreAtualizacao() throws BaseException {
		FatContasInternacao entidade = new FatContasInternacao();
		boolean result = false;
		try {
			result = systemUnderTest.preAtualizacao(entidade, entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}

	@Test
	public void testPreInsercao() throws BaseException {
		FatContasInternacao entidade = new FatContasInternacao();
		boolean result = false;
		try {
			result = systemUnderTest.preInsercao(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}

	@Test
	public void testPreRemocao()throws BaseException {
		FatContasInternacao entidade = new FatContasInternacao();
		boolean result = false;
		try {
			result = systemUnderTest.preRemocao(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
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
