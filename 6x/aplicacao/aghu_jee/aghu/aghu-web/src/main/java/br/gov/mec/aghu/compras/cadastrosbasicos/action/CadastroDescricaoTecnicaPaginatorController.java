package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ScoDescricaoTecnicaPadraoVO;
import br.gov.mec.aghu.model.ScoDescricaoTecnicaPadrao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroDescricaoTecnicaPaginatorController extends
ActionController implements ActionPaginator{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3466800293865968856L;
	
	private static final String CADASTRO_DESCRICAO_TECNICA = "cadastroDescricaoTecnicaCRUD";
	
	private ScoDescricaoTecnicaPadrao selecionado;
	
	private ScoDescricaoTecnicaPadraoVO vo = new ScoDescricaoTecnicaPadraoVO();
	
	private ScoMaterial material;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@PostConstruct
	public void inicializar(){
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ScoDescricaoTecnicaPadrao> dataModel;

	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limpar() {
		this.vo = new ScoDescricaoTecnicaPadraoVO();
		this.material = null;
		this.dataModel.limparPesquisa();
	}
	
	public String inserir() {
		return CADASTRO_DESCRICAO_TECNICA;
	}
	
	public void excluir() {
		this.comprasFacade.deletarDescricaoTecnica(selecionado.getCodigo());
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DESCRICAO_DELETADA_SUCESSO");
	}
	
	public String editar(){
		return CADASTRO_DESCRICAO_TECNICA;
	}
	
	@Override
	public Long recuperarCount() {
		return comprasFacade.listarDescricaoTecnicaCount(this.vo, this.material);
	}

	@Override
	public List recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return comprasFacade.listarDescricaoTecnica(firstResult, maxResult, orderProperty, asc, this.vo, this.material);
	}
	
	public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriaisAtivosOrderByCodigo(objPesquisa),listarMateriaisCount(objPesquisa));
	}
	
	public Long listarMateriaisCount(String param)	{
		return this.comprasFacade.listarScoMatriaisAtivosCount(param);
	}
	
	
	public ScoDescricaoTecnicaPadrao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoDescricaoTecnicaPadrao selecionado) {
		this.selecionado = selecionado;
	}

	
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public DynamicDataModel<ScoDescricaoTecnicaPadrao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoDescricaoTecnicaPadrao> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoDescricaoTecnicaPadraoVO getVo() {
		return vo;
	}

	public void setVo(ScoDescricaoTecnicaPadraoVO vo) {
		this.vo = vo;
	}
	
	
	
}