package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketsVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6380798975171814074L;

	private Integer seq;	
	private Integer status;
	private Date dataCriacao;
	private List<ResponsaveisStatusTicketsVO> listaResponsaveis = new ArrayList<ResponsaveisStatusTicketsVO>(); 

	public enum Fields {
		
		SEQ("seq"),
		STATUS("status"),
		DATA_CRIACAO("dataCriacao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public List<ResponsaveisStatusTicketsVO> getListaResponsaveis() {
		return listaResponsaveis;
	}

	public void setListaResponsaveis(
			List<ResponsaveisStatusTicketsVO> listaResponsaveis) {
		this.listaResponsaveis = listaResponsaveis;
	}	
}