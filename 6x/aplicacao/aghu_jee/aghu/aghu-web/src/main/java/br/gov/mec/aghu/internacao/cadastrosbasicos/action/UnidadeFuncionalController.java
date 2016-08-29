package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
//import br.gov.mec.aghu.business.exception.AGHUNegocioException;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
//import br.gov.mec.aghu.recursoshumanos.Pessoa;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
//import br.gov.mec.aghu.util.AghuUtil;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
//import br.gov.mec.seam.model.exception.MECModelException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de
 * tipo de unidade funcional.
 */



public class UnidadeFuncionalController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6375955370129302798L;
	
	@EJB
	protected IAghuFacade aghuFacade;
	
	@EJB	
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private Object responsavel;
	
	
	private final String PAGE_UNIDADE_FUNCIONAL_LIST = "unidadeFuncionalList";
	
	
	/**
	 * Flag novo, obtido via page parameter.
	 */
		
	
	private AghUnidadesFuncionais unidadeFuncional;

	private List<CaracteristicaUnidadeFuncionaisFlag> caracteristicas;
	private Map<ConstanteAghCaractUnidFuncionais,CaracteristicaUnidadeFuncionaisFlag> mapCaracteristicas;
		
	private List<AghCaractUnidFuncionais> listaCaractUnidFuncionais = new ArrayList<AghCaractUnidFuncionais>(0);
	
	

	private boolean operacaoConcluida = false;
	
	/**
	 * Codigo do tipo de unidade funcional, obtido via page parameter.
	 */
	private Integer unidadeFuncionalCodigo;
	
	private Integer codigoCentroCustoPesq;
	private Integer codigoClinicaPesq;
	private Integer codigoUnidPaiPesq;
	private Short codigoUnidFuncPesq;
	private Integer codigoTipoUnidFuncPesq;
	
	
	
	/*public String iniciarInclusao() {
		
		this.unidadeFuncionalCodigo = null;
		return "iniciarInclusao";
	}*/

	/*public void inicio() {

		if (isNovo()) {
			//Cria uma nova Unidade Funcional
			operacao = Operacao.CRIACAO;
			this.unidadeFuncional = new AghUnidadesFuncionais();
			this.unidadeFuncional.setRapServidorChefia(null);
		}else{
			operacao = Operacao.EDICAO;
			this.unidadeFuncional = this.cadastrosBasicosInternacaoFacade.obterUnidadeFuncional(this.unidadeFuncionalCodigo.shortValue());
		}
		
		inicializaCaracteristicas();
	}*/
	
	/*public void inicio() {

		if (isNovo()) {
			//Cria uma nova Unidade Funcional			
			this.unidadeFuncional = new AghUnidadesFuncionais();
			this.unidadeFuncional.setRapServidorChefia(null);
			
		}else{
			operacao = Operacao.EDICAO;
			this.unidadeFuncional = this.cadastrosBasicosInternacaoFacade.obterUnidadeFuncional(this.unidadeFuncionalCodigo.shortValue());
		}
		
		inicializaCaracteristicas();
	}*/
	
	@PostConstruct
	public void init() {
		begin(conversation);		
	}
	
	public void inicializaCaracteristicas(){
		caracteristicas = new ArrayList<CaracteristicaUnidadeFuncionaisFlag>();
		mapCaracteristicas = new HashMap<ConstanteAghCaractUnidFuncionais,CaracteristicaUnidadeFuncionaisFlag>();
		for(ConstanteAghCaractUnidFuncionais cacuf: ConstanteAghCaractUnidFuncionais.values()){
			CaracteristicaUnidadeFuncionaisFlag cuff = new CaracteristicaUnidadeFuncionaisFlag(cacuf,false);
			caracteristicas.add(cuff);
			mapCaracteristicas.put(cacuf, cuff);
		}
		        		
		if(this.unidadeFuncional != null && this.unidadeFuncional.getSeq() != null){	
			List<AghCaractUnidFuncionais> lst = aghuFacade.listarCaracteristicasUnidadesFuncionais(unidadeFuncional.getSeq() , null, null, null);
//			unidadeFuncional.setCaracteristicas(new HashSet<AghCaractUnidFuncionais>(lst));
			for(AghCaractUnidFuncionais acuf : lst){
				if(acuf != null){
					mapCaracteristicas.get(acuf.getId().getCaracteristica()).setValor(true);
				}
			}
		}	
		
		CoreUtil.ordenarLista(caracteristicas, "caracteristica.descricao", true);		
	}
	
	
	private List<ConstanteAghCaractUnidFuncionais> caracteristicasEnums;
	
	private boolean associaCaracteristicas() {
		caracteristicasEnums = new ArrayList<>();
		
		for (CaracteristicaUnidadeFuncionaisFlag cuf : this.caracteristicas) {
			if (cuf.getValor()) {
				caracteristicasEnums.add(cuf.getCaracteristica());
				
				//this.cadastrosBasicosInternacaoFacade.associaCaracteristica(unidadeFuncional, cuf.getCaracteristica());			
				// validar se caracteristica = Unidade Dia, ter datas obrigatórias
				if (cuf.getCaracteristica().getDescricao().equalsIgnoreCase("Unidade do Hospital Dia")){
					if (this.unidadeFuncional.getHrioInicioAtendimento()==null || "".equalsIgnoreCase(this.unidadeFuncional.getHrioInicioAtendimento().toString())
						|| 	this.unidadeFuncional.getHrioInicioAtendimento()==null || "".equalsIgnoreCase(this.unidadeFuncional.getHrioInicioAtendimento().toString())){
						return false;
					}
				}
			}
		}		
		mapCaracteristicas.clear();
		return true;
	}
	
	
	public void removerCaracteristicas(AghCaractUnidFuncionais caracteristica){
		listaCaractUnidFuncionais.remove(caracteristica);
	}
	
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de unidade funcional
	 */
	public String confirmar() {

		boolean novo = unidadeFuncional.getSeq() == null;
		try {
			if (this.associaCaracteristicas()){			
				
				if (novo){
					this.cadastrosBasicosInternacaoFacade.incluirUnidadeFuncional(unidadeFuncional, caracteristicasEnums);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_UNIDADE_FUNCIONAL",unidadeFuncional.getDescricao());
					
				} else {						
					this.cadastrosBasicosInternacaoFacade.atualizarUnidadeFuncionalidade(unidadeFuncional, caracteristicasEnums);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_UNIDADE_FUNCIONAL",this.unidadeFuncional.getDescricao());						
				}
							
				return cancelar();
				
			} else {
				apresentarMsgNegocio(Severity.INFO, "ERRO_CADASTRO_DATA_UNIDADE_DIA");
				return null;
			}
			
		} catch (ApplicationBusinessException e) {
			if(e.getMessage().equals("ERRO_NIVEL_HIERARQUIA_MAIOR_PERMITIDO")){
				apresentarMsgNegocio(Severity.ERROR, "Erro Unidade Funcional não pode ser Pai/Filho dela mesma");
			}else{
				apresentarExcecaoNegocio(e);
			}
		}

		return null;
	} 
	
	public AghAla[] getDominioAlas() {
		List<AghAla> alas = aghuFacade.buscarTodasAlas();
		
		return alas.toArray(new AghAla[alas.size()]);
	}
	
	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * tipo de unidade funcional
	 */
	public String cancelar() {		
		unidadeFuncional = new AghUnidadesFuncionais();
		return PAGE_UNIDADE_FUNCIONAL_LIST;
	}

	public List<FccCentroCustos> pesquisaCentroCustoSB(String strPesquisa) {
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(strPesquisa != null ? strPesquisa : null);
	}
	
	public List<AghClinicas> pesquisaClinicaSB(String strPesquisa) {
		return aghuFacade.listarClinicasPorNomeOuCodigo(strPesquisa);
	}
	
	public boolean isMostrarLinkExcluirResponsavel(){
		return this.unidadeFuncional.getRapServidorChefia() != null && this.unidadeFuncional.getRapServidorChefia().getId().getMatricula() != null;
	}
	
	public List<AghTiposUnidadeFuncional> pesquisaTipoUnidade(String strPesquisa) {
		return cadastrosBasicosInternacaoFacade.listarPorNomeOuCodigo((String) strPesquisa);
	}
	
	public List<AghUnidadesFuncionais> pesquisaUnidades(String strPesquisa){
		Short unidadeFuncionalFilha = unidadeFuncional.getSeq();
		return aghuFacade.pesquisarUnidadesPorCodigoDescricaoFilha(strPesquisa, false, unidadeFuncionalFilha);
	}

	public List<SceAlmoxarifado> pesquisarAlmoxarifado(String parametro) {
		List<SceAlmoxarifado> resultList = this.estoqueFacade.pesquisarAlmoxarifadosAtivosPorCodigoDescricao(parametro);
		return resultList;
	}
	
	public List<RapServidores> pesquisarResponsavel(String nome) {
		return registroColaboradorFacade.pesquisarRapServidoresPorCodigoDescricao((String) nome);
	}
	
	
	// ### GETs e SETs ###
	
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Map<ConstanteAghCaractUnidFuncionais, CaracteristicaUnidadeFuncionaisFlag> getMapCaracteristicas() {
		return mapCaracteristicas;
	}

	public void setMapCaracteristicas(
			Map<ConstanteAghCaractUnidFuncionais, CaracteristicaUnidadeFuncionaisFlag> mapCaracteristicas) {
		this.mapCaracteristicas = mapCaracteristicas;
	}

	public List<AghCaractUnidFuncionais> getListaCaractUnidFuncionais() {
		return listaCaractUnidFuncionais;
	}

	public void setListaCaractUnidFuncionais(
			List<AghCaractUnidFuncionais> listaCaractUnidFuncionais) {
		this.listaCaractUnidFuncionais = listaCaractUnidFuncionais;
	}

	public Integer getUnidadeFuncionalCodigo() {
		return unidadeFuncionalCodigo;
	}

	public void setUnidadeFuncionalCodigo(Integer unidadeFuncionalCodigo) {
		this.unidadeFuncionalCodigo = unidadeFuncionalCodigo;
	}

	public void setCodigoClinicaPesq(Integer codigoClinicaPesq) {
		this.codigoClinicaPesq = codigoClinicaPesq;
	}

	public Integer getCodigoClinicaPesq() {
		return codigoClinicaPesq;
	}

	public void setCodigoCentroCustoPesq(Integer codigoCentroCustoPesq) {
		this.codigoCentroCustoPesq = codigoCentroCustoPesq;
	}

	public Integer getCodigoCentroCustoPesq() {
		return codigoCentroCustoPesq;
	}

	public List<CaracteristicaUnidadeFuncionaisFlag> getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(
			List<CaracteristicaUnidadeFuncionaisFlag> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}

	public void setCodigoCentroUnidPaiPesq(Integer codigoCentroUnidPaiPesq) {
		this.codigoUnidPaiPesq = codigoCentroUnidPaiPesq;
	}

	public Integer getCodigoCentroUnidPaiPesq() {
		return codigoUnidPaiPesq;
	}

	public void setCodigoUnidFuncPesq(Short codigoUnidFuncPesq) {
		this.codigoUnidFuncPesq = codigoUnidFuncPesq;
	}

	public Short getCodigoUnidFuncPesq() {
		return codigoUnidFuncPesq;
	}

	public Integer getCodigoUnidPaiPesq() {
		return codigoUnidPaiPesq;
	}

	public void setCodigoUnidPaiPesq(Integer codigoUnidPaiPesq) {
		this.codigoUnidPaiPesq = codigoUnidPaiPesq;
	}

	public Integer getCodigoTipoUnidFunc() {
		return codigoTipoUnidFuncPesq;
	}

	public void setCodigoTipoUnidFunc(Integer codigoTipoUnidFunc) {
		this.codigoTipoUnidFuncPesq = codigoTipoUnidFunc;
	}

	public Integer getCodigoTipoUnidFuncPesq() {
		return codigoTipoUnidFuncPesq;
	}

	public void setCodigoTipoUnidFuncPesq(Integer codigoTipoUnidFuncPesq) {
		this.codigoTipoUnidFuncPesq = codigoTipoUnidFuncPesq;
	}

	

	public boolean isOperacaoConcluida() {
		return operacaoConcluida;
	}

	public void setOperacaoConcluida(boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}	

	
	
	/*public boolean isCodigoEditavel () {
		return operacao == Operacao.CRIACAO;
	}*/

	public Object getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Object responsavel) {
		this.responsavel = responsavel;
	}

	public List<ConstanteAghCaractUnidFuncionais> getCaracteristicasEnums() {
		return caracteristicasEnums;
	}

	public void setCaracteristicasEnums(
			List<ConstanteAghCaractUnidFuncionais> caracteristicasEnums) {
		this.caracteristicasEnums = caracteristicasEnums;
	}

	/*public Operacao getOperacao() {
		return operacao;
	}

	public void setOperacao(Operacao operacao) {
		this.operacao = operacao;
	}*/


	
}