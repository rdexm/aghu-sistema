/**
 * 
 */
package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.ScoMaterial;

/**
 * Classe que realiza a pesquisa de requisições de materiais, relacionada a
 * visualização de itens de requisições
 * 
 * @author clayton.bras
 * 
 */

public class PesquisaItensRequisicaoMaterialPaginatorController extends
		ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<SceReqMaterial> dataModel;
	
	private static final long serialVersionUID = 9100123854108042665L;
	
	private static final String PAGE_VISUALIZAR = "visualizarItemRequisicaoMaterial";


	private enum EnumTargetPesquisaItensRequisicaoMaterial {
		VISUALIZAR;
	}
	

	private SceReqMaterial requisicao;
	private DominioSimNao estornada;
	private DominioSimNao automatica;
	private ScoMaterial material;
	

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	private Integer seqRequisicaoMaterial;

	@EJB
	private IComprasFacade comprasFacade;


	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if(requisicao == null){
			requisicao = new SceReqMaterial();
		}		
	
	}


	public String pesquisar() {
		SceReqMaterial requisicaoRetornada = null;
		String retorno = null;
		dataModel.reiniciarPaginator();
		if(getDataModel().getRowCount() == 1){
			requisicaoRetornada = (SceReqMaterial)getDataModel().getRowData();
			setSeqRequisicaoMaterial(requisicaoRetornada.getSeq());
			retorno = EnumTargetPesquisaItensRequisicaoMaterial.VISUALIZAR.toString();
		}
		return retorno;
	}

	public void limparCampos() {
		setRequisicao(new SceReqMaterial());
		setEstornada(null);
		setAutomatica(null);
		dataModel.setPesquisaAtiva(false);
		dataModel.limparPesquisa();
	}
	
	public String visualizar(){
		return PAGE_VISUALIZAR;
	}

	/*
	 * /* (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.seam.action.ActionController implements ActionPaginator#recuperarListaPaginada(
	 * java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	public List<SceReqMaterial> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<SceReqMaterial> listaRequisicoesMateriais = new ArrayList<SceReqMaterial>();
//		Short seqAlmoxarifado = null;
//		Integer codigoCentroCustosRequisicao = null,
//				codigoCentroCustosAplicacao = null;
//		if(requisicao.getAlmoxarifado() != null){
//			seqAlmoxarifado = requisicao.getAlmoxarifado().getSeq();
//		}
//		if(requisicao.getCentroCusto() != null){
//			codigoCentroCustosRequisicao = requisicao.getCentroCusto().getCodigo();
//		}
//		if(requisicao.getCentroCustoAplica() != null){
//			codigoCentroCustosAplicacao = requisicao.getCentroCustoAplica().getCodigo();
//		}
		if(getEstornada() != null)
		{
			requisicao.setEstorno(DominioSimNao.S.equals(getEstornada()));	
		}else{
			requisicao.setEstorno(null);
		}
		
		if(getAutomatica() != null){
			requisicao.setAutomatica(DominioSimNao.S.equals(getAutomatica()));
		}
		else{
			requisicao.setAutomatica(null);
		}
		
		listaRequisicoesMateriais = estoqueFacade.pesquisarRequisicaoMaterialFiltroCompleto(requisicao, firstResult, maxResult, orderProperty, false, null, null,material);
//					
//					.pesquisarRequisicaoMaterialVisualizacao(firstResult, maxResult, orderProperty, false, 
//							requisicao.getSeq(), 
//							requisicao.getIndSituacao(), 
//							requisicao.getEstorno(), 
//							seqAlmoxarifado, 
//							codigoCentroCustosRequisicao,
//							codigoCentroCustosAplicacao);

		return listaRequisicoesMateriais;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.seam.action.ActionController implements ActionPaginator#recuperarCount()
	 */
	@Override
	public Long recuperarCount() {
//		Short seqAlmoxarifado = null;
//		Integer codigoCentroCustosRequisicao = null,
//				codigoCentroCustosAplicacao = null;
//		if(requisicao.getAlmoxarifado() != null){
//			seqAlmoxarifado = requisicao.getAlmoxarifado().getSeq();
//		}
//		if(requisicao.getCentroCusto() != null){
//			codigoCentroCustosRequisicao = requisicao.getCentroCusto().getCodigo();
//		}
//		if(requisicao.getCentroCustoAplica() != null){
//			codigoCentroCustosAplicacao = requisicao.getCentroCustoAplica().getCodigo();
//		}
		if(getEstornada() != null)
		{
			requisicao.setEstorno(DominioSimNao.S.equals(getEstornada()));	
		}else{
			requisicao.setEstorno(null);
		}
		return estoqueFacade.pesquisarRequisicaoMaterialFiltroCompletoCount(requisicao, null, null,material);
				
//				.pesquisarRequisicaoMaterialVisualizacaoCount(
//				requisicao.getSeq(),
//				requisicao.getIndSituacao(),
//				requisicao.getEstorno(),
//				seqAlmoxarifado,
//				codigoCentroCustosRequisicao,
//				codigoCentroCustosAplicacao);
	}

	public SceReqMaterial getRequisicao() {
		return requisicao;
	}

	public void setRequisicao(SceReqMaterial requisicao) {
		this.requisicao = requisicao;
	}

	/**
	 * Pesquisa do suggestion de almoxarifado
	 * @param parametro 
	 * @return List<SceAlmoxarifados>
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosPorCodigoDescricao(
			String parametro) {
		return estoqueFacade.pesquisarAlmoxarifadosPorCodigoDescricao(parametro);
	}

	/**
	 * Pesquisa do suggestion de centro de custo
	 * @param parametro
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoPorCodigoDescricao(String parametro) {
		String filtro = "";
		if(parametro != null){
			filtro = parametro;
		}
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(filtro);
	}
	
	/**
	 * Metodo para pesquisa na suggestion box de material
	 */
	public List<ScoMaterial> obterMaterial(String paramPesq) throws ApplicationBusinessException {
		return this.comprasFacade.pesquisarMaterial(paramPesq);
	}

	public DominioSimNao getEstornada() {
		return estornada;
	}
	
	public void setEstornada(DominioSimNao estornada) {
		this.estornada = estornada;
	}
	
	public DominioSimNao[] getDominioSimNaoSelect(){
		return new DominioSimNao[]{DominioSimNao.S, DominioSimNao.N};
	}
	
	public Integer getSeqRequisicaoMaterial() {
		return seqRequisicaoMaterial;
	}
	public void setSeqRequisicaoMaterial(Integer seqRequisicaoMaterial) {
		this.seqRequisicaoMaterial = seqRequisicaoMaterial;
	}


	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null) {
			abreviado = StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}


	public DominioSimNao getAutomatica() {
		return automatica;
	}


	public void setAutomatica(DominioSimNao automatica) {
		this.automatica = automatica;
	}
	
	public String getDescricaoDominioSimNao(Boolean valor){
		return DominioSimNao.getInstance(valor).getDescricao();
	}
 

	public DynamicDataModel<SceReqMaterial> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceReqMaterial> dataModel) {
	 this.dataModel = dataModel;
	}

	public ScoMaterial getMaterial() {
		return material;
	}
	
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	
}	