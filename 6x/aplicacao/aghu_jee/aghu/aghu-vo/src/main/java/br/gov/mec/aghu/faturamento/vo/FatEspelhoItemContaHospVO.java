package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioLocalSoma;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioTipoItemEspelhoContaHospitalar;

/**
 * VO utilizado nos cursores cItemRealiz e cItemAgrup
 * 
 * @author lcmoura
 *
 */

public class FatEspelhoItemContaHospVO implements java.io.Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2500172489602735826L;
	// Utilizados nos cursores cItemRealiz e cItemAgrup
	private Long procedimentoHospitalarSus;
	private Short ichSeq;
	private Short iphPhoSeq;
	private Integer iphSeq;
	private DominioModoCobranca indModoCobranca;
	private DominioLocalSoma indLocalSoma;
	private Long quantidade;
	private BigDecimal valorServHosp;
	private BigDecimal valorServProf;
	private BigDecimal valorSadt;
	private BigDecimal valorProcedimento;
	private BigDecimal valorAnestesista;
	private Long pontosAnestesista;
	private Long pontosCirurgiao;
	private Long pontosSadt;
	
	// Utilizados no cursor cItemAgrup
	private Byte tivSeq;
	private Byte taoSeq;	
	private Integer notaFiscal;
	private Boolean iphInternacao;
	private DominioTipoItemEspelhoContaHospitalar indTipoItem;
	private Long cgc;
	private String competenciaUti;
	private Short indEquipe;
	private String cbo;
	private Long cpfCns;
	private String serieOpm;
	private String loteOpm;
	private String regAnvisaOpm;
	private Long cnpjRegAnvisa;
	
	
	
	
	public Long getProcedimentoHospitalarSus() {
		return procedimentoHospitalarSus;
	}
	public Short getIchSeq() {
		return ichSeq;
	}
	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}
	public Integer getIphSeq() {
		return iphSeq;
	}
	public DominioModoCobranca getIndModoCobranca() {
		return indModoCobranca;
	}
	public DominioLocalSoma getIndLocalSoma() {
		return indLocalSoma;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public BigDecimal getValorServHosp() {
		return valorServHosp;
	}
	public BigDecimal getValorServProf() {
		return valorServProf;
	}
	public BigDecimal getValorSadt() {
		return valorSadt;
	}
	public BigDecimal getValorProcedimento() {
		return valorProcedimento;
	}
	public BigDecimal getValorAnestesista() {
		return valorAnestesista;
	}
	public Long getPontosAnestesista() {
		return pontosAnestesista;
	}
	public Long getPontosCirurgiao() {
		return pontosCirurgiao;
	}
	public Long getPontosSadt() {
		return pontosSadt;
	}
	public Byte getTivSeq() {
		return tivSeq;
	}
	public Byte getTaoSeq() {
		return taoSeq;
	}
	public Integer getNotaFiscal() {
		return notaFiscal;
	}
	public Boolean getIphInternacao() {
		return iphInternacao;
	}
	public DominioTipoItemEspelhoContaHospitalar getIndTipoItem() {
		return indTipoItem;
	}
	public Long getCgc() {
		return cgc;
	}
	public String getCompetenciaUti() {
		return competenciaUti;
	}
	public Short getIndEquipe() {
		return indEquipe;
	}
	public String getCbo() {
		return cbo;
	}
	public Long getCpfCns() {
		return cpfCns;
	}
	public String getSerieOpm() {
		return serieOpm;
	}
	public String getLoteOpm() {
		return loteOpm;
	}
	public String getRegAnvisaOpm() {
		return regAnvisaOpm;
	}
	public Long getCnpjRegAnvisa() {
		return cnpjRegAnvisa;
	}
	public void setProcedimentoHospitalarSus(Long procedimentoHospitalarSus) {
		this.procedimentoHospitalarSus = procedimentoHospitalarSus;
	}
	public void setIchSeq(Short ichSeq) {
		this.ichSeq = ichSeq;
	}
	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}
	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}
	public void setIndModoCobranca(DominioModoCobranca indModoCobranca) {
		this.indModoCobranca = indModoCobranca;
	}
	public void setIndLocalSoma(DominioLocalSoma indLocalSoma) {
		this.indLocalSoma = indLocalSoma;
	}	
	
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	public void setValorServHosp(BigDecimal valorServHosp) {
		this.valorServHosp = valorServHosp;
	}
	public void setValorServProf(BigDecimal valorServProf) {
		this.valorServProf = valorServProf;
	}
	public void setValorSadt(BigDecimal valorSadt) {
		this.valorSadt = valorSadt;
	}
	public void setValorProcedimento(BigDecimal valorProcedimento) {
		this.valorProcedimento = valorProcedimento;
	}
	public void setValorAnestesista(BigDecimal valorAnestesista) {
		this.valorAnestesista = valorAnestesista;
	}
	public void setPontosAnestesista(Long pontosAnestesista) {
		this.pontosAnestesista = pontosAnestesista;
	}
	public void setPontosCirurgiao(Long pontosCirurgiao) {
		this.pontosCirurgiao = pontosCirurgiao;
	}
	public void setPontosSadt(Long pontosSadt) {
		this.pontosSadt = pontosSadt;
	}
	public void setTivSeq(Byte tivSeq) {
		this.tivSeq = tivSeq;
	}
	public void setTaoSeq(Byte taoSeq) {
		this.taoSeq = taoSeq;
	}
	public void setNotaFiscal(Integer notaFiscal) {
		this.notaFiscal = notaFiscal;
	}
	public void setIphInternacao(Boolean iphInternacao) {
		this.iphInternacao = iphInternacao;
	}
	public void setIndTipoItem(DominioTipoItemEspelhoContaHospitalar indTipoItem) {
		this.indTipoItem = indTipoItem;
	}
	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}
	public void setCompetenciaUti(String competenciaUti) {
		this.competenciaUti = competenciaUti;
	}
	public void setIndEquipe(Short indEquipe) {
		this.indEquipe = indEquipe;
	}
	public void setCbo(String cbo) {
		this.cbo = cbo;
	}
	public void setCpfCns(Long cpfCns) {
		this.cpfCns = cpfCns;
	}
	public void setSerieOpm(String serieOpm) {
		this.serieOpm = serieOpm;
	}
	public void setLoteOpm(String loteOpm) {
		this.loteOpm = loteOpm;
	}
	public void setRegAnvisaOpm(String regAnvisaOpm) {
		this.regAnvisaOpm = regAnvisaOpm;
	}
	public void setCnpjRegAnvisa(Long cnpjRegAnvisa) {
		this.cnpjRegAnvisa = cnpjRegAnvisa;
	}
	
	public enum Fields {

		PROCEDIMENTO_HOSPITALAR_SUS("procedimentoHospitalarSus"),
		ICH_SEQ("ichSeq"),
		IPH_PHO_SEQ("iphPhoSeq"),
		IPH_SEQ("iphSeq"),
		IND_MODO_COBRANCA("indModoCobranca"),
		IND_LOCAL_SOMA("indLocalSoma"),
		QUANTIDADE("quantidade"),
		VALOR_SERV_HOSP("valorServHosp"),
		VALOR_SERV_PROF("valorServProf"),
		VALOR_SADT("valorSadt"),
		VALOR_PROCEDIMENTO("valorProcedimento"),
		VALOR_ANESTESISTA("valorAnestesista"),
		PONTOS_ANESTESISTA("pontosAnestesista"),
		PONTOS_CIRURGIAO("pontosCirurgiao"),
		PONTOS_SADT("pontosSadt"),
		TIV_SEQ("tivSeq"),
		TAO_SEQ("taoSeq"),	
		NOTA_FISCAL("notaFiscal"),
		IPH_INTERNACAO("iphInternacao"),
		IND_TIPO_ITEM("indTipoItem"),
		CGC("cgc"),
		COMPETENCIA_UTI("competenciaUti"),
		IND_EQUIPE("indEquipe"),
		CBO("cbo"),
		CPF_CNS("cpfCns"),
		SERIE_OPM("serieOpm"),
		LOTE_OPM("loteOpm"),
		REG_ANVISA_OPM("regAnvisaOpm"),
		CNPJ_REG_ANVISA("cnpjRegAnvisa"),
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
}
