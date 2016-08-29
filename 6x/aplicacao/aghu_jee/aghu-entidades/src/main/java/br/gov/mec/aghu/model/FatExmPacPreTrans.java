package br.gov.mec.aghu.model;

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


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "fatEppSq1", sequenceName = "AGH.FAT_EPP_SQ1")
@Table(name = "FAT_EXM_PAC_PRE_TRANS", schema = "AGH")
public class FatExmPacPreTrans extends BaseEntitySeq<Integer> {

	private static final long serialVersionUID = 4628738769346700275L;
	
	private Integer seq;
	private FatListaPacApac fatListaPacApac;
	private AipPacientes paciente;
	private FatProcedHospInternos procedimentoHospitalarInterno;
	private Date dtRealizacao;
	private Date criadoEm;
	private RapServidores servidor;
	private RapServidores servidorAltera;

	private Integer version;
	
	public FatExmPacPreTrans() {}
	
	public FatExmPacPreTrans(Integer seq, FatListaPacApac fatListaPacApac,
			AipPacientes paciente,
			FatProcedHospInternos procedimentoHospitalarInterno,
			Date dtRealizacao, Date criadoEm, RapServidores servidor,
			RapServidores servidorAltera, Integer version) {
		super();
		this.seq = seq;
		this.fatListaPacApac = fatListaPacApac;
		this.paciente = paciente;
		this.procedimentoHospitalarInterno = procedimentoHospitalarInterno;
		this.dtRealizacao = dtRealizacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.servidorAltera = servidorAltera;
		this.version = version;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fatEppSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LPP_SEQ", nullable = false)
	public FatListaPacApac getFatListaPacApac() {
		return this.fatListaPacApac;
	}

	public void setFatListaPacApac(FatListaPacApac fatListaPacApac) {
		this.fatListaPacApac = fatListaPacApac;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", nullable = false)
	public AipPacientes getPaciente() {
		return this.paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ", nullable = false)
	public FatProcedHospInternos getProcedimentoHospitalarInterno() {
		return procedimentoHospitalarInterno;
	}

	public void setProcedimentoHospitalarInterno(
			final FatProcedHospInternos procedimentoHospitalarInterno) {
		this.procedimentoHospitalarInterno = procedimentoHospitalarInterno;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_REALIZACAO", length = 7)
	public Date getDtRealizacao() {
		return dtRealizacao;
	}

	public void setDtRealizacao(Date dtRealizacao) {
		this.dtRealizacao = dtRealizacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
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
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ALTERA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAltera() {
		return servidorAltera;
	}

	public void setServidorAltera(RapServidores servidorAltera) {
		this.servidorAltera = servidorAltera;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(final Integer version) {
		this.version = version;
	}

	public enum Fields {
		SEQ("seq"), 
		FAT_LISTA_PAC_APAC("fatListaPacApac"), 
		LPP_SEQ("fatListaPacApac.seq"),

		PACIENTE("paciente"), 
		PAC_CODIGO("paciente.codigo"),

		PROCEDIMENTO_HOSPITALAR_INTERNO("procedimentoHospitalarInterno"), 
		PHI_SEQ("procedimentoHospitalarInterno.seq"),

		DATA_REALIZACAO("dtRealizacao"), 
		CRIADO_EM("criadoEm"), 
		
		SERVIDOR("servidor"), 
		SERVIDOR_ALTERA("servidorAltera");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (!(obj instanceof FatExmPacPreTrans)) {
			return false;
		}
		FatExmPacPreTrans other = (FatExmPacPreTrans) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

}
