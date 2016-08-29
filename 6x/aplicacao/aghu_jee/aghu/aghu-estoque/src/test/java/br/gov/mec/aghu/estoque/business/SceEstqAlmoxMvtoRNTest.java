package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.estoque.dao.SceEstqAlmoxMvtoDAO;
import br.gov.mec.aghu.model.SceEstqAlmoxMvto;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceEstqAlmoxMvtoRNTest extends AGHUBaseUnitTest<SceEstqAlmoxMvtoRN>{

	@Mock
	private SceEstqAlmoxMvtoDAO mockedSceEstqAlmoxMvtoDAO;
	
	
	@Test
	public void inserirTest() throws BaseException{

		SceTipoMovimento sceTipoMovimento = new SceTipoMovimento();
		sceTipoMovimento.setIndAltQtdeEstqAlmoxMvto(false);
		sceTipoMovimento.setIndAltValrEstqAlmoxMvto(false);
		
		SceEstqAlmoxMvto sceEstqAlmoxMvto = new SceEstqAlmoxMvto();
		sceEstqAlmoxMvto.setSceTipoMovimentos(sceTipoMovimento);
		sceEstqAlmoxMvto.setValor(new BigDecimal("2"));
		
		systemUnderTest.inserir(sceEstqAlmoxMvto);
	}
}
