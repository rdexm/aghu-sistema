package br.gov.mec.aghu.procedimentoterapeutico.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class MptProtocoloVersaoAssistencialVO implements BaseBean {
			
	private static final long serialVersionUID = -5390709362184204302L;
	
	private String descricao;
	private String titulo;
	

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
}
