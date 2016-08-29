package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class HorariosAgendamentoSessaoVO implements BaseBean {

	private static final long serialVersionUID = -2196451217588765435L;

	private Short loaSeq;
	private DominioTipoAcomodacao acomodacao;
	private Short dia;
	private Date tempo;
	private Short ciclo;
	private Integer sesSeq;
	private Date dataInicio;
	private Date dataFim;
	private DominioSituacaoHorarioSessao indSituacao;
	private PercentualOcupacaoVO percentualOcupacao;
	private List<HorarioAgendamentoAmbulatorioVO> horariosAgendamentoAmbulatorio;
	private String logTentativas;
	
	public HorariosAgendamentoSessaoVO() {
	}
	
	public HorariosAgendamentoSessaoVO(String logTentativas) {
		this.logTentativas = logTentativas;
	}
	
	public Short getLoaSeq() {
		return loaSeq;
	}

	public void setLoaSeq(Short loaSeq) {
		this.loaSeq = loaSeq;
	}

	public DominioTipoAcomodacao getAcomodacao() {
		return acomodacao;
	}

	public void setAcomodacao(DominioTipoAcomodacao acomodacao) {
		this.acomodacao = acomodacao;
	}

	public Short getDia() {
		return dia;
	}

	public void setDia(Short dia) {
		this.dia = dia;
	}

	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public Integer getSesSeq() {
		return sesSeq;
	}

	public void setSesSeq(Integer sesSeq) {
		this.sesSeq = sesSeq;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public DominioSituacaoHorarioSessao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoHorarioSessao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public PercentualOcupacaoVO getPercentualOcupacao() {
		return percentualOcupacao;
	}

	public void setPercentualOcupacao(PercentualOcupacaoVO percentualOcupacao) {
		this.percentualOcupacao = percentualOcupacao;
	}

	public List<HorarioAgendamentoAmbulatorioVO> getHorariosAgendamentoAmbulatorio() {
		return horariosAgendamentoAmbulatorio;
	}

	public void setHorariosAgendamentoAmbulatorio(
			List<HorarioAgendamentoAmbulatorioVO> horariosAgendamentoAmbulatorio) {
		this.horariosAgendamentoAmbulatorio = horariosAgendamentoAmbulatorio;
	}

	public String getLogTentativas() {
		return logTentativas;
	}

	public void setLogTentativas(String logTentativas) {
		this.logTentativas = logTentativas;
	}

	public enum Fields {

		LOA_SEQ("loaSeq"), 
		ACOMODACAO("acomodacao"),
		DIA("dia"),
		TEMPO("tempo"),
		CICLO("ciclo"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		IND_SITUACAO("indSituacao");
		
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
