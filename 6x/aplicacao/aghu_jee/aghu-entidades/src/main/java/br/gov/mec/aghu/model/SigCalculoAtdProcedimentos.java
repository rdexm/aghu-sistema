package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCcpSq1", sequenceName = "SIG_CCP_SQ1", allocationSize = 1)
@Table(name = "SIG_CALCULO_ATD_PROCEDIMENTOS", schema = "agh")
public class SigCalculoAtdProcedimentos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 831891794517398074L;
	
	private Integer seq;
	private SigCalculoAtdPaciente calculoAtdPaciente;
	
	private FatProcedHospInternos procedimentoHospitalarInterno;
	private Integer phiSeq;
	
	private FatContasHospitalares contaHospitalar;
	private Integer cthSeq;
	
	private CntaConv contaConvenio;
	private Integer ctaNro;
	
	private FatItensProcedHospitalar itemProcedimentoHospitalar;
	private Short iphPhoSeq;
	private Integer iphSeq;
	
	private Date criadoEm;
	private RapServidores rapServidores;
	private Boolean principal;
	private Integer version;
	
	public SigCalculoAtdProcedimentos() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCcpSq1")
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

	@Column(name = "IND_PRINCIPAL", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAC_SEQ", nullable = false)
	public SigCalculoAtdPaciente getCalculoAtdPaciente() {
		return calculoAtdPaciente;
	}

	public void setCalculoAtdPaciente(SigCalculoAtdPaciente calculoAtdPaciente) {
		this.calculoAtdPaciente = calculoAtdPaciente;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ", nullable = true, insertable=false, updatable=false)
	public FatProcedHospInternos getProcedimentoHospitalarInterno() {
		return this.procedimentoHospitalarInterno;
	}

	public void setProcedimentoHospitalarInterno(FatProcedHospInternos procedHospInterno) {
		this.procedimentoHospitalarInterno = procedHospInterno;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CTH_SEQ", nullable = true, insertable=false, updatable=false)
	public FatContasHospitalares getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(FatContasHospitalares contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CTA_NRO", nullable = true, insertable=false, updatable=false)
	public CntaConv getContaConvenio() {
		return contaConvenio;
	}

	public void setContaConvenio(CntaConv contaConvenio) {
		this.contaConvenio = contaConvenio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "IPH_PHO_SEQ", nullable=true, insertable = false, updatable = false),
			@JoinColumn(name = "IPH_SEQ", nullable=true, insertable = false, updatable = false) })
	public FatItensProcedHospitalar getItemProcedimentoHospitalar() {
		return this.itemProcedimentoHospitalar;
	}

	public void setItemProcedimentoHospitalar(FatItensProcedHospitalar itemProcedimentoHospitalar) {
		this.itemProcedimentoHospitalar = itemProcedimentoHospitalar;
	}
	
	@Column(name = "PHI_SEQ", nullable = true)
	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	@Column(name = "CTH_SEQ", nullable = true)
	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	@Column(name = "CTA_NRO", nullable = true)
	public Integer getCtaNro() {
		return ctaNro;
	}

	public void setCtaNro(Integer ctaNro) {
		this.ctaNro = ctaNro;
	}

	@Column(name = "IPH_PHO_SEQ", nullable = true)
	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	@Column(name = "IPH_SEQ", nullable = true)
	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public enum Fields {

		SEQ("seq"), 
		CRIADO_EM("criadoEm"), 
		SERVIDOR("rapServidores"), 
		PRINCIPAL("principal"),
		CALCULO_ATD_PACIENTE("calculoAtdPaciente"),
		ITEM_PROCEDIMENTO_HOSPITALAR("itemProcedimentoHospitalar"),
		IPH_PHO_SEQ("iphPhoSeq"),
		IPH_SEQ("iphSeq"),
		CONTA_HOSPITAL("contaHospitalar"),
		CTH_SEQ("cthSeq"),
		CONTA_CONVENIO("contaConvenio"),
		CTA_NRO("ctaNro");

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
		if (!(obj instanceof SigCalculoAtdProcedimentos)) {
			return false;
		}
		SigCalculoAtdProcedimentos other = (SigCalculoAtdProcedimentos) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}

}
