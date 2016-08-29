package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aghMamPcrJnSeq", sequenceName="AGH.mam_uan_jn_seq", allocationSize = 1)
@Table(name = "MAM_UNID_ATENDEM_JN", schema = "AGH")
@Immutable
public class MamUnidAtendemJn extends BaseJournal {

	private static final long serialVersionUID = 1693982787084871104L;

	private MamUnidAtendem mamUnidAtendem;
	private Short unfSeq;
	private String descricao;
	private Date criadoEm;	
	private String micNome;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer pcrSeq;
	
	private DominioSituacao indSituacao;
	private Boolean indMenorResponsavel;
	private Boolean indTriagem;
	private Boolean indDivideIdade;
	private Boolean indRecepcao;
	private Boolean indObrOrgPaciente;
	
	public MamUnidAtendemJn() {
	}

	public MamUnidAtendemJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public MamUnidAtendemJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short unfSeq, String descricao,
			Date criadoEm, DominioSituacao indSituacao, Boolean indTriagem, Boolean indDivideIdade, Boolean indRecepcao, String micNome,
			Integer serMatricula, Short serVinCodigo) {
		this.unfSeq = unfSeq;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.indTriagem = indTriagem;
		this.indDivideIdade = indDivideIdade;
		this.indRecepcao = indRecepcao;
		this.micNome = micNome;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghMamPcrJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "UNF_SEQ", nullable = false)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "DESCRICAO", length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_TRIAGEM", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTriagem() {
		return this.indTriagem;
	}

	public void setIndTriagem(Boolean indTriagem) {
		this.indTriagem = indTriagem;
	}

	@Column(name = "IND_DIVIDE_IDADE", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDivideIdade() {
		return this.indDivideIdade;
	}

	public void setIndDivideIdade(Boolean indDivideIdade) {
		this.indDivideIdade = indDivideIdade;
	}

	@Column(name = "IND_RECEPCAO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRecepcao() {
		return this.indRecepcao;
	}

	public void setIndRecepcao(Boolean indRecepcao) {
		this.indRecepcao = indRecepcao;
	}
	
	@Column(name = "IND_OBR_ORG_PACIENTE", nullable = false, length = 1)	
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndObrOrgPaciente() {
		return indObrOrgPaciente;
	}

	public void setIndObrOrgPaciente(Boolean indObrOrgPaciente) {
		this.indObrOrgPaciente = indObrOrgPaciente;
	}
	
	@Column(name = "IND_MENOR_RESPONSAVEL", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndMenorResponsavel() {
		return indMenorResponsavel;
	}

	public void setIndMenorResponsavel(Boolean indMenorResponsavel) {
		this.indMenorResponsavel = indMenorResponsavel;
	}	

	@Column(name = "MIC_NOME", length = 50)
	@Length(max = 50)
	public String getMicNome() {
		return this.micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UAN_UNF_SEQ")
	public MamUnidAtendem getMamUnidAtendem() {
		return this.mamUnidAtendem;
	}

	public void setMamUnidAtendem(MamUnidAtendem mamUnidAtendem) {
		this.mamUnidAtendem = mamUnidAtendem;
	}
	
	@Column(name = "PCR_SEQ", insertable = false, updatable = false)
	public Integer getPcrSeq() {
		return pcrSeq;
	}

	public void setPcrSeq(Integer pcrSeq) {
		this.pcrSeq = pcrSeq;
	}

	public enum Fields {

		UNF_SEQ("unfSeq"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		IND_TRIAGEM("indTriagem"),
		IND_DIVIDE_IDADE("indDivideIdade"),
		IND_RECEPCAO("indRecepcao"),
		MIC_NOME("micNome"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		PCR_SEQ("pcrSeq"),
		IND_MENOR_RESPONSAVEL("indMenorResponsavel");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
}
