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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptTstSq1", sequenceName="AGH.MPT_TST_SQ1", allocationSize = 1)
@Table(name = "MPT_TIPO_SESSAO_TIPO_INTERCOR", schema = "AGH")
public class MptTipoSessaoTipoIntercor extends BaseEntitySeq<Short> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2500122873363377776L;
	
	private Short seq;
	private MptTipoSessao tipoSessao;
	private MptTipoIntercorrencia tipoIntercorrencia;
	private Date criadoEm;
	private RapServidores servidor;
	
	private Short tpsSeq;
	private Short tpiSeq;
	
	public enum Fields {
		
		SEQ("seq"),
		TP_SESSAO("tipoSessao"),
		TP_INTERCOR("tipoIntercorrencia"),
		TPS_SEQ("tipoSessao.seq"),
		TPI_SEQ("tipoIntercorrencia.seq"),
		CRIADO_EM("criadoEm"),
		DESCRICAO_INTERCOR("tipoIntercorrencia.descricao"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	//Getters e Setters
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptTstSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPS_SEQ", nullable = false, referencedColumnName = "SEQ", updatable = false, insertable = false)
	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}
	
	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPI_SEQ", nullable = false, referencedColumnName = "SEQ", updatable = false, insertable = false)
	public MptTipoIntercorrencia getTipoIntercorrencia() {
		return tipoIntercorrencia;
	}
	
	public void setTipoIntercorrencia(MptTipoIntercorrencia tipoIntercorrencia) {
		this.tipoIntercorrencia = tipoIntercorrencia;
	}
	
	@Column(name = "TPS_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getTpsSeq() {
		return tpsSeq;
	}
	
	public void setTpsSeq(Short tpsSeq) {
		this.tpsSeq = tpsSeq;
	}
	
	@Column(name = "TPI_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getTpiSeq() {
		return tpiSeq;
	}
	public void setTpiSeq(Short tpiSeq) {
		this.tpiSeq = tpiSeq;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_MATRICULA", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

}
