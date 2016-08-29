package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoAnamnese;


@Entity
@Table(name = "MPM_ANAMNESES_JN", schema = "AGH")
@SequenceGenerator(name = "mpmAnaJnSeq", sequenceName = "AGH.mpm_ana_jn_sq1", allocationSize = 1)
@Immutable
public class MpmAnamnesesJn extends BaseJournal implements java.io.Serializable {

	private static final long serialVersionUID = 84162638996927628L;

	private Long seq;
	private Date dthrCriacao;
	private Date dthrAlteracao;
	private DominioIndPendenteAmbulatorio pendente;
	private Integer mamTipoItemAnamneses;
	private String descricao;
	private Integer matriculaServidor;
	private Short vinCodigoServidor;
	private Integer aghAtendimentos;
	private DominioSituacaoAnamnese situacao;

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmAnaJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ")
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CRIACAO", nullable = false, length = 7)
	public Date getDthrCriacao() {
		return this.dthrCriacao;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ALTERACAO", length = 7)
	public Date getDthrAlteracao() {
		return this.dthrAlteracao;
	}

	public void setDthrAlteracao(Date dthrAlteracao) {
		this.dthrAlteracao = dthrAlteracao;
	}

	@Column(name = "IND_PENDENTE")
	@Enumerated(EnumType.STRING)
	public DominioIndPendenteAmbulatorio getPendente() {
		return this.pendente;
	}

	public void setPendente(DominioIndPendenteAmbulatorio pendente) {
		this.pendente = pendente;
	}

	@Column(name = "TIN_SEQ")
	public Integer getMamTipoItemAnamneses() {
		return mamTipoItemAnamneses;
	}

	public void setMamTipoItemAnamneses(Integer mamTipoItemAnamneses) {
		this.mamTipoItemAnamneses = mamTipoItemAnamneses;
	}

	@Column(name = "DESCRICAO", length = 12000)
	@Length(max = 12000)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getMatriculaServidor() {
		return this.matriculaServidor;
	}

	public void setMatriculaServidor(Integer matricula) {
		this.matriculaServidor = matricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getVinCodigoServidor() {
		return this.vinCodigoServidor;
	}

	public void setVinCodigoServidor(Short vinCodigo) {
		this.vinCodigoServidor = vinCodigo;
	}

	@Column(name = "ATD_SEQ")
	public Integer getAghAtendimentos() {
		return aghAtendimentos;
	}

	public void setAghAtendimentos(Integer aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}

	@Column(name = "IND_USO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoAnamnese getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoAnamnese situacao) {
		this.situacao = situacao;
	}

	public enum Fields {
		SEQ_ANAMNESE("seq");
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