package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.ScoLicitacao;

public class ListaLicitacaoVO implements Serializable {
	
	private ScoLicitacao licitacao;
	private Boolean bTemSiasg; 
	private String toolTipSemSiasg;
	private String toolTipComSiasg;
	
	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public Boolean getbTemSiasg() {
		return bTemSiasg;
	}

	public void setbTemSiasg(Boolean bTemSiasg) {
		this.bTemSiasg = bTemSiasg;
	}
	
	public String getToolTipSemSiasg() {
		return toolTipSemSiasg;
	}

	public void setToolTipSemSiasg(String toolTipSemSiasg) {
		this.toolTipSemSiasg = toolTipSemSiasg;
	}

	public String getToolTipComSiasg() {
		return toolTipComSiasg;
	}

	public void setToolTipComSiasg(String toolTipComSiasg) {
		this.toolTipComSiasg = toolTipComSiasg;
	}

	public enum Fields {
		TEMSIASG("bTemSiasg"),		
		LICITACAO("licitacao"),
		TOOLTIPSEMSIASG("toolTipSemSiasg"),
		TOOLTIPCOMSIASG("toolTipComSiasg");
		
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
