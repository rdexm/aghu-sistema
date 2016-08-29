package br.gov.mec.aghu.faturamento.business;

import static org.junit.Assert.fail;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.FatSaldoUti;
import br.gov.mec.aghu.model.FatSaldoUtiId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SaldoUtiRNTest extends AGHUBaseUnitTest<SaldoUtiRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	private Log log = LogFactory.getLog(this.getClass());
	final Date data = new Date(0L);

    @Mock
    private SaldoUtiAtualizacaoRN mockedSaldoUtiAtualizacaoRN;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inicio() throws BaseException {
    	whenObterServidorLogado();
    }

	@Test
	public void testBriPreInsercaoRowFatSaldoUti() {
		
		FatSaldoUti entidade = null;
		int cap = 0;
		
		//setup
		entidade = new FatSaldoUti();
		cap = 0;
		
		entidade.setId( new FatSaldoUtiId());
		
		//assert
		try {
			Assert.assertTrue(systemUnderTest.briPreInsercaoRow(entidade, "AGHU_2K46", new Date()));
			Assert.assertEquals(cap, entidade.getCapacidade().intValue());
		} catch (BaseException e) {
			this.log.info(e);
			fail("Not expecting exception: " + e.getCode());
		}
	}

	@Test
	public void testBruPreAtualizacaoRowFatSaldoUtiFatSaldoUti() {
		
		FatSaldoUti original = null;
		FatSaldoUti modificada = null;
		int cap = 0;
		int qtdLeitos = 0;
		
		//setup
		original = new FatSaldoUti();
		modificada = new FatSaldoUti();
		qtdLeitos = 12;
		original.setNroLeitos(Integer.valueOf(qtdLeitos));
		qtdLeitos = 13;
		modificada.setNroLeitos(Integer.valueOf(qtdLeitos));
		cap = 0;

		original.setId( new FatSaldoUtiId());
		modificada.setId( new FatSaldoUtiId());
		
		//assert
		try {
			Assert.assertTrue(systemUnderTest.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date()));
			Assert.assertEquals(cap, modificada.getCapacidade().intValue());
		} catch (BaseException e) {
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
