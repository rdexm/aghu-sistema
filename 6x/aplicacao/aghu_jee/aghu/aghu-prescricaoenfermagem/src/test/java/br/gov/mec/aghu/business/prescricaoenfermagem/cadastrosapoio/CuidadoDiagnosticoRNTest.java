package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidDiagnosticoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class CuidadoDiagnosticoRNTest extends AGHUBaseUnitTest<CuidadoDiagnosticoRN>{

	@Mock
	private EpePrescCuidDiagnosticoDAO mockedEpePrescCuidDiagnosticoDAO;

	@Test
	public void prePersistirValidarEpeCuidadosTest() throws BaseException {
		EpeCuidados cuidado = new EpeCuidados();
		cuidado.setIndSituacao(DominioSituacao.I);
		try {
			systemUnderTest.prePersistirValidarEpeCuidados(cuidado);
			Assert.fail("Deveria ter ocorrido a excessão MENSAGEM_ERRO_ADICAO_MANTER_DIAGNOSTICO_CUIDADO");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("MENSAGEM_ERRO_ADICAO_MANTER_DIAGNOSTICO_CUIDADO", e.getMessage());
		}
	}

	@Test
	public void preRemoverValidarCuidadoRelacionadoPrescricaoTest() throws BaseException {
		Mockito.when(mockedEpePrescCuidDiagnosticoDAO.verificarCuidadoRelacionadoPrescricao(Mockito.anyShort(), Mockito.anyShort(), Mockito.anyShort(), Mockito.anyShort(),
				Mockito.anyShort())).thenReturn(true);

		Short cuiSeq = 0;
		Short freSeq = 0;
		Short dgnSequencia = 0;
		Short dgnSnbSequencia = 0;
		Short dgnSnbGnbSeq = 0;
		try {
			systemUnderTest.preRemoverValidarCuidadoRelacionadoPrescricao(cuiSeq, freSeq, dgnSequencia, dgnSnbSequencia, dgnSnbGnbSeq);
			Assert.fail("Deveria ter ocorrido a excessão MENSAGEM_ERRO_EXCLUSAO_MANTER_DIAGNOSTICO_CUIDADO");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("MENSAGEM_ERRO_EXCLUSAO_MANTER_DIAGNOSTICO_CUIDADO", e.getMessage());
		}
	}

}
