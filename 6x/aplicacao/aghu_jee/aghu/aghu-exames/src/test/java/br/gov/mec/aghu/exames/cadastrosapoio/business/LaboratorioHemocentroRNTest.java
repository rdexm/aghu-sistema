package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFormaEnvio;
import br.gov.mec.aghu.exames.cadastrosapoio.business.LaboratorioHemocentroRN.LaboratorioHemocentroRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelLaboratorioExternosDAO;
import br.gov.mec.aghu.exames.dao.AelRefCodeDAO;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class LaboratorioHemocentroRNTest extends AGHUBaseUnitTest<LaboratorioHemocentroRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelRefCodeDAO mockedAelRefCodeDAO;
	@Mock
	private AelLaboratorioExternosDAO mockedAelLaboratorioExternosDAO;
	
	@Test
	public void testPreAtualizar001() throws ApplicationBusinessException {
		final AelLaboratorioExternos entityMocked = new AelLaboratorioExternos();
		entityMocked.setCriadoEm(new Date());
		entityMocked.setFormaEnvio(DominioFormaEnvio.C);
		
		final AelLaboratorioExternos oldEntityMocked = new AelLaboratorioExternos();
		
		Mockito.when(mockedAelRefCodeDAO.verificarValorValido(Mockito.anyString(), Mockito.anyString())).thenReturn(0);

		Mockito.when(mockedAelLaboratorioExternosDAO.obterOriginal(Mockito.anyInt())).thenReturn(oldEntityMocked);
		
		try {
			systemUnderTest.preAtualizar(entityMocked);
			Assert.fail("Deveria ocorrer ApplicationBusinessException...");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == LaboratorioHemocentroRNExceptionCode.AEL_01601);
		}
	}
	
	@Test
	public void testPreAtualizar002() throws ApplicationBusinessException {
		final AelLaboratorioExternos entityMocked = new AelLaboratorioExternos();
		entityMocked.setCriadoEm(new Date());
		//entityMocked.setFormaEnvio(DominioFormaEnvio.C);
		
		final AelLaboratorioExternos oldEntityMocked = new AelLaboratorioExternos();
		oldEntityMocked.setFormaEnvio(DominioFormaEnvio.C);
		
		Mockito.when(mockedAelRefCodeDAO.verificarValorValido(Mockito.anyString(), Mockito.anyString())).thenReturn(1);

		Mockito.when(mockedAelLaboratorioExternosDAO.obterOriginal(Mockito.anyInt())).thenReturn(oldEntityMocked);
		
		try {
			systemUnderTest.preAtualizar(entityMocked);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Nao deveria ocorrer ApplicationBusinessException...");
		}
	}
}
