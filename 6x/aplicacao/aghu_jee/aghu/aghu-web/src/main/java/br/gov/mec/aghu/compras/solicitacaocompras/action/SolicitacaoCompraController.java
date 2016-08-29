package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.orcamento.business.IOrcamentoFacade;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class SolicitacaoCompraController extends ActionController {

	private static final String PROG_ENTREGA_COMPRA = "progEntregaCompra";
	private static final String ESTOQUE_CONSULTAR_CATALOGO_MATERIAL = "estoque-consultarCatalogoMaterial";
	private static final String ASSOCIAR_SOLICITACAO_COMPRA_SERVICO = "compras-associarSolicitacaoCompraServico";
	private static final String ESTOQUE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";
	private static final String SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";
	private static final String FASES_SOLICITACAO_COMPRA_LIST = "fasesSolicitacaoCompraList";
	private static final String PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";
	private static final String SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";
	private static final long serialVersionUID = 9041577601091390580L;	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@EJB
	private IOrcamentoFacade orcamentoFacade;
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	@EJB
	protected IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	protected IComprasFacade comprasFacade;
	
	@EJB
	protected IParametroFacade parametroFacade; 
	
	@Inject
	private LiberacaoSolicitacaoComprasPaginatorController liberacaoSolicitacaoComprasPaginatorController;
	private Integer numero; //numero da solicitacao
	private Integer codigoPaciente;
	private AipPacientes paciente;	
	private ScoSolicitacaoDeCompra solicitacaoDeCompra;
	private FsoGrupoNaturezaDespesa grupoNaturezaDespesa;
	private FccCentroCustos centroCusto;
	private FccCentroCustos centroCustoAplicada;
	private FsoVerbaGestao verbaGestao;
	private Boolean realizouPesquisaMaterial;
	private Boolean reqCcProjeto;
	private Boolean rdEdicao;
	private Boolean rdDevolucao;
	private boolean cameFromCatalogo;
	private FccCentroCustos lastCentroCusto;
	private FccCentroCustos lastCentroCustoAplicada;
	private ScoMaterial lastMaterial;
	private BigDecimal lastValorUnitario;

	private Boolean isCentroCustoReadonly;

	private Boolean isVerbaGestaoReadonly = false;

	private Boolean isGrupoNaturezaReadonly = false;
	
	private Boolean isNaturezaReadonly = false;
	
	private boolean habilitaDesabilitaDtAtendimento;
	// indica para onde o botao voltar deve redirecionar
	private String voltarParaUrl;
	private String gravarVoltarUrl;
	private Boolean carregou;
	// modal de encaminhamento
	private ScoPontoParadaSolicitacao proximoPontoParada;
	private RapServidores funcionarioComprador;	
	private Boolean desabilitaSuggestionComprador;
	private Boolean voltarPanel;
	private Boolean isCentroCustoAplicadaUpdated;
	private ScoSolicitacaoDeCompra solicitacaoCompraClone;
	private Boolean centroCustoAplicadaChanged;
	private Boolean verbaGestaoRemoved;
	private Boolean verbaGestaoFilled;
	private Boolean habAutorizarSC;
	private Boolean habEncaminharSC;
	private Boolean habBtAnexo;
	private Boolean edicaoArquivo;
	private Integer codigoMaterial;
	private String origem;
	private Boolean temPermissaoComprador;
	private Boolean temPermissaoPlanejamento;
	private Boolean temPermissaoEncaminhar;
	private Boolean temPermissaoCadastrar;
	private Boolean temPermissaoChefia;
	private Boolean temPermissaoAreasEspecificas;
	private Boolean temPermissaoGeral;
	private Integer numeroSolicitacaoCompra;
	
	private boolean chkCcSolic;
	private boolean chkCcAplic;
	private Boolean chkCcOriginal;
	
	private Boolean exibeModalPreferencia;
	
	private Boolean isUpdate;
	
	private ScoCaracteristicaUsuarioCentroCusto sugereCcSolic;
	private ScoCaracteristicaUsuarioCentroCusto sugereCcAplic;
	private ScoCaracteristicaUsuarioCentroCusto mantemCcOriginal;	
	

	public enum ManterSolicitacaoCompraControllerExceptionCode implements BusinessExceptionCode {
		SOLICITACAO_COMPRA_INCLUIDA_COM_SUCESSO, SOLICITACAO_COMPRA_ALTERADA_COM_SUCESSO,
		SOLICITACAO_COMPRA_DUPLICADA_COM_SUCESSO, CCUSTO_APLIC_INST_GERAIS_APENAS_MAT_INST, CCUSTO_APLIC_APENAS_OBR_AND,
		NATUREZA_NAO_PERMITIDA_PARA_SC, CENTRO_CUSTO_NAO_PERMITIDO_PARA_SC, VERBA_GESTAO_NAO_PERMITIDA_PARA_SC;;
	}
	
	public static enum ControlaCheck {
		URGENTE,PRIORITARIO
	};
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() throws ApplicationBusinessException {
		
		Boolean error = false;
		if (carregou == null || !carregou || cameFromCatalogo || (numero != null && solicitacaoCompraClone == null)) {
			
			this.exibeModalPreferencia = false;
			this.temPermissaoComprador = this.solicitacaoComprasFacade.verificaPemissaoUsuario("cadastrarSCComprador", this.obterLoginUsuarioLogado());
			this.temPermissaoPlanejamento= this.solicitacaoComprasFacade.verificaPemissaoUsuario("cadastrarSCPlanejamento", this.obterLoginUsuarioLogado());
			this.temPermissaoEncaminhar = this.solicitacaoComprasFacade.verificaPemissaoUsuario("encaminharSolicitacaoCompras", this.obterLoginUsuarioLogado());
			this.temPermissaoCadastrar = this.solicitacaoComprasFacade.verificaPemissaoUsuario("cadastrarSolicitacaoCompras", this.obterLoginUsuarioLogado()); 
			this.temPermissaoChefia = this.solicitacaoComprasFacade.verificaPemissaoUsuario("cadastrarSCChefias", this.obterLoginUsuarioLogado());
			this.temPermissaoAreasEspecificas = this.solicitacaoComprasFacade.verificaPemissaoUsuario("cadastrarSCAreasEspecificas", this.obterLoginUsuarioLogado());
			this.temPermissaoGeral = this.solicitacaoComprasFacade.verificaPemissaoUsuario("cadastrarSCGeral", this.obterLoginUsuarioLogado());
			
			this.atualizarRequiredCcProjeto();
			this.setarPreferencias();
						
			if(StringUtils.isNotBlank(origem)&&origem.equalsIgnoreCase("estoque-consultarCatalogoMaterial")&&this.getCodigoMaterial()!=null){
				this.cameFromCatalogo = false;
				solicitacaoDeCompra.setMaterial(this.comprasFacade.obterMaterialPorId(this.codigoMaterial));
				try{
					this.solicitacaoComprasFacade.verificarMaterialSelecionado(this.solicitacaoDeCompra.getMaterial(),
							temPermissaoCadastrar, temPermissaoChefia, temPermissaoAreasEspecificas, temPermissaoGeral, temPermissaoPlanejamento);
					this.atualizarValorUnitPrevisto(false);
					error = true;
				}
				catch(ApplicationBusinessException e){
					this.solicitacaoDeCompra.setMaterial(null);
					this.apresentarExcecaoNegocio(e);
					error = true;
				}
			}
			
			if (!error) {
				isUpdate = numero != null;		
				verbaGestaoRemoved = false;
				verbaGestaoFilled = false;
				if (isUpdate) {
					try {
										
						solicitacaoDeCompra = solicitacaoComprasFacade.obterSolicitacaoDeCompra(numero);
						this.setSolicitacaoCompraClone(solicitacaoComprasFacade.clonarSolicitacaoDeCompra(solicitacaoDeCompra));
						paciente = solicitacaoDeCompra.getPaciente();
						centroCusto = solicitacaoDeCompra.getCentroCusto();
						centroCustoAplicada = solicitacaoDeCompra.getCentroCustoAplicada();	
						funcionarioComprador = solicitacaoDeCompra.getServidorCompra();
						verbaGestao = solicitacaoDeCompra.getVerbaGestao();
						
						if(solicitacaoDeCompra.getServidorExclusao() != null) {
							solicitacaoDeCompra.setServidorExclusao(this.registroColaboradorFacade.obterServidor(solicitacaoDeCompra.getServidorExclusao().getId()));
							if (solicitacaoDeCompra.getServidorExclusao().getPessoaFisica() != null) {
							   solicitacaoDeCompra.getServidorExclusao().setPessoaFisica(this.registroColaboradorFacade.obterPessoaFisica(solicitacaoDeCompra.getServidorExclusao().getPessoaFisica().getCodigo()));
							}							
						}
						
						if (solicitacaoDeCompra.getNaturezaDespesa() != null) {
							grupoNaturezaDespesa = solicitacaoDeCompra
									.getNaturezaDespesa().getGrupoNaturezaDespesa();
						} 
						
						habAutorizarSC = this.habilitarAutorizarSC();
						habEncaminharSC = this.habilitarEncaminharSC();
						habBtAnexo = this.habilitarBtAnexo();
						
					} catch(ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				}
				else {
					if(numeroSolicitacaoCompra != null && numeroSolicitacaoCompra != 0){
						this.solicitacaoDeCompra = this.solicitacaoComprasFacade.obterSolicitacaoDeCompra(numeroSolicitacaoCompra);	

					} else if (centroCusto == null) {

						/* centro de custo do usuario logado */
						centroCusto = centroCustoFacade.pesquisarCentroCustoAtuacaoLotacaoServidor();
						centroCustoAplicada = centroCusto;
						
						if (!chkCcSolic) {
							centroCusto = null;
						}
						if (!chkCcAplic) {
							centroCustoAplicada = null;
						}

						grupoNaturezaDespesa = null;
						verbaGestao = null;
						
						solicitacaoDeCompra = new ScoSolicitacaoDeCompra();
						solicitacaoDeCompra.setPrioridade(false);
						solicitacaoDeCompra.setExclusao(false);
						solicitacaoDeCompra.setUrgente(false);
						solicitacaoDeCompra.setDevolucao(false);
						solicitacaoDeCompra.setGeracaoAutomatica(false);
						solicitacaoDeCompra.setEfetivada(false);
						solicitacaoDeCompra.setFundoFixo(false);
						solicitacaoDeCompra.setRecebimento(false);
						solicitacaoDeCompra.setDtSolicitacao(new Date());
						solicitacaoDeCompra.setDtDigitacao(new Date());
					}
					refreshParametrosOrcamento();
				}						
		
				if(codigoPaciente != null) {
					paciente = pacienteFacade.obterPacientePorCodigo(codigoPaciente);
					solicitacaoDeCompra.setPaciente(paciente);
				}
				this.carregou = Boolean.TRUE;
				this.rdEdicao = this.isReadonlyEdicao();
				this.rdDevolucao = this.isReadonlyDevolucao();
			}
		}
	
	}
	
	
	public void setarPreferencias() {
		this.sugereCcSolic = this.comprasCadastrosBasicosFacade.obterCaracteristica(DominioCaracteristicaCentroCusto.PREENCHER_CC_SOLIC_SC);
		this.sugereCcAplic = this.comprasCadastrosBasicosFacade.obterCaracteristica(DominioCaracteristicaCentroCusto.PREENCHER_CC_APLIC_SC);
		this.mantemCcOriginal = this.comprasCadastrosBasicosFacade.obterCaracteristica(DominioCaracteristicaCentroCusto.MANTER_CC_DUPLIC_SC);
		if (this.sugereCcSolic != null) {
			this.chkCcSolic = true;
		} else {
			this.chkCcSolic = false;
		}
		if (this.sugereCcAplic != null) {
			this.chkCcAplic = true;
		} else {
			this.chkCcAplic = false;
		}
		if (this.mantemCcOriginal != null) {
			this.chkCcOriginal = true;
		} else {
			this.chkCcOriginal = false;
		}
	}
	
	public void abrirNovo() throws ApplicationBusinessException{
		this.numero = null;
		this.centroCusto = null;
		this.carregou = Boolean.FALSE;
		this.origem = "";
		this.inicio();
	}
	
	public void atualizarPreferenciaSugestaoCc(Boolean isAplic, Boolean isDuplic) {

		if (isDuplic) {
			if (this.mantemCcOriginal == null) {
				this.mantemCcOriginal = this.comprasCadastrosBasicosFacade.montarScoCaracUsuario(DominioCaracteristicaCentroCusto.MANTER_CC_DUPLIC_SC);
				try {
					this.comprasCadastrosBasicosFacade.inserirCaracteristicaUserCC(this.mantemCcOriginal);
				} catch (ApplicationBusinessException e) {
					this.mantemCcOriginal = null;
					this.chkCcOriginal = false;
					this.apresentarExcecaoNegocio(e);
				}
			} else {
				try {
					this.comprasCadastrosBasicosFacade.excluirCaracteristicaUserCC(this.mantemCcOriginal);
					this.mantemCcOriginal = null;
				} catch (ApplicationBusinessException e) {
					this.apresentarExcecaoNegocio(e);
					this.chkCcOriginal = true;
				}
			}
		} else {
			if (isAplic) {
				if (this.sugereCcAplic == null) {
					this.sugereCcAplic = this.comprasCadastrosBasicosFacade.montarScoCaracUsuario(DominioCaracteristicaCentroCusto.PREENCHER_CC_APLIC_SC);
					try {
						this.comprasCadastrosBasicosFacade.inserirCaracteristicaUserCC(this.sugereCcAplic);
					} catch (ApplicationBusinessException e) {
						this.sugereCcAplic = null;
						this.chkCcAplic = false;
						this.apresentarExcecaoNegocio(e);
					}
				} else {
					try {
						this.comprasCadastrosBasicosFacade.excluirCaracteristicaUserCC(this.sugereCcAplic);
						this.sugereCcAplic = null;
					} catch (ApplicationBusinessException e) {
						this.apresentarExcecaoNegocio(e);
						this.chkCcAplic = true;
					}
				}
			} else {
				if (this.sugereCcSolic == null) {
					this.sugereCcSolic = this.comprasCadastrosBasicosFacade.montarScoCaracUsuario(DominioCaracteristicaCentroCusto.PREENCHER_CC_SOLIC_SC);
					try {
						this.comprasCadastrosBasicosFacade.inserirCaracteristicaUserCC(this.sugereCcSolic);
					} catch (ApplicationBusinessException e) {
						this.sugereCcSolic = null;
						this.chkCcSolic = false;
						this.apresentarExcecaoNegocio(e);
					}
				} else {
					try {
						this.comprasCadastrosBasicosFacade.excluirCaracteristicaUserCC(this.sugereCcSolic);
						this.sugereCcSolic = null;
					} catch (ApplicationBusinessException e) {
						this.apresentarExcecaoNegocio(e);
						this.chkCcSolic = true;
					}
				}
			}
		}
	}
	
	public String getGravarVoltarUrl() {
		return gravarVoltarUrl;		
	}

	public void setGravarVoltarUrl(String gravarVoltarUrl) {
		this.gravarVoltarUrl = gravarVoltarUrl;
	}

	public String pesqFon() {
		return PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}
	
	public String solicitacaoCompraCRUD() {
		return SOLICITACAO_COMPRA_CRUD;
	}	
	
	public void atualizarValorUnitPrevisto(Boolean calcularTotal) throws ApplicationBusinessException {
		
		
		if (solicitacaoDeCompra.getMaterial() != null) {
			Double ultimoValor = solicitacaoComprasFacade.getUltimoValorCompra(solicitacaoDeCompra.getMaterial());

			Integer codigoMaterialOld = null;
			if (solicitacaoCompraClone != null) {
				codigoMaterialOld = solicitacaoCompraClone.getMaterial().getCodigo();
			}
			
			Boolean recalculaSCRefreshRO = (solicitacaoDeCompra.getNumero() == null || (codigoMaterialOld != null && !solicitacaoDeCompra.getMaterial().getCodigo().equals(codigoMaterialOld)));
			
			if (ultimoValor != null && ultimoValor > 0  && recalculaSCRefreshRO) {
				solicitacaoDeCompra.setValorUnitPrevisto(new BigDecimal(ultimoValor));
			}
			
			if (calcularTotal || solicitacaoDeCompra.getQtdeSolicitada() != null) {
				atualizarValorTotal(recalculaSCRefreshRO);
				
				alteraQuantidadeAprovada();
				validaQtdeSolicitadaAprovada();
			}
			
		}
	}
	
	public void atualizarValorTotal() throws ApplicationBusinessException {
		atualizarValorTotal(true);
	}
	
	public void atualizarValorTotal(Boolean refreshParams) throws ApplicationBusinessException {
				
		if (refreshParams) {
			refreshParametrosOrcamento();
		}
	}
	
	// RN12 //
	public boolean isRequeridDescricaoCompra() throws ApplicationBusinessException {
		if (this.solicitacaoDeCompra != null && this.solicitacaoDeCompra.getMaterial() != null) {
			return solicitacaoComprasFacade.isRequeridDescricaoCompra(this.solicitacaoDeCompra.getMaterial());
		} else {
			return false;
		}
	}

	// RN 22 //
	public boolean isReadonlyEdicao() {
		if(solicitacaoDeCompra != null){
			return solicitacaoComprasFacade.isReadonlyEdicao(solicitacaoDeCompra, temPermissaoComprador, temPermissaoPlanejamento, temPermissaoGeral);
		}
		else{
			return false;
		}
	}
	
	// RN 36 e RN 22 //
	public boolean isReadonlyDevolucao() {
		if(solicitacaoDeCompra != null){
			return solicitacaoComprasFacade.isReadonlyDevolucao(solicitacaoDeCompra, temPermissaoComprador, temPermissaoPlanejamento, temPermissaoGeral);
		}
		else{
			return false;
		}
	}

    /**
     * Pesquisa paciente 
     */
	public List<AipPacientes> pesquisarPaciente(String objParam) throws ApplicationBusinessException {		

		String strPesquisa = (String) objParam;
		Integer prontuario = null;
		
		if (strPesquisa != null && !"".equalsIgnoreCase(strPesquisa)) {
			prontuario = Integer.valueOf(strPesquisa);
			this.paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
				
		}
		
		List<AipPacientes> pacientes = new ArrayList<AipPacientes>();
		if (this.paciente != null) {
			this.solicitacaoDeCompra.setPaciente(this.paciente);
			pacientes.add(this.paciente);
		}
		return pacientes;
	}			
    /**
     * Pesquisa modalidade licitacao
     */
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacao(String objParam) throws ApplicationBusinessException {		
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(objParam);
	}
				
	public void validaUrgente() {
		validaUrgentePrioritario(ControlaCheck.URGENTE.toString());	
	}	
	
	public void validaPrioritario() {
		validaUrgentePrioritario(ControlaCheck.PRIORITARIO.toString());	
   }

   public void limpaDtMaxAtendimento() {		
	   if (this.isHabilitaDesabilitaDtAtendimento())	{
			solicitacaoDeCompra.setDtMaxAtendimento(null);
		}
   }
	
	public void validaUrgentePrioritario(String AcaoEnum) {
		try {
			this.solicitacaoComprasFacade
					.validaUrgentePrioritario(solicitacaoDeCompra);
		} catch (final BaseException e) {
			if (ControlaCheck.URGENTE.toString().equals(AcaoEnum)) {
				solicitacaoDeCompra.setUrgente(false);
				this.limpaDtMaxAtendimento();
				
			}
			if (ControlaCheck.PRIORITARIO.toString().equals(AcaoEnum)) {
				solicitacaoDeCompra.setPrioridade(false);
				this.limpaDtMaxAtendimento();
			}
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void validaUrgentePrioritario(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException {
		solicitacaoComprasFacade.validaUrgentePrioritario(solicitacaoDeCompra);
	}
	
    public void validaExclusivo(){
		if (!solicitacaoDeCompra.getMatExclusivo()) {
			solicitacaoDeCompra.setJustificativaExclusividade(null);
			this.limpaDtMaxAtendimento();
		}
	}
	
    public void validaExcluidoDevolvido(){
		if (!solicitacaoDeCompra.getDevolucao()) {
			solicitacaoDeCompra.setJustificativaDevolucao(null);
		}
		if (!solicitacaoDeCompra.getExclusao()) {
			solicitacaoDeCompra.setMotivoExclusao(null);			
		}
	}
    
    public void verificarEstocavel() {
    	if (this.solicitacaoDeCompra.getMaterial() != null && this.getSolicitacaoDeCompra().getMaterial().getEstocavel()) {
    		this.solicitacaoDeCompra.setMaterial(null);
    	}
    }
    
    public void validaQtdeSolicitadaAprovada(){
    	try {
			this.solicitacaoComprasFacade.validaQtdeSolicitadaAprovada(solicitacaoDeCompra);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
    }
	
    public void alteraQuantidadeAprovada() {
    	try {
			this.solicitacaoComprasFacade.alteraQuantidadeAprovada(solicitacaoDeCompra);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
    }
    
    
    public Boolean isHabilitaDesabilitaDtAtendimento() {
    	boolean flagUrgente = false;
    	boolean flagPrioridade = false;
    	boolean flagExclusivo = false;
    	
    	if(solicitacaoDeCompra!=null){
	    	flagUrgente = (solicitacaoDeCompra.getUrgente() != null ? solicitacaoDeCompra.getUrgente() : false);
	    	flagPrioridade = (solicitacaoDeCompra.getPrioridade() != null ? solicitacaoDeCompra.getPrioridade() : false);
	    	flagExclusivo = (solicitacaoDeCompra.getMatExclusivo() != null ? solicitacaoDeCompra.getMatExclusivo() : false);
    	}
    	this.habilitaDesabilitaDtAtendimento = !(flagUrgente || flagPrioridade ||flagExclusivo);
		return this.habilitaDesabilitaDtAtendimento;
	}
	
	public void setHabilitaDesabilitaDtAtendimento(boolean habilitaDesabilitaDtAtendimento) {
		this.habilitaDesabilitaDtAtendimento = habilitaDesabilitaDtAtendimento;
	}
	
	private void atualizarRequiredCcProjeto() {
		try {
			this.reqCcProjeto = this.solicitacaoComprasFacade.isRequiredCcProjeto(centroCustoAplicada);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.reqCcProjeto = false;
		}
	}
	
	public String gravar() {
		try {
			boolean edicao = false;
			if(solicitacaoDeCompra.getNumero() != null) {
				edicao = true;
			}
		            
			solicitacaoDeCompra.setCentroCusto(centroCusto);
			solicitacaoDeCompra.setCentroCustoAplicada(centroCustoAplicada);
			solicitacaoDeCompra.setGrupoNaturezaDespesa(grupoNaturezaDespesa);
			solicitacaoDeCompra.setVerbaGestao(verbaGestao);
			
			if (funcionarioComprador!=null && !"".equals(funcionarioComprador)) {
				solicitacaoDeCompra.setServidorCompra(registroColaboradorFacade.obterServidor(funcionarioComprador));
			}
						
			
			solicitacaoComprasFacade.persistirSolicitacaoDeCompra(solicitacaoDeCompra, this.getSolicitacaoCompraClone());
			
			liberacaoSolicitacaoComprasPaginatorController.refazerConsulta();//Correspondente ao @Observer utilizado anteriormente
			
			if(!edicao) {
				this.setNumero(solicitacaoDeCompra.getNumero());
				this.inicio();
				this.apresentarMsgNegocio(Severity.INFO,ManterSolicitacaoCompraControllerExceptionCode.SOLICITACAO_COMPRA_INCLUIDA_COM_SUCESSO.toString(), solicitacaoDeCompra.getNumero());
			}
			else {
				this.apresentarMsgNegocio(Severity.INFO,ManterSolicitacaoCompraControllerExceptionCode.SOLICITACAO_COMPRA_ALTERADA_COM_SUCESSO.toString(), solicitacaoDeCompra.getNumero());				
			}
			
			if (!StringUtils.isBlank(getGravarVoltarUrl())){
				habAutorizarSC = this.habilitarAutorizarSC();
				habEncaminharSC = this.habilitarEncaminharSC();
				habBtAnexo = this.habilitarBtAnexo();
				this.verbaGestaoRemoved = false;
				this.verbaGestaoFilled = (this.verbaGestao != null);
				
				return this.gravarVoltarUrl;
			}
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String voltar() {
		this.carregou = null;
		this.cameFromCatalogo = false;
		this.numero = null;
        this.isUpdate = false;
		this.setCodigoMaterial(null); 
		if (StringUtils.isBlank(this.voltarParaUrl) || this.voltarParaUrl.equalsIgnoreCase("compras-solicitacaoCompraCRUD")) {
			this.voltarParaUrl = "compras-solicitacaoCompraList";
		}
		return voltarParaUrl;
	}
	
	public List<FccCentroCustos> listarCentroCustosSolic(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosServidor(objPesquisa);
	}
	
	public Long listarCentroCustosSolicCount(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosServidorCount(objPesquisa);
	}

	public Integer listarCentroCustosAplicCount(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricaoCount(objPesquisa);
	}

	public List<ScoMaterial> listarMateriais(String param) {
		this.realizouPesquisaMaterial = true;
		return  this.returnSGWithCount(this.solicitacaoComprasFacade.listarMateriaisSC(param),listarMateriaisCount(param));
	}
	
	public Integer listarMateriaisCount(String param)	{
		return this.solicitacaoComprasFacade.listarMateriaisSCCount(param, null);
	}

	public List<FsoConveniosFinanceiro> listarConvenioFinanceiro(String param) {
		return this.orcamentoFacade.listarConvenios(param);
	}

	public Long listarConvenioFinanceiroCount(String param) {
		return this.orcamentoFacade.listarConveniosCount(param);
	}

	public List<FsoNaturezaDespesa> listarNaturezaDespesa(String objPesquisa) {
		return this.orcamentoFacade.listarNaturezaDespesa(objPesquisa);
	}

	public Long listarNaturezaDespesaCount(String objPesquisa) {
		return this.orcamentoFacade.listarNaturezaDespesaCount(objPesquisa);
	}

	/**
	 * Mí©todo do suggestionBox de Modalidade
	 * 
	 * @param parametro
	 * @return
	 */
	public List<ScoModalidadeLicitacao> obterModalidades(String codigo) {
		return comprasCadastrosBasicosFacade.obterModalidadesLicitacaoAprovadasPorCodigo(codigo);
	}

	/**
	 * SuggestionBox CentroCusto Solicitante
	 * @param paramPesquisa
	 * @param servidor
	 * @return
	 */
    public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(String paramPesquisa ){
    	return  this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustoUsuarioGerarSCSuggestion(paramPesquisa),pesquisarCentroCustoUsuarioGerarSCSuggestionCount(paramPesquisa ));	
    }
    
    public Long pesquisarCentroCustoUsuarioGerarSCSuggestionCount(String paramPesquisa ){
    	return centroCustoFacade.pesquisarCentroCustoUsuarioGerarSCSuggestionCount(paramPesquisa);	
    }

	public void verificarMaterialSelecionado() {
		try{
			this.solicitacaoComprasFacade.verificarMaterialSelecionado(this.solicitacaoDeCompra.getMaterial(),
					temPermissaoCadastrar, temPermissaoChefia, temPermissaoAreasEspecificas, temPermissaoGeral,
					temPermissaoPlanejamento);
			refreshParametrosOrcamento();
		}
		catch(ApplicationBusinessException e){
			this.solicitacaoDeCompra.setMaterial(null);
			this.apresentarExcecaoNegocio(e);
		}
		
		if (this.solicitacaoDeCompra.getMaterial() != null) {
			try {
				this.atualizarValorUnitPrevisto(false);
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}
		
	}

	public Boolean bloqueiaMaterialEstocavel() {
		return this.solicitacaoComprasFacade.bloqueiaSolicitacaoComprasMatEstocavel(this.solicitacaoDeCompra.getMaterial(), temPermissaoCadastrar, temPermissaoChefia, temPermissaoAreasEspecificas, temPermissaoGeral, temPermissaoPlanejamento);
	}
	
	public String selecionarMaterial(){
		return ESTOQUE_CONSULTAR_CATALOGO_MATERIAL;
	}
    
	/**
	 * Atualiza campos restringidos pelos parâmetros orçamentários.
	 * 
	 * @param suggest Indica se deve encontrar parâmetro sugerido ou obrigatório.
	 */
	public void refreshParametrosOrcamento() {
		if (this.solicitacaoDeCompra.getMaterial() == null) {
			this.solicitacaoDeCompra.setValorUnitPrevisto(null);
		}
		BigDecimal valorUnitario = solicitacaoDeCompra.getValorUnitPrevisto();
		
		// Todos os valores - centro custo, material e valor total da SC - foram informados.				
		if (centroCusto != null && solicitacaoDeCompra.getMaterial() != null 
				&& valorUnitario != null && valorUnitario.compareTo(BigDecimal.ZERO) > 0) {
			
			// Um desses valores foi alterado.
			if ((!Objects.equals(centroCusto, lastCentroCusto)
					|| !Objects.equals(centroCustoAplicada, lastCentroCustoAplicada)
					|| !Objects.equals(solicitacaoDeCompra.getMaterial(), lastMaterial)
					|| !Objects.equals(valorUnitario, lastValorUnitario))
					&& (this.numero == null || isUpdate)) {
				
				setarRegraOrcamentaria();
			}
		} else {
			
			// Os valores a serem preenchidos com os parâmetros orçamentários são resetados.
			if (Boolean.TRUE.equals(isCentroCustoAplicadaUpdated)) {
				centroCustoAplicada = null;
				isCentroCustoReadonly = true;
			}
			
			grupoNaturezaDespesa = null;
			isGrupoNaturezaReadonly = true; 
			
			solicitacaoDeCompra.setNaturezaDespesa(null);
			isNaturezaReadonly = true;
			
			setVerbaGestao(null);			
			isVerbaGestaoReadonly = true;
		
			lastCentroCusto = centroCusto;
			lastCentroCustoAplicada = centroCustoAplicada;
			lastMaterial = solicitacaoDeCompra.getMaterial();
			lastValorUnitario = valorUnitario;
		}
	}
	
	private void setarRegraOrcamentaria() {
		
		//#39977 - Criado método pesquisarRegraOrcamentaria, para retornar todos parâmetros de uma única regra.  
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();
		criteria.setMaterial(solicitacaoDeCompra.getMaterial());
		criteria.setValor(solicitacaoDeCompra.getValorUnitPrevisto());
		criteria.setGrupoMaterial(solicitacaoDeCompra.getMaterial().getGrupoMaterial());
		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setCentroCusto(centroCusto);
		
		FsoParametrosOrcamento regraParametrosOrcamentarios = cadastrosBasicosOrcamentoFacade.pesquisarRegraOrcamentaria(criteria);
				
		setarGrupoNaturezaDespesa(regraParametrosOrcamentarios);
		setarNaturezaDespesa(regraParametrosOrcamentarios);
		setarVerbaGestao(regraParametrosOrcamentarios);
		setarCentroCustoAplicacao(regraParametrosOrcamentarios);

	}

	
	private void setarGrupoNaturezaDespesa(FsoParametrosOrcamento regra) {
		if(regra != null){
			
			this.setGrupoNaturezaDespesa(regra.getGrupoNaturezaDespesa());
						
			DominioAcaoParametrosOrcamento acaoGnd = regra.getAcaoGnd();
			if(acaoGnd != null){
				switch (acaoGnd) {
				// "O" Obrigado a ser o campo informado na regra orÃ§amentaria
				case O:
					this.isGrupoNaturezaReadonly = true;
					break;
				// "R" Restringe o campo informado na regra orÃ§amentaria
				case R:
					this.setGrupoNaturezaDespesa(null);
					this.isGrupoNaturezaReadonly = true;
					break;				
				// "S" Sugere o campo informado na regra orÃ§amentaria
				case S:
					this.isGrupoNaturezaReadonly = false;
					break;
				default:
					this.isGrupoNaturezaReadonly = false;
					break;
				}		
			}else{
				this.isGrupoNaturezaReadonly = false;
			}
		}else{
			this.setGrupoNaturezaDespesa(null);
			this.isGrupoNaturezaReadonly = false;
		}
	}

	private void setarNaturezaDespesa(FsoParametrosOrcamento regra) {
		
		if(regra == null){
			this.solicitacaoDeCompra.setNaturezaDespesa(null);
			this.isNaturezaReadonly = false;
		}else{	
			if(regra.getNaturezaDespesa() != null){
				this.solicitacaoDeCompra.setNaturezaDespesa(regra.getNaturezaDespesa());
			//Caso nÃ£o encontre Natureza Despesa cadastrado na regra, verifica se checbox estÃ¡ marcada para busca NTD do cadastro do Grupo de Material 
			}else if(regra.getIsCadastradaGrupo() != null){
				if(regra.getIsCadastradaGrupo()){
					BigDecimal paramVlrNumerico;
					try {
						paramVlrNumerico = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GR_NATUREZA_MAT_CONSUMO).getVlrNumerico();
					} catch (ApplicationBusinessException e) {
						paramVlrNumerico = null;
					}
					if(paramVlrNumerico != null){
						this.solicitacaoDeCompra.setNaturezaDespesa(cadastrosBasicosOrcamentoFacade.getNaturezaScGrupoMaterial(this.solicitacaoDeCompra.getMaterial(), paramVlrNumerico));
					}
				}else{
					this.solicitacaoDeCompra.setNaturezaDespesa(null);				
				}				
			}else{
				this.solicitacaoDeCompra.setNaturezaDespesa(null);				
			}
			
			DominioAcaoParametrosOrcamento acaoNtd = regra.getAcaoNtd();
			
			if(acaoNtd != null){
				switch (acaoNtd) {
				//"O" Obrigado a ser o campo informado na regra orÃ§amentaria 
				case O:
					this.isNaturezaReadonly = true;
					break;
				//"R" Restringe o campo informado na regra orÃ§amentaria
				case R:
					this.solicitacaoDeCompra.setNaturezaDespesa(null);
					this.isNaturezaReadonly = true;
					break;
				//"S" Sugere o campo informado na regra orÃ§amentaria
				case S:
					this.isNaturezaReadonly = false;
					break;
				default:
					this.isNaturezaReadonly = false;
					break;
				}
			}else{
				this.isNaturezaReadonly = false;
			}
		}		
	}

	private void setarVerbaGestao(FsoParametrosOrcamento regra) {
		
		if(regra != null){
					
			DominioAcaoParametrosOrcamento acaoVbg = regra.getAcaoVbg();
			if(acaoVbg != null){
				switch (acaoVbg) {
				// "O" Obrigado a ser o campo informado na regra orÃ§amentaria
				case O:
					this.isVerbaGestaoReadonly = true;
					break;
					// "R" Restringe o campo informado na regra orÃ§amentaria
				case R:
					setVerbaGestao(null);
					solicitacaoDeCompra.setVerbaGestao(null);
					this.isVerbaGestaoReadonly = true;
					break;
					// "S" Sugere o campo informado na regra orÃ§amentaria
				case S:
					this.isVerbaGestaoReadonly = false;
					break;
				default:
					this.isVerbaGestaoReadonly = false;
					break;
				}
			}else{
				this.isVerbaGestaoReadonly = false;
			}
		}else{
			this.isVerbaGestaoReadonly = false;
		}
		
		FsoVerbaGestao vbFipe = this.cadastrosBasicosOrcamentoFacade.obterVerbaGestaoProjetoFipe(this.centroCustoAplicada);
		
		if (vbFipe != null && (getVerbaGestao() == null || verbaGestaoRemoved)) {
			setVerbaGestao(vbFipe);
			solicitacaoDeCompra.setOrcamentoPrevio(DominioSimNao.N.toString());
		} else {
			if (!verbaGestaoFilled) {
				if(regra != null){
					DominioAcaoParametrosOrcamento acaoVbg = regra.getAcaoVbg();
					if(!DominioAcaoParametrosOrcamento.R.equals(acaoVbg)){
						setVerbaGestao(regra.getVerbaGestao());
						solicitacaoDeCompra.setVerbaGestao(regra.getVerbaGestao());
					}
				}
			}
		}
		
	}

	private void setarCentroCustoAplicacao(FsoParametrosOrcamento regra) {
		
		BigDecimal valorTotal = solicitacaoDeCompra.getValorTotal();
		
		if (this.centroCustoAplicada == null) {
			this.centroCustoAplicada = cadastrosBasicosOrcamentoFacade
					.getCentroCustoScParam(solicitacaoDeCompra.getMaterial(),
							centroCusto, valorTotal);
			
			isCentroCustoAplicadaUpdated = true;
		} else {
			FsoParametrosOrcamento acao = cadastrosBasicosOrcamentoFacade
					.getAcaoScParam(
							solicitacaoDeCompra.getMaterial(),
							centroCusto,
							valorTotal,
							FsoParametrosOrcamentoCriteriaVO.Parametro.CENTRO_CUSTO);

			if (acao != null && acao.getAcaoCct() != null) {
				switch (acao.getAcaoCct()) {
				case O:
					centroCustoAplicada = acao.getCentroCustoReferencia();

					isCentroCustoAplicadaUpdated = true;
					break;

				case R:
					Boolean validCentroCustoAplicada = null;

					if (centroCustoAplicada != null) {
						validCentroCustoAplicada = cadastrosBasicosOrcamentoFacade
								.isCentroCustoValidScParam(
										solicitacaoDeCompra.getMaterial(),
										centroCusto, valorTotal,
										centroCustoAplicada);
					}

					// Se o centro de custo aplicaÃ§Ã£o for invÃ¡lido ou usuÃ¡rio
					// nÃ£o mudou o valor, entÃ£o busca o centro de custo
					// parametrizado.
					if (Boolean.FALSE.equals(validCentroCustoAplicada)
							|| !Boolean.TRUE.equals(centroCustoAplicadaChanged)) {
						centroCustoAplicada = cadastrosBasicosOrcamentoFacade
								.getCentroCustoScParam(
										solicitacaoDeCompra.getMaterial(),
										centroCusto, valorTotal);

						isCentroCustoAplicadaUpdated = true;
					}

					break;

				case S:
					// Se usuÃ¡rio nÃ£o mudou centro de custo, entÃ£o busca o
					// centro de custo parametrizado como sugestÃ£o.
					if (!Boolean.TRUE.equals(centroCustoAplicadaChanged)) {
						centroCustoAplicada = acao.getCentroCustoReferencia();
						isCentroCustoAplicadaUpdated = true;
					}

					break;
				}
			}
		}
	}
	
	/**
	 * Sinaliza mudança do centro de custo aplicação por parte do usuário.
	 */
	public void setCentroCustoAplicadaChanged() {
		centroCustoAplicadaChanged = true;
		this.atualizarRequiredCcProjeto();
	}

	public void setVerbaGestaoRemoved() {
		this.verbaGestaoRemoved = true;
		this.verbaGestaoFilled = false;
	}
	
	public void setVerbaGestaoFilled() {
		this.verbaGestaoRemoved = false;
		this.verbaGestaoFilled = true;
	}
	
	/**
	 * Verifica se centro custo aplicação possui um único parâmetro, sendo este
	 * obrigatório.
	 * 
	 * @return Flag para habilitar ou desabilitar o campo.
	 */
	public Boolean isCentroCustoReadonly() {
		return isCentroCustoReadonly;
	}

	/**
	 * Verifica se grupo de natureza possui um único parâmetro, sendo este
	 * obrigatório.
	 * 
	 * @return Flag para habilitar ou desabilitar o campo.
	 */
	public Boolean isGrupoNaturezaReadonly() {
		return isGrupoNaturezaReadonly;
	}

	/**
	 * Verifica se natureza possui um único parâmetro, sendo este obrigatório.
	 * 
	 * @return Flag para habilitar ou desabilitar o campo.
	 */
	public Boolean isNaturezaReadonly() {
		return isNaturezaReadonly;
	}

	/**
	 * Verifica se verba de gestão possui um único parâmetro, sendo este
	 * obrigatório.
	 * 
	 * @return Flag para habilitar ou desabilitar o campo.
	 */
	public Boolean isVerbaGestaoReadonly() {
		return isVerbaGestaoReadonly;
	}

	public List<FccCentroCustos> listarCentroCustosAplic(String filter) {
		BigDecimal valorTotal = solicitacaoDeCompra.getValorTotal();
		if (solicitacaoDeCompra.getMaterial() != null && centroCusto != null && valorTotal != null) {
			return  this.returnSGWithCount(cadastrosBasicosOrcamentoFacade.listarCentroCustosScParams(solicitacaoDeCompra.getMaterial(), centroCusto, valorTotal, filter),listarCentroCustosAplicCount(filter));
		} else {
			return centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricao(filter);
		}
	}

	/**
	 * Obtem grupos de natureza disponí­veis.
	 * 
	 * @param filter
	 *            Filtro.
	 * @return Grupos de natureza disponí­veis.
	 */
	public List<FsoGrupoNaturezaDespesa> pesquisarGruposNaturezaDespesa(
			String filter) {
		return cadastrosBasicosOrcamentoFacade.listarGruposNaturezaScParams(solicitacaoDeCompra.getMaterial(), centroCusto, solicitacaoDeCompra.getValorTotal(),filter);
	}

	/**
	 * Obtem naturezas de despesa disponí­veis.
	 * 
	 * @param filter
	 *            Filtro.
	 * @return Naturezas de despesa disponí­veis.
	 */
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesa(String filter) {
		return cadastrosBasicosOrcamentoFacade.listarNaturezaScParams(solicitacaoDeCompra.getMaterial(), centroCusto, grupoNaturezaDespesa,solicitacaoDeCompra.getValorTotal(), filter);
	}

	public List<ScoModalidadeLicitacao> listarFormasContratacaoAtivas(String formaCont) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(formaCont);
	}
	
	/**
	 * Obtem verbas de gestão disponí­veis.
	 * 
	 * @param filter
	 *            Filtro.
	 * @return Verbas de gestão.
	 */
	public List<FsoVerbaGestao> pesquisarVerbasGestao(String filter) {
		return cadastrosBasicosOrcamentoFacade.listarVerbasGestaoScParams(solicitacaoDeCompra.getMaterial(), centroCusto, solicitacaoDeCompra.getValorTotal(), filter);
	}
	
	public void refreshNaturezaDespesa() {
		refreshNaturezaDespesa(true);
	}

	public void refreshNaturezaDespesa(Boolean suggest) {
		isNaturezaReadonly = grupoNaturezaDespesa == null
				|| cadastrosBasicosOrcamentoFacade
						.hasUniqueRequiredNaturezaScParam(
								solicitacaoDeCompra.getMaterial(), centroCusto,
								grupoNaturezaDespesa,
								solicitacaoDeCompra.getValorUnitPrevisto());

		if (Boolean.TRUE.equals(suggest)) {
			solicitacaoDeCompra.setNaturezaDespesa(
					grupoNaturezaDespesa == null ? null
					: cadastrosBasicosOrcamentoFacade.getNaturezaScParam(
							solicitacaoDeCompra.getMaterial(), centroCusto,
							grupoNaturezaDespesa,
							solicitacaoDeCompra.getValorUnitPrevisto()));
		}
	}
	
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoDeCompra() {
		return solicitacaoDeCompra;
	}

	public void setSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) {
		this.solicitacaoDeCompra = solicitacaoDeCompra;
	}

	public FsoGrupoNaturezaDespesa getGrupoNaturezaDespesa() {
		return grupoNaturezaDespesa;
	}

	public void setGrupoNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public FccCentroCustos getCentroCustoAplicada() {
		return centroCustoAplicada;
	}

	public void setCentroCustoAplicada(FccCentroCustos centroCustoAplicada) {
		this.centroCustoAplicada = centroCustoAplicada;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}	
		
	public String duplicarSC(){
		try {
			super.closeDialog("modalConfirmacaoDuplicarSCWG");
			if (!this.cadastrosBasicosOrcamentoFacade.isNaturezaValidScParam(solicitacaoDeCompra.getMaterial(), 
					solicitacaoDeCompra.getCentroCusto(), solicitacaoDeCompra.getValorTotal(), solicitacaoDeCompra.getNaturezaDespesa()) || 
					!this.cadastrosBasicosOrcamentoFacade.isVerbaGestaoValidScParam(solicitacaoDeCompra.getMaterial(), 
					solicitacaoDeCompra.getCentroCusto(), solicitacaoDeCompra.getValorTotal(), getVerbaGestao())) {
				//refreshParametrosOrcamento();
				this.solicitacaoComprasFacade.desatacharSolicitacaoCompras(solicitacaoDeCompra);
			}
			
			this.setSolicitacaoCompraClone(solicitacaoComprasFacade.clonarSolicitacaoDeCompra(solicitacaoDeCompra));		

			this.solicitacaoComprasFacade.duplicarSC(this.getSolicitacaoCompraClone(), (this.mantemCcOriginal != null));
			this.setNumero(this.getSolicitacaoCompraClone().getNumero());			
			this.apresentarMsgNegocio(Severity.INFO,
					ManterSolicitacaoCompraControllerExceptionCode.SOLICITACAO_COMPRA_DUPLICADA_COM_SUCESSO.toString(), this.getSolicitacaoCompraClone().getNumero());				
			this.carregou = false;
			this.cameFromCatalogo = false;
			this.origem = "";
			this.inicio();
			return SOLICITACAO_COMPRA_CRUD;
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String autorizarSC() {
		try {
			List<Integer> nroScos;
			
			nroScos = new ArrayList<Integer>();
			
			nroScos.add(this.solicitacaoDeCompra.getNumero());
			this.solicitacaoComprasFacade.autorizarListaSolicitacaoCompras(nroScos);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AUTORIZACAO_SOL_UNICA_COM_SUCESSO", nroScos.get(0));
			this.setNumero(this.solicitacaoDeCompra.getNumero());	
			this.carregou = false;
			this.cameFromCatalogo = false;
			this.origem = "";
			this.inicio();
			return SOLICITACAO_COMPRA_CRUD;
		} catch (BaseException e) {			
			apresentarExcecaoNegocio(e);
			return null;
		}
		
	}
	
	private Boolean habilitarBtAnexo(){
		if (solicitacaoDeCompra == null){
			return false;
		}
		
		if (getEdicaoArquivo()){
			return true;
		} else {
			// desabilitar o botão de anexo caso seja a inclusão de uma SC ou esteja sendo editada uma SC
			// que se encontre na situação que não possa incluir novos arquivos e ela ainda não possua nenhum a ser visualizado
			Boolean existeArquivo = false;
			
			existeArquivo = solicitacaoComprasFacade.verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento.SC, solicitacaoDeCompra.getNumero());
			
			return existeArquivo;
		}
	}
	
	
	public Boolean habilitarAutorizarSC(){
		try{
			return this.solicitacaoComprasFacade.habilitarAutorizarSC(this.solicitacaoDeCompra);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);	
			return null;
		}
			
		}
	
	public Boolean habilitarEncaminharSC(){
		List<FccCentroCustos> listaCentroCustosUsuario = this.centroCustoFacade.pesquisarCentroCustoUsuarioGerarSC();
		
		Boolean habilitaEncaminharSC = this.solicitacaoComprasFacade.habilitarEncaminharSC(this.solicitacaoDeCompra,
				temPermissaoComprador, temPermissaoPlanejamento, temPermissaoEncaminhar,
				listaCentroCustosUsuario);
		
		setEdicaoArquivo(habilitaEncaminharSC);
		
		return habilitaEncaminharSC;
	}
	
	public void encaminharScPontoParada() {
		try {
			List<Integer> nroScos = new ArrayList<Integer>();
			nroScos.add(getSolicitacaoDeCompra().getNumero());
			solicitacaoComprasFacade.encaminharListaSolicitacaoCompras(nroScos, comprasCadastrosBasicosFacade.obterPontoParadaAutorizacao(), this.getProximoPontoParada(), this.getFuncionarioComprador(), false);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOL_UNICA_ENCAMINHADA_COM_SUCESSO", nroScos.get(0));
			this.closeDialog("modalEncaminharScoWG");
			this.limparModalEncaminhamento();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}	
	}
	
	public void verificarPontoParadaComprador() throws ApplicationBusinessException {
		this.setDesabilitaSuggestionComprador(comprasCadastrosBasicosFacade.verificarPontoParadaComprador(this.proximoPontoParada));
	}
	
	public void limparModalEncaminhamento() {
		this.proximoPontoParada = null;
		this.funcionarioComprador = null;	
		this.setDesabilitaSuggestionComprador(false);
		this.voltarPanel = false;
	}
	
	public void mostrarModalEncaminhamento(){
		this.proximoPontoParada = null;
		this.funcionarioComprador = null;	
		this.setDesabilitaSuggestionComprador(false);
		this.voltarPanel = false;
		super.openDialog("modalEncaminharScoWG");
	}
	
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(String parametro) throws BaseException {
		String filtro = "";
		if(parametro != null){
			filtro = parametro;
		}
		return comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(filtro, this.solicitacaoDeCompra.getPontoParadaProxima().getCodigo());
	}
	
	public String obterNomePessoaFisicaServidorExclusao(ScoSolicitacaoDeCompra scoCompro) throws ApplicationBusinessException {
	if(scoCompro.getServidorExclusao() != null) {
		RapServidores servidorExclusao = this.registroColaboradorFacade.obterServidor(scoCompro.getServidorExclusao().getId());
		if (servidorExclusao.getPessoaFisica() != null) {
		   RapPessoasFisicas pesFis = this.registroColaboradorFacade.obterPessoaFisica(servidorExclusao.getPessoaFisica().getCodigo());
		   if (pesFis != null){
			   return pesFis.getNome();
		   }
		}							
	}
	 return "";
	}
	
	public String redirecionaAndamentoSc(){
		return FASES_SOLICITACAO_COMPRA_LIST;
	}
	
	public String redirecionaProgEntrega(){
		if (this.numero ==null){
				this.numero = solicitacaoDeCompra.getNumero();
		}
		return PROG_ENTREGA_COMPRA;
	}
	
	public String redirecionaAnexos(){
		return SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	}
		
	public String redirecionaEstatistica(){
		return ESTOQUE_ESTATISTICA_CONSUMO;
	}
	
	public String redirecionaAssociarSs(){
		return ASSOCIAR_SOLICITACAO_COMPRA_SERVICO;
	}
	
	public List<RapServidores> pesquisarCompradorAtivoPorMatriculaNome(String parametro) {
		return registroColaboradorFacade.pesquisarServidoresCompradorAtivoPorMatriculaNome(parametro);
	}
	
	public Boolean isPerfilComprador(){
		return this.temPermissaoComprador;
	}
	
	public Boolean isPerfilPlanejamento(){
		return this.temPermissaoPlanejamento;
	}
	
	public Boolean isPerfilGeral(){
		return this.temPermissaoGeral;
	}
	
	public ScoPontoParadaSolicitacao getProximoPontoParada() {
		return proximoPontoParada;
	}

	public void setProximoPontoParada(ScoPontoParadaSolicitacao proximoPontoParada) {
		this.proximoPontoParada = proximoPontoParada;
	}

	public RapServidores getFuncionarioComprador() {
		return funcionarioComprador;
	}

	public void setFuncionarioComprador(RapServidores funcionarioComprador) {
		this.funcionarioComprador = funcionarioComprador;
	}

	public Boolean getDesabilitaSuggestionComprador() {
		return desabilitaSuggestionComprador;
	}

	public void setDesabilitaSuggestionComprador(Boolean desabilitaSuggestionComprador) {
		this.desabilitaSuggestionComprador = desabilitaSuggestionComprador;
	}

	public Boolean getVoltarPanel() {
		return voltarPanel;
	}

	public void setVoltarPanel(Boolean voltarPanel) {
		this.voltarPanel = voltarPanel;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoCompraClone() {
		return solicitacaoCompraClone;
	}

	public void setSolicitacaoCompraClone(ScoSolicitacaoDeCompra solicitacaoCompraClone) {
		this.solicitacaoCompraClone = solicitacaoCompraClone;
	}

	public Boolean getRealizouPesquisaMaterial() {
		return realizouPesquisaMaterial;
	}

	public void setRealizouPesquisaMaterial(Boolean realizouPesquisaMaterial) {
		this.realizouPesquisaMaterial = realizouPesquisaMaterial;
	}

	public Boolean getHabAutorizarSC() {
		return habAutorizarSC;
	}


	public void setHabAutorizarSC(Boolean habAutorizarSC) {
		this.habAutorizarSC = habAutorizarSC;
	}

	public Boolean getHabEncaminharSC() {
		return habEncaminharSC;
	}

	public void setHabEncaminharSC(Boolean habEncaminharSC) {
		this.habEncaminharSC = habEncaminharSC;
	}

	public Boolean getHabBtAnexo() {
		return habBtAnexo;
	}

	public void setHabBtAnexo(Boolean habBtAnexo) {
		this.habBtAnexo = habBtAnexo;
	}

	public Boolean getEdicaoArquivo() {
		return edicaoArquivo;
	}

	public void setEdicaoArquivo(Boolean edicaoArquivo) {
		this.edicaoArquivo = edicaoArquivo;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Boolean getCarregou() {
		return carregou;
	}

	public void setCarregou(Boolean carregou) {
		this.carregou = carregou;
	}

	public Boolean getReqCcProjeto() {
		return reqCcProjeto;
	}

	public void setReqCcProjeto(Boolean reqCcProjeto) {
		this.reqCcProjeto = reqCcProjeto;
	}

	public Boolean getRdEdicao() {
		return rdEdicao;
	}

	public void setRdEdicao(Boolean rdEdicao) {
		this.rdEdicao = rdEdicao;
	}

	public Boolean getRdDevolucao() {
		return rdDevolucao;
	}

	public void setRdDevolucao(Boolean rdDevolucao) {
		this.rdDevolucao = rdDevolucao;
	}

	public boolean isCameFromCatalogo() {
		return cameFromCatalogo;
	}

	public void setCameFromCatalogo(boolean cameFromCatalogo) {
		this.cameFromCatalogo = cameFromCatalogo;
	}

	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}

	public Boolean getVerbaGestaoRemoved() {
		return verbaGestaoRemoved;
	}

	public void setVerbaGestaoRemoved(Boolean verbaGestaoRemoved) {
		this.verbaGestaoRemoved = verbaGestaoRemoved;
	}

	public Boolean getVerbaGestaoFilled() {
		return verbaGestaoFilled;
	}

	public void setVerbaGestaoFilled(Boolean verbaGestaoFilled) {
		this.verbaGestaoFilled = verbaGestaoFilled;
	}

	public Boolean getTemPermissaoComprador() {
		return temPermissaoComprador;
	}

	public void setTemPermissaoComprador(Boolean temPermissaoComprador) {
		this.temPermissaoComprador = temPermissaoComprador;
	}

	public Boolean getTemPermissaoPlanejamento() {
		return temPermissaoPlanejamento;
	}

	public void setTemPermissaoPlanejamento(Boolean temPermissaoPlanejamento) {
		this.temPermissaoPlanejamento = temPermissaoPlanejamento;
	}

	public Boolean getTemPermissaoEncaminhar() {
		return temPermissaoEncaminhar;
	}

	public void setTemPermissaoEncaminhar(Boolean temPermissaoEncaminhar) {
		this.temPermissaoEncaminhar = temPermissaoEncaminhar;
	}

	public Boolean getTemPermissaoCadastrar() {
		return temPermissaoCadastrar;
	}

	public void setTemPermissaoCadastrar(Boolean temPermissaoCadastrar) {
		this.temPermissaoCadastrar = temPermissaoCadastrar;
	}

	public Boolean getTemPermissaoChefia() {
		return temPermissaoChefia;
	}

	public void setTemPermissaoChefia(Boolean temPermissaoChefia) {
		this.temPermissaoChefia = temPermissaoChefia;
	}

	public Boolean getTemPermissaoAreasEspecificas() {
		return temPermissaoAreasEspecificas;
	}

	public void setTemPermissaoAreasEspecificas(
			Boolean temPermissaoAreasEspecificas) {
		this.temPermissaoAreasEspecificas = temPermissaoAreasEspecificas;
	}

	public Boolean getTemPermissaoGeral() {
		return temPermissaoGeral;
	}

	public void setTemPermissaoGeral(Boolean temPermissaoGeral) {
		this.temPermissaoGeral = temPermissaoGeral;
	}
	public boolean isChkCcSolic() {
		return chkCcSolic;
	}
	public void setChkCcSolic(boolean chkCcSolic) {
		this.chkCcSolic = chkCcSolic;
	}
	public boolean isChkCcAplic() {
		return chkCcAplic;
	}
	public void setChkCcAplic(boolean chkCcAplic) {
		this.chkCcAplic = chkCcAplic;
	}
	public ScoCaracteristicaUsuarioCentroCusto getSugereCcSolic() {
		return sugereCcSolic;
	}
	public void setSugereCcSolic(ScoCaracteristicaUsuarioCentroCusto sugereCcSolic) {
		this.sugereCcSolic = sugereCcSolic;
	}
	public ScoCaracteristicaUsuarioCentroCusto getSugereCcAplic() {
		return sugereCcAplic;
	}
	public void setSugereCcAplic(ScoCaracteristicaUsuarioCentroCusto sugereCcAplic) {
		this.sugereCcAplic = sugereCcAplic;
	}
	
	public Boolean getExibeModalPreferencia() {
		return exibeModalPreferencia;
	}

	public void setExibeModalPreferencia(Boolean exibeModalPreferencia) {
		this.exibeModalPreferencia = exibeModalPreferencia;
	}
	
	public boolean isChkCcOriginal() {
		return chkCcOriginal;
	}
	public void setChkCcOriginal(boolean chkCcOriginal) {
		this.chkCcOriginal = chkCcOriginal;
	}
	public ScoCaracteristicaUsuarioCentroCusto getMantemCcOriginal() {
		return mantemCcOriginal;
	}
	public void setMantemCcOriginal(ScoCaracteristicaUsuarioCentroCusto mantemCcOriginal) {
		this.mantemCcOriginal = mantemCcOriginal;
	}
	public void setNumeroSolicitacaoCompra(Integer numeroSolicitacaoCompra) {
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
	}
	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}
	
}