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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MCI_TIPO_GRUPO_PROC_RISCOS_JN", schema = "AGH")
@SequenceGenerator(name = "mciTpgJnSeq", sequenceName = "AGH.MCI_TGP_JN_SEQ", allocationSize = 1)
public class MciTipoGrupoProcedRiscoJn extends BaseJournal {

	private static final long serialVersionUID = 7006608037814759657L;

	private Short seq;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date alteradoEm;
	private Integer serMatriculaMovimentado;
	private Short serVinCodigoMovimentado;
	private String descricao;
	
	public MciTipoGrupoProcedRiscoJn() {}


	public MciTipoGrupoProcedRiscoJn(String nomeUsuario, DominioOperacoesJournal operacao, Short seq, DominioSituacao indSituacao,
			Date criadoEm, Integer serMatricula, Short serVinCodigo,
			Date alteradoEm, Integer serMatriculaMovimentado,
			Short serVinCodigoMovimentado, String descricao) {
		super();
		setNomeUsuario(nomeUsuario);
		setOperacao(operacao);
		this.seq = seq;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.alteradoEm = alteradoEm;
		this.serMatriculaMovimentado = serMatriculaMovimentado;
		this.serVinCodigoMovimentado = serVinCodigoMovimentado;
		this.descricao = descricao;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciTpgJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", nullable = false)
	public Short getSeq() {
		return seq;
	}
	
		
	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return serMatricula;
	}
	
	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)	
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return alteradoEm;
	}
	
	@Column(name = "SER_MATRICULA_MOVIMENTADO")
	public Integer getSerMatriculaMovimentado() {
		return serMatriculaMovimentado;
	}
	
	@Column(name = "SER_VIN_CODIGO_MOVIMENTADO")
	public Short getSerVinCodigoMovimentado() {
		return serVinCodigoMovimentado;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public void setSerMatriculaMovimentado(Integer serMatriculaMovimentado) {
		this.serMatriculaMovimentado = serMatriculaMovimentado;
	}

	public void setSerVinCodigoMovimentado(Short serVinCodigoMovimentado) {
		this.serVinCodigoMovimentado = serVinCodigoMovimentado;
	}
	
	@Column(name = "DESCRICAO",length = 60)	
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public static enum Fields {
		SEQ("seq"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA_MOVIMENTADO("serMatriculaMovimentado"),
		SER_VIN_CODIGO_MOVIMENTADO("serVinCodigoMovimentado")
		;
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
		
	}
}
