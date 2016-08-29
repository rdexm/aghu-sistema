package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;


public class ArvoreExameLaboratorioServicosVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3420619434025759315L;
	private String descUnidadeFuncional;
	private Short ufeUnfSeq;
	private Date data;
	private Short ordemNivel1;
	private Short ordemNivel2;
	private String descUsualExame;
	private String descMaterialAnalise;
	private Integer soeSeq;
	private Short seqp;
	
	public String getDescUnidadeFuncional() {
		return descUnidadeFuncional;
	}
	public void setDescUnidadeFuncional(String descUnidadeFuncional) {
		this.descUnidadeFuncional = descUnidadeFuncional;
	}
	public Short getUfeUnfSeq() {
		return ufeUnfSeq;
	}
	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Short getOrdemNivel1() {
		return ordemNivel1;
	}
	public void setOrdemNivel1(Short ordemNivel1) {
		this.ordemNivel1 = ordemNivel1;
	}
	public Short getOrdemNivel2() {
		return ordemNivel2;
	}
	public void setOrdemNivel2(Short ordemNivel2) {
		this.ordemNivel2 = ordemNivel2;
	}
	public String getDescUsualExame() {
		return descUsualExame;
	}
	public void setDescUsualExame(String descUsualExame) {
		this.descUsualExame = descUsualExame;
	}
	public String getDescMaterialAnalise() {
		return descMaterialAnalise;
	}
	public void setDescMaterialAnalise(String descMaterialAnalise) {
		this.descMaterialAnalise = descMaterialAnalise;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

}
