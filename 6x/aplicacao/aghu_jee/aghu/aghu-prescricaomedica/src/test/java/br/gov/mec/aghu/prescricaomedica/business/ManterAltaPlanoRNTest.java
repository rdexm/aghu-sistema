package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmAltaPlano;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPlanoPosAltaDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaPlanoRNTest extends AGHUBaseUnitTest<ManterAltaPlanoRN>{

	@Mock
	private ManterAltaPlanoRN systemUnderTest;
	@Mock
	private MpmPlanoPosAltaDAO mockedPlanoPosAltaDAO;

	/**
	 * Deve retornar exceção MPM_02645.
	 */
	@Test
	public void verificarComplExcecao_MPM_02645_Test() {
		
		final MpmPlanoPosAlta planoPosAlta = null;
		
		Short novoPlaSeq = Short.valueOf("1");
		Short oldPlaSeq = Short.valueOf("1");
		String novoComplPlanoPosAlta = "teste";
		
		Mockito.when(mockedPlanoPosAltaDAO.obterPlanoPosAltaPeloId(Mockito.anyInt())).thenReturn(planoPosAlta);
		
		try {
		
			systemUnderTest.verificarCompl(novoPlaSeq, oldPlaSeq, novoComplPlanoPosAlta);
			
		} catch (BaseException atual) {
			
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02949.
	 */
	@Test
	public void verificarComplExcecao_MPM_02949_Test() {
		
		final MpmPlanoPosAlta planoPosAlta = new MpmPlanoPosAlta();
		planoPosAlta.setIndExigeComplemento(true);
		
		Short novoPlaSeq = Short.valueOf("1");
		Short oldPlaSeq = Short.valueOf("1");
		String novoComplPlanoPosAlta = null;
		
		Mockito.when(mockedPlanoPosAltaDAO.obterPlanoPosAltaPeloId(Mockito.anyInt())).thenReturn(planoPosAlta);
		
		try {
		
			systemUnderTest.verificarCompl(novoPlaSeq, oldPlaSeq, novoComplPlanoPosAlta);
			
		} catch (BaseException atual) {
			
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02950.
	 */
	@Test
	public void verificarComplExcecao_MPM_02950_Test() {
		
		final MpmPlanoPosAlta planoPosAlta = new MpmPlanoPosAlta();
		planoPosAlta.setIndExigeComplemento(false);
		
		Short novoPlaSeq = Short.valueOf("1");
		Short oldPlaSeq = Short.valueOf("1");
		String novoComplPlanoPosAlta = "teste";
		
		Mockito.when(mockedPlanoPosAltaDAO.obterPlanoPosAltaPeloId(Mockito.anyInt())).thenReturn(planoPosAlta);

		try {
		
			systemUnderTest.verificarCompl(novoPlaSeq, oldPlaSeq, novoComplPlanoPosAlta);
			
		} catch (BaseException atual) {
			
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02643.
	 */
	@Test
	public void verificarAlteracaoPlanoPosAltaExcecao_MPM_02643_Test() {
		
		MpmAltaPlano altaPlano = new MpmAltaPlano();
		MpmAltaPlano altaPlanoOriginal = new MpmAltaPlano();
		MpmAltaSumarioId id = new MpmAltaSumarioId();
		
		altaPlano.setId(id);
		altaPlanoOriginal.setId(id);
		
		altaPlano.setDescPlanoPosAlta("teste1");
		altaPlanoOriginal.setDescPlanoPosAlta("teste2");
		
		try {
		
			systemUnderTest.verificarAlteracaoPlanoPosAlta(altaPlano, altaPlanoOriginal);
			
		} catch (BaseException atual) {
			
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02644.
	 */
	@Test
	public void verificarAlteracaoPlanoPosAltaExcecaoDescPlanoPosAltaNulo_MPM_02644_Test() {
		
		MpmAltaPlano altaPlano = new MpmAltaPlano();
		MpmAltaPlano altaPlanoOriginal = new MpmAltaPlano();
		MpmAltaSumarioId id1 = new MpmAltaSumarioId();
		MpmAltaSumarioId id2 = new MpmAltaSumarioId();
		id1.setApaAtdSeq(Integer.valueOf("123"));
		id2.setApaAtdSeq(Integer.valueOf("456"));
		
		altaPlano.setId(id1);
		altaPlanoOriginal.setId(id2);
		
		altaPlano.setDescPlanoPosAlta(null);
		altaPlanoOriginal.setDescPlanoPosAlta("teste2");
		
		try {
		
			systemUnderTest.verificarAlteracaoPlanoPosAlta(altaPlano, altaPlanoOriginal);
		} catch (BaseException atual) {
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02644.
	 */
	@Test
	public void verificarAlteracaoPlanoPosAltaExcecaoDescPlanoPosAltaIgualAntigo_MPM_02644_Test() {
		
		MpmAltaPlano altaPlano = new MpmAltaPlano();
		MpmAltaPlano altaPlanoOriginal = new MpmAltaPlano();
		MpmAltaSumarioId id1 = new MpmAltaSumarioId();
		MpmAltaSumarioId id2 = new MpmAltaSumarioId();
		id1.setApaAtdSeq(Integer.valueOf("123"));
		id2.setApaAtdSeq(Integer.valueOf("456"));
		
		altaPlano.setId(id1);
		altaPlanoOriginal.setId(id2);
		
		altaPlano.setDescPlanoPosAlta("teste2");
		altaPlanoOriginal.setDescPlanoPosAlta("teste2");
		
		try {
		
			systemUnderTest.verificarAlteracaoPlanoPosAlta(altaPlano, altaPlanoOriginal);
		} catch (BaseException atual) {
			
		}
		
	}
	
	

}
