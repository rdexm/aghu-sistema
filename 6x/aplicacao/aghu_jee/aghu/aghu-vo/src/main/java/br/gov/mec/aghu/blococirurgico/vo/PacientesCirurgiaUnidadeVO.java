package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;

public class PacientesCirurgiaUnidadeVO implements Serializable  {
	
	private static final long serialVersionUID = -5811566816823021846L;
	private String unid;
	private String equipe;
	private Date dataini;
	private Date datafim;
	private DominioSituacaoCirurgia sit;
	private Integer prnt;
	private String nome;
	private String cnvdesc;
	private String proc;
	private Date dataagenda;
	private String motivo;
	private DominioOrigemPacienteCirurgia origem;
	private DominioNaturezaFichaAnestesia natureza;
	private String esp;
	private Date dtatend;
	
	public String getUnid() {
		return unid;
	}
	
	public void setUnid(String unid) {
		this.unid = unid;
	}
	
	public String getEquipe() {
		return equipe;
	}
	
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	
	public Date getDataini() {
		return dataini;
	}
	
	public void setDataini(Date dataini) {
		this.dataini = dataini;
	}
	
	public Date getDatafim() {
		return datafim;
	}
	
	public void setDatafim(Date datafim) {
		this.datafim = datafim;
	}
	
	public DominioSituacaoCirurgia getSit() {
		return sit;
	}
	
	public void setSit(DominioSituacaoCirurgia sit) {
		this.sit = sit;
	}
	
	public Integer getPrnt() {
		return prnt;
	}
	
	public void setPrnt(Integer prnt) {
		this.prnt = prnt;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getCnvdesc() {
		return cnvdesc;
	}
	
	public void setCnvdesc(String cnvdesc) {
		this.cnvdesc = cnvdesc;
	}
	
	public String getProc() {
		return proc;
	}
	
	public void setProc(String proc) {
		this.proc = proc;
	}
	
	public Date getDataagenda() {
		return dataagenda;
	}
	
	public void setDataagenda(Date dataagenda) {
		this.dataagenda = dataagenda;
	}
	
	public String getMotivo() {
		return motivo;
	}
	
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	
	public DominioOrigemPacienteCirurgia getOrigem() {
		return origem;
	}
	
	public void setOrigem(DominioOrigemPacienteCirurgia origem) {
		this.origem = origem;
	}
	
	public DominioNaturezaFichaAnestesia getNatureza() {
		return natureza;
	}
	
	public void setNatureza(DominioNaturezaFichaAnestesia natureza) {
		this.natureza = natureza;
	}
	
	public String getEsp() {
		return esp;
	}
	
	public void setEsp(String esp) {
		this.esp = esp;
	}
	
	public Date getDtatend() {
		return dtatend;
	}
	
	public void setDtatend(Date dtatend) {
		this.dtatend = dtatend;
	}
	
}
