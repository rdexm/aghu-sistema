package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmAltaCirgRealizada;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ManterAltaCirgRealizadaRNTest extends AGHUBaseUnitTest<ManterAltaCirgRealizadaRN>{

	@Mock
	private ManterAltaSumarioRN mockedAltaSumarioRN;
	
	/**
	 * Deve retornar exceção MPM_02872.
	 * @throws BaseException 
	 */
	@Test
	public void verificarDataCirurgiaExcecao_MPM_02872_Test() throws BaseException {

		final Date dataInicio = DateUtil.adicionaDias(new Date(), -3);
		MpmAltaCirgRealizada altaCirgRealizada = new MpmAltaCirgRealizada();
		altaCirgRealizada.setDthrCirurgia(DateUtil.adicionaDias(new Date(), -4));		
		MpmAltaSumario altaSumario = new MpmAltaSumario();
		MpmAltaSumarioId id = new MpmAltaSumarioId();
		altaSumario.setId(id);
		altaCirgRealizada.setAltaSumario(altaSumario);
		
		Mockito.when(mockedAltaSumarioRN.obterDataInternacao2(Mockito.anyInt())).
		thenReturn(dataInicio);
		
		try {

			systemUnderTest.verificarDataCirurgia(altaCirgRealizada);
			fail("Deveria ter ocorrido uma exceção MPM_02872!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02872", e.getMessage());

		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02622.
	 * @throws BaseException 
	 */
	@Test
	public void verificarIndCargaExcecao_MPM_02622_Test() throws BaseException {

		MpmAltaCirgRealizada altaCirgRealizadaNova = new MpmAltaCirgRealizada();
		MpmAltaCirgRealizada altaCirgRealizadaVelha = new MpmAltaCirgRealizada();
		altaCirgRealizadaNova.setIndCarga(true);
		altaCirgRealizadaVelha.setIndCarga(false);
		
		try {

			systemUnderTest.verificarIndCarga(altaCirgRealizadaNova, altaCirgRealizadaVelha);
			fail("Deveria ter ocorrido uma exceção MPM_02622!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02622", e.getMessage());

		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02624.
	 * @throws BaseException 
	 */
	@Test
	public void validarCamposAlteradosExcecao_MPM_02624_Test() throws BaseException {

		MpmAltaCirgRealizada altaCirgRealizadaNova = new MpmAltaCirgRealizada();
		MpmAltaCirgRealizada altaCirgRealizadaVelha = new MpmAltaCirgRealizada();
		altaCirgRealizadaNova.setIndCarga(true);
		altaCirgRealizadaVelha.setIndCarga(false);
		
		try {

			systemUnderTest.validarCamposAlterados(altaCirgRealizadaNova, altaCirgRealizadaVelha);
			fail("Deveria ter ocorrido uma exceção MPM_02624!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02624", e.getMessage());

		}
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws BaseException 
	 */
	@Test
	public void validarCamposAlteradosSemExcecaoTest() throws BaseException {

		MpmAltaCirgRealizada altaCirgRealizadaNova = new MpmAltaCirgRealizada();
		MpmAltaCirgRealizada altaCirgRealizadaVelha = new MpmAltaCirgRealizada();
		altaCirgRealizadaNova.setIndCarga(false);
		altaCirgRealizadaVelha.setIndCarga(false);
		
		altaCirgRealizadaNova.setDescCirurgia("teste1");
		altaCirgRealizadaVelha.setDescCirurgia("teste2");
		
		systemUnderTest.validarCamposAlterados(altaCirgRealizadaNova, altaCirgRealizadaVelha);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02650.
	 * @throws BaseException 
	 */
	@Test
	public void verificarAlteracoesUpdateAltaCirgRealizadaExcecao_MPM_02650_Test() throws BaseException {

		MpmAltaCirgRealizada altaCirgRealizadaNova = new MpmAltaCirgRealizada();
		MpmAltaCirgRealizada altaCirgRealizadaVelha = new MpmAltaCirgRealizada();
		MbcProcEspPorCirurgias procedimentoEspCirurgia = new MbcProcEspPorCirurgias();
		MbcProcEspPorCirurgiasId id = new MbcProcEspPorCirurgiasId();
		procedimentoEspCirurgia.setId(id);
		
		altaCirgRealizadaNova.setIndCarga(false);
		altaCirgRealizadaNova.setProcedimentoEspCirurgia(procedimentoEspCirurgia);
		
		try {

			systemUnderTest.verificarAlteracoesUpdateAltaCirgRealizada(altaCirgRealizadaNova, altaCirgRealizadaVelha);
			fail("Deveria ter ocorrido uma exceção MPM_02650!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02650", e.getMessage());

		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02619.
	 * @throws BaseException 
	 */
	@Test
	public void verificarAlteracoesUpdateAltaCirgRealizadaExcecao_MPM_02619_Test() throws BaseException {

		MpmAltaCirgRealizada altaCirgRealizadaNova = new MpmAltaCirgRealizada();
		MpmAltaCirgRealizada altaCirgRealizadaVelha = new MpmAltaCirgRealizada();
		MbcProcEspPorCirurgias procedimentoEspCirurgia = new MbcProcEspPorCirurgias();
		MbcProcedimentoCirurgicos procedimentoCirurgico = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico.setSeq(Integer.valueOf("1"));
		MbcProcEspPorCirurgiasId id = new MbcProcEspPorCirurgiasId();
		procedimentoEspCirurgia.setId(id);
		
		altaCirgRealizadaNova.setIndCarga(false);
		
		altaCirgRealizadaNova.setDescCirurgia("teste1");
		altaCirgRealizadaVelha.setDescCirurgia("teste2");
		
		altaCirgRealizadaNova.setProcedimentoCirurgico(procedimentoCirurgico);
		altaCirgRealizadaVelha.setProcedimentoCirurgico(procedimentoCirurgico);
		
		try {

			systemUnderTest.verificarAlteracoesUpdateAltaCirgRealizada(altaCirgRealizadaNova, altaCirgRealizadaVelha);
			fail("Deveria ter ocorrido uma exceção MPM_02619!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02619", e.getMessage());

		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02649.
	 * @throws BaseException 
	 */
	@Test
	public void verificarAlteracoesUpdateAltaCirgRealizadaExcecao_MPM_02649_Test() throws BaseException {

		MpmAltaCirgRealizada altaCirgRealizadaNova = new MpmAltaCirgRealizada();
		MpmAltaCirgRealizada altaCirgRealizadaVelha = new MpmAltaCirgRealizada();
		MbcProcEspPorCirurgias procedimentoEspCirurgia = new MbcProcEspPorCirurgias();
		MbcProcedimentoCirurgicos procedimentoCirurgico1 = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico1.setSeq(Integer.valueOf("1"));
		MbcProcedimentoCirurgicos procedimentoCirurgico2 = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico2.setSeq(Integer.valueOf("2"));
		MbcProcEspPorCirurgiasId id = new MbcProcEspPorCirurgiasId();
		procedimentoEspCirurgia.setId(id);
		
		altaCirgRealizadaNova.setIndCarga(false);
		
		altaCirgRealizadaNova.setDescCirurgia("teste1");
		altaCirgRealizadaVelha.setDescCirurgia("teste1");
		
		altaCirgRealizadaNova.setProcedimentoCirurgico(procedimentoCirurgico1);
		altaCirgRealizadaVelha.setProcedimentoCirurgico(procedimentoCirurgico2);
		
		try {

			systemUnderTest.verificarAlteracoesUpdateAltaCirgRealizada(altaCirgRealizadaNova, altaCirgRealizadaVelha);
			fail("Deveria ter ocorrido uma exceção MPM_02649!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02649", e.getMessage());

		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02620.
	 * @throws BaseException 
	 */
	@Test
	public void verificarAlteracoesUpdateAltaCirgRealizadaExcecao_MPM_02620_Test() throws BaseException {

		MpmAltaCirgRealizada altaCirgRealizadaNova = new MpmAltaCirgRealizada();
		MpmAltaCirgRealizada altaCirgRealizadaVelha = new MpmAltaCirgRealizada();
		MbcProcEspPorCirurgias procedimentoEspCirurgia = new MbcProcEspPorCirurgias();
		MbcProcedimentoCirurgicos procedimentoCirurgico1 = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico1.setSeq(Integer.valueOf("1"));
		MbcProcedimentoCirurgicos procedimentoCirurgico2 = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico2.setSeq(Integer.valueOf("2"));
		MbcProcEspPorCirurgiasId id = new MbcProcEspPorCirurgiasId();
		procedimentoEspCirurgia.setId(id);
		
		altaCirgRealizadaNova.setIndCarga(true);
		
		altaCirgRealizadaNova.setDescCirurgia("teste1");
		altaCirgRealizadaVelha.setDescCirurgia("teste1");
		
		altaCirgRealizadaNova.setProcedimentoCirurgico(procedimentoCirurgico1);
		altaCirgRealizadaVelha.setProcedimentoCirurgico(procedimentoCirurgico2);
		altaCirgRealizadaNova.setProcedimentoEspCirurgia(null);
		
		try {

			systemUnderTest.verificarAlteracoesUpdateAltaCirgRealizada(altaCirgRealizadaNova, altaCirgRealizadaVelha);
			fail("Deveria ter ocorrido uma exceção MPM_02620!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02620", e.getMessage());

		}
		
	}
	
	
	
	
	

}
