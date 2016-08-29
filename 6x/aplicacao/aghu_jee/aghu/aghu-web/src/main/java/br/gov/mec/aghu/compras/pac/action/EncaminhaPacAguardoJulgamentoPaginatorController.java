package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoCriteriaVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoVO;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsável pelo encaminhamento de PAC's que aguardam julgamento.
 * 
 * @author mlcruz
 */
public class EncaminhaPacAguardoJulgamentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -729445225588120797L;

	/** Tamanho máximo da descrição do PAC a ser truncada. */
	private static final int PAC_DESCRICAO_MAXLENGTH = 50;

	private static final String PAGE_REGISTRA_JULGAMENTO_PAC_LOTE = "registraJulgamentoPacLote";
	private static final String PAGE_REGISTRA_JULGAMENTO_PAC = "registraJulgamentoPac";
	private static final String PAGE_RELATORIO_QUADRO_APROVACAO_COMPRAS_LICITACAO = "relatorioQuadroAprovacaoComprasLicitacao";
	
	private static final String VER_QUADRO_JULGAMENTO = "compras-relatorioQuadroJulgamentoPropostas";
	private static final String VER_QUADRO_PROVISORIO = "compras-relatorioQuadroPropostasProvisorio";

	@Inject @Paginator
	private DynamicDataModel<PacParaJulgamentoVO> dataModel;

	// Dependências

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IPacFacade pacFacade;

	/** Critério de Consulta */
	private PacParaJulgamentoCriteriaVO criteria = new PacParaJulgamentoCriteriaVO();

	/** PAC's Selecionados */
	private Map<Integer, Boolean> pacsDisponiveis = new HashMap<Integer, Boolean>();

	/** PAC's Ejetados */
	private Set<Integer> pacs;

	/** Imprimir assinaturas */
	private Boolean assinaturas;

	/** PAC a ser julgado. */
	private PacParaJulgamentoVO pacJulgado;
	
	@Inject
	private RelatorioQuadroJulgamentoPropostaController relatorioQuadroJulgamentoPropostaController;
	
	@Inject
	private RelatorioQuadroPropostasProvisorioController relatorioQuadroPropostasProvisorioController;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private Integer nroPac;
	
	private String voltarParaUrl;
	
	public void iniciar(){
		
		pacsDisponiveis = new HashMap<Integer, Boolean>();
		
		if (nroPac != null){
			criteria.setNroPac(nroPac);
			this.pesquisar();
		}
	}
	/**
	 * Consulta PAC's.
	 */
	public void pesquisar() {
		if (criteria.getNroPac() != null) {
			this.limparFiltros();
		} else {
			criteria.setDescricao(StringUtils.trimToNull(criteria.getDescricao()));
		}
		dataModel.reiniciarPaginator();
	}

	public void limparFiltros() {
		criteria.setDescricao(null);
		criteria.setModalidade(null);
		criteria.setSituacao(null);
		criteria.setPacPossuiProposta(Boolean.FALSE);
	}

	/**
	 * Limpa filtro de consulta.
	 */
	public void limpar() {
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		criteria = new PacParaJulgamentoCriteriaVO();
		pacsDisponiveis = new HashMap<Integer, Boolean>();
	}

	public String registraJulgamentoPacLote() {
		return PAGE_REGISTRA_JULGAMENTO_PAC_LOTE;
	}

	public String registraJulgamentoPac() {
		return PAGE_REGISTRA_JULGAMENTO_PAC;
	}

	/**
	 * Obtem modalidades.
	 * 
	 * @param filter
	 *            Filtro
	 * @return Modalidades
	 */
	public List<ScoModalidadeLicitacao> pesquisarModalidades(String filter) {
		return comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(filter);
	}

	/**
	 * Obtem situações.
	 * 
	 * @return Situações
	 */
	public DominioSituacaoLicitacao[] obterSituacoes() {
		return new DominioSituacaoLicitacao[] { DominioSituacaoLicitacao.AF, DominioSituacaoLicitacao.GR,
				DominioSituacaoLicitacao.JU, DominioSituacaoLicitacao.PT };
	}

	/**
	 * Obtem descrição truncada do PAC.
	 * 
	 * @param pac
	 *            PAC
	 * @param abbreviate
	 *            Abrevia descrição.
	 * @return Descrição Truncada
	 */
	public String getDescricao(PacParaJulgamentoVO pac, boolean abbreviate) {
		String descricao = String.format("%d - %s", pac.getNumero(), pac.getDescricao());

		return abbreviate ? StringUtils.abbreviate(descricao, PAC_DESCRICAO_MAXLENGTH) : descricao;
	}

	/**
	 * Encaminha PAC's selecionados ao comprador.
	 */
	public void encaminharComprador() {
		try {
			pacFacade.encaminharComprador(getPacsSelecionados(), false);

			apresentarMsgNegocio(Severity.INFO, "LICITACAO_ALTERADA_COM_SUCESSO");

			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Encaminha PAC's selecionados para parecer técnico.
	 */
	public void encaminharParecerTecnico() {
		for (Integer pacId : getPacsSelecionados()) {
			ScoLicitacao pac = pacFacade.obterLicitacao(pacId);
			pacFacade.encaminharParecerTecnico(pac);
		}

		apresentarMsgNegocio(Severity.INFO, "LICITACAO_ALTERADA_COM_SUCESSO");

		dataModel.reiniciarPaginator();
	}

	/**
	 * Visualiza relatório Quadro de Julgamento.
	 * 
	 * @return Redirect
	 * @throws IOException 
	 * @throws SystemException 
	 * @throws JRException 
	 * @throws BaseException 
	 */
	public String verQuadroJulgamento() throws BaseException, JRException, SystemException, IOException {
		pacs = getPacsSelecionados();
		this.relatorioQuadroJulgamentoPropostaController.setPacs(pacs);
		this.relatorioQuadroJulgamentoPropostaController.print();
		return VER_QUADRO_JULGAMENTO;
	}

	/**
	 * Visualiza relatório Quadro de Provisório.
	 * 
	 * @return Redirect
	 * @throws IOException 
	 * @throws SystemException 
	 * @throws JRException 
	 * @throws BaseException 
	 */
	public String verQuadroProvisorio() throws BaseException, JRException, SystemException, IOException {
		pacs = getPacsSelecionados();
		this.relatorioQuadroPropostasProvisorioController.setPacs(pacs);
		this.relatorioQuadroPropostasProvisorioController.print();
		return VER_QUADRO_PROVISORIO;
	}

	/**
	 * Imprime quadro de aprovação.
	 * 
	 * @param assinaturas
	 *            Flag para impressão de assinaturas.
	 * @return Redirect
	 */
	public String imprimirQuadroAprovacao(Boolean assinaturas) {
		pacs = getPacsSelecionados();
		this.assinaturas = assinaturas;
		return PAGE_RELATORIO_QUADRO_APROVACAO_COMPRAS_LICITACAO;
	}

	@Override
	public Long recuperarCount() {
		return pacFacade.contarPacsParaJulgamento(criteria);
	}

	@Override
	public List<PacParaJulgamentoVO> recuperarListaPaginada(Integer first, Integer max, String order, boolean asc) {
		return pacFacade.pesquisarPacsParaJulgamento(criteria, first, max, order, asc);
	}

	public Set<Integer> getPacsSelecionados() {
		Set<Integer> selecionados = new HashSet<Integer>();

		for (Entry<Integer, Boolean> entry : pacsDisponiveis.entrySet()) {
			if (entry.getValue()) {
				selecionados.add(entry.getKey());
			}
		}

		return selecionados;
	}

	public Boolean verificarUtilizaParecerTecnico() {
		try {
			return this.pacFacade.verificarUtilizaParecerTecnico();
		} catch (ApplicationBusinessException e) {
			return false;
		}
	}

	// Getters/Setters

	public PacParaJulgamentoCriteriaVO getCriteria() {
		return criteria;
	}

	public Map<Integer, Boolean> getPacsDisponiveis() {
		return pacsDisponiveis;
	}

	public PacParaJulgamentoVO getPacJulgado() {
		return pacJulgado;
	}

	public Set<Integer> getPacs() {
		return pacs;
	}

	public Boolean getAssinaturas() {
		return assinaturas;
	}

	public DynamicDataModel<PacParaJulgamentoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PacParaJulgamentoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public RelatorioQuadroPropostasProvisorioController getRelatorioQuadroPropostasProvisorioController() {
		return relatorioQuadroPropostasProvisorioController;
	}

	public void setRelatorioQuadroPropostasProvisorioController(
			RelatorioQuadroPropostasProvisorioController relatorioQuadroPropostasProvisorioController) {
		this.relatorioQuadroPropostasProvisorioController = relatorioQuadroPropostasProvisorioController;
	}
	public Integer getNroPac() {
		return nroPac;
	}
	public void setNroPac(Integer nroPac) {
		this.nroPac = nroPac;
	}
	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}
	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
}