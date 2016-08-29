package br.gov.mec.aghu.blococirurgico.cedenciasala.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.cedenciasala.business.BloqueioSalaRN.BloqueioSalaRNExceptionCode;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;

public class BloqueioSalaRNTest extends AGHUBaseUnitTest<BloqueioSalaRN> {

	@Test
	public void testDiaDaSemanaByDtInicio(){
		Date dtInicio = DateUtil.obterData(2013, 0, 1);//01/01/13 é uma terça		
		
		Date dtFim = DateUtil.obterData(2013, 0, 3);// 03/01/13 é uma quinta
		
		DominioDiaSemanaSigla diaSemana = DominioDiaSemanaSigla.QUI;
		
		try {
			systemUnderTest.rnBscpVerDiaSem(dtInicio, dtFim, diaSemana);
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((BloqueioSalaRNExceptionCode.MBC_01224)));			
		}		
	}	
	
	@Test
	public void testDiaDaSemanaByDtFim(){
		Date dtInicio = DateUtil.obterData(2013, 0, 1);//01/01/13 é uma terça		
		
		Date dtFim = DateUtil.obterData(2013, 0, 3);// 03/01/13 é uma quinta
		
		DominioDiaSemanaSigla diaSemana = DominioDiaSemanaSigla.TER;
		
		try {
			systemUnderTest.rnBscpVerDiaSem(dtInicio, dtFim, diaSemana);
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((BloqueioSalaRNExceptionCode.MBC_01225)));			
		}		
	}	
		
}
