package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.Transient;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AFA_SINONIMO_MDTOS_JN", schema = "AGH")
@SequenceGenerator(name = "afaSmdJnSeq", sequenceName = "AGH.AFA_SMD_JN_SEQ", allocationSize = 1)
@Immutable
public class AfaSinonimoMedicamentoJn extends BaseJournal implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 779469407181315427L;
	private Integer medMatCodigo;
	private Integer seqp;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	
	//Transient
	private String nomeResponsavel;

	public AfaSinonimoMedicamentoJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "afaSmdJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "MED_MAT_CODIGO", nullable = false, precision = 6, scale = 0)
	public Integer getMedMatCodigo() {
		return this.medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	@Column(name = "SEQP", nullable = false, precision = 6, scale = 0)
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	@Column(name = "SER_MATRICULA", precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60, message = "O tamanho da descrição ultrapassa o limite máximo de 60 caracteres.")
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	/**
	 * FIELDS
	 * 
	 */
	public enum Fields {
		ID("seqJn"), NOME_USUARIO("nomeUsuario"), DATA_ALTERACAO(
				"dataAlteracao"), OPERACAO("operacao"), MED_MAT_CODIGO(
				"medMatCodigo"), SEQP("seqp"), SERVIDOR_MATRICULA(
				"serMatricula"), SERVIDOR_VIN_CODIGO("serVinCodigo"), DESCRICAO(
				"descricao"), CRIADOEM("criadoEm"), SITUACAO("indSituacao"), ;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Transient
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

}
