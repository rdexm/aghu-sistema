package br.gov.mec.aghu.ambulatorio.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class RelatorioPacientesInterconsultasVO implements BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1521860062601177010L;
	private String pacnome;
	private Integer pacprontuario;
	private String ieoobservacao;
	private Date ieodthrcricao;
	
	public String getPacnome() {
		return pacnome;
	}
	public void setPacnome(String pacnome) {
		this.pacnome = pacnome;
	}
	public Integer getPacprontuario() {
		return pacprontuario;
	}
	public void setPacprontuario(Integer pacprontuario) {
		this.pacprontuario = pacprontuario;
	}
	public String getIeoobservacao() {
		return ieoobservacao;
	}
	public void setIeoobservacao(String ieoobservacao) {
		this.ieoobservacao = ieoobservacao;
	}
	public Date getIeodthrcricao() {
		return ieodthrcricao;
	}
	public void setIeodthrcricao(Date ieodthrcricao) {
		this.ieodthrcricao = ieodthrcricao;
	}
	
	public enum Fields {
		
		IEO_DTHRCRICAO("ieodthrcricao"),
		IEO_OBSERVACAO("ieoobservacao"),
		PAC_PRONTUARIO("pacprontuario"),  
		PAC_NOME("pacnome");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
