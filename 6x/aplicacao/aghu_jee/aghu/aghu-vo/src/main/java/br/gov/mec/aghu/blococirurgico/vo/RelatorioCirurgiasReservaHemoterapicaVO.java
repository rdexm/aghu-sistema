package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class RelatorioCirurgiasReservaHemoterapicaVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7295144028720484566L;
	
	private String req;//1
	private String hrInicioCirurgia;//2	
	private String prontuario;//3
	private String nome;//4
	private Date dtNascimento;//5
	private Short sala;//6
	private String hrFimCirurgia;//7
	private String origem;//8
	private String quarto;//9 		
	private String convenio;//10 
	private List<LinhaReportVO> cirurgiaoList;//11
	private List<LinhaReportVO> procedimentosList;//12
	private List<LinhaReportVO> componentesList;//13, 14, 15
	private List<LinhaReportVO> necessidadesList;//16
	private Integer crgSeq;
	

	public RelatorioCirurgiasReservaHemoterapicaVO() {
		
	}

	//Getters and Setters


	public String getReq() {
		return req;
	}


	public String getHrInicioCirurgia() {
		return hrInicioCirurgia;
	}


	public String getProntuario() {
		return prontuario;
	}


	public String getNome() {
		return nome;
	}


	public Date getDtNascimento() {
		return dtNascimento;
	}


	public Short getSala() {
		return sala;
	}


	public String getHrFimCirurgia() {
		return hrFimCirurgia;
	}


	public String getOrigem() {
		return origem;
	}


	public String getQuarto() {
		return quarto;
	}


	public String getConvenio() {
		return convenio;
	}


	public List<LinhaReportVO> getCirurgiaoList() {
		return cirurgiaoList;
	}


	public List<LinhaReportVO> getProcedimentosList() {
		return procedimentosList;
	}


	public List<LinhaReportVO> getComponentesList() {
		return componentesList;
	}


	public List<LinhaReportVO> getNecessidadesList() {
		return necessidadesList;
	}


	public void setReq(String req) {
		this.req = req;
	}


	public void setHrInicioCirurgia(String hrInicioCirurgia) {
		this.hrInicioCirurgia = hrInicioCirurgia;
	}


	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}


	public void setSala(Short sala) {
		this.sala = sala;
	}


	public void setHrFimCirurgia(String hrFimCirurgia) {
		this.hrFimCirurgia = hrFimCirurgia;
	}


	public void setOrigem(String origem) {
		this.origem = origem;
	}


	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}


	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}


	public void setCirurgiaoList(List<LinhaReportVO> cirurgiaoList) {
		this.cirurgiaoList = cirurgiaoList;
	}


	public void setProcedimentosList(List<LinhaReportVO> procedimentosList) {
		this.procedimentosList = procedimentosList;
	}


	public void setComponentesList(List<LinhaReportVO> componentesList) {
		this.componentesList = componentesList;
	}


	public void setNecessidadesList(List<LinhaReportVO> necessidadesList) {
		this.necessidadesList = necessidadesList;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}
	
	
}