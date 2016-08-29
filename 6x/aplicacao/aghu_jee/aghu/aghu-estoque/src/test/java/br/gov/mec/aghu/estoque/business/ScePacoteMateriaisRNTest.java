package br.gov.mec.aghu.estoque.business;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.ScePacoteMateriaisRN.ManterPacoteMateriaisRNExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceItemPacoteMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.ScePacoteMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceProducaoInternaDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMaterialRetornosDAO;
import br.gov.mec.aghu.estoque.vo.ItemPacoteMateriaisVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ScePacoteMateriaisRNTest extends AGHUBaseUnitTest<ScePacoteMateriaisRN>{

	@Mock
	private ScePacoteMateriaisDAO mockedScePacoteMateriaisDAO;
	@Mock
	private SceItemPacoteMateriaisDAO mockedSceItemPacoteMateriaisDAO;
	@Mock
	private SceReqMaterialRetornosDAO mockedSceReqMaterialRetornosDAO;
	@Mock
	private SceProducaoInternaDAO mockedSceProducaoInternaDAO;
	@Mock
	private ICentroCustoFacade mockedCentroCustoFacade;
	@Mock
	private ManterTransferenciaMaterialRN mockedManterTransferenciaMaterialRN;
	@Mock
	private ManterRequisicaoMaterialRN mockedManterRequisicaoMaterialRN;
	@Mock
	private IAghuFacade mockedAghuFacade;
	
	/**
	 * Valida BaseListException
	 * @param erros
	 * @param exception
	 * @return
	 */
	private boolean testaLista(BaseListException erros, BusinessExceptionCode exception){
		
		boolean retorno = false;
		
		final Iterator<BaseException> i = erros.iterator();
		
		while(i.hasNext()){
			
			String mensagem1 = i.next().getMessage();
			String mensagem2 = exception.toString();
			
			if(mensagem1.equals(mensagem2)){
				retorno = true;
			}
		}
		
		return retorno;
		
	}
	
	@Test
	public void testVerificarSeCodigoMaterialExisteError01(){

		BaseListException erros = new BaseListException();
		
		List<ItemPacoteMateriaisVO> itens = new LinkedList<ItemPacoteMateriaisVO>();
		ItemPacoteMateriaisVO vo = new ItemPacoteMateriaisVO();
		vo.setCodigoMaterial(1);
		itens.add(vo);

		this.systemUnderTest.verificarSeCodigoMaterialExiste(erros, itens, 1);
		
		if(testaLista(erros, ManterPacoteMateriaisRNExceptionCode.ERRO_CODIGO_MATERIAL_JA_EXISTENTE)){
			Assert.assertFalse(false);
		} else{
			Assert.assertFalse(true);
		}
		
	}
	
	@Test
	public void testVerificarSeCodigoMaterialExisteSuccess01(){

		BaseListException erros = new BaseListException();
		
		List<ItemPacoteMateriaisVO> itens = new LinkedList<ItemPacoteMateriaisVO>();
		ItemPacoteMateriaisVO vo = new ItemPacoteMateriaisVO();
		vo.setCodigoMaterial(1);
		itens.add(vo);

		this.systemUnderTest.verificarSeCodigoMaterialExiste(erros, itens, 2);
		
		if(!testaLista(erros, ManterPacoteMateriaisRNExceptionCode.ERRO_CODIGO_MATERIAL_JA_EXISTENTE)){
			Assert.assertFalse(false);
		} else{
			Assert.assertFalse(true);
		}
		
	}
	
	@Test
	public void testVerificarAlmoxarifadosItemEPacoteMateriaisError01(){

		BaseListException erros = new BaseListException();
		
		this.systemUnderTest.verificarAlmoxarifadosItemEPacoteMateriais(erros, (short)1, (short)2, DominioSituacao.A);
		
		if(testaLista(erros, ManterPacoteMateriaisRNExceptionCode.SCE_00393)){
			Assert.assertFalse(false);
		} else{
			Assert.assertFalse(true);
		}
		
	}
	
	@Test
	public void testVerificarAlmoxarifadosItemEPacoteMateriaisError02(){
		
		BaseListException erros = new BaseListException();
		
		this.systemUnderTest.verificarAlmoxarifadosItemEPacoteMateriais(erros, (short)1, (short)1, DominioSituacao.I);
		
		if(testaLista(erros, ManterPacoteMateriaisRNExceptionCode.SCE_00292)){
			Assert.assertFalse(false);
		} else{
			Assert.assertFalse(true);
		}
		
	}
	
	@Test
	public void testValidarDadosObrigatoriosPacoteMaterialError01(){
		
		BaseListException erros = new BaseListException();
		
		this.systemUnderTest.validarDadosObrigatoriosPacoteMaterial(erros, null, 1, (short)1 , DominioSituacao.A);
		
		if(testaLista(erros, ManterPacoteMateriaisRNExceptionCode.ERRO_PACOTE_MATERIAIS_CENTRO_CUSTO_PROPRIETARIO_OBRIGATORIO)){
			Assert.assertFalse(false);
		} else{
			Assert.assertFalse(true);
		}
	}
	
	
	@Test
	public void testVerificarCentrosCustoAtivosError01() {

		BaseListException erros = new BaseListException();

		final FccCentroCustos fccCentroCustos = new FccCentroCustos();
		fccCentroCustos.setIndSituacao(DominioSituacao.I);

		Mockito.when(mockedCentroCustoFacade.obterFccCentroCustos(Mockito.anyInt())).thenReturn(fccCentroCustos);

		this.systemUnderTest.verificarCentrosCustoAtivos(erros, 1, 1);

		if (testaLista(erros, ManterPacoteMateriaisRNExceptionCode.ERRO_PACOTE_MATERIAIS_CENTRO_CUSTO_INATIVO)) {
			Assert.assertFalse(false);
		} else {
			Assert.assertFalse(true);
		}
	}
	

}
