package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigPssSq1", sequenceName = "SIG_PSS_SQ1", allocationSize = 1)
@Table(name = "sig_passos", schema = "agh")
public class SigPassos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -6578077249075999249L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private String descricao;
	private Integer version;
	private Boolean indParaProcessamento;
	private Double ordemExecucao;

	public SigPassos() {
	}

	public SigPassos(Integer seq, Date criadoEm, RapServidores rapServidores, String descricao, Boolean indParaProcessamento) {
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.indParaProcessamento = indParaProcessamento;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigPssSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "descricao", nullable = false, length = 100)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "VERSION")
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "ind_para_processamento", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndParaProcessamento() {
		return indParaProcessamento;
	}

	public void setIndParaProcessamento(Boolean indParaProcessamento) {
		this.indParaProcessamento = indParaProcessamento;
	}
	
	@Column(name = "ordem_execucao", nullable = false, precision = 5, scale = 2)
	public Double getOrdemExecucao() {
		return ordemExecucao;
	}

	public void setOrdemExecucao(Double ordemExecucao) {
		this.ordemExecucao = ordemExecucao;
	}

	public enum Fields {

		SEQ("seq"), 
		CRIADO_EM("criadoEm"), 
		SERVIDOR_RESPONSAVEL("rapServidores"), 
		DESCRICAO("descricao"), 
		INDICA_PARA_PROCESSAMENTO("indParaProcessamento"), 
		ORDEM_EXECUCAO ("ordemExecucao");

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
	public boolean equals(Object other) {
		if (!(other instanceof SigPassos)) {
			return false;
		}
		SigPassos castOther = (SigPassos) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
}
