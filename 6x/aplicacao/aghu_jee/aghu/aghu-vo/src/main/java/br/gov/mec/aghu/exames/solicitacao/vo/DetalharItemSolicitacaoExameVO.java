package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoColeta;

public class DetalharItemSolicitacaoExameVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3938252043002099924L;
	//private AelItemSolicitacaoExames item;
	
	//exameVO.item.id.seqp
	private Short itemSolicitacaoExamesIdSeqp;
	//exameVO.item.aelUnfExecutaExames.aelExamesMaterialAnalise.nomeUsualMaterial
	private String nomeUsualExamesMaterialAnalise;
	//exameVO.item.situacaoItemSolicitacao.descricao
	private String descricaoSituacaoItemSolicitacao;
	//exameVO.item.tipoColeta
	private DominioTipoColeta tipoColeta;
	//exameVO.item.tipoTransporte.descricao
	private String descricaoTipoTransporte;
	//exameVO.item.indUsoO2String
	private String indUsoO2String;
	//exameVO.item.regiaoAnatomica.descricao
	private String descricaoRegiaoAnatomica;
	//.exameVO.item.descMaterialAnalise
	private String descMaterialAnalise;
	//exameVO.item.nroAmostras
	private Byte nroAmostras;
	//exameVO.item.intervaloDiasHoras
	private String intervaloDiasHoras;
	//exameVO.item.dthrProgramada
	private Date dthrProgramada;
	//exameVO.item.prioridadeExecucao
	private Byte prioridadeExecucao;
	//exameVO.item.intervaloColeta.nroColetas
	private Short nroColetasIntervaloColeta;
	//exameVO.item.intervaloColeta.descricao
	private String descricaoIntervaloColeta;
	//exameVO.item.aelMotivoCancelaExames.descricao
	private String descricaoMotivoCancelaExames;
	//exameVO.item.complementoMotCanc
	private String complementoMotCanc;
	
	
	public Short getItemSolicitacaoExamesIdSeqp() {
		return itemSolicitacaoExamesIdSeqp;
	}
	public void setItemSolicitacaoExamesIdSeqp(Short itemSolicitacaoExamesIdSeqp) {
		this.itemSolicitacaoExamesIdSeqp = itemSolicitacaoExamesIdSeqp;
	}
	public String getNomeUsualExamesMaterialAnalise() {
		return nomeUsualExamesMaterialAnalise;
	}
	public void setNomeUsualExamesMaterialAnalise(
			String nomeUsualExamesMaterialAnalise) {
		this.nomeUsualExamesMaterialAnalise = nomeUsualExamesMaterialAnalise;
	}
	public String getDescricaoSituacaoItemSolicitacao() {
		return descricaoSituacaoItemSolicitacao;
	}
	public void setDescricaoSituacaoItemSolicitacao(
			String descricaoSituacaoItemSolicitacao) {
		this.descricaoSituacaoItemSolicitacao = descricaoSituacaoItemSolicitacao;
	}
	public DominioTipoColeta getTipoColeta() {
		return tipoColeta;
	}
	public void setTipoColeta(DominioTipoColeta tipoColeta) {
		this.tipoColeta = tipoColeta;
	}
	public String getDescricaoTipoTransporte() {
		return descricaoTipoTransporte;
	}
	public void setDescricaoTipoTransporte(String descricaoTipoTransporte) {
		this.descricaoTipoTransporte = descricaoTipoTransporte;
	}
	public String getIndUsoO2String() {
		return indUsoO2String;
	}
	public void setIndUsoO2String(String indUsoO2String) {
		this.indUsoO2String = indUsoO2String;
	}
	public String getDescricaoRegiaoAnatomica() {
		return descricaoRegiaoAnatomica;
	}
	public void setDescricaoRegiaoAnatomica(String descricaoRegiaoAnatomica) {
		this.descricaoRegiaoAnatomica = descricaoRegiaoAnatomica;
	}
	public String getDescMaterialAnalise() {
		return descMaterialAnalise;
	}
	public void setDescMaterialAnalise(String descMaterialAnalise) {
		this.descMaterialAnalise = descMaterialAnalise;
	}
	public Byte getNroAmostras() {
		return nroAmostras;
	}
	public void setNroAmostras(Byte nroAmostras) {
		this.nroAmostras = nroAmostras;
	}
	public String getIntervaloDiasHoras() {
		return intervaloDiasHoras;
	}
	public void setIntervaloDiasHoras(String intervaloDiasHoras) {
		this.intervaloDiasHoras = intervaloDiasHoras;
	}
	public Date getDthrProgramada() {
		return dthrProgramada;
	}
	public void setDthrProgramada(Date dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}
	public Byte getPrioridadeExecucao() {
		return prioridadeExecucao;
	}
	public void setPrioridadeExecucao(Byte prioridadeExecucao) {
		this.prioridadeExecucao = prioridadeExecucao;
	}
	public Short getNroColetasIntervaloColeta() {
		return nroColetasIntervaloColeta;
	}
	public void setNroColetasIntervaloColeta(Short nroColetasIntervaloColeta) {
		this.nroColetasIntervaloColeta = nroColetasIntervaloColeta;
	}
	public String getDescricaoIntervaloColeta() {
		return descricaoIntervaloColeta;
	}
	public void setDescricaoIntervaloColeta(String descricaoIntervaloColeta) {
		this.descricaoIntervaloColeta = descricaoIntervaloColeta;
	}
	public String getDescricaoMotivoCancelaExames() {
		return descricaoMotivoCancelaExames;
	}
	public void setDescricaoMotivoCancelaExames(String descricaoMotivoCancelaExames) {
		this.descricaoMotivoCancelaExames = descricaoMotivoCancelaExames;
	}
	public String getComplementoMotCanc() {
		return complementoMotCanc;
	}
	public void setComplementoMotCanc(String complementoMotCanc) {
		this.complementoMotCanc = complementoMotCanc;
	}
}