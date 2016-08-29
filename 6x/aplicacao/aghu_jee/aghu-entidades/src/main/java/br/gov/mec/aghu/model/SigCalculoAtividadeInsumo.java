package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
@SequenceGenerator(name = "sigCvnSq1", sequenceName = "SIG_CVN_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_atividade_insumos", schema = "agh")
public class SigCalculoAtividadeInsumo extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 6393700530390916550L;

	private Integer seq;
	private SigCalculoComponente sigCalculoComponentes;
	private Date criadoEm;
	private RapServidores rapServidores;
	private ScoMaterial scoMaterial;
	//private BigDecimal vlrInsumo;
	private BigDecimal vlrInsumo = BigDecimal.ZERO;
	private Double qtdePrevista;
	private Double qtdeRealizada;
	private SigDirecionadores sigDirecionadores;
	private SigAtividades sigAtividades;
	private SigAtividadeInsumos sigAtividadeInsumos;
	private Integer version;
	private BigDecimal peso;
	
	public SigCalculoAtividadeInsumo() {
	}
	
	public SigCalculoAtividadeInsumo(Integer seq) {
		this.seq = seq;
	}
	
	public SigCalculoAtividadeInsumo(Integer seq, SigCalculoComponente sigCalculoComponentes, Date criadoEm, RapServidores rapServidores,
			ScoMaterial scoMaterial, BigDecimal vlrInsumo, Double qtdePrevista, Double qtdeRealizada, SigDirecionadores sigDirecionadores,
			SigAtividades sigAtividades, SigAtividadeInsumos sigAtividadeInsumos, Integer version, BigDecimal peso) {
		this.seq = seq;
		this.sigCalculoComponentes = sigCalculoComponentes;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.scoMaterial = scoMaterial;
		this.vlrInsumo = vlrInsumo;
		this.qtdePrevista = qtdePrevista;
		this.qtdeRealizada = qtdeRealizada;
		this.sigDirecionadores = sigDirecionadores;
		this.sigAtividades = sigAtividades;
		this.sigAtividadeInsumos = sigAtividadeInsumos;
		this.version = version;
		this.peso = peso;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCvnSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cmt_seq", nullable = false, referencedColumnName = "seq")
	public SigCalculoComponente getSigCalculoComponentes() {
		return this.sigCalculoComponentes;
	}

	public void setSigCalculoComponentes(SigCalculoComponente sigCalculoComponentes) {
		this.sigCalculoComponentes = sigCalculoComponentes;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mat_codigo", nullable = false, referencedColumnName = "codigo")
	public ScoMaterial getScoMaterial() {
		return this.scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	@Column(name = "vlr_insumo", nullable = false, precision = 18, scale = 4)
	public BigDecimal getVlrInsumo() {
		return this.vlrInsumo;
	}

	public void setVlrInsumo(BigDecimal vlrInsumo) {
		this.vlrInsumo = vlrInsumo;
	}
	
	@Column(name = "peso", precision = 14, scale = 5)	
	public BigDecimal getPeso() {
		return this.peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	@Column(name = "qtde_prevista", precision = 9, scale = 4)
	public Double getQtdePrevista() {
		return this.qtdePrevista;
	}

	public void setQtdePrevista(Double qtdePrevista) {
		this.qtdePrevista = qtdePrevista;
	}

	@Column(name = "qtde_realizada", precision = 9, scale = 4)
	public Double getQtdeRealizada() {
		return this.qtdeRealizada;
	}

	public void setQtdeRealizada(Double qtdeRealizada) {
		this.qtdeRealizada = qtdeRealizada;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dir_seq", referencedColumnName = "seq")
	public SigDirecionadores getSigDirecionadores() {
		return this.sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvd_seq", referencedColumnName = "seq")
	public SigAtividades getSigAtividades() {
		return this.sigAtividades;
	}

	public void setSigAtividades(SigAtividades sigAtividades) {
		this.sigAtividades = sigAtividades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ais_seq", referencedColumnName = "seq")
	public SigAtividadeInsumos getSigAtividadeInsumos() {
		return this.sigAtividadeInsumos;
	}

	public void setSigAtividadeInsumos(SigAtividadeInsumos sigAtividadeInsumos) {
		this.sigAtividadeInsumos = sigAtividadeInsumos;
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
		CALCULO_COMPONENTE("sigCalculoComponentes"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		MATERIAL("scoMaterial"),
		VALOR_INSUMO("vlrInsumo"),
		QUANTIDADE_PREVISTA("qtdePrevista"),
		QUANTIDADE_REALIZADA("qtdeRealizada"),
		DIRECIONADOR("sigDirecionadores"),
		ATIVIDADE_INSUMO("sigAtividadeInsumos");

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
		if (!(obj instanceof SigCalculoAtividadeInsumo)) {
			return false;
		}
		SigCalculoAtividadeInsumo other = (SigCalculoAtividadeInsumo) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();
	}
}
