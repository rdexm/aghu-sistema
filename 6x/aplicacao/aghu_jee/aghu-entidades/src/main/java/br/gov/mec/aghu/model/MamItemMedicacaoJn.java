package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mamMdmJnSeq", sequenceName="AGH.MAM_MDM_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_ITEM_MEDICACOES_JN", schema = "AGH")
@Immutable
public class MamItemMedicacaoJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -2880274908732954252L;

	private Integer seq;
	private String descricao;
	private Date criadoEm;
	private String indSituacao;
	private String indDigitaCompl;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer ordem;
	private String tipoDado;
	private Double valorMinimo;
	private Double valorMaximo;

	public MamItemMedicacaoJn() {
	}

	public MamItemMedicacaoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq) {
		this.seq = seq;
	}

	public MamItemMedicacaoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, String descricao,
			Date criadoEm, String indSituacao, String indDigitaCompl, Integer serMatricula, Short serVinCodigo, Integer ordem,
			String tipoDado, Double valorMinimo, Double valorMaximo) {

		this.seq = seq;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.indDigitaCompl = indDigitaCompl;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.ordem = ordem;
		this.tipoDado = tipoDado;
		this.valorMinimo = valorMinimo;
		this.valorMaximo = valorMaximo;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamMdmJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 240)
	@Length(max = 240)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_DIGITA_COMPL", length = 1)
	@Length(max = 1)
	public String getIndDigitaCompl() {
		return this.indDigitaCompl;
	}

	public void setIndDigitaCompl(String indDigitaCompl) {
		this.indDigitaCompl = indDigitaCompl;
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

	@Column(name = "ORDEM")
	public Integer getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@Column(name = "TIPO_DADO", length = 1)
	@Length(max = 1)
	public String getTipoDado() {
		return this.tipoDado;
	}

	public void setTipoDado(String tipoDado) {
		this.tipoDado = tipoDado;
	}

	@Column(name = "VALOR_MINIMO", precision = 17, scale = 17)
	public Double getValorMinimo() {
		return this.valorMinimo;
	}

	public void setValorMinimo(Double valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	@Column(name = "VALOR_MAXIMO", precision = 17, scale = 17)
	public Double getValorMaximo() {
		return this.valorMaximo;
	}

	public void setValorMaximo(Double valorMaximo) {
		this.valorMaximo = valorMaximo;
	}

	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		IND_DIGITA_COMPL("indDigitaCompl"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		ORDEM("ordem"),
		TIPO_DADO("tipoDado"),
		VALOR_MINIMO("valorMinimo"),
		VALOR_MAXIMO("valorMaximo");

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
