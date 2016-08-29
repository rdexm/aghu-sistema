package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoEtapaPac;

public class EtapasRelacionadasPacVO {

	private String descricaoLocProcesso;
	private String descricaoEtapa;
	private DominioSituacaoEtapaPac situacao;
	private String apontamentoUsuario;
	private String nome;
	private Short tempoPrevisto;
	private Date dataApontamento;
	private Integer codigoEtapa;

	public String getDescricaoLocProcesso() {
		return descricaoLocProcesso;
	}

	public void setDescricaoLocProcesso(String descricaoLocProcesso) {
		this.descricaoLocProcesso = descricaoLocProcesso;
	}

	public String getDescricaoEtapa() {
		return descricaoEtapa;
	}

	public void setDescricaoEtapa(String descricaoEtapa) {
		this.descricaoEtapa = descricaoEtapa;
	}

	public DominioSituacaoEtapaPac getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoEtapaPac situacao) {
		this.situacao = situacao;
	}

	public String getApontamentoUsuario() {
		return apontamentoUsuario;
	}

	public void setApontamentoUsuario(String apontamentoUsuario) {
		this.apontamentoUsuario = apontamentoUsuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Short getTempoPrevisto() {
		return tempoPrevisto;
	}

	public void setTempoPrevisto(Short tempoPrevisto) {
		this.tempoPrevisto = tempoPrevisto;
	}

	public Date getDataApontamento() {
		return dataApontamento;
	}

	public void setDataApontamento(Date dataApontamento) {
		this.dataApontamento = dataApontamento;
	}

	public Integer getCodigoEtapa() {
		return codigoEtapa;
	}

	public void setCodigoEtapa(Integer codigoEtapa) {
		this.codigoEtapa = codigoEtapa;
	}
	
    public enum Fields {
		
		DESCRICAO_LOC_PROCESSO("descricaoLocProcesso"),
		DESCRICAO_ETAPA("descricaoEtapa"),
		SITUACAO("situacao"),
		APONTAMENTO_USUARIO("apontamentoUsuario"),
		NOME_USUARIO("nome"),
		TEMPO_PREVISTO("tempoPrevisto"),
		DATA_APONTAMENTO("dataApontamento"),
		CODIGO_ETAPA("codigoEtapa");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
