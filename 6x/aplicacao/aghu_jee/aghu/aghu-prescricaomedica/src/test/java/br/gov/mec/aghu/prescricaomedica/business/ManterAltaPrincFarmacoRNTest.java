package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmAltaPrincFarmaco;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaPrincFarmacoRNTest extends AGHUBaseUnitTest<ManterAltaPrincFarmacoRN>{


	/**
	 * Não deve retornar exceção pois o Ind Carga é True.
	 * @throws BaseException
	 */
	@Test
	public void verificarCodMedicamentosModificadosIndCargaTrueTest() throws BaseException {

		MpmAltaPrincFarmaco altaPrincFarmaco = new MpmAltaPrincFarmaco();
		MpmAltaPrincFarmaco altaPrincFarmacoVelho = new MpmAltaPrincFarmaco();

		altaPrincFarmaco.setIndCarga(true);

		systemUnderTest.verificarCodMedicamentosModificados(altaPrincFarmaco, altaPrincFarmacoVelho);

	}

	/**
	 * Deve retornar exceção MPM-02604.
	 * @throws BaseException
	 */
	@Test
	public void verificarCodMedicamentosModificadosIndCargaFalseExcecao_MPM_02604_Test() throws BaseException {

		MpmAltaPrincFarmaco altaPrincFarmaco = new MpmAltaPrincFarmaco();
		MpmAltaPrincFarmaco altaPrincFarmacoVelho = new MpmAltaPrincFarmaco();

		altaPrincFarmaco.setIndCarga(false);
		altaPrincFarmaco.setDescMedicamento("teste1");
		altaPrincFarmacoVelho.setDescMedicamento("teste2");

		AfaMedicamento medicamento = new AfaMedicamento();
		medicamento.setMatCodigo(Integer.valueOf("12335"));
		altaPrincFarmaco.setMedicamento(medicamento);
		altaPrincFarmacoVelho.setMedicamento(medicamento);

		try {

			systemUnderTest.verificarCodMedicamentosModificados(altaPrincFarmaco, altaPrincFarmacoVelho);
			fail("Deveria ter ocorrido uma exceção MPM_02604!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02604", e.getMessage());

		} 

	}
	
	/**
	 * Deve retornar exceção MPM_02611.
	 * @throws BaseException
	 */
	@Test
	public void verificarCodMedicamentosModificadosIndCargaFalseExcecao_MPM_02611_Test() throws BaseException {

		MpmAltaPrincFarmaco altaPrincFarmaco = new MpmAltaPrincFarmaco();
		MpmAltaPrincFarmaco altaPrincFarmacoVelho = new MpmAltaPrincFarmaco();

		altaPrincFarmaco.setIndCarga(false);
		altaPrincFarmaco.setDescMedicamento("teste1");
		altaPrincFarmacoVelho.setDescMedicamento("teste1");

		AfaMedicamento medicamento1 = new AfaMedicamento();
		medicamento1.setMatCodigo(Integer.valueOf("123356"));
		altaPrincFarmaco.setMedicamento(medicamento1);
		
		AfaMedicamento medicamento2 = new AfaMedicamento();
		medicamento2.setMatCodigo(Integer.valueOf("1233567"));
		altaPrincFarmacoVelho.setMedicamento(medicamento2);

		try {

			systemUnderTest.verificarCodMedicamentosModificados(altaPrincFarmaco, altaPrincFarmacoVelho);
			fail("Deveria ter ocorrido uma exceção MPM_02611!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02611", e.getMessage());

		} 

	}

}
