package br.gov.mec.aghu.exames.questionario.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestionariosConvUnid;
import br.gov.mec.aghu.model.AelQuestionariosConvUnidId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class AssociarRequisitanteQuestionarioController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8370886743261164613L;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IQuestionarioExamesFacade questionarioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private String seqQuestionario;
	
	private AghUnidadesFuncionais unidade;
	
	private FatConvenioSaude convenio;
	
	private AelQuestionarios questionario;
	
	private List<AelQuestionariosConvUnid> listAelQuestionariosConvUnid;
	
	private AelQuestionariosConvUnid itemExclusao;

	@Inject @Paginator
	private DynamicDataModel<AelQuestionariosConvUnid> dataModel;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	public void carregaDadosPagina(){
		
		if(StringUtils.isNotBlank(seqQuestionario)) {
			
			setQuestionario(questionarioFacade.obterQuestionarioPorId(Integer.valueOf(seqQuestionario)));
			
//			for(AelQuestionariosConvUnid qcu : questionario.getAelQuestionariosConvUnids()){
//				// Necessario pois ORM esta FetchType.LAZY
//				if(qcu.getConvenioSaude() != null) {
//					faturamentoFacade.obterConvenioSaude(qcu.getConvenioSaude().getCodigo());
//				}
//				if(qcu.getUnidadeFuncional() != null) {
//					aghuFacade.obterUnidadeFuncional(qcu.getUnidadeFuncional().getSeq());
//				}	
//			}
			dataModel.reiniciarPaginator();
		}
		
	}
	
	// Pesquisa sugestionbox Unidade
	public List<AghUnidadesFuncionais> listarUnidades(Object objPesquisa) {
		return aghuFacade.obterUnidadesFuncionais((String) objPesquisa);
	}
	
	public Integer listarUnidadesCount(Object objPesquisa) {
		return aghuFacade.obterUnidadesFuncionais((String) objPesquisa).size();
	}
	
	// Pesquisa sugestionbox Convenio
	public List<FatConvenioSaude> listarConvenios(Object objPesquisa) {
		return faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricaoAtivos((String) objPesquisa);
	}
	
	public Integer listarConveniosCount(Object objPesquisa) {
		return faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricaoAtivos((String) objPesquisa).size();
	}
	
	@Override
	public Long recuperarCount() {
		if(getQuestionario() != null && listAelQuestionariosConvUnid != null){
			return Long.valueOf(listAelQuestionariosConvUnid.size());
		}
		return 0l;
	}

	@Override
	public List<AelQuestionariosConvUnid> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		this.listAelQuestionariosConvUnid = questionarioFacade.buscarAelQuestionariosConvUnidPorQuestionario(getQuestionario().getSeq());

		//if(getQuestionario() != null && getQuestionario().getAelQuestionariosConvUnids() != null){
		if (getQuestionario() != null && listAelQuestionariosConvUnid != null) {
			
			
		   //if (getQuestionario().getAelQuestionariosConvUnids().size() > 10 && firstResult < getQuestionario().getAelQuestionariosConvUnids().size()) { 
		   if (listAelQuestionariosConvUnid.size() > 10 && firstResult < listAelQuestionariosConvUnid.size()) {
			    
			   int toIndex = firstResult + maxResults; 
			  
			    if (toIndex > listAelQuestionariosConvUnid.size()) {
			    	toIndex = listAelQuestionariosConvUnid.size();
			    }
			    
			    return listAelQuestionariosConvUnid.subList(firstResult, toIndex);
			    //return new ArrayList<AelQuestionariosConvUnid>(listAelQuestionariosConvUnid).subList(firstResult, toIndex);
		   }
		   
		   //return new ArrayList<AelQuestionariosConvUnid>(listAelQuestionariosConvUnid);
		   return listAelQuestionariosConvUnid;
		}
		
		return new ArrayList<AelQuestionariosConvUnid>();
	}
	
	public void gravar() {
		try {
			
			questionarioFacade.gravarRequisitante(criaAssociacao());
			
			// Limpa campos tela
			setUnidade(null);
			setConvenio(null);
			
			dataModel.reiniciarPaginator();
			
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ASSOCIACAO_REQUISITANTE_QUESTIONARIO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private AelQuestionariosConvUnid criaAssociacao() {
		
		int seqp = questionarioFacade.obterProximoSequencialAelQuestionariosConvUnid();
		
		AelQuestionariosConvUnid qcu = new AelQuestionariosConvUnid();
		AelQuestionariosConvUnidId qcuId = new AelQuestionariosConvUnidId();
		qcuId.setSeqp(seqp);
		qcuId.setQtnSeq(questionario.getSeq());
		qcu.setId(qcuId);
		qcu.setConvenioSaude(getConvenio());
		qcu.setUnidadeFuncional(getUnidade());
		qcu.setAelQuestionarios(questionario);
		questionario.getAelQuestionariosConvUnids().add(qcu);
		
		return qcu;
	}
	
	public void selecionaItemExclusao(AelQuestionariosConvUnid qcu) {
		
		setItemExclusao(questionarioFacade.obterPorChavePrimaria(qcu.getId()));
		
	}

	public void excluir() {
		try {
			
			getQuestionario().getAelQuestionariosConvUnids().remove(getItemExclusao());
			examesFacade.remover(itemExclusao.getId());
			questionarioFacade.atualizarQuestionario(getQuestionario());
			dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_REQUISITANTE_QUESTIONARIO");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean isDesabilitaBotaoGravar(){
		
		boolean temUnidade = getUnidade() != null && getUnidade().getSeq() != null;
		
		boolean temConvenio = getConvenio() != null && getConvenio().getCodigo() != null;
		
		if(temUnidade && temConvenio) {
			return Boolean.TRUE;
		}
		else if(temUnidade || temConvenio) {
			return Boolean.FALSE;
		}
		else {
			return Boolean.TRUE;
		}
		
	}
	
	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}

	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}

	public FatConvenioSaude getConvenio() {
		return convenio;
	}

	public void setConvenio(FatConvenioSaude convenio) {
		this.convenio = convenio;
	}

	public AelQuestionarios getQuestionario() {
		return questionario;
	}

	public void setQuestionario(AelQuestionarios questionario) {
		this.questionario = questionario;
	}

	public AelQuestionariosConvUnid getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(AelQuestionariosConvUnid itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public String getSeqQuestionario() {
		return seqQuestionario;
	}

	public void setSeqQuestionario(String seqQuestionario) {
		this.seqQuestionario = seqQuestionario;
	}
 


	public DynamicDataModel<AelQuestionariosConvUnid> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelQuestionariosConvUnid> dataModel) {
	 this.dataModel = dataModel;
	}
}