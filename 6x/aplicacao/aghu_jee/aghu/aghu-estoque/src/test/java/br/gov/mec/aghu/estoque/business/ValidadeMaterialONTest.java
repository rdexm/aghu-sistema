package br.gov.mec.aghu.estoque.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.estoque.business.ValidadeMaterialON.ValidadeMaterialONExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteFornecedorDAO;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.model.SceValidadeId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ValidadeMaterialONTest extends AGHUBaseUnitTest<ValidadeMaterialON>{
	
	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	@Mock
	private SceLoteFornecedorDAO mockedSceLoteFornecedorDAO;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";
	
	
	/**
	 * Obtém instância padrão para os testes
	 * @return
	 */
	private SceValidade getDefaultInstance(){
		final SceValidade validade = new SceValidade();
		final SceValidadeId id = new SceValidadeId();
		id.setData(new Date(10000));
		id.setDataLong(10000L);
		id.setEalSeq(1);
		validade.setId(id);
		return validade;
	}
	
	@Test
	public void testValidaQuantidadesError01(){
		
		try {
			
			SceMovimentoMaterial movimentoMaterial = new SceMovimentoMaterial();
			movimentoMaterial.setQuantidade(0);
			this.systemUnderTest.validaQuantidades(movimentoMaterial, 1);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + ValidadeMaterialONExceptionCode.SCE_00697);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), ValidadeMaterialONExceptionCode.SCE_00697, e.getCode());
		}

	}
	
	@Test
	public void testValidarValidadeUnicalError01(){
		
		try {
			
			SceValidade validade = this.getDefaultInstance();
			SceMovimentoMaterial movimentoMaterial = new SceMovimentoMaterial();
			SceTipoMovimento tipoMovimento = new SceTipoMovimento();
			tipoMovimento.setIndOperacaoBasica(DominioIndOperacaoBasica.N);
			movimentoMaterial.setTipoMovimento(tipoMovimento);

			this.systemUnderTest.validaTipoOperacaoBasica(validade, movimentoMaterial);
			
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + ValidadeMaterialONExceptionCode.SCE_00701);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), ValidadeMaterialONExceptionCode.SCE_00701, e.getCode());
		}

	}
	
	@Test
	public void testValidarValidadeUnicalError02(){
		
		try {
			
			SceValidade validade = this.getDefaultInstance();
			
			validade.setQtdeDisponivel(1);
			
			SceMovimentoMaterial movimentoMaterial = new SceMovimentoMaterial();
			SceTipoMovimento tipoMovimento = new SceTipoMovimento();
			tipoMovimento.setIndOperacaoBasica(DominioIndOperacaoBasica.CR);
			movimentoMaterial.setTipoMovimento(tipoMovimento);

			this.systemUnderTest.validaTipoOperacaoBasica(validade, movimentoMaterial);
			
			if(validade.getQtdeEntrada().equals(validade.getQtdeDisponivel()) && validade.getQtdeConsumida().equals(0)){
				Assert.assertFalse(false);
			} else {
				Assert.assertFalse(true);
			}
			
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}

	}

}
