package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class MapaDisponibilidadeVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6178259945201738716L;

	private Date dataInicio;
	private Integer percentual;
	private Integer numeroPacientes;
	private Integer minutosDisponiveis;
	private Integer tempoSerie;
	private Long extras;

	public MapaDisponibilidadeVO() {
	}

	public MapaDisponibilidadeVO(Date dataInicio,
			Integer percentual, Integer numeroPacientes,
			Integer minutosDisponiveis) {
		this.dataInicio = dataInicio;
		this.percentual = percentual;
		this.numeroPacientes = numeroPacientes;
		this.minutosDisponiveis = minutosDisponiveis;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Integer getPercentual() {
		return percentual;
	}

	public void setPercentual(Integer percentual) {
		this.percentual = percentual;
	}

	public Integer getNumeroPacientes() {
		return numeroPacientes;
	}

	public void setNumeroPacientes(Integer numeroPacientes) {
		this.numeroPacientes = numeroPacientes;
	}

	public Integer getMinutosDisponiveis() {
		return minutosDisponiveis;
	}

	public void setMinutosDisponiveis(Integer minutosDisponiveis) {
		this.minutosDisponiveis = minutosDisponiveis;
	}
	
	public Integer getTempoSerie() {
		return tempoSerie;
	}

	public void setTempoSerie(Integer tempoSerie) {
		this.tempoSerie = tempoSerie;
	}

	public Long getExtras() {
		return extras;
	}

	public void setExtras(Long extras) {
		this.extras = extras;
	}

	public enum Fields {

		DATA_INICIO("dataInicio"),
		PERCENTUAL("percentual"),
		NUMERO_PACIENTES("numeroPacientes"),
		MINUTOS_DISPONIVEIS("minutosDisponiveis"),
		TEMPO_SERIE("tempoSerie"),
		EXTRAS("extras");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
