package br.gov.mec.aghu.compras.contaspagar.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PagamentosRealizadosPeriodoPdfSubLicitacoesVO implements BaseBean {

	private static final long serialVersionUID = -803970104049975259L;
	
	/*-*-*-* Variaveis *-*-*-*/
	private Integer licitacao;
	private Short complemento;
	private Integer codVerba;
	private String  descVerba;
	private Integer codGrupoNatureza;
	private Byte codNatureza;
	private String  ntdDescricao;

	/*-*-*-* Construtores *-*-*-*/
	public PagamentosRealizadosPeriodoPdfSubLicitacoesVO() {
		super();
	}

	/*-*-*-* Getters e Setters *-*-*-*/
	public Integer getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(Integer licitacao) {
		this.licitacao = licitacao;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Integer getCodVerba() {
		return codVerba;
	}

	public void setCodVerba(Integer codVerba) {
		this.codVerba = codVerba;
	}

	public String getDescVerba() {
		return descVerba;
	}

	public void setDescVerba(String descVerba) {
		this.descVerba = descVerba;
	}

	public Integer getCodGrupoNatureza() {
		return codGrupoNatureza;
	}

	public void setCodGrupoNatureza(Integer codGrupoNatureza) {
		this.codGrupoNatureza = codGrupoNatureza;
	}

	public Byte getCodNatureza() {
		return codNatureza;
	}

	public void setCodNatureza(Byte codNatureza) {
		this.codNatureza = codNatureza;
	}

	public String getNtdDescricao() {
		return ntdDescricao;
	}

	public void setNtdDescricao(String ntdDescricao) {
		this.ntdDescricao = ntdDescricao;
	}
}
