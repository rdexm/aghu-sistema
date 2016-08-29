package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.util.List;

public class AutorizacaoFornecimentoEmailVO {
	private String emailAssunto;
	private String textoEmail;
	private String textoEmailHTML;
	private String emailFornecedor;
	private String emailCopia;
	private String emailUsuarioLogado; //usuario logado
	private byte[] planilhaAnexo;
	private String razaoSocialFornecedor;
	
	private Boolean enviarFornecedor = Boolean.FALSE;
	private Boolean enviarUsuarioLogado = Boolean.FALSE;
	private Boolean enviarCopia = Boolean.FALSE;
	private Boolean anexarPlanilha = Boolean.FALSE;
	private Boolean mostrarMensagem;
	
	private List<AutorizacaoFornecimentoItemFornecedorVO> itensFornecedor;
	
	public String getTextoEmail() {
		return textoEmail;
	}
	public void setTextoEmail(String textoEmail) {
		this.textoEmail = textoEmail;
	}
	public List<AutorizacaoFornecimentoItemFornecedorVO> getItensFornecedor() {
		return itensFornecedor;
	}
	public void setItensFornecedor(
			List<AutorizacaoFornecimentoItemFornecedorVO> itensFornecedor) {
		this.itensFornecedor = itensFornecedor;
	}
	public String getTextoEmailHTML() {
		return textoEmailHTML;
	}
	public void setTextoEmailHTML(String textoEmailHTML) {
		this.textoEmailHTML = textoEmailHTML;
	}
	public String getEmailFornecedor() {
		return emailFornecedor;
	}
	public void setEmailFornecedor(String emailFornecedor) {
		this.emailFornecedor = emailFornecedor;
	}
	public String getEmailCopia() {
		return emailCopia;
	}
	public void setEmailCopia(String emailCopia) {
		this.emailCopia = emailCopia;
	}
	public String getEmailUsuarioLogado() {
		return emailUsuarioLogado;
	}
	public void setEmailUsuarioLogado(String emailUsuarioLogado) {
		this.emailUsuarioLogado = emailUsuarioLogado;
	}
	public String getEmailAssunto() {
		return emailAssunto;
	}
	public void setEmailAssunto(String emailAssunto) {
		this.emailAssunto = emailAssunto;
	}
	public byte[] getPlanilhaAnexo() {
		return planilhaAnexo;
	}
	public void setPlanilhaAnexo(byte[] planilhaAnexo) {
		this.planilhaAnexo = planilhaAnexo;
	}
	public Boolean getEnviarFornecedor() {
		return enviarFornecedor;
	}
	public void setEnviarFornecedor(Boolean enviarFornecedor) {
		this.enviarFornecedor = enviarFornecedor;
	}
	public Boolean getEnviarUsuarioLogado() {
		return enviarUsuarioLogado;
	}
	public void setEnviarUsuarioLogado(Boolean enviarUsuarioLogado) {
		this.enviarUsuarioLogado = enviarUsuarioLogado;
	}
	public Boolean getEnviarCopia() {
		return enviarCopia;
	}
	public void setEnviarCopia(Boolean enviarCopia) {
		this.enviarCopia = enviarCopia;
	}
	public Boolean getAnexarPlanilha() {
		return anexarPlanilha;
	}
	public void setAnexarPlanilha(Boolean anexarPlanilha) {
		this.anexarPlanilha = anexarPlanilha;
	}
	
	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}
	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}
	public Boolean getMostrarMensagem() {
		return mostrarMensagem;
	}
	public void setMostrarMensagem(Boolean mostrarMensagem) {
		this.mostrarMensagem = mostrarMensagem;
	}
}
