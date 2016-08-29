package br.gov.mec.aghu.exames.agendamento.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ConsultaHorarioLivreRNTest extends AGHUBaseUnitTest<ConsultaHorarioLivreRN>{

	@Mock
	private AelTipoAmostraExameDAO mockedTipoAmostraExameDAO;
	
	@Test
	public void testVerificarResponsavelColetaExameFalso() {
		final List<AelTipoAmostraExame> lista = new ArrayList<AelTipoAmostraExame>();
		
		Mockito.when(mockedTipoAmostraExameDAO.pesquisarResponsavelColeta(Mockito.anyString(), Mockito.anyInt())).thenReturn(lista);
		
		Assert.assertFalse(systemUnderTest.verificarResponsavelColetaExame(null, null));
	}
	
	@Test
	public void testVerificarResponsavelColetaExameVerdadeiro() {
		final List<AelTipoAmostraExame> lista = new ArrayList<AelTipoAmostraExame>();
		lista.add(new AelTipoAmostraExame());

		Mockito.when(mockedTipoAmostraExameDAO.pesquisarResponsavelColeta(Mockito.anyString(), Mockito.anyInt())).thenReturn(lista);

		Assert.assertTrue(systemUnderTest.verificarResponsavelColetaExame(null, null));
	}
	
}
