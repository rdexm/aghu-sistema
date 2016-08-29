package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO de Alteração de Horário para C1 em #44228 – Realizar Manutenção de
 * Agendamento de Sessão Terapêutica
 * 
 * @author aghu
 *
 */
public class ReservasVO implements BaseBean {

	private static final long serialVersionUID = -462903433407149494L;
	
	//C1
	private Short cloCiclo;
	private Short hrsDia;
	private Date hrsDataInicio;
	private Date hrsDataFim;
	private Integer sesCloSeq;	
	//C2
	private Date cloDataPresvista;
	private Integer cloSeq;
	//C3
	private String ptcDescricao;
	private String ptaTitulo;
	//C5
	private String tpsDescricao;
	private String salDescricao;
	private Short pteCiclo;
	private String tpsAviso;
	private Short espSeq;
	private String espNomeReduzido;
	private Short agsSeq;
	//C8
	private Short seq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String responsavel;
	private String descSessao;
	private String descSala;
	private String aviso;
	private String nome;
	private Integer prontuario;
	private Date criadoEm;
	private String titulo;
	//C9
	private Short dia;
	private Date dataInicio;
	private Date dataFim;
	private Short ciclo;
	
	
	public enum Fields {

		SEQ("seq"), 
		SER_MATRICULA("serMatricula"), 
		SER_VIN_CODIGO("serVinCodigo"), 
		RESPONSAVEL("responsavel"), 
		DESC_SESSAO("descSessao"), 
		DESC_SALA("descSala"), 
		AVISO("aviso"), 
		NOME("nome"), 
		PRONTUARIO("prontuario"),
		CRIADO_EM("criadoEm"),
		TITULO("titulo"),
		DIA("dia"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		CICLO("ciclo"),
		PTC_DESCRICAO("ptcDescricao"),
		PTA_TITULO("ptaTitulo"),
		CLO_CICLO("cloCiclo"),
		HRS_DIA("hrsDia"),
		HRS_DATA_INICIO("hrsDataInicio"),
		HRS_DATA_FIM("hrsDataFim"),
		SES_CLO_SEQ("sesCloSeq"),
		CLO_DATA_PRESVISTA("cloDataPresvista"),
		CLO_SEQ("cloSeq"),
		TPS_DESCRICAO("tpsDescricao"),
		SAL_DESCRICAO("salDescricao"),
		PTE_CICLO("pteCiclo"),
		TPS_AVISO("tpsAviso"),
		ESP_SEQ("espSeq"),
		ESP_NOME_REDUZIDO("espNomeReduzido"),
		AGS_SEQ("agsSeq");
				

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	
	
	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}	
	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}
	public String getDescSessao() {
		return descSessao;
	}
	public void setDescSessao(String descSessao) {
		this.descSessao = descSessao;
	}
	public String getDescSala() {
		return descSala;
	}
	public void setDescSala(String descSala) {
		this.descSala = descSala;
	}
	public String getAviso() {
		return aviso;
	}
	public void setAviso(String aviso) {
		this.aviso = aviso;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public Short getDia() {
		return dia;
	}
	public void setDia(Short dia) {
		this.dia = dia;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public Short getCiclo() {
		return ciclo;
	}
	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}
	public String getPtcDescricao() {
		return ptcDescricao;
	}
	public void setPtcDescricao(String ptcDescricao) {
		this.ptcDescricao = ptcDescricao;
	}
	public String getPtaTitulo() {
		return ptaTitulo;
	}
	public void setPtaTitulo(String ptaTitulo) {
		this.ptaTitulo = ptaTitulo;
	}
	public Short getCloCiclo() {
		return cloCiclo;
	}
	public void setCloCiclo(Short cloCiclo) {
		this.cloCiclo = cloCiclo;
	}
	public Short getHrsDia() {
		return hrsDia;
	}
	public void setHrsDia(Short hrsDia) {
		this.hrsDia = hrsDia;
	}
	public Date getHrsDataInicio() {
		return hrsDataInicio;
	}
	public void setHrsDataInicio(Date hrsDataInicio) {
		this.hrsDataInicio = hrsDataInicio;
	}
	public Date getHrsDataFim() {
		return hrsDataFim;
	}
	public void setHrsDataFim(Date hrsDataFim) {
		this.hrsDataFim = hrsDataFim;
	}
	public Integer getSesCloSeq() {
		return sesCloSeq;
	}
	public void setSesCloSeq(Integer sesCloSeq) {
		this.sesCloSeq = sesCloSeq;
	}
	public Date getCloDataPresvista() {
		return cloDataPresvista;
	}
	public void setCloDataPresvista(Date cloDataPresvista) {
		this.cloDataPresvista = cloDataPresvista;
	}
	public Integer getCloSeq() {
		return cloSeq;
	}
	public void setCloSeq(Integer cloSeq) {
		this.cloSeq = cloSeq;
	}
	public String getTpsDescricao() {
		return tpsDescricao;
	}
	public void setTpsDescricao(String tpsDescricao) {
		this.tpsDescricao = tpsDescricao;
	}
	public String getSalDescricao() {
		return salDescricao;
	}
	public void setSalDescricao(String salDescricao) {
		this.salDescricao = salDescricao;
	}
	public Short getPteCiclo() {
		return pteCiclo;
	}
	public void setPteCiclo(Short pteCiclo) {
		this.pteCiclo = pteCiclo;
	}
	public String getTpsAviso() {
		return tpsAviso;
	}
	public void setTpsAviso(String tpsAviso) {
		this.tpsAviso = tpsAviso;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	public String getEspNomeReduzido() {
		return espNomeReduzido;
	}
	public void setEspNomeReduzido(String espNomeReduzido) {
		this.espNomeReduzido = espNomeReduzido;
	}
	public Short getAgsSeq() {
		return agsSeq;
	}
	public void setAgsSeq(Short agsSeq) {
		this.agsSeq = agsSeq;
	}
	
	public String retornaLocalAtendimento() {
		return (this.descSessao != null ? this.descSessao + " - "  : StringUtils.EMPTY) + (this.descSala != null ? this.descSala : StringUtils.EMPTY);
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
}
