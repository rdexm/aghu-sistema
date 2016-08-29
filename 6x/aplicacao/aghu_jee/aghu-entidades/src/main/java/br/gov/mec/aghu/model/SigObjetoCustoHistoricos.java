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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigHtoSq1", sequenceName = "SIG_HTO_SQ1", allocationSize = 1)
@Table(name = "SIG_OBJETO_CUSTO_HISTORICOS", schema = "AGH")
public class SigObjetoCustoHistoricos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 4004658566301774032L;
	
	private Integer seq;
	private SigObjetoCustoVersoes sigObjetoCustoVersoes;
	private SigAtividades sigAtividades;
	private String componente;
	private String acao;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigHtoSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCV_SEQ", referencedColumnName = "SEQ")
	public SigObjetoCustoVersoes getSigObjetoCustoVersoes() {
		return sigObjetoCustoVersoes;
	}

	public void setSigObjetoCustoVersoes(SigObjetoCustoVersoes sigObjetoCustoVersoes) {
		this.sigObjetoCustoVersoes = sigObjetoCustoVersoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVD_SEQ", referencedColumnName = "SEQ")
	public SigAtividades getSigAtividades() {
		return sigAtividades;
	}

	public void setSigAtividades(SigAtividades sigAtividades) {
		this.sigAtividades = sigAtividades;
	}

	@Column(name = "COMPONENTE", nullable = false, length = 30)
	public String getComponente() {
		return componente;
	}

	public void setComponente(String componente) {
		this.componente = componente;
	}

	@Column(name = "ACAO", nullable = false, length = 2000)
	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
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
		SIG_OBJETO_CUSTO_VERSOES("sigObjetoCustoVersoes"),
		SIG_ATIVIDADES("sigAtividades"),
		COMPONENTE("componente"),
		ACAO("acao"),
		CRIADO_EM("criadoEm"),
		RAP_SERVIDORES("rapServidores");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigObjetoCustoHistoricos)) {
			return false;
		}
		SigObjetoCustoHistoricos castOther = (SigObjetoCustoHistoricos) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
