package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import org.apache.commons.lang3.StringUtils;

public class ParametrosGEDInativosVO extends ParametrosGEDVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1654546735461734154L;

	private String nomeMae;
	private String rg;
	private String cpf;
	private String codBarras;

	public ParametrosGEDInativosVO() {
	}

	public ParametrosGEDInativosVO(Integer pacCodigo, String nome, String prontuario, String nomeMae, String rg, String cpf,
			String codBarras, String usuarioLogado) {
		super(pacCodigo, prontuario, nome, usuarioLogado);
		this.nomeMae = nomeMae;
		this.rg = rg;
		this.cpf = cpf;
		this.codBarras = codBarras;
	}

	@Override
	public String getCamposFicha() {
		StringBuffer campos = new StringBuffer();
		if (StringUtils.isNotBlank(getNome())) {
			campos.append(getNome());
		}
		campos.append(';');
		if (StringUtils.isNotBlank(getProntuario())) {
			campos.append(getProntuario());
			campos.append(_TAG_NUMERICO);
		}
		campos.append(';');
		if (getNomeMae() != null) {
			campos.append(getNomeMae());
		}
		campos.append(';');
		if (StringUtils.isNotBlank(getRg())) {
			campos.append(getRg());
		}
		campos.append(';');
		if (StringUtils.isNotBlank(getCpf())) {
			campos.append(getCpf());
		}
		campos.append(';');
		if (StringUtils.isNotBlank(getCodBarras())) {
			campos.append(getCodBarras());
		}
		return campos.toString();
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getCodBarras() {
		return codBarras;
	}

	public void setCodBarras(String codBarras) {
		this.codBarras = codBarras;
	}

}