package br.gov.mec.aghu.paciente.prontuario.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.paciente.vo.AipMovimentacaoProntuariosVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class MovimentarProntuarioPaginatorController extends ActionController implements ActionPaginator{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AipMovimentacaoProntuariosVO> dataModel;

	private static final Log LOG = LogFactory.getLog(MovimentarProntuarioPaginatorController.class);

	private static final long serialVersionUID = -737622647179594509L;
	
	private static final String PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	
	private AghSamis origemProntuariosPesquisa;
	private DominioSituacaoMovimentoProntuario situacaoMovimentoProntuario;
	private AipPacientes paciente;
	private Boolean allChecked = Boolean.FALSE;
	
	private AipSolicitantesProntuario unidadeSolicitanteAlteracao;
	private AipSolicitantesProntuario unidadeSolicitantePesquisa;
	
	private Date dataMovimentacao = null;
	
	private Boolean habilitaAlteracaoOrigemProntuario = false;
	private Boolean habilitaBotoes = false;
	private Boolean exibirModal = false;
	private Boolean immediateRecuperarListaPaginada = Boolean.TRUE;
	
	private AghSamis origemProntuarioAlteracao;
	
	//Deixar
	private List<AipMovimentacaoProntuariosVO> listaAipMovimentacaoProntuariosVO;
	private List<AipMovimentacaoProntuariosVO> movimentacoesSelecionadasCheckbox;
	private List<AipMovimentacaoProntuariosVO> listaTodasSeqMovimentacaoBanco;
	private AipMovimentacaoProntuariosVO movimentacaoSelecionada;

	private Integer pacCodigoFonetica;

	

	public void iniciar(){		
		if (pacCodigoFonetica != null) {
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
		}		
		movimentacoesSelecionadasCheckbox = new ArrayList<AipMovimentacaoProntuariosVO>();
	}
	
	private void populaListaVODeMovimentacoes(Integer firstResult,
			Integer maxResult, String orderProperty) {
			AghUnidadesFuncionais unf = new AghUnidadesFuncionais();
		if(unidadeSolicitantePesquisa != null){
			unf = unidadeSolicitantePesquisa.getUnidadesFuncionais();
		}
		try {
			this.listaAipMovimentacaoProntuariosVO = this.pacienteFacade.pesquisaMovimentacoesDeProntuarios(firstResult, maxResult, orderProperty, 
					false, paciente, this.origemProntuariosPesquisa, unf, 
					this.situacaoMovimentoProntuario, this.dataMovimentacao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
			

		verificaSeHabilitaBotoes();
	}
	
	public void checkAll() {
		this.immediateRecuperarListaPaginada = Boolean.FALSE;
		if (allChecked) {
			setSelecionadoItens(listaAipMovimentacaoProntuariosVO, Boolean.TRUE);
			movimentacoesSelecionadasCheckbox.addAll(listaAipMovimentacaoProntuariosVO);
		}else{
			setSelecionadoItens(listaAipMovimentacaoProntuariosVO, Boolean.FALSE);
			movimentacoesSelecionadasCheckbox.removeAll(listaAipMovimentacaoProntuariosVO);
		}
	}	
	
	//Percorri lista e atribui ao atributo selecionado da vo, a opção escolhida na chamada do método
	private void setSelecionadoItens(List<AipMovimentacaoProntuariosVO> lista,Boolean isSelecionado){
		for(AipMovimentacaoProntuariosVO item : lista){
			if(!item.getSelecionado().equals(isSelecionado)){
			    item.setSelecionado(isSelecionado);
			}
		}
	}
	private void verificaSeHabilitaBotoes() {
		if(listaAipMovimentacaoProntuariosVO != null && !listaAipMovimentacaoProntuariosVO.isEmpty()){
			setHabilitaBotoes(true);
			
		} else{
			setHabilitaBotoes(false);
		}
	}
			
	@Override
	public List<AipMovimentacaoProntuariosVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		if (listaAipMovimentacaoProntuariosVO == null || 
		    listaAipMovimentacaoProntuariosVO.size() == 0 || 
		    this.immediateRecuperarListaPaginada) {
			populaListaVODeMovimentacoes(firstResult, maxResult, orderProperty);
			novaListaSelecao();
		}
		this.immediateRecuperarListaPaginada = Boolean.TRUE;
		return listaAipMovimentacaoProntuariosVO;
	}

	
	public void addSeqMovimentacao(AipMovimentacaoProntuariosVO movimentacaoPront) {
		if (movimentacoesSelecionadasCheckbox.contains(movimentacaoPront)) {
			movimentacoesSelecionadasCheckbox.remove(movimentacaoPront);
		} else {
			movimentacoesSelecionadasCheckbox.add(movimentacaoPront);
		}
		allChecked = (movimentacoesSelecionadasCheckbox.size() == listaAipMovimentacaoProntuariosVO.size());
		this.immediateRecuperarListaPaginada = Boolean.FALSE;
	}
	
		
	
	@Override
	public Long recuperarCount() {
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais();
		if(unidadeSolicitantePesquisa != null){
			unf = unidadeSolicitantePesquisa.getUnidadesFuncionais();
		}
		return pacienteFacade.pesquisaMovimentacoesDeProntuariosCount(paciente, origemProntuariosPesquisa, unf, situacaoMovimentoProntuario, 
				dataMovimentacao);
	}

	public void pesquisar(){
		this.limparCamposAlteracao();
		this.allChecked = Boolean.FALSE;
		movimentacoesSelecionadasCheckbox = null;
		movimentacoesSelecionadasCheckbox = new ArrayList<>();
		listaAipMovimentacaoProntuariosVO = null;
        if(isCamposPrenchidos()){
			this.dataModel.reiniciarPaginator();
			if(movimentacoesSelecionadasCheckbox != null){
				limparSelecao();
			}
		} else {
			this.apresentarMsgNegocio(Severity.ERROR,
					"MESSAGEM_ERRO_PREENCHA_PELO_MENOS_UM");
		}
	}

	private boolean isCamposPrenchidos() {
		return dataMovimentacao != null || situacaoMovimentoProntuario != null || origemProntuariosPesquisa != null || 
				unidadeSolicitantePesquisa != null || paciente != null; 
	}
	
	
	
	public void limparPesquisa() {
		movimentacoesSelecionadasCheckbox = null;
		movimentacoesSelecionadasCheckbox = new ArrayList<>();
		listaAipMovimentacaoProntuariosVO = null;
		this.paciente = null;
		this.situacaoMovimentoProntuario = null;
		this.origemProntuariosPesquisa = null;
		this.unidadeSolicitantePesquisa = null;
		this.dataMovimentacao = null;
		this.dataModel.setPesquisaAtiva(false);
		this.setHabilitaBotoes(false);
		this.setHabilitaAlteracaoOrigemProntuario(false);
		this.allChecked = Boolean.FALSE;
	}
	private void novaListaSelecao(){
		this.movimentacoesSelecionadasCheckbox = null;
		this.movimentacoesSelecionadasCheckbox = new ArrayList<>();
		this.allChecked = Boolean.FALSE;
	}
	public void limparSelecao() {
		if(listaAipMovimentacaoProntuariosVO != null){
		     setSelecionadoItens(listaAipMovimentacaoProntuariosVO, Boolean.FALSE);
		}
	}
	
	public void habilitarAlteracaoOrigemProntuario (){
		if( this.movimentacoesSelecionadasCheckbox != null && !this.movimentacoesSelecionadasCheckbox.isEmpty() ){
			this.habilitaAlteracaoOrigemProntuario = true;
			this.immediateRecuperarListaPaginada = Boolean.FALSE;
		}
		else{
			this.apresentarMsgSelecionePeloMenosUm();
		}
	}
	
	public String alterarOrigemProntuario(){
		if(this.movimentacoesSelecionadasCheckbox == null || this.movimentacoesSelecionadasCheckbox.isEmpty()){
			this.apresentarMsgSelecionePeloMenosUm();
			return null;
		}
		try {

			this.dataModel.reiniciarPaginator();
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			this.pacienteFacade.persistirCadastroOrigemProntuario(movimentacoesSelecionadasCheckbox, origemProntuarioAlteracao, servidorLogado);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_VINCULO_ORIGEM_PRONTUARIO",
						this.origemProntuarioAlteracao.getDescricao());
			this.immediateRecuperarListaPaginada = Boolean.TRUE;
			novaListaSelecao();
			this.listaAipMovimentacaoProntuariosVO = null;
			limparCamposAlteracao();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String movimentarProntuario () {
		try{
			this.dataModel.reiniciarPaginator();
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			this.pacienteFacade.persistirMovimentacaoParaUnidadeSolicitante(movimentacoesSelecionadasCheckbox, unidadeSolicitanteAlteracao.getUnidadesFuncionais(), servidorLogado, DominioSituacaoMovimentoProntuario.R);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_VINCULO_UNIDADE_SOLICITANTE",
						this.unidadeSolicitanteAlteracao.getDescricao());
			this.immediateRecuperarListaPaginada = Boolean.TRUE;
			novaListaSelecao();
			this.listaAipMovimentacaoProntuariosVO = null;
			limparCamposAlteracao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
		return null;
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}	
	
	
	public String devolverProntuario () {
		try{
			if( this.movimentacoesSelecionadasCheckbox == null || this.movimentacoesSelecionadasCheckbox.isEmpty() ){
				this.apresentarMsgSelecionePeloMenosUm();
				return null;
			}
			this.dataModel.reiniciarPaginator();
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			this.pacienteFacade.persistirDevolucaoDeProntuario(movimentacoesSelecionadasCheckbox, servidorLogado);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_DEVOLUCAO_PRONTUARIO");
			this.immediateRecuperarListaPaginada = Boolean.TRUE;
			novaListaSelecao();
			this.listaAipMovimentacaoProntuariosVO = null;
			limparCamposAlteracao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void cancelar() {
		limparSelecao();
		limparCamposAlteracao();
	}

	private void limparCamposAlteracao() {
		this.habilitaAlteracaoOrigemProntuario = false;
		this.origemProntuarioAlteracao = null;
		this.unidadeSolicitanteAlteracao = null;
	}
	
	public String redirecionarPesquisaFonetica(){
		return PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}
	
	public DominioSituacaoMovimentoProntuario getSituacaoMovimentoProntuario() {
		return this.situacaoMovimentoProntuario;
		
	}

	public void setSituacaoMovimentoProntuario(
			DominioSituacaoMovimentoProntuario situacaoMovimentoProntuario) {
		this.situacaoMovimentoProntuario = situacaoMovimentoProntuario;
	}

	
	
	public List<AipSolicitantesProntuario> pesquisarUnidadesSolicitantesPorCodigoOuSigla(String param) {
		return this.getPacienteFacade().pesquisarUnidadesSolicitantesPorCodigoOuSigla(param);
	}
	
	public List<AghSamis> pesquisaOrigemProntuarioPorCodigoOuDescricao(String param){
		return this.getCadastrosBasicosPacienteFacade().pesquisaOrigemProntuarioPorCodigoOuDescricao(param);
	}

	//validação do botão "movimentar prontuário" 
	public void validaMovimentacao(){
		refazerPesquisa();
		if( this.movimentacoesSelecionadasCheckbox == null || this.movimentacoesSelecionadasCheckbox.isEmpty()){
		     this.apresentarMsgSelecionePeloMenosUm();
		}else{			
			exibirModal=true;
			openDialog("modalMovimentarProntuarioWG");
			exibirMensagemErroMovimentacaoEmLote();
		}
	}
	//apresenta mensagem de erro - Selecione pelo menos um
	private void apresentarMsgSelecionePeloMenosUm(){
		this.apresentarMsgNegocio(Severity.ERROR, "MESSAGEM_ERRO_SELECIONE_PELO_MENOS_UM");
	}
	//recupera local da movimentação para popular suggestion box da modal. 
	public List<AipSolicitantesProntuario> verificaLocalParaMovimentacao(String param){
		try {
			return this.pacienteFacade.verificaLocalParaMovimentacao(movimentacoesSelecionadasCheckbox, param);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	private void exibirMensagemErroMovimentacaoEmLote(){
		for (AipMovimentacaoProntuariosVO itemSelecionado : movimentacoesSelecionadasCheckbox) {
			if( (itemSelecionado.getOrigemProntuario() == null) || isPrimeiraMovimentacao(itemSelecionado) ){
				if( !(situacaoMovimentoProntuario == null) ){
					if( !situacaoMovimentoProntuario.equals(DominioSituacaoMovimentoProntuario.Q) || unidadeSolicitantePesquisa == null){
						if(movimentacoesSelecionadasCheckbox.size() > 1){
							exibeMsgErroMovEmLoteNaPrimeiraMovimentacao();
							break;
						}
					}
				} if(situacaoMovimentoProntuario == null || unidadeSolicitantePesquisa == null){
					if(movimentacoesSelecionadasCheckbox.size() > 1){
						exibeMsgErroMovEmLoteNaPrimeiraMovimentacao();
						break;
					}
				}
			}
		}
	}

	private void exibeMsgErroMovEmLoteNaPrimeiraMovimentacao() {
		exibirModal = false;
		this.apresentarMsgNegocio(Severity.ERROR,
				"MESSAGEM_ERRO_MOVIMENTACAO_EM_LOTE_PRIMEIRA_MOVIMENTACAO");
	}

	private boolean isPrimeiraMovimentacao(
			AipMovimentacaoProntuariosVO itemSelecionado) {
		return itemSelecionado.getLocalAtual().equals(itemSelecionado.getOrigemProntuario()) && itemSelecionado.getSituacao().getDescricao().equals(DominioSituacaoMovimentoProntuario.Q.getDescricao());
	}
	
	/**
	 * Refaz pesquisa sem reiniciar o paginator, ou seja, pesquisando na
	 * mesma pagina
	 */
	public void refazerPesquisa() {
		getDataModel();
	}
	
	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public List<AipMovimentacaoProntuariosVO> getListaMovimentos() {
		return listaAipMovimentacaoProntuariosVO;
	}

	public void setListaMovimentos(List<AipMovimentacaoProntuariosVO> listaMovimentos) {
		this.listaAipMovimentacaoProntuariosVO = listaMovimentos;
	}
	
	public List<AipMovimentacaoProntuariosVO> getListaAipMovimentacaoProntuariosVO() {
		return listaAipMovimentacaoProntuariosVO;
	}

	public void setListaAipMovimentacaoProntuariosVO(
			List<AipMovimentacaoProntuariosVO> listaAipMovimentacaoProntuariosVO) {
		this.listaAipMovimentacaoProntuariosVO = listaAipMovimentacaoProntuariosVO;
	}

	public ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return cadastrosBasicosPacienteFacade;
	}

	public void setCadastrosBasicosPacienteFacade(
			ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade) {
		this.cadastrosBasicosPacienteFacade = cadastrosBasicosPacienteFacade;
	}

	public AghSamis getOrigemProntuarioAlteracao() {
		return origemProntuarioAlteracao;
	}

	public void setOrigemProntuarioAlteracao(AghSamis origensProntuarios) {
		this.origemProntuarioAlteracao = origensProntuarios;
	}

	public AghSamis getOrigemProntuariosPesquisa() {
		return origemProntuariosPesquisa;
	}

	public void setOrigemProntuariosPesquisa(AghSamis origemProntuariosPesquisa) {
		this.origemProntuariosPesquisa = origemProntuariosPesquisa;
	}
	
	public Boolean getHabilitaAlteracaoOrigemProntuario() {
		return habilitaAlteracaoOrigemProntuario;
	}

	public void setHabilitaAlteracaoOrigemProntuario(
			Boolean habilitaAlteracaoOrigemProntuario) {
		this.habilitaAlteracaoOrigemProntuario = habilitaAlteracaoOrigemProntuario;
	}

	public Boolean getHabilitaBotoes() {
		return habilitaBotoes;
	}

	public void setHabilitaBotoes(Boolean habilitaBotoes) {
		this.habilitaBotoes = habilitaBotoes;
	}

	public AipSolicitantesProntuario getUnidadeSolicitanteAlteracao() {
		return unidadeSolicitanteAlteracao;
	}

	public void setUnidadeSolicitanteAlteracao(
			AipSolicitantesProntuario unidadeSolicitanteAlteracao) {
		this.unidadeSolicitanteAlteracao = unidadeSolicitanteAlteracao;
	}

	public AipSolicitantesProntuario getUnidadeSolicitantePesquisa() {
		return unidadeSolicitantePesquisa;
	}

	public void setUnidadeSolicitantePesquisa(
			AipSolicitantesProntuario unidadeSolicitantePesquisa) {
		this.unidadeSolicitantePesquisa = unidadeSolicitantePesquisa;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Boolean getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(Boolean allChecked) {
		this.allChecked = allChecked;
	}

	public boolean isExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public Date getDataMovimentacao() {
		return dataMovimentacao;
	}

	public void setDataMovimentacao(Date dataMovimentacao) {
		this.dataMovimentacao = dataMovimentacao;
	}
 
	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public DynamicDataModel<AipMovimentacaoProntuariosVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipMovimentacaoProntuariosVO> dataModel) {
	 this.dataModel = dataModel;
	}

	public List<AipMovimentacaoProntuariosVO> getSeqsMovimentacao() {
		return movimentacoesSelecionadasCheckbox;
	}

	public void setSeqsMovimentacao(List<AipMovimentacaoProntuariosVO> seqsMovimentacao) {
		this.movimentacoesSelecionadasCheckbox = seqsMovimentacao;
	}

	public List<AipMovimentacaoProntuariosVO> getListaSeqsBanco() {
		return listaTodasSeqMovimentacaoBanco;
	}

	public void setListaSeqsBanco(List<AipMovimentacaoProntuariosVO> listaSeqsBanco) {
		this.listaTodasSeqMovimentacaoBanco = listaSeqsBanco;
	}

	public List<AipMovimentacaoProntuariosVO> getMovimentacoesSelecionadasCheckbox() {
		return movimentacoesSelecionadasCheckbox;
	}

	public void setMovimentacoesSelecionadasCheckbox(
			List<AipMovimentacaoProntuariosVO> movimentacoesSelecionadasCheckbox) {
		this.movimentacoesSelecionadasCheckbox = movimentacoesSelecionadasCheckbox;
	}

	public List<AipMovimentacaoProntuariosVO> getListaTodasSeqMovimentacaoBanco() {
		return listaTodasSeqMovimentacaoBanco;
	}

	public void setListaTodasSeqMovimentacaoBanco(
			List<AipMovimentacaoProntuariosVO> listaTodasSeqMovimentacaoBanco) {
		this.listaTodasSeqMovimentacaoBanco = listaTodasSeqMovimentacaoBanco;
	}

	public AipMovimentacaoProntuariosVO getMovimentacaoSelecionada() {
		return movimentacaoSelecionada;
	}

	public void setMovimentacaoSelecionada(
			AipMovimentacaoProntuariosVO movimentacaoSelecionada) {
		this.movimentacaoSelecionada = movimentacaoSelecionada;
	}

	public Boolean getImmediateRecuperarListaPaginada() {
		return immediateRecuperarListaPaginada;
	}

	public void setImmediateRecuperarListaPaginada(Boolean immediateRecuperarListaPaginada) {
		this.immediateRecuperarListaPaginada = immediateRecuperarListaPaginada;
	}
	
}
