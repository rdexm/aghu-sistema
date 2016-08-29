package br.gov.mec.aghu.exames.questionario.action;

import java.util.ArrayList;
import java.util.HashSet;
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

public class AssociaRequisitanteQuestionarioController extends ActionController implements ActionPaginator {


	@Inject @Paginator
	private DynamicDataModel<AelQuestionariosConvUnid> dataModel;

	private static final long serialVersionUID = -374090823391361635L;

	private static final String MANTER_QUESTIONARIO_PESQUISA = "exames-manterQuestionarioPesquisa";
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IQuestionarioExamesFacade questionarioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;
	
	private String seqQuestionario;
	
	private AghUnidadesFuncionais unidade;
	
	private FatConvenioSaude convenio;
	
	private AelQuestionarios questionario;
	
	private AelQuestionariosConvUnid selecionado;
	
	private List<AelQuestionariosConvUnid> listAelQuestionariosConvUnid;
	
	private boolean iniciouTela;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar(){
	 

		if(!iniciouTela){
			limpar();
			
			if(StringUtils.isNotBlank(seqQuestionario)) {
				setQuestionario(questionarioFacade.obterQuestionarioPorId(Integer.valueOf(seqQuestionario)));
				
				if(questionario == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return voltar();
				}
				
				dataModel.reiniciarPaginator();
			}
		}
		
		return null;
	
	}
	
	private void limpar() {
		setQuestionario(null);
		setConvenio(null);
		setUnidade(null);
		listAelQuestionariosConvUnid = null;
	}
	
	public String voltar(){
		iniciouTela = false;
		return MANTER_QUESTIONARIO_PESQUISA;
	}
	
	// Pesquisa sugestionbox Unidade
	public List<AghUnidadesFuncionais> listarUnidades(String objPesquisa) {
		return this.returnSGWithCount(aghuFacade.obterUnidadesFuncionais((String) objPesquisa),listarUnidadesCount(objPesquisa));
	}
	
	public Integer listarUnidadesCount(String objPesquisa) {
		return aghuFacade.obterUnidadesFuncionais((String) objPesquisa).size();
	}
	
	// Pesquisa sugestionbox Convenio
	public List<FatConvenioSaude> listarConvenios(String objPesquisa) {
		return this.returnSGWithCount(faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricaoAtivos((String) objPesquisa),listarConveniosCount(objPesquisa));
	}
	
	public Integer listarConveniosCount(String objPesquisa) {
		return faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricaoAtivos((String) objPesquisa).size();
	}
	
	@Override
	public Long recuperarCount() {
		return questionarioFacade.buscarAelQuestionariosConvUnidPorQuestionarioCount(getQuestionario().getSeq());
	}

	@Override
	public List<AelQuestionariosConvUnid>  recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {

		this.listAelQuestionariosConvUnid = questionarioFacade.buscarAelQuestionariosConvUnidPorQuestionario(getQuestionario().getSeq(),
																			firstResult, maxResults, orderProperty, asc);
		if(listAelQuestionariosConvUnid == null) {
			listAelQuestionariosConvUnid = new ArrayList<AelQuestionariosConvUnid>();
		}
		
		return listAelQuestionariosConvUnid;
	}
	
	
	public void gravar(){
		
		if(validarON1()) {
			try {
				questionarioFacade.gravarRequisitante(criarAssociacao());
				
				// Limpa campos tela
				setUnidade(null);
				setConvenio(null);
				dataModel.reiniciarPaginator();
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ASSOCIACAO_REQUISITANTE_QUESTIONARIO");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
		} else {
			apresentarMsgNegocio(Severity.ERROR,"ERRO_PREENCHIMENTO_CONVENIO_UNIDADE_FUNCIONAL");
		}
	}
	
	private AelQuestionariosConvUnid criarAssociacao() {
		
		int seqp = questionarioFacade.obterProximoSequencialAelQuestionariosConvUnid();
		
		AelQuestionariosConvUnid qcu = new AelQuestionariosConvUnid();
		AelQuestionariosConvUnidId qcuId = new AelQuestionariosConvUnidId();
		qcuId.setSeqp(seqp);
		qcuId.setQtnSeq(questionario.getSeq());
		qcu.setId(qcuId);
		qcu.setConvenioSaude(getConvenio());
		qcu.setUnidadeFuncional(getUnidade());
		qcu.setAelQuestionarios(questionario);
		questionario.setAelQuestionariosConvUnids(new HashSet<AelQuestionariosConvUnid>(listAelQuestionariosConvUnid));
		questionario.getAelQuestionariosConvUnids().add(qcu);
		
		return qcu;
	}
	
	public void excluir() {
		try {
			//getQuestionario().getAelQuestionariosConvUnids().remove(getItemExclusao());
			examesFacade.remover(selecionado.getId());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_REQUISITANTE_QUESTIONARIO");
			dataModel.reiniciarPaginator();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean validarON1(){
		
		boolean podeGravar = true;
		boolean temUnidade = getUnidade() != null && getUnidade().getSeq() != null;
		boolean temConvenio = getConvenio() != null && getConvenio().getCodigo() != null;
		
		if(temUnidade && temConvenio) {
			podeGravar = false;
		}
		
		return podeGravar;		
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

	public String getSeqQuestionario() {
		return seqQuestionario;
	}

	public void setSeqQuestionario(String seqQuestionario) {
		this.seqQuestionario = seqQuestionario;
	}

	public Boolean desabilitarBotaoGravar() {
		return this.unidade == null && this.convenio == null;
	}

	public DynamicDataModel<AelQuestionariosConvUnid> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AelQuestionariosConvUnid> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}

	public AelQuestionariosConvUnid getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelQuestionariosConvUnid selecionado) {
		this.selecionado = selecionado;
	}
}