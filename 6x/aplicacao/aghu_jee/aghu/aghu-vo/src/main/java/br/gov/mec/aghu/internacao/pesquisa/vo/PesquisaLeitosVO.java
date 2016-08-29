package br.gov.mec.aghu.internacao.pesquisa.vo;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Os dados armazenados nesse objeto representam os parametros da pesquisa de leitos
 * 
 * @author lalegre
 */
public class PesquisaLeitosVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3993162401729166969L;
	String prontuario;
	String nome;
	String descricaoUnidade;
	DominioSimNao indLeitoIsolamento;
	String criadoEm;
	String descricaoAcomodacao;
	String nomeEspecialidade;
	String observacao;
	
	String leito;
	String ala;
	Byte clcCodigo;
	String sigla;
	String dthrLancamento;
	String sexo;
	String grupoMovimento;
	String descricaoMovimentacao;
	String caracteristica;
	Integer intSeq;
	boolean atUrgencia;
	boolean internacao;
	
	
	// GETs AND SEts

	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}
	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}
	public DominioSimNao getIndLeitoIsolamento() {
		return indLeitoIsolamento;
	}
	public void setIndLeitoIsolamento(DominioSimNao indLeitoIsolamento) {
		this.indLeitoIsolamento = indLeitoIsolamento;
	}
	public String getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getDescricaoAcomodacao() {
		return descricaoAcomodacao;
	}
	public void setDescricaoAcomodacao(String descricaoAcomodacao) {
		this.descricaoAcomodacao = descricaoAcomodacao;
	}
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public String getAla() {
		return ala;
	}
	public void setAla(String ala) {
		this.ala = ala;
	}
	public Byte getClcCodigo() {
		return clcCodigo;
	}
	public void setClcCodigo(Byte clcCodigo) {
		this.clcCodigo = clcCodigo;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getDthrLancamento() {
		return dthrLancamento;
	}
	public void setDthrLancamento(String dthrLancamento) {
		this.dthrLancamento = dthrLancamento;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getDescricaoMovimentacao() {
		return descricaoMovimentacao;
	}
	public void setDescricaoMovimentacao(String descricaoMovimentacao) {
		this.descricaoMovimentacao = descricaoMovimentacao;
	}
	public String getCaracteristica() {
		return caracteristica;
	}
	public void setCaracteristica(String caracteristica) {
		this.caracteristica = caracteristica;
	}
	public boolean isAtUrgencia() {
		return atUrgencia;
	}
	public void setAtUrgencia(boolean atUrgencia) {
		this.atUrgencia = atUrgencia;
	}
	public String getGrupoMovimento() {
		return grupoMovimento;
	}
	public void setGrupoMovimento(String grupoMovimento) {
		this.grupoMovimento = grupoMovimento;
	}
	public boolean isInternacao() {
		return internacao;
	}
	public void setInternacao(boolean internacao) {
		this.internacao = internacao;
	}
	public Integer getIntSeq() {
		return intSeq;
	}
	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

}
