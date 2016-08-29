package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class VerificacaoItemProcedimentoHospitalarRNTest extends AGHUBaseUnitTest<VerificacaoItemProcedimentoHospitalarRN>{

	private final Log log = LogFactory.getLog(this.getClass());

	@Mock
	private FatCompetenciaDAO mockedFatCompetenciaDAO;
	@Mock
	private FatItensProcedHospitalarDAO mockedFatItensProcedHospitalarDAO;
	
	@Test
	public void testVerificarOutrosRegistrosAtivosParaMesmoCodTabela() {
		boolean result = false;
		Short phoSeq = null;
		Integer seq = null;
		Long codTabela = null;

		phoSeq = Short.valueOf((short) 10);
		seq = Integer.valueOf(11);
		codTabela = Long.valueOf(12L);
		try {
			result = systemUnderTest.verificarOutrosRegistrosAtivosParaMesmoCodTabela(phoSeq, seq, codTabela);
			Assert.assertTrue(result);
		} catch (Exception e) {
			this.log.info(e);
		}
		try {
			result = systemUnderTest.verificarOutrosRegistrosAtivosParaMesmoCodTabela(phoSeq, seq, codTabela);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(FaturamentoExceptionCode.FAT_00131, e.getCode());
		}
	}

	@Test
	public void testVerificaExigenciaValoresConformeFlag() {

		boolean result = false;
		Short phoSeq = null;
		Integer seq = null;
		Date dataCompetencia = null;
		DominioModuloCompetencia modulo = null;
		boolean flag = false;
		FatCompetencia primeiro = null;
		FatCompetenciaId id = null;
		FaturamentoExceptionCode code = null;

		//setup: FAT_00564
		phoSeq = Short.valueOf((short) 10);
		seq = Integer.valueOf(11);
		modulo = DominioModuloCompetencia.AMB;
		flag = true;
		code = FaturamentoExceptionCode.FAT_00564;
		// assert: FAT_00564
		try {
			result = systemUnderTest.verificaExigenciaValoresConformeFlag(phoSeq, seq, dataCompetencia, modulo, flag, code);
			Assert.fail("Expecting exception");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(FaturamentoExceptionCode.FAT_00564, e.getCode());
		}
		//setup: FAT_00416
		id = new FatCompetenciaId(modulo, Integer.valueOf(1), Integer.valueOf(2000), dataCompetencia);
		primeiro = new FatCompetencia();
		primeiro.setId(id);
		//assert: FAT_00416
		try {
			result = systemUnderTest.verificaExigenciaValoresConformeFlag(phoSeq, seq, dataCompetencia, modulo, flag, code);
			Assert.fail("Expecting exception");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(code, e.getCode());
		}
		//setup: OK comp = null
		id = new FatCompetenciaId(modulo, Integer.valueOf(1), Integer.valueOf(2000), dataCompetencia);
		primeiro = new FatCompetencia();
		primeiro.setId(id);
		//assert: OK, comp = null
		try {
			result = systemUnderTest.verificaExigenciaValoresConformeFlag(phoSeq, seq, dataCompetencia, modulo, flag, code);
			Assert.assertTrue(result);
		} catch (Exception e) {
		}
		//setup: OK comp != null
		dataCompetencia = new Date();
		primeiro = null;
		//assert: OK, comp != null
		try {
			result = systemUnderTest.verificaExigenciaValoresConformeFlag(phoSeq, seq, dataCompetencia, modulo, flag, code);
			Assert.assertTrue(result);
		} catch (Exception e) {
			this.log.info(e);
		}
	}

		
}
