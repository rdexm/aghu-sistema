package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceTipoMovimento;

public class ItemNrVO {
	
	private Integer frnNumero; 
	private Integer mcmCodigo; 
	private Integer ncNumero;
	private SceAlmoxarifado almoxarifado;
	private SceTipoMovimento tipoMovimento;
	
	public Integer getFrnNumero() {
		return frnNumero;
	}
	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}
	public Integer getMcmCodigo() {
		return mcmCodigo;
	}
	public void setMcmCodigo(Integer mcmCodigo) {
		this.mcmCodigo = mcmCodigo;
	}
	public Integer getNcNumero() {
		return ncNumero;
	}
	public void setNcNumero(Integer ncNumero) {
		this.ncNumero = ncNumero;
	}
	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}
	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}
	public SceTipoMovimento getTipoMovimento() {
		return tipoMovimento;
	}
	public void setTipoMovimento(SceTipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}
	

}
