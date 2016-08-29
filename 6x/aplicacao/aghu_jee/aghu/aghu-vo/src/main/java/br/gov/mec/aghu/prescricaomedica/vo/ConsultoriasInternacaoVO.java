package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioIndConcluidaSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;

public class ConsultoriasInternacaoVO implements Serializable {

	private static final long serialVersionUID = 214195818515995772L;
	
	private Integer seqAux;
	private Integer atdSeq;
	private Integer seq;
	private Integer prontuario;
	private String nome;
	private String leitoId;
	private Short qrtoNumero;
	private Short unfSeq;
	private DominioIndConcluidaSolicitacaoConsultoria indConcluida;
	private DominioTipoSolicitacaoConsultoria tipo;
	private DominioSimNao indUrgencia;
	private Date dthrSolicitada;
	private Date dthrResposta;
	private Date dthrPrimeiraConsulta;
	private String convenio;
	private String localPac;
	// Usado para ordenação
	private Date criadoEm;
	
	// Utilizados no relatório
	private String leitoRelatorio;
	private String nomePacRelatorio;
	private String espSigla;	
	private String equipe;
	private String drvSituacao;
	
	private Boolean indGmr = false;

	public Boolean getIndGmr() {
		return indGmr;
	}
	public void setIndGmr(Boolean indGmr) {
		this.indGmr = indGmr;
	}
	public Integer getSeqAux() {
		return seqAux;
	}
	public void setSeqAux(Integer seqAux) {
		this.seqAux = seqAux;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
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
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getLeitoId() {
		return leitoId;
	}
	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}
	public Short getQrtoNumero() {
		return qrtoNumero;
	}
	public void setQrtoNumero(Short qrtoNumero) {
		this.qrtoNumero = qrtoNumero;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public DominioIndConcluidaSolicitacaoConsultoria getIndConcluida() {
		return indConcluida;
	}
	public void setIndConcluida(
			DominioIndConcluidaSolicitacaoConsultoria indConcluida) {
		this.indConcluida = indConcluida;
	}
	public DominioTipoSolicitacaoConsultoria getTipo() {
		return tipo;
	}
	public void setTipo(DominioTipoSolicitacaoConsultoria tipo) {
		this.tipo = tipo;
	}
	public DominioSimNao getIndUrgencia() {
		return indUrgencia;
	}
	public void setIndUrgencia(DominioSimNao indUrgencia) {
		this.indUrgencia = indUrgencia;
	}
	public Date getDthrSolicitada() {
		return dthrSolicitada;
	}
	public void setDthrSolicitada(Date dthrSolicitada) {
		this.dthrSolicitada = dthrSolicitada;
	}
	public Date getDthrResposta() {
		return dthrResposta;
	}
	public void setDthrResposta(Date dthrResposta) {
		this.dthrResposta = dthrResposta;
	}
	public Date getDthrPrimeiraConsulta() {
		return dthrPrimeiraConsulta;
	}
	public void setDthrPrimeiraConsulta(Date dthrPrimeiraConsulta) {
		this.dthrPrimeiraConsulta = dthrPrimeiraConsulta;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getConvenio() {
		return convenio;
	}
	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}
	public String getLocalPac() {
		return localPac;
	}
	public void setLocalPac(String localPac) {
		this.localPac = localPac;
	}
	public String getEspSigla() {
		return espSigla;
	}
	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getDrvSituacao() {
		return drvSituacao;
	}
	public void setDrvSituacao(String drvSituacao) {
		this.drvSituacao = drvSituacao;
	}
	public String getLeitoRelatorio() {
		return leitoRelatorio;
	}
	public void setLeitoRelatorio(String leitoRelatorio) {
		this.leitoRelatorio = leitoRelatorio;
	}
	public String getNomePacRelatorio() {
		return nomePacRelatorio;
	}
	public void setNomePacRelatorio(String nomePacRelatorio) {
		this.nomePacRelatorio = nomePacRelatorio;
	}

	public enum Fields {
		ATD_SEQ("atdSeq"),
		SEQ("seq"), 
		PRONTUARIO("prontuario"),
		NOME("nome"),
		LEITO_ID("leitoId"),
		QRTO_NUMERO("qrtoNumero"),
		UNF_SEQ("unfSeq"),
		IND_CONCLUIDA("indConcluida"),
		TIPO("tipo"),
		IND_URGENCIA("indUrgencia"),
		DTHR_SOLICITADA("dthrSolicitada"),
		DTHR_RESPOSTA("dthrResposta"),
		DTHR_PRIM_CONSULTA("dthrPrimeiraConsulta"),
		CRIADO_EM("criadoEm"),
		CONVENIO("convenio"),
		LOCAL_PAC("localPac");
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	@Override
    public int hashCode() {
        HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getSeqAux());
        umHashCodeBuilder.append(this.getAtdSeq());
        umHashCodeBuilder.append(this.getSeq());
        umHashCodeBuilder.append(this.getProntuario());
        umHashCodeBuilder.append(this.getNome());
        umHashCodeBuilder.append(this.getLeitoId());
        umHashCodeBuilder.append(this.getQrtoNumero());
        umHashCodeBuilder.append(this.getUnfSeq());
        umHashCodeBuilder.append(this.getIndConcluida());
        umHashCodeBuilder.append(this.getTipo());
        umHashCodeBuilder.append(this.getIndUrgencia());
        umHashCodeBuilder.append(this.getDthrSolicitada());
        umHashCodeBuilder.append(this.getDthrResposta());
        umHashCodeBuilder.append(this.getDthrPrimeiraConsulta());
        umHashCodeBuilder.append(this.getCriadoEm());
        umHashCodeBuilder.append(this.getConvenio());
        umHashCodeBuilder.append(this.getLocalPac());
        umHashCodeBuilder.append(this.getEspSigla());
        umHashCodeBuilder.append(this.getEquipe());
        umHashCodeBuilder.append(this.getDrvSituacao());
        umHashCodeBuilder.append(this.getLeitoRelatorio());
        umHashCodeBuilder.append(this.getNomePacRelatorio());
        return umHashCodeBuilder.toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ConsultoriasInternacaoVO)) {
            return false;
        }
        ConsultoriasInternacaoVO other = (ConsultoriasInternacaoVO) obj;
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getSeqAux(), other.getSeqAux());
        umEqualsBuilder.append(this.getAtdSeq(), other.getAtdSeq());
        umEqualsBuilder.append(this.getSeq(), other.getSeq());
        umEqualsBuilder.append(this.getProntuario(), other.getProntuario());
        umEqualsBuilder.append(this.getProntuario(), other.getProntuario());
        umEqualsBuilder.append(this.getNome(), other.getNome());
        umEqualsBuilder.append(this.getLeitoId(), other.getLeitoId());
        umEqualsBuilder.append(this.getQrtoNumero(), other.getQrtoNumero());
        umEqualsBuilder.append(this.getUnfSeq(), other.getUnfSeq());
        umEqualsBuilder.append(this.getIndConcluida(), other.getIndConcluida());
        umEqualsBuilder.append(this.getTipo(), other.getTipo());
        umEqualsBuilder.append(this.getIndUrgencia(), other.getIndUrgencia());
        umEqualsBuilder.append(this.getDthrSolicitada(), other.getDthrSolicitada());
        umEqualsBuilder.append(this.getDthrResposta(), other.getDthrResposta());
        umEqualsBuilder.append(this.getDthrPrimeiraConsulta(), other.getDthrPrimeiraConsulta());
        umEqualsBuilder.append(this.getCriadoEm(), other.getCriadoEm());
        umEqualsBuilder.append(this.getConvenio(), other.getConvenio());
        umEqualsBuilder.append(this.getLocalPac(), other.getLocalPac());
        umEqualsBuilder.append(this.getEspSigla(), other.getEspSigla());
        umEqualsBuilder.append(this.getEquipe(), other.getEquipe());
        umEqualsBuilder.append(this.getDrvSituacao(), other.getDrvSituacao());
        umEqualsBuilder.append(this.getLeitoRelatorio(), other.getLeitoRelatorio());
        umEqualsBuilder.append(this.getNomePacRelatorio(), other.getNomePacRelatorio());
        return umEqualsBuilder.isEquals();
    }
}
