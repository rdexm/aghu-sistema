package br.gov.mec.aghu.ambulatorio.business;

import static br.gov.mec.aghu.ambulatorio.business.ManterCondicaoAtendimentoRN.ManterCondicaoAtendimentoRNExceptionCode.AGH_00651;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.dao.AacCondicaoAtendimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaAgendamentoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterCondicaoAtendimentoRNTest extends AGHUBaseUnitTest<ManterCondicaoAtendimentoRN>{
	
    @Mock
	private IAghuFacade aghuFacade;
    @Mock
	private AacCondicaoAtendimentoDAO aacCondicaoAtendimentoDAO;
    @Mock
	private AacFormaAgendamentoDAO aacFormaAgendamentoDAO;
	
	private static final Log log = LogFactory.getLog(ManterCondicaoAtendimentoRNTest.class);
	
	@Test
	public void remover01(){	
		final AacCondicaoAtendimento condicaoAtendimento = new AacCondicaoAtendimento();
		final Short codigoCondicaoAtendimento = Short.valueOf("1");
		condicaoAtendimento.setSeq(codigoCondicaoAtendimento);
		
		Mockito.when(aacCondicaoAtendimentoDAO.obterPorChavePrimaria(codigoCondicaoAtendimento)).thenReturn(condicaoAtendimento);
		
		Mockito.when(aacFormaAgendamentoDAO.existeFormaAgendamentoComCondicaoAtendimentoCount(condicaoAtendimento)).thenReturn(Boolean.FALSE);

		try {
			systemUnderTest.remover(codigoCondicaoAtendimento);
		} catch (ApplicationBusinessException e) {
			log.error(e.getMessage());
		}	
	}
	
	@Test
	public void remover02(){		
		final AacCondicaoAtendimento condicaoAtendimento = new AacCondicaoAtendimento();
		final Short codigoCondicaoAtendimento = Short.valueOf("1");
		condicaoAtendimento.setSeq(codigoCondicaoAtendimento);
		
		Mockito.when(aacCondicaoAtendimentoDAO.obterPorChavePrimaria(codigoCondicaoAtendimento)).thenReturn(condicaoAtendimento);
		
		Mockito.when(aacFormaAgendamentoDAO.existeFormaAgendamentoComCondicaoAtendimentoCount(condicaoAtendimento)).thenReturn(Boolean.TRUE);
		
		try {
			systemUnderTest.remover(codigoCondicaoAtendimento);
			Assert.fail("Deveria ter ocorrido uma "+AGH_00651);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+AGH_00651, e.getCode(), AGH_00651);
		}
	}
}
