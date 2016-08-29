package br.gov.mec.aghu.internacao.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.business.vo.ProfissionaisEscalaIntenacaoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class EscalaProfissionaisInternacaoController  extends ActionController {
	
	private static final long serialVersionUID = -8112144605273380305L;
	private static final String REDIRECT_PESQUISAR_ESCALA_PROFISSIONAIS = "escalaInternacao";

	@EJB
	IInternacaoFacade internacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private PesquisaEscalaProfissionaisInternacaoController pesquisaEscalaProfissionaisInternacaoController;
	
	@Inject
	private EscalaProfissionaisInternacaoPaginatorController escalaProfissionaisInternacaoPaginatorController;

	private Integer matricula;
	private Integer vinculo;
	private String conselhoProfissional;
	private String nomeServidor;
	private String siglaEspecialidade;
	private Integer codigoConvenio;
	private String descricaoConvenio;
	
	private Boolean pesquisaFeita;
	private boolean inicializado;
	private String retorno;

	private enum EscalaProfissionaisInternacaoPaginatorControllerExceptionCode
			implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_MEDICINA_NAO_ENCONTRADO, MENSAGEM_PARAMETRO_ODONTO_NAO_ENCONTRADO;
	}
	
	@PostConstruct
	public void iniciar() {
		this.begin(this.conversation);
		
		if (!inicializado) {
			try {
				// Este tratamento esta sendo feito previamente na controller
				// porque
				// a mensagem não estava sendo mostrada corretamente na
				// paginator
				// controller, no momento de executar a query
				final AghParametros qualificacaoMedicina = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_MEDICINA);
				if (qualificacaoMedicina == null || qualificacaoMedicina.getVlrNumerico() == null) {
					throw new ApplicationBusinessException(
							EscalaProfissionaisInternacaoPaginatorControllerExceptionCode.MENSAGEM_PARAMETRO_MEDICINA_NAO_ENCONTRADO);
				}

				final AghParametros qualificacaoOdonto = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_ODONTO);
				if (qualificacaoOdonto == null || qualificacaoOdonto.getVlrNumerico() == null) {
					throw new ApplicationBusinessException(
							EscalaProfissionaisInternacaoPaginatorControllerExceptionCode.MENSAGEM_PARAMETRO_ODONTO_NAO_ENCONTRADO);
				}
				inicializado = true;

			} catch (final ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
			}
		}
		// caso uma pesquisa já estiver feita, refaz a mesma quando o foco
		// voltar para esta página
		if ((pesquisaFeita != null && pesquisaFeita && !inicializado) || "true".equals(getRetorno()) ) {
			pesquisar();
			this.setRetorno(null);
		}
	}

	public void pesquisar() {
		escalaProfissionaisInternacaoPaginatorController.reiniciarPaginator();
		escalaProfissionaisInternacaoPaginatorController.setMaxResults(10);
		escalaProfissionaisInternacaoPaginatorController.setAtivo(true);
		// limpa filtros
		escalaProfissionaisInternacaoPaginatorController.setVinculo(null);
		escalaProfissionaisInternacaoPaginatorController.setCodigoConvenio(null);

		escalaProfissionaisInternacaoPaginatorController.setMatricula(matricula);
		escalaProfissionaisInternacaoPaginatorController.setConselhoProfissional(conselhoProfissional);
		escalaProfissionaisInternacaoPaginatorController.setNomeServidor(nomeServidor);
		if (siglaEspecialidade != null){
			escalaProfissionaisInternacaoPaginatorController.setSiglaEspecialidade(siglaEspecialidade.toUpperCase());
		}
		escalaProfissionaisInternacaoPaginatorController.setDescricaoConvenio(descricaoConvenio);
		if (vinculo != null) {
			escalaProfissionaisInternacaoPaginatorController.setVinculo(vinculo.shortValue());
		}
		if (codigoConvenio != null) {
			escalaProfissionaisInternacaoPaginatorController.setCodigoConvenio(codigoConvenio.shortValue());
		}
		
		this.pesquisaFeita = true;
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		this.vinculo = null;
		this.matricula = null;
		this.conselhoProfissional = null;
		this.nomeServidor = null;
		this.siglaEspecialidade = null;
		this.codigoConvenio = null;
		this.nomeServidor = null;
		this.descricaoConvenio = null;
		this.escalaProfissionaisInternacaoPaginatorController.setAtivo(false);
		this.pesquisaFeita = false;
		this.setRetorno(null);
	}
	
	public String editar(){
		ProfissionaisEscalaIntenacaoVO prof = escalaProfissionaisInternacaoPaginatorController.getObjetoSelecionado();
		this.pesquisaEscalaProfissionaisInternacaoController.setMatriculaServidor(prof.getMatriculaServidor());
		this.pesquisaEscalaProfissionaisInternacaoController.setVinculoServidor(prof.getVinculoServidor().intValue());
		this.pesquisaEscalaProfissionaisInternacaoController.setNomeServidor(prof.getNomeServidor());
		this.pesquisaEscalaProfissionaisInternacaoController.setSeqEspecialidade(prof.getSeqEspecialidade().intValue());
		this.pesquisaEscalaProfissionaisInternacaoController.setSiglaEspecialidade(prof.getSiglaEspecialidade());
		this.pesquisaEscalaProfissionaisInternacaoController.setCodigoConvenio(prof.getCodigoConvenio().intValue());
		this.pesquisaEscalaProfissionaisInternacaoController.setDescricaoConvenio(prof.getDescricaoConvenio());
		this.pesquisaEscalaProfissionaisInternacaoController.setRegistroServidor(prof.getNumeroRegistroConselho());
		this.pesquisaEscalaProfissionaisInternacaoController.setCameFrom("pesquisaEscalaProfissionaisInternacaoController");
		this.pesquisaEscalaProfissionaisInternacaoController.setInicializado(false);
		this.pesquisaEscalaProfissionaisInternacaoController.iniciar();
		return REDIRECT_PESQUISAR_ESCALA_PROFISSIONAIS;
	}

	// GETTERS AND SETTERS
	public EscalaProfissionaisInternacaoPaginatorController getPaginator() {
		return escalaProfissionaisInternacaoPaginatorController;
	}

	public void setPaginator(
			final EscalaProfissionaisInternacaoPaginatorController paginator) {
		this.escalaProfissionaisInternacaoPaginatorController = paginator;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(final Integer matricula) {
		this.matricula = matricula;
	}

	public Integer getVinculo() {
		return vinculo;
	}

	public void setVinculo(final Integer vinculo) {
		this.vinculo = vinculo;
	}

	public String getConselhoProfissional() {
		return conselhoProfissional;
	}

	public void setConselhoProfissional(final String conselhoProfissional) {
		this.conselhoProfissional = conselhoProfissional;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(final String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(final String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public Integer getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(final Integer codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public String getDescricaoConvenio() {
		return descricaoConvenio;
	}

	public void setDescricaoConvenio(final String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}

	public Boolean getPesquisaFeita() {
		return pesquisaFeita;
	}

	public void setPesquisaFeita(final Boolean pesquisaFeita) {
		this.pesquisaFeita = pesquisaFeita;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(final boolean inicializado) {
		this.inicializado = inicializado;
	}

	public void setRetorno(final String retorno) {
		this.retorno = retorno;
	}

	public String getRetorno() {
		return retorno;
	}
	
}
