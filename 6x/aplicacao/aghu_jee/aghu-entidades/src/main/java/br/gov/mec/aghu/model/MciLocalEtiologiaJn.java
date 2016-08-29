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

import br.gov.mec.aghu.dominio.DominioFormaContabilizacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aghMciLetJnSeq", sequenceName="AGH.MCI_LET_JN_SEQ", allocationSize = 1)
@Table(name = "MCI_LOCAL_ETIOLOGIAS_JN", schema = "AGH")
@Immutable
public class MciLocalEtiologiaJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -5055550095096216968L;
	
	private String einTipo;
	private Short unfSeq;
	private DominioFormaContabilizacao indFormaContabilizacao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date alteradoEm;
	private Integer serMatriculaMovimentado;
	private Short serVinCodigoMovimentado;

	public MciLocalEtiologiaJn() {
	}

	public MciLocalEtiologiaJn(String einTipo, Short unfSeq, DominioFormaContabilizacao indFormaContabilizacao, DominioSituacao indSituacao,
			Date criadoEm, Integer serMatricula, Short serVinCodigo, Date alteradoEm, Integer serMatriculaMovimentado, Short serVinCodigoMovimentado) {
		this.einTipo = einTipo;
		this.unfSeq = unfSeq;
		this.indFormaContabilizacao = indFormaContabilizacao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.alteradoEm = alteradoEm;
		this.serMatriculaMovimentado = serMatriculaMovimentado;
		this.serVinCodigoMovimentado = serVinCodigoMovimentado;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghMciLetJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	

	@Column(name = "EIN_TIPO", nullable = false, length = 2)
	@Length(max = 2)
	public String getEinTipo() {
		return this.einTipo;
	}

	public void setEinTipo(String einTipo) {
		this.einTipo = einTipo;
	}

	@Column(name = "UNF_SEQ", nullable = false)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "IND_FORMA_CONTABILIZACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioFormaContabilizacao getIndFormaContabilizacao() {
		return this.indFormaContabilizacao;
	}

	public void setIndFormaContabilizacao(DominioFormaContabilizacao indFormaContabilizacao) {
		this.indFormaContabilizacao = indFormaContabilizacao;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
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
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
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

		EIN_TIPO("einTipo"),
		UNF_SEQ("unfSeq"),
		IND_FORMA_CONTABILIZACAO("indFormaContabilizacao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

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
