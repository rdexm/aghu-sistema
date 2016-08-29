package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioLocalSoma;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioTipoItemEspelhoContaHospitalar;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity()
@SequenceGenerator(name = "fatEicpSq1", sequenceName = "AGH.FAT_EICP_SQ1", allocationSize = 1)
@Table(name = "FAT_ESPELHOS_ITENS_CONTA_PROD", schema = "AGH")
public class FatEspelhoItemContaProd extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 483690270590617365L;

	private Integer seq;

	private Short ichSeq;
	private Integer ichCthSeq;
	private Byte tivSeq;
	private Byte taoSeq;
	private Short quantidade;
	private Integer notaFiscal;
	private Integer pontosAnestesista;
	private Integer pontosCirurgiao;
	private Integer pontosSadt;
	private Long procedimentoHospitalarSus;
	private Boolean indConsistente;
	private DominioModoCobranca indModoCobranca;
	private BigDecimal valorAnestesista;
	private BigDecimal valorProcedimento;
	private BigDecimal valorSadt;
	private BigDecimal valorServHosp;
	private BigDecimal valorServProf;
	private Date dataPrevia;
	private DominioTipoItemEspelhoContaHospitalar indTipoItem;
	private Boolean indGeradoEncerramento;
	private DominioLocalSoma indLocalSoma;
	private Long cgc;
	private String competenciaUti;
	private Short indEquipe;
	private Long cpfCns;
	private String serieOpm;
	private String loteOpm;
	private String regAnvisaOpm;
	private Long cnpjRegAnvisa;
	private String cbo;
	private Integer version;

	private FatItensProcedHospitalar itemProcedimentoHospitalar;
	private FatEspelhoContaProd fatEspelhoContaProd;

	// construtores

	public FatEspelhoItemContaProd() {
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public FatEspelhoItemContaProd(Integer seq, Short ichSeq,
			Integer ichCthSeq, Byte tivSeq, Byte taoSeq, Short quantidade,
			Integer notaFiscal, Integer pontosAnestesista,
			Integer pontosCirurgiao, Integer pontosSadt,
			Long procedimentoHospitalarSus, Boolean indConsistente,
			DominioModoCobranca indModoCobranca, BigDecimal valorAnestesista,
			BigDecimal valorProcedimento, BigDecimal valorSadt,
			BigDecimal valorServHosp, BigDecimal valorServProf,
			Date dataPrevia, DominioTipoItemEspelhoContaHospitalar indTipoItem,
			Boolean indGeradoEncerramento, DominioLocalSoma indLocalSoma,
			Long cgc, String competenciaUti, Short indEquipe, Long cpfCns,
			String serieOpm, String loteOpm, String regAnvisaOpm,
			Long cnpjRegAnvisa, String cbo,
			FatItensProcedHospitalar itemProcedimentoHospitalar,
			FatEspelhoContaProd fatEspelhoContaProd) {

		this.seq = seq;
		this.ichSeq = ichSeq;
		this.ichCthSeq = ichCthSeq;
		this.tivSeq = tivSeq;
		this.taoSeq = taoSeq;
		this.quantidade = quantidade;
		this.notaFiscal = notaFiscal;
		this.pontosAnestesista = pontosAnestesista;
		this.pontosCirurgiao = pontosCirurgiao;
		this.pontosSadt = pontosSadt;
		this.procedimentoHospitalarSus = procedimentoHospitalarSus;
		this.indConsistente = indConsistente;
		this.indModoCobranca = indModoCobranca;
		this.valorAnestesista = valorAnestesista;
		this.valorProcedimento = valorProcedimento;
		this.valorSadt = valorSadt;
		this.valorServHosp = valorServHosp;
		this.valorServProf = valorServProf;
		this.dataPrevia = dataPrevia;
		this.indTipoItem = indTipoItem;
		this.indGeradoEncerramento = indGeradoEncerramento;
		this.indLocalSoma = indLocalSoma;
		this.cgc = cgc;
		this.competenciaUti = competenciaUti;
		this.indEquipe = indEquipe;
		this.cpfCns = cpfCns;
		this.serieOpm = serieOpm;
		this.loteOpm = loteOpm;
		this.regAnvisaOpm = regAnvisaOpm;
		this.cnpjRegAnvisa = cnpjRegAnvisa;
		this.cbo = cbo;
		this.itemProcedimentoHospitalar = itemProcedimentoHospitalar;
		this.fatEspelhoContaProd = fatEspelhoContaProd;
	}

	/**
	 * Cria objeto com os dados da FatEspelhoItemContaHosp.
	 * 
	 * @param espelhoItemContaHosp
	 */
	public FatEspelhoItemContaProd(FatEspelhoItemContaHosp espelhoItemContaHosp, FatEspelhoContaProd espelhoContaProd) {
		this.ichSeq = espelhoItemContaHosp.getIchSeq();
		this.ichCthSeq = espelhoItemContaHosp.getId().getIchCthSeq();
		this.tivSeq = espelhoItemContaHosp.getTivSeq();
		this.taoSeq = espelhoItemContaHosp.getTaoSeq();
		this.quantidade = espelhoItemContaHosp.getQuantidade();
		this.notaFiscal = espelhoItemContaHosp.getNotaFiscal();
		this.pontosAnestesista = espelhoItemContaHosp.getPontosAnestesista();
		this.pontosCirurgiao = espelhoItemContaHosp.getPontosCirurgiao();
		this.pontosSadt = espelhoItemContaHosp.getPontosSadt();
		this.procedimentoHospitalarSus = espelhoItemContaHosp
				.getProcedimentoHospitalarSus();
		this.indConsistente = espelhoItemContaHosp.getIndConsistente();
		this.indModoCobranca = espelhoItemContaHosp.getIndModoCobranca();
		this.valorAnestesista = espelhoItemContaHosp.getValorAnestesista();
		this.valorProcedimento = espelhoItemContaHosp.getValorProcedimento();
		this.valorSadt = espelhoItemContaHosp.getValorSadt();
		this.valorServHosp = espelhoItemContaHosp.getValorServHosp();
		this.valorServProf = espelhoItemContaHosp.getValorServProf();
		this.dataPrevia = espelhoItemContaHosp.getDataPrevia();
		this.indTipoItem = espelhoItemContaHosp.getIndTipoItem();
		this.indGeradoEncerramento = espelhoItemContaHosp
				.getIndGeradoEncerramento();
		this.indLocalSoma = espelhoItemContaHosp.getIndLocalSoma();
		this.cgc = espelhoItemContaHosp.getCgc();
		this.competenciaUti = espelhoItemContaHosp.getCompetenciaUti();
		this.indEquipe = espelhoItemContaHosp.getIndEquipe();
		this.cpfCns = espelhoItemContaHosp.getCpfCns();
		this.serieOpm = espelhoItemContaHosp.getSerieOpm();
		this.loteOpm = espelhoItemContaHosp.getLoteOpm();
		this.regAnvisaOpm = espelhoItemContaHosp.getRegAnvisaOpm();
		this.cnpjRegAnvisa = espelhoItemContaHosp.getCnpjRegAnvisa();
		this.cbo = espelhoItemContaHosp.getCbo();
		this.itemProcedimentoHospitalar = espelhoItemContaHosp
				.getItemProcedimentoHospitalar();
		this.fatEspelhoContaProd = espelhoContaProd;
	}

	// getters & setters
	@Id()
	@Column(name = "SEQ", length = 8, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fatEicpSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "ICH_SEQ", length = 4)
	public Short getIchSeq() {
		return this.ichSeq;
	}

	public void setIchSeq(Short ichSeq) {
		this.ichSeq = ichSeq;
	}

	@Column(name = "ICH_CTH_SEQ", length = 8, nullable = false)
	public Integer getIchCthSeq() {
		return this.ichCthSeq;
	}

	public void setIchCthSeq(Integer ichCthSeq) {
		this.ichCthSeq = ichCthSeq;
	}

	@Column(name = "TIV_SEQ", length = 2)
	public Byte getTivSeq() {
		return this.tivSeq;
	}

	public void setTivSeq(Byte tivSeq) {
		this.tivSeq = tivSeq;
	}

	@Column(name = "TAO_SEQ", length = 2)
	public Byte getTaoSeq() {
		return this.taoSeq;
	}

	public void setTaoSeq(Byte taoSeq) {
		this.taoSeq = taoSeq;
	}

	@Column(name = "QUANTIDADE", length = 4)
	public Short getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name = "NOTA_FISCAL", length = 6)
	public Integer getNotaFiscal() {
		return this.notaFiscal;
	}

	public void setNotaFiscal(Integer notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	@Column(name = "PONTOS_ANESTESISTA", length = 5)
	public Integer getPontosAnestesista() {
		return this.pontosAnestesista;
	}

	public void setPontosAnestesista(Integer pontosAnestesista) {
		this.pontosAnestesista = pontosAnestesista;
	}

	@Column(name = "PONTOS_CIRURGIAO", length = 5)
	public Integer getPontosCirurgiao() {
		return this.pontosCirurgiao;
	}

	public void setPontosCirurgiao(Integer pontosCirurgiao) {
		this.pontosCirurgiao = pontosCirurgiao;
	}

	@Column(name = "PONTOS_SADT", length = 5)
	public Integer getPontosSadt() {
		return this.pontosSadt;
	}

	public void setPontosSadt(Integer pontosSadt) {
		this.pontosSadt = pontosSadt;
	}

	@Column(name = "PROCEDIMENTO_HOSPITALAR_SUS", length = 10)
	public Long getProcedimentoHospitalarSus() {
		return this.procedimentoHospitalarSus;
	}

	public void setProcedimentoHospitalarSus(Long procedimentoHospitalarSus) {
		this.procedimentoHospitalarSus = procedimentoHospitalarSus;
	}

	@Column(name = "IND_CONSISTENTE", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsistente() {
		return this.indConsistente;
	}

	public void setIndConsistente(Boolean indConsistente) {
		this.indConsistente = indConsistente;
	}

	@Column(name = "IND_MODO_COBRANCA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioModoCobranca getIndModoCobranca() {
		return this.indModoCobranca;
	}

	public void setIndModoCobranca(DominioModoCobranca indModoCobranca) {
		this.indModoCobranca = indModoCobranca;
	}

	@Column(name = "VALOR_ANESTESISTA", length = 14)
	public BigDecimal getValorAnestesista() {
		return this.valorAnestesista;
	}

	public void setValorAnestesista(BigDecimal valorAnestesista) {
		this.valorAnestesista = valorAnestesista;
	}

	@Column(name = "VALOR_PROCEDIMENTO", length = 14)
	public BigDecimal getValorProcedimento() {
		return this.valorProcedimento;
	}

	public void setValorProcedimento(BigDecimal valorProcedimento) {
		this.valorProcedimento = valorProcedimento;
	}

	@Column(name = "VALOR_SADT", length = 14)
	public BigDecimal getValorSadt() {
		return this.valorSadt;
	}

	public void setValorSadt(BigDecimal valorSadt) {
		this.valorSadt = valorSadt;
	}

	@Column(name = "VALOR_SERV_HOSP", length = 14)
	public BigDecimal getValorServHosp() {
		return this.valorServHosp;
	}

	public void setValorServHosp(BigDecimal valorServHosp) {
		this.valorServHosp = valorServHosp;
	}

	@Column(name = "VALOR_SERV_PROF", length = 14)
	public BigDecimal getValorServProf() {
		return this.valorServProf;
	}

	public void setValorServProf(BigDecimal valorServProf) {
		this.valorServProf = valorServProf;
	}

	@Column(name = "DATA_PREVIA")
	@Temporal(TemporalType.DATE)
	public Date getDataPrevia() {
		return this.dataPrevia;
	}

	public void setDataPrevia(Date dataPrevia) {
		this.dataPrevia = dataPrevia;
	}

	@Column(name = "IND_TIPO_ITEM", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoItemEspelhoContaHospitalar getIndTipoItem() {
		return this.indTipoItem;
	}

	public void setIndTipoItem(DominioTipoItemEspelhoContaHospitalar indTipoItem) {
		this.indTipoItem = indTipoItem;
	}

	@Column(name = "IND_GERADO_ENCERRAMENTO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndGeradoEncerramento() {
		return this.indGeradoEncerramento;
	}

	public void setIndGeradoEncerramento(Boolean indGeradoEncerramento) {
		this.indGeradoEncerramento = indGeradoEncerramento;
	}

	@Column(name = "IND_LOCAL_SOMA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioLocalSoma getIndLocalSoma() {
		return this.indLocalSoma;
	}

	public void setIndLocalSoma(DominioLocalSoma indLocalSoma) {
		this.indLocalSoma = indLocalSoma;
	}

	@Column(name = "CGC", length = 14)
	public Long getCgc() {
		return this.cgc;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	@Column(name = "COMPETENCIA_UTI", length = 6)
	public String getCompetenciaUti() {
		return this.competenciaUti;
	}

	public void setCompetenciaUti(String competenciaUti) {
		this.competenciaUti = competenciaUti;
	}

	@Column(name = "IND_EQUIPE", length = 1)
	public Short getIndEquipe() {
		return this.indEquipe;
	}

	public void setIndEquipe(Short indEquipe) {
		this.indEquipe = indEquipe;
	}

	@Column(name = "CPF_CNS", length = 15)
	public Long getCpfCns() {
		return this.cpfCns;
	}

	public void setCpfCns(Long cpfCns) {
		this.cpfCns = cpfCns;
	}

	@Column(name = "SERIE_OPM", length = 20)
	public String getSerieOpm() {
		return this.serieOpm;
	}

	public void setSerieOpm(String serieOpm) {
		this.serieOpm = serieOpm;
	}

	@Column(name = "LOTE_OPM", length = 20)
	public String getLoteOpm() {
		return this.loteOpm;
	}

	public void setLoteOpm(String loteOpm) {
		this.loteOpm = loteOpm;
	}

	@Column(name = "REG_ANVISA_OPM", length = 20)
	public String getRegAnvisaOpm() {
		return this.regAnvisaOpm;
	}

	public void setRegAnvisaOpm(String regAnvisaOpm) {
		this.regAnvisaOpm = regAnvisaOpm;
	}

	@Column(name = "CNPJ_REG_ANVISA", length = 14)
	public Long getCnpjRegAnvisa() {
		return this.cnpjRegAnvisa;
	}

	public void setCnpjRegAnvisa(Long cnpjRegAnvisa) {
		this.cnpjRegAnvisa = cnpjRegAnvisa;
	}

	@Column(name = "CBO", length = 6)
	public String getCbo() {
		return this.cbo;
	}

	public void setCbo(String cbo) {
		this.cbo = cbo;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "IPH_PHO_SEQ", nullable = false),
			@JoinColumn(name = "IPH_SEQ", nullable = false) })
	public FatItensProcedHospitalar getItemProcedimentoHospitalar() {
		return itemProcedimentoHospitalar;
	}

	public void setItemProcedimentoHospitalar(
			FatItensProcedHospitalar itemProcedimentoHospitalar) {
		this.itemProcedimentoHospitalar = itemProcedimentoHospitalar;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EAIP_SEQ", nullable = false)
	public FatEspelhoContaProd getFatEspelhoContaProd() {
		return fatEspelhoContaProd;
	}

	public void setFatEspelhoContaProd(FatEspelhoContaProd fatEspelhoContaProd) {
		this.fatEspelhoContaProd = fatEspelhoContaProd;
	}

	// outros

	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	public boolean equals(Object other) {
		if (!(other instanceof FatEspelhoItemContaProd)) {
			return false;
		}
		FatEspelhoItemContaProd castOther = (FatEspelhoItemContaProd) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), ICH_SEQ("ichSeq"), ICH_CTH_SEQ("ichCthSeq"), TIV_SEQ(
				"tivSeq"), TAO_SEQ("taoSeq"), QUANTIDADE("quantidade"), NOTA_FISCAL(
				"notaFiscal"), PONTOS_ANESTESISTA("pontosAnestesista"), PONTOS_CIRURGIAO(
				"pontosCirurgiao"), PONTOS_SADT("pontosSadt"), PROCEDIMENTO_HOSPITALAR_SUS(
				"procedimentoHospitalarSus"), IND_CONSISTENTE("indConsistente"), IND_MODO_COBRANCA(
				"indModoCobranca"), VALOR_ANESTESISTA("valorAnestesista"), VALOR_PROCEDIMENTO(
				"valorProcedimento"), VALOR_SADT("valorSadt"), VALOR_SERV_HOSP(
				"valorServHosp"), VALOR_SERV_PROF("valorServProf"), DATA_PREVIA(
				"dataPrevia"), IND_TIPO_ITEM("indTipoItem"), IND_GERADO_ENCERRAMENTO(
				"indGeradoEncerramento"), IND_LOCAL_SOMA("indLocalSoma"), CGC(
				"cgc"), COMPETENCIA_UTI("competenciaUti"), IND_EQUIPE(
				"indEquipe"), CPF_CNS("cpfCns"), SERIE_OPM("serieOpm"), LOTE_OPM(
				"loteOpm"), REG_ANVISA_OPM("regAnvisaOpm"), CNPJ_REG_ANVISA(
				"cnpjRegAnvisa"), CBO("cbo"), VERSION("version"), ESPELHO_CONTA_PROD(
				"fatEspelhoContaProd");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		public String toString() {
			return this.field;
		}
	}

}