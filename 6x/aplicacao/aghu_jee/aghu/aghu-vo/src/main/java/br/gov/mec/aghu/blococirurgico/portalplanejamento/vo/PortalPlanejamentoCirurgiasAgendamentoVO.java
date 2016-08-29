package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;

public class PortalPlanejamentoCirurgiasAgendamentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6452078356399787802L;
	
	private Short sala;
	private String turno; 
	private DominioDiaSemana diaSemana;
	private String equipe;
	private String especialidade;
	private Short espSeq;
	private Date dataInicio;
	private Date dataFim;
	private Integer orderTurno;
	private Integer matricula;
	private Short vinCodigo;
	private DominioFuncaoProfissional indFuncaoProf;
	
	public Short getSala() {
		return sala;
	}
	public void setSala(Short sala) {
		this.sala = sala;
	}
	public String getTurno() {
		return turno;
	}
	public void setTurno(String turno) {
		this.turno = turno;
	}
	public DominioDiaSemana getDiaSemana() {
		return diaSemana;
	}
	public void setDiaSemana(DominioDiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public Integer getOrderTurno() {
		return orderTurno;
	}
	public void setOrderTurno(Integer orderTurno) {
		this.orderTurno = orderTurno;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
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
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	public DominioFuncaoProfissional getIndFuncaoProf() {
		return indFuncaoProf;
	}
	public void setIndFuncaoProf(DominioFuncaoProfissional indFuncaoProf) {
		this.indFuncaoProf = indFuncaoProf;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}


	public enum Fields {
		SALA("sala"),
		TURNO("turno"),
		ORDER_TURNO("orderTurno"),
		DIA_SEMANA("diaSemana"),
		ESPECIALIDADE("especialidade"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		MATRICULA("matricula"),
		VINCULO("vinCodigo"),
		FUNCAO_PROF("indFuncaoProf"),
		ESP_SEQ("espSeq"),
		EQUIPE("equipe"); 

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
