package br.gov.mec.aghu.prescricaomedica.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmAltaPedidoExame;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPedidoExameDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaPedidoExameONTest extends AGHUBaseUnitTest<ManterAltaPedidoExameON>{
	
	private static final Log log = LogFactory.getLog(ManterAltaPedidoExameONTest.class);

	@Mock
	private MpmAltaPedidoExameDAO mockedMpmAltaPedidoExameDAO;
	@Mock
	private ManterAltaPedidoExameRN mockedManterAltaPedidoExameRN;

	@Before
	public void doBeforeEachTestCase() {
	
		try {
			Mockito.when(mockedMpmAltaPedidoExameDAO.obterMpmAltaPedidoExame(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(new MpmAltaPedidoExame());
			
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	@Test
	public void gravarAltaPedidoExameInserir() {
		MpmAltaPedidoExame altaPedidoExame = new MpmAltaPedidoExame();
		try {
			systemUnderTest.gravarAltaPedidoExame(altaPedidoExame);
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			Assert.fail(e.getMessage());
			log.error(e.getMessage());
		}
	}
	
	@Test
	public void gravarAltaPedidoExameAlterar() {
		MpmAltaPedidoExame altaPedidoExame = new MpmAltaPedidoExame();
		altaPedidoExame.setId(null);
		try {
			systemUnderTest.gravarAltaPedidoExame(altaPedidoExame);
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			Assert.fail(e.getMessage());
			log.error(e.getMessage());
		}
	}
	

	@Test
	public void excluirAltaPedidoExame() {
		MpmAltaPedidoExame altaPedidoExame = new MpmAltaPedidoExame();
		try {
			systemUnderTest.excluirAltaPedidoExame(altaPedidoExame);
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			Assert.fail(e.getMessage());
			log.error(e.getMessage());
		}
	}
	
	@Test
	public void excluirAltaPedidoExameNulo() {
		try {
			systemUnderTest.excluirAltaPedidoExame(null);
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			Assert.fail(e.getMessage());
			log.error(e.getMessage());
		}
	}
	
	@Test
	public void atualizarAltaPedidoExame() {
		try {
			MpmAltaSumario altaSumario = new MpmAltaSumario();
			MpmAltaSumarioId id = new MpmAltaSumarioId();
			id.setApaAtdSeq(1);
			id.setApaSeq(2);
			id.setSeqp((short)3);
			altaSumario.setId(id);
			systemUnderTest.versionarAltaPedidoExame(altaSumario, (short)1);
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			Assert.fail(e.getMessage());
			log.error(e.getMessage());
		}
	}
	
	@Test
	public void obterMpmAltaPedidoExame() {
		try {
			systemUnderTest.obterMpmAltaPedidoExame(1,2,(short)3); 
		} catch (ApplicationBusinessException e) {
			// TODO Auto-generated catch block
			Assert.fail(e.getMessage());
			log.error(e.getMessage());
		}
	}

}