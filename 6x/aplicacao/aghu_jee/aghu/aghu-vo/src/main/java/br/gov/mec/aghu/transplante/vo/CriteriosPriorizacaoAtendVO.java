package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;

public class CriteriosPriorizacaoAtendVO implements Serializable {

	private static final long serialVersionUID = -4875802086330118620L;
	
	private String codigo;
	private Integer seq;
	private Integer cidSeq;

	private String descricao;
	private Integer criticidade;
	private Integer gravidade;
	private DominioSituacao situacao;
	private String diagnostico;
	private Integer coeficiente;
	private String ativo;
	private DominioSituacaoTmo tipoTmo;
	private String status;
	
	public enum Fields {
		CODIGO("codigo"),
		DESCRICAO("descricao"), 
		GRAVIDADE("gravidade"), 
		CRITICIDADE("criticidade"),
		SITUACAO("situacao"),
		SEQ("seq"),
//		CID_SEQ("cidSeq"),
		DIAGNOSTICO("diagnostico"),
		COEFICIENTE("coeficiente"),
		TIPO_TMO("tipoTmo"),
		STATUS("status");
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getCriticidade() {
		return criticidade;
	}
	public void setCriticidade(Integer criticidade) {
		this.criticidade = criticidade;
	}
	public Integer getGravidade() {
		return gravidade;
	}
	public void setGravidade(Integer gravidade) {
		this.gravidade = gravidade;
	}
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	public String getDiagnostico() {
		if(getCodigo() != null && getDescricao() != null){
				this.diagnostico = getCodigo() + " - " + getDescricao();
		}else if (getCodigo() != null && getDescricao() == null){
			this.diagnostico = getCodigo();
		}else if (getDescricao() != null && getCodigo() == null){
			this.diagnostico = getDescricao();
		}else{
			this.diagnostico = StringUtils.EMPTY;
		}
		return diagnostico;
	}
	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}
	
	public Integer getCoeficiente() {
		
		if(getGravidade() != null && getCriticidade() != null){
			this.coeficiente = getGravidade()+getCriticidade();
		}else{
			if(getGravidade() != null && getCriticidade() == null){
				this.coeficiente = getGravidade();
			}
			if(getCriticidade() != null && getGravidade() == null){
				this.coeficiente = getCriticidade();
			}
			if(getGravidade() == null && getCriticidade() == null){
				this.coeficiente = null;
			}			
		}
		
		return coeficiente;
	}
	
	public void setCoeficiente(Integer coeficiente) {
		this.coeficiente = coeficiente;
	}
	
	public String getAtivo(){
		if(getSituacao().isAtivo()){
			this.ativo = "Sim";
		}else{
			this.ativo = "Não";
		}
		 return ativo;
	}
	
	public String getSituacaoTmo() {
		switch (getTipoTmo()) {
		case G:
			return "Alogênico";
		case U:
			return "Autólogo";
		default:
			return "";
		}
	}
	
	public void setAtivo(String ativo) {
		this.ativo = ativo;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Integer getCidSeq() {
		return cidSeq;
	}
	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}
	public DominioSituacaoTmo getTipoTmo() {
		return tipoTmo;
	}
	public void setTipoTmo(DominioSituacaoTmo tipoTmo) {
		this.tipoTmo = tipoTmo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
