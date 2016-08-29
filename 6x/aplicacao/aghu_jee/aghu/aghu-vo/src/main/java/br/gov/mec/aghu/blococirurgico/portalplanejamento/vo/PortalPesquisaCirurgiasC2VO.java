package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import javax.persistence.Transient;

public class PortalPesquisaCirurgiasC2VO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7482889145371378355L;
	
	private String nomeUsual;
	private String nome;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short unfSeq;
	private DominioFuncaoProfissional indFuncaoProf;
	
	public enum Fields {

		NOME_USUAL("nomeUsual"),
		NOME("nome"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		UNF_SEQ("unfSeq"),
		IND_FUNCAO_PROF("indFuncaoProf");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getNomeUsual() {
		return nomeUsual;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
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

	public DominioFuncaoProfissional getIndFuncaoProf() {
		return indFuncaoProf;
	}

	public void setIndFuncaoProf(DominioFuncaoProfissional indFuncaoProf) {
		this.indFuncaoProf = indFuncaoProf;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}
	
	@Transient
	public String getMatriculaVinculo() {
		return this.getSerVinCodigo() + "  " + this.getSerMatricula();
	}	

}