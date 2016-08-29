package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoTransplante;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mtxCmbSq1", sequenceName="AGH.MTX_CMB_SQ1", allocationSize = 1)
@Table(name = "MTX_COMORBIDADES", schema = "AGH")
public class MtxComorbidade extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 553396802712757003L;
	private Integer seq;
	private AghCid cidSeq;
	private String descricao;
	private DominioTipoTransplante tipo;
	private DominioSituacao situacao;
	private Integer escore;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	private String concatDescricao;
	private Set<AipPacientes> aipPaciente;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxCmbSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 300, nullable = false)
	@Length(max = 300)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
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
	
	@JoinColumn(name = "CID_SEQ", referencedColumnName = "SEQ", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY)
	public AghCid getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(AghCid cidSeq) {
		this.cidSeq = cidSeq;
	}
	@Column(name = "IND_TIPO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoTransplante getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoTransplante tipo) {
		this.tipo = tipo;
	}
	
	@Column(name = "ESCORE", precision = 5, nullable = true)
	public Integer getEscore() {
		return escore;
	}

	public void setEscore(Integer escore) {
		this.escore = escore;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "MTX_COMORBIDADE_PACIENTES", schema = "AGH", joinColumns = { @JoinColumn(name = "CMB_SEQ", nullable = false, updatable = false) }, 
	inverseJoinColumns = { @JoinColumn(name = "PAC_CODIGO", nullable = false, updatable = false) })
	public Set<AipPacientes> getAipPaciente() {
		return aipPaciente;
	}

	public void setAipPaciente(Set<AipPacientes> aipPaciente) {
		this.aipPaciente = aipPaciente;
	}
	
	@Transient
	public String getConcatDescricao() {
		return concatDescricao;
	}

	public void setConcatDescricao(String concatDescricao) {
		this.concatDescricao = concatDescricao;
	}

	public static enum Fields {
		
		SEQ("seq"),
		CID_SEQ("cidSeq"),
		DESCRICAO("descricao"),
		TIPO("tipo"),
		AIP_PACIENTE("aipPaciente"),
		SITUACAO("situacao"),
		ESCORE("escore"),
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
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (!super.equals(obj)){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		MtxComorbidade other = (MtxComorbidade) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}

	

}
