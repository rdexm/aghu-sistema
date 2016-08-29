package br.gov.mec.aghu.compras.business;


import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.gov.mec.aghu.compras.autfornecimento.business.ScoAutorizacaoFornecedorPedidoON;
import br.gov.mec.aghu.compras.vo.AcessoFornProgEntregaFiltrosVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoAutorizacaoFornecedorPedidoONTest {

	ScoAutorizacaoFornecedorPedidoON on;
	
	@Before
	public void setUp() throws Exception {
		on = new ScoAutorizacaoFornecedorPedidoON();
	}
	
	@Test(expected=ApplicationBusinessException.class)
	public void testValidaPesquisaProgEntregaFornecedorNoFilter() throws ApplicationBusinessException{
		on.validaPesquisaProgEntregaFornecedor(new AcessoFornProgEntregaFiltrosVO());
	}
	
	@Test
	public void testValidaPesquisaProgEntregaFornecedorNoFilterMessage(){
		try {
			on.validaPesquisaProgEntregaFornecedor(new AcessoFornProgEntregaFiltrosVO());
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("MESSAGE_SEM_FILTROS_PESQUISA", e.getMessage());
		}
	}
	
	@Test(expected=ApplicationBusinessException.class)
	public void testValidaPesquisaProgEntregaFornecedorWithDataRangeError() throws ApplicationBusinessException{
		on.validaPesquisaProgEntregaFornecedor(buildAcessoFornProgEntregaFiltrosVO());
	}
	
	@Test
	public void testValidaPesquisaProgEntregaFornecedorWithDataRangeErrorMessage(){
		try {
			on.validaPesquisaProgEntregaFornecedor(buildAcessoFornProgEntregaFiltrosVO());
			Assert.fail();
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("MESSAGE_DATA_FINAL_MAIOR", e.getMessage());
		}
	}

	private AcessoFornProgEntregaFiltrosVO buildAcessoFornProgEntregaFiltrosVO() {
		AcessoFornProgEntregaFiltrosVO vo = new AcessoFornProgEntregaFiltrosVO();
		vo.setComplemento((short) 123);
		vo.setNumeroAF(12345);
		vo.setNumeroAFP(6789);
		vo.setDataAcessoInicial(DateUtil.obterData(2014, Calendar.OCTOBER, 10));
		vo.setDataAcessoFinal(DateUtil.obterData(2013, Calendar.OCTOBER, 10));
		return vo;
	}

}
