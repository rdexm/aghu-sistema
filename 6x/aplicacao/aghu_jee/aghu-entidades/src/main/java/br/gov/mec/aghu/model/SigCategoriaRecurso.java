package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCatrSq1", sequenceName = "SIG_CATR_SQ1", allocationSize = 1)
@Table(name = "SIG_CATEGORIA_RECURSOS", schema = "AGH")
public class SigCategoriaRecurso extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 6861878683792306737L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;
	private String descricao;
	private DominioSituacao indSituacao;

	
	public SigCategoriaRecurso(){
	}
	
	public SigCategoriaRecurso(Integer seq) {
		super();
		this.seq = seq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCatrSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
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

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public enum Fields {

		SEQ("seq"), CRIADO_EM("criadoEm"), SERVIDOR_RESPONSAVEL("rapServidores"), IND_SITUACAO("indSituacao"), DESCRICAO("descricao");

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
		if (!(other instanceof SigCategoriaRecurso)) {
			return false;
		}
		SigCategoriaRecurso castOther = (SigCategoriaRecurso) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
}
