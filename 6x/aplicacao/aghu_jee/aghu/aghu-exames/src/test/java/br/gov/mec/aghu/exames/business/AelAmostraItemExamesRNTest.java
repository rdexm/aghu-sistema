package br.gov.mec.aghu.exames.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelExecExamesMatAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelAmostraItemExamesRNTest extends AGHUBaseUnitTest<AelAmostraItemExamesRN>{
	
	private static final Log log = LogFactory.getLog(AelAmostraItemExamesRNTest.class);

	@Mock
	private AelAmostraItemExamesDAO mockedAelAmostraItemExamesDAO;
	@Mock
	private AelAmostraItemExamesON mockedAelAmostraItemExamesON;
	@Mock
	private AelExecExamesMatAnaliseDAO mockedAelExecExamesMatAnaliseDAO;
	@Mock
	private AelItemSolicitacaoExameDAO mockedAelItemSolicitacaoExameDAO;
	@Mock
	private AelExtratoItemSolicitacaoDAO mockedAelExtratoItemSolicitacaoDAO;
	@Mock
	private AelSitItemSolicitacoesDAO mockedAelSitItemSolicitacoesDAO;
	@Mock
	private ISolicitacaoExameFacade mockedSolicitacaoExameFacade;
	@Mock
	private IInternacaoFacade mockedInternacaoFacade;
	@Mock
	private AelAmostrasRN mockedAelAmostrasRN;
	@Mock
	private AelAmostrasON mockedAelAmostrasON;
	@Mock
	private IParametroFacade mockedParametroFacade;
	
	
	@Test
	public void beforeUpdateAelAmostraItemExamesTest() {
		
		AelAmostraItemExames aelAmostraItemExames = new AelAmostraItemExames();
    			
    	try {    		
			systemUnderTest.beforeUpdateAelAmostraItemExames(aelAmostraItemExames);
			
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		
	}
	
	
}
