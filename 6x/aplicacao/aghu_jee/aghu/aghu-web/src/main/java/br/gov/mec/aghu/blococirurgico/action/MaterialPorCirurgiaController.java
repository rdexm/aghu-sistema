package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.MaterialPorCirurgiaVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class MaterialPorCirurgiaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6226064985357441586L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	@EJB
	private IComprasFacade comprasFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@Inject
	private RelatorioRegistroDaNotaDeSalaController relatorioRegistroDaNotaDeSalaController;
	
	/*Cirurgia*/
	private MbcCirurgias cirurgia;
	
	private MaterialPorCirurgiaVO materialPorCirurgia;
	private ScoMaterial scoMaterial;
	private ScoUnidadeMedida scoUnidadeMedida;
	
	/*Lista de Materiais por Cirurgia*/
	private List<MaterialPorCirurgiaVO> listaMbcMaterialPorCirurgia;
	
	private MaterialPorCirurgiaVO itemSelecionado;
	
	private boolean emEdicao;
	private boolean listaVazia;
	private AghParametros pDispensario;
	private boolean dispensario;
	
	private Double quantidade;
	
	private String voltarPara;
	
	/**
	 * Codigo da cirurgia, obtido via page parameter.
	 */
	private Integer mbcCirurgiaCodigo;
	
	private CirurgiaTelaProcedimentoVO procedimento;
	
	private final String PAG_RELATORIO_REGISTRO_NOTA_SALA = "blococirurgico-relatorioRegistroDaNotaDeSala";
	private final String PAG_MATERIAL_CIRURGIA_LIST = "materialPorCirurgiaList";
	
	public void inicio() {

		if (this.mbcCirurgiaCodigo != null) {
			this.cirurgia = this.blocoCirurgicoFacade.obterCirurgiaPorSeq(mbcCirurgiaCodigo);
		}
		
		if (this.cirurgia.getCentroCustos() == null) {
			this.cirurgia.setCentroCustos(this.obterCodigoCentroCustoResponsavel(cirurgia));
		}
		
		materialPorCirurgia = new MaterialPorCirurgiaVO();
		materialPorCirurgia.setCrgSeq(cirurgia.getSeq());
		
		this.procedimento =  this.buscaProcedimentoPrincipal();
		
		try {
			final AghParametros pGrMatOrtProt = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GR_MAT_ORT_PROT);
			this.pDispensario = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DISPENSARIO);
			
			this.setDispensario(this.verificaDispensario());
			
			List<MaterialPorCirurgiaVO> listaPesquisaMateriais = this.blocoCirurgicoFacade.pesquisarMateriaisPorCirurgia(cirurgia, pGrMatOrtProt, this.pDispensario);
			this.setListaMbcMaterialPorCirurgia(listaPesquisaMateriais);
			
			if (listaMbcMaterialPorCirurgia.isEmpty()) {
				this.setListaVazia(Boolean.TRUE);
			} else if (listaMbcMaterialPorCirurgia.get(0).getQuantidade() != null) {
				this.setListaVazia(Boolean.FALSE);
			}else{
				this.setListaVazia(Boolean.TRUE);
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private boolean verificaDispensario() {
		return this.pDispensario.getVlrTexto().equalsIgnoreCase("S");
	}
	
	private CirurgiaTelaProcedimentoVO buscaProcedimentoPrincipal(){
		
		List<CirurgiaTelaProcedimentoVO> listaProcedimentos = this.blocoCirurgicoFacade.pesquisarProcedimentosAgendaProcedimentos(cirurgia.getSeq(), DominioIndRespProc.NOTA);
		
		CirurgiaTelaProcedimentoVO procedimento = null;
		
		if(listaProcedimentos != null && listaProcedimentos.size() > 0){
			procedimento = listaProcedimentos.get(0);
		}
		if(procedimento == null){
			listaProcedimentos = this.blocoCirurgicoFacade.pesquisarProcedimentosAgendaProcedimentos(cirurgia.getSeq(), DominioIndRespProc.DESC);
			if(listaProcedimentos != null && listaProcedimentos.size() > 0){
				procedimento = listaProcedimentos.get(0);
			}
		}
		if(procedimento == null){
			listaProcedimentos = this.blocoCirurgicoFacade.pesquisarProcedimentosAgendaProcedimentos(cirurgia.getSeq(), DominioIndRespProc.AGND);
			if(listaProcedimentos != null && listaProcedimentos.size() > 0){
				procedimento = listaProcedimentos.get(0);
			}
		}
		
		return procedimento;
	}
	
	/**
	 * Obtém o centro de custo onde está alocado o cirurgião responsável
	 * pela cirurgia
	 */
	protected FccCentroCustos obterCodigoCentroCustoResponsavel(MbcCirurgias cirurgia) {
		
		MbcProfCirurgias profResponsavel = this.blocoCirurgicoFacade.retornaResponsavelCirurgia(cirurgia);
		
		if (profResponsavel != null) {
			return profResponsavel.getServidorPuc().getCentroCustoLotacao();
		} else {
			return null;
		}
	}
	
	/**
	 * Adiciona uma instância de ScoMaterial na lista
	 */
	public void adicionarMaterial() {
		if(this.validarCamposObrigatorios()) {
			if (CoreUtil.maior(this.quantidade, 0)){
				if (!this.emEdicao) {
					/*Verifica se existe o mesmo item com mesma unidade de medida*/
					if (this.listaMbcMaterialPorCirurgia.isEmpty()) {
						this.materialPorCirurgia.setQuantidade(this.quantidade);
					}
					for (MaterialPorCirurgiaVO materialCorrente: this.listaMbcMaterialPorCirurgia){
						/*Criado método equalsSemId, para que lista de itens já gravados em BD possa ser comparado com lista de itens adicionados sem ID*/ 
						if (this.equalsSemId(this.materialPorCirurgia, materialCorrente)
								&& this.materialPorCirurgia.getUmdCodigo().equals(materialCorrente.getUmdCodigo())) {
							if (materialCorrente.getQuantidade() != null){
								materialCorrente.setQuantidade(materialCorrente.getQuantidade() + this.quantidade);
							} else {
								materialCorrente.setQuantidade(this.quantidade);
							}
							this.limpar();
							return;
						} else {
							this.materialPorCirurgia.setQuantidade(this.quantidade);
						}
					}
					this.listaMbcMaterialPorCirurgia.add(this.materialPorCirurgia);
					this.setListaVazia(Boolean.FALSE);
				} else {
					this.materialPorCirurgia.setQuantidade(this.quantidade);
					this.listaMbcMaterialPorCirurgia.set(this.listaMbcMaterialPorCirurgia.indexOf(this.materialPorCirurgia), this.materialPorCirurgia);
				}
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_QUANTIDADE_OBRIGATORIO");
				return;
			}
			this.limpar();
		}
	}
	
	/**
	 * Método utilizado para comparar lista de itens já gravado em BD com itens a serem gravados,
	 * desconsiderando ID do mesmo. 
	 * */
	private boolean equalsSemId(MaterialPorCirurgiaVO obj1, MaterialPorCirurgiaVO obj2) {
		if(!obj1.getMatCodigo().equals(obj2.getMatCodigo())) {
			return false;
		}
		return true;
	}
	
	public void popularMaterialVO() {
		this.materialPorCirurgia.setMatCodigo(this.scoMaterial.getCodigo());
		this.materialPorCirurgia.setMatNome(this.scoMaterial.getNome());
		this.materialPorCirurgia.setUnidadeMedidaMat(this.scoMaterial.getUnidadeMedida().getDescricao());
	}
	
	public void popularUnidadeVO() {
		this.materialPorCirurgia.setUmdCodigo(this.scoUnidadeMedida.getCodigo());
		this.materialPorCirurgia.setUnidadeMedidaCons(this.scoUnidadeMedida.getDescricao());
	}
	
	public boolean validarCamposObrigatorios() {
		boolean resultado = true;
		if (materialPorCirurgia.getMatCodigo() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", this.getBundle().getString("LABEL_MATERIAIS_MATERIAL"));
			resultado = false;
		}		
		if (materialPorCirurgia.getUmdCodigo() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", this.getBundle().getString("LABEL_MATERIAIS_UNIDADE_CONS"));
			resultado = false;
		}
		if (this.getQuantidade() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", this.getBundle().getString("LABEL_MATERIAIS_QUANTIDADE"));
			resultado = false;
		}
		return resultado;
	}
	
	/**
	 * Remove uma instância de ScoMaterial da lista
	 */
	public void removerMaterial() {
		try {
			this.blocoCirurgicoFacade.excluirMaterialPorCirurgia(itemSelecionado);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_MATERIAL");
			this.listaMbcMaterialPorCirurgia.remove(itemSelecionado);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		this.itemSelecionado = null;
	}
	
	public void editarMat() {
		this.emEdicao = true;
		this.materialPorCirurgia = this.itemSelecionado;
		this.scoMaterial = this.comprasFacade.obterMaterialPorId(this.materialPorCirurgia.getMatCodigo());
		this.scoUnidadeMedida = this.comprasFacade.obterPorCodigo(this.materialPorCirurgia.getUmdCodigo());
		this.setQuantidade(this.itemSelecionado.getQuantidade());
	}
	
	public void cancelarMat() {
		this.limpar();
	}
	
	/**
	 * Grava lista de MbcMaterialPorCirurgia
	 */
	public void gravar() {
		try {
			this.blocoCirurgicoFacade.persistirMaterialPorCirurgia(listaMbcMaterialPorCirurgia, this.pDispensario);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_MATERIAIS");
			this.limpar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
		this.inicio();
	}
	
	public void limpar() {
		materialPorCirurgia = new MaterialPorCirurgiaVO();
		materialPorCirurgia.setCrgSeq(cirurgia.getSeq());
		this.scoMaterial = null;
		this.scoUnidadeMedida = null;
		this.emEdicao = false;
		this.quantidade = null;
	}
	
	public void novaLista(){
		for (MaterialPorCirurgiaVO mbcMaterialPorCirurgia : this.listaMbcMaterialPorCirurgia) {
			try {
				this.blocoCirurgicoFacade.excluirMaterialPorCirurgia(mbcMaterialPorCirurgia);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);				
			}
		}		
		this.limpar();
		this.inicio();
	}
	
	public void directPrint() throws BaseException, JRException, SystemException, IOException{
		relatorioRegistroDaNotaDeSalaController.setCrgSeq(mbcCirurgiaCodigo);
		relatorioRegistroDaNotaDeSalaController.directPrint();
	}
	
	//Métodos para suggestion box de Materiais
	public List<ScoMaterial> pesquisarMaterial(String objParam) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarMateriais(objParam),pesquisarMaterialCount(objParam));
	}
	
	/**
	 * Método para verificar se existe apenas 1 un medida, caso sim, seta no campo unidade consumo.
	 */
	public void verificaQuantUnidConsumo(){
		String objParam = "";
		
		this.popularMaterialVO();
		
		List<ScoUnidadeMedida> listSUM = this.pesquisarUnidadeMedida(objParam);
		if(!listSUM.isEmpty()){ //		#36960
			this.setScoUnidadeMedida(listSUM.get(0));
			this.popularUnidadeVO();
		}
	}
	
	public Long pesquisarMaterialCount(String objParam) {
		return this.comprasFacade.listarScoMateriaisCount(objParam);
	}
	
	//Métodos para suggestion box de Unidades de Medida
	
	public List<ScoUnidadeMedida> pesquisarUnidadeMedida(String objParam) {
		return this.blocoCirurgicoFacade.pesquisarUnidadeConsumoEConversaoMateriaisConsumidos(materialPorCirurgia.getMatCodigo(), objParam);
	
	}
	
	public Long pesquisarUnidadeMedidaCount(String objParam) {
		return this.blocoCirurgicoFacade.pesquisarUnidadeConsumoEConversaoMateriaisConsumidosCount(materialPorCirurgia.getMatCodigo(), objParam);
	}
	
	public void posDeleteMaterial() {
		this.scoUnidadeMedida = null;
		this.materialPorCirurgia.setMatCodigo(null);
		this.materialPorCirurgia.setMatNome(null);
		this.materialPorCirurgia.setUmdCodigo(null);
		this.materialPorCirurgia.setUnidadeMedidaMat(null);
		this.materialPorCirurgia.setUnidadeMedidaCons(null);
	}
	
	public void posDeleteUnidade() {
		this.materialPorCirurgia.setUmdCodigo(null);
		this.materialPorCirurgia.setUnidadeMedidaCons(null);
	}
	
	public String voltar() {
		procedimento = null;
		limpar();
		if (StringUtils.isNotEmpty(voltarPara)){
			return voltarPara;
		} else {
			return PAG_MATERIAL_CIRURGIA_LIST;
		}
	}
	
	public String getVisualizarRelatorio() {
		return PAG_RELATORIO_REGISTRO_NOTA_SALA;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public Integer getMbcCirurgiaCodigo() {
		return mbcCirurgiaCodigo;
	}

	public void setMbcCirurgiaCodigo(Integer mbcCirurgiaCodigo) {
		this.mbcCirurgiaCodigo = mbcCirurgiaCodigo;
	}

	public MaterialPorCirurgiaVO getMaterialPorCirurgia() {
		return materialPorCirurgia;
	}

	public void setMaterialPorCirurgia(MaterialPorCirurgiaVO materialPorCirurgia) {
		this.materialPorCirurgia = materialPorCirurgia;
	}

	public ScoMaterial getScoMaterial() {
		return scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	public ScoUnidadeMedida getScoUnidadeMedida() {
		return scoUnidadeMedida;
	}

	public void setScoUnidadeMedida(ScoUnidadeMedida scoUnidadeMedida) {
		this.scoUnidadeMedida = scoUnidadeMedida;
	}

	public List<MaterialPorCirurgiaVO> getListaMbcMaterialPorCirurgia() {
		return listaMbcMaterialPorCirurgia;
	}

	public void setListaMbcMaterialPorCirurgia(
			List<MaterialPorCirurgiaVO> listaMbcMaterialPorCirurgia) {
		this.listaMbcMaterialPorCirurgia = listaMbcMaterialPorCirurgia;
	}
	
	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Double getQuantidade() {
		return quantidade;
	}
	
	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public boolean isListaVazia() {
		return listaVazia;
	}

	public void setListaVazia(boolean listaVazia) {
		this.listaVazia = listaVazia;
	}

	public AghParametros getpDispensario() {
		return pDispensario;
	}

	public void setpDispensario(AghParametros pDispensario) {
		this.pDispensario = pDispensario;
	}

	public boolean isDispensario() {
		return dispensario;
	}


	public void setDispensario(boolean dispensario) {
		this.dispensario = dispensario;
	}


	public CirurgiaTelaProcedimentoVO getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(CirurgiaTelaProcedimentoVO procedimento) {
		this.procedimento = procedimento;
	}


	public MaterialPorCirurgiaVO getItemSelecionado() {
		return itemSelecionado;
	}


	public void setItemSelecionado(MaterialPorCirurgiaVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	
}
