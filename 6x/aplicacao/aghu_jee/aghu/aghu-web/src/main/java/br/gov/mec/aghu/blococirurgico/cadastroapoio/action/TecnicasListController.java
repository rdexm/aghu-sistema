package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * #24722 
 */ 

public class TecnicasListController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<PdtTecnica> dataModel;

	private static final long serialVersionUID = 8377124220277489320L;

	private static final String TECNICAS_CRUD = "tecnicasProcedimentoCRUD";
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;

	@Inject
	private TecnicasProcedimentoCRUDController tecnicasProcedimentoCRUDController;
	
	private Integer codigo;
	private String descricao;
	private DominioSituacao situacao;
	private Boolean exibirBotaoNovo;		
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoNovo = true;
	}
	
	public String iniciarInclusao () {
		return TECNICAS_CRUD;
	}
	
	public String editar(PdtTecnica tecnica) {
		this.tecnicasProcedimentoCRUDController.setCodigo(tecnica.getSeq());
		return TECNICAS_CRUD;
	}
	
	public void limpar(){		
		codigo = null;
		descricao = null;
		situacao = null;
		exibirBotaoNovo = false;
		this.dataModel.setPesquisaAtiva(false);
	}

	@Override
	public List<PdtTecnica> recuperarListaPaginada(Integer firstResult,Integer maxResult,String orderProperty,boolean asc) {
		return blocoCirurgicoProcDiagTerapFacade.listarPdtTecnicaPorSeqDescricaoSituacao(codigo, descricao, situacao, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return blocoCirurgicoProcDiagTerapFacade.listarPdtTecnicaPorSeqDescricaoSituacaoCount(codigo, descricao, situacao);
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

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
	 


	public DynamicDataModel<PdtTecnica> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<PdtTecnica> dataModel) {
	 this.dataModel = dataModel;
	}
}
