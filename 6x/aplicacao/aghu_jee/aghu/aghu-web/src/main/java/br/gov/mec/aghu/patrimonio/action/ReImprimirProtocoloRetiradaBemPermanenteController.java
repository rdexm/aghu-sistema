package br.gov.mec.aghu.patrimonio.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.DetalhamentoRetiradaBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.QuantidadeDevolucaoBemPermanenteVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

import com.itextpdf.text.DocumentException;

public class ReImprimirProtocoloRetiradaBemPermanenteController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2201548468522624816L;
	
	private static final String PAGINA_LISTAR_ACEITES_TECNICOS = "patrimonio-listarAceitesTecnicos";

	private static final String PATRIMONIO_ITEM_RECEBIMENTO = "patrimonio-itemRecebimento";

	
	
	private static final String PAGINA_REIMPRIMIR_PROTOCOLO_RETIRADA_BEM_PERMANENTE = "patrimonio-reImprimirProtocoloRetiradaBemPermanente";
		
	@Inject
	private ListaAceiteTecnicoPaginatorController listaAceiteTecnicoController;
	
	@Inject
	private RelatorioReimprimirRetiradaBemPermanenteController relatorioReimprimirRetiradaBemPermanenteController;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnico = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
	
	private DevolucaoBemPermanenteVO[] itensDevolucaoSelecionados;
	
	private List<DevolucaoBemPermanenteVO> listaDevolucaoBemPermanente;
	
	private List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhamentoListCompleta = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();
	
	private List<DetalhamentoRetiradaBemPermanenteVO> itensSelecionadosDetalhamentoListCompleta = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();
	
	private AceiteTecnicoParaSerRealizadoVO mouseOver1;
	
	private DevolucaoBemPermanenteVO mouseOver2;
	
	private Long quantTotalDisponivel;
	
	private boolean allChecked;
	
	private boolean permitirDevolucao;
	
	private boolean textoExcedeValorMax;
	
	private String cameFrom;
	
	@Inject
	private RegistrarDevolucaoBemPermanenteDataModel devBemPermanDataModel;

	@PostConstruct
	protected void inicializar() {
		begin(conversation);
	}
	
	public String iniciar() throws ApplicationBusinessException, BaseListException, CloneNotSupportedException{
		this.allChecked = false;
		itensDevolucaoSelecionados = new DevolucaoBemPermanenteVO[0];
		
		populaListaAceiteTecnico();
		
		try {
			patrimonioFacade.validarInicializacaoReImpressaoBemPermanente(listaAceiteTecnico);
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		devBemPermanDataModel = new RegistrarDevolucaoBemPermanenteDataModel();
		listaDevolucaoBemPermanente = new ArrayList<DevolucaoBemPermanenteVO>();
		AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CENTRO_CUSTO_PATRIMONIO);
		for(AceiteTecnicoParaSerRealizadoVO itemBemPermanente : listaAceiteTecnico){
			List<DevolucaoBemPermanenteVO> listaDevolucoes = this.patrimonioFacade.carregarListaBemPermanente(itemBemPermanente.getSeqItemPatrimonio(), parametro.getVlrNumerico().intValue());
			if(listaDevolucoes != null && !listaDevolucoes.isEmpty()){
				for(DevolucaoBemPermanenteVO devolucao : listaDevolucoes){
					listaDevolucaoBemPermanente.add(devolucao);
				}
				devBemPermanDataModel = new RegistrarDevolucaoBemPermanenteDataModel(listaDevolucaoBemPermanente);
			}
		}
		
		for (AceiteTecnicoParaSerRealizadoVO item : listaAceiteTecnico) {
				DetalhamentoRetiradaBemPermanenteVO detalhe = new DetalhamentoRetiradaBemPermanenteVO();

				detalhe.setAceiteVO(item);
				detalhe.setCodigo(item.getCodigo());
				detalhe.setEsl(item.getEsl());
				detalhe.setItemRecebimento(item.getItemRecebimento());
				detalhe.setMaterial(item.getMaterial());
				detalhe.setRecebimento(item.getRecebimento());
				
				itensDetalhamentoListCompleta.add(detalhe);
		}
		
		return PAGINA_REIMPRIMIR_PROTOCOLO_RETIRADA_BEM_PERMANENTE;
	}
	
	public Long obterNotaPorNumeroRecebimento(Integer numRecebimento){
		return this.patrimonioFacade.obterNotaPorNumeroRecebimento(numRecebimento);
	}
	
	public Integer obterQuantidadeItem(Integer numRecebimento, Integer itemRecebimento){
		QuantidadeDevolucaoBemPermanenteVO qdbp = obterQuantidadeItensEntrega(numRecebimento, itemRecebimento);
		if(qdbp != null){
			return qdbp.getQuantidade();
		}
		return null;
	}
	
	public Long obterQuantidadeItemDisponiveis(Integer numRecebimento, Integer itemRecebimento ){
		QuantidadeDevolucaoBemPermanenteVO qdbpDisp = obterQuantidadeItensEntrega(numRecebimento, itemRecebimento);
		if(qdbpDisp != null){
			setQuantTotalDisponivel(qdbpDisp.getQuantidadeDisponivel());
			return qdbpDisp.getQuantidadeDisponivel();
		}
		return null;
	}
	
	public QuantidadeDevolucaoBemPermanenteVO obterQuantidadeItensEntrega(Integer numRecebimento, Integer itemRecebimento){
		return this.patrimonioFacade.obterQuantidadeItem(numRecebimento, itemRecebimento);
	}
	
	public String obterDescricaoDominioStatus(Integer value) {
		return DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(value).getDescricao();
	}
	
	public String visualizar() throws ApplicationBusinessException, JRException, SistemaImpressaoException, IOException, DocumentException, CloneNotSupportedException {
		itensSelecionadosDetalhamentoListCompleta = new  ArrayList<DetalhamentoRetiradaBemPermanenteVO>();
		if(itensDevolucaoSelecionados != null && itensDevolucaoSelecionados.length != 0){
				for(DevolucaoBemPermanenteVO itemDevolucaoSelecionado : itensDevolucaoSelecionados){
					DetalhamentoRetiradaBemPermanenteVO detalhamentoRetiradaBemPermanenteVOTemp = new DetalhamentoRetiradaBemPermanenteVO();
					AceiteTecnicoParaSerRealizadoVO aceiteVOTemp = new AceiteTecnicoParaSerRealizadoVO();
					
					for (AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVOTemp : listaAceiteTecnico) {
						if(itemDevolucaoSelecionado.getSirpNrpSeq() != null && aceiteTecnicoParaSerRealizadoVOTemp.getRecebimento() != null && itemDevolucaoSelecionado.getSirpNrpSeq().equals(aceiteTecnicoParaSerRealizadoVOTemp.getRecebimento()) &&
								itemDevolucaoSelecionado.getSirpNroItem() != null && aceiteTecnicoParaSerRealizadoVOTemp.getItemRecebimento() != null &&  itemDevolucaoSelecionado.getSirpNroItem().equals(aceiteTecnicoParaSerRealizadoVOTemp.getItemRecebimento())){
							aceiteVOTemp = aceiteTecnicoParaSerRealizadoVOTemp;
						}
					}
					
					aceiteVOTemp.setRecebimento(itemDevolucaoSelecionado.getSirpNrpSeq());
					
					detalhamentoRetiradaBemPermanenteVOTemp.setAceiteVO(aceiteVOTemp);	
					detalhamentoRetiradaBemPermanenteVOTemp.setRecebimento(itemDevolucaoSelecionado.getSirpNrpSeq());
					detalhamentoRetiradaBemPermanenteVOTemp.setItemRecebimento(itemDevolucaoSelecionado.getSirpNroItem());
					if(itemDevolucaoSelecionado.getSirpEslSeq() != null){
						detalhamentoRetiradaBemPermanenteVOTemp.setEsl(itemDevolucaoSelecionado.getSirpEslSeq());
					}
					
					detalhamentoRetiradaBemPermanenteVOTemp.setMaterial(itemDevolucaoSelecionado.getMatNome());
					detalhamentoRetiradaBemPermanenteVOTemp.setCodigo(itemDevolucaoSelecionado.getMatCodigo());
					
					if(itemDevolucaoSelecionado.getPbpNrBem() != null){
						detalhamentoRetiradaBemPermanenteVOTemp.setNumeroBem(itemDevolucaoSelecionado.getPbpNrBem());
					}
					if(itemDevolucaoSelecionado.getPbpNrSerie() != null){
						detalhamentoRetiradaBemPermanenteVOTemp.setNumeroSerie(itemDevolucaoSelecionado.getPbpNrSerie());
					}
					
					itensSelecionadosDetalhamentoListCompleta.add(detalhamentoRetiradaBemPermanenteVOTemp);
				}
			
				
				relatorioReimprimirRetiradaBemPermanenteController.setItensDetalhamentoListCompleta(itensSelecionadosDetalhamentoListCompleta);
			
			populaListaAceiteTecnico(itensSelecionadosDetalhamentoListCompleta);
			
			
			relatorioReimprimirRetiradaBemPermanenteController.setItemRetiradaList(listaAceiteTecnico);
			relatorioReimprimirRetiradaBemPermanenteController.setCameFrom("REIMPRIMIR_PROTOCOLO");
			
			return relatorioReimprimirRetiradaBemPermanenteController.print();
		}else{
			apresentarMsgNegocio(Severity.WARN, "ITEM_NAO_SELECIONA_REIMPRESSAO");
			return null;
		}
	}

	private void populaListaAceiteTecnico(List<DetalhamentoRetiradaBemPermanenteVO> itensSelecionadosDetalhamentoListCompleta) throws CloneNotSupportedException {
		listaAceiteTecnico = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		if (listaAceiteTecnicoController != null && listaAceiteTecnicoController.getListaAceiteTecnicoSelecionadosVO() != null) {
			for (AceiteTecnicoParaSerRealizadoVO item : listaAceiteTecnicoController.getListaAceiteTecnicoSelecionadosVO()) {
				for (DetalhamentoRetiradaBemPermanenteVO detalhamentoRetiradaBemPermanenteVOTemp : itensSelecionadosDetalhamentoListCompleta) {
					if(item.getEsl() != null && detalhamentoRetiradaBemPermanenteVOTemp.getEsl() != null && item.getEsl().equals(detalhamentoRetiradaBemPermanenteVOTemp.getEsl()) ||
							item.getItemRecebimento() != null && detalhamentoRetiradaBemPermanenteVOTemp.getItemRecebimento() != null && item.getItemRecebimento().equals(detalhamentoRetiradaBemPermanenteVOTemp.getItemRecebimento()) ||
									item.getRecebimento() != null && detalhamentoRetiradaBemPermanenteVOTemp.getRecebimento() != null &&  item.getRecebimento().equals(detalhamentoRetiradaBemPermanenteVOTemp.getRecebimento())){
				
					AceiteTecnicoParaSerRealizadoVO vo = new AceiteTecnicoParaSerRealizadoVO( item.getRecebimento(), item.getEsl(), item.getItemRecebimento(), 
							item.getAf(), item.getComplemento(), item.getNroSolicCompras(), item.getCodigo(), item.getMaterial(), item.getAreaTecAvaliacao(), 
							item.getTecnicoResponsavel(), item.getCodigoTecnicoResponsavel(), item.getNotaFiscal(), item.getQuantidade(), 
							item.getSeqItemPatrimonio(), item.getCentroCusto(), item.getFornecedor() );
				
					vo.setAceiteVO(item.getAceiteVO());
					listaAceiteTecnico.add(vo);
					break;
					}
				}
			}
		}
	}
	
	private void populaListaAceiteTecnico() throws CloneNotSupportedException {
		listaAceiteTecnico = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		if (listaAceiteTecnicoController != null && listaAceiteTecnicoController.getListaAceiteTecnicoSelecionadosVO() != null) {
			for (AceiteTecnicoParaSerRealizadoVO item : listaAceiteTecnicoController.getListaAceiteTecnicoSelecionadosVO()) {
					AceiteTecnicoParaSerRealizadoVO vo = new AceiteTecnicoParaSerRealizadoVO( item.getRecebimento(), item.getEsl(), item.getItemRecebimento(), 
							item.getAf(), item.getComplemento(), item.getNroSolicCompras(), item.getCodigo(), item.getMaterial(), item.getAreaTecAvaliacao(), 
							item.getTecnicoResponsavel(), item.getCodigoTecnicoResponsavel(), item.getNotaFiscal(), item.getQuantidade(), 
							item.getSeqItemPatrimonio(), item.getCentroCusto(), item.getFornecedor() );
				
					vo.setAceiteVO(item.getAceiteVO());
					listaAceiteTecnico.add(vo);
			}
		}
	}
	
	public String abrirModal(){
		if(itensDevolucaoSelecionados.length > 0){
			openDialog("modalCancelarWG");		
			return null;
		}else{
			return PAGINA_LISTAR_ACEITES_TECNICOS;
		}
	}

	public String cancelar(){
		if(cameFrom != null && cameFrom.equals(PATRIMONIO_ITEM_RECEBIMENTO)){
			cameFrom = "";
			return PATRIMONIO_ITEM_RECEBIMENTO;
		}else{
			return PAGINA_LISTAR_ACEITES_TECNICOS;
		}
	}
	
	public String truncarTexto(String texto, Integer tamanhoMaximo) {
		if(texto.length() > tamanhoMaximo){
			this.textoExcedeValorMax = Boolean.TRUE;
			return StringUtils.abbreviate(texto, tamanhoMaximo);
		}
		this.textoExcedeValorMax = Boolean.FALSE;
		return texto;
	}
	
	//Getters and Setters
	public List<AceiteTecnicoParaSerRealizadoVO> getListaAceiteTecnico() {
		return listaAceiteTecnico;
	}

	public void setListaAceiteTecnico(List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnico) {
		this.listaAceiteTecnico = listaAceiteTecnico;
	}

	public DevolucaoBemPermanenteVO[] getItensDevolucaoSelecionados() {
		return itensDevolucaoSelecionados;
	}

	public void setItensDevolucaoSelecionados(DevolucaoBemPermanenteVO[] itensDevolucaoSelecionados) {
		this.itensDevolucaoSelecionados = itensDevolucaoSelecionados;
	}

	public List<DevolucaoBemPermanenteVO> getListaDevolucaoBemPermanente() {
		return listaDevolucaoBemPermanente;
	}

	public void setListaDevolucaoBemPermanente(
			List<DevolucaoBemPermanenteVO> listaDevolucaoBemPermanente) {
		this.listaDevolucaoBemPermanente = listaDevolucaoBemPermanente;
	}

	public AceiteTecnicoParaSerRealizadoVO getMouseOver1() {
		return mouseOver1;
	}

	public void setMouseOver1(AceiteTecnicoParaSerRealizadoVO mouseOver1) {
		this.mouseOver1 = mouseOver1;
	}
	
	public DevolucaoBemPermanenteVO getMouseOver2() {
		return mouseOver2;
	}

	public void setMouseOver2(DevolucaoBemPermanenteVO mouseOver2) {
		this.mouseOver2 = mouseOver2;
	}

	public boolean isAllChecked() {
		return allChecked;
	}

	public void setAllChecked(boolean allChecked) {
		this.allChecked = allChecked;
	}

	public boolean isPermitirDevolucao() {
		return permitirDevolucao;
	}

	public void setPermitirDevolucao(boolean permitirDevolucao) {
		this.permitirDevolucao = permitirDevolucao;
	}
	
	public boolean isTextoExcedeValorMax() {
		return textoExcedeValorMax;
	}

	public void setTextoExcedeValorMax(boolean textoExcedeValorMax) {
		this.textoExcedeValorMax = textoExcedeValorMax;
	}

	public RegistrarDevolucaoBemPermanenteDataModel getDevBemPermanDataModel() {
		return devBemPermanDataModel;
	}

	public void setDevBemPermanDataModel(RegistrarDevolucaoBemPermanenteDataModel devBemPermanDataModel) {
		this.devBemPermanDataModel = devBemPermanDataModel;
	}

	public Long getQuantTotalDisponivel() {
		return quantTotalDisponivel;
	}

	public void setQuantTotalDisponivel(Long quantTotalDisponivel) {
		this.quantTotalDisponivel = quantTotalDisponivel;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
}
