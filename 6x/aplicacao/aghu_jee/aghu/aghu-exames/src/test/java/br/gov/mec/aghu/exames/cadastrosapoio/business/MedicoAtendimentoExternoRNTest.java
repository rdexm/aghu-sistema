package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.MedicoAtendimentoExternoRN.MedicoAtendimentoExternoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AghAtendimentosPacExternDAO;
import br.gov.mec.aghu.exames.dao.AghMedicoMatriculaConveniosDAO;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MedicoAtendimentoExternoRNTest extends AGHUBaseUnitTest<MedicoAtendimentoExternoRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AghAtendimentosPacExternDAO mockedAghAtendimentosPacExternDAO;
	@Mock
	private AghMedicoMatriculaConveniosDAO mockedAghMedicoMatriculaConveniosDAO;
	
	
	@Test
	public void testPreRemover001() throws ApplicationBusinessException {
		final AghMedicoExterno entityMocked = new AghMedicoExterno();
		entityMocked.setSeq(Integer.valueOf(1));
				
		Mockito.when(mockedAghAtendimentosPacExternDAO.countPacientesExternosPorMedicoExterno(Mockito.anyInt())).thenReturn(1l);
		
		try{
			ApplicationBusinessException returnException = systemUnderTest.verificaConstraintPacientesExternos(entityMocked);
			Assert.assertTrue(returnException.getCode() == MedicoAtendimentoExternoRNExceptionCode.APE_MEX_FK1);
		}catch(NullPointerException e) {
			Assert.fail("Nao deveria gerar excecao!");
		}
	}
	
	@Test
	public void testPreRemover002() throws ApplicationBusinessException {
		final AghMedicoExterno entityMocked = new AghMedicoExterno();
		entityMocked.setSeq(Integer.valueOf(1));

		Mockito.when(mockedAghAtendimentosPacExternDAO.countPacientesExternosPorMedicoExterno(Mockito.anyInt())).thenReturn(0l);

		ApplicationBusinessException returnException = null;
		try {
			returnException = systemUnderTest.verificaConstraintPacientesExternos(entityMocked);
			Assert.assertTrue(returnException == null);
		} catch (Exception e) {
			Assert.fail("Nao deveria gerar excecao!");
		}
	}
	
	@Test
	public void testPreRemover003() throws ApplicationBusinessException {
		final AghMedicoExterno entityMocked = new AghMedicoExterno();
		entityMocked.setSeq(Integer.valueOf(1));

		Mockito.when(mockedAghMedicoMatriculaConveniosDAO.countMatriculaConvenioPorMedicoExterno(Mockito.anyInt())).thenReturn(1l);
		
		try {
			ApplicationBusinessException returnException = systemUnderTest.verificaConstraintMatriculaConvenioMedico(entityMocked);
			Assert.assertTrue(returnException.getCode() == MedicoAtendimentoExternoRNExceptionCode.MMC_MEX_FK1);
		} catch (NullPointerException e) {
			Assert.fail("Nao deveria gerar excecao!");
		}
	}
	
	@Test
	public void testPreRemover004() throws ApplicationBusinessException {
		final AghMedicoExterno entityMocked = new AghMedicoExterno();
		entityMocked.setSeq(Integer.valueOf(1));

		Mockito.when(mockedAghMedicoMatriculaConveniosDAO.countMatriculaConvenioPorMedicoExterno(Mockito.anyInt())).thenReturn(0l);

		ApplicationBusinessException returnException = null;
		try {
			returnException = systemUnderTest.verificaConstraintMatriculaConvenioMedico(entityMocked);
			Assert.assertTrue(returnException == null);
		} catch (Exception e) {
			Assert.fail("Nao deveria gerar excecao!");
		}
	}
	
	
	
}
