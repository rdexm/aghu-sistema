package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterSalaExecutoraExamePaginatorController  extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -3021175000880071171L;

	private static final String MANTER_SALA_EXECUTORA_EXAME = "manterSalaExecutoraExame";
	
	private AelSalasExecutorasExames salaExecutoraFiltro = new AelSalasExecutorasExames();
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@Inject @Paginator
	private DynamicDataModel<AelSalasExecutorasExames> dataModel;
	
	private AelSalasExecutorasExames selecionado;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void limparPesquisa(){
		this.setSalaExecutoraFiltro(new AelSalasExecutorasExames());
		dataModel.limparPesquisa();
	}
	
	@Override
	public Long recuperarCount() {
		return this.examesFacade.pesquisaSalasExecutorasExamesCount(this.salaExecutoraFiltro);
	}

	@Override
	public List<AelSalasExecutorasExames> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.examesFacade.pesquisaSalasExecutorasExames(firstResult, maxResult, orderProperty, asc, this.salaExecutoraFiltro);
	}
	
	public void pesquisar(){
		dataModel.reiniciarPaginator();
	}

	public String inserir(){
		return MANTER_SALA_EXECUTORA_EXAME;
	}
	
	public String editar(){
		return MANTER_SALA_EXECUTORA_EXAME;
	}
	
	public void excluir(){
		try {
			this.cadastrosApoioExamesFacade.removerSalaExecutoraExame(selecionado.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MSG_SALA_EXECUTORA_REMOVIDA");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Pesquisa as unidades executoras
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutoras(String parametro) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
	}

	public void setSalaExecutoraFiltro(AelSalasExecutorasExames salaExecutoraFiltro) {
		this.salaExecutoraFiltro = salaExecutoraFiltro;
	}

	public AelSalasExecutorasExames getSalaExecutoraFiltro() {
		return salaExecutoraFiltro;
	}

	public DynamicDataModel<AelSalasExecutorasExames> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelSalasExecutorasExames> dataModel) {
		this.dataModel = dataModel;
	}

	public AelSalasExecutorasExames getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelSalasExecutorasExames selecionado) {
		this.selecionado = selecionado;
	}
}
