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
@Table(name = "AGH_NOTIFICACAO_DESTINOS", schema = "AGH")
@SequenceGenerator(name="aghNtdSeq1", sequenceName="AGH.agh_ntd_seq1", allocationSize = 1)
public class AghNotificacaoDestinos extends BaseEntitySeq<Integer> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 845749893159001437L;
	
	private Integer seq;
	private Date criadoEm;
	private Date alteradoEm;
	private RapServidores servidor;
	private RapServidores servidorContato;
	private Short dddCelular;
	private Long celular;
	private AghNotificacoes notificacao;
	private Integer version;
	
	public AghNotificacaoDestinos() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghNtdSeq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
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
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_CONTATO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_CONTATO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorContato() {
		return servidorContato;
	}

	public void setServidorContato(RapServidores servidorContato) {
		this.servidorContato = servidorContato;
	}
	
	@Column(name = "DDD_CELULAR", length=3, nullable = false)
	public Short getDddCelular() {
		return dddCelular;
	}

	public void setDddCelular(Short dddCelular) {
		this.dddCelular = dddCelular;
	}

	@Column(name = "CELULAR", length=15, nullable = false)
	public Long getCelular() {
		return celular;
	}

	public void setCelular(Long celular) {
		this.celular = celular;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NTS_SEQ", nullable = false)
	public AghNotificacoes getNotificacao() {
		return notificacao;
	}

	public void setNotificacao(AghNotificacoes notificacao) {
		this.notificacao = notificacao;
	}

	public enum Fields {
		SEQ("seq"),
		SERVIDOR_CONTATO("servidorContato"),
		DDD_CELULAR("dddCelular"),
		CELULAR("celular"),
		NTS_SEQ("notificacao.seq"),
		MATRICULA_CONTATO("servidorContato.id.matricula"),
		VIN_CODIGO_CONTATO("servidorContato.id.vinCodigo");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return fields;
		}
		
	}

}
