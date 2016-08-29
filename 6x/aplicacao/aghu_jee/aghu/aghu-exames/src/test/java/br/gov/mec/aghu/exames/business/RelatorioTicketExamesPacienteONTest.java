package br.gov.mec.aghu.exames.business;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelRecomendacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class RelatorioTicketExamesPacienteONTest extends AGHUBaseUnitTest<RelatorioTicketExamesPacienteON>{
	
	private static final Log log = LogFactory.getLog(RelatorioTicketExamesPacienteONTest.class);

	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private PesquisarRelatorioTicketExamesPacienteON mockedPesquisarRelatorioTicketExamesPacienteON;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelSolicitacaoExameDAO mockedAelSolicitacaoExameDAO;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private AelRecomendacaoExameDAO mockedAelRecomendacaoExameDAO;
	//private AghCaractUnidFuncionaisDAO mockedAghCaractUnidFuncionaisDAO;
	@Mock
	private AelItemHorarioAgendadoDAO mockedAelItemHorarioAgendadoDAO;
	@Mock
	private AelAmostraItemExamesDAO mockedAelAmostraItemExamesDAO;
	@Mock
	private IExamesLaudosFacade mockedExamesLaudosFacade;
	@Mock
	private IInternacaoFacade mockedInternacaoFacade;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	
	@Before
	public void doBeforeEachTestCase() throws Exception{

		AghParametros aghParametros = new AghParametros();
		aghParametros.setVlrNumerico(new BigDecimal("33"));
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(aghParametros);
		
		Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(new AghUnidadesFuncionais());
		
		Mockito.when(mockedPrescricaoMedicaFacade.buscarResumoLocalPaciente(Mockito.any(AghAtendimentos.class))).thenReturn(new String());
		
	}
	
	@Test
	public void pesquisarRelatorioTicketExamesPacienteTest() throws BaseException{
		Integer codSolicitacao = 12;
		

    	try {
			systemUnderTest.pesquisarRelatorioTicketExamesPaciente(codSolicitacao, null, null);
			
			Assert.assertTrue(true);
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertFalse(true);
			
		}
	}
}
