package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.agendamento.business.IBlocoCirurgicoAgendamentoFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcDescricaoPadrao;
import br.gov.mec.aghu.model.MbcDescricaoPadraoId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class DescricaoPadraoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcDescricaoPadrao> dataModel;

	private static final String DESCRICAO_PADRAO_CRUD = "descricaoPadraoCRUD";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -78678869136973541L;

	@EJB
	private IBlocoCirurgicoAgendamentoFacade blocoCirurgicoAgendamentoFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	@Inject
	private DescricaoPadraoCRUDController descricaoPadraoCRUDController;
	
	private MbcDescricaoPadraoId id = null;
	
	private MbcDescricaoPadrao descricaoPadrao;
	
	private final Integer maxRegitrosEsp = 100; 
	
	private final Integer maxRegitrosProc = 100;
	
	private boolean  cameFromEdit = false;
	
	

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 

	 

		if(cameFromEdit){
			pesquisar();
		}else{	
			if (!this.dataModel.getPesquisaAtiva()) {
				this.limparPesquisa();
			}
		}
	
	}
	

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		this.descricaoPadrao = new MbcDescricaoPadrao();
		this.descricaoPadrao.setTitulo(null);
		this.descricaoPadrao.setAghEspecialidades(null);
		this.descricaoPadrao.setMbcProcedimentoCirurgicos(null);
		this.dataModel.setPesquisaAtiva(false);
		this.setId(null);
	}

	
	
	//Metodo para Suggestion Box de especialidades
	public List<AghEspecialidades> pesquisarEspecialidades(String objPesquisa){
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadeAtivaPorNomeOuSigla((String)objPesquisa, this.maxRegitrosEsp),pesquisarEspecialidadesCount(objPesquisa));
	}
	
	public Integer pesquisarEspecialidadesCount(String objPesquisa){
		return this.aghuFacade.pesquisarEspecialidadeAtivaPorNomeOuSiglaCount((String)objPesquisa);
	}
	
	//Metodo para Suggestion Box de Procedimentos cirurgicos
	public List<MbcProcedimentoCirurgicos> obterProcedimentoCirurgicos(String objPesquisa){
		return this.returnSGWithCount(this.blocoCirurgicoFacade.listarProcedimentoCirurgicos(objPesquisa, this.maxRegitrosProc),obterProcedimentoCirurgicosCount(objPesquisa));
	}
	
	public Long obterProcedimentoCirurgicosCount(String objPesquisa){
		return this.blocoCirurgicoFacade.listarProcedimentoCirurgicosCount(objPesquisa);
	}
	
	public String iniciarInclusao() {
		return DESCRICAO_PADRAO_CRUD;
	}
	
	public String redirecionarCrud(Integer seqp, Short espSeq) {
		try {
			// tenta buscar o registro pra ver se esta na base, senão estiver o usuário é notificado
			blocoCirurgicoAgendamentoFacade.obterPorChavePrimaria(new MbcDescricaoPadraoId(espSeq, seqp));
			descricaoPadraoCRUDController.setSeqp(seqp);
			descricaoPadraoCRUDController.setEspSeq(espSeq);
			return DESCRICAO_PADRAO_CRUD;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.pesquisar();
		}
		return null;
	}
	
	@Override
	public Long recuperarCount() {
		Short especialidadeId = null;
		Integer procedimentoId = null;
		if(descricaoPadrao.getAghEspecialidades()!= null){
			especialidadeId = descricaoPadrao.getAghEspecialidades().getSeq();
		}
		if(descricaoPadrao.getMbcProcedimentoCirurgicos()!=null){
			procedimentoId = descricaoPadrao.getMbcProcedimentoCirurgicos().getSeq();
		}
		
		return blocoCirurgicoAgendamentoFacade.contarDescricaoPadrao(especialidadeId , procedimentoId, descricaoPadrao.getTitulo());
	}

	
	@Override
	public List<MbcDescricaoPadrao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short especialidadeId = null;
		Integer procedimentoId = null;
		
		if(descricaoPadrao.getAghEspecialidades()!= null){
			especialidadeId = descricaoPadrao.getAghEspecialidades().getSeq();
		}
		if(descricaoPadrao.getMbcProcedimentoCirurgicos()!=null){
			procedimentoId = descricaoPadrao.getMbcProcedimentoCirurgicos().getSeq();
		}
		
		return blocoCirurgicoAgendamentoFacade.buscarDescricaoPadrao(firstResult, maxResult, orderProperty, asc, especialidadeId , procedimentoId, descricaoPadrao.getTitulo());
	}
	
	
	
	
	public void excluir() {
		try {
			MbcDescricaoPadrao descricaoPadraoDelete = blocoCirurgicoAgendamentoFacade.obterDescricaoPadraoById(this.getId().getSeqp(), this.getId().getEspSeq());
			this.blocoCirurgicoAgendamentoFacade.excluirDescricaoPadrao(id);
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_DESCRICAO_PADRAO", descricaoPadraoDelete.getTitulo());
			this.pesquisar();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public MbcDescricaoPadrao getDescricaoPadrao() {
		return descricaoPadrao;
	}

	public void setDescricaoPadrao(MbcDescricaoPadrao descricaoPadrao) {
		this.descricaoPadrao = descricaoPadrao;
	}

	public MbcDescricaoPadraoId getId() {
		return id;
	}

	public void setId(MbcDescricaoPadraoId id) {
		this.id = id;
	}

	public boolean isCameFromEdit() {
		return cameFromEdit;
	}

	public void setCameFromEdit(boolean cameFromEdit) {
		this.cameFromEdit = cameFromEdit;
	}
 


	public DynamicDataModel<MbcDescricaoPadrao> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcDescricaoPadrao> dataModel) {
	 this.dataModel = dataModel;
	}
}