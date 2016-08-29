package br.gov.mec.aghu.administracao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

/**
 * Classe responsável por controlar as ações do criação e edição de microcomputadores
 * 
 * @author gzapalaglio
 */
public class MicrocomputadorController extends ActionController {
	
	private static final Log LOG = LogFactory.getLog(MicrocomputadorController.class);
	private static final long serialVersionUID = -7147750406942961583L;
	
	
	
	private static final String PAGE_CADASTROMICROCOMPUTADORLIST = "cadastroMicrocomputadorList";
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	

	//@EJB
	//private IRegistroColaboradorFacade registroColaboradorFacade;

	/**
	 * Microcomputador a ser criado/editado
	 */
	private AghMicrocomputador microcomputador;
	
	/**
	 * Atributos do Microcomputador
	 */
	private String nomeMicrocomputador;
	private DominioSimNao prioridade;
	private DominioSimNao indAtivo;

	/**
	 * Operação a ser realizada.
	 */
	private DominioOperacoesJournal operacao;
	
	
	public Boolean getIsEdicao() {
		return (DominioOperacoesJournal.UPD == operacao);
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio() throws BaseException {
	 

	 

		if (this.nomeMicrocomputador != null) {
			this.setMicrocomputador(this.administracaoFacade.obterMicrocomputadorPorNome(nomeMicrocomputador));	
			this.setOperacao(DominioOperacoesJournal.UPD);
			if(microcomputador.getAtivo()!=null) {
				if(microcomputador.getAtivo()) {
					indAtivo = DominioSimNao.S;
				}
				else {
					indAtivo = DominioSimNao.N;
				}
			}
			if(microcomputador.getPrioridade()!=null) {
				if(microcomputador.getPrioridade()) {
					prioridade = DominioSimNao.S;
				}
				else {
					prioridade = DominioSimNao.N;
				}
			}
		}
		else {
			iniciarForm();
		}
	
	}
	

	private void iniciarForm() {
		this.setMicrocomputador(new AghMicrocomputador());
		this.setNomeMicrocomputador(null);
		this.setIndAtivo(DominioSimNao.S);
		this.setPrioridade(DominioSimNao.N);
		this.setOperacao(DominioOperacoesJournal.INS);
	}
	
	public String confirmar() {
		try {
			//RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());

			if(prioridade!=null) {
				this.microcomputador.setPrioridade(prioridade.isSim());
			}
			if(indAtivo!=null) {
				this.microcomputador.setAtivo(indAtivo.isSim());
			}
			
			// Operação de inserção / atualização
			if(this.operacao.equals(DominioOperacoesJournal.INS)){
				this.administracaoFacade.persistirMicrocomputador(microcomputador);
			}
			else {
				this.administracaoFacade.atualizarMicrocomputador(microcomputador);
			}
			
			//this.reiniciarPaginator(MicrocomputadorPaginatorController.class);
			
			if(this.operacao.equals(DominioOperacoesJournal.INS)) {
				apresentarMsgNegocio(Severity.INFO,
						"MSG_MICROCOMPUTADOR_GRAVADO_SUCESSO");
			} else if(this.operacao.equals(DominioOperacoesJournal.UPD)) {
				apresentarMsgNegocio(Severity.INFO,
						"MSG_MICROCOMPUTADOR_ATUALIZADO_SUCESSO");
			}
			
			iniciarForm();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (BaseException e) {
			LOG.error("Exceção capturada: ", e);
			if (this.operacao.equals(DominioOperacoesJournal.INS)) {
				apresentarMsgNegocio(Severity.ERROR, "MSG_MICROCOMPUTADOR_GRAVADO_ERRO");
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MSG_MICROCOMPUTADOR_ATUALIZADO_ERRO");
			}
			return null;
		}
		
		return PAGE_CADASTROMICROCOMPUTADORLIST;
	}
	
	/**
	 * Método que limpa as salas relacionadas com a Unidade Funcional.
	 */
	public void limparSalas() {
		this.microcomputador.setAghUnidadesFuncionais(null);
		this.microcomputador.setAacUnidFuncionalSala(null);
		this.microcomputador.setAelSalasExecutorasExames(null);
	}
	
	
	/**
	 * Método que realiza a pesquisa de salas por Unidade Funcional.
	 */
	public List<AacUnidFuncionalSalas> pesquisarListaSalasPorUnidadeFuncional(String param) {
		return this.ambulatorioFacade.obterListaSalasPorUnidadeFuncional(microcomputador.getAghUnidadesFuncionais(), param);
	}
	
	/**
	 * Método que realiza a pesquisa da Sala Executora de Exames por Unidade Funcional.
	 */
	public List<AelSalasExecutorasExames>  pesquisarSalaExecutoraExamesPorUnidadeFuncional(String param) {
		return this.examesFacade.obterSalaExecutoraExamesPorUnidadeFuncional(microcomputador.getAghUnidadesFuncionais(), param);
	}
	
	/**
	 * Pesquisa Unidades Funcionais conforme o parametro recebido
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String objPesquisa){
		String strPesquisa = (String) objPesquisa;
		return aghuFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoAndar(strPesquisa);
	}
	
	/**
	 * Método que realiza a ação do botão cancelar.
	 */
	public String cancelar() {
		// Força refazer a pesquisa, para não ficar os valores modificados nesta edição (que foi cancelada)
		//reiniciarPaginator(MicrocomputadorPaginatorController.class);
		iniciarForm();
		LOG.info("Cancelado");
		return PAGE_CADASTROMICROCOMPUTADORLIST;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setMicrocomputador(AghMicrocomputador microcomputador) {
		this.microcomputador = microcomputador;
	}
	
	public String getNomeMicrocomputador() {
		return nomeMicrocomputador;
	}

	public void setNomeMicrocomputador(String nomeMicrocomputador) {
		this.nomeMicrocomputador = nomeMicrocomputador;
	}

	public AghMicrocomputador getMicrocomputador() {
		return microcomputador;
	}

	public void setPrioridade(DominioSimNao prioridade) {
		this.prioridade = prioridade;
	}

	public DominioSimNao getPrioridade() {
		return prioridade;
	}

	public void setIndAtivo(DominioSimNao indAtivo) {
		this.indAtivo = indAtivo;
	}

	public DominioSimNao getIndAtivo() {
		return indAtivo;
	}
}
