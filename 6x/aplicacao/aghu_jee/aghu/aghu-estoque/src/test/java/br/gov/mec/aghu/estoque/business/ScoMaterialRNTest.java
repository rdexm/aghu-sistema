package br.gov.mec.aghu.estoque.business;

import static br.gov.mec.aghu.estoque.business.ScoMaterialRN.ScoMaterialRNExceptionCode.SCO_00298;

import org.hibernate.jdbc.Expectations;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEntradaSaidaSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ScoMaterialRNTest extends AGHUBaseUnitTest<ScoMaterialRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private IFarmaciaApoioFacade mockedFarmaciaApoioFacade;
	@Mock
	private IComprasFacade mockedComprasFacade;
	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;
	@Mock
	private SceAlmoxarifadoDAO mockedSceAlmoxarifadoDAO;
	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	@Mock
	private SceHistoricoProblemaMaterialDAO mockedSceHistoricoProblemaMaterialDAO;
	@Mock
	private SceEntradaSaidaSemLicitacaoDAO mockedSceEntradaSaidaSemLicitacaoDAO;
	@Mock
	private SceEstoqueGeralRN mockedSceEstoqueGeralRN;
	@Mock
	private SceEstoqueGeralDAO mockedSceEstoqueGeralDAO;
	@Mock
	private SceEstoqueAlmoxarifadoRN mockedSceEstoqueAlmoxarifadoRN;
	
	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	
	@Test
	public void testValidarMaterialEstocavelGenericoError01(){
	
		try {
			
			ScoMaterial material = new ScoMaterial();
			material.setEstocavel(true);
			material.setIndGenerico(DominioSimNao.S);
			
			this.systemUnderTest.validarMaterialEstocavelGenerico(material);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SCO_00298);
			
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_DEVERIA_TER_OCORRIDO + SCO_00298, SCO_00298, e.getCode());
		}

	}
	
	
	@Test
	public void testInserirScoMaterialJN() throws BaseException {
		ScoMaterial material = new ScoMaterial();
		ScoMaterial old = new ScoMaterial();
		this.systemUnderTest.inserirScoMaterialJN(material, old);		
	}	

}
