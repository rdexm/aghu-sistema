package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

public class ProcedimentosXCompatibilidadesVO implements Serializable {

	private static final long serialVersionUID = -402823688023298764L;

	private Integer item;
	private Long codSUS;
	private String descricao;

	private String tipo;
	private String comparacao;

	private Integer seqSUSComp;
	private Long codSUSComp;
	private String itemCompativel;

	private Short quantidade;
	private String indAmbulatorio;
	private String indInternacao;
	private String ttrCodigo;
	private Long cpxSeq;
	
	private boolean processado;
	
	private StringBuilder compatibilidade;
	private StringBuilder compatibilidadeAux;
	private StringBuilder compatibilidadeTTR;
	
	private String tipoCompatibilidade;
	private String tipoComparacao;
	
	
	public ProcedimentosXCompatibilidadesVO() {}
	
	public ProcedimentosXCompatibilidadesVO(Long codSUS) {
		super();
		this.codSUS = codSUS;
	}





	public void appendCompatibilidade(final StringBuilder compatibilidade){
		this.compatibilidade.append(compatibilidade);	
	}
	
	public void appendCompatibilidadeAux(final StringBuilder compatibilidadeAux){
		this.compatibilidadeAux.append(compatibilidadeAux);	
	}
	
	public void appendCompatibilidadeTTR(final StringBuilder compatibilidadeTTR){
		this.compatibilidadeTTR.append(compatibilidadeTTR);	
	}
	
	public void inicializaVars(){
		compatibilidade = new StringBuilder();
		compatibilidadeAux = new StringBuilder();
		compatibilidadeTTR = new StringBuilder();
	}

	public enum Fields {
		
		ITEM("item"),
		COD_SUS("codSUS"),
		DESCRICAO("descricao"), 
		TIPO("tipo"),
		COMPARACAO("comparacao"),
		SEQ_SUS_COMP("seqSUSComp"),
		COD_SUS_COMP("codSUSComp"),
		ITEM_COMPATIVEL("itemCompativel"),
		QUANTIDADE("quantidade"),
		IND_AMBULATORIO("indAmbulatorio"),
		IND_INTERNACAO("indInternacao"),
		CPX_SEQ("cpxSeq"),
		TTR_CODIGO("ttrCodigo");
		

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public Long getCodSUS() {
		return codSUS;
	}

	public void setCodSUS(Long codSUS) {
		this.codSUS = codSUS;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getComparacao() {
		return comparacao;
	}

	public void setComparacao(String comparacao) {
		this.comparacao = comparacao;
	}

	public Integer getSeqSUSComp() {
		return seqSUSComp;
	}

	public void setSeqSUSComp(Integer seqSUSComp) {
		this.seqSUSComp = seqSUSComp;
	}

	public Long getCodSUSComp() {
		return codSUSComp;
	}

	public void setCodSUSComp(Long codSUSComp) {
		this.codSUSComp = codSUSComp;
	}

	public String getItemCompativel() {
		return itemCompativel;
	}

	public void setItemCompativel(String itemCompativel) {
		this.itemCompativel = itemCompativel;
	}

	public Short getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	public String getIndAmbulatorio() {
		return indAmbulatorio;
	}

	public void setIndAmbulatorio(String indAmbulatorio) {
		this.indAmbulatorio = indAmbulatorio;
	}

	public String getIndInternacao() {
		return indInternacao;
	}

	public void setIndInternacao(String indInternacao) {
		this.indInternacao = indInternacao;
	}

	public String getTtrCodigo() {
		return ttrCodigo;
	}

	public void setTtrCodigo(String ttrCodigo) {
		this.ttrCodigo = ttrCodigo;
	}

	public Long getCpxSeq() {
		return cpxSeq;
	}

	public void setCpxSeq(Long cpxSeq) {
		this.cpxSeq = cpxSeq;
	}

	public StringBuilder getCompatibilidade() {
		return compatibilidade;
	}

	public void setCompatibilidade(StringBuilder compatibilidade) {
		this.compatibilidade = compatibilidade;
	}

	public StringBuilder getCompatibilidadeAux() {
		return compatibilidadeAux;
	}

	public void setCompatibilidadeAux(StringBuilder compatibilidadeAux) {
		this.compatibilidadeAux = compatibilidadeAux;
	}

	public StringBuilder getCompatibilidadeTTR() {
		return compatibilidadeTTR;
	}

	public void setCompatibilidadeTTR(StringBuilder compatibilidadeTTR) {
		this.compatibilidadeTTR = compatibilidadeTTR;
	}

	public boolean isProcessado() {
		return processado;
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	public String getTipoCompatibilidade() {
		return tipoCompatibilidade;
	}

	public void setTipoCompatibilidade(String tipoCompatibilidade) {
		this.tipoCompatibilidade = tipoCompatibilidade;
	}

	public String getTipoComparacao() {
		return tipoComparacao;
	}

	public void setTipoComparacao(String tipoComparacao) {
		this.tipoComparacao = tipoComparacao;
	}

	
	
	
	
	
	// Não alterar equals e hascode pois são utilizados para procurar instancias em java.util.list
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codSUS == null) ? 0 : codSUS.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProcedimentosXCompatibilidadesVO)) {
			return false;
		}
		ProcedimentosXCompatibilidadesVO other = (ProcedimentosXCompatibilidadesVO) obj;
		if (codSUS == null) {
			if (other.codSUS != null) {
				return false;
			}
		} else if (!codSUS.equals(other.codSUS)) {
			return false;
		}
		return true;
	}
}