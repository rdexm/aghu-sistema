package br.gov.mec.aghu.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.core.commons.BaseBean;

public class PacientesEmAtendimentoVO implements BaseBean {

	private static final String RETICENCIAS = "...";

	private static final String HIFEN_ESPACADO = " - ";

	private static final long serialVersionUID = 8240123232690796965L;
	
	private Integer atdSeq;
	private String unfDescricao;
	private String espSigla;
	private String espNome;
	private DominioOrigemAtendimento origem;
	private DominioPacAtendimento indPacAtendimento;
	private Date dthrInicio;
	private Integer conNumero;
	private Integer matriculaResp;
	private Short vinCodigoResp;
	private Short espSeq;
	private Short unfSeq;
	private Integer pacProntuario;
	private Integer pacCodigo;
	private String pacNome;
	private String leito;
	private String nomeResp;
	private String nroRegConselhoResp;
	
	public PacientesEmAtendimentoVO() {
		
	}
	
	public String getProntuarioNomeConcatenado() {
		return (formataProntuario(pacProntuario) + HIFEN_ESPACADO + pacNome);
	}
	
	public String getProntuarioNomePaciente(Integer size) {
		String retorno;
		if(pacProntuario != null){
			retorno = formataProntuario(pacProntuario) + HIFEN_ESPACADO + pacNome;
		}else{
			retorno = pacNome;
		}
		if(size != null && size != 0 && retorno.length() > size) {
			retorno = retorno.substring(0, size - 2) + RETICENCIAS;
		}
		return retorno;
	}
	
	public String getCodigoDescricaoUf(Integer size) {
		String retorno = unfSeq + HIFEN_ESPACADO + unfDescricao;
		if(size != null && size != 0 && retorno.length() > size) {
			retorno = retorno.substring(0,size.intValue()-2) + RETICENCIAS;
		}
		return retorno;
	}
	
	public String getSiglaNomeEsp(Integer size) {
		String retorno = espSigla + HIFEN_ESPACADO + espNome;
		if(size != null && size != 0 && retorno.length() > size) {
			retorno = retorno.substring(0,size.intValue()-2) + RETICENCIAS;
		}
		return retorno;
	}
	public String getResponsavel(Integer size) {
		String retorno = null;
		if(nroRegConselhoResp == null){
			retorno = nomeResp;
		}else{
			retorno = nroRegConselhoResp + HIFEN_ESPACADO + nomeResp;
		}
		if(size != null && size != 0 && retorno.length() > size) {
			retorno = retorno.substring(0,size.intValue()-2) + RETICENCIAS;
		}
		return retorno;
	}
	
	public static String formataProntuario(Object valor) {
		if (valor == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder(valor.toString());
		while (sb.length() < 8) {
			sb.insert(0, '0');
		}
		sb.insert(7, '/');
		return sb.toString();
	}

	public enum Fields {
		NRO_REG_CONSELHO("nroRegConselhoResp"),
		ESP_SIGLA("espSigla"),
		ESP_NOME("espNome"),
		UNF_DESCRICAO("unfDescricao"),
		ATD_SEQ("atdSeq"),
		ORIGEM("origem"),
		IND_PAC_ATENDIMENTO("indPacAtendimento"),
		DTHR_INICIO("dthrInicio"),
		CON_NUMERO("conNumero"),
		MATRICULA_RESP("matriculaResp"),
		VIN_CODIGO_RESP("vinCodigoResp"),
		ESP_SEQ("espSeq"),
		UNF_SEQ("unfSeq"),
		PAC_PRONTUARIO("pacProntuario"),
		PAC_CODIGO("pacCodigo"),
		PAC_NOME("pacNome"),
		LEITO("leito"),
		NOME_RESP("nomeResp");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}

	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}

	public DominioPacAtendimento getIndPacAtendimento() {
		return indPacAtendimento;
	}

	public void setIndPacAtendimento(DominioPacAtendimento indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Integer getMatriculaResp() {
		return matriculaResp;
	}

	public void setMatriculaResp(Integer matriculaResp) {
		this.matriculaResp = matriculaResp;
	}

	public Short getVinCodigoResp() {
		return vinCodigoResp;
	}

	public void setVinCodigoResp(Short vinCodigoResp) {
		this.vinCodigoResp = vinCodigoResp;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getNomeResp() {
		return nomeResp;
	}

	public void setNomeResp(String nomeResp) {
		this.nomeResp = nomeResp;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}

	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}

	public String getEspSigla() {
		return espSigla;
	}

	public void setEspNome(String espNome) {
		this.espNome = espNome;
	}

	public String getEspNome() {
		return espNome;
	}

	public void setNroRegConselhoResp(String nroRegConselhoResp) {
		this.nroRegConselhoResp = nroRegConselhoResp;
	}

	public String getNroRegConselhoResp() {
		return nroRegConselhoResp;
	}
}