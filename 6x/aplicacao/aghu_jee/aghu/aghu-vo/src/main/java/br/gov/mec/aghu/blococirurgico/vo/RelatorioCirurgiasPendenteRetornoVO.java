package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;

public class RelatorioCirurgiasPendenteRetornoVO {
	
	private Date data;
	private Short numeroAgenda;
	private Integer crgSeq;
	private DominioOrigemPacienteCirurgia origemPacCirg;
	private String nome;
	private Integer prontuario;
	private String equipe;
	private String espSigla;
	private String agendadoPor;
	private String dc;		// indica se possui descrição cir. / pdt
	private String pesNome;
	private String pesNomeUsual;
	private String strProntuario;
	private String origem;
	private Integer crgSerMatricula;
	private Short crgSerVinCodigo;

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Short getNumeroAgenda() {
		return numeroAgenda;
	}

	public void setNumeroAgenda(Short numeroAgenda) {
		this.numeroAgenda = numeroAgenda;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public DominioOrigemPacienteCirurgia getOrigemPacCirg() {
		return origemPacCirg;
	}

	public void setOrigemPacCirg(DominioOrigemPacienteCirurgia origemPacCirg) {
		this.origemPacCirg = origemPacCirg;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	public String getEspSigla() {
		return espSigla;
	}

	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}

	public String getAgendadoPor() {
		return agendadoPor;
	}

	public void setAgendadoPor(String agendadoPor) {
		this.agendadoPor = agendadoPor;
	}

	public String getDc() {
		return dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}
	
	public String getPesNome() {
		return pesNome;
	}

	public void setPesNome(String pesNome) {
		this.pesNome = pesNome;
	}

	public String getPesNomeUsual() {
		return pesNomeUsual;
	}

	public void setPesNomeUsual(String pesNomeUsual) {
		this.pesNomeUsual = pesNomeUsual;
	}
	
	public String getStrProntuario() {
		return strProntuario;
	}

	public void setStrProntuario(String strProntuario) {
		this.strProntuario = strProntuario;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Integer getCrgSerMatricula() {
		return crgSerMatricula;
	}

	public void setCrgSerMatricula(Integer crgSerMatricula) {
		this.crgSerMatricula = crgSerMatricula;
	}

	public Short getCrgSerVinCodigo() {
		return crgSerVinCodigo;
	}

	public void setCrgSerVinCodigo(Short crgSerVinCodigo) {
		this.crgSerVinCodigo = crgSerVinCodigo;
	}


	public enum Fields {
		DATA("data"),
		NUMERO_AGENDA("numeroAgenda"),
		CRG_SEQ("crgSeq"),
		ORIGEM_PAC_CIRG("origemPacCirg"),
		NOME("nome"),
		PRONTUARIO("prontuario"),
		EQUIPE("equipe"),
		ESP_SIGLA("espSigla"),
		DC("dc"),
		PES_NOME("pesNome"),
		PES_NOME_USUAL("pesNomeUsual"),
		CRG_SER_MATRICULA("crgSerMatricula"),
		CRG_SER_VIN_CODIGO("crgSerVinCodigo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hashCode = new HashCodeBuilder();
		hashCode
			.append(this.getData())
			.append(this.getNumeroAgenda())
			.append(this.getOrigemPacCirg())
			.append(this.getEquipe())
			.append(this.getEspSigla())
			.append(this.getAgendadoPor())
			.append(this.getNome())
			.append(this.getProntuario())
			.append(this.getDc());
		
		return hashCode.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RelatorioCirurgiasPendenteRetornoVO)) {
			return false;
		}
		RelatorioCirurgiasPendenteRetornoVO other = (RelatorioCirurgiasPendenteRetornoVO) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder
			.append(this.getData(), other.getData())
			.append(this.getNumeroAgenda(), other.getNumeroAgenda())
			.append(this.getOrigemPacCirg(), other.getOrigemPacCirg())
			.append(this.getEquipe(), other.getEquipe())
			.append(this.getEspSigla(), other.getEspSigla())
			.append(this.getAgendadoPor(), other.getAgendadoPor())
			.append(this.getNome(), other.getNome())
			.append(this.getProntuario(), other.getProntuario())
			.append(this.getDc(), other.getDc());

		return equalsBuilder.isEquals();
	}	

}
