package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEstadoPacienteDAO;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Testes basicos para os metodos de verificacao da classe ManterAltaEstadoPacienteRN. <br>
 * 
 * @author gfmenezes
 *
 */
public class ManterAltaEstadoPacienteRNTest extends AGHUBaseUnitTest<ManterAltaEstadoPacienteRN>{

	@Mock
	private MpmAltaEstadoPacienteDAO mockedMpmAltaEstadoPacienteDAO;

	/**
	 * Valida se existe o estado clínico do paciente.
	 * Testa o retorno de valor zero.
	 */
	@Test
	public void testarValidarAltaEstadoPaciente001(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		final List<Long> list = new ArrayList<Long>();
		list.add(Long.valueOf(0));
		
		Mockito.when(mockedMpmAltaEstadoPacienteDAO.listAltaEstadoPaciente(Mockito.any(MpmAltaSumarioId.class))).thenReturn(list);
		
		Boolean retorno = systemUnderTest.validarAltaEstadoPaciente(altaSumario.getId());
		
		Assert.assertFalse(retorno);
	}
	
	/**
	 * Valida se existe o estado clínico do paciente.
	 * Testa o retorno de valor maior que zero.
	 */
	@Test
	public void testarValidarAltaEstadoPaciente002(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(null);

		final List<Long> list = new ArrayList<Long>();
		list.add(Long.valueOf(1));
		
		Mockito.when(mockedMpmAltaEstadoPacienteDAO.listAltaEstadoPaciente(Mockito.any(MpmAltaSumarioId.class))).thenReturn(list);
		
		Boolean retorno = systemUnderTest.validarAltaEstadoPaciente(altaSumario.getId());
		
		Assert.assertTrue(retorno);
	}
	
}
