package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

public class RelatorioExamesPacientesListaLegendaVO implements Serializable , Comparable<RelatorioExamesPacientesListaLegendaVO>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -177746660987429325L;
	
	private String grupoLegenda;
	private String numeroLegenda;
	private String descricaoLegenda;
	
	public String getGrupoLegenda() {
		return grupoLegenda;
	}
	public void setGrupoLegenda(String grupoLegenda) {
		this.grupoLegenda = grupoLegenda;
	}
	public String getNumeroLegenda() {
		return numeroLegenda;
	}
	public void setNumeroLegenda(String numeroLegenda) {
		this.numeroLegenda = numeroLegenda;
	}
	public String getDescricaoLegenda() {
		return descricaoLegenda;
	}
	public void setDescricaoLegenda(String descricaoLegenda) {
		this.descricaoLegenda = descricaoLegenda;
	}
	@Override
	public int compareTo(RelatorioExamesPacientesListaLegendaVO other) {
        int result = Integer.valueOf(this.getGrupoLegenda()).compareTo(Integer.valueOf(other.getGrupoLegenda()));
        if (result == 0) {
                if(this.getNumeroLegenda() != null && other.getNumeroLegenda() != null){
                         return Integer.valueOf(this.getNumeroLegenda()).compareTo(Integer.valueOf(other.getNumeroLegenda()));
                }                                
        }
        return result;
	}

}
