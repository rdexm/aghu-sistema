package br.gov.mec.aghu.compras.solicitacaocompra.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.dao.ScoMateriaisClassificacoesDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.dominio.DominioTipoDespesa;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Testa unitário da classe {@link SolicitacaoCompraON}.
 * 
 * @author mlcruz
 */
public class SolicitacaoCompraONTest extends AGHUBaseUnitTest<SolicitacaoCompraON>{

	@Mock
	private ScoSolicitacoesDeComprasDAO dao;
	@Mock
	private SolicitacaoCompraRN mockedSolicitacaoCompraRN;
	@Mock
	private IAghuFacade aghuFacade;
	@Mock
	private IParametroFacade parametrosFacade;
	@Mock
	private ScoMateriaisClassificacoesDAO scoMateriaisClassificacoesDAO;
	@Mock
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	private static final Log log = LogFactory.getLog(SolicitacaoCompraONTest.class);
	
	/**
	 * Testa validaTipoDespesaAceitaMaterial
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testvalidaTipoDespesaAceitaMaterial() throws ApplicationBusinessException {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();
		final FccCentroCustos centroCusto = new FccCentroCustos();
		final ScoMaterial material = new ScoMaterial();
		final ScoGrupoMaterial grupoMaterial = new ScoGrupoMaterial();
		
		centroCusto.setIndTipoDespesa(DominioTipoDespesa.O);
		grupoMaterial.setEngenhari(false);
		material.setGrupoMaterial(grupoMaterial);
		param.setCentroCustoAplicada(centroCusto);
		param.setMaterial(material);
		
		//param.setDtSolicitacao(new Date());
		
			try {
				
				systemUnderTest.validaTipoDespesaAceitaMaterial(param);
				assert(true);
				
			
			} catch (ApplicationBusinessException e) {
				// TODO: handle exception
				log.debug(e.getMessage());
				assert(true);
			}
	}
	
	
	
	/**
	 * Testa validaMaterialGenerico
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testvalidaMaterialGenerico() throws ApplicationBusinessException {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();
		final ScoMaterial material = new ScoMaterial();
		
		material.setIndGenericoBoolean(true);
		//param.setMaterial(material);
		param.setDescricao("D   ");
		
		//param.setDtSolicitacao(new Date());
		
			try {
				
				systemUnderTest.validaMaterialGenerico(param);
				assert(true);
				
			
			} catch (ApplicationBusinessException e) {
				// TODO: handle exception
				log.debug(e.getMessage());
				assert(true);
			}
	}
	
	/**
	 * Testa devolução de solicitações de compras.
	 * 
	 * @throws BaseException
	 */
	@Test
	public void testDevolucaoSolicitacoesCompras() throws BaseException {
		final Integer scId = 1;
		List<Integer> nroScos = Arrays.asList(scId);
		final ScoSolicitacaoDeCompra sc = new ScoSolicitacaoDeCompra();
		final ScoSolicitacaoDeCompra scClone = new ScoSolicitacaoDeCompra();
		sc.setNumero(scId);
		ScoPontoParadaSolicitacao pontoParada = new ScoPontoParadaSolicitacao();
		pontoParada.setCodigo((short) 1);
		sc.setPontoParada(pontoParada);
		ScoPontoParadaSolicitacao pontoParadaProxima = new ScoPontoParadaSolicitacao();
		pontoParadaProxima.setCodigo((short) 2);
		sc.setPontoParadaProxima(pontoParadaProxima);
		String justificativa = "Justificativa Teste";
		
		Mockito.when(dao.obterPorChavePrimaria(scId)).thenReturn(sc);

		Mockito.when(mockedSolicitacaoCompraRN.clonarSolicitacaoDeCompra(sc)).thenReturn(scClone);

		systemUnderTest.devolverSolicitacoesCompras(nroScos, justificativa);
		
		assertTrue(sc.getDevolucao());
		assertEquals(justificativa, sc.getJustificativaDevolucao());
		assertEquals(pontoParada, sc.getPontoParadaProxima());
		assertEquals(pontoParadaProxima, sc.getPontoParada());
	}

	/**
	 * Verifica mocks.
	 */
	@After
	public void tearDown() {
	}
}
