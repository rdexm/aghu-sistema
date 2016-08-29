package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatTipoTransplante;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FatCompatExclusItemONTest extends AGHUBaseUnitTest<FatCompatExclusItemON>{

	
	/**
	 * Testa clonar com dados nulos
	 */
	@Test
	public void testInserirJournalFatCompatExclusItemRNNulosTest() throws Exception {
	
		List<FatCompatExclusItem> listaOriginal = new ArrayList<FatCompatExclusItem>();
		listaOriginal.add(new FatCompatExclusItem());
		systemUnderTest.clonarListaFatCompatExclusItem(listaOriginal);
	}


	/**
	 * Testa clonar com dados 
	 */
	@Test
	public void testInserirJournalFatCompatExclusItemRNTest() throws Exception {
	
		List<FatCompatExclusItem> listaOriginal = new ArrayList<FatCompatExclusItem>();
		
		FatCompatExclusItem fatCompatExclusItem = new FatCompatExclusItem();
		FatItensProcedHospitalar itemProcedHospitalar = new FatItensProcedHospitalar();
		FatItensProcedHospitalarId id = new FatItensProcedHospitalarId((short) 11, 11);
		itemProcedHospitalar.setId(id);	
		fatCompatExclusItem.setItemProcedHosp(itemProcedHospitalar);
		fatCompatExclusItem.setItemProcedHospCompatibiliza(itemProcedHospitalar);
		fatCompatExclusItem.setTipoTransplante(new FatTipoTransplante());
		
		listaOriginal.add(fatCompatExclusItem);
		systemUnderTest.clonarListaFatCompatExclusItem(listaOriginal);
	}	

}
