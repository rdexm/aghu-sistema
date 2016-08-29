package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class PendenciaWorkflowVO implements Serializable {
	
	private static final long serialVersionUID = -4416334722208802279L;
	
	/**
	 * Dados do executor.
	 */
	private Integer matriculaExecutor;	
	private Short vinCodigoExecutor;
	
	/**
	 * Dados do template de etapa.
	 */
	private Boolean indRecebeNotificacao;
	private String codigo;
	private String descricao;	
	private String url;
	
	public Integer getMatriculaExecutor() {
		return matriculaExecutor;
	}
	public void setMatriculaExecutor(Integer matriculaExecutor) {
		this.matriculaExecutor = matriculaExecutor;
	}
	public Short getVinCodigoExecutor() {
		return vinCodigoExecutor;
	}
	public void setVinCodigoExecutor(Short vinCodigoExecutor) {
		this.vinCodigoExecutor = vinCodigoExecutor;
	}
	public Boolean getIndRecebeNotificacao() {
		return indRecebeNotificacao;
	}
	public void setIndRecebeNotificacao(Boolean indRecebeNotificacao) {
		this.indRecebeNotificacao = indRecebeNotificacao;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return "Pendência: " + descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
