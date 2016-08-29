package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipSinonimosOcupacao;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de ocupações.
 * 
 * @author david.laks
 */


public class OcupacaoController extends ActionController {

	private static final long serialVersionUID = 613407561573030L;

	private static final String REDIRECT_OCUPACAO_LIST = "ocupacaoList";
	
	private static final Log LOG = LogFactory.getLog(OcupacaoController.class);
	
	private static final Comparator<AipSinonimosOcupacao> COMPARATOR_SINONIMOS_OCUPACAO = new Comparator<AipSinonimosOcupacao>() {

		@Override
		public int compare(AipSinonimosOcupacao o1, AipSinonimosOcupacao o2) {
			return o1.getDescricao().toUpperCase().compareTo(
					o2.getDescricao().toUpperCase());
		}

	};

	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	private Integer codigoOcupacao;

	private AipOcupacoes aipOcupacao;

	private AipSinonimosOcupacao sinonimo;

	private List<AipSinonimosOcupacao> sinonimosOcupacao;
	private List<AipSinonimosOcupacao> sinonimosOcupacaoInseridos;
	private List<AipSinonimosOcupacao> sinonimosOcupacaoRemovidos;

	private boolean operacaoConcluida = false;

	public void inicio(){
		if(codigoOcupacao == null){
			aipOcupacao = new AipOcupacoes();
			sinonimosOcupacao = new ArrayList<AipSinonimosOcupacao>();
		} else {
			aipOcupacao = cadastrosBasicosPacienteFacade.obterOcupacao(codigoOcupacao);
			sinonimosOcupacao = cadastrosBasicosPacienteFacade.pesquisarSinonimosPorOcupacao(aipOcupacao);
		}
		sinonimosOcupacaoInseridos = new ArrayList<AipSinonimosOcupacao>();
		sinonimosOcupacaoRemovidos = new ArrayList<AipSinonimosOcupacao>();
	}

	public void removerSinonimo(AipSinonimosOcupacao sinonimo) {
		sinonimosOcupacaoRemovidos.add(sinonimo);
		sinonimosOcupacao.remove(sinonimo);
	}

	public void associarSinonimo() {
		
		try {
			boolean sinonimoJaEscolhido = false;
			for (AipSinonimosOcupacao _sinonimo : this.sinonimosOcupacao) {
				if (_sinonimo.getDescricao().equalsIgnoreCase(this.sinonimo.getDescricao())) {
					sinonimoJaEscolhido = true;
					break;
				}
			}

			if (sinonimoJaEscolhido) {
				this.apresentarMsgNegocio(Severity.ERROR, "SINONIMO_JA_ESCOLHIDO");
				
			} else {
				cadastrosBasicosPacienteFacade.validarSinonimo(this.sinonimo, this.sinonimosOcupacao, this.aipOcupacao);
				sinonimosOcupacao.add(this.sinonimo);
				sinonimosOcupacaoInseridos.add(sinonimo);
				
				Collections.sort(sinonimosOcupacao, COMPARATOR_SINONIMOS_OCUPACAO);

				this.operacaoConcluida = true;
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	

	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * ocupação
	 */
	public String confirmar() {
		
		try {
			
			boolean create = this.aipOcupacao.getCodigo() == null;

			this.cadastrosBasicosPacienteFacade.persistirOcupacao(this.aipOcupacao, sinonimosOcupacaoInseridos, sinonimosOcupacaoRemovidos);
			
			if (create) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_OCUPACAO",this.aipOcupacao.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_OCUPACAO", this.aipOcupacao.getDescricao());
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);			
			return null;
		}

		return REDIRECT_OCUPACAO_LIST;
	} 

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * ocupações
	 */
	public String cancelar() {
		LOG.info("Cancelado");
		return REDIRECT_OCUPACAO_LIST;
	}

	/**
	 * remove o sinonimo selecionado do contexto.
	 */
	public void limparSinonimo() {
		this.sinonimo = new AipSinonimosOcupacao();
		this.operacaoConcluida = false;
		openDialog("sinonimosOcupacaoWG");
		
	}



	public AipOcupacoes getAipOcupacao() {
		return aipOcupacao;
	}

	public void setAipOcupacao(AipOcupacoes aipOcupacao) {
		this.aipOcupacao = aipOcupacao;
	}

	public AipSinonimosOcupacao getSinonimo() {
		return sinonimo;
	}

	public void setSinonimo(AipSinonimosOcupacao sinonimo) {
		this.sinonimo = sinonimo;
	}

	public boolean isOperacaoConcluida() {
		return operacaoConcluida;
	}

	public void setOperacaoConcluida(boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public List<AipSinonimosOcupacao> getSinonimosOcupacao() {
		return sinonimosOcupacao;
	}

	public void setSinonimosOcupacao(
			List<AipSinonimosOcupacao> sinonimosOcupacao) {
		this.sinonimosOcupacao = sinonimosOcupacao;
	}

	public List<AipSinonimosOcupacao> getSinonimosOcupacaoInseridos() {
		return sinonimosOcupacaoInseridos;
	}

	public void setSinonimosOcupacaoInseridos(
			List<AipSinonimosOcupacao> sinonimosOcupacaoInseridos) {
		this.sinonimosOcupacaoInseridos = sinonimosOcupacaoInseridos;
	}

	public List<AipSinonimosOcupacao> getSinonimosOcupacaoRemovidos() {
		return sinonimosOcupacaoRemovidos;
	}

	public void setSinonimosOcupacaoRemovidos(
			List<AipSinonimosOcupacao> sinonimosOcupacaoRemovidos) {
		this.sinonimosOcupacaoRemovidos = sinonimosOcupacaoRemovidos;
	}


	public Integer getCodigoOcupacao() {
		return codigoOcupacao;
	}


	public void setCodigoOcupacao(Integer codigoOcupacao) {
		this.codigoOcupacao = codigoOcupacao;
	} 
}
