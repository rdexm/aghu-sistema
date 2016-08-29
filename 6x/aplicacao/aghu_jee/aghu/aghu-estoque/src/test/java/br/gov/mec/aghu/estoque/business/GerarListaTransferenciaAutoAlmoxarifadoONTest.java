package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.GerarListaTransferenciaAutoAlmoxarifadoON.GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoTransferenciaAutomaticaDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceAlmoxarifadoTransferenciaAutomatica;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GerarListaTransferenciaAutoAlmoxarifadoONTest extends AGHUBaseUnitTest<GerarListaTransferenciaAutoAlmoxarifadoON>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private SceTransferenciaRN mockedSceTransferenciaRN;
	@Mock
	private SceItemTransferenciaRN mockedSceItemTransferenciaRN;
	@Mock
	private SceAlmoxarifadoTransferenciaAutomaticaDAO mockedSceAlmoxarifadoTransferenciaAutomaticaDAO;
	@Mock
	private SceTransferenciaDAO mockedSceTransferenciaDAO;
	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	@Mock
	private IComprasFacade mockedComprasFacade;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";
	
	
	@Before
	public void doBeforeEachTestCase() {
		
	}
	
	private SceTransferencia getSceTransferenciaDefault(){
		
		SceTransferencia transferencia = new SceTransferencia();
		
		SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
		almoxarifado.setSeq((short)1);
		transferencia.setAlmoxarifado(almoxarifado);
		
		SceAlmoxarifado almoxarifadoRecebimento = new SceAlmoxarifado();
		almoxarifado.setSeq((short)2);
		transferencia.setAlmoxarifadoRecebimento(almoxarifadoRecebimento);
		
		return transferencia;
		
	}

	@Test
	public void testValidarAlmoxarifadosError01() {
		
		SceTransferencia transferencia = this.getSceTransferenciaDefault();
		
		// Teste aqui para almoxarifados iguais
		transferencia.getAlmoxarifado().setSeq((short)1);
		transferencia.getAlmoxarifadoRecebimento().setSeq((short)1);
		
		try {			
			systemUnderTest.validarAlmoxarifados(transferencia);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.MENSAGEM_ALMOXARIFADOS_IGUAIS);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.MENSAGEM_ALMOXARIFADOS_IGUAIS, e.getCode());
		}
		
	}
	
	@Test
	public void testValidarAlmoxarifadosError02() {
		
		SceTransferencia transferencia = this.getSceTransferenciaDefault();

		Mockito.when(mockedSceAlmoxarifadoTransferenciaAutomaticaDAO.obterAlmoxarifadoTransferenciaAutomaticaPorAlmoxarifadoOrigemDestino(Mockito.anyShort(), Mockito.anyShort()))
		.thenReturn(null);
		
		try {
			systemUnderTest.validarAlmoxarifados(transferencia);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00670);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00670, e.getCode());
		}
		
	}
	
	
	@Test
	public void testValidarAlmoxarifadosError03() {
		
		SceTransferencia transferencia = this.getSceTransferenciaDefault();

		// Teste aqui!
		final SceAlmoxarifadoTransferenciaAutomatica  almoxarifadoTransferenciaAutomatica = new SceAlmoxarifadoTransferenciaAutomatica();
		almoxarifadoTransferenciaAutomatica.setSituacao(DominioSituacao.I);
		
		Mockito.when(mockedSceAlmoxarifadoTransferenciaAutomaticaDAO.obterAlmoxarifadoTransferenciaAutomaticaPorAlmoxarifadoOrigemDestino(Mockito.anyShort(), Mockito.anyShort()))
		.thenReturn(almoxarifadoTransferenciaAutomatica);
		
		try {
			systemUnderTest.validarAlmoxarifados(transferencia);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00766);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00766, e.getCode());
		}
		
	}
	
	@Test
	public void testValidarNumeroCn5Success01(){
		
		SceTransferencia transferencia = this.getSceTransferenciaDefault();
		
		ScoClassifMatNiv5 classifMatNiv5 = new ScoClassifMatNiv5();
		classifMatNiv5.setNumero(Long.valueOf(1));
		transferencia.setClassifMatNiv5(classifMatNiv5);
		
		try {
			systemUnderTest.validarNumeroCn5(transferencia);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00766, e.getCode());
		}
		
		final Long cn5Modificado = transferencia.getClassifMatNiv5().getNumero();
		
		if(cn5Modificado != Long.parseLong("10000000000")){
			Assert.fail("Valor da CN5 inválido");
		}
		
	}
	
	@Test
	public void testValidarNiveisClassificacaoInformadosError01(){
		
		SceTransferencia transferencia = this.getSceTransferenciaDefault();
		
		final ScoClassifMatNiv5 classifMatNiv5 = new ScoClassifMatNiv5();
		classifMatNiv5.setNumero(Long.valueOf(1));
		classifMatNiv5.setCodigo(0);
		transferencia.setClassifMatNiv5(classifMatNiv5);
		
		final AghParametros pNumeroFornecedorHu = new AghParametros();
		pNumeroFornecedorHu.setVlrNumerico(BigDecimal.valueOf(1));
		
		try {
		
			Mockito.when(mockedComprasFacade.obterClassifMatNiv5PorNumero(Mockito.anyLong()))
			.thenReturn(classifMatNiv5);

			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
			.thenReturn(pNumeroFornecedorHu);
			
			Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.pesquisarMaterialSaldoMenorIgualEstoqueMinimoAlmoxarifadoDestino(Mockito.anyInt(), Mockito.anyShort(), 
					Mockito.anyShort(), Mockito.anyLong(), Mockito.anyLong()))
			.thenReturn(null);

			systemUnderTest.validarNiveisClassificacaoInformados(transferencia);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00671);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00671, e.getCode());
		}
		
	}
	
	
}
