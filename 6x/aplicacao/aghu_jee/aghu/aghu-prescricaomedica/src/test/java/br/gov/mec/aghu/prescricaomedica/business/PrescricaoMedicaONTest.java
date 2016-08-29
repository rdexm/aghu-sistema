package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEstadoPacienteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class PrescricaoMedicaONTest extends AGHUBaseUnitTest<PrescricaoMedicaON>{

	@Mock
	private MpmPrescricaoMedicaDAO mockedMpmPrescricaoMedicaDAO;
	@Mock
	private MpmAltaEstadoPacienteDAO mockedMpmAltaEstadoPacienteDAO;

	@Test
	public void isPrescricaoVigenteArgInvalidoTest() {
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setDthrInicio(new Date());
		Mockito.when(mockedMpmPrescricaoMedicaDAO.obterPorChavePrimaria(Mockito.any(MpmPrescricaoMedicaId.class))).thenReturn(null);

		
		try {
			this.systemUnderTest.isPrescricaoVigente(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

	}

	@Test
	public void isPrescricaoVigenteFalseTest() {
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();

		Calendar inicio = Calendar.getInstance();
		inicio.set(2010, Calendar.JANUARY, 01);
		prescricao.setDthrInicio(inicio.getTime());

		Calendar fim = Calendar.getInstance();
		fim.set(2010, Calendar.JANUARY, 10);
		prescricao.setDthrFim(fim.getTime());
		
		Mockito.when(mockedMpmPrescricaoMedicaDAO.obterPorChavePrimaria(Mockito.any(MpmPrescricaoMedicaId.class))).thenReturn(prescricao);
		
		boolean vigente = this.systemUnderTest.isPrescricaoVigente(prescricao);
		Assert.assertFalse(vigente);

	}

	@Test
	public void isPrescricaoVigenteTrueTest() {
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();

		Date hoje = Calendar.getInstance().getTime();
		prescricao.setDthrInicio(hoje);

		Calendar fim = Calendar.getInstance();
		fim.add(Calendar.HOUR_OF_DAY, 24);
		prescricao.setDthrFim(fim.getTime());

		Mockito.when(mockedMpmPrescricaoMedicaDAO.obterPorChavePrimaria(Mockito.any(MpmPrescricaoMedicaId.class))).thenReturn(prescricao);

		boolean vigente = this.systemUnderTest.isPrescricaoVigente(prescricao);
		Assert.assertTrue(vigente);

	}

}
