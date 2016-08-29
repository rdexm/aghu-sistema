package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoMaterial;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class MaterialFarmaciaPaginatorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = -8047572730037310010L;
	
	private static final String PAGE_MATERIAL_CRUD= "materialCRUD";
	
	private ScoMaterial material = new ScoMaterial();
	private ScoMaterial materialSelecionado;
	private DominioSituacao situacao;
	private Boolean exibirBotaoNovo = false;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private MaterialFarmaciaController materialFarmaciaController;
	
	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject @Paginator
	private DynamicDataModel<ScoMaterial> dataModel;	
	
	@PostConstruct
	public void init(){
		begin(conversation);
		this.dataModel.setUserEditPermission(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterMaterialFarmacia", "alterar"));
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		deveAparecerBotaoNovo();
		material.setIndSituacao(situacao);
	}
	
	/**

	 * Método que verifica se o botão contrato deve aparecer na tela de

	 * internação.

	 * 

	 * @return

	 */

	public boolean deveAparecerBotaoNovo(){
		boolean retorno = false;
		try{
			AghuParametrosEnum parametroEnum = AghuParametrosEnum.P_AGHU_DEVE_APARECER_BOTAO_NOVO_MATERIAL;
			AghParametros parametroAparecerBotaoNovo = this.parametroFacade.buscarAghParametro(parametroEnum);

			if ("S".equalsIgnoreCase(parametroAparecerBotaoNovo.getVlrTexto())){
					setExibirBotaoNovo(true);
					retorno = true;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return retorno;
	}
	

	@Override
	public List<ScoMaterial> recuperarListaPaginada(Integer firstResult,
						Integer maxResult, String orderProperty, boolean asc) {
		return comprasFacade.consultarMaterialFarmacia(
				firstResult, maxResult, ScoMaterial.Fields.NOME.toString(), true, material); 
	}
	
	@Override
	public Long recuperarCount() {
		return comprasFacade.consultarMaterialFarmaciaCount(material);
	}
	
	public String editar() {
		materialFarmaciaController.setCodigoMaterial(materialSelecionado.getCodigo());
		return PAGE_MATERIAL_CRUD;
	}
	
	public String incluir() {
		materialFarmaciaController.setCodigoMaterial(null);
		materialFarmaciaController.setEntidade(new ScoMaterial());
		return PAGE_MATERIAL_CRUD;
	}
	
	public void limparPesquisa() {
		this.setMaterial(new ScoMaterial());
		this.setSituacao(null);
		dataModel.limparPesquisa();
		deveAparecerBotaoNovo();
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<ScoMaterial> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoMaterial> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoMaterial getMaterialSelecionado() {
		return materialSelecionado;
	}

	public void setMaterialSelecionado(ScoMaterial materialSelecionado) {
		this.materialSelecionado = materialSelecionado;
	}
	
	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

}
