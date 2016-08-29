package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.utils.DateUtil;


public class RelatorioAnaEvoInternacaoVO implements Serializable {

	private static final long serialVersionUID = -7717103193721142648L;
	
	private static final String DATE_PATTERN_DDMMYYYY = "dd/MM/yyyy";
	private static final String DATE_PATTERN_DDMMYYYYHHMM = "dd/MM/yyyy HH:mm";

	private Long anaSeq;
	private Long evoSeq;
	private Long trgSeq;
	private Integer tinSeq;
	private String tinDescricao;
	private Short tinOrdem;
	private String tinConteudo;
	private Date dthrValidaAna;
	private Integer tieSeq;
	private String tieDescricao;
	private Short tieOrdem;
	private String tieConteudo;
	private String trgConteudo;
	private Date dthrValidaEvo;
	private Long rgtSeq;
	private String tipoRelatorio;
	private String grupoAnamnese;
	private String responsavel;
	private String visto;
	private String paciente;
	private String agenda;
	private String prontuario;
	private String rodape;
	private Integer codPac;
	private Date dtNascimento;
	private Boolean triagem;
	private Integer conNumero;

	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Integer getTinSeq() {
		return tinSeq;
	}

	public void setTinSeq(Integer tinSeq) {
		this.tinSeq = tinSeq;
	}

	public String getTinDescricao() {
		return tinDescricao;
	}

	public void setTinDescricao(String tinDescricao) {
		this.tinDescricao = tinDescricao;
	}

	public Short getTinOrdem() {
		return tinOrdem;
	}

	public void setTinOrdem(Short tinOrdem) {
		this.tinOrdem = tinOrdem;
	}
	
	public String getTinConteudo() {
		return tinConteudo;
	}

	public void setTinConteudo(String tinConteudo) {
		this.tinConteudo = tinConteudo;
	}

	public Date getDthrValidaAna() {
		return dthrValidaAna;
	}

	public void setDthrValidaAna(Date dthrValidaAna) {
		this.dthrValidaAna = dthrValidaAna;
	}

	public Long getRgtSeq() {
		return rgtSeq;
	}

	public void setRgtSeq(Long rgtSeq) {
		this.rgtSeq = rgtSeq;
	}

	public Integer getTieSeq() {
		return tieSeq;
	}

	public void setTieSeq(Integer tieSeq) {
		this.tieSeq = tieSeq;
	}

	public String getTieDescricao() {
		return tieDescricao;
	}

	public void setTieDescricao(String tieDescricao) {
		this.tieDescricao = tieDescricao;
	}

	public Short getTieOrdem() {
		return tieOrdem;
	}

	public void setTieOrdem(Short tieOrdem) {
		this.tieOrdem = tieOrdem;
	}

	public String getTieConteudo() {
		return tieConteudo;
	}

	public void setTieConteudo(String tieConteudo) {
		this.tieConteudo = tieConteudo;
	}
	
	public Date getDthrValidaEvo() {
		return dthrValidaEvo;
	}

	public void setDthrValidaEvo(Date dthrValidaEvo) {
		this.dthrValidaEvo = dthrValidaEvo;
	}
	
	public String getData(){
		String data = "";		
		if(getDthrValidaEvo() != null){
			data = DateUtil.dataToString(getDthrValidaEvo(), getDatePattern());
		}else{
			data = DateUtil.dataToString(getDthrValidaAna(), getDatePattern());
		}
		return data;
	}

	public String getDatePattern(){
		if(anaSeq != null){
			return DATE_PATTERN_DDMMYYYY;
		}else{
			return DATE_PATTERN_DDMMYYYYHHMM;
		}
	}
	
	public enum Fields {
		ANA_SEQ("anaSeq"),
		EVO_SEQ("evoSeq"),
		TIN_SEQ("tinSeq"), 
		TIN_ORDEM("tinOrdem"), 
		TIN_DESCRICAO("tinDescricao"),
		DTHR_VALIDA_ANA("dthrValidaAna"),
		TIE_SEQ("tieSeq"), 
		TIE_ORDEM("tieOrdem"), 
		TIE_DESCRICAO("tieDescricao"),
		DTHR_VALIDA_EVO("dthrValidaEvo"),
		COD_PAC("codPac"),
		RGT_SEQ("rgtSeq");		
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getGrupoAnamnese() {
		return grupoAnamnese;
	}

	public void setGrupoAnamnese(String grupoAnamnese) {
		this.grupoAnamnese = grupoAnamnese;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getVisto() {
		return visto;
	}

	public void setVisto(String visto) {
		this.visto = visto;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String getAgenda() {
		return agenda;
	}

	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getRodape() {
		return rodape;
	}

	public void setRodape(String rodape) {
		this.rodape = rodape;
	}
	
	public Long getAnaSeq() {
		return anaSeq;
	}
	
	public void setAnaSeq(Long anaSeq) {
		this.anaSeq = anaSeq;
	}
	
	public Long getEvoSeq() {
		return evoSeq;
	}
	
	public void setEvoSeq(Long evoSeq) {
		this.evoSeq = evoSeq;
	}
	
	public String getDescricao() {
		String s = "";
		
		if (StringUtils.isNotEmpty(getTieDescricao())) {
			s = getTieDescricao();
		} else if (StringUtils.isNotEmpty(getTinDescricao())) {
			s = getTinDescricao();
		}
		return s;
	}

	public Integer getCodPac() {
		return codPac;
	}

	public void setCodPac(Integer codPac) {
		this.codPac = codPac;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	
	public String getConteudo() {
		String str = "";
		if (StringUtils.isNotEmpty(getTieConteudo())) {
			str = getTieConteudo();
		} else if (StringUtils.isNotEmpty(getTinConteudo())) {
			str = getTinConteudo();
		} else if (StringUtils.isNotEmpty(getTrgConteudo())) {
			str = getTrgConteudo();
		}
		return str;
	}
	
	public Boolean getEvolucao() {
		return getEvoSeq() != null && getAnaSeq() == null;
	}
	
	public Boolean getAnamnese() {
		return getEvoSeq() == null && getAnaSeq() != null;
	}

	public Boolean getTriagem() {
		return triagem;
	}

	public void setTriagem(Boolean triagem) {
		this.triagem = triagem;
	}
	
	public String getTrgConteudo() {
		return trgConteudo;
	}

	public void setTrgConteudo(String trgConteudo) {
		this.trgConteudo = trgConteudo;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Integer getConNumero() {
		return conNumero;
	}

}
