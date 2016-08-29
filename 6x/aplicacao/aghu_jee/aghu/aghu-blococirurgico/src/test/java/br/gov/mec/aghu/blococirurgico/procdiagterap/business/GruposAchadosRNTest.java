package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.model.PdtAchado;
import br.gov.mec.aghu.model.PdtGrupo;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

public class GruposAchadosRNTest extends AGHUBaseUnitTest<GruposAchadosRN> {


	@Mock
	private IRegistroColaboradorFacade mockRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockServidorLogadoFacade;

	@Test
	public void testSetarPdtGrupoServidorLogado() {
		try {
			mockRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString());
			systemUnderTest.setarPdtGrupoServidorLogado(getPdtGrupo());			
		} catch (ApplicationBusinessException e) {			
			Assert.fail();
		}
	}
	
	@Test
	public void testSetarPdtAchadoServidorLogado() {
		try {
			mockRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString());
			systemUnderTest.setarPdtAchadoServidorLogado(getPdtAchado());			
		} catch (ApplicationBusinessException e) {			
			Assert.fail();
		}
	}
	
	private PdtGrupo getPdtGrupo() {
		PdtGrupo grupo = new PdtGrupo();		
		return grupo;
	};
	
	private PdtAchado getPdtAchado() {
		PdtAchado achado = new PdtAchado();		
		return achado;
	};	
	
}
