package br.gov.mec.aghu.paciente.cadastro.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.AipPacienteDadoClinicos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da tela de cadastro de Dados
 * Adicionais de Paciente
 * 
 * @author ehgsilva
 * 
 */

public class DadosAdicionaisPacienteController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4402996016755602610L;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	/**
	 * 
	 */
	private Integer aipPacientesCodigo;

	/**
	 * Paciente sendo editado ou incluído.
	 */
	private AipPacientes aipPaciente;

	/**
	 * Paciente sendo editado ou incluído.
	 */
	private AipPacienteDadoClinicos aipPacienteDadoClinicos;

	/**
	 * Peso sendo incluído ou editado.
	 */
	private AipPesoPacientes aipPesoPacientes;
	
	private static final String REDIRECT_CADASTRO_PACIENTE = "cadastroPaciente";
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void preparaInicioDadosAdicionaisPaciente() {
		aipPaciente = this.pacienteFacade.obterPaciente(aipPacientesCodigo);
		aipPacienteDadoClinicos = cadastroPacienteFacade.buscaDadosAdicionaisClinicos(aipPaciente); 
		if (aipPacienteDadoClinicos == null) {
			aipPacienteDadoClinicos = new AipPacienteDadoClinicos();
		}
		aipPesoPacientes = pacienteFacade.obterPesoPacienteAtual(aipPaciente);
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de dados adicionais
	 */
	public String confirmar() {
		try {	
			this.cadastroPacienteFacade.persistirDadosAdicionais(
					aipPacienteDadoClinicos, aipPaciente, aipPesoPacientes);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (BaseRuntimeException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "Peso Inválido");
			return null;
		}

		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_DADOS_ADICIONAIS");
		
		return REDIRECT_CADASTRO_PACIENTE;

	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de dados adicionais
	 */
	public String cancelar() {
		return REDIRECT_CADASTRO_PACIENTE;
	}
	
	/**
	 * Método que sugere um valor
	 * para o campo mesesGestacao de acordo com o igSemanas informado.
	 * 
	 * Método que realiza a execução da procedure do ORACLE
	 * busca_dados_rn
	 * 
	 */
	public void sugerirMesesGestacao(){
		if (aipPacienteDadoClinicos.getIgSemanas() != null){
			Byte mesesGestacao = cadastroPacienteFacade.definirMesesGestacaoDadosAdicionais(aipPacienteDadoClinicos.getIgSemanas());	
			aipPacienteDadoClinicos.setMesesGestacao(mesesGestacao);
		}
	}

	public AipPacienteDadoClinicos getAipPacienteDadoClinicos() {
		return aipPacienteDadoClinicos;
	}

	public void setAipPacienteDadoClinicos(
			AipPacienteDadoClinicos aipPacienteDadoClinicos) {
		this.aipPacienteDadoClinicos = aipPacienteDadoClinicos;
	}

	public AipPacientes getAipPaciente() {
		return aipPaciente;
	}

	public void setAipPaciente(AipPacientes aipPaciente) {
		this.aipPaciente = aipPaciente;
	}

	public AipPesoPacientes getAipPesoPacientes() {
		return aipPesoPacientes;
	}

	public void setAipPesoPacientes(AipPesoPacientes aipPesoPacientes) {
		this.aipPesoPacientes = aipPesoPacientes;
	}

	/**
	 * @return the aipPacientesCodigo
	 */
	public Integer getAipPacientesCodigo() {
		return aipPacientesCodigo;
	}

	/**
	 * @param aipPacientesCodigo
	 *            the aipPacientesCodigo to set
	 */
	public void setAipPacientesCodigo(Integer aipPacientesCodigo) {
		this.aipPacientesCodigo = aipPacientesCodigo;
	}

	public String getStyleProntuario(){
		String retorno = "";
		
		if (this.aipPaciente != null && aipPaciente.isProntuarioVirtual()){
			retorno = "background-color:#0000ff";
		}		
		return retorno;
		
	}
	
}