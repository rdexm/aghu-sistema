package br.gov.mec.aghu.faturamento.business;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class TipoCaracteristicaItemRNTest extends AGHUBaseUnitTest<TipoCaracteristicaItemRN>{

	@Mock
	FatTipoCaractItensDAO mockedDao = null;
	
	@Test
	public void testObterTipoCaractItemSeq() {
		
		DominioFatTipoCaractItem caracteristica = null;
		List<FatTipoCaractItens> tctList = null;
		FatTipoCaractItens tct = null;
		Integer seq = null;
		
		//setup
		caracteristica = DominioFatTipoCaractItem.COBRA_BPA;
		tct = new FatTipoCaractItens(Integer.valueOf(123), caracteristica.getDescricao());
		tctList = new LinkedList<FatTipoCaractItens>();
		tctList.add(tct);
		Mockito.when(mockedDao.listarTipoCaractItensPorCaracteristica(caracteristica.getDescricao())).thenReturn(tctList);
		//assert
		seq = systemUnderTest.obterTipoCaractItemSeq(caracteristica);
		Assert.assertEquals(seq, tct.getSeq());
		// assert exception
		tctList.add(tct);
		try {
			seq = systemUnderTest.obterTipoCaractItemSeq(caracteristica);
			Assert.fail("Expecting an exception!");
		} catch (IllegalStateException e) {
			Assert.assertTrue(true);
		}
	}

}
