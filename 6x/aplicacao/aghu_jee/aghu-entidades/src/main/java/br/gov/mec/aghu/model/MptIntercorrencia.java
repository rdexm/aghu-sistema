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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptItcSq1", sequenceName="AGH.MPT_ITC_SQ1", allocationSize = 1)
@Table(name = "MPT_INTERCORRENCIA", schema = "AGH")
public class MptIntercorrencia extends BaseEntitySeq<Short> implements java.io.Serializable {
 
	private static final long serialVersionUID = 7348276507737211372L;

	private Short seq;
	
	private String descricao;
	
	private DominioSituacao indSituacao;
	
	private Date criadoEm;
	
	private RapServidores servidor;
	
	private MptSessao sessao;
	private MptTipoIntercorrencia tipoIntercorrencia;
	
	
	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		IND_SITUACAO("indSituacao"),
		MATRICULA("serVinCodigo"),
		VIN_CODIGO("vinCodigo"),
		SESSAO("sessao"),
		TIPO_INTERCORRENCIA("tipoIntercorrencia");
		
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptItcSq1")
	@Column(name = "SEQ", nullable = false)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
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
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(
			RapServidores servidor) {
		this.servidor = servidor;
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
	@JoinColumn(name = "SES_SEQ", referencedColumnName = "SEQ")
	public MptSessao getSessao() {
		return sessao;
	}

	public void setSessao(MptSessao sessao) {
		this.sessao = sessao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="TPI_SEQ", referencedColumnName = "SEQ")
	public MptTipoIntercorrencia getTipoIntercorrencia() {
		return tipoIntercorrencia;
	}

	public void setTipoIntercorrencia(MptTipoIntercorrencia tipoIntercorrencia) {
		this.tipoIntercorrencia = tipoIntercorrencia;
	}

}