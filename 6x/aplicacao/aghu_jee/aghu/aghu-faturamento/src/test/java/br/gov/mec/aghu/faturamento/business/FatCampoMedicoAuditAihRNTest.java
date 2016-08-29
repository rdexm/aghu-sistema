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

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCampoMedicoAuditAihDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCampoMedicoAuditAih;
import br.gov.mec.aghu.model.FatCampoMedicoAuditAihId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FatCampoMedicoAuditAihRNTest extends AGHUBaseUnitTest<FatCampoMedicoAuditAihRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	final Log log = LogFactory.getLog(this.getClass());
	@Mock
	FatCampoMedicoAuditAihDAO mockedDao;
	@Mock
	IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mcokedParametroFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	Date data = new Date();
	
	final FatCampoMedicoAuditAih entidade = new FatCampoMedicoAuditAih(new FatCampoMedicoAuditAihId(1, 1, (byte)1), null, null);

	@Before
	public void iniciar() throws BaseException {
		Mockito.when(mcokedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(new AghParametros());
		
		whenObterServidorLogado();
	}
	
	@Test
	public void testPosAtualizacao() throws BaseException {
		
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

		Mockito.when(mockedDao.qtdCma(Mockito.anyInt(), Mockito.anyInt())).thenReturn(9l);

		boolean result = false;
		try {
			result = systemUnderTest.posInsercao(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
		}
	}
	
	@Test
	public void testPosInsercaoFail() throws BaseException {
		
		Mockito.when(mockedDao.qtdCma(Mockito.anyInt(), Mockito.anyInt())).thenReturn(11l);
		
		boolean result = false;
		try {
			result = systemUnderTest.posInsercao(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (ApplicationBusinessException e) {
		}
	}

	@Test
	public void testPosRemocao() throws BaseException {
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
