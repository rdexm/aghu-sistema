package br.gov.mec.aghu.estoque.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.SceLoteDocumentoRN.SceLoteDocumentoRNExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceLoteDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDocImpressaoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDocumentoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceValidadeDAO;
import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceLoteDocumentoRNTest extends AGHUBaseUnitTest<SceLoteDocumentoRN>{
	

	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private SceLoteDocumentoDAO mockedSceLoteDocumentoDAO;
	@Mock
	private SceLoteFornecedorDAO mockedSceLoteFornecedorDAO;
	@Mock
	private SceLoteFornecedorRN mockedSceLoteFornecedorRN;
	@Mock
	private SceLoteDocImpressaoDAO  mockedSceLoteDocImpressaoDAO;
	@Mock
	private IComprasFacade mockedComprasFacade;
	@Mock
	private SceLoteRN mockedSceLoteRN;
	@Mock
	private SceLoteDAO mockedSceLoteDAO;
	@Mock
	private SceValidadeDAO mockedSceValidadeDAO;
	@Mock
	private SceValidadesRN mockedSceValidadesRN;
	@Mock
	private SceItemNotaRecebimentoRN mockedSceItemNotaRecebimentoRN;


	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";
	
	
	/**
	 * Obtém instância padrão para os testes
	 * @return
	 */
	private SceLoteDocumento getDefaultInstance(){
		final SceLoteDocumento loteDocumento = new SceLoteDocumento();

		return loteDocumento;
	}
	
	@Test
	public void testValidaQuantidadeItemNotaFiscalError01() {

		try {

			SceLoteDocumento loteDocumento = this.getDefaultInstance();
			
			loteDocumento.setQuantidade(0); // Teste

			this.systemUnderTest.validaQuantidadeItemNotaFiscal(loteDocumento);

			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceLoteDocumentoRNExceptionCode.SCE_LDC_CK2);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceLoteDocumentoRNExceptionCode.SCE_LDC_CK2, e.getCode());
		}

	}
	
	@Test
	public void testValidaESLNotaRecebimentoError01() {

		try {

			SceLoteDocumento loteDocumento = this.getDefaultInstance();
			
			loteDocumento.setQuantidade(0); // Teste

			this.systemUnderTest.validaESLNotaRecebimento(loteDocumento);

			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceLoteDocumentoRNExceptionCode.MSG_ESL_NR_OBRIGATORIO);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceLoteDocumentoRNExceptionCode.MSG_ESL_NR_OBRIGATORIO, e.getCode());
		}

	}
	
	@Test
	public void testValidaESLNotaRecebimentoError02() {

		try {

			SceLoteDocumento loteDocumento = this.getDefaultInstance();
			
			loteDocumento.setEntradaSaidaSemLicitacao(new SceEntradaSaidaSemLicitacao()); 
			loteDocumento.setItemNotaRecebimento(new SceItemNotaRecebimento());
			loteDocumento.setIdaDalSeq(0);
			loteDocumento.setIrmRmsSeq(0);
			loteDocumento.setItrTrfSeq(0);
			
			loteDocumento.setQuantidade(0); 

			this.systemUnderTest.validaESLNotaRecebimento(loteDocumento);

			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceLoteDocumentoRNExceptionCode.MSG_ESL_OU_NR);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceLoteDocumentoRNExceptionCode.MSG_ESL_OU_NR, e.getCode());
		}

	}
	
	
	@Test
	public void testValidaEntradasSemLicitacaoError01() {

		try {

			SceLoteDocumento loteDocumento = this.getDefaultInstance();
			
			loteDocumento.setQuantidade(1); // Teste
			
			final SceEntradaSaidaSemLicitacao entradaSaidaSemLicitacao = new SceEntradaSaidaSemLicitacao();
			entradaSaidaSemLicitacao.setQuantidade(0); // Teste
			loteDocumento.setEntradaSaidaSemLicitacao(entradaSaidaSemLicitacao);			

			this.systemUnderTest.validaEntradasSemLicitacao(loteDocumento);

			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceLoteDocumentoRNExceptionCode.SCE_00669);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceLoteDocumentoRNExceptionCode.SCE_00669, e.getCode());
		}

	}
	
	
	@Test
	public void testValidaQuantidadesLoteDocumentoComItemNotaFiscalError01() {

		try {

			SceLoteDocumento loteDocumento = this.getDefaultInstance();
			
			loteDocumento.setQuantidade(1); // Teste
			
			final SceItemNotaRecebimento itemNotaRecebimento = new SceItemNotaRecebimento();
			itemNotaRecebimento.setQuantidade(0); // Teste
			loteDocumento.setItemNotaRecebimento(itemNotaRecebimento);			

			this.systemUnderTest.validaQuantidadesLoteDocumentoComItemNotaFiscal(loteDocumento);

			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceLoteDocumentoRNExceptionCode.SCE_00667);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceLoteDocumentoRNExceptionCode.SCE_00667, e.getCode());
		}

	}

	
}
