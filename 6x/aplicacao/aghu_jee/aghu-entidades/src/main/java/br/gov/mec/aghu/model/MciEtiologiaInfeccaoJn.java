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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aghMciEinJnSeq", sequenceName="AGH.MCI_EIN_JN_SEQ", allocationSize = 1)
@Table(name = "MCI_ETIOLOGIA_INFECCOES_JN", schema = "AGH")
@Immutable
public class MciEtiologiaInfeccaoJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4772943501093480801L;
	
	private String codigo;
	private String descricao;
	private Short unfSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Date alteradoEm;
	private String textoNotificacao;
	private Integer serMatriculaMovimentado;
	private Short serVinCodigoMovimentado;

	public MciEtiologiaInfeccaoJn() {
	}

	public MciEtiologiaInfeccaoJn(String codigo, String descricao, Short unfSeq, Integer serMatricula, Short serVinCodigo, DominioSituacao situacao,
			Date criadoEm, Date alteradoEm, String textoNotificacao, Integer serMatriculaMovimentado, Short serVinCodigoMovimentado) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.unfSeq = unfSeq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.textoNotificacao = textoNotificacao;
		this.serMatriculaMovimentado = serMatriculaMovimentado;
		this.serVinCodigoMovimentado = serVinCodigoMovimentado;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghMciEinJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "TIPO", nullable = false, length = 2)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "UNF_SEQ")
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	
	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@Column(name = "TEXTO_NOTIFICACAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getTextoNotificacao() {
		return this.textoNotificacao;
	}

	public void setTextoNotificacao(String textoNotificacao) {
		this.textoNotificacao = textoNotificacao;
	}
	
	@Column(name = "SER_MATRICULA_MOVIMENTADO")
	public Integer getSerMatriculaMovimentado() {
		return serMatriculaMovimentado;
	}

	public void setSerMatriculaMovimentado(Integer serMatriculaMovimentado) {
		this.serMatriculaMovimentado = serMatriculaMovimentado;
	}

	@Column(name = "SER_VIN_CODIGO_MOVIMENTADO")
	public Short getSerVinCodigoMovimentado() {
		return serVinCodigoMovimentado;
	}
	
	public void setSerVinCodigoMovimentado(Short serVinCodigoMovimentado) {
		this.serVinCodigoMovimentado = serVinCodigoMovimentado;
	}

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		SITUACAO("Situacao"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

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
