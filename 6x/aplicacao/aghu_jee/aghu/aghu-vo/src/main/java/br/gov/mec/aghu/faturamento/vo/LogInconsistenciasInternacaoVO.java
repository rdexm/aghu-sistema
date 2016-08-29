package br.gov.mec.aghu.faturamento.vo;

import java.sql.Timestamp;
import java.util.Date;


public class LogInconsistenciasInternacaoVO {

	// PAC.NOME NOME_PACIENTE
	private String pacnome;

	// PAC.PRONTUARIO NRO_PRONT
	private Integer prontuario;

	// LER.CTH_SEQ CONTA
	private Integer cthseq;

	// LER.ERRO ERRO
	private String erro;

	// LER.PHI_SEQ_ITEM1
	private Integer phiseqitem1;

	// LER.PHI_SEQ_ITEM2
	private Integer phiseqitem2;

	// IPH1.COD_TABELA COD_SUS1
	private Integer iphseqitem1;

	// IPH1.DESCRICAO DESC_ITEM_SUS1
	private String descsus1;

	// IPH2.COD_TABELA COD_SUS2
	private Integer iphseqitem2;

	// IPH2.DESCRICAO DESC_ITEM_SUS2
	private String descsus2;

	// IPH3.COD_TABELA COD_SUS3
	private Integer iphseqrealizado;

	// IPH3.DESCRICAO DESC_ITEM_REALIZADO
	private String descitemreal;

	// IPH4.COD_TABELA COD_SUS4
	private Integer iphseq;

	// IPH4.DESCRICAO DESC_ITEM_SOLICITADO
	private String descitemsol;

	// CTH.DT_INT_ADMINISTRATIVA DT_INT_ADM
	private Timestamp datainternacaoadministrativa;

	// CTH.DT_ALTA_ADMINISTRATIVA DT_ALT_ADM
	private Timestamp dtaltaadministrativa;

	// CTH.NRO_AIH
	private Long nroaih;

	// TO_CHAR(MSP.CODIGO_SUS)||TO_CHAR(SIA.CODIGO_SUS) MSP_SIA
	private Byte codigosusmsp;

	// TO_CHAR(MSP.CODIGO_SUS)||TO_CHAR(SIA.CODIGO_SUS) MSP_SIA
	private Byte codigosussia;

	// PHI1.DESCRICAO DESC_ITEM_INTERNO1
	private String descricaophi1;

	// PHI2.DESCRICAO DESC_ITEM_INTERNO2
	private String descricaophi2;

	// ICH.UNF_SEQ UNF_SEQ
	private Short unfseq;

	// ICH.DTHR_REALIZADO DATA_UTL
	private Date dthrrealizado;

	// ITE.LTO_LTO_ID AS LEIT
	private String leito;
	
	public String getPacnome() {
		return pacnome;
	}

	public void setPacnome(String pacnome) {
		this.pacnome = pacnome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getCthseq() {
		return cthseq;
	}

	public void setCthseq(Integer cthseq) {
		this.cthseq = cthseq;
	}

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	public Integer getPhiseqitem1() {
		return phiseqitem1;
	}

	public void setPhiseqitem1(Integer phiseqitem1) {
		this.phiseqitem1 = phiseqitem1;
	}

	public Integer getPhiseqitem2() {
		return phiseqitem2;
	}

	public void setPhiseqitem2(Integer phiseqitem2) {
		this.phiseqitem2 = phiseqitem2;
	}

	public Integer getIphseqitem1() {
		return iphseqitem1;
	}

	public void setIphseqitem1(Integer iphseqitem1) {
		this.iphseqitem1 = iphseqitem1;
	}

	public String getDescsus1() {
		return descsus1;
	}

	public void setDescsus1(String descsus1) {
		this.descsus1 = descsus1;
	}

	public Integer getIphseqitem2() {
		return iphseqitem2;
	}

	public void setIphseqitem2(Integer iphseqitem2) {
		this.iphseqitem2 = iphseqitem2;
	}

	public String getDescsus2() {
		return descsus2;
	}

	public void setDescsus2(String descsus2) {
		this.descsus2 = descsus2;
	}

	public Integer getIphseqrealizado() {
		return iphseqrealizado;
	}

	public void setIphseqrealizado(Integer iphseqrealizado) {
		this.iphseqrealizado = iphseqrealizado;
	}

	public String getDescitemreal() {
		return descitemreal;
	}

	public void setDescitemreal(String descitemreal) {
		this.descitemreal = descitemreal;
	}

	public Integer getIphseq() {
		return iphseq;
	}

	public void setIphseq(Integer iphseq) {
		this.iphseq = iphseq;
	}

	public String getDescitemsol() {
		return descitemsol;
	}

	public void setDescitemsol(String descitemsol) {
		this.descitemsol = descitemsol;
	}

	public Date getDatainternacaoadministrativa() {
		return datainternacaoadministrativa;
	}

	public void setDatainternacaoadministrativa(
			Timestamp datainternacaoadministrativa) {
		this.datainternacaoadministrativa = datainternacaoadministrativa;
	}

	public Date getDtaltaadministrativa() {
		return dtaltaadministrativa;
	}

	public void setDtaltaadministrativa(Timestamp dtaltaadministrativa) {
		this.dtaltaadministrativa = dtaltaadministrativa;
	}

	public Long getNroaih() {
		return nroaih;
	}

	public void setNroaih(Long nroaih) {
		this.nroaih = nroaih;
	}

	public Byte getCodigosusmsp() {
		return codigosusmsp;
	}

	public void setCodigosusmsp(Byte codigosusmsp) {
		this.codigosusmsp = codigosusmsp;
	}

	public Byte getCodigosussia() {
		return codigosussia;
	}

	public void setCodigosussia(Byte codigosussia) {
		this.codigosussia = codigosussia;
	}

	public String getDescricaophi1() {
		return descricaophi1;
	}

	public void setDescricaophi1(String descricaophi1) {
		this.descricaophi1 = descricaophi1;
	}

	public String getDescricaophi2() {
		return descricaophi2;
	}

	public void setDescricaophi2(String descricaophi2) {
		this.descricaophi2 = descricaophi2;
	}

	public Short getUnfseq() {
		return unfseq;
	}

	public void setUnfseq(Short unfseq) {
		this.unfseq = unfseq;
	}

	public Date getDthrrealizado() {
		return dthrrealizado;
	}

	public void setDthrrealizado(Date dthrrealizado) {
		this.dthrrealizado = dthrrealizado;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}
}