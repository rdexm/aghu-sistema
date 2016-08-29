package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCdcSq1", sequenceName = "SIG_CDC_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_detalhe_consumos", schema = "agh")
public class SigCalculoDetalheConsumo extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4423351486063029177L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;

	private FatProcedHospInternos procedHospInterno;
	private SigAtividades atividade;
	private SigCalculoAtdConsumo calculoAtividadeConsumo;
	private FccCentroCustos centroCustos;
	private ScoMaterial scoMaterial;
	private SigGrupoOcupacoes sigGrupoOcupacoes;
	private RapOcupacaoCargo rapOcupacaoCargo;
	private SigCategoriaRecurso categoriaRecurso;
	private String codPatrimonio;
	private String identificador;

	private BigDecimal qtdePrevisto = BigDecimal.ZERO;
	private BigDecimal qtdeDebitado = BigDecimal.ZERO;
	private BigDecimal qtdeConsumido = BigDecimal.ZERO;
	private BigDecimal valorPrevisto = BigDecimal.ZERO;
	private BigDecimal valorDebitado = BigDecimal.ZERO;
	private BigDecimal valorConsumido = BigDecimal.ZERO;
	
	private Boolean indExterno;

	public SigCalculoDetalheConsumo() {
	}
	
	public SigCalculoDetalheConsumo(Integer seq) {
		this.seq = seq;
	}

	public SigCalculoDetalheConsumo(Integer seq, BigDecimal qtdePrevisto, BigDecimal qtdeDebitado) {
		this.seq = seq;
		this.qtdePrevisto = qtdePrevisto;
		this.qtdeDebitado = qtdeDebitado;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCdcSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "version", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ",  nullable = true, referencedColumnName = "SEQ")
	public FatProcedHospInternos getProcedHospInterno() {
		return this.procedHospInterno;
	}

	public void setProcedHospInterno(FatProcedHospInternos procedHospInterno) {
		this.procedHospInterno = procedHospInterno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVD_SEQ")
	public SigAtividades getAtividade() {
		return atividade;
	}

	public void setAtividade(SigAtividades atividade) {
		this.atividade = atividade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCA_SEQ")
	public SigCalculoAtdConsumo getCalculoAtividadeConsumo() {
		return calculoAtividadeConsumo;
	}

	public void setCalculoAtividadeConsumo(SigCalculoAtdConsumo calculoAtividadeConsumo) {
		this.calculoAtividadeConsumo = calculoAtividadeConsumo;
	}

	@Column(name = "VLR_PREVISTO", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorPrevisto() {
		return valorPrevisto;
	}

	public void setValorPrevisto(BigDecimal valorPrevisto) {
		this.valorPrevisto = valorPrevisto;
	}

	@Column(name = "VLR_CONSUMIDO", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorConsumido() {
		return valorConsumido;
	}

	public void setValorConsumido(BigDecimal valorConsumido) {
		this.valorConsumido = valorConsumido;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCustos() {
		return centroCustos;
	}

	public void setCentroCustos(FccCentroCustos centroCustos) {
		this.centroCustos = centroCustos;
	}

	@Column(name = "COD_PATRIMONIO", length = 60)
	public String getCodPatrimonio() {
		return this.codPatrimonio;
	}

	public void setCodPatrimonio(String codPatrimonio) {
		this.codPatrimonio = codPatrimonio;
	}
	
	@Column(name = "IDENTIFICADOR", length = 60)
	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mat_codigo", referencedColumnName = "codigo")
	public ScoMaterial getScoMaterial() {
		return this.scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GOC_SEQ", referencedColumnName = "SEQ")
	public SigGrupoOcupacoes getSigGrupoOcupacoes() {
		return sigGrupoOcupacoes;
	}

	public void setSigGrupoOcupacoes(SigGrupoOcupacoes sigGrupoOcupacoes) {
		this.sigGrupoOcupacoes = sigGrupoOcupacoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "OCA_CAR_CODIGO", referencedColumnName = "CAR_CODIGO"), @JoinColumn(name = "OCA_CODIGO", referencedColumnName = "CODIGO") })
	public RapOcupacaoCargo getRapOcupacaoCargo() {
		return rapOcupacaoCargo;
	}

	public void setRapOcupacaoCargo(RapOcupacaoCargo rapOcupacaoCargo) {
		this.rapOcupacaoCargo = rapOcupacaoCargo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATR_SEQ", referencedColumnName = "SEQ")
	public SigCategoriaRecurso getCategoriaRecurso() {
		return categoriaRecurso;
	}

	public void setCategoriaRecurso(SigCategoriaRecurso categoriaRecurso) {
		this.categoriaRecurso = categoriaRecurso;
	}

	@Column(name = "qtde_previsto", nullable = false, precision = 10, scale = 4)
	public BigDecimal getQtdePrevisto() {
		return qtdePrevisto;
	}

	public void setQtdePrevisto(BigDecimal qtdePrevisto) {
		this.qtdePrevisto = qtdePrevisto;
	}

	@Column(name = "qtde_debitado", nullable = false, precision = 10, scale = 4)
	public BigDecimal getQtdeDebitado() {
		return qtdeDebitado;
	}

	public void setQtdeDebitado(BigDecimal qtdeDebitado) {
		this.qtdeDebitado = qtdeDebitado;
	}

	@Column(name = "qtde_consumido", nullable = false, precision = 10, scale = 4)
	public BigDecimal getQtdeConsumido() {
		return qtdeConsumido;
	}

	public void setQtdeConsumido(BigDecimal qtdeConsumido) {
		this.qtdeConsumido = qtdeConsumido;
	}
	
	

	public enum Fields {

		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("rapServidores"),
		ATIVIDADE("atividade"),
		CALCULO_ATIVIDADE_CONSUMO("calculoAtividadeConsumo"),
		CALCULO_ATIVIDADE_CONSUMO_SEQ("calculoAtividadeConsumo.seq"),
		PROCEDIMENTO_HOSPITALAR_INTERNO("procedHospInterno"),
		PROCEDIMENTO_HOSPITALAR_INTERNO_SEQ("procedHospInterno.seq"),
		VALOR_CONSUMIDO("valorConsumido"),
		MATERIAL("scoMaterial"),
		MATERIAL_CODIGO("scoMaterial.codigo"),
		CENTRO_CUSTO("centroCustos"),
		QUANTIDADE_PREVISTO("qtdePrevisto"),
		QUANTIDADE_DEBITADO("qtdeDebitado"),
		QUANTIDADE_CONSUMIDO("qtdeConsumido"),
		CODIGO_PATRIMONIO("codPatrimonio"),
		GRUPO_OCUPACAO("sigGrupoOcupacoes"),
		RAP_OCUPACAO_CARGO("rapOcupacaoCargo"),
		VALOR_PREVISTO("valorPrevisto"),
		VALOR_DEBITADO("valorDebitado"),
		IDENTIFICADOR("identificador"),
		IND_EXTERNO("indExterno");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigCalculoDetalheConsumo)) {
			return false;
		}
		SigCalculoDetalheConsumo other = (SigCalculoDetalheConsumo) obj;
		return new EqualsBuilder().append(this.seq, other.seq).isEquals();
	}

	public void setIndExterno(Boolean indExterno) {
		this.indExterno = indExterno;
	}

	@Column(name = "IND_EXTERNO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExterno() {
		return indExterno;
	}

	public void setValorDebitado(BigDecimal valorDebitado) {
		this.valorDebitado = valorDebitado;
	}
	
	@Column(name = "VLR_DEBITADO", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorDebitado() {
		return valorDebitado;
	}
}
