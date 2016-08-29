package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class CirurgiasCanceladasAgendaMedicoVO implements BaseBean {
	
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 7102047377830610822L;
	
	private Integer agdSeq;
	private Integer prontuario;
	private String pacProntuario;
	private String nome; 
	private String pciDescricao;
	private String motivoCancelamento;
	private Date dtInclusao;
	
	
	public enum Fields {
		
		AGD_SEQ ("agdSeq"),
		PRONTUARIO ("prontuario"),
		NOME ("nome"),
		PCI_DESCRICAO ("pciDescricao"),
		MOTIVO_CANCELAMENTO ("motivoCancelamento"),
		DT_INCLUSAO ("dtInclusao")		
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}
	
	public Integer getAgdSeq() {
		return agdSeq;
	}

	public String getPciDescricao() {
		return pciDescricao;
	}

	public void setPciDescricao(String pciDescricao) {
		this.pciDescricao = pciDescricao;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}


	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}	

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDtInclusao() {
		return dtInclusao;
	}

	public void setDtInclusao(Date dtInclusao) {
		this.dtInclusao = dtInclusao;
	}
	
	public String getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(String pacProntuario) {
		this.pacProntuario = pacProntuario;
	}
}
	
