package br.gov.mec.aghu.registrocolaborador.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapGrupoFuncional;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.registrocolaborador.vo.OcupacaoCargoVO;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ServidorController extends ActionController {

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ServidorController.class);

	private static final long serialVersionUID = -8297503365529920006L;

	private static final String PESQUISAR_SERVIDOR = "pesquisarServidor";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private ServidorPaginatorController servidorPaginatorController;
	
	private RapServidores servidor = new RapServidores();

	private String codigoPessoa;
	private String parametroCodigoPessoa; //Código da pessoa utilizado para retornar para a tela de cadastro
	private Short vinCodigo;
	private Short codigoVinculo;
	private Integer codigoMatricula;
	private Integer codigoCCustoLotacao;
	private Integer codigoCCustoAtuacao;
	private Integer codigoOcupacao;
	private Short codigoGrupoFuncional;
	private String funcaoCracha;
	private String idadeServidor;
	private String tempoContrato;
	private FccCentroCustos chefeCentroCusto;
	private String afastamento;
	private boolean edicao = false;
	private boolean producao = false;
	private boolean mostrarAlerta = false;
	private String mensagemModal;	
	private Integer tamanhoMatricula;	
	private RapPessoasFisicas pessoaFisica;

	private RapVinculos vinculo;
	private FccCentroCustos centroCustoLotacao;
	private FccCentroCustos centroCustoAtuacao;
	private OcupacaoCargoVO ocupacao;
	private RapGrupoFuncional grupoFuncional;

	private Boolean mostraCampoMatricula;

	private Usuario usuario;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * Carregar os valores dos atributos ao iniciar a página de cadastro 
	 */
	public String iniciar() throws ApplicationBusinessException {
	 

		
		if(mostrarAlerta){
			return null;
		}

		if (codigoMatricula != null && codigoVinculo != null) {
			try{
				servidor = registroColaboradorFacade.obterServidor(new RapServidoresId(codigoMatricula, codigoVinculo));

				if (servidor == null) {
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return null;
				}

				setPessoaFisica(servidor.getPessoaFisica());
				
				this.chefeCentroCusto = centroCustoFacade.pesquisarCentroCustosPorMatriculaVinculo( servidor.getId().getMatricula(), 
																									servidor.getId().getVinCodigo());
				
				
				this.setFuncaoCracha(aghuFacade.buscarFuncaoCracha(codigoMatricula, codigoVinculo));				
				this.setIdadeServidor(registroColaboradorFacade.obterIdadeExtenso(servidor.getPessoaFisica().getDtNascimento()));								
				this.setTempoContrato(registroColaboradorFacade.obterTempoExtenso(servidor.getDtInicioVinculo(), servidor.getDtFimVinculo()));
				this.setCodigoPessoa(servidor.getPessoaFisica().getCodigo().toString());
				this.setVinCodigo(servidor.getVinculo().getCodigo());

				this.setAfastamento(registroColaboradorFacade.obterAfastamento(servidor.getDtFimVinculo(), codigoMatricula, codigoVinculo));

				if (servidor.getCentroCustoAtuacao() != null) {
					this.setCentroCustoAtuacao(servidor.getCentroCustoAtuacao());
				}
				
				if (servidor.getCentroCustoLotacao() != null) {
					setCentroCustoLotacao(servidor.getCentroCustoLotacao());
				}

				if (servidor.getOcupacaoCargo() != null) {
					this.setCodigoOcupacao(servidor.getOcupacaoCargo().getCodigo());
					
					ocupacao = new OcupacaoCargoVO();
					ocupacao.setCodigoOcupacao(servidor.getOcupacaoCargo().getCodigo());
					ocupacao.setDescricaoOcupacao(servidor.getOcupacaoCargo().getDescricao());
					

					// Quando for HCPA, carregar a descrição da ocupação
					// cadastrada nas tabelas da STARH
					if (aghuFacade.isHCPA()) {
						String descricaoOcupacao = registroColaboradorFacade.obterDescricaoOcupacaoTabelaSTARH(servidor.getOcupacaoCargo().getCodigo());
						servidor.getOcupacaoCargo().setDescricao(descricaoOcupacao);
						ocupacao.setDescricaoOcupacao(descricaoOcupacao);
					}
				}

				if (servidor.getGrupoFuncional() != null) {
					this.setCodigoGrupoFuncional(servidor.getGrupoFuncional().getCodigo());
				}

				if (servidor.getUsuario() != null) {
					setUsuario(cascaFacade.recuperarUsuario(servidor.getUsuario()));
				}				

				this.edicao = true;
			} catch (BaseException mbe) {
				apresentarExcecaoNegocio(mbe);
				return null;
			}
		}else{			
			servidor.setIndSituacao(DominioSituacaoVinculo.A);
			servidor.setId(new RapServidoresId());
			// Caso uma pessoa física tenha sido selecionada na tela anterior
			if (servidorPaginatorController.getPessoaFisica() != null) {
				setPessoaFisica(servidorPaginatorController.getPessoaFisica());
				//setEdicao(Boolean.TRUE);
			}
			
			if(this.parametroCodigoPessoa != null){
				this.setCodigoPessoa(parametroCodigoPessoa);
				this.buscarPessoaFisica(parametroCodigoPessoa);
			}
		}

		//VALOR DEFAULT
		this.setTamanhoMatricula(7);
		try{
			AghParametros parametroTamanhoMatricula = parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_TAMANHO_MATRICULA);
			if (parametroTamanhoMatricula.getVlrNumerico() != null) {
				this.setTamanhoMatricula(Integer.valueOf(parametroTamanhoMatricula.getVlrNumerico().toString()));
			}	
		}
		catch (ApplicationBusinessException ex) {
			//Deixa prosseguir pois o tamanho default foi definido como 7
			LOG.error("Erro ao obter o tamanho da matricula.", ex);
		}

		// Atualiza os atributos que serão utilizados para controle na tela de cadastro
		this.setProducao(aghuFacade.isProducaoHCPA());		
		
		return null;
	
	}

	/**
	 * Realizar a chamada para a inserção/atualização de servidores
	 */
	public String salvar() {
		try {
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			// Caso 1: Usuario foi excluído ou não selecionado
			if (usuario == null) {
				servidor.setUsuario(null);
			}

			// Caso 2: Usuario foi escolhido			 
			if (usuario != null) {
				cascaFacade.validarAutenticacaoNegocial(usuario.getLogin());										
				servidor.setUsuario(usuario.getLogin());	
			}

			// Caso uma pessoa física tenha sido selecionada é adicionada ao servidor.
			if (getPessoaFisica() != null) {
				getServidor().setPessoaFisica(getPessoaFisica());
			}
			
			if (edicao) {
				registroColaboradorFacade.alterar(servidor, servidorLogado);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVIDOR_ALTERADO_COM_SUCESSO");
			} else {
				registroColaboradorFacade.inserir(servidor);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVIDOR_INCLUIDO_COM_SUCESSO");
			}
			
			mostrarAlerta = false;
			
			return cancelarCadastro();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}


	/**
	 * Montar as confirmações/alertas que deverão ser apresentados ao usuário
	 */
	public String realizarConfirmacoes() {

		String retorno = null;
		this.mostrarAlerta = false;

		try {
			this.mensagemModal = registroColaboradorFacade.montarConfirmacoes(this.servidor, this.edicao);
			if (mensagemModal != null) {
				this.mostrarAlerta = true;
				this.openDialog("modalConfirmacaoWG");
			} else {
				retorno = this.salvar();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return retorno;
	}

	public String cancelarCadastro() {
		limparCadastro();
		return PESQUISAR_SERVIDOR;
	}

	private void limparCadastro() {
		this.servidor = new RapServidores();
		this.codigoPessoa = null;
		this.pessoaFisica = null;
		this.vinculo=null;
		this.centroCustoLotacao = null;
		this.centroCustoAtuacao = null;
		this.ocupacao = null;
		this.grupoFuncional = null;
		this.vinCodigo = null;
		this.codigoVinculo = null;
		this.codigoMatricula = null;
		this.codigoCCustoLotacao = null;
		this.codigoCCustoAtuacao = null;
		this.codigoOcupacao = null;
		this.codigoGrupoFuncional = null;
		this.funcaoCracha = null;
		this.idadeServidor = null;
		this.tempoContrato = null;
		this.chefeCentroCusto = new FccCentroCustos();
		this.afastamento = null;
		this.edicao = false;
		this.mostrarAlerta = false;
		this.mensagemModal = null;
		this.usuario = null;
		this.setParametroCodigoPessoa(null);
	}

	/**
	 * Busca a pessoa física que será utilizada na pesquisa.
	 */
	public List<RapPessoasFisicas> buscarPessoaFisica(String param) throws ApplicationBusinessException {
		return this.registroColaboradorFacade.pesquisarPessoaFisicaPorCodigoNome(param);
	}

	/**
	 * Retorna uma lista de pessoas.
	 * @param param
	 * @return List<RapPessoasFisicas>
	 */
	public List<RapPessoasFisicas> pesquisarPessoaFisica(String param){
		return registroColaboradorFacade.pesquisarPessoaFisicaPorCodigoNome(param);
	}

	/**
	 * Busca o vínculo que será utilizado na pesquisa.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void buscarVinculo() throws ApplicationBusinessException {

		Short vinculoServidor = this.getVinCodigo();
		this.servidor.setVinculo(null);

		if (vinculoServidor != null) {
			this.servidor.setVinculo(cadastrosBasicosFacade.buscarVinculos(vinculoServidor, true));
		}

		verificaMatricula();
	}

	/**
	 * @throws ApplicationBusinessException
	 */
	public void verificaMatricula() throws ApplicationBusinessException {
		AghParametros matriculaUnificada = parametroFacade
		.buscarAghParametro(AghuParametrosEnum.P_AGHU_MATRICULA_UNIFICADA);

		Integer codigoMatricula = null;
		if ((matriculaUnificada.getVlrTexto() != null)
				&& (matriculaUnificada.getVlrTexto().equals("S")) && !verificarTabelaVazia()) {

			codigoMatricula = registroColaboradorFacade.obterProximoCodStarhLivre();
			this.servidor.getId().setMatricula(codigoMatricula);
			this.setMostraCampoMatricula(false);

		} else {
			if ((servidor.getVinculo() != null)
					&& (servidor.getVinculo().getIndGeraMatricula() != null)) {
				if (servidor.getVinculo().getIndGeraMatricula() == DominioSimNao.S && !verificarTabelaVazia()) {
					this.servidor.getId().setMatricula(
							registroColaboradorFacade
							.obterProximoCodStarhLivre());
					this.setMostraCampoMatricula(false);
				} else {
					this.setMostraCampoMatricula(true);
					this.servidor.getId().setMatricula(null);
				}
			} else {

				this.setMostraCampoMatricula(false);
				this.servidor.getId().setMatricula(null);

			}
		}
	}

	private Boolean verificarTabelaVazia() throws ApplicationBusinessException{
		Boolean retorno = false;
		Integer proximoCod = registroColaboradorFacade.obterProximoCodStarhLivre();
		if (proximoCod == null){
			retorno = true;
		}
		return retorno;
	}


	/**
	 * Retorna uma lista de Vinculos.
	 * @param param
	 * @return List<RapVinculos>
	 * @throws ApplicationBusinessException
	 */
	public List<RapVinculos> pesquisarVinculo(String param) throws ApplicationBusinessException{
		return cadastrosBasicosFacade.pesquisarVinculoPorCodigoDescricao(param, true);
	}

	/**
	 * Busca o centro de custo que será utilizado na pesquisa.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void buscarCCLotacao() {
		Integer codigoCentroCusto = this.getCodigoCCustoLotacao();
		this.servidor.setCentroCustoLotacao(null);

		if (codigoCentroCusto != null) {
			this.servidor.setCentroCustoLotacao(centroCustoFacade.obterFccCentroCustosAtivos(codigoCentroCusto));
		}
	}

	/**
	 * Retorna uma lista de Centro de Custo
	 * @param param
	 * @return List<FccCentroCustos>

	 */
	public List<FccCentroCustos> pesquisarCCLotacao(String param){
		return centroCustoFacade.pesquisarCentroCustosAtivosPorCodigoDescricaoOrdemCodigo(param, true);
	}

	/**
	 * Busca o centro de custo que será utilizado na pesquisa.
	 */
	public void buscarCCAtuacao() {

		Integer codigoCentroCusto = this.getCodigoCCustoAtuacao();
		this.servidor.setCentroCustoAtuacao(null);

		if (codigoCentroCusto != null) {
			this.servidor.setCentroCustoAtuacao(centroCustoFacade.obterFccCentroCustosAtivos(codigoCentroCusto));
		}
	}

	/**
	 * Retorna uma lista de Centro de Custo.
	 * @param param
	 * @return List<FccCentroCustos>
	 */
	public List<FccCentroCustos> pesquisarCCAtuacao(String param){
		return centroCustoFacade.pesquisarCentroCustosAtivosPorCodigoDescricaoOrdemCodigo(param, true);
	}

	/**
	 * Busca a ocupação que será utilizada na pesquisa.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void buscarOcupacao() {

		Integer codigoOcupacao = this.getCodigoOcupacao();
		this.servidor.setOcupacaoCargo(null);

		if (codigoOcupacao != null) {
			try {
				this.servidor.setOcupacaoCargo(cadastrosBasicosFacade.obterOcupacaoCargoPorCodigo(codigoOcupacao,true));
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 * Retorna uma lista de Ocupações.
	 */
	public List<OcupacaoCargoVO> pesquisarOcupacao(String param) throws ApplicationBusinessException, ApplicationBusinessException{
		return cadastrosBasicosFacade.pesquisarOcupacaoPorCodigo(param, true);
	}

	/**
	 * Pesquisa usada pela SB que deve retornar todos os usuários
	 * passíveis de serem associados com o servidor em edição
	 * 
	 * @param objParam Nome ou login do usuário
	 * @return Lista de usuários que podem ser associados
	 */
	public List<Usuario> pesquisarUsuario(String objParam) {
		String strPesquisa = (String) objParam;
		return cascaFacade.pesquisarUsuariosAssociaveis(strPesquisa);
	}	

	/**
	 * Busca a ocupação que será utilizada na pesquisa.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void buscarGrupoFuncional() {
		Short codigoGrupoFuncional = this.getCodigoGrupoFuncional();
		this.servidor.setGrupoFuncional(null);

		if (codigoGrupoFuncional != null) {
			try {
				this.servidor.setGrupoFuncional(cadastrosBasicosFacade.pesquisarGrupoFuncionalPorCodigo(codigoGrupoFuncional));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 * Retorna uma lista de Grupo Funcional.
	 * @param param
	 * @return List<RapGrupoFuncional>
	 */
	public List<RapGrupoFuncional> pesquisarGrupoFuncional(String param){
		return cadastrosBasicosFacade.pesquisarGrupoFuncional(param);
	}

	public List<RapRamalTelefonico> pesquisarRamais(String objParam) {
		return cadastrosBasicosFacade.pesquisarRamalTelefonicoPorNroRamal(objParam);
	}

	public void buscarChefeCCusto() {
		if (servidor.getId() != null) {
			this.chefeCentroCusto = centroCustoFacade.pesquisarCentroCustosPorMatriculaVinculo(servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
		}
	}

	/**
	 * Método que cancela a modal de confirmações
	 */
	public void cancelarModal() {
		this.mostrarAlerta = false;
		this.mensagemModal = null;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	} 

	public void setPessoaFisica(RapPessoasFisicas pessoaFisica) throws ApplicationBusinessException {
		if (pessoaFisica != null) {
			setCodigoPessoa(pessoaFisica.getCodigo().toString());
			this.servidor.setPessoaFisica(pessoaFisica);
		}
		this.pessoaFisica = pessoaFisica;
	}

	public RapPessoasFisicas getPessoaFisica() {
		return pessoaFisica;
	}

	public String getCodigoPessoa() {
		return codigoPessoa;
	}

	public void setCodigoPessoa(String codigoPessoa) {
		this.codigoPessoa = codigoPessoa;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public void setVinculo(RapVinculos vinculo) throws ApplicationBusinessException {
		this.vinculo = vinculo;

		if (vinculo != null) {
			setVinCodigo(vinculo.getCodigo());
			this.servidor.setVinculo(vinculo);
		}

		verificaMatricula();
	}

	public RapVinculos getVinculo() {
		return vinculo;
	} 

	public void setCentroCustoLotacao(FccCentroCustos centroCustoLotacao) {

		if(centroCustoLotacao!=null){
			setCodigoCCustoLotacao(centroCustoLotacao.getCodigo());
			this.servidor.setCentroCustoLotacao(centroCustoLotacao);
		}

		this.centroCustoLotacao = centroCustoLotacao;
	}

	public FccCentroCustos getCentroCustoLotacao() {
		return centroCustoLotacao;
	}

	public void setCentroCustoAtuacao(FccCentroCustos centroCustoAtuacao) {

		if(centroCustoAtuacao!=null){
			setCodigoCCustoAtuacao(centroCustoAtuacao.getCodigo());
			this.servidor.setCentroCustoAtuacao(centroCustoAtuacao);
		}

		this.centroCustoAtuacao = centroCustoAtuacao;
	}

	public FccCentroCustos getCentroCustoAtuacao() {
		return centroCustoAtuacao;
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

	public Integer getCodigoOcupacao() {
		return codigoOcupacao;
	}

	public void setCodigoOcupacao(Integer codigoOcupacao) {
		this.codigoOcupacao = codigoOcupacao;
	}

	public void setOcupacao(OcupacaoCargoVO ocupacao) {
		if(ocupacao!=null){
			setCodigoOcupacao(ocupacao.getCodigoOcupacao());
			this.buscarOcupacao();
		}

		this.ocupacao = ocupacao;
	}

	public OcupacaoCargoVO getOcupacao() {
		return ocupacao;
	}

	public Short getCodigoVinculo() {
		return codigoVinculo;
	}

	public void setCodigoVinculo(Short codigoVinculo) {
		this.codigoVinculo = codigoVinculo;
	}

	public Integer getCodigoMatricula() {
		return codigoMatricula;
	}

	public void setCodigoMatricula(Integer codigoMatricula) {
		this.codigoMatricula = codigoMatricula;
	}

	public FccCentroCustos getChefeCentroCusto() {
		return chefeCentroCusto;
	}

	public void setChefeCentroCusto(FccCentroCustos chefeCentroCusto) {
		this.chefeCentroCusto = chefeCentroCusto;
	}

	public Short getCodigoGrupoFuncional() {
		return codigoGrupoFuncional;
	}

	public void setCodigoGrupoFuncional(Short codigoGrupoFuncional) {
		this.codigoGrupoFuncional = codigoGrupoFuncional;
	}

	public void setGrupoFuncional(RapGrupoFuncional grupoFuncional) {
		if(grupoFuncional!=null){
			setCodigoGrupoFuncional(grupoFuncional.getCodigo());
			buscarGrupoFuncional();
		}
		this.grupoFuncional = grupoFuncional;
	}

	public RapGrupoFuncional getGrupoFuncional() {
		return grupoFuncional;
	}

	public String getFuncaoCracha() {
		return funcaoCracha;
	}

	public void setFuncaoCracha(String funcaoCracha) {
		this.funcaoCracha = funcaoCracha;
	}

	public String getIdadeServidor() {
		return idadeServidor;
	}

	public void setIdadeServidor(String idadeServidor) {
		this.idadeServidor = idadeServidor;
	}

	public String getTempoContrato() {
		return tempoContrato;
	}

	public void setTempoContrato(String tempoContrato) {
		this.tempoContrato = tempoContrato;
	}

	public String getAfastamento() {
		return afastamento;
	}

	public void setAfastamento(String afastamento) {
		this.afastamento = afastamento;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public boolean isMostrarAlerta() {
		return mostrarAlerta;
	}

	public void setMostrarAlerta(boolean mostrarAlerta) {
		this.mostrarAlerta = mostrarAlerta;
	}

	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}

	public String getModalMessage() {
		return this.mensagemModal;
	}

	public boolean isProducao() {
		return producao;
	}

	public void setProducao(boolean producao) {
		this.producao = producao;
	}

	public Boolean getMostraCampoMatricula() {
		return mostraCampoMatricula;
	}

	public void setMostraCampoMatricula(Boolean mostraCampoMatricula) {
		this.mostraCampoMatricula = mostraCampoMatricula;
	}

	public String getParametroCodigoPessoa() {
		return parametroCodigoPessoa;
	}

	public void setParametroCodigoPessoa(String parametroCodigoPessoa) {
		this.parametroCodigoPessoa = parametroCodigoPessoa;
	}	

	public void setTamanhoMatricula(Integer tamanhoMatricula) {
		this.tamanhoMatricula = tamanhoMatricula;
	}

	public Integer getTamanhoMatricula() {
		return tamanhoMatricula;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}