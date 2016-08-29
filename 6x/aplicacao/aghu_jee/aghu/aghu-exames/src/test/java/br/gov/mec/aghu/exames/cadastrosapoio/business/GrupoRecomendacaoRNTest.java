package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.gov.mec.aghu.model.AelGrupoRecomendacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GrupoRecomendacaoRNTest extends AGHUBaseUnitTest<GrupoRecomendacaoRN>{
	
	
	/**
	 * Passa objetos nulos, ou seja, com nenhuma alteração.
	 * 
	 * Logo deve retornar falso.
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void hasModificacaoAtributosNulosTest() throws ApplicationBusinessException {
		
		AelGrupoRecomendacao grupoRecomendacao = new AelGrupoRecomendacao();
		
		AelGrupoRecomendacao grupoRecomendacaoOriginal = new AelGrupoRecomendacao();
		
    	boolean atual = systemUnderTest.hasModificacao(grupoRecomendacao, grupoRecomendacaoOriginal);
    	
    	assertFalse(atual);
		
	}
	
	/**
	 * Passa objetos com o seq diferente.
	 * 
	 * Logo deve retornar verdadeiro.
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void hasModificacaoSeqDiferenteTest() throws ApplicationBusinessException {
		
		AelGrupoRecomendacao grupoRecomendacao = new AelGrupoRecomendacao();
		grupoRecomendacao.setSeq(1);
		
		AelGrupoRecomendacao grupoRecomendacaoOriginal = new AelGrupoRecomendacao();
		grupoRecomendacaoOriginal.setSeq(2);
		
    	boolean atual = systemUnderTest.hasModificacao(grupoRecomendacao, grupoRecomendacaoOriginal);
    	
    	assertTrue(atual);
		
	}
	
	/**
	 * Passa objetos com a descricao diferente.
	 * 
	 * Logo deve retornar verdadeiro.
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void hasModificacaoDescricaoDiferenteTest() throws ApplicationBusinessException {
		
		AelGrupoRecomendacao grupoRecomendacao = new AelGrupoRecomendacao();
		grupoRecomendacao.setDescricao("1");
		
		AelGrupoRecomendacao grupoRecomendacaoOriginal = new AelGrupoRecomendacao();
		grupoRecomendacaoOriginal.setDescricao("2");
		
    	boolean atual = systemUnderTest.hasModificacao(grupoRecomendacao, grupoRecomendacaoOriginal);
    	
    	assertTrue(atual);
		
	}

}
