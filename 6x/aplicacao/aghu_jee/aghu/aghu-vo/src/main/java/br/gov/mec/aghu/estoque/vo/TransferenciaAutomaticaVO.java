package br.gov.mec.aghu.estoque.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.core.commons.BaseBean;

public class TransferenciaAutomaticaVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -787653316540901383L;
	private Integer seq;
	private ScoClassifMatNiv5 classificacaoMaterial;
	private String descricaoClassificacaoMaterial;
	private SceAlmoxarifado almoxarifadoOrigem;
	private SceAlmoxarifado almoxarifadoDestino;
	private Date dtGeracao;
	private Date dtEfetivacao;
	private RapServidores servidorEfetivado;
	private RapServidores servidor;
	
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public ScoClassifMatNiv5 getClassificacaoMaterial() {
		return classificacaoMaterial;
	}
	public void setClassificacaoMaterial(ScoClassifMatNiv5 classificacaoMaterial) {
		this.classificacaoMaterial = classificacaoMaterial;
	}
	public SceAlmoxarifado getAlmoxarifadoOrigem() {
		return almoxarifadoOrigem;
	}
	public void setAlmoxarifadoOrigem(SceAlmoxarifado almoxarifadoOrigem) {
		this.almoxarifadoOrigem = almoxarifadoOrigem;
	}
	public SceAlmoxarifado getAlmoxarifadoDestino() {
		return almoxarifadoDestino;
	}
	public void setAlmoxarifadoDestino(SceAlmoxarifado almoxarifadoDestino) {
		this.almoxarifadoDestino = almoxarifadoDestino;
	}
	public Date getDtGeracao() {
		return dtGeracao;
	}
	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}
	public RapServidores getServidor() {
		return servidor;
	}
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	public Date getDtEfetivacao() {
		return dtEfetivacao;
	}
	public void setDtEfetivacao(Date dtEfetivacao) {
		this.dtEfetivacao = dtEfetivacao;
	}
	public RapServidores getServidorEfetivado() {
		return servidorEfetivado;
	}
	public void setServidorEfetivado(RapServidores servidorEfetivado) {
		this.servidorEfetivado = servidorEfetivado;
	}
	/**
	 * Gera uma descrição do almoxarifado de origem através do número e descrição
	 * @return
	 */
	public String getAlmoxarifadoOrigemDescricao() {
		if (this.almoxarifadoOrigem != null){
			return this.almoxarifadoOrigem.getSeqDescricao();
		}
		return "";
	}
	
	/**
	 * Gera uma descrição do almoxarifado de destino através do número e descrição
	 * @return
	 */
	public String getAlmoxarifadoDestinoDescricao() {
		if (this.almoxarifadoDestino != null){
			return this.almoxarifadoDestino.getSeqDescricao();
		}
		return "";
	}
	
	public String getDescricaoClassificacaoMaterial() {
		return descricaoClassificacaoMaterial;
	}
	
	public void setDescricaoClassificacaoMaterial(String descricaoClassificacaoMaterial) {
		this.descricaoClassificacaoMaterial = descricaoClassificacaoMaterial;
	}
	
	/**
	 * Gera uma descrição da classificação do material através do código e descricao
	 * @return
	 */
	public String getNumeroDescricaoClassificacaoMaterial() {
		
		if (this.classificacaoMaterial != null){
			final String descricao = this.descricaoClassificacaoMaterial == null ? "" : " - " + this.descricaoClassificacaoMaterial;
			return this.classificacaoMaterial.getNumero() + descricao;
		}
		
		return "";
	}
	
	
	/**
	 * Gera uma descrição da geração do registro através da data de geração e usuário que realizou a mesma
	 * @return
	 */
	public String getGeradoEm() {
		
		if (this.dtGeracao != null && this.servidor != null){
			return new SimpleDateFormat("dd/MM/yyyy").format(this.dtGeracao) + " - " + this.servidor.getPessoaFisica().getNome();
		}
		
		return "";
	}
	
	/**
	 * Gera uma descrição da geração do registro através da data de geração e usuário que realizou a mesma
	 * @return
	 */
	public String getEfetivadoEm() {
		
		if (this.dtEfetivacao != null && this.servidorEfetivado != null){
			return new SimpleDateFormat("dd/MM/yyyy").format(this.dtEfetivacao) + " - " + this.servidorEfetivado.getPessoaFisica().getNome();
		}
		
		return "";
	}	
}
