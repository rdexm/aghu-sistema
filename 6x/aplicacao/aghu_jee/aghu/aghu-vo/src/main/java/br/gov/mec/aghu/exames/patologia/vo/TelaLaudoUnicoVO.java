package br.gov.mec.aghu.exames.patologia.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExameAp;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class TelaLaudoUnicoVO implements Serializable {

	private static final long serialVersionUID = -4098211678416139576L;
	
	private AelExameAp aelExameAp;
	private AelAnatomoPatologico aelAnatomoPatologico;

	private Long numeroAp;
	private String prontuario;
	private String nomePaciente;
	private String convenioPlano;
	private boolean numeroApAlterado = false;

	private boolean assinarLaudo;
	private boolean stAssinaLaudo;
	private boolean stCancelaEst;
	private boolean stCancelaDept;
	
	private boolean permitirEdicaoCID;
	private boolean permitirEdicaoTopografia;

	// Campos/Botões Aba Laudo

	private String exame;
	private String etapasLaudo;
	private String situacao;
	private AelConfigExLaudoUnico configExame;
	private AelAnatomoPatologico aelAnatomoPatologicoOrigem;

	private boolean stInformacaoClinica;
	private boolean stSalvarInfClinica;

	private boolean stKitMaterial;
	private boolean stDescricaoMaterial;
	private boolean stSalvarMaterial;

	private boolean stDescricaoMaterialLaudo;
	private boolean stConcluirDescricaoMaterialLaudo;
	private boolean stReabrirDescricaoMaterialLaudo;
	private boolean concluirDescricaoMaterialLaudo;

	private boolean stMacroscopia;
	private boolean stConcluirMacro;
	private boolean stReabrirMacro;
	private boolean concluirMacro;

	private boolean stNeoplasiaMaligna;
	private boolean stBiopsia;
	private boolean stMargemComprometida;

	private boolean stDiagnostico;
	private boolean stModalDiagnostico;
	private boolean concluirDiagnostico;
	private boolean stConcluirDiagnostico;
	private boolean stReabrirDiagnostico;

	// Campos/Botões Aba Cadastro
	private boolean stTopografia;
	private boolean stNomenclatura;
	private boolean stAelPatologista;

	// Campos/Botões Aba Indice de Blocos
	private boolean stAelKitIndiceBloco;
	private boolean stIndiceBloco;
	private boolean stSalvarIndiceBloco;

	// Campos/Botões Aba Indice de Blocos
	private boolean stDtLamina;
	private boolean stCesto;
	private boolean stNroCapsulas;
	private boolean stNroFragmentos;
	private boolean stDsLamina;
	private boolean stTextoPadraoColoracs;
	private boolean stSalvarlamina;

	// Campos/Botões Aba Nota Adicional
	private boolean stTextoNotaAdicional;
	private boolean stSalvarNotaAdicional;

	/** Flag para controle da apresentacao da aba Laudo */
	private boolean renderLaudo;

	/** Flag para controle da apresentacao da aba Cadastro */
	private boolean renderCadastro;

	/** Flag para controle da apresentacao da aba Índice de Blocos */
	private boolean renderIndiceBloco;

	/** Flag para controle da apresentacao da aba Lâminas */
	private boolean renderLamina;

	/** Flag para controle da apresentacao da aba Imagens */
	private boolean renderImagem;

	/** Flag para controle da apresentacao da aba Notas Adicionais */
	private boolean renderNotaAdicional;

	/** Flag para controle da apresentacao da aba Conclusão */
	private boolean renderConclusao;

	private Integer selectedTab;
	
	private boolean laudoAssinado;
	
	/**
	 * Campos para #21585 – Adaptar Iniciar/Preencher laudo único
	 */
	private boolean exibeInformacoesClinicas;
	private boolean exibeDescricaoMaterial;
	private boolean exibeMacroscopia;
	private boolean exibeCombosDiagnostico;
	private boolean obrigCombosDiagnostico;
	private boolean exibeDescricaoDiagnostico;
	private boolean exibeTopografias;
	private boolean exibeDiagnosticos;
	private boolean exibeIndiceBlocos;
	
	public void onOff(final boolean situacao){
		assinarLaudo = situacao;
		stAssinaLaudo= situacao;
		stCancelaEst= situacao;
		stCancelaDept= situacao;
		
		onOffAbaLaudo(situacao);
		onOffAbaCadastro(situacao);
		onOffAbaIndiceBloco(situacao);
		onOffAbaLamina(situacao);
		onOffNotaAdicional(situacao);
	}

	public void onOffAbaLaudo(final boolean situacao) {
		stInformacaoClinica = situacao;
		stSalvarInfClinica = situacao;

		stKitMaterial = situacao;
		stDescricaoMaterial = situacao;
		stSalvarMaterial = situacao;
		
		stDescricaoMaterialLaudo = situacao;
		stMacroscopia = situacao;
		stConcluirMacro = situacao;
		stReabrirMacro = situacao;
		concluirMacro = situacao;

		stNeoplasiaMaligna = situacao;
		stBiopsia = situacao;
		stMargemComprometida = situacao;

		stDiagnostico = situacao;
		stModalDiagnostico = situacao;
		concluirDiagnostico = situacao;
		stConcluirDiagnostico = situacao;
		stReabrirDiagnostico = situacao;
	}

	public void onOffAbaCadastro(final boolean situacao) {
		stTopografia = situacao;
		stNomenclatura = situacao;
		stAelPatologista = situacao;
	}

	public void onOffAbaIndiceBloco(final boolean situacao) {
		stAelKitIndiceBloco = situacao;
		stIndiceBloco = situacao;
		stSalvarIndiceBloco = situacao;
	}

	public void onOffAbaLamina(final boolean situacao) {
		stDtLamina = situacao;
		stCesto = situacao;
		stNroCapsulas = situacao;
		stNroFragmentos = situacao;
		stDsLamina = situacao;
		stTextoPadraoColoracs = situacao;
		stSalvarlamina = situacao;
	}

	public void onOffNotaAdicional(final boolean situacao) {
		stTextoNotaAdicional = situacao;
		stSalvarNotaAdicional = situacao;
	}

	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	public Integer getProntuarioSemBarra() {
		if (prontuario == null) {
			return null;
		}
		return Integer.parseInt(prontuario.replace("/", ""));
	}
	
	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getConvenioPlano() {
		return convenioPlano;
	}

	public void setConvenioPlano(String convenioPlano) {
		this.convenioPlano = convenioPlano;
	}

	public boolean isNumeroApAlterado() {
		return numeroApAlterado;
	}

	public void setNumeroApAlterado(boolean numeroApAlterado) {
		this.numeroApAlterado = numeroApAlterado;
	}

	public boolean isAssinarLaudo() {
		return assinarLaudo;
	}

	public void setAssinarLaudo(boolean assinarLaudo) {
		this.assinarLaudo = assinarLaudo;
	}

	public boolean isStAssinaLaudo() {
		return stAssinaLaudo;
	}

	public void setStAssinaLaudo(boolean stAssinaLaudo) {
		this.stAssinaLaudo = stAssinaLaudo;
	}

	public boolean isStCancelaEst() {
		return stCancelaEst;
	}

	public void setStCancelaEst(boolean stCancelaEst) {
		this.stCancelaEst = stCancelaEst;
	}

	public boolean isStCancelaDept() {
		return stCancelaDept;
	}

	public void setStCancelaDept(boolean stCancelaDept) {
		this.stCancelaDept = stCancelaDept;
	}

	public boolean isPermitirEdicaoCID() {
		return permitirEdicaoCID;
	}

	public void setPermitirEdicaoCID(boolean permitirEdicaoCID) {
		this.permitirEdicaoCID = permitirEdicaoCID;
	}

	public boolean isPermitirEdicaoTopografia() {
		return permitirEdicaoTopografia;
	}

	public void setPermitirEdicaoTopografia(boolean permitirEdicaoTopografia) {
		this.permitirEdicaoTopografia = permitirEdicaoTopografia;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public AelConfigExLaudoUnico getConfigExame() {
		return configExame;
	}

	public void setConfigExame(AelConfigExLaudoUnico configExame) {
		this.configExame = configExame;
	}

	public AelAnatomoPatologico getAelAnatomoPatologicoOrigem() {
		return aelAnatomoPatologicoOrigem;
	}

	public void setAelAnatomoPatologicoOrigem(AelAnatomoPatologico aelAnatomoPatologicoOrigem) {
		this.aelAnatomoPatologicoOrigem = aelAnatomoPatologicoOrigem;
	}

	public boolean isStInformacaoClinica() {
		return stInformacaoClinica;
	}

	public void setStInformacaoClinica(boolean stInformacaoClinica) {
		this.stInformacaoClinica = stInformacaoClinica;
	}

	public boolean isStSalvarInfClinica() {
		return stSalvarInfClinica;
	}

	public void setStSalvarInfClinica(boolean stSalvarInfClinica) {
		this.stSalvarInfClinica = stSalvarInfClinica;
	}

	public boolean isStKitMaterial() {
		return stKitMaterial;
	}

	public void setStKitMaterial(boolean stKitMaterial) {
		this.stKitMaterial = stKitMaterial;
	}

	public boolean isStDescricaoMaterial() {
		return stDescricaoMaterial;
	}

	public void setStDescricaoMaterial(boolean stDescricaoMaterial) {
		this.stDescricaoMaterial = stDescricaoMaterial;
	}

	public boolean isStSalvarMaterial() {
		return stSalvarMaterial;
	}

	public void setStSalvarMaterial(boolean stSalvarMaterial) {
		this.stSalvarMaterial = stSalvarMaterial;
	}

	public boolean isStDescricaoMaterialLaudo() {
		return stDescricaoMaterialLaudo;
	}

	public void setStDescricaoMaterialLaudo(boolean stDescricaoMaterialLaudo) {
		this.stDescricaoMaterialLaudo = stDescricaoMaterialLaudo;
	}
	
	public boolean isStMacroscopia() {
		return stMacroscopia;
	}

	public void setStMacroscopia(boolean stMacroscopia) {
		this.stMacroscopia = stMacroscopia;
	}

	public boolean isStConcluirMacro() {
		return stConcluirMacro;
	}

	public void setStConcluirMacro(boolean stConcluirMacro) {
		this.stConcluirMacro = stConcluirMacro;
	}

	public boolean isStReabrirMacro() {
		return stReabrirMacro;
	}

	public void setStReabrirMacro(boolean stReabrirMacro) {
		this.stReabrirMacro = stReabrirMacro;
	}

	public boolean isConcluirMacro() {
		return concluirMacro;
	}

	public void setConcluirMacro(boolean concluirMacro) {
		this.concluirMacro = concluirMacro;
	}

	public boolean isStNeoplasiaMaligna() {
		return stNeoplasiaMaligna;
	}

	public void setStNeoplasiaMaligna(boolean stNeoplasiaMaligna) {
		this.stNeoplasiaMaligna = stNeoplasiaMaligna;
	}

	public boolean isStBiopsia() {
		return stBiopsia;
	}

	public void setStBiopsia(boolean stBiopsia) {
		this.stBiopsia = stBiopsia;
	}

	public boolean isStMargemComprometida() {
		return stMargemComprometida;
	}

	public void setStMargemComprometida(boolean stMargemComprometida) {
		this.stMargemComprometida = stMargemComprometida;
	}

	public boolean isStDiagnostico() {
		return stDiagnostico;
	}

	public void setStDiagnostico(boolean stDiagnostico) {
		this.stDiagnostico = stDiagnostico;
	}

	public boolean isStModalDiagnostico() {
		return stModalDiagnostico;
	}

	public void setStModalDiagnostico(boolean stModalDiagnostico) {
		this.stModalDiagnostico = stModalDiagnostico;
	}

	public boolean isConcluirDiagnostico() {
		return concluirDiagnostico;
	}

	public void setConcluirDiagnostico(boolean concluirDiagnostico) {
		this.concluirDiagnostico = concluirDiagnostico;
	}

	public boolean isStConcluirDiagnostico() {
		return stConcluirDiagnostico;
	}

	public void setStConcluirDiagnostico(boolean stConcluirDiagnostico) {
		this.stConcluirDiagnostico = stConcluirDiagnostico;
	}

	public boolean isStReabrirDiagnostico() {
		return stReabrirDiagnostico;
	}

	public void setStReabrirDiagnostico(boolean stReabrirDiagnostico) {
		this.stReabrirDiagnostico = stReabrirDiagnostico;
	}

	public boolean isStTopografia() {
		return stTopografia;
	}

	public void setStTopografia(boolean stTopografia) {
		this.stTopografia = stTopografia;
	}

	public boolean isStNomenclatura() {
		return stNomenclatura;
	}

	public void setStNomenclatura(boolean stNomenclatura) {
		this.stNomenclatura = stNomenclatura;
	}

	public boolean isStAelPatologista() {
		return stAelPatologista;
	}

	public void setStAelPatologista(boolean stAelPatologista) {
		this.stAelPatologista = stAelPatologista;
	}

	public boolean isStAelKitIndiceBloco() {
		return stAelKitIndiceBloco;
	}

	public void setStAelKitIndiceBloco(boolean stAelKitIndiceBloco) {
		this.stAelKitIndiceBloco = stAelKitIndiceBloco;
	}

	public boolean isStIndiceBloco() {
		return stIndiceBloco;
	}

	public void setStIndiceBloco(boolean stIndiceBloco) {
		this.stIndiceBloco = stIndiceBloco;
	}

	public boolean isStSalvarIndiceBloco() {
		return stSalvarIndiceBloco;
	}

	public void setStSalvarIndiceBloco(boolean stSalvarIndiceBloco) {
		this.stSalvarIndiceBloco = stSalvarIndiceBloco;
	}

	public boolean isStDtLamina() {
		return stDtLamina;
	}

	public void setStDtLamina(boolean stDtLamina) {
		this.stDtLamina = stDtLamina;
	}

	public boolean isStCesto() {
		return stCesto;
	}

	public void setStCesto(boolean stCesto) {
		this.stCesto = stCesto;
	}

	public boolean isStNroCapsulas() {
		return stNroCapsulas;
	}

	public void setStNroCapsulas(boolean stNroCapsulas) {
		this.stNroCapsulas = stNroCapsulas;
	}

	public boolean isStNroFragmentos() {
		return stNroFragmentos;
	}

	public void setStNroFragmentos(boolean stNroFragmentos) {
		this.stNroFragmentos = stNroFragmentos;
	}

	public boolean isStDsLamina() {
		return stDsLamina;
	}

	public void setStDsLamina(boolean stDsLamina) {
		this.stDsLamina = stDsLamina;
	}

	public boolean isStTextoPadraoColoracs() {
		return stTextoPadraoColoracs;
	}

	public void setStTextoPadraoColoracs(boolean stTextoPadraoColoracs) {
		this.stTextoPadraoColoracs = stTextoPadraoColoracs;
	}

	public boolean isStSalvarlamina() {
		return stSalvarlamina;
	}

	public void setStSalvarlamina(boolean stSalvarlamina) {
		this.stSalvarlamina = stSalvarlamina;
	}

	public boolean isStTextoNotaAdicional() {
		return stTextoNotaAdicional;
	}

	public void setStTextoNotaAdicional(boolean stTextoNotaAdicional) {
		this.stTextoNotaAdicional = stTextoNotaAdicional;
	}

	public boolean isStSalvarNotaAdicional() {
		return stSalvarNotaAdicional;
	}

	public void setStSalvarNotaAdicional(boolean stSalvarNotaAdicional) {
		this.stSalvarNotaAdicional = stSalvarNotaAdicional;
	}

	public boolean isRenderLaudo() {
		return renderLaudo;
	}

	public void setRenderLaudo(boolean renderLaudo) {
		this.renderLaudo = renderLaudo;
	}

	public boolean isRenderCadastro() {
		return renderCadastro;
	}

	public void setRenderCadastro(boolean renderCadastro) {
		this.renderCadastro = renderCadastro;
	}

	public boolean isRenderIndiceBloco() {
		return renderIndiceBloco;
	}

	public void setRenderIndiceBloco(boolean renderIndiceBloco) {
		this.renderIndiceBloco = renderIndiceBloco;
	}

	public boolean isRenderLamina() {
		return renderLamina;
	}

	public void setRenderLamina(boolean renderLamina) {
		this.renderLamina = renderLamina;
	}

	public boolean isRenderImagem() {
		return renderImagem;
	}

	public void setRenderImagem(boolean renderImagem) {
		this.renderImagem = renderImagem;
	}

	public boolean isRenderNotaAdicional() {
		return renderNotaAdicional;
	}

	public void setRenderNotaAdicional(boolean renderNotaAdicional) {
		this.renderNotaAdicional = renderNotaAdicional;
	}

	public boolean isRenderConclusao() {
		return renderConclusao;
	}

	public void setRenderConclusao(boolean renderConclusao) {
		this.renderConclusao = renderConclusao;
	}

	public Integer getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}

	public AelExameAp getAelExameAp() {
		return aelExameAp;
	}

	public void setAelExameAp(AelExameAp aelExameAp) {
		this.aelExameAp = aelExameAp;
	}

	public AelAnatomoPatologico getAelAnatomoPatologico() {
		return aelAnatomoPatologico;
	}

	public void setAelAnatomoPatologico(AelAnatomoPatologico aelAnatomoPatologico) {
		this.aelAnatomoPatologico = aelAnatomoPatologico;
	}

	public String getEtapasLaudo() {
		return etapasLaudo;
	}

	public void setEtapasLaudo(String etapasLaudo) {
		this.etapasLaudo = etapasLaudo;
	}

	public boolean isConcluirDescricaoMaterialLaudo() {
		return concluirDescricaoMaterialLaudo;
	}

	public void setConcluirDescricaoMaterialLaudo(boolean concluirDescricaoMaterialLaudo) {
		this.concluirDescricaoMaterialLaudo = concluirDescricaoMaterialLaudo;
	}

	public boolean isStConcluirDescricaoMaterialLaudo() {
		return stConcluirDescricaoMaterialLaudo;
	}

	public void setStConcluirDescricaoMaterialLaudo(boolean stConcluirDescricaoMaterialLaudo) {
		this.stConcluirDescricaoMaterialLaudo = stConcluirDescricaoMaterialLaudo;
	}

	public boolean isStReabrirDescricaoMaterialLaudo() {
		return stReabrirDescricaoMaterialLaudo;
	}

	public void setStReabrirDescricaoMaterialLaudo(boolean stReabrirDescricaoMaterialLaudo) {
		this.stReabrirDescricaoMaterialLaudo = stReabrirDescricaoMaterialLaudo;
	}

	public boolean isLaudoAssinado() {
		return laudoAssinado;
	}

	public void setLaudoAssinado(boolean laudoAssinado) {
		this.laudoAssinado = laudoAssinado;
	}

	public boolean isExibeInformacoesClinicas() {
		return exibeInformacoesClinicas;
	}

	public void setExibeInformacoesClinicas(boolean exibeInformacoesClinicas) {
		this.exibeInformacoesClinicas = exibeInformacoesClinicas;
	}

	public boolean isExibeDescricaoMaterial() {
		return exibeDescricaoMaterial;
	}

	public void setExibeDescricaoMaterial(boolean exibeDescricaoMaterial) {
		this.exibeDescricaoMaterial = exibeDescricaoMaterial;
	}

	public boolean isExibeMacroscopia() {
		return exibeMacroscopia;
	}

	public void setExibeMacroscopia(boolean exibeMacroscopia) {
		this.exibeMacroscopia = exibeMacroscopia;
	}

	public boolean isExibeCombosDiagnostico() {
		return exibeCombosDiagnostico;
	}

	public void setExibeCombosDiagnostico(boolean exibeCombosDiagnostico) {
		this.exibeCombosDiagnostico = exibeCombosDiagnostico;
	}

	public boolean isObrigCombosDiagnostico() {
		return obrigCombosDiagnostico;
	}

	public void setObrigCombosDiagnostico(boolean obrigCombosDiagnostico) {
		this.obrigCombosDiagnostico = obrigCombosDiagnostico;
	}

	public boolean isExibeDescricaoDiagnostico() {
		return exibeDescricaoDiagnostico;
	}

	public void setExibeDescricaoDiagnostico(boolean exibeDescricaoDiagnostico) {
		this.exibeDescricaoDiagnostico = exibeDescricaoDiagnostico;
	}

	public boolean isExibeTopografias() {
		return exibeTopografias;
	}

	public void setExibeTopografias(boolean exibeTopografias) {
		this.exibeTopografias = exibeTopografias;
	}

	public boolean isExibeDiagnosticos() {
		return exibeDiagnosticos;
	}

	public void setExibeDiagnosticos(boolean exibeDiagnosticos) {
		this.exibeDiagnosticos = exibeDiagnosticos;
	}

	public boolean isExibeIndiceBlocos() {
		return exibeIndiceBlocos;
	}

	public void setExibeIndiceBlocos(boolean exibeIndiceBlocos) {
		this.exibeIndiceBlocos = exibeIndiceBlocos;
	}
	
}