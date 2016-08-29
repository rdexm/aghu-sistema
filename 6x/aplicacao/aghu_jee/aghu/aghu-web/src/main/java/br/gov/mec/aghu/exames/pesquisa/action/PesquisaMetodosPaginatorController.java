package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelMetodo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaMetodosPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 7125574343156680508L;

	private static final String MANTER_METODO_CRUD = "manterMetodoCRUD";

	//Campos filtro
	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;

	@EJB
	private IExamesFacade examesFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AelMetodo> dataModel;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		codigo = null;
		descricao = null;
		situacao = null;
		dataModel.limparPesquisa();
	}
	
	@Override
	public List<AelMetodo> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return examesFacade.pesquisarMetodo(firstResult, maxResult, AelMetodo.Fields.SEQ.toString(), true, 
											new AelMetodo(codigo, descricao, situacao, null,null));
	}
	
	@Override
	public Long recuperarCount() {
		return examesFacade.pesquisarMetodoCount(new AelMetodo(codigo, descricao, situacao, null, null));
	}
	
	public String inserir() {
		return MANTER_METODO_CRUD;
	}

	/**
	 * Método que realiza a ação do botão atualizar situação
	 */
	public void editar(AelMetodo metodo) {
		try {
			examesFacade.ativarInativarAleMetodo(metodo);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_METODO", metodo.getDescricao());
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public boolean isActive(AelMetodo metodo) {
		return (metodo.getSituacao() == DominioSituacao.A);
	}
	
	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<AelMetodo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelMetodo> dataModel) {
		this.dataModel = dataModel;
	}
}