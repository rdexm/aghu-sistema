package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.dominio.DominioTipoJustificativa;

public class ExtratoSessaoVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6082584897984406090L;
	
	private DominioSituacaoSessao indSituacao;
	private Date criadoEm;
	private String justificativa;
	private String usuarioServidor;	
	private String descricaoJustificativa;
	private String tipoJustificativa;
	
	
	
	public enum Fields {
		INDSITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		JUSTIFICATIVA("justificativa"),
		USUARIO_SERVIDOR("usuarioServidor"),
		DESCRICAO_JUSTIFICATIVA("descricaoJustificativa"),
		TIPO_JUSTIFICATIVA("tipoJustificativa");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public DominioSituacaoSessao getIndSituacao() {
		return indSituacao;
	}


	public void setIndSituacao(DominioSituacaoSessao indSituacao) {
		this.indSituacao = indSituacao;
	}


	public Date getCriadoEm() {
		return criadoEm;
	}


	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	public String getJustificativa() {
		return justificativa;
	}


	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}


	public String getUsuarioServidor() {
		return usuarioServidor;
	}


	public void setUsuarioServidor(String usuarioServidor) {
		this.usuarioServidor = usuarioServidor;
	}


	public String getDescricaoJustificativa() {
		return descricaoJustificativa;
	}


	public void setDescricaoJustificativa(String descricaoJustificativa) {
		this.descricaoJustificativa = descricaoJustificativa;
	}


	public String getTipoJustificativa() {
		return tipoJustificativa;
	}


	public void setTipoJustificativa(String tipoJustificativa) {
		this.tipoJustificativa = tipoJustificativa;
	}
	
	public String getTipoJustificativaDescricao(){
		if(tipoJustificativa != null){			
			return DominioTipoJustificativa.getDescricao(tipoJustificativa);
		}
		return tipoJustificativa;
	}
}
