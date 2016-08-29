package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.cadastrosapoio.business.AelExameGrupoCaracteristicaRN.AelExameGrupoCaracteristicaRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelExameGrupoCaracteristicaDAO;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristicaId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelExameGrupoCaracteristicaRNTest extends AGHUBaseUnitTest<AelExameGrupoCaracteristicaRN>{
	@Mock
	private AelExameGrupoCaracteristicaDAO mockedAelExameGrupoCaracteristicaDAO;
	
	@Test
	public void executarRestricoesLancaExcecaoTest() {
		final AelExameGrupoCaracteristica elemento = new AelExameGrupoCaracteristica();
		final AelExameGrupoCaracteristica exameGrupoCaract = new AelExameGrupoCaracteristica();
		
		final AelExamesMaterialAnalise exaEma = new AelExamesMaterialAnalise();
		final AelResultadoCaracteristica resultCarac = new AelResultadoCaracteristica();
		final AelGrupoResultadoCaracteristica grupoResultCaract = new AelGrupoResultadoCaracteristica();
		
		Mockito.when(mockedAelExameGrupoCaracteristicaDAO.obterAelExameGrupoCaracteristica(Mockito.any(AelExameGrupoCaracteristica.class))).thenReturn(elemento);
		
		Mockito.when(mockedAelExameGrupoCaracteristicaDAO.obterOriginal(Mockito.any(AelExameGrupoCaracteristicaId.class))).thenReturn(exameGrupoCaract);
				
		try {
			
			//seto o exame material
			exaEma.setId(new AelExamesMaterialAnaliseId("MUS", 67));
			elemento.setExameMaterialAnalise(exaEma);
			
			//seto o resultado caract
			resultCarac.setSeq(0);
			elemento.setResultadoCaracteristica(resultCarac);
			
			//seto o grupo resultado caract
			grupoResultCaract.setSeq(0);
			elemento.setGrupoResultadoCaracteristica(grupoResultCaract);
			
			systemUnderTest.executarRestricoes(elemento);
			Assert.fail("Deveria lancar a excecao AEL_00046");
		} catch (BaseException e) {
			Assert.assertTrue(AelExameGrupoCaracteristicaRNExceptionCode.AEL_00046 == e.getCode());
		}
	}
	
	@Test
	public void executarRestricoesNaoLancaExcecaoTest() {
		final AelExameGrupoCaracteristica elemento = new AelExameGrupoCaracteristica();
		final AelExameGrupoCaracteristica exameGrupoCaract = null;
		
		final AelExamesMaterialAnalise exaEma = new AelExamesMaterialAnalise();
		final AelResultadoCaracteristica resultCarac = new AelResultadoCaracteristica();
		final AelGrupoResultadoCaracteristica grupoResultCaract = new AelGrupoResultadoCaracteristica();
		
		
		Mockito.when(mockedAelExameGrupoCaracteristicaDAO.obterAelExameGrupoCaracteristica(Mockito.any(AelExameGrupoCaracteristica.class))).thenReturn(elemento);
		
		Mockito.when(mockedAelExameGrupoCaracteristicaDAO.obterOriginal(Mockito.any(AelExameGrupoCaracteristicaId.class))).thenReturn(exameGrupoCaract);

		try {
			
			//seto o exame material
			exaEma.setId(new AelExamesMaterialAnaliseId("MUS", 67));
			elemento.setExameMaterialAnalise(exaEma);
			
			//seto o resultado caract
			resultCarac.setSeq(0);
			elemento.setResultadoCaracteristica(resultCarac);
			
			//seto o grupo resultado caract
			grupoResultCaract.setSeq(0);
			elemento.setGrupoResultadoCaracteristica(grupoResultCaract);
			
			
			systemUnderTest.executarRestricoes(elemento);
		} catch (BaseException e) {
			Assert.fail("Nao deveria lancar a excecao AEL_00046");
		}
	}
	
}
