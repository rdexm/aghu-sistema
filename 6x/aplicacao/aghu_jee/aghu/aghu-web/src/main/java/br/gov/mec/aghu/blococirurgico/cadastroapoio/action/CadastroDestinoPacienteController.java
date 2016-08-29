package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroDestinoPacienteController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4798835949825786646L;

	private static final String DESTINO_PACIENTE_LIST = "pesquisaDestinoPaciente";
	
	/*
	 * Injeções
	 */

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	/*
	 * Parâmetros de conversação
	 */
	private String voltarPara; // Controla a navegação do botão voltar

	/**
	 * Instância que será gravada
	 */
	private MbcDestinoPaciente destinoPaciente;

	// Campo situação booleano e utilizado no componente mec:selectBooleanCheckbox
	private DominioSituacao situacao;

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
	 

	 

		// Cria nova instância que será persistida
		this.destinoPaciente = new MbcDestinoPaciente();
		//this.situacao = true; // Valor padrão é ATIVO
		this.situacao = DominioSituacao.A;
	
	}
	

	/**
	 * Gravar
	 * 
	 * @return
	 */
	public String gravar() {
		try {

			// Seta situação do componente mec:selectBooleanCheckbox na instancia que será gravada
			//this.destinoPaciente.setSituacao(DominioSituacao.getInstance(this.situacao));
			this.destinoPaciente.setSituacao(this.situacao);

			// INSERIR
			this.blocoCirurgicoCadastroApoioFacade.persistirDestinoPaciente(this.destinoPaciente);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_DESTINO_PACIENTE", this.destinoPaciente.getDescricao());

			return DESTINO_PACIENTE_LIST;

		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String cancelar() {
		return DESTINO_PACIENTE_LIST;
	}

	/*
	 * Getters e Setters
	 */

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public MbcDestinoPaciente getDestinoPaciente() {
		return destinoPaciente;
	}

	public void setDestinoPaciente(MbcDestinoPaciente destinoPaciente) {
		this.destinoPaciente = destinoPaciente;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	public DominioSituacao[] obterValoresDominioSituacao(){
		return DominioSituacao.values();
	}
}