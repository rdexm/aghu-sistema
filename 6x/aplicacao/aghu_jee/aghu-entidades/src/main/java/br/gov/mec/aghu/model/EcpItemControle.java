package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioNomeGraficoItemControle;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoMedicaoItemControle;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "ECP_ITEM_CONTROLES", schema = "AGH")
@SequenceGenerator(name = "ecpIceSq1", sequenceName = "AGH.ECP_ICE_SQ1", allocationSize = 1)
public class EcpItemControle extends BaseEntitySeq<Short> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6692805338983061556L;
	private Short seq;
	private String descricao;
	private String sigla;
	private Short ordem;
	private DominioTipoMedicaoItemControle tipoMedicao;
	private DominioNomeGraficoItemControle grafico;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Integer version;

	private EcpGrupoControle grupo;
	private MpmUnidadeMedidaMedica unidadeMedidaMedica;
	private RapServidores servidor;

	private Set<EcpControlePaciente> controlePacientes = new HashSet<EcpControlePaciente>(
			0);

	// construtores
	public EcpItemControle() {
	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 4, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ecpIceSq1")
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 120, nullable = false)
	@Length(max = 120, message = "Descrição deve ter até 120 caracteres.")
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "SIGLA", length = 10, nullable = false)
	@Length(max = 10, message = "Sigla deve ter até 10 caracteres.")
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "ORDEM", length = 4, nullable = false)
	public Short getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	@Column(name = "TIPO_MEDICAO", length = 2, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioTipoMedicaoItemControle getTipoMedicao() {
		return tipoMedicao;
	}

	public void setTipoMedicao(DominioTipoMedicaoItemControle tipoMedicao) {
		this.tipoMedicao = tipoMedicao;
	}

	@Column(name = "GRAFICO", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioNomeGraficoItemControle getGrafico() {
		return grafico;
	}

	public void setGrafico(DominioNomeGraficoItemControle grafico) {
		this.grafico = grafico;
	}

	@Column(name = "SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GCO_SEQ", referencedColumnName = "SEQ", nullable = false)
	public EcpGrupoControle getGrupo() {
		return grupo;
	}

	public void setGrupo(EcpGrupoControle grupo) {
		this.grupo = grupo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMM_SEQ", referencedColumnName = "SEQ")
	public MpmUnidadeMedidaMedica getUnidadeMedidaMedica() {
		return unidadeMedidaMedica;
	}

	public void setUnidadeMedidaMedica(
			MpmUnidadeMedidaMedica unidadeMedidaMedica) {
		this.unidadeMedidaMedica = unidadeMedidaMedica;
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

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
	public Set<EcpControlePaciente> getControlePacientes() {
		return controlePacientes;
	}

	public void setControlePacientes(Set<EcpControlePaciente> controlePacientes) {
		this.controlePacientes = controlePacientes;
	}

	@Transient
	public String getDescricaoEditada() {
		StringBuffer result = new StringBuffer(getSigla());
		if (this.getUnidadeMedidaMedica() != null) {
			result.append(" (")
				.append(this.getUnidadeMedidaMedica().getDescricao()).append(')');
		}
		return result.toString();
	}

	@Transient
	public boolean isNumerico() {
		if (DominioTipoMedicaoItemControle.NU.equals(tipoMedicao)) {
			return true;
		}
		return false;
	}

	@Transient
	public boolean isTexto() {
		if (DominioTipoMedicaoItemControle.TX.equals(tipoMedicao)) {
			return true;
		}
		return false;
	}

	@Transient
	public boolean isSimNao() {
		if (DominioTipoMedicaoItemControle.SN.equals(tipoMedicao)) {
			return true;
		}
		return false;
	}

	@Transient
	public boolean isInicioFim() {
		if (DominioTipoMedicaoItemControle.IF.equals(tipoMedicao)) {
			return true;
		}
		return false;
	}

	@Transient
	public boolean isCalculado() {
		if (DominioTipoMedicaoItemControle.CA.equals(tipoMedicao)) {
			return true;
		}
		return false;
	}

	@Transient
	public boolean isMisto() {
		if (DominioTipoMedicaoItemControle.MI.equals(tipoMedicao)) {
			return true;
		}
		return false;
	}
	
	@Transient
	public boolean isMistoTexto() {
		if (DominioTipoMedicaoItemControle.MT.equals(tipoMedicao)) {
			return true;
		}
		return false;
	}

	// outros
	@Transient
	public String getDescricaoSiglaUnidadeMedida(){
		if(this.getUnidadeMedidaMedica()!=null){
			return this.getSigla() +  " (" + this.getUnidadeMedidaMedica().getDescricao() + ")";
		}else{
			return this.getSigla();
		}
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Transient
	public String getDescricaoUnidadeMedicaGrupo() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getDescricao())
		.append(" (");

		if (this.getUnidadeMedidaMedica() != null) {
			sb.append(this.getUnidadeMedidaMedica().getDescricao());

			if (this.getGrupo() != null) {
				sb.append(" - ");
			}
		}

		if (this.getGrupo() != null) {
			sb.append("Grupo ");
			sb.append(this.getGrupo().getDescricao());
		}

		sb.append(')');
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EcpItemControle)) {
			return false;
		}
		EcpItemControle castOther = (EcpItemControle) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), DESCRICAO("descricao"), SIGLA("sigla"), ORDEM("ordem"), TIPO_MEDICAO(
				"tipoMedicao"), GRAFICO("grafico"), SITUACAO("situacao"), CRIADO_EM(
				"criadoEm"), GRUPO("grupo"), UNIDADE_MEDIDA_MEDICA(
				"unidadeMedidaMedica"), SERVIDOR("servidor"), CONTROLE_PACIENTES(
				"controlePacientes");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}