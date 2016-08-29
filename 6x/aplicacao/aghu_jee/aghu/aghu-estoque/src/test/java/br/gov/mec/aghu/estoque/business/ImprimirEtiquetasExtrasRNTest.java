/**
 * 
 */
package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * @author ehgsilva
 *
 */
public class ImprimirEtiquetasExtrasRNTest extends AGHUBaseUnitTest<ImprimirEtiquetasExtrasRN>{

	
	//Daos e Facades a serem mockadas
	@Mock
	private IEstoqueFacade mockedEstoqueFacade;

	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;

	@Test
	public void verificarSetSolicitadoEm(){
		SceLoteDocImpressao entidade = new SceLoteDocImpressao();
		systemUnderTest.setSolicitadoEm(entidade);
		Assert.assertNotNull(entidade.getSolicitadoEm());
//		Assert.assertEquals("Não retornou uma Data", Calendar.class, entidade.getSolicitadoEm().getClass());
	}
	
			
	
	public SceLoteDocImpressao getEntidadeVerificarsetConcentracaoDescricao(){
		
		SceLoteDocImpressao entidade = new SceLoteDocImpressao();
		
		AfaMedicamento med = new AfaMedicamento();
		med.setConcentracao(new BigDecimal("765"));
				
		MpmUnidadeMedidaMedica umm = new MpmUnidadeMedidaMedica();
		umm.setDescricao("ml");
		med.setMpmUnidadeMedidaMedicas(umm);
		
		ScoMaterial mat = new ScoMaterial();
		mat.setAfaMedicamento(med);
		entidade.setMaterial(mat);		
		
		return entidade;
	}
	
	
	//Expectations
	private void esperarObterPorDadosInformados(final SceLoteDocImpressao entidade) {
		
		Mockito.when(mockedEstoqueFacade.pesquisarPorDadosInformados(new SceLoteDocImpressao())).thenReturn(Arrays.asList(entidade));
	}
	
	
	@Test
	public void verificarsetNroInicialNroFinalEntidadeNula(){		
		
		SceLoteDocImpressao entidade = null;
		esperarObterPorDadosInformados(entidade);
		
		SceLoteDocImpressao entidadeMock = new SceLoteDocImpressao();
		entidadeMock.setQtde(2);		
		
		Mockito.when(mockedFarmaciaFacade.obterMedicamentoPorMatCodigo(Mockito.anyInt())).thenReturn(new AfaMedicamento());
		
		systemUnderTest.setNroInicialNroFinal(entidadeMock);
		
		Assert.assertEquals(entidadeMock.getNroInicial(), Integer.valueOf(1));
		
		Integer nroFinalEsperado = Integer.valueOf(entidadeMock.getQtde());
		Assert.assertEquals(entidadeMock.getNroFinal(), nroFinalEsperado);
	}
	
	
	@Test
	public void verificarsetNroInicialNroFinalComTamanhoMenorQueCincoEntidadeNaoNula(){		
		
		SceLoteDocImpressao entidadeMock = new SceLoteDocImpressao();
		Integer nroFinalPadrao = 12345;
		entidadeMock.setNroFinal(nroFinalPadrao);
		entidadeMock.setQtde(2);
		esperarObterPorDadosInformados(entidadeMock);
		
		Mockito.when(mockedFarmaciaFacade.obterMedicamentoPorMatCodigo(Mockito.anyInt())).thenReturn(new AfaMedicamento());
		
		systemUnderTest.setNroInicialNroFinal(entidadeMock);
		
		Integer nroInicialEsperado = Integer.valueOf(nroFinalPadrao + 1);
		Assert.assertEquals(entidadeMock.getNroInicial(), nroInicialEsperado);
		
		Integer nroFinalEsperado = Integer.valueOf((entidadeMock.getNroInicial() + entidadeMock.getQtde()) - 1);
		Assert.assertEquals(entidadeMock.getNroFinal(), nroFinalEsperado);
	}
	
	@Test
	public void verificarsetNroInicialNroFinalComTamanhoMaiorQueCincoEntidadeNaoNula(){		
		
		SceLoteDocImpressao entidadeMock = new SceLoteDocImpressao();
		entidadeMock.setNroFinal(123456);
		entidadeMock.setQtde(2);
		esperarObterPorDadosInformados(entidadeMock);
		
		Mockito.when(mockedFarmaciaFacade.obterMedicamentoPorMatCodigo(Mockito.anyInt())).thenReturn(new AfaMedicamento());
		
		systemUnderTest.setNroInicialNroFinal(entidadeMock);
		
		Assert.assertEquals(entidadeMock.getNroInicial(), Integer.valueOf(1));
		
		Integer nroFinalEsperado = Integer.valueOf((entidadeMock.getNroInicial() + entidadeMock.getQtde()) - 1);
		Assert.assertEquals(entidadeMock.getNroFinal(), nroFinalEsperado);
	}
	

	

}