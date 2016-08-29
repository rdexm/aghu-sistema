package br.gov.mec.aghu.registrocolaborador.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.dominio.DominioTipoRemuneracao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.registrocolaborador.vo.OcupacaoCargoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ServidorPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<RapServidores> dataModel;

	private static final Log LOG = LogFactory.getLog(ServidorPaginatorController.class);
		
	private static final long serialVersionUID = -217836837467178626L;

	private static final String PESQUISAR_DEPENDENTE = "pesquisarDependente";
	private static final String PESQUISAR_GRADUACAO_SERVIDOR = "pesquisarGraduacaoServidor";
	private static final String PESQUISAR_PESSOA_TIPO_INFORMACOES = "pesquisarPessoaTipoInformacoes";
	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private static final String CADASTRAR_SERVIDOR = "cadastrarServidor";
	
	private RapServidores servidor = new RapServidores();
	
	private Integer codigoPessoa; 
	private String nomePessoa;
	private Integer codigoCCustoLotacao;
	private Integer codigoCCustoAtuacao;
	private Integer codigoOcupacao;
	private String codigoCargo;
	private boolean exibirNovo;
	private boolean fromEdicao;
	private boolean pesquisaAutomatica;
	private Integer parametroCodigoPessoa;
	private String voltarPara;
	private Integer tamanhoMatricula;
	private boolean expirarVinculo;
	private Date dataAtual;
	private Date dataExpiraVinculo;
	
	private RapVinculos vinculo = null;
	private Object vinculoObject;
	private List<RapVinculos> listaVinculos;
	
	private Object pessoaObject;
	private List<RapPessoasFisicas> listaPessoaFisica;

	private Object lotacaoObject;
	private List<FccCentroCustos> listaCCLotacao;

	private Object ocupacaoObject;
	private List<OcupacaoCargoVO> listaOcupacao;
	
	private Object atuacaoObject;
	private List<FccCentroCustos> listaCCAtuacao;

	
	//Filtros
	private FccCentroCustos centroCustoAtuacao = null;
	private FccCentroCustos centroCustoLotacao = null;
	private OcupacaoCargoVO ocupacaoCargo = null;
	private DominioTipoRemuneracao tipoRemuneracao;
	private DominioSituacaoVinculo indSituacao;
	private Integer matricula;
	private Short vinCodigo; 
	private String usuario;
	private RapPessoasFisicas pessoaFisica = null;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String pesquisarDependente(){
		return PESQUISAR_DEPENDENTE;
	}
	
	public String pesquisarGraduacaoServidor(){
		return PESQUISAR_GRADUACAO_SERVIDOR;
	}
	
	public String pesquisarPessoaTipoInformacoes(){
		return PESQUISAR_PESSOA_TIPO_INFORMACOES;
	}
	
	public void iniciar() throws ApplicationBusinessException {
	 

		
		if (this.parametroCodigoPessoa != null && this.voltarPara != null) {
			this.setCodigoPessoa(parametroCodigoPessoa);
			
			List<RapPessoasFisicas> pessoa = this.registroColaboradorFacade.pesquisarPessoaFisicaPorCodigoNome(parametroCodigoPessoa.toString());
			if(pessoa!=null && !pessoa.isEmpty()){
				this.setPessoaFisica(pessoa.get(0));
			}
			this.pesquisar();
			this.setPesquisaAutomatica(true);
		}
		
		//VALOR DEFAULT
		this.setTamanhoMatricula(7);
		AghParametros parametroTamanhoMatricula = null;
		
		try{
			parametroTamanhoMatricula = parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_TAMANHO_MATRICULA);
		} catch (ApplicationBusinessException ex) {
			//Deixa continuar
			LOG.error("Erro ao obter o parametro P_AGHU_TAMANHO_MATRICULA", ex);
		}	
		if (parametroTamanhoMatricula != null && parametroTamanhoMatricula.getVlrNumerico() != null) {
			this.setTamanhoMatricula(Integer.valueOf(parametroTamanhoMatricula.getVlrNumerico().toString()));
		}
	
	}
	
	@Override
	public Long recuperarCount() {
		
		if(expirarVinculo){
			this.periodoDataExpirarVinculo();
		} else {
			this.setDataAtual(null);
			this.setDataExpiraVinculo(null);
		}
		
		return registroColaboradorFacade.pesquisarServidorCount(vinCodigo, matricula,
				indSituacao, pessoaFisica, usuario, centroCustoLotacao,
				centroCustoAtuacao, tipoRemuneracao, ocupacaoCargo, dataAtual, dataExpiraVinculo);
	}

	@Override
	public List<RapServidores> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<RapServidores> listaServidores;
		
		if(expirarVinculo){
			this.periodoDataExpirarVinculo();
		} else {
			this.setDataAtual(null);
			this.setDataExpiraVinculo(null);
		}
		
		if(indSituacao != null && indSituacao.isProgramado()){
			listaServidores = 	registroColaboradorFacade.pesquisarServidor(vinCodigo, matricula, indSituacao, pessoaFisica, usuario, 
					centroCustoLotacao,	centroCustoAtuacao, tipoRemuneracao, ocupacaoCargo,firstResult, maxResult,
					RapServidores.Fields.DATA_FIM_VINCULO.toString(), false, dataAtual, dataExpiraVinculo);
		}else{
			listaServidores = 
					registroColaboradorFacade.pesquisarServidor(vinCodigo, matricula,	indSituacao, pessoaFisica, usuario, 
					centroCustoLotacao,	centroCustoAtuacao, tipoRemuneracao, ocupacaoCargo,firstResult, maxResult,	null, true, dataAtual, dataExpiraVinculo);
		}
		int numeroDependentes = 0;
		for (RapServidores rapServidores : listaServidores) {
			numeroDependentes = registroColaboradorFacade.obterNumeroDependentes(rapServidores.getPessoaFisica());			
			rapServidores.getPessoaFisica().setNumeroDependentes(numeroDependentes);
			if(rapServidores.getDtFimVinculo() != null){
				if(rapServidores.getDtFimVinculo().getTime() < new Date().getTime() && rapServidores.getIndSituacao().isProgramado()){
					rapServidores.setIndSituacao(DominioSituacaoVinculo.I); 
				}
			}
		}
		return listaServidores;
	}
	
	private void periodoDataExpirarVinculo(){
		
		Calendar data = Calendar.getInstance(); 
		data.setTime(new Date());  
		data.add(Calendar.DATE, 30);

		dataAtual = new Date();
		dataExpiraVinculo = data.getTime();
	}

	/**
	 * Método para verificar condição de inclusão de dependentes, permitindo ou não a visualização do
	 * ícone na tela. 
	 * 
	 * @param _servidor  Servidor selecionado na tela
	 * @return {@code true} para condição favorável a renderização do botão e
	 *         {@code false} para caso contrário.
	 */
	public Boolean renderizaDependente(RapServidores _servidor) {

		if (_servidor.getPessoaFisica().temDependentes()
				|| _servidor.getVinculo().getIndDependente() == DominioSimNao.S) {

			if (_servidor.getIndSituacao() == DominioSituacaoVinculo.P  ) {
				if (_servidor.getDtFimVinculo().after(new Date())) {
					return true;
				}
				return false;
			}else{
				if (_servidor.getIndSituacao() == DominioSituacaoVinculo.A){
					return true;
				}
			}
			return false;
		}

		return false;
	}

	public void pesquisar() {
		
		/**
		 * Verificar se os códigos informados nas LOV's são válidos
		 */
		//registroColaboradorFacade.verificarInformacoesValidas(vinculo, pessoaFisica, centroCustoLotacao, centroCustoAtuacao, ocupacaoCargo);

		// #41245
		if(vinculo!=null) {
			this.vinCodigo = vinculo.getCodigo();
		}

		// zerar todo resultset do paginator
		dataModel.reiniciarPaginator();

		// Ativa o uso de paguinação
		exibirNovo = true;
		dataModel.setPesquisaAtiva(true);
	}
	
	public String editar(){
		return CADASTRAR_SERVIDOR;
	}

	public String inserir(){
		return CADASTRAR_SERVIDOR;
	}
	
	/**
	 * Buscar a descrição da ocupação que será apresentada na tooltip da lista de pesquisa
	 */
	public String buscarDescricaoOcupacoCargo(Integer codigoOcupacao, String descricaoOriginal) {
		if(codigoOcupacao == null){
			return null;
		}
		
		String descricao = null;
		
		try {
			if (aghuFacade.isHCPA()) {
				descricao = aghuFacade.buscarDescricaoOcupacao(codigoOcupacao);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		if (descricao == null || descricao.isEmpty()) {
			return descricaoOriginal;
		}
		
		return descricao;
	}
	
	/**
	 * Reiniciar o paginator e os atributos da classe.
	 * 
	 */
	public void limpar() {
		dataModel.limparPesquisa();
		exibirNovo = false;
		this.pessoaFisica = null;
		this.listaPessoaFisica = null;
		this.servidor = new RapServidores();
		this.vinculo = null;
		this.listaVinculos = null;
		this.centroCustoAtuacao = null;
		this.listaCCAtuacao = null;
		this.centroCustoLotacao = null;
		this.listaCCLotacao = null;
		this.ocupacaoCargo = null;
		this.listaOcupacao = null;
		this.vinCodigo = null;
		this.matricula = null;
		this.indSituacao = null;
		this.codigoPessoa = null;
		this.nomePessoa = null;
		this.codigoCCustoLotacao = null;
		this.codigoCCustoAtuacao = null;
		this.tipoRemuneracao = null;
		this.codigoOcupacao = null;
		this.codigoCargo = null;
		this.setPesquisaAutomatica(false);
		this.setParametroCodigoPessoa(null);
		this.setVoltarPara(null);
		this.setExpirarVinculo(false);
	}
	
	/**
	 * Retorna uma lista de Vinculos.
	 * @param param
	 * @return List<RapVinculos>
	 * @throws ApplicationBusinessException
	 */
	public List<RapVinculos> pesquisarVinculo(String param) throws ApplicationBusinessException{

		try {
			this.listaVinculos = this.cadastrosBasicosFacade.pesquisarVinculoPorCodigoDescricao(param, false);
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return this.listaVinculos;
	}
	

	/**
	 * Retorna uma lista de Pessoas Fisicas.
	 * @param param
	 * @return List<RapPessoasFisicas>
	 */
	public List<RapPessoasFisicas> pesquisarPessoaFisica(String param){
		this.listaPessoaFisica = this.registroColaboradorFacade.pesquisarPessoaFisicaPorCodigoNome(param);
		return this.listaPessoaFisica;
	}

	/**
	 * Retorna uma lista de Centro de Custo.
	 * @param param
	 * @return List<FccCentroCustos>
	 */
	public List<FccCentroCustos> pesquisarCCLotacao(String param){
		this.listaCCLotacao = centroCustoFacade.pesquisarCentroCustosAtivosPorCodigoDescricaoOrdemCodigo(param, false);
		return this.listaCCLotacao;
	}

	/**
	 * Retorna uma lista de Centro de Custo.
	 * @param param
	 * @return List<FccCentroCustos>
	 */
	public List<FccCentroCustos> pesquisarCCAtuacao(String param){
		this.listaCCAtuacao = centroCustoFacade.pesquisarCentroCustosAtivosPorCodigoDescricaoOrdemCodigo(param, false);
		return this.listaCCAtuacao;
	}

	public String refazerPesquisa() {
		fromEdicao = false;
		//refaz pesquisa sem reiniciar o paginator, ou seja, pesquisando na mesma pagina
//		dirty = true;
//		TODO eScweigert Rever
		getDataModel();
		return "pesquisa";
	}	
	
	/**
	 * Retorna uma lista de Ocupações.
	 */
	public List<OcupacaoCargoVO> pesquisarOcupacao(String param) throws BaseException {
		return cadastrosBasicosFacade.pesquisarOcupacaoPorCodigo(param, false);
	}
	
	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public DominioSituacaoVinculo getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoVinculo indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getCodigoPessoa() {
		return codigoPessoa;
	}

	public void setCodigoPessoa(Integer codigoPessoa) {
		this.codigoPessoa = codigoPessoa;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	public Integer getCodigoCCustoLotacao() {
		return codigoCCustoLotacao;
	}

	public void setCodigoCCustoLotacao(Integer codigoCCustoLotacao) {
		this.codigoCCustoLotacao = codigoCCustoLotacao;
	}

	public Integer getCodigoCCustoAtuacao() {
		return codigoCCustoAtuacao;
	}

	public void setCodigoCCustoAtuacao(Integer codigoCCustoAtuacao) {
		this.codigoCCustoAtuacao = codigoCCustoAtuacao;
	}

	public DominioTipoRemuneracao getTipoRemuneracao() {
		return tipoRemuneracao;
	}

	public void setTipoRemuneracao(DominioTipoRemuneracao tipoRemuneracao) {
		this.tipoRemuneracao = tipoRemuneracao;
	}

	public Integer getCodigoOcupacao() {
		return codigoOcupacao;
	}

	public void setCodigoOcupacao(Integer codigoOcupacao) {
		this.codigoOcupacao = codigoOcupacao;
	}

	public String getCodigoCargo() {
		return codigoCargo;
	}

	public void setCodigoCargo(String codigoCargo) {
		this.codigoCargo = codigoCargo;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public boolean isExibirNovo() {
		return exibirNovo;
	}

	public void setExibirNovo(boolean exibirNovo) {
		this.exibirNovo = exibirNovo;
	}

	public RapVinculos getVinculo() {
		return vinculo;
	}

	public void setVinculo(RapVinculos vinculo) {
		this.vinculo = vinculo;
	}

	public Object getVinculoObject() {
		return vinculoObject;
	}

	public void setVinculoObject(Object vinculoObject) {
		this.vinculoObject = vinculoObject;
	}

	public List<RapVinculos> getListaVinculos() {
		return listaVinculos;
	}

	public void setListaVinculos(List<RapVinculos> listaVinculos) {
		this.listaVinculos = listaVinculos;
	}

	public RapPessoasFisicas getPessoaFisica() {
		return pessoaFisica;
	}

	public void setPessoaFisica(RapPessoasFisicas pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}

	public Object getPessoaObject() {
		return pessoaObject;
	}

	public void setPessoaObject(Object pessoaObject) {
		this.pessoaObject = pessoaObject;
	}

	public List<RapPessoasFisicas> getListaPessoaFisica() {
		return listaPessoaFisica;
	}

	public void setListaPessoaFisica(List<RapPessoasFisicas> listaPessoaFisica) {
		this.listaPessoaFisica = listaPessoaFisica;
	}

	public FccCentroCustos getCentroCustoLotacao() {
		return centroCustoLotacao;
	}

	public void setCentroCustoLotacao(FccCentroCustos centroCustoLotacao) {
		this.centroCustoLotacao = centroCustoLotacao;
	}

	public Object getLotacaoObject() {
		return lotacaoObject;
	}

	public void setLotacaoObject(Object lotacaoObject) {
		this.lotacaoObject = lotacaoObject;
	}

	public List<FccCentroCustos> getListaCCLotacao() {
		return listaCCLotacao;
	}

	public void setListaCCLotacao(List<FccCentroCustos> listaCCLotacao) {
		this.listaCCLotacao = listaCCLotacao;
	}

	public FccCentroCustos getCentroCustoAtuacao() {
		return centroCustoAtuacao;
	}

	public void setCentroCustoAtuacao(FccCentroCustos centroCustoAtuacao) {
		this.centroCustoAtuacao = centroCustoAtuacao;
	}

	public Object getAtuacaoObject() {
		return atuacaoObject;
	}

	public void setAtuacaoObject(Object atuacaoObject) {
		this.atuacaoObject = atuacaoObject;
	}

	public List<FccCentroCustos> getListaCCAtuacao() {
		return listaCCAtuacao;
	}

	public void setListaCCAtuacao(List<FccCentroCustos> listaCCAtuacao) {
		this.listaCCAtuacao = listaCCAtuacao;
	}

	public OcupacaoCargoVO getOcupacaoCargo() {
		return ocupacaoCargo;
	}

	public void setOcupacaoCargo(OcupacaoCargoVO ocupacaoCargo) {
		this.ocupacaoCargo = ocupacaoCargo;
	}

	public Object getOcupacaoObject() {
		return ocupacaoObject;
	}

	public void setOcupacaoObject(Object ocupacaoObject) {
		this.ocupacaoObject = ocupacaoObject;
	}

	public List<OcupacaoCargoVO> getListaOcupacao() {
		return listaOcupacao;
	}

	public void setListaOcupacao(List<OcupacaoCargoVO> listaOcupacao) {
		this.listaOcupacao = listaOcupacao;
	}

	public boolean isFromEdicao() {
		return fromEdicao;
	}

	public void setFromEdicao(boolean fromEdicao) {
		this.fromEdicao = fromEdicao;
	}

	public boolean isPesquisaAutomatica() {
		return pesquisaAutomatica;
	}

	public void setPesquisaAutomatica(boolean pesquisaAutomatica) {
		this.pesquisaAutomatica = pesquisaAutomatica;
	}

	public Integer getParametroCodigoPessoa() {
		return parametroCodigoPessoa;
	}

	public void setParametroCodigoPessoa(Integer parametroCodigoPessoa) {
		this.parametroCodigoPessoa = parametroCodigoPessoa;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setTamanhoMatricula(Integer tamanhoMatricula) {
		this.tamanhoMatricula = tamanhoMatricula;
	}

	public Integer getTamanhoMatricula() {
		return tamanhoMatricula;
	}

	public void setExpirarVinculo(boolean expirarVinculo) {
		this.expirarVinculo = expirarVinculo;
	}

	public boolean isExpirarVinculo() {
		return expirarVinculo;
	}

	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}

	public Date getDataAtual() {
		return dataAtual;
	}

	public void setDataExpiraVinculo(Date dataExpiraVinculo) {
		this.dataExpiraVinculo = dataExpiraVinculo;
	}

	public Date getDataExpiraVinculo() {
		return dataExpiraVinculo;
	}	 


	public DynamicDataModel<RapServidores> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapServidores> dataModel) {
	 this.dataModel = dataModel;
	}
}