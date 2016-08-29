package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.core.commons.BaseBean;

public class CampoLaudoVO implements BaseBean {
	
	private static final long serialVersionUID = 7186748282864961026L;
	
	private Integer seq;
	private String nome;
	private DominioTipoCampoCampoLaudo tipoCampo;
	private DominioSituacao situacao;
	private AelGrupoResultadoCodificado grupoResultadoCodificado;	
	private AelGrupoResultadoCaracteristica grupoResultadoCaracteristica;	
	private Boolean permiteDigitacao;
	private Boolean cancelaItemDept;
	private Boolean pertenceContador; 
	private Boolean pertenceAbs;
	
	public CampoLaudoVO() {
		
	}

	/**
	 * Construtor que popula o VO através do POJO AelCampoLaudo
	 * @param campoLaudo
	 */
	public CampoLaudoVO(AelCampoLaudo campoLaudo) {
		this.seq = campoLaudo.getSeq();
		this.nome = campoLaudo.getNome();
		this.tipoCampo = campoLaudo.getTipoCampo();
		this.situacao = campoLaudo.getSituacao();
		this.grupoResultadoCodificado = campoLaudo.getGrupoResultadoCodificado();
		this.grupoResultadoCaracteristica = campoLaudo.getGrupoResultadoCaracteristica();
		this.permiteDigitacao = campoLaudo.getPermiteDigitacao();
		this.cancelaItemDept = campoLaudo.getCancelaItemDept();
		this.pertenceContador = campoLaudo.getPertenceContador();
		this.pertenceAbs = campoLaudo.getPertenceAbs();
	}

	/**
	 * Obtém a descrição completa do Grupo Resultado Codificado
	 * @return
	 */
	public String getDescricaoGrupoResultadoCodificado(){
		return this.grupoResultadoCodificado != null ? this.grupoResultadoCodificado.getSeq() + " - " + this.grupoResultadoCodificado.getDescricao() : null;
	}
	
	/**
	 * Obtém a descrição completa do Grupo Resultado Característica
	 * @return
	 */
	public String getDescricaoGrupoResultadoCaracteristica(){
		return this.grupoResultadoCaracteristica != null ? this.grupoResultadoCaracteristica.getSeq() + " - " + this.grupoResultadoCaracteristica.getDescricao() : null;
	}

	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public DominioTipoCampoCampoLaudo getTipoCampo() {
		return tipoCampo;
	}
	public void setTipoCampo(DominioTipoCampoCampoLaudo tipoCampo) {
		this.tipoCampo = tipoCampo;
	}
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	public AelGrupoResultadoCodificado getGrupoResultadoCodificado() {
		return grupoResultadoCodificado;
	}
	public void setGrupoResultadoCodificado(
			AelGrupoResultadoCodificado grupoResultadoCodificado) {
		this.grupoResultadoCodificado = grupoResultadoCodificado;
	}
	public AelGrupoResultadoCaracteristica getGrupoResultadoCaracteristica() {
		return grupoResultadoCaracteristica;
	}
	public void setGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica) {
		this.grupoResultadoCaracteristica = grupoResultadoCaracteristica;
	}
	public Boolean getPermiteDigitacao() {
		return permiteDigitacao;
	}
	public void setPermiteDigitacao(Boolean permiteDigitacao) {
		this.permiteDigitacao = permiteDigitacao;
	}
	public Boolean getCancelaItemDept() {
		return cancelaItemDept;
	}
	public void setCancelaItemDept(Boolean cancelaItemDept) {
		this.cancelaItemDept = cancelaItemDept;
	}
	public Boolean getPertenceContador() {
		return pertenceContador;
	}
	public void setPertenceContador(Boolean pertenceContador) {
		this.pertenceContador = pertenceContador;
	}
	public Boolean getPertenceAbs() {
		return pertenceAbs;
	}
	public void setPertenceAbs(Boolean pertenceAbs) {
		this.pertenceAbs = pertenceAbs;
	}

}
