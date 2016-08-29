package br.gov.mec.aghu.blococirurgico.cedenciasala.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.cedenciasala.business.CedenciaSalasEntreEquipeON.CedenciaSalasEntreEquipeONExceptionCode;
import br.gov.mec.aghu.blococirurgico.cedenciasala.vo.CedenciaSalasEntreEquipesEquipeVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.util.AghuEnumUtils;

public class CedenciaSalasEntreEquipeONTest extends AGHUBaseUnitTest<CedenciaSalasEntreEquipeON>{

//	@Test
	public void testValidarDatasMenorIgualAtual(){
		
		CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO = new CedenciaSalasEntreEquipesEquipeVO();
		MbcSubstEscalaSala mbcSubstEscalaSala = new MbcSubstEscalaSala();
		mbcSubstEscalaSala.setMbcCaractSalaEsp(new MbcCaractSalaEsp());
		mbcSubstEscalaSala.getMbcCaractSalaEsp().setMbcCaracteristicaSalaCirg(new MbcCaracteristicaSalaCirg());
		cedenciaSalasEntreEquipesEquipeVO.setData(DateUtil.adicionaDias(new Date(), -1));
		cedenciaSalasEntreEquipesEquipeVO.setRecorrencia(Boolean.FALSE);
		mbcSubstEscalaSala.getMbcCaractSalaEsp().getMbcCaracteristicaSalaCirg().setDiaSemana( AghuEnumUtils.retornaDiaSemanaAghu(DateUtil.adicionaDias(new Date(), -1)));
		
		try {
			systemUnderTest.validarDatas(cedenciaSalasEntreEquipesEquipeVO, mbcSubstEscalaSala.getMbcCaractSalaEsp());
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((CedenciaSalasEntreEquipeONExceptionCode.MBC_01111)));
		}
	}
	
	@Test
	public void testValidarRecorrenciaDataMaior(){
		
		CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO = new CedenciaSalasEntreEquipesEquipeVO();
		MbcSubstEscalaSala mbcSubstEscalaSala = new MbcSubstEscalaSala();
		mbcSubstEscalaSala.setMbcCaractSalaEsp(new MbcCaractSalaEsp());
		mbcSubstEscalaSala.getMbcCaractSalaEsp().setMbcCaracteristicaSalaCirg(new MbcCaracteristicaSalaCirg());
		cedenciaSalasEntreEquipesEquipeVO.setData(DateUtil.adicionaDias(new Date(), 1));
		cedenciaSalasEntreEquipesEquipeVO.setDataFim(DateUtil.adicionaDias(new Date(), -1));
		cedenciaSalasEntreEquipesEquipeVO.setRecorrencia(Boolean.TRUE);
		mbcSubstEscalaSala.getMbcCaractSalaEsp().getMbcCaracteristicaSalaCirg().setDiaSemana( AghuEnumUtils.retornaDiaSemanaAghu(DateUtil.adicionaDias(new Date(), -1)));
		
		
		try {
			systemUnderTest.validarDatas(cedenciaSalasEntreEquipesEquipeVO, mbcSubstEscalaSala.getMbcCaractSalaEsp());
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((CedenciaSalasEntreEquipeONExceptionCode.MBC_01112)));
		}
	}
	
	@Test
	public void testValidarIntervaloMenor(){
		
		CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO = new CedenciaSalasEntreEquipesEquipeVO();
		MbcSubstEscalaSala mbcSubstEscalaSala = new MbcSubstEscalaSala();
		mbcSubstEscalaSala.setMbcCaractSalaEsp(new MbcCaractSalaEsp());
		mbcSubstEscalaSala.getMbcCaractSalaEsp().setMbcCaracteristicaSalaCirg(new MbcCaracteristicaSalaCirg());
		cedenciaSalasEntreEquipesEquipeVO.setData(DateUtil.adicionaDias(new Date(), 1));
		cedenciaSalasEntreEquipesEquipeVO.setDataFim(DateUtil.adicionaDias(new Date(), 2));
		cedenciaSalasEntreEquipesEquipeVO.setRecorrencia(Boolean.TRUE);
		mbcSubstEscalaSala.getMbcCaractSalaEsp().getMbcCaracteristicaSalaCirg().setDiaSemana( AghuEnumUtils.retornaDiaSemanaAghu(DateUtil.adicionaDias(new Date(), 1)));
		cedenciaSalasEntreEquipesEquipeVO.setIntervalo(0);
		
		try {
			systemUnderTest.validarDatas(cedenciaSalasEntreEquipesEquipeVO, mbcSubstEscalaSala.getMbcCaractSalaEsp());
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((CedenciaSalasEntreEquipeONExceptionCode.MBC_01113)));
		}
	}
	
	@Test
	public void testValidarIntervaloMaior(){
		
		CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO = new CedenciaSalasEntreEquipesEquipeVO();
		MbcSubstEscalaSala mbcSubstEscalaSala = new MbcSubstEscalaSala();
		mbcSubstEscalaSala.setMbcCaractSalaEsp(new MbcCaractSalaEsp());
		mbcSubstEscalaSala.getMbcCaractSalaEsp().setMbcCaracteristicaSalaCirg(new MbcCaracteristicaSalaCirg());
		cedenciaSalasEntreEquipesEquipeVO.setData(DateUtil.adicionaDias(new Date(), 1));
		cedenciaSalasEntreEquipesEquipeVO.setDataFim(DateUtil.adicionaDias(new Date(), 2));
		cedenciaSalasEntreEquipesEquipeVO.setRecorrencia(Boolean.TRUE);
		mbcSubstEscalaSala.getMbcCaractSalaEsp().getMbcCaracteristicaSalaCirg().setDiaSemana( AghuEnumUtils.retornaDiaSemanaAghu(DateUtil.adicionaDias(new Date(), 1)));
		cedenciaSalasEntreEquipesEquipeVO.setIntervalo(53);
		
		try {
			systemUnderTest.validarDatas(cedenciaSalasEntreEquipesEquipeVO, mbcSubstEscalaSala.getMbcCaractSalaEsp());
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((CedenciaSalasEntreEquipeONExceptionCode.MBC_01113)));
		}
	}
	
//	@Test
	public void testValidarDiaSemana(){
		
		CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO = new CedenciaSalasEntreEquipesEquipeVO();
		MbcSubstEscalaSala mbcSubstEscalaSala = new MbcSubstEscalaSala();
		mbcSubstEscalaSala.setMbcCaractSalaEsp(new MbcCaractSalaEsp());
		mbcSubstEscalaSala.getMbcCaractSalaEsp().setMbcCaracteristicaSalaCirg(new MbcCaracteristicaSalaCirg());
		cedenciaSalasEntreEquipesEquipeVO.setData(DateUtil.adicionaDias(new Date(), 1));
		cedenciaSalasEntreEquipesEquipeVO.setDataFim(DateUtil.adicionaDias(new Date(), 2));
		cedenciaSalasEntreEquipesEquipeVO.setRecorrencia(Boolean.TRUE);
		mbcSubstEscalaSala.getMbcCaractSalaEsp().getMbcCaracteristicaSalaCirg().setDiaSemana( AghuEnumUtils.retornaDiaSemanaAghu(new Date()));
		cedenciaSalasEntreEquipesEquipeVO.setIntervalo(1);
		
		try {
			systemUnderTest.validarDatas(cedenciaSalasEntreEquipesEquipeVO, mbcSubstEscalaSala.getMbcCaractSalaEsp());
			Assert.fail();
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals((CedenciaSalasEntreEquipeONExceptionCode.MBC_01117)));
		}
	}
}
