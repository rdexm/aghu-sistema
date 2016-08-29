package br.gov.mec.aghu.exames.sismama.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.sismama.business.ISismamaFacade;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.core.action.ActionController;

public class VerificarQuestoesSismamaApBiopsiaController extends ActionController {

	private static final long serialVersionUID = 2149511466024586215L;

	private Integer prontuario;
	private String nomePaciente;
	private String descricaoUsualExame;
	private Integer soeSeq;
	private Short seqp;
	private Map<String, Object> respostas = new HashMap<String, Object>();
	private Boolean isHist = Boolean.FALSE;

	private String voltarPara;

	@EJB
	private ISismamaFacade sismamaFacade;

	@EJB
	private IExamesFacade examesFacade;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {

		if (soeSeq != null && seqp != null) {
			if (isHist != null && isHist) {
				preencheDadosDaSolicitacaoHistorico();
			} else {
				preencheDadosDaSolicitacao();
			}
			respostas = this.sismamaFacade.recuperarRespostasBiopsisa(soeSeq, seqp, isHist);
		}
	}

	private void preencheDadosDaSolicitacaoHistorico() {
		AelItemSolicExameHist itemSolEx = this.examesFacade.buscaItemSolicitacaoExamePorIdHist(soeSeq, seqp);
		AelSolicitacaoExamesHist solEx = itemSolEx.getSolicitacaoExame();
		if (solEx.getAtendimento() != null) {
			nomePaciente = solEx.getAtendimento().getPaciente().getNome();
			prontuario = solEx.getAtendimento().getPaciente().getProntuario();
		}
		descricaoUsualExame = itemSolEx.getExame().getDescricaoUsual();

	}

	private void preencheDadosDaSolicitacao() {
		AelItemSolicitacaoExames itemSolEx = this.examesFacade.obterItemSolicitacaoExamesComAtendimento(soeSeq, seqp);
		AelSolicitacaoExames solEx = itemSolEx.getSolicitacaoExame();
		if (solEx.getAtendimento() != null) {
			nomePaciente = solEx.getAtendimento().getPaciente().getNome();
			prontuario = solEx.getAtendimento().getPaciente().getProntuario();
		}
		descricaoUsualExame = itemSolEx.getExame().getDescricaoUsual();
	}

	public String voltar() {
		return voltarPara;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getDescricaoUsualExame() {
		return descricaoUsualExame;
	}

	public void setDescricaoUsualExame(String descricaoUsualExame) {
		this.descricaoUsualExame = descricaoUsualExame;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Map<String, Object> getRespostas() {
		return respostas;
	}

	public void setRespostas(Map<String, Object> respostas) {
		this.respostas = respostas;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getIsHist() {
		return isHist;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}
}
