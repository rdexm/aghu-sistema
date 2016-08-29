package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jdbc.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoAihId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class EspelhoAihRNTest extends AGHUBaseUnitTest<EspelhoAihRN>{
	
	@Mock
	FatEspelhoAihDAO mockedDao;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
    @Mock
	private IParametroFacade mockedParametroFacade;
    
	private final Log log = LogFactory.getLog(this.getClass());
	final Date data = new Date(0L);
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Before
	public void inicar() throws BaseException {
		whenObterServidorLogado();
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

	}
	
	@Test
	public void testVerificarQtdMaxAIHPorEspelho() {

		FatEspelhoAihId id = null;
		FatEspelhoAih entidade = null;
		Expectations expect = null;
		int qtdMax = 0;

		//setup
		qtdMax = 1;
		id = new FatEspelhoAihId(Integer.valueOf(123), Integer.valueOf(456));
		entidade = new FatEspelhoAih();
		entidade.setId(id);		
		Mockito.when(mockedDao.countPorCthSeqpDataPreviaNotNull(id.getCthSeq(), id.getSeqp())).thenReturn(2l);
		//assert UPD
		try {
			systemUnderTest.verificarQtdMaxAIHPorEspelho(DominioOperacoesJournal.UPD, entidade);
			Assert.assertTrue(true);
		} catch (Exception e) {
			this.log.info(e);
		}
		//assert INS
		try {
			systemUnderTest.verificarQtdMaxAIHPorEspelho(DominioOperacoesJournal.INS, entidade);
			Assert.fail("Expecting exception");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(FaturamentoExceptionCode.FAT_00404, e.getCode());
		}
	}

	@Test
	public void testBriPreInsercaoRowFatEspelhoAih() {

		FatEspelhoAih entidade = null;
		FatEspelhoAihId id = new FatEspelhoAihId(Integer.valueOf(123), Integer.valueOf(456));
		//setup
		entidade = new FatEspelhoAih();
		
		entidade.setId(id);
		
		Mockito.when(mockedDao.countPorCthSeqpDataPreviaNotNull(id.getCthSeq(), id.getSeqp())).thenReturn(1l);
		
		//assert
		try {
			Assert.assertTrue(systemUnderTest.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date()));
		} catch (BaseException e) {
			this.log.info(e);
			Assert.fail("Not expecting exception: " + e.getCode());
		}
	}

	@Test
	public void testBruPreAtualizacaoRowFatEspelhoAihFatEspelhoAih() {

		FatEspelhoAih entidade = null;
		//setup
		entidade = new FatEspelhoAih();
		
		FatEspelhoAihId id = new FatEspelhoAihId(Integer.valueOf(123), Integer.valueOf(456));
		
		entidade.setId(id);
		
		//assert
		
		Mockito.when(mockedDao.countPorCthSeqpDataPreviaNotNull(id.getCthSeq(), id.getSeqp())).thenReturn(1l);
		
		try {
			Assert.assertTrue(systemUnderTest.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date()));
		} catch (BaseException e) {
			this.log.info(e);
			Assert.fail("Not expecting exception: " + e.getCode());
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
