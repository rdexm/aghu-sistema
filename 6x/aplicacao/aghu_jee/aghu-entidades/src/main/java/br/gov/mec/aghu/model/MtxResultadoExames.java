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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="MTX_REX_SEQ", sequenceName="AGH.MTX_REX_SQ1", allocationSize=1)
@Table(name="MTX_RESULTADO_EXAMES", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = "SEQ"))
public class MtxResultadoExames extends BaseEntitySeq<Integer> implements Serializable {
	/**
	 * Criada para estória #47146
	 */
	private static final long serialVersionUID = 8182518615834122799L;
	private Integer seq;
	private MtxTransplantes transplante;
	private String resultado;
	private Date data;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	private Boolean edita;
	
	public MtxResultadoExames() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="MTX_REX_SEQ")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="TRP_SEQ", referencedColumnName="SEQ", nullable=false)
	public MtxTransplantes getTransplante() {
		return transplante;
	}

	public void setTransplante(MtxTransplantes transplante) {
		this.transplante = transplante;
	}
	
	@Column(name = "RESULTADO", length = 60, nullable=false)
	@Length(max = 60)
	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA", nullable=false)
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CRIADO_EM", nullable=false)
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

	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return this.version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {

		SEQ("seq"),
		TRP_SEQ("transplante.seq"),
		RESULTADO("resultado"),
		DATA("data"),
		CRIADO_EM("criadoEm"),
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
		MtxResultadoExames other = (MtxResultadoExames) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}
	
	@Transient
	public Boolean getEdita() {
		return edita;
	}

	public void setEdita(Boolean edita) {
		this.edita = edita;
	}
	
}