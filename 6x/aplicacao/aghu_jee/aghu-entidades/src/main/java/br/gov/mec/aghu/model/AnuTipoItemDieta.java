package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioRestricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="anuTidSq1", sequenceName="AGH.ANU_TID_SQ1", allocationSize = 1)
@Table(name = "ANU_TIPO_ITEM_DIETAS", schema = "AGH")
public class AnuTipoItemDieta extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4925275933100374856L;
	private Integer seq;
	private String descricao;
	private String sintaxeMedico;
	private String sintaxeNutricao;
	private DominioRestricao indDigitaQuantidade;
	private DominioRestricao indDigitaAprazamento;
	private Boolean indAdulto;
	private Boolean indPediatria;
	private Boolean indNeonatologia;
	private Boolean indDietaPadronizada;
	private Boolean indItemUnico;
	private Short frequencia;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private String descricaoCompletaMapaDieta;
	private RapServidores servidor;
	private MpmUnidadeMedidaMedica unidadeMedidaMedica;
	private MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento;
	private Set<AnuTipoItemDietaUnfs> tipoDietaUnidadeFuncional = new HashSet<AnuTipoItemDietaUnfs>(0);

	private enum AnuTipoItemDietaExceptionCode implements BusinessExceptionCode {
		FREQUENCIA_COM_TIPO_OBRIGATORIA_INVALIDA, TIPO_PACIENTE_OBRIGATORIO
	}

	// construtores

	public AnuTipoItemDieta() {
	}

	public AnuTipoItemDieta(Integer seq) {
		this.seq = seq;
	}

	public AnuTipoItemDieta(Integer seq, String descricao,
			String sintaxeMedico, String sintaxeNutricao,
			DominioRestricao indDigitaQuantidade,
			DominioRestricao indDigitaAprazamento, Boolean indAdulto,
			Boolean indPediatria, Boolean indNeonatologia,
			Boolean indDietaPadronizada, Date criadoEm,
			DominioSituacao indSituacao, RapServidores servidor,
			Boolean indItemUnico) {
		this.seq = seq;
		this.descricao = descricao;
		this.sintaxeMedico = sintaxeMedico;
		this.sintaxeNutricao = sintaxeNutricao;
		this.indDigitaQuantidade = indDigitaQuantidade;
		this.indDigitaAprazamento = indDigitaAprazamento;
		this.indAdulto = indAdulto;
		this.indPediatria = indPediatria;
		this.indNeonatologia = indNeonatologia;
		this.indDietaPadronizada = indDietaPadronizada;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.servidor = servidor;
		this.indItemUnico = indItemUnico;
	}

	public AnuTipoItemDieta(Integer seq, String descricao,
			String sintaxeMedico, String sintaxeNutricao,
			DominioRestricao indDigitaQuantidade,
			DominioRestricao indDigitaAprazamento, Boolean indAdulto,
			Boolean indPediatria, Boolean indNeonatologia,
			Boolean indDietaPadronizada, Date criadoEm,
			DominioSituacao indSituacao, String descricaoCompletaMapaDieta,
			RapServidores servidor, MpmUnidadeMedidaMedica unidadeMedidaMedica,
			Boolean indItemUnico, Short frequencia,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		this.seq = seq;
		this.descricao = descricao;
		this.sintaxeMedico = sintaxeMedico;
		this.sintaxeNutricao = sintaxeNutricao;
		this.indDigitaQuantidade = indDigitaQuantidade;
		this.indDigitaAprazamento = indDigitaAprazamento;
		this.indAdulto = indAdulto;
		this.indPediatria = indPediatria;
		this.indNeonatologia = indNeonatologia;
		this.indDietaPadronizada = indDietaPadronizada;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.descricaoCompletaMapaDieta = descricaoCompletaMapaDieta;
		this.servidor = servidor;
		this.unidadeMedidaMedica = unidadeMedidaMedica;
		this.indItemUnico = indItemUnico;
		this.frequencia = frequencia;
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
	}

	// getters & setters

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "anuTidSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 45)
	@Length(max=45)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "SINTAXE_MEDICO", nullable = false, length = 15)
	@Length(max=15)
	public String getSintaxeMedico() {
		return this.sintaxeMedico;
	}

	public void setSintaxeMedico(String sintaxeMedico) {
		this.sintaxeMedico = sintaxeMedico;
	}

	@Column(name = "SINTAXE_NUTRICAO", nullable = false, length = 15)
	@Length(max=15)
	public String getSintaxeNutricao() {
		return this.sintaxeNutricao;
	}

	public void setSintaxeNutricao(String sintaxeNutricao) {
		this.sintaxeNutricao = sintaxeNutricao;
	}

	@Column(name = "IND_DIGITA_QUANTIDADE", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioRestricao getIndDigitaQuantidade() {
		return this.indDigitaQuantidade;
	}

	public void setIndDigitaQuantidade(DominioRestricao indDigitaQuantidade) {
		this.indDigitaQuantidade = indDigitaQuantidade;
	}

	@Column(name = "IND_DIGITA_APRAZAMENTO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioRestricao getIndDigitaAprazamento() {
		return this.indDigitaAprazamento;
	}

	public void setIndDigitaAprazamento(DominioRestricao indDigitaAprazamento) {
		this.indDigitaAprazamento = indDigitaAprazamento;
	}

	@Column(name = "IND_ADULTO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAdulto() {
		return this.indAdulto;
	}

	public void setIndAdulto(Boolean indAdulto) {
		this.indAdulto = indAdulto;
	}

	@Column(name = "IND_PEDIATRIA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPediatria() {
		return this.indPediatria;
	}

	public void setIndPediatria(Boolean indPediatria) {
		this.indPediatria = indPediatria;
	}

	@Column(name = "IND_NEONATOLOGIA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndNeonatologia() {
		return this.indNeonatologia;
	}

	public void setIndNeonatologia(Boolean indNeonatologia) {
		this.indNeonatologia = indNeonatologia;
	}

	@Column(name = "IND_DIETA_PADRONIZADA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDietaPadronizada() {
		return this.indDietaPadronizada;
	}

	public void setIndDietaPadronizada(Boolean indDietaPadronizada) {
		this.indDietaPadronizada = indDietaPadronizada;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "DESCR_COMPL_MAPA_DIETA", length = 60)
	@Length(max=60)
	public String getDescricaoCompletaMapaDieta() {
		return this.descricaoCompletaMapaDieta;
	}

	public void setDescricaoCompletaMapaDieta(String descricaoCompletaMapaDieta) {
		this.descricaoCompletaMapaDieta = descricaoCompletaMapaDieta;
	}

	/**
	 * @return the servidor
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMM_SEQ")
	public MpmUnidadeMedidaMedica getUnidadeMedidaMedica() {
		return unidadeMedidaMedica;
	}

	public void setUnidadeMedidaMedica(
			MpmUnidadeMedidaMedica unidadeMedidaMedica) {
		this.unidadeMedidaMedica = unidadeMedidaMedica;
	}

	@Column(name = "IND_ITEM_UNICO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndItemUnico() {
		return this.indItemUnico;
	}

	public void setIndItemUnico(Boolean indItemUnico) {
		this.indItemUnico = indItemUnico;
	}

	@Column(name = "FREQUENCIA", precision = 3, scale = 0)
	public Short getFrequencia() {
		return this.frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ")
	public MpmTipoFrequenciaAprazamento getTipoFrequenciaAprazamento() {
		return tipoFrequenciaAprazamento;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoItemDieta")
	public Set<AnuTipoItemDietaUnfs> getTipoDietaUnidadeFuncional() {
		return tipoDietaUnidadeFuncional;
	}

	public void setTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AnuTipoItemDieta)) {
			return false;
		}
		AnuTipoItemDieta castOther = (AnuTipoItemDieta) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), DESCRICAO("descricao"), SINTAXE_MEDICO("sintaxeMedico"), SINTAXE_NUTRICAO(
				"sintaxeNutricao"), IND_DIGITA_QUANTIDADE("indDigitaQuantidade"), IND_DIGITA_APRAZAMENTO(
				"indDigitaAprazamento"), IND_ADULTO("indAdulto"), IND_PEDIATRIA(
				"indPediatria"), IND_NEONATOLOGIA("indNeonatologia"), IND_DIETA_PADRONIZADA(
				"indDietaPadronizada"), CRIADO_EM("criadoEm"), IND_SITUACAO(
				"indSituacao"), DESCR_COMPL_MAPA_DIETA(
				"descricaoCompletaMapaDieta"), IND_ITEM_UNICO("indItemUnico"), FREQUENCIA(
				"frequencia"), SERVIDOR("servidor"),SERVIDOR_PF_NOME("servidor.pessoaFisica.nome"), UNIDADE_MEDIDA_MEDICAS(
				"unidadeMedidaMedica"), TIPO_FREQ_APRAZAMENTOS(
				"tipoFrequenciaAprazamento");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	/**
	 * Função de validação dos dados antes que ocorra a persistência.
	 */
	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void valida() {

		// CONSTRAINT ANU_TID_CK10
		if (!((this.tipoFrequenciaAprazamento == null) || (this.tipoFrequenciaAprazamento != null && this.indDigitaAprazamento.equals(DominioRestricao.O)))) {
			throw new BaseRuntimeException(
					AnuTipoItemDietaExceptionCode.FREQUENCIA_COM_TIPO_OBRIGATORIA_INVALIDA);
		}

		// CONSTRAINT ANU_TID_CK8
		if (!((this.indAdulto != null && this.indAdulto.booleanValue())
				|| (this.indPediatria != null && this.indPediatria
						.booleanValue()) || (this.indNeonatologia != null && this.indNeonatologia
				.booleanValue()))) {
			throw new BaseRuntimeException(
					AnuTipoItemDietaExceptionCode.TIPO_PACIENTE_OBRIGATORIO);
		}
	}

	public void setTipoDietaUnidadeFuncional(
			Set<AnuTipoItemDietaUnfs> tipoDietaUnidadeFuncional) {
		this.tipoDietaUnidadeFuncional = tipoDietaUnidadeFuncional;
	}
}
