package br.gov.mec.aghu.exames.patologia.vo;

import java.io.Serializable;

public class ConsultaMaterialApVO implements Serializable {

	private static final long serialVersionUID = 2125420067695143494L;

	private Integer iseSoeSeq;
	
	private Short iseSeqp;
	
	private String descMaterialAnalise;
	
	private String descRegiaoAnatomica;
	
	private Integer codMaterialAnalise;
	
	private String descricaoMaterial;
	
	private String descricaoUsual;
	
	private String descricaoRegiaoAnat;

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public String getDescMaterialAnalise() {
		return descMaterialAnalise;
	}

	public void setDescMaterialAnalise(String descMaterialAnalise) {
		this.descMaterialAnalise = descMaterialAnalise;
	}

	public String getDescRegiaoAnatomica() {
		return descRegiaoAnatomica;
	}

	public void setDescRegiaoAnatomica(String descRegiaoAnatomica) {
		this.descRegiaoAnatomica = descRegiaoAnatomica;
	}

	public Integer getCodMaterialAnalise() {
		return codMaterialAnalise;
	}

	public void setCodMaterialAnalise(Integer codMaterialAnalise) {
		this.codMaterialAnalise = codMaterialAnalise;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}

	public String getDescricaoUsual() {
		return descricaoUsual;
	}

	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}

	public String getDescricaoRegiaoAnat() {
		return descricaoRegiaoAnat;
	}

	public void setDescricaoRegiaoAnat(String descricaoRegiaoAnat) {
		this.descricaoRegiaoAnat = descricaoRegiaoAnat;
	}
	

	
}
