package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 */
public class FatSaldoUTIVO implements BaseBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 286643932646944075L;

	private Integer mes;
	private Integer ano;
	private DominioTipoIdadeUTI tipoUTI;
	private Integer numeroLeitos;
	private Integer capacidade;
	private Integer utilizacao;
	private Long saldo;
	private Double ocupacao;
	
	public Integer getMes() {
		return mes;
	}
	public void setMes(Integer mes) {
		this.mes = mes;
	}
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public Integer getNumeroLeitos() {
		return numeroLeitos;
	}
	public void setNumeroLeitos(Integer numeroLeitos) {
		this.numeroLeitos = numeroLeitos;
	}
	public Integer getCapacidade() {
		return capacidade;
	}
	public void setCapacidade(Integer capacidade) {
		this.capacidade = capacidade;
	}
	public Integer getUtilizacao() {
		return utilizacao;
	}
	public void setUtilizacao(Integer utilizacao) {
		this.utilizacao = utilizacao;
	}
	public Long getSaldo() {
		this.saldo = null;
		if (capacidade != null && utilizacao != null) {
			saldo = Long.valueOf(capacidade - utilizacao);
		}
		return saldo;
	}
	public void setSaldo(Long saldo) {
		this.saldo = saldo;
	}
	public DominioTipoIdadeUTI getTipoUTI() {
		return tipoUTI;
	}
	public void setTipoUTI(DominioTipoIdadeUTI tipoUTI) {
		this.tipoUTI = tipoUTI;
	}
	public Double getOcupacao() {
		this.ocupacao = 100-((Double.valueOf(getSaldo().toString())/Double.valueOf(getCapacidade().toString()))*100);
		return this.ocupacao;
	}
	public void setOcupacao(Double ocupacao) {
		this.ocupacao = ocupacao;
	}


	public enum Fields {
		MES("mes"),
		ANO("ano"),
		TIPO_UTI("tipoUTI"),
		NRO_LEITOS("numeroLeitos"),
		CAPACIDADE("capacidade"),
		UTILIZACAO("utilizacao"),
		SALDO("saldo"),
		OCUPACAO("ocupacao");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}