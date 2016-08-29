package br.gov.mec.aghu.exames.business;

import static br.gov.mec.aghu.exames.business.ListarAmostrasSolicitacaoRecebimentoRN.ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01969;
import static br.gov.mec.aghu.exames.business.ListarAmostrasSolicitacaoRecebimentoRN.ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01974;
import static br.gov.mec.aghu.exames.business.ListarAmostrasSolicitacaoRecebimentoRN.ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01975;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAmostrasId;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ListarAmostrasSolicitacaoRecebimentoRNTest extends AGHUBaseUnitTest<ListarAmostrasSolicitacaoRecebimentoRN>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private AelAmostrasDAO mockedAelAmostrasDAO;
	@Mock
	private AelAmostraItemExamesDAO mockedAelAmostraItemExamesDAO;
	@Mock
	private AelUnfExecutaExamesDAO mockedAelUnfExecutaExamesDAO;
	@Mock
	private ISolicitacaoExameFacade mockedSolicitacaoExameFacade;
	
	public AelAmostras getAmostraPadrao(){
		
		
		AelAmostras amostra = new AelAmostras();
		AelAmostrasId id = new AelAmostrasId();
		id.setSeqp(Short.valueOf("0"));
		amostra.setId(id);
		
	
		AelAmostraItemExames amostraItemExame = new AelAmostraItemExames();
		
		AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		
		AelExames exame = new AelExames();
		itemSolicitacaoExame.setExame(exame);
		
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		itemSolicitacaoExame.setMaterialAnalise(materialAnalise);
		
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		itemSolicitacaoExame.setUnidadeFuncional(unidadeFuncional);
		
		amostraItemExame.setAelItemSolicitacaoExames(itemSolicitacaoExame);
		
		Set<AelAmostraItemExames> set = new HashSet<AelAmostraItemExames>();
		set.add(amostraItemExame);
		amostra.setAelAmostraItemExames(set);
		
		return amostra;
	}
	
	
	/**
	 * ORADB AELP_TESTA_NRO_FRASCO
	 */
	
	@Test
	public void validarNumeroFrascoNaoRequerFrascoTest(){
		
		AelAmostras amostra = this.getAmostraPadrao();
		amostra.setNroFrascoFabricante(null);
		
		final AelUnfExecutaExames unidadeFuncionalExecutaExames = new AelUnfExecutaExames();
		unidadeFuncionalExecutaExames.setIndNroFrascoFornec(Boolean.FALSE);
		Mockito.when(mockedAelUnfExecutaExamesDAO.buscaAelUnfExecutaExames(Mockito.any(AelExames.class), Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class)))
		.thenReturn(unidadeFuncionalExecutaExames);
		
		
		try {
			systemUnderTest.validarNumeroFrasco(amostra,null);
		} catch (BaseException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
	}
	
	@Test
	public void validarNumeroFrascoRequerFrascoTest(){
		
		AelAmostras amostra = this.getAmostraPadrao();
		amostra.setNroFrascoFabricante(null);
		
		final AelUnfExecutaExames unidadeFuncionalExecutaExames = new AelUnfExecutaExames();
		unidadeFuncionalExecutaExames.setIndNroFrascoFornec(Boolean.TRUE);
		Mockito.when(mockedAelUnfExecutaExamesDAO.buscaAelUnfExecutaExames(Mockito.any(AelExames.class), Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class)))
		.thenReturn(unidadeFuncionalExecutaExames);
		try {
			systemUnderTest.validarNumeroFrasco(amostra, null);
			Assert.fail("Deveria ter ocorrido uma "+AEL_01969);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+AEL_01969, e.getCode(), AEL_01969);
		}
		
	}
	
	@Test
	public void validarNumeroFrascoRequerFrascoFrascoVazioTest(){
		
		AelAmostras amostra = this.getAmostraPadrao();
		amostra.setNroFrascoFabricante("");
		
		final AelUnfExecutaExames unidadeFuncionalExecutaExames = new AelUnfExecutaExames();
		unidadeFuncionalExecutaExames.setIndNroFrascoFornec(Boolean.TRUE);
		Mockito.when(mockedAelUnfExecutaExamesDAO.buscaAelUnfExecutaExames(Mockito.any(AelExames.class), Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class)))
		.thenReturn(unidadeFuncionalExecutaExames);
		try {
			systemUnderTest.validarNumeroFrasco(amostra,"");
			Assert.fail("Deveria ter ocorrido uma "+AEL_01969);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+AEL_01969, e.getCode(), AEL_01969);
		}
		
	}	
	
	
	@Test
	public void validarNumeroFrascoTamanhoValidoTest(){
		
		AelAmostras amostra = this.getAmostraPadrao();
		amostra.setNroFrascoFabricante("12345678");
		
		final AelUnfExecutaExames unidadeFuncionalExecutaExames = new AelUnfExecutaExames();
		unidadeFuncionalExecutaExames.setIndNroFrascoFornec(Boolean.TRUE);
		Mockito.when(mockedAelUnfExecutaExamesDAO.buscaAelUnfExecutaExames(Mockito.any(AelExames.class), Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class)))
		.thenReturn(unidadeFuncionalExecutaExames);
		
		try {
			systemUnderTest.validarNumeroFrasco(amostra,"12345678");
		} catch (BaseException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
	}
	
	@Test
	public void validarNumeroFrascoTamanhoInvalidoTest(){
		
		AelAmostras amostra = this.getAmostraPadrao();
		amostra.setNroFrascoFabricante("123");
		
		final AelUnfExecutaExames unidadeFuncionalExecutaExames = new AelUnfExecutaExames();
		unidadeFuncionalExecutaExames.setIndNroFrascoFornec(Boolean.TRUE);
		Mockito.when(mockedAelUnfExecutaExamesDAO.buscaAelUnfExecutaExames(Mockito.any(AelExames.class), Mockito.any(AelMateriaisAnalises.class), Mockito.any(AghUnidadesFuncionais.class)))
		.thenReturn(unidadeFuncionalExecutaExames);
		try {
			systemUnderTest.validarNumeroFrasco(amostra, "123");
			Assert.fail("Deveria ter ocorrido uma "+AEL_01975);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+AEL_01975, e.getCode(), AEL_01975);
		}
		
	}	
	
	//@Test
	public void imprimirEtiquetaAmostraSemNumeroUnicoUnidadeRequerNumeroUnicoTest(){
		
		AelAmostras amostra = this.getAmostraPadrao();
		AelAmostrasId id = new AelAmostrasId();
		id.setSeqp(Short.MIN_VALUE);
		amostra.setId(id);
		String nomeMicro = "microTeste";
		
		// Numero unico nullo
		amostra.setNroUnico(null);
		
		// Unidade funcional
		AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 5468823316035693058L;
		};
		amostra.setUnidadesFuncionais(unidadeExecutora);
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		solicitacaoExames.setAtendimento(new AghAtendimentos());
		amostra.setSolicitacaoExame(solicitacaoExames);

		try {	
			
			systemUnderTest.imprimirEtiquetaAmostra(amostra, unidadeExecutora, nomeMicro);
			Assert.fail("Deveria ter ocorrido uma "+AEL_01974);
		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser "+AEL_01974, e.getCode(), AEL_01974);
		}
		
	}
	
	//@Test
	public void imprimirEtiquetaAmostraComNumeroUnicoUnidadeRequerNumeroUnicoTest(){
		
		AelAmostras amostra = this.getAmostraPadrao();
		AelAmostrasId id = new AelAmostrasId();
		id.setSeqp(Short.MIN_VALUE);
		amostra.setId(id);
		String nomeMicro = "microTeste";
		
		// Numero unico populado
		amostra.setNroUnico(Integer.MIN_VALUE);
		
		// Unidade funcional 
		AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -8324221529965506467L;
		};
		amostra.setUnidadesFuncionais(unidadeExecutora);
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		solicitacaoExames.setAtendimento(new AghAtendimentos());
		amostra.setSolicitacaoExame(solicitacaoExames);

		try {	
			
			systemUnderTest.imprimirEtiquetaAmostra(amostra, unidadeExecutora, nomeMicro);
			
		} catch (BaseException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
	}
	
	//@Test
	public void imprimirEtiquetaAmostraUnicoUnidadeNaoRequerNumeroUnicoTest(){
		
		AelAmostras amostra = this.getAmostraPadrao();
		AelAmostrasId id = new AelAmostrasId();
		id.setSeqp(Short.MIN_VALUE);
		amostra.setId(id);
		String nomeMicro = "microTeste";
		
		// Unidade funcional
		AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -7980713186609920714L;
		};
		amostra.setUnidadesFuncionais(unidadeExecutora);
		
		AelSolicitacaoExames solicitacaoExames = new AelSolicitacaoExames();
		solicitacaoExames.setAtendimento(new AghAtendimentos());
		amostra.setSolicitacaoExame(solicitacaoExames);

		try {	
			
			systemUnderTest.imprimirEtiquetaAmostra(amostra, unidadeExecutora, nomeMicro);
			
		} catch (BaseException e) {
			fail("Exceção gerada: " + e.getCode());
		}
		
	}
}
