package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class AIHFaturadaPorPacienteVO {

	// DCI.CODIGO_DCIH DCIH
	private String codigodcih;

	// CLC.CODIGO COD_CLI
	private Integer codigo;

	// CLC.DESCRICAO DESC_CLI
	private String descricao;

	// EAI.PAC_PRONTUARIO PRONTUARIO
	private Integer prontuario;

	// EAI.PAC_NOME PAC_NOME
	private String pacnome;

	// CTH.SEQ CONTA
	private Integer cthseq;

	// CTH.DT_INT_ADMINISTRATIVA DT_INT
	private Date datainternacaoadministrativa;

	// CTH.DT_ALTA_ADMINISTRATIVA DT_ALTA
	private Date dtaltaadministrativa;

	// CTH.NRO_AIH AIH
	private Long nroaih;

	// EAI.IPH_COD_SUS_REALIZ SSM_REALIZ
	private Long iphcodsusrealiz;
	
	private Integer totAihs;

	public String getCodigodcih() {
		return codigodcih;
	}

	public void setCodigodcih(String codigodcih) {
		this.codigodcih = codigodcih;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getPacnome() {
		return pacnome;
	}

	public void setPacnome(String pacnome) {
		this.pacnome = pacnome;
	}

	public Integer getCthseq() {
		return cthseq;
	}

	public void setCthseq(Integer cthseq) {
		this.cthseq = cthseq;
	}

	public Date getDatainternacaoadministrativa() {
		return datainternacaoadministrativa;
	}

	public void setDatainternacaoadministrativa(
			Date datainternacaoadministrativa) {
		this.datainternacaoadministrativa = datainternacaoadministrativa;
	}

	public Date getDtaltaadministrativa() {
		return dtaltaadministrativa;
	}

	public void setDtaltaadministrativa(Date dtaltaadministrativa) {
		this.dtaltaadministrativa = dtaltaadministrativa;
	}

	public Long getNroaih() {
		return nroaih;
	}

	public void setNroaih(Long nroaih) {
		this.nroaih = nroaih;
	}

	public Long getIphcodsusrealiz() {
		return iphcodsusrealiz;
	}

	public void setIphcodsusrealiz(Long iphcodsusrealiz) {
		this.iphcodsusrealiz = iphcodsusrealiz;
	}

	public Integer getTotAihs() {
		return totAihs;
	}

	public void setTotAihs(Integer totAihs) {
		this.totAihs = totAihs;
	}

}