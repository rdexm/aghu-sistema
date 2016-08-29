package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;

public class GerarExtratoListaTransplantesVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7262694428904238943L;
	
	private Date dataSitucao;
	private DominioSituacaoTransplante situacao;
	private String motivo;
	private String responsavel;
	
	public enum Fields {
		DATA_SITUCAO("dataSitucao"),
		SITUACAO("situacao"),
		MOTIVO("motivo"), 
		RESPONSAVEL("responsavel");
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	//Getter e Setters
	public Date getDataSitucao() {
		return dataSitucao;
	}
	public void setDataSitucao(Date dataSitucao) {
		this.dataSitucao = dataSitucao;
	}
	public DominioSituacaoTransplante getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoTransplante situacao) {
		this.situacao = situacao;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

}
