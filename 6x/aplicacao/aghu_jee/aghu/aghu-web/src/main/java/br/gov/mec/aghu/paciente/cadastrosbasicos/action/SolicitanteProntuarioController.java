package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de Solicitante
 * de Prontuário.
 * 
 * @author david.laks
 */

public class SolicitanteProntuarioController extends ActionController {

	private static final long serialVersionUID = 4358809632479319093L;
	private static final Log LOG = LogFactory.getLog(SolicitanteProntuarioController.class);
	private static final String REDIRECT_PESQUISAR_SOLICITANTE_PRONTUARIO = "solicitanteProntuarioList";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private SolicitanteProntuarioPaginatorController solicitanteProntuarioPaginatorController;
	/**
	 * Solicitante de Prontuário a ser criada/editada
	 */
	private AipSolicitantesProntuario aipSolicitanteProntuario;


	private AghUnidadesFuncionais unidadesFuncionais;
	
	private AipFinalidadesMovimentacao finalidadesMovimentacao;

	private AghOrigemEventos origemEventos;
	
	private final Integer TAB_0 = 0, TAB_1 = 1, TAB_2 = 2;
	
	private Integer abaSelecionada;

	public void iniciarInclusao() {
		this.aipSolicitanteProntuario = new AipSolicitantesProntuario();
		this.aipSolicitanteProntuario.setIndSituacao(DominioSituacao.A);
		this.unidadesFuncionais = null;
		this.finalidadesMovimentacao = null;
		this.origemEventos = null;
		this.inicio();
	}
	
	public void iniciarEdicao(AipSolicitantesProntuario solicitanteProntuario) {
		
		if (solicitanteProntuario != null) {
			this.aipSolicitanteProntuario = solicitanteProntuario;
			
			this.setFinalidadesMovimentacao(this.aipSolicitanteProntuario
					.getFinalidadesMovimentacao());
			this.setUnidadesFuncionais(this.aipSolicitanteProntuario
					.getUnidadesFuncionais());
			this.setOrigemEventos(this.aipSolicitanteProntuario
					.getOrigemEventos());
		}
		this.inicio();
	}

	public void inicio() {
		if (abaSelecionada == null) {
			abaSelecionada = TAB_0;
		}

		if (this.aipSolicitanteProntuario == null) {
			this.origemEventos = new AghOrigemEventos();
		}
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * solicitante de prontuário
	 */
	public String confirmar() {
		this.solicitanteProntuarioPaginatorController.reiniciarPaginator();

		try {
			// Tarefa 659 - deixar todos textos das entidades em caixa alta via
			// toUpperCase()
			transformarTextosCaixaAlta();

			boolean create = this.aipSolicitanteProntuario.getSeq() == null;

			this.aipSolicitanteProntuario
					.setFinalidadesMovimentacao(this.finalidadesMovimentacao);
			this.aipSolicitanteProntuario
					.setUnidadesFuncionais(this.unidadesFuncionais);
			this.aipSolicitanteProntuario.setOrigemEventos(this.origemEventos);

			this.cadastrosBasicosPacienteFacade
					.persistirSolicitanteProntuario(this.aipSolicitanteProntuario);

			if (create) {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_SOLICITANTE_PRONTUARIO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_SOLICITANTE_PRONTUARIO");
			}
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);

			return null;
		}

		return REDIRECT_PESQUISAR_SOLICITANTE_PRONTUARIO;
	}

	private void transformarTextosCaixaAlta() {
		this.aipSolicitanteProntuario
				.setDescricao(this.aipSolicitanteProntuario.getDescricao() == null ? null
						: this.aipSolicitanteProntuario.getDescricao()
								.toUpperCase());
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Solicitantes de Prontuário
	 */
	public String cancelar() {
		LOG.info("Cancelado");
		this.solicitanteProntuarioPaginatorController.reiniciarPaginator();
		return REDIRECT_PESQUISAR_SOLICITANTE_PRONTUARIO;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricao(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricao(param);
	}

	public boolean isMostrarLinkExcluirUnidadeFuncional() {
		return this.getUnidadesFuncionais().getSeq() != null;
	}

	public List<AghOrigemEventos> pesquisarOrigemEventoPorDescricao(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarOrigemEventoPorCodigoEDescricao((String) param);
	}

	public boolean isMostrarLinkExcluirOrigemEvento() {
		return this.getOrigemEventos().getSeq() != null;
	}

	public List<AipFinalidadesMovimentacao> pesquisarFinalidadeMovimentacaoPorDescricao(
			String param) {
		return this.cadastrosBasicosPacienteFacade
				.pesquisarFinalidadeMovimentacaoPorCodigoEDescricao((String) param);
	}

	public void limparFinalidadeMovimentacao() {
		this.setFinalidadesMovimentacao(null);
	}

	public boolean isMostrarLinkExcluirFinalidadeMovimentacao() {
		if (this.getFinalidadesMovimentacao() == null) {
			return false;
		}
		return this.getFinalidadesMovimentacao().getCodigo() != null;
	}

	
	public void limparAbas() {
		this.unidadesFuncionais = null;
		this.origemEventos = null;
		if (aipSolicitanteProntuario != null) {
			this.aipSolicitanteProntuario.setDescricao(null);
		}
	}

	public void selecionarAbaPreenchida() {
		if (this.unidadesFuncionais != null) {
			abaSelecionada = TAB_0;
		} else if (this.origemEventos != null) {
			abaSelecionada = TAB_1;
		} else if (verificarPreenchimentoAbaDescricao()) {
			abaSelecionada = TAB_2;
		}		
	}

	public Boolean verificarPreenchimentoAbaDescricao() {
		return aipSolicitanteProntuario != null && StringUtils.isNotEmpty(aipSolicitanteProntuario.getDescricao());
	}

	
	// GETTERS e SETTERS

	public AipSolicitantesProntuario getAipSolicitanteProntuario() {
		return aipSolicitanteProntuario;
	}

	public void setAipSolicitanteProntuario(
			AipSolicitantesProntuario aipSolicitanteProntuario) {
		this.aipSolicitanteProntuario = aipSolicitanteProntuario;
	}

	public AghUnidadesFuncionais getUnidadesFuncionais() {
		return unidadesFuncionais;
	}

	public void setUnidadesFuncionais(AghUnidadesFuncionais unidadesFuncionais) {
		this.unidadesFuncionais = unidadesFuncionais;
	}

	public AipFinalidadesMovimentacao getFinalidadesMovimentacao() {
		return finalidadesMovimentacao;
	}

	public void setFinalidadesMovimentacao(
			AipFinalidadesMovimentacao finalidadesMovimentacao) {
		this.finalidadesMovimentacao = finalidadesMovimentacao;
	}

	public AghOrigemEventos getOrigemEventos() {
		return origemEventos;
	}

	public void setOrigemEventos(AghOrigemEventos origemEventos) {
		this.origemEventos = origemEventos;
	}
	
	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}
	
	public void onTabChange() {
	  if(this.unidadesFuncionais != null || this.origemEventos != null || this.verificarPreenchimentoAbaDescricao()){   
		  openDialog("modalConfirmacaoMudancaAbaWG");
	  }
	}


}
