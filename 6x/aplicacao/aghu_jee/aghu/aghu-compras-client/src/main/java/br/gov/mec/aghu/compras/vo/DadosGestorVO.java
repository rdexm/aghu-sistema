package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

public class DadosGestorVO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3278980481601899857L;
	
	private String nomeGestor;
	private Integer matriculaGestor;
	private String mlcCodigo;
	
	public String getNomeGestor() {
		return nomeGestor;
	}
	public void setNomeGestor(String nomeGestor) {
		this.nomeGestor = nomeGestor;
	}
	public Integer getMatriculaGestor() {
		return matriculaGestor;
	}
	public void setMatriculaGestor(Integer matriculaGestor) {
		this.matriculaGestor = matriculaGestor;
	}
	public String getMlcCodigo() {
		return mlcCodigo;
	}
	public void setMlcCodigo(String mlcCodigo) {
		this.mlcCodigo = mlcCodigo;
	}

	public enum Fields{
		
		NOME_GESTOR("nomeGestor"),
		MATRICULA_GESTOR("matriculaGestor"),
		MLC_CODIGO("mlcCodigo");
		
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
