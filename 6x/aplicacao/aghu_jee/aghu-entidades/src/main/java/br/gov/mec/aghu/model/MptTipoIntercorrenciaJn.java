package br.gov.mec.aghu.model;

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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="mptTpijnSq", sequenceName="AGH.MPT_TPI_JN_SEQ", allocationSize = 1)
@Table(name = "MPT_TIPO_INTERCORRENCIA_JN", schema = "AGH")
public class MptTipoIntercorrenciaJn extends BaseEntitySeq<Integer> implements java.io.Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3550930050609414363L;

	private Integer seq;
	
	private Integer seqJn;
	
	private String descricao;
	
	private DominioSituacao indSituacao;
	
	private Date criadoEm;
	
	private Integer serVinCodigo;
	
	private Integer vinCodigo;
	
	private RapServidores servidor;
	
	private String usuario;
	
	private Integer version; 
	
	private DominioOperacaoBanco operacao;
	
	public enum Fields {
		
		SEQ("seq"),
		SEQ_JN("seqJn"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		IND_SITUACAO("indSituacao"),
		MATRICULA("serVinCodigo"),
		VIN_CODIGO("vinCodigo"),
		USUARIO("usuario");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	//GETTERS AND SETTERS
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptTpijnSq")
	@Column(name = "SEQ_JN", nullable = false)
	public Integer getSeqJn() {
		return seqJn;
	}

	public void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}
	
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq(){
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Length(max = 60)
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", insertable = false, updatable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", insertable = false, updatable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(
			RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "JN_USER", nullable = false, length = 30)
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JN_DATE_TIME", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false, length = 7)
	public Integer getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, length = 3)
	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "JN_OPERATION", nullable = false)
	public DominioOperacaoBanco getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacaoBanco operacao) {
		this.operacao = operacao;
	}
}
