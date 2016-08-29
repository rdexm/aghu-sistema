package br.gov.mec.aghu.exames.agendamento.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoHorario;

public class VAelHrGradeDispVO  {
	
	private Integer id;
	private Boolean hrExtra;
	private Date dthrAgenda;
	private String dtAgenda;
	private String hrAgenda;
	private Short grade;
	private Integer seqGrade;
	private Integer grupoExame;
	private Short unfExame;
	private Short sala;
	private Short salaSeq;
	private Integer matricula;
	private Short vinCodigo;
	private String responsavel;
	private String descricaoGrupo;
	private String descricaoSala;
	private DominioSituacaoHorario situacaoHorario;
	
	public VAelHrGradeDispVO() {
	
	}
	
	
	public VAelHrGradeDispVO(Boolean hrExtra, Date dthrAgenda, String dtAgenda, String hrAgenda,
			Short grade, Integer seqGrade, Integer grupoExame, Short unfExame, Short sala,
			Short salaSeq, String descricaoSala, Integer matricula, Short vinCodigo) {
		this.hrExtra = hrExtra;
		this.dthrAgenda = dthrAgenda;
		this.dtAgenda = dtAgenda;
		this.hrAgenda = hrAgenda;
		this.grade = grade;
		this.seqGrade = seqGrade;
		this.grupoExame = grupoExame;
		this.unfExame = unfExame;
		this.sala = sala;
		this.salaSeq = salaSeq;
		this.descricaoSala = descricaoSala;
		this.matricula = matricula;
		this.vinCodigo = vinCodigo;
	}
	
	public String getDtAgenda() {
		return dtAgenda;
	}
	public void setDtAgenda(String dtAgenda) {
		this.dtAgenda = dtAgenda;
	}
	public String getHrAgenda() {
		return hrAgenda;
	}
	public void setHrAgenda(String hrAgenda) {
		this.hrAgenda = hrAgenda;
	}
	
	public Short getGrade() {
		return grade;
	}
	public void setGrade(Short grade) {
		this.grade = grade;
	}
	public Integer getGrupoExame() {
		return grupoExame;
	}
	public void setGrupoExame(Integer grupoExame) {
		this.grupoExame = grupoExame;
	}
	public Short getSala() {
		return sala;
	}
	public void setSala(Short sala) {
		this.sala = sala;
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
	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}
	public String getDescricaoGrupo() {
		return descricaoGrupo;
	}
	public void setDescricaoGrupo(String descricaoGrupo) {
		this.descricaoGrupo = descricaoGrupo;
	}
	public Integer getSeqGrade() {
		return seqGrade;
	}
	public void setSeqGrade(Integer seqGrade) {
		this.seqGrade = seqGrade;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Date getDthrAgenda() {
		return dthrAgenda;
	}

	public void setDthrAgenda(Date dthrAgenda) {
		this.dthrAgenda = dthrAgenda;
	}

	public Boolean getHrExtra() {
		return hrExtra;
	}

	public void setHrExtra(Boolean hrExtra) {
		this.hrExtra = hrExtra;
	}


	public Short getUnfExame() {
		return unfExame;
	}


	public void setUnfExame(Short unfExame) {
		this.unfExame = unfExame;
	}


	public Short getSalaSeq() {
		return salaSeq;
	}


	public void setSalaSeq(Short salaSeq) {
		this.salaSeq = salaSeq;
	}


	public String getDescricaoSala() {
		return descricaoSala;
	}


	public void setDescricaoSala(String descricaoSala) {
		this.descricaoSala = descricaoSala;
	}


	public DominioSituacaoHorario getSituacaoHorario() {
		return situacaoHorario;
	}


	public void setSituacaoHorario(DominioSituacaoHorario situacaoHorario) {
		this.situacaoHorario = situacaoHorario;
	}
	
	

}
