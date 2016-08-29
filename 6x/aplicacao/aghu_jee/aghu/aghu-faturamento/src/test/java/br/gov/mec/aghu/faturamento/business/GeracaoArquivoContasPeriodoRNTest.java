package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.vo.BuscaContaVO;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GeracaoArquivoContasPeriodoRNTest extends AGHUBaseUnitTest<GeracaoArquivoTextoInternacaoBean>{

	final Log log = LogFactory.getLog(this.getClass());
	final Random rand = new Random(System.currentTimeMillis());
	@Mock
	IAghuFacade mockedAghuFacade = null;

	@Before
	public void setUp()
			throws Exception {

	}

	@Test
	public void testObterValorTotal() {

		BuscaContaVO vo = null;
		BigDecimal result = null;
		double val = 0.0;
		double total = 0.0;

		//setup		
		vo = new BuscaContaVO();
		//assert
		result = GeracaoArquivoTextoInternacaoBean.obterValorTotal(vo);
		Assert.assertNotNull(result);
		Assert.assertTrue(BigDecimal.ZERO.equals(result));
		//setup
		val = this.rand.nextDouble();
		total += val;
		// nvl(VALOR_SH,0) 
		vo.setValorSh(BigDecimal.valueOf(val));
		// + nvl(VALOR_UTI,0) 		
		val = this.rand.nextDouble();
		total += val;
		vo.setValorUti(BigDecimal.valueOf(val));
		// + nvl(VALOR_UTIE,0) 
		val = this.rand.nextDouble();
		total += val;
		vo.setValorUtie(BigDecimal.valueOf(val));
		// + nvl(VALOR_SP,0) 
		val = this.rand.nextDouble();
		total += val;
		vo.setValorSp(BigDecimal.valueOf(val));
		// + nvl(VALOR_ACOMP,0) 
		val = this.rand.nextDouble();
		total += val;
		vo.setValorAcomp(BigDecimal.valueOf(val));
		// + nvl(VALOR_RN,0) 
		val = this.rand.nextDouble();
		total += val;
		vo.setValorRn(BigDecimal.valueOf(val));
		// + nvl(VALOR_SADT,0) 
		val = this.rand.nextDouble();
		total += val;
		vo.setValorSadt(BigDecimal.valueOf(val));
		// + nvl(VALOR_HEMAT,0) 
		val = this.rand.nextDouble();
		total += val;
		vo.setValorHemat(BigDecimal.valueOf(val));
		// + nvl(VALOR_TRANSP,0) 
		val = this.rand.nextDouble();
		total += val;
		vo.setValorTransp(BigDecimal.valueOf(val));
		// + nvl(VALOR_OPM,0) 
		val = this.rand.nextDouble();
		total += val;
		vo.setValorOpm(BigDecimal.valueOf(val));
		// + nvl(VALOR_ANESTESISTA,0) 
		val = this.rand.nextDouble();
		total += val;
		vo.setValorAnestesista(BigDecimal.valueOf(val));
		// + nvl(VALOR_PROCEDIMENTO,0) valor_conta,
		val = this.rand.nextDouble();
		total += val;
		vo.setValorProcedimento(BigDecimal.valueOf(val));
		//assert
		result = GeracaoArquivoTextoInternacaoBean.obterValorTotal(vo);
		Assert.assertNotNull(result);
		this.log.info("R: " + result + " T: " + total + " D:" + (result.doubleValue() - total));
		Assert.assertTrue(Math.abs(total - result.doubleValue()) < 0.000001);
	}

	@Test
	public void testObterPrefixoNomeArquivo() {

		String result = null;
		String exp = null;
		GregorianCalendar cal = null;
		int ano = 0;
		int mes = 0;
		int dia = 0;

		//setup
		ano = 2000;
		mes = 10;
		dia = 23;
		cal = new GregorianCalendar(ano, mes - 1, dia);
		exp = GeracaoArquivoTextoInternacaoBean.MAGIC_STRING_PREFIXO_ARQ_CSV_EQ_CTH + dia + mes + ano + "-";
		//assert
		result = GeracaoArquivoTextoInternacaoBean.obterPrefixoNomeArquivo(cal.getTime());
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(exp, result);
	}
}
