package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.cadastrosapoio.business.GrupoMaterialAnaliseRN.GrupoMaterialRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelGrupoMaterialAnaliseDAO;
import br.gov.mec.aghu.model.AelGrupoMaterialAnalise;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GrupoMaterialAnaliseRNTest extends AGHUBaseUnitTest<GrupoMaterialAnaliseRN>{
	
	@Mock
	private AelGrupoMaterialAnaliseDAO mockedAelGrupoMaterialAnaliseDAO;
	
	@Test
	public void testRemoverSuccess() {
		final Integer seqExclusao = 12;
		final AelGrupoMaterialAnalise grupoMaterialAnalise = new AelGrupoMaterialAnalise();
		grupoMaterialAnalise.setSeq(seqExclusao);
		
		try {
			Mockito.when(mockedAelGrupoMaterialAnaliseDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(grupoMaterialAnalise);

			systemUnderTest.remover(seqExclusao);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testRemoverError() {
		final Integer seqExclusao = 12;
		
		try {
			Mockito.when(mockedAelGrupoMaterialAnaliseDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(null);
			
			systemUnderTest.remover(seqExclusao);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoMaterialRNExceptionCode.MENSAGEM_ERRO_EXCLUIR_GRUPO_MATERIAL_ANALISE);
		}
	}
	
}
