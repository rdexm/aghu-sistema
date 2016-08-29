package br.gov.mec.aghu.exames.solicitacao.business;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SolicitacaoExameEnforceRNTest extends AGHUBaseUnitTest<SolicitacaoExameEnforceRN>{

	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private AelSolicitacaoExameDAO mockedAelSolicitacaoExameDAO;
	
	
	/**
	 * Deve retornar falso pois não houve modificação do mesmo.
	 * @throws BaseException
	 */
	@Test
	public void verificarConvenioPlanoModificadosFalsoTest() throws BaseException {
		
		FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		
		FatConvenioSaudePlano convenioSaudePlanoOriginal = new FatConvenioSaudePlano();
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		solicitacaoExames.setConvenioSaudePlano(convenioSaudePlano);
		
		AelSolicitacaoExames solicitacaoExamesOriginal = new AelSolicitacaoExames();
		solicitacaoExamesOriginal.setConvenioSaudePlano(convenioSaudePlanoOriginal);
				
		boolean atual = systemUnderTest.verificarConvenioPlanoModificados(solicitacaoExames, solicitacaoExamesOriginal);
		
		assertFalse(atual);
		
	}
	
	/**
	 * Deve retornar verdadeiro pois houve modificação do mesmo.
	 * @throws BaseException
	 */
	@Test
	public void verificarConvenioPlanoModificadosVerdadeiroTest() throws BaseException {
		
		FatConvenioSaudePlanoId convenioSaudePlanoId = new FatConvenioSaudePlanoId();
		convenioSaudePlanoId.setCnvCodigo(Short.valueOf("1"));
		
		FatConvenioSaudePlanoId convenioSaudePlanoOriginalId = new FatConvenioSaudePlanoId();
		convenioSaudePlanoOriginalId.setCnvCodigo(Short.valueOf("2"));
		
		FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		convenioSaudePlano.setId(convenioSaudePlanoId);
		
		FatConvenioSaudePlano convenioSaudePlanoOriginal = new FatConvenioSaudePlano();
		convenioSaudePlanoOriginal.setId(convenioSaudePlanoOriginalId);
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		solicitacaoExames.setConvenioSaudePlano(convenioSaudePlano);
		
		AelSolicitacaoExames solicitacaoExamesOriginal = new AelSolicitacaoExames();
		solicitacaoExamesOriginal.setConvenioSaudePlano(convenioSaudePlanoOriginal);
				
		boolean atual = systemUnderTest.verificarConvenioPlanoModificados(solicitacaoExames, solicitacaoExamesOriginal);
		
		assertTrue(atual);
		
	}
	
	

}
