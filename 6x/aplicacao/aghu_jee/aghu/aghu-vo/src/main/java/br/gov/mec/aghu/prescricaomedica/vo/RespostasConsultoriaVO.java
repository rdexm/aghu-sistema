package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioFinalizacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class RespostasConsultoriaVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 999896765402806007L;
	private Date criadoEm; 						// 1
	private Short ordemVisualizacao; 			// 2
	private Short trcSeq; 						// 3
	private String descricao; 					// 5
	private DominioFinalizacao indFinalizacao; 	// 6
	private String nome;						// 7
	private String tipo; 						// 8
	private Integer serMatricula;
	private Short serVinCodigo;

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Short getOrdemVisualizacao() {
		return ordemVisualizacao;
	}

	public void setOrdemVisualizacao(Short ordemVisualizacao) {
		this.ordemVisualizacao = ordemVisualizacao;
	}

	public Short getTrcSeq() {
		return trcSeq;
	}

	public void setTrcSeq(Short trcSeq) {
		this.trcSeq = trcSeq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioFinalizacao getIndFinalizacao() {
		return indFinalizacao;
	}

	public void setIndFinalizacao(DominioFinalizacao indFinalizacao) {
		this.indFinalizacao = indFinalizacao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public enum Fields {
		CRIADO_EM("criadoEm"), 
		DESCRICAO("descricao"), 
		IND_FINALIZACAO("indFinalizacao"),
		NOME("nome"),
		ORDEM_VISUALIZACAO("ordemVisualizacao"), 
		TIPO("tipo"), 
		TRC_SEQ("trcSeq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
