package br.gov.mec.aghu.exames.pesquisa.business;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelItemSolicCartasDAO;
import br.gov.mec.aghu.exames.dao.PesquisaExameDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ExamesCancelarRNTest extends AGHUBaseUnitTest<ExamesCancelarRN>{

	@Mock
	private AelItemSolicCartasDAO mockedAelItemSolicCartasDAO;
	@Mock
	private IExamesFacade mockedAelItemSolicCartasRN;
	@Mock
	private PesquisaExameDAO mockedPesquisaExameDAO;
	@Mock
	private ISolicitacaoExameFacade mockedSolicitacaoExameFacade;
	
	@Test
	public void verGeraCartaTest() {
		
		AelItemSolicitacaoExames aelItemSolicitacaoExames = new AelItemSolicitacaoExames();
		aelItemSolicitacaoExames.setSolicitacaoExame(new AelSolicitacaoExames());
			
//		try {
		
//	    	mockingContext.checking(new Expectations() {{
//	    		allowing(mockedAelAmostrasDAO).desatachar(with(any(AelAmostras.class))); 
//				allowing(mockedAelAmostrasDAO).obterPorChavePrimaria(with(any(AelAmostrasId.class))); 
//				will(returnValue(amostrasOld));
//				
//				allowing(mockedManterUnidadesExecutorasExamesCRUD).verUnfAtiv(with(any(AghUnidadesFuncionais.class)));
//				allowing(mockedRelatorioInternacaoRN).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)), with(any(ConstanteAghCaractUnidFuncionais.class)));
//				will(returnValue(true));
//			}});
    	
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			log.error(e.getMessage());
//		}
		
		systemUnderTest.verGeraCarta(aelItemSolicitacaoExames);
			
		
		
	}
}
