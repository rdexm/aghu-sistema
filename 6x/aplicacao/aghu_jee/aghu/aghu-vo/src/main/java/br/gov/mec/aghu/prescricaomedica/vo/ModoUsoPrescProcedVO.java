package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.model.MpmModoUsoPrescProcedId;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;

public class ModoUsoPrescProcedVO {
	
	private MpmModoUsoPrescProcedId id;
	private MpmTipoModoUsoProcedimento tipoModUsoProcedimento;
	private Short quantidade;
	
	
	public MpmModoUsoPrescProcedId getId() {
		return id;
	}
	public void setId(MpmModoUsoPrescProcedId id) {
		this.id = id;
	}
	public MpmTipoModoUsoProcedimento getTipoModUsoProcedimento() {
		return tipoModUsoProcedimento;
	}
	public void setTipoModUsoProcedimento(
			MpmTipoModoUsoProcedimento tipoModUsoProcedimento) {
		this.tipoModUsoProcedimento = tipoModUsoProcedimento;
	}
	public Short getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}
	
}
