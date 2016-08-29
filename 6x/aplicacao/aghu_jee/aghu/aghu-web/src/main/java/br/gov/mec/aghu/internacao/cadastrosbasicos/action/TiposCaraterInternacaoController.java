package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;

/**
 * Classe responsável por controlar as ações do criação e edição de tipo de caráter
 * de internação.
 * 
 */

public class TiposCaraterInternacaoController extends ActionController {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4960301130778578965L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Tipo de caráter de internação a ser criada/editada
	 */
	private AinTiposCaraterInternacao tipoCaraterInternacao;
	
	private final String PAGE_PESQUISAR_TIPO_CARATER_INTERNACAO = "tiposCaraterInternacaoList";

	@PostConstruct
	public void init() {
		begin(conversation);
		tipoCaraterInternacao=new AinTiposCaraterInternacao();
	}
	
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * Tipo de Caráter de Internação
	 */
	public String confirmar() {
		try {
			boolean update = this.tipoCaraterInternacao.getCodigo() != null;
			
			this.cadastrosBasicosInternacaoFacade
					.persistirTiposCaraterInternacao(this.tipoCaraterInternacao);

			if (!update) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_TIPOSCARATERINTERNACAO",
						this.tipoCaraterInternacao.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_TIPOSCARATERINTERNACAO",
						this.tipoCaraterInternacao.getDescricao());
			}
			tipoCaraterInternacao=new AinTiposCaraterInternacao();
			return PAGE_PESQUISAR_TIPO_CARATER_INTERNACAO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String cancelar() {
		tipoCaraterInternacao=new AinTiposCaraterInternacao();
		return PAGE_PESQUISAR_TIPO_CARATER_INTERNACAO;
	}

	public AinTiposCaraterInternacao getTipoCaraterInternacao() {
		return tipoCaraterInternacao;
	}

	public void setTipoCaraterInternacao(AinTiposCaraterInternacao tipoCaraterInternacao) {
		this.tipoCaraterInternacao = tipoCaraterInternacao;
	}

	
}
