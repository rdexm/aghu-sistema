package br.gov.mec.aghu.emergencia.perinatologia.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoSindrome;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;


/**
 * Controller Incluir/Editar um diagnostico
 * 
 * @author marcelo.corati
 * 
 */
public class SindromeController  extends ActionController  {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6589546988357451478L;
	private final String PAGE_PESQUISA = "sindromeList"; 
	private Integer cidSeq;
	
	private boolean indSituacao;
	private String descricao;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	
	public void inicio() {
			setIndSituacao(true);
			descricao = null;
	}
	
	public String gravar() {
			McoSindrome obj = emergenciaFacade.obterSindromePorDescricao(descricao);
			if(obj != null){
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ERRO_SINDROME_JA_CADASTRADA");
			}else{
				String situacao = "I";
				if(indSituacao){
					situacao = "A";
				}
				pacienteFacade.persistirSindrome(descricao, situacao);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SINDROME_INSERIDA_SUCESSO");
			}
			limpar();
			return PAGE_PESQUISA;
	}

	
	public String cancelar(){
		limpar();
		return PAGE_PESQUISA;
	}
	

	private  void limpar(){
		indSituacao = true;
		descricao = null;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public boolean isIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
