package br.gov.mec.aghu.emergencia.perinatologia.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoTabBallard;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;


/**
 * Controller Incluir/Editar um diagnostico
 * 
 * @author marcelo.corati
 * 
 */
public class BallardController  extends ActionController  {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6589546988357451478L;
	private final String PAGE_PESQUISA = "ballardList"; 
	private boolean edit;
	private Integer cidSeq;
	
	private Short seq;
	private Short escore;
	private Short igSemanas;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@PostConstruct
	public void init() {
		begin(conversation);
		//this.permManterDiagnostico = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterDiagnostico", "alterar");
	}
	
	
	public void inicio() {
		// caso seja um cadastro novo
		if (getSeq() == null) {
			setEdit(false);
			seq = null;
			escore = null;
			igSemanas = null;

		// caso seja edição	
		}else{
			// busca dados e popula na tela
			setEdit(true);
			McoTabBallard obj = emergenciaFacade.obterBallardPorChavePrimaria(seq);
			escore = obj.getEscoreBallard();
			igSemanas = obj.getIgSemanas();
		}
	
	}
	
	public String gravar() {
		if(igSemanas.intValue() >= 1 && igSemanas.intValue() <= 44){
			this.pacienteFacade.persistirBallard(seq,escore,igSemanas);
			if (!isEdit()) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_BALLARD_INSERIDO_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_BALLARDS_ALTERADO_SUCESSO");
			}
			
			limpar();
			return PAGE_PESQUISA;
		}else{
			apresentarMsgNegocio(Severity.ERROR,"MCO-00502");
		}
		return null;
	}

	
	public String cancelar(){
		limpar();
		return PAGE_PESQUISA;
	}
	


	private  void limpar(){
		seq = null;
		escore = null;
		igSemanas = null;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}


	public Short getSeq() {
		return seq;
	}


	public void setSeq(Short seq) {
		this.seq = seq;
	}


	public Short getEscore() {
		return escore;
	}


	public void setEscore(Short escore) {
		this.escore = escore;
	}


	public Short getIgSemanas() {
		return igSemanas;
	}


	public void setIgSemanas(Short igSemanas) {
		this.igSemanas = igSemanas;
	}

}
