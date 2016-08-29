package br.gov.mec.aghu.patrimonio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.patrimonio.vo.ResponsaveisStatusTicketsVO;
import br.gov.mec.aghu.patrimonio.vo.TicketsVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class HistoricoEncaminhamentoController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4317044114744962555L;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	private ItemRecebimentoVO itemRecebimento;
	
	private TicketsVO itemSelecionado;
	
	private Integer indice;
	
	@Inject	@Paginator
	private DynamicDataModel<TicketsVO> dataModel;

	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		
		this.dataModel.reiniciarPaginator();		
	}
	
	
	public void carregarHistoricoEncaminhamento() {
		if (indice.equals(0)){
			iniciar();
		}else{			
			this.dataModel.limparPesquisa();
		}
	}
	
	
	@Override
	public List<TicketsVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if (itemRecebimento != null){
			return this.patrimonioFacade.carregarTicketsItemRecebimentoProvisorio(itemRecebimento, firstResult, maxResult, orderProperty, asc);			
		}
		return null;
	}

	@Override
	public Long recuperarCount() {
		if (itemRecebimento != null){
			return this.patrimonioFacade.carregarTicketsItemRecebimentoProvisorioCount(itemRecebimento);			
		}
		return null;
	}
	
	
	public String responsavelTruncado(List<ResponsaveisStatusTicketsVO> itens, Integer tamanhoMaximo) {
		String resultado = "";
		StringBuilder responsavelBuilder = new StringBuilder();
		resultado = concatenarResponsavel(itens, resultado, responsavelBuilder);
		if (resultado.length() > tamanhoMaximo) {
			resultado = StringUtils.abbreviate(resultado, tamanhoMaximo);
		}
		return resultado;
	}
	
	
	/*Concatena os protocolos ciclo.*/
	public String hintResponsavel(List<ResponsaveisStatusTicketsVO> listaProtocolo) {
		 
		String resultado = "";
		StringBuilder protocolosBuilder = new StringBuilder();
		 
		resultado = concatenarResponsavelII(listaProtocolo, resultado,
				protocolosBuilder);	
		return resultado;
	}
	
	
	private String concatenarResponsavel(List<ResponsaveisStatusTicketsVO> listaResponsavel, String resultado, StringBuilder responsavelBuilder) {
		if (listaResponsavel == null || listaResponsavel.isEmpty()){
			return resultado;
		}else {
			boolean primeira = true;
			for (ResponsaveisStatusTicketsVO responsavel : listaResponsavel) {
				if (responsavel.getNome() != null){
					if (primeira){
						resultado = responsavelBuilder.append(responsavel.getNome()).toString();
						primeira = false;						
					}else{
						resultado = responsavelBuilder.append(" , " + responsavel.getNome()).toString();
					}
				}	
			}	
		}
		return resultado;
	}
	
	private String concatenarResponsavelII(List<ResponsaveisStatusTicketsVO> listaResponsavel, String resultado, StringBuilder responsavelBuilder) {
		if (listaResponsavel == null || listaResponsavel.isEmpty()){
			return resultado;
		}else {
			boolean primeira = true;
			for (ResponsaveisStatusTicketsVO responsavel : listaResponsavel) {
				if (responsavel.getNome() != null){
					if (primeira){
						resultado = responsavelBuilder.append("Responsável: " + responsavel.getNome()).toString();
						primeira = false;						
					}else{
						resultado = responsavelBuilder.append(" , " + responsavel.getNome()).toString();
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

	public TicketsVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(TicketsVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	
	public DynamicDataModel<TicketsVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<TicketsVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Integer getIndice() {
		return indice;
	}

	public void setIndice(Integer indice) {
		this.indice = indice;
	}
}
