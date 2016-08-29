package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.core.exception.Severity;


public class RegimeProcedimentoAgendaVO {

	private DominioRegimeProcedimentoCirurgicoSus regime;
	private Severity severidade;
	private String mensagem;
	private String descricaoRegime;
	private String descricaoRegimeProcedSus;
	private String descricaoProc;
	
	public DominioRegimeProcedimentoCirurgicoSus getRegime() {
		return regime;
	}
	public void setRegime(DominioRegimeProcedimentoCirurgicoSus regime) {
		this.regime = regime;
	}
	public Severity getSeveridade() {
		return severidade;
	}
	public void setSeveridade(Severity severidade) {
		this.severidade = severidade;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public String getDescricaoRegime() {
		return descricaoRegime;
	}
	public void setDescricaoRegime(String descricaoRegime) {
		this.descricaoRegime = descricaoRegime;
	}
	public String getDescricaoRegimeProcedSus() {
		return descricaoRegimeProcedSus;
	}
	public void setDescricaoRegimeProcedSus(String descricaoRegimeProcedSus) {
		this.descricaoRegimeProcedSus = descricaoRegimeProcedSus;
	}
	public String getDescricaoProc() {
		return descricaoProc;
	}
	public void setDescricaoProc(String descricaoProc) {
		this.descricaoProc = descricaoProc;
	}
}
