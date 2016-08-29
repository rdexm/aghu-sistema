package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoNutricaoParenteral;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 *
 *
 */
public class FaturamentoFatkInterfaceAnuRNTest extends AGHUBaseUnitTest<FaturamentoFatkInterfaceAnuRN>{
	
	private static final Log log = LogFactory.getLog(FaturamentoFatkInterfaceAnuRNTest.class);
	
	
	@Mock
	private IAghuFacade mockedAghuFacade; 
	@Mock
	private ItemContaHospitalarRN mockedItemContaHospitalarRN; 
	@Mock
	private FatProcedHospInternosDAO mockedFatProcedHospInternosDAO; 
	@Mock
	private FatItemContaHospitalarDAO mockedFatItemContaHospitalarDAO; 
	@Mock
	private IParametroFacade mockedParametroFacade;

	

	@Before
	public void doBeforeEachTestCase() {
	}

	/**
	 * Testa a execucao com dados nulos
	 *  
	 */
	@Test
	public void rnFatpInsNutrEntDadosNulosTest() throws ApplicationBusinessException{
	
		systemUnderTest.rnFatpInsNutrEnt(null, null, null, null, null);
	}

	/**
	 * Testa a execucao com dados válidos.
	 *  
	 */
	@Test
	public void rnFatpInsNutrEntDadosValidosTest() throws ApplicationBusinessException{
		
		AghParametros parametro =new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		
		Mockito.when(mockedItemContaHospitalarRN.fatcBuscaIdadePac(Mockito.anyInt())).thenReturn(1);

		Mockito.when(mockedFatProcedHospInternosDAO.buscarPrimeiroSeqProcedHospInternoPorTipoNutricaoEnteral(Mockito.any(DominioTipoNutricaoParenteral.class)))
		.thenReturn(32);

		List<FatItemContaHospitalar> listaIch = new ArrayList<FatItemContaHospitalar>();
		FatItemContaHospitalar ich = new FatItemContaHospitalar();
		listaIch.add(ich);
		Mockito.when(mockedFatItemContaHospitalarDAO.listarItensContaHospitalarComOrigemAnuFiltrandoPorContaHospitalarEProcedHospInt(Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaIch);

		systemUnderTest.rnFatpInsNutrEnt(null, null, null, 12, 1);
	}
	
	
	
}
