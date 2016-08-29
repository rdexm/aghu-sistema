package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import br.gov.mec.aghu.dominio.DominioTipoIndicador;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "AIN_IND_HOSPITALAR_RESUMIDO", schema = "AGH")

public class AinIndicadorHospitalarResumido extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5320277370259033628L;
	private Integer seq;
	private Date competenciaInternacao;
	private BigDecimal taxaOcupacao;
	private BigDecimal taxaMortalidade;
	private BigDecimal mediaPermanencia;
	private Integer quantidadePaciente;
	private Integer quantidadeObito;
	private Integer leitoDia;
	private Integer capacidadeInstalada;
	private Integer quantidadeSaida; // usar total saidas
	private Integer quantidadeTransferenciaAreaFuncional;
	private Integer quantidadeTransferenciaEspecialidade;
	private Integer quantidadeTransferenciaClinica;
	private Integer totalSaidas;
	private Date dataCriacao;
	private String competenciaInternacaoString;
	private AghUnidadesFuncionais unidadeFuncional;
	private AghClinicas clinica;
	private AghEspecialidades especialidade;
	private DominioTipoIndicador tipoIndicador;

	public AinIndicadorHospitalarResumido() {
	}

	public AinIndicadorHospitalarResumido(Integer capacidadeInstalada) {
		super();
		this.capacidadeInstalada = capacidadeInstalada;
	}

	@Id
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "COMPETENCIA_INTERNACAO", nullable = false)
	public Date getCompetenciaInternacao() {
		return competenciaInternacao;
	}

	public void setCompetenciaInternacao(Date competenciaInternacao) {
		this.competenciaInternacao = competenciaInternacao;
	}

	@Column(name = "TAXA_OCUPACAO", precision = 7)
	public BigDecimal getTaxaOcupacao() {
		return taxaOcupacao;
	}

	public void setTaxaOcupacao(BigDecimal taxaOcupacao) {
		this.taxaOcupacao = taxaOcupacao;
	}

	@Column(name = "TAXA_MORTALIDADE", precision = 7)
	public BigDecimal getTaxaMortalidade() {
		return taxaMortalidade;
	}

	public void setTaxaMortalidade(BigDecimal taxaMortalidade) {
		this.taxaMortalidade = taxaMortalidade;
	}

	@Column(name = "MEDIA_PERMANENCIA", precision = 7)
	public BigDecimal getMediaPermanencia() {
		return mediaPermanencia;
	}

	public void setMediaPermanencia(BigDecimal mediaPermanencia) {
		this.mediaPermanencia = mediaPermanencia;
	}

	@Column(name = "QTD_PACIENTE", precision = 7)
	public Integer getQuantidadePaciente() {
		return quantidadePaciente;
	}

	public void setQuantidadePaciente(Integer quantidadePaciente) {
		this.quantidadePaciente = quantidadePaciente;
	}

	@Column(name = "QTD_OBITO", precision = 7)
	public Integer getQuantidadeObito() {
		return quantidadeObito;
	}

	public void setQuantidadeObito(Integer quantidadeObito) {
		this.quantidadeObito = quantidadeObito;
	}

	@Column(name = "LEITO_DIA", precision = 7)
	public Integer getLeitoDia() {
		return leitoDia;
	}

	public void setLeitoDia(Integer leitoDia) {
		this.leitoDia = leitoDia;
	}

	@Column(name = "CAPAC_INSTALADA", precision = 7)
	public Integer getCapacidadeInstalada() {
		return capacidadeInstalada;
	}

	public void setCapacidadeInstalada(Integer capacidadeInstalada) {
		this.capacidadeInstalada = capacidadeInstalada;
	}

	@Deprecated
	@Column(name = "QTD_SAIDA", precision = 7)
	public Integer getQuantidadeSaida() {
		return quantidadeSaida;
	}

	@Deprecated
	public void setQuantidadeSaida(Integer quantidadeSaida) {
		this.quantidadeSaida = quantidadeSaida;
	}

	@Column(name = "QTD_TRANSF_AREA_FUNC", precision = 7)
	public Integer getQuantidadeTransferenciaAreaFuncional() {
		return quantidadeTransferenciaAreaFuncional;
	}

	public void setQuantidadeTransferenciaAreaFuncional(
			Integer quantidadeTransferenciaAreaFuncional) {
		this.quantidadeTransferenciaAreaFuncional = quantidadeTransferenciaAreaFuncional;
	}

	@Column(name = "QTD_TRANSF_ESPEC", precision = 7)
	public Integer getQuantidadeTransferenciaEspecialidade() {
		return quantidadeTransferenciaEspecialidade;
	}

	public void setQuantidadeTransferenciaEspecialidade(
			Integer quantidadeTransferenciaEspecialidade) {
		this.quantidadeTransferenciaEspecialidade = quantidadeTransferenciaEspecialidade;
	}

	@Column(name = "QTD_TRANSF_CLINICA", precision = 7)
	public Integer getQuantidadeTransferenciaClinica() {
		return quantidadeTransferenciaClinica;
	}

	public void setQuantidadeTransferenciaClinica(
			Integer quantidadeTransferenciaClinica) {
		this.quantidadeTransferenciaClinica = quantidadeTransferenciaClinica;
	}

	@Column(name = "TOTAL_SAIDAS", precision = 7)
	public Integer getTotalSaidas() {
		return totalSaidas;
	}

	public void setTotalSaidas(Integer totalSaidas) {
		this.totalSaidas = totalSaidas;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ALTERACAO", nullable = false)
	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public enum Fields {
		SEQ("seq"), COMPETENCIA_INTERNACAO("competenciaInternacao"), UNIDADE_FUNCIONAL(
				"unidadeFuncional"), ESPECIALIDADE("especialidade"), CLINICA("clinica"), TIPO_INDICADOR(
				"tipoIndicador");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	/**
	 * Método para retornar a competenciaInternacao em formato String
	 * 
	 * @return
	 */
	@Transient
	public String getCompetenciaInternacaoString() {
		this.setCompetenciaInternacaoString(competenciaInternacao);
		return this.competenciaInternacaoString;
	}

	/**
	 * Método para formatar a competenciaInternacaoString
	 * 
	 * @param competenciaInternacao
	 */
	public void setCompetenciaInternacaoString(Date competenciaInternacao) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
		this.competenciaInternacaoString = sdf.format(competenciaInternacao);
	}

	/**
	 * @return the unidadeFuncional
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", nullable = true)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return this.unidadeFuncional;
	}

	/**
	 * @param unidadeFuncional
	 *            the unidadeFuncional to set
	 */
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLC_CODIGO", nullable = true)
	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ", nullable = true)
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	@Column(name = "TIPO_INDICADOR", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoIndicador getTipoIndicador() {
		return this.tipoIndicador;
	}

	public void setTipoIndicador(DominioTipoIndicador tipoIndicador) {
		this.tipoIndicador = tipoIndicador;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AinIndicadorHospitalarResumido)) {
			return false;
		}
		AinIndicadorHospitalarResumido other = (AinIndicadorHospitalarResumido) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
