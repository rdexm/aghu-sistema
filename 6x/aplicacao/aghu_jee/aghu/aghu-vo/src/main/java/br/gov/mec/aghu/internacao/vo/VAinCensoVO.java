package br.gov.mec.aghu.internacao.vo;

import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.dominio.DominioTipoCensoDiarioPacientes;
import br.gov.mec.aghu.core.commons.BaseBean;


public class VAinCensoVO implements BaseBean {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1598726217030012864L;
	private Short unfSeq;
	private Short unfMaeSeq;
	private String qrtoLto;
	private Integer prontuario;
	private Integer pacCodigo;
	private String nomeSituacao;
	private boolean origemNomeSituacao = false;//Criado para indicar se o atributo "nomeSituacao" eh populado a partir das informacoes de um leito.
	private Date dataInternacao;
	private String siglaEsp;
	private String descricaoEsp;
	private String tamCodigo; 
	private String tamDescricao; 
	private String nomeMedico;//nome usual
	private String local;
	private Date dthrLancamento;
	private Date dthrLancamentoFinal;
	private Integer internacaoSeq;
	private DominioSituacaoUnidadeFuncional status;
	private DominioMovimentoLeito grupoMvtoLeito;
	private DominioTipoCensoDiarioPacientes tipo;
	private Short cspCnvCodigo;	
	private Date dtNascPaciente;
	private String descConvenio;
	private Integer seqExtrato;
	private String estadoSaude;
	
	
	//### ATRIBUTOS QUE CONTROLAM A EXIBICAO DA TELA DE PESQUISAR CENSO DIARIO DE PACIENTES ###//
	private Integer estiloColunaTempo;
	private boolean estiloColunaProntuario;
	private boolean estiloColunaQrto;
	private Integer tempo;
	private boolean exibirBotaoCadastroDePacientes;
	private boolean exibirBotaoExtrato;
	private boolean exibirBotaoTransferencia;
	private boolean exibirBotaoInternacao;
	private boolean exibirBotaoAlta;
	
	private String labelBotaoInternacao;
	private String iconeBotaoInternacao;
	
	private boolean previsaoDeAltaNasProximasHoras;
    private String  descPrevisaoDeAltaNasProximasHoras;
    
    private BigInteger nroCartaoSaude;

    private boolean pacienteNotifGMR;
    private String descPacienteNotifGMR;
    
    public VAinCensoVO(){
		
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getUnfMaeSeq() {
		return unfMaeSeq;
	}

	public void setUnfMaeSeq(Short unfMaeSeq) {
		this.unfMaeSeq = unfMaeSeq;
	}

	public String getQrtoLto() {
		return qrtoLto;
	}

	public void setQrtoLto(String qrtoLto) {
		this.qrtoLto = qrtoLto;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getNomeSituacao() {
		return nomeSituacao;
	}

	public void setNomeSituacao(String nomeSituacao) {
		this.nomeSituacao = nomeSituacao;
	}

	public boolean isOrigemNomeSituacao() {
		return origemNomeSituacao;
	}

	public void setOrigemNomeSituacao(boolean origemNomeSituacao) {
		this.origemNomeSituacao = origemNomeSituacao;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public String getSiglaEsp() {
		return siglaEsp;
	}

	public void setSiglaEsp(String siglaEsp) {
		this.siglaEsp = siglaEsp;
	}

	public String getTamCodigo() {
		return tamCodigo;
	}

	public void setTamCodigo(String tamCodigo) {
		this.tamCodigo = tamCodigo;
	}

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public Date getDthrLancamento() {
		return dthrLancamento;
	}

	public void setDthrLancamento(Date dthrLancamento) {
		this.dthrLancamento = dthrLancamento;
	}

	public Date getDthrLancamentoFinal() {
		return dthrLancamentoFinal;
	}

	public void setDthrLancamentoFinal(Date dthrLancamentoFinal) {
		this.dthrLancamentoFinal = dthrLancamentoFinal;
	}

	public Integer getInternacaoSeq() {
		return internacaoSeq;
	}

	public void setInternacaoSeq(Integer internacaoSeq) {
		this.internacaoSeq = internacaoSeq;
	}

	public DominioSituacaoUnidadeFuncional getStatus() {
		return status;
	}

	public void setStatus(DominioSituacaoUnidadeFuncional status) {
		this.status = status;
	}

	public DominioMovimentoLeito getGrupoMvtoLeito() {
		return grupoMvtoLeito;
	}

	public void setGrupoMvtoLeito(DominioMovimentoLeito grupoMvtoLeito) {
		this.grupoMvtoLeito = grupoMvtoLeito;
	}

	public DominioTipoCensoDiarioPacientes getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoCensoDiarioPacientes tipo) {
		this.tipo = tipo;
	}

	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Date getDtNascPaciente() {
		return dtNascPaciente;
	}

	public void setDtNascPaciente(Date dtNascPAciente) {
		this.dtNascPaciente = dtNascPAciente;
	}

	public String getDescConvenio() {
		return descConvenio;
	}

	public void setDescConvenio(String descConvenio) {
		this.descConvenio = descConvenio;
	}

	public String getEstadoSaude() {
		return estadoSaude;
	}

	public void setEstadoSaude(String estadoSaude) {
		this.estadoSaude = estadoSaude;
	}
	
	public Integer getEstiloColunaTempo() {
		return estiloColunaTempo;
	}

	public void setEstiloColunaTempo(Integer estiloColunaTempo) {
		this.estiloColunaTempo = estiloColunaTempo;
	}

	public boolean isEstiloColunaProntuario() {
		return estiloColunaProntuario;
	}

	public void setEstiloColunaProntuario(boolean estiloColunaProntuario) {
		this.estiloColunaProntuario = estiloColunaProntuario;
	}

	public boolean isEstiloColunaQrto() {
		return estiloColunaQrto;
	}

	public void setEstiloColunaQrto(boolean estiloColunaQrto) {
		this.estiloColunaQrto = estiloColunaQrto;
	}

	public Integer getTempo() {
		return tempo;
	}

	public void setTempo(Integer tempo) {
		this.tempo = tempo;
	}

	public boolean isExibirBotaoCadastroDePacientes() {
		return exibirBotaoCadastroDePacientes;
	}

	public void setExibirBotaoCadastroDePacientes(
			boolean exibirBotaoCadastroDePacientes) {
		this.exibirBotaoCadastroDePacientes = exibirBotaoCadastroDePacientes;
	}

	public boolean isExibirBotaoExtrato() {
		return exibirBotaoExtrato;
	}

	public void setExibirBotaoExtrato(boolean exibirBotaoExtrato) {
		this.exibirBotaoExtrato = exibirBotaoExtrato;
	}

	public boolean isExibirBotaoTransferencia() {
		return exibirBotaoTransferencia;
	}

	public void setExibirBotaoTransferencia(boolean exibirBotaoTransferencia) {
		this.exibirBotaoTransferencia = exibirBotaoTransferencia;
	}

	public boolean isExibirBotaoInternacao() {
		return exibirBotaoInternacao;
	}

	public void setExibirBotaoInternacao(boolean exibirBotaoInternacao) {
		this.exibirBotaoInternacao = exibirBotaoInternacao;
	}

	public String getLabelBotaoInternacao() {
		return labelBotaoInternacao;
	}

	public void setLabelBotaoInternacao(String labelBotaoInternacao) {
		this.labelBotaoInternacao = labelBotaoInternacao;
	}

	public String getIconeBotaoInternacao() {
		return iconeBotaoInternacao;
	}

	public void setIconeBotaoInternacao(String iconeBotaoInternacao) {
		this.iconeBotaoInternacao = iconeBotaoInternacao;
	}

	public boolean isExibirBotaoAlta() {
		return exibirBotaoAlta;
	}

	public void setExibirBotaoAlta(boolean exibirBotaoAlta) {
		this.exibirBotaoAlta = exibirBotaoAlta;
	}

	public String getTamDescricao() {
		return tamDescricao;
	}

	public void setTamDescricao(String tamDescricao) {
		this.tamDescricao = tamDescricao;
	}
	
	public String getDescricaoEsp() {
		return descricaoEsp;
	}

	public void setDescricaoEsp(String descricaoEsp) {
		this.descricaoEsp = descricaoEsp;
	}

	public String getDescConvenioAbreviada(){
		return StringUtils.abbreviate(this.descConvenio, 12);
	}

	public String getNomeMedicoAbreviado() {
		return StringUtils.abbreviate(this.nomeMedico, 12);
	}
	
	public Integer getSeqExtrato() {
		return seqExtrato;
	}

	public void setSeqExtrato(Integer seqExtrato) {
		this.seqExtrato = seqExtrato;
	}

	public boolean isPrevisaoDeAltaNasProximasHoras() {
		return previsaoDeAltaNasProximasHoras;
	}

	public void setPrevisaoDeAltaNasProximasHoras(
			boolean previsaoDeAltaNasProximasHoras) {
		this.previsaoDeAltaNasProximasHoras = previsaoDeAltaNasProximasHoras;
	}

	public String getDescPrevisaoDeAltaNasProximasHoras() {
		return descPrevisaoDeAltaNasProximasHoras;
	}

	public void setDescPrevisaoDeAltaNasProximasHoras(
			String descPrevisaoDeAltaNasProximasHoras) {
		this.descPrevisaoDeAltaNasProximasHoras = descPrevisaoDeAltaNasProximasHoras;
	}
	
	
	public BigInteger getNroCartaoSaude() {
		return nroCartaoSaude;
	}

	public void setNroCartaoSaude(BigInteger nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}

	public boolean isPacienteNotifGMR() {
		return pacienteNotifGMR;
	}

	public void setPacienteNotifGMR(boolean pacienteNotifGMR) {
		this.pacienteNotifGMR = pacienteNotifGMR;
	}

	public String getDescPacienteNotifGMR() {
		return descPacienteNotifGMR;
	}

	public void setDescPacienteNotifGMR(String descPacienteNotifGMR) {
		this.descPacienteNotifGMR = descPacienteNotifGMR;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((qrtoLto == null) ? 0 : qrtoLto.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		VAinCensoVO other = (VAinCensoVO) obj;
		if (qrtoLto == null) {
			if (other.qrtoLto != null){
				return false;
			}
		} else if (!qrtoLto.equals(other.qrtoLto)){
			return false;
		}else if(qrtoLto.equals(other.qrtoLto)){
			if(prontuario != null && other.prontuario != null
			&& !prontuario.equals(other.prontuario)){
				return false;
			}
		}
		return true;
	}
	
}
