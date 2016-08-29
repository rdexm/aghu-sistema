package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

public class InfoProcdCirgRequisicaoOPMEVO implements Serializable{
	
	private static final long serialVersionUID = 1014795366885783631L;

	private Date dataRequisicao;
	private Integer requerente;
	private String nomeRequerente;
	private Short especialidade;
	private Integer espCentroCusto;
	private String procedimentoPrincipal;
	private String procedimentoSUS;
	private Date dataProcedimento;
	private Short agenda;
	private Short unidade;
	private Integer equipe;
	private String nomePaciente;
	private Integer prontuario;
	private Short planoConvenio;
	private String convenioDescricao;
	private Byte convenioSaudePlanoSeq;
	private String fatConvSaudePlanoDesc;
	private String observacoesGerais;
	private String justificativaRequisicaoOpme;
	
	private String nomeEspecialidade;
	
	private String strProntuario;
	private Integer codTabela;
	private Boolean indAutorizado;

	public Date getDataRequisicao() {
		return dataRequisicao;
	}

	public void setDataRequisicao(Date dataRequisicao) {
		this.dataRequisicao = dataRequisicao;
	}

	public Integer getRequerente() {
		return requerente;
	}

	public void setRequerente(Integer requerente) {
		this.requerente = requerente;
	}

	public String getNomeRequerente() {
		return nomeRequerente;
	}

	public void setNomeRequerente(String nomeRequerente) {
		this.nomeRequerente = nomeRequerente;
	}

	public Short getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Short especialidade) {
		this.especialidade = especialidade;
	}

	public Integer getEspCentroCusto() {
		return espCentroCusto;
	}

	public void setEspCentroCusto(Integer espCentroCusto) {
		this.espCentroCusto = espCentroCusto;
	}

	public String getProcedimentoPrincipal() {
		return procedimentoPrincipal;
	}

	public void setProcedimentoPrincipal(String procedimentoPrincipal) {
		this.procedimentoPrincipal = procedimentoPrincipal;
	}

	public String getProcedimentoSUS() {
		return procedimentoSUS;
	}

	public void setProcedimentoSUS(String procedimentoSUS) {
		this.procedimentoSUS = procedimentoSUS;
	}

	public Date getDataProcedimento() {
		return dataProcedimento;
	}

	public void setDataProcedimento(Date dataProcedimento) {
		this.dataProcedimento = dataProcedimento;
	}

	public Short getAgenda() {
		return agenda;
	}

	public void setAgenda(Short agenda) {
		this.agenda = agenda;
	}

	public Short getUnidade() {
		return unidade;
	}

	public void setUnidade(Short unidade) {
		this.unidade = unidade;
	}

	public Integer getEquipe() {
		return equipe;
	}

	public void setEquipe(Integer equipe) {
		this.equipe = equipe;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Short getPlanoConvenio() {
		return planoConvenio;
	}

	public void setPlanoConvenio(Short planoConvenio) {
		this.planoConvenio = planoConvenio;
	}

	public String getConvenioDescricao() {
		return convenioDescricao;
	}

	public void setConvenioDescricao(String convenioDescricao) {
		this.convenioDescricao = convenioDescricao;
	}

	public Byte getConvenioSaudePlanoSeq() {
		return convenioSaudePlanoSeq;
	}

	public void setConvenioSaudePlanoSeq(Byte convenioSaudePlanoSeq) {
		this.convenioSaudePlanoSeq = convenioSaudePlanoSeq;
	}

	public String getFatConvSaudePlanoDesc() {
		return fatConvSaudePlanoDesc;
	}

	public void setFatConvSaudePlanoDesc(String fatConvSaudePlanoDesc) {
		this.fatConvSaudePlanoDesc = fatConvSaudePlanoDesc;
	}

	public String getObservacoesGerais() {
		return observacoesGerais;
	}

	public void setObservacoesGerais(String observacoesGerais) {
		this.observacoesGerais = observacoesGerais;
	}

	public String getJustificativaRequisicaoOpme() {
		return justificativaRequisicaoOpme;
	}

	public void setJustificativaRequisicaoOpme(String justificativaRequisicaoOpme) {
		this.justificativaRequisicaoOpme = justificativaRequisicaoOpme;
	}

	public String getStrProntuario() {
		return strProntuario;
	}

	public void setStrProntuario(String strProntuario) {
		this.strProntuario = strProntuario;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	
	public Integer getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Integer codTabela) {
		this.codTabela = codTabela;
	}

	public Boolean getIndAutorizado() {
		return indAutorizado;
	}

	public void setIndAutorizado(Boolean indAutorizado) {
		this.indAutorizado = indAutorizado;
	}



	public enum Fields {
		DATA_REQUISICAO("dataRequisicao"),
		REQUERENTE("requerente"),
		NOME_REQUERENTE("nomeRequerente"),
		ESPECIALIDADE("especialidade"),
		ESP_CENTRO_CUSTO("espCentroCusto"),
		PROCEDIMENTO_PRINCIPAL("procedimentoPrincipal"),
		PROCEDIMENTO_SUS("procedimentoSUS"),
		DATA_PROCEDIMENTO("dataProcedimento"),
		AGENDA("agenda"),
		UNIDADE("unidade"),
		EQUIPE("equipe"),
		NOME_PACIENTE("nomePaciente"),
		PRONTUARIO("prontuario"),
		PLANO_CONVENIO("planoConvenio"),
		CONVENIO_DESCRICAO("convenioDescricao"),
		CONVENIO_SAUDE_PLANO_CSP_SEQ("convenioSaudePlanoSeq"),
		CONV_SAUDE_PLANO_DESC("fatConvSaudePlanoDesc"),
		OBSERVACOES_GERAIS("observacoesGerais"),
		JUST_REQUISICAO_OPME("justificativaRequisicaoOpme"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		IND_AUTORIZADO("indAutorizado"),
		COD_TABELA("codTabela");
		
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
