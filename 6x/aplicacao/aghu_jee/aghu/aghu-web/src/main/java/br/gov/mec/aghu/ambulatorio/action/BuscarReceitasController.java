package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.TDataVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.view.VMamReceitas;

public class BuscarReceitasController extends ActionController {

	private static final long serialVersionUID = 5152041430440972649L;
	
	private TreeNode raiz;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	private AacConsultas consultaSelecionada;
	private String idadeFormatada;
	private String voltaPara;
	
	private TreeNode tDataVOGeralSelecionado;
	
	private List<VMamReceitas> listaVMamReceitasGeral;
	private List<VMamReceitas> listaVMamReceitasEspecialidades;
	
	private List<MamReceituarios> listaMamReceituarios;
	private boolean firsTime = true;
	
	public boolean isFirsTime() {
		return firsTime;
	}

	public void setFirsTime(boolean firsTime) {
		this.firsTime = firsTime;
	}

	TDataVO itemNode;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
		
		Integer pacCodigo = consultaSelecionada.getPaciente().getCodigo();
		
		List<TDataVO> listaCronologica = this.ambulatorioFacade.pGeraDadosData(pacCodigo);
		
		raiz = new DefaultTreeNode(raiz, null);
		
		TreeNode cronologica = null;
		TreeNode periodoCronologica = null;
		
		for(TDataVO item : listaCronologica){
			if(item.getHierarquia().equals(1)){
				cronologica  = new DefaultTreeNode(item, raiz);
			}else if(item.getHierarquia().equals(2)){
				periodoCronologica = new DefaultTreeNode(item, cronologica);
			}else if(item.getHierarquia().equals(3)){
				periodoCronologica.getChildren().add(new DefaultTreeNode(item, cronologica));
			}	
		}
		
		List<TDataVO> listaEspecialidade = this.ambulatorioFacade.pGerarDadosEspecialidade(pacCodigo);

		TreeNode especialidade = null;
		TreeNode nomeEspecialidade = null;
		TreeNode periodoEspecialidade = null;
		
		for(TDataVO item : listaEspecialidade){
			if(item.getHierarquia().equals(1)){
				especialidade  = new DefaultTreeNode(item, raiz);
			}else if(item.getHierarquia().equals(2)){
				nomeEspecialidade = new DefaultTreeNode(item, especialidade);
			}else if(item.getHierarquia().equals(3)){
				periodoEspecialidade = new DefaultTreeNode(item, nomeEspecialidade);	
			}else if(item.getHierarquia().equals(4)){
				periodoEspecialidade.getChildren().add(new DefaultTreeNode(item, nomeEspecialidade));
			}	
		}
		
		if(firsTime){
//			try {
//				AacConsultas ultimaConsulta = this.ambulatorioFacade.buscaConsultaAnterior(consultaSelecionada);
//				this.listaVMamReceitasGeral = this.ambulatorioFacade.obterListaVMamReceitas(ultimaConsulta.getPaciente().getCodigo(), 
//						ultimaConsulta.getNumero(), null,	null, DominioTipoReceituario.G);
//				this.listaVMamReceitasEspecialidades = this.ambulatorioFacade.obterListaVMamReceitas(ultimaConsulta.getPaciente().getCodigo(), 
//						ultimaConsulta.getNumero(), null,	null, DominioTipoReceituario.E);
//			} catch (ApplicationBusinessException e) {
//				apresentarExcecaoNegocio(e);
//			}
			
			firsTime = false;
			this.listaVMamReceitasEspecialidades = new ArrayList<VMamReceitas>();
			this.listaVMamReceitasGeral = new ArrayList<VMamReceitas>();
		}
	}
	
	public void nodeGeralSelecionado(){
		itemNode = (TDataVO) tDataVOGeralSelecionado.getData();
		
		Boolean pesquisaCronologia = (itemNode.getHierarquia() == 3 && itemNode.getConNumero() != null);
		
		Boolean pesquisaEspecialidade = (itemNode.getHierarquia() == 4 && itemNode.getConNumero() != null);
		
		if(pesquisaCronologia || pesquisaEspecialidade){
			this.listaVMamReceitasGeral = this.ambulatorioFacade.obterListaVMamReceitas(
					this.consultaSelecionada.getPaciente().getCodigo(),	itemNode.getConNumero(), null, null, DominioTipoReceituario.G);
			this.listaVMamReceitasEspecialidades = this.ambulatorioFacade.obterListaVMamReceitas(
					this.consultaSelecionada.getPaciente().getCodigo(),	itemNode.getConNumero(), null, null, DominioTipoReceituario.E);
			RequestContext.getCurrentInstance().reset(":formBuscarReceitas:dt_geral");
			RequestContext.getCurrentInstance().reset(":formBuscarReceitas:dt_especialidade");
		}else if(itemNode.getHierarquia() == 3 || itemNode.getHierarquia() == 4){
			listaVMamReceitasGeral = new ArrayList<VMamReceitas>();
			listaVMamReceitasEspecialidades = new ArrayList<VMamReceitas>();
			apresentarMsgNegocio(Severity.WARN, "MSG_CON_NUMERO");
		}else{
			itemNode = null;
			listaVMamReceitasGeral = null;
			listaVMamReceitasEspecialidades = null;
		}
	}
	
	public String simNao(boolean value){
		if(value){
			return "Sim";
		}else{
			return "Não";
		}
	}
	
	public void gravar(int numero) throws ApplicationBusinessException{
		Boolean registroSelecionado = Boolean.FALSE;
		
		if(itemNode != null){
				
			Integer numeroConsulta = consultaSelecionada.getNumero();
			DominioTipoReceituario tipoReceituario = (numero == 1) ? DominioTipoReceituario.G : DominioTipoReceituario.E;
		
			if(numero == 1){
				for (VMamReceitas registro : listaVMamReceitasGeral) {
					if (registro.isSelecionado()) {
						this.ambulatorioFacade.gravarReceitas(consultaSelecionada.getNumero(), registro, obterMamReceituario(numeroConsulta, tipoReceituario), tipoReceituario);
						registroSelecionado = true;
					}
				}
			}else if(numero == 2){
				for (VMamReceitas registro : listaVMamReceitasEspecialidades) {
					if (registro.isSelecionado()) {
						this.ambulatorioFacade.gravarReceitas(numeroConsulta, registro, obterMamReceituario(numeroConsulta, tipoReceituario), tipoReceituario);
						registroSelecionado = true;
					}
				}
			}
			
			if(registroSelecionado){
				apresentarMsgNegocio("MAM_06051");
			}else{
				apresentarMsgNegocio(Severity.WARN, "MAM_06045");
			}
			
		}else{
			apresentarMsgNegocio(Severity.WARN, "MAM_06045");
		}
	}
	
	private MamReceituarios obterMamReceituario(Integer numeroConsulta, DominioTipoReceituario tipoReceituario){
		MamReceituarios mamReceituario = null;
		
		this.listaMamReceituarios = this.ambulatorioFacade.obterListaSeqTipoMamReceituarios(null, null, null, null, null, null, numeroConsulta, tipoReceituario);
		
		if(this.listaMamReceituarios != null){
			if(!this.listaMamReceituarios.isEmpty()){
				mamReceituario = listaMamReceituarios.get(0);
			}
		}
		
		return mamReceituario;
	}
	
	public String truncar(String str, Integer tamanho){
		if(str.length() > tamanho){
			return StringUtils.abbreviate(str, tamanho);
		}
		return str;
	}
	
	public void itemSelecionado(VMamReceitas item){
		if(item.isSelecionado()){
			item.setSelecionado(false);
		}else{
			item.setSelecionado(true);
		}
	}
	
	public String voltar(){
		this.listaMamReceituarios = null;
		this.listaVMamReceitasEspecialidades = null;
		this.listaVMamReceitasGeral = null;
		this.tDataVOGeralSelecionado = null;
		this.itemNode = null;
		this.consultaSelecionada = null;
		this.raiz = null;
		this.idadeFormatada = null;
		this.firsTime = true;
		return voltaPara;
	}
	
	public TreeNode getRaiz() {
		return raiz;
	}

	public void setRaiz(TreeNode raiz) {
		this.raiz = raiz;
	}

	public String getIdadeFormatada() {
		return idadeFormatada;
	}

	public void setIdadeFormatada(String idadeFormatada) {
		this.idadeFormatada = idadeFormatada;
	}
	
	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public String getVoltaPara() {
		return voltaPara;
	}

	public void setVoltaPara(String voltaPara) {
		this.voltaPara = voltaPara;
	}

	public List<MamReceituarios> getListaMamReceituarios() {
		return listaMamReceituarios;
	}

	public void setListaMamReceituarios(List<MamReceituarios> listaMamReceituarios) {
		this.listaMamReceituarios = listaMamReceituarios;
	}

	public TreeNode gettDataVOGeralSelecionado() {
		return tDataVOGeralSelecionado;
	}

	public void settDataVOGeralSelecionado(TreeNode tDataVOGeralSelecionado) {
		this.tDataVOGeralSelecionado = tDataVOGeralSelecionado;
	}

	public List<VMamReceitas> getListaVMamReceitasGeral() {
		return listaVMamReceitasGeral;
	}

	public void setListaVMamReceitasGeral(List<VMamReceitas> listaVMamReceitasGeral) {
		this.listaVMamReceitasGeral = listaVMamReceitasGeral;
	}

	public List<VMamReceitas> getListaVMamReceitasEspecialidades() {
		return listaVMamReceitasEspecialidades;
	}

	public void setListaVMamReceitasEspecialidades(
			List<VMamReceitas> listaVMamReceitasEspecialidades) {
		this.listaVMamReceitasEspecialidades = listaVMamReceitasEspecialidades;
	}

	public TDataVO getItemNode() {
		return itemNode;
	}

	public void setItemNode(TDataVO itemNode) {
		this.itemNode = itemNode;
	}
}