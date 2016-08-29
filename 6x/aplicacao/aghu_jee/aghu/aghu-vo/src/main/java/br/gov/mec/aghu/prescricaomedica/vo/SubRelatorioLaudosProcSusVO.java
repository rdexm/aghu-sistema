package br.gov.mec.aghu.prescricaomedica.vo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubRelatorioLaudosProcSusVO {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private Integer ordem;

	private Long procedimentoCodigo;

	private Date dataHoraInicioValidade;

	private String procedimentoDescricao;

	private String justificativa;

	public SubRelatorioLaudosProcSusVO() {
	}

	public SubRelatorioLaudosProcSusVO(Integer ordem,
			Long procedimentoCodigo, Date dataHoraInicioValidade,
			String procedimentoDescricao, String justificativa) {
		this.ordem = ordem;
		this.procedimentoCodigo = procedimentoCodigo;
		this.dataHoraInicioValidade = dataHoraInicioValidade;
		this.procedimentoDescricao = procedimentoDescricao;
		this.justificativa = justificativa;
	}

	public SubRelatorioLaudosProcSusVO(Integer ordem,
			Date dataHoraInicioValidade,
			String descricao,
			String justificativa) {
		this.ordem = ordem;
		this.procedimentoCodigo = 0l;
		this.dataHoraInicioValidade = dataHoraInicioValidade;
		this.procedimentoDescricao = (dataHoraInicioValidade != null ? DATE_FORMAT
				.format(dataHoraInicioValidade)
				: "")
				+ " " + descricao;
		this.justificativa = justificativa;
	}

	public SubRelatorioLaudosProcSusVO(Integer ordem,
			Long procedimentoCodigo, String procedimentoDescricao,
			String justificativa) {
		this.ordem = ordem;
		this.procedimentoCodigo = procedimentoCodigo;
		this.procedimentoDescricao = procedimentoDescricao;
		this.justificativa = justificativa;
	}

	public SubRelatorioLaudosProcSusVO(Integer ordem,
			Integer procedimentoCodigo, String procedimentoDescricao,
			String justificativa) {
		this.ordem = ordem;
		this.procedimentoCodigo = procedimentoCodigo != null ? procedimentoCodigo
				.longValue()
				: null;
		this.procedimentoDescricao = procedimentoDescricao;
		this.justificativa = justificativa;
	}

	public SubRelatorioLaudosProcSusVO(Integer ordem,
			String procedimentoDescricao, Long procedimentoCodigo,
			String justificativa) {
		super();
		this.ordem = ordem;
		this.procedimentoDescricao = procedimentoDescricao;
		this.procedimentoCodigo = procedimentoCodigo;
		this.justificativa = justificativa;
	}
	
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public String getProcedimentoDescricao() {
		return procedimentoDescricao;
	}

	public void setProcedimentoDescricao(String procedimentoDescricao) {
		this.procedimentoDescricao = procedimentoDescricao;
	}

	public Long getProcedimentoCodigo() {
		return procedimentoCodigo;
	}

	public void setProcedimentoCodigo(Long procedimentoCodigo) {
		this.procedimentoCodigo = procedimentoCodigo;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public Date getDataHoraInicioValidade() {
		return dataHoraInicioValidade;
	}

	public void setDataHoraInicioValidade(Date dataHoraInicioValidade) {
		this.dataHoraInicioValidade = dataHoraInicioValidade;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((dataHoraInicioValidade == null) ? 0
						: dataHoraInicioValidade.hashCode());
		result = prime * result
				+ ((justificativa == null) ? 0 : justificativa.hashCode());
		result = prime * result + ((ordem == null) ? 0 : ordem.hashCode());
		result = prime
				* result
				+ ((procedimentoCodigo == null) ? 0 : procedimentoCodigo
						.hashCode());
		result = prime
				* result
				+ ((procedimentoDescricao == null) ? 0 : procedimentoDescricao
						.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
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
		SubRelatorioLaudosProcSusVO other = (SubRelatorioLaudosProcSusVO) obj;
		if (dataHoraInicioValidade == null) {
			if (other.dataHoraInicioValidade != null) {
				return false;
			}
		} else if (!dataHoraInicioValidade.equals(other.dataHoraInicioValidade)) {
			return false;
		}
		if (justificativa == null) {
			if (other.justificativa != null) {
				return false;
			}
		} else if (!justificativa.equals(other.justificativa)) {
			return false;
		}
		if (ordem == null) {
			if (other.ordem != null) {
				return false;
			}
		} else if (!ordem.equals(other.ordem)) {
			return false;
		}
		if (procedimentoCodigo == null) {
			if (other.procedimentoCodigo != null) {
				return false;
			}
		} else if (!procedimentoCodigo.equals(other.procedimentoCodigo)) {
			return false;
		}
		if (procedimentoDescricao == null) {
			if (other.procedimentoDescricao != null) {
				return false;
			}
		} else if (!procedimentoDescricao.equals(other.procedimentoDescricao)) {
			return false;
		}
		return true;
	}

}
