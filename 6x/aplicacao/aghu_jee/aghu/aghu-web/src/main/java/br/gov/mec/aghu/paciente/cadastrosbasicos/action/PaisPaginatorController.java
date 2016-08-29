package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipPaises;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class PaisPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5609277372596617736L;
	
	private static final String REDIRECIONA_MANTER_PAIS = "paisCRUD";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@Inject
	private Conversation conversation;

	private AipPaises pais = new AipPaises();
	
	private boolean exibirBotaoIncluirPais = false;
	
	private AipPaises paisSelecionado;

	@Inject @Paginator
	private DynamicDataModel<AipPaises> dataModel;

	
	@PostConstruct
	public void init(){
		this.begin(conversation);
	}
	
//	@Restrict("#{s:hasPermission('pais','pesquisar')}")
	public void pesquisar(){
		dataModel.reiniciarPaginator();
		exibirBotaoIncluirPais = true;
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosPacienteFacade.obterPaisCount(pais.getSigla(), pais.getNome());
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<AipPaises> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return cadastrosBasicosPacienteFacade.pesquisarPais(firstResult, maxResult, "___".equals(pais.getSigla()) ? "" : pais.getSigla(), pais.getNome());
	}
	
	public void limparCampos() {
		pais.setSigla("");
		pais.setNome("");
		dataModel.limparPesquisa();
		exibirBotaoIncluirPais = false;
	}
	
	public void excluir() {
		try {
			this.cadastrosBasicosPacienteFacade.removerPais(paisSelecionado.getSigla());			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_PAIS", paisSelecionado.getNome());		
			this.paisSelecionado = null;
			dataModel.reiniciarPaginator();
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}
	
	public String redirecionarInclusao(){
		return REDIRECIONA_MANTER_PAIS;
	}
	
	public String editar(){
		return REDIRECIONA_MANTER_PAIS;
	}
	
	public AipPaises getPais() {
		return pais;
	}

	public void setPais(final AipPaises paises) {
		this.pais = paises;
	}

	public boolean isExibirBotaoIncluirPais() {
		return exibirBotaoIncluirPais;
	}

	public void setExibirBotaoIncluirPais(
			final boolean exibirBotaoIncluirPais) {
		this.exibirBotaoIncluirPais = exibirBotaoIncluirPais;
	}

	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();		
	}

	public DynamicDataModel<AipPaises> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipPaises> dataModel) {
		this.dataModel = dataModel;
	}
	
	public AipPaises getPaisSelecionado() {
		return paisSelecionado;
	}

	public void setPaisSelecionado(AipPaises paisSelecionado) {
		this.paisSelecionado = paisSelecionado;
	}	
}