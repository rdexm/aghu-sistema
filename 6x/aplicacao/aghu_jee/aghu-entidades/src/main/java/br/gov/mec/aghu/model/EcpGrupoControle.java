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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoGrupoControle;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "ECP_GRUPO_CONTROLES", schema = "AGH")
@SequenceGenerator(name = "ecpGcoSq1", sequenceName = "AGH.ECP_GCO_SQ1", allocationSize = 1)
public class EcpGrupoControle extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3543494171046922775L;
	private Integer seq;
	private String descricao;
	private Short ordem;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Integer version;
	private RapServidores servidor;
	private DominioTipoGrupoControle tipo;

	// construtores
	public EcpGrupoControle() {
	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 8, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ecpGcoSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	@Length(max = 60, message = "Descrição deve ter até 60 caracteres")
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ORDEM", length = 3, nullable = false)
	public Short getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}


	@Column(name = "SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "TIPO", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoGrupoControle getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoGrupoControle tipo) {
		this.tipo = tipo;
	}

	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EcpGrupoControle)) {
			return false;
		}
		EcpGrupoControle castOther = (EcpGrupoControle) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), DESCRICAO("descricao"), ORDEM("ordem"), 
		SITUACAO("situacao"), CRIADO_EM("criadoEm"), SERVIDOR(
				"servidor"), TIPO("tipo") ;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}