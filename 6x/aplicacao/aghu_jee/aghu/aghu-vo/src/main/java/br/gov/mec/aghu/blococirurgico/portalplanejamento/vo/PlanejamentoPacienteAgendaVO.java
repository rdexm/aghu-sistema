package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import br.gov.mec.aghu.model.MbcAgendas;


public class PlanejamentoPacienteAgendaVO {
	
	private Short seqEspecialidade;
	private Integer matriculaEquipe;
	private Short vinCodigoEquipe;
	private Short unfSeqEquipe;
	private String indFuncaoProfEquipe;
	private Short seqUnidFuncionalCirugica;
	private String cameFrom;
	private Long dataAgenda;
	private Integer agdSeq;
	private String situacaoAgenda;
	
	public PlanejamentoPacienteAgendaVO(MbcAgendas agenda){
		if(agenda.getEspecialidade()!=null){
			seqEspecialidade = agenda.getEspecialidade().getSeq();
		}else {
			seqEspecialidade = null;
		}
		
		if(agenda.getProfAtuaUnidCirgs()!=null){
			matriculaEquipe = agenda.getProfAtuaUnidCirgs().getId().getSerMatricula();
			vinCodigoEquipe = agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo();
			unfSeqEquipe = agenda.getProfAtuaUnidCirgs().getId().getUnfSeq();
			indFuncaoProfEquipe = agenda.getProfAtuaUnidCirgs().getId().getIndFuncaoProf().toString();
		}
		
		if(agenda.getUnidadeFuncional()!=null){
			seqUnidFuncionalCirugica = agenda.getUnidadeFuncional().getSeq();
		}
		
		if(agenda.getDtAgenda()!=null){
			dataAgenda = agenda.getDtAgenda().getTime();
		}
		
		if(agenda.getSeq()!=null){
			agdSeq = agenda.getSeq();
		}
		
		if(agenda.getIndSituacao()!=null){
			situacaoAgenda = agenda.getIndSituacao().toString();
		}
	}
	
	
	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}
	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}
	public Integer getMatriculaEquipe() {
		return matriculaEquipe;
	}
	public void setMatriculaEquipe(Integer matriculaEquipe) {
		this.matriculaEquipe = matriculaEquipe;
	}
	public Short getVinCodigoEquipe() {
		return vinCodigoEquipe;
	}
	public void setVinCodigoEquipe(Short vinCodigoEquipe) {
		this.vinCodigoEquipe = vinCodigoEquipe;
	}
	public Short getUnfSeqEquipe() {
		return unfSeqEquipe;
	}
	public void setUnfSeqEquipe(Short unfSeqEquipe) {
		this.unfSeqEquipe = unfSeqEquipe;
	}
	public String getIndFuncaoProfEquipe() {
		return indFuncaoProfEquipe;
	}
	public void setIndFuncaoProfEquipe(String indFuncaoProfEquipe) {
		this.indFuncaoProfEquipe = indFuncaoProfEquipe;
	}
	public Short getSeqUnidFuncionalCirugica() {
		return seqUnidFuncionalCirugica;
	}
	public void setSeqUnidFuncionalCirugica(Short seqUnidFuncionalCirugica) {
		this.seqUnidFuncionalCirugica = seqUnidFuncionalCirugica;
	}
	public String getCameFrom() {
		return cameFrom;
	}
	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
	public Long getDataAgenda() {
		return dataAgenda;
	}
	public void setDataAgenda(Long dataAgenda) {
		this.dataAgenda = dataAgenda;
	}
	public Integer getAgdSeq() {
		return agdSeq;
	}
	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}
	public String getSituacaoAgenda() {
		return situacaoAgenda;
	}
	public void setSituacaoAgenda(String situacaoAgenda) {
		this.situacaoAgenda = situacaoAgenda;
	}
}
