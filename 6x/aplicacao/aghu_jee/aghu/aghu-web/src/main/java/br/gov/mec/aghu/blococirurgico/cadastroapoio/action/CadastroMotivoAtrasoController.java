package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcMotivoAtraso;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroMotivoAtrasoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final String MOTIVO_ATRASO_LIST = "pesquisaMotivoAtraso";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4798835949825786647L;

	/*
	 * Injeções
	 */

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	/**
	 * Instância que será gravada
	 */
	private MbcMotivoAtraso motivoAtraso;

	// Campo situação booleano e utilizado no componente mec:selectBooleanCheckbox
	private Boolean situacao;

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
	 

	 

		// Cria nova instância que será persistida
		this.motivoAtraso = new MbcMotivoAtraso();
		this.situacao = true; // Valor padrão é ATIVO
	
	}
	

	/**
	 * Gravar
	 * 
	 * @return
	 */
	public String gravar() {
		try {

			// Seta situação do componente mec:selectBooleanCheckbox na instancia que será gravada
			this.motivoAtraso.setSituacao(DominioSituacao.getInstance(this.situacao));

			// INSERIR
			this.blocoCirurgicoCadastroApoioFacade.persistirMotivoAtraso(this.motivoAtraso);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_MOTIVO_ATRASO", this.motivoAtraso.getDescricao());

			return MOTIVO_ATRASO_LIST;

		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String cancelar() {
		return MOTIVO_ATRASO_LIST;
	}

	/*
	 * Getters e Setters
	 */

	public MbcMotivoAtraso getMotivoAtraso() {
		return motivoAtraso;
	}
	
	public void setMotivoAtraso(MbcMotivoAtraso motivoAtraso) {
		this.motivoAtraso = motivoAtraso;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

}