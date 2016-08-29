package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GerarNotaRecebimentoONTest extends AGHUBaseUnitTest<GerarNotaRecebimentoON>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	
	@Mock
	private SceItemNotaRecebimentoDAO mockedSceItemNotaRecebimentoDAO;
	@Mock
	private SceNotaRecebimentoRN mockedSceNotaRecebimentoRN;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private SceTipoMovimentosRN mockedSceTipoMovimentosRN;
	

	@Test
	public void gerarNotaRecebimentoTest(){
		try {
			final AghParametros aghParametros = new AghParametros();
			aghParametros.setVlrNumerico(new BigDecimal("1"));
			
			SceNotaRecebimento sceNotaRecebimento = new SceNotaRecebimento();			

			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(aghParametros);
			
			systemUnderTest.gerarNotaRecebimento(sceNotaRecebimento, NOME_MICROCOMPUTADOR);
			
		} catch (BaseException e) {
			getLog().debug("Exceção ignorada.");
		}
	}

	protected Log getLog() {
		return LogFactory.getLog(this.getClass());
	}

}
