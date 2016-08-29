package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.commons.BaseBean;



public class InternacaoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 794586572820055305L;
	private Integer seq;
	private Integer prontuario;
	private Integer atdSeq;
	private Date dthrInicio;
	private Date dthrFim;
	private Date dtPrevisao;
	private String leito;
	private String nomeEspecialidade;
	private String convenio;
	private String nomeProfessor;
	private String servico;
	private String codigoCid;
	private String descricaoCid;
	private String tipo;
	private DominioPacAtendimento indPacAtendimento;
	private DominioSimNao indPacProtegido;
	private Boolean possuiDataItemSumarios;
	private Boolean possuiRegistroControlepaciente;
	private Date dtReferencia;
	private StatusAltaObito statusAltaObito;
	private Integer seqVersaoDocumento;
	private Boolean possuiDataItemSumariosPE;
	private Boolean flagExibeSumarioDeTransferencia;
	private Integer codigoPaciente;
	private Integer numeroConsulta;
	private Short gsoSeqp;
	List<String> descricaoCids;

	public InternacaoVO(){
	}
	
	public InternacaoVO(Integer seq) {
		super();
		this.seq = seq;
	}

	public InternacaoVO(Integer codigoPaciente,	Integer numeroConsulta,	Short gsoSeqp){
		this.codigoPaciente = codigoPaciente;
		this.numeroConsulta = numeroConsulta;
		this.gsoSeqp = gsoSeqp; 
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public Date getDtPrevisao() {
		return dtPrevisao;
	}

	public void setDtPrevisao(Date dtPrevisao) {
		this.dtPrevisao = dtPrevisao;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getNomeProfessor() {
		return nomeProfessor;
	}

	public void setNomeProfessor(String nomeProfessor) {
		this.nomeProfessor = nomeProfessor;
	}

	public String getServico() {
		return servico;
	}

	public void setServico(String servico) {
		this.servico = servico;
	}

	public String getCodigoCid() {
		return codigoCid;
	}

	public void setCodigoCid(String string) {
		this.codigoCid = string;
	}

	public String getDescricaoCid() {
		return descricaoCid;
	}

	public void setDescricaoCid(String descricaoCid) {
		this.descricaoCid = descricaoCid;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public DominioSimNao getIndPacProtegido() {
		return indPacProtegido;
	}

	public void setIndPacProtegido(DominioSimNao indPacProtegido) {
		this.indPacProtegido = indPacProtegido;
	}

	public Boolean getPossuiDataItemSumarios() {
		return possuiDataItemSumarios;
	}

	public void setPossuiDataItemSumarios(Boolean possuiDataItemSumarios) {
		this.possuiDataItemSumarios = possuiDataItemSumarios;
	}

	public DominioPacAtendimento getIndPacAtendimento() {
		return indPacAtendimento;
	}

	public void setIndPacAtendimento(DominioPacAtendimento indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}

	public Date getDtReferencia() {
		return dtReferencia;
	}

	public void setDtReferencia(Date dtReferencia) {
		this.dtReferencia = dtReferencia;
	}

	public Boolean getPossuiRegistroControlepaciente() {
		return possuiRegistroControlepaciente;
	}

	public void setPossuiRegistroControlepaciente(
			Boolean possuiRegistroControlepaciente) {
		this.possuiRegistroControlepaciente = possuiRegistroControlepaciente;
	}
	
	public StatusAltaObito getStatusAltaObito() {
		return statusAltaObito;
	}

	public void setStatusAltaObito(StatusAltaObito statusAltaObito) {
		this.statusAltaObito = statusAltaObito;
	}
		
	public Integer getSeqVersaoDocumento() {
		return seqVersaoDocumento;
	}

	public void setSeqVersaoDocumento(Integer seqVersaoDocumento) {
		this.seqVersaoDocumento = seqVersaoDocumento;
	}

	public Boolean getPossuiDocumentoPendente() {
		if (StatusAltaObito.APRESENTA_DOCUMENTO_PENDENTE
				.equals(this.statusAltaObito)) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getPossuiDocumentoAssinado() {
		if (StatusAltaObito.APRESENTA_DOCUMENTO_ASSINADO
				.equals(this.statusAltaObito)) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getPossuiRelatorioAltaObito() {
		if (StatusAltaObito.APRESENTA_RELATORIO_SUMARIO_ALTA
				.equals(this.statusAltaObito)
				|| StatusAltaObito.APRESENTA_RELATORIO_SUMARIO_OBITO
						.equals(this.statusAltaObito)) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getObito() {
		if (StatusAltaObito.APRESENTA_RELATORIO_SUMARIO_OBITO
				.equals(this.statusAltaObito)) {
			return "S";
		} else {
			return "N";
		}
	}
	
	public Boolean getPossuiDataItemSumariosPE() {
		return possuiDataItemSumariosPE;
	}

	public void setPossuiDataItemSumariosPE(Boolean possuiDataItemSumariosPE) {
		this.possuiDataItemSumariosPE = possuiDataItemSumariosPE;
	}

	public enum StatusAltaObito {
		/**
		 * Internação não possui documento alta/óbito
		 */
		NAO_APRESENTA_DOCUMENTO_ALTA_OBITO,
		/**
		 * Internação possui documento assinado
		 */
		APRESENTA_DOCUMENTO_ASSINADO,
		/**
		 * Internação possui documento pendente
		 */
		APRESENTA_DOCUMENTO_PENDENTE,
		/**
		 * Internação possui relatório alta
		 */
		APRESENTA_RELATORIO_SUMARIO_ALTA,
		/**
		 * Internação possui relatório óbito
		 */
		APRESENTA_RELATORIO_SUMARIO_OBITO;
	}

	public Boolean getFlagExibeSumarioDeTransferencia() {
		return flagExibeSumarioDeTransferencia;
	}

	public void setFlagExibeSumarioDeTransferencia(
			Boolean flagExibeSumarioDeTransferencia) {
		this.flagExibeSumarioDeTransferencia = flagExibeSumarioDeTransferencia;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}
	
	public List<String> getDescricaoCids() {
		return descricaoCids;
	}

	public void setDescricaoCids(List<String> descricaoCids) {
		this.descricaoCids = descricaoCids;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	/**
	 * Utilizado em InformacoesPerinataisPOLController
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof InternacaoVO)) {
			return false;
		}
		InternacaoVO other = (InternacaoVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
}