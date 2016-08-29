package br.gov.mec.aghu.administracao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AghCaractMicrocomputador;
import br.gov.mec.aghu.model.AghCaractMicrocomputadorId;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller da tela de Pesquisar Microcomputador
 * 
 * @author gzapalaglio
 * 
 */
public class MicrocomputadorPaginatorController extends ActionController implements ActionPaginator {
	

	private static final long serialVersionUID = 1315829044006925836L;
	
	
	private static final String PAGE_CADASTROMICROCOMPUTADORCRUD = "cadastroMicrocomputadorCRUD";


	@Inject @Paginator
	private DynamicDataModel<AghMicrocomputador> dataModel;

	// Filtros
	private String nome;
	private AacUnidFuncionalSalas sala;
	private Short ramal;
	private DominioSimNao prioridade;
	private DominioSimNao indAtivo;
	private String usuario;
	private String ip;
	private String ponto;
	private String impPadrao;
	private String impMatricial;
	private String impEtiqueta;
	private String observacao;
	private AghUnidadesFuncionais unidadeFuncional;
	
	private String nomeMicrocomputador;
	
	// Característica(s) do Microcomputador
	private DominioCaracteristicaMicrocomputador dominioCaracteristica;
	private AghMicrocomputador microcomputador;
	private List<AghCaractMicrocomputador> caracteristicasMicrocomputador;
	private List<AghCaractMicrocomputador> caracteristicasMicrocomputadorNaoAssociadas;

	AghCaractMicrocomputadorId caracteristicaId;
	
	private AghMicrocomputador microcomputadorSelecionado;

	@EJB
	private IAdministracaoFacade administracaoFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	//@EJB
	//private IRegistroColaboradorFacade registroColaboradorFacade;

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public List<AghMicrocomputador> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		Byte salaMicrocomputador = null;
		
		if(sala!=null) {
			salaMicrocomputador = sala.getId().getSala();
		}
		
		return getAdministracaoFacade().pesquisarMicrocomputador(firstResult, maxResult, orderProperty, asc, 
				nome, salaMicrocomputador, ramal, prioridade, indAtivo, ponto, ip, usuario, impPadrao, impEtiqueta,
				impMatricial, observacao, unidadeFuncional);
	}
	
	@Override
	public Long recuperarCount() {
		
		Byte salaMicrocomputador = null;
		
		if(sala!=null) {
			salaMicrocomputador = sala.getId().getSala();
		}
		
		return getAdministracaoFacade().pesquisarMicrocomputadorCount(nome, salaMicrocomputador, ramal, prioridade, indAtivo, 
				ponto, ip, usuario, impPadrao, impEtiqueta, impMatricial, observacao, unidadeFuncional);
	}
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.nome = null;
		this.sala = null;
		this.ramal = null;
		this.prioridade = null;
		this.indAtivo = null;
		this.usuario = null;
		this.ip = null;
		this.ponto = null;
		this.impPadrao = null;
		this.impMatricial = null;
		this.impEtiqueta = null;
		this.observacao = null;
		this.unidadeFuncional = null;
		
		this.dataModel.limparPesquisa();
	}

	/**
	 * Pesquisa todas as salas existentes
	 * 
	 * @param param
	 * @return
	 */
	public List<AacUnidFuncionalSalas> pesquisaUnidadesFucionaisSala(String param) {
		return getAdministracaoFacade().listarSalasPorNumeroSala(param);
	}
	
	/**
	 * Obtém o nome do Microcomputador
	 * 
	 * @param nome
	 * @return
	 */
	public String obterNomeMicrocomputador(String nome) {
		return getAdministracaoFacade().obterNomeMicrocomputador(nome);
	}
	
	/**
	 * Obtém descrição abreviada da Unidade Funcional
	 * 
	 * @param microcomputador
	 * @return
	 * @throws BaseException 
	 */
	public String obterDescricaoUnidadeFuncionalAbreviada(AghMicrocomputador microcomputador) throws BaseException {
		return getAdministracaoFacade().obterDescricaoUnidadeFuncionalAbreviada(microcomputador);
	}
	
	/**
	 * Obtém nome do usuário abreviado
	 * 
	 * @param nomeUsuario
	 * @return
	 * @throws BaseException 
	 */
	public String obterNomeUsuarioAbreviado(String nomeUsuario) throws BaseException {
		return getAdministracaoFacade().obterNomeUsuarioAbreviado(nomeUsuario);
	}
	
	/**
	 * Pesquisa Unidades Funcionais conforme o parametro recebido
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String objPesquisa){
		String strPesquisa = (String) objPesquisa;
		return this.aghuFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoAndar(strPesquisa);
	}
	
	/**
	 * Exclui Microcomputador
	 * 
	 */
	public void excluir() {
		try {
			if(getMicrocomputadorSelecionado() != null) {
				getAdministracaoFacade().excluirMicrocomputador(getMicrocomputadorSelecionado().getNome());
			
				// Apresenta as mensagens de acordo
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_MICROCOMPUTADOR");
				
				// Reinicia a paginação
				dataModel.reiniciarPaginator();//nator(MicrocomputadorPaginatorController.class);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String editar() {
		return PAGE_CADASTROMICROCOMPUTADORCRUD;
	}
	
	/**
	 * Persiste Característica ao Microcomputador
	 * 
	 * @return
	 * @throws BaseException 
	 */
	public void adicionarCaracteristicaMicrocomputador() throws BaseException {
		try {
			if(dominioCaracteristica!=null) {
				getAdministracaoFacade().persistirCaracteristicaMicrocomputador(microcomputador, dominioCaracteristica);
	
				// Apresenta as mensagens de acordo
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INCLUSAO_CARACTERISTICA");
	
				atualizarListasCaracteristicaMicrocomputador();
				dominioCaracteristica = null;
			
				// Reinicia a paginação
				dataModel.reiniciarPaginator();
			}
			else {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_VAZIO_INCLUSAO_CARACTERISTICA");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Obtém a lista de Característica que o Microcomputador ainda NÃO possui
	 * 
	 */
	public DominioCaracteristicaMicrocomputador[] listarCaractMicrocomputadorNaoAssociadas() {
		DominioCaracteristicaMicrocomputador[] listaCaract = new DominioCaracteristicaMicrocomputador[0];
		listaCaract = DominioCaracteristicaMicrocomputador.values();
		for(DominioCaracteristicaMicrocomputador caracteristica : DominioCaracteristicaMicrocomputador.values()) {
			if(caracteristicasMicrocomputador!=null) {
				for(AghCaractMicrocomputador caract : caracteristicasMicrocomputador) {
					if(caract != null && caracteristica.equals(caract.getId().getCaracteristica())) {
						listaCaract = (DominioCaracteristicaMicrocomputador[])ArrayUtils.removeElement(listaCaract, caracteristica);
						break;
					}
				}
			}
		}
		return listaCaract;
	}
	
	/**
	 * Obtém a lista de Característica que o Microcomputador possui
	 * 
	 */
	public void listarCaracteristicasMicrocomputador() {
		caracteristicasMicrocomputador = new ArrayList<AghCaractMicrocomputador>();
		if(microcomputador!=null) {
			caracteristicasMicrocomputador = getAdministracaoFacade().pesquisaCaractMicrocomputadorPorNome(microcomputador.getNome(), null);
		}
	}
	
	public void carregarMicrocomputador(AghMicrocomputador microcomputador) {
		this.microcomputador = microcomputador;
		this.atualizarListasCaracteristicaMicrocomputador();
		this.dominioCaracteristica = null;
	}
	
	public void atualizarListasCaracteristicaMicrocomputador() {
		listarCaracteristicasMicrocomputador();
		listarCaractMicrocomputadorNaoAssociadas();
	}
	
	/**
	 * Exclui Caracteristica do Microcomputador
	 * 
	 */
	public void excluirCaracteristica() {
		try {
			if (caracteristicaId != null) {
				getAdministracaoFacade().excluirCaracteristicaMicrocomputador(caracteristicaId);
	
				// Apresenta as mensagens de acordo
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EXCLUSAO_CARACTERISTICA");
	
				atualizarListasCaracteristicaMicrocomputador();
				
				// Reinicia a paginação
				dataModel.reiniciarPaginator();//or(MicrocomputadorPaginatorController.class);
			}
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_EXCLUSAO_CARACTERISTICA");
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public AacUnidFuncionalSalas getSala() {
		return sala;
	}

	public void setSala(AacUnidFuncionalSalas sala) {
		this.sala = sala;
	}

	public Short getRamal() {
		return ramal;
	}

	public void setRamal(Short ramal) {
		this.ramal = ramal;
	}

	public String getUsuario() {
		return usuario;
	}
	
	public DominioSimNao getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(DominioSimNao prioridade) {
		this.prioridade = prioridade;
	}

	public DominioSimNao getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(DominioSimNao indAtivo) {
		this.indAtivo = indAtivo;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPonto() {
		return ponto;
	}

	public void setPonto(String ponto) {
		this.ponto = ponto;
	}

	public String getImpPadrao() {
		return impPadrao;
	}

	public void setImpPadrao(String impPadrao) {
		this.impPadrao = impPadrao;
	}

	public String getImpMatricial() {
		return impMatricial;
	}

	public void setImpMatricial(String impMatricial) {
		this.impMatricial = impMatricial;
	}

	public String getImpEtiqueta() {
		return impEtiqueta;
	}

	public void setImpEtiqueta(String impEtiqueta) {
		this.impEtiqueta = impEtiqueta;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public IAdministracaoFacade getAdministracaoFacade() {
		return administracaoFacade;
	}

	public void setAdministracaoFacade(IAdministracaoFacade facade) {
		this.administracaoFacade = facade;
	}
	
	public String getNomeMicrocomputador() {
		return nomeMicrocomputador;
	}

	public void setNomeMicrocomputador(String nomeMicrocomputador) {
		this.nomeMicrocomputador = nomeMicrocomputador;
	}

	public void setMicrocomputador(AghMicrocomputador microcomputador) {
		this.microcomputador = microcomputador;
	}

	public AghMicrocomputador getMicrocomputador() {
		return microcomputador;
	}

	public void setCaracteristicasMicrocomputador(
			List<AghCaractMicrocomputador> caracteristicasMicrocomputador) {
		this.caracteristicasMicrocomputador = caracteristicasMicrocomputador;
	}

	public List<AghCaractMicrocomputador> getCaracteristicasMicrocomputador() {
		return caracteristicasMicrocomputador;
	}

	public void setCaracteristicasMicrocomputadorNaoAssociadas(
			List<AghCaractMicrocomputador> caracteristicasMicrocomputadorNaoAssociadas) {
		this.caracteristicasMicrocomputadorNaoAssociadas = caracteristicasMicrocomputadorNaoAssociadas;
	}

	public List<AghCaractMicrocomputador> getCaracteristicasMicrocomputadorNaoAssociadas() {
		return caracteristicasMicrocomputadorNaoAssociadas;
	}
	
	public DominioCaracteristicaMicrocomputador getDominioCaracteristica() {
		return dominioCaracteristica;
	}

	public void setDominioCaracteristica(
			DominioCaracteristicaMicrocomputador dominioCaracteristica) {
		this.dominioCaracteristica = dominioCaracteristica;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	} 


	public DynamicDataModel<AghMicrocomputador> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghMicrocomputador> dataModel) {
	 this.dataModel = dataModel;
	}

	public AghMicrocomputador getMicrocomputadorSelecionado() {
		return microcomputadorSelecionado;
	}

	public void setMicrocomputadorSelecionado(AghMicrocomputador microcomputadorSelecionado) {
		this.microcomputadorSelecionado = microcomputadorSelecionado;
	}

	public AghCaractMicrocomputadorId getCaracteristicaId() {
		return caracteristicaId;
	}

	public void setCaracteristicaId(AghCaractMicrocomputadorId caracteristicaId) {
		this.caracteristicaId = caracteristicaId;
	}
}