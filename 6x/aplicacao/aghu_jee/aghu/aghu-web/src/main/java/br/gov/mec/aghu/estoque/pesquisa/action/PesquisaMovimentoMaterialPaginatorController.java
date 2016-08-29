package br.gov.mec.aghu.estoque.pesquisa.action;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioComparacaoDataCompetencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceMovimentoMaterialId;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaMovimentoMaterialPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<MovimentoMaterialVO> dataModel;

	private static final long serialVersionUID = -368143986241841796L;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	private SceTipoMovimento tipoMovimento;
	private SceAlmoxarifado almoxarifado;
	private FccCentroCustos centroCusto;
	private ScoMaterial material;
	private Date dtGeracao;
	private DominioSimNao indEstorno;
	private ScoFornecedor fornecedor;
	private MovimentoMaterialVO movimentoMaterialDataCompetencia;
	
	// Filtro Intervalo da Data/Mês de Competência
	private DominioComparacaoDataCompetencia comparacaoDataCompetencia = DominioComparacaoDataCompetencia.IGUAL;

	private Integer nroDocGeracao;
	private MovimentoMaterialVO movimentoMaterialVO;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private SceMovimentoMaterial obterMovimentoMaterial() {
		SceMovimentoMaterial mov  = null;
		
		if (movimentoMaterialDataCompetencia != null) {
			SceMovimentoMaterialId id = new SceMovimentoMaterialId();
			id.setSeq(movimentoMaterialDataCompetencia.getSeq());
			id.setDtCompetencia(movimentoMaterialDataCompetencia.getCompetencia());
			
			mov = this.estoqueFacade.obterMovimetnoMaterialPorId(id);
			mov.setDtCompetencia(movimentoMaterialDataCompetencia.getCompetencia());
			mov.setSeq(movimentoMaterialDataCompetencia.getSeq());
		}
		
		return mov;
	}
	
	public void pesquisar() {
		try {
			
			estoqueFacade.validaCamposPesquisaMovimentoMaterial(tipoMovimento, material, dtGeracao, almoxarifado, centroCusto, indEstorno, fornecedor, obterMovimentoMaterial(), nroDocGeracao);
			dataModel.reiniciarPaginator();
			
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.WARN, e.getMessage());
		}
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		this.almoxarifado = null;
		this.centroCusto = null;
		this.dtGeracao = null;
		this.fornecedor = null;
		this.material = null;
		this.movimentoMaterialDataCompetencia = null;
		this.nroDocGeracao = null;
		this.tipoMovimento = null;
		this.movimentoMaterialVO = null;
		this.comparacaoDataCompetencia = DominioComparacaoDataCompetencia.IGUAL;
		this.indEstorno = null;
	}
	
	public void inicio() {
		if (!this.dataModel.getPesquisaAtiva() && this.movimentoMaterialDataCompetencia == null)   {
			this.movimentoMaterialDataCompetencia = pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(null).get(0);
		}
	}


	@Override
	public Long recuperarCount() {
		return estoqueFacade.pesquisarMovimentosMaterialCount(tipoMovimento, material, dtGeracao, almoxarifado, centroCusto, indEstorno, fornecedor, obterMovimentoMaterial(), comparacaoDataCompetencia, nroDocGeracao);
	}

	@Override
	public List<MovimentoMaterialVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		try {
			
			return estoqueFacade.pesquisarMovimentosMaterial(tipoMovimento, material, dtGeracao, almoxarifado, centroCusto, indEstorno, fornecedor, obterMovimentoMaterial(), comparacaoDataCompetencia, nroDocGeracao, firstResult, maxResult, orderProperty, asc);
		
		} catch (BaseException e) {
			
			apresentarMsgNegocio(Severity.WARN, e.getMessage());
			return null;
			
		}

	}
	
	/**
	 * Metodo para pesquisa na suggestion box de tipo de movimento
	 */
	public List<SceTipoMovimento> obterSceTipoMovimento(String objPesquisa) throws ApplicationBusinessException {
		return this.estoqueFacade.obterTipoMovimentoPorSigla(objPesquisa);
	}
	
	/**
	 * Metodo para pesquisa na suggestion box de material
	 */
	public List<ScoMaterial> obterMaterial(String paramPesq) throws ApplicationBusinessException {
		return this.comprasFacade.pesquisarMaterial(paramPesq);
	}
	
	/**
	 * Obtem lista para sugestion box de almoxarifado 
	 */
	public List<SceAlmoxarifado> obterAlmoxarifado(String param){
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}
	
	/**
	 * Metodo para pesquisa na suggestion box de CC Requisicao
	 */
	public List<FccCentroCustos> obterFccCentroCustos(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustos(objPesquisa);
	}
	
	/**
	 * Pesquisa para o suggestion de fornecedores
	 */
	public List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocial(String objPesquisa) {
		return comprasFacade.pesquisarFornecedoresPorNumeroRazaoSocial(objPesquisa);
	}	
	
	/**
	 * Método que realiza a pesquisa de competencias de estoque geral, por mes e ano.
	 */
	public List<MovimentoMaterialVO> pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(String paramPesquisa){
		
		List<MovimentoMaterialVO> lista = null;
		
		try {
		
			lista = estoqueFacade.pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(paramPesquisa);
		
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
		return lista;
		
	}
	
	/**
	 * Popula a modal
	 * @param vo
	 */
	public void detalharMovimento(MovimentoMaterialVO _vo) {
		
		this.movimentoMaterialVO = new MovimentoMaterialVO();
		SceMovimentoMaterial movimento = estoqueFacade.obterMovimetnoMaterialPorId(_vo.getId());
		
		if (movimento != null) {
			
			movimentoMaterialVO.setAlmoxarifadoModal(movimento.getAlmoxarifado().getSeqDescricao());
			
			if (movimento.getAlmoxarifadoComplemento() != null) {
				movimentoMaterialVO.setAlmoxarifadoComplementoModal(movimento.getAlmoxarifadoComplemento().getSeqDescricao());
			}
			
			if (movimento.getCentroCusto() != null) {
				movimentoMaterialVO.setCentroCustoAplicacaoModal(movimento.getCentroCusto().getCodigoDescricao());
			}
			
			if (movimento.getCentroCustoRequisita() != null) {
				movimentoMaterialVO.setCentroCustoReqModal(movimento.getCentroCustoRequisita().getCodigoDescricao());
			}
			
			if (movimento.getMotivoMovimento() != null) {
				movimentoMaterialVO.setMotivoModal(movimento.getMotivoMovimento().getDescricao());
			}
			
			if (movimento.getServidor() != null) {
				movimentoMaterialVO.setUsuarioModal(movimento.getServidor().getPessoaFisica().getNome());
			}
			
			if (movimento.getTipoMovimento() != null) {
				movimentoMaterialVO.setDescricaoModal(movimento.getTipoMovimento().getDescricao());
				
				if (movimento.getTipoMovimento().getSigla().equals(("RM"))) {
					SceReqMaterial rm = this.estoqueFacade.obterRequisicaoMaterial(movimento.getNroDocGeracao());
					
					if (rm != null && rm.getAtendimento() != null) {
						movimentoMaterialVO.setNomePaciente(rm.getAtendimento().getPaciente().getNome());
						movimentoMaterialVO.setNumeroAtendimento(rm.getAtendimento().getSeq());
						movimentoMaterialVO.setNumeroProntuario(rm.getAtendimento().getPaciente().getProntuario());
					}
				}
			}
			
			if (movimento.getCustoMedioPonderadoGer()!= null) {
				DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
			    symbols.setDecimalSeparator('.');
				DecimalFormat format = new DecimalFormat("#.#####", symbols);
				movimentoMaterialVO.setCustoMedioPonderadoGerModal(String.valueOf(format.format(movimento.getCustoMedioPonderadoGer())));
			}
			
			if (movimento.getResiduo() != null) {
				movimentoMaterialVO.setResiduo(movimento.getResiduo());
			}
			
			movimentoMaterialVO.setItemDocGeracaoModal(movimento.getItemDocGeracao());
			movimentoMaterialVO.setNroDocRefereModal(movimento.getNroDocRefere());
			movimentoMaterialVO.setHistoricoModal(movimento.getHistorico());
			movimentoMaterialVO.setQtdeRequisitadaModal(movimento.getQtdeRequisitada());
			movimentoMaterialVO.setQtdePosMovimentoModal(movimento.getQtdePosMovimento());
			movimentoMaterialVO.setMaterial(movimento.getMaterial().getCodigo().toString() + " - " + movimento.getMaterial().getNome());
			movimentoMaterialVO.setNroDocGeracao(movimento.getNroDocGeracao());
			movimentoMaterialVO.setQuantidade(movimento.getQuantidade());
		}
	}

	// GETS AND SETS

	public SceTipoMovimento getTipoMovimento() {
		return tipoMovimento;
	}

	public void setTipoMovimento(SceTipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public DominioSimNao getIndEstorno() {
		return indEstorno;
	}

	public void setIndEstorno(DominioSimNao indEstorno) {
		this.indEstorno = indEstorno;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Integer getNroDocGeracao() {
		return nroDocGeracao;
	}

	public void setNroDocGeracao(Integer nroDocGeracao) {
		this.nroDocGeracao = nroDocGeracao;
	}

	public MovimentoMaterialVO getMovimentoMaterialDataCompetencia() {
		return movimentoMaterialDataCompetencia;
	}

	public void setMovimentoMaterialDataCompetencia(
			MovimentoMaterialVO movimentoMaterialDataCompetencia) {
		this.movimentoMaterialDataCompetencia = movimentoMaterialDataCompetencia;
	}

	public MovimentoMaterialVO getMovimentoMaterialVO() {
		return movimentoMaterialVO;
	}

	public void setMovimentoMaterialVO(MovimentoMaterialVO movimentoMaterialVO) {
		this.movimentoMaterialVO = movimentoMaterialVO;
	}

	public DominioComparacaoDataCompetencia getComparacaoDataCompetencia() {
		return comparacaoDataCompetencia;
	}

	public void setComparacaoDataCompetencia(
			DominioComparacaoDataCompetencia comparacaoDataCompetencia) {
		this.comparacaoDataCompetencia = comparacaoDataCompetencia;
	}

	public DynamicDataModel<MovimentoMaterialVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MovimentoMaterialVO> dataModel) {
		this.dataModel = dataModel;
	}
}
