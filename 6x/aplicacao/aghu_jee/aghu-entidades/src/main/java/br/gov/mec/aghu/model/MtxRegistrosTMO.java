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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="mtxRtmSq1", sequenceName="AGH.MTX_RTM_SQ1", allocationSize = 1)
@Table(name = "MTX_REGISTROS_TMO", schema = "AGH")

public class MtxRegistrosTMO extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -1827282045826340635L;
	
	private Integer seq;
	private MtxTransplantes mtxTransplante;
	private Date dataHoraRealizacao;
	private String descricao; 
	private RapServidores servidor;
	private Date criadoEm; 
	private Integer version; 
	

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxRtmSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	@Override
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRP_SEQ", nullable = false)
	public MtxTransplantes getMtxTransplante() {
		return mtxTransplante;
	}

	public void setMtxTransplante(MtxTransplantes mtxTransplante) {
		this.mtxTransplante = mtxTransplante;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_REALIZACAO", nullable = false)
	public Date getDataHoraRealizacao() {
		return dataHoraRealizacao;
	}

	public void setDataHoraRealizacao(Date dataHoraRealizacao) {
		this.dataHoraRealizacao = dataHoraRealizacao;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 500)
	@Length(max = 500)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		MTX_TRANSPLANTE("mtxTransplante"),
		MTX_TRANSPLANTE_SEQ("mtxTransplante.seq"),
		DATA_HORA_REALIZACAO("dataHoraRealizacao"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		SER_MATRICULA("servidor.id.matricula");

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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MtxRegistrosTMO)) {
			return false;
		}
		MtxRegistrosTMO other = (MtxRegistrosTMO) obj;
		if (this.seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!this.seq.equals(other.getSeq())){
			return false;
		}
		return true;
	}
		
}
