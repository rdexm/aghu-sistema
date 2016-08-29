package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class MptTipoIntercorrenciaJnVO  implements java.io.Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4471663591501744811L;

	private String descricao;
	
	private DominioSituacao indSituacao;
	
	private Date criadoEm;
	
	private String usuario;
	
	private Integer serVinCodigo;
	
	private Integer vinCodigo;
	
	private String color;
	
	public enum Fields {
		
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		MATRICULA("serVinCodigo"),
		VIN_CODIGO("vinCodigo"),
		USUARIO("usuario");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getUsuario() {
		return usuario;
	}
	
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public Integer getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
}