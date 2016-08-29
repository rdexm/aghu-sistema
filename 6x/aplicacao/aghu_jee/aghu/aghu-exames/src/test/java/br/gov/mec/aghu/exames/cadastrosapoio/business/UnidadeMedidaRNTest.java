package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import br.gov.mec.aghu.exames.cadastrosapoio.business.UnidadeMedidaRN.ManterUnidadeMedidaRNExceptionCode;
import br.gov.mec.aghu.model.AelUnidMedValorNormal;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class UnidadeMedidaRNTest extends AGHUBaseUnitTest<UnidadeMedidaRN>{


	private AelUnidMedValorNormal aelUnidMedValorNormal;
	private AelUnidMedValorNormal aelUnidMedValorNormalOld;


	@Before
	public void doBeforeEachTestCase() {
		aelUnidMedValorNormal = new AelUnidMedValorNormal();
		aelUnidMedValorNormalOld = new AelUnidMedValorNormal();
	}

	@Test
	public void testPreUpdateUnidadeMedidaSuccess() {
		try {
			systemUnderTest.preAtualizarUnidadeMedida(aelUnidMedValorNormal, aelUnidMedValorNormal);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}

		//Passa no teste caso nao tenha ido lançada uma exceção.
		assertEquals(1, 1);
	}
	@Test
	public void testPreUpdateUnidadeMedidaErro1() {
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_WEEK, -1);
		
		aelUnidMedValorNormal.setCriadoEm(Calendar.getInstance().getTime());
		aelUnidMedValorNormalOld.setCriadoEm(ontem.getTime());
		

		try {
			systemUnderTest.preAtualizarUnidadeMedida(aelUnidMedValorNormal, aelUnidMedValorNormalOld);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterUnidadeMedidaRNExceptionCode.AEL_01739);
		}
	}
	
	@Test
	public void testPreUpdateUnidadeMedidaErro2() {
		RapServidores servidor = new RapServidores();
		RapServidoresId id1 = new RapServidoresId();
		id1.setMatricula(Integer.valueOf("123"));
		id1.setVinCodigo(Short.valueOf("1"));
		servidor.setId(id1);
		
		RapServidores servidor2 = new RapServidores();
		RapServidoresId id2 = new RapServidoresId();
		id2.setMatricula(Integer.valueOf("321"));
		id2.setVinCodigo(Short.valueOf("1"));
		servidor2.setId(id2);
		
		
		
		aelUnidMedValorNormal.setServidor(servidor);
		aelUnidMedValorNormalOld.setServidor(servidor2);
		

		try {
			systemUnderTest.preAtualizarUnidadeMedida(aelUnidMedValorNormal, aelUnidMedValorNormalOld);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterUnidadeMedidaRNExceptionCode.AEL_01739);
		}
	}
	
	@Test
	public void testPreUpdateUnidadeMedidaErro3() {
		RapServidores servidor = new RapServidores();
		RapServidoresId id1 = new RapServidoresId();
		id1.setMatricula(Integer.valueOf("123"));
		id1.setVinCodigo(Short.valueOf("2"));
		servidor.setId(id1);
		
		RapServidores servidor2 = new RapServidores();
		RapServidoresId id2 = new RapServidoresId();
		id2.setMatricula(Integer.valueOf("123"));
		id2.setVinCodigo(Short.valueOf("1"));
		servidor2.setId(id2);
		
		
		
		aelUnidMedValorNormal.setServidor(servidor);
		aelUnidMedValorNormalOld.setServidor(servidor2);
		

		try {
			systemUnderTest.preAtualizarUnidadeMedida(aelUnidMedValorNormal, aelUnidMedValorNormalOld);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterUnidadeMedidaRNExceptionCode.AEL_01739);
		}
	}
	
	@Test
	public void testPreUpdateUnidadeMedidaErro4() {
		aelUnidMedValorNormal.setDescricao("Desc1");
		aelUnidMedValorNormalOld.setDescricao("Desc2");
		

		try {
			systemUnderTest.preAtualizarUnidadeMedida(aelUnidMedValorNormal, aelUnidMedValorNormalOld);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterUnidadeMedidaRNExceptionCode.AEL_01740);
		}
	}
}
