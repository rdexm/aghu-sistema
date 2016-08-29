package br.gov.mec.aghu.internacao.pesquisa.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class CidInternacaoVO implements BaseBean{
   private String descricao;
   private String numero;
   private String prioridade;
   
public String getDescricao() {
	return descricao;
}
public void setDescricao(String descricao) {
	this.descricao = descricao;
}
public String getNumero() {
	return numero;
}
public void setCapitulo(String numero) {
	this.numero = numero;
}
public String getPrioridade() {
	return prioridade;
}
public void setPrioridade(String prioridade) {
	this.prioridade = prioridade;
}
}
