package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class ModalidadePacPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 6015596058345550617L;

	private static final String MODALIDADE_PAC_CRUD = "modalidadePacCRUD";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	private ScoModalidadeLicitacao modalidadePac = new ScoModalidadeLicitacao();
	private DominioSimNao geraLicitacao;
	
	@Inject @Paginator
	private DynamicDataModel<ScoModalidadeLicitacao> dataModel;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public Long recuperarCount() {
		
		//Verifica o campo geraLicitacao para converter de DominioSimNao para Booleano e popular o objeto modalidadePac
		if (this.geraLicitacao != null) {
			this.getModalidadePac().setLicitacao(this.geraLicitacao == DominioSimNao.S ?true:false);
		}
		else { this.getModalidadePac().setLicitacao(null);
		}

		return comprasCadastrosBasicosFacade.listarModalidadeLicitacaoCount(modalidadePac);
	}

	@Override
	public List<ScoModalidadeLicitacao> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return comprasCadastrosBasicosFacade.listarModalidadeLicitacao(firstResult, maxResult,orderProperty, asc, modalidadePac);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.setModalidadePac(new ScoModalidadeLicitacao());
		this.getModalidadePac().setSituacao(DominioSituacao.A);
		this.geraLicitacao = null;
	}

	public String inserir() {
		return MODALIDADE_PAC_CRUD;
	}

	public String editar() {
		return MODALIDADE_PAC_CRUD;
	}

	public String visualizar() {
		return MODALIDADE_PAC_CRUD;
	}

	public ScoModalidadeLicitacao getModalidadePac() {
		return modalidadePac;
	}

	public void setModalidadePac(ScoModalidadeLicitacao modalidadePac) {
		this.modalidadePac = modalidadePac;
	}

	public DominioSimNao getGeraLicitacao() {
		return geraLicitacao;
	}

	public void setGeraLicitacao(DominioSimNao geraLicitacao) {
		this.geraLicitacao = geraLicitacao;
	}

	public DynamicDataModel<ScoModalidadeLicitacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoModalidadeLicitacao> dataModel) {
		this.dataModel = dataModel;
	}
}