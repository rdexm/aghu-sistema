package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import org.apache.commons.lang3.StringUtils;

public class ParametrosGEDAdministrativosVO extends ParametrosGEDVO {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 1246134572356878L;

	private String tipo;
	private Integer ano;
	private Integer mes;
	private Integer codBarras;

	public ParametrosGEDAdministrativosVO() {
	}

	public ParametrosGEDAdministrativosVO(Integer pacCodigo, String prontuario, String nome, String tipo,
			Integer ano, Integer mes, Integer codBarras, String usuarioLogado) {
		super(pacCodigo, prontuario, nome, usuarioLogado);
		this.tipo = tipo;
		this.ano = ano;
		this.mes = mes;
		this.codBarras = codBarras;
	}

	@Override
	public String getCamposFicha() {
		StringBuffer campos = new StringBuffer();
		if (StringUtils.isNotBlank(getProntuario())) {
			campos.append(getProntuario());
			campos.append(_TAG_NUMERICO);
		}
		campos.append(';');
		if (StringUtils.isNotBlank(getTipo())) {
			campos.append(getTipo());
		}
		campos.append(';');
		if (getAno() != null) {
			campos.append(getAno());
			campos.append(_TAG_NUMERICO);
		}
		campos.append(';');
		if (getMes() != null) {
			campos.append(getMes());
			campos.append(_TAG_NUMERICO);
		}
		campos.append(';');
		if (getCodBarras() != null) {
			campos.append(getCodBarras());
		}
		return campos.toString();
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getCodBarras() {
		return codBarras;
	}

	public void setCodBarras(Integer codBarras) {
		this.codBarras = codBarras;
	}

}