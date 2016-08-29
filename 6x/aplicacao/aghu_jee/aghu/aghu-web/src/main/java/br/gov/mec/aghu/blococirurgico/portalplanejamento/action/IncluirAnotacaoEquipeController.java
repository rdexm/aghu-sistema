package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.MbcAgendaAnotacao;
import br.gov.mec.aghu.model.MbcAgendaAnotacaoId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class IncluirAnotacaoEquipeController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -1482290186703206986L;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	
	private static final String ID_MODAL_ANOTACAO_EQUIPE = "modalIncluirAnotacaoEquipeWG";

	private MbcAgendaAnotacaoId mbcAgendaAnotacaoId;
	private MbcAgendaAnotacao mbcAgendaAnotacao;
	private Boolean novaAnotacao;

	public void inicio(Date dtAgenda, Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf) {
		mbcAgendaAnotacaoId = new MbcAgendaAnotacaoId(dtAgenda,pucSerMatricula,pucSerVinCodigo,pucUnfSeq,pucIndFuncaoProf);
		mbcAgendaAnotacao = getBlocoCirurgicoPortalPlanejamentoFacade().obterMbcAgendaAnotacaoPorChavePrimaria(mbcAgendaAnotacaoId);
		if (mbcAgendaAnotacao == null) {
			novaAnotacao = true;
			mbcAgendaAnotacao = new MbcAgendaAnotacao();
			mbcAgendaAnotacao.setId(mbcAgendaAnotacaoId);
		} else {
			novaAnotacao = false;
		}
	}
	
	public void gravar() {
		try {
			
			if(this.mbcAgendaAnotacao.getId().getData() == null){
				apresentarMsgNegocio("data", Severity.ERROR, "CAMPO_OBRIGATORIO", super.getBundle().getString("LABEL_INCLUIR_ANOTACAO_EQUIPE_DATA"));
				return;
			} else if (this.mbcAgendaAnotacao.getDescricao() == null) {
				apresentarMsgNegocio("descricao", Severity.ERROR, "CAMPO_OBRIGATORIO", super.getBundle().getString("LABEL_INCLUIR_ANOTACAO_EQUIPE_DESCRICAO"));
			} else if (this.mbcAgendaAnotacao.getDescricao().length() > 2000){
				this.mbcAgendaAnotacao.setDescricao(this.mbcAgendaAnotacao.getDescricao().replaceAll("\\r\\n", "\n"));
			}
			
			getBlocoCirurgicoPortalPlanejamentoFacade().persistirMbcAgendaAnotacao(mbcAgendaAnotacao);
			flushEMensagemSucesso();
			detalhamentoPortalAgendamentoController.buscarDetalhamento();
			closeDialog(ID_MODAL_ANOTACAO_EQUIPE);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);	
		} catch (BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);	
		}
	}

	private void flushEMensagemSucesso() {
		if(novaAnotacao){
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INCLUIR_ANOTACAO_EQUIPE_SUCESSO_INCLUSAO");
		} else {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INCLUIR_ANOTACAO_EQUIPE_SUCESSO_ALTERACAO");
		}
	}

	public IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() {
		return blocoCirurgicoPortalPlanejamentoFacade;
	}

	public MbcAgendaAnotacaoId getMbcAgendaAnotacaoId() {
		return mbcAgendaAnotacaoId;
	}

	public void setMbcAgendaAnotacaoId(MbcAgendaAnotacaoId mbcAgendaAnotacaoId) {
		this.mbcAgendaAnotacaoId = mbcAgendaAnotacaoId;
	}

	public MbcAgendaAnotacao getMbcAgendaAnotacao() {
		return mbcAgendaAnotacao;
	}

	public void setMbcAgendaAnotacao(MbcAgendaAnotacao mbcAgendaAnotacao) {
		this.mbcAgendaAnotacao = mbcAgendaAnotacao;
	}

	public Boolean getNovaAnotacao() {
		return novaAnotacao;
	}

	public void setNovaAnotacao(Boolean novaAnotacao) {
		this.novaAnotacao = novaAnotacao;
	}

	public void setBlocoCirurgicoPortalPlanejamentoFacade(IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade) {
		this.blocoCirurgicoPortalPlanejamentoFacade = blocoCirurgicoPortalPlanejamentoFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}
}
