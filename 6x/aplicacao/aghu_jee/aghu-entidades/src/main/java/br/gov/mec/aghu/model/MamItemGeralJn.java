package br.gov.mec.aghu.model;

import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioIndDigitaCompl;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDadoParametro;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name = "mamItgJnSeq", sequenceName = "AGH.MAM_ITG_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_ITEM_GERAIS_JN", schema = "AGH")
@Immutable
public class MamItemGeralJn extends BaseJournal {
	private static final long serialVersionUID = -1418855896591240749L;

	private Integer seq;
	private Integer serMatricula;
	private Short serVinCodigo;	
	private String descricao;
	private Short ordem;
	private DominioSituacao indSituacao;
	private DominioTipoDadoParametro tipoDado;
	private Date criadoEm;
	private BigDecimal valorMinimo;
	private BigDecimal valorMaximo;
	private DominioIndDigitaCompl indDigitaCompl;

	public MamItemGeralJn() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamItgJnSeq")
	@Column(name = "SEQ_JN", unique = true)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 240)
	@NotNull
	@Length(max = 240)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ORDEM", nullable = false)
	public Short getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length=1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Column(name = "TIPO_DADO", length=1)
	@Enumerated(EnumType.STRING)
	public DominioTipoDadoParametro getTipoDado() {
		return tipoDado;
	}

	public void setTipoDado(DominioTipoDadoParametro tipoDado) {
		this.tipoDado = tipoDado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Column(name = "VALOR_MINIMO", precision = 10, scale = 4)
	public BigDecimal getValorMinimo() {
		return this.valorMinimo;
	}

	public void setValorMinimo(BigDecimal valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	@Column(name = "VALOR_MAXIMO", precision = 10, scale = 4)
	public BigDecimal getValorMaximo() {
		return this.valorMaximo;
	}

	public void setValorMaximo(BigDecimal valorMaximo) {
		this.valorMaximo = valorMaximo;
	}
	
	@Column(name = "IND_DIGITA_COMPL", length=1)
	@Enumerated(EnumType.STRING)
	public DominioIndDigitaCompl getIndDigitaCompl() {
		return indDigitaCompl;
	}

	public void setIndDigitaCompl(DominioIndDigitaCompl indDigitaCompl) {
		this.indDigitaCompl = indDigitaCompl;
	}

	public enum Fields {

		SEQ("seq"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA("serMatricula"),
		DESCRICAO("descricao"),
		ORDEM("ordem"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm");

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
