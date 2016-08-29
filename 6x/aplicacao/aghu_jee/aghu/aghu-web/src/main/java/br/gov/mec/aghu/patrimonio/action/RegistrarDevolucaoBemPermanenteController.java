package br.gov.mec.aghu.patrimonio.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.QuantidadeDevolucaoBemPermanenteVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RegistrarDevolucaoBemPermanenteController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2201548468522624816L;
	
	private static final String STATUS_NÃO_CONFORME = "Não Conforme";
	private static final String STATUS_CONCLUIDA = "Concluída";
	private static final String STATUS_PARCIALMENTE_ACEITO = "Parcialmente Aceito";
	private static final String STATUS_ACEITO = "Aceito";
	
	private static final String PAGINA_LISTAR_ACEITES_TECNICOS = "patrimonio-listarAceitesTecnicos";
	private static final String PAGINA_REGISTRAR_DEVOLUCAO_BEM_PERMANENTE= "patrimonio-registrarDevolucaoBemPermanente";
		
	@Inject
	private ListaAceiteTecnicoPaginatorController listaAceiteTecnicoController;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnico;
	
	private DevolucaoBemPermanenteVO[] itensDevolucaoSelecionados;
	
	private List<DevolucaoBemPermanenteVO> listaDevolucaoBemPermanente;
	
	private AceiteTecnicoParaSerRealizadoVO mouseOver1;
	
	private DevolucaoBemPermanenteVO mouseOver2;
	
	private boolean allChecked;
	
	private boolean permitirDevolucao;
	
	private boolean textoExcedeValorMax;
	
	private String paginaRetorno;
	
	@Inject
	private RegistrarDevolucaoBemPermanenteDataModel devBemPermanDataModel;

	@PostConstruct
	protected void inicializar() {
		begin(conversation);
	}
	
	public String iniciarDevolucao() throws ApplicationBusinessException {
		this.permitirDevolucao = true;
		this.allChecked = false;
		itensDevolucaoSelecionados = new DevolucaoBemPermanenteVO[0];
		listaAceiteTecnico = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		
		if (listaAceiteTecnicoController != null && listaAceiteTecnicoController.getListaAceiteTecnicoSelecionadosVO() != null) {
			for (AceiteTecnicoParaSerRealizadoVO item : listaAceiteTecnicoController.getListaAceiteTecnicoSelecionadosVO()) {
				if (obterDescricaoDominioStatus(item.getStatus()).equals(STATUS_CONCLUIDA)
						|| obterDescricaoDominioStatus(item.getStatus()).equals(STATUS_NÃO_CONFORME)
						|| obterDescricaoDominioStatus(item.getStatus()).equals(STATUS_PARCIALMENTE_ACEITO)
						|| obterDescricaoDominioStatus(item.getStatus()).equals(STATUS_ACEITO)) {
					listaAceiteTecnico.add(item);
				} else {
					this.permitirDevolucao = false;
					break;
				}
			}
		}
		if(permitirDevolucao){
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
			return PAGINA_REGISTRAR_DEVOLUCAO_BEM_PERMANENTE;
		}else{
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_DEVOLUCAO_NAO_PERMITIDA");
			return null;
		}
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
	
	public String gravar() throws ApplicationBusinessException {
		if(itensDevolucaoSelecionados != null && itensDevolucaoSelecionados.length > 0){
			this.patrimonioFacade.registrarDevolucaoBemPermanente(Arrays.asList(itensDevolucaoSelecionados));
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DEVOLUCAO_REGISTRADA_COM_SUCESSO");
			return PAGINA_LISTAR_ACEITES_TECNICOS;
		}else{
			apresentarMsgNegocio(Severity.WARN, "MENSAGEM_ITEM_DEVE_SER_SELECIONADO");
			return null;
		}
	}
	
	public String voltar() {
		if (paginaRetorno == null) {
			return PAGINA_LISTAR_ACEITES_TECNICOS;
		} else {
			String retorno = paginaRetorno;			
			paginaRetorno = null;
			return retorno;
		}
	}
	
	public String abrirModal(){
		if(itensDevolucaoSelecionados.length > 0){
			openDialog("modalCancelarWG");		
			return null;
		}else{
			if (paginaRetorno == null) {
				return PAGINA_LISTAR_ACEITES_TECNICOS;
			} else {
				String retorno = paginaRetorno;			
				paginaRetorno = null;
				return retorno;
			}
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

	public String getPaginaRetorno() {
		return paginaRetorno;
	}

	public void setPaginaRetorno(String paginaRetorno) {
		this.paginaRetorno = paginaRetorno;
	}

}
