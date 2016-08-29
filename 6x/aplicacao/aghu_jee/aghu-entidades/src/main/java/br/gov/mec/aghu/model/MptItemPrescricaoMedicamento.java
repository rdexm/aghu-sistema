package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.core.persistence.BaseEntityId;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

@Entity
@Table(name = "MPT_ITEM_PRESCRICAO_MDTOS", schema = "AGH")
public class MptItemPrescricaoMedicamento extends BaseEntityId<MptItemPrescricaoMedicamentoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3936216640771787515L;
	private MptItemPrescricaoMedicamentoId id;
	private MptPrescricaoMedicamento prescricaoMedicamento;
	private BigDecimal doseParametroCalculo;
	private DominioUnidadeBaseParametroCalculo unidadeBaseParametroCalculo;
	private Byte percentualReducaoDose;
	private Date criadoEm;
	private BigDecimal dose;
	private BigDecimal doseCalculada;
	private String complementoNp;
	private Boolean origemJustificativa;
	private Short duracaoTratamentoSolicitado;
	private Short duracaoTratamentoAprovado;
	private Date dtInicioTratamento;
	private MpmUnidadeMedidaMedica unidadeMedidaMedica;
	private RapServidores servidor;
	private MptParamCalculoPrescricao parametroCalculoPrescricao;
	private AfaMedicamento medicamento;
	private AfaFormaDosagem formaDosagem;
	private Short fcaSeq; // TODO Corrigir este mapeamento quando a tabela MPA_FUNCAO_CALCULOS for migrada.
	private Integer crmComSeq; // TODO Corrigir este mapeamento quando a tabela MPA_CAD_ORD_ITEM_MDTOS for migrada.
	private Short crmSeqp; // TODO Corrigir este mapeamento quando a tabela MPA_CAD_ORD_ITEM_MDTOS for migrada.
	private Integer jusSeq; // TODO Corrigir este mapeamento quando a tabela MPT_JUSTIFICATIVA_USO_MDTOS for migrada.
	private Integer pcsCrmComSeq; // TODO Corrigir este mapeamento quando a tabela MPA_PARAM_CALCULO_DOSES for migrada.
	private Short pcsCrmSeqp; // TODO Corrigir este mapeamento quando a tabela MPA_PARAM_CALCULO_DOSES for migrada.
	private Short pcsSeqp; // TODO Corrigir este mapeamento quando a tabela MPA_PARAM_CALCULO_DOSES for migrada.
	private Integer puoSeq; // TODO Corrigir este mapeamento quando a tabela MPT_PARECER_USO_MDTOS for migrada.
	
	public MptItemPrescricaoMedicamento() {
	}

	public MptItemPrescricaoMedicamento(MptItemPrescricaoMedicamentoId id,
			MptPrescricaoMedicamento prescricaoMedicamento, Date criadoEm,
			Boolean origemJustificativa) {
		this.id = id;
		this.prescricaoMedicamento = prescricaoMedicamento;
		this.criadoEm = criadoEm;
		this.origemJustificativa = origemJustificativa;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public MptItemPrescricaoMedicamento(MptItemPrescricaoMedicamentoId id,
			MptPrescricaoMedicamento prescricaoMedicamento,
			BigDecimal doseParametroCalculo, Byte percentualReducaoDose,
			Date criadoEm, BigDecimal dose, BigDecimal doseCalculada,
			String complementoNp, Integer crmComSeq, Short crmSeqp,
			Integer pcsCrmComSeq, Short pcsCrmSeqp, Short pcsSeqp,
			Integer jusSeq, Integer puoSeq, Short fcaSeq,
			Boolean origemJustificativa, Short duracaoTratamentoSolicitado,
			Short duracaoTratamentoAprovado, Date dtInicioTratamento,
			DominioUnidadeBaseParametroCalculo unidadeBaseParametroCalculo,
			MpmUnidadeMedidaMedica unidadeMedidaMedica, RapServidores servidor,
			MptParamCalculoPrescricao parametroCalculoPrescricao,
			AfaMedicamento medicamento, AfaFormaDosagem formaDosagem) {
	this.id = id;
		this.prescricaoMedicamento = prescricaoMedicamento;
		this.doseParametroCalculo = doseParametroCalculo;
		this.percentualReducaoDose = percentualReducaoDose;
		this.criadoEm = criadoEm;
		this.dose = dose;
		this.doseCalculada = doseCalculada;
		this.complementoNp = complementoNp;
		this.crmComSeq = crmComSeq;
		this.crmSeqp = crmSeqp;
		this.pcsCrmComSeq = pcsCrmComSeq;
		this.pcsCrmSeqp = pcsCrmSeqp;
		this.pcsSeqp = pcsSeqp;
		this.jusSeq = jusSeq;
		this.puoSeq = puoSeq;
		this.fcaSeq = fcaSeq;
		this.origemJustificativa = origemJustificativa;
		this.duracaoTratamentoSolicitado = duracaoTratamentoSolicitado;
		this.duracaoTratamentoAprovado = duracaoTratamentoAprovado;
		this.dtInicioTratamento = dtInicioTratamento;
		this.unidadeBaseParametroCalculo = unidadeBaseParametroCalculo;
		this.formaDosagem = formaDosagem;
		this.unidadeMedidaMedica = unidadeMedidaMedica;
		this.servidor = servidor;
		this.parametroCalculoPrescricao = parametroCalculoPrescricao;
		this.medicamento = medicamento;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pmoPteAtdSeq", column = @Column(name = "PMO_PTE_ATD_SEQ", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "pmoPteSeq", column = @Column(name = "PMO_PTE_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "pmoSeq", column = @Column(name = "PMO_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 3, scale = 0)) })
	public MptItemPrescricaoMedicamentoId getId() {
		return this.id;
	}

	public void setId(MptItemPrescricaoMedicamentoId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PMO_PTE_ATD_SEQ", referencedColumnName = "PTE_ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PMO_PTE_SEQ", referencedColumnName = "PTE_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PMO_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public MptPrescricaoMedicamento getPrescricaoMedicamento() {
		return this.prescricaoMedicamento;
	}

	public void setPrescricaoMedicamento(MptPrescricaoMedicamento prescricaoMedicamento) {
		this.prescricaoMedicamento = prescricaoMedicamento;
	}

	@Column(name = "DOSE_PARAM_CALCULO", precision = 14, scale = 4)
	public BigDecimal getDoseParametroCalculo() {
		return this.doseParametroCalculo;
	}

	public void setDoseParametroCalculo(BigDecimal doseParametroCalculo) {
		this.doseParametroCalculo = doseParametroCalculo;
	}

	@Column(name = "UNID_BASE_PARAM_CALCULO", length = 1)
	@Enumerated(EnumType.STRING)
	@Length(max = 1)
	public DominioUnidadeBaseParametroCalculo getUnidadeBaseParametroCalculo() {
		return this.unidadeBaseParametroCalculo;
	}

	public void setUnidadeBaseParametroCalculo(DominioUnidadeBaseParametroCalculo unidadeBaseParametroCalculo) {
		this.unidadeBaseParametroCalculo = unidadeBaseParametroCalculo;
	}

	@Column(name = "PERC_REDUCAO_DOSE", precision = 2, scale = 0)
	public Byte getPercentualReducaoDose() {
		return this.percentualReducaoDose;
	}

	public void setPercentualReducaoDose(Byte percentualReducaoDose) {
		this.percentualReducaoDose = percentualReducaoDose;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DOSE", precision = 14, scale = 4)
	public BigDecimal getDose() {
		return this.dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	@Column(name = "DOSE_CALCULADA", precision = 14, scale = 4)
	public BigDecimal getDoseCalculada() {
		return this.doseCalculada;
	}

	public void setDoseCalculada(BigDecimal doseCalculada) {
		this.doseCalculada = doseCalculada;
	}

	@Column(name = "COMPLEMENTO_NP", length = 120)
	@Length(max = 120)
	public String getComplementoNp() {
		return this.complementoNp;
	}

	public void setComplementoNp(String complementoNp) {
		this.complementoNp = complementoNp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MED_MAT_CODIGO", nullable = false)
	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	@Column(name = "CRM_COM_SEQ", precision = 8, scale = 0)
	public Integer getCrmComSeq() {
		return this.crmComSeq;
	}

	public void setCrmComSeq(Integer crmComSeq) {
		this.crmComSeq = crmComSeq;
	}

	@Column(name = "CRM_SEQP", precision = 4, scale = 0)
	public Short getCrmSeqp() {
		return this.crmSeqp;
	}

	public void setCrmSeqp(Short crmSeqp) {
		this.crmSeqp = crmSeqp;
	}

	@Column(name = "PCS_CRM_COM_SEQ", precision = 8, scale = 0)
	public Integer getPcsCrmComSeq() {
		return this.pcsCrmComSeq;
	}

	public void setPcsCrmComSeq(Integer pcsCrmComSeq) {
		this.pcsCrmComSeq = pcsCrmComSeq;
	}

	@Column(name = "PCS_CRM_SEQP", precision = 4, scale = 0)
	public Short getPcsCrmSeqp() {
		return this.pcsCrmSeqp;
	}

	public void setPcsCrmSeqp(Short pcsCrmSeqp) {
		this.pcsCrmSeqp = pcsCrmSeqp;
	}

	@Column(name = "PCS_SEQP", precision = 4, scale = 0)
	public Short getPcsSeqp() {
		return this.pcsSeqp;
	}

	public void setPcsSeqp(Short pcsSeqp) {
		this.pcsSeqp = pcsSeqp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FDS_SEQ")
	public AfaFormaDosagem getFormaDosagem() {
		return this.formaDosagem;
	}

	public void setFormaDosagem(AfaFormaDosagem formaDosagem) {
		this.formaDosagem = formaDosagem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMM_SEQ")
	public MpmUnidadeMedidaMedica getUnidadeMedidaMedica() {
		return this.unidadeMedidaMedica;
	}

	public void setUnidadeMedidaMedica(MpmUnidadeMedidaMedica unidadeMedidaMedica) {
		this.unidadeMedidaMedica = unidadeMedidaMedica;
	}

	@Column(name = "JUS_SEQ", precision = 8, scale = 0)
	public Integer getJusSeq() {
		return this.jusSeq;
	}

	public void setJusSeq(Integer jusSeq) {
		this.jusSeq = jusSeq;
	}

	@Column(name = "PUO_SEQ", precision = 8, scale = 0)
	public Integer getPuoSeq() {
		return this.puoSeq;
	}

	public void setPuoSeq(Integer puoSeq) {
		this.puoSeq = puoSeq;
	}

	@Column(name = "FCA_SEQ", precision = 4, scale = 0)
	public Short getFcaSeq() {
		return this.fcaSeq;
	}

	public void setFcaSeq(Short fcaSeq) {
		this.fcaSeq = fcaSeq;
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

	@Column(name = "IND_ORIGEM_JUSTIF", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getOrigemJustificativa() {
		return this.origemJustificativa;
	}

	public void setOrigemJustificativa(Boolean origemJustificativa) {
		this.origemJustificativa = origemJustificativa;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PCR_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PCR_SEQP", referencedColumnName = "SEQP") })
	public MptParamCalculoPrescricao getParametroCalculoPrescricao() {
		return parametroCalculoPrescricao;
	}

	public void setParametroCalculoPrescricao(
			MptParamCalculoPrescricao parametroCalculoPrescricao) {
		this.parametroCalculoPrescricao = parametroCalculoPrescricao;
	}

	@Column(name = "DURACAO_TRAT_SOLICITADO", precision = 3, scale = 0)
	public Short getDuracaoTratamentoSolicitado() {
		return this.duracaoTratamentoSolicitado;
	}

	public void setDuracaoTratamentoSolicitado(Short duracaoTratamentoSolicitado) {
		this.duracaoTratamentoSolicitado = duracaoTratamentoSolicitado;
	}

	@Column(name = "DURACAO_TRAT_APROVADO", precision = 3, scale = 0)
	public Short getDuracaoTratamentoAprovado() {
		return this.duracaoTratamentoAprovado;
	}

	public void setDuracaoTratamentoAprovado(Short duracaoTratamentoAprovado) {
		this.duracaoTratamentoAprovado = duracaoTratamentoAprovado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO_TRATAMENTO", length = 7)
	public Date getDtInicioTratamento() {
		return this.dtInicioTratamento;
	}

	public void setDtInicioTratamento(Date dtInicioTratamento) {
		this.dtInicioTratamento = dtInicioTratamento;
	}

	/**
	 * Formata a dose (14,4) #.###.###.###,####
	 * 
	 * @return
	 */
	@Transient
	public String getDoseFormatada() {
		String numFormated = null;
		if (this.getDose() != null) {
			numFormated = AghuNumberFormat.formatarValor(this.getDose(), this.getClass(), "dose");
		}
		return numFormated;
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
		MptItemPrescricaoMedicamento other = (MptItemPrescricaoMedicamento) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarDados() {
		if (this.origemJustificativa == null) {
			this.origemJustificativa = Boolean.FALSE;
		}
	}

	public enum Fields {

		ID("id"),
		PMO_PTE_ATD_SEQ("id.pmoPteAtdSeq"),
		PMO_PTE_SEQ("id.pmoPteSeq"),
		PMO_SEQ("id.pmoSeq"),
		SEQP("id.seqp"),
		PRESCRICAO_MEDICAMENTO("prescricaoMedicamento"),
		DOSE_PARAMETRO_CALCULO("doseParametroCalculo"),
		UNIDADE_BASE_PARAMETRO_CALCULO("unidadeBaseParametroCalculo"),
		PERCENTUAL_REDUCAO_DOSE("percentualReducaoDose"),
		CRIADO_EM("criadoEm"),
		DOSE("dose"),
		DOSE_CALCULADA("doseCalculada"),
		COMPLEMENTO_NP("complementoNp"),
		ORIGEM_JUSTIFICATIVA("origemJustificativa"),
		DURACAO_TRATAMENTO_SOLICITADO("duracaoTratamentoSolicitado"),
		DURACAO_TRATAMENTO_APROVADO("duracaoTratamentoAprovado"),
		DT_INICIO_TRATAMENTO("dtInicioTratamento"),
		UNIDADE_MEDIDA_MEDICA("unidadeMedidaMedica"),
		SERVIDOR("servidor"),
		PARAMETRO_CALCULO_PRESCRICAO("parametroCalculoPrescricao"),
		MEDICAMENTO("medicamento"),
		MEDICAMENTO_MAT_CODIGO("medicamento.matCodigo"),
		FORMA_DOSAGEM("formaDosagem"),
		FCA_SEQ("fcaSeq"),
		CRM_COM_SEQ("crmComSeq"),
		CRM_SEQP("crmSeqp"),
		JUS_SEQ("jusSeq"),
		PCS_CRMCOM_SEQ("pcsCrmComSeq"),
		PCS_CRM_SEQP("pcsCrmSeqp"),
		PCS_SEQP("pcsSeqp"),
		PUO_SEQ("puoSeq");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return fields;
		}
		
	}


}
