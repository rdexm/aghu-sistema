package br.gov.mec.aghu.patrimonio.action;


import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AvaliacaoTecnicaVO;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AnaliseTecnicoController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4317044114744962555L;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	private ItemRecebimentoVO itemRecebimento;
	
	private AvaliacaoTecnicaVO itemSelecionado;
	
	private Integer indice;
	
	@Inject	@Paginator
	private DynamicDataModel<AvaliacaoTecnicaVO> dataModel;

	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		
		this.dataModel.reiniciarPaginator();		
	}
	
	
	public void carregarAnaliseTecnicos() {
		if (indice.equals(0)){
			iniciar();
		}else{			
			this.dataModel.limparPesquisa();
		}
	}
	
	
	@Override
	public List<AvaliacaoTecnicaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if (itemRecebimento != null){
			return this.patrimonioFacade.carregarAnaliseTecnico(itemRecebimento, firstResult, maxResult, orderProperty, asc);			
		}
		return null;
	}

	@Override
	public Long recuperarCount() {
		if (itemRecebimento != null){
			return this.patrimonioFacade.carregarAnaliseTecnicoCount(itemRecebimento);			
		}
		return null;
	}

	/**
	 * Obtém Marca truncada.
	 * @param descricao
	 * @param tamanho
	 * @return String
	 */
	public String truncarMarca(String descricao) {
		StringBuilder str = new StringBuilder(100);
		str.append("Marca: ").append(descricao);
		return str.toString();
	}

	/**
	 * Obtém Modelo truncada.
	 * @param descricao
	 * @param tamanho
	 * @return String
	 */
	public String truncarModelo(String descricao) {
		StringBuilder str = new StringBuilder(100);
		str.append("Modelo: ").append(descricao);
		return str.toString();
	}

	/**
	 * Obtém Justificativa truncada.
	 * @param descricao
	 * @param tamanho
	 * @return String
	 */
	public String truncarJustificativa(String descricao) {
		StringBuilder str = new StringBuilder(100);
		str.append("Justificativa: ").append(descricao);
		return str.toString();
	}

	/**
	 * Obtém Centro de Custo truncada.
	 * @param descricao
	 * @param tamanho
	 * @return String
	 */
	public String truncarCentroCusto(String descricao) {
		StringBuilder str = new StringBuilder(100);
		str.append("Centro de Custo: ").append(descricao);		
		return str.toString();
	}

	/**
	 *  Obtém Técnico Responsável truncada. 
	 * @param descricao
	 * @param tamanho
	 * @return String
	 */
	public String truncarTecnicoResponsavel(AvaliacaoTecnicaVO resp) {
		StringBuilder str = new StringBuilder(100);
		str.append("Técnico Responsável: ").append(resp.getMatricula()).append(" - ")
		.append(resp.getVinCodigo()).append(" - ").append(resp.getNomePessoaFisica()) ;
		return str.toString();
	}

	/**
	 * Obtém Data inclusão truncada.
	 * @param data
	 * @param tamanho
	 * @return String
	 */
	public String truncarDataInclusao(Date data) {
		String descricao = DateUtil.obterDataFormatada(data, "dd/MM/yyyy HH:mm:ss");		
		StringBuilder str = new StringBuilder(100);
			str.append("Data inclusão: ").append(descricao);		
		return str.toString();
	}
	
	/**
	 * Obtém Situação truncada.
	 * @param descricao
	 * @param tamanho
	 * @return String
	 */
	public String truncarSituacao(String descricao) {
		StringBuilder str = new StringBuilder(100);	
		str.append("Situação: ").append(descricao);
		
		return str.toString();
	}
	
	/**
	 * Hint dos números dos bens
	 * @param listaNumBens
	 * @return String
	 */
	public String hintNumBens(List<Long> listaNumBens) {
		 
		String resultado = "";
		StringBuilder numBensBuilder = new StringBuilder();
		 
		resultado = concatenarNumBens(listaNumBens, resultado,
				numBensBuilder);	
		return resultado;
	}
	
	/**
	 * Concatenar Números bens.
	 * @param itens
	 * @param tamanhoMaximo
	 * @return
	 */
	public String numBensTruncado(List<Long> listaNumBens, Integer tamanhoMaximo) {
		
		String resultado = "";
		StringBuilder numBensBuilder = new StringBuilder();
		
		resultado = concatenarNumBens(listaNumBens, resultado, numBensBuilder);
		
		if (resultado.length() > tamanhoMaximo) {
			resultado = StringUtils.abbreviate(resultado, tamanhoMaximo);
		}
		
		return resultado;
	}
	
	private String concatenarNumBens(List<Long> listaNumBens, String resultado, StringBuilder numBensBuilder) {
		if (listaNumBens == null || listaNumBens.isEmpty()){
			return resultado;
		}else {
			boolean primeira = true;
			for (Long item : listaNumBens) {
				if (item != null){					
					if(primeira){
						resultado = numBensBuilder.append(item).toString();
						primeira = false;
					}else{
						resultado = numBensBuilder.append(", " + item).toString();
					}
				}
			}	
		}
		return resultado;
	}
	
	
	public ItemRecebimentoVO getItemRecebimento() {
		return itemRecebimento;
	}

	public void setItemRecebimento(ItemRecebimentoVO itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}

	public AvaliacaoTecnicaVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(AvaliacaoTecnicaVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	
	public DynamicDataModel<AvaliacaoTecnicaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AvaliacaoTecnicaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Integer getIndice() {
		return indice;
	}

	public void setIndice(Integer indice) {
		this.indice = indice;
	}
}
