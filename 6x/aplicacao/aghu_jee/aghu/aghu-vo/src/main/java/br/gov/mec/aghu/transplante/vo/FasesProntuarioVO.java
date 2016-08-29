package br.gov.mec.aghu.transplante.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;



public class FasesProntuarioVO {
	
	private DominioSituacaoTransplante situacao;
	private Integer transplanteSeq;
	private Date dataOcorrencia;
	
	
	public DominioSituacaoTransplante getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoTransplante situacao) {
		this.situacao = situacao;
	}
	public Integer getTransplanteSeq() {
		return transplanteSeq;
	}
	public void setTransplanteSeq(Integer transplanteSeq) {
		this.transplanteSeq = transplanteSeq;
	}
	public Date getDataOcorrencia() {
		return dataOcorrencia;
	}
	public void setDataOcorrencia(Date dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}
	
	public enum Fields{
		SITUACAO("situacao"),
		TRANSPLANTE_SEQ("transplanteSeq"),
		DT_OCORRENCIA("dataOcorrencia");
		
		private String fields;
		
		private Fields(String s){
			this.fields = s;
		}
		
		@Override
		public String toString(){
			return this.fields;
		}
	}
}
