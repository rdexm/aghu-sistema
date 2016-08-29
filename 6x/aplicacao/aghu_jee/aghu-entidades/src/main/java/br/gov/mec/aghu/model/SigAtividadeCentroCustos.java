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
@SequenceGenerator(name = "sigAvcSq1", sequenceName = "SIG_AVC_SQ1", allocationSize = 1)
@Table(name = "SIG_ATIVIDADE_CENTRO_CUSTOS", schema = "AGH")
public class SigAtividadeCentroCustos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 3022914659841204L;

	private Integer seq;
	private SigAtividades sigAtividades;
	private FccCentroCustos fccCentroCustos;
	private Date criadoEm;
	private RapServidores rapServidores;
	private DominioSituacao indSituacao;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigAvcSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVD_SEQ", referencedColumnName = "SEQ")
	public SigAtividades getSigAtividades() {
		return sigAtividades;
	}

	public void setSigAtividades(SigAtividades sigAtividades) {
		this.sigAtividades = sigAtividades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
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

	@Column(name = "VERSION")
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {

		SEQ("seq"),
		ATIVIDADE("sigAtividades"),
		CENTRO_CUSTO("fccCentroCustos"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		IND_SITUACAO("indSituacao");

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
	public int hashCode() {
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigAtividadeCentroCustos)) {
			return false;
		}
		SigAtividadeCentroCustos other = (SigAtividadeCentroCustos) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();
	}

}
