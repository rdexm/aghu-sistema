package br.gov.mec.aghu.patrimonio.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioStatusNotificacaoTecnica;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.core.action.ActionController;


public class ListaNotificacaoTecnicaController extends ActionController{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7884721918575597912L;
		
	private static final String PAGINA_DETALHAR_NOTIFICACOES_TECNICAS = "patrimonio-detalharNotificacaoTecnica";
	private static final String PAGINA_LISTA_ACEITES_TECNICOS = "patrimonio-listarAceitesTecnicos";	
	private static final String PAGINA_IMPRESSAO_NOTIFICACAO_TECNICA = "patrimonio-imprimirNotificacaoTecnica";	
	
	private String recebItem;
	private Integer esl;
	private String notaFiscal;
	private String afComplemento;
	private Integer sc;
	private String codMaterial;
	private PtmItemRecebProvisorios irpSeq;
	private List<PtmNotificacaoTecnica> listaNotificacoesTecnicas;	
	private PtmNotificacaoTecnica itemDetalhado;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@Inject
	private DetalharNotificacaoTecnicaController detalharNotificacaoTecnicaController;

	@PostConstruct
	protected void inicializar() {
		begin(conversation);	
		itemDetalhado = new PtmNotificacaoTecnica();
	}
	
	public void montarDados(AceiteTecnicoParaSerRealizadoVO item){
		listaNotificacoesTecnicas = new ArrayList<PtmNotificacaoTecnica>();
		recebItem = item.obterRecebItem();
		esl = item.getEsl();
		notaFiscal = this.patrimonioFacade.pesquisarNumeroNotaFiscal(item.getRecebimento()).toString();
		afComplemento = item.obterAfComplemento();
		sc = item.getNroSolicCompras();
		codMaterial = item.getCodigo() + "/" +item.getMaterial();
		irpSeq = this.patrimonioFacade.pesquisarIrpSeq(item.getRecebimento(), item.getItemRecebimento(), null);
		listaNotificacoesTecnicas = this.patrimonioFacade.pesquisarNotificacoesTecnica(irpSeq.getSeq());
	}
	
	public String pesquisarDominio(Integer cod){
		return DominioStatusNotificacaoTecnica.obterDominioStatusNotificacaoTecnica(cod);
	}
	
	public String detalharNotificacaoTecnica(){		
		detalharNotificacaoTecnicaController.setTipo(this.pesquisarDominio(itemDetalhado.getStatus()));
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String novaData = format.format(itemDetalhado.getData());		
		detalharNotificacaoTecnicaController.setDataCriacao(novaData);		
		detalharNotificacaoTecnicaController.setDescricao(itemDetalhado.getDescricao());	
		detalharNotificacaoTecnicaController.setSeq(itemDetalhado.getSeq());
		return PAGINA_DETALHAR_NOTIFICACOES_TECNICAS;
	}
	
	/**
	 * Ação do link Visualizar Impressão.
	 */
	public String visualizarImpressao() {
		return PAGINA_IMPRESSAO_NOTIFICACAO_TECNICA;
	}
	
	public String voltar(){
		return PAGINA_LISTA_ACEITES_TECNICOS;
	}
	
	public String getRecebItem() {
		return recebItem;
	}

	public void setRecebItem(String recebItem) {
		this.recebItem = recebItem;
	}

	public Integer getEsl() {
		return esl;
	}

	public void setEsl(Integer esl) {
		this.esl = esl;
	}

	public String getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(String notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public String getAfComplemento() {
		return afComplemento;
	}

	public void setAfComplemento(String afComplemento) {
		this.afComplemento = afComplemento;
	}

	public Integer getSc() {
		return sc;
	}

	public void setSc(Integer sc) {
		this.sc = sc;
	}

	public String getCodMaterial() {
		return codMaterial;
	}

	public void setCodMaterial(String codMaterial) {
		this.codMaterial = codMaterial;
	}

	public PtmItemRecebProvisorios getIrpSeq() {
		return irpSeq;
	}

	public void setIrpSeq(PtmItemRecebProvisorios irpSeq) {
		this.irpSeq = irpSeq;
	}

	public List<PtmNotificacaoTecnica> getListaNotificacoesTecnicas() {
		return listaNotificacoesTecnicas;
	}

	public void setListaNotificacoesTecnicas(List<PtmNotificacaoTecnica> listaNotificacoesTecnicas) {
		this.listaNotificacoesTecnicas = listaNotificacoesTecnicas;
	}

	public PtmNotificacaoTecnica getItemDetalhado() {
		return itemDetalhado;
	}

	public void setItemDetalhado(PtmNotificacaoTecnica itemDetalhado) {
		this.itemDetalhado = itemDetalhado;
	}	
}
