package br.gov.mec.aghu.internacao.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;

import br.gov.mec.aghu.dominio.DominioTipoCreditoRefeitorio;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.model.AinAcompanhantesInternacaoId;
import br.gov.mec.aghu.model.AinCrachaAcompanhantes;
import br.gov.mec.aghu.model.AinCrachaAcompanhantesId;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class AtualizaAcompanhantesInternacaoController extends ActionController {

	private static final long serialVersionUID = -1671923462893504404L;
	/** Internação */
	private Integer intSeq;
	private AinInternacao internacao;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/** AinAcompanhantesInternacao */
	private AinAcompanhantesInternacao acompanhante;
	private List<AinAcompanhantesInternacao> acompanhantes = new ArrayList<AinAcompanhantesInternacao>(0);

	/** AinCrachaAcompanhantes */
	private AinCrachaAcompanhantes crachaAcompanhante;
	private List<AinCrachaAcompanhantes> crachasAcompanhantes = new ArrayList<AinCrachaAcompanhantes>(0);

	private final String PAGE_CADASTRO_INTERNACAO = "cadastroInternacao";
	private final String PAGE_ATUALIZAR_ACOMPANHANTE = "atualizarAcompanhantes";
	private final String PAGE_ATUALIZAR_ACOMPANHANTE_CRACHA = "atualizarAcompanhanteCracha";

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	private Boolean isEdicaoCracha = false;
	private Long lNumeroCrachaSugerido = null;
	private boolean bSemCredito = false;

	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void inicio() {
		this.isEdicaoCracha = false;
		// A princípio entra no IF, pois esse Controller sempre vem da tela
		// de internação.
		if (this.intSeq != null) {
			this.internacao = pesquisaInternacaoFacade.obterInternacaoComLefts(intSeq);
			this.acompanhantes = pesquisaInternacaoFacade.obterAcompanhantesInternacao(intSeq);
		}
	
	}

	/**
	 * #########################################################################
	 * ##
	 */
	/**
	 * Este método é executado quando a combo de tipo crédito refeitório realiza
	 * a ação de onchange na tela
	 * 
	 */
	public void changeComboTipoCreditoRefeitorio(ValueChangeEvent event) {
		DominioTipoCreditoRefeitorio auxSituacao = null;
		if (event != null && event.getNewValue() != null) {
			auxSituacao = (DominioTipoCreditoRefeitorio) event.getNewValue();
		}

		bSemCredito = DominioTipoCreditoRefeitorio.VALOR_0.equals(auxSituacao);

		if (bSemCredito) {
			try {
				lNumeroCrachaSugerido = internacaoFacade.sugereNumeroDoCracha(obterLoginUsuarioLogado());
			} catch (Exception e) {
				lNumeroCrachaSugerido = null;
				bSemCredito = false;
			}
		} else {
			lNumeroCrachaSugerido = null;
		}
		this.crachaAcompanhante.setNumCra(lNumeroCrachaSugerido);
		this.crachaAcompanhante.setPermissaoAcesso(null);
	}

	/**
	 * #########################################################################
	 * ##
	 */
	/**
	 * Editar o Crachá do Acompanhante.
	 * 
	 * @param crachaAcompanhante
	 */
	public void editarCrachaAcompanhante(final AinCrachaAcompanhantes crachaAcompanhante) {

		this.crachaAcompanhante = crachaAcompanhante;

		this.isEdicaoCracha = true;
	}

	/**
	 * Remover crachaAcompanhante.
	 * 
	 * @param crachaAcompanhante
	 */
	public void removerCrachaAcompanhante(final Integer index) {
		if (index < crachasAcompanhantes.size() && crachasAcompanhantes.get(index) != null) {
			this.crachasAcompanhantes.remove(index.intValue());
			
			this.crachaAcompanhante = new AinCrachaAcompanhantes();
			this.crachaAcompanhante.setDthrInicio(Calendar.getInstance().getTime());
			
			this.isEdicaoCracha = false;
		}
	}

	/**
	 * Editar acompanhante.
	 * 
	 * @param acompanhante
	 */
	public String editarAcompanhante(final AinAcompanhantesInternacaoId id) {

		try {
			this.acompanhante = this.pesquisaInternacaoFacade.obterAinAcompanhantesInternacao(id);

			// Obtem listas associadas;
			this.crachasAcompanhantes = this.internacaoFacade.obterCrachasAcompanhantes(this.acompanhante);

			this.crachaAcompanhante = new AinCrachaAcompanhantes();
			this.crachaAcompanhante.setDthrInicio(Calendar.getInstance().getTime());
			// volta para atualizarAcompanhanteCracha.xhtml
			return PAGE_ATUALIZAR_ACOMPANHANTE_CRACHA;

		} catch (IllegalArgumentException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * Remover acompanhante.
	 * 
	 * @param acompanhante
	 */
	public void removerAcompanhante(final AinAcompanhantesInternacao acompanhante) {
		if (this.acompanhantes.contains(acompanhante)) {
			this.acompanhantes.remove(acompanhante);
		}
	}

	/**
	 * Adiciona acompanhante.
	 * 
	 * @param plano
	 * @throws ApplicationBusinessException
	 */
	public String associarAcompanhante() throws ApplicationBusinessException {

		// Variável que indica se é criação ou edição.
		boolean create = false;

		try {
			// Validação de campos.
			this.internacaoFacade.validaCamposAtualizaAcompanhantes(this.acompanhante.getDataHoraInicio(),
					this.acompanhante.getDataHoraFim(), this.acompanhante.getNome());
		} catch (final ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			return null;
		}

		// Caso seja criação de novo acompanhante.
		if (this.acompanhante == null || this.acompanhante.getId() == null) {

			create = true;

			// Criando objeto Acompanhante.
			final AinAcompanhantesInternacaoId id = new AinAcompanhantesInternacaoId();
			if (this.internacao != null) {
				id.setIntSeq(internacao.getSeq());
			}
			this.acompanhante.setId(id);

			this.acompanhante.setCriadoEm(new Date());

			this.acompanhante.setServidor(servidorLogadoFacade.obterServidorLogado());
		}

		// Atualizar acompanhantes.
		try {
			this.internacaoFacade.persistirCrachaAcompanhantes(crachasAcompanhantes, acompanhante, acompanhantes, internacao);
			for (AinCrachaAcompanhantes crachaAcompanhante : crachasAcompanhantes) {
				// se crachá já foi incluído com sucesso, então retorna para o
				// grid, na condição de não poder mais ter campos editados.
				// Campos:Data Início, Tipo Crédito Refeitório, Número crachá e
				// permissão acesso
				crachaAcompanhante.setbEditado(false);
			}
		} catch (final ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);

			if (create) {
				this.acompanhante.setId(null);
			}

			// volta para atualizarAcompanhanteCracha.xhtml
			return PAGE_ATUALIZAR_ACOMPANHANTE_CRACHA;
		}

		if (!acompanhantes.contains(acompanhante)) {
			acompanhantes.add(acompanhante);
		}

		if (create) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_ACOMPANHANTE", this.acompanhante.getNome());
		} else {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_ACOMPANHANTE", this.acompanhante.getNome());
		}

		return PAGE_ATUALIZAR_ACOMPANHANTE;
	}

	/**
	 * Cria associação entre Acompanhante e Crachá acompanhante.
	 */
	public void associarCrachaAcompanhante() {
		// TODO: Identificar validações ao salvar crachá. No AGH não está
		// salvando.

		if (this.crachaAcompanhante.getDthrInicio() == null) {
			apresentarMsgNegocio(Severity.WARN, "DATA_INICIO_NECESSARIA");

			return;
		}

		if (this.crachaAcompanhante.getNumCra() == null) {
			apresentarMsgNegocio(Severity.WARN, "NUM_CRACHA_NECESSARIO");

			return;
		}

		// Caso haja data de encerramento deve existir o motivo do encerramento
		// ou vice-versa.
		if ((this.crachaAcompanhante.getDthrEncerramento() == null && this.crachaAcompanhante.getMotivoEncerramento() != null)
				|| (this.crachaAcompanhante.getDthrEncerramento() != null && this.crachaAcompanhante.getMotivoEncerramento() == null)) {

			apresentarMsgNegocio(Severity.WARN, "MAM_04990");
			return;
		}

		// Inserção
		if (!(this.crachaAcompanhante.getId() != null) || !(this.crachaAcompanhante.getId().getSeqp() != null)) {
			this.crachaAcompanhante.setCriadoEm(Calendar.getInstance().getTime());
			this.crachaAcompanhante.setServidor(servidorLogadoFacade.obterServidorLogado());
			final AinCrachaAcompanhantesId id = new AinCrachaAcompanhantesId();
			this.crachaAcompanhante.setId(id);
			// se crachá está sendo incluído, retorna para o grid, na condição
			// de poder ser editado.
			// deixará de ser editado, após ser salvo no banco (campos que não
			// poderão mais ser editados após
			// a gravação: Data Início, Tipo Crédito Refeitório, Número crachá e
			// permissão acesso.
			this.crachaAcompanhante.setbEditado(true);
		}

		// Associa à lista.
		if (!isEdicaoCracha) {
			this.crachasAcompanhantes.add(this.crachaAcompanhante);
		}

		// Inicia item.
		this.crachaAcompanhante = new AinCrachaAcompanhantes();
		this.crachaAcompanhante.setDthrInicio(Calendar.getInstance().getTime());

		this.isEdicaoCracha = false;
	}

	public void associarAcompanhanteComCracha() throws ApplicationBusinessException {
		associarCrachaAcompanhante();
		associarAcompanhante();
	}

	/**
	 * Metodo que salvar Acompanhantes.
	 */
	public String confirmar() {

		try {
			// Atualizar acompanhantes.
			this.internacaoFacade.persistirAcompanhantes(this.acompanhantes, this.internacao);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZACAO_ACOMPANHANTES", this.internacao.getPaciente().getNome());

			// Limpa variáveis.
			this.acompanhante = new AinAcompanhantesInternacao();
			this.acompanhantes = new ArrayList<AinAcompanhantesInternacao>(0);

		} catch (final ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
			return null;
		}
		this.isEdicaoCracha = false;
		return PAGE_CADASTRO_INTERNACAO;
	}

	/**
	 * Prepara a criação de novo Acompanhante.
	 */
	public String prepararAdicaoAcompanhante() {

		this.acompanhante = new AinAcompanhantesInternacao();
		this.acompanhante.setDataHoraInicio(Calendar.getInstance().getTime());

		this.crachaAcompanhante = new AinCrachaAcompanhantes();
		this.crachaAcompanhante.setDthrInicio(Calendar.getInstance().getTime());

		this.crachasAcompanhantes = new ArrayList<AinCrachaAcompanhantes>(0);
		// volta para atualizarAcompanhanteCracha.xhtml
		return PAGE_ATUALIZAR_ACOMPANHANTE_CRACHA;
	}

	/**
	 * Método que realiza a ação do botão cancelar.
	 */
	public String cancelar() {
		return PAGE_CADASTRO_INTERNACAO;
	}

	public String cancelarCracha() {
		return PAGE_ATUALIZAR_ACOMPANHANTE;
	}

	// ### GETs e SETs ###
	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(final AinInternacao internacao) {
		this.internacao = internacao;
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public List<AinAcompanhantesInternacao> getAcompanhantes() {
		return acompanhantes;
	}

	public void setAcompanhantes(final List<AinAcompanhantesInternacao> acompanhantes) {
		this.acompanhantes = acompanhantes;
	}

	public AinAcompanhantesInternacao getAcompanhante() {
		return acompanhante;
	}

	public void setAcompanhante(final AinAcompanhantesInternacao acompanhante) {
		this.acompanhante = acompanhante;
	}

	public AinCrachaAcompanhantes getCrachaAcompanhante() {
		return crachaAcompanhante;
	}

	public void setCrachaAcompanhante(final AinCrachaAcompanhantes crachaAcompanhante) {
		this.crachaAcompanhante = crachaAcompanhante;
	}

	public List<AinCrachaAcompanhantes> getCrachasAcompanhantes() {
		return crachasAcompanhantes;
	}

	public void setCrachasAcompanhantes(final List<AinCrachaAcompanhantes> crachasAcompanhantes) {
		this.crachasAcompanhantes = crachasAcompanhantes;
	}

	public Boolean getIsEdicaoCracha() {
		return isEdicaoCracha;
	}

	public void setIsEdicaoCracha(final Boolean isEdicaoCracha) {
		this.isEdicaoCracha = isEdicaoCracha;
	}

	public boolean isbSemCredito() {
		return bSemCredito;
	}

	public boolean exibirPanel() {
		return bSemCredito;
	}

	public void setbSemCredito(boolean bSemCredito) {
		this.bSemCredito = bSemCredito;
	}

	public Long getlNumeroCrachaSugerido() {
		return lNumeroCrachaSugerido;
	}

	public void setlNumeroCrachaSugerido(Long lNumeroCrachaSugerido) {
		this.lNumeroCrachaSugerido = lNumeroCrachaSugerido;
	}
}
