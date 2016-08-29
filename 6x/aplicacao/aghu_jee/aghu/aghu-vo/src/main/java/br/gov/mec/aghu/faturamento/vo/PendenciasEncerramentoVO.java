package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class PendenciasEncerramentoVO {

	
	private Integer conta;
	private Short itemConta;
	private String erro;
	private String erroTruncado;
	private String paciente;
	private Integer prontuario;
	private String pacienteTruncado;
	
	private Short tabItem1;
	private Integer seqItem1;
	private String descricaoItem1;
	private Long susItem1;
	private Integer hcpaItem1;
	
	private Short tabItem2;
	private Integer seqItem2;
	private String descricaoItem2;
	private Long susItem2;
	private Integer hcpaItem2;
	
	private Short tabRealizado;
	private Integer seqRealizado;
	private String descricaoRealizado;
	private Long susRealizado;
	private Integer hcpaRealizado;
	
	private Short tabSolicitado;
	private Integer seqSolicitado;
	private String descricaoSolicitado;
	private Long susSolicitado;
	private Integer hcpaSolicitado;
	
	private Date dtOperacao;
	private String programa;
	
	private String labelItem1;
	private String labelItem2;
	private String labelRealizado;
	private String labelSolicitado;
	
	
	
	public Integer getConta() {
		return conta;
	}
	public void setConta(Integer conta) {
		this.conta = conta;
	}
	public Short getItemConta() {
		return itemConta;
	}
	public void setItemConta(Short itemConta) {
		this.itemConta = itemConta;
	}
	public String getErro() {
		return erro;
	}
	public void setErro(String erro) {
		this.erro = erro;
	}
	public String getPaciente() {
		return paciente;
	}
	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}
	public Short getTabItem1() {
		return tabItem1;
	}
	public void setTabItem1(Short tabItem1) {
		this.tabItem1 = tabItem1;
	}
	public Integer getSeqItem1() {
		return seqItem1;
	}
	public void setSeqItem1(Integer seqItem1) {
		this.seqItem1 = seqItem1;
	}
	public String getDescricaoItem1() {
		return descricaoItem1;
	}
	public void setDescricaoItem1(String descricaoItem1) {
		this.descricaoItem1 = descricaoItem1;
	}
	public Long getSusItem1() {
		return susItem1;
	}
	public void setSusItem1(Long susItem1) {
		this.susItem1 = susItem1;
	}
	public Integer getHcpaItem1() {
		return hcpaItem1;
	}
	public void setHcpaItem1(Integer hcpaItem1) {
		this.hcpaItem1 = hcpaItem1;
	}
	public Short getTabItem2() {
		return tabItem2;
	}
	public void setTabItem2(Short tabItem2) {
		this.tabItem2 = tabItem2;
	}
	public Integer getSeqItem2() {
		return seqItem2;
	}
	public void setSeqItem2(Integer seqItem2) {
		this.seqItem2 = seqItem2;
	}
	public String getDescricaoItem2() {
		return descricaoItem2;
	}
	public void setDescricaoItem2(String descricaoItem2) {
		this.descricaoItem2 = descricaoItem2;
	}
	public Long getSusItem2() {
		return susItem2;
	}
	public void setSusItem2(Long susItem2) {
		this.susItem2 = susItem2;
	}
	public Integer getHcpaItem2() {
		return hcpaItem2;
	}
	public void setHcpaItem2(Integer hcpaItem2) {
		this.hcpaItem2 = hcpaItem2;
	}
	public Short getTabRealizado() {
		return tabRealizado;
	}
	public void setTabRealizado(Short tabRealizado) {
		this.tabRealizado = tabRealizado;
	}
	public Integer getSeqRealizado() {
		return seqRealizado;
	}
	public void setSeqRealizado(Integer seqRealizado) {
		this.seqRealizado = seqRealizado;
	}
	public String getDescricaoRealizado() {
		return descricaoRealizado;
	}
	public void setDescricaoRealizado(String descricaoRealizado) {
		this.descricaoRealizado = descricaoRealizado;
	}
	public Long getSusRealizado() {
		return susRealizado;
	}
	public void setSusRealizado(Long susRealizado) {
		this.susRealizado = susRealizado;
	}
	public Integer getHcpaRealizado() {
		return hcpaRealizado;
	}
	public void setHcpaRealizado(Integer hcpaRealizado) {
		this.hcpaRealizado = hcpaRealizado;
	}
	public Short getTabSolicitado() {
		return tabSolicitado;
	}
	public void setTabSolicitado(Short tabSolicitado) {
		this.tabSolicitado = tabSolicitado;
	}
	public Integer getSeqSolicitado() {
		return seqSolicitado;
	}
	public void setSeqSolicitado(Integer seqSolicitado) {
		this.seqSolicitado = seqSolicitado;
	}
	public String getDescricaoSolicitado() {
		return descricaoSolicitado;
	}
	public void setDescricaoSolicitado(String descricaoSolicitado) {
		this.descricaoSolicitado = descricaoSolicitado;
	}
	public Long getSusSolicitado() {
		return susSolicitado;
	}
	public void setSusSolicitado(Long susSolicitado) {
		this.susSolicitado = susSolicitado;
	}
	public Integer getHcpaSolicitado() {
		return hcpaSolicitado;
	}
	public void setHcpaSolicitado(Integer hcpaSolicitado) {
		this.hcpaSolicitado = hcpaSolicitado;
	}
	public Date getDtOperacao() {
		return dtOperacao;
	}
	public void setDtOperacao(Date dtOperacao) {
		this.dtOperacao = dtOperacao;
	}
	public String getPrograma() {
		return programa;
	}
	public void setPrograma(String programa) {
		this.programa = programa;
	}
	public String getLabelItem1() {
		return labelItem1;
	}
	public void setLabelItem1(String labelItem1) {
		this.labelItem1 = labelItem1;
	}
	public String getLabelItem2() {
		return labelItem2;
	}
	public void setLabelItem2(String labelItem2) {
		this.labelItem2 = labelItem2;
	}
	public String getLabelRealizado() {
		return labelRealizado;
	}
	public void setLabelRealizado(String labelRealizado) {
		this.labelRealizado = labelRealizado;
	}
	public String getLabelSolicitado() {
		return labelSolicitado;
	}
	public void setLabelSolicitado(String labelSolicitado) {
		this.labelSolicitado = labelSolicitado;
	}
	public String getErroTruncado() {
		return erroTruncado;
	}
	public void setErroTruncado(String erroTruncado) {
		this.erroTruncado = erroTruncado;
	}
	public String getPacienteTruncado() {
		return pacienteTruncado;
	}
	public void setPacienteTruncado(String pacienteTruncado) {
		this.pacienteTruncado = pacienteTruncado;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
}