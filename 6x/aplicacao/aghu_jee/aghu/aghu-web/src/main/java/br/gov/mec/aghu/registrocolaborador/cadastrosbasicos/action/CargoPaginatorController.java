package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapCargos;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class CargoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -1008974254840576063L;

	private static final String CADASTRAR_CARGO = "cadastrarCargo";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private String codigoPesquisaCargo;

	private String descricaoPesquisaCargo;

	private DominioSituacao situacaoPesquisaCargo;
	
	@Inject @Paginator
	private DynamicDataModel<RapCargos> dataModel;
	
	private RapCargos selecionado;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosFacade.pesquisarCargoCount(codigoPesquisaCargo, descricaoPesquisaCargo, situacaoPesquisaCargo);
	}

	@Override
	public List<RapCargos> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return cadastrosBasicosFacade.pesquisarCargo(codigoPesquisaCargo, descricaoPesquisaCargo, situacaoPesquisaCargo, firstResult, maxResults, RapCargos.Fields.CODIGO.toString(), true);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public String editar(){
		return CADASTRAR_CARGO;
	}

	public String inserir(){
		return CADASTRAR_CARGO;
	}

	public void limpar() {
		dataModel.limparPesquisa();
		this.codigoPesquisaCargo = null;
		this.descricaoPesquisaCargo = null;
		this.situacaoPesquisaCargo = null;
	}

	public void excluir() {
		try {
			this.cadastrosBasicosFacade.removerCargo(selecionado.getCodigo());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_CARGO", selecionado.getCodigo());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String getCodigoPesquisaCargo() {
		return codigoPesquisaCargo;
	}

	public void setCodigoPesquisaCargo(String codigoPesquisaCargo) {
		this.codigoPesquisaCargo = codigoPesquisaCargo;
	}

	public String getDescricaoPesquisaCargo() {
		return descricaoPesquisaCargo;
	}

	public void setDescricaoPesquisaCargo(String descricaoPesquisaCargo) {
		this.descricaoPesquisaCargo = descricaoPesquisaCargo;
	}

	public DominioSituacao getSituacaoPesquisaCargo() {
		return situacaoPesquisaCargo;
	}

	public void setSituacaoPesquisaCargo(DominioSituacao situacaoPesquisaCargo) {
		this.situacaoPesquisaCargo = situacaoPesquisaCargo;
	}

	public DynamicDataModel<RapCargos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapCargos> dataModel) {
		this.dataModel = dataModel;
	}

	public RapCargos getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(RapCargos selecionado) {
		this.selecionado = selecionado;
	}
}
