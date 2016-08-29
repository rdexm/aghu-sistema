package br.gov.mec.aghu.compras.business;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.compras.dao.ScoServicoDAO;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ScoServicoONTest extends AGHUBaseUnitTest<ScoServicoON>{

	@Mock
	private ScoServicoDAO dao;
	@Mock
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	@Mock
	private ScoServico servico;
	
	/**
	 * Novo serviço deve ser incluído.
	 */
	@Test
	public void testNovoServicoDeveSerIncluido() {
		dadoServico();
		quandoIncluilo();
	}

	/**
	 * Serviço deve ser alterado.
	 */
	@Test
	public void testServicoDeveSerAlterado() {
		dadoServico();
		quandoAlteralo();
	}

	private void dadoServico() {
		servico = new ScoServico();
	}

	private void quandoIncluilo() {
		systemUnderTest.incluir(servico);
	}

	
	private void quandoAlteralo() {
		systemUnderTest.alterar(servico);
	}

}