package br.gov.mec.aghu.estoque.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ItemDevolucaoAlmoxarifadoVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceDocumentoValidade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class EstornoDevolucaoAlmoxarifadoController extends ActionController {
	
	private static final Log LOG = LogFactory.getLog(EstornoDevolucaoAlmoxarifadoController.class);
	private static final long serialVersionUID = -1989450676065108500L;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	private SceDevolucaoAlmoxarifado devolucaoAlmoxarifado = new SceDevolucaoAlmoxarifado();

	private List<SceDevolucaoAlmoxarifado> listaDevolucaoAlmoxarifado;
	private List<ItemDevolucaoAlmoxarifadoVO> listaItensDevolucaoAlmoxarifado;
	
	private Integer numeroDa;
	private SceAlmoxarifado almoxarifado;
	private Short almoxarifadoSeq;
	private FccCentroCustos centroCusto;
	private Integer cctCodigo;
	private DominioSimNao estornada;
	private Integer numeroDaEstorno;
	private Integer numeroDaSelecionado;
	private Boolean estorno;
	private Boolean pesquisaEstorno = false;
	private List<SceDocumentoValidade> listaValidades;
	private ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoSelecionado;
	private Date dtInicio = new Date();
	private Date dtFim;
	private Boolean operacaoEstorno = false;
	private Boolean primeiraPesquisa = false;
	private Boolean desabilitaCamposPesquisa = false;
		
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void pesquisar(){
		this.pesquisar(false);
	}
	
	public void pesquisar(Boolean isEstorno){
	
		this.listaDevolucaoAlmoxarifado = null;
		this.listaItensDevolucaoAlmoxarifado = null;
		devolucaoAlmoxarifado = new SceDevolucaoAlmoxarifado();
		if(almoxarifado!=null){
			almoxarifadoSeq = almoxarifado.getSeq();
		} else {
			almoxarifadoSeq = null;
		}
		if(centroCusto!=null){
			cctCodigo = centroCusto.getCodigo();
		} else {
			cctCodigo = null;
		}
		if(estornada!=null && estornada.equals(DominioSimNao.S)){
			this.estorno = true;
			this.pesquisaEstorno = true;
		} else if(estornada!=null && estornada.equals(DominioSimNao.N)){
			this.estorno = false;
			this.pesquisaEstorno = true;
		}
		this.pesquisarDevolucaoAlmoxarifado(numeroDa, almoxarifadoSeq, cctCodigo, estorno, pesquisaEstorno, dtInicio, dtFim);
		
		if(listaDevolucaoAlmoxarifado!=null && !listaDevolucaoAlmoxarifado.isEmpty()){
			if(!isEstorno){
				numeroDaSelecionado = listaDevolucaoAlmoxarifado.get(0).getSeq();	
			}
			this.pesquisarItensDevolucaoAlmoxarifado();
		} 
		this.primeiraPesquisa = true;
	}
	
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public void pesquisarDevolucaoAlmoxarifado(Integer numeroDa, Short almoxarifadoSeq, Integer cctCodigo, Boolean estorno, Boolean pesquisaEstorno, Date dtInicio, Date dtFim){
		this.listaDevolucaoAlmoxarifado = estoqueFacade.pesquisarDevolucaoAlmoxarifado(numeroDa, almoxarifadoSeq, cctCodigo, estorno, pesquisaEstorno, dtInicio, dtFim);
	}
	
	public void pesquisarItensDevolucaoAlmoxarifado(){
		this.listaItensDevolucaoAlmoxarifado = estoqueFacade.pesquisarItensComMaterialDevolucaoAlmoxarifado(numeroDaSelecionado);
	}
	
	
	
	public void limparPesquisa() {
		this.numeroDa = null;
		this.almoxarifado = null;
		this.almoxarifadoSeq = null;
		this.centroCusto = null;
		this.cctCodigo = null;
		this.estornada = null;
		this.pesquisaEstorno = false;
		this.listaValidades = null;
		this.listaDevolucaoAlmoxarifado = null;
		this.listaItensDevolucaoAlmoxarifado = null;
		this.dtInicio = null;
		this.dtFim = null;
		this.primeiraPesquisa = false;
		this.desabilitaCamposPesquisa = false;
	}
	
	public void carregar(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) {
		this.devolucaoAlmoxarifado = devolucaoAlmoxarifado;		
	}
	public void estornar() {
			SceDevolucaoAlmoxarifado devolucaoAlmoxarifadoAnterior = null;
			RapServidores servidorAnterior = null;
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			try {
				devolucaoAlmoxarifadoAnterior = this.estoqueFacade.obterDevolucaoAlmoxarifadoOriginal(devolucaoAlmoxarifado);
				servidorAnterior = devolucaoAlmoxarifado.getServidor();
				devolucaoAlmoxarifadoAnterior.setServidor(servidorAnterior);
				estoqueFacade.estornarDevolucaoAlmoxarifado(devolucaoAlmoxarifado, nomeMicrocomputador);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ESTORNO_DEVOLUCAO_ALMOXARIFADO");
				pesquisar(true);
			} catch (BaseException e) {
				//Neste trecho está sendo feito o refresh manual dos objetos alterados devido ao uso de flush na estoria #12310 da equipe Samurais
				for(int i=0; i<listaDevolucaoAlmoxarifado.size();i++){
					if(listaDevolucaoAlmoxarifado.get(i).equals(devolucaoAlmoxarifado)){
						listaDevolucaoAlmoxarifado.set(i, devolucaoAlmoxarifadoAnterior);
						break;
					}
				}
				this.apresentarExcecaoNegocio(e);
			}
	}
	
	public void desabilitarCampos(){
		if(numeroDa==null || numeroDa.equals(0)){
			this.desabilitaCamposPesquisa = false;
		} else {
			this.desabilitaCamposPesquisa = true;	
		}
	}

	public void selecionarItemDevolucao(ItemDevolucaoAlmoxarifadoVO item){
		this.listaValidades = item.getListaValidades();
	}
		
	public String cancelarPesquisa() {
		return null;
	}
	
	public List<SceAlmoxarifado> pesquisarAlmoxarifado(String objPesquisa) {
		return this.estoqueFacade.pesquisarAlmoxarifadosAtivosPorSeqOuDescricao(objPesquisa);
	}
	
	public List<FccCentroCustos> pesquisarCentroCusto(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustos(objPesquisa);
	}

	public SceDevolucaoAlmoxarifado getDevolucaoAlmoxarifado() {
		return devolucaoAlmoxarifado;
	}

	public void setDevolucaoAlmoxarifado(
			SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) {
		this.devolucaoAlmoxarifado = devolucaoAlmoxarifado;
	}

	public List<SceDevolucaoAlmoxarifado> getListaDevolucaoAlmoxarifado() {
		return listaDevolucaoAlmoxarifado;
	}

	public void setListaDevolucaoAlmoxarifado(
			List<SceDevolucaoAlmoxarifado> listaDevolucaoAlmoxarifado) {
		this.listaDevolucaoAlmoxarifado = listaDevolucaoAlmoxarifado;
	}

	public List<ItemDevolucaoAlmoxarifadoVO> getListaItensDevolucaoAlmoxarifado() {
		return listaItensDevolucaoAlmoxarifado;
	}

	public void setListaItensDevolucaoAlmoxarifado(
			List<ItemDevolucaoAlmoxarifadoVO> listaItensDevolucaoAlmoxarifado) {
		this.listaItensDevolucaoAlmoxarifado = listaItensDevolucaoAlmoxarifado;
	}

	public Integer getNumeroDa() {
		return numeroDa;
	}

	public void setNumeroDa(Integer numeroDa) {
		this.numeroDa = numeroDa;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public Short getAlmoxarifadoSeq() {
		return almoxarifadoSeq;
	}

	public void setAlmoxarifadoSeq(Short almoxarifadoSeq) {
		this.almoxarifadoSeq = almoxarifadoSeq;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public DominioSimNao getEstornada() {
		return estornada;
	}

	public void setEstornada(DominioSimNao estornada) {
		this.estornada = estornada;
	}

	public Integer getNumeroDaSelecionado() {
		return numeroDaSelecionado;
	}

	public void setNumeroDaSelecionado(Integer numeroDaSelecionado) {
		this.numeroDaSelecionado = numeroDaSelecionado;
	}

	public Integer getNumeroDaEstorno() {
		return numeroDaEstorno;
	}

	public void setNumeroDaEstorno(Integer numeroDaEstorno) {
		this.numeroDaEstorno = numeroDaEstorno;
	}

	public Boolean getEstorno() {
		return estorno;
	}

	public void setEstorno(Boolean estorno) {
		this.estorno = estorno;
	}

	public Boolean getPesquisaEstorno() {
		return pesquisaEstorno;
	}

	public void setPesquisaEstorno(Boolean pesquisaEstorno) {
		this.pesquisaEstorno = pesquisaEstorno;
	}

	public List<SceDocumentoValidade> getListaValidades() {
		return listaValidades;
	}

	public void setListaValidades(List<SceDocumentoValidade> listaValidades) {
		this.listaValidades = listaValidades;
	}

	public ItemDevolucaoAlmoxarifadoVO getItemDevolucaoAlmoxarifadoSelecionado() {
		return itemDevolucaoAlmoxarifadoSelecionado;
	}

	public void setItemDevolucaoDevolucaoAlmoxarifadoSelecionado(ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoSelecionado) {
		this.itemDevolucaoAlmoxarifadoSelecionado = itemDevolucaoAlmoxarifadoSelecionado;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Boolean getOperacaoEstorno() {
		return operacaoEstorno;
	}

	public void setOperacaoEstorno(Boolean operacaoEstorno) {
		this.operacaoEstorno = operacaoEstorno;
	}

	public Boolean getPrimeiraPesquisa() {
		return primeiraPesquisa;
	}

	public void setPrimeiraPesquisa(Boolean primeiraPesquisa) {
		this.primeiraPesquisa = primeiraPesquisa;
	}

	public void setItemDevolucaoAlmoxarifadoSelecionado(
			ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoSelecionado) {
		this.itemDevolucaoAlmoxarifadoSelecionado = itemDevolucaoAlmoxarifadoSelecionado;
	}

	public Boolean getDesabilitaCamposPesquisa() {
		return desabilitaCamposPesquisa;
	}

	public void setDesabilitaCamposPesquisa(Boolean desabilitaCamposPesquisa) {
		this.desabilitaCamposPesquisa = desabilitaCamposPesquisa;
	}
	
	
	
}
