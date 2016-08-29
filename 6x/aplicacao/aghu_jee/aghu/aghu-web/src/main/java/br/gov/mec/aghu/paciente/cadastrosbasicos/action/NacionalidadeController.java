package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de
 * nacionalidades.
 * 
 * @author david.laks
 */

public class NacionalidadeController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3765844896472728206L;
	
	private static final Log LOG = LogFactory.getLog(NacionalidadeController.class);
	
	private static final String REDIRECIONA_LISTAR_NACIONALIDADE = "nacionalidadeList";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@Inject
	private NacionalidadePaginatorController nacionalidadePaginatorController;
	
	/**
	 * Nacionalidade a ser criada/editada
	 */
	private AipNacionalidades aipNacionalidade;

	public void inicio() {
	 

		if (this.aipNacionalidade != null) {
			this.aipNacionalidade = this.cadastrosBasicosPacienteFacade
					.obterNacionalidade(this.aipNacionalidade.getCodigo());
		}

		if (this.aipNacionalidade == null) {
			this.aipNacionalidade = new AipNacionalidades();
			this.aipNacionalidade.setIndAtivo(DominioSituacao.A);
		}
	
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * nacionalidade
	 */
	public String confirmar() {
		nacionalidadePaginatorController.reiniciarPaginator();
		
		try {
			// Tarefa 659 - deixar todos textos das entidades em caixa alta via toUpperCase()
			transformarTextosCaixaAlta();
			
			boolean create = this.aipNacionalidade != null && this.aipNacionalidade.getCodigo() == null;

			if (create){
				this.cadastrosBasicosPacienteFacade
						.incluirNacionalidade(this.aipNacionalidade);
			} else {
				this.cadastrosBasicosPacienteFacade
						.atualizarNacionalidade(this.aipNacionalidade);
			}

			if (create) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_NACIONALIDADE",
						this.aipNacionalidade.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_NACIONALIDADE",
						this.aipNacionalidade.getDescricao());
			}
			
			limpar();
			
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);

			return null;
		}

		return REDIRECIONA_LISTAR_NACIONALIDADE;
	}

	private void transformarTextosCaixaAlta() {
		this.aipNacionalidade.setSigla(this.aipNacionalidade.getSigla() == null ? null : this.aipNacionalidade.getSigla().toUpperCase());
		this.aipNacionalidade.setDescricao(this.aipNacionalidade.getDescricao() == null ? null : this.aipNacionalidade.getDescricao().toUpperCase());
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Nacionalidades
	 */
	public String cancelar() {
		limpar();
		LOG.info("Cancelado");
		return REDIRECIONA_LISTAR_NACIONALIDADE;
	}
	
	private void limpar() {
		aipNacionalidade = null;
	}

	// ### GETs e SETs ###

	public AipNacionalidades getAipNacionalidade() {
		return aipNacionalidade;
	}

	public void setAipNacionalidade(AipNacionalidades aipNacionalidade) {
		this.aipNacionalidade = aipNacionalidade;
	}

}
