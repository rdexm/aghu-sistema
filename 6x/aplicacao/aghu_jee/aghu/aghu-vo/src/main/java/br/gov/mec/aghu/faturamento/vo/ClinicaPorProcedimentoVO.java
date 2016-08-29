package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ClinicaPorProcedimentoVO implements Serializable {

	private static final long serialVersionUID = 6699485709121105904L;

	private Byte especialidadeAih;
	private String clinicaDescricao;
	private String procedimentoDescricao;
	private Long qtdProcedimentosItem;
	private BigDecimal valorSadtRealiz;
	private BigDecimal valorShRealiz;
	private BigDecimal valorSpRealiz;
	private Long quantidadeProcedimento;
	private Long iphCodSusRealiz;
	private Short ordem;

	private BigDecimal sadtProc;
	private BigDecimal servHospProc;
	private BigDecimal servProfProc;

	private BigDecimal sadAih;
	private BigDecimal hospAih;
	private BigDecimal profAih;

	public Byte getEspecialidadeAih() {
		return especialidadeAih;
	}

	public void setEspecialidadeAih(Byte especialidadeAih) {
		this.especialidadeAih = especialidadeAih;
	}

	public String getClinicaDescricao() {
		return clinicaDescricao;
	}

	public void setClinicaDescricao(String clinicaDescricao) {
		this.clinicaDescricao = clinicaDescricao;
	}

	public String getProcedimentoDescricao() {
		return procedimentoDescricao;
	}

	public void setProcedimentoDescricao(String procedimentoDescricao) {
		this.procedimentoDescricao = procedimentoDescricao;
	}

	public Long getQtdProcedimentosItem() {
		return qtdProcedimentosItem;
	}

	public void setQtdProcedimentosItem(Long qtdProcedimentosItem) {
		this.qtdProcedimentosItem = qtdProcedimentosItem;
	}

	public BigDecimal getValorSadtRealiz() {
		return valorSadtRealiz;
	}

	public void setValorSadtRealiz(BigDecimal valorSadtRealiz) {
		this.valorSadtRealiz = valorSadtRealiz;
	}

	public BigDecimal getValorShRealiz() {
		return valorShRealiz;
	}

	public void setValorShRealiz(BigDecimal valorShRealiz) {
		this.valorShRealiz = valorShRealiz;
	}

	public BigDecimal getValorSpRealiz() {
		return valorSpRealiz;
	}

	public void setValorSpRealiz(BigDecimal valorSpRealiz) {
		this.valorSpRealiz = valorSpRealiz;
	}

	public Long getQuantidadeProcedimento() {
		return quantidadeProcedimento;
	}

	public void setQuantidadeProcedimento(Long quantidadeProcedimento) {
		this.quantidadeProcedimento = quantidadeProcedimento;
	}

	public Long getIphCodSusRealiz() {
		return iphCodSusRealiz;
	}

	public void setIphCodSusRealiz(Long iphCodSusRealiz) {
		this.iphCodSusRealiz = iphCodSusRealiz;
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	public BigDecimal getSadtProc() {
		return sadtProc;
	}

	public void setSadtProc(BigDecimal sadtProc) {
		this.sadtProc = sadtProc;
	}

	public BigDecimal getServHospProc() {
		return servHospProc;
	}

	public void setServHospProc(BigDecimal servHospProc) {
		this.servHospProc = servHospProc;
	}

	public BigDecimal getServProfProc() {
		return servProfProc;
	}

	public void setServProfProc(BigDecimal servProfProc) {
		this.servProfProc = servProfProc;
	}

	public BigDecimal getSadAih() {
		return sadAih;
	}

	public void setSadAih(BigDecimal sadAih) {
		this.sadAih = sadAih;
	}

	public BigDecimal getHospAih() {
		return hospAih;
	}

	public void setHospAih(BigDecimal hospAih) {
		this.hospAih = hospAih;
	}

	public BigDecimal getProfAih() {
		return profAih;
	}

	public void setProfAih(BigDecimal profAih) {
		this.profAih = profAih;
	}

	public enum Fields {
		
		 ESPECIALIDADE_AIH("especialidadeAih")
		,CLINICA_DESCRICAO("clinicaDescricao")
		,PROCEDIMENTO_DESCRICAO("procedimentoDescricao")
		,QUANTIDADE("qtdProcedimentosItem")
		,VALOR_ESP_SADT("valorSadtRealiz")
		,VALOR_ESP_SERV_HOSP("valorShRealiz")
		,VALOR_ESP_SERV_PROF("valorSpRealiz")

		,QUANT_PROC("quantidadeProcedimento")
		,SADT_PROC("sadtProc")
		,SERV_HOSP_PROC("servHospProc")
		,SERV_PROF_PROC("servProfProc")
		
		,QUANT_AIH("iphCodSusRealiz")
		,SADT_AIH("sadAih")
		,HOSP_AIH("hospAih")
		,PROF_AIH("profAih")
		,ORDEM("ordem")
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
