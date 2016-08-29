package br.gov.mec.aghu.estoque.business;

import static br.gov.mec.aghu.estoque.business.SceItemRmsRN.SceItemRmsRNExceptionCode.CENTRO_DE_CUSTO_NAO_ENCONTRADO;
import static br.gov.mec.aghu.estoque.business.SceItemRmsRN.SceItemRmsRNExceptionCode.SCE_00280;
import static br.gov.mec.aghu.estoque.business.SceItemRmsRN.SceItemRmsRNExceptionCode.SCE_00324;
import static br.gov.mec.aghu.estoque.business.SceItemRmsRN.SceItemRmsRNExceptionCode.SCE_00325;
import static br.gov.mec.aghu.estoque.business.SceItemRmsRN.SceItemRmsRNExceptionCode.SCE_00379;
import static br.gov.mec.aghu.estoque.business.SceItemRmsRN.SceItemRmsRNExceptionCode.SCE_00383;
import static br.gov.mec.aghu.estoque.business.SceItemRmsRN.SceItemRmsRNExceptionCode.SCE_00417;
import static br.gov.mec.aghu.estoque.business.SceItemRmsRN.SceItemRmsRNExceptionCode.SCE_00839;
import static br.gov.mec.aghu.estoque.business.SceItemRmsRN.SceItemRmsRNExceptionCode.SCO_00755;
import static br.gov.mec.aghu.estoque.business.SceItemRmsRN.SceItemRmsRNExceptionCode.SCO_00756;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.dominio.DominioTipoDespesa;
import br.gov.mec.aghu.estoque.dao.SceConsumoTotalMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceItemRmsId;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceItemRmsRNTest extends AGHUBaseUnitTest<SceItemRmsRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private SceItemRmsDAO mockedSceItemRmsDAO;
	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	@Mock
	private SceReqMateriaisDAO mockedSceReqMateriaisDAO;
	@Mock
	private SceReqMateriaisRN mockedSceReqMateriaisRN;
	@Mock
	private SceConsumoTotalMateriaisDAO mockedSceConsumoTotalMateriaisDAO;
	@Mock
	private IComprasFacade mockedComprasFacade;
	@Mock
	private SceMovimentoMaterialDAO mockedSceMovimentoMaterialDAO;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private ICentroCustoFacade mockedCentroCustoFacade;
	@Mock
	private ICascaFacade mockedCascaFacade;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU_EXCECAO = "Ocorreu a exceção: ";
	
	private SceItemRms sceItemRms;

	@Before
	public void doBeforeEachTestCase() {
		
		this.sceItemRms = new SceItemRms();
		SceItemRmsId id = new SceItemRmsId();
		id.setEalSeq(1);
		id.setRmsSeq(1);
		this.sceItemRms.setId(id);
		
		SceReqMaterial sceReqMateriais = new SceReqMaterial();
		sceReqMateriais.setSeq(Integer.MIN_VALUE);
		sceItemRms.setSceReqMateriais(sceReqMateriais);
		SceAlmoxarifado sceAlmoxarifados = new SceAlmoxarifado();
		sceReqMateriais.setAlmoxarifado(sceAlmoxarifados);
	}
	
	@Test
	public void testValidarAlteracaoSceItemRmsInserirError01(){
		
		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();
			sceReqMateriaisRetorno.setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
			
			this.sceItemRms.setIndTemEstoque(false);
			this.sceItemRms.setSceReqMateriais(sceReqMateriaisRetorno);
			
			final RapServidores servidor = new RapServidores();
			RapServidoresId id = new RapServidoresId();
			servidor.setId(id);
			
			final SceItemRms itemRmsRetornoOriginal = sceItemRms;
		
			Mockito.when(mockedSceItemRmsDAO.obterOriginal(Mockito.any(SceItemRms.class))).thenReturn(itemRmsRetornoOriginal);
			
			systemUnderTest.validarAlteracaoSceItemRms(sceItemRms, true); // Evento INSERIR
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00417);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00417, e.getCode(), SCE_00417);
		}
		
	}
	
	@Test
	public void testValidarAlteracaoSceItemRmsInserirError02(){
		
		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();
			sceReqMateriaisRetorno.setIndSituacao(DominioSituacaoRequisicaoMaterial.A);
			
			this.sceItemRms.setIndTemEstoque(false);
			this.sceItemRms.setSceReqMateriais(sceReqMateriaisRetorno);
		
			final SceItemRms itemRmsRetorno = sceItemRms;
		
			final RapServidores servidor = new RapServidores();
			RapServidoresId id = new RapServidoresId();
			servidor.setId(id);
			
			Mockito.when(mockedSceItemRmsDAO.obterOriginal(Mockito.any(SceItemRms.class))).thenReturn(itemRmsRetorno);

			systemUnderTest.validarAlteracaoSceItemRms(sceItemRms, true); // Evento INSERIR
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00325);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00325, e.getCode(), SCE_00325);
		}
		
	}
	
	@Test
	public void testValidarAlteracaoSceItemRmsInserirError03(){
		
		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();
			sceReqMateriaisRetorno.setIndSituacao(DominioSituacaoRequisicaoMaterial.E);
			
			this.sceItemRms.setIndTemEstoque(false);
			this.sceItemRms.setQtdeEntregue(null);
			this.sceItemRms.setSceReqMateriais(sceReqMateriaisRetorno);
			
			final SceItemRms itemRmsRetorno = sceItemRms;
		
			Mockito.when(mockedSceItemRmsDAO.obterOriginal(Mockito.any(SceItemRms.class))).thenReturn(itemRmsRetorno);

			systemUnderTest.validarAlteracaoSceItemRms(sceItemRms, true); // Evento INSERIR
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00379);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00379, e.getCode(), SCE_00379);
		}
		
	}
	
	//@Test
	public void testValidarAlteracaoSceItemRmsAtualizarError01(){
		
		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();
			sceReqMateriaisRetorno.setIndSituacao(DominioSituacaoRequisicaoMaterial.C);
			this.sceItemRms.setIndTemEstoque(false);
			this.sceItemRms.setSceReqMateriais(sceReqMateriaisRetorno);
			
			final SceItemRms sceItemRmsOriginal = new SceItemRms();
			SceItemRmsId id = new SceItemRmsId();
			id.setEalSeq(Integer.MIN_VALUE);
			sceItemRmsOriginal.setId(id);
			sceItemRmsOriginal.setQtdeRequisitada(1);
			ScoUnidadeMedida scoUnidadeMedida = new ScoUnidadeMedida();
			sceItemRmsOriginal.setScoUnidadeMedida(scoUnidadeMedida);
			
			Mockito.when(mockedSceItemRmsDAO.obterOriginal(Mockito.any(SceItemRms.class))).thenReturn(sceItemRmsOriginal);

			systemUnderTest.validarAlteracaoSceItemRms(sceItemRms, false); // Evento ATUALIZAR
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00324);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00324, e.getCode(), SCE_00324);
		}
		
	}
	
	//@Test
	public void testValidarAlteracaoSceItemRmsAtualizarError02(){
		
		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();
			sceReqMateriaisRetorno.setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
			this.sceItemRms.setIndTemEstoque(true);
			this.sceItemRms.setSceReqMateriais(sceReqMateriaisRetorno);
			
			final SceItemRms sceItemRmsOriginal = new SceItemRms();
			SceItemRmsId id = new SceItemRmsId();
			id.setEalSeq(Integer.MIN_VALUE);
			sceItemRmsOriginal.setId(id);
			sceItemRmsOriginal.setQtdeRequisitada(1);
			ScoUnidadeMedida scoUnidadeMedida = new ScoUnidadeMedida();
			sceItemRmsOriginal.setScoUnidadeMedida(scoUnidadeMedida);
			sceItemRmsOriginal.setQtdeEntregue(Integer.MIN_VALUE);

			Mockito.when(mockedSceItemRmsDAO.obterOriginal(Mockito.any(SceItemRms.class))).thenReturn(sceItemRmsOriginal);

			systemUnderTest.validarAlteracaoSceItemRms(sceItemRms, false); // Evento ATUALIZAR
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00383);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00383, e.getCode(), SCE_00383);
		}
		
	}

	@Test
	public void testValidarAlteracaoSceItemRmsEstoqueAlmoxarifadoError01(){

		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();
			
			final SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = new SceEstoqueAlmoxarifado();
			sceEstoqueAlmoxarifado.setSeq(1);
			this.sceItemRms.setEstoqueAlmoxarifado(sceEstoqueAlmoxarifado);
			
			ScoUnidadeMedida scoUnidadeMedida = new ScoUnidadeMedida();
			scoUnidadeMedida.setCodigo("A");
			sceEstoqueAlmoxarifado.setUnidadeMedida(scoUnidadeMedida);
			
			ScoUnidadeMedida scoUnidadeMedida2 = new ScoUnidadeMedida();
			scoUnidadeMedida2.setCodigo("B");
			this.sceItemRms.setScoUnidadeMedida(scoUnidadeMedida2);
			
			this.sceItemRms.setEstoqueAlmoxarifado(sceEstoqueAlmoxarifado);

			Mockito.when(mockedSceEstoqueAlmoxarifadoDAO.obterEstoqueAlmoxarifadoPorId(Mockito.anyInt())).thenReturn(sceEstoqueAlmoxarifado);

			Mockito.when(mockedSceReqMateriaisDAO.buscarSceReqMateriaisPorId(Mockito.anyInt())).thenReturn(sceReqMateriaisRetorno);

			systemUnderTest.validarAlteracaoSceItemRmsEstoqueAlmoxarifado(sceItemRms);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00280);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00280, e.getCode(), SCE_00280);
		}
	}
	
	@Test
	public void testAtualizarSceItemRmsGrupoMaterialSemGrupoError01(){

		try {
			
			Mockito.when(mockedSceReqMateriaisDAO.buscarSceReqMateriaisPorAlmoxarifado(Mockito.anyInt(), Mockito.anyShort())).thenReturn(null);

			systemUnderTest.atualizarSceItemRmsGrupoMaterialSemGrupo(sceItemRms, NOME_MICROCOMPUTADOR);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00839);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00839, e.getCode(), SCE_00839);
		}
	}

	/*@Test
	public void testAtualizarSceItemRmsGrupoMaterialSemGrupoError02(){

		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();
			SceAlmoxarifado sceAlmoxarifados2 = new SceAlmoxarifado();
			sceAlmoxarifados2.setIndCentral(false);
			sceReqMateriaisRetorno.setAlmoxarifado(sceAlmoxarifados2);
	
			
			mockingContext.checking(new Expectations() {{
				
				oneOf(mockedSceReqMateriaisDAO).buscarSceReqMateriaisPorAlmoxarifado(with(any(Integer.class)),with(any(Short.class))); 
				will(returnValue(sceReqMateriaisRetorno));
				
				oneOf(mockedSceItemRmsDAO).buscarSceItemRmsPorGrupoMaterialSemGrupo(with(any(Integer.class))); 
				will(returnValue(null));  // Teste principal

				
			}});

			systemUnderTest.atualizarSceItemRmsGrupoMaterialSemGrupo(sceItemRms);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00865);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCE_00865, e.getCode(), SCE_00865);
		}
	}*/

	//@Test
	public void testAtualizarSceItemRmsGrupoMaterialSemGrupoSuccess01(){

		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();
			SceAlmoxarifado sceAlmoxarifados2 = new SceAlmoxarifado();
			sceAlmoxarifados2.setIndCentral(true);
			sceReqMateriaisRetorno.setAlmoxarifado(sceAlmoxarifados2);
			
			final SceItemRms itemRmsRetorno = new SceItemRms();
			ScoGrupoMaterial scoGrupoMaterial = new ScoGrupoMaterial();
			scoGrupoMaterial.setCodigo(1);
			sceReqMateriaisRetorno.setGrupoMaterial(scoGrupoMaterial);
			itemRmsRetorno.setSceReqMateriais(sceReqMateriaisRetorno);
			
			SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = new SceEstoqueAlmoxarifado();
			ScoMaterial material = new ScoMaterial();
			material.setGrupoMaterial(scoGrupoMaterial);
			sceEstoqueAlmoxarifado.setMaterial(material);
			itemRmsRetorno.setEstoqueAlmoxarifado(sceEstoqueAlmoxarifado);
			sceReqMateriaisRetorno.setGrupoMaterial(scoGrupoMaterial);
			
			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(BigDecimal.valueOf(1));
			
			Mockito.when(mockedSceReqMateriaisDAO.buscarSceReqMateriaisPorAlmoxarifado(Mockito.anyInt(), Mockito.anyShort())).thenReturn(null);

			Mockito.when(mockedSceItemRmsDAO.buscarSceItemRmsPorGrupoMaterialSemGrupo(Mockito.anyInt())).thenReturn(null);

			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
			
			systemUnderTest.atualizarSceItemRmsGrupoMaterialSemGrupo(sceItemRms, NOME_MICROCOMPUTADOR);
			
		} catch (BaseException e) {
			fail(MENSAGEM_OCORREU_EXCECAO + e.getCode());
		}
	}
	
	@Test
	public void testValidarSceItemRmsMaterialTipoDespesaCentroError01(){
		try {

			final SceItemRms itemRmsRetorno = new SceItemRms();
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();
			itemRmsRetorno.setSceReqMateriais(sceReqMateriaisRetorno);
			
			FccCentroCustos fccCentroCustos = new FccCentroCustos();
			fccCentroCustos.setIndTipoDespesa(DominioTipoDespesa.S);
			sceReqMateriaisRetorno.setCentroCusto(fccCentroCustos);
			
			Mockito.when(mockedSceItemRmsDAO.buscarSceItemRmsPorMaterialCompativelTipoDespesaCentroCusto(Mockito.any(SceItemRms.class))).thenReturn(itemRmsRetorno);

			systemUnderTest.validarSceItemRmsMaterialTipoDespesaCentroCusto(sceItemRms);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCO_00756);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCO_00756, e.getCode(), SCO_00756);
		}
	}
	
	@Test
	public void testValidarSceItemRmsMaterialTipoDespesaCentroError02(){
		try {

			final SceItemRms itemRmsRetorno = new SceItemRms();
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();
			itemRmsRetorno.setSceReqMateriais(sceReqMateriaisRetorno);
			
			FccCentroCustos fccCentroCustos = new FccCentroCustos();
			fccCentroCustos.setIndTipoDespesa(DominioTipoDespesa.O);
			sceReqMateriaisRetorno.setCentroCusto(fccCentroCustos);
			
			itemRmsRetorno.setSceReqMateriais(sceReqMateriaisRetorno);
			ScoGrupoMaterial scoGrupoMaterial = new ScoGrupoMaterial();
			scoGrupoMaterial.setEngenhari(false);
			sceReqMateriaisRetorno.setGrupoMaterial(scoGrupoMaterial);
			
			Mockito.when(mockedSceItemRmsDAO.buscarSceItemRmsPorMaterialCompativelTipoDespesaCentroCusto(Mockito.any(SceItemRms.class))).thenReturn(itemRmsRetorno);

			systemUnderTest.validarSceItemRmsMaterialTipoDespesaCentroCusto(sceItemRms);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCO_00755);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCO_00755, e.getCode(), SCO_00755);
		}
	}

	@Test
	public void testValidarSceItemRmsMaterialTipoDespesaCentroError03(){
		try {

			Mockito.when(mockedSceItemRmsDAO.buscarSceItemRmsPorMaterialCompativelTipoDespesaCentroCusto(Mockito.any(SceItemRms.class))).thenReturn(null);

			systemUnderTest.validarSceItemRmsMaterialTipoDespesaCentroCusto(sceItemRms);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + CENTRO_DE_CUSTO_NAO_ENCONTRADO);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + CENTRO_DE_CUSTO_NAO_ENCONTRADO, e.getCode(), CENTRO_DE_CUSTO_NAO_ENCONTRADO);
		}
	}


}
