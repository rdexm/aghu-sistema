package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Cascade;

import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity

@Table(name = "MPM_MOD_BASIC_MDTOS", schema = "AGH")
public class MpmModeloBasicoMedicamento extends BaseEntityId<MpmModeloBasicoMedicamentoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4131861304914290654L;

	private MpmModeloBasicoMedicamentoId id;
	private MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento;
	private MpmModeloBasicoPrescricao modeloBasicoPrescricao;
	private AfaViaAdministracao viaAdministracao;
	private RapServidores servidor;
	private AfaTipoVelocAdministracoes tipoVelocidadeAdministracao;
	private Boolean indSeNecessario;
	private Boolean indSolucao;
	private Short frequencia;
	private Date horaInicioAdministracao;
	private BigDecimal gotejo;
	private Byte quantidadeHorasCorrer;
	private DominioUnidadeHorasMinutos unidHorasCorrer;
	private AfaMedicamento diluente;
	private BigDecimal volumeDiluenteMl;
	private Boolean indBombaInfusao;
	private String observacao;

	private List<MpmItemModeloBasicoMedicamento> itensModeloMedicamento = new LinkedList<MpmItemModeloBasicoMedicamento>();
	
	
	private enum ModBasicMdtosExceptionCode implements BusinessExceptionCode {
		MPM_MBM_CK2, MPM_MBM_CK3, MPM_MBM_CK4, MPM_MBM_CK5
	}
	

	// construtores
	public MpmModeloBasicoMedicamento() {
	}

	public MpmModeloBasicoMedicamento(MpmModeloBasicoMedicamentoId id,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			MpmModeloBasicoPrescricao modBasicPrescricao,
			AfaViaAdministracao viaAdministracao, RapServidores servidor,
			Boolean indSeNecessario, Boolean indSolucao) {
		this.id = id;
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
		this.modeloBasicoPrescricao = modBasicPrescricao;
		this.viaAdministracao = viaAdministracao;
		this.servidor = servidor;
		this.indSeNecessario = indSeNecessario;
		this.indSolucao = indSolucao;
	}

	public MpmModeloBasicoMedicamento(MpmModeloBasicoMedicamentoId id,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			MpmModeloBasicoPrescricao modBasicPrescricao,
			AfaViaAdministracao viaAdministracao, RapServidores servidor,
			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao,
			Boolean indSeNecessario, Boolean indSolucao, Short frequencia,
			Date horaInicioAdministracao, BigDecimal gotejo,
			Byte quantidadeHorasCorrer, String observacao,
			List<MpmItemModeloBasicoMedicamento> itensModeloMedicamento) {
		this.id = id;
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
		this.modeloBasicoPrescricao = modBasicPrescricao;
		this.viaAdministracao = viaAdministracao;
		this.servidor = servidor;
		this.tipoVelocidadeAdministracao = tipoVelocidadeAdministracao;
		this.indSeNecessario = indSeNecessario;
		this.indSolucao = indSolucao;
		this.frequencia = frequencia;
		this.horaInicioAdministracao = horaInicioAdministracao;
		this.gotejo = gotejo;
		this.quantidadeHorasCorrer = quantidadeHorasCorrer;
		this.observacao = observacao;
		this.itensModeloMedicamento = itensModeloMedicamento;
	}

	// getters & setters

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "modeloBasicoPrescricaoSeq", column = @Column(name = "MDB_SEQ", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false, precision = 8, scale = 0)) })
	public MpmModeloBasicoMedicamentoId getId() {
		return this.id;
	}

	public void setId(MpmModeloBasicoMedicamentoId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ", nullable = false)
	public MpmTipoFrequenciaAprazamento getTipoFrequenciaAprazamento() {
		return this.tipoFrequenciaAprazamento;
	}

	public void setTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDB_SEQ", nullable = false, insertable = false, updatable = false)
	public MpmModeloBasicoPrescricao getModeloBasicoPrescricao() {
		return this.modeloBasicoPrescricao;
	}

	public void setModeloBasicoPrescricao(
			MpmModeloBasicoPrescricao modBasicPrescricao) {
		this.modeloBasicoPrescricao = modBasicPrescricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VAD_SIGLA", nullable = false)
	public AfaViaAdministracao getViaAdministracao() {
		return viaAdministracao;
	}

	public void setViaAdministracao(AfaViaAdministracao viaAdministracao) {
		this.viaAdministracao = viaAdministracao;
	}

	/**
	 * @return the servidor
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVA_SEQ", nullable = true)
	public AfaTipoVelocAdministracoes getTipoVelocidadeAdministracao() {
		return tipoVelocidadeAdministracao;
	}

	public void setTipoVelocidadeAdministracao(
			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao) {
		this.tipoVelocidadeAdministracao = tipoVelocidadeAdministracao;
	}

	@Column(name = "IND_SE_NECESSARIO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSeNecessario() {
		return this.indSeNecessario;
	}

	public void setIndSeNecessario(Boolean indSeNecessario) {
		this.indSeNecessario = indSeNecessario;
	}

	@Column(name = "IND_SOLUCAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSolucao() {
		return this.indSolucao;
	}

	public void setIndSolucao(Boolean indSolucao) {
		this.indSolucao = indSolucao;
	}

	@Column(name = "FREQUENCIA", precision = 3, scale = 0)
	public Short getFrequencia() {
		return this.frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HORA_INICIO_ADMINISTRACAO", length = 7)
	public Date getHoraInicioAdministracao() {
		return this.horaInicioAdministracao;
	}

	public void setHoraInicioAdministracao(Date horaInicioAdministracao) {
		this.horaInicioAdministracao = horaInicioAdministracao;
	}

	@Column(name = "GOTEJO", precision = 5, scale = 2)
	public BigDecimal getGotejo() {
		return this.gotejo;
	}

	public void setGotejo(BigDecimal gotejo) {
		this.gotejo = gotejo;
	}

	@Column(name = "QTDE_HORAS_CORRER", precision = 2, scale = 0)
	public Byte getQuantidadeHorasCorrer() {
		return quantidadeHorasCorrer;
	}

	public void setQuantidadeHorasCorrer(Byte quantidadeHorasCorrer) {
		this.quantidadeHorasCorrer = quantidadeHorasCorrer;
	}
	
	@Column(name = "UNID_HORAS_CORRER", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioUnidadeHorasMinutos getUnidHorasCorrer() {
		return this.unidHorasCorrer;
	}

	public void setUnidHorasCorrer(DominioUnidadeHorasMinutos unidHorasCorrer) {
		this.unidHorasCorrer = unidHorasCorrer;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MED_MAT_CODIGO")
	public AfaMedicamento getDiluente() {
		return this.diluente;
	}

	public void setDiluente(AfaMedicamento medMatCodigo) {
		this.diluente = medMatCodigo;
	}

	@Column(name = "VOLUME_DILUENTE_ML", precision = 8, scale = 3)
	public BigDecimal getVolumeDiluenteMl() {
		return this.volumeDiluenteMl;
	}
	
	public void setVolumeDiluenteMl(BigDecimal volumeDiluenteMl) {
		this.volumeDiluenteMl = volumeDiluenteMl;
	}

	@Column(name = "IND_BOMBA_INFUSAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndBombaInfusao() {
		return this.indBombaInfusao;
	}

	public void setIndBombaInfusao(Boolean indBombaInfusao) {
		this.indBombaInfusao = indBombaInfusao;
	}

	@Column(name = "OBSERVACAO", length = 240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloBasicoMedicamento")
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public List<MpmItemModeloBasicoMedicamento> getItensModeloMedicamento() {
		return itensModeloMedicamento;
	}

	public void setItensModeloMedicamento(
			List<MpmItemModeloBasicoMedicamento> list) {
		this.itensModeloMedicamento = list;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		MpmModeloBasicoMedicamento other = (MpmModeloBasicoMedicamento) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public enum Fields {
		MODELO_BASICO_PRESCRICAO_SEQ("id.modeloBasicoPrescricaoSeq"), ID("id"), TIPO_FREQ_APRAZAMENTO(
				"tipoFrequenciaAprazamento"), VOLUME_DILUENTE_MT("volumeDiluenteMl")
				, MOD_BASIC_PRESCRICAO("modeloBasicoPrescricao")
				, VIA_ADMINISTRACAO("viaAdministracao"), SERVIDOR(
				"servidor"), TIPO_VELOCIDADE_ADMINISTRACAO(
				"tipoVelocidadeAdministracao"), IND_SE_NECESSARIO(
				"indSeNecessario"), IND_SOLUCAO("indSolucao"), FREQUENCIA(
				"frequencia"), HORA_INICIO_ADMINISTRACAO(
				"horaInicioAdministracao"), GOTEJO("gotejo"), QTDE_HORAS_CORRER(
				"quantidadeHorasCorrer"), OBSERVACAO("observacao"), ITENS_MOD_BASICO_MDTOS(
				"itensModeloMedicamento"), SEQ("id.seq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
    @PrePersist
    @PreUpdate
    @SuppressWarnings({"PMD.NPathComplexity", "unused"})
	private void validacoes() {

		if (!(this.frequencia == null || (this.frequencia != null && this.frequencia > 0))) {
			throw new BaseRuntimeException(ModBasicMdtosExceptionCode.MPM_MBM_CK2);
		}
		if (!(this.gotejo == null || (this.gotejo != null && this.gotejo.intValue() > 0))) {
			throw new BaseRuntimeException(ModBasicMdtosExceptionCode.MPM_MBM_CK3);
		}
		if (!(this.quantidadeHorasCorrer == null || (this.quantidadeHorasCorrer != null && this.quantidadeHorasCorrer > 0))) {
			throw new BaseRuntimeException(ModBasicMdtosExceptionCode.MPM_MBM_CK4);
		}
		if (!((this.tipoVelocidadeAdministracao == null && this.gotejo == null) || (this.tipoVelocidadeAdministracao != null && this.gotejo != null))) {
			throw new BaseRuntimeException(ModBasicMdtosExceptionCode.MPM_MBM_CK5);
		}

		if (this.indSeNecessario == null) {
			this.indSeNecessario = false;
		}
		if (this.indSolucao == null) {
			this.indSolucao = false;
		}
	}
	 

	
}
