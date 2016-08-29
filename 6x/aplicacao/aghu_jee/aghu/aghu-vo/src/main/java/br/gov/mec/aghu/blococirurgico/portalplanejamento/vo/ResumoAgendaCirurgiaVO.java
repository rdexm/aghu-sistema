package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Date;

public class ResumoAgendaCirurgiaVO implements Serializable {
	
	private static final long serialVersionUID = -8766087914318224836L;
	
	private Integer agdSeq;
	private Date dthrInclusao;
	private String pacNome;
	private Integer prontuario;
	private String descricaoProcedimento;
	private Date tempoSala;
	
	public enum Fields {
		
		AGENDA_SEQ("agdSeq"),
		DTHR_INCLUSAO("dthrInclusao"),
		PAC_NOME("pacNome"),
		PRONTUARIO("prontuario"),
		DESCRICAO_PROCED("descricaoProcedimento"),
		TEMPO_SALA("tempoSala");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	
	public Integer getAgdSeq() {
		return agdSeq;
	}
	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}
	public Date getDthrInclusao() {
		return dthrInclusao;
	}
	public void setDthrInclusao(Date dthrInclusao) {
		this.dthrInclusao = dthrInclusao;
	}
	public String getPacNome() {
		return pacNome;
	}
	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}
	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}
	public Date getTempoSala() {
		return tempoSala;
	}
	public void setTempoSala(Date tempoSala) {
		this.tempoSala = tempoSala;
	}
	
}
