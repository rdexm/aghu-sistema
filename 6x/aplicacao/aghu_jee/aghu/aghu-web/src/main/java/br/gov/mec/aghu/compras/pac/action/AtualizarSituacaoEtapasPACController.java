package br.gov.mec.aghu.compras.pac.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.EtapasRelacionadasPacVO;
import br.gov.mec.aghu.compras.vo.LocalPACVO;
import br.gov.mec.aghu.dominio.DominioSituacaoEtapaPac;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class AtualizarSituacaoEtapasPACController extends ActionController {
	private static final long serialVersionUID = 134567893241134L;
	
	private static final String LABEL_BOTAO_CANCELAR = "Cancelar";
	private static final String LABEL_BOTAO_VOLTAR = "Voltar";
	private static final String ENCAMINHAR_PAC = "andamentoPacList";
	
	public enum AtualizarSituacaoEtapasPACControllerExceptionCode {
		ETAPA_ATUALIZADA_SUCESSO
	}
	
	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	//paramentros
	private ScoLicitacao filtroPAC;
	private String localidadeAtual; 
	private List<String> historicoNesteLocal = new ArrayList<String>();
	private String tempoTotal; 
	private String historicoNesteLocalSelecionado;
	private LocalPACVO filtroPACLocal; 
	private boolean pesquisaAtiva;
	private List<EtapasRelacionadasPacVO> listaEtapasRelacionadas;
	private EtapasRelacionadasPacVO editarEtapaRelacionamento;
	private DominioSituacaoEtapaPac situacaoEtapaPAC;
	private DominioSituacaoEtapaPac novoDominioSituacaoEtapaPAC;
	private String novaObservacao;
	private String labelBotao = LABEL_BOTAO_VOLTAR;
	//paramentro de inicio de pagina
	private Integer licitacaoId;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	
	public void iniciar(){
		if(licitacaoId != null){
			this.obterLicitacao(licitacaoId);
		}
	}
	
	/**
	 * suggestion: lista de licitacoes
	 * @param param
	 * @return List<ScoLicitacao>
	 */
	public List<ScoLicitacao> pesquisarLicitacao(String param) {
		return pacFacade.pesquisarLicitacoesPorNumeroDescricao(param);
	}
	
	/**
	 * suggestion: quantidade total de licitacoes
	 * @param param
	 * @return Long
	 */
	public Long pesquisarLicitacaoCount(String param){
		return pacFacade.pesquisarLicitacoesPorNumeroDescricaoCount(param);
	}
	
	public void definirCamposTela(){
		if(filtroPAC != null){
			setLocalidadeAtual(pacFacade.obterLocalidadeAtualPACPorNumLicitacao(filtroPAC.getNumero()));
			setHistoricoNesteLocal(pacFacade.listarHistoricoEtapas(filtroPAC.getNumero()));
			setTempoTotal(pacFacade.obterTempoTotal(filtroPAC.getNumero()));
		}
	}
	
	public void obterLicitacao(Integer numeroLicitacao){
		limparDados();
		filtroPAC = pacFacade.obterLicitacao(numeroLicitacao);
		this.definirCamposTela();
	}
	
	public void limparDados(){ 
		setFiltroPAC(null);
		setLocalidadeAtual(null);
		setHistoricoNesteLocal(new ArrayList<String>());
		setTempoTotal(null);
		setFiltroPACLocal(null);
		setPesquisaAtiva(Boolean.FALSE);
		setListaEtapasRelacionadas(new ArrayList<EtapasRelacionadasPacVO>());
	}
	
	/**
	 * quantidade total de locais do PAC, conforme PAC selecionado (suggestion: lista de licitacoes)
	 * 
	 * @param param
	 * @return Long
	 */
	public Long pesquisarLocalPACPorNumeroDescricaoCount(String param){
		Integer numeroLicitacao = filtroPAC != null ? filtroPAC.getNumero() : null;
		String codigoLocalidade = filtroPAC != null && filtroPAC.getModalidadeLicitacao() != null ? filtroPAC.getModalidadeLicitacao().getCodigo() : null;
		return pacFacade.pesquisarLocalPACPorNumeroDescricaoCount(param, numeroLicitacao, codigoLocalidade);
	}
	
	/**
	 * suggestion: Lista todos os locais do PAC, conforme PAC selecionado (suggestion: lista de licitacoes)
	 * 
	 * @param param
	 * @return Integer
	 */
	public List<LocalPACVO> pesquisarLocalPACPorNumeroDescricao(String param){
		Integer numeroLicitacao = filtroPAC != null ? filtroPAC.getNumero() : null;
		String codigoModalidade = filtroPAC != null && filtroPAC.getModalidadeLicitacao() != null ? filtroPAC.getModalidadeLicitacao().getCodigo() : null;
		return pacFacade.pesquisarLocalPACPorNumeroDescricao(param, numeroLicitacao, codigoModalidade);
	}
	
	public void pesquisar(){
		setPesquisaAtiva(true);
		Integer numeroLicitacao = filtroPAC != null ? filtroPAC.getNumero() : null;
		Short codigoLocalidade = filtroPACLocal != null ? filtroPACLocal.getCodigo() : null;
		String codigoModalidade = filtroPAC != null && filtroPAC.getModalidadeLicitacao() != null ? filtroPAC.getModalidadeLicitacao().getCodigo() : null;
		setListaEtapasRelacionadas(pacFacade.listarEtapasRelacionadasPAC(numeroLicitacao, codigoLocalidade, codigoModalidade));
	}
	
	public void editar(EtapasRelacionadasPacVO vo){

		vo = pacFacade.verificaNecessidadeSalvarEtapaPAC(vo, filtroPAC.getNumero());		
		setEditarEtapaRelacionamento(vo);
		setNovaObservacao(vo.getApontamentoUsuario());
		setNovoDominioSituacaoEtapaPAC(vo.getSituacao());
		setLabelBotao(LABEL_BOTAO_VOLTAR);
	}
	
	public void atualizarSituacaoEtapaPAC(){
		editarEtapaRelacionamento.setSituacao(novoDominioSituacaoEtapaPAC);
		editarEtapaRelacionamento.setApontamentoUsuario(novaObservacao);
		RapServidores servidor = null;
		try {
			servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		
		pacFacade.atualizarSituacaoEtapaPAC(editarEtapaRelacionamento, servidor);		
		
		pesquisar();
		
		this.apresentarMsgNegocio(Severity.INFO, AtualizarSituacaoEtapasPACControllerExceptionCode.ETAPA_ATUALIZADA_SUCESSO.toString());	
	}
	
	public SelectItem[] getHistoricoNesteLocalItens(){
		SelectItem[] itens = new SelectItem[this.historicoNesteLocal.size()]; 
 		for (int i = 0; i < this.historicoNesteLocal.size(); i++) {
 			itens[i]= new SelectItem(historicoNesteLocal.get(i));
		}
 		return itens;
	}
	
	public String encaminhar(){
		return ENCAMINHAR_PAC;
	}
	
	public void verificaAlteracaoSituacao(){
		if(editarEtapaRelacionamento != null && novoDominioSituacaoEtapaPAC != null && editarEtapaRelacionamento.getSituacao() != null && 
				!editarEtapaRelacionamento.getSituacao().getDescricao().equals(novoDominioSituacaoEtapaPAC.getDescricao())){
			setLabelBotao(LABEL_BOTAO_CANCELAR);
		}
		else if ( editarEtapaRelacionamento.getSituacao() == null && novoDominioSituacaoEtapaPAC != null){
			setLabelBotao(LABEL_BOTAO_CANCELAR);
		}
	}
	
	public void verificaAlteracaoObservacao(){
		if(editarEtapaRelacionamento != null && novaObservacao != null && editarEtapaRelacionamento.getApontamentoUsuario() != null && 
				!editarEtapaRelacionamento.getApontamentoUsuario().trim().equalsIgnoreCase(novaObservacao.trim())){
			setLabelBotao(LABEL_BOTAO_CANCELAR);
		}
		else if (editarEtapaRelacionamento.getApontamentoUsuario() == null && novaObservacao != null){
			setLabelBotao(LABEL_BOTAO_CANCELAR);
		}
	}

	public void alterarTempoTotal(){
		setTempoTotal(pacFacade.alterarTempoTotal(historicoNesteLocalSelecionado));
	}
	
	public void setHistoricoNesteLocal(List<String> historicoNesteLocal) {
		this.historicoNesteLocal = historicoNesteLocal;
	}
	
	public List<String> getHistoricoNesteLocal() {
		return historicoNesteLocal;
	}

	public void setTempoTotal(String tempoTotal) {
		this.tempoTotal = tempoTotal;
	}

	public String getTempoTotal() {
		return tempoTotal;
	}

	public void setLocalidadeAtual(String localidadeAtual) {
		this.localidadeAtual = localidadeAtual;
	}

	public String getLocalidadeAtual() {
		return localidadeAtual;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setListaEtapasRelacionadas(List<EtapasRelacionadasPacVO> listaEtapasRelacionadas) {
		this.listaEtapasRelacionadas = listaEtapasRelacionadas;
	}

	public List<EtapasRelacionadasPacVO> getListaEtapasRelacionadas() {
		return listaEtapasRelacionadas;
	}

	public void setEditarEtapaRelacionamento(EtapasRelacionadasPacVO editarEtapaRelacionamento) {
		this.editarEtapaRelacionamento = editarEtapaRelacionamento;
	}

	public EtapasRelacionadasPacVO getEditarEtapaRelacionamento() {
		return editarEtapaRelacionamento;
	}

	public ScoLicitacao getFiltroPAC() {
		return filtroPAC;
	}

	public void setFiltroPAC(ScoLicitacao filtroPAC) {
		this.filtroPAC = filtroPAC;
	}

	public LocalPACVO getFiltroPACLocal() {
		return filtroPACLocal;
	}

	public void setFiltroPACLocal(LocalPACVO filtroPACLocal) {
		this.filtroPACLocal = filtroPACLocal;
	}

	public void setSituacaoEtapaPAC(DominioSituacaoEtapaPac situacaoEtapaPAC) {
		this.situacaoEtapaPAC = situacaoEtapaPAC;
	}

	public DominioSituacaoEtapaPac getSituacaoEtapaPAC() {
		return situacaoEtapaPAC;
	}

	public void setNovoDominioSituacaoEtapaPAC(
			DominioSituacaoEtapaPac novoDominioSituacaoEtapaPAC) {
		this.novoDominioSituacaoEtapaPAC = novoDominioSituacaoEtapaPAC;
	}

	public DominioSituacaoEtapaPac getNovoDominioSituacaoEtapaPAC() {
		return novoDominioSituacaoEtapaPAC;
	}

	public void setNovaObservacao(String novaObservacao) {
		this.novaObservacao = novaObservacao;
	}

	public String getNovaObservacao() {
		return novaObservacao;
	}

	public String getLabelBotao() {
		return labelBotao;
	}

	public void setLabelBotao(String labelBotao) {
		this.labelBotao = labelBotao;
	}

	public void setHistoricoNesteLocalSelecionado(
			String historicoNesteLocalSelecionado) {
		this.historicoNesteLocalSelecionado = historicoNesteLocalSelecionado;
	}

	public String getHistoricoNesteLocalSelecionado() {
		return historicoNesteLocalSelecionado;
	}

	public void setLicitacaoId(Integer licitacaoId) {
		this.licitacaoId = licitacaoId;
	}

	public Integer getLicitacaoId() {
		return licitacaoId;
	}

}
