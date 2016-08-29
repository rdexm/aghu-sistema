package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeFatDiagPaciente;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnosticoId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatDiagPacienteDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelDiagnosticoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class EtiologiaDiagnosticoRNTest extends AGHUBaseUnitTest<EtiologiaDiagnosticoRN>{

	@Mock
	private EpeCuidadoDiagnosticoDAO mockedEpeCuidadoDiagnosticoDAO;
	@Mock
	private EpeFatDiagPacienteDAO mockedEpeFatDiagPacienteDAO;
	@Mock
	private EpeFatRelDiagnosticoDAO mockedEpeFatRelDiagnosticoDAO;

	@Test
	public void prePersistirEtiologiaDiagnosticoTest() throws BaseException {
		EpeFatRelDiagnostico etiologiaDiagnostico = new EpeFatRelDiagnostico();
		etiologiaDiagnostico.setSituacao(DominioSituacao.I);
		try {
			systemUnderTest
					.prePersistirEtiologiaDiagnostico(etiologiaDiagnostico);
			Assert.fail("Deveria ter ocorrido a excessão MENSAGEM_ERRO_ADICAO_MANTER_DIAGNOSTICO_ETIOLOGIA");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					"MENSAGEM_ERRO_ADICAO_MANTER_DIAGNOSTICO_ETIOLOGIA",
					e.getMessage());
		}
	}

	@Test
	public void preDeleteEpeFatDiagPacienteTest() throws BaseException {

		List<EpeFatDiagPaciente> epeFatDiagPaciente = new ArrayList<EpeFatDiagPaciente>();
		epeFatDiagPaciente.add(new EpeFatDiagPaciente());
		Mockito.when(mockedEpeFatDiagPacienteDAO.obterEpeCuidadoDiagnostico(Mockito.anyShort(), Mockito.anyShort(), Mockito.anyShort(), Mockito.anyShort()))
		.thenReturn(epeFatDiagPaciente);

		Mockito.when(mockedEpeCuidadoDiagnosticoDAO.obterEpeCuidadoDiagnostico(Mockito.anyShort(), Mockito.anyShort(), Mockito.anyShort(), Mockito.anyShort()))
		.thenReturn(null);

		try {
			EpeFatRelDiagnostico etiologiaDiagnostico = new EpeFatRelDiagnostico();
			EpeFatRelDiagnosticoId id = new EpeFatRelDiagnosticoId();
			id.setDgnSequencia(Short.valueOf("1"));
			id.setDgnSnbGnbSeq(Short.valueOf("1"));
			id.setDgnSnbSequencia(Short.valueOf("1"));
			id.setFreSeq(Short.valueOf("1"));
			etiologiaDiagnostico.setId(id);
			systemUnderTest.preDelete(etiologiaDiagnostico);
			Assert.fail("Deveria ter ocorrido a excessão MENSAGEM_ERRO_EXCLUSAO_1_MANTER_DIAGNOSTICO_ETIOLOGIA");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					"MENSAGEM_ERRO_EXCLUSAO_1_MANTER_DIAGNOSTICO_ETIOLOGIA",
					e.getMessage());
		}
	}

	@Test
	public void preDeleteEpeCuidadoDiagnosticoTest()
			throws BaseException {
		Mockito.when(mockedEpeFatDiagPacienteDAO.obterEpeCuidadoDiagnostico(Mockito.anyShort(), Mockito.anyShort(), Mockito.anyShort(), Mockito.anyShort()))
		.thenReturn(null);

		List<EpeCuidadoDiagnostico> epeCuidadoDiagnostico = new ArrayList<EpeCuidadoDiagnostico>();
		epeCuidadoDiagnostico.add(new EpeCuidadoDiagnostico());
		Mockito.when(mockedEpeCuidadoDiagnosticoDAO.obterEpeCuidadoDiagnostico(Mockito.anyShort(), Mockito.anyShort(), Mockito.anyShort(), Mockito.anyShort()))
		.thenReturn(epeCuidadoDiagnostico);

		try {
			EpeFatRelDiagnostico etiologiaDiagnostico = new EpeFatRelDiagnostico();
			EpeFatRelDiagnosticoId id = new EpeFatRelDiagnosticoId();
			id.setDgnSequencia(Short.valueOf("1"));
			id.setDgnSnbGnbSeq(Short.valueOf("1"));
			id.setDgnSnbSequencia(Short.valueOf("1"));
			id.setFreSeq(Short.valueOf("1"));
			etiologiaDiagnostico.setId(id);
			systemUnderTest.preDelete(etiologiaDiagnostico);
			Assert.fail("Deveria ter ocorrido a excessão MENSAGEM_ERRO_EXCLUSAO_2_MANTER_DIAGNOSTICO_ETIOLOGIA");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					"MENSAGEM_ERRO_EXCLUSAO_2_MANTER_DIAGNOSTICO_ETIOLOGIA",
					e.getMessage());
		}
	}

}
