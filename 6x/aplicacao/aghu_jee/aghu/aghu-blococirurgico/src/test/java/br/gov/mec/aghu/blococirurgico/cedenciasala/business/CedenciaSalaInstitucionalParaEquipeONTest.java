package br.gov.mec.aghu.blococirurgico.cedenciasala.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.cedenciasala.business.CedenciaSalaInstitucionalParaEquipeON.CedenciaDeSalaInstitucionalParaEquipeONExceptionCode;
import br.gov.mec.aghu.blococirurgico.cedenciasala.business.CedenciaSalasEntreEquipeON.CedenciaSalasEntreEquipeONExceptionCode;
import br.gov.mec.aghu.blococirurgico.cedenciasala.vo.CedenciaSalaInstitucionalParaEquipeVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.util.AghuEnumUtils;

public class CedenciaSalaInstitucionalParaEquipeONTest extends AGHUBaseUnitTest<CedenciaSalaInstitucionalParaEquipeON> {

	@Test
	public void testValidarDatasMenorIgualAtual(){
		
		CedenciaSalaInstitucionalParaEquipeVO cedenciaSala = new CedenciaSalaInstitucionalParaEquipeVO();
		cedenciaSala.setData(DateUtil.adicionaDias(new Date(), -1));
		cedenciaSala.setRecorrencia(Boolean.FALSE);
		cedenciaSala.setDiaSemana( AghuEnumUtils.retornaDiaSemanaAghu(DateUtil.adicionaDias(new Date(), -1)));
		
		try {
			systemUnderTest.validarDatas(cedenciaSala, null);
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((CedenciaSalasEntreEquipeONExceptionCode.DATA_INFERIOR_ATUAL)));
		}
	}
	
	@Test
	public void testValidarRecorrenciaDataMaior(){
		
		CedenciaSalaInstitucionalParaEquipeVO cedenciaSala = new CedenciaSalaInstitucionalParaEquipeVO();
		cedenciaSala.setData(DateUtil.adicionaDias(new Date(), 1));
		cedenciaSala.setDataFim(DateUtil.adicionaDias(new Date(), -1));
		cedenciaSala.setRecorrencia(Boolean.TRUE);
		cedenciaSala.setDiaSemana( AghuEnumUtils.retornaDiaSemanaAghu(DateUtil.adicionaDias(new Date(), 1)));
		
		try {
			systemUnderTest.validarDatas(cedenciaSala, null);
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((CedenciaDeSalaInstitucionalParaEquipeONExceptionCode.MBC_01112)));
		}
	}
	
	@Test
	public void testValidarIntervaloMenor(){
		
		CedenciaSalaInstitucionalParaEquipeVO cedenciaSala = new CedenciaSalaInstitucionalParaEquipeVO();
		cedenciaSala.setData(DateUtil.adicionaDias(new Date(), 1));
		cedenciaSala.setDataFim(DateUtil.adicionaDias(new Date(), 2));
		cedenciaSala.setRecorrencia(Boolean.TRUE);
		cedenciaSala.setDiaSemana( AghuEnumUtils.retornaDiaSemanaAghu(DateUtil.adicionaDias(new Date(), 1)));
		cedenciaSala.setIntervalo(0);
		
		try {
			systemUnderTest.validarDatas(cedenciaSala, null);
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((CedenciaDeSalaInstitucionalParaEquipeONExceptionCode.MBC_01113)));
		}
	}
	
	@Test
	public void testValidarIntervaloMaior(){
		
		CedenciaSalaInstitucionalParaEquipeVO cedenciaSala = new CedenciaSalaInstitucionalParaEquipeVO();
		cedenciaSala.setData(DateUtil.adicionaDias(new Date(), 1));
		cedenciaSala.setDataFim(DateUtil.adicionaDias(new Date(), 2));
		cedenciaSala.setRecorrencia(Boolean.TRUE);
		cedenciaSala.setDiaSemana( AghuEnumUtils.retornaDiaSemanaAghu(DateUtil.adicionaDias(new Date(), 1)));
		cedenciaSala.setIntervalo(53);
		
		try {
			systemUnderTest.validarDatas(cedenciaSala, null);
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((CedenciaDeSalaInstitucionalParaEquipeONExceptionCode.MBC_01113)));
		}
	}
	
	@Test
	public void testValidarDiaSemana(){
		
		CedenciaSalaInstitucionalParaEquipeVO cedenciaSala = new CedenciaSalaInstitucionalParaEquipeVO();
		cedenciaSala.setData(DateUtil.adicionaDias(new Date(), 1));
		cedenciaSala.setDataFim(DateUtil.adicionaDias(new Date(), 2));
		cedenciaSala.setRecorrencia(Boolean.TRUE);
		cedenciaSala.setDiaSemana(AghuEnumUtils.retornaDiaSemanaAghu(new Date()));
		cedenciaSala.setIntervalo(1);
		
		try {
			systemUnderTest.validarDatas(cedenciaSala, null);
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((CedenciaDeSalaInstitucionalParaEquipeONExceptionCode.MBC_01117)));
		}
	}
}
