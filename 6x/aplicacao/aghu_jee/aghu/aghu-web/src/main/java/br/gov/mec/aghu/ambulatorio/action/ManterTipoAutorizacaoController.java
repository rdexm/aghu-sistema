package br.gov.mec.aghu.ambulatorio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterTipoAutorizacaoController extends ActionController {
	private static final String MANTER_TIPO_AUTORIZACAO_LIST = "manterTipoAutorizacaoList";
	private static final Log LOG = LogFactory.getLog(ManterTipoAutorizacaoController.class);
	private static final long serialVersionUID = -369420689142388987L;

	@EJB
    private IAmbulatorioFacade ambulatorioFacade;

	@EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;

    private Short codigoTipoAutorizacao;
    private DominioOperacoesJournal operacao;
    private AacTipoAgendamento tipoAgendamento;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
    public String iniciarInclusao() {
        this.codigoTipoAutorizacao = null;
        return null;
    }

    public void inicio() {
	 

	 

        if (this.codigoTipoAutorizacao != null) {
            this.tipoAgendamento = this.ambulatorioFacade.obterTipoAgendamentoPorCodigo(codigoTipoAutorizacao);
            this.setOperacao(DominioOperacoesJournal.UPD);
        } else {
            tipoAgendamento = new AacTipoAgendamento();
            this.setOperacao(DominioOperacoesJournal.INS);
        }
    
	}
	

    public String confirmar() {
        try {
            if (this.operacao.equals(DominioOperacoesJournal.INS)) {
                this.ambulatorioFacade.persistirTipoAgendamento(tipoAgendamento);
            } else {
                this.ambulatorioFacade.atualizarTipoAgendamento(tipoAgendamento);
            }

            if (this.operacao.equals(DominioOperacoesJournal.INS)) {
            	this.apresentarMsgNegocio(Severity.INFO,
                        "MSG_TIPO_AUTORIZACAO_INCLUIDO_SUCESSO");
            } else if (this.operacao.equals(DominioOperacoesJournal.UPD)) {
            	this.apresentarMsgNegocio(Severity.INFO,
                        "MSG_TIPO_AUTORIZACAO_ALTERADO_SUCESSO");
            }
        } catch (ApplicationBusinessException e) {
            apresentarExcecaoNegocio(e);
            return null;
        } catch (PersistenceException e) {
            getLog().error("Exceção capturada: ", e);
            if (e.getCause() instanceof ConstraintViolationException
                    || e.getCause() instanceof NonUniqueObjectException) {
            	this.apresentarMsgNegocio(Severity.ERROR,
                        "MSG_TIPO_AUTORIZACAO_EXISTENTE");
            } else {
                if (this.getOperacao().equals(DominioOperacoesJournal.INS)) {
                	this.apresentarMsgNegocio(Severity.ERROR,
                            "MSG_TIPO_AUTORIZACAO_INCLUIDO_ERRO");
                } else {
                	this.apresentarMsgNegocio(Severity.ERROR,
                            "MSG_TIPO_AUTORIZACAO_ALTERADO_ERRO");
                }
            }
            return null;
        }
        return MANTER_TIPO_AUTORIZACAO_LIST;
    }

    private Log getLog() {
		return LOG;
	}

	/**
     * Método que realiza a ação do botão cancelar na tela de cadastro da tipo
     * de Autorização.
     */
    public String cancelar() {
        getLog().info("Cancelado");
        return MANTER_TIPO_AUTORIZACAO_LIST;
    }

    public IAmbulatorioFacade getAmbulatorioFacade() {
        return ambulatorioFacade;
    }

    public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
        this.ambulatorioFacade = ambulatorioFacade;
    }

    public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
        return registroColaboradorFacade;
    }

    public void setRegistroColaboradorFacade(IRegistroColaboradorFacade registroColaboradorFacade) {
        this.registroColaboradorFacade = registroColaboradorFacade;
    }

    public DominioOperacoesJournal getOperacao() {
        return operacao;
    }

    public void setOperacao(DominioOperacoesJournal operacao) {
        this.operacao = operacao;
    }

    public AacTipoAgendamento getTipoAgendamento() {
        return tipoAgendamento;
    }

    public void setTipoAgendamento(AacTipoAgendamento tipoAgendamento) {
        this.tipoAgendamento = tipoAgendamento;
    }

    public Short getCodigoTipoAutorizacao() {
        return codigoTipoAutorizacao;
    }

    public void setCodigoTipoAutorizacao(Short codigoTipoAutorizacao) {
        this.codigoTipoAutorizacao = codigoTipoAutorizacao;
    }
}
