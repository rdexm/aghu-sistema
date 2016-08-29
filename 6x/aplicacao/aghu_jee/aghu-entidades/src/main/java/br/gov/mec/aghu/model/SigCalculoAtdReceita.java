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
@SequenceGenerator(name = "sigCcrSq1", sequenceName = "SIG_CCR_SQ1", allocationSize = 1)
@Table(name = "SIG_CALCULO_ATD_RECEITAS", schema = "agh")
public class SigCalculoAtdReceita extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4423351486063029177L;

	private Integer seq;
	private Date criadoEm;
	private SigCalculoAtdPermanencia calculoAtdPermanencia;
	private Integer phiSeq;
	private SigObjetoCustoVersoes objetoCustoVersoes;
	private BigDecimal qtde;
	private RapServidores rapServidores;
	private Integer version;
	private BigDecimal vlrReceita;
	private MbcCirurgias cirurgia;
	private FccCentroCustos centroCusto;
	private SigCategoriaConsumos categoriaConsumo;
	private AfaItemPreparoMdto itemPreparoMdto;
	private FatProcedHospInternos procedHospInterno;
	private Integer cctCodigo;
	private Integer ctcSeq;
	private Integer cppSeq;
    private Integer ocvSeq;

    public SigCalculoAtdReceita() {
	}
	
	public SigCalculoAtdReceita(Integer seq) {
		this.setSeq(seq);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCcrSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CPP_SEQ", nullable = true, insertable = false, updatable = false)
	public SigCalculoAtdPermanencia getCalculoAtdPermanencia() {
		return calculoAtdPermanencia;
	}

	public void setCalculoAtdPermanencia(
			SigCalculoAtdPermanencia calculoAtdPermanencia) {
		this.calculoAtdPermanencia = calculoAtdPermanencia;
	}
	
	@Column(name = "PHI_SEQ", nullable = true, precision = 6)
	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ", nullable = true, insertable = false, updatable = false)
	public FatProcedHospInternos getProcedHospInterno() {
		return this.procedHospInterno;
	}

	public void setProcedHospInterno(FatProcedHospInternos procedHospInterno) {
		this.procedHospInterno = procedHospInterno;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCV_SEQ", nullable = true, insertable = false, updatable = false)
	public SigObjetoCustoVersoes getObjetoCustoVersoes() {
		return objetoCustoVersoes;
	}

	public void setObjetoCustoVersoes(SigObjetoCustoVersoes objetoCustoVersoes) {
		this.objetoCustoVersoes = objetoCustoVersoes;
	}

	@Column(name = "QTDE", nullable = false, precision = 10)
	public BigDecimal getQtde() {
		return qtde;
	}

	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
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

	@Column(name = "VLR_RECEITA", nullable = false, precision = 20)
	public BigDecimal getVlrReceita() {
		return vlrReceita;
	}

	public void setVlrReceita(BigDecimal vlrReceita) {
		this.vlrReceita = vlrReceita;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CRG_SEQ", nullable = true)
	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", insertable = false, updatable = false)
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CTC_SEQ", insertable = false, updatable = false)
	public SigCategoriaConsumos getCategoriaConsumo() {
		return categoriaConsumo;
	}

	public void setCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) {
		this.categoriaConsumo = categoriaConsumo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "ITO_PTO_SEQ", referencedColumnName = "PTO_SEQ"),
			@JoinColumn(name = "ITO_SEQP", referencedColumnName = "SEQP") })
	public AfaItemPreparoMdto getItemPreparoMdto() {
		return itemPreparoMdto;
	}

	public void setItemPreparoMdto(AfaItemPreparoMdto itemPreparoMdto) {
		this.itemPreparoMdto = itemPreparoMdto;
	}
	
	@Column(name = "CCT_CODIGO", nullable=true)
	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	@Column(name = "CTC_SEQ", nullable=false)
	public Integer getCtcSeq() {
		return ctcSeq;
	}

	public void setCtcSeq(Integer ctcSeq) {
		this.ctcSeq = ctcSeq;
	}

	@Column(name = "CPP_SEQ", nullable = true)
	public Integer getCppSeq() {
		return cppSeq;
	}

	public void setCppSeq(Integer cppSeq) {
		this.cppSeq = cppSeq;
	}

    @Column(name = "OCV_SEQ")
    public Integer getOcvSeq() {
        return ocvSeq;
    }

    public void setOcvSeq(Integer ocvSeq) {
        this.ocvSeq = ocvSeq;
    }
	
	public enum Fields {

		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("rapServidores"),
		CALCULOS_ATD_PERMANENCIAS("calculoAtdPermanencia"),
		SIG_CATEGORIA_CONSUMO("categoriaConsumo"),
		CENTRO_CUSTO("centroCusto"),
		PROCEDIMENTO_HOSPITALAR_INTERNO("procedHospInterno"),
		OBJETO_CUSTO_VERSAO("objetoCustoVersoes"),
		CCT_CODIGO("cctCodigo"),
		VALOR_RECEITA("vlrReceita");

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
		if (!(obj instanceof SigCalculoAtdReceita)) {
			return false;
		}
		SigCalculoAtdReceita other = (SigCalculoAtdReceita) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}

}
