package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;


@Entity
@Table(name = "SCO_PT_PARADA_SOLICITACOES_JN", schema = "AGH")
@SequenceGenerator(name = "scoPpsJnSeq", sequenceName = "AGH.SCO_PPS_JN_SEQ", allocationSize = 1)
 public class ScoPtParadaSolJn  extends BaseJournal implements java.io.Serializable {
//	public class ScoPtParadaSolJn  implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8897671477394200211L;
	/**
	 * 
	*/	
	
	//private Integer seqJn;
	//private String nomeUsuario;
	//private Date dataAlteracao;
	//private DominioOperacoesJournal operacao;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short codigo;
	private String descricao;
	private Integer version;

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoPpsJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	/*public void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}*/

	
	@Column(name = "IND_SITUACAO", length = 1, nullable = true)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}


	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}


	@Column(name="CODIGO", nullable = false)
	public Short getCodigo() {
		return codigo;
	}



	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}


	@Column(name="DESCRICAO", nullable = false)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
/*	@Column(name = "JN_USER", nullable = false, length = 30)
	public String getNomeUsuario() {
		return nomeUsuario;
	}


	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}


	@Column(name = "JN_DATE_TIME", nullable = false, length = 7)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataAlteracao() {
		return dataAlteracao;
	}


	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	@Column(name = "JN_OPERATION", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}


	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	} */



	public enum Fields {

		CODIGO("codigo"), 
	    DESCRICAO("descricao"), 
	    SITUACAO("situacao"), 
	    VERSION("version"),
  		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),		
		DATE_TIME("dateTime"),
		OPERATION("operation"),
		USER("user");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
