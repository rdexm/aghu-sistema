package br.gov.mec.aghu.controleinfeccao.action;



import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciAntimicrobianos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class CadastroAntimicrobianosController extends ActionController {

	private static final long serialVersionUID = -4249190794519927027L;

	private static final String ANTIMICROBIANOS_LIST = "pesquisaAntimicrobianos";
	
	
	private MciAntimicrobianos mciAntimicrobiano;
	private Boolean ativo;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoPaciente;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

	 

		if(mciAntimicrobiano != null && mciAntimicrobiano.getSeq() != null) {
			this.mciAntimicrobiano = this.controleInfeccaoPaciente.obterAntimicrobianoPorChavePrimaria(mciAntimicrobiano.getSeq());
			this.ativo = DominioSituacao.A.equals(mciAntimicrobiano.getSituacao()) ? true : false;

			if(mciAntimicrobiano == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
		} else {
			this.mciAntimicrobiano = new MciAntimicrobianos();
			this.setAtivo(true);
		}
		
		return null;
	
	}
	
	public String gravar() {
		try {
			
			boolean isUpdate = mciAntimicrobiano.getSeq() != null;
			this.mciAntimicrobiano.setSituacao(this.ativo ? DominioSituacao.A : DominioSituacao.I);
			this.controleInfeccaoPaciente.persistirMciAntimicrobiano(mciAntimicrobiano);
			
			if(isUpdate) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ANTIMICROBIANO_SUCESSO_ALTERACAO", this.mciAntimicrobiano.getDescricao());
				
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ANTIMICROBIANO_SUCESSO_CADASTRO", this.mciAntimicrobiano.getDescricao());
			}
			
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		mciAntimicrobiano = null;
		ativo = null;
		return ANTIMICROBIANOS_LIST;
	}

	public MciAntimicrobianos getMciAntimicrobiano() {
		return mciAntimicrobiano;
	}

	public void setMciAntimicrobiano(MciAntimicrobianos mciAntimicrobiano) {
		this.mciAntimicrobiano = mciAntimicrobiano;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

}
