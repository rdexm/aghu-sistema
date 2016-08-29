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

import br.gov.mec.aghu.dominio.DominioTipoAtividade;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigEapSq1", sequenceName = "SIG_EAP_SQ1", allocationSize = 1)
@Table(name = "SIG_ESCALA_PESSOAS", schema = "AGH")
public class SigEscalaPessoa extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 5770396526806653797L;

	private Integer seq;
	private FccCentroCustos fccCentroCustos;
	private DominioTipoAtividade tipoAtividade;
	private Integer percentual;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigEapSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cct_codigo", nullable = false, referencedColumnName = "codigo")
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@Column(name = "TIPO_ATIVIDADE", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoAtividade getTipoAtividade() {
		return this.tipoAtividade;
	}

	public void setTipoAtividade(DominioTipoAtividade tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}

	@Column(name = "PERCENTUAL", nullable = false)
	public Integer getPercentual() {
		return percentual;
	}

	public void setPercentual(Integer percentual) {
		this.percentual = percentual;
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
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {

		SEQ("seq"),
		CENTRO_CUSTO("fccCentroCustos"),
		TIPO_ATIVIDADE("tipoAtividade"),
		PERCENTUAL("percentual"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("servidor");

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
		if (!(other instanceof SigEscalaPessoa)) {
			return false;
		}
		SigEscalaPessoa castOther = (SigEscalaPessoa) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
