package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.core.utils.StringUtil;

public class RelatorioFichaTrabalhoPatologiaExameVO implements Serializable {

	private static final long serialVersionUID = 4062266481609587577L;

	private Short amostraSeqP;
	private Short itemSolExSeqP;
	private Long numeroAp;
	private String nroFrascoFabricante;
	private BigDecimal tempoIntervaloColeta;
	private Date dtNumeroUnico;
	private Integer nroUnico;
	private String nomeUsualMaterial;
	private String descIntervaloColeta;
	private Byte prioridadeExecucao;
	private Byte nroAmostras;
	private DominioTipoColeta tipoColeta;
	private String descMatAnalise;
	private String itemRegiaoAnatomicaDescricao;
	private String regiaoAnatoDescricao;
	private String sigla;
	
	
	public Short getAmostraSeqP() {
		return amostraSeqP;
	}
	
	public void setAmostraSeqP(Short amostraSeqP) {
		this.amostraSeqP = amostraSeqP;
	}

	public Short getItemSolExSeqP() {
		return itemSolExSeqP;
	}

	public void setItemSolExSeqP(Short itemSolExSeqP) {
		this.itemSolExSeqP = itemSolExSeqP;
	}

	public String getNumeroApDesc() {
		return (this.numeroAp != null) ? StringUtil.formataNumeroAp(this.numeroAp) : null;
	}
	
	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	public String getNomeUsualMaterial() {
		return nomeUsualMaterial;
	}

	public void setNomeUsualMaterial(String nomeUsualMaterial) {
		this.nomeUsualMaterial = nomeUsualMaterial;
	}

	public String getDescIntervaloColeta() {
		return descIntervaloColeta;
	}

	public void setDescIntervaloColeta(String descIntervaloColeta) {
		this.descIntervaloColeta = descIntervaloColeta;
	}

	public Byte getPrioridadeExecucao() {
		return prioridadeExecucao;
	}

	public void setPrioridadeExecucao(Byte prioridadeExecucao) {
		this.prioridadeExecucao = prioridadeExecucao;
	}

	public DominioTipoColeta getTipoColeta() {
		return tipoColeta;
	}

	public void setTipoColeta(DominioTipoColeta tipoColeta) {
		this.tipoColeta = tipoColeta;
	}

	public String getTipoColetaDescricao() {
		return (tipoColeta!=null)?tipoColeta.getDescricao():null;
	}

	public String getDescMatAnalise() {
		return (this.descMatAnalise != null)?"DESC. MATERIAL: "+descMatAnalise:null;
	}

	public void setDescMatAnalise(String descMatAnalise) {
		this.descMatAnalise = descMatAnalise;
	}

	public String getDescRegiaoAnatomica() {
		return (this.itemRegiaoAnatomicaDescricao!=null)?"Desc. Região Anatômica: " + this.itemRegiaoAnatomicaDescricao:((this.regiaoAnatoDescricao !=null)?"Desc. Região Anatômica: " + this.regiaoAnatoDescricao:null);
	}

	public String getItemRegiaoAnatomicaDescricao() {
		return itemRegiaoAnatomicaDescricao;
	}

	public void setItemRegiaoAnatomicaDescricao(String itemRegiaoAnatomicaDescricao) {
		this.itemRegiaoAnatomicaDescricao = itemRegiaoAnatomicaDescricao;
	}

	public String getRegiaoAnatoDescricao() {
		return regiaoAnatoDescricao;
	}

	public void setRegiaoAnatoDescricao(String regiaoAnatoDescricao) {
		this.regiaoAnatoDescricao = regiaoAnatoDescricao;
	}

	public String getNroFrascoFabricante() {
		return nroFrascoFabricante;
	}

	public void setNroFrascoFabricante(String nroFrascoFabricante) {
		this.nroFrascoFabricante = nroFrascoFabricante;
	}

	public BigDecimal getTempoIntervaloColeta() {
		return tempoIntervaloColeta;
	}

	public void setTempoIntervaloColeta(BigDecimal tempoIntervaloColeta) {
		this.tempoIntervaloColeta = tempoIntervaloColeta;
	}

	public Date getDtNumeroUnico() {
		return dtNumeroUnico;
	}

	public void setDtNumeroUnico(Date dtNumeroUnico) {
		this.dtNumeroUnico = dtNumeroUnico;
	}

	public Integer getNroUnico() {
		return nroUnico;
	}

	public void setNroUnico(Integer nroUnico) {
		this.nroUnico = nroUnico;
	}

	public String getNroAmostraVtlEd() {
		return (nroAmostras != null)?nroAmostras.toString() + "ª amostra":"ª amostra";
	}

	public Byte getNroAmostras() {
		return nroAmostras;
	}

	public void setNroAmostras(Byte nroAmostras) {
		this.nroAmostras = nroAmostras;
	}

	public String getSigla() {
		return (sigla == null ? "AP" : sigla);
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
	
}
