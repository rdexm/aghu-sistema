package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcMotivoDemoraSalaRec;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class MotivosDemoraSRPACRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -3566582839368074664L;

	private static final String MOTIVO_DEMORA_LIST = "motivosDemoraSRPA";
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroFacade;
	
	private MbcMotivoDemoraSalaRec motivoDemoraSalaRec;
	
	private String descricao;
	// Campo situação booleano e utilizado no componente mec:selectBooleanCheckbox
	private Boolean situacao;
	
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
		// Cria nova instância que será persistida
		this.motivoDemoraSalaRec = new MbcMotivoDemoraSalaRec();
		descricao = null;
		situacao = true;	
	}
	
	public String gravar() {
		try {
			motivoDemoraSalaRec =  new MbcMotivoDemoraSalaRec(); 
			motivoDemoraSalaRec.setDescricao(descricao);
			if(situacao){
				motivoDemoraSalaRec.setSituacao(DominioSituacao.A);
			}else{
				motivoDemoraSalaRec.setSituacao(DominioSituacao.I);	
			}
			this.blocoCirurgicoCadastroFacade.inserirMotivoDemoraSalaRec(motivoDemoraSalaRec);

			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_SALA_UNIDADE_CIRURGICA", this.getMotivoDemoraSalaRec().getDescricao());
		
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return MOTIVO_DEMORA_LIST;
	}
	
	public void limpar() {
		motivoDemoraSalaRec = null;
		descricao = null;
		situacao = null;
	}
	
	public String cancelar() {
		motivoDemoraSalaRec = null;
		return MOTIVO_DEMORA_LIST;
	}

	public MbcMotivoDemoraSalaRec getMotivoDemoraSalaRec() {
		return motivoDemoraSalaRec;
	}

	public void setMotivoDemoraSalaRec(MbcMotivoDemoraSalaRec motivoDemoraSalaRec) {
		this.motivoDemoraSalaRec = motivoDemoraSalaRec;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}
}