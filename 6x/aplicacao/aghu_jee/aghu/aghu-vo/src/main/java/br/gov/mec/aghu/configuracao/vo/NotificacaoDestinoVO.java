package br.gov.mec.aghu.configuracao.vo;

import java.io.Serializable;


/**
 * @author felipe.palma
 * 
 */
public class NotificacaoDestinoVO  implements Serializable {
	
	private static final long serialVersionUID = 7743075115759031448L;
	
	private Integer seq;
	private Integer matriculaContato;
	private Short vinCodigoContato;
	private Short dddCelular;
	private Long celular;
	private Integer ntsSeq;
	private String nomePessoaFisica;
	
	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Integer getMatriculaContato() {
		return matriculaContato;
	}
	public void setMatriculaContato(Integer matriculaContato) {
		this.matriculaContato = matriculaContato;
	}
	public Short getVinCodigoContato() {
		return vinCodigoContato;
	}
	public void setVinCodigoContato(Short vinCodigoContato) {
		this.vinCodigoContato = vinCodigoContato;
	}
	public Short getDddCelular() {
		return dddCelular;
	}
	public void setDddCelular(Short dddCelular) {
		this.dddCelular = dddCelular;
	}
	public Long getCelular() {
		return celular;
	}
	public void setCelular(Long celular) {
		this.celular = celular;
	}
	public Integer getNtsSeq() {
		return ntsSeq;
	}
	public void setNtsSeq(Integer ntsSeq) {
		this.ntsSeq = ntsSeq;
	}
	public String getNomePessoaFisica() {
		return nomePessoaFisica;
	}
	public void setNomePessoaFisica(String nomePessoaFisica) {
		this.nomePessoaFisica = nomePessoaFisica;
	}
	
	public enum Fields {
		SEQ("seq"),
		MATRICULA_CONTATO("matriculaContato"),
		VIN_CODIGO_CONTATO("vinCodigoContato"),
		DDD_CELULAR("dddCelular"),
		CELULAR("celular"),
		NTS_SEQ("ntsSeq"),
		NOME_PESSOA_FISICA("nomePessoaFisica");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return fields;
		}
		
	}

}
