package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCppSq1", sequenceName = "SIG_CPP_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_atd_permanencias", schema = "agh")
public class SigCalculoAtdPermanencia extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4423351486063029177L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;
	private SigCalculoAtdPaciente calculoAtdPaciente;
	private AghEspecialidades especialidade;
	private FccCentroCustos centroCustos;
	private RapServidores responsavel;
	private DominioCalculoPermanencia tipo;
	private BigDecimal tempo;
	
	private Set<SigCalculoAtdConsumo> sigCalculoAtdConsumos;
	private Set<SigCalculoAtdReceita> sigCalculoAtdReceitas;

	public SigCalculoAtdPermanencia() {
	}
	
	public SigCalculoAtdPermanencia(Integer seq) {
		this.setSeq(seq);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCppSq1")
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

	@Column(name = "version", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "tempo", nullable = false, precision = 9, scale = 4)
	public BigDecimal getTempo() {
		return tempo;
	}

	public void setTempo(BigDecimal tempo) {
		this.tempo = tempo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_RESPONSAVEL", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_RESPONSAVEL", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(RapServidores responsavel) {
		this.responsavel = responsavel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", nullable = true, referencedColumnName = "codigo")
	public FccCentroCustos getCentroCustos() {
		return centroCustos;
	}

	public void setCentroCustos(FccCentroCustos centroCustos) {
		this.centroCustos = centroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ", nullable = true, referencedColumnName = "seq")
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAC_SEQ", nullable = false, referencedColumnName = "seq")
	public SigCalculoAtdPaciente getCalculoAtdPaciente() {
		return calculoAtdPaciente;
	}

	public void setCalculoAtdPaciente(SigCalculoAtdPaciente calculoAtdPaciente) {
		this.calculoAtdPaciente = calculoAtdPaciente;
	}

	@Column(name = "IND_TIPO", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioCalculoPermanencia getTipo() {
		return tipo;
	}

	public void setTipo(DominioCalculoPermanencia tipo) {
		this.tipo = tipo;
	}

	public enum Fields {

		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("rapServidores"),
		RESPONSAVEL("responsavel"),
		RESPONSAVEL_MATRICULA("responsavel.id.matricula"),
		RESPONSAVEL_VIN_CODIGO("responsavel.id.vinCodigo"),
		TEMPO("tempo"),
		CALCULO_ATD_PACIENTE("calculoAtdPaciente"),
		ESPECIALIDADE("especialidade"),
		ESPECIALIDADE_SEQ("especialidade.seq"),
		TIPO("tipo"),
		CENTRO_CUSTO("centroCustos"),
		CENTRO_CUSTO_CODIGO("centroCustos.codigo"),
		CALCULO_ATD_CONSUMO("sigCalculoAtdConsumos"),
		CALCULO_ATD_RECEITA("sigCalculoAtdReceitas");

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
		if (!(obj instanceof SigCalculoAtdPermanencia)) {
			return false;
		}
		SigCalculoAtdPermanencia other = (SigCalculoAtdPermanencia) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}

	public void setSigCalculoAtdConsumos(Set<SigCalculoAtdConsumo> sigCalculoAtdConsumos) {
		this.sigCalculoAtdConsumos = sigCalculoAtdConsumos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "calculoAtividadePermanencia")
	public Set<SigCalculoAtdConsumo> getSigCalculoAtdConsumos() {
		return sigCalculoAtdConsumos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "calculoAtdPermanencia")
	public Set<SigCalculoAtdReceita> getSigCalculoAtdReceitas() {
		return sigCalculoAtdReceitas;
	}

	public void setSigCalculoAtdReceitas(
			Set<SigCalculoAtdReceita> sigCalculoAtdReceitas) {
		this.sigCalculoAtdReceitas = sigCalculoAtdReceitas;
	}

}
