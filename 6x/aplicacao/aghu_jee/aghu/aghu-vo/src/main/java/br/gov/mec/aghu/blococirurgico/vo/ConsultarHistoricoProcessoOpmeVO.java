package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAcaoHistoricoWF;

public class ConsultarHistoricoProcessoOpmeVO implements Serializable{

	private Date dataRegistro;
	private String descricaoEtapa;
	private String nomePessoaFisica;
	private DominioAcaoHistoricoWF acao;
	private String justificativa;
	private String observacao;
	
	public enum Fields {

		DATA_REGISTRO("dataRegistro"),
		DESCRICAO_ETAPA("descricaoEtapa"),
		NOME_PESSOA_FISICA("nomePessoaFisica"),
		ACAO("acao"),
		JUSTIFICATIVA("justificativa"),
		OBSERVACAO("observacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public String getDescricaoEtapa() {
		return descricaoEtapa;
	}

	public void setDescricaoEtapa(String descricaoEtapa) {
		this.descricaoEtapa = descricaoEtapa;
	}

	public String getNomePessoaFisica() {
		return nomePessoaFisica;
	}

	public void setNomePessoaFisica(String nomePessoaFisica) {
		this.nomePessoaFisica = nomePessoaFisica;
	}

	public DominioAcaoHistoricoWF getAcao() {
		return acao;
	}

	public void setAcao(DominioAcaoHistoricoWF acao) {
		this.acao = acao;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(acao == null){
			result = prime * result + 0;	
		}else{
			result = prime * result + acao.hashCode();
		}
		if(dataRegistro == null){
			result = prime * result + 0;	
		}else{
			result = prime * result + dataRegistro.hashCode();	
		}
		if(nomePessoaFisica == null){
			result = prime * result	+ 0;
		}else{
			result = prime * result + nomePessoaFisica.hashCode();
		}
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
		if (!(obj instanceof ConsultarHistoricoProcessoOpmeVO)) {
			return false;
		}
		ConsultarHistoricoProcessoOpmeVO other = (ConsultarHistoricoProcessoOpmeVO) obj;
		if (acao != other.acao) {
			return false;
		}
		if (dataRegistro == null) {
			if (other.dataRegistro != null) {
				return false;
			}
		} else if (!dataRegistro.equals(other.dataRegistro)) {
			return false;
		}
		if (nomePessoaFisica == null) {
			if (other.nomePessoaFisica != null) {
				return false;
			}
		} else if (!nomePessoaFisica.equals(other.nomePessoaFisica)) {
			return false;
		}
		return true;
	}

	
	
	

}
