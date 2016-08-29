package br.gov.mec.aghu.model;

import java.io.Serializable;
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






import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTransplante;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="mtxPtrSq1", sequenceName="AGH.MTX_PTR_SQ1", allocationSize=1)
@Table(name="MTX_PROCEDIMENTO_TRANSPLANTES", schema = "AGH")
public class MtxProcedimentoTransplantes extends BaseEntitySeq<Integer> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7431039453140228685L;

	private Integer seq;
	private MbcProcedimentoCirurgicos pciSeq;
	private DominioTransplante tipo;
	private DominioTipoOrgao orgao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	
	
	
	public MtxProcedimentoTransplantes() {
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="mtxPtrSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PCI_SEQ", referencedColumnName = "SEQ", nullable = false)
	public MbcProcedimentoCirurgicos getPciSeq() {
		return pciSeq;
	}
	
	@Column(name = "IND_TIPO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioTransplante getTipo() {
		return tipo;
	}
	
	@Column(name = "TIPO_ORGAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioTipoOrgao getOrgao() {
		return orgao;
	}
	
	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}
	
	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return version;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public void setPciSeq(MbcProcedimentoCirurgicos pciSeq) {
		this.pciSeq = pciSeq;
	}
	
	public void setTipo(DominioTransplante tipo) {
		this.tipo = tipo;
	}
	
	public void setOrgao(DominioTipoOrgao orgao) {
		this.orgao = orgao;
	}
	
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		SER_MATRICULA("servidor.id.matricula"),
		PCI_SEQ("pciSeq"),
		TIPO_ORGAO("orgao"),
		IND_SITUACAO("indSituacao"),
		IND_TIPO("tipo");

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
		if (getClass() != obj.getClass()) {
			return false;
		}
		MtxProcedimentoTransplantes other = (MtxProcedimentoTransplantes) obj;
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
