package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.PdtCidPorProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class CidsProcedimentoListController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<PdtCidPorProc> dataModel;

	private static final long serialVersionUID = 8377124220277489320L;
	
	private static final String CIDS_PROC_CRUD = "cidsProcedimentoComplementoCRUD";
	private static final String PDT_LIST = "procedimentoDiagnosticoTerapeuticoList";
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private CidsProcedimentoComplementoCRUDController cidsProcedimentoComplementoCRUDController;
	
	private Boolean exibirBotaoNovo;
	private AghCid cid;
	private PdtProcDiagTerap procedimento;
	private DominioSituacao situacao;
	private Integer dptSeq;
	
	public void inicio() {
		if(dptSeq != null && procedimento == null){
			procedimento = blocoCirurgicoProcDiagTerapFacade.obterPdtProcDiagTerapPorChavePrimaria(dptSeq);		
			pesquisar();
		}		
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoNovo = true;
	}
	
	public String iniciarInclusao() {
		if (this.procedimento != null) {
			cidsProcedimentoComplementoCRUDController.setDptSeq(this.procedimento.getSeq());
			cidsProcedimentoComplementoCRUDController.setCidSeq(null);
		} else {
			cidsProcedimentoComplementoCRUDController.setDptSeq(null);
			cidsProcedimentoComplementoCRUDController.setCidSeq(null);
		}
		return CIDS_PROC_CRUD;
	}

	public String editar(Integer cidSeq, Integer dptSeq) {
		cidsProcedimentoComplementoCRUDController.setCidSeq(cidSeq);
		cidsProcedimentoComplementoCRUDController.setDptSeq(dptSeq);
		return CIDS_PROC_CRUD;
	}
	
	public String voltar() {
		limpar();
		return PDT_LIST;
	}
	
	public void limpar(){
		cid = null;
		procedimento = null;
		situacao = null;
		exibirBotaoNovo = false;
		dataModel.setPesquisaAtiva(false);
	}

	public List<PdtProcDiagTerap> listarProcedimentos(String filtro) {
		return this.returnSGWithCount(this.blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcDiagTerap((String) filtro),listarProcedimentosCount(filtro));
	}
	
	public Long listarProcedimentosCount(String filtro) {
		return this.blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcDiagTerapCount((String) filtro);
	}
	
	public List<AghCid> listarCids (String filtro) {
		String pesquisa = filtro != null ? filtro : null;
		return this.returnSGWithCount(aghuFacade.listarAghCidPorCodigoDescricao(pesquisa,
				null, DominioSituacao.A, false, false, 0,
				100, true, AghCid.Fields.CODIGO), listarCidsCount(filtro));
	}
	
	public Long listarCidsCount(String filtro) {
		String pesquisa = filtro != null ? filtro : null;
		return aghuFacade.listarAghCidPorCodigoDescricaoCount(
				pesquisa, null, DominioSituacao.A, false,
				false);
	}
	
	@Override
	public List<PdtCidPorProc> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return blocoCirurgicoProcDiagTerapFacade.listarPdtCidPorProcPorProcedimentoSituacaoCid(firstResult,maxResult,orderProperty,asc,procedimento,situacao,cid);
	}

	@Override
	public Long recuperarCount() {
		return blocoCirurgicoProcDiagTerapFacade.listarPdtCidPorProcPorProcedimentoSituacaoCidCount(procedimento,situacao,cid);
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public PdtProcDiagTerap getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(PdtProcDiagTerap procedimento) {
		this.procedimento = procedimento;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	public Integer getDptSeq() {
		return dptSeq;
	}
	
	public void setDptSeq(Integer dptSeq) {
		this.dptSeq = dptSeq;
	}

	public DynamicDataModel<PdtCidPorProc> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<PdtCidPorProc> dataModel) {
	 this.dataModel = dataModel;
	}
}
