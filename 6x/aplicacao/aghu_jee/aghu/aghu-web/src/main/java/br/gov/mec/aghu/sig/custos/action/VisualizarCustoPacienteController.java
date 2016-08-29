package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioVisaoCustoPaciente;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.VisualizarCustoPacienteVO;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class VisualizarCustoPacienteController extends ActionController implements ActionPaginator {

	private static final String PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";

	private static final String VISUALIZAR_CUSTO_PACIENTE_ARVORE_APRESENTACAO_DADOS = "visualizarCustoPacienteArvoreApresentacaoDados";

	private static final String VISUALIZAR_CUSTO_PACIENTE = "visualizarCustoPaciente";
	
	private static final long serialVersionUID = -4451268082494217883L;
	
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@Inject
	private VisualizarCustoPacienteArvoreApresentacaoDadosController visualizarCustoPacienteArvoreApresentacaoDadosController;
	
	private DominioVisaoCustoPaciente visao;
	
	private VisualizarCustoPacienteVO vo;
	
	private AipPacientes paciente;
	private Integer prontuario; 
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	private Integer pacCodigoFonetica;
	private String nomePaciente;
	private Boolean voltouPesquisaPacientes;
	private List<AghCid> listaCID;
	private List<FccCentroCustos> listaCentroCusto;
	private List<AghEspecialidades>listaEspecialidades;
	private List<AghEquipes> listaEquipes;
	private List<AghAtendimentosVO> listagem;
	private List<AghAtendimentosVO> listagemCompleta;
	private List<AghAtendimentosVO> listagemSelecionados;
	
	private SigProcessamentoCusto competencia;
	private List<SigProcessamentoCusto> listaCompetencias;
	
	private Boolean toggleAbertoCID = Boolean.FALSE;
	private Boolean toggleAbertoEspecialidadeMedica = Boolean.FALSE;
	private Boolean toggleAbertoEquipeMedica = Boolean.FALSE;
	private Boolean toggleAbertoCentroCusto = Boolean.FALSE;
	
	private Boolean pesquisado = Boolean.FALSE;
	
	private boolean exibePesquisaPaciente = true;
	
	private BigDecimal totalCusto = BigDecimal.ZERO;
	private BigDecimal totalReceita = BigDecimal.ZERO;
	
	public enum VisualizarCustoControllerExceptionCode implements BusinessExceptionCode{
			ERRO_PESQUISA_PACIENTE,MENSAGEM_ERRO_PACIENTE_SEM_PRONTUARIO;
	}
	
	@Inject @Paginator
	private DynamicDataModel<AghAtendimentosVO> dataModel;	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Metodo de setup chamado pelo page.xml
	 */
	public void iniciar(){
	 
		if(this.vo==null){
			this.vo = new VisualizarCustoPacienteVO();	
		}
		
		if(this.visao == null){
			this.visao = DominioVisaoCustoPaciente.PACIENTE;
		}
		
		if(this.listaCID == null){
			this.listaCID = new ArrayList<AghCid>();
			
		}
		
		if(this.listaCentroCusto == null){
			this.listaCentroCusto = new ArrayList<FccCentroCustos>();
			
		}
		
		if(this.listaEspecialidades == null){
			this.listaEspecialidades = new ArrayList<AghEspecialidades>();
		}
		
		if(this.listaEquipes == null){
			this.listaEquipes = new ArrayList<AghEquipes>();
		}
		
		if(this.listagem == null){
			this.listagem = new ArrayList<AghAtendimentosVO>();
			this.visao = DominioVisaoCustoPaciente.PACIENTE;
		}
		
		if(this.listagemCompleta == null){
			this.listagemCompleta = new ArrayList<AghAtendimentosVO>();
		}
		
		if(this.listagemSelecionados == null){
			this.listagemSelecionados = new ArrayList<AghAtendimentosVO>();
		}
		
		CodPacienteFoneticaVO pacienteFoneticaVO = codPacienteFonetica.get();
		//telas de pesquisa fonetica usam assim NA CONTROLLER
		if (Boolean.TRUE.equals(this.voltouPesquisaPacientes) && pacienteFoneticaVO != null && pacienteFoneticaVO.getCodigo() > 0 ) { // Retorno da pesquisa fonética			
			this.pacCodigoFonetica = pacienteFoneticaVO.getCodigo();
			try {
				this.setPaciente(this.pacienteFacade.obterPaciente(this.pacCodigoFonetica));
				this.prontuario = this.paciente.getProntuario();
			} finally {
				this.voltouPesquisaPacientes = false;
			}
		}
		
	}
	
	/**
	 * Aguardando estoria #32270
	 */
	public String confirmar(){
		try {
			this.atualizarLista();
			for(AghAtendimentosVO atendimento: listagemSelecionados){
				if(atendimento.getProntuario() == null) {
					throw new ApplicationBusinessException(VisualizarCustoControllerExceptionCode.MENSAGEM_ERRO_PACIENTE_SEM_PRONTUARIO);
				}	
			}
			this.custosSigFacade.validarConfirmar(listagemSelecionados);
			visualizarCustoPacienteArvoreApresentacaoDadosController.setVisao(this.visao);
			visualizarCustoPacienteArvoreApresentacaoDadosController.setPacienteEmAlta(this.vo.getPacienteComAlta());
			visualizarCustoPacienteArvoreApresentacaoDadosController.setProcessoCusto(this.competencia);
			visualizarCustoPacienteArvoreApresentacaoDadosController.setListaCentroCusto(this.listaCentroCusto);
			visualizarCustoPacienteArvoreApresentacaoDadosController.setListaEquipes(this.listaEquipes);
			visualizarCustoPacienteArvoreApresentacaoDadosController.setListaEspecialidades(this.listaEspecialidades);
			visualizarCustoPacienteArvoreApresentacaoDadosController.setListagemCustoPaciente(listagemSelecionados);
			visualizarCustoPacienteArvoreApresentacaoDadosController.setAtdSeq(null);
			visualizarCustoPacienteArvoreApresentacaoDadosController.setOrigem(VISUALIZAR_CUSTO_PACIENTE);
			return VISUALIZAR_CUSTO_PACIENTE_ARVORE_APRESENTACAO_DADOS;
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Retorna true se o tipo de visao eh paciente. Do contrario para competencia retorna false
	 * 
	 * @return
	 */
	public void validaTipoVisao(){
		this.listaCompetencias = this.custosSigFacade.obterListaCompetencias(this.visao);
		
		if(this.prontuario != null && this.paciente.getCodigo() != null && 
				this.pacCodigoFonetica != null && this.pesquisado
				&& !this.visao.equals(DominioVisaoCustoPaciente.PACIENTE)){
			this.paciente = new AipPacientes();
			this.prontuario = null;
			this.pacCodigoFonetica = null;
		}
		
		if(this.competencia != null && this.competencia.getSeq() != null && this.pesquisado){
			this.competencia = new SigProcessamentoCusto();
			this.setPesquisado(Boolean.FALSE);
		}

		if(DominioVisaoCustoPaciente.PACIENTE == this.visao){
			this.exibePesquisaPaciente = true;
		} else {
			this.exibePesquisaPaciente = false;	
		}
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			if(paciente==null){
				paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());	
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}

	public String redirecionarPesquisaFonetica() {
		this.voltouPesquisaPacientes = true;
		return PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}
	
	// suggestion - cid
	public List<AghCid> pesquisarCid(String param) {
		return this.returnSGWithCount(this.aghuFacade.obterCidPorNomeCodigoAtivaPaginado((String)param),pesquisarCidCount(param));
	}
	
	public Long pesquisarCidCount(String param) {
		return this.aghuFacade.obterCidPorNomeCodigoAtivaCount((String)param);
	}
	
	public void getAdicionarCidNaLista(){
		this.adicionarCidInternacao(this.vo.getAghCid());
		this.vo.setAghCid(null);
		this.toggleAbertoCID = Boolean.TRUE;
	}
	
	/**
	 * Método que adiciona um novo cidInternacao na lista
	 * 
	 * @param cid
	 */
	private void adicionarCidInternacao(AghCid cid) {
		try {
			this.custosSigFacade.adicionarCIDNaLista(this.listaCID, cid);
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getCode().toString());
		}
	}
	
	public void deletarCIDDaLista(AghCid cid){
		this.custosSigFacade.deletarCIDDaLista(this.listaCID, cid);
	}
	
	public void getAdicionarCentroCustoNaLista(){
		this.adicionarCentroCusto(this.vo.getCentroCusto());
		this.vo.setCentroCusto(null);
		this.toggleAbertoCentroCusto = Boolean.TRUE;
	}
	
	/**
	 * Método que adiciona um novo cidInternacao na lista
	 * 
	 * @param cid
	 */
	private void adicionarCentroCusto(FccCentroCustos centroCusto) {
		try {
			this.custosSigFacade.adicionarCentroCustoNaLista(this.listaCentroCusto, centroCusto);
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getCode().toString());
		}
	}
	
	public void deletarCentroCustoDaLista(FccCentroCustos centroCusto){
		this.custosSigFacade.deletarCentroCustoDaLista(this.listaCentroCusto, centroCusto);
	}
	
	/**
	 * Chama o metodo para adicionar especialidades na lista
	 */
	public void adicionarEspecialidadesNaLista(){
		this.adicionarEspecialidades(this.vo.getEspecialidades());
		this.vo.setEspecialidades(null);
		this.toggleAbertoEspecialidadeMedica = Boolean.TRUE;
	}
	
	/**
	 * Método que adiciona um novo cidInternacao na lista
	 * 
	 * @param cid
	 */
	private void adicionarEspecialidades(AghEspecialidades especialidade) {
		try {
			this.custosSigFacade.adicionarEspecialidadesNaLista(this.listaEspecialidades, especialidade);
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getCode().toString());
		}
	}
	
	public void deletarEspecialidadesDaLista(AghEspecialidades especialidade){
		this.custosSigFacade.deletarEspecialidadesDaLista(this.listaEspecialidades, especialidade);
	}
	
	
	/**
	 * SuggestionBox CentroCusto
	 * @param paramPesquisa
	 * @param servidor
	 * @return
	 */
    public List<FccCentroCustos> pesquisarCentroCustoSuggestion(String paramPesquisa ){
    	return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustoPorCodigoEDescricao(paramPesquisa),pesquisarCentroCustoSuggestionCount(paramPesquisa ));	
    }
    
    public Long pesquisarCentroCustoSuggestionCount(String paramPesquisa ){
    	return centroCustoFacade.pesquisarCentroCustoPorCodigoEDescricaoCount(paramPesquisa);	
    }
    
    public List<AghEspecialidades> pesquisarEspecialidadesSuggestion(String paramPesquisa){
    	return this.returnSGWithCount(this.aghuFacade.listarPorSigla(paramPesquisa),pesquisarEspecialidadesSuggestionCount(paramPesquisa));
    }
    
    public Long pesquisarEspecialidadesSuggestionCount(String paramPesquisa ){
    	return aghuFacade.listarEspecialidadesSolicitacaoProntuarioCount(paramPesquisa);	
    }
	
    /**
	 * Chama o metodo para adicionar equipes na lista
	 */
	public void adicionarEquipesNaLista(){
		this.adicionarEquipes(this.vo.getEquipes());
		this.vo.setEquipes(null);
		this.toggleAbertoEquipeMedica = Boolean.TRUE;
	}
    
    /**
	 * Método que adiciona uma nova equipe medica na lista
	 * 
	 * @param cid
	 */
	private void adicionarEquipes(AghEquipes equipes) {
		try {
			this.custosSigFacade.adicionarEquipeNaLista(this.getListaEquipes(), equipes);
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getCode().toString());
		}
	}
	
	public void deletarEquipeDaLista(AghEquipes equipes){
		this.custosSigFacade.deletarEquipesDaLista(this.getListaEquipes(), equipes);
	}
    
    public List<AghEquipes> pesquisarEquipesPorNomeOuCodigo(String paramPesquisa){
    	return this.returnSGWithCount(this.aghuFacade.pesquisarEquipesPorNomeOuCodigo(paramPesquisa, 100),pesquisarEquipeAtivaCount(paramPesquisa));
    }
    
    public Long pesquisarEquipeAtivaCount(String paramPesquisa){
    	return this.aghuFacade.pesquisarEquipeAtivaCount(paramPesquisa);
    }
    
    private void fecharToggles(){
    	this.toggleAbertoCID = Boolean.FALSE;
		this.toggleAbertoEspecialidadeMedica = Boolean.FALSE;
		this.toggleAbertoEquipeMedica = Boolean.FALSE;
		this.toggleAbertoCentroCusto = Boolean.FALSE;
    }
    
    public void pesquisar(){
		this.setListagemSelecionados(new ArrayList<AghAtendimentosVO>());
    	this.dataModel.reiniciarPaginator();
    }
    
    /**
     * Limpa os dados
     * @return
     */
    public void limpar(){
    	this.vo = new VisualizarCustoPacienteVO();
		this.listaCID = new ArrayList<AghCid>();
		this.listaCentroCusto = new ArrayList<FccCentroCustos>();
		this.listaEspecialidades = new ArrayList<AghEspecialidades>();
		this.listaEquipes = new ArrayList<AghEquipes>();
		this.setListaEquipes(new ArrayList<AghEquipes>());
		this.setListagem(new ArrayList<AghAtendimentosVO>());
		this.setListagemCompleta(new ArrayList<AghAtendimentosVO>());
		this.listagemSelecionados = new ArrayList<AghAtendimentosVO>();
		this.paciente = null;
		this.prontuario = null;
		this.pacCodigoFonetica = null;
		this.competencia = null;
		this.setPesquisado(Boolean.FALSE);
		this.exibePesquisaPaciente = Boolean.TRUE;
		this.visao = DominioVisaoCustoPaciente.PACIENTE;
		this.totalCusto = BigDecimal.ZERO;
		this.totalReceita = BigDecimal.ZERO;
		this.dataModel.limparPesquisa();
	} 
    
	//GETTERS E SETTERS

	public DominioVisaoCustoPaciente getVisao() {
		return visao;
	}

	public void setVisao(DominioVisaoCustoPaciente visao) {
		this.visao = visao;
	}

	public VisualizarCustoPacienteVO getVo() {
		return vo;
	}

	public void setVo(VisualizarCustoPacienteVO vo) {
		this.vo = vo;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}
	
	/**
	 * Padrão AGHU...em nenhuma tela chamando o pesquisa fonetica faz diferente disso.
	 * @param paciente
	 */
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
		//telas de pesquisa fonetica usam assim NA CONTROLLER
		if (paciente != null && StringUtils.isNotBlank(paciente.getNome())) {
			this.nomePaciente = paciente.getNome();
			this.pacCodigoFonetica = paciente.getCodigo();
			this.prontuario = paciente.getProntuario();
		} else {
			this.nomePaciente = null;
			this.pacCodigoFonetica = null;
			this.prontuario = null;
		}
	}
	
	public void obterTotais() {
		if(visao.equals(DominioVisaoCustoPaciente.COMPETENCIA)){
			totalCusto = this.custosSigFacade.buscarCustoTotalPesquisa(null, this.getCompetencia().getSeq(),listaCID, 
	    			listaCentroCusto,
	    			listaEspecialidades,
	    			this.custosSigFacade.obterListaReponsaveisPorListaDeEquipes(listaEquipes));
			totalReceita = this.custosSigFacade.buscarReceitaTotalPesquisa(null, this.getCompetencia().getSeq(), listaCID, 
	    			listaCentroCusto,
	    			listaEspecialidades,
	    			this.custosSigFacade.obterListaReponsaveisPorListaDeEquipes(listaEquipes));	
		} else {
			for(AghAtendimentosVO atendimentoVO: listagemCompleta){
				BigDecimal valorTotalCusto = atendimentoVO.getValorTotalCusto();
				BigDecimal valorTotalReceita = atendimentoVO.getValorTotalReceita();
				if (valorTotalCusto != null) {
					totalCusto = totalCusto.add(valorTotalCusto);
				}
				if (valorTotalReceita != null) {
					totalReceita = totalReceita.add(valorTotalReceita);
				}
			}
		}
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Boolean getVoltouPesquisaPacientes() {
		return voltouPesquisaPacientes;
	}

	public void setVoltouPesquisaPacientes(Boolean voltouPesquisaPacientes) {
		this.voltouPesquisaPacientes = voltouPesquisaPacientes;
	}

	public void setListaCID(List<AghCid> listaCID) {
		this.listaCID = listaCID;
	}

	public List<AghCid> getListaCID() {
		return listaCID;
	}

	public void setListaCentroCusto(List<FccCentroCustos> listaCentroCusto) {
		this.listaCentroCusto = listaCentroCusto;
	}

	public List<FccCentroCustos> getListaCentroCusto() {
		return listaCentroCusto;
	}

	public void setListaEspecialidades(List<AghEspecialidades> listaEspecialidades) {
		this.listaEspecialidades = listaEspecialidades;
	}

	public List<AghEspecialidades> getListaEspecialidades() {
		return listaEspecialidades;
	}

	public void setListaEquipes(List<AghEquipes> listaEquipes) {
		this.listaEquipes = listaEquipes;
	}

	public List<AghEquipes> getListaEquipes() {
		return listaEquipes;
	}

	public void setListagem(List<AghAtendimentosVO> listagem) {
		this.listagem = listagem;
	}

	public List<AghAtendimentosVO> getListagem() {
		return listagem;
	}

	public void setListagemCompleta(List<AghAtendimentosVO> listagemCompleta) {
		this.listagemCompleta = listagemCompleta;
	}

	public List<AghAtendimentosVO> getListagemCompleta() {
		return listagemCompleta;
	}

	public void setCompetencia(SigProcessamentoCusto competencia) {
		this.competencia = competencia;
	}

	public SigProcessamentoCusto getCompetencia() {
		return competencia;
	}

	public void setListaCompetencias(List<SigProcessamentoCusto> listaCompetencias) {
		this.listaCompetencias = listaCompetencias;
	}

	public List<SigProcessamentoCusto> getListaCompetencias() {
		return listaCompetencias;
	}

	public Boolean getToggleAbertoCID() {
		return toggleAbertoCID;
	}

	public void setToggleAbertoCID(Boolean toggleAbertoCID) {
		this.toggleAbertoCID = toggleAbertoCID;
	}

	public Boolean getToggleAbertoEspecialidadeMedica() {
		return toggleAbertoEspecialidadeMedica;
	}

	public void setToggleAbertoEspecialidadeMedica(
			Boolean toggleAbertoEspecialidadeMedica) {
		this.toggleAbertoEspecialidadeMedica = toggleAbertoEspecialidadeMedica;
	}

	public Boolean getToggleAbertoEquipeMedica() {
		return toggleAbertoEquipeMedica;
	}

	public void setToggleAbertoEquipeMedica(Boolean toggleAbertoEquipeMedica) {
		this.toggleAbertoEquipeMedica = toggleAbertoEquipeMedica;
	}

	public Boolean getToggleAbertoCentroCusto() {
		return toggleAbertoCentroCusto;
	}

	public void setToggleAbertoCentroCusto(Boolean toggleAbertoCentroCusto) {
		this.toggleAbertoCentroCusto = toggleAbertoCentroCusto;
	}

	public Boolean getPesquisado() {
		return pesquisado;
	}

	public void setPesquisado(Boolean pesquisado) {
		this.pesquisado = pesquisado;
	}

	public boolean isExibePesquisaPaciente() {
		return exibePesquisaPaciente;
	}

	public void setExibePesquisaPaciente(boolean exibePesquisaPaciente) {
		this.exibePesquisaPaciente = exibePesquisaPaciente;
	}

	public BigDecimal getTotalCusto() {
		return totalCusto;
	}

	public void setTotalCusto(BigDecimal totalCusto) {
		this.totalCusto = totalCusto;
	}

	public BigDecimal getTotalReceita() {
		return totalReceita;
	}

	public void setTotalReceita(BigDecimal totalReceita) {
		this.totalReceita = totalReceita;
	}

	@Override
	public Long recuperarCount() {
		Long retorno = null;
		if(visao == DominioVisaoCustoPaciente.PACIENTE){
	    		
	    		try {
	    			if(paciente == null || (paciente.getCodigo() == null && paciente.getProntuario()==null)){
	    				throw new ApplicationBusinessException(VisualizarCustoControllerExceptionCode.ERRO_PESQUISA_PACIENTE);
	    			}
	    			retorno = this.aghuFacade.obterAghAtendimentosPorFiltrosPacienteCount(
			    			paciente, 
			    			listaCID, 
			    			listaCentroCusto,
			    			listaEspecialidades,
			    			this.custosSigFacade.obterListaReponsaveisPorListaDeEquipes(listaEquipes));
					
				} catch (ApplicationBusinessException e) {
					this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
				}
	   	} else{
	    		retorno = this.aghuFacade.obterAghAtendimentosPorFiltrosCompetenciaCount(
		    			this.competencia, 
		    			listaCID, 
		    			listaCentroCusto,
		    			listaEspecialidades,
		    			this.custosSigFacade.obterListaReponsaveisPorListaDeEquipes(listaEquipes),
	    			this.vo.getPacienteComAlta());
    	}
		return retorno;
	}

	@Override
	public List recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
    	fecharToggles();
    	this.setPesquisado(Boolean.TRUE);
		this.totalCusto = BigDecimal.ZERO;
		this.totalReceita = BigDecimal.ZERO;
    	
    	if(visao == DominioVisaoCustoPaciente.PACIENTE){
    		
    		try {
    			if(paciente == null || (paciente.getCodigo() == null && paciente.getProntuario()==null)){
    				throw new ApplicationBusinessException(VisualizarCustoControllerExceptionCode.ERRO_PESQUISA_PACIENTE);
    			}
    			this.listagem = this.aghuFacade.obterAghAtendimentosPorFiltrosPaciente(firstResult,
    					maxResult, orderProperty, asc,
		    			paciente, 
		    			listaCID, 
		    			listaCentroCusto,
		    			listaEspecialidades,
		    			this.custosSigFacade.obterListaReponsaveisPorListaDeEquipes(listaEquipes));
    			this.listagemCompleta = this.aghuFacade.obterAghAtendimentosPorFiltrosPaciente(null,
    					null, null, false,
		    			paciente, 
		    			listaCID, 
		    			listaCentroCusto,
		    			listaEspecialidades,
		    			this.custosSigFacade.obterListaReponsaveisPorListaDeEquipes(listaEquipes));
    			this.custosSigFacade.obterValoresCustosReceitasPorProntuario(this.listagem);
    			this.atualizarLista();
    			this.carregarListaSelecionados();
   		} catch (ApplicationBusinessException e) {
				this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			}
    		
    		
			
    	} else{
    		this.listagem = this.aghuFacade.obterAghAtendimentosPorFiltrosCompetencia(firstResult,
					maxResult, orderProperty, asc,
	    			this.competencia, 
	    			listaCID, 
	    			listaCentroCusto,
	    			listaEspecialidades,
	    			this.custosSigFacade.obterListaReponsaveisPorListaDeEquipes(listaEquipes),
	    			this.vo.getPacienteComAlta());
    		this.listagemCompleta = this.aghuFacade.obterAghAtendimentosPorFiltrosCompetencia(null,
					null, null, false,
	    			this.competencia, 
	    			listaCID, 
	    			listaCentroCusto,
	    			listaEspecialidades,
	    			this.custosSigFacade.obterListaReponsaveisPorListaDeEquipes(listaEquipes),
	    			this.vo.getPacienteComAlta());
    		if (this.competencia != null) {
    			this.custosSigFacade.obterValoresCustosReceitasPorProntuarioEProcessamento(this.listagem, this.competencia.getSeq());
    		}
    		this.atualizarLista();
    		this.carregarListaSelecionados();
    	}
    	
    	try {
    		String[] informacoesMensagem = this.custosSigFacade.buscarInformacoesParaMostrarMensagem(this.competencia, this.listaCID, this.listaCentroCusto, this.listaEquipes, this.listaEspecialidades);
			this.custosSigFacade.validarListaVaziaExibeMensagem(this.listagem, informacoesMensagem);
			obterTotais();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}

		return listagem;
	}

	public void atualizarLista(){
		for(AghAtendimentosVO atendimentoAux: listagemCompleta){
			for(AghAtendimentosVO atendimento: listagem){
				if(atendimento.getProntuario().equals(atendimentoAux.getProntuario())){
					atendimentoAux.setValorTotalCusto(atendimento.getValorTotalCusto());
					atendimentoAux.setValorTotalReceita(atendimento.getValorTotalReceita());
					continue;
				} 
			}
		}
	}
	
	public void selecionarAtendimento(AghAtendimentosVO atendimento) {
		boolean remove = false;
		for(AghAtendimentosVO selecionado: listagemSelecionados){
			if(atendimento.getProntuario().equals(selecionado.getProntuario())){
				atendimento = selecionado;
				remove = true;
			} 
		}
		if(!remove){
			listagemSelecionados.add(atendimento);
		} else {
			listagemSelecionados.remove(atendimento);
		}
		
	}
	
	public void carregarListaSelecionados(){
		for(AghAtendimentosVO atendimento: listagem){
			for(AghAtendimentosVO atendimentoAux: listagemSelecionados){
				if(atendimento.getProntuario().equals(atendimentoAux.getProntuario())){
					atendimento.setControleAtd(true);
				}	
			}
		}
	}
	
	public DynamicDataModel<AghAtendimentosVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghAtendimentosVO> dataModel) {
		this.dataModel = dataModel;
	}

	public List<AghAtendimentosVO> getListagemSelecionados() {
		return listagemSelecionados;
	}

	public void setListagemSelecionados(List<AghAtendimentosVO> listagemSelecionados) {
		this.listagemSelecionados = listagemSelecionados;
	}


}
