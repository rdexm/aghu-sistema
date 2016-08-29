package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

public class RelatorioNotificGermeMultirresistenteVO {
	private Boolean indNotificacaoAtiva;
	private String descricaoMaterial;
	private Integer pacCodigo; 
	private Integer prontuarioPaciente;
	private String nomePaciente;
	private String descricaoAntiMicrobiano;
	private String descricaoBacteria;
	private String leitoAtendimento;
	private Boolean indFibroseCistica;
	private Date dataIdentificacao;


	public Boolean getIndNotificacaoAtiva() {
		return indNotificacaoAtiva;
	}
	public void setIndNotificacaoAtiva(Boolean indNotificacaoAtiva) {
		this.indNotificacaoAtiva = indNotificacaoAtiva;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}
	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getDescricaoAntiMicrobiano() {
		return descricaoAntiMicrobiano;
	}
	public void setDescricaoAntiMicrobiano(String descricaoAntiMicrobiano) {
		this.descricaoAntiMicrobiano = descricaoAntiMicrobiano;
	}
	public String getDescricaoBacteria() {
		return descricaoBacteria;
	}
	public void setDescricaoBacteria(String descricaoBacteria) {
		this.descricaoBacteria = descricaoBacteria;
	}
	public String getLeitoAtendimento() {
		return leitoAtendimento;
	}
	public void setLeitoAtendimento(String leitoAtendimento) {
		this.leitoAtendimento = leitoAtendimento;
	}
	public Date getDataIdentificacao() {
		return dataIdentificacao;
	}
	public void setDataIdentificacao(Date dataIdentificacao) {
		this.dataIdentificacao = dataIdentificacao;
	}

	
	public Boolean getIndFibroseCistica() {
		return indFibroseCistica;
	}
	public void setIndFibroseCistica(Boolean indFibroseCistica) {
		this.indFibroseCistica = indFibroseCistica;
	}





	public enum Fields {
		IND_NOTIFICACAO_ATIVA("indNotificacaoAtiva"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		PAC_CODIGO("pacCodigo"),
		PRONTUARIO_PACIENTE("prontuarioPaciente"),
		NOME_PACIENTE("nomePaciente"),
		DESCRICAO_MICROBIANA("descricaoAntiMicrobiano"),
		DESCRICAO_BACTERIA("descricaoBacteria"),
		LEITO_ATENDIMENTO("leitoAtendimento"),
		DATA_IDENTIFICACAO("dataIdentificacao"),
		IND_SITUACAO("situacao"),
		IND_FIBROSE_CISTICA("indFibroseCistica");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
}
