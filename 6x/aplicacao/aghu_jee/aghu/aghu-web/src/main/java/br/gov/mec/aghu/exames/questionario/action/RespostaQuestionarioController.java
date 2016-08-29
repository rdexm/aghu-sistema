package br.gov.mec.aghu.exames.questionario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.questionario.vo.QuestionarioVO;
import br.gov.mec.aghu.exames.questionario.vo.RespostaQuestaoVO;
import br.gov.mec.aghu.core.action.ActionController;


public class RespostaQuestionarioController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8125841864537062346L;
	private Integer soeSeq;
	private Short seqp;
	private List<QuestionarioVO> listaQuestionario;
	private List<RespostaQuestaoVO> listaRespostaQuestionario;
	private Integer qtnSeq;
	private String voltarPara;
	private Object itemSolicitacaoExames;
	
	private Boolean isHist;

	@EJB
	private IPesquisaExamesFacade  pesquisaExamesFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	
	
	public void iniciar() {
	 

		if(isHist == null){
			isHist = Boolean.FALSE;
		}
		if (this.soeSeq != null && this.seqp != null) {
			this.listaQuestionario = pesquisaExamesFacade.pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(this.soeSeq, this.seqp, this.isHist);
			if(isHist){ 
				this.itemSolicitacaoExames = examesFacade.buscaItemSolicitacaoExamePorIdHist(this.soeSeq, this.seqp);
			}else{
				this.itemSolicitacaoExames = examesFacade.obterItemSolicitacaoExamesComExame(this.soeSeq, this.seqp);
			}
		}
	
	}
	
	public void pesquisarRespostasPorQuestionario() {
		listaRespostaQuestionario = this.pesquisaExamesFacade.pesquisarRespostasPorQuestionarioEItemSolicitacaoExame(this.qtnSeq,this.soeSeq, this.seqp, this.isHist);
	}
	
	
	public String voltar() {
		return this.voltarPara;
	}
	
	
	public String cancelar() {
		return "cancelado";
	}
	
	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Integer getQtnSeq() {
		return qtnSeq;
	}

	public void setQtnSeq(Integer qtnSeq) {
		this.qtnSeq = qtnSeq;
	}

	public List<QuestionarioVO> getListaQuestionario() {
		return listaQuestionario;
	}

	public void setListaQuestionario(List<QuestionarioVO> listaQuestionario) {
		this.listaQuestionario = listaQuestionario;
	}

	public List<RespostaQuestaoVO> getListaRespostaQuestionario() {
		return listaRespostaQuestionario;
	}

	public void setListaRespostaQuestionario(
			List<RespostaQuestaoVO> listaRespostaQuestionario) {
		this.listaRespostaQuestionario = listaRespostaQuestionario;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setItemSolicitacaoExames(Object itemSolicitacaoExames) {
		this.itemSolicitacaoExames = itemSolicitacaoExames;
	}

	public Object getItemSolicitacaoExames() {
		return itemSolicitacaoExames;
	}

	public Boolean getIsHist() {
		return isHist;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}

}
