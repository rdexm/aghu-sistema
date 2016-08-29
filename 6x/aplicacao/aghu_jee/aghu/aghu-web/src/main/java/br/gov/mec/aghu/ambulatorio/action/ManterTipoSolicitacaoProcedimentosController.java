package br.gov.mec.aghu.ambulatorio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.MamTipoSolProcedimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterTipoSolicitacaoProcedimentosController extends ActionController {

	private static final String LABEL_TIPO_SOLICITACAO_PROCEDIMENTOS = "LABEL_TIPO_SOLICITACAO_PROCEDIMENTOS";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterTipoSolicitacaoProcedimentosController.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1269632700632932412L;
	
	private static final String CANCELA_MANTER_TIPO_SOLICITACAO_PROCEDIMENTO = "ambulatorio-cancelaManterTipoSolicitacaoProcedimentos";
	
	private static final String CONFIRMADO_MANTER_TIPO_SOLICITACAO_PROCEDIMENTO = "ambulatorio-confirmadoManterTipoSolicitacaoProcedimentos";
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	private DominioOperacoesJournal operacao;
	
	private Short codigoTipoSolicitacaoProcedimentos;
	
	private MamTipoSolProcedimento tipoSolicitacaoProcedimento;
	
	public String iniciarInclusao() {
		this.codigoTipoSolicitacaoProcedimentos = null;
		return "inicio";
	}
	
	public void inicio() {
	 

	 

		if (this.codigoTipoSolicitacaoProcedimentos != null) {
			this.tipoSolicitacaoProcedimento = this.ambulatorioFacade.obterTipoSolicitacaoProcedimentoPorCodigo(codigoTipoSolicitacaoProcedimentos);
			this.setOperacao(DominioOperacoesJournal.UPD);
		} else {
			tipoSolicitacaoProcedimento = new MamTipoSolProcedimento();
			this.setOperacao(DominioOperacoesJournal.INS);
		}
	
	}
	
	
	public String confirmar() {
		
		try {
			this.ambulatorioFacade.persistirTipoSolicitacaoProcedimentos(tipoSolicitacaoProcedimento);
			
			if (this.getOperacao().equals(DominioOperacoesJournal.INS)) {
				this.apresentarMsgNegocio(Severity.INFO, "MSG_INCLUIDO", getBundle().getString(LABEL_TIPO_SOLICITACAO_PROCEDIMENTOS));
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MSG_ALTERADO", getBundle().getString(LABEL_TIPO_SOLICITACAO_PROCEDIMENTOS));
			}		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (PersistenceException e) {
			LOG.error("Exceção capturada: ", e);
			if (e.getCause() instanceof ConstraintViolationException || e.getCause() instanceof NonUniqueObjectException) {
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_ITEM_EXISTENTE", getBundle().getString(LABEL_TIPO_SOLICITACAO_PROCEDIMENTOS));
			} else {
				if (this.getOperacao().equals(DominioOperacoesJournal.INS)) {
					this.apresentarMsgNegocio(Severity.ERROR, "MSG_INCLUIDO_ERRO", getBundle().getString(LABEL_TIPO_SOLICITACAO_PROCEDIMENTOS));
				} else {
					this.apresentarMsgNegocio(Severity.ERROR, "MSG_EXCLUIDO", getBundle().getString(LABEL_TIPO_SOLICITACAO_PROCEDIMENTOS));				
				}
			}
			return null;
		}
		return CONFIRMADO_MANTER_TIPO_SOLICITACAO_PROCEDIMENTO;
	}
	
	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro da tipo
	 * de Autorização.
	 */
	public String cancelar() {
		return CANCELA_MANTER_TIPO_SOLICITACAO_PROCEDIMENTO;
	}
	
	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}
	
	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}
	
	public MamTipoSolProcedimento getTipoSolicitacaoProcedimento() {
		return tipoSolicitacaoProcedimento;
	}
	
	public void setTipoSolicitacaoProcedimento(MamTipoSolProcedimento tipoSolicitacaoProcedimento) {
		this.tipoSolicitacaoProcedimento = tipoSolicitacaoProcedimento;
	}
	
	public Short getCodigoTipoSolicitacaoProcedimentos() {
		return codigoTipoSolicitacaoProcedimentos;
	}
	
	public void setCodigoTipoSolicitacaoProcedimentos(Short codigoTipoSolicitacaoProcedimentos) {
		this.codigoTipoSolicitacaoProcedimentos = codigoTipoSolicitacaoProcedimentos;
	}
}
