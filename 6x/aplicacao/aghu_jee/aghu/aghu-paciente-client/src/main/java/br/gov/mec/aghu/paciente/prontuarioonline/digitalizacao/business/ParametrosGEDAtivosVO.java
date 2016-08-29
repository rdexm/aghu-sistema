package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class ParametrosGEDAtivosVO extends ParametrosGEDVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1246134572356878L;

	private String origem;
	private Date data;
	private String tipo;
	private Integer codigoEvento;

	public ParametrosGEDAtivosVO() {
	}

	public ParametrosGEDAtivosVO(Integer pacCodigo, String prontuario, String nome, String origem, Date data, String tipo,
			Integer codigoEvento, String usuarioLogado) {
		super(pacCodigo, prontuario, nome, usuarioLogado);
		this.setOrigem(origem);
		this.setData(data);
		this.setTipo(tipo);
		this.setCodigoEvento(codigoEvento);
	}

	@Override
	public String getCamposFicha() {
		StringBuffer campos = new StringBuffer();
		if (StringUtils.isNotBlank(getProntuario())) {
			campos.append(getProntuario());
			campos.append(_TAG_NUMERICO);
		}
		campos.append(';');
		if (StringUtils.isNotBlank(getNome())) {
			campos.append(getNome());
		}
		campos.append(';');
		if (getData() != null) {
			campos.append(getData());
			campos.append(_TAG_DATA);
		}
		campos.append(';');
		if (getCodigoEvento() != null) {
			campos.append(getCodigoEvento());
			campos.append(_TAG_NUMERICO);
		}
		campos.append(';');
		if (StringUtils.isNotBlank(getOrigem())) {
			campos.append(getOrigem());
		}
		campos.append(';');
		if (StringUtils.isNotBlank(getTipo())) {
			campos.append(getTipo());
		}
		return campos.toString();
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getCodigoEvento() {
		return codigoEvento;
	}

	public void setCodigoEvento(Integer codigoEvento) {
		this.codigoEvento = codigoEvento;
	}

}