package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * #990 - VO para PC5 - @ORADB MPMP_VERIF_PARAM_CALCULO
 * @author paulo
 *
 */
public class MpmVerificaParamCalculoVO implements BaseBean {
	
	
	private static final long serialVersionUID = 2880437058654679848L;
	private BigDecimal peso;
	private BigDecimal altura;
	private BigDecimal sc;
	private Date criadoEm;
	private Boolean dadosAtualizados;
	

	public enum Fields {
		PESO("peso"),
		ALTURA("altura"),
		SC("sc"),
		CRIADO_EM("criadoEm");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	public BigDecimal getPeso() {
		return peso;
	}
	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}
	public BigDecimal getAltura() {
		return altura;
	}
	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}
	public BigDecimal getSc() {
		return sc;
	}
	public void setSc(BigDecimal sc) {
		this.sc = sc;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public Boolean getDadosAtualizados() {
		return dadosAtualizados;
	}
	public void setDadosAtualizados(Boolean dadosAtualizados) {
		this.dadosAtualizados = dadosAtualizados;
	}
	
}
