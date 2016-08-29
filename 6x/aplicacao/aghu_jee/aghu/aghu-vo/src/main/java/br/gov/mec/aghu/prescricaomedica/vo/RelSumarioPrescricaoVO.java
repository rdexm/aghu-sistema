package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;

public class RelSumarioPrescricaoVO {

	private Date data;	
	private String descricao;
	private String valor;
	private DominioTipoItemPrescricaoSumario tipo;
	private String [] status;
	private Short ordem;
	
	public RelSumarioPrescricaoVO(Date data, String descricao,
			String valor, DominioTipoItemPrescricaoSumario tipo, Short ordem) {	
		super();
		this.data = data;
		this.descricao = descricao;
		this.valor = valor;
		this.tipo = tipo;
		this.ordem = ordem;
		status = new String [15];
	}
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public DominioTipoItemPrescricaoSumario getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoItemPrescricaoSumario tipo) {
		this.tipo = tipo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RelSumarioPrescricaoVO other = (RelSumarioPrescricaoVO) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (descricao == null) {
			if (other.descricao != null) {
				return false;
			}
		} else if (!descricao.equals(other.descricao)) {
			return false;
		}
		if (tipo == null) {
			if (other.tipo != null) {
				return false;
			}
		} else if (!tipo.equals(other.tipo)) {
			return false;
		}
		return true;
	}

	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = (String[])status.clone();
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	
}