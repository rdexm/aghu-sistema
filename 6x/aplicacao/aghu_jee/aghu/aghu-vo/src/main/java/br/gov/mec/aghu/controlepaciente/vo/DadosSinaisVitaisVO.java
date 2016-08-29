package br.gov.mec.aghu.controlepaciente.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Representa os dados de sinais vitais
 * 
 * @author luismoura
 * 
 */
public class DadosSinaisVitaisVO implements BaseBean {
	private static final long serialVersionUID = 3451471253386230682L;

	private Short seq;
	private String descricao;
	private String sigla;
	private Date dataHora;
	private BigDecimal medicao;
	private String medicaoFormatada;
	private Integer ummSeq;
	private String unidaMedida;

	public DadosSinaisVitaisVO() {

	}

	public DadosSinaisVitaisVO(Short seq, String descricao, String sigla) {
		this.seq = seq;
		this.descricao = descricao;
		this.sigla = sigla;
	}

	public DadosSinaisVitaisVO(Short seq, String descricao, String sigla, Integer ummSeq) {
		this.seq = seq;
		this.descricao = descricao;
		this.sigla = sigla;
		this.ummSeq = ummSeq;
	}

	public DadosSinaisVitaisVO(String descricao, String sigla, Date dataHora,
			BigDecimal medicao, String medicaoFormatada) {
		super();
		this.descricao = descricao;
		this.sigla = sigla;
		this.dataHora = dataHora;
		this.medicao = medicao;
		this.medicaoFormatada = medicaoFormatada;
	}
	
	public DadosSinaisVitaisVO(String descricao, String sigla, Date dataHora,
			BigDecimal medicao, String medicaoFormatada, String unidadeMedida) {
		super();
		this.descricao = descricao;
		this.sigla = sigla;
		this.dataHora = dataHora;
		this.medicao = medicao;
		this.medicaoFormatada = medicaoFormatada;
		this.unidaMedida = unidadeMedida;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getUmmSeq() {
		return ummSeq;
	}

	public void setUmmSeq(Integer ummSeq) {
		this.ummSeq = ummSeq;
	}
	
	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public BigDecimal getMedicao() {
		return medicao;
	}

	public void setMedicao(BigDecimal medicao) {
		this.medicao = medicao;
	}

	public String getMedicaoFormatada() {
		return medicaoFormatada;
	}

	public void setMedicaoFormatada(String medicaoFormatada) {
		this.medicaoFormatada = medicaoFormatada;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		DadosSinaisVitaisVO other = (DadosSinaisVitaisVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	public String getUnidaMedida() {
		return unidaMedida;
	}

	public void setUnidaMedida(String unidaMedida) {
		this.unidaMedida = unidaMedida;
	}

	public enum Fields {
		SEQ_ITEM("seq"), //
		DESCRICAO("descricao"), //
		SIGLA("sigla"), //
		UMM_SEQ("ummSeq"), //
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
