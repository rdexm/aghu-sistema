package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class RelatorioMateriaisColetaEnfermagemONTest extends AGHUBaseUnitTest<RelatorioMateriaisColetaEnfermagemON>{
	
	private static final Log log = LogFactory.getLog(RelatorioMateriaisColetaEnfermagemONTest.class);

	@Mock
	private AelExamesDAO mockedAelExamesDAO;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	
	@Before
	public void doBeforeEachTestCase() throws Exception{
		
		
		Mockito.when(mockedAelExamesDAO.pesquisarRelatorioMateriaisColetaEnfermagem(Mockito.any(AghUnidadesFuncionais.class)))
		.thenReturn(new ArrayList<AelItemSolicitacaoExames>());
		
		Mockito.when(mockedPrescricaoMedicaFacade.buscarResumoLocalPaciente(Mockito.any(AghAtendimentos.class)))
		.thenReturn(new String());
	}
	
	
	@Test
	public void pesquisarRelatorioMateriaisColetaEnfermagemTest() throws BaseException{
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		

    	try {
			systemUnderTest.pesquisarRelatorioMateriaisColetaEnfermagem(unidadeFuncional);
			
			Assert.assertTrue(true);
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertFalse(true);
			
		}
	}
}
