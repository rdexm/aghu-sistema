package br.gov.mec.aghu.exames.solicitacao.business;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class VerificarPermissoesExameTest extends AGHUBaseUnitTest<VerificarPermissoesExame>{

	@Mock
	ItemSolicitacaoExameVO itemSolicitacaoExameVO;
	@Mock
	private VerificarPermissaoHorariosRotina mockedVerificarPermissaoHorariosRotina;
	
	@Test
	public void testProcessRequest() {
		AelSitItemSolicitacoes situacaoCodigo = new AelSitItemSolicitacoes();
		situacaoCodigo.setCodigo(DominioSituacaoItemSolicitacaoExame.AE.toString());
		
		itemSolicitacaoExameVO.setSituacaoCodigo(situacaoCodigo);
		
		try {
			systemUnderTest.processRequest(itemSolicitacaoExameVO);
		} catch (ApplicationBusinessException e) {
		}
	}
	
	@Test
	public void testProcessRequest02() {
		itemSolicitacaoExameVO.setUrgente(Boolean.TRUE);
		
		try {
			systemUnderTest.processRequest(itemSolicitacaoExameVO);
		} catch (ApplicationBusinessException e) {
		}
	}
}
