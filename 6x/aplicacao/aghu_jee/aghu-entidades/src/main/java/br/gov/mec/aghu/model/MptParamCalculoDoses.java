package br.gov.mec.aghu.model;

import java.math.BigDecimal;
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

import br.gov.mec.aghu.dominio.DominioCalculoDose;
import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.dominio.DominioUnidadeIdade;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
/**
 * 
 * @author rafael.nascimento
 *
 */
@Entity
@Table(name = "MPT_PARAM_CALCULO_DOSES", schema = "AGH")
@SequenceGenerator(name="mptPcdSq1", sequenceName="AGH.MPT_PCD_SQ1", allocationSize = 1)
public class MptParamCalculoDoses extends BaseEntitySeq<Integer> implements java.io.Serializable{ 
	 
	private static final long serialVersionUID = 447048653631317631L;
	
	private Integer seq;
	private BigDecimal dose;
	private DominioUnidadeBaseParametroCalculo unidBaseCalculo;
	private AfaFormaDosagem  afaFormaDosagem;
	private Integer fdsSeq;
	private DominioCalculoDose tipoCalculo;
	private Short idadeMinima;
	private Short idadeMaxima;
	private BigDecimal pesoMinimo;
	private DominioUnidadeIdade unidIdade;
	private BigDecimal pesoMaximo;
	private BigDecimal doseMaximaUnitaria;
	private MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos;
	private Long pmiSeq;
	private String alertaCalculoDose;
	private RapServidores servidor;
	private Date criadoEm;
	private Integer version;
	
	//GETTERS AND SETTERS
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptPcdSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "DOSE", precision = 14, scale = 4)
	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}
	
	@Column(name = "UNID_BASE_CALCULO")
	@Enumerated(EnumType.STRING)
	public DominioUnidadeBaseParametroCalculo getUnidBaseCalculo() {
		return unidBaseCalculo;
	}

	public void setUnidBaseCalculo(DominioUnidadeBaseParametroCalculo unidBaseCalculo) {
		this.unidBaseCalculo = unidBaseCalculo;
	}
	
	@Column(name = "TIPO_CALCULO")
	@Enumerated(EnumType.STRING)
	public DominioCalculoDose getTipoCalculo() {
		return tipoCalculo;
	}
	
	public void setTipoCalculo(DominioCalculoDose tipoCalculo) {
		this.tipoCalculo = tipoCalculo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FDS_SEQ", nullable = false)
	public AfaFormaDosagem getAfaFormaDosagem() {
		return afaFormaDosagem;
	}

	public void setAfaFormaDosagem(AfaFormaDosagem afaFormaDosagem) {
		this.afaFormaDosagem = afaFormaDosagem;
	}
	
	@Column(name = "FDS_SEQ", insertable = false, updatable = false)
	public Integer getTvaSeq() {
		return fdsSeq;
	}

	public void setTvaSeq(Integer fdsSeq) {
		this.fdsSeq = fdsSeq;
	}
	
	@Column(name = "IDADE_MINIMA", nullable = false, precision = 3, scale = 0)
	public Short getIdadeMinima() {
		return idadeMinima;
	}

	public void setIdadeMinima(Short idadeMinima) {
		this.idadeMinima = idadeMinima;
	}
	
	@Column(name = "IDADE_MAXIMA", nullable = false, precision = 3, scale = 0)
	public Short getIdadeMaxima() {
		return idadeMaxima;
	}
	
	public void setIdadeMaxima(Short idadeMaxima) {
		this.idadeMaxima = idadeMaxima;
	}
	
	@Column(name = "PESO_MINIMO", precision = 6, scale = 3)
	public BigDecimal getPesoMinimo() {
		return pesoMinimo;
	}
	
	public void setPesoMinimo(BigDecimal pesoMinimo) {
		this.pesoMinimo = pesoMinimo;
	}
	
	@Column(name = "UNID_IDADE")
	@Enumerated(EnumType.STRING)
	public DominioUnidadeIdade getUnidIdade() {
		return unidIdade;
	}

	public void setUnidIdade(DominioUnidadeIdade unidIdade) {
		this.unidIdade = unidIdade;
	}
	
	@Column(name = "PESO_MAXIMO", precision = 6, scale = 3)
	public BigDecimal getPesoMaximo() {
		return pesoMaximo;
	}
	
	public void setPesoMaximo(BigDecimal pesoMaximo) {
		this.pesoMaximo = pesoMaximo;
	}
	
	@Column(name = "DOSE_MAXIMA_UNITARIA", precision = 14, scale = 4)
	public BigDecimal getDoseMaximaUnitaria() {
		return doseMaximaUnitaria;
	}
	
	public void setDoseMaximaUnitaria(BigDecimal doseMaximaUnitaria) {
		this.doseMaximaUnitaria = doseMaximaUnitaria;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PMI_SEQ", nullable = false)
	public MptProtocoloItemMedicamentos getMptProtocoloItemMedicamentos() {
		return mptProtocoloItemMedicamentos;
	}

	public void setMptProtocoloItemMedicamentos(MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos) {
		this.mptProtocoloItemMedicamentos = mptProtocoloItemMedicamentos;
	}
	
	@Column(name = "PMI_SEQ", insertable = false, updatable = false)
	public Long getPmiSeq() {
		return pmiSeq;
	}

	public void setPmiSeq(Long pmiSeq) {
		this.pmiSeq = pmiSeq;
	}
	
	@Column(name = "ALERTA_CALCULO_DOSE", length = 240)	
	public String getAlertaCalculoDose() {
		return alertaCalculoDose;
	}

	public void setAlertaCalculoDose(String alertaCalculoDose) {
		this.alertaCalculoDose = alertaCalculoDose;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	//FIELDS
	public enum Fields {

		SEQ("seq"),
		DOSE("dose"),
		UNID_BASE_CALCULO("unidBaseCalculo"),
		AFA_FORMA_DOSAGEM("afaFormaDosagem"),
		AFA_FORMA_DOSAGEM_SEQ("afaFormaDosagem.seq"),
		FDS_SEQ("fdsSeq"),
		TIPO_CALCULO("tipoCalculo"),
		IDADE_MINIMA("idadeMinima"),
		IDADE_MAXIMA("idadeMaxima"),
		PESO_MINIMO("pesoMinimo"),
		UNID_IDADE("unidIdade"),
		PESO_MAXIMO("pesoMaximo"),
		DOSE_MAXIMA_UNITARIA("doseMaximaUnitaria"),
		MPT_PROTOCOLO_ITEM_MEDICAMENTOS("mptProtocoloItemMedicamentos"),
		MPT_PROTOCOLO_ITEM_MEDICAMENTOS_SEQ("mptProtocoloItemMedicamentos.seq"),
		PMI_SEQ("pmiSeq"),
		ALERTA_CALCULO_DOSE("alertaCalculoDose"),
		SERVIDOR("servidor"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_CODIGO("servidor.id.vinCodigo"),
		CRIADO_EM("criadoEm"),
		VERSION("version"),
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	//HASHCODE
	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(this.getDose());
		hashCodeBuilder.append(this.getUnidBaseCalculo());
		hashCodeBuilder.append(this.getAfaFormaDosagem());
		return hashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MptParamCalculoDoses other = (MptParamCalculoDoses) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getDose(), other.getDose());
		equalsBuilder.append(this.getUnidBaseCalculo(), other.getUnidBaseCalculo());
		equalsBuilder.append(this.getAfaFormaDosagem(), other.getAfaFormaDosagem());
		return equalsBuilder.isEquals();
	}
}
