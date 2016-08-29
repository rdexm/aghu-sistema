package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ItemPrescricaoMedicamentoRNTest extends AGHUBaseUnitTest<ItemPrescricaoMedicamentoRN>{

	@Mock
	private MpmPrescricaoMdtoDAO mockedMpmPrescricaoMdtoDAO;	
	
	@Test
	public void verificaDelecao() {

		MpmPrescricaoMdto mdto = new MpmPrescricaoMdto();
		mdto.setIndPendente(DominioIndPendenteItemPrescricao.D);
		
		Mockito.when(mockedMpmPrescricaoMdtoDAO.obterMedicamentoPeloId(Mockito.anyInt(), Mockito.anyLong())).thenReturn(mdto);
		
		try {
			systemUnderTest.verificaDelecao(1, 1l, null);
		} catch (BaseException e) {
			Assert.fail();
		}
	}

}
