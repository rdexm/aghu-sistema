package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipDistritoSanitarios;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class DistritoSanitarioPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5109970161306737286L;
	
	private static final String REDIRECIONA_MANTER_DISTRITO_SANITARIO = "distritoSanitarioCRUD";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	private AipDistritoSanitarios distritoSanitario = new AipDistritoSanitarios();
	
	private AipDistritoSanitarios distritoSanitarioSelecionado;
	
	/**
	 * Lista de cidades do distrito sanitário.
	 */
	private List<AipCidades> cidades = new ArrayList<AipCidades>();	
	
	private boolean exibirBotaoNovo = false;
	
	@Inject @Paginator
	private DynamicDataModel<AipCidades> dataModel;
	
	private static final Comparator<AipCidades> COMPARATOR_CIDADES = new Comparator<AipCidades>() {

		@Override
		public int compare(AipCidades o1, AipCidades o2) {
			return o1.getNome().toUpperCase().compareTo(
					o2.getNome().toUpperCase());
		}

	};	
	
	@PostConstruct
	public void init(){
		this.begin(conversation);
	}	
	
	//@Restrict("#{s:hasPermission('distritoSanitario','pesquisar')}")
	public void pesquisar(){
		reiniciarPaginator();
		exibirBotaoNovo = true;
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosPacienteFacade
				.obterDistritoSanitarioCount(distritoSanitario.getCodigo(),
						distritoSanitario.getDescricao());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AipDistritoSanitarios> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return cadastrosBasicosPacienteFacade.pesquisarDistritoSanitario(
				firstResult, maxResult, distritoSanitario.getCodigo(),
				distritoSanitario.getDescricao());
	}
	
	public void limparPesquisa() {
		distritoSanitario = new AipDistritoSanitarios();
		distritoSanitarioSelecionado = null;
		exibirBotaoNovo = false;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public void exibeCidades(AipDistritoSanitarios distritoSanitario) {
		this.cidades = new ArrayList<AipCidades>(distritoSanitario.getCidades());
		Collections.sort(this.cidades, COMPARATOR_CIDADES);
	}
	
	public void excluir(){
		reiniciarPaginator();
		
		try{
			AipDistritoSanitarios distSanit = distritoSanitarioSelecionado;
			if (distSanit != null && distSanit.getCidades().isEmpty()){
				this.cadastrosBasicosPacienteFacade.removerDistritoSanitario(distSanit);
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_DISTRITO_SANITARIO", distSanit.getDescricao());
			}else{
				apresentarMsgNegocio(Severity.ERROR, "ERRO_EXCLUIR_DISTRITO_COM_CIDADE");
			}
		}
		catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}		
	}
	
	public String redirecionaIncluirDistritoSanitario() {
		return REDIRECIONA_MANTER_DISTRITO_SANITARIO;
	}
	
	public String editar() {
		return REDIRECIONA_MANTER_DISTRITO_SANITARIO;
	}
	
	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();		
	}	
	
	//SET's e GET's
	
	public AipDistritoSanitarios getDistritoSanitario() {
		return distritoSanitario;
	}

	public void setDistritoSanitario(AipDistritoSanitarios distritoSanitario) {
		this.distritoSanitario = distritoSanitario;
	}

	public void setCidades(List<AipCidades> cidades) {
		this.cidades = cidades;
	}

	public List<AipCidades> getCidades() {
		return cidades;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(	boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public DynamicDataModel<AipCidades> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipCidades> dataModel) {
		this.dataModel = dataModel;
	}

	public AipDistritoSanitarios getDistritoSanitarioSelecionado() {
		return distritoSanitarioSelecionado;
	}

	public void setDistritoSanitarioSelecionado(
			AipDistritoSanitarios distritoSanitarioSelecionado) {
		this.distritoSanitarioSelecionado = distritoSanitarioSelecionado;
	}
	
}