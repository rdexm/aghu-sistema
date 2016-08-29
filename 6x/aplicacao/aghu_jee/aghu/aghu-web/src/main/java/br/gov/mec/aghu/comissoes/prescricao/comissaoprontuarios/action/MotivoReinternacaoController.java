package br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business.IComissaoProntuariosPrescricaoComissoesFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class MotivoReinternacaoController extends ActionController {


	private static final long serialVersionUID = -8874099797319214680L;

	@EJB
	private IComissaoProntuariosPrescricaoComissoesFacade comissaoProntuariosPrescricaoComissoesFacade;

	private MpmMotivoReinternacao motivoReinternacao;
	
	private static final String MOTIVO_REINTERNACAO_LIST = "motivoReinternacaoList";

	public void iniciar() {
		if (this.motivoReinternacao == null) {
			this.motivoReinternacao = new MpmMotivoReinternacao();
			this.motivoReinternacao.setIndSituacao(DominioSituacao.A);
		}
	}
	
	public void iniciarEdicao(MpmMotivoReinternacao selecionado){
		this.motivoReinternacao = selecionado;
	}

	public String cancelar() {
		this.motivoReinternacao = new MpmMotivoReinternacao();
		return MOTIVO_REINTERNACAO_LIST;
	}

//	@Restrict("#{s:hasPermission('motivoReinternacao','alterar')}")
	public String confirmar() {
		try {
			boolean isInsert = (motivoReinternacao.getSeq() != null);
			this.comissaoProntuariosPrescricaoComissoesFacade.inserirAtualizarMotivoReinternacao(this.motivoReinternacao);

			// Alteração
			if (isInsert) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_MOTIVO_REINTERNACAO", motivoReinternacao.getDescricao());
				
			// Inclusão
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MOTIVO_REINTERNACAO", motivoReinternacao.getDescricao());
			}
			
			return cancelar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	// GETS E SETS
	public MpmMotivoReinternacao getMotivoReinternacao() {
		return motivoReinternacao;
	}

	public void setMotivoReinternacao(MpmMotivoReinternacao motivoReinternacao) {
		this.motivoReinternacao = motivoReinternacao;
	} 
}