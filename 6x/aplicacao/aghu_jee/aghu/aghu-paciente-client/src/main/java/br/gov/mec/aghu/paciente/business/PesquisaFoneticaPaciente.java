package br.gov.mec.aghu.paciente.business;

import java.util.Calendar;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;

public class PesquisaFoneticaPaciente {
	
	private Integer firstResult;
	private Integer maxResults; 
	private String nome; 
	private String nomeMae;
	private String nomeSocial;
	private Boolean respeitarOrdem; 
	private Date dataNascimento;
	private DominioListaOrigensAtendimentos listaOrigensAtendimentos;
	private Boolean isCount = false;
	
	public Integer getFirstResult() {
		return firstResult;
	}
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}
	public Integer getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getNomeMae() {
		return nomeMae;
	}
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	public Boolean getRespeitarOrdem() {
		return respeitarOrdem;
	}
	public void setRespeitarOrdem(Boolean respeitarOrdem) {
		this.respeitarOrdem = respeitarOrdem;
	}
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public DominioListaOrigensAtendimentos getListaOrigensAtendimentos() {
		return listaOrigensAtendimentos;
	}
	public void setListaOrigensAtendimentos(DominioListaOrigensAtendimentos listaOrigensAtendimentos) {
		this.listaOrigensAtendimentos = listaOrigensAtendimentos;
	}
	public String getNomeSocial() {
		return nomeSocial;
	}
	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}
	
	public Date getInicioMesDataNascimento() {
		if (this.dataNascimento != null) {
			Calendar dataInicioMes = Calendar.getInstance();
			dataInicioMes.setTime(this.dataNascimento);
			dataInicioMes.set(Calendar.DAY_OF_MONTH, dataInicioMes.getActualMinimum(Calendar.DAY_OF_MONTH));
			return dataInicioMes.getTime();
		}
		return null;
	}
	
	public Date getFimMesDataNascimento() {
		if (this.dataNascimento != null) {
			Calendar dataFimMes = Calendar.getInstance();
			dataFimMes.setTime(this.dataNascimento);
			dataFimMes.set(Calendar.DAY_OF_MONTH, dataFimMes.getActualMaximum(Calendar.DAY_OF_MONTH));
			return dataFimMes.getTime();
		}
		return null;
	}
	public Boolean getIsCount() {
		return isCount;
	}
	public void setIsCount(Boolean isCount) {
		this.isCount = isCount;
	}
}
