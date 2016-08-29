package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Digits;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioInicioFim;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoMedicaoItemControle;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name = "ECP_CONTROLE_PACIENTES", schema = "AGH")
@SequenceGenerator(name = "ecpRcpSq1", sequenceName = "AGH.ECP_RCP_SQ1", allocationSize = 1)
public class EcpControlePaciente extends BaseEntitySeq<Long> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1373060210884845195L;
	private static final NumberFormat FORMATTER = new DecimalFormat("######0.##");
	
	private Long seq;

	private BigDecimal medicao;
	private DominioSimNao simNao;
	private DominioInicioFim inicioFim;
	private String texto;
	private Boolean foraLimiteNormal;
	private MpmUnidadeMedidaMedica unidadeMedida;

	private EcpItemControle item;
	private EcpHorarioControle horario;

	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	
	private String textoItemForaNormalidade;

	// construtores
	public EcpControlePaciente() {

	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 18, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ecpRcpSq1")
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "MEDICAO", precision = 8, scale = 2)
	@Digits(integer=6, fraction=2, message="Medição dever ter no máximo 6 números inteiros e 2 decimais")
	public BigDecimal getMedicao() {
		return this.medicao;
	}

	public void setMedicao(BigDecimal medicao) {
		this.medicao = medicao;
	}

	@Column(name = "SIM_NAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getSimNao() {
		return simNao;
	}

	public void setSimNao(DominioSimNao simNao) {
		this.simNao = simNao;
	}

	@Column(name = "INICIO_FIM", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioInicioFim getInicioFim() {
		return this.inicioFim;
	}

	public void setInicioFim(DominioInicioFim inicioFim) {
		this.inicioFim = inicioFim;
	}

	@Column(name = "TEXTO", length = 120)
	@Length(max = 120, message = "Sigla deve ter até 120 caracteres.")
	public String getTexto() {
		return this.texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@Column(name = "FORA_LIMITE_NORMAL", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getForaLimiteNormal() {
		return this.foraLimiteNormal;
	}

	public void setForaLimiteNormal(Boolean foraLimiteNormal) {
		this.foraLimiteNormal = foraLimiteNormal;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne
	@JoinColumn(name = "ICE_SEQ", referencedColumnName = "SEQ")
	public EcpItemControle getItem() {
		return item;
	}

	public void setItem(EcpItemControle item) {
		this.item = item;
	}

	@ManyToOne
	@JoinColumn(name = "HCT_SEQ", referencedColumnName = "SEQ")
	public EcpHorarioControle getHorario() {
		return horario;
	}

	public void setHorario(EcpHorarioControle horario) {
		this.horario = horario;
	}

	@ManyToOne
	@JoinColumn(name = "UMM_SEQ", referencedColumnName = "SEQ")
	public MpmUnidadeMedidaMedica getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(MpmUnidadeMedidaMedica unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EcpControlePaciente)) {
			return false;
		}
		EcpControlePaciente castOther = (EcpControlePaciente) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), MEDICAO("medicao"), SIM_NAO("simNao"), INICIO_FIM(
				"inicioFim"), TEXTO("texto"), FORA_LIMITE_NORMAL(
				"foraLimiteNormal"), CRIADO_EM("criadoEm"), SERVIDOR("servidor"), ITEM(
				"item"), HORARIO("horario"), UNIDADE_MEDIDA("unidadeMedida");

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
     * Mostra medição Formatada para relatório ou tela
     * @return
     */
	@Transient
	@SuppressWarnings({"PMD.NPathComplexity"})
	public String getMedicaoFormatada() {
		// Valor
		String valor;
		// apresentando decimais somente se existirem.- suprimindo zeros a
		// esquerda
		if (getMedicao() != null) {
			try {
				valor = String.valueOf(getMedicao().intValueExact());
			} catch (ArithmeticException ae) {
				valor = FORMATTER.format(getMedicao()).replace(".", ",");
			}
		} else {
			valor = null;
		}

		if (DominioTipoMedicaoItemControle.NU
				.equals(getItem().getTipoMedicao())) {
			return valor;
		}

		if (DominioTipoMedicaoItemControle.CA
				.equals(getItem().getTipoMedicao())) {
			return valor;
		}
		// Sim ou Não
		if (DominioTipoMedicaoItemControle.SN
				.equals(getItem().getTipoMedicao())) {
			if (getSimNao() == null) {
				return null;
			}
			if (DominioSimNao.S.equals(getSimNao())) {
				return ("Sim");
			} else {
				return ("Não");
			}
		}
		// Inicio ou Fim
		if (DominioTipoMedicaoItemControle.IF
				.equals(getItem().getTipoMedicao())) {
			if (getInicioFim() == null) {
				return null;
			}
			if (DominioInicioFim.I.equals(getInicioFim())) {
				return ("Início");
			} else {
				return ("Fim");
			}
		}

		// Texto
		if (DominioTipoMedicaoItemControle.TX
				.equals(getItem().getTipoMedicao())) {
			return getTexto();
		}

		// Mixto
		if (DominioTipoMedicaoItemControle.MI
				.equals(getItem().getTipoMedicao())) {
			if (valor != null) {
				return valor;
			} else {
				if (getSimNao() == null) {
					return null;
				}
				if (DominioSimNao.S.equals(getSimNao())) {
					return "Sim";
				}
				if (DominioSimNao.N.equals(getSimNao())) {
					return "Não";
				}
			}
		}
		return valor;
	}

	@Transient
	public String getTextoItemForaNormalidade() {
		return textoItemForaNormalidade;
	}

	public void setTextoItemForaNormalidade(String textoItemForaNormalidade) {
		this.textoItemForaNormalidade = textoItemForaNormalidade;
	}
}