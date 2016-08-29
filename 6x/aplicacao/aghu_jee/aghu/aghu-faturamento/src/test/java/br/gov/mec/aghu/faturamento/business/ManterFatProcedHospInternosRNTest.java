package br.gov.mec.aghu.faturamento.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoOperacaoConversao;
import br.gov.mec.aghu.faturamento.business.ManterFatProcedHospInternosRN.ManterFatProcedHospInternosRNExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosJnDAO;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterFatProcedHospInternosRNTest extends AGHUBaseUnitTest<ManterFatProcedHospInternosRN>{


	@Mock
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@Mock
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	@Mock
	private FatProcedHospInternosJnDAO fatProcedHospInternosJnDAO;
	private FatProcedHospInternos fatProcedHospInternos = new FatProcedHospInternos();
	private FatProcedHospInternos fatProcedHospInternosOriginal = new FatProcedHospInternos();


	@Test
	public void testVerificarPreenchimentoExcecao00878() {

		fatProcedHospInternos.setTipoOperConversao(DominioTipoOperacaoConversao.D);
		fatProcedHospInternos.setFatorConversao(null);

		try {
			systemUnderTest.verificarPreenchimento(fatProcedHospInternos);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterFatProcedHospInternosRNExceptionCode.FAT_00878);
		}
	}
	@Test
	public void testVerificarPreenchimentoExcecao00879() {

		fatProcedHospInternos.setTipoOperConversao(null);
		fatProcedHospInternos.setFatorConversao(BigDecimal.ONE);

		try {
			systemUnderTest.verificarPreenchimento(fatProcedHospInternos);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterFatProcedHospInternosRNExceptionCode.FAT_00879);
		}
	}
	@Test
	public void testVerificarPreenchimentoFail() {

		fatProcedHospInternos.setTipoOperConversao(DominioTipoOperacaoConversao.D);
		fatProcedHospInternos.setFatorConversao(BigDecimal.ONE);

		try {
			systemUnderTest.verificarPreenchimento(fatProcedHospInternos);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}

		//Passa no teste caso nao tenha ido lançada uma exceção.
		assertEquals(1, 1);
	}

	@Test
	public void testVerificarModificadosException() {
		Calendar hoje = Calendar.getInstance();
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_MONTH, -1);
		fatProcedHospInternos.setCriadoEm(ontem.getTime());
		fatProcedHospInternosOriginal.setCriadoEm(hoje.getTime());

		try {
			systemUnderTest.verificarModificados(fatProcedHospInternos, fatProcedHospInternosOriginal);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterFatProcedHospInternosRNExceptionCode.FAT_00149);
		}
	}
	
	@Test
	public void testVerificarModificadosException02() {
		fatProcedHospInternos.setServidor(new RapServidores(new RapServidoresId(Integer.valueOf((int)2), Short.valueOf((short)3))));

		try {
			systemUnderTest.verificarModificados(fatProcedHospInternos, fatProcedHospInternosOriginal);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterFatProcedHospInternosRNExceptionCode.FAT_00149);
		}
	}
	
	@Test
	public void testVerificarModificadosException03() {
		fatProcedHospInternos.setServidor(new RapServidores(new RapServidoresId(Integer.valueOf((int)1), Short.valueOf((short)3))));
		fatProcedHospInternosOriginal.setServidor(new RapServidores(new RapServidoresId(Integer.valueOf((int)2), Short.valueOf((short)3))));
		try {
			systemUnderTest.verificarModificados(fatProcedHospInternos, fatProcedHospInternosOriginal);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterFatProcedHospInternosRNExceptionCode.FAT_00149);
		}
	}
	
	@Test
	public void testVerificarModificadosException04() {
		fatProcedHospInternos.setSeq(Integer.valueOf(1));
		fatProcedHospInternosOriginal.setSeq(Integer.valueOf(2));

		try {
			systemUnderTest.verificarModificados(fatProcedHospInternos, fatProcedHospInternosOriginal);
			fail("Exeção não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), ManterFatProcedHospInternosRNExceptionCode.FAT_00149);
		}
	}
	
	@Test
	public void testVerificarModificadosSuccess() {
		try {
			systemUnderTest.verificarModificados(fatProcedHospInternos, fatProcedHospInternosOriginal);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}

		//Passa no teste caso nao tenha ido lançada uma exceção.
		assertEquals(1, 1);
	}
	
	@Test
	public void testVerificarModificadosSuccess02() {
		fatProcedHospInternos.setCsaCodigo("Codigo");
		fatProcedHospInternos.setSeq(Integer.valueOf(1));
		fatProcedHospInternosOriginal.setSeq(Integer.valueOf(2));
		
		try {
			systemUnderTest.verificarModificados(fatProcedHospInternos, fatProcedHospInternosOriginal);
		} catch (ApplicationBusinessException e) {
			fail("Exeção gerada. " + e.getCode());
		}

		//Passa no teste caso nao tenha ido lançada uma exceção.
		assertEquals(1, 1);
	}

	@Test
	public void testInserirJNFalse() {
		assertFalse(systemUnderTest.inserirJN(fatProcedHospInternos, fatProcedHospInternosOriginal));
	}
	
	@Test
	public void testInserirJNTrue() {
		fatProcedHospInternos.setSeq(Integer.valueOf(1));
		fatProcedHospInternosOriginal.setSeq(Integer.valueOf(2));
		
		assertTrue(systemUnderTest.inserirJN(fatProcedHospInternos, fatProcedHospInternosOriginal));
	}
	
	@Test
	public void testInserirJNTrue02() {
		fatProcedHospInternos.setSituacao(null);
		fatProcedHospInternosOriginal.setSituacao(DominioSituacao.I);
		
		assertTrue(systemUnderTest.inserirJN(fatProcedHospInternos, fatProcedHospInternosOriginal));
	}
	
	@Test
	public void testInserirJNTrue03() {
		fatProcedHospInternos.setSituacao(DominioSituacao.A);
		fatProcedHospInternosOriginal.setSituacao(DominioSituacao.I);
		
		assertTrue(systemUnderTest.inserirJN(fatProcedHospInternos, fatProcedHospInternosOriginal));
	}
	
	@Test
	public void testInserirJNTrue04() {
		fatProcedHospInternos.setProcedimentoHospitalarInterno(null);
		FatProcedHospInternos fatProcedHospInternos = new FatProcedHospInternos();
		fatProcedHospInternos.setSeq(Integer.valueOf(1));
		fatProcedHospInternosOriginal.setProcedimentoHospitalarInterno(fatProcedHospInternos);
		
		assertTrue(systemUnderTest.inserirJN(fatProcedHospInternos, fatProcedHospInternosOriginal));
	}
	
	@Test
	public void testInserirJNTrue05() {
		FatProcedHospInternos fatProcedHospInternos02 = new FatProcedHospInternos();
		fatProcedHospInternos02.setSeq(Integer.valueOf(2));
		fatProcedHospInternos.setProcedimentoHospitalarInterno(fatProcedHospInternos02);
		FatProcedHospInternos fatProcedHospInternos = new FatProcedHospInternos();
		fatProcedHospInternos.setSeq(Integer.valueOf(1));
		fatProcedHospInternosOriginal.setProcedimentoHospitalarInterno(fatProcedHospInternos);
		
		assertTrue(systemUnderTest.inserirJN(fatProcedHospInternos, fatProcedHospInternosOriginal));
	}
}
