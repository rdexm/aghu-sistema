package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class CursoPopulaProcedimentoHospitalarInternoVO implements Serializable {

	private static final long serialVersionUID = -1229958110087872543L;

	private Long codTabela;
	private BigDecimal vlrServHospitalar;
	private BigDecimal vlrServProfissional;
	private BigDecimal vlrSadt;
	private BigDecimal vlrProcedimento;
	private BigDecimal vlrAnestesista;
	private Short quantDiasFaturamento;

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public BigDecimal getVlrServHospitalar() {
		return vlrServHospitalar;
	}

	public void setVlrServHospitalar(BigDecimal vlrServHospitalar) {
		this.vlrServHospitalar = vlrServHospitalar;
	}

	public BigDecimal getVlrServProfissional() {
		return vlrServProfissional;
	}

	public void setVlrServProfissional(BigDecimal vlrServProfissional) {
		this.vlrServProfissional = vlrServProfissional;
	}

	public BigDecimal getVlrSadt() {
		return vlrSadt;
	}

	public void setVlrSadt(BigDecimal vlrSadt) {
		this.vlrSadt = vlrSadt;
	}

	public BigDecimal getVlrProcedimento() {
		return vlrProcedimento;
	}

	public void setVlrProcedimento(BigDecimal vlrProcedimento) {
		this.vlrProcedimento = vlrProcedimento;
	}

	public BigDecimal getVlrAnestesista() {
		return vlrAnestesista;
	}

	public void setVlrAnestesista(BigDecimal vlrAnestesista) {
		this.vlrAnestesista = vlrAnestesista;
	}

	public Short getQuantDiasFaturamento() {
		return quantDiasFaturamento;
	}

	public void setQuantDiasFaturamento(Short quantDiasFaturamento) {
		this.quantDiasFaturamento = quantDiasFaturamento;
	}

	public enum Fields {
		COD_TABELA("codTabela"), 
		VLR_SERV_HOSPITALAR("vlrServHospitalar"), 
		VLR_SERV_PROFISSIONAL("vlrServProfissional"), 
		VLR_SADT("vlrSadt"),
		VLR_PROCEDIMENTO("vlrProcedimento"), 
		VLR_ANESTESISTA("vlrAnestesista"), 
		QUANT_DIAS_FATURAMENTO("quantDiasFaturamento");

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
		return "CursoPopulaProcedimentoHospitalarInternoVO [codTabela="
				+ codTabela + ", vlrServHospitalar=" + vlrServHospitalar
				+ ", vlrServProfissional=" + vlrServProfissional + ", vlrSadt="
				+ vlrSadt + ", vlrProcedimento=" + vlrProcedimento
				+ ", vlrAnestesista=" + vlrAnestesista
				+ ", quantDiasFaturamento=" + quantDiasFaturamento + "]";
	}

}
