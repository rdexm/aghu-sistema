package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Date;

public class RelatorioExamesPacienteExamesDetalhesVO implements Serializable {
	
	private static final long serialVersionUID = 7652665886658220628L;
	private String pertenceSumario; //3 - pertence_sumario de Q_SUE2
	private String dataEvento; // 5 - data_evento de Q_SUE2
	private String horaEvento; // 6 - hora_evento de Q_SUE2 
	private Date dataHora;
	private String identificador; // 7 - Exibir retorno de CF_IDENTIFICADORFormula.sql
	private String valor; // 10 - REE_VALOR de Q_SUE2
	private String nome; // 9 - CAL_NOME de Q_SUE2	
	private String grupo; // 11 - grupo_legenda de Q_LEGENDA
	private String numero; // 12 - numero_legenda de Q_LEGENDA
	private String descricaoLegenda; // 13 - descricao2 de Q_LEGENDA
	private String codigo; // 14 - codigo_mensagem de Q_OBSERVACAO
	private String descricaoObservacao; // 15 - descricao4 de Q_OBSERVACAO
	private String rodape; // 16 - Retorno de CF_RODAPEFormula.sql
	private Short ordem;
	
	//campos auxiliares
	private String prontuarioObs;
	private Boolean recemNascidoObs;
	private String ltoLtoId2;
	private String dthrFim2;
	
	private Integer ppePleSeq;
	private Integer ppeSeqp;
	
	/**
	 * 
	 */
	public RelatorioExamesPacienteExamesDetalhesVO() {
	}

	public String getPertenceSumario() {
		return pertenceSumario;
	}
	public void setPertenceSumario(String pertenceSumario) {
		this.pertenceSumario = pertenceSumario;
	}
	public String getDataEvento() {
		return dataEvento;
	}

	public void setDataEvento(String dataEvento) {
		this.dataEvento = dataEvento;
	}

	public String getHoraEvento() {
		return horaEvento;
	}

	public void setHoraEvento(String horaEvento) {
		this.horaEvento = horaEvento;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getDescricaoLegenda() {
		return descricaoLegenda;
	}

	public void setDescricaoLegenda(String descricaoLegenda) {
		this.descricaoLegenda = descricaoLegenda;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricaoObservacao() {
		return descricaoObservacao;
	}

	public void setDescricaoObservacao(String descricaoObservacao) {
		this.descricaoObservacao = descricaoObservacao;
	}

	public String getRodape() {
		return rodape;
	}

	public void setRodape(String rodape) {
		this.rodape = rodape;
	}
	
	public String getProntuarioObs() {
		return prontuarioObs;
	}

	public void setProntuarioObs(String prontuarioObs) {
		this.prontuarioObs = prontuarioObs;
	}

	public Boolean getRecemNascidoObs() {
		return recemNascidoObs;
	}

	public void setRecemNascidoObs(Boolean recemNascidoObs) {
		this.recemNascidoObs = recemNascidoObs;
	}

	public String getLtoLtoId2() {
		return ltoLtoId2;
	}

	public void setLtoLtoId2(String ltoLtoId2) {
		this.ltoLtoId2 = ltoLtoId2;
	}

	public String getDthrFim2() {
		return dthrFim2;
	}

	public void setDthrFim2(String dthrFim2) {
		this.dthrFim2 = dthrFim2;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	public Integer getPpePleSeq() {
		return ppePleSeq;
	}

	public void setPpePleSeq(Integer ppePleSeq) {
		this.ppePleSeq = ppePleSeq;
	}

	public Integer getPpeSeqp() {
		return ppeSeqp;
	}

	public void setPpeSeqp(Integer ppeSeqp) {
		this.ppeSeqp = ppeSeqp;
	}
}
