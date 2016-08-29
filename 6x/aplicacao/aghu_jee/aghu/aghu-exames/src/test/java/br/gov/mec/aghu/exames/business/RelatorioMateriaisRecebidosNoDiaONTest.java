package br.gov.mec.aghu.exames.business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.business.RelatorioMateriaisRecebidosNoDiaON.RelatorioMateriaisRecebidosNoDiaONExceptionCode;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.exames.vo.RelatorioMateriaisRecebidosNoDiaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class RelatorioMateriaisRecebidosNoDiaONTest extends AGHUBaseUnitTest<RelatorioMateriaisRecebidosNoDiaON>{
	
	@Mock
	private VAelExameMatAnaliseDAO mockedVAelExameMatAnaliseDAO;
	
	
	@Test
	public void testPesquisarMateriaisRecebidosNoDia() throws ParseException {
		
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String stringDataInicial = "01/01/2011 08:00";
		String stringDataFinal = "01/01/2011 23:59";
		Date dtInicial = sdf.parse(stringDataInicial);
		Date dtFinal = sdf.parse(stringDataFinal);
		
		final List<RelatorioMateriaisRecebidosNoDiaVO> lista = new ArrayList<RelatorioMateriaisRecebidosNoDiaVO>();
		
		Mockito.when(mockedVAelExameMatAnaliseDAO.pesquisarRelatorioMateriaisRecebidosNoDia(Mockito.any(Date.class), Mockito.any(Date.class), Mockito.anyShort(), Mockito.anyString()))
		.thenReturn(lista);
		
    	try {
			systemUnderTest.pesquisarMateriaisRecebidosNoDia((short)151, dtInicial,dtFinal);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					RelatorioMateriaisRecebidosNoDiaONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA,
					e.getCode());
		}
    	
 	}
	
	@Test
	public void testObterExameMaterialComDescricao(){
		
		String indExigeDescricao = "N";
		String nomeUsual = "BICARBONATO";
		
		Assert.assertEquals(
			systemUnderTest.obterExameMaterialExtenso(indExigeDescricao, nomeUsual, null, null, null),
			nomeUsual
			);		
	}
	
	@Test
	public void testObterExameMaterialSemDescricao(){
		
		String materialAnalise = "TESTE";
		String descricaoExame = "teste";
		Long numeroAp = 1L;
		String resultado = "teste / TESTE-AP 000000/01";
		
		Assert.assertEquals(
			systemUnderTest.obterExameMaterialExtenso(null, null, descricaoExame, materialAnalise, numeroAp),
			resultado
			);		
	}
	//String obterExameMaterialExtenso(String indExigeDescricao, String nomeUsual, String descricaoExame, String materialAnalise, Integer numeroAp)
}
