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
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mtxCopSq1", sequenceName="AGH.MTX_COP_SQ1", allocationSize = 1)
@Table(name = "MTX_COMORBIDADE_PACIENTES", schema = "AGH")
public class MtxComorbidadePaciente extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 553396802712757003L;
	private Integer seq;
	private AipPacientes pacCodigo;
	private MtxComorbidade cmbSeq;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	
	public MtxComorbidadePaciente() {
		
	}

	public MtxComorbidadePaciente(AipPacientes pacCodigo, MtxComorbidade cmbSeq) {
		this.pacCodigo = pacCodigo;
		this.cmbSeq = cmbSeq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxCopSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", referencedColumnName = "CODIGO", nullable = false)
	public AipPacientes getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(AipPacientes pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CMB_SEQ", referencedColumnName = "SEQ", nullable = false)
	public MtxComorbidade getCmbSeq() {
		return cmbSeq;
	}

	public void setCmbSeq(MtxComorbidade cmbSeq) {
		this.cmbSeq = cmbSeq;
	}

	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@ManyToOne(fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
	public static enum Fields {
		
		SEQ("seq"),
		AIP_PACIENTE("pacCodigo"),
		CMB_SEQ("cmbSeq"),
		CRIADO_EM ("criadoEm"),
		SERVIDOR("servidor");

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
		int result = super.hashCode();
		result = prime * result + ((cmbSeq == null) ? 0 : cmbSeq.hashCode());
		result = prime * result
				+ ((pacCodigo == null) ? 0 : pacCodigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		MtxComorbidadePaciente other = (MtxComorbidadePaciente) obj;
		if(obj == null){
			return false;
			
		}else if(this.getCmbSeq() == null || other.getCmbSeq() == null || this.getPacCodigo() == null || other.getPacCodigo() == null){
			return false;
			
		}else if(this.getPacCodigo().getCodigo().equals(other.getPacCodigo().getCodigo()) && this.getCmbSeq().getSeq().equals(other.getCmbSeq().getSeq())){
			return true;
		}
		
		return false;
	}


}
