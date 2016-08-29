package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTodosUltimo;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * Classe responsável por controlar as ações do listagem de Solicitantes de
 * Prontuário.
 */

public class SolicitanteProntuarioPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5857838098020084240L;
	private static final Log LOG = LogFactory.getLog(SolicitanteProntuarioPaginatorController.class);
	private static final String REDIRECT_MANTER_SOLICITANTE_PRONTUARIO = "solicitanteProntuarioCRUD";
	
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Inject
	private SolicitanteProntuarioController solicitanteProntuarioController;
	
	@Inject @Paginator
	private DynamicDataModel<AipSolicitantesProntuario> dataModel;
	
	private AipSolicitantesProntuario solicitanteProntuario;
	
	private final Integer TAB_0 = 0, TAB_1 = 1, TAB_2 = 2;
	
	private Short seqEdicao;

	private Integer abaEdicao;

	private Integer abaSelecionada;

	
	/**
	 * Atributo referente ao campo de filtro de Unidade Funcional do Solicitante
	 * de Prontuário na tela de pesquisa.
	 */
	private AghUnidadesFuncionais unidadeFuncional;

	/**
	 * Atributo referente ao campo de filtro de Origem do Evento do Solicitante
	 * de Prontuário na tela de pesquisa.
	 */
	private AghOrigemEventos origemEvento;

	/**
	 * Atributo referente ao campo de filtro de Finalidade de Movimentação do
	 * Solicitante de Prontuário na tela de pesquisa.
	 */
	private AipFinalidadesMovimentacao finalidadeMovimentacao;
	/**
	 * Atributo referente ao campo de filtro de Sequencial (id) do Solicitante
	 * de Prontuário na tela de pesquisa.
	 */
	private Integer seqSolicitanteProntuario;

	/**
	 * Atributo referente ao campo de filtro de Situação do Solicitante de
	 * Prontuário na tela de pesquisa.
	 */
	private DominioSituacao indSituacaoSolicitanteProntuario;

	/**
	 * Atributo referente ao campo de filtro de Descrição do Solicitante de
	 * Prontuário na tela de pesquisa.
	 */
	private String descricaoSolicitanteProntuario;

	/**
	 * Atributo referente ao campo de filtro de Exige Responsável do Solicitante
	 * de Prontuário na tela de pesquisa.
	 */
	private DominioSimNao exigeResponsavelSolicitanteProntuario;

	/**
	 * Atributo referente ao campo de filtro de Mensagem SAMIS do Solicitante de
	 * Prontuário na tela de pesquisa.
	 */
	private DominioSimNao mensagemSamisSolicitanteProntuario;

	/**
	 * Atributo referente ao campo de filtro de Serapação Prévia do Solicitante
	 * de Prontuário na tela de pesquisa.
	 */
	private DominioSimNao separacaoPreviaSolicitanteProntuario;

	/**
	 * Atributo referente ao campo de filtro de Retorno do Solicitante de
	 * Prontuário na tela de pesquisa.
	 */
	private DominioSimNao retornoSolicitanteProntuario;

	/**
	 * Atributo referente ao campo de filtro de Volumes Manuseados do
	 * Solicitante de Prontuário na tela de pesquisa.
	 */
	private DominioTodosUltimo volumesManuseadosSolicitanteProntuario;

	@PostConstruct
	public void init(){
		LOG.info("Iniciando conversação");
		this.begin(this.conversation);
	}
	
	public void pesquisar() {
		this.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.seqSolicitanteProntuario = null;
		this.indSituacaoSolicitanteProntuario = null;
		this.descricaoSolicitanteProntuario = null;
		this.exigeResponsavelSolicitanteProntuario = null;
		this.mensagemSamisSolicitanteProntuario = null;
		this.separacaoPreviaSolicitanteProntuario = null;
		this.retornoSolicitanteProntuario = null;
		this.volumesManuseadosSolicitanteProntuario = null;
		this.unidadeFuncional = null;
		this.origemEvento = null;
		this.finalidadeMovimentacao = null;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String iniciarInclusao(){
		solicitanteProntuarioController.iniciarInclusao();
		return REDIRECT_MANTER_SOLICITANTE_PRONTUARIO;
	}

	public String editar(){
		solicitanteProntuarioController.iniciarEdicao(this.solicitanteProntuario);
		return REDIRECT_MANTER_SOLICITANTE_PRONTUARIO;
	}

	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosPacienteFacade.obterSolicitanteProntuarioCount(
				seqSolicitanteProntuario, indSituacaoSolicitanteProntuario,
				unidadeFuncional, origemEvento, finalidadeMovimentacao,
				descricaoSolicitanteProntuario,
				exigeResponsavelSolicitanteProntuario != null ? exigeResponsavelSolicitanteProntuario
						.isSim() : null,
				mensagemSamisSolicitanteProntuario,
				separacaoPreviaSolicitanteProntuario,
				retornoSolicitanteProntuario,
				volumesManuseadosSolicitanteProntuario);
	}

	@Override
	public List<AipSolicitantesProntuario> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosBasicosPacienteFacade.pesquisarSolicitanteProntuario(
				firstResult, maxResult, AipSolicitantesProntuario.Fields.SEQ.toString(), true,
				seqSolicitanteProntuario, indSituacaoSolicitanteProntuario,
				unidadeFuncional, origemEvento, finalidadeMovimentacao,
				descricaoSolicitanteProntuario,
				exigeResponsavelSolicitanteProntuario != null ? exigeResponsavelSolicitanteProntuario
						.isSim() : null,
				mensagemSamisSolicitanteProntuario,
				separacaoPreviaSolicitanteProntuario,
				retornoSolicitanteProntuario,
				volumesManuseadosSolicitanteProntuario);
	}
	
	public String editar(AipSolicitantesProntuario aipSolicitanteProntuario) {
		if (aipSolicitanteProntuario != null) {
			if (aipSolicitanteProntuario.getUnidadesFuncionais() != null) {
				abaEdicao = TAB_0;
			} else if (aipSolicitanteProntuario.getOrigemEventos() != null) {
				abaEdicao = TAB_1;
			} else if (aipSolicitanteProntuario.getDescricao() != null) { 
				abaEdicao = TAB_2;	
			}
			this.seqEdicao = aipSolicitanteProntuario.getSeq();
			return "edicao";
		}
		return "";
	}

	public Boolean verificarPreenchimentoAbaDescricao() {
		return StringUtils.isNotEmpty(descricaoSolicitanteProntuario);
	}

	public void limparAbas() {
		this.unidadeFuncional = null;
		this.origemEvento = null;
		this.descricaoSolicitanteProntuario = null;
	}

	public void selecionarAbaPreenchida() {
		if (this.unidadeFuncional != null) {
			abaSelecionada = TAB_0;
		} else if (this.origemEvento != null) {
			abaSelecionada = TAB_1;
		} else if (verificarPreenchimentoAbaDescricao()) {
			abaSelecionada = TAB_2;
		}		
	}	

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricao(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricao(param);
	}

	public void limparUnidadeFuncional() {
		this.unidadeFuncional = null;
	}

	public boolean isMostrarLinkExcluirUnidadeFuncional() {
		return this.getUnidadeFuncional().getSeq() != null;
	}

	public List<AghOrigemEventos> pesquisarOrigemEventoPorDescricao(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarOrigemEventoPorCodigoEDescricao((String) param);
	}

	public void limparOrigemEvento() {
		this.origemEvento = null;
	}

	public boolean isMostrarLinkExcluirOrigemEvento() {
		return this.getOrigemEvento().getSeq() != null;
	}

	public List<AipFinalidadesMovimentacao> pesquisarFinalidadeMovimentacaoPorDescricao(
			String param) {
		return this.cadastrosBasicosPacienteFacade
				.pesquisarFinalidadeMovimentacaoPorCodigoEDescricao((String) param);
	}

	public void limparFinalidadeMovimentacao() {
		this.finalidadeMovimentacao = null;
	}

	public boolean isMostrarLinkExcluirFinalidadeMovimentacao() {
		return this.getFinalidadeMovimentacao().getCodigo() != null;
	}

	// GETTERS e SETTERS

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AipFinalidadesMovimentacao getFinalidadeMovimentacao() {
		return finalidadeMovimentacao;
	}

	public void setFinalidadeMovimentacao(
			AipFinalidadesMovimentacao finalidadeMovimentacao) {
		this.finalidadeMovimentacao = finalidadeMovimentacao;
	}

	public AghOrigemEventos getOrigemEvento() {
		return origemEvento;
	}

	public void setOrigemEvento(AghOrigemEventos origemEvento) {
		this.origemEvento = origemEvento;
	}

	public Integer getSeqSolicitanteProntuario() {
		return seqSolicitanteProntuario;
	}

	public void setSeqSolicitanteProntuario(Integer seqSolicitanteProntuario) {
		this.seqSolicitanteProntuario = seqSolicitanteProntuario;
	}

	public DominioSituacao getIndSituacaoSolicitanteProntuario() {
		return indSituacaoSolicitanteProntuario;
	}

	public void setIndSituacaoSolicitanteProntuario(
			DominioSituacao indSituacaoSolicitanteProntuario) {
		this.indSituacaoSolicitanteProntuario = indSituacaoSolicitanteProntuario;
	}

	public String getDescricaoSolicitanteProntuario() {
		return descricaoSolicitanteProntuario;
	}

	public void setDescricaoSolicitanteProntuario(
			String descricaoSolicitanteProntuario) {
		this.descricaoSolicitanteProntuario = descricaoSolicitanteProntuario;
	}

	public DominioSimNao getExigeResponsavelSolicitanteProntuario() {
		return exigeResponsavelSolicitanteProntuario;
	}

	public void setExigeResponsavelSolicitanteProntuario(
			DominioSimNao exigeResponsavelSolicitanteProntuario) {
		this.exigeResponsavelSolicitanteProntuario = exigeResponsavelSolicitanteProntuario;
	}

	public DominioSimNao getMensagemSamisSolicitanteProntuario() {
		return mensagemSamisSolicitanteProntuario;
	}

	public void setMensagemSamisSolicitanteProntuario(
			DominioSimNao mensagemSamisSolicitanteProntuario) {
		this.mensagemSamisSolicitanteProntuario = mensagemSamisSolicitanteProntuario;
	}

	public DominioSimNao getSeparacaoPreviaSolicitanteProntuario() {
		return separacaoPreviaSolicitanteProntuario;
	}

	public void setSeparacaoPreviaSolicitanteProntuario(
			DominioSimNao separacaoPreviaSolicitanteProntuario) {
		this.separacaoPreviaSolicitanteProntuario = separacaoPreviaSolicitanteProntuario;
	}

	public DominioSimNao getRetornoSolicitanteProntuario() {
		return retornoSolicitanteProntuario;
	}

	public void setRetornoSolicitanteProntuario(
			DominioSimNao retornoSolicitanteProntuario) {
		this.retornoSolicitanteProntuario = retornoSolicitanteProntuario;
	}

	public DominioTodosUltimo getVolumesManuseadosSolicitanteProntuario() {
		return volumesManuseadosSolicitanteProntuario;
	}

	public void setVolumesManuseadosSolicitanteProntuario(
			DominioTodosUltimo volumesManuseadosSolicitanteProntuario) {
		this.volumesManuseadosSolicitanteProntuario = volumesManuseadosSolicitanteProntuario;
	}

	public DynamicDataModel<AipSolicitantesProntuario> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipSolicitantesProntuario> dataModel) {
		this.dataModel = dataModel;
	}

	public AipSolicitantesProntuario getSolicitanteProntuario() {
		return solicitanteProntuario;
	}

	public void setSolicitanteProntuario(
			AipSolicitantesProntuario solicitanteProntuario) {
		this.solicitanteProntuario = solicitanteProntuario;
	}
	
	public Short getSeqEdicao() {
		return seqEdicao;
	}

	public void setSeqEdicao(Short seqEdicao) {
		this.seqEdicao = seqEdicao;
	}

	public Integer getAbaEdicao() {
		return abaEdicao;
	}

	public void setAbaEdicao(Integer abaEdicao) {
		this.abaEdicao = abaEdicao;
	}
	
	public void onTabChange() {
	  if(this.unidadeFuncional != null || this.origemEvento != null || this.verificarPreenchimentoAbaDescricao()){   
		  openDialog("modalConfirmacaoMudancaAbaWG");
	  }
	}
	  
	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}
}
