package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;

public class DadosAltaSumarioVO {
	

	private Integer seqInternacao;
	private Date dthrInicioAtd;
	private Date dthrAltaAsu;
	private DominioIndTipoAltaSumarios indTipoAsu;
	private Short seqMotivoAltaMedica;
	private String codigoTipoAltaMedica;
	private String descricaoTipoAltaMedica;
	private String complPlanoPosAlta;
	private String descPlanoPosAlta;
	private String descTotalPlanoPosAlta;
	
	public DadosAltaSumarioVO(){
		
	}
	
	public DadosAltaSumarioVO(
			Integer seqInternacao,
			Date dthrInicioAtd,
			Date dthrAltaAsu ,
			DominioIndTipoAltaSumarios indTipoAsu,
			Short seqMotivoAltaMedica,
			String descricaoTipoAltaMedica,
			String codigoTipoAltaMedica, 
			String complPlanoPosAlta, 
			String descPlanoPosAlta	){
		
		this.seqInternacao = seqInternacao;
		this.dthrInicioAtd = dthrInicioAtd;
		this.dthrAltaAsu = dthrAltaAsu;
		this.indTipoAsu = indTipoAsu;
		this.seqMotivoAltaMedica = seqMotivoAltaMedica;
		this.codigoTipoAltaMedica = codigoTipoAltaMedica;
		this.descricaoTipoAltaMedica = descricaoTipoAltaMedica;
		this.complPlanoPosAlta = complPlanoPosAlta;
		this.descPlanoPosAlta= descPlanoPosAlta;
	}


	//GETTERs e SETTERs
	public Integer getSeqInternacao() {
		return seqInternacao;
	}


	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}


	public Date getDthrInicioAtd() {
		return dthrInicioAtd;
	}


	public void setDthrInicioAtd(Date dthrInicioAtd) {
		this.dthrInicioAtd = dthrInicioAtd;
	}


	public Date getDthrAltaAsu() {
		return dthrAltaAsu;
	}


	public void setDthrAltaAsu(Date dthrAltaAsu) {
		this.dthrAltaAsu = dthrAltaAsu;
	}


	public DominioIndTipoAltaSumarios getIndTipoAsu() {
		return indTipoAsu;
	}


	public void setIndTipoAsu(DominioIndTipoAltaSumarios indTipoAsu) {
		this.indTipoAsu = indTipoAsu;
	}


	public Short getSeqMotivoAltaMedica() {
		return seqMotivoAltaMedica;
	}


	public void setSeqMotivoAltaMedica(Short seqMotivoAltaMedica) {
		this.seqMotivoAltaMedica = seqMotivoAltaMedica;
	}


	public String getCodigoTipoAltaMedica() {
		return codigoTipoAltaMedica;
	}


	public void setCodigoTipoAltaMedica(String codigoTipoAltaMedica) {
		this.codigoTipoAltaMedica = codigoTipoAltaMedica;
	}


	public String getDescricaoTipoAltaMedica() {
		return descricaoTipoAltaMedica;
	}


	public void setDescricaoTipoAltaMedica(String descricaoTipoAltaMedica) {
		this.descricaoTipoAltaMedica = descricaoTipoAltaMedica;
	}


	public String getComplPlanoPosAlta() {
		return complPlanoPosAlta;
	}


	public void setComplPlanoPosAlta(String complPlanoPosAlta) {
		this.complPlanoPosAlta = complPlanoPosAlta;
	}


	public String getDescPlanoPosAlta() {
		return descPlanoPosAlta;
	}


	public void setDescPlanoPosAlta(String descPlanoPosAlta) {
		this.descPlanoPosAlta = descPlanoPosAlta;
	}


	public String getDescTotalPlanoPosAlta() {
		if(StringUtils.isBlank(descricaoTipoAltaMedica)){
			if(StringUtils.isBlank(this.complPlanoPosAlta)){
				this.descTotalPlanoPosAlta = this.descPlanoPosAlta; 
			}else{
				this.descTotalPlanoPosAlta = this.descPlanoPosAlta + " - " + this.complPlanoPosAlta; 
			}
			return this.descTotalPlanoPosAlta.substring(0, 60);
		}else{
			return this.descricaoTipoAltaMedica;
			
		}
	}


	public void setDescTotalPlanoPosAlta(String descTotalPlanoPosAlta) {
		this.descTotalPlanoPosAlta = descTotalPlanoPosAlta;
	}
	
	
}
