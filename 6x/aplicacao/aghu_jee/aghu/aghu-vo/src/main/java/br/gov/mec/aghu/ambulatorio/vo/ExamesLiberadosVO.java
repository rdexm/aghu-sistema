package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ExamesLiberadosVO implements Serializable {

	private static final long serialVersionUID = 5899900588166072188L;
	
	private Integer pacCodigo;
	private String origem;
	private Date dataExame;
	private String nomeExame;
	private String amostra;
	private String laboratorio;
	private String resultado;
	private String primResultado;
	private Boolean indDividePorMil;
	private Short qtdeCasasDecimais;
	private Long valor;
	private Date dataEvento;
	private Integer soeSeq;
	private Short seqp;
	private boolean checkbox;
	
	public enum Fields {
		PAC_CODIGO("pacCodigo"),
		ORIGEM("origem"),
		DATA_EXAME("dataExame"),
		NOME_EXAME("nomeExame"),
		AMOSTRA("amostra"),
		LABORATORIO("laboratorio"),
		RESULTADO("resultado"),
		PRIM_RESULTADO("primResultado"),
		IND_DIVIDE_POR_MIL("indDividePorMil"),
		QTDE_CASAS_DECIMAIS("qtdeCasasDecimais"),
		VALOR("valor"),
		DATA_EVENTO("dataEvento"),
		SOE_SEQ("soeSeq"),
		SEQP("seqp");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return fields;
		}		
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public Date getDataExame() {
		return dataExame;
	}
	public void setDataExame(Date dataExame) {
		this.dataExame = dataExame;
	}
	public String getNomeExame() {
		return nomeExame;
	}
	public void setNomeExame(String nomeExame) {
		this.nomeExame = nomeExame;
	}
	public String getAmostra() {
		return amostra;
	}
	public void setAmostra(String amostra) {
		this.amostra = amostra;
	}
	public String getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(String laboratorio) {
		this.laboratorio = laboratorio;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	public String getPrimResultado() {
		return primResultado;
	}
	public void setPrimResultado(String primResultado) {
		this.primResultado = primResultado;
	}	
	public Boolean getIndDividePorMil() {
		return indDividePorMil;
	}
	public void setIndDividePorMil(Boolean indDividePorMil) {
		this.indDividePorMil = indDividePorMil;
	}	
	public Short getQtdeCasasDecimais() {
		return qtdeCasasDecimais;
	}
	public void setQtdeCasasDecimais(Short qtdeCasasDecimais) {
		this.qtdeCasasDecimais = qtdeCasasDecimais;
	}
	public Long getValor() {
		return valor;
	}
	public void setValor(Long valor) {
		this.valor = valor;
	}
	public Date getDataEvento() {
		return dataEvento;
	}
	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public boolean isCheckbox() {
		return checkbox;
	}
	public void setCheckbox(boolean checkbox) {
		this.checkbox = checkbox;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ExamesLiberadosVO)) {
			return false;
		}
		ExamesLiberadosVO other = (ExamesLiberadosVO) obj;
		
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getDataExame(), other.getDataExame());
		equalsBuilder.append(this.getNomeExame(), other.getNomeExame());
		return equalsBuilder.isEquals();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(this.getDataExame());
		hashCodeBuilder.append(this.getNomeExame());
		return hashCodeBuilder.toHashCode();
	}
	
}
