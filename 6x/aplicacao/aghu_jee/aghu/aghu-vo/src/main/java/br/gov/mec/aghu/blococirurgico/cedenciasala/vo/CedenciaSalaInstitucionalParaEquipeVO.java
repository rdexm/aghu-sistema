package br.gov.mec.aghu.blococirurgico.cedenciasala.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

public class CedenciaSalaInstitucionalParaEquipeVO {

	private Short casSeq;
	private AghUnidadesFuncionais unidade;
	private MbcSalaCirurgica sala;
	private DominioDiaSemana diaSemana;
	private MbcTurnos turno;
	private Date data;
	private LinhaReportVO equipe;
	private Boolean recorrencia;
	private Date dataFim;
	private Integer intervalo;
	private String usuarioLogado;
	
	public CedenciaSalaInstitucionalParaEquipeVO() {
		super();
	}

	//Getters and Setters
	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}
	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}
	public MbcSalaCirurgica getSala() {
		return sala;
	}
	public void setSala(MbcSalaCirurgica sala) {
		this.sala = sala;
	}
	public DominioDiaSemana getDiaSemana() {
		return diaSemana;
	}
	public void setDiaSemana(DominioDiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}
	public MbcTurnos getTurno() {
		return turno;
	}
	public void setTurno(MbcTurnos turno) {
		this.turno = turno;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public LinhaReportVO getEquipe() {
		return equipe;
	}
	public void setEquipe(LinhaReportVO equipe) {
		this.equipe = equipe;
	}
	public Boolean getRecorrencia() {
		return recorrencia;
	}
	public void setRecorrencia(Boolean recorrencia) {
		this.recorrencia = recorrencia;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public Integer getIntervalo() {
		return intervalo;
	}
	public void setIntervalo(Integer intervalo) {
		this.intervalo = intervalo;
	}

	public void setUsuarioLogado(String usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public String getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setCasSeq(Short casSeq) {
		this.casSeq = casSeq;
	}

	public Short getCasSeq() {
		return casSeq;
	}
	
}
