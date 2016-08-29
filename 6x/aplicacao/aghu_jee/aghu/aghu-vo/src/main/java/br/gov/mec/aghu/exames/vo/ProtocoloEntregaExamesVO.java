package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.List;

public class ProtocoloEntregaExamesVO implements Comparable<ProtocoloEntregaExamesVO> , Serializable {

	
	private static final long serialVersionUID = 4350250473071240159L;
	private String prontuario;
	private String nomePaciente;
	private String unidadeSolicitacao;
	private String idade;
	
	private String codigoProtocolo;
	private String dataEntrega;
	private String horarioEntrega;
	private String nomeResponsavelRetirada;
	private String cpf;
	private String usuarioResponsavel;
	
	private List<ProtocoloItemEntregaExamesVO> protocoloItemEntregaExamesVO ;
	
	
	
	@Override
	public int compareTo(ProtocoloEntregaExamesVO other) {
		int result = this.getCodigoProtocolo().compareTo(
				other.getCodigoProtocolo());
		return result;
	}
	
	
	
	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getUnidadeSolicitacao() {
		return unidadeSolicitacao;
	}

	public void setUnidadeSolicitacao(String unidadeSolicitacao) {
		this.unidadeSolicitacao = unidadeSolicitacao;
	}
	
	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(String dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	public String getNomeResponsavelRetirada() {
		return nomeResponsavelRetirada;
	}

	public void setNomeResponsavelRetirada(String nomeResponsavelRetirada) {
		this.nomeResponsavelRetirada = nomeResponsavelRetirada;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getUsuarioResponsavel() {
		return usuarioResponsavel;
	}

	public void setUsuarioResponsavel(String usuarioResponsavel) {
		this.usuarioResponsavel = usuarioResponsavel;
	}

	public String getCodigoProtocolo() {
		return codigoProtocolo;
	}

	public void setCodigoProtocolo(String codigoProtocolo) {
		this.codigoProtocolo = codigoProtocolo;
	}

	public List<ProtocoloItemEntregaExamesVO> getProtocoloItemEntregaExamesVO() {
		return protocoloItemEntregaExamesVO;
	}

	public void setProtocoloItemEntregaExamesVO(
			List<ProtocoloItemEntregaExamesVO> protocoloItemEntregaExamesVO) {
		this.protocoloItemEntregaExamesVO = protocoloItemEntregaExamesVO;
	}

	public String getHorarioEntrega() {
		return horarioEntrega;
	}

	public void setHorarioEntrega(String horarioEntrega) {
		this.horarioEntrega = horarioEntrega;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codigoProtocolo == null) ? 0 : codigoProtocolo.hashCode());
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
		ProtocoloEntregaExamesVO other = (ProtocoloEntregaExamesVO) obj;
		if (codigoProtocolo == null) {
			if (other.codigoProtocolo != null) {
				return false;
			}
		} else {
			if (!codigoProtocolo.equals(other.codigoProtocolo)) {
				return false;
			}
		}
		return true;
	}
	
	

}
