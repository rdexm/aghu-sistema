package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescricaoMedicamento;
import br.gov.mec.aghu.dominio.DominioUnidadeCorrer;
import br.gov.mec.aghu.core.persistence.BaseEntityId;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

@Entity
@Table(name = "MPT_PRESCRICAO_MDTOS", schema = "AGH")
public class MptPrescricaoMedicamento extends BaseEntityId<MptPrescricaoMedicamentoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3879010364622010903L;
	private MptPrescricaoMedicamentoId id;
	private MptPrescricaoMedicamento prescricaoMedicamento;
	private Date criadoEm;
	private Byte diasDeUsoDomiciliar;
	private Short frequencia;
	private Date horaInicioAdministracao;
	private String observacao;
	private BigDecimal gotejo;
	private Byte qtdeCorrer;
	private Date alteradoEm;
	private Date dthrIncValida;
	private Date dthrAltValida;
	private Short ordem;
	private Short ordemImpressao;
	private DominioSituacaoItemPrescricaoMedicamento situacaoItem;
	private DominioUnidadeCorrer unidadeCorrer;
	private Boolean seNecessario;
	private Boolean usoDomiciliar;
	private Boolean bombaInfusao;
	private Boolean solucao;
	private Boolean altAndamento;
	private Integer comSeq; // TODO Validar se pode criar uma FK para este campo
	private MpmTipoFrequenciaAprazamento tipoFreqAprazamento;
	private AfaViaAdministracao viaAdministracao;
	private RapServidores servidor;
	private RapServidores servidorIncValida;
	private RapServidores servidorAltera;
	private RapServidores servidorAltValida;
	private AfaTipoVelocAdministracoes tipoVelocidadeAdministracao;
	private MptPrescricaoPaciente prescricaoPaciente;
	private MptAgrupaItem agrupaItem;
	private Set<MptItemPrescricaoMedicamento> itensPrescricoesMedicamentos = new HashSet<MptItemPrescricaoMedicamento>(
			0);
	private Set<MptPrescricaoMedicamento> prescricoesMedicamentos = new HashSet<MptPrescricaoMedicamento>(
			0);

	public MptPrescricaoMedicamento() {
	}

	public MptPrescricaoMedicamento(MptPrescricaoMedicamentoId id, Date criadoEm,
			Boolean seNecessario, Boolean usoDomiciliar, Boolean solucao, MptAgrupaItem agrupaItem) {
		this.id = id;
		this.criadoEm = criadoEm;
		this.seNecessario = seNecessario;
		this.usoDomiciliar = usoDomiciliar;
		this.solucao = solucao;
		this.agrupaItem =agrupaItem;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public MptPrescricaoMedicamento(MptPrescricaoMedicamentoId id,
			MptPrescricaoMedicamento prescricaoMedicamento, Date criadoEm,
			Boolean seNecessario, Byte diasDeUsoDomiciliar,
			Boolean usoDomiciliar, Short frequencia,
			Date horaInicioAdministracao, String observacao, BigDecimal gotejo,
			Byte qtdeCorrer, DominioUnidadeCorrer unidadeCorrer, Boolean solucao,
			Date alteradoEm, DominioSituacaoItemPrescricaoMedicamento situacaoItem,
			Integer comSeq, MpmTipoFrequenciaAprazamento tipoFreqAprazamento, MptAgrupaItem agrupaItem,
			Date dthrIncValida, Date dthrAltValida, Short ordem,
			Boolean bombaInfusao, Boolean altAndamento,
			Short ordemImpressao,
			MptPrescricaoPaciente prescricaoPaciente,
			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao,
			RapServidores servidor,
			RapServidores servidorAltValida,
			RapServidores servidorAltera,
			RapServidores servidorIncValida,
			AfaViaAdministracao viaAdministracao,
			Set<MptItemPrescricaoMedicamento> itensPrescricoesMedicamentos,
			Set<MptPrescricaoMedicamento> prescricoesMedicamentos) {
		this.id = id;
		this.prescricaoMedicamento = prescricaoMedicamento;
		this.criadoEm = criadoEm;
		this.seNecessario = seNecessario;
		this.diasDeUsoDomiciliar = diasDeUsoDomiciliar;
		this.usoDomiciliar = usoDomiciliar;
		this.frequencia = frequencia;
		this.horaInicioAdministracao = horaInicioAdministracao;
		this.observacao = observacao;
		this.gotejo = gotejo;
		this.qtdeCorrer = qtdeCorrer;
		this.unidadeCorrer = unidadeCorrer;
		this.solucao = solucao;
		this.alteradoEm = alteradoEm;
		this.situacaoItem = situacaoItem;
		this.comSeq = comSeq;
		this.tipoFreqAprazamento = tipoFreqAprazamento;
		this.agrupaItem = agrupaItem;
		this.dthrIncValida = dthrIncValida;
		this.dthrAltValida = dthrAltValida;
		this.ordem = ordem;
		this.bombaInfusao = bombaInfusao;
		this.altAndamento = altAndamento;
		this.ordemImpressao = ordemImpressao;
		this.prescricaoPaciente = prescricaoPaciente;
		this.tipoVelocidadeAdministracao = tipoVelocidadeAdministracao;
		this.servidor = servidor;
		this.servidorAltValida = servidorAltValida;
		this.servidorAltera = servidorAltera;
		this.servidorIncValida = servidorIncValida;
		this.viaAdministracao = viaAdministracao;
		this.itensPrescricoesMedicamentos = itensPrescricoesMedicamentos;
		this.prescricoesMedicamentos = prescricoesMedicamentos;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pteAtdSeq", column = @Column(name = "PTE_ATD_SEQ", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "pteSeq", column = @Column(name = "PTE_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false, precision = 8, scale = 0)) })
	public MptPrescricaoMedicamentoId getId() {
		return this.id;
	}

	public void setId(MptPrescricaoMedicamentoId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PMO_PTE_ATD_SEQ", referencedColumnName = "PTE_ATD_SEQ"),
			@JoinColumn(name = "PMO_PTE_SEQ", referencedColumnName = "PTE_SEQ"),
			@JoinColumn(name = "PMO_SEQ", referencedColumnName = "SEQ") })
	public MptPrescricaoMedicamento getPrescricaoMedicamento() {
		return this.prescricaoMedicamento;
	}

	public void setPrescricaoMedicamento(MptPrescricaoMedicamento prescricaoMedicamento) {
		this.prescricaoMedicamento = prescricaoMedicamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SE_NECESSARIO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getSeNecessario() {
		return this.seNecessario;
	}

	public void setSeNecessario(Boolean seNecessario) {
		this.seNecessario = seNecessario;
	}

	@Column(name = "DIAS_DE_USO_DOMICILIAR", precision = 2, scale = 0)
	public Byte getDiasDeUsoDomiciliar() {
		return this.diasDeUsoDomiciliar;
	}

	public void setDiasDeUsoDomiciliar(Byte diasDeUsoDomiciliar) {
		this.diasDeUsoDomiciliar = diasDeUsoDomiciliar;
	}

	@Column(name = "IND_USO_DOMICILIAR", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getUsoDomiciliar() {
		return this.usoDomiciliar;
	}

	public void setUsoDomiciliar(Boolean usoDomiciliar) {
		this.usoDomiciliar = usoDomiciliar;
	}

	@Column(name = "FREQUENCIA")
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

	@Column(name = "OBSERVACAO", length = 240)
	@Length(max = 240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "GOTEJO", precision = 6 , scale = 2)
	public BigDecimal getGotejo() {
		return this.gotejo;
	}

	/**
	 * Formata o gotejo (6,2) ###.###,##
	 * 
	 * @return
	 */
	@Transient
	public String getGotejoFormatado() {
		String numFormated = null;
		if (this.getGotejo() != null) {
			numFormated = AghuNumberFormat.formatarValor(this.getGotejo(), this.getClass(), "gotejo");
		}
		return numFormated;
	}
	
	public void setGotejo(BigDecimal gotejo) {
		this.gotejo = gotejo;
	}

	@Column(name = "QTDE_CORRER", precision = 2, scale = 0)
	public Byte getQtdeCorrer() {
		return this.qtdeCorrer;
	}

	public void setQtdeCorrer(Byte qtdeCorrer) {
		this.qtdeCorrer = qtdeCorrer;
	}

	@Column(name = "UNIDADE_CORRER", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioUnidadeCorrer getUnidadeCorrer() {
		return this.unidadeCorrer;
	}

	public void setUnidadeCorrer(DominioUnidadeCorrer unidadeCorrer) {
		this.unidadeCorrer = unidadeCorrer;
	}

	@Column(name = "IND_SOLUCAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getSolucao() {
		return this.solucao;
	}

	public void setSolucao(Boolean solucao) {
		this.solucao = solucao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "IND_SITUACAO_ITEM", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoItemPrescricaoMedicamento getSituacaoItem() {
		return this.situacaoItem;
	}

	public void setSituacaoItem(DominioSituacaoItemPrescricaoMedicamento situacaoItem) {
		this.situacaoItem = situacaoItem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VAD_SIGLA")
	public AfaViaAdministracao getViaAdministracao() {
		return viaAdministracao;
	}

	public void setViaAdministracao(AfaViaAdministracao viaAdministracao) {
		this.viaAdministracao = viaAdministracao;
	}

	@Column(name = "COM_SEQ", precision = 8, scale = 0)
	public Integer getComSeq() {
		return this.comSeq;
	}

	public void setComSeq(Integer comSeq) {
		this.comSeq = comSeq;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ")
	public MpmTipoFrequenciaAprazamento getTipoFreqAprazamento() {
		return tipoFreqAprazamento;
	}

	public void setTipoFreqAprazamento(
			MpmTipoFrequenciaAprazamento mpmTipoFreqAprazamentos) {
		this.tipoFreqAprazamento = mpmTipoFreqAprazamentos;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVA_SEQ")
	public AfaTipoVelocAdministracoes getTipoVelocidadeAdministracao() {
		return this.tipoVelocidadeAdministracao;
	}

	public void setTipoVelocidadeAdministracao(AfaTipoVelocAdministracoes tipoVelocidadeAdministracao) {
		this.tipoVelocidadeAdministracao = tipoVelocidadeAdministracao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
		public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_INC_VALIDA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_INC_VALIDA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorIncValida() {
		return servidorIncValida;
	}

	public void setServidorIncValida(RapServidores servidorIncValida) {
		this.servidorIncValida = servidorIncValida;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALTERA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAltera() {
		return servidorAltera;
	}
	
	public void setServidorAltera(RapServidores servidorAltera) {
		this.servidorAltera = servidorAltera;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALT_VALIDA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALT_VALIDA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAltValida() {
		return servidorAltValida;
	}

	public void setServidorAltValida(RapServidores servidorAltValida) {
		this.servidorAltValida = servidorAltValida;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INC_VALIDA", length = 7)
	public Date getDthrIncValida() {
		return this.dthrIncValida;
	}

	public void setDthrIncValida(Date dthrIncValida) {
		this.dthrIncValida = dthrIncValida;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ALT_VALIDA", length = 7)
	public Date getDthrAltValida() {
		return this.dthrAltValida;
	}

	public void setDthrAltValida(Date dthrAltValida) {
		this.dthrAltValida = dthrAltValida;
	}

	@Column(name = "ORDEM")
	public Short getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	@Column(name = "IND_BOMBA_INFUSAO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getBombaInfusao() {
		return this.bombaInfusao;
	}

	public void setBombaInfusao(Boolean bombaInfusao) {
		this.bombaInfusao = bombaInfusao;
	}

	@Column(name = "IND_ALT_ANDAMENTO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getAltAndamento() {
		return this.altAndamento;
	}

	public void setAltAndamento(Boolean altAndamento) {
		this.altAndamento = altAndamento;
	}

	@Column(name = "ORDEM_IMPRESSAO")
	public Short getOrdemImpressao() {
		return this.ordemImpressao;
	}

	public void setOrdemImpressao(Short ordemImpressao) {
		this.ordemImpressao = ordemImpressao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PTE_ATD_SEQ", referencedColumnName = "ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PTE_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public MptPrescricaoPaciente getPrescricaoPaciente() {
		return prescricaoPaciente;
	}

	public void setPrescricaoPaciente(MptPrescricaoPaciente prescricaoPaciente) {
		this.prescricaoPaciente = prescricaoPaciente;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "prescricaoMedicamento")
	public Set<MptItemPrescricaoMedicamento> getItensPrescricoesMedicamentos() {
		return this.itensPrescricoesMedicamentos;
	}

	public void setItensPrescricoesMedicamentos(
			Set<MptItemPrescricaoMedicamento> itensPrescricoesMedicamentos) {
		this.itensPrescricoesMedicamentos = itensPrescricoesMedicamentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "prescricaoMedicamento")
	public Set<MptPrescricaoMedicamento> getPrescricoesMedicamentos() {
		return this.prescricoesMedicamentos;
	}

	public void setPrescricoesMedicamentos(
			Set<MptPrescricaoMedicamento> prescricoesMedicamentos) {
		this.prescricoesMedicamentos = prescricoesMedicamentos;
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
		MptPrescricaoMedicamento other = (MptPrescricaoMedicamento) obj;
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
		
		ID("id"),
		PTE_ATD_SEQ("id.pteAtdSeq"),
		PTE_SEQ("id.pteSeq"),
		SEQ("id.seq"),
		PRESCRICAO_MEDICAMENTO("prescricaoMedicamento"),
		PRESCRICAO_MEDICAMENTO_ORIGINAL_PTE_ATD_SEQ("prescricaoMedicamento.id.pteAtdSeq"),
		PRESCRICAO_MEDICAMENTO_ORIGINAL_PTE_SEQ("prescricaoMedicamento.id.pteSeq"),
		PRESCRICAO_MEDICAMENTO_ORIGINAL_SEQ("prescricaoMedicamento.id.seq"),
		CRIADO_EM("criadoEm"),
		DIAS_DE_USO_DOMICILIAR("diasDeUsoDomiciliar"),
		FREQUENCIA("frequencia"),
		HORA_INICIO_ADMINISTRACAO("horaInicioAdministracao"),
		OBSERVACAO("observacao"),
		GOTEJO("gotejo"),
		QTDE_CORRER("qtdeCorrer"),
		ALTERADO_EM("alteradoEm"),
		DTHR_INC_VALIDA("dthrIncValida"),
		DTHR_ALT_VALIDA("dthrAltValida"),
		ORDEM("ordem"),
		ORDEM_IMPRESSAO("ordemImpressao"),
		SITUACAO_ITEM("situacaoItem"),
		UNIDADE_CORRER("unidadeCorrer"),
		SE_NECESSARIO("seNecessario"),
		USO_DOMICILIAR("usoDomiciliar"),
		BOMBA_INFUSAO("bombaInfusao"),
		SOLUCAO("solucao"),
		ALT_ANDAMENTO("altAndamento"),
		COM_SEQ("comSeq"),
		TIPO_FREQ_APRAZAMENTO("tipoFreqAprazamento"),
		TIPO_FREQ_APRAZAMENTO_SEQ("tipoFreqAprazamento.seq"),
		VIA_ADMINISTRACAO("viaAdministracao"),
		VIA_ADMINISTRACAO_SIGLA("viaAdministracao.sigla"),
		SERVIDOR("servidor"),
		SERVIDOR_INC_VALIDA("servidorIncValida"),
		SERVIDOR_ALTERA("servidorAltera"),
		SERVIDOR_ALT_VALIDA("servidorAltValida"),
		TIPO_VELOCIDADE_ADMINISTRACAO("tipoVelocidadeAdministracao"),
		PRESCRICAO_PACIENTE("prescricaoPaciente"),
		AGI_SEQ("agrupaItem.seq"),
		ITENS_PRESCRICOES_MEDICAMENTOS("itensPrescricoesMedicamentos"),
		PRESCRICOES_MEDICAMENTOS("prescricoesMedicamentos");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return fields;
		}
		
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGI_SEQ", referencedColumnName="SEQ", nullable = false)
	public MptAgrupaItem getAgrupaItem() {
		return agrupaItem;
	}

	public void setAgrupaItem(MptAgrupaItem agrupaItem) {
		this.agrupaItem = agrupaItem;
	}

}
