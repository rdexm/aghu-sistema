package br.gov.mec.aghu.faturamento.business;

import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jdbc.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatAtoMedicoAihId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AtoMedicoAihRNTest extends AGHUBaseUnitTest<AtoMedicoAihRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	final Log log = LogFactory.getLog(this.getClass());
	@Mock
	private FatAtoMedicoAihDAO mockedDao;
	@Mock
	private IParametroFacade mockedParametroFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	Date data = new Date();
	int maxQtdAma = 7;

	@Before
	public void inicio() throws BaseException{
		whenObterServidorLogado();
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).
		thenReturn(new AghParametros());

	}
	
	@Test
	public void testBriPreInsercaoRowFatAtoMedicoAih() {
		
		boolean result = false;
		FatAtoMedicoAih entidade = null;
		
		//setup
		entidade = new FatAtoMedicoAih();
		//assert
		try {
			result = systemUnderTest.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}

	@Test
	public void testBruPreAtualizacaoRowFatAtoMedicoAihFatAtoMedicoAih() {

		boolean result = false;
		FatAtoMedicoAih original = null;
		FatAtoMedicoAih modificada = null;
		
		//setup
		original = new FatAtoMedicoAih();
		modificada = new FatAtoMedicoAih();
		//assert
		try {
			result = systemUnderTest.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
		Assert.assertNull(modificada.getCriadoPor());
	}
	
	@Test
	public void testObterQtdFatAtoMedicoAihPorEai() {
		
		Long result = 0l;
		Long daoResult = 0l;
		Integer eaiSeq = null;
		Integer eaiCthSeq = null;
		Expectations expect = null;
		
		//setup
		eaiCthSeq = Integer.valueOf(123);
		eaiSeq = Integer.valueOf(321);
		daoResult = 13l;
		Mockito.when(mockedDao.obterQtdPorEai(eaiSeq, eaiCthSeq)).thenReturn(daoResult);
		//assert
		result = systemUnderTest.obterQtdFatAtoMedicoAihPorEai(eaiSeq, eaiCthSeq);
		Assert.assertEquals(daoResult, result);
	}
	
	@Test
	public void testVerificarQtdAtosDentroLimite() {

		AtoMedicoAihRN objRn = null;
		boolean result = false;
		FatAtoMedicoAih entidade = null;
		FatAtoMedicoAihId id = null;
		int daoResult = 0;
		Integer eaiSeq = null;
		Integer eaiCthSeq = null;
		Expectations expect = null;
		
		//setup
		eaiCthSeq = Integer.valueOf(123);
		eaiSeq = Integer.valueOf(321);
		id = new FatAtoMedicoAihId();
		id.setEaiCthSeq(eaiCthSeq);
		id.setEaiSeq(eaiSeq);
		entidade = new FatAtoMedicoAih();
		entidade.setId(id);
		daoResult = this.maxQtdAma - 1;
		Mockito.when(mockedDao.obterQtdPorEai(eaiSeq, eaiCthSeq)).thenReturn(Long.valueOf(daoResult));
		//assert
		try {
			result = systemUnderTest.verificarQtdAtosDentroLimite(entidade);
		} catch (ApplicationBusinessException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
		}
		//setup
		daoResult = this.maxQtdAma + 1;
		Mockito.when(mockedDao.obterQtdPorEai(eaiSeq, eaiCthSeq)).thenReturn(Long.valueOf(daoResult));
		//assert
		try {
			result = systemUnderTest.verificarQtdAtosDentroLimite(entidade);
		} catch (ApplicationBusinessException e) {
		}
	}

	@Test
	public void testBsiPreInsercaoStatementFatAtoMedicoAih() {

		boolean result = false;
		FatAtoMedicoAih entidade = null;
		Expectations expect = null;

		//assert
		try {
			result = systemUnderTest.verificarQtdAtosDentroLimite(entidade);
		} catch (ApplicationBusinessException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
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
