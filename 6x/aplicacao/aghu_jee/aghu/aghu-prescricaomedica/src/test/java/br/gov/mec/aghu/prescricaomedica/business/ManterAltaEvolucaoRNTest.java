package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmAltaEvolucao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.business.ManterAltaSumarioRN.ManterAltaSumarioRNExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEvolucaoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Testes basicos para os metodos de verificacao da classe ManterAltaEvolucaoRN. <br>
 * 
 * @author gfmenezes
 *
 */
public class ManterAltaEvolucaoRNTest extends AGHUBaseUnitTest<ManterAltaEvolucaoRN>{

	@Mock
	private ManterAltaSumarioRN mockedManterAltaSumarioRN;
	@Mock
	private MpmAltaEvolucaoDAO mockedMpmAltaEvolucaoDAO;
	
	
	/**
	 * Pre insert.
	 * Testa retorno de erro.
	 */
	@Test
	public void verificaPreInserirAltaEvolucao001() {
		MpmAltaEvolucao altaEvolucao = new MpmAltaEvolucao();
		
		try {
			Mockito.doThrow(new ApplicationBusinessException(ManterAltaSumarioRNExceptionCode.MPM_03735)).when(mockedManterAltaSumarioRN).verificarAltaSumarioAtivo(Mockito.any(MpmAltaSumario.class));
			systemUnderTest.preInserirAltaEvolucao(altaEvolucao);
			Assert.fail("Deveria retornar exception");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Pre insert.
	 * Testa SEM retorno de erro.
	 */
	@Test
	public void verificaPreInserirAltaEvolucao002() {
		MpmAltaEvolucao altaEvolucao = new MpmAltaEvolucao();
		
		try {
			systemUnderTest.preInserirAltaEvolucao(altaEvolucao);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NAO Deveria retornar exception");
		}
	}
	
	/**
	 * Pre update.
	 * Testa retorno de erro.
	 */
	@Test
	public void verificaPreAtualizarAltaEvolucao001() {
		MpmAltaEvolucao altaEvolucao = new MpmAltaEvolucao();
		
		try {
			Mockito.doThrow(new ApplicationBusinessException(ManterAltaSumarioRNExceptionCode.MPM_03735)).when(mockedManterAltaSumarioRN).verificarAltaSumarioAtivo(Mockito.any(MpmAltaSumario.class));
			
			systemUnderTest.preAtualizarAltaEvolucao(altaEvolucao);
			Assert.fail("Deveria retornar exception");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Pre update.
	 * Testa SEM retorno de erro.
	 */
	@Test
	public void verificaPreAtualizarAltaEvolucao002() {
		MpmAltaEvolucao altaEvolucao = new MpmAltaEvolucao();
		
		try {
			systemUnderTest.preAtualizarAltaEvolucao(altaEvolucao);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NAO Deveria retornar exception");
		}
	}
	
	/**
	 * Pre remove.
	 * Testa retorno de erro.
	 */
	@Test
	public void verificaPreRemoverAltaEvolucao001() {
		MpmAltaEvolucao altaEvolucao = new MpmAltaEvolucao();
		
		try {
			Mockito.doThrow(new ApplicationBusinessException(ManterAltaSumarioRNExceptionCode.MPM_03735)).when(mockedManterAltaSumarioRN).verificarAltaSumarioAtivo(Mockito.any(MpmAltaSumario.class));
			
			systemUnderTest.preRemoverAltaEvolucao(altaEvolucao);
			Assert.fail("Deveria retornar exception");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Pre remove.
	 * Testa SEM retorno de erro.
	 */
	@Test
	public void verificaPreRemoverAltaEvolucao002() {
		MpmAltaEvolucao altaEvolucao = new MpmAltaEvolucao();
		
		try {
			systemUnderTest.preRemoverAltaEvolucao(altaEvolucao);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NAO Deveria retornar exception");
		}
	}
	
	/**
	 * Testa a verificação da evolução da alta do paciente.
	 * O valor de retorno deve ser zero.
	 */
	@Test
	public void verificaValidarAltaEvolucao001() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		final List<Long> list = new ArrayList<Long>();
		list.add(Long.valueOf(0));

		Mockito.when(mockedMpmAltaEvolucaoDAO.listAltaEvolucao(Mockito.any(MpmAltaSumarioId.class))).thenReturn(list);

		Boolean retorno = systemUnderTest.validarAltaEvolucao(altaSumario.getId());
		
		Assert.assertFalse(retorno);
	}
	
	/**
	 * Testa a verificação da evolução da alta do paciente.
	 * O valor de retorno deve ser maior que zero.
	 */
	@Test
	public void verificaValidarAltaEvolucao002() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		final List<Long> list = new ArrayList<Long>();
		list.add(Long.valueOf(1));
		
		Mockito.when(mockedMpmAltaEvolucaoDAO.listAltaEvolucao(Mockito.any(MpmAltaSumarioId.class))).thenReturn(list);
		
		Boolean retorno = systemUnderTest.validarAltaEvolucao(altaSumario.getId());
		
		Assert.assertTrue(retorno);
	}
	
}
	
	

