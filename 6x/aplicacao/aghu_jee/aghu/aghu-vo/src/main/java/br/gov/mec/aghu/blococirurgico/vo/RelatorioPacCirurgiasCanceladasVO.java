package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;

public class RelatorioPacCirurgiasCanceladasVO {
	
	private Integer   crgSeq;
	private Short   unidade;
	private String  motivo;
	private Date    dataCirurg;
	private Date  	dataCancel;
	private Short  sala;
	private String  nomePac;
	private String  complemento;
	private String  canceladoPor;
	private String  siglaEspecialidade;
	private String  nomeUsual;
	private String  nome;
	private String 	procedimentoPrincipal;
	private DominioTipoProcedimentoCirurgico  tipo;
	private String  dataAtend;
	private String 	convenio;
	private Integer pacCodigo;
	private Date  	data;
	
	public enum Fields {
		CRG_SEQ("crgSeq"),
		UNF_SEQ("unidade"),
	    MOTIVO_CANC("motivo"),
	    DT_CANCEL("dataCancel"),
	    DT_CIRURGIA("dataCirurg"),
	    SALA("sala"),
	    PAC_NOME("nomePac"),
	    COMPLEMENTO_CANC("complemento"),
	    CANCELADO_POR("canceladoPor"),
	    ESP_SIGLA("siglaEspecialidade"),
	    NOME("nome"),
	    NOME_USUAL("nomeUsual"),
	    PROCEDIMENTO_PRINCIPAL("procedimentoPrincipal"),
	    TIPO("tipo"),
	    PAC_CODIGO("pacCodigo"),
	    DATA("data"),
	    CONV_PAGADOR("convenio");
	    ;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public Short getUnidade() {
		return unidade;
	}


	public void setUnidade(Short unidade) {
		this.unidade = unidade;
	}


	public String getMotivo() {
		return motivo;
	}


	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}


	public Date getDataCirurg() {
		return dataCirurg;
	}


	public void setDataCirurg(Date dataCirurg) {
		this.dataCirurg = dataCirurg;
	}


	public Date getDataCancel() {
		return dataCancel;
	}


	public void setDataCancel(Date dataCancel) {
		this.dataCancel = dataCancel;
	}


	public Short getSala() {
		return sala;
	}


	public void setSala(Short sala) {
		this.sala = sala;
	}


	public String getNomePac() {
		return nomePac;
	}


	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}


	public String getComplemento() {
		return complemento;
	}


	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}


	public String getCanceladoPor() {
		return canceladoPor;
	}


	public void setCanceladoPor(String canceladoPor) {
		this.canceladoPor = canceladoPor;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}


	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}


	public String getNomeUsual() {
		return nomeUsual;
	}


	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getProcedimentoPrincipal() {
		return procedimentoPrincipal;
	}


	public void setProcedimentoPrincipal(String procedimentoPrincipal) {
		this.procedimentoPrincipal = procedimentoPrincipal;
	}


	public String getDataAtend() {
		return dataAtend;
	}


	public void setDataAtend(String dataAtend) {
		this.dataAtend = dataAtend;
	}


	public String getConvenio() {
		return convenio;
	}


	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}


	public DominioTipoProcedimentoCirurgico getTipo() {
		return tipo;
	}


	public void setTipo(DominioTipoProcedimentoCirurgico tipo) {
		this.tipo = tipo;
	}


	public Integer getPacCodigo() {
		return pacCodigo;
	}


	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}


	public Date getData() {
		return data;
	}


	public void setData(Date data) {
		this.data = data;
	}


	public Integer getCrgSeq() {
		return crgSeq;
	}


	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}


	
//	@Override
//	public String toString() {
//		return "MbcRelatCirurRealizPorEspecEProfVO [seqEspecialidade="
//				+ seqEspecialidade + ", especialidade=" + especialidade
//				+ ", nomePessoaFisica=" + nomePessoaFisica
//				+ ", nomeUsualPessoaFisica=" + nomeUsualPessoaFisica
//				+ ", cirurgiao=" + cirurgiao + ", seqCirurgia=" + seqCirurgia
//				+ ", nomePaciente=" + nomePaciente + ", prontuario="
//				+ prontuario + ", prontuarioFormatado=" + prontuarioFormatado
//				+ ", dataInicioCirurgia=" + dataInicioCirurgia
//				+ ", numeroAgenda=" + numeroAgenda + ", convenio=" + convenio
//				+ ", procedimento=" + procedimento + ", clinica=" + clinica
//				+ ", listaCirurgioes=" + listaCirurgioes + "]";
//	}

}
