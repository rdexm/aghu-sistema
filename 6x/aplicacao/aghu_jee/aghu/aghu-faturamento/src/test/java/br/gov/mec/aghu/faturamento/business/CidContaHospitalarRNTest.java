package br.gov.mec.aghu.faturamento.business;

import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.core.exception.BaseException;

public class CidContaHospitalarRNTest {
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	final Log log = LogFactory.getLog(this.getClass());
	Date data = new Date();

	
	private CidContaHospitalarRN getObjRn() {		
		return new CidContaHospitalarRN();
	}

	@Test
	public void testPosAtualizacao() throws BaseException {
		CidContaHospitalarRN objRn = this.getObjRn();		
		FatCidContaHospitalar entidade = new FatCidContaHospitalar();
		boolean result = false;
		try {
			result = objRn.posAtualizacao(entidade, entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}		
	}

	@Test
	public void testPosInsercao() throws BaseException {
		CidContaHospitalarRN objRn = this.getObjRn();		
		FatCidContaHospitalar entidade = new FatCidContaHospitalar();
		boolean result = false;
		try {
			result = objRn.posInsercao(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}

	@Test
	public void testPosRemocao() throws BaseException {
		CidContaHospitalarRN objRn = this.getObjRn();		
		FatCidContaHospitalar entidade = new FatCidContaHospitalar();
		boolean result = false;
		try {
			result = objRn.posRemocao(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}

	@Test
	public void testPreAtualizacao() throws BaseException {
		CidContaHospitalarRN objRn = this.getObjRn();		
		FatCidContaHospitalar entidade = new FatCidContaHospitalar();
		boolean result = false;
		try {
			result = objRn.preAtualizacao(entidade, entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}

	@Test
	public void testPreInsercao() throws BaseException {
		CidContaHospitalarRN objRn = this.getObjRn();		
		FatCidContaHospitalar entidade = new FatCidContaHospitalar();
		boolean result = false;
		try {
			result = objRn.preInsercao(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}

	@Test
	public void testPreRemocao()throws BaseException {
		CidContaHospitalarRN objRn = this.getObjRn();		
		FatCidContaHospitalar entidade = new FatCidContaHospitalar();
		boolean result = false;
		try {
			result = objRn.preRemocao(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertTrue(result);
		} catch (BaseException e) {
			this.log.info(e + "\n" + Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}
}
