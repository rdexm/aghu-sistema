package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.exames.vo.RegiaoAnatomicaVO;
import br.gov.mec.aghu.model.McoAchado;
import br.gov.mec.aghu.perinatologia.vo.AchadoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class PesquisaAchadoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8594905230600848441L;

	@Inject @Paginator
	private DynamicDataModel<AchadoVO> dataModel;
	private final String PAGE_CADASTRO_ACHADO = "cadastroAchado";

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	private AchadoVO filtro;
	private AchadoVO itemSelecionado;
	private RegiaoAnatomicaVO regiaoAnatomicaVO;
	private Boolean hasPermission;
	private Boolean hasPermissionConsulta;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		iniciar();
	}
	
	public void iniciar() {
		// atualiza permissao para novo, editar, ativar/inativar
		filtro = new AchadoVO();
		verificarPermissoes();
	}
	
	public void verificarPermissoes() {
		hasPermission = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterAchados", "executar");
		hasPermissionConsulta = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "pesquisarAchados", "pesquisar");
		this.dataModel.setUserEditPermission(hasPermission);
	}

	/**
	 * Ações da tela.
	 */
	public List<RegiaoAnatomicaVO> pesquisarRegioesAnatomicas(String param) {
		return  this.returnSGWithCount(emergenciaFacade.pesquisarRegioesAnatomicas((String) param),pesquisarRegioesAnatomicasCount(param));
	}
	
	public Long pesquisarRegioesAnatomicasCount(String param) {
		return emergenciaFacade.pesquisarRegioesAnatomicasCount((String) param);
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limpar() {
		filtro = new AchadoVO();
		regiaoAnatomicaVO = null;		
		itemSelecionado = null;
		this.dataModel.limparPesquisa();
	}

	public String redirecionarCadastro() {
		return PAGE_CADASTRO_ACHADO;
	}

	public void ativarInativar(Integer seq) {
		try {
			McoAchado entity = emergenciaFacade.obterAchadoPorChavePrimaria(seq);
			if (entity != null) {
				emergenciaFacade.ativarInativarAchado(entity);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_SITUACAO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {		
		return emergenciaFacade.pesquisarAchadosCount(prepararFiltro());
	}
	
	public IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	@Override
	public List<AchadoVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		return emergenciaFacade.pesquisarAchados(firstResult, maxResults,
				orderProperty, asc, prepararFiltro());
	}
	
	private AchadoVO prepararFiltro() {
		if(regiaoAnatomicaVO != null) {
			filtro.setDescricaoRan(regiaoAnatomicaVO.getDescricao());
			filtro.setSeqRan(regiaoAnatomicaVO.getSeq());
		}
		return filtro;
	}

	public DynamicDataModel<AchadoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AchadoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public RegiaoAnatomicaVO getRegiaoAnatomicaVO() {
		return regiaoAnatomicaVO;
	}

	public void setRegiaoAnatomicaVO(RegiaoAnatomicaVO regiaoAnatomicaVO) {
		this.regiaoAnatomicaVO = regiaoAnatomicaVO;
	}

	public AchadoVO getFiltro() {
		return filtro;
	}

	public void setFiltro(AchadoVO filtro) {
		this.filtro = filtro;
	}

	public Boolean getHasPermission() {
		return hasPermission;
	}

	public void setHasPermission(Boolean hasPermission) {
		this.hasPermission = hasPermission;
	}

	public AchadoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(AchadoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public Boolean getHasPermissionConsulta() {
		return hasPermissionConsulta;
	}

	public void setHasPermissionConsulta(Boolean hasPermissionConsulta) {
		this.hasPermissionConsulta = hasPermissionConsulta;
	}

}
