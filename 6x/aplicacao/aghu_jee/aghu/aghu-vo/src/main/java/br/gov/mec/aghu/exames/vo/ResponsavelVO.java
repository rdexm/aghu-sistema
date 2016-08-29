package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.model.AghResponsavel;
import br.gov.mec.aghu.core.commons.BaseBean;



public class ResponsavelVO  implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8058821905355590734L;;
	
	private Integer seq;
    public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	private String documento;
	private String nome;
	private AghResponsavel aghResponsavel;
	
	public AghResponsavel getAghResponsavel() {
		return aghResponsavel;
	}
	public void setAghResponsavel(AghResponsavel aghResponsavel) {
		this.aghResponsavel = aghResponsavel;
	}
	

	
}
