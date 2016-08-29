package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;



public class IdentificarUnidadeExecutoraPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AghUnidadesFuncionais> dataModel;

	//private static final Log LOG = LogFactory.getLog(IdentificarUnidadeExecutoraPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1721766990808605266L;
	
	@EJB
	private IAghuFacade aghuFacade;

	
	//Campos filtro
	private Integer codigo;
	private String descricao;
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		//Limpa filtro
		codigo = null;
		descricao = null;
		
		//Apaga resultados da exibição
		this.dataModel.setPesquisaAtiva(false);
	}
	
	@Override
	public List<AghUnidadesFuncionais> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		AghUnidadesFuncionais elemento = new AghUnidadesFuncionais();
		elemento.setSeq(this.getCodigo() == null ? null : Short.parseShort(this.getCodigo().toString()));
		elemento.setDescricao(this.getDescricao());
		
		return aghuFacade.pesquisarDadosBasicosUnidadesExecutoras(firstResult, maxResult, AghUnidadesFuncionais.Fields.DESCRICAO.toString(), true, elemento);
	}
	
	@Override
	public Long recuperarCount() {
		//Cria objeto com os parâmetros para busca
		AghUnidadesFuncionais elemento = new AghUnidadesFuncionais();
		elemento.setSeq(this.getCodigo() == null ? null : Short.parseShort(this.getCodigo().toString()));
		elemento.setDescricao(this.getDescricao());
		
		return aghuFacade.pesquisarAghUnidadesFuncionaisCount(elemento);
	}
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	} 


	public DynamicDataModel<AghUnidadesFuncionais> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghUnidadesFuncionais> dataModel) {
	 this.dataModel = dataModel;
	}
}