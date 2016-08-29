/**
 * 
 */
package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.LicitacoesLiberarCriteriaVO;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller de Encaminhamento/Liberação de Licitações
 * 
 * @author Squadra
 */

public class EncaminhaLicitacoesLiberarController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<ScoLicitacao> dataModel;
	private static final String PAGE_MANTER_ENCAMINHAR_PROPOSTAS_FORNECEDORES = "manterEncaminharPropostasFornecedores";
	private static final String PAGE_ITEM_PROPOSTA_FORNECEDOR_CRUD = "itemPropostaFornecedorCRUD";
	private static final String PAGE_RELATORIO_QUADRO_PROPOSTA_PROVISORIO = "compras-relatorioQuadroPropostasProvisorio";
	private static final String PAGE_RELATORIO_QUADRO_JULGAMENTO_PROPOSTAS = "compras-relatorioQuadroJulgamentoPropostas";
	private static final String PAGE_ENCAMINHAR_LICITACOES_LIBERAR = "compras-encaminharLicitacoesLiberar";

	private static final long serialVersionUID = -1363544428651479731L;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	/** Critério para Consulta */
	private LicitacoesLiberarCriteriaVO criteria;

	/** Licitação */
	private ScoLicitacao licitacao;

	private String voltarPara;

	private Integer numeroPac;

	private Short numeroInicial;
	private Short numeroFinal;
	private String listaItens;
	private Boolean mostraFiltroQuadroProvisorio;
	private Boolean mostraFiltroQuadroJulgamento;

	@Inject
	RelatorioQuadroJulgamentoPropostaController relatorioQuadroJulgamentoPropostaController;
	
	@Inject
	RelatorioQuadroPropostasProvisorioController relatorioQuadroPropostasProvisorioController;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Executado quando a tela é acessado.
	 */
	public void inicio() {

		criteria = new LicitacoesLiberarCriteriaVO();
		setMostraFiltroQuadroProvisorio(Boolean.FALSE);
		if (this.numeroPac != null) {
			this.criteria.setNumero(this.getNumeroPac());
			this.pesquisar();
		}
	}
	

	public String encaminharPropostaFornecedores() {
		return PAGE_MANTER_ENCAMINHAR_PROPOSTAS_FORNECEDORES;
	}

	public String encaminharItemPropostaFornecedor() {
		return PAGE_ITEM_PROPOSTA_FORNECEDOR_CRUD;
	}

	/**
	 * Limpa os filtros da pesquisa e o grid que exibe o resultado.
	 */
	public void limparCampos() {
		criteria = new LicitacoesLiberarCriteriaVO();
		this.numeroInicial = null;
		this.numeroFinal = null;
		this.listaItens = null;
		setMostraFiltroQuadroProvisorio(Boolean.FALSE);
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	/**
	 * Atualiza criteria a partir do nro. do PAC.
	 */
	public void refreshFromLicitacaoId() {
		Integer licitacaoId = criteria.getNumero();
		criteria = new LicitacoesLiberarCriteriaVO();
		criteria.setNumero(licitacaoId);
	}

	/**
	 * Atualiza criteria a partir do nro. da SC.
	 */
	public void refreshFromSolicitacaoCompraId() {
		Integer scId = criteria.getSolicitacaoCompraId();
		criteria = new LicitacoesLiberarCriteriaVO();
		criteria.setTipo(DominioTipoSolicitacao.SC);
		criteria.setSolicitacaoCompraId(scId);
	}

	/**
	 * Atualiza criteria a partir do nro. da SS.
	 */
	public void refreshFromSolicitacaoServicoId() {
		Integer ssId = criteria.getSolicitacaoServicoId();
		criteria = new LicitacoesLiberarCriteriaVO();
		criteria.setTipo(DominioTipoSolicitacao.SS);
		criteria.setSolicitacaoServicoId(ssId);
	}

	/**
	 * Limpa campos conforme tipo selecioando.
	 */
	public void refreshFromTipo() {
		if (criteria.getTipo() == null){
			criteria.setSolicitacaoServicoId(null);
			criteria.setServico(null);
			criteria.setSolicitacaoCompraId(null);
			criteria.setMaterial(null);
			
		}else {
			switch (criteria.getTipo()) {
			case SC:
				criteria.setSolicitacaoServicoId(null);
				criteria.setServico(null);
				break;
	
			case SS:
				criteria.setSolicitacaoCompraId(null);
				criteria.setMaterial(null);
				break;
			}
		}
	}

	/**
	 * Obter materiais.
	 * 
	 * @return Materiais
	 */
	public List<ScoMaterial> pesquisarMateriais(String filtro) {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(filtro, null, true),listarMateriaisCount(filtro));
	}

	public Long listarMateriaisCount(String param) {
		return this.solicitacaoComprasFacade.listarMateriaisAtivosCount(param, this.obterLoginUsuarioLogado());
	}

	/**
	 * Obter serviços.
	 * 
	 * @return Serviços
	 */
	public List<ScoServico> pesquisarServicos(String filter) {
		return comprasFacade.listarServicos(filter);
	}

	/**
	 * Pesquisa licitações.
	 */
	public void pesquisar() {
		if (criteria.getNumero() != null) {
			refreshFromLicitacaoId();
		}
		criteria.setDescricao(StringUtils.trimToNull(criteria.getDescricao()));

		// Ajusta período caso tenha sido digitado ao contrário
		if (criteria.getGeracao() != null) {
			if (criteria.getGeracao().getInicio() != null && criteria.getGeracao().getFim() != null) {
				if (criteria.getGeracao().getInicio().after(criteria.getGeracao().getFim())) {
					Date inicio = criteria.getGeracao().getInicio();
					criteria.getGeracao().setInicio(criteria.getGeracao().getFim());
					criteria.getGeracao().setFim(inicio);
				}
			}
		}

		// Deve ser informado no mínimo um dos filtros solicitados.
		if (criteria.isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, "MESSAGE_INFORME_FILTROS_PARA_REALIZAR_PESQUISA");

			return;
		}

		dataModel.reiniciarPaginator();
	}

	/**
	 * Pesquisa do suggestion box Modalidade
	 * 
	 * @param parametro
	 * @return
	 */
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacaoPorCodigoDescricao(String parametro) {
		return comprasCadastrosBasicosFacade.pesquisarModalidadeLicitacaoPorCodigoDescricao(parametro);
	}

	/**
	 * Pesquisa do suggestion box Comprador
	 * 
	 * @param parametro
	 * @return
	 */
	public List<RapServidores> pesquisarGestores(String parametro) {
		return registroColaboradorFacade.pesquisarCompradoresAtivos(parametro, 0, 100, RapServidores.Fields.ID.toString(), true);
	}

	@Override
	public Long recuperarCount() {
		return pacFacade.pesquisarLicitacoesLiberarCount(criteria);
	}

	@Override
	public List<ScoLicitacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String order, boolean asc) {
		return pacFacade.pesquisarLicitacoesLiberar(criteria, firstResult, maxResult, ScoLicitacao.Fields.NUMERO.toString(),
				false);
	}

	public void prepararModalParametrosQuadroProvisorio(ScoLicitacao licitacao) {
		this.numeroInicial = null;
		this.numeroFinal = null;
		this.listaItens = null;
		this.licitacao = licitacao;
		setMostraFiltroQuadroProvisorio(Boolean.TRUE);
	}

	public void prepararModalParametrosQuadroJulgamento(ScoLicitacao licitacao) {
		this.numeroInicial = null;
		this.numeroFinal = null;
		this.listaItens = null;
		this.licitacao = licitacao;
		setMostraFiltroQuadroJulgamento(Boolean.TRUE);
	}

	/**
	 * Inclui proposta fornecedor.
	 * 
	 * @param licitacao
	 *            Licitação
	 * @return Redirect
	 */
	public Boolean verificarPropostaFornecedor(ScoLicitacao licitacao) {
		return pacFacade.contarPropostas(licitacao, null) > 0;
	}

	private String removerVirgulasIndevidasListaCsv() {
		String ret = "";
		if (StringUtils.isNotBlank(this.listaItens) && this.listaItens.length() > 0) {
			HashSet<String> inputSet = new HashSet<String>(Arrays.asList(this.listaItens.split(",")));
			StringBuilder sbRet = new StringBuilder();

			for (String item : inputSet) {
				if (StringUtils.isNotBlank(item)) {
					sbRet.append(item).append(',');
				}
			}

			ret = sbRet.toString().substring(0, sbRet.toString().length() - 1);
		}

		return ret;
	}

	/**
	 * Visualiza quadro provisório.
	 * 
	 * @return Redirect
	 * @throws IOException 
	 * @throws SystemException 
	 * @throws JRException 
	 * @throws BaseException 
	 */
	public String verQuadroProvisorio() throws BaseException, JRException, SystemException, IOException {
		this.listaItens = this.removerVirgulasIndevidasListaCsv();
		this.mostraFiltroQuadroProvisorio = Boolean.FALSE;
		
		
       relatorioQuadroPropostasProvisorioController.setNumPac(licitacao.getNumero());
       relatorioQuadroPropostasProvisorioController.setVoltarParaUrl(PAGE_ENCAMINHAR_LICITACOES_LIBERAR);
       relatorioQuadroPropostasProvisorioController.setNumeroItemInicial(numeroInicial);
       relatorioQuadroPropostasProvisorioController.setNumeroItemFinal(numeroFinal);
       relatorioQuadroPropostasProvisorioController.setListaItens(listaItens);
       		  
       relatorioQuadroPropostasProvisorioController.print();
       
		return PAGE_RELATORIO_QUADRO_PROPOSTA_PROVISORIO;
	}

	/**
	 * Visualiza quadro Julgamento.
	 * 
	 * @return Redirect
	 * @throws IOException 
	 * @throws SystemException 
	 * @throws JRException 
	 * @throws BaseException 
	 */
	public String verQuadroJulgamento() throws BaseException, JRException, SystemException, IOException {
		this.listaItens = this.removerVirgulasIndevidasListaCsv();
		this.mostraFiltroQuadroJulgamento = Boolean.FALSE;
	   
        relatorioQuadroJulgamentoPropostaController.setNumPac(licitacao.getNumero());
        relatorioQuadroJulgamentoPropostaController.setVoltarParaUrl(PAGE_ENCAMINHAR_LICITACOES_LIBERAR);
        relatorioQuadroJulgamentoPropostaController.setNumeroItemInicial(numeroInicial);
        relatorioQuadroJulgamentoPropostaController.setNumeroItemFinal(numeroFinal);
        relatorioQuadroJulgamentoPropostaController.setListaItens(listaItens);
        		  
		relatorioQuadroJulgamentoPropostaController.print();
		return PAGE_RELATORIO_QUADRO_JULGAMENTO_PROPOSTAS;
	}

	/**
	 * Confirma encaminhamento de proposta ao parecer técnico.
	 */
	public void encaminharParecerTecnico() {
		pacFacade.encaminharParecerTecnico(licitacao);

		apresentarMsgNegocio(Severity.INFO, "PAC_ENCAMINHADO_PARECER_TECNICO", licitacao.getNumero().toString(),
				licitacao.getDescricao());
	}

	/**
	 * Confirma encaminhamento de proposta ao comitê de licitação.
	 */
	public void encaminharComissaoLicitacao() {
		pacFacade.encaminharComissao(licitacao);

		apresentarMsgNegocio(Severity.INFO, "PAC_ENCAMINHADO_COMISSAO_LICITACAO", licitacao.getNumero().toString(),
				licitacao.getDescricao());
	}

	public Boolean verificarUtilizaParecerTecnico() {
		try {
			Boolean ret = Boolean.FALSE;			
			AghParametros quadroProvisorio = parametroFacade.buscarAghParametro(
					AghuParametrosEnum.P_QUADRO_PROVISORIO);
			
			if (quadroProvisorio != null && DominioSimNao.S.toString().equals(quadroProvisorio.getVlrTexto())) {
				ret = Boolean.TRUE;
			}
			
			return ret;
		} catch (ApplicationBusinessException e) {
			return false;
		}
	}

	// Getters/Setters

	public LicitacoesLiberarCriteriaVO getCriteria() {
		return criteria;
	}

	public void setCriteria(LicitacoesLiberarCriteriaVO criteria) {
		this.criteria = criteria;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String voltar() {
		return voltarPara;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public Short getNumeroInicial() {
		return numeroInicial;
	}

	public void setNumeroInicial(Short numeroInicial) {
		this.numeroInicial = numeroInicial;
	}

	public Short getNumeroFinal() {
		return numeroFinal;
	}

	public void setNumeroFinal(Short numeroFinal) {
		this.numeroFinal = numeroFinal;
	}

	public String getListaItens() {
		return listaItens;
	}

	public void setListaItens(String listaItens) {
		this.listaItens = listaItens;
	}

	public Boolean getMostraFiltroQuadroProvisorio() {
		return mostraFiltroQuadroProvisorio;
	}

	public void setMostraFiltroQuadroProvisorio(Boolean mostraFiltroQuadroProvisorio) {
		this.mostraFiltroQuadroProvisorio = mostraFiltroQuadroProvisorio;
	}

	public Boolean getMostraFiltroQuadroJulgamento() {
		return mostraFiltroQuadroJulgamento;
	}

	public void setMostraFiltroQuadroJulgamento(Boolean mostraFiltroQuadroJulgamento) {
		this.mostraFiltroQuadroJulgamento = mostraFiltroQuadroJulgamento;
	}

	public DynamicDataModel<ScoLicitacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoLicitacao> dataModel) {
		this.dataModel = dataModel;
	}
}
