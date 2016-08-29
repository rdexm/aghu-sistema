package br.gov.mec.aghu.ambulatorio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterPagadorController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ManterPagadorController.class);
	private static final long serialVersionUID = -2771415742588682065L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;
	
	/**
	 * Operação a ser realizada.
	 */
	private DominioOperacoesJournal operacao;

	/**
	 * Atributos do Tipo e Autorização.
	 */
	private Short codigoOrgaoPagador;
	
	/**
     * Objeto a ser criado/editado
     */
    private AacPagador pagador;
    
    private static final String REDIRECIONA_PESQUISAR_PAGADOR = "manterPagadorList";
    
    
	/*public String iniciarInclusao() {
		this.codigoOrgaoPagador = null;
		return "inicio";
	}*/
    
    @PostConstruct
	public void init() {
		this.setPagador(new AacPagador());
	}

	public void inicio() {
	 

	 

		
		if (pagador.getSeq() != null) {			
			this.setOperacao(DominioOperacoesJournal.UPD);
		} else {			
			this.setOperacao(DominioOperacoesJournal.INS);
		}
	
	}
	

	public String confirmar() {
		try {
			if (this.operacao.equals(DominioOperacoesJournal.INS)) {
				this.ambulatorioFacade.persistirPagador(pagador);
				this.apresentarMsgNegocio(Severity.INFO,
                        "MSG_PAGADOR_INCLUIDO_SUCESSO");
			} else {
				this.ambulatorioFacade.atualizarPagador(pagador);
				this.apresentarMsgNegocio(Severity.INFO,
                        "MSG_PAGADOR_ATUALIZADO_SUCESSO");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}catch (PersistenceException e) {
			LOG.error("Exceção capturada", e);
			if (e.getCause() instanceof ConstraintViolationException
                    || e.getCause() instanceof NonUniqueObjectException) {
                this.apresentarMsgNegocio(Severity.ERROR,
                        "MSG_PAGADOR_EXISTENTE");
            } else {
                if (this.operacao.equals(DominioOperacoesJournal.INS)) {
                    this.apresentarMsgNegocio(Severity.ERROR,
                            "MSG_PAGADOR_INCLUIDO_ERRO");
                } else {
                    this.apresentarMsgNegocio(Severity.ERROR,
                            "MSG_PAGADOR_ALTERADO_ERRO");
                }
            }            
		}
		return cancelar();
	}
	
	/**
     * Método que realiza a ação do botão cancelar na tela de cadastro
     */
    public String cancelar() {
    	this.setPagador(new AacPagador());
    	return REDIRECIONA_PESQUISAR_PAGADOR;
    }

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public AacPagador getPagador() {
		return pagador;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public Short getCodigoOrgaoPagador() {
		return codigoOrgaoPagador;
	}

	public void setCodigoOrgaoPagador(Short codigoOrgaoPagador) {
		this.codigoOrgaoPagador = codigoOrgaoPagador;
	}

}
