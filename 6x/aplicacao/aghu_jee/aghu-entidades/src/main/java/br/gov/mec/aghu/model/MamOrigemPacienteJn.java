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

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity

@SequenceGenerator(name="mamOrpJn", sequenceName="AGH.mam_orp_jn_seq", allocationSize = 1)
@Table(name = "MAM_ORIGEM_PACIENTE_JN", schema = "AGH")
@Immutable
public class MamOrigemPacienteJn extends BaseJournal{


	private static final long serialVersionUID = 8250872904954852844L;
	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer serVincCodigoInclusao;
	private Integer serMatriculaInclusao;
	private Integer serVincCodigoAlteracao;
	private Integer serMatriculaAlteracao;


	public MamOrigemPacienteJn() {
		super();
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamOrpJn")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	//@GeneratedValue(strategy = GenerationType.AUTO, generator = "")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", unique = true, nullable = false)
	@NotNull
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	@NotNull
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_VIN_CODIGO_INCLUSAO", nullable = false, length = 3)
	public Integer getSerVincCodigoInclusao() {
		return serVincCodigoInclusao;
	}

	public void setSerVincCodigoInclusao(Integer serVincCodigoInclusao) {
		this.serVincCodigoInclusao = serVincCodigoInclusao;
	}

	@Column(name = "SER_MATRICULA_INCLUSAO", nullable = false, length = 7)
	public Integer getSerMatriculaInclusao() {
		return serMatriculaInclusao;
	}

	public void setSerMatriculaInclusao(Integer serMatriculaInclusao) {
		this.serMatriculaInclusao = serMatriculaInclusao;
	}

	@Column(name = "SER_VIN_CODIGO_ALTERACAO", length = 3)
	public Integer getSerVincCodigoAlteracao() {
		return serVincCodigoAlteracao;
	}

	public void setSerVincCodigoAlteracao(Integer serVincCodigoAlteracao) {
		this.serVincCodigoAlteracao = serVincCodigoAlteracao;
	}

	@Column(name = "SER_MATRICULA_ALTERACAO", length = 7)
	public Integer getSerMatriculaAlteracao() {
		return serMatriculaAlteracao;
	}

	public void setSerMatriculaAlteracao(Integer serMatriculaAlteracao) {
		this.serMatriculaAlteracao = serMatriculaAlteracao;
	}

	public enum Fields {
		SEQ("seq"), VERSION("version"), IND_SITUACAO("indSituacao"), DESCRICAO(
				"descricao"), CRIADO_EM("criadoEm"), ALTERADO_EM("AlteradoEm"), SER_MATRICULA_INCLUSAO(
				"serMatriculaInclusao"), SER_MATRICULA_ALTERACAO(
				"serMatriculaAlteracao"), SER_VIC_CODIGO_INCLUSAO(
				"serVicCodigoInclusao"), SER_VIC_CODIGO_ALTERACAO(
				"serVincCodigoAlteracao");
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
