package br.gov.mec.aghu.ambulatorio.business;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.ambulatorio.business.AmbulatorioConsultaRN.AmbulatorioConsultasRNExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AmbulatorioConsultaRNTest extends AGHUBaseUnitTest<AmbulatorioConsultaRN>{

	@Test(expected=BaseException.class)
	public void testVerificarSituacaoConsultaAtiva() throws BaseException {
				AacSituacaoConsultas situacao = new AacSituacaoConsultas();
				situacao.setIndSituacao(DominioSituacao.I);
				systemUnderTest.verificarSituacaoConsultaAtiva(situacao);
	}
	
	@Test(expected=BaseException.class)
	public void testVerificarExclusaoConsulta() throws BaseException {
				AacConsultas consulta = new AacConsultas();
				AacSituacaoConsultas situacaoConsulta = new AacSituacaoConsultas();
				situacaoConsulta.setSituacao("B");
				consulta.setSituacaoConsulta(situacaoConsulta);
				systemUnderTest.verificarExclusaoConsulta(consulta);
	}
	
	@Test
	public void testVerificarExclusaoConsulta_LancaExcecao_AAC_00102() throws BaseException {
			AacConsultas consulta = new AacConsultas();
			AacSituacaoConsultas situacaoConsulta = new AacSituacaoConsultas();
			situacaoConsulta.setSituacao("M");
			consulta.setSituacaoConsulta(situacaoConsulta);
			consulta.setExcedeProgramacao(Boolean.FALSE);
			try {
				systemUnderTest.verificarExclusaoConsulta(consulta);
				Assert.fail("Deveria ocorrer AGHUNegocioExceptionSemRollback");
			} catch (ApplicationBusinessException e) {
				Assert.assertTrue(e.getCode() == AmbulatorioConsultasRNExceptionCode.AAC_00102);
			}
	}
	
	@Test
	public void testVerificarExclusaoConsulta_Nao_LancaExcecao_AAC_00102() throws BaseException {
			AacConsultas consulta = new AacConsultas();
			AacSituacaoConsultas situacaoConsulta = new AacSituacaoConsultas();
			situacaoConsulta.setSituacao("M");
			consulta.setSituacaoConsulta(situacaoConsulta);
			consulta.setExcedeProgramacao(Boolean.TRUE);
			try {
				systemUnderTest.verificarExclusaoConsulta(consulta);
				Assert.assertTrue(Boolean.TRUE.equals(consulta.getExcedeProgramacao()));
			} catch (ApplicationBusinessException e) {
				Assert.fail("Nao deveria ocorrer AGHUNegocioExceptionSemRollback");
			}
	}
	
}
