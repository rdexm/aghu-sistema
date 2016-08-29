package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidMedicamentoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class CuidadoMedicamentoRNTest extends AGHUBaseUnitTest<CuidadoMedicamentoRN>{

	@Mock
	private EpePrescCuidMedicamentoDAO mockedEpePrescCuidMedicamentoDAO;

	@Test
	public void prePersistirValidarHorasTest() throws BaseException {
		Integer horasAntes = null, horasApos = null;
		try {
			systemUnderTest.prePersistirValidarHoras(horasAntes, horasApos);
			Assert.fail("Deveria ter ocorrido a excessão EPE_00179");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("EPE_00179", e.getMessage());
		}
	}

	@Test
	public void preRemoverValidarCuidadoRelacionadoPrescricaoTest() throws BaseException {
		Mockito.when(mockedEpePrescCuidMedicamentoDAO.verificarCuidadoRelacionadoPrescricao(Mockito.anyShort(), Mockito.anyInt())).thenReturn(true);

		Short cuiSeq = 0;
		Integer medMatCodigo = 0;
		try {
			systemUnderTest.preRemoverValidarCuidadoRelacionadoPrescricao(cuiSeq, medMatCodigo);
			Assert.fail("Deveria ter ocorrido a excessão MENSAGEM_ERRO_EXCLUSAO_MANTER_MEDICAMENTOS_CUIDADOS");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("MENSAGEM_ERRO_EXCLUSAO_MANTER_MEDICAMENTOS_CUIDADOS", e.getMessage());
		}
	}

}
