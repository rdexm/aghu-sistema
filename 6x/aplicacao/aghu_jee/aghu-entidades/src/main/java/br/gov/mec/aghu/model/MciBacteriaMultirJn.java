package br.gov.mec.aghu.model;

// Generated 11/06/2010 10:31:39 by Hibernate Tools 3.2.5.Beta

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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mciBacteriaMultirJn", sequenceName="AGH.MCI_BMR_JN_SEQ", allocationSize = 1)
@Table(name = "MCI_BACTERIA_MULTIR_JN", schema = "AGH")
public class MciBacteriaMultirJn extends BaseJournal{


	/**
	 * 
	 */
	private static final long serialVersionUID = 3659805006299400068L;
	private Integer seqInt;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaMovimentado;
	private Short serVinCodigoMovimentado;
	
	public MciBacteriaMultirJn() {
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciBacteriaMultirJn")
	@Column(name = "SEQ_JN", nullable = false, precision = 12, scale = 0)
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", nullable = false)
	public Integer getSeqInt() {
		return seqInt;
	}

	public void setSeqInt(Integer seqInt) {
		this.seqInt = seqInt;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	
	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 4, scale = 0)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "SER_MATRICULA_MOVIMENTADO")
	public Integer getSerMatriculaMovimentado() {
		return serMatriculaMovimentado;
	}

	public void setSerMatriculaMovimentado(Integer serMatriculaMovimentado) {
		this.serMatriculaMovimentado = serMatriculaMovimentado;
	}

	@Column(name = "SER_VIN_CODIGO_MOVIMENTADO", precision = 4, scale = 0)
	public Short getSerVinCodigoMovimentado() {
		return serVinCodigoMovimentado;
	}

	public void setSerVinCodigoMovimentado(Short serVinCodigoMovimentado) {
		this.serVinCodigoMovimentado = serVinCodigoMovimentado;
	}

	public enum Fields {
		SEQ_JN("seq"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),
		SEQ("seqInt"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA_MOVIMENTADO("serMatriculaMovimentado"),
		SER_VIN_CODIGO_MOVIMENTADO("serVinCodigoMovimentado");

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
