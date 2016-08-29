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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mci_toi_jn_seq", sequenceName="AGH.MCI_TOI_JN_SEQ", allocationSize = 1)
@Table(name = "MCI_TOPOGRAFIA_INFECCOES_JN", schema = "AGH")
@Immutable
public class MciTopografiaInfeccaoJn extends BaseJournal {

	private static final long serialVersionUID = -1777910558487211358L;

	private Short seq;
	private String descricao;
	private Boolean indSupervisao;
	private DominioSituacao indSituacao;
	private Boolean indPacInfectado;
	private Boolean indContaInfecMensal;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date alteradoEm;
	private Integer serMatriculaMovimentado;
	private Short serVinCodigoMovimentado;

	public MciTopografiaInfeccaoJn() {
	}

	public MciTopografiaInfeccaoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq) {
		this.seq = seq;
	}

	public MciTopografiaInfeccaoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq, String descricao,
			Boolean indSupervisao, DominioSituacao indSituacao, Boolean indPacInfectado, Boolean indContaInfecMensal, Date criadoEm,
			Integer serMatricula, Short serVinCodigo) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSupervisao = indSupervisao;
		this.indSituacao = indSituacao;
		this.indPacInfectado = indPacInfectado;
		this.indContaInfecMensal = indContaInfecMensal;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mci_toi_jn_seq")
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

	@Column(name = "DESCRICAO", length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SUPERVISAO", length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSupervisao() {
		return this.indSupervisao;
	}

	public void setIndSupervisao(Boolean indSupervisao) {
		this.indSupervisao = indSupervisao;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_PAC_INFECTADO", nullable = false, length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPacInfectado() {
		return this.indPacInfectado;
	}

	public void setIndPacInfectado(Boolean indPacInfectado) {
		this.indPacInfectado = indPacInfectado;
	}

	@Column(name = "IND_CONTA_INFEC_MENSAL", nullable = false, length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndContaInfecMensal() {
		return this.indContaInfecMensal;
	}

	public void setIndContaInfecMensal(Boolean indContaInfecMensal) {
		this.indContaInfecMensal = indContaInfecMensal;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_MATRICULA_MOVIMENTADO")
	public Integer getSerMatriculaMovimentado() {
		return serMatriculaMovimentado;
	}

	public void setSerMatriculaMovimentado(Integer serMatriculaMovimentado) {
		this.serMatriculaMovimentado = serMatriculaMovimentado;
	}

	@Column(name = "SER_VIN_CODIGO_MOVIMENTADO")
	public Short getSerVinCodigoMovimentado() {
		return serVinCodigoMovimentado;
	}

	public void setSerVinCodigoMovimentado(Short serVinCodigoMovimentado) {
		this.serVinCodigoMovimentado = serVinCodigoMovimentado;
	}

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SUPERVISAO("indSupervisao"),
		IND_SITUACAO("indSituacao"),
		IND_PAC_INFECTADO("indPacInfectado"),
		IND_CONTA_INFEC_MENSAL("indContaInfecMensal"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA_MOVIMENTADO("serMatriculaMovimentado"),
		SER_VIN_CODIGO_MOVIMENTADO("serVinCodigoMovimentado");

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
