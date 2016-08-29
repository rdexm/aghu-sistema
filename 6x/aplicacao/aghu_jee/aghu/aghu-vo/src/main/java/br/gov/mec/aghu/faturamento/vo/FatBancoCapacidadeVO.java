package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 */
public class FatBancoCapacidadeVO implements BaseBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8125917584610086990L;
	
	
	private Integer mes;
	private Integer ano;
	private Integer clinica;
	private String clinicaDescricao;
	private Integer numeroLeitos;
	private Integer capacidade;
	private Integer utilizacao;
	private Long saldo;
	
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
	public Integer getClinica() {
		return clinica;
	}
	public void setClinica(Integer clinica) {
		this.clinica = clinica;
	}
	public String getClinicaDescricao() {
		return clinicaDescricao;
	}
	public void setClinicaDescricao(String clinicaDescricao) {
		this.clinicaDescricao = clinicaDescricao;
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
	
	public enum Fields {
		MES("mes"),
		ANO("ano"),
		CLINICA("clinica"),
		CLINICA_DESCRICAO("clinicaDescricao"),
		NRO_LEITOS("numeroLeitos"),
		CAPACIDADE("capacidade"),
		UTILIZACAO("utilizacao"),
		SALDO("saldo");
		
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