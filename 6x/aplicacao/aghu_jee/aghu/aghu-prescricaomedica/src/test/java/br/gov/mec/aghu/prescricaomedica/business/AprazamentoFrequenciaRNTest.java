package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAprazamentoFrequencia;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.business.AprazamentoFrequenciaRN.AprazamentoExceptionCode;
import br.gov.mec.aghu.prescricaomedica.business.PrescricaoMedicaRN.PrescricaoMedicamentoExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AprazamentoFrequenciaRNTest extends AGHUBaseUnitTest<AprazamentoFrequenciaRN> {

	@Test
	public void verificaTipoFrequenciaAprazamentoExceptionTest() {
		boolean isFail = true;

		// TESTA SE EXISTE INSTANCIA DO TIPO FREQUÊNCIA
		MpmAprazamentoFrequencia entity = new MpmAprazamentoFrequencia();
		try {
			systemUnderTest.verificaTipoFrequenciaAprazamento(entity);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(PrescricaoMedicamentoExceptionCode.MPM_00749));
			isFail = false;
		}

		// TESTA SITUAÇÃO ATIVA
		MpmTipoFrequenciaAprazamento tipo = new MpmTipoFrequenciaAprazamento();
		tipo.setSeq(Short.valueOf("1"));
		entity.setTipoFreqAprazamento(tipo);
		try {
			systemUnderTest.verificaTipoFrequenciaAprazamento(entity);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(AprazamentoExceptionCode.MPM_01199));
			isFail = false;
		}

		// TESTA FORMA CÁLCULO
		tipo.setIndSituacao(DominioSituacao.A);
		tipo.setIndFormaAprazamento(DominioFormaCalculoAprazamento.I);
		try {
			systemUnderTest.verificaTipoFrequenciaAprazamento(entity);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(AprazamentoExceptionCode.MPM_00795));
			isFail = false;
		}
		if (isFail) {
			Assert.fail("Todos os testes falharam para esta classe!");
		}
	}

	@Test(expected = BaseException.class)
	public void verificarMatriculaExceptionTest() throws BaseException {
		MpmAprazamentoFrequencia entity = new MpmAprazamentoFrequencia();
		systemUnderTest.verificaMatricula(entity);
	}

}
