package br.gov.mec.aghu.controleinfeccao.action;



import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.model.MciAntimicrobianos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaAntimicrobianosPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -596753050372084741L;

	private static final String PAGINA_MANTER_ANTIMICROBIANOS = "cadastroAntimicrobianos";
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	// filtros
	private MciAntimicrobianos filtros = new MciAntimicrobianos();

	// lista
	@Inject @Paginator
	private DynamicDataModel<MciAntimicrobianos> dataModel;
	
	private MciAntimicrobianos selecionado = new MciAntimicrobianos();
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterAntimicrobiano", "manter");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}


	public void limpar() {	
		filtros = new MciAntimicrobianos();
		dataModel.limparPesquisa();
	}
	
	
	public String novo(){
		return PAGINA_MANTER_ANTIMICROBIANOS;
	}
	
	public String editar(){
		return PAGINA_MANTER_ANTIMICROBIANOS;
	}
	
	public void excluir()  {
		try {
			controleInfeccaoFacade.excluirMciAntimicrobiano(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ANTIMICROBIANO_SUCESSO_EXCLUSAO", selecionado.getDescricao());
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}	
	
	@Override
	public Long recuperarCount() {
		Long count = controleInfeccaoFacade.pesquisarAntimicrobianosPorSeqDescricaoSituacaoCount(filtros.getSeq(), filtros.getDescricao(), 
				filtros.getSituacao());
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public  List<MciAntimicrobianos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return controleInfeccaoFacade.pesquisarAntimicrobianosPorSeqDescricaoSituacao(filtros.getSeq(), filtros.getDescricao(), 
				filtros.getSituacao(), firstResult, maxResult, orderProperty, asc);
	}

	
	public MciAntimicrobianos getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(MciAntimicrobianos selecionado) {
		this.selecionado = selecionado;
	}

	public DynamicDataModel<MciAntimicrobianos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MciAntimicrobianos> dataModel) {
		this.dataModel = dataModel;
	}

	public MciAntimicrobianos getFiltros() {
		return filtros;
	}

	public void setFiltros(MciAntimicrobianos filtros) {
		this.filtros = filtros;
	}

}
