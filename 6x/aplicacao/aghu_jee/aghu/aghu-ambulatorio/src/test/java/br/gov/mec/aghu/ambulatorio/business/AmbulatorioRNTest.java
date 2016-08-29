package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AmbulatorioRNTest extends AGHUBaseUnitTest<AmbulatorioRN>{

    @Mock
    private IPacienteFacade mockedPacienteFacade;
    @Mock
    private AacConsultasDAO mockedConsultasDAO;

	@Test(expected=BaseException.class)
	public void testVerificarSituacaoConsultaAtiva() throws BaseException {
		final AghEspecialidades especialidade = new AghEspecialidades();
				especialidade.setIndSituacao(DominioSituacao.I);
		
		systemUnderTest.verificaSituacaoEspecialidade(especialidade);
	}	
	
	
	@Test
	public void testValidaDadosPaciente(){
		final AipPacientes paciente=new AipPacientes();
			paciente.setAipPacientesDadosCns(new AipPacientesDadosCns());
			paciente.setRg("1l");

		Mockito.when(mockedPacienteFacade.obterAipPacientesPorChavePrimaria(Mockito.anyInt())).
		thenReturn(paciente);

		List<String> list = systemUnderTest.validaDadosPaciente(paciente.getCodigo());
		Assert.assertTrue(list.isEmpty());
	}

	
	@Test
	public void necessitaRecadastramentoPaciente(){
		final AipPacientes paciente=new AipPacientes();
		final Date dtConsulta=new Date();	
			paciente.setDtRecadastro(new Date());
		
		Mockito.when(mockedConsultasDAO.ultimaDataConsultaPaciente(paciente.getCodigo(), dtConsulta))
		.thenReturn(new Date());
		
		Mockito.when(mockedPacienteFacade.obterAipPacientesPorChavePrimaria(Mockito.anyInt())).
		thenReturn(paciente);
		
		Boolean result = systemUnderTest.necessitaRecadastramentoPaciente(paciente.getCodigo(), paciente.getDtRecadastro(), dtConsulta);
		Assert.assertFalse(result);
	}
}
