package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.vo.PesquisarProgEntregaItensAFParcelasVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoParcelaItemVO;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoJustificativa;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PesquisarProgEntregaItensAFParcelasController extends ActionController {
	
	private static final long serialVersionUID = 5373100830742540997L;
	private static final String PESQUISA_GRUPO_MATERIAL_ENTREGA_PROGRAMADA = "pesquisaGrupoMaterialEntregaProgramada";
	private static final String PROGRAMACAO_ENTREGA_GLOBAL_FORNECEDORES = "programacaoEntregaGlobalFornecedores";
	private static final String CONSULTAR_ITENS_AF_ENTREGA_PROGRAMADA = "consultarItensAFEntregaProgramada";
	private static final String PESQUISA_AUTFORNECIMENTO_ENTREGA_PROGRAMADA = "pesquisaAutFornecimentoEntregaProgramada";

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	private List<ScoProgEntregaItemAutorizacaoFornecimento> listaProgEntregaItemAF;
	private List<PesquisarProgEntregaItensAFParcelasVO> listaParcelasVO;
	private PesquisarProgEntregaItensAFParcelasVO itemParcela;
	
	private ProgramacaoParcelaItemVO progParcelaItemVO;
	
	private Integer iafAfnNumero; 
	private Integer iafNumero; 
	private Date dataInicial; 
	private Date dataFinal;
	private String voltarParaUrl;
	private String voltarParaInicio;
	
	private Integer codigoMaterial;
	private Short codigoJustificativa;
	private Integer numeroFornecedor;
		
	private ScoGrupoMaterial grupoMaterial;
	private String labelGrupoMaterial;
	
	private ScoJustificativa justifEmpenho;
	private String labelJustifEmpenho;
	
	private ScoMaterial material;
	private String labelMaterial;
	
	private SceEstoqueGeral curvaAbc;
	private String labelCurvaAbc;
	
	private ScoFornecedor fornecedor;
	private String labelFornecedor;
	
	private PesquisarProgEntregaItensAFParcelasVO selecionado;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
	 

	 

		
		try {
			
			if(dataInicial == null){
				AghParametros paramDataInicial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_INICIAL);
				this.setDataInicial(paramDataInicial.getVlrData());
			}
			
			if(dataFinal == null){
				AghParametros paramDataFinal = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_FINAL);
				this.setDataFinal(paramDataFinal.getVlrData());
			} else {
				dataFinal = DateUtil.adicionaDias(dataFinal, 1);
			}
			this.listaProgEntregaItemAF = this.comprasFacade.pesquisarProgEntregaItensAFParcelas(iafAfnNumero, iafNumero, numeroFornecedor, dataInicial, dataFinal);

			this.listaParcelasVO = new ArrayList<PesquisarProgEntregaItensAFParcelasVO>();
			
			for (ScoProgEntregaItemAutorizacaoFornecimento itemAF : this.listaProgEntregaItemAF) {
				
				PesquisarProgEntregaItensAFParcelasVO itemAFVO = new PesquisarProgEntregaItensAFParcelasVO();
				
				this.progParcelaItemVO = this.comprasFacade.pesquisarProgParcelaItem(itemAF.getId().getIafAfnNumero(), itemAF.getId().getIafNumero());
				
				itemAFVO.setNumeroAf(this.progParcelaItemVO.getNumeroAf());
				itemAFVO.setNroComplemento(this.progParcelaItemVO.getNroComplemento());
				itemAFVO.setNumeroItem(this.progParcelaItemVO.getNumeroItem());
				itemAFVO.setUnidFornecimento(this.progParcelaItemVO.getUnidFornecimento());
				itemAFVO.setFatorConversao(this.progParcelaItemVO.getFatorConversao());
				itemAFVO.setUnidEstoque(this.progParcelaItemVO.getUnidEstoque());
				itemAFVO.setCodigoMaterial(this.progParcelaItemVO.getCodigoMaterial());
				itemAFVO.setNumeroFornecedor(this.progParcelaItemVO.getNumeroFornecedor());
				itemAFVO.setDtVencContrato(this.progParcelaItemVO.getDtVencContrato());
				
				itemAFVO.setDtPrevEntrega(itemAF.getDtPrevEntrega());
				itemAFVO.setParcela(itemAF.getId().getParcela());
				itemAFVO.setQtde(itemAF.getQtde());
				itemAFVO.setValorTotal(itemAF.getValorTotal());
				itemAFVO.setIndAssinatura(itemAF.getIndAssinatura());
				itemAFVO.setIndCancelada(itemAF.getIndCancelada());
				itemAFVO.setIndPlanejamento(itemAF.getIndPlanejamento());
				itemAFVO.setIndEmpenhada(itemAF.getIndEmpenhada());
				itemAFVO.setIndEnvioFornecedor(itemAF.getIndEnvioFornecedor());
				itemAFVO.setIndRecalculoAutomatico(itemAF.getIndRecalculoAutomatico());
				itemAFVO.setIndRecalculoManual(itemAF.getIndRecalculoManual());
				itemAFVO.setCodigoJustificativa(itemAF.getScoJustificativa() != null ? itemAF.getScoJustificativa().getCodigo() : null);
				
				if (this.progParcelaItemVO.getFatorConversao() == null) {
					this.progParcelaItemVO.setFatorConversao(0);
				}
				
				Integer entrada = this.progParcelaItemVO.getFatorConversao() * this.progParcelaItemVO.getFatorConversao();
				itemAFVO.setQtdeEntrada(entrada);
				
				if(itemAF.getIndPlanejamento() && itemAF.getIndAssinatura() && 
						CoreUtil.isMenorOuIgualDatas(itemAF.getDtPrevEntrega(), new Date())){
				
					if(CoreUtil.isMaiorOuIgualDatas(itemAF.getDtPrevEntrega(), dataInicial) && 
							CoreUtil.isMenorOuIgualDatas(itemAF.getDtPrevEntrega(), dataFinal) &&
							CoreUtil.isMaiorDatas(this.progParcelaItemVO.getDtVencContrato(), new Date())){
						//azul 
						itemAFVO.setCorCelula("background-color: #95BCF2;");
	
					} else if(CoreUtil.isMaiorOuIgualDatas(itemAF.getDtPrevEntrega(), dataInicial) && 
							CoreUtil.isMenorOuIgualDatas(itemAF.getDtPrevEntrega(), dataFinal) && 
							!itemAF.getIndAssinatura() && 
							CoreUtil.isMaiorDatas(this.progParcelaItemVO.getDtVencContrato(), new Date())){
						//amarelo
						itemAFVO.setCorCelula("background-color: #FFFF80;");
					
					}  else if(CoreUtil.isMaiorOuIgualDatas(itemAF.getDtPrevEntrega(), dataInicial) && 
							CoreUtil.isMenorOuIgualDatas(itemAF.getDtPrevEntrega(), dataFinal) &&
							itemAF.getIndPlanejamento() && itemAF.getIndAssinatura()){
						//verde
						itemAFVO.setCorCelula("background-color: #80CF87;");
				
					} else if(CoreUtil.isMaiorOuIgualDatas(itemAF.getDtPrevEntrega(), dataInicial) && 
							CoreUtil.isMenorOuIgualDatas(itemAF.getDtPrevEntrega(), dataFinal)){
						//vermelho
						itemAFVO.setCorCelula("background-color: #E68080;");
						
					}
				}
				
				this.listaParcelasVO.add(itemAFVO);
				
			}
			
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		
	
	}
	
	
	public void exibirDetalheParcela(){
		
		try {
			
			if(this.selecionado.getCodigoJustificativa() != null){
				this.justifEmpenho = this.comprasCadastrosBasicosFacade.obterJustificativa(this.selecionado.getCodigoJustificativa());
				this.labelJustifEmpenho = String.valueOf(this.justifEmpenho.getCodigo());
			}
			
			if(this.selecionado.getCodigoMaterial() != null){
				this.material = this.comprasFacade.obterMaterialPorId(this.selecionado.getCodigoMaterial());
				this.labelMaterial = this.material.getCodigoENome();
				
				this.grupoMaterial = this.material.getGrupoMaterial();
				this.labelGrupoMaterial = String.valueOf(this.grupoMaterial.getCodigo()).concat(" - ").concat(this.grupoMaterial.getDescricao());

				AghParametros fornPadrao = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
				Integer vlrNumerico = fornPadrao.getVlrNumerico().intValue();
				this.curvaAbc = this.estoqueFacade.pesquisarEstoqueGeralPorMatDtCompFornecedor(this.material.getCodigo(), new Date(), vlrNumerico);
			}
			
			if(this.curvaAbc != null){
				this.labelCurvaAbc = this.curvaAbc.getClassificacaoAbc().getDescricao().concat(" ").concat(this.curvaAbc.getSubClassificacaoAbc().getDescricao());
			}
			
			if(this.selecionado.getNumeroFornecedor() != null){
				this.fornecedor = this.comprasFacade.obterFornecedorPorNumero(this.selecionado.getNumeroFornecedor());
				this.labelFornecedor = String.valueOf(this.fornecedor.getNumero()).concat(" - ").concat(this.fornecedor.getRazaoSocial());
			}
			
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		
	}
	
	public String voltar() {
		
		this.iafAfnNumero = null;
		this.iafNumero = null;
		this.limparCamposDetalhe();
		if(voltarParaUrl.equalsIgnoreCase("programacaoEntregaGlobalFornecedores")){
			return PROGRAMACAO_ENTREGA_GLOBAL_FORNECEDORES;
		} else if(voltarParaUrl.equalsIgnoreCase("consultarItensAFEntregaProgramada")){
			return CONSULTAR_ITENS_AF_ENTREGA_PROGRAMADA;
		} else if(voltarParaUrl.equalsIgnoreCase("pesquisaGrupoMaterialEntregaProgramada")){
			return PESQUISA_GRUPO_MATERIAL_ENTREGA_PROGRAMADA;
		} else if (voltarParaUrl.equalsIgnoreCase("pesquisaAutFornecimentoEntregaProgramada")){
			return PESQUISA_AUTFORNECIMENTO_ENTREGA_PROGRAMADA;
		}
		return null;
	}
	
	public void limparCamposDetalhe(){
		
		
		this.setLabelCurvaAbc(null);
		this.setLabelFornecedor(null);
		this.setLabelGrupoMaterial(null);
		this.setLabelJustifEmpenho(null);
		this.setLabelMaterial(null);
		
	}
	
	public String voltarInicio(){
		this.limparCamposDetalhe();
		return PESQUISA_GRUPO_MATERIAL_ENTREGA_PROGRAMADA;
	}

	public List<ScoProgEntregaItemAutorizacaoFornecimento> getListaProgEntregaItemAF() {
		return listaProgEntregaItemAF;
	}

	public void setListaProgEntregaItemAF(List<ScoProgEntregaItemAutorizacaoFornecimento> listaProgEntregaItemAF) {
		this.listaProgEntregaItemAF = listaProgEntregaItemAF;
	}

	public List<PesquisarProgEntregaItensAFParcelasVO> getListaParcelasVO() {
		return listaParcelasVO;
	}

	public void setListaParcelasVO(List<PesquisarProgEntregaItensAFParcelasVO> listaParcelasVO) {
		this.listaParcelasVO = listaParcelasVO;
	}

	public PesquisarProgEntregaItensAFParcelasVO getItemParcela() {
		return itemParcela;
	}

	public void setItemParcela(PesquisarProgEntregaItensAFParcelasVO itemParcela) {
		this.itemParcela = itemParcela;
	}

	public ProgramacaoParcelaItemVO getProgParcelaItemVO() {
		return progParcelaItemVO;
	}

	public void setProgParcelaItemVO(ProgramacaoParcelaItemVO progParcelaItemVO) {
		this.progParcelaItemVO = progParcelaItemVO;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public ScoJustificativa getJustifEmpenho() {
		return justifEmpenho;
	}

	public void setJustifEmpenho(ScoJustificativa justifEmpenho) {
		this.justifEmpenho = justifEmpenho;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public SceEstoqueGeral getCurvaAbc() {
		return curvaAbc;
	}

	public void setCurvaAbc(SceEstoqueGeral curvaAbc) {
		this.curvaAbc = curvaAbc;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setComprasCadastrosBasicosFacade(IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}

	public void setEstoqueFacade(IEstoqueFacade estoqueFacade) {
		this.estoqueFacade = estoqueFacade;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Short getCodigoJustificativa() {
		return codigoJustificativa;
	}

	public void setCodigoJustificativa(Short codigoJustificativa) {
		this.codigoJustificativa = codigoJustificativa;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public String getLabelGrupoMaterial() {
		return labelGrupoMaterial;
	}

	public void setLabelGrupoMaterial(String labelGrupoMaterial) {
		this.labelGrupoMaterial = labelGrupoMaterial;
	}

	public String getLabelJustifEmpenho() {
		return labelJustifEmpenho;
	}

	public void setLabelJustifEmpenho(String labelJustifEmpenho) {
		this.labelJustifEmpenho = labelJustifEmpenho;
	}

	public String getLabelMaterial() {
		return labelMaterial;
	}

	public void setLabelMaterial(String labelMaterial) {
		this.labelMaterial = labelMaterial;
	}

	public String getLabelCurvaAbc() {
		return labelCurvaAbc;
	}

	public void setLabelCurvaAbc(String labelCurvaAbc) {
		this.labelCurvaAbc = labelCurvaAbc;
	}

	public String getLabelFornecedor() {
		return labelFornecedor;
	}

	public void setLabelFornecedor(String labelFornecedor) {
		this.labelFornecedor = labelFornecedor;
	}

	public String getVoltarParaInicio() {
		return voltarParaInicio;
	}

	public void setVoltarParaInicio(String voltarParaInicio) {
		this.voltarParaInicio = voltarParaInicio;
	}

	public PesquisarProgEntregaItensAFParcelasVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(PesquisarProgEntregaItensAFParcelasVO selecionado) {
		this.selecionado = selecionado;
	}

}
