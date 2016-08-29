package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="afaGrupoCompoSeq", sequenceName="AGH.AFA_GCN_JN_SEQ", allocationSize = 1)
@Table(name = "AFA_GRUPO_COMPON_NPTS_JN", schema = "AGH")
public class AfaGrupoComponNptJn extends BaseJournal {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4859348535144363348L;
	private Short seq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private String observacao;

	public AfaGrupoComponNptJn() {
	}

	public AfaGrupoComponNptJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq) {
		this.seq = seq;
	}

	public AfaGrupoComponNptJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq, Integer serMatricula,
			Short serVinCodigo, String descricao, Date criadoEm, String indSituacao, String observacao) {
		this.seq = seq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.observacao = observacao;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "afaGrupoCompoSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
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

	@Column(name = "DESCRICAO", length = 60)
	@Length(max = 60)
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
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "OBSERVACAO", length = 500)
	@Length(max = 500)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public enum Fields {

		SEQ("seq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		OBSERVACAO("observacao");

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
