package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;
import java.util.Date;

public class CursorBuscaContaVO {

	private Integer cthSeq;
	private Date dataInternacao;
	private Date dataSaida;
	private Byte especialidadeAih;
	private BigDecimal valorConta;
	private String indBcoCapac;
	private Integer ordem;

	public enum Fields { 

		CTH_SEQ("cthSeq"), 
		DATA_INTERNACAO("dataInternacao"), 
		DATA_SAIDA("dataSaida"), 
		ESPECIALIDADE_AIH("especialidadeAih"), 
		VALOR_CONTA("valorConta"), 
		IND_BCO_CAPAC("indBcoCapac"),
		ORDEM("ordem"), ;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public Byte getEspecialidadeAih() {
		return especialidadeAih;
	}

	public void setEspecialidadeAih(Byte especialidadeAih) {
		this.especialidadeAih = especialidadeAih;
	}

	public BigDecimal getValorConta() {
		return valorConta;
	}

	public void setValorConta(BigDecimal valorConta) {
		this.valorConta = valorConta;
	}

	public String getIndBcoCapac() {
		return indBcoCapac;
	}

	public void setIndBcoCapac(String indBcoCapac) {
		this.indBcoCapac = indBcoCapac;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}
}
