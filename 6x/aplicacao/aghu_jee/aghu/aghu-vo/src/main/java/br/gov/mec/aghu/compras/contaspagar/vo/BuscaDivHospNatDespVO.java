package br.gov.mec.aghu.compras.contaspagar.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * 
 * @author alejandro
 *
 */
public class BuscaDivHospNatDespVO implements BaseBean {

	/**
		 * 
		 */
	private static final long serialVersionUID = 607025872814687081L;

	private String ano;
	private Integer seq;
	private Integer afnNtdGndCodigo;
	private Byte afnNtdCodigo;
	private BigDecimal ntdCodigo;
	private Byte ntpCodigo;
	private Double valor;

	public BuscaDivHospNatDespVO() {

	}

	public BuscaDivHospNatDespVO(String ano, Integer seq,
			Integer afnNtdGndCodigo, Byte afnNtdCodigo, BigDecimal ntdCodigo,
			Byte ntpCodigo, Double valor) {
		super();
		this.ano = ano;
		this.seq = seq;
		this.afnNtdGndCodigo = afnNtdGndCodigo;
		this.afnNtdCodigo = afnNtdCodigo;
		this.ntdCodigo = ntdCodigo;
		this.ntpCodigo = ntpCodigo;
		this.valor = valor;
	}
	
	public enum Fields {
		ANO("ano"),
		SEQ("seq"),
		AFN_NTD_GND("afnNtdGndCodigo"),
		AFN_NTD("afnNtdCodigo"),
		NTD("ntdCodigo"),
		NTP("ntpCodigo"),
		VALOR("valor");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public Integer getAfnNtdGndCodigo() {
		return afnNtdGndCodigo;
	}

	public void setAfnNtdGndCodigo(Integer afnNtdGndCodigo) {
		this.afnNtdGndCodigo = afnNtdGndCodigo;
	}

	public Byte getAfnNtdCodigo() {
		return afnNtdCodigo;
	}

	public void setAfnNtdCodigo(Byte afnNtdCodigo) {
		this.afnNtdCodigo = afnNtdCodigo;
	}

	public BigDecimal getNtdCodigo() {
		return ntdCodigo;
	}

	public void setNtdCodigo(BigDecimal ntdCodigo) {
		this.ntdCodigo = ntdCodigo;
	}

	public Byte getNtpCodigo() {
		return ntpCodigo;
	}

	public void setNtpCodigo(Byte ntpCodigo) {
		this.ntpCodigo = ntpCodigo;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

}
