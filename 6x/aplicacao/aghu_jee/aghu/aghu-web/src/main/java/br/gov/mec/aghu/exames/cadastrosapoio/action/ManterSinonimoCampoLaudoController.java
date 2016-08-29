package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelSinonimoCampoLaudo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterSinonimoCampoLaudoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterSinonimoCampoLaudoController.class);

	private static final long serialVersionUID = -5791502610365472403L;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	// Parâmetros da conversação
	private Integer seq;
	private String voltarPara; // O padrão é voltar para interface de pesquisa

	// Variaveis que representam os campos do XHTML
	private AelCampoLaudo campoLaudo;
	private AelSinonimoCampoLaudo sinonimoCampoLaudo = new AelSinonimoCampoLaudo();

	// Lista contendo os Sinônimos de Campo Laudo
	List<AelSinonimoCampoLaudo> listaSinonimoCampoLaudo = new LinkedList<AelSinonimoCampoLaudo>();

	// Controla a exibição dos botões gravar, alterar e cancelar, assimo como o tipo de operação realizada
	private boolean editandoItem;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 


		if (this.seq != null) {

			// Obtém Campo Laudo
			this.campoLaudo = this.examesFacade.obterCampoLaudoPorSeq(this.seq);

			if (this.campoLaudo != null) {
				// Popula Lista contendo os Sinônimos de Campo Laudo
				this.listaSinonimoCampoLaudo = this.examesFacade.pesquisarSinonimoCampoLaudoPorCampoLaudo(this.campoLaudo);
			}

		}
	
	}

	/**
	 * Confirma a operacao de gravar/alterar
	 * 
	 * @return
	 */
	public void confirmar() {

		try {

			// Para operação de inclusão o Campo Laudo será associado ao Sinônimo
			if (!this.editandoItem) {
				this.sinonimoCampoLaudo.setCampoLaudo(this.campoLaudo);
			}

			// Remove espaços em branco do nome
			this.sinonimoCampoLaudo.setNome(StringUtils.trim(this.sinonimoCampoLaudo.getNome()));

			// Persiste Campo Laudo
			this.cadastrosApoioExamesFacade.persistirSinonimoCampoLaudo(this.sinonimoCampoLaudo);

			// Determina o tipo de mensagem de confirmação através da operação
			String mensagem = null;
			if (this.editandoItem) {
				mensagem = "MENSAGEM_SUCESSO_ALTERAR_SINONIMO_CAMPO_LAUDO";
			} else {
				mensagem = "MENSAGEM_SUCESSO_INSERIR_SINONIMO_CAMPO_LAUDO";
			}
			this.apresentarMsgNegocio(Severity.INFO, mensagem, sinonimoCampoLaudo.getNome());

			// Limpa novo/antigo Sinônimo de Campo Laudo
			this.sinonimoCampoLaudo = new AelSinonimoCampoLaudo();
			this.editandoItem = false;

			this.iniciar();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage());
		}

	}

	/**
	 * Edita item de Sinônimo de Campo Laudoselecionado na lista
	 * 
	 * @param dtValidadeNumerica
	 */
	public void editar(final AelSinonimoCampoLaudo sinonimoCampoLaudo) {
		this.sinonimoCampoLaudo = sinonimoCampoLaudo;
		this.editandoItem = true;
	}

	/**
	 * Verifica se o item de Sinônimo foi selecionado na lista
	 * 
	 * @param sinonimoCampoLaudo
	 * @return
	 */
	public boolean isItemSelecionado(final AelSinonimoCampoLaudo sinonimoCampoLaudo) {
		if (this.sinonimoCampoLaudo != null && this.sinonimoCampoLaudo.equals(sinonimoCampoLaudo)) {
			return true;
		}
		return false;
	}

	/**
	 * Cancela edição do item de Sinônimo selecionado
	 */
	public void cancelarEdicao() {
		this.sinonimoCampoLaudo = new AelSinonimoCampoLaudo();
		this.editandoItem = false;
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {

		String retorno = this.voltarPara;

		this.seq = null;
		this.voltarPara = null;
		this.campoLaudo = null;
		this.sinonimoCampoLaudo = new AelSinonimoCampoLaudo();
		this.listaSinonimoCampoLaudo = new LinkedList<AelSinonimoCampoLaudo>();
		this.editandoItem = false;

		return retorno;
	}

	/*
	 * Getters e setters
	 */

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	public AelSinonimoCampoLaudo getSinonimoCampoLaudo() {
		return sinonimoCampoLaudo;
	}

	public void setSinonimoCampoLaudo(AelSinonimoCampoLaudo sinonimoCampoLaudo) {
		this.sinonimoCampoLaudo = sinonimoCampoLaudo;
	}

	public List<AelSinonimoCampoLaudo> getListaSinonimoCampoLaudo() {
		return listaSinonimoCampoLaudo;
	}

	public void setListaSinonimoCampoLaudo(List<AelSinonimoCampoLaudo> listaSinonimoCampoLaudo) {
		this.listaSinonimoCampoLaudo = listaSinonimoCampoLaudo;
	}

	public boolean isEditandoItem() {
		return editandoItem;
	}

	public void setEditandoItem(boolean editandoItem) {
		this.editandoItem = editandoItem;
	}

}