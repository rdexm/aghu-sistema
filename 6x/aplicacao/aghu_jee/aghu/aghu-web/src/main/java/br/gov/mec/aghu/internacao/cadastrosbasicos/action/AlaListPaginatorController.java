package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class AlaListPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 2704840209994505783L;
	
	private final String PAGE_CADASTRAR_ALA = "alaCRUD";

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private AghAla alaSelecionada;	
	private AghAla ala;

	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;	
	
	
	@PostConstruct
	public void init() {
		begin(conversation);
		ala = new AghAla();
	}
	
	@Override
	public List<AghAla> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosBasicosInternacaoFacade.pesquisaAlaList(
				firstResult, maxResult, orderProperty, asc,
				getAla().getCodigo(), getAla().getDescricao()
		);
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosInternacaoFacade.pesquisaAlaListCount(
				getAla().getCodigo(), getAla().getDescricao()
		);
	}
	
	
	/**
	 * Método responsável para ação do botão 'Pesquisar'.
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();		
	}
	
	/**
	 * Método que limpa os campos da tela de pesquisa.
	 */
	public void limpar() {
		this.setAla(new AghAla());
		this.dataModel.limparPesquisa();	
	}
	
	/**
	 * Método que realiza a ação do botão excluir na tela de 
	 * Pesquisa de Manter Parametro de Sistema.
	 * 
	 */
	public void excluir() {
		try {
			this.cadastrosBasicosInternacaoFacade.excluirAghAla(this.getAlaSelecionada().getCodigo());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_ALA", this.getAlaSelecionada().getDescricao());
			this.setAlaSelecionada(null);
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String iniciarInclusao() {
		this.setAlaSelecionada(null);
		return PAGE_CADASTRAR_ALA;
	}
	
	public String editar() {
		return PAGE_CADASTRAR_ALA;
	}
	
	
	public AghAla getAlaSelecionada() {
		return alaSelecionada;
	}

	public void setAlaSelecionada(AghAla alaSelecionada) {
		this.alaSelecionada = alaSelecionada;
	}
	
	public AghAla getAla() {
		return ala;
	}

	public void setAla(AghAla ala) {
		this.ala = ala;
	}
	
	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}
	
}
