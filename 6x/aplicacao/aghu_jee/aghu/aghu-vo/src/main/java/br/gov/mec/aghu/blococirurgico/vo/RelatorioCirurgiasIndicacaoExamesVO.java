package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class RelatorioCirurgiasIndicacaoExamesVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7295144028720484566L;
	
	private Integer crgSeq;
	private String req;
	private String hrInicioCirurgia;	
	private String hrFimCirurgia;
	private Short sala;
	private String prontuario;
	private String nome;
	private String convenio;
	private String quarto; 		
	private Short agenda;
	private String exame;
	private List<LinhaReportVO> cirurgiaoList;
	private List<LinhaReportVO> procedimentosList;
	private List<LinhaReportVO> examesList;
	
	
	public RelatorioCirurgiasIndicacaoExamesVO() {
		
	}


	public String getReq() {
		return req;
	}


	public void setReq(String req) {
		this.req = req;
	}


	public String getHrInicioCirurgia() {
		return hrInicioCirurgia;
	}


	public void setHrInicioCirurgia(String hrInicioCirurgia) {
		this.hrInicioCirurgia = hrInicioCirurgia;
	}


	public String getHrFimCirurgia() {
		return hrFimCirurgia;
	}


	public void setHrFimCirurgia(String hrFimCirurgia) {
		this.hrFimCirurgia = hrFimCirurgia;
	}


	public Short getSala() {
		return sala;
	}


	public void setSala(Short sala) {
		this.sala = sala;
	}


	public String getProntuario() {
		return prontuario;
	}


	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getConvenio() {
		return convenio;
	}


	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}


	public String getQuarto() {
		return quarto;
	}


	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}


	public String getExame() {
		return exame;
	}


	public void setExame(String exame) {
		this.exame = exame;
	}


	public List<LinhaReportVO> getCirurgiaoList() {
		return cirurgiaoList;
	}


	public void setCirurgiaoList(List<LinhaReportVO> cirurgiaoList) {
		this.cirurgiaoList = cirurgiaoList;
	}


	public List<LinhaReportVO> getProcedimentosList() {
		return procedimentosList;
	}


	public void setProcedimentosList(List<LinhaReportVO> procedimentosList) {
		this.procedimentosList = procedimentosList;
	}


	public Integer getCrgSeq() {
		return crgSeq;
	}


	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}


	public List<LinhaReportVO> getExamesList() {
		return examesList;
	}


	public void setExamesList(List<LinhaReportVO> examesList) {
		this.examesList = examesList;
	}


	public Short getAgenda() {
		return agenda;
	}


	public void setAgenda(Short agenda) {
		this.agenda = agenda;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((req == null) ? 0 : req.hashCode());
		result = prime * result
				+ ((req == null) ? 0 : req.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		RelatorioCirurgiasIndicacaoExamesVO other = (RelatorioCirurgiasIndicacaoExamesVO) obj;
		if (req == null) {
			if (other.req != null){
				return false;
			}
		} else if (!req.equals(other.req)){
			return false;
		}
		if (req == null) {
			if (other.req != null){
				return false;
			}
		} else if (!req.equals(other.req)){
			return false;
		}
		return true;
	}
	
	
	
}