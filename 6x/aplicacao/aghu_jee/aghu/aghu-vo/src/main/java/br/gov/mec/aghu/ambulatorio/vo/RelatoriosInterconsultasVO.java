package br.gov.mec.aghu.ambulatorio.vo;


import java.util.Date;
import java.util.List;


import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultasPesquisa;
import br.gov.mec.aghu.core.commons.BaseBean;

public class RelatoriosInterconsultasVO implements BaseBean{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 4396871693167304373L;
	
	//Relatorios Interconsultas
	private Long ieoseq;
	private Date ieodthrcricao;
	private DominioIndPendenteAmbulatorio ieopendente;
	private Integer ieopaccodigo;
	private Integer ieoconnumero;
	private Integer ieoconnumeromarcada;
	private Short ieoespseq;
	private Integer ieoeqpseq;
	private Integer ieosermatricularesp;
	private Short ieovincodigoresp;
	private DominioSituacaoInterconsultasPesquisa ieositucao;
	private String ieoindurgente;
	private String ieoobservacaoadd;
	
	private Integer paccodigo;
	private String pacnome;
	private Integer pacprontuario;
	private Long pacfoneresidencial;
	private Short pacdddfoneresidencial;
	
	private Short espseq;
	private String espsigla;
	private String espnomeespecialidade;
	
	private String marcacao;
	
	//Relatorios de pacientes com interconsultas
	private Integer cctcodigo;
	private String cctdescricao;
	private String ieoobservacao;
	
	private List<RelatorioPacientesInterconsultasVO> listapacientes;
	
	public Long getIeoseq() {
		return ieoseq;
	}
	public void setIeoseq(Long ieoseq) {
		this.ieoseq = ieoseq;
	}
	public Date getIeodthrcricao() {
		return ieodthrcricao;
	}
	public void setIeodthrcricao(Date ieodthrcricao) {
		this.ieodthrcricao = ieodthrcricao;
	}
	public DominioIndPendenteAmbulatorio getIeopendente() {
		return ieopendente;
	}
	public void setIeopendente(DominioIndPendenteAmbulatorio ieopendente) {
		this.ieopendente = ieopendente;
	}
	public Integer getIeopaccodigo() {
		return ieopaccodigo;
	}
	public void setIeopaccodigo(Integer ieopaccodigo) {
		this.ieopaccodigo = ieopaccodigo;
	}
	public Integer getIeoconnumero() {
		return ieoconnumero;
	}
	public void setIeoconnumero(Integer ieoconnumero) {
		this.ieoconnumero = ieoconnumero;
	}
	public Integer getIeoconnumeromarcada() {
		return ieoconnumeromarcada;
	}
	public void setIeoconnumeromarcada(Integer ieoconnumeromarcada) {
		this.ieoconnumeromarcada = ieoconnumeromarcada;
	}
	public Short getIeoespseq() {
		return ieoespseq;
	}
	public void setIeoespseq(Short ieoespseq) {
		this.ieoespseq = ieoespseq;
	}
	public Integer getIeoeqpseq() {
		return ieoeqpseq;
	}
	public void setIeoeqpseq(Integer ieoeqpseq) {
		this.ieoeqpseq = ieoeqpseq;
	}
	public Integer getIeosermatricularesp() {
		return ieosermatricularesp;
	}
	public void setIeosermatricularesp(Integer ieosermatricularesp) {
		this.ieosermatricularesp = ieosermatricularesp;
	}
	public Short getIeovincodigoresp() {
		return ieovincodigoresp;
	}
	public void setIeovincodigoresp(Short ieovincodigoresp) {
		this.ieovincodigoresp = ieovincodigoresp;
	}
	public DominioSituacaoInterconsultasPesquisa getIeositucao() {
		return ieositucao;
	}
	public void setIeositucao(DominioSituacaoInterconsultasPesquisa ieositucao) {
		this.ieositucao = ieositucao;
	}
	public String getIeoindurgente() {
		return ieoindurgente;
	}
	public void setIeoindurgente(String ieoindurgente) {
		this.ieoindurgente = ieoindurgente;
	}
	public String getIeoobservacao() {
		return ieoobservacao;
	}
	public void setIeoobservacao(String ieoobservacao) {
		this.ieoobservacao = ieoobservacao;
	}
	public String getIeoobservacaoadd() {
		if (ieoobservacaoadd != null && ieoobservacaoadd.length() > 21) {
			return ieoobservacaoadd.substring(0,22);
		}
		return ieoobservacaoadd;
	}
	public void setIeoobservacaoadd(String ieoobservacaoadd) {
		this.ieoobservacaoadd = ieoobservacaoadd;
	}
	public Integer getPaccodigo() {
		return paccodigo;
	}
	public void setPaccodigo(Integer paccodigo) {
		this.paccodigo = paccodigo;
	}
	public String getPacnome() {
		return pacnome;
	}
	public void setPacnome(String pacnome) {
		this.pacnome = pacnome;
	}
	public Integer getPacprontuario() {
		return pacprontuario;
	}
	public void setPacprontuario(Integer pacprontuario) {
		this.pacprontuario = pacprontuario;
	}
	public Long getPacfoneresidencial() {
		return pacfoneresidencial;
	}
	public void setPacfoneresidencial(Long pacfoneresidencial) {
		this.pacfoneresidencial = pacfoneresidencial;
	}
	public Short getPacdddfoneresidencial() {
		return pacdddfoneresidencial;
	}
	public void setPacdddfoneresidencial(Short pacdddfoneresidencial) {
		this.pacdddfoneresidencial = pacdddfoneresidencial;
	}
	public Short getEspseq() {
		return espseq;
	}
	public void setEspseq(Short espseq) {
		this.espseq = espseq;
	}
	public String getEspsigla() {
		return espsigla;
	}
	public void setEspsigla(String espsigla) {
		this.espsigla = espsigla;
	}
	public String getEspnomeespecialidade() {
		return espnomeespecialidade;
	}
	public void setEspnomeespecialidade(String espnomeespecialidade) {
		this.espnomeespecialidade = espnomeespecialidade;
	}
	public String getMarcacao() {
		return marcacao;
	}
	public void setMarcacao(String marcacao) {
		this.marcacao = marcacao;
	}

	public Integer getCctcodigo() {
		return cctcodigo;
	}
	public void setCctcodigo(Integer cctcodigo) {
		this.cctcodigo = cctcodigo;
	}
	public String getCctdescricao() {
		return cctdescricao;
	}
	public void setCctdescricao(String cctdescricao) {
		this.cctdescricao = cctdescricao;
	}
	public List<RelatorioPacientesInterconsultasVO> getListapacientes() {
		return listapacientes;
	}
	public void setListapacientes(
			List<RelatorioPacientesInterconsultasVO> listapacientes) {
		this.listapacientes = listapacientes;
	}

	public enum Fields {
		
		IEO_SEQ("ieoseq"),
		IEO_DTHRCRICAO("ieodthrcricao"),
		IEO_PENDENTE("ieopendente"),
		IEO_PACCODIGO("ieopaccodigo"),
		IEO_CONNUMERO("ieoconnumero"),
		IEO_CONNUMEROMARCADA("ieoconnumeromarcada"),
		IEO_ESPSEQ("ieoespseq"),
		IEO_EQPSEQ("ieoeqpseq"),
		IEO_SERMATRICULARESP("ieosermatricularesp"),
		IEO_VINCODIGORESP("ieovincodigoresp"),
		IEO_SITUCAO("ieositucao"),
		IEO_INDURGENTE("ieoindurgente"),
		IEO_OBSERVACAO("ieoobservacao"),
		IEO_OBSERVACAO_ADICIONAL("ieoobservacaoadd"),
		
		PAC_CODIGO("paccodigo"),	  
		PAC_NOME("pacnome"),
		PAC_PRONTUARIO("pacprontuario"),
		PAC_FONERESIDENCIAL("pacfoneresidencial"),
		PAC_DDDFONERESIDENCIAL("pacdddfoneresidencial"),
		
		ESP_SEQ("espseq"),
		ESP_SIGLA("espsigla"),
		ESP_NOME_ESPECIALIDADE("espnomeespecialidade"),
		
		MARCACAO("marcacao"),
		
		CCT_DESCRICAO("cctdescricao"),
		CCT_CODIGO("cctcodigo");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
