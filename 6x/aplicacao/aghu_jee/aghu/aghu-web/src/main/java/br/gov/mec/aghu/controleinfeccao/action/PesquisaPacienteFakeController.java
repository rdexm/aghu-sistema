package br.gov.mec.aghu.controleinfeccao.action;


import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;


/**
 * @author marcelo.corati
 */

public class PesquisaPacienteFakeController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4420513793542915780L;


	private static final String FATOR_PREDISPONENTE = "controleinfeccao-notificacaoFatorPredisponente";
	
	private static final String PROCEDIMENTO_RISCO = "controleinfeccao-notificacaoProcedimentoRisco";
	
	private static final String PACIENTE_FAKE = "controleinfeccao-pacienteFake";
	
	private static final String NOTIFICACAO_TOPOGRAFIA = "notificacaoTopografia";
	
	private static final String NOTIFICACAO_PREVENTIVA = "controleinfeccao-notificacaoMedidaPreventiva";
	

	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private Integer codigoPaciente;
	
	private AipPacientes paciente;
	
	private String voltarPara = PACIENTE_FAKE;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		limpar();
	}
	
	
	public void inicio() {
	 

	 

		voltarPara = PACIENTE_FAKE;
	
	}
	

	public Boolean pesquisaPaciente(){
		if(this.codigoPaciente != null){
			this.paciente = pacienteFacade.obterPaciente(this.codigoPaciente);
			if(this.paciente != null){
				return true;
			}else{
				apresentarMsgNegocio(Severity.INFO, "Paciente n\u00e3o existe");
			}
		}
		return false;
	}
	
	public String fatorPredisponente(){
		if(pesquisaPaciente()){
			limpar();
			return FATOR_PREDISPONENTE;
		}
		return "";
	}
	
	public String procedimentoRisco(){
		if(pesquisaPaciente()){
			limpar();
			return PROCEDIMENTO_RISCO;
		}
		return "";
	}
	
	public String notificacarTopografia(){
		if(pesquisaPaciente()){
			limpar();
			return NOTIFICACAO_TOPOGRAFIA;
		}
		return "";
	}
	
	public String notificacarPreventiva(){
		if(pesquisaPaciente()){
			limpar();
			return NOTIFICACAO_PREVENTIVA;
		}
		return "";
	}
	
	public void limpar(){
		this.codigoPaciente = null;
		this.paciente = null;
	}
	
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}


	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}


	public AipPacientes getPaciente() {
		return paciente;
	}


	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}


	public String getVoltarPara() {
		return voltarPara;
	}	
	
}
