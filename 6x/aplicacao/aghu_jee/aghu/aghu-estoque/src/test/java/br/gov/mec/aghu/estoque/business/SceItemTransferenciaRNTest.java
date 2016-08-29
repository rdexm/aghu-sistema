package br.gov.mec.aghu.estoque.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.estoque.business.SceItemTransferenciaRN.SceItemTransferenciaRNCode;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemTransferenciaDAO;
import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceItemTransferenciaRNTest extends AGHUBaseUnitTest<SceItemTransferenciaRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private SceTransferenciaDAO mockedSceTransferenciaDAO;
	@Mock
	private SceItemTransferenciaDAO mockedSceItemTransferenciaDAO;
	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";
	
	private SceTransferencia getSceTransferencia(){
		
		SceTransferencia transferencia = new SceTransferencia();
		
		SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
		almoxarifado.setSeq((short)1);
		transferencia.setAlmoxarifado(almoxarifado);
		
		SceAlmoxarifado almoxarifadoRecebimento = new SceAlmoxarifado();
		almoxarifadoRecebimento.setSeq((short)2);
		transferencia.setAlmoxarifadoRecebimento(almoxarifadoRecebimento);
		
		return transferencia;
		
	}
	
	
	
	private SceItemTransferencia getDefaultInstance(){
		
		SceItemTransferencia itemTransferencia = new SceItemTransferencia();
		itemTransferencia.setTransferencia(this.getSceTransferencia());

		return itemTransferencia;
		
	}
	
	
	@Test
	public void testValidarAlmoxarifadosPaiSuccess01() {
		
		final SceItemTransferencia itemTransferencia = this.getDefaultInstance();

		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.obterPorChavePrimaria(Mockito.any(Object.class))).thenReturn(null);
		
		try {			
			systemUnderTest.validarAlmoxarifadosPai(itemTransferencia);
			Assert.assertFalse(false);	
		} catch (BaseException e) {
			Assert.assertFalse(true);	
		}
		
	}
	
	@Test
	public void testValidarIndicadorBloqueioEntradaError01() {
		
		SceItemTransferencia itemTransferencia = this.getDefaultInstance();
	
		Mockito.when(mockedSceTransferenciaDAO.obterTransferenciaPorSeqAlmoxarifado(Mockito.anyInt(), Mockito.anyShort())).thenReturn(null);

		try {			
			systemUnderTest.validarIndicadorBloqueioEntrada(itemTransferencia);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceItemTransferenciaRNCode.SCE_00018);	
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceItemTransferenciaRNCode.SCE_00018, e.getCode());	
		}
		
	}
	
	@Test
	public void testValidarExclusaoError01() {
		
		SceItemTransferencia itemTransferencia = this.getDefaultInstance();
	
		Mockito.when(mockedSceTransferenciaDAO.obterTransferenciaPorSeqEfetivada(Mockito.anyInt())).thenReturn(new SceTransferencia());

		
		try {			
			systemUnderTest.validarExclusao(itemTransferencia.getTransferencia().getSeq());
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceItemTransferenciaRNCode.SCE_00490);	
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceItemTransferenciaRNCode.SCE_00490, e.getCode());	
		}
		
	}
	
	
	
	
}
