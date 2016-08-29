package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacHorarioGradeConsulta;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AelProjetoPesquisas;


public class HorarioGeradoConsultaVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2593788037138826974L;
	private Date dataAtual;
	private Date horaInicio;
	private String tpsTipo;
	private Date tempo;
	private Date horaFim;
	private Short numHorario;
	private Boolean geraFeriado;
	private Date dataUltimaGeracao;
	private Integer seqP;
	private AacCondicaoAtendimento condicaoAtendimento;
	private AacTipoAgendamento tipoAgendamento;
	private AacPagador pagador;
	private AelProjetoPesquisas projetoPesquisa;
	private Integer quantidadeGerada;
	private AacHorarioGradeConsulta horarioGradeConsulta;
	
	public HorarioGeradoConsultaVO(){
	}

	public HorarioGeradoConsultaVO(Date dataAtual, Date horaInicio,
			String tpsTipo, Date tempo, Date horaFim, Short numHorario,
			Boolean geraFeriado, Date dataUltimaGeracao, Integer seqP,
			AacCondicaoAtendimento condicaoAtendimento,
			AacTipoAgendamento tipoAgendamento, AacPagador pagador,
			AelProjetoPesquisas projetoPesquisa, AacHorarioGradeConsulta horarioGradeConsulta) {
		super();
		this.dataAtual = dataAtual;
		this.horaInicio = horaInicio;
		this.tpsTipo = tpsTipo;
		this.tempo = tempo;
		this.horaFim = horaFim;
		this.numHorario = numHorario;
		this.geraFeriado = geraFeriado;
		this.dataUltimaGeracao = dataUltimaGeracao;
		this.seqP = seqP;
		this.condicaoAtendimento = condicaoAtendimento;
		this.tipoAgendamento = tipoAgendamento;
		this.pagador = pagador;
		this.projetoPesquisa = projetoPesquisa;
		this.horarioGradeConsulta = horarioGradeConsulta;
	}

	public Date getDataAtual() {
		return dataAtual;
	}
	
	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}
	
	public Date getHoraInicio() {
		return horaInicio;
	}
	
	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}
	
	public String getTpsTipo() {
		return tpsTipo;
	}
	
	public void setTpsTipo(String tpsTipo) {
		this.tpsTipo = tpsTipo;
	}
	
	public Date getTempo() {
		return tempo;
	}
	
	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}
	
	public Date getHoraFim() {
		return horaFim;
	}
	
	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}
	
	public Short getNumHorario() {
		return numHorario;
	}
	
	public void setNumHorario(Short numHorario) {
		this.numHorario = numHorario;
	}
	
	public Boolean getGeraFeriado() {
		return geraFeriado;
	}
	
	public void setGeraFeriado(Boolean geraFeriado) {
		this.geraFeriado = geraFeriado;
	}
	
	public Date getDataUltimaGeracao() {
		return dataUltimaGeracao;
	}
	
	public void setDataUltimaGeracao(Date dataUltimaGeracao) {
		this.dataUltimaGeracao = dataUltimaGeracao;
	}
	
	public Integer getSeqP() {
		return seqP;
	}
	
	public void setSeqP(Integer seqP) {
		this.seqP = seqP;
	}
	
	public AacCondicaoAtendimento getCondicaoAtendimento() {
		return condicaoAtendimento;
	}
	
	public void setCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) {
		this.condicaoAtendimento = condicaoAtendimento;
	}
	
	public AacTipoAgendamento getTipoAgendamento() {
		return tipoAgendamento;
	}
	
	public void setTipoAgendamento(AacTipoAgendamento tipoAgendamento) {
		this.tipoAgendamento = tipoAgendamento;
	}
	
	public AacPagador getPagador() {
		return pagador;
	}
	
	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}
	
	public AelProjetoPesquisas getProjetoPesquisa() {
		return projetoPesquisa;
	}
	
	public void setProjetoPesquisa(AelProjetoPesquisas projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}
	
	public Integer getQuantidadeGerada() {
		return quantidadeGerada;
	}
	
	public void setQuantidadeGerada(Integer quantidadeGerada) {
		this.quantidadeGerada = quantidadeGerada;
	}

	public void setHorarioGradeConsulta(AacHorarioGradeConsulta horarioGradeConsulta) {
		this.horarioGradeConsulta = horarioGradeConsulta;
	}

	public AacHorarioGradeConsulta getHorarioGradeConsulta() {
		return horarioGradeConsulta;
	}	

}
