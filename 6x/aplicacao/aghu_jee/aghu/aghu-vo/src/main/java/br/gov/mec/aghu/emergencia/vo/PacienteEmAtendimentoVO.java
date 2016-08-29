package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;

public class PacienteEmAtendimentoVO implements Serializable {
	private static final long serialVersionUID = -4101227428458821214L;

	// Emergencia
	private Integer pacCodigo;
	private Long trgSeq;
	private Short segSeq;
	private DominioTipoMovimento ultTipoMvto;
	private DominioPacAtendimento indPacAtendimento;
	private Date dthrUltMvto;
	private Short trgUnfSeq;
	private String segDescricao;
	private Integer conNumero;
	private Boolean devePintar = Boolean.FALSE;
	
	// Monolito
	private Date dtConsulta;
	private Integer atdSeq;
	private Integer atdSerMatricula;
	private Short atdSerVinCodigo;
	private Short atdUnfSeq;
	private Short espSeq;
	private String espSigla;
	private String pacNome;
	private Date dtNascimentoPac;
	private Integer prontuarioPac;
	private Short teiSeqp;
	private Integer idade;
	private String nomeResponsavel;
	
	// Icones
	private String silkColuna10;
	private String silkColuna11;
	private String silkColuna12;
	private String silkColuna13;
	private String silkColuna14;
	
	// Labels
	private String labelColuna10;
	private String labelColuna11;
	private String labelColuna12;
	
	public enum Fields {
		PAC_CODIGO("pacCodigo"),
		TRG_SEQ("trgSeq"),
		SEG_SEQ("segSeq"),
		ULT_TIPO_MVTO("ultTipoMvto"),
		IND_PAC_ATENDIMENTO("indPacAtendimento"),
		DT_HR_ULT_MVTO("dthrUltMvto"),
		TRG_UNF_SEQ("trgUnfSeq"),
		SEG_DESCRICAO("segDescricao"),
		CON_NUMERO("conNumero"),
		PAC_NOME("pacNome"),
		TEI_SEQP("teiSeqp");
		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	public Short getSegSeq() {
		return segSeq;
	}

	public void setSegSeq(Short segSeq) {
		this.segSeq = segSeq;
	}

	public DominioTipoMovimento getUltTipoMvto() {
		return ultTipoMvto;
	}

	public void setUltTipoMvto(DominioTipoMovimento ultTipoMvto) {
		this.ultTipoMvto = ultTipoMvto;
	}

	public DominioPacAtendimento getIndPacAtendimento() {
		return indPacAtendimento;
	}

	public void setIndPacAtendimento(DominioPacAtendimento indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}

	public Date getDthrUltMvto() {
		return dthrUltMvto;
	}

	public void setDthrUltMvto(Date dthrUltMvto) {
		this.dthrUltMvto = dthrUltMvto;
	}

	public Short getTrgUnfSeq() {
		return trgUnfSeq;
	}

	public void setTrgUnfSeq(Short trgUnfSeq) {
		this.trgUnfSeq = trgUnfSeq;
	}

	public String getSegDescricao() {
		return segDescricao;
	}

	public void setSegDescricao(String segDescricao) {
		this.segDescricao = segDescricao;
	}

	public Date getDtConsulta() {
		return dtConsulta;
	}

	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getAtdSerMatricula() {
		return atdSerMatricula;
	}

	public void setAtdSerMatricula(Integer atdSerMatricula) {
		this.atdSerMatricula = atdSerMatricula;
	}

	public Short getAtdSerVinCodigo() {
		return atdSerVinCodigo;
	}

	public void setAtdSerVinCodigo(Short atdSerVinCodigo) {
		this.atdSerVinCodigo = atdSerVinCodigo;
	}

	public Short getAtdUnfSeq() {
		return atdUnfSeq;
	}

	public void setAtdUnfSeq(Short atdUnfSeq) {
		this.atdUnfSeq = atdUnfSeq;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public String getEspSigla() {
		return espSigla;
	}

	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Date getDtNascimentoPac() {
		return dtNascimentoPac;
	}

	public void setDtNascimentoPac(Date dtNascimentoPac) {
		this.dtNascimentoPac = dtNascimentoPac;
	}

	public Integer getProntuarioPac() {
		return prontuarioPac;
	}

	public void setProntuarioPac(Integer prontuarioPac) {
		this.prontuarioPac = prontuarioPac;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Short getTeiSeqp() {
		return teiSeqp;
	}

	public void setTeiSeqp(Short teiSeqp) {
		this.teiSeqp = teiSeqp;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
	
	public boolean isDevePintar() {
		return devePintar;
	}

	public void setDevePintar(boolean devePintar) {
		this.devePintar = devePintar;
	}

	public String getSilkColuna10() {
		return silkColuna10;
	}

	public void setSilkColuna10(String silkColuna10) {
		this.silkColuna10 = silkColuna10;
	}

	public String getSilkColuna11() {
		return silkColuna11;
	}

	public void setSilkColuna11(String silkColuna11) {
		this.silkColuna11 = silkColuna11;
	}

	public String getSilkColuna12() {
		return silkColuna12;
	}

	public void setSilkColuna12(String silkColuna12) {
		this.silkColuna12 = silkColuna12;
	}

	public String getSilkColuna13() {
		return silkColuna13;
	}

	public void setSilkColuna13(String silkColuna13) {
		this.silkColuna13 = silkColuna13;
	}

	public String getSilkColuna14() {
		return silkColuna14;
	}

	public void setSilkColuna14(String silkColuna14) {
		this.silkColuna14 = silkColuna14;
	}

	public String getLabelColuna10() {
		return labelColuna10;
	}

	public void setLabelColuna10(String labelColuna10) {
		this.labelColuna10 = labelColuna10;
	}

	public String getLabelColuna11() {
		return labelColuna11;
	}

	public void setLabelColuna11(String labelColuna11) {
		this.labelColuna11 = labelColuna11;
	}

	public String getLabelColuna12() {
		return labelColuna12;
	}

	public void setLabelColuna12(String labelColuna12) {
		this.labelColuna12 = labelColuna12;
	}
	
}
