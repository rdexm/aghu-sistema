package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;



public class ExameNotificacaoVO implements BaseBean {

	private static final long serialVersionUID = 5320291018047174058L;

	private String sigla;
	private Integer manSeq;
	private String exame;
	private String materialAnalise;
	private Integer codigo;
	private String campoLaudo;
	private DominioSituacao situacao;
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getExame() {
		return exame;
	}
	public void setExame(String exame) {
		this.exame = exame;
	}
	public String getMaterialAnalise() {
		return materialAnalise;
	}
	public void setMaterialAnalise(String materialAnalise) {
		this.materialAnalise = materialAnalise;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getCampoLaudo() {
		return campoLaudo;
	}
	public void setCampoLaudo(String campoLaudo) {
		this.campoLaudo = campoLaudo;
	}
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	
	
	public Integer getManSeq() {
		return manSeq;
	}
	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}



	public enum Fields {

		SIGLA("sigla"),
		MAN_SEQ("manSeq"),
		EXAME("exame"),
		MATERIAL_ANALISE("materialAnalise"),
		CODIGO("codigo"),
		CAMPO_LAUDO("campoLaudo"),
		SITUACAO("situacao");

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
