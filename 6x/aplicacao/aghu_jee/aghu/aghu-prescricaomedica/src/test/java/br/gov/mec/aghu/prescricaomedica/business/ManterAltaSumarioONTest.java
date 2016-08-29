package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaSumarioONTest extends AGHUBaseUnitTest<ManterAltaSumarioON>{
	
	private static final Log log = LogFactory.getLog(ManterAltaSumarioONTest.class);


	@Test
	public void testCalcularIdadeZeroMeses() {
		
		final AltaSumarioVO altaSumarioVO = new AltaSumarioVO();
		Calendar dataNascimento = Calendar.getInstance();
		dataNascimento.set(Calendar.DAY_OF_MONTH, 27);
		dataNascimento.set(Calendar.MONTH, Calendar.JUNE);
		dataNascimento.set(Calendar.YEAR, 1985);
		altaSumarioVO.setDataNascimento(dataNascimento.getTime());
		
		Calendar dataAlta = Calendar.getInstance();
		dataAlta.set(Calendar.DAY_OF_MONTH, 28);
		dataAlta.set(Calendar.MONTH, Calendar.JUNE);
		dataAlta.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataAlta(dataAlta.getTime());
		
		try {
			
			systemUnderTest.calcularIdade(altaSumarioVO);
			assertEquals("26 anos ", altaSumarioVO.getIdadeFormatado());
			
		} catch (ApplicationBusinessException e) {
			
			log.error(e.getMessage());
			
		}
	}
	
	@Test
	public void testCalcularIdadeZeroMesesFail() {
		
		final AltaSumarioVO altaSumarioVO = new AltaSumarioVO();
		Calendar dataNascimento = Calendar.getInstance();
		dataNascimento.set(Calendar.DAY_OF_MONTH, 27);
		dataNascimento.set(Calendar.MONTH, Calendar.JUNE);
		dataNascimento.set(Calendar.YEAR, 1985);
		altaSumarioVO.setDataNascimento(dataNascimento.getTime());
		
		Calendar dataAlta = Calendar.getInstance();
		dataAlta.set(Calendar.DAY_OF_MONTH, 28);
		dataAlta.set(Calendar.MONTH, Calendar.JUNE);
		dataAlta.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataAlta(dataAlta.getTime());
		
		try {
			
			systemUnderTest.calcularIdade(altaSumarioVO);
			assertNotSame("26 anos 0 meses ", altaSumarioVO.getIdadeFormatado());
			
		} catch (ApplicationBusinessException e) {
			
			log.error(e.getMessage());
			
		}
	}
	
	@Test
	public void testCalcularIdadeMes() {
		
		final AltaSumarioVO altaSumarioVO = new AltaSumarioVO();
		Calendar dataNascimento = Calendar.getInstance();
		dataNascimento.set(Calendar.DAY_OF_MONTH, 9);
		dataNascimento.set(Calendar.MONTH, Calendar.JANUARY);
		dataNascimento.set(Calendar.YEAR, 1971);
		altaSumarioVO.setDataNascimento(dataNascimento.getTime());
		
		Calendar dataAlta = Calendar.getInstance();
		dataAlta.set(Calendar.DAY_OF_MONTH, 8);
		dataAlta.set(Calendar.MONTH, Calendar.FEBRUARY);
		dataAlta.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataAlta(dataAlta.getTime());
		
		try {
			
			systemUnderTest.calcularIdade(altaSumarioVO);
			assertEquals("40 anos ", altaSumarioVO.getIdadeFormatado());
			
		} catch (ApplicationBusinessException e) {
			
			log.error(e.getMessage());
			
		}
	}
	
	@Test
	public void testCalcularIdadeMeses() {
		
		final AltaSumarioVO altaSumarioVO = new AltaSumarioVO();
		Calendar dataNascimento = Calendar.getInstance();
		dataNascimento.set(Calendar.DAY_OF_MONTH, 27);
		dataNascimento.set(Calendar.MONTH, Calendar.JUNE);
		dataNascimento.set(Calendar.YEAR, 1985);
		altaSumarioVO.setDataNascimento(dataNascimento.getTime());
		
		Calendar dataAlta = Calendar.getInstance();
		dataAlta.set(Calendar.DAY_OF_MONTH, 28);
		dataAlta.set(Calendar.MONTH, Calendar.AUGUST);
		dataAlta.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataAlta(dataAlta.getTime());
		
		try {
			
			systemUnderTest.calcularIdade(altaSumarioVO);
			assertEquals("26 anos 2 meses ", altaSumarioVO.getIdadeFormatado());
			
		} catch (ApplicationBusinessException e) {
			
			log.error(e.getMessage());
			
		}
	}
	
	@Test
	public void testCalcularIdadeDia() {
		
		final AltaSumarioVO altaSumarioVO = new AltaSumarioVO();
		Calendar dataNascimento = Calendar.getInstance();
		dataNascimento.set(Calendar.DAY_OF_MONTH, 27);
		dataNascimento.set(Calendar.MONTH, Calendar.JUNE);
		dataNascimento.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataNascimento(dataNascimento.getTime());
		
		Calendar dataAlta = Calendar.getInstance();
		dataAlta.set(Calendar.DAY_OF_MONTH, 28);
		dataAlta.set(Calendar.MONTH, Calendar.AUGUST);
		dataAlta.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataAlta(dataAlta.getTime());
		
		try {
			
			systemUnderTest.calcularIdade(altaSumarioVO);
			assertEquals("2 meses 1 dia ", altaSumarioVO.getIdadeFormatado());
			
		} catch (ApplicationBusinessException e) {
			
			log.error(e.getMessage());
			
		}
	}
	
	@Test
	public void testCalcularIdadeDiaFail() {
		
		final AltaSumarioVO altaSumarioVO = new AltaSumarioVO();
		Calendar dataNascimento = Calendar.getInstance();
		dataNascimento.set(Calendar.DAY_OF_MONTH, 27);
		dataNascimento.set(Calendar.MONTH, Calendar.JUNE);
		dataNascimento.set(Calendar.YEAR, 2010);
		altaSumarioVO.setDataNascimento(dataNascimento.getTime());
		
		Calendar dataAlta = Calendar.getInstance();
		dataAlta.set(Calendar.DAY_OF_MONTH, 28);
		dataAlta.set(Calendar.MONTH, Calendar.SEPTEMBER);
		dataAlta.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataAlta(dataAlta.getTime());
		
		try {
			
			systemUnderTest.calcularIdade(altaSumarioVO);
			assertNotSame("1 ano 2 meses 1 dia ", altaSumarioVO.getIdadeFormatado());
			
		} catch (ApplicationBusinessException e) {
			
			log.error(e.getMessage());
			
		}
	}
	
	@Test
	public void testCalcularIdadeDias() {
		
		final AltaSumarioVO altaSumarioVO = new AltaSumarioVO();
		Calendar dataNascimento = Calendar.getInstance();
		dataNascimento.set(Calendar.DAY_OF_MONTH, 27);
		dataNascimento.set(Calendar.MONTH, Calendar.JUNE);
		dataNascimento.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataNascimento(dataNascimento.getTime());
		
		Calendar dataAlta = Calendar.getInstance();
		dataAlta.set(Calendar.DAY_OF_MONTH, 29);
		dataAlta.set(Calendar.MONTH, Calendar.AUGUST);
		dataAlta.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataAlta(dataAlta.getTime());
		
		try {
			
			systemUnderTest.calcularIdade(altaSumarioVO);
			assertEquals("2 meses 2 dias ", altaSumarioVO.getIdadeFormatado());
			
		} catch (ApplicationBusinessException e) {
			
			log.error(e.getMessage());
			
		}
	}
	
	@Test
	public void calcularIdadeTeste() {
		
		final AltaSumarioVO altaSumarioVO = new AltaSumarioVO();
		Calendar dataNascimento = Calendar.getInstance();
		dataNascimento.set(Calendar.DAY_OF_MONTH, 27);
		dataNascimento.set(Calendar.MONTH, Calendar.JUNE);
		dataNascimento.set(Calendar.YEAR, 1985);
		altaSumarioVO.setDataNascimento(dataNascimento.getTime());
		
		Calendar dataAlta = Calendar.getInstance();
		dataAlta.set(Calendar.DAY_OF_MONTH, 1);
		dataAlta.set(Calendar.MONTH, Calendar.FEBRUARY);
		dataAlta.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataAlta(dataAlta.getTime());
		
		try {
			
			systemUnderTest.calcularIdade(altaSumarioVO);
			assertEquals(Short.valueOf("25"), altaSumarioVO.getIdadeAnos());
			assertEquals(Integer.valueOf("7"), altaSumarioVO.getIdadeMeses());
			assertEquals(Integer.valueOf("4"), altaSumarioVO.getIdadeDias());
			
		} catch (ApplicationBusinessException e) {
			
			log.error(e.getMessage());
			
		}
		
	}
	
	
	
	@Test
	public void calcularDiasPermanenciaTest() {
		
		AltaSumarioVO altaSumarioVO = new AltaSumarioVO();
		Calendar dataAtendimento = Calendar.getInstance();
		dataAtendimento.set(Calendar.DAY_OF_MONTH, 27);
		dataAtendimento.set(Calendar.MONTH, Calendar.JUNE);
		dataAtendimento.set(Calendar.YEAR, 2010);
		altaSumarioVO.setDataAtendimento(dataAtendimento.getTime());
		
		Calendar dataAlta = Calendar.getInstance();
		dataAlta.set(Calendar.DAY_OF_MONTH, 1);
		dataAlta.set(Calendar.MONTH, Calendar.FEBRUARY);
		dataAlta.set(Calendar.YEAR, 2011);
		altaSumarioVO.setDataAlta(dataAlta.getTime());
			
		altaSumarioVO = systemUnderTest.calcularDiasPermanencia(altaSumarioVO);
		assertEquals(Short.valueOf("219"), altaSumarioVO.getDiasPermanencia());
			
	}

}
