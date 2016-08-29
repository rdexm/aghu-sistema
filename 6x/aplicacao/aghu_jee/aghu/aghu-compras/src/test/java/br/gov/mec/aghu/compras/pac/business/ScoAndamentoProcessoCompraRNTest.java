package br.gov.mec.aghu.compras.pac.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.dao.ScoAndamentoProcessoCompraDAO;
import br.gov.mec.aghu.compras.pac.business.ScoAndamentoProcessoCompraRN.ScoAndamentoProcessoCompraRNExceptionCode;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Teste Unitário de Serviço de Andamento do PAC
 * 
 * @author mlcruz
 */
public class ScoAndamentoProcessoCompraRNTest extends AGHUBaseUnitTest<ScoAndamentoProcessoCompraRN>{

	@Mock
	private ScoAndamentoProcessoCompraDAO andamentoDao;
	
	/**
	 * Testa inclusão de primeiro andamento.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testInclusaoPrimeiroAndamento() throws ApplicationBusinessException {
		final ScoAndamentoProcessoCompra andamento = new ScoAndamentoProcessoCompra();
		final ScoLicitacao licitacao = new ScoLicitacao();
		andamento.setLicitacao(licitacao);

		Mockito.when(andamentoDao.obterAndamentoSemDataSaida(andamento.getLicitacao().getNumero())).thenReturn(null);

		systemUnderTest.incluir(andamento);
	}
	
	/**
	 * Testa inclusão de andamentos posteriores.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testInclusaoAndamentoPosterior() throws ApplicationBusinessException {
		final ScoAndamentoProcessoCompra andamento = new ScoAndamentoProcessoCompra();
		final ScoAndamentoProcessoCompra andamentoSemDataSaida = new ScoAndamentoProcessoCompra();
		final ScoLicitacao licitacao = new ScoLicitacao();
		andamento.setLicitacao(licitacao);

		Mockito.when(andamentoDao.obterAndamentoSemDataSaida(andamento.getLicitacao().getNumero())).thenReturn(andamentoSemDataSaida);
		
		systemUnderTest.incluir(andamento);
		
		assertEquals(DateUtil.truncaData(new Date()),
				DateUtil.truncaData(andamentoSemDataSaida.getDtSaida()));
	}
	
	/**
	 * Testa inclusão de andamento com mesmo local de andamento anterior.
	 */
	@Test
	public void testInclusaoAndamentoMesmoLocal() {
		final ScoAndamentoProcessoCompra andamento = new ScoAndamentoProcessoCompra();
		final ScoAndamentoProcessoCompra andamentoSemDataSaida = new ScoAndamentoProcessoCompra();
		final ScoLocalizacaoProcesso local = new ScoLocalizacaoProcesso();
		final ScoLicitacao licitacao = new ScoLicitacao();
		andamento.setLicitacao(licitacao);
		andamento.setLocalizacaoProcesso(local);
		andamentoSemDataSaida.setLocalizacaoProcesso(local);

		Mockito.when(andamentoDao.obterAndamentoSemDataSaida(andamento.getLicitacao().getNumero())).thenReturn(andamentoSemDataSaida);

		try {
			systemUnderTest.incluir(andamento);
			fail("Não deveria incluir andamento que já se encontra no local informado.");
		} catch (ApplicationBusinessException e) {
			assertEquals(
					ScoAndamentoProcessoCompraRNExceptionCode.MENSAGEM_PROCESSO_JA_SE_ENCONTRA_NO_LOCAL_INFORMADO,
					e.getCode());
		}
	}
}
