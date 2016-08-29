package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;

/**
 * @author eschweigert
 */
public class CursorAtosMedicosVO {

	public static final Short IP_PHO_SEQ_6 = Short.valueOf((short)6);
	public static final String IND_SITUACAO = DominioSituacao.A.toString();

	private static final String MEDIA = "MEDIA";
	
	// PK
	private Integer eaiSeq;
	private Integer eaiCthSeq;
	private Byte seqp;
	// PK

	private Byte tivSeq;
	private Byte taoSeq;

	private String amPphDescricao;
	private String registro;
	private String financ;
	private String complexidade;
	private Double valorPrinc;
	private String sequencia;
	private Integer atoCir;
	private Double valorTotal;
	
	private String realizadoConta;

	private Short cspCnvCodigo;
	
	private Byte cspSeq;
	
	private Short seqArqSus;
	private String loteOpm;
	private Long nivel;
	private Long nivel1;
	private Long nivel2;
	private Long nivel3;
	private Long nivel4;
	
	private Integer iphSeq;
	private Short iphPhoSeq;

	private Integer seqCompatibilidade;	

	public enum Registro {
		PRINCIPAL1(  "03","1PRINCIPAL"),
		ESPECIAL2(   "04","2ESPECIAL"),
		SECUNDARIO3( "05","3SECUNDARIO"),
		SECUNDARIO4( "00","4SECUNDARIO");

		private String indice;
		private String descricao;

		private Registro(final String indice, final String descricao) {
			this.indice = indice;
			this.descricao = descricao;
		}

		public String getIndice(){
			return indice;
		}
		
		public String getDescricao() {
			return this.descricao;
		}		
	}

	public enum Financ {
		FAEC1(  "2","1FAEC"),
		ALTA2(  "4","2ALTA"),
		MEDIA3( "3","3MEDIA"),
		XXXXX4( "0","4XXXXX");

		private String indice;
		private String descricao;

		private Financ(final String indice, final String descricao) {
			this.indice = indice;
			this.descricao = descricao;
		}

		public String getIndice(){
			return indice;
		}
		
		public String getDescricao() {
			return this.descricao;
		}		
	}

	public enum Complexidade {
		ALTA(   "4","ALTA"),
		MEDIA5( "5",MEDIA),
		MEDIA6( "6",MEDIA),
		MEDIA7( "7",MEDIA),
		MEDIA8( "8",MEDIA),
		XXXXXX( "0","XXXXXX");

		private String indice;
		private String descricao;
		

		private Complexidade(final String indice, final String descricao) {
			this.indice = indice;
			this.descricao = descricao;
		}

		public String getIndice(){
			return indice;
		}
		
		public String getDescricao() {
			return this.descricao;
		}		
	}

	public enum Fields {

		EAI_SEQ("eaiSeq"),
		EAI_CTH_SEQ("eaiCthSeq"),
		SEQ_P("seqp"),

		TIV_SEQ("tivSeq"),
		TAO_SEQ("taoSeq"),

		AM_PPH_DESCRICAO("amPphDescricao"),
		REGISTRO("registro"),
		FINANC("financ"),
		COMPLEXIDADE("complexidade"),
		VALOR_PRINC("valorPrinc"),
		SEQUENCIA("sequencia"),
		ATO_CIR("atoCir"),
		VALOR_TOTAL("valorTotal"),
		CSP_CNV_CODIGO("cspCnvCodigo"),
		CSP_SEQ("cspSeq"),
		SEQ_ARQ_SUS("seqArqSus"),
		SEQ_COMPATIBILIDADE("seqCompatibilidade"),
		LOTE_OPM("loteOpm"),
		NIVEL("nivel"),
		NIVEL1("nivel1"),
		NIVEL2("nivel2"),
		NIVEL3("nivel3"),
		NIVEL4("nivel4"),
		IPH_SEQ("iphSeq"),
		IPH_PHO_SEQ("iphPhoSeq");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	

	@Override
	public String toString() {
		return "CursorAtosMedicosVO [eaiSeq=" + eaiSeq + ", eaiCthSeq="
				+ eaiCthSeq + ", seqp=" + seqp + ", tivSeq=" + tivSeq
				+ ", taoSeq=" + taoSeq + ", amPphDescricao=" + amPphDescricao
				+ ", registro=" + registro + ", financ=" + financ
				+ ", complexidade=" + complexidade + ", valorPrinc="
				+ valorPrinc + ", sequencia=" + sequencia + ", atoCir="
				+ atoCir + ", valorTotal=" + valorTotal + "]";
	}
	


	public Integer getEaiSeq() {
		return eaiSeq;
	}

	public void setEaiSeq(Integer eaiSeq) {
		this.eaiSeq = eaiSeq;
	}

	public Integer getEaiCthSeq() {
		return eaiCthSeq;
	}

	public void setEaiCthSeq(Integer eaiCthSeq) {
		this.eaiCthSeq = eaiCthSeq;
	}

	public Byte getSeqp() {
		return seqp;
	}

	public void setSeqp(Byte seqp) {
		this.seqp = seqp;
	}

	public Byte getTivSeq() {
		return tivSeq;
	}

	public void setTivSeq(Byte tivSeq) {
		this.tivSeq = tivSeq;
	}

	public Byte getTaoSeq() {
		return taoSeq;
	}

	public void setTaoSeq(Byte taoSeq) {
		this.taoSeq = taoSeq;
	}

	public String getAmPphDescricao() {
		return amPphDescricao;
	}

	public void setAmPphDescricao(String amPphDescricao) {
		this.amPphDescricao = amPphDescricao;
	}

	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	public String getFinanc() {
		return financ;
	}

	public void setFinanc(String financ) {
		this.financ = financ;
	}

	public String getComplexidade() {
		return complexidade;
	}

	public void setComplexidade(String complexidade) {
		this.complexidade = complexidade;
	}

	public Double getValorPrinc() {
		return valorPrinc;
	}

	public void setValorPrinc(Double valorPrinc) {
		this.valorPrinc = valorPrinc;
	}

	public String getSequencia() {
		return sequencia;
	}

	public void setSequencia(String sequencia) {
		this.sequencia = sequencia;
	}

	public Integer getAtoCir() {
		return atoCir;
	}

	public void setAtoCir(Integer atoCir) {
		this.atoCir = atoCir;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}



	public String getRealizadoConta() {
		return realizadoConta;
	}



	public void setRealizadoConta(String realizadoConta) {
		this.realizadoConta = realizadoConta;
	}



	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}



	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}



	public Byte getCspSeq() {
		return cspSeq;
	}



	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}



	public Short getSeqArqSus() {
		return seqArqSus;
	}



	public void setSeqArqSus(Short seqArqSus) {
		this.seqArqSus = seqArqSus;
	}



	public String getLoteOpm() {
		return loteOpm;
	}



	public void setLoteOpm(String loteOpm) {
		this.loteOpm = loteOpm;
	}



	public Long getNivel() {
		return nivel;
	}



	public void setNivel(Long nivel) {
		this.nivel = nivel;
	}



	public Long getNivel1() {
		return nivel1;
	}



	public void setNivel1(Long nivel1) {
		this.nivel1 = nivel1;
	}



	public Long getNivel2() {
		return nivel2;
	}



	public void setNivel2(Long nivel2) {
		this.nivel2 = nivel2;
	}



	public Long getNivel3() {
		return nivel3;
	}



	public void setNivel3(Long nivel3) {
		this.nivel3 = nivel3;
	}



	public Long getNivel4() {
		return nivel4;
	}



	public void setNivel4(Long nivel4) {
		this.nivel4 = nivel4;
	}



	public Integer getIphSeq() {
		return iphSeq;
	}



	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}



	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}



	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}



	public Integer getSeqCompatibilidade() {
		return seqCompatibilidade;
	}



	public void setSeqCompatibilidade(Integer seqCompatibilidade) {
		this.seqCompatibilidade = seqCompatibilidade;
	}

}