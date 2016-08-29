package br.gov.mec.aghu.compras.parecer.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.parecer.cadastrosbasicos.business.IParecerCadastrosBasicosFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoAgrupamentoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PastaParecerPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = -7515958848282834376L;

	private static final String PASTA_PARECER_CRUD = "pastaParecerCRUD";

	@EJB
	private IParecerCadastrosBasicosFacade parecerCadastrosBasicosFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@Inject
	private SecurityController securityController;	
	
	private ScoOrigemParecerTecnico pasta = new ScoOrigemParecerTecnico();
	
	@Inject @Paginator
	private DynamicDataModel<ScoOrigemParecerTecnico> dataModel;
	
	private ScoOrigemParecerTecnico selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarParecerTecnico,gravar") ||  securityController.usuarioTemPermissao("cadastrarApoioParecerTecnico,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
	}

	@Override
	public Long recuperarCount() {
		return parecerCadastrosBasicosFacade.listarOrigemParecerCount(pasta);
	}

	@Override
	public List<ScoOrigemParecerTecnico> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return parecerCadastrosBasicosFacade.listarOrigemParecer(firstResult, maxResult, orderProperty, asc, pasta);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.setPasta(new ScoOrigemParecerTecnico());
		dataModel.limparPesquisa();
	}
	
	public List<ScoAgrupamentoMaterial> pesquisarAgrupamentoPorCodigoDescricao(String parametro) {
		return this.parecerCadastrosBasicosFacade.pesquisarAgrupamentoMaterialPorCodigoOuDescricao(parametro, false);
	}
	
	public List<FccCentroCustos> pesquisarCentroCustos(String parametro) {
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao((String) parametro),pesquisarCentroCustosCount(parametro));
	}
	
	public Long pesquisarCentroCustosCount(String parametro) {
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount((String) parametro);
	}

	public String inserir() {
		return PASTA_PARECER_CRUD;
	}

	public String editar() {
		return PASTA_PARECER_CRUD;
	}

	public String visualizar() {
		return PASTA_PARECER_CRUD;
	}
	
	public void excluir() {
		try{
			parecerCadastrosBasicosFacade.excluirOrigemParecer(selecionado.getCodigo());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PASTA_PARECER_DELETE_SUCESSO");
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public ScoOrigemParecerTecnico getPasta() {
		return pasta;
	}

	public void setPasta(ScoOrigemParecerTecnico pasta) {
		this.pasta = pasta;
	}

	public DynamicDataModel<ScoOrigemParecerTecnico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoOrigemParecerTecnico> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoOrigemParecerTecnico getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoOrigemParecerTecnico selecionado) {
		this.selecionado = selecionado;
	}
}