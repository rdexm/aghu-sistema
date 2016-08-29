package br.gov.mec.aghu.estoque.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.estoque.business.SceTransferenciaRN.SceTransferenciaRNExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoTransferenciaAutomaticaDAO;
import br.gov.mec.aghu.estoque.dao.SceDocumentoValidadeDAO;
import br.gov.mec.aghu.estoque.dao.SceItemTransferenciaDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceTransferenciaRNTest  extends AGHUBaseUnitTest<SceTransferenciaRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private SceTransferenciaDAO mockedSceTransferenciaDAO;
	@Mock
	private SceTipoMovimentosDAO mockedSceTipoMovimentosDAO;
	@Mock
	private SceAlmoxarifadosRN mockedSceAlmoxarifadosRN;
	@Mock
	private SceAlmoxarifadoTransferenciaAutomaticaDAO mockedSceAlmoxarifadoTransferenciaAutomaticaDAO;
	@Mock
	private SceTipoMovimentosRN mockedSceTipoMovimentosRN;
	@Mock
	private SceMovimentoMaterialDAO mockedSceMovimentoMaterialDAO;
	@Mock
	private SceItemTransferenciaDAO mockedSceItemTransferenciaDAO;
	@Mock
	private SceMovimentoMaterialRN mockedSceMovimentoMaterialRN;
	@Mock
	private SceItemTransferenciaRN mockedSceItemTransferenciaRN;
	@Mock
	private SceDocumentoValidadeDAO mockedSceDocumentoValidadeDAO;
	@Mock
	private SceValidadesRN mockedSceValidadesRN;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";

	private SceTransferencia getDefaultInstance(){
		
		SceTransferencia transferencia = new SceTransferencia();
		
		SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
		almoxarifado.setSeq((short)1);
		transferencia.setAlmoxarifado(almoxarifado);
		
		SceAlmoxarifado almoxarifadoRecebimento = new SceAlmoxarifado();
		almoxarifadoRecebimento.setSeq((short)2);
		transferencia.setAlmoxarifadoRecebimento(almoxarifadoRecebimento);
		
		return transferencia;
		
	}

	@Test
	public void testValidarInclusaoError01() {
		
		SceTransferencia transferencia = this.getDefaultInstance();
		transferencia.setEfetivada(true); // Teste aqui!
		
		try {			
			systemUnderTest.validarInclusao(transferencia);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceTransferenciaRNExceptionCode.SCE_00480);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceTransferenciaRNExceptionCode.SCE_00480, e.getCode());
		}
		
	}
	
	@Test
	public void testValidarInclusaoError02() {
		
		SceTransferencia transferencia = this.getDefaultInstance();
		transferencia.setDtEfetivacao(new Date(0)); // Teste aqui!
		
		try {			
			systemUnderTest.validarInclusao(transferencia);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceTransferenciaRNExceptionCode.SCE_00481);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceTransferenciaRNExceptionCode.SCE_00481, e.getCode());
		}
		
	}
	
	@Test
	public void testValidarInclusaoError03() {
		
		SceTransferencia transferencia = this.getDefaultInstance();
		transferencia.setServidorEfetivado(new RapServidores()); // Teste aqui!
		
		try {			
			systemUnderTest.validarInclusao(transferencia);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceTransferenciaRNExceptionCode.SCE_00481);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceTransferenciaRNExceptionCode.SCE_00481, e.getCode());
		}
		
	}
	
	
	@Test
	public void testValidarRemoverTransferenciaEfetivadaError01() {
		
		SceTransferencia transferencia = this.getDefaultInstance();
		transferencia.setEfetivada(true); // Teste aqui!
		
		try {			
			systemUnderTest.validarRemoverTransferenciaEfetivada(transferencia);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceTransferenciaRNExceptionCode.SCE_00476);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceTransferenciaRNExceptionCode.SCE_00476, e.getCode());
		}
		
	}
	
	@Test
	public void atualizarTest001() {
		
		RapServidores servidor = new RapServidores(new RapServidoresId());
		
		SceTransferencia transferencia = this.getDefaultInstance();
		transferencia.setServidor(servidor);
		transferencia.setDtGeracao(new Date());
		
		
		SceTransferencia transferenciaOld = this.getDefaultInstance();
		transferenciaOld.setServidor(servidor);
		
		try {			
		
			systemUnderTest.validarAtualizacao(transferencia,transferenciaOld );
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceTransferenciaRNExceptionCode.SCE_00482);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceTransferenciaRNExceptionCode.SCE_00482, e.getCode());
		}
		
	}
	
//	@Test
	public void atualizarTest002() {
		
		RapServidores servidor = new RapServidores();
		
		SceTransferencia transferencia = this.getDefaultInstance();
		transferencia.setServidor(servidor);
		transferencia.setDtGeracao(new Date());
		transferencia.setTransferenciaAutomatica(Boolean.TRUE);
		SceTipoMovimento tipoMovimento = new SceTipoMovimento();
		transferencia.setTipoMovimento(tipoMovimento);
		transferencia.setEfetivada(Boolean.TRUE);
		
		SceTransferencia transferenciaOld = this.getDefaultInstance();
		transferenciaOld.setServidor(servidor);
		transferenciaOld.setDtGeracao(new Date());
		transferenciaOld.setEfetivada(Boolean.TRUE);
		
		try {			
		
			systemUnderTest.validarAtualizacao(transferencia,transferenciaOld );
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceTransferenciaRNExceptionCode.SCE_00408);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceTransferenciaRNExceptionCode.SCE_00408, e.getCode());
		}
		
	}
	
}
