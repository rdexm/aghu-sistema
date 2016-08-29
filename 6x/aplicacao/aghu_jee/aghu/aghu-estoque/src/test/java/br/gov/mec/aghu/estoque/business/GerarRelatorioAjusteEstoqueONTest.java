package br.gov.mec.aghu.estoque.business;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.vo.RelatorioAjusteEstoqueVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GerarRelatorioAjusteEstoqueONTest extends AGHUBaseUnitTest<GerarRelatorioAjusteEstoqueON>{
	
	@Mock	
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	/**
	 * Testa a pesquisa dos dados para preenchimento do relatório de ajuste de estoque
	 * @throws ParseException 
	 * @throws ApplicationBusinessException
	 * @author clayton.bras
	 */
	@Test
	public void pesquisarDadosRelatorioAjusteEstoqueTest() throws ApplicationBusinessException, ParseException{

		Mockito.when(sceMovimentoMaterialDAO.pesquisarDadosRelatorioAjusteEstoque(null, new ArrayList<String>())).thenReturn(new ArrayList<RelatorioAjusteEstoqueVO>());

		systemUnderTest.pesquisarDadosRelatorioAjusteEstoque(null, new ArrayList<String>()); 
	}
	
	
	/**
	 * Testa a geração do arquivo CSV
	 * @throws ParseException 
	 * @throws ApplicationBusinessException
	 * @author clayton.bras
	 * @throws IOException 
	 */
	@Test
	public void gerarCSVRelatorioAjusteEstoqueTest() throws ApplicationBusinessException, ParseException, IOException{

		Mockito.when(sceMovimentoMaterialDAO.pesquisarDadosRelatorioAjusteEstoque(null, new ArrayList<String>())).thenReturn(new ArrayList<RelatorioAjusteEstoqueVO>());

		systemUnderTest.gerarCSVRelatorioAjusteEstoque(null, new ArrayList<String>()); 
	}
	
}
