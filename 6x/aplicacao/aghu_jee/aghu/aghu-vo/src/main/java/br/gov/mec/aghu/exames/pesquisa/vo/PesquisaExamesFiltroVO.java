package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioRestricaoUsuario;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;



public class PesquisaExamesFiltroVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6695110326710424110L;
	private RapServidores servidorPac;
	private AelAgrpPesquisas agrpPesquisaPac;
	private String nomePacientePac;
	private Integer prontuarioPac;
	private Integer codigoPac;
	private AghUnidadesFuncionais aelUnffuncionalPac;
	private AinLeitos leitoPac;
	private Integer consultaPac;
	private String infClinicas;
	
	//Solicitantes
	private RapConselhosProfissionais conselhoSolic;
	private String numeroConselhoSolic;
	private RapServidores servidorSolic;
	private String nomeSolicitante;
	private AghEspecialidades especialidade;
	
	//Info complementar
	private Integer numeroSolicitacaoInfo;
	private DominioSimNao indMostraCanceladosInfo = DominioSimNao.N;
	private DominioOrigemAtendimento origemAtendimentoInfo = null;
	private VAelExamesSolicitacao exameSolicitacaoInfo;
	private AghUnidadesFuncionais aelUnfExecutoraInfo;
	private AelConfigExLaudoUnico configExame;
	private Long numeroAp;
	private Long numeroProtocoloEntregaExames;
	
	//Restrição
	private DominioRestricaoUsuario restricao = DominioRestricaoUsuario.TD;

	//usuário logado
	private RapServidores servidorLogado;

	//Parametro P_SITUACAO_PENDENTE
	private String pSituacaoPendente;

	//Parametro P_SITUACAO_CANCELADO
	private String pSituacaoCancelado;


	/**
	 * Esta preenchido se algum dos atributos não for nulo ou vazio.
	 * @return
	 */
	public boolean isPreenchido() {
		return (
				this.getProntuarioPac() != null
				|| this.getNumeroSolicitacaoInfo() != null
				|| this.getConsultaPac() != null
				|| this.getOrigemAtendimentoInfo() != null
		);
	}

	/**
	 * Preenchimento minimo para a consulta
	 * @return
	 */
	public boolean isPreenchidoFiltroMinimo() {
		return (
				this.getProntuarioPac() != null
				|| this.getNumeroSolicitacaoInfo() != null 
				|| this.getConsultaPac() != null);
	}


	public String getNomeSolicitante() {
		return nomeSolicitante;
	}

	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}

	public String getInfClinicas() {
		return infClinicas;
	}

	public void setInfClinicas(String infClinicas) {
		this.infClinicas = infClinicas;
	}
	public RapServidores getServidorPac() {
		return servidorPac;
	}
	public void setServidorPac(RapServidores servidorPac) {
		this.servidorPac = servidorPac;
	}
	public AelAgrpPesquisas getAgrpPesquisaPac() {
		return agrpPesquisaPac;
	}
	public void setAgrpPesquisaPac(AelAgrpPesquisas agrpPesquisaPac) {
		this.agrpPesquisaPac = agrpPesquisaPac;
	}
	public String getNomePacientePac() {
		return nomePacientePac;
	}
	public void setNomePacientePac(String nomePacientePac) {
		this.nomePacientePac = nomePacientePac;
	}
	public Integer getProntuarioPac() {
		return prontuarioPac;
	}
	public void setProntuarioPac(Integer prontuarioPac) {
		this.prontuarioPac = prontuarioPac;
	}
	public AghUnidadesFuncionais getAelUnffuncionalPac() {
		return aelUnffuncionalPac;
	}
	public void setAelUnffuncionalPac(AghUnidadesFuncionais aelUnffuncionalPac) {
		this.aelUnffuncionalPac = aelUnffuncionalPac;
	}
	public AinLeitos getLeitoPac() {
		return leitoPac;
	}
	public void setLeitoPac(AinLeitos leitoPac) {
		this.leitoPac = leitoPac;
	}
	public Integer getConsultaPac() {
		return consultaPac;
	}
	public void setConsultaPac(Integer consultaPac) {
		this.consultaPac = consultaPac;
	}
	public RapConselhosProfissionais getConselhoSolic() {
		return conselhoSolic;
	}
	public void setConselhoSolic(RapConselhosProfissionais conselhoSolic) {
		this.conselhoSolic = conselhoSolic;
	}
	public String getNumeroConselhoSolic() {
		return numeroConselhoSolic;
	}
	public void setNumeroConselhoSolic(String numeroConselhoSolic) {
		this.numeroConselhoSolic = numeroConselhoSolic;
	}
	public RapServidores getServidorSolic() {
		return servidorSolic;
	}
	public void setServidorSolic(RapServidores servidorSolic) {
		this.servidorSolic = servidorSolic;
	}
	public Integer getNumeroSolicitacaoInfo() {
		return numeroSolicitacaoInfo;
	}
	public void setNumeroSolicitacaoInfo(Integer numeroSolicitacaoInfo) {
		this.numeroSolicitacaoInfo = numeroSolicitacaoInfo;
	}
	public DominioSimNao getIndMostraCanceladosInfo() {
		return indMostraCanceladosInfo;
	}
	public void setIndMostraCanceladosInfo(DominioSimNao indMostraCanceladosInfo) {
		this.indMostraCanceladosInfo = indMostraCanceladosInfo;
	}
	public DominioOrigemAtendimento getOrigemAtendimentoInfo() {
		return origemAtendimentoInfo;
	}
	public void setOrigemAtendimentoInfo(
			DominioOrigemAtendimento origemAtendimentoInfo) {
		this.origemAtendimentoInfo = origemAtendimentoInfo;
	}
	public VAelExamesSolicitacao getExameSolicitacaoInfo() {
		return exameSolicitacaoInfo;
	}
	public void setExameSolicitacaoInfo(VAelExamesSolicitacao exameSolicitacaoInfo) {
		this.exameSolicitacaoInfo = exameSolicitacaoInfo;
	}
	public AghUnidadesFuncionais getAelUnfExecutoraInfo() {
		return aelUnfExecutoraInfo;
	}
	public void setAelUnfExecutoraInfo(AghUnidadesFuncionais aelUnfExecutoraInfo) {
		this.aelUnfExecutoraInfo = aelUnfExecutoraInfo;
	}
	public DominioRestricaoUsuario getRestricao() {
		return restricao;
	}
	public void setRestricao(DominioRestricaoUsuario restricao) {
		this.restricao = restricao;
	}
	public RapServidores getServidorLogado() {
		return servidorLogado;
	}
	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AelConfigExLaudoUnico getConfigExame() {
		return configExame;
	}

	public void setConfigExame(AelConfigExLaudoUnico configExame) {
		this.configExame = configExame;
	}

	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	public String getpSituacaoPendente() {
		return pSituacaoPendente;
	}

	public void setpSituacaoPendente(String pSituacaoPendente) {
		this.pSituacaoPendente = pSituacaoPendente;
	}

	public String getpSituacaoCancelado() {
		return pSituacaoCancelado;
	}

	public void setpSituacaoCancelado(String pSituacaoCancelado) {
		this.pSituacaoCancelado = pSituacaoCancelado;
	}

	public Integer getCodigoPac() {
		return codigoPac;
	}

	public void setCodigoPac(Integer codigoPac) {
		this.codigoPac = codigoPac;
	}

	public Long getNumeroProtocoloEntregaExames() {
		return numeroProtocoloEntregaExames;
	}

	public void setNumeroProtocoloEntregaExames(Long numeroProtocoloEntregaExames) {
		this.numeroProtocoloEntregaExames = numeroProtocoloEntregaExames;
	}


	
	
}