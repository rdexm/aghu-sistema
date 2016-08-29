package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoOrgao;

public class FiltroTransplanteOrgaoVO implements Serializable {
	
	private static final long serialVersionUID = -4140482671508566434L;
	private Integer pacCodigo;
	private Integer prontuario;
	private String nomePaciente;
	private DominioTipoOrgao dominioTipoOrgao;
	private Long rgct;
	private Date dataInclusao;
	private Date dataTransplante;
	private Integer selectTab;
	
	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public Date getDataTransplante() {
		return dataTransplante;
	}

	public void setDataTransplante(Date dataTransplante) {
		this.dataTransplante = dataTransplante;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public DominioTipoOrgao getDominioTipoOrgao() {
		return dominioTipoOrgao;
	}

	public void setDominioTipoOrgao(DominioTipoOrgao dominioTipoOrgao) {
		this.dominioTipoOrgao = dominioTipoOrgao;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public enum Fields {
		PAC_CODIGO("pacCodigo"),
		PRONTUARIO("prontuario"),
		NOME_PACIENTE("nomePaciente"),
		TIPO_ORGAO("dominioTipoOrgao"),
		DATA_INCLUSAO("dataInclusao"),
		DATA_TRANSPLANTE("dataTransplante"),
		;
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}

	public Long getRgct() {
		return rgct;
	}

	public void setRgct(Long rgct) {
		this.rgct = rgct;
	}

	public Integer getSelectTab() {
		return selectTab;
	}

	public void setSelectTab(Integer selectTab) {
		this.selectTab = selectTab;
	}

}
