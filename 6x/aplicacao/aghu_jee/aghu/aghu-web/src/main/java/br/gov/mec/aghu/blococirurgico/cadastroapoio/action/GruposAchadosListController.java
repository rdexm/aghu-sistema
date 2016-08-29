package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.model.PdtGrupo;
import br.gov.mec.aghu.model.PdtGrupoId;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class GruposAchadosListController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<PdtGrupo> dataModel;

	private static final long serialVersionUID = 8377124220277489320L;

	private static final String GRUPO_CRUD = "gruposAchadosCRUD";
	private static final String PDT_LIST = "gruposAchadosCRUD";
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@Inject
	private GruposAchadosCRUDController gruposAchadosCRUDController;
	
	private Integer dptSeq;
	private PdtGrupo grupo;	
	private PdtProcDiagTerap procDiagTerap; 	
		
	
	public void inicio() {
	 

	 

		if(!this.dataModel.getPesquisaAtiva()){
			grupo = new PdtGrupo();
			grupo.setId(new PdtGrupoId());			

			if (dptSeq != null) {
				procDiagTerap = blocoCirurgicoProcDiagTerapFacade.obterPdtProcDiagTerap(dptSeq);				
			}			
		}			
	
	}
	

	public String iniciarInclusao() {
		return GRUPO_CRUD;
	}
	
	public String editar(Integer dptSeq, Short seqp) {
		gruposAchadosCRUDController.setSeqp(seqp);
		gruposAchadosCRUDController.setDptSeq(dptSeq);
		return GRUPO_CRUD;
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();		
	}
	
	public void limpar(){		
		grupo = new PdtGrupo();
		grupo.setId(new PdtGrupoId());	
		setProcDiagTerap(null);			
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String cancelar() {	
		this.dataModel.setPesquisaAtiva(false);
		return PDT_LIST;
	}
	
	public List<PdtProcDiagTerap> pesquisarPdtProcDiagTerap(final String strPesquisa) {		
		return this.returnSGWithCount(this.blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcDiagTerap((String) strPesquisa),pesquisarPdtProcDiagTerapCount(strPesquisa));
	}

	public Long pesquisarPdtProcDiagTerapCount(final String strPesquisa) {
		return this.blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcDiagTerapCount((String) strPesquisa);
	}
	
	@Override
	public List<PdtGrupo> recuperarListaPaginada(Integer firstResult,Integer maxResult,String orderProperty,boolean asc) {
		return blocoCirurgicoProcDiagTerapFacade.pesquisarPdtGrupoPorIdDescricaoSituacao(
				procDiagTerap == null ? null : procDiagTerap.getSeq(),
				 grupo.getId().getSeqp(), grupo.getDescricao(), grupo.getIndSituacao(), firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return blocoCirurgicoProcDiagTerapFacade.pesquisarPdtGrupoPorIdDescricaoSituacaoCount(
				procDiagTerap == null ? null : procDiagTerap.getSeq(),
				grupo.getId().getSeqp(), grupo.getDescricao(), grupo.getIndSituacao());
	}		
	
	public String editarSeqDescricao(Integer seq, String descricao, Boolean controle) {
				
		if (controle && descricao.length() > 60){
			descricao = descricao.substring(0,60) + "...";			
		}
				
		return seq + " - " + descricao;
	}
	

	// Getters e Setters

	public PdtGrupo getGrupo() {
		return grupo;
	}

	public void setGrupo(PdtGrupo grupo) {
		this.grupo = grupo;
	}

	public PdtProcDiagTerap getProcDiagTerap() {
		return procDiagTerap;
	}

	public void setProcDiagTerap(PdtProcDiagTerap procDiagTerap) {
		this.procDiagTerap = procDiagTerap;
	}

	public Integer getDptSeq() {
		return dptSeq;
	}

	public void setDptSeq(Integer dptSeq) {
		this.dptSeq = dptSeq;
	}
	 


	public DynamicDataModel<PdtGrupo> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<PdtGrupo> dataModel) {
	 this.dataModel = dataModel;
	}
}
