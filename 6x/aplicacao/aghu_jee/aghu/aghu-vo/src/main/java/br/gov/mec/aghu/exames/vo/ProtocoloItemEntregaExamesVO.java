package br.gov.mec.aghu.exames.vo;

public class ProtocoloItemEntregaExamesVO implements Comparable<ProtocoloItemEntregaExamesVO>{

	private String solicitacao;
	private String nomePaciente;
	private String situacao;
	private String descricaoExame;
	private String solicitante;
	private String unidSolic;
	private String convenio;
	
	
	@Override
	public int compareTo(ProtocoloItemEntregaExamesVO other) {
		int result = this.getSolicitacao().compareTo(
				other.getSolicitacao());
		return result;
	}



	public String getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getDescricaoExame() {
		return descricaoExame;
	}

	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}

	public String getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}

	public String getUnidSolic() {
		return unidSolic;
	}

	public void setUnidSolic(String unidSolic) {
		this.unidSolic = unidSolic;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((solicitacao == null) ? 0 : solicitacao.hashCode());
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
		ProtocoloItemEntregaExamesVO other = (ProtocoloItemEntregaExamesVO) obj;
		if (solicitacao == null) {
			if (other.solicitacao != null) {
				return false;
			}
		} else {
			if (!solicitacao.equals(other.solicitacao)) {
				return false;
			}
		}
		return true;
	}
	
	

}
