package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;

public class ExtratoAlteracoesListaOrgaosVO implements Serializable{

		
	/**
	 * 
	 */
	private static final long serialVersionUID = 3534571393274791573L;
	
	private Date dataSituacao;
	private DominioSituacaoTransplante situacao;
	private String motivo;
	private String responsavel;
	
	public enum Fields {
		DT_SITUACAO("dataSituacao"),
		SITUACAO("situacao"),
		MOTIVO("motivo"),
		RESPONSAVEL("responsavel")
		;
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}

	public Date getDataSituacao() {
		return dataSituacao;
	}

	public void setDataSituacao(Date dataSituacao) {
		this.dataSituacao = dataSituacao;
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
