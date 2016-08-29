package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class RelatorioProfissionaisUnidadeCirurgicaVO implements Serializable{

	private static final long serialVersionUID = -6888112874668516297L;
	
	private String ordem;
	private String funcao;
	private String conselho;
	private String nome;
	private Integer serMatricula;
	private Short serCodigo;
	private String sigla;
	
	public RelatorioProfissionaisUnidadeCirurgicaVO() {
		super();
	}
	
	public RelatorioProfissionaisUnidadeCirurgicaVO(String ordem,
											String funcao,
											String nome,
											Integer serMatricula,
											Short serCodigo,
											String sigla){
		super();
		this.ordem = ordem;
		this.funcao = funcao;
		this.nome = nome;
		this.serMatricula = serMatricula;
		this.serCodigo = serCodigo;
		this.sigla = sigla;
	}
	
	public enum Fields {
		
		ORDEM("ordem"),
		FUNCAO("funcao"),
		NOME("nome"),
		SER_MATRICULA("serMatricula"),
		SER_CODIGO("serCodigo"),
		SIGLA("sigla");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	//Getters and Setters
	
	public String getOrdem() {
		return ordem;
	}

	public void setOrdem(String ordem) {
		this.ordem = ordem;
	}

	public String getFuncao() {
		return funcao;
	}

	public void setFuncao(String funcao) {
		this.funcao = funcao;
	}

	public String getConselho() {
		return conselho;
	}

	public void setConselho(String conselho) {
		this.conselho = conselho;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerCodigo() {
		return serCodigo;
	}

	public void setSerCodigo(Short serCodigo) {
		this.serCodigo = serCodigo;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
}
