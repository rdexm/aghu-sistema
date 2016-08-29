package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;


public class ManterAnamneseController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 14350067867867L;
		
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private MpmAnamneses anamnese;
	private RapServidores servidor;
	private Integer seqAtendimento;
	private Long seqAnamnese;
	private String oldDescricaoAnamnese;
	
	public void iniciar() {
		try {
			if(this.seqAtendimento != null || this.seqAnamnese != null) {
				this.anamnese = this.prescricaoMedicaFacade.obterAnamnese(this.seqAtendimento, this.seqAnamnese, this.servidor);
				if(anamnese != null){
					this.prescricaoMedicaFacade.iniciarEdicaoAnamnese(anamnese, servidor);
					oldDescricaoAnamnese = anamnese.getDescricao();
				}
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean verificarAutorAnamneseEdicao(){
		if(CoreUtil.modificados(servidor, anamnese.getServidor()) && DominioIndPendenteAmbulatorio.V.equals(anamnese.getPendente())){
			return false;
		}
		return true;
	}
	
	public boolean verificarAnamneseConcluida(){
		return DominioIndPendenteAmbulatorio.V.equals(anamnese.getPendente()) ? Boolean.TRUE : Boolean.FALSE;
	}
	
	public String concluir() {
		try {
			this.prescricaoMedicaFacade.concluirAnamnese(this.anamnese, this.servidor);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ANAMNESE_SALVO_SUCESSO");			
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
		return "listarAnamneseEvolucoes";
	}
	
	public String deixarPendente() {
		try {
			this.prescricaoMedicaFacade.deixarPendenteAnamnese(this.anamnese, this.servidor);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ANAMNESE_SALVO_SUCESSO");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
		return "listarAnamneseEvolucoes";
	}
	
	public boolean desabilitarBotaoPendente() {
		return this.prescricaoMedicaFacade.verificarEvolucoesNotasAdicionais(this.anamnese.getSeq());
	}
	
	public boolean verificarAlteracao(){
		if(anamnese != null && CoreUtil.modificados(anamnese.getDescricao(), oldDescricaoAnamnese)){
			return true;
		}
		return false;
	}
	
	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}
	
	public void setSeqAnamnese(Long seqAnamnese) {
		this.seqAnamnese = seqAnamnese;
	}

	public MpmAnamneses getAnamnese() {
		return anamnese;
	}

	public void setAnamnese(MpmAnamneses anamnese) {
		this.anamnese = anamnese;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public String getOldDescricaoAnamnese() {
		return oldDescricaoAnamnese;
	}

	public void setOldDescricaoAnamnese(String oldDescricaoAnamnese) {
		this.oldDescricaoAnamnese = oldDescricaoAnamnese;
	}
	
}
