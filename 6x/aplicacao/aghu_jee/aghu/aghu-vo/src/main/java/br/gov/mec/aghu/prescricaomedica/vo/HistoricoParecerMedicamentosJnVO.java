package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

public class HistoricoParecerMedicamentosJnVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4135142563126576154L;

	private Date dataParecer;
	private String nomeUsuario;
	private String siglaRegistro;
	private String numeroRegistro;
	private String parecer;
	private String observacao;
	private Date dataAtualizacao;
	private DominioOperacoesJournal operacao;
	private String responsavel;
	
	public Date getDataParecer() {
		return dataParecer;
	}
	public void setDataParecer(Date dataParecer) {
		this.dataParecer = dataParecer;
	}
	public String getNomeUsuario() {
		return nomeUsuario;
	}
	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}
	public String getSiglaRegistro() {
		return siglaRegistro;
	}
	public void setSiglaRegistro(String siglaRegistro) {
		this.siglaRegistro = siglaRegistro;
	}
	public String getNumeroRegistro() {
		return numeroRegistro;
	}
	public void setNumeroRegistro(String numeroRegistro) {
		this.numeroRegistro = numeroRegistro;
	}
	public String getParecer() {
		return parecer;
	}
	public void setParecer(String parecer) {
		this.parecer = parecer;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}
	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}
	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public enum Fields{
		DATA_PARECER("dataParecer"),
		NOME_USUARIO("nomeUsuario"),
		SIGLA_REGISTRO("siglaRegistro"),
		NUMERO_REGISTRO("numeroRegistro"),
		PARECER("parecer"),
		OBSERVACAO("observacao"),
		DATA_ATUALIZACAO("dataAtualizacao"),
		OPERACAO("operacao"),
		RESPONSAVEL("responsavel");
		
		private String fields; 
		
		private Fields(String fields){
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return this.fields;
		}
	}
	
}
